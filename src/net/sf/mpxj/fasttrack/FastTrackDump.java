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
         {
            readDates(blockStartIndex, pw, buffer, startIndex, length);
            break;
         }

         case (byte) 0x6F:
         {
            readTimes(blockStartIndex, pw, buffer, startIndex, length);
            break;
         }

         case (byte) 0x71:
         {
            readDurations(blockStartIndex, pw, buffer, startIndex, length);
            break;
         }

         case (byte) 0x46:
         {
            readPercents(blockStartIndex, pw, buffer, startIndex, length);
            break;
         }

         case (byte) 0x6C:
         case (byte) 0x73:
         {
            readShorts(blockStartIndex, pw, buffer, startIndex, length);
            break;
         }

         case (byte) 0x6D:
         {
            readIdentifiers(blockStartIndex, pw, buffer, startIndex, length);
            break;
         }

         case (byte) 0x70:
         {
            readNumbers(blockStartIndex, pw, buffer, startIndex, length);
            break;
         }

         case (byte) 0x5C:
         {
            readCalendars(blockStartIndex, pw, buffer, startIndex, length);
            break;
         }

         case (byte) 0x4B:
         {
            readIntegers(blockStartIndex, pw, buffer, startIndex, length);
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

   private void readDates(int blockStartIndex, PrintWriter pw, byte[] buffer, int startIndex, int length)
   {
      DateBlock block = new DateBlock();
      block.read(buffer, startIndex, length);
      pw.println(block.toString());
   }

   private void readTimes(int blockStartIndex, PrintWriter pw, byte[] buffer, int startIndex, int length)
   {
      TimeBlock block = new TimeBlock();
      block.read(buffer, startIndex, length);
      pw.println(block.toString());
   }

   private void readDurations(int blockStartIndex, PrintWriter pw, byte[] buffer, int startIndex, int length)
   {
      DurationBlock block = new DurationBlock();
      block.read(buffer, startIndex, length);
      pw.println(block.toString());
   }

   private void readPercents(int blockStartIndex, PrintWriter pw, byte[] buffer, int startIndex, int length)
   {
      PercentBlock block = new PercentBlock();
      block.read(buffer, startIndex, length);
      pw.println(block.toString());
   }

   private void readIntegers(int blockStartIndex, PrintWriter pw, byte[] buffer, int startIndex, int length)
   {
      IntegerBlock block = new IntegerBlock();
      block.read(buffer, startIndex, length);
      pw.println(block.toString());
   }

   private void readIdentifiers(int blockStartIndex, PrintWriter pw, byte[] buffer, int startIndex, int length)
   {
      IdentifierBlock block = new IdentifierBlock();
      block.read(buffer, startIndex, length);
      pw.println(block.toString());
   }

   private void readShorts(int blockStartIndex, PrintWriter pw, byte[] buffer, int startIndex, int length)
   {
      ShortBlock block = new ShortBlock();
      block.read(buffer, startIndex, length);
      pw.println(block.toString());
   }

   private void readEnums(int blockStartIndex, PrintWriter pw, byte[] buffer, int startIndex, int length)
   {
      EnumBlock block = new EnumBlock();
      block.read(buffer, startIndex, length);
      pw.println(block.toString());
   }

   private void readCalendars(int blockStartIndex, PrintWriter pw, byte[] buffer, int startIndex, int length)
   {
      CalendarBlock block = new CalendarBlock();
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

   private void readNumbers(int blockStartIndex, PrintWriter pw, byte[] buffer, int startIndex, int length)
   {
      NumberBlock block = new NumberBlock();
      block.read(buffer, startIndex, length);
      pw.println(block.toString());
   }

   private void readRelations(int blockStartIndex, PrintWriter pw, byte[] buffer, int startIndex, int length)
   {
      RelationBlock block = new RelationBlock();
      block.read(buffer, startIndex, length);
      pw.println(block.toString());
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
