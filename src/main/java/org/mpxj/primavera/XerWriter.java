/*
 * file:       XerWriter.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2023
 * date:       07/03/2023
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

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.mpxj.AccrueType;
import org.mpxj.ActivityCodeScope;
import org.mpxj.ActivityStatus;
import org.mpxj.ActivityType;
import org.mpxj.CalendarType;
import org.mpxj.ConstraintType;
import org.mpxj.CriticalActivityType;
import org.mpxj.DataType;
import org.mpxj.Duration;
import org.mpxj.HtmlNotes;
import org.mpxj.Notes;
import org.mpxj.PercentCompleteType;
import org.mpxj.Priority;
import org.mpxj.ProjectFile;
import org.mpxj.Rate;
import org.mpxj.RateSource;
import org.mpxj.RelationType;
import org.mpxj.ResourceType;
import org.mpxj.SkillLevel;
import org.mpxj.TaskType;
import org.mpxj.TimeUnit;
import org.mpxj.common.ColorHelper;
import org.mpxj.common.HtmlHelper;
import org.mpxj.common.NumberHelper;

/**
 * Handles writing records to an XER file.
 */
final class XerWriter
{
   /**
    * Constructor.
    *
    * @param file project to write
    * @param writer Writer instance to receive XER records
    */
   public XerWriter(ProjectFile file, OutputStreamWriter writer)
   {
      m_file = file;
      m_writer = writer;
   }

   /**
    * Write the XER file's header record.
    *
    * @param data header data
    */
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

   /**
    * Write a table definition to an XER file.
    *
    * @param name table name
    * @param map table fields
    */
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

   /**
    * Write a table row to an XER file.
    *
    * @param columns functions used to create the table data
    * @param object source object for data
    * @param <T> source object type
    */
   public <T> void writeRecord(Map<String, PrimaveraXERFileWriter.ExportFunction<T>> columns, T object)
   {
      writeRecord(columns.values().stream().map(f -> f.apply(object)));
   }

   /**
    * Write a table row to an XER file.
    *
    * @param data field data for a table row
    */
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

   /**
    * Write the XER file trailer.
    */
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

   /**
    * Flush the writer.
    */
   public void flush() throws IOException
   {
      m_writer.flush();
   }

   /**
    * Format a field value to be written to an XER file.
    *
    * @param object field value
    * @return formatted field value
    */
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

   /**
    * Format a Notes object.
    *
    * @param notes Notes instance
    * @return formatted notes
    */
   private String formatNotes(Notes notes)
   {
      String result;
      if (notes == null || notes.isEmpty())
      {
         result = "";
      }
      else
      {
         result = notes instanceof HtmlNotes ? ((HtmlNotes) notes).getHtml() : HtmlHelper.getHtmlFromPlainText(notes.toString());
         result = result.replace("\n", "\u007F\u007F");
      }

      return formatString(result);
   }

   /**
    * Format a UUID.
    *
    * @param value UUID instance
    * @return formatted UUID value
    */
   private String formatUUID(UUID value)
   {
      byte[] data = new byte[16];
      long lsb = value.getLeastSignificantBits();
      long msb = value.getMostSignificantBits();

      data[15] = (byte) (lsb & 0xff);
      data[14] = (byte) (lsb >> 8 & 0xff);
      data[13] = (byte) (lsb >> 16 & 0xff);
      data[12] = (byte) (lsb >> 24 & 0xff);
      data[11] = (byte) (lsb >> 32 & 0xff);
      data[10] = (byte) (lsb >> 40 & 0xff);
      data[9] = (byte) (lsb >> 48 & 0xff);
      data[8] = (byte) (lsb >> 56 & 0xff);

      data[6] = (byte) (msb & 0xff);
      data[7] = (byte) (msb >> 8 & 0xff);
      data[4] = (byte) (msb >> 16 & 0xff);
      data[5] = (byte) (msb >> 24 & 0xff);
      data[0] = (byte) (msb >> 32 & 0xff);
      data[1] = (byte) (msb >> 40 & 0xff);
      data[2] = (byte) (msb >> 48 & 0xff);
      data[3] = (byte) (msb >> 56 & 0xff);

      String result = jakarta.xml.bind.DatatypeConverter.printBase64Binary(data);

      return result.substring(0, result.length() - 2);
   }

   /**
    * Format a Duration instance.
    *
    * @param duration Duration instance
    * @return formatted value
    */
   private String formatDuration(Duration duration)
   {
      if (duration == null)
      {
         return "";
      }

      return m_doubleFormat.format(duration.convertUnits(TimeUnit.HOURS, m_file.getProjectProperties()).getDuration());
   }

