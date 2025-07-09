/*
 * file:       AbstractReader.java
 * author:     Jon Iles
 * date:       2024-02-27
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

package org.mpxj.openplan;

import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.DocumentInputStream;

/**
 * Base class for working with data stored within a BK3 file.
 */
abstract class AbstractReader
{
   /**
    * Constructor.
    *
    * @param dir parent directory
    * @param name file name
    */
   public AbstractReader(DirectoryEntry dir, String name)
   {
      try
      {
         m_is = new DocumentInputStream((DocumentEntry) dir.getEntry(name));
      }

      catch (IOException e)
      {
         throw new OpenPlanException(e);
      }
   }

   /**
    * Read a 4 byte int.
    *
    * @return in value
    */
   protected int getInt()
   {
      try
      {
         int result = 0;
         for (int shiftBy = 0; shiftBy < 32; shiftBy += 8)
         {
            result |= ((m_is.read() & 0xff)) << shiftBy;
         }
         return result;
      }

      catch (IOException ex)
      {
         throw new OpenPlanException(ex);
      }
   }

   /**
    * Read a 2 byte int.
    *
    * @return in value
    */
   protected int getShort()
   {
      try
      {
         int result = 0;
         for (int shiftBy = 0; shiftBy < 16; shiftBy += 8)
         {
            result |= ((m_is.read() & 0xff)) << shiftBy;
         }
         return result;
      }

      catch (IOException ex)
      {
         throw new OpenPlanException(ex);
      }
   }

   /**
    * Read a single byte.
    *
    * @return byte value
    */
   protected int getByte()
   {
      try
      {
         return m_is.read();
      }

      catch (IOException ex)
      {
         throw new OpenPlanException(ex);
      }
   }

   /**
    * Read a string.
    *
    * @return string value
    */
   protected String getString()
   {
      try
      {
         int length = getByte();
         if (length <= 0)
         {
            return null;
         }

         if (length == 255)
         {
            length = getShort();
         }

         byte[] bytes = new byte[length];
         if (m_is.read(bytes) != length)
         {
            throw new OpenPlanException("Failed to read expected number of bytes");
         }

         return new String(bytes);
      }

      catch (IOException ex)
      {
         throw new OpenPlanException(ex);
      }
   }

   private final InputStream m_is;
}
