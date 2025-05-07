/*
 * file:       CodeDirectoryReader.java
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.poifs.filesystem.DirectoryEntry;

/**
 * Read a set of code definitions from a code directory
 * and create a map between the code ID and its definition.
 */
class CodeDirectoryReader extends DirectoryReader
{
   /**
    * Constructor.
    *
    * @param root parent directory
    */
   public CodeDirectoryReader(DirectoryEntry root)
   {
      m_root = root;
   }

   /**
    * Retrieve the code definitions from the named directory.
    *
    * @param name code directory name
    */
   public void read(String name)
   {
      /*
         COD - Directory Entry Record (1 row?)
         CDR - Code Rows
         EXF - Explorer Folders
         EXI - Explorer Items
         ACL = Access Control
         Dependencies
         Key
       */
      DirectoryEntry dir = getDirectoryEntry(m_root, name);

      List<Row> codeRows = new TableReader(dir, "COD").read();
      if (codeRows.isEmpty())
      {
         return;
      }

      Row codeRow = codeRows.get(0);
      if (codeRows.size() != 1)
      {
         throw new OpenPlanException("Expecting 1 code row, found " + codeRows.size());
      }

      List<Row> valueRows = new TableReader(dir, "CDR").read();

      m_map.put(codeRow.getString("DIR_ID"), new Code(codeRow.getString("DIR_ID"), codeRow.getString("PROMPT_TEXT"), codeRow.getString("DESCRIPTION"), valueRows.stream().map(r -> new CodeValue(r.getString("CDR_ID"), r.getString("CDR_UID"), r.getString("DESCRIPTION"))).collect(Collectors.toList())));
   }

   /**
    * Retrieve the mapping between a code ID and its definition.
    *
    * @return code map
    */
   public Map<String, Code> getCodes()
   {
      return m_map;
   }

   private final DirectoryEntry m_root;
   private final Map<String, Code> m_map = new HashMap<>();
}
