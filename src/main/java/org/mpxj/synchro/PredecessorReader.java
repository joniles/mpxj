/*
 * file:       PredecessorReader.java
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

import org.mpxj.Duration;
import org.mpxj.RelationType;

/**
 * Reads a predecessor table.
 */
class PredecessorReader extends TableReader
{
   /**
    * Constructor.
    *
    * @param stream input stream
    */
   public PredecessorReader(StreamReader stream)
   {
      super(stream);
   }

   @Override protected void readRow(StreamReader stream, Map<String, Object> map) throws IOException
   {
      map.put("PREDECESSOR_UUID", stream.readUUID());
      map.put("RELATION_TYPE", getRelationType(stream.readInt()));

      if (stream.getVersion().atLeast(Synchro.VERSION_6_2_0))
      {
         map.put("LAG", stream.readDurationFromLong());
         map.put("CALENDAR_UUID", stream.readUUID());
         map.put("UNKNOWN1", stream.readBytes(8));

      }
      else
      {
         map.put("UNKNOWN1", stream.readBytes(4));
         map.put("LAG", stream.readDuration());
         map.put("UNKNOWN2", stream.readBytes(4));

         boolean lagIsNegative;
         if (stream.getVersion().before(Synchro.VERSION_6_1_0))
         {
            lagIsNegative = stream.readInt() == 2;
            map.put("LAG_IS_NEGATIVE", Boolean.valueOf(lagIsNegative));
            map.put("CALENDAR_UUID", stream.readUUID());
            map.put("UNKNOWN3", stream.readBytes(8));
         }
         else
         {
            map.put("CALENDAR_UUID", stream.readUUID());
            lagIsNegative = stream.readInt() == 2;
            map.put("LAG_IS_NEGATIVE", Boolean.valueOf(lagIsNegative));
         }

         if (lagIsNegative)
         {
            map.put("LAG", ((Duration) map.get("LAG")).negate());
         }
      }
   }

   @Override protected int rowMagicNumber()
   {
      return 0x04E7E3D1;
   }

   /**
    * Convert an integer to a RelationType instance.
    *
    * @param type integer value
    * @return RelationType instance
    */
   private RelationType getRelationType(int type)
   {
      RelationType result;
      if (type > 0 && type < RELATION_TYPES.length)
      {
         result = RELATION_TYPES[type];
      }
      else
      {
         result = RelationType.FINISH_START;
      }
      return result;
   }

   private static final RelationType[] RELATION_TYPES =
   {
      null,
      RelationType.FINISH_START,
      RelationType.START_FINISH,
      RelationType.START_START,
      RelationType.FINISH_FINISH
   };
}
