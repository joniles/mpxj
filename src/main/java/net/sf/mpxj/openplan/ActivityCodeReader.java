
package net.sf.mpxj.openplan;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.mpxj.ActivityCode;
import net.sf.mpxj.ActivityCodeValue;
import net.sf.mpxj.ProjectFile;
import org.apache.poi.poifs.filesystem.DirectoryEntry;

class ActivityCodeReader
{
   public ActivityCodeReader(DirectoryEntry root, ProjectFile file)
   {
      m_root = root;
      m_file = file;
   }

   public void read(Map<String, Code> map)
   {
      List<Row> rows = new TableReader(m_root, "SCA").read();
      for (Row row : rows)
      {
         if (!"ACT".equals(row.getString("TABLE_TYPE")))
         {
            continue;
         }

         Code code = map.get(row.getString("COD_ID"));
         if (code == null)
         {
            continue;
         }

         ActivityCode ac = new ActivityCode.Builder(m_file).name(code.getPromptText()).build();
         Map<String, ActivityCodeValue> valueMap = new HashMap<>();

         int sequence = 1;
         for (CodeValue value : code.getValues())
         {
            ActivityCodeValue acv = new ActivityCodeValue.Builder(m_file).type(ac).name(value.getID()).description(value.getDescription()).sequenceNumber(Integer.valueOf(sequence++)).build();

            ac.getValues().add(acv);
            valueMap.put(value.getUniqueID(), acv);
         }

         m_codeMap.put(row.getString("SCA_ID"), valueMap);

         m_file.getActivityCodes().add(ac);
      }
   }

   public Map<String, Map<String, ActivityCodeValue>> getCodeMap()
   {
      return m_codeMap;
   }

   private final ProjectFile m_file;
   private final DirectoryEntry m_root;
   private final Map<String, Map<String, ActivityCodeValue>> m_codeMap = new HashMap<>();
}