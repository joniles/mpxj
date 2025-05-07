/*
 * file:       ResourceAssignmentCodeValue.java
 * author:     Jon Iles
 * date:       2024-12-10
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

import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents an individual assignment code value.
 */
public final class ResourceAssignmentCodeValue implements CodeValue
{
   /**
    * Constructor.
    *
    * @param builder builder
    */
   private ResourceAssignmentCodeValue(Builder builder)
   {
      m_uniqueID = builder.m_sequenceProvider.getUniqueIdObjectSequence(ResourceAssignmentCodeValue.class).syncOrGetNext(builder.m_uniqueID);
      m_resourceAssignmentCode = builder.m_resourceAssignmentCode;
      m_sequenceNumber = builder.m_sequenceNumber;
      m_name = builder.m_name;
      m_description = builder.m_description;
      m_parentValue = builder.m_parentValue;
   }

   @Override public ResourceAssignmentCode getParentCode()
   {
      return m_resourceAssignmentCode;
   }

   @Override public Integer getParentCodeUniqueID()
   {
      return m_resourceAssignmentCode == null ? null : m_resourceAssignmentCode.getUniqueID();
   }

   @Override public Integer getUniqueID()
   {
      return m_uniqueID;
   }

   @Override public Integer getSequenceNumber()
   {
      return m_sequenceNumber;
   }

   @Override public String getName()
   {
      return m_name;
   }

   @Override public String getDescription()
   {
      return m_description;
   }

   /**
    * Retrieve the parent ResourceAssignmentCodeValue.
    *
    * @return parent ResourceAssignmentCodeValue
    */
   public ResourceAssignmentCodeValue getParentValue()
   {
      return m_parentValue;
   }

   /**
    * Retrieve the parent ResourceAssignmentCodeValue unique ID.
    *
    * @return parent ResourceAssignmentCodeValue unique ID
    */
   @Override public Integer getParentValueUniqueID()
   {
      return m_parentValue == null ? null : m_parentValue.getUniqueID();
   }

   /**
    * Retrieve any children of this value.
    *
    * @return list of ResourceAssignmentCodeValue instances
    */
   @Override public List<ResourceAssignmentCodeValue> getChildValues()
   {
      return m_resourceAssignmentCode.getValues().stream().filter(a -> a.m_parentValue == this).collect(Collectors.toList());
   }

   @Override public String toString()
   {
      return m_resourceAssignmentCode.getName() + ": " + m_name;
   }

   private final ResourceAssignmentCode m_resourceAssignmentCode;
   private final Integer m_uniqueID;
   private final Integer m_sequenceNumber;
   private final String m_name;
   private final String m_description;
   private final ResourceAssignmentCodeValue m_parentValue;

   /**
    * ResourceAssignmentCodeValue builder.
    */
   public static class Builder
   {
      /**
       * Constructor.
       *
       * @param sequenceProvider parent assignment file
       */
      public Builder(UniqueIdObjectSequenceProvider sequenceProvider)
      {
         m_sequenceProvider = sequenceProvider;
      }

      /**
       * Initialise the builder from an existing ResourceAssignmentCodeValue instance.
       *
       * @param value ResourceAssignmentCodeValue instance
       * @return builder
       */
      public Builder from(ResourceAssignmentCodeValue value)
      {
         m_resourceAssignmentCode = value.m_resourceAssignmentCode;
         m_uniqueID = value.m_uniqueID;
         m_sequenceNumber = value.m_sequenceNumber;
         m_name = value.m_name;
         m_description = value.m_description;
         m_parentValue = value.m_parentValue;
         return this;
      }

      /**
       * Add parent assignment code.
       *
       * @param value assignment code
       * @return builder
       */
      public Builder resourceAssignmentCode(ResourceAssignmentCode value)
      {
         m_resourceAssignmentCode = value;
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
       * Add parent value.
       *
       * @param value parent value
       * @return builder
       */
      public Builder parentValue(ResourceAssignmentCodeValue value)
      {
         m_parentValue = value;
         return this;
      }

      /**
       * Build an ResourceAssignmentCodeValue instance.
       *
       * @return ResourceAssignmentCodeValue instance
       */
      public ResourceAssignmentCodeValue build()
      {
         return new ResourceAssignmentCodeValue(this);
      }

      private final UniqueIdObjectSequenceProvider m_sequenceProvider;
      private ResourceAssignmentCode m_resourceAssignmentCode;
      private Integer m_uniqueID;
      private Integer m_sequenceNumber;
      private String m_name;
      private String m_description;
      private ResourceAssignmentCodeValue m_parentValue;
   }
}