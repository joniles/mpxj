/*
 * file:       ResourceAssignmentFactory.java
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

package org.mpxj.mpp;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.mpxj.AssignmentField;
import org.mpxj.Duration;
import org.mpxj.FieldTypeClass;
import org.mpxj.ProjectCalendar;
import org.mpxj.ProjectFile;
import org.mpxj.Resource;
import org.mpxj.ResourceAssignment;
import org.mpxj.ResourceType;
import org.mpxj.Task;
import org.mpxj.TimeUnit;
import org.mpxj.TimephasedWork;
import org.mpxj.WorkContour;
import org.mpxj.common.ByteArrayHelper;
import org.mpxj.common.DefaultTimephasedWorkContainer;
import org.mpxj.common.MicrosoftProjectConstants;
import org.mpxj.common.NumberHelper;
import org.mpxj.common.SplitTaskFactory;

/**
 * Common implementation detail to extract resource assignment data from
 * MPP9, MPP12, and MPP14 files.
 */
public class ResourceAssignmentFactory
{
   /**
    * Main entry point when called to process assignment data.
    *
    * @param file parent project file
    * @param fieldMap assignment field map
    * @param enterpriseCustomFieldMap enterprise custom field map
    * @param useRawTimephasedData use raw timephased data flag
    * @param assnVarMeta var meta
    * @param assnVarData var data
    * @param assnFixedMeta fixed meta
    * @param assnFixedData fixed data
    * @param assnFixedData2 fixed data
    * @param count expected number of assignments
    */
   public void process(ProjectFile file, FieldMap fieldMap, FieldMap enterpriseCustomFieldMap, boolean useRawTimephasedData, VarMeta assnVarMeta, Var2Data assnVarData, FixedMeta assnFixedMeta, FixedData assnFixedData, FixedData assnFixedData2, int count)
   {
      Set<Integer> set = assnVarMeta.getUniqueIdentifierSet();
      TimephasedDataFactory timephasedFactory = new TimephasedDataFactory();
      HyperlinkReader hyperlinkReader = new HyperlinkReader();

      //      System.out.println(assnFixedMeta);
      //      System.out.println(assnFixedData);
      //      System.out.println(assnFixedData2);
      //      System.out.println(assnVarMeta.toString(fieldMap));
      //      System.out.println(assnVarData);

      MppBitFlag[] metaDataBitFlags;
      if (NumberHelper.getInt(file.getProjectProperties().getMppFileType()) == 14)
      {
         if (NumberHelper.getInt(file.getProjectProperties().getApplicationVersion()) > ApplicationVersion.PROJECT_2010)
         {
            metaDataBitFlags = PROJECT_2013_ASSIGNMENT_META_DATA_BIT_FLAGS;
         }
         else
         {
            metaDataBitFlags = PROJECT_2010_ASSIGNMENT_META_DATA_BIT_FLAGS;
         }
      }
      else
      {
         metaDataBitFlags = ASSIGNMENT_META_DATA_BIT_FLAGS;
      }

      Set<Task> processedSplits = new HashSet<>();

      for (int loop = 0; loop < count; loop++)
      {
         byte[] meta = assnFixedMeta.getByteArrayValue(loop);
         if (meta == null || meta[0] != 0)
         {
            continue;
         }

         int offset = ByteArrayHelper.getInt(meta, 4);
         byte[] data = assnFixedData.getByteArrayValue(assnFixedData.getIndexFromOffset(offset));
         if (data == null)
         {
            continue;
         }

         if (data.length < fieldMap.getMaxFixedDataSize(0))
         {
            byte[] newData = new byte[fieldMap.getMaxFixedDataSize(0)];
            System.arraycopy(data, 0, newData, 0, data.length);
            data = newData;
         }

         int id = ByteArrayHelper.getInt(data, fieldMap.getFixedDataOffset(AssignmentField.UNIQUE_ID));
         final Integer varDataId = Integer.valueOf(id);
         if (!set.contains(varDataId))
         {
            continue;
         }

         byte[] data2 = null;
         if (assnFixedData2 != null)
         {
            data2 = assnFixedData2.getByteArrayValue(assnFixedData.getIndexFromOffset(offset));
         }

         ResourceAssignment assignment = new ResourceAssignment(file, null);

         assignment.disableEvents();

         fieldMap.populateContainer(FieldTypeClass.ASSIGNMENT, assignment, varDataId, new byte[][]
         {
            data,
            data2
         }, assnVarData);

         if (enterpriseCustomFieldMap != null)
         {
            enterpriseCustomFieldMap.populateContainer(FieldTypeClass.ASSIGNMENT, assignment, varDataId, null, assnVarData);
         }

         assignment.enableEvents();

         Stream.of(metaDataBitFlags).forEach(f -> f.setValue(assignment, meta));

         // Map the null resource ID value to null
         if (NumberHelper.equals(assignment.getResourceUniqueID(), MicrosoftProjectConstants.ASSIGNMENT_NULL_RESOURCE_ID))
         {
            assignment.setResourceUniqueID(null);
         }

         hyperlinkReader.read(assignment, assnVarData.getByteArray(varDataId, fieldMap.getVarDataKey(AssignmentField.HYPERLINK_DATA)));

         //
         // Post processing
         //
         if (NumberHelper.getInt(file.getProjectProperties().getMppFileType()) == 9 && assignment.getCreateDate() == null)
         {
            byte[] creationData = assnVarData.getByteArray(varDataId, MPP9_CREATION_DATA);
            if (creationData != null && creationData.length >= 28)
            {
               assignment.setCreateDate(MPPUtility.getTimestamp(creationData, 24));
            }
         }

         Task task = file.getTaskByUniqueID(assignment.getTaskUniqueID());
         if (task != null && task.getExistingResourceAssignment(assignment.getResource()) == null)
         {
            task.addResourceAssignment(assignment);

            Resource resource = file.getResourceByUniqueID(assignment.getResourceUniqueID());
            ResourceType resourceType = resource == null ? ResourceType.WORK : resource.getType();
            ProjectCalendar calendar = assignment.getEffectiveCalendar();

            for (int index = 0; index < TIMEPHASED_BASELINE_WORK.length; index++)
            {
               assignment.setTimephasedBaselineWork(index, timephasedFactory.getBaselineWork(calendar, assignment, MPPTimephasedBaselineWorkNormaliser.INSTANCE, assnVarData.getByteArray(varDataId, fieldMap.getVarDataKey(TIMEPHASED_BASELINE_WORK[index])), !useRawTimephasedData));
               assignment.setTimephasedBaselineCost(index, timephasedFactory.getBaselineCost(assignment, MPPTimephasedBaselineCostNormaliser.INSTANCE, assnVarData.getByteArray(varDataId, fieldMap.getVarDataKey(TIMEPHASED_BASELINE_COST[index])), !useRawTimephasedData));
            }

            byte[] timephasedActualWorkData = assnVarData.getByteArray(varDataId, fieldMap.getVarDataKey(AssignmentField.TIMEPHASED_ACTUAL_WORK));
            byte[] timephasedWorkData = assnVarData.getByteArray(varDataId, fieldMap.getVarDataKey(AssignmentField.TIMEPHASED_WORK));
            byte[] timephasedActualOvertimeWorkData = assnVarData.getByteArray(varDataId, fieldMap.getVarDataKey(AssignmentField.TIMEPHASED_ACTUAL_OVERTIME_WORK));

            List<TimephasedWork> timephasedActualWork = timephasedFactory.getCompleteWork(calendar, assignment, timephasedActualWorkData);
            List<TimephasedWork> timephasedWork = timephasedFactory.getPlannedWork(calendar, assignment, timephasedWorkData, timephasedActualWork, resourceType);
            List<TimephasedWork> timephasedActualOvertimeWork = timephasedFactory.getCompleteWork(calendar, assignment, timephasedActualOvertimeWorkData);

            if (task.getDuration() == null || task.getDuration().getDuration() == 0)
            {
               // If we have a zero duration task, we'll set the assignment actual start and finish based on the task actual start and finish
               assignment.setActualStart(task.getActualStart() != null ? assignment.getStart() : null);
               assignment.setActualFinish(task.getActualFinish() != null ? assignment.getFinish() : null);
            }
            else
            {
               // We have a task with a duration, try to determine the assignment actual start and finish values
               assignment.setActualFinish((task.getActualStart() != null && assignment.getRemainingWork().getDuration() == 0 && resource != null) ? assignment.getFinish() : null);
               assignment.setActualStart(assignment.getActualFinish() != null || !timephasedActualWork.isEmpty() ? assignment.getStart() : null);
            }

            // TODO: this assumes that timephased data for all assignments of a task is the same
            if (!task.getMilestone() && !processedSplits.contains(task))
            {
               processedSplits.add(task);
               SplitTaskFactory.processSplitData(assignment, timephasedActualWork, timephasedWork);
            }

            createTimephasedData(file, assignment, timephasedWork, timephasedActualWork);

            assignment.setTimephasedWork(new DefaultTimephasedWorkContainer(assignment, MPPTimephasedWorkNormaliser.INSTANCE, timephasedWork, !useRawTimephasedData));
            assignment.setTimephasedActualWork(new DefaultTimephasedWorkContainer(assignment, MPPTimephasedWorkNormaliser.INSTANCE, timephasedActualWork, !useRawTimephasedData));
            assignment.setTimephasedActualOvertimeWork(new DefaultTimephasedWorkContainer(assignment, MPPTimephasedWorkNormaliser.INSTANCE, timephasedActualOvertimeWork, !useRawTimephasedData));

            if (timephasedWorkData != null)
            {
               // TODO: there is some additional logic around split tasks we need to account for,
               // the flag alone doesn't seem to be set for contoured split tasks.

               // If the assignment is contoured, this will already have been set by the time we get here.
               // If we're still set to flat, retrieve the actual work contour setting from the timephased data.
               if (assignment.getWorkContour() == WorkContour.FLAT)
               {
                  if (timephasedWorkData.length >= 30)
                  {
                     assignment.setWorkContour(WorkContourHelper.getInstance(file, ByteArrayHelper.getShort(timephasedWorkData, 28)));
                  }
               }
            }

            file.getEventManager().fireAssignmentReadEvent(assignment);
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
   private void createTimephasedData(ProjectFile file, ResourceAssignment assignment, List<TimephasedWork> timephasedPlanned, List<TimephasedWork> timephasedComplete)
   {
      if (!timephasedPlanned.isEmpty() || !timephasedComplete.isEmpty() || assignment.getTask().getDuration() == null || assignment.getTask().getDuration().getDuration() == 0)
      {
         return;
      }

      Duration totalMinutes = assignment.getWork().convertUnits(TimeUnit.MINUTES, file.getProjectProperties());

      Duration workPerDay;

      if (assignment.getResource() == null || assignment.getResource().getType() == ResourceType.WORK)
      {
         workPerDay = totalMinutes.getDuration() == 0 ? totalMinutes : ResourceAssignmentFactory.DEFAULT_NORMALIZER_WORK_PER_DAY;
         int units = NumberHelper.getInt(assignment.getUnits());
         if (units != 100)
         {
            workPerDay = Duration.getInstance((workPerDay.getDuration() * units) / 100.0, workPerDay.getUnits());
         }
      }
      else
      {
         if (assignment.getVariableRateUnits() == null)
         {
            Duration workingDays = assignment.getEffectiveCalendar().getWork(assignment.getStart(), assignment.getFinish(), TimeUnit.DAYS);
            double units = NumberHelper.getDouble(assignment.getUnits());
            double unitsPerDayAsMinutes = (units * 60) / (workingDays.getDuration() * 100);
            workPerDay = Duration.getInstance(unitsPerDayAsMinutes, TimeUnit.MINUTES);
         }
         else
         {
            double unitsPerHour = NumberHelper.getDouble(assignment.getUnits());
            workPerDay = ResourceAssignmentFactory.DEFAULT_NORMALIZER_WORK_PER_DAY;
            Duration hoursPerDay = workPerDay.convertUnits(TimeUnit.HOURS, file.getProjectProperties());
            double unitsPerDayAsHours = (unitsPerHour * hoursPerDay.getDuration()) / 100;
            double unitsPerDayAsMinutes = unitsPerDayAsHours * 60;
            workPerDay = Duration.getInstance(unitsPerDayAsMinutes, TimeUnit.MINUTES);
         }
      }

      Duration overtimeWork = assignment.getOvertimeWork();
      if (overtimeWork != null && overtimeWork.getDuration() != 0)
      {
         Duration totalOvertimeMinutes = overtimeWork.convertUnits(TimeUnit.MINUTES, file.getProjectProperties());
         totalMinutes = Duration.getInstance(totalMinutes.getDuration() - totalOvertimeMinutes.getDuration(), TimeUnit.MINUTES);
      }

      TimephasedWork tra = new TimephasedWork();
      tra.setStart(assignment.getStart());
      tra.setAmountPerDay(workPerDay);
      tra.setModified(false);
      tra.setFinish(assignment.getFinish());
      tra.setTotalAmount(totalMinutes);
      timephasedPlanned.add(tra);
   }

   private static final Integer MPP9_CREATION_DATA = Integer.valueOf(138);

   private static final MppBitFlag[] ASSIGNMENT_META_DATA_BIT_FLAGS =
   {
      new MppBitFlag(AssignmentField.FLAG1, 28, 0x00000080, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.FLAG2, 28, 0x00000100, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.FLAG3, 28, 0x00000200, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.FLAG4, 28, 0x00000400, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.FLAG5, 28, 0x00000800, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.FLAG6, 28, 0x00001000, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.FLAG7, 28, 0x00002000, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.FLAG8, 28, 0x00004000, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.FLAG9, 28, 0x00008000, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.FLAG10, 28, 0x00000040, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.FLAG11, 28, 0x00010000, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.FLAG12, 28, 0x00020000, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.FLAG13, 28, 0x00040000, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.FLAG14, 28, 0x00080000, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.FLAG15, 28, 0x00100000, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.FLAG16, 28, 0x00200000, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.FLAG17, 28, 0x00400000, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.FLAG18, 28, 0x00800000, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.FLAG19, 28, 0x01000000, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.FLAG20, 28, 0x02000000, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.LINKED_FIELDS, 8, 0x00000008, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.WORK_CONTOUR, 8, 0x00000010, WorkContour.FLAT, WorkContour.CONTOURED),
      new MppBitFlag(AssignmentField.CONFIRMED, 8, 0x00000080, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.TEAM_STATUS_PENDING, 8, 0x00020000, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.RESPONSE_PENDING, 8, 0x00000100, Boolean.FALSE, Boolean.TRUE)
   };

   private static final MppBitFlag[] PROJECT_2010_ASSIGNMENT_META_DATA_BIT_FLAGS =
   {
      new MppBitFlag(AssignmentField.FLAG10, 28, 0x000002, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.FLAG1, 28, 0x000004, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.FLAG2, 28, 0x000008, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.FLAG3, 28, 0x000010, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.FLAG4, 28, 0x000020, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.FLAG5, 28, 0x000040, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.FLAG6, 28, 0x000080, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.FLAG7, 28, 0x000100, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.FLAG8, 28, 0x000200, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.FLAG9, 28, 0x000400, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.FLAG11, 28, 0x000800, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.FLAG12, 28, 0x001000, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.FLAG13, 28, 0x002000, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.FLAG14, 28, 0x004000, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.FLAG15, 28, 0x008000, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.FLAG16, 28, 0x010000, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.FLAG17, 28, 0x020000, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.FLAG18, 28, 0x040000, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.FLAG19, 28, 0x080000, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.FLAG20, 28, 0x100000, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.LINKED_FIELDS, 8, 0x00000008, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.WORK_CONTOUR, 8, 0x00000010, WorkContour.FLAT, WorkContour.CONTOURED),
      new MppBitFlag(AssignmentField.CONFIRMED, 8, 0x00000080, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.TEAM_STATUS_PENDING, 8, 0x00020000, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.RESPONSE_PENDING, 8, 0x00000100, Boolean.FALSE, Boolean.TRUE)
   };

   private static final MppBitFlag[] PROJECT_2013_ASSIGNMENT_META_DATA_BIT_FLAGS =
   {
      new MppBitFlag(AssignmentField.FLAG1, 20, 0x000002, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.FLAG2, 20, 0x000004, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.FLAG3, 20, 0x000008, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.FLAG4, 20, 0x000010, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.FLAG5, 20, 0x000020, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.FLAG6, 20, 0x000040, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.FLAG7, 20, 0x000080, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.FLAG8, 20, 0x000100, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.FLAG9, 20, 0x000200, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.LINKED_FIELDS, 20, 0x00000400, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.FLAG10, 20, 0x000001, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.FLAG11, 25, 0x000008, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.FLAG12, 25, 0x000010, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.FLAG13, 25, 0x000020, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.FLAG14, 25, 0x000040, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.FLAG15, 25, 0x000080, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.FLAG16, 25, 0x000100, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.FLAG17, 25, 0x000200, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.FLAG18, 25, 0x000400, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.FLAG19, 25, 0x000800, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.FLAG20, 25, 0x001000, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.WORK_CONTOUR, 8, 0x00040000, WorkContour.FLAT, WorkContour.CONTOURED),
      new MppBitFlag(AssignmentField.CONFIRMED, 8, 0x00800000, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.TEAM_STATUS_PENDING, 8, 0x02000000, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.RESPONSE_PENDING, 8, 0x01000000, Boolean.FALSE, Boolean.TRUE)
   };

   private static final AssignmentField[] TIMEPHASED_BASELINE_WORK =
   {
      AssignmentField.TIMEPHASED_BASELINE_WORK,
      AssignmentField.TIMEPHASED_BASELINE1_WORK,
      AssignmentField.TIMEPHASED_BASELINE2_WORK,
      AssignmentField.TIMEPHASED_BASELINE3_WORK,
      AssignmentField.TIMEPHASED_BASELINE4_WORK,
      AssignmentField.TIMEPHASED_BASELINE5_WORK,
      AssignmentField.TIMEPHASED_BASELINE6_WORK,
      AssignmentField.TIMEPHASED_BASELINE7_WORK,
      AssignmentField.TIMEPHASED_BASELINE8_WORK,
      AssignmentField.TIMEPHASED_BASELINE9_WORK,
      AssignmentField.TIMEPHASED_BASELINE10_WORK
   };

   private static final AssignmentField[] TIMEPHASED_BASELINE_COST =
   {
      AssignmentField.TIMEPHASED_BASELINE_COST,
      AssignmentField.TIMEPHASED_BASELINE1_COST,
      AssignmentField.TIMEPHASED_BASELINE2_COST,
      AssignmentField.TIMEPHASED_BASELINE3_COST,
      AssignmentField.TIMEPHASED_BASELINE4_COST,
      AssignmentField.TIMEPHASED_BASELINE5_COST,
      AssignmentField.TIMEPHASED_BASELINE6_COST,
      AssignmentField.TIMEPHASED_BASELINE7_COST,
      AssignmentField.TIMEPHASED_BASELINE8_COST,
      AssignmentField.TIMEPHASED_BASELINE9_COST,
      AssignmentField.TIMEPHASED_BASELINE10_COST
   };

   private static final Duration DEFAULT_NORMALIZER_WORK_PER_DAY = Duration.getInstance(480, TimeUnit.MINUTES);
}
