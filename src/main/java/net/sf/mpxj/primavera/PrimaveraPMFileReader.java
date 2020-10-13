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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.poi.util.ReplacingInputStream;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import net.sf.mpxj.AccrueType;
import net.sf.mpxj.ActivityCode;
import net.sf.mpxj.ActivityCodeContainer;
import net.sf.mpxj.ActivityCodeValue;
import net.sf.mpxj.AssignmentField;
import net.sf.mpxj.ConstraintType;
import net.sf.mpxj.CostAccount;
import net.sf.mpxj.CostAccountContainer;
import net.sf.mpxj.CustomFieldContainer;
import net.sf.mpxj.DateRange;
import net.sf.mpxj.Day;
import net.sf.mpxj.DayType;
import net.sf.mpxj.Duration;
import net.sf.mpxj.EventManager;
import net.sf.mpxj.ExpenseCategory;
import net.sf.mpxj.ExpenseCategoryContainer;
import net.sf.mpxj.ExpenseItem;
import net.sf.mpxj.FieldContainer;
import net.sf.mpxj.FieldType;
import net.sf.mpxj.FieldTypeClass;
import net.sf.mpxj.MPXJException;
import net.sf.mpxj.Priority;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectCalendarException;
import net.sf.mpxj.ProjectCalendarHours;
import net.sf.mpxj.ProjectConfig;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ProjectProperties;
import net.sf.mpxj.Relation;
import net.sf.mpxj.RelationType;
import net.sf.mpxj.Resource;
import net.sf.mpxj.ResourceAssignment;
import net.sf.mpxj.ResourceField;
import net.sf.mpxj.Task;
import net.sf.mpxj.TaskField;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.common.BooleanHelper;
import net.sf.mpxj.common.DateHelper;
import net.sf.mpxj.common.NumberHelper;
import net.sf.mpxj.common.UnmarshalHelper;
import net.sf.mpxj.primavera.schema.APIBusinessObjects;
import net.sf.mpxj.primavera.schema.ActivityCodeType;
import net.sf.mpxj.primavera.schema.ActivityCodeTypeType;
import net.sf.mpxj.primavera.schema.ActivityExpenseType;
import net.sf.mpxj.primavera.schema.ActivityType;
import net.sf.mpxj.primavera.schema.CalendarType;
import net.sf.mpxj.primavera.schema.CalendarType.HolidayOrExceptions;
import net.sf.mpxj.primavera.schema.CalendarType.HolidayOrExceptions.HolidayOrException;
import net.sf.mpxj.primavera.schema.CalendarType.StandardWorkWeek;
import net.sf.mpxj.primavera.schema.CalendarType.StandardWorkWeek.StandardWorkHours;
import net.sf.mpxj.primavera.schema.CodeAssignmentType;
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
import net.sf.mpxj.reader.AbstractProjectStreamReader;

/**
 * This class creates a new ProjectFile instance by reading a Primavera PM file.
 */
public final class PrimaveraPMFileReader extends AbstractProjectStreamReader
{
   /**
    * Set the ID of the project to be read.
    *
    * @param projectID project ID
    */
   public void setProjectID(int projectID)
   {
      m_projectID = Integer.valueOf(projectID);
   }

   /**
    * Returns true if the WBS attribute of a summary task
    * contains a dot separated list representing the WBS hierarchy.
    *
    * @return true if WBS attribute is a hierarchy
    */
   public boolean getWbsIsFullPath()
   {
      return m_wbsIsFullPath;
   }

   /**
    * Sets a flag indicating if the WBS attribute of a summary task
    * contains a dot separated list representing the WBS hierarchy.
    *
    * @param wbsIsFullPath true if WBS attribute is a hierarchy
    */
   public void setWbsIsFullPath(boolean wbsIsFullPath)
   {
      m_wbsIsFullPath = wbsIsFullPath;
   }

   /**
    * Retrieve a flag indicating if, when using `realAll` to retrieve all
    * projects from a file, cross project relations should be linked together.
    *
    * @return true if cross project relations should be linked
    */
   public boolean getLinkCrossProjectRelations()
   {
      return m_linkCrossProjectRelations;
   }

   /**
    * Sets a flag indicating if, when using `realAll` to retrieve all
    * projects from a file, cross project relations should be linked together.
    *
    * @param linkCrossProjectRelations true if cross project relations should be linked
    */
   public void setLinkCrossProjectRelations(boolean linkCrossProjectRelations)
   {
      m_linkCrossProjectRelations = linkCrossProjectRelations;
   }

   /**
    * Populates a Map instance representing the IDs and names of
    * projects available in the current file.
    *
    * @param is input stream used to read XER file
    * @return Map instance containing ID and name pairs
    * @throws MPXJException
    */
   public Map<Integer, String> listProjects(InputStream is) throws MPXJException
   {
      APIBusinessObjects apibo = processFile(is);
      List<ProjectType> projects = apibo.getProject();

      Map<Integer, String> result = new HashMap<>();
      projects.forEach(p -> result.put(p.getObjectId(), p.getName()));
      return result;
   }

