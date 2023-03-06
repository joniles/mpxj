package net.sf.mpxj.primavera;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.sf.mpxj.Availability;
import net.sf.mpxj.CostRateTableEntry;
import net.sf.mpxj.FieldContainer;
import net.sf.mpxj.FieldType;
import net.sf.mpxj.HtmlNotes;
import net.sf.mpxj.Notes;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Rate;
import net.sf.mpxj.Resource;
import net.sf.mpxj.ResourceField;
import net.sf.mpxj.ResourceType;
import net.sf.mpxj.common.CharsetHelper;
import net.sf.mpxj.common.HtmlHelper;
import net.sf.mpxj.common.NumberHelper;
import net.sf.mpxj.writer.AbstractProjectWriter;

public class PrimaveraXERFileWriter extends AbstractProjectWriter
{
   /**
    * Sets the character encoding used when writing an XER file.
    *
    * @param encoding encoding name
    */
   public void setEncoding(String encoding)
   {
      m_encoding = encoding;
   }

   /**
    * Alternative way to set the file encoding. If both an encoding name and a Charset instance
    * are supplied, the Charset instance is used.
    *
    * @param charset Charset used when writing the file
    */
   public void setCharset(Charset charset)
   {
      m_charset = charset;
   }

   @Override public void write(ProjectFile projectFile, OutputStream outputStream) throws IOException
   {
      m_file = projectFile;
      m_writer = new OutputStreamWriter(outputStream, getCharset());
      m_roleRateUniqueID = 1;

      try
      {
         writeHeader();
         writeCurrencies();
         writeRoles();
         writeRoleRates();
         writeResources();
         m_writer.flush();
      }

      finally
      {
         m_writer = null;
      }
   }

   /**
    * Retrieve the Charset used to write the file.
    *
    * @return Charset instance
    */
   private Charset getCharset()
   {
      Charset result = m_charset;
      if (result == null)
      {
         // We default to CP1252 as this seems to be the most common encoding
         result = m_encoding == null ? CharsetHelper.CP1252 : Charset.forName(m_encoding);
      }
      return result;
   }

   private void writeHeader()
   {
      Object[] data = {
         "ERMHDR",
         "20.12",
         formatDate(new Date()),
         "Project",
         "admin",
         "admin",
         "dbxDatabaseNoName",
         "Project Management",
         DEFAULT_CURRENCY.get("curr_short_name")
      };

      try
      {
         m_writer.write(Arrays.stream(data).map(this::format).collect(Collectors.joining("\t")));
         m_writer.write("\n");
      }

      catch (IOException ex)
      {
         throw new RuntimeException(ex);
      }
   }

   private void writeCurrencies()
   {
      writeTable("CURRTYPE", CURRENCY_COLUMNS);
      writeRecord(CURRENCY_COLUMNS, DEFAULT_CURRENCY);
   }

   private void writeRoles()
   {
      writeTable("ROLES", ROLE_COLUMNS);
      m_file.getResources().stream().filter(Resource::getRole).sorted(Comparator.comparing(Resource::getUniqueID)).forEach(r -> writeRecord(ROLE_COLUMNS, r));
   }

   private void writeRoleRates()
   {
      writeTable("ROLERATE", ROLE_RATE_COLUMNS);
      m_file.getResources().stream().filter(Resource::getRole).sorted(Comparator.comparing(Resource::getUniqueID)).forEach(this::writeRoleRates);
   }

   private void writeRoleRates(Resource resource)
   {
      resource.getCostRateTable(0).stream().filter(e -> e != CostRateTableEntry.DEFAULT_ENTRY).forEach(e -> writeRoleRates(resource, e));
   }

   private void writeRoleRates(Resource resource, CostRateTableEntry entry)
   {
      writeRecord(ROLE_RATE_COLUMNS.values().stream().map(f -> f.apply(this, resource, entry)));
   }

   private void writeResources()
   {
      writeTable("RSRC", RESOURCE_COLUMNS);
      m_file.getResources().stream().filter(r -> !r.getRole().booleanValue()).sorted(Comparator.comparing(Resource::getUniqueID)).forEach(r -> writeRecord(RESOURCE_COLUMNS, r));
   }

