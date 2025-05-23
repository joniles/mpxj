/*
 * file:       TimePeriodEntity.java
 * author:     Jon Iles
 * date:       2024-01-27
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

import java.time.LocalDateTime;

/**
 * Classes implementing this interface represent a period of time
 * between a start LocalDateTime and a finish LocalDateTime.
 */
public interface TimePeriodEntity
{
   /**
    * Start time, represented as a LocalDateTime instance.
    *
    * @return start time
    */
   LocalDateTime getStart();

   /**
    * Finish time, represented as a LocalDateTime instance.
    *
    * @return finish time
    */
   LocalDateTime getFinish();
}
