/*
 * file:       DatatypeConverter.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2005
 * date:       Mar 30, 2005
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

package com.tapsterrock.mspdi;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.tapsterrock.mpx.AccrueType;
import com.tapsterrock.mpx.BookingType;
import com.tapsterrock.mpx.ConstraintType;
import com.tapsterrock.mpx.CurrencySymbolPosition;
import com.tapsterrock.mpx.Day;
import com.tapsterrock.mpx.EarnedValueMethod;
import com.tapsterrock.mpx.ExtendedAttributeContainer;
import com.tapsterrock.mpx.MPXCurrency;
import com.tapsterrock.mpx.MPXDuration;
import com.tapsterrock.mpx.MPXRate;
import com.tapsterrock.mpx.NumberUtility;
import com.tapsterrock.mpx.Priority;
import com.tapsterrock.mpx.ResourceType;
import com.tapsterrock.mpx.TaskType;
import com.tapsterrock.mpx.TimeUnit;
import com.tapsterrock.mpx.WorkContour;
import com.tapsterrock.mpx.WorkGroup;

/**
 * This class contains methods used to perform the datatype conversions
 * required to read and write MSPDI files.
 */
public final class DatatypeConverter
{   
   /**
    * Print an extended attribute currency value.
    * 
    * @param value currency value
    * @return string representation
    */
   private static final String printExtendedAttributeCurrency (Number value)
   {            
      return (getNumberFormat().format(value.doubleValue()*100));
   }

   /**
    * Parse an extended attribute currency value.
    * 
    * @param value string representation
    * @return currency value
    */
   private static final Number parseExtendedAttributeCurrency (String value)
   {
      return (new Double(Double.parseDouble(value)/100));      
   }
   
   /**
    * Print an extended attribute numeric value.
    * 
    * @param value numeric value
    * @return string representation
    */
   private static final String printExtendedAttributeNumber (Number value)
   {
      return (getNumberFormat().format(value.doubleValue()));
   }
   
   /**
    * Parse and extended attribute numeric value.
    * 
    * @param value string representation
    * @return numeric value
    */
   private static final Number parseExtendedAttributeNumber (String value)
   {
      return (new Double(value));
   }
   
   /**
    * Print an extended attribute boolean value.
    * 
    * @param value boolean value
    * @return string representation
    */
   private static final String printExtendedAttributeBoolean (Boolean value)
   {
      return(value.booleanValue()?"1":"0");      
   }

   /**
    * Parse an extended attribute boolean value.
    * 
    * @param value string representation
    * @return boolean value
    */
   private static final Boolean parseExtendedAttributeBoolean (String value)
   {
      return ((value.equals("1")?Boolean.TRUE:Boolean.FALSE));
   }
   
   /**
    * Print an extended attribute date value.
    * 
    * @param value date value
    * @return string representation
    */
   private static final String printExtendedAttributeDate (Date value)
   {
      return (getDateFormat().format(value));
   }

   /**
    * Parse an extended attribute date value.
    * 
    * @param value string representation
    * @return date value
    */
   private static final Date parseExtendedAttributeDate (String value)
   {
      Date result;

      try
      {
         result = getDateFormat().parse(value);
      }

      catch (ParseException ex)
      {
         result = null;
      }

      return (result);

   }

   /**
    * Print an extended attribute value.
    * 
    * @param file parent file
    * @param value attribute value
    * @return string representation
    */
   public static final String printExtendedAttribute (MSPDIFile file, Object value)
   {            
      String result;
      
      if (value instanceof Date)
      {
         result = printExtendedAttributeDate((Date)value);
      }
      else
      {
         if (value instanceof Boolean)
         {
            result = printExtendedAttributeBoolean((Boolean)value);
         }
         else
         {
            if (value instanceof MPXDuration)
            {
               result = printDuration(file, (MPXDuration)value);
            }
            else
            {
               if (value instanceof MPXCurrency)
               {           
                  result = printExtendedAttributeCurrency((MPXCurrency)value);
               }
               else
               {
                  if (value instanceof Number)
                  {
                     result = printExtendedAttributeNumber((Number)value);                     
                  }
                  else
                  {
                     result = value.toString();
                  }
               }
            }
         }
      }      
      
      return (result);
   }

