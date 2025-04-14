/*
 * file:       ShiftPeriod.java
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

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

/**
 * Represents a Resource Shift Period.
 */
public final class ShiftPeriod implements ProjectEntityWithUniqueID, Comparable<ShiftPeriod>
{
   /**
    * Private constructor.
    *
    * @param builder builder instance
    */
   private ShiftPeriod(Builder builder)
   {
      m_uniqueID = builder.m_sequenceProvider.getUniqueIdObjectSequence(ShiftPeriod.class).syncOrGetNext(builder.m_uniqueID);
      m_start = builder.m_start;
      m_parentShift = builder.m_shift;
      m_parentShift.getPeriods().add(this);
   }

   /**
    * Retrieve the parent shift.
    *
    * @return parent shift
    */
   public Shift getParentShift()
   {
      return m_parentShift;
   }

   /**
    * Retrieve the shift period's unique ID.
    *
    * @return unique ID
    */
   @Override public Integer getUniqueID()
   {
      return m_uniqueID;
   }

   /**
    * Retrieve the shift period's start time.
    *
    * @return start time
    */
   public LocalTime getStart()
   {
      return m_start;
   }

   /**
    * Calculate the duration of this shift period.
    *
    * @return shift period duration
    */
   public Duration getDuration()
   {
      LocalTime nextStart = m_parentShift.getPeriods().stream().filter(p -> p.m_start.isAfter(m_start)).findFirst().map(p -> p.m_start).orElse(LocalTime.MIDNIGHT);
      long hours = m_start.until(nextStart, ChronoUnit.HOURS);
      if (hours < 0)
      {
         hours = 24 + hours;
      }
      return Duration.getInstance(hours, TimeUnit.HOURS);
   }

   @Override public int compareTo(ShiftPeriod o)
   {
      return this.m_start.compareTo(o.m_start);
   }

   private final Integer m_uniqueID;
   private final Shift m_parentShift;
   private final LocalTime m_start;

   /**
    * ShiftPeriod builder.
    */
   public static class Builder
   {
      /**
       * Constructor.
       *
       * @param sequenceProvider sequence provider
       * @param shift parent shift
       */
      public Builder(UniqueIdObjectSequenceProvider sequenceProvider, Shift shift)
      {
         m_sequenceProvider = sequenceProvider;
         m_shift = shift;
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
       * Add the start time.
       *
       * @param value start time
       * @return builder
       */
      public Builder start(LocalTime value)
      {
         m_start = value;
         return this;
      }

      /**
       * Add the start time.
       *
       * @param value start time
       * @return builder
       */
      public Builder start(Integer value)
      {
         m_start = LocalTime.of(value.intValue(), 0);
         return this;
      }

      /**
       * Build a ShiftPeriod instance.
       *
       * @return ShiftPeriod instance
       */
      public ShiftPeriod build()
      {
         return new ShiftPeriod(this);
      }

      private final UniqueIdObjectSequenceProvider m_sequenceProvider;
      private final Shift m_shift;
      private Integer m_uniqueID;
      private LocalTime m_start;
   }
}
