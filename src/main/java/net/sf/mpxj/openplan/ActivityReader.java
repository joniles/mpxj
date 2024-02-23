package net.sf.mpxj.openplan;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Task;
import net.sf.mpxj.common.HierarchyHelper;
import net.sf.mpxj.common.NumberHelper;
import org.apache.poi.poifs.filesystem.DirectoryEntry;

class ActivityReader
{
   public ActivityReader(DirectoryEntry root, ProjectFile file)
   {
      m_root = root;
      m_file = file;
   }

   public void read(String name)
   {
      Map<String, Task> map = new HashMap<>();
      List<Row> rows = new TableReader(m_root, "ACT").read();
      HierarchyHelper.sortHierarchy(rows, r -> r.getString("ACT_ID"), r -> getParentActivityID(r.getString("ACT_ID")), Comparator.comparing(o -> o.getString("ACT_ID")));

      for (Row row : rows)
      {
         Task task;
         String activityID = row.getString("ACT_ID");

         Task parentTask = map.get(getParentActivityID(activityID));
         if (parentTask == null)
         {
            task = m_file.addTask();
         }
         else
         {
            task = parentTask.addTask();
         }


         // OPP_ACT and OPP_ACR in the same table in the file?
         //
         // ACTIVEINDEX: Probability of Being Active
         // ACT_ID: Activity ID
         task.setActivityID(activityID);
         // ACT_PROBABILITY: Probability of Occurrence
         // ACT_TYPE: Activity Type (L: ALAP, N: ASAP, D: Discontinuous, R: Effort Driven, E: External Subproject, F: Finish Milestone, H:Hammock, P: Subproject, S: Start Milestone, Z: Foreign Project, Y: Foreign Subproject, G:Foreign Activity)
         // ACT_UID: Activity Unique ID
         task.setGUID(row.getUuid("ACT_UID"));
         // ACWP_LAB: ACWP Labor
         // ACWP_MAT: ACWP Material
         // ACWP_ODC: ACWP Other Direct Cost
         // ACWP_QTY: ACWP Labor Units
         // ACWP_SUB: ACWP Subcontract
         // BAC_LAB: Budget At Completion Labor
         // BAC_MAT: Budget At Completion Material
         // BAC_ODC: Budget At Completion Other Direct Cost
         // BAC_QTY: Budget At Completion  Labor Units
         // BAC_SUB: Budget At Completion Subcontractor
         // BCWP_LAB: BCWP Labor
         // BCWP_MAT: BCWP Material
         // BCWP_ODC: BCWP Other Direct Cost
         // BCWP_QTY: BCWP Labor Units
         // BCWP_SUB: BCWP Subcontractor
         // BCWS_LAB: BCWS Labor
         // BCWS_MAT: BCWS Material
         // BCWS_ODC: BCWS Other Direct Cost
         // BCWS_QTY: BCWS Labor Units
         // BCWS_SUB: BCWS Subcontractor
         // BFDATE:  Baseline Finish Date
         task.setBaselineFinish(row.getDate("BFDATE"));
         // BSDATE: Baseline Start Date
         task.setBaselineStart(row.getDate("BSDATE"));
         // CLH_ID: Calendar Name
         // CLH_UID: Calendar Unique ID
         // COMPSTAT: Computed Status (0: Planned, 1: In Progress, 2: Complete)
         // COMP_RS_C: Result of Schedule Actions (null: Normal, P: Splittable, T: Stretchable, R: Reprofilable, I: Immediate)
         // CRITICAL: Critical (0: Not Critical, 1: Critical, 2: Most Critical, 3: Controlling Critical)
         task.setCritical(Boolean.valueOf(NumberHelper.getInt(row.getInteger("CRITICAL")) == 0));
         // CRITINDEX: Probability of Being Critical
         // DELAYRES_UID: Delaying Resource Unique ID
         // DESCRIPTION: Description
         task.setName(row.getString("DESCRIPTION"));
         // DHIGH: Pessimistic Duration
         // DIR_ID: Project Object Directory Name
         // DIR_UID: Project Object Directory UID
         // DLOW: Optimistic Duration
         // DSHAPE: Duration Distribution Type (null: None, U: Uniform, N: Normal, B: Beta, and T: Triangular)
         // EFDATE: Early Finish Date
         task.setEarlyFinish(row.getDate("EFDATE"));
         // ESDATE: Early Start Date
         task.setEarlyStart(row.getDate("ESDATE"));
         // ETC_LAB: Estimate to Complete Labor
         // ETC_MAT: Estimate to Complete Material
         // ETC_ODC: Estimate to Complete Other Direct Cost
         // ETC_QTY: Estimate to Complete Labor Units
         // ETC_SUB: Estimate to Complete Subcontractors
         // EVT: Earned Value Technique (A: Level of Effort, C: Percent Complete, E: 50-50, F: 0-100, G: 100-0, H: user defined percentage, K: Planning package, L: resource % complete, s: Steps)
         // FEDATE: Earliest Feasible Start
         // FINTOTFLT: Finish Total Float
         // FREEFLOAT: Free Float
         task.setFreeSlack(row.getDuration("FREEFLOAT"));
         // LASTUPDATE: Last Update Date
         // LFDATE: Late Finish Date
         task.setLateFinish(row.getDate("LFDATE"));
         // LOGICFLAG: Activity Logic Flag (S: Start Activity, null: None, F: End Activity, SF: Start and Finish Activity, I: Isolated)
         // LSDATE: Late Start Date
         task.setLateStart(row.getDate("LSDATE"));
         // MAXDUR: Maximum Duration for Resource Scheduling
         // MAXSPLITS: Maximum Splits for Resource Scheduling
         // MEAN_EF: Mean Early Finish Date
         // MEAN_ES: Mean Early Start Date
         // MEAN_LF: Mean late Finish Date
         // MEAN_LS: Mean Late Start Date
         // MEAN_TF: Mean Total Float
         // MINSPLITD: Minimum Split Length
         // MSPUNIQUEID: Imported MS Project Unique ID
         // OPKEY: Key Activity
         // ORIG_DUR: Original Duration
         task.setPlannedDuration(row.getDuration("ORIG_DUR"));
         // OUTOFSEQ: Progressed Out of Sequence Flag
         // PPC: Physical Percent Complete
         // PRIORITY: Priority
         // PROGTYPE: Progress Type (null: Planned, R: Remaining Duration, P: Percent Complete, E: Elapsed Duration, C: Complete)
         // PROGVALUE: Progress Value
         // REM_DUR: Remaining Duration
         task.setRemainingDuration(row.getDuration("REM_DUR"));
         // RES_DATE: First Usage Date
         // RSCLASS: Resource Scheduling Options (null: Normal, P: Splittable, T: Stretchable, R: Reprofilable, I: Immediate)
         // RS_FLOAT: Scheduled Float
         // RS_SUPRESS: Suppress Requirements During Resource Scheduling
         // SCHED_DUR: Scheduled Duration
         task.setDuration(row.getDuration("SCHED_DUR"));
         // SDEV_EF: Early Finish Standard Deviation
         // SDEV_ES: Early Start Standard Deviation
         // SDEV_LF: Late Finish Standard Deviation
         // SDEV_LS: Late Start Standard Deviation
         // SDEV_TF: Total Float Standard Deviation
         // SEP_ASG: Separate Assignments
         // SEQUENCE: Update Count
         // SFDATE: Scheduled Finish Date
         task.setFinish(row.getDate("SFDATE"));
         // SOURCE_BASELINE: Source Baseline Name
         // SSDATE: Schedule Start Date
         task.setStart(row.getDate("SSDATE"));
         // SSINDEX: Sensitivity Index
         // STARTPC: User Defined EVT Split %
         // TARGFTYPE: Target Finish Type (null: None, NE: Not Earlier Than, NL: Not Later Than, ON: On Target, FX: Fixed Target)
         // TARGSTYPE: Target Start Type  (null: None, NE: Not Earlier Than, NL: Not Later Than, ON: On Target, FX: Fixed Target)
         // TOTALFLOAT: Total Float
         task.setTotalSlack(row.getDuration("TOTALFLOAT"));
         // USR_ID: Last Update User
         
         map.put(task.getActivityID(), task);
      }
   }


   private String getParentActivityID(String activityID)
   {
      int index = activityID.lastIndexOf('.');
      if (index == -1)
      {
         return null;
      }
      return activityID.substring(0,index);
   }

   private final ProjectFile m_file;
   private final DirectoryEntry m_root;
}
