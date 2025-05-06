/*
 * file:       JackcessResultSetRow.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2022
 * date:       07/07/2022
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.healthmarketscience.jackcess.Column;
import com.healthmarketscience.jackcess.DataType;
import org.mpxj.common.NumberHelper;

/**
 * Implementation of the Row interface, wrapping a Map.
 */
final class JackcessResultSetRow extends MapRow
{
   /**
    * Constructor.
    *
    * @param nameMap column name map
    * @param row row from which data is drawn
    * @param columns column meta data
    */
   public JackcessResultSetRow(Map<String, String> nameMap, com.healthmarketscience.jackcess.Row row, List<? extends Column> columns)
   {
      super(new HashMap<>());

      for (Column column : columns)
      {
         String name = column.getName().toUpperCase();
         DataType type = column.getType();
         Object value;

         switch (type)
         {
            case BOOLEAN:
            {
               value = row.getBoolean(name);
               break;
            }

            case MEMO:
            case TEXT:
            case GUID:
            {
               value = row.getString(name);
               break;
            }

            case EXT_DATE_TIME:
            case SHORT_DATE_TIME:
            {
               value = row.getLocalDateTime(name);
               break;
            }

            case DOUBLE:
            case FLOAT:
            case MONEY:
            case NUMERIC:
            {
               value = row.getDouble(name);
               break;
            }

            case BIG_INT:
            case BYTE:
            case LONG:
            {
               value = row.getInt(name);
               break;
            }

            case INT:
            {
               value = NumberHelper.getInteger(row.getShort(name));
               break;
            }

            case OLE:
            case BINARY:
            {
               value = row.getBytes(name);
               break;
            }

            default:
            {
               throw new IllegalArgumentException("Unsupported type: " + type + " for column " + name);
            }
         }

         m_map.put(nameMap.getOrDefault(name, name), value);
      }
   }
}
