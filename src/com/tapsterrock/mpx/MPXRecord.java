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
import java.util.TreeMap;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.LinkedList;

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
    */
   protected MPXRecord (MPXFile mpx)
   {
      m_mpx = mpx;
   }

   /**
    * This method returns the string representation of an object. In most
    * cases this will simply involve calling the normal toString method
    * on the object, but a couple of exceptions are handled here.
    *
    * @param o the object to formatted
    * @return formatted string representing input Object
    */
   private String format (Object o)
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
            result = ((Boolean)o).booleanValue() == true ? "Yes" : "No";
         }
         else
         {
            if (o instanceof Float == true || o instanceof Double)
            {
               result = (DECIMAL_FORMAT.format(((Number)o).doubleValue()));
            }
            else
            {
               if (o instanceof MPXRate == true)
               {
                  result = ((MPXRate)o).toString(m_mpx.getCurrencyFormat());
               }
               else
               {
                  result = o.toString();
               }
            }
         }
      }

      return (result);
   }


   /**
    * This is a generic method to convert an MPX record into a correctly
    * formatted string. Note that this method uses the property of a
    * TreeMap that the keys are retrieved by a key set iterator in order.
    *
    * @param code the MPX record number of this record type.
    * @return MPX formatted String for supplied record type.
    */
   protected String toString (int code)
   {
      StringBuffer buf = new StringBuffer(String.valueOf(code));
      char sepchar = m_mpx.getDelimiter();
      String str;

      Iterator iter = m_map.keySet().iterator();
      while (iter.hasNext() == true)
      {
         str = format (m_map.get(iter.next()));

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
    * @param names array of keys
    * @return MPX formatted String for supplied record type.
    */
   protected String toString (int code, LinkedList names)
   {
      StringBuffer buf = new StringBuffer(String.valueOf(code));
      char sepchar = m_mpx.getDelimiter();
      String str;
      Iterator iter = names.iterator();

      while (iter.hasNext() == true)
      {
         str = format (m_map.get(iter.next()));

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
   private void stripTrailingDelimiters (StringBuffer buffer, char delimiter)
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
   protected void put (Integer key, Object value)
   {
      m_map.put (key, value);
   }

   /**
    * This method inserts a name value pair into internal storage.
    *
    * @param key attribute identifier
    * @param value attribute value
    */
   protected void put (Integer key, char value)
   {
      m_map.put (key, new Character (value));
   }

   /**
    * This method inserts a name value pair into internal storage.
    *
    * @param key attribute identifier
    * @param value attribute value
    */
   protected void put (Integer key, int value)
   {
      m_map.put (key, new Integer (value));
   }

   /**
    * This method inserts a name value pair into internal storage.
    * Note that this method maps Date objects into MPXDate objects.
    *
    * @param key attribute identifier
    * @param value attribute value
    */
   protected void putDate (Integer key, Date value)
   {
      if (value != null && value instanceof MPXDate == false)
      {
         value = new MPXDate (m_mpx.getDateFormat(), value);
      }

      m_map.put (key, value);
   }

   /**
    * This method inserts a name value pair into internal storage.
    * Note that this method maps Number objects into MPXCurrency objects.
    *
    * @param key attribute identifier
    * @param value attribute value
    */
   protected void putCurrency (Integer key, Number value)
   {
      if (value != null && value instanceof MPXCurrency == false)
      {
         value = new MPXCurrency (m_mpx.getCurrencyFormat(), value.doubleValue());
      }

      m_map.put (key, value);
   }

   /**
    * This method inserts a name value pair into internal storage.
    * Note that this method maps Number objects into MPXUnits objects.
    *
    * @param key attribute identifier
    * @param value attribute value
    */
   protected void putUnits (Integer key, Number value)
   {
      if (value != null && value instanceof MPXUnits == false)
      {
         value = new MPXUnits (value);
      }

      m_map.put (key, value);
   }

   /**
    * This method inserts a name value pair into internal storage.
    * Note that this method maps Date objects into MPXTime objects.
    *
    * @param key attribute identifier
    * @param value attribute value
    */
   protected void putTime (Integer key, Date value)
   {
      if (value != null && value instanceof MPXTime == false)
      {
         value = new MPXTime (m_mpx.getTimeFormat(), value);
      }

      m_map.put (key, value);
   }

   /**
    * Given an attribute name, this method retrieves that attribute
    * value from internal storage.
    *
    * @param key name of requested field value
    * @return requested value
    */
   protected Object get (Integer key)
   {
      return (m_map.get(key));
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
   protected byte getByteValue (Integer key)
   {
      Byte value = (Byte)m_map.get(key);

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
   protected boolean getBooleanValue (Integer key)
   {
      Boolean value = (Boolean)m_map.get(key);

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
   protected char getCharValue (Integer key)
   {
      Character value = (Character)m_map.get(key);

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
   protected int getIntValue (Integer key)
   {
      Integer value = (Integer)m_map.get(key);

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
   protected float getFloatValue (Integer key)
   {
      Float value = (Float)m_map.get(key);

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
   protected double getDoubleValue (Integer key)
   {
      Double value = (Double)m_map.get(key);

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
   public MPXFile getParentFile ()
   {
      return (m_mpx);
   }


   /**
    * Reference to parent MPXFile.
    */
   private MPXFile m_mpx;

   private TreeMap m_map = new TreeMap ();

   private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat ("0.00#");
}
