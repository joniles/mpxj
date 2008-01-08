/*
 * file:       TableFactory.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software Limited 2007
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

package net.sf.mpxj.mpp;

import net.sf.mpxj.Column;
import net.sf.mpxj.MPPResourceField;
import net.sf.mpxj.MPPTaskField;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Table;


/**
 * This interface is implemented by classes which can create Table classes
 * from the data extracted from an MS Project file.
 */
final class TableFactory
{
   /**
    * Constructor.
    * 
    * @param tableColumnDataStandard standard column data key
    * @param tableColumnDataEnterprise enterprise column data key
    */
   public TableFactory (Integer tableColumnDataStandard, Integer tableColumnDataEnterprise)
   {
      m_tableColumnDataStandard = tableColumnDataStandard;
      m_tableColumnDataEnterprise = tableColumnDataEnterprise;    
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
   public Table createTable (ProjectFile file, byte[] data, VarMeta varMeta, Var2Data varData)
   {
      Table table = new Table ();

      table.setID(MPPUtility.getInt(data, 0));
      table.setResourceFlag(MPPUtility.getShort(data, 108) == 1);
      table.setName(MPPUtility.removeAmpersands(MPPUtility.getUnicodeString(data, 4)));
     
      
      byte[] columnData = varData.getByteArray(varMeta.getOffset(new Integer(table.getID()), m_tableColumnDataEnterprise));
      if (columnData == null)
      {
         columnData = varData.getByteArray(varMeta.getOffset(new Integer(table.getID()), m_tableColumnDataStandard));
      }
      
      processColumnData (file, table, columnData);
     
      return (table);
   }
   
   /**
    * Adds columns to a Table instance.
    * 
    * @param file parent project file
    * @param table parent table instance
    * @param data column data
    */
   private void processColumnData (ProjectFile file, Table table, byte[] data)
   {
      if (data != null)
      {
         int columnCount = MPPUtility.getShort(data, 4)+1;
         int index = 8;
         int columnTitleOffset;
         Column  column;
         int alignment;

         for (int loop=0; loop < columnCount; loop++)
         {
            column = new Column (file);

            if (table.getResourceFlag() == false)
            {
               column.setFieldType (MPPTaskField.getInstance(MPPUtility.getShort(data, index)));
            }
            else
            {
               column.setFieldType (MPPResourceField.getInstance(MPPUtility.getShort(data, index)));
            }
            
//            if (column.getFieldType() == null)
//            {               
//               System.out.println("Unknown column type " + MPPUtility.getShort(data, index));
//            }
            
            column.setWidth (MPPUtility.getByte(data, index+4));
            
            columnTitleOffset = MPPUtility.getShort(data, index+6);
            if (columnTitleOffset != 0)
            {
               column.setTitle(MPPUtility.getUnicodeString(data, columnTitleOffset));
            }

            alignment = MPPUtility.getByte(data, index+8);
            if (alignment == 32)
            {
               column.setAlignTitle(Column.ALIGN_LEFT);
            }
            else
            {
               if (alignment == 33)
               {
                  column.setAlignTitle(Column.ALIGN_CENTER);
               }
               else
               {
                  column.setAlignTitle(Column.ALIGN_RIGHT);
               }
            }

            alignment = MPPUtility.getByte(data, index+10);
            if (alignment == 32)
            {
               column.setAlignData(Column.ALIGN_LEFT);
            }
            else
            {
               if (alignment == 33)
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
   
   private Integer m_tableColumnDataStandard;
   private Integer m_tableColumnDataEnterprise;   
}
