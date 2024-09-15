package net.sf.mpxj;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Shift implements ProjectEntityWithUniqueID
{
   private Shift(Builder builder)
   {
      m_uniqueID = builder.m_sequenceProvider.getUniqueIdObjectSequence(Shift.class).syncOrGetNext(builder.m_uniqueID);
      m_name = builder.m_name;
   }

   public Integer getUniqueID()
   {
      return m_uniqueID;
   }

   public String getName()
   {
      return m_name;
   }

   public Collection<ShiftPeriod> getPeriods()
   {
      return m_periods;
   }

   private final Integer m_uniqueID;
   private final String m_name;
   private final Set<ShiftPeriod> m_periods = new TreeSet<>();

   public static class Builder
   {
      public Builder(UniqueIdObjectSequenceProvider sequenceProvider)
      {
         m_sequenceProvider = sequenceProvider;
      }

      public Builder uniqueID(Integer value)
      {
         m_uniqueID = value;
         return this;
      }

      public Builder name(String value)
      {
         m_name = value;
         return this;
      }

      public Shift build()
      {
         return new Shift(this);
      }

      private final UniqueIdObjectSequenceProvider m_sequenceProvider;
      private Integer m_uniqueID;
      private String m_name;
   }
}
