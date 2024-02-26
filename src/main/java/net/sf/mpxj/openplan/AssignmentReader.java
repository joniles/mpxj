package net.sf.mpxj.openplan;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import net.sf.mpxj.Duration;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Resource;
import net.sf.mpxj.ResourceAssignment;
import net.sf.mpxj.Task;

import net.sf.mpxj.TimeUnit;
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

         ResourceAssignment assignment = task.addResourceAssignment(resource);

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

         // Not sure the units values makes sense
         //assignment.setUnits(row.getDouble("RES_LEVEL") * 100.0);

         // RES_OFFSET: Duration between activity start and actual resource start
         // RES_PERIOD: Assignment duration
         // RES_SKL_UID: Resource Skill Unique ID
         // RES_UID: Resource Unique ID
         // SEQUENCE: Update Count
         // SUPPRESS: Suppress Resource Requirement when Scheduling
         // USR_ID: Last Update User

         // The following is completely made up...
         // but it gets us to the point that we get something
         // approximately correct looking at the schedule in MS Project.
         // Need to understand resource units and assignment units as the values don't make sense in isolation.

         // Looks like RES_LEVEL is the number of units per day
         // Remaining = task.getDuration() * res_level
         
         Duration taskWork = assignment.getEffectiveCalendar().getWork(task.getStart(), task.getFinish(), TimeUnit.HOURS);
         assignment.setWork(taskWork);
         assignment.setRemainingWork(taskWork);

         Duration totalWork = task.getWork();
         if (totalWork == null)
         {
            totalWork = Duration.getInstance(0, TimeUnit.HOURS);
         }

         totalWork = Duration.getInstance(totalWork.getDuration() + taskWork.getDuration(), TimeUnit.HOURS);
         task.setWork(totalWork);
         task.setRemainingWork(totalWork);
      }
   }

   private final ProjectFile m_file;
   private final DirectoryEntry m_root;
}
