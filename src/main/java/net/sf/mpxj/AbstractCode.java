/*
 * file:       AbstractCode.java
 * author:     Jon Iles
 * date:       2024-12-07
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

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCode<T> implements ProjectEntityWithUniqueID
{
   protected AbstractCode(Class c, Builder builder)
   {
      m_uniqueID = builder.m_sequenceProvider.getUniqueIdObjectSequence(c).syncOrGetNext(builder.m_uniqueID);
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
    * Retrieve the sequence number of this activity code.
    *
    * @return sequence number
    */
   public Integer getSequenceNumber()
   {
      return m_sequenceNumber;
   }

   /**
    * Retrieve the activity code name.
    *
    * @return name
    */
   public String getName()
   {
      return m_name;
   }

   /**
    * Retrieve the secure flag.
    *
    * @return secure flag
    */
   public boolean getSecure()
   {
      return m_secure;
   }

   /**
    * Retrieve the max length.
    *
    * @return max length
    */
   public Integer getMaxLength()
   {
      return m_maxLength;
   }

   /**
    * Add a value to this code.
    *
    * @param value activity code value
    */
   public void addValue(T value)
   {
      m_values.add(value);
   }

   /**
    * Retrieve a list of all values for this code,
    * including child values from the hierarchy.
    *
    * @return list of ActivityCodeValue instances
    */
   public List<T> getValues()
   {
      return m_values;
   }

   protected final Integer m_uniqueID;
   protected final Integer m_sequenceNumber;
   protected final String m_name;
   protected final boolean m_secure;
   protected final Integer m_maxLength;
   protected final List<T> m_values = new ArrayList<>();

   /**
    * ActivityCode builder.
    */
   public abstract static class Builder<T>
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
       * Add unique ID.
       *
       * @param value unique ID
       * @return builder
       */
      public T uniqueID(Integer value)
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
      public T sequenceNumber(Integer value)
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
      public T name(String value)
      {
         m_name = value;
         return self();
      }

      /**
       * Add secure flag.
       *
       * @param value secure flag
       * @return builder
       */
      public T secure(boolean value)
      {
         m_secure = value;
         return self();
      }

      /**
       * Add max length.
       *
       * @param value max length
       * @return builder
       */
      public T maxLength(Integer value)
      {
         m_maxLength = value;
         return self();
      }

      protected abstract T self();

      protected final UniqueIdObjectSequenceProvider m_sequenceProvider;
      protected Integer m_uniqueID;
      protected Integer m_sequenceNumber;
      protected String m_name;
      protected boolean m_secure;
      protected Integer m_maxLength;
   }
}