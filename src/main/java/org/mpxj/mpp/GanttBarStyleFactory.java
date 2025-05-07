/*
 * file:       GantBarStyleFactory.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2010
 * date:       19/04/2010
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

import org.mpxj.ProjectFile;

/**
 * Interface used to read Gantt bar styles from MPP files.
 */
public interface GanttBarStyleFactory
{
   /**
    * Reads the default set of Gantt bar styles.
    *
    * @param file parent file
    * @param props props structure containing the Gantt chart style data
    * @return array of styles
    */
   GanttBarStyle[] processDefaultStyles(ProjectFile file, Props props);

   /**
    * Reads the set of exception bar styles from MPP files.
    *
    * @param file parent file
    * @param props props structure containing the Gantt chart style data
    * @return array of styles
    */
   GanttBarStyleException[] processExceptionStyles(ProjectFile file, Props props);
}
