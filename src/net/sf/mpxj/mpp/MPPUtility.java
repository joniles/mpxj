/*
 * file:       MPPUtility.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2002-2003
 * date:       05/01/2003
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

package net.sf.mpxj.mpp;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import net.sf.mpxj.CurrencySymbolPosition;
import net.sf.mpxj.Duration;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Task;
import net.sf.mpxj.TimeUnit;



/**
 * This class provides common functionality used by each of the classes
 * that read the different sections of the MPP file.
 */
final class MPPUtility
{
   /**
    * Private constructor to prevent instantiation.
    */
   private MPPUtility ()
   {
      // private constructor to prevent instantiation
   }

   /**
    * This method extracts a portion of a byte array and writes it into
    * another byte array.
    *
    * @param data Source data
    * @param offset Offset into source data
    * @param size Requied size to be extracted from the source data
    * @param buffer Destination buffer
    * @param bufferOffset Offset into destination buffer
    */
   public static final void getByteArray (byte[] data, int offset, int size, byte[] buffer, int bufferOffset)
   {
      for (int loop = 0; loop < size; loop++)
      {
         buffer[bufferOffset + loop] = data[offset + loop];
      }
   }

   /**
    * This method reads a single byte from the input array.
    *
    * @param data byte array of data
    * @param offset offset of byte data in the array
    * @return byte value
    */
   public static final int getByte (byte[] data, int offset)
   {
      int result = data[offset] & 0x0F;
      result += (((data[offset] >> 4) & 0x0F) * 16);
      return (result);
   }

   /**
    * This method reads a single byte from the input array.
    * The byte is assumed to be at the start of the array.
    *
    * @param data byte array of data
    * @return byte value
    */
   public static final int getByte (byte[] data)
   {
      return (getByte(data, 0));
   }

   /**
    * This method reads a two byte integer from the input array.
    *
    * @param data the input array
    * @param offset offset of integer data in the array
    * @return integer value
    */
   public static final int getShort (byte[] data, int offset)
   {
      int result = (data[offset] & 0x0F);
      result += (((data[offset] >> 4) & 0x0F) * 16);
      result += ((data[offset + 1] & 0x0F) * 256);
      result += (((data[offset + 1] >> 4) & 0x0F) * 4096);
      return (result);
   }

   /**
    * This method reads a two byte integer from the input array.
    * The integer is assumed to be at the start of the array.
    *
    * @param data the input array
    * @return integer value
    */
   public static final int getShort (byte[] data)
   {
      return (getShort(data, 0));
   }

   /**
    * This method reads a four byte integer from the input array.
    *
    * @param data the input array
    * @param offset offset of integer data in the array
    * @return integer value
    */
   public static final int getInt (byte[] data, int offset)
   {
      int result = (data[offset] & 0x0F);
      result += (((data[offset] >> 4) & 0x0F) * 16);
      result += ((data[offset + 1] & 0x0F) * 256);
      result += (((data[offset + 1] >> 4) & 0x0F) * 4096);
      result += ((data[offset + 2] & 0x0F) * 65536);
      result += (((data[offset + 2] >> 4) & 0x0F) * 1048576);
      result += ((data[offset + 3] & 0x0F) * 16777216);
      result += (((data[offset + 3] >> 4) & 0x0F) * 268435456);
      return (result);
   }

   /**
    * This method reads a four byte integer from the input array.
    * The integer is assumed to be at the start of the array.
    *
    * @param data the input array
    * @return integer value
    */
   public static final int getInt (byte[] data)
   {
      return (getInt(data, 0));
   }

