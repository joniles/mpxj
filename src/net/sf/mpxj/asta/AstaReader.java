/*
 * file:       AstaReader.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2011
 * date:       07/04/2011
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

package net.sf.mpxj.asta;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.mpxj.ConstraintType;
import net.sf.mpxj.Duration;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ProjectHeader;
import net.sf.mpxj.RelationType;
import net.sf.mpxj.Resource;
import net.sf.mpxj.ResourceAssignment;
import net.sf.mpxj.ResourceType;
import net.sf.mpxj.Task;
import net.sf.mpxj.TimeUnit;

/**
 * This class provides a generic front end to read project data from
 * a database.
 */
final class AstaReader
{
   /**
    * Constructor.
    */
   public AstaReader()
   {
      m_project = new ProjectFile();

      m_project.setAutoTaskUniqueID(false);
      m_project.setAutoResourceUniqueID(false);

      m_project.setAutoCalendarUniqueID(false);
      m_project.setAutoWBS(false);
   }

   /**
    * Retrieves the project data read from this file.
    * 
    * @return project data
    */
   public ProjectFile getProject()
   {
      return m_project;
   }

   /**
    * Process project header.
    * 
    * @param row project header data.
    */
   public void processProjectHeader(Row row)
   {
      ProjectHeader ph = m_project.getProjectHeader();
      ph.setDuration(row.getDuration("DURATIONHOURS"));
      ph.setStartDate(row.getDate("STARU"));
      ph.setFinishDate(row.getDate("ENE"));
      ph.setName(row.getString("SHORT_NAME"));
      ph.setAuthor(row.getString("PROJECT_BY"));
      //DURATION_TIME_UNIT
      ph.setLastSaved(row.getDate("LAST_EDITED_DATE"));
   }

   /**
    * Process resources.
    * 
    * @param permanentRows permanent resource data
    * @param consumableRows consumable resource data
    */
   public void processResources(List<Row> permanentRows, List<Row> consumableRows)
   {
      //
      // Process permanent resources
      //
      for (Row row : permanentRows)
      {
         Resource resource = m_project.addResource();
         resource.setType(ResourceType.WORK);
         resource.setUniqueID(row.getInteger("PERMANENT_RESOURCEID"));
         resource.setEmailAddress(row.getString("EMAIL_ADDRESS"));
         // EFFORT_TIME_UNIT
         resource.setName(row.getString("NASE"));
         resource.setResourceCalendar(deriveResourceCalendar(row.getInteger("CALENDAV")));
         resource.setMaxUnits(Double.valueOf(row.getDouble("AVAILABILITY").doubleValue() * 100));
         resource.setIsGeneric(row.getBoolean("CREATED_AS_FOLDER"));
         resource.setInitials(getInitials(resource.getName()));
      }

      //
      // Process groups
      //
      for (Row row : permanentRows)
      {
         Resource resource = m_project.getResourceByUniqueID(row.getInteger("PERMANENT_RESOURCEID"));
         Resource group = m_project.getResourceByUniqueID(row.getInteger("ROLE"));
         if (resource != null && group != null)
         {
            resource.setGroup(group.getName());
         }
      }

      //
      // Process consumable resources
      //
      for (Row row : consumableRows)
      {
         Resource resource = m_project.addResource();
         resource.setType(ResourceType.MATERIAL);
         resource.setUniqueID(row.getInteger("CONSUMABLE_RESOURCEID"));
         resource.setCostPerUse(row.getDouble("COST_PER_USEDEFAULTSAMOUNT"));
         resource.setPeakUnits(Double.valueOf(row.getDouble("AVAILABILITY").doubleValue() * 100));
         resource.setName(row.getString("NASE"));
         resource.setResourceCalendar(deriveResourceCalendar(row.getInteger("CALENDAV")));
         resource.setAvailableFrom(row.getDate("AVAILABLE_FROM"));
         resource.setAvailableTo(row.getDate("AVAILABLE_TO"));
         resource.setIsGeneric(row.getBoolean("CREATED_AS_FOLDER"));
         resource.setMaterialLabel(row.getString("MEASUREMENT"));
         resource.setInitials(getInitials(resource.getName()));
      }
   }

