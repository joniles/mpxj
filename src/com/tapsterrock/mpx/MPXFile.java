/*
 * file:       MPXFile.java
 * author:     Scott Melville
 *             Jon Iles
 * copyright:  (c) Tapster Rock Limited 2002-2003
 * date:       15/08/2002
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

package com.tapsterrock.mpx;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;


/**
 * This class encapsulates all functionality relating to creating, read
 * and writing MPX files and their constituent records.
 *
 * TODO Ensure that the underlying lists are returned unmodifyable
 */
public class MPXFile
{
   /**
    * Default constructor.
    */
   public MPXFile ()
   {
      configure();
   }

   /**
    * Copy constructor. WARNING: this provides a shallow copy only.
    *
    * @param file File to be copied
    */
   public MPXFile (MPXFile file)
   {
      m_allResourceAssignments = file.m_allResourceAssignments;
      m_allResources = file.m_allResources;
      m_allTasks = file.m_allTasks;
      m_autoOutlineLevel = file.m_autoOutlineLevel;
      m_autoOutlineNumber = file.m_autoOutlineNumber;
      m_autoResourceID = file.m_autoResourceID;
      m_autoResourceUniqueID = file.m_autoResourceUniqueID;
      m_autoTaskID = file.m_autoTaskID;
      m_autoTaskUniqueID = file.m_autoTaskUniqueID;
      m_autoCalendarUniqueID = file.m_autoCalendarUniqueID;
      m_autoWBS = file.m_autoWBS;
      m_baseCalendars = file.m_baseCalendars;
      m_baseOutlineLevel = file.m_baseOutlineLevel;
      m_childTasks = file.m_childTasks;
      m_currencyFormat = file.m_currencyFormat;
      m_dateTimeFormat = file.m_dateTimeFormat;
      m_dateFormat = file.m_dateFormat;
      m_ddeOleClientLinks = file.m_ddeOleClientLinks;
      m_delimiter = file.m_delimiter;
      m_fileCreationRecord = file.m_fileCreationRecord;
      m_lastBaseCalendar = file.m_lastBaseCalendar;
      m_lastResource = file.m_lastResource;
      m_lastResourceAssignment = file.m_lastResourceAssignment;
      m_lastResourceCalendar = file.m_lastResourceCalendar;
      m_lastTask = file.m_lastTask;
      m_projectHeader = file.m_projectHeader;
      m_projectNames = file.m_projectNames;
      m_records = file.m_records;
      m_resourceID = file.m_resourceID;
      m_resourceModel = file.m_resourceModel;
      m_resourceTableDefinition = file.m_resourceTableDefinition;
      m_resourceUniqueID = file.m_resourceUniqueID;
      m_taskID = file.m_taskID;
      m_taskModel = file.m_taskModel;
      m_taskTableDefinition = file.m_taskTableDefinition;
      m_taskUniqueID = file.m_taskUniqueID;
      m_calendarUniqueID = file.m_calendarUniqueID;
      m_timeFormat = file.m_timeFormat;
      m_locale = file.m_locale;
      m_taskFieldAlias = file.m_taskFieldAlias;
      m_aliasTaskField = file.m_aliasTaskField;
      m_resourceFieldAlias = file.m_resourceFieldAlias;
      m_aliasResourceField = file.m_aliasResourceField;
      m_taskUniqueIDMap = file.m_taskUniqueIDMap;
      m_taskIDMap = file.m_taskIDMap;
      m_resourceUniqueIDMap = file.m_resourceUniqueIDMap;
      m_resourceIDMap = file.m_resourceIDMap;
   }

   /**
    * This constructor is the primary mechanism for reading an
    * existing MPX file.
    *
    * @param stream input stream
    * @throws MPXException if construction fails
    */
   public MPXFile (InputStream stream)
      throws MPXException
   {
      configure();
      read(stream);
   }

   /**
    * This constructor is provided as a convenience to allow a source
    * file path to be specified from which the contents of the MPX file
    * are read.
    *
    * @param path path of input file
    * @throws MPXException if construction fails
    */
   public MPXFile (String path)
      throws MPXException
   {
      configure();
      read(new File(path));
   }

   /**
    * This constructor is provided as a convenience to allow a source
    * file to be specified from which the contents of the MPX file
    * are read.
    *
    * @param file input file object
    * @throws MPXException if construction fails
    */
   public MPXFile (File file)
      throws MPXException
   {
      configure();
      read(file);
   }

   /**
    * This method configures the basic MPX file.
    * Note that we force the locale of the file to be English, ignoring
    * the system default locale value. We do this as the vast majority of
    * MPX file users will have international versions of MS Project,
    * not localised ones. Users of localised MPX file versions must call
    * the setLocale method explicitly.
    */
   protected void configure ()
   {
      setLocale(Locale.ENGLISH);
      m_records.add(m_fileCreationRecord);
      m_records.add(m_projectHeader);
   }

   /**
    * Accessor method to retrieve the current file delimiter character.
    *
    * @return delimiter character
    */
   public char getDelimiter ()
   {
      return (m_delimiter);
   }

   /**
    * Modifier method used to set the delimiter character.
    *
    * @param delimiter delimiter character
    */
   public void setDelimiter (char delimiter)
   {
      m_delimiter = delimiter;
   }

   /**
    * Underlying method used to read an MPX file from an input stream.
    *
    * @param is input stream
    * @throws MPXException if file read fails
    */
   public void read (InputStream is)
      throws MPXException
   {
      int line = 1;

      try
      {
         //
         // Test the header and extract the separator. If this is successful,
         // we reset the stream back as far as we can. The design of the
         // BufferedInputStream class means that we can't get back to character
         // zero, so the first record we will read will get "PX" rather than
         // "MPX" in the first field position.
         //
         BufferedInputStream bis = new BufferedInputStream(is);
         byte[] data = new byte[4];
         data[0] = (byte)bis.read();
         bis.mark(1024);
         data[1] = (byte)bis.read();
         data[2] = (byte)bis.read();
         data[3] = (byte)bis.read();

         if ((data[0] != 'M') || (data[1] != 'P') || (data[2] != 'X'))
         {
            throw new MPXException(MPXException.INVALID_FILE);
         }

         setDelimiter((char)data[3]);

         bis.reset();

         //
         // Read the file creation record. At this point we are reading
         // directly from an input stream so no character set decoding is
         // taking place. We assume that any text in this record will not
         // require decoding.
         //
         Tokenizer tk = new InputStreamTokenizer(bis);
         tk.setDelimiter(m_delimiter);

         Record record;
         String number;

         //
         // Add the header record
         //
         add(Integer.toString(FileCreationRecord.RECORD_NUMBER), new Record(this, tk));
         ++line;

         //
         // Now process the remainder of the file in full. As we have read the
         // file creation record we have access to the field which specifies the
         // codepage used to encode the character set in this file. We set up
         // an input stream reader using the appropriate character set, and
         // create a new tokenizer to read from this Reader instance.
         //
         InputStreamReader reader = new InputStreamReader(bis, getFileCreationRecord().getCodePage().getCharset());
         tk = new ReaderTokenizer(reader);
         tk.setDelimiter(m_delimiter);

         //
         // Read the remainder of the records
         //
         while (tk.getType() != Tokenizer.TT_EOF)
         {
            record = new Record(this, tk);
            number = record.getRecordNumber();

            if (number != null)
            {
               add(number, record);
            }
            
            ++line;
         }

         //
         // Ensure that all tasks and resources have valid Unique IDs
         //
         updateUniqueIdentifiers();
         
         //
         // Ensure that the structure is consistent
         //
         updateStructure();
      }

      catch (Exception ex)
      {
         throw new MPXException(MPXException.READ_ERROR + " (failed at line " + line + ")", ex);
      }
   }

