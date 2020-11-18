/*
 * file:       PopulatedFields.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2020
 * date:       18/11/2020
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

package net.sf.mpxj.common;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;

import net.sf.mpxj.FieldContainer;
import net.sf.mpxj.FieldType;

/**
 * Given a collection of objects containing fields, return a set representing
 * all of the fields which have a non-null value in any of the objects.
 *
 * @param <E> field enumeration
 * @param <T> object type
 */
public class PopulatedFields<E extends Enum<E>, T extends FieldContainer>
{
   /**
    * Constructor.
    * 
    * @param fieldEnumType enumeration representing the set of fields
    * @param collection collection of objects containing fields
    */
   public PopulatedFields(Class<E> fieldEnumType, Collection<T> collection)
   {
      m_fieldEnumType = fieldEnumType;
      m_collection = collection;
   }

   /**
    * Retrieve the set of fields populated across the collection of objects.
    * 
    * @return populated fields
    */
   public Set<E> getPopulatedFields()
   {
      Set<E> unusedFields = EnumSet.allOf(m_fieldEnumType);

      for (FieldContainer item : m_collection)
      {
         Iterator<E> iter = unusedFields.iterator();
         while (iter.hasNext())
         {
            if (item.getCachedValue((FieldType) iter.next()) != null)
            {
               iter.remove();
            }
         }
      }

      Set<E> usedFields = EnumSet.allOf(m_fieldEnumType);
      usedFields.removeAll(unusedFields);

      return usedFields;
   }

   private final Class<E> m_fieldEnumType;
   private final Collection<T> m_collection;
}