   /**
    * Derive a calendar for a resource.
    * 
    * @param parentCalendarID calendar from which resource calendar is derived
    * @return new calendar for a resource
    */
   private ProjectCalendar deriveResourceCalendar(Integer parentCalendarID)
   {
      ProjectCalendar calendar = m_project.addDefaultDerivedCalendar();
      calendar.setUniqueID(Integer.valueOf(m_project.getCalendarUniqueID()));
      calendar.setParent(m_project.getCalendarByUniqueID(parentCalendarID));
      return calendar;
   }

   /**
    * Process tasks.
    * 
    * @param bars bar data
    * @param tasks task data
    * @param milestones milestone data
    */
   public void processTasks(List<Row> bars, List<Row> tasks, List<Row> milestones)
   {
      //
      // Process bars
      //
      for (Row row : bars)
      {
         Task task = m_project.addTask();
         Integer calendarID = row.getInteger("CALENDAU");
         ProjectCalendar calendar = m_project.getCalendarByUniqueID(calendarID);

         //PROJID
         task.setUniqueID(row.getInteger("BARID"));
         task.setStart(row.getDate("STARV"));
         task.setFinish(row.getDate("ENF"));
         //NATURAL_ORDER
         //SPARI_INTEGER
         task.setName(row.getString("NAMH"));
         //EXPANDED_TASK
         //PRIORITY
         //UNSCHEDULABLE
         //MARK_FOR_HIDING
         //TASKS_MAY_OVERLAP
         //SUBPROJECT_ID
         //ALT_ID
         //LAST_EDITED_DATE
         //LAST_EDITED_BY
         //Proc_Approve
         //Proc_Design_info
         //Proc_Proc_Dur
         //Proc_Procurement
         //Proc_SC_design
         //Proc_Select_SC
         //Proc_Tender
         //QA Checked
         //Related_Documents         
         task.setWBS("-");
         task.setCalendar(calendar);

         m_project.fireTaskReadEvent(task);
      }

      //
      // Create hierarchical bar structure
      //
      m_project.getChildTasks().clear();
      for (Row row : bars)
      {
         Task task = m_project.getTaskByUniqueID(row.getInteger("BARID"));
         //Task parentTask = m_project.getTaskByUniqueID(row.getInteger("SUBPROJECT_ID"));
         Task parentTask = m_project.getTaskByUniqueID(row.getInteger("BAR"));
         if (parentTask == null)
         {
            m_project.getChildTasks().add(task);
         }
         else
         {
            m_project.getChildTasks().remove(task);
            parentTask.getChildTasks().add(task);
            if (parentTask.getWBS().equals("-"))
            {
               String wbs = row.getString("WBN_CODE");
               parentTask.setWBS(wbs == null || wbs.length() == 0 ? "-" : wbs);
            }
         }
      }

      //
      // Process Tasks
      //
      for (Row row : tasks)
      {
         Task parentTask = m_project.getTaskByUniqueID(row.getInteger("BAR"));
         Task task = parentTask == null ? m_project.addTask() : parentTask.addTask();

         //"PROJID"
         task.setUniqueID(row.getInteger("TASKID"));
         //GIVEN_DURATIONTYPF
         //GIVEN_DURATIONELA_MONTHS
         task.setDuration(row.getDuration("GIVEN_DURATIONHOURS"));
         task.setResume(row.getDate("RESUME"));
         //task.setStart(row.getDate("GIVEN_START"));         
         //LATEST_PROGRESS_PERIOD
         //TASK_WORK_RATE_TIME_UNIT
         //TASK_WORK_RATE
         //PLACEMENT
         //BEEN_SPLIT
         //INTERRUPTIBLE
         //HOLDING_PIN
         ///ACTUAL_DURATIONTYPF
         //ACTUAL_DURATIONELA_MONTHS
         task.setActualDuration(row.getDuration("ACTUAL_DURATIONHOURS"));
         task.setEarlyStart(row.getDate("EARLY_START_DATE"));
         task.setLateStart(row.getDate("LATE_START_DATE"));
         //FREE_START_DATE
         //START_CONSTRAINT_DATE
         //END_CONSTRAINT_DATE
         task.setBaselineWork(row.getDuration("EFFORT_BUDGET"));
         //NATURAO_ORDER
         //LOGICAL_PRECEDENCE
         //SPAVE_INTEGER
         //SWIM_LANE
         //USER_PERCENT_COMPLETE
         task.setPercentageComplete(row.getDouble("OVERALL_PERCENV_COMPLETE"));
         //OVERALL_PERCENT_COMPL_WEIGHT
         task.setName(row.getString("NARE"));
         task.setWBS(row.getString("WBN_CODE"));
         //NOTET
         //UNIQUE_TASK_ID
         task.setCalendar(m_project.getCalendarByUniqueID(row.getInteger("CALENDAU")));
         //EFFORT_TIMI_UNIT
         //WORL_UNIT
         //LATEST_ALLOC_PROGRESS_PERIOD
         //WORN
         //BAR
         //CONSTRAINU
         //PRIORITB
         //CRITICAM
         //USE_PARENU_CALENDAR
         //BUFFER_TASK
         //MARK_FOS_HIDING
         //OWNED_BY_TIMESHEEV_X
         //START_ON_NEX_DAY
         //LONGEST_PATH
         //DURATIOTTYPF
         //DURATIOTELA_MONTHS
         //DURATIOTHOURS
         task.setStart(row.getDate("STARZ"));
         task.setFinish(row.getDate("ENJ"));
         //DURATION_TIMJ_UNIT
         //UNSCHEDULABLG
         //SUBPROJECT_ID
         //ALT_ID
         //LAST_EDITED_DATE
         //LAST_EDITED_BY    

         processConstraints(row, task);

         if (task.getPercentageComplete().intValue() != 0)
         {
            task.setActualStart(task.getStart());
            if (task.getPercentageComplete().intValue() == 100)
            {
               task.setActualFinish(task.getFinish());
               task.setDuration(task.getActualDuration());
            }
         }

         m_project.fireTaskReadEvent(task);
      }

      for (Row row : milestones)
      {
         Task parentTask = m_project.getTaskByUniqueID(row.getInteger("BAR"));
         Task task = parentTask == null ? m_project.addTask() : parentTask.addTask();

         task.setMilestone(true);
         //PROJID         
         task.setUniqueID(row.getInteger("MILESTONEID"));
         task.setStart(row.getDate("GIVEN_DATE_TIME"));
         task.setFinish(row.getDate("GIVEN_DATE_TIME"));
         //PROGREST_PERIOD
         //SYMBOL_APPEARANCE
         //MILESTONE_TYPE
         //PLACEMENU
         task.setPercentageComplete(row.getBoolean("COMPLETED") ? COMPLETE : INCOMPLETE);
         //INTERRUPTIBLE_X
         //ACTUAL_DURATIONTYPF
         //ACTUAL_DURATIONELA_MONTHS
         //ACTUAL_DURATIONHOURS
         task.setEarlyStart(row.getDate("EARLY_START_DATE"));
         task.setLateStart(row.getDate("LATE_START_DATE"));
         //FREE_START_DATE
         //START_CONSTRAINT_DATE
         //END_CONSTRAINT_DATE
         //EFFORT_BUDGET
         //NATURAO_ORDER
         //LOGICAL_PRECEDENCE
         //SPAVE_INTEGER
         //SWIM_LANE
         //USER_PERCENT_COMPLETE
         //OVERALL_PERCENV_COMPLETE
         //OVERALL_PERCENT_COMPL_WEIGHT
         task.setName(row.getString("NARE"));
         task.setWBS(row.getString("WBN_CODE"));
         //NOTET
         //UNIQUE_TASK_ID
         task.setCalendar(m_project.getCalendarByUniqueID(row.getInteger("CALENDAU")));
         //WBT
         //EFFORT_TIMI_UNIT
         //WORL_UNIT
         //LATEST_ALLOC_PROGRESS_PERIOD
         //WORN
         //CONSTRAINU
         //PRIORITB
         //CRITICAM
         //USE_PARENU_CALENDAR
         //BUFFER_TASK
         //MARK_FOS_HIDING
         //OWNED_BY_TIMESHEEV_X
         //START_ON_NEX_DAY
         //LONGEST_PATH
         //DURATIOTTYPF
         //DURATIOTELA_MONTHS
         //DURATIOTHOURS
         //STARZ
         //ENJ
         //DURATION_TIMJ_UNIT
         //UNSCHEDULABLG
         //SUBPROJECT_ID
         //ALT_ID
         //LAST_EDITED_DATE
         //LAST_EDITED_BY
         task.setDuration(ZERO_HOURS);
      }

      deriveProjectCalendar();

      updateStructure();
   }

