/*
 * file:       MppXmlCompare.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2005
 * date:       2005-12-05
 */

package net.sf.mpxj.junit;

import java.util.List;

import net.sf.mpxj.Duration;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Rate;
import net.sf.mpxj.Resource;
import net.sf.mpxj.Task;
import net.sf.mpxj.utility.NumberUtility;

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

      //compareHeaders
      compareResources();
      compareTasks();
      //compareAssignments
   }

   /**
    * Compares sets of tasks between files.
    *
    * @throws Exception
    */
   private void compareTasks() throws Exception
   {
      List<Task> xmlTasks = m_xml.getAllTasks();

      // won't always match - tasks with blank names not preserved?
      //List mppTasks = m_mpp.getAllTasks();
      //assertEquals(xmlTasks.size(), mppTasks.size());

      for (Task xmlTask : xmlTasks)
      {
         // too much variability
         if (NumberUtility.getInt(xmlTask.getUniqueID()) == 0)
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
         assertEquals(xmlTask.getCost1(), mppTask.getCost1());
         assertEquals(xmlTask.getCost2(), mppTask.getCost2());
         assertEquals(xmlTask.getCost3(), mppTask.getCost3());
         assertEquals(xmlTask.getCost4(), mppTask.getCost4());
         assertEquals(xmlTask.getCost5(), mppTask.getCost5());
         assertEquals(xmlTask.getCost6(), mppTask.getCost6());
         assertEquals(xmlTask.getCost7(), mppTask.getCost7());
         assertEquals(xmlTask.getCost8(), mppTask.getCost8());
         assertEquals(xmlTask.getCost9(), mppTask.getCost9());
         assertEquals(xmlTask.getCost10(), mppTask.getCost10());
         assertEquals(xmlTask.getCreateDate(), mppTask.getCreateDate());
         assertEquals(xmlTask.getDate1(), mppTask.getDate1());
         assertEquals(xmlTask.getDate2(), mppTask.getDate2());
         assertEquals(xmlTask.getDate3(), mppTask.getDate3());
         assertEquals(xmlTask.getDate4(), mppTask.getDate4());
         assertEquals(xmlTask.getDate5(), mppTask.getDate5());
         assertEquals(xmlTask.getDate6(), mppTask.getDate6());
         assertEquals(xmlTask.getDate7(), mppTask.getDate7());
         assertEquals(xmlTask.getDate8(), mppTask.getDate8());
         assertEquals(xmlTask.getDate9(), mppTask.getDate9());
         assertEquals(xmlTask.getDate10(), mppTask.getDate10());
         assertEquals(xmlTask.getDeadline(), mppTask.getDeadline());
         assertEquals(xmlTask.getDuration(), mppTask.getDuration());
         assertEquals(xmlTask.getDuration1(), mppTask.getDuration1());
         assertEquals(xmlTask.getDuration2(), mppTask.getDuration2());
         assertEquals(xmlTask.getDuration3(), mppTask.getDuration3());
         assertEquals(xmlTask.getDuration4(), mppTask.getDuration4());
         assertEquals(xmlTask.getDuration5(), mppTask.getDuration5());
         assertEquals(xmlTask.getDuration6(), mppTask.getDuration6());
         assertEquals(xmlTask.getDuration7(), mppTask.getDuration7());
         assertEquals(xmlTask.getDuration8(), mppTask.getDuration8());
         assertEquals(xmlTask.getDuration9(), mppTask.getDuration9());
         assertEquals(xmlTask.getDuration10(), mppTask.getDuration10());
         assertEquals(xmlTask.getEarlyFinish(), mppTask.getEarlyFinish());
         assertEquals(xmlTask.getEarlyStart(), mppTask.getEarlyStart());
         assertEquals(xmlTask.getEffortDriven(), mppTask.getEffortDriven());
         assertEquals(xmlTask.getEstimated(), mppTask.getEstimated());
         // check
         //assertEquals(xmlTask.getExpanded(), mppTask.getExpanded());
         assertEquals(xmlTask.getFinish(), mppTask.getFinish());
         assertEquals(xmlTask.getFinish1(), mppTask.getFinish1());
         assertEquals(xmlTask.getFinish2(), mppTask.getFinish2());
         assertEquals(xmlTask.getFinish3(), mppTask.getFinish3());
         assertEquals(xmlTask.getFinish4(), mppTask.getFinish4());
         assertEquals(xmlTask.getFinish5(), mppTask.getFinish5());
         assertEquals(xmlTask.getFinish6(), mppTask.getFinish6());
         assertEquals(xmlTask.getFinish7(), mppTask.getFinish7());
         assertEquals(xmlTask.getFinish8(), mppTask.getFinish8());
         assertEquals(xmlTask.getFinish9(), mppTask.getFinish9());
         assertEquals(xmlTask.getFinish10(), mppTask.getFinish10());
         assertEquals(xmlTask.getFixedCost(), mppTask.getFixedCost());
         assertEquals(xmlTask.getFixedCostAccrual(), mppTask.getFixedCostAccrual());
         assertEquals(xmlTask.getFlag1(), mppTask.getFlag1());
         assertEquals(xmlTask.getFlag2(), mppTask.getFlag2());
         assertEquals(xmlTask.getFlag3(), mppTask.getFlag3());
         assertEquals(xmlTask.getFlag4(), mppTask.getFlag4());
         assertEquals(xmlTask.getFlag5(), mppTask.getFlag5());
         assertEquals(xmlTask.getFlag6(), mppTask.getFlag6());
         assertEquals(xmlTask.getFlag7(), mppTask.getFlag7());
         assertEquals(xmlTask.getFlag8(), mppTask.getFlag8());
         assertEquals(xmlTask.getFlag9(), mppTask.getFlag9());
         assertEquals(xmlTask.getFlag10(), mppTask.getFlag10());
         assertEquals(xmlTask.getFlag11(), mppTask.getFlag11());
         assertEquals(xmlTask.getFlag12(), mppTask.getFlag12());
         assertEquals(xmlTask.getFlag13(), mppTask.getFlag13());
         assertEquals(xmlTask.getFlag14(), mppTask.getFlag14());
         assertEquals(xmlTask.getFlag15(), mppTask.getFlag15());
         assertEquals(xmlTask.getFlag16(), mppTask.getFlag16());
         assertEquals(xmlTask.getFlag17(), mppTask.getFlag17());
         assertEquals(xmlTask.getFlag18(), mppTask.getFlag18());
         assertEquals(xmlTask.getFlag19(), mppTask.getFlag19());
         assertEquals(xmlTask.getFlag20(), mppTask.getFlag20());
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
         assertEquals(xmlTask.getNumber1(), mppTask.getNumber1());
         assertEquals(xmlTask.getNumber2(), mppTask.getNumber2());
         assertEquals(xmlTask.getNumber3(), mppTask.getNumber3());
         assertEquals(xmlTask.getNumber4(), mppTask.getNumber4());
         assertEquals(xmlTask.getNumber5(), mppTask.getNumber5());
         assertEquals(xmlTask.getNumber6(), mppTask.getNumber6());
         assertEquals(xmlTask.getNumber7(), mppTask.getNumber7());
         assertEquals(xmlTask.getNumber8(), mppTask.getNumber8());
         assertEquals(xmlTask.getNumber9(), mppTask.getNumber9());
         assertEquals(xmlTask.getNumber10(), mppTask.getNumber10());
         assertEquals(xmlTask.getNumber11(), mppTask.getNumber11());
         assertEquals(xmlTask.getNumber12(), mppTask.getNumber12());
         assertEquals(xmlTask.getNumber13(), mppTask.getNumber13());
         assertEquals(xmlTask.getNumber14(), mppTask.getNumber14());
         assertEquals(xmlTask.getNumber15(), mppTask.getNumber15());
         assertEquals(xmlTask.getNumber16(), mppTask.getNumber16());
         assertEquals(xmlTask.getNumber17(), mppTask.getNumber17());
         assertEquals(xmlTask.getNumber18(), mppTask.getNumber18());
         assertEquals(xmlTask.getNumber19(), mppTask.getNumber19());
         assertEquals(xmlTask.getNumber20(), mppTask.getNumber20());
         assertEquals(xmlTask.getOutlineCode1(), mppTask.getOutlineCode1());
         assertEquals(xmlTask.getOutlineCode2(), mppTask.getOutlineCode2());
         assertEquals(xmlTask.getOutlineCode3(), mppTask.getOutlineCode3());
         assertEquals(xmlTask.getOutlineCode4(), mppTask.getOutlineCode4());
         assertEquals(xmlTask.getOutlineCode5(), mppTask.getOutlineCode5());
         assertEquals(xmlTask.getOutlineCode6(), mppTask.getOutlineCode6());
         assertEquals(xmlTask.getOutlineCode7(), mppTask.getOutlineCode7());
         assertEquals(xmlTask.getOutlineCode8(), mppTask.getOutlineCode8());
         assertEquals(xmlTask.getOutlineCode9(), mppTask.getOutlineCode9());
         assertEquals(xmlTask.getOutlineCode10(), mppTask.getOutlineCode10());
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
         assertEquals(xmlTask.getStart1(), mppTask.getStart1());
         assertEquals(xmlTask.getStart2(), mppTask.getStart2());
         assertEquals(xmlTask.getStart3(), mppTask.getStart3());
         assertEquals(xmlTask.getStart4(), mppTask.getStart4());
         assertEquals(xmlTask.getStart5(), mppTask.getStart5());
         assertEquals(xmlTask.getStart6(), mppTask.getStart6());
         assertEquals(xmlTask.getStart7(), mppTask.getStart7());
         assertEquals(xmlTask.getStart8(), mppTask.getStart8());
         assertEquals(xmlTask.getStart9(), mppTask.getStart9());
         assertEquals(xmlTask.getStart10(), mppTask.getStart10());
         assertEquals(xmlTask.getStop(), mppTask.getStop());
         // Subprojects not implemented in XML
         //assertEquals(xmlTask.getSubprojectTaskUniqueID(), mppTask.getSubprojectTaskUniqueID());
         assertEquals(xmlTask.getText1(), mppTask.getText1());
         assertEquals(xmlTask.getText2(), mppTask.getText2());
         assertEquals(xmlTask.getText3(), mppTask.getText3());
         assertEquals(xmlTask.getText4(), mppTask.getText4());
         assertEquals(xmlTask.getText5(), mppTask.getText5());
         assertEquals(xmlTask.getText6(), mppTask.getText6());
         assertEquals(xmlTask.getText7(), mppTask.getText7());
         assertEquals(xmlTask.getText8(), mppTask.getText8());
         assertEquals(xmlTask.getText9(), mppTask.getText9());
         assertEquals(xmlTask.getText10(), mppTask.getText10());
         assertEquals(xmlTask.getText11(), mppTask.getText11());
         assertEquals(xmlTask.getText12(), mppTask.getText12());
         assertEquals(xmlTask.getText13(), mppTask.getText13());
         assertEquals(xmlTask.getText14(), mppTask.getText14());
         assertEquals(xmlTask.getText15(), mppTask.getText15());
         assertEquals(xmlTask.getText16(), mppTask.getText16());
         assertEquals(xmlTask.getText17(), mppTask.getText17());
         assertEquals(xmlTask.getText18(), mppTask.getText18());
         assertEquals(xmlTask.getText19(), mppTask.getText19());
         assertEquals(xmlTask.getText20(), mppTask.getText20());
         assertEquals(xmlTask.getText21(), mppTask.getText21());
         assertEquals(xmlTask.getText22(), mppTask.getText22());
         assertEquals(xmlTask.getText23(), mppTask.getText23());
         assertEquals(xmlTask.getText24(), mppTask.getText24());
         assertEquals(xmlTask.getText25(), mppTask.getText25());
         assertEquals(xmlTask.getText26(), mppTask.getText26());
         assertEquals(xmlTask.getText27(), mppTask.getText27());
         assertEquals(xmlTask.getText28(), mppTask.getText28());
         assertEquals(xmlTask.getText29(), mppTask.getText29());
         assertEquals(xmlTask.getText30(), mppTask.getText30());
         assertEquals(xmlTask.getType(), mppTask.getType());
         //assertEquals(xmlTask.getWBS(), mppTask.getWBS());
         assertEquals(xmlTask.getWork(), mppTask.getWork());

         //assertEquals(xmlTask.getNotes().trim(), mppTask.getNotes().trim());
         //assertEquals(xmlTask.getCostVariance(), mppTask.getCostVariance());
         //assertEquals(xmlTask.getCalendar().getName(), mppTask.getCalendar().getName());
         //assertEquals(xmlTask.getSubproject(), mppTask.getSubproject());
      }
   }

   /**
    * Compares sets of resources between files.
    *
    * @throws Exception
    */
   private void compareResources() throws Exception
   {
      List<Resource> xmlResources = m_xml.getAllResources();
      //List mppResources = m_mpp.getAllResources();
      //assertEquals(xmlResources.size(), mppResources.size());

      for (Resource xmlResource : xmlResources)
      {
         // too much variability
         if (NumberUtility.getInt(xmlResource.getUniqueID()) == 0)
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
         assertEquals(xmlResource.getAvailableFrom(), mppResource.getAvailableFrom());
         assertEquals(xmlResource.getAvailableTo(), mppResource.getAvailableTo());
         //assertEquals(xmlResource.getBaselineCost(), mppResource.getBaselineCost());
         //assertEquals(xmlResource.getBaselineWork(), mppResource.getBaselineWork());
         assertEquals(xmlResource.getCode(), mppResource.getCode());

         // check this failure
         //assertEquals(xmlResource.getCost(), mppResource.getCost());
         assertEquals(xmlResource.getCost1(), mppResource.getCost1());
         assertEquals(xmlResource.getCost2(), mppResource.getCost2());
         assertEquals(xmlResource.getCost3(), mppResource.getCost3());
         assertEquals(xmlResource.getCost4(), mppResource.getCost4());
         assertEquals(xmlResource.getCost5(), mppResource.getCost5());
         assertEquals(xmlResource.getCost6(), mppResource.getCost6());
         assertEquals(xmlResource.getCost7(), mppResource.getCost7());
         assertEquals(xmlResource.getCost8(), mppResource.getCost8());
         assertEquals(xmlResource.getCost9(), mppResource.getCost9());
         assertEquals(xmlResource.getCost10(), mppResource.getCost10());
         assertEquals(xmlResource.getCostPerUse(), mppResource.getCostPerUse());
         assertEquals(xmlResource.getDate1(), mppResource.getDate1());
         assertEquals(xmlResource.getDate2(), mppResource.getDate2());
         assertEquals(xmlResource.getDate3(), mppResource.getDate3());
         assertEquals(xmlResource.getDate4(), mppResource.getDate4());
         assertEquals(xmlResource.getDate5(), mppResource.getDate5());
         assertEquals(xmlResource.getDate6(), mppResource.getDate6());
         assertEquals(xmlResource.getDate7(), mppResource.getDate7());
         assertEquals(xmlResource.getDate8(), mppResource.getDate8());
         assertEquals(xmlResource.getDate9(), mppResource.getDate9());
         assertEquals(xmlResource.getDate10(), mppResource.getDate10());
         assertEquals(xmlResource.getDuration1(), mppResource.getDuration1());
         assertEquals(xmlResource.getDuration2(), mppResource.getDuration2());
         assertEquals(xmlResource.getDuration3(), mppResource.getDuration3());
         assertEquals(xmlResource.getDuration4(), mppResource.getDuration4());
         assertEquals(xmlResource.getDuration5(), mppResource.getDuration5());
         assertEquals(xmlResource.getDuration6(), mppResource.getDuration6());
         assertEquals(xmlResource.getDuration7(), mppResource.getDuration7());
         assertEquals(xmlResource.getDuration8(), mppResource.getDuration8());
         assertEquals(xmlResource.getDuration9(), mppResource.getDuration9());
         assertEquals(xmlResource.getDuration10(), mppResource.getDuration10());
         assertEquals(xmlResource.getEmailAddress(), mppResource.getEmailAddress());
         assertEquals(xmlResource.getFinish1(), mppResource.getFinish1());
         assertEquals(xmlResource.getFinish2(), mppResource.getFinish2());
         assertEquals(xmlResource.getFinish3(), mppResource.getFinish3());
         assertEquals(xmlResource.getFinish4(), mppResource.getFinish4());
         assertEquals(xmlResource.getFinish5(), mppResource.getFinish5());
         assertEquals(xmlResource.getFinish6(), mppResource.getFinish6());
         assertEquals(xmlResource.getFinish7(), mppResource.getFinish7());
         assertEquals(xmlResource.getFinish8(), mppResource.getFinish8());
         assertEquals(xmlResource.getFinish9(), mppResource.getFinish9());
         assertEquals(xmlResource.getFinish10(), mppResource.getFinish10());
         assertEquals(xmlResource.getGroup(), mppResource.getGroup());
         // check this failure
         //assertEquals(xmlResource.getID(), mppResource.getID());
         assertEquals(xmlResource.getInitials(), mppResource.getInitials());
         // check this failure
         //assertEquals(xmlResource.getMaxUnits(), mppResource.getMaxUnits());
         assertEquals(xmlResource.getName(), mppResource.getName());
         assertEquals(xmlResource.getNumber1(), mppResource.getNumber1());
         assertEquals(xmlResource.getNumber2(), mppResource.getNumber2());
         assertEquals(xmlResource.getNumber3(), mppResource.getNumber3());
         assertEquals(xmlResource.getNumber4(), mppResource.getNumber4());
         assertEquals(xmlResource.getNumber5(), mppResource.getNumber5());
         assertEquals(xmlResource.getNumber6(), mppResource.getNumber6());
         assertEquals(xmlResource.getNumber7(), mppResource.getNumber7());
         assertEquals(xmlResource.getNumber8(), mppResource.getNumber8());
         assertEquals(xmlResource.getNumber9(), mppResource.getNumber9());
         assertEquals(xmlResource.getNumber10(), mppResource.getNumber10());
         assertEquals(xmlResource.getNumber11(), mppResource.getNumber11());
         assertEquals(xmlResource.getNumber12(), mppResource.getNumber12());
         assertEquals(xmlResource.getNumber13(), mppResource.getNumber13());
         assertEquals(xmlResource.getNumber14(), mppResource.getNumber14());
         assertEquals(xmlResource.getNumber15(), mppResource.getNumber15());
         assertEquals(xmlResource.getNumber16(), mppResource.getNumber16());
         assertEquals(xmlResource.getNumber17(), mppResource.getNumber17());
         assertEquals(xmlResource.getNumber18(), mppResource.getNumber18());
         assertEquals(xmlResource.getNumber19(), mppResource.getNumber19());
         assertEquals(xmlResource.getNumber20(), mppResource.getNumber20());
         assertEquals(xmlResource.getOutlineCode1(), mppResource.getOutlineCode1());
         assertEquals(xmlResource.getOutlineCode2(), mppResource.getOutlineCode2());
         assertEquals(xmlResource.getOutlineCode3(), mppResource.getOutlineCode3());
         assertEquals(xmlResource.getOutlineCode4(), mppResource.getOutlineCode4());
         assertEquals(xmlResource.getOutlineCode5(), mppResource.getOutlineCode5());
         assertEquals(xmlResource.getOutlineCode6(), mppResource.getOutlineCode6());
         assertEquals(xmlResource.getOutlineCode7(), mppResource.getOutlineCode7());
         assertEquals(xmlResource.getOutlineCode8(), mppResource.getOutlineCode8());
         assertEquals(xmlResource.getOutlineCode9(), mppResource.getOutlineCode9());
         assertEquals(xmlResource.getOutlineCode10(), mppResource.getOutlineCode10());
         assertEquals(xmlResource.getOvertimeCost(), mppResource.getOvertimeCost());
         assertEquals(xmlResource.getOvertimeRate(), mppResource.getOvertimeRate());
         assertEquals(xmlResource.getOvertimeWork(), mppResource.getOvertimeWork());
         // Check this failure
         //assertEquals(xmlResource.getPeakUnits(), mppResource.getPeakUnits());
         assertEquals(xmlResource.getRegularWork(), mppResource.getRegularWork());

         // Check this failure
         //assertEquals(xmlResource.getRemainingCost(), mppResource.getRemainingCost());
         assertEquals(xmlResource.getRemainingOvertimeCost(), mppResource.getRemainingOvertimeCost());
         assertEquals(xmlResource.getRemainingWork(), mppResource.getRemainingWork());
         assertEquals(xmlResource.getStandardRate(), mppResource.getStandardRate());
         assertEquals(xmlResource.getStart1(), mppResource.getStart1());
         assertEquals(xmlResource.getStart2(), mppResource.getStart2());
         assertEquals(xmlResource.getStart3(), mppResource.getStart3());
         assertEquals(xmlResource.getStart4(), mppResource.getStart4());
         assertEquals(xmlResource.getStart5(), mppResource.getStart5());
         assertEquals(xmlResource.getStart6(), mppResource.getStart6());
         assertEquals(xmlResource.getStart7(), mppResource.getStart7());
         assertEquals(xmlResource.getStart8(), mppResource.getStart8());
         assertEquals(xmlResource.getStart9(), mppResource.getStart9());
         assertEquals(xmlResource.getStart10(), mppResource.getStart10());
         //assertEquals(xmlResource.getSubprojectResourceUniqueID(), mppResource.getSubprojectResourceUniqueID());
         assertEquals(xmlResource.getText1(), mppResource.getText1());
         assertEquals(xmlResource.getText2(), mppResource.getText2());
         assertEquals(xmlResource.getText3(), mppResource.getText3());
         assertEquals(xmlResource.getText4(), mppResource.getText4());
         assertEquals(xmlResource.getText5(), mppResource.getText5());
         assertEquals(xmlResource.getText6(), mppResource.getText6());
         assertEquals(xmlResource.getText7(), mppResource.getText7());
         assertEquals(xmlResource.getText8(), mppResource.getText8());
         assertEquals(xmlResource.getText9(), mppResource.getText9());
         assertEquals(xmlResource.getText10(), mppResource.getText10());
         assertEquals(xmlResource.getText11(), mppResource.getText11());
         assertEquals(xmlResource.getText12(), mppResource.getText12());
         assertEquals(xmlResource.getText13(), mppResource.getText13());
         assertEquals(xmlResource.getText14(), mppResource.getText14());
         assertEquals(xmlResource.getText15(), mppResource.getText15());
         assertEquals(xmlResource.getText16(), mppResource.getText16());
         assertEquals(xmlResource.getText17(), mppResource.getText17());
         assertEquals(xmlResource.getText18(), mppResource.getText18());
         assertEquals(xmlResource.getText19(), mppResource.getText19());
         assertEquals(xmlResource.getText20(), mppResource.getText20());
         assertEquals(xmlResource.getText21(), mppResource.getText21());
         assertEquals(xmlResource.getText22(), mppResource.getText22());
         assertEquals(xmlResource.getText23(), mppResource.getText23());
         assertEquals(xmlResource.getText24(), mppResource.getText24());
         assertEquals(xmlResource.getText25(), mppResource.getText25());
         assertEquals(xmlResource.getText26(), mppResource.getText26());
         assertEquals(xmlResource.getText27(), mppResource.getText27());
         assertEquals(xmlResource.getText28(), mppResource.getText28());
         assertEquals(xmlResource.getText29(), mppResource.getText29());
         assertEquals(xmlResource.getText30(), mppResource.getText30());
         // Check this failure
         //assertEquals(xmlResource.getType(), mppResource.getType());
         assertEquals(xmlResource.getWork(), mppResource.getWork());
         assertEquals(xmlResource.getFlag1(), mppResource.getFlag1());
         assertEquals(xmlResource.getFlag2(), mppResource.getFlag2());
         assertEquals(xmlResource.getFlag3(), mppResource.getFlag3());
         assertEquals(xmlResource.getFlag4(), mppResource.getFlag4());
         assertEquals(xmlResource.getFlag5(), mppResource.getFlag5());
         assertEquals(xmlResource.getFlag6(), mppResource.getFlag6());
         assertEquals(xmlResource.getFlag7(), mppResource.getFlag7());
         assertEquals(xmlResource.getFlag8(), mppResource.getFlag8());
         assertEquals(xmlResource.getFlag9(), mppResource.getFlag9());
         assertEquals(xmlResource.getFlag10(), mppResource.getFlag10());
         assertEquals(xmlResource.getFlag11(), mppResource.getFlag11());
         assertEquals(xmlResource.getFlag12(), mppResource.getFlag12());
         assertEquals(xmlResource.getFlag13(), mppResource.getFlag13());
         assertEquals(xmlResource.getFlag14(), mppResource.getFlag14());
         assertEquals(xmlResource.getFlag15(), mppResource.getFlag15());
         assertEquals(xmlResource.getFlag16(), mppResource.getFlag16());
         assertEquals(xmlResource.getFlag17(), mppResource.getFlag17());
         assertEquals(xmlResource.getFlag18(), mppResource.getFlag18());
         assertEquals(xmlResource.getFlag19(), mppResource.getFlag19());
         assertEquals(xmlResource.getFlag20(), mppResource.getFlag20());
         assertEquals(xmlResource.getNotes().trim(), mppResource.getNotes().trim());
         // check this failure
         //assertEquals(xmlResource.getCostVariance(), mppResource.getCostVariance());
         //assertEquals(xmlResource.getWorkVariance(), mppResource.getWorkVariance());
      }
   }

   /**
    * Not null assertion.
    *
    * @param message failure message
    * @param object test parameter
    * @throws Exception
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
    * @throws Exception
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

      throw new Exception("Expected: " + expected + " Found: " + actual);
   }

   /**
    * String equality assertion, allowing equivalence between null and empty
    * strings.
    *
    * @param expected expected value
    * @param actual actual value
    * @throws Exception
    */
   private void assertEquals(String expected, String actual) throws Exception
   {
      if (expected != null && expected.trim().length() == 0)
      {
         expected = null;
      }

      if (actual != null && actual.trim().length() == 0)
      {
         actual = null;
      }

      assertEquals((Object) expected, (Object) actual);
   }

   /**
    * Equality assertion.
    *
    * @param expected expected value
    * @param actual actual value
    * @throws Exception
    */
   private void assertEquals(boolean expected, boolean actual) throws Exception
   {
      if (expected != actual)
      {
         throw new Exception("Expected: " + expected + " Found: " + actual);
      }
   }

   /**
    * Equality assertion, with delta allowance, derived from JUnit.
    *
    * @param expected expected value
    * @param actual actual value
    * @param delta delta allowance
    * @throws Exception
    */
   private void assertEquals(double expected, double actual, double delta) throws Exception
   {
      if (Double.isInfinite(expected))
      {
         if (!(expected == actual))
         {
            throw new Exception("Expected: " + expected + " Found: " + actual);
         }
      }
      else
      {
         if (!(Math.abs(expected - actual) <= delta))
         {
            throw new Exception("Expected: " + expected + " Found: " + actual);
         }
      }
   }

   /**
    * Numeric equality assertion, allows null to be equated to zero.
    *
    * @param expected expected value
    * @param actual actual value
    * @throws Exception
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

            assertEquals((Object) expected, (Object) actual);
         }
      }
   }

   /**
    * Duration equality assertion.
    *
    * @param expected expected value
    * @param actual actual value
    * @throws Exception
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
                  actual = actual.convertUnits(expected.getUnits(), m_mpp.getProjectHeader());
               }

               assertEquals(expected.getDuration(), actual.getDuration(), 0.99);
            }
         }
         else
         {
            if ((actual == null && expected != null && expected.getDuration() != 0) || (actual != null && actual.getDuration() != 0 && expected == null))
            {
               assertEquals((Object) expected, (Object) actual);
            }
         }
      }
   }

   /**
    * Rate equality assertion.
    *
    * @param expected expected value
    * @param actual actual value
    * @throws Exception
    */
   private void assertEquals(Rate expected, Rate actual) throws Exception
   {
      if (expected != null && actual != null && expected.getUnits() == actual.getUnits())
      {
         assertEquals(expected.getAmount(), actual.getAmount(), 0.99);
      }
      else
      {
         if (expected != null && expected.getAmount() == 0)
         {
            expected = null;
         }

         if (actual != null && actual.getAmount() == 0)
         {
            actual = null;
         }

         assertEquals((Object) expected, (Object) actual);
      }
   }

   private ProjectFile m_xml;
   private ProjectFile m_mpp;
}
