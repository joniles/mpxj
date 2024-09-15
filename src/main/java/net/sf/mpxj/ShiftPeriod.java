package net.sf.mpxj;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class ShiftPeriod implements ProjectEntityWithUniqueID, Comparable<ShiftPeriod>
{
   private ShiftPeriod(Builder builder)
   {
      m_uniqueID = builder.m_sequenceProvider.getUniqueIdObjectSequence(ShiftPeriod.class).syncOrGetNext(builder.m_uniqueID);
      m_startHour = builder.m_startHour;
      m_parentShift = builder.m_shift;
      m_parentShift.getPeriods().add(this);
   }

   public Shift getParentShift()
   {
      return m_parentShift;
   }

   public Integer getUniqueID()
   {
      return m_uniqueID;
   }

   public LocalTime getStartHour()
   {
      return m_startHour;
   }

   public Duration getDuration()
   {
      LocalTime nextStart = m_parentShift.getPeriods().stream().filter(p -> p.m_startHour.isAfter(m_startHour)).findFirst().map(p -> p.m_startHour).orElse(LocalTime.MIDNIGHT);
      long hours = m_startHour.until(nextStart, ChronoUnit.HOURS);
      if (hours < 0)
      {
         hours = 24 + hours;
      }
      return Duration.getInstance(hours, TimeUnit.HOURS);
   }

   @Override public int compareTo(ShiftPeriod o)
   {
      return this.m_startHour.compareTo(o.m_startHour);
   }

   private final Integer m_uniqueID;
   private final Shift m_parentShift;
   private final LocalTime m_startHour;

   public static class Builder
   {
      public Builder(UniqueIdObjectSequenceProvider sequenceProvider, Shift shift)
      {
         m_sequenceProvider = sequenceProvider;
         m_shift = shift;
      }

      public Builder uniqueID(Integer value)
      {
         m_uniqueID = value;
         return this;
      }

      public Builder startHour(LocalTime value)
      {
         m_startHour = value;
         return this;
      }

      public Builder startHour(Integer value)
      {
         m_startHour = LocalTime.of(value.intValue(), 0);
         return this;
      }

      public ShiftPeriod build()
      {
         return new ShiftPeriod(this);
      }

      private final UniqueIdObjectSequenceProvider m_sequenceProvider;
      private final Shift m_shift;
      private Integer m_uniqueID;
      private LocalTime m_startHour;
   }
}
