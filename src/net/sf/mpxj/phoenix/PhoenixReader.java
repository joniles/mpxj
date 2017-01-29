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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;

import net.sf.mpxj.ChildTaskContainer;
import net.sf.mpxj.Duration;
import net.sf.mpxj.EventManager;
import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectConfig;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ProjectProperties;
import net.sf.mpxj.Rate;
import net.sf.mpxj.RelationType;
import net.sf.mpxj.Resource;
import net.sf.mpxj.Task;
import net.sf.mpxj.listener.ProjectListener;
import net.sf.mpxj.phoenix.schema.Project;
import net.sf.mpxj.phoenix.schema.Project.Settings;
import net.sf.mpxj.phoenix.schema.Project.Storepoints.Storepoint;
import net.sf.mpxj.phoenix.schema.Project.Storepoints.Storepoint.Activities.Activity;
import net.sf.mpxj.phoenix.schema.Project.Storepoints.Storepoint.Activities.Activity.CodeAssignment;
import net.sf.mpxj.phoenix.schema.Project.Storepoints.Storepoint.ActivityCodes.Code;
import net.sf.mpxj.phoenix.schema.Project.Storepoints.Storepoint.ActivityCodes.Code.Value;
import net.sf.mpxj.phoenix.schema.Project.Storepoints.Storepoint.Relationships.Relationship;
import net.sf.mpxj.phoenix.schema.Project.Storepoints.Storepoint.Resources;
import net.sf.mpxj.phoenix.schema.Project.Storepoints.Storepoint.Resources.Resource.Assignment;
import net.sf.mpxj.planner.PlannerReader;
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
         m_eventManager = m_projectFile.getEventManager();

         ProjectConfig config = m_projectFile.getProjectConfig();
         config.setAutoTaskUniqueID(false);
         config.setAutoResourceUniqueID(true);
         config.setAutoOutlineLevel(false);
         config.setAutoOutlineNumber(false);
         config.setAutoWBS(false);

         m_eventManager.addProjectListeners(m_projectListeners);

         SAXParserFactory factory = SAXParserFactory.newInstance();
         //factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
         //factory.setNamespaceAware(true);
         SAXParser saxParser = factory.newSAXParser();
         XMLReader xmlReader = saxParser.getXMLReader();
         //SAXSource doc = new SAXSource(xmlReader, new InputSource(new PhoenixInputStream(stream)));
         SAXSource doc = new SAXSource(xmlReader, new InputSource(stream));

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
      //      Calendars calendars = phoenixProject.getCalendars();
      //      if (calendars != null)
      //      {
      //         for (net.sf.mpxj.planner.schema.Calendar cal : calendars.getCalendar())
      //         {
      //            readCalendar(cal, null);
      //         }
      //
      //         Integer defaultCalendarID = getInteger(phoenixProject.getCalendar());
      //         m_defaultCalendar = m_projectFile.getCalendarByUniqueID(defaultCalendarID);
      //         if (m_defaultCalendar != null)
      //         {
      //            m_projectFile.getProjectProperties().setDefaultCalendarName(m_defaultCalendar.getName());
      //         }
      //      }
   }

   /**
    * This method extracts data for a single calendar from a Planner file.
    *
    * @param plannerCalendar Calendar data
    * @param parentMpxjCalendar parent of derived calendar
    */
   //   private void readCalendar(net.sf.mpxj.planner.schema.Calendar plannerCalendar, ProjectCalendar parentMpxjCalendar) throws MPXJException
   //   {
   //      //
   //      // Create a calendar instance
   //      //
   //      ProjectCalendar mpxjCalendar = m_projectFile.addCalendar();
   //
   //      //
   //      // Populate basic details
   //      //
   //      mpxjCalendar.setUniqueID(getInteger(plannerCalendar.getId()));
   //      mpxjCalendar.setName(plannerCalendar.getName());
   //      mpxjCalendar.setParent(parentMpxjCalendar);
   //
   //      //
   //      // Set working and non working days
   //      //
   //      DefaultWeek dw = plannerCalendar.getDefaultWeek();
   //      setWorkingDay(mpxjCalendar, Day.MONDAY, dw.getMon());
   //      setWorkingDay(mpxjCalendar, Day.TUESDAY, dw.getTue());
   //      setWorkingDay(mpxjCalendar, Day.WEDNESDAY, dw.getWed());
   //      setWorkingDay(mpxjCalendar, Day.THURSDAY, dw.getThu());
   //      setWorkingDay(mpxjCalendar, Day.FRIDAY, dw.getFri());
   //      setWorkingDay(mpxjCalendar, Day.SATURDAY, dw.getSat());
   //      setWorkingDay(mpxjCalendar, Day.SUNDAY, dw.getSun());
   //
   //      //
   //      // Set working hours
   //      //
   //      processWorkingHours(mpxjCalendar, plannerCalendar);
   //
   //      //
   //      // Process exception days
   //      //
   //      processExceptionDays(mpxjCalendar, plannerCalendar);
   //
   //      m_eventManager.fireCalendarReadEvent(mpxjCalendar);
   //
   //      //
   //      // Process any derived calendars
   //      //
   //      List<net.sf.mpxj.planner.schema.Calendar> calendarList = plannerCalendar.getCalendar();
   //      for (net.sf.mpxj.planner.schema.Calendar cal : calendarList)
   //      {
   //         readCalendar(cal, mpxjCalendar);
   //      }
   //   }

   /**
    * Set the working/non-working status of a weekday.
    *
    * @param mpxjCalendar MPXJ calendar
    * @param mpxjDay day of the week
    * @param plannerDay planner day type
    */
   //   private void setWorkingDay(ProjectCalendar mpxjCalendar, Day mpxjDay, String plannerDay)
   //   {
   //      DayType dayType = DayType.DEFAULT;
   //
   //      if (plannerDay != null)
   //      {
   //         switch (getInt(plannerDay))
   //         {
   //            case 0:
   //            {
   //               dayType = DayType.WORKING;
   //               break;
   //            }
   //
   //            case 1:
   //            {
   //               dayType = DayType.NON_WORKING;
   //               break;
   //            }
   //         }
   //      }
   //
   //      mpxjCalendar.setWorkingDay(mpxjDay, dayType);
   //   }

   /**
    * Add the appropriate working hours to each working day.
    *
    * @param mpxjCalendar MPXJ calendar
    * @param plannerCalendar Planner calendar
    */
   //   private void processWorkingHours(ProjectCalendar mpxjCalendar, net.sf.mpxj.planner.schema.Calendar plannerCalendar) throws MPXJException
   //   {
   //      OverriddenDayTypes types = plannerCalendar.getOverriddenDayTypes();
   //      if (types != null)
   //      {
   //         List<OverriddenDayType> typeList = types.getOverriddenDayType();
   //         Iterator<OverriddenDayType> iter = typeList.iterator();
   //         OverriddenDayType odt = null;
   //         while (iter.hasNext())
   //         {
   //            odt = iter.next();
   //            if (getInt(odt.getId()) != 0)
   //            {
   //               odt = null;
   //               continue;
   //            }
   //
   //            break;
   //         }
   //
   //         if (odt != null)
   //         {
   //            List<Interval> intervalList = odt.getInterval();
   //            ProjectCalendarHours mondayHours = null;
   //            ProjectCalendarHours tuesdayHours = null;
   //            ProjectCalendarHours wednesdayHours = null;
   //            ProjectCalendarHours thursdayHours = null;
   //            ProjectCalendarHours fridayHours = null;
   //            ProjectCalendarHours saturdayHours = null;
   //            ProjectCalendarHours sundayHours = null;
   //
   //            if (mpxjCalendar.isWorkingDay(Day.MONDAY))
   //            {
   //               mondayHours = mpxjCalendar.addCalendarHours(Day.MONDAY);
   //            }
   //
   //            if (mpxjCalendar.isWorkingDay(Day.TUESDAY))
   //            {
   //               tuesdayHours = mpxjCalendar.addCalendarHours(Day.TUESDAY);
   //            }
   //
   //            if (mpxjCalendar.isWorkingDay(Day.WEDNESDAY))
   //            {
   //               wednesdayHours = mpxjCalendar.addCalendarHours(Day.WEDNESDAY);
   //            }
   //
   //            if (mpxjCalendar.isWorkingDay(Day.THURSDAY))
   //            {
   //               thursdayHours = mpxjCalendar.addCalendarHours(Day.THURSDAY);
   //            }
   //
   //            if (mpxjCalendar.isWorkingDay(Day.FRIDAY))
   //            {
   //               fridayHours = mpxjCalendar.addCalendarHours(Day.FRIDAY);
   //            }
   //
   //            if (mpxjCalendar.isWorkingDay(Day.SATURDAY))
   //            {
   //               saturdayHours = mpxjCalendar.addCalendarHours(Day.SATURDAY);
   //            }
   //
   //            if (mpxjCalendar.isWorkingDay(Day.SUNDAY))
   //            {
   //               sundayHours = mpxjCalendar.addCalendarHours(Day.SUNDAY);
   //            }
   //
   //            for (Interval interval : intervalList)
   //            {
   //               Date startTime = getTime(interval.getStart());
   //               Date endTime = getTime(interval.getEnd());
   //
   //               m_defaultWorkingHours.add(new DateRange(startTime, endTime));
   //
   //               if (mondayHours != null)
   //               {
   //                  mondayHours.addRange(new DateRange(startTime, endTime));
   //               }
   //
   //               if (tuesdayHours != null)
   //               {
   //                  tuesdayHours.addRange(new DateRange(startTime, endTime));
   //               }
   //
   //               if (wednesdayHours != null)
   //               {
   //                  wednesdayHours.addRange(new DateRange(startTime, endTime));
   //               }
   //
   //               if (thursdayHours != null)
   //               {
   //                  thursdayHours.addRange(new DateRange(startTime, endTime));
   //               }
   //
   //               if (fridayHours != null)
   //               {
   //                  fridayHours.addRange(new DateRange(startTime, endTime));
   //               }
   //
   //               if (saturdayHours != null)
   //               {
   //                  saturdayHours.addRange(new DateRange(startTime, endTime));
   //               }
   //
   //               if (sundayHours != null)
   //               {
   //                  sundayHours.addRange(new DateRange(startTime, endTime));
   //               }
   //            }
   //         }
   //      }
   //   }

   /**
    * Process exception days.
    *
    * @param mpxjCalendar MPXJ calendar
    * @param plannerCalendar Planner calendar
    */
   //   private void processExceptionDays(ProjectCalendar mpxjCalendar, net.sf.mpxj.planner.schema.Calendar plannerCalendar) throws MPXJException
   //   {
   //      Days days = plannerCalendar.getDays();
   //      if (days != null)
   //      {
   //         List<net.sf.mpxj.planner.schema.Day> dayList = days.getDay();
   //         for (net.sf.mpxj.planner.schema.Day day : dayList)
   //         {
   //            if (day.getType().equals("day-type"))
   //            {
   //               Date exceptionDate = getDate(day.getDate());
   //               ProjectCalendarException exception = mpxjCalendar.addCalendarException(exceptionDate, exceptionDate);
   //               if (getInt(day.getId()) == 0)
   //               {
   //                  for (int hoursIndex = 0; hoursIndex < m_defaultWorkingHours.size(); hoursIndex++)
   //                  {
   //                     DateRange range = m_defaultWorkingHours.get(hoursIndex);
   //                     exception.addRange(range);
   //                  }
   //               }
   //            }
   //         }
   //      }
   //   }

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
      Map<String, Task> phases = processPhases(phoenixProject);
      processActivities(phases, phoenixProject);
   }

   /**
    * Read phases from the Phoenix file.
    *
    * @param phoenixProject project data
    * @return phase name to task map
    */
   private Map<String, Task> processPhases(Storepoint phoenixProject)
   {
      Map<String, Task> map = new HashMap<String, Task>();
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
            Task task = m_projectFile.addTask();
            task.setName(value.getName());
            task.setGUID(value.getUuid());
            map.put(value.getName(), task);
         }
      }
      return map;
   }

   /**
    * Process the set of activities from the Phoenix file.
    *
    * @param phaseMap map of phase names to tasks
    * @param phoenixProject project data
    */
   private void processActivities(Map<String, Task> phaseMap, Storepoint phoenixProject)
   {
      for (Activity activity : phoenixProject.getActivities().getActivity())
      {
         processActivity(phaseMap, activity);
      }
   }

   /**
    * Create a Task instance from a Phoenix activity.
    *
    * @param phaseMap map of phase names to tasks
    * @param activity Phoenix activity data
    */
   private void processActivity(Map<String, Task> phaseMap, Activity activity)
   {
      Task task = getParentTask(phaseMap, activity).addTask();
      task.setUniqueID(activity.getId());

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
    * @param phaseMap phase name to task map
    * @param activity Phoenix activity
    * @return parent task
    */
   private ChildTaskContainer getParentTask(Map<String, Task> phaseMap, Activity activity)
   {
      ChildTaskContainer result = m_projectFile;
      for (CodeAssignment ca : activity.getCodeAssignment())
      {
         if (ca.getCode().equals("Phase"))
         {
            result = phaseMap.get(ca.getValue());
         }
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
      Task task = m_projectFile.getTaskByUniqueID(assignment.getActivity());
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
         CONTEXT = JAXBContext.newInstance("net.sf.mpxj.phoenix.schema", PlannerReader.class.getClassLoader());
      }

      catch (JAXBException ex)
      {
         CONTEXT_EXCEPTION = ex;
         CONTEXT = null;
      }
   }
}