   /**
    * This method reads an eight byte integer from the input array.
    *
    * @param data the input array
    * @param offset offset of integer data in the array
    * @return integer value
    */
   public static final long getLong (byte[] data, int offset)
   {
      long result = (data[offset] & 0x0F); // 0
      result += (((data[offset] >> 4) & 0x0F) * 16); // 1
      result += ((data[offset + 1] & 0x0F) * 256); // 2
      result += (((data[offset + 1] >> 4) & 0x0F) * 4096); // 3
      result += ((data[offset + 2] & 0x0F) * 65536); // 4
      result += (((data[offset + 2] >> 4) & 0x0F) * 1048576); // 5
      result += ((data[offset + 3] & 0x0F) * 16777216); // 6
      result += (((data[offset + 3] >> 4) & 0x0F) * 268435456); // 7
      result += ((data[offset + 4] & 0x0F) * 4294967296L); // 8
      result += (((data[offset + 4] >> 4) & 0x0F) * 68719476736L); // 9
      result += ((data[offset + 5] & 0x0F) * 1099511627776L); // 10
      result += (((data[offset + 5] >> 4) & 0x0F) * 17592186044416L); // 11
      result += ((data[offset + 6] & 0x0F) * 281474976710656L); // 12
      result += (((data[offset + 6] >> 4) & 0x0F) * 4503599627370496L); // 13
      result += ((data[offset + 7] & 0x0F) * 72057594037927936L); // 14
      result += (((data[offset + 7] >> 4) & 0x0F) * 1152921504606846976L); // 15
      return (result);
   }

   /**
    * This method reads a six byte long from the input array.
    *
    * @param data the input array
    * @param offset offset of integer data in the array
    * @return integer value
    */
   public static final long getLong6 (byte[] data, int offset)
   {
      long result = (data[offset] & 0x0F); // 0
      result += (((data[offset] >> 4) & 0x0F) * 16); // 1
      result += ((data[offset + 1] & 0x0F) * 256); // 2
      result += (((data[offset + 1] >> 4) & 0x0F) * 4096); // 3
      result += ((data[offset + 2] & 0x0F) * 65536); // 4
      result += (((data[offset + 2] >> 4) & 0x0F) * 1048576); // 5
      result += ((data[offset + 3] & 0x0F) * 16777216); // 6
      result += (((data[offset + 3] >> 4) & 0x0F) * 268435456); // 7
      result += ((data[offset + 4] & 0x0F) * 4294967296L); // 8
      result += (((data[offset + 4] >> 4) & 0x0F) * 68719476736L); // 9
      result += ((data[offset + 5] & 0x0F) * 1099511627776L); // 10
      result += (((data[offset + 5] >> 4) & 0x0F) * 17592186044416L); // 11

      return (result);
   }

   /**
    * This method reads a six byte long from the input array.
    * The integer is assumed to be at the start of the array.
    *
    * @param data the input array
    * @return integer value
    */
   public static final long getLong6 (byte[] data)
   {
      return (getLong6(data, 0));
   }

   /**
    * This method reads a eight byte integer from the input array.
    * The integer is assumed to be at the start of the array.
    *
    * @param data the input array
    * @return integer value
    */
   public static final long getLong (byte[] data)
   {
      return (getLong(data, 0));
   }

   /**
    * This method reads an eight byte double from the input array.
    *
    * @param data the input array
    * @param offset offset of double data in the array
    * @return double value
    */
   public static final double getDouble (byte[] data, int offset)
   {
      return (Double.longBitsToDouble(getLong(data, offset)));
   }

   /**
    * This method reads an eight byte double from the input array.
    * The double is assumed to be at the start of the array.
    *
    * @param data the input array
    * @return double value
    */
   public static final double getDouble (byte[] data)
   {
      return (Double.longBitsToDouble(getLong(data, 0)));
   }

   /**
    * Reads a date value. Note that an NA is represented as 65535 in the
    * MPP file. We represent this in Java using a null value. The actual
    * value in the MPP file is number of days since 31/12/1983.
    *
    * @param data byte array of data
    * @param offset location of data as offset into the array
    * @return date value
    */
   public static final Date getDate (byte[] data, int offset)
   {
      Date result;
      long days = getShort(data, offset);

      if (days == 65535)
      {
         result = null;
      }
      else
      {
         TimeZone tz = TimeZone.getDefault();
         result = new Date(EPOCH + (days * MS_PER_DAY) - tz.getRawOffset());
      }

      return (result);
   }