   /**
    * {@inheritDoc}
    */
   @Override public ProjectFile read(InputStream is) throws MPXJException
   {
      ProjectFile project = null;
      // Using readAll ensures that cross project relations can be included if required
      List<ProjectFile> projects = readAll(is);
      if (!projects.isEmpty())
      {
         if (m_projectID == null)
         {
            project = projects.get(0);
         }
         else
         {
            String uniqueID = m_projectID.toString();
            project = projects.stream().filter(p -> uniqueID.equals(p.getProjectProperties().getUniqueID())).findFirst().orElse(null);

         }
      }

      return project;
   }

   /**
    * This is a convenience method which allows all projects in a
    * PMXML file to be read in a single pass. External relationships
    * are not linked.
    *
    * @param is input stream
    * @return list of ProjectFile instances
    * @throws MPXJException
    */
   @Override public List<ProjectFile> readAll(InputStream is) throws MPXJException
   {
      APIBusinessObjects apibo = processFile(is);

      List<ProjectType> projects = apibo.getProject();
      List<ProjectFile> result = new ArrayList<>(projects.size());
      m_externalRelations = new ArrayList<>();
      projects.forEach(project -> result.add(read(apibo, project)));

      // Sort to ensure exported project is first
      result.sort((o1, o2) -> Boolean.compare(o2.getProjectProperties().getExportFlag(), o1.getProjectProperties().getExportFlag()));

      if (m_linkCrossProjectRelations)
      {
         for (ExternalRelation externalRelation : m_externalRelations)
         {
            Task externalTask = findTaskInProjects(result, externalRelation.externalTaskUniqueID());
            if (externalTask != null)
            {
               Task successor;
               Task predecessor;

               if (externalRelation.getPredecessor())
               {
                  successor = externalRelation.getTargetTask();
                  predecessor = externalTask;
               }
               else
               {
                  successor = externalTask;
                  predecessor = externalRelation.getTargetTask();
               }

               Relation relation = successor.addPredecessor(predecessor, externalRelation.getType(), externalRelation.getLag());
               relation.setUniqueID(externalRelation.getUniqueID());
            }
         }
      }

      m_externalRelations = null;

      return result;
   }

   /**
    * This is a convenience method which allows all projects in a
    * PMXML file to be read in a single pass.
    *
    * @param is input stream
    * @param linkCrossProjectRelations add Relation links that cross ProjectFile boundaries
    * @return list of ProjectFile instances
    * @deprecated use setLinkCrossProjectRelations(flag) and readAll(is) instead
    */
   @Deprecated public List<ProjectFile> readAll(InputStream is, boolean linkCrossProjectRelations) throws MPXJException
   {
      m_linkCrossProjectRelations = linkCrossProjectRelations;
      return readAll(is);
   }

   /**
    * Find a task by unique ID across multiple projects.
    *
    * @param projects list of projects
    * @param uniqueID unique ID to find
    * @return requested task, or null if the task can't be found
    */
   private Task findTaskInProjects(List<ProjectFile> projects, Integer uniqueID)
   {
      Task result = null;

      // we could aggregate the project task id maps but that's likely more work than just looping through the projects
      for (ProjectFile proj : projects)
      {
         result = proj.getTaskByUniqueID(uniqueID);
         if (result != null)
         {
            break;
         }
      }
      return result;
   }

