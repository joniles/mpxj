/*
 * file:       MPP8File.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2002-2003
 * date:       08/05/2003
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
  
package com.tapsterrock.mpp;

import java.io.IOException;

import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.DocumentInputStream;

import com.tapsterrock.mpx.AccrueType;
import com.tapsterrock.mpx.BaseCalendar;
import com.tapsterrock.mpx.ConstraintType;
import com.tapsterrock.mpx.MPXDuration;
import com.tapsterrock.mpx.MPXException;
import com.tapsterrock.mpx.MPXRate;
import com.tapsterrock.mpx.Priority;
import com.tapsterrock.mpx.Relation;
import com.tapsterrock.mpx.Resource;
import com.tapsterrock.mpx.Task;
import com.tapsterrock.mpx.TimeUnit;

/**
 * This class is used to represent a Microsoft Project MPP8 file. This
 * implementation allows the file to be read, and the data it contains
 * exported as a set of MPX objects. These objects can be interrogated
 * to retrieve any required data, or stored as an MPX file.
 */
final class MPP8File
{
   /**
    * This method is used to process an MPP8 file. This is the file format
    * used by Project 98.
    * 
    * @param file Parent MPX file
    * @param root Root of the POI file system.
    * @throws MPXException
    * @throws IOException
    */
   static void process (MPPFile file, DirectoryEntry root)
      throws MPXException, IOException
   {
      //
      // Retrieve the project directory
      //
      DirectoryEntry projectDir = (DirectoryEntry)root.getEntry ("   1");

      processCalendarData (file, projectDir);
             
      processResourceData (file, projectDir);

      processTaskData (file, projectDir);
      
      processConstraintData (file, projectDir);      
      
      processAssignmentData (file, projectDir);
   }


   /**
    * This method extracts and collates calendar data.
    * 
    * TODO work out the calendar data format
    * 
    * @param file Parent MPX file
    * @param projectDir Project data directory
    * @throws MPXException
    * @throws IOException
    */   
   private static void processCalendarData (MPPFile file,  DirectoryEntry projectDir)
      throws MPXException, IOException
   {
      DirectoryEntry calDir = (DirectoryEntry)projectDir.getEntry ("TBkndCal");      
      FixFix calendarFixedData = new FixFix (36, new DocumentInputStream (((DocumentEntry)calDir.getEntry("FixFix   0"))));
      FixDeferFix calendarVarData = new FixDeferFix (new DocumentInputStream (((DocumentEntry)calDir.getEntry("FixDeferFix   0"))));      

      int calendars = calendarFixedData.getItemCount();      
      byte[] data;
      String name;
      BaseCalendar cal;
            
      for (int loop=0; loop < calendars; loop++)
      {
         data = calendarFixedData.getByteArrayValue(loop);
         name = calendarVarData.getUnicodeString(getOffset(data, 20));
         
         //
         // Ignore calendars with the same name as existing calendars
         //
         if (file.getBaseCalendar(name) != null)
         {
            continue;
         }
             
         //
         // Until we have worked out the file structure, add a default
         // calendar for each one we find in the file.
         //                                 
         cal = file.addDefaultBaseCalendar();
         cal.setName (name);
      }
   }


