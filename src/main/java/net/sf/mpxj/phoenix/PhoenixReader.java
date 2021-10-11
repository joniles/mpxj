/*
 * file:       PhoenixReader.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2015
 * date:       28 November 2015
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

package net.sf.mpxj.phoenix;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.common.CharsetHelper;
import net.sf.mpxj.common.SemVer;
import net.sf.mpxj.reader.AbstractProjectStreamReader;

/**
 * This class creates a new ProjectFile instance by reading a Phoenix Project Manager file.
 */
public final class PhoenixReader extends AbstractProjectStreamReader
{
   /**
    * {@inheritDoc}
    */
   @Override public ProjectFile read(InputStream stream) throws MPXJException
   {
      try
      {
         BufferedInputStream bis = new BufferedInputStream(stream);
         bis.mark(BUFFER_SIZE);
         byte[] buffer = new byte[BUFFER_SIZE];
         int bytesRead = bis.read(buffer);
         bis.reset();

         //
         // If the file is smaller than the buffer we are peeking into,
         // it's probably not a valid schedule file.
         //
         if (bytesRead != BUFFER_SIZE)
         {
            return null;
         }

         Matcher matcher = VERSION_PATTERN.matcher(new String(buffer, CharsetHelper.UTF8));
         SemVer version = matcher.find() ? new SemVer(matcher.group(1)) : VERSION_4;
         return (version.before(VERSION_5) ? new Phoenix4Reader() : new Phoenix5Reader()).read(bis);
      }

      catch (Exception ex)
      {
         throw new MPXJException(MPXJException.INVALID_FILE, ex);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override public List<ProjectFile> readAll(InputStream inputStream) throws MPXJException
   {
      return Arrays.asList(read(inputStream));
   }

   private static final int BUFFER_SIZE = 512;

   private static final Pattern VERSION_PATTERN = Pattern.compile(".*<project.* version=\\\"((?:\\d\\.)*\\d*)\\\".*", Pattern.DOTALL);

   private static final SemVer VERSION_4 = new SemVer(4);
   private static final SemVer VERSION_5 = new SemVer(5);
}
