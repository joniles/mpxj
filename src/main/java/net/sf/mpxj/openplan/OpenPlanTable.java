package net.sf.mpxj.openplan;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.mpxj.DataType;
import net.sf.mpxj.Duration;
import net.sf.mpxj.TimeUnit;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.DocumentInputStream;

class OpenPlanTable
{
   public OpenPlanTable(DirectoryEntry dir, String tableName)
   {
      try
      {
         m_is = new DocumentInputStream((DocumentEntry) dir.getEntry(tableName));
      }
      catch (IOException e)
      {
         throw new OpenPlanException(e);
      }
   }

   public List<Row> read()
   {
      int magic = getInt();
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
            Object value = convertType(columnName, getString());
            if (value != null)
            {
               map.put(columnName, value);
            }
         }

         rows.add(new MapRow(map));
      }

      return rows;
   }

   private int getInt()
   {
      try
      {
         int result = 0;
         for (int shiftBy = 0; shiftBy < 32; shiftBy += 8)
         {
            result |= ((m_is.read() & 0xff)) << shiftBy;
         }
         return result;
      }
      catch (IOException ex)
      {
         throw new OpenPlanException(ex);
      }
   }


   private int getByte()
   {
      try
      {
         return m_is.read();
      }
      catch (IOException ex)
      {
         throw new OpenPlanException(ex);
      }
   }

   private String getString()
   {
      try
      {
         int length = getByte();
         if (length == 0)
         {
            return null;
         }

         byte[] bytes = new byte[length];
         if (m_is.read(bytes) != length)
         {
            throw new OpenPlanException("Failed to read expected number of bytes");
         }

         return new String(bytes);
      }

      catch (IOException ex)
      {
         throw new OpenPlanException(ex);
      }
   }

   private Object convertType(String name, String value)
   {
      if (value == null)
      {
         return null;
      }
      
      switch(TYPE_MAP.getOrDefault(name, DataType.BINARY))
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
            // TODO: could improve optional section parsing
            return LocalDateTime.parse(value.substring(0,12), DATE_FORMAT);
         }

         case BOOLEAN:
         {
            return Boolean.valueOf(value.charAt(0) == 'T');
         }

         case DURATION:
         {
            return parseDuration(value);
         }

         default:
         {
            return value;
         }
      }
   }

   private Object parseDuration(String value)
   {
      if (value.equals("0"))
      {
         return Duration.getInstance(0, TimeUnit.HOURS);
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
         case 'h':
         {
            unit = TimeUnit.HOURS;
            break;
         }

         case 'd':
         {
            unit = TimeUnit.DAYS;
            break;
         }

         default:
         {
            return value;
         }
      }

      return Duration.getInstance(duration, unit);
   }

   private final InputStream m_is;

   private static final DateTimeFormatter DATE_FORMAT = new DateTimeFormatterBuilder().parseLenient().appendPattern("yyyyMMddHHmm").toFormatter();

   private static final Pattern DURATION_REGEX = Pattern.compile("(\\d+)([dh])");
   private static final Map<String, DataType> TYPE_MAP = new HashMap<>();
   static
   {
      TYPE_MAP.put("ACT_PROBABILITY", DataType.NUMERIC);
      TYPE_MAP.put("ACTIVEINDEX", DataType.NUMERIC);
      TYPE_MAP.put("ACWP_LAB", DataType.NUMERIC);
      TYPE_MAP.put("ACWP_MAT", DataType.NUMERIC);
      TYPE_MAP.put("ACWP_ODC", DataType.NUMERIC);
      TYPE_MAP.put("ACWP_QTY", DataType.NUMERIC);
      TYPE_MAP.put("ACWP_SUB", DataType.NUMERIC);
      TYPE_MAP.put("AFDATE", DataType.DATE);
      TYPE_MAP.put("ASDATE", DataType.DATE);
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
      TYPE_MAP.put("CRITINDEX", DataType.NUMERIC);
      TYPE_MAP.put("DHIGH", DataType.DURATION);
      TYPE_MAP.put("DLOW", DataType.DURATION);
      TYPE_MAP.put("EFDATE", DataType.DATE);
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
      TYPE_MAP.put("LASTUPDATE", DataType.DATE);
      TYPE_MAP.put("LFDATE", DataType.DATE);
      TYPE_MAP.put("LSDATE", DataType.DATE);
      TYPE_MAP.put("MEAN_EF", DataType.DATE);
      TYPE_MAP.put("MEAN_ES", DataType.DATE);
      TYPE_MAP.put("MEAN_FF", DataType.DURATION);
      TYPE_MAP.put("MEAN_LF", DataType.DATE);
      TYPE_MAP.put("MEAN_LS", DataType.DATE);
      TYPE_MAP.put("MEAN_TF", DataType.DURATION);
      TYPE_MAP.put("OPKEY", DataType.BOOLEAN);
      TYPE_MAP.put("ORIG_DUR", DataType.DURATION);
      TYPE_MAP.put("OUTOFSEQ", DataType.BOOLEAN);
      TYPE_MAP.put("PPC", DataType.NUMERIC);
      TYPE_MAP.put("REM_DUR", DataType.DURATION);
      TYPE_MAP.put("RES_DATE", DataType.DATE);
      TYPE_MAP.put("RS_FLOAT", DataType.DURATION);
      TYPE_MAP.put("RS_SUPRESS", DataType.BOOLEAN);
      TYPE_MAP.put("SCHED_DUR", DataType.DURATION);
      TYPE_MAP.put("SDEV_EF", DataType.DURATION);
      TYPE_MAP.put("SDEV_ES", DataType.DURATION);
      TYPE_MAP.put("SDEV_FF", DataType.DURATION);
      TYPE_MAP.put("SDEV_LF", DataType.DURATION);
      TYPE_MAP.put("SDEV_LS", DataType.DURATION);
      TYPE_MAP.put("SDEV_TF", DataType.DURATION);
      TYPE_MAP.put("SEQUENCE", DataType.INTEGER);
      TYPE_MAP.put("SFDATE", DataType.DATE);
      TYPE_MAP.put("SSDATE", DataType.DATE);
      TYPE_MAP.put("SSINDEX", DataType.NUMERIC);
      TYPE_MAP.put("TOTALFLOAT", DataType.DURATION);
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

      TYPE_MAP.put("MNPERDAY", DataType.INTEGER);
      TYPE_MAP.put("TOTACT", DataType.INTEGER);
      TYPE_MAP.put("TARGCOST", DataType.NUMERIC);
      TYPE_MAP.put("MNPERWK", DataType.INTEGER);
      TYPE_MAP.put("TOTRELSHP", DataType.INTEGER);
      TYPE_MAP.put("TOTRESO", DataType.INTEGER);
      TYPE_MAP.put("REFDATE", DataType.DATE);
      TYPE_MAP.put("MNPERMON", DataType.INTEGER);

      TYPE_MAP.put("RSDATE", DataType.DATE);
      TYPE_MAP.put("RFDATE", DataType.DATE);
      TYPE_MAP.put("RES_ESC", DataType.NUMERIC);
      TYPE_MAP.put("RES_USED", DataType.NUMERIC);
      TYPE_MAP.put("RES_CST", DataType.NUMERIC);
   }
}
