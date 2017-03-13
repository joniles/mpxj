/*
 * file:       PhoenixReader.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2015
 * date:       28 November 2015
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

package net.sf.mpxj.phoenix;

import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;

import net.sf.mpxj.ChildTaskContainer;
import net.sf.mpxj.Day;
import net.sf.mpxj.Duration;
import net.sf.mpxj.EventManager;
import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectCalendarHours;
import net.sf.mpxj.ProjectCalendarWeek;
import net.sf.mpxj.ProjectConfig;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ProjectProperties;
import net.sf.mpxj.Rate;
import net.sf.mpxj.RelationType;
import net.sf.mpxj.Resource;
import net.sf.mpxj.Task;
import net.sf.mpxj.TaskField;
import net.sf.mpxj.listener.ProjectListener;
import net.sf.mpxj.phoenix.schema.Project;
import net.sf.mpxj.phoenix.schema.Project.Settings;
import net.sf.mpxj.phoenix.schema.Project.Storepoints.Storepoint;
import net.sf.mpxj.phoenix.schema.Project.Storepoints.Storepoint.Activities.Activity;
import net.sf.mpxj.phoenix.schema.Project.Storepoints.Storepoint.Activities.Activity.CodeAssignment;
import net.sf.mpxj.phoenix.schema.Project.Storepoints.Storepoint.ActivityCodes.Code;
import net.sf.mpxj.phoenix.schema.Project.Storepoints.Storepoint.ActivityCodes.Code.Value;
import net.sf.mpxj.phoenix.schema.Project.Storepoints.Storepoint.Calendars;
import net.sf.mpxj.phoenix.schema.Project.Storepoints.Storepoint.Calendars.Calendar;
import net.sf.mpxj.phoenix.schema.Project.Storepoints.Storepoint.Calendars.Calendar.NonWork;
import net.sf.mpxj.phoenix.schema.Project.Storepoints.Storepoint.Relationships.Relationship;
import net.sf.mpxj.phoenix.schema.Project.Storepoints.Storepoint.Resources;
import net.sf.mpxj.phoenix.schema.Project.Storepoints.Storepoint.Resources.Resource.Assignment;
import net.sf.mpxj.reader.AbstractProjectReader;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * This class creates a new ProjectFile instance by reading a Phoenix Project Manager file.
 */
