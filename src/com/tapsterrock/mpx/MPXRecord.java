/*
 * file:       MPXRecord.java
 * author:     Scott Melville
 *             Jon Iles
 * copyright:  (c) Tapster Rock Limited 2002-2003
 * date:       15/08/2002
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

import java.util.Date;
import java.util.HashMap;

/**
 * This is the base class from which all classes representing records found
 * in an MPX file are derived. It contains common funciotnality and
 * attribute storage used by all of the derived classes.
 */
class MPXRecord
{
   /**
    * Constructor.
    *
    * @param mpx Parent MPX file
    * @param size Maxmum number of fields in this record
    */
   protected MPXRecord (MPXFile mpx, int size)
   {
      m_mpx = mpx;
      m_array = new Object[size];
   }

   /**
    * This method removes line breaks from a piece of text, and replaces
    * them with the supplied text.
    * 
    * @param text source text
    * @param replacement line break replacement text
    * @return text with line breaks removed.
    */
   protected final String stripLineBreaks (String text, String replacement)
   {
      if (text.indexOf('\r') != -1 || text.indexOf('\n') != -1)
      {
         StringBuffer sb = new StringBuffer (text);

         int index;

         while ((index = sb.indexOf("\r\n")) != -1)
         {
            sb.replace(index, index+2, replacement);
         }

         while ((index = sb.indexOf("\n\r")) != -1)
         {
            sb.replace(index, index+2, replacement);
         }

         while ((index = sb.indexOf("\r")) != -1)
         {
            sb.replace(index, index+1, replacement);
         }

         while ((index = sb.indexOf("\n")) != -1)
         {
            sb.replace(index, index+1, replacement);
         }

         text = sb.toString();
      }

      return (text);
   }

   /**
    * This method returns the string representation of an object. In most
    * cases this will simply involve calling the normal toString method
    * on the object, but a couple of exceptions are handled here.
    *
    * @param o the object to formatted
    * @return formatted string representing input Object
    */
   protected final String format (Object o)
   {
      String result;

      if (o == null)
      {
         result = "";
      }
      else
      {
         if (o instanceof Boolean == true)
         {
            result = LocaleData.getString(m_mpx.getLocale(), (((Boolean)o).booleanValue() == true?LocaleData.YES:LocaleData.NO));
         }
         else
         {
            if (o instanceof Float == true || o instanceof Double == true)
            {
               result = (m_mpx.getDecimalFormat().format(((Number)o).doubleValue()));
            }
            else
            {
               if (o instanceof ToStringRequiresFile == true)
               {
                  result = ((ToStringRequiresFile)o).toString(m_mpx);
               }
               else
               {
                  result = o.toString();
               }
            }
         }

         //
         // At this point there should be no line break characters in
         // the file. If we find any, replace them with spaces
         //
         result = stripLineBreaks(result, EOL_PLACEHOLDER_STRING);
      }

      return (result);
   }


   /**
    * This is a generic method to convert an MPX record into a correctly
    * formatted string.
    *
    * @param code the MPX record number of this record type.
    * @return MPX formatted String for supplied record type.
    */
   protected String toString (int code)
   {
      StringBuffer buf = new StringBuffer(String.valueOf(code));
      char sepchar = m_mpx.getDelimiter();
      String str;

      for (int loop=0; loop < m_array.length; loop++)
      {
         str = format (m_array[loop]);

         buf.append (sepchar);

         if (str != null)
         {
            if (str.indexOf(sepchar) != -1)
            {
               buf.append ('"');
               buf.append (str);
               buf.append ('"');
            }
            else
            {
               buf.append (str);
            }
         }
      }

      stripTrailingDelimiters (buf, sepchar);

      buf.append (MPXFile.EOL);

      return (buf.toString());
   }

   /**
    * This is a generic method to convert an MPX record into a correctly
    * formatted string. In this instance one of the variable length record
    * types is being processed, and to allow this to work successfully, an
    * array containing the keys to be used to retrieve each field from
    * the map is supplied.
    *
    * @param code the MPX record number of this record type.
    * @param fields array of fields
    * @return MPX formatted String for supplied record type.
    */
   protected String toString (int code, int[] fields)
   {
      StringBuffer buf = new StringBuffer(String.valueOf(code));
      char sepchar = m_mpx.getDelimiter();
      String str;
      int field;

      for (int loop=0; loop < fields.length; loop++)
      {
         field = fields[loop];
         if (field == -1)
         {
            break;
         }

         str = format (get(field));

         buf.append (sepchar);

         if (str != null)
         {
            if (str.indexOf (sepchar) != -1)
            {
               buf.append ('"');
               buf.append (str);
               buf.append ('"');
            }
            else
            {
               buf.append (str);
            }
         }
      }

      stripTrailingDelimiters (buf, sepchar);

      buf.append (MPXFile.EOL);

      return (buf.toString());
   }

