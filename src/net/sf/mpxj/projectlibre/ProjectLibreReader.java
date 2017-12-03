/*
 * file:       ProjectLibreReader.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2017
 * date:       24/04/2017
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

package net.sf.mpxj.projectlibre;

import java.io.InputStream;

import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.listener.ProjectListener;
import net.sf.mpxj.mspdi.MSPDIReader;
import net.sf.mpxj.reader.AbstractProjectReader;

/**
 * Reads a ProjectLibre POD file.
 * The POD file contains serialized Java data, followed by an MSPDI file.
 * This class simply locates the MSPDI file and reads it using the normal MSPDI reader class.
 */
public class ProjectLibreReader extends AbstractProjectReader
{
   @Override public void addProjectListener(ProjectListener listener)
   {
      m_reader.addProjectListener(listener);
   }

   @Override public ProjectFile read(InputStream inputStream) throws MPXJException
   {
      ProjectFile file = m_reader.read(new SearchableInputStream(inputStream, "@@@@@@@@@@ProjectLibreSeparator_MSXML@@@@@@@@@@"));
      file.getProjectProperties().setFileApplication("ProjectLibre");
      file.getProjectProperties().setFileType("POD");
      return file;
   }

   private final MSPDIReader m_reader = new MSPDIReader();
}