   /**
    * Reads a time value. The time is represented as tenths of a
    * minute since midnight.
    *
    * @param data byte array of data
    * @param offset location of data as offset into the array
    * @return time value
    */
   public static final Date getTime (byte[] data, int offset)
   {
      int time = getShort(data, offset) / 10;
      Calendar cal = Calendar.getInstance();
      cal.set(Calendar.HOUR_OF_DAY, (time / 60));
      cal.set(Calendar.MINUTE, (time % 60));
      cal.set(Calendar.SECOND, 0);
      cal.set(Calendar.MILLISECOND, 0);
      return (cal.getTime());
   }

   /**
    * Reads a time value. The time is represented as tenths of a
    * minute since midnight.
    *
    * @param data byte array of data
    * @return time value
    */
   public static final Date getTime (byte[] data)
   {
      return (getTime(data, 0));
   }

   /**
    * Reads a duration value in milliseconds. The time is represented as
    * tenths of a minute since midnight.
    *
    * @param data byte array of data
    * @param offset location of data as offset into the array
    * @return duration value
    */
   public static final long getDuration (byte[] data, int offset)
   {
      return ((getShort(data, offset) * MS_PER_MINUTE) / 10);
   }

   /**
    * Reads a combined date and time value.
    *
    * @param data byte array of data
    * @param offset location of data as offset into the array
    * @return time value
    */
   public static final Date getTimestamp (byte[] data, int offset)
   {
      Date result;

      long days = getShort(data, offset + 2);

      if (days == 65535)
      {
         result = null;
      }
      else
      {
         TimeZone tz = TimeZone.getDefault();
         long time = getShort(data, offset);
         if (time == 65535)
         {
            time = 0;
         }
         result = new Date((EPOCH + (days * MS_PER_DAY) + ((time * MS_PER_MINUTE) / 10)) - tz.getRawOffset());

         if (tz.inDaylightTime(result) == true)
         {
            int savings;

            if (HAS_DST_SAVINGS == true)
            {
               savings = tz.getDSTSavings();
            }
            else
            {
               savings = DEFAULT_DST_SAVINGS;
            }

            result = new Date(result.getTime() - savings);
         }
      }

      return (result);
   }

   /**
    * Reads a combined date and time value.
    * The value is assumed to be at the start of the array.
    *
    * @param data byte array of data
    * @return time value
    */
   public static final Date getTimestamp (byte[] data)
   {
      return (getTimestamp(data, 0));
   }

   /**
    * Reads a string of two byte characters from the input array.
    * This method assumes that the string finishes either at the
    * end of the array, or when char zero is encountered.
    * The value is assumed to be at the start of the array.
    *
    * @param data byte array of data
    * @return string value
    */
   public static final String getUnicodeString (byte[] data)
   {
      return (getUnicodeString(data, 0));
   }

   /**
    * Reads a string of two byte characters from the input array.
    * This method assumes that the string finishes either at the
    * end of the array, or when char zero is encountered.
    * The value starts at the position specified by the offset
    * parameter.
    *
    * @param data byte array of data
    * @param offset start point of unicode string
    * @return string value
    */
   public static final String getUnicodeString (byte[] data, int offset)
   {
      StringBuffer buffer = new StringBuffer();
      char c;

      for (int loop = offset; loop < (data.length - 1); loop += 2)
      {
         c = (char)getShort(data, loop);

         if (c == 0)
         {
            break;
         }

         buffer.append(c);
      }

      return (buffer.toString());
   }


