/*
 * file:       TimephasedNormaliser.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2009
 * date:       09/01/2009
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

import org.mpxj.TimePeriodEntity;
import org.mpxj.ProjectCalendar;

/**
 * Classes implementing this interface are used to normalise timephased data.
 *
 * @param <T> timephased data type
 */
public interface TimephasedNormaliser<T>
{
   /**
    * This method converts the internal representation of timephased
    * data used by MS Project into a standardised
    * format to make it easy to work with.
    *
    * @param calendar calendar context for normalisation
    * @param parent parent entity
    * @param list list of assignment data
    */
   void normalise(ProjectCalendar calendar, TimePeriodEntity parent, List<T> list);
}
