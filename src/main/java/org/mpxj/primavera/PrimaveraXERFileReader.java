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

package org.mpxj.primavera;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mpxj.DataType;
import org.mpxj.FieldType;
import org.mpxj.HasCharset;
import org.mpxj.MPXJException;
import org.mpxj.Notes;
import org.mpxj.ProjectFile;
import org.mpxj.ProjectFileSharedData;
import org.mpxj.Relation;
import org.mpxj.Task;
import org.mpxj.WorkContour;
import org.mpxj.WorkContourContainer;
import org.mpxj.common.CharsetHelper;
import org.mpxj.common.LocalDateTimeHelper;
import org.mpxj.common.NumberHelper;
import org.mpxj.common.ReaderTokenizer;
import org.mpxj.common.Tokenizer;
import org.mpxj.reader.AbstractProjectStreamReader;

/**
 * This class creates a new ProjectFile instance by reading a Primavera XER file.
 */
public final class PrimaveraXERFileReader extends AbstractProjectStreamReader implements HasCharset
{
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
    * Set the Charset used to read the file.
    *
    * @param charset Charset used when reading the file
    */
   @Override public void setCharset(Charset charset)
   {
      m_charset = charset;
   }

   /**
    * Retrieve the Charset used to read the file.
    *
    * @return Charset instance
    */
   @Override public Charset getCharset()
   {
      return m_charset;
   }

   /**
    * Retrieve a flag indicating if, when using `realAll` to retrieve all
    * projects from a file, cross project relations should be linked together.
    *
    * @return true if cross project relations should be linked
    */
   public boolean getLinkCrossProjectRelations()
   {
      return m_linkCrossProjectRelations;
   }

   /**
    * Sets a flag indicating if, when using `realAll` to retrieve all
    * projects from a file, cross project relations should be linked together.
    *
    * @param linkCrossProjectRelations true if cross project relations should be linked
    */
   public void setLinkCrossProjectRelations(boolean linkCrossProjectRelations)
   {
      m_linkCrossProjectRelations = linkCrossProjectRelations;
   }

   @Override public ProjectFile read(InputStream is) throws MPXJException
   {
      ProjectFile project = null;

      // Preserve the requested project ID, this member variable is used when reading all projects
      Integer targetProjectID = m_projectID;

      // Using readAll ensures that cross project relations can be included if required
      List<ProjectFile> projects = readAll(is);

      if (!projects.isEmpty())
      {
         if (targetProjectID == null)
         {
            // We haven't been asked for a specific project: the first one will be the exported project
            project = projects.get(0);
         }
         else
         {
            // We have been asked for a specific project: find it
            project = projects.stream().filter(p -> targetProjectID.equals(p.getProjectProperties().getUniqueID())).findFirst().orElse(null);
         }
      }

      return project;
   }

   /**
    * This is a convenience method which allows all projects in an
    * XER file to be read in a single pass. External relationships
    * are not linked.
    *
    * @param is input stream
    * @return list of ProjectFile instances
    */
   @Override public List<ProjectFile> readAll(InputStream is) throws MPXJException
   {
      try
      {
         m_tables = new HashMap<>();
         m_numberFormat = new DecimalFormat();
         m_readSharedData = true;

         processFile(READ_REQUIRED_TABLES, is);

         List<Row> rows = getRows("project", null, null);
         List<ProjectFile> result = new ArrayList<>(rows.size());
         List<ExternalRelation> externalRelations = new ArrayList<>();
         ProjectFileSharedData shared = new ProjectFileSharedData();
         for (Row row : rows)
         {
            setProjectID(row.getInt("proj_id"));
            m_reader = new PrimaveraReader(shared, m_resourceFields, m_roleFields, m_wbsFields, m_taskFields, m_assignmentFields, m_matchPrimaveraWBS, m_wbsIsFullPath, m_ignoreErrors);
            ProjectFile project = readProject();
            externalRelations.addAll(m_reader.getExternalRelations());

            result.add(project);
         }

         // Sort to ensure exported project is first
         result.sort((o1, o2) -> Boolean.compare(o2.getProjectProperties().getExportFlag(), o1.getProjectProperties().getExportFlag()));

         if (m_linkCrossProjectRelations)
         {
            for (ExternalRelation externalRelation : externalRelations)
            {
               Task predecessorTask;
               // we could aggregate the project task id maps but that's likely more work
               // than just looping through the projects
               for (ProjectFile proj : result)
               {
                  predecessorTask = proj.getTaskByUniqueID(externalRelation.externalTaskUniqueID());
                  if (predecessorTask != null)
                  {
                     Task successorTask = externalRelation.getTargetTask();

                     // We need to ensure that the relation is present in both
                     // projects so that predecessors and successors are populated
                     // in both projects.

                     ProjectFile successorProject = successorTask.getParentFile();
                     successorProject.getRelations().addPredecessor(new Relation.Builder()
                        .predecessorTask(predecessorTask)
                        .successorTask(successorTask)
                        .type(externalRelation.getType())
                        .lag(externalRelation.getLag())
                        .uniqueID(externalRelation.getUniqueID())
                        .notes(externalRelation.getNotes()));

                     ProjectFile predecessorProject = predecessorTask.getParentFile();
                     predecessorProject.getRelations().addPredecessor(new Relation.Builder()
                        .predecessorTask(predecessorTask)
                        .successorTask(successorTask)
                        .type(externalRelation.getType())
                        .lag(externalRelation.getLag())
                        .uniqueID(externalRelation.getUniqueID())
                        .notes(externalRelation.getNotes()));

                     break;
                  }
               }
               // if predecessorTask not found the external task is outside of the file so ignore
            }
         }

         return result;
      }

      finally
      {
         m_tables = null;
         m_numberFormat = null;
         m_reader = null;
      }
   }

