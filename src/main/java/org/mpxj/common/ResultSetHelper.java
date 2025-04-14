/*
 * file:       ResultSetHelper.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2022
 * date:       12/01/2022
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

package org.mpxj.common;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Helper methods relating to ResultSet instances.
 */
public final class ResultSetHelper
{
   /**
    * Retrieves basic metadata from a ResultSet instance.
    *
    * @param rs ResultSet instance
    * @return map containing column names and types
    */
   public static Map<String, Integer> populateMetaData(ResultSet rs) throws SQLException
   {
      Map<String, Integer> map = new HashMap<>();
      ResultSetMetaData meta = rs.getMetaData();
      int columnCount = meta.getColumnCount() + 1;
      for (int loop = 1; loop < columnCount; loop++)
      {
         String name = meta.getColumnName(loop);
         Integer type = Integer.valueOf(meta.getColumnType(loop));
         map.put(name, type);
      }
      return map;
   }
}
