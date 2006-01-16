/*
 * file:       ResourceAssignment.java
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

import java.util.Date;

/**
 * This class represents a resource assignment record from an MPX file.
 */
public final class ResourceAssignment extends MPXRecord
{
   /**
    * Default constructor.
    *
    * @param file The parent file to which this record belongs.
    * @param task The task to which this assignment is being made
    * @throws MPXException Normally thrown when parsing fails
    */
   ResourceAssignment (ProjectFile file, Task task)
      throws MPXException
   {
      this (file, Record.EMPTY_RECORD, task);
   }

   /**
    * Constructor used to create an instance of this class from data
    * taken from an MPXFile record.
    *
    * @param file The MPXFile object to which this record belongs.
    * @param record Record containing the data for  this object.
    * @param task The task to which this assignment is being made
    * @throws MPXException normally thrown when parsing fails
    */
   ResourceAssignment (ProjectFile file, Record record, Task task)
      throws MPXException
   {
      super (file);

      m_task = task;

      setResourceID(record.getInteger(0));
      setUnits(record.getUnits(1));
      setWork(record.getDuration(2));
      setPlannedWork(record.getDuration(3));
      setActualWork(record.getDuration(4));
      setOvertimeWork(record.getDuration(5));
      setCost(record.getCurrency(6));
      setPlannedCost(record.getCurrency(7));
      setActualCost(record.getCurrency(8));
      setStart(record.getDateTime(9));
      setFinish(record.getDateTime(10));
      setDelay(record.getDuration(11));
      setResourceUniqueID(record.getInteger(12));

      //
      // Calculate the remaining work
      //
      MPXDuration work = getWork();
      MPXDuration actualWork = getActualWork();
      if (work != null && actualWork != null)
      {
         if (work.getUnits() != actualWork.getUnits())
         {
            actualWork = actualWork.convertUnits(work.getUnits(), getParentFile().getProjectHeader());
         }
         
         setRemainingWork(MPXDuration.getInstance(work.getDuration() - actualWork.getDuration(), work.getUnits()));
      }
   }

   /**
    * This method allows a resource assignment workgroup fields record
    * to be added to the current resource assignment. A maximum of
    * one of these records can be added to a resource assignment record.
    *
    * @return ResourceAssignmentWorkgroupFields object
    * @throws MPXException if MSP defined limit of 1 is exceeded
    */
   public ResourceAssignmentWorkgroupFields addWorkgroupAssignment ()
      throws MPXException
   {
      if (m_workgroup != null)
      {
         throw new MPXException (MPXException.MAXIMUM_RECORDS);
      }

      m_workgroup = new ResourceAssignmentWorkgroupFields (getParentFile());

      return (m_workgroup);
   }

   /**
    * This method allows a resource assignment workgroup fields record
    * to be added to the current resource assignment. A maximum of
    * one of these records can be added to a resource assignment record.
    * The data for this new record is taken from a record read from an
    * MPX file.
    *
    * @param record record from an MPX file
    * @return ResourceAssignmentWorkgroupFields object
    * @throws MPXException if MSP defined limit of 1 is exceeded
    */
   ResourceAssignmentWorkgroupFields addWorkgroupAssignment (Record record)
      throws MPXException
   {
      if (m_workgroup != null)
      {
         throw new MPXException (MPXException.MAXIMUM_RECORDS);
      }

      m_workgroup = new ResourceAssignmentWorkgroupFields (getParentFile(), record);

      return (m_workgroup);
   }

   /**
    * Returns the resource ID associated with this assignment.
    *
    * @return ID
    */
   public Integer getResourceID ()
   {
      return (m_resourceID);
   }

   /**
    * Sets the resource ID associated with this assignment.
    *
    * @param val  ID
    */
   public void setResourceID (Integer val)
   {
      if (val != null)
      {
         m_resourceID = val;
         
         //
         // If the resource unique ID has not yet been set,
         // use the resource ID as the default value.
         //
         if (getResourceUniqueID() == null)
         {
            setResourceUniqueID(val);
         }
      }
   }

   /**
    * Returns the units of this resource assignment.
    *
    * @return units
    */
   public Number getUnits ()
   {
      return (m_units);
   }

   /**
    * Sets the units for this resource assignment.
    *
    * @param val units
    */
   public void setUnits (Number val)
   {
      m_units = val;
   }

