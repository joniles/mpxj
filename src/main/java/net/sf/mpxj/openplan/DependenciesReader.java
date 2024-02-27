/*
 * file:       DependencyReader.java
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

         String path = name + "_" + type;

         //         if (!path.endsWith("_VUE"))
         //         {
         //            System.out.println(path);
         //         }

         Test x = TYPE_MAP.get(type);
         if (x != null)
         {
            x.add(this, path);
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
