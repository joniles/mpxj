/*
 * file:       ExternalRelation.java
 * author:     Brandon Herzog
 * copyright:  (c) Packwood Software 2017
 * date:       3/20/2017
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

package org.mpxj.primavera;

import org.mpxj.Duration;
import org.mpxj.RelationType;
import org.mpxj.Task;
import org.mpxj.TimeUnit;

/**
 * This class represents the relationship between two tasks in Primavera when the predecessor
 * task is not contained in the same project.
 */
final class ExternalRelation
{
   /**
    * Default constructor.
    *
    * @param uniqueID external relation unique ID
    * @param sourceUniqueID source task unique ID
    * @param targetTask target task instance
    * @param type relation type
    * @param lag relation lag
    * @param predecessor true if this is a predecessor of the current task
    * @param notes comments on this relationship
    */
   public ExternalRelation(Integer uniqueID, Integer sourceUniqueID, Task targetTask, RelationType type, Duration lag, boolean predecessor, String notes)
   {
      m_uniqueID = uniqueID;
      m_externalTaskUniqueID = sourceUniqueID;
      m_targetTask = targetTask;

      if (type == null)
      {
         m_type = RelationType.FINISH_START;
      }
      else
      {
         m_type = type;
      }

      if (lag == null)
      {
         m_lag = Duration.getInstance(0, TimeUnit.DAYS);
      }
      else
      {
         m_lag = lag;
      }

      m_predecessor = predecessor;
      m_notes = notes;
   }

   /**
    * Method used to retrieve the type of relationship being
    * represented.
    *
    * @return relationship type
    */
   public RelationType getType()
   {
      return m_type;
   }

   /**
    * This method retrieves the lag duration associated
    * with this relationship.
    *
    * @return lag duration
    */
   public Duration getLag()
   {
      return m_lag;
   }

   /**
    * Retrieve the source task of this relationship.
    *
    * @return source task
    */
   public Integer externalTaskUniqueID()
   {
      return m_externalTaskUniqueID;
   }

   /**
    * Retrieve the target task of this relationship.
    *
    * @return target task
    */
   public Task getTargetTask()
   {
      return m_targetTask;
   }

   /**
    * Retrieve the Unique ID of this Relation.
    *
    * @return unique ID
    */
   public Integer getUniqueID()
   {
      return m_uniqueID;
   }

   /**
    * Indication relation type.
    *
    * @return true if this is an external predecessor, or false if it is an external successor.
    */
   public boolean getPredecessor()
   {
      return m_predecessor;
   }

   /**
    * Retrieve notes relating to this external relationship.
    *
    * @return notes
    */
   public String getNotes()
   {
      return m_notes;
   }

   @Override public String toString()
   {
      return "[ExternalPredecessor " + m_externalTaskUniqueID + " -> " + m_targetTask + "]";
   }

   private final Integer m_uniqueID;

   /**
    * External task unique ID.
    */
   private final Integer m_externalTaskUniqueID;

   /**
    * Identifier of task with which this relationship is held.
    */
   private final Task m_targetTask;

   /**
    * Type of relationship.
    */
   private final RelationType m_type;

   /**
    * Lag between the two tasks.
    */
   private final Duration m_lag;

   /**
    * True if the external activity is a predecessor.
    */
   private final boolean m_predecessor;

   private final String m_notes;
}
