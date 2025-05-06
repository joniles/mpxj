/*
 * file:       ProjectCodeValue.java
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
 * Represents an individual project code value.
 */
public final class ProjectCodeValue implements CodeValue
{
   /**
    * Constructor.
    *
    * @param builder builder
    */
   private ProjectCodeValue(Builder builder)
   {
      m_uniqueID = builder.m_sequenceProvider.getUniqueIdObjectSequence(ProjectCodeValue.class).syncOrGetNext(builder.m_uniqueID);
      m_projectCode = builder.m_projectCode;
      m_sequenceNumber = builder.m_sequenceNumber;
      m_name = builder.m_name;
      m_description = builder.m_description;
      m_parentValue = builder.m_parentValue;
   }

   @Override public ProjectCode getParentCode()
   {
      return m_projectCode;
   }

   @Override public Integer getParentCodeUniqueID()
   {
      return m_projectCode == null ? null : m_projectCode.getUniqueID();
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
    * Retrieve the parent ProjectCodeValue.
    *
    * @return parent ProjectCodeValue
    */
   public ProjectCodeValue getParentValue()
   {
      return m_parentValue;
   }

   /**
    * Retrieve the parent ProjectCodeValue unique ID.
    *
    * @return parent ProjectCodeValue unique ID
    */
   @Override public Integer getParentValueUniqueID()
   {
      return m_parentValue == null ? null : m_parentValue.getUniqueID();
   }

   /**
    * Retrieve any children of this value.
    *
    * @return list of ProjectCodeValue instances
    */
   @Override public List<ProjectCodeValue> getChildValues()
   {
      return m_projectCode.getValues().stream().filter(a -> a.m_parentValue == this).collect(Collectors.toList());
   }

   @Override public String toString()
   {
      return m_projectCode.getName() + ": " + m_name;
   }

   private final ProjectCode m_projectCode;
   private final Integer m_uniqueID;
   private final Integer m_sequenceNumber;
   private final String m_name;
   private final String m_description;
   private final ProjectCodeValue m_parentValue;

   /**
    * ProjectCodeValue builder.
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
       * Initialise the builder from an existing ProjectCodeValue instance.
       *
       * @param value ProjectCodeValue instance
       * @return builder
       */
      public Builder from(ProjectCodeValue value)
      {
         m_projectCode = value.m_projectCode;
         m_uniqueID = value.m_uniqueID;
         m_sequenceNumber = value.m_sequenceNumber;
         m_name = value.m_name;
         m_description = value.m_description;
         m_parentValue = value.m_parentValue;
         return this;
      }

      /**
       * Add parent project code.
       *
       * @param value project code
       * @return builder
       */
      public Builder projectCode(ProjectCode value)
      {
         m_projectCode = value;
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
      public Builder parentValue(ProjectCodeValue value)
      {
         m_parentValue = value;
         return this;
      }

      /**
       * Build an ProjectCodeValue instance.
       *
       * @return ProjectCodeValue instance
       */
      public ProjectCodeValue build()
      {
         return new ProjectCodeValue(this);
      }

      private final UniqueIdObjectSequenceProvider m_sequenceProvider;
      private ProjectCode m_projectCode;
      private Integer m_uniqueID;
      private Integer m_sequenceNumber;
      private String m_name;
      private String m_description;
      private ProjectCodeValue m_parentValue;
   }
}