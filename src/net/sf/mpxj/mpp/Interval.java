/*
 * file:       Interval.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2005
 * date:       22 July 2005
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

import net.sf.mpxj.utility.MpxjEnum;

/**
 * This class represents daily, weekly or monthly time intervals.
 */
public final class Interval implements MpxjEnum
{
   /**
    * Private constructor.
    *
    * @param value interval type
    */
   private Interval (int value)
   {
      m_value = value;
   }

   /**
    * Retrieve an instance of this type based data from MS Project.
    *
    * @param value interval type
    * @return Interval instance
    */
   public static Interval getInstance (int value)
   {
      Interval interval;

      if (value < 0 || value >= INTERVAL_TYPES.length)
      {
         interval = DAILY;
      }
      else
      {
         interval = INTERVAL_TYPES[value];
      }

      return (interval);
   }

   /**
    * Retrieve the interval name. Currently this is not localised.
    *
    * @return interval name
    */
   public String getName ()
   {
      return (INTERVAL_NAMES[m_value]);
   }

   /**
    * Retrieve the String representation of this line style.
    *
    * @return String representation of this line style
    */
   @Override public String toString ()
   {
      return (getName());
   }

   /**
    * Retrieve the value associated with this instance.
    *
    * @return int value
    */
   public int getValue ()
   {
      return (m_value);
   }

   public static final int DAILY_VALUE = 0;
   public static final int WEEKLY_VALUE = 1;
   public static final int MONTHLY_VALUE = 2;

   public static final Interval DAILY = new Interval (DAILY_VALUE);
   public static final Interval WEEKLY = new Interval (WEEKLY_VALUE);
   public static final Interval MONTHLY = new Interval (MONTHLY_VALUE);

   private static final Interval[] INTERVAL_TYPES =
   {
      DAILY,
      WEEKLY,
      MONTHLY
   };

   private static final String[] INTERVAL_NAMES =
   {
      "Daily",
      "Weekly",
      "Monthly"
   };

   private int m_value;
}
