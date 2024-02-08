package net.sf.mpxj.openplan;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Resource;
import net.sf.mpxj.ResourceType;
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
      System.out.println("Reading resources from " + name);
      DirectoryEntry dir = getDirectoryEntry(m_root, name);
      Map<String, Resource> map = new HashMap<>();

      for (Row row : new TableReader(dir, "RES").read())
      {
         Resource resource = m_file.addResource();
         resource.setName(row.getString("DESCRIPTION"));
         resource.setResourceID(row.getString("RES_ID"));
         resource.setType("Equip-hr".equals(row.getString("UNIT")) ? ResourceType.MATERIAL : ResourceType.WORK); // TODO review
         map.put(resource.getResourceID(), resource);
      }

      for (Resource resource : map.values())
      {
         Resource parentResource = map.get(getParentResourceID(resource.getResourceID()));
         resource.setParentResource(parentResource);
      }

      m_file.getResources().updateStructure();
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