   /**
    * This method extracts and collates task data.
    * 
    * TODO extract extended attributes
    * 
    * @param file Parent MPX file
    * @param projectDir Project data directory
    * @throws MPXException
    * @throws IOException
    */   
   private static void processTaskData (MPPFile file,  DirectoryEntry projectDir)
      throws MPXException, IOException
   {
      DirectoryEntry taskDir = (DirectoryEntry)projectDir.getEntry ("TBkndTask");
      FixFix taskFixedData = new FixFix (316, new DocumentInputStream (((DocumentEntry)taskDir.getEntry("FixFix   0"))));
      FixDeferFix taskVarData = null;
      ExtendedData taskExtData = null;
           
      int tasks = taskFixedData.getItemCount();
      byte[] data;
      int id;
      Task task;
      String notes;
      
      for (int loop=0; loop < tasks; loop++)
      {
         data = taskFixedData.getByteArrayValue(loop);
         
         //
         // Test for a valid unique id
         //         
         id = MPPUtility.getInt(data, 0);
         if (id < 1)
         {
            continue;
         }

         //
         // Test to ensure this resource has not been deleted
         //         
         if (MPPUtility.getShort(data, 272) != 0)
         {
            continue;
         }


         //
         // Load the var data if we have not already done so
         //
         if (taskVarData == null)
         {
            taskVarData = new FixDeferFix (new DocumentInputStream (((DocumentEntry)taskDir.getEntry("FixDeferFix   0"))));
            taskExtData = new ExtendedData (taskVarData, getOffset(data, 312));
         }
                                       
         task = file.addTask();
                                      
         task.setActualCost(new Double (((double)MPPUtility.getLong6(data, 234)) / 100));
         task.setActualDuration(MPPUtility.getDuration (MPPUtility.getInt (data, 74), MPPUtility.getDurationUnits(MPPUtility.getShort (data, 72))));
         task.setActualFinish(MPPUtility.getTimestamp (data, 108));
         task.setActualStart(MPPUtility.getTimestamp (data, 104));
         // TODO sort out the actual work value
//         task.setActualWork(new MPXDuration (MPPUtility.getDouble (data, 184)/60000, TimeUnit.HOURS));
         task.setBaselineCost(new Double ((double)MPPUtility.getLong6 (data, 246) / 100));
         task.setBaselineDuration(MPPUtility.getDuration (MPPUtility.getInt (data, 82), MPPUtility.getDurationUnits (MPPUtility.getShort (data, 72))));
         task.setBaselineFinish(MPPUtility.getTimestamp (data, 116));
         task.setBaselineStart(MPPUtility.getTimestamp (data, 112));
         task.setBaselineWork(MPPUtility.getDuration(((double)MPPUtility.getLong6(data, 174))/100, TimeUnit.HOURS));
         //task.setBCWP(); // Calculated value
         //task.setBCWS(); // Calculated value
         //task.setConfirmed(); // Calculated value
         task.setConstraintDate (MPPUtility.getTimestamp (data, 120));
         task.setConstraintType (ConstraintType.getInstance (MPPUtility.getShort (data, 88)));
         task.setContact(taskExtData.getString (TASK_CONTACT));
         task.setCost(new Double (((double)MPPUtility.getLong6(data, 222)) / 100));
         task.setCost1(new Double (((double)taskExtData.getLong (TASK_COST1)) / 100));
         task.setCost2(new Double (((double)taskExtData.getLong (TASK_COST2)) / 100));
         task.setCost3(new Double (((double)taskExtData.getLong (TASK_COST3)) / 100));
         task.setCost4(new Double (((double)taskExtData.getLong (TASK_COST4)) / 100));
         task.setCost5(new Double (((double)taskExtData.getLong (TASK_COST5)) / 100));
         task.setCost6(new Double (((double)taskExtData.getLong (TASK_COST6)) / 100));
         task.setCost7(new Double (((double)taskExtData.getLong (TASK_COST7)) / 100));
         task.setCost8(new Double (((double)taskExtData.getLong (TASK_COST8)) / 100));
         task.setCost9(new Double (((double)taskExtData.getLong (TASK_COST9)) / 100));
         task.setCost10(new Double (((double)taskExtData.getLong (TASK_COST10)) / 100));         
         task.setCreated(MPPUtility.getTimestamp (data, 138));
         //task.setCritical(); // Calculated value
         //task.setCV(); // Calculated value
         //task.setDelay(); // Not in MSP98?
         task.setDate1(taskExtData.getTimestamp(TASK_DATE1));
         task.setDate2(taskExtData.getTimestamp(TASK_DATE2));         
         task.setDate3(taskExtData.getTimestamp(TASK_DATE3));
         task.setDate4(taskExtData.getTimestamp(TASK_DATE4));
         task.setDate5(taskExtData.getTimestamp(TASK_DATE5));
         task.setDate6(taskExtData.getTimestamp(TASK_DATE6));
         task.setDate7(taskExtData.getTimestamp(TASK_DATE7));
         task.setDate8(taskExtData.getTimestamp(TASK_DATE8));
         task.setDate9(taskExtData.getTimestamp(TASK_DATE9));
         task.setDate10(taskExtData.getTimestamp(TASK_DATE10));
         task.setDuration (MPPUtility.getDuration (MPPUtility.getInt (data, 68), MPPUtility.getDurationUnits(MPPUtility.getShort (data, 72))));
         task.setDuration1(MPPUtility.getDuration (taskExtData.getInt (TASK_DURATION1), MPPUtility.getDurationUnits(taskExtData.getShort (TASK_DURATION1_UNITS))));
         task.setDuration2(MPPUtility.getDuration (taskExtData.getInt (TASK_DURATION2), MPPUtility.getDurationUnits(taskExtData.getShort (TASK_DURATION2_UNITS))));
         task.setDuration3(MPPUtility.getDuration (taskExtData.getInt (TASK_DURATION3), MPPUtility.getDurationUnits(taskExtData.getShort (TASK_DURATION3_UNITS))));
         task.setDuration4(MPPUtility.getDuration (taskExtData.getInt (TASK_DURATION4), MPPUtility.getDurationUnits(taskExtData.getShort (TASK_DURATION4_UNITS))));
         task.setDuration5(MPPUtility.getDuration (taskExtData.getInt (TASK_DURATION5), MPPUtility.getDurationUnits(taskExtData.getShort (TASK_DURATION5_UNITS))));
         task.setDuration6(MPPUtility.getDuration (taskExtData.getInt (TASK_DURATION6), MPPUtility.getDurationUnits(taskExtData.getShort (TASK_DURATION6_UNITS))));
         task.setDuration7(MPPUtility.getDuration (taskExtData.getInt (TASK_DURATION7), MPPUtility.getDurationUnits(taskExtData.getShort (TASK_DURATION7_UNITS))));
         task.setDuration8(MPPUtility.getDuration (taskExtData.getInt (TASK_DURATION8), MPPUtility.getDurationUnits(taskExtData.getShort (TASK_DURATION8_UNITS))));
         task.setDuration9(MPPUtility.getDuration (taskExtData.getInt (TASK_DURATION9), MPPUtility.getDurationUnits(taskExtData.getShort (TASK_DURATION9_UNITS))));
         task.setDuration10(MPPUtility.getDuration (taskExtData.getInt (TASK_DURATION10), MPPUtility.getDurationUnits(taskExtData.getShort (TASK_DURATION10_UNITS)))); 
         //task.setDurationVariance(); // Calculated value
         //task.setEarlyFinish(); // Calculated value
         //task.setEarlyStart(); // Calculated value
         task.setEffortDriven((data[17] & 0x08) != 0);
         task.setFinish (MPPUtility.getTimestamp (data, 20));
         task.setFinish1(taskExtData.getTimestamp(TASK_FINISH1));
         task.setFinish2(taskExtData.getTimestamp(TASK_FINISH2));
         task.setFinish3(taskExtData.getTimestamp(TASK_FINISH3));
         task.setFinish4(taskExtData.getTimestamp(TASK_FINISH4));
         task.setFinish5(taskExtData.getTimestamp(TASK_FINISH5));
         task.setFinish6(taskExtData.getTimestamp(TASK_FINISH6));
         task.setFinish7(taskExtData.getTimestamp(TASK_FINISH7));
         task.setFinish8(taskExtData.getTimestamp(TASK_FINISH8));
         task.setFinish9(taskExtData.getTimestamp(TASK_FINISH9));
         task.setFinish10(taskExtData.getTimestamp(TASK_FINISH10)); 
         //task.setFinishVariance(); // Calculated value
         //task.setFixed(); // Not in MSP98?
         task.setFixedCost(new Double (((double)MPPUtility.getLong6(data, 228)) / 100));
         task.setFlag1((data[268] & 0x02) != 0);
         task.setFlag2((data[268] & 0x04) != 0);
         task.setFlag3((data[268] & 0x08) != 0);
         task.setFlag4((data[268] & 0x10) != 0);
         task.setFlag5((data[268] & 0x20) != 0);
         task.setFlag6((data[268] & 0x40) != 0);         
         task.setFlag7((data[268] & 0x80) != 0);                  
         task.setFlag8((data[269] & 0x01) != 0);
         task.setFlag9((data[269] & 0x02) != 0);
         task.setFlag10((data[269] & 0x04) != 0);
         task.setFlag11((data[269] & 0x08) != 0);
         task.setFlag12((data[269] & 0x10) != 0);
         task.setFlag13((data[269] & 0x20) != 0);
         task.setFlag14((data[269] & 0x40) != 0);
         task.setFlag15((data[269] & 0x80) != 0);
         task.setFlag16((data[270] & 0x01) != 0);
         task.setFlag17((data[270] & 0x02) != 0);
         task.setFlag18((data[270] & 0x04) != 0);
         task.setFlag19((data[270] & 0x08) != 0);
         task.setFlag20((data[270] & 0x10) != 0);
         //task.setFreeSlack();  // Calculated value
         task.setHideBar((data[16] & 0x01) != 0);
         task.setID (MPPUtility.getInt (data, 4));
         //task.setLateFinish();  // Calculated value
         //task.setLateStart();  // Calculated value
         //task.setLinkedFields();  // Calculated value
         task.setMarked((data[13] & 0x02) != 0);
         task.setMilestone((data[12] & 0x01) != 0);
         task.setName(taskVarData.getUnicodeString (getOffset(data, 264)));
         task.setNumber1(new Double (taskExtData.getDouble(TASK_NUMBER1)));
         task.setNumber2(new Double (taskExtData.getDouble(TASK_NUMBER2)));
         task.setNumber3(new Double (taskExtData.getDouble(TASK_NUMBER3)));
         task.setNumber4(new Double (taskExtData.getDouble(TASK_NUMBER4)));
         task.setNumber5(new Double (taskExtData.getDouble(TASK_NUMBER5)));
         task.setNumber6(new Double (taskExtData.getDouble(TASK_NUMBER6)));
         task.setNumber7(new Double (taskExtData.getDouble(TASK_NUMBER7)));
         task.setNumber8(new Double (taskExtData.getDouble(TASK_NUMBER8)));
         task.setNumber9(new Double (taskExtData.getDouble(TASK_NUMBER9)));
         task.setNumber10(new Double (taskExtData.getDouble(TASK_NUMBER10)));
         task.setNumber11(new Double (taskExtData.getDouble(TASK_NUMBER11)));
         task.setNumber12(new Double (taskExtData.getDouble(TASK_NUMBER12)));
         task.setNumber13(new Double (taskExtData.getDouble(TASK_NUMBER13)));
         task.setNumber14(new Double (taskExtData.getDouble(TASK_NUMBER14)));
         task.setNumber15(new Double (taskExtData.getDouble(TASK_NUMBER15)));
         task.setNumber16(new Double (taskExtData.getDouble(TASK_NUMBER16)));
         task.setNumber17(new Double (taskExtData.getDouble(TASK_NUMBER17)));
         task.setNumber18(new Double (taskExtData.getDouble(TASK_NUMBER18)));
         task.setNumber19(new Double (taskExtData.getDouble(TASK_NUMBER19)));
         task.setNumber20(new Double (taskExtData.getDouble(TASK_NUMBER20)));
         //task.setObjects(); // Calculated value
         task.setOutlineLevel (MPPUtility.getShort (data, 48));
         //task.setOutlineNumber(); // Calculated value 
         // TODO add overtime cost?
         //task.setOvertimeCost (new Double(((double)MPPUtility.getLong6(data, 204))/100));
         task.setPercentageComplete((double)MPPUtility.getShort(data, 130));
         task.setPercentageWorkComplete((double)MPPUtility.getShort(data, 132));
         task.setPriority(Priority.getInstance(MPPUtility.getShort (data, 128)));
         //task.setProject(); // Calculated value
         task.setRemainingCost(new Double (((double)MPPUtility.getLong6(data, 240)) / 100));
         task.setRemainingDuration (MPPUtility.getDuration (MPPUtility.getInt (data, 78), MPPUtility.getDurationUnits(MPPUtility.getShort (data, 72))));
         task.setRemainingWork(MPPUtility.getDuration(((double)MPPUtility.getLong6(data, 186))/100, TimeUnit.HOURS));
         //task.setResourceGroup(); // Calculated value from resource
         //task.setResourceInitials(); // Calculated value from resource
         //task.setResourceNames(); // Calculated value from resource
         task.setResume(MPPUtility.getTimestamp(data, 32));
         //task.setResumeNoEarlierThan(); // Not in MSP98?
         task.setRollup((data[15] & 0x04) != 0);
         task.setStart (MPPUtility.getTimestamp (data, 24));
         task.setStart1(taskExtData.getTimestamp(TASK_START1));
         task.setStart2(taskExtData.getTimestamp(TASK_START2));
         task.setStart3(taskExtData.getTimestamp(TASK_START3));
         task.setStart4(taskExtData.getTimestamp(TASK_START4));
         task.setStart5(taskExtData.getTimestamp(TASK_START5));
         task.setStart6(taskExtData.getTimestamp(TASK_START6));
         task.setStart7(taskExtData.getTimestamp(TASK_START7));
         task.setStart8(taskExtData.getTimestamp(TASK_START8));
         task.setStart9(taskExtData.getTimestamp(TASK_START9));
         task.setStart10(taskExtData.getTimestamp(TASK_START10));          
         //task.setStartVariance(); // Calculated value
         task.setStop(MPPUtility.getTimestamp (data, 124));
         //task.setSubprojectFile();
         //task.setSV(); // Calculated value
         task.setText1(taskExtData.getString(TASK_TEXT1));
         task.setText2(taskExtData.getString(TASK_TEXT2));
         task.setText3(taskExtData.getString(TASK_TEXT3));
         task.setText4(taskExtData.getString(TASK_TEXT4));
         task.setText5(taskExtData.getString(TASK_TEXT5));
         task.setText6(taskExtData.getString(TASK_TEXT6));
         task.setText7(taskExtData.getString(TASK_TEXT7));
         task.setText8(taskExtData.getString(TASK_TEXT8));
         task.setText9(taskExtData.getString(TASK_TEXT9));
         task.setText10(taskExtData.getString(TASK_TEXT10));
         task.setText11(taskExtData.getString(TASK_TEXT10));
         task.setText12(taskExtData.getString(TASK_TEXT12));
         task.setText13(taskExtData.getString(TASK_TEXT13));
         task.setText14(taskExtData.getString(TASK_TEXT14));
         task.setText15(taskExtData.getString(TASK_TEXT15));
         task.setText16(taskExtData.getString(TASK_TEXT16));
         task.setText17(taskExtData.getString(TASK_TEXT17));
         task.setText18(taskExtData.getString(TASK_TEXT18));
         task.setText19(taskExtData.getString(TASK_TEXT19));
         task.setText20(taskExtData.getString(TASK_TEXT20));
         task.setText21(taskExtData.getString(TASK_TEXT21));
         task.setText22(taskExtData.getString(TASK_TEXT22));
         task.setText23(taskExtData.getString(TASK_TEXT23));
         task.setText24(taskExtData.getString(TASK_TEXT24));
         task.setText25(taskExtData.getString(TASK_TEXT25));
         task.setText26(taskExtData.getString(TASK_TEXT26));
         task.setText27(taskExtData.getString(TASK_TEXT27));
         task.setText28(taskExtData.getString(TASK_TEXT28));
         task.setText29(taskExtData.getString(TASK_TEXT29));
         task.setText30(taskExtData.getString(TASK_TEXT30)); 
         //task.setTotalSlack(); // Calculated value
         task.setType(MPPUtility.getShort(data, 134));
         task.setUniqueID(id);
         //task.setUpdateNeeded(); // Calculated value
         task.setWBS(taskExtData.getString (TASK_WBS));
         task.setWork(MPPUtility.getDuration(((double)MPPUtility.getLong6(data, 168))/100, TimeUnit.HOURS));
         //task.setWorkVariance(); // Calculated value
         
         //
         // Retrieve the task notes.
         // TODO fix reading notes in FixDeferFix         
         //
//         notes = taskExtData.getString (TASK_NOTES);
//         if (notes != null)
//         {
//            if (m_preserveNoteFormatting == false)
//            {
//               notes = removeNoteFormatting (rtfEditor, rtfDoc, notes);
//            }
//                                      
//            task.addTaskNotes(notes);
//         }
         
         //
         // Calculate the cost variance
         //
         if (task.getCost() != null && task.getBaselineCost() != null)
         {
            task.setCostVariance(new Double(task.getCost().doubleValue() - task.getBaselineCost().doubleValue()));   
         }                                                                                                                 
      }            
   }

