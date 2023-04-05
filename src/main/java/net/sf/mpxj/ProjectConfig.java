/*
 * file:       ProjectConfig.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2002-2015
 * date:       20/04/2015
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

import net.sf.mpxj.common.NumberHelper;

/**
 * Container for configuration details used to control the behaviour of the ProjectFile class.
 */
public class ProjectConfig
{
   /**
    * Constructor.
    *
    * @param projectFile parent project
    */
   public ProjectConfig(ProjectFile projectFile)
   {
      m_parent = projectFile;
   }

   /**
    * Used to set whether WBS numbers are automatically created.
    *
    * @param flag true if automatic WBS required.
    */
   public void setAutoWBS(boolean flag)
   {
      m_autoWBS = flag;
   }

   /**
    * Used to set whether outline level numbers are automatically created.
    *
    * @param flag true if automatic outline level required.
    */
   public void setAutoOutlineLevel(boolean flag)
   {
      m_autoOutlineLevel = flag;
   }

   /**
    * Used to set whether outline numbers are automatically created.
    *
    * @param flag true if automatic outline number required.
    */
   public void setAutoOutlineNumber(boolean flag)
   {
      m_autoOutlineNumber = flag;
   }

   /**
    * Used to set whether the task unique ID field is automatically populated.
    *
    * @param flag true if automatic unique ID required.
    */
   public void setAutoTaskUniqueID(boolean flag)
   {
      m_autoTaskUniqueID = flag;
   }

   /**
    * Used to set whether the calendar unique ID field is automatically populated.
    *
    * @param flag true if automatic unique ID required.
    */
   public void setAutoCalendarUniqueID(boolean flag)
   {
      m_autoCalendarUniqueID = flag;
   }

   /**
    * Used to set whether the assignment unique ID field is automatically populated.
    *
    * @param flag true if automatic unique ID required.
    */
   public void setAutoAssignmentUniqueID(boolean flag)
   {
      m_autoAssignmentUniqueID = flag;
   }

   /**
    * Used to set whether the task ID field is automatically populated.
    *
    * @param flag true if automatic ID required.
    */
   public void setAutoTaskID(boolean flag)
   {
      m_autoTaskID = flag;
   }

   /**
    * Retrieve the flag that determines whether WBS is generated
    * automatically.
    *
    * @return boolean, default is false.
    */
   public boolean getAutoWBS()
   {
      return m_autoWBS;
   }

   /**
    * Retrieve the flag that determines whether outline level is generated
    * automatically.
    *
    * @return boolean, default is false.
    */
   public boolean getAutoOutlineLevel()
   {
      return m_autoOutlineLevel;
   }

   /**
    * Retrieve the flag that determines whether outline numbers are generated
    * automatically.
    *
    * @return boolean, default is false.
    */
   public boolean getAutoOutlineNumber()
   {
      return m_autoOutlineNumber;
   }

   /**
    * Retrieve the flag that determines whether the task unique ID
    * is generated automatically.
    *
    * @return boolean, default is false.
    */
   public boolean getAutoTaskUniqueID()
   {
      return m_autoTaskUniqueID;
   }

   /**
    * Retrieve the flag that determines whether the calendar unique ID
    * is generated automatically.
    *
    * @return boolean, default is false.
    */
   public boolean getAutoCalendarUniqueID()
   {
      return m_autoCalendarUniqueID;
   }

   /**
    * Retrieve the flag that determines whether the assignment unique ID
    * is generated automatically.
    *
    * @return boolean, default is true.
    */
   public boolean getAutoAssignmentUniqueID()
   {
      return m_autoAssignmentUniqueID;
   }

   /**
    * Retrieve the flag that determines whether the task ID
    * is generated automatically.
    *
    * @return boolean, default is false.
    */
   public boolean getAutoTaskID()
   {
      return m_autoTaskID;
   }

   /**
    * Used to set whether the resource unique ID field is automatically populated.
    *
    * @param flag true if automatic unique ID required.
    */
   public void setAutoResourceUniqueID(boolean flag)
   {
      m_autoResourceUniqueID = flag;
   }