public final class PhoenixReader extends AbstractProjectReader
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
         m_phaseNameMap = new HashMap<String, Task>();
         m_phaseUuidMap = new HashMap<UUID, Task>();
         m_activityMap = new HashMap<String, Task>();
         m_eventManager = m_projectFile.getEventManager();

         ProjectConfig config = m_projectFile.getProjectConfig();
         config.setAutoResourceUniqueID(true);
         config.setAutoOutlineLevel(false);
         config.setAutoOutlineNumber(false);
         config.setAutoWBS(false);

         // Equivalent to Primavera's Activity ID
         m_projectFile.getCustomFields().getCustomField(TaskField.TEXT1).setAlias("Code");

         m_eventManager.addProjectListeners(m_projectListeners);

         SAXParserFactory factory = SAXParserFactory.newInstance();
         SAXParser saxParser = factory.newSAXParser();
         XMLReader xmlReader = saxParser.getXMLReader();
         SAXSource doc = new SAXSource(xmlReader, new InputSource(new SkipNulInputStream(stream)));

         if (CONTEXT == null)
         {
            throw CONTEXT_EXCEPTION;
         }

         Unmarshaller unmarshaller = CONTEXT.createUnmarshaller();

         Project phoenixProject = (Project) unmarshaller.unmarshal(doc);
         Storepoint storepoint = phoenixProject.getStorepoints().getStorepoint();

         readProjectProperties(phoenixProject.getSettings());
         readCalendars(storepoint);
         readTasks(storepoint);
         readResources(storepoint);
         readRelationships(storepoint);

         //
         // Ensure that the unique ID counters are correct
         //
         config.updateUniqueCounters();

         return (m_projectFile);
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
         m_phaseNameMap = null;
         m_phaseUuidMap = null;
         m_activityMap = null;
      }
   }

   /**
    * This method extracts project properties from a Phoenix file.
    *
    * @param phoenixSettings Phoenix settings
    */
   private void readProjectProperties(Settings phoenixSettings)
   {
      ProjectProperties mpxjProperties = m_projectFile.getProjectProperties();
      mpxjProperties.setName(phoenixSettings.getTitle());
      mpxjProperties.setDefaultDurationUnits(phoenixSettings.getBaseunit());
   }

   /**
    * This method extracts calendar data from a Phoenix file.
    *
    * @param phoenixProject Root node of the Phoenix file
    */
   private void readCalendars(Storepoint phoenixProject)
   {
      Calendars calendars = phoenixProject.getCalendars();
      if (calendars != null)
      {
         for (Calendar calendar : calendars.getCalendar())
         {
            readCalendar(calendar);
         }

         ProjectCalendar defaultCalendar = m_projectFile.getCalendarByName(phoenixProject.getDefaultCalendar());
         if (defaultCalendar != null)
         {
            m_projectFile.getProjectProperties().setDefaultCalendarName(defaultCalendar.getName());
         }
      }
   }

   /**
    * This method extracts data for a single calendar from a Phoenix file.
    *
    * @param calendar calendar data
    */
   private void readCalendar(Calendar calendar)
   {
      // Create the calendar
      ProjectCalendar mpxjCalendar = m_projectFile.addCalendar();
      mpxjCalendar.setName(calendar.getName());

      // Default all days to working
      for (Day day : Day.values())
      {
         mpxjCalendar.setWorkingDay(day, true);
      }

      // Mark non-working days
      List<NonWork> nonWorkingDays = calendar.getNonWork();
      for (NonWork nonWorkingDay : nonWorkingDays)
      {
         // TODO: handle recurring exceptions
         if (nonWorkingDay.getType().equals("internal_weekly"))
         {
            mpxjCalendar.setWorkingDay(nonWorkingDay.getWeekday(), false);
         }
      }

      // Add default working hours for working days
      for (Day day : Day.values())
      {
         if (mpxjCalendar.isWorkingDay(day))
         {
            ProjectCalendarHours hours = mpxjCalendar.addCalendarHours(day);
            hours.addRange(ProjectCalendarWeek.DEFAULT_WORKING_MORNING);
            hours.addRange(ProjectCalendarWeek.DEFAULT_WORKING_AFTERNOON);
         }
      }
   }

   /**
    * This method extracts resource data from a Phoenix file.
    *
    * @param phoenixProject parent node for resources
    */
   private void readResources(Storepoint phoenixProject)
   {
      Resources resources = phoenixProject.getResources();
      if (resources != null)
      {
         for (net.sf.mpxj.phoenix.schema.Project.Storepoints.Storepoint.Resources.Resource res : resources.getResource())
         {
            Resource resource = readResource(res);
            readAssignments(resource, res);
         }
      }
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

   /**
    * Read phases and activities from the Phoenix file to create the task hierarchy.
    *
    * @param phoenixProject project data
    */
   private void readTasks(Storepoint phoenixProject)
   {
      processPhases(phoenixProject);
      processActivities(phoenixProject);
   }

   /**
    * Read phases from the Phoenix file.
    *
    * @param phoenixProject project data
    */
   private void processPhases(Storepoint phoenixProject)
   {
      Code phase = null;

      for (Code code : phoenixProject.getActivityCodes().getCode())
      {
         if (code.getName().equals("Phase"))
         {
            phase = code;
            break;
         }
      }

      if (phase != null)
      {
         for (Value value : phase.getValue())
         {
            String name = value.getName();
            UUID uuid = value.getUuid();

            // Phases in compressed ppx files don't appear to have uuids
            // The name is used as the unique identifier.
            // Construct a UUID from the name
            if (uuid == null)
            {
               uuid = UUID.nameUUIDFromBytes(name.getBytes());
            }

            Task task = m_projectFile.addTask();
            task.setName(name);
            task.setGUID(uuid);
            m_phaseNameMap.put(name, task);
            m_phaseUuidMap.put(uuid, task);
         }
      }
   }

   /**
    * Process the set of activities from the Phoenix file.
    *
    * @param phoenixProject project data
    */
   private void processActivities(Storepoint phoenixProject)
   {
      for (Activity activity : phoenixProject.getActivities().getActivity())
      {
         processActivity(activity);
      }
   }

   /**
    * Create a Task instance from a Phoenix activity.
    *
    * @param activity Phoenix activity data
    */
   private void processActivity(Activity activity)
   {
      Task task = getParentTask(activity).addTask();
      task.setText(1, activity.getId());

      task.setActualDuration(activity.getActualDuration());
      //activity.getBaseunit()
      //activity.getBilled()
      //activity.getCalendar()
      //activity.getCostAccount()
      task.setCreateDate(activity.getCreationTime());
      task.setFinish(activity.getCurrentFinish());
      task.setStart(activity.getCurrentStart());
      task.setName(activity.getDescription());
      task.setDuration(activity.getDurationAtCompletion());
      task.setEarlyFinish(activity.getEarlyFinish());
      task.setEarlyStart(activity.getEarlyStart());
      task.setFreeSlack(activity.getFreeFloat());
      task.setLateFinish(activity.getLateFinish());
      task.setLateStart(activity.getLateStart());
      task.setNotes(activity.getNotes());
      task.setBaselineDuration(activity.getOriginalDuration());
      //activity.getPathFloat()
      task.setPhysicalPercentComplete(activity.getPhysicalPercentComplete());
      task.setRemainingDuration(activity.getRemainingDuration());
      task.setCost(activity.getTotalCost());
      task.setTotalSlack(activity.getTotalFloat());
      task.setMilestone(activityIsMilestone(activity));
      //activity.getUserDefined()
      task.setGUID(activity.getUuid());

      m_activityMap.put(activity.getId(), task);
   }

   /**
    * Returns true if the activity is a milestone.
    *
    * @param activity Phoenix activity
    * @return true if the activity is a milestone
    */
   private boolean activityIsMilestone(Activity activity)
   {
      String type = activity.getType();
      return type != null && type.indexOf("Milestone") != -1;
   }

   /**
    * Retrieves the parent task for a Phoenix activity.
    *
    * @param activity Phoenix activity
    * @return parent task
    */
   private ChildTaskContainer getParentTask(Activity activity)
   {
      ChildTaskContainer result = null;
      for (CodeAssignment ca : activity.getCodeAssignment())
      {
         UUID uuid = ca.getValueUuid();
         if (uuid != null)
         {
            result = m_phaseUuidMap.get(uuid);
         }
         else
         {
            String code = ca.getCode();
            if (code != null && code.equals("Phase"))
            {
               result = m_phaseNameMap.get(ca.getValue());
            }
         }

         if (result != null)
         {
            break;
         }
      }

      if (result == null)
      {
         result = m_projectFile;
      }
      return result;
   }

   /**
    * Reads Phoenix resource assignments.
    *
    * @param mpxjResource MPXJ resource
    * @param res Phoenix resource
    */
   private void readAssignments(Resource mpxjResource, net.sf.mpxj.phoenix.schema.Project.Storepoints.Storepoint.Resources.Resource res)
   {
      for (Assignment assignment : res.getAssignment())
      {
         readAssignment(mpxjResource, assignment);
      }
   }

   /**
    * Read a single resource assignment.
    *
    * @param resource MPXJ resource
    * @param assignment Phoenix assignment
    */
   private void readAssignment(Resource resource, Assignment assignment)
   {
      Task task = m_activityMap.get(assignment.getActivity());
      if (task != null)
      {
         task.addResourceAssignment(resource);
      }
   }

   /**
    * Read task relationships from a Phoenix file.
    *
    * @param phoenixProject Phoenix project data
    */
   private void readRelationships(Storepoint phoenixProject)
   {
      for (Relationship relation : phoenixProject.getRelationships().getRelationship())
      {
         readRelation(relation);
      }
   }

   /**
    * Read an individual Phoenix task relationship.
    *
    * @param relation Phoenix task relationship
    */
   private void readRelation(Relationship relation)
   {
      Task predecessor = m_projectFile.getTaskByUniqueID(relation.getPredecessor());
      Task successor = m_projectFile.getTaskByUniqueID(relation.getSuccessor());
      if (predecessor != null && successor != null)
      {
         Duration lag = relation.getLag();
         RelationType type = relation.getType();
         successor.addPredecessor(predecessor, type, lag);
      }
   }

   private ProjectFile m_projectFile;
   private Map<String, Task> m_phaseNameMap;
   private Map<UUID, Task> m_phaseUuidMap;
   private Map<String, Task> m_activityMap;
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
         CONTEXT = JAXBContext.newInstance("net.sf.mpxj.phoenix.schema", PhoenixReader.class.getClassLoader());
      }

      catch (JAXBException ex)
      {
         CONTEXT_EXCEPTION = ex;
         CONTEXT = null;
      }
   }
}
