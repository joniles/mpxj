/*
 * file:       DefaultTimephasedCostContainer.java
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

package net.sf.mpxj.common;

import java.util.LinkedList;
import java.util.List;

import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.TimephasedCost;
import net.sf.mpxj.TimephasedCostContainer;

/**
 * Class used to manage timephased data.
 */
public class DefaultTimephasedCostContainer implements TimephasedCostContainer
{
   /**
    * Constructor.
    *
    * @param calendar calendar to which the timephased data relates
    * @param normaliser normaliser used to process this data
    * @param data timephased data
    * @param raw flag indicating if this data is raw
    */
   public DefaultTimephasedCostContainer(ProjectCalendar calendar, TimephasedCostNormaliser normaliser, List<TimephasedCost> data, boolean raw)
   {
      if (data instanceof LinkedList<?>)
      {
         m_data = (LinkedList<TimephasedCost>) data;
      }
      else
      {
         m_data = new LinkedList<TimephasedCost>(data);
      }
      m_raw = raw;
      m_calendar = calendar;
      m_normaliser = normaliser;
   }

   /* (non-Javadoc)
    * @see net.sf.mpxj.TimephasedCostContainer#getData()
    */
   @Override public List<TimephasedCost> getData()
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
   @Override public boolean hasData()
   {
      return !m_data.isEmpty();
   }

   private LinkedList<TimephasedCost> m_data;
   private boolean m_raw;
   private TimephasedCostNormaliser m_normaliser;
   private ProjectCalendar m_calendar;
}
