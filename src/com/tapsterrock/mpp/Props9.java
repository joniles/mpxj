/*
 * file:       Props9.java
 * author:     Jon Iles
 * copyright:  Tapster Rock Limited
 * date:       07/11/2003
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

package com.tapsterrock.mpp;

//import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;

/**
 * This class represents the Props files found in Microsoft Project MPP9 files.
 */
final class Props9 extends Props
{
   /**
    * Constructor, reads the property data from an input stream.
    *
    * @param is input stream for reading props data
    */
   Props9 (InputStream is)
      throws IOException
   {
//      FileOutputStream fos = new FileOutputStream ("c:\\temp\\props9." + System.currentTimeMillis() + ".txt");
//      PrintWriter pw = new PrintWriter (fos);

      byte[] header = new byte[16];
      byte[] data;
      is.read(header);
      int address = 16;

      int headerCount = MPPUtility.getShort(header, 12);
      int foundCount = 0;

      while (foundCount < headerCount)
      {
         int attrib1 = readInt(is);
         int attrib2 = readShort(is);
         readShort(is); // attrib3
         int attrib4 = readInt(is);
         address += 12;

         data = new byte[attrib1];
         is.read(data);
         address += attrib4;

         m_map.put(new Integer (attrib2), data);
//         pw.println(foundCount + " "+ attrib2 + ": " + MPPUtility.hexdump(data, true));
         ++foundCount;

         //
         // Align to two byte boundary
         //
         if (data.length % 2 != 0)
         {
            is.skip(1);
            ++address;
         }
      }

//      pw.flush();
//      pw.close();
   }

   /**
    * This method dumps the contents of this properties block as a String.
    * Note that this facility is provided as a debugging aid.
    *
    * @return formatted contents of this block
    */
   public String toString ()
   {
      StringWriter sw = new StringWriter ();
      PrintWriter pw = new PrintWriter (sw);

      pw.println ("BEGIN Props");

      Iterator iter = m_map.keySet().iterator();
      Integer key;

      while (iter.hasNext() == true)
      {
         key = (Integer)iter.next();
         pw.println ("   Key: " + key + " Value: " + MPPUtility.hexdump((byte[])m_map.get(key), true));
      }

      pw.println ("END Props");

      pw.println ();
      pw.close();
      return (sw.toString());
   }
}
