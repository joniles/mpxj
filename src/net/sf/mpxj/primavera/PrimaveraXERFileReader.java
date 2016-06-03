/*
 * file:       PrimaveraXERFileReader.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2010
 * date:       25/03/2010
 */

/*
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or (at your
 * option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 */

package net.sf.mpxj.primavera;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.mpxj.FieldType;
import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.common.NumberHelper;
import net.sf.mpxj.common.ReaderTokenizer;
import net.sf.mpxj.common.Tokenizer;
import net.sf.mpxj.listener.ProjectListener;
import net.sf.mpxj.reader.AbstractProjectReader;

/**
 * This class creates a new ProjectFile instance by reading a Primavera XER file.
 */
public final class PrimaveraXERFileReader extends AbstractProjectReader
{
   /**
    * {@inheritDoc}
    */
   @Override public void addProjectListener(ProjectListener listener)
   {
      if (m_projectListeners == null)
      {
         m_projectListeners = new LinkedList<ProjectListener>();
      }
      m_projectListeners.add(listener);
   }

   /**
    * Set the ID of the project to be read.
    *
    * @param projectID project ID
    */
   public void setProjectID(int projectID)
   {
      m_projectID = Integer.valueOf(projectID);
   }

   /**
    * Sets the character encoding used when reading an XER file.
    *
    * @param encoding encoding name
    */
   public void setEncoding(String encoding)
   {
      m_encoding = encoding;
   }

   /**
    * {@inheritDoc}
    */
   @Override public ProjectFile read(InputStream is) throws MPXJException
   {
      try
      {
         m_tables = new HashMap<String, List<Row>>();
         m_numberFormat = new DecimalFormat();

         processFile(is);

         m_reader = new PrimaveraReader(m_udfCounters, m_resourceFields, m_wbsFields, m_taskFields, m_assignmentFields, m_aliases, m_matchPrimaveraWBS);
         ProjectFile project = m_reader.getProject();
         project.getEventManager().addProjectListeners(m_projectListeners);

         processProjectID();
         processProjectProperties();
         processUserDefinedFields();
         processCalendars();
         processResources();
         processTasks();
         processPredecessors();
         processAssignments();

         m_reader = null;
         project.updateStructure();

         return (project);
      }

      finally
      {
         m_reader = null;
         m_tables = null;
         m_currentTableName = null;
         m_currentTable = null;
         m_currentFieldNames = null;
         m_defaultCurrencyName = null;
         m_currencyMap.clear();
         m_numberFormat = null;
         m_defaultCurrencyData = null;
      }
   }

   /**
    * This is a convenience method which allows all projects in an
    * XER file to be read in a single pass.
    *
    * @param is input stream
    * @return list of ProjectFile instances
    * @throws MPXJException
    */
   public List<ProjectFile> readAll(InputStream is) throws MPXJException
   {
      try
      {
         m_tables = new HashMap<String, List<Row>>();
         m_numberFormat = new DecimalFormat();

         processFile(is);

         List<Row> rows = getRows("project", null, null);
         List<ProjectFile> result = new ArrayList<ProjectFile>(rows.size());
         for (Row row : rows)
         {
            setProjectID(row.getInt("proj_id"));

            m_reader = new PrimaveraReader(m_udfCounters, m_resourceFields, m_wbsFields, m_taskFields, m_assignmentFields, m_aliases, m_matchPrimaveraWBS);
            ProjectFile project = m_reader.getProject();
            project.getEventManager().addProjectListeners(m_projectListeners);

            processProjectProperties();
            processUserDefinedFields();
            processCalendars();
            processResources();
            processTasks();
            processPredecessors();
            processAssignments();

            m_reader = null;
            project.updateStructure();

            result.add(project);
         }

         return result;
      }

      finally
      {
         m_reader = null;
         m_tables = null;
         m_currentTableName = null;
         m_currentTable = null;
         m_currentFieldNames = null;
         m_defaultCurrencyName = null;
         m_currencyMap.clear();
         m_numberFormat = null;
         m_defaultCurrencyData = null;
      }
   }

