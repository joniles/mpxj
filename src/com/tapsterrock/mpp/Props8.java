/*
 * file:       Props8.java
 * author:     Jon Iles
 * copyright:  Tapster Rock Limited
 * date:       12/11/2003
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
 * This class represents the Props files found in Microsoft Project MPP8 files.
 * These files appear to be collections of properties, indexed by an integer
 * key. The format of the properties section is not fully understood, so
 * reading data from the section may fail. To allow the rest of the file
 * to be read in successfully, any failure to read the props section will
 * not cause an exception, instead the complete flag is set to false to
 * indicate that the property data has not been fully retrieved. All properties
 * retrieved up to the point of failure will be available.
 */
final class Props8 extends Props
{
   /**
    * Constructor, reads the property data from an input stream.
    *
    * @param is input stream fo reading Props data
    */
   Props8 (InputStream is)
   {
      try
      {
//         FileOutputStream fos = new FileOutputStream ("c:\\temp\\props8." + System.currentTimeMillis() + ".txt");
//         PrintWriter pw = new PrintWriter (fos);

         readInt (is); // File size
         readInt (is); // Repeat of file size
         readInt(is); // unknown
         int count = readShort(is); // Number of entries
         readShort(is); // unknown


         for (int loop=0; loop < count; loop++)
         {
            int attrib1 = readInt(is);
            int attrib2 = readShort(is);
            int attrib3 = is.read();
            is.read(); // attrib4
            int attrib5 = readInt(is);
            int size;
            byte[] data;

            if (attrib3 == 64)
            {
               size = attrib1;
            }
            else
            {
               size = attrib5;
            }

            if (attrib5 == 65536)
            {
               size = 4;
            }

            if (size != 0)
            {
               data = new byte[size];
               is.read(data);
            }
            else
            {
               // bail out here as we don't understand the structure
               m_complete = false;
               break;
            }

            m_map.put(new Integer (attrib2), data);
//            pw.println(attrib2 + ": " + MPPUtility.hexdump(data, true));

            //
            // Align to two byte boundary
            //
            if (data.length % 2 != 0)
            {
               is.skip(1);
            }
         }

//         pw.flush();
//         pw.close();
      }

      catch (IOException ex)
      {
         m_complete = false;
      }
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
      if (m_complete == true)
      {
         pw.println ("   COMPLETE");
      }
      else
      {
         pw.println ("   INCOMPLETE");
      }

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

   private boolean m_complete = true;
}
