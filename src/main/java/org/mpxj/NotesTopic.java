/*
 * file:       NotesTopic.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software
 * date:       2023-03-27
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
 * Represents a topic, used by P6 to organise notes.
 */
public final class NotesTopic implements ProjectEntityWithUniqueID
{
   /**
    * Constructor.
    *
    * @param builder NotesTopic builder
    */
   private NotesTopic(Builder builder)
   {
      m_uniqueID = builder.m_sequenceProvider == null ? builder.m_uniqueID : builder.m_sequenceProvider.getUniqueIdObjectSequence(NotesTopic.class).syncOrGetNext(builder.m_uniqueID);
      m_sequenceNumber = builder.m_sequenceNumber;
      m_name = builder.m_name;
      m_availableForEPS = builder.m_availableForEPS;
      m_availableForProject = builder.m_availableForProject;
      m_availableForWBS = builder.m_availableForWBS;
      m_availableForActivity = builder.m_availableForActivity;
   }

   @Override public Integer getUniqueID()
   {
      return m_uniqueID;
   }

   /**
    * Retrieve the sequence number.
    *
    * @return sequence number
    */
   public Integer getSequenceNumber()
   {
      return m_sequenceNumber;
   }

   /**
    * Retrieve the name.
    *
    * @return name
    */
   public String getName()
   {
      return m_name;
   }

   /**
    * Retrieve the available for EPS flag.
    *
    * @return available for EPS flag
    */
   public boolean getAvailableForEPS()
   {
      return m_availableForEPS;
   }

   /**
    * Retrieve the available for project flag.
    *
    * @return available for project flag
    */
   public boolean getAvailableForProject()
   {
      return m_availableForProject;
   }

   /**
    * Retrieve the available for WBS flag.
    *
    * @return available for WBS flag
    */
   public boolean getAvailableForWBS()
   {
      return m_availableForWBS;
   }

   /**
    * Retrieve the available for activity flag.
    *
    * @return available for activity flag
    */
   public boolean getAvailableForActivity()
   {
      return m_availableForActivity;
   }

   /**
    * NotesTopic builder.
    */
   public static class Builder
   {
      /**
       * Constructor.
       *
       * @param sequenceProvider parent file
       */
      public Builder(UniqueIdObjectSequenceProvider sequenceProvider)
      {
         m_sequenceProvider = sequenceProvider;
      }

      /**
       * Initialise the builder from an existing NotesTopic instance.
       *
       * @param value NotesTopic instance
       * @return builder
       */
      public Builder from(NotesTopic value)
      {
         m_uniqueID = value.m_uniqueID;
         m_sequenceNumber = value.m_sequenceNumber;
         m_name = value.m_name;
         m_availableForEPS = value.m_availableForEPS;
         m_availableForProject = value.m_availableForProject;
         m_availableForWBS = value.m_availableForWBS;
         m_availableForActivity = value.m_availableForActivity;
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
       * Add the sequence number.
       *
       * @param value sequence number
       * @return builder
       */
      public Builder sequenceNumber(Integer value)
      {
         m_sequenceNumber = value;
         return this;
      }

      /**
       * Add the name.
       *
       * @param value name
       * @return builder
       */
      public Builder name(String value)
      {
         m_name = value;
         return this;
      }

      /**
       * Add the available for EPS flag.
       *
       * @param value available for EPS flag
       * @return builder
       */
      public Builder availableForEPS(boolean value)
      {
         m_availableForEPS = value;
         return this;
      }

      /**
       * Add the available for project flag.
       *
       * @param value available for project flag
       * @return builder
       */
      public Builder availableForProject(boolean value)
      {
         m_availableForProject = value;
         return this;
      }

      /**
       * Add the available for WBS flag.
       *
       * @param value available for WBS flag
       * @return builder
       */
      public Builder availableForWBS(boolean value)
      {
         m_availableForWBS = value;
         return this;
      }

      /**
       * Add the available for project flag.
       *
       * @param value available for project flag
       * @return builder
       */
      public Builder availableForActivity(boolean value)
      {
         m_availableForActivity = value;
         return this;
      }

      /**
       * Build a NotesTopic instance.
       *
       * @return NotesTopic instance
       */
      public NotesTopic build()
      {
         return new NotesTopic(this);
      }

      private final UniqueIdObjectSequenceProvider m_sequenceProvider;
      private Integer m_uniqueID;
      private Integer m_sequenceNumber;
      private String m_name;
      private boolean m_availableForEPS;
      private boolean m_availableForProject;
      private boolean m_availableForWBS;
      private boolean m_availableForActivity;
   }

   private final Integer m_uniqueID;
   private final Integer m_sequenceNumber;
   private final String m_name;
   private final boolean m_availableForEPS;
   private final boolean m_availableForProject;
   private final boolean m_availableForWBS;
   private final boolean m_availableForActivity;

   public static final NotesTopic DEFAULT = new Builder(null)
      .uniqueID(Integer.valueOf(1))
      .sequenceNumber(Integer.valueOf(1))
      .name("Notes")
      .availableForEPS(true)
      .availableForProject(true)
      .availableForWBS(true)
      .availableForActivity(true)
      .build();
}