   /**
    * Reads the XER file table and row structure ready for processing.
    *
    * @param is input stream
    * @throws MPXJException
    */
   private void processFile(InputStream is) throws MPXJException
   {
      int line = 1;

      try
      {
         //
         // Test the header and extract the separator. If this is successful,
         // we reset the stream back as far as we can. The design of the
         // BufferedInputStream class means that we can't get back to character
         // zero, so the first record we will read will get "RMHDR" rather than
         // "ERMHDR" in the first field position.
         //
         BufferedInputStream bis = new BufferedInputStream(is);
         byte[] data = new byte[6];
         data[0] = (byte) bis.read();
         bis.mark(1024);
         bis.read(data, 1, 5);

         if (!new String(data).equals("ERMHDR"))
         {
            throw new MPXJException(MPXJException.INVALID_FILE);
         }

         bis.reset();

         Charset charset = m_encoding == null ? Charset.defaultCharset() : Charset.forName(m_encoding);
         InputStreamReader reader = new InputStreamReader(bis, charset);
         Tokenizer tk = new ReaderTokenizer(reader);
         tk.setDelimiter('\t');
         List<String> record = new ArrayList<String>();

         while (tk.getType() != Tokenizer.TT_EOF)
         {
            readRecord(tk, record);
            if (processRecord(record))
            {
               break;
            }
            ++line;
         }
      }

      catch (Exception ex)
      {
         throw new MPXJException(MPXJException.READ_ERROR + " (failed at line " + line + ")", ex);
      }
   }

   /**
    * If the user has not specified a project ID, this method
    * retrieves the ID of the first project in the file.
    */
   private void processProjectID()
   {
      if (m_projectID == null)
      {
         List<Row> rows = getRows("project", null, null);
         if (!rows.isEmpty())
         {
            Row row = rows.get(0);
            m_projectID = row.getInteger("proj_id");
         }
      }
   }

   /**
    * Process a currency definition.
    *
    * @param row record from XER file
    */
   private void processCurrency(Row row)
   {
      String currencyName = row.getString("curr_short_name");
      DecimalFormatSymbols symbols = new DecimalFormatSymbols();
      symbols.setDecimalSeparator(row.getString("decimal_symbol").charAt(0));
      symbols.setGroupingSeparator(row.getString("digit_group_symbol").charAt(0));
      DecimalFormat nf = new DecimalFormat();
      nf.setDecimalFormatSymbols(symbols);
      nf.applyPattern("#.#");
      m_currencyMap.put(currencyName, nf);

      if (currencyName.equalsIgnoreCase(m_defaultCurrencyName))
      {
         m_numberFormat = nf;
         m_defaultCurrencyData = row;
      }
   }

   /**
    * Populates a Map instance representing the IDs and names of
    * projects available in the current file.
    *
    * @param is input stream used to read XER file
    * @return Map instance containing ID and name pairs
    * @throws MPXJException
    */
   public Map<Integer, String> listProjects(InputStream is) throws MPXJException
   {
      try
      {
         m_tables = new HashMap<String, List<Row>>();
         processFile(is);

         Map<Integer, String> result = new HashMap<Integer, String>();

         List<Row> rows = getRows("project", null, null);
         for (Row row : rows)
         {
            Integer id = row.getInteger("proj_id");
            String name = row.getString("proj_short_name");
            result.put(id, name);
         }

         return result;
      }

      finally
      {
         m_tables = null;
         m_currentTable = null;
         m_currentFieldNames = null;
      }
   }

   /**
    * Process project properties.
    */
   private void processProjectProperties()
   {
      //
      // Process common attributes
      //
      List<Row> rows = getRows("project", "proj_id", m_projectID);
      m_reader.processProjectProperties(rows);

      //
      // Process XER-specific attributes
      //
      if (m_defaultCurrencyData != null)
      {
         m_reader.processDefaultCurrency(m_defaultCurrencyData);
      }
   }

