/*
 * file:       XerFile.java
 * author:     Jon Iles
 * date:       2025-11-12
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
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mpxj.DataType;
import org.mpxj.MPXJException;
import org.mpxj.common.LocalDateTimeHelper;
import org.mpxj.common.NumberHelper;
import org.mpxj.common.ReaderTokenizer;
import org.mpxj.common.Tokenizer;

/**
 * Represents data read from an XER file.
 */
class XerFile
{
   /**
    * Constructor.
    *
    * @param requiredTables tables to read from the file
    * @param charset character set to use when reading the file
    * @param ignoreErrors true if errors are ignored when reading
    */
   public XerFile(Set<String> requiredTables, Charset charset, boolean ignoreErrors)
   {
      m_requiredTables = requiredTables;
      m_charset = charset;
      m_ignoreErrors = ignoreErrors;
   }

   /**
    * Reads the XER file table and row structure ready for processing.
    *
    * @param is input stream
    * @return XerFile instance
    */
   public XerFile read(InputStream is) throws MPXJException
   {
      int line = 1;

      m_tables.clear();
      m_numberFormat = new DecimalFormat();
      m_defaultCurrencyData = null;

      try
      {
         InputStreamReader reader = new InputStreamReader(is, m_charset);
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

         return this;
      }

      catch (Exception ex)
      {
         throw new MPXJException(MPXJException.READ_ERROR + " (failed at line " + line + ")", ex);
      }
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
   public List<Row> getRows(String tableName, String columnName, Integer id)
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
    * Retrieve the default currency record.
    *
    * @return default currency record
    */
   public Row getDefaultCurrencyData()
   {
      return m_defaultCurrencyData;
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
    * Extract any useful attributes from the header record.
    *
    * @param record header record
    */
   private void processHeader(List<String> record)
   {
      m_defaultCurrencyName = record.size() > 8 ? record.get(8) : "USD";
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
         fieldType = FIELD_TYPE_MAP.getOrDefault(fieldName, DataType.STRING);
      }
      return fieldType;
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

   private final boolean m_ignoreErrors;
   private final Set<String> m_requiredTables;
   private final Charset m_charset;
   private final DateTimeFormatter m_df = DateTimeFormatter.ofPattern("yyyy-M-dd[ HH[:][.]mm[[:][.]ss]]");
   private final Map<String, List<Row>> m_tables = new HashMap<>();

   private DecimalFormat m_numberFormat;
   private String m_defaultCurrencyName;
   private String m_currentTableName;
   boolean m_skipTable;
   private List<Row> m_currentTable;
   private String[] m_currentFieldNames;
   private Map<String, Integer> m_currentFieldIndex;
   private Row m_defaultCurrencyData;

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
      FIELD_TYPE_MAP.put("est_wt", DataType.NUMERIC);
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
      FIELD_TYPE_MAP.put("step_complete_flag", DataType.BOOLEAN);
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
}
