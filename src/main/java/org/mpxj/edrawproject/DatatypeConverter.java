/*
 * file:       DatatypeConverter.java
 * author:     Jon Iles
 * date:       2025-05-06
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

package org.mpxj.edrawproject;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.mpxj.common.NumberHelper;

/**
 * This class contains methods used to perform the datatype conversions
 * required to read Edraw Project EDPX files.
 */
public class DatatypeConverter
{
   /**
    * Parse a Boolean value.
    *
    * @param value string representation
    * @return Boolean value
    */
   public static final Boolean parseBoolean(String value)
   {
      return (value == null || value.charAt(0) != '1' || value.equalsIgnoreCase("false") ? Boolean.FALSE : Boolean.TRUE);
   }

   /**
    * Print a Boolean value.
    *
    * @param value Boolean value
    * @return string representation
    */
   public static final String printBoolean(Boolean value)
   {
      throw new UnsupportedOperationException();
   }

   /**
    * Parse a timestamp.
    *
    * @param value string representation
    * @return LocalDateTime instance
    */
   public static final LocalDateTime parseTimestamp(String value)
   {
      LocalDateTime result = null;

      if (value != null && !value.isEmpty())
      {
         try
         {
            result = LocalDateTime.parse(value, TIMESTAMP_FORMAT);
         }

         catch (DateTimeParseException ex)
         {
            // Ignore parse exception
         }
      }

      return (result);
   }

   /**
    * Print a timestamp.
    *
    * @param value LocalDateTime instance
    * @return string representation
    */
   public static final String printTimestamp(LocalDateTime value)
   {
      throw new UnsupportedOperationException();
   }

   /**
    * Parse a time value.
    *
    * @param value string representation.
    * @return LocalTime instance
    */
   public static final LocalTime parseTime(String value)
   {
      if (value == null || value.isEmpty())
      {
         return null;
      }

      try
      {
         return LocalTime.parse(value, TIME_FORMAT);
      }

      catch (DateTimeParseException ex)
      {
         // Ignore parse errors
         return null;
      }
   }

   /**
    * Print a time value.
    *
    * @param value LocalTime instance
    * @return string representation
    */
   public static final String printTime(LocalTime value)
   {
      throw new UnsupportedOperationException();
   }

   /**
    * Parse a double value.
    *
    * @param value string representation
    * @return Double instance
    */
   public static final Double parseDouble(String value)
   {
      return NumberHelper.getDoubleObject(value);
   }

   /**
    * Print a double value.
    *
    * @param value Double instance
    * @return string representation
    */
   public static final String printDouble(Double value)
   {
      throw new UnsupportedOperationException();
   }

   /**
    * Parse an integer value.
    *
    * @param value string representation
    * @return Integer instance
    */
   public static final Integer parseInteger(String value)
   {
      if (value == null || value.isEmpty())
      {
         return null;
      }

      return Integer.valueOf(value);
   }

   /**
    * Print an integer value.
    *
    * @param value Integer instance
    * @return string representation
    */
   public static final String printInteger(Integer value)
   {
      throw new UnsupportedOperationException();
   }

   private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

   private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");
}