   /**
    * Process user defined fields.
    */
   private void processUserDefinedFields()
   {
      List<Row> udfs = getRows("udftype", null, null);
      m_reader.processUserDefinedFields(udfs);
   }

   /**
    * Process project calendars.
    */
   private void processCalendars()
   {
      List<Row> rows = getRows("calendar", null, null);
      m_reader.processCalendars(rows);
   }

   /**
    * Process resources.
    */
   private void processResources()
   {
      List<Row> rows = getRows("rsrc", null, null);
      m_reader.processResources(rows);
   }

   /**
    * Process tasks.
    */
   private void processTasks()
   {
      List<Row> wbs = getRows("projwbs", "proj_id", m_projectID);
      List<Row> tasks = getRows("task", "proj_id", m_projectID);
      List<Row> costs = getRows("projcost", "proj_id", m_projectID);
      //List<Row> wbsmemos = getRows("wbsmemo", "proj_id", m_projectID);
      //List<Row> taskmemos = getRows("taskmemo", "proj_id", m_projectID);
      List<Row> udfVals = getRows("udfvalue", "proj_id", m_projectID);
      Collections.sort(wbs, WBS_ROW_COMPARATOR);
      m_reader.processTasks(wbs, tasks, costs, udfVals/*, wbsmemos, taskmemos*/);
   }

   /**
    * Process predecessors.
    */
   private void processPredecessors()
   {
      List<Row> rows = getRows("taskpred", "proj_id", m_projectID);
      m_reader.processPredecessors(rows);
   }

   /**
    * Process resource assignments.
    */
   private void processAssignments()
   {
      List<Row> rows = getRows("taskrsrc", "proj_id", m_projectID);
      m_reader.processAssignments(rows);
   }

   /**
    * Reads each token from a single record and adds it to a list.
    *
    * @param tk tokenizer
    * @param record list of tokens
    * @throws IOException
    */
   private void readRecord(Tokenizer tk, List<String> record) throws IOException
   {
      record.clear();
      while (tk.nextToken() == Tokenizer.TT_WORD)
      {
         record.add(tk.getToken());
      }
   }

