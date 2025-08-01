/*
 * file:       ArrayRow.java
 * author:     Jon Iles
 * date:       2025-06-29
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

package org.mpxj.primavera;

import java.util.Map;

/**
 * Implementation of the Row interface, wrapping an array.
 */
class ArrayRow extends AbstractRow
{
   /**
    * Constructor.
    *
    * @param index field index
    * @param array field data
    * @param ignoreErrors true if errors reading values are ignored
    */
   public ArrayRow(Map<String, Integer> index, Object[] array, boolean ignoreErrors)
   {
      super(ignoreErrors);
      m_index = index;
      m_array = array;
   }

   /**
    * Retrieve a value from the map.
    *
    * @param name column name
    * @return column value
    */
   @Override protected Object getObject(String name)
   {
      Integer index = m_index.get(name);
      return index == null ? null : m_array[index.intValue()];
   }

   protected final Object[] m_array;
   private final Map<String, Integer> m_index;
}
