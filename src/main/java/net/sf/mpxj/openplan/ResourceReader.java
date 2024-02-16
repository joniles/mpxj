package net.sf.mpxj.openplan;
import java.io.FileNotFoundException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Resource;
import net.sf.mpxj.ResourceType;
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

         resource.setName(row.getString("DESCRIPTION"));
         resource.setResourceID(resourceID);
         resource.setGUID(row.getUuid("RES_UID"));
         resource.setType("Equip-hr".equals(row.getString("UNIT")) ? ResourceType.MATERIAL : ResourceType.WORK); // TODO review
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
