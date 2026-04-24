/*
 * file:       PropsBlock.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2007
 * date:       07/12/2007
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

import java.time.LocalDateTime;
import java.util.Map;
import java.util.TreeMap;

import org.mpxj.common.ByteArrayHelper;

/**
 * This class represents a block of property data.
 */
final class PropsBlock extends Props
{
   /**
    * Constructor.
    *
    * @param data block of property data
    */
   PropsBlock(byte[] data)
   {
      int dataSize = ByteArrayHelper.getInt(data, 0);
      int itemCount = ByteArrayHelper.getInt(data, 4);

      int offset = 8;
      Map<Integer, Integer> offsetMap = new TreeMap<>();
      for (int loop = 0; loop < itemCount; loop++)
      {
         int itemKey = ByteArrayHelper.getInt(data, offset);
         offset += 4;

         int itemOffset = ByteArrayHelper.getInt(data, offset);
         offset += 4;

         offsetMap.put(Integer.valueOf(itemOffset), Integer.valueOf(itemKey));
      }

      Integer previousItemOffset = null;
      Integer previousItemKey = null;

      for (Integer itemOffset : offsetMap.keySet())
      {
         populateMap(data, previousItemOffset, previousItemKey, itemOffset);
         previousItemOffset = itemOffset;
         previousItemKey = offsetMap.get(previousItemOffset);
      }

      if (previousItemOffset != null)
      {
         Integer itemOffset = Integer.valueOf(dataSize);
         populateMap(data, previousItemOffset, previousItemKey, itemOffset);
      }
   }

   /**
    * Retrieve property data as a byte array.
    *
    * @param type Type identifier
    * @return  byte array of data
    */
   public byte[] getByteArray(Integer type)
   {
      return m_map.get(type);
   }

   /**
    * Retrieves a short int value from the property data.
    *
    * @param type Type identifier
    * @return short int value
    */
   public int getShort(Integer type)
   {
      byte[] item = m_map.get(type);
      if (item == null)
      {
         return 0;
      }
      return ByteArrayHelper.getShort(item, 0);
   }

   /**
    * Retrieves an integer value from the property data.
    *
    * @param type Type identifier
    * @return integer value
    */
   public int getInt(Integer type)
   {
      byte[] item = m_map.get(type);
      if (item == null)
      {
         return 0;
      }
      return ByteArrayHelper.getInt(item, 0);
   }

   /**
    * Retrieves a double value from the property data.
    *
    * @param type Type identifier
    * @return double value
    */
   public double getDouble(Integer type)
   {
      byte[] item = m_map.get(type);
      if (item == null)
      {
         return 0;
      }
      return MPPUtility.getDouble(item, 0);
   }

   /**
    * Retrieves a timestamp from the property data.
    *
    * @param type Type identifier
    * @return timestamp
    */
   public LocalDateTime getTimestamp(Integer type)
   {
      byte[] item = m_map.get(type);
      if (item == null)
      {
         return null;
      }
      return MPPUtility.getTimestamp(item, 0);
   }

   /**
    * Retrieves a string value from the property data.
    *
    * @param type Type identifier
    * @return string value
    */
   public String getUnicodeString(Integer type)
   {
      byte[] item = m_map.get(type);
      if (item == null)
      {
         return null;
      }
      return MPPUtility.getUnicodeString(item, 0);
   }

   /**
    * Method used to extract data from the block of properties and
    * insert the key value pair into a map.
    *
    * @param data block of property data
    * @param previousItemOffset previous offset
    * @param previousItemKey item key
    * @param itemOffset current item offset
    */
   private void populateMap(byte[] data, Integer previousItemOffset, Integer previousItemKey, Integer itemOffset)
   {
      if (previousItemOffset != null)
      {
         int itemSize = itemOffset.intValue() - previousItemOffset.intValue();
         byte[] itemData = new byte[itemSize];
         System.arraycopy(data, previousItemOffset.intValue(), itemData, 0, itemSize);
         m_map.put(previousItemKey, itemData);
      }
   }

}
