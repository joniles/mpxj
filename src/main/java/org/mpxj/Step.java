/*
 * file:       Step.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software
 * date:       2023-01-24
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

import org.mpxj.common.NumberHelper;

/**
 * Represents an activity step.
 */
public final class Step
{
   /**
    * Constructor.
    *
    * @param builder step builder
    */
   private Step(Builder builder)
   {
      m_task = builder.m_task;
      m_uniqueID = m_task.getParentFile().getUniqueIdObjectSequence(Step.class).syncOrGetNext(builder.m_uniqueID);
      m_name = builder.m_name;
      m_percentComplete = builder.m_percentComplete;
      m_sequenceNumber = builder.m_sequenceNumber;
      m_weight = builder.m_weight;
      m_description = builder.m_description;
   }

   /**
    * Retrieve the parent task.
    *
    * @return parent task
    */
   public Task getTask()
   {
      return m_task;
   }

   /**
    * Retrieve the unique ID.
    *
    * @return unique ID
    */
   public Integer getUniqueID()
   {
      return m_uniqueID;
   }

   /**
    * Retrieve the step name.
    *
    * @return step name
    */
   public String getName()
   {
      return m_name;
   }

   /**
    * Retrieve the step percent complete.
    *
    * @return step percent complete
    */
   public Double getPercentComplete()
   {
      return m_percentComplete;
   }

   /**
    * Retrieve the step sequence number.
    *
    * @return step sequence number
    */
   public Integer getSequenceNumber()
   {
      return m_sequenceNumber;
   }

   /**
    * Retrieve the step weight.
    *
    * @return step weight
    */
   public Double getWeight()
   {
      return m_weight;
   }

   /**
    * Retrieve the step description as plain text.
    *
    * @return step description
    */
   public String getDescription()
   {
      return m_description == null ? "" : m_description.toString();
   }

   /**
    * Retrieve the step description.
    *
    * @return step description
    */
   public Notes getDescriptionObject()
   {
      return m_description;
   }

   /**
    * Retrieve a flag indicating if the step is complete.
    *
    * @return true if the step is complete
    */
   public boolean getComplete()
   {
      return NumberHelper.getDouble(m_percentComplete) == 100.0;
   }

   private final Task m_task;
   private final Integer m_uniqueID;
   private final String m_name;
   private final Double m_percentComplete;
   private final Integer m_sequenceNumber;
   private final Double m_weight;
   private final Notes m_description;

   /**
    * Step builder.
    */
   public static class Builder
   {
      /**
       * Constructor.
       *
       * @param task parent task
       */
      public Builder(Task task)
      {
         m_task = task;
      }

      /**
       * Initialise the builder from an existing Step instance.
       *
       * @param value Step instance
       * @return builder
       */
      public Builder from(Step value)
      {
         m_uniqueID = value.m_uniqueID;
         m_name = value.m_name;
         m_percentComplete = value.m_percentComplete;
         m_sequenceNumber = value.m_sequenceNumber;
         m_weight = value.m_weight;
         m_description = value.m_description;
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
       * Add the percent complete.
       *
       * @param value percent complete
       * @return builder
       */
      public Builder percentComplete(Double value)
      {
         m_percentComplete = value;
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
       * Add the weight.
       *
       * @param value weight
       * @return builder
       */
      public Builder weight(Double value)
      {
         m_weight = value;
         return this;
      }

      /**
       * Add the description.
       *
       * @param value description.
       * @return builder
       */
      public Builder description(Notes value)
      {
         m_description = value;
         return this;
      }

      /**
       * Add the description.
       *
       * @param value description.
       * @return builder
       */
      public Builder description(String value)
      {
         m_description = value == null ? null : new Notes(value);
         return this;
      }

      /**
       * Build a Step instance.
       *
       * @return Step instance
       */
      public Step build()
      {
         return new Step(this);
      }

      private final Task m_task;
      private Integer m_uniqueID;
      private String m_name;
      private Double m_percentComplete;
      private Integer m_sequenceNumber;
      private Double m_weight;
      private Notes m_description;
   }
}
