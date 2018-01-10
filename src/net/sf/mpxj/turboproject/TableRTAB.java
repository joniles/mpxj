
package net.sf.mpxj.turboproject;

import java.util.HashMap;
import java.util.Map;

class TableRTAB extends Table
{

   @Override protected void readRow(int uniqueID, byte[] data)
   {
      if ((data[0] != (byte) 0xFF))
      {
         Map<String, Object> map = new HashMap<String, Object>();
         map.put("ID", Integer.valueOf(m_id++));
         map.put("UNIQUE_ID", Integer.valueOf(uniqueID));
         map.put("NAME", PEPUtility.getString(data, 1, 20));
         map.put("GROUP", PEPUtility.getString(data, 21, 12));
         map.put("UNIT", PEPUtility.getString(data, 33, 4));
         map.put("RATE", Integer.valueOf(PEPUtility.getInt(data, 39)));
         map.put("DESCRIPTION", PEPUtility.getString(data, 43, 30));
         map.put("CALENDAR_ID", Integer.valueOf(PEPUtility.getShort(data, 95)));
         map.put("POOL", Integer.valueOf(PEPUtility.getInt(data, 97)));
         map.put("PER_DAY", Integer.valueOf(PEPUtility.getInt(data, 101)));
         map.put("EXPENSES_ONLY", Boolean.valueOf((data[106] & 0x01) != 0));
         map.put("MODIFY_ON_INTEGRATE", Boolean.valueOf((data[106] & 0x02) != 0));
         map.put("PRIORITY", Integer.valueOf(PEPUtility.getShort(data, 127)));
         map.put("PERIOD_DUE", Integer.valueOf(PEPUtility.getShort(data, 129)));
         map.put("PARENT_ID", Integer.valueOf(PEPUtility.getShort(data, 133)));
         addRow(uniqueID, map);
      }
   }

   private int m_id = 1;
}
