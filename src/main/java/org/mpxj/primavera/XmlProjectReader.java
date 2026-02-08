/*
 * file:       XmlProjectReader.java
 * author:     Jon Iles
 * date:       2025-11-12
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.mpxj.ActivityCode;
import org.mpxj.ActivityCodeValue;
import org.mpxj.AssignmentField;
import org.mpxj.ChildTaskContainer;
import org.mpxj.CriticalActivityType;
import org.mpxj.Duration;
import org.mpxj.ExpenseItem;
import org.mpxj.FieldContainer;
import org.mpxj.FieldType;
import org.mpxj.HtmlNotes;
import org.mpxj.Notes;
import org.mpxj.NotesTopic;
import org.mpxj.ParentNotes;
import org.mpxj.ProjectCalendar;
import org.mpxj.ProjectCode;
import org.mpxj.ProjectCodeValue;
import org.mpxj.ProjectFile;
import org.mpxj.ProjectProperties;
import org.mpxj.Rate;
import org.mpxj.Relation;
import org.mpxj.RelationType;
import org.mpxj.Resource;
import org.mpxj.ResourceAssignment;
import org.mpxj.ResourceAssignmentCode;
import org.mpxj.ResourceAssignmentCodeValue;
import org.mpxj.Step;
import org.mpxj.StructuredNotes;
import org.mpxj.Task;
import org.mpxj.TaskField;
import org.mpxj.TimeUnit;
import org.mpxj.common.BooleanHelper;
import org.mpxj.common.DayOfWeekHelper;
import org.mpxj.common.LocalDateTimeHelper;
import org.mpxj.common.NumberHelper;
import org.mpxj.cpm.PrimaveraSlackCalculator;
import org.mpxj.primavera.schema.APIBusinessObjects;
import org.mpxj.primavera.schema.ActivityCodeType;
import org.mpxj.primavera.schema.ActivityCodeTypeType;
import org.mpxj.primavera.schema.ActivityExpenseType;
import org.mpxj.primavera.schema.ActivityNoteType;
import org.mpxj.primavera.schema.ActivityStepType;
import org.mpxj.primavera.schema.ActivityType;
import org.mpxj.primavera.schema.BaselineProjectType;
import org.mpxj.primavera.schema.CodeAssignmentType;
import org.mpxj.primavera.schema.CurrencyType;
import org.mpxj.primavera.schema.GlobalPreferencesType;
import org.mpxj.primavera.schema.ProjectNoteType;
import org.mpxj.primavera.schema.ProjectType;
import org.mpxj.primavera.schema.RelationshipType;
import org.mpxj.primavera.schema.ResourceAssignmentType;
import org.mpxj.primavera.schema.ScheduleOptionsType;
import org.mpxj.primavera.schema.WBSType;

/**
 * Populate a ProjectFile instance from an XML file.
 */
class XmlProjectReader
{
   /**
    * Constructor.
    *
    * @param state common state data
    */
   public XmlProjectReader(XmlReaderState state)
   {
      m_state = state;
   }

