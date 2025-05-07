/*
 * file:       DayTypeReader.java
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
 * Reads a day types table.
 */
class DayTypeReader extends TableReader
{
   /**
    * Constructor.
    *
    * @param stream input stream
    */
   public DayTypeReader(StreamReader stream)
   {
      super(stream);
   }

   @Override protected void readRow(StreamReader stream, Map<String, Object> map) throws IOException
   {
      map.put("UUID", stream.readUUID());
      map.put("NAME", stream.readString());
      map.put("COLOR", stream.readBytes(12));
      map.put("UNKNOWN2", stream.readBytes(4));
      map.put("TIME_RANGES", stream.readBlocks(TimeRangeBlockReader.class));
      map.put("UNKNOWN3", stream.readBytes(8));
   }

   @Override protected boolean hasUUID()
   {
      return false;
   }

   @Override protected int rowMagicNumber()
   {
      return 0xC4F4C21D;
   }
}
