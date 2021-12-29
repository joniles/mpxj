/*
 * file:       Phoenix5Reader.java
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
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.stream.Collectors;

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
import net.sf.mpxj.ProjectCalendarHours;
import net.sf.mpxj.ProjectCalendarWeek;
import net.sf.mpxj.ProjectConfig;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ProjectProperties;
import net.sf.mpxj.Rate;
import net.sf.mpxj.RelationType;
import net.sf.mpxj.Resource;
import net.sf.mpxj.Task;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.common.AlphanumComparator;
import net.sf.mpxj.common.DateHelper;
import net.sf.mpxj.common.DebugLogPrintWriter;
import net.sf.mpxj.common.NumberHelper;
import net.sf.mpxj.common.UnmarshalHelper;
import net.sf.mpxj.phoenix.schema.phoenix5.Project;
import net.sf.mpxj.phoenix.schema.phoenix5.Project.Layouts.GanttLayout;
import net.sf.mpxj.phoenix.schema.phoenix5.Project.Layouts.GanttLayout.CodeOptions;
import net.sf.mpxj.phoenix.schema.phoenix5.Project.Layouts.GanttLayout.CodeOptions.CodeOption;
import net.sf.mpxj.phoenix.schema.phoenix5.Project.Settings;
import net.sf.mpxj.phoenix.schema.phoenix5.Project.Storepoints.Storepoint;
import net.sf.mpxj.phoenix.schema.phoenix5.Project.Storepoints.Storepoint.Activities.Activity;
import net.sf.mpxj.phoenix.schema.phoenix5.Project.Storepoints.Storepoint.ActivityCodes;
import net.sf.mpxj.phoenix.schema.phoenix5.Project.Storepoints.Storepoint.ActivityCodes.Code;
import net.sf.mpxj.phoenix.schema.phoenix5.Project.Storepoints.Storepoint.ActivityCodes.Code.Value;
import net.sf.mpxj.phoenix.schema.phoenix5.Project.Storepoints.Storepoint.Calendars;
import net.sf.mpxj.phoenix.schema.phoenix5.Project.Storepoints.Storepoint.Calendars.Calendar;
import net.sf.mpxj.phoenix.schema.phoenix5.Project.Storepoints.Storepoint.Calendars.Calendar.NonWork;
import net.sf.mpxj.phoenix.schema.phoenix5.Project.Storepoints.Storepoint.Relationships;
import net.sf.mpxj.phoenix.schema.phoenix5.Project.Storepoints.Storepoint.Relationships.Relationship;
import net.sf.mpxj.phoenix.schema.phoenix5.Project.Storepoints.Storepoint.Resources;
import net.sf.mpxj.phoenix.schema.phoenix5.Project.Storepoints.Storepoint.Resources.Resource.Assignment;
import net.sf.mpxj.reader.AbstractProjectStreamReader;

/**
 * This class creates a new ProjectFile instance by reading a Phoenix Project Manager file.
 */
public final class Phoenix5Reader extends AbstractProjectStreamReader
{
   @Override public ProjectFile read(InputStream stream) throws MPXJException
   {
      openLogFile();

      try
      {
         if (CONTEXT == null)
         {
            throw CONTEXT_EXCEPTION;
         }

         m_projectFile = new ProjectFile();
         m_activityMap = new HashMap<>();
         m_activityCodeValues = new HashMap<>();
         m_activityCodeSequence = new HashMap<>();
         m_activityCodeCache = new HashMap<>();
         m_codeSequence = new ArrayList<>();
         m_eventManager = m_projectFile.getEventManager();

         ProjectConfig config = m_projectFile.getProjectConfig();
         config.setAutoResourceUniqueID(true);
         config.setAutoOutlineLevel(false);
         config.setAutoOutlineNumber(false);
         config.setAutoWBS(false);

         m_projectFile.getProjectProperties().setFileApplication("Phoenix");
         m_projectFile.getProjectProperties().setFileType("PPX");

         addListenersToProject(m_projectFile);

         Project phoenixProject = (Project) UnmarshalHelper.unmarshal(CONTEXT, new SkipNulInputStream(stream));
         Storepoint storepoint = getCurrentStorepoint(phoenixProject);
         readProjectProperties(phoenixProject.getSettings(), storepoint);
         readCalendars(storepoint);
         readTasks(phoenixProject, storepoint);
         readResources(storepoint);
         readRelationships(storepoint);

         //
         // Ensure that the unique ID counters are correct
         //
         config.updateUniqueCounters();

         return (m_projectFile);
      }

      catch (ParserConfigurationException | SAXException | JAXBException ex)
      {
         throw new MPXJException("Failed to parse file", ex);
      }

      finally
      {
         m_projectFile = null;
         m_activityMap = null;
         m_activityCodeValues = null;
         m_activityCodeSequence = null;
         m_activityCodeCache = null;
         m_codeSequence = null;

         closeLogFile();
      }
   }

