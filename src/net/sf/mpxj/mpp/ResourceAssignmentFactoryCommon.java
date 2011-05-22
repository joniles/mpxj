/*
 * file:       ResourceAssignmentFactoryCommon.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2010
 * date:       21/03/2010
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

package net.sf.mpxj.mpp;

import java.util.List;
import java.util.Set;

import net.sf.mpxj.AssignmentField;
import net.sf.mpxj.Duration;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Resource;
import net.sf.mpxj.ResourceAssignment;
import net.sf.mpxj.ResourceType;
import net.sf.mpxj.SplitTaskFactory;
import net.sf.mpxj.Task;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.TimephasedResourceAssignment;
import net.sf.mpxj.TimephasedResourceAssignmentNormaliser;
import net.sf.mpxj.WorkContour;
import net.sf.mpxj.utility.NumberUtility;

/**
 * Common implementation detail to extract resource assignment data from 
 * MPP9 and MPP12 files.
 */
public class ResourceAssignmentFactoryCommon implements ResourceAssignmentFactory
{
   /**
    * {@inheritDoc}
    */
   public void process(ProjectFile file, FieldMap fieldMap, boolean useRawTimephasedData, VarMeta assnVarMeta, Var2Data assnVarData, FixedMeta assnFixedMeta, FixedData assnFixedData)
   {
      Set<Integer> set = assnVarMeta.getUniqueIdentifierSet();
      int count = assnFixedMeta.getItemCount();
      TimephasedResourceAssignmentFactory timephasedFactory = new TimephasedResourceAssignmentFactory();
      SplitTaskFactory splitFactory = new SplitTaskFactory();
      TimephasedResourceAssignmentNormaliser normaliser = new MPPTimephasedResourceAssignmentNormaliser();

      //System.out.println(assnFixedMeta);
      //System.out.println(assnFixedData);

      for (int loop = 0; loop < count; loop++)
      {
         byte[] meta = assnFixedMeta.getByteArrayValue(loop);
         if (meta[0] != 0)
         {
            continue;
         }

         int offset = MPPUtility.getInt(meta, 4);
         byte[] data = assnFixedData.getByteArrayValue(assnFixedData.getIndexFromOffset(offset));
         if (data == null || data.length <= fieldMap.getMaxFixedDataOffset(0))
         {
            continue;
         }

         int id = MPPUtility.getInt(data, fieldMap.getFixedDataOffset(AssignmentField.UNIQUE_ID));
         final Integer varDataId = Integer.valueOf(id);
         if (set.contains(varDataId) == false)
         {
            continue;
         }

         ResourceAssignment assignment = new ResourceAssignment(file);
         assignment.setTimephasedNormaliser(normaliser);
         assignment.disableEvents();
         fieldMap.populateContainer(assignment, varDataId, new byte[][]
         {
            data
         }, assnVarData);
         assignment.enableEvents();

         //
         // Post processing
         //
         Task task = file.getTaskByUniqueID(assignment.getTaskUniqueID());
         if (task != null)
         {
            task.addResourceAssignment(assignment);

            Resource resource = file.getResourceByUniqueID(assignment.getResourceUniqueID());

            ProjectCalendar calendar = null;
            if (resource != null)
            {
               calendar = resource.getResourceCalendar();
            }

            if (calendar == null || task.getIgnoreResourceCalendar())
            {
               calendar = task.getCalendar();
            }

            if (calendar == null)
            {
               calendar = file.getCalendar();
            }

            byte[] completeWork = assnVarData.getByteArray(varDataId, fieldMap.getVarDataKey(AssignmentField.COMPLETE_WORK_DATA));
            byte[] plannedWork = assnVarData.getByteArray(varDataId, fieldMap.getVarDataKey(AssignmentField.PLANNED_WORK_DATA));

            List<TimephasedResourceAssignment> timephasedComplete = timephasedFactory.getCompleteWork(calendar, assignment.getStart(), completeWork);
            List<TimephasedResourceAssignment> timephasedPlanned = timephasedFactory.getPlannedWork(calendar, assignment.getStart(), assignment.getUnits().doubleValue(), plannedWork, timephasedComplete);
            //System.out.println(timephasedComplete);
            //System.out.println(timephasedPlanned);
            assignment.setActualStart(timephasedComplete.isEmpty() ? null : assignment.getStart());
            assignment.setActualFinish((assignment.getRemainingWork().getDuration() == 0 && resource != null) ? assignment.getFinish() : null);

            if (task.getSplits() != null && task.getSplits().isEmpty())
            {
               splitFactory.processSplitData(task, timephasedComplete, timephasedPlanned);
            }

            createTimephasedData(file, assignment, timephasedPlanned, timephasedComplete);

            assignment.setTimephasedPlanned(timephasedPlanned, !useRawTimephasedData);
            assignment.setTimephasedComplete(timephasedComplete, !useRawTimephasedData);

            if (plannedWork != null)
            {
               if (timephasedFactory.getWorkModified(timephasedPlanned))
               {
                  assignment.setWorkContour(WorkContour.CONTOURED);
               }
               else
               {
                  if (plannedWork.length >= 30)
                  {
                     assignment.setWorkContour(WorkContour.getInstance(MPPUtility.getShort(plannedWork, 28)));
                  }
                  else
                  {
                     assignment.setWorkContour(WorkContour.FLAT);
                  }
               }
               //System.out.println(assignment.getWorkContour());
               //System.out.println(assignment);
            }
         }
      }
   }

