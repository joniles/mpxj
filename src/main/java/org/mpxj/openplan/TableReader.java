/*
 * file:       TableReader.java
 * author:     Jon Iles
 * date:       2024-02-27
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

package org.mpxj.openplan;

import java.io.PrintWriter;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mpxj.DataType;
import org.mpxj.Duration;
import org.mpxj.ResourceType;
import org.mpxj.TimeUnit;
import org.mpxj.common.DebugLogPrintWriter;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.mpxj.common.LocalDateTimeHelper;

/**
 * Reads a table of data held in a BK3 file.
 */
class TableReader extends AbstractReader
{
   /**
    * Constructor.
    *
    * @param dir parent directory
    * @param tableName table file name
    */
   public TableReader(DirectoryEntry dir, String tableName)
   {
      super(dir, tableName);
      m_name = tableName;
   }

   /**
    * Rda table data as a list of Row instance.
    *
    * @return table data
    */
   public List<Row> read()
   {
      int magic = getInt();
      if (magic != 550)
      {
         throw new OpenPlanException("Unexpected magic number: " + magic);
      }

      int columnCount = getInt();
      String[] columns = new String[columnCount];

      for (int index = 0; index < columnCount; index++)
      {
         columns[index] = getString();
      }

      int rowCount = getInt();
      List<Row> rows = new ArrayList<>(rowCount);

      for (int rowIndex = 0; rowIndex < rowCount; rowIndex++)
      {
         Map<String, Object> map = new HashMap<>();

         for (int columnIndex = 0; columnIndex < columnCount; columnIndex++)
         {
            String columnName = columns[columnIndex];
            String stringValue = getString();
            Object value = convertType(columnName, stringValue);
            if (value != null)
            {
               map.put(columnName, value);
            }
         }

         rows.add(new MapRow(map));
      }

      return log(rows);
   }

   /**
    * Convert a string value into a specific type.
    *
    * @param name column name
    * @param value column value
    * @return typed column value
    */
   private Object convertType(String name, String value)
   {
      if (value == null)
      {
         return null;
      }

      switch (TYPE_MAP.getOrDefault(name, DataType.BINARY))
      {
         case NUMERIC:
         {
            return Double.valueOf(value);
         }

         case INTEGER:
         {
            return Integer.valueOf(value);
         }

         case DATE:
         {
            return LocalDateTimeHelper.parseBest(DATE_FORMAT, value);
         }

         case TIME:
         {
            return LocalTime.parse(value, TIME_FORMAT);
         }

         case BOOLEAN:
         {
            return Boolean.valueOf(value.charAt(0) == 'T');
         }

         case DURATION:
         {
            return parseDuration(value);
         }

         case GUID:
         {
            return UuidHelper.parse(value);
         }

         case RESOURCE_TYPE:
         {
            return parseResourceType(value);
         }

         default:
         {
            return value;
         }
      }
   }

   /**
    * Parse a duration value.
    *
    * @param value string representation of a duration
    * @return duration value
    */
   private Object parseDuration(String value)
   {
      if (value.equals("0"))
      {
         //return Duration.getInstance(0, TimeUnit.HOURS);
         return null; // is this correct?
      }

      Matcher match = DURATION_REGEX.matcher(value);
      if (!match.matches())
      {
         return value;
      }

      int duration = Integer.parseInt(match.group(1));
      TimeUnit unit;

      switch (match.group(2).charAt(0))
      {
         case 'm':
         {
            unit = TimeUnit.MONTHS;
            break;
         }

         case 'w':
         {
            unit = TimeUnit.WEEKS;
            break;
         }

         case 'd':
         {
            unit = TimeUnit.DAYS;
            break;
         }

         case 'h':
         {
            unit = TimeUnit.HOURS;
            break;
         }

         case 't':
         {
            unit = TimeUnit.MINUTES;
            break;
         }

         default:
         {
            return value;
         }
      }

      return Duration.getInstance(duration, unit);
   }

   /**
    * Parse a resource type.
    *
    * @param value string representation of a resource type
    * @return ResourceType instance
    */
   private ResourceType parseResourceType(String value)
   {
      if (value.isEmpty())
      {
         return null;
      }

      switch (value.charAt(0))
      {
         case 'N':
         {
            return ResourceType.MATERIAL;
         }

         case 'C':
         {
            return ResourceType.COST;
         }

         case 'L':
         default:
         {
            return ResourceType.WORK;
         }
      }
   }