   /**
    * Used to set whether the resource ID field is automatically populated.
    *
    * @param flag true if automatic ID required.
    */
   public void setAutoResourceID(boolean flag)
   {
      m_autoResourceID = flag;
   }

   /**
    * Retrieve the flag that determines whether the resource unique ID
    * is generated automatically.
    *
    * @return boolean, default is false.
    */
   public boolean getAutoResourceUniqueID()
   {
      return m_autoResourceUniqueID;
   }

   /**
    * Retrieve the flag that determines whether the resource ID
    * is generated automatically.
    *
    * @return boolean, default is false.
    */
   public boolean getAutoResourceID()
   {
      return m_autoResourceID;
   }

   /**
    * Used to set whether the relation unique ID field is automatically populated.
    *
    * @param flag true if automatic unique ID required.
    */
   public void setAutoRelationUniqueID(boolean flag)
   {
      m_autoRelationUniqueID = flag;
   }

   /**
    * Retrieve the flag that determines whether the relation unique ID
    * is generated automatically.
    *
    * @return boolean, default is false.
    */
   public boolean getAutoRelationUniqueID()
   {
      return m_autoRelationUniqueID;
   }

   /**
    * This method is used to retrieve the next unique ID for a task.
    *
    * @return next unique ID
    */
   public int getNextTaskUniqueID()
   {
      return ++m_taskUniqueID;
   }

   /**
    * This method is used to retrieve the next unique ID for a calendar.
    *
    * @return next unique ID
    */
   public int getNextCalendarUniqueID()
   {
      return ++m_calendarUniqueID;
   }

   /**
    * This method is used to retrieve the next unique ID for an assignment.
    *
    * @return next unique ID
    */
   int getNextAssignmentUniqueID()
   {
      return ++m_assignmentUniqueID;
   }

   /**
    * This method is used to retrieve the next ID for a task.
    *
    * @return next ID
    */
   public int getNextTaskID()
   {
      return ++m_taskID;
   }

   /**
    * This method is used to retrieve the next unique ID for a resource.
    *
    * @return next unique ID
    */
   public int getNextResourceUniqueID()
   {
      return ++m_resourceUniqueID;
   }

   /**
    * This method is used to retrieve the next ID for a resource.
    *
    * @return next ID
    */
   public int getNextResourceID()
   {
      return ++m_resourceID;
   }

   /**
    * This method is used to retrieve the next unique ID for a relation.
    *
    * @return next unique ID
    */
   public int getNextRelationUniqueID()
   {
      return ++m_relationUniqueID;
   }

   /**
    * Returns true if a task's Complete Through attribute is reported as
    * the time work can next start. Defaults to false. When set to true this
    * matches the behaviour of MS Project versions prior to 2007.
    *
    * @return true if Complete Through is next work start date
    */
   public boolean getCompleteThroughIsNextWorkStart()
   {
      return m_completeThroughIsNextWorkStart;
   }

   /**
    * When set to true a task's Complete Through attribute is reported as
    * the time work can next start. Defaults to false. When set to true this
    * matches the behaviour of MS Project versions prior to 2007.
    *
    * @param completeThroughIsNextWorkStart true if Complete Through is next work start date
    */
   public void setCompleteThroughIsNextWorkStart(boolean completeThroughIsNextWorkStart)
   {
      m_completeThroughIsNextWorkStart = completeThroughIsNextWorkStart;
   }

   /**
    * This method is called to ensure that after a project file has been
    * read, the cached unique ID values used to generate new unique IDs
    * start after the end of the existing set of unique IDs.
    */
   public void updateUniqueCounters()
   {
      updateTaskUniqueCounter();
      updateResourceUniqueCounter();
      updateCalendarUniqueCounter();
      updateAssignmentUniqueCounter();
   }

   /**
    * Ensure unique ID counter is in sync with project file.
    */
   public void updateTaskUniqueCounter()
   {
      for (Task task : m_parent.getTasks())
      {
         int uniqueID = NumberHelper.getInt(task.getUniqueID());
         if (uniqueID > m_taskUniqueID)
         {
            m_taskUniqueID = uniqueID;
         }
      }
   }