   /**
    * Method used to create missing timephased data.
    * 
    * @param file project file
    * @param assignment resource assignment
    * @param timephasedPlanned planned timephased data
    * @param timephasedComplete complete timephased data
    */
   private void createTimephasedData(ProjectFile file, ResourceAssignment assignment, List<TimephasedResourceAssignment> timephasedPlanned, List<TimephasedResourceAssignment> timephasedComplete)
   {
      if (timephasedPlanned.isEmpty() && timephasedComplete.isEmpty())
      {
         Duration workPerDay;

         if (assignment.getResource() == null || assignment.getResource().getType() == ResourceType.WORK)
         {
            workPerDay = TimephasedResourceAssignmentNormaliser.DEFAULT_NORMALIZER_WORK_PER_DAY;
            int units = NumberUtility.getInt(assignment.getUnits());
            if (units != 100)
            {
               workPerDay = Duration.getInstance((workPerDay.getDuration() * units) / 100.0, workPerDay.getUnits());
            }
         }
         else
         {
            if (assignment.getVariableRateUnits() == null)
            {
               Duration workingDays = assignment.getCalendar().getWork(assignment.getStart(), assignment.getFinish(), TimeUnit.DAYS);
               double units = NumberUtility.getDouble(assignment.getUnits());
               double unitsPerDayAsMinutes = (units * 60) / (workingDays.getDuration() * 100);
               workPerDay = Duration.getInstance(unitsPerDayAsMinutes, TimeUnit.MINUTES);
            }
            else
            {
               double unitsPerHour = NumberUtility.getDouble(assignment.getUnits());
               workPerDay = TimephasedResourceAssignmentNormaliser.DEFAULT_NORMALIZER_WORK_PER_DAY;
               Duration hoursPerDay = workPerDay.convertUnits(TimeUnit.HOURS, file.getProjectHeader());
               double unitsPerDayAsHours = (unitsPerHour * hoursPerDay.getDuration()) / 100;
               double unitsPerDayAsMinutes = unitsPerDayAsHours * 60;
               workPerDay = Duration.getInstance(unitsPerDayAsMinutes, TimeUnit.MINUTES);
            }
         }

         TimephasedResourceAssignment tra = new TimephasedResourceAssignment();
         tra.setStart(assignment.getStart());
         tra.setWorkPerDay(workPerDay);
         tra.setModified(false);
         tra.setFinish(assignment.getFinish());
         tra.setTotalWork(assignment.getWork().convertUnits(TimeUnit.MINUTES, file.getProjectHeader()));
         timephasedPlanned.add(tra);
      }
   }
}
