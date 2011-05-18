/*
 * file:       FieldMap14.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2011
 * date:       03/05/2011
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

import net.sf.mpxj.FieldType;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.utility.FieldTypeUtility;

/**
 * MPP14 field map.
 */
class FieldMap14 extends FieldMap
{
   /**
    * Constructor.
    * 
    * @param file parent file
    */
   public FieldMap14(ProjectFile file)
   {
      super(file);
   }

   /**
    * {@inheritDoc}
    */
   @Override protected FieldType getFieldType(int fieldID)
   {
      return FieldTypeUtility.getInstance14Unmapped(fieldID);
   }

   /**
    * {@inheritDoc}
    */
   @Override protected boolean useTypeAsVarDataKey()
   {
      return true;
   }

   /**
    * {@inheritDoc}
    */
   @Override protected Object[][] getDefaultTaskData()
   {
      return null;
   }

   /**
    * {@inheritDoc}
    */
   @Override protected Object[][] getDefaultResourceData()
   {
      return null;
   }

   /**
    * {@inheritDoc}
    */
   @Override protected Object[][] getDefaultAssignmentData()
   {
      return null;
   }
}
