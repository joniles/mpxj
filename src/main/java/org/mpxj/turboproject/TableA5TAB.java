/*
 * file:       TableA5TAB.java
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

import org.mpxj.Duration;
import org.mpxj.TimeUnit;

/**
 * Read the contents of the A3
 * 5TAB table.
 */
class TableA5TAB extends Table
{
   @Override protected void readRow(int uniqueID, byte[] data)
   {
      Map<String, Object> map = new HashMap<>();
      map.put("UNIQUE_ID", Integer.valueOf(uniqueID));

      int originalDuration = PEPUtility.getShort(data, 22);
      int remainingDuration = PEPUtility.getShort(data, 24);
      int percentComplete = 0;

      if (originalDuration != 0)
      {
         percentComplete = ((originalDuration - remainingDuration) * 100) / originalDuration;
      }

      map.put("ORIGINAL_DURATION", Duration.getInstance(originalDuration, TimeUnit.DAYS));
      map.put("REMAINING_DURATION", Duration.getInstance(remainingDuration, TimeUnit.DAYS));
      map.put("PERCENT_COMPLETE", Integer.valueOf(percentComplete));
      map.put("TARGET_START", PEPUtility.getStartDate(data, 4));
      map.put("TARGET_FINISH", PEPUtility.getFinishDate(data, 6));
      map.put("ACTUAL_START", PEPUtility.getStartDate(data, 16));
      map.put("ACTUAL_FINISH", PEPUtility.getFinishDate(data, 18));

      addRow(uniqueID, map);
   }
}
