/*
 * file:       Var2Data.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2002-2003
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

package com.tapsterrock.mpp;

import java.io.InputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.Date;

/**
 * This class represents a block of variable data. Each block of
 * data is represented by a 4 byte size, followed by the data itself.
 * Each Var2Data block should be associated with a MetaData block
 * which describes the layout of the data in the Var2Data block.
 */
public class Var2Data extends MPPComponent
{
   /**
    * Constructor. Extracts the content of the data block, with reference
    * to the meta data held in the VarMeta block.
    *
    * @param meta meta data for this block
    * @param is InputStream from which data is read
    * @throws IOException on file read error
    */
   public Var2Data (VarMeta meta, InputStream is)
      throws IOException
   {
      m_meta = meta;
      ByteArray data;

      int itemCount = m_meta.getItemCount();

      int index;
      int offset = 0;
      int itemOffset;
      int itemSize;

      for (int loop=0; loop < itemCount; loop++)
      {
         itemOffset = meta.getOffset (loop);
         is.reset();
         is.skip(itemOffset);

         int size = readInt (is);

         data = new ByteArray (readByteArray (is, size));

         m_map.put (new Integer (itemOffset), data);
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
   public byte[] getByteArray (Integer offset)
   {
      byte[] result = null;

      if (offset != null)
      {
         ByteArray value = (ByteArray)m_map.get (offset);
         if (value != null)
         {
            result = value.byteArrayValue ();
         }
      }

      return (result);
   }

   /**
    * This method rerieves a byte array of the specified type,
    * belonging to the item with the specified unique ID.
    *
    * @param id unique ID of entity to which this data belongs
    * @param type data type identifier
    * @return byte array containing required data
    */
   public byte[] getByteArray (Integer id, Integer type)
   {
      return (getByteArray (m_meta.getOffset(id, type)));
   }

   /**
    * This method retrieves the data at the given offset and returns
    * it as a String, assuming the underlying data is composed of
    * two byte characters.
    *
    * @param offset offset of required data
    * @return string containing required data
    */
   public String getUnicodeString (Integer offset)
   {
      String result = null;

      if (offset != null)
      {
         ByteArray value = (ByteArray)m_map.get (offset);
         if (value != null)
         {
            result = MPPUtility.getUnicodeString(value.byteArrayValue());
         }
      }

      return (result);
   }

   /**
    * This method rerieves a String of the specified type,
    * belonging to the item with the specified unique ID.
    *
    * @param id unique ID of entity to which this data belongs
    * @param type data type identifier
    * @return string containing required data
    */
   public String getUnicodeString (Integer id, Integer type)
   {
      return (getUnicodeString (m_meta.getOffset(id, type)));
   }


   /**
    * This method rerieves a timestamp of the specified type,
    * belonging to the item with the specified unique ID.
    *
    * @param id unique ID of entity to which this data belongs
    * @param type data type identifier
    * @return required timestamp data
    */
   public Date getTimestamp (Integer id, Integer type)
   {
      Date result = null;

      Integer offset = m_meta.getOffset (id, type);

      if (offset != null)
      {
         ByteArray value = (ByteArray)m_map.get (offset);
         if (value != null)
         {
            result = MPPUtility.getTimestamp(value.byteArrayValue());
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
   public String getString (Integer offset)
   {
      String result = null;

      if (offset != null)
      {
         ByteArray value = (ByteArray)m_map.get (offset);
         if (value != null)
         {
            result = MPPUtility.getString(value.byteArrayValue());
         }
      }

      return (result);
   }

   /**
    * This method rerieves a string of the specified type,
    * belonging to the item with the specified unique ID.
    *
    * @param id unique ID of entity to which this data belongs
    * @param type data type identifier
    * @return required string data
    */
   public String getString (Integer id, Integer type)
   {
      return (getString (m_meta.getOffset(id, type)));
   }


   /**
    * This method rerieves an integer of the specified type,
    * belonging to the item with the specified unique ID.
    *
    * @param id unique ID of entity to which this data belongs
    * @param type data type identifier
    * @return required integer data
    */
   public int getShort (Integer id, Integer type)
   {
      int result = 0;

      Integer offset = m_meta.getOffset (id, type);

      if (offset != null)
      {
         ByteArray value = (ByteArray)m_map.get (offset);

         if (value != null)
         {
            result = MPPUtility.getShort(value.byteArrayValue());
         }
      }

      return (result);
   }

   /**
    * This method rerieves an integer of the specified type,
    * belonging to the item with the specified unique ID.
    *
    * @param id unique ID of entity to which this data belongs
    * @param type data type identifier
    * @return required integer data
    */
   public int getInt (Integer id, Integer type)
   {
      int result = 0;

      Integer offset = m_meta.getOffset (id, type);

      if (offset != null)
      {
         ByteArray value = (ByteArray)m_map.get (offset);

         if (value != null)
         {
            result = MPPUtility.getInt(value.byteArrayValue());
         }
      }

      return (result);
   }

   /**
    * This method rerieves an integer of the specified type,
    * belonging to the item with the specified unique ID.
    *
    * @param id unique ID of entity to which this data belongs
    * @param type data type identifier
    * @return required integer data
    */
   public long getLong (Integer id, Integer type)
   {
      long result = 0;

      Integer offset = m_meta.getOffset (id, type);

      if (offset != null)
      {
         ByteArray value = (ByteArray)m_map.get (offset);

         if (value != null)
         {
            result = MPPUtility.getLong(value.byteArrayValue());
         }
      }

      return (result);
   }

   /**
    * This method rerieves a double of the specified type,
    * belonging to the item with the specified unique ID.
    *
    * @param id unique ID of entity to which this data belongs
    * @param type data type identifier
    * @return required double data
    */
   public double getDouble (Integer id, Integer type)
   {
      return (Double.longBitsToDouble(getLong (id, type)));
   }


   /**
    * This method dumps the contents of this Var2Data block as a String.
    * Note that this facility is provided as a debugging aid.
    *
    * @return formatted contents of this block
    */
   public String toString ()
   {
      StringWriter sw = new StringWriter ();
      PrintWriter pw = new PrintWriter (sw);
      Iterator iter = m_map.keySet().iterator();
      Integer offset;
      ByteArray data;

      pw.println ("BEGIN Var2Data");
      while (iter.hasNext() == true)
      {
         offset = (Integer)iter.next();
         data = (ByteArray)m_map.get(offset);
         pw.println ("   Data at offset: " + offset + " size: " + data.byteArrayValue().length);
         pw.println ("  " + MPPUtility.hexdump (data.byteArrayValue(), true));
      }

      pw.println ("END Var2Data");
      pw.println ();
      pw.close();
      return (sw.toString());
   }

   /**
    * Map containing data items indexed by offset.
    */
   private TreeMap m_map = new TreeMap ();

   /**
    * Reference to the meta data associated with this block.
    */
   private VarMeta m_meta;
}