   /**
    * Reads a string of two byte characters from the input array.
    * This method assumes that the string finishes either at the
    * end of the array, or when char zero is encountered, or
    * when a string of a certain length in bytes has been read.
    * The value starts at the position specified by the offset
    * parameter.
    *
    * @param data byte array of data
    * @param offset start point of unicode string
    * @param length length in bytes of the string
    * @return string value
    */
   public static final String getUnicodeString (byte[] data, int offset, int length)
   {
      StringBuffer buffer = new StringBuffer();
      char c;
      int loop = offset;
      int byteLength = 0;

      while (loop < (data.length - 1) && byteLength < length)
      {
         c = (char)getShort(data, loop);

         if (c == 0)
         {
            break;
         }

         buffer.append(c);

         loop += 2;
         byteLength += 2;
      }

      return (buffer.toString());
   }

   /**
    * Reads a string of single byte characters from the input array.
    * This method assumes that the string finishes either at the
    * end of the array, or when char zero is encountered.
    * The value is assumed to be at the start of the array.
    *
    * @param data byte array of data
    * @return string value
    */
   public static final String getString (byte[] data)
   {
      return (getString(data, 0));
   }

   /**
    * Reads a string of single byte characters from the input array.
    * This method assumes that the string finishes either at the
    * end of the array, or when char zero is encountered.
    * Redaing begins at the supplied offset into the array.
    *
    * @param data byte array of data
    * @param offset offset into the array
    * @return string value
    */
   public static final String getString (byte[] data, int offset)
   {
      StringBuffer buffer = new StringBuffer();
      char c;

      for (int loop = 0; offset+loop < data.length; loop++)
      {
         c = (char)data[offset+loop];

         if (c == 0)
         {
            break;
         }

         buffer.append(c);
      }

      return (buffer.toString());
   }

   /**
    * Reads a duration value. This method relies on the fact that
    * the units of the duration have been specified elsewhere.
    *
    * @param value Duration value
    * @param type type of units of the duration
    * @return Duration instance
    */
   public static final Duration getDuration (int value, TimeUnit type)
   {
      return (getDuration((double)value, type));
   }

   /**
    * Reads a duration value. This method relies on the fact that
    * the units of the duration have been specified elsewhere.
    *
    * @param value Duration value
    * @param type type of units of the duration
    * @return Duration instance
    */
   public static final Duration getDuration (double value, TimeUnit type)
   {
      double duration;

      switch (type.getValue())
      {
         case TimeUnit.MINUTES_VALUE:
         case TimeUnit.ELAPSED_MINUTES_VALUE:
         {
            duration = value / 10;
            break;
         }

         case TimeUnit.HOURS_VALUE:
         case TimeUnit.ELAPSED_HOURS_VALUE:
         {
            duration = value / 600;
            break;
         }

         case TimeUnit.DAYS_VALUE:
         case TimeUnit.ELAPSED_DAYS_VALUE:
         {
            duration = value / 4800;
            break;
         }

         case TimeUnit.WEEKS_VALUE:
         case TimeUnit.ELAPSED_WEEKS_VALUE:
         {
            duration = value / 24000;
            break;
         }

         case TimeUnit.MONTHS_VALUE:
         case TimeUnit.ELAPSED_MONTHS_VALUE:
         {
            duration = value / 96000;
            break;
         }

         default:
         {
            duration = value;
            break;
         }
      }

      return (Duration.getInstance(duration, type));
   }

   /**
    * This method converts between the duration units representation
    * used in the MPP file, and the standard MPX duration units.
    * If the supplied units are unrecognised, the units default to days.
    *
    * @param type MPP units
    * @return MPX units
    */
   public static final TimeUnit getDurationTimeUnits (int type)
   {
      TimeUnit units;

      switch (type & DURATION_UNITS_MASK)
      {
         case 3:
         {
            units = TimeUnit.MINUTES;
            break;
         }

         case 4:
         {
            units = TimeUnit.ELAPSED_MINUTES;
            break;
         }

         case 5:
         {
            units = TimeUnit.HOURS;
            break;
         }

         case 6:
         {
            units = TimeUnit.ELAPSED_HOURS;
            break;
         }

         case 8:
         {
            units = TimeUnit.ELAPSED_DAYS;
            break;
         }

         case 9:
         {
            units = TimeUnit.WEEKS;
            break;
         }

         case 10:
         {
            units = TimeUnit.ELAPSED_WEEKS;
            break;
         }

         case 11:
         {
            units = TimeUnit.MONTHS;
            break;
         }

         case 12:
         {
            units = TimeUnit.ELAPSED_MONTHS;
            break;
         }

         default:
         case 7:
         {
            units = TimeUnit.DAYS;
            break;
         }
      }

      return (units);
   }

