package net.sf.mpxj.primavera;
import java.awt.Color;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.sf.mpxj.AccrueType;
import net.sf.mpxj.ActivityCodeScope;
import net.sf.mpxj.ActivityStatus;
import net.sf.mpxj.ActivityType;
import net.sf.mpxj.CalendarType;
import net.sf.mpxj.ConstraintType;
import net.sf.mpxj.CriticalActivityType;
import net.sf.mpxj.DataType;
import net.sf.mpxj.Duration;
import net.sf.mpxj.HtmlNotes;
import net.sf.mpxj.Notes;
import net.sf.mpxj.PercentCompleteType;
import net.sf.mpxj.Priority;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Rate;
import net.sf.mpxj.RateSource;
import net.sf.mpxj.RelationType;
import net.sf.mpxj.ResourceType;
import net.sf.mpxj.TaskType;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.common.ColorHelper;
import net.sf.mpxj.common.HtmlHelper;
import net.sf.mpxj.common.NumberHelper;

final class XerWriter
{
   public XerWriter(ProjectFile file, OutputStreamWriter writer)
   {
      m_file = file;
      m_writer = writer;
   }

   public void writeHeader(Object[] data)
   {
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

   public void writeTable(String name, Map<String, ?> map)
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

   public <T> void writeRecord(Map<String, PrimaveraXERFileWriter.ExportFunction<T>> columns, T object)
   {
      writeRecord(columns.values().stream().map(f -> f.apply(object)));
   }

   public void writeRecord(Stream<Object> data)
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

   public void writeTrailer()
   {
      try
      {
         m_writer.write("%E\n");
      }

      catch (IOException ex)
      {
         throw new RuntimeException(ex);
      }
   }

   public void flush() throws IOException
   {
      m_writer.flush();
   }

   private String format(Object object)
   {
      if (object == null)
      {
         return "";
      }

      // Handle objects which may be subclasses
      if (object instanceof Notes)
      {
         return formatNotes((Notes) object);
      }

      // Handle objects we can identify by class name
      FormatFunction f = FORMAT_MAP.get(object.getClass());

      return f == null ? object.toString() : f.apply(this, object);
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

   private String formatDuration(Duration duration)
   {
      if (duration == null)
      {
         return "";
      }

      return m_doubleFormat.format(duration.convertUnits(TimeUnit.HOURS, m_file.getProjectProperties()).getDuration());
   }

   private interface FormatFunction
   {
      String apply(XerWriter writer, Object source);
   }

   private final ProjectFile m_file;
   private final OutputStreamWriter m_writer;

   private final Format m_dateFormat = new SimpleDateFormat("yyyy-MM-dd");
   private final Format m_timestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
   private final DecimalFormat m_doubleFormat = new DecimalFormat("0.####");
   private final DecimalFormat m_rateFormat = new DecimalFormat("0.0000");
   private final DecimalFormat m_maxUnitsFormat = new DecimalFormat("0.####");

   private static final Map<Class<?>, FormatFunction> FORMAT_MAP = new HashMap<>();
   static
   {
      FORMAT_MAP.put(DateOnly.class, (w, o) -> w.m_dateFormat.format(((DateOnly)o).toDate()));
      FORMAT_MAP.put(Date.class, (w, o) -> w.m_timestampFormat.format(o));
      FORMAT_MAP.put(Double.class, (w, o) -> w.m_doubleFormat.format(o));
      FORMAT_MAP.put(Boolean.class, (w, o) -> ((Boolean) o).booleanValue() ? "Y" : "N");
      FORMAT_MAP.put(Rate.class, (w, o) -> w.m_rateFormat.format(((Rate)o).getAmount()));
      FORMAT_MAP.put(UUID.class, (w, o) -> w.formatUUID((UUID)o));
      FORMAT_MAP.put(ResourceType.class, (w, o) -> ResourceTypeHelper.getXerFromInstance((ResourceType)o));
      FORMAT_MAP.put(CriticalActivityType.class, (w, o) -> CriticalActivityTypeHelper.getXerFromInstance((CriticalActivityType)o));
      FORMAT_MAP.put(TaskType.class, (w, o) -> TaskTypeHelper.getXerFromInstance((TaskType) o));
      FORMAT_MAP.put(CalendarType.class, (w, o) -> CalendarTypeHelper.getXerFromInstance((CalendarType)o));
      FORMAT_MAP.put(ActivityType.class, (w, o) -> ActivityTypeHelper.getXerFromInstance((ActivityType)o));
      FORMAT_MAP.put(PercentCompleteType.class, (w, o) -> PercentCompleteTypeHelper.getXerFromInstance((PercentCompleteType)o));
      FORMAT_MAP.put(ActivityStatus.class, (w, o) -> ActivityStatusHelper.getXerFromInstance((ActivityStatus)o));
      FORMAT_MAP.put(Duration.class, (w, o) -> w.formatDuration((Duration)o));
      FORMAT_MAP.put(ConstraintType.class, (w, o) -> ConstraintTypeHelper.getXerFromInstance((ConstraintType)o));
      FORMAT_MAP.put(Priority.class, (w, o) -> PriorityHelper.getXerFromInstance((Priority)o));
      FORMAT_MAP.put(RelationType.class, (w, o) -> RelationTypeHelper.getXerFromInstance((RelationType)o));
      FORMAT_MAP.put(AccrueType.class, (w, o) -> AccrueTypeHelper.getXerFromInstance((AccrueType)o));
      FORMAT_MAP.put(ActivityCodeScope.class, (w, o) -> ActivityCodeScopeHelper.getXerFromInstance((ActivityCodeScope)o));
      FORMAT_MAP.put(Color.class, (w, o) -> ColorHelper.getHexColor((Color)o));
      FORMAT_MAP.put(RateSource.class, (w, o) -> RateSourceHelper.getXerFromInstance((RateSource)o));
      FORMAT_MAP.put(DataType.class, (w, o) -> UdfHelper.getXerFromDataType((DataType)o));
      FORMAT_MAP.put(MaxUnits.class, (w, o) -> w.m_maxUnitsFormat.format(NumberHelper.getDouble(((MaxUnits)o).toNumber()) / 100.0));
   }
}