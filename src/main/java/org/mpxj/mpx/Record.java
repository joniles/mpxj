/*
 * file:       Record.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2002-2003
 * date:       01/01/2003
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

package org.mpxj.mpx;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.List;
import java.util.Locale;

import org.mpxj.AccrueType;
import org.mpxj.CodePage;
import org.mpxj.CurrencySymbolPosition;
import org.mpxj.DateOrder;
import org.mpxj.DayType;
import org.mpxj.Duration;
import org.mpxj.MPXJException;
import org.mpxj.ProjectDateFormat;
import org.mpxj.ProjectFile;
import org.mpxj.ProjectTimeFormat;
import org.mpxj.Rate;
import org.mpxj.ScheduleFrom;
import org.mpxj.TimeUnit;
import org.mpxj.common.Tokenizer;

/**
 * This class is used to represent a record in an MPX file.
 */
final class Record
{
   /**
    * This constructor takes a stream of tokens and extracts the
    * fields of an individual record from those tokens.
    *
    * @param file parent project file
    * @param locale target locale
    * @param tk tokenizer providing the input stream of tokens
    * @param formats formats used when parsing data
    * @throws MPXJException normally thrown when parsing fails
    */
   Record(ProjectFile file, Locale locale, Tokenizer tk, MPXJFormats formats)
      throws MPXJException
   {
      try
      {
         m_locale = locale;

         m_formats = formats;

         List<String> list = new ArrayList<>();

         while (tk.nextToken() == Tokenizer.TT_WORD)
         {
            list.add(tk.getToken());
         }

         if (!list.isEmpty())
         {
            setRecordNumber(file, list);
            m_fields = list.toArray(new String[0]);
         }
      }

      catch (IOException ex)
      {
         throw new MPXJException(MPXJException.INVALID_RECORD, ex);
      }
   }

   /**
    * Pop the record number from the front of the list, and parse it to ensure that
    * it is a valid integer.
    *
    * @param file parent file
    * @param list MPX record
    */
   private void setRecordNumber(ProjectFile file, List<String> list)
   {
      try
      {
         String number = list.remove(0);

         // We expect `PX` when we read the file creation record.
         // Don't attempt to parse this.
         if (!"PX".equals(number))
         {
            m_recordNumber = Integer.valueOf(number);
         }
      }
      catch (NumberFormatException ex)
      {
         // Malformed MPX file: the record number isn't a valid integer
         // Catch the exception here, leaving m_recordNumber as null
         // so we will skip this record entirely.
         file.addIgnoredError(ex);
      }
   }

   /**
    * Retrieves the record number associated with this record.
    *
    * @return the record number associated with this record
    */
   public Integer getRecordNumber()
   {
      return (m_recordNumber);
   }

   /**
    * Accessor method used to retrieve a String object representing the
    * contents of an individual field. If the field does not exist in the
    * record, null is returned.
    *
    * @param field the index number of the field to be retrieved
    * @return the value of the required field
    */
   public String getString(int field)
   {
      String result;

      if (field < m_fields.length)
      {
         result = m_fields[field];

         if (result != null)
         {
            result = result.replace(MPXConstants.EOL_PLACEHOLDER, '\n');
         }
      }
      else
      {
         result = null;
      }

      return (result);
   }

   /**
    * Accessor method used to retrieve a char representing the
    * contents of an individual field. If the field does not exist in the
    * record, the default character is returned.
    *
    * @param field the index number of the field to be retrieved
    * @return the value of the required field
    */
   public Character getCharacter(int field)
   {
      Character result;

      if ((field < m_fields.length) && (!m_fields[field].isEmpty()))
      {
         result = Character.valueOf(m_fields[field].charAt(0));
      }
      else
      {
         result = null;
      }

      return (result);
   }

   /**
    * Accessor method used to retrieve a Float object representing the
    * contents of an individual field. If the field does not exist in the
    * record, null is returned.
    *
    * @param field the index number of the field to be retrieved
    * @return the value of the required field
    */
   public Number getFloat(int field) throws MPXJException
   {
      try
      {
         Number result;

         if ((field < m_fields.length) && (!m_fields[field].isEmpty()))
         {
            result = m_formats.getDecimalFormat().parse(m_fields[field]);
         }
         else
         {
            result = null;
         }

         return (result);
      }

      catch (ParseException ex)
      {
         throw new MPXJException("Failed to parse float", ex);
      }
   }

