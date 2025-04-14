/*
 * file:       TimestampVarDataFieldReader.java
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

import java.time.LocalDateTime;

import org.mpxj.CustomFieldContainer;

/**
 * Read a timestamp field from the var data.
 */
class TimestampVarDataFieldReader extends VarDataFieldReader
{
   /**
    * Constructor.
    *
    * @param customFields custom fields container
    */
   public TimestampVarDataFieldReader(CustomFieldContainer customFields)
   {
      super(customFields);
   }

   @Override protected Object readValue(Var2Data varData, Integer id, Integer type)
   {
      return getRawTimestampValue(varData, id, type);
   }

   @Override protected Object coerceValue(Object value)
   {
      if (value instanceof LocalDateTime)
      {
         return value;
      }

      return null;
   }

   /**
    * Read a timestamp value from the var data block.
    *
    * @param varData var data block
    * @param id value id
    * @param type value type
    * @return timestamp value
    */
   private Object getRawTimestampValue(Var2Data varData, Integer id, Integer type)
   {
      Object result = null;
      byte[] data = varData.getByteArray(id, type);
      if (data != null)
      {
         if (data.length == 512)
         {
            result = MPPUtility.getUnicodeString(data, 0);
         }
         else
         {
            if (data.length >= 4)
            {
               result = MPPUtility.getTimestamp(data, 0);
            }
         }
      }
      return result;
   }
}
