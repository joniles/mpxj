/*
 * file:       ResourceCostContainerReader.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2021
 * date:       2021-04-03
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
 * Reads a resource cost container table.
 */
class ResourceCostContainerReader extends TableReader
{
   /**
    * Constructor.
    *
    * @param stream input stream
    */
   public ResourceCostContainerReader(StreamReader stream)
   {
      super(stream);
   }

   @Override protected void readRow(StreamReader stream, Map<String, Object> map) throws IOException
   {
      int unknown1BlockSize = stream.getVersion().atLeast(Synchro.VERSION_6_0_0) ? 36 : 44;
      map.put("UNKNOWN1", stream.readBytes(unknown1BlockSize));
      map.put("COST_TABLE", stream.readTable(ResourceCostTableReader.class));
      map.put("UNKNOWN2", stream.readBytes(8));
   }

   @Override protected boolean hasUUID()
   {
      return false;
   }

   @Override protected int rowMagicNumber()
   {
      return 0x701BAFBD;
   }
}