   /**
    * Parse an extended attribute value.
    * 
    * @param file parent file
    * @param mpx parent entity
    * @param value string value
    * @param mpxFieldID field ID
    * @param dataType data type
    */
   public static final void parseExtendedAttribute (MSPDIFile file, ExtendedAttributeContainer mpx, String value, Integer mpxFieldID, int dataType)
   {
      switch (dataType)
      {
         case MSPDIFile.STRING_ATTRIBUTE:
         {
            mpx.set(mpxFieldID.intValue(), value);
            break;
         }
   
         case MSPDIFile.DATE_ATTRIBUTE:
         {
            mpx.setDate(mpxFieldID.intValue(), parseExtendedAttributeDate(value));
            break;
         }
   
         case MSPDIFile.CURRENCY_ATTRIBUTE:
         {
            mpx.setCurrency(mpxFieldID.intValue(), parseExtendedAttributeCurrency(value));
            break;
         }
   
         case MSPDIFile.BOOLEAN_ATTRIBUTE:
         {
            mpx.set(mpxFieldID.intValue(), parseExtendedAttributeBoolean(value));
            break;
         }
   
         case MSPDIFile.NUMERIC_ATTRIBUTE:
         {
            mpx.set(mpxFieldID.intValue(), parseExtendedAttributeNumber(value));
            break;
         }
   
         case MSPDIFile.DURATION_ATTRIBUTE:
         {
            mpx.set(mpxFieldID.intValue(), parseDuration(file, null, value));
            break;
         }
      }      
   }

   /**
    * Printy a currency symbol psoition value.
    *
    * @param value CurrencySymbolPosition instance
    * @return currency symbol position
    */
   public static final String printCurrencySymbolPosition (CurrencySymbolPosition value)
   {
      String result;
   
      switch (value.getValue())
      {
         default:
         case CurrencySymbolPosition.BEFORE_VALUE:
         {
            result = "0";
            break;
         }
   
         case CurrencySymbolPosition.AFTER_VALUE:
         {
            result = "1";
            break;
         }
   
         case CurrencySymbolPosition.BEFORE_WITH_SPACE_VALUE:
         {
            result = "2";
            break;
         }
   
         case CurrencySymbolPosition.AFTER_WITH_SPACE_VALUE:
         {
            result = "3";
            break;
         }
      }
   
      return (result);
   }
   
   /**
    * Parse a currency symbol position value.
    *
    * @param value currency symbol position
    * @return CurrencySymbolPosition instance
    */
   public static final CurrencySymbolPosition parseCurrencySymbolPosition (String value)
   {
      CurrencySymbolPosition result = CurrencySymbolPosition.BEFORE;
   
      switch (NumberUtility.getInt(value))
      {
         case 0:
         {
            result = CurrencySymbolPosition.BEFORE;
            break;
         }

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
      }
   
      return (result);
   }


   /**
    * Print an accrue type.
    *
    * @param value AccrueType instance
    * @return accrue type value
    */
   public static final String printAccrueType (AccrueType value)
   {
      return (Integer.toString(value==null?AccrueType.PRORATED_VALUE:value.getType()));
   }

   /**
    * Parse an accrue type.
    * 
    * @param value accrue type value
    * @return AccrueType instance
    */
   public static final AccrueType parseAccrueType (String value)
   {
      return (AccrueType.getInstance(NumberUtility.getInt(value)));
   }
   
   /**
    * Print a resource type.
    * 
    * @param value ResourceType instance
    * @return resource type value
    */
   public static final String  printResourceType (ResourceType value)
   {
      return (Integer.toString(value==null?ResourceType.WORK_VALUE:value.getValue()));
   }

   /**
    * Parse a resource type.
    * 
    * @param value resource type value
    * @return ResourceType instance
    */
   public static final ResourceType parseResourceType (String value)
   {
      return (ResourceType.getInstance(NumberUtility.getInt(value)));      
   }
   
   /**
    * Print a work group.
    * 
    * @param value WorkGroup instance
    * @return work group value
    */
   public static final String printWorkGroup (WorkGroup value)
   {
      return (Integer.toString(value==null?WorkGroup.DEFAULT_VALUE:value.getValue()));
   }

   /**
    * Parse a work group.
    * 
    * @param value work group value
    * @return WorkGroup instance
    */
   public static final WorkGroup parseWorkGroup (String value)
   {
      return (WorkGroup.getInstance(NumberUtility.getInt(value)));      
   }
   
   /**
    * Print a work contour.
    * 
    * @param value WorkContour instance
    * @return work contour value
    */
   public static final String printWorkContour (WorkContour value)
   {
      return (Integer.toString(value==null?WorkContour.FLAT_VALUE:value.getValue()));
   }

   /**
    * Parse a work contour.
    * 
    * @param value work contour value
    * @return WorkContour instance
    */
   public static final WorkContour parseWorkContour (String value)
   {
      return (WorkContour.getInstance(NumberUtility.getInt(value)));
   }
   