   /**
    * This method extracts and collates constraint data.
    * 
    * @param file Parent MPX file
    * @param projectDir Project data directory
    * @throws MPXException
    * @throws IOException
    */   
   private static void processConstraintData (MPPFile file, DirectoryEntry projectDir)
      throws IOException
   {         
      DirectoryEntry consDir = (DirectoryEntry)projectDir.getEntry ("TBkndCons");
      FixFix consFixedData = new FixFix (36, new DocumentInputStream (((DocumentEntry)consDir.getEntry("FixFix   0"))));      
      
      int count = consFixedData.getItemCount();
      int index;
      byte[] data;
      Task task1;
      Task task2;
      Relation rel;
      int durationUnits;
      int taskID1;
      int taskID2;
      byte[] metaData;
            
      for (int loop=0; loop < count; loop++)
      {
         data = consFixedData.getByteArrayValue(loop);

         if (MPPUtility.getInt(data, 28) == 0)                  
         {
            taskID1 = MPPUtility.getInt (data, 12);
            taskID2 = MPPUtility.getInt (data, 16);
   
            if (taskID1 != taskID2)
            {
               task1 = file.getTaskByUniqueID (taskID1);
               task2 = file.getTaskByUniqueID (taskID2);
               if (task1 != null && task2 != null)
               {
                  rel = task2.addPredecessor(task1);
                  rel.setType (MPPUtility.getShort(data, 20));
                  durationUnits = MPPUtility.getDurationUnits(MPPUtility.getShort (data, 22));
                  rel.setDuration(MPPUtility.getDuration (MPPUtility.getInt (data, 24), durationUnits));
               }
            }               
         }            
      }      
   }


