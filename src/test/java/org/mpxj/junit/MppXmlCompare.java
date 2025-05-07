/*
 * file:       MppXmlCompare.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2005
 * date:       2005-12-05
 */

package org.mpxj.junit;

import java.util.List;

import org.mpxj.Duration;
import org.mpxj.ProjectFile;
import org.mpxj.Resource;
import org.mpxj.Task;
import org.mpxj.common.NumberHelper;

/**
 * The purpose of this class is to allow the contents of an MSPDI file
 * to be compared to the contents of an MPP file.
 *
 * The anticipated use of this functionality is to ensure that where we have
 * an example MPP file, we can generate an MSPDI file from it using the
 * most recent version of MS Project, then we can ensure that we get
 * consistent data from both files when they are compared.
 *
 * This is designed to be used as part of the regression testing suite.
 */
public final class MppXmlCompare
{
   /**
    * Compares the data held in two project files.
    * @param xml MSPDI file
    * @param mpp MPP file
    */
   public void process(ProjectFile xml, ProjectFile mpp) throws Exception
   {
      m_xml = xml;
      m_mpp = mpp;
      m_fileVersion = mpp.getProjectProperties().getMppFileType().intValue();

      //compareProperties
      compareResources();
      compareTasks();
      //compareAssignments
   }

