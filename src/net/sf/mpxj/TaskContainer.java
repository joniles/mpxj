/*
 * file:       TaskContainer.java
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

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.sf.mpxj.common.NumberHelper;

/**
 * Manages the collection of tasks belonging to a project.
 */
public class TaskContainer extends ProjectEntityWithIDContainer<Task>
{
   /**
    * Constructor.
    * 
    * @param projectFile parent project
    */
   public TaskContainer(ProjectFile projectFile)
   {
      super(projectFile);
   }

   /**
    * Add a task to the project.
    * 
    * @return new task instance
    */
   public Task add()
   {
      Task task = new Task(m_projectFile, (Task) null);
      add(task);
      m_projectFile.getChildTasks().add(task);
      return task;
   }

   /**
    * {@inheritDoc}
    */
   @Override public void removed(Task task)
   {
      //If the removal of attached data is suppressed do nothing
      if (!getRemoveAttachedData())
         return;

      //
      // Remove the task from the file and its parent task
      //
      m_uniqueIDMap.remove(task.getUniqueID());
      m_idMap.remove(task.getID());

      Task parentTask = task.getParentTask();
      if (parentTask != null)
      {
         parentTask.removeChildTask(task);
      }
      else
      {
         m_projectFile.getChildTasks().remove(task);
      }

      //
      // Remove all resource assignments
      //
      Iterator<ResourceAssignment> iter = m_projectFile.getAllResourceAssignments().iterator();
      while (iter.hasNext() == true)
      {
         ResourceAssignment assignment = iter.next();
         if (assignment.getTask() == task)
         {
            Resource resource = assignment.getResource();
            if (resource != null)
            {
               resource.removeResourceAssignment(assignment);
            }
            iter.remove();
         }
      }

      //
      // Recursively remove any child tasks
      //
      while (true)
      {
         List<Task> childTaskList = task.getChildTasks();
         if (childTaskList.isEmpty() == true)
         {
            break;
         }

         remove(childTaskList.get(0));
      }
   }

   private boolean m_removeAttachedData = true;

   /**
    * If true this suppresses the removal of attached data (parent Task, ResourceAssignments and ChildTasks)
    * when removing a Task from the list.
    * @param remove
    */
   public void setRemoveAttachedData(boolean remove)
   {
      m_removeAttachedData = remove;
   }

   /**
    * If true this suppresses the removal of attached data (parent Task, ResourceAssignments and ChildTasks)
    * when removing a Task from the list.
    */
   public boolean getRemoveAttachedData()
   {
      return m_removeAttachedData;
   }

   /**
    * Microsoft Project bases the order of tasks displayed on their ID
    * value. This method takes the hierarchical structure of tasks
    * represented in MPXJ and renumbers the ID values to ensure that
    * this structure is displayed as expected in Microsoft Project. This
    * is typically used to deal with the case where a hierarchical task
    * structure has been created programmatically in MPXJ.  
    */
   public void synchronizeTaskIDToHierarchy()
   {
      boolean prevSuppression = getRemoveAttachedData();
      setRemoveAttachedData(false);
      clear();
      setRemoveAttachedData(prevSuppression);

      int currentID = (getByID(Integer.valueOf(0)) == null ? 1 : 0);
      for (Task task : m_projectFile.getChildTasks())
      {
         task.setID(Integer.valueOf(currentID++));
         add(task);
         currentID = synchroizeTaskIDToHierarchy(task, currentID);
      }
   }

   /**
    * Called recursively to renumber child task IDs.
    * 
    * @param parentTask parent task instance
    * @param currentID current task ID
    * @return updated current task ID
    */
   private int synchroizeTaskIDToHierarchy(Task parentTask, int currentID)
   {
      for (Task task : parentTask.getChildTasks())
      {
         task.setID(Integer.valueOf(currentID++));
         add(task);
         currentID = synchroizeTaskIDToHierarchy(task, currentID);
      }
      return currentID;
   }

   /**
    * This method is used to recreate the hierarchical structure of the
    * project file from scratch. The method sorts the list of all tasks,
    * then iterates through it creating the parent-child structure defined
    * by the outline level field.
    */
   public void updateStructure()
   {
      if (size() > 1)
      {
         Collections.sort(this);
         m_projectFile.getChildTasks().clear();

         Task lastTask = null;
         int lastLevel = -1;
         boolean autoWbs = m_projectFile.getProjectConfig().getAutoWBS();
         boolean autoOutlineNumber = m_projectFile.getProjectConfig().getAutoOutlineNumber();

         for (Task task : this)
         {
            task.clearChildTasks();
            Task parent = null;
            if (!task.getNull())
            {
               int level = NumberHelper.getInt(task.getOutlineLevel());

               if (lastTask != null)
               {
                  if (level == lastLevel || task.getNull())
                  {
                     parent = lastTask.getParentTask();
                     level = lastLevel;
                  }
                  else
                  {
                     if (level > lastLevel)
                     {
                        parent = lastTask;
                     }
                     else
                     {
                        while (level <= lastLevel)
                        {
                           parent = lastTask.getParentTask();

                           if (parent == null)
                           {
                              break;
                           }

                           lastLevel = NumberHelper.getInt(parent.getOutlineLevel());
                           lastTask = parent;
                        }
                     }
                  }
               }

               lastTask = task;
               lastLevel = level;

               if (autoWbs || task.getWBS() == null)
               {
                  task.generateWBS(parent);
               }

               if (autoOutlineNumber)
               {
                  task.generateOutlineNumber(parent);
               }
            }

            if (parent == null)
            {
               m_projectFile.getChildTasks().add(task);
            }
            else
            {
               parent.addChildTask(task);
            }
         }
      }
   }

   @Override protected int firstUniqueID()
   {
      Task firstEntity = getByID(Integer.valueOf(0));
      return firstEntity == null ? 1 : 0;
   }
}
