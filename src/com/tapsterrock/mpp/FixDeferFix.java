/*
 * file:       FixDeferFix.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2002-2003
 * date:       31/03/2003
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

import java.io.InputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.TreeMap;
import java.util.Iterator;

/**
 * This class represents the a block of variable length data items that appears
 * in the Microsoft Project 98 file format.
 */
public class FixDeferFix extends MPPComponent
{
	/**
	 * Extract the variable size data items from the input stream.
	 * 
	 * @param is Input stream
	 * @throws IOException Thrown on read errors
	 */
   public FixDeferFix (InputStream is)
      throws IOException
   {		
		//
		// 8 byte header
		//			
		int int1 = readInt(is);
		int int2 = readInt(is);
		
		//
		// Read data
		//
		int offset = 8;
		int size;
		int skip;
		int available = is.available();
					
		while (offset < available)
		{
			size = readInt(is);				
			if ((offset+4+size) > available)
			{
				break;
			}
							
			m_map.put(new Integer(offset), new ByteArray (readByteArray(is, size)));
			
			offset += (size+4);		
			skip = (((size / 32) + 1) * 32) - size;				
			is.skip(skip);
			offset += skip;
		}		
   }

   /**
    * This method dumps the contents of this FixDeferFix block as a String.
    * Note that this facility is provided as a debugging aid.
    *
    * @return formatted contents of this block
    */
   public String toString ()
   {
      StringWriter sw = new StringWriter ();
      PrintWriter pw = new PrintWriter (sw);
      Iterator iter = m_map.keySet().iterator();
      Integer offset;
      ByteArray data;

      pw.println ("BEGIN FixDeferFix");
      while (iter.hasNext() == true)
      {
         offset = (Integer)iter.next();
         data = (ByteArray)m_map.get(offset);
         pw.println ("   Data at offset: " + offset + " size: " + data.byteArrayValue().length);
         pw.println ("  " + MPPUtility.hexdump (data.byteArrayValue(), true));
      }

      pw.println ("END FixDeferFix");
      pw.println ();
      pw.close();
      return (sw.toString());
   }

   /**
    * Map containing data items indexed by offset.
    */
   private TreeMap m_map = new TreeMap ();
}
