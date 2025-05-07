/*
 * file:       TextFileRow.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2012
 * date:       29/04/2012
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

package org.mpxj.asta;

import java.sql.Types;
import java.util.HashMap;
import java.util.List;

import org.mpxj.MPXJException;

/**
 * Extends the MapRow class to allow it to manage data read from an Asta file.
 */
class TextFileRow extends MapRow
{
   /**
    * Constructor.
    *
    * @param table table definition
    * @param data table data
    * @param epochDateFormat true if date is represented as an offset from an epoch
    */
   public TextFileRow(TableDefinition table, List<String> data, boolean epochDateFormat)
      throws MPXJException
   {
      super(new HashMap<>());

      ColumnDefinition[] columns = table.getColumns();
      for (int index = 0; index < columns.length; index++)
      {
         ColumnDefinition column = columns[index];
         if (index < data.size())
         {
            if (column != null)
            {
               m_map.put(column.getName(), getColumnValue(table.getName(), column.getName(), data.get(index), column.getType(), epochDateFormat));
            }
         }
      }
   }

   /**
    * Maps the text representation of column data to Java types.
    *
    * @param table table name
    * @param column column name
    * @param data text representation of column data
    * @param type column data type
    * @param epochDateFormat true if date is represented as an offset from an epoch
    * @return Java representation of column data
    */
   private Object getColumnValue(String table, String column, String data, int type, boolean epochDateFormat) throws MPXJException
   {
      try
      {
         Object value;

         switch (type)
         {
            case Types.BIT:
            {
               value = DatatypeConverter.parseBoolean(data);
               break;
            }

            case Types.VARCHAR:
            case Types.LONGVARCHAR:
            {
               value = DatatypeConverter.parseString(data);
               break;
            }

            case Types.TIME:
            {
               value = DatatypeConverter.parseBasicTime(data);
               break;
            }

            case Types.TIMESTAMP:
            {
               if (epochDateFormat)
               {
                  value = DatatypeConverter.parseEpochTimestamp(data);
               }
               else
               {
                  value = DatatypeConverter.parseBasicTimestamp(data);
               }
               break;
            }

            case Types.DOUBLE:
            {
               value = DatatypeConverter.parseDouble(data);
               break;
            }

            case Types.INTEGER:
            {
               value = DatatypeConverter.parseInteger(data);
               break;
            }

            default:
            {
               throw new IllegalArgumentException("Unsupported SQL type: " + type);
            }
         }

         return value;
      }

      catch (Exception ex)
      {
         throw new MPXJException("Failed to parse " + table + "." + column + " (data=" + data + ", type=" + type + ")", ex);
      }
   }
}
