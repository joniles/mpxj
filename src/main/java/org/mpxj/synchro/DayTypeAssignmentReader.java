/*
 * file:       DayTypeAssignmentReader.java
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
 * Read a day type assignment table.
 */
class DayTypeAssignmentReader extends TableReader
{
   /**
    * Constructor.
    *
    * @param stream input stream
    */
   public DayTypeAssignmentReader(StreamReader stream)
   {
      super(stream);
   }

   @Override protected void readRow(StreamReader stream, Map<String, Object> map) throws IOException
   {
      int unknown2size = stream.getVersion().atLeast(Synchro.VERSION_6_2_0) ? 8 : 4;

      map.put("DATE", stream.readDate());
      map.put("UNKNOWN1", stream.readBytes(4));
      map.put("DAY_TYPE_UUID", stream.readUUID());
      map.put("UNKNOWN2", stream.readBytes(unknown2size));
   }

   @Override protected boolean hasUUID()
   {
      return false;
   }

   @Override protected int rowMagicNumber()
   {
      return 0xD1A3D6C;
   }
}
