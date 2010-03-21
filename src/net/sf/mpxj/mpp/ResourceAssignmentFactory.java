/*
 * file:       ResourceAssignmentFactory.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2010
 * date:       21/03/2010
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

package net.sf.mpxj.mpp;

import net.sf.mpxj.ProjectFile;

/**
 * Interface implemented by classes which read resource assignments from
 * MPP files.
 */
public interface ResourceAssignmentFactory
{
   /**
    * Reads resource assignments from an MPP file.
    * 
    * @param file parent MPP file
    * @param useRawTimephasedData flag indicating if raw timephased data is preserved
    * @param assnVarMeta assignment var meta
    * @param assnVarData assignment var data
    * @param assnFixedMeta assignment fixed meta
    * @param assnFixedData assignment fixed data
    */
   public void process(ProjectFile file, boolean useRawTimephasedData, VarMeta assnVarMeta, Var2Data assnVarData, FixedMeta assnFixedMeta, FixedData assnFixedData);

}