   /**
    * Handles a complete record at a time, stores it in a form ready for
    * further processing.
    *
    * @param record record to be processed
    * @return flag indicating if this is the last record in the file to be processed
    * @throws MPXJException
    */
   private boolean processRecord(List<String> record) throws MPXJException
   {
      boolean done = false;

      XerRecordType type = RECORD_TYPE_MAP.get(record.get(0));
      if (type == null)
      {
         throw new MPXJException(MPXJException.INVALID_FORMAT);
      }

      switch (type)
      {
         case HEADER:
         {
            processHeader(record);
            break;
         }

         case TABLE:
         {
            m_currentTableName = record.get(1).toLowerCase();
            m_skipTable = !REQUIRED_TABLES.contains(m_currentTableName);
            if (m_skipTable)
            {
               m_currentTable = null;
            }
            else
            {
               m_currentTable = new LinkedList<Row>();
               m_tables.put(m_currentTableName, m_currentTable);
            }
            break;
         }

         case FIELDS:
         {
            if (m_skipTable)
            {
               m_currentFieldNames = null;
            }
            else
            {
               m_currentFieldNames = record.toArray(new String[record.size()]);
               for (int loop = 0; loop < m_currentFieldNames.length; loop++)
               {
                  m_currentFieldNames[loop] = m_currentFieldNames[loop].toLowerCase();
               }
            }
            break;
         }

         case DATA:
         {
            if (!m_skipTable)
            {
               Map<String, Object> map = new HashMap<String, Object>();
               for (int loop = 1; loop < record.size(); loop++)
               {
                  String fieldName = m_currentFieldNames[loop];
                  String fieldValue = record.get(loop);
                  XerFieldType fieldType = FIELD_TYPE_MAP.get(fieldName);
                  if (fieldType == null)
                  {
                     fieldType = XerFieldType.STRING;
                  }

                  Object objectValue;
                  if (fieldValue.length() == 0)
                  {
                     objectValue = null;
                  }
                  else
                  {
                     switch (fieldType)
                     {
                        case DATE:
                        {
                           try
                           {
                              objectValue = m_df.parseObject(fieldValue);
                           }

                           catch (ParseException ex)
                           {
                              throw new MPXJException(MPXJException.INVALID_DATE, ex);
                           }

                           break;
                        }

                        case CURRENCY:
                        {
                           try
                           {
                              objectValue = Double.valueOf(m_numberFormat.parse(fieldValue).doubleValue());
                           }

                           catch (ParseException ex)
                           {
                              throw new MPXJException(MPXJException.INVALID_NUMBER, ex);
                           }
                           break;
                        }

                        case DOUBLE:
                        {
                           try
                           {
                              objectValue = Double.valueOf(m_numberFormat.parse(fieldValue).doubleValue());
                           }

                           catch (ParseException ex)
                           {
                              throw new MPXJException(MPXJException.INVALID_NUMBER, ex);
                           }
                           break;
                        }

                        case DURATION:
                        {
                           try
                           {
                              objectValue = Double.valueOf(m_numberFormat.parse(fieldValue).doubleValue());
                           }

                           catch (ParseException ex)
                           {
                              throw new MPXJException(MPXJException.INVALID_NUMBER, ex);
                           }
                           break;
                        }

                        case INTEGER:
                        {
                           objectValue = Integer.valueOf(fieldValue);
                           break;
                        }

                        default:
                        {
                           objectValue = fieldValue;
                           break;
                        }
                     }
                  }

                  map.put(fieldName, objectValue);
               }

               Row currentRow = new MapRow(map);
               m_currentTable.add(currentRow);

               //
               // Special case - we need to know the default currency format
               // ahead of time, so process each row as we get it so that
               // we can correctly parse currency values in later tables.
               //
               if (m_currentTableName.equals("currtype"))
               {
                  processCurrency(currentRow);
               }
            }
            break;
         }

         case END:
         {
            done = true;
            break;
         }

         default:
         {
            break;
         }
      }

      return done;
   }

   /**
    * Extract any useful attributes from the header record.
    *
    * @param record header record
    */
   private void processHeader(List<String> record)
   {
      m_defaultCurrencyName = record.size() > 8 ? record.get(8) : "USD";
   }

   /**
    * Override the default field name mapping for user defined types.
    *
    * @param type target user defined data type
    * @param fieldName field name
    */
   public void setFieldNameForUdfType(UserFieldDataType type, String fieldName)
   {
      m_udfCounters.setFieldNameForType(type, fieldName);
   }

   /**
    * Customise the data retrieved by this reader by modifying the contents of this map.
    *
    * @return Primavera field name to MPXJ field type map
    */
   public Map<FieldType, String> getResourceFieldMap()
   {
      return m_resourceFields;
   }

   /**
    * Customise the data retrieved by this reader by modifying the contents of this map.
    *
    * @return Primavera field name to MPXJ field type map
    */
   public Map<FieldType, String> getWbsFieldMap()
   {
      return m_wbsFields;
   }

   /**
    * Customise the data retrieved by this reader by modifying the contents of this map.
    *
    * @return Primavera field name to MPXJ field type map
    */
   public Map<FieldType, String> getTaskFieldMap()
   {
      return m_taskFields;
   }

   /**
    * Customise the data retrieved by this reader by modifying the contents of this map.
    *
    * @return Primavera field name to MPXJ field type map
    */
   public Map<FieldType, String> getAssignmentFields()
   {
      return m_assignmentFields;
   }

   /**
    * Customise the MPXJ field name aliases applied by this reader by modifying the contents of this map.
    *
    * @return Primavera field name to MPXJ field type map
    */
   public Map<FieldType, String> getAliases()
   {
      return m_aliases;
   }

