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

import com.tapsterrock.mpx.AccrueType;
import com.tapsterrock.mpx.BaseCalendar;
import com.tapsterrock.mpx.BaseCalendarHours;
import com.tapsterrock.mpx.BaseCalendarException;
import com.tapsterrock.mpx.ConstraintType;
import com.tapsterrock.mpx.MPXDuration;
import com.tapsterrock.mpx.MPXException;
import com.tapsterrock.mpx.MPXFile;
import com.tapsterrock.mpx.MPXRate;
import com.tapsterrock.mpx.Priority;
import com.tapsterrock.mpx.Relation;
import com.tapsterrock.mpx.Resource;
import com.tapsterrock.mpx.Task;
import com.tapsterrock.mpx.TimeUnit;

import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.TreeMap;
import java.util.LinkedList;
import java.util.Iterator;
import javax.swing.text.rtf.RTFEditorKit;
import javax.swing.text.Document;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.DocumentInputStream;

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

      //processCalendarData (file, projectDir);
             
      processResourceData (file, projectDir);

      processTaskData (file, projectDir);
      
      processConstraintData (file, projectDir);      
      
      processAssignmentData (file, projectDir);
   }


   /**
    * This method extracts and collates calendar data.
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
//      Integer id;
//      BaseCalendar cal;
//      BaseCalendarHours hours;
//      BaseCalendarException exception;
//      String name;
      byte[] data;
//
//      int periodCount;
//      int index;
//      int offset;
//      int defaultFlag;
//      Date start;
//      long duration;
//      int exceptionCount;
//
      //
      // Configure default time ranges
      //
      SimpleDateFormat df = new SimpleDateFormat ("HH:mm");
      Date defaultStart1;
      Date defaultEnd1;
      Date defaultStart2;
      Date defaultEnd2;

      try
      {
         defaultStart1 = df.parse ("08:00");
         defaultEnd1 = df.parse ("12:00");
         defaultStart2 = df.parse ("13:00");
         defaultEnd2 = df.parse ("17:00");
      }

      catch (ParseException ex)
      {
         throw new MPXException (MPXException.INVALID_FORMAT, ex);
      }

      System.out.println (calendarVarData);
      
      for (int loop=0; loop < calendars; loop++)
      {
         data = calendarFixedData.getByteArrayValue(loop);
         System.out.println (MPPUtility.hexdump(data, false));
         System.out.println (getOffset(data, 20));   
         
//         id = uniqueid[loop];
//         name = calVarData.getUnicodeString (id, CALENDAR_NAME);
//         data = calVarData.getByteArray (id, CALENDAR_DATA);
//         if (data == null)
//         {
//            cal = addDefaultBaseCalendar();
//            cal.setName (name);
//         }
//         else
//         {
//            //
//            // Populate the basic calendar
//            //
//            cal = addBaseCalendar();
//            cal.setName (name);
//
//            for (index=0; index < 7; index++)
//            {
//               offset = 4 + (60 * index);
//               defaultFlag = MPPUtility.getShort (data, offset);
//
//               if (defaultFlag == 1)
//               {
//                  cal.setWorkingDay(index+1, DEFAULT_WORKING_WEEK[index]);
//                  if (cal.isWorkingDay(index+1) == true)
//                  {
//                     hours = cal.addBaseCalendarHours(index+1);
//                     hours.setFromTime1(defaultStart1);
//                     hours.setToTime1(defaultEnd1);
//                     hours.setFromTime2(defaultStart2);
//                     hours.setToTime2(defaultEnd2);
//                  }
//               }
//               else
//               {
//                  periodCount = MPPUtility.getShort (data, offset+2);
//                  if (periodCount == 0)
//                  {
//                     cal.setWorkingDay(index+1, false);
//                  }
//                  else
//                  {
//                     cal.setWorkingDay(index+1, true);
//                     hours = cal.addBaseCalendarHours(index+1);
//
//                     start = MPPUtility.getTime (data, offset + 8);
//                     duration = MPPUtility.getDuration (data, offset + 20);
//                     hours.setFromTime1(start);
//                     hours.setToTime1(new Date (start.getTime()+duration));
//                  
//                     if (periodCount > 1)
//                     {
//                        start = MPPUtility.getTime (data, offset + 10);
//                        duration = MPPUtility.getDuration (data, offset + 24);
//                        hours.setFromTime2(start);
//                        hours.setToTime2(new Date (start.getTime()+duration));
//
//                        if (periodCount > 2)
//                        {                          
//                           start = MPPUtility.getTime (data, offset + 12);
//                           duration = MPPUtility.getDuration (data, offset + 28);
//                           hours.setFromTime3(start);
//                           hours.setToTime3(new Date (start.getTime()+duration));
//                        }
//                     }
//
//                     // Note that MPP defines 5 time ranges, the additional
//                     // start times are at offsets 14, 16 and the additional
//                     // durations are at offsets 32 and 36.
//                  }
//               }
//            }
//
//            //
//            // Handle any exceptions
//            //
//            exceptionCount = MPPUtility.getShort (data, 0);
//            if (exceptionCount != 0)
//            {
//               for (index=0; index < exceptionCount; index++)
//               {
//                  offset = 4 + (60 * 7) + (index * 64);
//                  exception = cal.addBaseCalendarException();
//                  exception.setFromDate(MPPUtility.getDate (data, offset));
//                  exception.setToDate(MPPUtility.getDate (data, offset+2));
//
//                  periodCount = MPPUtility.getShort (data, offset+6);
//                  if (periodCount == 0)
//                  {
//                     exception.setWorking (false);
//                  }
//                  else
//                  {
//                     exception.setWorking (true);
//
//                     start = MPPUtility.getTime (data, offset+12);
//                     duration = MPPUtility.getDuration (data, offset+24);
//                     exception.setFromTime1(start);
//                     exception.setToTime1(new Date (start.getTime() + duration));
//
//                     if (periodCount > 1)
//                     {
//                        start = MPPUtility.getTime (data, offset+14);
//                        duration = MPPUtility.getDuration (data, offset+28);
//                        exception.setFromTime2(start);
//                        exception.setToTime2(new Date (start.getTime() + duration));
//
//                        if (periodCount > 2)
//                        {
//                           start = MPPUtility.getTime (data, offset+16);
//                           duration = MPPUtility.getDuration (data, offset+32);
//                           exception.setFromTime3(start);
//                           exception.setToTime3(new Date (start.getTime() + duration));
//                        }
//                     }
//                     //
//                     // Note that MPP defines 5 time ranges rather than 3
//                     //
//                  }
//               }
//            }
//         }
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
      
      int tasks = taskFixedData.getItemCount();
      byte[] data;
      int id;
      Task task;
      
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
         }
                              
         task = file.addTask();
                  
         task.setActualCost(new Double (((double)MPPUtility.getInt(data, 234)) / 100));
         task.setActualDuration(MPPUtility.getDuration (MPPUtility.getInt (data, 74), MPPUtility.getDurationUnits(MPPUtility.getShort (data, 72))));
         task.setActualFinish(MPPUtility.getTimestamp (data, 108));
         task.setActualStart(MPPUtility.getTimestamp (data, 104));
//         task.setActualWork(new MPXDuration (MPPUtility.getDouble (data, 184)/60000, TimeUnit.HOURS));
         task.setBaselineCost(new Double ((double)MPPUtility.getInt (data, 246) / 100));
         task.setBaselineDuration(MPPUtility.getDuration (MPPUtility.getInt (data, 82), MPPUtility.getDurationUnits (MPPUtility.getShort (data, 72))));
         task.setBaselineFinish(MPPUtility.getTimestamp (data, 116));
         task.setBaselineStart(MPPUtility.getTimestamp (data, 112));
         task.setBaselineWork(MPPUtility.getDuration(((double)MPPUtility.getInt(data, 174))/100, TimeUnit.HOURS));
         //task.setBCWP(); // Calculated value
         //task.setBCWS(); // Calculated value
         //task.setConfirmed(); // Calculated value
         task.setConstraintDate (MPPUtility.getTimestamp (data, 120));
         task.setConstraintType (ConstraintType.getInstance (MPPUtility.getShort (data, 88)));
//         task.setContact(taskVarData.getUnicodeString (id, TASK_CONTACT));
         task.setCost(new Double (((double)MPPUtility.getInt(data, 222)) / 100));
//         task.setCost1(new Double (taskVarData.getDouble (id, TASK_COST1) / 100));
//         task.setCost2(new Double (taskVarData.getDouble (id, TASK_COST2) / 100));
//         task.setCost3(new Double (taskVarData.getDouble (id, TASK_COST3) / 100));
         task.setCreated(MPPUtility.getTimestamp (data, 138));
         //task.setCritical(); // Calculated value
         //task.setCV(); // Calculated value
         //task.setDelay(); // Not in MSP98?
         task.setDuration (MPPUtility.getDuration (MPPUtility.getInt (data, 68), MPPUtility.getDurationUnits(MPPUtility.getShort (data, 72))));
//         task.setDuration1(getDuration (taskVarData.getInt(id, TASK_DURATION1), getDurationUnits(taskVarData.getShort(id, TASK_DURATION1_UNITS))));
//         task.setDuration2(getDuration (taskVarData.getInt(id, TASK_DURATION2), getDurationUnits(taskVarData.getShort(id, TASK_DURATION2_UNITS))));
//         task.setDuration3(getDuration (taskVarData.getInt(id, TASK_DURATION3), getDurationUnits(taskVarData.getShort(id, TASK_DURATION3_UNITS))));
         //task.setDurationVariance(); // Calculated value
         //task.setEarlyFinish(); // Calculated value
         //task.setEarlyStart(); // Calculated value
         task.setFinish (MPPUtility.getTimestamp (data, 20));
//         task.setFinish1(taskVarData.getTimestamp (id, TASK_FINISH1));
//         task.setFinish2(taskVarData.getTimestamp (id, TASK_FINISH2));
//         task.setFinish3(taskVarData.getTimestamp (id, TASK_FINISH3));
//         task.setFinish4(taskVarData.getTimestamp (id, TASK_FINISH4));
//         task.setFinish5(taskVarData.getTimestamp (id, TASK_FINISH5));
         //task.setFinishVariance(); // Calculated value
         //task.setFixed(); // Not in MSP98?
         task.setFixedCost(new Double (((double)MPPUtility.getInt(data, 222)) / 228));
         //task.setFreeSlack();  // Calculated value
         task.setID (MPPUtility.getInt (data, 4));
         //task.setLateFinish();  // Calculated value
         //task.setLateStart();  // Calculated value
         //task.setLinkedFields();  // Calculated value
         task.setMarked((data[13] & 0x02) != 0);
         task.setName(taskVarData.getUnicodeString (getOffset(data, 264)));
//         task.setNumber1(new Double (taskVarData.getDouble(id, TASK_NUMBER1)));
//         task.setNumber2(new Double (taskVarData.getDouble(id, TASK_NUMBER2)));
//         task.setNumber3(new Double (taskVarData.getDouble(id, TASK_NUMBER3)));
//         task.setNumber4(new Double (taskVarData.getDouble(id, TASK_NUMBER4)));
//         task.setNumber5(new Double (taskVarData.getDouble(id, TASK_NUMBER5)));
         //task.setObjects(); // Calculated value
         task.setOutlineLevel (MPPUtility.getShort (data, 48));
         //task.setOutlineNumber(); // Calculated value        
         task.setPercentageComplete((double)MPPUtility.getShort(data, 130));
         task.setPercentageWorkComplete((double)MPPUtility.getShort(data, 132));
         task.setPriority(Priority.getInstance(MPPUtility.getShort (data, 128)));
//         //task.setProject(); // Calculated value
         task.setRemainingCost(new Double (((double)MPPUtility.getInt(data, 240)) / 100));
         task.setRemainingDuration (MPPUtility.getDuration (MPPUtility.getInt (data, 78), MPPUtility.getDurationUnits(MPPUtility.getShort (data, 72))));
         task.setRemainingWork(MPPUtility.getDuration(((double)MPPUtility.getInt(data, 186))/100, TimeUnit.HOURS));
         //task.setResourceGroup(); // Calculated value from resource
         //task.setResourceInitials(); // Calculated value from resource
         //task.setResourceNames(); // Calculated value from resource
         task.setResume(MPPUtility.getTimestamp(data, 32));
         //task.setResumeNoEarlierThan(); // Not in MSP98?
         task.setStart (MPPUtility.getTimestamp (data, 24));
//         task.setStart1(taskVarData.getTimestamp (id, TASK_START1));
//         task.setStart2(taskVarData.getTimestamp (id, TASK_START2));
//         task.setStart3(taskVarData.getTimestamp (id, TASK_START3));
//         task.setStart4(taskVarData.getTimestamp (id, TASK_START4));
//         task.setStart5(taskVarData.getTimestamp (id, TASK_START5));
         //task.setStartVariance(); // Calculated value
         task.setStop(MPPUtility.getTimestamp (data, 124));
         //task.setSubprojectFile();
         //task.setSV(); // Calculated value
//         task.setText1(taskVarData.getUnicodeString (id, TASK_TEXT1));
//         task.setText2(taskVarData.getUnicodeString (id, TASK_TEXT2));
//         task.setText3(taskVarData.getUnicodeString (id, TASK_TEXT3));
//         task.setText4(taskVarData.getUnicodeString (id, TASK_TEXT4));
//         task.setText5(taskVarData.getUnicodeString (id, TASK_TEXT5));
//         task.setText6(taskVarData.getUnicodeString (id, TASK_TEXT6));
//         task.setText7(taskVarData.getUnicodeString (id, TASK_TEXT7));
//         task.setText8(taskVarData.getUnicodeString (id, TASK_TEXT8));
//         task.setText9(taskVarData.getUnicodeString (id, TASK_TEXT9));
//         task.setText10(taskVarData.getUnicodeString (id, TASK_TEXT10));
         //task.setTotalSlack(); // Calculated value
         task.setUniqueID(id);
         //task.setUpdateNeeded(); // Calculated value
//         task.setWBS(taskVarData.getUnicodeString (id, TASK_WBS));
         task.setWork(MPPUtility.getDuration(((double)MPPUtility.getInt(data, 168))/100, TimeUnit.HOURS));
         //task.setWorkVariance(); // Calculated value


//         offset = (Integer)taskMetaMap.get(id);
//         if (offset != null)
//         {
//            metaData = taskFixedMeta.getByteArrayValue(offset.intValue());
//
//            task.setFlag1((metaData[37] & 0x20) != 0);
//            task.setFlag2((metaData[37] & 0x40) != 0);
//            task.setFlag3((metaData[37] & 0x80) != 0);
//            task.setFlag4((metaData[38] & 0x01) != 0);
//            task.setFlag5((metaData[38] & 0x02) != 0);
//            task.setFlag6((metaData[38] & 0x04) != 0);
//            task.setFlag7((metaData[38] & 0x08) != 0);
//            task.setFlag8((metaData[38] & 0x10) != 0);
//            task.setFlag9((metaData[38] & 0x20) != 0);
//            task.setFlag10((metaData[38] & 0x40) != 0);            
//            
//            task.setMilestone((metaData[8] & 0x20) != 0);
//            task.setRollup((metaData[10] & 0x08) != 0);            
//            task.setHideBar((metaData[10] & 0x80) != 0);                        
//         }

         //
         // Retrieve the task notes.
         //       
//         notes = taskVarData.getString (id, TASK_NOTES);
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
         resource.setActualCost(new Double(((double)MPPUtility.getInt(data, 114))/100));
         resource.setActualOvertimeCost(new Double(((double)MPPUtility.getInt(data, 144))/100));
         resource.setActualWork(MPPUtility.getDuration(((double)MPPUtility.getInt(data, 62))/100, TimeUnit.HOURS));
         //resource.setBaseCalendar();
         resource.setBaselineCost(new Double(((double)MPPUtility.getInt(data, 126))/100));
         resource.setBaselineWork(MPPUtility.getDuration(((double)MPPUtility.getInt(data, 68))/100, TimeUnit.HOURS));
//         resource.setCode (rscVarData.getUnicodeString (id, RESOURCE_CODE));
         resource.setCost(new Double(((double)MPPUtility.getInt(data, 120))/100));
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
         resource.setCostPerUse(new Double(((double)MPPUtility.getInt(data, 80))/100));                  
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
         resource.setOvertimeCost(new Double(((double)MPPUtility.getInt(data, 138))/100));         
         resource.setOvertimeRate(new MPXRate (MPPUtility.getDouble(data, 44), TimeUnit.HOURS));
         resource.setOvertimeWork(MPPUtility.getDuration(((double)MPPUtility.getInt(data, 74))/100, TimeUnit.HOURS));
         resource.setPeak(new Double(((double)MPPUtility.getInt(data, 110))/100));
         //resource.setPercentageWorkComplete(); // Calculated value
         resource.setRegularWork(MPPUtility.getDuration(((double)MPPUtility.getInt(data, 92))/100, TimeUnit.HOURS));
         resource.setRemainingCost(new Double(((double)MPPUtility.getInt(data, 132))/100));
         resource.setRemainingOvertimeCost(new Double(((double)MPPUtility.getInt(data, 150))/100));                  
         resource.setRemainingWork(MPPUtility.getDuration(((double)MPPUtility.getInt(data, 86))/100, TimeUnit.HOURS));
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
         resource.setWork(MPPUtility.getDuration(((double)MPPUtility.getInt(data, 56))/100, TimeUnit.HOURS));
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
}

