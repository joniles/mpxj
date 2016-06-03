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
import net.sf.mpxj.Task;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.TimephasedWork;
import net.sf.mpxj.WorkContour;
import net.sf.mpxj.common.DefaultTimephasedWorkContainer;
import net.sf.mpxj.common.NumberHelper;
import net.sf.mpxj.common.RtfHelper;
import net.sf.mpxj.common.SplitTaskFactory;
import net.sf.mpxj.common.TimephasedCostNormaliser;
import net.sf.mpxj.common.TimephasedWorkNormaliser;

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
    * @param preserveNoteFormatting preserve note formatting flag
    * @param assnVarMeta var meta
    * @param assnVarData var data
    * @param assnFixedMeta fixed meta
    * @param assnFixedData fixed data
    * @param assnFixedData2 fixed data
    * @param count expected number of assignments
    */
   public void process(ProjectFile file, FieldMap fieldMap, FieldMap enterpriseCustomFieldMap, boolean useRawTimephasedData, boolean preserveNoteFormatting, VarMeta assnVarMeta, Var2Data assnVarData, FixedMeta assnFixedMeta, FixedData assnFixedData, FixedData assnFixedData2, int count)
   {
      Set<Integer> set = assnVarMeta.getUniqueIdentifierSet();
      TimephasedDataFactory timephasedFactory = new TimephasedDataFactory();
      SplitTaskFactory splitFactory = new SplitTaskFactory();
      TimephasedWorkNormaliser normaliser = new MPPTimephasedWorkNormaliser();
      TimephasedWorkNormaliser baselineWorkNormaliser = new MPPTimephasedBaselineWorkNormaliser();
      TimephasedCostNormaliser baselineCostNormaliser = new MPPTimephasedBaselineCostNormaliser();
      ProjectCalendar baselineCalendar = file.getBaselineCalendar();

      //System.out.println(assnFixedMeta);
      //System.out.println(assnFixedData);
      //System.out.println(assnVarMeta.toString(fieldMap));
      //System.out.println(assnVarData);

      MppBitFlag[] metaDataBitFlags;
      if (NumberHelper.getInt(file.getProjectProperties().getMppFileType()) == 14)
      {
         metaDataBitFlags = MPP14_ASSIGNMENT_META_DATA_BIT_FLAGS;
      }
      else
      {
         metaDataBitFlags = ASSIGNMENT_META_DATA_BIT_FLAGS;
      }

      for (int loop = 0; loop < count; loop++)
      {
         byte[] meta = assnFixedMeta.getByteArrayValue(loop);
         if (meta[0] != 0)
         {
            continue;
         }

         int offset = MPPUtility.getInt(meta, 4);
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

         int id = MPPUtility.getInt(data, fieldMap.getFixedDataOffset(AssignmentField.UNIQUE_ID));
         final Integer varDataId = Integer.valueOf(id);
         if (set.contains(varDataId) == false)
         {
            continue;
         }

         byte[] data2 = null;
         if (assnFixedData2 != null)
         {
            data2 = assnFixedData2.getByteArrayValue(loop);
         }

         ResourceAssignment assignment = new ResourceAssignment(file, null);

         assignment.disableEvents();

         fieldMap.populateContainer(AssignmentField.class, assignment, varDataId, new byte[][]
         {
            data,
            data2
         }, assnVarData);

         if (enterpriseCustomFieldMap != null)
         {
            enterpriseCustomFieldMap.populateContainer(AssignmentField.class, assignment, varDataId, null, assnVarData);
         }

         assignment.enableEvents();

         for (MppBitFlag flag : metaDataBitFlags)
         {
            flag.setValue(assignment, meta);
         }

         assignment.setConfirmed((meta[8] & 0x80) != 0);
         assignment.setResponsePending((meta[9] & 0x01) != 0);
         assignment.setTeamStatusPending((meta[10] & 0x02) != 0);

         processHyperlinkData(assignment, assnVarData.getByteArray(varDataId, fieldMap.getVarDataKey(AssignmentField.HYPERLINK_DATA)));

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

         String notes = assignment.getNotes();
         if (notes != null)
         {
            if (!preserveNoteFormatting)
            {
               notes = RtfHelper.strip(notes);
            }

            assignment.setNotes(notes);
         }

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
               calendar = file.getDefaultCalendar();
            }

            assignment.setTimephasedBaselineWork(0, timephasedFactory.getBaselineWork(assignment, baselineCalendar, baselineWorkNormaliser, assnVarData.getByteArray(varDataId, fieldMap.getVarDataKey(AssignmentField.TIMEPHASED_BASELINE_WORK)), !useRawTimephasedData));
            assignment.setTimephasedBaselineWork(1, timephasedFactory.getBaselineWork(assignment, baselineCalendar, baselineWorkNormaliser, assnVarData.getByteArray(varDataId, fieldMap.getVarDataKey(AssignmentField.TIMEPHASED_BASELINE1_WORK)), !useRawTimephasedData));
            assignment.setTimephasedBaselineWork(2, timephasedFactory.getBaselineWork(assignment, baselineCalendar, baselineWorkNormaliser, assnVarData.getByteArray(varDataId, fieldMap.getVarDataKey(AssignmentField.TIMEPHASED_BASELINE2_WORK)), !useRawTimephasedData));
            assignment.setTimephasedBaselineWork(3, timephasedFactory.getBaselineWork(assignment, baselineCalendar, baselineWorkNormaliser, assnVarData.getByteArray(varDataId, fieldMap.getVarDataKey(AssignmentField.TIMEPHASED_BASELINE3_WORK)), !useRawTimephasedData));
            assignment.setTimephasedBaselineWork(4, timephasedFactory.getBaselineWork(assignment, baselineCalendar, baselineWorkNormaliser, assnVarData.getByteArray(varDataId, fieldMap.getVarDataKey(AssignmentField.TIMEPHASED_BASELINE4_WORK)), !useRawTimephasedData));
            assignment.setTimephasedBaselineWork(5, timephasedFactory.getBaselineWork(assignment, baselineCalendar, baselineWorkNormaliser, assnVarData.getByteArray(varDataId, fieldMap.getVarDataKey(AssignmentField.TIMEPHASED_BASELINE5_WORK)), !useRawTimephasedData));
            assignment.setTimephasedBaselineWork(6, timephasedFactory.getBaselineWork(assignment, baselineCalendar, baselineWorkNormaliser, assnVarData.getByteArray(varDataId, fieldMap.getVarDataKey(AssignmentField.TIMEPHASED_BASELINE6_WORK)), !useRawTimephasedData));
            assignment.setTimephasedBaselineWork(7, timephasedFactory.getBaselineWork(assignment, baselineCalendar, baselineWorkNormaliser, assnVarData.getByteArray(varDataId, fieldMap.getVarDataKey(AssignmentField.TIMEPHASED_BASELINE7_WORK)), !useRawTimephasedData));
            assignment.setTimephasedBaselineWork(8, timephasedFactory.getBaselineWork(assignment, baselineCalendar, baselineWorkNormaliser, assnVarData.getByteArray(varDataId, fieldMap.getVarDataKey(AssignmentField.TIMEPHASED_BASELINE8_WORK)), !useRawTimephasedData));
            assignment.setTimephasedBaselineWork(9, timephasedFactory.getBaselineWork(assignment, baselineCalendar, baselineWorkNormaliser, assnVarData.getByteArray(varDataId, fieldMap.getVarDataKey(AssignmentField.TIMEPHASED_BASELINE9_WORK)), !useRawTimephasedData));
            assignment.setTimephasedBaselineWork(10, timephasedFactory.getBaselineWork(assignment, baselineCalendar, baselineWorkNormaliser, assnVarData.getByteArray(varDataId, fieldMap.getVarDataKey(AssignmentField.TIMEPHASED_BASELINE10_WORK)), !useRawTimephasedData));

            assignment.setTimephasedBaselineCost(0, timephasedFactory.getBaselineCost(baselineCalendar, baselineCostNormaliser, assnVarData.getByteArray(varDataId, fieldMap.getVarDataKey(AssignmentField.TIMEPHASED_BASELINE_COST)), !useRawTimephasedData));
            assignment.setTimephasedBaselineCost(1, timephasedFactory.getBaselineCost(baselineCalendar, baselineCostNormaliser, assnVarData.getByteArray(varDataId, fieldMap.getVarDataKey(AssignmentField.TIMEPHASED_BASELINE1_COST)), !useRawTimephasedData));
            assignment.setTimephasedBaselineCost(2, timephasedFactory.getBaselineCost(baselineCalendar, baselineCostNormaliser, assnVarData.getByteArray(varDataId, fieldMap.getVarDataKey(AssignmentField.TIMEPHASED_BASELINE2_COST)), !useRawTimephasedData));
            assignment.setTimephasedBaselineCost(3, timephasedFactory.getBaselineCost(baselineCalendar, baselineCostNormaliser, assnVarData.getByteArray(varDataId, fieldMap.getVarDataKey(AssignmentField.TIMEPHASED_BASELINE3_COST)), !useRawTimephasedData));
            assignment.setTimephasedBaselineCost(4, timephasedFactory.getBaselineCost(baselineCalendar, baselineCostNormaliser, assnVarData.getByteArray(varDataId, fieldMap.getVarDataKey(AssignmentField.TIMEPHASED_BASELINE4_COST)), !useRawTimephasedData));
            assignment.setTimephasedBaselineCost(5, timephasedFactory.getBaselineCost(baselineCalendar, baselineCostNormaliser, assnVarData.getByteArray(varDataId, fieldMap.getVarDataKey(AssignmentField.TIMEPHASED_BASELINE5_COST)), !useRawTimephasedData));
            assignment.setTimephasedBaselineCost(6, timephasedFactory.getBaselineCost(baselineCalendar, baselineCostNormaliser, assnVarData.getByteArray(varDataId, fieldMap.getVarDataKey(AssignmentField.TIMEPHASED_BASELINE6_COST)), !useRawTimephasedData));
            assignment.setTimephasedBaselineCost(7, timephasedFactory.getBaselineCost(baselineCalendar, baselineCostNormaliser, assnVarData.getByteArray(varDataId, fieldMap.getVarDataKey(AssignmentField.TIMEPHASED_BASELINE7_COST)), !useRawTimephasedData));
            assignment.setTimephasedBaselineCost(8, timephasedFactory.getBaselineCost(baselineCalendar, baselineCostNormaliser, assnVarData.getByteArray(varDataId, fieldMap.getVarDataKey(AssignmentField.TIMEPHASED_BASELINE8_COST)), !useRawTimephasedData));
            assignment.setTimephasedBaselineCost(9, timephasedFactory.getBaselineCost(baselineCalendar, baselineCostNormaliser, assnVarData.getByteArray(varDataId, fieldMap.getVarDataKey(AssignmentField.TIMEPHASED_BASELINE9_COST)), !useRawTimephasedData));
            assignment.setTimephasedBaselineCost(10, timephasedFactory.getBaselineCost(baselineCalendar, baselineCostNormaliser, assnVarData.getByteArray(varDataId, fieldMap.getVarDataKey(AssignmentField.TIMEPHASED_BASELINE10_COST)), !useRawTimephasedData));

            byte[] timephasedActualWorkData = assnVarData.getByteArray(varDataId, fieldMap.getVarDataKey(AssignmentField.TIMEPHASED_ACTUAL_WORK));
            byte[] timephasedWorkData = assnVarData.getByteArray(varDataId, fieldMap.getVarDataKey(AssignmentField.TIMEPHASED_WORK));
            byte[] timephasedActualOvertimeWorkData = assnVarData.getByteArray(varDataId, fieldMap.getVarDataKey(AssignmentField.TIMEPHASED_ACTUAL_OVERTIME_WORK));

            List<TimephasedWork> timephasedActualWork = timephasedFactory.getCompleteWork(calendar, assignment.getStart(), timephasedActualWorkData);
            List<TimephasedWork> timephasedWork = timephasedFactory.getPlannedWork(calendar, assignment.getStart(), assignment.getUnits().doubleValue(), timephasedWorkData, timephasedActualWork);
            List<TimephasedWork> timephasedActualOvertimeWork = timephasedFactory.getCompleteWork(calendar, assignment.getStart(), timephasedActualOvertimeWorkData);

            assignment.setActualStart(timephasedActualWork.isEmpty() ? null : assignment.getStart());
            assignment.setActualFinish((assignment.getRemainingWork().getDuration() == 0 && resource != null) ? assignment.getFinish() : null);

            if (task.getSplits() != null && task.getSplits().isEmpty())
            {
               splitFactory.processSplitData(task, timephasedActualWork, timephasedWork);
            }

            createTimephasedData(file, assignment, timephasedWork, timephasedActualWork);

            assignment.setTimephasedWork(new DefaultTimephasedWorkContainer(calendar, normaliser, timephasedWork, !useRawTimephasedData));
            assignment.setTimephasedActualWork(new DefaultTimephasedWorkContainer(calendar, normaliser, timephasedActualWork, !useRawTimephasedData));
            assignment.setTimephasedActualOvertimeWork(new DefaultTimephasedWorkContainer(calendar, normaliser, timephasedActualOvertimeWork, !useRawTimephasedData));

            if (timephasedWorkData != null)
            {
               if (timephasedFactory.getWorkModified(timephasedWork))
               {
                  assignment.setWorkContour(WorkContour.CONTOURED);
               }
               else
               {
                  if (timephasedWorkData.length >= 30)
                  {
                     assignment.setWorkContour(WorkContour.getInstance(MPPUtility.getShort(timephasedWorkData, 28)));
                  }
                  else
                  {
                     assignment.setWorkContour(WorkContour.FLAT);
                  }
               }
            }

            file.getEventManager().fireAssignmentReadEvent(assignment);
         }
      }
   }

   /**
    * Extract assignment hyperlink data.
    *
    * @param assignment assignment instance
    * @param data hyperlink data
    */
   private void processHyperlinkData(ResourceAssignment assignment, byte[] data)
   {
      if (data != null)
      {
         int offset = 12;

         offset += 12;
         String hyperlink = MPPUtility.getUnicodeString(data, offset);
         offset += ((hyperlink.length() + 1) * 2);

         offset += 12;
         String address = MPPUtility.getUnicodeString(data, offset);
         offset += ((address.length() + 1) * 2);

         offset += 12;
         String subaddress = MPPUtility.getUnicodeString(data, offset);
         offset += ((subaddress.length() + 1) * 2);

         offset += 12;
         String screentip = MPPUtility.getUnicodeString(data, offset);

         assignment.setHyperlink(hyperlink);
         assignment.setHyperlinkAddress(address);
         assignment.setHyperlinkSubAddress(subaddress);
         assignment.setHyperlinkScreenTip(screentip);
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
      if (timephasedPlanned.isEmpty() && timephasedComplete.isEmpty())
      {
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
               Duration workingDays = assignment.getCalendar().getWork(assignment.getStart(), assignment.getFinish(), TimeUnit.DAYS);
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
   }

   private static final Integer MPP9_CREATION_DATA = Integer.valueOf(138);

   private static final MppBitFlag[] ASSIGNMENT_META_DATA_BIT_FLAGS =
   {
      new MppBitFlag(AssignmentField.FLAG1, 28, 0x0000080, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.FLAG2, 28, 0x0000100, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.FLAG3, 28, 0x0000200, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.FLAG4, 28, 0x0000400, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.FLAG5, 28, 0x0000800, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.FLAG6, 28, 0x0001000, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.FLAG7, 28, 0x0002000, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.FLAG8, 28, 0x0004000, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.FLAG9, 28, 0x0008000, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.FLAG10, 28, 0x0010000, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.FLAG11, 28, 0x0020000, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.FLAG12, 28, 0x0040000, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.FLAG13, 28, 0x0080000, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.FLAG14, 28, 0x0100000, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.FLAG15, 28, 0x0200000, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.FLAG16, 28, 0x0400000, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.FLAG17, 28, 0x0800000, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.FLAG18, 28, 0x1000000, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.FLAG19, 28, 0x2000000, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(AssignmentField.FLAG20, 28, 0x4000000, Boolean.FALSE, Boolean.TRUE)
   };

   private static final MppBitFlag[] MPP14_ASSIGNMENT_META_DATA_BIT_FLAGS =
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
      new MppBitFlag(AssignmentField.FLAG20, 28, 0x100000, Boolean.FALSE, Boolean.TRUE)
   };

   private static final Duration DEFAULT_NORMALIZER_WORK_PER_DAY = Duration.getInstance(480, TimeUnit.MINUTES);
}
