/*
 * file:       CompObj.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2002-2003
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

package com.tapsterrock.mpp;

import java.io.InputStream;
import java.io.IOException;

/**
 * This class handles reading the data found in the CompObj block
 * of an MPP file. The bits we can decypher allow us to determine
 * the file format.
 */
class CompObj extends MPPComponent
{
   /**
    * Constructor. Reads and processes the block data.
    */
   public CompObj (InputStream is)
      throws IOException
   {
      int length;
      String string;

      is.skip(28);

      length = readInt(is);
      m_applicationName = new String (readByteArray(is, length), 0, length-1);

      length = readInt(is);
      m_fileFormat = new String (readByteArray(is, length), 0, length-1);

      length = readInt(is);
      m_applicationID = new String (readByteArray(is, length), 0, length-1);
   }

   /**
    * Accessor method to retrieve the application name.
    */
   public String getApplicationName ()
   {
      return (m_applicationName);
   }

   /**
    * Accessor method to retrieve the application ID.
    */
   public String getApplicationID ()
   {
      return (m_applicationID);
   }

   /**
    * Accessor method to retrieve the file format.
    */
   public String getFileFormat ()
   {
      return (m_fileFormat);
   }

   private String m_applicationName;
   private String m_applicationID;
   private String m_fileFormat;
}
