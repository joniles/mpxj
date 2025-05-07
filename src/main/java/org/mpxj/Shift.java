/*
 * file:       Shift.java
 * author:     Jon Iles
 * date:       2024-09-16
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

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

/**
 * Represents a Resource Shift.
 */
public final class Shift implements ProjectEntityWithUniqueID
{
   /**
    * Private constructor.
    *
    * @param builder builder instance
    */
   private Shift(Builder builder)
   {
      m_uniqueID = builder.m_sequenceProvider.getUniqueIdObjectSequence(Shift.class).syncOrGetNext(builder.m_uniqueID);
      m_name = builder.m_name;
   }

   /**
    * Retrieve the shift's unique ID.
    *
    * @return shift unique ID
    */
   @Override public Integer getUniqueID()
   {
      return m_uniqueID;
   }

   /**
    * Retrieves the shift's name.
    *
    * @return shift name
    */
   public String getName()
   {
      return m_name;
   }

   /**
    * Retrieves the periods which make up this shift.
    * This collection is sorted by shift period start time.
    *
    * @return shift periods
    */
   public Collection<ShiftPeriod> getPeriods()
   {
      return m_periods;
   }

   private final Integer m_uniqueID;
   private final String m_name;
   private final Set<ShiftPeriod> m_periods = new TreeSet<>();

   /**
    * Shift builder.
    */
   public static class Builder
   {
      /**
       * Constructor.
       *
       * @param sequenceProvider sequence provider
       */
      public Builder(UniqueIdObjectSequenceProvider sequenceProvider)
      {
         m_sequenceProvider = sequenceProvider;
      }

      /**
       * Add the unique ID.
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
       * Add the name.
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
       * Build a Shift instance.
       *
       * @return Shift instance
       */
      public Shift build()
      {
         return new Shift(this);
      }

      private final UniqueIdObjectSequenceProvider m_sequenceProvider;
      private Integer m_uniqueID;
      private String m_name;
   }
}
