/*
 * file:       MPP9File.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2002-2003
 * date:       22/05/2003
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeMap;

import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.DocumentInputStream;

import com.tapsterrock.mpx.AccrueType;
import com.tapsterrock.mpx.BaseCalendar;
import com.tapsterrock.mpx.BaseCalendarException;
import com.tapsterrock.mpx.BaseCalendarHours;
import com.tapsterrock.mpx.ConstraintType;
import com.tapsterrock.mpx.CurrencySettings;
import com.tapsterrock.mpx.DefaultSettings;
import com.tapsterrock.mpx.MPXDuration;
import com.tapsterrock.mpx.MPXException;
import com.tapsterrock.mpx.MPXRate;
import com.tapsterrock.mpx.Priority;
import com.tapsterrock.mpx.Relation;
import com.tapsterrock.mpx.Resource;
import com.tapsterrock.mpx.Task;
import com.tapsterrock.mpx.TimeUnit;

/**
 * This class is used to represent a Microsoft Project MPP9 file. This
 * implementation allows the file to be read, and the data it contains
 * exported as a set of MPX objects. These objects can be interrogated
 * to retrieve any required data, or stored as an MPX file.
 */
final class MPP9File
{
	/**
	 * This method is used to process an MPP9 file. This is the file format
	 * used by Project 2000 and Project 2002.
	 * 
	 * @param root Root of the POI file system.
	 * @throws MPXException Normally thrown on dat validation errors
	 */
	static void process (MPPFile file, DirectoryEntry root)
		throws MPXException, IOException
	{
		//
		// Retrieve the project directory
		//
		DirectoryEntry projectDir = (DirectoryEntry)root.getEntry ("   19");
                  
		//
		// Extract the required data from the MPP file
		//
      processPropertyData (file, projectDir);      
		processCalendarData (file, projectDir);
  		processResourceData (file, projectDir);
	  	processTaskData (file, projectDir);
	  	processConstraintData (file, projectDir);
	  	processAssignmentData (file, projectDir);
	}
	
   
   /**
    * This method extracts and collates global property data.
    * 
    * @param file Parent MPX file
    * @param projectDir Project data directory
    * @throws MPXException
    * @throws IOException
    */   
   private static void processPropertyData (MPPFile file,  DirectoryEntry projectDir)
      throws MPXException, IOException
   {
      Props props = new Props (new DocumentInputStream (((DocumentEntry)projectDir.getEntry("Props"))));
      
      DefaultSettings ds = file.getDefaultSettings();           
      //ds.setDefaultDurationIsFixed();
      ds.setDefaultDurationUnits(MPPUtility.getDurationUnits(props.getShort(Props.DURATION_UNITS)));
      ds.setDefaultEndTime(props.getTime(Props.END_TIME));
      ds.setDefaultHoursInDay(((float)props.getInt(Props.HOURS_PER_DAY))/60);
      ds.setDefaultHoursInWeek(((float)props.getInt(Props.HOURS_PER_WEEK))/60);
      ds.setDefaultOvertimeRate(new MPXRate (props.getDouble(Props.OVERTIME_RATE), TimeUnit.HOURS));
      ds.setDefaultStandardRate(new MPXRate (props.getDouble(Props.STANDARD_RATE), TimeUnit.HOURS));
      ds.setDefaultStartTime(props.getTime(Props.START_TIME));
      ds.setDefaultWorkUnits(MPPUtility.getWorkUnits(props.getShort(Props.WORK_UNITS)));
      ds.setSplitInProgressTasks(props.getBoolean(Props.SPLIT_TASKS));
      ds.setUpdatingTaskStatusUpdatesResourceStatus(props.getBoolean(Props.TASK_UPDATES_RESOURCE));

      CurrencySettings cs = file.getCurrencySettings();
      cs.setCurrencyDigits(props.getShort(Props.CURRENCY_DIGITS));
      cs.setCurrencySymbol(props.getUnicodeString(Props.CURRENCY_SYMBOL));
      //cs.setDecimalSeparator();
      cs.setSymbolPosition(MPPUtility.getSymbolPosition(props.getShort(Props.CURRENCY_PLACEMENT)));
      //cs.setThousandsSeparator();
   }
   
   /**
    * This method maps the task unique identifiers to their index number
    * within the FixedData block.
    *
    * @param taskFixedMeta Fixed meta data for this task
    * @param taskFixedData Fixed data for this task
    * @return Mapping between task identifiers and block position
    */
   private static TreeMap createTaskMap (FixedMeta taskFixedMeta, FixedData taskFixedData)
   {
      TreeMap taskMap = new TreeMap ();
      int itemCount = taskFixedMeta.getItemCount();
      byte[] data;
      int uniqueID;

      for (int loop=0; loop < itemCount; loop++)
      {
         data = taskFixedData.getByteArrayValue(loop);
         if (data != null && data.length > 4)
         {
            uniqueID = MPPUtility.getInt (data, 0);
            taskMap.put(new Integer (uniqueID), new Integer (loop));
         }
      }

      return (taskMap);
   }

   /**
    * This method creates a mapping between task unique identifiers, and the
    * entries in the fixed meta data block.
    * 
    * @param taskFixedMeta Fixed meta data block
    * @return mapping
    */
   private static TreeMap createTaskMetaMap (FixedMeta taskFixedMeta)
   {
      TreeMap taskMetaMap = new TreeMap ();
      int itemCount = taskFixedMeta.getItemCount();
      byte[] data;
      int uniqueID;
      
      for (int loop=0; loop < itemCount; loop++)
      {
         data = taskFixedMeta.getByteArrayValue(loop);
         if (data != null && data.length > 6)
         {
            uniqueID = MPPUtility.getShort (data, 5);
            taskMetaMap.put(new Integer (uniqueID), new Integer (loop));
         }
      }

      return (taskMetaMap);
   }

   /**
    * This method maps the resource unique identifiers to their index number
    * within the FixedData block.
    */
   private static TreeMap createResourceMap (FixedMeta rscFixedMeta, FixedData rscFixedData)
   {
      TreeMap resourceMap = new TreeMap ();
      int itemCount = rscFixedMeta.getItemCount();
      byte[] data;
      int uniqueID;

      for (int loop=0; loop < itemCount; loop++)
      {
         data = rscFixedData.getByteArrayValue(loop);
         if (data != null && data.length > 4)
         {         
	         uniqueID = MPPUtility.getInt (data, 0);
	         resourceMap.put(new Integer (uniqueID), new Integer (loop));
         }	         
      }

      return (resourceMap);
   }

   
   /**
    * The format of the calandar data is a 4 byte header followed
    * by 7x 60 byte blocks, one for each day of the week. Optionally
    * following this is a set of 64 byte blocks representing exceptions
    * to the calendar.
    *
    * @throws Exception on unexpected file format
    */
   private static void processCalendarData (MPPFile file,  DirectoryEntry projectDir)
      throws MPXException, IOException
   {
      DirectoryEntry calDir = (DirectoryEntry)projectDir.getEntry ("TBkndCal");
      VarMeta calVarMeta = new VarMeta (new DocumentInputStream (((DocumentEntry)calDir.getEntry("VarMeta"))));
      Var2Data calVarData = new Var2Data (calVarMeta, new DocumentInputStream (((DocumentEntry)calDir.getEntry("Var2Data"))));
      
      Integer[] uniqueid = calVarMeta.getUniqueIdentifiers();
      Integer id;
      BaseCalendar cal;
      BaseCalendarHours hours;
      BaseCalendarException exception;
      String name;
      byte[] data;
      int periodCount;
      int index;
      int offset;
      int defaultFlag;
      Date start;
      long duration;
      int exceptionCount;

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

      for (int loop=0; loop < uniqueid.length; loop++)
      {
         id = uniqueid[loop];
         name = calVarData.getUnicodeString (id, CALENDAR_NAME);
         data = calVarData.getByteArray (id, CALENDAR_DATA);
         if (data == null)
         {
            cal = file.addDefaultBaseCalendar();
            cal.setName (name);
         }
         else
         {
            //
            // Populate the basic calendar
            //
            cal = file.addBaseCalendar();
            cal.setName (name);

            for (index=0; index < 7; index++)
            {
               offset = 4 + (60 * index);
               defaultFlag = MPPUtility.getShort (data, offset);

               if (defaultFlag == 1)
               {
                  cal.setWorkingDay(index+1, DEFAULT_WORKING_WEEK[index]);
                  if (cal.isWorkingDay(index+1) == true)
                  {
                     hours = cal.addBaseCalendarHours(index+1);
                     hours.setFromTime1(defaultStart1);
                     hours.setToTime1(defaultEnd1);
                     hours.setFromTime2(defaultStart2);
                     hours.setToTime2(defaultEnd2);
                  }
               }
               else
               {
                  periodCount = MPPUtility.getShort (data, offset+2);
                  if (periodCount == 0)
                  {
                     cal.setWorkingDay(index+1, false);
                  }
                  else
                  {
                     cal.setWorkingDay(index+1, true);
                     hours = cal.addBaseCalendarHours(index+1);

                     start = MPPUtility.getTime (data, offset + 8);
                     duration = MPPUtility.getDuration (data, offset + 20);
                     hours.setFromTime1(start);
                     hours.setToTime1(new Date (start.getTime()+duration));
						
                     if (periodCount > 1)
                     {
                        start = MPPUtility.getTime (data, offset + 10);
                        duration = MPPUtility.getDuration (data, offset + 24);
                        hours.setFromTime2(start);
                        hours.setToTime2(new Date (start.getTime()+duration));

                        if (periodCount > 2)
                        {                        	
                           start = MPPUtility.getTime (data, offset + 12);
                           duration = MPPUtility.getDuration (data, offset + 28);
                           hours.setFromTime3(start);
                           hours.setToTime3(new Date (start.getTime()+duration));
                        }
                     }

                     // Note that MPP defines 5 time ranges, the additional
                     // start times are at offsets 14, 16 and the additional
                     // durations are at offsets 32 and 36.
                  }
               }
            }

            //
            // Handle any exceptions
            //
            exceptionCount = MPPUtility.getShort (data, 0);
            if (exceptionCount != 0)
            {
               for (index=0; index < exceptionCount; index++)
               {
                  offset = 4 + (60 * 7) + (index * 64);
                  exception = cal.addBaseCalendarException();
                  exception.setFromDate(MPPUtility.getDate (data, offset));
                  exception.setToDate(MPPUtility.getDate (data, offset+2));

                  periodCount = MPPUtility.getShort (data, offset+6);
                  if (periodCount == 0)
                  {
                     exception.setWorking (false);
                  }
                  else
                  {
                     exception.setWorking (true);

                     start = MPPUtility.getTime (data, offset+12);
                     duration = MPPUtility.getDuration (data, offset+24);
                     exception.setFromTime1(start);
                     exception.setToTime1(new Date (start.getTime() + duration));

                     if (periodCount > 1)
                     {
                        start = MPPUtility.getTime (data, offset+14);
                        duration = MPPUtility.getDuration (data, offset+28);
                        exception.setFromTime2(start);
                        exception.setToTime2(new Date (start.getTime() + duration));

                        if (periodCount > 2)
                        {
                           start = MPPUtility.getTime (data, offset+16);
                           duration = MPPUtility.getDuration (data, offset+32);
                           exception.setFromTime3(start);
                           exception.setToTime3(new Date (start.getTime() + duration));
                        }
                     }
                     //
                     // Note that MPP defines 5 time ranges rather than 3
                     //
                  }
               }
            }
         }
      }
   }


