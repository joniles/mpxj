/*
 * file:       GanttDesignerReader.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2019
 * date:       10 February 2019
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

package net.sf.mpxj.ganttdesigner;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import net.sf.mpxj.ChildTaskContainer;
import net.sf.mpxj.Day;
import net.sf.mpxj.Duration;
import net.sf.mpxj.EventManager;
import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectCalendarException;
import net.sf.mpxj.ProjectCalendarHours;
import net.sf.mpxj.ProjectCalendarWeek;
import net.sf.mpxj.ProjectConfig;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ProjectProperties;
import net.sf.mpxj.Relation;
import net.sf.mpxj.RelationType;
import net.sf.mpxj.Task;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.common.UnmarshalHelper;
import net.sf.mpxj.ganttdesigner.schema.Gantt;
import net.sf.mpxj.ganttdesigner.schema.GanttDesignerRemark;
import net.sf.mpxj.reader.AbstractProjectStreamReader;

/**
 * This class creates a new ProjectFile instance by reading a GanttDesigner file.
 */
public final class GanttDesignerReader extends AbstractProjectStreamReader
{
   /**
    * {@inheritDoc}
    */
   @Override public ProjectFile read(InputStream stream) throws MPXJException
   {
      try
      {
         if (CONTEXT == null)
         {
            throw CONTEXT_EXCEPTION;
         }

         m_projectFile = new ProjectFile();
         m_eventManager = m_projectFile.getEventManager();
         m_taskMap = new HashMap<>();

         ProjectConfig config = m_projectFile.getProjectConfig();
         config.setAutoWBS(false);

         m_projectFile.getProjectProperties().setFileApplication("GanttDesigner");
         m_projectFile.getProjectProperties().setFileType("GNT");

         addListenersToProject(m_projectFile);

         Gantt gantt = (Gantt) UnmarshalHelper.unmarshal(CONTEXT, stream);

         readProjectProperties(gantt);
         readCalendar(gantt);
         readTasks(gantt);

         return m_projectFile;
      }

      catch (ParserConfigurationException ex)
      {
         throw new MPXJException("Failed to parse file", ex);
      }

      catch (JAXBException ex)
      {
         throw new MPXJException("Failed to parse file", ex);
      }

      catch (SAXException ex)
      {
         throw new MPXJException("Failed to parse file", ex);
      }

      finally
      {
         m_projectFile = null;
         m_eventManager = null;
         m_taskMap = null;
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override public List<ProjectFile> readAll(InputStream inputStream) throws MPXJException
   {
      return Arrays.asList(read(inputStream));
   }

   /**
    * Read project properties.
    *
    * @param gantt Gantt Designer file
    */
   private void readProjectProperties(Gantt gantt)
   {
      Gantt.File file = gantt.getFile();
      ProjectProperties props = m_projectFile.getProjectProperties();
      props.setLastSaved(file.getSaved());
      props.setCreationDate(file.getCreated());
      props.setName(file.getName());
   }

   /**
    * Read the calendar data from a Gantt Designer file.
    *
    * @param gantt Gantt Designer file.
    */
   private void readCalendar(Gantt gantt)
   {
      Gantt.Calendar ganttCalendar = gantt.getCalendar();
      m_projectFile.getProjectProperties().setWeekStartDay(ganttCalendar.getWeekStart());

      ProjectCalendar calendar = m_projectFile.addCalendar();
      calendar.setName("Standard");
      m_projectFile.setDefaultCalendar(calendar);

      String workingDays = ganttCalendar.getWorkDays();
      calendar.setWorkingDay(Day.SUNDAY, workingDays.charAt(0) == '1');
      calendar.setWorkingDay(Day.MONDAY, workingDays.charAt(1) == '1');
      calendar.setWorkingDay(Day.TUESDAY, workingDays.charAt(2) == '1');
      calendar.setWorkingDay(Day.WEDNESDAY, workingDays.charAt(3) == '1');
      calendar.setWorkingDay(Day.THURSDAY, workingDays.charAt(4) == '1');
      calendar.setWorkingDay(Day.FRIDAY, workingDays.charAt(5) == '1');
      calendar.setWorkingDay(Day.SATURDAY, workingDays.charAt(6) == '1');

      for (int i = 1; i <= 7; i++)
      {
         Day day = Day.getInstance(i);
         ProjectCalendarHours hours = calendar.addCalendarHours(day);
         if (calendar.isWorkingDay(day))
         {
            hours.addRange(ProjectCalendarWeek.DEFAULT_WORKING_MORNING);
            hours.addRange(ProjectCalendarWeek.DEFAULT_WORKING_AFTERNOON);
         }
      }

      for (Gantt.Holidays.Holiday holiday : gantt.getHolidays().getHoliday())
      {
         ProjectCalendarException exception = calendar.addCalendarException(holiday.getDate(), holiday.getDate());
         exception.setName(holiday.getContent());
      }

      m_eventManager.fireCalendarReadEvent(calendar);
   }

   /**
    * Read task data from a Gantt Designer file.
    *
    * @param gantt Gantt Designer file
    */
   private void readTasks(Gantt gantt)
   {
      processTasks(gantt);
      processPredecessors(gantt);
      processRemarks(gantt);
   }

   /**
    * Read task data from a Gantt Designer file.
    *
    * @param gantt Gantt Designer file
    */
   private void processTasks(Gantt gantt)
   {
      ProjectCalendar calendar = m_projectFile.getDefaultCalendar();

      for (Gantt.Tasks.Task ganttTask : gantt.getTasks().getTask())
      {
         String wbs = ganttTask.getID();
         ChildTaskContainer parentTask = getParentTask(wbs);

         Task task = parentTask.addTask();
         //ganttTask.getB() // bar type
         //ganttTask.getBC() // bar color
         task.setCost(ganttTask.getC());
         task.setName(ganttTask.getContent());
         task.setDuration(ganttTask.getD());
         task.setDeadline(ganttTask.getDL());
         //ganttTask.getH() // height
         //ganttTask.getIn(); // indent
         task.setWBS(wbs);
         task.setPercentageComplete(ganttTask.getPC());
         task.setStart(ganttTask.getS());
         //ganttTask.getU(); // Unknown
         //ganttTask.getVA(); // Valign

         task.setFinish(calendar.getDate(task.getStart(), task.getDuration(), false));
         m_taskMap.put(wbs, task);

         // We don't have early/late start/finish.
         // Set attributes here to avoid trying to calculate them.
         task.setStartSlack(Duration.getInstance(0, TimeUnit.DAYS));
         task.setFinishSlack(Duration.getInstance(0, TimeUnit.DAYS));
         task.setCritical(false);

         m_eventManager.fireTaskReadEvent(task);
      }
   }

   /**
    * Read predecessors from a Gantt Designer file.
    *
    * @param gantt Gantt Designer file
    */
   private void processPredecessors(Gantt gantt)
   {
      for (Gantt.Tasks.Task ganttTask : gantt.getTasks().getTask())
      {
         String predecessors = ganttTask.getP();
         if (predecessors != null && !predecessors.isEmpty())
         {
            String wbs = ganttTask.getID();
            Task task = m_taskMap.get(wbs);
            for (String predecessor : predecessors.split(";"))
            {
               Task predecessorTask = m_projectFile.getTaskByID(Integer.valueOf(predecessor));
               Relation relation = task.addPredecessor(predecessorTask, RelationType.FINISH_START, ganttTask.getL());
               m_eventManager.fireRelationReadEvent(relation);
            }
         }
      }
   }

   /**
    * Read remarks from a Gantt Designer file.
    *
    * @param gantt Gantt Designer file
    */
   private void processRemarks(Gantt gantt)
   {
      processRemarks(gantt.getRemarks());
      processRemarks(gantt.getRemarks1());
      processRemarks(gantt.getRemarks2());
      processRemarks(gantt.getRemarks3());
      processRemarks(gantt.getRemarks4());
   }

   /**
    * Read an individual remark type from a Gantt Designer file.
    *
    * @param remark remark type
    */
   private void processRemarks(GanttDesignerRemark remark)
   {
      for (GanttDesignerRemark.Task remarkTask : remark.getTask())
      {
         Integer id = remarkTask.getRow();
         Task task = m_projectFile.getTaskByID(id);
         String notes = task.getNotes();
         if (notes.isEmpty())
         {
            notes = remarkTask.getContent();
         }
         else
         {
            notes = notes + '\n' + remarkTask.getContent();
         }
         task.setNotes(notes);
      }
   }

   /**
    * Extract the parent WBS from a WBS.
    *
    * @param wbs current WBS
    * @return parent WBS
    */
   private String getParentWBS(String wbs)
   {
      String result;
      int index = wbs.lastIndexOf('.');
      if (index == -1)
      {
         result = null;
      }
      else
      {
         result = wbs.substring(0, index);
      }
      return result;
   }

   /**
    * Retrieve the parent task based on its WBS.
    *
    * @param wbs parent WBS
    * @return parent task
    */
   private ChildTaskContainer getParentTask(String wbs)
   {
      ChildTaskContainer result;
      String parentWbs = getParentWBS(wbs);
      if (parentWbs == null)
      {
         result = m_projectFile;
      }
      else
      {
         result = m_taskMap.get(parentWbs);
      }
      return result;
   }

   private ProjectFile m_projectFile;
   private EventManager m_eventManager;
   Map<String, Task> m_taskMap;

   /**
    * Cached context to minimise construction cost.
    */
   private static JAXBContext CONTEXT;

   /**
    * Note any error occurring during context construction.
    */
   private static JAXBException CONTEXT_EXCEPTION;

   static
   {
      try
      {
         //
         // JAXB RI property to speed up construction
         //
         System.setProperty("com.sun.xml.bind.v2.runtime.JAXBContextImpl.fastBoot", "true");

         //
         // Construct the context
         //
         CONTEXT = JAXBContext.newInstance("net.sf.mpxj.ganttdesigner.schema", GanttDesignerReader.class.getClassLoader());
      }

      catch (JAXBException ex)
      {
         CONTEXT_EXCEPTION = ex;
         CONTEXT = null;
      }
   }
}
