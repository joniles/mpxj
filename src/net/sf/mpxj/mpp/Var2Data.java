/*
 * file:       Var2Data.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2002-2003
 * date:       03/01/2003
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

package net.sf.mpxj.mpp;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.TreeMap;

/**
 * This class represents a block of variable data. Each block of
 * data is represented by a 4 byte size, followed by the data itself.
 * Each Var2Data block should be associated with a MetaData block
 * which describes the layout of the data in the Var2Data block.
 */
final class Var2Data extends MPPComponent
{
   /**
    * Constructor. Extracts the content of the data block, with reference
    * to the meta data held in the VarMeta block.
    *
    * @param meta meta data for this block
    * @param is InputStream from which data is read
    * @throws IOException on file read error
    */
   Var2Data(VarMeta meta, InputStream is)
      throws IOException
   {
      m_meta = meta;
      byte[] data;

      int currentOffset = 0;

      for (int itemOffset : meta.getOffsets())
      {
         if (currentOffset > itemOffset)
         {
            is.reset();
            is.skip(itemOffset);
         }
         else
         {
            if (currentOffset < itemOffset)
            {
               is.skip(itemOffset - currentOffset);
            }
         }

         int size = readInt(is);

         data = readByteArray(is, size);

         m_map.put(Integer.valueOf(itemOffset), data);
         currentOffset = itemOffset + 4 + size;
      }
   }

   /**
    * This method retrieves a byte array containing the data at the
    * given offset in the block. If no data is found at the given offset
    * this method returns null.
    *
    * @param offset offset of required data
    * @return byte array containing required data
    */
   public byte[] getByteArray(Integer offset)
   {
      byte[] result = null;

      if (offset != null)
      {
         result = m_map.get(offset);
      }

      return (result);
   }

   /**
    * This method retrieves a byte array of the specified type,
    * belonging to the item with the specified unique ID.
    *
    * @param id unique ID of entity to which this data belongs
    * @param type data type identifier
    * @return byte array containing required data
    */
   public byte[] getByteArray(Integer id, Integer type)
   {
      return (getByteArray(m_meta.getOffset(id, type)));
   }

   /**
    * This method retrieves the data at the given offset and returns
    * it as a String, assuming the underlying data is composed of
    * two byte characters.
    *
    * @param offset offset of required data
    * @return string containing required data
    */
   public String getUnicodeString(Integer offset)
   {
      String result = null;

      if (offset != null)
      {
         byte[] value = m_map.get(offset);
         if (value != null)
         {
            result = MPPUtility.getUnicodeString(value, 0);
         }
      }

      return (result);
   }

   /**
    * This method retrieves a String of the specified type,
    * belonging to the item with the specified unique ID.
    *
    * @param id unique ID of entity to which this data belongs
    * @param type data type identifier
    * @return string containing required data
    */
   public String getUnicodeString(Integer id, Integer type)
   {
      return (getUnicodeString(m_meta.getOffset(id, type)));
   }

   /**
    * This method retrieves a timestamp of the specified type,
    * belonging to the item with the specified unique ID.
    *
    * @param id unique ID of entity to which this data belongs
    * @param type data type identifier
    * @return required timestamp data
    */
   public Date getTimestamp(Integer id, Integer type)
   {
      Date result = null;

      Integer offset = m_meta.getOffset(id, type);

      if (offset != null)
      {
         byte[] value = m_map.get(offset);
         if (value != null && value.length >= 4)
         {
            result = MPPUtility.getTimestamp(value, 0);
         }
      }

      return (result);
   }

   /**
    * This method retrieves the data at the given offset and returns
    * it as a String, assuming the underlying data is composed of
    * single byte characters.
    *
    * @param offset offset of required data
    * @return string containing required data
    */
   public String getString(Integer offset)
   {
      String result = null;

      if (offset != null)
      {
         byte[] value = m_map.get(offset);
         if (value != null)
         {
            result = MPPUtility.getString(value, 0);
         }
      }

      return (result);
   }

   /**
    * This method retrieves a string of the specified type,
    * belonging to the item with the specified unique ID.
    *
    * @param id unique ID of entity to which this data belongs
    * @param type data type identifier
    * @return required string data
    */
   public String getString(Integer id, Integer type)
   {
      return (getString(m_meta.getOffset(id, type)));
   }

   /**
    * This method retrieves an integer of the specified type,
    * belonging to the item with the specified unique ID.
    *
    * @param id unique ID of entity to which this data belongs
    * @param type data type identifier
    * @return required integer data
    */
   public int getShort(Integer id, Integer type)
   {
      int result = 0;

      Integer offset = m_meta.getOffset(id, type);

      if (offset != null)
      {
         byte[] value = m_map.get(offset);

         if (value != null && value.length >= 2)
         {
            result = MPPUtility.getShort(value, 0);
         }
      }

      return (result);
   }

