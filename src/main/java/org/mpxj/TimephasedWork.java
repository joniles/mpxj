/*
 * file:       TimephasedWork.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2011
 * date:       05/12/2011
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
 * Represents timephased work.
 */
public final class TimephasedWork extends TimephasedItem<Duration>
{
   /**
    * Default constructor.
    */
   public TimephasedWork()
   {
      super();
   }

   /**
    * Copy constructor.
    *
    * @param sourceItem item to copy
    */
   public TimephasedWork(TimephasedWork sourceItem)
   {
      setStart(sourceItem.getStart());
      setFinish(sourceItem.getFinish());
      setModified(sourceItem.getModified());
      setTotalAmount(sourceItem.getTotalAmount());
      setAmountPerHour(sourceItem.getAmountPerHour());
   }

   /**
    * Copy constructor, scales by  the factor provided.
    *
    * @param sourceItem item to copy
    * @param factor scaling factor
    */
   public TimephasedWork(TimephasedWork sourceItem, double factor)
   {
      setStart(sourceItem.getStart());
      setFinish(sourceItem.getFinish());
      setModified(sourceItem.getModified());
      setTotalAmount(Duration.getInstance(sourceItem.getTotalAmount().getDuration() * factor, sourceItem.getTotalAmount().getUnits()));
      setAmountPerHour(Duration.getInstance(sourceItem.getAmountPerHour().getDuration() * factor, sourceItem.getAmountPerHour().getUnits()));
   }
}
