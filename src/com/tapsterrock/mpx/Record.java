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
import java.util.LinkedList;

/**
 * This class is used to represent a record in an MPX file.
 */
class Record
{
   /**
    * Default constructor used to create an empty record.
    */
   private Record ()
   {
      m_parent = null;
      m_fields = new String [0];
   }

   /**
    * This constructor takes a stream of tokens and extracts the
    * fields of an individual record from those tokens.
    *
    * @param parent parent MPX file
    * @param tk tokenizer providing the input stream of tokens
    */
   public Record (MPXFile parent, Tokenizer tk)
      throws MPXException
   {
      try
      {
         m_parent = parent;

         LinkedList list = new LinkedList ();

         while (tk.nextToken() == Tokenizer.TT_WORD)
         {
            list.add (tk.getToken());
         }

         if (list.size() > 0)
         {
            m_recordNumber = (String)list.remove(0);
            m_fields = (String[])list.toArray(new String[list.size()]);
         }
      }

      catch (IOException ex)
      {
         throw new MPXException (MPXException.INVALID_RECORD, ex);
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
      }
      else
      {
         result = null;
      }

      return (result);
   }

   /**
    * Accessor method used to retrieve a String value representing the
    * contents of an individual field. If the field does not exist in the
    * record, a default value is returned.
    *
    * @param field the index number of the field to be retrieved
    * @return the value of the required field
    */
   public String getStringValue (int field)
   {
      String result = getString (field);
      if (result == null)
      {
         result = DEFAULT_STRING;
      }
      return (result);
   }

   /**
    * Accessor method used to retrieve a Byte object representing the
    * contents of an individual field. If the field does not exist in the
    * record, null is returned.
    *
    * @param field the index number of the field to be retrieved
    * @return the value of the required field
    */
   public Byte getByte (int field)
   {
      Byte result;

      if (field < m_fields.length)
      {
         result = Byte.valueOf(m_fields[field]);
      }
      else
      {
         result = null;
      }

      return (result);
   }

   /**
    * Accessor method used to retrieve a byte value representing the
    * contents of an individual field. If the field does not exist in the
    * record, a default value is returned.
    *
    * @param field the index number of the field to be retrieved
    * @return the value of the required field
    */
   public byte getByteValue (int field)
   {
      Byte result = getByte (field);
      if (result == null)
      {
         result = DEFAULT_BYTE;
      }
      return (result.byteValue());
   }

   /**
    * Accessor method used to retrieve a Character object representing the
    * contents of an individual field. If the field does not exist in the
    * record, null is returned.
    *
    * @param field the index number of the field to be retrieved
    * @return the value of the required field
    */
   public Character getCharacter (int field)
   {
      Character result;

      if (field < m_fields.length)
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
   {
      Float result;

      if (field < m_fields.length)
      {
         result = Float.valueOf(m_fields[field]);
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
   public Integer getInteger (int field)
   {
      Integer result;

      if (field < m_fields.length)
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
    */
   public MPXDate getDate (int field)
      throws MPXException
   {
      MPXDate result;

      if (field < m_fields.length)
      {
         result = m_parent.getDateFormat().parse(m_fields[field]);
      }
      else
      {
         result = null;
      }

      return (result);
   }

   /**
    * Accessor method used to retrieve an MPXTime object representing the
    * contents of an individual field. If the field does not exist in the
    * record, null is returned.
    *
    * @param field the index number of the field to be retrieved
    * @return the value of the required field
    */
   public MPXTime getTime (int field)
      throws MPXException
   {
      MPXTime result;

      if (field < m_fields.length)
      {
         result = m_parent.getTimeFormat().parse(m_fields[field]);
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
   public Boolean getBoolean (int field)
   {
      Boolean result;

      if (field < m_fields.length)
      {
         result = m_fields[field].equals("0") ? Boolean.FALSE : Boolean.TRUE;
      }
      else
      {
         result = null;
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
    */
   public MPXRate getRate (int field)
      throws MPXException
   {
      MPXRate result;

      if (field < m_fields.length)
      {
         result = new MPXRate (m_parent, m_fields[field]);
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
    */
   public MPXCurrency getCurrency (int field)
      throws MPXException
   {
      MPXCurrency result;

      if (field < m_fields.length)
      {
         result = new MPXCurrency (m_parent, m_fields[field]);
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
    */
   public MPXPercentage getPercentage (int field)
      throws MPXException
   {
      MPXPercentage result;

      if (field < m_fields.length)
      {
         result = new MPXPercentage (m_fields[field]);
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
    */
   public MPXDuration getDuration (int field)
      throws MPXException
   {
      MPXDuration result;

      if (field < m_fields.length)
      {
         result = new MPXDuration (m_fields[field]);
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
    */
   public MPXUnits getUnits (int field)
      throws MPXException
   {
      MPXUnits result;

      if (field < m_fields.length)
      {
         result = new MPXUnits (m_fields[field]);
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
   public int getLength ()
   {
      return (m_fields.length);
   }

   private MPXFile m_parent;
   private String m_recordNumber;
   private String[] m_fields;

   private static final Byte DEFAULT_BYTE = new Byte ((byte)0);
   private static final String DEFAULT_STRING = "";

   public static final Record EMPTY_RECORD = new Record ();
}
