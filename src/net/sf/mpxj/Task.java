/*
 * file:       Task.java
 * author:     Scott Melville
 *             Jon Iles
 * copyright:  (c) Packwood Software 2002-2003
 * date:       15/08/2002
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

package net.sf.mpxj;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import net.sf.mpxj.listener.FieldListener;
import net.sf.mpxj.utility.BooleanUtility;
import net.sf.mpxj.utility.DateUtility;
import net.sf.mpxj.utility.NumberUtility;

/**
 * This class represents a task record from an project file.
 */
public final class Task extends ProjectEntity implements Comparable<Task>, FieldContainer
{
   /**
    * Default constructor.
    *
    * @param file Parent file to which this record belongs.
    * @param parent Parent task
    */
   Task(ProjectFile file, Task parent)
   {
      super(file);

      setType(TaskType.FIXED_UNITS);
      setConstraintType(ConstraintType.AS_SOON_AS_POSSIBLE);
      setTaskMode(TaskMode.AUTO_SCHEDULED);
      setActive(true);

      m_parent = parent;

      if (file.getAutoTaskUniqueID() == true)
      {
         setUniqueID(Integer.valueOf(file.getTaskUniqueID()));
      }

      if (file.getAutoTaskID() == true)
      {
         setID(Integer.valueOf(file.getTaskID()));
      }

      if (file.getAutoWBS() == true)
      {
         generateWBS(parent);
      }

      if (file.getAutoOutlineNumber() == true)
      {
         generateOutlineNumber(parent);
      }

      if (file.getAutoOutlineLevel() == true)
      {
         if (parent == null)
         {
            setOutlineLevel(Integer.valueOf(1));
         }
         else
         {
            setOutlineLevel(Integer.valueOf(NumberUtility.getInt(parent.getOutlineLevel()) + 1));
         }
      }
   }

   /**
    * This method is used to automatically generate a value
    * for the WBS field of this task.
    *
    * @param parent Parent Task
    */
   public void generateWBS(Task parent)
   {
      String wbs;

      if (parent == null)
      {
         if (NumberUtility.getInt(getUniqueID()) == 0)
         {
            wbs = "0";
         }
         else
         {
            wbs = Integer.toString(getParentFile().getChildTaskCount() + 1);
         }
      }
      else
      {
         wbs = parent.getWBS();

         int index = wbs.lastIndexOf(".0");

         if (index != -1)
         {
            wbs = wbs.substring(0, index);
         }

         if (wbs.equals("0") == true)
         {
            wbs = Integer.toString(parent.getChildTaskCount() + 1);
         }
         else
         {
            wbs += ("." + (parent.getChildTaskCount() + 1));
         }
      }

      setWBS(wbs);
   }

   /**
    * This method is used to automatically generate a value
    * for the Outline Number field of this task.
    *
    * @param parent Parent Task
    */
   public void generateOutlineNumber(Task parent)
   {
      String outline;

      if (parent == null)
      {
         if (NumberUtility.getInt(getUniqueID()) == 0)
         {
            outline = "0";
         }
         else
         {
            outline = Integer.toString(getParentFile().getChildTaskCount() + 1);
         }
      }
      else
      {
         outline = parent.getOutlineNumber();

         int index = outline.lastIndexOf(".0");

         if (index != -1)
         {
            outline = outline.substring(0, index);
         }

         if (outline.equals("0") == true)
         {
            outline = Integer.toString(parent.getChildTaskCount() + 1);
         }
         else
         {
            outline += ("." + (parent.getChildTaskCount() + 1));
         }
      }

      setOutlineNumber(outline);
   }

   /**
    * This method is used to add notes to the current task.
    *
    * @param notes notes to be added
    */
   public void setNotes(String notes)
   {
      set(TaskField.NOTES, notes);
   }

   /**
    * This method allows nested tasks to be added, with the WBS being
    * completed automatically.
    *
    * @return new task
    */
   public Task addTask()
   {
      ProjectFile parent = getParentFile();

      Task task = new Task(parent, this);

      m_children.add(task);

      parent.addTask(task);

      setSummary(true);

      return (task);
   }

   /**
    * This method is used to associate a child task with the current
    * task instance. It has package access, and has been designed to
    * allow the hierarchical outline structure of tasks in an MPX
    * file to be constructed as the file is read in.
    *
    * @param child Child task.
    * @param childOutlineLevel Outline level of the child task.
    */
   public void addChildTask(Task child, int childOutlineLevel)
   {
      int outlineLevel = NumberUtility.getInt(getOutlineLevel());

      if ((outlineLevel + 1) == childOutlineLevel)
      {
         m_children.add(child);
      }
      else
      {
         if (m_children.isEmpty() == false)
         {
            (m_children.get(m_children.size() - 1)).addChildTask(child, childOutlineLevel);
         }
      }
   }

   /**
    * This method is used to associate a child task with the current
    * task instance. It has been designed to
    * allow the hierarchical outline structure of tasks in an MPX
    * file to be updated once all of the task data has been read.
    *
    * @param child child task
    */
   public void addChildTask(Task child)
   {
      child.m_parent = this;
      m_children.add(child);
   }

   /**
    * Removes a child task.
    *
    * @param child child task instance
    */
   public void removeChildTask(Task child)
   {
      if (m_children.remove(child))
      {
         child.m_parent = null;
      }
      setSummary(!m_children.isEmpty());
   }

   /**
    * This method allows the list of child tasks to be cleared in preparation
    * for the hierarchical task structure to be built.
    */
   public void clearChildTasks()
   {
      m_children.clear();
   }

   /**
    * This method allows recurring task details to be added to the
    * current task.
    *
    * @return RecurringTask object
    */
   public RecurringTask addRecurringTask()
   {
      if (m_recurringTask == null)
      {
         m_recurringTask = new RecurringTask();
      }

      return (m_recurringTask);
   }

   /**
    * This method retrieves the recurring task record. If the current
    * task is not a recurring task, then this method will return null.
    *
    * @return Recurring task record.
    */
   public RecurringTask getRecurringTask()
   {
      return (m_recurringTask);
   }

   /**
    * This method allows a resource assignment to be added to the
    * current task.
    *
    * @param resource the resource to assign
    * @return ResourceAssignment object
    */
   public ResourceAssignment addResourceAssignment(Resource resource)
   {
      ResourceAssignment assignment = null;
      Integer resourceUniqueID = null;

      if (resource != null)
      {
         Iterator<ResourceAssignment> iter = m_assignments.iterator();
         resourceUniqueID = resource.getUniqueID();

         while (iter.hasNext() == true)
         {
            assignment = iter.next();
            Integer uniqueID = assignment.getResourceUniqueID();
            if (uniqueID.equals(resourceUniqueID) == true)
            {
               break;
            }
            assignment = null;
         }
      }

      if (assignment == null)
      {
         assignment = new ResourceAssignment(getParentFile(), this);
         m_assignments.add(assignment);
         getParentFile().addResourceAssignment(assignment);

         assignment.setResourceUniqueID(resourceUniqueID);
         assignment.setWork(getDuration());
         assignment.setUnits(ResourceAssignment.DEFAULT_UNITS);

         if (resource != null)
         {
            resource.addResourceAssignment(assignment);
         }
      }

      return (assignment);
   }

   /**
    * This method allows the list of resource assignments for this
    * task to be retrieved.
    *
    * @return list of resource assignments
    */
   public List<ResourceAssignment> getResourceAssignments()
   {
      return (m_assignments);
   }

   /**
    * Internal method used as part of the process of removing a
    * resource assignment.
    *
    * @param assignment resource assignment to be removed
    */
   void removeResourceAssignment(ResourceAssignment assignment)
   {
      m_assignments.remove(assignment);
   }

   /**
    * This method allows a predecessor relationship to be added to this
    * task instance.
    *
    * @param targetTask the predecessor task
    * @param type relation type
    * @param lag relation lag
    * @return relationship
    */
   @SuppressWarnings("unchecked") public Relation addPredecessor(Task targetTask, RelationType type, Duration lag)
   {
      //
      // Ensure that we have a valid lag duration
      //
      if (lag == null)
      {
         lag = Duration.getInstance(0, TimeUnit.DAYS);
      }

      //
      // Retrieve the list of predecessors
      //
      List<Relation> predecessorList = (List<Relation>) getCachedValue(TaskField.PREDECESSORS);
      if (predecessorList == null)
      {
         predecessorList = new LinkedList<Relation>();
         set(TaskField.PREDECESSORS, predecessorList);
      }

      //
      // Ensure that there is only one predecessor relationship between
      // these two tasks.
      //
      Relation predecessorRelation = null;
      Iterator<Relation> iter = predecessorList.iterator();
      while (iter.hasNext() == true)
      {
         predecessorRelation = iter.next();
         if (predecessorRelation.getTargetTask() == targetTask)
         {
            if (predecessorRelation.getType() != type || predecessorRelation.getLag().compareTo(lag) != 0)
            {
               predecessorRelation = null;
            }
            break;
         }
         predecessorRelation = null;
      }

      //
      // If necessary, create a new predecessor relationship
      //
      if (predecessorRelation == null)
      {
         predecessorRelation = new Relation(this, targetTask, type, lag);
         predecessorList.add(predecessorRelation);
      }

      //
      // Retrieve the list of successors
      //
      List<Relation> successorList = (List<Relation>) targetTask.getCachedValue(TaskField.SUCCESSORS);
      if (successorList == null)
      {
         successorList = new LinkedList<Relation>();
         targetTask.set(TaskField.SUCCESSORS, successorList);
      }

      //
      // Ensure that there is only one successor relationship between
      // these two tasks.
      //
      Relation successorRelation = null;
      iter = successorList.iterator();
      while (iter.hasNext() == true)
      {
         successorRelation = iter.next();
         if (successorRelation.getTargetTask() == this)
         {
            if (successorRelation.getType() != type || successorRelation.getLag().compareTo(lag) != 0)
            {
               successorRelation = null;
            }
            break;
         }
         successorRelation = null;
      }

      //
      // If necessary, create a new successor relationship
      //
      if (successorRelation == null)
      {
         successorRelation = new Relation(targetTask, this, type, lag);
         successorList.add(successorRelation);
      }

      return (predecessorRelation);
   }

   /**
    * The % Complete field contains the current status of a task, expressed
    * as the percentage of the
    * task's duration that has been completed. You can enter percent complete,
    * or you can have
    * Microsoft Project calculate it for you based on actual duration.
    *
    * @param val value to be set
    */
   public void setPercentageComplete(Number val)
   {
      set(TaskField.PERCENT_COMPLETE, val);
   }

   /**
    * The % Work Complete field contains the current status of a task,
    * expressed as the
    * percentage of the task's work that has been completed. You can enter
    * percent work
    * complete, or you can have Microsoft Project calculate it for you
    * based on actual
    * work on the task.
    *
    * @param val value to be set
    */
   public void setPercentageWorkComplete(Number val)
   {
      set(TaskField.PERCENT_WORK_COMPLETE, val);
   }

   /**
    * The Actual Cost field shows costs incurred for work already performed
    * by all resources
    * on a task, along with any other recorded costs associated with the task.
    * You can enter
    * all the actual costs or have Microsoft Project calculate them for you.
    *
    * @param val value to be set
    */
   public void setActualCost(Number val)
   {
      set(TaskField.ACTUAL_COST, val);
   }

   /**
    * The Actual Duration field shows the span of actual working time for a
    * task so far,
    * based on the scheduled duration and current remaining work or
    * completion percentage.
    *
    * @param val value to be set
    */
   public void setActualDuration(Duration val)
   {
      set(TaskField.ACTUAL_DURATION, val);
   }

   /**
    * The Actual Finish field shows the date and time that a task actually
    * finished.
    * Microsoft Project sets the Actual Finish field to the scheduled finish
    * date if
    * the completion percentage is 100. This field contains "NA" until you
    * enter actual
    * information or set the completion percentage to 100.
    *
    * @param val value to be set
    */
   public void setActualFinish(Date val)
   {
      set(TaskField.ACTUAL_FINISH, val);
   }

   /**
    * The Actual Start field shows the date and time that a task actually began.
    * When a task is first created, the Actual Start field contains "NA." Once you
    * enter the first actual work or a completion percentage for a task, Microsoft
    * Project sets the actual start date to the scheduled start date.
    * @param val value to be set
    */
   public void setActualStart(Date val)
   {
      set(TaskField.ACTUAL_START, val);
   }

   /**
    * The Actual Work field shows the amount of work that has already been
    * done by the
    * resources assigned to a task.
    * @param val value to be set
    */
   public void setActualWork(Duration val)
   {
      set(TaskField.ACTUAL_WORK, val);
   }

   /**
    * The Baseline Cost field shows the total planned cost for a task.
    * Baseline cost is also referred to as budget at completion (BAC).
    *
    * @param val the amount to be set
    */
   public void setBaselineCost(Number val)
   {
      set(TaskField.BASELINE_COST, val);
   }

   /**
    * The Baseline Duration field shows the original span of time planned to
    * complete a task.
    *
    * @param val duration
    */
   public void setBaselineDuration(Duration val)
   {
      set(TaskField.BASELINE_DURATION, val);
   }

   /**
    * The Baseline Finish field shows the planned completion date for a
    * task at the time
    * you saved a baseline. Information in this field becomes available
    * when you set a
    * baseline for a task.
    *
    * @param val Date to be set
    */
   public void setBaselineFinish(Date val)
   {
      set(TaskField.BASELINE_FINISH, val);
   }

   /**
    * The Baseline Start field shows the planned beginning date for a task at
    * the time
    * you saved a baseline. Information in this field becomes available when you
    * set a baseline.
    *
    * @param val Date to be set
    */
   public void setBaselineStart(Date val)
   {
      set(TaskField.BASELINE_START, val);
   }

   /**
    * The Baseline Work field shows the originally planned amount of work to
    * be performed
    * by all resources assigned to a task. This field shows the planned
    * person-hours
    * scheduled for a task. Information in the Baseline Work field
    * becomes available
    * when you set a baseline for the project.
    *
    * @param val the duration to be set.
    */
   public void setBaselineWork(Duration val)
   {
      set(TaskField.BASELINE_WORK, val);
   }

   /**
    * The BCWP (budgeted cost of work performed) field contains the
    * cumulative value
    * of the assignment's timephased percent complete multiplied by
    * the assignments
    * timephased baseline cost. BCWP is calculated up to the status
    * date or todays
    * date. This information is also known as earned value.
    *
    * @param val the amount to be set
    */
   public void setBCWP(Number val)
   {
      set(TaskField.BCWP, val);
   }

   /**
    * The BCWS (budgeted cost of work scheduled) field contains the cumulative
    * timephased baseline costs up to the status date or today's date.
    *
    * @param val the amount to set
    */
   public void setBCWS(Number val)
   {
      set(TaskField.BCWS, val);
   }

   /**
    * The Confirmed field indicates whether all resources assigned to a task have
    * accepted or rejected the task assignment in response to a TeamAssign message
    * regarding their assignments.
    *
    * @param val boolean value
    */
   public void setConfirmed(boolean val)
   {
      set(TaskField.CONFIRMED, val);
   }

   /**
    * The Constraint Date field shows the specific date associated with certain
    * constraint types,
    *  such as Must Start On, Must Finish On, Start No Earlier Than,
    *  Start No Later Than,
    *  Finish No Earlier Than, and Finish No Later Than.
    *  SEE class constants
    *
    * @param val Date to be set
    */
   public void setConstraintDate(Date val)
   {
      set(TaskField.CONSTRAINT_DATE, val);
   }

   /**
    * Private method for dealing with string parameters from File.
    *
    * @param type string constraint type
    */
   public void setConstraintType(ConstraintType type)
   {
      set(TaskField.CONSTRAINT_TYPE, type);
   }

   /**
    * The Contact field contains the name of an individual
    * responsible for a task.
    *
    * @param val value to be set
    */
   public void setContact(String val)
   {
      set(TaskField.CONTACT, val);
   }

   /**
    * The Cost field shows the total scheduled, or projected, cost for a task,
    * based on costs already incurred for work performed by all resources assigned
    * to the task, in addition to the costs planned for the remaining work for the
    * assignment. This can also be referred to as estimate at completion (EAC).
    *
    * @param val amount
    */
   public void setCost(Number val)
   {
      set(TaskField.COST, val);
   }

   /**
    * The Cost1-10 fields show any custom task cost information you want to enter
    * in your project.
    *
    * @param val amount
    */
   public void setCost1(Number val)
   {
      set(TaskField.COST1, val);
   }

   /**
    * The Cost1-10 fields show any custom task cost information you want to
    * enter in your project.
    *
    * @param val amount
    */
   public void setCost2(Number val)
   {
      set(TaskField.COST2, val);
   }

   /**
    * The Cost1-10 fields show any custom task cost information you want to
    * enter in your project.
    *
    * @param val amount
    */
   public void setCost3(Number val)
   {
      set(TaskField.COST3, val);
   }

   /**
    * The Cost Variance field shows the difference between the
    * baseline cost and total cost for a task. The total cost is the
    * current estimate of costs based on actual costs and remaining costs.
    * This is also referred to as variance at completion (VAC).
    *
    * @param val amount
    */
   public void setCostVariance(Number val)
   {
      set(TaskField.COST_VARIANCE, val);
   }

   /**
    * The Created field contains the date and time when a task was
    * added to the project.
    *
    * @param val date
    */
   public void setCreateDate(Date val)
   {
      set(TaskField.CREATED, val);
   }

   /**
    * The Critical field indicates whether a task has any room in the
    * schedule to slip,
    * or if a task is on the critical path. The Critical field contains
    * Yes if the task
    * is critical and No if the task is not critical.
    *
    * @param val whether task is critical or not
    */
   public void setCritical(boolean val)
   {
      set(TaskField.CRITICAL, val);
   }

   /**
    * The CV (earned value cost variance) field shows the difference
    * between how much it should have cost to achieve the current level of
    * completion on the task, and how much it has actually cost to achieve the
    * current level of completion up to
    * the status date or today's date.
    *
    * @param val value to set
    */
   public void setCV(Number val)
   {
      set(TaskField.CV, val);
   }

   /**
    * Set amount of delay as elapsed real time.
    *
    * @param val elapsed time
    */
   public void setLevelingDelay(Duration val)
   {
      set(TaskField.LEVELING_DELAY, val);
   }

   /**
    * The Duration field is the total span of active working time for a task.
    * This is generally the amount of time from the start to the finish of a task.
    * The default for new tasks is 1 day (1d).
    *
    * @param val duration
    */
   public void setDuration(Duration val)
   {
      set(TaskField.DURATION, val);
   }

   /**
    * Set the duration text used for a manually scheduled task.
    * 
    * @param val text
    */
   public void setDurationText(String val)
   {
      set(TaskField.DURATION_TEXT, val);
   }

   /**
    * User defined duration field.
    *
    * @param duration Duration value
    */
   public void setDuration1(Duration duration)
   {
      set(TaskField.DURATION1, duration);
   }

   /**
    * User defined duration field.
    *
    * @param duration Duration value
    */
   public void setDuration2(Duration duration)
   {
      set(TaskField.DURATION2, duration);
   }

   /**
    * User defined duration field.
    *
    * @param duration Duration value
    */
   public void setDuration3(Duration duration)
   {
      set(TaskField.DURATION3, duration);
   }

   /**
    * The Duration Variance field contains the difference between the
    * baseline duration of a task and the forecast or actual duration
    * of the task.
    *
    * @param duration duration value
    */
   public void setDurationVariance(Duration duration)
   {
      set(TaskField.DURATION_VARIANCE, duration);
   }

   /**
    * The Early Finish field contains the earliest date that a task
    * could possibly finish, based on early finish dates of predecessor
    * and successor tasks, other constraints, and any leveling delay.
    *
    * @param date Date value
    */
   public void setEarlyFinish(Date date)
   {
      set(TaskField.EARLY_FINISH, date);
   }

   /**
    * The Early Start field contains the earliest date that a task could
    * possibly begin, based on the early start dates of predecessor and
    * successor tasks, and other constraints.
    *
    * @param date Date value
    */
   public void setEarlyStart(Date date)
   {
      set(TaskField.EARLY_START, date);
   }

