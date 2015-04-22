/*
 * file:       AliasContainer.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2002-2015
 * date:       22/04/2015
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

package net.sf.mpxj;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages aliases used in place of standard field names.
 *
 * @param <T> field type
 */
public class AliasContainer<T extends FieldType>
{
   /**
    * Add an alias for a field.
    * 
    * @param field field type
    * @param alias alias text
    */
   public void add(T field, String alias)
   {
      if (alias != null && !alias.isEmpty())
      {
         m_fieldToAliasMap.put(field, alias);
         m_aliasToFieldMap.put(alias, field);
      }
   }

   /**
    * Retrieve the alias for this field.
    * 
    * @param field field type
    * @return alias, or null if no alias has been defined
    */
   public String getAlias(T field)
   {
      return m_fieldToAliasMap.get(field);
   }

   /**
    * Retrieve the field which has been given this alias.
    * 
    * @param alias alias text
    * @return field this alias has been applied to, or null if no field has this alias
    */
   public T getField(String alias)
   {
      return m_aliasToFieldMap.get(alias);
   }

   private Map<T, String> m_fieldToAliasMap = new HashMap<T, String>();
   private Map<String, T> m_aliasToFieldMap = new HashMap<String, T>();
}