   /**
    * This method post-processes tasks and resources read from an MPX
    * file to ensure that they all have valid unique ID fields. This is
    * designed to cope with poorly formed MPX files where tasks and resources
    * have ID values, but not unique ID values.
    */
   private void updateUniqueIdentifiers ()
   {
      Iterator iter = m_allTasks.iterator();
      Task task;
      while (iter.hasNext() == true)
      {
         task = (Task)iter.next();
         if (task.getUniqueID() == null)
         {
            task.setUniqueID(task.getID());
         }
      }
      
      iter = m_allResources.iterator();
      Resource resource;
      while (iter.hasNext() == true)
      {
         resource = (Resource)iter.next();
         if (resource.getUniqueID() == null)
         {
            resource.setUniqueID(resource.getID());
         }
      }
   }
   
   /**
    * This is a convenience method to read an MPX file
    * given a file name.
    *
    * @param file the file to be read.
    * @throws MPXException thrown if a file read error occurs
    */
   public void read (String file)
      throws MPXException
   {
      try
      {
         FileInputStream fis = new FileInputStream(file);
         read(fis);
         fis.close();
      }

      catch (IOException ex)
      {
         throw new MPXException(MPXException.READ_ERROR, ex);
      }
   }

   /**
    * This is a convenience method to read an MPX file
    * from a file object.
    *
    * @param file the file to be read.
    * @throws MPXException thrown if a file read error occurs
    */
   public void read (File file)
      throws MPXException
   {
      try
      {
         FileInputStream fis = new FileInputStream(file);
         read(fis);
         fis.close();
      }

      catch (IOException ex)
      {
         throw new MPXException(MPXException.READ_ERROR, ex);
      }
   }

   /**
    * This is a convenience method provided to allow an empty record
    * of a specified type to be added to the file.
    *
    * @param recordNumber type of record to be added
    * @return object representing the new record
    * @throws MPXException Thrown on parse errors
    */
   private MPXRecord add (int recordNumber)
      throws MPXException
   {
      return (add(String.valueOf(recordNumber), Record.EMPTY_RECORD));
   }

   /**
    * This method adds a new record of the specified type and populates
    * it with data read from an MPX file.
    *
    * @param recordNumber type of record to add
    * @param record data from MPX file
    * @return new object representing record from MPX file
    * @throws MPXException normally thrown on parsing errors
    */
   private MPXRecord add (String recordNumber, Record record)
      throws MPXException
   {
      MPXRecord current = null;

      switch (Integer.parseInt(recordNumber))
      {
         case Comments.RECORD_NUMBER:
         {
            current = new Comments(this, record);
            m_records.add(current);
            break;
         }

         case ProjectHeader.CURRENCY_SETTINGS_RECORD_NUMBER:
         {
            m_projectHeader.updateCurrencySettings(record);
            updateFormats();
            current = m_projectHeader;
            break;
         }

         case ProjectHeader.DEFAULT_SETTINGS_RECORD_NUMBER:
         {
            m_projectHeader.updateDefaultSettings(record);
            current = m_projectHeader;
            break;
         }

         case ProjectHeader.DATE_TIME_SETTINGS_RECORD_NUMBER:
         {
            m_projectHeader.updateDateTimeSettings(record);
            current = m_projectHeader;
            break;
         }

         case MPXCalendar.BASE_CALENDAR_RECORD_NUMBER:
         {
            m_lastBaseCalendar = new MPXCalendar(this, record, true);
            current = m_lastBaseCalendar;
            m_records.add(current);
            m_baseCalendars.add(current);
            break;
         }

         case MPXCalendarHours.BASE_CALENDAR_HOURS_RECORD_NUMBER:
         {
            if (m_lastBaseCalendar != null)
            {
               current = m_lastBaseCalendar.addCalendarHours(record);
            }

            break;
         }

         case MPXCalendarException.BASE_CALENDAR_EXCEPTION_RECORD_NUMBER:
         {
            if (m_lastBaseCalendar != null)
            {
               current = m_lastBaseCalendar.addCalendarException(record);
            }

            break;
         }

         case ProjectHeader.PROJECT_HEADER_RECORD_NUMBER:
         {
            m_projectHeader.updateProjectHeader(record);
            current = m_projectHeader;
            break;
         }

         case ResourceModel.RECORD_NUMBER_TEXT:
         {
            if ((m_resourceTableDefinition == false) && (m_ignoreTextModels == false))
            {
               current = m_resourceModel;
               m_resourceModel.update(record, true);
               m_resourceTableDefinition = true;
            }

            break;
         }

         case ResourceModel.RECORD_NUMBER_NUMERIC:
         {
            if (m_resourceTableDefinition == false)
            {
               current = m_resourceModel;
               m_resourceModel.update(record, false);
               m_resourceTableDefinition = true;
            }

            break;
         }

         case Resource.RECORD_NUMBER:
         {
            m_lastResource = new Resource(this, record);
            current = m_lastResource;
            m_records.add(current);
            m_allResources.add(current);
            break;
         }

         case ResourceNotes.RECORD_NUMBER:
         {
            if (m_lastResource != null)
            {
               current = m_lastResource.addResourceNotes(record);
            }

            break;
         }

         case MPXCalendar.RESOURCE_CALENDAR_RECORD_NUMBER:
         {
            if (m_lastResource != null)
            {
               m_lastResourceCalendar = m_lastResource.addResourceCalendar(record);
               current = m_lastResourceCalendar;
            }

            break;
         }

         case MPXCalendarHours.RESOURCE_CALENDAR_HOURS_RECORD_NUMBER:
         {
            if (m_lastResourceCalendar != null)
            {
               current = m_lastResourceCalendar.addCalendarHours(record);
            }

            break;
         }

         case MPXCalendarException.RESOURCE_CALENDAR_EXCEPTION_RECORD_NUMBER:
         {
            if (m_lastResourceCalendar != null)
            {
               current = m_lastResourceCalendar.addCalendarException(record);
            }

            break;
         }

         case TaskModel.RECORD_NUMBER_TEXT:
         {
            if ((m_taskTableDefinition == false) && (m_ignoreTextModels == false))
            {
               current = m_taskModel;
               m_taskModel.update(record, true);
               m_taskTableDefinition = true;
            }

            break;
         }

         case TaskModel.RECORD_NUMBER_NUMERIC:
         {
            if (m_taskTableDefinition == false)
            {
               current = m_taskModel;
               m_taskModel.update(record, false);
               m_taskTableDefinition = true;
            }

            break;
         }

         case Task.RECORD_NUMBER:
         {
            m_lastTask = new Task(this, record);
            current = m_lastTask;
            m_records.add(current);
            m_allTasks.add(current);

            int outlineLevel = m_lastTask.getOutlineLevelValue();

            if (m_baseOutlineLevel == -1)
            {
               m_baseOutlineLevel = outlineLevel;
            }

            if (outlineLevel == m_baseOutlineLevel)
            {
               m_childTasks.add(m_lastTask);
            }
            else
            {
               if (m_childTasks.isEmpty() == true)
               {
                  throw new MPXException(MPXException.INVALID_OUTLINE);
               }

               ((Task)m_childTasks.getLast()).addChildTask(m_lastTask, outlineLevel);
            }

            break;
         }

         case TaskNotes.RECORD_NUMBER:
         {
            if (m_lastTask != null)
            {
               current = m_lastTask.addTaskNotes(record);
            }

            break;
         }

         case RecurringTask.RECORD_NUMBER:
         {
            if (m_lastTask != null)
            {
               current = m_lastTask.addRecurringTask(record);
            }

            break;
         }

         case ResourceAssignment.RECORD_NUMBER:
         {
            if (m_lastTask != null)
            {
               m_lastResourceAssignment = m_lastTask.addResourceAssignment(record);
               current = m_lastResourceAssignment;
               m_allResourceAssignments.add(m_lastResourceAssignment);
            }

            break;
         }

         case ResourceAssignmentWorkgroupFields.RECORD_NUMBER:
         {
            if (m_lastResourceAssignment != null)
            {
               current = m_lastResourceAssignment.addWorkgroupAssignment(record);
            }

            break;
         }

         case ProjectNames.RECORD_NUMBER:
         {
            current = new ProjectNames(this, record);
            m_records.add(current);
            ++m_projectNames;
            break;
         }

         case DdeOleClientLinks.RECORD_NUMBER:
         {
            current = new DdeOleClientLinks(this, record);
            m_records.add(current);
            m_ddeOleClientLinks++;
            break;
         }

         case FileCreationRecord.RECORD_NUMBER:
         {
            current = getFileCreationRecord();
            ((FileCreationRecord)current).setValues(record);
            break;
         }

         default:
            throw new MPXException(MPXException.INVALID_RECORD);
      }

      return (current);
   }

