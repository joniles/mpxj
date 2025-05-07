/*
 * file:       RelationTypeColumn.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2018
 * date:       01/03/2018
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

package org.mpxj.primavera.p3;

import org.mpxj.RelationType;
import org.mpxj.primavera.common.AbstractShortColumn;

/**
 * Extract column data from a table.
 */
class RelationTypeColumn extends AbstractShortColumn
{
   /**
    * Constructor.
    *
    * @param name column name
    * @param offset offset within data
    */
   public RelationTypeColumn(String name, int offset)
   {
      super(name, offset);
   }

   @Override public RelationType read(int offset, byte[] data)
   {
      int result = readShort(offset, data);
      RelationType type = null;
      if (result >= 0 && result < TYPES.length)
      {
         type = TYPES[result];
      }
      if (type == null)
      {
         type = RelationType.START_FINISH;
      }

      return type;
   }

   private static final RelationType[] TYPES = new RelationType[]
   {
      null,
      RelationType.START_START,
      RelationType.FINISH_START,
      RelationType.FINISH_FINISH
   };
}
