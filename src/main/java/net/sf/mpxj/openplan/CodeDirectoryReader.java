package net.sf.mpxj.openplan;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import net.sf.mpxj.ProjectFile;
import org.apache.poi.poifs.filesystem.DirectoryEntry;

class CodeDirectoryReader extends DirectoryReader
{
   public CodeDirectoryReader(DirectoryEntry root, ProjectFile file)
   {
      m_root = root;
      m_file = file;
   }

   public void read(String name)
   {
      /*
         COD - Directory Entry Record (1 row?)
         CDR - Code Rows
         EXF - Explorer Folders
         EXI - Explorer Items
         ACL = Acess Control
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

      List<Row> valueRows = new TableReader(dir, "CDR").read();

      m_map.put(codeRow.getString("DIR_ID"), new Code(
         codeRow.getString("DIR_ID"),
         codeRow.getString("PROMPT_TEXT"),
         codeRow.getString("DESCRIPTION"),
         valueRows.stream().map(r -> new CodeValue(r.getString("CDR_ID"), r.getString("DESCRIPTION"))).collect(Collectors.toList()))
      );
   }


   public Map<String, Code> getCodes()
   {
      return m_map;
   }

   private final ProjectFile m_file;
   private final DirectoryEntry m_root;
   private final Map<String, Code> m_map = new HashMap<>();
}
