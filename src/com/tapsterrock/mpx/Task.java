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
public class Task extends MPXRecord
{
   /**
    * Default constructor.
    *
    * @param file the parent file to which this record belongs.
    */
   Task (MPXFile file, Task parent)
   {
      super (file);

      m_model = getParentFile().getTaskModel();

      m_parent = parent;

      if (file.getAutoWBS() == true)
      {
         String wbs = parent.getWBS();
         int index = wbs.lastIndexOf(".0");
         if (index != -1)
         {
            wbs = wbs.substring (0, index);
         }

         setWBS (wbs + "." + (parent.getChildTaskCount()+1));
      }

      if (file.getAutoOutlineLevel() == true)
      {
         Integer outline = parent.getOutlineLevel();
         setOutlineLevel (new Integer (outline.intValue()+1));
      }

      if (file.getAutoTaskUniqueID() == true)
      {
         setUniqueID (new Integer (file.getTaskUniqueID ()));
      }

      if (file.getAutoTaskID() == true)
      {
         setID (new Integer (file.getTaskID ()));
      }
   }


   /**
    * This constructor populates an instance of the Taks class
    * using values read in from an MPXFile record.
    *
    * @param file parent MPX file
    * @param record record from MPX file
    * @throws MPXException normally thrown for paring errors
    * @todo null pointer handling here must be reviewed
    */
   Task (MPXFile file, Record record)
      throws MPXException
   {
      super (file);
      m_model = getParentFile().getTaskModel();

      int x = 0;
      String field;

      int i = 0;
      int length = record.getLength();
      Iterator mod = m_model.iterator();

      while (i < length && mod.hasNext() == true)
      {
         x = ((Integer)mod.next()).intValue();
         field = record.getString(i++);

         switch (x)
         {
            case PREDECESSORS:
            case SUCCESSORS:
            case UNIQUE_ID_PREDECESSORS:
            case UNIQUE_ID_SUCCESSORS:
            {
               set (x, new RelationList (field));
               break;
            }

            case PERCENTAGE_COMPLETE:
            case PERCENTAGE_WORK_COMPLETE:
            {
               set (x, new MPXPercentage(field));
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
               set (x, new MPXCurrency(getParentFile().getCurrencyFormat(), field));
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
               set(x,new MPXDuration(field));
               break;
            }

            case ACTUAL_FINISH:
            case ACTUAL_START:
            case BASELINE_FINISH:
            case BASELINE_START:
            case CONSTRAINT_DATE:
            case CREATED:
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
               set(x, getParentFile().getDateFormat().parse(field));
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
               set (x, (field.equals("No")==true?Boolean.FALSE:Boolean.TRUE));
               break;
            }

            case CONSTRAINT_TYPE:
            {
               set (x, new ConstraintType (field));
               break;
            }

            case OBJECTS:
            case OUTLINE_LEVEL:
            case UNIQUE_ID:
            case ID:
            {
               set(x,Integer.valueOf(field));
               break;
            }

            case NUMBER1:
            case NUMBER2:
            case NUMBER3:
            case NUMBER4:
            case NUMBER5:
            {
               set (x, Double.valueOf(field));
               break;
            }

            case PRIORITY:
            {
               set (x, new Priority (field));
               break;
            }

            default:
            {
               set (x,field);
               break;
            }
         }
      }

      if (file.getAutoWBS() == true)
      {
         setWBS (Integer.toString(getParentFile().getChildTaskCount()+1) + ".0");
      }

      if (file.getAutoOutlineLevel() == true)
      {
         setOutlineLevel (new Integer (1));
      }

      if (file.getAutoTaskUniqueID() == true)
      {
         setUniqueID (new Integer (file.getTaskUniqueID ()));
      }

