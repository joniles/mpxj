/*
 * file:       CompoundFileProvider.java
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
 * Implemented by classes which can open OLE2 Compound Document / MS-CFB files
 * and return an object which implements the CompoundFile interface, allowing
 * the contents of the file to be read,
 */
public interface CompoundFileProvider
{
   /**
    * Open a compound file from an InputStream.
    *
    * @param is InputStream instance
    * @return object implementing CompoundFile interface
    */
   CompoundFile open(InputStream is) throws IOException;

   /**
    * Open a compound file from a File.
    *
    * @param file File instance
    * @return object implementing CompoundFile interface
    */
   CompoundFile open(File file) throws IOException;

   /**
    * Open a compound file from a FileChannel.
    *
    * @param channel FileChannel instance
    * @return object implementing CompoundFile interface
    */
   CompoundFile open(FileChannel channel) throws IOException;
}
