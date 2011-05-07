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
import net.sf.mpxj.MPPAssignmentField;
import net.sf.mpxj.MPPResourceField;
import net.sf.mpxj.MPPResourceField14;
import net.sf.mpxj.MPPTaskField;
import net.sf.mpxj.MPPTaskField14;
import net.sf.mpxj.ProjectFile;

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
      FieldType result;
      int prefix = fieldID & 0xFFFF0000;
      int index = fieldID & 0x0000FFFF;

      switch (prefix)
      {
         case MPPTaskField.TASK_FIELD_BASE :
         {
            result = MPPTaskField14.getInstance(index);
            break;
         }

         case MPPResourceField.RESOURCE_FIELD_BASE :
         {
            result = MPPResourceField14.getInstance(index);
            break;
         }

         case MPPAssignmentField.ASSIGNMENT_FIELD_BASE :
         {
            result = MPPAssignmentField.getInstance(index);
            break;
         }

         default :
         {
            result = null;
            break;
         }
      }

      return result;
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