   /**
    * The Finish field shows the date and time that a task is scheduled to be
    * completed. MS project allows a finish date to be entered, and will
    * calculate the duration, or a duration can be supplied and MS Project
    * will calculate the finish date.
    *
    * @param date Date value
    */
   public void setFinish(Date date)
   {
      set(TaskField.FINISH, date);
   }

   /**
    * Set the finish text used for a manually scheduled task.
    * 
    * @param val text
    */
   public void setFinishText(String val)
   {
      set(TaskField.FINISH_TEXT, val);
   }

   /**
    * User defined finish date field.
    *
    * @param date Date value
    */
   public void setFinish1(Date date)
   {
      set(TaskField.FINISH1, date);
   }

   /**
    * User defined finish date field.
    *
    * @param date Date value
    */
   public void setFinish2(Date date)
   {
      set(TaskField.FINISH2, date);
   }

   /**
    * User defined finish date field.
    *
    * @param date Date value
    */
   public void setFinish3(Date date)
   {
      set(TaskField.FINISH3, date);
   }

   /**
    * User defined finish date field.
    *
    * @param date Date value
    */
   public void setFinish4(Date date)
   {
      set(TaskField.FINISH4, date);
   }

   /**
    * User defined finish date field.
    *
    * @param date Date value
    */
   public void setFinish5(Date date)
   {
      set(TaskField.FINISH5, date);
   }

   /**
    * The Finish Variance field contains the amount of time that represents the
    * difference between a task's baseline finish date and its forecast
    * or actual finish date.
    *
    * @param duration duration value
    */
   public void setFinishVariance(Duration duration)
   {
      set(TaskField.FINISH_VARIANCE, duration);
   }

   /**
    * The Fixed Cost field shows any task expense that is not associated
    * with a resource cost.
    *
    * @param val amount
    */
   public void setFixedCost(Number val)
   {
      set(TaskField.FIXED_COST, val);
   }

   /**
    * User defined flag field.
    *
    * @param val boolean value
    */
   public void setFlag1(boolean val)
   {
      set(TaskField.FLAG1, val);
   }

   /**
    * User defined flag field.
    *
    * @param val boolean value
    */
   public void setFlag2(boolean val)
   {
      set(TaskField.FLAG2, val);
   }

   /**
    * User defined flag field.
    *
    * @param val boolean value
    */
   public void setFlag3(boolean val)
   {
      set(TaskField.FLAG3, val);
   }

   /**
    * User defined flag field.
    *
    * @param val boolean value
    */
   public void setFlag4(boolean val)
   {
      set(TaskField.FLAG4, val);
   }

   /**
    * User defined flag field.
    *
    * @param val boolean value
    */
   public void setFlag5(boolean val)
   {
      set(TaskField.FLAG5, val);
   }

   /**
    * User defined flag field.
    *
    * @param val boolean value
    */
   public void setFlag6(boolean val)
   {
      set(TaskField.FLAG6, val);
   }

   /**
    * User defined flag field.
    *
    * @param val boolean value
    */
   public void setFlag7(boolean val)
   {
      set(TaskField.FLAG7, val);
   }

   /**
    * User defined flag field.
    *
    * @param val boolean value
    */
   public void setFlag8(boolean val)
   {
      set(TaskField.FLAG8, val);
   }

   /**
    * User defined flag field.
    *
    * @param val boolean value
    */
   public void setFlag9(boolean val)
   {
      set(TaskField.FLAG9, val);
   }

   /**
    * User defined flag field.
    *
    * @param val boolean value
    */
   public void setFlag10(boolean val)
   {
      set(TaskField.FLAG10, val);
   }

   /**
    * The Free Slack field contains the amount of time that a task can be
    * delayed without delaying any successor tasks. If the task has no
    * successors, free slack is the amount of time that a task can be delayed
    * without delaying the entire project's finish date.
    *
    * @param duration duration value
    */
   public void setFreeSlack(Duration duration)
   {
      set(TaskField.FREE_SLACK, duration);
   }

   /**
    * The Hide Bar flag indicates whether the Gantt bars and Calendar bars
    * for a task are hidden when this project's data is displayed in MS Project.
    *
    * @param flag boolean value
    */
   public void setHideBar(boolean flag)
   {
      set(TaskField.HIDEBAR, flag);
   }

   /**
    * The ID field contains the identifier number that Microsoft Project
    * automatically assigns to each task as you add it to the project.
    * The ID indicates the position of a task with respect to the other tasks.
    *
    * @param val ID
    */
   public void setID(Integer val)
   {
      ProjectFile parent = getParentFile();
      Integer previous = getID();

      if (previous != null)
      {
         parent.unmapTaskID(previous);
      }

      parent.mapTaskID(val, this);

      set(TaskField.ID, val);
   }

   /**
    * The Late Finish field contains the latest date that a task can finish
    * without delaying the finish of the project. This date is based on the
    * task's late start date, as well as the late start and late finish dates
    * of predecessor and successor tasks, and other constraints.
    *
    * @param date date value
    */
   public void setLateFinish(Date date)
   {
      set(TaskField.LATE_FINISH, date);
   }

   /**
    * The Late Start field contains the latest date that a task can start
    * without delaying the finish of the project. This date is based on the
    * task's start date, as well as the late start and late finish dates of
    * predecessor and successor tasks, and other constraints.
    *
    * @param date date value
    */
   public void setLateStart(Date date)
   {
      set(TaskField.LATE_START, date);
   }

   /**
    * The Linked Fields field indicates whether there are OLE links to the task,
    * either from elsewhere in the active project, another Microsoft Project
    * file, or from another program.
    *
    * @param flag boolean value
    */
   public void setLinkedFields(boolean flag)
   {
      set(TaskField.LINKED_FIELDS, flag);
   }

   /**
    * This is a user defined field used to mark a task for some form of
    * additional action.
    *
    * @param flag boolean value
    */
   public void setMarked(boolean flag)
   {
      set(TaskField.MARKED, flag);
   }

   /**
    * The Milestone field indicates whether a task is a milestone.
    *
    * @param flag boolean value
    */
   public void setMilestone(boolean flag)
   {
      set(TaskField.MILESTONE, flag);
   }

   /**
    * The Name field contains the name of a task.
    *
    * @param name task name
    */
   public void setName(String name)
   {
      set(TaskField.NAME, name);
   }

   /**
    * Sets a numeric value.
    *
    * @param val Numeric value
    */
   public void setNumber1(Number val)
   {
      set(TaskField.NUMBER1, val);
   }

   /**
    * Sets a numeric value.
    *
    * @param val Numeric value
    */
   public void setNumber2(Number val)
   {
      set(TaskField.NUMBER2, val);
   }

   /**
    * Sets a numeric value.
    *
    * @param val Numeric value
    */
   public void setNumber3(Number val)
   {
      set(TaskField.NUMBER3, val);
   }

   /**
    * Sets a numeric value.
    *
    * @param val Numeric value
    */
   public void setNumber4(Number val)
   {
      set(TaskField.NUMBER4, val);
   }

   /**
    * Sets a numeric value.
    *
    * @param val Numeric value
    */
   public void setNumber5(Number val)
   {
      set(TaskField.NUMBER5, val);
   }

   /**
    * The Objects field contains the number of objects attached to a task.
    *
    * @param val - integer value
    */
   public void setObjects(Integer val)
   {
      set(TaskField.OBJECTS, val);
   }

   /**
    * The Outline Level field contains the number that indicates the level of
    * the task in the project outline hierarchy.
    *
    * @param val - int
    */
   public void setOutlineLevel(Integer val)
   {
      set(TaskField.OUTLINE_LEVEL, val);
   }

   /**
    * The Outline Number field contains the number of the task in the structure
    * of an outline. This number indicates the task's position within the
    * hierarchical structure of the project outline. The outline number is
    * similar to a WBS (work breakdown structure) number, except that the
    * outline number is automatically entered by Microsoft Project.
    *
    * @param val - text
    */
   public void setOutlineNumber(String val)
   {
      set(TaskField.OUTLINE_NUMBER, val);
   }

   /**
    * The Priority field provides choices for the level of importance
    * assigned to a task, which in turn indicates how readily a task can be
    * delayed or split during resource leveling.
    * The default priority is Medium. Those tasks with a priority
    * of Do Not Level are never delayed or split when Microsoft Project levels
    * tasks that have overallocated resources assigned.
    *
    * @param priority the priority value
    */
   public void setPriority(Priority priority)
   {
      set(TaskField.PRIORITY, priority);
   }

   /**
    * The Project field shows the name of the project from which a
    * task originated.
    * This can be the name of the active project file. If there are
    * other projects
    * inserted into the active project file, the name of the
    * inserted project appears
    * in this field for the task.
    *
    * @param val - text
    */
   public void setProject(String val)
   {
      set(TaskField.PROJECT, val);
   }

   /**
    * The Remaining Cost field shows the remaining scheduled expense of a task that
    * will be incurred in completing the remaining scheduled work by all resources
    * assigned to the task.
    *
    * @param val - currency amount
    */
   public void setRemainingCost(Number val)
   {
      set(TaskField.REMAINING_COST, val);
   }

   /**
    * The Remaining Duration field shows the amount of time required to complete
    * the unfinished portion of a task.
    *
    * @param val - duration.
    */
   public void setRemainingDuration(Duration val)
   {
      set(TaskField.REMAINING_DURATION, val);
   }

   /**
    * The Remaining Work field shows the amount of time, or person-hours,
    * still required by all assigned resources to complete a task.
    * @param val  - duration
    */
   public void setRemainingWork(Duration val)
   {
      set(TaskField.REMAINING_WORK, val);
   }

   /**
    * The Resource Group field contains the list of resource groups to which the
    * resources assigned to a task belong.
    *
    * @param val - String list
    */
   public void setResourceGroup(String val)
   {
      set(TaskField.RESOURCE_GROUP, val);
   }

   /**
    * The Resource Initials field lists the abbreviations for the names of
    * resources assigned to a task. These initials can serve as substitutes
    * for the names.
    *
    * Note that MS Project 98 does no normally populate this field when
    * it generates an MPX file, and will therefore not expect to see values
    * in this field when it reads an MPX file. Supplying values for this
    * field will cause MS Project 98, 2000, and 2002 to create new resources
    * and ignore any other resource assignments that have been defined
    * in the MPX file.
    *
    * @param val String containing a comma separated list of initials
    */
   public void setResourceInitials(String val)
   {
      set(TaskField.RESOURCE_INITIALS, val);
   }

   /**
    * The Resource Names field lists the names of all resources
    * assigned to a task.
    *
    * Note that MS Project 98 does no normally populate this field when
    * it generates an MPX file, and will therefore not expect to see values
    * in this field when it reads an MPX file. Supplying values for this
    * field will cause MS Project 98, 2000, and 2002 to create new resources
    * and ignore any other resource assignments that have been defined
    * in the MPX file.
    *
    * @param val String containing a comma separated list of names
    */
   public void setResourceNames(String val)
   {
      set(TaskField.RESOURCE_NAMES, val);
   }

   /**
    * The Resume field shows the date that the remaining portion of a task is
    * scheduled to resume after you enter a new value for the % Complete field.
    * The Resume field is also recalculated when the remaining portion of a task
    * is moved to a new date.
    *
    * @param val - Date
    */
   public void setResume(Date val)
   {
      set(TaskField.RESUME, val);
   }

   /**
    * For subtasks, the Rollup field indicates whether information on the subtask
    * Gantt bars will be rolled up to the summary task bar. For summary tasks, the
    * Rollup field indicates whether the summary task bar displays rolled up bars.
    * You must have the Rollup field for summary tasks set to Yes for any subtasks
    * to roll up to them.
    *
    * @param val - boolean
    */
   public void setRollup(boolean val)
   {
      set(TaskField.ROLLUP, val);
   }

   /**
    * The Start field shows the date and time that a task is scheduled to begin.
    * You can enter the start date you want, to indicate the date when the task
    * should begin. Or, you can have Microsoft Project calculate the start date.
    * @param val - Date
    */
   public void setStart(Date val)
   {
      set(TaskField.START, val);
   }

   /**
    * Set the start text used for a manually scheduled task.
    * 
    * @param val text
    */
   public void setStartText(String val)
   {
      set(TaskField.START_TEXT, val);
   }

   /**
    * The Start1 through Start10 fields are custom fields that show any specific
    * task start date information you want to enter and store separately in
    * your project.
    *
    * @param val - Date
    */
   public void setStart1(Date val)
   {
      set(TaskField.START1, val);
   }

   /**
    * The Start1 through Start10 fields are custom fields that show any specific
    * task start date information you want to enter and store separately in
    * your project.
    *
    * @param val - Date
    */
   public void setStart2(Date val)
   {
      set(TaskField.START2, val);
   }

   /**
    * The Start1 through Start10 fields are custom fields that show any specific
    * task start date information you want to enter and store separately in
    * your project.
    *
    * @param val - Date
    */
   public void setStart3(Date val)
   {
      set(TaskField.START3, val);
   }

   /**
    * The Start1 through Start10 fields are custom fields that show any specific
    * task start date information you want to enter and store separately in
    * your project.
    *
    * @param val - Date
    */
   public void setStart4(Date val)
   {
      set(TaskField.START4, val);
   }

   /**
    * The Start1 through Start10 fields are custom fields that show any specific
    * task start date information you want to enter and store separately in
    * your project.
    *
    * @param val - Date
    */
   public void setStart5(Date val)
   {
      set(TaskField.START5, val);
   }

   /**
    * The Start Variance field contains the amount of time that represents the
    * difference between a task's baseline start date and its currently
    * scheduled start date.
    *
    * @param val - duration
    */
   public void setStartVariance(Duration val)
   {
      set(TaskField.START_VARIANCE, val);
   }

   /**
    * The Stop field shows the date that represents the end of the actual
    * portion of a task. Typically, Microsoft Project calculates the stop date.
    * However, you can edit this date as well.
    *
    * @param val - Date
    */
   public void setStop(Date val)
   {
      set(TaskField.STOP, val);
   }

   /**
    * The Subproject File field contains the name of a project inserted into
    * the active project file. The Subproject File field contains the inserted
    * project's path and file name.
    *
    * @param val - String
    */
   public void setSubprojectName(String val)
   {
      set(TaskField.SUBPROJECT_FILE, val);
   }

   /**
    * The Summary field indicates whether a task is a summary task.
    *
    * @param val - boolean
    */
   public void setSummary(boolean val)
   {
      set(TaskField.SUMMARY, val);
   }

   /**
    * The SV (earned value schedule variance) field shows the difference
    * in cost terms between the current progress and the baseline plan
    * of the task up to the status date or today's date. You can use SV
    * to check costs to determine whether tasks are on schedule.
    * @param val - currency amount
    */
   public void setSV(Number val)
   {
      set(TaskField.SV, val);
   }

   /**
    * The Text1-30 fields show any custom text information you want to enter
    * in your project about tasks.
    *
    * @param val - String
    */
   public void setText1(String val)
   {
      set(TaskField.TEXT1, val);
   }

   /**
    * The Text1-30 fields show any custom text information you want to
    * enter in your project about tasks.
    *
    * @param val - String
    */
   public void setText2(String val)
   {
      set(TaskField.TEXT2, val);
   }

   /**
    * The Text1-30 fields show any custom text information you want to
    * enter in your project about tasks.
    *
    * @param val - String
    */
   public void setText3(String val)
   {
      set(TaskField.TEXT3, val);
   }

   /**
    * The Text1-30 fields show any custom text information you want to
    * enter in your project about tasks.
    *
    * @param val - String
    */
   public void setText4(String val)
   {
      set(TaskField.TEXT4, val);
   }

   /**
    * The Text1-30 fields show any custom text information you want to
    * enter in your project about tasks.
    *
    * @param val - String
    */
   public void setText5(String val)
   {
      set(TaskField.TEXT5, val);
   }

   /**
    * The Text1-30 fields show any custom text information you want to
    * enter in your project about tasks.
    *
    * @param val - String
    */
   public void setText6(String val)
   {
      set(TaskField.TEXT6, val);
   }

   /**
    * The Text1-30 fields show any custom text information you want to
    * enter in your project about tasks.
    *
    * @param val - String
    */
   public void setText7(String val)
   {
      set(TaskField.TEXT7, val);
   }

   /**
    * The Text1-30 fields show any custom text information you want to
    * enter in your project about tasks.
    *
    * @param val - String
    */
   public void setText8(String val)
   {
      set(TaskField.TEXT8, val);
   }

   /**
    * The Text1-30 fields show any custom text information you want to
    * enter in your project about tasks.
    *
    * @param val - String
    */
   public void setText9(String val)
   {
      set(TaskField.TEXT9, val);
   }

   /**
    * The Text1-30 fields show any custom text information you want to
    * enter in your project about tasks.
    *
    * @param val - String
    */
   public void setText10(String val)
   {
      set(TaskField.TEXT10, val);
   }

   /**
    * The Total Slack field contains the amount of time a task can be delayed
    * without delaying the project's finish date.
    *
    * @param val - duration
    */
   public void setTotalSlack(Duration val)
   {
      set(TaskField.TOTAL_SLACK, val);
   }

   /**
    * The Unique ID field contains the number that Microsoft Project
    * automatically designates whenever a new task is created.
    * This number indicates the sequence in which the task was created,
    * regardless of placement in the schedule.
    *
    * @param val unique ID
    */
   public void setUniqueID(Integer val)
   {
      ProjectFile parent = getParentFile();
      Integer previous = getUniqueID();

      if (previous != null)
      {
         parent.unmapTaskUniqueID(previous);
      }

      parent.mapTaskUniqueID(val, this);

      set(TaskField.UNIQUE_ID, val);
   }

   /**
    * The Update Needed field indicates whether a TeamUpdate message should
    * be sent to the assigned resources because of changes to the start date,
    * finish date, or resource reassignments of the task.
    *
    * @param val - boolean
    */
   public void setUpdateNeeded(boolean val)
   {
      set(TaskField.UPDATE_NEEDED, val);
   }

   /**
    * The work breakdown structure code. The WBS field contains an alphanumeric
    * code you can use to represent the task's position within the hierarchical
    * structure of the project. This field is similar to the outline number,
    * except that you can edit it.
    *
    * @param val - String
    */
   public void setWBS(String val)
   {
      set(TaskField.WBS, val);
   }

   /**
    * The Work field shows the total amount of work scheduled to be performed
    * on a task by all assigned resources. This field shows the total work,
    * or person-hours, for a task.
    *
    * @param val - duration
    */
   public void setWork(Duration val)
   {
      set(TaskField.WORK, val);
   }

   /**
    * The Work Variance field contains the difference between a task's baseline
    * work and the currently scheduled work.
    *
    * @param val - duration
    */
   public void setWorkVariance(Duration val)
   {
      set(TaskField.WORK_VARIANCE, val);
   }

   /**
    * The % Complete field contains the current status of a task,
    * expressed as the percentage of the task's duration that has been completed.
    * You can enter percent complete, or you can have Microsoft Project calculate
    * it for you based on actual duration.
    * @return percentage as float
    */
   public Number getPercentageComplete()
   {
      return ((Number) getCachedValue(TaskField.PERCENT_COMPLETE));
   }

   /**
    * The % Work Complete field contains the current status of a task,
    * expressed as the percentage of the task's work that has been completed.
    * You can enter percent work complete, or you can have Microsoft Project
    * calculate it for you based on actual work on the task.
    *
    * @return percentage as float
    */
   public Number getPercentageWorkComplete()
   {
      return ((Number) getCachedValue(TaskField.PERCENT_WORK_COMPLETE));
   }

   /**
    * The Actual Cost field shows costs incurred for work already performed
    * by all resources on a task, along with any other recorded costs associated
    * with the task. You can enter all the actual costs or have Microsoft Project
    * calculate them for you.
    *
    * @return currency amount as float
    */
   public Number getActualCost()
   {
      return ((Number) getCachedValue(TaskField.ACTUAL_COST));
   }

   /**
    * The Actual Duration field shows the span of actual working time for a
    * task so far, based on the scheduled duration and current remaining work
    * or completion percentage.
    *
    * @return duration string
    */
   public Duration getActualDuration()
   {
      return ((Duration) getCachedValue(TaskField.ACTUAL_DURATION));
   }

