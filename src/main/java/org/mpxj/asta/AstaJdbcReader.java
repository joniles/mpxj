/*
 * file:       AstaJdbcReader.java
 * author:     Jon Iles
 * date:       2011-04-07
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

import java.sql.Connection;

import javax.sql.DataSource;

/**
 * This class provides a generic front end to read project data from
 * a database.
 */
public final class AstaJdbcReader extends AbstractAstaDatabaseReader
{
   /**
    * Constructor.
    */
   public AstaJdbcReader()
   {
      super(new JdbcDataProvider());
   }

   /**
    * Set the data source. A DataSource or a Connection can be supplied
    * to this class to allow connection to the database.
    *
    * @param dataSource data source
    */
   public void setDataSource(DataSource dataSource)
   {
      ((JdbcDataProvider)m_data).setDataSource(dataSource);
   }

   /**
    * Sets the connection. A DataSource or a Connection can be supplied
    * to this class to allow connection to the database.
    *
    * @param connection database connection
    */
   public void setConnection(Connection connection)
   {
      ((JdbcDataProvider)m_data).setConnection(connection);
   }
}