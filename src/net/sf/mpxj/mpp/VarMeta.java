/*
 * file:       VarMeta.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2005
 * date:       Dec 5, 2005
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

import java.util.Set;

/**
 * Interface implemented by VarMeta types.
 */
interface VarMeta
{
   /**
    * This method retrieves the number of items in the Var2Data block.
    *
    * @return number of items
    */
   public int getItemCount();

   /**
    * This method retrieves the size of the Var2Data block.
    *
    * @return data size
    */
   public int getDataSize();

   /**
    * This method returns an array containing all of the unique identifiers
    * for which data has been stored in the Var2Data block.
    *
    * @return array of unique identifiers
    */
   public Integer[] getUniqueIdentifierArray();

   /**
    * This method returns an set containing all of the unique identifiers
    * for which data has been stored in the Var2Data block.
    *
    * @return set of unique identifiers
    */
   public Set<Integer> getUniqueIdentifierSet();

   /**
    * This method retrieves the offset of a given entry in the Var2Data block.
    * Each entry can be uniquely located by the identifier of the object to
    * which the data belongs, and the type of the data.
    *
    * @param id unique identifier of an entity
    * @param type data type identifier
    * @return offset of requested item
    */
   public Integer getOffset(Integer id, Integer type);

   /**
    * Retrieve the offsets array.
    *
    * @return offsets array
    */
   public int[] getOffsets();

   /**
    * Retrieves a set containing the types defined
    * in the var data for a given ID.
    *
    * @param id unique ID
    * @return set of types
    */
   public Set<Integer> getTypes(Integer id);

   /**
    * This method is used to check if a given key is present.
    *
    * @param key key to test
    * @return Boolean flag
    */
   public boolean containsKey(Integer key);

   /**
    * This method dumps the contents of this VarMeta block as a String.
    * Note that this facility is provided as a debugging aid.
    *
    * @param fieldMap field map used to decode var data keys
    * @return formatted contents of this block
    */
   public String toString(FieldMap fieldMap);
}
