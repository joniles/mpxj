/*
 * file:       TimeUnitDefaultsContainer.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2021
 * date:       27/09/2021
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

package org.mpxj;

/**
 * Classes implementing this interface provide access to the defaults used
 * when converting duration time units.
 */
public interface TimeUnitDefaultsContainer
{
   /**
    * Retrieve the number of minutes per day.
    *
    * @return minutes per day
    */
   Integer getMinutesPerDay();

   /**
    * Retrieve the number of minutes per week.
    *
    * @return minutes per week
    */
   Integer getMinutesPerWeek();

   /**
    * Retrieve the number of minutes per month.
    *
    * @return minutes per month
    */
   Integer getMinutesPerMonth();

   /**
    * Retrieve the number of minutes per year.
    *
    * @return minutes per year
    */
   Integer getMinutesPerYear();

   /**
    * Retrieve the number of days per month.
    *
    * @return days per month
    */
   Integer getDaysPerMonth();
}
