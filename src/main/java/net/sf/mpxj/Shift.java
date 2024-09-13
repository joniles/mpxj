package net.sf.mpxj;

import java.util.ArrayList;
import java.util.List;

public class Shift implements ProjectEntityWithUniqueID
{
   public Shift(Integer uniqueID, String name)
   {
      m_uniqueID = uniqueID;
      m_name = name;
   }

   public Integer getUniqueID()
   {
      return m_uniqueID;
   }

   public String getName()
   {
      return m_name;
   }

   public List<ShiftPeriod> getPeriods()
   {
      return m_periods;
   }

   public void addPeriod(ShiftPeriod period)
   {
      m_periods.add(period);
   }

   private final Integer m_uniqueID;
   private final String m_name;
   private final List<ShiftPeriod> m_periods = new ArrayList<>();
}
