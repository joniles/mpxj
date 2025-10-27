package org.mpxj.primavera;

import java.util.List;
import java.util.Map;

import org.mpxj.FieldType;
import org.mpxj.Notes;
import org.mpxj.ProjectFile;

class PrimaveraXERProjectReader extends PrimaveraProjectReader
{
   public PrimaveraXERProjectReader(XerFile file, ProjectFile project, Integer projectID, Map<FieldType, String> resourceFields, Map<FieldType, String> roleFields, Map<FieldType, String> wbsFields, Map<FieldType, String> taskFields, Map<FieldType, String> assignmentFields, boolean matchPrimaveraWBS, boolean wbsIsFullPath, boolean ignoreErrors)
   {
      m_file = file;
      m_project = project;
      m_projectID = projectID;

      m_resourceFields = resourceFields;
      m_roleFields = roleFields;
      m_wbsFields = wbsFields;
      m_taskFields = taskFields;
      m_assignmentFields = assignmentFields;
      m_matchPrimaveraWBS = matchPrimaveraWBS;
      m_wbsIsFullPath = wbsIsFullPath;
      m_ignoreErrors = ignoreErrors;

      m_eventManager = m_project.getEventManager();
   }

   public ProjectFile read()
   {
      m_project.getProjectProperties().setFileApplication("Primavera");
      m_project.getProjectProperties().setFileType("XER");
      m_project.getProjectProperties().setUniqueID(m_projectID);

      processActivityCodeAssignments();
      processResourceCodeAssignments();
      processRoleCodeAssignments();
      processResourceAssignmentCodeAssignments();
      processUdfValues();
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

      rollupValues();
      m_project.updateStructure();
      m_project.readComplete();

      return m_project;
   }

   /**
    * Process activity code assignments.
    */
   private void processActivityCodeAssignments()
   {
      List<Row> assignments = m_file.getRows("taskactv", null, null);
      processActivityCodeAssignments(assignments);
   }

   /**
    * Process resource code assignments.
    */
   private void processResourceCodeAssignments()
   {
      List<Row> assignments = m_file.getRows("rsrcrcat", null, null);
      processResourceCodeAssignments(assignments);
   }

   /**
    * Process role code assignments.
    */
   private void processRoleCodeAssignments()
   {
      List<Row> assignments = m_file.getRows("rolercat", null, null);
      processRoleCodeAssignments(assignments);
   }

   /**
    * Process resource assignment code assignments.
    */
   private void processResourceAssignmentCodeAssignments()
   {
      List<Row> assignments = m_file.getRows("asgnmntacat", null, null);
      processResourceAssignmentCodeAssignments(assignments);
   }

   /**
    * Process user defined field values.
    */
   private void processUdfValues()
   {
      List<Row> values = m_file.getRows("udfvalue", null, null);
      processUdfValues(values);
   }

   /**
    * Process resources.
    */
   private void processResources()
   {
      List<Row> rows = m_file.getRows("rsrc", null, null);
      processResources(rows);
   }

   /**
    * Process roles.
    */
   private void processRoles()
   {
      List<Row> rows = m_file.getRows("roles", null, null);
      processRoles(rows);
   }

   /**
    * Process role assignments.
    */
   private void processRoleAssignments()
   {
      List<Row> rows = m_file.getRows("rsrcrole", null, null);
      processRoleAssignments(rows);
   }

   /**
    * Process resource rates.
    */
   private void processResourceRates()
   {
      List<Row> rows = m_file.getRows("rsrcrate", null, null);
      processResourceRates(rows);
   }

   /**
    * Process role rates.
    */
   private void processRoleRates()
   {
      List<Row> rows = m_file.getRows("rolerate", null, null);
      processRoleRates(rows);
      processRoleAvailability(rows);
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
      processProjectProperties(rows);

      rows = m_file.getRows("projpcat", "proj_id", m_projectID);
      processProjectCodeAssignments(rows);

      //
      // Process XER-specific attributes
      //
      if (m_file.getDefaultCurrencyData() != null)
      {
         processDefaultCurrency(m_file.getDefaultCurrencyData());
      }

      processScheduleOptions();
   }

   /**
    * Process tasks.
    */
   private void processTasks()
   {
      List<Row> wbs = m_file.getRows("projwbs", "proj_id", m_projectID);
      List<Row> tasks = m_file.getRows("task", "proj_id", m_projectID);
      Map<Integer, Notes> wbsNotes = getNotes(m_file.getRows("wbsmemo", "proj_id", m_projectID), "wbs_memo_id", "wbs_id", "wbs_memo");
      Map<Integer, Notes> taskNotes = getNotes(m_file.getRows("taskmemo", "proj_id", m_projectID), "memo_id", "task_id", "task_memo");

      wbs.sort(WBS_ROW_COMPARATOR);
      processTasks(wbs, tasks, wbsNotes, taskNotes);
   }

   /**
    * Process predecessors.
    */
   private void processPredecessors()
   {
      List<Row> rows = m_file.getRows("taskpred", "proj_id", m_projectID);
      processPredecessors(rows);
   }

   /**
    * Process resource assignments.
    */
   private void processAssignments()
   {
      List<Row> rows = m_file.getRows("taskrsrc", "proj_id", m_projectID);
      processAssignments(rows);
   }

   /**
    * Process expense items.
    */
   private void processExpenseItems()
   {
      processExpenseItems(m_file.getRows("projcost", "proj_id", m_projectID));
   }

   /**
    * Process activity steps.
    */
   private void processActivitySteps()
   {
      processActivitySteps(m_file.getRows("taskproc", "proj_id", m_projectID));
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
         processScheduleOptions(rows.get(0));
      }
   }

   private final XerFile m_file;
   private final Integer m_projectID;

   private static final WbsRowComparatorXER WBS_ROW_COMPARATOR = new WbsRowComparatorXER();
}