   /**
    * Iterates through the tasks setting the correct
    * outline level and ID values.
    */
   private void updateStructure()
   {
      int id = 1;
      Integer outlineLevel = Integer.valueOf(1);
      for (Task task : m_project.getChildTasks())
      {
         id = updateStructure(id, task, outlineLevel);
      }
   }

   /**
    * Iterates through the tasks setting the correct
    * outline level and ID values. 
    * 
    * @param id current ID value
    * @param task current task
    * @param outlineLevel current outline level
    * @return next ID value
    */
   private int updateStructure(int id, Task task, Integer outlineLevel)
   {
      task.setID(Integer.valueOf(id++));
      task.setOutlineLevel(outlineLevel);
      outlineLevel = Integer.valueOf(outlineLevel.intValue() + 1);
      for (Task childTask : task.getChildTasks())
      {
         id = updateStructure(id, childTask, outlineLevel);
      }
      return id;
   }

   /**
    * Processes predecessor data.
    * 
    * @param rows predecessor data
    */
   public void processPredecessors(List<Row> rows)
   {
      for (Row row : rows)
      {
         Task startTask = m_project.getTaskByUniqueID(row.getInteger("START_TASK"));
         Task endTask = m_project.getTaskByUniqueID(row.getInteger("END_TASK"));

         if (startTask != null && endTask != null)
         {
            RelationType type = getRelationType(row.getInt("TYPI"));

            Duration startLag = row.getDuration("START_LAG_TIMEHOURS");
            Duration endLag = row.getDuration("END_LAG_TIMEHOURS");
            Duration lag = null;
            if (startLag.getDuration() != 0)
            {
               lag = startLag;
            }
            else
            {
               if (endLag.getDuration() != 0)
               {
                  lag = endLag;
               }
            }

            endTask.addPredecessor(startTask, type, lag);
         }

         //PROJID
         //LINKID
         //START_LAG_TIMETYPF
         //START_LAG_TIMEELA_MONTHS
         //START_LAG_TIMEHOURS
         //END_LAG_TIMETYPF
         //END_LAG_TIMEELA_MONTHS
         //END_LAG_TIMEHOURS
         //MAXIMUM_LAGTYPF
         //MAXIMUM_LAGELA_MONTHS
         //MAXIMUM_LAGHOURS
         //STARV_DATE
         //ENF_DATE
         //CURVATURE_PERCENTAGE
         //START_LAG_PERCENT_FLOAT
         //END_LAG_PERCENT_FLOAT
         //COMMENTS
         //LINK_CATEGORY
         //START_LAG_TIME_UNIT
         //END_LAG_TIME_UNIT
         //MAXIMUM_LAG_TIME_UNIT
         //START_TASK
         //END_TASK
         //TYPI
         //START_LAG_TYPE
         //END_LAG_TYPE
         //MAINTAIN_TASK_OFFSETS
         //UNSCHEDULABLF
         //CRITICAL
         //ON_LOOP
         //MAXIMUM_LAG_MODE
         //ANNOTATE_LEAD_LAG
         //START_REPOSITION_ON_TAS_MOVE
         //END_REPOSITION_ON_TASK_MOVE
         //DRAW_CURVED_IF_VERTICAL
         //AUTOMATIC_CURVED_LI_SETTINGS
         //DRAW_CURVED_LINK_TO_LEFT
         //LOCAL_LINK
         //DRIVING
         //ALT_ID
         //LAST_EDITED_DATE
         //LAST_EDITED_BY
      }
   }

