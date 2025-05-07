/*
 * file:       MPPComponent.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2002-2003
 * date:       03/01/2003
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

package org.mpxj.mpp;

import java.io.IOException;
import java.io.InputStream;

import org.mpxj.common.ByteArrayHelper;
import org.mpxj.common.InputStreamHelper;

/**
 * This class provides common functionality used by each of the classes
 * that read the different sections of the MPP file.
 */
class MPPComponent
{
   /**
    * Constructor.
    */
   protected MPPComponent()
   {
      // protected constructor to prevent instantiation
   }

   /**
    * This method reads a single byte from the input stream.
    *
    * @param is the input stream
    * @return byte value
    * @throws IOException on file read error or EOF
    */
   protected int readByte(InputStream is) throws IOException
   {
      return MPPUtility.getByte(InputStreamHelper.read(is, 1), 0);
   }

   /**
    * This method reads a two byte integer from the input stream.
    *
    * @param is the input stream
    * @return integer value
    * @throws IOException on file read error or EOF
    */
   protected int readShort(InputStream is) throws IOException
   {
      return ByteArrayHelper.getShort(InputStreamHelper.read(is, 2), 0);
   }

   /**
    * This method reads a four byte integer from the input stream.
    *
    * @param is the input stream
    * @return byte value
    * @throws IOException on file read error or EOF
    */
   protected int readInt(InputStream is) throws IOException
   {
      return ByteArrayHelper.getInt(InputStreamHelper.read(is, 4), 0);
   }

   /**
    * This method reads a byte array from the input stream.
    *
    * @param is the input stream
    * @param size number of bytes to read
    * @return byte array
    * @throws IOException on file read error or EOF
    */
   protected byte[] readByteArray(InputStream is, int size) throws IOException
   {
      return InputStreamHelper.read(is, size);
   }
}
