/*
 * file:       MPPFile.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2002-2003
 * date:       03/01/2003
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
import com.tapsterrock.mpx.ResourceAssignment;
import com.tapsterrock.mpx.Task;
import com.tapsterrock.mpx.TimeUnit;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.TreeMap;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.TimeZone;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.DocumentInputStream;

/**
 * This class is used to represent a Microsoft Project MPP file. This
 * implementation allows the file to be read, and the data it contains
 * exported as a set of MPX objects. These objects can be interrogated
 * to retrieve any required data, or stored as an MPX file.
 */
public class MPPFile extends MPXFile
{
   /**
    * Constructor allowing an MPP file to be read from an input stream
    *
    * @param is an input stream
    * @throws MPXException on file read errors
    */
   public MPPFile (InputStream is)
      throws MPXException
   {
      process (is);
   }

   /**
    * Constructor allowing an MPP file to be read from a file object
    *
    * @param file a file object
    * @throws MPXException on file read errors
    */
   public MPPFile (File file)
      throws MPXException
   {
      try
      {
         process (new FileInputStream (file));
      }

      catch (IOException ex)
      {
         throw new MPXException (MPXException.READ_ERROR, ex);
      }
   }

   /**
    * Constructor allowing an MPP file to be read from a named file
    *
    * @param name name of a file
    * @throws MPXException on file read errors
    */
   public MPPFile (String name)
      throws MPXException
   {
      try
      {
         process (new FileInputStream (name));
      }

      catch (IOException ex)
      {
         throw new MPXException (MPXException.READ_ERROR, ex);
      }
   }

   /**
    * This method brings together all of the processing required to
    * read data from an MPP file, and populate an MPXFile object.
    *
    * @param is input stream
    * @throws MPXException on file read errors
    */
   private void process (InputStream is)
      throws MPXException
   {
		try
		{   	
	      //
	      // Open the file system and retrieve the root directory
	      //
	      POIFSFileSystem fs = new POIFSFileSystem (is);
	      DirectoryEntry root = fs.getRoot ();
	
	      //
	      // Retrieve the CompObj data and validate the file format
	      //
	      CompObj compObj = new CompObj (new DocumentInputStream ((DocumentEntry)root.getEntry("\1CompObj")));			         
	
			String format = compObj.getFileFormat();
			if (format.equals("MSProject.MPP9") == true)
			{
				processMpp9 (root);
			}
			else
			{
				// Under development
//				if (format.equals("MSProject.MPP8") == true)				
//				{
//					processMpp8 (root);	
//				}
//				else
				{
					throw new MPXException (MPXException.INVALID_FILE + ": " + format);					
				}
			}			
		}
		
		catch (IOException ex)
		{
			throw new MPXException (MPXException.READ_ERROR, ex);
		}		
   }

	/**
	 * This method is used to process an MPP9 file. This is the file format
	 * used by Project 2000 and Project 2002.
	 * 
	 * @param root Root of the POI file system.
	 * @throws MPXException Normally thrown on dat validation errors
	 */
	private void processMpp9 (DirectoryEntry root)
		throws MPXException, IOException
	{
		//
		// Retrieve the project directory
		//
		DirectoryEntry projectDir = (DirectoryEntry)root.getEntry ("   19");

		//
		// Retrieve calendar data
		//
		DirectoryEntry calDir = (DirectoryEntry)projectDir.getEntry ("TBkndCal");
		VarMeta calVarMeta = new VarMeta (new DocumentInputStream (((DocumentEntry)calDir.getEntry("VarMeta"))));
		Var2Data calVarData = new Var2Data (calVarMeta, new DocumentInputStream (((DocumentEntry)calDir.getEntry("Var2Data"))));

		//
		// Retrieve task data
		//
		DirectoryEntry taskDir = (DirectoryEntry)projectDir.getEntry ("TBkndTask");
		VarMeta taskVarMeta = new VarMeta (new DocumentInputStream (((DocumentEntry)taskDir.getEntry("VarMeta"))));
		Var2Data taskVarData = new Var2Data (taskVarMeta, new DocumentInputStream (((DocumentEntry)taskDir.getEntry("Var2Data"))));
		FixedMeta taskFixedMeta = new FixedMeta (new DocumentInputStream (((DocumentEntry)taskDir.getEntry("FixedMeta"))), 47);
		FixedData taskFixedData = new FixedData (taskFixedMeta, new DocumentInputStream (((DocumentEntry)taskDir.getEntry("FixedData"))));
      
		//
		// Retrieve constraint data
		//
		DirectoryEntry consDir = (DirectoryEntry)projectDir.getEntry ("TBkndCons");
      FixedMeta consFixedMeta = new FixedMeta (new DocumentInputStream (((DocumentEntry)consDir.getEntry("FixedMeta"))), 10);      
		FixedData consFixedData = new FixedData (20, new DocumentInputStream (((DocumentEntry)consDir.getEntry("FixedData"))));

		//
		// Retrieve resource data
		//
		DirectoryEntry rscDir = (DirectoryEntry)projectDir.getEntry ("TBkndRsc");
		VarMeta rscVarMeta = new VarMeta (new DocumentInputStream (((DocumentEntry)rscDir.getEntry("VarMeta"))));
		Var2Data rscVarData = new Var2Data (rscVarMeta, new DocumentInputStream (((DocumentEntry)rscDir.getEntry("Var2Data"))));
		FixedMeta rscFixedMeta = new FixedMeta (new DocumentInputStream (((DocumentEntry)rscDir.getEntry("FixedMeta"))), 37);
		FixedData rscFixedData = new FixedData (rscFixedMeta, new DocumentInputStream (((DocumentEntry)rscDir.getEntry("FixedData"))));

		//
		// Retrieve resource assignment data
		//
		DirectoryEntry assnDir = (DirectoryEntry)projectDir.getEntry ("TBkndAssn");
		FixedData assnFixedData = new FixedData (142, new DocumentInputStream (((DocumentEntry)assnDir.getEntry("FixedData"))));

		//
		// Extract the required data from the MPP file
		//
		processCalendarData (calVarMeta, calVarData);
  		processResourceData (rscVarMeta, rscVarData, rscFixedMeta, rscFixedData);
	  	processTaskData (taskVarMeta, taskVarData, taskFixedMeta, taskFixedData);
	  	processConstraintData (consFixedMeta, consFixedData);
	  	processAssignmentData (assnFixedData);
	}
	
