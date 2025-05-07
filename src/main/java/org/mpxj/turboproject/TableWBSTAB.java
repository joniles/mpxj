/*
 * file:       TableWBSTAB.java
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

package org.mpxj.turboproject;

import java.util.HashMap;
import java.util.Map;

/**
 * Read the contents of the WBSTAB table.
 */
class TableWBSTAB extends Table
{
   @Override protected void readRow(int uniqueID, byte[] data)
   {
      if ((data[0] != (byte) 0xFF))
      {
         Map<String, Object> map = new HashMap<>();
         map.put("UNIQUE_ID", Integer.valueOf(uniqueID));
         map.put("CHILD_ID", Integer.valueOf(PEPUtility.getShort(data, 7)));
         map.put("NEXT_ID", Integer.valueOf(PEPUtility.getShort(data, 5)));
         map.put("TASK_ID", Integer.valueOf(PEPUtility.getShort(data, 9)));
         map.put("FIRST_CHILD_TASK_ID", Integer.valueOf(PEPUtility.getShort(data, 39)));
         addRow(uniqueID, map);
      }
   }
}
