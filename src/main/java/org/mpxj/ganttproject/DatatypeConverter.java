/*
 * file:       DatatypeConverter.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2017
 * date:       28 December 2017
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

package org.mpxj.ganttproject;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * This class contains methods used to perform the datatype conversions
 * required to read GanttProject files.
 */
public final class DatatypeConverter
{

   /**
    * Parse a date value.
    *
    * @param value string representation
    * @return date value
    */
   public static final LocalDateTime parseDate(String value)
   {
      LocalDateTime result = null;

      if (value != null && !value.isEmpty())
      {
         try
         {
            result = LocalDate.parse(value, DATE_FORMAT).atStartOfDay();
         }

         catch (DateTimeParseException ex)
         {
            // Ignore parse exception
         }
      }

      return (result);
   }

   /**
    * Print a date value.
    *
    * @param value time value
    * @return time value
    */
   public static final String printDate(LocalDateTime value)
   {
      return value == null ? null : DATE_FORMAT.format(value);
   }

   private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
}
