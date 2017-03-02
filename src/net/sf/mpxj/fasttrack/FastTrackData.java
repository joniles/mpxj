
package net.sf.mpxj.fasttrack;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.mpxj.common.CharsetHelper;

public class FastTrackData
{
   public void process(File file) throws Exception
   {
      String output = "c:/temp/project1.txt";
      int blockIndex = 0;
      int length = (int) file.length();
      byte[] buffer = new byte[length];
      FileInputStream is = new FileInputStream(file);
      PrintWriter pw = new PrintWriter(new FileWriter(output));
      int bytesRead = is.read(buffer);
      if (bytesRead != length)
      {
         throw new RuntimeException("Read count different");
      }

      List<Integer> blocks = new ArrayList<Integer>();
      for (int index = 64; index < buffer.length - 11; index++)
      {
         if (matchPattern(PARENT_BLOCK_PATTERNS, buffer, index))
         {
            blocks.add(Integer.valueOf(index));
         }
      }

      int startIndex = 0;
      for (int endIndex : blocks)
      {
         int blockLength = endIndex - startIndex;
         dumpBlock(blockIndex, pw, startIndex, blockLength, buffer);
         startIndex = endIndex;
         ++blockIndex;
      }

      int blockLength = buffer.length - startIndex;
      dumpBlock(blockIndex, pw, startIndex, blockLength, buffer);

      System.out.println("Duration units: " + m_durationTimeUnitValue);
      System.out.println("Work units: " + m_workTimeUnitValue);

      is.close();
      pw.flush();
      pw.close();
   }

   public FastTrackTable getTable(String name)
   {
      return m_tables.get(name);
   }

   private final void dumpBlock(int blockIndex, PrintWriter pw, int startIndex, int blockLength, byte[] buffer) throws Exception
   {
      pw.write("Block Index: " + blockIndex + "\n");
      pw.write("Length: " + blockLength + " (" + Integer.toHexString(blockLength) + ")\n");
      pw.write("\n");
      pw.write(FastTrackUtility.hexdump(buffer, startIndex, blockLength, true, 16, ""));
      pw.write("\n\n");

      if (blockLength < 128)
      {
         readTableBlock(pw, buffer, startIndex, blockLength);
      }
      else
      {
         readColumns(pw, buffer, startIndex, blockLength);
      }
   }

   private void readTableBlock(PrintWriter pw, byte[] buffer, int startIndex, int blockLength)
   {
      for (int index = startIndex; index < (startIndex + blockLength - 11); index++)
      {
         if (matchPattern(TABLE_BLOCK_PATTERNS, buffer, index))
         {
            int offset = index + 7;
            int nameLength = FastTrackUtility.getInt(buffer, offset);
            offset += 4;
            String name = new String(buffer, offset, nameLength, CharsetHelper.UTF16LE).toUpperCase();
            m_currentTable = new FastTrackTable(name);
            m_tables.put(name, m_currentTable);
            break;
         }
      }
   }

   private void readColumns(PrintWriter pw, byte[] buffer, int startIndex, int blockLength) throws Exception
   {
      int endIndex = startIndex + blockLength;
      List<Integer> blocks = new ArrayList<Integer>();
      for (int index = startIndex; index < endIndex - 11; index++)
      {
         if (matchPattern(CHILD_BLOCK_PATTERNS, buffer, index))
         {
            int childBlockStart = index - 2;
            blocks.add(Integer.valueOf(childBlockStart));
         }
      }
      blocks.add(Integer.valueOf(endIndex));

      int childBlockStart = -1;
      for (int childBlockEnd : blocks)
      {
         if (childBlockStart != -1)
         {
            int childblockLength = childBlockEnd - childBlockStart;
            pw.flush();

            try
            {
               dumpChildBlock(pw, buffer, startIndex, childBlockStart, childblockLength);
            }
            catch (UnexpectedStructureException ex)
            {
               pw.println("ABORTED COLUMN - unexpected structure");
            }
         }
         childBlockStart = childBlockEnd;
      }
   }

   private void dumpChildBlock(PrintWriter pw, byte[] buffer, int blockStartIndex, int startIndex, int length) throws Exception
   {
      int value = FastTrackUtility.getByte(buffer, startIndex);
      Class<?> klass = COLUMN_MAP[value];
      if (klass == null)
      {
         klass = UnknownColumn.class;
      }

      FastTrackColumn block = (FastTrackColumn) klass.newInstance();
      block.read(buffer, startIndex, length);
      pw.println(block.toString());
      m_currentTable.addColumn(block);

      updateDurationUnits(block);
      updateWorkUnits(block);
   }

