/*
 * file:       MPPProjectField.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software
 * date:       20/10/2020
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

package net.sf.mpxj.common;

import net.sf.mpxj.DataType;
import net.sf.mpxj.FieldType;
import net.sf.mpxj.FieldTypeClass;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.UserDefinedField;

/**
 * Utility class used to map between the integer values held in MS Project
 * to represent a project field, and the enumerated type used to represent
 * task fields in MPXJ.
 */
public class MPPProjectField
{
   /**
    * Retrieve an instance of the ProjectField class based on the data read from an
    * MS Project file.
    *
    * @param project parent project
    * @param value value from an MS Project file
    * @return ProjectField instance
    */
   public static FieldType getInstance(ProjectFile project, int value)
   {
      if ((value & 0x8000) != 0)
      {
         return project.getUserDefinedFields().getOrCreateProjectField(Integer.valueOf(value), (k) -> {
            int id = (k.intValue() & 0xFFF) + 1;
            String internalName = "ENTERPRISE_CUSTOM_FIELD" + id;
            String externalName = "Enterprise Custom Field " + id;
            return new UserDefinedField(k, internalName, externalName, FieldTypeClass.PROJECT, false, DataType.CUSTOM);
         });
      }

      return null;
   }

   /**
    * Retrieve the ID of a field, as used by MS Project.
    *
    * @param value field instance
    * @return field ID
    */
   public static int getID(FieldType value)
   {
      int result;
      if (value instanceof UserDefinedField)
      {
         result = value.getValue();
      }
      else
      {
         result = -1;
      }
      return result;
   }

   public static final int PROJECT_FIELD_BASE = 0x0B600000;
}