   /**
    * Returns the work of this resource assignment.
    *
    * @return work
    */
   public MPXDuration getWork ()
   {
      return (m_work);
   }

   /**
    * Sets the work for this resource assignment.
    *
    * @param dur work
    */
   public void setWork (MPXDuration dur)
   {
      m_work = dur;
   }

   /**
    * Returns the planned work of this resource assignment.
    *
    * @return planned work
    */
   public MPXDuration getPlannedWork ()
   {
      return (m_plannedWork);
   }

   /**
    * Sets the planned work for this resource assignment.
    *
    * @param dur planned work
    */
   public void setPlannedWork (MPXDuration dur)
   {
      m_plannedWork = dur;
   }

   /**
    * Returns the actual completed work of this resource assignment.
    *
    * @return completed work
    */
   public MPXDuration getActualWork ()
   {
      return (m_actualWork);
   }

   /**
    * Sets the actual completed work for this resource assignment.
    *
    * @param dur actual completed work
    */
   public void setActualWork (MPXDuration dur)
   {
      m_actualWork = dur;
   }

   /**
    * Returns the overtime work done of this resource assignment.
    *
    * @return overtime work
    */
   public MPXDuration getOvertimeWork ()
   {
      return (m_overtimeWork);
   }

   /**
    * Sets the overtime work for this resource assignment.
    *
    * @param dur overtime work
    */
   public void setOvertimeWork (MPXDuration dur)
   {
      m_overtimeWork = dur;
   }

   /**
    * Returns the cost  of this resource assignment.
    *
    * @return cost
    */
   public Number getCost ()
   {
      return (m_cost);
   }

   /**
    * Sets the cost for this resource assignment.
    *
    * @param val cost
    */
   public void setCost (Number val)
   {
      m_cost = val;
   }

   /**
    * Returns the planned cost for this resource assignment.
    *
    * @return planned cost
    */
   public Number getPlannedCost ()
   {
      return (m_plannedCost);
   }

   /**
    * Sets the planned cost for this resource assignment.
    *
    * @param val planned cost
    */
   public void setPlannedCost (Number val)
   {
      m_plannedCost = val;
   }

   /**
    * Returns the actual cost for this resource assignment.
    *
    * @return actual cost
    */
   public Number getActualCost ()
   {
      return (m_actualCost);
   }

   /**
    * Sets the actual cost so far incurred for this resource assignment.
    *
    * @param val actual cost
    */
   public void setActualCost (Number val)
   {
      m_actualCost = val;
   }

   /**
    * Returns the start of this resource assignment.
    *
    * @return start date
    */
   public Date getStart ()
   {
      return (m_start);
   }

   /**
    * Sets the start date for this resource assignment.
    *
    * @param val start date
    */
   public void setStart (Date val)
   {
      m_start = val;
   }

   /**
    * Returns the finish date for this resource assignment.
    *
    * @return finish date
    */
   public Date getFinish ()
   {
      return (m_finish);
   }

   /**
    * Sets the finish date for this resource assignment.
    *
    * @param val finish date
    */
   public void setFinish (Date val)
   {
      m_finish = val;
   }

   /**
    * Returns the delay for this resource assignment.
    *
    * @return delay
    */
   public MPXDuration getDelay ()
   {
      return (m_delay);
   }

   /**
    * Sets the delay for this resource assignment.
    *
    * @param dur delay
    */
   public void setDelay (MPXDuration dur)
   {
      m_delay = dur;
   }

   /**
    * Returns the resources unique id for this resource assignment.
    *
    * @return resources unique id
    */
   public Integer getResourceUniqueID ()
   {
      return (m_resourceUniqueID);
   }

   /**
    * Sets the resources unique id for this resource assignment.
    *
    * @param val resources unique id
    */
   public void setResourceUniqueID (Integer val)
   {
      if (val != null)
      {
         m_resourceUniqueID = val;
         
         //
         // If the resource ID has not been set, then use
         // the resource unique ID as the default value.
         //
         if (getResourceID() == null)
         {
            setResourceID(val);
         }
      }
   }

   /**
    * Gets the Resource Assignment Workgroup Fields if one exists.
    *
    * @return workgroup assignment object
    */
   public ResourceAssignmentWorkgroupFields getWorkgroupAssignment ()
   {
      return (m_workgroup);
   }

   /**
    * This method retrieves a reference to the task with which this
    * assignment is associated.
    *
    * @return task
    */
   public Task getTask ()
   {
      return (m_task);
   }

