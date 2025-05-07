/*
 * file:       UnknownColumn.java
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

package org.mpxj.fasttrack;

import java.io.PrintWriter;

/**
 * Placeholder for a column we don't know how to read.
 */
class UnknownColumn extends AbstractColumn
{
   @Override protected int postHeaderSkipBytes()
   {
      return 0;
   }

   @Override protected int readData(byte[] buffer, int offset)
   {
      m_data = new Object[0];
      return offset;
   }

   @Override protected void dumpData(PrintWriter pw)
   {
      // Nothing to dump
   }
}
