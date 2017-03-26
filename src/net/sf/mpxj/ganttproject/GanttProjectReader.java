/*
 * file:       GanttProjectReader.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2017
 * date:       22 March 2017
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

package net.sf.mpxj.ganttproject;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;

import net.sf.mpxj.ChildTaskContainer;
import net.sf.mpxj.ConstraintType;
import net.sf.mpxj.Duration;
import net.sf.mpxj.EventManager;
import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectConfig;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ProjectProperties;
import net.sf.mpxj.Rate;
import net.sf.mpxj.Resource;
import net.sf.mpxj.Task;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.common.NumberHelper;
import net.sf.mpxj.ganttproject.schema.Project;
import net.sf.mpxj.listener.ProjectListener;
import net.sf.mpxj.reader.AbstractProjectReader;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * This class creates a new ProjectFile instance by reading a GanttProject file.
 */
public final class GanttProjectReader extends AbstractProjectReader
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

         ProjectConfig config = m_projectFile.getProjectConfig();
         config.setAutoResourceUniqueID(true);
         config.setAutoOutlineLevel(false);
         config.setAutoOutlineNumber(false);
         config.setAutoWBS(false);

         m_eventManager.addProjectListeners(m_projectListeners);

         SAXParserFactory factory = SAXParserFactory.newInstance();
         SAXParser saxParser = factory.newSAXParser();
         XMLReader xmlReader = saxParser.getXMLReader();
         SAXSource doc = new SAXSource(xmlReader, new InputSource(stream));

         if (CONTEXT == null)
         {
            throw CONTEXT_EXCEPTION;
         }

         Unmarshaller unmarshaller = CONTEXT.createUnmarshaller();

         Project ganttProject = (Project) unmarshaller.unmarshal(doc);

         readProjectProperties(ganttProject);
         readCalendars(ganttProject);
         readTasks(ganttProject, m_projectFile);
         readResources(ganttProject);
         readRelationships(ganttProject);

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

      finally
      {
         m_projectFile = null;
      }
   }

   /**
    * This method extracts project properties from a GanttProject file.
    *
    * @param ganttProject GanttProject file
    */
   private void readProjectProperties(Project ganttProject)
   {
      ProjectProperties mpxjProperties = m_projectFile.getProjectProperties();
      mpxjProperties.setName(ganttProject.getName());
      mpxjProperties.setCompany(ganttProject.getCompany());
      mpxjProperties.setDefaultDurationUnits(TimeUnit.DAYS);
   }

   /**
    * This method extracts calendar data from a GanttProject file.
    *
    * @param ganttProject Root node of the GanttProject file
    */
   private void readCalendars(Project ganttProject)
   {
      //      Calendars calendars = phoenixProject.getCalendars();
      //      if (calendars != null)
      //      {
      //         for (Calendar calendar : calendars.getCalendar())
      //         {
      //            readCalendar(calendar);
      //         }
      //
      //         ProjectCalendar defaultCalendar = m_projectFile.getCalendarByName(phoenixProject.getDefaultCalendar());
      //         if (defaultCalendar != null)
      //         {
      //            m_projectFile.getProjectProperties().setDefaultCalendarName(defaultCalendar.getName());
      //         }
      //      }
   }

   /**
    * This method extracts data for a single calendar from a Phoenix file.
    *
    * @param calendar calendar data
    */
   //   private void readCalendar(Calendar calendar)
   //   {
   //      // Create the calendar
   //      ProjectCalendar mpxjCalendar = m_projectFile.addCalendar();
   //      mpxjCalendar.setName(calendar.getName());
   //
   //      // Default all days to working
   //      for (Day day : Day.values())
   //      {
   //         mpxjCalendar.setWorkingDay(day, true);
   //      }
   //
   //      // Mark non-working days
   //      List<NonWork> nonWorkingDays = calendar.getNonWork();
   //      for (NonWork nonWorkingDay : nonWorkingDays)
   //      {
   //         // TODO: handle recurring exceptions
   //         if (nonWorkingDay.getType().equals("internal_weekly"))
   //         {
   //            mpxjCalendar.setWorkingDay(nonWorkingDay.getWeekday(), false);
   //         }
   //      }
   //
   //      // Add default working hours for working days
   //      for (Day day : Day.values())
   //      {
   //         if (mpxjCalendar.isWorkingDay(day))
   //         {
   //            ProjectCalendarHours hours = mpxjCalendar.addCalendarHours(day);
   //            hours.addRange(ProjectCalendarWeek.DEFAULT_WORKING_MORNING);
   //            hours.addRange(ProjectCalendarWeek.DEFAULT_WORKING_AFTERNOON);
   //         }
   //      }
   //   }

   /**
    * This method extracts resource data from a GanttProject file.
    *
    * @param ganttProject parent node for resources
    */
   private void readResources(Project ganttProject)
   {
      //      Resources resources = phoenixProject.getResources();
      //      if (resources != null)
      //      {
      //         for (net.sf.mpxj.phoenix.schema.Project.Storepoints.Storepoint.Resources.Resource res : resources.getResource())
      //         {
      //            Resource resource = readResource(res);
      //            readAssignments(resource, res);
      //         }
      //      }
   }

   /**
    * This method extracts data for a single resource from a Phoenix file.
    *
    * @param phoenixResource resource data
    * @return Resource instance
    */
   private Resource readResource(net.sf.mpxj.phoenix.schema.Project.Storepoints.Storepoint.Resources.Resource phoenixResource)
   {
      Resource mpxjResource = m_projectFile.addResource();

      // phoenixResource.getMaximum()
      mpxjResource.setCostPerUse(phoenixResource.getMonetarycostperuse());
      mpxjResource.setStandardRate(new Rate(phoenixResource.getMonetaryrate(), phoenixResource.getMonetarybase()));
      mpxjResource.setStandardRateUnits(phoenixResource.getMonetarybase());
      mpxjResource.setName(phoenixResource.getName());
      mpxjResource.setType(phoenixResource.getType());
      mpxjResource.setMaterialLabel(phoenixResource.getUnitslabel());
      //phoenixResource.getUnitsperbase()
      mpxjResource.setGUID(phoenixResource.getUuid());

      m_eventManager.fireResourceReadEvent(mpxjResource);

      return mpxjResource;
   }

   private void readTasks(Project ganttProject, ChildTaskContainer parent)
   {
      for (net.sf.mpxj.ganttproject.schema.Task task : ganttProject.getTasks().getTask())
      {
         Task mpxjTask = parent.addTask();
         mpxjTask.setPercentageComplete(task.getComplete());
         mpxjTask.setDuration(Duration.getInstance(NumberHelper.getDouble(task.getDuration()), TimeUnit.DAYS));
         mpxjTask.setUniqueID(Integer.valueOf(NumberHelper.getInt(task.getId()) + 1));
         mpxjTask.setName(task.getName());
         mpxjTask.setStart(task.getStart());
         mpxjTask.setConstraintDate(task.getThirdDate());
         if (mpxjTask.getConstraintDate() != null)
         {
            // Can you change this in GanttProject?
            mpxjTask.setConstraintType(ConstraintType.START_NO_EARLIER_THAN);
         }
      }
   }

   /**
    * Reads Phoenix resource assignments.
    *
    * @param mpxjResource MPXJ resource
    * @param res Phoenix resource
    */
   //   private void readAssignments(Resource mpxjResource, net.sf.mpxj.phoenix.schema.Project.Storepoints.Storepoint.Resources.Resource res)
   //   {
   //      for (Assignment assignment : res.getAssignment())
   //      {
   //         readAssignment(mpxjResource, assignment);
   //      }
   //   }

   /**
    * Read a single resource assignment.
    *
    * @param resource MPXJ resource
    * @param assignment Phoenix assignment
    */
   //   private void readAssignment(Resource resource, Assignment assignment)
   //   {
   //      Task task = m_activityMap.get(assignment.getActivity());
   //      if (task != null)
   //      {
   //         task.addResourceAssignment(resource);
   //      }
   //   }

   /**
    * Read task relationships from a Phoenix file.
    *
    * @param ganttProject Phoenix project data
    */
   private void readRelationships(Project ganttProject)
   {
      //      for (Relationship relation : phoenixProject.getRelationships().getRelationship())
      //      {
      //         readRelation(relation);
      //      }
   }

   /**
    * Read an individual Phoenix task relationship.
    *
    * @param relation Phoenix task relationship
    */
   //   private void readRelation(Relationship relation)
   //   {
   //      Task predecessor = m_projectFile.getTaskByUniqueID(relation.getPredecessor());
   //      Task successor = m_projectFile.getTaskByUniqueID(relation.getSuccessor());
   //      if (predecessor != null && successor != null)
   //      {
   //         Duration lag = relation.getLag();
   //         RelationType type = relation.getType();
   //         successor.addPredecessor(predecessor, type, lag);
   //      }
   //   }

   private ProjectFile m_projectFile;
   private EventManager m_eventManager;
   private List<ProjectListener> m_projectListeners;

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
         CONTEXT = JAXBContext.newInstance("net.sf.mpxj.ganttproject.schema", GanttProjectReader.class.getClassLoader());
      }

      catch (JAXBException ex)
      {
         CONTEXT_EXCEPTION = ex;
         CONTEXT = null;
      }
   }
}
