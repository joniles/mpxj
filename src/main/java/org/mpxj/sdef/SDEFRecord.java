/*
 * file:       SDEFRecord.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2019
 * date:       01/07/2019
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

package org.mpxj.sdef;

import org.mpxj.ProjectFile;

/**
 * Interface implemented by SDEF records.
 */
interface SDEFRecord
{
   /**
    * Extract fields from the record.
    *
    * @param file parent project file
    * @param line record data
    * @param ignoreErrors true if parse errors are ignored
    */
   void read(ProjectFile file, String line, boolean ignoreErrors);

   /**
    * Process the extracted fields.
    *
    * @param context current context
    */
   void process(Context context);
}
