/*
 * file:       PrimaveraDatabaseReader.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2010
 * date:       22/03/2010
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
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.mpxj.EPS;
import org.mpxj.EpsNode;
import org.mpxj.EpsProjectNode;
import org.mpxj.FieldType;
import org.mpxj.MPXJException;
import org.mpxj.ProjectFile;
import org.mpxj.reader.AbstractProjectReader;

/**
 * This class provides a generic front end to read project data from
 * a database.
 */
public final class PrimaveraDatabaseReader extends AbstractProjectReader
{
   /**
    * Populates a Map instance representing the IDs and names of
    * projects available in the current database.
    *
    * @return Map instance containing ID and name pairs
    */
   public Map<Integer, String> listProjects() throws MPXJException
   {
      try
      {
         Map<Integer, String> result = new HashMap<>();

         List<Row> rows = m_database.getRows("select proj_id, proj_short_name from " + m_schema + "project where delete_date is null");
         for (Row row : rows)
         {
            Integer id = row.getInteger("proj_id");
            String name = row.getString("proj_short_name");
            result.put(id, name);
         }

         return result;
      }

      catch (SQLException ex)
      {
         throw new MPXJException(MPXJException.READ_ERROR, ex);
      }
   }

   /**
    * Retrieve an instance of the EPS class, allowing the hierarchy of EpsNode
    * and EpsProjectNodes to be traversed.
    *
    * @return EPS instance
    */
   public EPS listEps() throws MPXJException
   {
      try
      {
         List<Row> rows = m_database.getRows("select project.project_flag, projwbs.* from " + m_schema + "projwbs join " + m_schema + "project on project.proj_id = projwbs.proj_id where proj_node_flag='Y' order by seq_num");
         return processEps(rows);
      }

      catch (SQLException ex)
      {
         throw new MPXJException(MPXJException.READ_ERROR, ex);
      }
   }

   /**
    * Read a project from the current data source.
    *
    * @return ProjectFile instance
    */
   public ProjectFile read() throws MPXJException
   {
      Map<String, Map<Integer, List<Row>>> udfValues = new HashMap<>();
      ClashMap roleClashMap = new ClashMap();
      TableReaderState state = new TableReaderState(m_resourceFields, m_roleFields, m_wbsFields, m_taskFields, m_assignmentFields, m_matchPrimaveraWBS, m_wbsIsFullPath, m_ignoreErrors);
      addListenersToContext(state.getContext());
      new DatabaseContextReader(m_database, m_schema, m_projectID, state).read();

      ProjectFile project = new ProjectFile(state.getContext());
      DatabaseProjectReader reader = new DatabaseProjectReader(m_database, m_schema, project, m_projectID, state);
      reader.read();

      //
      // We've used Primavera's unique ID values for the calendars we've read so far.
      // At this point any new calendars we create must be auto numbered. We also need to
      // ensure that the auto numbering starts from an appropriate value.
      //
      state.getContext().getProjectConfig().setAutoCalendarUniqueID(true);

      return project;
   }

   /**
    * Convenience method which allows all projects in the database to
    * be read in a single operation.
    *
    * @return list of ProjectFile instances
    */
   public List<ProjectFile> readAll() throws MPXJException
   {
      Map<String, Map<Integer, List<Row>>> udfValues = new HashMap<>();
      ClashMap roleClashMap = new ClashMap();
      Map<Integer, String> projects = listProjects();
      List<ProjectFile> result = new ArrayList<>(projects.size());
      TableReaderState state = new TableReaderState(m_resourceFields, m_roleFields, m_wbsFields, m_taskFields, m_assignmentFields, m_matchPrimaveraWBS, m_wbsIsFullPath, m_ignoreErrors);
      addListenersToContext(state.getContext());
      new DatabaseContextReader(m_database, m_schema, null, state).read();

      for (Integer id : projects.keySet())
      {
         ProjectFile project = new ProjectFile(state.getContext());
         DatabaseProjectReader reader = new DatabaseProjectReader(m_database, m_schema, project, id, state);
         reader.read();
         result.add(project);
      }

      //
      // We've used Primavera's unique ID values for the calendars we've read so far.
      // At this point any new calendars we create must be auto numbered. We also need to
      // ensure that the auto numbering starts from an appropriate value.
      //
      state.getContext().getProjectConfig().setAutoCalendarUniqueID(true);

      return result;
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
      m_database = new PrimaveraDatabaseConnection(dataSource);
   }

   /**
    * Sets the connection. A DataSource or a Connection can be supplied
    * to this class to allow connection to the database.
    *
    * @param connection database connection
    */
   public void setConnection(Connection connection)
   {
      m_database = new PrimaveraDatabaseConnection(connection);
   }

   @Override public ProjectFile read(String fileName)
   {
      throw new UnsupportedOperationException();
   }

   @Override public List<ProjectFile> readAll(String fileName)
   {
      throw new UnsupportedOperationException();
   }

   @Override public ProjectFile read(File file)
   {
      throw new UnsupportedOperationException();
   }

   @Override public List<ProjectFile> readAll(File file)
   {
      throw new UnsupportedOperationException();
   }

   @Override public ProjectFile read(InputStream inputStream)
   {
      throw new UnsupportedOperationException();
   }

   @Override public List<ProjectFile> readAll(InputStream inputStream)
   {
      throw new UnsupportedOperationException();
   }

   /**
    * Set the name of the schema containing the Primavera tables.
    *
    * @param schema schema name.
    */
   public void setSchema(String schema)
   {
      if (schema == null)
      {
         schema = "";
      }
      else
      {
         if (!schema.isEmpty() && !schema.endsWith("."))
         {
            schema = schema + '.';
         }
      }
      m_schema = schema;
   }

