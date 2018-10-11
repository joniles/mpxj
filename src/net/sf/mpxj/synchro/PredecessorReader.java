
package net.sf.mpxj.synchro;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import net.sf.mpxj.Duration;
import net.sf.mpxj.RelationType;

class PredecessorReader extends TableReader
{
   public PredecessorReader(InputStream stream)
   {
      super(stream);
   }

   @Override protected void readRow(StreamReader stream, Map<String, Object> map) throws IOException
   {
      map.put("PREDECESSOR_UUID", stream.readUUID());
      map.put("RELATION_TYPE", getRelationType(stream.readInt()));
      map.put("UNKNOWN1", stream.readBytes(4));
      Duration lag = stream.readDuration();
      map.put("UNKNOWN2", stream.readBytes(4));
      boolean lagIsNegative = stream.readInt() == 2;
      map.put("CALENDAR_UUID", stream.readUUID());
      map.put("UNKNOWN3", stream.readBytes(8));

      if (lagIsNegative)
      {
         lag = Duration.getInstance(-lag.getDuration(), lag.getUnits());
      }

      map.put("LAG", lag);
   }

   @Override protected int rowMagicNumber()
   {
      return 0x04E7E3D1;
   }

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
