/*
 * file:       CompoundFile.java
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

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * Interface representing an OLE2 Compound Document, also known as a Microsoft Compound File Binary File Format file (MS-CFB).
 * <a href="https://learn.microsoft.com/en-us/openspecs/windows_protocols/ms-cfb/53989ce4-7b05-4f8d-829b-d08d6148375b">a Microsoft Compound File Binary File Format file (MS-CFB)</a>
 */
public interface CompoundFile extends Closeable
{
   /**
    * List the contents of the directory with supplied path.
    *
    * @param path target path
    * @return list of absolute paths representing the contents of the directory
    */
   List<String> list(String path) throws IOException;

   /**
    * Return an InputStream to allow the contents of a file to be read.
    *
    * @param path target path
    * @return InputStream used to read the file contents
    */
   InputStream read(String path) throws IOException;

   /**
    * Retrieve a map representing the summary information for this compound file.
    * Note that any values are represented as text.
    *
    * @return file summary information
    */
   Map<String, String> getSummaryInformation() throws IOException;

   /**
    * Retrieve a map representing the document summary information for this compound file.
    * Note that any values are represented as text.
    *
    * @return document summary information
    */
   Map<String, String> getDocumentSummaryInformation() throws IOException;

   /**
    * Retrieve a map representing any custom properties for this compound file.
    * Note that any values are represented by native types.
    *
    * @return custom properties
    */
   Map<String, Object> getCustomProperties() throws IOException;

   /**
    * Returns true if the supplied path exists in the compound file.
    *
    * @param path target path
    * @return true if the supplied path exists
    */
   boolean exists(String path) throws IOException;

   /**
    * Returns true if the supplied path is a directory.
    *
    * @param path target path
    * @return true if the supplied path is a directory
    */
   boolean isDirectory(String path) throws IOException;

   /**
    * Returns true if the supplied path is a file.
    *
    * @param path target path
    * @return true if the supplied path is a file
    */
   boolean isFile(String path) throws IOException;

   /**
    * Create a new file with the supplied path, the contents of the new
    * file are read from the supplied InputStream instance.
    *
    * @param path target path
    * @param stream InputStream instance
    */
   void create(String path, InputStream stream) throws IOException;

   /**
    * Delete the file with the supplied path.
    *
    * @param path targte path
    */
   void delete(String path) throws IOException;

   /**
    * Write the compound file to the supplied output stream.
    *
    * @param stream OutputStream instance
    */
   void write(OutputStream stream) throws IOException;

   @Override void close() throws IOException;
}
