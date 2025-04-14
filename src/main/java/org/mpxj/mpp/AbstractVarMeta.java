/*
 * file:       AbstractVarMeta.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2002-2005
 * date:       05/12/2005
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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.mpxj.FieldType;

/**
 * This class reads in the data from a VarMeta block. This block contains
 * metadata about variable length data items stored in a Var2Data block.
 * The metadata allows the size of the Var2Data block to be determined,
 * along with the number of data items it contains, identifiers for each item,
 * and finally the offset of each item within the block.
 */
abstract class AbstractVarMeta extends MPPComponent implements VarMeta
{
   /**
    * This method retrieves the number of items in the Var2Data block.
    *
    * @return number of items
    */
   @Override public int getItemCount()
   {
      return (m_itemCount);
   }

   /**
    * This method retrieves the size of the Var2Data block.
    *
    * @return data size
    */
   @Override public int getDataSize()
   {
      return (m_dataSize);
   }

   /**
    * This method returns an array containing all of the unique identifiers
    * for which data has been stored in the Var2Data block.
    *
    * @return array of unique identifiers
    */
   @Override public Integer[] getUniqueIdentifierArray()
   {
      Integer[] result = new Integer[m_table.size()];
      int index = 0;
      for (Integer value : m_table.keySet())
      {
         result[index] = value;
         ++index;
      }
      return (result);
   }

   /**
    * This method returns a set containing all of the unique identifiers
    * for which data has been stored in the Var2Data block.
    *
    * @return set of unique identifiers
    */
   @Override public Set<Integer> getUniqueIdentifierSet()
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
   @Override public Integer getOffset(Integer id, Integer type)
   {
      Integer result = null;

      Map<Integer, Integer> map = m_table.get(id);
      if (map != null && type != null)
      {
         result = map.get(type);
      }

      return (result);
   }

   /**
    * Allows subclasses to provide the array of offsets.
    *
    * @param offsets array of offsets
    */
   protected void setOffsets(int[] offsets)
   {
      m_offsets = offsets;
   }

   @Override public int[] getOffsets()
   {
      return m_offsets;
   }

   @Override public Set<Integer> getTypes(Integer id)
   {
      Set<Integer> result;

      Map<Integer, Integer> map = m_table.get(id);
      if (map != null)
      {
         result = map.keySet();
      }
      else
      {
         result = new HashSet<>();
      }

      return (result);
   }

   @Override public boolean containsKey(Integer key)
   {
      return m_table.containsKey(key);
   }

   /**
    * This method dumps the contents of this VarMeta block as a String.
    * Note that this facility is provided as a debugging aid.
    *
    * @return formatted contents of this block
    */
   @Override public String toString()
   {
      return toString(null);
   }

   /**
    * This method dumps the contents of this VarMeta block as a String.
    * Note that this facility is provided as a debugging aid.
    *
    * @param fieldMap field map used to decode var data keys
    * @return formatted contents of this block
    */
   @Override public String toString(FieldMap fieldMap)
   {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);

      pw.println("BEGIN: VarMeta");
      pw.println("   Item count: " + m_itemCount);
      pw.println("   Data size: " + m_dataSize);

      for (Map.Entry<Integer, Map<Integer, Integer>> tableEntry : m_table.entrySet())
      {
         Integer uniqueID = tableEntry.getKey();
         pw.println("   Entries for Unique ID: " + uniqueID);
         Map<Integer, Integer> map = tableEntry.getValue();
         for (Map.Entry<Integer, Integer> entry : map.entrySet())
         {
            FieldType fieldType = fieldMap == null ? null : fieldMap.getFieldTypeFromVarDataKey(entry.getKey());
            pw.println("      Type=" + (fieldType == null ? entry.getKey() : fieldType) + " Offset=" + entry.getValue());
         }
      }

      pw.println("END: VarMeta");
      pw.println();

      pw.close();
      return (sw.toString());
   }

   //protected int m_unknown1;
   protected int m_itemCount;
   //protected int m_unknown2;
   //protected int m_unknown3;
   protected int m_dataSize;
   private int[] m_offsets;
   protected final Map<Integer, Map<Integer, Integer>> m_table = new TreeMap<>();
}
