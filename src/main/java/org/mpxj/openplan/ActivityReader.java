/*
 * file:       ActivityReader.java
 * author:     Jon Iles
 * date:       2024-02-27
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

package org.mpxj.openplan;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.mpxj.ActivityCodeValue;
import org.mpxj.ActivityStatus;
import org.mpxj.ActivityType;
import org.mpxj.ConstraintType;
import org.mpxj.Priority;
import org.mpxj.ProjectCalendar;
import org.mpxj.ProjectFile;
import org.mpxj.Task;
import org.mpxj.common.HierarchyHelper;
import org.mpxj.common.NumberHelper;
import org.apache.poi.poifs.filesystem.DirectoryEntry;

/**
 * Populate the parent project with activities.
 */
class ActivityReader
{
   /**
    * Constructor.
    *
    * @param root parent directory
    * @param file project file
    */
   public ActivityReader(DirectoryEntry root, ProjectFile file)
   {
      m_root = root;
      m_file = file;
   }

   /**
    * Add activities to the project file.
    *
    * @param codeMap column names to ActivityCodeValue map
    * @param calendarMap calendar ID to ProjectCalendar map.
    */
   public void read(Map<String, Map<String, ActivityCodeValue>> codeMap, Map<String, ProjectCalendar> calendarMap)
   {
      Map<String, Task> map = new HashMap<>();
      List<Row> rows = new TableReader(m_root, "ACT").read();
      HierarchyHelper.sortHierarchy(rows, r -> r.getString("ACT_ID"), r -> OpenPlanHierarchyHelper.getParentID(r.getString("ACT_ID")), Comparator.comparing(o -> o.getString("ACT_ID")));

      for (Row row : rows)
      {
         Task task;
         String activityID = row.getString("ACT_ID");

         Task parentTask = map.get(OpenPlanHierarchyHelper.getParentID(activityID));
         if (parentTask == null)
         {
            task = m_file.addTask();
         }
         else
         {
            task = parentTask.addTask();
         }

         // OPP_ACT and OPP_ACR in the same table in the file?

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
         task.setACWP(sum(row, "ACWP_LAB", "ACWP_MAT", "ACWP_ODC", "ACWP_SUB"));
         // AFDATE: Actual Finish Date
         task.setActualFinish(row.getDate("AFDATE"));
         // ASDATE: Actual Start Date
         task.setActualStart(row.getDate("ASDATE"));
         // BAC_LAB: Budget At Completion Labor
         // BAC_MAT: Budget At Completion Material
         // BAC_ODC: Budget At Completion Other Direct Cost
         // BAC_QTY: Budget At Completion  Labor Units
         // BAC_SUB: Budget At Completion Subcontractor
         task.setBaselineCost(sum(row, "BAC_LAB", "BAC_MAT", "BAC_ODC", "BAC_SUB"));
         // BCWP_LAB: BCWP Labor
         // BCWP_MAT: BCWP Material
         // BCWP_ODC: BCWP Other Direct Cost
         // BCWP_QTY: BCWP Labor Units
         // BCWP_SUB: BCWP Subcontractor
         task.setBCWP(sum(row, "BCWP_LAB", "BCWP_MAT", "BCWP_ODC", "BCWP_SUB"));
         // BCWS_LAB: BCWS Labor
         // BCWS_MAT: BCWS Material
         // BCWS_ODC: BCWS Other Direct Cost
         // BCWS_QTY: BCWS Labor Units
         // BCWS_SUB: BCWS Subcontractor
         task.setBCWS(sum(row, "BCWS_LAB", "BCWS_MAT", "BCWS_ODC", "BCWS_SUB"));
         // BFDATE:  Baseline Finish Date
         task.setBaselineFinish(row.getDate("BFDATE"));
         // BSDATE: Baseline Start Date
         task.setBaselineStart(row.getDate("BSDATE"));
         // CLH_ID: Calendar Name
         task.setCalendar(calendarMap.get(row.getString("CLH_ID")));
         // CLH_UID: Calendar Unique ID
         // COMPSTAT: Computed Status (0: Planned, 1: In Progress, 2: Complete)
         // COMP_RS_C: Result of Schedule Actions (null: Normal, P: Splittable, T: Stretchable, R: Reprofilable, I: Immediate)
         // CRITICAL: Critical (0: Not Critical, 1: Critical, 2: Most Critical, 3: Controlling Critical)
         task.setCritical(NumberHelper.getInt(row.getInteger("CRITICAL")) == 0);
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
         task.setCost(sum(row, "ETC_LAB", "ETC_MAT", "ETC_ODC", "ETC_SUB"));
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
         task.setPlannedFinish(row.getDate("SFDATE"));
         // SOURCE_BASELINE: Source Baseline Name
         // SSDATE: Schedule Start Date
         task.setPlannedStart(row.getDate("SSDATE"));
         // SSINDEX: Sensitivity Index
         // STARTPC: User Defined EVT Split %
         // TARGFTYPE: Target Finish Type (null: None, NE: Not Earlier Than, NL: Not Later Than, ON: On Target, FX: Fixed Target)
         // TARGSTYPE: Target Start Type  (null: None, NE: Not Earlier Than, NL: Not Later Than, ON: On Target, FX: Fixed Target)
         // TOTALFLOAT: Total Float
         task.setTotalSlack(row.getDuration("TOTALFLOAT"));
         // USR_ID: Last Update User

         // From: https://help.deltek.com/product/acumentouchstone/8.2/ga/Open%20Plan%20BK3%20Calculated%20Fields.html
         task.setConstraintType(getConstraintType(row));
         task.setConstraintDate(getConstraintDate(task, row));
         task.setSecondaryConstraintType(getSecondaryConstraintType(task, row));
         task.setSecondaryConstraintDate(getSecondaryConstraintDate(task, row));
         task.setActivityStatus(getActivityStatus(row));
         task.setStart(task.getActualStart() == null ? task.getEarlyStart() : task.getActualStart());
         task.setFinish(task.getActualFinish() == null ? task.getEarlyFinish() : task.getActualFinish());

         // Align with sample XER
         //task.setType(TaskType.FIXED_DURATION);
         task.setActivityType(ActivityType.TASK_DEPENDENT);
         task.setPriority(Priority.getInstance(Priority.MEDIUM));

         for (Map.Entry<String, Map<String, ActivityCodeValue>> entry : codeMap.entrySet())
         {
            String columnValue = row.getString(entry.getKey());
            ActivityCodeValue acv = entry.getValue().get(columnValue);
            if (acv != null)
            {
               task.addActivityCodeValue(acv);
            }
         }

         map.put(task.getActivityID(), task);
      }
   }

