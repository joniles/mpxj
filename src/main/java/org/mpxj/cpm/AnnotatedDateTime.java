/*
 * file:       AnnotatedDateTime.java
 * author:     Jon Iles
 * date:       2025-04-02
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

package org.mpxj.cpm;

import java.time.LocalDateTime;

/**
 * This class is a wrapper around LocalDateTime allowing a flag
 * to be associated with the date indicating if it is actual
 * or planned.
 */
final class AnnotatedDateTime
{
   /**
    * Factory method for a planned value.
    *
    * @param value date time value
    * @return new AnnotatedDateTime instance
    */
   public static AnnotatedDateTime from(LocalDateTime value)
   {
      if (value == null)
      {
         return null;
      }

      return new AnnotatedDateTime(value, false);
   }

   /**
    * Factory method for an actual value.
    *
    * @param value date time value
    * @return new AnnotatedDateTime instance
    */
   public static AnnotatedDateTime fromActual(LocalDateTime value)
   {
      if (value == null)
      {
         return null;
      }

      return new AnnotatedDateTime(value, true);
   }

   /**
    * Private constructor.
    *
    * @param value date time value
    * @param actual true if this is an actual value
    */
   private AnnotatedDateTime(LocalDateTime value, boolean actual)
   {
      m_value = value;
      m_actual = actual;
   }

   /**
    * Retrieve the date time value.
    *
    * @return date time value
    */
   public LocalDateTime getValue()
   {
      return m_value;
   }

   /**
    * Determines if this is an actual date time value.
    *
    * @return true if this instances represents an actual date time value.
    */
   public boolean isActual()
   {
      return m_actual;
   }

   /**
    * Determine if the value represented by this instance is before another AnnotatedDateTime instance value.
    *
    * @param value value to compare
    * @return true if this instance is before the supplied instance
    */
   public boolean isBefore(AnnotatedDateTime value)
   {
      return m_value.isBefore(value.m_value);
   }

   /**
    * Determine if the value represented by this instance is after another AnnotatedDateTime instance value.
    *
    * @param value value to compare
    * @return true if this instance is after the supplied instance
    */
   public boolean isAfter(AnnotatedDateTime value)
   {
      return m_value.isAfter(value.m_value);
   }

   /**
    * Determine if the value represented by this instance is before a date time value.
    *
    * @param value value to compare
    * @return true if this instance is before the supplied value
    */
   public boolean isBefore(LocalDateTime value)
   {
      return m_value.isBefore(value);
   }

   /**
    * Determine if the value represented by this instance is after a date time value.
    *
    * @param value value to compare
    * @return true if this instance is after the supplied value
    */
   public boolean isAfter(LocalDateTime value)
   {
      return m_value.isAfter(value);
   }

   @Override public String toString()
   {
      return m_value + (m_actual ? "A" : "");
   }

   private final LocalDateTime m_value;
   private final boolean m_actual;
}
