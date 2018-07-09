/*
 * file:       ConceptDrawProjectReader.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2018
 * date:       9 July 2018
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

package net.sf.mpxj.conceptdraw;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.UnmarshallerHandler;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;

import net.sf.mpxj.DateRange;
import net.sf.mpxj.EventManager;
import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectCalendarException;
import net.sf.mpxj.ProjectCalendarHours;
import net.sf.mpxj.ProjectConfig;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ProjectProperties;
import net.sf.mpxj.Rate;
import net.sf.mpxj.Resource;
import net.sf.mpxj.Task;
import net.sf.mpxj.conceptdraw.schema.Document;
import net.sf.mpxj.conceptdraw.schema.Document.Calendars.Calendar;
import net.sf.mpxj.conceptdraw.schema.Document.Calendars.Calendar.ExceptedDays.ExceptedDay;
import net.sf.mpxj.conceptdraw.schema.Document.Calendars.Calendar.WeekDays.WeekDay;
import net.sf.mpxj.conceptdraw.schema.Document.Projects.Project;
import net.sf.mpxj.conceptdraw.schema.Document.WorkspaceProperties;
import net.sf.mpxj.listener.ProjectListener;
import net.sf.mpxj.reader.AbstractProjectReader;

/**
 * This class creates a new ProjectFile instance by reading a ConceptDraw Project file.
 */
public final class ConceptDrawProjectReader extends AbstractProjectReader
{
   /**
    * {@inheritDoc}
    */
   @Override public void addProjectListener(ProjectListener listener)
   {
      if (m_projectListeners == null)
      {
         m_projectListeners = new LinkedList<ProjectListener>();
      }
      m_projectListeners.add(listener);
   }