   /**
    * The Actual Finish field shows the date and time that a task actually
    * finished. Microsoft Project sets the Actual Finish field to the scheduled
    * finish date if the completion percentage is 100. This field contains "NA"
    * until you enter actual information or set the completion percentage to 100.
    * If "NA" is entered as value, arbitrary year zero Date is used. Date(0);
    *
    * @return Date
    */
   public Date getActualFinish()
   {
      return ((Date) getCachedValue(TaskField.ACTUAL_FINISH));
   }

   /**
    * The Actual Start field shows the date and time that a task actually began.
    * When a task is first created, the Actual Start field contains "NA." Once
    * you enter the first actual work or a completion percentage for a task,
    * Microsoft Project sets the actual start date to the scheduled start date.
    * If "NA" is entered as value, arbitrary year zero Date is used. Date(0);
    *
    * @return Date
    */
   public Date getActualStart()
   {
      return ((Date) getCachedValue(TaskField.ACTUAL_START));
   }

   /**
    * The Actual Work field shows the amount of work that has already been done
    * by the resources assigned to a task.
    *
    * @return duration string
    */
   public Duration getActualWork()
   {
      return ((Duration) getCachedValue(TaskField.ACTUAL_WORK));
   }

   /**
    * The Baseline Cost field shows the total planned cost for a task.
    * Baseline cost is also referred to as budget at completion (BAC).
    * @return currency amount as float
    */
   public Number getBaselineCost()
   {
      return ((Number) getCachedValue(TaskField.BASELINE_COST));
   }

   /**
    * The Baseline Duration field shows the original span of time planned
    * to complete a task.
    *
    * @return  - duration string
    */
   public Duration getBaselineDuration()
   {
      return ((Duration) getCachedValue(TaskField.BASELINE_DURATION));
   }

   /**
    * The Baseline Finish field shows the planned completion date for a task
    * at the time you saved a baseline. Information in this field becomes
    * available when you set a baseline for a task.
    *
    * @return Date
    */
   public Date getBaselineFinish()
   {
      return ((Date) getCachedValue(TaskField.BASELINE_FINISH));
   }

   /**
    * The Baseline Start field shows the planned beginning date for a task at
    * the time you saved a baseline. Information in this field becomes available
    * when you set a baseline.
    *
    * @return Date
    */
   public Date getBaselineStart()
   {
      return ((Date) getCachedValue(TaskField.BASELINE_START));
   }

   /**
    * The Baseline Work field shows the originally planned amount of work to be
    * performed by all resources assigned to a task. This field shows the planned
    * person-hours scheduled for a task. Information in the Baseline Work field
    * becomes available when you set a baseline for the project.
    *
    * @return Duration
    */
   public Duration getBaselineWork()
   {
      return ((Duration) getCachedValue(TaskField.BASELINE_WORK));
   }

   /**
    * The BCWP (budgeted cost of work performed) field contains
    * the cumulative value of the assignment's timephased percent complete
    * multiplied by the assignment's timephased baseline cost.
    * BCWP is calculated up to the status date or today's date.
    * This information is also known as earned value.
    *
    * @return currency amount as float
    */
   public Number getBCWP()
   {
      return ((Number) getCachedValue(TaskField.BCWP));
   }

   /**
    * The BCWS (budgeted cost of work scheduled) field contains the cumulative
    * timephased baseline costs up to the status date or today's date.
    *
    * @return currency amount as float
    */
   public Number getBCWS()
   {
      return ((Number) getCachedValue(TaskField.BCWS));
   }

   /**
    * The Confirmed field indicates whether all resources assigned to a task
    * have accepted or rejected the task assignment in response to a TeamAssign
    * message regarding their assignments.
    *
    * @return boolean
    */
   public boolean getConfirmed()
   {
      return (BooleanUtility.getBoolean((Boolean) getCachedValue(TaskField.CONFIRMED)));
   }

   /**
    * The Constraint Date field shows the specific date associated with certain
    * constraint types, such as Must Start On, Must Finish On,
    * Start No Earlier Than,
    * Start No Later Than, Finish No Earlier Than, and Finish No Later Than.
    *
    * @return Date
    */
   public Date getConstraintDate()
   {
      return ((Date) getCachedValue(TaskField.CONSTRAINT_DATE));
   }

   /**
    * The Constraint Type field provides choices for the type of constraint you
    * can apply for scheduling a task.
    *
    * @return constraint type
    */
   public ConstraintType getConstraintType()
   {
      return ((ConstraintType) getCachedValue(TaskField.CONSTRAINT_TYPE));
   }

   /**
    * The Contact field contains the name of an individual
    * responsible for a task.
    *
    * @return String
    */
   public String getContact()
   {
      return ((String) getCachedValue(TaskField.CONTACT));
   }

   /**
    * The Cost field shows the total scheduled, or projected, cost for a task,
    * based on costs already incurred for work performed by all resources assigned
    * to the task, in addition to the costs planned for the remaining work for the
    * assignment. This can also be referred to as estimate at completion (EAC).
    *
    * @return cost amount
    */
   public Number getCost()
   {
      return ((Number) getCachedValue(TaskField.COST));
   }

   /**
    * The Cost1-10 fields show any custom task cost information you
    * want to enter in your project.
    *
    * @return cost amount
    */
   public Number getCost1()
   {
      return ((Number) getCachedValue(TaskField.COST1));
   }

   /**
    * The Cost1-10 fields show any custom task cost information
    * you want to enter in your project.
    *
    * @return amount
    */
   public Number getCost2()
   {
      return ((Number) getCachedValue(TaskField.COST2));
   }

   /**
    * The Cost1-10 fields show any custom task cost information you want to enter
    * in your project.
    *
    * @return amount
    */
   public Number getCost3()
   {
      return ((Number) getCachedValue(TaskField.COST3));
   }

   /**
    * The Cost Variance field shows the difference between the baseline cost
    * and total cost for a task. The total cost is the current estimate of costs
    * based on actual costs and remaining costs. This is also referred to as
    * variance at completion (VAC).
    *
    * @return amount
    */
   public Number getCostVariance()
   {
      Number variance = (Number) getCachedValue(TaskField.COST_VARIANCE);
      if (variance == null)
      {
         Number cost = getCost();
         Number baselineCost = getBaselineCost();
         if (cost != null && baselineCost != null)
         {
            variance = NumberUtility.getDouble(cost.doubleValue() - baselineCost.doubleValue());
            set(TaskField.COST_VARIANCE, variance);
         }
      }
      return (variance);
   }

   /**
    * The Created field contains the date and time when a task was added
    * to the project.
    *
    * @return Date
    */
   public Date getCreateDate()
   {
      return ((Date) getCachedValue(TaskField.CREATED));
   }

   /**
    * The Critical field indicates whether a task has any room in the schedule
    * to slip, or if a task is on the critical path. The Critical field contains
    * Yes if the task is critical and No if the task is not critical.
    *
    * @return boolean
    */
   public boolean getCritical()
   {
      Boolean critical = (Boolean) getCachedValue(TaskField.CRITICAL);
      if (critical == null)
      {
         Duration totalSlack = getTotalSlack();
         critical = Boolean.valueOf(totalSlack != null && totalSlack.getDuration() <= 0 && NumberUtility.getInt(getPercentageComplete()) != 100);
         set(TaskField.CRITICAL, critical);
      }
      return (BooleanUtility.getBoolean(critical));
   }

   /**
    * The CV (earned value cost variance) field shows the difference between
    * how much it should have cost to achieve the current level of completion
    * on the task, and how much it has actually cost to achieve the current
    * level of completion up to the status date or today's date.
    * How Calculated   CV is the difference between BCWP
    * (budgeted cost of work performed) and ACWP
    * (actual cost of work performed). Microsoft Project calculates
    * the CV as follows: CV = BCWP - ACWP
    *
    * @return sum of earned value cost variance
    */
   public Number getCV()
   {
      Number variance = (Number) getCachedValue(TaskField.CV);
      if (variance == null)
      {
         variance = Double.valueOf(NumberUtility.getDouble(getBCWP()) - NumberUtility.getDouble(getACWP()));
         set(TaskField.CV, variance);
      }
      return (variance);
   }

   /**
    * Delay , in MPX files as eg '0ed'. Use duration
    *
    * @return Duration
    */
   public Duration getLevelingDelay()
   {
      return ((Duration) getCachedValue(TaskField.LEVELING_DELAY));
   }

   /**
    * The Duration field is the total span of active working time for a task.
    * This is generally the amount of time from the start to the finish of a task.
    * The default for new tasks is 1 day (1d).
    *
    * @return Duration
    */
   public Duration getDuration()
   {
      return (Duration) getCachedValue(TaskField.DURATION);
   }

   /**
    * Retrieves the duration text of a manually scheduled task.
    * 
    * @return duration text
    */
   public String getDurationText()
   {
      return (String) getCachedValue(TaskField.DURATION_TEXT);
   }

   /**
    * The Duration1 through Duration10 fields are custom fields that show any
    * specialized task duration information you want to enter and store separately
    * in your project.
    *
    * @return Duration
    */
   public Duration getDuration1()
   {
      return (Duration) getCachedValue(TaskField.DURATION1);
   }

   /**
    * The Duration1 through Duration10 fields are custom fields that show any
    * specialized task duration information you want to enter and store separately
    * in your project.
    *
    * @return Duration
    */
   public Duration getDuration2()
   {
      return ((Duration) getCachedValue(TaskField.DURATION2));
   }

   /**
    * The Duration1 through Duration10 fields are custom fields that show any
    * specialized task duration information you want to enter and store separately
    * in your project.
    *
    * @return Duration
    */
   public Duration getDuration3()
   {
      return ((Duration) getCachedValue(TaskField.DURATION3));
   }

   /**
    * The Duration Variance field contains the difference between the
    * baseline duration of a task and the total duration (current estimate)
    * of a task.
    *
    * @return Duration
    */
   public Duration getDurationVariance()
   {
      Duration variance = (Duration) getCachedValue(TaskField.DURATION_VARIANCE);
      if (variance == null)
      {
         Duration duration = getDuration();
         Duration baselineDuration = getBaselineDuration();

         if (duration != null && baselineDuration != null)
         {
            variance = Duration.getInstance(duration.getDuration() - baselineDuration.convertUnits(duration.getUnits(), getParentFile().getProjectHeader()).getDuration(), duration.getUnits());
            set(TaskField.DURATION_VARIANCE, variance);
         }
      }
      return (variance);
   }

   /**
    * The Early Finish field contains the earliest date that a task could
    * possibly finish, based on early finish dates of predecessor and
    * successor tasks, other constraints, and any leveling delay.
    *
    * @return Date
    */
   public Date getEarlyFinish()
   {
      return ((Date) getCachedValue(TaskField.EARLY_FINISH));
   }

   /**
    * The Early Start field contains the earliest date that a task could
    * possibly begin, based on the early start dates of predecessor and
    * successor tasks, and other constraints.
    *
    * @return Date
    */
   public Date getEarlyStart()
   {
      return ((Date) getCachedValue(TaskField.EARLY_START));
   }

   /**
    * The Finish field shows the date and time that a task is scheduled to
    * be completed. You can enter the finish date you want, to indicate the
    * date when the task should be completed. Or, you can have Microsoft
    * Project calculate the finish date.
    *
    * @return Date
    */
   public Date getFinish()
   {
      return (Date) getCachedValue(TaskField.FINISH);
   }

   /**
    * Retrieves the finish text of a manually scheduled task.
    * 
    * @return finish text
    */
   public String getFinishText()
   {
      return (String) getCachedValue(TaskField.FINISH_TEXT);
   }

   /**
    * The Finish1 through Finish10 fields are custom fields that show any
    * specific task finish date information you want to enter and store
    * separately in your project.
    *
    * @return Date
    */
   public Date getFinish1()
   {
      return ((Date) getCachedValue(TaskField.FINISH1));
   }

   /**
    * The Finish1 through Finish10 fields are custom fields that show any
    * specific task finish date information you want to enter and store
    * separately in your project.
    *
    * @return Date
    */
   public Date getFinish2()
   {
      return ((Date) getCachedValue(TaskField.FINISH2));
   }

   /**
    * The Finish1 through Finish10 fields are custom fields that show any
    * specific task finish date information you want to enter and store
    * separately in your project.
    *
    * @return Date
    */
   public Date getFinish3()
   {
      return ((Date) getCachedValue(TaskField.FINISH3));
   }

   /**
    * The Finish1 through Finish10 fields are custom fields that show any
    * specific task finish date information you want to enter and store
    * separately in your project.
    *
    * @return Date
    */
   public Date getFinish4()
   {
      return ((Date) getCachedValue(TaskField.FINISH4));
   }

   /**
    * The Finish1 through Finish10 fields are custom fields that show any
    * specific task finish date information you want to enter and store
    * separately in your project.
    *
    * @return Date
    */
   public Date getFinish5()
   {
      return ((Date) getCachedValue(TaskField.FINISH5));
   }

   /**
    * Calculate the finish variance.
    *
    * @return finish variance
    */
   public Duration getFinishVariance()
   {
      Duration variance = (Duration) getCachedValue(TaskField.FINISH_VARIANCE);
      if (variance == null)
      {
         TimeUnit format = getParentFile().getProjectHeader().getDefaultDurationUnits();
         variance = DateUtility.getVariance(this, getBaselineFinish(), getFinish(), format);
         set(TaskField.FINISH_VARIANCE, variance);
      }
      return (variance);
   }

   /**
    * The Fixed Cost field shows any task expense that is not associated
    * with a resource cost.
    *
    * @return currency amount
    */
   public Number getFixedCost()
   {
      return ((Number) getCachedValue(TaskField.FIXED_COST));
   }

   /**
    * The Flag1-20 fields indicate whether a task is marked for further
    * action or identification of some kind. To mark a task, click Yes
    * in a Flag field. If you don't want a task marked, click No.
    *
    * @return boolean
    */
   public boolean getFlag1()
   {
      return (BooleanUtility.getBoolean((Boolean) getCachedValue(TaskField.FLAG1)));
   }

   /**
    * The Flag1-20 fields indicate whether a task is marked for further
    * action or identification of some kind. To mark a task, click Yes
    * in a Flag field. If you don't want a task marked, click No.
    *
    * @return boolean
    */
   public boolean getFlag2()
   {
      return (BooleanUtility.getBoolean((Boolean) getCachedValue(TaskField.FLAG2)));
   }

   /**
    * The Flag1-20 fields indicate whether a task is marked for further
    * action or identification of some kind. To mark a task, click Yes
    * in a Flag field. If you don't want a task marked, click No.
    *
    * @return boolean
    */
   public boolean getFlag3()
   {
      return (BooleanUtility.getBoolean((Boolean) getCachedValue(TaskField.FLAG3)));
   }

   /**
    * The Flag1-20 fields indicate whether a task is marked for further
    * action or identification of some kind. To mark a task, click Yes
    * in a Flag field. If you don't want a task marked, click No.
    *
    * @return boolean
    */
   public boolean getFlag4()
   {
      return (BooleanUtility.getBoolean((Boolean) getCachedValue(TaskField.FLAG4)));
   }

   /**
    * The Flag1-20 fields indicate whether a task is marked for further
    * action or identification of some kind. To mark a task, click Yes
    * in a Flag field. If you don't want a task marked, click No.
    *
    * @return boolean
    */
   public boolean getFlag5()
   {
      return (BooleanUtility.getBoolean((Boolean) getCachedValue(TaskField.FLAG5)));
   }

   /**
    * The Flag1-20 fields indicate whether a task is marked for further
    * action or identification of some kind. To mark a task, click Yes
    * in a Flag field. If you don't want a task marked, click No.
    *
    * @return boolean
    */
   public boolean getFlag6()
   {
      return (BooleanUtility.getBoolean((Boolean) getCachedValue(TaskField.FLAG6)));
   }

   /**
    * The Flag1-20 fields indicate whether a task is marked for further
    * action or identification of some kind. To mark a task, click Yes
    * in a Flag field. If you don't want a task marked, click No.
    *
    * @return boolean
    */
   public boolean getFlag7()
   {
      return (BooleanUtility.getBoolean((Boolean) getCachedValue(TaskField.FLAG7)));
   }

   /**
    * The Flag1-20 fields indicate whether a task is marked for further
    * action or identification of some kind. To mark a task, click Yes
    * in a Flag field. If you don't want a task marked, click No.
    *
    * @return boolean
    */
   public boolean getFlag8()
   {
      return (BooleanUtility.getBoolean((Boolean) getCachedValue(TaskField.FLAG8)));
   }

   /**
    * The Flag1-20 fields indicate whether a task is marked for further
    * action or identification of some kind. To mark a task, click Yes
    * in a Flag field. If you don't want a task marked, click No.
    *
    * @return boolean
    */
   public boolean getFlag9()
   {
      return (BooleanUtility.getBoolean((Boolean) getCachedValue(TaskField.FLAG9)));
   }

   /**
    * The Flag1-20 fields indicate whether a task is marked for further
    * action or identification of some kind. To mark a task, click Yes
    * in a Flag field. If you don't want a task marked, click No.
    *
    * @return boolean
    */
   public boolean getFlag10()
   {
      return (BooleanUtility.getBoolean((Boolean) getCachedValue(TaskField.FLAG10)));
   }

   /**
    * The Free Slack field contains the amount of time that a task can be
    * delayed without delaying any successor tasks. If the task has no
    * successors, free slack is the amount of time that a task can be
    * delayed without delaying the entire project's finish date.
    *
    * @return Duration
    */
   public Duration getFreeSlack()
   {
      return ((Duration) getCachedValue(TaskField.FREE_SLACK));
   }

   /**
    * The Hide Bar field indicates whether the Gantt bars and Calendar bars
    * for a task are hidden. Click Yes in the Hide Bar field to hide the
    * bar for the task. Click No in the Hide Bar field to show the bar
    * for the task.
    *
    * @return boolean
    */
   public boolean getHideBar()
   {
      return (BooleanUtility.getBoolean((Boolean) getCachedValue(TaskField.HIDEBAR)));
   }

   /**
    * The ID field contains the identifier number that Microsoft Project
    * automatically assigns to each task as you add it to the project.
    * The ID indicates the position of a task with respect to the other tasks.
    *
    * @return the task ID
    */
   public Integer getID()
   {
      return ((Integer) getCachedValue(TaskField.ID));
   }

   /**
    * The Late Finish field contains the latest date that a task can finish
    * without delaying the finish of the project. This date is based on the
    * task's late start date, as well as the late start and late finish
    * dates of predecessor and successor
    * tasks, and other constraints.
    *
    * @return Date
    */
   public Date getLateFinish()
   {
      return ((Date) getCachedValue(TaskField.LATE_FINISH));
   }

   /**
    * The Late Start field contains the latest date that a task can start
    * without delaying the finish of the project. This date is based on
    * the task's start date, as well as the late start and late finish
    * dates of predecessor and successor tasks, and other constraints.
    *
    * @return Date
    */
   public Date getLateStart()
   {
      return ((Date) getCachedValue(TaskField.LATE_START));
   }

   /**
    * The Linked Fields field indicates whether there are OLE links to the task,
    * either from elsewhere in the active project, another Microsoft Project file,
    * or from another program.
    *
    * @return boolean
    */
   public boolean getLinkedFields()
   {
      return (BooleanUtility.getBoolean((Boolean) getCachedValue(TaskField.LINKED_FIELDS)));
   }

   /**
    * The Marked field indicates whether a task is marked for further action or
    * identification of some kind. To mark a task, click Yes in the Marked field.
    * If you don't want a task marked, click No.
    *
    * @return true for marked
    */
   public boolean getMarked()
   {
      return (BooleanUtility.getBoolean((Boolean) getCachedValue(TaskField.MARKED)));
   }

   /**
    * The Milestone field indicates whether a task is a milestone.
    *
    * @return boolean
    */
   public boolean getMilestone()
   {
      return (BooleanUtility.getBoolean((Boolean) getCachedValue(TaskField.MILESTONE)));
   }

