/*
 * file:       MPPUtility.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2002-2003
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

import java.awt.Color;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import net.sf.mpxj.CurrencySymbolPosition;
import net.sf.mpxj.Duration;
import net.sf.mpxj.ProjectProperties;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.common.DateHelper;
import net.sf.mpxj.common.NumberHelper;

/**
 * This class provides common functionality used by each of the classes
 * that read the different sections of the MPP file.
 */
public final class MPPUtility
{
   /**
    * Private constructor to prevent instantiation.
    */
   private MPPUtility()
   {
      // private constructor to prevent instantiation
   }

   /**
    * This method decodes a byte array with the given encryption code
    * using XOR encryption.
    *
    * @param data Source data
    * @param encryptionCode Encryption code
    */
   public static final void decodeBuffer(byte[] data, byte encryptionCode)
   {
      for (int i = 0; i < data.length; i++)
      {
         data[i] = (byte) (data[i] ^ encryptionCode);
      }
   }

   /**
    * The mask used by Project to hide the password. The data must first
    * be decoded using the XOR key and then the password can be read by reading
    * the characters in given order starting with 1 and going up to 16.
    *
    * 00000: 00 00 04 00 00 00 05 00 07 00 12 00 10 00 06 00
    * 00016: 14 00 00 00 00 00 08 00 16 00 00 00 00 00 02 00
    * 00032: 00 00 15 00 00 00 11 00 00 00 00 00 00 00 09 00
    * 00048: 03 00 00 00 00 00 00 00 00 00 00 00 01 00 13 00
    */
   private static final int[] PASSWORD_MASK =
   {
      60,
      30,
      48,
      2,
      6,
      14,
      8,
      22,
      44,
      12,
      38,
      10,
      62,
      16,
      34,
      24
   };

   private static final int MINIMUM_PASSWORD_DATA_LENGTH = 64;

   /**
    * Decode the password from the given data. Will decode the data block as well.
    *
    * @param data encrypted data block
    * @param encryptionCode encryption code
    *
    * @return password
    */
   public static final String decodePassword(byte[] data, byte encryptionCode)
   {
      String result;

      if (data.length < MINIMUM_PASSWORD_DATA_LENGTH)
      {
         result = null;
      }
      else
      {
         MPPUtility.decodeBuffer(data, encryptionCode);

         StringBuilder buffer = new StringBuilder();
         char c;

         for (int i = 0; i < PASSWORD_MASK.length; i++)
         {
            int index = PASSWORD_MASK[i];
            c = (char) data[index];

            if (c == 0)
            {
               break;
            }
            buffer.append(c);
         }

         result = buffer.toString();
      }

      return (result);
   }

   /**
    * This method extracts a portion of a byte array and writes it into
    * another byte array.
    *
    * @param data Source data
    * @param offset Offset into source data
    * @param size Required size to be extracted from the source data
    * @param buffer Destination buffer
    * @param bufferOffset Offset into destination buffer
    */
   public static final void getByteArray(byte[] data, int offset, int size, byte[] buffer, int bufferOffset)
   {
      System.arraycopy(data, offset, buffer, bufferOffset, size);
   }

   /**
    * This method reads a single byte from the input array.
    *
    * @param data byte array of data
    * @param offset offset of byte data in the array
    * @return byte value
    */
   public static final int getByte(byte[] data, int offset)
   {
      int result = (data[offset] & 0xFF);
      return result;
   }

   /**
    * This method reads a two byte integer from the input array.
    *
    * @param data the input array
    * @param offset offset of integer data in the array
    * @return integer value
    */
   public static final int getShort(byte[] data, int offset)
   {
      int result = 0;
      int i = offset;
      for (int shiftBy = 0; shiftBy < 16; shiftBy += 8)
      {
         result |= ((data[i] & 0xff)) << shiftBy;
         ++i;
      }
      return result;
   }

   /**
    * This method reads a four byte integer from the input array.
    *
    * @param data the input array
    * @param offset offset of integer data in the array
    * @return integer value
    */
   public static final int getInt(byte[] data, int offset)
   {
      int result = 0;
      int i = offset;
      for (int shiftBy = 0; shiftBy < 32; shiftBy += 8)
      {
         result |= ((data[i] & 0xff)) << shiftBy;
         ++i;
      }
      return result;
   }

