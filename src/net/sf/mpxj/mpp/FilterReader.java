/*
 * file:       FilterReader.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software Limited 2006
 * date:       2006-10-31
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

import net.sf.mpxj.FieldType;
import net.sf.mpxj.Filter;
import net.sf.mpxj.FilterCriteria;
import net.sf.mpxj.MPPResourceField;
import net.sf.mpxj.MPPTaskField;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.TaskField;
import net.sf.mpxj.TestOperator;

/**
 * This class allows filter definitions to be read from an MPP file.
 */
public abstract class FilterReader
{
   /**
    * Retrieves the type used for the VarData lookup.
    * 
    * @return VarData type
    */
   protected abstract Integer getVarDataType();

   /**
    * Entry point for processing filter definitions.
    * 
    * @param file project file
    * @param fixedData filter fixed data
    * @param varData filter var data
    */
   public void process(ProjectFile file, FixedData fixedData, Var2Data varData)
   {
      Filter filter;
      boolean lastLogicalAnd = true;

      int filterCount = fixedData.getItemCount();
      for (int filterLoop = 0; filterLoop < filterCount; filterLoop++)
      {
         byte[] filterFixedData = fixedData.getByteArrayValue(filterLoop);
         if (filterFixedData == null || filterFixedData.length < 4)
         {
            continue;
         }

         filter = new Filter();
         filter.setID(Integer.valueOf(MPPUtility.getInt(filterFixedData, 0)));
         filter.setName(MPPUtility.removeAmpersands(MPPUtility.getUnicodeString(filterFixedData, 4)));
         byte[] filterVarData = varData.getByteArray(filter.getID(), getVarDataType());
         if (filterVarData == null)
         {
            continue;
         }

         //System.out.println(filter.getName());
         //System.out.println(MPPUtility.hexdump(filterVarData, true, 16, ""));

         int varDataOffset = MPPUtility.getInt(filterVarData, 16);
         filter.setShowRelatedSummaryRows(MPPUtility.getByte(filterVarData, 4) != 0);

         // 20 byte header, ignore first 2 80 byte blocks
         int offset = 20 + (2 * 80);
         while (offset + (3 * 80) <= varDataOffset)
         {
            FilterCriteria criteria = new FilterCriteria(file);
            filter.addCriteria(criteria);

            int operatorValue = MPPUtility.getInt(filterVarData, offset);
            criteria.setOperator(TestOperator.getInstance(operatorValue - 0x3E7));

            int fieldType = MPPUtility.getShort(filterVarData, offset + 120);
            int entityType = MPPUtility.getByte(filterVarData, offset + 123);

            FieldType type = null;
            switch (entityType)
            {
               case 0x0B :
               {
                  type = MPPTaskField.getInstance(fieldType);
                  break;
               }

               case 0x0C :
               {
                  type = MPPResourceField.getInstance(fieldType);
                  break;
               }
            }
            criteria.setField(type);

            if (MPPUtility.getByte(filterVarData, offset + 224) == 0)
            {
               Object value = getValue(file, type, filterVarData, offset, varDataOffset);
               criteria.setValue(0, value);
               //System.out.println("Value=" + value);
            }
            else
            {
               String prompt = getPrompt(filterVarData, offset, varDataOffset);
               criteria.setPromptText(0, prompt);
               //System.out.println("Prompt=" + prompt);
            }

            if (criteria.getOperator() == TestOperator.IS_WITHIN || criteria.getOperator() == TestOperator.IS_NOT_WITHIN)
            {
               if (MPPUtility.getByte(filterVarData, offset + 224 + 80) == 0)
               {
                  Object value = getValue(file, type, filterVarData, offset + 80, varDataOffset);
                  criteria.setValue(1, value);
                  //System.out.println("Value=" + value);
               }
               else
               {
                  String prompt = getPrompt(filterVarData, offset + 80, varDataOffset);
                  criteria.setPromptText(1, prompt);
                  //System.out.println("Prompt=" + prompt);
               }

               offset += (4 * 80);
            }
            else
            {
               offset += (3 * 80);
            }

            //System.out.println(MPPUtility.hexdump(filterVarData, startOffset, offset-startOffset, true, 16, ""));
            //System.out.println(criteria);

            // have we got enough data left for the logical operator
            if (offset + 80 > varDataOffset)
            {
               continue;
            }

            // extract the logical operator here
            int logicalOperator = MPPUtility.getShort(filterVarData, offset);
            //System.out.println("LogicalOperator=" + Integer.toHexString(logicalOperator));
            switch (logicalOperator)
            {
               case 0x19 :
               case 0x1B :
               {
                  criteria.setLogicalAnd(true);
                  lastLogicalAnd = true;
                  offset += 80;
                  break;
               }

               case 0x1A :
               case 0x1C :
               {
                  criteria.setLogicalAnd(false);
                  lastLogicalAnd = false;
                  offset += 80;
                  break;
               }

               default :
               {
                  criteria.setLogicalAnd(lastLogicalAnd);
                  break;
               }
            }

         }

         file.addFilter(filter);
         //System.out.println(filter);
      }
   }

