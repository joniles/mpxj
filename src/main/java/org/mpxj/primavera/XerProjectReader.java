package org.mpxj.primavera;

import java.util.List;
import java.util.Map;

import org.mpxj.FieldType;
import org.mpxj.Notes;
import org.mpxj.ProjectFile;

class XerProjectReader extends TableProjectReader
{
   public XerProjectReader(XerFile file, ProjectFile project, Integer projectID, TableReaderState state)
   {
      m_file = file;
      m_project = project;
      m_projectID = projectID;
      m_state = state;
   }

   public ProjectFile read()
   {
      m_project.getProjectProperties().setFileApplication("Primavera");
      m_project.getProjectProperties().setFileType("XER");
      m_project.getProjectProperties().setUniqueID(m_projectID);

      processActivityCodeAssignments();
      processResourceAssignmentCodeAssignments();
      processRoleAssignments();

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
    * Process resource assignment code assignments.
    */
   private void processResourceAssignmentCodeAssignments()
   {
      List<Row> assignments = m_file.getRows("asgnmntacat", null, null);
      processResourceAssignmentCodeAssignments(assignments);
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