   /**
    * This method extracts and collates resource data.
    * 
    * TODO extract extended attributes
    * 
    * @param file Parent MPX file
    * @param projectDir Project data directory
    * @throws MPXException
    * @throws IOException
    */   
   private static void processResourceData (MPPFile file, DirectoryEntry projectDir)
      throws MPXException, IOException
   {
      DirectoryEntry rscDir = (DirectoryEntry)projectDir.getEntry ("TBkndRsc");
      FixFix rscFixedData = new FixFix (196, new DocumentInputStream (((DocumentEntry)rscDir.getEntry("FixFix   0"))));
      FixDeferFix rscVarData = null;            
            
      int resources = rscFixedData.getItemCount();
      byte[] data;
      int id;
      Resource resource;
            
      for (int loop=0; loop < resources; loop++)
      {
         data = rscFixedData.getByteArrayValue(loop);
         
         //
         // Test for a valid unique id
         //
         id = MPPUtility.getInt(data, 0);
         if (id < 1)
         {
            continue;
         }

         //
         // Test to ensure this resource has not been deleted
         //         
         if (MPPUtility.getShort(data, 164) != 0)
         {
            continue;
         }
         
         //
         // Load the var data if we have not already done so
         //
         if (rscVarData == null)
         {
            rscVarData = new FixDeferFix (new DocumentInputStream (((DocumentEntry)rscDir.getEntry("FixDeferFix   0"))));                        
         }
                     
         resource = file.addResource();
                 
         resource.setAccrueAt(AccrueType.getInstance (MPPUtility.getShort (data, 20)));
         resource.setActualCost(new Double(((double)MPPUtility.getLong6(data, 114))/100));
         resource.setActualOvertimeCost(new Double(((double)MPPUtility.getLong6(data, 144))/100));
         resource.setActualWork(MPPUtility.getDuration(((double)MPPUtility.getLong6(data, 62))/100, TimeUnit.HOURS));
         //resource.setBaseCalendar();
         resource.setBaselineCost(new Double(((double)MPPUtility.getLong6(data, 126))/100));
         resource.setBaselineWork(MPPUtility.getDuration(((double)MPPUtility.getLong6(data, 68))/100, TimeUnit.HOURS));
//         resource.setCode (rscVarData.getUnicodeString (id, RESOURCE_CODE));
         resource.setCost(new Double(((double)MPPUtility.getLong6(data, 120))/100));
//         resource.setCost1(new Double (rscVarData.getDouble (id, RESOURCE_COST1) / 100));
//         resource.setCost2(new Double (rscVarData.getDouble (id, RESOURCE_COST2) / 100));
//         resource.setCost3(new Double (rscVarData.getDouble (id, RESOURCE_COST3) / 100));
//         resource.setCost4(new Double (rscVarData.getDouble (id, RESOURCE_COST4) / 100));
//         resource.setCost5(new Double (rscVarData.getDouble (id, RESOURCE_COST5) / 100));
//         resource.setCost6(new Double (rscVarData.getDouble (id, RESOURCE_COST6) / 100));
//         resource.setCost7(new Double (rscVarData.getDouble (id, RESOURCE_COST7) / 100));
//         resource.setCost8(new Double (rscVarData.getDouble (id, RESOURCE_COST8) / 100));
//         resource.setCost9(new Double (rscVarData.getDouble (id, RESOURCE_COST9) / 100));
//         resource.setCost10(new Double (rscVarData.getDouble (id, RESOURCE_COST10) / 100));         
         resource.setCostPerUse(new Double(((double)MPPUtility.getLong6(data, 80))/100));                  
//         resource.setDate1(rscVarData.getTimestamp (id, RESOURCE_DATE1));
//         resource.setDate2(rscVarData.getTimestamp (id, RESOURCE_DATE2));
//         resource.setDate3(rscVarData.getTimestamp (id, RESOURCE_DATE3));
//         resource.setDate4(rscVarData.getTimestamp (id, RESOURCE_DATE4));
//         resource.setDate5(rscVarData.getTimestamp (id, RESOURCE_DATE5));
//         resource.setDate6(rscVarData.getTimestamp (id, RESOURCE_DATE6));
//         resource.setDate7(rscVarData.getTimestamp (id, RESOURCE_DATE7));
//         resource.setDate8(rscVarData.getTimestamp (id, RESOURCE_DATE8));
//         resource.setDate9(rscVarData.getTimestamp (id, RESOURCE_DATE9));
//         resource.setDate10(rscVarData.getTimestamp (id, RESOURCE_DATE10));                           
//         resource.setDuration1(MPPUtility.getDuration (rscVarData.getInt(id, RESOURCE_DURATION1), MPPUtility.getDurationUnits(rscVarData.getShort(id, RESOURCE_DURATION1_UNITS))));
//         resource.setDuration2(MPPUtility.getDuration (rscVarData.getInt(id, RESOURCE_DURATION2), MPPUtility.getDurationUnits(rscVarData.getShort(id, RESOURCE_DURATION2_UNITS))));
//         resource.setDuration3(MPPUtility.getDuration (rscVarData.getInt(id, RESOURCE_DURATION3), MPPUtility.getDurationUnits(rscVarData.getShort(id, RESOURCE_DURATION3_UNITS))));
//         resource.setDuration4(MPPUtility.getDuration (rscVarData.getInt(id, RESOURCE_DURATION4), MPPUtility.getDurationUnits(rscVarData.getShort(id, RESOURCE_DURATION4_UNITS))));
//         resource.setDuration5(MPPUtility.getDuration (rscVarData.getInt(id, RESOURCE_DURATION5), MPPUtility.getDurationUnits(rscVarData.getShort(id, RESOURCE_DURATION5_UNITS))));
//         resource.setDuration6(MPPUtility.getDuration (rscVarData.getInt(id, RESOURCE_DURATION6), MPPUtility.getDurationUnits(rscVarData.getShort(id, RESOURCE_DURATION6_UNITS))));
//         resource.setDuration7(MPPUtility.getDuration (rscVarData.getInt(id, RESOURCE_DURATION7), MPPUtility.getDurationUnits(rscVarData.getShort(id, RESOURCE_DURATION7_UNITS))));
//         resource.setDuration8(MPPUtility.getDuration (rscVarData.getInt(id, RESOURCE_DURATION8), MPPUtility.getDurationUnits(rscVarData.getShort(id, RESOURCE_DURATION8_UNITS))));
//         resource.setDuration9(MPPUtility.getDuration (rscVarData.getInt(id, RESOURCE_DURATION9), MPPUtility.getDurationUnits(rscVarData.getShort(id, RESOURCE_DURATION9_UNITS))));
//         resource.setDuration10(MPPUtility.getDuration (rscVarData.getInt(id, RESOURCE_DURATION10), MPPUtility.getDurationUnits(rscVarData.getShort(id, RESOURCE_DURATION10_UNITS))));                                    
//         resource.setEmailAddress(rscVarData.getUnicodeString (id, RESOURCE_EMAIL));         
//         resource.setFinish1(rscVarData.getTimestamp (id, RESOURCE_FINISH1));         
//         resource.setFinish2(rscVarData.getTimestamp (id, RESOURCE_FINISH2));
//         resource.setFinish3(rscVarData.getTimestamp (id, RESOURCE_FINISH3));
//         resource.setFinish4(rscVarData.getTimestamp (id, RESOURCE_FINISH4));
//         resource.setFinish5(rscVarData.getTimestamp (id, RESOURCE_FINISH5));
//         resource.setFinish6(rscVarData.getTimestamp (id, RESOURCE_FINISH6));
//         resource.setFinish7(rscVarData.getTimestamp (id, RESOURCE_FINISH7));
//         resource.setFinish8(rscVarData.getTimestamp (id, RESOURCE_FINISH8));
//         resource.setFinish9(rscVarData.getTimestamp (id, RESOURCE_FINISH9));
//         resource.setFinish10(rscVarData.getTimestamp (id, RESOURCE_FINISH10));
//         resource.setGroup(rscVarData.getUnicodeString (id, RESOURCE_GROUP));
         resource.setID (MPPUtility.getInt (data, 4));
         resource.setInitials (rscVarData.getUnicodeString(getOffset (data, 160)));
         //resource.setLinkedFields(); // Calculated value
         resource.setMaxUnits(new Double(((double)MPPUtility.getInt(data, 52))/100));
         resource.setName (rscVarData.getUnicodeString(getOffset (data, 156)));         
//         resource.setNumber1(new Double (rscVarData.getDouble(id, RESOURCE_NUMBER1)));
//         resource.setNumber2(new Double (rscVarData.getDouble(id, RESOURCE_NUMBER2)));
//         resource.setNumber3(new Double (rscVarData.getDouble(id, RESOURCE_NUMBER3)));
//         resource.setNumber4(new Double (rscVarData.getDouble(id, RESOURCE_NUMBER4)));
//         resource.setNumber5(new Double (rscVarData.getDouble(id, RESOURCE_NUMBER5)));
//         resource.setNumber6(new Double (rscVarData.getDouble(id, RESOURCE_NUMBER6)));
//         resource.setNumber7(new Double (rscVarData.getDouble(id, RESOURCE_NUMBER7)));
//         resource.setNumber8(new Double (rscVarData.getDouble(id, RESOURCE_NUMBER8)));
//         resource.setNumber9(new Double (rscVarData.getDouble(id, RESOURCE_NUMBER9)));
//         resource.setNumber10(new Double (rscVarData.getDouble(id, RESOURCE_NUMBER10)));
//         resource.setNumber11(new Double (rscVarData.getDouble(id, RESOURCE_NUMBER11)));
//         resource.setNumber12(new Double (rscVarData.getDouble(id, RESOURCE_NUMBER12)));
//         resource.setNumber13(new Double (rscVarData.getDouble(id, RESOURCE_NUMBER13)));
//         resource.setNumber14(new Double (rscVarData.getDouble(id, RESOURCE_NUMBER14)));
//         resource.setNumber15(new Double (rscVarData.getDouble(id, RESOURCE_NUMBER15)));
//         resource.setNumber16(new Double (rscVarData.getDouble(id, RESOURCE_NUMBER16)));
//         resource.setNumber17(new Double (rscVarData.getDouble(id, RESOURCE_NUMBER17)));
//         resource.setNumber18(new Double (rscVarData.getDouble(id, RESOURCE_NUMBER18)));
//         resource.setNumber19(new Double (rscVarData.getDouble(id, RESOURCE_NUMBER19)));
//         resource.setNumber20(new Double (rscVarData.getDouble(id, RESOURCE_NUMBER20)));                           
         //resource.setObjects(); // Calculated value
         //resource.setOverallocated(); // Calculated value
         resource.setOvertimeCost(new Double(((double)MPPUtility.getLong6(data, 138))/100));         
         resource.setOvertimeRate(new MPXRate (MPPUtility.getDouble(data, 44), TimeUnit.HOURS));
         resource.setOvertimeWork(MPPUtility.getDuration(((double)MPPUtility.getLong6(data, 74))/100, TimeUnit.HOURS));
         resource.setPeak(new Double(((double)MPPUtility.getInt(data, 110))/100));
         //resource.setPercentageWorkComplete(); // Calculated value
         resource.setRegularWork(MPPUtility.getDuration(((double)MPPUtility.getLong6(data, 92))/100, TimeUnit.HOURS));
         resource.setRemainingCost(new Double(((double)MPPUtility.getLong6(data, 132))/100));
         resource.setRemainingOvertimeCost(new Double(((double)MPPUtility.getLong6(data, 150))/100));                  
         resource.setRemainingWork(MPPUtility.getDuration(((double)MPPUtility.getLong6(data, 86))/100, TimeUnit.HOURS));
         resource.setStandardRate(new MPXRate (MPPUtility.getDouble(data, 36), TimeUnit.HOURS));         
//         resource.setStart1(rscVarData.getTimestamp (id, RESOURCE_START1));
//         resource.setStart2(rscVarData.getTimestamp (id, RESOURCE_START2));
//         resource.setStart3(rscVarData.getTimestamp (id, RESOURCE_START3));
//         resource.setStart4(rscVarData.getTimestamp (id, RESOURCE_START4));
//         resource.setStart5(rscVarData.getTimestamp (id, RESOURCE_START5));
//         resource.setStart6(rscVarData.getTimestamp (id, RESOURCE_START6));
//         resource.setStart7(rscVarData.getTimestamp (id, RESOURCE_START7));
//         resource.setStart8(rscVarData.getTimestamp (id, RESOURCE_START8));
//         resource.setStart9(rscVarData.getTimestamp (id, RESOURCE_START9));
//         resource.setStart10(rscVarData.getTimestamp (id, RESOURCE_START10));                           
//         resource.setText1(rscVarData.getUnicodeString (id, RESOURCE_TEXT1));
//         resource.setText2(rscVarData.getUnicodeString (id, RESOURCE_TEXT2));
//         resource.setText3(rscVarData.getUnicodeString (id, RESOURCE_TEXT3));
//         resource.setText4(rscVarData.getUnicodeString (id, RESOURCE_TEXT4));
//         resource.setText5(rscVarData.getUnicodeString (id, RESOURCE_TEXT5));
//         resource.setText6(rscVarData.getUnicodeString (id, RESOURCE_TEXT6));
//         resource.setText7(rscVarData.getUnicodeString (id, RESOURCE_TEXT7));
//         resource.setText8(rscVarData.getUnicodeString (id, RESOURCE_TEXT8));
//         resource.setText9(rscVarData.getUnicodeString (id, RESOURCE_TEXT9));
//         resource.setText10(rscVarData.getUnicodeString (id, RESOURCE_TEXT10));
//         resource.setText11(rscVarData.getUnicodeString (id, RESOURCE_TEXT11));
//         resource.setText12(rscVarData.getUnicodeString (id, RESOURCE_TEXT12));
//         resource.setText13(rscVarData.getUnicodeString (id, RESOURCE_TEXT13));
//         resource.setText14(rscVarData.getUnicodeString (id, RESOURCE_TEXT14));
//         resource.setText15(rscVarData.getUnicodeString (id, RESOURCE_TEXT15));
//         resource.setText16(rscVarData.getUnicodeString (id, RESOURCE_TEXT16));
//         resource.setText17(rscVarData.getUnicodeString (id, RESOURCE_TEXT17));
//         resource.setText18(rscVarData.getUnicodeString (id, RESOURCE_TEXT18));
//         resource.setText19(rscVarData.getUnicodeString (id, RESOURCE_TEXT19));
//         resource.setText20(rscVarData.getUnicodeString (id, RESOURCE_TEXT20));
//         resource.setText21(rscVarData.getUnicodeString (id, RESOURCE_TEXT21));
//         resource.setText22(rscVarData.getUnicodeString (id, RESOURCE_TEXT22));
//         resource.setText23(rscVarData.getUnicodeString (id, RESOURCE_TEXT23));
//         resource.setText24(rscVarData.getUnicodeString (id, RESOURCE_TEXT24));
//         resource.setText25(rscVarData.getUnicodeString (id, RESOURCE_TEXT25));
//         resource.setText26(rscVarData.getUnicodeString (id, RESOURCE_TEXT26));
//         resource.setText27(rscVarData.getUnicodeString (id, RESOURCE_TEXT27));
//         resource.setText28(rscVarData.getUnicodeString (id, RESOURCE_TEXT28));
//         resource.setText29(rscVarData.getUnicodeString (id, RESOURCE_TEXT29));
//         resource.setText30(rscVarData.getUnicodeString (id, RESOURCE_TEXT30));         
         resource.setUniqueID(id);
         resource.setWork(MPPUtility.getDuration(((double)MPPUtility.getLong6(data, 56))/100, TimeUnit.HOURS));
//
//         notes = rscVarData.getString (id, RESOURCE_NOTES);
//         if (notes != null)
//         {
//            if (m_preserveNoteFormatting == false)
//            {
//               notes = removeNoteFormatting (rtfEditor, rtfDoc, notes);
//            }
//            
//            resource.addResourceNotes(notes);
//         }

         //
         // Calculate the cost variance
         //         
         if (resource.getCost() != null && resource.getBaselineCost() != null)
         {
            resource.setCostVariance(resource.getCost().doubleValue() - resource.getBaselineCost().doubleValue());   
         }

         //
         // Calculate the work variance
         //       
         if (resource.getWork() != null && resource.getBaselineWork() != null)
         {
            resource.setWorkVariance(new MPXDuration (resource.getWork().getDuration() - resource.getBaselineWork().getDuration(), TimeUnit.HOURS));  
         }        
      }
   }
   