   /**
    * Ensure unique ID counter is in sync with project file.
    */
   public void updateResourceUniqueCounter()
   {
      for (Resource resource : m_parent.getResources())
      {
         int uniqueID = NumberHelper.getInt(resource.getUniqueID());
         if (uniqueID > m_resourceUniqueID)
         {
            m_resourceUniqueID = uniqueID;
         }
      }
   }

   /**
    * Ensure unique ID counter is in sync with project file.
    */
   public void updateCalendarUniqueCounter()
   {
      for (ProjectCalendar calendar : m_parent.getCalendars())
      {
         int uniqueID = NumberHelper.getInt(calendar.getUniqueID());
         if (uniqueID > m_calendarUniqueID)
         {
            m_calendarUniqueID = uniqueID;
         }
      }
   }

   /**
    * Ensure unique ID counter is in sync with project file.
    */
   public void updateAssignmentUniqueCounter()
   {
      for (ResourceAssignment assignment : m_parent.getResourceAssignments())
      {
         int uniqueID = NumberHelper.getInt(assignment.getUniqueID());
         if (uniqueID > m_assignmentUniqueID)
         {
            m_assignmentUniqueID = uniqueID;
         }
      }
   }

   /**
    * Retrieve the strategy used by this project to populate baseline attributes from another schedule.
    *
    * @return baseline strategy
    */
   public BaselineStrategy getBaselineStrategy()
   {
      return m_baselineStrategy;
   }

   /**
    * Set the strategy used by this project to populate baseline attributes from another schedule.
    *
    * @param strategy baseline strategy
    */
   public void setBaselineStrategy(BaselineStrategy strategy)
   {
      m_baselineStrategy = strategy;
   }

   private final ProjectFile m_parent;

   /**
    * Indicating whether WBS value should be calculated on creation, or will
    * be manually set.
    */
   private boolean m_autoWBS = true;

   /**
    * Indicating whether the Outline Level value should be calculated on
    * creation, or will be manually set.
    */
   private boolean m_autoOutlineLevel = true;

   /**
    * Indicating whether the Outline Number value should be calculated on
    * creation, or will be manually set.
    */
   private boolean m_autoOutlineNumber = true;

   /**
    * Indicating whether the unique ID of a task should be
    * calculated on creation, or will be manually set.
    */
   private boolean m_autoTaskUniqueID = true;

   /**
    * Indicating whether the unique ID of a calendar should be
    * calculated on creation, or will be manually set.
    */
   private boolean m_autoCalendarUniqueID = true;

   /**
    * Indicating whether the unique ID of an assignment should be
    * calculated on creation, or will be manually set.
    */
   private boolean m_autoAssignmentUniqueID = true;

   /**
    * Indicating whether the ID of a task should be
    * calculated on creation, or will be manually set.
    */
   private boolean m_autoTaskID = true;

   /**
    * Indicating whether the unique ID of a resource should be
    * calculated on creation, or will be manually set.
    */
   private boolean m_autoResourceUniqueID = true;

   /**
    * Indicating whether the ID of a resource should be
    * calculated on creation, or will be manually set.
    */
   private boolean m_autoResourceID = true;

   /**
    * Indicating whether the unique ID of a relation should be
    * calculated on creation, or will be manually set.
    */
   private boolean m_autoRelationUniqueID = true;

   /**
    * Counter used to populate the unique ID field of a task.
    */
   private int m_taskUniqueID;

   /**
    * Counter used to populate the unique ID field of a calendar.
    */
   private int m_calendarUniqueID;

   /**
    * Counter used to populate the unique ID field of an assignment.
    */
   private int m_assignmentUniqueID;

   /**
    * Counter used to populate the ID field of a task.
    */
   private int m_taskID;

   /**
    * Counter used to populate the unique ID field of a resource.
    */
   private int m_resourceUniqueID;

   /**
    * Counter used to populate the ID field of a resource.
    */
   private int m_resourceID;

   /**
    * Counter used to populate the unique ID field of a relation.
    */
   private int m_relationUniqueID;

   /**
    * Set to true provides compatibility with MS Project versions prior to 2007.
    */
   private boolean m_completeThroughIsNextWorkStart;

   private BaselineStrategy m_baselineStrategy = new DefaultBaselineStrategy();
}
