package net.sf.mpxj.openplan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.poifs.filesystem.DirectoryEntry;

public class DependenciesReader extends AbstractReader
{
   public DependenciesReader(DirectoryEntry dir)
   {
      super(dir, "Dependencies");
   }

   public DependenciesReader read()
   {
      int count = getInt();
      for (int index = 0; index < count; index++)
      {
         String name = getString();
         String type = getString();
         getByte();
         Test x = TYPE_MAP.get(type);
         if (x != null)
         {
            x.add(this, name + "_" + type);
         }
      }

      return this;
   }

   public List<String> getCodes()
   {
      return m_codes;
   }

   public List<String> getResources()
   {
      return m_resources;
   }

   public List<String> getCalendars()
   {
      return m_calendars;
   }

   private final List<String> m_calendars = new ArrayList<>();
   private final List<String> m_resources = new ArrayList<>();
   private final List<String> m_codes = new ArrayList<>();

   private interface Test
   {
      public void add(DependenciesReader reader, String name);
   }

   private static final Map<String, Test> TYPE_MAP = new HashMap<>();
   static
   {
      TYPE_MAP.put("CLD", (d, c) -> d.m_calendars.add(c));
      TYPE_MAP.put("RDS", (d, c) -> d.m_resources.add(c));
      TYPE_MAP.put("COD", (d, c) -> d.m_codes.add(c));
   }
}
