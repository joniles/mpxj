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

package org.mpxj;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.mpxj.common.NumberHelper;
import org.mpxj.common.PopulatedFields;

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
      m_projectFile = projectFile;
   }

   /**
    * Add a task to the project.
    *
    * @return new task instance
    */
   public Task add()
   {
      Task task = new Task(m_projectFile, null);
      add(task);
      m_projectFile.getChildTasks().add(task);
      return task;
   }

   @Override public void removed(Task task)
   {
      //
      // Remove the task from the file and its parent task
      //
      super.removed(task);
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
      Iterator<ResourceAssignment> iter = m_projectFile.getResourceAssignments().iterator();
      while (iter.hasNext())
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
         if (childTaskList.isEmpty())
         {
            break;
         }

         remove(childTaskList.get(0));
      }
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
      int currentID = (getByID(Integer.valueOf(0)) == null ? 1 : 0);
      synchroizeTaskIDToHierarchy(m_projectFile, currentID);
   }

   /**
    * Called recursively to renumber child task IDs.
    *
    * @param parentTask parent task instance
    * @param currentID current task ID
    * @return updated current task ID
    */
   private int synchroizeTaskIDToHierarchy(ChildTaskContainer parentTask, int currentID)
   {
      for (Task task : parentTask.getChildTasks())
      {
         task.setID(Integer.valueOf(currentID++));
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
   void updateStructure()
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

   /**
    * Retrieve the set of populated fields for this project.
    *
    * @return set of populated fields
    */
   public Set<FieldType> getPopulatedFields()
   {
      return new PopulatedFields<>(m_projectFile, TaskField.class, m_projectFile.getUserDefinedFields().getTaskFields(), this).getPopulatedFields();
   }

   /**
    * Retrieve a list of task custom fields.
    *
    * @return task custom fields
    */
   public List<CustomField> getCustomFields()
   {
      return m_projectFile.getCustomFields().getCustomFieldsByFieldTypeClass(FieldTypeClass.TASK);
   }

   /**
    * Retrieve the type of a field by its alias.
    *
    * @param alias field alias
    * @return FieldType instance
    */
   public FieldType getFieldTypeByAlias(String alias)
   {
      return m_projectFile.getCustomFields().getFieldTypeByAlias(FieldTypeClass.TASK, alias);
   }

   @Override protected int firstUniqueID()
   {
      Task firstEntity = getByID(Integer.valueOf(0));
      return firstEntity == null ? 1 : 0;
   }

   private final ProjectFile m_projectFile;
}
