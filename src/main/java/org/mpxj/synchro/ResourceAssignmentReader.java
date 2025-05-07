/*
 * file:       ResourceAssignmentReader.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2018
 * date:       2018-10-11
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

package org.mpxj.synchro;

import java.io.IOException;
import java.util.Map;

/**
 * Reads a resource assignment table.
 */
class ResourceAssignmentReader extends TableReader
{
   /**
    * Constructor.
    *
    * @param stream input stream
    */
   public ResourceAssignmentReader(StreamReader stream)
   {
      super(stream);
   }

   @Override protected void readRow(StreamReader stream, Map<String, Object> map) throws IOException
   {
      int unknown1BlockSize = stream.getVersion().atLeast(Synchro.VERSION_6_3_0) ? 69 : 57;

      map.put("UNKNOWN1", stream.readBytes(unknown1BlockSize));
      map.put("UNKNOWN2", stream.readDouble());
      map.put("UNKNOWN3", stream.readBytes(10));
      map.put("UNKNOWN4", stream.readUUID());
      map.put("RESOURCE_UUID", stream.readUUID());
      map.put("UNKNOWN5", stream.readBytes(16));
      map.put("PLANNED_UNITS", stream.readDouble());
      map.put("PLANNED_UNITS_TIME", stream.readDouble());
      map.put("ACTUAL_UNITS", stream.readDouble());
      map.put("ACTUAL_UNITS_TIME", stream.readDouble());
      map.put("UNKNOWN6", stream.readDouble());
      map.put("DRIVING", stream.readBoolean());
      map.put("UNKNOWN7", stream.readByte());

      boolean fixedUnits = stream.readBoolean().booleanValue();
      if (stream.getVersion().before(Synchro.VERSION_6_3_0))
      {
         fixedUnits = !fixedUnits;
      }
      map.put("FIXED_UNITS", Boolean.valueOf(fixedUnits));

      skipToRowEnd(0);
   }

   @Override protected int rowMagicNumber()
   {
      return 0x4623D899;
   }
}
