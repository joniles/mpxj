/*
 * file:       DatatypeConverter.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2018
 * date:       9 July 2018
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

package net.sf.mpxj.conceptdraw;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.sf.mpxj.CurrencySymbolPosition;
import net.sf.mpxj.Day;
import net.sf.mpxj.Priority;
import net.sf.mpxj.RelationType;
import net.sf.mpxj.ResourceType;
import net.sf.mpxj.TaskType;
import net.sf.mpxj.TimeUnit;

/**
 * This class contains methods used to perform the datatype conversions
 * required to read and write ConceptDraw PROJECT files.
 */
public final class DatatypeConverter
{
   /**
    * Parse an integer value.
    *
    * @param value string representation
    * @return Integer instance
    */
   public static final Integer parseInteger(String value)
   {
      return Integer.valueOf(value);
   }

   /**
    * Print an integer value.
    *
    * @param value integer value
    * @return string representation
    */
   public static final String printInteger(Integer value)
   {
      throw new UnsupportedOperationException();
   }

   /**
    * Parse a double value.
    *
    * @param value String representation.
    * @return Double instance
    */
   public static final Double parseDouble(String value)
   {
      return Double.valueOf(value);
   }

   /**
    * Print a double value.
    * @param value Double instance
    * @return string representation.
    */
   public static final String printDouble(Double value)
   {
      throw new UnsupportedOperationException();
   }

   /**
    * Parse a percentage value.
    *
    * @param value String representation
    * @return Double instance
    */
   public static final Double parsePercent(String value)
   {
      return Double.valueOf(Double.parseDouble(value) * 100.0);
   }

   /**
    * Print a percentage value.
    *
    * @param value Double instance
    * @return String representation
    */
   public static final String printPercent(Double value)
   {
      throw new UnsupportedOperationException();
   }

   /**
    * Parse a duration in minutes form a number of hours.
    *
    * @param value String representation
    * @return Integer instance
    */
   public static final Integer parseMinutesFromHours(String value)
   {
      Integer result = null;
      if (value != null)
      {
         result = Integer.valueOf(Integer.parseInt(value) * 60);
      }
      return result;
   }

   /**
    * Print a duration in hours from a number of minutes.
    *
    * @param value String representation
    * @return String representation
    */
   public static final String printHoursFromMinutes(Integer value)
   {
      throw new UnsupportedOperationException();
   }

   /**
    * Parse a currency symbol position from a string representation.
    *
    * @param value String representation
    * @return CurrencySymbolPosition instance
    */
   public static final CurrencySymbolPosition parseCurrencySymbolPosition(String value)
   {
      CurrencySymbolPosition result = MAP_TO_CURRENCY_SYMBOL_POSITION.get(value);
      result = result == null ? CurrencySymbolPosition.BEFORE_WITH_SPACE : result;
      return result;
   }

   /**
    * Print a currency symbol position.
    *
    * @param value CurrencySymbolPosition instance
    * @return String representation
    */
   public static final String printCurrencySymbolPosition(CurrencySymbolPosition value)
   {
      throw new UnsupportedOperationException();
   }

   /**
    * Parse a Day.
    *
    * @param value String representation
    * @return Day instance
    */
   public static final Day parseDay(String value)
   {
      return Day.getInstance(Integer.parseInt(value) + 1);
   }

   /**
    * Print a Day.
    *
    * @param value Day instance
    * @return String representation
    */
   public static final String printDay(Day value)
   {
      throw new UnsupportedOperationException();
   }

   /**
    * Parse a time value.
    *
    * @param value String representation
    * @return Date instance
    */
   public static final Date parseTime(String value)
   {
      Date result = null;

      try
      {
         if (value != null && !value.isEmpty())
         {
            result = TIME_FORMAT.get().parse(value);
         }
      }
      catch (ParseException ex)
      {
         // Ignore
      }

      return result;
   }

   /**
    * Print a time value.
    *
    * @param value Date instance
    * @return String representation
    */
   public static final String printTime(Date value)
   {
      throw new UnsupportedOperationException();
   }

