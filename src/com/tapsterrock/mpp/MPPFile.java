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

import com.tapsterrock.mpx.BaseCalendar;
import com.tapsterrock.mpx.BaseCalendarHours;
import com.tapsterrock.mpx.BaseCalendarException;
import com.tapsterrock.mpx.ConstraintType;
import com.tapsterrock.mpx.MPXDuration;
import com.tapsterrock.mpx.MPXFile;
import com.tapsterrock.mpx.MPXPercentage;
import com.tapsterrock.mpx.Relation;
import com.tapsterrock.mpx.Resource;
import com.tapsterrock.mpx.ResourceAssignment;
import com.tapsterrock.mpx.Task;
import com.tapsterrock.mpx.TimeUnit;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeMap;
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
public class MPPFile
{
   /**
    * Constructor allowing an MPP file to be read from an input stream
    *
    * @param is an input stream
    * @throws Exception on file read errors
    */
   public MPPFile (InputStream is)
      throws Exception
   {
      process (is);
   }

   /**
    * Constructor allowing an MPP file to be read from a file object
    *
    * @param file a file object
    * @throws Exception on file read errors
    */
   public MPPFile (File file)
      throws Exception
   {
      this (new FileInputStream (file));
   }

   /**
    * Constructor allowing an MPP file to be read from a named file
    *
    * @param name name of a file
    * @throws Exception on file read errors
    */
   public MPPFile (String name)
      throws Exception
   {
      this (new FileInputStream (name));
   }

   /**
    * This method retrieves an MPXFile object representing the
    * data extracted from the MPP file.
    *
    * @return an MPXFile instance
    */
   public MPXFile getMpxFile ()
   {
      return (m_mpx);
   }

   /**
    * This method brings together all of the processing required to
    * read data from an MPP file, and populate an MPXFile object.
    *
    * @param is input stream
    * @throws an exception on file read errors
    */
   private void process (InputStream is)
      throws Exception
   {
      //
      // Open the file system and retrieve the root directory
      //
      POIFSFileSystem fs = new POIFSFileSystem (is);
      DirectoryEntry root = fs.getRoot ();
      DirectoryEntry projectDir = (DirectoryEntry)root.getEntry ("   19");

      //
      // Retrieve calendar data
      //
      DirectoryEntry calDir = (DirectoryEntry)projectDir.getEntry ("TBkndCal");
      m_calVarMeta = new VarMeta (new DocumentInputStream (((DocumentEntry)calDir.getEntry("VarMeta"))));
      m_calVarData = new Var2Data (m_calVarMeta, new DocumentInputStream (((DocumentEntry)calDir.getEntry("Var2Data"))));
      m_calFixedMeta = new FixedMeta (new DocumentInputStream (((DocumentEntry)calDir.getEntry("FixedMeta"))));
      m_calFixedData = new FixedData (m_calFixedMeta, new DocumentInputStream (((DocumentEntry)calDir.getEntry("FixedData"))));

      //
      // Retrieve task data
      //
      DirectoryEntry taskDir = (DirectoryEntry)projectDir.getEntry ("TBkndTask");
      m_taskVarMeta = new VarMeta (new DocumentInputStream (((DocumentEntry)taskDir.getEntry("VarMeta"))));
      m_taskVarData = new Var2Data (m_taskVarMeta, new DocumentInputStream (((DocumentEntry)taskDir.getEntry("Var2Data"))));
      m_taskFixedMeta = new FixedMeta (new DocumentInputStream (((DocumentEntry)taskDir.getEntry("FixedMeta"))));
      m_taskFixedData = new FixedData (m_taskFixedMeta, new DocumentInputStream (((DocumentEntry)taskDir.getEntry("FixedData"))));

      //
      // Retrieve constraint data
      //
      DirectoryEntry consDir = (DirectoryEntry)projectDir.getEntry ("TBkndCons");
      m_consFixedData = new FixedData (20, new DocumentInputStream (((DocumentEntry)consDir.getEntry("FixedData"))));

      //
      // Retrieve resource data
      //
      DirectoryEntry rscDir = (DirectoryEntry)projectDir.getEntry ("TBkndRsc");
      m_rscVarMeta = new VarMeta (new DocumentInputStream (((DocumentEntry)rscDir.getEntry("VarMeta"))));
      m_rscVarData = new Var2Data (m_rscVarMeta, new DocumentInputStream (((DocumentEntry)rscDir.getEntry("Var2Data"))));
      m_rscFixedMeta = new FixedMeta (new DocumentInputStream (((DocumentEntry)rscDir.getEntry("FixedMeta"))));
      m_rscFixedData = new FixedData (m_rscFixedMeta, new DocumentInputStream (((DocumentEntry)rscDir.getEntry("FixedData"))));

      //
      // Retrieve resource assignment data
      //
      DirectoryEntry assnDir = (DirectoryEntry)projectDir.getEntry ("TBkndAssn");
      m_assnVarMeta = new VarMeta (new DocumentInputStream (((DocumentEntry)assnDir.getEntry("VarMeta"))));
      m_assnVarData = new Var2Data (m_assnVarMeta, new DocumentInputStream (((DocumentEntry)assnDir.getEntry("Var2Data"))));
      m_assnFixedData = new FixedData (142, new DocumentInputStream (((DocumentEntry)assnDir.getEntry("FixedData"))));

      //
      // Extract the required data from the MPP file
      //
      m_mpx = new MPXFile ();
      createTaskMap ();
      createResourceMap ();
      processCalendarData ();
      processResourceData ();
      processTaskData ();
      processConstraintData ();
      processAssignmentData ();
   }