   /**
    * Print a booking type.
    * 
    * @param value BookingType instance
    * @return booking type value
    */
   public static final String printBookingType (BookingType value)
   {
      return (Integer.toString(value==null?BookingType.COMMITTED_VALUE:value.getValue()));
   }

   /**
    * Parse a booking type.
    * 
    * @param value booking type value
    * @return BookingType instance
    */
   public static final BookingType parseBookingType (String value)
   {
      return (BookingType.getInstance(NumberUtility.getInt(value)));
   }
   
   /**
    * Print a task type.
    * 
    * @param value TaskType instance
    * @return task type value
    */
   public static final String printTaskType (TaskType value)
   {
      return (Integer.toString(value==null?TaskType.FIXED_UNITS_VALUE:value.getValue()));
   }

   /**
    * Parse a task type.
    * 
    * @param value task type value
    * @return TaskType instance
    */
   public static final TaskType parseTaskType (String value)
   {
      return (TaskType.getInstance(NumberUtility.getInt(value)));
   }

   /**
    * Print an earned value method.
    * 
    * @param value EarnedValueMethod instance
    * @return earned value method value
    */
   public static final BigInteger printEarnedValueMethod (EarnedValueMethod value)
   {
      return (value==null?BigInteger.valueOf(EarnedValueMethod.PERCENT_COMPLETE_VALUE):BigInteger.valueOf(value.getValue()));
   }

   /**
    * Parse an earned value method.
    * 
    * @param value earned value method
    * @return EarnedValueMethod instance
    */
   public static final EarnedValueMethod parseEarnedValueMethod (Number value)
   {
      return (EarnedValueMethod.getInstance(NumberUtility.getInt(value)));
   }
   
   /**
    * Print units.
    *
    * @param value units value
    * @return units value
    */
   public static final BigDecimal printUnits (Number value)
   {
      return (value==null?BIGDECIMAL_ONE:new BigDecimal(value.doubleValue() / 100));
   }

   /**
    * Parse units.
    * 
    * @param value units value
    * @return units value
    */
   public static final Number parseUnits (Number value)
   {
      return (value==null?null:new Double(value.doubleValue()*100));
   }
   
   /**
    * Print time unit.
    * 
    * @param value TimeUnit instance
    * @return time unit value
    */
   public static final BigInteger printTimeUnit (TimeUnit value)
   {
      return (BigInteger.valueOf(value==null?TimeUnit.DAYS_VALUE+1:value.getValue()+1));
   }

   /**
    * Parse time unit.
    * 
    * @param value time unit value
    * @return TimeUnit instance
    */
   public static final TimeUnit parseTimeUnit (Number value)
   {
      return (TimeUnit.getInstance(NumberUtility.getInt(value)-1));
   }
   
   /**
    * Print date.
    *
    * @param value Date value
    * @return Calendar value
    */
   public static final Calendar printDate (Date value)
   {
      Calendar cal = null;
   
      if (value != null)
      {
         cal = Calendar.getInstance();
         cal.setTime(value);
      }
   
      return (cal);
   }

   /**
    * Parse date
    *
    * @param value Calendar value
    * @return Date value
    */
   public static final Date parseDate (Calendar value)
   {
      Date result = null;
   
      if (value != null)
      {
         Calendar cal = Calendar.getInstance();
         cal.set(Calendar.YEAR, value.get(Calendar.YEAR));
         cal.set(Calendar.MONTH, value.get(Calendar.MONTH));
         cal.set(Calendar.DAY_OF_MONTH, value.get(Calendar.DAY_OF_MONTH));
         cal.set(Calendar.HOUR_OF_DAY, value.get(Calendar.HOUR_OF_DAY));
         cal.set(Calendar.MINUTE, value.get(Calendar.MINUTE));
         cal.set(Calendar.SECOND, value.get(Calendar.SECOND));
         cal.set(Calendar.MILLISECOND, value.get(Calendar.MILLISECOND));
         result = cal.getTime();
      }
   
      return (result);
   }
   
   /**
    * Print time.
    * 
    * @param value time value
    * @return calendar value
    */
   public static final Calendar printTime (Date value)
   {
      Calendar cal = null;
   
      if (value != null)
      {
         cal = Calendar.getInstance();
         cal.setTime(value);
      }
   
      return (cal);
   }
   