   /**
    * This method retrieves an integer of the specified type,
    * belonging to the item with the specified unique ID.
    *
    * @param id unique ID of entity to which this data belongs
    * @param type data type identifier
    * @return required integer data
    */
   public int getByte(Integer id, Integer type)
   {
      int result = 0;

      Integer offset = m_meta.getOffset(id, type);

      if (offset != null)
      {
         byte[] value = m_map.get(offset);

         if (value != null)
         {
            result = MPPUtility.getByte(value, 0);
         }
      }

      return (result);
   }

   /**
    * This method retrieves an integer of the specified type,
    * belonging to the item with the specified unique ID.
    *
    * @param id unique ID of entity to which this data belongs
    * @param type data type identifier
    * @return required integer data
    */
   public int getInt(Integer id, Integer type)
   {
      int result = 0;

      Integer offset = m_meta.getOffset(id, type);

      if (offset != null)
      {
         byte[] value = m_map.get(offset);

         if (value != null && value.length >= 4)
         {
            result = MPPUtility.getInt(value, 0);
         }
      }

      return (result);
   }

   /**
    * This method retrieves an integer of the specified type,
    * belonging to the item with the specified unique ID. Note that
    * the integer value is read from an arbitrary offset within the
    * byte array of data.
    *
    * @param id unique ID of entity to which this data belongs
    * @param offset offset into the byte array fom which to read the integer
    * @param type data type identifier
    * @return required integer data
    */
   public int getInt(Integer id, int offset, Integer type)
   {
      int result = 0;

      Integer metaOffset = m_meta.getOffset(id, type);

      if (metaOffset != null)
      {
         byte[] value = m_map.get(metaOffset);

         if (value != null && value.length >= offset + 4)
         {
            result = MPPUtility.getInt(value, offset);
         }
      }

      return (result);
   }

   /**
    * This method retrieves an integer of the specified type,
    * belonging to the item with the specified unique ID.
    *
    * @param id unique ID of entity to which this data belongs
    * @param type data type identifier
    * @return required integer data
    */
   public long getLong(Integer id, Integer type)
   {
      long result = 0;

      Integer offset = m_meta.getOffset(id, type);

      if (offset != null)
      {
         byte[] value = m_map.get(offset);

         if (value != null && value.length >= 8)
         {
            result = MPPUtility.getLong(value, 0);
         }
      }

      return (result);
   }

   /**
    * This method retrieves a double of the specified type,
    * belonging to the item with the specified unique ID.
    *
    * @param id unique ID of entity to which this data belongs
    * @param type data type identifier
    * @return required double data
    */
   public double getDouble(Integer id, Integer type)
   {
      double result = Double.longBitsToDouble(getLong(id, type));
      if (Double.isNaN(result))
      {
         result = 0;
      }
      return result;
   }

   /**
    * Retrieve the underlying meta data. This method is provided
    * mainly as a convenience for debugging.
    *
    * @return VarMeta instance
    */
   public VarMeta getVarMeta()
   {
      return (m_meta);
   }

   /**
    * This method dumps the contents of this Var2Data block as a String.
    * Note that this facility is provided as a debugging aid.
    *
    * @return formatted contents of this block
    */
   @Override public String toString()
   {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);

      pw.println("BEGIN Var2Data");
      for (Integer offset : m_map.keySet())
      {
         byte[] data = m_map.get(offset);
         pw.println("   Data at offset: " + offset + " size: " + data.length);
         pw.println(MPPUtility.hexdump(data, true, 16, "   "));
      }

      pw.println("END Var2Data");
      pw.println();
      pw.close();
      return (sw.toString());
   }

   /**
    * This is a specialised version of the toString method which
    * outputs just the data in this structure for the given unique ID.
    *
    * @param id unique ID
    * @return string representation
    */
   public String toString(Integer id)
   {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);

      pw.println("BEGIN Var2Data for " + id);
      for (Integer type : m_meta.getTypes(id))
      {
         Integer offset = m_meta.getOffset(id, type);
         byte[] data = m_map.get(offset);
         pw.println("   Data at offset: " + offset + " size: " + data.length);
         pw.println(MPPUtility.hexdump(data, true, 16, "   "));
      }
      pw.println("END Var2Data for " + id);
      pw.println();
      pw.close();
      return (sw.toString());
   }

   /**
    * Map containing data items indexed by offset.
    */
   private TreeMap<Integer, byte[]> m_map = new TreeMap<Integer, byte[]>();

   /**
    * Reference to the meta data associated with this block.
    */
   private VarMeta m_meta;
}
