/*
 * file:       DoubleColumn.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2017
 * date:       14/03/2017
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

import java.io.PrintWriter;

/**
 * Column containing double values.
 */
class DoubleColumn extends AbstractColumn
{
   /**
    * {@inheritDoc}
    */
   @Override protected int postHeaderSkipBytes()
   {
      return 16;
   }

   /**
    * {@inheritDoc}
    */
   @Override protected int readData(byte[] buffer, int offset)
   {
      m_data = new Double[FastTrackUtility.getInt(buffer, offset)];
      offset += 4;

      for (int index = 0; index < m_data.length; index++)
      {
         m_data[index] = FastTrackUtility.getDouble(buffer, offset);
         offset += 8;
      }

      return offset;
   }

   /**
    * {@inheritDoc}
    */
   @Override protected void dumpData(PrintWriter pw)
   {
      pw.println("  [Data");
      for (Object item : m_data)
      {
         pw.println("    " + item);
      }
      pw.println("  ]");
   }
}
