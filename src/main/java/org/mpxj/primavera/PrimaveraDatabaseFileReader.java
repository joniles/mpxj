/*
 * file:       PrimaveraDatabaseFileReader.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2020
 * date:       2020-09-07
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

package org.mpxj.primavera;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.mpxj.EPS;
import org.mpxj.MPXJException;
import org.mpxj.ProjectFile;
import org.mpxj.common.AutoCloseableHelper;
import org.mpxj.common.NumberHelper;
import org.mpxj.common.SQLite;
import org.mpxj.reader.AbstractProjectFileReader;

/**
 * This class provides a generic front end to read project data from
 * a SQLite-based Primavera database.
 */
public final class PrimaveraDatabaseFileReader extends AbstractProjectFileReader
{
   /**
    * Retrieve a map containing details of the projects available in this database.
    *
    * @param file database file
    * @return map of project ids and names
    */
   public Map<Integer, String> listProjects(File file) throws MPXJException
   {
      Connection connection = null;

      try
      {
         connection = getDatabaseConnection(file);
         PrimaveraDatabaseReader reader = new PrimaveraDatabaseReader();
         reader.setConnection(connection);
         return reader.listProjects();
      }

      catch (SQLException ex)
      {
         throw new MPXJException("Failed to create connection", ex);
      }

      finally
      {
         AutoCloseableHelper.closeQuietly(connection);
      }
   }

   /**
    * Retrieve an instance of the EPS class, allowing the hierarchy of EpsNode
    * and EpsProjectNodes to be traversed.
    *
    * @param file database file
    * @return EPS instance
    */
   public EPS listEps(File file) throws MPXJException
   {
      Connection connection = null;

      try
      {
         connection = getDatabaseConnection(file);
         PrimaveraDatabaseReader reader = new PrimaveraDatabaseReader();
         reader.setConnection(connection);
         return reader.listEps();
      }

      catch (SQLException ex)
      {
         throw new MPXJException("Failed to create connection", ex);
      }

      finally
      {
         AutoCloseableHelper.closeQuietly(connection);
      }
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

   @Override public ProjectFile read(File file) throws MPXJException
   {
      Connection connection = null;

      try
      {
         connection = getDatabaseConnection(file);
         PrimaveraDatabaseReader reader = new PrimaveraDatabaseReader();
         reader.setConnection(connection);
         reader.setProjectID(NumberHelper.getInt(m_projectID));
         addListenersToReader(reader);
         return reader.read();
      }

      catch (SQLException ex)
      {
         throw new MPXJException("Failed to create connection", ex);
      }

      finally
      {
         AutoCloseableHelper.closeQuietly(connection);
      }
   }

   @Override public List<ProjectFile> readAll(File file) throws MPXJException
   {
      Connection connection = null;

      try
      {
         connection = getDatabaseConnection(file);
         PrimaveraDatabaseReader reader = new PrimaveraDatabaseReader();
         reader.setConnection(connection);
         addListenersToReader(reader);
         return reader.readAll();
      }

      catch (SQLException ex)
      {
         throw new MPXJException("Failed to create connection", ex);
      }

      finally
      {
         AutoCloseableHelper.closeQuietly(connection);
      }
   }

   /**
    * Open a database connection to the database file.
    *
    * @param file database file
    * @return database connection
    */
   private Connection getDatabaseConnection(File file) throws SQLException
   {
      return SQLite.createConnection(file, SQLite.dateFormatProperties());
   }

   private Integer m_projectID;
}
