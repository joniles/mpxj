/*
 * file:       PoiCompoundFileProvider.java
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
 * Implementation of the CompoundFileProvider interface using
 * the POI library.
 */
class PoiCompoundFileProvider implements CompoundFileProvider
{
   @Override public CompoundFile open(InputStream is) throws IOException
   {
      return new PoiCompoundFile(is);
   }

   @Override public CompoundFile open(File file) throws IOException
   {
      return new PoiCompoundFile(file);
   }

   @Override public CompoundFile open(FileChannel channel) throws IOException
   {
      return new PoiCompoundFile(channel);
   }
}
