/*
 * file:       TimescaleUnits.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2005
 * date:       Apr 7, 2005
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
   
   
   public static final TimescaleUnits NONE = new TimescaleUnits (-1);   
   public static final TimescaleUnits MINUTES = new TimescaleUnits (0);
   public static final TimescaleUnits HOURS = new TimescaleUnits (1);
   public static final TimescaleUnits DAYS = new TimescaleUnits (2);
   public static final TimescaleUnits WEEKS = new TimescaleUnits (3);
   public static final TimescaleUnits THIRDS_OF_MONTHS = new TimescaleUnits (4);
   public static final TimescaleUnits MONTHS = new TimescaleUnits (5);
   public static final TimescaleUnits QUARTERS = new TimescaleUnits (6);
   public static final TimescaleUnits HALF_YEARS = new TimescaleUnits (7);   
   public static final TimescaleUnits YEARS = new TimescaleUnits (8);

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