   /**
    * Parse time.
    * 
    * @param value Calendar value
    * @return time value
    */
   public static final Date parseTime (Calendar value)
   {
      Date result = null;
   
      if (value != null)
      {
         Calendar cal = Calendar.getInstance();
         cal.set(Calendar.HOUR_OF_DAY, value.get(Calendar.HOUR_OF_DAY));
         cal.set(Calendar.MINUTE, value.get(Calendar.MINUTE));
         cal.set(Calendar.SECOND, value.get(Calendar.SECOND));
         cal.set(Calendar.MILLISECOND, value.get(Calendar.MILLISECOND));
         result = cal.getTime();
      }
   
      return (result);
   }


   /**
    * Parse work units
    *
    * @param value work units value
    * @return TimeUnit instance
    */
   public static final TimeUnit parseWorkUnits (BigInteger value)
   {
      TimeUnit result = TimeUnit.HOURS;
   
      if (value != null)
      {
         switch (value.intValue())
         {
            case 1:
            {
               result = TimeUnit.MINUTES;
               break;
            }
   
            case 3:
            {
               result = TimeUnit.DAYS;
               break;
            }
   
            case 4:
            {
               result = TimeUnit.WEEKS;
               break;
            }
   
            case 5:
            {
               result = TimeUnit.MONTHS;
               break;
            }
   
            case 7:
            {
               result = TimeUnit.YEARS;
               break;
            }
   
            default:
            case 2:
            {
               result = TimeUnit.HOURS;
               break;
            }
         }
      }
   
      return (result);
   }

   /**
    * Print work units.
    *
    * @param value TimeUnit instance
    * @return work units value
    */
   public static final BigInteger printWorkUnits (TimeUnit value)
   {
      int result;
   
      if (value == null)
      {
         value = TimeUnit.HOURS;
      }
   
      switch (value.getValue())
      {
         case TimeUnit.MINUTES_VALUE:
         {
            result = 1;
            break;
         }
   
         case TimeUnit.DAYS_VALUE:
         {
            result = 3;
            break;
         }
   
         case TimeUnit.WEEKS_VALUE:
         {
            result = 4;
            break;
         }
   
         case TimeUnit.MONTHS_VALUE:
         {
            result = 5;
            break;
         }
   
         case TimeUnit.YEARS_VALUE:
         {
            result = 7;
            break;
         }
   
         default:
         case TimeUnit.HOURS_VALUE:
         {
            result = 2;
            break;
         }
      }
   
      return (BigInteger.valueOf(result));
   }

