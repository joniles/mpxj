/*
 * file:       TimeUnit.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2002-2003
 * date:       03/01/2003
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

package org.mpxj;

import org.mpxj.common.EnumHelper;
import org.mpxj.common.NumberHelper;

/**
 * This class contains utility functions allowing time unit specifications
 * to be parsed and formatted.
 */
public enum TimeUnit implements MpxjEnum
{
   /**
    * Constant representing Minutes.
    */
   MINUTES(0, "m", false),

   /**
    * Constant representing Hours.
    */
   HOURS(1, "h", false),

   /**
    * Constant representing Days.
    */
   DAYS(2, "d", false),

   /**
    * Constant representing Weeks.
    */
   WEEKS(3, "w", false),

   /**
    * Constant representing Months.
    */
   MONTHS(4, "mo", false),

   /**
    * Constant representing Percent.
    */
   PERCENT(5, "%", false),

   /**
    * Constant representing Years.
    */
   YEARS(6, "y", false),

   /**
    * Constant representing Elapsed Minutes.
    */
   ELAPSED_MINUTES(7, "em", true),

   /**
    * Constant representing Elapsed Hours.
    */
   ELAPSED_HOURS(8, "eh", true),

   /**
    * Constant representing Elapsed Days.
    */
   ELAPSED_DAYS(9, "ed", true),

   /**
    * Constant representing Elapsed Weeks.
    */
   ELAPSED_WEEKS(10, "ew", true),

   /**
    * Constant representing Elapsed Months.
    */
   ELAPSED_MONTHS(11, "emo", true),

   /**
    * Constant representing Elapsed Years.
    */
   ELAPSED_YEARS(12, "ey", true),

   /**
    * Constant representing Elapsed Percent.
    */
   ELAPSED_PERCENT(13, "e%", true);

   /**
    * Private constructor.
    *
    * @param type int version of the enum
    * @param name enum name
    * @param elapsed true if this is an elapsed duration
    */
   TimeUnit(int type, String name, boolean elapsed)
   {
      m_value = type;
      m_name = name;
      m_elapsed = elapsed;
   }

   /**
    * Retrieve an instance of the enum based on its int value.
    *
    * @param type int type
    * @return enum instance
    */
   public static TimeUnit getInstance(int type)
   {
      if (type < 0 || type >= TYPE_VALUES.length)
      {
         type = DAYS.getValue();
      }
      return (TYPE_VALUES[type]);
   }

   /**
    * Retrieve an instance of the enum based on its int value.
    *
    * @param type int type
    * @return enum instance
    */
   public static TimeUnit getInstance(Number type)
   {
      int value;
      if (type == null)
      {
         value = -1;
      }
      else
      {
         value = NumberHelper.getInt(type);
      }
      return (getInstance(value));
   }

   /**
    * Accessor method used to retrieve the numeric representation of the enum.
    *
    * @return int representation of the enum
    */
   @Override public int getValue()
   {
      return (m_value);
   }

   /**
    * Retrieve the name associated with this enum.
    *
    * @return name
    */
   public String getName()
   {
      return (m_name);
   }

   /**
    * Determine if this is an elapsed time unit.
    *
    * @return true if this is an elapsed time unit
    */
   public boolean isElapsed()
   {
      return m_elapsed;
   }

   @Override public String toString()
   {
      return (getName());
   }

   /**
    * Array mapping int types to enums.
    */
   private static final TimeUnit[] TYPE_VALUES = EnumHelper.createTypeArray(TimeUnit.class);

   /**
    * Internal representation of the enum int type.
    */
   private final int m_value;
   private final String m_name;
   private final boolean m_elapsed;
}