   /**
    * This method extracts and collates resource assignment data.
    * 
    * TODO Extract more attributes from each record.
    * 
    * @param file Parent MPX file
    * @param projectDir Project data directory
    * @throws MPXException
    * @throws IOException
    */      
   private static void processAssignmentData (MPPFile file, DirectoryEntry projectDir)
      throws MPXException, IOException
   {
      DirectoryEntry assnDir = (DirectoryEntry)projectDir.getEntry ("TBkndAssn");
      FixFix assnFixedData = new FixFix (204, new DocumentInputStream (((DocumentEntry)assnDir.getEntry("FixFix   0"))));
      
      int count = assnFixedData.getItemCount();
      byte[] data;
      Task task;
      Resource resource;

      for (int loop=0; loop < count; loop++)
      {
         data = assnFixedData.getByteArrayValue(loop);
         task = file.getTaskByUniqueID (MPPUtility.getInt (data, 16));
         resource = file.getResourceByUniqueID (MPPUtility.getInt (data, 20));
         if (task != null && resource != null)
         {
            task.addResourceAssignment (resource);
         }
      }
   }
   
   /**
    * This method is used to extract a value from a fixed data block,
    * which represents an offset into a variable data block.
    * 
    * @param data Fixed data block
    * @param offset Offset in fixed data block
    * @return Offset in var data block
    */   
   private static int getOffset (byte[] data, int offset)
   {
      return (-1 - MPPUtility.getInt(data, offset));      
   }      

