/*
 * file:       TimephasedWorkContainer.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2014
 * date:       2014-11-17
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

import java.util.List;

/**
 * Timephased data container.
 */
public interface TimephasedWorkContainer
{
   /**
    * Retrieves the timephased data.
    *
    * @return timephased data
    */
   public List<TimephasedWork> getData();

   /**
    * Indicates if any timephased data is present.
    *
    * @return boolean flag
    */
   public boolean hasData();

   /**
    * Create a new TimephasedWorkContainer duplicating the content of
    * this one and applying a scaling factor.
    *
    * @param perDayFactor per day factor
    * @param totalFactor total factor
    * @return new TimephasedWorkContainer instance
    */
   public TimephasedWorkContainer applyFactor(double perDayFactor, double totalFactor);
}