   /**
    * This method maps the task unique identifiers to their index number
    * within the FixedData block.
    */
   private void createTaskMap ()
   {
      int itemCount = m_taskFixedMeta.getItemCount();
      byte[] data;
      int uniqueID;

      for (int loop=0; loop < itemCount; loop++)
      {
         data = m_taskFixedData.getByteArrayValue(loop);
         uniqueID = MPPUtility.getInt (data, 0);
         m_taskMap.put(new Integer (uniqueID), new Integer (loop));
      }
   }

   /**
    * This method maps the resource unique identifiers to their index number
    * within the FixedData block.
    */
   private void createResourceMap ()
   {
      int itemCount = m_rscFixedMeta.getItemCount();
      byte[] data;
      int uniqueID;

      for (int loop=0; loop < itemCount; loop++)
      {
         data = m_rscFixedData.getByteArrayValue(loop);
         uniqueID = MPPUtility.getInt (data, 0);
         m_resourceMap.put(new Integer (uniqueID), new Integer (loop));
      }
   }

   /**
    * The format of the calandar data is a 4 byte header followed
    * by 7x 60 byte blocks, one for each day of the week. Optionally
    * following this is a set of 64 byte blocks representing exceptions
    * to the calendar.
    *
    * @throws Exception on unexpected file format
    */
   private void processCalendarData ()
      throws Exception
   {
      Integer[] uniqueid = m_calVarMeta.getUniqueIdentifiers();
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
      Date defaultStart1 = df.parse ("08:00");
      Date defaultEnd1 = df.parse ("12:00");
      Date defaultStart2 = df.parse ("13:00");
      Date defaultEnd2 = df.parse ("17:00");

      for (int loop=0; loop < uniqueid.length; loop++)
      {
         id = uniqueid[loop];
         name = m_calVarData.getUnicodeString (id, CALENDAR_NAME);
         data = m_calVarData.getByteArray (id, CALENDAR_DATA);
         if (data == null)
         {
            cal = m_mpx.addDefaultBaseCalendar();
            cal.setName (name);
         }
         else
         {
            //
            // Populate the basic calendar
            //
            cal = m_mpx.addBaseCalendar();
            cal.setName (name);

            for (index=0; index < 7; index++)
            {
               offset = 4 + (60 * index);
               defaultFlag = MPPUtility.getShort (data, offset);

               if (defaultFlag == 1)
               {
                  cal.setDay(index+1, DEFAULT_WEEK[index]);
                  if (cal.isWorkingDay(index+1) == true)
                  {
                     hours = cal.addBaseCalendarHours();
                     hours.setDay(index+1);
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
                     cal.setDay(index+1, BaseCalendar.NONWORKING);
                  }
                  else
                  {
                     cal.setDay(index+1, BaseCalendar.WORKING);
                     hours = cal.addBaseCalendarHours();
                     hours.setDay(index+1);

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
                     exception.setWorking(BaseCalendar.NONWORKING);
                  }
                  else
                  {
                     exception.setWorking(BaseCalendar.WORKING);

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
   private void processTaskData ()
      throws Exception
   {
      Integer[] uniqueid = m_taskVarMeta.getUniqueIdentifiers();
      Integer id;
      Integer offset;
      byte[] data;
      Task task;
      String notes;

      for (int loop=0; loop < uniqueid.length; loop++)
      {
         id = uniqueid[loop];
         offset = (Integer)m_taskMap.get(id);
         if (offset == null)
         {
            throw new Exception ("File format error");
         }

         data = m_taskFixedData.getByteArrayValue(offset.intValue());

         task = m_mpx.addTask();
         task.setActualCost(new Double (MPPUtility.getDouble (data, 216) / 100));
         //task.setActualDuration();
         task.setActualFinish(MPPUtility.getTimestamp (data, 16));
         //task.setActualStart();
         //task.setActualWork();
         task.setBaselineCost(new Double (MPPUtility.getDouble (data, 232) / 100));
         task.setBaselineDuration(getDuration (MPPUtility.getInt (data, 74), getDurationUnits (MPPUtility.getShort (data, 78))));
         task.setBaselineFinish(MPPUtility.getTimestamp (data, 108));
         task.setBaselineStart(MPPUtility.getTimestamp (data, 104));
         //task.setBaselineWork();
         //task.setBCWP(); // Calculated value
         //task.setBCWS(); // Calculated value
         //task.setConfirmed();
         task.setConstraintDate (MPPUtility.getTimestamp (data, 112));
         task.setConstraintType (new ConstraintType (MPPUtility.getShort (data, 80)));
         task.setContact(m_taskVarData.getUnicodeString (id, TASK_CONTACT));
         task.setCost(new Double (MPPUtility.getDouble(data, 200) / 100));
         task.setCost1(new Double (m_taskVarData.getDouble (id, TASK_COST1) / 100));
         task.setCost2(new Double (m_taskVarData.getDouble (id, TASK_COST2) / 100));
         task.setCost3(new Double (m_taskVarData.getDouble (id, TASK_COST3) / 100));
         //task.setCostVariance(); // Calculated value
         task.setCreated(MPPUtility.getTimestamp (data, 130));
         //task.setCritical(); // Calculated value
         //task.setCV(); // Calculated value
         //task.setDelay();
         task.setDuration (getDuration (MPPUtility.getInt (data, 70), getDurationUnits(MPPUtility.getShort (data, 64))));
         task.setDuration1(getDuration (m_taskVarData.getInt(id, TASK_DURATION1), getDurationUnits(m_taskVarData.getShort(id, TASK_DURATION1_UNITS))));
         task.setDuration2(getDuration (m_taskVarData.getInt(id, TASK_DURATION2), getDurationUnits(m_taskVarData.getShort(id, TASK_DURATION2_UNITS))));
         task.setDuration3(getDuration (m_taskVarData.getInt(id, TASK_DURATION3), getDurationUnits(m_taskVarData.getShort(id, TASK_DURATION3_UNITS))));
         //task.setDurationVariance(); // Calculated value
         //task.setEarlyFinish(); // Calculated value
         //task.setEarlyStart(); // Calculated value
         task.setFinish (MPPUtility.getTimestamp (data, 8));
         task.setFinish1(m_taskVarData.getTimestamp (id, TASK_FINISH1));
         task.setFinish2(m_taskVarData.getTimestamp (id, TASK_FINISH2));
         task.setFinish3(m_taskVarData.getTimestamp (id, TASK_FINISH3));
         task.setFinish4(m_taskVarData.getTimestamp (id, TASK_FINISH4));
         task.setFinish5(m_taskVarData.getTimestamp (id, TASK_FINISH5));
         //task.setFinishVariance(); // Calculated value
         //task.setFixed(); // Unsure of mapping from MPX->MSP2K
         task.setFixedCost(new Double (MPPUtility.getDouble (data, 208) / 100));
         //task.setFlag1();
         //task.setFlag2();
         //task.setFlag3();
         //task.setFlag4();
         //task.setFlag5();
         //task.setFlag6();
         //task.setFlag7();
         //task.setFlag8();
         //task.setFlag9();
         //task.setFlag10();
         //task.setFreeSlack();  // Calculated value
         //task.setHideBar();
         task.setID (new Integer (MPPUtility.getInt (data, 4)));
         //task.setLateFinish();  // Calculated value
         //task.setLateStart();  // Calculated value
         //task.setLinkedFields();  // Calculated value
         //task.setMarked();
         //task.setMilestone();
         task.setName(m_taskVarData.getUnicodeString (id, TASK_NAME));
         task.setNumber1(new Double (m_taskVarData.getDouble(id, TASK_NUMBER1)));
         task.setNumber2(new Double (m_taskVarData.getDouble(id, TASK_NUMBER2)));
         task.setNumber3(new Double (m_taskVarData.getDouble(id, TASK_NUMBER3)));
         task.setNumber4(new Double (m_taskVarData.getDouble(id, TASK_NUMBER4)));
         task.setNumber5(new Double (m_taskVarData.getDouble(id, TASK_NUMBER5)));
         //task.setObjects(); // Calculated value
         task.setOutlineLevel (new Integer (MPPUtility.getShort (data, 40)));
         //task.setOutlineNumber(); // Calculated value
         task.setPercentageComplete(new MPXPercentage ((float)MPPUtility.getShort(data, 122)));
         task.setPercentageWorkComplete(new MPXPercentage ((float)MPPUtility.getShort(data, 124)));
         //task.setProject(); // Calculated value
         task.setRemainingCost(new Double (MPPUtility.getDouble (data, 224)/100));
         //task.setRemainingDuration(); // Calculated value form percent complete?
         //task.setRemainingWork(); // Calculated value from percent complete?
         //task.setResourceGroup(); // Calculated value from resource
         //task.setResourceInitials(); // Calculated value from resource
         //task.setResourceNames(); // Calculated value from resource
         task.setResume(MPPUtility.getTimestamp(data, 20));
         //task.setResumeNoEarlierThan(); // No mapping in MSP2K?
         //task.setRollup();
         task.setStart (MPPUtility.getTimestamp (data, 88));
         task.setStart1(m_taskVarData.getTimestamp (id, TASK_START1));
         task.setStart2(m_taskVarData.getTimestamp (id, TASK_START2));
         task.setStart3(m_taskVarData.getTimestamp (id, TASK_START3));
         task.setStart4(m_taskVarData.getTimestamp (id, TASK_START4));
         task.setStart5(m_taskVarData.getTimestamp (id, TASK_START5));
         //task.setStartVariance();
         task.setStop(MPPUtility.getTimestamp (data, 16));
         //task.setSubprojectFile();
         //task.setSummary(); // Calculated value
         //task.setSV(); // Calculated value
         task.setText1(m_taskVarData.getUnicodeString (id, TASK_TEXT1));
         task.setText2(m_taskVarData.getUnicodeString (id, TASK_TEXT2));
         task.setText3(m_taskVarData.getUnicodeString (id, TASK_TEXT3));
         task.setText4(m_taskVarData.getUnicodeString (id, TASK_TEXT4));
         task.setText5(m_taskVarData.getUnicodeString (id, TASK_TEXT5));
         task.setText6(m_taskVarData.getUnicodeString (id, TASK_TEXT6));
         task.setText7(m_taskVarData.getUnicodeString (id, TASK_TEXT7));
         task.setText8(m_taskVarData.getUnicodeString (id, TASK_TEXT8));
         task.setText9(m_taskVarData.getUnicodeString (id, TASK_TEXT9));
         task.setText10(m_taskVarData.getUnicodeString (id, TASK_TEXT10));
         //task.setTotalSlack(); // Calculated value
         task.setUniqueID(id);
         //task.setUpdateNeeded(); // Calculated value
         task.setWBS(m_taskVarData.getUnicodeString (id, TASK_WBS));
         //task.setWork();
         //task.setWorkVariance(); // Calculated value

         //notes = m_taskVarData.getString (id, TASK_NOTES);
         //if (notes != null)
         //{
         //   task.addTaskNotes(notes);
         //}

         // Calendar number
         // MPPUtility.getInt (data, 160); // 160-163 0xFFFFFFFF = default

         // Other start date candidates
         // MPPUtility.getTimestamp (data, 92);
         // MPPUtility.getTimestamp (data, 148);

         // Other duration related candidates
         // MPPUtility.getInt (data, 24); // 24-27
         // MPPUtility.getInt (data, 28); // 28-31
         // MPPUtility.getInt (data, 32); // 32-35

         // Other end date related attributes
         // MPPUtility.getTimestamp (data, 12); //12-15 actual start?
         // MPPUtility.getTimestamp (data, 130); // 130-133

         // task type
         // MPPUtility.getShort (data, 126); // 126-127

         // deadline
         // MPPUtility.getTimestamp (data, 152); // 152-155
         // MPPUtility.getTimestamp (data, 164); // 164-167

         // priority *** need to work out mapping between MPX and MSP2K
         // MPPUtility.getShort (data, 120); // 120-121

         // percent complete
         // MPPUtility.getShort (data, 122); // 122-123
         // MPPUtility.getShort (data, 124); // 124-125 ?

         // costs values (8 byte IEEE doubles) seem to be duplicates of cost
         // 248 duplicate of fixed cost?

         // duplicate of resume?
         // 116

         // duplicate of actual cost / overtime cost?
         // 240
      }
   }

   /**
    * This method extracts and collates constraint data
    */
   private void processConstraintData ()
   {
      int count = m_consFixedData.getItemCount();
      byte[] data;
      Task task1;
      Task task2;
      Relation rel;
      int durationUnits;

      for (int loop=0; loop < count; loop++)
      {
         data = m_consFixedData.getByteArrayValue(loop);
         int taskID1 = MPPUtility.getInt (data, 4);
         int taskID2 = MPPUtility.getInt (data, 8);
         task1 = m_mpx.getTaskByUniqueID (taskID1);
         task2 = m_mpx.getTaskByUniqueID (taskID2);
         if (task1 != null && task2 != null)
         {
            rel = task2.addPredecessor(task1);
            rel.setType (MPPUtility.getShort(data, 12));
            durationUnits = getDurationUnits(MPPUtility.getShort (data, 14));
            rel.setDuration(getDuration (MPPUtility.getInt (data, 16), durationUnits));
         }
      }
   }


   /**
    * This method extracts and collates resource data
    *
    * @throws Exception on unexpected file format
    * @todo we need to strip the RTF formatting from the resource notes text
    */
   private void processResourceData ()
      throws Exception
   {
      Integer[] uniqueid = m_rscVarMeta.getUniqueIdentifiers();
      Integer id;
      Integer offset;
      byte[] data;
      Resource resource;
      String notes;

      for (int loop=0; loop < uniqueid.length; loop++)
      {
         id = uniqueid[loop];
         offset = (Integer)m_resourceMap.get(id);
         if (offset == null)
         {
            throw new Exception ("File format error");
         }

         data = m_taskFixedData.getByteArrayValue(offset.intValue());

         resource = m_mpx.addResource();

         //resource.setAccrueAt();
         resource.setUniqueID(id);
         resource.setID (new Integer (MPPUtility.getInt (data, 4)));
         resource.setName (m_rscVarData.getUnicodeString (id, RESOURCE_NAME));
         resource.setInitials (m_rscVarData.getUnicodeString (id, RESOURCE_INITIALS));
         resource.setCode (m_rscVarData.getUnicodeString (id, RESOURCE_CODE));
         resource.setEmailAddress(m_rscVarData.getUnicodeString (id, RESOURCE_EMAIL));
         resource.setGroup(m_rscVarData.getUnicodeString (id, RESOURCE_GROUP));
         resource.setText1(m_rscVarData.getUnicodeString (id, RESOURCE_TEXT1));
         resource.setText2(m_rscVarData.getUnicodeString (id, RESOURCE_TEXT2));
         resource.setText3(m_rscVarData.getUnicodeString (id, RESOURCE_TEXT3));
         resource.setText4(m_rscVarData.getUnicodeString (id, RESOURCE_TEXT4));
         resource.setText5(m_rscVarData.getUnicodeString (id, RESOURCE_TEXT5));

         //notes = m_rscVarData.getString (id, RESOURCE_NOTES);
         //if (notes != null)
         //{
         //   resource.addResourceNotes(notes);
         //}
      }
   }


   /**
    * This method extracts and collates resource assignment data
    *
    * @throws Exception on unexpected file format
    */
   private void processAssignmentData ()
      throws Exception
   {
      int count = m_assnFixedData.getItemCount();
      byte[] data;
      Task task;
      Resource resource;
      ResourceAssignment assignment;

      for (int loop=0; loop < count; loop++)
      {
         data = m_assnFixedData.getByteArrayValue(loop);
         task = m_mpx.getTaskByUniqueID (MPPUtility.getInt (data, 4));
         resource = m_mpx.getResourceByUniqueID (MPPUtility.getInt (data, 8));
         if (task != null && resource != null)
         {
            assignment = task.addResourceAssignment (resource);
            assignment.setID(resource.getID());
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

      switch (type)
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
    * Default working week
    */
   private static final Byte[] DEFAULT_WEEK =
   {
      BaseCalendar.NONWORKING,
      BaseCalendar.WORKING,
      BaseCalendar.WORKING,
      BaseCalendar.WORKING,
      BaseCalendar.WORKING,
      BaseCalendar.WORKING,
      BaseCalendar.NONWORKING
   };

   private MPXFile m_mpx;

   private VarMeta m_calVarMeta;
   private Var2Data m_calVarData;
   private FixedMeta m_calFixedMeta;
   private FixedData m_calFixedData;

   private VarMeta m_taskVarMeta;
   private Var2Data m_taskVarData;
   private FixedMeta m_taskFixedMeta;
   private FixedData m_taskFixedData;

   private FixedData m_consFixedData;

   private VarMeta m_rscVarMeta;
   private Var2Data m_rscVarData;
   private FixedMeta m_rscFixedMeta;
   private FixedData m_rscFixedData;

   private VarMeta m_assnVarMeta;
   private Var2Data m_assnVarData;
   private FixedData m_assnFixedData;

   private TreeMap m_taskMap = new TreeMap ();
   private TreeMap m_resourceMap = new TreeMap ();
}
