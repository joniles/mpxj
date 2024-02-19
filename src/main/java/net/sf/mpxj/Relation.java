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
public final class Relation implements ProjectEntityWithUniqueID
{
   /**
    * Default constructor.
    *
    * @param sourceTask source task instance
    * @param targetTask target task instance
    * @param type relation type
    * @param lag relation lag
    * @deprecated use Relation.Builder
    */
   @Deprecated public Relation(Task sourceTask, Task targetTask, RelationType type, Duration lag)
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
         m_lag = DEFAULT_LAG;
      }

      ProjectFile project = sourceTask.getParentFile();
      ProjectConfig projectConfig = project.getProjectConfig();
      if (projectConfig.getAutoRelationUniqueID())
      {
         setUniqueID(project.getUniqueIdObjectSequence(Relation.class).getNext());
      }
   }

   private Relation(Builder builder)
   {
      m_sourceTask = builder.m_sourceTask;
      m_targetTask = builder.m_targetTask;
      m_type = builder.m_type == null ? RelationType.FINISH_START : builder.m_type;
      m_lag = builder.m_lag == null ? Duration.getInstance(0, TimeUnit.DAYS) : builder.m_lag;

      // TODO - are these mutually exclusive - check!
      if (builder.m_uniqueID != null)
      {
         setUniqueID(builder.m_uniqueID);
      }

      ProjectFile project = m_sourceTask.getParentFile();
      ProjectConfig projectConfig = project.getProjectConfig();
      if (projectConfig.getAutoRelationUniqueID())
      {
         setUniqueID(project.getUniqueIdObjectSequence(Relation.class).getNext());
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
      m_sourceTask.getParentFile().getRelations().updateUniqueID(this, m_uniqueID, uniqueID);
      m_uniqueID = uniqueID;
   }

   @Override public String toString()
   {
      return ("[Relation lag: " + m_lag + " type: " + m_type + " " + m_targetTask + " -> " + m_sourceTask + "]");
   }

   private Integer m_uniqueID;

   /**
    * Parent task file.
    */
   private final Task m_sourceTask;

   /**
    * Identifier of task with which this relationship is held.
    */
   private final Task m_targetTask;

   /**
    * Type of relationship.
    */
   private RelationType m_type;

   /**
    * Lag between the two tasks.
    */
   private Duration m_lag;

   /**
    * Relation builder.
    */
   public static class Builder
   {
      public static Builder from(Relation relation)
      {
         return new Builder()
            .targetTask(relation.m_targetTask)
            .sourceTask(relation.m_sourceTask)
            .type(relation.m_type)
            .lag(relation.m_lag)
            .uniqueID(relation.m_uniqueID);
      }

      public Builder uniqueID(Integer value)
      {
         m_uniqueID = value;
         return this;
      }


      public Builder sourceTask(Task value)
      {
         m_sourceTask = value;
         return this;
      }

      public Builder targetTask(Task value)
      {
         m_targetTask = value;
         return this;
      }

      public Builder type(RelationType value)
      {
         m_type = value;
         return this;
      }

      public Builder lag(Duration value)
      {
         m_lag = value;
         return this;
      }

      public Relation build()
      {
         return new Relation(this);
      }

      Integer m_uniqueID;
       Task m_sourceTask;
       Task m_targetTask;
       RelationType m_type  = RelationType.FINISH_START;
       Duration m_lag = DEFAULT_LAG;
   }

   private static final Duration DEFAULT_LAG = Duration.getInstance(0, TimeUnit.DAYS);
}