   /**
    * Compares sets of tasks between files.
    */
   private void compareTasks() throws Exception
   {
      List<Task> xmlTasks = m_xml.getTasks();

      // won't always match - tasks with blank names not preserved?
      //List mppTasks = m_mpp.getAllTasks();
      //assertEquals(xmlTasks.size(), mppTasks.size());

      for (Task xmlTask : xmlTasks)
      {
         m_currentEntity = xmlTask;

         // too much variability
         if (NumberHelper.getInt(xmlTask.getUniqueID()) == 0)
         {
            continue;
         }

         // tasks with null names not read?
         if (xmlTask.getName() == null)
         {
            continue;
         }

         Task mppTask = m_mpp.getTaskByUniqueID(xmlTask.getUniqueID());
         assertNotNull("Missing task " + xmlTask.getName() + " (Unique ID= " + xmlTask.getUniqueID() + ")", mppTask);

         //
         // Test MPP9 task attributes
         //
         assertEquals(xmlTask.getActualCost(), mppTask.getActualCost());
         assertEquals(xmlTask.getActualDuration(), mppTask.getActualDuration());
         assertEquals(xmlTask.getActualFinish(), mppTask.getActualFinish());
         assertEquals(xmlTask.getActualOvertimeCost(), mppTask.getActualOvertimeCost());
         assertEquals(xmlTask.getActualOvertimeWork(), mppTask.getActualOvertimeWork());
         assertEquals(xmlTask.getActualStart(), mppTask.getActualStart());
         assertEquals(xmlTask.getActualWork(), mppTask.getActualWork());
         // Baselines not currently read from MSPDI files
         //assertEquals(xmlTask.getBaselineCost(), mppTask.getBaselineCost());
         //assertEquals(xmlTask.getBaselineDuration(), mppTask.getBaselineDuration());
         //assertEquals(xmlTask.getBaselineFinish(), mppTask.getBaselineFinish());
         //assertEquals(xmlTask.getBaselineStart(), mppTask.getBaselineStart());
         //assertEquals(xmlTask.getBaselineWork(), mppTask.getBaselineWork());
         assertEquals(xmlTask.getConstraintDate(), mppTask.getConstraintDate());
         assertEquals(xmlTask.getConstraintType(), mppTask.getConstraintType());
         assertEquals(xmlTask.getContact(), mppTask.getContact());
         assertEquals(xmlTask.getCost(), mppTask.getCost());
         assertEquals(xmlTask.getCost(1), mppTask.getCost(1));
         assertEquals(xmlTask.getCost(2), mppTask.getCost(2));
         assertEquals(xmlTask.getCost(3), mppTask.getCost(3));
         assertEquals(xmlTask.getCost(4), mppTask.getCost(4));
         assertEquals(xmlTask.getCost(5), mppTask.getCost(5));
         assertEquals(xmlTask.getCost(6), mppTask.getCost(6));
         assertEquals(xmlTask.getCost(7), mppTask.getCost(7));
         assertEquals(xmlTask.getCost(8), mppTask.getCost(8));
         assertEquals(xmlTask.getCost(9), mppTask.getCost(9));
         assertEquals(xmlTask.getCost(10), mppTask.getCost(10));
         assertEquals(xmlTask.getCreateDate(), mppTask.getCreateDate());
         assertEquals(xmlTask.getDate(1), mppTask.getDate(1));
         assertEquals(xmlTask.getDate(2), mppTask.getDate(2));
         assertEquals(xmlTask.getDate(3), mppTask.getDate(3));
         assertEquals(xmlTask.getDate(4), mppTask.getDate(4));
         assertEquals(xmlTask.getDate(5), mppTask.getDate(5));
         assertEquals(xmlTask.getDate(6), mppTask.getDate(6));
         assertEquals(xmlTask.getDate(7), mppTask.getDate(7));
         assertEquals(xmlTask.getDate(8), mppTask.getDate(8));
         assertEquals(xmlTask.getDate(9), mppTask.getDate(9));
         assertEquals(xmlTask.getDate(10), mppTask.getDate(10));
         assertEquals(xmlTask.getDeadline(), mppTask.getDeadline());
         assertEquals(xmlTask.getDuration(), mppTask.getDuration());
         assertEquals(xmlTask.getDuration(1), mppTask.getDuration(1));
         assertEquals(xmlTask.getDuration(2), mppTask.getDuration(2));
         assertEquals(xmlTask.getDuration(3), mppTask.getDuration(3));
         assertEquals(xmlTask.getDuration(4), mppTask.getDuration(4));
         assertEquals(xmlTask.getDuration(5), mppTask.getDuration(5));
         assertEquals(xmlTask.getDuration(6), mppTask.getDuration(6));
         assertEquals(xmlTask.getDuration(7), mppTask.getDuration(7));
         assertEquals(xmlTask.getDuration(8), mppTask.getDuration(8));
         assertEquals(xmlTask.getDuration(9), mppTask.getDuration(9));
         assertEquals(xmlTask.getDuration(10), mppTask.getDuration(10));
         assertEquals(xmlTask.getEarlyFinish(), mppTask.getEarlyFinish());
         assertEquals(xmlTask.getEarlyStart(), mppTask.getEarlyStart());
         assertEquals(xmlTask.getEffortDriven(), mppTask.getEffortDriven());
         assertEquals(xmlTask.getEstimated(), mppTask.getEstimated());
         // check
         //assertEquals(xmlTask.getExpanded(), mppTask.getExpanded());
         assertEquals(xmlTask.getFinish(), mppTask.getFinish());
         assertEquals(xmlTask.getFinish(1), mppTask.getFinish(1));
         assertEquals(xmlTask.getFinish(2), mppTask.getFinish(2));
         assertEquals(xmlTask.getFinish(3), mppTask.getFinish(3));
         assertEquals(xmlTask.getFinish(4), mppTask.getFinish(4));
         assertEquals(xmlTask.getFinish(5), mppTask.getFinish(5));
         assertEquals(xmlTask.getFinish(6), mppTask.getFinish(6));
         assertEquals(xmlTask.getFinish(7), mppTask.getFinish(7));
         assertEquals(xmlTask.getFinish(8), mppTask.getFinish(8));
         assertEquals(xmlTask.getFinish(9), mppTask.getFinish(9));
         assertEquals(xmlTask.getFinish(10), mppTask.getFinish(10));
         assertEquals(xmlTask.getFixedCost(), mppTask.getFixedCost());
         assertEquals(xmlTask.getFixedCostAccrual(), mppTask.getFixedCostAccrual());
         assertEquals(xmlTask.getFlag(1), mppTask.getFlag(1));
         assertEquals(xmlTask.getFlag(2), mppTask.getFlag(2));
         assertEquals(xmlTask.getFlag(3), mppTask.getFlag(3));
         assertEquals(xmlTask.getFlag(4), mppTask.getFlag(4));
         assertEquals(xmlTask.getFlag(5), mppTask.getFlag(5));
         assertEquals(xmlTask.getFlag(6), mppTask.getFlag(6));
         assertEquals(xmlTask.getFlag(7), mppTask.getFlag(7));
         assertEquals(xmlTask.getFlag(8), mppTask.getFlag(8));
         assertEquals(xmlTask.getFlag(9), mppTask.getFlag(9));
         assertEquals(xmlTask.getFlag(10), mppTask.getFlag(10));
         assertEquals(xmlTask.getFlag(11), mppTask.getFlag(11));
         assertEquals(xmlTask.getFlag(12), mppTask.getFlag(12));
         assertEquals(xmlTask.getFlag(13), mppTask.getFlag(13));
         assertEquals(xmlTask.getFlag(14), mppTask.getFlag(14));
         assertEquals(xmlTask.getFlag(15), mppTask.getFlag(15));
         assertEquals(xmlTask.getFlag(16), mppTask.getFlag(16));
         assertEquals(xmlTask.getFlag(17), mppTask.getFlag(17));
         assertEquals(xmlTask.getFlag(18), mppTask.getFlag(18));
         assertEquals(xmlTask.getFlag(19), mppTask.getFlag(19));
         assertEquals(xmlTask.getFlag(20), mppTask.getFlag(20));
         assertEquals(xmlTask.getHideBar(), mppTask.getHideBar());
         assertEquals(xmlTask.getHyperlink(), mppTask.getHyperlink());
         assertEquals(xmlTask.getHyperlinkAddress(), mppTask.getHyperlinkAddress());
         assertEquals(xmlTask.getHyperlinkSubAddress(), mppTask.getHyperlinkSubAddress());
         // check this
         //assertEquals(xmlTask.getID(), mppTask.getID());
         // must check
         //assertEquals(xmlTask.getLateFinish(), mppTask.getLateFinish());
         //assertEquals(xmlTask.getLateStart(), mppTask.getLateStart());
         assertEquals(xmlTask.getLevelAssignments(), mppTask.getLevelAssignments());
         assertEquals(xmlTask.getLevelingCanSplit(), mppTask.getLevelingCanSplit());
         assertEquals(xmlTask.getLevelingDelay(), mppTask.getLevelingDelay());
         assertEquals(xmlTask.getMarked(), mppTask.getMarked());
         assertEquals(xmlTask.getMilestone(), mppTask.getMilestone());
         assertEquals(xmlTask.getName(), mppTask.getName());
         assertEquals(xmlTask.getNumber(1), mppTask.getNumber(1));
         assertEquals(xmlTask.getNumber(2), mppTask.getNumber(2));
         assertEquals(xmlTask.getNumber(3), mppTask.getNumber(3));
         assertEquals(xmlTask.getNumber(4), mppTask.getNumber(4));
         assertEquals(xmlTask.getNumber(5), mppTask.getNumber(5));
         assertEquals(xmlTask.getNumber(6), mppTask.getNumber(6));
         assertEquals(xmlTask.getNumber(7), mppTask.getNumber(7));
         assertEquals(xmlTask.getNumber(8), mppTask.getNumber(8));
         assertEquals(xmlTask.getNumber(9), mppTask.getNumber(9));
         assertEquals(xmlTask.getNumber(10), mppTask.getNumber(10));
         assertEquals(xmlTask.getNumber(11), mppTask.getNumber(11));
         assertEquals(xmlTask.getNumber(12), mppTask.getNumber(12));
         assertEquals(xmlTask.getNumber(13), mppTask.getNumber(13));
         assertEquals(xmlTask.getNumber(14), mppTask.getNumber(14));
         assertEquals(xmlTask.getNumber(15), mppTask.getNumber(15));
         assertEquals(xmlTask.getNumber(16), mppTask.getNumber(16));
         assertEquals(xmlTask.getNumber(17), mppTask.getNumber(17));
         assertEquals(xmlTask.getNumber(18), mppTask.getNumber(18));
         assertEquals(xmlTask.getNumber(19), mppTask.getNumber(19));
         assertEquals(xmlTask.getNumber(20), mppTask.getNumber(20));
         assertEquals(xmlTask.getOutlineCode(1), mppTask.getOutlineCode(1));
         assertEquals(xmlTask.getOutlineCode(2), mppTask.getOutlineCode(2));
         assertEquals(xmlTask.getOutlineCode(3), mppTask.getOutlineCode(3));
         assertEquals(xmlTask.getOutlineCode(4), mppTask.getOutlineCode(4));
         assertEquals(xmlTask.getOutlineCode(5), mppTask.getOutlineCode(5));
         assertEquals(xmlTask.getOutlineCode(6), mppTask.getOutlineCode(6));
         assertEquals(xmlTask.getOutlineCode(7), mppTask.getOutlineCode(7));
         assertEquals(xmlTask.getOutlineCode(8), mppTask.getOutlineCode(8));
         assertEquals(xmlTask.getOutlineCode(9), mppTask.getOutlineCode(9));
         assertEquals(xmlTask.getOutlineCode(10), mppTask.getOutlineCode(10));
         assertEquals(xmlTask.getOutlineLevel(), mppTask.getOutlineLevel());
         assertEquals(xmlTask.getOvertimeCost(), mppTask.getOvertimeCost());
         assertEquals(xmlTask.getPercentageComplete(), mppTask.getPercentageComplete());
         assertEquals(xmlTask.getPercentageWorkComplete(), mppTask.getPercentageWorkComplete());
         assertEquals(xmlTask.getPreleveledFinish(), mppTask.getPreleveledFinish());
         assertEquals(xmlTask.getPreleveledStart(), mppTask.getPreleveledStart());
         assertEquals(xmlTask.getPriority(), mppTask.getPriority());
         assertEquals(xmlTask.getRemainingCost(), mppTask.getRemainingCost());
         assertEquals(xmlTask.getRemainingDuration(), mppTask.getRemainingDuration());
         assertEquals(xmlTask.getRemainingOvertimeCost(), mppTask.getRemainingOvertimeCost());
         assertEquals(xmlTask.getRemainingOvertimeWork(), mppTask.getRemainingOvertimeWork());
         assertEquals(xmlTask.getRemainingWork(), mppTask.getRemainingWork());
         assertEquals(xmlTask.getResume(), mppTask.getResume());
         assertEquals(xmlTask.getRollup(), mppTask.getRollup());
         assertEquals(xmlTask.getStart(), mppTask.getStart());
         assertEquals(xmlTask.getStart(1), mppTask.getStart(1));
         assertEquals(xmlTask.getStart(2), mppTask.getStart(2));
         assertEquals(xmlTask.getStart(3), mppTask.getStart(3));
         assertEquals(xmlTask.getStart(4), mppTask.getStart(4));
         assertEquals(xmlTask.getStart(5), mppTask.getStart(5));
         assertEquals(xmlTask.getStart(6), mppTask.getStart(6));
         assertEquals(xmlTask.getStart(7), mppTask.getStart(7));
         assertEquals(xmlTask.getStart(8), mppTask.getStart(8));
         assertEquals(xmlTask.getStart(9), mppTask.getStart(9));
         assertEquals(xmlTask.getStart(10), mppTask.getStart(10));
         assertEquals(xmlTask.getStop(), mppTask.getStop());
         // Subprojects not implemented in XML
         //assertEquals(xmlTask.getSubprojectTaskUniqueID(), mppTask.getSubprojectTaskUniqueID());
         assertEquals(xmlTask.getText(1), mppTask.getText(1));
         assertEquals(xmlTask.getText(2), mppTask.getText(2));
         assertEquals(xmlTask.getText(3), mppTask.getText(3));
         assertEquals(xmlTask.getText(4), mppTask.getText(4));
         assertEquals(xmlTask.getText(5), mppTask.getText(5));
         assertEquals(xmlTask.getText(6), mppTask.getText(6));
         assertEquals(xmlTask.getText(7), mppTask.getText(7));
         assertEquals(xmlTask.getText(8), mppTask.getText(8));
         assertEquals(xmlTask.getText(9), mppTask.getText(9));
         assertEquals(xmlTask.getText(10), mppTask.getText(10));
         assertEquals(xmlTask.getText(11), mppTask.getText(11));
         assertEquals(xmlTask.getText(12), mppTask.getText(12));
         assertEquals(xmlTask.getText(13), mppTask.getText(13));
         assertEquals(xmlTask.getText(14), mppTask.getText(14));
         assertEquals(xmlTask.getText(15), mppTask.getText(15));
         assertEquals(xmlTask.getText(16), mppTask.getText(16));
         assertEquals(xmlTask.getText(17), mppTask.getText(17));
         assertEquals(xmlTask.getText(18), mppTask.getText(18));
         assertEquals(xmlTask.getText(19), mppTask.getText(19));
         assertEquals(xmlTask.getText(20), mppTask.getText(20));
         assertEquals(xmlTask.getText(21), mppTask.getText(21));
         assertEquals(xmlTask.getText(22), mppTask.getText(22));
         assertEquals(xmlTask.getText(23), mppTask.getText(23));
         assertEquals(xmlTask.getText(24), mppTask.getText(24));
         assertEquals(xmlTask.getText(25), mppTask.getText(25));
         assertEquals(xmlTask.getText(26), mppTask.getText(26));
         assertEquals(xmlTask.getText(27), mppTask.getText(27));
         assertEquals(xmlTask.getText(28), mppTask.getText(28));
         assertEquals(xmlTask.getText(29), mppTask.getText(29));
         assertEquals(xmlTask.getText(30), mppTask.getText(30));
         assertEquals(xmlTask.getType(), mppTask.getType());
         //assertEquals(xmlTask.getWBS(), mppTask.getWBS());
         assertEquals(xmlTask.getWork(), mppTask.getWork());

         //assertEquals(xmlTask.getNotes().trim(), mppTask.getNotes().trim());
         //assertEquals(xmlTask.getCostVariance(), mppTask.getCostVariance());
         //assertEquals(xmlTask.getCalendar().getName(), mppTask.getCalendar().getName());
         //assertEquals(xmlTask.getSubproject(), mppTask.getSubproject());

         if (m_fileVersion > 9)
         {
            assertEquals(xmlTask.getGUID(), mppTask.getGUID());
         }
      }
   }