   /**
    * Common project read functionality.
    *
    * @return ProjectFile instance
    */
   private ProjectFile readProject()
   {
      try
      {
         ProjectFile project = m_reader.getProject();
         project.getProjectProperties().setFileApplication("Primavera");
         project.getProjectProperties().setFileType("XER");
         addListenersToProject(project);
         processProjectID();
         project.getProjectProperties().setUniqueID(m_projectID);

         if (m_readSharedData)
         {
            m_readSharedData = false;
            processCurrencies();
            processLocations();
            processShifts();
            processUnitsOfMeasure();
            processExpenseCategories();
            processCostAccounts();
            processWorkContours();
            processNotebookTopics();
            processUdfDefinitions();
            processProjectCodeDefinitions();
            processResourceCodeDefinitions();
            processRoleCodeDefinitions();
            processResourceAssignmentCodeDefinitions();
            processActivityCodeDefinitions();
         }

         processActivityCodeAssignments();
         processResourceCodeAssignments();
         processRoleCodeAssignments();
         processResourceAssignmentCodeAssignments();
         processUdfValues();
         processCalendars();
         processResources();
         processRoles();
         processRoleAssignments();
         processResourceRates();
         processRoleRates();

         processProjectProperties();
         processTasks();
         processPredecessors();
         processAssignments();
         processExpenseItems();
         processActivitySteps();

         m_reader.rollupValues();
         project.updateStructure();
         project.readComplete();

         return project;
      }

      finally
      {
         m_currentTableName = null;
         m_currentTable = null;
         m_currentFieldNames = null;
         m_defaultCurrencyName = null;
         m_numberFormat = null;
         m_defaultCurrencyData = null;
      }
   }

