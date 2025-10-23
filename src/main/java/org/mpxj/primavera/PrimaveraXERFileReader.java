/*
 * file:       PrimaveraXERFileReader.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2010
 * date:       25/03/2010
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

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mpxj.FieldType;
import org.mpxj.HasCharset;
import org.mpxj.MPXJException;
import org.mpxj.Notes;
import org.mpxj.ProjectFile;
import org.mpxj.ProjectContext;
import org.mpxj.Relation;
import org.mpxj.Task;
import org.mpxj.WorkContour;
import org.mpxj.WorkContourContainer;
import org.mpxj.common.CharsetHelper;
import org.mpxj.common.NumberHelper;
import org.mpxj.reader.AbstractProjectStreamReader;

/**
 * This class creates a new ProjectFile instance by reading a Primavera XER file.
 */
public final class PrimaveraXERFileReader extends AbstractProjectStreamReader implements HasCharset
{
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
    * Set the Charset used to read the file.
    *
    * @param charset Charset used when reading the file
    */
   @Override public void setCharset(Charset charset)
   {
      m_charset = charset;
   }

   /**
    * Retrieve the Charset used to read the file.
    *
    * @return Charset instance
    */
   @Override public Charset getCharset()
   {
      return m_charset;
   }

   /**
    * Retrieve a flag indicating if, when using `realAll` to retrieve all
    * projects from a file, cross project relations should be linked together.
    *
    * @return true if cross project relations should be linked
    */
   public boolean getLinkCrossProjectRelations()
   {
      return m_linkCrossProjectRelations;
   }

   /**
    * Sets a flag indicating if, when using `realAll` to retrieve all
    * projects from a file, cross project relations should be linked together.
    *
    * @param linkCrossProjectRelations true if cross project relations should be linked
    */
   public void setLinkCrossProjectRelations(boolean linkCrossProjectRelations)
   {
      m_linkCrossProjectRelations = linkCrossProjectRelations;
   }

   @Override public ProjectFile read(InputStream is) throws MPXJException
   {
      ProjectFile project = null;

      // Preserve the requested project ID, this member variable is used when reading all projects
      Integer targetProjectID = m_projectID;

      // Using readAll ensures that cross project relations can be included if required
      List<ProjectFile> projects = readAll(is);

      if (!projects.isEmpty())
      {
         if (targetProjectID == null)
         {
            // We haven't been asked for a specific project: the first one will be the exported project
            project = projects.get(0);
         }
         else
         {
            // We have been asked for a specific project: find it
            project = projects.stream().filter(p -> targetProjectID.equals(p.getProjectProperties().getUniqueID())).findFirst().orElse(null);
         }
      }

      return project;
   }

