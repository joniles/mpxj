/*
 * file:       ActivityCodeValue.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2018
 * date:       18/06/2018
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

import java.awt.Color;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents an individual activity code value.
 */
public final class ActivityCodeValue implements CodeValue
{
   /**
    * Constructor.
    *
    * @param builder builder
    */
   private ActivityCodeValue(Builder builder)
   {
      m_uniqueID = builder.m_sequenceProvider.getUniqueIdObjectSequence(ActivityCodeValue.class).syncOrGetNext(builder.m_uniqueID);
      m_activityCode = builder.m_activityCode;
      m_sequenceNumber = builder.m_sequenceNumber;
      m_name = builder.m_name;
      m_description = builder.m_description;
      m_color = builder.m_color;
      m_parentValue = builder.m_parentValue;
   }

   @Override public ActivityCode getParentCode()
   {
      return m_activityCode;
   }

   @Override public Integer getParentCodeUniqueID()
   {
      return m_activityCode == null ? null : m_activityCode.getUniqueID();
   }

   /**
    * Retrieves the unique ID for this value.
    *
    * @return unique ID
    */
   @Override public Integer getUniqueID()
   {
      return m_uniqueID;
   }

   /**
    * Retrieves the sequence number for this value.
    *
    * @return sequence number
    */
   @Override public Integer getSequenceNumber()
   {
      return m_sequenceNumber;
   }

   /**
    * Retrieves the value name.
    *
    * @return value name
    */
   @Override public String getName()
   {
      return m_name;
   }

   /**
    * Retrieves the value description.
    *
    * @return value description
    */
   @Override public String getDescription()
   {
      return m_description;
   }

   /**
    * Retrieves the color associated with this value.
    *
    * @return Color instance
    */
   public Color getColor()
   {
      return m_color;
   }

   /**
    * Retrieve the parent ActivityCodeValue.
    *
    * @return parent ActivityCodeValue
    */
   public ActivityCodeValue getParentValue()
   {
      return m_parentValue;
   }

   /**
    * Retrieve the parent ActivityCodeValue unique ID.
    *
    * @return parent ActivityCodeValue unique ID
    */
   @Override public Integer getParentValueUniqueID()
   {
      return m_parentValue == null ? null : m_parentValue.getUniqueID();
   }

   /**
    * Retrieve any children of this value.
    *
    * @return list of ActivityCodeValue instances
    */
   @Override public List<ActivityCodeValue> getChildValues()
   {
      return m_activityCode.getValues().stream().filter(a -> a.m_parentValue == this).collect(Collectors.toList());
   }

   @Override public String toString()
   {
      return m_activityCode.getName() + ": " + m_name;
   }

   private final ActivityCode m_activityCode;
   private final Integer m_uniqueID;
   private final Integer m_sequenceNumber;
   private final String m_name;
   private final String m_description;
   private final Color m_color;
   private final ActivityCodeValue m_parentValue;

   /**
    * ActivityCodeValue builder.
    */
   public static class Builder
   {
      /**
       * Constructor.
       *
       * @param sequenceProvider parent project file
       */
      public Builder(UniqueIdObjectSequenceProvider sequenceProvider)
      {
         m_sequenceProvider = sequenceProvider;
      }

      /**
       * Initialise the builder from an existing ActivityCodeValue instance.
       *
       * @param value ActivityCodeValue instance
       * @return builder
       */
      public Builder from(ActivityCodeValue value)
      {
         m_activityCode = value.m_activityCode;
         m_uniqueID = value.m_uniqueID;
         m_sequenceNumber = value.m_sequenceNumber;
         m_name = value.m_name;
         m_description = value.m_description;
         m_color = value.m_color;
         m_parentValue = value.m_parentValue;
         return this;
      }

      /**
       * Add parent activity code.
       *
       * @param value activity code
       * @return builder
       */
      public Builder activityCode(ActivityCode value)
      {
         m_activityCode = value;
         return this;
      }

      /**
       * Add unique ID.
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
       * Add sequence number.
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
       * Add name.
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
       * Add description.
       *
       * @param value description
       * @return builder
       */
      public Builder description(String value)
      {
         m_description = value;
         return this;
      }

      /**
       * Add color.
       *
       * @param value color
       * @return builder
       */
      public Builder color(Color value)
      {
         m_color = value;
         return this;
      }

      /**
       * Add parent value.
       *
       * @param value parent value
       * @return builder
       */
      public Builder parentValue(ActivityCodeValue value)
      {
         m_parentValue = value;
         return this;
      }

      /**
       * Build an ActivityCodeValue instance.
       *
       * @return ActivityCodeValue instance
       */
      public ActivityCodeValue build()
      {
         return new ActivityCodeValue(this);
      }

      private final UniqueIdObjectSequenceProvider m_sequenceProvider;
      private ActivityCode m_activityCode;
      private Integer m_uniqueID;
      private Integer m_sequenceNumber;
      private String m_name;
      private String m_description;
      private Color m_color;
      private ActivityCodeValue m_parentValue;
   }
}