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

import java.io.File;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Collections;

/**
 * This class encapsulates all functionality relating to creating, read
 * and writing MPX files and their constituent records.
 *
 * @todo Ensure that the underlying lists are returned unmodifyable
 */
public class MPXFile
{
   /**
    * Default constructor.
    *
    * @throws MPXException if construction fails
    */
   public MPXFile()
      throws MPXException
   {
      configure ();
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
      configure ();
      read (stream);
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
      read (new File (path));
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
      configure ();
      read (file);
   }


   /**
    * This method configures the basic MPX file.
    */
   private void configure ()
   {
      m_records.add (m_fileCreationRecord);
      m_records.add (m_currencySettings);
      m_records.add (m_defaultSettings);
      m_records.add (m_dateTimeSettings);
      m_records.add (m_projectHeader);
   }

   /**
    * Accessor method to retrieve the current file delimiter character.
    *
    * @return delimiter character
    */
   public char getDelimiter()
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
   private void read (InputStream is)
      throws MPXException
   {
      try
      {
         //
         // Test the header and extract the separator. If this is successful,
         // we reset the stream back as far as we can. The design of the
         // BufferedInputStream class means that we can't get back to character
         // zero, so the first record we will read will get "PX" rather than
         // "MPX" in the first field position.
         //
         BufferedInputStream bis = new BufferedInputStream (is);
         byte[] data = new byte[4];
         data[0] = (byte)bis.read();
         bis.mark(1024);
         data[1] = (byte)bis.read();
         data[2] = (byte)bis.read();
         data[3] = (byte)bis.read();

         if (data[0] != 'M' || data[1] != 'P' || data[2] != 'X')
         {
            throw new MPXException (MPXException.INVALID_FILE);
         }

         setDelimiter ((char)data[3]);

         bis.reset();

         //
         // Now process the file in full
         //
         InputStreamReader reader = new InputStreamReader (bis);
         Tokenizer tk = new Tokenizer (reader);
         tk.setDelimiter(m_delimiter);
         Record record;
         String number;

         //
         // Add the header record using a dummy value for the record number
         //
         add ("999", new Record (this, tk));

         //
         // Read the remainder of the records
         //
         while (true)
         {
            record = new Record (this, tk);
            number = record.getRecordNumber();
            if (number == null)
            {
               break;
            }

            add (number, record);
         }
      }

      catch (IOException ex)
      {
         throw new MPXException (MPXException.READ_ERROR, ex);
      }

   }

   /**
    * This is a convenience method to read an MPX file
    * from a file object.
    *
    * @param file the file to be read.
    * @throws MPXException thrown if a file read error occurs
    */
   private void read (File file)
     throws MPXException
   {
      try
      {
         FileInputStream fis = new FileInputStream (file);
         read (fis);
         fis.close();
      }

      catch (IOException ex)
      {
         throw new MPXException (MPXException.READ_ERROR, ex);
      }
   }