   /**
    * Process assignment data.
    * 
    * @param permanentAssignments assignment data
    */
   public void processAssignments(List<Row> permanentAssignments)
   {
      for (Row row : permanentAssignments)
      {
         Task task = m_project.getTaskByUniqueID(row.getInteger("ALLOCATEE_TO"));
         Resource resource = m_project.getResourceByUniqueID(row.getInteger("PLAYER"));
         if (task != null && resource != null)
         {
            double percentComplete = row.getDouble("PERCENT_COMPLETE").doubleValue();
            Duration work = row.getWork("EFFORW");
            double actualWork = work.getDuration() * percentComplete;
            double remainingWork = work.getDuration() - actualWork;

            ResourceAssignment assignment = task.addResourceAssignment(resource);
            assignment.setUniqueID(row.getInteger("PERMANENT_SCHEDUL_ALLOCATIONID"));
            assignment.setStart(row.getDate("STARZ"));
            assignment.setFinish(row.getDate("ENJ"));
            assignment.setUnits(Double.valueOf(row.getDouble("GIVEN_ALLOCATION").doubleValue() * 100));
            assignment.setDelay(row.getDuration("DELAAHOURS"));
            assignment.setPercentageWorkComplete(Double.valueOf(percentComplete * 100));
            assignment.setWork(work);
            assignment.setActualWork(Duration.getInstance(actualWork, work.getUnits()));
            assignment.setRemainingWork(Duration.getInstance(remainingWork, work.getUnits()));

         }

         //PROJID
         //REQUIREE_BY
         //OWNED_BY_TIMESHEET_X
         //EFFORW
         //GIVEN_EFFORT
         //WORK_FROM_TASK_FACTOR
         //ALLOCATIOO
         //GIVEN_ALLOCATION
         //ALLOCATIOP_OF
         //WORM_UNIT
         //WORK_RATE_TIMF_UNIT
         //EFFORT_TIMJ_UNIT
         //WORO
         //GIVEN_WORK
         //WORL_RATE
         //GIVEN_WORK_RATE
         //TYPV
         //CALCULATEG_PARAMETER
         //BALANCINJ_PARAMETER
         //SHAREE_EFFORT
         //CONTRIBUTES_TO_ACTIVI_EFFORT
         //DELAATYPF
         //DELAAELA_MONTHS
         //DELAAHOURS
         //GIVEO_DURATIONTYPF
         //GIVEO_DURATIONELA_MONTHS
         //GIVEO_DURATIONHOURS
         //DELAY_TIMI_UNIT
         //RATE_TYPE
         //USE_TASM_CALENDAR
         //IGNORF
         //ELAPSEE
         //MAY_BE_SHORTER_THAN_TASK
         //RESUMF
         //SPAXE_INTEGER
         //USER_PERCENU_COMPLETE
         //ALLOCATIOR_GROUP         
         //PRIORITC
         //ACCOUNTED_FOR_ELSEWHERE
         //DURATIOTTYPF
         //DURATIOTELA_MONTHS
         //DURATIOTHOURS
         //DURATION_TIMJ_UNIT
         //UNSCHEDULABLG
         //SUBPROJECT_ID
         //permanent_schedul_allocation_ALT_ID
         //permanent_schedul_allocation_LAST_EDITED_DATE
         //permanent_schedul_allocation_LAST_EDITED_BY
         //perm_resource_skill_PROJID
         //PERM_RESOURCE_SKILLID
         //ARR_STOUT_STSKI_APARROW_TYPE
         //ARR_STOUT_STSKI_APLENGTH
         //ARR_STOUT_STSKI_APEDGE
         //ARR_STOUT_STSKI_APBORDET_COL
         //ARR_STOUT_STSKI_APINSIDG_COL
         //ARR_STOUT_STSKI_APPLACEMENW
         //BLI_STOUT_STSKI_APBLIP_TYPE
         //BLI_STOUT_STSKI_APSCALEY
         //BLI_STOUT_STSKI_APSCALEZ
         //BLI_STOUT_STSKI_APGAP
         //BLI_STOUT_STSKI_APBORDES_COL
         //BLI_STOUT_STSKI_APINSIDF_COL
         //BLI_STOUT_STSKI_APPLACEMENV
         //LIN_STOUT_STSKI_APSCALEX
         //LIN_STOUT_STSKI_APWIDTH
         //LIN_STOUT_STSKI_APBORDER_COL
         //LIN_STOUT_STSKI_APINSIDE_COL
         //LIN_STOUT_STSKI_APLINE_TYPE
         //SKI_APFOREGROUND_FILL_COLOUR
         //SKI_APBACKGROUND_FILL_COLOUR
         //SKI_APPATTERN
         //DURATIOODEFAULTTTYPF
         //DURATIOODEFAULTTELA_MONTHS
         //DURATIOODEFAULTTHOURS
         //DELAYDEFAULTTTYPF
         //DELAYDEFAULTTELA_MONTHS
         //DELAYDEFAULTTHOURS
         //DEFAULTTALLOCATION
         //DEFAULTTWORK_FROM_ACT_FACTOR
         //DEFAULTTEFFORT
         //DEFAULTTWORL
         //DEFAULTTWORK_RATE
         //DEFAULTTWORK_UNIT
         //DEFAULTTWORK_RATE_TIME_UNIT
         //DEFAULTTEFFORT_TIMG_UNIT
         //DEFAULTTDURATION_TIMF_UNIT
         //DEFAULTTDELAY_TIME_UNIT
         //DEFAULTTTYPL
         //DEFAULTTCALCULATED_PARAMETER
         //DEFAULTTBALANCING_PARAMETER
         //DEFAULTTWORK_RATE_TYPE
         //DEFAULTTUSE_TASK_CALENDAR
         //DEFAULTTALLOC_PROPORTIONALLY
         //DEFAULTTCAN_BE_SPLIT
         //DEFAULTTCAN_BE_DELAYED
         //DEFAULTTCAN_BE_STRETCHED
         //DEFAULTTACCOUNTED__ELSEWHERE
         //DEFAULTTCONTRIBUTES_T_EFFORT
         //DEFAULTTMAY_BE_SHORTER__TASK
         //DEFAULTTSHARED_EFFORT
         //ABILITY
         //EFFECTIVENESS
         //AVAILABLF_FROM
         //AVAILABLF_TO
         //SPARO_INTEGER
         //EFFORT_TIMF_UNIT
         //ROLE
         //CREATED_AS_FOLDER
         //perm_resource_skill_ALT_ID
         //perm_resource_skill_LAST_EDITED_DATE
         //perm_resource_skill_LAST_EDITED_BY

      }
   }

