/*
 * file:       GraphicalIndicatorReader.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2006
 * date:       16-Feb-2006
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
import org.mpxj.Duration;
import org.mpxj.FieldType;
import org.mpxj.GraphicalIndicator;
import org.mpxj.GraphicalIndicatorCriteria;
import org.mpxj.ProjectFile;
import org.mpxj.ProjectProperties;
import org.mpxj.TestOperator;
import org.mpxj.common.ByteArrayHelper;
import org.mpxj.common.FieldTypeHelper;

/**
 * This class allows graphical indicator definitions to be read from an MPP
 * file.
 */
public final class GraphicalIndicatorReader
{
   /**
    * The main entry point for processing graphical indicator definitions.
    *
    * @param file project file
    * @param props properties data
    */
   public void process(ProjectFile file, Props props)
   {
      m_file = file;
      m_container = file.getCustomFields();
      m_properties = file.getProjectProperties();
      m_data = props.getByteArray(Props.TASK_FIELD_ATTRIBUTES);

      if (m_data != null)
      {
         int columnsCount = ByteArrayHelper.getInt(m_data, 4);
         m_headerOffset = 8;
         for (int loop = 0; loop < columnsCount; loop++)
         {
            processColumns();
         }
      }
   }

   /**
    * Processes graphical indicator definitions for each column.
    */
   private void processColumns()
   {
      int fieldID = ByteArrayHelper.getInt(m_data, m_headerOffset);
      m_headerOffset += 4;

      m_dataOffset = ByteArrayHelper.getInt(m_data, m_headerOffset);
      m_headerOffset += 4;

      FieldType type = FieldTypeHelper.getInstance(m_file, fieldID);
      if (type.getDataType() != null)
      {
         processKnownType(type);
      }
   }

   /**
    * Process a graphical indicator definition for a known type.
    *
    * @param type field type
    */
   private void processKnownType(FieldType type)
   {
      //System.out.println("Header: " + type);
      //System.out.println(ByteArrayHelper.hexdump(m_data, m_dataOffset, 36, false, 16, ""));
      int flags = m_data[m_dataOffset];
      if ((flags & 0x02) == 0)
      {
         return;
      }

      GraphicalIndicator indicator = m_container.getOrCreate(type).getGraphicalIndicator();
      indicator.setProjectSummaryInheritsFromSummaryRows((flags & 0x08) != 0);
      indicator.setSummaryRowsInheritFromNonSummaryRows((flags & 0x04) != 0);
      indicator.setDisplayGraphicalIndicators((flags & 0x02) != 0);
      indicator.setShowDataValuesInToolTips((flags & 0x01) != 0);
      m_dataOffset += 20;

      int nonSummaryRowOffset = ByteArrayHelper.getInt(m_data, m_dataOffset) - 36;
      m_dataOffset += 4;

      int summaryRowOffset = ByteArrayHelper.getInt(m_data, m_dataOffset) - 36;
      m_dataOffset += 4;

      int projectSummaryOffset = ByteArrayHelper.getInt(m_data, m_dataOffset) - 36;
      m_dataOffset += 4;

      int dataSize = ByteArrayHelper.getInt(m_data, m_dataOffset) - 36;
      m_dataOffset += 4;

      //System.out.println("Data");
      //System.out.println(ByteArrayHelper.hexdump(m_data, m_dataOffset, dataSize, false, 16, ""));

      int maxNonSummaryRowOffset = m_dataOffset + summaryRowOffset;
      int maxSummaryRowOffset = m_dataOffset + projectSummaryOffset;
      int maxProjectSummaryOffset = m_dataOffset + dataSize;

      m_dataOffset += nonSummaryRowOffset;

      while (m_dataOffset + 2 < maxNonSummaryRowOffset)
      {
         indicator.addNonSummaryRowCriteria(processCriteria(type));
      }

      while (m_dataOffset + 2 < maxSummaryRowOffset)
      {
         indicator.addSummaryRowCriteria(processCriteria(type));
      }

      while (m_dataOffset + 2 < maxProjectSummaryOffset)
      {
         indicator.addProjectSummaryCriteria(processCriteria(type));
      }
   }