   /**
    * Accessor method used to retrieve an Integer object representing the
    * contents of an individual field. If the field does not exist in the
    * record, null is returned.
    *
    * @param field the index number of the field to be retrieved
    * @return the value of the required field
    */
   public Integer getInteger(int field)
   {
      Integer result;

      if ((field < m_fields.length) && (!m_fields[field].isEmpty()))
      {
         result = Integer.valueOf(m_fields[field]);
      }
      else
      {
         result = null;
      }

      return (result);
   }

   /**
    * Accessor method used to retrieve a LocalDateTime instance representing the
    * contents of an individual field. If the field does not exist in the
    * record, null is returned.
    *
    * @param field the index number of the field to be retrieved
    * @return the value of the required field
    */
   public LocalDateTime getDateTime(int field)
   {
      LocalDateTime result = null;

      if ((field < m_fields.length) && (!m_fields[field].isEmpty()))
      {
         result = m_formats.parseDateTime(m_fields[field]);
      }

      return result;
   }

   /**
    * Accessor method used to retrieve a LocalDateTime instance representing the
    * contents of an individual field. If the field does not exist in the
    * record, null is returned.
    *
    * @param field the index number of the field to be retrieved
    * @return the value of the required field
    */
   public LocalDateTime getDate(int field)
   {
      LocalDateTime result;

      if ((field < m_fields.length) && (!m_fields[field].isEmpty()))
      {
         result = m_formats.parseDate(m_fields[field]);
      }
      else
      {
         result = null;
      }

      return (result);
   }

   /**
    * Accessor method used to retrieve an Date instance representing the
    * contents of an individual field. If the field does not exist in the
    * record, null is returned.
    *
    * @param field the index number of the field to be retrieved
    * @return the value of the required field
    */
   public LocalTime getTime(int field)
   {
      LocalTime result;

      if ((field < m_fields.length) && (!m_fields[field].isEmpty()))
      {
         result = m_formats.parseTime(m_fields[field]);
      }
      else
      {
         result = null;
      }

      return (result);
   }

   /**
    * Accessor method used to retrieve a Boolean object representing the
    * contents of an individual field. If the field does not exist in the
    * record, null is returned.
    *
    * @param field the index number of the field to be retrieved
    * @return the value of the required field
    */
   public boolean getNumericBoolean(int field)
   {
      boolean result = false;

      if ((field < m_fields.length) && (!m_fields[field].isEmpty()))
      {
         result = Integer.parseInt(m_fields[field]) == 1;
      }

      return (result);
   }

   /**
    * Accessor method used to retrieve an Rate object representing the
    * contents of an individual field. If the field does not exist in the
    * record, null is returned.
    *
    * @param field the index number of the field to be retrieved
    * @return the value of the required field
    * @throws MPXJException normally thrown when parsing fails
    */
   public Rate getRate(int field) throws MPXJException
   {
      Rate result;

      if ((field < m_fields.length) && (!m_fields[field].isEmpty()))
      {
         try
         {
            String rate = m_fields[field];
            int index = rate.indexOf('/');
            double amount;
            TimeUnit units;

            if (index == -1)
            {
               amount = m_formats.getCurrencyFormat().parse(rate).doubleValue();
               units = TimeUnit.HOURS;
            }
            else
            {
               amount = m_formats.getCurrencyFormat().parse(rate.substring(0, index)).doubleValue();
               units = TimeUnitUtility.getInstance(rate.substring(index + 1), m_locale);
            }

            result = new Rate(amount, units);
         }

         catch (ParseException ex)
         {
            throw new MPXJException("Failed to parse rate", ex);
         }
      }
      else
      {
         result = null;
      }

      return (result);
   }

   /**
    * Accessor method used to retrieve an Number instance representing the
    * contents of an individual field. If the field does not exist in the
    * record, null is returned.
    *
    * @param field the index number of the field to be retrieved
    * @return the value of the required field
    * @throws MPXJException normally thrown when parsing fails
    */
   public Number getCurrency(int field) throws MPXJException
   {
      Number result;

      if ((field < m_fields.length) && (!m_fields[field].isEmpty()))
      {
         try
         {
            result = m_formats.getCurrencyFormat().parse(m_fields[field]);
         }

         catch (ParseException ex)
         {
            throw new MPXJException("Failed to parse currency", ex);
         }
      }
      else
      {
         result = null;
      }

      return (result);
   }