   @Override public List<ProjectFile> readAll(InputStream inputStream) throws MPXJException
   {
      return Collections.singletonList(read(inputStream));
   }

   /**
    * This method extracts project properties from a Phoenix file.
    *
    * @param phoenixSettings Phoenix settings
    * @param storepoint Current storepoint
    */
   private void readProjectProperties(Settings phoenixSettings, Storepoint storepoint)
   {
      ProjectProperties mpxjProperties = m_projectFile.getProjectProperties();
      mpxjProperties.setName(phoenixSettings.getTitle());
      mpxjProperties.setDefaultDurationUnits(phoenixSettings.getBaseunit());
      mpxjProperties.setStatusDate(storepoint.getDataDate());
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
         for (net.sf.mpxj.phoenix.schema.phoenix5.Project.Storepoints.Storepoint.Resources.Resource res : resources.getResource())
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
   private Resource readResource(net.sf.mpxj.phoenix.schema.phoenix5.Project.Storepoints.Storepoint.Resources.Resource phoenixResource)
   {
      Resource mpxjResource = m_projectFile.addResource();

      TimeUnit rateUnits = phoenixResource.getMonetarybase();
      if (rateUnits == null)
      {
         rateUnits = TimeUnit.HOURS;
      }

      // phoenixResource.getMaximum()
      mpxjResource.setCostPerUse(phoenixResource.getMonetarycostperuse());
      mpxjResource.setStandardRate(new Rate(phoenixResource.getMonetaryrate(), rateUnits));
      mpxjResource.setStandardRateUnits(rateUnits);
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
    * @param phoenixProject all project data
    * @param storepoint storepoint containing current project data
    */
   private void readTasks(Project phoenixProject, Storepoint storepoint)
   {
      processLayouts(phoenixProject);
      processActivityCodes(storepoint);
      processActivities(storepoint);
      updateDates();
   }

   /**
    * Map from an activity code value UUID to the actual value itself, and its
    * sequence number.
    *
    * @param storepoint storepoint containing current project data
    */
   private void processActivityCodes(Storepoint storepoint)
   {
      ActivityCodes activityCodes = storepoint.getActivityCodes();
      if (activityCodes != null)
      {
         for (Code code : activityCodes.getCode())
         {
            int sequence = 0;
            UUID codeUUID = getCodeUUID(code.getUuid(), code.getName());
            for (Value value : code.getValue())
            {
               String name = value.getName();
               UUID uuid = getValueUUID(codeUUID, value.getUuid(), name);
               m_activityCodeValues.put(uuid, name);
               m_activityCodeSequence.put(uuid, Integer.valueOf(++sequence));
            }
         }
      }
   }

   /**
    * Find the current layout and extract the activity code order and visibility.
    *
    * @param phoenixProject phoenix project data
    */
   private void processLayouts(Project phoenixProject)
   {
      //
      // Find the active layout
      //
      GanttLayout activeLayout = getActiveLayout(phoenixProject);

      //
      // Create a list of the visible codes in the correct order
      //
      CodeOptions codeOptions = activeLayout.getCodeOptions();
      if (codeOptions != null)
      {
         for (CodeOption option : codeOptions.getCodeOption())
         {
            if (option.isShown().booleanValue())
            {
               m_codeSequence.add(option.getCodeUuid());
            }
         }
      }
   }

   /**
    * Find the current active layout.
    *
    * @param phoenixProject phoenix project data
    * @return current active layout
    */
   private GanttLayout getActiveLayout(Project phoenixProject)
   {
      return phoenixProject.getLayouts().getGanttLayout().get(0);
   }

   /**
    * Process the set of activities from the Phoenix file.
    *
    * @param phoenixProject project data
    */
   private void processActivities(Storepoint phoenixProject)
   {
      final AlphanumComparator comparator = new AlphanumComparator();
      List<Activity> activities = phoenixProject.getActivities() == null ? Collections.emptyList() : phoenixProject.getActivities().getActivity();

      // If logging enabled, dump detail to investigate "Comparison method violates its general contract!" error
      if (m_log != null)
      {
         m_log.println("{");
         StringJoiner codeJoiner = new StringJoiner(",");
         m_codeSequence.forEach(code -> codeJoiner.add("\"" + code + "\""));
         m_log.println("\"codeSequence\": [" + codeJoiner + "],");

         StringJoiner sequenceJoiner = new StringJoiner(",");
         m_activityCodeSequence.entrySet().forEach(entry -> sequenceJoiner.add("\"" + entry.getKey() + "\": " + entry.getValue() + ""));
         m_log.println("\"activityCodeSequence\": {" + sequenceJoiner + "},");

         StringJoiner activityJoiner = new StringJoiner(",");
         for (Activity activity : activities)
         {
            Map<UUID, UUID> codes = getActivityCodes(activity);
            StringJoiner activityCodeJoiner = new StringJoiner(",");
            codes.entrySet().forEach(entry -> activityCodeJoiner.add("\"" + entry.getKey() + "\": \"" + entry.getValue() + "\""));
            activityJoiner.add("\"" + activity.getId() + "\": {" + activityCodeJoiner + "}");
         }
         m_log.println("\"activityCodes\": {" + activityJoiner + "}}");
      }

      // First pass: sort the activities by ID to avoid "Comparison method violates its general contract!" error
      activities.sort((o1, o2) -> comparator.compare(o1.getId(), o2.getId()));

      // Second pass: perform the main sort
      activities.sort(new Comparator<Activity>()
      {
         @Override public int compare(Activity o1, Activity o2)
         {
            Map<UUID, UUID> codes1 = getActivityCodes(o1);
            Map<UUID, UUID> codes2 = getActivityCodes(o2);
            for (UUID code : m_codeSequence)
            {
               UUID codeValue1 = codes1.get(code);
               UUID codeValue2 = codes2.get(code);

               if (codeValue1 == null || codeValue2 == null)
               {
                  if (codeValue1 == null && codeValue2 == null)
                  {
                     continue;
                  }

                  if (codeValue1 == null)
                  {
                     return -1;
                  }

                  if (codeValue2 == null)
                  {
                     return 1;
                  }
               }

               if (!codeValue1.equals(codeValue2))
               {
                  Integer sequence1 = m_activityCodeSequence.get(codeValue1);
                  Integer sequence2 = m_activityCodeSequence.get(codeValue2);

                  return NumberHelper.compare(sequence1, sequence2);
               }
            }

            return comparator.compare(o1.getId(), o2.getId());
         }
      });

      for (Activity activity : activities)
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
      task.setActivityID(activity.getId());

      task.setActualDuration(activity.getActualDuration());
      task.setActualFinish(activity.getActualFinish());
      task.setActualStart(activity.getActualStart());
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

      if (task.getMilestone())
      {
         if (activityIsStartMilestone(activity))
         {
            task.setFinish(task.getStart());
         }
         else
         {
            task.setStart(task.getFinish());
         }
      }

      if (task.getDuration().getDuration() == 0)
      {
         // Phoenix normally represents the finish date as the start of the
         // day following the end of the activity. For example a 2 day activity
         // starting on day 1 would be shown in the PPX file as having a finish
         // date of day 3. We subtract one day to make the dates consistent with
         // all other schedule formats MPXJ handles. Occasionally for zero
         // duration tasks (which aren't tagged as milestones) the finish date
         // will be the same as the start date, so applying our "subtract 1" fix
         // gives us a finish date before the start date. The code below
         // deals with this situation.
         if (DateHelper.compare(task.getStart(), task.getFinish()) > 0)
         {
            task.setFinish(task.getStart());
         }

         if (task.getActualStart() != null && task.getActualFinish() != null && DateHelper.compare(task.getActualStart(), task.getActualFinish()) > 0)
         {
            task.setActualFinish(task.getActualStart());
         }
      }

      if (task.getActualStart() == null)
      {
         task.setPercentageComplete(Integer.valueOf(0));
      }
      else
      {
         if (task.getActualFinish() != null)
         {
            task.setPercentageComplete(Integer.valueOf(100));
         }
         else
         {
            Duration remaining = activity.getRemainingDuration();
            Duration total = activity.getDurationAtCompletion();
            if (remaining != null && total != null && total.getDuration() != 0)
            {
               double percentComplete = ((total.getDuration() - remaining.getDuration()) * 100.0) / total.getDuration();
               task.setPercentageComplete(Double.valueOf(percentComplete));
            }
         }
      }

      // Force calculation to avoid later issues
      task.getStartSlack();
      task.getFinishSlack();
      task.getCritical();

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
      return type != null && type.contains("Milestone");
   }

   /**
    * Returns true if the activity is a start milestone.
    *
    * @param activity Phoenix activity
    * @return true if the activity is a milestone
    */
   private boolean activityIsStartMilestone(Activity activity)
   {
      String type = activity.getType();
      return type != null && type.contains("StartMilestone");
   }

   /**
    * Retrieves the parent task for a Phoenix activity.
    *
    * @param activity Phoenix activity
    * @return parent task
    */
   private ChildTaskContainer getParentTask(Activity activity)
   {
      //
      // Make a map of activity codes and their values for this activity
      //
      Map<UUID, UUID> map = getActivityCodes(activity);

      //
      // Work through the activity codes in sequence
      //
      ChildTaskContainer parent = m_projectFile;
      StringBuilder uniqueIdentifier = new StringBuilder();
      for (UUID activityCode : m_codeSequence)
      {
         UUID activityCodeValue = map.get(activityCode);
         String activityCodeText = m_activityCodeValues.get(activityCodeValue);
         if (activityCodeText != null)
         {
            if (uniqueIdentifier.length() != 0)
            {
               uniqueIdentifier.append('>');
            }
            uniqueIdentifier.append(activityCodeValue.toString());
            UUID uuid = UUID.nameUUIDFromBytes(uniqueIdentifier.toString().getBytes());
            Task newParent = findChildTaskByUUID(parent, uuid);
            if (newParent == null)
            {
               newParent = parent.addTask();
               newParent.setGUID(uuid);
               newParent.setName(activityCodeText);
            }
            parent = newParent;
         }
      }
      return parent;
   }

   /**
    * Locates a task within a child task container which matches the supplied UUID.
    *
    * @param parent child task container
    * @param uuid required UUID
    * @return Task instance or null if the task is not found
    */
   private Task findChildTaskByUUID(ChildTaskContainer parent, UUID uuid)
   {
      Task result = null;

      for (Task task : parent.getChildTasks())
      {
         if (uuid.equals(task.getGUID()))
         {
            result = task;
            break;
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
   private void readAssignments(Resource mpxjResource, net.sf.mpxj.phoenix.schema.phoenix5.Project.Storepoints.Storepoint.Resources.Resource res)
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
      Relationships relationships = phoenixProject.getRelationships();
      if (relationships != null)
      {
         for (Relationship relation : relationships.getRelationship())
         {
            readRelation(relation);
         }
      }
   }

   /**
    * Read an individual Phoenix task relationship.
    *
    * @param relation Phoenix task relationship
    */
   private void readRelation(Relationship relation)
   {
      Task predecessor = m_activityMap.get(relation.getPredecessor());
      Task successor = m_activityMap.get(relation.getSuccessor());
      if (predecessor != null && successor != null)
      {
         Duration lag = relation.getLag();
         RelationType type = relation.getType();
         successor.addPredecessor(predecessor, type, lag);
      }
   }

   /**
    * For a given activity, retrieve a map of the activity code values which have been assigned to it.
    *
    * @param activity target activity
    * @return map of activity code value UUIDs
    */
   Map<UUID, UUID> getActivityCodes(Activity activity)
   {
      return m_activityCodeCache.computeIfAbsent(activity, this::getActivityCodesForCache);
   }

   private Map<UUID, UUID> getActivityCodesForCache(Activity activity)
   {
      return activity.getCodeAssignment().stream().collect(Collectors.toMap(Activity.CodeAssignment::getCodeUuid, Activity.CodeAssignment::getValueUuid));
   }

   /**
    * Retrieve the most recent storepoint.
    *
    * @param phoenixProject project data
    * @return Storepoint instance
    */
   private Storepoint getCurrentStorepoint(Project phoenixProject)
   {
      List<Storepoint> storepoints = phoenixProject.getStorepoints() == null ? Collections.emptyList() : phoenixProject.getStorepoints().getStorepoint();
      storepoints.sort((o1, o2) -> DateHelper.compare(o2.getCreationTime(), o1.getCreationTime()));
      return storepoints.get(0);
   }

   /**
    * Utility method. In some cases older compressed PPX files only have a code name
    * but no UUID. This method ensures that we either use the UUID supplied, or if it is missing, we
    * generate a UUID from the name.
    *
    * @param uuid UUID from object
    * @param name name from object
    * @return UUID instance
    */
   private UUID getCodeUUID(UUID uuid, String name)
   {
      return uuid == null ? UUID.nameUUIDFromBytes(name.getBytes()) : uuid;
   }

   /**
    * Utility method. In some cases older compressed PPX files only have a value name
    * but no UUID. This method ensures that we either use the UUID supplied, or if it is missing, we
    * generate a UUID from the value name and parent code UUID.
    *
    * @param parent parent code UUID
    * @param uuid value UUID
    * @param name value name
    * @return UUID instance
    */
   private UUID getValueUUID(UUID parent, UUID uuid, String name)
   {
      UUID result;
      if (uuid == null)
      {
         result = UUID.nameUUIDFromBytes((parent.toString() + ":" + name).getBytes());
      }
      else
      {
         result = uuid;
      }
      return result;
   }

   /**
    * Ensure summary tasks have dates.
    */
   private void updateDates()
   {
      for (Task task : m_projectFile.getChildTasks())
      {
         updateDates(task);
      }
   }

   /**
    * See the notes above.
    *
    * @param parentTask parent task.
    */
   private void updateDates(Task parentTask)
   {
      if (parentTask.hasChildTasks())
      {
         int finished = 0;
         Date plannedStartDate = parentTask.getStart();
         Date plannedFinishDate = parentTask.getFinish();
         Date actualStartDate = parentTask.getActualStart();
         Date actualFinishDate = parentTask.getActualFinish();
         Date earlyStartDate = parentTask.getEarlyStart();
         Date earlyFinishDate = parentTask.getEarlyFinish();
         Date lateStartDate = parentTask.getLateStart();
         Date lateFinishDate = parentTask.getLateFinish();
         boolean critical = false;

         for (Task task : parentTask.getChildTasks())
         {
            updateDates(task);

            plannedStartDate = DateHelper.min(plannedStartDate, task.getStart());
            plannedFinishDate = DateHelper.max(plannedFinishDate, task.getFinish());
            actualStartDate = DateHelper.min(actualStartDate, task.getActualStart());
            actualFinishDate = DateHelper.max(actualFinishDate, task.getActualFinish());
            earlyStartDate = DateHelper.min(earlyStartDate, task.getEarlyStart());
            earlyFinishDate = DateHelper.max(earlyFinishDate, task.getEarlyFinish());
            lateStartDate = DateHelper.min(lateStartDate, task.getLateStart());
            lateFinishDate = DateHelper.max(lateFinishDate, task.getLateFinish());

            if (task.getActualFinish() != null)
            {
               ++finished;
            }

            critical = critical || task.getCritical();
         }

         parentTask.setStart(plannedStartDate);
         parentTask.setFinish(plannedFinishDate);
         parentTask.setActualStart(actualStartDate);
         parentTask.setEarlyStart(earlyStartDate);
         parentTask.setEarlyFinish(earlyFinishDate);
         parentTask.setLateStart(lateStartDate);
         parentTask.setLateFinish(lateFinishDate);

         //
         // Only if all child tasks have actual finish dates do we
         // set the actual finish date on the parent task.
         //
         if (finished == parentTask.getChildTasks().size())
         {
            parentTask.setActualFinish(actualFinishDate);
         }

         if (plannedStartDate != null && plannedFinishDate != null)
         {
            Duration duration = m_projectFile.getDefaultCalendar().getWork(plannedStartDate, plannedFinishDate, TimeUnit.DAYS);
            parentTask.setDuration(duration);
         }

         // Force calculation here to avoid later issues
         parentTask.getStartSlack();
         parentTask.getFinishSlack();
         parentTask.setCritical(critical);

      }
   }

   /**
    * Open the log file for writing.
    */
   private void openLogFile()
   {
      m_log = DebugLogPrintWriter.getInstance();
   }

   /**
    * Close the log file.
    */
   private void closeLogFile()
   {
      if (m_log != null)
      {
         m_log.flush();
         m_log.close();
      }
   }

   private PrintWriter m_log;
   private ProjectFile m_projectFile;
   private Map<String, Task> m_activityMap;
   private Map<UUID, String> m_activityCodeValues;
   Map<UUID, Integer> m_activityCodeSequence;
   private Map<Activity, Map<UUID, UUID>> m_activityCodeCache;
   private EventManager m_eventManager;
   List<UUID> m_codeSequence;

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
         CONTEXT = JAXBContext.newInstance("net.sf.mpxj.phoenix.schema.phoenix5", Phoenix5Reader.class.getClassLoader());
      }

      catch (JAXBException ex)
      {
         CONTEXT_EXCEPTION = ex;
         CONTEXT = null;
      }
   }
}
