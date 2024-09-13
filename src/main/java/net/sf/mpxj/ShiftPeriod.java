package net.sf.mpxj;

import java.time.LocalTime;

public class ShiftPeriod implements ProjectEntityWithUniqueID
{
   public ShiftPeriod(Builder builder)
   {
      m_parentShift = builder.m_shift;
      m_uniqueID = builder.m_sequenceProvider.getUniqueIdObjectSequence(ShiftPeriod.class).syncOrGetNext(builder.m_uniqueID);
      m_startHour = builder.m_startHour;
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

      public void uniqueID(Integer value)
      {
         m_uniqueID = value;
      }

      public void startHour(LocalTime value)
      {
         m_startHour = value;
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