   /**
    * Accessor method used to retrieve an Number instance representing the
    * contents of an individual field. If the field does not exist in the
    * record, null is returned.
    *
    * @param field the index number of the field to be retrieved
    * @return the value of the required field
    * @throws MPXJException normally thrown when parsing fails
    */
   public Number getPercentage(int field) throws MPXJException
   {
      Number result;

      if ((field < m_fields.length) && (!m_fields[field].isEmpty()))
      {
         try
         {
            result = m_formats.getPercentageDecimalFormat().parse(m_fields[field]);
         }

         catch (ParseException ex)
         {
            throw new MPXJException("Failed to parse percentage", ex);
         }
      }
      else
      {
         result = null;
      }

      return (result);
   }

   /**
    * Accessor method used to retrieve an Duration object representing the
    * contents of an individual field. If the field does not exist in the
    * record, null is returned.
    *
    * @param field the index number of the field to be retrieved
    * @return the value of the required field
    * @throws MPXJException normally thrown when parsing fails
    */
   public Duration getDuration(int field) throws MPXJException
   {
      Duration result;

      if ((field < m_fields.length) && (!m_fields[field].isEmpty()))
      {
         result = DurationUtility.getInstance(m_fields[field], m_formats.getDurationDecimalFormat(), m_locale);
      }
      else
      {
         result = null;
      }

      return (result);
   }

   /**
    * Accessor method used to retrieve a Number instance representing the
    * contents of an individual field. If the field does not exist in the
    * record, null is returned.
    *
    * @param field the index number of the field to be retrieved
    * @return the value of the required field
    * @throws MPXJException normally thrown when parsing fails
    */
   public Number getUnits(int field) throws MPXJException
   {
      Number result;

      if ((field < m_fields.length) && (!m_fields[field].isEmpty()))
      {
         try
         {
            result = Double.valueOf(m_formats.getUnitsDecimalFormat().parse(m_fields[field]).doubleValue() * 100);
         }

         catch (ParseException ex)
         {
            throw new MPXJException("Failed to parse units", ex);
         }
      }
      else
      {
         result = null;
      }

      return (result);
   }

   /**
    * Accessor method used to retrieve an Integer object representing the
    * contents of an individual field. If the field does not exist in the
    * record, null is returned.
    *
    * @param field the index number of the field to be retrieved
    * @return the value of the required field
    */
   public TimeUnit getTimeUnit(int field)
   {
      TimeUnit result;

      if ((field < m_fields.length) && (!m_fields[field].isEmpty()))
      {
         result = TimeUnit.getInstance(Integer.parseInt(m_fields[field]));
      }
      else
      {
         result = TimeUnit.DAYS;
      }

      return (result);
   }

   /**
    * Accessor method used to retrieve an Integer object representing the
    * contents of an individual field. If the field does not exist in the
    * record, null is returned.
    *
    * @param field the index number of the field to be retrieved
    * @return the value of the required field
    */
   public ProjectTimeFormat getTimeFormat(int field)
   {
      ProjectTimeFormat result;

      if ((field < m_fields.length) && (!m_fields[field].isEmpty()))
      {
         result = ProjectTimeFormat.getInstance(Integer.parseInt(m_fields[field]));
      }
      else
      {
         result = ProjectTimeFormat.TWELVE_HOUR;
      }

      return (result);
   }

   /**
    * Accessor method used to retrieve an Integer object representing the
    * contents of an individual field. If the field does not exist in the
    * record, null is returned.
    *
    * @param field the index number of the field to be retrieved
    * @return the value of the required field
    */
   public ScheduleFrom getScheduleFrom(int field)
   {
      ScheduleFrom result;

      if ((field < m_fields.length) && (!m_fields[field].isEmpty()))
      {
         result = ScheduleFrom.getInstance(Integer.parseInt(m_fields[field]));
      }
      else
      {
         result = ScheduleFrom.START;
      }

      return (result);
   }

