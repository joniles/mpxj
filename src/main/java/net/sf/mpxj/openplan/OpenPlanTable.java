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
import java.util.regex.Pattern;

import net.sf.mpxj.DataType;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.DocumentInputStream;

class OpenPlanTable
{
   public OpenPlanTable(DirectoryEntry dir, String tableName) throws IOException
   {
      m_is = new DocumentInputStream((DocumentEntry) dir.getEntry(tableName));
   }

   public List<Row> read() throws IOException
   {
      int magic = getInt();
      int columnCount = getInt();
      String[] columns = new String[columnCount];

      for (int index = 0; index < columnCount; index++)
      {
         int length = m_is.read();
         byte[] nameBytes = new byte[length];
         if (m_is.read(nameBytes) != length)
         {
            throw new IOException("Failed to read expected number of bytes");
         }

         columns[index] = new String(nameBytes);
      }

      int rowCount = getInt();
      List<Row> rows = new ArrayList<>(rowCount);

      for (int rowIndex = 0; rowIndex < rowCount; rowIndex++)
      {
         Map<String, Object> map = new HashMap<>();

         for (int columnIndex=0; columnIndex < columnCount; columnIndex++)
         {
            int byteCount = m_is.read();
            byte[] bytes = new byte[byteCount];
            if (m_is.read(bytes) != byteCount)
            {
               throw new IOException("Failed to read expected number of bytes");
            }

            String columnName = columns[columnIndex];
            Object value =  convertType(columnName, bytes);
            if (value != null)
            {
               map.put(columnName, value);
            }
         }

         rows.add(new MapRow(map));
      }

      return rows;
   }

   private int getInt() throws IOException
   {
      int result = 0;
      for (int shiftBy = 0; shiftBy < 32; shiftBy += 8)
      {
         result |= ((m_is.read() & 0xff)) << shiftBy;
      }
      return result;
   }

