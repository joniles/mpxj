package net.sf.mpxj.openplan;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Resource;
import net.sf.mpxj.Task;

import org.apache.poi.poifs.filesystem.DirectoryEntry;

class AssignmentReader
{

   public AssignmentReader(DirectoryEntry root, ProjectFile file)
   {
      m_root = root;
      m_file = file;
   }

   public void read()
   {
      Map<UUID, Task> taskMap = m_file.getTasks().stream().collect(Collectors.toMap(t -> t.getGUID(), t -> t));
      Map<UUID, Resource> resourceMap = m_file.getResources().stream().collect(Collectors.toMap(r -> r.getGUID(), r -> r));

      for (Row row : new TableReader(m_root, "ASG").read())
      {
         Task task = taskMap.get(row.getUuid("ACT_UID"));
         if (task == null)
         {
            continue;
         }

         Resource resource = resourceMap.get(row.getUuid("RES_UID"));
         if (resource == null)
         {
            continue;
         }

         task.addResourceAssignment(resource);

         // ACT_ID: Activity ID
         // ACT_UID: Activity Unique ID
         // ALT_RES_UID: Alternate Resource Unique ID
         // ASG_UID: Assignment Unique ID
         // DIR_ID: Project Object Directory Name
         // DIR_UID: Project Object Directory UID
         // INTEGRATION_ID: External Unique ID
         // LASTUPDATE: Last Update Date
         // LEV_TYPE: Resource Curve
         // PPC: Physical Percent Complete
         // RDS_ID: Resource Directory ID
         // RDS_UID: Resource Directory Unique ID
         // REMAINING: Remaining Requirement
         // RES_ID: Resource ID
         // RES_LEVEL: Assignment Units
         // RES_OFFSET: Duration between activity start and actual resource start
         // RES_PERIOD: Assignment duration
         // RES_SKL_UID: Resource Skill Unique ID
         // RES_UID: Resource Unique ID
         // SEQUENCE: Update Count
         // SUPPRESS: Suppress Resource Requirement when Scheduling
         // USR_ID: Last Update User
      }
   }

   private final ProjectFile m_file;
   private final DirectoryEntry m_root;
}