   /**
    * Populate a ProjectFile instance by reading data from an XML file.
    *
    * @param projectObject project data source
    * @return populated ProjectFile instance
    */
   public ProjectFile read(Object projectObject)
   {
      try
      {
         m_projectFile = new ProjectFile(m_state.getContext());
         m_activityClashMap = new ClashMap();

         m_projectFile.getProjectConfig().setSlackClaculator(new PrimaveraSlackCalculator());
         m_projectFile.getProjectProperties().setFileApplication("Primavera");
         m_projectFile.getProjectProperties().setFileType("PMXML");

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
            XmlReaderHelper.processCalendars(m_projectFile.getProjectContext(), project.getCalendar());
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
            XmlReaderHelper.processCalendars(m_projectFile.getProjectContext(), project.getCalendar());
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

         processGlobalProperties(m_state.getApibo());
         XmlReaderHelper.processActivityCodeDefinitions(m_projectFile.getProjectContext(), activityCodeTypes, activityCodes);
         processProjectCodeAssignments(codes);
         processTasks(wbs, wbsNotes, activities, getActivityNotes(activityNotes));
         processPredecessors(relationships);
         processAssignments(assignments);
         processExpenseItems(activityExpenseType);
         processActivitySteps(steps);
         RollupHelper.rollupValues(m_projectFile);

         m_projectFile.updateStructure();
         m_projectFile.readComplete();

         return m_projectFile;
      }

      finally
      {
         m_projectFile = null;
         m_activityClashMap = null;
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
      properties.setActivityPercentCompleteBasedOnActivitySteps(BooleanHelper.getBoolean(project.isActivityPercentCompleteBasedOnActivitySteps()));
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
      properties.setActivityDefaultCalendarUniqueID(project.getActivityDefaultCalendarObjectId());
      properties.setActivityIdPrefix(project.getActivityIdPrefix());
      properties.setActivityIdSuffix(project.getActivityIdSuffix());
      properties.setActivityIdIncrement(project.getActivityIdIncrement());
      properties.setActivityIdIncrementBasedOnSelectedActivity(BooleanHelper.getBoolean(project.isActivityIdBasedOnSelectedActivity()));
      properties.setProjectWebsiteUrl(nullIfEmpty(project.getWebSiteURL()));
      properties.setEnablePublication(BooleanHelper.getBoolean(project.isEnablePublication()));
      properties.setEnableSummarization(BooleanHelper.getBoolean(project.isEnableSummarization()));

      if (properties.getDefaultCalendar() == null)
      {
         m_projectFile.getProjectProperties().setDefaultCalendarUniqueID(properties.getActivityDefaultCalendarUniqueID());
      }

      processScheduleOptions(project.getScheduleOptions());
      XmlReaderHelper.populateUserDefinedFieldValues(m_projectFile.getProjectContext(), properties, project.getUDF());
   }

   private void processProjectProperties(BaselineProjectType project)
   {
      ProjectProperties properties = m_projectFile.getProjectProperties();

      properties.setActivityPercentCompleteBasedOnActivitySteps(BooleanHelper.getBoolean(project.isActivityPercentCompleteBasedOnActivitySteps()));
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
      properties.setActivityDefaultCalendarUniqueID(project.getActivityDefaultCalendarObjectId());
      properties.setBaselineTypeName(project.getBaselineTypeName());
      properties.setBaselineTypeUniqueID(project.getBaselineTypeObjectId());
      properties.setLastBaselineUpdateDate(project.getLastBaselineUpdateDate());
      properties.setLastScheduledDate(project.getLastScheduleDate());
      properties.setProjectIsBaseline(true);
      properties.setProjectWebsiteUrl(project.getWebSiteURL());
      properties.setEnablePublication(BooleanHelper.getBoolean(project.isEnablePublication()));
      properties.setEnableSummarization(BooleanHelper.getBoolean(project.isEnableSummarization()));

      if (properties.getDefaultCalendar() == null)
      {
         m_projectFile.getProjectProperties().setDefaultCalendarUniqueID(properties.getActivityDefaultCalendarUniqueID());
      }

      processScheduleOptions(project.getScheduleOptions());
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
         task.setOriginalBudget(row.getOriginalBudget());
         task.setEstimatedWeight(row.getEstimatedWeight());

         XmlReaderHelper.populateUserDefinedFieldValues(m_projectFile.getProjectContext(), task, row.getUDF());
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
         task.setEstimatedWeight(row.getEstimatedWeight());
         task.setAutoComputeActuals(BooleanHelper.getBoolean(row.isAutoComputeActuals()));

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

         XmlReaderHelper.populateUserDefinedFieldValues(m_projectFile.getProjectContext(), task, row.getUDF());
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
            BaselineHelper.populateBaselineFromCurrentProject(task);
         }

         m_projectFile.getEventManager().fireTaskReadEvent(task);
      }

      new ActivitySorter(wbsTasks).sort(m_projectFile);

      updateStructure();
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
            m_projectFile.getEventManager().fireRelationReadEvent(relation);
         }
         else
         {
            // If we're missing the predecessor or successor we assume they are external relations
            if (successorTask != null && predecessorTask == null)
            {
               ExternalRelation relation = new ExternalRelation(row.getObjectId(), predecessorID, successorTask, type, lag, true, comments);
               m_state.getExternalRelations().add(relation);
            }
            else
            {
               if (successorTask == null && predecessorTask != null)
               {
                  ExternalRelation relation = new ExternalRelation(row.getObjectId(), successorID, predecessorTask, type, lag, false, comments);
                  m_state.getExternalRelations().add(relation);
               }
            }
         }
      }
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
         Integer roleID = m_state.getRoleClashMap().getID(row.getRoleObjectId());
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
            assignment.setOverrideRate(Rate.valueOf(row.getCostPerQuantity(), TimeUnit.HOURS));
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
            XmlReaderHelper.populateUserDefinedFieldValues(m_projectFile.getProjectContext(), assignment, row.getUDF());

            // Read timephased data
            assignment.getRawTimephasedPlannedWork().addAll(TimephasedHelper.read(effectiveCalendar, assignment.getPlannedStart(), row.getPlannedCurve()));
            assignment.getRawTimephasedActualRegularWork().addAll(TimephasedHelper.read(effectiveCalendar, assignment.getActualStart(), row.getActualCurve()));
            assignment.getRawTimephasedRemainingRegularWork().addAll(TimephasedHelper.read(effectiveCalendar, assignment.getRemainingEarlyStart(), row.getRemainingCurve()));

            processResourceAssignmentCodeAssignments(assignment, row.getCode());

            m_projectFile.getEventManager().fireAssignmentReadEvent(assignment);
         }
      }
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
            .description(NotesHelper.getHtmlNote(activityStep.getDescription()))
            .build();

         task.getSteps().add(step);
      }
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

   private Notes getNote(Integer uniqueID, Integer topicID, String text)
   {
      HtmlNotes note = NotesHelper.getHtmlNote(text);
      if (note == null || note.isEmpty())
      {
         return null;
      }

      NotesTopic topic = m_projectFile.getNotesTopics().getByUniqueID(topicID);
      if (topic == null)
      {
         topic = m_projectFile.getNotesTopics().getDefaultTopic();
      }

      return new StructuredNotes(m_state.getContext(), uniqueID, topic, note);
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

   private final XmlReaderState m_state;
   private ProjectFile m_projectFile;
   private ClashMap m_activityClashMap;

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
}