
package net.sf.mpxj.primavera.p3;

import net.sf.mpxj.RelationType;

public class RelationTypeColumn extends AbstractColumn
{
   public RelationTypeColumn(String name, int offset)
   {
      super(name, offset);
   }

   @Override public RelationType read(int offset, byte[] data)
   {
      int result = 0;
      int i = offset + m_offset;
      for (int shiftBy = 0; shiftBy < 16; shiftBy += 8)
      {
         result |= ((data[i] & 0xff)) << shiftBy;
         ++i;
      }

      RelationType type = null;
      if (result >= 0 || result < TYPES.length)
      {
         type = TYPES[result];
      }
      if (type == null)
      {
         type = RelationType.START_FINISH;
      }

      return type;
   }

   private static final RelationType[] TYPES = new RelationType[]
   {
      null,
      RelationType.START_START,
      RelationType.FINISH_START,
      RelationType.FINISH_FINISH
   };
}