   /**
    * This method retrieves a reference to the resource with which this
    * assignment is associated.
    *
    * @return resource
    */
   public Resource getResource ()
   {
      return (getParentFile().getResourceByUniqueID(NumberUtility.getInt(getResourceUniqueID())));
   }

   /**
    * This method returns the Work Contour type of this Assignment.
    *
    * @return the Work Contour type
    */
   public WorkContour getWorkContour()
   {
      return (m_workContour);
   }

   /**
    * This method sets the Work Contour type of this Assignment.
    *
    * @param workContour the Work Contour type
    */
   public void setWorkContour(WorkContour workContour)
   {
      m_workContour = workContour;
   }

   /**
    * Removes this resource assignment from the project.
    */
   public void remove ()
   {
      getParentFile().removeResourceAssignment(this);
   }
   
   /**
    * This method generates a string in MPX format representing the
    * contents of this record.
    *
    * @return string containing the data for this record in MPX format.
    */
   public String toString()
   {
      StringBuffer buf = new StringBuffer();
      char delimiter = getParentFile().getDelimiter();

      buf.append(RECORD_NUMBER);
      buf.append(delimiter);
      buf.append(format(delimiter, getResourceID()));
      buf.append(delimiter);
      buf.append(format(delimiter, toUnits(getUnits())));
      buf.append(delimiter);
      buf.append(format(delimiter, getWork()));
      buf.append(delimiter);
      buf.append(format(delimiter, getPlannedWork()));
      buf.append(delimiter);
      buf.append(format(delimiter, getActualWork()));
      buf.append(delimiter);
      buf.append(format(delimiter, getOvertimeWork()));
      buf.append(delimiter);
      buf.append(format(delimiter, toCurrency(getCost())));
      buf.append(delimiter);
      buf.append(format(delimiter, toCurrency(getPlannedCost())));
      buf.append(delimiter);
      buf.append(format(delimiter, toCurrency(getActualCost())));
      buf.append(delimiter);
      buf.append(format(delimiter, toDate(getStart())));
      buf.append(delimiter);
      buf.append(format(delimiter, toDate(getFinish())));
      buf.append(delimiter);
      buf.append(format(delimiter, getDelay()));
      buf.append(delimiter);
      buf.append(format(delimiter, getResourceUniqueID()));      
      stripTrailingDelimiters(buf, delimiter);
      buf.append (ProjectFile.EOL);

      if (m_workgroup != null)
      {
         buf.append (m_workgroup.toString());
      }
      
      return (buf.toString());
   }

   /**
    * Returns the remaining work for this resource assignment.
    *
    * @return remaining work
    */
   public MPXDuration getRemainingWork ()
   {
      return (m_remainingWork);
   }

   /**
    * Sets the remaining work for this resource assignment.
    *
    * @param remainingWork remaining work
    */
   public void setRemainingWork (MPXDuration remainingWork)
   {
      m_remainingWork = remainingWork;
   }
   
   private Integer m_resourceID;
   private Number m_units;
   private MPXDuration m_work;
   private MPXDuration m_plannedWork;
   private MPXDuration m_actualWork;
   private MPXDuration m_overtimeWork;
   private Number m_cost;
   private Number m_plannedCost;
   private Number m_actualCost;
   private Date m_start;
   private Date m_finish;
   private MPXDuration m_delay;
   private Integer m_resourceUniqueID;
   
   /**
    * The following member variables are extended attributes. They are
    * do not form part of the MPX file format definition, and are neither
    * loaded from an MPX file, or saved to an MPX file. Their purpose
    * is to provide storage for attributes which are defined by later versions
    * of Microsoft Project. This allows these attributes to be manipulated
    * when they have been retrieved from file formats other than MPX.
    */
   private MPXDuration m_remainingWork;

   /**
    * Reference to the parent task of this assignment.
    */
   private Task m_task;

   /**
    *  Child record for Workgroup fields.
    */
   private ResourceAssignmentWorkgroupFields m_workgroup;

   /**
    * Work Contour type of this assignment.
    */
   private WorkContour m_workContour;

   /**
    * Default units value: 100%.
    */
   public static final Double DEFAULT_UNITS = new Double (100);

    /**
    * Constant containing the record number associated with this record.
    */
   static final int RECORD_NUMBER = 75;
}
