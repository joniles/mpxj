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

package org.mpxj.common;

import java.util.List;

import org.mpxj.ResourceAssignment;
import org.mpxj.TimephasedCost;
import org.mpxj.TimephasedCostContainer;

/**
 * Class used to manage timephased data.
 */
public class DefaultTimephasedCostContainer implements TimephasedCostContainer
{
   /**
    * Constructor.
    *
    * @param assignment resource assignment to which the timephased data relates
    * @param normaliser normaliser used to process this data
    * @param data timephased data
    * @param raw flag indicating if this data is raw
    */
   public DefaultTimephasedCostContainer(ResourceAssignment assignment, TimephasedNormaliser<TimephasedCost> normaliser, List<TimephasedCost> data, boolean raw)
   {
      m_data = data;
      m_raw = raw;
      m_assignment = assignment;
      m_normaliser = normaliser;
   }

   /* (non-Javadoc)
    * @see org.mpxj.TimephasedCostContainer#getData()
    */
   @Override public List<TimephasedCost> getData()
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

   private final List<TimephasedCost> m_data;
   private boolean m_raw;
   private final TimephasedNormaliser<TimephasedCost> m_normaliser;
   private final ResourceAssignment m_assignment;
}