   /**
    * Accessor method used to retrieve an Integer object representing the
    * contents of an individual field. If the field does not exist in the
    * record, null is returned.
    *
    * @param field the index number of the field to be retrieved
    * @return the value of the required field
    */
   public DateOrder getDateOrder(int field)
   {
      DateOrder result;

      if ((field < m_fields.length) && (!m_fields[field].isEmpty()))
      {
         result = DateOrder.getInstance(Integer.parseInt(m_fields[field]));
      }
      else
      {
         result = DateOrder.MDY;
      }

      return (result);
   }

   /**
    * Accessor method used to retrieve an Integer object representing the
    * contents of an individual field. If the field does not exist in the
    * record, null is returned.
    *
    * @param field the index number of the field to be retrieved
    * @return the value of the required field
    */
   public CurrencySymbolPosition getCurrencySymbolPosition(int field)
   {
      CurrencySymbolPosition result;

      if ((field < m_fields.length) && (!m_fields[field].isEmpty()))
      {
         result = CurrencySymbolPosition.getInstance(Integer.parseInt(m_fields[field]));
      }
      else
      {
         result = CurrencySymbolPosition.BEFORE;
      }

      return (result);
   }

   /**
    * Accessor method used to retrieve an Integer object representing the
    * contents of an individual field. If the field does not exist in the
    * record, null is returned.
    *
    * @param field the index number of the field to be retrieved
    * @return the value of the required field
    */
   public ProjectDateFormat getDateFormat(int field)
   {
      ProjectDateFormat result;

      if ((field < m_fields.length) && (!m_fields[field].isEmpty()))
      {
         result = ProjectDateFormat.getInstance(Integer.parseInt(m_fields[field]));
      }
      else
      {
         result = ProjectDateFormat.DD_MM_YY;
      }

      return (result);
   }

   /**
    * Retrieves a CodePage instance. Defaults to ANSI.
    *
    * @param field the index number of the field to be retrieved
    * @return the value of the required field
    */
   public CodePage getCodePage(int field)
   {
      CodePage result;

      if ((field < m_fields.length) && (!m_fields[field].isEmpty()))
      {
         result = CodePage.getInstance(m_fields[field]);
      }
      else
      {
         result = CodePage.getInstance(null);
      }

      return (result);
   }

   /**
    * Accessor method to retrieve an accrue type instance.
    *
    * @param field the index number of the field to be retrieved
    * @return the value of the required field
    */
   public AccrueType getAccrueType(int field)
   {
      AccrueType result;

      if ((field < m_fields.length) && (!m_fields[field].isEmpty()))
      {
         result = AccrueTypeUtility.getInstance(m_fields[field], m_locale);
      }
      else
      {
         result = null;
      }

      return (result);
   }

   /**
    * Accessor method to retrieve a Boolean instance.
    *
    * @param field the index number of the field to be retrieved
    * @param falseText locale specific text representing false
    * @return the value of the required field
    */
   public Boolean getBoolean(int field, String falseText)
   {
      Boolean result;

      if ((field < m_fields.length) && (!m_fields[field].isEmpty()))
      {
         String value = m_fields[field];

         // Handle common non-standard true/false values
         if (value.equals("0"))
         {
            return Boolean.FALSE;
         }

         if (value.equals("1"))
         {
            return Boolean.TRUE;
         }

         // Handle standard true/false value
         result = Boolean.valueOf(!value.equalsIgnoreCase(falseText));
      }
      else
      {
         result = null;
      }

      return (result);
   }

   /**
    * Accessor method to retrieve a DayType instance.
    *
    * @param field the index number of the field to be retrieved
    * @return the value of the required field
    */
   public DayType getDayType(int field)
   {
      DayType result;

      if ((field < m_fields.length) && (!m_fields[field].isEmpty()))
      {
         result = DayType.getInstance(Integer.parseInt(m_fields[field]));
      }
      else
      {
         result = DayType.DEFAULT;
      }

      return (result);
   }

   /**
    * This method returns the number of fields present in this record.
    *
    * @return number of fields
    */
   public int getLength()
   {
      return (m_fields.length);
   }

   @Override public String toString()
   {
      return (Arrays.toString(m_fields));
   }

   /**
    * Target locale.
    */
   private final Locale m_locale;

   /**
    * Current record number.
    */
   private Integer m_recordNumber;

   /**
    * Array of field data.
    */
   private String[] m_fields;

   private final MPXJFormats m_formats;
}