   /**
    * This method is provided to allow child tasks that have been created
    * programatically to be added as a record to the main file.
    *
    * @param task task created as a child of another task
    */
   void addTask (Task task)
   {
      m_records.add(task);
      m_allTasks.add(task);
   }

   /**
    * This method allows a task to be added to the file programatically.
    *
    * @return new task object
    * @throws MPXException normally thrown on parse errors
    */
   public Task addTask ()
      throws MPXException
   {
      return ((Task)add(Task.RECORD_NUMBER));
   }

   /**
    * This method is used to retrieve a list of all of the top level tasks
    * that are defined in this MPX file.
    *
    * @return list of tasks
    */
   public LinkedList getChildTasks ()
   {
      return (m_childTasks);
   }

   /**
    * This method is used to retrieve a list of all of the tasks
    * that are defined in this MPX file.
    *
    * @return list of all tasks
    */
   public LinkedList getAllTasks ()
   {
      return (m_allTasks);
   }

   /**
    * Method for accessing the Task Model
    *
    * @return task model
    */
   TaskModel getTaskModel ()
   {
      return (m_taskModel);
   }

   /**
    * Method for accessing the Resource Model
    *
    * @return resource model
    */
   ResourceModel getResourceModel ()
   {
      return (m_resourceModel);
   }

   /**
    * Used to set whether WBS numbers are automatically created.
    *
    * @param flag true if automatic WBS required.
    */
   public void setAutoWBS (boolean flag)
   {
      m_autoWBS = flag;
   }

   /**
    * Used to set whether outline level numbers are automatically created.
    *
    * @param flag true if automatic outline level required.
    */
   public void setAutoOutlineLevel (boolean flag)
   {
      m_autoOutlineLevel = flag;
   }

   /**
    * Used to set whether outline numbers are automatically created.
    *
    * @param flag true if automatic outline number required.
    */
   public void setAutoOutlineNumber (boolean flag)
   {
      m_autoOutlineNumber = flag;
   }

   /**
    * Used to set whether the task unique ID field is automatically populated.
    *
    * @param flag true if automatic unique ID required.
    */
   public void setAutoTaskUniqueID (boolean flag)
   {
      m_autoTaskUniqueID = flag;
   }

   /**
    * Used to set whether the calendar unique ID field is automatically populated.
    *
    * @param flag true if automatic unique ID required.
    */
   public void setAutoCalendarUniqueID (boolean flag)
   {
      m_autoCalendarUniqueID = flag;
   }

   /**
    * Used to set whether the task ID field is automatically populated.
    *
    * @param flag true if automatic ID required.
    */
   public void setAutoTaskID (boolean flag)
   {
      m_autoTaskID = flag;
   }

   /**
    * Retrieve the flag that determines whether WBS is generated
    * automatically.
    *
    * @return boolean, default is false.
    */
   public boolean getAutoWBS ()
   {
      return (m_autoWBS);
   }

   /**
    * Retrieve the flag that determines whether outline level is generated
    * automatically.
    *
    * @return boolean, default is false.
    */
   public boolean getAutoOutlineLevel ()
   {
      return (m_autoOutlineLevel);
   }

   /**
    * Retrieve the flag that determines whether outline numbers are generated
    * automatically.
    *
    * @return boolean, default is false.
    */
   public boolean getAutoOutlineNumber ()
   {
      return (m_autoOutlineNumber);
   }

   /**
    * Retrieve the flag that determines whether the task unique ID
    * is generated automatically.
    *
    * @return boolean, default is false.
    */
   public boolean getAutoTaskUniqueID ()
   {
      return (m_autoTaskUniqueID);
   }

   /**
    * Retrieve the flag that determines whether the calendar unique ID
    * is generated automatically.
    *
    * @return boolean, default is false.
    */
   public boolean getAutoCalendarUniqueID ()
   {
      return (m_autoCalendarUniqueID);
   }

   /**
    * Retrieve the flag that determines whether the task ID
    * is generated automatically.
    *
    * @return boolean, default is false.
    */
   public boolean getAutoTaskID ()
   {
      return (m_autoTaskID);
   }

   /**
    * This method is used to retrieve the next unique ID for a task.
    *
    * @return next unique ID
    */
   int getTaskUniqueID ()
   {
      return (++m_taskUniqueID);
   }

   /**
    * This method is used to retrieve the next unique ID for a calendar.
    *
    * @return next unique ID
    */
   int getCalendarUniqueID ()
   {
      return (++m_calendarUniqueID);
   }

