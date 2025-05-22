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

package org.mpxj.primavera;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.mpxj.BaselineStrategy;
import org.mpxj.Currency;
import org.mpxj.CurrencyContainer;
import org.mpxj.ProjectCode;
import org.mpxj.ProjectCodeContainer;
import org.mpxj.ProjectCodeValue;
import org.mpxj.ProjectFileSharedData;
import org.mpxj.ResourceAssignmentCode;
import org.mpxj.ResourceAssignmentCodeContainer;
import org.mpxj.ResourceAssignmentCodeValue;
import org.mpxj.ResourceCode;
import org.mpxj.ResourceCodeContainer;
import org.mpxj.ResourceCodeValue;
import org.mpxj.RoleCode;
import org.mpxj.RoleCodeContainer;
import org.mpxj.RoleCodeValue;
import org.mpxj.Shift;
import org.mpxj.ShiftContainer;
import org.mpxj.ShiftPeriod;
import org.mpxj.ShiftPeriodContainer;
import org.mpxj.UnitOfMeasure;
import org.mpxj.UnitOfMeasureContainer;
import org.mpxj.common.DayOfWeekHelper;
import org.mpxj.Location;
import org.mpxj.LocationContainer;
import org.mpxj.NotesTopic;
import org.mpxj.Step;
import org.mpxj.LocalTimeRange;
import org.mpxj.UserDefinedField;
import org.mpxj.common.ColorHelper;
import org.mpxj.common.HierarchyHelper;
import org.mpxj.common.InputStreamHelper;
import org.mpxj.common.LocalDateHelper;
import org.mpxj.common.LocalDateTimeHelper;
import org.mpxj.primavera.schema.ActivityStepType;
import org.mpxj.primavera.schema.CostAccountType;
import org.mpxj.primavera.schema.ProjectCodeType;
import org.mpxj.primavera.schema.ProjectCodeTypeType;
import org.mpxj.primavera.schema.ProjectListType;
import org.mpxj.primavera.schema.ResourceAssignmentCodeType;
import org.mpxj.primavera.schema.ResourceAssignmentCodeTypeType;
import org.mpxj.primavera.schema.ResourceCodeType;
import org.mpxj.primavera.schema.ResourceCodeTypeType;
import org.mpxj.primavera.schema.ResourceRoleType;
import org.mpxj.primavera.schema.RoleCodeType;
import org.mpxj.primavera.schema.RoleCodeTypeType;
import org.mpxj.primavera.schema.ShiftPeriodType;
import org.mpxj.primavera.schema.ShiftType;
import org.mpxj.primavera.schema.UnitOfMeasureType;
import org.apache.poi.util.ReplacingInputStream;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import org.mpxj.ActivityCode;
import org.mpxj.ActivityCodeContainer;
import org.mpxj.ActivityCodeValue;
import org.mpxj.AssignmentField;
import org.mpxj.Availability;
import org.mpxj.ChildTaskContainer;
import org.mpxj.CostAccount;
import org.mpxj.CostAccountContainer;
import org.mpxj.CostRateTableEntry;
import org.mpxj.CriticalActivityType;
import java.time.DayOfWeek;
import org.mpxj.Duration;
import org.mpxj.EventManager;
import org.mpxj.ExpenseCategory;
import org.mpxj.ExpenseCategoryContainer;
import org.mpxj.ExpenseItem;
import org.mpxj.FieldContainer;
import org.mpxj.FieldType;
import org.mpxj.FieldTypeClass;
import org.mpxj.HtmlNotes;
import org.mpxj.MPXJException;
import org.mpxj.Notes;
import org.mpxj.ParentNotes;
import org.mpxj.PercentCompleteType;
import org.mpxj.ProjectCalendar;
import org.mpxj.ProjectCalendarException;
import org.mpxj.ProjectCalendarHours;
import org.mpxj.ProjectConfig;
import org.mpxj.ProjectFile;
import org.mpxj.ProjectProperties;
import org.mpxj.Rate;
import org.mpxj.Relation;
import org.mpxj.RelationType;
import org.mpxj.Resource;
import org.mpxj.ResourceAssignment;
import org.mpxj.StructuredNotes;
import org.mpxj.Task;
import org.mpxj.TaskField;
import org.mpxj.TimeUnit;
import org.mpxj.WorkContour;
import org.mpxj.common.BooleanHelper;
import org.mpxj.common.NumberHelper;
import org.mpxj.common.UnmarshalHelper;
import org.mpxj.primavera.schema.APIBusinessObjects;
import org.mpxj.primavera.schema.ActivityCodeType;
import org.mpxj.primavera.schema.ActivityCodeTypeType;
import org.mpxj.primavera.schema.ActivityExpenseType;
import org.mpxj.primavera.schema.ActivityNoteType;
import org.mpxj.primavera.schema.ActivityType;
import org.mpxj.primavera.schema.BaselineProjectType;
import org.mpxj.primavera.schema.CalendarType;
import org.mpxj.primavera.schema.CalendarType.HolidayOrExceptions;
import org.mpxj.primavera.schema.CalendarType.HolidayOrExceptions.HolidayOrException;
import org.mpxj.primavera.schema.CalendarType.StandardWorkWeek;
import org.mpxj.primavera.schema.CalendarType.StandardWorkWeek.StandardWorkHours;
import org.mpxj.primavera.schema.CodeAssignmentType;
import org.mpxj.primavera.schema.CurrencyType;
import org.mpxj.primavera.schema.GlobalPreferencesType;
import org.mpxj.primavera.schema.NotebookTopicType;
import org.mpxj.primavera.schema.ProjectNoteType;
import org.mpxj.primavera.schema.ProjectType;
import org.mpxj.primavera.schema.RelationshipType;
import org.mpxj.primavera.schema.ResourceAssignmentType;
import org.mpxj.primavera.schema.ResourceCurveType;
import org.mpxj.primavera.schema.ResourceCurveValuesType;
import org.mpxj.primavera.schema.ResourceRateType;
import org.mpxj.primavera.schema.ResourceType;
import org.mpxj.primavera.schema.RoleRateType;
import org.mpxj.primavera.schema.RoleType;
import org.mpxj.primavera.schema.ScheduleOptionsType;
import org.mpxj.primavera.schema.UDFAssignmentType;
import org.mpxj.primavera.schema.UDFTypeType;
import org.mpxj.primavera.schema.WBSType;
import org.mpxj.primavera.schema.WorkTimeType;
import org.mpxj.reader.AbstractProjectStreamReader;

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
    * Set the strategy to use when populating baseline fields.
    * The default is the planned dates strategy.
    *
    * @param strategy baseline strategy
    */
   public void setBaselineStrategy(BaselineStrategy strategy)
   {
      m_baselineStrategy = strategy;
   }

   /**
    * Retrieve the strategy to use when populating baseline fields.
    *
    * @return baseline strategy
    */
   public BaselineStrategy getBaselineStrategy()
   {
      return m_baselineStrategy;
   }

   /**
    * Populates a Map instance representing the IDs and names of
    * projects available in the current file.
    *
    * @param is input stream used to read XER file
    * @return Map instance containing ID and name pairs
    */
   public Map<Integer, String> listProjects(InputStream is) throws MPXJException
   {
      APIBusinessObjects apibo = processFile(is);
      List<ProjectType> projects = apibo.getProject();

      Map<Integer, String> result = new HashMap<>();
      projects.forEach(p -> result.put(p.getObjectId(), p.getName()));
      return result;
   }

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
            project = projects.stream().filter(p -> m_projectID.equals(p.getProjectProperties().getUniqueID())).findFirst().orElse(null);
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
    */
   @Override public List<ProjectFile> readAll(InputStream is) throws MPXJException
   {
      APIBusinessObjects apibo = processFile(is);

      List<ProjectType> projects = apibo.getProject();
      List<BaselineProjectType> baselineProjects = apibo.getBaselineProject();
      List<ProjectFile> result = new ArrayList<>(projects.size() + baselineProjects.size());
      m_externalRelations = new ArrayList<>();

      projects.forEach(project -> result.add(read(apibo, project)));
      baselineProjects.forEach(project -> result.add(read(apibo, project)));

      // Sort to ensure exported project is first
      result.sort((o1, o2) -> Boolean.compare(o2.getProjectProperties().getExportFlag(), o1.getProjectProperties().getExportFlag()));

      linkCrossProjectRelations(result);
      populateBaselines(apibo, result);

      m_externalRelations = null;

      return result;
   }

   private void linkCrossProjectRelations(List<ProjectFile> projects)
   {
      if (m_linkCrossProjectRelations)
      {
         for (ExternalRelation externalRelation : m_externalRelations)
         {
            Task externalTask = findTaskInProjects(projects, externalRelation.externalTaskUniqueID());
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

               // We need to ensure that the relation is present in both
               // projects so that predecessors and successors are populated
               // in both projects.

               ProjectFile successorProject = successor.getParentFile();
               successorProject.getRelations().addPredecessor(new Relation.Builder()
                  .predecessorTask(predecessor)
                  .successorTask(successor)
                  .type(externalRelation.getType())
                  .lag(externalRelation.getLag())
                  .uniqueID(externalRelation.getUniqueID())
                  .notes(externalRelation.getNotes()));

               ProjectFile predecessorProject = predecessor.getParentFile();
               predecessorProject.getRelations().addPredecessor(new Relation.Builder()
                  .predecessorTask(predecessor)
                  .successorTask(successor)
                  .type(externalRelation.getType())
                  .lag(externalRelation.getLag())
                  .uniqueID(externalRelation.getUniqueID())
                  .notes(externalRelation.getNotes()));
            }
         }
      }
   }

   private void populateBaselines(APIBusinessObjects apibo, List<ProjectFile> projects)
   {
      if (projects.stream().anyMatch(p -> p.getProjectProperties().getBaselineProjectUniqueID() != null))
      {
         // We have baseline project unique ID values
         populateBaselinesByUniqueID(projects);
      }
      else
      {
         if (apibo.getProjectList() != null && apibo.getProjectList().getProject().stream().anyMatch(p -> !p.getBaselineProject().isEmpty()))
         {
            // We have baselines in the project list
            populateBaselinesByProjectList(apibo, projects);
         }
      }
   }

   private void populateBaselinesByUniqueID(List<ProjectFile> projects)
   {
      Map<Integer, ProjectFile> map = projects.stream().collect(Collectors.toMap(p -> p.getProjectProperties().getUniqueID(), p -> p));
      for (ProjectFile project : projects)
      {
         ProjectFile baseline = map.get(project.getProjectProperties().getBaselineProjectUniqueID());
         if (baseline != null)
         {
            project.setBaseline(baseline);
         }
      }
   }

   private void populateBaselinesByProjectList(APIBusinessObjects apibo, List<ProjectFile> projects)
   {
      Map<Integer, ProjectFile> map = projects.stream().collect(Collectors.toMap(p -> p.getProjectProperties().getUniqueID(), p -> p));
      for (ProjectListType.Project project : apibo.getProjectList().getProject())
      {
         List<ProjectListType.Project.BaselineProject> baselineProjects = project.getBaselineProject();
         if (baselineProjects.isEmpty())
         {
            continue;
         }

         ProjectFile parentProject = map.get(Integer.valueOf(project.getObjectId()));
         if (parentProject == null)
         {
            continue;
         }

         int baselineIndex = 0;
         for (ProjectListType.Project.BaselineProject baseline : baselineProjects)
         {
            ProjectFile baselineProject = map.get(Integer.valueOf(baseline.getObjectId()));
            if (baselineProject == null)
            {
               continue;
            }

            parentProject.setBaseline(baselineProject, baselineIndex++);

            // TODO: allow an arbitrary number of baselines to be captured
            if (baselineIndex > 10)
            {
               break;
            }
         }
      }
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

      catch (ParserConfigurationException | IOException | SAXException | JAXBException ex)
      {
         throw new MPXJException("Failed to parse file", ex);
      }
   }

   private ProjectFile read(APIBusinessObjects apibo, Object projectObject)
   {
      try
      {
         m_activityClashMap = new ClashMap();
         m_roleClashMap = new ClashMap();

         boolean readSharedData = m_shared == null;
         if (m_shared == null)
         {
            m_shared = new ProjectFileSharedData();
         }

         m_projectFile = new ProjectFile(m_shared);
         m_eventManager = m_projectFile.getEventManager();

         ProjectConfig config = m_projectFile.getProjectConfig();
         config.setAutoTaskUniqueID(false);
         config.setAutoResourceUniqueID(false);
         config.setAutoCalendarUniqueID(false);
         config.setAutoAssignmentUniqueID(false);
         config.setAutoWBS(false);
         config.setAutoRelationUniqueID(false);
         config.setBaselineStrategy(m_baselineStrategy);

         m_projectFile.getProjectProperties().setFileApplication("Primavera");
         m_projectFile.getProjectProperties().setFileType("PMXML");

         addListenersToProject(m_projectFile);

         // Process common data
         if (readSharedData)
         {
            processCurrencies(apibo);
            processLocations(apibo);
            processUnitsOfMeasure(apibo);
            processExpenseCategories(apibo);
            processCostAccounts(apibo);
            processWorkContours(apibo);
            processNotebookTopics(apibo);
            processUdfDefintions(apibo);
            processActivityCodeDefinitions(apibo.getActivityCodeType(), apibo.getActivityCode());
            processProjectCodeDefinitions(apibo);
            processResourceCodeDefinitions(apibo);
            processRoleCodeDefinitions(apibo);
            processResourceAssignmentCodeDefinitions(apibo);
            processShifts(apibo);
         }

         processCalendars(apibo.getCalendar());
         processResources(apibo);
         processRoles(apibo);
         processRoleAssignments(apibo);
         processResourceRates(apibo);
         processRoleRates(apibo);

         // Process project specific data
         List<ActivityCodeTypeType> activityCodeTypes;
         List<ActivityCodeType> activityCodes;
         List<WBSType> wbs;
         List<ProjectNoteType> projectNotes;
         List<ActivityType> activities;
         List<ActivityNoteType> activityNotes;
         List<RelationshipType> relationships;
         List<ResourceAssignmentType> assignments;
         List<ActivityExpenseType> activityExpenseType;
         List<ActivityStepType> steps;
         List<CodeAssignmentType> codes;

         if (projectObject instanceof ProjectType)
         {
            ProjectType project = (ProjectType) projectObject;
            processCalendars(project.getCalendar());
            processProjectProperties(project);
            activityCodeTypes = project.getActivityCodeType();
            activityCodes = project.getActivityCode();
            wbs = project.getWBS();
            projectNotes = project.getProjectNote();
            activities = project.getActivity();
            steps = project.getActivityStep();
            activityNotes = project.getActivityNote();
            relationships = project.getRelationship();
            assignments = project.getResourceAssignment();
            activityExpenseType = project.getActivityExpense();
            codes = project.getCode();
         }
         else
         {
            BaselineProjectType project = (BaselineProjectType) projectObject;
            processCalendars(project.getCalendar());
            processProjectProperties(project);
            activityCodeTypes = project.getActivityCodeType();
            activityCodes = project.getActivityCode();
            wbs = project.getWBS();
            projectNotes = project.getProjectNote();
            activities = project.getActivity();
            steps = project.getActivityStep();
            activityNotes = project.getActivityNote();
            relationships = project.getRelationship();
            assignments = project.getResourceAssignment();
            activityExpenseType = project.getActivityExpense();
            codes = project.getCode();
         }

         Map<Integer, Notes> wbsNotes = getWbsNotes(projectNotes);
         m_projectFile.getProjectProperties().setNotesObject(wbsNotes.get(Integer.valueOf(0)));

         processGlobalProperties(apibo);
         processActivityCodeDefinitions(activityCodeTypes, activityCodes);
         configureProjectCalendars();
         processProjectCodeAssignments(codes);
         processTasks(wbs, wbsNotes, activities, getActivityNotes(activityNotes));
         processPredecessors(relationships);
         processAssignments(assignments);
         processExpenseItems(activityExpenseType);
         processActivitySteps(steps);
         rollupValues();

         m_projectFile.updateStructure();
         m_projectFile.readComplete();

         return m_projectFile;
      }

      finally
      {
         m_projectFile = null;
         m_shared = null;
         m_activityClashMap = null;
         m_roleClashMap = null;
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
      byte[] buffer = InputStreamHelper.read(bis, bufferSize);
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
   private void processUdfDefintions(APIBusinessObjects apibo)
   {
      for (UDFTypeType udf : apibo.getUDFType())
      {
         processUdfDefinition(udf);
      }
   }

   /**
    * Process an individual UDF.
    *
    * @param udf UDF definition
    */
   private void processUdfDefinition(UDFTypeType udf)
   {
      FieldTypeClass fieldTypeClass = FieldTypeClassHelper.getInstanceFromXml(udf.getSubjectArea());
      if (fieldTypeClass == null)
      {
         return;
      }

      UserDefinedField field = new UserDefinedField.Builder(m_projectFile)
         .uniqueID(udf.getObjectId())
         .externalName(udf.getTitle())
         .fieldTypeClass(fieldTypeClass)
         .summaryTaskOnly(udf.getSubjectArea().equals("WBS"))
         .dataType(UdfHelper.getDataTypeFromXml(udf.getDataType()))
         .build();

      m_projectFile.getUserDefinedFields().add(field);
      m_projectFile.getCustomFields().add(field).setAlias(udf.getTitle()).setUniqueID(udf.getObjectId());
   }

   private void processGlobalProperties(APIBusinessObjects apibo)
   {
      List<GlobalPreferencesType> list = apibo.getGlobalPreferences();
      if (!list.isEmpty())
      {
         GlobalPreferencesType prefs = list.get(0);
         ProjectProperties properties = m_projectFile.getProjectProperties();

         properties.setCreationDate(prefs.getCreateDate());
         properties.setLastSaved(prefs.getLastUpdateDate());
         properties.setMinutesPerDay(Integer.valueOf((int) (NumberHelper.getDouble(prefs.getHoursPerDay()) * 60)));
         properties.setMinutesPerWeek(Integer.valueOf((int) (NumberHelper.getDouble(prefs.getHoursPerWeek()) * 60)));
         properties.setMinutesPerMonth(Integer.valueOf((int) (NumberHelper.getDouble(prefs.getHoursPerMonth()) * 60)));
         properties.setMinutesPerYear(Integer.valueOf((int) (NumberHelper.getDouble(prefs.getHoursPerYear()) * 60)));
         properties.setWeekStartDay(DayOfWeekHelper.getInstance(NumberHelper.getInt(prefs.getStartDayOfWeek())));

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
    * Process project code assignments.
    *
    * @param codes project code assignments
    */
   private void processProjectCodeAssignments(List<CodeAssignmentType> codes)
   {
      ProjectProperties props = m_projectFile.getProjectProperties();

      for (CodeAssignmentType assignment : codes)
      {
         ProjectCode code = m_projectFile.getProjectCodes().getByUniqueID(Integer.valueOf(assignment.getTypeObjectId()));
         if (code == null)
         {
            continue;
         }

         ProjectCodeValue codeValue = code.getValueByUniqueID(Integer.valueOf(assignment.getValueObjectId()));
         if (codeValue != null)
         {
            props.addProjectCodeValue(codeValue);
         }
      }
   }

   /**
    * Process resource code assignments.
    *
    * @param resource parent resource
    * @param codes resource code assignments
    */
   private void processResourceCodeAssignments(Resource resource, List<CodeAssignmentType> codes)
   {
      for (CodeAssignmentType assignment : codes)
      {
         ResourceCode code = m_projectFile.getResourceCodes().getByUniqueID(Integer.valueOf(assignment.getTypeObjectId()));
         if (code == null)
         {
            continue;
         }

         ResourceCodeValue codeValue = code.getValueByUniqueID(Integer.valueOf(assignment.getValueObjectId()));
         if (codeValue != null)
         {
            resource.addResourceCodeValue(codeValue);
         }
      }
   }

   /**
    * Process role code assignments.
    *
    * @param resource parent resource
    * @param codes role code assignments
    */
   private void processRoleCodeAssignments(Resource resource, List<CodeAssignmentType> codes)
   {
      for (CodeAssignmentType assignment : codes)
      {
         RoleCode code = m_projectFile.getRoleCodes().getByUniqueID(Integer.valueOf(assignment.getTypeObjectId()));
         if (code == null)
         {
            continue;
         }

         RoleCodeValue codeValue = code.getValueByUniqueID(Integer.valueOf(assignment.getValueObjectId()));
         if (codeValue != null)
         {
            resource.addRoleCodeValue(codeValue);
         }
      }
   }

   /**
    * Process resource assignment code assignments.
    *
    * @param resourceAssignment parent resource assignment
    * @param codes resource assignment code assignments
    */
   private void processResourceAssignmentCodeAssignments(ResourceAssignment resourceAssignment, List<CodeAssignmentType> codes)
   {
      for (CodeAssignmentType assignment : codes)
      {
         ResourceAssignmentCode code = m_projectFile.getResourceAssignmentCodes().getByUniqueID(Integer.valueOf(assignment.getTypeObjectId()));
         if (code == null)
         {
            continue;
         }

         ResourceAssignmentCodeValue codeValue = code.getValueByUniqueID(Integer.valueOf(assignment.getValueObjectId()));
         if (codeValue != null)
         {
            resourceAssignment.addResourceAssignmentCodeValue(codeValue);
         }
      }
   }

   /**
    * Process project properties.
    *
    * @param project xml container
    */
   private void processProjectProperties(ProjectType project)
   {
      ProjectProperties properties = m_projectFile.getProjectProperties();
      properties.setBaselineProjectUniqueID(project.getCurrentBaselineProjectObjectId());
      properties.setCreationDate(project.getCreateDate() == null ? project.getDateAdded() : project.getCreateDate());
      properties.setCriticalActivityType(CriticalActivityTypeHelper.getInstanceFromXml(project.getCriticalActivityPathType()));
      properties.setFinishDate(project.getFinishDate());
      properties.setGUID(DatatypeConverter.parseUUID(project.getGUID()));
      properties.setName(project.getName());
      properties.setStartDate(project.getStartDate());
      properties.setStatusDate(project.getDataDate());
      properties.setProjectID(project.getId());
      properties.setUniqueID(project.getObjectId());
      properties.setExportFlag(!BooleanHelper.getBoolean(project.isExternal()));
      properties.setPlannedStart(project.getPlannedStartDate());
      properties.setScheduledFinish(project.getScheduledFinishDate());
      properties.setMustFinishBy(project.getMustFinishByDate());
      properties.setCriticalSlackLimit(Duration.getInstance(NumberHelper.getDouble(project.getCriticalActivityFloatLimit()), TimeUnit.HOURS));
      properties.setLocationUniqueID(project.getLocationObjectId());
      // NOTE: this also appears in the schedule options. We will override this with the schedule options value if both are present
      properties.setRelationshipLagCalendar(RelationshipLagCalendarHelper.getInstanceFromXml(project.getRelationshipLagCalendar()));
      properties.setWbsCodeSeparator(project.getWBSCodeSeparator());
      properties.setActivityIdPrefix(project.getActivityIdPrefix());
      properties.setActivityIdSuffix(project.getActivityIdSuffix());
      properties.setActivityIdIncrement(project.getActivityIdIncrement());
      properties.setActivityIdIncrementBasedOnSelectedActivity(BooleanHelper.getBoolean(project.isActivityIdBasedOnSelectedActivity()));
      properties.setProjectWebsiteUrl(nullIfEmpty(project.getWebSiteURL()));
      properties.setEnablePublication(BooleanHelper.getBoolean(project.isEnablePublication()));
      properties.setEnableSummarization(BooleanHelper.getBoolean(project.isEnableSummarization()));

      ProjectCalendar calendar = m_projectFile.getCalendarByUniqueID(project.getActivityDefaultCalendarObjectId());
      if (calendar != null)
      {
         m_projectFile.setDefaultCalendar(calendar);
      }

      processScheduleOptions(project.getScheduleOptions());
      populateUserDefinedFieldValues(properties, project.getUDF());
   }

   private void processProjectProperties(BaselineProjectType project)
   {
      ProjectProperties properties = m_projectFile.getProjectProperties();

      properties.setCreationDate(project.getCreateDate() == null ? project.getDateAdded() : project.getCreateDate());
      properties.setFinishDate(project.getFinishDate());
      properties.setGUID(DatatypeConverter.parseUUID(project.getGUID()));
      properties.setName(project.getName());
      properties.setStartDate(project.getPlannedStartDate());
      properties.setStatusDate(project.getDataDate());
      properties.setProjectID(project.getId());
      properties.setUniqueID(project.getObjectId());
      properties.setExportFlag(false);
      properties.setMustFinishBy(project.getMustFinishByDate());
      properties.setCriticalSlackLimit(Duration.getInstance(NumberHelper.getDouble(project.getCriticalActivityFloatLimit()), TimeUnit.HOURS));
      properties.setWbsCodeSeparator(project.getWBSCodeSeparator());
      properties.setBaselineTypeName(project.getBaselineTypeName());
      properties.setBaselineTypeUniqueID(project.getBaselineTypeObjectId());
      properties.setLastBaselineUpdateDate(project.getLastBaselineUpdateDate());
      properties.setProjectIsBaseline(true);
      properties.setProjectWebsiteUrl(project.getWebSiteURL());
      properties.setEnablePublication(BooleanHelper.getBoolean(project.isEnablePublication()));
      properties.setEnableSummarization(BooleanHelper.getBoolean(project.isEnableSummarization()));

      ProjectCalendar calendar = m_projectFile.getCalendarByUniqueID(project.getActivityDefaultCalendarObjectId());
      if (calendar != null)
      {
         m_projectFile.setDefaultCalendar(calendar);
      }

      processScheduleOptions(project.getScheduleOptions());
   }

   /**
    * Process activity code definitions.
    *
    * @param types list of activity code types
    * @param typeValues list of activity code values
    */
   private void processActivityCodeDefinitions(List<ActivityCodeTypeType> types, List<ActivityCodeType> typeValues)
   {
      ActivityCodeContainer container = m_projectFile.getActivityCodes();
      Map<Integer, ActivityCode> map = new HashMap<>();

      for (ActivityCodeTypeType type : types)
      {
         ActivityCode code = new ActivityCode.Builder(m_projectFile)
            .uniqueID(type.getObjectId())
            .scope(ActivityCodeScopeHelper.getInstanceFromXml(type.getScope()))
            .scopeEpsUniqueID(type.getEPSObjectId())
            .scopeProjectUniqueID(type.getProjectObjectId())
            .sequenceNumber(type.getSequenceNumber())
            .name(type.getName())
            .secure(BooleanHelper.getBoolean(type.isIsSecureCode()))
            .maxLength(type.getLength())
            .build();
         container.add(code);
         map.put(code.getUniqueID(), code);
      }

      typeValues = HierarchyHelper.sortHierarchy(typeValues, ActivityCodeType::getObjectId, ActivityCodeType::getParentObjectId);
      for (ActivityCodeType typeValue : typeValues)
      {
         ActivityCode code = map.get(typeValue.getCodeTypeObjectId());
         if (code != null)
         {
            ActivityCodeValue value = new ActivityCodeValue.Builder(m_projectFile)
               .activityCode(code)
               .uniqueID(typeValue.getObjectId())
               .sequenceNumber(typeValue.getSequenceNumber())
               .name(typeValue.getCodeValue())
               .description(typeValue.getDescription())
               .color(ColorHelper.parseHtmlColor(typeValue.getColor()))
               .parentValue(code.getValueByUniqueID(typeValue.getParentObjectId()))
               .build();
            code.addValue(value);
         }
      }
   }

   /**
    * Process project code definitions.
    *
    * @param apibo top level object
    */
   private void processProjectCodeDefinitions(APIBusinessObjects apibo)
   {
      ProjectCodeContainer container = m_projectFile.getProjectCodes();
      Map<Integer, ProjectCode> map = new HashMap<>();

      for (ProjectCodeTypeType type : apibo.getProjectCodeType())
      {
         ProjectCode code = new ProjectCode.Builder(m_projectFile)
            .uniqueID(type.getObjectId())
            .sequenceNumber(type.getSequenceNumber())
            .name(type.getName())
            .secure(BooleanHelper.getBoolean(type.isIsSecureCode()))
            .maxLength(type.getLength())
            .build();
         container.add(code);
         map.put(code.getUniqueID(), code);
      }

      List<ProjectCodeType> typeValues = HierarchyHelper.sortHierarchy(apibo.getProjectCode(), ProjectCodeType::getObjectId, ProjectCodeType::getParentObjectId);
      for (ProjectCodeType typeValue : typeValues)
      {
         ProjectCode code = map.get(typeValue.getCodeTypeObjectId());
         if (code != null)
         {
            ProjectCodeValue value = new ProjectCodeValue.Builder(m_projectFile)
               .projectCode(code)
               .uniqueID(typeValue.getObjectId())
               .sequenceNumber(typeValue.getSequenceNumber())
               .name(typeValue.getCodeValue())
               .description(typeValue.getDescription())
               .parentValue(code.getValueByUniqueID(typeValue.getParentObjectId()))
               .build();
            code.addValue(value);
         }
      }
   }

   /**
    * Process resource code definitions.
    *
    * @param apibo top level object
    */
   private void processResourceCodeDefinitions(APIBusinessObjects apibo)
   {
      ResourceCodeContainer container = m_projectFile.getResourceCodes();
      Map<Integer, ResourceCode> map = new HashMap<>();

      for (ResourceCodeTypeType type : apibo.getResourceCodeType())
      {
         ResourceCode code = new ResourceCode.Builder(m_projectFile)
            .uniqueID(type.getObjectId())
            .sequenceNumber(type.getSequenceNumber())
            .name(type.getName())
            .secure(BooleanHelper.getBoolean(type.isIsSecureCode()))
            .maxLength(type.getLength())
            .build();
         container.add(code);
         map.put(code.getUniqueID(), code);
      }

      List<ResourceCodeType> typeValues = HierarchyHelper.sortHierarchy(apibo.getResourceCode(), ResourceCodeType::getObjectId, ResourceCodeType::getParentObjectId);
      for (ResourceCodeType typeValue : typeValues)
      {
         ResourceCode code = map.get(typeValue.getCodeTypeObjectId());
         if (code != null)
         {
            ResourceCodeValue value = new ResourceCodeValue.Builder(m_projectFile)
               .resourceCode(code)
               .uniqueID(typeValue.getObjectId())
               .sequenceNumber(typeValue.getSequenceNumber())
               .name(typeValue.getCodeValue())
               .description(typeValue.getDescription())
               .parentValue(code.getValueByUniqueID(typeValue.getParentObjectId()))
               .build();
            code.addValue(value);
         }
      }
   }

   /**
    * Process role code definitions.
    *
    * @param apibo top level object
    */
   private void processRoleCodeDefinitions(APIBusinessObjects apibo)
   {
      RoleCodeContainer container = m_projectFile.getRoleCodes();
      Map<Integer, RoleCode> map = new HashMap<>();

      for (RoleCodeTypeType type : apibo.getRoleCodeType())
      {
         RoleCode code = new RoleCode.Builder(m_projectFile)
            .uniqueID(type.getObjectId())
            .sequenceNumber(type.getSequenceNumber())
            .name(type.getName())
            .secure(BooleanHelper.getBoolean(type.isIsSecureCode()))
            .maxLength(type.getLength())
            .build();
         container.add(code);
         map.put(code.getUniqueID(), code);
      }

      List<RoleCodeType> typeValues = HierarchyHelper.sortHierarchy(apibo.getRoleCode(), RoleCodeType::getObjectId, RoleCodeType::getParentObjectId);
      for (RoleCodeType typeValue : typeValues)
      {
         RoleCode code = map.get(typeValue.getCodeTypeObjectId());
         if (code != null)
         {
            RoleCodeValue value = new RoleCodeValue.Builder(m_projectFile)
               .roleCode(code)
               .uniqueID(typeValue.getObjectId())
               .sequenceNumber(typeValue.getSequenceNumber())
               .name(typeValue.getCodeValue())
               .description(typeValue.getDescription())
               .parentValue(code.getValueByUniqueID(typeValue.getParentObjectId()))
               .build();
            code.addValue(value);
         }
      }
   }

   /**
    * Process resource assignment code definitions.
    *
    * @param apibo top level object
    */
   private void processResourceAssignmentCodeDefinitions(APIBusinessObjects apibo)
   {
      ResourceAssignmentCodeContainer container = m_projectFile.getResourceAssignmentCodes();
      Map<Integer, ResourceAssignmentCode> map = new HashMap<>();

      for (ResourceAssignmentCodeTypeType type : apibo.getResourceAssignmentCodeType())
      {
         ResourceAssignmentCode code = new ResourceAssignmentCode.Builder(m_projectFile)
            .uniqueID(type.getObjectId())
            .sequenceNumber(type.getSequenceNumber())
            .name(type.getName())
            .secure(BooleanHelper.getBoolean(type.isIsSecureCode()))
            .maxLength(type.getLength())
            .build();
         container.add(code);
         map.put(code.getUniqueID(), code);
      }

      List<ResourceAssignmentCodeType> typeValues = HierarchyHelper.sortHierarchy(apibo.getResourceAssignmentCode(), ResourceAssignmentCodeType::getObjectId, ResourceAssignmentCodeType::getParentObjectId);
      for (ResourceAssignmentCodeType typeValue : typeValues)
      {
         ResourceAssignmentCode code = map.get(typeValue.getCodeTypeObjectId());
         if (code != null)
         {
            ResourceAssignmentCodeValue value = new ResourceAssignmentCodeValue.Builder(m_projectFile)
               .resourceAssignmentCode(code)
               .uniqueID(typeValue.getObjectId())
               .sequenceNumber(typeValue.getSequenceNumber())
               .name(typeValue.getCodeValue())
               .description(typeValue.getDescription())
               .parentValue(code.getValueByUniqueID(typeValue.getParentObjectId()))
               .build();
            code.addValue(value);
         }
      }
   }

   /**
    * Process locations.
    *
    * @param apibo top level object
    */
   private void processLocations(APIBusinessObjects apibo)
   {
      LocationContainer container = m_projectFile.getLocations();
      apibo.getLocation().forEach(c -> container.add(
         new Location.Builder(m_projectFile)
            .uniqueID(c.getObjectId())
            .name(c.getName())
            .addressLine1(c.getAddressLine1())
            .addressLine2(c.getAddressLine2())
            .city(c.getCity())
            .municipality(c.getMunicipality())
            .state(c.getState())
            .stateCode(c.getStateCode())
            .country(c.getCountry())
            .countryCode(c.getCountryCode())
            .postalCode(c.getPostalCode())
            .latitude(c.getLatitude())
            .longitude(c.getLongitude())
            .build()));
   }

   /**
    * Process currencies.
    *
    * @param apibo top level object
    */
   private void processCurrencies(APIBusinessObjects apibo)
   {
      CurrencyContainer container = m_projectFile.getCurrencies();

      apibo.getCurrency().forEach(c -> container.add(
         new Currency.Builder(m_projectFile)
            .uniqueID(c.getObjectId())
            .currencyID(c.getId())
            .name(c.getName())
            .symbol(c.getSymbol())
            .exchangeRate(c.getExchangeRate())
            .decimalSymbol("Comma".equals(c.getDecimalSymbol()) ? "," : ".")
            .numberOfDecimalPlaces(c.getDecimalPlaces())
            .digitGroupingSymbol("Comma".equals(c.getDigitGroupingSymbol()) ? "," : ".")
            .positiveCurrencyFormat(c.getPositiveSymbol())
            .negativeCurrencyFormat(c.getNegativeSymbol())
            .build()));
   }

   /**
    * Process shifts.
    *
    * @param apibo top level object
    */
   private void processShifts(APIBusinessObjects apibo)
   {
      ShiftContainer shiftContainer = m_projectFile.getShifts();
      ShiftPeriodContainer shiftPeriodContainer = m_projectFile.getShiftPeriods();

      for (ShiftType xml : apibo.getShift())
      {
         Shift shift = new Shift.Builder(m_projectFile)
            .name(xml.getName())
            .uniqueID(xml.getObjectId())
            .build();
         shiftContainer.add(shift);

         for (ShiftPeriodType xmlPeriod : xml.getShiftPeriod())
         {
            ShiftPeriod period = new ShiftPeriod.Builder(m_projectFile, shift)
               .uniqueID(xmlPeriod.getObjectId())
               .start(xmlPeriod.getStartHour())
               .build();
            shiftPeriodContainer.add(period);
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
      apibo.getExpenseCategory().forEach(c -> container.add(new ExpenseCategory.Builder(m_projectFile).uniqueID(c.getObjectId()).name(c.getName()).sequenceNumber(c.getSequenceNumber()).build()));
   }

   /**
    * Process cost accounts.
    *
    * @param apibo top level object
    */
   private void processCostAccounts(APIBusinessObjects apibo)
   {
      CostAccountContainer container = m_projectFile.getCostAccounts();
      HierarchyHelper.sortHierarchy(apibo.getCostAccount(), CostAccountType::getObjectId, CostAccountType::getParentObjectId).forEach(c -> container.add(
         new CostAccount.Builder(m_projectFile)
            .uniqueID(c.getObjectId())
            .id(c.getId())
            .name(c.getName())
            .notes(getNotes(c.getDescription()))
            .sequenceNumber(c.getSequenceNumber())
            .parent(container.getByUniqueID(c.getParentObjectId()))
            .build()));
   }

   /**
    * Process units of measure.
    *
    * @param apibo top level object
    */
   private void processUnitsOfMeasure(APIBusinessObjects apibo)
   {
      UnitOfMeasureContainer container = m_projectFile.getUnitsOfMeasure();
      apibo.getUnitOfMeasure().forEach(u -> container.add(processUnitOfMeasure(u)));
   }

   /**
    * Create a unit of measure.
    *
    * @param u unit of measure data
    * @return UnitOfMeasure instance
    */
   private UnitOfMeasure processUnitOfMeasure(UnitOfMeasureType u)
   {
      return new UnitOfMeasure.Builder(m_projectFile)
         .uniqueID(u.getObjectId())
         .abbreviation(u.getAbbreviation())
         .name(u.getName())
         .sequenceNumber(u.getSequenceNumber())
         .build();
   }

   /**
    * Process expense items.
    *
    * @param expenseItems expense items
    */
   private void processExpenseItems(List<ActivityExpenseType> expenseItems)
   {
      for (ActivityExpenseType item : expenseItems)
      {
         Task task = m_projectFile.getTaskByUniqueID(m_activityClashMap.getID(item.getActivityObjectId()));
         if (task != null)
         {
            ExpenseItem.Builder builder = new ExpenseItem.Builder(task)
               .account(m_projectFile.getCostAccounts().getByUniqueID(item.getCostAccountObjectId()))
               .accrueType(AccrueTypeHelper.getInstanceFromXml(item.getAccrualType()))
               .actualCost(item.getActualCost())
               .actualUnits(item.getActualUnits())
               .atCompletionUnits(item.getAtCompletionUnits())
               .autoComputeActuals(BooleanHelper.getBoolean(item.isAutoComputeActuals()))
               .category(m_projectFile.getExpenseCategories().getByUniqueID(item.getExpenseCategoryObjectId()))
               .description(item.getExpenseDescription())
               .documentNumber(item.getDocumentNumber())
               .name(item.getExpenseItem())
               .plannedCost(item.getPlannedCost())
               .plannedUnits(item.getPlannedUnits())
               .pricePerUnit(item.getPricePerUnit())
               .remainingCost(item.getRemainingCost())
               .remainingUnits(item.getRemainingUnits())
               .uniqueID(item.getObjectId())
               .unitOfMeasure(item.getUnitOfMeasure())
               .vendor(item.getVendor())
               .atCompletionCost(NumberHelper.sumAsDouble(item.getActualCost(), item.getRemainingCost()));

            ExpenseItem ei = builder.build();
            task.getExpenseItems().add(ei);

            // Roll up to parent task
            task.setPlannedCost(NumberHelper.sumAsDouble(task.getPlannedCost(), ei.getPlannedCost()));
            task.setActualCost(NumberHelper.sumAsDouble(task.getActualCost(), ei.getActualCost()));
            task.setRemainingCost(NumberHelper.sumAsDouble(task.getRemainingCost(), ei.getRemainingCost()));
            task.setCost(NumberHelper.sumAsDouble(task.getCost(), ei.getAtCompletionCost()));
            task.setFixedCost(NumberHelper.sumAsDouble(task.getFixedCost(), ei.getAtCompletionCost()));
         }
      }
   }

   /**
    * Process project calendars.
    *
    * @param calendars list of calendar data
    */
   private void processCalendars(List<CalendarType> calendars)
   {
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
    * Set the default calendar ID and enable auto calendar unique ID values.
    */
   private void configureProjectCalendars()
   {
      // Ensure that resource calendars we create later have valid unique IDs
      ProjectConfig config = m_projectFile.getProjectConfig();
      config.setAutoCalendarUniqueID(true);
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
      calendar.setUniqueID(id);
      calendar.setName(row.getName());
      calendar.setType(CalendarTypeHelper.getInstanceFromXml(row.getType()));
      calendar.setPersonal(BooleanHelper.getBoolean(row.isIsPersonal()));

      if (BooleanHelper.getBoolean(row.isIsDefault()) && m_projectFile.getDefaultCalendar() == null)
      {
         m_projectFile.setDefaultCalendar(calendar);
      }

      Map<DayOfWeek, StandardWorkHours> hoursMap = new HashMap<>();
      StandardWorkWeek stdWorkWeek = row.getStandardWorkWeek();
      if (stdWorkWeek != null)
      {
         for (StandardWorkHours hours : stdWorkWeek.getStandardWorkHours())
         {
            hoursMap.put(DAY_MAP.get(hours.getDayOfWeek()), hours);
         }
      }

      for (DayOfWeek day : DayOfWeek.values())
      {
         // If we don't have an entry for a day, use default values
         StandardWorkHours hours = hoursMap.get(day);
         if (hours == null)
         {
            calendar.setWorkingDay(day, day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY);
            if (calendar.isWorkingDay(day))
            {
               calendar.addCalendarHours(day).add(ProjectCalendarHelper.getDefaultCalendarHours());
            }
            continue;
         }

         ProjectCalendarHours calendarHours = calendar.addCalendarHours(day);
         List<WorkTimeType> workTime = hours.getWorkTime();
         if (workTime.isEmpty() || workTime.get(0) == null)
         {
            calendar.setWorkingDay(day, false);
         }
         else
         {
            calendar.setWorkingDay(day, true);
            for (WorkTimeType work : workTime)
            {
               if (work != null)
               {
                  calendarHours.add(new LocalTimeRange(work.getStart(), getEndTime(work.getFinish())));
               }
            }
         }
      }

      HolidayOrExceptions hoe = row.getHolidayOrExceptions();
      if (hoe != null)
      {
         for (HolidayOrException ex : hoe.getHolidayOrException())
         {
            LocalDate startDate = LocalDateHelper.getLocalDate(ex.getDate());
            LocalDate endDate = LocalDateHelper.getLocalDate(ex.getDate());
            ProjectCalendarException pce = calendar.addCalendarException(startDate, endDate);

            List<WorkTimeType> workTime = ex.getWorkTime();

            // Special case: a single entry for 00:00-23:59 is treated by P6 as a non-working day
            if (workTime.size() == 1)
            {
               WorkTimeType work = workTime.get(0);
               if (work == null || (LocalTime.MIDNIGHT.equals(work.getStart()) && NON_WORKING_END_TIME.equals(work.getFinish())))
               {
                  continue;
               }
            }

            for (WorkTimeType work : workTime)
            {
               if (work != null && work.getStart() != null && work.getFinish() != null)
               {
                  pce.add(new LocalTimeRange(work.getStart(), getEndTime(work.getFinish())));
               }
            }
         }
      }

      ProjectCalendarHelper.ensureWorkingTime(calendar);

      //
      // Try and extract minutes per period from the calendar row
      //
      Double rowHoursPerDay = row.getHoursPerDay();
      Double rowHoursPerWeek = row.getHoursPerWeek();
      Double rowHoursPerMonth = row.getHoursPerMonth();
      Double rowHoursPerYear = row.getHoursPerYear();

      calendar.setCalendarMinutesPerDay(Integer.valueOf((int) (NumberHelper.getDouble(rowHoursPerDay) * 60)));
      calendar.setCalendarMinutesPerWeek(Integer.valueOf((int) (NumberHelper.getDouble(rowHoursPerWeek) * 60)));
      calendar.setCalendarMinutesPerMonth(Integer.valueOf((int) (NumberHelper.getDouble(rowHoursPerMonth) * 60)));
      calendar.setCalendarMinutesPerYear(Integer.valueOf((int) (NumberHelper.getDouble(rowHoursPerYear) * 60)));

      //
      // If we're missing any of these figures, generate them.
      // Note that P6 allows users to enter arbitrary hours per period,
      // as far as I can see they aren't validated to see if they make sense,
      // so the figures here won't necessarily match what you'd see in P6.
      //
      if (rowHoursPerDay == null || rowHoursPerWeek == null || rowHoursPerMonth == null || rowHoursPerYear == null)
      {
         int minutesPerWeek = 0;
         int workingDays = 0;

         for (DayOfWeek day : DayOfWeek.values())
         {
            ProjectCalendarHours hours = calendar.getCalendarHours(day);
            if (hours == null)
            {
               continue;
            }

            if (!hours.isEmpty())
            {
               ++workingDays;
               for (LocalTimeRange range : hours)
               {
                  minutesPerWeek += (range.getDurationAsMilliseconds() / (1000 * 60));
               }
            }
         }

         int minutesPerDay = minutesPerWeek / workingDays;
         int minutesPerMonth = minutesPerWeek * 4;
         int minutesPerYear = minutesPerMonth * 12;

         if (rowHoursPerDay == null)
         {
            calendar.setCalendarMinutesPerDay(Integer.valueOf(minutesPerDay));
         }

         if (rowHoursPerWeek == null)
         {
            calendar.setCalendarMinutesPerWeek(Integer.valueOf(minutesPerWeek));
         }

         if (rowHoursPerMonth == null)
         {
            calendar.setCalendarMinutesPerMonth(Integer.valueOf(minutesPerMonth));
         }

         if (rowHoursPerYear == null)
         {
            calendar.setCalendarMinutesPerYear(Integer.valueOf(minutesPerYear));
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
         m_roleClashMap.addID(xml.getObjectId());

         Double defaultUnitsPerTime = xml.getDefaultUnitsPerTime();
         if (defaultUnitsPerTime == null)
         {
            // Older versions of P6 appear to use MaxUnitsPerTime, so we'll fall back
            // to this value if DefaultUnitsPerTime is not present
            defaultUnitsPerTime = xml.getMaxUnitsPerTime();
         }

         // Note: if default units per time is an empty field, this represents a value of zero in P6
         defaultUnitsPerTime = defaultUnitsPerTime == null ? NumberHelper.DOUBLE_ZERO : Double.valueOf(defaultUnitsPerTime.doubleValue() * 100.0);

         resource.setUniqueID(xml.getObjectId());
         resource.setName(xml.getName());
         resource.setCode(xml.getEmployeeId());
         resource.setEmailAddress(xml.getEmailAddress());
         resource.setGUID(DatatypeConverter.parseUUID(xml.getGUID()));
         resource.setNotesObject(getNotes(xml.getResourceNotes()));
         resource.setCreationDate(xml.getCreateDate());
         resource.setType(ResourceTypeHelper.getInstanceFromXml(xml.getResourceType()));
         resource.setDefaultUnits(defaultUnitsPerTime);
         resource.setParentResourceUniqueID(xml.getParentObjectId());
         resource.setResourceID(xml.getId());
         resource.setCalendar(m_projectFile.getCalendars().getByUniqueID(xml.getCalendarObjectId()));
         resource.setCalculateCostsFromUnits(BooleanHelper.getBoolean(xml.isCalculateCostFromUnits()));
         resource.setSequenceNumber(xml.getSequenceNumber());
         resource.setActive(BooleanHelper.getBoolean(xml.isIsActive()));
         resource.setLocationUniqueID(xml.getLocationObjectId());
         resource.setUnitOfMeasureUniqueID(xml.getUnitOfMeasureObjectId());
         resource.setShiftUniqueID(xml.getShiftObjectId());
         resource.setPrimaryRoleUniqueID(xml.getPrimaryRoleObjectId());
         resource.setCurrencyUniqueID(xml.getCurrencyObjectId());

         processResourceCodeAssignments(resource, xml.getCode());

         populateUserDefinedFieldValues(resource, xml.getUDF());

         m_eventManager.fireResourceReadEvent(resource);
      }
   }

   /**
    * Process roles.
    *
    * @param apibo xml container
    */
   private void processRoles(APIBusinessObjects apibo)
   {
      for (RoleType role : apibo.getRole())
      {
         Resource resource = m_projectFile.addResource();
         resource.setRole(true);
         resource.setUniqueID(m_roleClashMap.getID(role.getObjectId()));
         resource.setName(role.getName());
         resource.setResourceID(role.getId());
         resource.setNotesObject(getHtmlNote(role.getResponsibilities()));
         resource.setSequenceNumber(role.getSequenceNumber());

         processRoleCodeAssignments(resource, role.getCode());
      }
   }

   /**
    * Process role assignments.
    *
    * @param apibo xml container
    */
   private void processRoleAssignments(APIBusinessObjects apibo)
   {
      for (ResourceRoleType assignment : apibo.getResourceRole())
      {
         Resource resource = m_projectFile.getResourceByUniqueID(assignment.getResourceObjectId());
         if (resource == null)
         {
            continue;
         }

         Resource role = m_projectFile.getResourceByUniqueID(assignment.getRoleObjectId());
         if (role == null)
         {
            continue;
         }

         resource.addRoleAssignment(role, SkillLevelHelper.getInstanceFromXml(assignment.getProficiency()));
      }
   }

   /**
    * Create a Notes instance from an HTML document.
    *
    * @param text HTML document
    * @return Notes instance
    */
   private Notes getNotes(String text)
   {
      Notes notes = getHtmlNote(text);
      return notes == null || notes.isEmpty() ? null : notes;
   }

   /**
    * Return null if string is empty, otherwise return string.
    *
    * @param text string
    * @return null if empty, otherwise string
    */
   private String nullIfEmpty(String text)
   {
      return text == null || text.isEmpty() ? null : text;
   }

   /**
    * Process tasks.
    *
    * @param wbs project wbs entries
    * @param wbsNotes ebs entry notes
    * @param activities project activities
    * @param activityNotes activity notes
    */
   private void processTasks(List<WBSType> wbs, Map<Integer, Notes> wbsNotes, List<ActivityType> activities, Map<Integer, Notes> activityNotes)
   {
      Set<Task> wbsTasks = new HashSet<>();
      boolean baselineFromCurrentProject = m_projectFile.getProjectProperties().getBaselineProjectUniqueID() == null;

      //
      // Read WBS entries and create tasks
      //
      wbs.sort(WBS_ROW_COMPARATOR);

      for (WBSType row : wbs)
      {
         Task task = m_projectFile.addTask();
         Integer uniqueID = row.getObjectId();
         m_activityClashMap.addID(uniqueID);
         wbsTasks.add(task);

         task.setUniqueID(uniqueID);
         task.setGUID(DatatypeConverter.parseUUID(row.getGUID()));
         task.setName(row.getName());
         task.setSummary(true);
         task.setStart(row.getAnticipatedStartDate());
         task.setFinish(row.getAnticipatedFinishDate());
         task.setWBS(row.getCode());
         task.setNotesObject(wbsNotes.get(uniqueID));
         task.setSequenceNumber(row.getSequenceNumber());

         populateUserDefinedFieldValues(task, row.getUDF());
      }

      //
      // Create rough hierarchical structure (note parent tasks not populated yet)
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
      // Set the WBS and the Activity ID
      //
      String prefix = m_projectFile.getProjectProperties().getProjectID();
      if (prefix != null && m_projectFile.getChildTasks().size() == 1)
      {
         String activityID = m_projectFile.getChildTasks().get(0).getWBS();
         if (activityID != null && activityID.equals(prefix))
         {
            prefix = "";
         }
      }
      populateWBS(prefix, m_projectFile);

      //
      // Read Task entries and create tasks
      //

      // If the schedule is using longest path to determine critical activities,
      // we currently don't have enough information to correctly set this attribute.
      // In this case we'll force the critical flag to false, which avoid activities
      // being incorrectly marked as critical.
      boolean forceCriticalToFalse = m_projectFile.getProjectProperties().getCriticalActivityType() == CriticalActivityType.LONGEST_PATH;

      for (ActivityType row : activities)
      {
         Integer uniqueID = row.getObjectId();
         Notes notes = activityNotes.get(uniqueID);
         uniqueID = m_activityClashMap.addID(uniqueID);

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
         task.setNotesObject(notes);

         task.setPercentCompleteType(PercentCompleteTypeHelper.getInstanceFromXml(row.getPercentCompleteType()));
         task.setPercentageComplete(reversePercentage(row.getDurationPercentComplete()));
         task.setPhysicalPercentComplete(reversePercentage(row.getPhysicalPercentComplete()));
         task.setPercentageWorkComplete(reversePercentage(row.getUnitsPercentComplete()));

         task.setActualWorkLabor(getDuration(row.getActualLaborUnits()));
         task.setActualWorkNonlabor(getDuration(row.getActualNonLaborUnits()));
         task.setPlannedWorkLabor(getDuration(row.getPlannedLaborUnits()));
         task.setPlannedWorkNonlabor(getDuration(row.getPlannedNonLaborUnits()));
         task.setRemainingWorkLabor(getDuration(row.getRemainingLaborUnits()));
         task.setRemainingWorkNonlabor(getDuration(row.getRemainingNonLaborUnits()));

         task.setActualWork(WorkHelper.addWork(task.getActualWorkLabor(), task.getActualWorkNonlabor()));
         task.setPlannedWork(WorkHelper.addWork(task.getPlannedWorkLabor(), task.getPlannedWorkNonlabor()));
         task.setRemainingWork(WorkHelper.addWork(task.getRemainingWorkLabor(), task.getRemainingWorkNonlabor()));
         task.setWork(WorkHelper.addWork(task.getActualWork(), task.getRemainingWork()));

         task.setPlannedDuration(getDuration(row.getPlannedDuration()));
         task.setActualDuration(getDuration(row.getActualDuration()));
         task.setRemainingDuration(getDuration(row.getRemainingDuration()));
         task.setDuration(getDuration(row.getAtCompletionDuration()));

         // Tempting as this is, P6 doesn't write all of these values to PMXML,
         // so we need to roll them up from the resource assignments and expenses.
         // task.setActualCost(NumberHelper.sumAsDouble(row.getActualLaborCost(), row.getActualNonLaborCost(), row.getActualMaterialCost(), row.getActualExpenseCost()));
         // task.setPlannedCost(NumberHelper.sumAsDouble(row.getPlannedLaborCost(), row.getPlannedNonLaborCost(), row.getPlannedMaterialCost(), row.getPlannedExpenseCost()));
         // task.setRemainingCost(NumberHelper.sumAsDouble(row.getRemainingLaborCost(), row.getRemainingNonLaborCost(), row.getRemainingMaterialCost(), row.getRemainingExpenseCost()));
         // task.setCost(NumberHelper.sumAsDouble(row.getAtCompletionLaborCost(), row.getAtCompletionNonLaborCost(), row.getAtCompletionMaterialCost(), row.getAtCompletionExpenseCost()));

         task.setConstraintDate(row.getPrimaryConstraintDate());
         task.setConstraintType(ConstraintTypeHelper.getInstanceFromXml(row.getPrimaryConstraintType()));
         task.setSecondaryConstraintDate(row.getSecondaryConstraintDate());
         task.setSecondaryConstraintType(ConstraintTypeHelper.getInstanceFromXml(row.getSecondaryConstraintType()));
         task.setActualStart(row.getActualStartDate());
         task.setActualFinish(row.getActualFinishDate());
         task.setPlannedStart(row.getPlannedStartDate());
         task.setPlannedFinish(row.getPlannedFinishDate());
         task.setRemainingEarlyStart(row.getRemainingEarlyStartDate());
         task.setRemainingEarlyFinish(row.getRemainingEarlyFinishDate());
         task.setRemainingLateStart(row.getRemainingLateStartDate());
         task.setRemainingLateFinish(row.getRemainingLateFinishDate());

         // My understanding is that the Early/Late Start/Finish dates in P6 are
         // equivalent to the Remaining Early/Late Start/Finish dates.
         // The only difference is that the Early/Late Start/Finish dates will be populated
         // in P6 for completed activities/milestones, but the Remaining Early/Late Start/Finish dates will not.
         // Unfortunately P6 does not populate any of these attributes in PMXML files for completed activities/milestones
         // so without running the CPM calculation ourselves we can't at present replicate
         // the Early/Late Start/Finish dates visible in P6 for completed activities/milestones.
         task.setEarlyStart(row.getRemainingEarlyStartDate());
         task.setEarlyFinish(row.getRemainingEarlyFinishDate());
         task.setLateStart(row.getRemainingLateStartDate());
         task.setLateFinish(row.getRemainingLateFinishDate());

         task.setPriority(PriorityHelper.getInstanceFromXml(row.getLevelingPriority()));
         task.setCreateDate(row.getCreateDate());
         task.setActivityID(row.getId());
         task.setActivityType(ActivityTypeHelper.getInstanceFromXml(row.getType()));
         task.setActivityStatus(ActivityStatusHelper.getInstanceFromXml(row.getStatus()));
         task.setPrimaryResourceUniqueID(row.getPrimaryResourceObjectId());
         task.setSuspendDate(row.getSuspendDate());
         task.setResume(row.getResumeDate());
         task.setType(TaskTypeHelper.getInstanceFromXml(row.getDurationType()));
         task.setMilestone(BooleanHelper.getBoolean(MILESTONE_MAP.get(row.getType())));
         task.setExternalEarlyStart(row.getExternalEarlyStartDate());
         task.setExternalLateFinish(row.getExternalLateFinishDate());
         task.setLongestPath(BooleanHelper.getBoolean(row.isIsLongestPath()));
         task.setLocationUniqueID(row.getLocationObjectId());
         task.setExpectedFinish(row.getExpectedFinishDate());

         if (parentTask != null)
         {
            task.setWBS(parentTask.getWBS());
         }

         Integer calId = row.getCalendarObjectId();
         ProjectCalendar cal = m_projectFile.getCalendarByUniqueID(calId);
         task.setCalendar(cal);

         task.setStart(row.getStartDate());
         task.setFinish(row.getFinishDate());

         // Note that planned finish is handled in the code below
         populateField(task, TaskField.START, TaskField.START, TaskField.ACTUAL_START, TaskField.REMAINING_EARLY_START, TaskField.PLANNED_START, TaskField.EARLY_START);
         populateField(task, TaskField.FINISH, TaskField.FINISH, TaskField.ACTUAL_FINISH, TaskField.REMAINING_EARLY_FINISH, TaskField.EARLY_FINISH);

         //
         // We've tried the finish and actual finish fields... but we still have null.
         // P6 itself doesn't export PMXML like this.
         // The sample I have that requires this code appears to have been generated by Synchro.
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
            // If the task hasn't started, or we don't have a usable duration let's just use the planned finish.
            //
            if (task.getActualStart() == null || duration == null)
            {
               task.setFinish(task.getPlannedFinish());
            }
            else
            {
               //
               // The task has started, let's calculate the finish date using the planned start and duration
               //
               ProjectCalendar calendar = task.getEffectiveCalendar();
               LocalDateTime finish = calendar.getDate(task.getPlannedStart(), duration);

               //
               // Deal with an oddity where the finish date shows up as the
               // start of work date for the next working day. If we can identify this,
               // wind the date back to the end of the previous working day.
               //
               LocalDateTime nextWorkStart = calendar.getNextWorkStart(finish);
               if (LocalDateTimeHelper.compare(finish, nextWorkStart) == 0)
               {
                  finish = calendar.getPreviousWorkFinish(finish);
               }
               task.setFinish(finish);
            }
         }

         //
         // Force calculation of the critical flag
         //
         task.getCritical();

         populateUserDefinedFieldValues(task, row.getUDF());
         readActivityCodes(task, row.getCode());

         // For P6 the start date is the relevant date for a Start Milestone, and the
         // Finish Date is the relevant date for a Finish Milestone. Typically, this
         // is irrelevant as both the Start and Finish dates are the same. In some
         // PMXML files this is not the case, which causes problems for applications
         // which assume that the Start and Finish dates are the same for a milestone
         // and only read one of them.
         // The code below ensures that the correct date is used, and both Start
         // and Finish Date attributes are populated with that date.
         if (task.getMilestone())
         {
            if (task.getActivityType() == org.mpxj.ActivityType.START_MILESTONE)
            {
               task.setFinish(task.getStart());
            }
            else
            {
               task.setStart(task.getFinish());
            }
         }

         if (forceCriticalToFalse)
         {
            task.setCritical(false);
         }

         if (baselineFromCurrentProject)
         {
            populateBaselineFromCurrentProject(task);
         }

         m_eventManager.fireTaskReadEvent(task);
      }

      new ActivitySorter(wbsTasks).sort(m_projectFile);

      updateStructure();
   }

   private void populateBaselineFromCurrentProject(Task task)
   {
      task.setBaselineCost(task.getPlannedCost());
      task.setBaselineDuration(task.getPlannedDuration());
      task.setBaselineFinish(task.getPlannedFinish());
      task.setBaselineStart(task.getPlannedStart());
      task.setBaselineWork(task.getPlannedWork());
   }

   private void populateWBS(String prefix, ChildTaskContainer container)
   {
      for (Task task : container.getChildTasks())
      {
         String wbs = prefix.isEmpty() ? task.getWBS() : prefix + m_projectFile.getProjectProperties().getWbsCodeSeparator() + task.getWBS();
         task.setWBS(wbs);
         task.setActivityID(wbs);
         populateWBS(wbs, task);
      }
   }

   /**
    * This method sets the calendar used by a WBS entry. In P6 if all activities
    * under a WBS entry use the same calendar, the WBS entry uses this calendar
    * for date calculation. If the activities use different calendars, the WBS
    * entry will use the project's default calendar.
    *
    * @param task task to validate
    * @return calendar used by this task
    */
   private ProjectCalendar rollupCalendars(Task task)
   {
      ProjectCalendar result = null;

      if (task.hasChildTasks())
      {
         List<ProjectCalendar> calendars = task.getChildTasks().stream().map(this::rollupCalendars).distinct().collect(Collectors.toList());

         if (calendars.size() == 1)
         {
            ProjectCalendar firstCalendar = calendars.get(0);
            if (firstCalendar != null && firstCalendar != m_projectFile.getDefaultCalendar())
            {
               result = firstCalendar;
               task.setCalendar(result);
            }
         }
      }
      else
      {
         result = task.getCalendar();
      }

      return result;
   }

   /**
    * The Primavera WBS entries we read in as tasks have user-entered start and end dates
    * which aren't calculated or adjusted based on the child task dates. We try
    * to compensate for this by using these user-entered dates as baseline dates, and
    * deriving the planned start, actual start, planned finish and actual finish from
    * the child tasks. This method recursively descends through the tasks to do this.
    *
    * @param parentTask parent task.
    */
   private void rollupDates(Task parentTask)
   {
      if (parentTask.hasChildTasks())
      {
         int finished = 0;
         LocalDateTime startDate = parentTask.getStart();
         LocalDateTime finishDate = parentTask.getFinish();
         LocalDateTime plannedStartDate = parentTask.getPlannedStart();
         LocalDateTime plannedFinishDate = parentTask.getPlannedFinish();
         LocalDateTime actualStartDate = parentTask.getActualStart();
         LocalDateTime actualFinishDate = parentTask.getActualFinish();
         LocalDateTime earlyStartDate = parentTask.getEarlyStart();
         LocalDateTime earlyFinishDate = parentTask.getEarlyFinish();
         LocalDateTime lateStartDate = parentTask.getLateStart();
         LocalDateTime lateFinishDate = parentTask.getLateFinish();
         LocalDateTime baselineStartDate = parentTask.getBaselineStart();
         LocalDateTime baselineFinishDate = parentTask.getBaselineFinish();
         LocalDateTime remainingEarlyStartDate = parentTask.getRemainingEarlyStart();
         LocalDateTime remainingEarlyFinishDate = parentTask.getRemainingEarlyFinish();
         LocalDateTime remainingLateStartDate = parentTask.getRemainingLateStart();
         LocalDateTime remainingLateFinishDate = parentTask.getRemainingLateFinish();

         boolean critical = false;

         for (Task task : parentTask.getChildTasks())
         {
            rollupDates(task);

            // the child tasks can have null dates (e.g. for nested wbs elements with no task children) so we
            // still must protect against some children having null dates

            startDate = LocalDateTimeHelper.min(startDate, task.getStart());
            finishDate = LocalDateTimeHelper.max(finishDate, task.getFinish());
            plannedStartDate = LocalDateTimeHelper.min(plannedStartDate, task.getPlannedStart());
            plannedFinishDate = LocalDateTimeHelper.max(plannedFinishDate, task.getPlannedFinish());
            actualStartDate = LocalDateTimeHelper.min(actualStartDate, task.getActualStart());
            actualFinishDate = LocalDateTimeHelper.max(actualFinishDate, task.getActualFinish());
            earlyStartDate = LocalDateTimeHelper.min(earlyStartDate, task.getEarlyStart());
            earlyFinishDate = LocalDateTimeHelper.max(earlyFinishDate, task.getEarlyFinish());
            remainingEarlyStartDate = LocalDateTimeHelper.min(remainingEarlyStartDate, task.getRemainingEarlyStart());
            remainingEarlyFinishDate = LocalDateTimeHelper.max(remainingEarlyFinishDate, task.getRemainingEarlyFinish());
            lateStartDate = LocalDateTimeHelper.min(lateStartDate, task.getLateStart());
            lateFinishDate = LocalDateTimeHelper.max(lateFinishDate, task.getLateFinish());
            remainingLateStartDate = LocalDateTimeHelper.min(remainingLateStartDate, task.getRemainingLateStart());
            remainingLateFinishDate = LocalDateTimeHelper.max(remainingLateFinishDate, task.getRemainingLateFinish());
            baselineStartDate = LocalDateTimeHelper.min(baselineStartDate, task.getBaselineStart());
            baselineFinishDate = LocalDateTimeHelper.max(baselineFinishDate, task.getBaselineFinish());

            if (task.getActualFinish() != null)
            {
               ++finished;
            }

            critical = critical || task.getCritical();
         }

         parentTask.setStart(startDate);
         parentTask.setFinish(finishDate);
         parentTask.setPlannedStart(plannedStartDate);
         parentTask.setPlannedFinish(plannedFinishDate);
         parentTask.setActualStart(actualStartDate);
         parentTask.setEarlyStart(earlyStartDate);
         parentTask.setEarlyFinish(earlyFinishDate);
         parentTask.setRemainingEarlyStart(remainingEarlyStartDate);
         parentTask.setRemainingEarlyFinish(remainingEarlyFinishDate);
         parentTask.setLateStart(lateStartDate);
         parentTask.setLateFinish(lateFinishDate);
         parentTask.setRemainingLateStart(remainingLateStartDate);
         parentTask.setRemainingLateFinish(remainingLateFinishDate);
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

         Duration plannedDuration = null;
         Duration actualDuration = null;
         Duration remainingDuration = null;
         Duration duration = null;

         ProjectCalendar effectiveCalendar = parentTask.getEffectiveCalendar();
         if (effectiveCalendar != null)
         {
            if (plannedStartDate != null && plannedFinishDate != null)
            {
               plannedDuration = effectiveCalendar.getWork(plannedStartDate, plannedFinishDate, TimeUnit.HOURS);
               parentTask.setPlannedDuration(plannedDuration);
            }

            if (parentTask.getActualFinish() == null)
            {
               LocalDateTime taskStartDate = parentTask.getRemainingEarlyStart();
               if (taskStartDate == null)
               {
                  taskStartDate = parentTask.getEarlyStart();
                  if (taskStartDate == null)
                  {
                     taskStartDate = plannedStartDate;
                  }
               }

               LocalDateTime taskFinishDate = parentTask.getRemainingEarlyFinish();
               if (taskFinishDate == null)
               {
                  taskFinishDate = parentTask.getEarlyFinish();
                  if (taskFinishDate == null)
                  {
                     taskFinishDate = plannedFinishDate;
                  }
               }

               if (taskStartDate != null)
               {
                  if (parentTask.getActualStart() != null)
                  {
                     actualDuration = effectiveCalendar.getWork(parentTask.getActualStart(), taskStartDate, TimeUnit.HOURS);
                  }

                  if (taskFinishDate != null)
                  {
                     remainingDuration = effectiveCalendar.getWork(taskStartDate, taskFinishDate, TimeUnit.HOURS);
                  }
               }
            }
            else
            {
               if (parentTask.getActualStart() != null)
               {
                  actualDuration = effectiveCalendar.getWork(parentTask.getActualStart(), parentTask.getActualFinish(), TimeUnit.HOURS);
               }

               remainingDuration = Duration.getInstance(0, TimeUnit.HOURS);
            }

            if (actualDuration != null && actualDuration.getDuration() < 0)
            {
               actualDuration = null;
            }

            if (remainingDuration != null && remainingDuration.getDuration() < 0)
            {
               remainingDuration = null;
            }

            duration = Duration.add(actualDuration, remainingDuration, effectiveCalendar);
         }

         parentTask.setActualDuration(actualDuration);
         parentTask.setRemainingDuration(remainingDuration);
         parentTask.setDuration(duration);

         if (plannedDuration != null && remainingDuration != null && plannedDuration.getDuration() != 0)
         {
            double durationPercentComplete = ((plannedDuration.getDuration() - remainingDuration.getDuration()) / plannedDuration.getDuration()) * 100.0;
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
            parentTask.setPercentCompleteType(PercentCompleteType.DURATION);
         }

         // Force total slack calculation to avoid overwriting the critical flag
         parentTask.getTotalSlack();
         parentTask.setCritical(critical);
      }
   }

   /**
    * Populates a field based on planned and actual values.
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
    * @param relationships activity relationships
    */
   private void processPredecessors(List<RelationshipType> relationships)
   {
      for (RelationshipType row : relationships)
      {
         Integer predecessorID = row.getPredecessorActivityObjectId();
         Integer successorID = row.getSuccessorActivityObjectId();

         Task successorTask = m_projectFile.getTaskByUniqueID(m_activityClashMap.getID(successorID));
         Task predecessorTask = m_projectFile.getTaskByUniqueID(m_activityClashMap.getID(predecessorID));

         RelationType type = RelationTypeHelper.getInstanceFromXml(row.getType());
         Duration lag = getDuration(row.getLag());
         String comments = nullIfEmpty(row.getComments());

         if (successorTask != null && predecessorTask != null)
         {
            Relation relation = successorTask.addPredecessor(new Relation.Builder()
               .predecessorTask(predecessorTask)
               .type(type)
               .lag(lag)
               .uniqueID(row.getObjectId())
               .notes(comments));
            m_eventManager.fireRelationReadEvent(relation);
         }
         else
         {
            // If we're missing the predecessor or successor we assume they are external relations
            if (successorTask != null && predecessorTask == null)
            {
               ExternalRelation relation = new ExternalRelation(row.getObjectId(), predecessorID, successorTask, type, lag, true, comments);
               m_externalRelations.add(relation);
            }
            else
            {
               if (successorTask == null && predecessorTask != null)
               {
                  ExternalRelation relation = new ExternalRelation(row.getObjectId(), successorID, predecessorTask, type, lag, false, comments);
                  m_externalRelations.add(relation);
               }
            }
         }
      }
   }

   private void processWorkContours(APIBusinessObjects apibo)
   {
      apibo.getResourceCurve().forEach(this::processWorkContour);
   }

   private void processWorkContour(ResourceCurveType curve)
   {
      if (m_projectFile.getWorkContours().getByUniqueID(curve.getObjectId()) != null)
      {
         return;
      }

      ResourceCurveValuesType curveValues = curve.getValues();

      double[] values =
      {
         NumberHelper.getDouble(curveValues.getValue0()),
         NumberHelper.getDouble(curveValues.getValue5()),
         NumberHelper.getDouble(curveValues.getValue10()),
         NumberHelper.getDouble(curveValues.getValue15()),
         NumberHelper.getDouble(curveValues.getValue20()),
         NumberHelper.getDouble(curveValues.getValue25()),
         NumberHelper.getDouble(curveValues.getValue30()),
         NumberHelper.getDouble(curveValues.getValue35()),
         NumberHelper.getDouble(curveValues.getValue40()),
         NumberHelper.getDouble(curveValues.getValue45()),
         NumberHelper.getDouble(curveValues.getValue50()),
         NumberHelper.getDouble(curveValues.getValue55()),
         NumberHelper.getDouble(curveValues.getValue60()),
         NumberHelper.getDouble(curveValues.getValue65()),
         NumberHelper.getDouble(curveValues.getValue70()),
         NumberHelper.getDouble(curveValues.getValue75()),
         NumberHelper.getDouble(curveValues.getValue80()),
         NumberHelper.getDouble(curveValues.getValue85()),
         NumberHelper.getDouble(curveValues.getValue90()),
         NumberHelper.getDouble(curveValues.getValue95()),
         NumberHelper.getDouble(curveValues.getValue100()),
      };

      m_projectFile.getWorkContours().add(new WorkContour(curve.getObjectId(), curve.getName(), BooleanHelper.getBoolean(curve.isIsDefault()), values));
   }

   /**
    * Process resource assignments.
    *
    * @param assignments project resource assignments
    */
   private void processAssignments(List<ResourceAssignmentType> assignments)
   {
      for (ResourceAssignmentType row : assignments)
      {
         Task task = m_projectFile.getTaskByUniqueID(m_activityClashMap.getID(row.getActivityObjectId()));
         Integer roleID = m_roleClashMap.getID(row.getRoleObjectId());
         Integer resourceID = row.getResourceObjectId();

         // If we don't have a resource ID, but we do have a role ID then the task is being assigned to a role
         if (resourceID == null && roleID != null)
         {
            resourceID = roleID;
            roleID = null;
         }

         Resource resource = m_projectFile.getResourceByUniqueID(resourceID);

         if (task != null && resource != null)
         {
            ProjectCalendar effectiveCalendar = task.getEffectiveCalendar();
            ResourceAssignment assignment = task.addResourceAssignment(resource);

            assignment.setUniqueID(row.getObjectId());
            assignment.setRemainingWork(getDuration(row.getRemainingUnits()));
            assignment.setPlannedWork(getDuration(row.getPlannedUnits()));
            assignment.setActualWork(getDuration(row.getActualUnits()));
            assignment.setRemainingCost(row.getRemainingCost());
            assignment.setPlannedCost(row.getPlannedCost());
            assignment.setActualCost(row.getActualCost());
            assignment.setActualStart(row.getActualStartDate());
            assignment.setActualFinish(row.getActualFinishDate());
            assignment.setPlannedStart(row.getPlannedStartDate());
            assignment.setPlannedFinish(row.getPlannedFinishDate());
            assignment.setGUID(DatatypeConverter.parseUUID(row.getGUID()));
            assignment.setActualOvertimeCost(row.getActualOvertimeCost());
            assignment.setActualOvertimeWork(getDuration(row.getActualOvertimeUnits()));
            assignment.setWorkContour(CurveHelper.getWorkContour(m_projectFile, row.getResourceCurveObjectId()));
            assignment.setRateIndex(RateTypeHelper.getInstanceFromXml(row.getRateType()));
            assignment.setRole(m_projectFile.getResourceByUniqueID(roleID));
            assignment.setOverrideRate(readRate(row.getCostPerQuantity()));
            assignment.setRateSource(RateSourceHelper.getInstanceFromXml(row.getRateSource()));
            assignment.setCalculateCostsFromUnits(BooleanHelper.getBoolean(row.isIsCostUnitsLinked()));
            assignment.setCostAccount(m_projectFile.getCostAccounts().getByUniqueID(row.getCostAccountObjectId()));
            assignment.setRemainingEarlyStart(row.getRemainingStartDate());
            assignment.setRemainingEarlyFinish(row.getRemainingFinishDate());

            populateField(assignment, AssignmentField.START, AssignmentField.ACTUAL_START, AssignmentField.REMAINING_EARLY_START, AssignmentField.PLANNED_START);
            populateField(assignment, AssignmentField.FINISH, AssignmentField.ACTUAL_FINISH, AssignmentField.REMAINING_EARLY_FINISH, AssignmentField.PLANNED_FINISH);

            // calculate work
            Duration remainingWork = assignment.getRemainingWork();
            Duration actualWork = assignment.getActualWork();
            Duration totalWork = Duration.add(actualWork, remainingWork, effectiveCalendar);
            assignment.setWork(totalWork);

            // calculate cost
            Number remainingCost = assignment.getRemainingCost();
            Number actualCost = assignment.getActualCost();
            Number atCompletionCost = NumberHelper.sumAsDouble(actualCost, remainingCost);
            assignment.setCost(atCompletionCost);

            // roll up to parent task
            task.setPlannedCost(NumberHelper.sumAsDouble(task.getPlannedCost(), assignment.getPlannedCost()));
            task.setActualCost(NumberHelper.sumAsDouble(task.getActualCost(), actualCost));
            task.setRemainingCost(NumberHelper.sumAsDouble(task.getRemainingCost(), remainingCost));
            task.setCost(NumberHelper.sumAsDouble(task.getCost(), atCompletionCost));

            assignment.setUnits(Double.valueOf(NumberHelper.getDouble(row.getPlannedUnitsPerTime()) * 100));
            assignment.setRemainingUnits(Double.valueOf(NumberHelper.getDouble(row.getRemainingUnitsPerTime()) * 100));

            // Add User Defined Fields
            populateUserDefinedFieldValues(assignment, row.getUDF());

            // Read timephased data
            assignment.setTimephasedPlannedWork(TimephasedHelper.read(effectiveCalendar, assignment.getPlannedStart(), row.getPlannedCurve()));
            assignment.setTimephasedActualWork(TimephasedHelper.read(effectiveCalendar, assignment.getActualStart(), row.getActualCurve()));
            assignment.setTimephasedWork(TimephasedHelper.read(effectiveCalendar, assignment.getRemainingEarlyStart(), row.getRemainingCurve()));

            processResourceAssignmentCodeAssignments(assignment, row.getCode());

            m_eventManager.fireAssignmentReadEvent(assignment);
         }
      }
   }

   /**
    * Process resource rates.
    *
    * @param apibo xml container
    */
   private void processResourceRates(APIBusinessObjects apibo)
   {
      List<ResourceRateType> rates = new ArrayList<>(apibo.getResourceRate());

      // Primavera defines resource cost tables by start dates so sort and define end by next
      rates.sort((r1, r2) -> {
         Integer id1 = r1.getResourceObjectId();
         Integer id2 = r2.getResourceObjectId();
         int cmp = NumberHelper.compare(id1, id2);
         if (cmp != 0)
         {
            return cmp;
         }
         LocalDateTime d1 = r1.getEffectiveDate();
         LocalDateTime d2 = r2.getEffectiveDate();
         return LocalDateTimeHelper.compare(d1, d2);
      });

      Resource resource = null;

      for (int i = 0; i < rates.size(); ++i)
      {
         ResourceRateType row = rates.get(i);

         Integer resourceID = row.getResourceObjectId();
         if (resource == null || !resource.getUniqueID().equals(resourceID))
         {
            resource = m_projectFile.getResourceByUniqueID(resourceID);
            if (resource == null)
            {
               continue;
            }
            resource.getCostRateTable(0).clear();
         }

         Rate[] values = new Rate[]
         {
            readRate(row.getPricePerUnit()),
            readRate(row.getPricePerUnit2()),
            readRate(row.getPricePerUnit3()),
            readRate(row.getPricePerUnit4()),
            readRate(row.getPricePerUnit5()),
         };

         Double costPerUse = NumberHelper.getDouble(0.0);
         Double maxUnits = NumberHelper.getDouble(NumberHelper.getDouble(row.getMaxUnitsPerTime()) * 100); // adjust to be % as in MS Project
         ShiftPeriod period = m_projectFile.getShiftPeriods().getByUniqueID(row.getShiftPeriodObjectId());
         LocalDateTime startDate = row.getEffectiveDate();
         LocalDateTime endDate = LocalDateTimeHelper.END_DATE_NA;

         if (i + 1 < rates.size())
         {
            ResourceRateType nextRow = rates.get(i + 1);
            if (NumberHelper.equals(resourceID, nextRow.getResourceObjectId()))
            {
               endDate = nextRow.getEffectiveDate().minusMinutes(1);
            }
         }

         if (startDate == null || startDate.isBefore(LocalDateTimeHelper.START_DATE_NA))
         {
            startDate = LocalDateTimeHelper.START_DATE_NA;
         }

         if (endDate == null || endDate.isAfter(LocalDateTimeHelper.END_DATE_NA))
         {
            endDate = LocalDateTimeHelper.END_DATE_NA;
         }

         resource.getCostRateTable(0).add(new CostRateTableEntry(startDate, endDate, costPerUse, period, values));
         resource.getAvailability().add(new Availability(startDate, endDate, maxUnits));
      }
   }

   /**
    * Read a rate value, handle null.
    *
    * @param value rate as a double
    * @return new Rate instance
    */
   private Rate readRate(Double value)
   {
      if (value == null)
      {
         return null;
      }

      return new Rate(value, TimeUnit.HOURS);
   }

   /**
    * Process role rates.
    *
    * @param apibo xml container
    */
   private void processRoleRates(APIBusinessObjects apibo)
   {
      List<RoleRateType> rates = new ArrayList<>(apibo.getRoleRateNew().isEmpty() ? apibo.getRoleRate() : apibo.getRoleRateNew());

      // Primavera defines resource cost tables by start dates so sort and define end by next
      rates.sort((r1, r2) -> {
         Integer id1 = r1.getRoleObjectId();
         Integer id2 = r2.getRoleObjectId();
         int cmp = NumberHelper.compare(id1, id2);
         if (cmp != 0)
         {
            return cmp;
         }
         LocalDateTime d1 = r1.getEffectiveDate();
         LocalDateTime d2 = r2.getEffectiveDate();
         return LocalDateTimeHelper.compare(d1, d2);
      });

      Resource resource = null;

      for (int i = 0; i < rates.size(); ++i)
      {
         RoleRateType row = rates.get(i);

         Integer resourceID = m_roleClashMap.getID(row.getRoleObjectId());
         if (resource == null || !resource.getUniqueID().equals(resourceID))
         {
            resource = m_projectFile.getResourceByUniqueID(resourceID);
            if (resource == null)
            {
               continue;
            }
            resource.getCostRateTable(0).clear();
         }

         Rate[] values = new Rate[]
         {
            readRate(row.getPricePerUnit()),
            readRate(row.getPricePerUnit2()),
            readRate(row.getPricePerUnit3()),
            readRate(row.getPricePerUnit4()),
            readRate(row.getPricePerUnit5()),
         };

         Double costPerUse = NumberHelper.getDouble(0.0);
         Double maxUnits = NumberHelper.getDouble(NumberHelper.getDouble(row.getMaxUnitsPerTime()) * 100); // adjust to be % as in MS Project
         LocalDateTime startDate = row.getEffectiveDate();
         LocalDateTime endDate = LocalDateTimeHelper.END_DATE_NA;

         if (i + 1 < rates.size())
         {
            RoleRateType nextRow = rates.get(i + 1);
            if (NumberHelper.equals(row.getRoleObjectId(), nextRow.getRoleObjectId()))
            {
               endDate = nextRow.getEffectiveDate().minusMinutes(1);
            }
         }

         if (startDate == null || startDate.isBefore(LocalDateTimeHelper.START_DATE_NA))
         {
            startDate = LocalDateTimeHelper.START_DATE_NA;
         }

         if (endDate == null || endDate.isAfter(LocalDateTimeHelper.END_DATE_NA))
         {
            endDate = LocalDateTimeHelper.END_DATE_NA;
         }

         resource.getCostRateTable(0).add(new CostRateTableEntry(startDate, endDate, costPerUse, values));
         resource.getAvailability().add(new Availability(startDate, endDate, maxUnits));
      }
   }

   /**
    * Process activity steps.
    *
    * @param activitySteps list of activity steps
    */
   private void processActivitySteps(List<ActivityStepType> activitySteps)
   {
      List<ActivityStepType> steps = new ArrayList<>(activitySteps);
      steps.sort(Comparator.comparing(ActivityStepType::getSequenceNumber));

      for (ActivityStepType activityStep : steps)
      {
         Task task = m_projectFile.getTaskByUniqueID(m_activityClashMap.getID(activityStep.getActivityObjectId()));
         if (task == null)
         {
            continue;
         }

         Step step = new Step.Builder(task)
            .name(activityStep.getName())
            .sequenceNumber(activityStep.getSequenceNumber())
            .uniqueID(activityStep.getObjectId())
            .weight(activityStep.getWeight())
            .percentComplete(Double.valueOf(NumberHelper.getDouble(activityStep.getPercentComplete()) * 100.0))
            .description(getHtmlNote(activityStep.getDescription()))
            .build();

         task.getSteps().add(step);
      }
   }

   /**
    * Populate notebook topics.
    *
    * @param apibo top level object
    */
   private void processNotebookTopics(APIBusinessObjects apibo)
   {
      apibo.getNotebookTopic().forEach(this::processNotebookTopic);
   }

   /**
    * Populate an individual notebook topic.
    *
    * @param xml notebook topic data
    */
   private void processNotebookTopic(NotebookTopicType xml)
   {
      NotesTopic topic = new NotesTopic.Builder(m_projectFile)
         .uniqueID(xml.getObjectId())
         .sequenceNumber(xml.getSequenceNumber())
         .availableForEPS(BooleanHelper.getBoolean(xml.isAvailableForEPS()))
         .availableForProject(BooleanHelper.getBoolean(xml.isAvailableForProject()))
         .availableForWBS(BooleanHelper.getBoolean(xml.isAvailableForWBS()))
         .availableForActivity(BooleanHelper.getBoolean(xml.isAvailableForActivity()))
         .name(xml.getName())
         .build();

      m_projectFile.getNotesTopics().add(topic);
   }

   /**
    * Retrieve notes attached to WBS entries.
    *
    * @param notes wbs notes
    * @return map of WBS notes
    */
   private Map<Integer, Notes> getWbsNotes(List<ProjectNoteType> notes)
   {
      // Project notes have a null WBS ID. We'll map this to zero to allow us to add them to the map.
      Map<Integer, List<ProjectNoteType>> map = notes.stream().collect(Collectors.groupingBy(n -> n.getWBSObjectId() == null ? Integer.valueOf(0) : n.getWBSObjectId(), Collectors.toList()));
      return map.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> new ParentNotes(e.getValue().stream().map(n -> getNote(n.getObjectId(), n.getNotebookTopicObjectId(), n.getNote())).filter(Objects::nonNull).collect(Collectors.toList()))));
   }

   /**
    * Retrieve notes attached to activity entries.
    *
    * @param notes activity notes
    * @return map of activity notes
    */
   private Map<Integer, Notes> getActivityNotes(List<ActivityNoteType> notes)
   {
      Map<Integer, List<ActivityNoteType>> map = notes.stream().filter(n -> n.getActivityObjectId() != null).collect(Collectors.groupingBy(ActivityNoteType::getActivityObjectId, Collectors.toList()));
      return map.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> new ParentNotes(e.getValue().stream().map(n -> getNote(n.getObjectId(), n.getNotebookTopicObjectId(), n.getNote())).filter(Objects::nonNull).collect(Collectors.toList()))));
   }

   private Notes getNote(Integer uniqueID, Integer topicID, String text)
   {
      HtmlNotes note = getHtmlNote(text);
      if (note == null || note.isEmpty())
      {
         return null;
      }

      NotesTopic topic = m_projectFile.getNotesTopics().getByUniqueID(topicID);
      if (topic == null)
      {
         topic = m_projectFile.getNotesTopics().getDefaultTopic();
      }

      return new StructuredNotes(m_projectFile, uniqueID, topic, note);
   }

   /**
    * Create an HtmlNote instance.
    *
    * @param text note text
    * @return HtmlNote instance
    */
   private HtmlNotes getHtmlNote(String text)
   {
      if (text == null || text.isEmpty())
      {
         return null;
      }

      // Remove BOM and NUL characters
      String html = text.replaceAll("[\\uFEFF\\uFFFE\\x00]", "");

      HtmlNotes result = new HtmlNotes(html);

      return result.isEmpty() ? null : result;
   }

   /**
    * Extracts a duration from a JAXBElement instance.
    *
    * @param duration duration expressed in hours
    * @return duration instance
    */
   private Duration getDuration(Double duration)
   {
      if (duration == null)
      {
         return null;
      }

      return Duration.getInstance(NumberHelper.getDouble(duration), TimeUnit.HOURS);
   }

   /**
    * The end of a Primavera time range finishes on the last minute
    * of the period, so a range of 12:00 -> 13:00 is represented by
    * Primavera as 12:00 -> 12:59.
    *
    * @param date Primavera end time
    * @return date MPXJ end time
    */
   private LocalTime getEndTime(LocalTime date)
   {
      return date.plusMinutes(1);
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
   private void populateUserDefinedFieldValues(FieldContainer mpxj, List<UDFAssignmentType> udfs)
   {
      for (UDFAssignmentType udf : udfs)
      {
         UserDefinedField fieldType = m_projectFile.getUserDefinedFields().getByUniqueID(Integer.valueOf(udf.getTypeObjectId()));
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
         ActivityCode activityCode = m_projectFile.getActivityCodes().getByUniqueID(Integer.valueOf(assignment.getTypeObjectId()));
         if (activityCode == null)
         {
            continue;
         }

         ActivityCodeValue activityCodeValue = activityCode.getValueByUniqueID(Integer.valueOf(assignment.getValueObjectId()));
         if (activityCodeValue != null)
         {
            task.addActivityCodeValue(activityCodeValue);
         }
      }
   }

   /**
    * Process schedule options.
    *
    * @param list list of schedule options
    */
   private void processScheduleOptions(List<ScheduleOptionsType> list)
   {
      if (list == null || list.isEmpty())
      {
         return;
      }

      ScheduleOptionsType options = list.get(0);

      ProjectProperties projectProperties = m_projectFile.getProjectProperties();

      //
      // Leveling Options
      //

      // Automatically level resources when scheduling
      projectProperties.setConsiderAssignmentsInOtherProjects(BooleanHelper.getBoolean(options.isIncludeExternalResAss()));
      projectProperties.setConsiderAssignmentsInOtherProjectsWithPriorityEqualHigherThan(options.getExternalProjectPriorityLimit());
      projectProperties.setPreserveScheduledEarlyAndLateDates(BooleanHelper.getBoolean(options.isPreserveScheduledEarlyAndLateDates()));

      // Recalculate assignment costs after leveling
      projectProperties.setLevelAllResources(BooleanHelper.getBoolean(options.isLevelAllResources()));
      projectProperties.setLevelResourcesOnlyWithinActivityTotalFloat(BooleanHelper.getBoolean(options.isLevelWithinFloat()));
      projectProperties.setPreserveMinimumFloatWhenLeveling(options.getMinFloatToPreserve() == null ? null : Duration.getInstance(options.getMinFloatToPreserve().intValue(), TimeUnit.HOURS));
      projectProperties.setMaxPercentToOverallocateResources(options.getOverAllocationPercentage());
      projectProperties.setLevelingPriorities(options.getPriorityList());

      //
      // Schedule
      //
      // Set Data Date and Planned Date to Project Forecast Start not in PMXML?
      // Unsure how to enable forecasting in P6 to test this.
      //projectProperties.setDataDateAndPlannedStartSetToProjectForecastStart();

      //
      // Schedule Options - General
      //
      projectProperties.setIgnoreRelationshipsToAndFromOtherProjects(BooleanHelper.getBoolean(options.isIgnoreOtherProjectRelationships()));
      projectProperties.setMakeOpenEndedActivitiesCritical(BooleanHelper.getBoolean(options.isMakeOpenEndedActivitiesCritical()));
      projectProperties.setUseExpectedFinishDates(BooleanHelper.getBoolean(options.isUseExpectedFinishDates()));

      // Schedule automatically when a change affects dates - not in PMXML?

      // Level resources during scheduling - not in PMXML?
      projectProperties.setComputeStartToStartLagFromEarlyStart(BooleanHelper.getBoolean(options.isStartToStartLagCalculationType()));
      projectProperties.setSchedulingProgressedActivities(SchedulingProgressedActivitiesHelper.getInstanceFromXml(options.getOutOfSequenceScheduleType()));

      // Define critical activities as
      projectProperties.setCalculateFloatBasedOnFinishDateOfEachProject(BooleanHelper.getBoolean(options.isCalculateFloatBasedOnFinishDate()));

      // NOTE: this also appears as a project attribute, this one takes precedence
      projectProperties.setRelationshipLagCalendar(RelationshipLagCalendarHelper.getInstanceFromXml(options.getRelationshipLagCalendar()));
      projectProperties.setTotalSlackCalculationType(TotalSlackCalculationTypeHelper.getInstanceFromXml(options.getComputeTotalFloatType()));

      //
      // Schedule Options - Advanced
      //
      projectProperties.setCalculateMultipleFloatPaths(BooleanHelper.getBoolean(options.isMultipleFloatPathsEnabled()));
      projectProperties.setCalculateMultipleFloatPathsUsingTotalFloat(BooleanHelper.getBoolean(options.isMultipleFloatPathsUseTotalFloat()));
      projectProperties.setDisplayMultipleFloatPathsEndingWithActivityUniqueID(options.getMultipleFloatPathsEndingActivityObjectId());
      projectProperties.setMaximumNumberOfFloatPathsToCalculate(options.getMaximumMultipleFloatPaths());
   }

   private void rollupValues()
   {
      m_projectFile.getChildTasks().forEach(this::rollupCalendars);
      m_projectFile.getChildTasks().forEach(this::rollupDates);
      m_projectFile.getChildTasks().forEach(this::rollupWork);
      m_projectFile.getChildTasks().forEach(this::rollupCosts);

      if (m_projectFile.getProjectProperties().getBaselineProjectUniqueID() == null)
      {
         m_projectFile.getTasks().stream().filter(Task::getSummary).forEach(this::populateBaselineFromCurrentProject);
      }
   }

   /**
    * See the notes above.
    *
    * @param parentTask parent task
    */
   private void rollupCosts(Task parentTask)
   {
      if (parentTask.hasChildTasks())
      {
         double plannedCost = 0;
         double actualCost = 0;
         double remainingCost = 0;
         double cost = 0;
         double fixedCost = 0;

         //process children first before adding their costs
         for (Task child : parentTask.getChildTasks())
         {
            rollupCosts(child);
            plannedCost += NumberHelper.getDouble(child.getPlannedCost());
            actualCost += NumberHelper.getDouble(child.getActualCost());
            remainingCost += NumberHelper.getDouble(child.getRemainingCost());
            cost += NumberHelper.getDouble(child.getCost());
            fixedCost += NumberHelper.getDouble(child.getFixedCost());
         }

         parentTask.setPlannedCost(NumberHelper.getDouble(plannedCost));
         parentTask.setActualCost(NumberHelper.getDouble(actualCost));
         parentTask.setRemainingCost(NumberHelper.getDouble(remainingCost));
         parentTask.setCost(NumberHelper.getDouble(cost));
         parentTask.setFixedCost(NumberHelper.getDouble(fixedCost));
      }
   }

   /**
    * The Primavera WBS entries we read in as tasks don't have work entered. We try
    * to compensate for this by summing the child tasks' work. This method recursively
    * descends through the tasks to do this.
    *
    * @param parentTask parent task.
    */
   private void rollupWork(Task parentTask)
   {
      if (parentTask.hasChildTasks())
      {
         ProjectCalendar calendar = parentTask.getEffectiveCalendar();

         Duration actualWork = null;
         Duration plannedWork = null;
         Duration remainingWork = null;
         Duration work = null;

         for (Task task : parentTask.getChildTasks())
         {
            rollupWork(task);

            actualWork = Duration.add(actualWork, task.getActualWork(), calendar);
            plannedWork = Duration.add(plannedWork, task.getPlannedWork(), calendar);
            remainingWork = Duration.add(remainingWork, task.getRemainingWork(), calendar);
            work = Duration.add(work, task.getWork(), calendar);
         }

         parentTask.setActualWork(actualWork);
         parentTask.setPlannedWork(plannedWork);
         parentTask.setRemainingWork(remainingWork);
         parentTask.setWork(work);
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
         CONTEXT = JAXBContext.newInstance("org.mpxj.primavera.schema", PrimaveraPMFileReader.class.getClassLoader());
      }

      catch (JAXBException ex)
      {
         CONTEXT_EXCEPTION = ex;
         CONTEXT = null;
      }
   }

   private Integer m_projectID;
   private ProjectFile m_projectFile;
   private ProjectFileSharedData m_shared;
   private EventManager m_eventManager;
   private ClashMap m_activityClashMap;
   private ClashMap m_roleClashMap;
   private List<ExternalRelation> m_externalRelations;
   private boolean m_linkCrossProjectRelations;
   private BaselineStrategy m_baselineStrategy = PrimaveraBaselineStrategy.PLANNED_ATTRIBUTES;

   private static final Map<String, DayOfWeek> DAY_MAP = new HashMap<>();
   static
   {
      // Current PMXML schema
      DAY_MAP.put("Monday", DayOfWeek.MONDAY);
      DAY_MAP.put("Tuesday", DayOfWeek.TUESDAY);
      DAY_MAP.put("Wednesday", DayOfWeek.WEDNESDAY);
      DAY_MAP.put("Thursday", DayOfWeek.THURSDAY);
      DAY_MAP.put("Friday", DayOfWeek.FRIDAY);
      DAY_MAP.put("Saturday", DayOfWeek.SATURDAY);
      DAY_MAP.put("Sunday", DayOfWeek.SUNDAY);

      // Older (6.2?) schema
      DAY_MAP.put("1", DayOfWeek.SUNDAY);
      DAY_MAP.put("2", DayOfWeek.MONDAY);
      DAY_MAP.put("3", DayOfWeek.TUESDAY);
      DAY_MAP.put("4", DayOfWeek.WEDNESDAY);
      DAY_MAP.put("5", DayOfWeek.THURSDAY);
      DAY_MAP.put("6", DayOfWeek.FRIDAY);
      DAY_MAP.put("7", DayOfWeek.SATURDAY);
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

   private static final WbsRowComparatorPMXML WBS_ROW_COMPARATOR = new WbsRowComparatorPMXML();

   private static final Pattern ENCODING_PATTERN = Pattern.compile(".*<\\?xml.*encoding=\"([^\"]+)\".*\\?>.*", Pattern.DOTALL);
   private static final LocalTime NON_WORKING_END_TIME = LocalTime.of(23, 59);
}
