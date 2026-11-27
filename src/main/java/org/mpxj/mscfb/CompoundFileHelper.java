/*
 * file:       CompoundFileHelper.java
 * author:     Jon Iles
 * date:       2026-11-27
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

package org.mpxj.mscfb;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;

/**
 * Callers use the static methods provided by this class to open
 * OLE2 Compound Document / MS-CFB files.
 */
public final class CompoundFileHelper
{
   /**
    * Open a compound file from an input stream.
    *
    * @param is input stream
    * @return CompoundFile instance
    */
   public static CompoundFile open(InputStream is) throws IOException
   {
      return new PoiCompoundFile(is);
   }

   /**
    * Open a compound file from a File instance.
    *
    * @param file File instance
    * @return CompoundFile instance
    */
   public static CompoundFile open(File file) throws IOException
   {
      return new PoiCompoundFile(file);
   }

   /**
    * Open a compound file from a file channel.
    *
    * @param channel file channel
    * @return CompoundFile instance
    */
   public static CompoundFile open(FileChannel channel) throws IOException
   {
      return new PoiCompoundFile(channel);
   }
}
