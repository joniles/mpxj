/*
 * file:       TableNCALTAB.java
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
 * Read the contents of the NCALTAB table.
 */
class TableNCALTAB extends Table
{
   @Override protected void readRow(int uniqueID, byte[] data)
   {
      if (data[0] != (byte) 0xFF)
      {
         Map<String, Object> map = new HashMap<>();
         map.put("UNIQUE_ID", Integer.valueOf(uniqueID));

         map.put("NAME", PEPUtility.getString(data, 1, 8));
         map.put("START", PEPUtility.getStartDate(data, 9));
         map.put("BASE_CALENDAR_ID", Integer.valueOf(PEPUtility.getShort(data, 11)));
         map.put("FIRST_CALENDAR_EXCEPTION_ID", Integer.valueOf(PEPUtility.getShort(data, 13)));
         map.put("SUNDAY", Boolean.valueOf((data[17] & 0x40) == 0));
         map.put("MONDAY", Boolean.valueOf((data[17] & 0x02) == 0));
         map.put("TUESDAY", Boolean.valueOf((data[17] & 0x04) == 0));
         map.put("WEDNESDAY", Boolean.valueOf((data[17] & 0x08) == 0));
         map.put("THURSDAY", Boolean.valueOf((data[17] & 0x10) == 0));
         map.put("FRIDAY", Boolean.valueOf((data[17] & 0x20) == 0));
         map.put("SATURDAY", Boolean.valueOf((data[17] & 0x01) == 0));

         addRow(uniqueID, map);
      }
   }
}
