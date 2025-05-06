/*
 * file:       AssignmentReader.java
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

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.mpxj.Duration;
import org.mpxj.ProjectFile;
import org.mpxj.Resource;
import org.mpxj.ResourceAssignment;
import org.mpxj.Task;

import org.mpxj.TimeUnit;
import org.apache.poi.poifs.filesystem.DirectoryEntry;

/**
 * Populate the parent project with resource assignments.
 */
class AssignmentReader
{

   /**
    * Constructor.
    *
    * @param root parent directory
    * @param file project file
    */
   public AssignmentReader(DirectoryEntry root, ProjectFile file)
   {
      m_root = root;
      m_file = file;
   }

   /**
    * Read assignments and populate the parent project.
    */
   public void read()
   {
      Map<UUID, Task> taskMap = m_file.getTasks().stream().collect(Collectors.toMap(Task::getGUID, t -> t));
      Map<UUID, Resource> resourceMap = m_file.getResources().stream().collect(Collectors.toMap(Resource::getGUID, r -> r));

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
         // RES_OFFSET: Duration between activity start and actual resource start
         // RES_PERIOD: Assignment duration
         // RES_SKL_UID: Resource Skill Unique ID
         // RES_UID: Resource Unique ID
         // SEQUENCE: Update Count
         // SUPPRESS: Suppress Resource Requirement when Scheduling
         // USR_ID: Last Update User

         // Best guess: RES_LEVEL represents hours per day worked by the resource
         Double resLevel = row.getDouble("RES_LEVEL");
         // So the units is RES_LEVEL divided by  the number of hours per day (from which calendar? we'll use a constant for now)
         double units = resLevel.doubleValue() / 8.0; // hours per day
         assignment.setUnits(Double.valueOf(units * 100.0));

         // What's the maximum possible work for the task, give its duration
         Duration taskWork = assignment.getEffectiveCalendar().getWork(task.getStart(), task.getFinish(), TimeUnit.HOURS);
         // Multiply by our units figure to get the actual assignment work
         Duration assignmentWork = Duration.getInstance(taskWork.getDuration() * units, TimeUnit.HOURS);
         assignment.setWork(assignmentWork);

         // In theory, REMAINING contains the amount of remaining work for the assignment?
         // Haven't been able to verify behaviour with test data, so we'll leave everything with 0% progress.
         assignment.setRemainingWork(assignmentWork);

         // Roll up to the parent task
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
