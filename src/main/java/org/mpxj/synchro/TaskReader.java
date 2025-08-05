/*
 * file:       TaskReader.java
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
 * Reads a task table.
 */
class TaskReader extends TableReader
{
   /**
    * Constructor.
    *
    * @param stream input stream
    */
   public TaskReader(StreamReader stream)
   {
      super(stream);
   }

   @Override protected void readRow(StreamReader stream, Map<String, Object> map) throws IOException
   {
      int unknown5Size = stream.getVersion().before(Synchro.VERSION_6_3_0) ? 85 : 84;

      map.put("UNKNOWN1", stream.readByte());
      map.put("RESOURCE_ASSIGNMENTS", stream.readTable(ResourceAssignmentReader.class));
      map.put("UNKNOWN2", stream.readBytes(4));
      map.put("PREDECESSORS", stream.readTable(PredecessorReader.class));
      map.put("CALENDAR_UUID", stream.readUUID());
      map.put("NAME", stream.readString());
      map.put("PLANNED_START", stream.readDate());
      map.put("UNKNOWN3", stream.readBytes(4));
      map.put("PLANNED_DURATION", stream.readDuration());
      map.put("UNKNOWN4", stream.readBytes(4));
      map.put("TASKS", stream.readTableConditional(TaskReader.class));
      map.put("COSTS", stream.readTableConditional(CostReader.class));
      map.put("ACTUAL_START", stream.readDate());
      map.put("UNKNOWN_DATE1_EXTRA", stream.readBytes(4));
      map.put("PLANNED_FINISH", stream.readDate());
      map.put("UNKNOWN_DATE2_EXTRA", stream.readBytes(4));
      map.put("UNKNOWN_DATE3", stream.readDate());
      map.put("UNKNOWN_DATE3_EXTRA", stream.readBytes(4));
      map.put("ACTUAL_FINISH", stream.readDate());
      map.put("UNKNOWN_DATE4_EXTRA", stream.readBytes(4));
      // Note: contains an embedded table
      map.put("UNKNOWN5", stream.readUnknownTableConditional(unknown5Size, 0x72B5E632));
      map.put("UNKNOWN6", stream.readBytes(2));
      map.put("COMMENTARY", stream.readTableConditional(CommentaryReader.class));
      map.put("FILES", stream.readUnknownBlocks(20));
      map.put("UNKNOWN7", stream.readBytes(4));
      map.put("CONSTRAINT_TYPE", stream.readInteger());
      map.put("CONSTRAINT_EARLY_DATE", stream.readDate());
      map.put("CONSTRAINT_EARLY_DATE_EXTRA", stream.readBytes(4));
      map.put("CONSTRAINT_LATE_DATE", stream.readDate());
      map.put("CONSTRAINT_LATE_DATE_EXTRA", stream.readBytes(4));
      map.put("UNKNOWN8A", stream.readBytes(40));
      map.put("UNKNOWN8B", stream.readUnknownBlocks(20));
      map.put("UNKNOWN8C", stream.readBytes(26));
      map.put("URL", stream.readString());
      map.put("PROGRESS_TYPE", stream.readInteger());
      map.put("PERCENT_COMPLETE", stream.readDouble());
      map.put("UNKNOWN9", stream.readUnknownBlocks(20));
      map.put("ID", stream.readString());
      map.put("USER_FIELDS", stream.readTableConditional(UserFieldReader.class));
      map.put("REMAINING_DURATION", stream.readDuration());
      map.put("UNKNOWN10", stream.readBytes(20));
      map.put("STATUS", stream.readInteger());
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
      map.put("ESTIMATED_FINISH", stream.readDate());
      map.put("EXPECTED_FINISH_EXTRA", stream.readBytes(4));
      // NOTE: UNKNOWN12 contains a nested table at the end of the row - will need sample data for further work
      map.put("UNKNOWN12", stream.readUnknownTable(163, 0xD87556E6));
      map.put("UNKNOWN13", stream.readBytes(8));
   }

   @Override protected int rowMagicNumber()
   {
      return 0x04EC2576;
   }
}
