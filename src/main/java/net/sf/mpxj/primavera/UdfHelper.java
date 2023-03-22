/*
 * file:       UdfHelper.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2023
 * date:       07/03/2023
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

package net.sf.mpxj.primavera;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import net.sf.mpxj.CustomField;
import net.sf.mpxj.FieldType;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.common.FieldLists;

/**
 * Common methods for working with user defined fields in P6 schedules.
 */
final class UdfHelper
{
   /**
    * Retrieve a set of FieldType instances representing all the fields
    * in a schedule which should be treated as user defined fields when
    * exported to P6.
    *
    * @param file schedule being exported
    * @return set of FieldType instances
    */
   public static Set<FieldType> getUserDefinedFieldsSet(ProjectFile file)
   {
      // All custom fields with configuration
      Set<FieldType> set = file.getCustomFields().stream().map(CustomField::getFieldType).filter(Objects::nonNull).collect(Collectors.toSet());

      // All user defined fields
      set.addAll(file.getUserDefinedFields());

      // All custom fields with values
      set.addAll(file.getPopulatedFields().stream().filter(FieldLists.CUSTOM_FIELDS::contains).collect(Collectors.toSet()));

      // Remove unknown fields
      set.removeIf(f -> net.sf.mpxj.common.FieldTypeHelper.getFieldID(f) == -1);

      return set;
   }
}