   /**
    * Task data types.
    */
   private static final Integer TASK_WBS = new Integer (104);
   private static final Integer TASK_CONTACT = new Integer (105);
   
   private static final Integer TASK_TEXT1 = new Integer (106);
   private static final Integer TASK_TEXT2 = new Integer (107);
   private static final Integer TASK_TEXT3 = new Integer (108);
   private static final Integer TASK_TEXT4 = new Integer (109);
   private static final Integer TASK_TEXT5 = new Integer (110);
   private static final Integer TASK_TEXT6 = new Integer (111);
   private static final Integer TASK_TEXT7 = new Integer (112);
   private static final Integer TASK_TEXT8 = new Integer (113);
   private static final Integer TASK_TEXT9 = new Integer (114);
   private static final Integer TASK_TEXT10 = new Integer (115);
   
   private static final Integer TASK_START1 = new Integer (116);
   private static final Integer TASK_FINISH1 = new Integer (117);
   private static final Integer TASK_START2 = new Integer (118);
   private static final Integer TASK_FINISH2 = new Integer (119);
   private static final Integer TASK_START3 = new Integer (120);
   private static final Integer TASK_FINISH3 = new Integer (121);
   private static final Integer TASK_START4 = new Integer (122);
   private static final Integer TASK_FINISH4 = new Integer (123);
   private static final Integer TASK_START5 = new Integer (124);
   private static final Integer TASK_FINISH5 = new Integer (125);
   private static final Integer TASK_START6 = new Integer (126);
   private static final Integer TASK_FINISH6 = new Integer (127);
   private static final Integer TASK_START7 = new Integer (128);
   private static final Integer TASK_FINISH7 = new Integer (129);
   private static final Integer TASK_START8 = new Integer (130);
   private static final Integer TASK_FINISH8 = new Integer (131);
   private static final Integer TASK_START9 = new Integer (132);
   private static final Integer TASK_FINISH9 = new Integer (133);
   private static final Integer TASK_START10 = new Integer (134);
   private static final Integer TASK_FINISH10 = new Integer (135);

