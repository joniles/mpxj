/*
 * file:       MppDump.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2002-2003
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

package com.tapsterrock.utility;

import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.poifs.filesystem.Entry;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Iterator;
import java.io.InputStream;

/**
 * This is a trivial class used to dump the contents of an MPP file
 * broken down into its interal file and directory structure, with the
 * content of each of the files written out as hex digits, and their
 * ASCII equivalents.
 */
public class MppDump
{
   /**
    * Main method.
    *
    * @param args array of command line arguments
    */
   public static void main (String[] args)
   {
      try
      {
         if (args.length != 2)
         {
            System.out.println ("Usage: MppDump <input mpp file name> <output text file name>");
         }
         else
         {
            process (args[0], args[1]);
         }
      }

      catch (Exception ex)
      {
         System.out.println ("Caught " + ex.toString());
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
   private static void process (String input, String output)
      throws Exception
   {
      FileInputStream is = new FileInputStream (input);
      PrintWriter pw = new PrintWriter (new FileWriter (output));

      POIFSFileSystem fs = new POIFSFileSystem (is);
      dumpTree (pw, fs.getRoot(), "");

      is.close();
      pw.flush();
      pw.close();
   }

   /**
    * This method recursively descends the directory structure, dumping
    * details of any files it finds to the output file.
    *
    * @param pw Output PrintWriter
    * @param dir DirectoryEntry to dump
    * @param prefix Prefix to prepend to output
    * @throws Exception Thrown on file read errors
    */
   private static void dumpTree (PrintWriter pw, DirectoryEntry dir, String prefix)
      throws Exception
   {
      prefix += " ";

      for (Iterator iter = dir.getEntries(); iter.hasNext(); )
      {
         Entry entry = (Entry)iter.next();
         if (entry instanceof DirectoryEntry)
         {
            pw.println ("start dir: " + entry.getName());
            dumpTree (pw, (DirectoryEntry)entry, prefix);
            pw.println ("end dir: " + entry.getName());
         }
         else if (entry instanceof DocumentEntry)
         {
            pw.println ("start doc: " + entry.getName());
            hexdump (new DocumentInputStream ((DocumentEntry)entry), pw);
            pw.println ("end doc: " + entry.getName());
         }
         else
         {
            pw.println (prefix + "found unknown: " + entry.getName());
         }
      }
   }

   /**
    * This method dumps the entire contents of a file to an output
    * print writer as hex and ASCII data.
    *
    * @param is Input Stream
    * @param pw Output PrintWriter
    * @throws Exception Thrown on file read errors
    */
   private static void hexdump (InputStream is, PrintWriter pw)
      throws Exception
   {
      byte[] buffer = new byte[BUFFER_SIZE];

      char c;
      int loop;
      int count;
      long address = 0;
      StringBuffer sb = new StringBuffer ();

      while (true)
      {
         count = is.read(buffer);
         if (count == -1)
         {
            break;
         }

         sb.setLength(0);

         for (loop=0; loop < count; loop++)
         {
            sb.append (" ");
            sb.append (HEX_DIGITS[(buffer[loop] & 0xF0) >> 4]);
            sb.append (HEX_DIGITS[buffer[loop] & 0x0F]);
         }

         while (loop < BUFFER_SIZE)
         {
            sb.append ("   ");
            ++loop;
         }

         sb.append ("   ");

         for (loop=0; loop < count; loop++)
         {
            c = (char)buffer[loop];
            if (c > 200 || c < 27)
            {
               c = ' ';
            }

            sb.append (c);
         }

         pw.println (sb.toString());

         address += count;
      }
   }

   /**
    * Buffer size for data output.
    */
   private static final int BUFFER_SIZE = 16;

   /**
    * Data used for conversion to hex.
    */
   private static final char[] HEX_DIGITS =
   {
      '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
      'A', 'B', 'C', 'D', 'E', 'F'
   };
}