   /**
    * This method maps the task unique identifiers to their index number
    * within the FixedData block.
    *
    * @param taskFixedMeta Fixed meta data for this task
    * @param taskFixedData Fixed data for this task
    * @return Mapping between task identifiers and block position
    */
   private TreeMap createTaskMap (FixedMeta taskFixedMeta, FixedData taskFixedData)
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
   private TreeMap createTaskMetaMap (FixedMeta taskFixedMeta)
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
   private TreeMap createResourceMap (FixedMeta rscFixedMeta, FixedData rscFixedData)
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
   private void processCalendarData (VarMeta calVarMeta, Var2Data calVarData)
      throws MPXException
   {
      Integer[] uniqueid = calVarMeta.getUniqueIdentifiers();
      Integer id;
      BaseCalendar cal;
      BaseCalendarHours hours;
      BaseCalendarException exception;
      String name;
      byte[] data;
      byte[] day;
      int periodCount;
      int index;
      int offset;
      int defaultFlag;
      Date start;
      Date duration;
      int exceptionCount;

      //
      // Configure default time ranges
      //
      SimpleDateFormat df = new SimpleDateFormat ("HH:mm");
		df.setTimeZone(TimeZone.getTimeZone("GMT"));      
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
            cal = addDefaultBaseCalendar();
            cal.setName (name);
         }
         else
         {
            //
            // Populate the basic calendar
            //
            cal = addBaseCalendar();
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
                     duration = MPPUtility.getTime (data, offset + 20);
                     hours.setFromTime1(start);
                     hours.setToTime1(new Date (start.getTime()+duration.getTime()));
						
                     if (periodCount > 1)
                     {
                        start = MPPUtility.getTime (data, offset + 10);
                        duration = MPPUtility.getTime (data, offset + 24);
                        hours.setFromTime2(start);
                        hours.setToTime2(new Date (start.getTime()+duration.getTime()));

                        if (periodCount > 2)
                        {                        	
                           start = MPPUtility.getTime (data, offset + 12);
                           duration = MPPUtility.getTime (data, offset + 28);
                           hours.setFromTime3(start);
                           hours.setToTime3(new Date (start.getTime()+duration.getTime()));
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
                     duration = MPPUtility.getTime (data, offset+24);
                     exception.setFromTime1(start);
                     exception.setToTime1(new Date (start.getTime() + duration.getTime()));

                     if (periodCount > 1)
                     {
                        start = MPPUtility.getTime (data, offset+14);
                        duration = MPPUtility.getTime (data, offset+28);
                        exception.setFromTime2(start);
                        exception.setToTime2(new Date (start.getTime() + duration.getTime()));

                        if (periodCount > 2)
                        {
                           start = MPPUtility.getTime (data, offset+16);
                           duration = MPPUtility.getTime (data, offset+32);
                           exception.setFromTime3(start);
                           exception.setToTime3(new Date (start.getTime() + duration.getTime()));
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
    * @todo we need to strip the RTF formatting from the task note text
    */
   private void processTaskData (VarMeta taskVarMeta, Var2Data taskVarData, FixedMeta taskFixedMeta, FixedData taskFixedData)
      throws MPXException
   {
      TreeMap taskMap = createTaskMap (taskFixedMeta, taskFixedData);
      TreeMap taskMetaMap = createTaskMetaMap (taskFixedMeta);
      Integer[] uniqueid = taskVarMeta.getUniqueIdentifiers();
      Integer id;
      Integer offset;
      byte[] data;
      byte[] metaData;
      Task task;
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
			                     
         task = addTask();
         task.setActualCost(new Double (MPPUtility.getDouble (data, 216) / 100));
         task.setActualDuration(getDuration (MPPUtility.getInt (data, 66), getDurationUnits(MPPUtility.getShort (data, 64))));
         task.setActualFinish(MPPUtility.getTimestamp (data, 100));
         task.setActualStart(MPPUtility.getTimestamp (data, 96));
         task.setActualWork(new MPXDuration (MPPUtility.getDouble (data, 184)/60000, TimeUnit.HOURS));
         task.setBaselineCost(new Double (MPPUtility.getDouble (data, 232) / 100));
         task.setBaselineDuration(getDuration (MPPUtility.getInt (data, 74), getDurationUnits (MPPUtility.getShort (data, 78))));
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
         task.setCreated(MPPUtility.getTimestamp (data, 130));
         //task.setCritical(); // Calculated value
         //task.setCV(); // Calculated value
         task.setDeadline (MPPUtility.getTimestamp (data, 152));
         //task.setDelay(); // Field does not appear in Project 2000
         task.setDuration (getDuration (MPPUtility.getInt (data, 70), getDurationUnits(MPPUtility.getShort (data, 64))));
         task.setDuration1(getDuration (taskVarData.getInt(id, TASK_DURATION1), getDurationUnits(taskVarData.getShort(id, TASK_DURATION1_UNITS))));
         task.setDuration2(getDuration (taskVarData.getInt(id, TASK_DURATION2), getDurationUnits(taskVarData.getShort(id, TASK_DURATION2_UNITS))));
         task.setDuration3(getDuration (taskVarData.getInt(id, TASK_DURATION3), getDurationUnits(taskVarData.getShort(id, TASK_DURATION3_UNITS))));
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
         //task.setObjects(); // Calculated value
         task.setOutlineLevel (MPPUtility.getShort (data, 40));
         //task.setOutlineNumber(); // Calculated value
         task.setPercentageComplete((double)MPPUtility.getShort(data, 122));
         task.setPercentageWorkComplete((double)MPPUtility.getShort(data, 124));
         task.setPriority(getPriority (MPPUtility.getShort (data, 120)));
         //task.setProject(); // Calculated value
         task.setRemainingCost(new Double (MPPUtility.getDouble (data, 224)/100));
         //task.setRemainingDuration(); // Calculated value form percent complete?
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
			// This has been disbaled until we can do something with the
			// RTF formatting.
			//
			
         //notes = taskVarData.getString (id, TASK_NOTES);
         //if (notes != null)
         //{
         //   task.addTaskNotes(notes);
         //}
         
			//
			// Calculate the cost variance
			//
			if (task.getCost() != null && task.getBaselineCost() != null)
			{
				task.setCostVariance(new Double(task.getCost().doubleValue() - task.getBaselineCost().doubleValue()));	
			}       																																	
      }

		
		//
		// Update the internal structure. We'll take this opportunity to 
		// generate outline numbers for the tasks as they don't appear to
		// be present in the MPP file.
		//
		setAutoOutlineNumber(true);
      updateStructure ();
      setAutoOutlineNumber(false);      
      
      //
      // Perform post-processing to set the summary flag
      //
      LinkedList tasks = getAllTasks();
      Iterator iter = tasks.iterator();     
      
      while (iter.hasNext() == true)
      {
			task = (Task)iter.next();
			task.setSummary(task.getChildTasks().size() != 0);
      }
   }

   /**
    * This method extracts and collates constraint data
    */
   private void processConstraintData (FixedMeta consFixedMeta, FixedData consFixedData)
   {      
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
                  task1 = getTaskByUniqueID (taskID1);
                  task2 = getTaskByUniqueID (taskID2);
                  if (task1 != null && task2 != null)
                  {
                     rel = task2.addPredecessor(task1);
                     rel.setType (MPPUtility.getShort(data, 12));
                     durationUnits = getDurationUnits(MPPUtility.getShort (data, 14));
                     rel.setDuration(getDuration (MPPUtility.getInt (data, 16), durationUnits));
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
    * @todo we need to strip the RTF formatting from the resource notes text
    */
   private void processResourceData (VarMeta rscVarMeta, Var2Data rscVarData, FixedMeta rscFixedMeta, FixedData rscFixedData)
      throws MPXException
   {
      TreeMap resourceMap = createResourceMap (rscFixedMeta, rscFixedData);
      Integer[] uniqueid = rscVarMeta.getUniqueIdentifiers();
      Integer id;
      Integer offset;
      byte[] data;
      Resource resource;
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

         resource = addResource();

         resource.setAccrueAt(AccrueType.getInstance (MPPUtility.getShort (data, 12)));
         resource.setActualCost(new Double(MPPUtility.getDouble(data, 132)/100));
         resource.setActualOvertimeCost(new Double(MPPUtility.getDouble(data, 172)/100));         
         resource.setActualWork(new MPXDuration (MPPUtility.getDouble (data, 60)/60000, TimeUnit.HOURS));
         //resource.setBaseCalendar();
         resource.setBaselineCost(new Double(MPPUtility.getDouble(data, 148)/100));
         resource.setBaselineWork(new MPXDuration (MPPUtility.getDouble (data, 68)/60000, TimeUnit.HOURS));
         resource.setCode (rscVarData.getUnicodeString (id, RESOURCE_CODE));
         resource.setCost(new Double(MPPUtility.getDouble(data, 140)/100));
         resource.setCostPerUse(new Double(MPPUtility.getDouble(data, 84)/100));
         resource.setEmailAddress(rscVarData.getUnicodeString (id, RESOURCE_EMAIL));         
         resource.setGroup(rscVarData.getUnicodeString (id, RESOURCE_GROUP));
         resource.setID (MPPUtility.getInt (data, 4));
         resource.setInitials (rscVarData.getUnicodeString (id, RESOURCE_INITIALS));
         //resource.setLinkedFields(); // Calculated value
         resource.setMaxUnits(MPPUtility.getDouble(data, 44)/100);
         resource.setName (rscVarData.getUnicodeString (id, RESOURCE_NAME));
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
         resource.setText1(rscVarData.getUnicodeString (id, RESOURCE_TEXT1));
         resource.setText2(rscVarData.getUnicodeString (id, RESOURCE_TEXT2));
         resource.setText3(rscVarData.getUnicodeString (id, RESOURCE_TEXT3));
         resource.setText4(rscVarData.getUnicodeString (id, RESOURCE_TEXT4));
         resource.setText5(rscVarData.getUnicodeString (id, RESOURCE_TEXT5));
         resource.setUniqueID(id.intValue());
         resource.setWork(new MPXDuration (MPPUtility.getDouble (data, 52)/60000, TimeUnit.HOURS));

         //notes = rscVarData.getString (id, RESOURCE_NOTES);
         //if (notes != null)
         //{
         //   resource.addResourceNotes(notes);
         //}

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
   private void processAssignmentData (FixedData assnFixedData)
      throws MPXException
   {
      int count = assnFixedData.getItemCount();
      byte[] data;
      Task task;
      Resource resource;
      ResourceAssignment assignment;

      for (int loop=0; loop < count; loop++)
      {
         data = assnFixedData.getByteArrayValue(loop);
         task = getTaskByUniqueID (MPPUtility.getInt (data, 4));
         resource = getResourceByUniqueID (MPPUtility.getInt (data, 8));
         if (task != null && resource != null)
         {
            task.addResourceAssignment (resource);
         }
      }
   }



   /**
    * Reads a duration value. This method relies on the fact that
    * the units of the duration have been specified elsewhere.
    *
    * @param data byte array of data
    * @param offset location of data as offset into the array
    * @param type type of units of the duration
    * @param integer value
    */
   private MPXDuration getDuration (int value, int type)
   {
      double duration;

      switch (type)
      {
         case TimeUnit.MINUTES:
         case TimeUnit.ELAPSED_MINUTES:
         {
            duration = (double)value / 10;
            break;
         }

         case TimeUnit.HOURS:
         case TimeUnit.ELAPSED_HOURS:
         {
            duration = (double)value / 600;
            break;
         }

         case TimeUnit.DAYS:
         case TimeUnit.ELAPSED_DAYS:
         {
            duration = (double)value / 4800;
            break;
         }

         case TimeUnit.WEEKS:
         case TimeUnit.ELAPSED_WEEKS:
         {
            duration = (double)value / 24000;
            break;
         }

         case TimeUnit.MONTHS:
         case TimeUnit.ELAPSED_MONTHS:
         {
            duration = (double)value / 96000;
            break;
         }

         default:
         {
            duration = (double)value;
            break;
         }
      }

      return (new MPXDuration (duration, type));
   }

	/**
	 * This method is used to determine if a duration is estimated.
	 *  
	 * @param type Duration units value
	 * @return boolean Estimated flag
	 */
   private boolean getDurationEstimated (int type)
   { 
		return ((type & DURATION_CONFIRMED_MASK) != 0);      	
   }
   
   /**
    * This method converts between the duration units representation
    * used in the MPP file, and the standard MPX duration units.
    * If the supplied units are unrecognised, the units default to days.
    *
    * @param type MPP units
    * @return MPX units
    */
   private int getDurationUnits (int type)
   {
      int units;

      switch (type & DURATION_UNITS_MASK)
      {
         case 3:
         {
            units = TimeUnit.MINUTES;
            break;
         }

         case 4:
         {
            units = TimeUnit.ELAPSED_MINUTES;
            break;
         }

         case 5:
         {
            units = TimeUnit.HOURS;
            break;
         }

         case 6:
         {
            units = TimeUnit.ELAPSED_HOURS;
            break;
         }

         case 8:
         {
            units = TimeUnit.ELAPSED_DAYS;
            break;
         }

         case 9:
         {
            units = TimeUnit.WEEKS;
            break;
         }

         case 10:
         {
            units = TimeUnit.ELAPSED_WEEKS;
            break;
         }

         case 11:
         {
            units = TimeUnit.MONTHS;
            break;
         }

         case 12:
         {
            units = TimeUnit.ELAPSED_MONTHS;
            break;
         }

         default:
         case 7:
         {
            units = TimeUnit.DAYS;
            break;
         }
      }

      return (units);
   }


   /**
    * This method converts between the duration units representation
    * used in the MPP file for rate values, and the standard MPX duration units.
    * If the supplied units are unrecognised, the units default to hours.
    *
    * @param type MPP units
    * @return MPX units
    */
   private int getRateDurationUnits (int type)
   {
      int units;

      switch (type)
      {
         case 1:
         {
            units = TimeUnit.MINUTES;
            break;
         }

         case 3:
         {
            units = TimeUnit.DAYS;
            break;
         }

         case 4:
         {
            units = TimeUnit.WEEKS;
            break;
         }

         case 5:
         {
            units = TimeUnit.MONTHS;
            break;
         }

         //
         // There is a missing option. Can't see what it can be set to
         // when using MSP2K
         //

         case 7:
         {
            units = TimeUnit.YEARS;
            break;
         }

         default:
         case 2:
         {
            units = TimeUnit.HOURS;
            break;
         }

      }

      return (units);
   }

   /**
    * This method converts between the numeric priority value
    * used in versions of MSP after MSP98 and the 10 priority
    * levels defined by the MPX standard.
    *
    * @param priority value read from MPP file
    */
   private Priority getPriority (int priority)
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
	 * This method is used to process an MPP8 file. This is the file format
	 * used by Project 98.
	 * 
	 * @param root Root of the POI file system.
	 * @throws MPXException Normally thrown on dat validation errors
	 */
	private void processMpp8 (DirectoryEntry root)
		throws MPXException, IOException
	{
		//
		// Retrieve the project directory
		//
		DirectoryEntry projectDir = (DirectoryEntry)root.getEntry ("   1");

		//
		// Retrieve calendar data
		//
		DirectoryEntry calDir = (DirectoryEntry)projectDir.getEntry ("TBkndCal");
		FixFix calendarFixedData = new FixFix (36, new DocumentInputStream (((DocumentEntry)calDir.getEntry("FixFix   0"))));
		FixDeferFix calendarVarData = new FixDeferFix (new DocumentInputStream (((DocumentEntry)calDir.getEntry("FixDeferFix   0"))));		
		
		//
		// Retrieve task data
		//
		DirectoryEntry taskDir = (DirectoryEntry)projectDir.getEntry ("TBkndTask");
		FixFix taskFixedData = new FixFix (316, new DocumentInputStream (((DocumentEntry)taskDir.getEntry("FixFix   0"))));
		FixDeferFix taskVarData = new FixDeferFix (new DocumentInputStream (((DocumentEntry)taskDir.getEntry("FixDeferFix   0"))));		
		
		// Under development		
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
   private static final Integer RESOURCE_NOTES = new Integer (124);

	/**
	 * Mask used to remove flags from the duration units field.
	 */
	private static final int DURATION_UNITS_MASK = 0x1F;
	
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