   private Object convertType(String name, byte[] bytes)
   {
      if (bytes == null || bytes.length == 0)
      {
         return null;
      }

      String value = new String(bytes);

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

         default:
         {
            return value;
         }
      }
   }

   private final InputStream m_is;

   private static final DateTimeFormatter DATE_FORMAT = new DateTimeFormatterBuilder().parseLenient().appendPattern("yyyyMMddHHmm").toFormatter();

   private static final Pattern DURATION_REGEX = Pattern.compile("(\\d+)([dh])");
   private static final Map<String, DataType> TYPE_MAP = new HashMap<>();
   static
   {
      TYPE_MAP.put("ACT_ID", DataType.STRING);
      TYPE_MAP.put("ACT_PROBABILITY", DataType.NUMERIC);
      TYPE_MAP.put("ACT_TYPE", DataType.STRING);
      TYPE_MAP.put("ACT_UID", DataType.BINARY);
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
      TYPE_MAP.put("C1", DataType.BINARY);
      TYPE_MAP.put("C2", DataType.BINARY);
      TYPE_MAP.put("CLH_ID", DataType.STRING);
      TYPE_MAP.put("CLH_UID", DataType.BINARY);
      TYPE_MAP.put("COMP_RS_C", DataType.STRING);
      TYPE_MAP.put("COMPSTAT", DataType.STRING);
      TYPE_MAP.put("CRITICAL", DataType.STRING);
      TYPE_MAP.put("CRITINDEX", DataType.NUMERIC);
      TYPE_MAP.put("DELAYRES_ID", DataType.BINARY);
      TYPE_MAP.put("DELAYRES_UID", DataType.BINARY);
      TYPE_MAP.put("DESCRIPTION", DataType.STRING);
      TYPE_MAP.put("DHIGH", DataType.DURATION);
      TYPE_MAP.put("DIR_ID", DataType.STRING);
      TYPE_MAP.put("DIR_UID", DataType.BINARY);
      TYPE_MAP.put("DLOW", DataType.DURATION);
      TYPE_MAP.put("DSHAPE", DataType.STRING);
      TYPE_MAP.put("EFDATE", DataType.DATE);
      TYPE_MAP.put("ESDATE", DataType.DATE);
      TYPE_MAP.put("ETC_LAB", DataType.NUMERIC);
      TYPE_MAP.put("ETC_MAT", DataType.NUMERIC);
      TYPE_MAP.put("ETC_ODC", DataType.NUMERIC);
      TYPE_MAP.put("ETC_QTY", DataType.NUMERIC);
      TYPE_MAP.put("ETC_SUB", DataType.NUMERIC);
      TYPE_MAP.put("EVT", DataType.STRING);
      TYPE_MAP.put("FEDATE", DataType.DATE);
      TYPE_MAP.put("FINFREEFLT", DataType.DURATION);
      TYPE_MAP.put("FINTOTFLT", DataType.DURATION);
      TYPE_MAP.put("FREEFLOAT", DataType.DURATION);
      TYPE_MAP.put("LASTUPDATE", DataType.DATE);
      TYPE_MAP.put("LFDATE", DataType.DATE);
      TYPE_MAP.put("LOGICFLAG", DataType.STRING);
      TYPE_MAP.put("LSDATE", DataType.DATE);
      TYPE_MAP.put("MAXDUR", DataType.BINARY);
      TYPE_MAP.put("MAXSPLITS", DataType.BINARY);
      TYPE_MAP.put("MEAN_EF", DataType.DATE);
      TYPE_MAP.put("MEAN_ES", DataType.DATE);
      TYPE_MAP.put("MEAN_FF", DataType.DURATION);
      TYPE_MAP.put("MEAN_LF", DataType.DATE);
      TYPE_MAP.put("MEAN_LS", DataType.DATE);
      TYPE_MAP.put("MEAN_TF", DataType.DURATION);
      TYPE_MAP.put("MINSPLITD", DataType.BINARY);
      TYPE_MAP.put("MSPUNIQUEID", DataType.BINARY);
      TYPE_MAP.put("OPKEY", DataType.BOOLEAN);
      TYPE_MAP.put("ORIG_DUR", DataType.DURATION);
      TYPE_MAP.put("OUTOFSEQ", DataType.BOOLEAN);
      TYPE_MAP.put("PPC", DataType.NUMERIC);
      TYPE_MAP.put("PRIORITY", DataType.BINARY);
      TYPE_MAP.put("PROGTYPE", DataType.BINARY);
      TYPE_MAP.put("PROGVALUE", DataType.BINARY);
      TYPE_MAP.put("PWAGUID", DataType.BINARY);
      TYPE_MAP.put("REM_DUR", DataType.DURATION);
      TYPE_MAP.put("RES_DATE", DataType.DATE);
      TYPE_MAP.put("RS_FLOAT", DataType.DURATION);
      TYPE_MAP.put("RS_SUPRESS", DataType.BOOLEAN);
      TYPE_MAP.put("RSCLASS", DataType.BINARY);
      TYPE_MAP.put("SCHED_DUR", DataType.DURATION);
      TYPE_MAP.put("SDEV_EF", DataType.DURATION);
      TYPE_MAP.put("SDEV_ES", DataType.DURATION);
      TYPE_MAP.put("SDEV_FF", DataType.DURATION);
      TYPE_MAP.put("SDEV_LF", DataType.DURATION);
      TYPE_MAP.put("SDEV_LS", DataType.DURATION);
      TYPE_MAP.put("SDEV_TF", DataType.DURATION);
      TYPE_MAP.put("SEP_ASG", DataType.BINARY);
      TYPE_MAP.put("SEQUENCE", DataType.INTEGER);
      TYPE_MAP.put("SFDATE", DataType.DATE);
      TYPE_MAP.put("SOURCE_BASELINE", DataType.STRING);
      TYPE_MAP.put("SSDATE", DataType.DATE);
      TYPE_MAP.put("SSINDEX", DataType.NUMERIC);
      TYPE_MAP.put("STARTPC", DataType.BINARY);
      TYPE_MAP.put("TARGFTYPE", DataType.BINARY);
      TYPE_MAP.put("TARGSTYPE", DataType.BINARY);
      TYPE_MAP.put("TFDATE", DataType.BINARY);
      TYPE_MAP.put("TOTALFLOAT", DataType.DURATION);
      TYPE_MAP.put("TSDATE", DataType.BINARY);
      TYPE_MAP.put("USER_CHR01", DataType.STRING);
      TYPE_MAP.put("USER_CHR02", DataType.STRING);
      TYPE_MAP.put("USER_CHR03", DataType.STRING);
      TYPE_MAP.put("USER_CHR04", DataType.STRING);
      TYPE_MAP.put("USER_CHR05", DataType.STRING);
      TYPE_MAP.put("USER_CHR06", DataType.STRING);
      TYPE_MAP.put("USER_CHR07", DataType.STRING);
      TYPE_MAP.put("USER_CHR08", DataType.STRING);
      TYPE_MAP.put("USER_CHR09", DataType.STRING);
      TYPE_MAP.put("USER_CHR10", DataType.STRING);
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
      TYPE_MAP.put("USR_ID", DataType.STRING);
      TYPE_MAP.put("XFDATE", DataType.BINARY);
   }
}