   /**
    * Retrieves the task name.
    *
    * @return task name
    */
   public String getName()
   {
      return ((String) getCachedValue(TaskField.NAME));
   }

   /**
    * The Notes field contains notes that you can enter about a task.
    * You can use task notes to help maintain a history for a task.
    *
    * @return notes
    */
   public String getNotes()
   {
      String notes = (String) getCachedValue(TaskField.NOTES);
      return (notes == null ? "" : notes);
   }

   /**
    * Retrieves a numeric value.
    *
    * @return Numeric value
    */
   public Number getNumber1()
   {
      return ((Number) getCachedValue(TaskField.NUMBER1));
   }

   /**
    * Retrieves a numeric value.
    *
    * @return Numeric value
    */
   public Number getNumber2()
   {
      return ((Number) getCachedValue(TaskField.NUMBER2));
   }

   /**
    * Retrieves a numeric value.
    *
    * @return Numeric value
    */
   public Number getNumber3()
   {
      return ((Number) getCachedValue(TaskField.NUMBER3));
   }

   /**
    * Retrieves a numeric value.
    *
    * @return Numeric value
    */
   public Number getNumber4()
   {
      return ((Number) getCachedValue(TaskField.NUMBER4));
   }

   /**
    * Retrieves a numeric value.
    *
    * @return Numeric value
    */
   public Number getNumber5()
   {
      return ((Number) getCachedValue(TaskField.NUMBER5));
   }

   /**
    * The Objects field contains the number of objects attached to a task.
    * Microsoft Project counts the number of objects linked or embedded to a task.
    * However, objects in the Notes box in the Resource Form are not included
    * in this count.
    *
    * @return int
    */
   public Integer getObjects()
   {
      return ((Integer) getCachedValue(TaskField.OBJECTS));
   }

   /**
    * The Outline Level field contains the number that indicates the level
    * of the task in the project outline hierarchy.
    *
    * @return int
    */
   public Integer getOutlineLevel()
   {
      return ((Integer) getCachedValue(TaskField.OUTLINE_LEVEL));
   }

   /**
    * The Outline Number field contains the number of the task in the structure
    * of an outline. This number indicates the task's position within the
    * hierarchical structure of the project outline. The outline number is
    * similar to a WBS (work breakdown structure) number,
    * except that the outline number is automatically entered by
    * Microsoft Project.
    *
    * @return String
    */
   public String getOutlineNumber()
   {
      return ((String) getCachedValue(TaskField.OUTLINE_NUMBER));
   }

   /**
    * Retrieves the list of predecessors for this task.
    * 
    * @return list of prdecessor Relation instances
    */
   @SuppressWarnings("unchecked") public List<Relation> getPredecessors()
   {
      return ((List<Relation>) getCachedValue(TaskField.PREDECESSORS));
   }

   /**
    * Retrieves the list of succesors for this task.
    * 
    * @return list of successor Relation instances
    */
   @SuppressWarnings("unchecked") public List<Relation> getSuccessors()
   {
      return ((List<Relation>) getCachedValue(TaskField.SUCCESSORS));
   }

   /**
    * The Priority field provides choices for the level of importance
    * assigned to a task, which in turn indicates how readily a task can be
    * delayed or split during resource leveling.
    * The default priority is Medium. Those tasks with a priority
    * of Do Not Level are never delayed or split when Microsoft Project levels
    * tasks that have overallocated resources assigned.
    *
    * @return priority class instance
    */
   public Priority getPriority()
   {
      return ((Priority) getCachedValue(TaskField.PRIORITY));
   }

   /**
    * The Project field shows the name of the project from which a task
    * originated.
    * This can be the name of the active project file. If there are other
    * projects inserted
    * into the active project file, the name of the inserted project appears
    * in this field
    * for the task.
    *
    * @return name of originating project
    */
   public String getProject()
   {
      return ((String) getCachedValue(TaskField.PROJECT));
   }

   /**
    * The Remaining Cost field shows the remaining scheduled expense of a
    * task that will be incurred in completing the remaining scheduled work
    * by all resources assigned to the task.
    *
    * @return remaining cost
    */
   public Number getRemainingCost()
   {
      return ((Number) getCachedValue(TaskField.REMAINING_COST));
   }

   /**
    * The Remaining Duration field shows the amount of time required
    * to complete the unfinished portion of a task.
    *
    * @return Duration
    */
   public Duration getRemainingDuration()
   {
      return ((Duration) getCachedValue(TaskField.REMAINING_DURATION));
   }

   /**
    * The Remaining Work field shows the amount of time, or person-hours,
    * still required by all assigned resources to complete a task.
    *
    * @return the amount of time still required to complete a task
    */
   public Duration getRemainingWork()
   {
      return ((Duration) getCachedValue(TaskField.REMAINING_WORK));
   }

   /**
    * The Resource Group field contains the list of resource groups to which
    * the resources assigned to a task belong.
    *
    * @return single string list of groups
    */
   public String getResourceGroup()
   {
      return ((String) getCachedValue(TaskField.RESOURCE_GROUP));
   }

   /**
    * The Resource Initials field lists the abbreviations for the names of
    * resources assigned to a task. These initials can serve as substitutes
    * for the names.
    *
    * Note that MS Project 98 does not export values for this field when
    * writing an MPX file, and the field is not currently populated by MPXJ
    * when reading an MPP file.
    *
    * @return String containing a comma separated list of initials
    */
   public String getResourceInitials()
   {
      return ((String) getCachedValue(TaskField.RESOURCE_INITIALS));
   }

   /**
    * The Resource Names field lists the names of all resources assigned
    * to a task.
    *
    * Note that MS Project 98 does not export values for this field when
    * writing an MPX file, and the field is not currently populated by MPXJ
    * when reading an MPP file.
    *
    * @return String containing a comma separated list of names
    */
   public String getResourceNames()
   {
      return ((String) getCachedValue(TaskField.RESOURCE_NAMES));
   }

   /**
    * The Resume field shows the date that the remaining portion of a task
    * is scheduled to resume after you enter a new value for the % Complete
    * field. The Resume field is also recalculated when the remaining portion
    * of a task is moved to a new date.
    *
    * @return Date
    */
   public Date getResume()
   {
      return ((Date) getCachedValue(TaskField.RESUME));
   }

   /**
    * For subtasks, the Rollup field indicates whether information on the
    * subtask Gantt bars
    * will be rolled up to the summary task bar. For summary tasks, the
    * Rollup field indicates
    * whether the summary task bar displays rolled up bars. You must
    * have the Rollup field for
    * summary tasks set to Yes for any subtasks to roll up to them.
    *
    * @return boolean
    */
   public boolean getRollup()
   {
      return (BooleanUtility.getBoolean((Boolean) getCachedValue(TaskField.ROLLUP)));
   }

   /**
    * The Start field shows the date and time that a task is scheduled to begin.
    * You can enter the start date you want, to indicate the date when the task
    * should begin. Or, you can have Microsoft Project calculate the start date.
    *
    * @return Date
    */
   public Date getStart()
   {
      return (Date) getCachedValue(TaskField.START);
   }

   /**
    * Retrieve the start text for a manually scheduled task.
    * 
    * @return start text
    */
   public String getStartText()
   {
      return (String) getCachedValue(TaskField.START_TEXT);
   }

   /**
    * The Start1 through Start10 fields are custom fields that show any
    * specific task start date information you want to enter and store
    * separately in your project.
    *
    * @return Date
    */
   public Date getStart1()
   {
      return ((Date) getCachedValue(TaskField.START1));
   }

   /**
    * The Start1 through Start10 fields are custom fields that show any
    * specific task start date information you want to enter and store
    * separately in your project.
    *
    * @return Date
    */
   public Date getStart2()
   {
      return ((Date) getCachedValue(TaskField.START2));
   }

   /**
    * The Start1 through Start10 fields are custom fields that show any
    * specific task start date information you want to enter and store
    * separately in your project.
    *
    * @return Date
    */
   public Date getStart3()
   {
      return ((Date) getCachedValue(TaskField.START3));
   }

   /**
    * The Start1 through Start10 fields are custom fields that show any
    * specific task start date information you want to enter and store
    * separately in your project.
    *
    * @return Date
    */
   public Date getStart4()
   {
      return ((Date) getCachedValue(TaskField.START4));
   }

   /**
    * The Start1 through Start10 fields are custom fields that show any
    * specific task start date information you want to enter and store
    * separately in your project.
    *
    * @return Date
    */
   public Date getStart5()
   {
      return ((Date) getCachedValue(TaskField.START5));
   }

   /**
    * Calculate the start variance.
    * 
    * @return start variance
    */
   public Duration getStartVariance()
   {
      Duration variance = (Duration) getCachedValue(TaskField.START_VARIANCE);
      if (variance == null)
      {
         TimeUnit format = getParentFile().getProjectHeader().getDefaultDurationUnits();
         variance = DateUtility.getVariance(this, getBaselineStart(), getStart(), format);
         set(TaskField.START_VARIANCE, variance);
      }
      return (variance);
   }

   /**
    * The Stop field shows the date that represents the end of the actual
    * portion of a task. Typically, Microsoft Project calculates the stop date.
    * However, you can edit this date as well.
    *
    * @return Date
    */
   public Date getStop()
   {
      return ((Date) getCachedValue(TaskField.STOP));
   }

   /**
    * Contains the file name and path of the sub project represented by
    * the current task.
    *
    * @return sub project file path
    */
   public String getSubprojectName()
   {
      return ((String) getCachedValue(TaskField.SUBPROJECT_FILE));
   }

   /**
    * The Summary field indicates whether a task is a summary task.
    *
    * @return boolean, true-is summary task
    */
   public boolean getSummary()
   {
      return (BooleanUtility.getBoolean((Boolean) getCachedValue(TaskField.SUMMARY)));
   }

   /**
    * The SV (earned value schedule variance) field shows the difference in
    * cost terms between the current progress and the baseline plan of the
    * task up to the status date or today's date. You can use SV to
    * check costs to determine whether tasks are on schedule.
    *
    * @return -earned value schedule variance
    */
   public Number getSV()
   {
      Number variance = (Number) getCachedValue(TaskField.SV);
      if (variance == null)
      {
         Number bcwp = getBCWP();
         Number bcws = getBCWS();
         if (bcwp != null && bcws != null)
         {
            variance = NumberUtility.getDouble(bcwp.doubleValue() - bcws.doubleValue());
            set(TaskField.SV, variance);
         }
      }
      return (variance);
   }

   /**
    * The Text1-30 fields show any custom text information you want
    * to enter in your project about tasks.
    *
    * @return String
    */
   public String getText1()
   {
      return ((String) getCachedValue(TaskField.TEXT1));
   }

   /**
    * The Text1-30 fields show any custom text information you want
    * to enter in your project about tasks.
    *
    * @return String
    */
   public String getText2()
   {
      return ((String) getCachedValue(TaskField.TEXT2));
   }

   /**
    * The Text1-30 fields show any custom text information you want
    * to enter in your project about tasks.
    *
    * @return String
    */
   public String getText3()
   {
      return ((String) getCachedValue(TaskField.TEXT3));
   }

   /**
    * The Text1-30 fields show any custom text information you want
    * to enter in your project about tasks.
    *
    * @return String
    */
   public String getText4()
   {
      return ((String) getCachedValue(TaskField.TEXT4));
   }

   /**
    * The Text1-30 fields show any custom text information you want
    * to enter in your project about tasks.
    *
    * @return String
    */
   public String getText5()
   {
      return ((String) getCachedValue(TaskField.TEXT5));
   }

   /**
    * The Text1-30 fields show any custom text information you want
    * to enter in your project about tasks.
    *
    * @return String
    */
   public String getText6()
   {
      return ((String) getCachedValue(TaskField.TEXT6));
   }

   /**
    * The Text1-30 fields show any custom text information you want
    * to enter in your project about tasks.
    *
    * @return String
    */
   public String getText7()
   {
      return ((String) getCachedValue(TaskField.TEXT7));
   }

   /**
    * The Text1-30 fields show any custom text information you want
    * to enter in your project about tasks.
    *
    * @return String
    */
   public String getText8()
   {
      return ((String) getCachedValue(TaskField.TEXT8));
   }

   /**
    * The Text1-30 fields show any custom text information you want
    * to enter in your project about tasks.
    *
    * @return String
    */
   public String getText9()
   {
      return ((String) getCachedValue(TaskField.TEXT9));
   }

   /**
    * The Text1-30 fields show any custom text information you want
    * to enter in your project about tasks.
    *
    * @return String
    */
   public String getText10()
   {
      return ((String) getCachedValue(TaskField.TEXT10));
   }

   /**
    * The Total Slack field contains the amount of time a task can be
    * delayed without delaying the project's finish date.
    *
    * @return string representing duration
    */
   public Duration getTotalSlack()
   {
      Duration totalSlack = (Duration) getCachedValue(TaskField.TOTAL_SLACK);
      if (totalSlack == null)
      {
         Duration duration = getDuration();
         if (duration == null)
         {
            duration = Duration.getInstance(0, TimeUnit.DAYS);
         }

         TimeUnit units = duration.getUnits();

         Duration startSlack = getStartSlack();
         if (startSlack == null)
         {
            startSlack = Duration.getInstance(0, units);
         }
         else
         {
            if (startSlack.getUnits() != units)
            {
               startSlack = startSlack.convertUnits(units, getParentFile().getProjectHeader());
            }
         }

         Duration finishSlack = getFinishSlack();
         if (finishSlack == null)
         {
            finishSlack = Duration.getInstance(0, units);
         }
         else
         {
            if (finishSlack.getUnits() != units)
            {
               finishSlack = finishSlack.convertUnits(units, getParentFile().getProjectHeader());
            }
         }

         double startSlackDuration = startSlack.getDuration();
         double finishSlackDuration = finishSlack.getDuration();

         if (startSlackDuration == 0 || finishSlackDuration == 0)
         {
            if (startSlackDuration != 0)
            {
               totalSlack = startSlack;
            }
            else
            {
               totalSlack = finishSlack;
            }
         }
         else
         {
            if (startSlackDuration < finishSlackDuration)
            {
               totalSlack = startSlack;
            }
            else
            {
               totalSlack = finishSlack;
            }
         }

         set(TaskField.TOTAL_SLACK, totalSlack);
      }

      return (totalSlack);
   }

   /**
    * The Unique ID field contains the number that Microsoft Project
    * automatically designates whenever a new task is created. This number
    * indicates the sequence in which the task was
    * created, regardless of placement in the schedule.
    *
    * @return String
    */
   public Integer getUniqueID()
   {
      return ((Integer) getCachedValue(TaskField.UNIQUE_ID));
   }

   /**
    * The Update Needed field indicates whether a TeamUpdate message
    * should be sent to the assigned resources because of changes to the
    * start date, finish date, or resource reassignments of the task.
    *
    * @return true if needed.
    */
   public boolean getUpdateNeeded()
   {
      return (BooleanUtility.getBoolean((Boolean) getCachedValue(TaskField.UPDATE_NEEDED)));
   }

   /**
    * The work breakdown structure code. The WBS field contains an
    * alphanumeric code you can use to represent the task's position within
    * the hierarchical structure of the project. This field is similar to
    * the outline number, except that you can edit it.
    *
    * @return string
    */
   public String getWBS()
   {
      return ((String) getCachedValue(TaskField.WBS));
   }

   /**
    * The Work field shows the total amount of work scheduled to be performed
    * on a task by all assigned resources. This field shows the total work,
    * or person-hours, for a task.
    *
    * @return Duration representing duration .
    */
   public Duration getWork()
   {
      return ((Duration) getCachedValue(TaskField.WORK));
   }

   /**
    * The Work Variance field contains the difference between a task's
    * baseline work and the currently scheduled work.
    *
    * @return Duration representing duration.
    */
   public Duration getWorkVariance()
   {
      Duration variance = (Duration) getCachedValue(TaskField.WORK_VARIANCE);
      if (variance == null)
      {
         Duration work = getWork();
         Duration baselineWork = getBaselineWork();
         if (work != null && baselineWork != null)
         {
            variance = Duration.getInstance(work.getDuration() - baselineWork.convertUnits(work.getUnits(), getParentFile().getProjectHeader()).getDuration(), work.getUnits());
            set(TaskField.WORK_VARIANCE, variance);
         }
      }
      return (variance);
   }

   /**
    * Retrieve count of the number of child tasks.
    *
    * @return Number of child tasks.
    */
   int getChildTaskCount()
   {
      return (m_children.size());
   }

   /**
    * This method retrieves a reference to the parent of this task, as
    * defined by the outline level. If this task is at the top level,
    * this method will return null.
    *
    * @return parent task
    */
   public Task getParentTask()
   {
      return (m_parent);
   }

   /**
    * This method retrieves a list of child tasks relative to the
    * current task, as defined by the outine level. If there
    * are no child tasks, this method will return an empty list.
    *
    * @return child tasks
    */
   public List<Task> getChildTasks()
   {
      return (m_children);
   }

   /**
    * This method implements the only method in the Comparable interface.
    * This allows Tasks to be compared and sorted based on their ID value.
    * Note that if the MPX/MPP file has been generated by MSP, the ID value
    * will always be in the correct sequence. The Unique ID value will not
    * necessarily be in the correct sequence as task insertions and deletions
    * will change the order.
    *
    * @param o object to compare this instance with
    * @return result of comparison
    */
   public int compareTo(Task o)
   {
      int id1 = NumberUtility.getInt(getID());
      int id2 = NumberUtility.getInt(o.getID());
      return ((id1 < id2) ? (-1) : ((id1 == id2) ? 0 : 1));
   }

   /**
    * This method retrieves a flag indicating whether the duration of the
    * task has only been estimated.
    *
    * @return boolean
    */
   public boolean getEstimated()
   {
      return (BooleanUtility.getBoolean((Boolean) getCachedValue(TaskField.ESTIMATED)));
   }

   /**
    * This method retrieves a flag indicating whether the duration of the
    * task has only been estimated.

    * @param estimated Boolean flag
    */
   public void setEstimated(boolean estimated)
   {
      set(TaskField.ESTIMATED, estimated);
   }

   /**
    * This method retrieves the deadline for this task.
    *
    * @return Task deadline
    */
   public Date getDeadline()
   {
      return ((Date) getCachedValue(TaskField.DEADLINE));
   }

   /**
    * This method sets the deadline for this task.
    *
    * @param deadline deadline date
    */
   public void setDeadline(Date deadline)
   {
      set(TaskField.DEADLINE, deadline);
   }

   /**
    * This method retrieves the task type.
    *
    * @return int representing the task type
    */
   public TaskType getType()
   {
      return ((TaskType) getCachedValue(TaskField.TYPE));
   }

   /**
    * This method sets the task type.
    *
    * @param type task type
    */
   public void setType(TaskType type)
   {
      set(TaskField.TYPE, type);
   }

   /**
    * Retrieves the flag indicating if this is a null task.
    *
    * @return boolean flag
    */
   public boolean getNull()
   {
      return (m_null);
   }

   /**
    * Sets the flag indicating if this is a null task.
    *
    * @param isNull boolean flag
    */
   public void setNull(boolean isNull)
   {
      m_null = isNull;
   }

   /**
    * Retrieve the WBS level.
    *
    * @return WBS level
    */
   public String getWBSLevel()
   {
      return (m_wbsLevel);
   }

   /**
    * Set the WBS level.
    *
    * @param wbsLevel WBS level
    */
   public void setWBSLevel(String wbsLevel)
   {
      m_wbsLevel = wbsLevel;
   }

   /**
    * Retrieve the resume valid flag.
    *
    * @return resume valie flag
    */
   public boolean getResumeValid()
   {
      return (m_resumeValid);
   }

   /**
    * Set the resume valid flag.
    *
    * @param resumeValid resume valid flag
    */
   public void setResumeValid(boolean resumeValid)
   {
      m_resumeValid = resumeValid;
   }

   /**
    * Retrieve the recurring flag.
    *
    * @return recurring flag
    */
   public boolean getRecurring()
   {
      return (BooleanUtility.getBoolean((Boolean) getCachedValue(TaskField.RECURRING)));
   }

