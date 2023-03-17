/*
 * file:       PrimaveraPMProjectWriter.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2022
 * date:       2022-08-15
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

import java.awt.Color;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import net.sf.mpxj.AccrueType;
import net.sf.mpxj.ActivityCode;
import net.sf.mpxj.ActivityCodeScope;
import net.sf.mpxj.ActivityCodeValue;
import net.sf.mpxj.Availability;
import net.sf.mpxj.AvailabilityTable;
import net.sf.mpxj.CostAccount;
import net.sf.mpxj.CostRateTable;
import net.sf.mpxj.CostRateTableEntry;
import net.sf.mpxj.CurrencySymbolPosition;
import net.sf.mpxj.CustomField;
import net.sf.mpxj.CustomFieldContainer;
import net.sf.mpxj.DataType;
import net.sf.mpxj.DateRange;
import net.sf.mpxj.Day;
import net.sf.mpxj.Duration;
import net.sf.mpxj.ExpenseCategory;
import net.sf.mpxj.ExpenseItem;
import net.sf.mpxj.FieldContainer;
import net.sf.mpxj.FieldType;
import net.sf.mpxj.FieldTypeClass;
import net.sf.mpxj.HtmlNotes;
import net.sf.mpxj.Notes;
import net.sf.mpxj.ParentNotes;
import net.sf.mpxj.PercentCompleteType;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectCalendarException;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ProjectProperties;
import net.sf.mpxj.Rate;
import net.sf.mpxj.RateSource;
import net.sf.mpxj.Relation;
import net.sf.mpxj.Resource;
import net.sf.mpxj.ResourceAssignment;
import net.sf.mpxj.Step;
import net.sf.mpxj.StructuredNotes;
import net.sf.mpxj.Task;
import net.sf.mpxj.TaskField;
import net.sf.mpxj.TaskType;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.WorkContour;
import net.sf.mpxj.common.BooleanHelper;
import net.sf.mpxj.common.DateHelper;
import net.sf.mpxj.common.FieldLists;
import net.sf.mpxj.common.FieldTypeHelper;
import net.sf.mpxj.common.HtmlHelper;
import net.sf.mpxj.common.NumberHelper;
import net.sf.mpxj.common.ProjectCalendarHelper;
import net.sf.mpxj.common.RateHelper;
import net.sf.mpxj.primavera.schema.APIBusinessObjects;
import net.sf.mpxj.primavera.schema.ActivityCodeType;
import net.sf.mpxj.primavera.schema.ActivityCodeTypeType;
import net.sf.mpxj.primavera.schema.ActivityExpenseType;
import net.sf.mpxj.primavera.schema.ActivityNoteType;
import net.sf.mpxj.primavera.schema.ActivityStepType;
import net.sf.mpxj.primavera.schema.ActivityType;
import net.sf.mpxj.primavera.schema.BaselineProjectType;
import net.sf.mpxj.primavera.schema.CalendarType;
import net.sf.mpxj.primavera.schema.CalendarType.HolidayOrExceptions;
import net.sf.mpxj.primavera.schema.CalendarType.HolidayOrExceptions.HolidayOrException;
import net.sf.mpxj.primavera.schema.CalendarType.StandardWorkWeek;
import net.sf.mpxj.primavera.schema.CalendarType.StandardWorkWeek.StandardWorkHours;
import net.sf.mpxj.primavera.schema.CodeAssignmentType;
import net.sf.mpxj.primavera.schema.CostAccountType;
import net.sf.mpxj.primavera.schema.CurrencyType;
import net.sf.mpxj.primavera.schema.ExpenseCategoryType;
import net.sf.mpxj.primavera.schema.NotebookTopicType;
import net.sf.mpxj.primavera.schema.ObjectFactory;
import net.sf.mpxj.primavera.schema.ProjectNoteType;
import net.sf.mpxj.primavera.schema.ProjectType;
import net.sf.mpxj.primavera.schema.RelationshipType;
import net.sf.mpxj.primavera.schema.ResourceAssignmentType;
import net.sf.mpxj.primavera.schema.ResourceCurveType;
import net.sf.mpxj.primavera.schema.ResourceCurveValuesType;
import net.sf.mpxj.primavera.schema.ResourceRateType;
import net.sf.mpxj.primavera.schema.ResourceType;
import net.sf.mpxj.primavera.schema.RoleRateType;
import net.sf.mpxj.primavera.schema.RoleType;
import net.sf.mpxj.primavera.schema.UDFAssignmentType;
import net.sf.mpxj.primavera.schema.UDFTypeType;
import net.sf.mpxj.primavera.schema.WBSType;
import net.sf.mpxj.primavera.schema.WorkTimeType;

final class PrimaveraPMProjectWriter
{
   public PrimaveraPMProjectWriter(APIBusinessObjects apibo, ProjectFile projectFile, Integer projectObjectID, PrimaveraPMObjectSequences sequences)
   {
      m_projectFile = projectFile;
      m_apibo = apibo;
      m_projectObjectID = projectObjectID;
      m_sequences = sequences;
   }

   public void writeProject()
   {
      write(false);
   }

   public void writeBaseline()
   {
      write(true);
   }

   private void write(boolean baseline)
   {
      try
      {
         m_factory = new ObjectFactory();
         m_topics = new HashMap<>();
         m_activityTypePopulated = m_projectFile.getTasks().getPopulatedFields().contains(TaskField.ACTIVITY_TYPE);
         m_wbsSequence = new ObjectSequence(0);
         m_userDefinedFields = getUserDefinedFieldsSet();

         if (baseline)
         {
            BaselineProjectType project = m_factory.createBaselineProjectType();
            m_apibo.getBaselineProject().add(project);

            m_wbs = project.getWBS();
            m_activities = project.getActivity();
            m_activitySteps = project.getActivityStep();
            m_assignments = project.getResourceAssignment();
            m_relationships = project.getRelationship();
            m_expenses = project.getActivityExpense();
            m_projectNotes = project.getProjectNote();
            m_activityNotes = project.getActivityNote();
            m_udf = project.getUDF();

            writeProjectProperties(project);
            writeTasks();
            writeAssignments();
            writeExpenseItems();
            writeActivitySteps();
         }
         else
         {
            ProjectType project = m_factory.createProjectType();
            m_apibo.getProject().add(project);

            m_wbs = project.getWBS();
            m_activities = project.getActivity();
            m_activitySteps = project.getActivityStep();
            m_assignments = project.getResourceAssignment();
            m_relationships = project.getRelationship();
            m_expenses = project.getActivityExpense();
            m_projectNotes = project.getProjectNote();
            m_activityNotes = project.getActivityNote();
            m_udf = project.getUDF();

            writeProjectProperties(project);
            writeUDF();
            writeActivityCodes();
            writeCurrency();
            writeUserDefinedFieldDefinitions();
            writeExpenseCategories();
            writeCostAccounts();
            writeResourceCurves();
            writeCalendars();
            writeResources();
            writeRoles();
            writeResourceRates();
            writeRoleRates();
            writeTasks();
            writeAssignments();
            writeExpenseItems();
            writeActivitySteps();
            writeTopics();
         }
      }

      finally
      {
         m_factory = null;
         m_wbsSequence = null;
         m_userDefinedFields = null;
         m_topics = null;
      }
   }

   private void writeUDF()
   {
      m_udf.addAll(writeUserDefinedFieldAssignments(FieldTypeClass.PROJECT, m_projectFile.getProjectProperties()));
   }

   /**
    * Create a handful of default currencies to keep Primavera happy.
    */
   private void writeCurrency()
   {
      ProjectProperties props = m_projectFile.getProjectProperties();
      CurrencyType currency = m_factory.createCurrencyType();
      m_apibo.getCurrency().add(currency);

      String positiveSymbol = getCurrencyFormat(props.getSymbolPosition());
      String negativeSymbol = "(" + positiveSymbol + ")";

      currency.setDecimalPlaces(props.getCurrencyDigits());
      currency.setDecimalSymbol(getSymbolName(props.getDecimalSeparator()));
      currency.setDigitGroupingSymbol(getSymbolName(props.getThousandsSeparator()));
      currency.setExchangeRate(Double.valueOf(1.0));
      currency.setId("CUR");
      currency.setName("Default Currency");
      currency.setNegativeSymbol(negativeSymbol);
      currency.setObjectId(DEFAULT_CURRENCY_ID);
      currency.setPositiveSymbol(positiveSymbol);
      currency.setSymbol(props.getCurrencySymbol());
   }

   /**
    * Map the currency separator character to a symbol name.
    *
    * @param c currency separator character
    * @return symbol name
    */
   private String getSymbolName(char c)
   {
      String result = null;

      switch (c)
      {
         case ',':
         {
            result = "Comma";
            break;
         }

         case '.':
         {
            result = "Period";
            break;
         }
      }

      return result;
   }

   /**
    * Generate a currency format.
    *
    * @param position currency symbol position
    * @return currency format
    */
   private String getCurrencyFormat(CurrencySymbolPosition position)
   {
      String result;

      switch (position)
      {
         case AFTER:
         {
            result = "1.1#";
            break;
         }

         case AFTER_WITH_SPACE:
         {
            result = "1.1 #";
            break;
         }

         case BEFORE_WITH_SPACE:
         {
            result = "# 1.1";
            break;
         }

         default:
         case BEFORE:
         {
            result = "#1.1";
            break;
         }
      }

      return result;
   }

   /**
    * Add UDFType objects to a PM XML file.
    *
    * @author kmahan
    * @author lsong
    */
   private void writeUserDefinedFieldDefinitions()
   {
      List<UDFTypeType> fields = m_apibo.getUDFType();

      for (FieldType type : m_userDefinedFields)
      {
         CustomField field = m_projectFile.getCustomFields().get(type);
         String title = field != null && field.getAlias() != null && !field.getAlias().isEmpty() ? field.getAlias() : type.getName();
         Integer uniqueID = field == null ? Integer.valueOf(FieldTypeHelper.getFieldID(type)) : field.getUniqueID();

         DataType dataType = type.getDataType();
         if (dataType == DataType.CUSTOM)
         {
            dataType = DataType.BINARY;
         }

         UDFTypeType udf = m_factory.createUDFTypeType();
         udf.setObjectId(uniqueID);
         udf.setDataType(inferUserFieldDataType(dataType));
         udf.setSubjectArea(inferUserFieldSubjectArea(type));
         udf.setTitle(title);
         fields.add(udf);
      }

      fields.sort(Comparator.comparing(UDFTypeType::getObjectId));
   }

   /**
    * Infers the Primavera entity type based on the MPXJ field type.
    *
    * @author lsong
    * @param fieldType MPXJ field type
    * @return UDF subject area
    */
   private String inferUserFieldSubjectArea(FieldType fieldType)
   {
      String result = SUBJECT_AREA_MAP.get(fieldType.getFieldTypeClass());
      if (result == null)
      {
         throw new RuntimeException("Unrecognized field type: " + fieldType);
      }
      return result;
   }

   /**
    * Infers the Primavera user defined field data type from the MPXJ data type.
    *
    * @author kmahan
    * @param dataType MPXJ data type
    * @return string representation of data type
    */
   private String inferUserFieldDataType(DataType dataType)
   {
      switch (dataType)
      {
         case BINARY:
         case STRING:
         case DURATION:
            return "Text";
         case DATE:
            return "Start Date";
         case NUMERIC:
            return "Double";
         case BOOLEAN:
         case INTEGER:
         case SHORT:
            return "Integer";
         case CURRENCY:
            return "Cost";
         default:
            throw new RuntimeException("Unconvertible data type: " + dataType);
      }
   }

   /**
    * Write expense categories.
    */
   private void writeExpenseCategories()
   {
      List<ExpenseCategoryType> expenseCategories = m_apibo.getExpenseCategory();
      for (ExpenseCategory category : m_projectFile.getExpenseCategories())
      {
         ExpenseCategoryType ect = m_factory.createExpenseCategoryType();
         ect.setObjectId(category.getUniqueID());
         ect.setName(category.getName());
         ect.setSequenceNumber(category.getSequenceNumber());
         expenseCategories.add(ect);
      }
   }

   /**
    * Write cost accounts.
    */
   private void writeCostAccounts()
   {
      List<CostAccountType> costAccounts = m_apibo.getCostAccount();
      for (CostAccount account : m_projectFile.getCostAccounts())
      {
         CostAccountType cat = m_factory.createCostAccountType();
         cat.setObjectId(account.getUniqueID());
         cat.setId(account.getID());
         cat.setName(account.getName());
         cat.setDescription(account.getDescription());
         cat.setSequenceNumber(account.getSequenceNumber());

         if (account.getParent() != null)
         {
            cat.setParentObjectId(account.getParent().getUniqueID());
         }

         costAccounts.add(cat);
      }
   }

   private void writeResourceCurves()
   {
      List<WorkContour> contours = m_projectFile.getWorkContours().stream().filter(w -> !w.isContourManual() && !w.isContourFlat()).sorted(Comparator.comparing(WorkContour::getName)).collect(Collectors.toList());

      List<ResourceCurveType> curves = m_apibo.getResourceCurve();
      for (WorkContour contour : contours)
      {
         ResourceCurveType curve = m_factory.createResourceCurveType();
         curves.add(curve);
         curve.setObjectId(contour.getUniqueID());
         curve.setName(contour.getName());
         ResourceCurveValuesType values = m_factory.createResourceCurveValuesType();
         curve.setValues(values);
         values.setValue0(Double.valueOf(contour.getCurveValues()[0]));
         values.setValue5(Double.valueOf(contour.getCurveValues()[1]));
         values.setValue10(Double.valueOf(contour.getCurveValues()[2]));
         values.setValue15(Double.valueOf(contour.getCurveValues()[3]));
         values.setValue20(Double.valueOf(contour.getCurveValues()[4]));
         values.setValue25(Double.valueOf(contour.getCurveValues()[5]));
         values.setValue30(Double.valueOf(contour.getCurveValues()[6]));
         values.setValue35(Double.valueOf(contour.getCurveValues()[7]));
         values.setValue40(Double.valueOf(contour.getCurveValues()[8]));
         values.setValue45(Double.valueOf(contour.getCurveValues()[9]));
         values.setValue50(Double.valueOf(contour.getCurveValues()[10]));
         values.setValue55(Double.valueOf(contour.getCurveValues()[11]));
         values.setValue60(Double.valueOf(contour.getCurveValues()[12]));
         values.setValue65(Double.valueOf(contour.getCurveValues()[13]));
         values.setValue70(Double.valueOf(contour.getCurveValues()[14]));
         values.setValue75(Double.valueOf(contour.getCurveValues()[15]));
         values.setValue80(Double.valueOf(contour.getCurveValues()[16]));
         values.setValue85(Double.valueOf(contour.getCurveValues()[17]));
         values.setValue90(Double.valueOf(contour.getCurveValues()[18]));
         values.setValue95(Double.valueOf(contour.getCurveValues()[19]));
         values.setValue100(Double.valueOf(contour.getCurveValues()[20]));
      }
   }

   /**
    * This method writes project properties data to a PMXML file.
    *
    * @param project project
    */
   private void writeProjectProperties(ProjectType project)
   {
      ProjectProperties mpxj = m_projectFile.getProjectProperties();
      String projectID = getProjectID(mpxj);

      //
      // P6 import may fail if planned start is not populated
      //
      Date plannedStart = Optional.ofNullable(Optional.ofNullable(mpxj.getPlannedStart()).orElseGet(mpxj::getStartDate)).orElseGet(mpxj::getCurrentDate);

      project.setActivityDefaultActivityType(ActivityTypeHelper.getXmlFromInstance(net.sf.mpxj.ActivityType.TASK_DEPENDENT));
      project.setActivityDefaultCalendarObjectId(mpxj.getDefaultCalendarUniqueID());
      project.setActivityDefaultDurationType(TaskTypeHelper.getXmlFromInstance(TaskType.FIXED_DURATION_AND_UNITS));
      project.setActivityDefaultPercentCompleteType(PercentCompleteTypeHelper.getXmlFromInstance(PercentCompleteType.DURATION));
      project.setActivityDefaultPricePerUnit(NumberHelper.DOUBLE_ZERO);
      project.setActivityIdBasedOnSelectedActivity(Boolean.TRUE);
      project.setActivityIdIncrement(Integer.valueOf(10));
      project.setActivityIdPrefix("A");
      project.setActivityIdSuffix(Integer.valueOf(1000));
      project.setActivityPercentCompleteBasedOnActivitySteps(Boolean.FALSE);
      project.setAddActualToRemaining(Boolean.FALSE);
      project.setAllowNegativeActualUnitsFlag(Boolean.FALSE);
      project.setAssignmentDefaultDrivingFlag(Boolean.TRUE);
      project.setAssignmentDefaultRateType(RateTypeHelper.getXmlFromInstance(Integer.valueOf(0)));
      project.setCheckOutStatus(Boolean.FALSE);
      project.setCostQuantityRecalculateFlag(Boolean.FALSE);
      project.setCreateDate(mpxj.getCreationDate());
      project.setCriticalActivityFloatLimit(Double.valueOf(mpxj.getCriticalSlackLimit().convertUnits(TimeUnit.HOURS, mpxj).getDuration()));
      project.setCriticalActivityPathType(CriticalActivityTypeHelper.getXmlFromInstance(mpxj.getCriticalActivityType()));
      project.setCurrentBaselineProjectObjectId(mpxj.getBaselineProjectUniqueID());
      project.setDataDate(m_projectFile.getProjectProperties().getStatusDate());
      project.setDefaultPriceTimeUnits("Hour");
      project.setDiscountApplicationPeriod("Month");
      project.setEarnedValueComputeType("Activity Percent Complete");
      project.setEarnedValueETCComputeType("ETC = Remaining Cost for Activity");
      project.setEarnedValueETCUserValue(Double.valueOf(0.88));
      project.setEarnedValueUserPercent(Double.valueOf(0.06));
      project.setEnableSummarization(Boolean.TRUE);
      project.setFiscalYearStartMonth(Integer.valueOf(1));
      project.setFinishDate(mpxj.getFinishDate());
      project.setGUID(DatatypeConverter.printUUID(mpxj.getGUID()));
      project.setId(projectID);
      project.setLastUpdateDate(mpxj.getLastSaved());
      project.setLevelingPriority(Integer.valueOf(10));
      project.setLinkActualToActualThisPeriod(Boolean.TRUE);
      project.setLinkPercentCompleteWithActual(Boolean.TRUE);
      project.setLinkPlannedAndAtCompletionFlag(Boolean.TRUE);
      project.setMustFinishByDate(mpxj.getMustFinishBy());
      project.setName(mpxj.getName() == null ? projectID : mpxj.getName());
      project.setObjectId(m_projectObjectID);
      project.setPlannedStartDate(plannedStart);
      project.setPrimaryResourcesCanMarkActivitiesAsCompleted(Boolean.TRUE);
      project.setResetPlannedToRemainingFlag(Boolean.FALSE);
      project.setResourceCanBeAssignedToSameActivityMoreThanOnce(Boolean.TRUE);
      project.setResourcesCanAssignThemselvesToActivities(Boolean.TRUE);
      project.setResourcesCanEditAssignmentPercentComplete(Boolean.FALSE);
      project.setResourcesCanMarkAssignmentAsCompleted(Boolean.FALSE);
      project.setResourcesCanViewInactiveActivities(Boolean.FALSE);
      project.setRiskLevel("Medium");
      project.setScheduledFinishDate(mpxj.getScheduledFinish());
      project.setStartDate(mpxj.getStartDate());
      project.setStatus("Active");
      project.setStrategicPriority(Integer.valueOf(500));
      project.setSummarizeToWBSLevel(Integer.valueOf(2));
      project.setSummaryLevel("Assignment Level");
      project.setUseProjectBaselineForEarnedValue(Boolean.TRUE);
      project.setWBSCodeSeparator(PrimaveraReader.DEFAULT_WBS_SEPARATOR);
   }

   private void writeProjectProperties(BaselineProjectType project)
   {
      ProjectProperties mpxj = m_projectFile.getProjectProperties();
      String projectID = getProjectID(mpxj);

      //
      // P6 import may fail if planned start is not populated
      //
      Date plannedStart = Optional.ofNullable(mpxj.getPlannedStart()).orElseGet(mpxj::getStartDate);

      project.setActivityDefaultActivityType(ActivityTypeHelper.getXmlFromInstance(net.sf.mpxj.ActivityType.TASK_DEPENDENT));
      project.setActivityDefaultCalendarObjectId(mpxj.getDefaultCalendarUniqueID());
      project.setActivityDefaultDurationType(TaskTypeHelper.getXmlFromInstance(TaskType.FIXED_DURATION_AND_UNITS));
      project.setActivityDefaultPercentCompleteType(PercentCompleteTypeHelper.getXmlFromInstance(PercentCompleteType.DURATION));
      project.setActivityDefaultPricePerUnit(NumberHelper.DOUBLE_ZERO);
      project.setActivityIdBasedOnSelectedActivity(Boolean.TRUE);
      project.setActivityIdIncrement(Integer.valueOf(10));
      project.setActivityIdPrefix("A");
      project.setActivityIdSuffix(Integer.valueOf(1000));
      project.setActivityPercentCompleteBasedOnActivitySteps(Boolean.FALSE);
      project.setAddActualToRemaining(Boolean.FALSE);
      project.setAssignmentDefaultDrivingFlag(Boolean.TRUE);
      project.setAssignmentDefaultRateType(RateTypeHelper.getXmlFromInstance(Integer.valueOf(0)));
      project.setCheckOutStatus(Boolean.FALSE);
      project.setCostQuantityRecalculateFlag(Boolean.FALSE);
      project.setCreateDate(mpxj.getCreationDate());
      project.setCriticalActivityFloatLimit(Double.valueOf(mpxj.getCriticalSlackLimit().convertUnits(TimeUnit.HOURS, mpxj).getDuration()));
      project.setCriticalActivityPathType(CriticalActivityTypeHelper.getXmlFromInstance(mpxj.getCriticalActivityType()));
      project.setDataDate(m_projectFile.getProjectProperties().getStatusDate());
      project.setDefaultPriceTimeUnits("Hour");
      project.setDiscountApplicationPeriod("Month");
      project.setEnableSummarization(Boolean.TRUE);
      project.setFiscalYearStartMonth(Integer.valueOf(1));
      project.setFinishDate(mpxj.getFinishDate());
      project.setGUID(DatatypeConverter.printUUID(mpxj.getGUID()));
      project.setId(projectID);
      project.setLastUpdateDate(mpxj.getLastSaved());
      project.setLevelingPriority(Integer.valueOf(10));
      project.setLinkActualToActualThisPeriod(Boolean.TRUE);
      project.setLinkPercentCompleteWithActual(Boolean.TRUE);
      project.setLinkPlannedAndAtCompletionFlag(Boolean.TRUE);
      project.setMustFinishByDate(mpxj.getMustFinishBy());
      project.setName(mpxj.getName() == null ? projectID : mpxj.getName());
      project.setObjectId(m_projectObjectID);
      project.setPlannedStartDate(plannedStart);
      project.setPrimaryResourcesCanMarkActivitiesAsCompleted(Boolean.TRUE);
      project.setResetPlannedToRemainingFlag(Boolean.FALSE);
      project.setResourceCanBeAssignedToSameActivityMoreThanOnce(Boolean.TRUE);
      project.setResourcesCanAssignThemselvesToActivities(Boolean.TRUE);
      project.setResourcesCanEditAssignmentPercentComplete(Boolean.FALSE);
      project.setRiskLevel("Medium");
      project.setStartDate(mpxj.getStartDate());
      project.setStatus("Active");
      project.setStrategicPriority(Integer.valueOf(500));
      project.setSummarizeToWBSLevel(Integer.valueOf(2));
      project.setWBSCodeSeparator(PrimaveraReader.DEFAULT_WBS_SEPARATOR);
   }

   private String getProjectID(ProjectProperties mpxj)
   {
      String result = mpxj.getProjectID();
      if (result == null)
      {
         int id = m_sequences.getProjectID().intValue();
         if (id == 0)
         {
            result = DEFAULT_PROJECT_ID;
         }
         else
         {
            result = DEFAULT_PROJECT_ID + "-" + id;
         }
      }
      return result;
   }

   /**
    * This method writes calendar data to a PM XML file.
    */
   private void writeCalendars()
   {
      for (ProjectCalendar calendar : m_projectFile.getCalendars())
      {
         writeCalendar(normalizeCalendar(calendar));
      }
   }

   /**
    * Tries to ensure that the calendar structure we write matches P6's expectations.
    *
    * @param calendar calendar to normalize
    * @return normalized calendar
    */
   private ProjectCalendar normalizeCalendar(ProjectCalendar calendar)
   {
      ProjectCalendar result = calendar;
      if (calendar.getType() == net.sf.mpxj.CalendarType.GLOBAL && calendar.isDerived())
      {
         // Global calendars in P6 are not derived from other calendars.
         // If this calendar is marked as a global calendar, and it is
         // derived then we'll flatten it.
         result = ProjectCalendarHelper.createTemporaryFlattenedCalendar(calendar);
      }
      return result;
   }

   /**
    * This method writes data for an individual calendar to a PM XML file.
    *
    * @param mpxj ProjectCalendar instance
    */
   private void writeCalendar(ProjectCalendar mpxj)
   {
      CalendarType xml = m_factory.createCalendarType();
      m_apibo.getCalendar().add(xml);

      String name = mpxj.getName();
      if (name == null || name.isEmpty())
      {
         name = "(blank)";
      }

      xml.setBaseCalendarObjectId(mpxj.getParentUniqueID());
      xml.setIsDefault(Boolean.valueOf(mpxj == m_projectFile.getDefaultCalendar()));
      xml.setIsPersonal(Boolean.valueOf(mpxj.getPersonal()));
      xml.setName(name);
      xml.setObjectId(mpxj.getUniqueID());
      xml.setType(CalendarTypeHelper.getXmlFromInstance(mpxj.getType()));

      xml.setHoursPerDay(Double.valueOf(NumberHelper.getDouble(mpxj.getMinutesPerDay()) / 60.0));
      xml.setHoursPerWeek(Double.valueOf(NumberHelper.getDouble(mpxj.getMinutesPerWeek()) / 60.0));
      xml.setHoursPerMonth(Double.valueOf(NumberHelper.getDouble(mpxj.getMinutesPerMonth()) / 60.0));
      xml.setHoursPerYear(Double.valueOf(NumberHelper.getDouble(mpxj.getMinutesPerYear()) / 60.0));

      StandardWorkWeek xmlStandardWorkWeek = m_factory.createCalendarTypeStandardWorkWeek();
      xml.setStandardWorkWeek(xmlStandardWorkWeek);

      for (Day day : EnumSet.allOf(Day.class))
      {
         StandardWorkHours xmlHours = m_factory.createCalendarTypeStandardWorkWeekStandardWorkHours();
         xmlStandardWorkWeek.getStandardWorkHours().add(xmlHours);
         xmlHours.setDayOfWeek(getDayName(day));

         // Working days/hours are not inherited between calendars, just exceptions.
         for (DateRange range : mpxj.getHours(day))
         {
            WorkTimeType xmlWorkTime = m_factory.createWorkTimeType();
            xmlHours.getWorkTime().add(xmlWorkTime);

            xmlWorkTime.setStart(range.getStart());
            xmlWorkTime.setFinish(getEndTime(range.getEnd()));
         }
      }

      HolidayOrExceptions xmlExceptions = m_factory.createCalendarTypeHolidayOrExceptions();
      xml.setHolidayOrExceptions(xmlExceptions);

      List<ProjectCalendarException> expandedExceptions = ProjectCalendarHelper.getExpandedExceptionsWithWorkWeeks(mpxj);
      if (!expandedExceptions.isEmpty())
      {
         Calendar calendar = DateHelper.popCalendar();
         Set<Date> exceptionDates = new HashSet<>();

         for (ProjectCalendarException mpxjException : expandedExceptions)
         {
            calendar.setTime(mpxjException.getFromDate());
            while (calendar.getTimeInMillis() < mpxjException.getToDate().getTime())
            {
               Date exceptionDate = calendar.getTime();

               // Prevent duplicate exception dates being written.
               // P6 will fail to import files with duplicate exceptions.
               if (exceptionDates.add(exceptionDate))
               {
                  HolidayOrException xmlException = m_factory.createCalendarTypeHolidayOrExceptionsHolidayOrException();
                  xmlExceptions.getHolidayOrException().add(xmlException);

                  xmlException.setDate(exceptionDate);

                  for (DateRange range : mpxjException)
                  {
                     WorkTimeType xmlHours = m_factory.createWorkTimeType();
                     xmlException.getWorkTime().add(xmlHours);

                     xmlHours.setStart(range.getStart());

                     if (range.getEnd() != null)
                     {
                        xmlHours.setFinish(getEndTime(range.getEnd()));
                     }
                  }
               }
               calendar.add(Calendar.DAY_OF_YEAR, 1);
            }
         }
         DateHelper.pushCalendar(calendar);
      }
   }

   /**
    * This method writes resource data to a PMXML file.
    */
   private void writeResources()
   {
      m_projectFile.getResources().stream().filter(r -> !BooleanHelper.getBoolean(r.getRole()) && r.getUniqueID().intValue() != 0).forEach(this::writeResource);
   }

   /**
    * Write a single resource.
    *
    * @param mpxj Resource instance
    */
   private void writeResource(Resource mpxj)
   {
      ResourceType xml = m_factory.createResourceType();
      m_apibo.getResource().add(xml);

      String name = mpxj.getName();
      if (name == null || name.isEmpty())
      {
         name = "(blank)";
      }

      // Note: a default units per time value of zero represents an empty field in P6
      Number maxUnits = mpxj.getMaxUnits();
      Double defaultUnitsPerTime = maxUnits == null ? Double.valueOf(0) : Double.valueOf(maxUnits.doubleValue() / 100.0);

      xml.setAutoComputeActuals(Boolean.TRUE);
      xml.setCalculateCostFromUnits(Boolean.valueOf(mpxj.getCalculateCostsFromUnits()));
      xml.setCalendarObjectId(mpxj.getCalendarUniqueID());
      xml.setCurrencyObjectId(DEFAULT_CURRENCY_ID);
      xml.setEmailAddress(mpxj.getEmailAddress());
      xml.setEmployeeId(mpxj.getCode());
      xml.setGUID(DatatypeConverter.printUUID(mpxj.getGUID()));
      xml.setId(getResourceID(mpxj));
      xml.setIsActive(Boolean.valueOf(mpxj.getActive()));
      xml.setName(name);
      xml.setObjectId(mpxj.getUniqueID());
      xml.setParentObjectId(mpxj.getParentResourceUniqueID());
      xml.setResourceNotes(getNotes(mpxj.getNotesObject()));
      xml.setResourceType(ResourceTypeHelper.getXmlFromInstance(mpxj.getType()));
      xml.setSequenceNumber(mpxj.getSequenceNumber());

      // Write both attributes for backward compatibility,
      // "DefaultUnitsPerTime" is the value read by recent versions of P6
      // MaxUnitsPerTime is ignored
      xml.setDefaultUnitsPerTime(defaultUnitsPerTime);
      xml.setMaxUnitsPerTime(defaultUnitsPerTime);

      xml.getUDF().addAll(writeUserDefinedFieldAssignments(FieldTypeClass.RESOURCE, mpxj));
   }

   /**
    * This method writes role data to a PMXML file.
    */
   private void writeRoles()
   {
      m_projectFile.getResources().stream().filter(r -> BooleanHelper.getBoolean(r.getRole()) && r.getUniqueID().intValue() != 0).forEach(this::writeRole);
   }

   /**
    * Write a single role.
    *
    * @param mpxj Resource instance
    */
   private void writeRole(Resource mpxj)
   {
      RoleType xml = m_factory.createRoleType();
      m_apibo.getRole().add(xml);

      xml.setObjectId(mpxj.getUniqueID());
      xml.setName(mpxj.getName());
      xml.setId(mpxj.getResourceID());
      xml.setCalculateCostFromUnits(Boolean.valueOf(mpxj.getCalculateCostsFromUnits()));
      xml.setResponsibilities(getNotes(mpxj.getNotesObject()));
      xml.setSequenceNumber(mpxj.getSequenceNumber());
   }

   /**
    * Retrieve the resource notes text. If an HTML representation
    * is already available, use that, otherwise generate HTML from
    * the plain text of the note.
    *
    * @param notes notes text
    * @return Notes instance
    */
   private String getNotes(Notes notes)
   {
      String result;
      if (notes == null || notes.isEmpty())
      {
         // TODO: switch to null to remove the tag - check import
         result = "";
      }
      else
      {
         result = notes instanceof HtmlNotes ? ((HtmlNotes) notes).getHtml() : HtmlHelper.getHtmlFromPlainText(notes.toString());
      }

      return result;
   }

   /**
    * This method writes task data to a PMXML file.
    */
   private void writeTasks()
   {
      // We're jumping through hoops here to try and ensure that the PMXML
      // we generate is close to what P6 would generate. Makes for easier comparison...
      // Anyhow, it looks like P6 exports WBS and tasks in unique ID order, so we'll sort
      // to match this... but, we also need to generate a sequence number for the WBS entries before
      // we do this to ensure that they are ordered correctly on import.

      // Filter out WBS entries and generate a sequence number.
      // If it's a summary task... it's a WBS entry. If the task has come from P6, and the activity type is not set, it's a WBS entry
      Map<Task, Integer> wbsSequence = m_projectFile.getTasks().stream().filter(t -> t.getSummary() || (m_activityTypePopulated && t.getActivityType() == null)).collect(Collectors.toMap(t -> t, t -> t.getSequenceNumber() == null ? m_wbsSequence.getNext() : t.getSequenceNumber()));

      // Sort the tasks into unique ID order
      List<Task> tasks = new ArrayList<>(m_projectFile.getTasks());
      tasks.sort((t1, t2) -> NumberHelper.compare(t1.getUniqueID(), t2.getUniqueID()));

      // Write the tasks
      tasks.forEach(t -> writeTask(t, wbsSequence.get(t)));
   }

   /**
    * Given a Task instance, this task determines if it should be written to the
    * PMXML file as an activity or as a WBS item, and calls the appropriate
    * method.
    *
    * @param task Task instance
    * @param wbsSequence null if this is an activity, wbs sequence otherwise
    */
   private void writeTask(Task task, Integer wbsSequence)
   {
      if (!task.getNull() && task.getUniqueID().intValue() != 0)
      {
         if (wbsSequence != null)
         {
            writeWBS(task, wbsSequence);
         }
         else
         {
            writeActivity(task);
         }
      }
   }

   /**
    * Writes a WBS entity to the PM XML file.
    *
    * @param mpxj MPXJ Task entity
    * @param sequence number for this WBS entry
    */
   private void writeWBS(Task mpxj, Integer sequence)
   {
      WBSType xml = m_factory.createWBSType();
      m_wbs.add(xml);

      String name = mpxj.getName();
      if (name == null || name.isEmpty())
      {
         name = "(blank)";
      }

      xml.setCode(getWbsCode(mpxj));
      xml.setGUID(DatatypeConverter.printUUID(mpxj.getGUID()));
      xml.setName(name);

      xml.setObjectId(mpxj.getUniqueID());
      xml.setParentObjectId(mpxj.getParentTaskUniqueID());
      xml.setProjectObjectId(m_projectObjectID);
      xml.setSequenceNumber(sequence);

      xml.setStatus("Active");

      // TODO: we don't currently write WBS UDF values as we don't distinguish between WBS and Activity UDFs
      // xml.getUDF().addAll(writeUDFType(FieldTypeClass.TASK, mpxj));
      writeWbsNote(mpxj);
   }

   /**
    * Retrieve the WBS code attribute.
    *
    * @param task Task instance
    * @return WBS code attribute
    */
   private String getWbsCode(Task task)
   {
      // If we don't have a WBS code, use a default value
      String code = task.getWBS();
      if (code == null || code.length() == 0)
      {
         code = DEFAULT_WBS_CODE;
      }
      else
      {
         String prefix = null;

         if (task.getParentTask() == null && m_projectFile.getProjectProperties().getProjectID() != null)
         {
            prefix = m_projectFile.getProjectProperties().getProjectID() + PrimaveraReader.DEFAULT_WBS_SEPARATOR;
         }
         else
         {
            if (task.getParentTask() != null)
            {
               prefix = task.getParentTask().getWBS() + PrimaveraReader.DEFAULT_WBS_SEPARATOR;
            }
         }

         // If we have a parent task, and it looks like WBS contains the full path
         // (including the parent's WBS), remove the parent's WBS. This matches
         // how P6 exports this value. This test is brittle as it assumes
         // the default WBS separator has been used.
         if (prefix != null && code.startsWith(prefix))
         {
            code = code.substring(prefix.length());
         }
      }
      return code;
   }

   /**
    * Writes an activity to a PM XML file.
    *
    * @param mpxj MPXJ Task instance
    */
   private void writeActivity(Task mpxj)
   {
      ActivityType xml = m_factory.createActivityType();
      m_activities.add(xml);

      Integer parentTaskUniqueID = mpxj.getParentTaskUniqueID();
      Integer parentObjectID = null;
      if (parentTaskUniqueID != null && parentTaskUniqueID.intValue() != 0)
      {
         parentObjectID = parentTaskUniqueID;
      }

      String name = mpxj.getName();
      if (name == null || name.isEmpty())
      {
         name = "(blank)";
      }

      // Not required, but keeps Asta import happy if we ensure that planned start and finish are populated.
      Date plannedStart = mpxj.getPlannedStart() == null ? mpxj.getStart() : mpxj.getPlannedStart();
      Date plannedFinish = mpxj.getPlannedFinish() == null ? mpxj.getFinish() : mpxj.getPlannedFinish();
      ProjectCalendar effectiveCalendar = mpxj.getEffectiveCalendar();

      xml.setActualStartDate(mpxj.getActualStart());
      xml.setActualDuration(getDuration(mpxj.getActualDuration()));
      xml.setActualFinishDate(mpxj.getActualFinish());
      xml.setAtCompletionDuration(getDuration(mpxj.getDuration()));
      xml.setCalendarObjectId(effectiveCalendar == null ? null : effectiveCalendar.getUniqueID());
      xml.setDurationPercentComplete(getPercentage(mpxj.getPercentageComplete()));
      xml.setDurationType(TaskTypeHelper.getXmlFromInstance(mpxj.getType()));
      xml.setExternalEarlyStartDate(mpxj.getExternalEarlyStart());
      xml.setExternalLateFinishDate(mpxj.getExternalLateFinish());
      xml.setFinishDate(mpxj.getFinish());
      xml.setGUID(DatatypeConverter.printUUID(mpxj.getGUID()));
      xml.setId(mpxj.getActivityID() == null ? mpxj.getWBS() : mpxj.getActivityID());
      // Note that P6 doesn't write this attribute to PMXML, but appears to read it
      xml.setIsLongestPath(BooleanHelper.getBoolean(mpxj.getLongestPath()) ? Boolean.TRUE : null);
      xml.setLevelingPriority(PriorityHelper.getXmlFromInstance(mpxj.getPriority()));
      xml.setName(name);
      xml.setObjectId(mpxj.getUniqueID());
      xml.setPercentCompleteType(PercentCompleteTypeHelper.getXmlFromInstance(mpxj.getPercentCompleteType()));
      xml.setPercentComplete(getPercentComplete(mpxj));
      xml.setPhysicalPercentComplete(getPercentage(mpxj.getPhysicalPercentComplete()));
      xml.setPrimaryConstraintType(ConstraintTypeHelper.getXmlFromInstance(mpxj.getConstraintType()));
      xml.setPrimaryConstraintDate(mpxj.getConstraintDate());
      xml.setPrimaryResourceObjectId(mpxj.getPrimaryResourceID());
      xml.setPlannedDuration(getDuration(mpxj.getPlannedDuration() == null ? mpxj.getDuration() : mpxj.getPlannedDuration()));
      xml.setPlannedFinishDate(plannedFinish);
      xml.setPlannedStartDate(plannedStart);
      xml.setProjectObjectId(m_projectObjectID);
      xml.setRemainingDuration(getDuration(mpxj.getRemainingDuration()));
      xml.setRemainingEarlyFinishDate(mpxj.getRemainingEarlyFinish());
      xml.setRemainingEarlyStartDate(mpxj.getRemainingEarlyStart());
      xml.setRemainingLateFinishDate(mpxj.getRemainingLateFinish());
      xml.setRemainingLateStartDate(mpxj.getRemainingLateStart());
      xml.setRemainingLaborCost(NumberHelper.DOUBLE_ZERO);
      xml.setRemainingLaborUnits(NumberHelper.DOUBLE_ZERO);
      xml.setRemainingNonLaborCost(NumberHelper.DOUBLE_ZERO);
      xml.setRemainingNonLaborUnits(NumberHelper.DOUBLE_ZERO);

      // Trying to ensure data from other scheduling applications makes sense in P6.
      // We won't populate the resume date unless we have a suspend date,
      // i.e. the activity has been suspended.
      if (mpxj.getSuspendDate() != null)
      {
         xml.setResumeDate(mpxj.getResume());
      }

      xml.setSecondaryConstraintDate(mpxj.getSecondaryConstraintDate());
      xml.setSecondaryConstraintType(ConstraintTypeHelper.getXmlFromInstance(mpxj.getSecondaryConstraintType()));
      xml.setStartDate(mpxj.getStart());
      xml.setStatus(ActivityStatusHelper.getXmlFromInstance(ActivityStatusHelper.getActivityStatus(mpxj)));
      xml.setSuspendDate(mpxj.getSuspendDate());
      xml.setType(ActivityTypeHelper.getXmlFromInstance(mpxj.getActivityType()));
      xml.setUnitsPercentComplete(getPercentage(mpxj.getPercentageWorkComplete()));
      xml.setWBSObjectId(parentObjectID);
      xml.getUDF().addAll(writeUserDefinedFieldAssignments(FieldTypeClass.TASK, mpxj));

      writeActivityNote(mpxj);
      writePredecessors(mpxj);
      writeActivityCodeAssignments(mpxj, xml);
   }

   /**
    * Writes assignment data to a PM XML file.
    */
   private void writeAssignments()
   {
      List<ResourceAssignment> assignments = new ArrayList<>();
      m_projectFile.getTasks().forEach(t -> assignments.addAll(t.getResourceAssignments()));

      for (ResourceAssignment assignment : assignments)
      {
         Resource resource = assignment.getResource();
         if (resource != null)
         {
            Task task = assignment.getTask();
            if (task != null && task.getUniqueID().intValue() != 0 && !task.getSummary())
            {
               writeAssignment(assignment);
            }
         }
      }
   }

   /**
    * Writes a resource assignment to a PM XML file.
    *
    * @param mpxj MPXJ ResourceAssignment instance
    */
   private void writeAssignment(ResourceAssignment mpxj)
   {
      ResourceAssignmentType xml = m_factory.createResourceAssignmentType();
      m_assignments.add(xml);

      Task task = mpxj.getTask();

      //
      // P6 import may fail if planned start, planned finish, and actual overtime units are not populated
      //
      Double actualOvertimeUnits = Optional.ofNullable(getDuration(mpxj.getActualOvertimeWork())).orElse(NumberHelper.DOUBLE_ZERO);
      Date plannedStart = Optional.ofNullable(mpxj.getPlannedStart()).orElseGet(mpxj::getStart);
      plannedStart = Optional.ofNullable(plannedStart).orElseGet(task::getStart);
      // If we can't find any finish date to use we'll fall back on using the start date which we'll assume is always populated
      Date plannedFinish = Optional.ofNullable(mpxj.getPlannedFinish()).orElseGet(mpxj::getFinish);
      plannedFinish = Optional.ofNullable(plannedFinish).orElseGet(task::getFinish);
      plannedFinish = Optional.ofNullable(plannedFinish).orElse(plannedStart);

      if (BooleanHelper.getBoolean(mpxj.getResource().getRole()))
      {
         xml.setRoleObjectId(mpxj.getResourceUniqueID());
      }
      else
      {
         xml.setResourceObjectId(mpxj.getResourceUniqueID());
         xml.setRoleObjectId(mpxj.getRoleUniqueID());
      }

      xml.setActivityObjectId(mpxj.getTaskUniqueID());
      xml.setActualCost(getDouble(mpxj.getActualCost()));
      xml.setActualFinishDate(mpxj.getActualFinish());
      xml.setActualOvertimeCost(getDouble(mpxj.getActualOvertimeCost()));
      xml.setActualOvertimeUnits(actualOvertimeUnits);
      xml.setActualRegularUnits(getDuration(mpxj.getActualWork()));
      xml.setActualStartDate(mpxj.getActualStart());
      xml.setActualUnits(getDuration(mpxj.getActualWork()));
      xml.setAtCompletionCost(NumberHelper.sumAsDouble(mpxj.getActualCost(), mpxj.getRemainingCost()));
      xml.setAtCompletionUnits(getDuration(Duration.add(mpxj.getActualWork(), mpxj.getRemainingWork(), task.getEffectiveCalendar())));
      xml.setResourceCurveObjectId(CurveHelper.getCurveID(mpxj.getWorkContour()));
      xml.setFinishDate(mpxj.getFinish());
      xml.setGUID(DatatypeConverter.printUUID(mpxj.getGUID()));
      xml.setIsCostUnitsLinked(Boolean.valueOf(mpxj.getCalculateCostsFromUnits()));
      xml.setObjectId(mpxj.getUniqueID());
      xml.setPlannedCost(getDouble(mpxj.getPlannedCost()));
      xml.setPlannedFinishDate(plannedFinish);
      xml.setPlannedStartDate(plannedStart);
      xml.setPlannedUnits(getDuration(mpxj.getPlannedWork()));
      xml.setPlannedUnitsPerTime(getPercentage(mpxj.getUnits()));
      xml.setProjectObjectId(m_projectObjectID);
      xml.setRemainingCost(getDouble(mpxj.getRemainingCost()));
      xml.setRemainingDuration(getDuration(mpxj.getRemainingWork()));
      xml.setRemainingUnits(getDuration(mpxj.getRemainingWork()));
      xml.setRemainingUnitsPerTime(getPercentage(mpxj.getUnits()));
      xml.setStartDate(mpxj.getStart());
      xml.setWBSObjectId(task.getParentTaskUniqueID());
      xml.getUDF().addAll(writeUserDefinedFieldAssignments(FieldTypeClass.ASSIGNMENT, mpxj));
      xml.setRateType(RateTypeHelper.getXmlFromInstance(mpxj.getRateIndex()));
      xml.setCostPerQuantity(writeRate(mpxj.getOverrideRate()));
      xml.setRateSource(RATE_SOURCE_MAP.get(mpxj.getRateSource()));
      xml.setCostAccountObjectId(mpxj.getCostAccountUniqueID());
   }

   /**
    * Writes task predecessor links to a PM XML file.
    *
    * @param task MPXJ Task instance
    */
   private void writePredecessors(Task task)
   {
      List<Relation> relations = task.getPredecessors();
      for (Relation mpxj : relations)
      {
         RelationshipType xml = m_factory.createRelationshipType();
         m_relationships.add(xml);

         xml.setLag(getDuration(mpxj.getLag()));
         xml.setObjectId(m_sequences.getRelationshipObjectID());
         xml.setPredecessorActivityObjectId(mpxj.getTargetTask().getUniqueID());
         xml.setSuccessorActivityObjectId(mpxj.getSourceTask().getUniqueID());
         xml.setPredecessorProjectObjectId(m_projectObjectID);
         xml.setSuccessorProjectObjectId(m_projectObjectID);
         xml.setType(RelationTypeHelper.getXmlFromInstance(mpxj.getType()));
      }
   }

   /**
    * Write all expense items for project.
    */
   private void writeExpenseItems()
   {
      List<Task> tasks = new ArrayList<>(m_projectFile.getTasks());
      tasks.sort((t1, t2) -> NumberHelper.compare(t1.getUniqueID(), t2.getUniqueID()));

      tasks.forEach(this::writeExpenseItems);
   }

   /**
    * Write expense items for a task.
    *
    * @param task Task instance
    */
   private void writeExpenseItems(Task task)
   {
      List<ExpenseItem> items = task.getExpenseItems();
      if (items.isEmpty())
      {
         return;
      }

      List<ExpenseItem> expenseItems = new ArrayList<>(items);
      expenseItems.sort((i1, i2) -> NumberHelper.compare(i1.getUniqueID(), i2.getUniqueID()));

      for (ExpenseItem item : expenseItems)
      {
         ActivityExpenseType expense = m_factory.createActivityExpenseType();
         m_expenses.add(expense);

         //
         // Item may be rejected on import if price per unit is not present
         //
         Double pricePerUnit = Optional.ofNullable(item.getPricePerUnit()).orElse(NumberHelper.DOUBLE_ZERO);

         expense.setAccrualType(ACCRUE_TYPE_MAP.get(item.getAccrueType()));
         //expense.setActivityId(value);
         //expense.setActivityName(value);
         expense.setActivityObjectId(task.getUniqueID());
         expense.setActualCost(item.getActualCost());
         expense.setActualUnits(item.getActualUnits());
         //expense.setAtCompletionCost(item.getAtCompletionCost());
         //expense.setAtCompletionUnits(item.getAtCompletionUnits());
         expense.setAutoComputeActuals(Boolean.valueOf(item.getAutoComputeActuals()));
         //expense.setCBSCode(value);
         //expense.setCBSId(value);

         if (item.getAccount() != null)
         {
            expense.setCostAccountObjectId(item.getAccount().getUniqueID());
         }

         //expense.setCreateDate(value);
         //expense.setCreateUser(value);
         expense.setDocumentNumber(item.getDocumentNumber());

         if (item.getCategory() != null)
         {
            expense.setExpenseCategoryObjectId(item.getCategory().getUniqueID());
         }

         expense.setExpenseDescription(item.getDescription());

         expense.setExpenseItem(item.getName());
         //expense.setExpensePercentComplete(value);
         //expense.setIsBaseline(value);
         //expense.setIsTemplate(value);
         //expense.setLastUpdateDate(value);
         //expense.setLastUpdateUser(value);
         expense.setObjectId(item.getUniqueID());
         expense.setPlannedCost(item.getPlannedCost());
         expense.setPlannedUnits(item.getPlannedUnits());
         expense.setPricePerUnit(pricePerUnit);
         //expense.setProjectId(PROJECT_ID);
         expense.setProjectObjectId(m_projectObjectID);
         expense.setRemainingCost(item.getRemainingCost());
         expense.setRemainingUnits(item.getRemainingUnits());
         expense.setUnitOfMeasure(item.getUnitOfMeasure());
         expense.setVendor(item.getVendor());
         expense.setWBSObjectId(task.getParentTaskUniqueID());
      }
   }

   /**
    * Write all activity steps for project.
    */
   private void writeActivitySteps()
   {
      List<Task> tasks = new ArrayList<>(m_projectFile.getTasks());
      tasks.sort((t1, t2) -> NumberHelper.compare(t1.getUniqueID(), t2.getUniqueID()));

      tasks.forEach(this::writeActivitySteps);
   }

   /**
    * Write activity steps for a task.
    *
    * @param task Task instance
    */
   private void writeActivitySteps(Task task)
   {
      List<Step> items = task.getSteps();
      if (items.isEmpty())
      {
         return;
      }

      List<Step> steps = new ArrayList<>(items);
      steps.sort((i1, i2) -> NumberHelper.compare(i1.getSequenceNumber(), i2.getSequenceNumber()));

      for (Step step : steps)
      {
         ActivityStepType activityStep = m_factory.createActivityStepType();
         m_activitySteps.add(activityStep);
         activityStep.setActivityObjectId(task.getUniqueID());
         activityStep.setDescription(getNotes(step.getDescriptionObject()));
         activityStep.setName(step.getName());
         activityStep.setObjectId(step.getUniqueID());
         activityStep.setPercentComplete(Double.valueOf(NumberHelper.getDouble(step.getPercentComplete()) / 100.0));
         activityStep.setIsCompleted(Boolean.valueOf(step.getComplete()));
         activityStep.setSequenceNumber(step.getSequenceNumber());
         activityStep.setWeight(step.getWeight());
      }
   }

   /**
    * Write rate information for each resource.
    */
   private void writeResourceRates()
   {
      m_projectFile.getResources().stream().filter(r -> !BooleanHelper.getBoolean(r.getRole()) && r.getUniqueID().intValue() != 0).forEach(this::writeResourceRates);
   }

   /**
    * Write rate information for a single resource.
    *
    * @param resource Resource instance
    */
   private void writeResourceRates(Resource resource)
   {
      CostRateTable table = resource.getCostRateTable(0);
      AvailabilityTable availabilityTable = resource.getAvailability();

      for (CostRateTableEntry entry : table)
      {
         if (costRateTableWriteRequired(entry))
         {
            Availability availability = availabilityTable.getEntryByDate(entry.getStartDate());
            Double maxUnits = availability == null ? Double.valueOf(1) : Double.valueOf(availability.getUnits().doubleValue() / 100.0);

            ResourceRateType rate = m_factory.createResourceRateType();
            m_apibo.getResourceRate().add(rate);

            //rate.setCreateDate(value);
            //rate.setCreateUser(value);
            rate.setEffectiveDate(entry.getStartDate());
            //rate.setLastUpdateDate(value);
            //rate.setLastUpdateUser(value);
            rate.setMaxUnitsPerTime(maxUnits);
            rate.setObjectId(m_sequences.getRateObjectID());
            rate.setPricePerUnit(writeRate(entry.getRate(0)));
            rate.setPricePerUnit2(writeRate(entry.getRate(1)));
            rate.setPricePerUnit3(writeRate(entry.getRate(2)));
            rate.setPricePerUnit4(writeRate(entry.getRate(3)));
            rate.setPricePerUnit5(writeRate(entry.getRate(4)));
            //rate.setResourceId(value);
            //rate.setResourceName(value);
            rate.setResourceObjectId(resource.getUniqueID());
            //rate.setShiftPeriodObjectId(value);
         }
      }
   }

   /**
    * Write a rate value, handling null.
    *
    * @param rate rate
    * @return rate in hours as a Double
    */
   private Double writeRate(Rate rate)
   {
      if (rate == null || rate.getAmount() == 0.0)
      {
         return null;
      }

      return Double.valueOf(RateHelper.convertToHours(m_projectFile, rate));
   }

   /**
    * Write rate information for each role.
    */
   private void writeRoleRates()
   {
      m_projectFile.getResources().stream().filter(r -> BooleanHelper.getBoolean(r.getRole())).forEach(this::writeRoleRates);
   }

   /**
    * Write rate information for a single role.
    *
    * @param resource Resource instance
    */
   private void writeRoleRates(Resource resource)
   {
      CostRateTable table = resource.getCostRateTable(0);
      AvailabilityTable availabilityTable = resource.getAvailability();

      for (CostRateTableEntry entry : table)
      {
         if (costRateTableWriteRequired(entry))
         {
            Availability availability = availabilityTable.getEntryByDate(entry.getStartDate());
            Double maxUnits = availability == null ? Double.valueOf(1) : Double.valueOf(availability.getUnits().doubleValue() / 100.0);

            RoleRateType rate = m_factory.createRoleRateType();
            m_apibo.getRoleRate().add(rate);

            //rate.setCreateDate(value);
            //rate.setCreateUser(value);
            rate.setEffectiveDate(entry.getStartDate());
            //rate.setLastUpdateDate(value);
            //rate.setLastUpdateUser(value);
            rate.setMaxUnitsPerTime(maxUnits);
            rate.setObjectId(m_sequences.getRateObjectID());
            rate.setPricePerUnit(writeRate(entry.getRate(0)));
            rate.setPricePerUnit2(writeRate(entry.getRate(1)));
            rate.setPricePerUnit3(writeRate(entry.getRate(2)));
            rate.setPricePerUnit4(writeRate(entry.getRate(3)));
            rate.setPricePerUnit5(writeRate(entry.getRate(4)));
            //rate.setResourceId(value);
            //rate.setResourceName(value);
            rate.setRoleObjectId(resource.getUniqueID());
            //rate.setShiftPeriodObjectId(value);
         }
      }
   }

   /**
    * This method determines whether the cost rate table should be written.
    * A default cost rate table should not be written to the file.
    *
    * @param entry cost rate table entry
    * @return boolean flag
    */
   private boolean costRateTableWriteRequired(CostRateTableEntry entry)
   {
      boolean fromDate = (DateHelper.compare(entry.getStartDate(), DateHelper.START_DATE_NA) > 0);
      boolean toDate = (DateHelper.compare(entry.getEndDate(), DateHelper.END_DATE_NA) > 0);
      boolean nonZeroRates = false;
      for (int rateIndex = 0; rateIndex < CostRateTableEntry.MAX_RATES; rateIndex++)
      {
         if (entry.getRate(rateIndex) != null && entry.getRate(rateIndex).getAmount() != 0)
         {
            nonZeroRates = true;
            break;
         }
      }

      return (fromDate || toDate || nonZeroRates);
   }

   /**
    * Write any notebook topics used by this schedule.
    */
   private void writeTopics()
   {
      int sequenceNumber = 1;
      for (Map.Entry<Integer, String> entry : m_topics.entrySet())
      {
         NotebookTopicType topic = m_factory.createNotebookTopicType();
         m_apibo.getNotebookTopic().add(topic);

         topic.setAvailableForActivity(Boolean.TRUE);
         topic.setAvailableForWBS(Boolean.TRUE);
         topic.setName(entry.getValue());
         topic.setObjectId(entry.getKey());
         topic.setSequenceNumber(Integer.valueOf(sequenceNumber++));
      }
   }

   /**
    * Write notes for a WBS entry.
    *
    * @param task WBS entry.
    */
   private void writeWbsNote(Task task)
   {
      String notes = task.getNotes();
      if (notes.isEmpty())
      {
         return;
      }

      if (notesAreNativeFormat(task.getNotesObject()))
      {
         writeNativeWbsNote(task);
      }
      else
      {
         writeDefaultWbsNote(task);
      }
   }

   /**
    * Generate a notebook entry from plain text.
    *
    * @param task WBS entry
    */
   private void writeDefaultWbsNote(Task task)
   {
      ProjectNoteType xml = m_factory.createProjectNoteType();
      m_projectNotes.add(xml);

      m_topics.put(NOTEBOOK_TOPIC_OBJECT_ID, "Notes");
      xml.setNote(HtmlHelper.getHtmlFromPlainText(task.getNotes()));
      xml.setNotebookTopicObjectId(NOTEBOOK_TOPIC_OBJECT_ID);
      xml.setObjectId(m_sequences.getWbsNoteObjectID());
      xml.setProjectObjectId(m_projectObjectID);
      xml.setWBSObjectId(task.getUniqueID());
   }

   /**
    * Generate notebook entries from structured notes.
    *
    * @param task WBS entry
    */
   private void writeNativeWbsNote(Task task)
   {
      for (Notes note : ((ParentNotes) task.getNotesObject()).getChildNotes())
      {
         StructuredNotes structuredNotes = (StructuredNotes) note;
         HtmlNotes htmlNotes = (HtmlNotes) structuredNotes.getNotes();

         ProjectNoteType xml = m_factory.createProjectNoteType();
         m_projectNotes.add(xml);

         m_topics.put(structuredNotes.getTopicID(), structuredNotes.getTopicName());
         xml.setNote(htmlNotes.getHtml());
         xml.setNotebookTopicObjectId(structuredNotes.getTopicID());
         xml.setObjectId(m_sequences.getWbsNoteObjectID());
         xml.setProjectObjectId(m_projectObjectID);
         xml.setWBSObjectId(task.getUniqueID());
      }
   }

   /**
    * Write notes for an Activity entry.
    *
    * @param task activity entry.
    */
   private void writeActivityNote(Task task)
   {
      String notes = task.getNotes();
      if (notes.isEmpty())
      {
         return;
      }

      if (notesAreNativeFormat(task.getNotesObject()))
      {
         writeNativeActivityNote(task);
      }
      else
      {
         writeDefaultActivityNote(task);
      }
   }

   /**
    * Generate a notebook entry from plain text.
    *
    * @param task activity entry
    */
   private void writeDefaultActivityNote(Task task)
   {
      ActivityNoteType xml = m_factory.createActivityNoteType();
      m_activityNotes.add(xml);

      m_topics.put(NOTEBOOK_TOPIC_OBJECT_ID, "Notes");
      xml.setNote(HtmlHelper.getHtmlFromPlainText(task.getNotes()));
      xml.setNotebookTopicObjectId(NOTEBOOK_TOPIC_OBJECT_ID);
      xml.setObjectId(m_sequences.getActivityNoteObjectID());
      xml.setProjectObjectId(m_projectObjectID);
      xml.setActivityObjectId(task.getUniqueID());
   }

   /**
    * Generate notebook entries from structured notes.
    *
    * @param task activity entry
    */
   private void writeNativeActivityNote(Task task)
   {
      for (Notes note : ((ParentNotes) task.getNotesObject()).getChildNotes())
      {
         StructuredNotes structuredNotes = (StructuredNotes) note;
         HtmlNotes htmlNotes = (HtmlNotes) structuredNotes.getNotes();

         ActivityNoteType xml = m_factory.createActivityNoteType();
         m_activityNotes.add(xml);

         m_topics.put(structuredNotes.getTopicID(), structuredNotes.getTopicName());
         xml.setNote(htmlNotes.getHtml());
         xml.setNotebookTopicObjectId(structuredNotes.getTopicID());
         xml.setObjectId(m_sequences.getActivityNoteObjectID());
         xml.setProjectObjectId(m_projectObjectID);
         xml.setActivityObjectId(task.getUniqueID());
      }
   }

   /**
    * Returns true if the notes are in a form that can be exported
    * as P6 notepad entries.
    *
    * @param notes Notes instance
    * @return true if the notes can be exported as notepad entries
    */
   private boolean notesAreNativeFormat(Notes notes)
   {
      return notes instanceof ParentNotes && ((ParentNotes) notes).getChildNotes().stream().allMatch(n -> n instanceof StructuredNotes && ((StructuredNotes) n).getNotes() instanceof HtmlNotes);
   }

   /**
    * Writes a list of UDF types.
    *
    * @author lsong
    * @param type parent entity type
    * @param mpxj parent entity
    * @return list of UDFAssignmentType instances
    */
   private List<UDFAssignmentType> writeUserDefinedFieldAssignments(FieldTypeClass type, FieldContainer mpxj)
   {
      List<UDFAssignmentType> out = new ArrayList<>();
      CustomFieldContainer customFields = m_projectFile.getCustomFields();

      for (FieldType fieldType : m_userDefinedFields)
      {
         if (type == fieldType.getFieldTypeClass())
         {
            Object value = mpxj.getCachedValue(fieldType);
            if (FieldTypeHelper.valueIsNotDefault(fieldType, value))
            {
               CustomField field = customFields.get(fieldType);
               int uniqueID = field == null ? FieldTypeHelper.getFieldID(fieldType) : NumberHelper.getInt(field.getUniqueID());

               DataType dataType = fieldType.getDataType();
               if (dataType == DataType.CUSTOM)
               {
                  dataType = DataType.BINARY;
               }

               UDFAssignmentType udf = m_factory.createUDFAssignmentType();
               udf.setTypeObjectId(uniqueID);
               setUserFieldValue(udf, dataType, value);
               out.add(udf);
            }
         }
      }

      out.sort(Comparator.comparing(UDFAssignmentType::getTypeObjectId));

      return out;
   }

   /**
    * Write activity code definitions.
    */
   private void writeActivityCodes()
   {
      m_projectFile.getActivityCodes().stream().sorted(Comparator.comparing(ActivityCode::getSequenceNumber)).forEach(this::writeActivityCode);
   }

   /**
    * Write an activity code definition.
    *
    * @param code activity code
    */
   private void writeActivityCode(ActivityCode code)
   {
      ActivityCodeTypeType xml = m_factory.createActivityCodeTypeType();
      m_apibo.getActivityCodeType().add(xml);
      xml.setObjectId(code.getUniqueID());
      xml.setScope(ACTIVITY_CODE_SCOPE_MAP.get(code.getScope()));
      xml.setSequenceNumber(code.getSequenceNumber());
      xml.setName(code.getName());
      xml.setIsSecureCode(Boolean.valueOf(code.getSecure()));
      xml.setLength(code.getMaxLength());

      if (code.getScope() != ActivityCodeScope.GLOBAL)
      {
         xml.setProjectObjectId(m_projectObjectID);
      }

      code.getChildValues().stream().sorted(Comparator.comparing(ActivityCodeValue::getSequenceNumber)).forEach(v -> writeActivityCodeValue(xml, null, v));
   }

   /**
    * Write an activity code value.
    *
    * @param code parent activity code
    * @param parentValue parent value
    * @param value value to write
    */
   private void writeActivityCodeValue(ActivityCodeTypeType code, ActivityCodeType parentValue, ActivityCodeValue value)
   {
      ActivityCodeType xml = m_factory.createActivityCodeType();
      m_apibo.getActivityCode().add(xml);
      xml.setObjectId(value.getUniqueID());
      xml.setProjectObjectId(code.getProjectObjectId());
      xml.setCodeTypeObjectId(code.getObjectId());
      xml.setParentObjectId(parentValue == null ? null : parentValue.getObjectId());
      xml.setSequenceNumber(value.getSequenceNumber());
      xml.setCodeValue(value.getName());
      xml.setDescription(value.getDescription());
      xml.setColor(getColor(value.getColor()));

      value.getChildValues().stream().sorted(Comparator.comparing(ActivityCodeValue::getSequenceNumber)).forEach(v -> writeActivityCodeValue(code, xml, v));
   }

   /**
    * Write activity code assignments for this task.
    *
    * @param task MPXJ task
    * @param xml PMXML activity
    */
   private void writeActivityCodeAssignments(Task task, ActivityType xml)
   {
      task.getActivityCodes().forEach(v -> writeActivityCodeAssignment(xml, v));
   }

   /**
    * Write an individual activity code assignment.
    *
    * @param xml PMXML activity
    * @param value activity code value
    */
   private void writeActivityCodeAssignment(ActivityType xml, ActivityCodeValue value)
   {
      CodeAssignmentType assignment = m_factory.createCodeAssignmentType();
      xml.getCode().add(assignment);
      assignment.setTypeObjectId(NumberHelper.getInt(value.getType().getUniqueID()));
      assignment.setValueObjectId(NumberHelper.getInt(value.getUniqueID()));
   }

   /**
    * Convert a color to a string representation.
    *
    * @param color color to convert
    * @return string representation
    */
   private String getColor(Color color)
   {
      if (color == null)
      {
         return null;
      }

      String result = "000000" + Integer.toHexString(color.getRGB()).toUpperCase();
      return "#" + result.substring(result.length() - 6);
   }

   /**
    * Sets the value of a UDF.
    *
    * @param udf user defined field
    * @param dataType MPXJ data type
    * @param value field value
    */
   private void setUserFieldValue(UDFAssignmentType udf, DataType dataType, Object value)
   {
      switch (dataType)
      {
         case DURATION:
         {
            udf.setTextValue(((Duration) value).toString());
            break;
         }

         case CURRENCY:
         {
            if (!(value instanceof Double))
            {
               value = Double.valueOf(((Number) value).doubleValue());
            }
            udf.setCostValue((Double) value);
            break;
         }

         case BINARY:
         {
            udf.setTextValue("");
            break;
         }

         case STRING:
         {
            udf.setTextValue(value == null ? null : value.toString());
            break;
         }

         case DATE:
         {
            udf.setStartDateValue((Date) value);
            break;
         }

         case NUMERIC:
         {
            if (!(value instanceof Double))
            {
               value = Double.valueOf(((Number) value).doubleValue());
            }
            udf.setDoubleValue((Double) value);
            break;
         }

         case BOOLEAN:
         {
            udf.setIntegerValue(BooleanHelper.getBoolean((Boolean) value) ? Integer.valueOf(1) : Integer.valueOf(0));
            break;
         }

         case INTEGER:
         case SHORT:
         {
            udf.setIntegerValue(NumberHelper.getInteger((Number) value));
            break;
         }

         default:
         {
            throw new RuntimeException("Unconvertible data type: " + dataType);
         }
      }
   }

   /**
    * Retrieve a duration in the form required by Primavera.
    *
    * @param duration Duration instance
    * @return formatted duration
    */
   private Double getDuration(Duration duration)
   {
      Double result;
      if (duration == null)
      {
         result = null;
      }
      else
      {
         if (duration.getUnits() != TimeUnit.HOURS)
         {
            duration = duration.convertUnits(TimeUnit.HOURS, m_projectFile.getProjectProperties());
         }

         result = Double.valueOf(duration.getDuration());
      }
      return result;
   }

   /**
    * Formats a day name.
    *
    * @param day MPXJ Day instance
    * @return Primavera day instance
    */
   private String getDayName(Day day)
   {
      return DAY_NAMES[day.getValue() - 1];
   }

   /**
    * Formats a percentage value.
    *
    * @param number MPXJ percentage value
    * @return Primavera percentage value
    */
   private Double getPercentage(Number number)
   {
      Double result = null;

      if (number != null)
      {
         result = Double.valueOf(number.doubleValue() / 100);
      }

      return result;
   }

   /**
    * Returns the reported percent complete value for this task.
    *
    * @param task task
    * @return percent complete value
    */
   private Double getPercentComplete(Task task)
   {
      Number result;
      PercentCompleteType type = task.getPercentCompleteType();

      if (type == null)
      {
         type = PercentCompleteType.DURATION;
      }

      switch (type)
      {
         case PHYSICAL:
         {
            result = task.getPhysicalPercentComplete();
            break;
         }

         case UNITS:
         {
            result = task.getPercentageWorkComplete();
            break;
         }

         case DURATION:
         case SCOPE:
         default:
         {
            result = task.getPercentageComplete();
            break;
         }
      }

      return getPercentage(result);
   }

   /**
    * Formats a double value.
    *
    * @param number numeric value
    * @return Double instance
    */
   private Double getDouble(Number number)
   {
      Double result = null;

      if (number != null)
      {
         result = Double.valueOf(number.doubleValue());
      }

      return result;
   }

   /**
    * The end of a Primavera time range finishes on the last minute
    * of the period, so a range of 12:00 -> 13:00 is represented by
    * Primavera as 12:00 -> 12:59.
    *
    * @param date MPXJ end time
    * @return Primavera end time
    */
   private Date getEndTime(Date date)
   {
      return new Date(date.getTime() - 60000);
   }

   /**
    * Generate a default Resource ID for a resource.
    *
    * @param resource Resource instance
    * @return generated Resource ID
    */
   private String getResourceID(Resource resource)
   {
      String result = resource.getResourceID();
      if (result == null)
      {
         result = RESOURCE_ID_PREFIX + resource.getUniqueID();
      }
      return result;
   }

   private Set<FieldType> getUserDefinedFieldsSet()
   {
      // All custom fields with configuration
      Set<FieldType> set = m_projectFile.getCustomFields().stream().map(CustomField::getFieldType).filter(Objects::nonNull).collect(Collectors.toSet());

      // All user defined fields
      set.addAll(m_projectFile.getUserDefinedFields());

      // All custom fields with values
      set.addAll(m_projectFile.getPopulatedFields().stream().filter(FieldLists.CUSTOM_FIELDS::contains).collect(Collectors.toSet()));

      // Remove unknown fields
      set.removeIf(f -> FieldTypeHelper.getFieldID(f) == -1);

      return set;
   }

   /**
    * Package-private accessor method used to retrieve the project file
    * currently being processed by this writer.
    *
    * @return project file instance
    */
   ProjectFile getProjectFile()
   {
      return m_projectFile;
   }

   private static final Integer NOTEBOOK_TOPIC_OBJECT_ID = Integer.valueOf(1);
   private static final String DEFAULT_PROJECT_ID = "PROJECT";
   private static final String RESOURCE_ID_PREFIX = "RESOURCE-";
   private static final String DEFAULT_WBS_CODE = "WBS";
   private static final Integer DEFAULT_CURRENCY_ID = Integer.valueOf(1);

   private static final String[] DAY_NAMES =
   {
      "Sunday",
      "Monday",
      "Tuesday",
      "Wednesday",
      "Thursday",
      "Friday",
      "Saturday"
   };

   private static final Map<AccrueType, String> ACCRUE_TYPE_MAP = new HashMap<>();
   static
   {
      ACCRUE_TYPE_MAP.put(AccrueType.PRORATED, "Uniform Over Activity");
      ACCRUE_TYPE_MAP.put(AccrueType.END, "End of Activity");
      ACCRUE_TYPE_MAP.put(AccrueType.START, "Start of Activity");
   }

   private static final Map<RateSource, String> RATE_SOURCE_MAP = new HashMap<>();
   static
   {
      RATE_SOURCE_MAP.put(RateSource.RESOURCE, "Resource");
      RATE_SOURCE_MAP.put(RateSource.OVERRIDE, "Override");
      RATE_SOURCE_MAP.put(RateSource.ROLE, "Role");
   }

   private static final Map<FieldTypeClass, String> SUBJECT_AREA_MAP = new HashMap<>();
   static
   {
      SUBJECT_AREA_MAP.put(FieldTypeClass.TASK, "Activity");
      SUBJECT_AREA_MAP.put(FieldTypeClass.RESOURCE, "Resource");
      SUBJECT_AREA_MAP.put(FieldTypeClass.PROJECT, "Project");
      SUBJECT_AREA_MAP.put(FieldTypeClass.ASSIGNMENT, "Resource Assignment");
      SUBJECT_AREA_MAP.put(FieldTypeClass.CONSTRAINT, "Constraint");
   }

   private static final Map<ActivityCodeScope, String> ACTIVITY_CODE_SCOPE_MAP = new HashMap<>();
   static
   {
      ACTIVITY_CODE_SCOPE_MAP.put(ActivityCodeScope.GLOBAL, "Global");
      ACTIVITY_CODE_SCOPE_MAP.put(ActivityCodeScope.EPS, "EPS");
      ACTIVITY_CODE_SCOPE_MAP.put(ActivityCodeScope.PROJECT, "Project");
   }

   private final ProjectFile m_projectFile;
   private final APIBusinessObjects m_apibo;
   private final Integer m_projectObjectID;
   private final PrimaveraPMObjectSequences m_sequences;

   private ObjectFactory m_factory;
   private List<WBSType> m_wbs;
   private List<ActivityType> m_activities;
   private List<ActivityStepType> m_activitySteps;
   private List<ResourceAssignmentType> m_assignments;
   private List<RelationshipType> m_relationships;
   private List<ActivityExpenseType> m_expenses;
   private List<ProjectNoteType> m_projectNotes;
   private List<ActivityNoteType> m_activityNotes;
   private List<UDFAssignmentType> m_udf;

   private ObjectSequence m_wbsSequence;
   private Set<FieldType> m_userDefinedFields;
   private Map<Integer, String> m_topics;
   private boolean m_activityTypePopulated;
}
