/*
 * file:       JdbcOdbcHelper.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2021
 * date:       11/12/2021
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

import java.io.File;

public final class JdbcOdbcHelper
{
   public static boolean jdbcOdbcAvailable()
   {
      return JDBC_ODBC_AVAILABLE;
   }

   public static String getJdbcUrl(File file)
   {
      return getJdbcUrl(file.getAbsolutePath());
   }

   public static String getJdbcUrl(String filename)
   {
      if (!JDBC_ODBC_AVAILABLE)
      {
         throw new RuntimeException("JDBC-ODBC Bridge not available");
      }
      return "jdbc:odbc:DRIVER=Microsoft Access Driver (*.mdb);DBQ=" + filename;
   }

   private static final boolean JDBC_ODBC_AVAILABLE;

   static
   {
      boolean available = false;

      try
      {
         Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
         available = true;
      }

      catch (ClassNotFoundException ex)
      {
      }

      JDBC_ODBC_AVAILABLE = available;
   }
}
