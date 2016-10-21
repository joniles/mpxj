/*
 * file:       PrimaveraPMFileReader.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2011
 * date:       08/08/2011
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

package net.sf.mpxj.primavera;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;

import net.sf.mpxj.AssignmentField;
import net.sf.mpxj.ConstraintType;
import net.sf.mpxj.CustomFieldContainer;
import net.sf.mpxj.DateRange;
import net.sf.mpxj.Day;
import net.sf.mpxj.DayType;
import net.sf.mpxj.Duration;
import net.sf.mpxj.EventManager;
import net.sf.mpxj.FieldContainer;
import net.sf.mpxj.FieldType;
import net.sf.mpxj.MPXJException;
import net.sf.mpxj.Priority;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectCalendarException;
import net.sf.mpxj.ProjectCalendarHours;
import net.sf.mpxj.ProjectConfig;
import net.sf.mpxj.ProjectEntityWithUniqueID;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ProjectProperties;
import net.sf.mpxj.Relation;
import net.sf.mpxj.RelationType;
import net.sf.mpxj.Resource;
import net.sf.mpxj.ResourceAssignment;
import net.sf.mpxj.Task;
import net.sf.mpxj.TaskField;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.common.BooleanHelper;
import net.sf.mpxj.common.DateHelper;
import net.sf.mpxj.common.NumberHelper;
import net.sf.mpxj.listener.ProjectListener;
import net.sf.mpxj.mpp.CustomFieldValueItem;
import net.sf.mpxj.primavera.schema.APIBusinessObjects;
import net.sf.mpxj.primavera.schema.ActivityType;
import net.sf.mpxj.primavera.schema.CalendarType;
import net.sf.mpxj.primavera.schema.CalendarType.HolidayOrExceptions;
import net.sf.mpxj.primavera.schema.CalendarType.HolidayOrExceptions.HolidayOrException;
import net.sf.mpxj.primavera.schema.CalendarType.StandardWorkWeek;
import net.sf.mpxj.primavera.schema.CalendarType.StandardWorkWeek.StandardWorkHours;
import net.sf.mpxj.primavera.schema.CurrencyType;
import net.sf.mpxj.primavera.schema.GlobalPreferencesType;
import net.sf.mpxj.primavera.schema.ProjectType;
import net.sf.mpxj.primavera.schema.RelationshipType;
import net.sf.mpxj.primavera.schema.ResourceAssignmentType;
import net.sf.mpxj.primavera.schema.ResourceType;
import net.sf.mpxj.primavera.schema.UDFAssignmentType;
import net.sf.mpxj.primavera.schema.UDFTypeType;
import net.sf.mpxj.primavera.schema.WBSType;
import net.sf.mpxj.primavera.schema.WorkTimeType;
import net.sf.mpxj.reader.AbstractProjectReader;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * This class creates a new ProjectFile instance by reading a Primavera PM file.
 */
