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

package net.sf.mpxj;

/**
 * Activity code type definition, contains a list of the valid
 * values for this activity code.
 */
public final class ActivityCode extends AbstractCode<ActivityCodeValue>
{
   /**
    * Constructor.
    *
    * @param builder builder
    */
   private ActivityCode(Builder builder)
   {
      super(ActivityCode.class, builder);
      m_scope = builder.m_scope;
      m_scopeEpsUniqueID = builder.m_scopeEpsUniqueID;
      m_scopeProjectUniqueID = builder.m_scopeProjectUniqueID;
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

   private final ActivityCodeScope m_scope;
   private final Integer m_scopeEpsUniqueID;
   private final Integer m_scopeProjectUniqueID;

   /**
    * ActivityCode builder.
    */
   public static class Builder extends AbstractCode.Builder<Builder>
   {
      /**
       * Constructor.
       *
       * @param sequenceProvider parent file
       */
      public Builder(UniqueIdObjectSequenceProvider sequenceProvider)
      {
         super(sequenceProvider);
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
       * Build an ActivityCode instance.
       *
       * @return ActivityCode instance
       */
      public ActivityCode build()
      {
         return new ActivityCode(this);
      }

      @Override protected Builder self()
      {
         return this;
      }

      private ActivityCodeScope m_scope = ActivityCodeScope.GLOBAL;
      private Integer m_scopeEpsUniqueID;
      private Integer m_scopeProjectUniqueID;
   }
}