   /**
    * {@inheritDoc}
    */
   @Override public ProjectFile read(InputStream stream) throws MPXJException
   {
      try
      {
         m_projectFile = new ProjectFile();
         m_eventManager = m_projectFile.getEventManager();
         m_calendarMap = new HashMap<Integer, ProjectCalendar>();

         ProjectConfig config = m_projectFile.getProjectConfig();
         config.setAutoResourceUniqueID(false);
         config.setAutoTaskUniqueID(false);
         config.setAutoOutlineLevel(true);
         config.setAutoOutlineNumber(true);
         config.setAutoWBS(true);

         m_projectFile.getProjectProperties().setFileApplication("ConceptDraw PROJECT");
         m_projectFile.getProjectProperties().setFileType("CDP");

         m_eventManager.addProjectListeners(m_projectListeners);

         SAXParserFactory factory = SAXParserFactory.newInstance();
         SAXParser saxParser = factory.newSAXParser();
         XMLReader xmlReader = saxParser.getXMLReader();

         if (CONTEXT == null)
         {
            throw CONTEXT_EXCEPTION;
         }

         Unmarshaller unmarshaller = CONTEXT.createUnmarshaller();

         XMLFilter filter = new NamespaceFilter();
         filter.setParent(xmlReader);
         UnmarshallerHandler unmarshallerHandler = unmarshaller.getUnmarshallerHandler();
         filter.setContentHandler(unmarshallerHandler);
         filter.parse(new InputSource(new InputStreamReader(stream)));
         Document cdp = (Document) unmarshallerHandler.getResult();

         readProjectProperties(cdp);
         readCalendars(cdp);
         readResources(cdp);
         readTasks(cdp);
         readRelationships(cdp);
         readResourceAssignments(cdp);

         //
         // Ensure that the unique ID counters are correct
         //
         config.updateUniqueCounters();

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

      catch (IOException ex)
      {
         throw new MPXJException("Failed to parse file", ex);
      }

      finally
      {
         m_projectFile = null;
         m_eventManager = null;
         m_projectListeners = null;
         m_calendarMap = null;
      }
   }

   /**
    * This method extracts project properties from a ConceptDraw PROJECT file.
    *
    * @param cdp ConceptDraw PROJECT file
    */
   private void readProjectProperties(Document cdp)
   {
      WorkspaceProperties props = cdp.getWorkspaceProperties();
      ProjectProperties mpxjProps = m_projectFile.getProjectProperties();
      mpxjProps.setSymbolPosition(props.getCurrencyPosition());
      mpxjProps.setCurrencyDigits(props.getCurrencyDigits());
      mpxjProps.setCurrencySymbol(props.getCurrencySymbol());
      mpxjProps.setDaysPerMonth(props.getDaysPerMonth());
      mpxjProps.setMinutesPerDay(props.getHoursPerDay());
      mpxjProps.setMinutesPerWeek(props.getHoursPerWeek());
   }

   /**
   * This method extracts calendar data from a ConceptDraw PROJECT file.
   *
   * @param cdp ConceptDraw PROJECT file
   */
   private void readCalendars(Document cdp)
   {
      for (Calendar calendar : cdp.getCalendars().getCalendar())
      {
         readCalendar(calendar);
      }

      for (Calendar calendar : cdp.getCalendars().getCalendar())
      {
         ProjectCalendar child = m_calendarMap.get(calendar.getID());
         ProjectCalendar parent = m_calendarMap.get(calendar.getBaseCalendarID());
         if (parent == null)
         {
            m_projectFile.setDefaultCalendar(child);
         }
         else
         {
            child.setParent(parent);
         }
      }
   }

   private void readCalendar(Calendar calendar)
   {
      ProjectCalendar mpxjCalendar = m_projectFile.addCalendar();
      mpxjCalendar.setName(calendar.getName());
      m_calendarMap.put(calendar.getID(), mpxjCalendar);

      for (WeekDay day : calendar.getWeekDays().getWeekDay())
      {
         readWeekDay(mpxjCalendar, day);
      }

      for (ExceptedDay day : calendar.getExceptedDays().getExceptedDay())
      {
         readExceptionDay(mpxjCalendar, day);
      }
   }

   private void readWeekDay(ProjectCalendar mpxjCalendar, WeekDay day)
   {
      if (day.isIsDayWorking())
      {
         ProjectCalendarHours hours = mpxjCalendar.addCalendarHours(day.getDay());
         for (Document.Calendars.Calendar.WeekDays.WeekDay.TimePeriods.TimePeriod period : day.getTimePeriods().getTimePeriod())
         {
            hours.addRange(new DateRange(period.getFrom(), period.getTo()));
         }
      }
   }

   private void readExceptionDay(ProjectCalendar mpxjCalendar, ExceptedDay day)
   {
      ProjectCalendarException mpxjException = mpxjCalendar.addCalendarException(day.getDate(), day.getDate());
      if (day.isIsDayWorking())
      {
         for (Document.Calendars.Calendar.ExceptedDays.ExceptedDay.TimePeriods.TimePeriod period : day.getTimePeriods().getTimePeriod())
         {
            mpxjException.addRange(new DateRange(period.getFrom(), period.getTo()));
         }
      }
   }

   /**
    * This method extracts resource data from a ConceptDraw PROJECT file.
    *
    * @param cdp ConceptDraw PROJECT file
    */
   private void readResources(Document cdp)
   {
      for (Document.Resources.Resource resource : cdp.getResources().getResource())
      {
         readResource(resource);
      }
   }

   private void readResource(Document.Resources.Resource resource)
   {
      Resource mpxjResource = m_projectFile.addResource();
      mpxjResource.setName(resource.getName());
      mpxjResource.setResourceCalendar(m_calendarMap.get(resource.getCalendarID()));
      mpxjResource.setStandardRate(new Rate(resource.getCost(), resource.getCostTimeUnit()));
      mpxjResource.setEmailAddress(resource.getEMail());
      mpxjResource.setGroup(resource.getGroup());
      //resource.getHyperlinks()
      mpxjResource.setUniqueID(resource.getID());
      //resource.getMarkerID()
      mpxjResource.setNotes(resource.getNote());
      //resource.getOutlineNumber()
      //resource.getStyleProject()
      mpxjResource.setType(resource.getSubType() == null ? resource.getType() : resource.getSubType());
   }

   /**
    * Read the top level tasks from a ConceptDraw PROJECT file.
    *
    * @param cdp ConceptDraw PROJECT file
    */
   private void readTasks(Document cdp)
   {
      for (Project project : cdp.getProjects().getProject())
      {
         readProject(project);
      }
   }

   private void readProject(Project project)
   {
      Task mpxjTask = m_projectFile.addTask();
      //project.getAuthor()
      mpxjTask.setBaselineCost(project.getBaselineCost());
      mpxjTask.setBaselineFinish(project.getBaselineFinishDate());
      mpxjTask.setBaselineStart(project.getBaselineStartDate());
      //project.getBudget();
      //project.getCompany()
      mpxjTask.setFinish(project.getFinishDate());
      //project.getGoal()
      //project.getHyperlinks()
      mpxjTask.setUniqueID(project.getID()); // TODO: handle clashes
      //project.getMarkerID()
      mpxjTask.setName(project.getName());
      mpxjTask.setNotes(project.getNote());
      //project.getOutlineNumber()
      mpxjTask.setPriority(project.getPriority());
      //      project.getSite()
      mpxjTask.setStart(project.getStartDate());
      //      project.getStyleProject()
      //      project.getTask()
      //      project.getTimeScale()
      //      project.getViewProperties()

      for (Document.Projects.Project.Task task : project.getTask())
      {
         readTask(mpxjTask.addTask(), task);
      }
   }

   private void readTask(Task mpxjTask, Document.Projects.Project.Task task)
   {
      //      task.getActualCost()
      //      task.getActualDuration()
   }

   /**
    * Read all task relationships from a ConceptDraw PROJECT file.
    *
    * @param cdp ConceptDraw PROJECT file
    */
   private void readRelationships(Document cdp)
   {
   }

   /**
    * Read all resource assignments from a ConceptDraw PROJECT file.
    *
    * @param cdp ConceptDraw PROJECT file
    */
   private void readResourceAssignments(Document cdp)
   {
   }

   private ProjectFile m_projectFile;
   private EventManager m_eventManager;
   private List<ProjectListener> m_projectListeners;
   private Map<Integer, ProjectCalendar> m_calendarMap;

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
         CONTEXT = JAXBContext.newInstance("net.sf.mpxj.conceptdraw.schema", ConceptDrawProjectReader.class.getClassLoader());
      }

      catch (JAXBException ex)
      {
         CONTEXT_EXCEPTION = ex;
         CONTEXT = null;
      }
   }
}
