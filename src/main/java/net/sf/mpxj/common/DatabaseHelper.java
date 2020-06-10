/*
 * file:       DatabaseHelper
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2020
 * date:       10/067/2020
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

package net.sf.mpxj.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Common helper methods for working with database connections.
 */
public final class DatabaseHelper
{
   /**
    * Close a database connection without raising an exception on error.
    *
    * @param connection connection to close
    */
   public static final void closeQuietly(Connection connection)
   {
      if (connection != null)
      {
         try
         {
            connection.close();
         }

         catch (SQLException ex)
         {
            // silently ignore exceptions
         }
      }
   }
   
   /**
    * Close a result set without raising an exception on error.
    *
    * @param rs result set to close
    */
   public static final void closeQuietly(ResultSet rs)
   {
      if (rs != null)
      {
         try
         {
            rs.close();
         }

         catch (SQLException ex)
         {
            // silently ignore exceptions
         }
      }
   }
   
   /**
    * Close a prepared statement without raising an exception on error.
    *
    * @param ps prepared statement to close
    */
   public static final void closeQuietly(PreparedStatement ps)
   {
      if (ps != null)
      {
         try
         {
            ps.close();
         }

         catch (SQLException ex)
         {
            // silently ignore exceptions
         }
      }
   }
}