   /**
    * This is a convenience method which allows all projects in an
    * XER file to be read in a single pass. External relationships
    * are not linked.
    *
    * @param is input stream
    * @return list of ProjectFile instances
    */
   @Override public List<ProjectFile> readAll(InputStream is) throws MPXJException
   {
      try
      {
         m_populateContext = true;

         m_file = new XerFile(READ_REQUIRED_TABLES, m_charset, m_ignoreErrors).read(is);
         ProjectContext context = new PrimaveraXERContextReader(m_file).read();

         List<Row> rows = m_file.getRows("project", null, null);
         List<ProjectFile> result = new ArrayList<>(rows.size());
         List<ExternalRelation> externalRelations = new ArrayList<>();

         for (Row row : rows)
         {
            setProjectID(row.getInt("proj_id"));
            m_reader = new PrimaveraReader(context, m_resourceFields, m_roleFields, m_wbsFields, m_taskFields, m_assignmentFields, m_matchPrimaveraWBS, m_wbsIsFullPath, m_ignoreErrors);
            ProjectFile project = readProject();
            externalRelations.addAll(m_reader.getExternalRelations());

            result.add(project);
         }

         //
         // We've used Primavera's unique ID values for the calendars we've read so far.
         // At this point any new calendars we create must be auto numbered. We also need to
         // ensure that the auto numbering starts from an appropriate value.
         //
         context.getProjectConfig().setAutoCalendarUniqueID(true);

         // Sort to ensure exported project is first
         result.sort((o1, o2) -> Boolean.compare(o2.getProjectProperties().getExportFlag(), o1.getProjectProperties().getExportFlag()));

         if (m_linkCrossProjectRelations)
         {
            for (ExternalRelation externalRelation : externalRelations)
            {
               Task predecessorTask;
               // we could aggregate the project task id maps but that's likely more work
               // than just looping through the projects
               for (ProjectFile proj : result)
               {
                  predecessorTask = proj.getTaskByUniqueID(externalRelation.externalTaskUniqueID());
                  if (predecessorTask != null)
                  {
                     Task successorTask = externalRelation.getTargetTask();

                     // We need to ensure that the relation is present in both
                     // projects so that predecessors and successors are populated
                     // in both projects.

                     ProjectFile successorProject = successorTask.getParentFile();
                     successorProject.getRelations().addPredecessor(new Relation.Builder()
                        .predecessorTask(predecessorTask)
                        .successorTask(successorTask)
                        .type(externalRelation.getType())
                        .lag(externalRelation.getLag())
                        .uniqueID(externalRelation.getUniqueID())
                        .notes(externalRelation.getNotes()));

                     ProjectFile predecessorProject = predecessorTask.getParentFile();
                     predecessorProject.getRelations().addPredecessor(new Relation.Builder()
                        .predecessorTask(predecessorTask)
                        .successorTask(successorTask)
                        .type(externalRelation.getType())
                        .lag(externalRelation.getLag())
                        .uniqueID(externalRelation.getUniqueID())
                        .notes(externalRelation.getNotes()));

                     break;
                  }
               }
               // if predecessorTask not found the external task is outside of the file so ignore
            }
         }

         return result;
      }

      finally
      {
         m_reader = null;
      }
   }

   /**
    * Common project read functionality.
    *
    * @return ProjectFile instance
    */
   private ProjectFile readProject()
   {
      ProjectFile project = m_reader.getProject();
      project.getProjectProperties().setFileApplication("Primavera");
      project.getProjectProperties().setFileType("XER");
      addListenersToProject(project);
      processProjectID();
      project.getProjectProperties().setUniqueID(m_projectID);

      processActivityCodeAssignments();
      processResourceCodeAssignments();
      processRoleCodeAssignments();
      processResourceAssignmentCodeAssignments();
      processUdfValues();
      processCalendars();
      processResources();
      processRoles();
      processRoleAssignments();
      processResourceRates();
      processRoleRates();

      processProjectProperties();
      processTasks();
      processPredecessors();
      processAssignments();
      processExpenseItems();
      processActivitySteps();

      m_reader.rollupValues();
      project.updateStructure();
      project.readComplete();

      return project;
   }

   /**
    * If the user has not specified a project ID, this method
    * retrieves the ID of the first project in the file.
    */
   private void processProjectID()
   {
      if (m_projectID == null)
      {
         List<Row> rows = m_file.getRows("project", null, null);
         if (!rows.isEmpty())
         {
            Row row = rows.get(0);
            m_projectID = row.getInteger("proj_id");
         }
      }
   }

   /**
    * Populates a Map instance representing the IDs and names of
    * projects available in the current file.
    *
    * @param is input stream used to read XER file
    * @return Map instance containing ID and name pairs
    */
   public Map<Integer, String> listProjects(InputStream is) throws MPXJException
   {
      XerFile file = new XerFile(LIST_REQUIRED_TABLES, m_charset, m_ignoreErrors).read(is);

      Map<Integer, String> result = new HashMap<>();

      List<Row> rows = file.getRows("project", null, null);
      for (Row row : rows)
      {
         Integer id = row.getInteger("proj_id");
         String name = row.getString("proj_short_name");
         result.put(id, name);
      }

      return result;
   }

   /**
    * Process project properties.
    */
   private void processProjectProperties()
   {
      //
      // Process common attributes
      //
      List<Row> rows = m_file.getRows("project", "proj_id", m_projectID);
      m_reader.processProjectProperties(rows);

      rows = m_file.getRows("projpcat", "proj_id", m_projectID);
      m_reader.processProjectCodeAssignments(rows);

      //
      // Process XER-specific attributes
      //
      if (m_file.getDefaultCurrencyData() != null)
      {
         m_reader.processDefaultCurrency(m_file.getDefaultCurrencyData());
      }

      processScheduleOptions();
   }

