
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
   /**
    * Main method.
    *
    * @param args array of command line arguments
    */
   public static void main(String[] args)
   {
      try
      {
         if (args.length != 1)
         {
            System.out.println("Usage: FasttrackDump <input FastTrack file name>");
         }
         else
         {
            System.out.println("Dump started.");
            long start = System.currentTimeMillis();
            FastTrackData dump = new FastTrackData();
            dump.process(new File(args[0]));
            long elapsed = System.currentTimeMillis() - start;
            System.out.println("Dump completed in " + elapsed + "ms");
         }
      }

      catch (Exception ex)
      {
         System.out.println("Caught " + ex.toString());
      }
   }

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
            System.out.println("Block start: " + index);
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

      pw.println("TABLES");
      for (String tableName : m_tables.keySet())
      {
         pw.println(tableName);
      }

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
         dumpPreambleBlock(pw, buffer, startIndex, blockLength);
      }
      else
      {
         dumpChildBlocks(pw, buffer, startIndex, blockLength);
      }

   }

   private void dumpPreambleBlock(PrintWriter pw, byte[] buffer, int startIndex, int blockLength)
   {
      pw.println("PREAMBLE");

      for (int index = startIndex; index < (startIndex + blockLength - 11); index++)
      {
         if (matchPattern(PREAMBLE_BLOCK_PATTERNS, buffer, index))
         {
            int offset = index + 7;
            int nameLength = FastTrackUtility.getInt(buffer, offset);
            pw.write("Preamble Name Length: " + nameLength + "\n");
            offset += 4;
            String name = new String(buffer, offset, nameLength, CharsetHelper.UTF16LE).toUpperCase();
            pw.println("Preamble Name: " + name);
            m_currentTable = new FastTrackTable(name);
            m_tables.put(name, m_currentTable);
            break;
         }
      }
   }

   private void dumpChildBlocks(PrintWriter pw, byte[] buffer, int startIndex, int blockLength) throws Exception
   {
      int endIndex = startIndex + blockLength;
      List<Integer> blocks = new ArrayList<Integer>();
      for (int index = startIndex; index < endIndex - 11; index++)
      {
         if (matchPattern(CHILD_BLOCK_PATTERNS, buffer, index))
         {
            int childBlockStart = index - 2;
            System.out.println("  Child Block start: " + childBlockStart);
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
               pw.println("ABORTED CHILD BLOCK - unexpected structure");
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
         klass = UnknownBlock.class;
      }

      FastTrackBlock block = (FastTrackBlock) klass.newInstance();
      block.read(buffer, startIndex, length);
      pw.println(block.toString());
      m_currentTable.addColumn(block);
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

   private final Map<String, FastTrackTable> m_tables = new HashMap<String, FastTrackTable>();
   private FastTrackTable m_currentTable;

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

   private static final byte[][] PREAMBLE_BLOCK_PATTERNS =
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
      COLUMN_MAP[0x6E] = DateBlock.class;
      COLUMN_MAP[0x6F] = TimeBlock.class;
      COLUMN_MAP[0x71] = DurationBlock.class;
      COLUMN_MAP[0x46] = PercentBlock.class;
      COLUMN_MAP[0x6C] = ShortBlock.class;
      COLUMN_MAP[0x73] = ShortBlock.class;
      COLUMN_MAP[0x6D] = IdentifierBlock.class;
      COLUMN_MAP[0x70] = NumberBlock.class;
      COLUMN_MAP[0x5C] = CalendarBlock.class;
      COLUMN_MAP[0x4B] = IntegerBlock.class;
      COLUMN_MAP[0x49] = AssignmentBlock.class;
      COLUMN_MAP[0x59] = EnumBlock.class;
      COLUMN_MAP[0x53] = BooleanBlock.class;
      COLUMN_MAP[0x5b] = DoubleBlock.class;
      COLUMN_MAP[0x4A] = DoubleBlock.class;
      COLUMN_MAP[0x54] = DoubleBlock.class;
      COLUMN_MAP[0x57] = RelationBlock.class;
      COLUMN_MAP[0x58] = RelationBlock.class;
      COLUMN_MAP[0x68] = StringBlock.class;
      COLUMN_MAP[0x69] = StringBlock.class;
   }
}
