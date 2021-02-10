/*
 * file:       MPDDatabaseReader.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2007
 * date:       02/02/2006
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

package net.sf.mpxj.mpd;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.common.AutoCloseableHelper;
import net.sf.mpxj.reader.AbstractProjectFileReader;

/**
 * This class provides a generic front end to read project data from
 * a database.
 */
public final class MPDDatabaseReader extends AbstractProjectFileReader
{
   /**
    * Populates a Map instance representing the IDs and names of
    * projects available in the current database.
    *
    * @return Map instance containing ID and name pairs
    * @throws MPXJException
    */
   public Map<Integer, String> listProjects() throws MPXJException
   {
      return getReader().listProjects();
   }

   /**
    * Read project data from a database.
    *
    * @return ProjectFile instance
    * @throws MPXJException
    */
   public ProjectFile read() throws MPXJException
   {
      MPD9DatabaseReader reader = getReader();
      reader.setProjectID(m_projectID);
      return reader.read();
   }

   /**
    * Set the ID of the project to be read.
    *
    * @param projectID project ID
    */
   public void setProjectID(int projectID)
   {
      m_projectID = Integer.valueOf(projectID);
   }

   /**
    * Set the data source. A DataSource or a Connection can be supplied
    * to this class to allow connection to the database.
    *
    * @param dataSource data source
    */
   public void setDataSource(DataSource dataSource)
   {
      m_dataSource = dataSource;
   }

   /**
    * Sets the connection. A DataSource or a Connection can be supplied
    * to this class to allow connection to the database.
    *
    * @param connection database connection
    */
   public void setConnection(Connection connection)
   {
      m_connection = connection;
   }

   /**
    * {@inheritDoc}
    */
   @Override public ProjectFile read(File file) throws MPXJException
   {
      try
      {
         m_connection = getDatabaseConnection(file);
         m_projectID = Integer.valueOf(1);
         return read();
      }

      finally
      {
         AutoCloseableHelper.closeQuietly(m_connection);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override public List<ProjectFile> readAll(File file) throws MPXJException
   {
      try
      {
         m_connection = getDatabaseConnection(file);
         List<ProjectFile> result = new ArrayList<>();
         Set<Integer> ids = listProjects().keySet();
         for (Integer id : ids)
         {
            m_projectID = id;
            result.add(read());
         }
         return result;
      }

      finally
      {
         AutoCloseableHelper.closeQuietly(m_connection);
      }
   }

   /**
    * Create and configure a JDBC/ODBC bridge connection.
    *
    * @param file database file to open
    * @return database connection
    */
   private Connection getDatabaseConnection(File file) throws MPXJException
   {
      try
      {
         Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
         String url = "jdbc:odbc:DRIVER=Microsoft Access Driver (*.mdb);DBQ=" + file.getAbsolutePath();
         return DriverManager.getConnection(url);
      }

      catch (ClassNotFoundException ex)
      {
         throw new MPXJException("Failed to load JDBC driver", ex);
      }

      catch (SQLException ex)
      {
         throw new MPXJException("Failed to create connection", ex);
      }
   }

   /**
    * Create, configure, and return an MPD9DatabaseReader instance.
    *
    * @return MPD9DatabaseReader instance
    */
   private MPD9DatabaseReader getReader()
   {
      MPD9DatabaseReader reader = new MPD9DatabaseReader();
      reader.setDataSource(m_dataSource);
      reader.setConnection(m_connection);
      return reader;
   }

   private Integer m_projectID;
   private DataSource m_dataSource;
   private Connection m_connection;
}