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
            if (o instanceof Float == true)
            {
               result = (FLOAT_FORMAT.format(((Float)o).doubleValue()));
            }
            else
            {
               result = o.toString();
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
    * Accessor method allowing retreival of MPXFile reference.
    *
    * @return reference to this MPXFile
    */
   public MPXFile getParentFile ()
   {
      return (m_mpx);
   }


   /**
    * Reference to parent MPXFile. Used for accessing Separator character. Used by all subclasses.
    */
   private MPXFile m_mpx;

   private TreeMap m_map = new TreeMap ();

   private static final DecimalFormat FLOAT_FORMAT = new DecimalFormat ("0.00#");
}
