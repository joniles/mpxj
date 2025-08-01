/*
 * file:       ResultSetRow.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2010
 * date:       22/03/2010
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

package org.mpxj.primavera;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

import java.util.Map;
import java.util.Map.Entry;

import org.mpxj.common.NumberHelper;

/**
 * Implementation of the Row interface, wrapping a Map.
 */
final class ResultSetRow extends ArrayRow
{
   /**
    * Constructor.
    *
    * @param rs result set from which data is drawn
    * @param meta result set meta data
    * @param index field index
    */
   public ResultSetRow(ResultSet rs, Map<String, Integer> meta, Map<String, Integer> index)
      throws SQLException
   {
      super(index, populateArray(rs, meta, index), false);
   }

   private static Object[] populateArray(ResultSet rs, Map<String, Integer> meta, Map<String, Integer> index) throws SQLException
   {
      Object[] array = new Object[meta.size()];

      for (Entry<String, Integer> entry : meta.entrySet())
      {
         String name = entry.getKey().toLowerCase();
         int type = (entry.getValue()).intValue();
         Object value;

         switch (type)
         {
            case Types.BIT:
            case Types.BOOLEAN:
            {
               value = Boolean.valueOf(rs.getBoolean(name));
               break;
            }

            case Types.CHAR:
            case Types.VARCHAR:
            case Types.LONGVARCHAR:
            case Types.CLOB:
            case Types.NVARCHAR:
            case Types.LONGNVARCHAR:
            {
               value = stripTrailingNul(rs.getString(name));
               break;
            }

            case Types.DATE:
            case Types.TIMESTAMP:
            case SQL_SERVER_TIMESTAMP:
            {
               Timestamp ts = rs.getTimestamp(name);
               if (ts != null)
               {
                  value = ts.toLocalDateTime();
               }
               else
               {
                  value = null;
               }
               break;
            }

            case Types.REAL:
            case Types.DECIMAL:
            case Types.DOUBLE:
            case Types.NUMERIC:
            {
               value = NumberHelper.getDouble(rs.getDouble(name));
               break;
            }

            case Types.INTEGER:
            case Types.SMALLINT:
            {
               value = Integer.valueOf(rs.getInt(name));
               break;
            }

            case Types.BIGINT:
            {
               value = Long.valueOf(rs.getLong(name));
               break;
            }

            case Types.VARBINARY:
            case Types.LONGVARBINARY:
            case Types.BLOB:
            {
               value = rs.getBytes(name);
               break;
            }

            case Types.OTHER:
            {
               value = rs.getObject(name);
               break;
            }

            default:
            {
               throw new IllegalArgumentException("Unsupported SQL type: " + type + " for column " + name);
            }
         }

         if (rs.wasNull())
         {
            value = null;
         }

         array[index.get(name).intValue()] = value;
      }

      return array;
   }

   /**
    * When reading some text fields (typically note fields which are held as HTML)
    * the field value will include a trailing ASCII NUL. This causes issues downstream
    * as it will generate XML 1.0 which parses will reject.
    *
    * @param value string value
    * @return string value with trailing ASCII NUL stripped
    */
   private static String stripTrailingNul(String value)
   {
      String result = value;
      while (result != null && !result.isEmpty() && result.charAt(result.length() - 1) == 0)
      {
         result = result.substring(0, result.length() - 1);
      }
      return result;
   }

   // https://stackoverflow.com/questions/45377247/microsoft-sql-jdbc-driver-v6-2-returning-incorrect-sql-type-code-for-datetime-fi
   private static final int SQL_SERVER_TIMESTAMP = -151;
}