   /**
    * Format a String instance. This is used to escape double quote characters
    * if they are present.
    *
    * @param value String instance
    * @return formatted value
    */
   private String formatString(String value)
   {
      if (value == null || value.isEmpty())
      {
         return "";
      }

      if (value.indexOf('"') == -1)
      {
         return value;
      }

      return value.replace("\"", "\"\"");
   }

   private interface FormatFunction
   {
      String apply(XerWriter writer, Object source);
   }

   private final ProjectFile m_file;
   private final OutputStreamWriter m_writer;
   private final DateTimeFormatter m_dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
   private final DateTimeFormatter m_timestampFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
   private final DecimalFormat m_doubleFormat = new DecimalFormat("0.######");
   private final DecimalFormat m_currencyFormat = new DecimalFormat("0.0000");
   private final DecimalFormat m_maxUnitsFormat = new DecimalFormat("0.####");

   private static final Map<Class<?>, FormatFunction> FORMAT_MAP = new HashMap<>();
   static
   {
      FORMAT_MAP.put(DateOnly.class, (w, o) -> w.m_dateFormat.format(((DateOnly) o).toDate()));
      FORMAT_MAP.put(LocalDateTime.class, (w, o) -> w.m_timestampFormat.format((LocalDateTime) o));
      FORMAT_MAP.put(Double.class, (w, o) -> w.m_doubleFormat.format(o));
      FORMAT_MAP.put(Boolean.class, (w, o) -> ((Boolean) o).booleanValue() ? "Y" : "N");
      FORMAT_MAP.put(Rate.class, (w, o) -> w.m_currencyFormat.format(((Rate) o).getAmount()));
      FORMAT_MAP.put(UUID.class, (w, o) -> w.formatUUID((UUID) o));
      FORMAT_MAP.put(ResourceType.class, (w, o) -> ResourceTypeHelper.getXerFromInstance((ResourceType) o));
      FORMAT_MAP.put(CriticalActivityType.class, (w, o) -> CriticalActivityTypeHelper.getXerFromInstance((CriticalActivityType) o));
      FORMAT_MAP.put(TaskType.class, (w, o) -> TaskTypeHelper.getXerFromInstance((TaskType) o));
      FORMAT_MAP.put(CalendarType.class, (w, o) -> CalendarTypeHelper.getXerFromInstance((CalendarType) o));
      FORMAT_MAP.put(ActivityType.class, (w, o) -> ActivityTypeHelper.getXerFromInstance((ActivityType) o));
      FORMAT_MAP.put(PercentCompleteType.class, (w, o) -> PercentCompleteTypeHelper.getXerFromInstance((PercentCompleteType) o));
      FORMAT_MAP.put(ActivityStatus.class, (w, o) -> ActivityStatusHelper.getXerFromInstance((ActivityStatus) o));
      FORMAT_MAP.put(Duration.class, (w, o) -> w.formatDuration((Duration) o));
      FORMAT_MAP.put(ConstraintType.class, (w, o) -> ConstraintTypeHelper.getXerFromInstance((ConstraintType) o));
      FORMAT_MAP.put(Priority.class, (w, o) -> PriorityHelper.getXerFromInstance((Priority) o));
      FORMAT_MAP.put(RelationType.class, (w, o) -> RelationTypeHelper.getXerFromInstance((RelationType) o));
      FORMAT_MAP.put(AccrueType.class, (w, o) -> AccrueTypeHelper.getXerFromInstance((AccrueType) o));
      FORMAT_MAP.put(ActivityCodeScope.class, (w, o) -> ActivityCodeScopeHelper.getXerFromInstance((ActivityCodeScope) o));
      FORMAT_MAP.put(Color.class, (w, o) -> ColorHelper.getHexColor((Color) o));
      FORMAT_MAP.put(RateSource.class, (w, o) -> RateSourceHelper.getXerFromInstance((RateSource) o));
      FORMAT_MAP.put(DataType.class, (w, o) -> UdfHelper.getXerFromDataType((DataType) o));
      FORMAT_MAP.put(MaxUnits.class, (w, o) -> w.m_maxUnitsFormat.format(NumberHelper.getDouble(((MaxUnits) o).toNumber()) / 100.0));
      FORMAT_MAP.put(CurrencyValue.class, (w, o) -> w.m_currencyFormat.format(((CurrencyValue) o).toNumber()));
      FORMAT_MAP.put(SkillLevel.class, (w, o) -> SkillLevelHelper.getXerFromInstance((SkillLevel) o));
      FORMAT_MAP.put(String.class, (w, o) -> w.formatString((String) o));
   }
}
