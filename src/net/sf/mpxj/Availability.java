/*
 * file:       Availability.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2009
 * date:       11/06/2009
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

import java.util.Date;

/**
 * This class represents a row from a resource's availability table.
 */
public final class Availability implements Comparable<Availability>
{
   /**
    * Constructor.
    *
    * @param startDate start date
    * @param endDate end date
    * @param units units for the period
    */
   public Availability(Date startDate, Date endDate, Number units)
   {
      m_range = new DateRange(startDate, endDate);
      m_units = units;
   }

   /**
    * Retrieves the date range of the availability period.
    *
    * @return start date
    */
   public DateRange getRange()
   {
      return m_range;
   }

   /**
    * Retrieves the units for the availability period.
    *
    * @return units
    */
   public Number getUnits()
   {
      return m_units;
   }

   /**
    * {@inheritDoc}
    */
   @Override public int compareTo(Availability o)
   {
      return m_range.compareTo(o.m_range);
   }

   /**
    * {@inheritDoc}
    */
   @Override public String toString()
   {
      return "[Availability range=" + m_range + " units=" + m_units + "]";
   }

   private DateRange m_range;
   private Number m_units;
}
