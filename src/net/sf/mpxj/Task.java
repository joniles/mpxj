/*
 * file:       Task.java
 * author:     Scott Melville
 *             Jon Iles
 * copyright:  (c) Tapster Rock Limited 2002-2003
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

import net.sf.mpxj.utility.BooleanUtility;
import net.sf.mpxj.utility.NumberUtility;


/**
 * This class represents a task record from an MPX file.
 */
public final class Task extends ProjectEntity implements Comparable, ExtendedAttributeContainer
{
   /**
    * Default constructor.
    *
    * @param file Parent file to which this record belongs.
    * @param parent Parent task
    */
   Task (ProjectFile file, Task parent)
   {
      super(file);

      m_parent = parent;

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
            setOutlineLevel(new Integer(1));
         }
         else
         {
            setOutlineLevel(new Integer(NumberUtility.getInt(parent.getOutlineLevel()) + 1));
         }
      }

      if (file.getAutoTaskUniqueID() == true)
      {
         setUniqueID(new Integer(file.getTaskUniqueID()));
      }

      if (file.getAutoTaskID() == true)
      {
         setID(new Integer(file.getTaskID()));
      }
   }

   /**
    * This package-access method is used to automatically generate a value
    * for the WBS field of this task.
    *
    * @param parent Parent Task
    */
   public void generateWBS (Task parent)
   {
      String wbs;

      if (parent == null)
      {
         wbs = Integer.toString(getParentFile().getChildTaskCount() + 1) + ".0";
      }
      else
      {
         wbs = parent.getWBS();

         int index = wbs.lastIndexOf(".0");

         if (index != -1)
         {
            wbs = wbs.substring(0, index);
         }

         wbs += ("." + (parent.getChildTaskCount() + 1));
      }

      setWBS(wbs);
   }

   /**
    * This package-access method is used to automatically generate a value
    * for the Outline Number field of this task.
    *
    * @param parent Parent Task
    */
   public void generateOutlineNumber (Task parent)
   {
      String outline;

      if (parent == null)
      {
         outline = Integer.toString(getParentFile().getChildTaskCount() + 1) + ".0";
      }
      else
      {
         outline = parent.getOutlineNumber();

         int index = outline.lastIndexOf(".0");

         if (index != -1)
         {
            outline = outline.substring(0, index);
         }

         outline += ("." + (parent.getChildTaskCount() + 1));
      }

      setOutlineNumber(outline);
   }

   /**
    * This method is used to add notes to the current task.
    *
    * @param notes notes to be added
    */
   public void setNotes (String notes)
   {
      m_notes = notes;
   }

   /**
    * This method allows nested tasks to be added, with the WBS being
    * completed automatically.
    *
    * @return new task
    */
   public Task addTask ()
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
    * @throws MPXJException Thrown if an invalid outline level is supplied.
    */
   public void addChildTask (Task child, int childOutlineLevel)
      throws MPXJException
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
            ((Task)m_children.get(m_children.size()-1)).addChildTask(child, childOutlineLevel);
         }
      }
   }

   /**
    * This method is used to associate a child task with the current
    * task instance. It has package access, and has been designed to
    * allow the hierarchical outline structure of tasks in an MPX
    * file to be updated once all of the task data has been read.
    *
    * @param child child task
    */
   void addChildTask (Task child)
   {
      child.m_parent = this;
      m_children.add(child);
   }

   /**
    * Removes a child task.
    *
    * @param child child task instance
    */
   void removeChildTask (Task child)
   {
      m_children.remove(child);
      setSummary(!m_children.isEmpty());
   }

   /**
    * This method allows the list of child tasks to be cleared in preparation
    * for the hierarchical task structure to be built.
    */
   void clearChildTasks ()
   {
      m_children.clear();
   }

   /**
    * This method allows recurring task details to be added to the
    * current task.
    *
    * @return RecurringTask object
    * @throws MPXJException thrown if more than one one recurring task is added
    */
   public RecurringTask addRecurringTask ()
      throws MPXJException
   {
      if (m_recurringTask != null)
      {
         throw new MPXJException(MPXJException.MAXIMUM_RECORDS);
      }

      m_recurringTask = new RecurringTask(getParentFile());

      return (m_recurringTask);
   }

   /**
    * This method retrieves the recurring task record. If the current
    * task is not a recurring task, then this method will return null.
    *
    * @return Recurring task record.
    */
   public RecurringTask getRecurringTask ()
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
   public ResourceAssignment addResourceAssignment (Resource resource)
   {
      Iterator iter = m_assignments.iterator();
      ResourceAssignment assignment = null;
      Integer resourceUniqueID = resource.getUniqueID();
      Integer uniqueID;

      while (iter.hasNext() == true)
      {
         assignment = (ResourceAssignment)iter.next();
         uniqueID = assignment.getResourceUniqueID();
         if (uniqueID.equals(resourceUniqueID) == true)
         {
            break;
         }
         assignment = null;
      }

      if (assignment == null)
      {
         assignment = new ResourceAssignment(getParentFile(), this);
         m_assignments.add(assignment);
         getParentFile().addResourceAssignment(assignment);

         assignment.setResourceID(resource.getID());
         assignment.setResourceUniqueID(resourceUniqueID);
         assignment.setWork(getDuration());
         assignment.setUnits(ResourceAssignment.DEFAULT_UNITS);

         resource.addResourceAssignment(assignment);
      }

      return (assignment);
   }

   /**
    * This method allows a resource assignment to be added to the
    * current task. The data for the resource assignment is derived from
    * an MPX file record.
    *
    * @return ResourceAssignment object
    */
   public ResourceAssignment addResourceAssignment ()
   {
      ResourceAssignment assignment = new ResourceAssignment(getParentFile(), this);
      m_assignments.add(assignment);
      getParentFile().getAllResourceAssignments().add(assignment);
      return (assignment);
   }

   /**
    * This method allows the list of resource assignments for this
    * task to be retrieved.
    *
    * @return list of resource assignments
    */
   public List getResourceAssignments ()
   {
      return (m_assignments);
   }

   /**
    * Internal method used as part of the process of removing a
    * resource assignment.
    *
    * @param assignment resource assignment to be removed
    */
   void removeResourceAssignment (ResourceAssignment assignment)
   {
      m_assignments.remove(assignment);
   }

   /**
    * This method allows a predecessor relationship to be added to this
    * task instance.
    *
    * @return relationship
    */
   public Relation addPredecessor ()
   {
      return (addPredecessor(null));
   }

   /**
    * This method allows a predecessor relationship to be added to this
    * task instance.
    *
    * @param task the predecessor task
    * @return relationship
    */
   public Relation addPredecessor (Task task)
   {
      //
      // Retrieve the list of predecessors
      //
      List list = (List)get(PREDECESSORS);

      if (list == null)
      {
         list = new LinkedList();
         set(PREDECESSORS, list);
      }

      //
      // Ensure that there is only one relationship between
      // these two tasks.
      //
      Relation rel = null;

      if (task != null)
      {
         Iterator iter = list.iterator();

         while (iter.hasNext() == true)
         {
            rel = (Relation)iter.next();
            if (NumberUtility.equals(rel.getTaskID(), task.getID()))
            {
               break;
            }
            rel = null;
         }
      }

      //
      // If necessary, create a new relationship
      //
      if (rel == null)
      {
         rel = new Relation(getParentFile());

         if (task != null)
         {
            rel.setTaskID(task.getID());
            rel.setTaskUniqueID(task.getUniqueID());
         }

         list.add(rel);
      }

      return (rel);
   }

   /**
    * This method allows a predecessor relationship to be added to this
    * task instance.
    *
    * @return relationship
    */
   public Relation addUniqueIdPredecessor ()
   {
      return (addUniqueIdPredecessor(null));
   }

   /**
    * This method allows a predecessor relationship to be added to this
    * task instance.
    *
    * @param task the predecessor task
    * @return relationship
    */
   public Relation addUniqueIdPredecessor (Task task)
   {
      //
      // Retrieve the list of predecessors
      //
      List list = (List)get(UNIQUE_ID_PREDECESSORS);

      if (list == null)
      {
         list = new LinkedList();
         set(UNIQUE_ID_PREDECESSORS, list);
      }

      //
      // Ensure that there is only one relationship between
      // these two tasks.
      //
      Relation rel = null;

      if (task != null)
      {
         Iterator iter = list.iterator();

         while (iter.hasNext() == true)
         {
            rel = (Relation)iter.next();
            if (NumberUtility.equals(rel.getTaskUniqueID(), task.getUniqueID()))
            {
               break;
            }
            rel = null;
         }
      }

      //
      // If necessary, create a new relationship
      //
      if (rel == null)
      {
         rel = new Relation(getParentFile());

         if (task != null)
         {
            rel.setTaskID(task.getID());
            rel.setTaskUniqueID(task.getUniqueID());
         }

         list.add(rel);
      }

      return (rel);
   }

   /**
    * This method allows a successor relationship to be added to this
    * task instance.
    *
    * @return relationship
    */
   public Relation addSuccessor ()
   {
      return (addSuccessor(null));
   }

   /**
    * This method allows a successor relationship to be added to this
    * task instance.
    *
    * @param task the successor task
    * @return relationship
    */
   public Relation addSuccessor (Task task)
   {
      List list = (List)get(SUCCESSORS);

      if (list == null)
      {
         list = new LinkedList();
         set(SUCCESSORS, list);
      }

      Relation rel = new Relation(getParentFile());

      if (task != null)
      {
         rel.setTaskID(task.getID());
         rel.setTaskUniqueID(task.getUniqueID());
      }

      list.add(rel);

      return (rel);
   }

   /**
    * This method allows a successor relationship to be added to this
    * task instance.
    *
    * @return relationship
    */
   public Relation addUniqueIdSuccessor ()
   {
      return (addUniqueIdSuccessor(null));
   }

   /**
    * This method allows a successor relationship to be added to this
    * task instance.
    *
    * @param task the successor task
    * @return relationship
    */
   public Relation addUniqueIdSuccessor (Task task)
   {
      List list = (List)get(UNIQUE_ID_SUCCESSORS);

      if (list == null)
      {
         list = new LinkedList();
         set(UNIQUE_ID_SUCCESSORS, list);
      }

      Relation rel = new Relation(getParentFile());

      if (task != null)
      {
         rel.setTaskID(task.getID());
         rel.setTaskUniqueID(task.getUniqueID());
      }

      list.add(rel);

      return (rel);
   }

   /**
    * This method is used to set the value of a field in the task,
    * and also to ensure that the field exists in the task model
    * record.
    *
    * @param field field to be added or updated.
    * @param val new value for field.
    */
   public void set (int field, Object val)
   {
      put(field, val);
   }

   /**
    * This method is used to set the value of a field in the task,
    * and also to ensure that the field exists in the task model
    * record.
    *
    * @param field field to be added or updated.
    * @param val new value for field.
    */
   private void set (int field, boolean val)
   {
      put(field, val);
   }

   /**
    * The % Complete field contains the current status of a task, expressed
    * as the percentage of the
    * task's duration that has been completed. You can enter percent complete,
    * or you can have
    * Microsoft Project calculate it for you based on actual duration.
    *
    * @param val value to be set
    */
   public void setPercentageComplete (Number val)
   {
      set(PERCENTAGE_COMPLETE, val);
   }

   /**
    * The % Work Complete field contains the current status of a task,
    * expressed as the
    * percentage of the task's work that has been completed. You can enter
    * percent work
    * complete, or you can have Microsoft Project calculate it for you
    * based on actual
    * work on the task.
    *
    * @param val value to be set
    */
   public void setPercentageWorkComplete (Number val)
   {
      set(PERCENTAGE_WORK_COMPLETE, val);
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
   public void setActualCost (Number val)
   {
      set(ACTUAL_COST, val);
   }

   /**
    * The Actual Duration field shows the span of actual working time for a
    * task so far,
    * based on the scheduled duration and current remaining work or
    * completion percentage.
    *
    * @param val value to be set
    */
   public void setActualDuration (Duration val)
   {
      set(ACTUAL_DURATION, val);
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
   public void setActualFinish (Date val)
   {
      set(ACTUAL_FINISH, val);
   }

   /**
    * The Actual Start field shows the date and time that a task actually began.
    * When a task is first created, the Actual Start field contains "NA." Once you
    * enter the first actual work or a completion percentage for a task, Microsoft
    * Project sets the actual start date to the scheduled start date.
    * @param val value to be set
    */
   public void setActualStart (Date val)
   {
      set(ACTUAL_START, val);
   }

   /**
    * The Actual Work field shows the amount of work that has already been
    * done by the
    * resources assigned to a task.
    * @param val value to be set
    */
   public void setActualWork (Duration val)
   {
      set(ACTUAL_WORK, val);
   }

   /**
    * The Baseline Cost field shows the total planned cost for a task.
    * Baseline cost is also referred to as budget at completion (BAC).
    *
    * @param val the amount to be set
    */
   public void setBaselineCost (Number val)
   {
      set(BASELINE_COST, val);
   }

   /**
    * The Baseline Duration field shows the original span of time planned to
    * complete a task.
    *
    * @param val duration
    */
   public void setBaselineDuration (Duration val)
   {
      set(BASELINE_DURATION, val);
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
   public void setBaselineFinish (Date val)
   {
      set(BASELINE_FINISH, val);
   }

   /**
    * The Baseline Start field shows the planned beginning date for a task at
    * the time
    * you saved a baseline. Information in this field becomes available when you
    * set a baseline.
    *
    * @param val Date to be set
    */
   public void setBaselineStart (Date val)
   {
      set(BASELINE_START, val);
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
   public void setBaselineWork (Duration val)
   {
      set(BASELINE_WORK, val);
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
   public void setBCWP (Number val)
   {
      set(BCWP, val);
   }

   /**
    * The BCWS (budgeted cost of work scheduled) field contains the cumulative
    * timephased baseline costs up to the status date or today's date.
    *
    * @param val the amount to set
    */
   public void setBCWS (Number val)
   {
      set(BCWS, val);
   }

   /**
    * The Confirmed field indicates whether all resources assigned to a task have
    * accepted or rejected the task assignment in response to a TeamAssign message
    * regarding their assignments.
    *
    * @param val boolean value
    */
   public void setConfirmed (boolean val)
   {
      set(CONFIRMED, val);
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
   public void setConstraintDate (Date val)
   {
      set(CONSTRAINT_DATE, val);
   }

   /**
    * Private method for dealing with string parameters from File.
    *
    * @param type string constraint type
    */
   public void setConstraintType (ConstraintType type)
   {
      set(CONSTRAINT_TYPE, type);
   }

   /**
    * The Contact field contains the name of an individual
    * responsible for a task.
    *
    * @param val value to be set
    */
   public void setContact (String val)
   {
      set(CONTACT, val);
   }

   /**
    * The Cost field shows the total scheduled, or projected, cost for a task,
    * based on costs already incurred for work performed by all resources assigned
    * to the task, in addition to the costs planned for the remaining work for the
    * assignment. This can also be referred to as estimate at completion (EAC).
    *
    * @param val amount
    */
   public void setCost (Number val)
   {
      set(COST, val);
   }

   /**
    * The Cost1-10 fields show any custom task cost information you want to enter
    * in your project.
    *
    * @param val amount
    */
   public void setCost1 (Number val)
   {
      set(COST1, val);
   }

   /**
    * The Cost1-10 fields show any custom task cost information you want to
    * enter in your project.
    *
    * @param val amount
    */
   public void setCost2 (Number val)
   {
      set(COST2, val);
   }

   /**
    * The Cost1-10 fields show any custom task cost information you want to
    * enter in your project.
    *
    * @param val amount
    */
   public void setCost3 (Number val)
   {
      set(COST3, val);
   }

   /**
    * The Cost Variance field shows the difference between the
    * baseline cost and total cost for a task. The total cost is the
    * current estimate of costs based on actual costs and remaining costs.
    * This is also referred to as variance at completion (VAC).
    *
    * @param val amount
    */
   public void setCostVariance (Number val)
   {
      set(COST_VARIANCE, val);
   }

   /**
    * The Created field contains the date and time when a task was
    * added to the project.
    *
    * @param val date
    */
   public void setCreateDate (Date val)
   {
      set(CREATE_DATE, val);
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
   public void setCritical (boolean val)
   {
      set(CRITICAL, val);
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
   public void setCV (Number val)
   {
      set(CV, val);
   }

   /**
    * Set amount of delay as elapsed real time.
    *
    * @param val elapsed time
    */
   public void setDelay (Duration val)
   {
      set(DELAY, val);
   }

   /**
    * The Duration field is the total span of active working time for a task.
    * This is generally the amount of time from the start to the finish of a task.
    * The default for new tasks is 1 day (1d).
    *
    * @param val duration
    */
   public void setDuration (Duration val)
   {
      set(DURATION, val);
   }

   /**
    * User defined duration field.
    *
    * @param duration Duration value
    */
   public void setDuration1 (Duration duration)
   {
      set(DURATION1, duration);
   }

   /**
    * User defined duration field.
    *
    * @param duration Duration value
    */
   public void setDuration2 (Duration duration)
   {
      set(DURATION2, duration);
   }

   /**
    * User defined duration field.
    *
    * @param duration Duration value
    */
   public void setDuration3 (Duration duration)
   {
      set(DURATION3, duration);
   }

   /**
    * The Duration Variance field contains the difference between the
    * baseline duration of a task and the forecast or actual duration
    * of the task.
    *
    * @param duration duration value
    */
   public void setDurationVariance (Duration duration)
   {
      set(DURATION_VARIANCE, duration);
   }

   /**
    * The Early Finish field contains the earliest date that a task
    * could possibly finish, based on early finish dates of predecessor
    * and successor tasks, other constraints, and any leveling delay.
    *
    * @param date Date value
    */
   public void setEarlyFinish (Date date)
   {
      set(EARLY_FINISH, date);
   }

   /**
    * The Early Start field contains the earliest date that a task could
    * possibly begin, based on the early start dates of predecessor and
    * successor tasks, and other constraints.
    *
    * @param date Date value
    */
   public void setEarlyStart (Date date)
   {
      set(EARLY_START, date);
   }

   /**
    * The Finish field shows the date and time that a task is scheduled to be
    * completed. MS project allows a finish date to be entered, and will
    * calculate the duartion, or a duration can be supplied and MS Project
    * will calculate the finish date.
    *
    * @param date Date value
    */
   public void setFinish (Date date)
   {
      set(FINISH, date);
   }

   /**
    * User defined finish date field.
    *
    * @param date Date value
    */
   public void setFinish1 (Date date)
   {
      set(FINISH1, date);
   }

   /**
    * User defined finish date field.
    *
    * @param date Date value
    */
   public void setFinish2 (Date date)
   {
      set(FINISH2, date);
   }

   /**
    * User defined finish date field.
    *
    * @param date Date value
    */
   public void setFinish3 (Date date)
   {
      set(FINISH3, date);
   }

   /**
    * User defined finish date field.
    *
    * @param date Date value
    */
   public void setFinish4 (Date date)
   {
      set(FINISH4, date);
   }

   /**
    * User defined finish date field.
    *
    * @param date Date value
    */
   public void setFinish5 (Date date)
   {
      set(FINISH5, date);
   }

   /**
    * The Finish Variance field contains the amount of time that represents the
    * difference between a task's baseline finish date and its forecast
    * or actual finish date.
    *
    * @param duration duration value
    */
   public void setFinishVariance (Duration duration)
   {
      set(FINISH_VARIANCE, duration);
   }

   /**
    * Despite the name, this flag sets the task type. If the suppied value is
    * false, the task type shown in MS Project will be set to fixed units. If
    * the value is true, the task type will be set to fixed duration.
    *
    * @param val value to be set
    */
   public void setFixed (boolean val)
   {
      set(FIXED, val);
   }

   /**
    * The Fixed Cost field shows any task expense that is not associated
    * with a resource cost.
    *
    * @param val amount
    */
   public void setFixedCost (Number val)
   {
      set(FIXED_COST, val);
   }

   /**
    * User defined flag field.
    *
    * @param val boolean value
    */
   public void setFlag1 (boolean val)
   {
      set(FLAG1, val);
   }

   /**
    * User defined flag field.
    *
    * @param val boolean value
    */
   public void setFlag2 (boolean val)
   {
      set(FLAG2, val);
   }

   /**
    * User defined flag field.
    *
    * @param val boolean value
    */
   public void setFlag3 (boolean val)
   {
      set(FLAG3, val);
   }

   /**
    * User defined flag field.
    *
    * @param val boolean value
    */
   public void setFlag4 (boolean val)
   {
      set(FLAG4, val);
   }

   /**
    * User defined flag field.
    *
    * @param val boolean value
    */
   public void setFlag5 (boolean val)
   {
      set(FLAG5, val);
   }

   /**
    * User defined flag field.
    *
    * @param val boolean value
    */
   public void setFlag6 (boolean val)
   {
      set(FLAG6, val);
   }

   /**
    * User defined flag field.
    *
    * @param val boolean value
    */
   public void setFlag7 (boolean val)
   {
      set(FLAG7, val);
   }

   /**
    * User defined flag field.
    *
    * @param val boolean value
    */
   public void setFlag8 (boolean val)
   {
      set(FLAG8, val);
   }

   /**
    * User defined flag field.
    *
    * @param val boolean value
    */
   public void setFlag9 (boolean val)
   {
      set(FLAG9, val);
   }

   /**
    * User defined flag field.
    *
    * @param val boolean value
    */
   public void setFlag10 (boolean val)
   {
      set(FLAG10, val);
   }

   /**
    * The Free Slack field contains the amount of time that a task can be
    * delayed without delaying any successor tasks. If the task has no
    * successors, free slack is the amount of time that a task can be delayed
    * without delaying the entire project's finish date.
    *
    * @param duration duration value
    */
   public void setFreeSlack (Duration duration)
   {
      set(FREE_SLACK, duration);
   }

   /**
    * The Hide Bar flag indicates whether the Gantt bars and Calendar bars
    * for a task are hidden when this project's data is displayed in MS Project.
    *
    * @param flag boolean value
    */
   public void setHideBar (boolean flag)
   {
      set(HIDE_BAR, flag);
   }

   /**
    * The ID field contains the identifier number that Microsoft Project
    * automatically assigns to each task as you add it to the project.
    * The ID indicates the position of a task with respect to the other tasks.
    *
    * @param val ID
    */
   public void setID (Integer val)
   {
      ProjectFile parent = getParentFile();
      Integer previous = getID();

      if (previous != null)
      {
         parent.unmapTaskID(previous);
      }

      parent.mapTaskID(val, this);

      set(ID, val);
   }

   /**
    * The Late Finish field contains the latest date that a task can finish
    * without delaying the finish of the project. This date is based on the
    * task's late start date, as well as the late start and late finish dates
    * of predecessor and successor tasks, and other constraints.
    *
    * @param date date value
    */
   public void setLateFinish (Date date)
   {
      set(LATE_FINISH, date);
   }

   /**
    * The Late Start field contains the latest date that a task can start
    * without delaying the finish of the project. This date is based on the
    * task's start date, as well as the late start and late finish dates of
    * predecessor and successor tasks, and other constraints.
    *
    * @param date date value
    */
   public void setLateStart (Date date)
   {
      set(LATE_START, date);
   }

   /**
    * The Linked Fields field indicates whether there are OLE links to the task,
    * either from elsewhere in the active project, another Microsoft Project
    * file, or from another program.
    *
    * @param flag boolean value
    */
   public void setLinkedFields (boolean flag)
   {
      set(LINKED_FIELDS, flag);
   }

   /**
    * This is a user defined field used to mark a task for some form of
    * additional action.
    *
    * @param flag boolean value
    */
   public void setMarked (boolean flag)
   {
      set(MARKED, flag);
   }

   /**
    * The Milestone field indicates whether a task is a milestone.
    *
    * @param flag boolean value
    */
   public void setMilestone (boolean flag)
   {
      set(MILESTONE, flag);
   }

   /**
    * The Name field contains the name of a task.
    *
    * @param name task name
    */
   public void setName (String name)
   {
      set(NAME, name);
   }

   /**
    * Sets a numeric value.
    *
    * @param val Numeric value
    */
   public void setNumber1 (Number val)
   {
      set(NUMBER1, val);
   }

   /**
    * Sets a numeric value.
    *
    * @param val Numeric value
    */
   public void setNumber2 (Number val)
   {
      set(NUMBER2, val);
   }

   /**
    * Sets a numeric value.
    *
    * @param val Numeric value
    */
   public void setNumber3 (Number val)
   {
      set(NUMBER3, val);
   }

   /**
    * Sets a numeric value.
    *
    * @param val Numeric value
    */
   public void setNumber4 (Number val)
   {
      set(NUMBER4, val);
   }

   /**
    * Sets a numeric value.
    *
    * @param val Numeric value
    */
   public void setNumber5 (Number val)
   {
      set(NUMBER5, val);
   }

   /**
    * The Objects field contains the number of objects attached to a task.
    *
    * @param val - integer value
    */
   public void setObjects (Integer val)
   {
      set(OBJECTS, val);
   }

   /**
    * The Outline Level field contains the number that indicates the level of
    * the task in the project outline hierarchy.
    *
    * @param val - int
    */
   public void setOutlineLevel (Integer val)
   {
      set(OUTLINE_LEVEL, val);
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
   public void setOutlineNumber (String val)
   {
      set(OUTLINE_NUMBER, val);
   }

   /**
    * The Predecessors field lists the task ID numbers for the predecessor
    * tasks on which the task depends before it can be started or finished.
    * Each predecessor is linked to the task by a specific type of task
    * dependency
    * and a lead time or lag time.
    *
    * @param list list of relationships
    */
   public void setPredecessors (List list)
   {
      set(PREDECESSORS, list);
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
   public void setPriority (Priority priority)
   {
      set(PRIORITY, priority);
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
   public void setProject (String val)
   {
      set(PROJECT, val);
   }

   /**
    * The Remaining Cost field shows the remaining scheduled expense of a task that
    * will be incurred in completing the remaining scheduled work by all resources
    * assigned to the task.
    *
    * @param val - currency amount
    */
   public void setRemainingCost (Number val)
   {
      set(REMAINING_COST, val);
   }

   /**
    * The Remaining Duration field shows the amount of time required to complete
    * the unfinished portion of a task.
    *
    * @param val - duration.
    */
   public void setRemainingDuration (Duration val)
   {
      set(REMAINING_DURATION, val);
   }

   /**
    * The Remaining Work field shows the amount of time, or person-hours,
    * still required by all assigned resources to complete a task.
    * @param val  - duration
    */
   public void setRemainingWork (Duration val)
   {
      set(REMAINING_WORK, val);
   }

   /**
    * The Resource Group field contains the list of resource groups to which the
    * resources assigned to a task belong.
    *
    * @param val - String list
    */
   public void setResourceGroup (String val)
   {
      set(RESOURCE_GROUP, val);
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
   public void setResourceInitials (String val)
   {
      set(RESOURCE_INITIALS, val);
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
   public void setResourceNames (String val)
   {
      set(RESOURCE_NAMES, val);
   }

   /**
    * The Resume field shows the date that the remaining portion of a task is
    * scheduled to resume after you enter a new value for the % Complete field.
    * The Resume field is also recalculated when the remaining portion of a task
    * is moved to a new date.
    *
    * @param val - Date
    */
   public void setResume (Date val)
   {
      set(RESUME, val);
   }

   /**
    * No help info. Earliest possible resume date?
    * @param val - Date
    */
   public void setResumeNoEarlierThan (Date val)
   {
      set(RESUME_NO_EARLIER_THAN, val);
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
   public void setRollup (boolean val)
   {
      set(ROLLUP, val);
   }

   /**
    * The Start field shows the date and time that a task is scheduled to begin.
    * You can enter the start date you want, to indicate the date when the task
    * should begin. Or, you can have Microsoft Project calculate the start date.
    * @param val - Date
    */
   public void setStart (Date val)
   {
      set(START, val);
   }

   /**
    * The Start1 through Start10 fields are custom fields that show any specific
    * task start date information you want to enter and store separately in
    * your project.
    *
    * @param val - Date
    */
   public void setStart1 (Date val)
   {
      set(START1, val);
   }

   /**
    * The Start1 through Start10 fields are custom fields that show any specific
    * task start date information you want to enter and store separately in
    * your project.
    *
    * @param val - Date
    */
   public void setStart2 (Date val)
   {
      set(START2, val);
   }

   /**
    * The Start1 through Start10 fields are custom fields that show any specific
    * task start date information you want to enter and store separately in
    * your project.
    *
    * @param val - Date
    */
   public void setStart3 (Date val)
   {
      set(START3, val);
   }

   /**
    * The Start1 through Start10 fields are custom fields that show any specific
    * task start date information you want to enter and store separately in
    * your project.
    *
    * @param val - Date
    */
   public void setStart4 (Date val)
   {
      set(START4, val);
   }

   /**
    * The Start1 through Start10 fields are custom fields that show any specific
    * task start date information you want to enter and store separately in
    * your project.
    *
    * @param val - Date
    */
   public void setStart5 (Date val)
   {
      set(START5, val);
   }

   /**
    * The Start Variance field contains the amount of time that represents the
    * difference between a task's baseline start date and its currently
    * scheduled start date.
    *
    * @param val - duration
    */
   public void setStartVariance (Duration val)
   {
      set(START_VARIANCE, val);
   }

   /**
    * The Stop field shows the date that represents the end of the actual
    * portion of a task. Typically, Microsoft Project calculates the stop date.
    * However, you can edit this date as well.
    *
    * @param val - Date
    */
   public void setStop (Date val)
   {
      set(STOP, val);
   }

   /**
    * The Subproject File field contains the name of a project inserted into
    * the active project file. The Subproject File field contains the inserted
    * project's path and file name.
    *
    * @param val - String
    */
   public void setSubprojectName (String val)
   {
      set(SUBPROJECT_NAME, val);
   }

   /**
    * The Successors field lists the task ID numbers for the successor tasks
    * to a task.
    * A task must start or finish before successor tasks can start or finish.
    * Each successor is linked to the task by a specific type of task dependency
    * and a lead time or lag time.
    *
    * @param list list of relationships
    */
   public void setSuccessors (List list)
   {
      set(SUCCESSORS, list);
   }

   /**
    * The Summary field indicates whether a task is a summary task.
    *
    * @param val - boolean
    */
   public void setSummary (boolean val)
   {
      set(SUMMARY, val);
   }

   /**
    * The SV (earned value schedule variance) field shows the difference
    * in cost terms between the current progress and the baseline plan
    * of the task up to the status date or today's date. You can use SV
    * to check costs to determine whether tasks are on schedule.
    * @param val - currency amount
    */
   public void setSV (Number val)
   {
      set(SV, val);
   }

   /**
    * The Text1-30 fields show any custom text information you want to enter
    * in your project about tasks.
    *
    * @param val - String
    */
   public void setText1 (String val)
   {
      set(TEXT1, val);
   }

   /**
    * The Text1-30 fields show any custom text information you want to
    * enter in your project about tasks.
    *
    * @param val - String
    */
   public void setText2 (String val)
   {
      set(TEXT2, val);
   }

   /**
    * The Text1-30 fields show any custom text information you want to
    * enter in your project about tasks.
    *
    * @param val - String
    */
   public void setText3 (String val)
   {
      set(TEXT3, val);
   }

   /**
    * The Text1-30 fields show any custom text information you want to
    * enter in your project about tasks.
    *
    * @param val - String
    */
   public void setText4 (String val)
   {
      set(TEXT4, val);
   }

   /**
    * The Text1-30 fields show any custom text information you want to
    * enter in your project about tasks.
    *
    * @param val - String
    */
   public void setText5 (String val)
   {
      set(TEXT5, val);
   }

   /**
    * The Text1-30 fields show any custom text information you want to
    * enter in your project about tasks.
    *
    * @param val - String
    */
   public void setText6 (String val)
   {
      set(TEXT6, val);
   }

   /**
    * The Text1-30 fields show any custom text information you want to
    * enter in your project about tasks.
    *
    * @param val - String
    */
   public void setText7 (String val)
   {
      set(TEXT7, val);
   }

   /**
    * The Text1-30 fields show any custom text information you want to
    * enter in your project about tasks.
    *
    * @param val - String
    */
   public void setText8 (String val)
   {
      set(TEXT8, val);
   }

   /**
    * The Text1-30 fields show any custom text information you want to
    * enter in your project about tasks.
    *
    * @param val - String
    */
   public void setText9 (String val)
   {
      set(TEXT9, val);
   }

   /**
    * The Text1-30 fields show any custom text information you want to
    * enter in your project about tasks.
    *
    * @param val - String
    */
   public void setText10 (String val)
   {
      set(TEXT10, val);
   }

   /**
    * The Total Slack field contains the amount of time a task can be delayed
    * without delaying the project's finish date.
    *
    * @param val - duration
    */
   public void setTotalSlack (Duration val)
   {
      set(TOTAL_SLACK, val);
   }

   /**
    * The Unique ID field contains the number that Microsoft Project
    * automatically designates whenever a new task is created.
    * This number indicates the sequence in which the task was created,
    * regardless of placement in the schedule.
    *
    * @param val unique ID
    */
   public void setUniqueID (Integer val)
   {
      ProjectFile parent = getParentFile();
      Integer previous = getUniqueID();

      if (previous != null)
      {
         parent.unmapTaskUniqueID(previous);
      }

      parent.mapTaskUniqueID(val, this);

      set(UNIQUE_ID, val);
   }

   /**
    * The Unique ID Predecessors field lists the unique ID numbers for
    * the predecessor
    * tasks on which a task depends before it can be started or finished.
    * Each predecessor is linked to the task by a specific type of
    * task dependency
    * and a lead time or lag time.
    *
    * @param list list of relationships
    */
   public void setUniqueIDPredecessors (List list)
   {
      set(UNIQUE_ID_PREDECESSORS, list);
   }

   /**
    * The Unique ID Successors field lists the unique ID numbers for the successor
    * tasks to a task. A task must start or finish before successor tasks can start
    * or finish. Each successor is linked to the task by a specific type of task
    * dependency and a lead time or lag time.
    *
    * @param list list of relationships
    */
   public void setUniqueIDSuccessors (List list)
   {
      set(UNIQUE_ID_SUCCESSORS, list);
   }

   /**
    * The Update Needed field indicates whether a TeamUpdate message should
    * be sent to the assigned resources because of changes to the start date,
    * finish date, or resource reassignments of the task.
    *
    * @param val - boolean
    */
   public void setUpdateNeeded (boolean val)
   {
      set(UPDATE_NEEDED, val);
   }

   /**
    * The work breakdown structure code. The WBS field contains an alphanumeric
    * code you can use to represent the task's position within the hierarchical
    * structure of the project. This field is similar to the outline number,
    * except that you can edit it.
    *
    * @param val - String
    */
   public void setWBS (String val)
   {
      set(WBS, val);
   }

   /**
    * The Work field shows the total amount of work scheduled to be performed
    * on a task by all assigned resources. This field shows the total work,
    * or person-hours, for a task.
    *
    * @param val - duration
    */
   public void setWork (Duration val)
   {
      set(WORK, val);
   }

   /**
    * The Work Variance field contains the difference between a task's baseline
    * work and the currently scheduled work.
    *
    * @param val - duration
    */
   public void setWorkVariance (Duration val)
   {
      set(WORK_VARIANCE, val);
   }

   /**
    * The % Complete field contains the current status of a task,
    * expressed as the percentage of the task's duration that has been completed.
    * You can enter percent complete, or you can have Microsoft Project calculate
    * it for you based on actual duration.
    * @return percentage as float
    */
   public Number getPercentageComplete ()
   {
      return ((Number)get(PERCENTAGE_COMPLETE));
   }

   /**
    * The % Work Complete field contains the current status of a task,
    * expressed as the percentage of the task's work that has been completed.
    * You can enter percent work complete, or you can have Microsoft Project
    * calculate it for you based on actual work on the task.
    *
    * @return percentage as float
    */
   public Number getPercentageWorkComplete ()
   {
      return ((Number)get(PERCENTAGE_WORK_COMPLETE));
   }

   /**
    * The Actual Cost field shows costs incurred for work already performed
    * by all resources on a task, along with any other recorded costs associated
    * with the task. You can enter all the actual costs or have Microsoft Project
    * calculate them for you.
    *
    * @return currency amount as float
    */
   public Number getActualCost ()
   {
      return ((Number)get(ACTUAL_COST));
   }

   /**
    * The Actual Duration field shows the span of actual working time for a
    * task so far, based on the scheduled duration and current remaining work
    * or completion percentage.
    *
    * @return duration string
    */
   public Duration getActualDuration ()
   {
      return ((Duration)get(ACTUAL_DURATION));
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
   public Date getActualFinish ()
   {
      return ((Date)get(ACTUAL_FINISH));
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
   public Date getActualStart ()
   {
      return ((Date)get(ACTUAL_START));
   }

   /**
    * The Actual Work field shows the amount of work that has already been done
    * by the resources assigned to a task.
    *
    * @return duration string
    */
   public Duration getActualWork ()
   {
      return ((Duration)get(ACTUAL_WORK));
   }

   /**
    * The Baseline Cost field shows the total planned cost for a task.
    * Baseline cost is also referred to as budget at completion (BAC).
    * @return currency amount as float
    */
   public Number getBaselineCost ()
   {
      return ((Number)get(BASELINE_COST));
   }

   /**
    * The Baseline Duration field shows the original span of time planned
    * to complete a task.
    *
    * @return  - duration string
    */
   public Duration getBaselineDuration ()
   {
      return ((Duration)get(BASELINE_DURATION));
   }

   /**
    * The Baseline Finish field shows the planned completion date for a task
    * at the time you saved a baseline. Information in this field becomes
    * available when you set a baseline for a task.
    *
    * @return Date
    */
   public Date getBaselineFinish ()
   {
      return ((Date)get(BASELINE_FINISH));
   }

   /**
    * The Baseline Start field shows the planned beginning date for a task at
    * the time you saved a baseline. Information in this field becomes available
    * when you set a baseline.
    *
    * @return Date
    */
   public Date getBaselineStart ()
   {
      return ((Date)get(BASELINE_START));
   }

   /**
    * The Baseline Work field shows the originally planned amount of work to be
    * performed by all resources assigned to a task. This field shows the planned
    * person-hours scheduled for a task. Information in the Baseline Work field
    * becomes available when you set a baseline for the project.
    *
    * @return Duration
    */
   public Duration getBaselineWork ()
   {
      return ((Duration)get(BASELINE_WORK));
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
   public Number getBCWP ()
   {
      return ((Number)get(BCWP));
   }

   /**
    * The BCWS (budgeted cost of work scheduled) field contains the cumulative
    * timephased baseline costs up to the status date or today's date.
    *
    * @return currency amount as float
    */
   public Number getBCWS ()
   {
      return ((Number)get(BCWS));
   }

   /**
    * The Confirmed field indicates whether all resources assigned to a task
    * have accepted or rejected the task assignment in response to a TeamAssign
    * message regarding their assignments.
    *
    * @return boolean
    */
   public boolean getConfirmed ()
   {
      return (BooleanUtility.getBoolean((Boolean)get(CONFIRMED)));
   }

   /**
    * The Constraint Date field shows the specific date associated with certain
    * constraint types, such as Must Start On, Must Finish On,
    * Start No Earlier Than,
    * Start No Later Than, Finish No Earlier Than, and Finish No Later Than.
    *
    * @return Date
    */
   public Date getConstraintDate ()
   {
      return ((Date)get(CONSTRAINT_DATE));
   }

   /**
    * The Constraint Type field provides choices for the type of constraint you
    * can apply for scheduling a task.
    *
    * @return constraint type
    */
   public ConstraintType getConstraintType ()
   {
      return ((ConstraintType)get(CONSTRAINT_TYPE));
   }

   /**
    * The Contact field contains the name of an individual
    * responsible for a task.
    *
    * @return String
    */
   public String getContact ()
   {
      return ((String)get(CONTACT));
   }

   /**
    * The Cost field shows the total scheduled, or projected, cost for a task,
    * based on costs already incurred for work performed by all resources assigned
    * to the task, in addition to the costs planned for the remaining work for the
    * assignment. This can also be referred to as estimate at completion (EAC).
    *
    * @return cost amount
    */
   public Number getCost ()
   {
      return ((Number)get(COST));
   }

   /**
    * The Cost1-10 fields show any custom task cost information you
    * want to enter in your project.
    *
    * @return cost amount
    */
   public Number getCost1 ()
   {
      return ((Number)get(COST1));
   }

   /**
    * The Cost1-10 fields show any custom task cost information
    * you want to enter in your project.
    *
    * @return amount
    */
   public Number getCost2 ()
   {
      return ((Number)get(COST2));
   }

   /**
    * The Cost1-10 fields show any custom task cost information you want to enter
    * in your project.
    *
    * @return amount
    */
   public Number getCost3 ()
   {
      return ((Number)get(COST3));
   }

   /**
    * The Cost Variance field shows the difference between the baseline cost
    * and total cost for a task. The total cost is the current estimate of costs
    * based on actual costs and remaining costs. This is also referred to as
    * variance at completion (VAC).
    *
    * @return amount
    */
   public Number getCostVariance ()
   {
      return ((Number)get(COST_VARIANCE));
   }

   /**
    * The Created field contains the date and time when a task was added
    * to the project.
    *
    * @return Date
    */
   public Date getCreateDate ()
   {
      return ((Date)get(CREATE_DATE));
   }

   /**
    * The Critical field indicates whether a task has any room in the schedule
    * to slip, or if a task is on the critical path. The Critical field contains
    * Yes if the task is critical and No if the task is not critical.
    *
    * @return boolean
    */
   public boolean getCritical ()
   {
      return (BooleanUtility.getBoolean((Boolean)get(CRITICAL)));
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
   public Number getCV ()
   {
      return ((Number)get(CV));
   }

   /**
    * Delay , in MPX files as eg '0ed'. Use duration
    *
    * @return Duration
    */
   public Duration getDelay ()
   {
      return ((Duration)get(DELAY));
   }

   /**
    * The Duration field is the total span of active working time for a task.
    * This is generally the amount of time from the start to the finish of a task.
    * The default for new tasks is 1 day (1d).
    *
    * @return Duration
    */
   public Duration getDuration ()
   {
      return ((Duration)get(DURATION));
   }

   /**
    * The Duration1 through Duration10 fields are custom fields that show any
    * specialized task duration information you want to enter and store separately
    * in your project.
    *
    * @return Duration
    */
   public Duration getDuration1 ()
   {
      return (Duration)get(DURATION1);
   }

   /**
    * The Duration1 through Duration10 fields are custom fields that show any
    * specialized task duration information you want to enter and store separately
    * in your project.
    *
    * @return Duration
    */
   public Duration getDuration2 ()
   {
      return ((Duration)get(DURATION2));
   }

   /**
    * The Duration1 through Duration10 fields are custom fields that show any
    * specialized task duration information you want to enter and store separately
    * in your project.
    *
    * @return Duration
    */
   public Duration getDuration3 ()
   {
      return ((Duration)get(DURATION3));
   }

   /**
    * The Duration Variance field contains the difference between the
    * baseline duration of a task and the total duration (current estimate)
    * of a task.
    *
    * @return Duration
    */
   public Duration getDurationVariance ()
   {
      return ((Duration)get(DURATION_VARIANCE));
   }

   /**
    * The Early Finish field contains the earliest date that a task could
    * possibly finish, based on early finish dates of predecessor and
    * successor tasks, other constraints, and any leveling delay.
    *
    * @return Date
    */
   public Date getEarlyFinish ()
   {
      return ((Date)get(EARLY_FINISH));
   }

   /**
    * The Early Start field contains the earliest date that a task could
    * possibly begin, based on the early start dates of predecessor and
    * successor tasks, and other constraints.
    *
    * @return Date
    */
   public Date getEarlyStart ()
   {
      return ((Date)get(EARLY_START));
   }

   /**
    * The Finish field shows the date and time that a task is scheduled to
    * be completed. You can enter the finish date you want, to indicate the
    * date when the task should be completed. Or, you can have Microsoft
    * Project calculate the finish date.
    *
    * @return Date
    */
   public Date getFinish ()
   {
      return ((Date)get(FINISH));
   }

   /**
    * The Finish1 through Finish10 fields are custom fields that show any
    * specific task finish date information you want to enter and store
    * separately in your project.
    *
    * @return Date
    */
   public Date getFinish1 ()
   {
      return ((Date)get(FINISH1));
   }

   /**
    * The Finish1 through Finish10 fields are custom fields that show any
    * specific task finish date information you want to enter and store
    * separately in your project.
    *
    * @return Date
    */
   public Date getFinish2 ()
   {
      return ((Date)get(FINISH2));
   }

   /**
    * The Finish1 through Finish10 fields are custom fields that show any
    * specific task finish date information you want to enter and store
    * separately in your project.
    *
    * @return Date
    */
   public Date getFinish3 ()
   {
      return ((Date)get(FINISH3));
   }

   /**
    * The Finish1 through Finish10 fields are custom fields that show any
    * specific task finish date information you want to enter and store
    * separately in your project.
    *
    * @return Date
    */
   public Date getFinish4 ()
   {
      return ((Date)get(FINISH4));
   }

   /**
    * The Finish1 through Finish10 fields are custom fields that show any
    * specific task finish date information you want to enter and store
    * separately in your project.
    *
    * @return Date
    */
   public Date getFinish5 ()
   {
      return ((Date)get(FINISH5));
   }

   /**
    * This field will be ignored on input into MS Project.
    *
    * @return String
    */
   public Duration getFinishVariance ()
   {
      return ((Duration)get(FINISH_VARIANCE));
   }

   /**
    * Despite the name, this flag represents the task type. If the value is
    * false, the task type shown in MS Project will be fixed units. If
    * the value is true, the task type will be fixed duration.
    *
    * @return boolean
    */
   public boolean getFixed ()
   {
      return (BooleanUtility.getBoolean((Boolean)get(FIXED)));
   }

   /**
    * The Fixed Cost field shows any task expense that is not associated
    * with a resource cost.
    *
    * @return currenct amount as float
    */
   public Number getFixedCost ()
   {
      return ((Number)get(FIXED_COST));
   }

   /**
    * The Flag1-20 fields indicate whether a task is marked for further
    * action or identification of some kind. To mark a task, click Yes
    * in a Flag field. If you don't want a task marked, click No.
    *
    * @return boolean
    */
   public boolean getFlag1 ()
   {
      return (BooleanUtility.getBoolean((Boolean)get(FLAG1)));
   }

   /**
    * The Flag1-20 fields indicate whether a task is marked for further
    * action or identification of some kind. To mark a task, click Yes
    * in a Flag field. If you don't want a task marked, click No.
    *
    * @return boolean
    */
   public boolean getFlag2 ()
   {
      return (BooleanUtility.getBoolean((Boolean)get(FLAG2)));
   }

   /**
    * The Flag1-20 fields indicate whether a task is marked for further
    * action or identification of some kind. To mark a task, click Yes
    * in a Flag field. If you don't want a task marked, click No.
    *
    * @return boolean
    */
   public boolean getFlag3 ()
   {
      return (BooleanUtility.getBoolean((Boolean)get(FLAG3)));
   }

   /**
    * The Flag1-20 fields indicate whether a task is marked for further
    * action or identification of some kind. To mark a task, click Yes
    * in a Flag field. If you don't want a task marked, click No.
    *
    * @return boolean
    */
   public boolean getFlag4 ()
   {
      return (BooleanUtility.getBoolean((Boolean)get(FLAG4)));
   }

   /**
    * The Flag1-20 fields indicate whether a task is marked for further
    * action or identification of some kind. To mark a task, click Yes
    * in a Flag field. If you don't want a task marked, click No.
    *
    * @return boolean
    */
   public boolean getFlag5 ()
   {
      return (BooleanUtility.getBoolean((Boolean)get(FLAG5)));
   }

   /**
    * The Flag1-20 fields indicate whether a task is marked for further
    * action or identification of some kind. To mark a task, click Yes
    * in a Flag field. If you don't want a task marked, click No.
    *
    * @return boolean
    */
   public boolean getFlag6 ()
   {
      return (BooleanUtility.getBoolean((Boolean)get(FLAG6)));
   }

   /**
    * The Flag1-20 fields indicate whether a task is marked for further
    * action or identification of some kind. To mark a task, click Yes
    * in a Flag field. If you don't want a task marked, click No.
    *
    * @return boolean
    */
   public boolean getFlag7 ()
   {
      return (BooleanUtility.getBoolean((Boolean)get(FLAG7)));
   }


   /**
    * The Flag1-20 fields indicate whether a task is marked for further
    * action or identification of some kind. To mark a task, click Yes
    * in a Flag field. If you don't want a task marked, click No.
    *
    * @return boolean
    */
   public boolean getFlag8 ()
   {
      return (BooleanUtility.getBoolean((Boolean)get(FLAG8)));
   }

   /**
    * The Flag1-20 fields indicate whether a task is marked for further
    * action or identification of some kind. To mark a task, click Yes
    * in a Flag field. If you don't want a task marked, click No.
    *
    * @return boolean
    */
   public boolean getFlag9 ()
   {
      return (BooleanUtility.getBoolean((Boolean)get(FLAG9)));
   }

   /**
    * The Flag1-20 fields indicate whether a task is marked for further
    * action or identification of some kind. To mark a task, click Yes
    * in a Flag field. If you don't want a task marked, click No.
    *
    * @return boolean
    */
   public boolean getFlag10 ()
   {
      return (BooleanUtility.getBoolean((Boolean)get(FLAG10)));
   }

   /**
    * The Free Slack field contains the amount of time that a task can be
    * delayed without delaying any successor tasks. If the task has no
    * successors, free slack is the amount of time that a task can be
    * delayed without delaying the entire project's finish date.
    *
    * @return Duration
    */
   public Duration getFreeSlack ()
   {
      return ((Duration)get(FREE_SLACK));
   }

   /**
    * The Hide Bar field indicates whether the Gantt bars and Calendar bars
    * for a task are hidden. Click Yes in the Hide Bar field to hide the
    * bar for the task. Click No in the Hide Bar field to show the bar
    * for the task.
    *
    * @return boolean
    */
   public boolean getHideBar ()
   {
      return (BooleanUtility.getBoolean((Boolean)get(HIDE_BAR)));
   }

   /**
    * The ID field contains the identifier number that Microsoft Project
    * automatically assigns to each task as you add it to the project.
    * The ID indicates the position of a task with respect to the other tasks.
    *
    * @return the task ID
    */
   public Integer getID ()
   {
      return ((Integer)get(ID));
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
   public Date getLateFinish ()
   {
      return ((Date)get(LATE_FINISH));
   }

   /**
    * The Late Start field contains the latest date that a task can start
    * without delaying the finish of the project. This date is based on
    * the task's start date, as well as the late start and late finish
    * dates of predecessor and successor tasks, and other constraints.
    *
    * @return Date
    */
   public Date getLateStart ()
   {
      return ((Date)get(LATE_START));
   }

   /**
    * The Linked Fields field indicates whether there are OLE links to the task,
    * either from elsewhere in the active project, another Microsoft Project file,
    * or from another program.
    *
    * @return boolean
    */
   public boolean getLinkedFields ()
   {
      return (BooleanUtility.getBoolean((Boolean)get(LINKED_FIELDS)));
   }

   /**
    * The Marked field indicates whether a task is marked for further action or
    * identification of some kind. To mark a task, click Yes in the Marked field.
    * If you don't want a task marked, click No.
    *
    * @return true for marked
    */
   public boolean getMarked ()
   {
      return (BooleanUtility.getBoolean((Boolean)get(MARKED)));
   }

   /**
    * The Milestone field indicates whether a task is a milestone.
    *
    * @return boolean
    */
   public boolean getMilestone ()
   {
      return (BooleanUtility.getBoolean((Boolean)get(MILESTONE)));
   }

   /**
    * Retrieves the task name.
    *
    * @return task name
    */
   public String getName ()
   {
      return ((String)get(NAME));
   }

   /**
    * The Notes field contains notes that you can enter about a task.
    * You can use task notes to help maintain a history for a task.
    *
    * @return notes
    */
   public String getNotes ()
   {
      return (m_notes==null?"":m_notes);
   }

   /**
    * Retrieves a numeric value.
    *
    * @return Numeric value
    */
   public Number getNumber1 ()
   {
      return ((Number)get(NUMBER1));
   }

   /**
    * Retrieves a numeric value.
    *
    * @return Numeric value
    */
   public Number getNumber2 ()
   {
      return ((Number)get(NUMBER2));
   }

   /**
    * Retrieves a numeric value.
    *
    * @return Numeric value
    */
   public Number getNumber3 ()
   {
      return ((Number)get(NUMBER3));
   }

   /**
    * Retrieves a numeric value.
    *
    * @return Numeric value
    */
   public Number getNumber4 ()
   {
      return ((Number)get(NUMBER4));
   }

   /**
    * Retrieves a numeric value.
    *
    * @return Numeric value
    */
   public Number getNumber5 ()
   {
      return ((Number)get(NUMBER5));
   }

   /**
    * The Objects field contains the number of objects attached to a task.
    * Microsoft Project counts the number of objects linked or embedded to a task.
    * However, objects in the Notes box in the Resource Form are not included
    * in this count.
    *
    * @return int
    */
   public Integer getObjects ()
   {
      return ((Integer)get(OBJECTS));
   }

   /**
    * The Outline Level field contains the number that indicates the level
    * of the task in the project outline hierarchy.
    *
    * @return int
    */
   public Integer getOutlineLevel ()
   {
      return ((Integer)get(OUTLINE_LEVEL));
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
   public String getOutlineNumber ()
   {
      return ((String)get(OUTLINE_NUMBER));
   }

   /**
    * The Predecessor field in an MPX file lists the task ID numbers for the
    * predecessor for a given task. A predecessor task must start or finish
    * the current task can be started. Each predecessor is linked to the task
    * by a specific type of task dependency and a lead time or lag time.
    *
    * This method returns a RelationList object which contains a list of
    * relationships between tasks. An iterator can be used to traverse
    * this list to retrieve each relationship. Note that this method may
    * return null if no predecessor relationships have been defined.
    *
    * @return RelationList instance
    */
   public List getPredecessors ()
   {
      return ((List)get(PREDECESSORS));
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
   public Priority getPriority ()
   {
      return ((Priority)get(PRIORITY));
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
   public String getProject ()
   {
      return ((String)get(PROJECT));
   }

   /**
    * The Remaining Cost field shows the remaining scheduled expense of a
    * task that will be incurred in completing the remaining scheduled work
    * by all resources assigned to the task.
    *
    * @return remaining cost
    */
   public Number getRemainingCost ()
   {
      return ((Number)get(REMAINING_COST));
   }

   /**
    * The Remaining Duration field shows the amount of time required
    * to complete the unfinished portion of a task.
    *
    * @return Duration
    */
   public Duration getRemainingDuration ()
   {
      return ((Duration)get(REMAINING_DURATION));
   }

   /**
    * The Remaining Work field shows the amount of time, or person-hours,
    * still required by all assigned resources to complete a task.
    *
    * @return the amount of time still required to complete a task
    */
   public Duration getRemainingWork ()
   {
      return ((Duration)get(REMAINING_WORK));
   }

   /**
    * The Resource Group field contains the list of resource groups to which
    * the resources assigned to a task belong.
    *
    * @return single string list of groups
    */
   public String getResourceGroup ()
   {
      return ((String)get(RESOURCE_GROUP));
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
   public String getResourceInitials ()
   {
      return ((String)get(RESOURCE_INITIALS));
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
   public String getResourceNames ()
   {
      return ((String)get(RESOURCE_NAMES));
   }

   /**
    * The Resume field shows the date that the remaining portion of a task
    * is scheduled to resume after you enter a new value for the % Complete
    * field. The Resume field is also recalculated when the remaining portion
    * of a task is moved to a new date.
    *
    * @return Date
    */
   public Date getResume ()
   {
      return ((Date)get(RESUME));
   }

   /**
    * Gets the date to resume this task no earlier than.
    *
    * @return date
    */
   public Date getResumeNoEarlierThan ()
   {
      return ((Date)get(RESUME_NO_EARLIER_THAN));
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
   public boolean getRollup ()
   {
      return (BooleanUtility.getBoolean((Boolean)get(ROLLUP)));
   }

   /**
    * The Start field shows the date and time that a task is scheduled to begin.
    * You can enter the start date you want, to indicate the date when the task
    * should begin. Or, you can have Microsoft Project calculate the start date.
    *
    * @return Date
    */
   public Date getStart ()
   {
      return ((Date)get(START));
   }

   /**
    * The Start1 through Start10 fields are custom fields that show any
    * specific task start date information you want to enter and store
    * separately in your project.
    *
    * @return Date
    */
   public Date getStart1 ()
   {
      return ((Date)get(START1));
   }

   /**
    * The Start1 through Start10 fields are custom fields that show any
    * specific task start date information you want to enter and store
    * separately in your project.
    *
    * @return Date
    */
   public Date getStart2 ()
   {
      return ((Date)get(START2));
   }

   /**
    * The Start1 through Start10 fields are custom fields that show any
    * specific task start date information you want to enter and store
    * separately in your project.
    *
    * @return Date
    */
   public Date getStart3 ()
   {
      return ((Date)get(START3));
   }

   /**
    * The Start1 through Start10 fields are custom fields that show any
    * specific task start date information you want to enter and store
    * separately in your project.
    *
    * @return Date
    */
   public Date getStart4 ()
   {
      return ((Date)get(START4));
   }

   /**
    * The Start1 through Start10 fields are custom fields that show any
    * specific task start date information you want to enter and store
    * separately in your project.
    *
    * @return Date
    */
   public Date getStart5 ()
   {
      return ((Date)get(START5));
   }

   /**
    * The Start Variance field contains the amount of time that represents
    * the difference between a tasks baseline start date and its currently
    * scheduled start date.
    *
    * @return value of duration. Duration
    */
   public Duration getStartVariance ()
   {
      return ((Duration)get(START_VARIANCE));
   }

   /**
    * The Stop field shows the date that represents the end of the actual
    * portion of a task. Typically, Microsoft Project calculates the stop date.
    * However, you can edit this date as well.
    *
    * @return Date
    */
   public Date getStop ()
   {
      return ((Date)get(STOP));
   }

   /**
    * The Subproject File field contains the name of a project inserted
    * into the active project file. The Subproject File field contains the
    * inserted project's path and file name.
    *
    * @return String path
    */
   public String getSubprojectName ()
   {
      return ((String)get(SUBPROJECT_NAME));
   }

   /**
    * The Successors field in an MPX file lists the task ID numbers for the
    * successor for a given task. A task must start or finish before successor
    * tasks can start or finish. Each successor is linked to the task by a
    * specific type of task dependency and a lead time or lag time.
    *
    * This method returns a RelationList object which contains a list of
    * relationships between tasks. An iterator can be used to traverse
    * this list to retrieve each relationship.
    *
    * @return RelationList instance
    */
   public List getSuccessors ()
   {
      return ((List)get(SUCCESSORS));
   }

   /**
    * The Summary field indicates whether a task is a summary task.
    *
    * @return boolean, true-is summary task
    */
   public boolean getSummary ()
   {
      return (BooleanUtility.getBoolean((Boolean)get(SUMMARY)));
   }

   /**
    * The SV (earned value schedule variance) field shows the difference in
    * cost terms between the current progress and the baseline plan of the
    * task up to the status date or today's date. You can use SV to
    * check costs to determine whether tasks are on schedule.
    *
    * @return -earned value schedule variance
    */
   public Number getSV ()
   {
      return ((Number)get(SV));
   }

   /**
    * The Text1-30 fields show any custom text information you want
    * to enter in your project about tasks.
    *
    * @return String
    */
   public String getText1 ()
   {
      return ((String)get(TEXT1));
   }

   /**
    * The Text1-30 fields show any custom text information you want
    * to enter in your project about tasks.
    *
    * @return String
    */
   public String getText2 ()
   {
      return ((String)get(TEXT2));
   }

   /**
    * The Text1-30 fields show any custom text information you want
    * to enter in your project about tasks.
    *
    * @return String
    */
   public String getText3 ()
   {
      return ((String)get(TEXT3));
   }

   /**
    * The Text1-30 fields show any custom text information you want
    * to enter in your project about tasks.
    *
    * @return String
    */
   public String getText4 ()
   {
      return ((String)get(TEXT4));
   }

   /**
    * The Text1-30 fields show any custom text information you want
    * to enter in your project about tasks.
    *
    * @return String
    */
   public String getText5 ()
   {
      return ((String)get(TEXT5));
   }

   /**
    * The Text1-30 fields show any custom text information you want
    * to enter in your project about tasks.
    *
    * @return String
    */
   public String getText6 ()
   {
      return ((String)get(TEXT6));
   }

   /**
    * The Text1-30 fields show any custom text information you want
    * to enter in your project about tasks.
    *
    * @return String
    */
   public String getText7 ()
   {
      return ((String)get(TEXT7));
   }

   /**
    * The Text1-30 fields show any custom text information you want
    * to enter in your project about tasks.
    *
    * @return String
    */
   public String getText8 ()
   {
      return ((String)get(TEXT8));
   }

   /**
    * The Text1-30 fields show any custom text information you want
    * to enter in your project about tasks.
    *
    * @return String
    */
   public String getText9 ()
   {
      return ((String)get(TEXT9));
   }

   /**
    * The Text1-30 fields show any custom text information you want
    * to enter in your project about tasks.
    *
    * @return String
    */
   public String getText10 ()
   {
      return ((String)get(TEXT10));
   }

   /**
    * The Total Slack field contains the amount of time a task can be
    * delayed without delaying the project's finish date.
    *
    * @return string representing duration
    */
   public Duration getTotalSlack ()
   {
      return ((Duration)get(TOTAL_SLACK));
   }

   /**
    * The Unique ID field contains the number that Microsoft Project
    * automatically designates whenever a new task is created. This number
    * indicates the sequence in which the task was
    * created, regardless of placement in the schedule.
    *
    * @return String
    */
   public Integer getUniqueID ()
   {
      return ((Integer)get(UNIQUE_ID));
   }

   /**
    * The Unique ID Predecessors field lists the unique ID numbers for the
    * predecessor tasks on which a task depends before it can be started or
    * finished. Each predecessor is linked to the task by a specific type of
    * task dependency and a lead time or lag time.
    *
    * @return list of predecessor UniqueIDs
    */
   public List getUniqueIDPredecessors ()
   {
      return ((List)get(UNIQUE_ID_PREDECESSORS));
   }

   /**
    * The Unique ID Predecessors field lists the unique ID numbers for the
    * predecessor tasks on which a task depends before it can be started or
    * finished. Each predecessor is linked to the task by a specific type of
    * task dependency and a lead time or lag time.
    *
    * @return list of predecessor UniqueIDs
    */
   public List getUniqueIDSuccessors ()
   {
      return ((List)get(UNIQUE_ID_SUCCESSORS));
   }

   /**
    * The Update Needed field indicates whether a TeamUpdate message
    * should be sent to the assigned resources because of changes to the
    * start date, finish date, or resource reassignments of the task.
    *
    * @return true if needed.
    */
   public boolean getUpdateNeeded ()
   {
      return (BooleanUtility.getBoolean((Boolean)get(UPDATE_NEEDED)));
   }

   /**
    * The work breakdown structure code. The WBS field contains an
    * alphanumeric code you can use to represent the task's position within
    * the hierarchical structure of the project. This field is similar to
    * the outline number, except that you can edit it.
    *
    * @return string
    */
   public String getWBS ()
   {
      return ((String)get(WBS));
   }

   /**
    * The Work field shows the total amount of work scheduled to be performed
    * on a task by all assigned resources. This field shows the total work,
    * or person-hours, for a task.
    *
    * @return Duration representing duration .
    */
   public Duration getWork ()
   {
      return ((Duration)get(WORK));
   }

   /**
    * The Work Variance field contains the difference between a task's
    * baseline work and the currently scheduled work.
    *
    * @return Duration representing duration.
    */
   public Duration getWorkVariance ()
   {
      return ((Duration)get(WORK_VARIANCE));
   }

   /**
    * Retrieve count of the number of child tasks.
    *
    * @return Number of child tasks.
    */
   int getChildTaskCount ()
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
   public Task getParentTask ()
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
   public List getChildTasks ()
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
   public int compareTo (Object o)
   {
      int id1 = NumberUtility.getInt(getID());
      int id2 = NumberUtility.getInt(((Task)o).getID());
      return ((id1 < id2) ? (-1) : ((id1 == id2) ? 0 : 1));
   }

   /**
    * This method retrieves a flag indicating whether the duration of the
    * task has only been estimated.
    *
    * @return boolean
    */
   public boolean getEstimated ()
   {
      return (m_estimated);
   }

   /**
    * This method retrieves a flag indicating whether the duration of the
    * task has only been estimated.

    * @param estimated Boolean flag
    */
   public void setEstimated (boolean estimated)
   {
      m_estimated = estimated;
   }

   /**
    * This method retrieves the deadline for this task.
    *
    * @return Task deadline
    */
   public Date getDeadline ()
   {
      return (m_deadline);
   }

   /**
    * This method sets the deadline for this task.
    *
    * @param deadline deadline date
    */
   public void setDeadline (Date deadline)
   {
      m_deadline = deadline;
   }

   /**
    * This method retrieves the task type.
    *
    * @return int representing the task type
    */
   public TaskType getType ()
   {
      return (m_type);
   }

   /**
    * This method sets the task type.
    *
    * @param type task type
    */
   public void setType (TaskType type)
   {
      m_type = type;
   }

   /**
    * Retrieves the flag indicating if this is a null task.
    *
    * @return boolean flag
    */
   public boolean getNull ()
   {
      return (m_null);
   }

   /**
    * Sets the flag indicating if this is a null task.
    *
    * @param isNull boolean flag
    */
   public void setNull (boolean isNull)
   {
      m_null = isNull;
   }

   /**
    * Retrieve the WBS level.
    *
    * @return WBS level
    */
   public String getWBSLevel ()
   {
      return (m_wbsLevel);
   }

   /**
    * Set the WBS level.
    *
    * @param wbsLevel WBS level
    */
   public void setWBSLevel (String wbsLevel)
   {
      m_wbsLevel = wbsLevel;
   }

   /**
    * Retrieve the duration format.
    *
    * @return duration format
    */
   public TimeUnit getDurationFormat ()
   {
      return (m_durationFormat);
   }

   /**
    * Set the duration format.
    *
    * @param durationFormat duration format
    */
   public void setDurationFormat (TimeUnit durationFormat)
   {
      m_durationFormat = durationFormat;
   }

   /**
    * Retrieve the resume valid flag.
    *
    * @return resume valie flag
    */
   public boolean getResumeValid ()
   {
      return (m_resumeValid);
   }

   /**
    * Set the resume valid flag.
    *
    * @param resumeValid resume valid flag
    */
   public void setResumeValid (boolean resumeValid)
   {
      m_resumeValid = resumeValid;
   }

   /**
    * Retrieve the recurring flag.
    *
    * @return recurring flag
    */
   public boolean getRecurring ()
   {
      return (m_recurring);
   }

   /**
    * Set the recurring flag.
    *
    * @param recurring recurring flag
    */
   public void setRecurring (boolean recurring)
   {
      m_recurring = recurring;
   }

   /**
    * Retrieve the over allocated flag.
    *
    * @return over allocated flag
    */
   public boolean getOverAllocated ()
   {
      return (m_overAllocated);
   }

   /**
    * Set the over allocated flag.
    *
    * @param overAllocated over allocated flag
    */
   public void setOverAllocated (boolean overAllocated)
   {
      m_overAllocated = overAllocated;
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
   public Integer getSubprojectTaskUniqueID ()
   {
      return (m_subprojectTaskUniqueID);
   }

   /**
    * Sets the sub project unique task ID.
    *
    * @param subprojectUniqueTaskID subproject unique task ID
    */
   public void setSubprojectTaskUniqueID (Integer subprojectUniqueTaskID)
   {
      m_subprojectTaskUniqueID = subprojectUniqueTaskID;
   }

   /**
    * Sets the offset added to unique task IDs from sub projects
    * to generate the task ID shown in the master project.
    *
    * @param offset unique ID offset
    */
   public void setSubprojectTasksUniqueIDOffset (Integer offset)
   {
      m_subprojectTasksUniqueIDOffset = offset;
   }

   /**
    * Retrieves the offset added to unique task IDs from sub projects
    * to generate the task ID shown in the master project.
    *
    * @return unique ID offset
    */
   public Integer getSubprojectTasksUniqueIDOffset ()
   {
      return (m_subprojectTasksUniqueIDOffset);
   }

   /**
    * Retrieve the subproject read only flag.
    *
    * @return subproject read only flag
    */
   public boolean getSubprojectReadOnly ()
   {
      return (m_subprojectReadOnly);
   }

   /**
    * Set the subproject read only flag.
    *
    * @param subprojectReadOnly subproject read only flag
    */
   public void setSubprojectReadOnly (boolean subprojectReadOnly)
   {
      m_subprojectReadOnly = subprojectReadOnly;
   }

   /**
    * Retrieves the external task flag.
    *
    * @return external task flag
    */
   public boolean getExternalTask ()
   {
      return (m_externalTask);
   }

   /**
    * Sets the external task flag.
    *
    * @param externalTask external task flag
    */
   public void setExternalTask (boolean externalTask)
   {
      m_externalTask = externalTask;
   }

   /**
    * Retrieves the external task project file name.
    *
    * @return external task project file name
    */
   public String getExternalTaskProject ()
   {
      return (m_externalTaskProject);
   }

   /**
    * Sets the external task project file name.
    *
    * @param externalTaskProject external task project file name
    */
   public void setExternalTaskProject (String externalTaskProject)
   {
      m_externalTaskProject = externalTaskProject;
   }

   /**
    * Retrieve the ACWP value.
    *
    * @return ACWP value
    */
   public Number getACWP ()
   {
      return (m_acwp);
   }

   /**
    * Set the ACWP value.
    *
    * @param acwp ACWP value
    */
   public void setACWP (Number acwp)
   {
      m_acwp = acwp;
   }

   /**
    * Retrieve the leveling delay format.
    *
    * @return leveling delay  format
    */
   public TimeUnit getLevelingDelayFormat ()
   {
      return (m_levelingDelayFormat);
   }

   /**
    * Set the leveling delay format.
    *
    * @param levelingDelayFormat leveling delay format
    */
   public void setLevelingDelayFormat (TimeUnit levelingDelayFormat)
   {
      m_levelingDelayFormat = levelingDelayFormat;
   }

   /**
    * Retrieves the ignore resource celandar flag.
    *
    * @return ignore resource celandar flag
    */
   public boolean getIgnoreResourceCalendar ()
   {
      return (m_ignoreResourceCalendar);
   }

   /**
    * Sets the ignore resource celandar flag.
    *
    * @param ignoreResourceCalendar ignore resource celandar flag
    */
   public void setIgnoreResourceCalendar (boolean ignoreResourceCalendar)
   {
      m_ignoreResourceCalendar = ignoreResourceCalendar;
   }

   /**
    * Retrieves the physical percent complete value.
    *
    * @return physical percent complete value
    */
   public Integer getPhysicalPercentComplete ()
   {
      return (m_physicalPercentComplete);
   }

   /**
    * Srts the physical percent complete value.
    *
    * @param physicalPercentComplete physical percent complete value
    */
   public void setPhysicalPercentComplete (Integer physicalPercentComplete)
   {
      m_physicalPercentComplete = physicalPercentComplete;
   }

   /**
    * Retrieves the earned value method.
    *
    * @return earned value method
    */
   public EarnedValueMethod getEarnedValueMethod ()
   {
      return (m_earnedValueMethod);
   }

   /**
    * Sets the earned value method.
    *
    * @param earnedValueMethod earned value method
    */
   public void setEarnedValueMethod (EarnedValueMethod earnedValueMethod)
   {
      m_earnedValueMethod = earnedValueMethod;
   }

   /**
    * Retrieves the actual work protected value.
    *
    * @return actual work protected value
    */
   public Duration getActualWorkProtected ()
   {
      return (m_actualWorkProtected);
   }

   /**
    * Sets the actual work protected value.
    *
    * @param actualWorkProtected actual work protected value
    */
   public void setActualWorkProtected (Duration actualWorkProtected)
   {
      m_actualWorkProtected = actualWorkProtected;
   }

   /**
    * Retrieves the actual overtime work protected value.
    *
    * @return actual overtime work protected value
    */
   public Duration getActualOvertimeWorkProtected ()
   {
      return (m_actualOvertimeWorkProtected);
   }

   /**
    * Sets the actual overtime work protected value.
    *
    * @param actualOvertimeWorkProtected actual overtime work protected value
    */
   public void setActualOvertimeWorkProtected (Duration actualOvertimeWorkProtected)
   {
      m_actualOvertimeWorkProtected = actualOvertimeWorkProtected;
   }

   /**
    * Retrieve the amount of regular work.
    *
    * @return amount of regular work
    */
   public Duration getRegularWork ()
   {
      return (m_regularWork);
   }

   /**
    * Set the amount of regular work.
    *
    * @param regularWork amount of regular work
    */
   public void setRegularWork (Duration regularWork)
   {
      m_regularWork = regularWork;
   }

   /**
    * Retrieves the flag value.
    *
    * @return flag value
    */
   public boolean getFlag11 ()
   {
      return (BooleanUtility.getBoolean((Boolean)get(FLAG11)));
   }

   /**
    * Retrieves the flag value.
    *
    * @return flag value
    */
   public boolean getFlag12 ()
   {
      return (BooleanUtility.getBoolean((Boolean)get(FLAG12)));
   }

   /**
    * Retrieves the flag value.
    *
    * @return flag value
    */
   public boolean getFlag13 ()
   {
      return (BooleanUtility.getBoolean((Boolean)get(FLAG13)));
   }

   /**
    * Retrieves the flag value.
    *
    * @return flag value
    */
   public boolean getFlag14 ()
   {
      return (BooleanUtility.getBoolean((Boolean)get(FLAG14)));
   }

   /**
    * Retrieves the flag value.
    *
    * @return flag value
    */
   public boolean getFlag15 ()
   {
      return (BooleanUtility.getBoolean((Boolean)get(FLAG15)));
   }

   /**
    * Retrieves the flag value.
    *
    * @return flag value
    */
   public boolean getFlag16 ()
   {
      return (BooleanUtility.getBoolean((Boolean)get(FLAG16)));
   }

   /**
    * Retrieves the flag value.
    *
    * @return flag value
    */
   public boolean getFlag17 ()
   {
      return (BooleanUtility.getBoolean((Boolean)get(FLAG17)));
   }

   /**
    * Retrieves the flag value.
    *
    * @return flag value
    */
   public boolean getFlag18 ()
   {
      return (BooleanUtility.getBoolean((Boolean)get(FLAG18)));
   }

   /**
    * Retrieves the flag value.
    *
    * @return flag value
    */
   public boolean getFlag19 ()
   {
      return (BooleanUtility.getBoolean((Boolean)get(FLAG19)));
   }

   /**
    * Retrieves the flag value.
    *
    * @return flag value
    */
   public boolean getFlag20 ()
   {
      return (BooleanUtility.getBoolean((Boolean)get(FLAG20)));
   }

   /**
    * Sets the flag value.
    *
    * @param b flag value
    */
   public void setFlag11 (boolean b)
   {
      set(FLAG11, b);
   }

   /**
    * Sets the flag value.
    *
    * @param b flag value
    */
   public void setFlag12 (boolean b)
   {
      set(FLAG12, b);
   }

   /**
    * Sets the flag value.
    *
    * @param b flag value
    */
   public void setFlag13 (boolean b)
   {
      set(FLAG13, b);
   }

   /**
    * Sets the flag value.
    *
    * @param b flag value
    */
   public void setFlag14 (boolean b)
   {
      set(FLAG14, b);
   }

   /**
    * Sets the flag value.
    *
    * @param b flag value
    */
   public void setFlag15 (boolean b)
   {
      set(FLAG15, b);
   }

   /**
    * Sets the flag value.
    *
    * @param b flag value
    */
   public void setFlag16 (boolean b)
   {
      set(FLAG16, b);
   }

   /**
    * Sets the flag value.
    *
    * @param b flag value
    */
   public void setFlag17 (boolean b)
   {
      set(FLAG17, b);
   }

   /**
    * Sets the flag value.
    *
    * @param b flag value
    */
   public void setFlag18 (boolean b)
   {
      set(FLAG18, b);
   }

   /**
    * Sets the flag value.
    *
    * @param b flag value
    */
   public void setFlag19 (boolean b)
   {
      set(FLAG19, b);
   }

   /**
    * Sets the flag value.
    *
    * @param b flag value
    */
   public void setFlag20 (boolean b)
   {
      set(FLAG20, b);
   }

   /**
    * Sets the effort driven flag.
    *
    * @param flag value
    */
   public void setEffortDriven (boolean flag)
   {
      m_effortDriven = flag;
   }

   /**
    * Retrieves the effort friven flag.
    *
    * @return Flag value
    */
   public boolean getEffortDriven ()
   {
      return (m_effortDriven);
   }

   /**
    * Retrieves a text value.
    *
    * @return Text value
    */
   public String getText11 ()
   {
      return ((String)get(TEXT11));
   }

   /**
    * Retrieves a text value.
    *
    * @return Text value
    */
   public String getText12 ()
   {
      return ((String)get(TEXT12));
   }

   /**
    * Retrieves a text value.
    *
    * @return Text value
    */
   public String getText13 ()
   {
      return ((String)get(TEXT13));
   }

   /**
    * Retrieves a text value.
    *
    * @return Text value
    */
   public String getText14 ()
   {
      return ((String)get(TEXT14));
   }

   /**
    * Retrieves a text value.
    *
    * @return Text value
    */
   public String getText15 ()
   {
      return ((String)get(TEXT15));
   }

   /**
    * Retrieves a text value.
    *
    * @return Text value
    */
   public String getText16 ()
   {
      return ((String)get(TEXT16));
   }

   /**
    * Retrieves a text value.
    *
    * @return Text value
    */
   public String getText17 ()
   {
      return ((String)get(TEXT17));
   }

   /**
    * Retrieves a text value.
    *
    * @return Text value
    */
   public String getText18 ()
   {
      return ((String)get(TEXT18));
   }

   /**
    * Retrieves a text value.
    *
    * @return Text value
    */
   public String getText19 ()
   {
      return ((String)get(TEXT19));
   }

   /**
    * Retrieves a text value.
    *
    * @return Text value
    */
   public String getText20 ()
   {
      return ((String)get(TEXT20));
   }

   /**
    * Retrieves a text value.
    *
    * @return Text value
    */
   public String getText21 ()
   {
      return ((String)get(TEXT21));
   }

   /**
    * Retrieves a text value.
    *
    * @return Text value
    */
   public String getText22 ()
   {
      return ((String)get(TEXT22));
   }

   /**
    * Retrieves a text value.
    *
    * @return Text value
    */
   public String getText23 ()
   {
      return ((String)get(TEXT23));
   }

   /**
    * Retrieves a text value.
    *
    * @return Text value
    */
   public String getText24 ()
   {
      return ((String)get(TEXT24));
   }

   /**
    * Retrieves a text value.
    *
    * @return Text value
    */
   public String getText25 ()
   {
      return ((String)get(TEXT25));
   }

   /**
    * Retrieves a text value.
    *
    * @return Text value
    */
   public String getText26 ()
   {
      return ((String)get(TEXT26));
   }

   /**
    * Retrieves a text value.
    *
    * @return Text value
    */
   public String getText27 ()
   {
      return ((String)get(TEXT27));
   }

   /**
    * Retrieves a text value.
    *
    * @return Text value
    */
   public String getText28 ()
   {
      return ((String)get(TEXT28));
   }

   /**
    * Retrieves a text value.
    *
    * @return Text value
    */
   public String getText29 ()
   {
      return ((String)get(TEXT29));
   }

   /**
    * Retrieves a text value.
    *
    * @return Text value
    */
   public String getText30 ()
   {
      return ((String)get(TEXT30));
   }

   /**
    * Sets a text value.
    *
    * @param string Text value
    */
   public void setText11 (String string)
   {
      set(TEXT11, string);
   }

   /**
    * Sets a text value.
    *
    * @param string Text value
    */
   public void setText12 (String string)
   {
      set(TEXT12, string);
   }

   /**
    * Sets a text value.
    *
    * @param string Text value
    */
   public void setText13 (String string)
   {
      set(TEXT13, string);
   }

   /**
    * Sets a text value.
    *
    * @param string Text value
    */
   public void setText14 (String string)
   {
      set(TEXT14, string);
   }

   /**
    * Sets a text value.
    *
    * @param string Text value
    */
   public void setText15 (String string)
   {
      set(TEXT15, string);
   }

   /**
    * Sets a text value.
    *
    * @param string Text value
    */
   public void setText16 (String string)
   {
      set(TEXT16, string);
   }

   /**
    * Sets a text value.
    *
    * @param string Text value
    */
   public void setText17 (String string)
   {
      set(TEXT17, string);
   }

   /**
    * Sets a text value.
    *
    * @param string Text value
    */
   public void setText18 (String string)
   {
      set(TEXT18, string);
   }

   /**
    * Sets a text value.
    *
    * @param string Text value
    */
   public void setText19 (String string)
   {
      set(TEXT19, string);
   }

   /**
    * Sets a text value.
    *
    * @param string Text value
    */
   public void setText20 (String string)
   {
      set(TEXT20, string);
   }

   /**
    * Sets a text value.
    *
    * @param string Text value
    */
   public void setText21 (String string)
   {
      set(TEXT21, string);
   }

   /**
    * Sets a text value.
    *
    * @param string Text value
    */
   public void setText22 (String string)
   {
      set(TEXT22, string);
   }

   /**
    * Sets a text value.
    *
    * @param string Text value
    */
   public void setText23 (String string)
   {
      set(TEXT23, string);
   }

   /**
    * Sets a text value.
    *
    * @param string Text value
    */
   public void setText24 (String string)
   {
      set(TEXT24, string);
   }

   /**
    * Sets a text value.
    *
    * @param string Text value
    */
   public void setText25 (String string)
   {
      set(TEXT25, string);
   }

   /**
    * Sets a text value.
    *
    * @param string Text value
    */
   public void setText26 (String string)
   {
      set(TEXT26, string);
   }

   /**
    * Sets a text value.
    *
    * @param string Text value
    */
   public void setText27 (String string)
   {
      set(TEXT27, string);
   }

   /**
    * Sets a text value.
    *
    * @param string Text value
    */
   public void setText28 (String string)
   {
      set(TEXT28, string);
   }

   /**
    * Sets a text value.
    *
    * @param string Text value
    */
   public void setText29 (String string)
   {
      set(TEXT29, string);
   }

   /**
    * Sets a text value.
    *
    * @param string Text value
    */
   public void setText30 (String string)
   {
      set(TEXT30, string);
   }

   /**
    * Sets a numeric value.
    *
    * @param val Numeric value
    */
   public void setNumber6 (Number val)
   {
      set(NUMBER6, val);
   }

   /**
    * Retrieves a numeric value.
    *
    * @return Numeric value
    */
   public Number getNumber6 ()
   {
      return ((Number)get(NUMBER6));
   }

   /**
    * Sets a numeric value.
    *
    * @param val Numeric value
    */
   public void setNumber7 (Number val)
   {
      set(NUMBER7, val);
   }

   /**
    * Retrieves a numeric value.
    *
    * @return Numeric value
    */
   public Number getNumber7 ()
   {
      return ((Number)get(NUMBER7));
   }

   /**
    * Sets a numeric value.
    *
    * @param val Numeric value
    */
   public void setNumber8 (Number val)
   {
      set(NUMBER8, val);
   }

   /**
    * Retrieves a numeric value.
    *
    * @return Numeric value
    */
   public Number getNumber8 ()
   {
      return ((Number)get(NUMBER8));
   }

   /**
    * Sets a numeric value.
    *
    * @param val Numeric value
    */
   public void setNumber9 (Number val)
   {
      set(NUMBER9, val);
   }

   /**
    * Retrieves a numeric value.
    *
    * @return Numeric value
    */
   public Number getNumber9 ()
   {
      return ((Number)get(NUMBER9));
   }

   /**
    * Sets a numeric value.
    *
    * @param val Numeric value
    */
   public void setNumber10 (Number val)
   {
      set(NUMBER10, val);
   }

   /**
    * Retrieves a numeric value.
    *
    * @return Numeric value
    */
   public Number getNumber10 ()
   {
      return ((Number)get(NUMBER10));
   }

   /**
    * Sets a numeric value.
    *
    * @param val Numeric value
    */
   public void setNumber11 (Number val)
   {
      set(NUMBER11, val);
   }

   /**
    * Retrieves a numeric value.
    *
    * @return Numeric value
    */
   public Number getNumber11 ()
   {
      return ((Number)get(NUMBER11));
   }

   /**
    * Sets a numeric value.
    *
    * @param val Numeric value
    */
   public void setNumber12 (Number val)
   {
      set(NUMBER12, val);
   }

   /**
    * Retrieves a numeric value.
    *
    * @return Numeric value
    */
   public Number getNumber12 ()
   {
      return ((Number)get(NUMBER12));
   }

   /**
    * Sets a numeric value.
    *
    * @param val Numeric value
    */
   public void setNumber13 (Number val)
   {
      set(NUMBER13, val);
   }

   /**
    * Retrieves a numeric value.
    *
    * @return Numeric value
    */
   public Number getNumber13 ()
   {
      return ((Number)get(NUMBER13));
   }

   /**
    * Sets a numeric value.
    *
    * @param val Numeric value
    */
   public void setNumber14 (Number val)
   {
      set(NUMBER14, val);
   }

   /**
    * Retrieves a numeric value.
    *
    * @return Numeric value
    */
   public Number getNumber14 ()
   {
      return ((Number)get(NUMBER14));
   }

   /**
    * Sets a numeric value.
    *
    * @param val Numeric value
    */
   public void setNumber15 (Number val)
   {
      set(NUMBER15, val);
   }

   /**
    * Retrieves a numeric value.
    *
    * @return Numeric value
    */
   public Number getNumber15 ()
   {
      return ((Number)get(NUMBER15));
   }

   /**
    * Sets a numeric value.
    *
    * @param val Numeric value
    */
   public void setNumber16 (Number val)
   {
      set(NUMBER16, val);
   }

   /**
    * Retrieves a numeric value.
    *
    * @return Numeric value
    */
   public Number getNumber16 ()
   {
      return ((Number)get(NUMBER16));
   }

   /**
    * Sets a numeric value.
    *
    * @param val Numeric value
    */
   public void setNumber17 (Number val)
   {
      set(NUMBER17, val);
   }

   /**
    * Retrieves a numeric value.
    *
    * @return Numeric value
    */
   public Number getNumber17 ()
   {
      return ((Number)get(NUMBER17));
   }

   /**
    * Sets a numeric value.
    *
    * @param val Numeric value
    */
   public void setNumber18 (Number val)
   {
      set(NUMBER18, val);
   }

   /**
    * Retrieves a numeric value.
    *
    * @return Numeric value
    */
   public Number getNumber18 ()
   {
      return ((Number)get(NUMBER18));
   }

   /**
    * Sets a numeric value.
    *
    * @param val Numeric value
    */
   public void setNumber19 (Number val)
   {
      set(NUMBER19, val);
   }

   /**
    * Retrieves a numeric value.
    *
    * @return Numeric value
    */
   public Number getNumber19 ()
   {
      return ((Number)get(NUMBER19));
   }

   /**
    * Sets a numeric value.
    *
    * @param val Numeric value
    */
   public void setNumber20 (Number val)
   {
      set(NUMBER20, val);
   }

   /**
    * Retrieves a numeric value.
    *
    * @return Numeric value
    */
   public Number getNumber20 ()
   {
      return ((Number)get(NUMBER20));
   }

   /**
    * Retrieves a duration.
    *
    * @return Duration
    */
   public Duration getDuration10 ()
   {
      return (Duration)get(DURATION10);
   }

   /**
    * Retrieves a duration.
    *
    * @return Duration
    */
   public Duration getDuration4 ()
   {
      return (Duration)get(DURATION4);
   }

   /**
    * Retrieves a duration.
    *
    * @return Duration
    */
   public Duration getDuration5 ()
   {
      return (Duration)get(DURATION5);
   }

   /**
    * Retrieves a duration.
    *
    * @return Duration
    */
   public Duration getDuration6 ()
   {
      return (Duration)get(DURATION6);
   }

   /**
    * Retrieves a duration.
    *
    * @return Duration
    */
   public Duration getDuration7 ()
   {
      return (Duration)get(DURATION7);
   }

   /**
    * Retrieves a duration.
    *
    * @return Duration
    */
   public Duration getDuration8 ()
   {
      return (Duration)get(DURATION8);
   }

   /**
    * Retrieves a duration.
    *
    * @return Duration
    */
   public Duration getDuration9 ()
   {
      return (Duration)get(DURATION9);
   }

   /**
    * User defined duration field.
    *
    * @param duration Duration value
    */
   public void setDuration10 (Duration duration)
   {
      set(DURATION10, duration);
   }

   /**
    * User defined duration field.
    *
    * @param duration Duration value
    */
   public void setDuration4 (Duration duration)
   {
      set(DURATION4, duration);
   }

   /**
    * User defined duration field.
    *
    * @param duration Duration value
    */
   public void setDuration5 (Duration duration)
   {
      set(DURATION5, duration);
   }

   /**
    * User defined duration field.
    *
    * @param duration Duration value
    */
   public void setDuration6 (Duration duration)
   {
      set(DURATION6, duration);
   }

   /**
    * User defined duration field.
    *
    * @param duration Duration value
    */
   public void setDuration7 (Duration duration)
   {
      set(DURATION7, duration);
   }

   /**
    * User defined duration field.
    *
    * @param duration Duration value
    */
   public void setDuration8 (Duration duration)
   {
      set(DURATION8, duration);
   }

   /**
    * User defined duration field.
    *
    * @param duration Duration value
    */
   public void setDuration9 (Duration duration)
   {
      set(DURATION9, duration);
   }

   /**
    * Retrieves a date value.
    *
    * @return Date value
    */
   public Date getDate1 ()
   {
      return ((Date)get(DATE1));
   }

   /**
    * Retrieves a date value.
    *
    * @return Date value
    */
   public Date getDate10 ()
   {
      return ((Date)get(DATE10));
   }

   /**
    * Retrieves a date value.
    *
    * @return Date value
    */
   public Date getDate2 ()
   {
      return ((Date)get(DATE2));
   }

   /**
    * Retrieves a date value.
    *
    * @return Date value
    */
   public Date getDate3 ()
   {
      return ((Date)get(DATE3));
   }

   /**
    * Retrieves a date value.
    *
    * @return Date value
    */
   public Date getDate4 ()
   {
      return ((Date)get(DATE4));
   }

   /**
    * Retrieves a date value.
    *
    * @return Date value
    */
   public Date getDate5 ()
   {
      return ((Date)get(DATE5));
   }

   /**
    * Retrieves a date value.
    *
    * @return Date value
    */
   public Date getDate6 ()
   {
      return ((Date)get(DATE6));
   }

   /**
    * Retrieves a date value.
    *
    * @return Date value
    */
   public Date getDate7 ()
   {
      return ((Date)get(DATE7));
   }

   /**
    * Retrieves a date value.
    *
    * @return Date value
    */
   public Date getDate8 ()
   {
      return ((Date)get(DATE8));
   }

   /**
    * Retrieves a date value.
    *
    * @return Date value
    */
   public Date getDate9 ()
   {
      return ((Date)get(DATE9));
   }

   /**
    * Sets a date value.
    *
    * @param date Date value
    */
   public void setDate1 (Date date)
   {
      set(DATE1, date);
   }

   /**
    * Sets a date value.
    *
    * @param date Date value
    */
   public void setDate10 (Date date)
   {
      set(DATE10, date);
   }

   /**
    * Sets a date value.
    *
    * @param date Date value
    */
   public void setDate2 (Date date)
   {
      set(DATE2, date);
   }

   /**
    * Sets a date value.
    *
    * @param date Date value
    */
   public void setDate3 (Date date)
   {
      set(DATE3, date);
   }

   /**
    * Sets a date value.
    *
    * @param date Date value
    */
   public void setDate4 (Date date)
   {
      set(DATE4, date);
   }

   /**
    * Sets a date value.
    *
    * @param date Date value
    */
   public void setDate5 (Date date)
   {
      set(DATE5, date);
   }

   /**
    * Sets a date value.
    *
    * @param date Date value
    */
   public void setDate6 (Date date)
   {
      set(DATE6, date);
   }

   /**
    * Sets a date value.
    *
    * @param date Date value
    */
   public void setDate7 (Date date)
   {
      set(DATE7, date);
   }

   /**
    * Sets a date value.
    *
    * @param date Date value
    */
   public void setDate8 (Date date)
   {
      set(DATE8, date);
   }

   /**
    * Sets a date value.
    *
    * @param date Date value
    */
   public void setDate9 (Date date)
   {
      set(DATE9, date);
   }

   /**
    * Retrieves a cost.
    *
    * @return Cost value
    */
   public Number getCost10 ()
   {
      return ((Number)get(COST10));
   }

   /**
    * Retrieves a cost.
    *
    * @return Cost value
    */
   public Number getCost4 ()
   {
      return ((Number)get(COST4));
   }

   /**
    * Retrieves a cost.
    *
    * @return Cost value
    */
   public Number getCost5 ()
   {
      return ((Number)get(COST5));
   }

   /**
    * Retrieves a cost.
    *
    * @return Cost value
    */
   public Number getCost6 ()
   {
      return ((Number)get(COST6));
   }

   /**
    * Retrieves a cost.
    *
    * @return Cost value
    */
   public Number getCost7 ()
   {
      return ((Number)get(COST7));
   }

   /**
    * Retrieves a cost.
    *
    * @return Cost value
    */
   public Number getCost8 ()
   {
      return ((Number)get(COST8));
   }

   /**
    * Retrieves a cost.
    *
    * @return Cost value
    */
   public Number getCost9 ()
   {
      return ((Number)get(COST9));
   }

   /**
    * Sets a cost value.
    *
    * @param number Cost value
    */
   public void setCost10 (Number number)
   {
      set(COST10, number);
   }

   /**
    * Sets a cost value.
    *
    * @param number Cost value
    */
   public void setCost4 (Number number)
   {
      set(COST4, number);
   }

   /**
    * Sets a cost value.
    *
    * @param number Cost value
    */
   public void setCost5 (Number number)
   {
      set(COST5, number);
   }

   /**
    * Sets a cost value.
    *
    * @param number Cost value
    */
   public void setCost6 (Number number)
   {
      set(COST6, number);
   }

   /**
    * Sets a cost value.
    *
    * @param number Cost value
    */
   public void setCost7 (Number number)
   {
      set(COST7, number);
   }

   /**
    * Sets a cost value.
    *
    * @param number Cost value
    */
   public void setCost8 (Number number)
   {
      set(COST8, number);
   }

   /**
    * Sets a cost value.
    *
    * @param number Cost value
    */
   public void setCost9 (Number number)
   {
      set(COST9, number);
   }

   /**
    * Retrieves a start date.
    *
    * @return Date start date
    */
   public Date getStart10 ()
   {
      return ((Date)get(START10));
   }

   /**
    * Retrieves a start date.
    *
    * @return Date start date
    */
   public Date getStart6 ()
   {
      return ((Date)get(START6));
   }

   /**
    * Retrieves a start date.
    *
    * @return Date start date
    */
   public Date getStart7 ()
   {
      return ((Date)get(START7));
   }

   /**
    * Retrieves a start date.
    *
    * @return Date start date
    */
   public Date getStart8 ()
   {
      return ((Date)get(START8));
   }

   /**
    * Retrieves a start date.
    *
    * @return Date start date
    */
   public Date getStart9 ()
   {
      return ((Date)get(START9));
   }

   /**
    * Sets a start date.
    *
    * @param date Start date
    */
   public void setStart10 (Date date)
   {
      set(START10, date);
   }

   /**
    * Sets a start date.
    *
    * @param date Start date
    */
   public void setStart6 (Date date)
   {
      set(START6, date);
   }

   /**
    * Sets a start date.
    *
    * @param date Start date
    */
   public void setStart7 (Date date)
   {
      set(START7, date);
   }

   /**
    * Sets a start date.
    *
    * @param date Start date
    */
   public void setStart8 (Date date)
   {
      set(START8, date);
   }

   /**
    * Sets a start date.
    *
    * @param date Start date
    */
   public void setStart9 (Date date)
   {
      set(START9, date);
   }

   /**
    * Retrieves a finish date.
    *
    * @return Date finish date
    */
   public Date getFinish10 ()
   {
      return ((Date)get(FINISH10));
   }

   /**
    * Retrieves a finish date.
    *
    * @return Date finish date
    */
   public Date getFinish6 ()
   {
      return ((Date)get(FINISH6));
   }

   /**
    * Retrieves a finish date.
    *
    * @return Date finish date
    */
   public Date getFinish7 ()
   {
      return ((Date)get(FINISH7));
   }

   /**
    * Retrieves a finish date.
    *
    * @return Date finish date
    */
   public Date getFinish8 ()
   {
      return ((Date)get(FINISH8));
   }

   /**
    * Retrieves a finish date.
    *
    * @return Date finish date
    */
   public Date getFinish9 ()
   {
      return ((Date)get(FINISH9));
   }

   /**
    * User defined finish date field.
    *
    * @param date Date value
    */
   public void setFinish10 (Date date)
   {
      set(FINISH10, date);
   }

   /**
    * User defined finish date field.
    *
    * @param date Date value
    */
   public void setFinish6 (Date date)
   {
      set(FINISH6, date);
   }

   /**
    * User defined finish date field.
    *
    * @param date Date value
    */
   public void setFinish7 (Date date)
   {
      set(FINISH7, date);
   }

   /**
    * User defined finish date field.
    *
    * @param date Date value
    */
   public void setFinish8 (Date date)
   {
      set(FINISH8, date);
   }

   /**
    * User defined finish date field.
    *
    * @param date Date value
    */
   public void setFinish9 (Date date)
   {
      set(FINISH9, date);
   }

   /**
    * Retrieves the overtime cost.
    *
    * @return Cost value
    */
   public Number getOvertimeCost ()
   {
      return m_overtimeCost;
   }

   /**
    * Sets the overtime cost value.
    *
    * @param number Cost value
    */
   public void setOvertimeCost (Number number)
   {
      m_overtimeCost = number;
   }

   /**
    * Sets the value of an outline code field.
    *
    * @param value outline code value
    */
   public void setOutlineCode1 (String value)
   {
      set(OUTLINECODE1, value);
   }

   /**
    * Retrieves the value of an outline code field.
    *
    * @return outline code value
    */
   public String getOutlineCode1 ()
   {
      return ((String)get(OUTLINECODE1));
   }

   /**
    * Sets the value of an outline code field.
    *
    * @param value outline code value
    */
   public void setOutlineCode2 (String value)
   {
      set(OUTLINECODE2, value);
   }

   /**
    * Retrieves the value of an outline code field.
    *
    * @return outline code value
    */
   public String getOutlineCode2 ()
   {
      return ((String)get(OUTLINECODE2));
   }

   /**
    * Sets the value of an outline code field.
    *
    * @param value outline code value
    */
   public void setOutlineCode3 (String value)
   {
      set(OUTLINECODE3, value);
   }

   /**
    * Retrieves the value of an outline code field.
    *
    * @return outline code value
    */
   public String getOutlineCode3 ()
   {
      return ((String)get(OUTLINECODE3));
   }

   /**
    * Sets the value of an outline code field.
    *
    * @param value outline code value
    */
   public void setOutlineCode4 (String value)
   {
      set(OUTLINECODE4, value);
   }

   /**
    * Retrieves the value of an outline code field.
    *
    * @return outline code value
    */
   public String getOutlineCode4 ()
   {
      return ((String)get(OUTLINECODE4));
   }

   /**
    * Sets the value of an outline code field.
    *
    * @param value outline code value
    */
   public void setOutlineCode5 (String value)
   {
      set(OUTLINECODE5, value);
   }

   /**
    * Retrieves the value of an outline code field.
    *
    * @return outline code value
    */
   public String getOutlineCode5 ()
   {
      return ((String)get(OUTLINECODE5));
   }

   /**
    * Sets the value of an outline code field.
    *
    * @param value outline code value
    */
   public void setOutlineCode6 (String value)
   {
      set(OUTLINECODE6, value);
   }

   /**
    * Retrieves the value of an outline code field.
    *
    * @return outline code value
    */
   public String getOutlineCode6 ()
   {
      return ((String)get(OUTLINECODE6));
   }

   /**
    * Sets the value of an outline code field.
    *
    * @param value outline code value
    */
   public void setOutlineCode7 (String value)
   {
      set(OUTLINECODE7, value);
   }

   /**
    * Retrieves the value of an outline code field.
    *
    * @return outline code value
    */
   public String getOutlineCode7 ()
   {
      return ((String)get(OUTLINECODE7));
   }

   /**
    * Sets the value of an outline code field.
    *
    * @param value outline code value
    */
   public void setOutlineCode8 (String value)
   {
      set(OUTLINECODE8, value);
   }

   /**
    * Retrieves the value of an outline code field.
    *
    * @return outline code value
    */
   public String getOutlineCode8 ()
   {
      return ((String)get(OUTLINECODE8));
   }

   /**
    * Sets the value of an outline code field.
    *
    * @param value outline code value
    */
   public void setOutlineCode9 (String value)
   {
      set(OUTLINECODE9, value);
   }

   /**
    * Retrieves the value of an outline code field.
    *
    * @return outline code value
    */
   public String getOutlineCode9 ()
   {
      return ((String)get(OUTLINECODE9));
   }

   /**
    * Sets the value of an outline code field.
    *
    * @param value outline code value
    */
   public void setOutlineCode10 (String value)
   {
      set(OUTLINECODE10, value);
   }

   /**
    * Retrieves the value of an outline code field.
    *
    * @return outline code value
    */
   public String getOutlineCode10 ()
   {
      return ((String)get(OUTLINECODE10));
   }

   /**
    * Retrieves the actual overtime cost for this task.
    *
    * @return actual overtime cost
    */
   public Number getActualOvertimeCost ()
   {
      return (m_actualOvertimeCost);
   }

   /**
    * Sets the actual overtime cost for this task.
    *
    * @param cost actual overtime cost
    */
   public void setActualOvertimeCost (Number cost)
   {
      m_actualOvertimeCost = cost;
   }

   /**
    * Retrieves the actual overtime work value.
    *
    * @return actual overtime work value
    */
   public Duration getActualOvertimeWork ()
   {
      return (m_actualOvertimeWork);
   }

   /**
    * Sets the actual overtime work value.
    *
    * @param work actual overtime work value
    */
   public void setActualOvertimeWork (Duration work)
   {
      m_actualOvertimeWork = work;
   }

   /**
    * Retrieves the fixed cost accrual flag value.
    *
    * @return fixed cost accrual flag
    */
   public AccrueType getFixedCostAccrual ()
   {
      return (m_fixedCostAccrual);
   }

   /**
    * Sets the fixed cost accrual flag value.
    *
    * @param type fixed cost accrual type
    */
   public void setFixedCostAccrual (AccrueType type)
   {
      m_fixedCostAccrual = type;
   }

   /**
    * Retrieves the task hyperlink attribute.
    *
    * @return hyperlink attribute
    */
   public String getHyperlink ()
   {
      return (m_hyperlink);
   }

   /**
    * Retrieves the task hyperlink address attribute.
    *
    * @return hyperlink address attribute
    */
   public String getHyperlinkAddress ()
   {
      return (m_hyperlinkAddress);
   }

   /**
    * Retrieves the task hyperlink sub-address attribute.
    *
    * @return hyperlink sub address attribute
    */
   public String getHyperlinkSubAddress ()
   {
      return (m_hyperlinkSubAddress);
   }

   /**
    * Sets the task hyperlink attribute.
    *
    * @param text hyperlink attribute
    */
   public void setHyperlink (String text)
   {
      m_hyperlink = text;
   }

   /**
    * Sets the task hyperlink address attribute.
    *
    * @param text hyperlink address attribute
    */
   public void setHyperlinkAddress (String text)
   {
      m_hyperlinkAddress = text;
   }

   /**
    * Sets the task hyperlink sub address attribute.
    *
    * @param text hyperlink sub address attribute
    */
   public void setHyperlinkSubAddress (String text)
   {
      m_hyperlinkSubAddress = text;
   }

   /**
    * Retrieves the level assignments flag.
    *
    * @return level assignments flag
    */
   public boolean getLevelAssignments ()
   {
      return (m_levelAssignments);
   }

   /**
    * Sets the level assignments flag.
    *
    * @param flag level assignments flag
    */
   public void setLevelAssignments (boolean flag)
   {
      m_levelAssignments = flag;
   }

   /**
    * Retrieves the leveling can split flag.
    *
    * @return leveling can split flag
    */
   public boolean getLevelingCanSplit ()
   {
      return (m_levelingCanSplit);
   }

   /**
    * Sets the leveling can split flag.
    *
    * @param flag leveling can split flag
    */
   public void setLevelingCanSplit (boolean flag)
   {
      m_levelingCanSplit = flag;
   }

   /**
    * Retrieves the task leveling delay attribute.
    *
    * @return task leveling delay
    */
   public Duration getLevelingDelay ()
   {
      return (m_levelingDelay);
   }

   /**
    * Sets the task leveling delay attribute.
    *
    * @param delay task leveling delay attribute
    */
   public void setLevelingDelay (Duration delay)
   {
      m_levelingDelay = delay;
   }

   /**
    * Retrieves the overtime work attribute.
    *
    * @return overtime work value
    */
   public Duration getOvertimeWork ()
   {
      return (m_overtimeWork);
   }

   /**
    * Sets the overtime work attribute.
    *
    * @param work overtime work value
    */
   public void setOvertimeWork (Duration work)
   {
      m_overtimeWork = work;
   }

   /**
    * Retrieves the preleveled start attribute.
    *
    * @return preleveled start
    */
   public Date getPreleveledStart ()
   {
      return (m_preleveledStart);
   }

   /**
    * Retrieves the preleveled finish attribute.
    *
    * @return preleveled finish
    */
   public Date getPreleveledFinish ()
   {
      return (m_preleveledFinish);
   }

   /**
    * Sets the preleveled start attribute.
    *
    * @param date preleveled start attribute
    */
   public void setPreleveledStart (Date date)
   {
      m_preleveledStart = date;
   }

   /**
    * Sets the preleveled finish attribute.
    *
    * @param date preleveled finish attribute
    */
   public void setPreleveledFinish (Date date)
   {
      m_preleveledFinish = date;
   }

   /**
    * Retrieves the remaining overtime work attribute.
    *
    * @return remaining overtime work
    */
   public Duration getRemainingOvertimeWork ()
   {
      return (m_remainingOvertimeWork);
   }

   /**
    * Sets the remaining overtime work attribute.
    *
    * @param work remaining overtime work
    */
   public void setRemainingOvertimeWork (Duration work)
   {
      m_remainingOvertimeWork = work;
   }

   /**
    * Retrieves the remaining overtime cost.
    *
    * @return remaining overtime cost value
    */
   public Number getRemainingOvertimeCost ()
   {
      return (m_remainingOvertimeCost);
   }

   /**
    * Sets the remaining overtime cost value.
    *
    * @param cost overtime cost value
    */
   public void setRemainingOvertimeCost (Number cost)
   {
      m_remainingOvertimeCost = cost;
   }

   /**
    * Retrieves the base calendar instance associated with this task.
    * Note that this attribute appears in MPP9 and MSPDI files.
    *
    * @return ProjectCalendar instance
    */
   public ProjectCalendar getCalendar ()
   {
      return (m_calendar);
   }


   /**
    * Sets the name of the base calendar associated with this task.
    * Note that this attribute appears in MPP9 and MSPDI files.
    *
    * @param calendar calendar instance
    */
   public void setCalendar (ProjectCalendar calendar)
   {
      m_calendar = calendar;
   }

   /**
    * Retrieve a flag indicating if the task is shown as expanded
    * in MS Project. If this flag is set to true, any sub tasks
    * for this current task will be visible. If this is false,
    * any sub tasks will be hidden.
    *
    * @return boolean flag
    */
   public boolean getExpanded ()
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
   public void setExpanded (boolean expanded)
   {
      m_expanded = expanded;
   }


   /**
    * Retrieve the value of a field using its alias.
    *
    * @param alias field alias
    * @return field value
    */
   public Object getFieldByAlias (String alias)
   {
      Object result = null;

      int field = getParentFile().getAliasTaskField(alias);

      if (field != -1)
      {
         result = get(field);
      }

      return (result);
   }

   /**
    * Set the value of a field using its alias.
    *
    * @param alias field alias
    * @param value field value
    */
   public void setFieldByAlias (String alias, Object value)
   {
      int field = getParentFile().getAliasTaskField(alias);

      if (field != -1)
      {
         set(field, value);
      }
   }

   /**
    * This method retrieves a list of task splits. Each split is represented
    * by a number of working hours since the start of the task to the end of
    * the current split. The list will always follow the pattern
    * task time, split time, task time and so on. For example, if we have a
    * 5 day task which is represented as 2 days work, a one day, then three
    * days work, the splits list will contain 16h, 24h, 48h. Assuming an 8 hour
    * working day, this equates to the end of the first working segment beging
    * 2 working days from the task start date (16h), the end of the first split
    * being 3 working days from the task start date (24h) and finally, the end
    * of the entire task being 6 working days from the task start date (48h).
    *
    * Note that this method will return null if the task is not split.
    *
    * @return list of split times
    */
   public List getSplits ()
   {
      return (m_splits);
   }

   /**
    * Internal method used to set the list of splits.
    *
    * @param splits list of split times
    */
   public void setSplits (List splits)
   {
      m_splits = splits;
   }

   /**
    * Removes this task from the project.
    */
   public void remove ()
   {
      getParentFile().removeTask(this);
   }

   /**
    * Retrieve the sub project represented by this task.
    *
    * @return sub project
    */
   public SubProject getSubProject ()
   {
      return (m_subProject);
   }

   /**
    * Set the sub project represented by this task.
    *
    * @param subProject sub project
    */
   public void setSubProject (SubProject subProject)
   {
      m_subProject = subProject;
   }

   /**
    * This method inserts a name value pair into internal storage.
    *
    * @param key attribute identifier
    * @param value attribute value
    */
   public void put (int key, Object value)
   {
      m_array[key] = value;
   }

   /**
    * Given an attribute id, this method retrieves that attribute
    * value from internal storage.
    *
    * @param key name of requested field value
    * @return requested value
    */
   public Object get (int key)
   {
      return (m_array[key]);
   }

   /**
    * This method inserts a name value pair into internal storage.
    *
    * @param key attribute identifier
    * @param value attribute value
    */
   private void put (int key, boolean value)
   {
      put (key, (value==true ? Boolean.TRUE : Boolean.FALSE));
   }

   /**
    * Array of field values.
    */
   private Object[] m_array = new Object[MAX_FIELDS + MAX_EXTENDED_FIELDS];


   /**
    * This is a reference to the parent task, as specified by the
    * outline level.
    */
   private Task m_parent;

   /**
    * This list holds references to all tasks that are children of the
    * current task as specified by the outline level.
    */
   private List m_children = new LinkedList();

   /**
    * List of resource assignments for this task.
    */
   private List m_assignments = new LinkedList();

   /**
    * Task notes associated with this task.
    */
   private String m_notes;

   /**
    * Recurring task details associated with this task.
    */
   private RecurringTask m_recurringTask;

   /**
    * The following member variables are extended attributes. They are
    * do not form part of the MPX file format definition, and are neither
    * loaded from an MPX file, or saved to an MPX file. Their purpose
    * is to provide storage for attributes which are defined by later versions
    * of Microsoft Project. This allows these attributes to be manipulated
    * when they have been retrieved from file formats other than MPX.
    */
   private boolean m_estimated;
   private Date m_deadline;
   private TaskType m_type = TaskType.FIXED_UNITS;
   private boolean m_effortDriven;
   private Number m_overtimeCost;
   private Number m_actualOvertimeCost;
   private Duration m_actualOvertimeWork;
   private AccrueType m_fixedCostAccrual;
   private String m_hyperlink;
   private String m_hyperlinkAddress;
   private String m_hyperlinkSubAddress;
   private boolean m_levelAssignments;
   private boolean m_levelingCanSplit;
   private Duration m_levelingDelay;
   private Duration m_overtimeWork;
   private Date m_preleveledStart;
   private Date m_preleveledFinish;
   private Duration m_remainingOvertimeWork;
   private Number m_remainingOvertimeCost;
   private ProjectCalendar m_calendar;
   private boolean m_null;
   private String m_wbsLevel;
   private TimeUnit m_durationFormat;
   private boolean m_resumeValid;
   private boolean m_recurring;
   private boolean m_overAllocated;
   private boolean m_subprojectReadOnly;
   private Integer m_subprojectTaskUniqueID;
   private Integer m_subprojectTasksUniqueIDOffset;
   private boolean m_externalTask;
   private String m_externalTaskProject;
   private Number m_acwp;
   private TimeUnit m_levelingDelayFormat;
   private boolean m_ignoreResourceCalendar;
   private Integer m_physicalPercentComplete;
   private EarnedValueMethod m_earnedValueMethod;
   private Duration m_actualWorkProtected;
   private Duration m_actualOvertimeWorkProtected;
   private Duration m_regularWork;
   private boolean m_expanded = true;
   private List m_splits;
   private SubProject m_subProject;

   /**
    * The % Complete field contains the current status of a task, expressed as
    * the percentage of the task's duration that has been completed. You can
    * enter percent complete, or you can have Microsoft Project calculate it
    * for you based on actual duration.
    */
   public static final int PERCENTAGE_COMPLETE = 44;

   /**
    * The % Work Complete field contains the current status of a task, expressed as the
    * percentage of the task's work that has been completed. You can enter percent work
    * complete, or you can have Microsoft Project calculate it for you based on actual
    * work on the task.
    */
   public static final int PERCENTAGE_WORK_COMPLETE = 25;

   /**
    * The Actual Cost field shows costs incurred for work already performed by all resources
    * on a task, along with any other recorded costs associated with the task. You can enter
    * all the actual costs or have Microsoft Project calculate them for you.
    */
   public static final int ACTUAL_COST = 32;

   /**
    * The Actual Duration field shows the span of actual working time for a task so far,
    * based on the scheduled duration and current remaining work or completion percentage.
    */
   public static final int ACTUAL_DURATION = 42;

   /**
    * The Actual Finish field shows the date and time that a task actually finished.
    * Microsoft Project sets the Actual Finish field to the scheduled finish date if
    * the completion percentage is 100. This field contains "NA" until you enter actual
    * information or set the completion percentage to 100.
    */
   public static final int ACTUAL_FINISH = 59;

   /**
    * The Actual Start field shows the date and time that a task actually began.
    * When a task is first created, the Actual Start field contains "NA." Once you
    * enter the first actual work or a completion percentage for a task, Microsoft
    * Project sets the actual start date to the scheduled start date.
    */
   public static final int ACTUAL_START = 58;

   /**
    * The Actual Work field shows the amount of work that has already been done by
    * the resources assigned to a task.
    */
   public static final int ACTUAL_WORK = 22;

   /**
    * The Baseline Cost field shows the total planned cost for a task. Baseline cost
    * is also referred to as budget at completion (BAC).
    */
   public static final int BASELINE_COST = 31;

   /**
    * The Baseline Duration field shows the original span of time planned to complete a task.
    */
   public static final int BASELINE_DURATION = 41;

   /**
    * The Baseline Finish field shows the planned completion date for a task at the time
    * you saved a baseline. Information in this field becomes available when you set a
    * baseline for a task.
    */
   public static final int BASELINE_FINISH = 57;

   /**
    * The Baseline Start field shows the planned beginning date for a task at the time
    * you saved a baseline. Information in this field becomes available when you set a baseline.
    */
   public static final int BASELINE_START = 56;

   /**
    * The Baseline Work field shows the originally planned amount of work to be performed
    * by all resources assigned to a task. This field shows the planned person-hours
    * scheduled for a task. Information in the Baseline Work field becomes available
    * when you set a baseline for the project.
    */
   public static final int BASELINE_WORK = 21;

   /**
    * The BCWP (budgeted cost of work performed) field contains the cumulative value
    * of the assignment's timephased percent complete multiplied by the assignments
    * timephased baseline cost. BCWP is calculated up to the status date or today's date.
    * This information is also known as earned value.
    */
   public static final int BCWP = 86;

   /**
    * The BCWS (budgeted cost of work scheduled) field contains the cumulative timephased
    * baseline costs up to the status date or todays date.
    */
   public static final int BCWS = 85;

   /**
    * The Confirmed field indicates whether all resources assigned to a task have
    * accepted or rejected the task assignment in response to a TeamAssign message
    * regarding their assignments.
    */
   public static final int CONFIRMED = 135;

   /**
    *  The Constraint Date field shows the specific date associated with certain
    * constraint types, such as Must Start On, Must Finish On, Start No Earlier Than,
    * Start No Later Than, Finish No Earlier Than, and Finish No Later Than.
    */
   public static final int CONSTRAINT_DATE = 68;

   /**
    * The Constraint Type field provides choices for the type of constraint you can apply
    * for scheduling a task. The options are:
    * - As Late As Possible (default in a project scheduled from the finish date)
    * - As Soon As Possible (default in a project scheduled from the start date)
    * - Finish No Earlier Than
    * - Finish No Later Than
    * - Must Start On
    * - Must Finish On
    * - Start No Earlier Than
    * - Start No Later Than
    */
   public static final int CONSTRAINT_TYPE = 91;

   /**
    *  The Contact field contains the name of an individual responsible for a task.
    */
   public static final int CONTACT = 15;

   /**
    * The Cost field shows the total scheduled, or projected, cost for a task,
    * based on costs already incurred for work performed by all resources assigned
    * to the task, in addition to the costs planned for the remaining work for the
    * assignment. This can also be referred to as estimate at completion (EAC).
    */
   public static final int COST = 30;

   /**
    * The Cost fields show any custom task cost information you want to enter in your project.
    */
   public static final int COST1 = 36;

   /**
    * The Cost fields show any custom task cost information you want to enter in your project.
    */
   public static final int COST2 = 37;

   /**
    * The Cost fields show any custom task cost information you want to enter in your project.
    */
   public static final int COST3 = 38;

   /**
    * The Cost Variance field shows the difference between the baseline cost and total
    * cost for a task. The total cost is the current estimate of costs based on actual
    * costs and remaining costs. This is also referred to as variance at completion (VAC).
    */
   public static final int COST_VARIANCE = 34;

   /**
    * The Created field contains the date and time when a task was added to the project.
    */
   public static final int CREATE_DATE = 125;

   /**
    * The Critical field indicates whether a task has any room in the schedule to slip,
    * or if a task is on the critical path. The Critical field contains Yes if the task
    * is critical and No if the task is not critical.
    */
   public static final int CRITICAL = 82;

   /**
    * The CV (earned value cost variance) field shows the difference between how much
    * it should have cost to achieve the current level of completion on the task, and
    * how much it has actually cost to achieve the current level of completion up to
    * the status date or todays date.
    */
   public static final int CV = 88;

   /**
    * The amount of time a task can slip before it affects another task's dates or the
    * project finish date. Free slack is the amount of time a task can slip before it
    * delays another task. Total slack is the amount of time a task can slip before it
    * delays the project finish date. When the total slack is negative, the task
    * duration is too long for its successor to begin on the date required by a constraint.
    */
   public static final int DELAY = 92;

   /**
    * The Duration field is the total span of active working time for a task.
    * This is generally the amount of time from the start to the finish of a task.
    * The default for new tasks is 1 day (1d).
    */
   public static final int DURATION = 40;

   /**
    * The Duration fields are custom fields that show any specialized task duration
    * information you want to enter and store separately in your project.
    */
   public static final int DURATION1 = 46;

   /**
    * The Duration fields are custom fields that show any specialized task duration
    * information you want to enter and store separately in your project.
    */
   public static final int DURATION2 = 47;

   /**
    * The Duration fields are custom fields that show any specialized task duration
    * information you want to enter and store separately in your project.
    */
   public static final int DURATION3 = 48;

   /**
    * The Duration Variance field contains the difference between the baseline duration
    * of a task and the total duration (current estimate) of a task.
    */
   public static final int DURATION_VARIANCE = 45;

   /**
    * The Early Finish field contains the earliest date that a task could possibly finish,
    * based on early finish dates of predecessor and successor tasks, other constraints,
    * and any leveling delay.
    */
   public static final int EARLY_FINISH = 53;

   /**
    * The Early Start field contains the earliest date that a task could possibly begin,
    * based on the early start dates of predecessor and successor tasks, and other constraints.
    */
   public static final int EARLY_START = 52;

   /**
    * The Finish field shows the date and time that a task is scheduled to be completed.
    * You can enter the finish date you want, to indicate the date when the task should be
    * completed. Or, you can have Microsoft Project calculate the finish date.
    */
   public static final int FINISH = 51;

   /**
    * The Finish fields are custom fields that show any specific task finish date information
    * you want to enter and store separately in your project.
    */
   public static final int FINISH1 = 61;

   /**
    * The Finish fields are custom fields that show any specific task finish date information
    * you want to enter and store separately in your project.
    */
   public static final int FINISH2 = 63;

   /**
    * The Finish fields are custom fields that show any specific task finish date information
    * you want to enter and store separately in your project.
    */
   public static final int FINISH3 = 65;

   /**
    * The Finish fields are custom fields that show any specific task finish date information
    * you want to enter and store separately in your project.
    */
   public static final int FINISH4 = 127;

   /**
    * The Finish fields are custom fields that show any specific task finish date information
    * you want to enter and store separately in your project.
    */
   public static final int FINISH5 = 129;

   /**
    * The Finish Variance field contains the amount of time that represents the difference
    * between a task's baseline finish date and its current finish date.
    */
   public static final int FINISH_VARIANCE = 67;

   /**
    * Whether  fixed or not. Boolean value
    */
   public static final int FIXED = 80;

   /**
    * The Fixed Cost field shows any task expense that is not associated
    * with a resource cost.
    */
   public static final int FIXED_COST = 35;

   /**
    * The Flag fields indicate whether a task is marked for further action or
    * identification of some kind. To mark a task, click Yes in a Flag field. If you
    * don't want a task marked, click No.
    */
   public static final int FLAG1 = 110;

   /**
    * The Flag fields indicate whether a task is marked for further action or
    * identification of some kind. To mark a task, click Yes in a Flag field. If you
    * don't want a task marked, click No.
    */
   public static final int FLAG2 = 111;

   /**
    * The Flag fields indicate whether a task is marked for further action or
    * identification of some kind. To mark a task, click Yes in a Flag field. If you
    * don't want a task marked, click No.
    */
   public static final int FLAG3 = 112;

   /**
    * The Flag fields indicate whether a task is marked for further action or
    * identification of some kind. To mark a task, click Yes in a Flag field. If you
    * don't want a task marked, click No.
    */
   public static final int FLAG4 = 113;

   /**
    * The Flag fields indicate whether a task is marked for further action or
    * identification of some kind. To mark a task, click Yes in a Flag field. If you
    * don't want a task marked, click No.
    */
   public static final int FLAG5 = 114;

   /**
    * The Flag fields indicate whether a task is marked for further action or
    * identification of some kind. To mark a task, click Yes in a Flag field. If you
    * don't want a task marked, click No.
    */
   public static final int FLAG6 = 115;

   /**
    * The Flag fields indicate whether a task is marked for further action or
    * identification of some kind. To mark a task, click Yes in a Flag field. If you
    * don't want a task marked, click No.
    */
   public static final int FLAG7 = 116;

   /**
    * The Flag fields indicate whether a task is marked for further action or
    * identification of some kind. To mark a task, click Yes in a Flag field. If you
    * don't want a task marked, click No.
    */
   public static final int FLAG8 = 117;

   /**
    * The Flag fields indicate whether a task is marked for further action or
    * identification of some kind. To mark a task, click Yes in a Flag field. If you
    * don't want a task marked, click No.
    */
   public static final int FLAG9 = 118;

   /**
    * The Flag fields indicate whether a task is marked for further action or
    * identification of some kind. To mark a task, click Yes in a Flag field. If you
    * don't want a task marked, click No.
    */
   public static final int FLAG10 = 119;

   /**
    * The Free Slack field contains the amount of time that a task can be delayed without
    * delaying any successor tasks. If the task has no successors, free slack is the amount
    * of time that a task can be delayed without delaying the entire project's finish date.
    */
   public static final int FREE_SLACK = 93;

   /**
    * The Hide Bar field indicates whether the Gantt bars and Calendar bars for a task
    * are hidden. Click Yes in the Hide Bar field to hide the bar for the task. Click No
    * in the Hide Bar field to show the bar for the task.
    */
   public static final int HIDE_BAR = 123;

   /**
    * The ID field contains the identifier number that Microsoft Project automatically
    * assigns to each task as you add it to the project. The ID indicates the position
    * of a task with respect to the other tasks.
    */
   public static final int ID = 90;

   /**
    * The Late Finish field contains the latest date that a task can finish without
    * delaying the finish of the project. This date is based on the task's late start
    * date, as well as the late start and late finish dates of predecessor and successor
    *  tasks, and other constraints.
    */
   public static final int LATE_FINISH = 55;

   /**
    * The Late Start field contains the latest date that a task can start without delaying
    * the finish of the project. This date is based on the tasks start date, as well as
    * the late start and late finish dates of predecessor and successor tasks, and other constraints.
    */
   public static final int LATE_START = 54;

   /**
    *  The Linked Fields field indicates whether there are OLE links to the task,
    * either from elsewhere in the active project, another Microsoft Project file,
    * or from another program.
    */
   public static final int LINKED_FIELDS = 122;

   /**
    * The Marked field indicates whether a task is marked for further action or identification
    * of some kind. To mark a task, click Yes in the Marked field. If you don't want a task
    * marked, click No.
    */
   public static final int MARKED = 83;

   /**
    * The Milestone field indicates whether a task is a milestone.
    */
   public static final int MILESTONE = 81;

   /**
    * The Name field contains the name of a task.
    */
   public static final int NAME = 1;

   /**
    * The Notes field contains notes that you can enter about a task. You can use task
    * notes to help maintain a history for a task.
    */
   //private static final int NOTES = 14;

   /**
    * The Number fields show any custom numeric information you enter in your project
    * regarding tasks.
    */
   public static final int NUMBER1 = 140;

   /**
    * The Number fields show any custom numeric information you enter in your project
    * regarding tasks.
    */
   public static final int NUMBER2 = 141;

   /**
    * The Number fields show any custom numeric information you enter in your project
    * regarding tasks.
    */
   public static final int NUMBER3 = 142;

   /**
    * The Number fields show any custom numeric information you enter in your project
    * regarding tasks.
    */
   public static final int NUMBER4 = 143;

   /**
    * The Number fields show any custom numeric information you enter in your project
    * regarding tasks.
    */
   public static final int NUMBER5 = 144;

   /**
    * The Objects field contains the number of objects attached to a task.
    */
   public static final int OBJECTS = 121;

   /**
    * The Outline Level field contains the number that indicates the level of the task
    * in the project outline hierarchy.
    */
   public static final int OUTLINE_LEVEL = 3;

   /**
    * The Outline Number field contains the number of the task in the structure of an outline.
    * This number indicates the task's position within the hierarchical structure of the
    * project outline. The outline number is similar to a WBS (work breakdown structure)
    * number, except that the outline number is automatically entered by Microsoft Project.
    */
   public static final int OUTLINE_NUMBER = 99;

   /**
    * The Predecessors field lists the task ID numbers for the predecessor tasks on which
    * the task depends before it can be started or finished. Each predecessor is linked to
    * the task by a specific type of task dependency and a lead time or lag time.
    */
   public static final int PREDECESSORS = 70;

   /**
    * The Priority field provides choices for the level of importance assigned to a
    * task, which in turn indicates how readily a task can be delayed or split
    * during resource leveling. The default priority is Medium. Those tasks with a
    * priority of Do Not Level are never delayed or split when Microsoft Project
    * levels tasks that have overallocated resources assigned.
    */
   public static final int PRIORITY = 95;

   /**
    * The Project field shows the name of the project from which a task originated.
    * This can be the name of the active project file. If there are other projects
    * inserted into the active project file, the name of the inserted project appears
    * in this field for the task.
    */
   public static final int PROJECT = 97;

   /**
    * The Remaining Cost field shows the remaining scheduled expense of a task
    * that will be incurred in completing the remaining scheduled work by all
    * resources assigned to the task.
    */
   public static final int REMAINING_COST = 33;

   /**
    * The Remaining Duration field shows the amount of time required to complete
    * the unfinished portion of a task.
    */
   public static final int REMAINING_DURATION = 43;

   /**
    * The Remaining Work field shows the amount of time, or person-hours,
    * still required by all assigned resources to complete a task.
    */
   public static final int REMAINING_WORK = 23;

   /**
    * The Resource Group field contains the list of resource groups to which the
    * resources assigned to a task belong.
    */
   public static final int RESOURCE_GROUP = 16;

   /**
    * The Resource Initials field lists the abbreviations for the names of resources
    * assigned to a task. These initials can serve as substitutes for the names.
    */
   public static final int RESOURCE_INITIALS = 73;

   /**
    * The Resource Names field lists the names of all resources assigned to a task.
    */
   public static final int RESOURCE_NAMES = 72;

   /**
    * The Resume field shows the date that the remaining portion of a task is scheduled
    * to resume after you enter a new value for the % Complete field. The Resume field
    * is also recalculated when the remaining portion of a task is moved to a new date.
    */
   public static final int RESUME = 151;

   /**
    * The Resume No Earlier than field constains the date which is the earliest time
    * to restart this task.
    */
   public static final int RESUME_NO_EARLIER_THAN = 152;

   /**
    * For subtasks, the Rollup field indicates whether information on the subtask
    * Gantt bars will be rolled up to the summary task bar. For summary tasks, the
    * Rollup field indicates whether the summary task bar displays rolled up bars.
    * You must have the Rollup field for summary tasks set to Yes for any subtasks
    * to roll up to them.
    */
   public static final int ROLLUP = 84;

   /**
    * The Start field shows the date and time that a task is scheduled to begin.
    * You can enter the start date you want, to indicate the date when the task
    * should begin. Or, you can have Microsoft Project calculate the start date.
    */
   public static final int START = 50;

   /**
    * The Start fields are custom fields that show any specific task start date
    * information you want to enter and store separately in your project.
    */
   public static final int START1 = 60;

   /**
    * The Start fields are custom fields that show any specific task start date
    * information you want to enter and store separately in your project.
    */
   public static final int START2 = 62;

   /**
    * The Start fields are custom fields that show any specific task start date
    * information you want to enter and store separately in your project.
    */
   public static final int START3 = 64;

   /**
    * The Start fields are custom fields that show any specific task start date
    * information you want to enter and store separately in your project.
    */
   public static final int START4 = 126;

   /**
    * The Start fields are custom fields that show any specific task start date
    * information you want to enter and store separately in your project.
    */
   public static final int START5 = 128;

   /**
    * The Start fields are custom fields that show any specific task start date
    * information you want to enter and store separately in your project.
    */
   public static final int START_VARIANCE = 66;

   /**
    * The Stop field shows the date that represents the end of the actual portion of a task.
    * Typically, Microsoft Project calculates the stop date. However, you can edit this date as well.
    */
   public static final int STOP = 150;

   /**
    * The Subproject File field contains the name of a project inserted into the active project file.
    * The Subproject File field contains the inserted project's path and file name.
    */
   public static final int SUBPROJECT_NAME = 96;

   /**
    * The Successors field lists the task ID numbers for the successor tasks to a task.
    * A task must start or finish before successor tasks can start or finish. Each successor
    * is linked to the task by a specific type of task dependency and a lead time or lag time.
    */
   public static final int SUCCESSORS = 71;

   /**
    * The Summary field indicates whether a task is a summary task.
    */
   public static final int SUMMARY = 120;

   /**
    * The SV (earned value schedule variance) field shows the difference in cost terms
    * between the current progress and the baseline plan of the task up to the status
    * date or today's date. You can use SV to check costs to determine whether tasks
    * are on schedule.
    */
   public static final int SV = 87;

   /**
    * The Text fields show any custom text information you want to enter in your project about tasks.
    */
   public static final int TEXT1 = 4;

   /**
    * The Text fields show any custom text information you want to enter in your project about tasks.
    */
   public static final int TEXT2 = 5;

   /**
    * The Text fields show any custom text information you want to enter in your project about tasks.
    */
   public static final int TEXT3 = 6;

   /**
    * The Text fields show any custom text information you want to enter in your project about tasks.
    */
   public static final int TEXT4 = 7;

   /**
    * The Text fields show any custom text information you want to enter in your project about tasks.
    */
   public static final int TEXT5 = 8;

   /**
    * The Text fields show any custom text information you want to enter in your project about tasks.
    */
   public static final int TEXT6 = 9;

   /**
    * The Text fields show any custom text information you want to enter in your project about tasks.
    */
   public static final int TEXT7 = 10;

   /**
    * The Text fields show any custom text information you want to enter in your project about tasks.
    */
   public static final int TEXT8 = 11;

   /**
    * The Text fields show any custom text information you want to enter in your project about tasks.
    */
   public static final int TEXT9 = 12;

   /**
    * The Text fields show any custom text information you want to enter in your project about tasks.
    */
   public static final int TEXT10 = 13;

   /**
    * The Total Slack field contains the amount of time a task can be delayed without delaying
    * the project's finish date.
    */
   public static final int TOTAL_SLACK = 94;

   /**
    * The Unique ID field contains the number that Microsoft Project automatically designates
    * whenever a new task is created. This number indicates the sequence in which the task was
    * created, regardless of placement in the schedule.
    */
   public static final int UNIQUE_ID = 98;

   /**
    * The Unique ID Predecessors field lists the unique ID numbers for the predecessor
    * tasks on which a task depends before it can be started or finished. Each predecessor
    * is linked to the task by a specific type of task dependency and a lead time or lag time.
    */
   public static final int UNIQUE_ID_PREDECESSORS = 76;

   /**
    * The Unique ID Successors field lists the unique ID numbers for the successor tasks
    * to a task. A task must start or finish before successor tasks can start or finish.
    * Each successor is linked to the task by a specific type of task dependency and a
    * lead time or lag time.
    */
   public static final int UNIQUE_ID_SUCCESSORS = 75;

   /**
    * The Update Needed field indicates whether a TeamUpdate messageshould be sent to
    * the assigned resources because of changes to the start date, finish date, or
    * resource reassignments of the task.
    */
   public static final int UPDATE_NEEDED = 136;

   /**
    * The work breakdown structure code. The WBS field contains an alphanumeric code
    * you can use to represent the task's position within the hierarchical structure
    * of the project. This field is similar to the outline number, except that you can edit it.
    */
   public static final int WBS = 2;

   /**
    * The Work field shows the total amount of work scheduled to be performed on a task
    * by all assigned resources. This field shows the total work, or person-hours, for a task.
    */
   public static final int WORK = 20;

   /**
    *  The Work Variance field contains the difference between a task's baseline work and
    * the currently scheduled work.
    */
   public static final int WORK_VARIANCE = 24;

   /**
    * Maximum number of fields in this record. Note that this is package
    * access to allow the task model to get at it.
    */
   public static final int MAX_FIELDS = 153;

   private static final int EXTENDED_OFFSET = MAX_FIELDS;

   /**
    * Maximum number of extended fields in this record.
    */
   private static final int MAX_EXTENDED_FIELDS = 89;

   /**
    * The following constants are used purely to identify custom fields,
    * these field names are NOT written to the MPX file.
    */
   public static final int TEXT11 = EXTENDED_OFFSET + 0;
   public static final int TEXT12 = EXTENDED_OFFSET + 1;
   public static final int TEXT13 = EXTENDED_OFFSET + 2;
   public static final int TEXT14 = EXTENDED_OFFSET + 3;
   public static final int TEXT15 = EXTENDED_OFFSET + 4;
   public static final int TEXT16 = EXTENDED_OFFSET + 5;
   public static final int TEXT17 = EXTENDED_OFFSET + 6;
   public static final int TEXT18 = EXTENDED_OFFSET + 7;
   public static final int TEXT19 = EXTENDED_OFFSET + 8;
   public static final int TEXT20 = EXTENDED_OFFSET + 9;
   public static final int TEXT21 = EXTENDED_OFFSET + 10;
   public static final int TEXT22 = EXTENDED_OFFSET + 11;
   public static final int TEXT23 = EXTENDED_OFFSET + 12;
   public static final int TEXT24 = EXTENDED_OFFSET + 13;
   public static final int TEXT25 = EXTENDED_OFFSET + 14;
   public static final int TEXT26 = EXTENDED_OFFSET + 15;
   public static final int TEXT27 = EXTENDED_OFFSET + 16;
   public static final int TEXT28 = EXTENDED_OFFSET + 17;
   public static final int TEXT29 = EXTENDED_OFFSET + 18;
   public static final int TEXT30 = EXTENDED_OFFSET + 19;
   public static final int START6 = EXTENDED_OFFSET + 20;
   public static final int START7 = EXTENDED_OFFSET + 21;
   public static final int START8 = EXTENDED_OFFSET + 22;
   public static final int START9 = EXTENDED_OFFSET + 23;
   public static final int START10 = EXTENDED_OFFSET + 24;
   public static final int FINISH6 = EXTENDED_OFFSET + 25;
   public static final int FINISH7 = EXTENDED_OFFSET + 26;
   public static final int FINISH8 = EXTENDED_OFFSET + 27;
   public static final int FINISH9 = EXTENDED_OFFSET + 28;
   public static final int FINISH10 = EXTENDED_OFFSET + 29;
   public static final int COST4 = EXTENDED_OFFSET + 30;
   public static final int COST5 = EXTENDED_OFFSET + 31;
   public static final int COST6 = EXTENDED_OFFSET + 32;
   public static final int COST7 = EXTENDED_OFFSET + 33;
   public static final int COST8 = EXTENDED_OFFSET + 34;
   public static final int COST9 = EXTENDED_OFFSET + 35;
   public static final int COST10 = EXTENDED_OFFSET + 36;
   public static final int DATE1 = EXTENDED_OFFSET + 37;
   public static final int DATE2 = EXTENDED_OFFSET + 38;
   public static final int DATE3 = EXTENDED_OFFSET + 39;
   public static final int DATE4 = EXTENDED_OFFSET + 40;
   public static final int DATE5 = EXTENDED_OFFSET + 41;
   public static final int DATE6 = EXTENDED_OFFSET + 42;
   public static final int DATE7 = EXTENDED_OFFSET + 43;
   public static final int DATE8 = EXTENDED_OFFSET + 44;
   public static final int DATE9 = EXTENDED_OFFSET + 45;
   public static final int DATE10 = EXTENDED_OFFSET + 46;
   public static final int FLAG11 = EXTENDED_OFFSET + 47;
   public static final int FLAG12 = EXTENDED_OFFSET + 48;
   public static final int FLAG13 = EXTENDED_OFFSET + 49;
   public static final int FLAG14 = EXTENDED_OFFSET + 50;
   public static final int FLAG15 = EXTENDED_OFFSET + 51;
   public static final int FLAG16 = EXTENDED_OFFSET + 52;
   public static final int FLAG17 = EXTENDED_OFFSET + 53;
   public static final int FLAG18 = EXTENDED_OFFSET + 54;
   public static final int FLAG19 = EXTENDED_OFFSET + 55;
   public static final int FLAG20 = EXTENDED_OFFSET + 56;
   public static final int NUMBER6 = EXTENDED_OFFSET + 57;
   public static final int NUMBER7 = EXTENDED_OFFSET + 58;
   public static final int NUMBER8 = EXTENDED_OFFSET + 59;
   public static final int NUMBER9 = EXTENDED_OFFSET + 60;
   public static final int NUMBER10 = EXTENDED_OFFSET + 61;
   public static final int NUMBER11 = EXTENDED_OFFSET + 62;
   public static final int NUMBER12 = EXTENDED_OFFSET + 63;
   public static final int NUMBER13 = EXTENDED_OFFSET + 64;
   public static final int NUMBER14 = EXTENDED_OFFSET + 65;
   public static final int NUMBER15 = EXTENDED_OFFSET + 66;
   public static final int NUMBER16 = EXTENDED_OFFSET + 67;
   public static final int NUMBER17 = EXTENDED_OFFSET + 68;
   public static final int NUMBER18 = EXTENDED_OFFSET + 69;
   public static final int NUMBER19 = EXTENDED_OFFSET + 70;
   public static final int NUMBER20 = EXTENDED_OFFSET + 71;
   public static final int DURATION4 = EXTENDED_OFFSET + 72;
   public static final int DURATION5 = EXTENDED_OFFSET + 73;
   public static final int DURATION6 = EXTENDED_OFFSET + 74;
   public static final int DURATION7 = EXTENDED_OFFSET + 75;
   public static final int DURATION8 = EXTENDED_OFFSET + 76;
   public static final int DURATION9 = EXTENDED_OFFSET + 77;
   public static final int DURATION10 = EXTENDED_OFFSET + 78;
   public static final int OUTLINECODE1 = EXTENDED_OFFSET + 79;
   public static final int OUTLINECODE2 = EXTENDED_OFFSET + 80;
   public static final int OUTLINECODE3 = EXTENDED_OFFSET + 81;
   public static final int OUTLINECODE4 = EXTENDED_OFFSET + 82;
   public static final int OUTLINECODE5 = EXTENDED_OFFSET + 83;
   public static final int OUTLINECODE6 = EXTENDED_OFFSET + 84;
   public static final int OUTLINECODE7 = EXTENDED_OFFSET + 85;
   public static final int OUTLINECODE8 = EXTENDED_OFFSET + 86;
   public static final int OUTLINECODE9 = EXTENDED_OFFSET + 87;
   public static final int OUTLINECODE10 = EXTENDED_OFFSET + 88;

   public static final DataType[] FIELD_TYPES = new DataType [MAX_FIELDS + MAX_EXTENDED_FIELDS];
   static
   {
      FIELD_TYPES[ACTUAL_COST] = DataType.CURRENCY;
      FIELD_TYPES[BASELINE_COST] = DataType.CURRENCY;
      FIELD_TYPES[BCWP] = DataType.CURRENCY;
      FIELD_TYPES[BCWS] = DataType.CURRENCY;
      FIELD_TYPES[COST] = DataType.CURRENCY;
      FIELD_TYPES[COST1] = DataType.CURRENCY;
      FIELD_TYPES[COST2] = DataType.CURRENCY;
      FIELD_TYPES[COST3] = DataType.CURRENCY;
      FIELD_TYPES[COST_VARIANCE] = DataType.CURRENCY;
      FIELD_TYPES[CV] = DataType.CURRENCY;
      FIELD_TYPES[FIXED_COST] = DataType.CURRENCY;
      FIELD_TYPES[REMAINING_COST] = DataType.CURRENCY;
      FIELD_TYPES[SV] = DataType.CURRENCY;
      FIELD_TYPES[COST10] = DataType.CURRENCY;
      FIELD_TYPES[COST4] = DataType.CURRENCY;
      FIELD_TYPES[COST5] = DataType.CURRENCY;
      FIELD_TYPES[COST6] = DataType.CURRENCY;
      FIELD_TYPES[COST7] = DataType.CURRENCY;
      FIELD_TYPES[COST8] = DataType.CURRENCY;
      FIELD_TYPES[COST9] = DataType.CURRENCY;

      FIELD_TYPES[ACTUAL_FINISH] = DataType.DATE;
      FIELD_TYPES[ACTUAL_START] = DataType.DATE;
      FIELD_TYPES[BASELINE_FINISH] = DataType.DATE;
      FIELD_TYPES[BASELINE_START] = DataType.DATE;
      FIELD_TYPES[CONSTRAINT_DATE] = DataType.DATE;
      FIELD_TYPES[CREATE_DATE] = DataType.DATE;
      FIELD_TYPES[EARLY_FINISH] = DataType.DATE;
      FIELD_TYPES[EARLY_START] = DataType.DATE;
      FIELD_TYPES[FINISH] = DataType.DATE;
      FIELD_TYPES[FINISH1] = DataType.DATE;
      FIELD_TYPES[FINISH2] = DataType.DATE;
      FIELD_TYPES[FINISH3] = DataType.DATE;
      FIELD_TYPES[FINISH4] = DataType.DATE;
      FIELD_TYPES[FINISH5] = DataType.DATE;
      FIELD_TYPES[LATE_FINISH] = DataType.DATE;
      FIELD_TYPES[LATE_START] = DataType.DATE;
      FIELD_TYPES[RESUME] = DataType.DATE;
      FIELD_TYPES[RESUME_NO_EARLIER_THAN] = DataType.DATE;
      FIELD_TYPES[START] = DataType.DATE;
      FIELD_TYPES[START1] = DataType.DATE;
      FIELD_TYPES[START2] = DataType.DATE;
      FIELD_TYPES[START3] = DataType.DATE;
      FIELD_TYPES[START4] = DataType.DATE;
      FIELD_TYPES[START5] = DataType.DATE;
      FIELD_TYPES[STOP] = DataType.DATE;
      FIELD_TYPES[DATE1] = DataType.DATE;
      FIELD_TYPES[DATE2] = DataType.DATE;
      FIELD_TYPES[DATE3] = DataType.DATE;
      FIELD_TYPES[DATE4] = DataType.DATE;
      FIELD_TYPES[DATE5] = DataType.DATE;
      FIELD_TYPES[DATE6] = DataType.DATE;
      FIELD_TYPES[DATE7] = DataType.DATE;
      FIELD_TYPES[DATE8] = DataType.DATE;
      FIELD_TYPES[DATE9] = DataType.DATE;
      FIELD_TYPES[DATE10] = DataType.DATE;
      FIELD_TYPES[START6] = DataType.DATE;
      FIELD_TYPES[START7] = DataType.DATE;
      FIELD_TYPES[START8] = DataType.DATE;
      FIELD_TYPES[START9] = DataType.DATE;
      FIELD_TYPES[START10] = DataType.DATE;
      FIELD_TYPES[FINISH6] = DataType.DATE;
      FIELD_TYPES[FINISH7] = DataType.DATE;
      FIELD_TYPES[FINISH8] = DataType.DATE;
      FIELD_TYPES[FINISH9] = DataType.DATE;
      FIELD_TYPES[FINISH10] = DataType.DATE;

      FIELD_TYPES[PERCENTAGE_COMPLETE] = DataType.PERCENTAGE;
      FIELD_TYPES[PERCENTAGE_WORK_COMPLETE] = DataType.PERCENTAGE;

      FIELD_TYPES[CONSTRAINT_TYPE] = DataType.CONSTRAINT;

      FIELD_TYPES[ACTUAL_DURATION] = DataType.DURATION;
      FIELD_TYPES[ACTUAL_WORK] = DataType.DURATION;
      FIELD_TYPES[BASELINE_DURATION] = DataType.DURATION;
      FIELD_TYPES[BASELINE_WORK] = DataType.DURATION;
      FIELD_TYPES[DELAY] = DataType.DURATION;
      FIELD_TYPES[DURATION] = DataType.DURATION;
      FIELD_TYPES[DURATION1] = DataType.DURATION;
      FIELD_TYPES[DURATION2] = DataType.DURATION;
      FIELD_TYPES[DURATION3] = DataType.DURATION;
      FIELD_TYPES[DURATION_VARIANCE] = DataType.DURATION;
      FIELD_TYPES[FINISH_VARIANCE] = DataType.DURATION;
      FIELD_TYPES[FREE_SLACK] = DataType.DURATION;
      FIELD_TYPES[REMAINING_DURATION] = DataType.DURATION;
      FIELD_TYPES[REMAINING_WORK] = DataType.DURATION;
      FIELD_TYPES[START_VARIANCE] = DataType.DURATION;
      FIELD_TYPES[TOTAL_SLACK] = DataType.DURATION;
      FIELD_TYPES[WORK] = DataType.DURATION;
      FIELD_TYPES[WORK_VARIANCE] = DataType.DURATION;
      FIELD_TYPES[DURATION4] = DataType.DURATION;
      FIELD_TYPES[DURATION5] = DataType.DURATION;
      FIELD_TYPES[DURATION6] = DataType.DURATION;
      FIELD_TYPES[DURATION7] = DataType.DURATION;
      FIELD_TYPES[DURATION8] = DataType.DURATION;
      FIELD_TYPES[DURATION9] = DataType.DURATION;
      FIELD_TYPES[DURATION10] = DataType.DURATION;

      FIELD_TYPES[PRIORITY] = DataType.PRIORITY;

      FIELD_TYPES[PREDECESSORS] = DataType.RELATION_LIST;
      FIELD_TYPES[SUCCESSORS] = DataType.RELATION_LIST;
      FIELD_TYPES[UNIQUE_ID_PREDECESSORS] = DataType.RELATION_LIST;
      FIELD_TYPES[UNIQUE_ID_SUCCESSORS] = DataType.RELATION_LIST;
   }
}
