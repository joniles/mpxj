/*
 * file:       ResourceContainer.java
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.mpxj.common.NumberHelper;
import org.mpxj.common.PopulatedFields;

/**
 * Manages the collection of resources belonging to a project.
 */
public class ResourceContainer extends ProjectEntityWithIDContainer<Resource> implements ChildResourceContainer
{
   /**
    * Constructor.
    *
    * @param context parent context
    */
   public ResourceContainer(ProjectContext context)
   {
      super(context);
      m_context = context;
   }

   @Override public List<Resource> getChildResources()
   {
      return m_childResources;
   }

   @Override public Resource addResource()
   {
      return add();
   }

   @Override public void removed(Resource resource)
   {
      //
      // Remove the resource from the file and its parent resource
      //
      super.removed(resource);
      m_idMap.remove(resource.getID());

      Resource parentResource = resource.getParentResource();
      if (parentResource != null)
      {
         parentResource.removeChildResource(resource);
      }
      else
      {
         m_childResources.remove(resource);
      }

      //
      // Remove all resource assignments
      //
      m_context.getProjects().forEach(p -> removeAssignments(p, resource));

      ProjectCalendar calendar = resource.getCalendar();
      if (calendar != null)
      {
         calendar.remove();
      }
   }

   private void removeAssignments(ProjectFile project, Resource resource)
   {
      Iterator<ResourceAssignment> iter = project.getResourceAssignments().iterator();
      Integer resourceUniqueID = resource.getUniqueID();
      while (iter.hasNext())
      {
         ResourceAssignment assignment = iter.next();
         if (NumberHelper.equals(assignment.getResourceUniqueID(), resourceUniqueID))
         {
            iter.remove();
         }
      }
   }

   /**
    * Add a resource to the project.
    *
    * @return new resource instance
    */
   public Resource add()
   {
      Resource resource = new Resource(m_context);
      add(resource);
      m_childResources.add(resource);
      return resource;
   }

   /**
    * Rebuild the hierarchical resource structure based on the Parent Resource ID.
    * Note that if a resource has a Parent Resource ID which we can't find, the
    * resource will be left at the top level by default.
    */
   void updateStructure()
   {
      if (size() > 1)
      {
         m_childResources.clear();
         this.forEach(r -> r.getChildResources().clear());
         this.forEach(r -> {
            Resource parent = r.getParentResource();
            if (parent == null)
            {
               m_childResources.add(r);
            }
            else
            {
               parent.addChildResource(r);
            }
         });
      }
   }

   /**
    * Retrieve the set of populated fields for this project.
    *
    * @return set of populated fields
    */
   public Set<FieldType> getPopulatedFields()
   {
      return new PopulatedFields<>(null, ResourceField.class, m_context.getUserDefinedFields().getResourceFields(), this).getPopulatedFields();
   }

   /**
    * Retrieve a list of resource custom fields.
    *
    * @return resource custom fields
    */
   public List<CustomField> getCustomFields()
   {
      return m_context.getCustomFields().getCustomFieldsByFieldTypeClass(FieldTypeClass.RESOURCE);
   }

   /**
    * Retrieve the type of a field by its alias.
    *
    * @param alias field alias
    * @return FieldType instance
    */
   public FieldType getFieldTypeByAlias(String alias)
   {
      return m_context.getCustomFields().getFieldTypeByAlias(FieldTypeClass.RESOURCE, alias);
   }

   private final ProjectContext m_context;
   private final List<Resource> m_childResources = new ArrayList<>();
}
