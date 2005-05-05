/*
 * file:       TimescaleUnits.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2005
 * date:       Apr 7, 2005
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

/**
 * Class representing the units which may be shown on a Gantt chart timescale.
 */
public final class TimescaleUnits
{
   /**
    * Private constructor.
    * 
    * @param value units value from an MS Project file
    */
   private TimescaleUnits (int value)
   {
      m_value = value;
   }
   
   /**
    * Retrieve an instance of this class based on the data read from an
    * MS Project file.
    * 
    * @param value value from an MS Project file
    * @return instance of this class
    */
   public static TimescaleUnits getInstance (int value)
   {  
      TimescaleUnits result;
      
      if (value < 0 || value >= UNITS_ARRAY.length)
      {
         result = NONE;
      }
      else
      {
         result = UNITS_ARRAY[value];
      }
      
      return (result);
   }

   /**
    * Retrieve the name of this time unit. Note that this is not
    * localised.
    * 
    * @return name of this timescale unit
    */
   public String getName ()
   {
      String result;
      
      if (m_value == -1)
      {
         result = "None";
      }
      else
      {
         result = UNITS_NAMES[m_value];
      }
      
      return (result);
   }
   
   /**
    * Generate a string representation of this instance.
    * 
    * @return string representation of this instance
    */
   public String toString ()
   {
      return (getName());
   }
   
   public static final int NONE_VALUE = -1;
   public static final int MINUTES_VALUE = 0;
   public static final int HOURS_VALUE = 1;
   public static final int DAYS_VALUE = 2;
   public static final int WEEKS_VALUE = 3;
   public static final int THIRDS_OF_MONTHS_VALUE = 4;
   public static final int MONTHS_VALUE = 5;
   public static final int QUARTERS_VALUE = 6;
   public static final int HALF_YEARS_VALUE = 7;
   public static final int YEARS_VALUE = 8;
   
   public static final TimescaleUnits NONE = new TimescaleUnits (NONE_VALUE);   
   public static final TimescaleUnits MINUTES = new TimescaleUnits (MINUTES_VALUE);
   public static final TimescaleUnits HOURS = new TimescaleUnits (HOURS_VALUE);
   public static final TimescaleUnits DAYS = new TimescaleUnits (DAYS_VALUE);
   public static final TimescaleUnits WEEKS = new TimescaleUnits (WEEKS_VALUE);
   public static final TimescaleUnits THIRDS_OF_MONTHS = new TimescaleUnits (THIRDS_OF_MONTHS_VALUE);
   public static final TimescaleUnits MONTHS = new TimescaleUnits (MONTHS_VALUE);
   public static final TimescaleUnits QUARTERS = new TimescaleUnits (QUARTERS_VALUE);
   public static final TimescaleUnits HALF_YEARS = new TimescaleUnits (HALF_YEARS_VALUE);   
   public static final TimescaleUnits YEARS = new TimescaleUnits (YEARS_VALUE);

   private static final TimescaleUnits[] UNITS_ARRAY =
   {
      MINUTES,
      HOURS,
      DAYS,
      WEEKS,
      THIRDS_OF_MONTHS,
      MONTHS,
      QUARTERS,
      HALF_YEARS,
      YEARS
   };
   
   private static final String[] UNITS_NAMES = 
   {
      "Minutes",
      "Hours",
      "Days",
      "Weeks",
      "Thirds of Months",
      "Months",
      "Quarters",
      "Half Years",
      "Years"
   };
   
   private int m_value;
}