   /**
    * This is a convenience method provided to allow an empty record
    * of a specified type to be added to the file.
    *
    * @param recordNumber type of record to be added
    * @return object representing the new record
    */
   private MPXRecord add (int recordNumber)
      throws MPXException
   {
      return (add (String.valueOf(recordNumber), Record.EMPTY_RECORD));
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

      switch (Integer.parseInt (recordNumber))
      {
         case Comments.RECORD_NUMBER:
         {
            current = new Comments (this, record);
            m_records.add (current);
            break;
         }

         case CurrencySettings.RECORD_NUMBER:
         {
            m_currencySettings.update (record);
            current = m_currencySettings;
            break;
         }

         case DefaultSettings.RECORD_NUMBER:
         {
            m_defaultSettings.update (record);
            current = m_defaultSettings;
            break;
         }

         case DateTimeSettings.RECORD_NUMBER:
         {
            m_dateTimeSettings.update (record);
            current = m_dateTimeSettings;
            break;
         }

         case BaseCalendar.RECORD_NUMBER:
         {
            if (m_baseCalendarCount < MAX_BASE_CALENDARS)
            {
               m_lastBaseCalendar = new BaseCalendar (this, record);
               current = m_lastBaseCalendar;
               m_records.add (current);
               m_baseCalendars.add (current);
               ++m_baseCalendarCount;
            }
            break;
         }

         case BaseCalendarHours.RECORD_NUMBER:
         {
            if (m_lastBaseCalendar != null)
            {
               current = m_lastBaseCalendar.addBaseCalendarHours (record);
            }
            break;
         }

         case BaseCalendarException.RECORD_NUMBER:
         {
            if (m_lastBaseCalendar != null)
            {
               current = m_lastBaseCalendar.addBaseCalendarException (record);
            }
            break;
         }

         case ProjectHeader.RECORD_NUMBER:
         {
            m_projectHeader.update (record);
            current = m_projectHeader;
            break;
         }

         case ResourceModel.RECORD_NUMBER_TEXT:
         {
            if (m_resourceTableDefinition == false)
            {
               current = m_resourceModel;
               m_resourceModel.update (record, true);
               m_resourceTableDefinition=true;
            }
            break;
         }

         case ResourceModel.RECORD_NUMBER_NUMERIC:
         {
            if (m_resourceTableDefinition == false)
            {
               current = m_resourceModel;
               m_resourceModel.update (record, false);
               m_resourceTableDefinition=true;
            }
            break;
         }

         case Resource.RECORD_NUMBER:
         {
            if (m_resourceCount < MAX_RESOURCES)
            {
               m_lastResource = new Resource (this, record);
               current = m_lastResource;
               m_records.add (current);
               m_allResources.add (current);
            }

            break;
         }

         case ResourceNotes.RECORD_NUMBER:
         {
            if (m_lastResource != null)
            {
               current = m_lastResource.addResourceNotes (record);
            }
            break;
         }

         case ResourceCalendar.RECORD_NUMBER:
         {
            if (m_lastResource != null)
            {
               m_lastResourceCalendar = m_lastResource.addResourceCalendar (record);
               current = m_lastResourceCalendar;
            }
            break;
         }

         case ResourceCalendarHours.RECORD_NUMBER:
         {
            if (m_lastResourceCalendar != null)
            {
               current = m_lastResourceCalendar.addResourceCalendarHours (record);
            }
            break;
         }

         case ResourceCalendarException.RECORD_NUMBER:
         {
            if (m_lastResourceCalendar != null)
            {
               current = m_lastResourceCalendar.addResourceCalendarException (record);
            }
            break;
         }

         case TaskModel.RECORD_NUMBER_TEXT:
         {
            if (m_taskTableDefinition == false)
            {
               current = m_taskModel;
               m_taskModel.update (record, true);
               m_taskTableDefinition=true;
            }
            break;
         }

         case TaskModel.RECORD_NUMBER_NUMERIC:
         {
            if (m_taskTableDefinition == false)
            {
               current = m_taskModel;
               m_taskModel.update (record, false);
               m_taskTableDefinition=true;
            }
            break;
         }

         case Task.RECORD_NUMBER:
         {
            if (m_allTasks.size() < MAX_TASKS)
            {
               m_lastTask = new Task(this, record);
               current = m_lastTask;
               m_records.add (current);
               m_allTasks.add (current);

               int outlineLevel = m_lastTask.getOutlineLevelValue();

               if (m_baseOutlineLevel == -1)
               {
                  m_baseOutlineLevel = outlineLevel;
               }

               if (outlineLevel == m_baseOutlineLevel)
               {
                  m_childTasks.add (m_lastTask);
               }
               else
               {
                  if (m_childTasks.isEmpty() == true)
                  {
                     throw new MPXException (MPXException.INVALID_OUTLINE);
                  }

                  ((Task)m_childTasks.getLast()).addChildTask (m_lastTask, outlineLevel);
               }
            }
            break;
         }

         case TaskNotes.RECORD_NUMBER:
         {
            if (m_lastTask != null)
            {
               current = m_lastTask.addTaskNotes (record);
            }
            break;
         }

         case RecurringTask.RECORD_NUMBER:
         {
            if (m_lastTask != null)
            {
               current = m_lastTask.addRecurringTask (record);
            }
            break;
         }

         case ResourceAssignment.RECORD_NUMBER:
         {
            if (m_lastTask != null)
            {
               m_lastResourceAssignment = m_lastTask.addResourceAssignment (record);
               current = m_lastResourceAssignment;
               m_allResourceAssignments.add (m_lastResourceAssignment);
            }
            break;
         }

         case ResourceAssignmentWorkgroupFields.RECORD_NUMBER:
         {
            if (m_lastResourceAssignment != null)
            {
               current = m_lastResourceAssignment.addWorkgroupAssignment (record);
            }
            break;
         }

         case ProjectNames.RECORD_NUMBER:
         {
            if (m_projectNames < MAX_PROJECT_NAMES)
            {
               current = new ProjectNames (this, record);
               m_records.add (current);
               ++m_projectNames;
            }
            break;
         }

         case DdeOleClientLinks.RECORD_NUMBER:
         {
            if (m_ddeOleClientLinks < MAX_DDE_OLE)
            {
               current = new DdeOleClientLinks (this, record);
               m_records.add(current);
               m_ddeOleClientLinks++;
            }
            break;
         }

         case FileCreationRecord.RECORD_NUMBER:
         {
            current = getFileCreationRecord();
            ((FileCreationRecord)current).setValues (record);
            break;
         }

         default:
         {
            throw new MPXException (MPXException.INVALID_RECORD);
         }
      }

      return (current);
   }