   /**
    * Process expense items.
    */
   private void processExpenseItems()
   {
      m_reader.processExpenseItems(m_file.getRows("projcost", "proj_id", m_projectID));
   }

   /**
    * Process activity steps.
    */
   private void processActivitySteps()
   {
      m_reader.processActivitySteps(m_file.getRows("taskproc", "proj_id", m_projectID));
   }

   /**
    * Process activity code assignments.
    */
   private void processActivityCodeAssignments()
   {
      List<Row> assignments = m_file.getRows("taskactv", null, null);
      m_reader.processActivityCodeAssignments(assignments);
   }

   /**
    * Process resource code assignments.
    */
   private void processResourceCodeAssignments()
   {
      List<Row> assignments = m_file.getRows("rsrcrcat", null, null);
      m_reader.processResourceCodeAssignments(assignments);
   }

   /**
    * Process role code assignments.
    */
   private void processRoleCodeAssignments()
   {
      List<Row> assignments = m_file.getRows("rolercat", null, null);
      m_reader.processRoleCodeAssignments(assignments);
   }

   /**
    * Process resource assignment code assignments.
    */
   private void processResourceAssignmentCodeAssignments()
   {
      List<Row> assignments = m_file.getRows("asgnmntacat", null, null);
      m_reader.processResourceAssignmentCodeAssignments(assignments);
   }

   /**
    * Process schedule options from SCHEDOPTIONS.
    * This is represented as the PROJPROP table in a P6 database.
    */
   private void processScheduleOptions()
   {
      List<Row> rows = m_file.getRows("schedoptions", "proj_id", m_projectID);
      if (!rows.isEmpty())
      {
         m_reader.processScheduleOptions(rows.get(0));
      }
   }


   /**
    * Process user defined field values.
    */
   private void processUdfValues()
   {
      List<Row> values = m_file.getRows("udfvalue", null, null);
      m_reader.processUdfValues(values);
   }

   /**
    * Process project calendars.
    */
   private void processCalendars()
   {
      List<Row> rows = m_file.getRows("calendar", null, null);
      m_reader.processCalendars(rows);
   }

   /**
    * Process resources.
    */
   private void processResources()
   {
      List<Row> rows = m_file.getRows("rsrc", null, null);
      m_reader.processResources(rows);
   }

   /**
    * Process roles.
    */
   private void processRoles()
   {
      List<Row> rows = m_file.getRows("roles", null, null);
      m_reader.processRoles(rows);
   }

   /**
    * Process role assignments.
    */
   private void processRoleAssignments()
   {
      List<Row> rows = m_file.getRows("rsrcrole", null, null);
      m_reader.processRoleAssignments(rows);
   }

   /**
    * Process resource rates.
    */
   private void processResourceRates()
   {
      List<Row> rows = m_file.getRows("rsrcrate", null, null);
      m_reader.processResourceRates(rows);
   }

   /**
    * Process role rates.
    */
   private void processRoleRates()
   {
      List<Row> rows = m_file.getRows("rolerate", null, null);
      m_reader.processRoleRates(rows);
      m_reader.processRoleAvailability(rows);
   }

   /**
    * Process tasks.
    */
   private void processTasks()
   {
      List<Row> wbs = m_file.getRows("projwbs", "proj_id", m_projectID);
      List<Row> tasks = m_file.getRows("task", "proj_id", m_projectID);
      Map<Integer, Notes> wbsNotes = m_reader.getNotes(m_file.getRows("wbsmemo", "proj_id", m_projectID), "wbs_memo_id", "wbs_id", "wbs_memo");
      Map<Integer, Notes> taskNotes = m_reader.getNotes(m_file.getRows("taskmemo", "proj_id", m_projectID), "memo_id", "task_id", "task_memo");

      wbs.sort(WBS_ROW_COMPARATOR);
      m_reader.processTasks(wbs, tasks, wbsNotes, taskNotes);
   }

   /**
    * Process predecessors.
    */
   private void processPredecessors()
   {
      List<Row> rows = m_file.getRows("taskpred", "proj_id", m_projectID);
      m_reader.processPredecessors(rows);
   }