   /**
    * Given a duration and the time units for the duration extracted from an MPP
    * file, this method creates a new Duration to represent the given
    * duration. This instance has been adjusted to take into account the
    * number of "hours per day" specified for the current project.
    *
    * @param file parent file
    * @param duration duration length
    * @param timeUnit duration units
    * @return Duration instance
    */
   public static Duration getAdjustedDuration (ProjectFile file, int duration, TimeUnit timeUnit)
   {
      Duration result;
      switch (timeUnit.getValue())
      {
         case TimeUnit.DAYS_VALUE:
         {
            double unitsPerDay = file.getProjectHeader().getMinutesPerDay().doubleValue() * 10d;
            double totalDays = duration / unitsPerDay;
            result = Duration.getInstance(totalDays, timeUnit);
            break;
         }

         case TimeUnit.ELAPSED_DAYS_VALUE:
         {
            double unitsPerDay = 24d * 600d;
            double totalDays = duration / unitsPerDay;
            result = Duration.getInstance(totalDays, timeUnit);
            break;
         }

         case TimeUnit.ELAPSED_WEEKS_VALUE:
         {
            double unitsPerWeek = (60 * 24 * 7 * 10);
            double totalWeeks = duration / unitsPerWeek;
            result = Duration.getInstance(totalWeeks, timeUnit);
            break;
         }

         case TimeUnit.ELAPSED_MONTHS_VALUE:
         {
            double unitsPerMonth = (60 * 24 * 29 * 10);
            double totalMonths = duration / unitsPerMonth;
            result = Duration.getInstance(totalMonths, timeUnit);
            break;
         }
         
         default:
         {
            result = getDuration(duration, timeUnit);
            break;
         }
      }

      return (result);
   }


   /**
    * This method maps from the value used to specify default work units in the
    * MPP file to a standard TimeUnit.
    *
    * @param value Default work units
    * @return TimeUnit value
    */
   public static TimeUnit getWorkTimeUnits (int value)
   {
      TimeUnit result;

      switch (value)
      {
         case 1:
         {
            result = TimeUnit.MINUTES;
            break;
         }

         case 3:
         {
            result = TimeUnit.DAYS;
            break;
         }

         case 4:
         {
            result = TimeUnit.WEEKS;
            break;
         }

         case 2:default:
         {
            result = TimeUnit.HOURS;
            break;
         }
      }

      return (result);
   }

   /**
    * This method maps the currency symbol position from the
    * representation used in the MPP file to the representation
    * used by MPX.
    *
    * @param value MPP symbol position
    * @return MPX symbol position
    */
   public static CurrencySymbolPosition getSymbolPosition (int value)
   {
      CurrencySymbolPosition result;

      switch (value)
      {
         case 1:
         {
            result = CurrencySymbolPosition.AFTER;
            break;
         }

         case 2:
         {
            result = CurrencySymbolPosition.BEFORE_WITH_SPACE;
            break;
         }

         case 3:
         {
            result = CurrencySymbolPosition.AFTER_WITH_SPACE;
            break;
         }

         case 0:default:
         {
            result = CurrencySymbolPosition.BEFORE;
            break;
         }
      }

      return (result);
   }

   /**
    * Utility methdo to remove ampersands embedded in names.
    *
    * @param name name text
    * @return name text without embedded ampersands
    */
   public static final String removeAmpersands (String name)
   {
      if (name != null)
      {
         if (name.indexOf('&') != -1)
         {
            StringBuffer sb = new StringBuffer();
            int index = 0;
            char c;

            while (index < name.length())
            {
               c = name.charAt(index);
               if (c != '&')
               {
                  sb.append(c);
               }
               ++index;
            }

            name = sb.toString();
         }
      }

      return (name);
   }