   /**
    * This method is used to retrieve the next ID for a task.
    *
    * @return next ID
    */
   int getTaskID ()
   {
      return (++m_taskID);
   }

   /**
    * Used to set whether the resource unique ID field is automatically populated.
    *
    * @param flag true if automatic unique ID required.
    */
   public void setAutoResourceUniqueID (boolean flag)
   {
      m_autoResourceUniqueID = flag;
   }

   /**
    * Used to set whether the resource ID field is automatically populated.
    *
    * @param flag true if automatic ID required.
    */
   public void setAutoResourceID (boolean flag)
   {
      m_autoResourceID = flag;
   }

   /**
    * Retrieve the flag that determines whether the resource unique ID
    * is generated automatically.
    *
    * @return boolean, default is false.
    */
   public boolean getAutoResourceUniqueID ()
   {
      return (m_autoResourceUniqueID);
   }

   /**
    * This method sets the flag indicating that the text version of the
    * Task and Resource Table Definition records should be ignored. Ignoring
    * these records gets around the problem where MPX files have been generated
    * with incorrect taks or resource field names, but correct task or resource
    * field numbers in the numeric version of the record.
    *
    * @param flag Boolean flag
    */
   public void setIgnoreTextModels (boolean flag)
   {
      m_ignoreTextModels = flag;
   }

   /**
    * Retrieves the flag indicating that the text version of the Task and
    * Resource Table Definition records should be ignored.
    *
    * @return Boolean flag
    */
   public boolean getIgnoreTextModels ()
   {
      return (m_ignoreTextModels);
   }

   /**
    * Retrieve the flag that determines whether the resource ID
    * is generated automatically.
    *
    * @return boolean, default is false.
    */
   public boolean getAutoResourceID ()
   {
      return (m_autoResourceID);
   }

   /**
    * This method is used to retrieve the next unique ID for a resource.
    *
    * @return next unique ID
    */
   int getResourceUniqueID ()
   {
      return (++m_resourceUniqueID);
   }

   /**
    * This method is used to retrieve the next ID for a resource.
    *
    * @return next ID
    */
   int getResourceID ()
   {
      return (++m_resourceID);
   }

   /**
    * Programatically add a new comment to the MPX file.
    *
    * @return new comment object
    * @throws MPXException normally thrown on parse errors
    */
   public Comments addComments ()
      throws MPXException
   {
      return ((Comments)add(Comments.RECORD_NUMBER));
   }

   /**
    * Retrieves the file creation record.
    *
    * @return file creation record.
    */
   public FileCreationRecord getFileCreationRecord ()
   {
      return (m_fileCreationRecord);
   }

   /**
    * This method is provided to create a resource calendar, before it
    * has been attached to a resource.
    *
    * @return new MPXCalendar instance
    */
   protected MPXCalendar addResourceCalendar ()
   {
      return (new MPXCalendar(this, false));
   }

   /**
    * This method is used to add a new base calendar to the file.
    *
    * @return new base calendar object
    * @throws MPXException normally thrown on parse errors
    */
   public MPXCalendar addBaseCalendar ()
      throws MPXException
   {
      return ((MPXCalendar)add(MPXCalendar.BASE_CALENDAR_RECORD_NUMBER));
   }

   /**
    * This is a convenience method used to add a base calendar called
    * "Standard" to the file, and populate it with a default working week
    * and default working hours
    *
    * @return a new default base calendar
    * @throws MPXException normally thrown when a parse error occurs
    */
   public MPXCalendar addDefaultBaseCalendar ()
      throws MPXException
   {
      MPXCalendar calendar = (MPXCalendar)add(MPXCalendar.BASE_CALENDAR_RECORD_NUMBER);

      calendar.setName(MPXCalendar.DEFAULT_BASE_CALENDAR_NAME);

      calendar.setWorkingDay(1, false);
      calendar.setWorkingDay(2, true);
      calendar.setWorkingDay(3, true);
      calendar.setWorkingDay(4, true);
      calendar.setWorkingDay(5, true);
      calendar.setWorkingDay(6, true);
      calendar.setWorkingDay(7, false);

      calendar.addDefaultCalendarHours();

      return (calendar);
   }

   /**
    * This is a protected convenience method to add a default resource
    * calendar. This is used when the calendar data is available before
    * the resource data has been read, a situation which occurs with MPP
    * files.
    *
    * @return new MPXCalendar instance
    * @throws MPXException
    */
   protected MPXCalendar addDefaultResourceCalendar ()
   {
      MPXCalendar calendar = new MPXCalendar(this, false);

      calendar.setWorkingDay(1, MPXCalendar.DEFAULT);
      calendar.setWorkingDay(2, MPXCalendar.DEFAULT);
      calendar.setWorkingDay(3, MPXCalendar.DEFAULT);
      calendar.setWorkingDay(4, MPXCalendar.DEFAULT);
      calendar.setWorkingDay(5, MPXCalendar.DEFAULT);
      calendar.setWorkingDay(6, MPXCalendar.DEFAULT);
      calendar.setWorkingDay(7, MPXCalendar.DEFAULT);

      return (calendar);
   }

   /**
    * This is a convenience method to allow a pre-existing calendar
    * to be attached to a resource
    *
    * @param resource Resource instance
    * @param calendar MPXCalendar instance
    */
   protected void attachResourceCalendar (Resource resource, MPXCalendar calendar)
   {
      resource.attachResourceCalendar(calendar);
   }

   /**
    * This method retrieves the list of base calendars defined in
    * this file.
    *
    * @return List of calendars
    */
   public LinkedList getBaseCalendars ()
   {
      return (m_baseCalendars);
   }

   /**
    * This method is used to retrieve the project header record.
    *
    * @return project header object
    */
   public ProjectHeader getProjectHeader ()
   {
      return (m_projectHeader);
   }

   /**
    * This method is used to add a new resource to the file.
    *
    * @return new resource object
    * @throws MPXException normally thrown on parse errors
    */
   public Resource addResource ()
      throws MPXException
   {
      return ((Resource)add(Resource.RECORD_NUMBER));
   }

   /**
    * This method is used to retrieve a list of all of the resources
    * that are defined in this MPX file.
    *
    * @return list of all resources
    */
   public LinkedList getAllResources ()
   {
      return (m_allResources);
   }

   /**
    * This method is used to retrieve a list of all of the resource assignments
    * that are defined in this MPX file.
    *
    * @return list of all resources
    */
   public LinkedList getAllResourceAssignments ()
   {
      return (m_allResourceAssignments);
   }

   /**
    * This method is provided to allow resource assignments that have been created
    * programatically to be added as a record to the main file.
    *
    * @param assignment Resource assignment created as part of a task
    */
   void addResourceAssignment (ResourceAssignment assignment)
   {
      m_allResourceAssignments.add(assignment);
   }

   /**
    * This method is used to add project names to the file.
    *
    * @return new project names object
    * @throws MPXException normally thrown on parse errors
    */
   public ProjectNames addProjectNames ()
      throws MPXException
   {
      return ((ProjectNames)add(ProjectNames.RECORD_NUMBER));
   }

