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

package net.sf.mpxj.mpx;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;

import net.sf.mpxj.AccrueType;
import net.sf.mpxj.CodePage;
import net.sf.mpxj.CurrencySymbolPosition;
import net.sf.mpxj.DateOrder;
import net.sf.mpxj.Duration;
import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectDateFormat;
import net.sf.mpxj.ProjectTimeFormat;
import net.sf.mpxj.Rate;
import net.sf.mpxj.ScheduleFrom;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.common.Tokenizer;

/**
 * This class is used to represent a record in an MPX file.
 */
final class Record
{
   /**
    * This constructor takes a stream of tokens and extracts the
    * fields of an individual record from those tokens.
    *
    * @param locale target locale
    * @param tk tokenizer providing the input stream of tokens
    * @param formats formats used when parsing data
    * @throws MPXJException normally thrown when parsing fails
    */
   Record(Locale locale, Tokenizer tk, MPXJFormats formats)
      throws MPXJException
   {
      try
      {
         m_locale = locale;

         m_formats = formats;

         LinkedList<String> list = new LinkedList<String>();

         while (tk.nextToken() == Tokenizer.TT_WORD)
         {
            list.add(tk.getToken());
         }

         if (list.size() > 0)
         {
            m_recordNumber = list.remove(0);
            m_fields = list.toArray(new String[list.size()]);
         }
      }

      catch (IOException ex)
      {
         throw new MPXJException(MPXJException.INVALID_RECORD, ex);
      }
   }

   /**
    * Retrieves the record number associated with this record.
    *
    * @return the record number associated with this record
    */
   public String getRecordNumber()
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

      if ((field < m_fields.length) && (m_fields[field].length() != 0))
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

         if ((field < m_fields.length) && (m_fields[field].length() != 0))
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

      if ((field < m_fields.length) && (m_fields[field].length() != 0))
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
    * Accessor method used to retrieve an Date instance representing the
    * contents of an individual field. If the field does not exist in the
    * record, null is returned.
    *
    * @param field the index number of the field to be retrieved
    * @return the value of the required field
    * @throws MPXJException normally thrown when parsing fails
    */
   public Date getDateTime(int field) throws MPXJException
   {
      Date result = null;

      if ((field < m_fields.length) && (m_fields[field].length() != 0))
      {
         try
         {
            result = m_formats.getDateTimeFormat().parse(m_fields[field]);
         }

         catch (ParseException ex)
         {
            // Failed to parse a full date time.
         }

         //
         // Fall back to trying just parsing the date component
         //
         if (result == null)
         {
            try
            {
               result = m_formats.getDateFormat().parse(m_fields[field]);
            }

            catch (ParseException ex)
            {
               throw new MPXJException("Failed to parse date time", ex);
            }
         }
      }

      return result;
   }

   /**
    * Accessor method used to retrieve an Date instance representing the
    * contents of an individual field. If the field does not exist in the
    * record, null is returned.
    *
    * @param field the index number of the field to be retrieved
    * @return the value of the required field
    * @throws MPXJException normally thrown when parsing fails
    */
   public Date getDate(int field) throws MPXJException
   {
      try
      {
         Date result;

         if ((field < m_fields.length) && (m_fields[field].length() != 0))
         {
            result = m_formats.getDateFormat().parse(m_fields[field]);
         }
         else
         {
            result = null;
         }

         return (result);
      }

      catch (ParseException ex)
      {
         throw new MPXJException("Failed to parse date", ex);
      }
   }

   /**
    * Accessor method used to retrieve an Date instance representing the
    * contents of an individual field. If the field does not exist in the
    * record, null is returned.
    *
    * @param field the index number of the field to be retrieved
    * @return the value of the required field
    * @throws MPXJException normally thrown when parsing fails
    */
   public Date getTime(int field) throws MPXJException
   {
      try
      {
         Date result;

         if ((field < m_fields.length) && (m_fields[field].length() != 0))
         {
            result = m_formats.getTimeFormat().parse(m_fields[field]);
         }
         else
         {
            result = null;
         }

         return (result);
      }

      catch (ParseException ex)
      {
         throw new MPXJException("Failed to parse time", ex);
      }
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

      if ((field < m_fields.length) && (m_fields[field].length() != 0))
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

      if ((field < m_fields.length) && (m_fields[field].length() != 0))
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

      if ((field < m_fields.length) && (m_fields[field].length() != 0))
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

      if ((field < m_fields.length) && (m_fields[field].length() != 0))
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

      if ((field < m_fields.length) && (m_fields[field].length() != 0))
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

      if ((field < m_fields.length) && (m_fields[field].length() != 0))
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

      if ((field < m_fields.length) && (m_fields[field].length() != 0))
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

      if ((field < m_fields.length) && (m_fields[field].length() != 0))
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

      if ((field < m_fields.length) && (m_fields[field].length() != 0))
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

      if ((field < m_fields.length) && (m_fields[field].length() != 0))
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

      if ((field < m_fields.length) && (m_fields[field].length() != 0))
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
      ProjectDateFormat result = null;

      if ((field < m_fields.length) && (m_fields[field].length() != 0))
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

      if ((field < m_fields.length) && (m_fields[field].length() != 0))
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

      if ((field < m_fields.length) && (m_fields[field].length() != 0))
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

      if ((field < m_fields.length) && (m_fields[field].length() != 0))
      {
         result = ((m_fields[field].equalsIgnoreCase(falseText) == true) ? Boolean.FALSE : Boolean.TRUE);
      }
      else
      {
         result = null;
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

   /**
    * {@inheritDoc}
    */
   @Override public String toString()
   {
      return (Arrays.toString(m_fields));
   }

   /**
    * Target locale.
    */
   private Locale m_locale;

   /**
    * Current record number.
    */
   private String m_recordNumber;

   /**
    * Array of field data.
    */
   private String[] m_fields;

   private MPXJFormats m_formats;
}