   /**
    * This method allows a subsection of a byte array to be copied.
    *
    * @param data source data
    * @param offset offset into the source data
    * @param size length of the source data to copy
    * @return new byte array containing copied data
    */
   public static final byte[] cloneSubArray (byte[] data, int offset, int size)
   {
      byte[] newData = new byte[size];
      System.arraycopy(data, offset, newData, 0, size);
      return (newData);
   }

   /**
    * This utility method calculates the difference in working
    * time between two dates, given the context of a task.
    * 
    * @param task parent task
    * @param date1 first date
    * @param date2 second date
    * @param format required format for the resulting duration
    * @return difference in working time between the two dates
    */
   public static Duration getVariance (Task task, Date date1, Date date2, TimeUnit format)
   {
      Duration variance = null;
      
      if (date1 != null & date2 != null)
      {
         ProjectCalendar calendar = task.getCalendar();
         if (calendar == null)
         {
            ProjectFile file = task.getParentFile();
            calendar = file.getBaseCalendar(file.getProjectHeader().getCalendarName());
         }
         
         if (calendar != null)
         {
            variance = calendar.getWork(date1, date2, format);
         }         
      }
      
      if (variance == null)
      {
         variance = Duration.getInstance(0, format);
      }
      
      return (variance);
   }
   
   /**
    * This method generates a formatted version of the data contained
    * in a byte array. The data is written both in hex, and as ASCII
    * characters.
    *
    * @param buffer data to be displayed
    * @param offset offset of start of data to be displayed
    * @param length length of data to be displayed
    * @param ascii flag indicating whether ASCII equivalent chars should also be displayed
    * @return formatted string
    */
   public static final String hexdump (byte[] buffer, int offset, int length, boolean ascii)
   {
      StringBuffer sb = new StringBuffer();

      if (buffer != null)
      {
         char c;
         int loop;
         int count = offset + length;

         for (loop = offset; loop < count; loop++)
         {
            sb.append(" ");
            sb.append(HEX_DIGITS[(buffer[loop] & 0xF0) >> 4]);
            sb.append(HEX_DIGITS[buffer[loop] & 0x0F]);
         }

         if (ascii == true)
         {
            sb.append("   ");

            for (loop = offset; loop < count; loop++)
            {
               c = (char)buffer[loop];

               if ((c > 200) || (c < 27))
               {
                  c = ' ';
               }

               sb.append(c);
            }
         }
      }

      return (sb.toString());
   }

   /**
    * This method generates a formatted version of the data contained
    * in a byte array. The data is written both in hex, and as ASCII
    * characters.
    *
    * @param buffer data to be displayed
    * @param ascii flag indicating whether ASCII equivalent chars should also be displayed
    * @return formatted string
    */
   public static final String hexdump (byte[] buffer, boolean ascii)
   {
      int length = 0;

      if (buffer != null)
      {
         length = buffer.length;
      }

      return (hexdump(buffer, 0, length, ascii));
   }

   /**
    * This method generates a formatted version of the data contained
    * in a byte array. The data is written both in hex, and as ASCII
    * characters. The data is organised into fixed width columns.
    *
    * @param buffer data to be displayed
    * @param ascii flag indicating whether ASCII equivalent chars should also be displayed
    * @param columns number of columns
    * @param prefix prefix to be added before the start of the data
    * @return formatted string
    */
   public static final String hexdump (byte[] buffer, boolean ascii, int columns, String prefix)
   {
      StringBuffer sb = new StringBuffer();
      if (buffer != null)
      {
         int index = 0;
         DecimalFormat df = new DecimalFormat("00000");

         while (index < buffer.length)
         {
            if (index + columns > buffer.length)
            {
               columns = buffer.length - index;
            }

            sb.append (prefix);
            sb.append (df.format(index));
            sb.append (":");
            sb.append (hexdump(buffer, index, columns, ascii));
            sb.append ('\n');

            index += columns;
         }
      }

      return (sb.toString());
   }