   private final boolean matchPattern(byte[][] patterns, byte[] buffer, int bufferIndex)
   {
      boolean match = false;
      for (byte[] pattern : patterns)
      {
         int index = 0;
         match = true;
         for (byte b : pattern)
         {
            if (b != buffer[bufferIndex + index])
            {
               match = false;
               break;
            }
            ++index;
         }
         if (match)
         {
            break;
         }
      }
      return match;
   }

   private void updateDurationUnits(FastTrackColumn column)
   {
      if (m_durationTimeUnitValue == 1 && isDurationColumn(column))
      {
         int value = ((DurationColumn) column).getTimeUnitValue();
         if (value != 1)
         {
            m_durationTimeUnitValue = value;
         }
      }
   }

   private void updateWorkUnits(FastTrackColumn column)
   {
      if (m_workTimeUnitValue == 1 && isWorkColumn(column))
      {
         int value = ((DurationColumn) column).getTimeUnitValue();
         if (value != 1)
         {
            m_workTimeUnitValue = ((DurationColumn) column).getTimeUnitValue();
         }
      }
   }

   private boolean isDurationColumn(FastTrackColumn column)
   {
      return column instanceof DurationColumn && column.getName().indexOf("Duration") != -1;
   }

   private boolean isWorkColumn(FastTrackColumn column)
   {
      return column instanceof DurationColumn && column.getName().indexOf("Work") != -1;
   }

   private final Map<String, FastTrackTable> m_tables = new HashMap<String, FastTrackTable>();
   private FastTrackTable m_currentTable;
   private int m_durationTimeUnitValue = 1;
   private int m_workTimeUnitValue = 1;

   private static final byte[][] PARENT_BLOCK_PATTERNS =
   {
      {
         (byte) 0xFB,
         0x01,
         0x02,
         0x00,
         0x02,
         0x00,
         (byte) 0xFF,
         (byte) 0xFF,
         0x00,
         0x00,
         0x00
      },
      {
         (byte) 0xFC,
         0x01,
         0x02,
         0x00,
         0x02,
         0x00,
         (byte) 0xFF,
         (byte) 0xFF,
         0x00,
         0x00,
         0x00
      },
      {
         (byte) 0xFD,
         0x01,
         0x02,
         0x00,
         0x02,
         0x00,
         (byte) 0xFF,
         (byte) 0xFF,
         0x00,
         0x00,
         0x00
      }

   };

   private static final byte[][] CHILD_BLOCK_PATTERNS =
   {
      {
         0x05,
         0x00,
         0x00,
         0x00,
         0x01,
         0x00
      }
   };

   private static final byte[][] TABLE_BLOCK_PATTERNS =
   {
      {
         0x00,
         0x00,
         0x00,
         0x65,
         0x00,
         0x01,
         0x00
      }
   };

   private static final Class<?>[] COLUMN_MAP = new Class<?>[256];
   static
   {
      COLUMN_MAP[0x6E] = DateColumn.class;
      COLUMN_MAP[0x6F] = TimeColumn.class;
      COLUMN_MAP[0x71] = DurationColumn.class;
      COLUMN_MAP[0x46] = PercentColumn.class;
      COLUMN_MAP[0x6C] = ShortColumn.class;
      COLUMN_MAP[0x73] = ShortColumn.class;
      COLUMN_MAP[0x6D] = IdentifierColumn.class;
      COLUMN_MAP[0x70] = NumberColumn.class;
      COLUMN_MAP[0x5C] = CalendarColumn.class;
      COLUMN_MAP[0x4B] = IntegerColumn.class;
      COLUMN_MAP[0x49] = AssignmentColumn.class;
      COLUMN_MAP[0x59] = EnumColumn.class;
      COLUMN_MAP[0x53] = BooleanColumn.class;
      COLUMN_MAP[0x5b] = DoubleColumn.class;
      COLUMN_MAP[0x4A] = DoubleColumn.class;
      COLUMN_MAP[0x54] = DoubleColumn.class;
      COLUMN_MAP[0x57] = RelationColumn.class;
      COLUMN_MAP[0x58] = RelationColumn.class;
      COLUMN_MAP[0x68] = StringColumn.class;
      COLUMN_MAP[0x69] = StringColumn.class;
   }
}
