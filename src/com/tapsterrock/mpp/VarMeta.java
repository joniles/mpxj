/*
 * file:       VarMeta.java
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

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

/**
 * This class reads in the data from a VarMeta block. This block contains
 * meta data about variable length data items stored in a Var2Data block.
 * The meta data allows the size of the Var2Data block to be determined,
 * along with the number of data items it contains, identifiers for each item,
 * and finally the offset of each item within the block.
 */
final class VarMeta extends MPPComponent
{
   /**
    * Constructor. Extracts that makes up this block from the input stream.
    *
    * @param is Input stream from whic data is read
    * @throws IOException on file read error
    */
   VarMeta (InputStream is)
      throws IOException
   {
      if (readInt (is) != MAGIC)
      {
         throw new IOException ("Bad magic number");
      }

      m_unknown1 = readInt (is);
      m_itemCount = readInt (is);
      m_unknown2 = readInt (is);
      m_unknown3 = readInt (is);
      m_dataSize = readInt (is);

      Integer uniqueID;
      Integer type;
      Integer offset;
      TreeMap map;
      m_offsets = new int[m_itemCount];

      for (int loop=0; loop < m_itemCount; loop++)
      {
         uniqueID = new Integer (readShort (is));
         readByte (is);
         type = new Integer (readByte (is));
         offset = new Integer (readInt (is));

         map = (TreeMap)m_table.get (uniqueID);
         if (map == null)
         {
            map = new TreeMap ();
            m_table.put (uniqueID, map);
         }
         map.put (type, offset);
         m_offsets[loop] = offset.intValue();
      }
   }

   /**
    * This method retrieves the number of items in the Var2Data block.
    *
    * @return number of items
    */
   public int getItemCount ()
   {
      return (m_itemCount);
   }

   /**
    * This method retrieves the size of the Var2Data block.
    *
    * @return data size
    */
   public int getDataSize ()
   {
      return (m_dataSize);
   }

   /**
    * This method returns an array containing all of the unique identifiers
    * for which data has been stored in the Var2Data block.
    *
    * @return array of unique identifiers
    */
   public Integer[] getUniqueIdentifierArray ()
   {
      Integer[] result = new Integer [m_table.size()];
      Iterator iter = m_table.keySet().iterator();
      int index = 0;

      while (iter.hasNext() == true)
      {
         result[index] = (Integer)iter.next();
         ++index;
      }
      return (result);
   }

   /**
    * This method returns an set containing all of the unique identifiers
    * for which data has been stored in the Var2Data block.
    *
    * @return set of unique identifiers
    */   
   public Set getUniqueIdentifierSet ()
   {
      return (m_table.keySet());
   }
   
   /**
    * This method retrieves the offset of a given entry in the Var2Data block.
    * Each entry can be uniquely located by the identifier of the object to
    * which the data belongs, and the type of the data.
    *
    * @param id unique identifier of an entity
    * @param type data type identifier
    * @return offset of requested item
    */
   public Integer getOffset (Integer id, Integer type)
   {
      Integer result = null;

      TreeMap map = (TreeMap)m_table.get(id);
      if (map != null)
      {
         result = (Integer)map.get(type);
      }

      return (result);
   }

   /**
    * This method retrieves the offset of the data item at
    * the position defined by the index parameter.
    *
    * @param index index of item in the block
    * @return offset of the item in the block
    */
   public int getOffset (int index)
   {
      return (m_offsets[index]);
   }

   /**
    * This method dumps the contents of this VarMeta block as a String.
    * Note that this facility is provided as a debugging aid.
    *
    * @return formatted contents of this block
    */
   public String toString ()
   {
      StringWriter sw = new StringWriter ();
      PrintWriter pw = new PrintWriter (sw);

      pw.println ("BEGIN: VarMeta");
      pw.println ("   Item count: " + m_itemCount);
      pw.println ("   Data size: " + m_dataSize);

      Iterator tableIter = m_table.keySet().iterator();
      TreeMap map;
      Iterator mapIter;
      Integer uniqueID;
      Integer type;
      Integer offset;

      while (tableIter.hasNext() == true)
      {
         uniqueID = (Integer)tableIter.next();
         pw.println ("   Entries for Unique ID: " + uniqueID);
         map = (TreeMap)m_table.get(uniqueID);
         mapIter = map.keySet().iterator();
         while (mapIter.hasNext() == true)
         {
            type = (Integer)mapIter.next();
            offset = (Integer)map.get(type);
            pw.println ("      Type=" + type + " Offset=" + offset);
         }
      }

      pw.println ("END: VarMeta");
      pw.println ();

      pw.close ();
      return (sw.toString ());
   }

   private int m_unknown1;
   private int m_itemCount;
   private int m_unknown2;
   private int m_unknown3;
   private int m_dataSize;
   private int[] m_offsets;
   private TreeMap m_table = new TreeMap ();

   private static final int MAGIC = 0xFADFADBA;
}