   /**
    * This method generates a formatted version of the data contained
    * in a byte array. The data is written both in hex, and as ASCII
    * characters. The data is organised into fixed width columns.
    * 
    * @param buffer data to be displayed
    * @param offset offset into buffer
    * @param length number of bytes to display
    * @param ascii flag indicating whether ASCII equivalent chars should also be displayed
    * @param columns number of columns
    * @param prefix prefix to be added before the start of the data
    * @return formatted string
    */
   public static final String hexdump (byte[] buffer, int offset, int length, boolean ascii, int columns, String prefix)
   {
      StringBuffer sb = new StringBuffer();
      if (buffer != null)
      {
         int index = offset;
         DecimalFormat df = new DecimalFormat("00000");

         while (index < (offset+length))
         {
            if (index + columns > (offset+length))
            {
               columns = (offset+length) - index;
            }

            sb.append (prefix);
            sb.append (df.format(index));
            sb.append (":");
            sb.append (hexdump(buffer, index, columns, ascii));
            sb.append ('\n');

            index += columns;
         }
      }

      return (sb.toString());
   }
   
   /**
    * Writes a hex dump to a file for a large byte array.
    *
    * @param fileName output file name
    * @param data target data
    */
   public static final void fileHexDump (String fileName, byte[] data)
   {
      try
      {
         FileOutputStream os = new FileOutputStream(fileName);
         os.write(hexdump(data, true, 16, "").getBytes());
         os.close();
      }

      catch (IOException ex)
      {
         ex.printStackTrace();
      }
   }

   /**
    * Writes a hex dump to a file from a POI input stream.
    * Note that this assumes that the complete size of the data in
    * the stream is returned by the available() method.
    *
    * @param fileName output file name
    * @param is input stream
    */
   public static final void fileHexDump (String fileName, InputStream is)
   {
      try
      {
         byte[] data = new byte[is.available()];
         is.read(data);
         fileHexDump(fileName, data);
      }

      catch (IOException ex)
      {
         ex.printStackTrace();
      }
   }

   /**
    * Writes a large byte array to a file.
    *
    * @param fileName output file name
    * @param data target data
    */
   public static final void fileDump (String fileName, byte[] data)
   {
      try
      {
         FileOutputStream os = new FileOutputStream(fileName);
         os.write(data);
         os.close();
      }

      catch (IOException ex)
      {
         ex.printStackTrace();
      }
   }

   /**
    * Epoch date for MPP date calculation is 31/12/1983. This constant
    * is that date expressed in milliseconds using the Java date epoch.
    */
   private static final long EPOCH = 441676800000L;

   /**
    * Number of milliseconds per day.
    */
   private static final long MS_PER_DAY = 24 * 60 * 60 * 1000;

   /**
    * Number of milliseconds per minute.
    */
   private static final long MS_PER_MINUTE = 60 * 1000;

   /**
    * Constants used to convert bytes to hex digits.
    */
   private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

   /**
    * Mask used to remove flags from the duration units field.
    */
   private static final int DURATION_UNITS_MASK = 0x1F;

   /**
    * Default value to use for DST savings if we are using a version
    * of Java < 1.4.
    */
   private static final int DEFAULT_DST_SAVINGS = 3600000;

   /**
    * Flag used to indicate the existance of the getDSTSavings
    * method that was introduced in Java 1.4.
    */
   private static boolean HAS_DST_SAVINGS;

   static
   {
      Class tz = TimeZone.class;

      try
      {
         tz.getMethod("getDSTSavings", (Class[])null);
         HAS_DST_SAVINGS = true;
      }

      catch (NoSuchMethodException ex)
      {
         HAS_DST_SAVINGS = false;
      }
   }
}