      if (file.getAutoTaskID() == true)
      {
         setID (new Integer (file.getTaskID ()));
      }
   }

   /**
    * This method is used to add notes to the current task.
    *
    * @return TaskNotes object
    * @throws MPXException if maximum number of task notes is exceeded
    */
   public TaskNotes addTaskNotes ()
      throws MPXException
   {
      return (addTaskNotes(""));
   }

   /**
    * This method is used to add notes to the current task.
    *
    * @param notes notes to be added
    * @return TaskNotes object
    * @throws MPXException if maximum number of task notes is exceeded
    */
   public TaskNotes addTaskNotes (String notes)
      throws MPXException
   {
      if (m_notes != null)
      {
         throw new MPXException (MPXException.MAXIMUM_RECORDS);
      }

      m_notes = new TaskNotes(getParentFile());

      m_notes.setNotes (notes);

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
         throw new MPXException (MPXException.MAXIMUM_RECORDS);
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
      MPXFile parent = getParentFile ();

      Task task = new Task (parent, this);

      m_children.add (task);

      parent.addTask (task);

      return (task);
   }

   /**
    * This method is used to associate a child task with the current
    * task instance. It has package access, and has been designed to
    * allow the hierarchical outline structure of tasks in an MPX
    * file to be constructed as the file is read in.
    *
    * @param child child task
    */
   void addChildTask (Task child, Integer childOutlineLevel)
      throws MPXException
   {
      Integer outlineLevel = getOutlineLevel ();
      if (outlineLevel == null)
      {
         throw new MPXException (MPXException.INVALID_OUTLINE);
      }

      if (outlineLevel.intValue()+1 == childOutlineLevel.intValue())
      {
         m_children.add (child);
      }
      else
      {
         if (m_children.isEmpty() == true)
         {
            throw new MPXException (MPXException.INVALID_OUTLINE);
         }

         ((Task)m_children.getLast()).addChildTask(child, childOutlineLevel);
      }
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
      if (m_recurring != null)
      {
         throw new MPXException (MPXException.MAXIMUM_RECORDS);
      }

      m_recurring = new RecurringTask (getParentFile());

      return (m_recurring);
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
      if (m_recurring != null)
      {
         throw new MPXException (MPXException.MAXIMUM_RECORDS);
      }

      m_recurring = new RecurringTask (getParentFile(), record);

      return (m_recurring);
   }


   /**
    * This method allows a resource assignment to be added to the
    * current task.
    *
    * @return ResourceAssignment object
    * @throws MPXException thrown if more than the maximum permitted assignments is added
    */
   public ResourceAssignment addResourceAssignment ()
      throws MPXException
   {
      if (m_assignments.size() == MAX_RESOURCE_ASSIGNMENTS)
      {
         throw new MPXException (MPXException.MAXIMUM_RECORDS);
      }

      ResourceAssignment tra = new ResourceAssignment(getParentFile());

      m_assignments.add (tra);

      return (tra);
   }

   /**
    * This method allows a resource assignment to be added to the
    * current task.
    *
    * @param resource the resource to assign
    * @return ResourceAssignment object
    * @throws MPXException thrown if more than the maximum permitted assignments is added
    */
   public ResourceAssignment addResourceAssignment (Resource resource)
      throws MPXException
   {
      Iterator iter = m_assignments.iterator();
      ResourceAssignment assignment = null;
      Integer resourceUniqueID = resource.getUniqueID();
      Integer uniqueID;

      while (iter.hasNext() == true)
      {
         assignment = (ResourceAssignment)iter.next();
         uniqueID = assignment.getResourceUniqueID();
         if (uniqueID != null && uniqueID.intValue() == resourceUniqueID.intValue())
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
         assignment = addResourceAssignment ();
         assignment.setID(resource.getID());
         assignment.setResourceUniqueID(resourceUniqueID);
         assignment.setWork(getDuration());
         assignment.setUnits(ResourceAssignment.DEFAULT_UNITS);
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
    * @throws MPXException thrown if more than the maximum permitted assignments is added
    */
   ResourceAssignment addResourceAssignment (Record record)
     throws MPXException
   {
      if (m_assignments.size() == MAX_RESOURCE_ASSIGNMENTS)
      {
         throw new MPXException (MPXException.MAXIMUM_RECORDS);
      }

      ResourceAssignment tra = new ResourceAssignment (getParentFile(), record);

      m_assignments.add(tra);

      return (tra);
   }

   /**
    * This method allows a predecessor relationship to be added to this
    * task instance.
    *
    * @return relationship
    */
   public Relation addPredecessor ()
   {
      return (addPredecessor (null));
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
         list = new RelationList ();
         set (PREDECESSORS, list);
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
            if (rel.getID() == task.getID().intValue())
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
         rel = new Relation ();

         if (task != null)
         {
            rel.setID(task.getID().intValue());
         }

         list.add (rel);
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
         list = new RelationList ();
         set (UNIQUE_ID_PREDECESSORS, list);
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
            if (rel.getID() == task.getUniqueID().intValue())
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
         rel = new Relation ();

         if (task != null)
         {
            rel.setID(task.getUniqueID().intValue());
         }

         list.add (rel);
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
         list = new RelationList ();
         set (SUCCESSORS, list);
      }

      Relation rel = new Relation ();

      if (task != null)
      {
         rel.setID(task.getID().intValue());
      }

      list.add (rel);

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
         list = new RelationList ();
         set (UNIQUE_ID_SUCCESSORS, list);
      }

      Relation rel = new Relation ();

      if (task != null)
      {
         rel.setID(task.getUniqueID().intValue());
      }

      list.add (rel);

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
   private void set(int field, Object val)
   {
      Integer key = new Integer (field);
      m_model.add (key);
      put (key, val);
   }

   /**
    * This method is used to set the value of a date field in the task,
    * and also to ensure that the field exists in the task model
    * record.
    *
    * @param field field to be added or updated.
    * @param val new value for field.
    */
   private void setDate (int field, Date val)
   {
      Integer key = new Integer (field);
      m_model.add(key);
      putDate (key, val);
   }

   /**
    * This method is used to set the value of a currency field in the task,
    * and also to ensure that the field exists in the task model
    * record.
    *
    * @param field field to be added or updated.
    * @param val new value for field.
    */
   private void setCurrency (int field, Number val)
   {
      Integer key = new Integer (field);
      m_model.add(key);
      putCurrency (key, val);
   }

   /**
    * This method is used to retrieve a particular field value.
    *
    * @param field requested field
    * @return field value
    */
   private Object get (int field)
   {
      return (get(new Integer(field)));
   }


   /**
    * The % Complete field contains the current status of a task, expressed
    * as the percentage of the
    * task's duration that has been completed. You can enter percent complete,
    * or you can have
    * Microsoft Project calculate it for you based on actual duration.
    *
    * @param val - value to be set
    */
   public void setPercentageComplete (MPXPercentage val)
   {
      set (PERCENTAGE_COMPLETE, val);
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
    * @param val - value to be set
    */
   public void setPercentageWorkComplete (MPXPercentage val)
   {
      set (PERCENTAGE_WORK_COMPLETE, val);
   }

   /**
    * The Actual Cost field shows costs incurred for work already performed
    * by all resources
    * on a task, along with any other recorded costs associated with the task.
    * You can enter
    * all the actual costs or have Microsoft Project calculate them for you.
    *
    * @param val - value to be set
    */
   public void setActualCost (Number val)
   {
      setCurrency (ACTUAL_COST, val);
   }

   /**
    * The Actual Duration field shows the span of actual working time for a
    * task so far,
    * based on the scheduled duration and current remaining work or
    * completion percentage.
    *
    * @param val - value to be set
    */
   public void setActualDuration (MPXDuration val)
   {
      set (ACTUAL_DURATION, val);
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
    * @param val - value to be set
    */
   public void setActualFinish (Date val)
   {
      setDate (ACTUAL_FINISH, val);
   }

   /**
    * The Actual Start field shows the date and time that a task actually began.
    * When a task is first created, the Actual Start field contains "NA." Once you
    * enter the first actual work or a completion percentage for a task, Microsoft
    * Project sets the actual start date to the scheduled start date.
    * @param val - value to be set
    */
   public void setActualStart (Date val)
   {
      setDate (ACTUAL_START, val);
   }

   /**
    * The Actual Work field shows the amount of work that has already been
    * done by the
    * resources assigned to a task.
    * @param val - value to be set
    */
   public void setActualWork (MPXDuration val)
   {
      set (ACTUAL_WORK, val);
   }

   /**
    * The Baseline Cost field shows the total planned cost for a task.
    * Baseline cost is also referred to as budget at completion (BAC).
    * @param val - the amount to be set
    */
   public void setBaselineCost (Number val)
   {
      setCurrency (BASELINE_COST, val);
   }

   /**
    * The Baseline Duration field shows the original span of time planned to
    * complete a task.
    * @param val - duration
    */
   public void setBaselineDuration (MPXDuration val)
   {
      set (BASELINE_DURATION, val);
   }

   /**
    * The Baseline Finish field shows the planned completion date for a
    * task at the time
    * you saved a baseline. Information in this field becomes available
    * when you set a
    * baseline for a task.
    *
    * @param val - Date to be set
    */
   public void setBaselineFinish (Date val)
   {
      setDate (BASELINE_FINISH, val);
   }

   /**
    * The Baseline Start field shows the planned beginning date for a task at
    * the time
    * you saved a baseline. Information in this field becomes available when you
    * set a baseline.
    *
    * @param val - Date to be set
    */
   public void setBaselineStart (Date val)
   {
      setDate (BASELINE_START, val);
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
    * @param val - the duration to be set.
    */
    public void setBaselineWork (MPXDuration val)
    {
       set (BASELINE_WORK, val);
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
     * @param val - the ampunt to be set
     */
   public void setBCWP (Number val)
   {
      setCurrency (BCWP, val);
   }


   /**
    * The BCWS (budgeted cost of work scheduled) field contains the cumulative
    * timephased baseline costs up to the status date or today's date.
    * @param val - the amount to set.
    */
   public void setBCWS (Number val)
   {
      setCurrency (BCWS, val);
   }

   /**
    * The Confirmed field indicates whether all resources assigned to a task have
    * accepted or rejected the task assignment in response to a TeamAssign message
    * regarding their assignments.
    * @param val - boolean value
    */
   public void setConfirmed (Boolean val)
   {
      set (CONFIRMED, val);
   }

   /**
    * The Constraint Date field shows the specific date associated with certain
    * constraint types,
    *  such as Must Start On, Must Finish On, Start No Earlier Than,
    *  Start No Later Than,
    *  Finish No Earlier Than, and Finish No Later Than.
    *  SEE class constants
    *
    * @param val - Date to be set
    */
   public void setConstraintDate (Date val)
   {
      setDate (CONSTRAINT_DATE, val);
   }

   /**
    * Private method for dealing with string parameters from File
    * @param type string constraint type
    */
   public void setConstraintType (ConstraintType type)
   {
      set (CONSTRAINT_TYPE, type);
   }

   /**
    * The Contact field contains the name of an individual
    * responsible for a task.
    * @param val - value to be set
    */
   public void setContact (String val)
   {
      set (CONTACT, val);
   }

   /**
    * The Cost field shows the total scheduled, or projected, cost for a task,
    * based on costs already incurred for work performed by all resources assigned
    * to the task, in addition to the costs planned for the remaining work for the
    * assignment. This can also be referred to as estimate at completion (EAC).
    * @param val - amount
    */
   public void setCost (Number val)
   {
      setCurrency (COST, val);
   }

   /**
    * The Cost1-10 fields show any custom task cost information you want to enter
    * in your project.
    *
    * @param val -amount
    */
   public void setCost1(Number val)
   {
      setCurrency (COST1, val);
   }

   /**
    * The Cost1-10 fields show any custom task cost information you want to
    * enter in your project.
    *
    * @param val -amount
    */
   public void setCost2 (Number val)
   {
      setCurrency (COST2, val);
   }

   /**
    * The Cost1-10 fields show any custom task cost information you want to
    * enter in your project.
    *
    * @param val -amount
    */
   public void setCost3 (Number val)
   {
      setCurrency (COST3, val);
   }

   /**
    * The Cost Variance field shows the difference between the
    * baseline cost and total
    * cost for a task. The total cost is the current estimate of
    * costs based on actual
    * costs and remaining costs. This is also referred to as variance
    * at completion (VAC).
    *
    * @param val  amount
    */
   public void setCostVariance (Number val)
   {
      setCurrency (COST_VARIANCE, val);
   }

   /**
    * The Created field contains the date and time when a task was
    * added to the project.
    *
    * @param val - date
    */
   public void setCreated (Date val)
   {
      setDate (CREATED, val);
   }

   /**
    * The Critical field indicates whether a task has any room in the
    * schedule to slip,
    * or if a task is on the critical path. The Critical field contains
    * Yes if the task
    * is critical and No if the task is not critical.
    *
    * @param val - whether task is critical or not
    */
   public void setCritical (Boolean val)
   {
      set (CRITICAL, val);
   }

   /**
    * The CV (earned value cost variance) field shows the difference
    * between how much
    * it should have cost to achieve the current level of completion
    * on the task, and
    * how much it has actually cost to achieve the current level of
    * completion up to
    * the status date or today's date.
    *
    * @param val - value to set
    */
   public void setCV (Number val)
   {
      setCurrency (CV, val);
   }

   /**
    * Set amount of delay as elapsed real time
    *
    * @param val elapsed time
    */
   public void setDelay (MPXDuration val)
   {
      set (DELAY, val);
   }

   /**
    * The Duration field is the total span of active working time for a task.
    * This is generally the amount of time from the start to the finish of a task.
    * The default for new tasks is 1 day (1d).
    *
    * @param val - duration
    */
   public void setDuration (MPXDuration val)
   {
      set (DURATION, val);
   }

   /**
    * The Duration1 through Duration10 fields are custom fields that show
    * any specialized
    * task duration information you want to enter and store
    * separately in your project.
    * @param val - duration
    */
   public void setDuration1 (MPXDuration val)
   {
      set (DURATION1, val);
   }

   /**
    * The Duration1 through Duration10 fields are custom fields that show
    * any specialized
    * task duration information you want to enter and store separately
    * in your project.
    *
    * @param val - duration
    */
   public void setDuration2 (MPXDuration val)
   {
      set (DURATION2, val);
   }

   /**
    * The Duration1 through Duration10 fields are custom fields that show
    * any specialized
    * task duration information you want to enter and store separately
    * in your project.
    * @param val - duration
    */
   public void setDuration3 (MPXDuration val)
   {
      set (DURATION3, val);
   }


   /**
    * The Duration Variance field contains the difference between the
    * baseline duration
    * of a task and the total duration (current estimate) of a task.
    *
    * @param val - duration
    */
   public void setDurationVariance (MPXDuration val)
   {
      set (DURATION_VARIANCE, val);
   }

   /**
    * The Early Finish field contains the earliest date that a task
    * could possibly finish,
    * based on early finish dates of predecessor and successor tasks,
    * other constraints,
    * and any leveling delay.
    *
    * @param val - Date
    */
   public void setEarlyFinish (Date val)
   {
      setDate (EARLY_FINISH, val);
   }

   /**
    * The Early Start field contains the earliest date that a task could
    * possibly begin,
    * based on the early start dates of predecessor and successor tasks,
    * and other constraints.
    *
    * @param val - Date
    */
   public void setEarlyStart (Date val)
   {
      setDate (EARLY_START, val);
   }

   /**
    * The Finish field shows the date and time that a task is scheduled to be
    * completed.
    * You can enter the finish date you want, to indicate the date when the
    * task should
    * be completed. Or, you can have Microsoft Project calculate the finish date.
    *
    * @param val - Date
    */
   public void setFinish (Date val)
   {
      setDate (FINISH, val);
   }

   /**
    * The Finish1 through Finish10 fields are custom fields that show any
    * specific task
    * finish date information you want to enter and store separately in
    * your project.
    *
    * @param val - Date
    */
   public void setFinish1 (Date val)
   {
      setDate (FINISH1, val);
   }

   /**
    * The Finish1 through Finish10 fields are custom fields that show any
    * specific task
    * finish date information you want to enter and store separately in
    * your project.
    *
    * @param val - Date
    */
   public void setFinish2 (Date val)
   {
      setDate (FINISH2, val);
   }

   /**
    * The Finish1 through Finish10 fields are custom fields that show any
    * specific task
    * finish date information you want to enter and store separately in
    * your project.
    *
    * @param val - Date
    */
   public void setFinish3 (Date val)
   {
      setDate (FINISH3, val);
   }

   /**
    * The Finish1 through Finish10 fields are custom fields that show any
    * specific task
    * finish date information you want to enter and store separately in
    * your project.
    *
    * @param val - Date
    */
   public void setFinish4 (Date val)
   {
      setDate (FINISH4, val);
   }

   /**
    * The Finish1 through Finish10 fields are custom fields that show any
    * specific task
    * finish date information you want to enter and store separately in
    * your project.
    *
    * @param val - Date
    */
   public void setFinish5 (Date val)
   {
      setDate (FINISH5, val);
   }

   /**
    * The Finish Variance field contains the amount of time that represents the
    * difference between a task's baseline finish date and its current finish date.
    * @param val - duration
    */
   public void setFinishVariance (MPXDuration val)
   {
      set (FINISH_VARIANCE, val);
   }

   /**
    * Flag indicatig whether or not  the amount of work is a fixed value
    * and any changes to
    * the task's duration or the number of assigned units (or resources)
    * don't impact the
    * task's work.
    * Work = Duration x Units
    *
    * @param val - value to be set
    */
   public void setFixed (Boolean val)
   {
      set (FIXED, val);
   }

   /**
    * The Fixed Cost field shows any task expense that is not associated
    * with a resource cost.
    *
    * @param val - amount
    */
   public void setFixedCost (Number val)
   {
      setCurrency (FIXED_COST, val);
   }

   /**
    * The Flag1-20 fields indicate whether a task is marked for further action or
    * identification of some kind. To mark a task, click Yes in a Flag field.
    * If you don't want a task marked, click No.
    *
    * @param val - boolena
    */
   public void setFlag1 (Boolean val)
   {
      set (FLAG1, val);
   }

   /**
    * The Flag1-20 fields indicate whether a task is marked for further action or
    * identification of some kind. To mark a task, click Yes in a Flag field.
    * If you don't want a task marked, click No.
    *
    * @param val - boolean
    */
   public void setFlag2 (Boolean val)
   {
      set (FLAG2, val);
   }

   /**
    * The Flag1-20 fields indicate whether a task is marked for further action or
    * identification of some kind. To mark a task, click Yes in a Flag field.
    * If you don't want a task marked, click No.
    *
    * @param val - boolean
    */
   public void setFlag3(Boolean val)
   {
      set (FLAG3, val);
   }

   /**
    * The Flag1-20 fields indicate whether a task is marked for further action or
    * identification of some kind. To mark a task, click Yes in a Flag field.
    * If you don't want a task marked, click No.
    * @param val - boolean
    */
   public void setFlag4 (Boolean val)
   {
      set (FLAG4, val);
   }

   /**
    * The Flag1-20 fields indicate whether a task is marked for further action or
    * identification of some kind. To mark a task, click Yes in a Flag field.
    * If you don't want a task marked, click No.
    * @param val - boolena
    */
   public void setFlag5 (Boolean val)
   {
      set (FLAG5, val);
   }

   /**
    * The Flag1-20 fields indicate whether a task is marked for further action or
    * identification of some kind. To mark a task, click Yes in a Flag field.
    * If you don't want a task marked, click No.
    * @param val - boolena
    */
   public void setFlag6 (Boolean val)
   {
      set (FLAG6, val);
   }

   /**
    * The Flag1-20 fields indicate whether a task is marked for further action or
    * identification of some kind. To mark a task, click Yes in a Flag field.
    * If you don't want a task marked, click No.
    * @param val - boolena
    */
   public void setFlag7 (Boolean val)
   {
      set (FLAG7, val);
   }

   /**
    * The Flag1-20 fields indicate whether a task is marked for further action or
    * identification of some kind. To mark a task, click Yes in a Flag field.
    * If you don't want a task marked, click No.
    * @param val - boolena
    */
   public void setFlag8 (Boolean val)
   {
      set (FLAG8, val);
   }

   /**
    * The Flag1-20 fields indicate whether a task is marked for further action or
    * identification of some kind. To mark a task, click Yes in a Flag field.
    * If you don't want a task marked, click No.
    * @param val - boolena
    */
   public void setFlag9 (Boolean val)
   {
      set (FLAG9, val);
   }

   /**
    * The Flag1-20 fields indicate whether a task is marked for further action or
    * identification of some kind. To mark a task, click Yes in a Flag field.
    * If you don't want a task marked, click No.
    * @param val - boolena
    */
   public void setFlag10 (Boolean val)
   {
      set (FLAG10, val);
   }

   /**
    * The Free Slack field contains the amount of time that a task can be delayed
    * without delaying any successor tasks. If the task has no successors, free
    * slack is the amount of time that a task can be delayed without delaying
    * the entire project's finish date.
    *
    * @param val - duration
    */
   public void setFreeSlack (MPXDuration val)
   {
      set (FREE_SLACK, val);
   }

   /**
    * The Hide Bar field indicates whether the Gantt bars and Calendar bars
    * for a task are hidden. Click Yes in the Hide Bar field to hide the bar
    * for the task. Click No in the Hide Bar field to show the bar for the task.
    * @param val - boolean
    */
   public void setHideBar (Boolean val)
   {
      set (HIDE_BAR, val);
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
      set (ID, val);
   }

   /**
    * The Late Finish field contains the latest date that a task can finish without
    * delaying the finish of the project. This date is based on the task's late
    * start date, as well as the late start and late finish dates of predecessor
    * and successor tasks, and other constraints.
    * @param val - Date
    */
   public void setLateFinish (Date val)
   {
      setDate (LATE_FINISH, val);
   }

   /**
    * The Late Start field contains the latest date that a task can start without
    * delaying the finish of the project. This date is based on the
    * task's start date,
    * as well as the late start and late finish dates of predecessor
    * and successor tasks,
    * and other constraints.
    *
    * @param val - Date
    */
   public void setLateStart (Date val)
   {
      setDate (LATE_START, val);
   }

   /**
    * The Linked Fields field indicates whether there are OLE links to the task,
    * either from elsewhere in the active project, another Microsoft Project file,
    * or from another program.
    *
    * @param val - boolean
    */
   public void setLinkedFields (Boolean val)
   {
      set (LINKED_FIELDS, val);
   }

   /**
    * The Marked field indicates whether a task is marked for further action or
    * identification of some kind. To mark a task, click Yes in the Marked field.
    * If you don't want a task marked, click No.
    *
    * @param val - boolean
    */
   public void setMarked (Boolean val)
   {
      set (MARKED, val);
   }

   /**
    * The Milestone field indicates whether a task is a milestone.
    *
    * @param val - boolean
    */
   public void setMilestone (Boolean val)
   {
      set (MILESTONE, val);
   }

   /**
    * The Name field contains the name of a task.
    *
    * @param val - value to set as name
    */
   public void setName (String val)
   {
      set (NAME, val);
   }

   /**
    * The Notes field contains notes that you can enter about a task.
    * You can use task notes to help maintain a history for a task.
    *
    * @param val - string
    */
   public void setNotes (String val)
   {
      set (NOTES, val);
   }

   /**
    * The Number1-20 fields show any custom numeric information you enter
    * in your project regarding tasks.
    *
    * @param val - string
    */
   public void setNumber1 (Double val)
   {
      set (NUMBER1, val);
   }

   /**
    * The Number1-20 fields show any custom numeric information you enter
    * in your project regarding tasks.
    *
    * @param val - string
    */
   public void setNumber2 (Double val)
   {
      set (NUMBER2, val);
   }

   /**
    * The Number1-20 fields show any custom numeric information you enter
    * in your project regarding tasks.
    *
    * @param val - string
    */
   public void setNumber3 (Double val)
   {
      set (NUMBER3, val);
   }

   /**
    * The Number1-20 fields show any custom numeric information you enter
    * in your project regarding tasks.
    *
    * @param val - string
    */
   public void setNumber4 (Double val)
   {
      set (NUMBER4, val);
   }

   /**
    * The Number1-20 fields show any custom numeric information you enter
    * in your project regarding tasks.
    *
    * @param val - string
    */
   public void setNumber5 (Double val)
   {
      set (NUMBER5, val);
   }

   /**
    * The Objects field contains the number of objects attached to a task.
    *
    * @param val - integer value
    */
   public void setObjects (Integer val)
   {
      set (OBJECTS, val);
   }

   /**
    * The Outline Level field contains the number that indicates the level of
    * the task in the project outline hierarchy.
    *
    * @param val - int
    */
   public void setOutlineLevel (Integer val)
   {
      set (OUTLINE_LEVEL, val);
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
      set (OUTLINE_NUMBER, val);
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
      set (PREDECESSORS, list);
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
      set (PRIORITY, priority);
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
      set (PROJECT, val);
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
      setCurrency (REMAINING_COST, val);
   }

   /**
    * The Remaining Duration field shows the amount of time required to complete
    * the unfinished portion of a task.
    *
    * @param val - duration.
    */
   public void setRemainingDuration (MPXDuration val)
   {
      set (REMAINING_DURATION, val);
   }

   /**
    * The Remaining Work field shows the amount of time, or person-hours,
    * still required by all assigned resources to complete a task.
    * @param val  - duration
    */
   public void setRemainingWork (MPXDuration val)
   {
      set (REMAINING_WORK, val);
   }

   /**
    * The Resource Group field contains the list of resource groups to which the
    * resources assigned to a task belong.
    *
    * @param val - String list
    */
   public void setResourceGroup (String val)
   {
      set (RESOURCE_GROUP, val);
   }

   /**
    * The Resource Initials field lists the abbreviations for the names of
    * resources assigned to a task. These initials can serve as substitutes
    * for the names.
    *
    * @param val - text list
    */
   public void setResourceInitials (String val)
   {
      set (RESOURCE_INITIALS, val);
   }

   /**
    * The Resource Names field lists the names of all resources
    * assigned to a task.
    *
    * @param val - text list
    */
   public void setResourceNames (String val)
   {
      set (RESOURCE_NAMES, val);
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
      setDate (RESUME, val);
   }

   /**
    * No help info. Earliest possible resume date?
    * @param val - Date
    */
   public void setResumeNoEarlierThan (Date val)
   {
      setDate (RESUME_NO_EARLIER_THAN, val);
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
   public void setRollup(Boolean val)
   {
      set (ROLLUP, val);
   }

   /**
    * The Start field shows the date and time that a task is scheduled to begin.
    * You can enter the start date you want, to indicate the date when the task
    * should begin. Or, you can have Microsoft Project calculate the start date.
    * @param val - Date
    */
   public void setStart (Date val)
   {
      setDate (START, val);
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
      setDate (START1, val);
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
      setDate (START2, val);
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
      setDate (START3, val);
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
      setDate (START4, val);
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
      setDate (START5, val);
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
      set (START_VARIANCE, val);
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
      setDate (STOP, val);
   }

   /**
    * The Subproject File field contains the name of a project inserted into
    * the active project file. The Subproject File field contains the inserted
    * project's path and file name.
    *
    * @param val - String
    */
   public void setSubprojectFile (String val)
   {
      set (SUBPROJECT_FILE, val);
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
      set (SUCCESSORS, list);
   }


   /**
    * The Summary field indicates whether a task is a summary task.
    *
    * @param val - boolean
    */
   public void setSummary (Boolean val)
   {
      set (SUMMARY, val);
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
      setCurrency (SV, val);
   }

   /**
    * The Text1-30 fields show any custom text information you want to enter
    * in your project about tasks.
    *
    * @param val - String
    */
   public void setText1 (String val)
   {
      set (TEXT1, val);
   }

   /**
    * The Text1-30 fields show any custom text information you want to
    * enter in your project about tasks.
    *
    * @param val - String
    */
   public void setText2 (String val)
   {
      set (TEXT2, val);
   }

   /**
    * The Text1-30 fields show any custom text information you want to
    * enter in your project about tasks.
    *
    * @param val - String
    */
   public void setText3 (String val)
   {
      set (TEXT3, val);
   }

   /**
    * The Text1-30 fields show any custom text information you want to
    * enter in your project about tasks.
    *
    * @param val - String
    */
   public void setText4 (String val)
   {
      set (TEXT4, val);
   }

   /**
    * The Text1-30 fields show any custom text information you want to
    * enter in your project about tasks.
    *
    * @param val - String
    */
   public void setText5 (String val)
   {
      set (TEXT5, val);
   }

   /**
    * The Text1-30 fields show any custom text information you want to
    * enter in your project about tasks.
    *
    * @param val - String
    */
   public void setText6 (String val)
   {
      set (TEXT6, val);
   }

   /**
    * The Text1-30 fields show any custom text information you want to
    * enter in your project about tasks.
    *
    * @param val - String
    */
   public void setText7 (String val)
   {
      set (TEXT7, val);
   }

   /**
    * The Text1-30 fields show any custom text information you want to
    * enter in your project about tasks.
    *
    * @param val - String
    */
   public void setText8 (String val)
   {
      set (TEXT8, val);
   }

   /**
    * The Text1-30 fields show any custom text information you want to
    * enter in your project about tasks.
    *
    * @param val - String
    */
   public void setText9 (String val)
   {
      set (TEXT9, val);
   }

   /**
    * The Text1-30 fields show any custom text information you want to
    * enter in your project about tasks.
    *
    * @param val - String
    */
   public void setText10 (String val)
   {
      set (TEXT10, val);
   }

   /**
    * The Total Slack field contains the amount of time a task can be delayed
    * without delaying the project's finish date.
    *
    * @param val - duration
    */
   public void setTotalSlack (MPXDuration val)
   {
      set (TOTAL_SLACK, val);
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
      set (UNIQUE_ID, val);
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
      set (UNIQUE_ID_PREDECESSORS, list);
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
      set (UNIQUE_ID_SUCCESSORS, list);
   }

   /**
    * The Update Needed field indicates whether a TeamUpdate messageshould
    * be sent to the assigned resources because of changes to the start date,
    * finish date, or resource reassignments of the task.
    *
    * @param val - boolean
    */
   public void setUpdateNeeded (Boolean val)
   {
      set (UPDATE_NEEDED, val);
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
      set (WBS, val);
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
      set (WORK, val);
   }

   /**
    * The Work Variance field contains the difference between a task's baseline
    * work and the currently scheduled work.
    *
    * @param val - duration
    */
   public void setWorkVariance (MPXDuration val)
   {
      set (WORK_VARIANCE, val);
   }


   /**
    * The % Complete field contains the current status of a task,
    * expressed as the percentage of the task's duration that has been completed.
    * You can enter percent complete, or you can have Microsoft Project calculate
    * it for you based on actual duration.
    * @return - percentage as float
    */
   public MPXPercentage getPercentageComplete ()
   {
      return ((MPXPercentage)get(PERCENTAGE_COMPLETE));
   }

   /**
    * The % Work Complete field contains the current status of a task,
    * expressed as the percentage of the task's work that has been completed.
    * You can enter percent work complete, or you can have Microsoft Project
    * calculate it for you based on actual work on the task.
    *
    * @return - percentage as float
    */
   public MPXPercentage getPercentageWorkComplete ()
   {
      return ((MPXPercentage)get(PERCENTAGE_WORK_COMPLETE));
   }


   /**
    * The Actual Cost field shows costs incurred for work already performed
    * by all resources on a task, along with any other recorded costs associated
    * with the task. You can enter all the actual costs or have Microsoft Project
    * calculate them for you.
    *
    * @return - currency amount as float
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
    * @return - duration string
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
    * @return - Date
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
    * @return - Date
    */
   public Date getActualStart ()
   {
      return ((Date) get(ACTUAL_START));
   }

   /**
    * The Actual Work field shows the amount of work that has already been done
    * by the resources assigned to a task.
    *
    * @return - duration string
    */
   public MPXDuration getActualWork ()
   {
      return ((MPXDuration)get(ACTUAL_WORK));
   }

   /**
    * The Baseline Cost field shows the total planned cost for a task.
    * Baseline cost is also referred to as budget at completion (BAC).
    * @return - currency amount as float
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
    * @return - Date
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
    * @return - Date
    */
   public Date getBaselineStart ()
   {
      return ((Date) get(BASELINE_START));
   }

   /**
    * The Baseline Work field shows the originally planned amount of work to be
    * performed by all resources assigned to a task. This field shows the planned
    * person-hours scheduled for a task. Information in the Baseline Work field
    * becomes available when you set a baseline for the project.
    *
    * @return - MPXDuration
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
    * @return - currency amount as float
    */
   public Number getBCWP ()
   {
      return ((Number)get(BCWP));
   }

   /**
    * The BCWS (budgeted cost of work scheduled) field contains the cumulative
    * timephased baseline costs up to the status date or today's date.
    *
    * @return - currency amount as float
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
    * @return - boolean
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
    * @return - Date
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
    * @return - String
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
    * @return - Date
    */
   public Date getCreated ()
   {
      return ((Date)get(CREATED));
   }

   /**
    * The Critical field indicates whether a task has any room in the schedule
    * to slip, or if a task is on the critical path. The Critical field contains
    * Yes if the task is critical and No if the task is not critical.
    *
    * @return - boolean
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
    * @return - MPXDuration
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
    * @return - MPXDuration
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
    * @return - MPXDuration
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
    * @return - MPXDuration
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
    * @return - MPXDuration
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
    * @return - Date
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
    * @return - Date
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
    * @return - Date
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
    * @return - Date
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
    * @return - Date
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
    * @return - Date
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
    * @return - Date
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
    * @return - Date
    */
   public Date getFinish5 ()
   {
      return ((Date)get(FINISH5));
   }

   /**
    * This field will be ignored on input into MS Project
    *
    * @return - String
    */
   public String getFinishVariance ()
   {
      return ((String)get(FINISH_VARIANCE));
   }

   /**
    * Assuming this field is boolean. Referring to whether
    * or not task is of fixed duration.
    *
    * @return - boolean
    */
   public Boolean getFixed ()
   {
      return ((Boolean)get(FIXED));
   }

   /**
    * The Fixed Cost field shows any task expense that is not associated
    * with a resource cost.
    *
    * @return - currenct amount as float
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
    * @return - boolean
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
    * @return - boolean
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
    * @return - boolean
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
    * @return - boolean
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
    * @return - boolean
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
    * @return - boolean
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
    * @return - boolean
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
    * @return - boolean
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
    * @return - boolean
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
    * @return - boolean
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
    * @return - MPXDuration
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
    * @return - boolean
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
    * @return - Date
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
    * @return - Date
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
   public Boolean getMarked ()
   {
      return ((Boolean)get(MARKED));
   }

   /**
    * The Milestone field indicates whether a task is a milestone
    *
    * @return - boolean
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
      return ((String)get(NOTES));
   }

   /**
    * The Number fields show any custom numeric information you
    * enter in your project regarding tasks. eg dept.no.
    *
    * @return String of numeric info.
    */
   public Double getNumber1 ()
   {
      return ((Double)get(NUMBER1));
   }

   /**
    * The Number fields show any custom numeric information you enter in
    * your project regarding tasks. eg dept.no.
    *
    * @return String of numeric info.
    */
   public Double getNumber2 ()
   {
      return ((Double)get(NUMBER2));
   }

   /**
    * The Number fields show any custom numeric information you enter
    * in your project regarding tasks. eg dept.no.
    *
    * @return String of numeric info.
    */
   public Double getNumber3 ()
   {
      return ((Double)get(NUMBER3));
   }

   /**
    * The Number fields show any custom numeric information you enter
    * in your project regarding tasks. eg dept.no.
    *
    * @return String of numeric info.
    */
   public Double getNumber4 ()
   {
      return ((Double)get(NUMBER4));
   }

   /**
    * The Number fields show any custom numeric information you enter in
    * your project regarding tasks. eg dept.no.
    *
    * @return String of numeric info.
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
    * @return - int
    */
   public Integer getObjects ()
   {
      return ((Integer)get(OBJECTS));
   }

   /**
    * The Outline Level field contains the number that indicates the level
    * of the task in the project outline hierarchy.
    *
    * @return - int
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
    * @return - String
    */
   public String getOutlineNumber ()
   {
      return ((String)get(OUTLINE_NUMBER));
   }

   /**
    * List of predecessors. Dealt with here as a single string list.
    *
    * @return - String
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
    * @return - MPXDuration
    */
   public MPXDuration getRemainingDuration ()
   {
      return ((MPXDuration)get(REMAINING_DURATION));
   }

   /**
    * The Remaining Work field shows the amount of time, or person-hours,
    * still required by all assigned resources to complete a task.
    *
    * @return - the amount of time, or person-hours, still required by all assigned
    * resources to complete a task.MPXDuration
    */
   public MPXDuration getRemainingWork ()
   {
      return ((MPXDuration)get(REMAINING_WORK));
   }

   /**
    * The Resource Group field contains the list of resource groups to which
    * the resources assigned to a task belong.
    *
    * @return - single string list of groups
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
    * @return - String list
    */
   public String getResourceInitials ()
   {
      return ((String)get(RESOURCE_INITIALS));
   }

   /**
    * The Resource Names field lists the names of all resources assigned to a task.
    *
    * @return - String list
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
    * @return - Date
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
   public Boolean getRollup ()
   {
      return ((Boolean)get(ROLLUP));
   }

   /**
    * The Start field shows the date and time that a task is scheduled to begin.
    * You can enter the start date you want, to indicate the date when the task
    * should begin. Or, you can have Microsoft Project calculate the start date.
    *
    * @return - Date
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
    * @return - Date
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
    * @return - Date
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
    * @return - Date
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
    * @return - Date
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
    * @return - Date
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
    * @return - value of duration. MPXDuration
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
    * @return - Date
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
    * @return - String path
    */
   public String getSubprojectFile ()
   {
      return ((String)get(SUBPROJECT_FILE));
   }

   /**
    * The Successors field lists the task ID numbers for the successor
    * tasks to a task. A task must start or finish before successor tasks
    * can start or finish. Each successor is linked to the task by a specific
    * type of task dependency and a lead time or lag time.
    *
    * @return - String list
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
    * @return - String
    */
   public String getText1 ()
   {
      return ((String)get(TEXT1));
   }

   /**
    * The Text1-30 fields show any custom text information you want
    * to enter in your project about tasks.
    *
    * @return - String
    */
   public String getText2 ()
   {
      return ((String)get(TEXT2));
   }

   /**
    * The Text1-30 fields show any custom text information you want
    * to enter in your project about tasks.
    *
    * @return - String
    */
   public String getText3 ()
   {
      return ((String)get(TEXT3));
   }

   /**
    * The Text1-30 fields show any custom text information you want
    * to enter in your project about tasks.
    *
    * @return - String
    */
   public String getText4 ()
   {
      return ((String)get(TEXT4));
   }

   /**
    * The Text1-30 fields show any custom text information you want
    * to enter in your project about tasks.
    *
    * @return - String
    */
   public String getText5 ()
   {
      return ((String)get(TEXT5));
   }

   /**
    * The Text1-30 fields show any custom text information you want
    * to enter in your project about tasks.
    *
    * @return - String
    */
   public String getText6 ()
   {
      return ((String)get(TEXT6));
   }

   /**
    * The Text1-30 fields show any custom text information you want
    * to enter in your project about tasks.
    *
    * @return - String
    */
   public String getText7 ()
   {
      return ((String)get(TEXT7));
   }

   /**
    * The Text1-30 fields show any custom text information you want
    * to enter in your project about tasks.
    *
    * @return - String
    */
   public String getText8 ()
   {
      return ((String)get(TEXT8));
   }

   /**
    * The Text1-30 fields show any custom text information you want
    * to enter in your project about tasks.
    *
    * @return - String
    */
   public String getText9 ()
   {
      return ((String)get(TEXT9));
   }

   /**
    * The Text1-30 fields show any custom text information you want
    * to enter in your project about tasks.
    *
    * @return - String
    */
   public String getText10 ()
   {
      return ((String)get(TEXT10));
   }

   /**
    * The Total Slack field contains the amount of time a task can be
    * delayed without delaying the project's finish date.
    *
    * @return - string representing duration
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
    * @return - String
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
    * @return - list of predecessor UniqueIDs
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
    * @return - list of predecessor UniqueIDs
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
    * @return - true if needed.
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
    * @return - string
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
    * @return - MPXDuration representing duration .
    */
   public MPXDuration getWork ()
   {
      return ((MPXDuration)get(WORK));
   }

   /**
    * The Work Variance field contains the difference between a task's
    * baseline work and the currently scheduled work.
    *
    * @return - MPXDuration representing duration.
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
         buf.append (m_model.toString());
         m_model.setWritten (true);
      }

      //
      // Write the task
      //
      buf.append (toString (RECORD_NUMBER, m_model.getModel ()));


      //
      // Write the task notes
      //
      if (m_notes != null)
      {
         buf.append (m_notes.toString ());
      }

      //
      // Write the recurring task
      //
      if (m_recurring != null)
      {
         buf.append (m_recurring.toString ());
      }

      //
      // Write any resource assignments
      //
      if (m_assignments.isEmpty() == false)
      {
         Iterator list = m_assignments.iterator();
         while(list.hasNext())
         {
            buf.append(((ResourceAssignment)list.next()).toString());
         }
      }

      return (buf.toString());
   }

   /**
    * Retrieve count of the number of child tasks.
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
    * This is a reference to the parent task, as specified by the
    * outline level.
    */
   private Task m_parent;

   /**
    * This list holds references to all tasks that are children of the
    * current task as specified by the outline level.
    */
   private LinkedList m_children = new LinkedList ();

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
   private RecurringTask m_recurring;


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
   private static final int COST1 = 36;

   /**
    * The Cost fields show any custom task cost information you want to enter in your project.
    */
   private static final int COST2 = 37;

   /**
    * The Cost fields show any custom task cost information you want to enter in your project.
    */
   private static final int COST3 = 38;

   /**
    * The Cost Variance field shows the difference between the baseline cost and total
    * cost for a task. The total cost is the current estimate of costs based on actual
    * costs and remaining costs. This is also referred to as variance at completion (VAC).
    */
   private static final int COST_VARIANCE = 34;

   /**
    * The Created field contains the date and time when a task was added to the project.
    */
   private static final int CREATED = 125;

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
   private static final int DURATION1 = 46;

   /**
    * The Duration fields are custom fields that show any specialized task duration
    * information you want to enter and store separately in your project.
    */
   private static final int DURATION2 = 47;

   /**
    * The Duration fields are custom fields that show any specialized task duration
    * information you want to enter and store separately in your project.
    */
   private static final int DURATION3 = 48;

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
   private static final int FINISH1 = 61;

   /**
    * The Finish fields are custom fields that show any specific task finish date information
    * you want to enter and store separately in your project.
    */
   private static final int FINISH2 = 63;

   /**
    * The Finish fields are custom fields that show any specific task finish date information
    * you want to enter and store separately in your project.
    */
   private static final int FINISH3 = 65;

   /**
    * The Finish fields are custom fields that show any specific task finish date information
    * you want to enter and store separately in your project.
    */
   private static final int FINISH4 = 127;

   /**
    * The Finish fields are custom fields that show any specific task finish date information
    * you want to enter and store separately in your project.
    */
   private static final int FINISH5 = 129;

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
   private static final int FLAG1 = 110;

   /**
    * The Flag fields indicate whether a task is marked for further action or
    * identification of some kind. To mark a task, click Yes in a Flag field. If you
    * don't want a task marked, click No.
    */
   private static final int FLAG2 = 111;

   /**
    * The Flag fields indicate whether a task is marked for further action or
    * identification of some kind. To mark a task, click Yes in a Flag field. If you
    * don't want a task marked, click No.
    */
   private static final int FLAG3 = 112;

   /**
    * The Flag fields indicate whether a task is marked for further action or
    * identification of some kind. To mark a task, click Yes in a Flag field. If you
    * don't want a task marked, click No.
    */
   private static final int FLAG4 = 113;

   /**
    * The Flag fields indicate whether a task is marked for further action or
    * identification of some kind. To mark a task, click Yes in a Flag field. If you
    * don't want a task marked, click No.
    */
   private static final int FLAG5 = 114;

   /**
    * The Flag fields indicate whether a task is marked for further action or
    * identification of some kind. To mark a task, click Yes in a Flag field. If you
    * don't want a task marked, click No.
    */
   private static final int FLAG6 = 115;

   /**
    * The Flag fields indicate whether a task is marked for further action or
    * identification of some kind. To mark a task, click Yes in a Flag field. If you
    * don't want a task marked, click No.
    */
   private static final int FLAG7 = 116;

   /**
    * The Flag fields indicate whether a task is marked for further action or
    * identification of some kind. To mark a task, click Yes in a Flag field. If you
    * don't want a task marked, click No.
    */
   private static final int FLAG8 = 117;

   /**
    * The Flag fields indicate whether a task is marked for further action or
    * identification of some kind. To mark a task, click Yes in a Flag field. If you
    * don't want a task marked, click No.
    */
   private static final int FLAG9 = 118;

   /**
    * The Flag fields indicate whether a task is marked for further action or
    * identification of some kind. To mark a task, click Yes in a Flag field. If you
    * don't want a task marked, click No.
    */
   private static final int FLAG10 = 119;

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
   private static final int NOTES = 14;

   /**
    * The Number fields show any custom numeric information you enter in your project
    * regarding tasks.
    */
   private static final int NUMBER1 = 140;

   /**
    * The Number fields show any custom numeric information you enter in your project
    * regarding tasks.
    */
   private static final int NUMBER2 = 141;

   /**
    * The Number fields show any custom numeric information you enter in your project
    * regarding tasks.
    */
   private static final int NUMBER3 = 142;

   /**
    * The Number fields show any custom numeric information you enter in your project
    * regarding tasks.
    */
   private static final int NUMBER4 = 143;

   /**
    * The Number fields show any custom numeric information you enter in your project
    * regarding tasks.
    */
   private static final int NUMBER5 = 144;

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
   private static final int START1 = 60;

   /**
    * The Start fields are custom fields that show any specific task start date
    * information you want to enter and store separately in your project.
    */
   private static final int START2 = 62;

   /**
    * The Start fields are custom fields that show any specific task start date
    * information you want to enter and store separately in your project.
    */
   private static final int START3 = 64;

   /**
    * The Start fields are custom fields that show any specific task start date
    * information you want to enter and store separately in your project.
    */
   private static final int START4 = 126;

   /**
    * The Start fields are custom fields that show any specific task start date
    * information you want to enter and store separately in your project.
    */
   private static final int START5 = 128;

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
   private static final int SUBPROJECT_FILE = 96;

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
   private static final int TEXT1 = 4;

   /**
    * The Text fields show any custom text information you want to enter in your project about tasks.
    */
   private static final int TEXT2 = 5;

   /**
    * The Text fields show any custom text information you want to enter in your project about tasks.
    */
   private static final int TEXT3 = 6;

   /**
    * The Text fields show any custom text information you want to enter in your project about tasks.
    */
   private static final int TEXT4 = 7;

   /**
    * The Text fields show any custom text information you want to enter in your project about tasks.
    */
   private static final int TEXT5 = 8;

   /**
    * The Text fields show any custom text information you want to enter in your project about tasks.
    */
   private static final int TEXT6 = 9;

   /**
    * The Text fields show any custom text information you want to enter in your project about tasks.
    */
   private static final int TEXT7 = 10;

   /**
    * The Text fields show any custom text information you want to enter in your project about tasks.
    */
   private static final int TEXT8 = 11;

   /**
    * The Text fields show any custom text information you want to enter in your project about tasks.
    */
   private static final int TEXT9 = 12;

   /**
    * The Text fields show any custom text information you want to enter in your project about tasks.
    */
   private static final int TEXT10 = 13;

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
    * Constant representing maximum number of ResourceAssignments children per Task.
    */
   public static final int MAX_RESOURCE_ASSIGNMENTS = 100;

   /**
    * Constant containing the record number associated with this record.
    */
   public static final int RECORD_NUMBER = 70;
}