   /**
    * Set the recurring flag.
    *
    * @param recurring recurring flag
    */
   public void setRecurring(boolean recurring)
   {
      set(TaskField.RECURRING, recurring);
   }

   /**
    * Retrieve the over allocated flag.
    *
    * @return over allocated flag
    */
   public boolean getOverAllocated()
   {
      return (BooleanUtility.getBoolean((Boolean) getCachedValue(TaskField.OVERALLOCATED)));
   }

   /**
    * Set the over allocated flag.
    *
    * @param overAllocated over allocated flag
    */
   public void setOverAllocated(boolean overAllocated)
   {
      set(TaskField.OVERALLOCATED, overAllocated);
   }

   /**
    * Where a task in an MPP file represents a task from a subproject,
    * this value will be non-zero. The value itself is the unique ID
    * value shown in the parent project. To retrieve the value of the
    * task unique ID in the child project, remove the top two bytes:
    *
    * taskID = (subprojectUniqueID & 0xFFFF)
    *
    * @return sub project unique task ID
    */
   public Integer getSubprojectTaskUniqueID()
   {
      return (m_subprojectTaskUniqueID);
   }

   /**
    * Sets the sub project unique task ID.
    *
    * @param subprojectUniqueTaskID subproject unique task ID
    */
   public void setSubprojectTaskUniqueID(Integer subprojectUniqueTaskID)
   {
      m_subprojectTaskUniqueID = subprojectUniqueTaskID;
   }

   /**
    * Where a task in an MPP file represents a task from a subproject,
    * this value will be non-zero. The value itself is the ID
    * value shown in the parent project.
    *
    * @return sub project task ID
    */
   public Integer getSubprojectTaskID()
   {
      return (m_subprojectTaskID);
   }

   /**
    * Sets the sub project task ID.
    *
    * @param subprojectTaskID subproject task ID
    */
   public void setSubprojectTaskID(Integer subprojectTaskID)
   {
      m_subprojectTaskID = subprojectTaskID;
   }

   /**
    * Sets the offset added to unique task IDs from sub projects
    * to generate the task ID shown in the master project.
    *
    * @param offset unique ID offset
    */
   public void setSubprojectTasksUniqueIDOffset(Integer offset)
   {
      m_subprojectTasksUniqueIDOffset = offset;
   }

   /**
    * Retrieves the offset added to unique task IDs from sub projects
    * to generate the task ID shown in the master project.
    *
    * @return unique ID offset
    */
   public Integer getSubprojectTasksUniqueIDOffset()
   {
      return (m_subprojectTasksUniqueIDOffset);
   }

   /**
    * Retrieve the subproject read only flag.
    *
    * @return subproject read only flag
    */
   public boolean getSubprojectReadOnly()
   {
      return (BooleanUtility.getBoolean((Boolean) getCachedValue(TaskField.SUBPROJECT_READ_ONLY)));
   }

   /**
    * Set the subproject read only flag.
    *
    * @param subprojectReadOnly subproject read only flag
    */
   public void setSubprojectReadOnly(boolean subprojectReadOnly)
   {
      set(TaskField.SUBPROJECT_READ_ONLY, subprojectReadOnly);
   }

   /**
    * Retrieves the external task flag.
    *
    * @return external task flag
    */
   public boolean getExternalTask()
   {
      return (BooleanUtility.getBoolean((Boolean) getCachedValue(TaskField.EXTERNAL_TASK)));
   }

   /**
    * Sets the external task flag.
    *
    * @param externalTask external task flag
    */
   public void setExternalTask(boolean externalTask)
   {
      set(TaskField.EXTERNAL_TASK, externalTask);
   }

   /**
    * Retrieves the external task project file name.
    *
    * @return external task project file name
    */
   public String getExternalTaskProject()
   {
      return (m_externalTaskProject);
   }

   /**
    * Sets the external task project file name.
    *
    * @param externalTaskProject external task project file name
    */
   public void setExternalTaskProject(String externalTaskProject)
   {
      m_externalTaskProject = externalTaskProject;
   }

   /**
    * Retrieve the ACWP value.
    *
    * @return ACWP value
    */
   public Number getACWP()
   {
      return ((Number) getCachedValue(TaskField.ACWP));
   }

   /**
    * Set the ACWP value.
    *
    * @param acwp ACWP value
    */
   public void setACWP(Number acwp)
   {
      set(TaskField.ACWP, acwp);
   }

   /**
    * Retrieve the leveling delay format.
    *
    * @return leveling delay  format
    */
   public TimeUnit getLevelingDelayFormat()
   {
      return (m_levelingDelayFormat);
   }

   /**
    * Set the leveling delay format.
    *
    * @param levelingDelayFormat leveling delay format
    */
   public void setLevelingDelayFormat(TimeUnit levelingDelayFormat)
   {
      m_levelingDelayFormat = levelingDelayFormat;
   }

   /**
    * Retrieves the ignore resource celandar flag.
    *
    * @return ignore resource celandar flag
    */
   public boolean getIgnoreResourceCalendar()
   {
      return (BooleanUtility.getBoolean((Boolean) getCachedValue(TaskField.IGNORE_RESOURCE_CALENDAR)));
   }

   /**
    * Sets the ignore resource celandar flag.
    *
    * @param ignoreResourceCalendar ignore resource celandar flag
    */
   public void setIgnoreResourceCalendar(boolean ignoreResourceCalendar)
   {
      set(TaskField.IGNORE_RESOURCE_CALENDAR, ignoreResourceCalendar);
   }

   /**
    * Retrieves the physical percent complete value.
    *
    * @return physical percent complete value
    */
   public Integer getPhysicalPercentComplete()
   {
      return (m_physicalPercentComplete);
   }

   /**
    * Srts the physical percent complete value.
    *
    * @param physicalPercentComplete physical percent complete value
    */
   public void setPhysicalPercentComplete(Integer physicalPercentComplete)
   {
      m_physicalPercentComplete = physicalPercentComplete;
   }

   /**
    * Retrieves the earned value method.
    *
    * @return earned value method
    */
   public EarnedValueMethod getEarnedValueMethod()
   {
      return (m_earnedValueMethod);
   }

   /**
    * Sets the earned value method.
    *
    * @param earnedValueMethod earned value method
    */
   public void setEarnedValueMethod(EarnedValueMethod earnedValueMethod)
   {
      m_earnedValueMethod = earnedValueMethod;
   }

   /**
    * Retrieves the actual work protected value.
    *
    * @return actual work protected value
    */
   public Duration getActualWorkProtected()
   {
      return (m_actualWorkProtected);
   }

   /**
    * Sets the actual work protected value.
    *
    * @param actualWorkProtected actual work protected value
    */
   public void setActualWorkProtected(Duration actualWorkProtected)
   {
      m_actualWorkProtected = actualWorkProtected;
   }

   /**
    * Retrieves the actual overtime work protected value.
    *
    * @return actual overtime work protected value
    */
   public Duration getActualOvertimeWorkProtected()
   {
      return (m_actualOvertimeWorkProtected);
   }

   /**
    * Sets the actual overtime work protected value.
    *
    * @param actualOvertimeWorkProtected actual overtime work protected value
    */
   public void setActualOvertimeWorkProtected(Duration actualOvertimeWorkProtected)
   {
      m_actualOvertimeWorkProtected = actualOvertimeWorkProtected;
   }

   /**
    * Retrieve the amount of regular work.
    *
    * @return amount of regular work
    */
   public Duration getRegularWork()
   {
      return ((Duration) getCachedValue(TaskField.REGULAR_WORK));
   }

   /**
    * Set the amount of regular work.
    *
    * @param regularWork amount of regular work
    */
   public void setRegularWork(Duration regularWork)
   {
      set(TaskField.REGULAR_WORK, regularWork);
   }

   /**
    * Retrieves the flag value.
    *
    * @return flag value
    */
   public boolean getFlag11()
   {
      return (BooleanUtility.getBoolean((Boolean) getCachedValue(TaskField.FLAG11)));
   }

   /**
    * Retrieves the flag value.
    *
    * @return flag value
    */
   public boolean getFlag12()
   {
      return (BooleanUtility.getBoolean((Boolean) getCachedValue(TaskField.FLAG12)));
   }

   /**
    * Retrieves the flag value.
    *
    * @return flag value
    */
   public boolean getFlag13()
   {
      return (BooleanUtility.getBoolean((Boolean) getCachedValue(TaskField.FLAG13)));
   }

   /**
    * Retrieves the flag value.
    *
    * @return flag value
    */
   public boolean getFlag14()
   {
      return (BooleanUtility.getBoolean((Boolean) getCachedValue(TaskField.FLAG14)));
   }

   /**
    * Retrieves the flag value.
    *
    * @return flag value
    */
   public boolean getFlag15()
   {
      return (BooleanUtility.getBoolean((Boolean) getCachedValue(TaskField.FLAG15)));
   }

   /**
    * Retrieves the flag value.
    *
    * @return flag value
    */
   public boolean getFlag16()
   {
      return (BooleanUtility.getBoolean((Boolean) getCachedValue(TaskField.FLAG16)));
   }

   /**
    * Retrieves the flag value.
    *
    * @return flag value
    */
   public boolean getFlag17()
   {
      return (BooleanUtility.getBoolean((Boolean) getCachedValue(TaskField.FLAG17)));
   }

   /**
    * Retrieves the flag value.
    *
    * @return flag value
    */
   public boolean getFlag18()
   {
      return (BooleanUtility.getBoolean((Boolean) getCachedValue(TaskField.FLAG18)));
   }

   /**
    * Retrieves the flag value.
    *
    * @return flag value
    */
   public boolean getFlag19()
   {
      return (BooleanUtility.getBoolean((Boolean) getCachedValue(TaskField.FLAG19)));
   }

   /**
    * Retrieves the flag value.
    *
    * @return flag value
    */
   public boolean getFlag20()
   {
      return (BooleanUtility.getBoolean((Boolean) getCachedValue(TaskField.FLAG20)));
   }

   /**
    * Sets the flag value.
    *
    * @param b flag value
    */
   public void setFlag11(boolean b)
   {
      set(TaskField.FLAG11, b);
   }

   /**
    * Sets the flag value.
    *
    * @param b flag value
    */
   public void setFlag12(boolean b)
   {
      set(TaskField.FLAG12, b);
   }

   /**
    * Sets the flag value.
    *
    * @param b flag value
    */
   public void setFlag13(boolean b)
   {
      set(TaskField.FLAG13, b);
   }

   /**
    * Sets the flag value.
    *
    * @param b flag value
    */
   public void setFlag14(boolean b)
   {
      set(TaskField.FLAG14, b);
   }

   /**
    * Sets the flag value.
    *
    * @param b flag value
    */
   public void setFlag15(boolean b)
   {
      set(TaskField.FLAG15, b);
   }

   /**
    * Sets the flag value.
    *
    * @param b flag value
    */
   public void setFlag16(boolean b)
   {
      set(TaskField.FLAG16, b);
   }

   /**
    * Sets the flag value.
    *
    * @param b flag value
    */
   public void setFlag17(boolean b)
   {
      set(TaskField.FLAG17, b);
   }

   /**
    * Sets the flag value.
    *
    * @param b flag value
    */
   public void setFlag18(boolean b)
   {
      set(TaskField.FLAG18, b);
   }

   /**
    * Sets the flag value.
    *
    * @param b flag value
    */
   public void setFlag19(boolean b)
   {
      set(TaskField.FLAG19, b);
   }

   /**
    * Sets the flag value.
    *
    * @param b flag value
    */
   public void setFlag20(boolean b)
   {
      set(TaskField.FLAG20, b);
   }

   /**
    * Sets the effort driven flag.
    *
    * @param flag value
    */
   public void setEffortDriven(boolean flag)
   {
      set(TaskField.EFFORT_DRIVEN, flag);
   }

   /**
    * Retrieves the effort driven flag.
    *
    * @return Flag value
    */
   public boolean getEffortDriven()
   {
      return (BooleanUtility.getBoolean((Boolean) getCachedValue(TaskField.EFFORT_DRIVEN)));
   }

   /**
    * Retrieves a text value.
    *
    * @return Text value
    */
   public String getText11()
   {
      return ((String) getCachedValue(TaskField.TEXT11));
   }

   /**
    * Retrieves a text value.
    *
    * @return Text value
    */
   public String getText12()
   {
      return ((String) getCachedValue(TaskField.TEXT12));
   }

   /**
    * Retrieves a text value.
    *
    * @return Text value
    */
   public String getText13()
   {
      return ((String) getCachedValue(TaskField.TEXT13));
   }

   /**
    * Retrieves a text value.
    *
    * @return Text value
    */
   public String getText14()
   {
      return ((String) getCachedValue(TaskField.TEXT14));
   }

   /**
    * Retrieves a text value.
    *
    * @return Text value
    */
   public String getText15()
   {
      return ((String) getCachedValue(TaskField.TEXT15));
   }

   /**
    * Retrieves a text value.
    *
    * @return Text value
    */
   public String getText16()
   {
      return ((String) getCachedValue(TaskField.TEXT16));
   }

   /**
    * Retrieves a text value.
    *
    * @return Text value
    */
   public String getText17()
   {
      return ((String) getCachedValue(TaskField.TEXT17));
   }

   /**
    * Retrieves a text value.
    *
    * @return Text value
    */
   public String getText18()
   {
      return ((String) getCachedValue(TaskField.TEXT18));
   }

   /**
    * Retrieves a text value.
    *
    * @return Text value
    */
   public String getText19()
   {
      return ((String) getCachedValue(TaskField.TEXT19));
   }

   /**
    * Retrieves a text value.
    *
    * @return Text value
    */
   public String getText20()
   {
      return ((String) getCachedValue(TaskField.TEXT20));
   }

   /**
    * Retrieves a text value.
    *
    * @return Text value
    */
   public String getText21()
   {
      return ((String) getCachedValue(TaskField.TEXT21));
   }

   /**
    * Retrieves a text value.
    *
    * @return Text value
    */
   public String getText22()
   {
      return ((String) getCachedValue(TaskField.TEXT22));
   }

   /**
    * Retrieves a text value.
    *
    * @return Text value
    */
   public String getText23()
   {
      return ((String) getCachedValue(TaskField.TEXT23));
   }

   /**
    * Retrieves a text value.
    *
    * @return Text value
    */
   public String getText24()
   {
      return ((String) getCachedValue(TaskField.TEXT24));
   }

   /**
    * Retrieves a text value.
    *
    * @return Text value
    */
   public String getText25()
   {
      return ((String) getCachedValue(TaskField.TEXT25));
   }

   /**
    * Retrieves a text value.
    *
    * @return Text value
    */
   public String getText26()
   {
      return ((String) getCachedValue(TaskField.TEXT26));
   }

   /**
    * Retrieves a text value.
    *
    * @return Text value
    */
   public String getText27()
   {
      return ((String) getCachedValue(TaskField.TEXT27));
   }

   /**
    * Retrieves a text value.
    *
    * @return Text value
    */
   public String getText28()
   {
      return ((String) getCachedValue(TaskField.TEXT28));
   }

   /**
    * Retrieves a text value.
    *
    * @return Text value
    */
   public String getText29()
   {
      return ((String) getCachedValue(TaskField.TEXT29));
   }

   /**
    * Retrieves a text value.
    *
    * @return Text value
    */
   public String getText30()
   {
      return ((String) getCachedValue(TaskField.TEXT30));
   }

   /**
    * Sets a text value.
    *
    * @param string Text value
    */
   public void setText11(String string)
   {
      set(TaskField.TEXT11, string);
   }

   /**
    * Sets a text value.
    *
    * @param string Text value
    */
   public void setText12(String string)
   {
      set(TaskField.TEXT12, string);
   }

   /**
    * Sets a text value.
    *
    * @param string Text value
    */
   public void setText13(String string)
   {
      set(TaskField.TEXT13, string);
   }

   /**
    * Sets a text value.
    *
    * @param string Text value
    */
   public void setText14(String string)
   {
      set(TaskField.TEXT14, string);
   }

   /**
    * Sets a text value.
    *
    * @param string Text value
    */
   public void setText15(String string)
   {
      set(TaskField.TEXT15, string);
   }

   /**
    * Sets a text value.
    *
    * @param string Text value
    */
   public void setText16(String string)
   {
      set(TaskField.TEXT16, string);
   }

   /**
    * Sets a text value.
    *
    * @param string Text value
    */
   public void setText17(String string)
   {
      set(TaskField.TEXT17, string);
   }

   /**
    * Sets a text value.
    *
    * @param string Text value
    */
   public void setText18(String string)
   {
      set(TaskField.TEXT18, string);
   }

   /**
    * Sets a text value.
    *
    * @param string Text value
    */
   public void setText19(String string)
   {
      set(TaskField.TEXT19, string);
   }

   /**
    * Sets a text value.
    *
    * @param string Text value
    */
   public void setText20(String string)
   {
      set(TaskField.TEXT20, string);
   }

   /**
    * Sets a text value.
    *
    * @param string Text value
    */
   public void setText21(String string)
   {
      set(TaskField.TEXT21, string);
   }

   /**
    * Sets a text value.
    *
    * @param string Text value
    */
   public void setText22(String string)
   {
      set(TaskField.TEXT22, string);
   }

   /**
    * Sets a text value.
    *
    * @param string Text value
    */
   public void setText23(String string)
   {
      set(TaskField.TEXT23, string);
   }

   /**
    * Sets a text value.
    *
    * @param string Text value
    */
   public void setText24(String string)
   {
      set(TaskField.TEXT24, string);
   }

   /**
    * Sets a text value.
    *
    * @param string Text value
    */
   public void setText25(String string)
   {
      set(TaskField.TEXT25, string);
   }

   /**
    * Sets a text value.
    *
    * @param string Text value
    */
   public void setText26(String string)
   {
      set(TaskField.TEXT26, string);
   }

   /**
    * Sets a text value.
    *
    * @param string Text value
    */
   public void setText27(String string)
   {
      set(TaskField.TEXT27, string);
   }

   /**
    * Sets a text value.
    *
    * @param string Text value
    */
   public void setText28(String string)
   {
      set(TaskField.TEXT28, string);
   }

   /**
    * Sets a text value.
    *
    * @param string Text value
    */
   public void setText29(String string)
   {
      set(TaskField.TEXT29, string);
   }

   /**
    * Sets a text value.
    *
    * @param string Text value
    */
   public void setText30(String string)
   {
      set(TaskField.TEXT30, string);
   }

   /**
    * Sets a numeric value.
    *
    * @param val Numeric value
    */
   public void setNumber6(Number val)
   {
      set(TaskField.NUMBER6, val);
   }

   /**
    * Retrieves a numeric value.
    *
    * @return Numeric value
    */
   public Number getNumber6()
   {
      return ((Number) getCachedValue(TaskField.NUMBER6));
   }

   /**
    * Sets a numeric value.
    *
    * @param val Numeric value
    */
   public void setNumber7(Number val)
   {
      set(TaskField.NUMBER7, val);
   }

   /**
    * Retrieves a numeric value.
    *
    * @return Numeric value
    */
   public Number getNumber7()
   {
      return ((Number) getCachedValue(TaskField.NUMBER7));
   }

   /**
    * Sets a numeric value.
    *
    * @param val Numeric value
    */
   public void setNumber8(Number val)
   {
      set(TaskField.NUMBER8, val);
   }

   /**
    * Retrieves a numeric value.
    *
    * @return Numeric value
    */
   public Number getNumber8()
   {
      return ((Number) getCachedValue(TaskField.NUMBER8));
   }

   /**
    * Sets a numeric value.
    *
    * @param val Numeric value
    */
   public void setNumber9(Number val)
   {
      set(TaskField.NUMBER9, val);
   }

   /**
    * Retrieves a numeric value.
    *
    * @return Numeric value
    */
   public Number getNumber9()
   {
      return ((Number) getCachedValue(TaskField.NUMBER9));
   }

   /**
    * Sets a numeric value.
    *
    * @param val Numeric value
    */
   public void setNumber10(Number val)
   {
      set(TaskField.NUMBER10, val);
   }

   /**
    * Retrieves a numeric value.
    *
    * @return Numeric value
    */
   public Number getNumber10()
   {
      return ((Number) getCachedValue(TaskField.NUMBER10));
   }