   /**
    * Compares sets of resources between files.
    */
   private void compareResources() throws Exception
   {
      List<Resource> xmlResources = m_xml.getResources();
      //List mppResources = m_mpp.getAllResources();
      //assertEquals(xmlResources.size(), mppResources.size());

      for (Resource xmlResource : xmlResources)
      {
         m_currentEntity = xmlResource;

         // too much variability
         if (NumberHelper.getInt(xmlResource.getUniqueID()) == 0)
         {
            continue;
         }

         // tasks with null names not read?
         if (xmlResource.getName() == null)
         {
            continue;
         }

         Resource mppResource = m_mpp.getResourceByUniqueID(xmlResource.getUniqueID());
         assertNotNull("Missing resource " + xmlResource.getName(), mppResource);

         assertEquals(xmlResource.getAccrueAt(), mppResource.getAccrueAt());

         // check this failure
         //assertEquals(xmlResource.getActualCost(), mppResource.getActualCost());
         assertEquals(xmlResource.getActualOvertimeCost(), mppResource.getActualOvertimeCost());
         assertEquals(xmlResource.getActualWork(), mppResource.getActualWork());
         //assertEquals(xmlResource.getAvailableFrom(), mppResource.getAvailableFrom());
         //assertEquals(xmlResource.getAvailableTo(), mppResource.getAvailableTo());
         //assertEquals(xmlResource.getBaselineCost(), mppResource.getBaselineCost());
         //assertEquals(xmlResource.getBaselineWork(), mppResource.getBaselineWork());
         assertEquals(xmlResource.getCode(), mppResource.getCode());

         // check this failure
         //assertEquals(xmlResource.getCost(), mppResource.getCost());
         assertEquals(xmlResource.getCost(1), mppResource.getCost(1));
         assertEquals(xmlResource.getCost(2), mppResource.getCost(2));
         assertEquals(xmlResource.getCost(3), mppResource.getCost(3));
         assertEquals(xmlResource.getCost(4), mppResource.getCost(4));
         assertEquals(xmlResource.getCost(5), mppResource.getCost(5));
         assertEquals(xmlResource.getCost(6), mppResource.getCost(6));
         assertEquals(xmlResource.getCost(7), mppResource.getCost(7));
         assertEquals(xmlResource.getCost(8), mppResource.getCost(8));
         assertEquals(xmlResource.getCost(9), mppResource.getCost(9));
         assertEquals(xmlResource.getCost(10), mppResource.getCost(10));
         //assertEquals(xmlResource.getCostPerUse(), mppResource.getCostPerUse());
         assertEquals(xmlResource.getDate(1), mppResource.getDate(1));
         assertEquals(xmlResource.getDate(2), mppResource.getDate(2));
         assertEquals(xmlResource.getDate(3), mppResource.getDate(3));
         assertEquals(xmlResource.getDate(4), mppResource.getDate(4));
         assertEquals(xmlResource.getDate(5), mppResource.getDate(5));
         assertEquals(xmlResource.getDate(6), mppResource.getDate(6));
         assertEquals(xmlResource.getDate(7), mppResource.getDate(7));
         assertEquals(xmlResource.getDate(8), mppResource.getDate(8));
         assertEquals(xmlResource.getDate(9), mppResource.getDate(9));
         assertEquals(xmlResource.getDate(10), mppResource.getDate(10));
         assertEquals(xmlResource.getDuration(1), mppResource.getDuration(1));
         assertEquals(xmlResource.getDuration(2), mppResource.getDuration(2));
         assertEquals(xmlResource.getDuration(3), mppResource.getDuration(3));
         assertEquals(xmlResource.getDuration(4), mppResource.getDuration(4));
         assertEquals(xmlResource.getDuration(5), mppResource.getDuration(5));
         assertEquals(xmlResource.getDuration(6), mppResource.getDuration(6));
         assertEquals(xmlResource.getDuration(7), mppResource.getDuration(7));
         assertEquals(xmlResource.getDuration(8), mppResource.getDuration(8));
         assertEquals(xmlResource.getDuration(9), mppResource.getDuration(9));
         assertEquals(xmlResource.getDuration(10), mppResource.getDuration(10));
         assertEquals(xmlResource.getEmailAddress(), mppResource.getEmailAddress());
         assertEquals(xmlResource.getFinish(1), mppResource.getFinish(1));
         assertEquals(xmlResource.getFinish(2), mppResource.getFinish(2));
         assertEquals(xmlResource.getFinish(3), mppResource.getFinish(3));
         assertEquals(xmlResource.getFinish(4), mppResource.getFinish(4));
         assertEquals(xmlResource.getFinish(5), mppResource.getFinish(5));
         assertEquals(xmlResource.getFinish(6), mppResource.getFinish(6));
         assertEquals(xmlResource.getFinish(7), mppResource.getFinish(7));
         assertEquals(xmlResource.getFinish(8), mppResource.getFinish(8));
         assertEquals(xmlResource.getFinish(9), mppResource.getFinish(9));
         assertEquals(xmlResource.getFinish(10), mppResource.getFinish(10));
         assertEquals(xmlResource.getGroup(), mppResource.getGroup());
         // check this failure
         //assertEquals(xmlResource.getID(), mppResource.getID());
         assertEquals(xmlResource.getInitials(), mppResource.getInitials());
         // check this failure
         //assertEquals(xmlResource.getMaxUnits(), mppResource.getMaxUnits());
         assertEquals(xmlResource.getName(), mppResource.getName());
         assertEquals(xmlResource.getNumber(1), mppResource.getNumber(1));
         assertEquals(xmlResource.getNumber(2), mppResource.getNumber(2));
         assertEquals(xmlResource.getNumber(3), mppResource.getNumber(3));
         assertEquals(xmlResource.getNumber(4), mppResource.getNumber(4));
         assertEquals(xmlResource.getNumber(5), mppResource.getNumber(5));
         assertEquals(xmlResource.getNumber(6), mppResource.getNumber(6));
         assertEquals(xmlResource.getNumber(7), mppResource.getNumber(7));
         assertEquals(xmlResource.getNumber(8), mppResource.getNumber(8));
         assertEquals(xmlResource.getNumber(9), mppResource.getNumber(9));
         assertEquals(xmlResource.getNumber(10), mppResource.getNumber(10));
         assertEquals(xmlResource.getNumber(11), mppResource.getNumber(11));
         assertEquals(xmlResource.getNumber(12), mppResource.getNumber(12));
         assertEquals(xmlResource.getNumber(13), mppResource.getNumber(13));
         assertEquals(xmlResource.getNumber(14), mppResource.getNumber(14));
         assertEquals(xmlResource.getNumber(15), mppResource.getNumber(15));
         assertEquals(xmlResource.getNumber(16), mppResource.getNumber(16));
         assertEquals(xmlResource.getNumber(17), mppResource.getNumber(17));
         assertEquals(xmlResource.getNumber(18), mppResource.getNumber(18));
         assertEquals(xmlResource.getNumber(19), mppResource.getNumber(19));
         assertEquals(xmlResource.getNumber(20), mppResource.getNumber(20));
         assertEquals(xmlResource.getOutlineCode(1), mppResource.getOutlineCode(1));
         assertEquals(xmlResource.getOutlineCode(2), mppResource.getOutlineCode(2));
         assertEquals(xmlResource.getOutlineCode(3), mppResource.getOutlineCode(3));
         assertEquals(xmlResource.getOutlineCode(4), mppResource.getOutlineCode(4));
         assertEquals(xmlResource.getOutlineCode(5), mppResource.getOutlineCode(5));
         assertEquals(xmlResource.getOutlineCode(6), mppResource.getOutlineCode(6));
         assertEquals(xmlResource.getOutlineCode(7), mppResource.getOutlineCode(7));
         assertEquals(xmlResource.getOutlineCode(8), mppResource.getOutlineCode(8));
         assertEquals(xmlResource.getOutlineCode(9), mppResource.getOutlineCode(9));
         assertEquals(xmlResource.getOutlineCode(10), mppResource.getOutlineCode(10));
         assertEquals(xmlResource.getOvertimeCost(), mppResource.getOvertimeCost());
         //assertEquals(xmlResource.getOvertimeRate(), mppResource.getOvertimeRate());
         assertEquals(xmlResource.getOvertimeWork(), mppResource.getOvertimeWork());
         // Check this failure
         //assertEquals(xmlResource.getPeakUnits(), mppResource.getPeakUnits());
         assertEquals(xmlResource.getRegularWork(), mppResource.getRegularWork());

         // Check this failure
         //assertEquals(xmlResource.getRemainingCost(), mppResource.getRemainingCost());
         assertEquals(xmlResource.getRemainingOvertimeCost(), mppResource.getRemainingOvertimeCost());
         assertEquals(xmlResource.getRemainingWork(), mppResource.getRemainingWork());
         //assertEquals(xmlResource.getStandardRate(), mppResource.getStandardRate());
         assertEquals(xmlResource.getStart(1), mppResource.getStart(1));
         assertEquals(xmlResource.getStart(2), mppResource.getStart(2));
         assertEquals(xmlResource.getStart(3), mppResource.getStart(3));
         assertEquals(xmlResource.getStart(4), mppResource.getStart(4));
         assertEquals(xmlResource.getStart(5), mppResource.getStart(5));
         assertEquals(xmlResource.getStart(6), mppResource.getStart(6));
         assertEquals(xmlResource.getStart(7), mppResource.getStart(7));
         assertEquals(xmlResource.getStart(8), mppResource.getStart(8));
         assertEquals(xmlResource.getStart(9), mppResource.getStart(9));
         assertEquals(xmlResource.getStart(10), mppResource.getStart(10));
         //assertEquals(xmlResource.getSubprojectResourceUniqueID(), mppResource.getSubprojectResourceUniqueID());
         assertEquals(xmlResource.getText(1), mppResource.getText(1));
         assertEquals(xmlResource.getText(2), mppResource.getText(2));
         assertEquals(xmlResource.getText(3), mppResource.getText(3));
         assertEquals(xmlResource.getText(4), mppResource.getText(4));
         assertEquals(xmlResource.getText(5), mppResource.getText(5));
         assertEquals(xmlResource.getText(6), mppResource.getText(6));
         assertEquals(xmlResource.getText(7), mppResource.getText(7));
         assertEquals(xmlResource.getText(8), mppResource.getText(8));
         assertEquals(xmlResource.getText(9), mppResource.getText(9));
         assertEquals(xmlResource.getText(10), mppResource.getText(10));
         assertEquals(xmlResource.getText(11), mppResource.getText(11));
         assertEquals(xmlResource.getText(12), mppResource.getText(12));
         assertEquals(xmlResource.getText(13), mppResource.getText(13));
         assertEquals(xmlResource.getText(14), mppResource.getText(14));
         assertEquals(xmlResource.getText(15), mppResource.getText(15));
         assertEquals(xmlResource.getText(16), mppResource.getText(16));
         assertEquals(xmlResource.getText(17), mppResource.getText(17));
         assertEquals(xmlResource.getText(18), mppResource.getText(18));
         assertEquals(xmlResource.getText(19), mppResource.getText(19));
         assertEquals(xmlResource.getText(20), mppResource.getText(20));
         assertEquals(xmlResource.getText(21), mppResource.getText(21));
         assertEquals(xmlResource.getText(22), mppResource.getText(22));
         assertEquals(xmlResource.getText(23), mppResource.getText(23));
         assertEquals(xmlResource.getText(24), mppResource.getText(24));
         assertEquals(xmlResource.getText(25), mppResource.getText(25));
         assertEquals(xmlResource.getText(26), mppResource.getText(26));
         assertEquals(xmlResource.getText(27), mppResource.getText(27));
         assertEquals(xmlResource.getText(28), mppResource.getText(28));
         assertEquals(xmlResource.getText(29), mppResource.getText(29));
         assertEquals(xmlResource.getText(30), mppResource.getText(30));
         // Check this failure
         //assertEquals(xmlResource.getType(), mppResource.getType());
         assertEquals(xmlResource.getWork(), mppResource.getWork());
         assertEquals(xmlResource.getFlag(1), mppResource.getFlag(1));
         assertEquals(xmlResource.getFlag(2), mppResource.getFlag(2));
         assertEquals(xmlResource.getFlag(3), mppResource.getFlag(3));
         assertEquals(xmlResource.getFlag(4), mppResource.getFlag(4));
         assertEquals(xmlResource.getFlag(5), mppResource.getFlag(5));
         assertEquals(xmlResource.getFlag(6), mppResource.getFlag(6));
         assertEquals(xmlResource.getFlag(7), mppResource.getFlag(7));
         assertEquals(xmlResource.getFlag(8), mppResource.getFlag(8));
         assertEquals(xmlResource.getFlag(9), mppResource.getFlag(9));
         assertEquals(xmlResource.getFlag(10), mppResource.getFlag(10));
         assertEquals(xmlResource.getFlag(11), mppResource.getFlag(11));
         assertEquals(xmlResource.getFlag(12), mppResource.getFlag(12));
         assertEquals(xmlResource.getFlag(13), mppResource.getFlag(13));
         assertEquals(xmlResource.getFlag(14), mppResource.getFlag(14));
         assertEquals(xmlResource.getFlag(15), mppResource.getFlag(15));
         assertEquals(xmlResource.getFlag(16), mppResource.getFlag(16));
         assertEquals(xmlResource.getFlag(17), mppResource.getFlag(17));
         assertEquals(xmlResource.getFlag(18), mppResource.getFlag(18));
         assertEquals(xmlResource.getFlag(19), mppResource.getFlag(19));
         assertEquals(xmlResource.getFlag(20), mppResource.getFlag(20));
         assertEquals(xmlResource.getNotes().trim(), mppResource.getNotes().trim());
         // check this failure
         //assertEquals(xmlResource.getCostVariance(), mppResource.getCostVariance());
         //assertEquals(xmlResource.getWorkVariance(), mppResource.getWorkVariance());

         if (m_fileVersion > 9)
         {
            assertEquals(xmlResource.getGUID(), mppResource.getGUID());
         }
      }
   }