   /**
    * Filters a list of rows from the named table. If a column name and a value
    * are supplied, then use this to filter the rows. If no column name is
    * supplied, then return all rows.
    *
    * @param tableName table name
    * @param columnName filter column name
    * @param id filter column value
    * @return filtered list of rows
    */
   private List<Row> getRows(String tableName, String columnName, Integer id)
   {
      List<Row> result;
      List<Row> table = m_tables.get(tableName);
      if (table == null)
      {
         result = Collections.<Row> emptyList();
      }
      else
      {
         if (columnName == null)
         {
            result = table;
         }
         else
         {
            result = new LinkedList<Row>();
            for (Row row : table)
            {
               if (NumberHelper.equals(id, row.getInteger(columnName)))
               {
                  result.add(row);
               }
            }
         }
      }
      return result;
   }

   /**
    * If set to true, the WBS for each task read from Primavera will exactly match the WBS value shown in Primavera.
    * If set to false, each task will be given a unique WBS based on the WBS present in Primavera.
    * Defaults to true.
    *
    * @return flag value
    */
   public boolean getMatchPrimaveraWBS()
   {
      return m_matchPrimaveraWBS;
   }

   /**
    * If set to true, the WBS for each task read from Primavera will exactly match the WBS value shown in Primavera.
    * If set to false, each task will be given a unique WBS based on the WBS present in Primavera.
    * Defaults to true.
    *
    * @param matchPrimaveraWBS flag value
    */
   public void setMatchPrimaveraWBS(boolean matchPrimaveraWBS)
   {
      m_matchPrimaveraWBS = matchPrimaveraWBS;
   }

   private String m_encoding;
   private PrimaveraReader m_reader;
   private Integer m_projectID;
   boolean m_skipTable;
   private Map<String, List<Row>> m_tables;
   private String m_currentTableName;
   private List<Row> m_currentTable;
   private String[] m_currentFieldNames;
   private String m_defaultCurrencyName;
   private Map<String, DecimalFormat> m_currencyMap = new HashMap<String, DecimalFormat>();
   private DecimalFormat m_numberFormat;
   private Row m_defaultCurrencyData;
   private DateFormat m_df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
   private List<ProjectListener> m_projectListeners;
   private UserFieldCounters m_udfCounters = new UserFieldCounters();
   private Map<FieldType, String> m_resourceFields = PrimaveraReader.getDefaultResourceFieldMap();
   private Map<FieldType, String> m_wbsFields = PrimaveraReader.getDefaultWbsFieldMap();
   private Map<FieldType, String> m_taskFields = PrimaveraReader.getDefaultTaskFieldMap();
   private Map<FieldType, String> m_assignmentFields = PrimaveraReader.getDefaultAssignmentFieldMap();
   private Map<FieldType, String> m_aliases = PrimaveraReader.getDefaultAliases();
   private boolean m_matchPrimaveraWBS = true;

   /**
    * Represents expected record types.
    */
   private enum XerRecordType
   {
      HEADER,
      TABLE,
      FIELDS,
      DATA,
      END
   }

   /**
    * Represents column data types.
    */
   private enum XerFieldType
   {
      STRING,
      INTEGER,
      DOUBLE,
      DATE,
      DURATION,
      CURRENCY
   }

   /**
    * Maps record type text to record types.
    */
   private static final Map<String, XerRecordType> RECORD_TYPE_MAP = new HashMap<String, XerRecordType>();
   static
   {
      RECORD_TYPE_MAP.put("RMHDR", XerRecordType.HEADER);
      RECORD_TYPE_MAP.put("%T", XerRecordType.TABLE);
      RECORD_TYPE_MAP.put("%F", XerRecordType.FIELDS);
      RECORD_TYPE_MAP.put("%R", XerRecordType.DATA);
      RECORD_TYPE_MAP.put("%E", XerRecordType.END);
   }