   /**
    * Parse the PMXML file.
    *
    * @param stream PMXML file
    * @return APIBusinessObjects instance
    */
   private APIBusinessObjects processFile(InputStream stream) throws MPXJException
   {
      try
      {
         if (CONTEXT == null)
         {
            throw CONTEXT_EXCEPTION;
         }

         return (APIBusinessObjects) UnmarshalHelper.unmarshal(CONTEXT, configureInputSource(stream), new NamespaceFilter(), false);
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
   }

   private ProjectFile read(APIBusinessObjects apibo, ProjectType project)
   {
      try
      {
         m_clashMap = new HashMap<>();
         m_activityCodeMap = new HashMap<>();
         m_taskUdfCounters = new UserFieldCounters();
         m_resourceUdfCounters = new UserFieldCounters();
         m_assignmentUdfCounters = new UserFieldCounters();
         m_fieldTypeMap = new HashMap<>();

         m_projectFile = new ProjectFile();
         m_eventManager = m_projectFile.getEventManager();

         ProjectConfig config = m_projectFile.getProjectConfig();
         config.setAutoTaskUniqueID(false);
         config.setAutoResourceUniqueID(false);
         config.setAutoCalendarUniqueID(false);
         config.setAutoAssignmentUniqueID(false);
         config.setAutoWBS(false);

         m_projectFile.getProjectProperties().setFileApplication("Primavera");
         m_projectFile.getProjectProperties().setFileType("PMXML");

         CustomFieldContainer fields = m_projectFile.getCustomFields();
         TASK_FIELD_ALIASES.forEach((k, v) -> fields.getCustomField(k).setAlias(v).setUserDefined(false));

         addListenersToProject(m_projectFile);

         processProjectUDFs(apibo);
         processExpenseCategories(apibo);
         processCostAccounts(apibo);
         processProjectProperties(apibo, project);
         processActivityCodes(apibo, project);
         processCalendars(apibo, project);
         processResources(apibo);
         processTasks(project);
         processPredecessors(project);
         processAssignments(project);
         processExpenseItems(project);

         m_projectFile.updateStructure();

         //
         // Ensure that the unique ID counters are correct
         //
         config.updateUniqueCounters();

         return (m_projectFile);
      }

      finally
      {
         m_projectFile = null;
         m_clashMap = null;
         m_activityCodeMap = null;
         m_taskUdfCounters = null;
         m_resourceUdfCounters = null;
         m_assignmentUdfCounters = null;
         m_fieldTypeMap = null;
      }
   }

   /**
    * Normally we'd just create an InputSource instance directly from
    * the input stream. Unfortunately P6 doesn't seem to filter out
    * characters which are invalid for XML or not encoded correctly
    * when it writes PMXML files. This method tries to identify the
    * encoding claimed in the XML header and use this to a
    * PrimaveraInputStreamReader which can ignore these invalid characters.
    *
    * @param stream InputStream instance
    * @return InputSource instance
    */
   private InputSource configureInputSource(InputStream stream) throws IOException
   {
      int bufferSize = 512;
      BufferedInputStream bis = new BufferedInputStream(stream);
      bis.mark(bufferSize);
      byte[] buffer = new byte[bufferSize];
      bis.read(buffer);
      bis.reset();

      // Handle trailing nul character following HTML content expressed as &#0;
      InputStream ris = new ReplacingInputStream(bis, "&lt;/HTML&gt;&#0;", "&lt;/HTML&gt;");

      InputSource result;
      Matcher matcher = ENCODING_PATTERN.matcher(new String(buffer));
      if (matcher.find())
      {
         result = new InputSource(new PrimaveraInputStreamReader(ris, matcher.group(1)));
      }
      else
      {
         result = new InputSource(ris);
      }

      return result;
   }

   /**
    * Process UDF definitions.
    *
    * @param apibo top level object
    */
   private void processProjectUDFs(APIBusinessObjects apibo)
   {
      for (UDFTypeType udf : apibo.getUDFType())
      {
         processUDF(udf);
      }
   }

   /**
    * Process an individual UDF.
    *
    * @param udf UDF definition
    */
   private void processUDF(UDFTypeType udf)
   {
      FieldTypeClass fieldType = FIELD_TYPE_MAP.get(udf.getSubjectArea());
      if (fieldType != null)
      {
         UserFieldDataType dataType = UserFieldDataType.getInstanceFromXmlName(udf.getDataType());
         String name = udf.getTitle();
         FieldType field = addUserDefinedField(fieldType, dataType, name);
         if (field != null)
         {
            m_fieldTypeMap.put(udf.getObjectId(), field);
         }
      }
   }

   /**
    * Map the Primavera UDF to a custom field.
    *
    * @param fieldType parent object type
    * @param dataType UDF data type
    * @param name UDF name
    * @return FieldType instance
    */
   private FieldType addUserDefinedField(FieldTypeClass fieldType, UserFieldDataType dataType, String name)
   {
      FieldType field = null;

      try
      {
         switch (fieldType)
         {
            case TASK:
            {
               do
               {
                  field = m_taskUdfCounters.nextField(TaskField.class, dataType);
               }
               while (TASK_FIELD_ALIASES.containsKey(field));

               m_projectFile.getCustomFields().getCustomField(field).setAlias(name);

               break;
            }

            case RESOURCE:
            {
               field = m_resourceUdfCounters.nextField(ResourceField.class, dataType);
               m_projectFile.getCustomFields().getCustomField(field).setAlias(name);
               break;
            }

            case ASSIGNMENT:
            {
               field = m_assignmentUdfCounters.nextField(AssignmentField.class, dataType);
               m_projectFile.getCustomFields().getCustomField(field).setAlias(name);
               break;
            }

            default:
            {
               break;
            }
         }
      }

      catch (Exception ex)
      {
         //
         // SF#227: If we get an exception thrown here... it's likely that
         // we've run out of user defined fields, for example
         // there are only 30 TEXT fields. We'll ignore this: the user
         // defined field won't be mapped to an alias, so we'll
         // ignore it when we read in the values.
         //
      }

      return field;
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
      properties.setUniqueID(project.getObjectId() == null ? null : project.getObjectId().toString());
      properties.setExportFlag(!BooleanHelper.getBoolean(project.isExternal()));

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
    * Process activity code data.
    *
    * @param apibo global activity code data
    * @param project project-specific activity code data
    */
   private void processActivityCodes(APIBusinessObjects apibo, ProjectType project)
   {
      ActivityCodeContainer container = m_projectFile.getActivityCodes();
      Map<Integer, ActivityCode> map = new HashMap<>();

      List<ActivityCodeTypeType> types = new ArrayList<>();
      types.addAll(apibo.getActivityCodeType());
      types.addAll(project.getActivityCodeType());

      for (ActivityCodeTypeType type : types)
      {
         ActivityCode code = new ActivityCode(type.getObjectId(), type.getName());
         container.add(code);
         map.put(code.getUniqueID(), code);
      }

      List<ActivityCodeType> typeValues = new ArrayList<>();
      typeValues.addAll(apibo.getActivityCode());
      typeValues.addAll(project.getActivityCode());

      for (ActivityCodeType typeValue : typeValues)
      {
         ActivityCode code = map.get(typeValue.getCodeTypeObjectId());
         if (code != null)
         {
            ActivityCodeValue value = code.addValue(typeValue.getObjectId(), typeValue.getCodeValue(), typeValue.getDescription());
            m_activityCodeMap.put(value.getUniqueID(), value);
         }
      }

      for (ActivityCodeType typeValue : typeValues)
      {
         ActivityCodeValue child = m_activityCodeMap.get(typeValue.getObjectId());
         ActivityCodeValue parent = m_activityCodeMap.get(typeValue.getParentObjectId());
         if (parent != null && child != null)
         {
            child.setParent(parent);
         }
      }
   }

   /**
    * Process expense categories.
    * 
    * @param apibo top level object
    */
   private void processExpenseCategories(APIBusinessObjects apibo)
   {
      ExpenseCategoryContainer container = m_projectFile.getExpenseCategories();
      apibo.getExpenseCategory().forEach(c -> container.add(new ExpenseCategory(c.getObjectId(), c.getName(), c.getSequenceNumber())));
   }

   /**
    * Process cost accounts.
    * 
    * @param apibo top level object
    */
   private void processCostAccounts(APIBusinessObjects apibo)
   {
      CostAccountContainer container = m_projectFile.getCostAccounts();
      apibo.getCostAccount().forEach(c -> container.add(new CostAccount(c.getObjectId(), c.getId(), c.getName(), c.getDescription(), c.getSequenceNumber())));
      apibo.getCostAccount().forEach(c -> container.getByUniqueID(c.getObjectId()).setParent(container.getByUniqueID(c.getParentObjectId())));
   }

   /**
    * Process expense items.
    * 
    * @param project parent project
    */
   private void processExpenseItems(ProjectType project)
   {
      for (ActivityExpenseType item : project.getActivityExpense())
      {
         Task task = m_projectFile.getTaskByUniqueID(item.getActivityObjectId());
         if (task != null)
         {
            List<ExpenseItem> items = task.getExpenseItems();
            if (items == null)
            {
               items = new ArrayList<>();
               task.setExpenseItems(items);
            }

            ExpenseItem ei = new ExpenseItem(task);
            items.add(ei);

            ei.setAccount(m_projectFile.getCostAccounts().getByUniqueID(item.getCostAccountObjectId()));
            ei.setAccrueType(ACCRUE_TYPE_MAP.get(item.getAccrualType()));
            ei.setActualCost(item.getActualCost());
            ei.setActualUnits(item.getActualUnits());
            ei.setAtCompletionCost(item.getAtCompletionCost());
            ei.setAtCompletionUnits(item.getAtCompletionUnits());
            ei.setAutoComputeActuals(BooleanHelper.getBoolean(item.isAutoComputeActuals()));
            ei.setCategory(m_projectFile.getExpenseCategories().getByUniqueID(item.getExpenseCategoryObjectId()));
            ei.setDescription(item.getExpenseDescription());
            ei.setDocumentNumber(item.getDocumentNumber());
            ei.setName(item.getExpenseItem());
            ei.setPlannedCost(item.getPlannedCost());
            ei.setPlannedUnits(item.getPlannedUnits());
            ei.setPricePerUnit(item.getPricePerUnit());
            ei.setRemainingCost(item.getRemainingCost());
            ei.setRemainingUnits(item.getRemainingUnits());
            ei.setUniqueID(item.getObjectId());
            ei.setUnitOfMeasure(item.getUnitOfMeasure());
            ei.setVendor(item.getVendor());
         }
      }
   }

   /**
    * Process project calendars.
    *
    * @param apibo file data
    * @param project current project data
    */
   private void processCalendars(APIBusinessObjects apibo, ProjectType project)
   {
      List<CalendarType> calendars = new ArrayList<>(apibo.getCalendar());
      calendars.addAll(project.getCalendar());

      //
      // First pass: read calendar definitions
      //
      Map<ProjectCalendar, Integer> baseCalendarMap = new HashMap<>();
      for (CalendarType row : calendars)
      {
         ProjectCalendar calendar = processCalendar(row);
         Integer baseCalendarID = row.getBaseCalendarObjectId();
         if (baseCalendarID != null)
         {
            baseCalendarMap.put(calendar, baseCalendarID);
         }
      }

      //
      // Second pass: create calendar hierarchy
      //
      for (Map.Entry<ProjectCalendar, Integer> entry : baseCalendarMap.entrySet())
      {
         ProjectCalendar baseCalendar = m_projectFile.getCalendarByUniqueID(entry.getValue());
         if (baseCalendar != null)
         {
            entry.getKey().setParent(baseCalendar);
         }
      }
   }

   /**
    * Process data for an individual calendar.
    *
    * @param row calendar data
    * @return ProjectCalendar instance
    */
   private ProjectCalendar processCalendar(CalendarType row)
   {
      ProjectCalendar calendar = m_projectFile.addCalendar();
      Integer id = row.getObjectId();
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
               if (work != null && work.getStart() != null && work.getFinish() != null)
               {
                  pce.addRange(new DateRange(work.getStart(), getEndTime(work.getFinish())));
               }
            }
         }
      }

