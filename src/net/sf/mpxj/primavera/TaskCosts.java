/*
 * file:       TaskCosts.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2013
 * date:       12/09/2013
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

package net.sf.mpxj.primavera;

/**
 * Package private class used as a container for
 * summarised task costs..
 */
class TaskCosts
{
   /**
    * Add a planned cost value.
    *
    * @param value cost value
    */
   public void addPlanned(Double value)
   {
      if (value != null)
      {
         m_planned += value.doubleValue();
      }
   }

   /**
    * Add an actual cost value.
    *
    * @param value cost value
    */
   public void addActual(Double value)
   {
      if (value != null)
      {
         m_actual += value.doubleValue();
      }
   }

   /**
    * Add a remaining cost value.
    *
    * @param value cost value
    */
   public void addRemaining(Double value)
   {
      if (value != null)
      {
         m_remaining += value.doubleValue();
      }
   }

   /**
    * Retrieve the planned cost for a task.
    *
    * @return cost value
    */
   public Double getPlanned()
   {
      return Double.valueOf(m_planned);
   }

   /**
    * Retrieved the actual cost for a task.
    *
    * @return cost value
    */
   public Double getActual()
   {
      return Double.valueOf(m_actual);
   }

   /**
    * Retrieved the remaining cost for a task.
    *
    * @return cost value
    */
   public Double getRemaining()
   {
      return Double.valueOf(m_remaining);
   }

   private double m_planned;
   private double m_actual;
   private double m_remaining;
}