   /**
    * Parse a duration.
    *
    * @param file parent file
    * @param defaultUnits default time units for the resulting duration
    * @param value duration value
    * @return MPXDuration instance
    */
   public static final MPXDuration parseDuration (MSPDIFile file, TimeUnit defaultUnits, String value)
   {
      MPXDuration result = null;
   
      if (value != null && value.length() != 0)
      {
         XsdDuration xsd = new XsdDuration (value);
         TimeUnit units = TimeUnit.DAYS;
   
         if (xsd.getSeconds() != 0 || xsd.getMinutes() != 0)
         {
            units = TimeUnit.MINUTES;
         }
   
         if (xsd.getHours() != 0)
         {
            units = TimeUnit.HOURS;
         }
   
         if (xsd.getDays() != 0)
         {
            units = TimeUnit.DAYS;
         }
   
         if (xsd.getMonths() != 0)
         {
            units = TimeUnit.MONTHS;
         }
   
         if (xsd.getYears() != 0)
         {
            units = TimeUnit.YEARS;
         }
   
         double duration = 0;
   
         switch (units.getValue())
         {
            case TimeUnit.YEARS_VALUE:
            {
               //
               // Calculate the number of years
               //
               duration += xsd.getYears();
               duration += (xsd.getMonths() / 12);
               duration += (xsd.getDays() / 365);
               duration += (xsd.getHours() / (365 * 24));
               duration += (xsd.getMinutes() / (365 * 24 * 60));
               duration += (xsd.getSeconds() / (365 * 24 * 60 * 60));
               
               //
               // Convert to hours
               //
               duration *= (file.getDefaultHoursPerWeek() * 52);
               
               break;
            }

            case TimeUnit.ELAPSED_YEARS_VALUE:
            {
               //
               // Calculate the number of years
               //
               duration += xsd.getYears();
               duration += (xsd.getMonths() / 12);
               duration += (xsd.getDays() / 365);
               duration += (xsd.getHours() / (365 * 24));
               duration += (xsd.getMinutes() / (365 * 24 * 60));
               duration += (xsd.getSeconds() / (365 * 24 * 60 * 60));
               
               //
               // Convert to hours
               //
               duration *= (24 * 7 * 52);
               break;
            }
            
            case TimeUnit.MONTHS_VALUE:
            {
               //
               // Calculate the number of months
               //
               duration += (xsd.getYears() * 12);
               duration += xsd.getMonths();
               duration += (xsd.getDays() / 30);
               duration += (xsd.getHours() / (30 * 24));
               duration += (xsd.getMinutes() / (30 * 24 * 60));
               duration += (xsd.getSeconds() / (30 * 24 * 60 * 60));
               
               //
               // Convert to hours
               //
               duration *= (file.getDefaultHoursPerWeek() * 4);
               
               break;
            }

            case TimeUnit.ELAPSED_MONTHS_VALUE:
            {
               //
               // Calculate the number of months
               //
               duration += (xsd.getYears() * 12);
               duration += xsd.getMonths();
               duration += (xsd.getDays() / 30);
               duration += (xsd.getHours() / (30 * 24));
               duration += (xsd.getMinutes() / (30 * 24 * 60));
               duration += (xsd.getSeconds() / (30 * 24 * 60 * 60));
               
               //
               // Convert to hours
               //
               duration *= (24 * 7 * 4);
               
               break;
            }
            
            case TimeUnit.WEEKS_VALUE:
            {
               //
               // Calculate the number of weeks
               //
               duration += (xsd.getYears() * 52);
               duration += (xsd.getMonths() * 4);
               duration += (xsd.getDays() / 7);
               duration += (xsd.getHours() / (7 * 24));
               duration += (xsd.getMinutes() / (7 * 24 * 60));
               duration += (xsd.getSeconds() / (7 * 24 * 60 * 60));
               
               //
               // Convert to hours
               //
               duration *= file.getDefaultHoursPerWeek();
               
               break;
            }

            case TimeUnit.ELAPSED_WEEKS_VALUE:
            {
               //
               // Calculate the number of weeks
               //
               duration += (xsd.getYears() * 52);
               duration += (xsd.getMonths() * 4);
               duration += (xsd.getDays() / 7);
               duration += (xsd.getHours() / (7 * 24));
               duration += (xsd.getMinutes() / (7 * 24 * 60));
               duration += (xsd.getSeconds() / (7 * 24 * 60 * 60));
               
               //
               // Convert to hours
               //
               duration *= (24 * 7);
               
               break;
            }
            
            case TimeUnit.DAYS_VALUE:
            {
               //
               // Calculate the number of days
               //
               duration += (xsd.getYears() * 365);
               duration += (xsd.getMonths() * 30);
               duration += xsd.getDays();
               duration += (xsd.getHours() / 24);
               duration += (xsd.getMinutes() / (24 * 60));
               duration += (xsd.getSeconds() / (24 * 60 * 60));
               
               //
               // Convert to hours
               //
               duration *= file.getDefaultHoursPerDay();
               
               break;
            }

            case TimeUnit.ELAPSED_DAYS_VALUE:
            {
               //
               // Calculate the number of days
               //
               duration += (xsd.getYears() * 365);
               duration += (xsd.getMonths() * 30);
               duration += xsd.getDays();
               duration += (xsd.getHours() / 24);
               duration += (xsd.getMinutes() / (24 * 60));
               duration += (xsd.getSeconds() / (24 * 60 * 60));
               
               //
               // Convert to hours
               //
               duration *= 24;
               
               break;
            }
            
            case TimeUnit.HOURS_VALUE:
            case TimeUnit.ELAPSED_HOURS_VALUE:
            {
               //
               // Calculate the number of hours
               //
               duration += (xsd.getYears() * (365 * 24));
               duration += (xsd.getMonths() * (30 * 24));
               duration += (xsd.getDays() * 24);
               duration += xsd.getHours();
               duration += (xsd.getMinutes() / 60);
               duration += (xsd.getSeconds() / (60 * 60));
               break;
            }
   
            case TimeUnit.MINUTES_VALUE:
            case TimeUnit.ELAPSED_MINUTES_VALUE:
            {
               //
               // Calculate the number of minutes
               //
               duration += (xsd.getYears() * (365 * 24 * 60));
               duration += (xsd.getMonths() * (30 * 24 * 60));
               duration += (xsd.getDays() * (24 * 60));
               duration += (xsd.getHours() * 60);
               duration += xsd.getMinutes();
               duration += (xsd.getSeconds() / 60);
               
               //
               // Convert to hours
               //
               duration /= 60;
               
               break;
            }
         }
   
         
         //
         // Convert from a duration in hours to a duration
         // expressed in the default duration units
         //
         if (defaultUnits == null)
         {
            units = file.getDefaultDurationUnits();
         }
         else
         {
            units = defaultUnits;
         }
         
         if (units != TimeUnit.HOURS && units != TimeUnit.ELAPSED_HOURS)
         {
            switch (units.getValue())
            {
               case TimeUnit.MINUTES_VALUE:
               case TimeUnit.ELAPSED_MINUTES_VALUE:
               {
                  duration *= 60;
                  break;
               }
   
               case TimeUnit.DAYS_VALUE:
               {
                  duration /= file.getDefaultHoursPerDay();
                  break;
               }
   
               case TimeUnit.ELAPSED_DAYS_VALUE:
               {
                  duration /= 24;
                  break;
               }
               
               case TimeUnit.WEEKS_VALUE:
               {
                  duration /= file.getDefaultHoursPerWeek();
                  break;
               }
   
               case TimeUnit.ELAPSED_WEEKS_VALUE:
               {
                  duration /= (24 * 7);
                  break;
               }
               
               case TimeUnit.MONTHS_VALUE:
               {
                  duration /= (file.getDefaultHoursPerWeek() * 4);
                  break;
               }
   
               case TimeUnit.ELAPSED_MONTHS_VALUE:
               {
                  duration /= (24 * 7 * 4);
                  break;
               }
               
               case TimeUnit.YEARS_VALUE:
               {
                  duration /= (file.getDefaultHoursPerWeek() * 52);
                  break;
               }
               
               case TimeUnit.ELAPSED_YEARS_VALUE:
               {
                  duration /= (24 * 7 * 52);
                  break;
               }                           
            }
         }
         
         result = new MPXDuration (duration, units);
      }
   
      return (result);
   }

