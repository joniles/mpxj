/*
 * file:       FieldContainerDependencies.java
 * author:     Jon Iles
 * date:       2022-09-11
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

package org.mpxj;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Trivial class used to construct a list of dependencies for a calculated field.
 * This enabled the cached value for this field to be cleared when one of the
 * dependencies changes, forcing recalculation
 * when the calculated field is next referenced.
 *
 * @param <T>
 */
class FieldContainerDependencies<T>
{
   /**
    * Constructor.
    *
    * @param map map to populate with the dependencies
    */
   public FieldContainerDependencies(Map<T, List<T>> map)
   {
      m_map = map;
   }

   /**
    * Identify the target calculated field.
    *
    * @param type calculated field
    * @return this to allow method chaining
    */
   public FieldContainerDependencies<T> calculatedField(T type)
   {
      m_currentField = type;
      return this;
   }

   /**
    * Identify one or more fields on which the calculated field depends.
    *
    * @param fields array of dependency fields
    */
   @SafeVarargs public final void dependsOn(T... fields)
   {
      Arrays.stream(fields).forEach(field -> m_map.computeIfAbsent(field, f -> new ArrayList<>()).add(m_currentField));
   }

   private T m_currentField;
   private final Map<T, List<T>> m_map;
}
