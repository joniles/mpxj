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

package com.tapsterrock.mpx;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Date;


/**
 * This class represents a task record from an MPX file.
 */
public final class Task extends MPXRecord implements Comparable, ExtendedAttributeContainer
{
   /**
    * Default constructor.
    *
    * @param file Parent file to which this record belongs.
    * @param parent Parent task
    */
   Task (MPXFile file, Task parent)
   {
      super(file, MAX_FIELDS);

      m_model = getParentFile().getTaskModel();

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
         setOutlineLevel(parent.getOutlineLevelValue() + 1);
      }

      if (file.getAutoTaskUniqueID() == true)
      {
         setUniqueID(file.getTaskUniqueID());
      }

      if (file.getAutoTaskID() == true)
      {
         setID(file.getTaskID());
      }
   }

   /**
    * This constructor populates an instance of the Task class
    * using values read in from an MPXFile record.
    *
    * @param file parent MPX file
    * @param record record from MPX file
    * @throws MPXException normally thrown for paring errors
    */
   Task (MPXFile file, Record record)
      throws MPXException
   {
      super(file, MAX_FIELDS);

      String falseText = LocaleData.getString(getParentFile().getLocale(), LocaleData.NO);

      m_model = getParentFile().getTaskModel();

      int x = 0;
      String field;

      int i = 0;
      int length = record.getLength();
      int[] model = m_model.getModel();

      while (i < length)
      {
         x = model[i];

         if (x == -1)
         {
            break;
         }

         field = record.getString(i++);

         if ((field == null) || (field.length() == 0))
         {
            continue;
         }

         switch (x)
         {
            case PREDECESSORS:
            case SUCCESSORS:
            case UNIQUE_ID_PREDECESSORS:
            case UNIQUE_ID_SUCCESSORS:
            {
               set(x, new RelationList(field, getParentFile().getDurationDecimalFormat(), getParentFile().getLocale()));
               break;
            }

            case PERCENTAGE_COMPLETE:
            case PERCENTAGE_WORK_COMPLETE:
            {
               set(x, new MPXPercentage(field, getParentFile().getPercentageDecimalFormat()));
               break;
            }

            case ACTUAL_COST:
            case BASELINE_COST:
            case BCWP:
            case BCWS:
            case COST:
            case COST1:
            case COST2:
            case COST3:
            case COST_VARIANCE:
            case CV:
            case FIXED_COST:
            case REMAINING_COST:
            case SV:
            {
               set(x, new MPXCurrency(getParentFile().getCurrencyFormat(), field));
               break;
            }

            case ACTUAL_DURATION:
            case ACTUAL_WORK:
            case BASELINE_DURATION:
            case BASELINE_WORK:
            case DURATION:
            case DURATION1:
            case DURATION2:
            case DURATION3:
            case DURATION_VARIANCE:
            case FINISH_VARIANCE:
            case FREE_SLACK:
            case REMAINING_DURATION:
            case REMAINING_WORK:
            case START_VARIANCE:
            case TOTAL_SLACK:
            case WORK:
            case WORK_VARIANCE:
            case DELAY:
            {
               set(x, new MPXDuration(field, getParentFile().getDurationDecimalFormat(), getParentFile().getLocale()));
               break;
            }

            case ACTUAL_FINISH:
            case ACTUAL_START:
            case BASELINE_FINISH:
            case BASELINE_START:
            case CONSTRAINT_DATE:
            case CREATE_DATE:
            case EARLY_FINISH:
            case EARLY_START:
            case FINISH:
            case FINISH1:
            case FINISH2:
            case FINISH3:
            case FINISH4:
            case FINISH5:
            case LATE_FINISH:
            case LATE_START:
            case RESUME:
            case RESUME_NO_EARLIER_THAN:
            case START:
            case START1:
            case START2:
            case START3:
            case START4:
            case START5:
            case STOP:
            {
               set(x, getParentFile().getDateTimeFormat().parse(field));
               break;
            }

            case CONFIRMED:
            case CRITICAL:
            case FIXED:
            case FLAG1:
            case FLAG2:
            case FLAG3:
            case FLAG4:
            case FLAG5:
            case FLAG6:
            case FLAG7:
            case FLAG8:
            case FLAG9:
            case FLAG10:
            case HIDE_BAR:
            case LINKED_FIELDS:
            case MARKED:
            case MILESTONE:
            case ROLLUP:
            case SUMMARY:
            case UPDATE_NEEDED:
            {
               set(x, ((field.equalsIgnoreCase(falseText) == true) ? Boolean.FALSE : Boolean.TRUE));
               break;
            }

            case CONSTRAINT_TYPE:
            {
               set(x, ConstraintType.getInstance(getParentFile().getLocale(), field));
               break;
            }

            case OBJECTS:
            case OUTLINE_LEVEL:
            {
               set(x, Integer.valueOf(field));
               break;
            }

            case ID:
            {
               setID(Integer.valueOf(field));
               break;
            }

            case UNIQUE_ID:
            {
               setUniqueID(Integer.valueOf(field));
               break;
            }

            case NUMBER1:
            case NUMBER2:
            case NUMBER3:
            case NUMBER4:
            case NUMBER5:
            {
               set(x, getParentFile().getDecimalFormat().parse(field));
               break;
            }

            case PRIORITY:
            {
               set(x, Priority.getInstance(getParentFile().getLocale(), field));
               break;
            }

            default:
            {
               set(x, field);
               break;
            }
         }
      }

      if (file.getAutoWBS() == true)
      {
         generateWBS(null);
      }

      if (file.getAutoOutlineNumber() == true)
      {
         generateOutlineNumber(null);
      }

      if (file.getAutoOutlineLevel() == true)
      {
         setOutlineLevel(1);
      }

      if (file.getAutoTaskUniqueID() == true)
      {
         setUniqueID(file.getTaskUniqueID());
      }

      if (file.getAutoTaskID() == true)
      {
         setID(file.getTaskID());
      }

      if (getFixedValue() == true)
      {
         setType(TaskType.FIXED_DURATION);
      }
      else
      {
         setType(TaskType.FIXED_UNITS);
      }
   }

   /**
    * This package-access method is used to automatically generate a value
    * for the WBS field of this task.
    *
    * @param parent Parent Task
    */
   void generateWBS (Task parent)
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
   void generateOutlineNumber (Task parent)
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
    * @return TaskNotes object
    */
   public TaskNotes setNotes (String notes)
   {
      if (m_notes == null)
      {
         m_notes = new TaskNotes(getParentFile());
      }

      m_notes.setNotes(notes);

      return (m_notes);
   }

   /**
    * This method is used to add notes to the current task.
    * The data to populate the note comes from an MPX file.
    *
    * @param record data from an MPX file
    * @return TaskNotes object
    * @throws MPXException if maximum number of task notes is exceeded
    */
   TaskNotes addTaskNotes (Record record)
      throws MPXException
   {
      if (m_notes != null)
      {
         throw new MPXException(MPXException.MAXIMUM_RECORDS);
      }

      m_notes = new TaskNotes(getParentFile(), record);

      return (m_notes);
   }

   /**
    * This method allows nested tasks to be added, with the WBS being
    * completed automatically.
    *
    * @return new task
    * @throws MPXException normally thrown on parse error
    */
   public Task addTask ()
      throws MPXException
   {
      MPXFile parent = getParentFile();

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
    * @throws MPXException Thrown if an invalid outline level is supplied.
    */
   void addChildTask (Task child, int childOutlineLevel)
      throws MPXException
   {
      int outlineLevel = getOutlineLevelValue();

      if ((outlineLevel + 1) == childOutlineLevel)
      {
         m_children.add(child);
      }
      else
      {
         if (m_children.isEmpty() == false)
         {
            ((Task)m_children.getLast()).addChildTask(child, childOutlineLevel);
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
    * @throws MPXException thrown if more than one one recurring task is added
    */
   public RecurringTask addRecurringTask ()
      throws MPXException
   {
      if (m_recurringTask != null)
      {
         throw new MPXException(MPXException.MAXIMUM_RECORDS);
      }

      m_recurringTask = new RecurringTask(getParentFile());

      return (m_recurringTask);
   }

   /**
    * This method allows recurring task details to be added to the
    * current task. The data used to populate the recurring task
    * object is taken from a record in an MPX file.
    *
    * @param record data from MPX file record
    * @return RecurringTask object
    * @throws MPXException thrown if more than one one recurring task is added
    */
   RecurringTask addRecurringTask (Record record)
      throws MPXException
   {
      if (m_recurringTask != null)
      {
         throw new MPXException(MPXException.MAXIMUM_RECORDS);
      }

      m_recurringTask = new RecurringTask(getParentFile(), record);

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
    * @throws MPXException
    */
   public ResourceAssignment addResourceAssignment (Resource resource)
      throws MPXException
   {
      Iterator iter = m_assignments.iterator();
      ResourceAssignment assignment = null;
      int resourceUniqueID = resource.getUniqueIDValue();
      int uniqueID;

      while (iter.hasNext() == true)
      {
         assignment = (ResourceAssignment)iter.next();
         uniqueID = assignment.getResourceUniqueIDValue();

         if (uniqueID == resourceUniqueID)
         {
            break;
         }
         else
         {
            assignment = null;
         }
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
         
         resource.addAssignment(assignment);
      }

      return (assignment);
   }

   /**
    * This method allows a resource assignment to be added to the
    * current task. The data for the resource assignment is derived from
    * an MPX file record.
    *
    * @param record data from MPX file record
    * @return ResourceAssignment object
    * @throws MPXException
    */
   ResourceAssignment addResourceAssignment (Record record)
      throws MPXException
   {
      ResourceAssignment assignment = new ResourceAssignment(getParentFile(), record, this);

      m_assignments.add(assignment);
      
      Resource resource = assignment.getResource();
      if (resource != null)
      {
         resource.addAssignment(assignment);
      }
      
      return (assignment);
   }

   /**
    * This method allows the list of resource assignments for this
    * task to be retrieved.
    *
    * @return list of resource assignments
    */
   public LinkedList getResourceAssignments ()
   {
      return (m_assignments);
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
      RelationList list = (RelationList)get(PREDECESSORS);

      if (list == null)
      {
         list = new RelationList();
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

            if (rel.getTaskIDValue() == task.getIDValue())
            {
               break;
            }
            else
            {
               rel = null;
            }
         }
      }

      //
      // If necessary, create a new relationship
      //
      if (rel == null)
      {
         rel = new Relation();

         if (task != null)
         {
            rel.setTaskIDValue(task.getIDValue());
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
      RelationList list = (RelationList)get(UNIQUE_ID_PREDECESSORS);

      if (list == null)
      {
         list = new RelationList();
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

            if (rel.getTaskIDValue() == task.getUniqueIDValue())
            {
               break;
            }
            else
            {
               rel = null;
            }
         }
      }

      //
      // If necessary, create a new relationship
      //
      if (rel == null)
      {
         rel = new Relation();

         if (task != null)
         {
            rel.setTaskIDValue(task.getUniqueIDValue());
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
      RelationList list = (RelationList)get(SUCCESSORS);

      if (list == null)
      {
         list = new RelationList();
         set(SUCCESSORS, list);
      }

      Relation rel = new Relation();

      if (task != null)
      {
         rel.setTaskIDValue(task.getIDValue());
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
      RelationList list = (RelationList)get(UNIQUE_ID_SUCCESSORS);

      if (list == null)
      {
         list = new RelationList();
         set(UNIQUE_ID_SUCCESSORS, list);
      }

      Relation rel = new Relation();

      if (task != null)
      {
         rel.setTaskIDValue(task.getUniqueIDValue());
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
      m_model.add(field);
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
   private void set (int field, int val)
   {
      m_model.add(field);
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
      m_model.add(field);
      put(field, val);
   }

   /**
    * This method is used to set the value of a date field in the task,
    * and also to ensure that the field exists in the task model
    * record.
    *
    * @param field field to be added or updated.
    * @param val new value for field.
    */
   public void setDate (int field, Date val)
   {
      m_model.add(field);
      putDate(field, val);
   }

   /**
    * This method is used to set the value of a percentage field in the task,
    * and also to ensure that the field exists in the task model
    * record.
    *
    * @param field field to be added or updated.
    * @param val new value for field.
    */
   private void setPercentage (int field, Number val)
   {
      m_model.add(field);
      putPercentage(field, val);
   }

   /**
    * This method is used to set the value of a currency field in the task,
    * and also to ensure that the field exists in the task model
    * record.
    *
    * @param field field to be added or updated.
    * @param val new value for field.
    */
   public void setCurrency (int field, Number val)
   {
      m_model.add(field);
      putCurrency(field, val);
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
   public void setPercentageComplete (double val)
   {
      setPercentage(PERCENTAGE_COMPLETE, new MPXPercentage(val));
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
      setPercentage(PERCENTAGE_COMPLETE, val);
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
   public void setPercentageWorkComplete (double val)
   {
      setPercentage(PERCENTAGE_WORK_COMPLETE, new MPXPercentage(val));
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
      setPercentage(PERCENTAGE_WORK_COMPLETE, val);
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
      setCurrency(ACTUAL_COST, val);
   }

   /**
    * The Actual Duration field shows the span of actual working time for a
    * task so far,
    * based on the scheduled duration and current remaining work or
    * completion percentage.
    *
    * @param val value to be set
    */
   public void setActualDuration (MPXDuration val)
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
      setDate(ACTUAL_FINISH, val);
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
      setDate(ACTUAL_START, val);
   }

   /**
    * The Actual Work field shows the amount of work that has already been
    * done by the
    * resources assigned to a task.
    * @param val value to be set
    */
   public void setActualWork (MPXDuration val)
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
      setCurrency(BASELINE_COST, val);
   }

   /**
    * The Baseline Duration field shows the original span of time planned to
    * complete a task.
    *
    * @param val duration
    */
   public void setBaselineDuration (MPXDuration val)
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
      setDate(BASELINE_FINISH, val);
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
      setDate(BASELINE_START, val);
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
   public void setBaselineWork (MPXDuration val)
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
      setCurrency(BCWP, val);
   }

   /**
    * The BCWS (budgeted cost of work scheduled) field contains the cumulative
    * timephased baseline costs up to the status date or today's date.
    *
    * @param val the amount to set
    */
   public void setBCWS (Number val)
   {
      setCurrency(BCWS, val);
   }

   /**
    * The Confirmed field indicates whether all resources assigned to a task have
    * accepted or rejected the task assignment in response to a TeamAssign message
    * regarding their assignments.
    *
    * @param val boolean value
    */
   public void setConfirmed (Boolean val)
   {
      set(CONFIRMED, val);
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
      setDate(CONSTRAINT_DATE, val);
   }

   /**
    * Private method for dealing with string parameters from File
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
      setCurrency(COST, val);
   }

   /**
    * The Cost1-10 fields show any custom task cost information you want to enter
    * in your project.
    *
    * @param val amount
    */
   public void setCost1 (Number val)
   {
      setCurrency(COST1, val);
   }

   /**
    * The Cost1-10 fields show any custom task cost information you want to
    * enter in your project.
    *
    * @param val amount
    */
   public void setCost2 (Number val)
   {
      setCurrency(COST2, val);
   }

   /**
    * The Cost1-10 fields show any custom task cost information you want to
    * enter in your project.
    *
    * @param val amount
    */
   public void setCost3 (Number val)
   {
      setCurrency(COST3, val);
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
      setCurrency(COST_VARIANCE, val);
   }

   /**
    * The Created field contains the date and time when a task was
    * added to the project.
    *
    * @param val date
    */
   public void setCreateDate (Date val)
   {
      setDate(CREATE_DATE, val);
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
    * The Critical field indicates whether a task has any room in the
    * schedule to slip,
    * or if a task is on the critical path. The Critical field contains
    * Yes if the task
    * is critical and No if the task is not critical.
    *
    * @param val whether task is critical or not
    */
   public void setCritical (Boolean val)
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
   public void setCV (double val)
   {
      set(CV, new MPXCurrency(getParentFile().getCurrencyFormat(), val));
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
      setCurrency(CV, val);
   }

   /**
    * Set amount of delay as elapsed real time
    *
    * @param val elapsed time
    */
   public void setDelay (MPXDuration val)
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
   public void setDuration (MPXDuration val)
   {
      set(DURATION, val);
   }

   /**
    * User defined duration field.
    *
    * @param duration Duration value
    */
   public void setDuration1 (MPXDuration duration)
   {
      set(DURATION1, duration);
   }

   /**
    * User defined duration field.
    *
    * @param duration Duration value
    */
   public void setDuration2 (MPXDuration duration)
   {
      set(DURATION2, duration);
   }

   /**
    * User defined duration field.
    *
    * @param duration Duration value
    */
   public void setDuration3 (MPXDuration duration)
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
   public void setDurationVariance (MPXDuration duration)
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
      setDate(EARLY_FINISH, date);
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
      setDate(EARLY_START, date);
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
      setDate(FINISH, date);
   }

   /**
    * User defined finish date field.
    *
    * @param date Date value
    */
   public void setFinish1 (Date date)
   {
      setDate(FINISH1, date);
   }

   /**
    * User defined finish date field.
    *
    * @param date Date value
    */
   public void setFinish2 (Date date)
   {
      setDate(FINISH2, date);
   }

   /**
    * User defined finish date field.
    *
    * @param date Date value
    */
   public void setFinish3 (Date date)
   {
      setDate(FINISH3, date);
   }

   /**
    * User defined finish date field.
    *
    * @param date Date value
    */
   public void setFinish4 (Date date)
   {
      setDate(FINISH4, date);
   }

   /**
    * User defined finish date field.
    *
    * @param date Date value
    */
   public void setFinish5 (Date date)
   {
      setDate(FINISH5, date);
   }

   /**
    * The Finish Variance field contains the amount of time that represents the
    * difference between a task's baseline finish date and its forecast
    * or actual finish date.
    *
    * @param duration duration value
    */
   public void setFinishVariance (MPXDuration duration)
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
    * Despite the name, this flag sets the task type. If the suppied value is
    * false, the task type shown in MS Project will be set to fixed units. If
    * the value is true, the task type will be set to fixed duration.
    *
    * @param val value to be set
    */
   public void setFixed (Boolean val)
   {
      set(FIXED, val);
   }

   /**
    * The Fixed Cost field shows any task expense that is not associated
    * with a resource cost.
    *
    * @param val amount
    */
   public void setFixedCost (double val)
   {
      set(FIXED_COST, new MPXCurrency(getParentFile().getCurrencyFormat(), val));
   }

   /**
    * The Fixed Cost field shows any task expense that is not associated
    * with a resource cost.
    *
    * @param val amount
    */
   public void setFixedCost (Number val)
   {
      setCurrency(FIXED_COST, val);
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
   public void setFlag1 (Boolean val)
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
   public void setFlag2 (Boolean val)
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
   public void setFlag3 (Boolean val)
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
   public void setFlag4 (Boolean val)
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
   public void setFlag5 (Boolean val)
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
   public void setFlag6 (Boolean val)
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
   public void setFlag7 (Boolean val)
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
   public void setFlag8 (Boolean val)
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
   public void setFlag9 (Boolean val)
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
    * User defined flag field.
    *
    * @param val boolean value
    */
   public void setFlag10 (Boolean val)
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
   public void setFreeSlack (MPXDuration duration)
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
    * The Hide Bar flag indicates whether the Gantt bars and Calendar bars
    * for a task are hidden when this project's data is displayed in MS Project.
    *
    * @param flag boolean value
    */
   public void setHideBar (Boolean flag)
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
   public void setID (int val)
   {
      setID(new Integer(val));
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
      MPXFile parent = getParentFile();
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
      setDate(LATE_FINISH, date);
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
      setDate(LATE_START, date);
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
    * The Linked Fields field indicates whether there are OLE links to the task,
    * either from elsewhere in the active project, another Microsoft Project
    * file, or from another program.
    *
    * @param flag boolean value
    */
   public void setLinkedFields (Boolean flag)
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
    * This is a user defined field used to mark a task for some form of
    * additional action.
    *
    * @param flag boolean value
    */
   public void setMarked (Boolean flag)
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
    * The Milestone field indicates whether a task is a milestone.
    *
    * @param flag boolean value
    */
   public void setMilestone (Boolean flag)
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
   public void setNumber1 (Double val)
   {
      set(NUMBER1, val);
   }

   /**
    * Sets a numeric value.
    *
    * @param val Numeric value
    */
   public void setNumber2 (Double val)
   {
      set(NUMBER2, val);
   }

   /**
    * Sets a numeric value.
    *
    * @param val Numeric value
    */
   public void setNumber3 (Double val)
   {
      set(NUMBER3, val);
   }

   /**
    * Sets a numeric value.
    *
    * @param val Numeric value
    */
   public void setNumber4 (Double val)
   {
      set(NUMBER4, val);
   }

   /**
    * Sets a numeric value.
    *
    * @param val Numeric value
    */
   public void setNumber5 (Double val)
   {
      set(NUMBER5, val);
   }

   /**
    * The Objects field contains the number of objects attached to a task.
    *
    * @param val - integer value
    */
   public void setObjects (int val)
   {
      set(OBJECTS, val);
   }

   /**
    * The Outline Level field contains the number that indicates the level of
    * the task in the project outline hierarchy.
    *
    * @param val - int
    */
   public void setOutlineLevel (int val)
   {
      set(OUTLINE_LEVEL, val);
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
   public void setPredecessors (RelationList list)
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
      setCurrency(REMAINING_COST, val);
   }

   /**
    * The Remaining Duration field shows the amount of time required to complete
    * the unfinished portion of a task.
    *
    * @param val - duration.
    */
   public void setRemainingDuration (MPXDuration val)
   {
      set(REMAINING_DURATION, val);
   }

   /**
    * The Remaining Work field shows the amount of time, or person-hours,
    * still required by all assigned resources to complete a task.
    * @param val  - duration
    */
   public void setRemainingWork (MPXDuration val)
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
      setDate(RESUME, val);
   }

   /**
    * No help info. Earliest possible resume date?
    * @param val - Date
    */
   public void setResumeNoEarlierThan (Date val)
   {
      setDate(RESUME_NO_EARLIER_THAN, val);
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
    * For subtasks, the Rollup field indicates whether information on the subtask
    * Gantt bars will be rolled up to the summary task bar. For summary tasks, the
    * Rollup field indicates whether the summary task bar displays rolled up bars.
    * You must have the Rollup field for summary tasks set to Yes for any subtasks
    * to roll up to them.
    *
    * @param val - boolean
    */
   public void setRollup (Boolean val)
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
      setDate(START, val);
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
      setDate(START1, val);
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
      setDate(START2, val);
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
      setDate(START3, val);
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
      setDate(START4, val);
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
      setDate(START5, val);
   }

   /**
    * The Start Variance field contains the amount of time that represents the
    * difference between a task's baseline start date and its currently
    * scheduled start date.
    *
    * @param val - duration
    */
   public void setStartVariance (MPXDuration val)
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
      setDate(STOP, val);
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
   public void setSuccessors (RelationList list)
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
    * The Summary field indicates whether a task is a summary task.
    *
    * @param val - boolean
    */
   public void setSummary (Boolean val)
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
      setCurrency(SV, val);
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
   public void setTotalSlack (MPXDuration val)
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
   public void setUniqueID (int val)
   {
      setUniqueID(new Integer(val));
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
      MPXFile parent = getParentFile();
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
   public void setUniqueIDPredecessors (RelationList list)
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
   public void setUniqueIDSuccessors (RelationList list)
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
    * The Update Needed field indicates whether a TeamUpdate message should
    * be sent to the assigned resources because of changes to the start date,
    * finish date, or resource reassignments of the task.
    *
    * @param val - boolean
    */
   public void setUpdateNeeded (Boolean val)
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
   public void setWork (MPXDuration val)
   {
      set(WORK, val);
   }

   /**
    * The Work Variance field contains the difference between a task's baseline
    * work and the currently scheduled work.
    *
    * @param val - duration
    */
   public void setWorkVariance (MPXDuration val)
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
   public double getPercentageCompleteValue ()
   {
      return (getDoubleValue(PERCENTAGE_COMPLETE));
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
   public double getPercentageWorkCompleteValue ()
   {
      return (getDoubleValue(PERCENTAGE_WORK_COMPLETE));
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
   public MPXDuration getActualDuration ()
   {
      return ((MPXDuration)get(ACTUAL_DURATION));
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
   public MPXDuration getActualWork ()
   {
      return ((MPXDuration)get(ACTUAL_WORK));
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
   public MPXDuration getBaselineDuration ()
   {
      return ((MPXDuration)get(BASELINE_DURATION));
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
    * @return MPXDuration
    */
   public MPXDuration getBaselineWork ()
   {
      return ((MPXDuration)get(BASELINE_WORK));
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
   public double getBCWPValue ()
   {
      return (getDoubleValue(BCWP));
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
   public double getBCWSValue ()
   {
      return (getDoubleValue(BCWS));
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
   public boolean getConfirmedValue ()
   {
      return (getBooleanValue(CONFIRMED));
   }

   /**
    * The Confirmed field indicates whether all resources assigned to a task
    * have accepted or rejected the task assignment in response to a TeamAssign
    * message regarding their assignments.
    *
    * @return boolean
    */
   public Boolean getConfirmed ()
   {
      return ((Boolean)get(CONFIRMED));
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
   public int getConstraintTypeValue ()
   {
      int result;
      ConstraintType type = (ConstraintType)get(CONSTRAINT_TYPE);

      if (type == null)
      {
         result = ConstraintType.AS_SOON_AS_POSSIBLE;
      }
      else
      {
         result = type.getType();
      }

      return (result);
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
   public boolean getCriticalValue ()
   {
      return (getBooleanValue(CRITICAL));
   }

   /**
    * The Critical field indicates whether a task has any room in the schedule
    * to slip, or if a task is on the critical path. The Critical field contains
    * Yes if the task is critical and No if the task is not critical.
    *
    * @return boolean
    */
   public Boolean getCritical ()
   {
      return ((Boolean)get(CRITICAL));
   }

   /**
    * The CV (earned value cost variance) field shows the difference between
    * how much it should have cost to achieve the current level of completion
    * on the task, and how much it has actually cost to achieve the current
    * level of completion up to the status date or today's date.
    *
    * @return sum of earned value cost variance
    */
   public double getCVValue ()
   {
      return (getDoubleValue(CV));
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
    * @return MPXDuration
    */
   public MPXDuration getDelay ()
   {
      return ((MPXDuration)get(DELAY));
   }

   /**
    * The Duration field is the total span of active working time for a task.
    * This is generally the amount of time from the start to the finish of a task.
    * The default for new tasks is 1 day (1d).
    *
    * @return MPXDuration
    */
   public MPXDuration getDuration ()
   {
      return ((MPXDuration)get(DURATION));
   }

   /**
    * The Duration1 through Duration10 fields are custom fields that show any
    * specialized task duration information you want to enter and store separately
    * in your project.
    *
    * @return MPXDuration
    */
   public MPXDuration getDuration1 ()
   {
      return (MPXDuration)get(DURATION1);
   }

   /**
    * The Duration1 through Duration10 fields are custom fields that show any
    * specialized task duration information you want to enter and store separately
    * in your project.
    *
    * @return MPXDuration
    */
   public MPXDuration getDuration2 ()
   {
      return ((MPXDuration)get(DURATION2));
   }

   /**
    * The Duration1 through Duration10 fields are custom fields that show any
    * specialized task duration information you want to enter and store separately
    * in your project.
    *
    * @return MPXDuration
    */
   public MPXDuration getDuration3 ()
   {
      return ((MPXDuration)get(DURATION3));
   }

   /**
    * The Duration Variance field contains the difference between the
    * baseline duration of a task and the total duration (current estimate)
    * of a task.
    *
    * @return MPXDuration
    */
   public MPXDuration getDurationVariance ()
   {
      return ((MPXDuration)get(DURATION_VARIANCE));
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
    * This field will be ignored on input into MS Project
    *
    * @return String
    */
   public MPXDuration getFinishVariance ()
   {
      return ((MPXDuration)get(FINISH_VARIANCE));
   }

   /**
    * Despite the name, this flag represents the task type. If the value is
    * false, the task type shown in MS Project will be fixed units. If
    * the value is true, the task type will be fixed duration.
    *
    * @return boolean
    */
   public boolean getFixedValue ()
   {
      return (getBooleanValue(FIXED));
   }

   /**
    * Despite the name, this flag represents the task type. If the value is
    * false, the task type shown in MS Project will be fixed units. If
    * the value is true, the task type will be fixed duration.
    *
    * @return boolean
    */
   public Boolean getFixed ()
   {
      return ((Boolean)get(FIXED));
   }

   /**
    * The Fixed Cost field shows any task expense that is not associated
    * with a resource cost.
    *
    * @return currenct amount as float
    */
   public double getFixedCostValue ()
   {
      return (getDoubleValue(FIXED_COST));
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
   public boolean getFlag1Value ()
   {
      return (getBooleanValue(FLAG1));
   }

   /**
    * The Flag1-20 fields indicate whether a task is marked for further
    * action or identification of some kind. To mark a task, click Yes
    * in a Flag field. If you don't want a task marked, click No.
    *
    * @return boolean
    */
   public Boolean getFlag1 ()
   {
      return ((Boolean)get(FLAG1));
   }

   /**
    * The Flag1-20 fields indicate whether a task is marked for further
    * action or identification of some kind. To mark a task, click Yes
    * in a Flag field. If you don't want a task marked, click No.
    *
    * @return boolean
    */
   public boolean getFlag2Value ()
   {
      return (getBooleanValue(FLAG2));
   }

   /**
    * The Flag1-20 fields indicate whether a task is marked for further
    * action or identification of some kind. To mark a task, click Yes
    * in a Flag field. If you don't want a task marked, click No.
    *
    * @return boolean
    */
   public Boolean getFlag2 ()
   {
      return ((Boolean)get(FLAG2));
   }

   /**
    * The Flag1-20 fields indicate whether a task is marked for further
    * action or identification of some kind. To mark a task, click Yes
    * in a Flag field. If you don't want a task marked, click No.
    *
    * @return boolean
    */
   public boolean getFlag3Value ()
   {
      return (getBooleanValue(FLAG3));
   }

   /**
    * The Flag1-20 fields indicate whether a task is marked for further
    * action or identification of some kind. To mark a task, click Yes
    * in a Flag field. If you don't want a task marked, click No.
    *
    * @return boolean
    */
   public Boolean getFlag3 ()
   {
      return ((Boolean)get(FLAG3));
   }

   /**
    * The Flag1-20 fields indicate whether a task is marked for further
    * action or identification of some kind. To mark a task, click Yes
    * in a Flag field. If you don't want a task marked, click No.
    *
    * @return boolean
    */
   public boolean getFlag4Value ()
   {
      return (getBooleanValue(FLAG4));
   }

   /**
    * The Flag1-20 fields indicate whether a task is marked for further
    * action or identification of some kind. To mark a task, click Yes
    * in a Flag field. If you don't want a task marked, click No.
    *
    * @return boolean
    */
   public Boolean getFlag4 ()
   {
      return ((Boolean)get(FLAG4));
   }

   /**
    * The Flag1-20 fields indicate whether a task is marked for further
    * action or identification of some kind. To mark a task, click Yes
    * in a Flag field. If you don't want a task marked, click No.
    *
    * @return boolean
    */
   public boolean getFlag5Value ()
   {
      return (getBooleanValue(FLAG5));
   }

   /**
    * The Flag1-20 fields indicate whether a task is marked for further
    * action or identification of some kind. To mark a task, click Yes
    * in a Flag field. If you don't want a task marked, click No.
    *
    * @return boolean
    */
   public Boolean getFlag5 ()
   {
      return ((Boolean)get(FLAG5));
   }

   /**
    * The Flag1-20 fields indicate whether a task is marked for further
    * action or identification of some kind. To mark a task, click Yes
    * in a Flag field. If you don't want a task marked, click No.
    *
    * @return boolean
    */
   public boolean getFlag6Value ()
   {
      return (getBooleanValue(FLAG6));
   }

   /**
    * The Flag1-20 fields indicate whether a task is marked for further
    * action or identification of some kind. To mark a task, click Yes
    * in a Flag field. If you don't want a task marked, click No.
    *
    * @return boolean
    */
   public Boolean getFlag6 ()
   {
      return ((Boolean)get(FLAG6));
   }

   /**
    * The Flag1-20 fields indicate whether a task is marked for further
    * action or identification of some kind. To mark a task, click Yes
    * in a Flag field. If you don't want a task marked, click No.
    *
    * @return boolean
    */
   public boolean getFlag7Value ()
   {
      return (getBooleanValue(FLAG7));
   }

   /**
    * The Flag1-20 fields indicate whether a task is marked for further
    * action or identification of some kind. To mark a task, click Yes
    * in a Flag field. If you don't want a task marked, click No.
    *
    * @return boolean
    */
   public Boolean getFlag7 ()
   {
      return ((Boolean)get(FLAG7));
   }

   /**
    * The Flag1-20 fields indicate whether a task is marked for further
    * action or identification of some kind. To mark a task, click Yes
    * in a Flag field. If you don't want a task marked, click No.
    *
    * @return boolean
    */
   public boolean getFlag8Value ()
   {
      return (getBooleanValue(FLAG8));
   }

   /**
    * The Flag1-20 fields indicate whether a task is marked for further
    * action or identification of some kind. To mark a task, click Yes
    * in a Flag field. If you don't want a task marked, click No.
    *
    * @return boolean
    */
   public Boolean getFlag8 ()
   {
      return ((Boolean)get(FLAG8));
   }

   /**
    * The Flag1-20 fields indicate whether a task is marked for further
    * action or identification of some kind. To mark a task, click Yes
    * in a Flag field. If you don't want a task marked, click No.
    *
    * @return boolean
    */
   public boolean getFlag9Value ()
   {
      return (getBooleanValue(FLAG9));
   }

   /**
    * The Flag1-20 fields indicate whether a task is marked for further
    * action or identification of some kind. To mark a task, click Yes
    * in a Flag field. If you don't want a task marked, click No.
    *
    * @return boolean
    */
   public Boolean getFlag9 ()
   {
      return ((Boolean)get(FLAG9));
   }

   /**
    * The Flag1-20 fields indicate whether a task is marked for further
    * action or identification of some kind. To mark a task, click Yes
    * in a Flag field. If you don't want a task marked, click No.
    *
    * @return boolean
    */
   public boolean getFlag10Value ()
   {
      return (getBooleanValue(FLAG10));
   }

   /**
    * The Flag1-20 fields indicate whether a task is marked for further
    * action or identification of some kind. To mark a task, click Yes
    * in a Flag field. If you don't want a task marked, click No.
    *
    * @return boolean
    */
   public Boolean getFlag10 ()
   {
      return ((Boolean)get(FLAG10));
   }

   /**
    * The Free Slack field contains the amount of time that a task can be
    * delayed without delaying any successor tasks. If the task has no
    * successors, free slack is the amount of time that a task can be
    * delayed without delaying the entire project's finish date.
    *
    * @return MPXDuration
    */
   public MPXDuration getFreeSlack ()
   {
      return ((MPXDuration)get(FREE_SLACK));
   }

   /**
    * The Hide Bar field indicates whether the Gantt bars and Calendar bars
    * for a task are hidden. Click Yes in the Hide Bar field to hide the
    * bar for the task. Click No in the Hide Bar field to show the bar
    * for the task.
    *
    * @return boolean
    */
   public boolean getHideBarValue ()
   {
      return (getBooleanValue(HIDE_BAR));
   }

   /**
    * The Hide Bar field indicates whether the Gantt bars and Calendar bars
    * for a task are hidden. Click Yes in the Hide Bar field to hide the
    * bar for the task. Click No in the Hide Bar field to show the bar
    * for the task.
    *
    * @return boolean
    */
   public Boolean getHideBar ()
   {
      return ((Boolean)get(HIDE_BAR));
   }

   /**
    * The ID field contains the identifier number that Microsoft Project
    * automatically assigns to each task as you add it to the project.
    * The ID indicates the position of a task with respect to the other tasks.
    *
    * @return the task ID
    */
   public int getIDValue ()
   {
      return (getIntValue(ID));
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
   public boolean getLinkedFieldsValue ()
   {
      return (getBooleanValue(LINKED_FIELDS));
   }

   /**
    * The Linked Fields field indicates whether there are OLE links to the task,
    * either from elsewhere in the active project, another Microsoft Project file,
    * or from another program.
    *
    * @return boolean
    */
   public Boolean getLinkedFields ()
   {
      return ((Boolean)get(LINKED_FIELDS));
   }

   /**
    * The Marked field indicates whether a task is marked for further action or
    * identification of some kind. To mark a task, click Yes in the Marked field.
    * If you don't want a task marked, click No.
    *
    * @return true for marked
    */
   public boolean getMarkedValue ()
   {
      return (getBooleanValue(MARKED));
   }

   /**
    * The Marked field indicates whether a task is marked for further action or
    * identification of some kind. To mark a task, click Yes in the Marked field.
    * If you don't want a task marked, click No.
    *
    * @return true for marked
    */
   public Boolean getMarked ()
   {
      return ((Boolean)get(MARKED));
   }

   /**
    * The Milestone field indicates whether a task is a milestone
    *
    * @return boolean
    */
   public boolean getMilestoneValue ()
   {
      return (getBooleanValue(MILESTONE));
   }

   /**
    * The Milestone field indicates whether a task is a milestone
    *
    * @return boolean
    */
   public Boolean getMilestone ()
   {
      return ((Boolean)get(MILESTONE));
   }

   /**
    * @return name of task
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
      String result;

      if (m_notes != null)
      {
         result = m_notes.getNotes();
      }
      else
      {
         result = "";
      }

      return (result);
   }

   /**
    * Retrieves a numeric value.
    *
    * @return Numeric value
    */
   public double getNumber1Value ()
   {
      return (getDoubleValue(NUMBER1));
   }

   /**
    * Retrieves a numeric value.
    *
    * @return Numeric value
    */
   public Double getNumber1 ()
   {
      return ((Double)get(NUMBER1));
   }

   /**
    * Retrieves a numeric value.
    *
    * @return Numeric value
    */
   public double getNumber2Value ()
   {
      return (getDoubleValue(NUMBER2));
   }

   /**
    * Retrieves a numeric value.
    *
    * @return Numeric value
    */
   public Double getNumber2 ()
   {
      return ((Double)get(NUMBER2));
   }

   /**
    * Retrieves a numeric value.
    *
    * @return Numeric value
    */
   public double getNumber3Value ()
   {
      return (getDoubleValue(NUMBER3));
   }

   /**
    * Retrieves a numeric value.
    *
    * @return Numeric value
    */
   public Double getNumber3 ()
   {
      return ((Double)get(NUMBER3));
   }

   /**
    * Retrieves a numeric value.
    *
    * @return Numeric value
    */
   public double getNumber4Value ()
   {
      return (getDoubleValue(NUMBER4));
   }

   /**
    * Retrieves a numeric value.
    *
    * @return Numeric value
    */
   public Double getNumber4 ()
   {
      return ((Double)get(NUMBER4));
   }

   /**
    * Retrieves a numeric value.
    *
    * @return Numeric value
    */
   public double getNumber5Value ()
   {
      return (getDoubleValue(NUMBER5));
   }

   /**
    * Retrieves a numeric value.
    *
    * @return Numeric value
    */
   public Double getNumber5 ()
   {
      return ((Double)get(NUMBER5));
   }

   /**
    * The Objects field contains the number of objects attached to a task.
    * Microsoft Project counts the number of objects linked or embedded to a task.
    * However, objects in the Notes box in the Resource Form are not included
    * in this count.
    *
    * @return int
    */
   public int getObjectsValue ()
   {
      return (getIntValue(OBJECTS));
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
   public int getOutlineLevelValue ()
   {
      return (getIntValue(OUTLINE_LEVEL));
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
   public RelationList getPredecessors ()
   {
      return ((RelationList)get(PREDECESSORS));
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
    * @return MPXDuration
    */
   public MPXDuration getRemainingDuration ()
   {
      return ((MPXDuration)get(REMAINING_DURATION));
   }

   /**
    * The Remaining Work field shows the amount of time, or person-hours,
    * still required by all assigned resources to complete a task.
    *
    * @return the amount of time still required to complete a task
    */
   public MPXDuration getRemainingWork ()
   {
      return ((MPXDuration)get(REMAINING_WORK));
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
    * Gets the date to resume this task no earlier than
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
   public boolean getRollupValue ()
   {
      return (getBooleanValue(ROLLUP));
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
   public Boolean getRollup ()
   {
      return ((Boolean)get(ROLLUP));
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
    * @return value of duration. MPXDuration
    */
   public MPXDuration getStartVariance ()
   {
      return ((MPXDuration)get(START_VARIANCE));
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
   public RelationList getSuccessors ()
   {
      return ((RelationList)get(SUCCESSORS));
   }

   /**
    * The Summary field indicates whether a task is a summary task.
    *
    * @return boolean, true-is summary task
    */
   public boolean getSummaryValue ()
   {
      return (getBooleanValue(SUMMARY));
   }

   /**
    * The Summary field indicates whether a task is a summary task.
    *
    * @return boolean, true-is summary task
    */
   public Boolean getSummary ()
   {
      return ((Boolean)get(SUMMARY));
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
   public MPXDuration getTotalSlack ()
   {
      return ((MPXDuration)get(TOTAL_SLACK));
   }

   /**
    * The Unique ID field contains the number that Microsoft Project
    * automatically designates whenever a new task is created. This number
    * indicates the sequence in which the task was
    * created, regardless of placement in the schedule.
    *
    * @return String
    */
   public int getUniqueIDValue ()
   {
      return (getIntValue(UNIQUE_ID));
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
   public RelationList getUniqueIDPredecessors ()
   {
      return ((RelationList)get(UNIQUE_ID_PREDECESSORS));
   }

   /**
    * The Unique ID Predecessors field lists the unique ID numbers for the
    * predecessor tasks on which a task depends before it can be started or
    * finished. Each predecessor is linked to the task by a specific type of
    * task dependency and a lead time or lag time.
    *
    * @return list of predecessor UniqueIDs
    */
   public RelationList getUniqueIDSuccessors ()
   {
      return ((RelationList)get(UNIQUE_ID_SUCCESSORS));
   }

   /**
    * The Update Needed field indicates whether a TeamUpdate message
    * should be sent to the assigned resources because of changes to the
    * start date, finish date, or resource reassignments of the task.
    *
    * @return true if needed.
    */
   public boolean getUpdateNeededValue ()
   {
      return (getBooleanValue(UPDATE_NEEDED));
   }

   /**
    * The Update Needed field indicates whether a TeamUpdate message
    * should be sent to the assigned resources because of changes to the
    * start date, finish date, or resource reassignments of the task.
    *
    * @return true if needed.
    */
   public Boolean getUpdateNeeded ()
   {
      return ((Boolean)get(UPDATE_NEEDED));
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
    * @return MPXDuration representing duration .
    */
   public MPXDuration getWork ()
   {
      return ((MPXDuration)get(WORK));
   }

   /**
    * The Work Variance field contains the difference between a task's
    * baseline work and the currently scheduled work.
    *
    * @return MPXDuration representing duration.
    */
   public MPXDuration getWorkVariance ()
   {
      return ((MPXDuration)get(WORK_VARIANCE));
   }

   /**
    * This method generates a string in MPX format representing the
    * contents of this record.
    *
    * @return string containing the data for this record in MPX format.
    */
   public String toString ()
   {
      StringBuffer buf = new StringBuffer();

      //
      // If necessary, write the task model
      //
      if (m_model.getWritten() == false)
      {
         buf.append(m_model.toString());
         m_model.setWritten(true);
      }

      //
      // Write the task
      //
      buf.append(toString(RECORD_NUMBER, m_model.getModel()));

      //
      // Write the task notes
      //
      if (m_notes != null)
      {
         buf.append(m_notes.toString());
      }

      //
      // Write the recurring task
      //
      if (m_recurringTask != null)
      {
         buf.append(m_recurringTask.toString());
      }

      //
      // Write any resource assignments
      //
      if (m_assignments.isEmpty() == false)
      {
         Iterator list = m_assignments.iterator();

         while (list.hasNext())
         {
            buf.append(((ResourceAssignment)list.next()).toString());
         }
      }

      return (buf.toString());
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
   public LinkedList getChildTasks ()
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
      int id1 = getIDValue();
      int id2 = ((Task)o).getIDValue();

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
    * Retrieve the duration format
    *
    * @return duration format
    */
   public TimeUnit getDurationFormat ()
   {
      return (m_durationFormat);
   }

   /**
    * Set the duration format
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
    * Retrieve the subproject flag.
    *
    * @return subproject flag
    */
   public boolean getSubproject ()
   {
      return (m_subproject);
   }

   /**
    * Set the subproject flag.
    *
    * @param subproject subproject flag
    */
   public void setSubproject (boolean subproject)
   {
      m_subproject = subproject;
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
    * Sets the external task project file name
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
    * Retrieve the leveling delay format
    *
    * @return leveling delay  format
    */
   public TimeUnit getLevelingDelayFormat ()
   {
      return (m_levelingDelayFormat);
   }

   /**
    * Set the leveling delay format
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
   public MPXDuration getActualWorkProtected ()
   {
      return (m_actualWorkProtected);
   }

   /**
    * Sets the actual work protected value.
    *
    * @param actualWorkProtected actual work protected value
    */
   public void setActualWorkProtected (MPXDuration actualWorkProtected)
   {
      m_actualWorkProtected = actualWorkProtected;
   }

   /**
    * Retrieves the actual overtime work protected value.
    *
    * @return actual overtime work protected value
    */
   public MPXDuration getActualOvertimeWorkProtected ()
   {
      return (m_actualOvertimeWorkProtected);
   }

   /**
    * Sets the actual overtime work protected value.
    *
    * @param actualOvertimeWorkProtected actual overtime work protected value
    */
   public void setActualOvertimeWorkProtected (MPXDuration actualOvertimeWorkProtected)
   {
      m_actualOvertimeWorkProtected = actualOvertimeWorkProtected;
   }

   /**
    * Retrieve the amount of regular work.
    *
    * @return amount of regular work
    */
   public MPXDuration getRegularWork ()
   {
      return (m_regularWork);
   }

   /**
    * Set the amount of regular work.
    *
    * @param regularWork amount of regular work
    */
   public void setRegularWork (MPXDuration regularWork)
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
      return (getBooleanValue(FLAG11));
   }

   /**
    * Retrieves the flag value.
    *
    * @return flag value
    */
   public boolean getFlag12 ()
   {
      return (getBooleanValue(FLAG12));
   }

   /**
    * Retrieves the flag value.
    *
    * @return flag value
    */
   public boolean getFlag13 ()
   {
      return (getBooleanValue(FLAG13));
   }

   /**
    * Retrieves the flag value.
    *
    * @return flag value
    */
   public boolean getFlag14 ()
   {
      return (getBooleanValue(FLAG14));
   }

   /**
    * Retrieves the flag value.
    *
    * @return flag value
    */
   public boolean getFlag15 ()
   {
      return (getBooleanValue(FLAG15));
   }

   /**
    * Retrieves the flag value.
    *
    * @return flag value
    */
   public boolean getFlag16 ()
   {
      return (getBooleanValue(FLAG16));
   }

   /**
    * Retrieves the flag value.
    *
    * @return flag value
    */
   public boolean getFlag17 ()
   {
      return (getBooleanValue(FLAG17));
   }

   /**
    * Retrieves the flag value.
    *
    * @return flag value
    */
   public boolean getFlag18 ()
   {
      return (getBooleanValue(FLAG18));
   }

   /**
    * Retrieves the flag value.
    *
    * @return flag value
    */
   public boolean getFlag19 ()
   {
      return (getBooleanValue(FLAG19));
   }

   /**
    * Retrieves the flag value.
    *
    * @return flag value
    */
   public boolean getFlag20 ()
   {
      return (getBooleanValue(FLAG20));
   }

   /**
    * Sets the flag value
    *
    * @param b flag value
    */
   public void setFlag11 (boolean b)
   {
      set(FLAG11, b);
   }

   /**
    * Sets the flag value
    *
    * @param b flag value
    */
   public void setFlag12 (boolean b)
   {
      set(FLAG12, b);
   }

   /**
    * Sets the flag value
    *
    * @param b flag value
    */
   public void setFlag13 (boolean b)
   {
      set(FLAG13, b);
   }

   /**
    * Sets the flag value
    *
    * @param b flag value
    */
   public void setFlag14 (boolean b)
   {
      set(FLAG14, b);
   }

   /**
    * Sets the flag value
    *
    * @param b flag value
    */
   public void setFlag15 (boolean b)
   {
      set(FLAG15, b);
   }

   /**
    * Sets the flag value
    *
    * @param b flag value
    */
   public void setFlag16 (boolean b)
   {
      set(FLAG16, b);
   }

   /**
    * Sets the flag value
    *
    * @param b flag value
    */
   public void setFlag17 (boolean b)
   {
      set(FLAG17, b);
   }

   /**
    * Sets the flag value
    *
    * @param b flag value
    */
   public void setFlag18 (boolean b)
   {
      set(FLAG18, b);
   }

   /**
    * Sets the flag value
    *
    * @param b flag value
    */
   public void setFlag19 (boolean b)
   {
      set(FLAG19, b);
   }

   /**
    * Sets the flag value
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
    * Retrieves a text value
    *
    * @return Text value
    */
   public String getText11 ()
   {
      return ((String)get(TEXT11));
   }

   /**
    * Retrieves a text value
    *
    * @return Text value
    */
   public String getText12 ()
   {
      return ((String)get(TEXT12));
   }

   /**
    * Retrieves a text value
    *
    * @return Text value
    */
   public String getText13 ()
   {
      return ((String)get(TEXT13));
   }

   /**
    * Retrieves a text value
    *
    * @return Text value
    */
   public String getText14 ()
   {
      return ((String)get(TEXT14));
   }

   /**
    * Retrieves a text value
    *
    * @return Text value
    */
   public String getText15 ()
   {
      return ((String)get(TEXT15));
   }

   /**
    * Retrieves a text value
    *
    * @return Text value
    */
   public String getText16 ()
   {
      return ((String)get(TEXT16));
   }

   /**
    * Retrieves a text value
    *
    * @return Text value
    */
   public String getText17 ()
   {
      return ((String)get(TEXT17));
   }

   /**
    * Retrieves a text value
    *
    * @return Text value
    */
   public String getText18 ()
   {
      return ((String)get(TEXT18));
   }

   /**
    * Retrieves a text value
    *
    * @return Text value
    */
   public String getText19 ()
   {
      return ((String)get(TEXT19));
   }

   /**
    * Retrieves a text value
    *
    * @return Text value
    */
   public String getText20 ()
   {
      return ((String)get(TEXT20));
   }

   /**
    * Retrieves a text value
    *
    * @return Text value
    */
   public String getText21 ()
   {
      return ((String)get(TEXT21));
   }

   /**
    * Retrieves a text value
    *
    * @return Text value
    */
   public String getText22 ()
   {
      return ((String)get(TEXT22));
   }

   /**
    * Retrieves a text value
    *
    * @return Text value
    */
   public String getText23 ()
   {
      return ((String)get(TEXT23));
   }

   /**
    * Retrieves a text value
    *
    * @return Text value
    */
   public String getText24 ()
   {
      return ((String)get(TEXT24));
   }

   /**
    * Retrieves a text value
    *
    * @return Text value
    */
   public String getText25 ()
   {
      return ((String)get(TEXT25));
   }

   /**
    * Retrieves a text value
    *
    * @return Text value
    */
   public String getText26 ()
   {
      return ((String)get(TEXT26));
   }

   /**
    * Retrieves a text value
    *
    * @return Text value
    */
   public String getText27 ()
   {
      return ((String)get(TEXT27));
   }

   /**
    * Retrieves a text value
    *
    * @return Text value
    */
   public String getText28 ()
   {
      return ((String)get(TEXT28));
   }

   /**
    * Retrieves a text value
    *
    * @return Text value
    */
   public String getText29 ()
   {
      return ((String)get(TEXT29));
   }

   /**
    * Retrieves a text value
    *
    * @return Text value
    */
   public String getText30 ()
   {
      return ((String)get(TEXT30));
   }

   /**
    * Sets a text value
    *
    * @param string Text value
    */
   public void setText11 (String string)
   {
      set(TEXT11, string);
   }

   /**
    * Sets a text value
    *
    * @param string Text value
    */
   public void setText12 (String string)
   {
      set(TEXT12, string);
   }

   /**
    * Sets a text value
    *
    * @param string Text value
    */
   public void setText13 (String string)
   {
      set(TEXT13, string);
   }

   /**
    * Sets a text value
    *
    * @param string Text value
    */
   public void setText14 (String string)
   {
      set(TEXT14, string);
   }

   /**
    * Sets a text value
    *
    * @param string Text value
    */
   public void setText15 (String string)
   {
      set(TEXT15, string);
   }

   /**
    * Sets a text value
    *
    * @param string Text value
    */
   public void setText16 (String string)
   {
      set(TEXT16, string);
   }

   /**
    * Sets a text value
    *
    * @param string Text value
    */
   public void setText17 (String string)
   {
      set(TEXT17, string);
   }

   /**
    * Sets a text value
    *
    * @param string Text value
    */
   public void setText18 (String string)
   {
      set(TEXT18, string);
   }

   /**
    * Sets a text value
    *
    * @param string Text value
    */
   public void setText19 (String string)
   {
      set(TEXT19, string);
   }

   /**
    * Sets a text value
    *
    * @param string Text value
    */
   public void setText20 (String string)
   {
      set(TEXT20, string);
   }

   /**
    * Sets a text value
    *
    * @param string Text value
    */
   public void setText21 (String string)
   {
      set(TEXT21, string);
   }

   /**
    * Sets a text value
    *
    * @param string Text value
    */
   public void setText22 (String string)
   {
      set(TEXT22, string);
   }

   /**
    * Sets a text value
    *
    * @param string Text value
    */
   public void setText23 (String string)
   {
      set(TEXT23, string);
   }

   /**
    * Sets a text value
    *
    * @param string Text value
    */
   public void setText24 (String string)
   {
      set(TEXT24, string);
   }

   /**
    * Sets a text value
    *
    * @param string Text value
    */
   public void setText25 (String string)
   {
      set(TEXT25, string);
   }

   /**
    * Sets a text value
    *
    * @param string Text value
    */
   public void setText26 (String string)
   {
      set(TEXT26, string);
   }

   /**
    * Sets a text value
    *
    * @param string Text value
    */
   public void setText27 (String string)
   {
      set(TEXT27, string);
   }

   /**
    * Sets a text value
    *
    * @param string Text value
    */
   public void setText28 (String string)
   {
      set(TEXT28, string);
   }

   /**
    * Sets a text value
    *
    * @param string Text value
    */
   public void setText29 (String string)
   {
      set(TEXT29, string);
   }

   /**
    * Sets a text value
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
   public void setNumber6 (Double val)
   {
      set(NUMBER6, val);
   }

   /**
    * Retrieves a numeric value
    *
    * @return Numeric value
    */
   public double getNumber6Value ()
   {
      return (getDoubleValue(NUMBER6));
   }

   /**
    * Retrieves a numeric value
    *
    * @return Numeric value
    */
   public Double getNumber6 ()
   {
      return ((Double)get(NUMBER6));
   }

   /**
    * Sets a numeric value.
    *
    * @param val Numeric value
    */
   public void setNumber7 (Double val)
   {
      set(NUMBER7, val);
   }

   /**
    * Retrieves a numeric value
    *
    * @return Numeric value
    */
   public double getNumber7Value ()
   {
      return (getDoubleValue(NUMBER7));
   }

   /**
    * Retrieves a numeric value
    *
    * @return Numeric value
    */
   public Double getNumber7 ()
   {
      return ((Double)get(NUMBER7));
   }

   /**
    * Sets a numeric value.
    *
    * @param val Numeric value
    */
   public void setNumber8 (Double val)
   {
      set(NUMBER8, val);
   }

   /**
    * Retrieves a numeric value
    *
    * @return Numeric value
    */
   public double getNumber8Value ()
   {
      return (getDoubleValue(NUMBER8));
   }

   /**
    * Retrieves a numeric value
    *
    * @return Numeric value
    */
   public Double getNumber8 ()
   {
      return ((Double)get(NUMBER8));
   }

   /**
    * Sets a numeric value.
    *
    * @param val Numeric value
    */
   public void setNumber9 (Double val)
   {
      set(NUMBER9, val);
   }

   /**
    * Retrieves a numeric value
    *
    * @return Numeric value
    */
   public double getNumber9Value ()
   {
      return (getDoubleValue(NUMBER9));
   }

   /**
    * Retrieves a numeric value
    *
    * @return Numeric value
    */
   public Double getNumber9 ()
   {
      return ((Double)get(NUMBER9));
   }

   /**
    * Sets a numeric value.
    *
    * @param val Numeric value
    */
   public void setNumber10 (Double val)
   {
      set(NUMBER10, val);
   }

   /**
    * Retrieves a numeric value
    *
    * @return Numeric value
    */
   public double getNumber10Value ()
   {
      return (getDoubleValue(NUMBER10));
   }

   /**
    * Retrieves a numeric value
    *
    * @return Numeric value
    */
   public Double getNumber10 ()
   {
      return ((Double)get(NUMBER10));
   }

   /**
    * Sets a numeric value.
    *
    * @param val Numeric value
    */
   public void setNumber11 (Double val)
   {
      set(NUMBER11, val);
   }

   /**
    * Retrieves a numeric value
    *
    * @return Numeric value
    */
   public double getNumber11Value ()
   {
      return (getDoubleValue(NUMBER11));
   }

   /**
    * Retrieves a numeric value
    *
    * @return Numeric value
    */
   public Double getNumber11 ()
   {
      return ((Double)get(NUMBER11));
   }

   /**
    * Sets a numeric value.
    *
    * @param val Numeric value
    */
   public void setNumber12 (Double val)
   {
      set(NUMBER12, val);
   }

   /**
    * Retrieves a numeric value
    *
    * @return Numeric value
    */
   public double getNumber12Value ()
   {
      return (getDoubleValue(NUMBER12));
   }

   /**
    * Retrieves a numeric value
    *
    * @return Numeric value
    */
   public Double getNumber12 ()
   {
      return ((Double)get(NUMBER12));
   }

   /**
    * Sets a numeric value.
    *
    * @param val Numeric value
    */
   public void setNumber13 (Double val)
   {
      set(NUMBER13, val);
   }

   /**
    * Retrieves a numeric value
    *
    * @return Numeric value
    */
   public double getNumber13Value ()
   {
      return (getDoubleValue(NUMBER13));
   }

   /**
    * Retrieves a numeric value
    *
    * @return Numeric value
    */
   public Double getNumber13 ()
   {
      return ((Double)get(NUMBER13));
   }

   /**
    * Sets a numeric value.
    *
    * @param val Numeric value
    */
   public void setNumber14 (Double val)
   {
      set(NUMBER14, val);
   }

   /**
    * Retrieves a numeric value
    *
    * @return Numeric value
    */
   public double getNumber14Value ()
   {
      return (getDoubleValue(NUMBER14));
   }

   /**
    * Retrieves a numeric value
    *
    * @return Numeric value
    */
   public Double getNumber14 ()
   {
      return ((Double)get(NUMBER14));
   }

   /**
    * Sets a numeric value.
    *
    * @param val Numeric value
    */
   public void setNumber15 (Double val)
   {
      set(NUMBER15, val);
   }

   /**
    * Retrieves a numeric value
    *
    * @return Numeric value
    */
   public double getNumber15Value ()
   {
      return (getDoubleValue(NUMBER15));
   }

   /**
    * Retrieves a numeric value
    *
    * @return Numeric value
    */
   public Double getNumber15 ()
   {
      return ((Double)get(NUMBER15));
   }

   /**
    * Sets a numeric value.
    *
    * @param val Numeric value
    */
   public void setNumber16 (Double val)
   {
      set(NUMBER16, val);
   }

   /**
    * Retrieves a numeric value
    *
    * @return Numeric value
    */
   public double getNumber16Value ()
   {
      return (getDoubleValue(NUMBER16));
   }

   /**
    * Retrieves a numeric value
    *
    * @return Numeric value
    */
   public Double getNumber16 ()
   {
      return ((Double)get(NUMBER16));
   }

   /**
    * Sets a numeric value.
    *
    * @param val Numeric value
    */
   public void setNumber17 (Double val)
   {
      set(NUMBER17, val);
   }

   /**
    * Retrieves a numeric value
    *
    * @return Numeric value
    */
   public double getNumber17Value ()
   {
      return (getDoubleValue(NUMBER17));
   }

   /**
    * Retrieves a numeric value
    *
    * @return Numeric value
    */
   public Double getNumber17 ()
   {
      return ((Double)get(NUMBER17));
   }

   /**
    * Sets a numeric value.
    *
    * @param val Numeric value
    */
   public void setNumber18 (Double val)
   {
      set(NUMBER18, val);
   }

   /**
    * Retrieves a numeric value
    *
    * @return Numeric value
    */
   public double getNumber18Value ()
   {
      return (getDoubleValue(NUMBER18));
   }

   /**
    * Retrieves a numeric value
    *
    * @return Numeric value
    */
   public Double getNumber18 ()
   {
      return ((Double)get(NUMBER18));
   }

   /**
    * Sets a numeric value.
    *
    * @param val Numeric value
    */
   public void setNumber19 (Double val)
   {
      set(NUMBER19, val);
   }

   /**
    * Retrieves a numeric value
    *
    * @return Numeric value
    */
   public double getNumber19Value ()
   {
      return (getDoubleValue(NUMBER19));
   }

   /**
    * Retrieves a numeric value
    *
    * @return Numeric value
    */
   public Double getNumber19 ()
   {
      return ((Double)get(NUMBER19));
   }

   /**
    * Sets a numeric value.
    *
    * @param val Numeric value
    */
   public void setNumber20 (Double val)
   {
      set(NUMBER20, val);
   }

   /**
    * Retrieves a numeric value
    *
    * @return Numeric value
    */
   public double getNumber20Value ()
   {
      return (getDoubleValue(NUMBER20));
   }

   /**
    * Retrieves a numeric value
    *
    * @return Numeric value
    */
   public Double getNumber20 ()
   {
      return ((Double)get(NUMBER20));
   }

   /**
    * Retrieves a duration.
    *
    * @return MPXDuration
    */
   public MPXDuration getDuration10 ()
   {
      return (MPXDuration)get(DURATION10);
   }

   /**
    * Retrieves a duration.
    *
    * @return MPXDuration
    */
   public MPXDuration getDuration4 ()
   {
      return (MPXDuration)get(DURATION4);
   }

   /**
    * Retrieves a duration.
    *
    * @return MPXDuration
    */
   public MPXDuration getDuration5 ()
   {
      return (MPXDuration)get(DURATION5);
   }

   /**
    * Retrieves a duration.
    *
    * @return MPXDuration
    */
   public MPXDuration getDuration6 ()
   {
      return (MPXDuration)get(DURATION6);
   }

   /**
    * Retrieves a duration.
    *
    * @return MPXDuration
    */
   public MPXDuration getDuration7 ()
   {
      return (MPXDuration)get(DURATION7);
   }

   /**
    * Retrieves a duration.
    *
    * @return MPXDuration
    */
   public MPXDuration getDuration8 ()
   {
      return (MPXDuration)get(DURATION8);
   }

   /**
    * Retrieves a duration.
    *
    * @return MPXDuration
    */
   public MPXDuration getDuration9 ()
   {
      return (MPXDuration)get(DURATION9);
   }

   /**
    * User defined duration field.
    *
    * @param duration Duration value
    */
   public void setDuration10 (MPXDuration duration)
   {
      set(DURATION10, duration);
   }

   /**
    * User defined duration field.
    *
    * @param duration Duration value
    */
   public void setDuration4 (MPXDuration duration)
   {
      set(DURATION4, duration);
   }

   /**
    * User defined duration field.
    *
    * @param duration Duration value
    */
   public void setDuration5 (MPXDuration duration)
   {
      set(DURATION5, duration);
   }

   /**
    * User defined duration field.
    *
    * @param duration Duration value
    */
   public void setDuration6 (MPXDuration duration)
   {
      set(DURATION6, duration);
   }

   /**
    * User defined duration field.
    *
    * @param duration Duration value
    */
   public void setDuration7 (MPXDuration duration)
   {
      set(DURATION7, duration);
   }

   /**
    * User defined duration field.
    *
    * @param duration Duration value
    */
   public void setDuration8 (MPXDuration duration)
   {
      set(DURATION8, duration);
   }

   /**
    * User defined duration field.
    *
    * @param duration Duration value
    */
   public void setDuration9 (MPXDuration duration)
   {
      set(DURATION9, duration);
   }

   /**
    * Retrieves a date value
    *
    * @return Date value
    */
   public Date getDate1 ()
   {
      return ((Date)get(DATE1));
   }

   /**
    * Retrieves a date value
    *
    * @return Date value
    */
   public Date getDate10 ()
   {
      return ((Date)get(DATE10));
   }

   /**
    * Retrieves a date value
    *
    * @return Date value
    */
   public Date getDate2 ()
   {
      return ((Date)get(DATE2));
   }

   /**
    * Retrieves a date value
    *
    * @return Date value
    */
   public Date getDate3 ()
   {
      return ((Date)get(DATE3));
   }

   /**
    * Retrieves a date value
    *
    * @return Date value
    */
   public Date getDate4 ()
   {
      return ((Date)get(DATE4));
   }

   /**
    * Retrieves a date value
    *
    * @return Date value
    */
   public Date getDate5 ()
   {
      return ((Date)get(DATE5));
   }

   /**
    * Retrieves a date value
    *
    * @return Date value
    */
   public Date getDate6 ()
   {
      return ((Date)get(DATE6));
   }

   /**
    * Retrieves a date value
    *
    * @return Date value
    */
   public Date getDate7 ()
   {
      return ((Date)get(DATE7));
   }

   /**
    * Retrieves a date value
    *
    * @return Date value
    */
   public Date getDate8 ()
   {
      return ((Date)get(DATE8));
   }

   /**
    * Retrieves a date value
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
      setDate(DATE1, date);
   }

   /**
    * Sets a date value.
    *
    * @param date Date value
    */
   public void setDate10 (Date date)
   {
      setDate(DATE10, date);
   }

   /**
    * Sets a date value.
    *
    * @param date Date value
    */
   public void setDate2 (Date date)
   {
      setDate(DATE2, date);
   }

   /**
    * Sets a date value.
    *
    * @param date Date value
    */
   public void setDate3 (Date date)
   {
      setDate(DATE3, date);
   }

   /**
    * Sets a date value.
    *
    * @param date Date value
    */
   public void setDate4 (Date date)
   {
      setDate(DATE4, date);
   }

   /**
    * Sets a date value.
    *
    * @param date Date value
    */
   public void setDate5 (Date date)
   {
      setDate(DATE5, date);
   }

   /**
    * Sets a date value.
    *
    * @param date Date value
    */
   public void setDate6 (Date date)
   {
      setDate(DATE6, date);
   }

   /**
    * Sets a date value.
    *
    * @param date Date value
    */
   public void setDate7 (Date date)
   {
      setDate(DATE7, date);
   }

   /**
    * Sets a date value.
    *
    * @param date Date value
    */
   public void setDate8 (Date date)
   {
      setDate(DATE8, date);
   }

   /**
    * Sets a date value.
    *
    * @param date Date value
    */
   public void setDate9 (Date date)
   {
      setDate(DATE9, date);
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
      setCurrency(COST10, number);
   }

   /**
    * Sets a cost value.
    *
    * @param number Cost value
    */
   public void setCost4 (Number number)
   {
      setCurrency(COST4, number);
   }

   /**
    * Sets a cost value.
    *
    * @param number Cost value
    */
   public void setCost5 (Number number)
   {
      setCurrency(COST5, number);
   }

   /**
    * Sets a cost value.
    *
    * @param number Cost value
    */
   public void setCost6 (Number number)
   {
      setCurrency(COST6, number);
   }

   /**
    * Sets a cost value.
    *
    * @param number Cost value
    */
   public void setCost7 (Number number)
   {
      setCurrency(COST7, number);
   }

   /**
    * Sets a cost value.
    *
    * @param number Cost value
    */
   public void setCost8 (Number number)
   {
      setCurrency(COST8, number);
   }

   /**
    * Sets a cost value.
    *
    * @param number Cost value
    */
   public void setCost9 (Number number)
   {
      setCurrency(COST9, number);
   }

   /**
    * Retrieves a start date
    *
    * @return Date start date
    */
   public Date getStart10 ()
   {
      return ((Date)get(START10));
   }

   /**
    * Retrieves a start date
    *
    * @return Date start date
    */
   public Date getStart6 ()
   {
      return ((Date)get(START6));
   }

   /**
    * Retrieves a start date
    *
    * @return Date start date
    */
   public Date getStart7 ()
   {
      return ((Date)get(START7));
   }

   /**
    * Retrieves a start date
    *
    * @return Date start date
    */
   public Date getStart8 ()
   {
      return ((Date)get(START8));
   }

   /**
    * Retrieves a start date
    *
    * @return Date start date
    */
   public Date getStart9 ()
   {
      return ((Date)get(START9));
   }

   /**
    * Sets a start date
    *
    * @param date Start date
    */
   public void setStart10 (Date date)
   {
      setDate(START10, date);
   }

   /**
    * Sets a start date
    *
    * @param date Start date
    */
   public void setStart6 (Date date)
   {
      setDate(START6, date);
   }

   /**
    * Sets a start date
    *
    * @param date Start date
    */
   public void setStart7 (Date date)
   {
      setDate(START7, date);
   }

   /**
    * Sets a start date
    *
    * @param date Start date
    */
   public void setStart8 (Date date)
   {
      setDate(START8, date);
   }

   /**
    * Sets a start date
    *
    * @param date Start date
    */
   public void setStart9 (Date date)
   {
      setDate(START9, date);
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
      setDate(FINISH10, date);
   }

   /**
    * User defined finish date field.
    *
    * @param date Date value
    */
   public void setFinish6 (Date date)
   {
      setDate(FINISH6, date);
   }

   /**
    * User defined finish date field.
    *
    * @param date Date value
    */
   public void setFinish7 (Date date)
   {
      setDate(FINISH7, date);
   }

   /**
    * User defined finish date field.
    *
    * @param date Date value
    */
   public void setFinish8 (Date date)
   {
      setDate(FINISH8, date);
   }

   /**
    * User defined finish date field.
    *
    * @param date Date value
    */
   public void setFinish9 (Date date)
   {
      setDate(FINISH9, date);
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
    * Retrieves the actual overtime work value
    *
    * @return actual overtime work value
    */
   public MPXDuration getActualOvertimeWork ()
   {
      return (m_actualOvertimeWork);
   }

   /**
    * Sets the actual overtime work value
    *
    * @param work actual overtime work value
    */
   public void setActualOvertimeWork (MPXDuration work)
   {
      m_actualOvertimeWork = work;
   }

   /**
    * Retrieves the fixed cost accrual flag value
    *
    * @return fixed cost accrual flag
    */
   public AccrueType getFixedCostAccrual ()
   {
      return (m_fixedCostAccrual);
   }

   /**
    * Sets the fixed cost accrual flag value
    *
    * @param type fixed cost accrual type
    */
   public void setFixedCostAccrual (AccrueType type)
   {
      m_fixedCostAccrual = type;
   }

   /**
    * Retrieves the task hyperlink attribute
    *
    * @return hyperlink attribute
    */
   public String getHyperlink ()
   {
      return (m_hyperlink);
   }

   /**
    * Retrieves the task hyperlink address attribute
    *
    * @return hyperlink address attribute
    */
   public String getHyperlinkAddress ()
   {
      return (m_hyperlinkAddress);
   }

   /**
    * Retrieves the task hyperlink sub-address attribute
    *
    * @return hyperlink sub address attribute
    */
   public String getHyperlinkSubAddress ()
   {
      return (m_hyperlinkSubAddress);
   }

   /**
    * Sets the task hyperlink attribute
    *
    * @param text hyperlink attribute
    */
   public void setHyperlink (String text)
   {
      m_hyperlink = text;
   }

   /**
    * Sets the task hyperlink address attribute
    *
    * @param text hyperlink address attribute
    */
   public void setHyperlinkAddress (String text)
   {
      m_hyperlinkAddress = text;
   }

   /**
    * Sets the task hyperlink sub address attribute
    *
    * @param text hyperlink sub address attribute
    */
   public void setHyperlinkSubAddress (String text)
   {
      m_hyperlinkSubAddress = text;
   }

   /**
    * Retrieves the level assignments flag
    *
    * @return level assignments flag
    */
   public boolean getLevelAssignments ()
   {
      return (m_levelAssignments);
   }

   /**
    * Sets the level assignments flag
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
    * Retrieves the task leveling delay attribute
    *
    * @return task leveling delay
    */
   public MPXDuration getLevelingDelay ()
   {
      return (m_levelingDelay);
   }

   /**
    * Sets the task leveling delay attribute
    *
    * @param delay task leveling delay attribute
    */
   public void setLevelingDelay (MPXDuration delay)
   {
      m_levelingDelay = delay;
   }

   /**
    * Retrieves the overtime work attribute.
    *
    * @return overtime work value
    */
   public MPXDuration getOvertimeWork ()
   {
      return (m_overtimeWork);
   }

   /**
    * Sets the overtime work attribute.
    *
    * @param work overtime work value
    */
   public void setOvertimeWork (MPXDuration work)
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
    * Retrieves the preleveled finish attribute
    *
    * @return preleveled finish
    */
   public Date getPreleveledFinish ()
   {
      return (m_preleveledFinish);
   }

   /**
    * Sets the preleveled start attribute
    *
    * @param date preleveled start attribute
    */
   public void setPreleveledStart (Date date)
   {
      m_preleveledStart = date;
   }

   /**
    * Sets the preleveled finish attribute
    *
    * @param date preleveled finish attribute
    */
   public void setPreleveledFinish (Date date)
   {
      m_preleveledFinish = date;
   }

   /**
    * Retrieves the remaining overtime work attribute
    *
    * @return remaining overtime work
    */
   public MPXDuration getRemainingOvertimeWork ()
   {
      return (m_remainingOvertimeWork);
   }

   /**
    * Sets the remaining overtime work attribute
    *
    * @param work remaining overtime work
    */
   public void setRemainingOvertimeWork (MPXDuration work)
   {
      m_remainingOvertimeWork = work;
   }

   /**
    * Retrieves the remaining overtime cost
    *
    * @return remaining overtime cost value
    */
   public Number getRemainingOvertimeCost ()
   {
      return (m_remainingOvertimeCost);
   }

   /**
    * Sets the remaining overtime cost value
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
    * @return MPXCalendar instance
    */
   public MPXCalendar getCalendar ()
   {
      return (getParentFile().getBaseCalendar(m_calendarName));
   }

   /**
    * Retrieves the name of the base calendar associated with this task.
    * Note that this attribute appears in MPP9 and MSPDI files.
    *
    * @return calendar name
    */
   public String getCalendarName ()
   {
      return (m_calendarName);
   }

   /**
    * Sets the name of the base calendar associated with this task.
    * Note that this attribute appears in MPP9 and MSPDI files.
    *
    * @param name base calendar name
    */
   public void setCalendarName (String name)
   {
      m_calendarName = name;
   }

   /**
    * Retrieve the value of a field using its alias
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
    * Set the value of a field using its alias
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
    * This is a reference to the parent task, as specified by the
    * outline level.
    */
   private Task m_parent;

   /**
    * This list holds references to all tasks that are children of the
    * current task as specified by the outline level.
    */
   private LinkedList m_children = new LinkedList();

   /**
    * Reference to the task model controlling which fields from the task
    * record appear in the MPX file.
    */
   private TaskModel m_model;

   /**
    * List of resource assignments for this task.
    */
   private LinkedList m_assignments = new LinkedList();

   /**
    * Task notes associated with this task.
    */
   private TaskNotes m_notes;

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
   private MPXDuration m_actualOvertimeWork;
   private AccrueType m_fixedCostAccrual;
   private String m_hyperlink;
   private String m_hyperlinkAddress;
   private String m_hyperlinkSubAddress;
   private boolean m_levelAssignments;
   private boolean m_levelingCanSplit;
   private MPXDuration m_levelingDelay;
   private MPXDuration m_overtimeWork;
   private Date m_preleveledStart;
   private Date m_preleveledFinish;
   private MPXDuration m_remainingOvertimeWork;
   private Number m_remainingOvertimeCost;
   private String m_calendarName;
   private boolean m_null;
   private String m_wbsLevel;
   private TimeUnit m_durationFormat;
   private boolean m_resumeValid;
   private boolean m_recurring;
   private boolean m_overAllocated;
   private boolean m_subproject;
   private boolean m_subprojectReadOnly;
   private boolean m_externalTask;
   private String m_externalTaskProject;
   private Number m_acwp;
   private TimeUnit m_levelingDelayFormat;
   private boolean m_ignoreResourceCalendar;
   private Integer m_physicalPercentComplete;
   private EarnedValueMethod m_earnedValueMethod;
   private MPXDuration m_actualWorkProtected;
   private MPXDuration m_actualOvertimeWorkProtected;
   private MPXDuration m_regularWork;

   /**
    * The % Complete field contains the current status of a task, expressed as the percentage
    * of the task's duration that has been completed. You can enter percent complete, or you
    * can have Microsoft Project calculate it for you based on actual duration.
    */
   private static final int PERCENTAGE_COMPLETE = 44;

   /**
    * The % Work Complete field contains the current status of a task, expressed as the
    * percentage of the task's work that has been completed. You can enter percent work
    * complete, or you can have Microsoft Project calculate it for you based on actual
    * work on the task.
    */
   private static final int PERCENTAGE_WORK_COMPLETE = 25;

   /**
    * The Actual Cost field shows costs incurred for work already performed by all resources
    * on a task, along with any other recorded costs associated with the task. You can enter
    * all the actual costs or have Microsoft Project calculate them for you.
    */
   private static final int ACTUAL_COST = 32;

   /**
    * The Actual Duration field shows the span of actual working time for a task so far,
    * based on the scheduled duration and current remaining work or completion percentage.
    */
   private static final int ACTUAL_DURATION = 42;

   /**
    * The Actual Finish field shows the date and time that a task actually finished.
    * Microsoft Project sets the Actual Finish field to the scheduled finish date if
    * the completion percentage is 100. This field contains "NA" until you enter actual
    * information or set the completion percentage to 100.
    */
   private static final int ACTUAL_FINISH = 59;

   /**
    * The Actual Start field shows the date and time that a task actually began.
    * When a task is first created, the Actual Start field contains "NA." Once you
    * enter the first actual work or a completion percentage for a task, Microsoft
    * Project sets the actual start date to the scheduled start date.
    */
   private static final int ACTUAL_START = 58;

   /**
    * The Actual Work field shows the amount of work that has already been done by
    * the resources assigned to a task.
    */
   private static final int ACTUAL_WORK = 22;

   /**
    * The Baseline Cost field shows the total planned cost for a task. Baseline cost
    * is also referred to as budget at completion (BAC).
    */
   private static final int BASELINE_COST = 31;

   /**
    * The Baseline Duration field shows the original span of time planned to complete a task.
    */
   private static final int BASELINE_DURATION = 41;

   /**
    * The Baseline Finish field shows the planned completion date for a task at the time
    * you saved a baseline. Information in this field becomes available when you set a
    * baseline for a task.
    */
   private static final int BASELINE_FINISH = 57;

   /**
    * The Baseline Start field shows the planned beginning date for a task at the time
    * you saved a baseline. Information in this field becomes available when you set a baseline.
    */
   private static final int BASELINE_START = 56;

   /**
    * The Baseline Work field shows the originally planned amount of work to be performed
    * by all resources assigned to a task. This field shows the planned person-hours
    * scheduled for a task. Information in the Baseline Work field becomes available
    * when you set a baseline for the project.
    */
   private static final int BASELINE_WORK = 21;

   /**
    * The BCWP (budgeted cost of work performed) field contains the cumulative value
    * of the assignment's timephased percent complete multiplied by the assignments
    * timephased baseline cost. BCWP is calculated up to the status date or today's date.
    * This information is also known as earned value.
    */
   private static final int BCWP = 86;

   /**
    * The BCWS (budgeted cost of work scheduled) field contains the cumulative timephased
    * baseline costs up to the status date or todays date.
    */
   private static final int BCWS = 85;

   /**
    * The Confirmed field indicates whether all resources assigned to a task have
    * accepted or rejected the task assignment in response to a TeamAssign message
    * regarding their assignments.
    */
   private static final int CONFIRMED = 135;

   /**
    *  The Constraint Date field shows the specific date associated with certain
    * constraint types, such as Must Start On, Must Finish On, Start No Earlier Than,
    * Start No Later Than, Finish No Earlier Than, and Finish No Later Than.
    */
   private static final int CONSTRAINT_DATE = 68;

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
   private static final int CONSTRAINT_TYPE = 91;

   /**
    *  The Contact field contains the name of an individual responsible for a task.
    */
   private static final int CONTACT = 15;

   /**
    * The Cost field shows the total scheduled, or projected, cost for a task,
    * based on costs already incurred for work performed by all resources assigned
    * to the task, in addition to the costs planned for the remaining work for the
    * assignment. This can also be referred to as estimate at completion (EAC).
    */
   private static final int COST = 30;

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
   private static final int COST_VARIANCE = 34;

   /**
    * The Created field contains the date and time when a task was added to the project.
    */
   private static final int CREATE_DATE = 125;

   /**
    * The Critical field indicates whether a task has any room in the schedule to slip,
    * or if a task is on the critical path. The Critical field contains Yes if the task
    * is critical and No if the task is not critical.
    */
   private static final int CRITICAL = 82;

   /**
    * The CV (earned value cost variance) field shows the difference between how much
    * it should have cost to achieve the current level of completion on the task, and
    * how much it has actually cost to achieve the current level of completion up to
    * the status date or todays date.
    */
   private static final int CV = 88;

   /**
    * The amount of time a task can slip before it affects another task's dates or the
    * project finish date. Free slack is the amount of time a task can slip before it
    * delays another task. Total slack is the amount of time a task can slip before it
    * delays the project finish date. When the total slack is negative, the task
    * duration is too long for its successor to begin on the date required by a constraint.
    */
   private static final int DELAY = 92;

   /**
    * The Duration field is the total span of active working time for a task.
    * This is generally the amount of time from the start to the finish of a task.
    * The default for new tasks is 1 day (1d).
    */
   private static final int DURATION = 40;

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
   private static final int DURATION_VARIANCE = 45;

   /**
    * The Early Finish field contains the earliest date that a task could possibly finish,
    * based on early finish dates of predecessor and successor tasks, other constraints,
    * and any leveling delay.
    */
   private static final int EARLY_FINISH = 53;

   /**
    * The Early Start field contains the earliest date that a task could possibly begin,
    * based on the early start dates of predecessor and successor tasks, and other constraints.
    */
   private static final int EARLY_START = 52;

   /**
    * The Finish field shows the date and time that a task is scheduled to be completed.
    * You can enter the finish date you want, to indicate the date when the task should be
    * completed. Or, you can have Microsoft Project calculate the finish date.
    */
   private static final int FINISH = 51;

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
   private static final int FINISH_VARIANCE = 67;

   /**
    * Whether  fixed or not. Boolean value
    */
   private static final int FIXED = 80;

   /**
    * The Fixed Cost field shows any task expense that is not associated with a resource cost.
    */
   private static final int FIXED_COST = 35;

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
   private static final int FREE_SLACK = 93;

   /**
    * The Hide Bar field indicates whether the Gantt bars and Calendar bars for a task
    * are hidden. Click Yes in the Hide Bar field to hide the bar for the task. Click No
    * in the Hide Bar field to show the bar for the task.
    */
   private static final int HIDE_BAR = 123;

   /**
    * The ID field contains the identifier number that Microsoft Project automatically
    * assigns to each task as you add it to the project. The ID indicates the position
    * of a task with respect to the other tasks.
    */
   private static final int ID = 90;

   /**
    * The Late Finish field contains the latest date that a task can finish without
    * delaying the finish of the project. This date is based on the task's late start
    * date, as well as the late start and late finish dates of predecessor and successor
    *  tasks, and other constraints.
    */
   private static final int LATE_FINISH = 55;

   /**
    * The Late Start field contains the latest date that a task can start without delaying
    * the finish of the project. This date is based on the tasks start date, as well as
    * the late start and late finish dates of predecessor and successor tasks, and other constraints.
    */
   private static final int LATE_START = 54;

   /**
    *  The Linked Fields field indicates whether there are OLE links to the task,
    * either from elsewhere in the active project, another Microsoft Project file,
    * or from another program.
    */
   private static final int LINKED_FIELDS = 122;

   /**
    * The Marked field indicates whether a task is marked for further action or identification
    * of some kind. To mark a task, click Yes in the Marked field. If you don't want a task
    * marked, click No.
    */
   private static final int MARKED = 83;

   /**
    * The Milestone field indicates whether a task is a milestone.
    */
   private static final int MILESTONE = 81;

   /**
    * The Name field contains the name of a task.
    */
   private static final int NAME = 1;

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
   private static final int OBJECTS = 121;

   /**
    * The Outline Level field contains the number that indicates the level of the task
    * in the project outline hierarchy.
    */
   private static final int OUTLINE_LEVEL = 3;

   /**
    * The Outline Number field contains the number of the task in the structure of an outline.
    * This number indicates the task's position within the hierarchical structure of the
    * project outline. The outline number is similar to a WBS (work breakdown structure)
    * number, except that the outline number is automatically entered by Microsoft Project.
    */
   private static final int OUTLINE_NUMBER = 99;

   /**
    * The Predecessors field lists the task ID numbers for the predecessor tasks on which
    * the task depends before it can be started or finished. Each predecessor is linked to
    * the task by a specific type of task dependency and a lead time or lag time.
    */
   private static final int PREDECESSORS = 70;

   /**
    * The Priority field provides choices for the level of importance assigned to a
    * task, which in turn indicates how readily a task can be delayed or split
    * during resource leveling. The default priority is Medium. Those tasks with a
    * priority of Do Not Level are never delayed or split when Microsoft Project
    * levels tasks that have overallocated resources assigned.
    */
   private static final int PRIORITY = 95;

   /**
    * The Project field shows the name of the project from which a task originated.
    * This can be the name of the active project file. If there are other projects
    * inserted into the active project file, the name of the inserted project appears
    * in this field for the task.
    */
   private static final int PROJECT = 97;

   /**
    * The Remaining Cost field shows the remaining scheduled expense of a task
    * that will be incurred in completing the remaining scheduled work by all
    * resources assigned to the task.
    */
   private static final int REMAINING_COST = 33;

   /**
    * The Remaining Duration field shows the amount of time required to complete
    * the unfinished portion of a task.
    */
   private static final int REMAINING_DURATION = 43;

   /**
    * The Remaining Work field shows the amount of time, or person-hours,
    * still required by all assigned resources to complete a task.
    */
   private static final int REMAINING_WORK = 23;

   /**
    * The Resource Group field contains the list of resource groups to which the
    * resources assigned to a task belong.
    */
   private static final int RESOURCE_GROUP = 16;

   /**
    * The Resource Initials field lists the abbreviations for the names of resources
    * assigned to a task. These initials can serve as substitutes for the names.
    */
   private static final int RESOURCE_INITIALS = 73;

   /**
    * The Resource Names field lists the names of all resources assigned to a task.
    */
   private static final int RESOURCE_NAMES = 72;

   /**
    * The Resume field shows the date that the remaining portion of a task is scheduled
    * to resume after you enter a new value for the % Complete field. The Resume field
    * is also recalculated when the remaining portion of a task is moved to a new date.
    */
   private static final int RESUME = 151;

   /**
    * The Resume No Earlier than field constains the date which is the earliest time
    * to restart this task
    */
   private static final int RESUME_NO_EARLIER_THAN = 152;

   /**
    * For subtasks, the Rollup field indicates whether information on the subtask
    * Gantt bars will be rolled up to the summary task bar. For summary tasks, the
    * Rollup field indicates whether the summary task bar displays rolled up bars.
    * You must have the Rollup field for summary tasks set to Yes for any subtasks
    * to roll up to them.
    */
   private static final int ROLLUP = 84;

   /**
    * The Start field shows the date and time that a task is scheduled to begin.
    * You can enter the start date you want, to indicate the date when the task
    * should begin. Or, you can have Microsoft Project calculate the start date.
    */
   private static final int START = 50;

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
   private static final int START_VARIANCE = 66;

   /**
    * The Stop field shows the date that represents the end of the actual portion of a task.
    * Typically, Microsoft Project calculates the stop date. However, you can edit this date as well.
    */
   private static final int STOP = 150;

   /**
    * The Subproject File field contains the name of a project inserted into the active project file.
    * The Subproject File field contains the inserted project's path and file name.
    */
   private static final int SUBPROJECT_NAME = 96;

   /**
    * The Successors field lists the task ID numbers for the successor tasks to a task.
    * A task must start or finish before successor tasks can start or finish. Each successor
    * is linked to the task by a specific type of task dependency and a lead time or lag time.
    */
   private static final int SUCCESSORS = 71;

   /**
    * The Summary field indicates whether a task is a summary task.
    */
   private static final int SUMMARY = 120;

   /**
    * The SV (earned value schedule variance) field shows the difference in cost terms
    * between the current progress and the baseline plan of the task up to the status
    * date or today's date. You can use SV to check costs to determine whether tasks
    * are on schedule.
    */
   private static final int SV = 87;

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
   private static final int TOTAL_SLACK = 94;

   /**
    * The Unique ID field contains the number that Microsoft Project automatically designates
    * whenever a new task is created. This number indicates the sequence in which the task was
    * created, regardless of placement in the schedule.
    */
   private static final int UNIQUE_ID = 98;

   /**
    * The Unique ID Predecessors field lists the unique ID numbers for the predecessor
    * tasks on which a task depends before it can be started or finished. Each predecessor
    * is linked to the task by a specific type of task dependency and a lead time or lag time.
    */
   private static final int UNIQUE_ID_PREDECESSORS = 76;

   /**
    * The Unique ID Successors field lists the unique ID numbers for the successor tasks
    * to a task. A task must start or finish before successor tasks can start or finish.
    * Each successor is linked to the task by a specific type of task dependency and a
    * lead time or lag time.
    */
   private static final int UNIQUE_ID_SUCCESSORS = 75;

   /**
    * The Update Needed field indicates whether a TeamUpdate messageshould be sent to
    * the assigned resources because of changes to the start date, finish date, or
    * resource reassignments of the task.
    */
   private static final int UPDATE_NEEDED = 136;

   /**
    * The work breakdown structure code. The WBS field contains an alphanumeric code
    * you can use to represent the task's position within the hierarchical structure
    * of the project. This field is similar to the outline number, except that you can edit it.
    */
   private static final int WBS = 2;

   /**
    * The Work field shows the total amount of work scheduled to be performed on a task
    * by all assigned resources. This field shows the total work, or person-hours, for a task.
    */
   private static final int WORK = 20;

   /**
    *  The Work Variance field contains the difference between a task's baseline work and
    * the currently scheduled work.
    */
   private static final int WORK_VARIANCE = 24;

   /**
    * Maximum number of fields in this record. Note that this is package
    * access to allow the task model to get at it.
    */
   static final int MAX_FIELDS = 153;

   /**
    * The following constants are used purely to identify custom fields,
    * these field names are NOT written to the MPX file.
    */
   public static final int TEXT11 = 1011;
   public static final int TEXT12 = 1012;
   public static final int TEXT13 = 1013;
   public static final int TEXT14 = 1014;
   public static final int TEXT15 = 1015;
   public static final int TEXT16 = 1016;
   public static final int TEXT17 = 1017;
   public static final int TEXT18 = 1018;
   public static final int TEXT19 = 1019;
   public static final int TEXT20 = 1020;
   public static final int TEXT21 = 1021;
   public static final int TEXT22 = 1022;
   public static final int TEXT23 = 1023;
   public static final int TEXT24 = 1024;
   public static final int TEXT25 = 1025;
   public static final int TEXT26 = 1026;
   public static final int TEXT27 = 1027;
   public static final int TEXT28 = 1028;
   public static final int TEXT29 = 1029;
   public static final int TEXT30 = 1030;
   public static final int START6 = 1106;
   public static final int START7 = 1107;
   public static final int START8 = 1108;
   public static final int START9 = 1109;
   public static final int START10 = 1110;
   public static final int FINISH6 = 1206;
   public static final int FINISH7 = 1207;
   public static final int FINISH8 = 1208;
   public static final int FINISH9 = 1209;
   public static final int FINISH10 = 1210;
   public static final int COST4 = 1304;
   public static final int COST5 = 1305;
   public static final int COST6 = 1306;
   public static final int COST7 = 1307;
   public static final int COST8 = 1308;
   public static final int COST9 = 1309;
   public static final int COST10 = 1310;
   public static final int DATE1 = 1401;
   public static final int DATE2 = 1402;
   public static final int DATE3 = 1403;
   public static final int DATE4 = 1404;
   public static final int DATE5 = 1405;
   public static final int DATE6 = 1406;
   public static final int DATE7 = 1407;
   public static final int DATE8 = 1408;
   public static final int DATE9 = 1409;
   public static final int DATE10 = 1410;
   public static final int FLAG11 = 1511;
   public static final int FLAG12 = 1512;
   public static final int FLAG13 = 1513;
   public static final int FLAG14 = 1514;
   public static final int FLAG15 = 1515;
   public static final int FLAG16 = 1516;
   public static final int FLAG17 = 1517;
   public static final int FLAG18 = 1518;
   public static final int FLAG19 = 1519;
   public static final int FLAG20 = 1520;
   public static final int NUMBER6 = 1606;
   public static final int NUMBER7 = 1607;
   public static final int NUMBER8 = 1608;
   public static final int NUMBER9 = 1609;
   public static final int NUMBER10 = 1610;
   public static final int NUMBER11 = 1611;
   public static final int NUMBER12 = 1612;
   public static final int NUMBER13 = 1613;
   public static final int NUMBER14 = 1614;
   public static final int NUMBER15 = 1615;
   public static final int NUMBER16 = 1616;
   public static final int NUMBER17 = 1617;
   public static final int NUMBER18 = 1618;
   public static final int NUMBER19 = 1619;
   public static final int NUMBER20 = 1620;
   public static final int DURATION4 = 1704;
   public static final int DURATION5 = 1705;
   public static final int DURATION6 = 1706;
   public static final int DURATION7 = 1707;
   public static final int DURATION8 = 1708;
   public static final int DURATION9 = 1709;
   public static final int DURATION10 = 1710;
   public static final int OUTLINECODE1 = 1801;
   public static final int OUTLINECODE2 = 1802;
   public static final int OUTLINECODE3 = 1803;
   public static final int OUTLINECODE4 = 1804;
   public static final int OUTLINECODE5 = 1805;
   public static final int OUTLINECODE6 = 1806;
   public static final int OUTLINECODE7 = 1807;
   public static final int OUTLINECODE8 = 1808;
   public static final int OUTLINECODE9 = 1809;
   public static final int OUTLINECODE10 = 1810;

   /**
    * Constant containing the record number associated with this record.
    */
   static final int RECORD_NUMBER = 70;
}
