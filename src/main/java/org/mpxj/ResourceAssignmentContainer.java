/*
 * file:       ResourceAssignmentContainer.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2002-2015
 * date:       20/04/2015
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

package org.mpxj;

import java.util.List;
import java.util.Set;

import org.mpxj.common.PopulatedFields;

/**
 * Manages the collection of resource assignments belonging to a project.
 */
public class ResourceAssignmentContainer extends ProjectEntityContainer<ResourceAssignment>
{
   /**
    * Constructor.
    *
    * @param projectFile parent project
    */
   public ResourceAssignmentContainer(ProjectFile projectFile)
   {
      super(projectFile);
      m_projectFile = projectFile;
   }

   @Override public void removed(ResourceAssignment assignment)
   {
      super.removed(assignment);
      assignment.getTask().removeResourceAssignment(assignment);
      Resource resource = assignment.getResource();
      if (resource != null)
      {
         resource.removeResourceAssignment(assignment);
      }
   }

   /**
    * Retrieve the set of populated fields for this project.
    *
    * @return set of populated fields
    */
   public Set<FieldType> getPopulatedFields()
   {
      return new PopulatedFields<>(m_projectFile, AssignmentField.class, m_projectFile.getUserDefinedFields().getAssignmentFields(), this).getPopulatedFields();
   }

   /**
    * Retrieve a list of resource assignment custom fields.
    *
    * @return resource assignment custom fields
    */
   public List<CustomField> getCustomFields()
   {
      return m_projectFile.getCustomFields().getCustomFieldsByFieldTypeClass(FieldTypeClass.ASSIGNMENT);
   }

   /**
    * Retrieve the type of a field by its alias.
    *
    * @param alias field alias
    * @return FieldType instance
    */
   public FieldType getFieldTypeByAlias(String alias)
   {
      return m_projectFile.getCustomFields().getFieldTypeByAlias(FieldTypeClass.ASSIGNMENT, alias);
   }

   private final ProjectFile m_projectFile;
}
