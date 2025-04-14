/*
 * file:       ActivityCode.java
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Activity code type definition, contains a list of the valid
 * values for this activity code.
 */
public final class ActivityCode implements Code
{
   /**
    * Constructor.
    *
    * @param builder builder
    */
   private ActivityCode(Builder builder)
   {
      m_uniqueID = builder.m_sequenceProvider.getUniqueIdObjectSequence(ActivityCode.class).syncOrGetNext(builder.m_uniqueID);
      m_scope = builder.m_scope;
      m_scopeEpsUniqueID = builder.m_scopeEpsUniqueID;
      m_scopeProjectUniqueID = builder.m_scopeProjectUniqueID;
      m_sequenceNumber = builder.m_sequenceNumber;
      m_name = builder.m_name;
      m_secure = builder.m_secure;
      m_maxLength = builder.m_maxLength;
   }

   /**
    * Retrieve the activity code unique ID.
    *
    * @return unique ID
    */
   @Override public Integer getUniqueID()
   {
      return m_uniqueID;
   }

   /**
    * Retrieve the scope of this activity code.
    *
    * @return activity code scope
    */
   public ActivityCodeScope getScope()
   {
      return m_scope;
   }

   /**
    * Scope project unique ID.
    *
    * @return project unique ID
    */
   public Integer getScopeProjectUniqueID()
   {
      return m_scopeProjectUniqueID;
   }

   /**
    * Scope EPS unique ID.
    *
    * @return EPS unique ID
    */
   public Integer getScopeEpsUniqueID()
   {
      return m_scopeEpsUniqueID;
   }

   /**
    * Retrieve the sequence number of this activity code.
    *
    * @return sequence number
    */
   @Override public Integer getSequenceNumber()
   {
      return m_sequenceNumber;
   }

   /**
    * Retrieve the activity code name.
    *
    * @return name
    */
   @Override public String getName()
   {
      return m_name;
   }

   /**
    * Retrieve the secure flag.
    *
    * @return secure flag
    */
   @Override public boolean getSecure()
   {
      return m_secure;
   }

   /**
    * Retrieve the max length.
    *
    * @return max length
    */
   @Override public Integer getMaxLength()
   {
      return m_maxLength;
   }

   /**
    * Retrieve a list of all values for this activity code,
    * including child values from the hierarchy.
    *
    * @return list of ActivityCodeValue instances
    */
   @Override public List<ActivityCodeValue> getValues()
   {
      return m_values;
   }

   /**
    * Retrieve a list of top level values for his activity code.
    * This excludes any child values from further down the
    * hierarchy of values.
    *
    * @return list of ActivityCodeValue instances
    */
   @Override public List<ActivityCodeValue> getChildValues()
   {
      return m_values.stream().filter(v -> v.getParentValue() == null).collect(Collectors.toList());
   }

   /**
    * Add a value to this activity code.
    *
    * @param value activity code value
    */
   public void addValue(ActivityCodeValue value)
   {
      m_values.add(value);
   }

   /**
    * Retrieve a value belonging to this activity code using its unique ID.
    *
    * @param id activity code value unique ID
    * @return ActivityCodeValue instance or null
    */
   public ActivityCodeValue getValueByUniqueID(Integer id)
   {
      if (id == null)
      {
         return null;
      }

      // I'd prefer a map-based lookup, but this will do for now and the list of values will typically be fairly short
      return m_values.stream().filter(v -> v.getUniqueID().intValue() == id.intValue()).findFirst().orElse(null);
   }

   private final Integer m_uniqueID;
   private final ActivityCodeScope m_scope;
   private final Integer m_scopeEpsUniqueID;
   private final Integer m_scopeProjectUniqueID;
   private final Integer m_sequenceNumber;
   private final String m_name;
   private final boolean m_secure;
   private final Integer m_maxLength;
   private final List<ActivityCodeValue> m_values = new ArrayList<>();

   /**
    * ActivityCode builder.
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
       * Initialise the builder from an existing ActivityCode instance.
       *
       * @param value ActivityCode instance
       * @return builder
       */
      public Builder from(ActivityCode value)
      {
         m_uniqueID = value.m_uniqueID;
         m_scope = value.m_scope;
         m_scopeEpsUniqueID = value.m_scopeEpsUniqueID;
         m_scopeProjectUniqueID = value.m_scopeProjectUniqueID;
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
       * Add scope.
       *
       * @param value scope
       * @return builder
       */
      public Builder scope(ActivityCodeScope value)
      {
         m_scope = value;
         return this;
      }

      /**
       * Add scope EPS ID.
       *
       * @param value scope EPS ID
       * @return builder
       */
      public Builder scopeEpsUniqueID(Integer value)
      {
         m_scopeEpsUniqueID = value;
         return this;
      }

      /**
       * Add scope project ID.
       *
       * @param value scope project ID
       * @return builder
       */
      public Builder scopeProjectUniqueID(Integer value)
      {
         m_scopeProjectUniqueID = value;
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
       * Build an ActivityCode instance.
       *
       * @return ActivityCode instance
       */
      public ActivityCode build()
      {
         return new ActivityCode(this);
      }

      private final UniqueIdObjectSequenceProvider m_sequenceProvider;
      private Integer m_uniqueID;
      private ActivityCodeScope m_scope = ActivityCodeScope.GLOBAL;
      private Integer m_scopeEpsUniqueID;
      private Integer m_scopeProjectUniqueID;
      private Integer m_sequenceNumber;
      private String m_name;
      private boolean m_secure;
      private Integer m_maxLength;
   }
}