   private void writeTable(String name, String[] columns)
   {
      try
      {
         m_writer.write("%T\t" + name + "\n");
         m_writer.write("%F\t");
         m_writer.write(String.join("\t", columns));
         m_writer.write("\n");
      }

      catch (IOException ex)
      {
         throw new RuntimeException(ex);
      }
   }

   private void writeTable(String name, Map<String, ?> map)
   {
      try
      {
         m_writer.write("%T\t" + name + "\n");
         m_writer.write("%F\t");
         m_writer.write(String.join("\t", map.keySet()));
         m_writer.write("\n");
      }

      catch (IOException ex)
      {
         throw new RuntimeException(ex);
      }
   }

   private void writeRecord(String[] columns, Map<String, Object> data)
   {
      writeRecord(Arrays.stream(columns).map(c -> format(data.get(c))));
   }

   private void writeRecord(Stream<Object> data)
   {
      try
      {
         m_writer.write("%R\t");
         m_writer.write(data.map(this::format).collect(Collectors.joining("\t")));
         m_writer.write("\n");
      }

      catch (IOException ex)
      {
         throw new RuntimeException(ex);
      }
   }

   private void writeRecord(Map<String, Object> columns, FieldContainer container)
   {
      try
      {
         m_writer.write("%R\t");
         m_writer.write(columns.values().stream().map(c -> format(getData(container, c))).collect(Collectors.joining("\t")));
         m_writer.write("\n");
      }

      catch (IOException ex)
      {
         throw new RuntimeException(ex);
      }
   }

   private Object getData(FieldContainer container, Object key)
   {
      if (key instanceof FieldType)
      {
         return container.get((FieldType) key);
      }

      if (key instanceof ExportFunction)
      {
         return ((ExportFunction)key).apply(container);
      }

      return key;
   }

   private String format(Object object)
   {
      // TODO: do we need to implement type-specific defaults?
      if (object == null)
      {
         return "";
      }

      if (object instanceof Date)
      {
         return m_timestampFormat.format(object);
      }

      if (object instanceof Double)
      {
         return m_doubleFormat.format(object);
      }

      if (object instanceof Boolean)
      {
         return ((Boolean) object).booleanValue() ? "Y" : "N";
      }

      if (object instanceof Notes)
      {
         return formatNotes((Notes) object);
      }

      if (object instanceof Rate)
      {
         return formatRate((Rate) object);
      }

      if (object instanceof UUID)
      {
         return formatUUID((UUID)object);
      }

      if (object instanceof ResourceType)
      {
         return formatResourceType((ResourceType)object);
      }

      return object.toString();
   }

   private String formatDate(Date date)
   {
      return m_dateFormat.format(date);
   }

   private String formatNotes(Notes notes)
   {
      String result;
      if (notes == null || notes.isEmpty())
      {
         // TODO: switch to null to remove the tag - check import
         result = "";
      }
      else
      {
         result = notes instanceof HtmlNotes ? ((HtmlNotes) notes).getHtml() : HtmlHelper.getHtmlFromPlainText(notes.toString());
         result = result.replace("\n", "\u007F\u007F");
      }

      return result;
   }

   private String formatRate(Rate rate)
   {
      return m_rateFormat.format(rate.getAmount());
   }

   private String formatUUID(UUID value)
   {
      byte[] data = new byte[16];
      long lsb = value.getLeastSignificantBits();
      long msb = value.getMostSignificantBits();

      data[15] = (byte)(lsb & 0xff);
      data[14] = (byte)(lsb >> 8 & 0xff);
      data[13] = (byte)(lsb >> 16 & 0xff);
      data[12] = (byte)(lsb >> 24 & 0xff);
      data[11] = (byte)(lsb >> 32 & 0xff);
      data[10] = (byte)(lsb >> 40 & 0xff);
      data[9] = (byte)(lsb >> 48 & 0xff);
      data[8] = (byte)(lsb >> 56 & 0xff);

      data[6] = (byte)(msb & 0xff);
      data[7] = (byte)(msb >> 8 & 0xff);
      data[4] = (byte)(msb >> 16 & 0xff);
      data[5] = (byte)(msb >> 24 & 0xff);
      data[0] = (byte)(msb >> 32 & 0xff);
      data[1] = (byte)(msb >> 40 & 0xff);
      data[2] = (byte)(msb >> 48 & 0xff);
      data[3] = (byte)(msb >> 56 & 0xff);

      String result = javax.xml.bind.DatatypeConverter.printBase64Binary(data);

      return result.substring(0, result.length()-2);
   }

