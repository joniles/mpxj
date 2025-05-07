/*
 * file:       CompObj.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2002-2003
 * date:       07/01/2003
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mpxj.common.InputStreamHelper;

/**
 * This class handles reading the data found in the CompObj block
 * of an MPP file. The bits we can decipher allow us to determine
 * the file format.
 */
final class CompObj extends MPPComponent
{
   /**
    * Constructor. Reads and processes the block data.
    *
    * @param is input stream
    * @throws IOException on read failure
    */
   CompObj(InputStream is)
      throws IOException
   {
      int length;

      InputStreamHelper.skip(is, 28);

      length = readInt(is);
      m_applicationName = new String(readByteArray(is, length), 0, length - 1);
      Matcher matcher = PATTERN.matcher(m_applicationName);
      if (matcher.matches())
      {
         m_applicationVersion = Integer.valueOf(matcher.group(1));
      }

      if (m_applicationName.equals("Microsoft Project 4.0"))
      {
         m_fileFormat = "MSProject.MPP4";
         m_applicationID = "MSProject.Project.4";
      }
      else
      {
         length = readInt(is);
         if (length > 0)
         {
            m_fileFormat = new String(readByteArray(is, length), 0, length - 1);
            length = readInt(is);
            if (length > 0)
            {
               m_applicationID = new String(readByteArray(is, length), 0, length - 1);
            }
         }
      }
   }

   /**
    * Accessor method to retrieve the application name.
    *
    * @return Name of the application
    */
   public String getApplicationName()
   {
      return (m_applicationName);
   }

   /**
    * Accessor method to retrieve the application version.
    *
    * @return application version
    */
   public Integer getApplicationVersion()
   {
      return (m_applicationVersion);
   }

   /**
    * Accessor method to retrieve the application ID.
    *
    * @return Application ID
    */
   public String getApplicationID()
   {
      return (m_applicationID);
   }

   /**
    * Accessor method to retrieve the file format.
    *
    * @return File format
    */
   public String getFileFormat()
   {
      return (m_fileFormat);
   }

   @Override public String toString()
   {
      return ("[CompObj applicationName=" + m_applicationName + " applicationID=" + m_applicationID + " fileFormat=" + m_fileFormat);
   }

   /**
    * Application name.
    */
   private final String m_applicationName;

   /**
    * Application version.
    */
   private Integer m_applicationVersion;

   /**
    * Application identifier.
    */
   private String m_applicationID;

   /**
    * File format.
    */
   private String m_fileFormat;

   private static final Pattern PATTERN = Pattern.compile("Microsoft.Project.(\\d+)\\.0");
}
