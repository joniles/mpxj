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

package com.tapsterrock.mpp;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.tapsterrock.mpx.CurrencySymbolPosition;
import com.tapsterrock.mpx.MPXDuration;
import com.tapsterrock.mpx.TimeUnit;

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
      for (int loop=0; loop < size; loop++)
      {
         buffer[bufferOffset+loop] = data[offset+loop];
      }
   }

   /**
    * This method reads a single byte from the input array
    *
    * @param data byte array of data
    * @param offset offset of byte data in the array
    * @return byte value
    */
   public static final int getByte (byte[] data, int offset)
   {
      int result = data[offset] & 0x0F;
      result += ((data[offset] >> 4 & 0x0F) * 16);
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
      return (getByte (data, 0));
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
      result += ((data[offset] >> 4 & 0x0F) * 16);
      result += ((data[offset+1] & 0x0F) * 256);
      result += ((data[offset+1] >> 4 & 0x0F) * 4096);
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
      return (getShort (data, 0));
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
      result += ((data[offset] >> 4 & 0x0F) * 16);
      result += ((data[offset+1] & 0x0F) * 256);
      result += ((data[offset+1] >> 4 & 0x0F) * 4096);
      result += ((data[offset+2] & 0x0F) * 65536);
      result += ((data[offset+2] >> 4 & 0x0F) * 1048576);
      result += ((data[offset+3] & 0x0F) * 16777216);
      result += ((data[offset+3] >> 4 & 0x0F) * 268435456);
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
      return (getInt (data, 0));
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
      result += ((data[offset] >> 4 & 0x0F) * 16); // 1
      result += ((data[offset+1] & 0x0F) * 256); // 2
      result += ((data[offset+1] >> 4 & 0x0F) * 4096); // 3
      result += ((data[offset+2] & 0x0F) * 65536); // 4
      result += ((data[offset+2] >> 4 & 0x0F) * 1048576); // 5
      result += ((data[offset+3] & 0x0F) * 16777216); // 6
      result += ((data[offset+3] >> 4 & 0x0F) * 268435456); // 7
      result += ((data[offset+4] & 0x0F) * 4294967296L); // 8
      result += ((data[offset+4] >> 4 & 0x0F) * 68719476736L); // 9
      result += ((data[offset+5] & 0x0F) * 1099511627776L); // 10
      result += ((data[offset+5] >> 4 & 0x0F) * 17592186044416L); // 11
      result += ((data[offset+6] & 0x0F) * 281474976710656L); // 12
      result += ((data[offset+6] >> 4 & 0x0F) * 4503599627370496L); // 13
      result += ((data[offset+7] & 0x0F) * 72057594037927936L); // 14
      result += ((data[offset+7] >> 4 & 0x0F) * 1152921504606846976L); // 15
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
      result += ((data[offset] >> 4 & 0x0F) * 16); // 1
      result += ((data[offset+1] & 0x0F) * 256); // 2
      result += ((data[offset+1] >> 4 & 0x0F) * 4096); // 3
      result += ((data[offset+2] & 0x0F) * 65536); // 4
      result += ((data[offset+2] >> 4 & 0x0F) * 1048576); // 5
      result += ((data[offset+3] & 0x0F) * 16777216); // 6
      result += ((data[offset+3] >> 4 & 0x0F) * 268435456); // 7
      result += ((data[offset+4] & 0x0F) * 4294967296L); // 8
      result += ((data[offset+4] >> 4 & 0x0F) * 68719476736L); // 9
      result += ((data[offset+5] & 0x0F) * 1099511627776L); // 10
      result += ((data[offset+5] >> 4 & 0x0F) * 17592186044416L); // 11

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
      return (getLong6 (data, 0));
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
      return (getLong (data, 0));
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
      return (Double.longBitsToDouble(getLong (data, offset)));
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
      return (Double.longBitsToDouble(getLong (data, 0)));
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
      long days = getShort (data, offset);
      if (days == 65535)
      {
         result = null;
      }
      else
      {
         result = new Date (EPOCH + (days * MS_PER_DAY));
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
      int time = getShort (data, offset) / 10;
      Calendar cal = Calendar.getInstance();
      cal.set(Calendar.HOUR_OF_DAY, (time/60));
      cal.set(Calendar.MINUTE, (time%60));
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
      return ((getShort (data, offset) * MS_PER_MINUTE)/10);
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

      long days = getShort (data, offset+2);
      if (days == 65535)
      {
         result = null;
      }
      else
      {
         TimeZone tz = TimeZone.getDefault();
         long time = getShort (data, offset);
         result = new Date (EPOCH + (days * MS_PER_DAY) + ((time * MS_PER_MINUTE)/10) - tz.getRawOffset());
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

            result = new Date (result.getTime() - savings);
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
      return (getTimestamp (data, 0));
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
      StringBuffer buffer = new StringBuffer ();
      char c;

      for (int loop=offset; loop < data.length-1; loop += 2)
      {
         c = (char)getShort (data, loop);
         if (c == 0)
         {
            break;
         }
         buffer.append (c);
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
      StringBuffer buffer = new StringBuffer ();
      char c;

      for (int loop=0; loop < data.length; loop++)
      {
         c = (char)data[loop];
         if (c == 0)
         {
            break;
         }
         buffer.append (c);
      }

      return (buffer.toString());
   }

   /**
    * Reads a duration value. This method relies on the fact that
    * the units of the duration have been specified elsewhere.
    *
    * @param value Duration value
    * @param type type of units of the duration
    * @return MPXDuration instance
    */
   public static final MPXDuration getDuration (int value, TimeUnit type)
   {
      return (getDuration ((double)value, type));
   }

   /**
    * Reads a duration value. This method relies on the fact that
    * the units of the duration have been specified elsewhere.
    *
    * @param value Duration value
    * @param type type of units of the duration
    * @return MPXDuration instance
    */
   public static final MPXDuration getDuration (double value, TimeUnit type)
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

      return (new MPXDuration (duration, type));
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

         case 2:
         default:
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
      StringBuffer sb = new StringBuffer ();

      if (buffer != null)
      {
         char c;
         int loop;
         int count = offset+length;

         for (loop=offset; loop < count; loop++)
         {
            sb.append (" ");
            sb.append (HEX_DIGITS[(buffer[loop] & 0xF0) >> 4]);
            sb.append (HEX_DIGITS[buffer[loop] & 0x0F]);
         }

         if (ascii == true)
         {
            sb.append ("   ");

            for (loop=offset; loop < count; loop++)
            {
               c = (char)buffer[loop];
               if (c > 200 || c < 27)
               {
                  c = ' ';
               }

               sb.append (c);
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

      return (hexdump (buffer, 0, length, ascii));
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
    * Constants used to convert bytes to hex digits
    */
   private static final char[] HEX_DIGITS =
   {
      '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
      'A', 'B', 'C', 'D', 'E', 'F'
   };

   /**
    * Mask used to remove flags from the duration units field.
    */
   private static final int DURATION_UNITS_MASK = 0x1F;

   /**
    * Default value to use for DST savings if we are using a version
    * of Java < 1.4
    */
   private static final int DEFAULT_DST_SAVINGS = 3600000;

   /**
    * Flag used to indicate the existance of the getDSTSavings
    * method that was introduced in Java 1.4
    */
   private static boolean HAS_DST_SAVINGS;

   static
   {
      Class tz = TimeZone.class;

      try
      {
         tz.getMethod("getDSTSavings", null);
         HAS_DST_SAVINGS = true;
      }

      catch (NoSuchMethodException ex)
      {
         HAS_DST_SAVINGS = false;
      }
   }
}