   /**
    * This method is used to add new dde/ole links to the file.
    *
    * @return new dde/ole links object
    * @throws MPXException normally thrown on parse errors
    */
   public DdeOleClientLinks addDdeOleClientLinks ()
      throws MPXException
   {
      return ((DdeOleClientLinks)add(DdeOleClientLinks.RECORD_NUMBER));
   }

   /**
    * Retrieves the named base calendar. This method will return
    * null if the named base calendar is not located.
    *
    * @param calendarName name of the required base calendar
    * @return base calendar object
    */
   public MPXCalendar getBaseCalendar (String calendarName)
   {
      MPXCalendar calendar = null;
      String name;
      Iterator iter = m_baseCalendars.iterator();

      while (iter.hasNext() == true)
      {
         calendar = (MPXCalendar)iter.next();
         name = calendar.getName();

         if ((name != null) && (name.equalsIgnoreCase(calendarName) == true))
         {
            break;
         }

         calendar = null;
      }

      return (calendar);
   }

   /**
    * Retrieves the base calendar referred to by the supplied unique ID
    * value. This method will return null if the required calendar is not
    * located.
    *
    * @param calendarID calendar unique ID
    * @return MPXCalendar instance
    */
   public MPXCalendar getBaseCalendarByUniqueID (int calendarID)
   {
      MPXCalendar calendar = null;
      Iterator iter = m_baseCalendars.iterator();

      while (iter.hasNext() == true)
      {
         calendar = (MPXCalendar)iter.next();

         if (calendar.getUniqueID() == calendarID)
         {
            break;
         }

         calendar = null;
      }

      return (calendar);
   }

   /**
    * This method writes each record in an MPX file to an output stream, via
    * the specified OutputStreamWriter. By providing the OutputStreamWriter
    * as an argument, the caller can control the character encoding used
    * when writing the file. Note that this is not recommended, the other
    * write methods made available with this class will use the correct
    * encoding as specified as part of the MPX file creation record.
    *
    * @param w OutputStreamWriterinstance
    * @throws IOException thrown on failure to write to the output stream
    */
   public void write (OutputStreamWriter w)
      throws IOException
   {
      updateFormats();

      Iterator iter = m_records.iterator();

      while (iter.hasNext())
      {
         w.write((iter.next()).toString());
      }

      w.flush();

      //
      // Reset the model written flags to allow them to be written again
      //
      m_taskModel.setWritten(false);
      m_resourceModel.setWritten(false);
   }

   /**
    * This method writes each record in the MPX file to an output stream.
    *
    * @param out destination output stream
    * @throws IOException thrown on failure to write to the output stream
    */
   public void write (OutputStream out)
      throws IOException
   {
      write(new OutputStreamWriter(new BufferedOutputStream(out), getFileCreationRecord().getCodePage().getCharset()));
   }

   /**
    * This is a convenience method provided to allow the contents of the MPX
    * file to be written to a file specified by the File object passed as
    * a parameter.
    *
    * @param out destination output file
    * @throws IOException thrown on failure to write to the file
    */
   public void write (File out)
      throws IOException
   {
      FileOutputStream fos = new FileOutputStream(out);
      write(fos);
      fos.flush();
      fos.close();
   }

   /**
    * This is a convenience method provided to allow the contents of the MPX
    * file to be written to a file specified by the file name passed as
    * a parameter.
    *
    * @param file destination output file
    * @throws IOException thrown on failure to write to the file
    */
   public void write (String file)
      throws IOException
   {
      FileOutputStream fos = new FileOutputStream(file);
      write(fos);
      fos.flush();
      fos.close();
   }

   /**
    * This method retrieves the time formatter.
    *
    * @return time formatter
    */
   MPXTimeFormat getTimeFormat ()
   {
      return (m_timeFormat);
   }

   /**
    * This method retrieves the date time formatter.
    *
    * @return date time formatter
    */
   MPXDateFormat getDateTimeFormat ()
   {
      return (m_dateTimeFormat);
   }

   /**
    * This method retrieves the date formatter.
    *
    * @return date formatter
    */
   MPXDateFormat getDateFormat ()
   {
      return (m_dateFormat);
   }

   /**
    * This method retrieves the currency formatter.
    *
    * @return currency formatter
    */
   MPXNumberFormat getCurrencyFormat ()
   {
      return (m_currencyFormat);
   }

   /**
    * This method is used to retrieve the number of child tasks associated
    * with this parent task. This method is used as part of the process
    * of automatically generating the WBS.
    *
    * @return Number of child tasks
    */
   int getChildTaskCount ()
   {
      return (m_childTasks.size());
   }

   /**
    * This method is used to calculate the duration of work between two fixed
    * dates according to the work schedule defined in the named calendar. The
    * calendar used is the "Standard" calendar. If this calendar does not exist,
    * and exception will be thrown.
    *
    * @param startDate start of the period
    * @param endDate end of the period
    * @return new MPXDuration object
    * @throws MPXException normally when no Standard calendar is available
    */
   public MPXDuration getDuration (Date startDate, Date endDate)
      throws MPXException
   {
      return (getDuration("Standard", startDate, endDate));
   }

   /**
    * This method is used to calculate the duration of work between two fixed
    * dates according to the work schedule defined in the named calendar.
    * The name of the calendar to be used is passed as an argument.
    *
    * @param calendarName name of the calendar to use
    * @param startDate start of the period
    * @param endDate end of the period
    * @return new MPXDuration object
    * @throws MPXException normally when no Standard calendar is available
    */
   public MPXDuration getDuration (String calendarName, Date startDate, Date endDate)
      throws MPXException
   {
      MPXCalendar calendar = getBaseCalendar(calendarName);

      if (calendar == null)
      {
         throw new MPXException(MPXException.CALENDAR_ERROR + ": " + calendarName);
      }

      return (calendar.getDuration(startDate, endDate));
   }

   /**
    * This method allows an arbitrary task to be retrieved based
    * on its ID field.
    *
    * @param id task identified
    * @return the requested task, or null if not found
    */
   public Task getTaskByID (int id)
   {
      return ((Task)m_taskIDMap.get(new Integer(id)));
   }

   /**
    * This method allows an arbitrary task to be retrieved based
    * on its UniqueID field.
    *
    * @param id task identified
    * @return the requested task, or null if not found
    */
   public Task getTaskByUniqueID (int id)
   {
      return ((Task)m_taskUniqueIDMap.get(new Integer(id)));
   }

   /**
    * This method allows an arbitrary resource to be retrieved based
    * on its ID field.
    *
    * @param id resource identified
    * @return the requested resource, or null if not found
    */
   public Resource getResourceByID (int id)
   {
      return ((Resource)m_resourceIDMap.get(new Integer(id)));
   }

   /**
    * This method allows an arbitrary resource to be retrieved based
    * on its UniqueID field.
    *
    * @param id resource identified
    * @return the requested resource, or null if not found
    */
   public Resource getResourceByUniqueID (int id)
   {
      return ((Resource)m_resourceUniqueIDMap.get(new Integer(id)));
   }

