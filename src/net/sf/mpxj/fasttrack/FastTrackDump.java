/*
 * file:       FastTrackDump.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2017
 * date:       01/02/2017
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

package net.sf.mpxj.fasttrack;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class FastTrackDump
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
         if (args.length != 2)
         {
            System.out.println("Usage: FasttrackDump <input FastTrack file name> <output text file name>");
         }
         else
         {
            System.out.println("Dump started.");
            long start = System.currentTimeMillis();
            FastTrackDump dump = new FastTrackDump();
            dump.process(args[0], args[1]);
            long elapsed = System.currentTimeMillis() - start;
            System.out.println("Dump completed in " + elapsed + "ms");
         }
      }

      catch (Exception ex)
      {
         System.out.println("Caught " + ex.toString());
      }
   }

   /**
    * This method opens the input and output files and kicks
    * off the processing.
    *
    * @param input Name of the input file
    * @param output Name of the output file
    * @throws Exception Thrown on file read errors
    */
   private void process(String input, String output) throws Exception
   {
      int blockIndex = 0;
      File file = new File(input);
      int length = (int) file.length();
      byte[] buffer = new byte[length];
      FileInputStream is = new FileInputStream(input);
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
            blocks.add(index);
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

      pw.write("\n\nChild Block Names\n");
      for (String name : m_childBlockNames)
      {
         pw.write(name + "\n");
      }

      is.close();
      pw.flush();
      pw.close();
   }

   private final void dumpBlock(int blockIndex, PrintWriter pw, int startIndex, int blockLength, byte[] buffer)
   {
      // Offset is not being stored in the block
      //pw.write("Offset: " + startIndex + " (" + Integer.toHexString(startIndex) + ")\n");
      pw.write("Block Index: " + blockIndex + "\n");
      pw.write("Length: " + blockLength + " (" + Integer.toHexString(blockLength) + ")\n");
      pw.write("\n");
      pw.write(hexdump(buffer, startIndex, blockLength, true, 16, ""));
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
            String name = new String(buffer, offset, nameLength, UTF16LE).toUpperCase();
            pw.println("Preamble Name: " + name);
            m_currentTable = new FastTrackTable(name);
            m_tables.put(name, m_currentTable);
            break;
         }
      }
   }

   private void dumpChildBlocks(PrintWriter pw, byte[] buffer, int startIndex, int blockLength)
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

   private void dumpChildBlock(PrintWriter pw, byte[] buffer, int blockStartIndex, int startIndex, int length)
   {
      pw.write("Child Block\n");

      byte value = buffer[startIndex];
      switch (value)
      {
         case (byte) 0x6E:
         case (byte) 0x6F:
         {
            dumpFixedDataBlock(blockStartIndex, pw, buffer, startIndex, 48, length);
            break;
         }

         case (byte) 0x73:
         case (byte) 0x6C:
         case (byte) 0x71:
         case (byte) 0x40:
         case (byte) 0x5C:
         case (byte) 0x6D:
         case (byte) 0x46:
         case (byte) 0x70:
         {
            dumpFixedDataBlock(blockStartIndex, pw, buffer, startIndex, 18, length);
            break;
         }

         case (byte) 0x4B:
         {
            dumpFixedDataBlock(blockStartIndex, pw, buffer, startIndex, 40, length);
            break;
         }

         case (byte) 0x49:
         {
            readAssignments(blockStartIndex, pw, buffer, startIndex, length);
            break;
         }

         case (byte) 0x59:
         {
            readEnums(blockStartIndex, pw, buffer, startIndex, length);
            break;
         }

         case (byte) 0x53:
         {
            readBooleans(blockStartIndex, pw, buffer, startIndex, length);
            break;
         }

         case (byte) 0x5b:
         case (byte) 0x4A:
         case (byte) 0x54:
         {
            readDoubles(blockStartIndex, pw, buffer, startIndex, length);
            break;
         }

         case (byte) 0x57:
         case (byte) 0x58:
         {
            readRelations(blockStartIndex, pw, buffer, startIndex, length);
            break;
         }

         case (byte) 0x68:
         case (byte) 0x69:
         {
            readStrings(pw, buffer, startIndex, length);
            break;
         }
      }
   }

   private int dumpFixedDataBlock(int blockStartIndex, PrintWriter pw, byte[] buffer, int startIndex, int unknownBlockSize, int length)
   {
      int offset = dumpBlockHeader("Fixed Data", blockStartIndex, pw, buffer, startIndex);

      pw.write(hexdump(buffer, startIndex + offset, unknownBlockSize, true, 16, ""));
      offset += unknownBlockSize;

      offset = dumpFixedSizeItems(pw, offset, buffer, startIndex);

      pw.write(hexdump(buffer, startIndex + offset, (length - offset), true, 16, ""));
      offset = length;

      pw.write("Total Block Size: " + offset + "(" + Integer.toHexString(offset) + ")\n\n");
      return offset;
   }

   private void readEnums(int blockStartIndex, PrintWriter pw, byte[] buffer, int startIndex, int length)
   {
      EnumBlock block = new EnumBlock();
      block.read(buffer, startIndex, length);
      pw.println(block.toString());
   }

   private void readAssignments(int blockStartIndex, PrintWriter pw, byte[] buffer, int startIndex, int length)
   {
      AssignmentBlock block = new AssignmentBlock();
      block.read(buffer, startIndex, length);
      pw.println(block.toString());
   }

   private void readStrings(PrintWriter pw, byte[] buffer, int startIndex, int length)
   {
      StringBlock block = new StringBlock();
      block.read(buffer, startIndex, length);
      pw.println(block.toString());
   }

   private void readBooleans(int blockStartIndex, PrintWriter pw, byte[] buffer, int startIndex, int length)
   {
      BooleanBlock block = new BooleanBlock();
      block.read(buffer, startIndex, length);
      pw.println(block.toString());
   }

   private void readDoubles(int blockStartIndex, PrintWriter pw, byte[] buffer, int startIndex, int length)
   {
      DoubleBlock block = new DoubleBlock();
      block.read(buffer, startIndex, length);
      pw.println(block.toString());
   }

   private void readRelations(int blockStartIndex, PrintWriter pw, byte[] buffer, int startIndex, int length)
   {
      RelationBlock block = new RelationBlock();
      block.read(buffer, startIndex, length);
      pw.println(block.toString());
   }

   private int dumpBlockHeader(String blockType, int blockStartIndex, PrintWriter pw, byte[] buffer, int startIndex)
   {
      int offset = 0;
      pw.write(blockType + " Block at " + (startIndex - blockStartIndex) + "\n");

      pw.write("Header: " + hexdump(buffer, startIndex + offset, 8, false, 16, ""));
      offset += 8;

      int nameLength = FastTrackUtility.getInt(buffer, startIndex + offset);
      offset += 4;
      pw.write("Name Length: " + nameLength + "\n");
      if (nameLength > 0 && nameLength < 255)
      {
         String name = new String(buffer, startIndex + offset, nameLength, UTF16LE);
         offset += nameLength;
         String fullName = m_currentTable.getName() + "." + name;
         pw.write("Name: " + fullName + "\n");
         m_childBlockNames.add(fullName);

         int indexNumber = FastTrackUtility.getInt(buffer, startIndex + offset);
         offset += 4;
         pw.write("Index Number: " + indexNumber + "\n");
      }

      return offset;
   }

   private int skipTo(PrintWriter pw, int offset, byte[] buffer, int startIndex, int value)
   {
      int nextOffset = offset;
      while (FastTrackUtility.getShort(buffer, startIndex + nextOffset) != value)
      {
         ++nextOffset;
      }
      nextOffset += 2;

      pw.write(hexdump(buffer, startIndex + offset, (nextOffset - offset), false, 16, ""));

      return nextOffset;
   }

   private int dumpStringsWithLengths(PrintWriter pw, int offset, byte[] buffer, int startIndex, boolean inclusive)
   {
      int numberOfItems = FastTrackUtility.getInt(buffer, startIndex + offset);
      offset += 4;
      pw.write("Number of items: " + numberOfItems + "\n");

      if (inclusive)
      {
         ++numberOfItems;
      }

      for (int index = 0; index < numberOfItems; index++)
      {
         pw.write("Item " + index + "\n");
         pw.write("  " + hexdump(buffer, startIndex + offset, 2, false, 16, ""));
         offset += 2;
         int itemNameLength = FastTrackUtility.getInt(buffer, startIndex + offset);
         offset += 4;
         pw.write("  Item Name Length: " + itemNameLength + "\n");
         String itemName = new String(buffer, startIndex + offset, itemNameLength, UTF16LE);
         offset += itemNameLength;
         pw.write("  Item Name: " + itemName + "\n");
      }
      return offset;
   }

   private int dumpFixedSizeItems(PrintWriter pw, int offset, byte[] buffer, int startIndex)
   {
      int offsetToData = FastTrackUtility.getShort(buffer, startIndex + offset);
      offset += 2;
      pw.write("Offset to Data: " + offsetToData + "\n");

      int numberOfItems = FastTrackUtility.getInt(buffer, startIndex + offset);
      offset += 4;
      pw.write("Number of items: " + numberOfItems + "\n");
      if (numberOfItems < 0 || numberOfItems > 1000000)
      {
         throw new UnexpectedStructureException();
      }

      int itemLength = FastTrackUtility.getShort(buffer, startIndex + offset);
      offset += 2;
      pw.write("Item length: " + itemLength + "\n");

      int offsetToEnd = FastTrackUtility.getInt(buffer, startIndex + offset);
      offset += 4;
      pw.write("Offset to End: " + offsetToEnd + "\n");

      for (int index = 0; index <= numberOfItems; index++)
      {
         pw.write("Item " + index + ": " + hexdump(buffer, startIndex + offset, itemLength, true, 16, ""));
         offset += itemLength;
      }
      return offset;
   }

   private final void dumpFD(PrintWriter pw, byte[] buffer, int startIndex)
   {
      pw.write("FD BLOCK\n");
      int offset = 0;
      pw.write(hexdump(buffer, startIndex + offset, 41, true, 16, ""));
      offset += 41;

      int nameLength = FastTrackUtility.getInt(buffer, startIndex + offset);
      offset += 4;
      System.out.println(nameLength);
      if (nameLength > 0)
      {
         String name = new String(buffer, startIndex + offset, nameLength, UTF16LE);
         offset += nameLength;
         pw.write("Block Name: " + name + "\n");
         int blockNumber = FastTrackUtility.getShort(buffer, startIndex + offset);
         pw.write("Block Number: " + blockNumber + "\n");
         offset += 2;
         pw.write(hexdump(buffer, startIndex + offset, 8, false, 16, ""));
         offset += 8;
         int itemCount = FastTrackUtility.getInt(buffer, startIndex + offset);
         offset += 4;
         pw.write("Item Count: " + itemCount + "\n");

         pw.write(hexdump(buffer, startIndex + offset, 2, false, 16, ""));
         offset += 2;

         int offsetToEnd = FastTrackUtility.getInt(buffer, startIndex + offset);
         offset += 4;
         pw.write("Offset to End: " + offsetToEnd + "\n");
         for (int index = 0; index < itemCount; index++)
         {
            pw.write("Item " + index + ":  " + hexdump(buffer, startIndex + offset, 4, false, 16, ""));
            offset += 4;
         }
         pw.write(hexdump(buffer, startIndex + offset, 4, false, 16, ""));
         offset += 4;
      }
   }

   private int dumpFcBlock(PrintWriter pw, byte[] buffer, int startIndex, int offset)
   {
      int itemCount = FastTrackUtility.getInt(buffer, startIndex + offset);
      offset += 4;
      pw.write("Item Count: " + itemCount + "\n");

      int itemSize = FastTrackUtility.getShort(buffer, startIndex + offset);
      offset += 2;
      pw.write("Item Size: " + itemSize + "\n");

      int dataSize = FastTrackUtility.getInt(buffer, startIndex + offset);
      offset += 4;
      pw.write("Data Size: " + dataSize + "\n");

      if (dataSize != 0)
      {
         pw.write("Data:\n");
         pw.write(hexdump(buffer, startIndex + offset, dataSize, true, 16, ""));
         offset += dataSize;
      }
      return offset;
   }

   private int dumpFbBlock(PrintWriter pw, byte[] buffer, int startIndex)
   {
      pw.write("BLOCK DUMP");

      int offset = 1;

      pw.write(hexdump(buffer, startIndex + offset, 10, true, 16, ""));
      offset += 10;

      int itemSize = FastTrackUtility.getShort(buffer, startIndex + offset);
      offset += 2;
      pw.write("Item Size: " + itemSize + "\n");

      int itemCount = FastTrackUtility.getInt(buffer, startIndex + offset);
      offset += 4;
      pw.write("Item Count: " + itemCount + "\n");

      for (int index = 0; index <= itemCount; index++)
      {
         pw.write("  Item " + index + ": " + hexdump(buffer, startIndex + offset, itemSize, true, 16, ""));
         offset += itemSize;
      }

      pw.write(hexdump(buffer, startIndex + offset, 10, true, 16, ""));
      offset += 10;

      itemSize = FastTrackUtility.getShort(buffer, startIndex + offset);
      offset += 2;
      pw.write("Item Size: " + itemSize + "\n");

      int dataSize = FastTrackUtility.getInt(buffer, startIndex + offset);
      offset += 4;
      pw.write("Data size: " + dataSize + "\n");

      itemCount = dataSize / itemSize;
      pw.write("Item Count: " + itemCount + "\n");

      for (int index = 0; index <= itemCount; index++)
      {
         pw.write("  Item " + index + ": " + hexdump(buffer, startIndex + offset, itemSize, true, 16, ""));
         offset += itemSize;
      }

      pw.write(hexdump(buffer, startIndex + offset, 4, true, 16, ""));
      offset += 4;

      return offset;
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

   public final String hexdump(byte[] buffer, int offset, int length, boolean ascii, int columns, String prefix)
   {
      StringBuilder sb = new StringBuilder();
      if (buffer != null)
      {
         int index = offset;
         DecimalFormat df = new DecimalFormat("00000");

         while (index < (offset + length))
         {
            if (index + columns > (offset + length))
            {
               columns = (offset + length) - index;
            }

            sb.append(prefix);
            sb.append(df.format(index - offset));
            sb.append(":");
            sb.append(hexdump(buffer, index, columns, ascii));
            sb.append('\n');

            index += columns;
         }
      }

      return (sb.toString());
   }

   public final String hexdump(byte[] buffer, int offset, int length, boolean ascii)
   {
      StringBuilder sb = new StringBuilder();

      if (buffer != null)
      {
         char c;
         int loop;
         int count = offset + length;

         for (loop = offset; loop < count; loop++)
         {
            sb.append(" ");
            sb.append(HEX_DIGITS[(buffer[loop] & 0xF0) >> 4]);
            sb.append(HEX_DIGITS[buffer[loop] & 0x0F]);
         }

         if (ascii == true)
         {
            sb.append("   ");

            for (loop = offset; loop < count; loop++)
            {
               c = (char) buffer[loop];

               if ((c > 200) || (c < 27))
               {
                  c = ' ';
               }

               sb.append(c);
            }
         }
      }

      return (sb.toString());
   }

   private final Map<String, FastTrackTable> m_tables = new HashMap<String, FastTrackTable>();
   private FastTrackTable m_currentTable;
   private final Set<String> m_childBlockNames = new TreeSet<String>();

   private static final char[] HEX_DIGITS =
   {
      '0',
      '1',
      '2',
      '3',
      '4',
      '5',
      '6',
      '7',
      '8',
      '9',
      'A',
      'B',
      'C',
      'D',
      'E',
      'F'
   };

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

   private static final Charset UTF16LE = Charset.forName("UTF-16LE");
}
