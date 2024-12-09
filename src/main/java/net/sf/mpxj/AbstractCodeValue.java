/*
 * file:       AbstractCodeValue.java
 * author:     Jon Iles
 * date:       2024-12-09
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

import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents an individual activity code value.
 */
public abstract class AbstractCodeValue<B, V extends CodeValue, C extends Code<V>> implements CodeValue
{
   /**
    * Constructor.
    *
    * @param builder builder
    */
   protected AbstractCodeValue(Builder<B, V, C> builder)
   {
      m_uniqueID = builder.m_sequenceProvider.getUniqueIdObjectSequence(ActivityCodeValue.class).syncOrGetNext(builder.m_uniqueID);
      m_parentCode = builder.m_parentCode;
      m_sequenceNumber = builder.m_sequenceNumber;
      m_name = builder.m_name;
      m_description = builder.m_description;
      m_parentValue = builder.m_parentValue;
   }


   /**
    * Retrieves the parent activity code.
    *
    * @return ActivityCode instance
    */
   @Override public C getParentCode()
   {
      return m_parentCode;
   }

   /**
    * Retrieves the unique ID for this value.
    *
    * @return unique ID
    */
   public Integer getUniqueID()
   {
      return m_uniqueID;
   }

   /**
    * Retrieves the sequence number for this value.
    *
    * @return sequence number
    */
   public Integer getSequenceNumber()
   {
      return m_sequenceNumber;
   }

   /**
    * Retrieves the value name.
    *
    * @return value name
    */
   public String getName()
   {
      return m_name;
   }

   /**
    * Retrieves the value description.
    *
    * @return value description
    */
   public String getDescription()
   {
      return m_description;
   }

   /**
    * Retrieve the parent ActivityCodeValue.
    *
    * @return parent ActivityCodeValue
    */
   public V getParentValue()
   {
      return m_parentValue;
   }

   /**
    * Retrieve the parent ActivityCodeValue unique ID.
    *
    * @return parent ActivityCodeValue unique ID
    */
   public Integer getParentValueUniqueID()
   {
      return m_parentValue == null ? null : m_parentValue.getUniqueID();
   }

   /**
    * Retrieve any children of this value.
    *
    * @return list of ActivityCodeValue instances
    */
   public List<V> getChildValues()
   {
      return m_parentCode.getValues().stream().filter(a -> a.getParentValue() == this).collect(Collectors.toList());
   }

   @Override public String toString()
   {
      return m_parentCode.getName() + ": " + m_name;
   }

   protected final C m_parentCode;
   protected final Integer m_uniqueID;
   protected final Integer m_sequenceNumber;
   protected final String m_name;
   protected final String m_description;
   protected final V m_parentValue;

   /**
    * ActivityCodeValue builder.
    */
   public static abstract class Builder<B, V extends CodeValue, C extends Code<V>>
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
       * Add parent activity code.
       *
       * @param value activity code
       * @return builder
       */
      public B parentCode(C value)
      {
         m_parentCode = value;
         return self();
      }

      /**
       * Add unique ID.
       *
       * @param value unique ID
       * @return builder
       */
      public B uniqueID(Integer value)
      {
         m_uniqueID = value;
         return self();
      }

      /**
       * Add sequence number.
       *
       * @param value sequence number
       * @return builder
       */
      public B sequenceNumber(Integer value)
      {
         m_sequenceNumber = value;
         return self();
      }

      /**
       * Add name.
       *
       * @param value name
       * @return builder
       */
      public B name(String value)
      {
         m_name = value;
         return self();
      }

      /**
       * Add description.
       *
       * @param value description
       * @return builder
       */
      public B description(String value)
      {
         m_description = value;
         return self();
      }

      /**
       * Add parent value.
       *
       * @param value parent value
       * @return builder
       */
      public B parentValue(V value)
      {
         m_parentValue = value;
         return self();
      }

      protected abstract B self();

      protected final UniqueIdObjectSequenceProvider m_sequenceProvider;
      protected C m_parentCode;
      protected Integer m_uniqueID;
      protected Integer m_sequenceNumber;
      protected String m_name;
      protected String m_description;
      protected V m_parentValue;
   }
}