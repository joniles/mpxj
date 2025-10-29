package org.mpxj.primavera;

import java.sql.Time;
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
import org.mpxj.EventManager;
import org.mpxj.ExpenseItem;
import org.mpxj.FieldContainer;
import org.mpxj.FieldType;
import org.mpxj.HtmlNotes;
import org.mpxj.Notes;
import org.mpxj.NotesTopic;
import org.mpxj.ParentNotes;
import org.mpxj.PercentCompleteType;
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

class XmlProjectReader
{
   public XmlProjectReader(ProjectFile projectFile, List<ExternalRelation> externalRelations)
   {
      m_projectFile = projectFile;
      m_externalRelations = externalRelations;
      m_eventManager = m_projectFile.getEventManager();
   }

   public ProjectFile read(APIBusinessObjects apibo, Object projectObject, ClashMap roleClashMap)
   {
      // TODO
      // addListenersToProject(m_projectFile);

      try
      {
         m_activityClashMap = new ClashMap();
         m_roleClashMap = roleClashMap;

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

         processGlobalProperties(apibo);
         XmlReaderHelper.processActivityCodeDefinitions(m_projectFile.getProjectContext(), activityCodeTypes, activityCodes);
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
         m_activityClashMap = null;
         m_roleClashMap = null;
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
            populateBaselineFromCurrentProject(task);
         }

         m_eventManager.fireTaskReadEvent(task);
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
            assignment.setTimephasedPlannedWork(TimephasedHelper.read(effectiveCalendar, assignment.getPlannedStart(), row.getPlannedCurve()));
            assignment.setTimephasedActualWork(TimephasedHelper.read(effectiveCalendar, assignment.getActualStart(), row.getActualCurve()));
            assignment.setTimephasedWork(TimephasedHelper.read(effectiveCalendar, assignment.getRemainingEarlyStart(), row.getRemainingCurve()));

            processResourceAssignmentCodeAssignments(assignment, row.getCode());

            m_eventManager.fireAssignmentReadEvent(assignment);
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

   private void populateBaselineFromCurrentProject(Task task)
   {
      task.setBaselineCost(task.getPlannedCost());
      task.setBaselineDuration(task.getPlannedDuration());
      task.setBaselineFinish(task.getPlannedFinish());
      task.setBaselineStart(task.getPlannedStart());
      task.setBaselineWork(task.getPlannedWork());
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

      return new StructuredNotes(m_projectFile, uniqueID, topic, note);
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

   private final ProjectFile m_projectFile;
   private final EventManager m_eventManager;
   private ClashMap m_activityClashMap;
   private ClashMap m_roleClashMap;
   private final List<ExternalRelation> m_externalRelations;

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