   /**
    * Print duration.
    *
    * Note that Microsoft's xsd:duration parser implementation does not
    * appear to recognise durations other than those expressed in hours.
    * We use the compatibility flag to determine whether the output
    * is adjusted for the benefit of Microsoft Project.
    *
    * @param file parent file
    * @param duration MPXDuration value
    * @return xsd:duration value
    */
   public static final String printDuration (MSPDIFile file, MPXDuration duration)
   {
      String result = null;
   
      if (duration == null)
      {
         result = DatatypeConverter.ZERO_DURATION;
      }
      else
      {
         TimeUnit durationType = duration.getUnits();
   
         if (file.getMicrosoftProjectCompatibleOutput() == false || 
             durationType.getValue() == TimeUnit.HOURS_VALUE || 
             durationType.getValue() == TimeUnit.ELAPSED_HOURS_VALUE)
         {
            result = new XsdDuration(duration).toString();
         }
         else
         {
            double hours = duration.getDuration();
   
            switch (durationType.getValue())
            {
               case TimeUnit.MINUTES_VALUE:
               case TimeUnit.ELAPSED_MINUTES_VALUE:
               {
                  hours = duration.getDuration() / 60;
                  break;
               }
   
               case TimeUnit.DAYS_VALUE:
               {
                  hours *= file.getDefaultHoursPerDay();
                  break;
               }
   
               case TimeUnit.ELAPSED_DAYS_VALUE:
               {
                  hours *= 24;
                  break;
               }
               
               case TimeUnit.WEEKS_VALUE:
               {
                  hours *= file.getDefaultHoursPerWeek();
                  break;
               }
   
               case TimeUnit.ELAPSED_WEEKS_VALUE:
               {
                  hours *= (24 * 7);
                  break;
               }
               
               case TimeUnit.MONTHS_VALUE:
               {
                  hours *= (file.getDefaultHoursPerWeek() * 4);
                  break;
               }
   
               case TimeUnit.ELAPSED_MONTHS_VALUE:
               {
                  hours *= (24 * 7 * 4);
                  break;
               }
               
               case TimeUnit.YEARS_VALUE:
               {
                  hours *= (file.getDefaultHoursPerWeek() * 52);
                  break;
               }
               
               case TimeUnit.ELAPSED_YEARS_VALUE:
               {
                  hours *= (24 * 7 * 52);
                  break;
               }               
            }
   
            result = new XsdDuration(new MPXDuration (hours, TimeUnit.HOURS)).toString();
         }
      }
   
      return (result);
   }

   /**
    * Print duration time units.
    *
    * @param duration MPXDuration value
    * @return time units value
    */
   public static final BigInteger printDurationTimeUnits (MPXDuration duration)
   {
      BigInteger result = null;
   
      if (duration != null)
      {
         result = printDurationTimeUnits(duration.getUnits());
      }
   
      return (result);
   }

   /**
    * Parse currency
    *
    * @param value currency value
    * @return currency value
    */
   public static final Number parseCurrency (Number value)
   {
      return (value==null?null:new Double (value.doubleValue() / 100));
   }
  