   /**
    * This method reads an eight byte integer from the input array.
    *
    * @param data the input array
    * @param offset offset of integer data in the array
    * @return integer value
    */
   public static final long getLong(byte[] data, int offset)
   {
      long result = 0;
      int i = offset;
      for (int shiftBy = 0; shiftBy < 64; shiftBy += 8)
      {
         result |= ((long) (data[i] & 0xff)) << shiftBy;
         ++i;
      }
      return result;
   }

   /**
    * This method reads a six byte long from the input array.
    *
    * @param data the input array
    * @param offset offset of integer data in the array
    * @return integer value
    */
   public static final long getLong6(byte[] data, int offset)
   {
      long result = 0;
      int i = offset;
      for (int shiftBy = 0; shiftBy < 48; shiftBy += 8)
      {
         result |= ((long) (data[i] & 0xff)) << shiftBy;
         ++i;
      }
      return result;
   }

   /**
    * This method reads an eight byte double from the input array.
    *
    * @param data the input array
    * @param offset offset of double data in the array
    * @return double value
    */
   public static final double getDouble(byte[] data, int offset)
   {
      double result = Double.longBitsToDouble(getLong(data, offset));
      if (Double.isNaN(result))
      {
         result = 0;
      }
      return result;
   }

   /**
    * Reads a UUID/GUID from a data block.
    *
    * @param data data block
    * @param offset offset into the data block
    * @return UUID instance
    */
   public static final UUID getGUID(byte[] data, int offset)
   {
      UUID result = null;
      if (data != null && data.length > 15)
      {
         long long1 = 0;
         long1 |= ((long) (data[offset + 3] & 0xFF)) << 56;
         long1 |= ((long) (data[offset + 2] & 0xFF)) << 48;
         long1 |= ((long) (data[offset + 1] & 0xFF)) << 40;
         long1 |= ((long) (data[offset + 0] & 0xFF)) << 32;
         long1 |= ((long) (data[offset + 5] & 0xFF)) << 24;
         long1 |= ((long) (data[offset + 4] & 0xFF)) << 16;
         long1 |= ((long) (data[offset + 7] & 0xFF)) << 8;
         long1 |= ((long) (data[offset + 6] & 0xFF)) << 0;

         long long2 = 0;
         long2 |= ((long) (data[offset + 8] & 0xFF)) << 56;
         long2 |= ((long) (data[offset + 9] & 0xFF)) << 48;
         long2 |= ((long) (data[offset + 10] & 0xFF)) << 40;
         long2 |= ((long) (data[offset + 11] & 0xFF)) << 32;
         long2 |= ((long) (data[offset + 12] & 0xFF)) << 24;
         long2 |= ((long) (data[offset + 13] & 0xFF)) << 16;
         long2 |= ((long) (data[offset + 14] & 0xFF)) << 8;
         long2 |= ((long) (data[offset + 15] & 0xFF)) << 0;

         result = new UUID(long1, long2);
      }
      return result;
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
   public static final Date getDate(byte[] data, int offset)
   {
      Date result;
      long days = getShort(data, offset);

      if (days == 65535)
      {
         result = null;
      }
      else
      {
         result = DateHelper.getDateFromLong(EPOCH + (days * MS_PER_DAY));
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
   public static final Date getTime(byte[] data, int offset)
   {
      int time = getShort(data, offset) / 10;
      Calendar cal = Calendar.getInstance();
      cal.setTime(EPOCH_DATE);
      cal.set(Calendar.HOUR_OF_DAY, (time / 60));
      cal.set(Calendar.MINUTE, (time % 60));
      cal.set(Calendar.SECOND, 0);
      cal.set(Calendar.MILLISECOND, 0);
      return (cal.getTime());
   }

   /**
    * Reads a duration value in milliseconds. The time is represented as
    * tenths of a minute since midnight.
    *
    * @param data byte array of data
    * @param offset location of data as offset into the array
    * @return duration value
    */
   public static final long getDuration(byte[] data, int offset)
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
   public static final Date getTimestamp(byte[] data, int offset)
   {
      Date result;

      long days = getShort(data, offset + 2);

      if (days == 0 || days == 65535)
      {
         result = null;
      }
      else
      {
         long time = getShort(data, offset);
         if (time == 65535)
         {
            time = 0;
         }
         result = DateHelper.getTimestampFromLong((EPOCH + (days * MS_PER_DAY) + ((time * MS_PER_MINUTE) / 10)));
      }

      return (result);
   }

   /**
    * Reads a combined date and time value expressed in tenths of a minute.
    *
    * @param data byte array of data
    * @param offset location of data as offset into the array
    * @return time value
    */
   public static final Date getTimestampFromTenths(byte[] data, int offset)
   {
      long ms = ((long) getInt(data, offset)) * 6000;
      return (DateHelper.getTimestampFromLong(EPOCH + ms));
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
   public static final String getUnicodeString(byte[] data, int offset)
   {
      int length = getUnicodeStringLengthInBytes(data, offset);
      return length == 0 ? "" : new String(data, offset, length, UTF16LE);
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
    * @param maxLength length in bytes of the string
    * @return string value
    */
   public static final String getUnicodeString(byte[] data, int offset, int maxLength)
   {
      int length = getUnicodeStringLengthInBytes(data, offset);
      if (maxLength > 0 && length > maxLength)
      {
         length = maxLength;
      }
      return length == 0 ? "" : new String(data, offset, length, UTF16LE);
   }

   /**
    * Determine the length of a nul terminated UTF16LE string in bytes.
    *
    * @param data string data
    * @param offset offset into string data
    * @return length in bytes
    */
   private static final int getUnicodeStringLengthInBytes(byte[] data, int offset)
   {
      int result;
      if (data == null || offset >= data.length)
      {
         result = 0;
      }
      else
      {
         result = data.length - offset;

         for (int loop = offset; loop < (data.length - 1); loop += 2)
         {
            if (data[loop] == 0 && data[loop + 1] == 0)
            {
               result = loop - offset;
               break;
            }
         }
      }
      return result;
   }

   /**
    * Reads a string of single byte characters from the input array.
    * This method assumes that the string finishes either at the
    * end of the array, or when char zero is encountered.
    * Reading begins at the supplied offset into the array.
    *
    * @param data byte array of data
    * @param offset offset into the array
    * @return string value
    */
   public static final String getString(byte[] data, int offset)
   {
      StringBuilder buffer = new StringBuilder();
      char c;

      for (int loop = 0; offset + loop < data.length; loop++)
      {
         c = (char) data[offset + loop];

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
   public static final Duration getDuration(int value, TimeUnit type)
   {
      return (getDuration((double) value, type));
   }

   /**
    * Reads a color value represented by three bytes, for R, G, and B
    * components, plus a flag byte indicating if this is an automatic color.
    * Returns null if the color type is "Automatic".
    *
    * @param data byte array of data
    * @param offset offset into array
    * @return new Color instance
    */
   public static final Color getColor(byte[] data, int offset)
   {
      Color result = null;

      if (getByte(data, offset + 3) == 0)
      {
         int r = getByte(data, offset);
         int g = getByte(data, offset + 1);
         int b = getByte(data, offset + 2);
         result = new Color(r, g, b);
      }

      return result;
   }

   /**
    * Reads a duration value. This method relies on the fact that
    * the units of the duration have been specified elsewhere.
    *
    * @param value Duration value
    * @param type type of units of the duration
    * @return Duration instance
    */
   public static final Duration getDuration(double value, TimeUnit type)
   {
      double duration;
      // Value is given in 1/10 of minute
      switch (type)
      {
         case MINUTES:
         case ELAPSED_MINUTES:
         {
            duration = value / 10;
            break;
         }

         case HOURS:
         case ELAPSED_HOURS:
         {
            duration = value / 600; // 60 * 10
            break;
         }

         case DAYS:
         {
            duration = value / 4800; // 8 * 60 * 10
            break;
         }

         case ELAPSED_DAYS:
         {
            duration = value / 14400; // 24 * 60 * 10
            break;
         }

         case WEEKS:
         {
            duration = value / 24000; // 5 * 8 * 60 * 10
            break;
         }

         case ELAPSED_WEEKS:
         {
            duration = value / 100800; // 7 * 24 * 60 * 10
            break;
         }

         case MONTHS:
         {
            duration = value / 96000; //
            break;
         }

         case ELAPSED_MONTHS:
         {
            duration = value / 432000; // 30 * 24 * 60 * 10
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
   public static final TimeUnit getDurationTimeUnits(int type)
   {
      return getDurationTimeUnits(type, null);
   }

   /**
    * This method converts between the duration units representation
    * used in the MPP file, and the standard MPX duration units.
    * If the supplied units are unrecognised, the units default to days.
    *
    * @param type MPP units
    * @param projectDefaultDurationUnits default duration units for this project
    * @return MPX units
    */
   public static final TimeUnit getDurationTimeUnits(int type, TimeUnit projectDefaultDurationUnits)
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

         case 19:
         {
            units = TimeUnit.PERCENT;
            break;
         }

         case 7:
         {
            units = TimeUnit.DAYS;
            break;
         }

         case 21:
         {
            units = projectDefaultDurationUnits == null ? TimeUnit.DAYS : projectDefaultDurationUnits;
            break;
         }

         default:
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
    * @param properties project properties
    * @param duration duration length
    * @param timeUnit duration units
    * @return Duration instance
    */
   public static Duration getAdjustedDuration(ProjectProperties properties, int duration, TimeUnit timeUnit)
   {
      Duration result = null;

      if (duration != -1)
      {
         switch (timeUnit)
         {
            case DAYS:
            {
               double unitsPerDay = properties.getMinutesPerDay().doubleValue() * 10d;
               double totalDays = 0;
               if (unitsPerDay != 0)
               {
                  totalDays = duration / unitsPerDay;
               }
               result = Duration.getInstance(totalDays, timeUnit);
               break;
            }

            case ELAPSED_DAYS:
            {
               double unitsPerDay = 24d * 600d;
               double totalDays = duration / unitsPerDay;
               result = Duration.getInstance(totalDays, timeUnit);
               break;
            }

            case WEEKS:
            {
               double unitsPerWeek = properties.getMinutesPerWeek().doubleValue() * 10d;
               double totalWeeks = 0;
               if (unitsPerWeek != 0)
               {
                  totalWeeks = duration / unitsPerWeek;
               }
               result = Duration.getInstance(totalWeeks, timeUnit);
               break;
            }

            case ELAPSED_WEEKS:
            {
               double unitsPerWeek = (60 * 24 * 7 * 10);
               double totalWeeks = duration / unitsPerWeek;
               result = Duration.getInstance(totalWeeks, timeUnit);
               break;
            }

            case MONTHS:
            {
               double unitsPerMonth = properties.getMinutesPerDay().doubleValue() * properties.getDaysPerMonth().doubleValue() * 10d;
               double totalMonths = 0;
               if (unitsPerMonth != 0)
               {
                  totalMonths = duration / unitsPerMonth;
               }
               result = Duration.getInstance(totalMonths, timeUnit);
               break;
            }

            case ELAPSED_MONTHS:
            {
               double unitsPerMonth = (60 * 24 * 30 * 10);
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
   public static TimeUnit getWorkTimeUnits(int value)
   {
      return TimeUnit.getInstance(value - 1);
   }

   /**
    * This method maps the currency symbol position from the
    * representation used in the MPP file to the representation
    * used by MPX.
    *
    * @param value MPP symbol position
    * @return MPX symbol position
    */
   public static CurrencySymbolPosition getSymbolPosition(int value)
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

         case 0:
         default:
         {
            result = CurrencySymbolPosition.BEFORE;
            break;
         }
      }

      return (result);
   }

   /**
    * Utility method to remove ampersands embedded in names.
    *
    * @param name name text
    * @return name text without embedded ampersands
    */
   public static final String removeAmpersands(String name)
   {
      if (name != null)
      {
         if (name.indexOf('&') != -1)
         {
            StringBuilder sb = new StringBuilder();
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
    * Utility method to read a percentage value.
    *
    * @param data data block
    * @param offset offset into data block
    * @return percentage value
    */
   public static final Double getPercentage(byte[] data, int offset)
   {
      int value = MPPUtility.getShort(data, offset);
      Double result = null;
      if (value >= 0 && value <= 100)
      {
         result = NumberHelper.getDouble(value);
      }
      return result;
   }

   /**
    * This method allows a subsection of a byte array to be copied.
    *
    * @param data source data
    * @param offset offset into the source data
    * @param size length of the source data to copy
    * @return new byte array containing copied data
    */
   public static final byte[] cloneSubArray(byte[] data, int offset, int size)
   {
      byte[] newData = new byte[size];
      System.arraycopy(data, offset, newData, 0, size);
      return (newData);
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
   public static final String hexdump(byte[] buffer, int offset, int length, boolean ascii)
   {
      StringBuilder sb = new StringBuilder();

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
               c = (char) buffer[loop];

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
   public static final String hexdump(byte[] buffer, boolean ascii)
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
   public static final String hexdump(byte[] buffer, boolean ascii, int columns, String prefix)
   {
      StringBuilder sb = new StringBuilder();
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

            sb.append(prefix);
            sb.append(df.format(index));
            sb.append(":");
            sb.append(hexdump(buffer, index, columns, ascii));
            sb.append('\n');

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
   public static final String hexdump(byte[] buffer, int offset, int length, boolean ascii, int columns, String prefix)
   {
      StringBuilder sb = new StringBuilder();
      if (buffer != null)
      {
         int index = offset;
         DecimalFormat df = new DecimalFormat("00000");

         while (index < (offset + length))
         {
            if (index + columns > (offset + length))
            {
               columns = (offset + length) - index;
            }

            sb.append(prefix);
            sb.append(df.format(index - offset));
            sb.append(":");
            sb.append(hexdump(buffer, index, columns, ascii));
            sb.append('\n');

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
   public static final void fileHexDump(String fileName, byte[] data)
   {
      System.out.println("FILE HEX DUMP");
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
   public static final void fileHexDump(String fileName, InputStream is)
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
   public static final void fileDump(String fileName, byte[] data)
   {
      System.out.println("FILE DUMP");
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
    * Dump out all the possible variables within the given data block.
    *
    * @param properties project properties
    * @param data data to dump from
    * @param dumpShort true to dump all the data as shorts
    * @param dumpInt true to dump all the data as ints
    * @param dumpDouble true to dump all the data as Doubles
    * @param dumpTimeStamp true to dump all the data as TimeStamps
    * @param dumpDuration true to dump all the data as Durations (long)
    * @param dumpDate true to dump all the data as Dates
    * @param dumpTime true to dump all the data as Dates (time)
    * @param dumpAdjustedDuration true to dump all data as adjusted durations
    */
   public static final void dataDump(ProjectProperties properties, byte[] data, boolean dumpShort, boolean dumpInt, boolean dumpDouble, boolean dumpTimeStamp, boolean dumpDuration, boolean dumpDate, boolean dumpTime, boolean dumpAdjustedDuration)
   {
      System.out.println("DATA");

      if (data != null)
      {
         System.out.println(MPPUtility.hexdump(data, false, 16, ""));

         for (int i = 0; i < data.length; i++)
         {
            if (dumpShort)
            {
               try
               {
                  int sh = MPPUtility.getShort(data, i);
                  System.out.println(i + ":" + sh);
               }
               catch (Exception ex)
               {
                  // Silently ignore exceptions
               }
            }
            if (dumpInt)
            {
               try
               {
                  int sh = MPPUtility.getInt(data, i);
                  System.out.println(i + ":" + sh);
               }
               catch (Exception ex)
               {
                  // Silently ignore exceptions
               }
            }
            if (dumpDouble)
            {
               try
               {
                  double d = MPPUtility.getDouble(data, i);
                  System.out.println(i + ":" + d);
               }
               catch (Exception ex)
               {
                  // Silently ignore exceptions
               }
            }
            if (dumpTimeStamp)
            {
               try
               {
                  Date d = MPPUtility.getTimestamp(data, i);
                  if (d != null)
                  {
                     System.out.println(i + ":" + d.toString());
                  }
               }
               catch (Exception ex)
               {
                  // Silently ignore exceptions
               }
            }
            if (dumpDuration)
            {
               try
               {
                  long d = MPPUtility.getDuration(data, i);
                  System.out.println(i + ":" + d);
               }
               catch (Exception ex)
               {
                  // Silently ignore exceptions
               }
            }
            if (dumpDate)
            {
               try
               {
                  Date d = MPPUtility.getDate(data, i);
                  if (d != null)
                  {
                     System.out.println(i + ":" + d.toString());
                  }
               }
               catch (Exception ex)
               {
                  // Silently ignore exceptions
               }
            }
            if (dumpTime)
            {
               try
               {
                  Date d = MPPUtility.getTime(data, i);
                  System.out.println(i + ":" + d.toString());
               }
               catch (Exception ex)
               {
                  // Silently ignore exceptions
               }
            }
            if (dumpAdjustedDuration)
            {
               try
               {
                  System.out.println(i + ":" + MPPUtility.getAdjustedDuration(properties, MPPUtility.getInt(data, i), TimeUnit.DAYS));
               }
               catch (Exception ex)
               {
                  // Silently ignore exceptions
               }
            }

         }
      }
   }

   /**
    * Dump out all the possible variables within the given data block.
    *
    * @param data data to dump from
    * @param id unique ID
    * @param dumpShort true to dump all the data as shorts
    * @param dumpInt true to dump all the data as ints
    * @param dumpDouble true to dump all the data as Doubles
    * @param dumpTimeStamp true to dump all the data as TimeStamps
    * @param dumpUnicodeString true to dump all the data as Unicode strings
    * @param dumpString true to dump all the data as strings
    */
   public static final void varDataDump(Var2Data data, Integer id, boolean dumpShort, boolean dumpInt, boolean dumpDouble, boolean dumpTimeStamp, boolean dumpUnicodeString, boolean dumpString)
   {
      System.out.println("VARDATA");
      for (int i = 0; i < 500; i++)
      {
         if (dumpShort)
         {
            try
            {
               int sh = data.getShort(id, Integer.valueOf(i));
               System.out.println(i + ":" + sh);
            }
            catch (Exception ex)
            {
               // Silently ignore exceptions
            }
         }
         if (dumpInt)
         {
            try
            {
               int sh = data.getInt(id, Integer.valueOf(i));
               System.out.println(i + ":" + sh);
            }
            catch (Exception ex)
            {
               // Silently ignore exceptions
            }
         }
         if (dumpDouble)
         {
            try
            {
               double d = data.getDouble(id, Integer.valueOf(i));
               System.out.println(i + ":" + d);
               System.out.println(i + ":" + d / 60000);
            }
            catch (Exception ex)
            {
               // Silently ignore exceptions
            }
         }
         if (dumpTimeStamp)
         {
            try
            {
               Date d = data.getTimestamp(id, Integer.valueOf(i));
               if (d != null)
               {
                  System.out.println(i + ":" + d.toString());
               }
            }
            catch (Exception ex)
            {
               // Silently ignore exceptions
            }
         }
         if (dumpUnicodeString)
         {
            try
            {
               String s = data.getUnicodeString(id, Integer.valueOf(i));
               if (s != null)
               {
                  System.out.println(i + ":" + s);
               }
            }
            catch (Exception ex)
            {
               // Silently ignore exceptions
            }
         }
         if (dumpString)
         {
            try
            {
               String s = data.getString(id, Integer.valueOf(i));
               if (s != null)
               {
                  System.out.println(i + ":" + s);
               }
            }
            catch (Exception ex)
            {
               // Silently ignore exceptions
            }
         }
      }
   }

   /**
    * Dumps the contents of a structured block made up from a header
    * and fixed sized records.
    *
    * @param headerSize header zie
    * @param blockSize block size
    * @param data data block
    */
   public static void dumpBlockData(int headerSize, int blockSize, byte[] data)
   {
      if (data != null)
      {
         System.out.println(MPPUtility.hexdump(data, 0, headerSize, false));
         int index = headerSize;
         while (index < data.length)
         {
            System.out.println(MPPUtility.hexdump(data, index, blockSize, false));
            index += blockSize;
         }
      }
   }

   /**
    * Get the epoch date.
    *
    * @return epoch date.
    */
   public static Date getEpochDate()
   {
      return EPOCH_DATE;
   }

   /**
    * Epoch date for MPP date calculation is 31/12/1983. This constant
    * is that date expressed in milliseconds using the Java date epoch.
    */
   private static final long EPOCH = 441676800000L;

   /**
    * Epoch Date as a Date instance.
    */
   private static Date EPOCH_DATE = DateHelper.getTimestampFromLong(EPOCH);

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
   private static final char[] HEX_DIGITS =
   {
      '0',
      '1',
      '2',
      '3',
      '4',
      '5',
      '6',
      '7',
      '8',
      '9',
      'A',
      'B',
      'C',
      'D',
      'E',
      'F'
   };

   /**
    * Mask used to remove flags from the duration units field.
    */
   private static final int DURATION_UNITS_MASK = 0x1F;

   private static final Charset UTF16LE = Charset.forName("UTF-16LE");
}
