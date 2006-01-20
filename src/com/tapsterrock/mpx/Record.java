/*
 * file:       Record.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2002-2003
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

package com.tapsterrock.mpx;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;


/**
 * This class is used to represent a record in an MPX file.
 */
final class Record
{
   /**
    * This constructor takes a stream of tokens and extracts the
    * fields of an individual record from those tokens.
    *
    * @param parent parent MPX file
    * @param tk tokenizer providing the input stream of tokens
    * @param formats formats used when parsing data
    * @throws MPXException normally thrown when parsing fails
    */
   Record (ProjectFile parent, Tokenizer tk, MPXFormats formats)
      throws MPXException
   {
      try
      {
         m_parent = parent;

         m_formats = formats;
         
         LinkedList list = new LinkedList();

         while (tk.nextToken() == Tokenizer.TT_WORD)
         {
            list.add(tk.getToken());
         }

         if (list.size() > 0)
         {
            m_recordNumber = (String)list.remove(0);
            m_fields = (String[])list.toArray(new String[list.size()]);
         }
      }

      catch (IOException ex)
      {
         throw new MPXException(MPXException.INVALID_RECORD, ex);
      }
   }
   
   /**
    * Retrieves the record number associated with this record.
    *
    * @return the record number asscoiated with this record
    */
   public String getRecordNumber ()
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
   public String getString (int field)
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
   public Character getCharacter (int field)
   {
      Character result;

      if ((field < m_fields.length) && (m_fields[field].length() != 0))
      {
         result = new Character(m_fields[field].charAt(0));
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
   public Float getFloat (int field)
      throws MPXException
   {
      try
      {
         Float result;
   
         if ((field < m_fields.length) && (m_fields[field].length() != 0))
         {
            result = new Float(m_formats.getDecimalFormat().parse(m_fields[field]).floatValue());
         }
         else
         {
            result = null;
         }
   
         return (result);
      }
      
      catch (ParseException ex)
      {
         throw new MPXException ("Failed to parse float", ex);
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
   public Integer getInteger (int field)
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
    * Accessor method used to retrieve an MPXDate object representing the
    * contents of an individual field. If the field does not exist in the
    * record, null is returned.
    *
    * @param field the index number of the field to be retrieved
    * @return the value of the required field
    * @throws MPXException normally thrown when parsing fails
    */
   public Date getDateTime (int field)
      throws MPXException
   {
      try
      {
         Date result;
   
         if ((field < m_fields.length) && (m_fields[field].length() != 0))
         {
            result = m_parent.getDateTimeFormat().parse(m_fields[field]);
         }
         else
         {
            result = null;
         }
   
         return (result);
      }
      
      catch (ParseException ex)
      {
         throw new MPXException ("Failed to parse date time", ex);
      }
   }

   /**
    * Accessor method used to retrieve an MPXDate object representing the
    * contents of an individual field. If the field does not exist in the
    * record, null is returned.
    *
    * @param field the index number of the field to be retrieved
    * @return the value of the required field
    * @throws MPXException normally thrown when parsing fails
    */
   public Date getDate (int field)
      throws MPXException
   {
      try
      {
         Date result;
   
         if ((field < m_fields.length) && (m_fields[field].length() != 0))
         {
            result = m_parent.getDateFormat().parse(m_fields[field]);
         }
         else
         {          
            result = null;
         }
         
         return (result);         
      }
      
      catch (ParseException ex)
      {
         throw new MPXException ("Failed to parse date", ex);
      }
   }

   /**
    * Accessor method used to retrieve an MPXTime object representing the
    * contents of an individual field. If the field does not exist in the
    * record, null is returned.
    *
    * @param field the index number of the field to be retrieved
    * @return the value of the required field
    * @throws MPXException normally thrown when parsing fails
    */
   public Date getTime (int field)
      throws MPXException
   {
      try
      {
         Date result;
   
         if ((field < m_fields.length) && (m_fields[field].length() != 0))
         {
            result = m_parent.getTimeFormat().parse(m_fields[field]);
         }
         else
         {
            result = null;
         }
   
         return (result);
      }
      
      catch (ParseException ex)
      {
         throw new MPXException ("Failed to parse time", ex);
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
   public boolean getNumericBoolean (int field)
   {
      boolean result = false;

      if ((field < m_fields.length) && (m_fields[field].length() != 0))
      {
         result = Integer.parseInt(m_fields[field])==1?true:false;
      }

      return (result);
   }

   /**
    * Accessor method used to retrieve an MPXRate object representing the
    * contents of an individual field. If the field does not exist in the
    * record, null is returned.
    *
    * @param field the index number of the field to be retrieved
    * @return the value of the required field
    * @throws MPXException normally thrown when parsing fails
    */
   public MPXRate getRate (int field)
      throws MPXException
   {
      MPXRate result;

      if ((field < m_fields.length) && (m_fields[field].length() != 0))
      {
         result = new MPXRate(m_formats.getCurrencyFormat(), m_fields[field], m_parent.getLocale());
      }
      else
      {
         result = null;
      }

      return (result);
   }

   /**
    * Accessor method used to retrieve an MPXCurrency object representing the
    * contents of an individual field. If the field does not exist in the
    * record, null is returned.
    *
    * @param field the index number of the field to be retrieved
    * @return the value of the required field
    * @throws MPXException normally thrown when parsing fails
    */
   public Double getCurrency (int field)
      throws MPXException
   {
      Double result;

      if ((field < m_fields.length) && (m_fields[field].length() != 0))
      {
         try
         {
            NumberFormat format = m_formats.getCurrencyFormat();
            double value = format.parse(m_fields[field]).doubleValue();
            result = NumberUtility.getDouble(value);
         }
         
         catch (ParseException ex)
         {
            throw new MPXException ("Failed to parse currency", ex);
         }
      }
      else
      {
         result = null;
      }

      return (result);
   }

   /**
    * Accessor method used to retrieve an MPXPercentage object representing the
    * contents of an individual field. If the field does not exist in the
    * record, null is returned.
    *
    * @param field the index number of the field to be retrieved
    * @return the value of the required field
    * @throws MPXException normally thrown when parsing fails
    */
   public Double getPercentage (int field)
      throws MPXException
   {
      Double result;

      if ((field < m_fields.length) && (m_fields[field].length() != 0))
      {
         try
         {
            double value = m_parent.getPercentageDecimalFormat().parse(m_fields[field]).doubleValue();
            result = NumberUtility.getDouble(value);
         }
         
         catch (ParseException ex)
         {
            throw new MPXException ("Failed to parse percentage", ex);
         }         
      }
      else
      {
         result = null;
      }

      return (result);
   }

   /**
    * Accessor method used to retrieve an MPXDuration object representing the
    * contents of an individual field. If the field does not exist in the
    * record, null is returned.
    *
    * @param field the index number of the field to be retrieved
    * @return the value of the required field
    * @throws MPXException normally thrown when parsing fails
    */
   public MPXDuration getDuration (int field)
      throws MPXException
   {
      MPXDuration result;

      if ((field < m_fields.length) && (m_fields[field].length() != 0))
      {
         result = MPXDuration.getInstance(m_fields[field], m_parent.getDurationDecimalFormat(), m_parent.getLocale());
      }
      else
      {
         result = null;
      }

      return (result);
   }

   /**
    * Accessor method used to retrieve an MPXUnits object representing the
    * contents of an individual field. If the field does not exist in the
    * record, null is returned.
    *
    * @param field the index number of the field to be retrieved
    * @return the value of the required field
    * @throws MPXException normally thrown when parsing fails
    */
   public Number getUnits (int field)
      throws MPXException
   {
      Number result;

      if ((field < m_fields.length) && (m_fields[field].length() != 0))
      {
         try
         {
            result = new Double(m_formats.getUnitsDecimalFormat().parse(m_fields[field]).doubleValue() * 100);
         }
         
         catch (ParseException ex)
         {
            throw new MPXException ("Failed to parse units", ex);
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
   public TimeUnit getTimeUnit (int field)
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
   public ProjectTimeFormat getTimeFormat (int field)
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
   public ScheduleFrom getScheduleFrom (int field)
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
   public DateOrder getDateOrder (int field)
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
   public CurrencySymbolPosition getCurrencySymbolPosition (int field)
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
   public ProjectDateFormat getDateFormat (int field)
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
    * Accessor method used to retrieve an Integer object representing the
    * contents of an individual field. If the field does not exist in the
    * record, null is returned.
    *
    * @param field the index number of the field to be retrieved
    * @return the value of the required field
    */
   public CodePage getCodePage (int field)
   {
      CodePage result = null;

      if ((field < m_fields.length) && (m_fields[field].length() != 0))
      {
         result = CodePage.getInstance(m_fields[field]);
      }

      return (result);
   }

   /**
    * This method returns the number of fields present in this record.
    *
    * @return number of fields
    */
   public int getLength ()
   {
      return (m_fields.length);
   }

   /**
    * Reference to the parent file.
    */
   private ProjectFile m_parent;

   /**
    * Current record number.
    */
   private String m_recordNumber;

   /**
    * Array of field data.
    */
   private String[] m_fields;
   
   private MPXFormats m_formats;
}
