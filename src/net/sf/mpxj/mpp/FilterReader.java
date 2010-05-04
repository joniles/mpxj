/*
 * file:       FilterReader.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2006
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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.sf.mpxj.FieldType;
import net.sf.mpxj.Filter;
import net.sf.mpxj.GenericCriteria;
import net.sf.mpxj.GenericCriteriaPrompt;
import net.sf.mpxj.MPPResourceField;
import net.sf.mpxj.MPPTaskField;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ResourceField;
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
    * Retrieves the offset of the start of the filter data.
    * 
    * @return criteria start offset
    */
   protected abstract int getCriteriaStartOffset();

   /**
    * Retrieves the criteria block size.
    * 
    * @return criteria block size
    */
   protected abstract int getCriteriaBlockSize();

   /**
    * Retrieves the child of the current block.
    * 
    * @param block parent block
    * @return child block
    */
   protected abstract byte[] getChildBlock(byte[] block);

   /**
    * Retrieves the next list sibling of this block.
    * 
    * @param block current block
    * @return next sibling list block
    */
   protected abstract byte[] getListNextBlock(byte[] block);

   /**
    * Retrieves the ID of the field type. 
    * 
    * @param block current block
    * @return field type ID
    */
   protected abstract int getFieldIndex(byte[] block);

   /**
    * Retrieves the offset of the start of the text block. 
    * 
    * @param block current block
    * @return text block start offset
    */
   protected abstract int getTextOffset(byte[] block);

   /**
    * Retrieves the offset of the prompt text.
    * 
    * @param block current block
    * @return prompt text offset
    */
   protected abstract int getPromptOffset(byte[] block);

   /**
    * Retrieves the offset of the field value.
    * 
    * @return field value offset
    */
   protected abstract int getValueOffset();

   /**
    * Retrieves the offset of the time unit field.
    * 
    * @return time unit field offset
    */
   protected abstract int getTimeUnitsOffset();

   /**
    * Retrieves a TaskField instance based on its ID.
    * 
    * @param index task field index
    * @return TaskField instance
    */
   protected abstract TaskField getTaskField(int index);

   /**
    * Retrieves a ResourceField instance based on its ID.
    * 
    * @param index resource field index
    * @return ResourceField index
    */
   protected abstract ResourceField getResourceField(int index);

   /**
    * Entry point for processing filter definitions.
    * 
    * @param file project file
    * @param fixedData filter fixed data
    * @param varData filter var data
    */
   public void process(ProjectFile file, FixedData fixedData, Var2Data varData)
   {
      m_file = file;

      Filter filter;

      int filterCount = fixedData.getItemCount();
      for (int filterLoop = 0; filterLoop < filterCount; filterLoop++)
      {
         m_isTaskFilter = true;
         m_isResourceFilter = true;

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

         //System.out.println(MPPUtility.hexdump(filterVarData, true, 16, ""));

         m_criteriaTextStart = MPPUtility.getInt(filterVarData, 16);
         filter.setShowRelatedSummaryRows(MPPUtility.getByte(filterVarData, 4) != 0);
         filter.setCriteria(processCriteria(filterVarData));

         filter.setIsTaskFilter(m_isTaskFilter);
         filter.setIsResourceFilter(m_isResourceFilter);
         filter.setPrompts(m_prompts);

         m_file.addFilter(filter);
         //System.out.println(filter);
      }
   }

   /**
    * Extracts the criteria which define the filter.
    * 
    * @param filterVarData filter dat ablock
    * @return first node of the filter criteria
    */
   private GenericCriteria processCriteria(byte[] filterVarData)
   {
      m_criteriaBlockMap.clear();
      m_prompts = new LinkedList<GenericCriteriaPrompt>();

      m_criteriaData = filterVarData;
      m_criteriaTextStart = MPPUtility.getInt(filterVarData, 16);

      //
      // Populate the map
      //
      int offset = getCriteriaStartOffset();
      int blockSize = getCriteriaBlockSize();
      while (offset < m_criteriaTextStart)
      {
         byte[] block = new byte[blockSize];
         System.arraycopy(filterVarData, offset, block, 0, blockSize);
         m_criteriaBlockMap.put(Integer.valueOf(offset), block);
         //System.out.println(Integer.toHexString(offset)+": " +MPPUtility.hexdump(block, false));
         offset += blockSize;
      }

      List<GenericCriteria> list = new LinkedList<GenericCriteria>();
      processBlock(list, m_criteriaBlockMap.get(Integer.valueOf(20)));
      GenericCriteria criteria;
      if (list.isEmpty())
      {
         criteria = null;
      }
      else
      {
         criteria = list.get(0);
      }
      return criteria;
   }

   /**
    * Process a single criteria block.
    * 
    * @param list parent criteria list
    * @param block current block 
    */
   private void processBlock(List<GenericCriteria> list, byte[] block)
   {
      if (block != null)
      {
         if (MPPUtility.getShort(block, 0) > 0x3E6)
         {
            addCriteria(list, block);
         }
         else
         {
            switch (block[0])
            {
               case (byte) 0x0B :
               {
                  processBlock(list, getChildBlock(block));
                  break;
               }

               case (byte) 0x06 :
               {
                  processBlock(list, getListNextBlock(block));
                  break;
               }

               case (byte) 0xED : // EQUALS
               {
                  addCriteria(list, block);
                  break;
               }

               case (byte) 0x19 : // AND
               case (byte) 0x1B :
               {
                  addBlock(list, block, TestOperator.AND);
                  break;
               }

               case (byte) 0x1A : // OR
               case (byte) 0x1C :
               {
                  addBlock(list, block, TestOperator.OR);
                  break;
               }
            }
         }
      }
   }

   /**
    * Adds a basic LHS OPERATOR RHS block.
    * 
    * @param list parent criteria list
    * @param block current block
    */
   private void addCriteria(List<GenericCriteria> list, byte[] block)
   {
      byte[] leftBlock = getChildBlock(block);
      byte[] rightBlock1 = getListNextBlock(leftBlock);
      byte[] rightBlock2 = getListNextBlock(rightBlock1);
      TestOperator operator = TestOperator.getInstance(MPPUtility.getShort(block, 0) - 0x3E7);
      FieldType leftValue = getFieldType(leftBlock);
      Object rightValue1 = getValue(leftValue, rightBlock1);
      Object rightValue2 = rightBlock2 == null ? null : getValue(leftValue, rightBlock2);

      GenericCriteria criteria = new GenericCriteria(m_file);
      criteria.setLeftValue(leftValue);
      criteria.setOperator(operator);
      criteria.setRightValue(0, rightValue1);
      criteria.setRightValue(1, rightValue2);
      list.add(criteria);

      m_isTaskFilter = leftValue instanceof TaskField;
      m_isResourceFilter = !m_isTaskFilter;

      processBlock(list, getListNextBlock(block));
   }

   /**
    * Adds a logical operator block.
    * 
    * @param list parent criteria list
    * @param block current block
    * @param operator logical operator represented by this block
    */
   private void addBlock(List<GenericCriteria> list, byte[] block, TestOperator operator)
   {
      GenericCriteria result = new GenericCriteria(m_file);
      result.setOperator(operator);
      list.add(result);
      processBlock(result.getCriteriaList(), getChildBlock(block));
      processBlock(list, getListNextBlock(block));
   }

   /**
    * Retrieves the value component of a criteria expression.
    * 
    * @param field field type
    * @param block block data
    * @return field value
    */
   private Object getValue(FieldType field, byte[] block)
   {
      Object result = null;

      switch (block[0])
      {
         case 0x07 : // Field
         {
            result = getFieldType(block);
            break;
         }

         case 0x01 : // Constant value
         {
            result = getConstantValue(field, block);
            break;
         }

         case 0x00 : // Prompt
         {
            result = getPromptValue(field, block);
            break;
         }
      }

      return result;
   }

   /**
    * Retrieves a field type value.
    * 
    * @param block criteria block
    * @return field type value
    */
   private FieldType getFieldType(byte[] block)
   {
      FieldType result = null;
      int fieldIndex = getFieldIndex(block);
      switch (fieldIndex & 0xFFFF0000)
      {
         case MPPTaskField.TASK_FIELD_BASE :
         {
            result = getTaskField(fieldIndex & 0xFFFF);
            break;
         }

         case MPPResourceField.RESOURCE_FIELD_BASE :
         {
            result = getResourceField(fieldIndex & 0xFFFF);
            break;
         }
      }
      return result;
   }

   /**
    * Retrieves a constant value.
    * 
    * @param type field type
    * @param block criteria data block
    * @return constant value
    */
   private Object getConstantValue(FieldType type, byte[] block)
   {
      Object value;

      switch (type.getDataType())
      {
         case DURATION :
         {
            value = MPPUtility.getAdjustedDuration(m_file, MPPUtility.getInt(block, getValueOffset()), MPPUtility.getDurationTimeUnits(MPPUtility.getShort(block, getTimeUnitsOffset())));
            break;
         }

         case NUMERIC :
         {
            value = Double.valueOf(MPPUtility.getDouble(block, getValueOffset()));
            break;
         }

         case PERCENTAGE :
         {
            value = Double.valueOf(MPPUtility.getShort(block, getValueOffset()));
            break;
         }

         case CURRENCY :
         {
            value = Double.valueOf(MPPUtility.getDouble(block, getValueOffset()) / 100);
            break;
         }

         case STRING :
         {
            int textOffset = getTextOffset(block);
            value = MPPUtility.getUnicodeString(m_criteriaData, m_criteriaTextStart + textOffset);
            break;
         }

         case BOOLEAN :
         {
            int intValue = MPPUtility.getShort(block, getValueOffset());
            value = (intValue == 1 ? Boolean.TRUE : Boolean.FALSE);
            break;
         }

         case DATE :
         {
            value = MPPUtility.getTimestamp(block, getValueOffset());
            break;
         }

         default :
         {
            value = null;
            break;
         }
      }

      return value;
   }

   /**
    * Retrieves a prompt value.
    * 
    * @param field field type
    * @param block criteria data block
    * @return prompt value
    */
   private GenericCriteriaPrompt getPromptValue(FieldType field, byte[] block)
   {
      int textOffset = getPromptOffset(block);
      String value = MPPUtility.getUnicodeString(m_criteriaData, m_criteriaTextStart + textOffset);
      GenericCriteriaPrompt prompt = new GenericCriteriaPrompt(field.getDataType(), value);
      m_prompts.add(prompt);
      return prompt;
   }

   private ProjectFile m_file;
   private byte[] m_criteriaData;
   private int m_criteriaTextStart;
   private boolean m_isTaskFilter;
   private boolean m_isResourceFilter;
   private List<GenericCriteriaPrompt> m_prompts;
   protected Map<Integer, byte[]> m_criteriaBlockMap = new TreeMap<Integer, byte[]>();
}
