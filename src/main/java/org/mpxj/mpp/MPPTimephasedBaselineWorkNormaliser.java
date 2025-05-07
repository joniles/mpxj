/*
 * file:       MppTimephasedBaselineWorkNormaliser.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2011
 * date:       02/12/2011
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

package org.mpxj.mpp;

import java.time.LocalDateTime;
import java.util.ArrayList;

import java.util.List;

import org.mpxj.Duration;
import org.mpxj.ProjectCalendar;
import org.mpxj.TimeUnit;
import org.mpxj.TimephasedWork;
import org.mpxj.common.LocalDateTimeHelper;

/**
 * Normalise timephased data from an MPP file.
 */
public final class MPPTimephasedBaselineWorkNormaliser extends MPPAbstractTimephasedWorkNormaliser
{
   /**
    * Private constructor to prevent instantiation.
    */
   private MPPTimephasedBaselineWorkNormaliser()
   {

   }

   /**
    * This method merges together timephased data for the same day.
    *
    * @param calendar current calendar
    * @param list timephased data
    */
   @Override protected void mergeSameDay(ProjectCalendar calendar, List<TimephasedWork> list)
   {
      List<TimephasedWork> result = new ArrayList<>();

      TimephasedWork previousItem = null;
      for (TimephasedWork item : list)
      {
         if (previousItem != null)
         {
            LocalDateTime previousItemStart = previousItem.getStart();
            LocalDateTime previousItemStartDay = LocalDateTimeHelper.getDayStartDate(previousItemStart);
            LocalDateTime itemStart = item.getStart();
            LocalDateTime itemStartDay = LocalDateTimeHelper.getDayStartDate(itemStart);

            if (previousItemStartDay.equals(itemStartDay))
            {
               result.remove(result.size() - 1);

               double work = previousItem.getTotalAmount().getDuration();
               work += item.getTotalAmount().getDuration();
               Duration totalWork = Duration.getInstance(work, TimeUnit.MINUTES);

               TimephasedWork merged = new TimephasedWork();
               merged.setStart(previousItem.getStart());
               merged.setFinish(item.getFinish());
               merged.setTotalAmount(totalWork);
               item = merged;
            }

         }
         item.setAmountPerDay(item.getTotalAmount());
         result.add(item);

         previousItem = item;
      }

      list.clear();
      list.addAll(result);
   }

   public static final MPPTimephasedBaselineWorkNormaliser INSTANCE = new MPPTimephasedBaselineWorkNormaliser();
}