  /**
    * This method removes trailing delimiter characters
    *
    * @param buffer input sring buffer
    * @param delimiter delimiter character
    */
   protected final void stripTrailingDelimiters (StringBuffer buffer, char delimiter)
   {
      int index = buffer.length() - 1;

      while (index > 0 && buffer.charAt(index) == delimiter)
      {
         --index;
      }

      buffer.setLength (index+1);
   }

   /**
    * This method inserts a name value pair into internal storage.
    *
    * @param key attribute identifier
    * @param value attribute value
    */
   public void put (int key, Object value)
   {
      if (key < m_array.length)
      {
         m_array[key] = value;
      }
      else
      {
         m_extended.put(new Integer(key), value);
      }
   }

   /**
    * This method inserts a name value pair into internal storage.
    *
    * @param key attribute identifier
    * @param value attribute value
    */
   protected void put (int key, char value)
   {
      put (key, new Character(value));
   }

   /**
    * This method inserts a name value pair into internal storage.
    *
    * @param key attribute identifier
    * @param value attribute value
    */
   protected void put (int key, int value)
   {
      put (key, new Integer (value));
   }

   /**
    * This method inserts a name value pair into internal storage.
    *
    * @param key attribute identifier
    * @param value attribute value
    */
   protected void put (int key, boolean value)
   {
      put (key, (value==true ? Boolean.TRUE : Boolean.FALSE));
   }

   /**
    * This method is called to ensure that a Date value is actually
    * represented as an MPXDate instance rather than a raw date
    * type.
    * 
    * @param value date value
    * @return date value
    */   
   protected MPXDate toDate (Date value)
   {
      MPXDate result = null;
      
      if (value != null)
      {
         if (value instanceof MPXDate == false)
         {
            result = new MPXDate (m_mpx.getDateFormat(), value);
         }
         else
         {
            result = (MPXDate)value;
         }
      }

      return (result);
   }
   
   /**
    * This method inserts a name value pair into internal storage.
    * Note that this method maps Date objects into MPXDate objects.
    *
    * @param key attribute identifier
    * @param value attribute value
    */
   protected void putDate (int key, Date value)
   {
      put (key, toDate(value));
   }

   /**
    * This method is called to ensure that a Number value is actually
    * represented as an MPXCurrency instance rather than a raw numeric
    * type.
    * 
    * @param value numeric value
    * @return currency value
    */
   protected MPXCurrency toCurrency (Number value)
   {
      MPXCurrency result = null;
      
      if (value != null)
      {
         if (value instanceof MPXCurrency == false)
         {
            result = new MPXCurrency (m_mpx.getCurrencyFormat(), value.doubleValue());
         }
         else
         {
            result = (MPXCurrency)value;
         }
      }
      
      return (result);
   }

   /**
    * This method inserts a name value pair into internal storage.
    * Note that this method maps Number objects into MPXCurrency objects.
    *
    * @param key attribute identifier
    * @param value attribute value
    */
   protected void putCurrency (int key, Number value)
   {
      put (key, toCurrency(value));
   }

   /**
    * This method inserts a name value pair into internal storage.
    * Note that this method maps Number objects into MPXUnits objects.
    *
    * @param key attribute identifier
    * @param value attribute value
    */
   protected void putUnits (int key, Number value)
   {
      if (value != null && value instanceof MPXUnits == false)
      {
         value = new MPXUnits (value);
      }

      put (key, value);
   }

   /**
    * This method is called to ensure that a Number value is actually
    * represented as an MPXPercentage instance rather than a raw numeric
    * type.
    * 
    * @param value numeric value
    * @return percentage value
    */   
   protected MPXPercentage toPercentage (Number value)
   {
      MPXPercentage result = null;
      
      if (value != null) 
      {
         if (value instanceof MPXPercentage == false)
         {
            result = new MPXPercentage (value);
         }
         else
         {
            result = (MPXPercentage)value;
         }
      }
      
      return (result);
   }
   
   /**
    * This method inserts a name value pair into internal storage.
    * Note that this method maps Number objects into MPXPercentage objects.
    *
    * @param key attribute identifier
    * @param value attribute value
    */
   protected void putPercentage (int key, Number value)
   {
      put (key, toPercentage(value));
   }

   /**
    * This method inserts a name value pair into internal storage.
    * Note that this method maps Date objects into MPXTime objects.
    *
    * @param key attribute identifier
    * @param value attribute value
    */
   protected void putTime (int key, Date value)
   {
      if (value != null && value instanceof MPXTime == false)
      {
         value = new MPXTime (m_mpx.getTimeFormat(), value);
      }

      put (key, value);
   }

