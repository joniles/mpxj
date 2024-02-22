package net.sf.mpxj.openplan;
import java.io.FileNotFoundException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Resource;
import net.sf.mpxj.UnitOfMeasureContainer;
import net.sf.mpxj.common.HierarchyHelper;
import org.apache.poi.poifs.filesystem.DirectoryEntry;

class ResourceReader
{
   public ResourceReader(DirectoryEntry root, ProjectFile file)
   {
      m_root = root;
      m_file = file;
   }

   public void read(String name)
   {
      DirectoryEntry dir = getDirectoryEntry(m_root, name);
      Map<String, Resource> map = new HashMap<>();

      List<Row> rows = new TableReader(dir, "RES").read();
      HierarchyHelper.sortHierarchy(rows, r -> r.getString("RES_ID"), r -> getParentResourceID(r.getString("RES_ID")), Comparator.comparing(o -> o.getString("RES_ID")));
      UnitOfMeasureContainer uom = m_file.getUnitsOfMeasure();

      for (Row row : rows)
      {
         String resourceID = row.getString("RES_ID");
         Resource parentResource = map.get(getParentResourceID(resourceID));
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
         // LASTUPDATE: Last Update
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

/*
      System.out.println("RES");
      new TableReader(dir, "RES").read().forEach(System.out::println);

      System.out.println("RDS");
      new TableReader(dir, "RDS").read().forEach(System.out::println);

      System.out.println("RSL");
      new TableReader(dir, "RSL").read().forEach(System.out::println);

      System.out.println("SKL");
      new TableReader(dir, "SKL").read().forEach(System.out::println);

      System.out.println("SCA");
      new TableReader(dir, "SCA").read().forEach(System.out::println);

      System.out.println("EXF");
      new TableReader(dir, "EXF").read().forEach(System.out::println);

      System.out.println("PSU");
      new TableReader(dir, "PSU").read().forEach(System.out::println);

      System.out.println("EXI");
      new TableReader(dir, "EXI").read().forEach(System.out::println);

      System.out.println("AVL");
      new TableReader(dir, "AVL").read().forEach(System.out::println);
 */
   }

   private String getParentResourceID(String resourceID)
   {
      int index = resourceID.lastIndexOf('.');
      if (index == -1)
      {
         return null;
      }
      return resourceID.substring(0,index);
   }

   // TODO: helper class
   private DirectoryEntry getDirectoryEntry(DirectoryEntry root, String name)
   {
      try
      {
         return (DirectoryEntry) root.getEntry(name);
      }

      catch (FileNotFoundException e)
      {
         throw new OpenPlanException(e);
      }
   }

   private final ProjectFile m_file;
   private final DirectoryEntry m_root;
}
