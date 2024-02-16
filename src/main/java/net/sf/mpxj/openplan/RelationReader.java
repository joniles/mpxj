package net.sf.mpxj.openplan;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import net.sf.mpxj.Duration;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.RelationType;
import net.sf.mpxj.Task;
import org.apache.poi.poifs.filesystem.DirectoryEntry;

class RelationReader
{
   public RelationReader(DirectoryEntry root, ProjectFile file)
   {
      m_root = root;
      m_file = file;
   }

   public void read(String name)
   {
      Map<UUID, Task> map = m_file.getTasks().stream().collect(Collectors.toMap(t -> t.getGUID(), t -> t));

      for (Row row : new TableReader(m_root, "REL").read())
      {
         Task predecessor = map.get(row.getUuid("PRED_ACT_UID"));
         Task successor = map.get(row.getUuid("SUCC_ACT_UID"));
         if (predecessor != null && successor != null)
         {
            Duration lag = row.getDuration("REL_LAG");
            RelationType type = TYPE_MAP.getOrDefault(row.getString("REL_TYPE"), RelationType.FINISH_START);
            successor.addPredecessor(predecessor, type, lag);
         }

         // REL_TYPE	FS (String)
         // USR_ID	SYSADMIN (String)
         // CLH_UID	00000000-0000-0000-0000-000000000000 (UUID)
         // REL_TF	0 (String)
         // PRED_ACT_UID	6c7e6433-a103-0c59-5b8f-c1d113026300 (UUID)
         // REL_PROBABILITY	0.00 (String)
         // SUCC_ACT_UID	725b5790-ee67-ac5e-5f9e-3323aa996700 (UUID)
         // LASTUPDATE	2002-04-09T11:46 (LocalDateTime)
         // REL_UID	aff3bbb4-7d93-dc7f-5308-66fd5f39ac00 (UUID)
         // PRED_ACT_ID	1.09.01 (String)
         // REL_LAG	0 (String)
         // SUCC_ACT_ID	1.09.02 (String)
         // DIR_UID	d8acbddc-030d-4ca7-4cdc-38c8c7df9300 (UUID)
         // SEQUENCE	2 (Integer)
         // DIR_ID	CLEAN (String)
         // REL_FF	0 (String)
      }
   }

   private final ProjectFile m_file;
   private final DirectoryEntry m_root;

   private static final Map<String, RelationType> TYPE_MAP = new HashMap<>();
   static
   {
      TYPE_MAP.put("SS", RelationType.START_START);
      TYPE_MAP.put("SF", RelationType.START_FINISH);
      TYPE_MAP.put("FS", RelationType.FINISH_START);
      TYPE_MAP.put("FF", RelationType.FINISH_FINISH);
   }
}