   /**
    * Given an attribute id, this method retrieves that attribute
    * value from internal storage.
    *
    * @param key name of requested field value
    * @return requested value
    */
   public Object get (int key)
   {
      Object result;

      if (key < m_array.length)
      {
         result = m_array[key];
      }
      else
      {
         result = m_extended.get(new Integer(key));
      }

      return (result);
   }

   /**
    * Given an attribute name, this method retrieves that attribute
    * value from internal storage and maps it to a byte value. If
    * no value has been stored (i.e. we find a null pointer) we
    * map this to the default byte value, zero.
    *
    * @param key name of requested field value
    * @return requested value
    */
   protected byte getByteValue (int key)
   {
      Number value = (Number)get(key);

      byte result;
      if (value == null)
      {
         result = 0;
      }
      else
      {
         result = value.byteValue();
      }

      return (result);
   }

   /**
    * Given an attribute name, this method retrieves that attribute
    * value from internal storage and maps it to a boolean value. If
    * no value has been stored (i.e. we find a null pointer) we
    * map this to the default boolean value, false.
    *
    * @param key name of requested field value
    * @return requested value
    */
   protected boolean getBooleanValue (int key)
   {
      Boolean value = (Boolean)get(key);

      boolean result;
      if (value == null)
      {
         result = false;
      }
      else
      {
         result = value.booleanValue();
      }

      return (result);
   }

   /**
    * Given an attribute name, this method retrieves that attribute
    * value from internal storage and maps it to a boolean value. If
    * no value has been stored (i.e. we find a null pointer) we
    * map this to the default boolean value, false.
    *
    * @param key name of requested field value
    * @return requested value
    */
   protected boolean getNumericBooleanValue (int key)
   {
      NumericBoolean value = (NumericBoolean)get(key);

      boolean result;
      if (value == null)
      {
         result = false;
      }
      else
      {
         result = value.booleanValue();
      }

      return (result);
   }

   /**
    * Given an attribute name, this method retrieves that attribute
    * value from internal storage and maps it to a char value. If
    * no value has been stored (i.e. we find a null pointer) we
    * map this to the default char value, zero.
    *
    * @param key name of requested field value
    * @return requested value
    */
   protected char getCharValue (int key)
   {
      Character value = (Character)get(key);

      char result;
      if (value == null)
      {
         result = 0;
      }
      else
      {
         result = value.charValue();
      }

      return (result);
   }

   /**
    * Given an attribute name, this method retrieves that attribute
    * value from internal storage and maps it to a int value. If
    * no value has been stored (i.e. we find a null pointer) we
    * map this to the default int value, zero.
    *
    * @param key name of requested field value
    * @return requested value
    */
   protected int getIntValue (int key)
   {
      Integer value = (Integer)get(key);

      int result;
      if (value == null)
      {
         result = 0;
      }
      else
      {
         result = value.intValue();
      }

      return (result);
   }

   /**
    * Given an attribute name, this method retrieves that attribute
    * value from internal storage and maps it to a float value. If
    * no value has been stored (i.e. we find a null pointer) we
    * map this to the default float value, zero.
    *
    * @param key name of requested field value
    * @return requested value
    */
   protected float getFloatValue (int key)
   {
      Number value = (Number)get(key);

      float result;
      if (value == null)
      {
         result = 0;
      }
      else
      {
         result = value.floatValue();
      }

      return (result);
   }

   /**
    * Given an attribute name, this method retrieves that attribute
    * value from internal storage and maps it to a double value. If
    * no value has been stored (i.e. we find a null pointer) we
    * map this to the default double value, zero.
    *
    * @param key name of requested field value
    * @return requested value
    */
   protected double getDoubleValue (int key)
   {
      return (getDoubleValue((Number)get(key)));
   }

   /**
    * Given a number, this method returns a double value. If the
    * number parameter is null, then zero is returned.
    *
    * @param value Number value
    * @return double value
    */
   protected double getDoubleValue (Number value)
   {
      double result;
      if (value == null)
      {
         result = 0;
      }
      else
      {
         result = value.doubleValue();
      }

      return (result);
   }

   /**
    * Accessor method allowing retreival of MPXFile reference.
    *
    * @return reference to this MPXFile
    */
   public final MPXFile getParentFile ()
   {
      return (m_mpx);
   }


   /**
    * Reference to parent MPXFile.
    */
   private MPXFile m_mpx;

   /**
    * Array of field values.
    */
   private Object[] m_array;
   private HashMap m_extended = new HashMap ();

   /**
    * Placeholder character used in MPX files to represent
    * carriage returns embedded in note text.
    */
   static final char EOL_PLACEHOLDER = (char)0x7F;
   static final String EOL_PLACEHOLDER_STRING = new String(new byte[]{EOL_PLACEHOLDER});
}
