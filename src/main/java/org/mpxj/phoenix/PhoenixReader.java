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

package org.mpxj.phoenix;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mpxj.MPXJException;
import org.mpxj.ProjectFile;
import org.mpxj.common.CharsetHelper;
import org.mpxj.common.SemVer;
import org.mpxj.reader.AbstractProjectStreamReader;

/**
 * This class creates a new ProjectFile instance by reading a Phoenix Project Manager file.
 */
public final class PhoenixReader extends AbstractProjectStreamReader
{

   /**
    * Retrieve a flag indicating if the Activity Codes should be used
    * to form parent/child Task hierarchy.
    * If true then Task hierarchy will be created and Activity Codes will
    * not be returned in the Project File.
    * If false then Tasks will be a flat hierarchy and Activity Codes will
    * be returned in the Project File.
    *
    * @return true if task hierarchy should be formed
    */
   public boolean getUseActivityCodesForTaskHierarchy()
   {
      return m_useActivityCodesForTaskHierarchy;
   }

   /**
    * Sets a flag indicating if the Activity Codes should be used
    * to form parent/child Task hierarchy.
    *
    * @param useActivityCodesForTaskHierarchy true if task hierarchy should be formed
    */
   public void setUseActivityCodesForTaskHierarchy(boolean useActivityCodesForTaskHierarchy)
   {
      m_useActivityCodesForTaskHierarchy = useActivityCodesForTaskHierarchy;
   }

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
         return (version.before(VERSION_5) ? new Phoenix4Reader(m_useActivityCodesForTaskHierarchy) : new Phoenix5Reader(m_useActivityCodesForTaskHierarchy)).read(bis);
      }

      catch (Exception ex)
      {
         throw new MPXJException(MPXJException.INVALID_FILE, ex);
      }
   }

   private boolean m_useActivityCodesForTaskHierarchy = true;

   private static final int BUFFER_SIZE = 512;

   private static final Pattern VERSION_PATTERN = Pattern.compile(".*<project.* version=\"((?:\\d\\.)*\\d*)\".*", Pattern.DOTALL);

   private static final SemVer VERSION_4 = new SemVer(4);
   private static final SemVer VERSION_5 = new SemVer(5);
}
