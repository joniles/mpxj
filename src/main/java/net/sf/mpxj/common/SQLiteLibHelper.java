/*
 * file:       SQLiteHelper.java
 * author:     Jon Iles
 * date:       2023-08-23
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

/**
 * Implements a workaround for an issue with the SQLite JDBC driver under IKVM.
 * The changes in
 * <a href="https://github.com/xerial/sqlite-jdbc/commit/6f426839c56f3924be6cad8920d9192400a37d5f">this commit</a>
 * removed a condition which explicitly set the SQLite native library name
 * for Mac. When run with the current version of IKVM, the new version of the SQLite code
 * no longer generates the correct library name. The methods  in this class provide
 * a workaround to explicitly set the native library name when we detect that we're
 * running on a Mac under IKVM.
 */
class SQLiteLibHelper
{
   /**
    * Apply workaround if required.
    */
   public void configure()
   {
      if (JvmHelper.isIkvm() && System.getProperty("os.name").contains("Mac"))
      {
         m_originalLibName = System.getProperty(SQLITE_LIB_NAME_PROPERTY);
         System.setProperty(SQLITE_LIB_NAME_PROPERTY, "libsqlitejdbc.jnilib");
         m_libNameUpdated = true;
      }
   }

   /**
    * Remove workaround if applied.
    */
   public void restore()
   {
      if (m_libNameUpdated)
      {
         if (m_originalLibName == null)
         {
            System.clearProperty(SQLITE_LIB_NAME_PROPERTY);
         }
         else
         {
            System.setProperty(SQLITE_LIB_NAME_PROPERTY, m_originalLibName);
         }
      }
   }

   private String m_originalLibName;
   private boolean m_libNameUpdated;

   private static final String SQLITE_LIB_NAME_PROPERTY = "org.sqlite.lib.name";
}
