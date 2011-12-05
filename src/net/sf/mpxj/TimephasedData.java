/*
 * file:       TimephasedData.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2011
 * date:       2011-12-03
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

import java.util.LinkedList;
import java.util.List;

/**
 * Class used to manage timephased data.
 */
public class TimephasedData
{
   /**
    * Constructor.
    * 
    * @param calendar calendar to which the timephased data relates
    * @param normaliser normaliser used to process this data
    * @param data timephased data
    * @param raw flag indicating if this data is raw
    */
   public TimephasedData(ProjectCalendar calendar, TimephasedWorkNormaliser normaliser, List<TimephasedWork> data, boolean raw)
   {
      if (data instanceof LinkedList<?>)
      {
         m_data = (LinkedList<TimephasedWork>) data;
      }
      else
      {
         m_data = new LinkedList<TimephasedWork>(data);
      }
      m_raw = raw;
      m_calendar = calendar;
      m_normaliser = normaliser;
   }

   /**
    * Copy constructor which can be used to scale the data it is copying
    * by a given factor.
    * 
    * @param source source data
    * @param perDayFactor per day scaling factor
    * @param totalFactor total scaling factor
    */
   public TimephasedData(TimephasedData source, double perDayFactor, double totalFactor)
   {
      m_data = new LinkedList<TimephasedWork>();
      m_raw = source.m_raw;
      m_calendar = source.m_calendar;
      m_normaliser = source.m_normaliser;

      for (TimephasedWork sourceItem : source.m_data)
      {
         TimephasedWork targetItem = new TimephasedWork();
         targetItem.setStart(sourceItem.getStart());
         targetItem.setFinish(sourceItem.getFinish());
         targetItem.setModified(sourceItem.getModified());
         targetItem.setTotalAmount(Duration.getInstance(sourceItem.getTotalAmount().getDuration() * totalFactor, sourceItem.getTotalAmount().getUnits()));
         targetItem.setAmountPerDay(Duration.getInstance(sourceItem.getAmountPerDay().getDuration() * perDayFactor, sourceItem.getAmountPerDay().getUnits()));
         m_data.add(targetItem);
      }
   }

   /**
    * Retrieves the timephased data.
    * 
    * @return timephased data
    */
   public List<TimephasedWork> getData()
   {
      if (m_raw)
      {
         m_normaliser.normalise(m_calendar, m_data);
         m_raw = false;
      }
      return m_data;
   }

   /**
    * Indicates if any timephased data is present.
    * 
    * @return boolean flag
    */
   boolean hasData()
   {
      return !m_data.isEmpty();
   }

   private LinkedList<TimephasedWork> m_data;
   private boolean m_raw;
   private TimephasedWorkNormaliser m_normaliser;
   private ProjectCalendar m_calendar;
}