   private String formatResourceType(ResourceType type)
   {
      return RESOURCE_TYPE_MAP.get(type);
   }

   private String getMaxQuantityPerHour(Resource resource, CostRateTableEntry entry)
   {
      Availability availability = resource.getAvailability().getEntryByDate(entry.getStartDate());
      if (availability == null)
      {
         return "0";
      }

      return m_maxUnitsFormat.format(NumberHelper.getDouble(availability.getUnits()) / 100.0);
   }

   private String m_encoding;
   private Charset m_charset;
   private ProjectFile m_file;
   private OutputStreamWriter m_writer;

   private int m_roleRateUniqueID;

   private final Format m_dateFormat = new SimpleDateFormat("yyyy-MM-dd");

   private final Format m_timestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

   private final DecimalFormat m_rateFormat = new DecimalFormat("0.0000");

   private final DecimalFormat m_maxUnitsFormat = new DecimalFormat("0.####");

   private final DecimalFormat m_doubleFormat = new DecimalFormat("0.####");

   private static final Map<ResourceType, String> RESOURCE_TYPE_MAP = new HashMap<>();
   static
   {
      RESOURCE_TYPE_MAP.put(ResourceType.WORK, "RT_Labor");
      RESOURCE_TYPE_MAP.put(ResourceType.MATERIAL, "RT_Mat");
      RESOURCE_TYPE_MAP.put(ResourceType.COST, "RT_Equip");
   }

   private static final String[] CURRENCY_COLUMNS = {
      "curr_id",
      "decimal_digit_cnt",
      "curr_symbol",
      "decimal_symbol",
      "digit_group_symbol",
      "pos_curr_fmt_type",
      "neg_curr_fmt_type",
      "curr_type",
      "curr_short_name",
      "group_digit_cnt",
      "base_exch_rate"
   };

   private static final Map<String, Object> DEFAULT_CURRENCY = new HashMap<>();
   static
   {
      DEFAULT_CURRENCY.put("curr_id", "1");
      DEFAULT_CURRENCY.put("decimal_digit_cnt", "2");
      DEFAULT_CURRENCY.put("curr_symbol", "$");
      DEFAULT_CURRENCY.put("decimal_symbol", ".");
      DEFAULT_CURRENCY.put("digit_group_symbol", ",");
      DEFAULT_CURRENCY.put("pos_curr_fmt_type", "#1.1");
      DEFAULT_CURRENCY.put("neg_curr_fmt_type", "(#1.1)");
      DEFAULT_CURRENCY.put("curr_type", "US Dollar");
      DEFAULT_CURRENCY.put("curr_short_name", "USD");
      DEFAULT_CURRENCY.put("group_digit_cnt", "3");
      DEFAULT_CURRENCY.put("base_exch_rate", "1");
   }

   private static final Map<String, Object> ROLE_COLUMNS = new LinkedHashMap<>();
   static
   {
      ROLE_COLUMNS.put("role_id", ResourceField.UNIQUE_ID);
      ROLE_COLUMNS.put("parent_role_id", ResourceField.PARENT_ID);
      ROLE_COLUMNS.put("seq_num", ResourceField.SEQUENCE_NUMBER);
      ROLE_COLUMNS.put("role_name", ResourceField.NAME);
      ROLE_COLUMNS.put("role_short_name", ResourceField.RESOURCE_ID);
      ROLE_COLUMNS.put("pobs_id", "");
      ROLE_COLUMNS.put("def_cost_qty_link_flag", ResourceField.CALCULATE_COSTS_FROM_UNITS);
      ROLE_COLUMNS.put("cost_qty_type", "QT_Hour");
      ROLE_COLUMNS.put("role_descr", ResourceField.NOTES);
      ROLE_COLUMNS.put("last_checksum", "");
   }

   private interface RoleRateFunction
   {
      Object apply(PrimaveraXERFileWriter writer, Resource resource, CostRateTableEntry entry);
   }

   private interface ExportFunction
   {
      Object apply(Object source);
   }