   /**
    * Convert an integer into a RelationType instance.
    * 
    * @param index integer value
    * @return RelationType instance
    */
   private RelationType getRelationType(int index)
   {
      if (index < 0 || index > RELATION_TYPES.length)
      {
         index = 0;
      }

      return RELATION_TYPES[index];
   }

   /**
    * Convert a name into initials.
    * 
    * @param name source name
    * @return initials
    */
   private String getInitials(String name)
   {
      String result = null;

      if (name != null && name.length() != 0)
      {
         StringBuffer sb = new StringBuffer();
         sb.append(name.charAt(0));
         int index = 1;
         while (true)
         {
            index = name.indexOf(' ', index);
            if (index == -1)
            {
               break;
            }

            ++index;
            if (index < name.length() && name.charAt(index) != ' ')
            {
               sb.append(name.charAt(index));
            }

            ++index;
         }

         result = sb.toString();
      }

      return result;
   }

   /**
    * Asta Powerproject assigns an explicit calendar for each task. This method 
    * is used to find the most common calendar and use this as the default project
    * calendar. This allows the explicitly assigned task calendars to be removed.
    */
   private void deriveProjectCalendar()
   {
      //
      // Count the number of times each calendar is used
      //
      Map<ProjectCalendar, Integer> map = new HashMap<ProjectCalendar, Integer>();
      for (Task task : m_project.getAllTasks())
      {
         ProjectCalendar calendar = task.getCalendar();
         Integer count = map.get(calendar);
         if (count == null)
         {
            count = Integer.valueOf(1);
         }
         else
         {
            count = Integer.valueOf(count.intValue() + 1);
         }
         map.put(calendar, count);
      }

      //
      // Find the most frequently used calendar
      //
      int maxCount = 0;
      ProjectCalendar defaultCalendar = null;

      for (Entry<ProjectCalendar, Integer> entry : map.entrySet())
      {
         if (entry.getValue().intValue() > maxCount)
         {
            maxCount = entry.getValue().intValue();
            defaultCalendar = entry.getKey();
         }
      }

      //
      // Set the default calendar for the project
      // and remove it's use as a task-specific calendar.
      //
      if (defaultCalendar != null)
      {
         m_project.setCalendar(defaultCalendar);
         for (Task task : m_project.getAllTasks())
         {
            if (task.getCalendar() == defaultCalendar)
            {
               task.setCalendar(null);
            }
         }
      }
   }

