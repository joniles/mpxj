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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import net.sf.mpxj.ProjectField;

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
    * @param value value from an MS Project file
    * @return ProjectField instance
    */
   public static ProjectField getInstance(int value)
   {
      ProjectField result = null;

      if (value >= 0x8000)
      {
         if ((value & 0x8000) != 0)
         {
            int baseValue = ProjectField.ENTERPRISE_CUSTOM_FIELD1.getValue();
            int id = baseValue + (value & 0xFFF);
            result = ProjectField.getInstance(id);
         }
      }

      return (result);
   }

   /**
    * Retrieve the ID of a field, as used by MS Project.
    *
    * @param value field instance
    * @return field ID
    */
   public static int getID(ProjectField value)
   {
      int result;
      
      if (ENTERPRISE_CUSTOM_FIELDS.contains(value))
      {
         int baseValue = ProjectField.ENTERPRISE_CUSTOM_FIELD1.getValue();
         int id = value.getValue() - baseValue;
         result = 0x8000 + id;
      }
      else
      {
         result = -1;
      }
      
      return result;
   }

   public static final int PROJECT_FIELD_BASE = 0x0B600000;
   
   private static final Set<ProjectField> ENTERPRISE_CUSTOM_FIELDS = new HashSet<>(Arrays.asList(ProjectFieldLists.ENTERPRISE_CUSTOM_FIELD));
}
