/*
 * file:       MPDUtility.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2007
 * date:       02-Feb-2006
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

package net.sf.mpxj.mpd;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.mpxj.CurrencySymbolPosition;
import net.sf.mpxj.Duration;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.TimeUnit;

/**
 * This class implements common utility methods used when processing
 * MPD files.
 */
public final class MPDUtility
{
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
    * This method converts between the duration units representation
    * used in the MPP file, and the standard MPX duration units.
    * If the supplied units are unrecognised, the units default to days.
    *
    * @param type MPP units
    * @return MPX units
    */
   public static final TimeUnit getDurationTimeUnits(int type)
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
   public static Duration getAdjustedDuration(ProjectFile file, int duration, TimeUnit timeUnit)
   {
      Duration result;
      switch (timeUnit)
      {
         case MINUTES:
         case ELAPSED_MINUTES:
         {
            double totalMinutes = duration / 10d;
            result = Duration.getInstance(totalMinutes, timeUnit);
            break;
         }

         case HOURS:
         case ELAPSED_HOURS:
         {
            double totalHours = duration / 600d;
            result = Duration.getInstance(totalHours, timeUnit);
            break;
         }

         case DAYS:
         {
            double unitsPerDay = file.getProjectProperties().getMinutesPerDay().doubleValue() * 10d;
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
            double unitsPerWeek = file.getProjectProperties().getMinutesPerWeek().doubleValue() * 10d;
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

         case ELAPSED_MONTHS:
         {
            double unitsPerMonth = (60 * 24 * 30 * 10);
            double totalMonths = duration / unitsPerMonth;
            result = Duration.getInstance(totalMonths, timeUnit);
            break;
         }

         case MONTHS:
         {
            double totalMonths = duration / 96000d;
            result = Duration.getInstance(totalMonths, timeUnit);
            break;
         }

         default:
         {
            result = Duration.getInstance(duration, timeUnit);
            break;
         }
      }

      return (result);
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
            duration = value / 96000; // 4 * 5 * 8 * 60 * 10
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
    * Dump the contents of a row from an MPD file.
    *
    * @param row row data
    */
   public static void dumpRow(Map<String, Object> row)
   {
      for (Entry<String, Object> entry : row.entrySet())
      {
         Object value = entry.getValue();
         System.out.println(entry.getKey() + " = " + value + " ( " + (value == null ? "" : value.getClass().getName()) + ")");
      }
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
    * Writes a hex dump to a file for a large byte array.
    *
    * @param fileName output file name
    * @param data target data
    */
   public static final void fileHexDump(String fileName, byte[] data)
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

}