   /**
    * Print currency
    *
    * @param value currency value
    * @return currency value
    */
   public static final BigDecimal printCurrency (Number value)
   {
      return (value==null?BIGDECIMAL_ZERO:new BigDecimal (value.doubleValue() * 100));
   }

   /**
    * Parse duration time units.
    * 
    * Note that we don't differentiate between confirmed and unconfirmed
    * durations. Unrecognised duration types are default to hours.
    *
    * @param value BigInteger value
    * @return Duration units
    */
   public static final TimeUnit parseDurationTimeUnits (BigInteger value)
   {
      TimeUnit result = TimeUnit.HOURS;
   
      if (value != null)
      {
         switch (value.intValue())
         {
            case 3:
            case 35:
            {
               result = TimeUnit.MINUTES;
               break;
            }
   
            case 4:
            case 36:
            {
               result = TimeUnit.ELAPSED_MINUTES;
               break;
            }
   
            case 5:
            case 37:
            {
               result = TimeUnit.HOURS;
               break;
            }
   
            case 6:
            case 38:
            {
               result = TimeUnit.ELAPSED_HOURS;
               break;
            }
   
            case 7:
            case 39:
            {
               result = TimeUnit.DAYS;
               break;
            }
   
            case 8:
            case 40:
            {
               result = TimeUnit.ELAPSED_DAYS;
               break;
            }
   
            case 9:
            case 41:
            {
               result = TimeUnit.WEEKS;
               break;
            }
   
            case 10:
            case 42:
            {
               result = TimeUnit.ELAPSED_WEEKS;
               break;
            }
   
            case 11:
            case 43:
            {
               result = TimeUnit.MONTHS;
               break;
            }
   
            case 12:
            case 44:
            {
               result = TimeUnit.ELAPSED_MONTHS;
               break;
            }
   
            case 19:
            case 51:
            {
               result = TimeUnit.PERCENT;
               break;
            }
   
            case 20:
            case 52:
            {
               result = TimeUnit.ELAPSED_PERCENT;
               break;
            }
         }
      }
   
      return (result);
   }

   /**
    * Print duration time units.
    * 
    * Note that we don't differentiate between confirmed and unconfirmed
    * durations. Unrecognised duration types are default to hours.
    *
    * @param value Duration units
    * @return BigInteger value
    */
   public static final BigInteger printDurationTimeUnits (TimeUnit value)
   {
      int result;
   
      if (value == null)
      {
         value = TimeUnit.HOURS;
      }
   
      switch (value.getValue())
      {
         case TimeUnit.MINUTES_VALUE:
         {
            result = 3;
            break;
         }
   
         case TimeUnit.ELAPSED_MINUTES_VALUE:
         {
            result = 4;
            break;
         }
   
         case TimeUnit.ELAPSED_HOURS_VALUE:
         {
            result = 6;
            break;
         }
   
         case TimeUnit.DAYS_VALUE:
         {
            result = 7;
            break;
         }
   
         case TimeUnit.ELAPSED_DAYS_VALUE:
         {
            result = 8;
            break;
         }
   
         case TimeUnit.WEEKS_VALUE:
         {
            result = 9;
            break;
         }
   
         case TimeUnit.ELAPSED_WEEKS_VALUE:
         {
            result = 10;
            break;
         }
   
         case TimeUnit.MONTHS_VALUE:
         {
            result = 11;
            break;
         }
   
         case TimeUnit.ELAPSED_MONTHS_VALUE:
         {
            result = 12;
            break;
         }
   
         case TimeUnit.PERCENT_VALUE:
         {
            result = 19;
            break;
         }
   
         case TimeUnit.ELAPSED_PERCENT_VALUE:
         {
            result = 20;
            break;
         }
   
         default:
         case TimeUnit.HOURS_VALUE:
         {
            result = 5;
            break;
         }
      }
   
      return (BigInteger.valueOf(result));
   }


   /**
    * Parse priority.
    * 
    *
    * @param priority priority value
    * @return Priority instance
    */
   public static final Priority parsePriority (BigInteger priority)
   {
      int result = Priority.MEDIUM;
   
      if (priority != null)
      {
         if (priority.intValue() >= 1000)
         {
            result = Priority.DO_NOT_LEVEL;
         }
         else
         {
            result = (priority.intValue() / 100)-1;
         }
      }
   
      return (Priority.getInstance (result));
   }