   /**
    * This method is provided to allow child tasks that have been created
    * programatically to be added as a record to the main file.
    *
    * @param task task created as a child of another task
    * @throws MPXException thrown when too many tasks are defined
    */
   void addTask (Task task)
      throws MPXException
   {
      if (m_allTasks.size() == MAX_TASKS)
      {
         throw new MPXException (MPXException.MAXIMUM_RECORDS);
      }

      m_records.add (task);
      m_allTasks.add (task);
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
    * Used to set whether the task unique ID field is automatically populated.
    *
    * @param flag true if automatic unique ID required.
    */
   public void setAutoTaskUniqueID (boolean flag)
   {
      m_autoTaskUniqueID = flag;
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
   public Comments addComments()
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
    * Retrieves the currency settings.
    *
    * @return currency settings
    */
   public CurrencySettings getCurrencySettings ()
   {
      return (m_currencySettings);
   }

   /**
    * Retrieves the default settings.
    *
    * @return default settings
    */
   public DefaultSettings getDefaultSettings ()
   {
      return (m_defaultSettings);
   }

   /**
    * Retrieves the date and time settings.
    *
    * @return date and time settings
    */
   public DateTimeSettings getDateTimeSettings ()
   {
      return (m_dateTimeSettings);
   }

   /**
    * This method is used to add a new base calendar to the file.
    *
    * @return new base calendar object
    * @throws MPXException normally thrown on parse errors
    */
   public BaseCalendar addBaseCalendar ()
      throws MPXException
   {
      return ((BaseCalendar)add(BaseCalendar.RECORD_NUMBER));
   }

   /**
    * This is a convenience method used to add a base calendar called
    * "Standard" to the file, and populate it with a default working week
    * and default working hours
    *
    * @return a new default base calendar
    * @throws MPXException normally thrown when a parse error occurs
    */
   public BaseCalendar addDefaultBaseCalendar ()
      throws MPXException
   {
      BaseCalendar calendar = (BaseCalendar)add(BaseCalendar.RECORD_NUMBER);

      calendar.setName("Standard");

      calendar.setSunday(BaseCalendar.NONWORKING);
      calendar.setMonday(BaseCalendar.WORKING);
      calendar.setTuesday(BaseCalendar.WORKING);
      calendar.setWednesday(BaseCalendar.WORKING);
      calendar.setThursday(BaseCalendar.WORKING);
      calendar.setFriday(BaseCalendar.WORKING);
      calendar.setSaturday(BaseCalendar.NONWORKING);

      calendar.addDefaultBaseCalendarHours ();

      return (calendar);
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
    * This method is used to add project names to the file.
    *
    * @return new project names object
    * @throws MPXException normally thrown on parse errors
    */
   public ProjectNames addProjectNames()
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
   public DdeOleClientLinks addDdeOleClientLinks()
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
   public BaseCalendar getBaseCalendar (String calendarName)
   {
      BaseCalendar calendar = null;
      String name;
      Iterator iter = m_baseCalendars.iterator();

      while (iter.hasNext() == true)
      {
         calendar = (BaseCalendar)iter.next();
         name = calendar.getName();

         if (name != null && name.equalsIgnoreCase(calendarName) == true)
         {
            break;
         }

         calendar = null;
      }

      return (calendar);
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
      OutputStreamWriter w = new OutputStreamWriter (new BufferedOutputStream (out));
      Iterator iter = m_records.iterator();
      String str;

      while(iter.hasNext())
      {
         w.write((iter.next()).toString());
      }

      w.flush();

      //
      // Reset the model written flags to allow them to be written again
      //
      m_taskModel.setWritten (false);
      m_resourceModel.setWritten (false);
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
      FileOutputStream fos = new FileOutputStream (out);
      write (fos);
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
      FileOutputStream fos = new FileOutputStream (file);
      write (fos);
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
    * Sets the flag that tells the library whether or not to
    * ignore the thousands separator specified in the currency format.
    *
    * @param ignore boolean flag
    */
   public void setIgnoreCurrencyThousandsSeparator (boolean ignore)
   {
      m_ignoreCurrencyThousandsSeparator = ignore;
   }

   /**
    * Retrieves the flag that tells the library whether or not to
    * ignore the thousands separator specified in the currency format.
    *
    * @return boolean flag
    */
   public boolean getIgnoreCurrencyThousandsSeparator ()
   {
      return (m_ignoreCurrencyThousandsSeparator);
   }

   /**
    * This method is used to retrieve the number of child tasks associated
    * with this parent task. This method is used as part of the process
    * of automatically generating the WBS.
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
      return (getDuration ("Standard", startDate, endDate));
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
      BaseCalendar calendar = getBaseCalendar(calendarName);
      if (calendar == null)
      {
         throw new MPXException (MPXException.CALENDAR_ERROR + ": " + calendarName);
      }

      return (calendar.getDuration (startDate, endDate));
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
      Task result = null;
      Iterator iter = m_allTasks.iterator();
      Task task;
      int taskID;

      while (iter.hasNext() == true)
      {
         task = (Task)iter.next();
         taskID = task.getIDValue();
         if (taskID == id)
         {
            result = task;
            break;
         }
      }

      return (result);
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
      Task result = null;
      Iterator iter = m_allTasks.iterator();
      Task task;
      int taskID;

      while (iter.hasNext() == true)
      {
         task = (Task)iter.next();
         taskID = task.getUniqueIDValue();
         if (taskID == id)
         {
            result = task;
            break;
         }
      }

      return (result);
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
      Resource result = null;
      Iterator iter = m_allResources.iterator();
      Resource resource;
      int resourceID;

      while (iter.hasNext() == true)
      {
         resource = (Resource)iter.next();
         resourceID = resource.getIDValue();
         if (resourceID == id)
         {
            result = resource;
            break;
         }
      }

      return (result);
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
      Resource result = null;
      Iterator iter = m_allResources.iterator();
      Resource resource;
      int resourceID;

      while (iter.hasNext() == true)
      {
         resource = (Resource)iter.next();
         resourceID = resource.getUniqueIDValue();
         if (resourceID == id)
         {
            result = resource;
            break;
         }
      }

      return (result);
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
         Collections.sort (m_allTasks);
         m_childTasks.clear ();

         Task task;
         Task lastTask = null;
         Task parent;
         int level;
         int lastLevel = -1;

         Iterator iter = m_allTasks.iterator();

         while (iter.hasNext() == true)
         {
            task = (Task)iter.next();
            task.clearChildTasks ();
            level = task.getOutlineLevelValue();

            if (lastTask == null)
            {
               m_childTasks.add (task);
            }
            else
            {
               if (level == lastLevel)
               {
                  parent = lastTask.getParentTask();
                  if (parent == null)
                  {
                     m_childTasks.add (task);
                  }
                  else
                  {
                     parent.addChildTask (task);
                  }
               }
               else
               {
                  if (level > lastLevel)
                  {
                     lastTask.addChildTask (task);
                  }
                  else
                  {
                     parent = lastTask.getParentTask();
                     if (parent != null)
                     {
                        parent = parent.getParentTask();
                     }

                     if (parent == null)
                     {
                        m_childTasks.add (task);
                     }
                     else
                     {
                        parent.addChildTask (task);
                     }
                  }
               }
            }

            lastTask = task;
            lastLevel = level;
         }
      }
   }


   /**
    * Constant containing the end of line characters used in MPX files.
    * Note that this constant has package level access only.
    */
   static final String EOL = "\r\n";

   /**
    * Counter used to populate the unique ID field of a task
    */
   private int m_taskUniqueID = 0;

   /**
    * Counter used to populate the ID field of a task
    */
   private int m_taskID = 0;

   /**
    * Counter used to populate the unique ID field of a resource
    */
   private int m_resourceUniqueID = 0;

   /**
    * Counter used to populate the ID field of a resource
    */
   private int m_resourceID = 0;

   /**
    * List to maintain records in the order that they are added.
    */
   private LinkedList m_records = new LinkedList();

   /**
    * This list holds a reference to all resources defined in the
    * MPX file.
    */
   private LinkedList m_allResources = new LinkedList ();

   /**
    * This list holds a reference to all tasks defined in the
    * MPX file.
    */
   private LinkedList m_allTasks = new LinkedList ();

   /**
    * List holding references to the top level tasks
    * as defined by the outline level.
    */
   private LinkedList m_childTasks = new LinkedList ();

   /**
    * This list holds a reference to all resource assignments defined in the
    * MPX file.
    */
   private LinkedList m_allResourceAssignments = new LinkedList ();

   /**
    * List holding references to all base calendars.
    */
   private LinkedList m_baseCalendars = new LinkedList ();

   /**
    * Date formatter.
    */
   private MPXDateFormat m_dateFormat = new MPXDateFormat();

   /**
    * Time formatter.
    */
   private MPXTimeFormat m_timeFormat = new MPXTimeFormat ();

   /**
    * Currency formatter.
    */
   private MPXNumberFormat m_currencyFormat = new MPXNumberFormat ();

   /**
    * File creation record.
    */
   private FileCreationRecord m_fileCreationRecord = new FileCreationRecord (this);

   /**
    * Currency settings record.
    */
   private CurrencySettings m_currencySettings = new CurrencySettings (this);

   /**
    * Default settings record.
    */
   private DefaultSettings m_defaultSettings = new DefaultSettings (this);

   /**
    * Date and time settings record.
    */
   private DateTimeSettings m_dateTimeSettings = new DateTimeSettings (this);

   /**
    * Project header record.
    */
   private ProjectHeader m_projectHeader = new ProjectHeader (this);

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
   private Task m_lastTask = null;

   /**
    * Reference to the last resource added to the file.
    */
   private Resource m_lastResource = null;

   /**
    * Reference to the last resource calendar added to the file.
    */
   private ResourceCalendar m_lastResourceCalendar = null;

   /**
    * Reference to the last resource assignment added to the file.
    */
   private ResourceAssignment m_lastResourceAssignment = null;

   /**
    * Reference to the last base calendar added to the file.
    */
   private BaseCalendar m_lastBaseCalendar = null;

   /**
    * Count of the number of base calendars.
    */
   private int m_baseCalendarCount = 0;

   /**
    * Flag indicating the existence of a resource model record.
    */
   private boolean m_resourceTableDefinition = false;

   /**
    * Count of the number of resources.
    */
   private int m_resourceCount = 0;

   /**
    * Flag indicating the existence of a task model record.
    */
   private boolean m_taskTableDefinition = false;

   /**
    * Count of the number of project names.
    */
   private int m_projectNames = 0;

   /**
    * Count of the number of dde/ole links.
    */
   private int m_ddeOleClientLinks = 0;

   /**
    * Character to be used as delimiter throughout this file.
    */
   private char m_delimiter = ',';

   /**
    * Indicating whether WBS value should be calculated on creation, or will
    * be manually set.
    */
   private boolean m_autoWBS = false;

   /**
    * Indicating whether OutlineLevel value should be calculated on creation,
    * or will be manually set.
    */
   private boolean m_autoOutlineLevel = false;

   /**
    * Indicating whether the unique ID of a task should be
    * calculated on creation, or will be manually set.
    */
   private boolean m_autoTaskUniqueID = false;

   /**
    * Indicating whether the ID of a task should be
    * calculated on creation, or will be manually set.
    */
   private boolean m_autoTaskID = false;

   /**
    * Indicating whether the unique ID of a tresource should be
    * calculated on creation, or will be manually set.
    */
   private boolean m_autoResourceUniqueID = false;

   /**
    * Indicating whether the ID of a tresource should be
    * calculated on creation, or will be manually set.
    */
   private boolean m_autoResourceID = false;

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
    * Flag used to tell the library whether to use thousands separators when
    * reading and writing MPX files, or whether to ignore them. Microsoft
    * Project appears to ignore them when reading and writing files, so the
    * default value is true.
    *
    * @todo move this into the currency settings class
    */
   private boolean m_ignoreCurrencyThousandsSeparator = true;


   /**
    * Constant representing maximum number of BaseCalendars per MPX file.
    */
   private static final int MAX_BASE_CALENDARS = 250;

   /**
    * Constant representing maximum number of Tasks per MPX file.
    */
   private static final int MAX_TASKS = 9999;

   /**
    * Constant representing maximum number of ProjectNames per MPX file.
    */
   private static final int MAX_PROJECT_NAMES = 500;

   /**
    * Constant representing maximum number of DdeOleClientLinks per MPX file.
    */
   private static final int MAX_DDE_OLE = 500;

   /**
    * Constant representing maximum number of Resources per MPX file.
    */
   private static final int MAX_RESOURCES = 9999;
}