      return calendar;
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
            ProjectCalendar calendar = m_projectFile.getCalendarByUniqueID(calendarID);
            if (calendar != null)
            {
               if (calendar.isDerived())
               {
                  //
                  // Primavera seems to allow a calendar to be shared between resources
                  // whereas in the MS Project model there is a one-to-one
                  // relationship. If we find a calendar is shared between resources,
                  // take a copy of it so each resource has its own copy.
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
               else
               {
                  //
                  // If the resource is linked to a base calendar, derive
                  // a default calendar from the base calendar.
                  //
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
      Set<Integer> uniqueIDs = new HashSet<>();
      Set<Task> wbsTasks = new HashSet<>();

      //
      // Read WBS entries and create tasks
      //
      Collections.sort(wbs, WBS_ROW_COMPARATOR);

      for (WBSType row : wbs)
      {
         Task task = m_projectFile.addTask();
         Integer uniqueID = row.getObjectId();
         uniqueIDs.add(uniqueID);
         wbsTasks.add(task);

         task.setUniqueID(uniqueID);
         task.setGUID(DatatypeConverter.parseUUID(row.getGUID()));
         task.setName(row.getName());
         task.setBaselineCost(row.getSummaryBaselineTotalCost());
         task.setRemainingCost(row.getSummaryRemainingTotalCost());
         task.setRemainingDuration(getDuration(row.getSummaryRemainingDuration()));
         task.setSummary(true);
         task.setStart(row.getAnticipatedStartDate());
         task.setFinish(row.getAnticipatedFinishDate());
         task.setWBS(row.getCode());
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

            if (m_wbsIsFullPath)
            {
               task.setWBS(parentTask.getWBS() + "." + task.getWBS());
            }

            task.setText(1, task.getWBS());
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
         task.setName(row.getName());
         task.setPercentageComplete(reversePercentage(row.getPercentComplete()));
         task.setRemainingDuration(getDuration(row.getRemainingDuration()));
         task.setActualWork(getDuration(zeroIsNull(row.getActualDuration())));
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
         task.setSecondaryConstraintDate(row.getSecondaryConstraintDate());
         task.setSecondaryConstraintType(CONSTRAINT_TYPE_MAP.get(row.getSecondaryConstraintType()));
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
         task.setText(2, row.getType());
         task.setText(3, row.getStatus());
         task.setNumber(1, row.getPrimaryResourceObjectId());

         task.setMilestone(BooleanHelper.getBoolean(MILESTONE_MAP.get(row.getType())));
         task.setCritical(task.getEarlyStart() != null && task.getLateStart() != null && !(task.getLateStart().compareTo(task.getEarlyStart()) > 0));

         if (parentTask != null)
         {
            task.setWBS(parentTask.getWBS());
         }

         Integer calId = row.getCalendarObjectId();
         ProjectCalendar cal = m_projectFile.getCalendarByUniqueID(calId);
         task.setCalendar(cal);

         task.setStart(row.getStartDate());
         task.setFinish(row.getFinishDate());

         populateField(task, TaskField.START, TaskField.START, TaskField.ACTUAL_START, TaskField.BASELINE_START);
         populateField(task, TaskField.FINISH, TaskField.FINISH, TaskField.ACTUAL_FINISH);
         populateField(task, TaskField.WORK, TaskField.ACTUAL_WORK, TaskField.BASELINE_WORK);

         //
         // We've tried the finish and actual finish fields... but we still have null.
         // P6 itself doesn't export PMXML like this.
         // The sample I have that requires this code appears to have been been generated by Synchro.
         //
         if (task.getFinish() == null)
         {
            //
            // Find the remaining duration, set it to null if it is zero
            //
            Duration duration = task.getRemainingDuration();
            if (duration != null && duration.getDuration() == 0)
            {
               duration = null;
            }

            //
            // If the task hasn't started, or we don't have a usable duration
            // let's just use the baseline finish.
            //
            if (task.getActualStart() == null || duration == null)
            {
               task.setFinish(task.getBaselineFinish());
            }
            else
            {
               //
               // The task has started, let's calculate the finish date using the remaining duration
               // and the "restart" date, which we've put in the baseline start date.
               //
               ProjectCalendar calendar = task.getEffectiveCalendar();
               Date finish = calendar.getDate(task.getBaselineStart(), duration, false);

               //
               // Deal with an oddity where the finish date shows up as the
               // start of work date for the next working day. If we can identify this,
               // wind the date back to the end of the previous working day.
               //
               Date nextWorkStart = calendar.getNextWorkStart(finish);
               if (DateHelper.compare(finish, nextWorkStart) == 0)
               {
                  finish = calendar.getPreviousWorkFinish(finish);
               }
               task.setFinish(finish);
            }
         }

         readUDFTypes(task, row.getUDF());
         readActivityCodes(task, row.getCode());

         m_eventManager.fireTaskReadEvent(task);
      }

      new ActivitySorter(TaskField.TEXT1, wbsTasks).sort(m_projectFile);

      updateStructure();
      updateDates();
   }

   /**
    * The Primavera WBS entries we read in as tasks have user-entered start and end dates
    * which aren't calculated or adjusted based on the child task dates. We try
    * to compensate for this by using these user-entered dates as baseline dates, and
    * deriving the planned start, actual start, planned finish and actual finish from
    * the child tasks. This method recursively descends through the tasks to do this.
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
         Date baselineStartDate = parentTask.getBaselineStart();
         Date baselineFinishDate = parentTask.getBaselineFinish();
         Date remainingEarlyStartDate = parentTask.getRemainingEarlyStart();
         Date remainingEarlyFinishDate = parentTask.getRemainingEarlyFinish();
         boolean critical = false;

         for (Task task : parentTask.getChildTasks())
         {
            updateDates(task);

            // the child tasks can have null dates (e.g. for nested wbs elements with no task children) so we
            // still must protect against some children having null dates

            plannedStartDate = DateHelper.min(plannedStartDate, task.getStart());
            plannedFinishDate = DateHelper.max(plannedFinishDate, task.getFinish());
            actualStartDate = DateHelper.min(actualStartDate, task.getActualStart());
            actualFinishDate = DateHelper.max(actualFinishDate, task.getActualFinish());
            earlyStartDate = DateHelper.min(earlyStartDate, task.getEarlyStart());
            earlyFinishDate = DateHelper.max(earlyFinishDate, task.getEarlyFinish());
            remainingEarlyStartDate = DateHelper.min(remainingEarlyStartDate, task.getRemainingEarlyStart());
            remainingEarlyFinishDate = DateHelper.max(remainingEarlyFinishDate, task.getRemainingEarlyFinish());
            lateStartDate = DateHelper.min(lateStartDate, task.getLateStart());
            lateFinishDate = DateHelper.max(lateFinishDate, task.getLateFinish());
            baselineStartDate = DateHelper.min(baselineStartDate, task.getBaselineStart());
            baselineFinishDate = DateHelper.max(baselineFinishDate, task.getBaselineFinish());

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
         parentTask.setRemainingEarlyStart(remainingEarlyStartDate);
         parentTask.setRemainingEarlyFinish(remainingEarlyFinishDate);
         parentTask.setLateStart(lateStartDate);
         parentTask.setLateFinish(lateFinishDate);
         parentTask.setBaselineStart(baselineStartDate);
         parentTask.setBaselineFinish(baselineFinishDate);

         //
         // Only if all child tasks have actual finish dates do we
         // set the actual finish date on the parent task.
         //
         if (finished == parentTask.getChildTasks().size())
         {
            parentTask.setActualFinish(actualFinishDate);
         }

         Duration baselineDuration = null;
         if (baselineStartDate != null && baselineFinishDate != null)
         {
            baselineDuration = m_projectFile.getDefaultCalendar().getWork(baselineStartDate, baselineFinishDate, TimeUnit.HOURS);
            parentTask.setBaselineDuration(baselineDuration);
         }

         Duration remainingDuration = null;
         if (parentTask.getActualFinish() == null)
         {
            Date startDate = parentTask.getEarlyStart();
            if (startDate == null)
            {
               startDate = baselineStartDate;
            }

            Date finishDate = parentTask.getEarlyFinish();
            if (finishDate == null)
            {
               finishDate = baselineFinishDate;
            }

            if (startDate != null && finishDate != null)
            {
               remainingDuration = m_projectFile.getDefaultCalendar().getWork(startDate, finishDate, TimeUnit.HOURS);
            }
         }
         else
         {
            remainingDuration = Duration.getInstance(0, TimeUnit.HOURS);
         }
         parentTask.setRemainingDuration(remainingDuration);

         if (baselineDuration != null && remainingDuration != null && baselineDuration.getDuration() != 0)
         {
            double durationPercentComplete = ((baselineDuration.getDuration() - remainingDuration.getDuration()) / baselineDuration.getDuration()) * 100.0;
            if (durationPercentComplete < 0)
            {
               durationPercentComplete = 0;
            }
            else
            {
               if (durationPercentComplete > 100)
               {
                  durationPercentComplete = 100;
               }
            }
            parentTask.setPercentageComplete(Double.valueOf(durationPercentComplete));
         }

         parentTask.setCritical(critical);
      }
   }

   /**
    * Populates a field based on baseline and actual values.
    *
    * @param container field container
    * @param target target field
    * @param types fields to test for not-null values
    */
   private void populateField(FieldContainer container, FieldType target, FieldType... types)
   {
      for (FieldType type : types)
      {
         Object value = container.getCachedValue(type);
         if (value != null)
         {
            container.set(target, value);
            break;
         }
      }
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
         Integer predecessorID = row.getPredecessorActivityObjectId();
         Integer successorID = row.getSuccessorActivityObjectId();

         Task successorTask = m_projectFile.getTaskByUniqueID(mapTaskID(successorID));
         Task predecessorTask = m_projectFile.getTaskByUniqueID(mapTaskID(predecessorID));

         RelationType type = RELATION_TYPE_MAP.get(row.getType());
         Duration lag = getDuration(row.getLag());

         if (successorTask != null && predecessorTask != null)
         {
            Relation relation = successorTask.addPredecessor(predecessorTask, type, lag);
            relation.setUniqueID(row.getObjectId());
            m_eventManager.fireRelationReadEvent(relation);
         }
         else
         {
            // If we're missing the predecessor or successor we assume they are external relations
            if (successorTask != null && predecessorTask == null)
            {
               ExternalRelation relation = new ExternalRelation(predecessorID, successorTask, type, lag, true);
               m_externalRelations.add(relation);
               relation.setUniqueID(row.getObjectId());
            }
            else
            {
               if (successorTask == null && predecessorTask != null)
               {
                  ExternalRelation relation = new ExternalRelation(successorID, predecessorTask, type, lag, false);
                  m_externalRelations.add(relation);
                  relation.setUniqueID(row.getObjectId());
               }
            }
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

            populateField(assignment, AssignmentField.WORK, AssignmentField.ACTUAL_WORK, AssignmentField.BASELINE_WORK);
            populateField(assignment, AssignmentField.COST, AssignmentField.ACTUAL_COST, AssignmentField.BASELINE_COST);
            populateField(assignment, AssignmentField.START, AssignmentField.ACTUAL_START, AssignmentField.BASELINE_START);
            populateField(assignment, AssignmentField.FINISH, AssignmentField.ACTUAL_FINISH, AssignmentField.BASELINE_FINISH);

            readUDFTypes(assignment, row.getUDF());

            m_eventManager.fireAssignmentReadEvent(assignment);
         }
      }
   }

   /**
    * Render a zero Double as null.
    *
    * @param value double value
    * @return null if the double value is zero
    */
   private Double zeroIsNull(Double value)
   {
      if (value != null && value.doubleValue() == 0)
      {
         value = null;
      }
      return value;
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
    * Process UDFs for a specific object.
    *
    * @param mpxj field container
    * @param udfs UDF values
    */
   private void readUDFTypes(FieldContainer mpxj, List<UDFAssignmentType> udfs)
   {
      for (UDFAssignmentType udf : udfs)
      {
         FieldType fieldType = m_fieldTypeMap.get(Integer.valueOf(udf.getTypeObjectId()));
         if (fieldType != null)
         {
            mpxj.set(fieldType, getUdfValue(udf));
         }
      }
   }

   /**
    * Retrieve the value of a UDF.
    *
    * @param udf UDF value holder
    * @return UDF value
    */
   private Object getUdfValue(UDFAssignmentType udf)
   {
      if (udf.getCostValue() != null)
      {
         return udf.getCostValue();
      }

      if (udf.getDoubleValue() != null)
      {
         return udf.getDoubleValue();
      }

      if (udf.getFinishDateValue() != null)
      {
         return udf.getFinishDateValue();
      }

      if (udf.getIndicatorValue() != null)
      {
         return udf.getIndicatorValue();
      }

      if (udf.getIntegerValue() != null)
      {
         return udf.getIntegerValue();
      }

      if (udf.getStartDateValue() != null)
      {
         return udf.getStartDateValue();
      }

      if (udf.getTextValue() != null)
      {
         return udf.getTextValue();
      }

      return null;
   }

   /**
    * Read details of any activity codes assigned to this task.
    * @param task parent task
    * @param codes activity code assignments
    */
   private void readActivityCodes(Task task, List<CodeAssignmentType> codes)
   {
      for (CodeAssignmentType assignment : codes)
      {
         ActivityCodeValue code = m_activityCodeMap.get(Integer.valueOf(assignment.getValueObjectId()));
         if (code != null)
         {
            task.addActivityCode(code);
         }
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

   private Integer m_projectID;
   private ProjectFile m_projectFile;
   private EventManager m_eventManager;
   private Map<Integer, Integer> m_clashMap;
   private Map<Integer, ActivityCodeValue> m_activityCodeMap;
   private UserFieldCounters m_taskUdfCounters;
   private UserFieldCounters m_resourceUdfCounters;
   private UserFieldCounters m_assignmentUdfCounters;
   private Map<Integer, FieldType> m_fieldTypeMap;
   private List<ExternalRelation> m_externalRelations;
   private boolean m_wbsIsFullPath = true;
   private boolean m_linkCrossProjectRelations;

   private static final Map<String, net.sf.mpxj.ResourceType> RESOURCE_TYPE_MAP = new HashMap<>();
   static
   {
      RESOURCE_TYPE_MAP.put(null, net.sf.mpxj.ResourceType.WORK);
      RESOURCE_TYPE_MAP.put("Labor", net.sf.mpxj.ResourceType.WORK);
      RESOURCE_TYPE_MAP.put("Material", net.sf.mpxj.ResourceType.MATERIAL);
      RESOURCE_TYPE_MAP.put("Nonlabor", net.sf.mpxj.ResourceType.COST);
   }

   private static final Map<String, ConstraintType> CONSTRAINT_TYPE_MAP = new HashMap<>();
   static
   {
      CONSTRAINT_TYPE_MAP.put("Start On", ConstraintType.MUST_START_ON);
      CONSTRAINT_TYPE_MAP.put("Start On or Before", ConstraintType.START_NO_LATER_THAN);
      CONSTRAINT_TYPE_MAP.put("Start On or After", ConstraintType.START_NO_EARLIER_THAN);
      CONSTRAINT_TYPE_MAP.put("Finish On", ConstraintType.MUST_FINISH_ON);
      CONSTRAINT_TYPE_MAP.put("Finish On or Before", ConstraintType.FINISH_NO_LATER_THAN);
      CONSTRAINT_TYPE_MAP.put("Finish On or After", ConstraintType.FINISH_NO_EARLIER_THAN);
      CONSTRAINT_TYPE_MAP.put("As Late As Possible", ConstraintType.AS_LATE_AS_POSSIBLE);
      CONSTRAINT_TYPE_MAP.put("Mandatory Start", ConstraintType.MANDATORY_START);
      CONSTRAINT_TYPE_MAP.put("Mandatory Finish", ConstraintType.MANDATORY_FINISH);
   }

   private static final Map<String, Priority> PRIORITY_MAP = new HashMap<>();
   static
   {
      PRIORITY_MAP.put("Top", Priority.getInstance(Priority.HIGHEST));
      PRIORITY_MAP.put("High", Priority.getInstance(Priority.HIGH));
      PRIORITY_MAP.put("Normal", Priority.getInstance(Priority.MEDIUM));
      PRIORITY_MAP.put("Low", Priority.getInstance(Priority.LOW));
      PRIORITY_MAP.put("Lowest", Priority.getInstance(Priority.LOWEST));
   }

   private static final Map<String, RelationType> RELATION_TYPE_MAP = new HashMap<>();
   static
   {
      RELATION_TYPE_MAP.put("Finish to Start", RelationType.FINISH_START);
      RELATION_TYPE_MAP.put("Finish to Finish", RelationType.FINISH_FINISH);
      RELATION_TYPE_MAP.put("Start to Start", RelationType.START_START);
      RELATION_TYPE_MAP.put("Start to Finish", RelationType.START_FINISH);
   }

   private static final Map<String, Day> DAY_MAP = new HashMap<>();
   static
   {
      // Current PMXML schema
      DAY_MAP.put("Monday", Day.MONDAY);
      DAY_MAP.put("Tuesday", Day.TUESDAY);
      DAY_MAP.put("Wednesday", Day.WEDNESDAY);
      DAY_MAP.put("Thursday", Day.THURSDAY);
      DAY_MAP.put("Friday", Day.FRIDAY);
      DAY_MAP.put("Saturday", Day.SATURDAY);
      DAY_MAP.put("Sunday", Day.SUNDAY);

      // Older (6.2?) schema
      DAY_MAP.put("1", Day.SUNDAY);
      DAY_MAP.put("2", Day.MONDAY);
      DAY_MAP.put("3", Day.TUESDAY);
      DAY_MAP.put("4", Day.WEDNESDAY);
      DAY_MAP.put("5", Day.THURSDAY);
      DAY_MAP.put("6", Day.FRIDAY);
      DAY_MAP.put("7", Day.SATURDAY);
   }

   private static final Map<String, Boolean> MILESTONE_MAP = new HashMap<>();
   static
   {
      MILESTONE_MAP.put("Task Dependent", Boolean.FALSE);
      MILESTONE_MAP.put("Resource Dependent", Boolean.FALSE);
      MILESTONE_MAP.put("Level of Effort", Boolean.FALSE);
      MILESTONE_MAP.put("Start Milestone", Boolean.TRUE);
      MILESTONE_MAP.put("Finish Milestone", Boolean.TRUE);
      MILESTONE_MAP.put("WBS Summary", Boolean.FALSE);
   }

   private static final Map<String, FieldTypeClass> FIELD_TYPE_MAP = new HashMap<>();
   static
   {
      FIELD_TYPE_MAP.put("Activity", FieldTypeClass.TASK);
      FIELD_TYPE_MAP.put("WBS", FieldTypeClass.TASK);
      FIELD_TYPE_MAP.put("Resource", FieldTypeClass.RESOURCE);
      FIELD_TYPE_MAP.put("Resource Assignment", FieldTypeClass.ASSIGNMENT);
   }

   private static final Map<TaskField, String> TASK_FIELD_ALIASES = new HashMap<>();
   static
   {
      TASK_FIELD_ALIASES.put(TaskField.TEXT1, "Code");
      TASK_FIELD_ALIASES.put(TaskField.TEXT2, "Activity Type");
      TASK_FIELD_ALIASES.put(TaskField.TEXT3, "Status");
      TASK_FIELD_ALIASES.put(TaskField.NUMBER1, "Primary Resource Unique ID");
   }

   private static final Map<String, AccrueType> ACCRUE_TYPE_MAP = new HashMap<>();
   static
   {
      ACCRUE_TYPE_MAP.put("Uniform Over Activity", AccrueType.PRORATED);
      ACCRUE_TYPE_MAP.put("End of Activity", AccrueType.END);
      ACCRUE_TYPE_MAP.put("Start of Activity", AccrueType.START);
   }

   private static final WbsRowComparatorPMXML WBS_ROW_COMPARATOR = new WbsRowComparatorPMXML();

   private static final Pattern ENCODING_PATTERN = Pattern.compile(".*<\\?xml.*encoding=\"([^\"]+)\".*\\?>.*", Pattern.DOTALL);
}