   /**
    * Print priority.
    *
    * @param priority Priority instance
    * @return priority value
    */
   public static final BigInteger printPriority (Priority priority)
   {
      int result = Priority.MEDIUM;
   
      if (priority != null)
      {
         result = (priority.getPriority()+1) * 100;
      }
   
      return (BigInteger.valueOf(result));
   }

   /**
    * Parse duration in minutes.
    *
    * @param value duration value
    * @return MPXDuration instance
    */
   public static final MPXDuration parseDurationInMinutes (Number value)
   {
      MPXDuration result = null;
   
      if (value != null)
      {
         result = new MPXDuration (value.intValue()/1000, TimeUnit.MINUTES);
      }
   
      return (result);
   }

   /**
    * Print duration in minutes.
    *
    * @param duration MPXDuration instance
    * @return duration in minutes
    */
   public static final double printDurationInMinutes (MPXDuration duration)
   {
      double result = 0;
   
      if (duration != null)
      {
         result = duration.getDuration();
   
         switch (duration.getUnits().getValue())
         {
            case TimeUnit.HOURS_VALUE:
            case TimeUnit.ELAPSED_HOURS_VALUE:
            {
               result *= 60;
               break;
            }
   
            case TimeUnit.DAYS_VALUE:
            case TimeUnit.ELAPSED_DAYS_VALUE:
            {
               result *= (60 * 8);
               break;
            }
   
            case TimeUnit.WEEKS_VALUE:
            case TimeUnit.ELAPSED_WEEKS_VALUE:
            {
               result *= (60 * 8 * 5);
               break;
            }
   
            case TimeUnit.MONTHS_VALUE:
            case TimeUnit.ELAPSED_MONTHS_VALUE:
            {
               result *= (60 * 8 * 5 * 4);
               break;
            }
   
            case TimeUnit.YEARS_VALUE:
            case TimeUnit.ELAPSED_YEARS_VALUE:
            {
               result *= (60 * 8 * 5 * 52);
               break;
            }
         }
      }
   
      return (result);
   }

   /**
    * Print rate.
    *
    * @param rate MPXRate instance
    * @return rate value
    */
   public static final BigDecimal printRate (MPXRate rate)
   {
      return (rate==null?null:new BigDecimal(rate.getAmount()));
   }

   /**
    * Parse rate.
    *
    * @param value rate value
    * @return MPXRate instance
    */
   public static final MPXRate parseRate (BigDecimal value)
   {
      MPXRate result = null;
   
      if (value != null)
      {
         result = new MPXRate (value, TimeUnit.HOURS);
      }
   
      return (result);
   }


   /**
    * Print a day.
    * 
    * @param day Day instance
    * @return day value
    */
   public static final BigInteger printDay (Day day)
   {
      return (day==null?null:BigInteger.valueOf(day.getValue()-1));
   }

   /**
    * Parse a day.
    * 
    * @param value day value
    * @return Day instance
    */
   public static final Day parseDay (Number value)
   {
      return (Day.getInstance(NumberUtility.getInt(value)+1));      
   }

   /**
    * Parse a constraint type.
    * 
    * @param value constraint type value
    * @return ConstraintType instance
    */
   public static final ConstraintType parseConstraintType (Number value)
   {
      return (ConstraintType.getInstance(value));
   }
   
   /**
    * Print a constraint type.
    * 
    * @param value ConstraintType instance
    * @return constraint type value
    */
   public static final BigInteger printConstraintType (ConstraintType value)
   {
      return (value==null?null:BigInteger.valueOf(value.getType()));
   }

   /**
    * Retrieve a number formatter.
    * 
    * @return NumberFormat instance
    */
   private static final NumberFormat getNumberFormat ()
   {
      NumberFormat format = (NumberFormat)NUMBER_FORMAT.get();      
      if (format == null)
      {
         format = new DecimalFormat("#.##");
         NUMBER_FORMAT.set(format);
      }
      return (format);
   }
   
   /**
    * Retrieve a date formatter.
    * 
    * @return DateFormat instance
    */
   private static final DateFormat getDateFormat ()
   {
      DateFormat df = (DateFormat)DATE_FORMAT.get();
      if (df == null)
      {
         df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
         df.setLenient(false);
      }
      return (df);
      
   }
   
   private static final ThreadLocal DATE_FORMAT = new ThreadLocal ();   
   private static final ThreadLocal NUMBER_FORMAT = new ThreadLocal ();
   private static final String ZERO_DURATION = "PT0H0M0S";   
   private static final BigDecimal BIGDECIMAL_ZERO = BigDecimal.valueOf(0);   
   private static final BigDecimal BIGDECIMAL_ONE = BigDecimal.valueOf(1);      
}
