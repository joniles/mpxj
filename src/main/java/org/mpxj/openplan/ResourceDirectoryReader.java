/*
 * file:       ResourceDirectoryReader.java
 * author:     Jon Iles
 * date:       2024-02-27
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

package org.mpxj.openplan;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mpxj.Availability;
import org.mpxj.ProjectFile;
import org.mpxj.Resource;
import org.mpxj.UnitOfMeasureContainer;
import org.mpxj.common.HierarchyHelper;
import org.apache.poi.poifs.filesystem.DirectoryEntry;

/**
 * Populate a project with resources.
 */
class ResourceDirectoryReader extends DirectoryReader
{
   /**
    * Constructor.
    *
    * @param root parent directory
    * @param file project file
    */
   public ResourceDirectoryReader(DirectoryEntry root, ProjectFile file)
   {
      m_root = root;
      m_file = file;
   }

   /**
    * Read resources from the named directory.
    *
    * @param name resource directory
    */
   public void read(String name)
   {
      DirectoryEntry dir = getDirectoryEntry(m_root, name);
      Map<String, Resource> map = new HashMap<>();

      List<Row> rows = new TableReader(dir, "RES").read();
      HierarchyHelper.sortHierarchy(rows, r -> r.getString("RES_ID"), r -> OpenPlanHierarchyHelper.getParentID(r.getString("RES_ID")), Comparator.comparing(o -> o.getString("RES_ID")));
      UnitOfMeasureContainer uom = m_file.getUnitsOfMeasure();

      for (Row row : rows)
      {
         String resourceID = row.getString("RES_ID");
         Resource parentResource = map.get(OpenPlanHierarchyHelper.getParentID(resourceID));
         Resource resource;

         if (parentResource == null)
         {
            resource = m_file.addResource();
         }
         else
         {
            resource = parentResource.addResource();
         }

         // CLC_COST: Cost Based on Progress Quantity
         // CLC_PROG: Progress Based on Activity Progress
         // DESCRIPTION: Resource Description
         resource.setName(row.getString("DESCRIPTION"));
         // DIR_ID: Resource Directory Name
         // DIR_UID: Resource Directory Unique ID
         // EFF_FACTOR: Effort Factor
         // EMAIL: Email Address
         resource.setEmailAddress(row.getString("EMAIL"));
         // EMP_ID: Employee ID
         // LASTUPDATE: Last Update Date
         // MSPUNIQUEID: Imported MS Project Unique ID
         // NO_LIST: Suppress In Lists
         // PALLOC_UID: Project Allocation Unique ID
         // POSITION_NUM: Child Position
         // RES_CLASS: Resource Category (L: Labor, N: Material, C: Other Direct Cost: S: Subcontract)
         resource.setType(row.getResourceType("RES_CLASS"));
         // RES_ID: Resource ID
         resource.setResourceID(resourceID);
         // RES_TYPE: Resource Type (null: Normal, C: Consumable, D: Perishable, P: Resource Pool, S: Skill)
         // RES_UID: Resource Unique Identifier
         resource.setGUID(row.getUuid("RES_UID"));
         // ROLLCOST: Rollup for Cost Flag
         // ROLLUP: Rollup for Scheduling Flag
         // SEQUENCE: Update Count
         // SUPPRESS: Suppress Resource Scheduling Flag
         // THRESHOLD: Resource Threshold
         // UNIT: Resource Units
         resource.setUnitOfMeasure(uom.getOrCreateByAbbreviation(row.getString("UNIT")));
         // UNIT_COST: Unit Cost
         // USER_NUM01
         // USER_NUM02
         // USR_ID: Last Update User

         map.put(resource.getResourceID(), resource);
      }

      readAvailability(dir, map);

      /*
      // Resource Structure
      System.out.println("RDS");
      new TableReader(dir, "RDS").read().forEach(System.out::println);
      
      // Resource Cost Escalation
      System.out.println("RSL");
      new TableReader(dir, "RSL").read().forEach(System.out::println);
      
      // Skill Assignment
      System.out.println("SKL");
      new TableReader(dir, "SKL").read().forEach(System.out::println);
      
      // Explorer Folders
      System.out.println("EXF");
      new TableReader(dir, "EXF").read().forEach(System.out::println);
      
      // Project Summary Usage
      System.out.println("PSU");
      new TableReader(dir, "PSU").read().forEach(System.out::println);
      
      // Explorer Folder Items
      System.out.println("EXI");
      new TableReader(dir, "EXI").read().forEach(System.out::println);
      
      // Code Structure Association
      // Not populated for any resources in the sample data
      System.out.println("SCA");
      new TableReader(dir, "SCA").read().forEach(System.out::println);
      */
   }

   /**
    * Read resource availability.
    *
    * @param dir parent directory
    * @param map resource to to Resource instance map
    */
   private void readAvailability(DirectoryEntry dir, Map<String, Resource> map)
   {
      List<Row> rows = new TableReader(dir, "AVL").read();
      for (Row row : rows)
      {
         Resource resource = map.get(row.getString("RES_ID"));
         if (resource == null)
         {
            return;
         }

         // AVL_UID: Availability Unique ID
         // CLH_ID: Resource Calendar Name for Resource Usage
         // CLH_UID: Resource Calendar Unique ID for Resource Usage
         // DIR_ID: Resource Directory Name
         // DIR_UID: Resource Directory Unique ID
         // LASTUPDATE: Last Update Date
         // PALLOC_UID: Project Allocation Unique ID
         // RES_ID: Resource ID
         // RES_LEVEL: Quantity Available This Period
         // Not sure about this - this may be the number of hours per day?
         Double units = Double.valueOf((row.getDouble("RES_LEVEL").doubleValue() / 8.0) * 100.0);
         // RES_UID: Resource Unique ID
         // RFDATE: Period Finish Date
         LocalDateTime finish = row.getDate("RFDATE");
         // RSDATE: Period Start Date
         LocalDateTime start = row.getDate("RSDATE");
         // SEQUENCE: Update Count
         // USR_ID: Last Update User

         resource.getAvailability().add(new Availability(start, finish, units));
      }
   }

   private final ProjectFile m_file;
   private final DirectoryEntry m_root;
}