   /**
    * Process the graphical indicator criteria for a single column.
    *
    * @param type field type
    * @return indicator criteria data
    */
   private GraphicalIndicatorCriteria processCriteria(FieldType type)
   {
      GraphicalIndicatorCriteria criteria = new GraphicalIndicatorCriteria(m_properties);
      criteria.setLeftValue(type);

      int indicatorType = ByteArrayHelper.getInt(m_data, m_dataOffset);
      m_dataOffset += 4;
      criteria.setIndicator(indicatorType);

      if (m_dataOffset + 4 < m_data.length)
      {
         int operatorValue = ByteArrayHelper.getInt(m_data, m_dataOffset);
         m_dataOffset += 4;
         TestOperator operator = (operatorValue == 0 ? TestOperator.IS_ANY_VALUE : TestOperator.getInstance(operatorValue - 0x3E7));
         criteria.setOperator(operator);

         if (operator != TestOperator.IS_ANY_VALUE)
         {
            processOperandValue(0, type, criteria);

            if (operator == TestOperator.IS_WITHIN || operator == TestOperator.IS_NOT_WITHIN)
            {
               processOperandValue(1, type, criteria);
            }
         }
      }

      return (criteria);
   }

   /**
    * Process an operand value used in the definition of the graphical
    * indicator criteria.
    *
    * @param index position in operand list
    * @param type field type
    * @param criteria indicator criteria
    */
   private void processOperandValue(int index, FieldType type, GraphicalIndicatorCriteria criteria)
   {
      boolean valueFlag = (ByteArrayHelper.getInt(m_data, m_dataOffset) == 1);
      m_dataOffset += 4;

      if (!valueFlag)
      {
         int fieldID = ByteArrayHelper.getInt(m_data, m_dataOffset);
         criteria.setRightValue(index, FieldTypeHelper.getInstance(m_file, fieldID));
         m_dataOffset += 4;
      }
      else
      {
         //int dataTypeValue = MPPUtility.getShort(m_data, m_dataOffset);
         m_dataOffset += 2;

         switch (type.getDataType())
         {
            case DURATION: // 0x03
            {
               Duration value = MPPUtility.getAdjustedDuration(m_properties, ByteArrayHelper.getInt(m_data, m_dataOffset), MPPUtility.getDurationTimeUnits(ByteArrayHelper.getShort(m_data, m_dataOffset + 4)));
               m_dataOffset += 6;
               criteria.setRightValue(index, value);
               break;
            }

            case NUMERIC: // 0x05
            {
               Double value = Double.valueOf(MPPUtility.getDouble(m_data, m_dataOffset));
               m_dataOffset += 8;
               criteria.setRightValue(index, value);
               break;
            }

            case CURRENCY: // 0x06
            {
               Double value = Double.valueOf(MPPUtility.getDouble(m_data, m_dataOffset) / 100);
               m_dataOffset += 8;
               criteria.setRightValue(index, value);
               break;
            }

            case STRING: // 0x08
            {
               String value = MPPUtility.getUnicodeString(m_data, m_dataOffset);
               m_dataOffset += ((value.length() + 1) * 2);
               criteria.setRightValue(index, value);
               break;
            }

            case BOOLEAN: // 0x0B
            {
               int value = ByteArrayHelper.getShort(m_data, m_dataOffset);
               m_dataOffset += 2;
               criteria.setRightValue(index, value == 1 ? Boolean.TRUE : Boolean.FALSE);
               break;
            }

            case DATE: // 0x13
            {
               LocalDateTime value = MPPUtility.getTimestamp(m_data, m_dataOffset);
               m_dataOffset += 4;
               criteria.setRightValue(index, value);
               break;
            }

            default:
            {
               break;
            }
         }
      }
   }

   private byte[] m_data;
   private int m_headerOffset;
   private int m_dataOffset;
   private CustomFieldContainer m_container;
   private ProjectFile m_file;
   private ProjectProperties m_properties;
}