   /**
    * Determine the constraint type.
    *
    * @param row activity data
    * @return constraint type
    */
   private ConstraintType getConstraintType(Row row)
   {
      if ("L".equals(row.getString("ACT_TYPE")))
      {
         return ConstraintType.AS_LATE_AS_POSSIBLE;
      }

      String targetStartType = row.getString("TARGSTYPE");
      return targetStartType == null ? TARGET_FINISH_TYPE_MAP.get(row.getString("TARGFTYPE")) : TARGET_START_MAP.get(targetStartType);
   }

   /**
    * Determine the constraint date.
    *
    * @param task parent task
    * @param row activity data
    * @return constraint date
    */
   private LocalDateTime getConstraintDate(Task task, Row row)
   {
      ConstraintType type = task.getConstraintType();
      if (type == null)
      {
         return null;
      }

      switch (task.getConstraintType())
      {
         case START_NO_EARLIER_THAN:
         case START_NO_LATER_THAN:
         case MUST_START_ON:
            return row.getDate("TSDATE");

         case FINISH_NO_LATER_THAN:
         case FINISH_NO_EARLIER_THAN:
         case MUST_FINISH_ON:
            return row.getDate("TFDATE");

         default:
            return null;
      }
   }

   /**
    * Determine the secondary constraint type.
    *
    * @param task parent task
    * @param row activity data
    * @return secondary constraint type
    */
   private ConstraintType getSecondaryConstraintType(Task task, Row row)
   {
      ConstraintType primaryConstraintType = task.getConstraintType();

      if (primaryConstraintType == ConstraintType.START_NO_EARLIER_THAN || primaryConstraintType == ConstraintType.START_NO_LATER_THAN || primaryConstraintType == ConstraintType.MUST_START_ON)
      {
         return TARGET_FINISH_TYPE_MAP.get(row.getString("TARGFTYPE"));
      }

      return null;
   }

   /**
    * Determine the secondary constraint date.
    *
    * @param task parent task
    * @param row activity data
    * @return secondary constraint date
    */
   private LocalDateTime getSecondaryConstraintDate(Task task, Row row)
   {
      return task.getSecondaryConstraintType() == null ? null : row.getDate("TFDATE");
   }

   /**
    * Determine the activity status.
    *
    * @param row activity data
    * @return activity status
    */
   private ActivityStatus getActivityStatus(Row row)
   {
      switch (row.getInteger("COMPSTAT").intValue())
      {
         case 2:
            return ActivityStatus.COMPLETED;

         case 1:
            return ActivityStatus.IN_PROGRESS;

         default:
            return null;
      }
   }

   /**
    * Generate a sum of the values in the named columns.
    *
    * @param row activity data
    * @param keys column names
    * @return sum of values
    */
   private Double sum(Row row, String... keys)
   {
      return Double.valueOf(Arrays.stream(keys).map(row::getDouble).filter(Objects::nonNull).mapToDouble(Double::doubleValue).sum());
   }

   private final ProjectFile m_file;
   private final DirectoryEntry m_root;

   private static final Map<String, ConstraintType> TARGET_FINISH_TYPE_MAP = new HashMap<>();
   static
   {
      TARGET_FINISH_TYPE_MAP.put("NE", ConstraintType.FINISH_NO_EARLIER_THAN); // Finish On Or After
      TARGET_FINISH_TYPE_MAP.put("NL", ConstraintType.FINISH_NO_LATER_THAN); // Finish On Or Before
      TARGET_FINISH_TYPE_MAP.put("ON", ConstraintType.MUST_FINISH_ON); // Must Finish On
      TARGET_FINISH_TYPE_MAP.put("FX", ConstraintType.MUST_FINISH_ON); // Mandatory Finish
   }

   private static final Map<String, ConstraintType> TARGET_START_MAP = new HashMap<>();
   static
   {
      TARGET_START_MAP.put("NE", ConstraintType.START_NO_EARLIER_THAN); // Start On Or After
      TARGET_START_MAP.put("NL", ConstraintType.START_NO_LATER_THAN); // Start On Or Before
      TARGET_START_MAP.put("ON", ConstraintType.MUST_START_ON); // Must Start On
      TARGET_START_MAP.put("FX", ConstraintType.MUST_START_ON); // Mandatory Start
   }
}
