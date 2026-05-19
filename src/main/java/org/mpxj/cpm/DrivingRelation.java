package org.mpxj.cpm;

import java.time.LocalDateTime;

import org.mpxj.Relation;

class DrivingRelation implements Comparable<DrivingRelation>
{
   public DrivingRelation(Relation relation, LocalDateTime earlyStart)
   {
      m_relation = relation;
      m_earlyStart = earlyStart;
   }

   public Relation getRelation()
   {
      return m_relation;
   }

   public LocalDateTime getEarlyStart()
   {
      return m_earlyStart;
   }

   @Override public int compareTo(DrivingRelation o)
   {
      return m_earlyStart.compareTo(o.getEarlyStart());
   }

   private final LocalDateTime m_earlyStart;
   private final Relation m_relation;
}
