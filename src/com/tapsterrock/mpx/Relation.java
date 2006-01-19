/*
 * file:       Relation.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2002-2003
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

package com.tapsterrock.mpx;



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
    * @param parent parent file
    */
   Relation (ProjectFile parent)
   {
      m_parent = parent;
      m_taskIDValue = 0;
      m_taskUniqueIDValue = 0;
      m_type = RelationType.FINISH_START;
      m_duration = MPXDuration.getInstance(0, TimeUnit.DAYS);
   }

   /**
    * Constructs an instance of this class from a String representation
    * of a relationship.
    *
    * @param relationship String representation of a relationship
    * @param file parent MPX file
    * @throws MPXException normally indicating that parsing the string has failed
    */
   Relation (String relationship, ProjectFile file)
      throws MPXException
   {
      int index = 0;
      int length = relationship.length();

      //
      // Extract the identifier
      //
      while ((index < length) && (Character.isDigit(relationship.charAt(index)) == true))
      {
         ++index;
      }

      try
      {
         m_taskIDValue = Integer.parseInt(relationship.substring(0, index));
      }

      catch (NumberFormatException ex)
      {
         throw new MPXException(MPXException.INVALID_FORMAT + " '" + relationship + "'");
      }

      //
      // Now find the task, so we can extract the unique ID      
      //
      Task task = file.getTaskByID(m_taskIDValue);
      if (task != null)
      {
         m_taskUniqueIDValue = task.getUniqueIDValue();
      }
      
      //
      // If we haven't reached the end, we next expect to find
      // SF, SS, FS, FF
      //
      if (index == length)
      {
         m_type = RelationType.FINISH_START;
         m_duration = MPXDuration.getInstance(0, TimeUnit.DAYS);
      }
      else
      {
         if ((index + 1) == length)
         {
            throw new MPXException(MPXException.INVALID_FORMAT + " '" + relationship + "'");
         }

         String relationType = relationship.substring(index, index + 2);
         m_type = RelationType.getInstance(file.getLocale(), relationship.substring(index, index + 2));         
         if (m_type == null)
         {
            throw new MPXException(MPXException.INVALID_FORMAT + " '" + relationType + "'");
         }

         index += 2;

         if (index == length)
         {
            m_duration = MPXDuration.getInstance(0, TimeUnit.DAYS);
         }
         else
         {
            if (relationship.charAt(index) == '+')
            {
               ++index;
            }

            m_duration = MPXDuration.getInstance(relationship.substring(index), file.getDurationDecimalFormat(), file.getLocale());
         }
      }
   }

   /**
    * Method used to retrieve the ID of the task
    * related to the current task instance. 
    *
    * @return task ID
    */
   public int getTaskIDValue ()
   {
      return (m_taskIDValue);
   }

   /**
    * Method used to retrieve the unique ID of the task
    * related to the current task instance. 
    *
    * @return task unique ID
    */
   public int getTaskUniqueIDValue ()
   {
      return (m_taskUniqueIDValue);
   }
   
   /**
    * Method used to set the identifier of the task
    * related to the current task instance.
    *
    * @param id task identifier
    */
   public void setTaskIDValue (int id)
   {
      m_taskIDValue = id;
   }

   /**
    * Method used to set the identifier of the task
    * related to the current task instance.
    *
    * @param id task identifier
    */
   public void setTaskUniqueIDValue (int id)
   {
      m_taskUniqueIDValue = id;
   }
   
   /**
    * Method used to retrieve the type of relationship being
    * represented.
    *
    * @return relationship type
    */
   public RelationType getType ()
   {
      return (m_type);
   }

   /**
    * Method used to set the type of relationship being
    * represented.
    *
    * @param type relationship type
    */
   public void setType (RelationType type)
   {
      m_type = type;
   }

   /**
    * This method retrieves the lag duration associated
    * with this relationship.
    *
    * @return lag duration
    */
   public MPXDuration getDuration ()
   {
      return (m_duration);
   }

   /**
    * This method sets the lag duration associated
    * with this relationship.
    *
    * @param duration the lag duration
    */
   public void setDuration (MPXDuration duration)
   {
      m_duration = duration;
   }

   /**
    * Retrieve the task related to the current task instance.
    * 
    * @return task instance
    */
   public Task getTask ()
   {
      return (m_parent.getTaskByUniqueID(m_taskUniqueIDValue));
   }
   
   /**
    * Parent file.
    */
   private ProjectFile m_parent;
   
   /**
    * Identifier of task with which this relationship is held.
    */
   private int m_taskIDValue;
   private int m_taskUniqueIDValue;
   
   /**
    * Type of relationship.
    */
   private RelationType m_type;

   /**
    * Lag between the two tasks.
    */
   private MPXDuration m_duration;
}