   /**
    * Maps field names to data types.
    */
   private static final Map<String, XerFieldType> FIELD_TYPE_MAP = new HashMap<String, XerFieldType>();
   static
   {
      FIELD_TYPE_MAP.put("proj_id", XerFieldType.INTEGER);
      FIELD_TYPE_MAP.put("create_date", XerFieldType.DATE);
      FIELD_TYPE_MAP.put("plan_end_date", XerFieldType.DATE);
      FIELD_TYPE_MAP.put("plan_start_date", XerFieldType.DATE);
      FIELD_TYPE_MAP.put("rsrc_id", XerFieldType.INTEGER);
      FIELD_TYPE_MAP.put("create_date", XerFieldType.DATE);
      FIELD_TYPE_MAP.put("wbs_id", XerFieldType.INTEGER);
      FIELD_TYPE_MAP.put("orig_cost", XerFieldType.CURRENCY);
      FIELD_TYPE_MAP.put("indep_remain_total_cost", XerFieldType.CURRENCY);
      FIELD_TYPE_MAP.put("indep_remain_work_qty", XerFieldType.DURATION);
      FIELD_TYPE_MAP.put("anticip_start_date", XerFieldType.DATE);
      FIELD_TYPE_MAP.put("anticip_end_date", XerFieldType.DATE);
      FIELD_TYPE_MAP.put("parent_wbs_id", XerFieldType.INTEGER);
      FIELD_TYPE_MAP.put("task_id", XerFieldType.INTEGER);
      FIELD_TYPE_MAP.put("phys_complete_pct", XerFieldType.DOUBLE);
      FIELD_TYPE_MAP.put("remain_drtn_hr_cnt", XerFieldType.DURATION);
      FIELD_TYPE_MAP.put("act_work_qty", XerFieldType.DURATION);
      FIELD_TYPE_MAP.put("remain_work_qty", XerFieldType.DURATION);
      FIELD_TYPE_MAP.put("target_work_qty", XerFieldType.DURATION);
      FIELD_TYPE_MAP.put("target_drtn_hr_cnt", XerFieldType.DURATION);
      FIELD_TYPE_MAP.put("cstr_date", XerFieldType.DATE);
      FIELD_TYPE_MAP.put("act_start_date", XerFieldType.DATE);
      FIELD_TYPE_MAP.put("act_end_date", XerFieldType.DATE);
      FIELD_TYPE_MAP.put("late_start_date", XerFieldType.DATE);
      FIELD_TYPE_MAP.put("late_end_date", XerFieldType.DATE);
      FIELD_TYPE_MAP.put("expect_end_date", XerFieldType.DATE);
      FIELD_TYPE_MAP.put("early_start_date", XerFieldType.DATE);
      FIELD_TYPE_MAP.put("early_end_date", XerFieldType.DATE);
      FIELD_TYPE_MAP.put("target_start_date", XerFieldType.DATE);
      FIELD_TYPE_MAP.put("target_end_date", XerFieldType.DATE);
      FIELD_TYPE_MAP.put("restart_date", XerFieldType.DATE);
      FIELD_TYPE_MAP.put("reend_date", XerFieldType.DATE);
      FIELD_TYPE_MAP.put("create_date", XerFieldType.DATE);
      FIELD_TYPE_MAP.put("pred_task_id", XerFieldType.INTEGER);
      FIELD_TYPE_MAP.put("lag_hr_cnt", XerFieldType.DURATION);
      FIELD_TYPE_MAP.put("remain_qty", XerFieldType.DURATION);
      FIELD_TYPE_MAP.put("target_qty", XerFieldType.DURATION);
      FIELD_TYPE_MAP.put("act_reg_qty", XerFieldType.DURATION);
      FIELD_TYPE_MAP.put("target_cost", XerFieldType.CURRENCY);
      FIELD_TYPE_MAP.put("act_reg_cost", XerFieldType.CURRENCY);
      FIELD_TYPE_MAP.put("target_start_date", XerFieldType.DATE);
      FIELD_TYPE_MAP.put("target_end_date", XerFieldType.DATE);
      FIELD_TYPE_MAP.put("act_equip_qty", XerFieldType.DOUBLE);
      FIELD_TYPE_MAP.put("remain_equip_qty", XerFieldType.DOUBLE);

      FIELD_TYPE_MAP.put("clndr_id", XerFieldType.INTEGER);
      FIELD_TYPE_MAP.put("default_flag", XerFieldType.STRING);
      FIELD_TYPE_MAP.put("clndr_name", XerFieldType.STRING);
      FIELD_TYPE_MAP.put("proj_id", XerFieldType.INTEGER);
      FIELD_TYPE_MAP.put("base_clndr_id", XerFieldType.INTEGER);
      FIELD_TYPE_MAP.put("last_chng_date", XerFieldType.STRING);
      FIELD_TYPE_MAP.put("clndr_type", XerFieldType.STRING);
      FIELD_TYPE_MAP.put("day_hr_cnt", XerFieldType.DOUBLE);
      FIELD_TYPE_MAP.put("week_hr_cnt", XerFieldType.DOUBLE);
      FIELD_TYPE_MAP.put("month_hr_cnt", XerFieldType.DOUBLE);
      FIELD_TYPE_MAP.put("year_hr_cnt", XerFieldType.DOUBLE);
      FIELD_TYPE_MAP.put("clndr_data", XerFieldType.STRING);

      FIELD_TYPE_MAP.put("seq_num", XerFieldType.INTEGER);
      FIELD_TYPE_MAP.put("taskrsrc_id", XerFieldType.INTEGER);
      FIELD_TYPE_MAP.put("parent_rsrc_id", XerFieldType.INTEGER);

      FIELD_TYPE_MAP.put("free_float_hr_cnt", XerFieldType.DURATION);
      FIELD_TYPE_MAP.put("total_float_hr_cnt", XerFieldType.DURATION);

      FIELD_TYPE_MAP.put("decimal_digit_cnt", XerFieldType.INTEGER);
      FIELD_TYPE_MAP.put("target_qty_per_hr", XerFieldType.DOUBLE);
      FIELD_TYPE_MAP.put("target_lag_drtn_hr_cnt", XerFieldType.DURATION);

      FIELD_TYPE_MAP.put("act_cost", XerFieldType.DOUBLE);
      FIELD_TYPE_MAP.put("target_cost", XerFieldType.DOUBLE);
      FIELD_TYPE_MAP.put("remain_cost", XerFieldType.DOUBLE);

      FIELD_TYPE_MAP.put("last_recalc_date", XerFieldType.DATE);

      // User Defined Fields types (UDF)
      FIELD_TYPE_MAP.put("udf_type", XerFieldType.INTEGER);
      FIELD_TYPE_MAP.put("table_name", XerFieldType.STRING);
      FIELD_TYPE_MAP.put("udf_type_name", XerFieldType.STRING);
      FIELD_TYPE_MAP.put("udf_type_label", XerFieldType.STRING);
      FIELD_TYPE_MAP.put("loginal_data_type", XerFieldType.STRING);
      FIELD_TYPE_MAP.put("super_flag", XerFieldType.STRING);
      // User Defined Fields values
      FIELD_TYPE_MAP.put("fk_id", XerFieldType.INTEGER);
      FIELD_TYPE_MAP.put("udf_date", XerFieldType.DATE);
      FIELD_TYPE_MAP.put("udf_number", XerFieldType.DOUBLE);
      FIELD_TYPE_MAP.put("udf_text", XerFieldType.STRING);
      FIELD_TYPE_MAP.put("udf_code_id", XerFieldType.INTEGER);
   }

   private static final Set<String> REQUIRED_TABLES = new HashSet<String>();
   static
   {
      REQUIRED_TABLES.add("project");
      REQUIRED_TABLES.add("calendar");
      REQUIRED_TABLES.add("rsrc");
      REQUIRED_TABLES.add("projwbs");
      REQUIRED_TABLES.add("task");
      REQUIRED_TABLES.add("taskpred");
      REQUIRED_TABLES.add("taskrsrc");
      REQUIRED_TABLES.add("currtype");
      REQUIRED_TABLES.add("udftype");
      REQUIRED_TABLES.add("udfvalue");
      REQUIRED_TABLES.add("projcost");
   }

   private static final WbsRowComparator WBS_ROW_COMPARATOR = new WbsRowComparator();
}