   /**
    * Sets a numeric value.
    *
    * @param val Numeric value
    */
   public void setNumber11(Number val)
   {
      set(TaskField.NUMBER11, val);
   }

   /**
    * Retrieves a numeric value.
    *
    * @return Numeric value
    */
   public Number getNumber11()
   {
      return ((Number) getCachedValue(TaskField.NUMBER11));
   }

   /**
    * Sets a numeric value.
    *
    * @param val Numeric value
    */
   public void setNumber12(Number val)
   {
      set(TaskField.NUMBER12, val);
   }

   /**
    * Retrieves a numeric value.
    *
    * @return Numeric value
    */
   public Number getNumber12()
   {
      return ((Number) getCachedValue(TaskField.NUMBER12));
   }

   /**
    * Sets a numeric value.
    *
    * @param val Numeric value
    */
   public void setNumber13(Number val)
   {
      set(TaskField.NUMBER13, val);
   }

   /**
    * Retrieves a numeric value.
    *
    * @return Numeric value
    */
   public Number getNumber13()
   {
      return ((Number) getCachedValue(TaskField.NUMBER13));
   }

   /**
    * Sets a numeric value.
    *
    * @param val Numeric value
    */
   public void setNumber14(Number val)
   {
      set(TaskField.NUMBER14, val);
   }

   /**
    * Retrieves a numeric value.
    *
    * @return Numeric value
    */
   public Number getNumber14()
   {
      return ((Number) getCachedValue(TaskField.NUMBER14));
   }

   /**
    * Sets a numeric value.
    *
    * @param val Numeric value
    */
   public void setNumber15(Number val)
   {
      set(TaskField.NUMBER15, val);
   }

   /**
    * Retrieves a numeric value.
    *
    * @return Numeric value
    */
   public Number getNumber15()
   {
      return ((Number) getCachedValue(TaskField.NUMBER15));
   }

   /**
    * Sets a numeric value.
    *
    * @param val Numeric value
    */
   public void setNumber16(Number val)
   {
      set(TaskField.NUMBER16, val);
   }

   /**
    * Retrieves a numeric value.
    *
    * @return Numeric value
    */
   public Number getNumber16()
   {
      return ((Number) getCachedValue(TaskField.NUMBER16));
   }

   /**
    * Sets a numeric value.
    *
    * @param val Numeric value
    */
   public void setNumber17(Number val)
   {
      set(TaskField.NUMBER17, val);
   }

   /**
    * Retrieves a numeric value.
    *
    * @return Numeric value
    */
   public Number getNumber17()
   {
      return ((Number) getCachedValue(TaskField.NUMBER17));
   }

   /**
    * Sets a numeric value.
    *
    * @param val Numeric value
    */
   public void setNumber18(Number val)
   {
      set(TaskField.NUMBER18, val);
   }

   /**
    * Retrieves a numeric value.
    *
    * @return Numeric value
    */
   public Number getNumber18()
   {
      return ((Number) getCachedValue(TaskField.NUMBER18));
   }

   /**
    * Sets a numeric value.
    *
    * @param val Numeric value
    */
   public void setNumber19(Number val)
   {
      set(TaskField.NUMBER19, val);
   }

   /**
    * Retrieves a numeric value.
    *
    * @return Numeric value
    */
   public Number getNumber19()
   {
      return ((Number) getCachedValue(TaskField.NUMBER19));
   }

   /**
    * Sets a numeric value.
    *
    * @param val Numeric value
    */
   public void setNumber20(Number val)
   {
      set(TaskField.NUMBER20, val);
   }

   /**
    * Retrieves a numeric value.
    *
    * @return Numeric value
    */
   public Number getNumber20()
   {
      return ((Number) getCachedValue(TaskField.NUMBER20));
   }

   /**
    * Retrieves a duration.
    *
    * @return Duration
    */
   public Duration getDuration10()
   {
      return (Duration) getCachedValue(TaskField.DURATION10);
   }

   /**
    * Retrieves a duration.
    *
    * @return Duration
    */
   public Duration getDuration4()
   {
      return (Duration) getCachedValue(TaskField.DURATION4);
   }

   /**
    * Retrieves a duration.
    *
    * @return Duration
    */
   public Duration getDuration5()
   {
      return (Duration) getCachedValue(TaskField.DURATION5);
   }

   /**
    * Retrieves a duration.
    *
    * @return Duration
    */
   public Duration getDuration6()
   {
      return (Duration) getCachedValue(TaskField.DURATION6);
   }

   /**
    * Retrieves a duration.
    *
    * @return Duration
    */
   public Duration getDuration7()
   {
      return (Duration) getCachedValue(TaskField.DURATION7);
   }

   /**
    * Retrieves a duration.
    *
    * @return Duration
    */
   public Duration getDuration8()
   {
      return (Duration) getCachedValue(TaskField.DURATION8);
   }

   /**
    * Retrieves a duration.
    *
    * @return Duration
    */
   public Duration getDuration9()
   {
      return (Duration) getCachedValue(TaskField.DURATION9);
   }

   /**
    * User defined duration field.
    *
    * @param duration Duration value
    */
   public void setDuration10(Duration duration)
   {
      set(TaskField.DURATION10, duration);
   }

   /**
    * User defined duration field.
    *
    * @param duration Duration value
    */
   public void setDuration4(Duration duration)
   {
      set(TaskField.DURATION4, duration);
   }

   /**
    * User defined duration field.
    *
    * @param duration Duration value
    */
   public void setDuration5(Duration duration)
   {
      set(TaskField.DURATION5, duration);
   }

   /**
    * User defined duration field.
    *
    * @param duration Duration value
    */
   public void setDuration6(Duration duration)
   {
      set(TaskField.DURATION6, duration);
   }

   /**
    * User defined duration field.
    *
    * @param duration Duration value
    */
   public void setDuration7(Duration duration)
   {
      set(TaskField.DURATION7, duration);
   }

   /**
    * User defined duration field.
    *
    * @param duration Duration value
    */
   public void setDuration8(Duration duration)
   {
      set(TaskField.DURATION8, duration);
   }

   /**
    * User defined duration field.
    *
    * @param duration Duration value
    */
   public void setDuration9(Duration duration)
   {
      set(TaskField.DURATION9, duration);
   }

   /**
    * Retrieves a date value.
    *
    * @return Date value
    */
   public Date getDate1()
   {
      return ((Date) getCachedValue(TaskField.DATE1));
   }

   /**
    * Retrieves a date value.
    *
    * @return Date value
    */
   public Date getDate10()
   {
      return ((Date) getCachedValue(TaskField.DATE10));
   }

   /**
    * Retrieves a date value.
    *
    * @return Date value
    */
   public Date getDate2()
   {
      return ((Date) getCachedValue(TaskField.DATE2));
   }

   /**
    * Retrieves a date value.
    *
    * @return Date value
    */
   public Date getDate3()
   {
      return ((Date) getCachedValue(TaskField.DATE3));
   }

   /**
    * Retrieves a date value.
    *
    * @return Date value
    */
   public Date getDate4()
   {
      return ((Date) getCachedValue(TaskField.DATE4));
   }

   /**
    * Retrieves a date value.
    *
    * @return Date value
    */
   public Date getDate5()
   {
      return ((Date) getCachedValue(TaskField.DATE5));
   }

   /**
    * Retrieves a date value.
    *
    * @return Date value
    */
   public Date getDate6()
   {
      return ((Date) getCachedValue(TaskField.DATE6));
   }

   /**
    * Retrieves a date value.
    *
    * @return Date value
    */
   public Date getDate7()
   {
      return ((Date) getCachedValue(TaskField.DATE7));
   }

   /**
    * Retrieves a date value.
    *
    * @return Date value
    */
   public Date getDate8()
   {
      return ((Date) getCachedValue(TaskField.DATE8));
   }

   /**
    * Retrieves a date value.
    *
    * @return Date value
    */
   public Date getDate9()
   {
      return ((Date) getCachedValue(TaskField.DATE9));
   }

   /**
    * Sets a date value.
    *
    * @param date Date value
    */
   public void setDate1(Date date)
   {
      set(TaskField.DATE1, date);
   }

   /**
    * Sets a date value.
    *
    * @param date Date value
    */
   public void setDate10(Date date)
   {
      set(TaskField.DATE10, date);
   }

   /**
    * Sets a date value.
    *
    * @param date Date value
    */
   public void setDate2(Date date)
   {
      set(TaskField.DATE2, date);
   }

   /**
    * Sets a date value.
    *
    * @param date Date value
    */
   public void setDate3(Date date)
   {
      set(TaskField.DATE3, date);
   }

   /**
    * Sets a date value.
    *
    * @param date Date value
    */
   public void setDate4(Date date)
   {
      set(TaskField.DATE4, date);
   }

   /**
    * Sets a date value.
    *
    * @param date Date value
    */
   public void setDate5(Date date)
   {
      set(TaskField.DATE5, date);
   }

   /**
    * Sets a date value.
    *
    * @param date Date value
    */
   public void setDate6(Date date)
   {
      set(TaskField.DATE6, date);
   }

   /**
    * Sets a date value.
    *
    * @param date Date value
    */
   public void setDate7(Date date)
   {
      set(TaskField.DATE7, date);
   }

   /**
    * Sets a date value.
    *
    * @param date Date value
    */
   public void setDate8(Date date)
   {
      set(TaskField.DATE8, date);
   }

   /**
    * Sets a date value.
    *
    * @param date Date value
    */
   public void setDate9(Date date)
   {
      set(TaskField.DATE9, date);
   }

   /**
    * Retrieves a cost.
    *
    * @return Cost value
    */
   public Number getCost10()
   {
      return ((Number) getCachedValue(TaskField.COST10));
   }

   /**
    * Retrieves a cost.
    *
    * @return Cost value
    */
   public Number getCost4()
   {
      return ((Number) getCachedValue(TaskField.COST4));
   }

   /**
    * Retrieves a cost.
    *
    * @return Cost value
    */
   public Number getCost5()
   {
      return ((Number) getCachedValue(TaskField.COST5));
   }

   /**
    * Retrieves a cost.
    *
    * @return Cost value
    */
   public Number getCost6()
   {
      return ((Number) getCachedValue(TaskField.COST6));
   }

   /**
    * Retrieves a cost.
    *
    * @return Cost value
    */
   public Number getCost7()
   {
      return ((Number) getCachedValue(TaskField.COST7));
   }

   /**
    * Retrieves a cost.
    *
    * @return Cost value
    */
   public Number getCost8()
   {
      return ((Number) getCachedValue(TaskField.COST8));
   }

   /**
    * Retrieves a cost.
    *
    * @return Cost value
    */
   public Number getCost9()
   {
      return ((Number) getCachedValue(TaskField.COST9));
   }

   /**
    * Sets a cost value.
    *
    * @param number Cost value
    */
   public void setCost10(Number number)
   {
      set(TaskField.COST10, number);
   }

   /**
    * Sets a cost value.
    *
    * @param number Cost value
    */
   public void setCost4(Number number)
   {
      set(TaskField.COST4, number);
   }

   /**
    * Sets a cost value.
    *
    * @param number Cost value
    */
   public void setCost5(Number number)
   {
      set(TaskField.COST5, number);
   }

   /**
    * Sets a cost value.
    *
    * @param number Cost value
    */
   public void setCost6(Number number)
   {
      set(TaskField.COST6, number);
   }

   /**
    * Sets a cost value.
    *
    * @param number Cost value
    */
   public void setCost7(Number number)
   {
      set(TaskField.COST7, number);
   }

   /**
    * Sets a cost value.
    *
    * @param number Cost value
    */
   public void setCost8(Number number)
   {
      set(TaskField.COST8, number);
   }

   /**
    * Sets a cost value.
    *
    * @param number Cost value
    */
   public void setCost9(Number number)
   {
      set(TaskField.COST9, number);
   }

   /**
    * Retrieves a start date.
    *
    * @return Date start date
    */
   public Date getStart10()
   {
      return ((Date) getCachedValue(TaskField.START10));
   }

   /**
    * Retrieves a start date.
    *
    * @return Date start date
    */
   public Date getStart6()
   {
      return ((Date) getCachedValue(TaskField.START6));
   }

   /**
    * Retrieves a start date.
    *
    * @return Date start date
    */
   public Date getStart7()
   {
      return ((Date) getCachedValue(TaskField.START7));
   }

   /**
    * Retrieves a start date.
    *
    * @return Date start date
    */
   public Date getStart8()
   {
      return ((Date) getCachedValue(TaskField.START8));
   }

   /**
    * Retrieves a start date.
    *
    * @return Date start date
    */
   public Date getStart9()
   {
      return ((Date) getCachedValue(TaskField.START9));
   }

   /**
    * Sets a start date.
    *
    * @param date Start date
    */
   public void setStart10(Date date)
   {
      set(TaskField.START10, date);
   }

   /**
    * Sets a start date.
    *
    * @param date Start date
    */
   public void setStart6(Date date)
   {
      set(TaskField.START6, date);
   }

   /**
    * Sets a start date.
    *
    * @param date Start date
    */
   public void setStart7(Date date)
   {
      set(TaskField.START7, date);
   }

   /**
    * Sets a start date.
    *
    * @param date Start date
    */
   public void setStart8(Date date)
   {
      set(TaskField.START8, date);
   }

   /**
    * Sets a start date.
    *
    * @param date Start date
    */
   public void setStart9(Date date)
   {
      set(TaskField.START9, date);
   }

   /**
    * Retrieves a finish date.
    *
    * @return Date finish date
    */
   public Date getFinish10()
   {
      return ((Date) getCachedValue(TaskField.FINISH10));
   }

   /**
    * Retrieves a finish date.
    *
    * @return Date finish date
    */
   public Date getFinish6()
   {
      return ((Date) getCachedValue(TaskField.FINISH6));
   }

   /**
    * Retrieves a finish date.
    *
    * @return Date finish date
    */
   public Date getFinish7()
   {
      return ((Date) getCachedValue(TaskField.FINISH7));
   }

   /**
    * Retrieves a finish date.
    *
    * @return Date finish date
    */
   public Date getFinish8()
   {
      return ((Date) getCachedValue(TaskField.FINISH8));
   }

   /**
    * Retrieves a finish date.
    *
    * @return Date finish date
    */
   public Date getFinish9()
   {
      return ((Date) getCachedValue(TaskField.FINISH9));
   }

   /**
    * User defined finish date field.
    *
    * @param date Date value
    */
   public void setFinish10(Date date)
   {
      set(TaskField.FINISH10, date);
   }

   /**
    * User defined finish date field.
    *
    * @param date Date value
    */
   public void setFinish6(Date date)
   {
      set(TaskField.FINISH6, date);
   }

   /**
    * User defined finish date field.
    *
    * @param date Date value
    */
   public void setFinish7(Date date)
   {
      set(TaskField.FINISH7, date);
   }

   /**
    * User defined finish date field.
    *
    * @param date Date value
    */
   public void setFinish8(Date date)
   {
      set(TaskField.FINISH8, date);
   }

   /**
    * User defined finish date field.
    *
    * @param date Date value
    */
   public void setFinish9(Date date)
   {
      set(TaskField.FINISH9, date);
   }

   /**
    * Retrieves the overtime cost.
    *
    * @return Cost value
    */
   public Number getOvertimeCost()
   {
      return ((Number) getCachedValue(TaskField.OVERTIME_COST));
   }

   /**
    * Sets the overtime cost value.
    *
    * @param number Cost value
    */
   public void setOvertimeCost(Number number)
   {
      set(TaskField.OVERTIME_COST, number);
   }

   /**
    * Sets the value of an outline code field.
    *
    * @param value outline code value
    */
   public void setOutlineCode1(String value)
   {
      set(TaskField.OUTLINE_CODE1, value);
   }

   /**
    * Retrieves the value of an outline code field.
    *
    * @return outline code value
    */
   public String getOutlineCode1()
   {
      return ((String) getCachedValue(TaskField.OUTLINE_CODE1));
   }

   /**
    * Sets the value of an outline code field.
    *
    * @param value outline code value
    */
   public void setOutlineCode2(String value)
   {
      set(TaskField.OUTLINE_CODE2, value);
   }

   /**
    * Retrieves the value of an outline code field.
    *
    * @return outline code value
    */
   public String getOutlineCode2()
   {
      return ((String) getCachedValue(TaskField.OUTLINE_CODE2));
   }

   /**
    * Sets the value of an outline code field.
    *
    * @param value outline code value
    */
   public void setOutlineCode3(String value)
   {
      set(TaskField.OUTLINE_CODE3, value);
   }

   /**
    * Retrieves the value of an outline code field.
    *
    * @return outline code value
    */
   public String getOutlineCode3()
   {
      return ((String) getCachedValue(TaskField.OUTLINE_CODE3));
   }

   /**
    * Sets the value of an outline code field.
    *
    * @param value outline code value
    */
   public void setOutlineCode4(String value)
   {
      set(TaskField.OUTLINE_CODE4, value);
   }

   /**
    * Retrieves the value of an outline code field.
    *
    * @return outline code value
    */
   public String getOutlineCode4()
   {
      return ((String) getCachedValue(TaskField.OUTLINE_CODE4));
   }

   /**
    * Sets the value of an outline code field.
    *
    * @param value outline code value
    */
   public void setOutlineCode5(String value)
   {
      set(TaskField.OUTLINE_CODE5, value);
   }

   /**
    * Retrieves the value of an outline code field.
    *
    * @return outline code value
    */
   public String getOutlineCode5()
   {
      return ((String) getCachedValue(TaskField.OUTLINE_CODE5));
   }

   /**
    * Sets the value of an outline code field.
    *
    * @param value outline code value
    */
   public void setOutlineCode6(String value)
   {
      set(TaskField.OUTLINE_CODE6, value);
   }

   /**
    * Retrieves the value of an outline code field.
    *
    * @return outline code value
    */
   public String getOutlineCode6()
   {
      return ((String) getCachedValue(TaskField.OUTLINE_CODE6));
   }

   /**
    * Sets the value of an outline code field.
    *
    * @param value outline code value
    */
   public void setOutlineCode7(String value)
   {
      set(TaskField.OUTLINE_CODE7, value);
   }

   /**
    * Retrieves the value of an outline code field.
    *
    * @return outline code value
    */
   public String getOutlineCode7()
   {
      return ((String) getCachedValue(TaskField.OUTLINE_CODE7));
   }

   /**
    * Sets the value of an outline code field.
    *
    * @param value outline code value
    */
   public void setOutlineCode8(String value)
   {
      set(TaskField.OUTLINE_CODE8, value);
   }

   /**
    * Retrieves the value of an outline code field.
    *
    * @return outline code value
    */
   public String getOutlineCode8()
   {
      return ((String) getCachedValue(TaskField.OUTLINE_CODE8));
   }

   /**
    * Sets the value of an outline code field.
    *
    * @param value outline code value
    */
   public void setOutlineCode9(String value)
   {
      set(TaskField.OUTLINE_CODE9, value);
   }

   /**
    * Retrieves the value of an outline code field.
    *
    * @return outline code value
    */
   public String getOutlineCode9()
   {
      return ((String) getCachedValue(TaskField.OUTLINE_CODE9));
   }

   /**
    * Sets the value of an outline code field.
    *
    * @param value outline code value
    */
   public void setOutlineCode10(String value)
   {
      set(TaskField.OUTLINE_CODE10, value);
   }

   /**
    * Retrieves the value of an outline code field.
    *
    * @return outline code value
    */
   public String getOutlineCode10()
   {
      return ((String) getCachedValue(TaskField.OUTLINE_CODE10));
   }

   /**
    * Retrieves the actual overtime cost for this task.
    *
    * @return actual overtime cost
    */
   public Number getActualOvertimeCost()
   {
      return ((Number) getCachedValue(TaskField.ACTUAL_OVERTIME_COST));
   }

   /**
    * Sets the actual overtime cost for this task.
    *
    * @param cost actual overtime cost
    */
   public void setActualOvertimeCost(Number cost)
   {
      set(TaskField.ACTUAL_OVERTIME_COST, cost);
   }

