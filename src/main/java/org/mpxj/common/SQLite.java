/*
 * file:       SQLite.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2021
 * date:       2021-11-24
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

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.mpxj.ikvm.OperatingSystem;

/**
 * Utility methods used to manage SQLite database connections.
 */
public final class SQLite
{
   /**
    * Returns a `Properties` instance with date format used by SQLite databases.
    *
    * @return Properties instance
    */
   public static Properties dateFormatProperties()
   {
      Properties props = new Properties();
      props.setProperty("date_string_format", "yyyy-MM-dd HH:mm:ss");
      return props;
   }

   /**
    * Create a connection to a SQLite database.
    *
    * @param file SQLite database file
    * @return Connection instance
    */
   public static Connection createConnection(File file) throws SQLException
   {
      return createConnection(file, new Properties());
   }

   /**
    * Create a connection to a SQLite database, allowing properties to be passed.
    *
    * @param file SQLite database file
    * @param props Properties instance
    * @return Connection instance
    */
   public static Connection createConnection(File file, Properties props) throws SQLException
   {
      // Handle issue with IKVM .Net Core on OSX and Linux
      // where the OS is not identified by the os.name property
      // and thus the SQLite native library can't be loaded.
      OperatingSystem os = new OperatingSystem();

      try
      {
         os.configure();
         String url = "jdbc:sqlite:" + file.getAbsolutePath();
         return org.sqlite.JDBC.createConnection(url, props);
      }

      finally
      {
         os.restore();
      }
   }
}