   private static final Integer TASK_NUMBER1 = new Integer (137);
   private static final Integer TASK_NUMBER2 = new Integer (138);
   private static final Integer TASK_NUMBER3 = new Integer (139);
   private static final Integer TASK_NUMBER4 = new Integer (140);
   private static final Integer TASK_NUMBER5 = new Integer (141);
   private static final Integer TASK_NUMBER6 = new Integer (142);
   private static final Integer TASK_NUMBER7 = new Integer (143);
   private static final Integer TASK_NUMBER8 = new Integer (144);
   private static final Integer TASK_NUMBER9 = new Integer (145);
   private static final Integer TASK_NUMBER10 = new Integer (146);
   
   private static final Integer TASK_DURATION1 = new Integer (147);
   private static final Integer TASK_DURATION1_UNITS = new Integer (148);
   private static final Integer TASK_DURATION2 = new Integer (149);
   private static final Integer TASK_DURATION2_UNITS = new Integer (150);
   private static final Integer TASK_DURATION3 = new Integer (151);
   private static final Integer TASK_DURATION3_UNITS = new Integer (152);
   private static final Integer TASK_DURATION4 = new Integer (153);
   private static final Integer TASK_DURATION4_UNITS = new Integer (154);
   private static final Integer TASK_DURATION5 = new Integer (155);
   private static final Integer TASK_DURATION5_UNITS = new Integer (156);
   private static final Integer TASK_DURATION6 = new Integer (157);
   private static final Integer TASK_DURATION6_UNITS = new Integer (158);
   private static final Integer TASK_DURATION7 = new Integer (159);
   private static final Integer TASK_DURATION7_UNITS = new Integer (160);
   private static final Integer TASK_DURATION8 = new Integer (161);
   private static final Integer TASK_DURATION8_UNITS = new Integer (162);
   private static final Integer TASK_DURATION9 = new Integer (163);
   private static final Integer TASK_DURATION9_UNITS = new Integer (164);
   private static final Integer TASK_DURATION10 = new Integer (165);
   private static final Integer TASK_DURATION10_UNITS = new Integer (166);
   