   /**
    * Dump table contents to the MPXJ debug log file if configured.
    *
    * @param rows table rows
    * @return list of rows to allow method chaining
    */
   private List<Row> log(List<Row> rows)
   {
      PrintWriter pw = DebugLogPrintWriter.getInstance(true);
      if (pw == null)
      {
         return rows;
      }

      pw.println("TABLE: " + m_name);
      rows.forEach(pw::println);
      pw.println();
      pw.flush();
      pw.close();
      return rows;
   }

   private final String m_name;

   private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("[yyyy-MM-dd'T'HH:mm:ss][yyyyMMddHHmmss'Z'][yyyyMMddHHmm]");

   private static final DateTimeFormatter TIME_FORMAT = new DateTimeFormatterBuilder().parseLenient().appendPattern("HH:mm").toFormatter();

   private static final Pattern DURATION_REGEX = Pattern.compile("(-?\\d+)([mwdht])");
   private static final Map<String, DataType> TYPE_MAP = new HashMap<>();
   static
   {
      // TYPE_MAP.put("OUTOFSEQ", DataType.INTEGER); different type in Project and Activity - both Boolean maybe?
      TYPE_MAP.put("ACT_PROBABILITY", DataType.NUMERIC);
      TYPE_MAP.put("ACT_UID", DataType.GUID);
      TYPE_MAP.put("ACTIVEINDEX", DataType.NUMERIC);
      TYPE_MAP.put("ACWP_LAB", DataType.NUMERIC);
      TYPE_MAP.put("ACWP_MAT", DataType.NUMERIC);
      TYPE_MAP.put("ACWP_ODC", DataType.NUMERIC);
      TYPE_MAP.put("ACWP_QTY", DataType.NUMERIC);
      TYPE_MAP.put("ACWP_SUB", DataType.NUMERIC);
      TYPE_MAP.put("AFDATE", DataType.DATE);
      TYPE_MAP.put("ALT_RES_UID", DataType.GUID);
      TYPE_MAP.put("ASDATE", DataType.DATE);
      TYPE_MAP.put("ASG_UID", DataType.GUID);
      TYPE_MAP.put("AUTOANAL", DataType.INTEGER);
      TYPE_MAP.put("AUTOPROGACT", DataType.INTEGER);
      TYPE_MAP.put("AUTOPROGBASE", DataType.INTEGER);
      TYPE_MAP.put("AUTOPROGCFB", DataType.INTEGER);
      TYPE_MAP.put("AUTOPROGPPC", DataType.INTEGER);
      TYPE_MAP.put("AUTOPROGPSB", DataType.INTEGER);
      TYPE_MAP.put("AUTOPROGRES", DataType.INTEGER);
      TYPE_MAP.put("AUTOPROGTYPE", DataType.INTEGER);
      TYPE_MAP.put("AVL_UID", DataType.GUID);
      TYPE_MAP.put("BAC_LAB", DataType.NUMERIC);
      TYPE_MAP.put("BAC_MAT", DataType.NUMERIC);
      TYPE_MAP.put("BAC_ODC", DataType.NUMERIC);
      TYPE_MAP.put("BAC_QTY", DataType.NUMERIC);
      TYPE_MAP.put("BAC_SUB", DataType.NUMERIC);
      TYPE_MAP.put("BCWP_LAB", DataType.NUMERIC);
      TYPE_MAP.put("BCWP_MAT", DataType.NUMERIC);
      TYPE_MAP.put("BCWP_ODC", DataType.NUMERIC);
      TYPE_MAP.put("BCWP_QTY", DataType.NUMERIC);
      TYPE_MAP.put("BCWP_SUB", DataType.NUMERIC);
      TYPE_MAP.put("BCWS_LAB", DataType.NUMERIC);
      TYPE_MAP.put("BCWS_MAT", DataType.NUMERIC);
      TYPE_MAP.put("BCWS_ODC", DataType.NUMERIC);
      TYPE_MAP.put("BCWS_QTY", DataType.NUMERIC);
      TYPE_MAP.put("BCWS_SUB", DataType.NUMERIC);
      TYPE_MAP.put("BFDATE", DataType.DATE);
      TYPE_MAP.put("BSDATE", DataType.DATE);
      TYPE_MAP.put("CALACTCST", DataType.INTEGER);
      TYPE_MAP.put("CALBUDCST", DataType.INTEGER);
      TYPE_MAP.put("CALCCOSTBASE", DataType.INTEGER);
      TYPE_MAP.put("CALCSTESC", DataType.INTEGER);
      TYPE_MAP.put("CALEVCST", DataType.INTEGER);
      TYPE_MAP.put("CALREMCST", DataType.INTEGER);
      TYPE_MAP.put("CLC_COST", DataType.BOOLEAN);
      TYPE_MAP.put("CLC_PROG", DataType.BOOLEAN);
      TYPE_MAP.put("CLD_UID", DataType.GUID);
      TYPE_MAP.put("CLH_UID", DataType.GUID);
      TYPE_MAP.put("COD_UID", DataType.GUID);
      TYPE_MAP.put("COMPSTAT", DataType.INTEGER);
      TYPE_MAP.put("CRITICAL", DataType.INTEGER);
      TYPE_MAP.put("CRITINDEX", DataType.NUMERIC);
      TYPE_MAP.put("CST_ROLLUP", DataType.INTEGER);
      TYPE_MAP.put("DEFACTDUR", DataType.INTEGER);
      TYPE_MAP.put("DEFENDHR", DataType.INTEGER);
      TYPE_MAP.put("DEFENDMN", DataType.INTEGER);
      TYPE_MAP.put("DEFSTARTHR", DataType.INTEGER);
      TYPE_MAP.put("DEFSTARTMN", DataType.INTEGER);
      TYPE_MAP.put("DELAYRES_UID", DataType.GUID);
      TYPE_MAP.put("DHIGH", DataType.DURATION);
      TYPE_MAP.put("DIR_UID", DataType.GUID);
      TYPE_MAP.put("DLOW", DataType.DURATION);
      TYPE_MAP.put("EFDATE", DataType.DATE);
      TYPE_MAP.put("EFF_FACTOR", DataType.NUMERIC);
      TYPE_MAP.put("ESDATE", DataType.DATE);
      TYPE_MAP.put("ETC_LAB", DataType.NUMERIC);
      TYPE_MAP.put("ETC_MAT", DataType.NUMERIC);
      TYPE_MAP.put("ETC_ODC", DataType.NUMERIC);
      TYPE_MAP.put("ETC_QTY", DataType.NUMERIC);
      TYPE_MAP.put("ETC_SUB", DataType.NUMERIC);
      TYPE_MAP.put("FEDATE", DataType.DATE);
      TYPE_MAP.put("FINFREEFLT", DataType.DURATION);
      TYPE_MAP.put("FINTOTFLT", DataType.DURATION);
      TYPE_MAP.put("FREEFLOAT", DataType.DURATION);
      TYPE_MAP.put("HARDZERO", DataType.INTEGER);
      TYPE_MAP.put("LASTUPDATE", DataType.DATE);
      TYPE_MAP.put("LFDATE", DataType.DATE);
      TYPE_MAP.put("LSDATE", DataType.DATE);
      TYPE_MAP.put("MEAN_EF", DataType.DATE);
      TYPE_MAP.put("MEAN_ES", DataType.DATE);
      TYPE_MAP.put("MEAN_FF", DataType.DURATION);
      TYPE_MAP.put("MEAN_LF", DataType.DATE);
      TYPE_MAP.put("MEAN_LS", DataType.DATE);
      TYPE_MAP.put("MEAN_TF", DataType.DURATION);
      TYPE_MAP.put("MINCALCDU", DataType.INTEGER);
      TYPE_MAP.put("MINSPLITD", DataType.DURATION);
      TYPE_MAP.put("MNPERDAY", DataType.INTEGER);
      TYPE_MAP.put("MNPERMON", DataType.INTEGER);
      TYPE_MAP.put("MNPERWK", DataType.INTEGER);
      TYPE_MAP.put("MSPUNIQUEID", DataType.INTEGER);
      TYPE_MAP.put("MULTIEND", DataType.INTEGER);
      TYPE_MAP.put("NO_LIST", DataType.BOOLEAN);
      TYPE_MAP.put("NRISKSIMULS", DataType.INTEGER);
      TYPE_MAP.put("OPFINISH", DataType.TIME);
      TYPE_MAP.put("OPKEY", DataType.BOOLEAN);
      TYPE_MAP.put("OPSTART", DataType.TIME);
      TYPE_MAP.put("OPWORK", DataType.BOOLEAN);
      TYPE_MAP.put("ORIG_DUR", DataType.DURATION);
      TYPE_MAP.put("PALLOC_UID", DataType.GUID);
      TYPE_MAP.put("PCOMPLETE", DataType.INTEGER);
      TYPE_MAP.put("POSITION_NUM", DataType.INTEGER);
      TYPE_MAP.put("PPC", DataType.NUMERIC);
      TYPE_MAP.put("PRED_ACT_UID", DataType.GUID);
      TYPE_MAP.put("PRJ_FLAG", DataType.INTEGER);
      TYPE_MAP.put("PROGPRIO", DataType.INTEGER);
      TYPE_MAP.put("RDS_UID", DataType.GUID);
      TYPE_MAP.put("REFDATE", DataType.DATE);
      TYPE_MAP.put("REL_FF", DataType.DURATION);
      TYPE_MAP.put("REL_LAG", DataType.DURATION);
      TYPE_MAP.put("REL_PROBABILITY", DataType.NUMERIC);
      TYPE_MAP.put("REL_TF", DataType.DURATION);
      TYPE_MAP.put("REL_UID", DataType.GUID);
      TYPE_MAP.put("REM_DUR", DataType.DURATION);
      TYPE_MAP.put("REMAINING", DataType.NUMERIC);
      TYPE_MAP.put("RES_CLASS", DataType.RESOURCE_TYPE);
      TYPE_MAP.put("RES_CST", DataType.NUMERIC);
      TYPE_MAP.put("RES_DATE", DataType.DATE);
      TYPE_MAP.put("RES_ESC", DataType.NUMERIC);
      TYPE_MAP.put("RES_LEVEL", DataType.NUMERIC);
      TYPE_MAP.put("RES_OFFSET", DataType.DURATION);
      TYPE_MAP.put("RES_PERIOD", DataType.DURATION);
      TYPE_MAP.put("RES_SKL_UID", DataType.GUID);
      TYPE_MAP.put("RES_UID", DataType.GUID);
      TYPE_MAP.put("RES_USED", DataType.NUMERIC);
      TYPE_MAP.put("RFDATE", DataType.DATE);
      TYPE_MAP.put("RISKSEED", DataType.INTEGER);
      TYPE_MAP.put("ROLLCOST", DataType.BOOLEAN);
      TYPE_MAP.put("ROLLUP", DataType.BOOLEAN);
      TYPE_MAP.put("RS_ACTDATE", DataType.INTEGER);
      TYPE_MAP.put("RS_ALTPRTY", DataType.INTEGER);
      TYPE_MAP.put("RS_CONUSE", DataType.INTEGER);
      TYPE_MAP.put("RS_FLOAT", DataType.DURATION);
      TYPE_MAP.put("RS_OVLLATE", DataType.INTEGER);
      TYPE_MAP.put("RS_PRIORTY", DataType.INTEGER);
      TYPE_MAP.put("RS_REPROF", DataType.INTEGER);
      TYPE_MAP.put("RS_SUMDATE", DataType.INTEGER);
      TYPE_MAP.put("RS_SUMMARY", DataType.INTEGER);
      TYPE_MAP.put("RS_SUPRESS", DataType.BOOLEAN);
      TYPE_MAP.put("RSDATE", DataType.DATE);
      TYPE_MAP.put("RSK_CALSD", DataType.INTEGER);
      TYPE_MAP.put("RSL_UID", DataType.GUID);
      TYPE_MAP.put("RSLDATE", DataType.DATE);
      TYPE_MAP.put("SCA_UID", DataType.GUID);
      TYPE_MAP.put("SCHED_DUR", DataType.DURATION);
      TYPE_MAP.put("SCHMETHOD", DataType.INTEGER);
      TYPE_MAP.put("SDEV_EF", DataType.DURATION);
      TYPE_MAP.put("SDEV_ES", DataType.DURATION);
      TYPE_MAP.put("SDEV_FF", DataType.DURATION);
      TYPE_MAP.put("SDEV_LF", DataType.DURATION);
      TYPE_MAP.put("SDEV_LS", DataType.DURATION);
      TYPE_MAP.put("SDEV_TF", DataType.DURATION);
      TYPE_MAP.put("SEP_ASG", DataType.BOOLEAN);
      TYPE_MAP.put("SEQUENCE", DataType.INTEGER);
      TYPE_MAP.put("SFDATE", DataType.DATE);
      TYPE_MAP.put("SMOOTHING", DataType.INTEGER);
      TYPE_MAP.put("SSDATE", DataType.DATE);
      TYPE_MAP.put("SSINDEX", DataType.NUMERIC);
      TYPE_MAP.put("STARTDATE", DataType.DATE);
      TYPE_MAP.put("STARTPC", DataType.INTEGER);
      TYPE_MAP.put("STATDATE", DataType.DATE);
      TYPE_MAP.put("SUBPRJ_UID", DataType.GUID);
      TYPE_MAP.put("SUCC_ACT_UID", DataType.GUID);
      TYPE_MAP.put("SUPPRESS", DataType.BOOLEAN);
      TYPE_MAP.put("TA_BEFORE_RK", DataType.INTEGER);
      TYPE_MAP.put("TA_SUBEND", DataType.INTEGER);
      TYPE_MAP.put("TA_SUMMARY", DataType.INTEGER);
      TYPE_MAP.put("TARGCOST", DataType.NUMERIC);
      TYPE_MAP.put("THRESHOLD", DataType.NUMERIC);
      TYPE_MAP.put("TIMEUNIT", DataType.INTEGER);
      TYPE_MAP.put("TOTACT", DataType.INTEGER);
      TYPE_MAP.put("TOTACTCOM", DataType.INTEGER);
      TYPE_MAP.put("TOTACTPRG", DataType.INTEGER);
      TYPE_MAP.put("TOTALFLOAT", DataType.DURATION);
      TYPE_MAP.put("TOTRELSHP", DataType.INTEGER);
      TYPE_MAP.put("TOTRESO", DataType.INTEGER);
      TYPE_MAP.put("TSDATE", DataType.DATE);
      TYPE_MAP.put("UNIT_COST", DataType.NUMERIC);
      TYPE_MAP.put("USE_UID", DataType.GUID);
      TYPE_MAP.put("USER_DTE01", DataType.DATE);
      TYPE_MAP.put("USER_DTE02", DataType.DATE);
      TYPE_MAP.put("USER_DTE03", DataType.DATE);
      TYPE_MAP.put("USER_DTE04", DataType.DATE);
      TYPE_MAP.put("USER_DTE05", DataType.DATE);
      TYPE_MAP.put("USER_DTE06", DataType.DATE);
      TYPE_MAP.put("USER_DTE07", DataType.DATE);
      TYPE_MAP.put("USER_DTE08", DataType.DATE);
      TYPE_MAP.put("USER_DTE09", DataType.DATE);
      TYPE_MAP.put("USER_DTE10", DataType.DATE);
      TYPE_MAP.put("USER_NUM01", DataType.NUMERIC);
      TYPE_MAP.put("USER_NUM02", DataType.NUMERIC);
      TYPE_MAP.put("USER_NUM03", DataType.NUMERIC);
      TYPE_MAP.put("USER_NUM04", DataType.NUMERIC);
      TYPE_MAP.put("USER_NUM05", DataType.NUMERIC);
      TYPE_MAP.put("USER_NUM06", DataType.NUMERIC);
      TYPE_MAP.put("USER_NUM07", DataType.NUMERIC);
      TYPE_MAP.put("USER_NUM08", DataType.NUMERIC);
      TYPE_MAP.put("USER_NUM09", DataType.NUMERIC);
      TYPE_MAP.put("USER_NUM10", DataType.NUMERIC);
   }
}