   /**
    * Parse a date value.
    *
    * @param value String representation
    * @return Date instance
    */
   public static final Date parseDate(String value)
   {
      Date result = null;

      try
      {
         if (value != null && !value.isEmpty())
         {
            result = DATE_FORMAT.get().parse(value);
         }
      }
      catch (ParseException ex)
      {
         // Ignore
      }

      return result;
   }

   /**
    * Print a date value.
    *
    * @param value Date instance
    * @return String representation
    */
   public static final String printDate(Date value)
   {
      throw new UnsupportedOperationException();
   }

   /**
    * Parse a date time value.
    *
    * @param value String representation
    * @return Date instance
    */
   public static final Date parseDateTime(String value)
   {
      Date result = null;

      try
      {
         if (value != null && !value.isEmpty())
         {
            result = DATE_TIME_FORMAT.get().parse(value);
         }
      }
      catch (ParseException ex)
      {
         // Ignore
      }

      return result;
   }

   /**
    * Print a date time value.
    *
    * @param value Date instance
    * @return String representation
    */
   public static final String printDateTime(Date value)
   {
      throw new UnsupportedOperationException();
   }

   /**
    * Parse a time unit value.
    *
    * @param value String representation
    * @return TimeUnit instance
    */
   public static final TimeUnit parseTimeUnit(String value)
   {
      return MAP_TO_TIME_UNIT.get(value);
   }

   /**
    * Print a time unit value.
    *
    * @param value TimeUnit instance
    * @return String representation
    */
   public static final String printTimeUnit(TimeUnit value)
   {
      throw new UnsupportedOperationException();
   }

   /**
    * Parse a resource type value.
    *
    * @param value String representation
    * @return ResourceType instance
    */
   public static final ResourceType parseResourceType(String value)
   {
      return MAP_TO_RESOURCE_TYPE.get(value);
   }

   /**
    * Print a resource type value.
    *
    * @param value ResourceType instance
    * @return String representation
    */
   public static final String printResourceType(ResourceType value)
   {
      throw new UnsupportedOperationException();
   }

   /**
    * Parse a priority value.
    *
    * @param value String representation
    * @return Priority instance
    */
   public static final Priority parsePriority(String value)
   {
      return MAP_TO_PRIORITY.get(value);
   }

   /**
    * Print a priority value.
    *
    * @param value Priority instance
    * @return String representation
    */
   public static final String printPriority(Priority value)
   {
      throw new UnsupportedOperationException();
   }

   /**
    * Parse a task type value.
    *
    * @param value String representation
    * @return TaskType instance
    */
   public static final TaskType parseTaskType(String value)
   {
      return MAP_TO_TASK_TYPE.get(value);
   }

   /**
    * Print a task type value.
    *
    * @param value TaskType instance
    * @return String representation
    */
   public static final String printTaskType(TaskType value)
   {
      throw new UnsupportedOperationException();
   }

   /**
    * Parse a relation type value.
    *
    * @param value String representation
    * @return RelationType instance
    */
   public static final RelationType parseRelationType(String value)
   {
      return MAP_TO_RELATION_TYPE.get(value);
   }

   /**
    * Print a relation type value.
    *
    * @param value RelationType instance
    * @return string representation
    */
   public static final String printRelationType(RelationType value)
   {
      throw new UnsupportedOperationException();
   }

   private static final Map<String, CurrencySymbolPosition> MAP_TO_CURRENCY_SYMBOL_POSITION = new HashMap<String, CurrencySymbolPosition>();
   static
   {
      MAP_TO_CURRENCY_SYMBOL_POSITION.put("0", CurrencySymbolPosition.BEFORE);
      MAP_TO_CURRENCY_SYMBOL_POSITION.put("1", CurrencySymbolPosition.AFTER);
      MAP_TO_CURRENCY_SYMBOL_POSITION.put("2", CurrencySymbolPosition.BEFORE_WITH_SPACE);
      MAP_TO_CURRENCY_SYMBOL_POSITION.put("3", CurrencySymbolPosition.AFTER_WITH_SPACE);
   }

