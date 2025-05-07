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

package org.mpxj;

/**
 * This class represents the relationship between two tasks.
 */
public final class Relation implements ProjectEntityWithMutableUniqueID
{
   /**
    * Constructor.
    *
    * @param builder Builder instance
    */
   private Relation(Builder builder)
   {
      m_predecessorTask = builder.m_predecessorTask;
      m_successorTask = builder.m_successorTask;
      m_type = builder.m_type == null ? RelationType.FINISH_START : builder.m_type;
      m_lag = builder.m_lag == null ? Duration.getInstance(0, TimeUnit.DAYS) : builder.m_lag;
      m_notes = builder.m_notes;

      Integer uniqueID = builder.m_uniqueID;

      if (uniqueID == null)
      {
         ProjectFile project = m_successorTask.getParentFile();
         ProjectConfig projectConfig = project.getProjectConfig();
         if (projectConfig.getAutoRelationUniqueID())
         {
            uniqueID = project.getUniqueIdObjectSequence(Relation.class).getNext();
         }
      }

      if (uniqueID != null)
      {
         m_successorTask.getParentFile().getRelations().updateUniqueID(this, m_uniqueID, uniqueID);
         m_uniqueID = uniqueID;
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
    * Retrieve the predecessor task of this relationship.
    *
    * @return predecessor task
    */
   public Task getPredecessorTask()
   {
      return m_predecessorTask;
   }

   /**
    * Retrieve the successor task of this relationship.
    *
    * @return successor task
    */
   public Task getSuccessorTask()
   {
      return m_successorTask;
   }

   /**
    * Retrieve the Unique ID of this Relation.
    *
    * @return unique ID
    */
   @Override public Integer getUniqueID()
   {
      return m_uniqueID;
   }

   /**
    * Set the Unique ID of this Relation.
    *
    * @param uniqueID unique ID
    */
   @Override public void setUniqueID(Integer uniqueID)
   {
      m_successorTask.getParentFile().getRelations().updateUniqueID(this, m_uniqueID, uniqueID);
      m_uniqueID = uniqueID;
   }

   /**
    * Retrieve the notes association with this relationship.
    *
    * @return notes
    */
   public String getNotes()
   {
      return m_notes;
   }

   @Override public String toString()
   {
      return ("[Relation lag: " + m_lag + " type: " + m_type + " " + m_predecessorTask + " -> " + m_successorTask + "]");
   }

   private Integer m_uniqueID;
   private final Task m_predecessorTask;
   private final Task m_successorTask;

   /**
    * Type of relationship.
    */
   private final RelationType m_type;

   /**
    * Lag between the two tasks.
    */
   private final Duration m_lag;

   private final String m_notes;

   /**
    * Relation builder.
    */
   public static class Builder
   {
      /**
       * Create a builder from a Relation instance.
       *
       * @param value relation instance
       * @return Builder instance
       */
      public Builder from(Relation value)
      {
         m_uniqueID = value.m_uniqueID;
         m_predecessorTask = value.m_predecessorTask;
         m_successorTask = value.m_successorTask;
         m_type = value.m_type;
         m_lag = value.m_lag;
         m_notes = value.m_notes;
         return this;
      }

      /**
       * Add the unique ID.
       *
       * @param value unique ID
       * @return builder
       */
      public Builder uniqueID(Integer value)
      {
         m_uniqueID = value;
         return this;
      }

      /**
       * Add the predecessor task.
       *
       * @param value predecessor task
       * @return builder
       */
      public Builder predecessorTask(Task value)
      {
         m_predecessorTask = value;
         return this;
      }

      /**
       * Add the successor task.
       *
       * @param value successor task
       * @return builder
       */
      public Builder successorTask(Task value)
      {
         m_successorTask = value;
         return this;
      }

      /**
       * Add the type.
       *
       * @param value type
       * @return builder
       */
      public Builder type(RelationType value)
      {
         m_type = value;
         return this;
      }

      /**
       * Add the lag.
       *
       * @param value lag
       * @return builder
       */
      public Builder lag(Duration value)
      {
         m_lag = value;
         return this;
      }

      /**
       * Add notes.
       *
       * @param value notes
       * @return builder
       */
      public Builder notes(String value)
      {
         m_notes = value;
         return this;
      }

      /**
       * Build a Relation instance.
       *
       * @return Relation instance
       */
      public Relation build()
      {
         return new Relation(this);
      }

      Integer m_uniqueID;
      Task m_predecessorTask;
      Task m_successorTask;
      RelationType m_type = RelationType.FINISH_START;
      Duration m_lag = Duration.getInstance(0, TimeUnit.DAYS);
      String m_notes;
   }
}