   /**
    * Extracts the RHS value from a filter expression.
    * 
    * @param file project file
    * @param type field type
    * @param filterVarData filter data
    * @param offset current offset into filter data
    * @param varDataOffset offset to variable data at end of block
    * @return value object
    */
   private Object getValue(ProjectFile file, FieldType type, byte[] filterVarData, int offset, int varDataOffset)
   {
      Object value = null;

      boolean valueFlag = (MPPUtility.getInt(filterVarData, offset + 160) == 1);
      if (valueFlag == false)
      {
         int field = MPPUtility.getShort(filterVarData, offset + 200);
         if (type instanceof TaskField)
         {
            value = MPPTaskField.getInstance(field);
         }
         else
         {
            value = MPPResourceField.getInstance(field);
         }
      }
      else
      {
         switch (type.getDataType())
         {
            case DURATION :
            {
               value = MPPUtility.getAdjustedDuration(file, MPPUtility.getInt(filterVarData, offset + 192), MPPUtility.getDurationTimeUnits(MPPUtility.getShort(filterVarData, offset + 192)));
               break;
            }

            case NUMERIC :
            {
               value = Double.valueOf(MPPUtility.getDouble(filterVarData, offset + 192));
               break;
            }

            case PERCENTAGE :
            {
               value = Double.valueOf(MPPUtility.getInt(filterVarData, offset + 192));
               break;
            }

            case CURRENCY :
            {
               value = Double.valueOf(MPPUtility.getDouble(filterVarData, offset + 192) / 100);
               break;
            }

            case STRING :
            {
               int textOffset = MPPUtility.getShort(filterVarData, offset + 228);
               value = MPPUtility.getUnicodeString(filterVarData, varDataOffset + textOffset);
               break;
            }

            case BOOLEAN :
            {
               int intValue = MPPUtility.getShort(filterVarData, offset + 192);
               value = (intValue == 1 ? Boolean.TRUE : Boolean.FALSE);
               break;
            }

            case DATE :
            {
               value = MPPUtility.getTimestamp(filterVarData, offset + 192);
               break;
            }

            default :
            {
               break;
            }
         }
      }

      return (value);
   }

   /**
    * Retrieve prompt text.
    * 
    * @param filterVarData filter data
    * @param offset current offset
    * @param varDataOffset variable data offset
    * @return prompt text
    */
   private String getPrompt(byte[] filterVarData, int offset, int varDataOffset)
   {
      int textOffset = MPPUtility.getShort(filterVarData, offset + 232);
      String value = MPPUtility.getUnicodeString(filterVarData, varDataOffset + textOffset);
      return (value);
   }
}
