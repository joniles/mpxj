/*
 * file:       SqliteResultSetRow.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2016
 * date:       06/06/2016
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

package net.sf.mpxj.asta;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.mpxj.Duration;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.common.NumberHelper;

/**
 * Implementation of the Row interface, wrapping a Map.
 */
final class SqliteResultSetRow extends MapRow
{
   /**
    * Constructor.
    *
    * @param rs result set from which data is drawn
    * @param meta result set meta data
    */
   public SqliteResultSetRow(ResultSet rs, Map<String, Integer> meta)
      throws SQLException
   {
      super(new HashMap<String, Object>());

      for (Entry<String, Integer> entry : meta.entrySet())
      {
         String name = entry.getKey().toUpperCase();
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
            {
               value = rs.getString(name);
               break;
            }

            case Types.DATE:
            {
               String stringValue = rs.getString(name);
               if (stringValue == null || stringValue.isEmpty())
               {
                  value = null;
               }
               else
               {
                  value = rs.getDate(name);
               }
               break;
            }

            case Types.TIMESTAMP:
            {
               Timestamp ts = rs.getTimestamp(name);
               if (ts != null)
               {
                  value = new Date(ts.getTime());
               }
               else
               {
                  value = null;
               }
               break;
            }

            case Types.DOUBLE:
            case Types.NUMERIC:
            case Types.FLOAT:
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

         m_map.put(name, value);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override public Duration getDuration(String name)
   {
      String value = getString(name);
      if (value == null || value.isEmpty())
      {
         throw new IllegalArgumentException("Unexpected duration value");
      }

      String[] items = value.split(",");
      if (items.length != 3)
      {
         throw new IllegalArgumentException("Unexpected duration value: " + value);
      }

      String item = DatatypeConverter.parseString(items[2]);
      Number durationValue;

      try
      {
         durationValue = DatatypeConverter.parseDouble(item);
      }

      catch (ParseException ex)
      {
         throw new IllegalArgumentException("Unexpected duration value", ex);
      }

      return Duration.getInstance(NumberHelper.getDouble(durationValue), TimeUnit.HOURS);
   }

}
