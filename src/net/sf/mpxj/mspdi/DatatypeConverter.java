/*
 * file:       DatatypeConverter.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2005
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

package net.sf.mpxj.mspdi;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import net.sf.mpxj.AccrueType;
import net.sf.mpxj.BookingType;
import net.sf.mpxj.ConstraintType;
import net.sf.mpxj.CurrencySymbolPosition;
import net.sf.mpxj.DataType;
import net.sf.mpxj.Day;
import net.sf.mpxj.Duration;
import net.sf.mpxj.EarnedValueMethod;
import net.sf.mpxj.FieldContainer;
import net.sf.mpxj.FieldType;
import net.sf.mpxj.Priority;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ProjectProperties;
import net.sf.mpxj.Rate;
import net.sf.mpxj.ResourceType;
import net.sf.mpxj.TaskType;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.WorkContour;
import net.sf.mpxj.WorkGroup;
import net.sf.mpxj.common.NumberHelper;

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
   public static final String printExtendedAttributeCurrency(Number value)
   {
      return (value == null ? null : getNumberFormat().format(value.doubleValue() * 100));
   }

   /**
    * Parse an extended attribute currency value.
    *
    * @param value string representation
    * @return currency value
    */
   public static final Number parseExtendedAttributeCurrency(String value)
   {
      Number result;

      if (value == null)
      {
         result = null;
      }
      else
      {
         result = NumberHelper.getDouble(Double.parseDouble(correctNumberFormat(value)) / 100);
      }
      return result;
   }

   /**
    * Print an extended attribute numeric value.
    *
    * @param value numeric value
    * @return string representation
    */
   public static final String printExtendedAttributeNumber(Number value)
   {
      return (getNumberFormat().format(value.doubleValue()));
   }

   /**
    * Parse and extended attribute numeric value.
    *
    * @param value string representation
    * @return numeric value
    */
   public static final Number parseExtendedAttributeNumber(String value)
   {
      return (Double.valueOf(correctNumberFormat(value)));
   }

   /**
    * Print an extended attribute boolean value.
    *
    * @param value boolean value
    * @return string representation
    */
   public static final String printExtendedAttributeBoolean(Boolean value)
   {
      return (value.booleanValue() ? "1" : "0");
   }

   /**
    * Parse an extended attribute boolean value.
    *
    * @param value string representation
    * @return boolean value
    */
   public static final Boolean parseExtendedAttributeBoolean(String value)
   {
      return ((value.equals("1") ? Boolean.TRUE : Boolean.FALSE));
   }

   /**
    * Print an extended attribute date value.
    *
    * @param value date value
    * @return string representation
    */
   public static final String printExtendedAttributeDate(Date value)
   {
      return (value == null ? null : getDateFormat().format(value));
   }

   /**
    * Parse an extended attribute date value.
    *
    * @param value string representation
    * @return date value
    */
   public static final Date parseExtendedAttributeDate(String value)
   {
      Date result = null;

      if (value != null)
      {
         try
         {
            result = getDateFormat().parse(value);
         }

         catch (ParseException ex)
         {
            // ignore exceptions
         }
      }

      return (result);
   }

   /**
    * Print an extended attribute value.
    *
    * @param writer parent MSPDIWriter instance
    * @param value attribute value
    * @param type type of the value being passed
    * @return string representation
    */
   public static final String printExtendedAttribute(MSPDIWriter writer, Object value, DataType type)
   {
      String result;

      if (type == DataType.DATE)
      {
         result = printExtendedAttributeDate((Date) value);
      }
      else
      {
         if (value instanceof Boolean)
         {
            result = printExtendedAttributeBoolean((Boolean) value);
         }
         else
         {
            if (value instanceof Duration)
            {
               result = printDuration(writer, (Duration) value);
            }
            else
            {
               if (type == DataType.CURRENCY)
               {
                  result = printExtendedAttributeCurrency((Number) value);
               }
               else
               {
                  if (value instanceof Number)
                  {
                     result = printExtendedAttributeNumber((Number) value);
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
    * @param durationFormat duration format associated with the extended attribute
    */
   public static final void parseExtendedAttribute(ProjectFile file, FieldContainer mpx, String value, FieldType mpxFieldID, TimeUnit durationFormat)
   {
      if (mpxFieldID != null)
      {
         switch (mpxFieldID.getDataType())
         {
            case STRING:
            {
               mpx.set(mpxFieldID, value);
               break;
            }

            case DATE:
            {
               mpx.set(mpxFieldID, parseExtendedAttributeDate(value));
               break;
            }

            case CURRENCY:
            {
               mpx.set(mpxFieldID, parseExtendedAttributeCurrency(value));
               break;
            }

            case BOOLEAN:
            {
               mpx.set(mpxFieldID, parseExtendedAttributeBoolean(value));
               break;
            }

            case NUMERIC:
            {
               mpx.set(mpxFieldID, parseExtendedAttributeNumber(value));
               break;
            }

            case DURATION:
            {
               mpx.set(mpxFieldID, parseDuration(file, durationFormat, value));
               break;
            }

            default:
            {
               break;
            }
         }
      }
   }

   /**
    * Prints a currency symbol position value.
    *
    * @param value CurrencySymbolPosition instance
    * @return currency symbol position
    */
   public static final String printCurrencySymbolPosition(CurrencySymbolPosition value)
   {
      String result;

      switch (value)
      {
         default:
         case BEFORE:
         {
            result = "0";
            break;
         }

         case AFTER:
         {
            result = "1";
            break;
         }

         case BEFORE_WITH_SPACE:
         {
            result = "2";
            break;
         }

         case AFTER_WITH_SPACE:
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
   public static final CurrencySymbolPosition parseCurrencySymbolPosition(String value)
   {
      CurrencySymbolPosition result = CurrencySymbolPosition.BEFORE;

      switch (NumberHelper.getInt(value))
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
   public static final String printAccrueType(AccrueType value)
   {
      return (Integer.toString(value == null ? AccrueType.PRORATED.getValue() : value.getValue()));
   }

   /**
    * Parse an accrue type.
    *
    * @param value accrue type value
    * @return AccrueType instance
    */
   public static final AccrueType parseAccrueType(String value)
   {
      return (AccrueType.getInstance(NumberHelper.getInt(value)));
   }

   /**
    * Print a resource type.
    *
    * @param value ResourceType instance
    * @return resource type value
    */
   public static final String printResourceType(ResourceType value)
   {
      return (Integer.toString(value == null ? ResourceType.WORK.getValue() : value.getValue()));
   }

   /**
    * Parse a resource type.
    *
    * @param value resource type value
    * @return ResourceType instance
    */
   public static final ResourceType parseResourceType(String value)
   {
      return (ResourceType.getInstance(NumberHelper.getInt(value)));
   }

   /**
    * Print a work group.
    *
    * @param value WorkGroup instance
    * @return work group value
    */
   public static final String printWorkGroup(WorkGroup value)
   {
      return (Integer.toString(value == null ? WorkGroup.DEFAULT.getValue() : value.getValue()));
   }

   /**
    * Parse a work group.
    *
    * @param value work group value
    * @return WorkGroup instance
    */
   public static final WorkGroup parseWorkGroup(String value)
   {
      return (WorkGroup.getInstance(NumberHelper.getInt(value)));
   }

   /**
    * Print a work contour.
    *
    * @param value WorkContour instance
    * @return work contour value
    */
   public static final String printWorkContour(WorkContour value)
   {
      return (Integer.toString(value == null ? WorkContour.FLAT.getValue() : value.getValue()));
   }

   /**
    * Parse a work contour.
    *
    * @param value work contour value
    * @return WorkContour instance
    */
   public static final WorkContour parseWorkContour(String value)
   {
      return (WorkContour.getInstance(NumberHelper.getInt(value)));
   }

   /**
    * Print a booking type.
    *
    * @param value BookingType instance
    * @return booking type value
    */
   public static final String printBookingType(BookingType value)
   {
      return (Integer.toString(value == null ? BookingType.COMMITTED.getValue() : value.getValue()));
   }

   /**
    * Parse a booking type.
    *
    * @param value booking type value
    * @return BookingType instance
    */
   public static final BookingType parseBookingType(String value)
   {
      return (BookingType.getInstance(NumberHelper.getInt(value)));
   }

   /**
    * Print a task type.
    *
    * @param value TaskType instance
    * @return task type value
    */
   public static final String printTaskType(TaskType value)
   {
      return (Integer.toString(value == null ? TaskType.FIXED_UNITS.getValue() : value.getValue()));
   }

   /**
    * Parse a task type.
    *
    * @param value task type value
    * @return TaskType instance
    */
   public static final TaskType parseTaskType(String value)
   {
      return (TaskType.getInstance(NumberHelper.getInt(value)));
   }

   /**
    * Print an earned value method.
    *
    * @param value EarnedValueMethod instance
    * @return earned value method value
    */
   public static final BigInteger printEarnedValueMethod(EarnedValueMethod value)
   {
      return (value == null ? BigInteger.valueOf(EarnedValueMethod.PERCENT_COMPLETE.getValue()) : BigInteger.valueOf(value.getValue()));
   }

   /**
    * Parse an earned value method.
    *
    * @param value earned value method
    * @return EarnedValueMethod instance
    */
   public static final EarnedValueMethod parseEarnedValueMethod(Number value)
   {
      return (EarnedValueMethod.getInstance(NumberHelper.getInt(value)));
   }

   /**
    * Print units.
    *
    * @param value units value
    * @return units value
    */
   public static final BigDecimal printUnits(Number value)
   {
      return (value == null ? BIGDECIMAL_ONE : new BigDecimal(value.doubleValue() / 100));
   }

   /**
    * Parse units.
    *
    * @param value units value
    * @return units value
    */
   public static final Number parseUnits(Number value)
   {
      return (value == null ? null : NumberHelper.getDouble(value.doubleValue() * 100));
   }

   /**
    * Print time unit.
    *
    * @param value TimeUnit instance
    * @return time unit value
    */
   public static final BigInteger printTimeUnit(TimeUnit value)
   {
      return (BigInteger.valueOf(value == null ? TimeUnit.DAYS.getValue() + 1 : value.getValue() + 1));
   }

   /**
    * Parse time unit.
    *
    * @param value time unit value
    * @return TimeUnit instance
    */
   public static final TimeUnit parseTimeUnit(Number value)
   {
      return (TimeUnit.getInstance(NumberHelper.getInt(value) - 1));
   }

   /**
    * Print date.
    *
    * @param value Date value
    * @return Calendar value
    */
   public static final Calendar printDate(Date value)
   {
      Calendar cal = null;

      if (value != null)
      {
         cal = Calendar.getInstance();
         cal.setTime(value);
         cal.set(Calendar.MILLISECOND, 0);
         cal.set(Calendar.SECOND, 0);
      }

      return (cal);
   }

   /**
    * Parse date.
    *
    * @param value Calendar value
    * @return Date value
    */
   public static final Date parseDate(Calendar value)
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
   public static final Calendar printTime(Date value)
   {
      Calendar cal = null;

      if (value != null)
      {
         cal = Calendar.getInstance();
         cal.setTime(value);
         cal.set(Calendar.MILLISECOND, 0);
         cal.set(Calendar.SECOND, 0);
      }

      return (cal);
   }

   /**
    * Parse time.
    *
    * @param value Calendar value
    * @return time value
    */
   public static final Date parseTime(Calendar value)
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
    * Parse work units.
    *
    * @param value work units value
    * @return TimeUnit instance
    */
   public static final TimeUnit parseWorkUnits(BigInteger value)
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
   public static final BigInteger printWorkUnits(TimeUnit value)
   {
      int result;

      if (value == null)
      {
         value = TimeUnit.HOURS;
      }

      switch (value)
      {
         case MINUTES:
         {
            result = 1;
            break;
         }

         case DAYS:
         {
            result = 3;
            break;
         }

         case WEEKS:
         {
            result = 4;
            break;
         }

         case MONTHS:
         {
            result = 5;
            break;
         }

         case YEARS:
         {
            result = 7;
            break;
         }

         default:
         case HOURS:
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
    * @return Duration instance
    */
   public static final Duration parseDuration(ProjectFile file, TimeUnit defaultUnits, String value)
   {
      Duration result = null;

      if (value != null && value.length() != 0)
      {
         XsdDuration xsd = new XsdDuration(value);
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

         switch (units)
         {
            case YEARS:
            {
               //
               // Calculate the number of years
               //
               duration += xsd.getYears();
               duration += ((double) xsd.getMonths() / 12);
               duration += ((double) xsd.getDays() / 365);
               duration += ((double) xsd.getHours() / (365 * 24));
               duration += ((double) xsd.getMinutes() / (365 * 24 * 60));
               duration += (xsd.getSeconds() / (365 * 24 * 60 * 60));
               break;
            }

            case ELAPSED_YEARS:
            {
               //
               // Calculate the number of years
               //
               duration += xsd.getYears();
               duration += ((double) xsd.getMonths() / 12);
               duration += ((double) xsd.getDays() / 365);
               duration += ((double) xsd.getHours() / (365 * 24));
               duration += ((double) xsd.getMinutes() / (365 * 24 * 60));
               duration += (xsd.getSeconds() / (365 * 24 * 60 * 60));
               break;
            }

            case MONTHS:
            {
               //
               // Calculate the number of months
               //
               duration += (xsd.getYears() * 12);
               duration += xsd.getMonths();
               duration += ((double) xsd.getDays() / 30);
               duration += ((double) xsd.getHours() / (30 * 24));
               duration += ((double) xsd.getMinutes() / (30 * 24 * 60));
               duration += (xsd.getSeconds() / (30 * 24 * 60 * 60));
               break;
            }

            case ELAPSED_MONTHS:
            {
               //
               // Calculate the number of months
               //
               duration += (xsd.getYears() * 12);
               duration += xsd.getMonths();
               duration += ((double) xsd.getDays() / 30);
               duration += ((double) xsd.getHours() / (30 * 24));
               duration += ((double) xsd.getMinutes() / (30 * 24 * 60));
               duration += (xsd.getSeconds() / (30 * 24 * 60 * 60));
               break;
            }

            case WEEKS:
            {
               //
               // Calculate the number of weeks
               //
               duration += (xsd.getYears() * 52);
               duration += (xsd.getMonths() * 4);
               duration += ((double) xsd.getDays() / 7);
               duration += ((double) xsd.getHours() / (7 * 24));
               duration += ((double) xsd.getMinutes() / (7 * 24 * 60));
               duration += (xsd.getSeconds() / (7 * 24 * 60 * 60));
               break;
            }

            case ELAPSED_WEEKS:
            {
               //
               // Calculate the number of weeks
               //
               duration += (xsd.getYears() * 52);
               duration += (xsd.getMonths() * 4);
               duration += ((double) xsd.getDays() / 7);
               duration += ((double) xsd.getHours() / (7 * 24));
               duration += ((double) xsd.getMinutes() / (7 * 24 * 60));
               duration += (xsd.getSeconds() / (7 * 24 * 60 * 60));
               break;
            }

            case DAYS:
            {
               //
               // Calculate the number of days
               //
               duration += (xsd.getYears() * 365);
               duration += (xsd.getMonths() * 30);
               duration += xsd.getDays();
               duration += ((double) xsd.getHours() / 24);
               duration += ((double) xsd.getMinutes() / (24 * 60));
               duration += (xsd.getSeconds() / (24 * 60 * 60));
               break;
            }

            case ELAPSED_DAYS:
            {
               //
               // Calculate the number of days
               //
               duration += (xsd.getYears() * 365);
               duration += (xsd.getMonths() * 30);
               duration += xsd.getDays();
               duration += ((double) xsd.getHours() / 24);
               duration += ((double) xsd.getMinutes() / (24 * 60));
               duration += (xsd.getSeconds() / (24 * 60 * 60));
               break;
            }

            case HOURS:
            case ELAPSED_HOURS:
            {
               //
               // Calculate the number of hours
               //
               duration += (xsd.getYears() * (365 * 24));
               duration += (xsd.getMonths() * (30 * 24));
               duration += (xsd.getDays() * 24);
               duration += xsd.getHours();
               duration += ((double) xsd.getMinutes() / 60);
               duration += (xsd.getSeconds() / (60 * 60));
               break;
            }

            case MINUTES:
            case ELAPSED_MINUTES:
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
               break;
            }

            default:
            {
               break;
            }
         }

         //
         // Convert from a duration in hours to a duration
         // expressed in the default duration units
         //
         ProjectProperties properties = file.getProjectProperties();
         if (defaultUnits == null)
         {
            defaultUnits = properties.getDefaultDurationUnits();
         }

         result = Duration.convertUnits(duration, units, defaultUnits, properties);
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
    * @param writer parent MSPDIWriter instance
    * @param duration Duration value
    * @return xsd:duration value
    */
   public static final String printDuration(MSPDIWriter writer, Duration duration)
   {
      String result = null;

      if (duration != null && duration.getDuration() != 0)
      {
         result = printDurationMandatory(writer, duration);
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
    * @param writer parent MSPDIWriter instance
    * @param duration Duration value
    * @return xsd:duration value
    */
   public static final String printDurationMandatory(MSPDIWriter writer, Duration duration)
   {
      String result = null;

      if (duration != null)
      {
         TimeUnit durationType = duration.getUnits();

         if (durationType == TimeUnit.HOURS || durationType == TimeUnit.ELAPSED_HOURS)
         {
            result = new XsdDuration(duration).toString();
         }
         else
         {
            duration = duration.convertUnits(TimeUnit.HOURS, writer.getProjectFile().getProjectProperties());
            result = new XsdDuration(duration).toString();
         }
      }

      return (result);
   }

   /**
    * Print duration time units.
    *
    * @param duration Duration value
    * @param estimated is this an estimated duration
    * @return time units value
    */
   public static final BigInteger printDurationTimeUnits(Duration duration, boolean estimated)
   {
      BigInteger result = null;

      if (duration != null)
      {
         result = printDurationTimeUnits(duration.getUnits(), estimated);
      }

      return (result);
   }

   /**
    * Parse currency.
    *
    * @param value currency value
    * @return currency value
    */
   public static final Double parseCurrency(Number value)
   {
      return (value == null ? null : NumberHelper.getDouble(value.doubleValue() / 100));
   }

   /**
    * Print currency.
    *
    * @param value currency value
    * @return currency value
    */
   public static final BigDecimal printCurrency(Number value)
   {
      return (value == null || value.doubleValue() == 0 ? null : new BigDecimal(value.doubleValue() * 100));
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
   public static final TimeUnit parseDurationTimeUnits(BigInteger value)
   {
      return parseDurationTimeUnits(value, TimeUnit.HOURS);
   }

   /**
    * Parse duration time units.
    *
    * Note that we don't differentiate between confirmed and unconfirmed
    * durations. Unrecognised duration types are default the supplied default value.
    *
    * @param value BigInteger value
    * @param defaultValue if value is null, use this value as the result
    * @return Duration units
    */
   public static final TimeUnit parseDurationTimeUnits(BigInteger value, TimeUnit defaultValue)
   {
      TimeUnit result = defaultValue;

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
            case 53:
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

            default:
            {
               result = PARENT_FILE.get().getProjectProperties().getDefaultDurationUnits();
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
    * @param estimated is this an estimated duration
    * @return BigInteger value
    */
   public static final BigInteger printDurationTimeUnits(TimeUnit value, boolean estimated)
   {
      int result;

      if (value == null)
      {
         value = TimeUnit.HOURS;
      }

      switch (value)
      {
         case MINUTES:
         {
            result = (estimated ? 35 : 3);
            break;
         }

         case ELAPSED_MINUTES:
         {
            result = (estimated ? 36 : 4);
            break;
         }

         case ELAPSED_HOURS:
         {
            result = (estimated ? 38 : 6);
            break;
         }

         case DAYS:
         {
            result = (estimated ? 39 : 7);
            break;
         }

         case ELAPSED_DAYS:
         {
            result = (estimated ? 40 : 8);
            break;
         }

         case WEEKS:
         {
            result = (estimated ? 41 : 9);
            break;
         }

         case ELAPSED_WEEKS:
         {
            result = (estimated ? 42 : 10);
            break;
         }

         case MONTHS:
         {
            result = (estimated ? 43 : 11);
            break;
         }

         case ELAPSED_MONTHS:
         {
            result = (estimated ? 44 : 12);
            break;
         }

         case PERCENT:
         {
            result = (estimated ? 51 : 19);
            break;
         }

         case ELAPSED_PERCENT:
         {
            result = (estimated ? 52 : 20);
            break;
         }

         default:
         case HOURS:
         {
            result = (estimated ? 37 : 5);
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
   public static final Priority parsePriority(BigInteger priority)
   {
      return (priority == null ? null : Priority.getInstance(priority.intValue()));
   }

   /**
    * Print priority.
    *
    * @param priority Priority instance
    * @return priority value
    */
   public static final BigInteger printPriority(Priority priority)
   {
      int result = Priority.MEDIUM;

      if (priority != null)
      {
         result = priority.getValue();
      }

      return (BigInteger.valueOf(result));
   }

   /**
    * Parse duration represented in thousandths of minutes.
    *
    * @param value duration value
    * @return Duration instance
    */
   public static final Duration parseDurationInThousanthsOfMinutes(Number value)
   {
      return parseDurationInFractionsOfMinutes(null, value, TimeUnit.MINUTES, 1000);
   }

   /**
    * Parse duration represented in tenths of minutes.
    *
    * @param value duration value
    * @return Duration instance
    */
   public static final Duration parseDurationInTenthsOfMinutes(Number value)
   {
      return parseDurationInFractionsOfMinutes(null, value, TimeUnit.MINUTES, 10);
   }

   /**
    * Parse duration represented in thousandths of minutes.
    *
    * @param properties project properties
    * @param value duration value
    * @param targetTimeUnit required output time units
    * @return Duration instance
    */
   public static final Duration parseDurationInThousanthsOfMinutes(ProjectProperties properties, Number value, TimeUnit targetTimeUnit)
   {
      return parseDurationInFractionsOfMinutes(properties, value, targetTimeUnit, 1000);
   }

   /**
    * Parse duration represented as tenths of minutes.
    *
    * @param properties project properties
    * @param value duration value
    * @param targetTimeUnit required output time units
    * @return Duration instance
    */
   public static final Duration parseDurationInTenthsOfMinutes(ProjectProperties properties, Number value, TimeUnit targetTimeUnit)
   {
      return parseDurationInFractionsOfMinutes(properties, value, targetTimeUnit, 10);
   }

   /**
    * Print duration in thousandths of minutes.
    *
    * @param duration Duration instance
    * @return duration in thousandths of minutes
    */
   public static final BigInteger printDurationInIntegerThousandthsOfMinutes(Duration duration)
   {
      BigInteger result = null;
      if (duration != null && duration.getDuration() != 0)
      {
         result = BigInteger.valueOf((long) printDurationFractionsOfMinutes(duration, 1000));
      }
      return result;
   }

   /**
    * Print duration in thousandths of minutes.
    *
    * @param duration Duration instance
    * @return duration in thousandths of minutes
    */
   public static final BigDecimal printDurationInDecimalThousandthsOfMinutes(Duration duration)
   {
      BigDecimal result = null;
      if (duration != null && duration.getDuration() != 0)
      {
         result = BigDecimal.valueOf(printDurationFractionsOfMinutes(duration, 1000));
      }
      return result;
   }

   /**
    * Print duration in tenths of minutes.
    *
    * @param duration Duration instance
    * @return duration in tenths of minutes
    */
   public static final BigInteger printDurationInIntegerTenthsOfMinutes(Duration duration)
   {
      BigInteger result = null;

      if (duration != null && duration.getDuration() != 0)
      {
         result = BigInteger.valueOf((long) printDurationFractionsOfMinutes(duration, 10));
      }

      return result;
   }

   /**
    * Parse duration represented as an arbitrary fraction of minutes.
    *
    * @param properties project properties
    * @param value duration value
    * @param targetTimeUnit required output time units
    * @param factor required fraction of a minute
    * @return Duration instance
    */
   private static final Duration parseDurationInFractionsOfMinutes(ProjectProperties properties, Number value, TimeUnit targetTimeUnit, int factor)
   {
      Duration result = null;

      if (value != null)
      {
         result = Duration.getInstance(value.intValue() / factor, TimeUnit.MINUTES);
         if (targetTimeUnit != result.getUnits())
         {
            result = result.convertUnits(targetTimeUnit, properties);
         }
      }

      return (result);
   }

   /**
    * Print a duration represented by an arbitrary fraction of minutes.
    *
    * @param duration Duration instance
    * @param factor required factor
    * @return duration represented as an arbitrary fraction of minutes
    */
   private static final double printDurationFractionsOfMinutes(Duration duration, int factor)
   {
      double result = 0;

      if (duration != null)
      {
         result = duration.getDuration();

         switch (duration.getUnits())
         {
            case HOURS:
            case ELAPSED_HOURS:
            {
               result *= 60;
               break;
            }

            case DAYS:
            {
               result *= (60 * 8);
               break;
            }

            case ELAPSED_DAYS:
            {
               result *= (60 * 24);
               break;
            }

            case WEEKS:
            {
               result *= (60 * 8 * 5);
               break;
            }

            case ELAPSED_WEEKS:
            {
               result *= (60 * 24 * 7);
               break;
            }

            case MONTHS:
            {
               result *= (60 * 8 * 5 * 4);
               break;
            }

            case ELAPSED_MONTHS:
            {
               result *= (60 * 24 * 30);
               break;
            }

            case YEARS:
            {
               result *= (60 * 8 * 5 * 52);
               break;
            }

            case ELAPSED_YEARS:
            {
               result *= (60 * 24 * 365);
               break;
            }

            default:
            {
               break;
            }
         }
      }

      result *= factor;

      return (result);
   }

   /**
    * Print rate.
    *
    * @param rate Rate instance
    * @return rate value
    */
   public static final BigDecimal printRate(Rate rate)
   {
      BigDecimal result = null;
      if (rate != null && rate.getAmount() != 0)
      {
         result = new BigDecimal(rate.getAmount());
      }
      return result;
   }

   /**
    * Parse rate.
    *
    * @param value rate value
    * @return Rate instance
    */
   public static final Rate parseRate(BigDecimal value)
   {
      Rate result = null;

      if (value != null)
      {
         result = new Rate(value, TimeUnit.HOURS);
      }

      return (result);
   }

   /**
    * Print a day.
    *
    * @param day Day instance
    * @return day value
    */
   public static final BigInteger printDay(Day day)
   {
      return (day == null ? null : BigInteger.valueOf(day.getValue() - 1));
   }

   /**
    * Parse a day.
    *
    * @param value day value
    * @return Day instance
    */
   public static final Day parseDay(Number value)
   {
      return (Day.getInstance(NumberHelper.getInt(value) + 1));
   }

   /**
    * Parse a constraint type.
    *
    * @param value constraint type value
    * @return ConstraintType instance
    */
   public static final ConstraintType parseConstraintType(Number value)
   {
      return (ConstraintType.getInstance(value));
   }

   /**
    * Print a constraint type.
    *
    * @param value ConstraintType instance
    * @return constraint type value
    */
   public static final BigInteger printConstraintType(ConstraintType value)
   {
      return (value == null ? null : BigInteger.valueOf(value.getValue()));
   }

   /**
    * Print a task UID.
    *
    * @param value task UID
    * @return task UID string
    */
   public static final String printTaskUID(Integer value)
   {
      ProjectFile file = PARENT_FILE.get();
      if (file != null)
      {
         file.getEventManager().fireTaskWrittenEvent(file.getTaskByUniqueID(value));
      }
      return (value.toString());
   }

   /**
    * Parse a task UID.
    *
    * @param value task UID string
    * @return task UID
    */
   public static final Integer parseTaskUID(String value)
   {
      return (Integer.valueOf(value));
   }

   /**
    * Print a resource UID.
    *
    * @param value resource UID value
    * @return resource UID string
    */
   public static final String printResourceUID(Integer value)
   {
      ProjectFile file = PARENT_FILE.get();
      if (file != null)
      {
         file.getEventManager().fireResourceWrittenEvent(file.getResourceByUniqueID(value));
      }
      return (value.toString());
   }

   /**
    * Parse a resource UID.
    *
    * @param value resource UID string
    * @return resource UID value
    */
   public static final Integer parseResourceUID(String value)
   {
      return (Integer.valueOf(value));
   }

   /**
    * Print a boolean.
    *
    * @param value boolean
    * @return boolean value
    */
   public static final String printBoolean(Boolean value)
   {
      return (value == null || !value.booleanValue() ? "0" : "1");
   }

   /**
    * Parse a boolean.
    *
    * @param value boolean
    * @return Boolean value
    */
   public static final Boolean parseBoolean(String value)
   {
      return (value == null || value.charAt(0) != '1' ? Boolean.FALSE : Boolean.TRUE);
   }

   /**
    * Print a time value.
    *
    * @param value time value
    * @return time value
    */
   public static final String printTime(Calendar value)
   {
      return (value == null ? null : getTimeFormat().format(value.getTime()));
   }

   /**
    * Parse a time value.
    *
    * @param value time value
    * @return time value
    */
   public static final Calendar parseTime(String value)
   {
      Calendar cal = null;
      if (value != null && value.length() != 0)
      {
         cal = Calendar.getInstance();
         try
         {
            cal.setTime(getTimeFormat().parse(value));
         }

         catch (ParseException ex)
         {
            cal = null;
         }
      }
      return (cal);
   }

   /**
    * Print a date time value.
    *
    * @param value date time value
    * @return string representation
    */
   public static final String printDateTime(Calendar value)
   {
      return (value == null ? null : getDateFormat().format(value.getTime()));
   }

   /**
    * Parse a date time value.
    *
    * @param value string representation
    * @return date time value
    */
   public static final Calendar parseDateTime(String value)
   {
      Calendar result = null;

      if (value != null && value.length() != 0)
      {
         try
         {
            result = Calendar.getInstance();
            result.setTime(getDateFormat().parse(value));
         }

         catch (ParseException ex)
         {
            result = null;
         }
      }

      return (result);
   }

   /**
    * Print method for a string: returns the string unchanged.
    * This is used to enable to string representation of an
    * xsd:datetime to be generated by MPXJ.
    *
    * @param value string value
    * @return string value
    */
   public static final String printString(String value)
   {
      return (value);
   }

   /**
    * Parse method for a string: returns the string unchanged.
    * This is used to enable to string representation of an
    * xsd:datetime to be processed by MPXJ.

    * @param value string value
    * @return string value
    */
   public static final String parseString(String value)
   {
      return (value);
   }

   /**
    * This method is called to set the parent file for the current
    * write operation. This allows task and resource write events
    * to be captured and passed to any file listeners.
    *
    * @param file parent file instance
    */
   public static final void setParentFile(ProjectFile file)
   {
      PARENT_FILE.set(file);
   }

   /**
    * Retrieve a number formatter.
    *
    * @return NumberFormat instance
    */
   private static final NumberFormat getNumberFormat()
   {
      NumberFormat format = NUMBER_FORMAT.get();
      if (format == null)
      {
         // XML numbers should use . as decimal separator and no grouping.
         format = new DecimalFormat("#.##", new DecimalFormatSymbols(Locale.US));
         format.setGroupingUsed(false);
         NUMBER_FORMAT.set(format);
      }
      return (format);
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
         df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
         df.setLenient(false);
      }
      return (df);

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
      }
      return (df);

   }

   /**
    * Detect numbers using comma as a decimal separator and replace with period.
    *
    * @param value original numeric value
    * @return corrected numeric value
    */
   private static final String correctNumberFormat(String value)
   {
      String result;
      int index = value.indexOf(',');
      if (index == -1)
      {
         result = value;
      }
      else
      {
         char[] chars = value.toCharArray();
         chars[index] = '.';
         result = new String(chars);
      }
      return result;
   }

   private static final ThreadLocal<DateFormat> DATE_FORMAT = new ThreadLocal<DateFormat>();
   private static final ThreadLocal<DateFormat> TIME_FORMAT = new ThreadLocal<DateFormat>();
   private static final ThreadLocal<NumberFormat> NUMBER_FORMAT = new ThreadLocal<NumberFormat>();
   private static final BigDecimal BIGDECIMAL_ONE = BigDecimal.valueOf(1);
   private static final ThreadLocal<ProjectFile> PARENT_FILE = new ThreadLocal<ProjectFile>();
}