   private static final Map<String, RoleRateFunction> ROLE_RATE_COLUMNS = new LinkedHashMap<>();
   static
   {
      ROLE_RATE_COLUMNS.put("role_rate_id", (PrimaveraXERFileWriter w, Resource r, CostRateTableEntry e) -> Integer.valueOf(w.m_roleRateUniqueID++));
      ROLE_RATE_COLUMNS.put("role_id", (PrimaveraXERFileWriter w, Resource r, CostRateTableEntry e) -> r.getUniqueID());
      ROLE_RATE_COLUMNS.put("cost_per_qty", (PrimaveraXERFileWriter w, Resource r, CostRateTableEntry e) -> e.getRate(0));
      ROLE_RATE_COLUMNS.put("cost_per_qty2", (PrimaveraXERFileWriter w, Resource r, CostRateTableEntry e) -> e.getRate(1));
      ROLE_RATE_COLUMNS.put("cost_per_qty3", (PrimaveraXERFileWriter w, Resource r, CostRateTableEntry e) -> e.getRate(2));
      ROLE_RATE_COLUMNS.put("cost_per_qty4", (PrimaveraXERFileWriter w, Resource r, CostRateTableEntry e) -> e.getRate(3));
      ROLE_RATE_COLUMNS.put("cost_per_qty5", (PrimaveraXERFileWriter w, Resource r, CostRateTableEntry e) -> e.getRate(4));
      ROLE_RATE_COLUMNS.put("start_date", (PrimaveraXERFileWriter w, Resource r, CostRateTableEntry e) -> e.getStartDate());
      ROLE_RATE_COLUMNS.put("max_qty_per_hr", PrimaveraXERFileWriter::getMaxQuantityPerHour);
   }

   private static final Map<String, Object> RESOURCE_COLUMNS = new LinkedHashMap<>();
   static
   {
      RESOURCE_COLUMNS.put("rsrc_id", ResourceField.UNIQUE_ID);
      RESOURCE_COLUMNS.put("parent_rsrc_id", ResourceField.PARENT_ID);
      RESOURCE_COLUMNS.put("clndr_id", ResourceField.CALENDAR_UNIQUE_ID);
      RESOURCE_COLUMNS.put("role_id", "");
      RESOURCE_COLUMNS.put("shift_id", "");
      RESOURCE_COLUMNS.put("user_id", "");
      RESOURCE_COLUMNS.put("pobs_id", "");
      RESOURCE_COLUMNS.put("guid", ResourceField.GUID);
      RESOURCE_COLUMNS.put("rsrc_seq_num", ResourceField.SEQUENCE_NUMBER);
      RESOURCE_COLUMNS.put("email_addr", ResourceField.EMAIL_ADDRESS);
      RESOURCE_COLUMNS.put("employee_code", ResourceField.CODE);
      RESOURCE_COLUMNS.put("office_phone", "");
      RESOURCE_COLUMNS.put("other_phone", "");
      RESOURCE_COLUMNS.put("rsrc_name", ResourceField.NAME);
      RESOURCE_COLUMNS.put("rsrc_short_name", ResourceField.RESOURCE_ID);
      RESOURCE_COLUMNS.put("rsrc_title_name", "");
      RESOURCE_COLUMNS.put("def_qty_per_hr", (ExportFunction)(r) -> ((Resource)r).getMaxUnits() == null ? null : ((Resource)r).getMaxUnits().doubleValue() / 100.0);
      RESOURCE_COLUMNS.put("cost_qty_type", "QT_Hour");
      RESOURCE_COLUMNS.put("ot_factor", "");
      RESOURCE_COLUMNS.put("active_flag", ResourceField.ACTIVE);
      RESOURCE_COLUMNS.put("auto_compute_act_flag", Boolean.TRUE);
      RESOURCE_COLUMNS.put("def_cost_qty_link_flag", ResourceField.CALCULATE_COSTS_FROM_UNITS);
      RESOURCE_COLUMNS.put("ot_flag", Boolean.FALSE);
      RESOURCE_COLUMNS.put("curr_id", DEFAULT_CURRENCY.get("curr_id"));
      RESOURCE_COLUMNS.put("unit_id", "");
      RESOURCE_COLUMNS.put("rsrc_type", ResourceField.TYPE);
      RESOURCE_COLUMNS.put("location_id", "");
      RESOURCE_COLUMNS.put("rsrc_notes", ResourceField.NOTES);
      RESOURCE_COLUMNS.put("load_tasks_flag", "");
      RESOURCE_COLUMNS.put("level_flag", "");
      RESOURCE_COLUMNS.put("last_checksum", "");
   }
}