   /**
    * This method is used to recreate the hierarchical structure of the
    * MPX file from scratch. The method sorts the list of all tasks,
    * then iterates through it creating the parent-child structure defined
    * by the outline level field.
    */
   protected void updateStructure ()
   {
      if (m_allTasks.size() > 1)
      {
         Collections.sort(m_allTasks);
         m_childTasks.clear();

         Task task;
         Task lastTask = null;
         Task parent;
         int level;
         int lastLevel = -1;

         Iterator iter = m_allTasks.iterator();

         while (iter.hasNext() == true)
         {
            task = (Task)iter.next();
            task.clearChildTasks();
            level = task.getOutlineLevelValue();
            parent = null;

            if (lastTask != null)
            {
               if (level == lastLevel)
               {
                  parent = lastTask.getParentTask();
               }
               else
               {
                  if (level > lastLevel)
                  {
                     parent = lastTask;
                  }
                  else
                  {
                     while (level <= lastLevel)
                     {
                        parent = lastTask.getParentTask();

                        if (parent == null)
                        {
                           break;
                        }

                        lastLevel = parent.getOutlineLevelValue();
                        lastTask = parent;
                     }
                  }
               }
            }

            lastTask = task;
            lastLevel = level;

            if (getAutoWBS() == true)
            {
               task.generateWBS(parent);
            }

            if (getAutoOutlineNumber() == true)
            {
               task.generateOutlineNumber(parent);
            }

            if (parent == null)
            {
               m_childTasks.add(task);
            }
            else
            {
               parent.addChildTask(task);
            }
         }
      }
   }

   /**
    * Accessor method used to retrieve the decimal separator character.
    * Note that this value is synchronized with the same value in the
    * currency settings record. This value affects all decimal numbers
    * that appear in the MPX file.
    *
    * @return decimal separator character
    */
   public char getDecimalSeparator ()
   {
      return (m_decimalSeparator);
   }

   /**
    * Modifier method used to set the decimal separator character.
    * Note that this value is synchronized with the same value in the
    * currency settings record. This value affects all decimal numbers
    * that appear in the MPX file.
    *
    * @param separator decimal separator character
    */
   public void setDecimalSeparator (char separator)
   {
      m_decimalSeparator = separator;

      if ((m_projectHeader != null) && (m_projectHeader.getDecimalSeparator() != separator))
      {
         m_projectHeader.setDecimalSeparator(separator);
      }
   }

   /**
    * Accessor method used to retrieve the thousands separator character.
    * Note that this value is synchronized with the same value in the
    * currency settings record. This value affects all decimal numbers
    * that appear in the MPX file.
    *
    * @return thousands separator character
    */
   public char getThousandsSeparator ()
   {
      return (m_thousandsSeparator);
   }

   /**
    * Modifier method used to set the thousands separator character.
    * Note that this value is synchronized with the same value in the
    * currency settings record. This value affects all decimal numbers
    * that appear in the MPX file.
    *
    * @param separator thousands separator character
    */
   public void setThousandsSeparator (char separator)
   {
      m_thousandsSeparator = separator;

      if ((m_projectHeader != null) && (m_projectHeader.getThousandsSeparator() != separator))
      {
         m_projectHeader.setThousandsSeparator(separator);
      }
   }

   /**
    * Find the earliest task start date. We treat this as the
    * start date for the project.
    *
    * @return start date
    */
   public Date getStartDate ()
   {
      Date startDate = null;

      Iterator iter = m_allTasks.iterator();
      Task task;
      Date taskStartDate;
     
      while (iter.hasNext() == true)
      {
         task = (Task)iter.next();
         
         //
         // If a hidden "summary" task is present we ignore it
         //
         if (task.getUniqueIDValue() == 0)
         {
            continue;
         }
         
         //
         // Select the actual or forecast start date
         //
         taskStartDate = task.getActualStart();
         if (taskStartDate == null)
         {
            taskStartDate = task.getStart();
         }

         if (taskStartDate != null)
         {
            if (startDate == null)
            {
               startDate = taskStartDate;
            }
            else
            {
               if (taskStartDate.getTime() < startDate.getTime())
               {
                  startDate = taskStartDate;
               }
            }
         }
      }

      return (startDate);
   }

   /**
    * Find the latest task finish date. We treat this as the
    * finish date for the project.
    *
    * @return finish date
    */
   public Date getFinishDate ()
   {
      Date finishDate = null;

      Iterator iter = m_allTasks.iterator();
      Task task;
      Date taskFinishDate;

      while (iter.hasNext() == true)
      {
         task = (Task)iter.next();
         
         //
         // If a hidden "summary" task is present we ignore it
         //
         if (task.getUniqueIDValue() == 0)
         {
            continue;
         }
         
         //
         // Select the actual or forecast start date
         //
         taskFinishDate = task.getActualFinish();
         if (taskFinishDate == null)
         {
            taskFinishDate = task.getFinish();
         }
         
         if (taskFinishDate != null)
         {
            if (finishDate == null)
            {
               finishDate = taskFinishDate;
            }
            else
            {
               if (taskFinishDate.getTime() > finishDate.getTime())
               {
                  finishDate = taskFinishDate;
               }
            }
         }
      }

      return (finishDate);
   }

   /**
    * This method is called prior to writing an MPX file to ensure that all of
    * the required number formats are up to date.
    */
   private void updateFormats ()
   {
      m_decimalFormat = new MPXNumberFormat("0.00#", m_decimalSeparator, m_thousandsSeparator);
      m_durationDecimalFormat = new MPXNumberFormat(MPXDuration.DECIMAL_FORMAT_STRING, m_decimalSeparator, m_thousandsSeparator);
      m_percentageDecimalFormat = new MPXNumberFormat(MPXPercentage.DECIMAL_FORMAT_STRING, m_decimalSeparator, m_thousandsSeparator);
      m_unitsDecimalFormat = new MPXNumberFormat(MPXUnits.DECIMAL_FORMAT_STRING, m_decimalSeparator, m_thousandsSeparator);
   }

   /**
    * Package private method used to retrieve the standard decimal format
    * used for writing MPX records.
    *
    * @return MPXNumberFormat instance
    */
   MPXNumberFormat getDecimalFormat ()
   {
      return (new MPXNumberFormat("0.00#", m_decimalSeparator, m_thousandsSeparator));
   }

   /**
    * Package private method used to retrieve the standard decimal format
    * used for writing MPXDuration values.
    *
    * @return MPXNumberFormat instance
    */
   MPXNumberFormat getDurationDecimalFormat ()
   {
      return (new MPXNumberFormat(MPXDuration.DECIMAL_FORMAT_STRING, m_decimalSeparator, m_thousandsSeparator));
   }