   private static final Integer TASK_DATE1 = new Integer (174);
   private static final Integer TASK_DATE2 = new Integer (175);
   private static final Integer TASK_DATE3 = new Integer (176);
   private static final Integer TASK_DATE4 = new Integer (177);
   private static final Integer TASK_DATE5 = new Integer (178);
   private static final Integer TASK_DATE6 = new Integer (179);
   private static final Integer TASK_DATE7 = new Integer (180);
   private static final Integer TASK_DATE8 = new Integer (181);
   private static final Integer TASK_DATE9 = new Integer (182);
   private static final Integer TASK_DATE10 = new Integer (183);

   private static final Integer TASK_TEXT11 = new Integer (184);
   private static final Integer TASK_TEXT12 = new Integer (185);   
   private static final Integer TASK_TEXT13 = new Integer (186);
   private static final Integer TASK_TEXT14 = new Integer (187);   
   private static final Integer TASK_TEXT15 = new Integer (188);
   private static final Integer TASK_TEXT16 = new Integer (189);
   private static final Integer TASK_TEXT17 = new Integer (190);         
   private static final Integer TASK_TEXT18 = new Integer (191);   
   private static final Integer TASK_TEXT19 = new Integer (192);   
   private static final Integer TASK_TEXT20 = new Integer (193);         
   private static final Integer TASK_TEXT21 = new Integer (194);
   private static final Integer TASK_TEXT22 = new Integer (195);   
   private static final Integer TASK_TEXT23 = new Integer (196);   
   private static final Integer TASK_TEXT24 = new Integer (197);   
   private static final Integer TASK_TEXT25 = new Integer (198);   
   private static final Integer TASK_TEXT26 = new Integer (199);   
   private static final Integer TASK_TEXT27 = new Integer (200);   
   private static final Integer TASK_TEXT28 = new Integer (201);   
   private static final Integer TASK_TEXT29 = new Integer (202);   
   private static final Integer TASK_TEXT30 = new Integer (203);   
   
   private static final Integer TASK_NUMBER11 = new Integer (204);
   private static final Integer TASK_NUMBER12 = new Integer (205);
   private static final Integer TASK_NUMBER13 = new Integer (206);
   private static final Integer TASK_NUMBER14 = new Integer (207);
   private static final Integer TASK_NUMBER15 = new Integer (208);
   private static final Integer TASK_NUMBER16 = new Integer (209);
   private static final Integer TASK_NUMBER17 = new Integer (210);
   private static final Integer TASK_NUMBER18 = new Integer (211);
   private static final Integer TASK_NUMBER19 = new Integer (212);
   private static final Integer TASK_NUMBER20 = new Integer (213);
   
   private static final Integer TASK_COST1 = new Integer (237);
   private static final Integer TASK_COST2 = new Integer (238);
   private static final Integer TASK_COST3 = new Integer (239);
   private static final Integer TASK_COST4 = new Integer (240);
   private static final Integer TASK_COST5 = new Integer (241);
   private static final Integer TASK_COST6 = new Integer (242);
   private static final Integer TASK_COST7 = new Integer (243);
   private static final Integer TASK_COST8 = new Integer (244);
   private static final Integer TASK_COST9 = new Integer (245);
   private static final Integer TASK_COST10 = new Integer (246);

   private static final Integer TASK_NOTES = new Integer (247);
}