   /**
    * Process resource assignments.
    */
   private void processAssignments()
   {
      List<Row> rows = m_file.getRows("taskrsrc", "proj_id", m_projectID);
      m_reader.processAssignments(rows);
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

   private Charset m_charset = CharsetHelper.CP1252;
   private XerFile m_file;
   private PrimaveraReader m_reader;
   private Integer m_projectID;
   private final Map<FieldType, String> m_resourceFields = PrimaveraReader.getDefaultResourceFieldMap();
   private final Map<FieldType, String> m_roleFields = PrimaveraReader.getDefaultRoleFieldMap();
   private final Map<FieldType, String> m_wbsFields = PrimaveraReader.getDefaultWbsFieldMap();
   private final Map<FieldType, String> m_taskFields = PrimaveraReader.getDefaultTaskFieldMap();
   private final Map<FieldType, String> m_assignmentFields = PrimaveraReader.getDefaultAssignmentFieldMap();
   private boolean m_matchPrimaveraWBS = true;
   private boolean m_wbsIsFullPath = true;
   private boolean m_linkCrossProjectRelations;
   private boolean m_ignoreErrors = true;
   private boolean m_populateContext;

   private static final Set<String> LIST_REQUIRED_TABLES = new HashSet<>();
   static
   {
      LIST_REQUIRED_TABLES.add("project");
   }

   private static final Set<String> READ_REQUIRED_TABLES = new HashSet<>();
   static
   {
      READ_REQUIRED_TABLES.add("project");
      READ_REQUIRED_TABLES.add("calendar");
      READ_REQUIRED_TABLES.add("rsrc");
      READ_REQUIRED_TABLES.add("rsrcrate");
      READ_REQUIRED_TABLES.add("projwbs");
      READ_REQUIRED_TABLES.add("task");
      READ_REQUIRED_TABLES.add("taskpred");
      READ_REQUIRED_TABLES.add("taskrsrc");
      READ_REQUIRED_TABLES.add("currtype");
      READ_REQUIRED_TABLES.add("udftype");
      READ_REQUIRED_TABLES.add("udfvalue");
      READ_REQUIRED_TABLES.add("schedoptions");
      READ_REQUIRED_TABLES.add("actvtype");
      READ_REQUIRED_TABLES.add("actvcode");
      READ_REQUIRED_TABLES.add("taskactv");
      READ_REQUIRED_TABLES.add("costtype");
      READ_REQUIRED_TABLES.add("account");
      READ_REQUIRED_TABLES.add("projcost");
      READ_REQUIRED_TABLES.add("memotype");
      READ_REQUIRED_TABLES.add("wbsmemo");
      READ_REQUIRED_TABLES.add("taskmemo");
      READ_REQUIRED_TABLES.add("roles");
      READ_REQUIRED_TABLES.add("rolerate");
      READ_REQUIRED_TABLES.add("rsrccurvdata");
      READ_REQUIRED_TABLES.add("taskproc");
      READ_REQUIRED_TABLES.add("location");
      READ_REQUIRED_TABLES.add("umeasure");
      READ_REQUIRED_TABLES.add("shift");
      READ_REQUIRED_TABLES.add("shiftper");
      READ_REQUIRED_TABLES.add("rsrcrole");
      READ_REQUIRED_TABLES.add("pcattype");
      READ_REQUIRED_TABLES.add("pcatval");
      READ_REQUIRED_TABLES.add("projpcat");
      READ_REQUIRED_TABLES.add("rcattype");
      READ_REQUIRED_TABLES.add("rcatval");
      READ_REQUIRED_TABLES.add("rsrcrcat");
      READ_REQUIRED_TABLES.add("rolecattype");
      READ_REQUIRED_TABLES.add("rolecatval");
      READ_REQUIRED_TABLES.add("rolercat");
      READ_REQUIRED_TABLES.add("asgnmntcattype");
      READ_REQUIRED_TABLES.add("asgnmntcatval");
      READ_REQUIRED_TABLES.add("asgnmntacat");
   }

   private static final WbsRowComparatorXER WBS_ROW_COMPARATOR = new WbsRowComparatorXER();
}