   /**
    * Not null assertion.
    *
    * @param message failure message
    * @param object test parameter
    */
   private void assertNotNull(String message, Object object) throws Exception
   {
      if (object == null)
      {
         throw new Exception(message);
      }
   }

   /**
    * Equality assertion, derived from JUnit.
    *
    * @param expected expected value
    * @param actual actual value
    */
   private void assertEquals(Object expected, Object actual) throws Exception
   {
      if (expected == null && actual == null)
      {
         return;
      }

      if (expected != null && expected.equals(actual))
      {
         return;
      }

      throw new Exception("Expected: " + expected + " Found: " + actual + " (Current Entity=" + m_currentEntity + " MPP File Type=" + m_mpp.getProjectProperties().getMppFileType() + ")");
   }

   /**
    * String equality assertion, allowing equivalence between null and empty
    * strings.
    *
    * @param expected expected value
    * @param actual actual value
    */
   private void assertEquals(String expected, String actual) throws Exception
   {
      if (expected != null && expected.trim().isEmpty())
      {
         expected = null;
      }

      if (actual != null && actual.trim().isEmpty())
      {
         actual = null;
      }

      assertEquals(expected, (Object) actual);
   }

   /**
    * Equality assertion.
    *
    * @param expected expected value
    * @param actual actual value
    */
   private void assertEquals(boolean expected, boolean actual) throws Exception
   {
      if (expected != actual)
      {
         throw new Exception("Expected: " + expected + " Found: " + actual + " (Current Entity=" + m_currentEntity + " MPP File Type=" + m_mpp.getProjectProperties().getMppFileType() + ")");
      }
   }

