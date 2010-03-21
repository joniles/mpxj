/*
 * file:       ResourceAssignmentFactory14.java
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

import java.util.Date;
import java.util.List;
import java.util.Set;

import net.sf.mpxj.Duration;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Resource;
import net.sf.mpxj.ResourceAssignment;
import net.sf.mpxj.SplitTaskFactory;
import net.sf.mpxj.Task;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.TimephasedResourceAssignment;
import net.sf.mpxj.TimephasedResourceAssignmentNormaliser;
import net.sf.mpxj.WorkContour;
import net.sf.mpxj.utility.NumberUtility;


/**
 * Reads resource assignment data from an MPP14 file.
 */
public final class ResourceAssignmentFactory14 implements ResourceAssignmentFactory
{
   /**
    * {@inheritDoc}
    */
   public void process(ProjectFile file, boolean useRawTimephasedData, VarMeta assnVarMeta, Var2Data assnVarData, FixedMeta assnFixedMeta, FixedData assnFixedData)
   {
      Set<Integer> set = assnVarMeta.getUniqueIdentifierSet();
      int count = assnFixedMeta.getItemCount();
      TimephasedResourceAssignmentFactory timephasedFactory = new TimephasedResourceAssignmentFactory();
      SplitTaskFactory splitFactory = new SplitTaskFactory();
      TimephasedResourceAssignmentNormaliser normaliser = new MPPTimephasedResourceAssignmentNormaliser();

      //System.out.println(assnVarMeta);
      //System.out.println(assnVarData);
      
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

         int id = MPPUtility.getInt(data, 0);
         final Integer varDataId = Integer.valueOf(id);
         if (set.contains(varDataId) == false)
         {
            continue;
         }

         Integer taskID = Integer.valueOf(MPPUtility.getInt(data, 4));
         Task task = file.getTaskByUniqueID(taskID);

         if (task != null)
         {
            Integer resourceID = Integer.valueOf(MPPUtility.getInt(data, 8));
            Resource resource = file.getResourceByUniqueID(resourceID);

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

            Date assignmentStart = MPPUtility.getTimestamp(data, 12);
            Date assignmentFinish = MPPUtility.getTimestamp(data, 16);            
            double assignmentUnits = (MPPUtility.getDouble(data, 46)) / 100;
            byte[] completeWork = assnVarData.getByteArray(varDataId, COMPLETE_WORK);
            byte[] plannedWork = assnVarData.getByteArray(varDataId, PLANNED_WORK);
            double remainingWork = (MPPUtility.getDouble(data, 78)) / 100;            
            List<TimephasedResourceAssignment> timephasedComplete = timephasedFactory.getCompleteWork(calendar, assignmentStart, completeWork);
            List<TimephasedResourceAssignment> timephasedPlanned = timephasedFactory.getPlannedWork(calendar, assignmentStart, assignmentUnits, plannedWork, timephasedComplete);
            //System.out.println(timephasedComplete);
            //System.out.println(timephasedPlanned);

            if (task.getSplits() != null && task.getSplits().isEmpty())
            {
               splitFactory.processSplitData(task, timephasedComplete, timephasedPlanned);
            }

            if (resource != null)
            {
               //System.out.println("Task: " + task.getName());
               //System.out.println("Resource: " + resource.getName());
               //System.out.println(MPPUtility.hexdump(data, false, 16, ""));
               //System.out.println(MPPUtility.hexdump(incompleteWork, false, 16, ""));
               //System.out.println(MPPUtility.hexdump(meta, false, 16, ""));               

               ResourceAssignment assignment = task.addResourceAssignment(resource);
               assignment.setTimephasedNormaliser(normaliser);

               assignment.setActualCost(NumberUtility.getDouble(MPPUtility.getDouble(data, 94) / 100));
               assignment.setActualFinish(remainingWork == 0 ? assignmentFinish : null);  
               assignment.setActualStart(completeWork == null ? null : assignmentStart);               
               assignment.setActualWork(MPPUtility.getDuration((MPPUtility.getDouble(data, 70)) / 100, TimeUnit.HOURS));
               assignment.setCost(NumberUtility.getDouble(MPPUtility.getDouble(data, 86) / 100));
               assignment.setDelay(MPPUtility.getDuration(MPPUtility.getShort(data, 24), TimeUnit.HOURS));
               assignment.setFinish(assignmentFinish);
               assignment.setRemainingWork(MPPUtility.getDuration(remainingWork, TimeUnit.HOURS));
               assignment.setStart(assignmentStart);
               assignment.setUnits(Double.valueOf(assignmentUnits));
               assignment.setWork(MPPUtility.getDuration((MPPUtility.getDouble(data, 70)/100), TimeUnit.HOURS)); 
               assignment.setBaselineCost(NumberUtility.getDouble(assnVarData.getDouble(varDataId, BASELINE_COST)/100));
               assignment.setBaselineFinish(assnVarData.getTimestamp(varDataId, BASELINE_FINISH));
               assignment.setBaselineStart(assnVarData.getTimestamp(varDataId, BASELINE_START));
               assignment.setBaselineWork(Duration.getInstance(assnVarData.getDouble(varDataId, BASELINE_WORK) / 60000, TimeUnit.HOURS));
               
               if (timephasedPlanned.isEmpty() && timephasedComplete.isEmpty())
               {
                  Duration workPerDay = TimephasedResourceAssignmentNormaliser.DEFAULT_NORMALIZER_WORK_PER_DAY;
                  int units = NumberUtility.getInt(assignment.getUnits());
                  if (units != 100)
                  {
                     workPerDay = Duration.getInstance((workPerDay.getDuration() * units) / 100.0, workPerDay.getUnits());
                  }

                  TimephasedResourceAssignment tra = new TimephasedResourceAssignment();
                  tra.setStart(assignmentStart);
                  tra.setWorkPerDay(workPerDay);
                  tra.setModified(false);
                  tra.setFinish(assignment.getFinish());
                  tra.setTotalWork(assignment.getWork().convertUnits(TimeUnit.MINUTES, file.getProjectHeader()));
                  timephasedPlanned.add(tra);
               }

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
               }
            }
         }
      }      
   }
   
   private static final Integer PLANNED_WORK = Integer.valueOf(49);
   private static final Integer COMPLETE_WORK = Integer.valueOf(50);
   private static final Integer BASELINE_WORK = Integer.valueOf(16);   
   private static final Integer BASELINE_COST = Integer.valueOf(32);   
   private static final Integer BASELINE_START = Integer.valueOf(146);
   private static final Integer BASELINE_FINISH = Integer.valueOf(147);      
}
