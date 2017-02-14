/*
 * file:       MppDump.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2002-2003
 * date:       07/02/2003
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

package net.sf.mpxj.sample;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * This is a trivial class used to dump the contents of an MPP file
 * broken down into its internal file and directory structure, with the
 * content of each of the files written out as hex digits, and their
 * ASCII equivalents.
 */
public class FasttrackDump
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
            System.out.println("Usage: FasttrackDump <input mpp file name> <output text file name>");
         }
         else
         {
            System.out.println("Dump started.");
            long start = System.currentTimeMillis();
            process(args[0], args[1]);
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
   private static void process(String input, String output) throws Exception
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
         if (matchPattern(buffer, index))
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

      is.close();
      pw.flush();
      pw.close();
   }

   private static final void dumpBlock(int blockIndex, PrintWriter pw, int startIndex, int blockLength, byte[] buffer)
   {
      // Offset is not being stored in the block
      //pw.write("Offset: " + startIndex + " (" + Integer.toHexString(startIndex) + ")\n");
      pw.write("Block Index: " + blockIndex + "\n");
      pw.write("Length: " + blockLength + " (" + Integer.toHexString(blockLength) + ")\n");
      pw.write("\n");
      pw.write(hexdump(buffer, startIndex, blockLength, true, 16, ""));
      pw.write("\n\n");

      //      switch (buffer[startIndex])
      //      {
      //         case (byte) 0xFD:
      //         {
      //            dumpFD(pw, buffer, startIndex);
      //            break;
      //         }
      //
      //         case (byte) 0xFC:
      //         {
      //            dumpFC(pw, buffer, startIndex);
      //            break;
      //         }
      //      }

      if (blockIndex == 2)
      {
         dumpData(pw, buffer, startIndex, 10303);
      }
   }

   private static void dumpData(PrintWriter pw, byte[] buffer, int blockStartIndex, int blockOffset)
   {
      int offset = 0;
      pw.write("Item\n");
      int startIndex = blockStartIndex + blockOffset;
      while (true)
      {
         pw.flush();
         byte value = buffer[startIndex + offset];
         switch (value)
         {
            case (byte) 0x6E:
            case (byte) 0x6F:
            {
               offset += dumpFixedDataBlock(blockStartIndex, pw, buffer, startIndex + offset, 48, 4);
               break;
            }

            case (byte) 0x73:
            {
               offset += dumpFixedDataBlock(blockStartIndex, pw, buffer, startIndex + offset, 18, 6);
               break;
            }

            case (byte) 0x71:
            {
               offset += dumpFixedDataBlock(blockStartIndex, pw, buffer, startIndex + offset, 18, 3);
               break;
            }

            case (byte) 0x6C:
            {
               offset += dumpFixedDataBlock(blockStartIndex, pw, buffer, startIndex + offset, 18, 2);
               break;
            }

            case (byte) 0x5C:
            case (byte) 0x6D:
            {
               offset += dumpFixedDataBlock(blockStartIndex, pw, buffer, startIndex + offset, 18, 0);
               break;
            }

            case (byte) 0x46:
            case (byte) 0x70:
            {
               offset += dumpFixedDataBlock(blockStartIndex, pw, buffer, startIndex + offset, 18, 11);
               break;
            }

            case (byte) 0x4B:
            {
               offset += dumpFixedDataBlock(blockStartIndex, pw, buffer, startIndex + offset, 40, 0);
               break;
            }

            case (byte) 0x49:
            {
               offset += dumpAssignmentBlock(blockStartIndex, pw, buffer, startIndex + offset);
               break;
            }

            case (byte) 0x59:
            {
               offset += dumpOptionsBlock(blockStartIndex, pw, buffer, startIndex + offset);
               break;
            }

            case (byte) 0x53:
            {
               offset += dumpBooleanOptionsBlock(blockStartIndex, pw, buffer, startIndex + offset);
               break;
            }

            case (byte) 0x5b:
            case (byte) 0x4A:
            {
               offset += dumpDurationBlock(blockStartIndex, pw, buffer, startIndex + offset);
               break;
            }

            case (byte) 0x52:
            {
               offset += dumpIDBlock(blockStartIndex, pw, buffer, startIndex + offset);
               break;
            }

            case (byte) 0x54:
            {
               offset += dumpTotalCostBlock(blockStartIndex, pw, buffer, startIndex + offset);
               break;
            }

            case (byte) 0x57:
            case (byte) 0x58:
            {
               offset += dumpLinkBlock(blockStartIndex, pw, buffer, startIndex + offset);
               break;
            }

            case (byte) 0x68:
            {
               offset += dumpCreatedBlock(blockStartIndex, pw, buffer, startIndex + offset);
               break;
            }

            case (byte) 0x55:
            case (byte) 0x56:
            {
               offset += dumpTableBlock(blockStartIndex, pw, buffer, startIndex + offset);
               break;
            }

            default:
            {
               pw.write("Unknown block type at " + ((startIndex + offset) - blockStartIndex) + ": " + Integer.toHexString(value) + "\n");
               pw.write(hexdump(buffer, startIndex + offset, 1024, true, 16, ""));
               return;
            }
         }
      }
   }

   private static int dumpFixedDataBlock(int blockStartIndex, PrintWriter pw, byte[] buffer, int startIndex, int unknownBlockSize, int trailerLength)
   {
      int offset = 0;
      pw.write("Fixed Data Block at " + (startIndex - blockStartIndex) + "\n");

      pw.write(hexdump(buffer, startIndex + offset, 8, false, 16, ""));
      offset += 8;

      int nameLength = getInt(buffer, startIndex + offset);
      offset += 4;
      pw.write("Name Length: " + nameLength + "\n");

      String name = new String(buffer, startIndex + offset, nameLength, UTF16LE);
      offset += nameLength;
      pw.write("Name: " + name + "\n");

      int indexNumber = getInt(buffer, startIndex + offset);
      offset += 4;
      pw.write("Index Number: " + indexNumber + "\n");

      pw.write(hexdump(buffer, startIndex + offset, unknownBlockSize, false, 16, ""));
      offset += unknownBlockSize;

      int offsetToData = getShort(buffer, startIndex + offset);
      offset += 2;
      pw.write("Offset to Data: " + offsetToData + "\n");

      int numberOfItems = getInt(buffer, startIndex + offset);
      offset += 4;
      pw.write("Number of items: " + numberOfItems + "\n");

      int itemLength = getShort(buffer, startIndex + offset);
      offset += 2;
      pw.write("Item length: " + itemLength + "\n");

      int offsetToEnd = getInt(buffer, startIndex + offset);
      offset += 4;
      pw.write("Offset to End: " + offsetToEnd + "\n");

      for (int index = 0; index <= numberOfItems; index++)
      {
         pw.write("Item " + index + ": " + hexdump(buffer, startIndex + offset, itemLength, false, 16, ""));
         offset += itemLength;
      }

      pw.write(hexdump(buffer, startIndex + offset, trailerLength, false, 16, ""));
      offset += trailerLength;

      pw.write("Total Block Size: " + offset + "(" + Integer.toHexString(offset) + ")\n\n");
      return offset;
   }

   private static int dumpAssignmentBlock(int blockStartIndex, PrintWriter pw, byte[] buffer, int startIndex)
   {
      int offset = 0;
      pw.write("Assignment Block at " + (startIndex - blockStartIndex) + "\n");

      pw.write(hexdump(buffer, startIndex + offset, 8, false, 16, ""));
      offset += 8;

      int nameLength = getInt(buffer, startIndex + offset);
      offset += 4;
      pw.write("Name Length: " + nameLength + "\n");

      String name = new String(buffer, startIndex + offset, nameLength, UTF16LE);
      offset += nameLength;
      pw.write("Name: " + name + "\n");

      int indexNumber = getInt(buffer, startIndex + offset);
      offset += 4;
      pw.write("Index Number: " + indexNumber + "\n");

      pw.write(hexdump(buffer, startIndex + offset, 34, false, 16, ""));
      offset += 34;

      int numberOfItems = getInt(buffer, startIndex + offset);
      offset += 4;
      pw.write("Number of items: " + numberOfItems + "\n");

      for (int index = 0; index < numberOfItems; index++)
      {
         pw.write("Item " + index + "\n");
         pw.write("  " + hexdump(buffer, startIndex + offset, 2, false, 16, ""));
         offset += 2;
         int itemNameLength = getInt(buffer, startIndex + offset);
         offset += 4;
         pw.write("  Item Name Length: " + itemNameLength + "\n");
         String itemName = new String(buffer, startIndex + offset, itemNameLength, UTF16LE);
         offset += itemNameLength;
         pw.write("  Item Name: " + itemName + "\n");
      }

      pw.write(hexdump(buffer, startIndex + offset, 8, true, 16, ""));
      offset += 8;

      int numberOfFullNames = getInt(buffer, startIndex + offset);
      offset += 4;
      pw.write("Number of full names: " + numberOfFullNames + "\n");

      for (int index = 0; index < numberOfFullNames; index++)
      {
         pw.write("Full Name " + index + "\n");
         pw.write("  " + hexdump(buffer, startIndex + offset, 2, false, 16, ""));
         offset += 2;
         int itemNameLength = getInt(buffer, startIndex + offset);
         offset += 4;
         pw.write("  Full Name Length: " + itemNameLength + "\n");
         String itemName = new String(buffer, startIndex + offset, itemNameLength, UTF16LE);
         offset += itemNameLength;
         pw.write("  Full Name: " + itemName + "\n");
      }

      pw.write(hexdump(buffer, startIndex + offset, 8, true, 16, ""));
      offset += 8;

      pw.write("Total Block Size: " + offset + "(" + Integer.toHexString(offset) + ")\n\n");

      return offset;
   }

   private static int dumpCreatedBlock(int blockStartIndex, PrintWriter pw, byte[] buffer, int startIndex)
   {
      int offset = 0;
      pw.write("Assignment Block at " + (startIndex - blockStartIndex) + "\n");

      pw.write(hexdump(buffer, startIndex + offset, 8, false, 16, ""));
      offset += 8;

      int nameLength = getInt(buffer, startIndex + offset);
      offset += 4;
      pw.write("Name Length: " + nameLength + "\n");

      String name = new String(buffer, startIndex + offset, nameLength, UTF16LE);
      offset += nameLength;
      pw.write("Name: " + name + "\n");

      int indexNumber = getInt(buffer, startIndex + offset);
      offset += 4;
      pw.write("Index Number: " + indexNumber + "\n");

      int nextOffset = offset;
      while (getShort(buffer, startIndex + nextOffset) != 0x000F)
      {
         ++nextOffset;
      }
      nextOffset += 2;

      pw.write(hexdump(buffer, startIndex + offset, (nextOffset - offset), false, 16, ""));
      offset = nextOffset;

      //      pw.write(hexdump(buffer, startIndex + offset, 36, false, 16, ""));
      //      offset += 36;

      int numberOfItems = getInt(buffer, startIndex + offset);
      offset += 4;
      pw.write("Number of items: " + numberOfItems + "\n");

      int offsetToData = getInt(buffer, startIndex + offset);
      offset += 4;
      pw.write("Offset to data: " + offsetToData + "\n");

      int[] blockOffsets = new int[numberOfItems + 1];
      for (int index = 0; index <= numberOfItems; index++)
      {
         int offsetInBlock = getInt(buffer, startIndex + offset);
         blockOffsets[index] = offsetInBlock;
         offset += 4;
         pw.write("Item " + index + " offset in block:" + offsetInBlock + "\n");
      }

      int dataSize = getInt(buffer, startIndex + offset);
      offset += 4;
      pw.write("Data size: " + dataSize + "\n");

      for (int index = 0; index < numberOfItems; index++)
      {
         pw.write("Item " + index + "\n");
         int itemNameLength = blockOffsets[index + 1] - blockOffsets[index];
         pw.write("  Item Name Length: " + itemNameLength + "\n");
         pw.flush();
         String itemName = new String(buffer, startIndex + offset, itemNameLength, UTF16LE);
         offset += itemNameLength;
         pw.write("  Item Name: " + itemName + "\n");

      }

      pw.write(hexdump(buffer, startIndex + offset, 8, true, 16, ""));
      offset += 8;

      pw.write("Total Block Size: " + offset + "(" + Integer.toHexString(offset) + ")\n\n");

      return offset;
   }

   private static int dumpOptionsBlock(int blockStartIndex, PrintWriter pw, byte[] buffer, int startIndex)
   {
      int offset = 0;
      pw.write("Options Block at " + (startIndex - blockStartIndex) + "\n");

      pw.write(hexdump(buffer, startIndex + offset, 8, false, 16, ""));
      offset += 8;

      int nameLength = getInt(buffer, startIndex + offset);
      offset += 4;
      pw.write("Name Length: " + nameLength + "\n");

      String name = new String(buffer, startIndex + offset, nameLength, UTF16LE);
      offset += nameLength;
      pw.write("Name: " + name + "\n");

      int indexNumber = getInt(buffer, startIndex + offset);
      offset += 4;
      pw.write("Index Number: " + indexNumber + "\n");

      pw.write(hexdump(buffer, startIndex + offset, 34, false, 16, ""));
      offset += 34;

      int numberOfOptions = getInt(buffer, startIndex + offset);
      offset += 4;
      pw.write("Number of options: " + numberOfOptions + "\n");

      for (int index = 0; index < numberOfOptions; index++)
      {
         pw.write("Item " + index + "\n");
         pw.write("  " + hexdump(buffer, startIndex + offset, 2, false, 16, ""));
         offset += 2;
         int itemNameLength = getInt(buffer, startIndex + offset);
         offset += 4;
         pw.write("  Item Name Length: " + itemNameLength + "\n");
         String itemName = new String(buffer, startIndex + offset, itemNameLength, UTF16LE);
         offset += itemNameLength;
         pw.write("  Item Name: " + itemName + "\n");
      }

      pw.write(hexdump(buffer, startIndex + offset, 10, false, 16, ""));
      offset += 10;

      int offsetToData = getShort(buffer, startIndex + offset);
      offset += 2;
      pw.write("Offset to Data: " + offsetToData + "\n");

      int numberOfItems = getInt(buffer, startIndex + offset);
      offset += 4;
      pw.write("Number of items: " + numberOfItems + "\n");

      int itemLength = getShort(buffer, startIndex + offset);
      offset += 2;
      pw.write("Item length: " + itemLength + "\n");

      int offsetToEnd = getInt(buffer, startIndex + offset);
      offset += 4;
      pw.write("Offset to End: " + offsetToEnd + "\n");

      for (int index = 0; index <= numberOfItems; index++)
      {
         pw.write("Item " + index + ": " + hexdump(buffer, startIndex + offset, itemLength, false, 16, ""));
         offset += itemLength;
      }

      pw.write(hexdump(buffer, startIndex + offset, 2, false, 16, ""));
      offset += 2;

      pw.write("Total Block Size: " + offset + "(" + Integer.toHexString(offset) + ")\n\n");

      return offset;
   }

   private static int dumpBooleanOptionsBlock(int blockStartIndex, PrintWriter pw, byte[] buffer, int startIndex)
   {
      int offset = 0;
      pw.write("Boolean Options Block at " + (startIndex - blockStartIndex) + "\n");

      pw.write(hexdump(buffer, startIndex + offset, 8, false, 16, ""));
      offset += 8;

      int nameLength = getInt(buffer, startIndex + offset);
      offset += 4;
      pw.write("Name Length: " + nameLength + "\n");

      String name = new String(buffer, startIndex + offset, nameLength, UTF16LE);
      offset += nameLength;
      pw.write("Name: " + name + "\n");

      int indexNumber = getInt(buffer, startIndex + offset);
      offset += 4;
      pw.write("Index Number: " + indexNumber + "\n");

      pw.write(hexdump(buffer, startIndex + offset, 34, false, 16, ""));
      offset += 34;

      int numberOfOptions = getInt(buffer, startIndex + offset);
      offset += 4;
      pw.write("Number of options: " + numberOfOptions + "\n");

      for (int index = 0; index < numberOfOptions; index++)
      {
         pw.write("Item " + index + "\n");
         pw.write("  " + hexdump(buffer, startIndex + offset, 2, false, 16, ""));
         offset += 2;
         int itemNameLength = getInt(buffer, startIndex + offset);
         offset += 4;
         pw.write("  Item Name Length: " + itemNameLength + "\n");
         String itemName = new String(buffer, startIndex + offset, itemNameLength, UTF16LE);
         offset += itemNameLength;
         pw.write("  Item Name: " + itemName + "\n");
      }

      //      pw.write(hexdump(buffer, startIndex + offset, 36, false, 16, ""));
      //      offset += 36;

      int nextOffset = offset;
      while (getShort(buffer, startIndex + nextOffset) != 0x000F)
      {
         ++nextOffset;
      }
      nextOffset += 2;

      pw.write(hexdump(buffer, startIndex + offset, (nextOffset - offset), false, 16, ""));
      offset = nextOffset;

      int numberOfItems1 = getInt(buffer, startIndex + offset);
      offset += 4;
      pw.write("Number of items: " + numberOfItems1 + "\n");

      int itemLength1 = getInt(buffer, startIndex + offset);
      offset += 4;
      pw.write("Item length: " + itemLength1 + "\n");

      for (int index = 0; index <= numberOfItems1; index++)
      {
         pw.write("Item " + index + "\n");
         pw.write("  " + hexdump(buffer, startIndex + offset, 4, false, 16, ""));
         offset += 4;
      }

      int itemLength2 = getInt(buffer, startIndex + offset);
      offset += 4;
      pw.write("Item length: " + itemLength2 + "\n");

      for (int index = 0; index <= numberOfItems1; index++)
      {
         pw.write("Item " + index + "\n");
         pw.write("  " + hexdump(buffer, startIndex + offset, 2, false, 16, ""));
         offset += 2;
      }

      pw.write(hexdump(buffer, startIndex + offset, 6, false, 16, ""));
      offset += 6;

      pw.write("Total Block Size: " + offset + "(" + Integer.toHexString(offset) + ")\n\n");

      return offset;
   }

   private static int dumpDurationBlock(int blockStartIndex, PrintWriter pw, byte[] buffer, int startIndex)
   {
      int offset = 0;
      pw.write("Duration Block at " + (startIndex - blockStartIndex) + "\n");

      pw.write(hexdump(buffer, startIndex + offset, 8, false, 16, ""));
      offset += 8;

      int nameLength = getInt(buffer, startIndex + offset);
      offset += 4;
      pw.write("Name Length: " + nameLength + "\n");

      String name = new String(buffer, startIndex + offset, nameLength, UTF16LE);
      offset += nameLength;
      pw.write("Name: " + name + "\n");

      int indexNumber = getInt(buffer, startIndex + offset);
      offset += 4;
      pw.write("Index Number: " + indexNumber + "\n");

      pw.write(hexdump(buffer, startIndex + offset, 16, false, 16, ""));
      offset += 16;

      int numberOfItems = getInt(buffer, startIndex + offset);
      offset += 4;
      pw.write("Number of items: " + numberOfItems + "\n");

      for (int index = 0; index < numberOfItems; index++)
      {
         pw.write("Item " + index + "\n");
         pw.write("  " + hexdump(buffer, startIndex + offset, 8, false, 16, ""));
         offset += 8;
      }

      pw.write(hexdump(buffer, startIndex + offset, 25, true, 16, ""));
      offset += 25;

      pw.write("Total Block Size: " + offset + "(" + Integer.toHexString(offset) + ")\n\n");

      return offset;
   }

   private static int dumpIDBlock(int blockStartIndex, PrintWriter pw, byte[] buffer, int startIndex)
   {
      int offset = 0;
      pw.write("ID Block at " + (startIndex - blockStartIndex) + "\n");

      pw.write(hexdump(buffer, startIndex + offset, 8, false, 16, ""));
      offset += 8;

      int nameLength = getInt(buffer, startIndex + offset);
      offset += 4;
      pw.write("Name Length: " + nameLength + "\n");

      String name = new String(buffer, startIndex + offset, nameLength, UTF16LE);
      offset += nameLength;
      pw.write("Name: " + name + "\n");

      int indexNumber = getInt(buffer, startIndex + offset);
      offset += 4;
      pw.write("Index Number: " + indexNumber + "\n");

      pw.write(hexdump(buffer, startIndex + offset, 58, false, 16, ""));
      offset += 58;

      pw.write("Total Block Size: " + offset + "(" + Integer.toHexString(offset) + ")\n\n");

      return offset;
   }

   private static int dumpTotalCostBlock(int blockStartIndex, PrintWriter pw, byte[] buffer, int startIndex)
   {
      int offset = 0;
      pw.write("Duration Block at " + (startIndex - blockStartIndex) + "\n");

      pw.write(hexdump(buffer, startIndex + offset, 8, false, 16, ""));
      offset += 8;

      int nameLength = getInt(buffer, startIndex + offset);
      offset += 4;
      pw.write("Name Length: " + nameLength + "\n");

      String name = new String(buffer, startIndex + offset, nameLength, UTF16LE);
      offset += nameLength;
      pw.write("Name: " + name + "\n");

      int indexNumber = getInt(buffer, startIndex + offset);
      offset += 4;
      pw.write("Index Number: " + indexNumber + "\n");

      pw.write(hexdump(buffer, startIndex + offset, 16, false, 16, ""));
      offset += 16;

      int numberOfItems = getInt(buffer, startIndex + offset);
      offset += 4;
      pw.write("Number of items: " + numberOfItems + "\n");

      for (int index = 0; index < numberOfItems; index++)
      {
         pw.write("Item " + index + "\n");
         pw.write("  " + hexdump(buffer, startIndex + offset, 8, false, 16, ""));
         offset += 8;
      }

      pw.write(hexdump(buffer, startIndex + offset, 17, true, 16, ""));
      offset += 17;

      pw.write("Total Block Size: " + offset + "(" + Integer.toHexString(offset) + ")\n\n");

      return offset;
   }

   private static int dumpLinkBlock(int blockStartIndex, PrintWriter pw, byte[] buffer, int startIndex)
   {
      int offset = 0;
      pw.write("Link Block at " + (startIndex - blockStartIndex) + "\n");

      pw.write(hexdump(buffer, startIndex + offset, 8, false, 16, ""));
      offset += 8;

      int nameLength = getInt(buffer, startIndex + offset);
      offset += 4;
      pw.write("Name Length: " + nameLength + "\n");

      String name = new String(buffer, startIndex + offset, nameLength, UTF16LE);
      offset += nameLength;
      pw.write("Name: " + name + "\n");

      int indexNumber = getInt(buffer, startIndex + offset);
      offset += 4;
      pw.write("Index Number: " + indexNumber + "\n");

      pw.write(hexdump(buffer, startIndex + offset, 16, false, 16, ""));
      offset += 16;

      int numberOfOptions = getInt(buffer, startIndex + offset);
      offset += 4;
      pw.write("Number of links: " + numberOfOptions + "\n");

      for (int index = 0; index <= numberOfOptions; index++)
      {
         pw.write("Item " + index + "\n");
         pw.write("  " + hexdump(buffer, startIndex + offset, 2, false, 16, ""));
         offset += 2;
         int itemNameLength = getInt(buffer, startIndex + offset);
         offset += 4;
         pw.write("  Item Name Length: " + itemNameLength + "\n");
         String itemName = new String(buffer, startIndex + offset, itemNameLength, UTF16LE);
         offset += itemNameLength;
         pw.write("  Item Name: " + itemName + "\n");
      }

      pw.write(hexdump(buffer, startIndex + offset, 2, false, 16, ""));
      offset += 2;

      pw.write("Total Block Size: " + offset + "(" + Integer.toHexString(offset) + ")\n\n");

      return offset;
   }

   private static int dumpTableBlock(int blockStartIndex, PrintWriter pw, byte[] buffer, int startIndex)
   {
      int offset = 0;
      pw.write("Assignment Block at " + (startIndex - blockStartIndex) + "\n");

      pw.write(hexdump(buffer, startIndex + offset, 8, false, 16, ""));
      offset += 8;

      int nameLength = getInt(buffer, startIndex + offset);
      offset += 4;
      pw.write("Name Length: " + nameLength + "\n");

      String name = new String(buffer, startIndex + offset, nameLength, UTF16LE);
      offset += nameLength;
      pw.write("Name: " + name + "\n");

      int indexNumber = getInt(buffer, startIndex + offset);
      offset += 4;
      pw.write("Index Number: " + indexNumber + "\n");

      int nextOffset = offset;
      while (getShort(buffer, startIndex + nextOffset) != 0x000F)
      {
         ++nextOffset;
      }
      nextOffset += 2;

      pw.write(hexdump(buffer, startIndex + offset, (nextOffset - offset), false, 16, ""));
      offset = nextOffset;

      //      pw.write(hexdump(buffer, startIndex + offset, 36, false, 16, ""));
      //      offset += 36;

      int numberOfItems = getInt(buffer, startIndex + offset);
      offset += 4;
      pw.write("Number of items: " + numberOfItems + "\n");

      int offsetToData = getInt(buffer, startIndex + offset);
      offset += 4;
      pw.write("Offset to data: " + offsetToData + "\n");

      int[] blockOffsets = new int[numberOfItems + 1];
      for (int index = 0; index <= numberOfItems; index++)
      {
         int offsetInBlock = getInt(buffer, startIndex + offset);
         blockOffsets[index] = offsetInBlock;
         offset += 4;
         pw.write("Item " + index + " offset in block:" + offsetInBlock + "\n");
      }

      int dataSize = getInt(buffer, startIndex + offset);
      offset += 4;
      pw.write("Data size: " + dataSize + "\n");

      for (int index = 0; index < numberOfItems; index++)
      {
         pw.write("Item " + index + "\n");
         int dataLength = blockOffsets[index + 1] - blockOffsets[index];
         pw.write("  Data Length: " + dataLength + "\n");
         pw.flush();
         pw.write(hexdump(buffer, startIndex + offset, dataLength, false, 16, ""));
         offset += dataLength;
      }

      pw.write(hexdump(buffer, startIndex + offset, 4, true, 16, ""));
      offset += 4;

      pw.write("Total Block Size: " + offset + "(" + Integer.toHexString(offset) + ")\n\n");

      return offset;
   }

   private static final void dumpFD(PrintWriter pw, byte[] buffer, int startIndex)
   {
      pw.write("FD BLOCK\n");
      int offset = 0;
      pw.write(hexdump(buffer, startIndex + offset, 41, true, 16, ""));
      offset += 41;

      int nameLength = getInt(buffer, startIndex + offset);
      offset += 4;
      System.out.println(nameLength);
      if (nameLength > 0)
      {
         String name = new String(buffer, startIndex + offset, nameLength, UTF16LE);
         offset += nameLength;
         pw.write("Block Name: " + name + "\n");
         int blockNumber = getShort(buffer, startIndex + offset);
         pw.write("Block Number: " + blockNumber + "\n");
         offset += 2;
         pw.write(hexdump(buffer, startIndex + offset, 8, false, 16, ""));
         offset += 8;
         int itemCount = getInt(buffer, startIndex + offset);
         offset += 4;
         pw.write("Item Count: " + itemCount + "\n");

         pw.write(hexdump(buffer, startIndex + offset, 2, false, 16, ""));
         offset += 2;

         int offsetToEnd = getInt(buffer, startIndex + offset);
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

   private static final void dumpFC(PrintWriter pw, byte[] buffer, int startIndex)
   {
      pw.write("FC BLOCK\n");
      int offset = 1;
      pw.write(hexdump(buffer, startIndex + offset, 16, true, 16, ""));
      offset += 16;

      //      offset = dumpFcBlock(pw, buffer, startIndex, offset);
      //      offset = dumpFcBlock(pw, buffer, startIndex, offset);
      //offset = dumpFcBlock(pw, buffer, startIndex, offset);

      //      pw.write(hexdump(buffer, startIndex + offset, 6, true, 16, ""));
      //      offset += 6;
      //
      //      int itemCount = getInt(buffer, startIndex + offset);
      //      offset += 4;
      //      pw.write("Item Count: " + itemCount + "\n");
      //
      //      int itemSize = getShort(buffer, startIndex + offset);
      //      offset += 2;
      //      pw.write("Item Size: " + itemSize + "\n");
      //
      //      int dataSize = getInt(buffer, startIndex + offset);
      //      offset += 4;
      //      pw.write("Data Size: " + dataSize + "\n");
      //
      //      if (dataSize != 0)
      //      {
      //         pw.write("Data:\n");
      //         pw.write(hexdump(buffer, startIndex + offset, dataSize, true, 16, ""));
      //         offset += dataSize;
      //      }

      //      // First Item
      //      pw.write("Item 1\n");
      //      pw.write(hexdump(buffer, startIndex + offset, 4, false, 4, ""));
      //      offset += 4;
      //
      //      int itemNameLength = getInt(buffer, startIndex + offset);
      //      offset += 4;
      //      pw.write("Item Name Length: " + itemNameLength + "\n");
      //      String itemName = new String(buffer, startIndex + offset, itemNameLength, UTF16LE);
      //      offset += itemNameLength;
      //      pw.write("Item Name: " + itemName + "\n");

      pw.write("\n");
   }

   private static int dumpFcBlock(PrintWriter pw, byte[] buffer, int startIndex, int offset)
   {
      int itemCount = getInt(buffer, startIndex + offset);
      offset += 4;
      pw.write("Item Count: " + itemCount + "\n");

      int itemSize = getShort(buffer, startIndex + offset);
      offset += 2;
      pw.write("Item Size: " + itemSize + "\n");

      int dataSize = getInt(buffer, startIndex + offset);
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

   private static final int getInt(byte[] data, int offset)
   {
      int result = 0;
      int i = offset;
      for (int shiftBy = 0; shiftBy < 32; shiftBy += 8)
      {
         result |= ((data[i] & 0xff)) << shiftBy;
         ++i;
      }
      return result;
   }

   private static final int getShort(byte[] data, int offset)
   {
      int result = 0;
      int i = offset;
      for (int shiftBy = 0; shiftBy < 16; shiftBy += 8)
      {
         result |= ((data[i] & 0xff)) << shiftBy;
         ++i;
      }
      return result;
   }

   private static final boolean matchPattern(byte[] buffer, int bufferIndex)
   {
      boolean match = false;
      for (byte[] pattern : PATTERNS)
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

   public static final String hexdump(byte[] buffer, int offset, int length, boolean ascii, int columns, String prefix)
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

   public static final String hexdump(byte[] buffer, int offset, int length, boolean ascii)
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

   private static final byte[][] PATTERNS =
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

   private static final Charset UTF16LE = Charset.forName("UTF-16LE");
}
