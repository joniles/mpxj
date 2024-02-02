package net.sf.mpxj.common;
import java.util.List;

import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.TimePeriodEntity;
import net.sf.mpxj.TimephasedCost;
import net.sf.mpxj.TimephasedWork;

public class NullNormaliser<T> implements TimephasedNormaliser<T>
{
   @Override public void normalise(ProjectCalendar calendar, TimePeriodEntity parent, List<T> list)
   {

   }

   public static final NullNormaliser<TimephasedWork> NULL_WORK_NORMALISER = new NullNormaliser<>();

   public static final NullNormaliser<TimephasedCost> NULL_COST_NORMALISER = new NullNormaliser<>();
}