   /**
    * Equality assertion, with delta allowance, derived from JUnit.
    *
    * @param expected expected value
    * @param actual actual value
    * @param delta delta allowance
    */
   private void assertEquals(double expected, double actual, double delta) throws Exception
   {
      if (Double.isInfinite(expected))
      {
         if (!(expected == actual))
         {
            throw new Exception("Expected: " + expected + " Found: " + actual + " (Current Entity=" + m_currentEntity + " MPP File Type=" + m_mpp.getProjectProperties().getMppFileType() + ")");
         }
      }
      else
      {
         if (!(Math.abs(expected - actual) <= delta))
         {
            throw new Exception("Expected: " + expected + " Found: " + actual + " (Current Entity=" + m_currentEntity + " MPP File Type=" + m_mpp.getProjectProperties().getMppFileType() + ")");
         }
      }
   }

   /**
    * Numeric equality assertion, allows null to be equated to zero.
    *
    * @param expected expected value
    * @param actual actual value
    */
   private void assertEquals(Number expected, Number actual) throws Exception
   {
      if (expected != null || actual != null)
      {
         if (expected != null && actual != null)
         {
            assertEquals(expected.doubleValue(), actual.doubleValue(), 0.05);
         }
         else
         {
            if (actual != null && actual.doubleValue() == 0)
            {
               actual = null;
            }

            if (expected != null && expected.doubleValue() == 0)
            {
               expected = null;
            }

            assertEquals(expected, (Object) actual);
         }
      }
   }

   /**
    * Duration equality assertion.
    *
    * @param expected expected value
    * @param actual actual value
    */
   private void assertEquals(Duration expected, Duration actual) throws Exception
   {
      if (expected != null || actual != null)
      {
         if (expected != null && actual != null)
         {
            if (expected.getDuration() != 0 || actual.getDuration() != 0)
            {
               if (expected.getUnits() != actual.getUnits())
               {
                  actual = actual.convertUnits(expected.getUnits(), m_mpp.getProjectProperties());
               }

               assertEquals(expected.getDuration(), actual.getDuration(), 0.99);
            }
         }
         else
         {
            if ((actual == null && expected != null && expected.getDuration() != 0) || (actual != null && actual.getDuration() != 0 && expected == null))
            {
               assertEquals(expected, (Object) actual);
            }
         }
      }
   }

   private ProjectFile m_xml;
   private ProjectFile m_mpp;
   private int m_fileVersion;
   private Object m_currentEntity;
}