   /**
    * Package private method used to retrieve the standard decimal format
    * used for writing MPXPercentage values.
    *
    * @return MPXNumberFormat instance
    */
   MPXNumberFormat getPercentageDecimalFormat ()
   {
      return (new MPXNumberFormat(MPXPercentage.DECIMAL_FORMAT_STRING, m_decimalSeparator, m_thousandsSeparator));
   }

   /**
    * Package private method used to retrieve the standard decimal format
    * used for writing MPXUnits values.
    *
    * @return MPXNumberFormat instance
    */
   MPXNumberFormat getUnitsDecimalFormat ()
   {
      return (new MPXNumberFormat(MPXUnits.DECIMAL_FORMAT_STRING, m_decimalSeparator, m_thousandsSeparator));
   }

   /**
    * This method returns the locale used by this MPX file.
    *
    * @return current locale
    */
   public Locale getLocale ()
   {
      return (m_locale);
   }

   /**
    * Locale used for this MPX file. Defaults to English.
    */
   private Locale m_locale = Locale.ENGLISH;

   /**
    * This method sets the locale to be used by this MPX file.
    *
    * @param locale locale to be used
    */
   public void setLocale (Locale locale)
   {
      m_locale = locale;

      m_delimiter = LocaleData.getChar(m_locale, LocaleData.FILE_DELIMITER);
      m_thousandsSeparator = LocaleData.getChar(m_locale, LocaleData.CURRENCY_THOUSANDS_SEPARATOR);
      m_decimalSeparator = LocaleData.getChar(m_locale, LocaleData.CURRENCY_DECIMAL_SEPARATOR);
      m_fileCreationRecord.setLocale(locale);
      m_projectHeader.setLocale(locale);
      m_dateTimeFormat.setLocale(locale);
      m_dateFormat.setLocale(locale);
      m_timeFormat.setLocale(locale);
      m_taskModel.setLocale(locale);
      m_resourceModel.setLocale(locale);
   }

   /**
    * Associates an alias with a custom task field number
    *
    * @param field custom field number
    * @param alias alias text
    */
   public void setTaskFieldAlias (int field, String alias)
   {
      if ((alias != null) && (alias.length() != 0))
      {
         Integer id = new Integer(field);
         m_taskFieldAlias.put(id, alias);
         m_aliasTaskField.put(alias, id);
      }
   }

   /**
    * Retrieves the alias associated with a custom task field.
    * This method will return null if no alias has been defined for
    * this field
    *
    * @param field field number
    * @return alias text
    */
   public String getTaskFieldAlias (int field)
   {
      return ((String)m_taskFieldAlias.get(new Integer(field)));
   }

   /**
    * Retrieves the field number of a task field based on its alias. If the
    * alias is not recognised, this method will return -1.
    *
    * @param alias alias text
    * @return task field number
    */
   public int getAliasTaskField (String alias)
   {
      Integer result = (Integer)m_aliasTaskField.get(alias);
      int field;

      if (result == null)
      {
         field = -1;
      }
      else
      {
         field = result.intValue();
      }

      return (field);
   }

   /**
    * Associates an alias with a custom resource field number
    *
    * @param field custom field number
    * @param alias alias text
    */
   public void setResourceFieldAlias (int field, String alias)
   {
      if ((alias != null) && (alias.length() != 0))
      {
         Integer id = new Integer(field);
         m_resourceFieldAlias.put(id, alias);
         m_aliasResourceField.put(alias, id);
      }
   }

   /**
    * Retrieves the alias associated with a custom resource field.
    * This method will return null if no alias has been defined for
    * this field
    *
    * @param field field number
    * @return alias text
    */
   public String getResourceFieldAlias (int field)
   {
      return ((String)m_resourceFieldAlias.get(new Integer(field)));
   }

   /**
    * Retrieves the field number of a resource field based on its alias. If the
    * alias is not recognised, this method will return -1.
    *
    * @param alias alias text
    * @return task field number
    */
   public int getAliasResourceField (String alias)
   {
      Integer result = (Integer)m_aliasResourceField.get(alias);
      int field;

      if (result == null)
      {
         field = -1;
      }
      else
      {
         field = result.intValue();
      }

      return (field);
   }

   /**
    * Allows derived classes to determine if task field aliases have
    * been defined.
    *
    * @return true if task field aliases have been defined
    */
   protected boolean taskFieldAliasesDefined ()
   {
      return (!m_taskFieldAlias.isEmpty());
   }

   /**
    * Allows derived classes to determine if resource field aliases have
    * been defined.
    *
    * @return true if resource field aliases have been defined
    */
   protected boolean resourceFieldAliasesDefined ()
   {
      return (!m_resourceFieldAlias.isEmpty());
   }

   /**
    * Allows derived classes to gain access to the mapping between
    * MPX task field numbers and aliases
    *
    * @return task field to alias map
    */
   protected HashMap getTaskFieldAliasMap ()
   {
      return (m_taskFieldAlias);
   }

   /**
    * Allows derived classes to gain access to the mapping between
    * MPX resource field numbers and aliases
    *
    * @return resource field to alias map
    */
   protected HashMap getResourceFieldAliasMap ()
   {
      return (m_resourceFieldAlias);
   }

   /**
    * Removes an id-to-task mapping
    *
    * @param id task unique ID
    */
   void unmapTaskUniqueID (Integer id)
   {
      m_taskUniqueIDMap.remove(id);
   }

   /**
    * Adds an id-to-task mapping
    *
    * @param id task unique ID
    * @param task task instance
    */
   void mapTaskUniqueID (Integer id, Task task)
   {
      m_taskUniqueIDMap.put(id, task);
   }

   /**
    * Removes an id-to-task mapping
    *
    * @param id task ID
    */
   void unmapTaskID (Integer id)
   {
      m_taskIDMap.remove(id);
   }

   /**
    * Adds an id-to-task mapping
    *
    * @param id task ID
    * @param task task instance
    */
   void mapTaskID (Integer id, Task task)
   {
      m_taskIDMap.put(id, task);
   }

   /**
    * Removes an id-to-resource mapping
    *
    * @param id resource unique ID
    */
   void unmapResourceUniqueID (Integer id)
   {
      m_resourceUniqueIDMap.remove(id);
   }

   /**
    * Adds an id-to-resource mapping
    *
    * @param id resource unique ID
    * @param resource resource instance
    */
   void mapResourceUniqueID (Integer id, Resource resource)
   {
      m_resourceUniqueIDMap.put(id, resource);
   }

   /**
    * Removes an id-to-resource mapping
    *
    * @param id resource ID
    */
   void unmapResourceID (Integer id)
   {
      m_resourceIDMap.remove(id);
   }

   /**
    * Adds an id-to-resource mapping
    *
    * @param id resource ID
    * @param resource resource instance
    */
   void mapResourceID (Integer id, Resource resource)
   {
      m_resourceIDMap.put(id, resource);
   }

   /**
    * Constant containing the end of line characters used in MPX files.
    * Note that this constant has package level access only.
    */
   static final String EOL = "\r\n";

   /**
    * Counter used to populate the unique ID field of a task
    */
   private int m_taskUniqueID;

