/*
 * file:       StringVarDataFieldReader.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2019
 * date:       18/09/2019
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

import org.mpxj.CustomFieldContainer;

/**
 * Read a string field from the var data.
 */
class StringVarDataFieldReader extends VarDataFieldReader
{
   /**
    * Constructor.
    *
    * @param customFields custom fields container
    */
   public StringVarDataFieldReader(CustomFieldContainer customFields)
   {
      super(customFields);
   }

   @Override protected Object readValue(Var2Data varData, Integer id, Integer type)
   {
      return varData.getUnicodeString(id, type);
   }

   @Override protected Object coerceValue(Object value)
   {
      if (value instanceof String)
      {
         return value;
      }
      return null;
   }
}
