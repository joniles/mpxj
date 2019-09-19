/*
 * file:       VarDataFieldReader.java
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

package net.sf.mpxj.mpp;

import net.sf.mpxj.CustomFieldContainer;

/**
 * Core implementation to read fields from var data, including
 * handling for retrieving items from lookup tables. 
 */
abstract class VarDataFieldReader
{
   /**
    * Constructor.
    * 
    * @param customFields custom fields container
    */
   public VarDataFieldReader(CustomFieldContainer customFields)
   {
      m_customFields = customFields;
   }
   
   /**
    * Retrieve a value from the var data block.
    * 
    * @param varData var data block
    * @param id value ID
    * @param type value type
    * @return value from var data block
    */
   public Object getValue(Var2Data varData, Integer id, Integer type)
   {
      Object result = null;

      //
      // Note that this simplistic approach could produce false positives
      //
      int mask = varData.getShort(id, type);
      if ((mask & 0xFF00) == VALUE_LIST_MASK)
      {         
         CustomFieldValueItem item;
         byte[] data = varData.getByteArray(id, type);
         
         // 2 byte mask, 4 byte unique ID, 16 byte GUID?, 4 byte unknown?
         if (data.length == 26)
         {
            int uniqueId = MPPUtility.getInt(data, 2);
            item = m_customFields.getCustomFieldValueItemByUniqueID(uniqueId);            
            if (item != null)
            {
               result = coerceValue(item.getValue());
            }
            else
            {
               // Haven't found any sample data yet which ends up here 
            }                       
         }
         else
         {
            // Do we potentially have the 2 byte flag, plus a 4 byte unique ID?           
            if (data.length >= 6)
            {
               int uniqueId = MPPUtility.getInt(data, 2);
               item = m_customFields.getCustomFieldValueItemByUniqueID(uniqueId);
               if (item == null)
               {
                  // Fall back on the readValue method to make sense of the value.
                  result = readValue(varData, id, type);
               }
               else
               {
                  result = coerceValue(item.getValue());
               }            
            }
            else
            {
               // None of the types we read have only one or two bytes, so ignore those values. 
               if (data.length > 2)
               {
                  // Fall back on the readValue method to make sense of the value.
                  result = readValue(varData, id, type);
               }
            }
         }
      }
      else
      {
         result = readValue(varData, id, type);
      }
            
      return result;
   }

   /**
    * Sub classes override this method to read a value of the appropriate type.
    * 
    * @param varData var data block
    * @param id value ID
    * @param type value type
    * @return value
    */
   protected abstract Object readValue(Var2Data varData, Integer id, Integer type);
   
   /**
    * Sub classes override this method to transform the data type of the value
    * read from the var data block.
    * 
    * @param value value read from var data block
    * @return coerced value
    */
   protected abstract Object coerceValue(Object value);
     
   private final CustomFieldContainer m_customFields;
   private static final int VALUE_LIST_MASK = 0x0700;
}
