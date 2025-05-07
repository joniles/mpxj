/*
 * file:       ResourceCode.java
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Resource code type definition, contains a list of the valid
 * values for this resource code.
 */
public final class ResourceCode implements Code
{
   /**
    * Constructor.
    *
    * @param builder builder
    */
   private ResourceCode(Builder builder)
   {
      m_uniqueID = builder.m_sequenceProvider.getUniqueIdObjectSequence(ResourceCode.class).syncOrGetNext(builder.m_uniqueID);
      m_sequenceNumber = builder.m_sequenceNumber;
      m_name = builder.m_name;
      m_secure = builder.m_secure;
      m_maxLength = builder.m_maxLength;
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

   @Override public boolean getSecure()
   {
      return m_secure;
   }

   @Override public Integer getMaxLength()
   {
      return m_maxLength;
   }

   @Override public List<ResourceCodeValue> getValues()
   {
      return m_values;
   }

   @Override public List<ResourceCodeValue> getChildValues()
   {
      return m_values.stream().filter(v -> v.getParentValue() == null).collect(Collectors.toList());
   }

   /**
    * Add value to this code.
    *
    * @param value new value
    */
   public void addValue(ResourceCodeValue value)
   {
      m_values.add(value);
   }

   /**
    * Retrieve a value by unique ID.
    *
    * @param id unique ID
    * @return value or null
    */
   public ResourceCodeValue getValueByUniqueID(Integer id)
   {
      if (id == null)
      {
         return null;
      }

      // I'd prefer a map-based lookup, but this will do for now and the list of values will typically be fairly short
      return m_values.stream().filter(v -> v.getUniqueID().intValue() == id.intValue()).findFirst().orElse(null);
   }

   private final Integer m_uniqueID;
   private final Integer m_sequenceNumber;
   private final String m_name;
   private final boolean m_secure;
   private final Integer m_maxLength;
   private final List<ResourceCodeValue> m_values = new ArrayList<>();

   /**
    * ResourceCode builder.
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
       * Initialise the builder from an existing ResourceCode instance.
       *
       * @param value ResourceCode instance
       * @return builder
       */
      public Builder from(ResourceCode value)
      {
         m_uniqueID = value.m_uniqueID;
         m_sequenceNumber = value.m_sequenceNumber;
         m_name = value.m_name;
         m_secure = value.m_secure;
         m_maxLength = value.m_maxLength;
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
       * Add secure flag.
       *
       * @param value secure flag
       * @return builder
       */
      public Builder secure(boolean value)
      {
         m_secure = value;
         return this;
      }

      /**
       * Add max length.
       *
       * @param value max length
       * @return builder
       */
      public Builder maxLength(Integer value)
      {
         m_maxLength = value;
         return this;
      }

      /**
       * Build an ResourceCode instance.
       *
       * @return ResourceCode instance
       */
      public ResourceCode build()
      {
         return new ResourceCode(this);
      }

      private final UniqueIdObjectSequenceProvider m_sequenceProvider;
      private Integer m_uniqueID;
      private Integer m_sequenceNumber;
      private String m_name;
      private boolean m_secure;
      private Integer m_maxLength;
   }
}