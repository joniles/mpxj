/*
 * file:       TableCONTAB.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2018
 * date:       12/01/2018
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

package net.sf.mpxj.turboproject;

import java.util.HashMap;
import java.util.Map;

import net.sf.mpxj.Duration;
import net.sf.mpxj.RelationType;
import net.sf.mpxj.TimeUnit;

/**
 * Read the contents of the CONTAB table.
 */
class TableCONTAB extends Table
{
   /**
    * {@inheritDoc}
    */
   @Override protected void readRow(int uniqueID, byte[] data)
   {
      Map<String, Object> map = new HashMap<String, Object>();
      map.put("UNIQUE_ID", Integer.valueOf(uniqueID));
      map.put("DELETED", Boolean.valueOf(data[0] == (byte) 0xFF));
      map.put("TASK_ID_1", Integer.valueOf(PEPUtility.getShort(data, 1)));
      map.put("TASK_ID_2", Integer.valueOf(PEPUtility.getShort(data, 3)));
      map.put("TYPE", getRelationType(PEPUtility.getShort(data, 9)));
      map.put("LAG", Duration.getInstance(PEPUtility.getShort(data, 11), TimeUnit.DAYS));
      addRow(uniqueID, map);
   }

   private RelationType getRelationType(int value)
   {
      RelationType result = null;
      if (value >= 0 || value < RELATION_TYPES.length)
      {
         result = RELATION_TYPES[value];
      }
      if (result == null)
      {
         result = RelationType.FINISH_START;
      }
      return result;
   }

   private static final RelationType[] RELATION_TYPES =
   {
      null,
      RelationType.START_START,
      null,
      RelationType.START_FINISH,
      RelationType.FINISH_FINISH
   };
}
