/*
 * file:       CriteriaReader.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2010
 * date:       2010-05-06
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.mpxj.DataType;
import org.mpxj.FieldType;
import org.mpxj.FieldTypeClass;
import org.mpxj.GenericCriteria;
import org.mpxj.GenericCriteriaPrompt;
import org.mpxj.ProjectFile;
import org.mpxj.ProjectProperties;
import org.mpxj.TestOperator;
import org.mpxj.common.ByteArrayHelper;

/**
 * This class allows criteria definitions to be read from an MPP file.
 */
public abstract class CriteriaReader
{
   /**
    * Retrieves the offset of the start of the criteria data.
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
    * Retrieves offset of value which determines the start of the text block.
    *
    * @return criteria text start offset
    */
   protected abstract int getCriteriaTextStartOffset();

   /**
    * Retrieves a field type value.
    *
    * @param block criteria block
    * @return field type value
    */
   protected abstract FieldType getFieldType(byte[] block);

   /**
    * Main entry point to read criteria data.
    *
    * @param file project file
    * @param data criteria data block
    * @param dataOffset offset of the data start within the larger data block
    * @param entryOffset offset of start node for walking the tree
    * @param prompts optional list to hold prompts
    * @param fields optional list of hold fields
    * @param criteriaType optional array representing criteria types
    * @return first node of the criteria
    */
   public GenericCriteria process(ProjectFile file, byte[] data, int dataOffset, int entryOffset, List<GenericCriteriaPrompt> prompts, List<FieldType> fields, boolean[] criteriaType)
   {
      m_file = file;
      m_properties = file.getProjectProperties();
      m_prompts = prompts;
      m_fields = fields;
      m_criteriaType = criteriaType;
      m_dataOffset = dataOffset;
      if (m_criteriaType != null)
      {
         m_criteriaType[0] = true;
         m_criteriaType[1] = true;
      }

      m_criteriaBlockMap.clear();

      m_criteriaData = data;
      m_criteriaTextStart = ByteArrayHelper.getShort(m_criteriaData, m_dataOffset + getCriteriaTextStartOffset());

      //
      // Populate the map
      //
      int criteriaStartOffset = getCriteriaStartOffset();
      int criteriaBlockSize = getCriteriaBlockSize();

      //System.out.println();
      //System.out.println(ByteArrayHelper.hexdump(data, dataOffset, criteriaStartOffset, false));

      if (m_criteriaData.length <= m_criteriaTextStart)
      {
         return null; // bad data
      }

      while (criteriaStartOffset + criteriaBlockSize <= m_criteriaTextStart)
      {
         byte[] block = new byte[criteriaBlockSize];
         System.arraycopy(m_criteriaData, m_dataOffset + criteriaStartOffset, block, 0, criteriaBlockSize);
         m_criteriaBlockMap.put(Integer.valueOf(criteriaStartOffset), block);
         //System.out.println(Integer.toHexString(criteriaStartOffset) + ": " + ByteArrayHelper.hexdump(block, false));
         criteriaStartOffset += criteriaBlockSize;
      }

      if (entryOffset == -1)
      {
         entryOffset = getCriteriaStartOffset();
      }

      List<GenericCriteria> list = new ArrayList<>();
      processBlock(list, m_criteriaBlockMap.get(Integer.valueOf(entryOffset)));
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
         if (ByteArrayHelper.getShort(block, 0) > 0x3E6)
         {
            addCriteria(list, block);
         }
         else
         {
            switch (block[0])
            {
               case (byte) 0x0B:
               {
                  processBlock(list, getChildBlock(block));
                  break;
               }

               case (byte) 0x06:
               {
                  processBlock(list, getListNextBlock(block));
                  break;
               }

               case (byte) 0xED: // EQUALS
               {
                  addCriteria(list, block);
                  break;
               }

               case (byte) 0x19: // AND
               case (byte) 0x1B:
               {
                  addBlock(list, block, TestOperator.AND);
                  break;
               }

               case (byte) 0x1A: // OR
               case (byte) 0x1C:
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
      TestOperator operator = TestOperator.getInstance(ByteArrayHelper.getShort(block, 0) - 0x3E7);
      FieldType leftValue = getFieldType(leftBlock);
      Object rightValue1 = getValue(leftValue, rightBlock1);
      Object rightValue2 = rightBlock2 == null ? null : getValue(leftValue, rightBlock2);

      GenericCriteria criteria = new GenericCriteria(m_properties);
      criteria.setLeftValue(leftValue);
      criteria.setOperator(operator);
      criteria.setRightValue(0, rightValue1);
      criteria.setRightValue(1, rightValue2);
      list.add(criteria);

      if (m_criteriaType != null)
      {
         m_criteriaType[0] = leftValue.getFieldTypeClass() == FieldTypeClass.TASK;
         m_criteriaType[1] = !m_criteriaType[0];
      }

      if (m_fields != null)
      {
         m_fields.add(leftValue);
      }

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
      GenericCriteria result = new GenericCriteria(m_properties);
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
         case 0x07: // Field
         {
            result = getFieldType(block);
            break;
         }

         case 0x01: // Constant value
         {
            result = getConstantValue(field, block);
            break;
         }

         case 0x00: // Prompt
         {
            result = getPromptValue(field, block);
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
      DataType dataType = type.getDataType();

      if (dataType == null)
      {
         value = null;
      }
      else
      {
         switch (dataType)
         {
            case DURATION:
            {
               value = MPPUtility.getAdjustedDuration(m_properties, ByteArrayHelper.getInt(block, getValueOffset()), MPPUtility.getDurationTimeUnits(ByteArrayHelper.getShort(block, getTimeUnitsOffset())));
               break;
            }

            case NUMERIC:
            {
               value = Double.valueOf(MPPUtility.getDouble(block, getValueOffset()));
               break;
            }

            case PERCENTAGE:
            {
               value = Double.valueOf(ByteArrayHelper.getShort(block, getValueOffset()));
               break;
            }

            case CURRENCY:
            {
               value = Double.valueOf(MPPUtility.getDouble(block, getValueOffset()) / 100);
               break;
            }

            case STRING:
            {
               int textOffset = getTextOffset(block);
               value = MPPUtility.getUnicodeString(m_criteriaData, m_dataOffset + m_criteriaTextStart + textOffset);
               break;
            }

            case BOOLEAN:
            {
               int intValue = ByteArrayHelper.getShort(block, getValueOffset());
               value = (intValue == 1 ? Boolean.TRUE : Boolean.FALSE);
               break;
            }

            case DATE:
            {
               value = MPPUtility.getTimestamp(block, getValueOffset());
               break;
            }

            default:
            {
               value = null;
               break;
            }
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
      if (m_prompts != null)
      {
         m_prompts.add(prompt);
      }
      return prompt;
   }

   protected ProjectFile m_file;
   private ProjectProperties m_properties;
   private byte[] m_criteriaData;
   private boolean[] m_criteriaType;
   private int m_criteriaTextStart;
   private int m_dataOffset;
   private List<GenericCriteriaPrompt> m_prompts;
   private List<FieldType> m_fields;
   protected final Map<Integer, byte[]> m_criteriaBlockMap = new TreeMap<>();
}
