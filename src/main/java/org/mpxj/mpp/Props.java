/*
 * file:       Props.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2003
 * date:       27/05/2003
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

package org.mpxj.mpp;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;

import org.mpxj.common.ByteArrayHelper;

/**
 * This class represents the common structure of Props files found in
 * Microsoft Project MPP files. The MPP8 and MPP9 file formats both
 * implement Props files slightly differently, so this class contains
 * the shared implementation detail, with specific implementations for
 * MPP8 and MPP9 Props files found in the Props8 and Props9 classes.
 */
class Props extends MPPComponent
{
   public byte[] getByteArray(PropsKey key)
   {
      return getByteArray(key.getValue());
   }

   /**
    * Retrieve property data as a byte array.
    *
    * @param type Type identifier
    * @return  byte array of data
    */
   public byte[] getByteArray(Integer type)
   {
      return (m_map.get(type));
   }


   /**
    * Retrieves a byte value from the property data.
    *
    * @param type Type identifier
    * @return byte value
    */
   public byte getByte(PropsKey key)
   {
      byte result = 0;

      byte[] item = m_map.get(key.getValue());
      if (item != null)
      {
         result = item[0];
      }

      return (result);
   }

   public int getShort(PropsKey key)
   {
      return getShort(key.getValue());
   }

   /**
    * Retrieves a short int value from the property data.
    *
    * @param type Type identifier
    * @return short int value
    */
   public int getShort(Integer type)
   {
      int result = 0;

      byte[] item = m_map.get(type);
      if (item != null)
      {
         result = ByteArrayHelper.getShort(item, 0);
      }

      return (result);
   }

   public int getInt(PropsKey key)
   {
      return getInt(key.getValue());
   }

   /**
    * Retrieves an integer value from the property data.
    *
    * @param type Type identifier
    * @return integer value
    */
   public int getInt(Integer type)
   {
      int result = 0;

      byte[] item = m_map.get(type);
      if (item != null)
      {
         result = ByteArrayHelper.getInt(item, 0);
      }

      return (result);
   }

   public double getDouble(PropsKey key)
   {
      return getDouble(key.getValue());
   }

   /**
    * Retrieves a double value from the property data.
    *
    * @param type Type identifier
    * @return double value
    */
   public double getDouble(Integer type)
   {
      double result = 0;

      byte[] item = m_map.get(type);
      if (item != null)
      {
         result = MPPUtility.getDouble(item, 0);
      }

      return (result);
   }

   public LocalTime getTime(PropsKey key)
   {
      return getTime(key.getValue());
   }

   /**
    * Retrieves a time from the property data.
    *
    * @param type Type identifier
    * @return timestamp
    */
   public LocalTime getTime(Integer type)
   {
      LocalTime result = null;

      byte[] item = m_map.get(type);
      if (item != null)
      {
         result = MPPUtility.getTime(item, 0);
      }

      return result;
   }

   public LocalDateTime getTimestamp(PropsKey key)
   {
      return getTimestamp(key.getValue());
   }

   /**
    * Retrieves a timestamp from the property data.
    *
    * @param type Type identifier
    * @return timestamp
    */
   public LocalDateTime getTimestamp(Integer type)
   {
      LocalDateTime result = null;

      byte[] item = m_map.get(type);
      if (item != null)
      {
         result = MPPUtility.getTimestamp(item, 0);
      }

      return (result);
   }

   /**
    * Retrieves a boolean value from the property data.
    *
    * @param type Type identifier
    * @return boolean value
    */
   public boolean getBoolean(PropsKey key)
   {
      boolean result = false;

      byte[] item = m_map.get(key.getValue());
      if (item != null)
      {
         result = !(ByteArrayHelper.getShort(item, 0) == 0);
      }

      return (result);
   }

   public String getUnicodeString(PropsKey key)
   {
      return getUnicodeString(key.getValue());
   }

   /**
    * Retrieves a string value from the property data.
    *
    * @param type Type identifier
    * @return string value
    */
   public String getUnicodeString(Integer type)
   {
      String result = null;

      byte[] item = m_map.get(type);
      if (item != null)
      {
         result = MPPUtility.getUnicodeString(item, 0);
      }

      return (result);
   }

   /**
    * Retrieves a date value from the property data.
    *
    * @param type Type identifier
    * @return string value
    */
   public LocalDate getDate(Integer type)
   {
      byte[] item = m_map.get(type);
      if (item == null)
      {
         return null;
      }
      return MPPUtility.getDate(item, 0);
   }

   /**
    * Retrieves a UUID value from the property data.
    *
    * @param type Type identifier
    * @return UUID value
    */
   public UUID getUUID(PropsKey key)
   {
      byte[] item = m_map.get(key.getValue());
      if (item == null)
      {
         return null;
      }

      if (item.length > 16)
      {
         // MPP9 stores a string representation of the GUID
         String value = MPPUtility.getUnicodeString(item, 0, 76);
         if (value.length() == 38 && value.charAt(0) == '{' && value.charAt(37) == '}')
         {
            return UUID.fromString(value.substring(1, 37));
         }
      }

      return MPPUtility.getGUID(item, 0);
   }

   /**
    * Retrieve the set of keys represented by this instance.
    *
    * @return key set
    */
   public Set<Integer> keySet()
   {
      return (m_map.keySet());
   }

   /**
    * This method dumps the contents of this properties block as a String.
    * Note that this facility is provided as a debugging aid.
    *
    * @return formatted contents of this block
    */
   @Override public String toString()
   {
      Map<Integer, PropsKey> map = Arrays.stream(PropsKey.values()).collect(Collectors.toMap(PropsKey::getValue, k -> k));

      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);

      pw.println("BEGIN Props");

      for (Map.Entry<Integer, byte[]> entry : m_map.entrySet())
      {
         PropsKey key = map.get(entry.getKey());
         String keyLabel = key == null ? entry.getKey().toString() : key + "(" + entry.getKey() + ")";
         pw.println("   Key: " + keyLabel + " Value: ");
         pw.println(ByteArrayHelper.hexdump(entry.getValue(), true, 16, "      "));
      }

      pw.println("END Props");

      pw.println();
      pw.close();
      return (sw.toString());
   }

   protected final TreeMap<Integer, byte[]> m_map = new TreeMap<>();
}