   private static final Map<CurrencySymbolPosition, String> MAP_FROM_CURRENCY_SYMBOL_POSITION = new HashMap<CurrencySymbolPosition, String>();
   static
   {
      MAP_FROM_CURRENCY_SYMBOL_POSITION.put(CurrencySymbolPosition.BEFORE, "0");
      MAP_FROM_CURRENCY_SYMBOL_POSITION.put(CurrencySymbolPosition.AFTER, "1");
      MAP_FROM_CURRENCY_SYMBOL_POSITION.put(CurrencySymbolPosition.BEFORE_WITH_SPACE, "2");
      MAP_FROM_CURRENCY_SYMBOL_POSITION.put(CurrencySymbolPosition.AFTER_WITH_SPACE, "3");
   }

   private static final Map<String, TimeUnit> MAP_TO_TIME_UNIT = new HashMap<String, TimeUnit>();
   static
   {
      MAP_TO_TIME_UNIT.put("0", TimeUnit.MINUTES);
      MAP_TO_TIME_UNIT.put("1", TimeUnit.HOURS);
      MAP_TO_TIME_UNIT.put("2", TimeUnit.DAYS);
      MAP_TO_TIME_UNIT.put("3", TimeUnit.WEEKS);
      MAP_TO_TIME_UNIT.put("4", TimeUnit.MONTHS);
   }

   private static final Map<String, ResourceType> MAP_TO_RESOURCE_TYPE = new HashMap<String, ResourceType>();
   static
   {
      MAP_TO_RESOURCE_TYPE.put("0", ResourceType.MATERIAL);
      MAP_TO_RESOURCE_TYPE.put("1", ResourceType.WORK);
      MAP_TO_RESOURCE_TYPE.put("work", ResourceType.WORK);
      MAP_TO_RESOURCE_TYPE.put("material", ResourceType.MATERIAL);
      MAP_TO_RESOURCE_TYPE.put("cost", ResourceType.COST);
   }

   private static final Map<String, Priority> MAP_TO_PRIORITY = new HashMap<String, Priority>();
   static
   {
      MAP_TO_PRIORITY.put("veryLow", Priority.getInstance(Priority.LOWEST));
      MAP_TO_PRIORITY.put("low", Priority.getInstance(Priority.LOW));
      MAP_TO_PRIORITY.put("normal", Priority.getInstance(Priority.MEDIUM));
      MAP_TO_PRIORITY.put("high", Priority.getInstance(Priority.HIGH));
      MAP_TO_PRIORITY.put("veryHigh", Priority.getInstance(Priority.HIGHEST));
   }

   private static final Map<String, TaskType> MAP_TO_TASK_TYPE = new HashMap<String, TaskType>();
   static
   {
      MAP_TO_TASK_TYPE.put("fixedDuration", TaskType.FIXED_DURATION);
      MAP_TO_TASK_TYPE.put("fixedUnits", TaskType.FIXED_UNITS);
      MAP_TO_TASK_TYPE.put("fixedWork", TaskType.FIXED_WORK);
   }

   private static final Map<String, RelationType> MAP_TO_RELATION_TYPE = new HashMap<String, RelationType>();
   static
   {
      MAP_TO_RELATION_TYPE.put("0", RelationType.START_START);
      MAP_TO_RELATION_TYPE.put("1", RelationType.START_FINISH);
      MAP_TO_RELATION_TYPE.put("2", RelationType.FINISH_START);
      MAP_TO_RELATION_TYPE.put("3", RelationType.FINISH_FINISH);

   }

   private static final ThreadLocal<DateFormat> TIME_FORMAT = new ThreadLocal<DateFormat>()
   {
      @Override protected DateFormat initialValue()
      {
         DateFormat df = new SimpleDateFormat("HH:mm:ss");
         df.setLenient(false);
         return df;
      }
   };            

   private static final ThreadLocal<DateFormat> DATE_FORMAT = new ThreadLocal<DateFormat>()
   {
      @Override protected DateFormat initialValue()
      {
         DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
         df.setLenient(false);
         return df;
      }
   };            
   
   private static final ThreadLocal<DateFormat> DATE_TIME_FORMAT = new ThreadLocal<DateFormat>()
   {
      @Override protected DateFormat initialValue()
      {
         DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
         df.setLenient(false);
         return df;
      }
   };            
}