   /**
    * Retrieves the actual overtime work value.
    *
    * @return actual overtime work value
    */
   public Duration getActualOvertimeWork()
   {
      return ((Duration) getCachedValue(TaskField.ACTUAL_OVERTIME_WORK));
   }

   /**
    * Sets the actual overtime work value.
    *
    * @param work actual overtime work value
    */
   public void setActualOvertimeWork(Duration work)
   {
      set(TaskField.ACTUAL_OVERTIME_WORK, work);
   }

   /**
    * Retrieves the fixed cost accrual flag value.
    *
    * @return fixed cost accrual flag
    */
   public AccrueType getFixedCostAccrual()
   {
      return ((AccrueType) getCachedValue(TaskField.FIXED_COST_ACCRUAL));
   }

   /**
    * Sets the fixed cost accrual flag value.
    *
    * @param type fixed cost accrual type
    */
   public void setFixedCostAccrual(AccrueType type)
   {
      set(TaskField.FIXED_COST_ACCRUAL, type);
   }

   /**
    * Retrieves the task hyperlink attribute.
    *
    * @return hyperlink attribute
    */
   public String getHyperlink()
   {
      return ((String) getCachedValue(TaskField.HYPERLINK));
   }

   /**
    * Retrieves the task hyperlink address attribute.
    *
    * @return hyperlink address attribute
    */
   public String getHyperlinkAddress()
   {
      return ((String) getCachedValue(TaskField.HYPERLINK_ADDRESS));
   }

   /**
    * Retrieves the task hyperlink sub-address attribute.
    *
    * @return hyperlink sub address attribute
    */
   public String getHyperlinkSubAddress()
   {
      return ((String) getCachedValue(TaskField.HYPERLINK_SUBADDRESS));
   }

   /**
    * Sets the task hyperlink attribute.
    *
    * @param text hyperlink attribute
    */
   public void setHyperlink(String text)
   {
      set(TaskField.HYPERLINK, text);
   }

   /**
    * Sets the task hyperlink address attribute.
    *
    * @param text hyperlink address attribute
    */
   public void setHyperlinkAddress(String text)
   {
      set(TaskField.HYPERLINK_ADDRESS, text);
   }

   /**
    * Sets the task hyperlink sub address attribute.
    *
    * @param text hyperlink sub address attribute
    */
   public void setHyperlinkSubAddress(String text)
   {
      set(TaskField.HYPERLINK_SUBADDRESS, text);
   }

   /**
    * Retrieves the level assignments flag.
    *
    * @return level assignments flag
    */
   public boolean getLevelAssignments()
   {
      return (BooleanUtility.getBoolean((Boolean) getCachedValue(TaskField.LEVEL_ASSIGNMENTS)));
   }

   /**
    * Sets the level assignments flag.
    *
    * @param flag level assignments flag
    */
   public void setLevelAssignments(boolean flag)
   {
      set(TaskField.LEVEL_ASSIGNMENTS, flag);
   }

   /**
    * Retrieves the leveling can split flag.
    *
    * @return leveling can split flag
    */
   public boolean getLevelingCanSplit()
   {
      return (BooleanUtility.getBoolean((Boolean) getCachedValue(TaskField.LEVELING_CAN_SPLIT)));
   }

   /**
    * Sets the leveling can split flag.
    *
    * @param flag leveling can split flag
    */
   public void setLevelingCanSplit(boolean flag)
   {
      set(TaskField.LEVELING_CAN_SPLIT, flag);
   }

   /**
    * Retrieves the overtime work attribute.
    *
    * @return overtime work value
    */
   public Duration getOvertimeWork()
   {
      return ((Duration) getCachedValue(TaskField.OVERTIME_WORK));
   }

   /**
    * Sets the overtime work attribute.
    *
    * @param work overtime work value
    */
   public void setOvertimeWork(Duration work)
   {
      set(TaskField.OVERTIME_WORK, work);
   }

   /**
    * Retrieves the preleveled start attribute.
    *
    * @return preleveled start
    */
   public Date getPreleveledStart()
   {
      return ((Date) getCachedValue(TaskField.PRELEVELED_START));
   }

   /**
    * Retrieves the preleveled finish attribute.
    *
    * @return preleveled finish
    */
   public Date getPreleveledFinish()
   {
      return ((Date) getCachedValue(TaskField.PRELEVELED_FINISH));
   }

   /**
    * Sets the preleveled start attribute.
    *
    * @param date preleveled start attribute
    */
   public void setPreleveledStart(Date date)
   {
      set(TaskField.PRELEVELED_START, date);
   }

   /**
    * Sets the preleveled finish attribute.
    *
    * @param date preleveled finish attribute
    */
   public void setPreleveledFinish(Date date)
   {
      set(TaskField.PRELEVELED_FINISH, date);
   }

   /**
    * Retrieves the remaining overtime work attribute.
    *
    * @return remaining overtime work
    */
   public Duration getRemainingOvertimeWork()
   {
      return ((Duration) getCachedValue(TaskField.REMAINING_OVERTIME_WORK));
   }

   /**
    * Sets the remaining overtime work attribute.
    *
    * @param work remaining overtime work
    */
   public void setRemainingOvertimeWork(Duration work)
   {
      set(TaskField.REMAINING_OVERTIME_WORK, work);
   }

   /**
    * Retrieves the remaining overtime cost.
    *
    * @return remaining overtime cost value
    */
   public Number getRemainingOvertimeCost()
   {
      return ((Number) getCachedValue(TaskField.REMAINING_OVERTIME_COST));
   }

   /**
    * Sets the remaining overtime cost value.
    *
    * @param cost overtime cost value
    */
   public void setRemainingOvertimeCost(Number cost)
   {
      set(TaskField.REMAINING_OVERTIME_COST, cost);
   }

   /**
    * Retrieves the base calendar instance associated with this task.
    * Note that this attribute appears in MPP9 and MSPDI files.
    *
    * @return ProjectCalendar instance
    */
   public ProjectCalendar getCalendar()
   {
      return ((ProjectCalendar) getCachedValue(TaskField.CALENDAR));
   }

   /**
    * Sets the name of the base calendar associated with this task.
    * Note that this attribute appears in MPP9 and MSPDI files.
    *
    * @param calendar calendar instance
    */
   public void setCalendar(ProjectCalendar calendar)
   {
      set(TaskField.CALENDAR, calendar);
   }

   /**
    * Retrieve a flag indicating if the task is shown as expanded
    * in MS Project. If this flag is set to true, any sub tasks
    * for this current task will be visible. If this is false,
    * any sub tasks will be hidden.
    *
    * @return boolean flag
    */
   public boolean getExpanded()
   {
      return (m_expanded);
   }

   /**
    * Set a flag indicating if the task is shown as expanded
    * in MS Project. If this flag is set to true, any sub tasks
    * for this current task will be visible. If this is false,
    * any sub tasks will be hidden.
    *
    * @param expanded boolean flag
    */
   public void setExpanded(boolean expanded)
   {
      m_expanded = expanded;
   }

   /**
    * Set the start slack.
    * 
    * @param duration start slack
    */
   public void setStartSlack(Duration duration)
   {
      set(TaskField.START_SLACK, duration);
   }

   /**
    * Set the finish slack.
    * 
    * @param duration finish slack
    */
   public void setFinishSlack(Duration duration)
   {
      set(TaskField.FINISH_SLACK, duration);
   }

   /**
    * Retrieve the start slack.
    * 
    * @return start slack
    */
   public Duration getStartSlack()
   {
      Duration startSlack = (Duration) getCachedValue(TaskField.START_SLACK);
      if (startSlack == null)
      {
         Duration duration = getDuration();
         if (duration != null)
         {
            startSlack = DateUtility.getVariance(this, getLateStart(), getEarlyStart(), duration.getUnits());
            set(TaskField.START_SLACK, startSlack);
         }
      }
      return (startSlack);
   }

   /**
    * Retrieve the finish slack.
    * 
    * @return finish slack
    */
   public Duration getFinishSlack()
   {
      Duration finishSlack = (Duration) getCachedValue(TaskField.FINISH_SLACK);
      if (finishSlack == null)
      {
         Duration duration = getDuration();
         if (duration != null)
         {
            finishSlack = DateUtility.getVariance(this, getLateFinish(), getEarlyFinish(), duration.getUnits());
            set(TaskField.FINISH_SLACK, finishSlack);
         }
      }
      return (finishSlack);
   }

   /**
    * Retrieve the value of a field using its alias.
    *
    * @param alias field alias
    * @return field value
    */
   public Object getFieldByAlias(String alias)
   {
      return (getCachedValue(getParentFile().getAliasTaskField(alias)));
   }

   /**
    * Set the value of a field using its alias.
    *
    * @param alias field alias
    * @param value field value
    */
   public void setFieldByAlias(String alias, Object value)
   {
      set(getParentFile().getAliasTaskField(alias), value);
   }

   /**
    * This method retrieves a list of task splits. Each split is represented
    * by a DateRange instance. The list will always follow the pattern
    * task range, split range, task range and so on.
    *
    * Note that this method will return null if the task is not split.
    *
    * @return list of split times
    */
   public List<DateRange> getSplits()
   {
      return (m_splits);
   }

   /**
    * Internal method used to set the list of splits.
    *
    * @param splits list of split times
    */
   public void setSplits(List<DateRange> splits)
   {
      m_splits = splits;
   }

   /**
    * Task splits contain the time up to which the splits are completed.
    *
    * @return Duration of completed time for the splits.
    */
   public Date getSplitCompleteDuration()
   {
      return m_splitsComplete;
   }

   /**
    * Set the time up to which the splits are completed.
    *
    * @param splitsComplete Duration of completed time for the splits.
    */
   public void setSplitCompleteDuration(Date splitsComplete)
   {
      m_splitsComplete = splitsComplete;
   }

   /**
    * Removes this task from the project.
    */
   public void remove()
   {
      getParentFile().removeTask(this);
   }

   /**
    * Retrieve the sub project represented by this task.
    *
    * @return sub project
    */
   public SubProject getSubProject()
   {
      return (m_subProject);
   }

   /**
    * Set the sub project represented by this task.
    *
    * @param subProject sub project
    */
   public void setSubProject(SubProject subProject)
   {
      m_subProject = subProject;
   }

   /**
    * If this is an external task, this method will return  the ID
    * (note: not the unique ID) of the task in the external file.
    * 
    * @return external task ID
    */
   public Integer getExternalTaskID()
   {
      return (m_externalTaskID);
   }

   /**
    * If this is an external task, this method is used to set the ID
    * (note: not the unique ID) of the task in the external file.
    * 
    * @param externalTaskID external task ID
    */
   public void setExternalTaskID(Integer externalTaskID)
   {
      m_externalTaskID = externalTaskID;
   }

   /**
    * Retrieve an enterprise field value.
    * 
    * @param index field index
    * @return field value
    */
   public Number getEnterpriseCost(int index)
   {
      return ((Number) getCachedValue(selectTaskField(ENTERPRISE_COST_FIELDS, index)));
   }

   /**
    * Set an enterprise field value.
    * 
    * @param index field index
    * @param value field value
    */
   public void setEnterpriseCost(int index, Number value)
   {
      set(selectTaskField(ENTERPRISE_COST_FIELDS, index), value);
   }

   /**
    * Retrieve an enterprise field value.
    * 
    * @param index field index
    * @return field value
    */
   public Date getEnterpriseDate(int index)
   {
      return ((Date) getCachedValue(selectTaskField(ENTERPRISE_DATE_FIELDS, index)));
   }

   /**
    * Set an enterprise field value.
    * 
    * @param index field index
    * @param value field value
    */
   public void setEnterpriseDate(int index, Date value)
   {
      set(selectTaskField(ENTERPRISE_DATE_FIELDS, index), value);
   }

   /**
    * Retrieve an enterprise field value.
    * 
    * @param index field index
    * @return field value
    */
   public Duration getEnterpriseDuration(int index)
   {
      return ((Duration) getCachedValue(selectTaskField(ENTERPRISE_DURATION_FIELDS, index)));
   }

   /**
    * Set an enterprise field value.
    * 
    * @param index field index
    * @param value field value
    */
   public void setEnterpriseDuration(int index, Duration value)
   {
      set(selectTaskField(ENTERPRISE_DURATION_FIELDS, index), value);
   }

   /**
    * Retrieve an enterprise field value.
    * 
    * @param index field index
    * @return field value
    */
   public boolean getEnterpriseFlag(int index)
   {
      return (BooleanUtility.getBoolean((Boolean) getCachedValue(selectTaskField(ENTERPRISE_FLAG_FIELDS, index))));
   }

   /**
    * Set an enterprise field value.
    * 
    * @param index field index
    * @param value field value
    */
   public void setEnterpriseFlag(int index, boolean value)
   {
      set(selectTaskField(ENTERPRISE_FLAG_FIELDS, index), value);
   }

   /**
    * Retrieve an enterprise field value.
    * 
    * @param index field index
    * @return field value
    */
   public Number getEnterpriseNumber(int index)
   {
      return ((Number) getCachedValue(selectTaskField(ENTERPRISE_NUMBER_FIELDS, index)));
   }

   /**
    * Set an enterprise field value.
    * 
    * @param index field index
    * @param value field value
    */
   public void setEnterpriseNumber(int index, Number value)
   {
      set(selectTaskField(ENTERPRISE_NUMBER_FIELDS, index), value);
   }

   /**
    * Retrieve an enterprise field value.
    * 
    * @param index field index
    * @return field value
    */
   public String getEnterpriseText(int index)
   {
      return ((String) getCachedValue(selectTaskField(ENTERPRISE_TEXT_FIELDS, index)));
   }

   /**
    * Set an enterprise field value.
    * 
    * @param index field index
    * @param value field value
    */
   public void setEnterpriseText(int index, String value)
   {
      set(selectTaskField(ENTERPRISE_TEXT_FIELDS, index), value);
   }

   /**
    * Set a baseline value.
    * 
    * @param baselineNumber baseline index (1-10)
    * @param value baseline value
    */
   public void setBaselineCost(int baselineNumber, Number value)
   {
      set(selectTaskField(BASELINE_COSTS, baselineNumber), value);
   }

   /**
    * Set a baseline value.
    * 
    * @param baselineNumber baseline index (1-10)
    * @param value baseline value
    */
   public void setBaselineDuration(int baselineNumber, Duration value)
   {
      set(selectTaskField(BASELINE_DURATIONS, baselineNumber), value);
   }

   /**
    * Set a baseline value.
    * 
    * @param baselineNumber baseline index (1-10)
    * @param value baseline value
    */
   public void setBaselineFinish(int baselineNumber, Date value)
   {
      set(selectTaskField(BASELINE_FINISHES, baselineNumber), value);
   }

   /**
    * Set a baseline value.
    * 
    * @param baselineNumber baseline index (1-10)
    * @param value baseline value
    */
   public void setBaselineStart(int baselineNumber, Date value)
   {
      set(selectTaskField(BASELINE_STARTS, baselineNumber), value);
   }

   /**
    * Set a baseline value.
    * 
    * @param baselineNumber baseline index (1-10)
    * @param value baseline value
    */
   public void setBaselineWork(int baselineNumber, Duration value)
   {
      set(selectTaskField(BASELINE_WORKS, baselineNumber), value);
   }

   /**
    * Retrieve a baseline value.
    * 
    * @param baselineNumber baseline index (1-10)
    * @return baseline value
    */
   public Number getBaselineCost(int baselineNumber)
   {
      return ((Number) getCachedValue(selectTaskField(BASELINE_COSTS, baselineNumber)));
   }

   /**
    * Retrieve a baseline value.
    * 
    * @param baselineNumber baseline index (1-10)
    * @return baseline value
    */
   public Duration getBaselineDuration(int baselineNumber)
   {
      return ((Duration) getCachedValue(selectTaskField(BASELINE_DURATIONS, baselineNumber)));
   }

   /**
    * Retrieve a baseline value.
    * 
    * @param baselineNumber baseline index (1-10)
    * @return baseline value
    */
   public Date getBaselineFinish(int baselineNumber)
   {
      return ((Date) getCachedValue(selectTaskField(BASELINE_FINISHES, baselineNumber)));
   }

   /**
    * Retrieve a baseline value.
    * 
    * @param baselineNumber baseline index (1-10)
    * @return baseline value
    */
   public Date getBaselineStart(int baselineNumber)
   {
      return ((Date) getCachedValue(selectTaskField(BASELINE_STARTS, baselineNumber)));
   }

   /**
    * Retrieve a baseline value.
    * 
    * @param baselineNumber baseline index (1-10)
    * @return baseline value
    */
   public Duration getBaselineWork(int baselineNumber)
   {
      return ((Duration) getCachedValue(selectTaskField(BASELINE_WORKS, baselineNumber)));
   }

   /**
    * Retrieve the "complete through" date.
    * 
    * @return complete through date
    */
   public Date getCompleteThrough()
   {
      Date value = (Date) getCachedValue(TaskField.COMPLETE_THROUGH);
      if (value == null)
      {
         int percentComplete = NumberUtility.getInt(getPercentageComplete());
         switch (percentComplete)
         {
            case 0 :
            {
               break;
            }

            case 100 :
            {
               value = getActualFinish();
               break;
            }

            default :
            {
               Duration duration = getDuration();
               double durationValue = (duration.getDuration() * percentComplete) / 100d;
               duration = Duration.getInstance(durationValue, duration.getUnits());
               ProjectCalendar calendar = getCalendar();
               if (calendar == null)
               {
                  calendar = getParentFile().getCalendar();
               }
               value = calendar.getDate(getActualStart(), duration, true);
               break;
            }
         }

         set(TaskField.COMPLETE_THROUGH, value);
      }
      return value;
   }

   /**
    * Retrieve the summary progress date.
    * 
    * @return summary progress date
    */
   public Date getSummaryProgress()
   {
      Date value = (Date) getCachedValue(TaskField.SUMMARY_PROGRESS);
      return value;
   }

   /**
    * Set the summary progress date.
    * 
    * @param value summary progress date
    */
   public void setSummaryProgress(Date value)
   {
      set(TaskField.SUMMARY_PROGRESS, value);
   }

   /**
    * Retrieve the task GUID.
    * 
    * @return task GUID
    */
   public UUID getGUID()
   {
      return (UUID) getCachedValue(TaskField.GUID);
   }

   /**
    * Set the task GUID.
    * 
    * @param value task GUID
    */
   public void setGUID(UUID value)
   {
      set(TaskField.GUID, value);
   }

   /**
    * Retrieves the task mode.
    * 
    * @return task mode
    */
   public TaskMode getTaskMode()
   {
      return (TaskMode) getCachedValue(TaskField.TASK_MODE);
   }

   /**
    * Sets the task mode.
    * 
    * @param mode task mode
    */
   public void setTaskMode(TaskMode mode)
   {
      set(TaskField.TASK_MODE, mode);
   }

   /**
    * Retrieves the active flag.
    * 
    * @return active flag value
    */
   public boolean getActive()
   {
      return (BooleanUtility.getBoolean((Boolean) getCachedValue(TaskField.ACTIVE)));
   }

   /**
    * Sets the active flag.
    * 
    * @param active active flag value
    */
   public void setActive(boolean active)
   {
      set(TaskField.ACTIVE, active);
   }

   /**
    * This method allows a predecessor relationship to be removed from this
    * task instance.  It will only delete relationships that exactly match the 
    * given targetTask, type and lag time.
    *
    * @param targetTask the predecessor task
    * @param type relation type
    * @param lag relation lag
    * @return returns true if the relation is found and removed
    */
   public boolean removePredecessor(Task targetTask, RelationType type, Duration lag)
   {
      boolean matchFound = false;

      //
      // Retrieve the list of predecessors
      //
      List<Relation> predecessorList = getPredecessors();
      if (predecessorList != null && !predecessorList.isEmpty())
      {
         //
         // Ensure that we have a valid lag duration
         //
         if (lag == null)
         {
            lag = Duration.getInstance(0, TimeUnit.DAYS);
         }

         //
         // Ensure that there is a predecessor relationship between
         // these two tasks, and remove it.
         //
         matchFound = removeRelation(predecessorList, targetTask, type, lag);

         //
         // If we have removed a predecessor, then we must remove the
         // corresponding successor entry from the target task list
         //
         if (matchFound)
         {
            //
            // Retrieve the list of successors
            //
            List<Relation> successorList = targetTask.getSuccessors();
            if (successorList != null && !successorList.isEmpty())
            {
               //
               // Ensure that there is a successor relationship between
               // these two tasks, and remove it.
               //
               removeRelation(successorList, this, type, lag);
            }
         }
      }

      return matchFound;
   }

