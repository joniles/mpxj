/*
 * file:       Relation.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2002-2003
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
 * This class represents the relationship between two tasks.
 */
public final class Relation
{
   /**
    * Default constructor.
    *
    * @param sourceTask source task instance
    * @param targetTask target task instance
    * @param type relation type
    * @param lag relation lag
    */
   public Relation(Task sourceTask, Task targetTask, RelationType type, Duration lag)
   {
      m_sourceTask = sourceTask;
      m_targetTask = targetTask;
      m_type = type;
      m_lag = lag;

      if (m_type == null)
      {
         m_type = RelationType.FINISH_START;
      }

      if (m_lag == null)
      {
         m_lag = Duration.getInstance(0, TimeUnit.DAYS);
      }
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
    * This method retrieves the lag duration associated
    * with this relationship.
    *
    * @return lag duration
    */
   public Duration getLag()
   {
      return (m_lag);
   }

   /**
    * Retrieve the source task of this relationship.
    *
    * @return source task
    */
   public Task getSourceTask()
   {
      return m_sourceTask;
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
    * {@inheritDoc}
    */
   @Override public String toString()
   {
      return ("[Relation " + m_sourceTask + " -> " + m_targetTask + "]");
   }

   /**
    * Parent task file.
    */
   private Task m_sourceTask;

   /**
    * Identifier of task with which this relationship is held.
    */
   private Task m_targetTask;

   /**
    * Type of relationship.
    */
   private RelationType m_type;

   /**
    * Lag between the two tasks.
    */
   private Duration m_lag;
}
