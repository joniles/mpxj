
package net.sf.mpxj.synchro;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

class TaskReader extends TableReader
{
   public TaskReader(InputStream stream)
   {
      super(stream);
   }

   @Override protected void readRow(StreamReader stream, Map<String, Object> map) throws IOException
   {
      map.put("UNKNOWN1", stream.readByte());
      map.put("RESOURCE_ASSIGNMENTS", stream.readTable(ResourceAssignmentReader.class));
      map.put("UNKNOWN2", stream.readBytes(4));
      map.put("PREDECESSORS", stream.readTable(PredecessorReader.class));
      map.put("CALENDAR_UUID", stream.readUUID());
      map.put("NAME", stream.readString());
      map.put("START", stream.readDate());
      map.put("UNKNOWN3", stream.readBytes(4));
      map.put("DURATION", stream.readDuration());
      map.put("UNKNOWN4", stream.readBytes(4));
      map.put("TASKS", stream.readTableConditional(TaskReader.class));
      map.put("COSTS", stream.readTableConditional(CostReader.class));
      map.put("UNKNOWN_DATE1", stream.readDate());
      map.put("UNKNOWN_DATE1_EXTRA", stream.readBytes(4));
      map.put("UNKNOWN_DATE2", stream.readDate());
      map.put("UNKNOWN_DATE2_EXTRA", stream.readBytes(4));
      map.put("UNKNOWN_DATE3", stream.readDate());
      map.put("UNKNOWN_DATE3_EXTRA", stream.readBytes(4));
      map.put("UNKNOWN_DATE4", stream.readDate());
      map.put("UNKNOWN_DATE4_EXTRA", stream.readBytes(4));
      map.put("UNKNOWN5", stream.readBytes(3));
      map.put("COMMENTARY", stream.readTableConditional(CommentaryReader.class));
      map.put("UNKNOWN6", stream.readBytes(4));
      map.put("FILES", stream.readUnknownBlocks(20));
      map.put("UNKNOWN7", stream.readBytes(8));
      map.put("CONSTRAINT_TYPE", stream.readInteger());
      map.put("CONSTRAINT_EARLY_DATE", stream.readDate());
      map.put("CONSTRAINT_EARLY_DATE_EXTRA", stream.readBytes(4));
      map.put("CONSTRAINT_LATE_DATE", stream.readDate());
      map.put("CONSTRAINT_LATE_DATE_EXTRA", stream.readBytes(4));
      map.put("UNKNOWN8", stream.readBytes(78));
      map.put("URL", stream.readString());
      map.put("PROGRESS_TYPE", stream.readInteger());
      map.put("PERCENT_COMPLETE", stream.readDouble());
      map.put("UNKNOWN9", stream.readBytes(12));
      map.put("ID", stream.readString());
      map.put("USER_FIELDS", stream.readTableConditional(UserFieldReader.class));
      map.put("UNKNOWN10", stream.readBytes(28));
      map.put("UNKNOWN_DATE7", stream.readDate());
      map.put("UNKNOWN_DATE7_EXTRA", stream.readBytes(4));
      map.put("UNKNOWN_DATE8", stream.readDate());
      map.put("UNKNOWN_DATE8_EXTRA", stream.readBytes(4));
      map.put("UNKNOWN_DATE9", stream.readDate());
      map.put("UNKNOWN_DATE9_EXTRA", stream.readBytes(4));
      map.put("UNKNOWN_DATE10", stream.readDate());
      map.put("UNKNOWN_DATE10_EXTRA", stream.readBytes(4));
      map.put("UNKNOWN11", stream.readBytes(4));
      map.put("PHYSICAL_QUANTITY", stream.readDouble());
      map.put("REMAINING_PHYSICAL_QUANTITY", stream.readDouble());
      map.put("PHYSICAL_QUANTITY_UNIT", stream.readInteger());
      map.put("ACTUAL_PHYSICAL_QUANTITY", stream.readDouble());
      map.put("EXPECTED_FINISH", stream.readDate());
      map.put("EXPECTED_FINISH_EXTRA", stream.readBytes(4));
      map.put("UNKNOWN12", stream.readTable(UnknownTableReader.class));
      map.put("UNKNOWN13", stream.readBytes(8));
   }

   @Override protected int rowMagicNumber()
   {
      return 0x04EC2576;
   }
}
