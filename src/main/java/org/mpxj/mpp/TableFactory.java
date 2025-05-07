/*
 * file:       TableFactory.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2007
 * date:       09/12/2007
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

import org.mpxj.Column;
import org.mpxj.ProjectFile;
import org.mpxj.Table;
import org.mpxj.common.ByteArrayHelper;
import org.mpxj.common.FieldTypeHelper;

/**
 * This interface is implemented by classes which can create Table classes
 * from the data extracted from an MS Project file.
 */
final class TableFactory
{
   /**
    * Constructor.
    *
    * @param tableColumnDataStandard standard columns data key
    * @param tableColumnDataEnterprise enterprise columns data key
    * @param tableColumnDataBaseline baseline columns data key
    */
   public TableFactory(Integer tableColumnDataStandard, Integer tableColumnDataEnterprise, Integer tableColumnDataBaseline)
   {
      m_tableColumnDataStandard = tableColumnDataStandard;
      m_tableColumnDataEnterprise = tableColumnDataEnterprise;
      m_tableColumnDataBaseline = tableColumnDataBaseline;
   }

   /**
    * Creates a new Table instance from data extracted from an MPP file.
    *
    * @param file parent project file
    * @param data fixed data
    * @param varMeta var meta
    * @param varData var data
    * @return Table instance
    */
   public Table createTable(ProjectFile file, byte[] data, VarMeta varMeta, Var2Data varData)
   {
      Table table = new Table();

      table.setID(ByteArrayHelper.getInt(data, 0));
      table.setResourceFlag(ByteArrayHelper.getShort(data, 108) == 1);
      table.setName(MPPUtility.removeAmpersands(MPPUtility.getUnicodeString(data, 4)));

      byte[] columnData = null;
      Integer tableID = Integer.valueOf(table.getID());
      if (m_tableColumnDataBaseline != null)
      {
         columnData = varData.getByteArray(varMeta.getOffset(tableID, m_tableColumnDataBaseline));
      }

      if (columnData == null)
      {
         columnData = varData.getByteArray(varMeta.getOffset(tableID, m_tableColumnDataEnterprise));
         if (columnData == null)
         {
            columnData = varData.getByteArray(varMeta.getOffset(tableID, m_tableColumnDataStandard));
         }
      }

      processColumnData(file, table, columnData);

      //System.out.println(table);

      return (table);
   }

   /**
    * Adds columns to a Table instance.
    *
    * @param file parent project file
    * @param table parent table instance
    * @param data column data
    */
   private void processColumnData(ProjectFile file, Table table, byte[] data)
   {
      //System.out.println("Table=" + table.getName());
      //System.out.println(ByteArrayHelper.hexdump(data, 8, data.length-8, false, 12, ""));
      if (data != null && data.length > 6)
      {
         int columnCount = ByteArrayHelper.getShort(data, 4) + 1;
         int index = 8;
         int columnTitleOffset;
         Column column;
         int alignment;

         for (int loop = 0; loop < columnCount; loop++)
         {
            column = new Column(file);
            column.setFieldType(FieldTypeHelper.getInstance(file, ByteArrayHelper.getInt(data, index)));

            //                        if (column.getFieldType() == null)
            //                        {
            //                           System.out.println(loop + ": Unknown column type " + fieldType);
            //                        }
            //                        else
            //                        {
            //                           System.out.println(loop + ": " + column.getFieldType());
            //                        }

            column.setWidth(MPPUtility.getByte(data, index + 4));

            columnTitleOffset = ByteArrayHelper.getShort(data, index + 6);
            if (columnTitleOffset != 0)
            {
               column.setTitle(MPPUtility.getUnicodeString(data, columnTitleOffset));
            }

            alignment = MPPUtility.getByte(data, index + 8);
            if ((alignment & 0x0F) == 0x00)
            {
               column.setAlignTitle(Column.ALIGN_LEFT);
            }
            else
            {
               if ((alignment & 0x0F) == 0x01)
               {
                  column.setAlignTitle(Column.ALIGN_CENTER);
               }
               else
               {
                  column.setAlignTitle(Column.ALIGN_RIGHT);
               }
            }

            alignment = MPPUtility.getByte(data, index + 10);
            if ((alignment & 0x0F) == 0x00)
            {
               column.setAlignData(Column.ALIGN_LEFT);
            }
            else
            {
               if ((alignment & 0x0F) == 0x01)
               {
                  column.setAlignData(Column.ALIGN_CENTER);
               }
               else
               {
                  column.setAlignData(Column.ALIGN_RIGHT);
               }
            }

            table.addColumn(column);
            index += 12;
         }
      }
   }

   private final Integer m_tableColumnDataStandard;
   private final Integer m_tableColumnDataEnterprise;
   private final Integer m_tableColumnDataBaseline;
}