   /**
    * Internal method used to locate an remove an item from a list Relations. 
    * 
    * @param relationList list of Relation instances
    * @param targetTask target relationship task
    * @param type target relationship type
    * @param lag target relationship lag
    * @return true if a relationship was removed
    */
   private boolean removeRelation(List<Relation> relationList, Task targetTask, RelationType type, Duration lag)
   {
      boolean matchFound = false;
      for (Relation relation : relationList)
      {
         if (relation.getTargetTask() == targetTask)
         {
            if (relation.getType() == type && relation.getLag().compareTo(lag) == 0)
            {
               matchFound = relationList.remove(relation);
               break;
            }
         }
      }
      return matchFound;
   }

   /**
    * Maps a field index to a TaskField instance.
    * 
    * @param fields array of fields used as the basis for the mapping.
    * @param index required field index
    * @return TaskField instance
    */
   private TaskField selectTaskField(TaskField[] fields, int index)
   {
      if (index < 1 || index > fields.length)
      {
         throw new IllegalArgumentException(index + " is not a valid field index");
      }
      return (fields[index - 1]);
   }

   /**
    * {@inheritDoc}
    */
   public Object getCachedValue(FieldType field)
   {
      return (field == null ? null : m_array[field.getValue()]);
   }

   /**
    * {@inheritDoc}
    */
   public Object getCurrentValue(FieldType field)
   {
      Object result = null;

      if (field != null)
      {
         switch ((TaskField) field)
         {
            case START_VARIANCE :
            {
               result = getStartVariance();
               break;
            }

            case COST_VARIANCE :
            {
               result = getCostVariance();
               break;
            }

            case DURATION_VARIANCE :
            {
               result = getDurationVariance();
               break;
            }

            case WORK_VARIANCE :
            {
               result = getWorkVariance();
               break;
            }

            case CV :
            {
               result = getCV();
               break;
            }

            case SV :
            {
               result = getSV();
               break;
            }

            case TOTAL_SLACK :
            {
               result = getTotalSlack();
               break;
            }

            case CRITICAL :
            {
               result = Boolean.valueOf(getCritical());
               break;
            }

            case COMPLETE_THROUGH :
            {
               result = getCompleteThrough();
               break;
            }

            default :
            {
               result = m_array[field.getValue()];
               break;
            }
         }
      }

      return (result);
   }

   /**
    * {@inheritDoc}
    */
   public void set(FieldType field, Object value)
   {
      if (field != null)
      {
         int index = field.getValue();
         fireFieldChangeEvent((TaskField) field, m_array[index], value);
         m_array[index] = value;
      }
   }

   /**
    * Handle the change in a field value. Reset any cached calculated
    * values affected by this change, pass on the event to any external
    * listeners.
    * 
    * @param field field changed
    * @param oldValue old field value
    * @param newValue new field value
    */
   private void fireFieldChangeEvent(TaskField field, Object oldValue, Object newValue)
   {
      //
      // Internal event handling
      //
      switch (field)
      {
         case START :
         case BASELINE_START :
         {
            m_array[TaskField.START_VARIANCE.getValue()] = null;
            break;
         }

         case FINISH :
         case BASELINE_FINISH :
         {
            m_array[TaskField.FINISH_VARIANCE.getValue()] = null;
            break;
         }

         case COST :
         case BASELINE_COST :
         {
            m_array[TaskField.COST_VARIANCE.getValue()] = null;
            break;
         }

         case DURATION :
         {
            m_array[TaskField.DURATION_VARIANCE.getValue()] = null;
            m_array[TaskField.COMPLETE_THROUGH.getValue()] = null;
            break;
         }

         case BASELINE_DURATION :
         {
            m_array[TaskField.DURATION_VARIANCE.getValue()] = null;
            break;
         }

         case WORK :
         case BASELINE_WORK :
         {
            m_array[TaskField.WORK_VARIANCE.getValue()] = null;
            break;
         }

         case BCWP :
         case ACWP :
         {
            m_array[TaskField.CV.getValue()] = null;
            m_array[TaskField.SV.getValue()] = null;
            break;
         }

         case BCWS :
         {
            m_array[TaskField.SV.getValue()] = null;
            break;
         }

         case START_SLACK :
         case FINISH_SLACK :
         {
            m_array[TaskField.TOTAL_SLACK.getValue()] = null;
            m_array[TaskField.CRITICAL.getValue()] = null;
            break;
         }

         case EARLY_FINISH :
         case LATE_FINISH :
         {
            m_array[TaskField.FINISH_SLACK.getValue()] = null;
            m_array[TaskField.TOTAL_SLACK.getValue()] = null;
            m_array[TaskField.CRITICAL.getValue()] = null;
            break;
         }

         case EARLY_START :
         case LATE_START :
         {
            m_array[TaskField.START_SLACK.getValue()] = null;
            m_array[TaskField.TOTAL_SLACK.getValue()] = null;
            m_array[TaskField.CRITICAL.getValue()] = null;
            break;
         }

         case ACTUAL_START :
         case PERCENT_COMPLETE :
         {
            m_array[TaskField.COMPLETE_THROUGH.getValue()] = null;
            break;
         }

         default :
         {
            break;
         }
      }

      //
      // External event handling
      //
      if (m_listeners != null)
      {
         for (FieldListener listener : m_listeners)
         {
            listener.fieldChange(this, field, oldValue, newValue);
         }
      }
   }

   /**
    * {@inheritDoc}
    */
   public void addFieldListener(FieldListener listener)
   {
      if (m_listeners == null)
      {
         m_listeners = new LinkedList<FieldListener>();
      }
      m_listeners.add(listener);
   }

   /**
    * {@inheritDoc}
    */
   public void removeFieldListener(FieldListener listener)
   {
      if (m_listeners != null)
      {
         m_listeners.remove(listener);
      }
   }

   /**
    * This method inserts a name value pair into internal storage.
    *
    * @param field task field
    * @param value attribute value
    */
   private void set(FieldType field, boolean value)
   {
      set(field, (value ? Boolean.TRUE : Boolean.FALSE));
   }

   /**
    * {@inheritDoc}
    */
   @Override public String toString()
   {
      SubProject externalTask = getSubProject();
      return ("[Task id=" + getID() + " uniqueID=" + getUniqueID() + " name=" + getName() + (getExternalTask() ? " [EXTERNAL " + externalTask.getFullPath() + " " + getExternalTaskID() + "]" : "") + "]");
   }

   /**
    * Utility method used to determine if the supplied task
    * is a predecessor of the current task.
    * 
    * @param task potential predecessor task
    * @return Boolean flag
    */
   public boolean isPredecessor(Task task)
   {
      return isRelated(task, getPredecessors());
   }

   /**
    * Utility method used to determine if the supplied task
    * is a successor of the current task.
    * 
    * @param task potential successor task
    * @return Boolean flag
    */
   public boolean isSucessor(Task task)
   {
      return isRelated(task, getSuccessors());
   }

   /**
    * Internal method used to test for the existence of a relationship
    * with a task.
    * 
    * @param task target task
    * @param list list of relationships
    * @return boolean flag
    */
   private boolean isRelated(Task task, List<Relation> list)
   {
      boolean result = false;
      if (list != null)
      {
         for (Relation relation : list)
         {
            if (relation.getTargetTask().getUniqueID() == task.getUniqueID())
            {
               result = true;
               break;
            }
         }
      }
      return result;
   }

   /**
    * Array of field values.
    */
   private Object[] m_array = new Object[TaskField.MAX_VALUE];

   /**
    * This is a reference to the parent task, as specified by the
    * outline level.
    */
   private Task m_parent;

   /**
    * This list holds references to all tasks that are children of the
    * current task as specified by the outline level.
    */
   private List<Task> m_children = new LinkedList<Task>();

   /**
    * List of resource assignments for this task.
    */
   private List<ResourceAssignment> m_assignments = new LinkedList<ResourceAssignment>();

   /**
    * Recurring task details associated with this task.
    */
   private RecurringTask m_recurringTask;

   private static final TaskField[] ENTERPRISE_COST_FIELDS =
   {
      TaskField.ENTERPRISE_COST1,
      TaskField.ENTERPRISE_COST2,
      TaskField.ENTERPRISE_COST3,
      TaskField.ENTERPRISE_COST4,
      TaskField.ENTERPRISE_COST5,
      TaskField.ENTERPRISE_COST6,
      TaskField.ENTERPRISE_COST7,
      TaskField.ENTERPRISE_COST8,
      TaskField.ENTERPRISE_COST9,
      TaskField.ENTERPRISE_COST10
   };

   private static final TaskField[] ENTERPRISE_DATE_FIELDS =
   {
      TaskField.ENTERPRISE_DATE1,
      TaskField.ENTERPRISE_DATE2,
      TaskField.ENTERPRISE_DATE3,
      TaskField.ENTERPRISE_DATE4,
      TaskField.ENTERPRISE_DATE5,
      TaskField.ENTERPRISE_DATE6,
      TaskField.ENTERPRISE_DATE7,
      TaskField.ENTERPRISE_DATE8,
      TaskField.ENTERPRISE_DATE9,
      TaskField.ENTERPRISE_DATE10,
      TaskField.ENTERPRISE_DATE11,
      TaskField.ENTERPRISE_DATE12,
      TaskField.ENTERPRISE_DATE13,
      TaskField.ENTERPRISE_DATE14,
      TaskField.ENTERPRISE_DATE15,
      TaskField.ENTERPRISE_DATE16,
      TaskField.ENTERPRISE_DATE17,
      TaskField.ENTERPRISE_DATE18,
      TaskField.ENTERPRISE_DATE19,
      TaskField.ENTERPRISE_DATE20,
      TaskField.ENTERPRISE_DATE21,
      TaskField.ENTERPRISE_DATE22,
      TaskField.ENTERPRISE_DATE23,
      TaskField.ENTERPRISE_DATE24,
      TaskField.ENTERPRISE_DATE25,
      TaskField.ENTERPRISE_DATE26,
      TaskField.ENTERPRISE_DATE27,
      TaskField.ENTERPRISE_DATE28,
      TaskField.ENTERPRISE_DATE29,
      TaskField.ENTERPRISE_DATE30
   };

   private static final TaskField[] ENTERPRISE_DURATION_FIELDS =
   {
      TaskField.ENTERPRISE_DURATION1,
      TaskField.ENTERPRISE_DURATION2,
      TaskField.ENTERPRISE_DURATION3,
      TaskField.ENTERPRISE_DURATION4,
      TaskField.ENTERPRISE_DURATION5,
      TaskField.ENTERPRISE_DURATION6,
      TaskField.ENTERPRISE_DURATION7,
      TaskField.ENTERPRISE_DURATION8,
      TaskField.ENTERPRISE_DURATION9,
      TaskField.ENTERPRISE_DURATION10
   };

   private static final TaskField[] ENTERPRISE_FLAG_FIELDS =
   {
      TaskField.ENTERPRISE_FLAG1,
      TaskField.ENTERPRISE_FLAG2,
      TaskField.ENTERPRISE_FLAG3,
      TaskField.ENTERPRISE_FLAG4,
      TaskField.ENTERPRISE_FLAG5,
      TaskField.ENTERPRISE_FLAG6,
      TaskField.ENTERPRISE_FLAG7,
      TaskField.ENTERPRISE_FLAG8,
      TaskField.ENTERPRISE_FLAG9,
      TaskField.ENTERPRISE_FLAG10,
      TaskField.ENTERPRISE_FLAG11,
      TaskField.ENTERPRISE_FLAG12,
      TaskField.ENTERPRISE_FLAG13,
      TaskField.ENTERPRISE_FLAG14,
      TaskField.ENTERPRISE_FLAG15,
      TaskField.ENTERPRISE_FLAG16,
      TaskField.ENTERPRISE_FLAG17,
      TaskField.ENTERPRISE_FLAG18,
      TaskField.ENTERPRISE_FLAG19,
      TaskField.ENTERPRISE_FLAG20
   };

   private static final TaskField[] ENTERPRISE_NUMBER_FIELDS =
   {
      TaskField.ENTERPRISE_NUMBER1,
      TaskField.ENTERPRISE_NUMBER2,
      TaskField.ENTERPRISE_NUMBER3,
      TaskField.ENTERPRISE_NUMBER4,
      TaskField.ENTERPRISE_NUMBER5,
      TaskField.ENTERPRISE_NUMBER6,
      TaskField.ENTERPRISE_NUMBER7,
      TaskField.ENTERPRISE_NUMBER8,
      TaskField.ENTERPRISE_NUMBER9,
      TaskField.ENTERPRISE_NUMBER10,
      TaskField.ENTERPRISE_NUMBER11,
      TaskField.ENTERPRISE_NUMBER12,
      TaskField.ENTERPRISE_NUMBER13,
      TaskField.ENTERPRISE_NUMBER14,
      TaskField.ENTERPRISE_NUMBER15,
      TaskField.ENTERPRISE_NUMBER16,
      TaskField.ENTERPRISE_NUMBER17,
      TaskField.ENTERPRISE_NUMBER18,
      TaskField.ENTERPRISE_NUMBER19,
      TaskField.ENTERPRISE_NUMBER20,
      TaskField.ENTERPRISE_NUMBER21,
      TaskField.ENTERPRISE_NUMBER22,
      TaskField.ENTERPRISE_NUMBER23,
      TaskField.ENTERPRISE_NUMBER24,
      TaskField.ENTERPRISE_NUMBER25,
      TaskField.ENTERPRISE_NUMBER26,
      TaskField.ENTERPRISE_NUMBER27,
      TaskField.ENTERPRISE_NUMBER28,
      TaskField.ENTERPRISE_NUMBER29,
      TaskField.ENTERPRISE_NUMBER30,
      TaskField.ENTERPRISE_NUMBER31,
      TaskField.ENTERPRISE_NUMBER32,
      TaskField.ENTERPRISE_NUMBER33,
      TaskField.ENTERPRISE_NUMBER34,
      TaskField.ENTERPRISE_NUMBER35,
      TaskField.ENTERPRISE_NUMBER36,
      TaskField.ENTERPRISE_NUMBER37,
      TaskField.ENTERPRISE_NUMBER38,
      TaskField.ENTERPRISE_NUMBER39,
      TaskField.ENTERPRISE_NUMBER40
   };

   private static final TaskField[] ENTERPRISE_TEXT_FIELDS =
   {
      TaskField.ENTERPRISE_TEXT1,
      TaskField.ENTERPRISE_TEXT2,
      TaskField.ENTERPRISE_TEXT3,
      TaskField.ENTERPRISE_TEXT4,
      TaskField.ENTERPRISE_TEXT5,
      TaskField.ENTERPRISE_TEXT6,
      TaskField.ENTERPRISE_TEXT7,
      TaskField.ENTERPRISE_TEXT8,
      TaskField.ENTERPRISE_TEXT9,
      TaskField.ENTERPRISE_TEXT10,
      TaskField.ENTERPRISE_TEXT11,
      TaskField.ENTERPRISE_TEXT12,
      TaskField.ENTERPRISE_TEXT13,
      TaskField.ENTERPRISE_TEXT14,
      TaskField.ENTERPRISE_TEXT15,
      TaskField.ENTERPRISE_TEXT16,
      TaskField.ENTERPRISE_TEXT17,
      TaskField.ENTERPRISE_TEXT18,
      TaskField.ENTERPRISE_TEXT19,
      TaskField.ENTERPRISE_TEXT20,
      TaskField.ENTERPRISE_TEXT21,
      TaskField.ENTERPRISE_TEXT22,
      TaskField.ENTERPRISE_TEXT23,
      TaskField.ENTERPRISE_TEXT24,
      TaskField.ENTERPRISE_TEXT25,
      TaskField.ENTERPRISE_TEXT26,
      TaskField.ENTERPRISE_TEXT27,
      TaskField.ENTERPRISE_TEXT28,
      TaskField.ENTERPRISE_TEXT29,
      TaskField.ENTERPRISE_TEXT30,
      TaskField.ENTERPRISE_TEXT31,
      TaskField.ENTERPRISE_TEXT32,
      TaskField.ENTERPRISE_TEXT33,
      TaskField.ENTERPRISE_TEXT34,
      TaskField.ENTERPRISE_TEXT35,
      TaskField.ENTERPRISE_TEXT36,
      TaskField.ENTERPRISE_TEXT37,
      TaskField.ENTERPRISE_TEXT38,
      TaskField.ENTERPRISE_TEXT39,
      TaskField.ENTERPRISE_TEXT40
   };

   private static final TaskField[] BASELINE_COSTS =
   {
      TaskField.BASELINE1_COST,
      TaskField.BASELINE2_COST,
      TaskField.BASELINE3_COST,
      TaskField.BASELINE4_COST,
      TaskField.BASELINE5_COST,
      TaskField.BASELINE6_COST,
      TaskField.BASELINE7_COST,
      TaskField.BASELINE8_COST,
      TaskField.BASELINE9_COST,
      TaskField.BASELINE10_COST
   };

   private static final TaskField[] BASELINE_DURATIONS =
   {
      TaskField.BASELINE1_DURATION,
      TaskField.BASELINE2_DURATION,
      TaskField.BASELINE3_DURATION,
      TaskField.BASELINE4_DURATION,
      TaskField.BASELINE5_DURATION,
      TaskField.BASELINE6_DURATION,
      TaskField.BASELINE7_DURATION,
      TaskField.BASELINE8_DURATION,
      TaskField.BASELINE9_DURATION,
      TaskField.BASELINE10_DURATION
   };

   private static final TaskField[] BASELINE_STARTS =
   {
      TaskField.BASELINE1_START,
      TaskField.BASELINE2_START,
      TaskField.BASELINE3_START,
      TaskField.BASELINE4_START,
      TaskField.BASELINE5_START,
      TaskField.BASELINE6_START,
      TaskField.BASELINE7_START,
      TaskField.BASELINE8_START,
      TaskField.BASELINE9_START,
      TaskField.BASELINE10_START
   };

   private static final TaskField[] BASELINE_FINISHES =
   {
      TaskField.BASELINE1_FINISH,
      TaskField.BASELINE2_FINISH,
      TaskField.BASELINE3_FINISH,
      TaskField.BASELINE4_FINISH,
      TaskField.BASELINE5_FINISH,
      TaskField.BASELINE6_FINISH,
      TaskField.BASELINE7_FINISH,
      TaskField.BASELINE8_FINISH,
      TaskField.BASELINE9_FINISH,
      TaskField.BASELINE10_FINISH
   };

   private static final TaskField[] BASELINE_WORKS =
   {
      TaskField.BASELINE1_WORK,
      TaskField.BASELINE2_WORK,
      TaskField.BASELINE3_WORK,
      TaskField.BASELINE4_WORK,
      TaskField.BASELINE5_WORK,
      TaskField.BASELINE6_WORK,
      TaskField.BASELINE7_WORK,
      TaskField.BASELINE8_WORK,
      TaskField.BASELINE9_WORK,
      TaskField.BASELINE10_WORK
   };

   private boolean m_null;
   private String m_wbsLevel;
   private boolean m_resumeValid;
   private Integer m_subprojectTaskUniqueID;
   private Integer m_subprojectTaskID;
   private Integer m_subprojectTasksUniqueIDOffset;
   private String m_externalTaskProject;
   private TimeUnit m_levelingDelayFormat;
   private Integer m_physicalPercentComplete;
   private EarnedValueMethod m_earnedValueMethod;
   private Duration m_actualWorkProtected;
   private Duration m_actualOvertimeWorkProtected;
   private boolean m_expanded = true;
   private Integer m_externalTaskID;

   private List<DateRange> m_splits;
   private Date m_splitsComplete;
   private SubProject m_subProject;
   private List<FieldListener> m_listeners;
}
