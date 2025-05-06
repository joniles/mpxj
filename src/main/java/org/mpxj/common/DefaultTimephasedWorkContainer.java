/*
 * file:       DefaultTimephasedWorkContainer.java
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

package org.mpxj.common;

import java.util.ArrayList;
import java.util.List;

import org.mpxj.ResourceAssignment;
import org.mpxj.TimephasedWork;
import org.mpxj.TimephasedWorkContainer;

/**
 * Class used to manage timephased data.
 */
public class DefaultTimephasedWorkContainer implements TimephasedWorkContainer
{
   /**
    * Constructor.
    *
    * @param assignment resource assignment to which the timephased data relates
    * @param normaliser normaliser used to process this data
    * @param data timephased data
    * @param raw flag indicating if this data is raw
    */
   public DefaultTimephasedWorkContainer(ResourceAssignment assignment, TimephasedNormaliser<TimephasedWork> normaliser, List<TimephasedWork> data, boolean raw)
   {
      m_data = data;
      m_raw = raw;
      m_assignment = assignment;
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
   private DefaultTimephasedWorkContainer(DefaultTimephasedWorkContainer source, double perDayFactor, double totalFactor)
   {
      m_data = new ArrayList<>();
      m_raw = source.m_raw;
      m_assignment = source.m_assignment;
      m_normaliser = source.m_normaliser;

      for (TimephasedWork sourceItem : source.m_data)
      {
         m_data.add(new TimephasedWork(sourceItem, totalFactor, perDayFactor));
      }
   }

   /* (non-Javadoc)
    * @see org.mpxj.TimephasedWorkContainer#getData()
    */
   @Override public List<TimephasedWork> getData()
   {
      if (m_raw)
      {
         m_normaliser.normalise(m_assignment.getEffectiveCalendar(), m_assignment, m_data);
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

   @Override public TimephasedWorkContainer applyFactor(double perDayFactor, double totalFactor)
   {
      return new DefaultTimephasedWorkContainer(this, perDayFactor, totalFactor);
   }

   private final List<TimephasedWork> m_data;
   private boolean m_raw;
   private final TimephasedNormaliser<TimephasedWork> m_normaliser;
   private final ResourceAssignment m_assignment;
}
