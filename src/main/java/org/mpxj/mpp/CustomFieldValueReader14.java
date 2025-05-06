/*
 * file:       CustomFieldValueReader14.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2015
 * date:       28/04/2015
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

import java.util.Map;
import java.util.UUID;

import org.mpxj.FieldType;
import org.mpxj.ProjectFile;
import org.mpxj.common.NumberHelper;

/**
 * MPP14 custom field value reader.
 */
class CustomFieldValueReader14 extends CustomFieldValueReader
{
   /**
    * Constructor.
    *
    * @param file project file
    * @param lookupTableMap map of GUIDs to lookup tables
    * @param outlineCodeVarMeta raw mpp data
    * @param outlineCodeVarData raw mpp data
    * @param outlineCodeFixedData raw mpp data
    * @param outlineCodeFixedData2 raw mpp data
    */
   public CustomFieldValueReader14(ProjectFile file, Map<UUID, FieldType> lookupTableMap, VarMeta outlineCodeVarMeta, Var2Data outlineCodeVarData, FixedData outlineCodeFixedData, FixedData outlineCodeFixedData2)
   {
      super(file, lookupTableMap, outlineCodeVarMeta, outlineCodeVarData, outlineCodeFixedData, outlineCodeFixedData2);

      if (NumberHelper.getInt(m_properties.getApplicationVersion()) > ApplicationVersion.PROJECT_2010)
      {
         m_typeOffset = 16;
         m_fieldOffset = 18;
         m_parentOffset = 10;
      }
      else
      {
         m_fieldOffset = 16;
         m_typeOffset = 32;
         m_parentOffset = 8;
      }
   }
}
