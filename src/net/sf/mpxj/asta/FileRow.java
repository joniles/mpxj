/*
 * file:       FileRow.java
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

package net.sf.mpxj.asta;

import java.sql.Types;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import net.sf.mpxj.MPXJException;

/**
 * Extends the MapRow class to allow it to manage data read from an Asta file.
 */
class FileRow extends MapRow
{
   /**
    * Constructor.
    * 
    * @param table table definition
    * @param data table data
    * @throws MPXJException
    */
   public FileRow(TableDefinition table, List<String> data)
      throws MPXJException
   {
      super(new HashMap<String, Object>());

      ColumnDefinition[] columns = table.getColumns();
      for (int index = 0; index < columns.length; index++)
      {
         ColumnDefinition column = columns[index];
         if (index < data.size())
         {
            m_map.put(column.getName(), getColumnValue(table.getName(), column.getName(), data.get(index), column.getType()));
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
    * @return Java representation of column data
    * @throws MPXJException
    */
   private Object getColumnValue(String table, String column, String data, int type) throws MPXJException
   {
      try
      {
         Object value = null;

         switch (type)
         {
            case Types.BIT:
            {
               value = parseBoolean(data);
               break;
            }

            case Types.VARCHAR:
            case Types.LONGVARCHAR:
            {
               value = parseString(data);
               break;
            }

            case Types.TIMESTAMP:
            {
               value = parseTimestamp(data);
               break;
            }

            case Types.DOUBLE:
            {
               value = parseDouble(data);
               break;
            }

            case Types.INTEGER:
            {
               value = parseInteger(data);
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
         throw new MPXJException("Failed to parse " + table + "." + column + " (data=" + data + ", type=" + type + ")");
      }
   }

   /**
    * Parse a string representation of a Boolean value.
    * 
    * @param value string representation
    * @return Boolean value
    */
   private Boolean parseBoolean(String value) throws ParseException
   {
      Boolean result = null;
      Integer number = parseInteger(value);
      if (number != null)
      {
         result = number.intValue() == 0 ? Boolean.FALSE : Boolean.TRUE;
      }

      return result;
   }

   /**
    * Parse a string representation of an Integer value.
    * 
    * @param value string representation
    * @return Integer value
    */
   private Integer parseInteger(String value) throws ParseException
   {
      Integer result = null;

      if (value.length() > 0 && value.indexOf(' ') == -1)
      {
         if (value.indexOf('.') == -1)
         {
            result = Integer.valueOf(value);
         }
         else
         {
            Number n = parseDouble(value);
            result = Integer.valueOf(n.intValue());
         }
      }

      return result;
   }

   /**
    * Parse a string.
    * 
    * @param value string representation
    * @return String value
    */
   private String parseString(String value)
   {
      if (value != null)
      {
         // Strip angle brackets if present
         if (!value.isEmpty() && value.charAt(0) == '<')
         {
            value = value.substring(1, value.length() - 1);
         }

         // Strip quotes if present
         if (!value.isEmpty() && value.charAt(0) == '"')
         {
            value = value.substring(1, value.length() - 1);
         }
      }
      return value;
   }

   /**
    * Parse the string representation of a double.
    * 
    * @param value string representation
    * @return Java representation
    * @throws ParseException
    */
   private Number parseDouble(String value) throws ParseException
   {

      Number result = null;
      value = parseString(value);

      // If we still have a value
      if (value != null && !value.isEmpty() && !value.equals("-1 -1"))
      {
         int index = value.indexOf("E+");
         if (index != -1)
         {
            value = value.substring(0, index) + 'E' + value.substring(index + 2, value.length());
         }

         if (value.indexOf('E') != -1)
         {
            DecimalFormat df = DOUBLE_FORMAT.get();
            if (df == null)
            {
               df = new DecimalFormat("#.#E0");
               DOUBLE_FORMAT.set(df);
            }

            result = df.parse(value);
         }
         else
         {
            result = Double.valueOf(value);
         }
      }

      return result;
   }

   /**
    * Parse the string representation of a timestamp.
    * 
    * @param value string representation
    * @return Java representation
    */
   private Date parseTimestamp(String value)
   {
      Date result = null;

      if (value.length() > 0)
      {
         if (!value.equals("-1 -1"))
         {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(JAVA_EPOCH);

            int index = value.indexOf(' ');
            if (index == -1)
            {
               if (value.length() < 6)
               {
                  value = "000000" + value;
                  value = value.substring(value.length() - 6);
               }

               int hours = Integer.parseInt(value.substring(0, 2));
               int minutes = Integer.parseInt(value.substring(2, 4));
               int seconds = Integer.parseInt(value.substring(4));

               cal.set(Calendar.HOUR, hours);
               cal.set(Calendar.MINUTE, minutes);
               cal.set(Calendar.SECOND, seconds);
            }
            else
            {
               long astaDays = Long.parseLong(value.substring(0, index));
               int astaSeconds = Integer.parseInt(value.substring(index + 1));

               cal.add(Calendar.DAY_OF_YEAR, (int) (astaDays - ASTA_EPOCH));
               cal.set(Calendar.MILLISECOND, 0);
               cal.set(Calendar.SECOND, 0);
               cal.set(Calendar.HOUR, 0);
               cal.add(Calendar.SECOND, astaSeconds);
            }

            result = cal.getTime();
         }
      }

      return result;
   }

   private static final ThreadLocal<DecimalFormat> DOUBLE_FORMAT = new ThreadLocal<DecimalFormat>();
   private static final long JAVA_EPOCH = -2208988800000L;
   private static final long ASTA_EPOCH = 2415021L;
}