   /**
    * This method extracts and collates task data. The code below
    * goes through the modifier methods of the Task class in alphabetical
    * order extracting the data from the MPP file. Where there is no
    * mapping (e.g. the field is calculated on the fly, or we can't
    * find it in the data) the line is commented out.
    *
    * The missing boolean attributes are probably represented in the Props
    * section of the task data, which we have yet to decode.
    *
    * @throws Exception on unexpected file format
    */
   private static void processTaskData (MPPFile file,  DirectoryEntry projectDir)
      throws MPXException, IOException
   {
      DirectoryEntry taskDir = (DirectoryEntry)projectDir.getEntry ("TBkndTask");
      VarMeta taskVarMeta = new VarMeta (new DocumentInputStream (((DocumentEntry)taskDir.getEntry("VarMeta"))));
      Var2Data taskVarData = new Var2Data (taskVarMeta, new DocumentInputStream (((DocumentEntry)taskDir.getEntry("Var2Data"))));
      FixedMeta taskFixedMeta = new FixedMeta (new DocumentInputStream (((DocumentEntry)taskDir.getEntry("FixedMeta"))), 47);
      FixedData taskFixedData = new FixedData (taskFixedMeta, new DocumentInputStream (((DocumentEntry)taskDir.getEntry("FixedData"))));
      
      TreeMap taskMap = createTaskMap (taskFixedMeta, taskFixedData);
      TreeMap taskMetaMap = createTaskMetaMap (taskFixedMeta);
      Integer[] uniqueid = taskVarMeta.getUniqueIdentifiers();
      Integer id;
      Integer offset;
      byte[] data;
      byte[] metaData;
      Task task;
      RTFUtility rtf = new RTFUtility ();
      String notes;                  

      for (int loop=0; loop < uniqueid.length; loop++)
      {
         id = uniqueid[loop];
         
         offset = (Integer)taskMap.get(id);
         if (offset == null)
         {
            throw new MPXException (MPXException.INVALID_FILE);
         }

         data = taskFixedData.getByteArrayValue(offset.intValue());        
                  
         if (data.length < MINIMUM_EXPECTED_TASK_SIZE)
         {
            continue;
         }
			           
         task = file.addTask();
         task.setActualCost(new Double (MPPUtility.getDouble (data, 216) / 100));
         task.setActualDuration(MPPUtility.getDuration (MPPUtility.getInt (data, 66), MPPUtility.getDurationUnits(MPPUtility.getShort (data, 64))));
         task.setActualFinish(MPPUtility.getTimestamp (data, 100));
         task.setActualStart(MPPUtility.getTimestamp (data, 96));
         task.setActualWork(new MPXDuration (MPPUtility.getDouble (data, 184)/60000, TimeUnit.HOURS));
         task.setBaselineCost(new Double (MPPUtility.getDouble (data, 232) / 100));
         task.setBaselineDuration(MPPUtility.getDuration (MPPUtility.getInt (data, 74), MPPUtility.getDurationUnits (MPPUtility.getShort (data, 78))));
         task.setBaselineFinish(MPPUtility.getTimestamp (data, 108));
         task.setBaselineStart(MPPUtility.getTimestamp (data, 104));
         task.setBaselineWork(new MPXDuration (MPPUtility.getDouble (data, 176)/60000, TimeUnit.HOURS));
         //task.setBCWP(); // Calculated value
         //task.setBCWS(); // Calculated value
         //task.setConfirmed(); // Calculated value
         task.setConstraintDate (MPPUtility.getTimestamp (data, 112));
         task.setConstraintType (ConstraintType.getInstance (MPPUtility.getShort (data, 80)));
         task.setContact(taskVarData.getUnicodeString (id, TASK_CONTACT));
         task.setCost(new Double (MPPUtility.getDouble(data, 200) / 100));
         task.setCost1(new Double (taskVarData.getDouble (id, TASK_COST1) / 100));
         task.setCost2(new Double (taskVarData.getDouble (id, TASK_COST2) / 100));
         task.setCost3(new Double (taskVarData.getDouble (id, TASK_COST3) / 100));
         task.setCost4(new Double (taskVarData.getDouble (id, TASK_COST4) / 100));
         task.setCost5(new Double (taskVarData.getDouble (id, TASK_COST5) / 100));
         task.setCost6(new Double (taskVarData.getDouble (id, TASK_COST6) / 100));
         task.setCost7(new Double (taskVarData.getDouble (id, TASK_COST7) / 100));
         task.setCost8(new Double (taskVarData.getDouble (id, TASK_COST8) / 100));
         task.setCost9(new Double (taskVarData.getDouble (id, TASK_COST9) / 100));
         task.setCost10(new Double (taskVarData.getDouble (id, TASK_COST10) / 100));         
         task.setCreated(MPPUtility.getTimestamp (data, 130));
         //task.setCritical(); // Calculated value
         //task.setCV(); // Calculated value
         task.setDate1(taskVarData.getTimestamp (id, TASK_DATE1));
         task.setDate2(taskVarData.getTimestamp (id, TASK_DATE2));         
         task.setDate3(taskVarData.getTimestamp (id, TASK_DATE3));
         task.setDate4(taskVarData.getTimestamp (id, TASK_DATE4));
         task.setDate5(taskVarData.getTimestamp (id, TASK_DATE5));
         task.setDate6(taskVarData.getTimestamp (id, TASK_DATE6));
         task.setDate7(taskVarData.getTimestamp (id, TASK_DATE7));
         task.setDate8(taskVarData.getTimestamp (id, TASK_DATE8));
         task.setDate9(taskVarData.getTimestamp (id, TASK_DATE9));
         task.setDate10(taskVarData.getTimestamp (id, TASK_DATE10));                                                                        
         task.setDeadline (MPPUtility.getTimestamp (data, 152));
         //task.setDelay(); // Field does not appear in Project 2000
         task.setDuration (MPPUtility.getDuration (MPPUtility.getInt (data, 60), MPPUtility.getDurationUnits(MPPUtility.getShort (data, 64))));
         task.setDuration1(MPPUtility.getDuration (taskVarData.getInt(id, TASK_DURATION1), MPPUtility.getDurationUnits(taskVarData.getShort(id, TASK_DURATION1_UNITS))));
         task.setDuration2(MPPUtility.getDuration (taskVarData.getInt(id, TASK_DURATION2), MPPUtility.getDurationUnits(taskVarData.getShort(id, TASK_DURATION2_UNITS))));
         task.setDuration3(MPPUtility.getDuration (taskVarData.getInt(id, TASK_DURATION3), MPPUtility.getDurationUnits(taskVarData.getShort(id, TASK_DURATION3_UNITS))));
         task.setDuration4(MPPUtility.getDuration (taskVarData.getInt(id, TASK_DURATION4), MPPUtility.getDurationUnits(taskVarData.getShort(id, TASK_DURATION4_UNITS))));
         task.setDuration5(MPPUtility.getDuration (taskVarData.getInt(id, TASK_DURATION5), MPPUtility.getDurationUnits(taskVarData.getShort(id, TASK_DURATION5_UNITS))));
         task.setDuration6(MPPUtility.getDuration (taskVarData.getInt(id, TASK_DURATION6), MPPUtility.getDurationUnits(taskVarData.getShort(id, TASK_DURATION6_UNITS))));
         task.setDuration7(MPPUtility.getDuration (taskVarData.getInt(id, TASK_DURATION7), MPPUtility.getDurationUnits(taskVarData.getShort(id, TASK_DURATION7_UNITS))));
         task.setDuration8(MPPUtility.getDuration (taskVarData.getInt(id, TASK_DURATION8), MPPUtility.getDurationUnits(taskVarData.getShort(id, TASK_DURATION8_UNITS))));
         task.setDuration9(MPPUtility.getDuration (taskVarData.getInt(id, TASK_DURATION9), MPPUtility.getDurationUnits(taskVarData.getShort(id, TASK_DURATION9_UNITS))));
         task.setDuration10(MPPUtility.getDuration (taskVarData.getInt(id, TASK_DURATION10), MPPUtility.getDurationUnits(taskVarData.getShort(id, TASK_DURATION10_UNITS))));
         //task.setDurationVariance(); // Calculated value
         //task.setEarlyFinish(); // Calculated value
         //task.setEarlyStart(); // Calculated value
         task.setEstimated(getDurationEstimated(MPPUtility.getShort (data, 64)));
         task.setFinish (MPPUtility.getTimestamp (data, 8));
         task.setFinish1(taskVarData.getTimestamp (id, TASK_FINISH1));
         task.setFinish2(taskVarData.getTimestamp (id, TASK_FINISH2));
         task.setFinish3(taskVarData.getTimestamp (id, TASK_FINISH3));
         task.setFinish4(taskVarData.getTimestamp (id, TASK_FINISH4));
         task.setFinish5(taskVarData.getTimestamp (id, TASK_FINISH5));
         task.setFinish6(taskVarData.getTimestamp (id, TASK_FINISH6));
         task.setFinish7(taskVarData.getTimestamp (id, TASK_FINISH7));
         task.setFinish8(taskVarData.getTimestamp (id, TASK_FINISH8));
         task.setFinish9(taskVarData.getTimestamp (id, TASK_FINISH9));
         task.setFinish10(taskVarData.getTimestamp (id, TASK_FINISH10));
         //task.setFinishVariance(); // Calculated value
         //task.setFixed(); // Unsure of mapping from MPX->MSP2K
         task.setFixedCost(new Double (MPPUtility.getDouble (data, 208) / 100));                        
         //task.setFreeSlack();  // Calculated value
         task.setID (MPPUtility.getInt (data, 4));
         //task.setLateFinish();  // Calculated value
         //task.setLateStart();  // Calculated value
         //task.setLinkedFields();  // Calculated value
         //task.setMarked();
         task.setName(taskVarData.getUnicodeString (id, TASK_NAME));
         task.setNumber1(new Double (taskVarData.getDouble(id, TASK_NUMBER1)));
         task.setNumber2(new Double (taskVarData.getDouble(id, TASK_NUMBER2)));
         task.setNumber3(new Double (taskVarData.getDouble(id, TASK_NUMBER3)));
         task.setNumber4(new Double (taskVarData.getDouble(id, TASK_NUMBER4)));
         task.setNumber5(new Double (taskVarData.getDouble(id, TASK_NUMBER5)));
         task.setNumber6(new Double (taskVarData.getDouble(id, TASK_NUMBER6)));
         task.setNumber7(new Double (taskVarData.getDouble(id, TASK_NUMBER7)));
         task.setNumber8(new Double (taskVarData.getDouble(id, TASK_NUMBER8)));
         task.setNumber9(new Double (taskVarData.getDouble(id, TASK_NUMBER9)));
         task.setNumber10(new Double (taskVarData.getDouble(id, TASK_NUMBER10)));         
         task.setNumber11(new Double (taskVarData.getDouble(id, TASK_NUMBER11)));
         task.setNumber12(new Double (taskVarData.getDouble(id, TASK_NUMBER12)));
         task.setNumber13(new Double (taskVarData.getDouble(id, TASK_NUMBER13)));
         task.setNumber14(new Double (taskVarData.getDouble(id, TASK_NUMBER14)));
         task.setNumber15(new Double (taskVarData.getDouble(id, TASK_NUMBER15)));
         task.setNumber16(new Double (taskVarData.getDouble(id, TASK_NUMBER16)));
         task.setNumber17(new Double (taskVarData.getDouble(id, TASK_NUMBER17)));
         task.setNumber18(new Double (taskVarData.getDouble(id, TASK_NUMBER18)));
         task.setNumber19(new Double (taskVarData.getDouble(id, TASK_NUMBER19)));
         task.setNumber20(new Double (taskVarData.getDouble(id, TASK_NUMBER20)));                                                                                                   
         //task.setObjects(); // Calculated value
         task.setOutlineLevel (MPPUtility.getShort (data, 40));
         //task.setOutlineNumber(); // Calculated value
         task.setOvertimeCost(new Double (MPPUtility.getDouble (data, 240)/100));         
         task.setPercentageComplete((double)MPPUtility.getShort(data, 122));
         task.setPercentageWorkComplete((double)MPPUtility.getShort(data, 124));
         task.setPriority(getPriority (MPPUtility.getShort (data, 120)));
         //task.setProject(); // Calculated value
         task.setRemainingCost(new Double (MPPUtility.getDouble (data, 224)/100));
         task.setRemainingDuration(MPPUtility.getDuration (MPPUtility.getInt (data, 70), MPPUtility.getDurationUnits(MPPUtility.getShort (data, 64))));
         task.setRemainingWork(new MPXDuration (MPPUtility.getDouble (data, 192)/60000, TimeUnit.HOURS));
         //task.setResourceGroup(); // Calculated value from resource
         //task.setResourceInitials(); // Calculated value from resource
         //task.setResourceNames(); // Calculated value from resource
         task.setResume(MPPUtility.getTimestamp(data, 20));
         //task.setResumeNoEarlierThan(); // No mapping in MSP2K?
         task.setStart (MPPUtility.getTimestamp (data, 88));
         task.setStart1(taskVarData.getTimestamp (id, TASK_START1));
         task.setStart2(taskVarData.getTimestamp (id, TASK_START2));
         task.setStart3(taskVarData.getTimestamp (id, TASK_START3));
         task.setStart4(taskVarData.getTimestamp (id, TASK_START4));
         task.setStart5(taskVarData.getTimestamp (id, TASK_START5));
         task.setStart6(taskVarData.getTimestamp (id, TASK_START6));
         task.setStart7(taskVarData.getTimestamp (id, TASK_START7));
         task.setStart8(taskVarData.getTimestamp (id, TASK_START8));
         task.setStart9(taskVarData.getTimestamp (id, TASK_START9));
         task.setStart10(taskVarData.getTimestamp (id, TASK_START10));         
         //task.setStartVariance(); // Calculated value
         task.setStop(MPPUtility.getTimestamp (data, 16));
         //task.setSubprojectFile();
         //task.setSV(); // Calculated value
         task.setText1(taskVarData.getUnicodeString (id, TASK_TEXT1));
         task.setText2(taskVarData.getUnicodeString (id, TASK_TEXT2));
         task.setText3(taskVarData.getUnicodeString (id, TASK_TEXT3));
         task.setText4(taskVarData.getUnicodeString (id, TASK_TEXT4));
         task.setText5(taskVarData.getUnicodeString (id, TASK_TEXT5));
         task.setText6(taskVarData.getUnicodeString (id, TASK_TEXT6));
         task.setText7(taskVarData.getUnicodeString (id, TASK_TEXT7));
         task.setText8(taskVarData.getUnicodeString (id, TASK_TEXT8));
         task.setText9(taskVarData.getUnicodeString (id, TASK_TEXT9));
         task.setText10(taskVarData.getUnicodeString (id, TASK_TEXT10));
         task.setText11(taskVarData.getUnicodeString (id, TASK_TEXT11));
         task.setText12(taskVarData.getUnicodeString (id, TASK_TEXT12));
         task.setText13(taskVarData.getUnicodeString (id, TASK_TEXT13));
         task.setText14(taskVarData.getUnicodeString (id, TASK_TEXT14));
         task.setText15(taskVarData.getUnicodeString (id, TASK_TEXT15));
         task.setText16(taskVarData.getUnicodeString (id, TASK_TEXT16));
         task.setText17(taskVarData.getUnicodeString (id, TASK_TEXT17));
         task.setText18(taskVarData.getUnicodeString (id, TASK_TEXT18));
         task.setText19(taskVarData.getUnicodeString (id, TASK_TEXT19));
         task.setText20(taskVarData.getUnicodeString (id, TASK_TEXT20));
         task.setText21(taskVarData.getUnicodeString (id, TASK_TEXT21));
         task.setText22(taskVarData.getUnicodeString (id, TASK_TEXT22));
         task.setText23(taskVarData.getUnicodeString (id, TASK_TEXT23));
         task.setText24(taskVarData.getUnicodeString (id, TASK_TEXT24));
         task.setText25(taskVarData.getUnicodeString (id, TASK_TEXT25));
         task.setText26(taskVarData.getUnicodeString (id, TASK_TEXT26));
         task.setText27(taskVarData.getUnicodeString (id, TASK_TEXT27));
         task.setText28(taskVarData.getUnicodeString (id, TASK_TEXT28));
         task.setText29(taskVarData.getUnicodeString (id, TASK_TEXT29));
         task.setText30(taskVarData.getUnicodeString (id, TASK_TEXT30));
         //task.setTotalSlack(); // Calculated value
         task.setType(MPPUtility.getShort(data, 126));
         task.setUniqueID(id.intValue());
         //task.setUpdateNeeded(); // Calculated value
         task.setWBS(taskVarData.getUnicodeString (id, TASK_WBS));
         task.setWork(new MPXDuration (MPPUtility.getDouble (data, 168)/60000, TimeUnit.HOURS));
         //task.setWorkVariance(); // Calculated value


         offset = (Integer)taskMetaMap.get(id);
         if (offset != null)
         {
            metaData = taskFixedMeta.getByteArrayValue(offset.intValue());

            task.setFlag1((metaData[37] & 0x20) != 0);
            task.setFlag2((metaData[37] & 0x40) != 0);
            task.setFlag3((metaData[37] & 0x80) != 0);
            task.setFlag4((metaData[38] & 0x01) != 0);
            task.setFlag5((metaData[38] & 0x02) != 0);
            task.setFlag6((metaData[38] & 0x04) != 0);
            task.setFlag7((metaData[38] & 0x08) != 0);
            task.setFlag8((metaData[38] & 0x10) != 0);
            task.setFlag9((metaData[38] & 0x20) != 0);
            task.setFlag10((metaData[38] & 0x40) != 0);            
            task.setFlag11((metaData[38] & 0x80) != 0);
            task.setFlag12((metaData[39] & 0x01) != 0);
            task.setFlag13((metaData[39] & 0x02) != 0);
            task.setFlag14((metaData[39] & 0x04) != 0);
            task.setFlag15((metaData[39] & 0x08) != 0);
            task.setFlag16((metaData[39] & 0x10) != 0);
            task.setFlag17((metaData[39] & 0x20) != 0);
            task.setFlag18((metaData[39] & 0x40) != 0);
            task.setFlag19((metaData[39] & 0x80) != 0);
            task.setFlag20((metaData[40] & 0x01) != 0);
            
            task.setMilestone((metaData[8] & 0x20) != 0);
            task.setRollup((metaData[10] & 0x08) != 0);            
            task.setHideBar((metaData[10] & 0x80) != 0);                        
            task.setEffortDriven((metaData[11] & 0x10) != 0);  
         }

			//
			// Retrieve the task notes.
			//			
         notes = taskVarData.getString (id, TASK_NOTES);
         if (notes != null)
         {
            if (file.getPreserveNoteFormatting() == false)
            {
               notes = rtf.strip(notes);
            }
                                      
            task.addTaskNotes(notes);
         }
         
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
    * This method extracts and collates constraint data
    */
   private static void processConstraintData (MPPFile file,  DirectoryEntry projectDir)
      throws IOException   
   {      
      DirectoryEntry consDir = (DirectoryEntry)projectDir.getEntry ("TBkndCons");
      FixedMeta consFixedMeta = new FixedMeta (new DocumentInputStream (((DocumentEntry)consDir.getEntry("FixedMeta"))), 10);      
      FixedData consFixedData = new FixedData (20, new DocumentInputStream (((DocumentEntry)consDir.getEntry("FixedData"))));
      
      int count = consFixedMeta.getItemCount();
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
         metaData = consFixedMeta.getByteArrayValue(loop);
         
         if (MPPUtility.getInt(metaData, 0) == 0)
         {
            index = consFixedData.getIndexFromOffset(MPPUtility.getInt(metaData, 4));
            if (index != -1)
            {
               data = consFixedData.getByteArrayValue(index);
               taskID1 = MPPUtility.getInt (data, 4);
               taskID2 = MPPUtility.getInt (data, 8);

               if (taskID1 != taskID2)
               {
                  task1 = file.getTaskByUniqueID (taskID1);
                  task2 = file.getTaskByUniqueID (taskID2);
                  if (task1 != null && task2 != null)
                  {
                     rel = task2.addPredecessor(task1);
                     rel.setType (MPPUtility.getShort(data, 12));
                     durationUnits = MPPUtility.getDurationUnits(MPPUtility.getShort (data, 14));
                     rel.setDuration(MPPUtility.getDuration (MPPUtility.getInt (data, 16), durationUnits));
                  }
               }               
            }
         }
      }      
   }


   /**
    * This method extracts and collates resource data
    *
    * @throws Exception on unexpected file format
    */
   private static void processResourceData (MPPFile file,  DirectoryEntry projectDir)
      throws MPXException, IOException
   {
      DirectoryEntry rscDir = (DirectoryEntry)projectDir.getEntry ("TBkndRsc");
      VarMeta rscVarMeta = new VarMeta (new DocumentInputStream (((DocumentEntry)rscDir.getEntry("VarMeta"))));
      Var2Data rscVarData = new Var2Data (rscVarMeta, new DocumentInputStream (((DocumentEntry)rscDir.getEntry("Var2Data"))));
      FixedMeta rscFixedMeta = new FixedMeta (new DocumentInputStream (((DocumentEntry)rscDir.getEntry("FixedMeta"))), 37);
      FixedData rscFixedData = new FixedData (rscFixedMeta, new DocumentInputStream (((DocumentEntry)rscDir.getEntry("FixedData"))));
               
      TreeMap resourceMap = createResourceMap (rscFixedMeta, rscFixedData);
      Integer[] uniqueid = rscVarMeta.getUniqueIdentifiers();
      Integer id;
      Integer offset;
      byte[] data;
      byte[] metaData;      
      Resource resource;

      RTFUtility rtf = new RTFUtility ();      
      String notes;            

      for (int loop=0; loop < uniqueid.length; loop++)
      {
         id = uniqueid[loop];
         offset = (Integer)resourceMap.get(id);
         if (offset == null)
         {
            throw new MPXException (MPXException.INVALID_FILE);
         }
         
         data = rscFixedData.getByteArrayValue(offset.intValue());

         resource = file.addResource();

         resource.setAccrueAt(AccrueType.getInstance (MPPUtility.getShort (data, 12)));
         resource.setActualCost(new Double(MPPUtility.getDouble(data, 132)/100));
         resource.setActualOvertimeCost(new Double(MPPUtility.getDouble(data, 172)/100));         
         resource.setActualWork(new MPXDuration (MPPUtility.getDouble (data, 60)/60000, TimeUnit.HOURS));
         //resource.setBaseCalendar();
         resource.setBaselineCost(new Double(MPPUtility.getDouble(data, 148)/100));
         resource.setBaselineWork(new MPXDuration (MPPUtility.getDouble (data, 68)/60000, TimeUnit.HOURS));
         resource.setCode (rscVarData.getUnicodeString (id, RESOURCE_CODE));
         resource.setCost(new Double(MPPUtility.getDouble(data, 140)/100));         
         resource.setCost1(new Double (rscVarData.getDouble (id, RESOURCE_COST1) / 100));
         resource.setCost2(new Double (rscVarData.getDouble (id, RESOURCE_COST2) / 100));
         resource.setCost3(new Double (rscVarData.getDouble (id, RESOURCE_COST3) / 100));
         resource.setCost4(new Double (rscVarData.getDouble (id, RESOURCE_COST4) / 100));
         resource.setCost5(new Double (rscVarData.getDouble (id, RESOURCE_COST5) / 100));
         resource.setCost6(new Double (rscVarData.getDouble (id, RESOURCE_COST6) / 100));
         resource.setCost7(new Double (rscVarData.getDouble (id, RESOURCE_COST7) / 100));
         resource.setCost8(new Double (rscVarData.getDouble (id, RESOURCE_COST8) / 100));
         resource.setCost9(new Double (rscVarData.getDouble (id, RESOURCE_COST9) / 100));
         resource.setCost10(new Double (rscVarData.getDouble (id, RESOURCE_COST10) / 100));         
         resource.setCostPerUse(new Double(MPPUtility.getDouble(data, 84)/100));                  
         resource.setDate1(rscVarData.getTimestamp (id, RESOURCE_DATE1));
         resource.setDate2(rscVarData.getTimestamp (id, RESOURCE_DATE2));
         resource.setDate3(rscVarData.getTimestamp (id, RESOURCE_DATE3));
         resource.setDate4(rscVarData.getTimestamp (id, RESOURCE_DATE4));
         resource.setDate5(rscVarData.getTimestamp (id, RESOURCE_DATE5));
         resource.setDate6(rscVarData.getTimestamp (id, RESOURCE_DATE6));
         resource.setDate7(rscVarData.getTimestamp (id, RESOURCE_DATE7));
         resource.setDate8(rscVarData.getTimestamp (id, RESOURCE_DATE8));
         resource.setDate9(rscVarData.getTimestamp (id, RESOURCE_DATE9));
         resource.setDate10(rscVarData.getTimestamp (id, RESOURCE_DATE10));                           
         resource.setDuration1(MPPUtility.getDuration (rscVarData.getInt(id, RESOURCE_DURATION1), MPPUtility.getDurationUnits(rscVarData.getShort(id, RESOURCE_DURATION1_UNITS))));
         resource.setDuration2(MPPUtility.getDuration (rscVarData.getInt(id, RESOURCE_DURATION2), MPPUtility.getDurationUnits(rscVarData.getShort(id, RESOURCE_DURATION2_UNITS))));
         resource.setDuration3(MPPUtility.getDuration (rscVarData.getInt(id, RESOURCE_DURATION3), MPPUtility.getDurationUnits(rscVarData.getShort(id, RESOURCE_DURATION3_UNITS))));
         resource.setDuration4(MPPUtility.getDuration (rscVarData.getInt(id, RESOURCE_DURATION4), MPPUtility.getDurationUnits(rscVarData.getShort(id, RESOURCE_DURATION4_UNITS))));
         resource.setDuration5(MPPUtility.getDuration (rscVarData.getInt(id, RESOURCE_DURATION5), MPPUtility.getDurationUnits(rscVarData.getShort(id, RESOURCE_DURATION5_UNITS))));
         resource.setDuration6(MPPUtility.getDuration (rscVarData.getInt(id, RESOURCE_DURATION6), MPPUtility.getDurationUnits(rscVarData.getShort(id, RESOURCE_DURATION6_UNITS))));
         resource.setDuration7(MPPUtility.getDuration (rscVarData.getInt(id, RESOURCE_DURATION7), MPPUtility.getDurationUnits(rscVarData.getShort(id, RESOURCE_DURATION7_UNITS))));
         resource.setDuration8(MPPUtility.getDuration (rscVarData.getInt(id, RESOURCE_DURATION8), MPPUtility.getDurationUnits(rscVarData.getShort(id, RESOURCE_DURATION8_UNITS))));
         resource.setDuration9(MPPUtility.getDuration (rscVarData.getInt(id, RESOURCE_DURATION9), MPPUtility.getDurationUnits(rscVarData.getShort(id, RESOURCE_DURATION9_UNITS))));
         resource.setDuration10(MPPUtility.getDuration (rscVarData.getInt(id, RESOURCE_DURATION10), MPPUtility.getDurationUnits(rscVarData.getShort(id, RESOURCE_DURATION10_UNITS))));                                    
         resource.setEmailAddress(rscVarData.getUnicodeString (id, RESOURCE_EMAIL));         
         resource.setFinish1(rscVarData.getTimestamp (id, RESOURCE_FINISH1));         
         resource.setFinish2(rscVarData.getTimestamp (id, RESOURCE_FINISH2));
         resource.setFinish3(rscVarData.getTimestamp (id, RESOURCE_FINISH3));
         resource.setFinish4(rscVarData.getTimestamp (id, RESOURCE_FINISH4));
         resource.setFinish5(rscVarData.getTimestamp (id, RESOURCE_FINISH5));
         resource.setFinish6(rscVarData.getTimestamp (id, RESOURCE_FINISH6));
         resource.setFinish7(rscVarData.getTimestamp (id, RESOURCE_FINISH7));
         resource.setFinish8(rscVarData.getTimestamp (id, RESOURCE_FINISH8));
         resource.setFinish9(rscVarData.getTimestamp (id, RESOURCE_FINISH9));
         resource.setFinish10(rscVarData.getTimestamp (id, RESOURCE_FINISH10));
         resource.setGroup(rscVarData.getUnicodeString (id, RESOURCE_GROUP));
         resource.setID (MPPUtility.getInt (data, 4));
         resource.setInitials (rscVarData.getUnicodeString (id, RESOURCE_INITIALS));
         //resource.setLinkedFields(); // Calculated value
         resource.setMaxUnits(MPPUtility.getDouble(data, 44)/100);
         resource.setName (rscVarData.getUnicodeString (id, RESOURCE_NAME));         
         resource.setNumber1(new Double (rscVarData.getDouble(id, RESOURCE_NUMBER1)));
         resource.setNumber2(new Double (rscVarData.getDouble(id, RESOURCE_NUMBER2)));
         resource.setNumber3(new Double (rscVarData.getDouble(id, RESOURCE_NUMBER3)));
         resource.setNumber4(new Double (rscVarData.getDouble(id, RESOURCE_NUMBER4)));
         resource.setNumber5(new Double (rscVarData.getDouble(id, RESOURCE_NUMBER5)));
         resource.setNumber6(new Double (rscVarData.getDouble(id, RESOURCE_NUMBER6)));
         resource.setNumber7(new Double (rscVarData.getDouble(id, RESOURCE_NUMBER7)));
         resource.setNumber8(new Double (rscVarData.getDouble(id, RESOURCE_NUMBER8)));
         resource.setNumber9(new Double (rscVarData.getDouble(id, RESOURCE_NUMBER9)));
         resource.setNumber10(new Double (rscVarData.getDouble(id, RESOURCE_NUMBER10)));
         resource.setNumber11(new Double (rscVarData.getDouble(id, RESOURCE_NUMBER11)));
         resource.setNumber12(new Double (rscVarData.getDouble(id, RESOURCE_NUMBER12)));
         resource.setNumber13(new Double (rscVarData.getDouble(id, RESOURCE_NUMBER13)));
         resource.setNumber14(new Double (rscVarData.getDouble(id, RESOURCE_NUMBER14)));
         resource.setNumber15(new Double (rscVarData.getDouble(id, RESOURCE_NUMBER15)));
         resource.setNumber16(new Double (rscVarData.getDouble(id, RESOURCE_NUMBER16)));
         resource.setNumber17(new Double (rscVarData.getDouble(id, RESOURCE_NUMBER17)));
         resource.setNumber18(new Double (rscVarData.getDouble(id, RESOURCE_NUMBER18)));
         resource.setNumber19(new Double (rscVarData.getDouble(id, RESOURCE_NUMBER19)));
         resource.setNumber20(new Double (rscVarData.getDouble(id, RESOURCE_NUMBER20)));                           
         //resource.setObjects(); // Calculated value
         //resource.setOverallocated(); // Calculated value
			resource.setOvertimeCost(new Double(MPPUtility.getDouble(data, 164)/100));         
         resource.setOvertimeRate(new MPXRate (MPPUtility.getDouble(data, 36), TimeUnit.HOURS));
         resource.setOvertimeWork(new MPXDuration (MPPUtility.getDouble (data, 76)/60000, TimeUnit.HOURS));
         resource.setPeak(MPPUtility.getDouble(data, 124)/100);
         //resource.setPercentageWorkComplete(); // Calculated value
         resource.setRegularWork(new MPXDuration (MPPUtility.getDouble (data, 100)/60000, TimeUnit.HOURS));
         resource.setRemainingCost(new Double(MPPUtility.getDouble(data, 156)/100));
			resource.setRemainingOvertimeCost(new Double(MPPUtility.getDouble(data, 180)/100));                  
         resource.setRemainingWork(new MPXDuration (MPPUtility.getDouble (data, 92)/60000, TimeUnit.HOURS));
         resource.setStandardRate(new MPXRate (MPPUtility.getDouble(data, 28), TimeUnit.HOURS));         
         resource.setStart1(rscVarData.getTimestamp (id, RESOURCE_START1));
         resource.setStart2(rscVarData.getTimestamp (id, RESOURCE_START2));
         resource.setStart3(rscVarData.getTimestamp (id, RESOURCE_START3));
         resource.setStart4(rscVarData.getTimestamp (id, RESOURCE_START4));
         resource.setStart5(rscVarData.getTimestamp (id, RESOURCE_START5));
         resource.setStart6(rscVarData.getTimestamp (id, RESOURCE_START6));
         resource.setStart7(rscVarData.getTimestamp (id, RESOURCE_START7));
         resource.setStart8(rscVarData.getTimestamp (id, RESOURCE_START8));
         resource.setStart9(rscVarData.getTimestamp (id, RESOURCE_START9));
         resource.setStart10(rscVarData.getTimestamp (id, RESOURCE_START10));                           
         resource.setText1(rscVarData.getUnicodeString (id, RESOURCE_TEXT1));
         resource.setText2(rscVarData.getUnicodeString (id, RESOURCE_TEXT2));
         resource.setText3(rscVarData.getUnicodeString (id, RESOURCE_TEXT3));
         resource.setText4(rscVarData.getUnicodeString (id, RESOURCE_TEXT4));
         resource.setText5(rscVarData.getUnicodeString (id, RESOURCE_TEXT5));
         resource.setText6(rscVarData.getUnicodeString (id, RESOURCE_TEXT6));
         resource.setText7(rscVarData.getUnicodeString (id, RESOURCE_TEXT7));
         resource.setText8(rscVarData.getUnicodeString (id, RESOURCE_TEXT8));
         resource.setText9(rscVarData.getUnicodeString (id, RESOURCE_TEXT9));
         resource.setText10(rscVarData.getUnicodeString (id, RESOURCE_TEXT10));
         resource.setText11(rscVarData.getUnicodeString (id, RESOURCE_TEXT11));
         resource.setText12(rscVarData.getUnicodeString (id, RESOURCE_TEXT12));
         resource.setText13(rscVarData.getUnicodeString (id, RESOURCE_TEXT13));
         resource.setText14(rscVarData.getUnicodeString (id, RESOURCE_TEXT14));
         resource.setText15(rscVarData.getUnicodeString (id, RESOURCE_TEXT15));
         resource.setText16(rscVarData.getUnicodeString (id, RESOURCE_TEXT16));
         resource.setText17(rscVarData.getUnicodeString (id, RESOURCE_TEXT17));
         resource.setText18(rscVarData.getUnicodeString (id, RESOURCE_TEXT18));
         resource.setText19(rscVarData.getUnicodeString (id, RESOURCE_TEXT19));
         resource.setText20(rscVarData.getUnicodeString (id, RESOURCE_TEXT20));
         resource.setText21(rscVarData.getUnicodeString (id, RESOURCE_TEXT21));
         resource.setText22(rscVarData.getUnicodeString (id, RESOURCE_TEXT22));
         resource.setText23(rscVarData.getUnicodeString (id, RESOURCE_TEXT23));
         resource.setText24(rscVarData.getUnicodeString (id, RESOURCE_TEXT24));
         resource.setText25(rscVarData.getUnicodeString (id, RESOURCE_TEXT25));
         resource.setText26(rscVarData.getUnicodeString (id, RESOURCE_TEXT26));
         resource.setText27(rscVarData.getUnicodeString (id, RESOURCE_TEXT27));
         resource.setText28(rscVarData.getUnicodeString (id, RESOURCE_TEXT28));
         resource.setText29(rscVarData.getUnicodeString (id, RESOURCE_TEXT29));
         resource.setText30(rscVarData.getUnicodeString (id, RESOURCE_TEXT30));         
         resource.setUniqueID(id.intValue());
         resource.setWork(new MPXDuration (MPPUtility.getDouble (data, 52)/60000, TimeUnit.HOURS));

         metaData = rscFixedMeta.getByteArrayValue(offset.intValue());
         resource.setFlag1((metaData[28] & 0x40) != 0);
         resource.setFlag2((metaData[28] & 0x80) != 0);
         resource.setFlag3((metaData[29] & 0x01) != 0);
         resource.setFlag4((metaData[29] & 0x02) != 0);
         resource.setFlag5((metaData[29] & 0x04) != 0);
         resource.setFlag6((metaData[29] & 0x08) != 0);
         resource.setFlag7((metaData[29] & 0x10) != 0);
         resource.setFlag8((metaData[29] & 0x20) != 0);
         resource.setFlag9((metaData[29] & 0x40) != 0);
         resource.setFlag10((metaData[28] & 0x20) != 0);
         resource.setFlag11((metaData[29] & 0x20) != 0);
         resource.setFlag12((metaData[30] & 0x01) != 0);
         resource.setFlag13((metaData[30] & 0x02) != 0);
         resource.setFlag14((metaData[30] & 0x04) != 0);
         resource.setFlag15((metaData[30] & 0x08) != 0);
         resource.setFlag16((metaData[30] & 0x10) != 0);
         resource.setFlag17((metaData[30] & 0x20) != 0);
         resource.setFlag18((metaData[30] & 0x40) != 0);
         resource.setFlag19((metaData[30] & 0x80) != 0);
         resource.setFlag20((metaData[31] & 0x01) != 0);
                                                                                                            
         notes = rscVarData.getString (id, RESOURCE_NOTES);
         if (notes != null)
         {
            if (file.getPreserveNoteFormatting() == false)
            {
               notes = rtf.strip(notes);
            }
            
            resource.addResourceNotes(notes);
         }

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
    * This method extracts and collates resource assignment data
    *
    * @throws Exception on unexpected file format
    */
   private static void processAssignmentData (MPPFile file,  DirectoryEntry projectDir)
      throws MPXException, IOException
   {
      DirectoryEntry assnDir = (DirectoryEntry)projectDir.getEntry ("TBkndAssn");
      FixedData assnFixedData = new FixedData (142, new DocumentInputStream (((DocumentEntry)assnDir.getEntry("FixedData"))));
      
      int count = assnFixedData.getItemCount();
      byte[] data;
      Task task;
      Resource resource;
      for (int loop=0; loop < count; loop++)
      {
         data = assnFixedData.getByteArrayValue(loop);
         task = file.getTaskByUniqueID (MPPUtility.getInt (data, 4));
         resource = file.getResourceByUniqueID (MPPUtility.getInt (data, 8));
         if (task != null && resource != null)
         {
            task.addResourceAssignment (resource);
         }
      }
   }




	/**
	 * This method is used to determine if a duration is estimated.
	 *  
	 * @param type Duration units value
	 * @return boolean Estimated flag
	 */
   private static boolean getDurationEstimated (int type)
   { 
		return ((type & DURATION_CONFIRMED_MASK) != 0);      	
   }
   

   /**
    * This method converts between the numeric priority value
    * used in versions of MSP after MSP98 and the 10 priority
    * levels defined by the MPX standard.
    *
    * @param priority value read from MPP file
    */
   private static Priority getPriority (int priority)
   {
      int result;

      if (priority >= 1000)
      {
         result = Priority.DO_NOT_LEVEL;
      }
      else
      {
         result = priority / 100;
      }

      return (Priority.getInstance (result));
   }
      
   /**
    * Calendar data types.
    */
   private static final Integer CALENDAR_NAME = new Integer (1);
   private static final Integer CALENDAR_DATA = new Integer (3);

   /**
    * Task data types.
    */
   private static final Integer TASK_WBS = new Integer (10);
   private static final Integer TASK_NAME = new Integer (11);
   private static final Integer TASK_CONTACT = new Integer (12);

   private static final Integer TASK_TEXT1 = new Integer (14);
   private static final Integer TASK_TEXT2 = new Integer (15);
   private static final Integer TASK_TEXT3 = new Integer (16);
   private static final Integer TASK_TEXT4 = new Integer (17);
   private static final Integer TASK_TEXT5 = new Integer (18);
   private static final Integer TASK_TEXT6 = new Integer (19);
   private static final Integer TASK_TEXT7 = new Integer (20);
   private static final Integer TASK_TEXT8 = new Integer (21);
   private static final Integer TASK_TEXT9 = new Integer (22);
   private static final Integer TASK_TEXT10 = new Integer (23);

   private static final Integer TASK_START1 = new Integer (24);
   private static final Integer TASK_FINISH1 = new Integer (25);
   private static final Integer TASK_START2 = new Integer (26);
   private static final Integer TASK_FINISH2 = new Integer (27);
   private static final Integer TASK_START3 = new Integer (28);
   private static final Integer TASK_FINISH3 = new Integer (29);
   private static final Integer TASK_START4 = new Integer (30);
   private static final Integer TASK_FINISH4 = new Integer (31);
   private static final Integer TASK_START5 = new Integer (32);
   private static final Integer TASK_FINISH5 = new Integer (33);
   private static final Integer TASK_START6 = new Integer (34);
   private static final Integer TASK_FINISH6 = new Integer (35);
   private static final Integer TASK_START7 = new Integer (36);
   private static final Integer TASK_FINISH7 = new Integer (37);
   private static final Integer TASK_START8 = new Integer (38);
   private static final Integer TASK_FINISH8 = new Integer (39);
   private static final Integer TASK_START9 = new Integer (40);
   private static final Integer TASK_FINISH9 = new Integer (41);
   private static final Integer TASK_START10 = new Integer (42);
   private static final Integer TASK_FINISH10 = new Integer (43);

   private static final Integer TASK_NUMBER1 = new Integer (45);
   private static final Integer TASK_NUMBER2 = new Integer (46);
   private static final Integer TASK_NUMBER3 = new Integer (47);
   private static final Integer TASK_NUMBER4 = new Integer (48);
   private static final Integer TASK_NUMBER5 = new Integer (49);
   private static final Integer TASK_NUMBER6 = new Integer (50);
   private static final Integer TASK_NUMBER7 = new Integer (51);
   private static final Integer TASK_NUMBER8 = new Integer (52);
   private static final Integer TASK_NUMBER9 = new Integer (53);
   private static final Integer TASK_NUMBER10 = new Integer (54);

   private static final Integer TASK_DURATION1 = new Integer (55);
   private static final Integer TASK_DURATION1_UNITS = new Integer (56);
   private static final Integer TASK_DURATION2 = new Integer (57);
   private static final Integer TASK_DURATION2_UNITS = new Integer (58);
   private static final Integer TASK_DURATION3 = new Integer (59);
   private static final Integer TASK_DURATION3_UNITS = new Integer (60);
   private static final Integer TASK_DURATION4 = new Integer (61);
   private static final Integer TASK_DURATION4_UNITS = new Integer (62);
   private static final Integer TASK_DURATION5 = new Integer (63);
   private static final Integer TASK_DURATION5_UNITS = new Integer (64);
   private static final Integer TASK_DURATION6 = new Integer (65);
   private static final Integer TASK_DURATION6_UNITS = new Integer (66);
   private static final Integer TASK_DURATION7 = new Integer (67);
   private static final Integer TASK_DURATION7_UNITS = new Integer (68);
   private static final Integer TASK_DURATION8 = new Integer (69);
   private static final Integer TASK_DURATION8_UNITS = new Integer (70);
   private static final Integer TASK_DURATION9 = new Integer (71);
   private static final Integer TASK_DURATION9_UNITS = new Integer (72);
   private static final Integer TASK_DURATION10 = new Integer (73);
   private static final Integer TASK_DURATION10_UNITS = new Integer (74);

   private static final Integer TASK_DATE1 = new Integer (80);
   private static final Integer TASK_DATE2 = new Integer (81);
   private static final Integer TASK_DATE3 = new Integer (82);
   private static final Integer TASK_DATE4 = new Integer (83);
   private static final Integer TASK_DATE5 = new Integer (84);
   private static final Integer TASK_DATE6 = new Integer (85);
   private static final Integer TASK_DATE7 = new Integer (86);
   private static final Integer TASK_DATE8 = new Integer (87);
   private static final Integer TASK_DATE9 = new Integer (88);
   private static final Integer TASK_DATE10 = new Integer (89);
                              
   private static final Integer TASK_TEXT11 = new Integer (90);
   private static final Integer TASK_TEXT12 = new Integer (91);   
   private static final Integer TASK_TEXT13 = new Integer (92);
   private static final Integer TASK_TEXT14 = new Integer (93);   
   private static final Integer TASK_TEXT15 = new Integer (94);
   private static final Integer TASK_TEXT16 = new Integer (95);
   private static final Integer TASK_TEXT17 = new Integer (96);         
   private static final Integer TASK_TEXT18 = new Integer (97);   
   private static final Integer TASK_TEXT19 = new Integer (98);   
   private static final Integer TASK_TEXT20 = new Integer (99);      
   private static final Integer TASK_TEXT21 = new Integer (100);
   private static final Integer TASK_TEXT22 = new Integer (101);   
   private static final Integer TASK_TEXT23 = new Integer (102);   
   private static final Integer TASK_TEXT24 = new Integer (103);   
   private static final Integer TASK_TEXT25 = new Integer (104);   
   private static final Integer TASK_TEXT26 = new Integer (105);   
   private static final Integer TASK_TEXT27 = new Integer (106);   
   private static final Integer TASK_TEXT28 = new Integer (107);   
   private static final Integer TASK_TEXT29 = new Integer (108);   
   private static final Integer TASK_TEXT30 = new Integer (109);
      
   private static final Integer TASK_NUMBER11 = new Integer (110);
   private static final Integer TASK_NUMBER12 = new Integer (111);
   private static final Integer TASK_NUMBER13 = new Integer (112);
   private static final Integer TASK_NUMBER14 = new Integer (113);
   private static final Integer TASK_NUMBER15 = new Integer (114);
   private static final Integer TASK_NUMBER16 = new Integer (115);
   private static final Integer TASK_NUMBER17 = new Integer (116);
   private static final Integer TASK_NUMBER18 = new Integer (117);
   private static final Integer TASK_NUMBER19 = new Integer (118);
   private static final Integer TASK_NUMBER20 = new Integer (119);

   private static final Integer TASK_OUTLINECODE1 = new Integer (123);
   private static final Integer TASK_OUTLINECODE2 = new Integer (124);
   private static final Integer TASK_OUTLINECODE3 = new Integer (125);
   private static final Integer TASK_OUTLINECODE4 = new Integer (126);
   private static final Integer TASK_OUTLINECODE5 = new Integer (127);
   private static final Integer TASK_OUTLINECODE6 = new Integer (128);
   private static final Integer TASK_OUTLINECODE7 = new Integer (129);
   private static final Integer TASK_OUTLINECODE8 = new Integer (130);
   private static final Integer TASK_OUTLINECODE9 = new Integer (131);
   private static final Integer TASK_OUTLINECODE10 = new Integer (132);
                                 
   private static final Integer TASK_COST1 = new Integer (134);
   private static final Integer TASK_COST2 = new Integer (135);
   private static final Integer TASK_COST3 = new Integer (136);
   private static final Integer TASK_COST4 = new Integer (137);
   private static final Integer TASK_COST5 = new Integer (138);
   private static final Integer TASK_COST6 = new Integer (139);
   private static final Integer TASK_COST7 = new Integer (140);
   private static final Integer TASK_COST8 = new Integer (141);
   private static final Integer TASK_COST9 = new Integer (142);
   private static final Integer TASK_COST10 = new Integer (143);

   private static final Integer TASK_NOTES = new Integer (144);

   /**
    * Resource data types.
    */
   private static final Integer RESOURCE_NAME = new Integer (1);
   private static final Integer RESOURCE_INITIALS = new Integer (3);
   private static final Integer RESOURCE_GROUP = new Integer (4);
   private static final Integer RESOURCE_CODE = new Integer (5);
   private static final Integer RESOURCE_EMAIL = new Integer (6);
   
   private static final Integer RESOURCE_TEXT1 = new Integer (10);
   private static final Integer RESOURCE_TEXT2 = new Integer (11);
   private static final Integer RESOURCE_TEXT3 = new Integer (12);
   private static final Integer RESOURCE_TEXT4 = new Integer (13);
   private static final Integer RESOURCE_TEXT5 = new Integer (14);
   private static final Integer RESOURCE_TEXT6 = new Integer (15);
   private static final Integer RESOURCE_TEXT7 = new Integer (16);
   private static final Integer RESOURCE_TEXT8 = new Integer (17);
   private static final Integer RESOURCE_TEXT9 = new Integer (18);            
   private static final Integer RESOURCE_TEXT10 = new Integer (19);
   private static final Integer RESOURCE_TEXT11 = new Integer (20);
   private static final Integer RESOURCE_TEXT12 = new Integer (21);
   private static final Integer RESOURCE_TEXT13 = new Integer (22);
   private static final Integer RESOURCE_TEXT14 = new Integer (23);
   private static final Integer RESOURCE_TEXT15 = new Integer (24);
   private static final Integer RESOURCE_TEXT16 = new Integer (25);
   private static final Integer RESOURCE_TEXT17 = new Integer (26);
   private static final Integer RESOURCE_TEXT18 = new Integer (27);
   private static final Integer RESOURCE_TEXT19 = new Integer (28);
   private static final Integer RESOURCE_TEXT20 = new Integer (29);
   private static final Integer RESOURCE_TEXT21 = new Integer (30);
   private static final Integer RESOURCE_TEXT22 = new Integer (31);
   private static final Integer RESOURCE_TEXT23 = new Integer (32);
   private static final Integer RESOURCE_TEXT24 = new Integer (33);
   private static final Integer RESOURCE_TEXT25 = new Integer (34);
   private static final Integer RESOURCE_TEXT26 = new Integer (35);
   private static final Integer RESOURCE_TEXT27 = new Integer (36);
   private static final Integer RESOURCE_TEXT28 = new Integer (37);
   private static final Integer RESOURCE_TEXT29 = new Integer (38);                           
   private static final Integer RESOURCE_TEXT30 = new Integer (39);

   private static final Integer RESOURCE_START1 = new Integer (40);
   private static final Integer RESOURCE_START2 = new Integer (41);
   private static final Integer RESOURCE_START3 = new Integer (42);
   private static final Integer RESOURCE_START4 = new Integer (43);
   private static final Integer RESOURCE_START5 = new Integer (44);
   private static final Integer RESOURCE_START6 = new Integer (45);
   private static final Integer RESOURCE_START7 = new Integer (46);
   private static final Integer RESOURCE_START8 = new Integer (47);
   private static final Integer RESOURCE_START9 = new Integer (48);
   private static final Integer RESOURCE_START10 = new Integer (49);
      
   private static final Integer RESOURCE_FINISH1 = new Integer (50);
   private static final Integer RESOURCE_FINISH2 = new Integer (51);
   private static final Integer RESOURCE_FINISH3 = new Integer (52);
   private static final Integer RESOURCE_FINISH4 = new Integer (53);
   private static final Integer RESOURCE_FINISH5 = new Integer (54);
   private static final Integer RESOURCE_FINISH6 = new Integer (55);
   private static final Integer RESOURCE_FINISH7 = new Integer (56);
   private static final Integer RESOURCE_FINISH8 = new Integer (57);
   private static final Integer RESOURCE_FINISH9 = new Integer (58);
   private static final Integer RESOURCE_FINISH10 = new Integer (59);

   private static final Integer RESOURCE_NUMBER1 = new Integer (60);
   private static final Integer RESOURCE_NUMBER2 = new Integer (61);
   private static final Integer RESOURCE_NUMBER3 = new Integer (62);
   private static final Integer RESOURCE_NUMBER4 = new Integer (63);
   private static final Integer RESOURCE_NUMBER5 = new Integer (64);
   private static final Integer RESOURCE_NUMBER6 = new Integer (65);
   private static final Integer RESOURCE_NUMBER7 = new Integer (66);
   private static final Integer RESOURCE_NUMBER8 = new Integer (67);
   private static final Integer RESOURCE_NUMBER9 = new Integer (68);
   private static final Integer RESOURCE_NUMBER10 = new Integer (69);
   private static final Integer RESOURCE_NUMBER11 = new Integer (70);
   private static final Integer RESOURCE_NUMBER12 = new Integer (71);
   private static final Integer RESOURCE_NUMBER13 = new Integer (72);
   private static final Integer RESOURCE_NUMBER14 = new Integer (73);
   private static final Integer RESOURCE_NUMBER15 = new Integer (74);
   private static final Integer RESOURCE_NUMBER16 = new Integer (75);
   private static final Integer RESOURCE_NUMBER17 = new Integer (76);
   private static final Integer RESOURCE_NUMBER18 = new Integer (77);
   private static final Integer RESOURCE_NUMBER19 = new Integer (78);
   private static final Integer RESOURCE_NUMBER20 = new Integer (79);
                  
   private static final Integer RESOURCE_DURATION1 = new Integer (80);
   private static final Integer RESOURCE_DURATION2 = new Integer (81);
   private static final Integer RESOURCE_DURATION3 = new Integer (82);
   private static final Integer RESOURCE_DURATION4 = new Integer (83);
   private static final Integer RESOURCE_DURATION5 = new Integer (84);
   private static final Integer RESOURCE_DURATION6 = new Integer (85);
   private static final Integer RESOURCE_DURATION7 = new Integer (86);
   private static final Integer RESOURCE_DURATION8 = new Integer (87);
   private static final Integer RESOURCE_DURATION9 = new Integer (88);
   private static final Integer RESOURCE_DURATION10 = new Integer (89);
                        
   private static final Integer RESOURCE_DURATION1_UNITS = new Integer (90);      
   private static final Integer RESOURCE_DURATION2_UNITS = new Integer (91);      
   private static final Integer RESOURCE_DURATION3_UNITS = new Integer (92);      
   private static final Integer RESOURCE_DURATION4_UNITS = new Integer (93);      
   private static final Integer RESOURCE_DURATION5_UNITS = new Integer (94);      
   private static final Integer RESOURCE_DURATION6_UNITS = new Integer (95);      
   private static final Integer RESOURCE_DURATION7_UNITS = new Integer (96);      
   private static final Integer RESOURCE_DURATION8_UNITS = new Integer (97);      
   private static final Integer RESOURCE_DURATION9_UNITS = new Integer (98);      
   private static final Integer RESOURCE_DURATION10_UNITS = new Integer (99);      
         
   private static final Integer RESOURCE_DATE1 = new Integer (103);   
   private static final Integer RESOURCE_DATE2 = new Integer (104);   
   private static final Integer RESOURCE_DATE3 = new Integer (105);   
   private static final Integer RESOURCE_DATE4 = new Integer (106);   
   private static final Integer RESOURCE_DATE5 = new Integer (107);   
   private static final Integer RESOURCE_DATE6 = new Integer (108);   
   private static final Integer RESOURCE_DATE7 = new Integer (109);   
   private static final Integer RESOURCE_DATE8 = new Integer (110);   
   private static final Integer RESOURCE_DATE9 = new Integer (111);   
   private static final Integer RESOURCE_DATE10 = new Integer (112);   

   private static final Integer RESOURCE_OUTLINECODE1 = new Integer (113);   
   private static final Integer RESOURCE_OUTLINECODE2 = new Integer (114);   
   private static final Integer RESOURCE_OUTLINECODE3 = new Integer (115);   
   private static final Integer RESOURCE_OUTLINECODE4 = new Integer (116);   
   private static final Integer RESOURCE_OUTLINECODE5 = new Integer (117);   
   private static final Integer RESOURCE_OUTLINECODE6 = new Integer (118);   
   private static final Integer RESOURCE_OUTLINECODE7 = new Integer (119);   
   private static final Integer RESOURCE_OUTLINECODE8 = new Integer (120);   
   private static final Integer RESOURCE_OUTLINECODE9 = new Integer (121);   
   private static final Integer RESOURCE_OUTLINECODE10 = new Integer (122);   
   
   private static final Integer RESOURCE_NOTES = new Integer (124);

   private static final Integer RESOURCE_COST1 = new Integer (125);
   private static final Integer RESOURCE_COST2 = new Integer (126);
   private static final Integer RESOURCE_COST3 = new Integer (127);
   private static final Integer RESOURCE_COST4 = new Integer (128);
   private static final Integer RESOURCE_COST5 = new Integer (129);
   private static final Integer RESOURCE_COST6 = new Integer (130);
   private static final Integer RESOURCE_COST7 = new Integer (131);
   private static final Integer RESOURCE_COST8 = new Integer (132);
   private static final Integer RESOURCE_COST9 = new Integer (133);
   private static final Integer RESOURCE_COST10 = new Integer (134);
                              	
	/**
	 * Mask used to isolate confirmed flag from the duration units field.
	 */
   private static final int DURATION_CONFIRMED_MASK = 0x20;
   
   /**
    * Default working week
    */
   private static final boolean[] DEFAULT_WORKING_WEEK =
   {
      false,
      true,
      true,
      true,
      true,
      true,
      false
   };

   private static final int MINIMUM_EXPECTED_TASK_SIZE = 240;

}
