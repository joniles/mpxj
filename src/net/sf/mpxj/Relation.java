/*
 * file:       Relation.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software Limited 2002-2003
 * date:       14/01/2003
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

/**
 * This class represents the relationship between two tasks. These
 * relationships normally found in the lists of predecessors and
 * successors associated with a task record in an MPX file.
 */
public final class Relation
{
   /**
    * Default constructor.
    *
    * @param parentProject parent project file
    * @param parentTask parent task instance
    */
   public Relation(ProjectFile parentProject, Task parentTask)
   {
      m_parentProject = parentProject;
      m_parentTask = parentTask;
      m_type = RelationType.FINISH_START;
      m_duration = Duration.getInstance(0, TimeUnit.DAYS);
   }

   /**
    * Method used to retrieve the unique ID of the task
    * related to the current task instance.
    *
    * @return task unique ID
    */
   public Integer getTaskUniqueID()
   {
      return (m_taskUniqueID);
   }

   /**
    * Method used to set the identifier of the task
    * related to the current task instance.
    *
    * @param id task identifier
    */
   public void setTaskUniqueID(Integer id)
   {
      m_taskUniqueID = id;
   }

   /**
    * Method used to retrieve the type of relationship being
    * represented.
    *
    * @return relationship type
    */
   public RelationType getType()
   {
      return (m_type);
   }

   /**
    * Method used to set the type of relationship being
    * represented.
    *
    * @param type relationship type
    */
   public void setType(RelationType type)
   {
      m_type = type;
   }

   /**
    * This method retrieves the lag duration associated
    * with this relationship.
    *
    * @return lag duration
    */
   public Duration getDuration()
   {
      return (m_duration);
   }

   /**
    * This method sets the lag duration associated
    * with this relationship.
    *
    * @param duration the lag duration
    */
   public void setDuration(Duration duration)
   {
      m_duration = duration;
   }

   /**
    * Retrieve the task related to the current task instance.
    *
    * @return task instance
    */
   public Task getTask()
   {
      return (m_parentProject.getTaskByUniqueID(m_taskUniqueID));
   }

   /**
    * {@inheritDoc}
    */
   @Override public String toString()
   {
      return ("[Relation " + m_parentTask + " -> " + m_parentProject.getTaskByUniqueID(m_taskUniqueID) + "]");
   }

   /**
    * Parent project file.
    */
   private ProjectFile m_parentProject;

   /**
    * Parent task file.
    */
   private Task m_parentTask;

   /**
    * Identifier of task with which this relationship is held.
    */
   private Integer m_taskUniqueID;

   /**
    * Type of relationship.
    */
   private RelationType m_type;

   /**
    * Lag between the two tasks.
    */
   private Duration m_duration;
}