   /**
    * Reads the XER file table and row structure ready for processing.
    *
    * @param requiredTables names of tables which should be read
    * @param is input stream
    */
   private void processFile(Set<String> requiredTables, InputStream is) throws MPXJException
   {
      int line = 1;
      m_requiredTables = requiredTables;

      try
      {
         InputStreamReader reader = new InputStreamReader(is, getCharset());
         Tokenizer tk = new ReaderTokenizer(reader);
         tk.setDelimiter('\t');
         List<String> record = new ArrayList<>();

         //
         // Read the first record as a special case so we can check for the header record token
         //
         if (tk.getType() == Tokenizer.TT_EOF)
         {
            throw new MPXJException(MPXJException.INVALID_FILE);
         }

         readRecord(tk, record);
         if (record.isEmpty() || !"ERMHDR".equals(record.get(0)))
         {
            throw new MPXJException(MPXJException.INVALID_FILE);
         }

         processRecord(record);

         //
         // Read the remaining records
         //
         while (tk.getType() != Tokenizer.TT_EOF)
         {
            readRecord(tk, record);
            if (!record.isEmpty())
            {
               if (processRecord(record))
               {
                  break;
               }
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
      if (currencyName.equalsIgnoreCase(m_defaultCurrencyName))
      {
         DecimalFormatSymbols symbols = new DecimalFormatSymbols();
         symbols.setDecimalSeparator(row.getString("decimal_symbol").charAt(0));
         symbols.setGroupingSeparator(row.getString("digit_group_symbol").charAt(0));

         DecimalFormat nf = new DecimalFormat();
         nf.setDecimalFormatSymbols(symbols);
         nf.applyPattern("#.#");

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
    */
   public Map<Integer, String> listProjects(InputStream is) throws MPXJException
   {
      try
      {
         m_tables = new HashMap<>();
         m_numberFormat = new DecimalFormat();

         processFile(LIST_REQUIRED_TABLES, is);

         Map<Integer, String> result = new HashMap<>();

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
         m_numberFormat = null;
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

      rows = getRows("projpcat", "proj_id", m_projectID);
      m_reader.processProjectCodeAssignments(rows);

      //
      // Process XER-specific attributes
      //
      if (m_defaultCurrencyData != null)
      {
         m_reader.processDefaultCurrency(m_defaultCurrencyData);
      }

      processScheduleOptions();
   }

   /**
    * Process currencies.
    */
   private void processCurrencies()
   {
      m_reader.processCurrencies(getRows("currtype", null, null));
   }

   /**
    * Process locations.
    */
   private void processLocations()
   {
      m_reader.processLocations(getRows("location", null, null));
   }

   /**
    * Process shifts.
    */
   private void processShifts()
   {
      m_reader.processShifts(getRows("shift", null, null), getRows("shiftper", null, null));
   }

   /**
    * Process expense categories.
    */
   private void processExpenseCategories()
   {
      m_reader.processExpenseCategories(getRows("costtype", null, null));
   }

   /**
    * Process expense items.
    */
   private void processExpenseItems()
   {
      m_reader.processExpenseItems(getRows("projcost", "proj_id", m_projectID));
   }

   /**
    * Process activity steps.
    */
   private void processActivitySteps()
   {
      m_reader.processActivitySteps(getRows("taskproc", "proj_id", m_projectID));
   }

   /**
    * Process cost accounts.
    */
   private void processCostAccounts()
   {
      m_reader.processCostAccounts(getRows("account", null, null));
   }

   /**
    * Process units of measure.
    */
   private void processUnitsOfMeasure()
   {
      m_reader.processUnitsOfMeasure(getRows("umeasure", null, null));
   }

   /**
    * Process notebook topics.
    */
   private void processNotebookTopics()
   {
      m_reader.processNotebookTopics(getRows("memotype", null, null));
   }

   /**
    * Process activity code definitions.
    */
   private void processActivityCodeDefinitions()
   {
      List<Row> types = getRows("actvtype", null, null);
      List<Row> typeValues = getRows("actvcode", null, null);
      m_reader.processActivityCodeDefinitions(types, typeValues);
   }

   /**
    * Process project code definitions.
    */
   private void processProjectCodeDefinitions()
   {
      List<Row> types = getRows("pcattype", null, null);
      List<Row> typeValues = getRows("pcatval", null, null);
      m_reader.processProjectCodeDefinitions(types, typeValues);
   }

   /**
    * Process resource code definitions.
    */
   private void processResourceCodeDefinitions()
   {
      List<Row> types = getRows("rcattype", null, null);
      List<Row> typeValues = getRows("rcatval", null, null);
      m_reader.processResourceCodeDefinitions(types, typeValues);
   }

   /**
    * Process role code definitions.
    */
   private void processRoleCodeDefinitions()
   {
      List<Row> types = getRows("rolecattype", null, null);
      List<Row> typeValues = getRows("rolecatval", null, null);
      m_reader.processRoleCodeDefinitions(types, typeValues);
   }

   /**
    * Process resource assignment code definitions.
    */
   private void processResourceAssignmentCodeDefinitions()
   {
      List<Row> types = getRows("asgnmntcattype", null, null);
      List<Row> typeValues = getRows("asgnmntcatval", null, null);
      m_reader.processResourceAssignmentCodeDefinitions(types, typeValues);
   }

   /**
    * Process activity code assignments.
    */
   private void processActivityCodeAssignments()
   {
      List<Row> assignments = getRows("taskactv", null, null);
      m_reader.processActivityCodeAssignments(assignments);
   }

   /**
    * Process resource code assignments.
    */
   private void processResourceCodeAssignments()
   {
      List<Row> assignments = getRows("rsrcrcat", null, null);
      m_reader.processResourceCodeAssignments(assignments);
   }

   /**
    * Process role code assignments.
    */
   private void processRoleCodeAssignments()
   {
      List<Row> assignments = getRows("rolercat", null, null);
      m_reader.processRoleCodeAssignments(assignments);
   }

   /**
    * Process resource assignment code assignments.
    */
   private void processResourceAssignmentCodeAssignments()
   {
      List<Row> assignments = getRows("asgnmntacat", null, null);
      m_reader.processResourceAssignmentCodeAssignments(assignments);
   }

   /**
    * Process schedule options from SCHEDOPTIONS.
    * This is represented as the PROJPROP table in a P6 database.
    */
   private void processScheduleOptions()
   {
      List<Row> rows = getRows("schedoptions", "proj_id", m_projectID);
      if (!rows.isEmpty())
      {
         m_reader.processScheduleOptions(rows.get(0));
      }
   }

   /**
    * Process user defined field definitions.
    */
   private void processUdfDefinitions()
   {
      List<Row> fields = getRows("udftype", null, null);
      m_reader.processUdfDefinitions(fields);
   }

   /**
    * Process user defined field values.
    */
   private void processUdfValues()
   {
      List<Row> values = getRows("udfvalue", null, null);
      m_reader.processUdfValues(values);
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
    * Process roles.
    */
   private void processRoles()
   {
      List<Row> rows = getRows("roles", null, null);
      m_reader.processRoles(rows);
   }

   /**
    * Process role assignments.
    */
   private void processRoleAssignments()
   {
      List<Row> rows = getRows("rsrcrole", null, null);
      m_reader.processRoleAssignments(rows);
   }

   /**
    * Process resource rates.
    */
   private void processResourceRates()
   {
      List<Row> rows = getRows("rsrcrate", null, null);
      m_reader.processResourceRates(rows);
   }

   /**
    * Process role rates.
    */
   private void processRoleRates()
   {
      List<Row> rows = getRows("rolerate", null, null);
      m_reader.processRoleRates(rows);
      m_reader.processRoleAvailability(rows);
   }

   /**
    * Process tasks.
    */
   private void processTasks()
   {
      List<Row> wbs = getRows("projwbs", "proj_id", m_projectID);
      List<Row> tasks = getRows("task", "proj_id", m_projectID);
      Map<Integer, Notes> wbsNotes = m_reader.getNotes(getRows("wbsmemo", "proj_id", m_projectID), "wbs_memo_id", "wbs_id", "wbs_memo");
      Map<Integer, Notes> taskNotes = m_reader.getNotes(getRows("taskmemo", "proj_id", m_projectID), "memo_id", "task_id", "task_memo");

      wbs.sort(WBS_ROW_COMPARATOR);
      m_reader.processTasks(wbs, tasks, wbsNotes, taskNotes);
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
    * Process resource curves.
    */
   private void processWorkContours()
   {
      WorkContourContainer contours = m_reader.getProject().getWorkContours();

      List<Row> rows = getRows("rsrccurvdata", null, null);
      for (Row row : rows)
      {
         Integer id = row.getInteger("curv_id");
         if (contours.getByUniqueID(id) != null)
         {
            continue;
         }

         double[] values =
         {
            NumberHelper.getDouble(row.getDouble("pct_usage_0")),
            NumberHelper.getDouble(row.getDouble("pct_usage_1")),
            NumberHelper.getDouble(row.getDouble("pct_usage_2")),
            NumberHelper.getDouble(row.getDouble("pct_usage_3")),
            NumberHelper.getDouble(row.getDouble("pct_usage_4")),
            NumberHelper.getDouble(row.getDouble("pct_usage_5")),
            NumberHelper.getDouble(row.getDouble("pct_usage_6")),
            NumberHelper.getDouble(row.getDouble("pct_usage_7")),
            NumberHelper.getDouble(row.getDouble("pct_usage_8")),
            NumberHelper.getDouble(row.getDouble("pct_usage_9")),
            NumberHelper.getDouble(row.getDouble("pct_usage_10")),
            NumberHelper.getDouble(row.getDouble("pct_usage_11")),
            NumberHelper.getDouble(row.getDouble("pct_usage_12")),
            NumberHelper.getDouble(row.getDouble("pct_usage_13")),
            NumberHelper.getDouble(row.getDouble("pct_usage_14")),
            NumberHelper.getDouble(row.getDouble("pct_usage_15")),
            NumberHelper.getDouble(row.getDouble("pct_usage_16")),
            NumberHelper.getDouble(row.getDouble("pct_usage_17")),
            NumberHelper.getDouble(row.getDouble("pct_usage_18")),
            NumberHelper.getDouble(row.getDouble("pct_usage_19")),
            NumberHelper.getDouble(row.getDouble("pct_usage_20"))
         };

         contours.add(new WorkContour(id, row.getString("curv_name"), row.getBoolean("default_flag"), values));
      }
   }

   /**
    * Reads each token from a single record and adds it to a list.
    *
    * @param tk tokenizer
    * @param record list of tokens
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
    * Determine the XER record type and process.
    *
    * @param record record to be processed
    * @return flag indicating if this is the last record in the file to be processed
    */
   private boolean processRecord(List<String> record)
   {
      XerRecordType type = RECORD_TYPE_MAP.get(record.get(0));
      return type != null && processRecord(type, record);
   }

   /**
    * Handles a complete record at a time, stores it in a form ready for
    * further processing.
    *
    * @param type XER record type
    * @param record record to be processed
    * @return flag indicating if this is the last record in the file to be processed
    */
   private boolean processRecord(XerRecordType type, List<String> record)
   {
      boolean done = false;

      switch (type)
      {
         case HEADER:
         {
            processHeader(record);
            break;
         }

         case TABLE:
         {
            m_currentTableName = record.size() > 1 ? record.get(1).toLowerCase() : null;
            m_skipTable = !m_requiredTables.contains(m_currentTableName);
            if (m_skipTable)
            {
               m_currentTable = null;
            }
            else
            {
               m_currentTable = new ArrayList<>();
               m_tables.put(m_currentTableName, m_currentTable);
            }
            break;
         }

         case FIELDS:
         {
            if (m_skipTable)
            {
               m_currentFieldNames = null;
               m_currentFieldIndex = null;
            }
            else
            {
               m_currentFieldNames = record.toArray(new String[0]);
               m_currentFieldIndex = new HashMap<>();
               for (int loop = 0; loop < m_currentFieldNames.length; loop++)
               {
                  m_currentFieldNames[loop] = m_currentFieldNames[loop].toLowerCase();
                  m_currentFieldIndex.put(m_currentFieldNames[loop], Integer.valueOf(loop));
               }
            }
            break;
         }

         case DATA:
         {
            if (!m_skipTable)
            {
               processData(record);
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
    * Populate a table row from a data record.
    *
    * @param record data record.
    */
   private void processData(List<String> record)
   {
      Object[] array = new Object[m_currentFieldIndex.size()];

      for (int loop = 1; loop < record.size(); loop++)
      {
         // We have more fields than field names, stop processing
         if (loop == m_currentFieldNames.length)
         {
            break;
         }

         String fieldName = m_currentFieldNames[loop];
         String fieldValue = record.get(loop);
         DataType fieldType = getFieldType(fieldName);

         Object objectValue;
         if (fieldValue.isEmpty())
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
                     objectValue = LocalDateTimeHelper.parseBest(m_df, fieldValue);
                  }

                  catch (DateTimeParseException ex)
                  {
                     objectValue = fieldValue;
                  }

                  break;
               }

               case CURRENCY:
               case NUMERIC:
               case DURATION:
               {
                  try
                  {
                     objectValue = Double.valueOf(m_numberFormat.parse(fieldValue.trim()).doubleValue());
                  }

                  catch (ParseException ex)
                  {
                     objectValue = fieldValue;
                  }
                  break;
               }

               case INTEGER:
               {
                  try
                  {
                     objectValue = Integer.valueOf(fieldValue.trim());
                  }

                  catch (NumberFormatException ex)
                  {
                     objectValue = fieldValue;
                  }
                  break;
               }

               default:
               {
                  objectValue = unescapeQuotes(fieldValue);
                  break;
               }
            }
         }

         array[loop] = objectValue;
      }

      Row currentRow = new ArrayRow(m_currentFieldIndex, array, m_ignoreErrors);
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

   /**
    * Handle unescaping of double quotes.
    *
    * @param value string value
    * @return string value with unescaped double quotes
    */
   private String unescapeQuotes(String value)
   {
      if (value == null || value.isEmpty())
      {
         return value;
      }

      if (!value.contains("\"\""))
      {
         return value;
      }

      return value.replace("\"\"", "\"");
   }

   /**
    * Given a field name, retrieve its type.
    *
    * @param fieldName field name
    * @return field type
    */
   private DataType getFieldType(String fieldName)
   {
      DataType fieldType;

      // This is the only field name collision we've encountered so far
      // when determining the field type. Ideally we'd perform a lookup
      // based on table name and field name, but as we only have this one
      // collision, we'll take a simpler approach for now.
      if (m_currentTableName.equals("projcost") && fieldName.equals("target_qty"))
      {
         fieldType = DataType.CURRENCY;
      }
      else
      {
         fieldType = m_fieldTypes.getOrDefault(fieldName, DataType.STRING);
      }
      return fieldType;
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
    * Customise the data retrieved by this reader by modifying the contents of this map.
    *
    * @return Map of XER column names to types
    */
   public Map<String, DataType> getFieldTypeMap()
   {
      return m_fieldTypes;
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
   public Map<FieldType, String> getRoleFieldMap()
   {
      return m_roleFields;
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
   public Map<FieldType, String> getActivityFieldMap()
   {
      return m_taskFields;
   }

   /**
    * Customise the data retrieved by this reader by modifying the contents of this map.
    *
    * @return Primavera field name to MPXJ field type map
    */
   public Map<FieldType, String> getAssignmentFieldMap()
   {
      return m_assignmentFields;
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
         result = Collections.emptyList();
      }
      else
      {
         if (columnName == null)
         {
            result = table;
         }
         else
         {
            result = new ArrayList<>();
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

   /**
    * Returns true if the WBS attribute of a summary task
    * contains a dot separated list representing the WBS hierarchy.
    *
    * @return true if WBS attribute is a hierarchy
    */
   public boolean getWbsIsFullPath()
   {
      return m_wbsIsFullPath;
   }

   /**
    * Sets a flag indicating if the WBS attribute of a summary task
    * contains a dot separated list representing the WBS hierarchy.
    *
    * @param wbsIsFullPath true if WBS attribute is a hierarchy
    */
   public void setWbsIsFullPath(boolean wbsIsFullPath)
   {
      m_wbsIsFullPath = wbsIsFullPath;
   }

   /**
    * Set a flag to determine if datatype parse errors can be ignored.
    * Defaults to true.
    *
    * @param ignoreErrors pass true to ignore errors
    */
   public void setIgnoreErrors(boolean ignoreErrors)
   {
      m_ignoreErrors = ignoreErrors;
   }

   /**
    * Retrieve the flag which determines if datatype parse errors can be ignored.
    * Defaults to true.
    *
    * @return true if datatype parse errors are ignored
    */
   public boolean getIgnoreErrors()
   {
      return m_ignoreErrors;
   }

   /**
    * Rather than using FIELD_TYPE_MAP directly, we copy it. This allows the
    * caller to add or modify type mappings for an individual instance of
    * the reader class.
    *
    * @return field type map instance
    */
   private Map<String, DataType> getDefaultFieldTypes()
   {
      return new HashMap<>(FIELD_TYPE_MAP);
   }

   private Charset m_charset = CharsetHelper.CP1252;
   private PrimaveraReader m_reader;
   private Integer m_projectID;
   boolean m_skipTable;
   private Map<String, List<Row>> m_tables;
   private String m_currentTableName;
   private List<Row> m_currentTable;
   private String[] m_currentFieldNames;
   private Map<String, Integer> m_currentFieldIndex;
   private String m_defaultCurrencyName;
   private DecimalFormat m_numberFormat;
   private Row m_defaultCurrencyData;
   private final DateTimeFormatter m_df = DateTimeFormatter.ofPattern("yyyy-M-dd[ HH[:][.]mm[[:][.]ss]]");
   private final Map<FieldType, String> m_resourceFields = PrimaveraReader.getDefaultResourceFieldMap();
   private final Map<FieldType, String> m_roleFields = PrimaveraReader.getDefaultRoleFieldMap();
   private final Map<FieldType, String> m_wbsFields = PrimaveraReader.getDefaultWbsFieldMap();
   private final Map<FieldType, String> m_taskFields = PrimaveraReader.getDefaultTaskFieldMap();
   private final Map<FieldType, String> m_assignmentFields = PrimaveraReader.getDefaultAssignmentFieldMap();
   private final Map<String, DataType> m_fieldTypes = getDefaultFieldTypes();
   private boolean m_matchPrimaveraWBS = true;
   private boolean m_wbsIsFullPath = true;
   private boolean m_linkCrossProjectRelations;
   private boolean m_ignoreErrors = true;
   private boolean m_readSharedData;
   private Set<String> m_requiredTables;

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
    * Maps record type text to record types.
    */
   private static final Map<String, XerRecordType> RECORD_TYPE_MAP = new HashMap<>();
   static
   {
      RECORD_TYPE_MAP.put("ERMHDR", XerRecordType.HEADER);
      RECORD_TYPE_MAP.put("%T", XerRecordType.TABLE);
      RECORD_TYPE_MAP.put("%F", XerRecordType.FIELDS);
      RECORD_TYPE_MAP.put("%R", XerRecordType.DATA);
      RECORD_TYPE_MAP.put("", XerRecordType.DATA); // Multiline data
      RECORD_TYPE_MAP.put("%E", XerRecordType.END);
   }

   /**
    * Maps field names to data types.
    */
   private static final Map<String, DataType> FIELD_TYPE_MAP = new HashMap<>();
   static
   {
      FIELD_TYPE_MAP.put("acct_id", DataType.INTEGER);
      FIELD_TYPE_MAP.put("acct_seq_num", DataType.INTEGER);
      FIELD_TYPE_MAP.put("act_cost", DataType.NUMERIC);
      FIELD_TYPE_MAP.put("act_end_date", DataType.DATE);
      FIELD_TYPE_MAP.put("act_equip_qty", DataType.DURATION);
      FIELD_TYPE_MAP.put("act_ot_cost", DataType.CURRENCY);
      FIELD_TYPE_MAP.put("act_ot_qty", DataType.DURATION);
      FIELD_TYPE_MAP.put("act_reg_cost", DataType.CURRENCY);
      FIELD_TYPE_MAP.put("act_reg_qty", DataType.DURATION);
      FIELD_TYPE_MAP.put("act_start_date", DataType.DATE);
      FIELD_TYPE_MAP.put("act_work_qty", DataType.DURATION);
      FIELD_TYPE_MAP.put("actv_code_id", DataType.INTEGER);
      FIELD_TYPE_MAP.put("actv_code_type_id", DataType.INTEGER);
      FIELD_TYPE_MAP.put("actv_short_len", DataType.INTEGER);
      FIELD_TYPE_MAP.put("anticip_end_date", DataType.DATE);
      FIELD_TYPE_MAP.put("anticip_start_date", DataType.DATE);
      FIELD_TYPE_MAP.put("asgnmnt_catg_id", DataType.NUMERIC);
      FIELD_TYPE_MAP.put("asgnmnt_catg_short_len", DataType.NUMERIC);
      FIELD_TYPE_MAP.put("asgnmnt_catg_type_id", DataType.NUMERIC);
      FIELD_TYPE_MAP.put("base_clndr_id", DataType.INTEGER);
      FIELD_TYPE_MAP.put("base_exch_rate", DataType.NUMERIC);
      FIELD_TYPE_MAP.put("clndr_data", DataType.STRING);
      FIELD_TYPE_MAP.put("clndr_id", DataType.INTEGER);
      FIELD_TYPE_MAP.put("clndr_name", DataType.STRING);
      FIELD_TYPE_MAP.put("clndr_type", DataType.STRING);
      FIELD_TYPE_MAP.put("complete_pct", DataType.NUMERIC);
      FIELD_TYPE_MAP.put("cost_item_id", DataType.INTEGER);
      FIELD_TYPE_MAP.put("cost_per_qty", DataType.NUMERIC);
      FIELD_TYPE_MAP.put("cost_per_qty2", DataType.NUMERIC);
      FIELD_TYPE_MAP.put("cost_per_qty3", DataType.NUMERIC);
      FIELD_TYPE_MAP.put("cost_per_qty4", DataType.NUMERIC);
      FIELD_TYPE_MAP.put("cost_per_qty5", DataType.NUMERIC);
      FIELD_TYPE_MAP.put("cost_type_id", DataType.INTEGER);
      FIELD_TYPE_MAP.put("create_date", DataType.DATE);
      FIELD_TYPE_MAP.put("critical_drtn_hr_cnt", DataType.NUMERIC);
      FIELD_TYPE_MAP.put("cstr_date", DataType.DATE);
      FIELD_TYPE_MAP.put("cstr_date2", DataType.DATE);
      FIELD_TYPE_MAP.put("curr_id", DataType.INTEGER);
      FIELD_TYPE_MAP.put("curv_id", DataType.NUMERIC);
      FIELD_TYPE_MAP.put("day_hr_cnt", DataType.NUMERIC);
      FIELD_TYPE_MAP.put("decimal_digit_cnt", DataType.INTEGER);
      FIELD_TYPE_MAP.put("default_flag", DataType.STRING);
      FIELD_TYPE_MAP.put("def_qty_per_hr", DataType.NUMERIC);
      FIELD_TYPE_MAP.put("driving_path_flag", DataType.STRING);
      FIELD_TYPE_MAP.put("early_end_date", DataType.DATE);
      FIELD_TYPE_MAP.put("early_start_date", DataType.DATE);
      FIELD_TYPE_MAP.put("expect_end_date", DataType.DATE);
      FIELD_TYPE_MAP.put("external_early_start_date", DataType.DATE);
      FIELD_TYPE_MAP.put("external_late_end_date", DataType.DATE);
      FIELD_TYPE_MAP.put("fk_id", DataType.INTEGER);
      FIELD_TYPE_MAP.put("float_path", DataType.INTEGER);
      FIELD_TYPE_MAP.put("float_path_order", DataType.INTEGER);
      FIELD_TYPE_MAP.put("free_float_hr_cnt", DataType.DURATION);
      FIELD_TYPE_MAP.put("fy_start_month_num", DataType.INTEGER);
      FIELD_TYPE_MAP.put("group_digit_cnt", DataType.INTEGER);
      FIELD_TYPE_MAP.put("indep_remain_total_cost", DataType.CURRENCY);
      FIELD_TYPE_MAP.put("indep_remain_work_qty", DataType.DURATION);
      FIELD_TYPE_MAP.put("lag_hr_cnt", DataType.DURATION);
      FIELD_TYPE_MAP.put("last_chng_date", DataType.STRING);
      FIELD_TYPE_MAP.put("last_recalc_date", DataType.DATE);
      FIELD_TYPE_MAP.put("late_end_date", DataType.DATE);
      FIELD_TYPE_MAP.put("late_start_date", DataType.DATE);
      FIELD_TYPE_MAP.put("latitude", DataType.NUMERIC);
      FIELD_TYPE_MAP.put("location_id", DataType.INTEGER);
      FIELD_TYPE_MAP.put("loginal_data_type", DataType.STRING);
      FIELD_TYPE_MAP.put("longitude", DataType.NUMERIC);
      FIELD_TYPE_MAP.put("max_qty_per_hr", DataType.NUMERIC);
      FIELD_TYPE_MAP.put("memo_type_id", DataType.INTEGER);
      FIELD_TYPE_MAP.put("memo_id", DataType.INTEGER);
      FIELD_TYPE_MAP.put("month_hr_cnt", DataType.NUMERIC);
      FIELD_TYPE_MAP.put("orig_cost", DataType.CURRENCY);
      FIELD_TYPE_MAP.put("parent_acct_id", DataType.INTEGER);
      FIELD_TYPE_MAP.put("parent_actv_code_id", DataType.INTEGER);
      FIELD_TYPE_MAP.put("parent_asgnmnt_catg_id", DataType.NUMERIC);
      FIELD_TYPE_MAP.put("parent_proj_catg_id", DataType.INTEGER);
      FIELD_TYPE_MAP.put("parent_role_id", DataType.INTEGER);
      FIELD_TYPE_MAP.put("parent_role_catg_id", DataType.INTEGER);
      FIELD_TYPE_MAP.put("parent_rsrc_catg_id", DataType.INTEGER);
      FIELD_TYPE_MAP.put("parent_rsrc_id", DataType.INTEGER);
      FIELD_TYPE_MAP.put("parent_wbs_id", DataType.INTEGER);
      FIELD_TYPE_MAP.put("pct_usage_0", DataType.NUMERIC);
      FIELD_TYPE_MAP.put("pct_usage_1", DataType.NUMERIC);
      FIELD_TYPE_MAP.put("pct_usage_2", DataType.NUMERIC);
      FIELD_TYPE_MAP.put("pct_usage_3", DataType.NUMERIC);
      FIELD_TYPE_MAP.put("pct_usage_4", DataType.NUMERIC);
      FIELD_TYPE_MAP.put("pct_usage_5", DataType.NUMERIC);
      FIELD_TYPE_MAP.put("pct_usage_6", DataType.NUMERIC);
      FIELD_TYPE_MAP.put("pct_usage_7", DataType.NUMERIC);
      FIELD_TYPE_MAP.put("pct_usage_8", DataType.NUMERIC);
      FIELD_TYPE_MAP.put("pct_usage_9", DataType.NUMERIC);
      FIELD_TYPE_MAP.put("pct_usage_10", DataType.NUMERIC);
      FIELD_TYPE_MAP.put("pct_usage_11", DataType.NUMERIC);
      FIELD_TYPE_MAP.put("pct_usage_12", DataType.NUMERIC);
      FIELD_TYPE_MAP.put("pct_usage_13", DataType.NUMERIC);
      FIELD_TYPE_MAP.put("pct_usage_14", DataType.NUMERIC);
      FIELD_TYPE_MAP.put("pct_usage_15", DataType.NUMERIC);
      FIELD_TYPE_MAP.put("pct_usage_16", DataType.NUMERIC);
      FIELD_TYPE_MAP.put("pct_usage_17", DataType.NUMERIC);
      FIELD_TYPE_MAP.put("pct_usage_18", DataType.NUMERIC);
      FIELD_TYPE_MAP.put("pct_usage_19", DataType.NUMERIC);
      FIELD_TYPE_MAP.put("pct_usage_20", DataType.NUMERIC);
      FIELD_TYPE_MAP.put("phys_complete_pct", DataType.NUMERIC);
      FIELD_TYPE_MAP.put("plan_end_date", DataType.DATE);
      FIELD_TYPE_MAP.put("plan_start_date", DataType.DATE);
      FIELD_TYPE_MAP.put("pred_task_id", DataType.INTEGER);
      FIELD_TYPE_MAP.put("proc_id", DataType.INTEGER);
      FIELD_TYPE_MAP.put("proc_wt", DataType.NUMERIC);
      FIELD_TYPE_MAP.put("proj_catg_id", DataType.INTEGER);
      FIELD_TYPE_MAP.put("proj_catg_type_id", DataType.INTEGER);
      FIELD_TYPE_MAP.put("proj_catg_short_len", DataType.INTEGER);
      FIELD_TYPE_MAP.put("proj_id", DataType.INTEGER);
      FIELD_TYPE_MAP.put("reend_date", DataType.DATE);
      FIELD_TYPE_MAP.put("rem_late_start_date", DataType.DATE);
      FIELD_TYPE_MAP.put("rem_late_end_date", DataType.DATE);
      FIELD_TYPE_MAP.put("remain_cost", DataType.NUMERIC);
      FIELD_TYPE_MAP.put("remain_drtn_hr_cnt", DataType.DURATION);
      FIELD_TYPE_MAP.put("remain_equip_qty", DataType.DURATION);
      FIELD_TYPE_MAP.put("remain_qty", DataType.DURATION);
      FIELD_TYPE_MAP.put("remain_qty_per_hr", DataType.NUMERIC);
      FIELD_TYPE_MAP.put("remain_work_qty", DataType.DURATION);
      FIELD_TYPE_MAP.put("restart_date", DataType.DATE);
      FIELD_TYPE_MAP.put("resume_date", DataType.DATE);
      FIELD_TYPE_MAP.put("role_id", DataType.INTEGER);
      FIELD_TYPE_MAP.put("role_catg_id", DataType.INTEGER);
      FIELD_TYPE_MAP.put("role_catg_short_len", DataType.INTEGER);
      FIELD_TYPE_MAP.put("role_catg_type_id", DataType.INTEGER);
      FIELD_TYPE_MAP.put("rsrc_id", DataType.INTEGER);
      FIELD_TYPE_MAP.put("rsrc_catg_id", DataType.INTEGER);
      FIELD_TYPE_MAP.put("rsrc_catg_type_id", DataType.INTEGER);
      FIELD_TYPE_MAP.put("rsrc_catg_short_len", DataType.INTEGER);
      FIELD_TYPE_MAP.put("rsrc_role_id", DataType.INTEGER);
      FIELD_TYPE_MAP.put("rsrc_seq_num", DataType.INTEGER);
      FIELD_TYPE_MAP.put("scd_end_date", DataType.DATE);
      FIELD_TYPE_MAP.put("sched_calendar_on_relationship_lag", DataType.STRING);
      FIELD_TYPE_MAP.put("seq_num", DataType.INTEGER);
      FIELD_TYPE_MAP.put("shift_id", DataType.INTEGER);
      FIELD_TYPE_MAP.put("shift_period_id", DataType.INTEGER);
      FIELD_TYPE_MAP.put("shift_start_hr_num", DataType.INTEGER);
      FIELD_TYPE_MAP.put("skill_level", DataType.INTEGER);
      FIELD_TYPE_MAP.put("start_date", DataType.DATE);
      FIELD_TYPE_MAP.put("sum_base_proj_id", DataType.INTEGER);
      FIELD_TYPE_MAP.put("super_flag", DataType.STRING);
      FIELD_TYPE_MAP.put("suspend_date", DataType.DATE);
      FIELD_TYPE_MAP.put("table_name", DataType.STRING);
      FIELD_TYPE_MAP.put("target_cost", DataType.CURRENCY);
      FIELD_TYPE_MAP.put("target_drtn_hr_cnt", DataType.DURATION);
      FIELD_TYPE_MAP.put("target_end_date", DataType.DATE);
      FIELD_TYPE_MAP.put("target_equip_qty", DataType.DURATION);
      FIELD_TYPE_MAP.put("target_lag_drtn_hr_cnt", DataType.DURATION);
      FIELD_TYPE_MAP.put("target_qty", DataType.DURATION);
      FIELD_TYPE_MAP.put("target_qty_per_hr", DataType.NUMERIC);
      FIELD_TYPE_MAP.put("target_start_date", DataType.DATE);
      FIELD_TYPE_MAP.put("target_work_qty", DataType.DURATION);
      FIELD_TYPE_MAP.put("task_code_base", DataType.INTEGER);
      FIELD_TYPE_MAP.put("task_code_step", DataType.INTEGER);
      FIELD_TYPE_MAP.put("task_id", DataType.INTEGER);
      FIELD_TYPE_MAP.put("task_pred_id", DataType.INTEGER);
      FIELD_TYPE_MAP.put("taskrsrc_id", DataType.INTEGER);
      FIELD_TYPE_MAP.put("tmpl_guid", DataType.GUID);
      FIELD_TYPE_MAP.put("total_float_hr_cnt", DataType.DURATION);
      FIELD_TYPE_MAP.put("udf_code_id", DataType.INTEGER);
      FIELD_TYPE_MAP.put("udf_date", DataType.DATE);
      FIELD_TYPE_MAP.put("udf_number", DataType.NUMERIC);
      FIELD_TYPE_MAP.put("udf_text", DataType.STRING);
      FIELD_TYPE_MAP.put("udf_type", DataType.INTEGER);
      FIELD_TYPE_MAP.put("udf_type_id", DataType.INTEGER);
      FIELD_TYPE_MAP.put("udf_type_label", DataType.STRING);
      FIELD_TYPE_MAP.put("udf_type_name", DataType.STRING);
      FIELD_TYPE_MAP.put("unit_id", DataType.INTEGER);
      FIELD_TYPE_MAP.put("wbs_id", DataType.INTEGER);
      FIELD_TYPE_MAP.put("wbs_memo_id", DataType.INTEGER);
      FIELD_TYPE_MAP.put("week_hr_cnt", DataType.NUMERIC);
      FIELD_TYPE_MAP.put("year_hr_cnt", DataType.NUMERIC);
   }

   private static final Set<String> LIST_REQUIRED_TABLES = new HashSet<>();
   static
   {
      LIST_REQUIRED_TABLES.add("project");
   }

   private static final Set<String> READ_REQUIRED_TABLES = new HashSet<>();
   static
   {
      READ_REQUIRED_TABLES.add("project");
      READ_REQUIRED_TABLES.add("calendar");
      READ_REQUIRED_TABLES.add("rsrc");
      READ_REQUIRED_TABLES.add("rsrcrate");
      READ_REQUIRED_TABLES.add("projwbs");
      READ_REQUIRED_TABLES.add("task");
      READ_REQUIRED_TABLES.add("taskpred");
      READ_REQUIRED_TABLES.add("taskrsrc");
      READ_REQUIRED_TABLES.add("currtype");
      READ_REQUIRED_TABLES.add("udftype");
      READ_REQUIRED_TABLES.add("udfvalue");
      READ_REQUIRED_TABLES.add("schedoptions");
      READ_REQUIRED_TABLES.add("actvtype");
      READ_REQUIRED_TABLES.add("actvcode");
      READ_REQUIRED_TABLES.add("taskactv");
      READ_REQUIRED_TABLES.add("costtype");
      READ_REQUIRED_TABLES.add("account");
      READ_REQUIRED_TABLES.add("projcost");
      READ_REQUIRED_TABLES.add("memotype");
      READ_REQUIRED_TABLES.add("wbsmemo");
      READ_REQUIRED_TABLES.add("taskmemo");
      READ_REQUIRED_TABLES.add("roles");
      READ_REQUIRED_TABLES.add("rolerate");
      READ_REQUIRED_TABLES.add("rsrccurvdata");
      READ_REQUIRED_TABLES.add("taskproc");
      READ_REQUIRED_TABLES.add("location");
      READ_REQUIRED_TABLES.add("umeasure");
      READ_REQUIRED_TABLES.add("shift");
      READ_REQUIRED_TABLES.add("shiftper");
      READ_REQUIRED_TABLES.add("rsrcrole");
      READ_REQUIRED_TABLES.add("pcattype");
      READ_REQUIRED_TABLES.add("pcatval");
      READ_REQUIRED_TABLES.add("projpcat");
      READ_REQUIRED_TABLES.add("rcattype");
      READ_REQUIRED_TABLES.add("rcatval");
      READ_REQUIRED_TABLES.add("rsrcrcat");
      READ_REQUIRED_TABLES.add("rolecattype");
      READ_REQUIRED_TABLES.add("rolecatval");
      READ_REQUIRED_TABLES.add("rolercat");
      READ_REQUIRED_TABLES.add("asgnmntcattype");
      READ_REQUIRED_TABLES.add("asgnmntcatval");
      READ_REQUIRED_TABLES.add("asgnmntacat");
   }

   private static final WbsRowComparatorXER WBS_ROW_COMPARATOR = new WbsRowComparatorXER();
}