   /**
    * Determines the constraints relating to a task.
    * 
    * @param row row data
    * @param task Task instance
    */
   private void processConstraints(Row row, Task task)
   {
      ConstraintType constraintType = ConstraintType.AS_SOON_AS_POSSIBLE;
      Date constraintDate = null;

      switch (row.getInt("CONSTRAINU"))
      {
         case 0:
         {
            if (row.getInt("PLACEMENT") == 0)
            {
               constraintType = ConstraintType.AS_SOON_AS_POSSIBLE;
            }
            else
            {
               constraintType = ConstraintType.AS_LATE_AS_POSSIBLE;
            }
            break;
         }

         case 1:
         {
            constraintType = ConstraintType.MUST_START_ON;
            constraintDate = row.getDate("START_CONSTRAINT_DATE");
            break;
         }

         case 2:
         {
            constraintType = ConstraintType.START_NO_LATER_THAN;
            constraintDate = row.getDate("START_CONSTRAINT_DATE");
            break;
         }

         case 3:
         {
            constraintType = ConstraintType.START_NO_EARLIER_THAN;
            constraintDate = row.getDate("START_CONSTRAINT_DATE");
            break;
         }

         case 4:
         {
            constraintType = ConstraintType.MUST_FINISH_ON;
            constraintDate = row.getDate("END_CONSTRAINT_DATE");
            break;
         }

         case 5:
         {
            constraintType = ConstraintType.FINISH_NO_LATER_THAN;
            constraintDate = row.getDate("END_CONSTRAINT_DATE");
            break;
         }

         case 6:
         {
            constraintType = ConstraintType.FINISH_NO_EARLIER_THAN;
            constraintDate = row.getDate("END_CONSTRAINT_DATE");
            break;
         }

         case 8:
         {
            task.setDeadline(row.getDate("END_CONSTRAINT_DATE"));
            break;
         }
      }

      task.setConstraintType(constraintType);
      task.setConstraintDate(constraintDate);
   }

   private ProjectFile m_project;

   private static final Double COMPLETE = Double.valueOf(100);
   private static final Double INCOMPLETE = Double.valueOf(0);
   private static final Duration ZERO_HOURS = Duration.getInstance(0, TimeUnit.HOURS);

   private static final RelationType[] RELATION_TYPES =
   {
      RelationType.FINISH_START,
      RelationType.START_START,
      RelationType.FINISH_FINISH,
      RelationType.START_FINISH
   };
}