   /**
    * Retrieve the name of the schema containing the Primavera tables.
    *
    * @return schema name
    */
   public String getSchema()
   {
      return m_schema;
   }

   /**
    * Customise the data retrieved by this reader by modifying the contents of this map.
    *
    * @return Primavera field name to MPXJ field type map
    */
   public Map<FieldType, String> getResourceFieldMap()
   {
      return m_resourceFields;
   }

   /**
    * Customise the data retrieved by this reader by modifying the contents of this map.
    *
    * @return Primavera field name to MPXJ field type map
    */
   public Map<FieldType, String> getRoleFieldMap()
   {
      return m_roleFields;
   }

   /**
    * Customise the data retrieved by this reader by modifying the contents of this map.
    *
    * @return Primavera field name to MPXJ field type map
    */
   public Map<FieldType, String> getWbsFieldMap()
   {
      return m_wbsFields;
   }

   /**
    * Customise the data retrieved by this reader by modifying the contents of this map.
    *
    * @return Primavera field name to MPXJ field type map
    */
   public Map<FieldType, String> getActivityFieldMap()
   {
      return m_taskFields;
   }

   /**
    * Customise the data retrieved by this reader by modifying the contents of this map.
    *
    * @return Primavera field name to MPXJ field type map
    */
   public Map<FieldType, String> getAssignmentFieldMap()
   {
      return m_assignmentFields;
   }

   /**
    * If set to true, the WBS for each task read from Primavera will exactly match the WBS value shown in Primavera.
    * If set to false, each task will be given a unique WBS based on the WBS present in Primavera.
    * Defaults to true.
    *
    * @return flag value
    */
   public boolean getMatchPrimaveraWBS()
   {
      return m_matchPrimaveraWBS;
   }

   /**
    * If set to true, the WBS for each task read from Primavera will exactly match the WBS value shown in Primavera.
    * If set to false, each task will be given a unique WBS based on the WBS present in Primavera.
    * Defaults to true.
    *
    * @param matchPrimaveraWBS flag value
    */
   public void setMatchPrimaveraWBS(boolean matchPrimaveraWBS)
   {
      m_matchPrimaveraWBS = matchPrimaveraWBS;
   }

   /**
    * Returns true if the WBS attribute of a summary task
    * contains a dot separated list representing the WBS hierarchy.
    *
    * @return true if WBS attribute is a hierarchy
    */
   public boolean getWbsIsFullPath()
   {
      return m_wbsIsFullPath;
   }

   /**
    * Sets a flag indicating if the WBS attribute of a summary task
    * contains a dot separated list representing the WBS hierarchy.
    *
    * @param wbsIsFullPath true if WBS attribute is a hierarchy
    */
   public void setWbsIsFullPath(boolean wbsIsFullPath)
   {
      m_wbsIsFullPath = wbsIsFullPath;
   }

   /**
    * Set a flag to determine if datatype parse errors can be ignored.
    * Defaults to true.
    *
    * @param ignoreErrors pass true to ignore errors
    */
   public void setIgnoreErrors(boolean ignoreErrors)
   {
      m_ignoreErrors = ignoreErrors;
   }

   /**
    * Retrieve the flag which determines if datatype parse errors can be ignored.
    * Defaults to true.
    *
    * @return true if datatype parse errors are ignored
    */
   public boolean getIgnoreErrors()
   {
      return m_ignoreErrors;
   }

   /**
    * Create an EPS instance and add nodes to it
    * using the supplied rows.
    *
    * @param rows EPS data
    * @return EPS instance
    */
   private EPS processEps(List<Row> rows)
   {
      EPS eps = new EPS();
      rows.forEach(r -> addEpsNode(eps, r));
      return eps;
   }

   /**
    * Determine if we have a project on an EPS node and add the
    * appropriate node type to the EPS.
    *
    * @param eps EPS instance
    * @param row EPS data
    */
   private void addEpsNode(EPS eps, Row row)
   {
      if (row.getBoolean("project_flag"))
      {
         addEpsProjectNode(eps, row);
      }
      else
      {
         addEpsChildNode(eps, row);
      }
   }

   /**
    * Add an EPS node to the EPS.
    *
    * @param eps EPS instance
    * @param row node data
    */
   private void addEpsChildNode(EPS eps, Row row)
   {
      new EpsNode(eps,
         row.getInteger("wbs_id"),
         row.getInteger("parent_wbs_id"),
         row.getString("wbs_name"),
         row.getString("wbs_short_name"));
   }

   /**
    * Add an EPS project node to the EPS.
    *
    * @param eps EPS instance
    * @param row node data
    */
   private void addEpsProjectNode(EPS eps, Row row)
   {
      new EpsProjectNode(eps,
         row.getInteger("proj_id"),
         row.getInteger("parent_wbs_id"),
         row.getString("wbs_short_name"),
         row.getString("wbs_name"));
   }

   private Integer m_projectID;
   private String m_schema = "";
   private boolean m_matchPrimaveraWBS = true;
   private boolean m_wbsIsFullPath = true;
   private boolean m_ignoreErrors = true;
   private PrimaveraDatabaseConnection m_database;

   private final Map<FieldType, String> m_resourceFields = TableContextReader.getDefaultResourceFieldMap();
   private final Map<FieldType, String> m_roleFields = TableContextReader.getDefaultRoleFieldMap();
   private final Map<FieldType, String> m_wbsFields = TableProjectReader.getDefaultWbsFieldMap();
   private final Map<FieldType, String> m_taskFields = TableProjectReader.getDefaultTaskFieldMap();
   private final Map<FieldType, String> m_assignmentFields = TableProjectReader.getDefaultAssignmentFieldMap();
}