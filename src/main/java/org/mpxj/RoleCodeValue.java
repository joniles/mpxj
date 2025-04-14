/*
 * file:       RoleCodeValue.java
 * author:     Jon Iles
 * date:       2024-12-12
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
 * Represents an individual role code value.
 */
public final class RoleCodeValue implements CodeValue
{
   /**
    * Constructor.
    *
    * @param builder builder
    */
   private RoleCodeValue(Builder builder)
   {
      m_uniqueID = builder.m_sequenceProvider.getUniqueIdObjectSequence(RoleCodeValue.class).syncOrGetNext(builder.m_uniqueID);
      m_roleCode = builder.m_roleCode;
      m_sequenceNumber = builder.m_sequenceNumber;
      m_name = builder.m_name;
      m_description = builder.m_description;
      m_parentValue = builder.m_parentValue;
   }

   @Override public RoleCode getParentCode()
   {
      return m_roleCode;
   }

   @Override public Integer getParentCodeUniqueID()
   {
      return m_roleCode == null ? null : m_roleCode.getUniqueID();
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
    * Retrieve the parent RoleCodeValue.
    *
    * @return parent RoleCodeValue
    */
   public RoleCodeValue getParentValue()
   {
      return m_parentValue;
   }

   /**
    * Retrieve the parent RoleCodeValue unique ID.
    *
    * @return parent RoleCodeValue unique ID
    */
   @Override public Integer getParentValueUniqueID()
   {
      return m_parentValue == null ? null : m_parentValue.getUniqueID();
   }

   /**
    * Retrieve any children of this value.
    *
    * @return list of RoleCodeValue instances
    */
   @Override public List<RoleCodeValue> getChildValues()
   {
      return m_roleCode.getValues().stream().filter(a -> a.m_parentValue == this).collect(Collectors.toList());
   }

   @Override public String toString()
   {
      return m_roleCode.getName() + ": " + m_name;
   }

   private final RoleCode m_roleCode;
   private final Integer m_uniqueID;
   private final Integer m_sequenceNumber;
   private final String m_name;
   private final String m_description;
   private final RoleCodeValue m_parentValue;

   /**
    * RoleCodeValue builder.
    */
   public static class Builder
   {
      /**
       * Constructor.
       *
       * @param sequenceProvider parent role file
       */
      public Builder(UniqueIdObjectSequenceProvider sequenceProvider)
      {
         m_sequenceProvider = sequenceProvider;
      }

      /**
       * Initialise the builder from an existing RoleCodeValue instance.
       *
       * @param value RoleCodeValue instance
       * @return builder
       */
      public Builder from(RoleCodeValue value)
      {
         m_roleCode = value.m_roleCode;
         m_uniqueID = value.m_uniqueID;
         m_sequenceNumber = value.m_sequenceNumber;
         m_name = value.m_name;
         m_description = value.m_description;
         m_parentValue = value.m_parentValue;
         return this;
      }

      /**
       * Add parent role code.
       *
       * @param value role code
       * @return builder
       */
      public Builder roleCode(RoleCode value)
      {
         m_roleCode = value;
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
      public Builder parentValue(RoleCodeValue value)
      {
         m_parentValue = value;
         return this;
      }

      /**
       * Build an RoleCodeValue instance.
       *
       * @return RoleCodeValue instance
       */
      public RoleCodeValue build()
      {
         return new RoleCodeValue(this);
      }

      private final UniqueIdObjectSequenceProvider m_sequenceProvider;
      private RoleCode m_roleCode;
      private Integer m_uniqueID;
      private Integer m_sequenceNumber;
      private String m_name;
      private String m_description;
      private RoleCodeValue m_parentValue;
   }
}