   /**
    * Counter used to populate the unique ID field of a calendar
    */
   private int m_calendarUniqueID;

   /**
    * Counter used to populate the ID field of a task
    */
   private int m_taskID;

   /**
    * Counter used to populate the unique ID field of a resource
    */
   private int m_resourceUniqueID;

   /**
    * Counter used to populate the ID field of a resource
    */
   private int m_resourceID;

   /**
    * List to maintain records in the order that they are added.
    */
   private LinkedList m_records = new LinkedList();

   /**
    * This list holds a reference to all resources defined in the
    * MPX file.
    */
   private LinkedList m_allResources = new LinkedList();

   /**
    * This list holds a reference to all tasks defined in the
    * MPX file.
    */
   private LinkedList m_allTasks = new LinkedList();

   /**
    * List holding references to the top level tasks
    * as defined by the outline level.
    */
   private LinkedList m_childTasks = new LinkedList();

   /**
    * This list holds a reference to all resource assignments defined in the
    * MPX file.
    */
   private LinkedList m_allResourceAssignments = new LinkedList();

   /**
    * List holding references to all base calendars.
    */
   private LinkedList m_baseCalendars = new LinkedList();

   /**
    * Date time formatter.
    */
   private MPXDateFormat m_dateTimeFormat = new MPXDateFormat();

   /**
    * Date formatter.
    */
   private MPXDateFormat m_dateFormat = new MPXDateFormat();

   /**
    * Time formatter.
    */
   private MPXTimeFormat m_timeFormat = new MPXTimeFormat();

   /**
    * Currency formatter.
    */
   private MPXNumberFormat m_currencyFormat = new MPXNumberFormat();

   /**
    * File creation record.
    */
   private FileCreationRecord m_fileCreationRecord = new FileCreationRecord(this);

   /**
    * Project header record.
    */
   private ProjectHeader m_projectHeader = new ProjectHeader(this);

   /**
    * Task model.
    */
   private TaskModel m_taskModel = new TaskModel(this);

   /**
    * Resource model.
    */
   private ResourceModel m_resourceModel = new ResourceModel(this);

   /**
    * Reference to the last task added to the file.
    */
   private Task m_lastTask;

   /**
    * Reference to the last resource added to the file.
    */
   private Resource m_lastResource;

   /**
    * Reference to the last resource calendar added to the file.
    */
   private MPXCalendar m_lastResourceCalendar;

   /**
    * Reference to the last resource assignment added to the file.
    */
   private ResourceAssignment m_lastResourceAssignment;

   /**
    * Reference to the last base calendar added to the file.
    */
   private MPXCalendar m_lastBaseCalendar;

   /**
    * Flag indicating the existence of a resource model record.
    */
   private boolean m_resourceTableDefinition;

   /**
    * Flag indicating the existence of a task model record.
    */
   private boolean m_taskTableDefinition;

   /**
    * Count of the number of project names.
    */
   private int m_projectNames;

   /**
    * Count of the number of dde/ole links.
    */
   private int m_ddeOleClientLinks;

   /**
    * Character to be used as delimiter throughout this file.
    */
   private char m_delimiter = LocaleData.getChar(m_locale, LocaleData.FILE_DELIMITER);

   /**
    * Indicating whether WBS value should be calculated on creation, or will
    * be manually set.
    */
   private boolean m_autoWBS;

   /**
    * Indicating whether the Outline Level value should be calculated on
    * creation, or will be manually set.
    */
   private boolean m_autoOutlineLevel;

   /**
    * Indicating whether the Outline Number value should be calculated on
    * creation, or will be manually set.
    */
   private boolean m_autoOutlineNumber;

   /**
    * Indicating whether the unique ID of a task should be
    * calculated on creation, or will be manually set.
    */
   private boolean m_autoTaskUniqueID;

   /**
    * Indicating whether the unique ID of a calendar should be
    * calculated on creation, or will be manually set.
    */
   private boolean m_autoCalendarUniqueID;

   /**
    * Indicating whether the ID of a task should be
    * calculated on creation, or will be manually set.
    */
   private boolean m_autoTaskID;

   /**
    * Indicating whether the unique ID of a resource should be
    * calculated on creation, or will be manually set.
    */
   private boolean m_autoResourceUniqueID;

   /**
    * Indicating whether the ID of a resource should be
    * calculated on creation, or will be manually set.
    */
   private boolean m_autoResourceID;

   /**
    * Flag indicating that the text form of the task and resource
    * models should be ignored.
    */
   private boolean m_ignoreTextModels = true;

   /**
    * This member data is used to hold the outline level number of the
    * first outline level used in the MPX file. When data from
    * Microsoft Project is saved in MPX format, MSP creates an invisible
    * task with an outline level as zero, which acts as an umbrella
    * task for all of the other tasks defined in the file. This is not
    * a strict requirement, and an MPX file could be generated from another
    * source that only contains "visible" tasks that have outline levels
    * >= 1.
    */
   private int m_baseOutlineLevel = -1;

   /**
    * Default thousands separator character. Despite the fact that this
    * value appears as part of the CurrencySettings, it is in fact a global
    * setting, which is why this attribute is defined here.
    */
   private char m_thousandsSeparator = LocaleData.getChar(m_locale, LocaleData.CURRENCY_THOUSANDS_SEPARATOR);

   /**
    * Default decimal separator character. Despite the fact that this
    * value appears as part of the CurrencySettings, it is in fact a global
    * setting, which is why this attribute is defined here.
    */
   private char m_decimalSeparator = LocaleData.getChar(m_locale, LocaleData.CURRENCY_DECIMAL_SEPARATOR);

   /**
    * Number format used for writing decimal values.
    */
   private MPXNumberFormat m_decimalFormat;

   /**
    * Number format used for writing MPXDuration values.
    */
   private MPXNumberFormat m_durationDecimalFormat;

   /**
    * Number format used for writing MPXPercentage values.
    */
   private MPXNumberFormat m_percentageDecimalFormat;

   /**
    * Number format used for writing MPXUnits values.
    */
   private MPXNumberFormat m_unitsDecimalFormat;

   /**
    * Maps from a task field number to a task alias.
    */
   private HashMap m_taskFieldAlias = new HashMap();

   /**
    * Maps from a task field alias to a task field number
    */
   private HashMap m_aliasTaskField = new HashMap();

   /**
    * Maps from a resource field number to a resource alias.
    */
   private HashMap m_resourceFieldAlias = new HashMap();

   /**
    * Maps from a resource field alias to a resource field number
    */
   private HashMap m_aliasResourceField = new HashMap();

   /**
    * Maps from a task unique ID to a task instance
    */
   private HashMap m_taskUniqueIDMap = new HashMap();

   /**
    * Maps from a task ID to a task instance
    */
   private HashMap m_taskIDMap = new HashMap();

   /**
    * Maps from a resource unique ID to a resource instance
    */
   private HashMap m_resourceUniqueIDMap = new HashMap();

   /**
    * Maps from a resource ID to a resource instance
    */
   private HashMap m_resourceIDMap = new HashMap();
}