public final class PrimaveraPMFileReader extends AbstractProjectReader
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
         //
         // This is a hack to ensure that the incoming file has a namespace
         // which JAXB will accept.
         //
         InputStream namespaceCorrectedStream = new ReplaceOnceStream(stream, NAMESPACE_REGEX, NAMESPACE_REPLACEMENT, NAMESPACE_SCOPE, UTF8);

         m_projectFile = new ProjectFile();
         m_eventManager = m_projectFile.getEventManager();

         ProjectConfig config = m_projectFile.getProjectConfig();
         config.setAutoTaskUniqueID(false);
         config.setAutoResourceUniqueID(false);
         config.setAutoCalendarUniqueID(false);
         config.setAutoAssignmentUniqueID(false);

         CustomFieldContainer fields = m_projectFile.getCustomFields();
         fields.getCustomField(TaskField.TEXT1).setAlias("WBS Code");
         fields.getCustomField(TaskField.TEXT2).setAlias("Task ID");

         m_eventManager.addProjectListeners(m_projectListeners);

         SAXParserFactory factory = SAXParserFactory.newInstance();
         factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
         factory.setNamespaceAware(true);
         SAXParser saxParser = factory.newSAXParser();
         XMLReader xmlReader = saxParser.getXMLReader();
         SAXSource doc = new SAXSource(xmlReader, new InputSource(namespaceCorrectedStream));

         if (CONTEXT == null)
         {
            throw CONTEXT_EXCEPTION;
         }

         Unmarshaller unmarshaller = CONTEXT.createUnmarshaller();

         APIBusinessObjects apibo = (APIBusinessObjects) unmarshaller.unmarshal(doc);

         List<ProjectType> projects = apibo.getProject();
         ProjectType project = null;
         for (ProjectType currentProject : projects)
         {
            if (!BooleanHelper.getBoolean(currentProject.isExternal()))
            {
               project = currentProject;
               break;
            }
         }

         if (project == null)
         {
            throw new MPXJException("Unable to locate any non-external projects in a list of " + projects.size() + " projects");
         }

         processProjectUDFs(apibo);
         processProjectProperties(apibo, project);
         processCalendars(apibo);
         processResources(apibo);
         processTasks(project);
         processPredecessors(project);
         processAssignments(project);

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
         m_clashMap.clear();
         m_calMap.clear();
      }
   }

   /**
    * Set up CustomFieldValueItems as UDF object id -> UDFType title (alias).
    *
    * @param apibo PMXML data
    * @author lsong
    */
   private void processProjectUDFs(APIBusinessObjects apibo)
   {
      CustomFieldContainer customFields = m_projectFile.getCustomFields();
      for (UDFTypeType udf : apibo.getUDFType())
      {
         CustomFieldValueItem item = new CustomFieldValueItem(udf.getObjectId());
         item.setValue(udf.getTitle());
         customFields.registerValue(item);
      }
   }

   /**
    * Process project properties.
    *
    * @param apibo top level object
    * @param project xml container
    */
   private void processProjectProperties(APIBusinessObjects apibo, ProjectType project)
   {
      ProjectProperties properties = m_projectFile.getProjectProperties();

      properties.setCreationDate(project.getCreateDate());
      properties.setFinishDate(project.getFinishDate());
      properties.setName(project.getName());
      properties.setStartDate(project.getPlannedStartDate());
      properties.setStatusDate(project.getDataDate());
      properties.setProjectTitle(project.getId());

      List<GlobalPreferencesType> list = apibo.getGlobalPreferences();
      if (!list.isEmpty())
      {
         GlobalPreferencesType prefs = list.get(0);

         properties.setCreationDate(prefs.getCreateDate());
         properties.setLastSaved(prefs.getLastUpdateDate());
         properties.setMinutesPerDay(Integer.valueOf((int) (NumberHelper.getDouble(prefs.getHoursPerDay()) * 60)));
         properties.setMinutesPerWeek(Integer.valueOf((int) (NumberHelper.getDouble(prefs.getHoursPerWeek()) * 60)));
         properties.setWeekStartDay(Day.getInstance(NumberHelper.getInt(prefs.getStartDayOfWeek())));

         List<CurrencyType> currencyList = apibo.getCurrency();
         for (CurrencyType currency : currencyList)
         {
            if (currency.getObjectId().equals(prefs.getBaseCurrencyObjectId()))
            {
               properties.setCurrencySymbol(currency.getSymbol());
               break;
            }
         }
      }
   }

   /**
    * Process project calendars.
    *
    * @param apibo xml container
    */
   private void processCalendars(APIBusinessObjects apibo)
   {
      for (CalendarType row : apibo.getCalendar())
      {
         ProjectCalendar calendar = m_projectFile.addCalendar();
         Integer id = row.getObjectId();
         m_calMap.put(id, calendar);
         calendar.setName(row.getName());
         calendar.setUniqueID(id);

         StandardWorkWeek stdWorkWeek = row.getStandardWorkWeek();
         if (stdWorkWeek != null)
         {
            for (StandardWorkHours hours : stdWorkWeek.getStandardWorkHours())
            {
               Day day = DAY_MAP.get(hours.getDayOfWeek());
               List<WorkTimeType> workTime = hours.getWorkTime();
               if (workTime.isEmpty() || workTime.get(0) == null)
               {
                  calendar.setWorkingDay(day, false);
               }
               else
               {
                  calendar.setWorkingDay(day, true);

                  ProjectCalendarHours calendarHours = calendar.addCalendarHours(day);
                  for (WorkTimeType work : workTime)
                  {
                     if (work != null)
                     {
                        calendarHours.addRange(new DateRange(work.getStart(), getEndTime(work.getFinish())));
                     }
                  }
               }
            }
         }

         HolidayOrExceptions hoe = row.getHolidayOrExceptions();
         if (hoe != null)
         {
            for (HolidayOrException ex : hoe.getHolidayOrException())
            {
               Date startDate = DateHelper.getDayStartDate(ex.getDate());
               Date endDate = DateHelper.getDayEndDate(ex.getDate());
               ProjectCalendarException pce = calendar.addCalendarException(startDate, endDate);

               List<WorkTimeType> workTime = ex.getWorkTime();
               for (WorkTimeType work : workTime)
               {
                  if (work != null)
                  {
                     pce.addRange(new DateRange(work.getStart(), getEndTime(work.getFinish())));
                  }
               }
            }
         }
      }
   }

   /**
    * Process resources.
    *
    * @param apibo xml container
    */
   private void processResources(APIBusinessObjects apibo)
   {
      List<ResourceType> resources = apibo.getResource();
      for (ResourceType xml : resources)
      {
         Resource resource = m_projectFile.addResource();
         resource.setUniqueID(xml.getObjectId());
         resource.setName(xml.getName());
         resource.setCode(xml.getEmployeeId());
         resource.setEmailAddress(xml.getEmailAddress());
         resource.setGUID(DatatypeConverter.parseUUID(xml.getGUID()));
         resource.setNotes(xml.getResourceNotes());
         resource.setCreationDate(xml.getCreateDate());
         resource.setType(RESOURCE_TYPE_MAP.get(xml.getResourceType()));
         resource.setMaxUnits(reversePercentage(xml.getMaxUnitsPerTime()));
         resource.setParentID(xml.getParentObjectId());

         Integer calendarID = xml.getCalendarObjectId();
         if (calendarID != null)
         {
            ProjectCalendar calendar = m_calMap.get(calendarID);
            if (calendar != null)
            {
               //
               // If the resource is linked to a base calendar, derive
               // a default calendar from the base calendar.
               //
               if (!calendar.isDerived())
               {
                  ProjectCalendar resourceCalendar = m_projectFile.addCalendar();
                  resourceCalendar.setParent(calendar);
                  resourceCalendar.setWorkingDay(Day.MONDAY, DayType.DEFAULT);
                  resourceCalendar.setWorkingDay(Day.TUESDAY, DayType.DEFAULT);
                  resourceCalendar.setWorkingDay(Day.WEDNESDAY, DayType.DEFAULT);
                  resourceCalendar.setWorkingDay(Day.THURSDAY, DayType.DEFAULT);
                  resourceCalendar.setWorkingDay(Day.FRIDAY, DayType.DEFAULT);
                  resourceCalendar.setWorkingDay(Day.SATURDAY, DayType.DEFAULT);
                  resourceCalendar.setWorkingDay(Day.SUNDAY, DayType.DEFAULT);
                  resource.setResourceCalendar(resourceCalendar);
               }
               else
               {
                  //
                  // Primavera seems to allow a calendar to be shared between resources
                  // whereas in the MS Project model there is a one-to-one
                  // relationship. If we find a shared calendar, take a copy of it
                  //
                  if (calendar.getResource() == null)
                  {
                     resource.setResourceCalendar(calendar);
                  }
                  else
                  {
                     ProjectCalendar copy = m_projectFile.addCalendar();
                     copy.copy(calendar);
                     resource.setResourceCalendar(copy);
                  }
               }
            }
         }

         readUDFTypes(resource, xml.getUDF());

         m_eventManager.fireResourceReadEvent(resource);
      }
   }

   /**
    * Process tasks.
    *
    * @param project xml container
    */
   private void processTasks(ProjectType project)
   {
      List<WBSType> wbs = project.getWBS();
      List<ActivityType> tasks = project.getActivity();

      Set<Integer> uniqueIDs = new HashSet<Integer>();

      //
      // Read WBS entries and create tasks
      //
      for (WBSType row : wbs)
      {
         Task task = m_projectFile.addTask();
         Integer uniqueID = row.getObjectId();
         uniqueIDs.add(uniqueID);

         task.setUniqueID(uniqueID);
         task.setName(row.getName());
         task.setBaselineCost(row.getSummaryBaselineTotalCost());
         task.setGUID(DatatypeConverter.parseUUID(row.getGUID()));
         task.setRemainingCost(row.getSummaryRemainingTotalCost());
         task.setRemainingDuration(getDuration(row.getSummaryRemainingDuration()));
         task.setStart(row.getAnticipatedStartDate());
         task.setFinish(row.getAnticipatedFinishDate());
         task.setText(1, row.getCode());
      }

      //
      // Create hierarchical structure
      //
      m_projectFile.getChildTasks().clear();
      for (WBSType row : wbs)
      {
         Task task = m_projectFile.getTaskByUniqueID(row.getObjectId());
         Task parentTask = m_projectFile.getTaskByUniqueID(row.getParentObjectId());
         if (parentTask == null)
         {
            m_projectFile.getChildTasks().add(task);
         }
         else
         {
            m_projectFile.getChildTasks().remove(task);
            parentTask.getChildTasks().add(task);
         }
      }

      //
      // Read Task entries and create tasks
      //
      int nextID = 1;
      m_clashMap.clear();
      for (ActivityType row : tasks)
      {
         Integer uniqueID = row.getObjectId();
         if (uniqueIDs.contains(uniqueID))
         {
            while (uniqueIDs.contains(Integer.valueOf(nextID)))
            {
               ++nextID;
            }
            Integer newUniqueID = Integer.valueOf(nextID);
            m_clashMap.put(uniqueID, newUniqueID);
            uniqueID = newUniqueID;
         }
         uniqueIDs.add(uniqueID);

         Task task;
         Integer parentTaskID = row.getWBSObjectId();
         Task parentTask = m_projectFile.getTaskByUniqueID(parentTaskID);
         if (parentTask == null)
         {
            task = m_projectFile.addTask();
         }
         else
         {
            task = parentTask.addTask();
         }

         task.setUniqueID(uniqueID);
         task.setGUID(DatatypeConverter.parseUUID(row.getGUID()));
         task.setPercentageComplete(reversePercentage(row.getPercentComplete()));
         task.setName(row.getName());
         task.setRemainingDuration(getDuration(row.getRemainingDuration()));
         task.setActualWork(getDuration(row.getActualDuration()));
         task.setRemainingWork(getDuration(row.getRemainingTotalUnits()));
         task.setBaselineDuration(getDuration(row.getPlannedDuration()));
         task.setActualDuration(getDuration(row.getActualDuration()));
         task.setDuration(getDuration(row.getAtCompletionDuration()));

         // ActualCost and RemainingCost will be set when we resolve the resource assignments
         task.setActualCost(NumberHelper.DOUBLE_ZERO);
         task.setRemainingCost(NumberHelper.DOUBLE_ZERO);
         task.setBaselineCost(NumberHelper.DOUBLE_ZERO);

         task.setConstraintDate(row.getPrimaryConstraintDate());
         task.setConstraintType(CONSTRAINT_TYPE_MAP.get(row.getPrimaryConstraintType()));
         task.setActualStart(row.getActualStartDate());
         task.setActualFinish(row.getActualFinishDate());
         task.setLateStart(row.getRemainingLateStartDate());
         task.setLateFinish(row.getRemainingLateFinishDate());
         task.setEarlyStart(row.getRemainingEarlyStartDate());
         task.setEarlyFinish(row.getRemainingEarlyFinishDate());
         task.setBaselineStart(row.getPlannedStartDate());
         task.setBaselineFinish(row.getPlannedFinishDate());

         task.setPriority(PRIORITY_MAP.get(row.getLevelingPriority()));
         task.setCreateDate(row.getCreateDate());
         task.setText(1, row.getId());

         task.setMilestone(BooleanHelper.getBoolean(MILESTONE_MAP.get(row.getType())));
         task.setCritical(task.getEarlyStart() != null && task.getLateStart() != null && !(task.getLateStart().compareTo(task.getEarlyStart()) > 0));

         Integer calId = row.getCalendarObjectId();
         ProjectCalendar cal = m_calMap.get(calId);
         task.setCalendar(cal);

         task.setStart(row.getStartDate());
         task.setFinish(row.getFinishDate());

         populateField(task, TaskField.WORK, TaskField.BASELINE_WORK, TaskField.ACTUAL_WORK);

         readUDFTypes(task, row.getUDF());

         m_eventManager.fireTaskReadEvent(task);
      }

      updateStructure();
   }

   /**
    * Populates a field based on baseline and actual values.
    *
    * @param container field container
    * @param target target field
    * @param baseline baseline field
    * @param actual actual field
    */
   private void populateField(FieldContainer container, FieldType target, FieldType baseline, FieldType actual)
   {
      Object value = container.getCachedValue(actual);
      if (value == null)
      {
         value = container.getCachedValue(baseline);
      }
      container.set(target, value);
   }

   /**
    * Iterates through the tasks setting the correct
    * outline level and ID values.
    */
   private void updateStructure()
   {
      int id = 1;
      Integer outlineLevel = Integer.valueOf(1);
      for (Task task : m_projectFile.getChildTasks())
      {
         id = updateStructure(id, task, outlineLevel);
      }
   }

   /**
    * Iterates through the tasks setting the correct
    * outline level and ID values.
    *
    * @param id current ID value
    * @param task current task
    * @param outlineLevel current outline level
    * @return next ID value
    */
   private int updateStructure(int id, Task task, Integer outlineLevel)
   {
      task.setID(Integer.valueOf(id++));
      task.setOutlineLevel(outlineLevel);
      outlineLevel = Integer.valueOf(outlineLevel.intValue() + 1);
      for (Task childTask : task.getChildTasks())
      {
         id = updateStructure(id, childTask, outlineLevel);
      }
      return id;
   }

   /**
    * Process predecessors.
    *
    * @param project xml container
    */
   private void processPredecessors(ProjectType project)
   {
      for (RelationshipType row : project.getRelationship())
      {

         Task currentTask = m_projectFile.getTaskByUniqueID(mapTaskID(row.getSuccessorActivityObjectId()));
         Task predecessorTask = m_projectFile.getTaskByUniqueID(mapTaskID(row.getPredecessorActivityObjectId()));
         if (currentTask != null && predecessorTask != null)
         {
            RelationType type = RELATION_TYPE_MAP.get(row.getType());
            Duration lag = getDuration(row.getLag());
            Relation relation = currentTask.addPredecessor(predecessorTask, type, lag);
            m_eventManager.fireRelationReadEvent(relation);
         }
      }
   }

   /**
    * Process resource assignments.
    *
    * @param project xml container
    */
   private void processAssignments(ProjectType project)
   {
      List<ResourceAssignmentType> assignments = project.getResourceAssignment();
      for (ResourceAssignmentType row : assignments)
      {
         Task task = m_projectFile.getTaskByUniqueID(mapTaskID(row.getActivityObjectId()));
         Resource resource = m_projectFile.getResourceByUniqueID(row.getResourceObjectId());
         if (task != null && resource != null)
         {
            ResourceAssignment assignment = task.addResourceAssignment(resource);

            assignment.setUniqueID(row.getObjectId());
            assignment.setRemainingWork(getDuration(row.getRemainingUnits()));
            assignment.setBaselineWork(getDuration(row.getPlannedUnits()));
            assignment.setActualWork(getDuration(row.getActualUnits()));
            assignment.setRemainingCost(row.getRemainingCost());
            assignment.setBaselineCost(row.getPlannedCost());
            assignment.setActualCost(row.getActualCost());
            assignment.setActualStart(row.getActualStartDate());
            assignment.setActualFinish(row.getActualFinishDate());
            assignment.setBaselineStart(row.getPlannedStartDate());
            assignment.setBaselineFinish(row.getPlannedFinishDate());
            assignment.setGUID(DatatypeConverter.parseUUID(row.getGUID()));

            task.setActualCost(Double.valueOf(NumberHelper.getDouble(task.getActualCost()) + NumberHelper.getDouble(assignment.getActualCost())));
            task.setRemainingCost(Double.valueOf(NumberHelper.getDouble(task.getRemainingCost()) + NumberHelper.getDouble(assignment.getRemainingCost())));
            task.setBaselineCost(Double.valueOf(NumberHelper.getDouble(task.getBaselineCost()) + NumberHelper.getDouble(assignment.getBaselineCost())));

            populateField(assignment, AssignmentField.WORK, AssignmentField.BASELINE_WORK, AssignmentField.ACTUAL_WORK);
            populateField(assignment, AssignmentField.COST, AssignmentField.BASELINE_COST, AssignmentField.ACTUAL_COST);
            populateField(assignment, AssignmentField.START, AssignmentField.BASELINE_START, AssignmentField.ACTUAL_START);
            populateField(assignment, AssignmentField.FINISH, AssignmentField.BASELINE_FINISH, AssignmentField.ACTUAL_FINISH);

            readUDFTypes(assignment, row.getUDF());

            m_eventManager.fireAssignmentReadEvent(assignment);
         }
      }
   }

   /**
    * Extracts a duration from a JAXBElement instance.
    *
    * @param duration duration expressed in hours
    * @return duration instance
    */
   private Duration getDuration(Double duration)
   {
      Duration result = null;

      if (duration != null)
      {
         result = Duration.getInstance(NumberHelper.getDouble(duration), TimeUnit.HOURS);
      }

      return result;
   }

   /**
    * The end of a Primavera time range finishes on the last minute
    * of the period, so a range of 12:00 -> 13:00 is represented by
    * Primavera as 12:00 -> 12:59.
    *
    * @param date Primavera end time
    * @return date MPXJ end time
    */
   private Date getEndTime(Date date)
   {
      return new Date(date.getTime() + 60000);
   }

   /**
    * Reverse the effects of PrimaveraPMFileWriter.getPercentage().
    *
    * @param n percentage value to convert
    * @return percentage value usable by MPXJ
    */
   private Number reversePercentage(Double n)
   {
      return n == null ? null : NumberHelper.getDouble(n.doubleValue() * 100.0);
   }

   /**
    * Write user defined types to the PMXML file.
    *
    * @param mpxj parent object
    * @param udfs user defined fields
    * @author lsong
    */
   private void readUDFTypes(ProjectEntityWithUniqueID mpxj, List<UDFAssignmentType> udfs)
   {
      CustomFieldContainer customFields = m_projectFile.getCustomFields();
      for (UDFAssignmentType udf : udfs)
      {
         customFields.registerAliasValue((String) customFields.getCustomFieldValueItemByUniqueID(udf.getTypeObjectId()).getValue(), mpxj.getUniqueID(), udf);
      }
   }

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
         CONTEXT = JAXBContext.newInstance("net.sf.mpxj.primavera.schema", PrimaveraPMFileReader.class.getClassLoader());
      }

      catch (JAXBException ex)
      {
         CONTEXT_EXCEPTION = ex;
         CONTEXT = null;
      }
   }

   /**
    * Deals with the case where we have had to map a task ID to a new value.
    *
    * @param id task ID from database
    * @return mapped task ID
    */
   private Integer mapTaskID(Integer id)
   {
      Integer mappedID = m_clashMap.get(id);
      if (mappedID == null)
      {
         mappedID = id;
      }
      return (mappedID);
   }

   private ProjectFile m_projectFile;
   private EventManager m_eventManager;
   private List<ProjectListener> m_projectListeners;
   private Map<Integer, Integer> m_clashMap = new HashMap<Integer, Integer>();
   private Map<Integer, ProjectCalendar> m_calMap = new HashMap<Integer, ProjectCalendar>();

   private static final Charset UTF8 = Charset.forName("UTF8");
   private static final int NAMESPACE_SCOPE = 512;
   private static final String NAMESPACE_REGEX = "xmlns=\\\".*\\\"";
   private static final String NAMESPACE_REPLACEMENT = "xmlns=\"http://xmlns.oracle.com/Primavera/P6/V8.3/API/BusinessObjects\"";

   private static final Map<String, net.sf.mpxj.ResourceType> RESOURCE_TYPE_MAP = new HashMap<String, net.sf.mpxj.ResourceType>();
   static
   {
      RESOURCE_TYPE_MAP.put(null, net.sf.mpxj.ResourceType.WORK);
      RESOURCE_TYPE_MAP.put("Labor", net.sf.mpxj.ResourceType.WORK);
      RESOURCE_TYPE_MAP.put("Material", net.sf.mpxj.ResourceType.MATERIAL);
      RESOURCE_TYPE_MAP.put("Nonlabor", net.sf.mpxj.ResourceType.MATERIAL);
   }

   private static final Map<String, ConstraintType> CONSTRAINT_TYPE_MAP = new HashMap<String, ConstraintType>();
   static
   {
      CONSTRAINT_TYPE_MAP.put("Start On", ConstraintType.MUST_START_ON);
      CONSTRAINT_TYPE_MAP.put("Start On or Before", ConstraintType.START_NO_LATER_THAN);
      CONSTRAINT_TYPE_MAP.put("Start On or After", ConstraintType.START_NO_EARLIER_THAN);
      CONSTRAINT_TYPE_MAP.put("Finish On", ConstraintType.MUST_FINISH_ON);
      CONSTRAINT_TYPE_MAP.put("Finish On or Before", ConstraintType.FINISH_NO_LATER_THAN);
      CONSTRAINT_TYPE_MAP.put("Finish On or After", ConstraintType.FINISH_NO_EARLIER_THAN);
      CONSTRAINT_TYPE_MAP.put("As Late As Possible", ConstraintType.AS_LATE_AS_POSSIBLE);
      CONSTRAINT_TYPE_MAP.put("Mandatory Start", ConstraintType.MUST_START_ON);
      CONSTRAINT_TYPE_MAP.put("Mandatory Finish", ConstraintType.MUST_FINISH_ON);
   }

   private static final Map<String, Priority> PRIORITY_MAP = new HashMap<String, Priority>();
   static
   {
      PRIORITY_MAP.put("Top", Priority.getInstance(Priority.HIGHEST));
      PRIORITY_MAP.put("High", Priority.getInstance(Priority.HIGH));
      PRIORITY_MAP.put("Normal", Priority.getInstance(Priority.MEDIUM));
      PRIORITY_MAP.put("Low", Priority.getInstance(Priority.LOW));
      PRIORITY_MAP.put("Lowest", Priority.getInstance(Priority.LOWEST));
   }

   private static final Map<String, RelationType> RELATION_TYPE_MAP = new HashMap<String, RelationType>();
   static
   {
      RELATION_TYPE_MAP.put("Finish to Start", RelationType.FINISH_START);
      RELATION_TYPE_MAP.put("Finish to Finish", RelationType.FINISH_FINISH);
      RELATION_TYPE_MAP.put("Start to Start", RelationType.START_START);
      RELATION_TYPE_MAP.put("Start to Finish", RelationType.START_FINISH);
   }

   private static final Map<String, Day> DAY_MAP = new HashMap<String, Day>();
   static
   {
      DAY_MAP.put("Monday", Day.MONDAY);
      DAY_MAP.put("Tuesday", Day.TUESDAY);
      DAY_MAP.put("Wednesday", Day.WEDNESDAY);
      DAY_MAP.put("Thursday", Day.THURSDAY);
      DAY_MAP.put("Friday", Day.FRIDAY);
      DAY_MAP.put("Saturday", Day.SATURDAY);
      DAY_MAP.put("Sunday", Day.SUNDAY);
   }

   private static final Map<String, Boolean> MILESTONE_MAP = new HashMap<String, Boolean>();
   static
   {
      MILESTONE_MAP.put("Task Dependent", Boolean.FALSE);
      MILESTONE_MAP.put("Resource Dependent", Boolean.FALSE);
      MILESTONE_MAP.put("Level of Effort", Boolean.FALSE);
      MILESTONE_MAP.put("Start Milestone", Boolean.TRUE);
      MILESTONE_MAP.put("Finish Milestone", Boolean.TRUE);
      MILESTONE_MAP.put("WBS Summary", Boolean.FALSE);
   }
}
