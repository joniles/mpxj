/*
 * file:       ConnectionHelper.java
 * author:     Jon Iles
 * date:       16/06/2023
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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

/**
 * Common code for working with database connections.
 */
public final class ConnectionHelper
{
   /**
    * Retrieve the set of table names available via the supplied database connection.
    *
    * @param connection database connection
    * @return set of table names
    */
   public static Set<String> getTableNames(Connection connection) throws SQLException
   {
      Set<String> tables = new HashSet<>();
      DatabaseMetaData dmd = connection.getMetaData();
      try (ResultSet rs = dmd.getTables(null, null, null, null))
      {
         while (rs.next())
         {
            tables.add(rs.getString("TABLE_NAME").toUpperCase());
         }
      }
      return tables;
   }
}
