/*
 * file:       TableA1TAB.java
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

/**
 * Read the contents of the A1TAB table.
 */
class TableA1TAB extends Table
{
   /**
    * {@inheritDoc}
    */
   @Override protected void readRow(int uniqueID, byte[] data)
   {
      Map<String, Object> map = new HashMap<String, Object>();
      map.put("UNIQUE_ID", Integer.valueOf(uniqueID));
      map.put("NAME", PEPUtility.getString(data, 1, 15));
      map.put("ORDER", Integer.valueOf(PEPUtility.getShort(data, 16)));
      map.put("PREVIOUS_TASK_ID", Integer.valueOf(PEPUtility.getShort(data, 18)));
      map.put("NEXT_TASK_ID", Integer.valueOf(PEPUtility.getShort(data, 26)));
      map.put("PLANNED_START", PEPUtility.getStartDate(data, 22));
      map.put("PLANNED_FINISH", PEPUtility.getFinishDate(data, 24));
      addRow(uniqueID, map);
   }
}
