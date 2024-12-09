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

package net.sf.mpxj;

import java.awt.Color;

/**
 * Represents an individual activity code value.
 */
public final class ActivityCodeValue extends AbstractCodeValue<ActivityCodeValue.Builder, ActivityCodeValue, ActivityCode>
{
   /**
    * Constructor.
    *
    * @param builder builder
    */
   private ActivityCodeValue(Builder builder)
   {
      super(builder);
      m_color = builder.m_color;
   }

   /**
    * Retrieves the parent activity code.
    *
    * @return ActivityCode instance
    * @deprecated use getActivityCode instead
    */
   @Deprecated public ActivityCode getType()
   {
      return m_parentCode;
   }

   /**
    * Retrieves the parent activity code.
    *
    * @return ActivityCode instance
    */
   public ActivityCode getActivityCode()
   {
      return m_parentCode;
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
   @Deprecated public ActivityCodeValue getParent()
   {
      return getParentValue();
   }

   /**
    * Retrieve the parent ActivityCodeValue unique ID.
    *
    * @return parent ActivityCodeValue unique ID
    */
   @Deprecated public Integer getParentUniqueID()
   {
      return getParentValueUniqueID();
   }

   private final Color m_color;


   /**
    * ActivityCodeValue builder.
    */
   public static class Builder extends AbstractCodeValue.Builder<Builder, ActivityCodeValue, ActivityCode>
   {
      /**
       * Constructor.
       *
       * @param sequenceProvider parent project file
       */
      public Builder(UniqueIdObjectSequenceProvider sequenceProvider)
      {
         super(sequenceProvider);
      }

      /**
       * Initialise the builder from an existing ActivityCodeValue instance.
       *
       * @param value ActivityCodeValue instance
       * @return builder
       */
      public Builder from(ActivityCodeValue value)
      {
         m_parentCode = value.m_parentCode;
         m_uniqueID = value.m_uniqueID;
         m_sequenceNumber = value.m_sequenceNumber;
         m_name = value.m_name;
         m_description = value.m_description;
         m_color = value.m_color;
         m_parentValue = value.m_parentValue;
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
       * Add parent activity code.
       *
       * @param value activity code
       * @return builder
       * @deprecated use activityCode instead
       */
      @Deprecated public Builder type(ActivityCode value)
      {
         m_parentCode = value;
         return this;
      }

      /**
       * Add parent activity code.
       *
       * @param value activity code
       * @return builder
       */
      @Deprecated public Builder activityCode(ActivityCode value)
      {
         m_parentCode = value;
         return this;
      }

      /**
       * Add parent value.
       *
       * @param value parent value
       * @return builder
       */
      @Deprecated public Builder parent(ActivityCodeValue value)
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

      @Override protected Builder self()
      {
         return this;
      }

      private Color m_color;
   }
}