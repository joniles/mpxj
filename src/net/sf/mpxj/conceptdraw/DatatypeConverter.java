/*
 * file:       DatatypeConverter.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2015
 * date:       28/11/2015
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
import net.sf.mpxj.ResourceType;
import net.sf.mpxj.TimeUnit;

/**
 * This class contains methods used to perform the datatype conversions
 * required to read and write ConceptDraw PROJECT files.
 */
public final class DatatypeConverter
{
   /**
    * Retrieve an integer in the form required by Phoenix.
    *
    * @param value integer value
    * @return formatted integer
    */
   public static final String printInteger(Integer value)
   {
      return value == null ? null : value.toString();
   }

   /**
    * Convert the Phoenix representation of an integer into a Java Integer instance.
    *
    * @param value Phoenix integer
    * @return Java Integer instance
    */
   public static final Integer parseInteger(String value)
   {
      return Integer.valueOf(value);
   }

   public static final Integer parseMinutesFromHours(String value)
   {
      Integer result = null;
      if (value != null)
      {
         result = Integer.valueOf(Integer.parseInt(value) * 60);
      }
      return result;
   }

   public static final String printHoursFromMinutes(Integer value)
   {
      return value == null ? null : Integer.toString(value.intValue() / 60);
   }

   public static final String printCurrencySymbolPosition(CurrencySymbolPosition value)
   {
      return MAP_FROM_CURRENCY_SYMBOL_POSITION.get(value);
   }

   public static final CurrencySymbolPosition parseCurrencySymbolPosition(String value)
   {
      CurrencySymbolPosition result = MAP_TO_CURRENCY_SYMBOL_POSITION.get(value);
      result = result == null ? CurrencySymbolPosition.BEFORE_WITH_SPACE : result;
      return result;
   }

   public static final String printDay(Day value)
   {
      return Integer.toString(value.getValue() - 1);
   }

   public static final Day parseDay(String value)
   {
      return Day.getInstance(Integer.parseInt(value) + 1);
   }

   public static final String printTime(Date value)
   {
      String result = null;
      if (value != null)
      {
         result = getTimeFormat().format(value);
      }
      return result;
   }

   public static final Date parseTime(String value)
   {
      Date result = null;

      try
      {
         if (value != null && !value.isEmpty())
         {
            result = getTimeFormat().parse(value);
         }
      }
      catch (ParseException ex)
      {
         // Ignore
      }

      return result;
   }

   public static final String printDate(Date value)
   {
      String result = null;
      if (value != null)
      {
         result = getDateFormat().format(value);
      }
      return result;
   }

   public static final Date parseDate(String value)
   {
      Date result = null;

      try
      {
         if (value != null && !value.isEmpty())
         {
            result = getDateFormat().parse(value);
         }
      }
      catch (ParseException ex)
      {
         // Ignore
      }

      return result;
   }

   public static final String printDateTime(Date value)
   {
      String result = null;
      if (value != null)
      {
         result = getDateTimeFormat().format(value);
      }
      return result;
   }

   public static final Date parseDateTime(String value)
   {
      Date result = null;

      try
      {
         if (value != null && !value.isEmpty())
         {
            result = getDateTimeFormat().parse(value);
         }
      }
      catch (ParseException ex)
      {
         // Ignore
      }

      return result;
   }

   public static final String printTimeUnit(TimeUnit value)
   {
      throw new UnsupportedOperationException();
   }

   public static final TimeUnit parseTimeUnit(String value)
   {
      return MAP_TO_TIME_UNIT.get(value);
   }

   public static final String printResourceType(ResourceType value)
   {
      throw new UnsupportedOperationException();
   }

   public static final ResourceType parseResourceType(String value)
   {
      return MAP_TO_RESOURCE_TYPE.get(value);
   }

   public static final String printPriority(Priority value)
   {
      throw new UnsupportedOperationException();
   }

   public static final Priority parsePriority(String value)
   {
      return MAP_TO_PRIORITY.get(value);
   }

   /**
    * Retrieve a time formatter.
    *
    * @return DateFormat instance
    */
   private static final DateFormat getTimeFormat()
   {
      DateFormat df = TIME_FORMAT.get();
      if (df == null)
      {
         df = new SimpleDateFormat("HH:mm:ss");
         df.setLenient(false);
         TIME_FORMAT.set(df);
      }
      return (df);

   }

   /**
    * Retrieve a date formatter.
    *
    * @return DateFormat instance
    */
   private static final DateFormat getDateFormat()
   {
      DateFormat df = DATE_FORMAT.get();
      if (df == null)
      {
         df = new SimpleDateFormat("yyyy-MM-dd");
         df.setLenient(false);
         DATE_FORMAT.set(df);
      }
      return (df);

   }

   private static final DateFormat getDateTimeFormat()
   {
      DateFormat df = DATE_TIME_FORMAT.get();
      if (df == null)
      {
         df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
         df.setLenient(false);
         DATE_TIME_FORMAT.set(df);
      }
      return (df);

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

   private static final ThreadLocal<DateFormat> TIME_FORMAT = new ThreadLocal<DateFormat>();
   private static final ThreadLocal<DateFormat> DATE_FORMAT = new ThreadLocal<DateFormat>();
   private static final ThreadLocal<DateFormat> DATE_TIME_FORMAT = new ThreadLocal<DateFormat>();
   // TODO: check thread locals everywhere!
}
