/*
 * file:       ResourceCodeValue.java
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
 * Represents an individual resource code value.
 */
public final class ResourceCodeValue implements CodeValue
{
   /**
    * Constructor.
    *
    * @param builder builder
    */
   private ResourceCodeValue(Builder builder)
   {
      m_uniqueID = builder.m_sequenceProvider.getUniqueIdObjectSequence(ResourceCodeValue.class).syncOrGetNext(builder.m_uniqueID);
      m_resourceCode = builder.m_resourceCode;
      m_sequenceNumber = builder.m_sequenceNumber;
      m_name = builder.m_name;
      m_description = builder.m_description;
      m_parentValue = builder.m_parentValue;
   }

   @Override public ResourceCode getParentCode()
   {
      return m_resourceCode;
   }

   @Override public Integer getParentCodeUniqueID()
   {
      return m_resourceCode == null ? null : m_resourceCode.getUniqueID();
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
    * Retrieve the parent ResourceCodeValue.
    *
    * @return parent ResourceCodeValue
    */
   public ResourceCodeValue getParentValue()
   {
      return m_parentValue;
   }

   /**
    * Retrieve the parent ResourceCodeValue unique ID.
    *
    * @return parent ResourceCodeValue unique ID
    */
   @Override public Integer getParentValueUniqueID()
   {
      return m_parentValue == null ? null : m_parentValue.getUniqueID();
   }

   /**
    * Retrieve any children of this value.
    *
    * @return list of ResourceCodeValue instances
    */
   @Override public List<ResourceCodeValue> getChildValues()
   {
      return m_resourceCode.getValues().stream().filter(a -> a.m_parentValue == this).collect(Collectors.toList());
   }

   @Override public String toString()
   {
      return m_resourceCode.getName() + ": " + m_name;
   }

   private final ResourceCode m_resourceCode;
   private final Integer m_uniqueID;
   private final Integer m_sequenceNumber;
   private final String m_name;
   private final String m_description;
   private final ResourceCodeValue m_parentValue;

   /**
    * ResourceCodeValue builder.
    */
   public static class Builder
   {
      /**
       * Constructor.
       *
       * @param sequenceProvider parent resource file
       */
      public Builder(UniqueIdObjectSequenceProvider sequenceProvider)
      {
         m_sequenceProvider = sequenceProvider;
      }

      /**
       * Initialise the builder from an existing ResourceCodeValue instance.
       *
       * @param value ResourceCodeValue instance
       * @return builder
       */
      public Builder from(ResourceCodeValue value)
      {
         m_resourceCode = value.m_resourceCode;
         m_uniqueID = value.m_uniqueID;
         m_sequenceNumber = value.m_sequenceNumber;
         m_name = value.m_name;
         m_description = value.m_description;
         m_parentValue = value.m_parentValue;
         return this;
      }

      /**
       * Add parent resource code.
       *
       * @param value resource code
       * @return builder
       */
      public Builder resourceCode(ResourceCode value)
      {
         m_resourceCode = value;
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
      public Builder parentValue(ResourceCodeValue value)
      {
         m_parentValue = value;
         return this;
      }

      /**
       * Build an ResourceCodeValue instance.
       *
       * @return ResourceCodeValue instance
       */
      public ResourceCodeValue build()
      {
         return new ResourceCodeValue(this);
      }

      private final UniqueIdObjectSequenceProvider m_sequenceProvider;
      private ResourceCode m_resourceCode;
      private Integer m_uniqueID;
      private Integer m_sequenceNumber;
      private String m_name;
      private String m_description;
      private ResourceCodeValue m_parentValue;
   }
}