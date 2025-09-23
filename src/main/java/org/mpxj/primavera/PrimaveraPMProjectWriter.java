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

package org.mpxj.primavera;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.mpxj.ActivityCode;
import org.mpxj.ActivityCodeScope;
import org.mpxj.ActivityCodeValue;
import org.mpxj.Availability;
import org.mpxj.AvailabilityTable;
import org.mpxj.Code;
import org.mpxj.CodeValue;
import org.mpxj.CostAccount;
import org.mpxj.CostRateTable;
import org.mpxj.CostRateTableEntry;
import org.mpxj.Currency;
import org.mpxj.CurrencySymbolPosition;
import org.mpxj.CustomField;
import org.mpxj.CustomFieldContainer;
import org.mpxj.DataType;
import java.time.DayOfWeek;

import org.mpxj.ProjectCode;
import org.mpxj.ProjectCodeValue;
import org.mpxj.ResourceAssignmentCode;
import org.mpxj.ResourceAssignmentCodeValue;
import org.mpxj.ResourceCode;
import org.mpxj.ResourceCodeValue;
import org.mpxj.RoleCode;
import org.mpxj.RoleCodeValue;
import org.mpxj.Shift;
import org.mpxj.ShiftPeriod;
import org.mpxj.SkillLevel;
import org.mpxj.UnitOfMeasure;
import org.mpxj.common.DayOfWeekHelper;
import org.mpxj.Duration;
import org.mpxj.ExpenseCategory;
import org.mpxj.ExpenseItem;
import org.mpxj.FieldContainer;
import org.mpxj.FieldType;
import org.mpxj.FieldTypeClass;
import org.mpxj.HtmlNotes;
import org.mpxj.Location;
import org.mpxj.Notes;
import org.mpxj.NotesTopic;
import org.mpxj.ParentNotes;
import org.mpxj.PercentCompleteType;
import org.mpxj.ProjectCalendar;
import org.mpxj.ProjectCalendarException;
import org.mpxj.ProjectFile;
import org.mpxj.ProjectProperties;
import org.mpxj.Rate;
import org.mpxj.Relation;
import org.mpxj.Resource;
import org.mpxj.ResourceAssignment;
import org.mpxj.Step;
import org.mpxj.StructuredNotes;
import org.mpxj.Task;
import org.mpxj.TaskField;
import org.mpxj.TaskType;
import org.mpxj.LocalTimeRange;
import org.mpxj.TimeUnit;
import org.mpxj.UserDefinedField;
import org.mpxj.WorkContour;
import org.mpxj.common.BooleanHelper;
import org.mpxj.common.ColorHelper;
import org.mpxj.common.FieldTypeHelper;
import org.mpxj.common.HtmlHelper;
import org.mpxj.common.LocalDateTimeHelper;
import org.mpxj.common.NumberHelper;
import org.mpxj.common.ObjectSequence;
import org.mpxj.common.RateHelper;
import org.mpxj.primavera.schema.APIBusinessObjects;
import org.mpxj.primavera.schema.ActivityCodeType;
import org.mpxj.primavera.schema.ActivityCodeTypeType;
import org.mpxj.primavera.schema.ActivityExpenseType;
import org.mpxj.primavera.schema.ActivityNoteType;
import org.mpxj.primavera.schema.ActivityStepType;
import org.mpxj.primavera.schema.ActivityType;
import org.mpxj.primavera.schema.BaselineProjectType;
import org.mpxj.primavera.schema.CalendarType;
import org.mpxj.primavera.schema.CalendarType.HolidayOrExceptions;
import org.mpxj.primavera.schema.CalendarType.HolidayOrExceptions.HolidayOrException;
import org.mpxj.primavera.schema.CalendarType.StandardWorkWeek;
import org.mpxj.primavera.schema.CalendarType.StandardWorkWeek.StandardWorkHours;
import org.mpxj.primavera.schema.CodeAssignmentType;
import org.mpxj.primavera.schema.CostAccountType;
import org.mpxj.primavera.schema.CurrencyType;
import org.mpxj.primavera.schema.ExpenseCategoryType;
import org.mpxj.primavera.schema.LocationType;
import org.mpxj.primavera.schema.NotebookTopicType;
import org.mpxj.primavera.schema.ObjectFactory;
import org.mpxj.primavera.schema.ProjectCodeType;
import org.mpxj.primavera.schema.ProjectCodeTypeType;
import org.mpxj.primavera.schema.ProjectNoteType;
import org.mpxj.primavera.schema.ProjectType;
import org.mpxj.primavera.schema.RelationshipType;
import org.mpxj.primavera.schema.ResourceAssignmentCodeType;
import org.mpxj.primavera.schema.ResourceAssignmentCodeTypeType;
import org.mpxj.primavera.schema.ResourceAssignmentType;
import org.mpxj.primavera.schema.ResourceCodeType;
import org.mpxj.primavera.schema.ResourceCodeTypeType;
import org.mpxj.primavera.schema.ResourceCurveType;
import org.mpxj.primavera.schema.ResourceCurveValuesType;
import org.mpxj.primavera.schema.ResourceRateType;
import org.mpxj.primavera.schema.ResourceRoleType;
import org.mpxj.primavera.schema.ResourceType;
import org.mpxj.primavera.schema.RoleCodeType;
import org.mpxj.primavera.schema.RoleCodeTypeType;
import org.mpxj.primavera.schema.RoleRateType;
import org.mpxj.primavera.schema.RoleType;
import org.mpxj.primavera.schema.ScheduleOptionsType;
import org.mpxj.primavera.schema.ShiftPeriodType;
import org.mpxj.primavera.schema.ShiftType;
import org.mpxj.primavera.schema.UDFAssignmentType;
import org.mpxj.primavera.schema.UDFTypeType;
import org.mpxj.primavera.schema.UnitOfMeasureType;
import org.mpxj.primavera.schema.WBSType;
import org.mpxj.primavera.schema.WorkTimeType;

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
         m_activityTypePopulated = m_projectFile.getTasks().getPopulatedFields().contains(TaskField.ACTIVITY_TYPE);
         m_wbsSequence = new ObjectSequence(0);
         m_userDefinedFields = UdfHelper.getUserDefinedFieldsSet(m_projectFile);
         m_projectFromPrimavera = "Primavera".equals(m_projectFile.getProjectProperties().getFileApplication());

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
            writeCodeAssignments(m_projectFile.getProjectProperties().getProjectCodeValues(), project.getCode());
            writeActivityCodeDefinitions(project.getActivityCodeType(), project.getActivityCode());
            writeCalendars(project.getCalendar());
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

            writeCurrencies();
            writeLocations();
            writeShifts();
            writeProjectProperties(project);
            writeUnitsOfMeasure();
            writeProjectCodeDefinitions();
            writeResourceCodeDefinitions();
            writeRoleCodeDefinitions();
            writeResourceAssignmentCodeDefinitions();
            writeCodeAssignments(m_projectFile.getProjectProperties().getProjectCodeValues(), project.getCode());
            writeActivityCodeDefinitions(project.getActivityCodeType(), project.getActivityCode());
            writeCalendars(project.getCalendar());
            writeUDF();
            writeActivityCodeDefinitions();
            writeUserDefinedFieldDefinitions();
            writeExpenseCategories();
            writeCostAccounts();
            writeResourceCurves();
            writeCalendars();
            writeResources();
            writeRoles();
            writeRoleAssignments();
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
      }
   }

   private void writeUDF()
   {
      m_udf.addAll(writeUserDefinedFieldAssignments(FieldTypeClass.PROJECT, false, m_projectFile.getProjectProperties()));
   }

   /**
    * Write currencies.
    */
   private void writeCurrencies()
   {
      if (m_projectFile.getCurrencies().isEmpty())
      {
         // No currencies defined? Write a default currency based on the project properties.
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
         return;
      }

      for (Currency currency : m_projectFile.getCurrencies())
      {
         CurrencyType xml = m_factory.createCurrencyType();
         xml.setObjectId(currency.getUniqueID());
         xml.setId(currency.getCurrencyID());
         xml.setName(currency.getName());
         xml.setSymbol(currency.getSymbol());
         xml.setExchangeRate(currency.getExchangeRate());
         xml.setDecimalSymbol(getSymbolName(currency.getDecimalSymbol()));
         xml.setDecimalPlaces(currency.getNumberOfDecimalPlaces());
         xml.setDigitGroupingSymbol(getSymbolName(currency.getDigitGroupingSymbol()));
         xml.setPositiveSymbol(currency.getPositiveCurrencyFormat());
         xml.setNegativeSymbol(currency.getNegativeCurrencyFormat());
         m_apibo.getCurrency().add(xml);
      }
   }

   /**
    * Map the currency separator character to a symbol name.
    *
    * @param s currency separator string
    * @return symbol name
    */
   private String getSymbolName(String s)
   {
      return s == null || s.isEmpty() ? null : getSymbolName(s.charAt(0));
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

         case BEFORE:
         default:
         {
            result = "#1.1";
            break;
         }
      }

      return result;
   }

   /**
    * Add UDFType objects to a PMXML file.
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
         udf.setDataType(UdfHelper.getXmlFromDataType(dataType));
         udf.setSubjectArea(FieldTypeClassHelper.getXmlFromInstance(type));
         udf.setTitle(title);
         fields.add(udf);
      }

      fields.sort(Comparator.comparing(UDFTypeType::getObjectId));
   }

   /**
    * Write locations.
    */
   private void writeLocations()
   {
      List<LocationType> locations = m_apibo.getLocation();
      for (Location location : m_projectFile.getLocations())
      {
         LocationType lt = m_factory.createLocationType();
         lt.setObjectId(location.getUniqueID());
         lt.setName(location.getName());
         lt.setAddressLine1(location.getAddressLine1());
         lt.setAddressLine2(location.getAddressLine2());
         lt.setCity(location.getCity());
         lt.setCountry(location.getCountry());
         lt.setCountryCode(location.getCountryCode());
         lt.setMunicipality(location.getMunicipality());
         lt.setPostalCode(location.getPostalCode());
         lt.setState(location.getState());
         lt.setStateCode(location.getStateCode());
         lt.setLatitude(location.getLatitude());
         lt.setLongitude(location.getLongitude());
         locations.add(lt);
      }
   }

   /**
    * Write shifts.
    */
   private void writeShifts()
   {
      List<ShiftType> shifts = m_apibo.getShift();
      for (Shift shift : m_projectFile.getShifts())
      {
         ShiftType st = m_factory.createShiftType();
         st.setObjectId(shift.getUniqueID());
         st.setName(shift.getName());

         for (ShiftPeriod period : shift.getPeriods())
         {
            ShiftPeriodType spt = m_factory.createShiftPeriodType();
            spt.setObjectId(period.getUniqueID());
            spt.setStartHour(Integer.valueOf(period.getStart().getHour()));
            st.getShiftPeriod().add(spt);
         }

         shifts.add(st);
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
      m_projectFile.getCostAccounts().stream().sorted(Comparator.comparing(CostAccount::getUniqueID)).forEach(c -> writeCostAccount(costAccounts, c));
   }

   /**
    * Write a cost account.
    *
    * @param costAccounts cost accounts list
    * @param account cost account
    */
   private void writeCostAccount(List<CostAccountType> costAccounts, CostAccount account)
   {
      CostAccountType cat = m_factory.createCostAccountType();
      cat.setObjectId(account.getUniqueID());
      cat.setId(account.getID());
      cat.setName(account.getName());
      cat.setDescription(getNotes(account.getNotesObject()));
      cat.setSequenceNumber(account.getSequenceNumber());
      cat.setParentObjectId(account.getParentUniqueID());
      costAccounts.add(cat);
   }

   /**
    * Write units of measure.
    */
   private void writeUnitsOfMeasure()
   {
      List<UnitOfMeasureType> units = m_apibo.getUnitOfMeasure();
      for (UnitOfMeasure uom : m_projectFile.getUnitsOfMeasure())
      {
         UnitOfMeasureType unit = m_factory.createUnitOfMeasureType();
         unit.setObjectId(uom.getUniqueID());
         unit.setAbbreviation(uom.getAbbreviation());
         unit.setName(uom.getName());
         unit.setSequenceNumber(uom.getSequenceNumber());
         units.add(unit);
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

      project.setActivityDefaultActivityType(ActivityTypeHelper.getXmlFromInstance(ActivityTypeHelper.NEW_ACTIVITY_DEFAULT_TYPE));
      project.setActivityDefaultCalendarObjectId(mpxj.getDefaultCalendarUniqueID());
      project.setActivityDefaultDurationType(TaskTypeHelper.getXmlFromInstance(TaskType.FIXED_DURATION_AND_UNITS));
      project.setActivityDefaultPercentCompleteType(PercentCompleteTypeHelper.getXmlFromInstance(PercentCompleteType.DURATION));
      project.setActivityDefaultPricePerUnit(NumberHelper.DOUBLE_ZERO);
      project.setActivityIdBasedOnSelectedActivity(Boolean.valueOf(mpxj.getActivityIdIncrementBasedOnSelectedActivity()));
      project.setActivityIdIncrement(mpxj.getActivityIdIncrement());
      project.setActivityIdPrefix(mpxj.getActivityIdPrefix());
      project.setActivityIdSuffix(mpxj.getActivityIdSuffix());
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
      project.setDateAdded(mpxj.getCreationDate());
      project.setDataDate(m_projectFile.getProjectProperties().getStatusDate());
      project.setDefaultPriceTimeUnits("Hour");
      project.setDiscountApplicationPeriod("Month");
      project.setEarnedValueComputeType("Activity Percent Complete");
      project.setEarnedValueETCComputeType("ETC = Remaining Cost for Activity");
      project.setEarnedValueETCUserValue(Double.valueOf(0.88));
      project.setEarnedValueUserPercent(Double.valueOf(0.06));
      project.setEnablePublication(Boolean.valueOf(mpxj.getEnablePublication()));
      project.setEnableSummarization(Boolean.valueOf(mpxj.getEnableSummarization()));
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
      project.setPlannedStartDate(WriterHelper.getProjectPlannedStart(mpxj));
      project.setPrimaryResourcesCanMarkActivitiesAsCompleted(Boolean.TRUE);
      project.setRelationshipLagCalendar(RelationshipLagCalendarHelper.getXmlFromInstance(mpxj.getRelationshipLagCalendar()));
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
      project.setWBSCodeSeparator(mpxj.getWbsCodeSeparator());
      project.setLocationObjectId(mpxj.getLocationUniqueID());
      project.setWebSiteURL(mpxj.getProjectWebsiteUrl());

      writeScheduleOptions(project.getScheduleOptions());

      writeWbsNote(null, mpxj.getNotesObject());
   }

   private void writeProjectProperties(BaselineProjectType project)
   {
      ProjectProperties mpxj = m_projectFile.getProjectProperties();
      String projectID = getProjectID(mpxj);

      project.setActivityDefaultActivityType(ActivityTypeHelper.getXmlFromInstance(ActivityTypeHelper.NEW_ACTIVITY_DEFAULT_TYPE));
      project.setActivityDefaultCalendarObjectId(mpxj.getDefaultCalendarUniqueID());
      project.setActivityDefaultDurationType(TaskTypeHelper.getXmlFromInstance(TaskType.FIXED_DURATION_AND_UNITS));
      project.setActivityDefaultPercentCompleteType(PercentCompleteTypeHelper.getXmlFromInstance(PercentCompleteType.DURATION));
      project.setActivityDefaultPricePerUnit(NumberHelper.DOUBLE_ZERO);
      project.setActivityIdBasedOnSelectedActivity(Boolean.valueOf(mpxj.getActivityIdIncrementBasedOnSelectedActivity()));
      project.setActivityIdIncrement(mpxj.getActivityIdIncrement());
      project.setActivityIdPrefix(mpxj.getActivityIdPrefix());
      project.setActivityIdSuffix(mpxj.getActivityIdSuffix());
      project.setActivityPercentCompleteBasedOnActivitySteps(Boolean.FALSE);
      project.setAddActualToRemaining(Boolean.FALSE);
      project.setAssignmentDefaultDrivingFlag(Boolean.TRUE);
      project.setAssignmentDefaultRateType(RateTypeHelper.getXmlFromInstance(Integer.valueOf(0)));
      project.setCheckOutStatus(Boolean.FALSE);
      project.setCostQuantityRecalculateFlag(Boolean.FALSE);
      project.setCreateDate(mpxj.getCreationDate());
      project.setCriticalActivityFloatLimit(Double.valueOf(mpxj.getCriticalSlackLimit().convertUnits(TimeUnit.HOURS, mpxj).getDuration()));
      project.setCriticalActivityPathType(CriticalActivityTypeHelper.getXmlFromInstance(mpxj.getCriticalActivityType()));
      project.setDateAdded(mpxj.getCreationDate());
      project.setDataDate(m_projectFile.getProjectProperties().getStatusDate());
      project.setDefaultPriceTimeUnits("Hour");
      project.setDiscountApplicationPeriod("Month");
      project.setEnablePublication(Boolean.valueOf(mpxj.getEnablePublication()));
      project.setEnableSummarization(Boolean.valueOf(mpxj.getEnableSummarization()));
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
      project.setPlannedStartDate(WriterHelper.getProjectPlannedStart(mpxj));
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
      project.setWBSCodeSeparator(mpxj.getWbsCodeSeparator());
      project.setLocationObjectId(mpxj.getLocationUniqueID());
      project.setBaselineTypeName(mpxj.getBaselineTypeName());
      project.setBaselineTypeObjectId(mpxj.getBaselineProjectUniqueID());
      project.setLastBaselineUpdateDate(mpxj.getLastBaselineUpdateDate());
      project.setWebSiteURL(mpxj.getProjectWebsiteUrl());

      writeScheduleOptions(project.getScheduleOptions());

      writeWbsNote(null, mpxj.getNotesObject());
   }

   private void writeScheduleOptions(List<ScheduleOptionsType> list)
   {
      ScheduleOptionsType options = m_factory.createScheduleOptionsType();
      options.setProjectObjectId(m_projectObjectID);
      list.add(options);
      ProjectProperties projectProperties = m_projectFile.getProjectProperties();

      //
      // Leveling Options
      //

      // Automatically level resources when scheduling
      options.setIncludeExternalResAss(Boolean.valueOf(projectProperties.getConsiderAssignmentsInOtherProjects()));
      options.setExternalProjectPriorityLimit(projectProperties.getConsiderAssignmentsInOtherProjectsWithPriorityEqualHigherThan());
      options.setPreserveScheduledEarlyAndLateDates(Boolean.valueOf(projectProperties.getPreserveScheduledEarlyAndLateDates()));

      // Recalculate assignment costs after leveling
      options.setLevelAllResources(Boolean.valueOf(projectProperties.getLevelAllResources()));
      options.setLevelWithinFloat(Boolean.valueOf(projectProperties.getLevelResourcesOnlyWithinActivityTotalFloat()));
      options.setMinFloatToPreserve(Integer.valueOf((int) projectProperties.getPreserveMinimumFloatWhenLeveling().convertUnits(TimeUnit.HOURS, projectProperties).getDuration()));
      options.setOverAllocationPercentage(getDouble(projectProperties.getMaxPercentToOverallocateResources()));
      options.setPriorityList("(0||priority_type(sort_type|ASC)())"); // TODO: translation required

      //
      // Schedule
      //
      //customProperties.put("SetDataDateAndPlannedStartToProjectForecastStart", Boolean.valueOf(row.getBoolean("sched_setplantoforecast")));

      //
      // Schedule Options - General
      //
      options.setIgnoreOtherProjectRelationships(Boolean.valueOf(projectProperties.getIgnoreRelationshipsToAndFromOtherProjects()));
      options.setMakeOpenEndedActivitiesCritical(Boolean.valueOf(projectProperties.getMakeOpenEndedActivitiesCritical()));
      options.setUseExpectedFinishDates(Boolean.valueOf(projectProperties.getUseExpectedFinishDates()));

      // Schedule automatically when a change affects dates - not in PMXML?

      // Level resources during scheduling - not in PMXML?

      options.setStartToStartLagCalculationType(Boolean.valueOf(projectProperties.getComputeStartToStartLagFromEarlyStart()));
      options.setOutOfSequenceScheduleType(SchedulingProgressedActivitiesHelper.getXmlFromInstance(projectProperties.getSchedulingProgressedActivities()));

      // Define critical activities as
      options.setCalculateFloatBasedOnFinishDate(Boolean.valueOf(projectProperties.getCalculateFloatBasedOnFinishDateOfEachProject()));
      options.setRelationshipLagCalendar(RelationshipLagCalendarHelper.getXmlFromInstance(projectProperties.getRelationshipLagCalendar()));
      options.setComputeTotalFloatType(TotalSlackCalculationTypeHelper.getXmlFromInstance(projectProperties.getTotalSlackCalculationType()));
      options.setCriticalActivityFloatThreshold(Double.valueOf(projectProperties.getCriticalSlackLimit().convertUnits(TimeUnit.HOURS, projectProperties).getDuration()));
      options.setCriticalActivityPathType(CriticalActivityTypeHelper.getXmlFromInstance(projectProperties.getCriticalActivityType()));

      //
      // Schedule Options - Advanced
      //
      options.setMultipleFloatPathsEnabled(Boolean.valueOf(projectProperties.getCalculateMultipleFloatPaths()));
      options.setMultipleFloatPathsUseTotalFloat(Boolean.valueOf(projectProperties.getCalculateMultipleFloatPathsUsingTotalFloat()));
      options.setMultipleFloatPathsEndingActivityObjectId(projectProperties.getDisplayMultipleFloatPathsEndingWithActivityUniqueID());
      options.setMaximumMultipleFloatPaths(projectProperties.getMaximumNumberOfFloatPathsToCalculate());
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
    * This method writes calendar data to a PMXML file.
    */
   private void writeCalendars()
   {
      List<CalendarType> calendars = m_apibo.getCalendar();
      m_projectFile.getCalendars().stream().filter(c -> c.getType() != org.mpxj.CalendarType.PROJECT).forEach(c -> writeCalendar(calendars, c));
   }

   /**
    * This method writes project calendar data to a PMXML file.
    *
    * @param calendars project calendar container
    */
   private void writeCalendars(List<CalendarType> calendars)
   {
      m_projectFile.getCalendars().stream().filter(c -> c.getType() == org.mpxj.CalendarType.PROJECT).forEach(c -> writeCalendar(calendars, c));
   }

   /**
    * This method writes data for an individual calendar to a PMXML file.
    *
    * @param calendars calendars container
    * @param calendar ProjectCalendar instance
    */
   private void writeCalendar(List<CalendarType> calendars, ProjectCalendar calendar)
   {
      ProjectCalendar mpxj = ProjectCalendarHelper.normalizeCalendar(calendar);
      CalendarType xml = m_factory.createCalendarType();
      calendars.add(xml);

      String name = mpxj.getName();
      if (name == null || name.isEmpty())
      {
         name = "(blank)";
      }

      if (calendar.getType() == org.mpxj.CalendarType.PROJECT)
      {
         xml.setProjectObjectId(m_projectObjectID);
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

      for (DayOfWeek day : DayOfWeekHelper.ORDERED_DAYS)
      {
         StandardWorkHours xmlHours = m_factory.createCalendarTypeStandardWorkWeekStandardWorkHours();
         xmlStandardWorkWeek.getStandardWorkHours().add(xmlHours);
         xmlHours.setDayOfWeek(getDayName(day));

         // Working days/hours are not inherited between calendars, just exceptions.
         for (LocalTimeRange range : mpxj.getHours(day))
         {
            WorkTimeType xmlWorkTime = m_factory.createWorkTimeType();
            xmlHours.getWorkTime().add(xmlWorkTime);

            xmlWorkTime.setStart(range.getStart());
            xmlWorkTime.setFinish(getEndTime(range.getEnd()));
         }
      }

      HolidayOrExceptions xmlExceptions = m_factory.createCalendarTypeHolidayOrExceptions();
      xml.setHolidayOrExceptions(xmlExceptions);

      List<ProjectCalendarException> expandedExceptions = mpxj.getExpandedCalendarExceptionsWithWorkWeeks();
      if (!expandedExceptions.isEmpty())
      {
         Set<LocalDate> exceptionDates = new HashSet<>();

         for (ProjectCalendarException mpxjException : expandedExceptions)
         {
            LocalDate date = mpxjException.getFromDate();
            while (!date.isAfter(mpxjException.getToDate()))
            {
               // Prevent duplicate exception dates being written.
               // P6 will fail to import files with duplicate exceptions.
               if (exceptionDates.add(date))
               {
                  HolidayOrException xmlException = m_factory.createCalendarTypeHolidayOrExceptionsHolidayOrException();
                  xmlExceptions.getHolidayOrException().add(xmlException);

                  xmlException.setDate(date.atStartOfDay());

                  for (LocalTimeRange range : mpxjException)
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
               date = date.plusDays(1);
            }
         }
      }
   }

   /**
    * This method writes resource data to a PMXML file.
    */
   private void writeResources()
   {
      m_projectFile.getResources().stream().filter(r -> !r.getRole() && r.getUniqueID().intValue() != 0).forEach(this::writeResource);
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
      Double defaultUnitsPerTime = mpxj.getDefaultUnits() == null ? NumberHelper.DOUBLE_ZERO : Double.valueOf(mpxj.getDefaultUnits().doubleValue() / 100.0);

      xml.setAutoComputeActuals(Boolean.TRUE);
      xml.setCalculateCostFromUnits(Boolean.valueOf(mpxj.getCalculateCostsFromUnits()));
      xml.setCalendarObjectId(mpxj.getCalendarUniqueID());
      xml.setCurrencyObjectId(mpxj.getCurrencyUniqueID() == null ? DEFAULT_CURRENCY_ID : mpxj.getCurrencyUniqueID());
      xml.setEmailAddress(mpxj.getEmailAddress());
      xml.setEmployeeId(mpxj.getCode());
      xml.setGUID(DatatypeConverter.printUUID(mpxj.getGUID()));
      xml.setId(WriterHelper.getResourceID(mpxj));
      xml.setIsActive(Boolean.valueOf(mpxj.getActive()));
      xml.setName(name);
      xml.setObjectId(mpxj.getUniqueID());
      xml.setParentObjectId(mpxj.getParentResourceUniqueID());
      xml.setResourceNotes(getNotes(mpxj.getNotesObject()));
      xml.setResourceType(ResourceTypeHelper.getXmlFromInstance(mpxj.getType()));
      xml.setSequenceNumber(mpxj.getSequenceNumber());
      xml.setLocationObjectId(mpxj.getLocationUniqueID());
      xml.setUnitOfMeasureObjectId(mpxj.getUnitOfMeasureUniqueID());
      xml.setShiftObjectId(mpxj.getShiftUniqueID());
      xml.setPrimaryRoleObjectId(mpxj.getPrimaryRoleUniqueID());

      // Write both attributes for backward compatibility,
      // "DefaultUnitsPerTime" is the value read by recent versions of P6
      // MaxUnitsPerTime is ignored
      xml.setDefaultUnitsPerTime(defaultUnitsPerTime);
      xml.setMaxUnitsPerTime(defaultUnitsPerTime);

      xml.getUDF().addAll(writeUserDefinedFieldAssignments(FieldTypeClass.RESOURCE, false, mpxj));

      writeCodeAssignments(mpxj.getResourceCodeValues(), xml.getCode());
   }

   /**
    * This method writes role data to a PMXML file.
    */
   private void writeRoles()
   {
      m_projectFile.getResources().stream().filter(r -> r.getRole() && r.getUniqueID().intValue() != 0).forEach(this::writeRole);
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
      xml.setId(WriterHelper.getRoleID(mpxj));
      xml.setCalculateCostFromUnits(Boolean.valueOf(mpxj.getCalculateCostsFromUnits()));
      xml.setResponsibilities(getNotes(mpxj.getNotesObject()));
      xml.setSequenceNumber(mpxj.getSequenceNumber());

      writeCodeAssignments(mpxj.getRoleCodeValues(), xml.getCode());
   }

   /**
    * Write all resource role assignments.
    */
   private void writeRoleAssignments()
   {
      m_projectFile.getResources().stream().filter(r -> !r.getRole() && r.getUniqueID().intValue() != 0).sorted(Comparator.comparing(Resource::getUniqueID)).forEach(this::writeRoleAssignments);
   }

   /**
    * Write role assignments for a single resource.
    *
    * @param resource resource
    */
   private void writeRoleAssignments(Resource resource)
   {
      for (Map.Entry<Resource, SkillLevel> entry : resource.getRoleAssignments().entrySet().stream().sorted(Comparator.comparing(e -> e.getKey().getUniqueID())).collect(Collectors.toList()))
      {
         ResourceRoleType assignment = m_factory.createResourceRoleType();
         m_apibo.getResourceRole().add(assignment);

         assignment.setResourceObjectId(resource.getUniqueID());
         assignment.setRoleObjectId(entry.getKey().getUniqueID());
         assignment.setProficiency(SkillLevelHelper.getXmlFromInstance(entry.getValue()));
      }
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
    * Writes a WBS entity to the PMXML file.
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

      xml.setCode(TaskHelper.getWbsCode(mpxj));
      xml.setGUID(DatatypeConverter.printUUID(mpxj.getGUID()));
      xml.setName(name);

      xml.setObjectId(mpxj.getUniqueID());
      xml.setParentObjectId(mpxj.getParentTaskUniqueID());
      xml.setProjectObjectId(m_projectObjectID);
      xml.setSequenceNumber(sequence);

      xml.setStatus("Active");

      xml.getUDF().addAll(writeUserDefinedFieldAssignments(FieldTypeClass.TASK, true, mpxj));

      writeWbsNote(mpxj.getUniqueID(), mpxj.getNotesObject());
   }

   /**
    * Writes an activity to a PMXML file.
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
      LocalDateTime plannedStart = mpxj.getPlannedStart() == null ? mpxj.getStart() : mpxj.getPlannedStart();
      LocalDateTime plannedFinish = mpxj.getPlannedFinish() == null ? mpxj.getFinish() : mpxj.getPlannedFinish();
      ProjectCalendar effectiveCalendar = mpxj.getEffectiveCalendar();

      xml.setActualStartDate(mpxj.getActualStart());
      xml.setActualDuration(getDurationInHours(mpxj.getActualDuration()));
      xml.setActualFinishDate(mpxj.getActualFinish());
      xml.setActualLaborUnits(getDurationInHours(WorkHelper.getActualWorkLabor(mpxj)));
      xml.setActualNonLaborUnits(getDurationInHours(WorkHelper.zeroIfNull(mpxj.getActualWorkNonlabor())));
      xml.setAtCompletionDuration(getDurationInHours(mpxj.getDuration()));
      xml.setCalendarObjectId(effectiveCalendar == null ? null : effectiveCalendar.getUniqueID());
      xml.setDurationPercentComplete(getPercentage(mpxj.getPercentageComplete()));
      xml.setDurationType(TaskTypeHelper.getXmlFromInstance(mpxj.getType()));
      xml.setExpectedFinishDate(mpxj.getExpectedFinish());
      xml.setExternalEarlyStartDate(mpxj.getExternalEarlyStart());
      xml.setExternalLateFinishDate(mpxj.getExternalLateFinish());
      xml.setFinishDate(mpxj.getFinish());
      xml.setGUID(DatatypeConverter.printUUID(mpxj.getGUID()));
      xml.setId(WriterHelper.getActivityID(mpxj));
      // Note that P6 doesn't write this attribute to PMXML, but appears to read it
      xml.setIsLongestPath(mpxj.getLongestPath() ? Boolean.TRUE : null);
      xml.setLevelingPriority(PriorityHelper.getXmlFromInstance(mpxj.getPriority()));
      xml.setLocationObjectId(mpxj.getLocationUniqueID());
      xml.setName(name);
      xml.setObjectId(mpxj.getUniqueID());
      xml.setPercentCompleteType(PercentCompleteTypeHelper.getXmlFromInstance(mpxj.getPercentCompleteType()));
      xml.setPercentComplete(getPercentComplete(mpxj));
      xml.setPhysicalPercentComplete(getPercentage(mpxj.getPhysicalPercentComplete()));
      xml.setPlannedLaborUnits(getDurationInHours(WorkHelper.getPlannedWorkLabor(mpxj)));
      xml.setPlannedNonLaborUnits(getDurationInHours(WorkHelper.zeroIfNull(mpxj.getPlannedWorkNonlabor())));
      xml.setPrimaryConstraintType(ConstraintTypeHelper.getXmlFromInstance(mpxj.getConstraintType()));
      xml.setPrimaryConstraintDate(mpxj.getConstraintDate());
      xml.setPrimaryResourceObjectId(mpxj.getPrimaryResourceUniqueID());
      xml.setPlannedDuration(getDurationInHours(mpxj.getPlannedDuration() == null ? mpxj.getDuration() : mpxj.getPlannedDuration()));
      xml.setPlannedFinishDate(plannedFinish);
      xml.setPlannedStartDate(plannedStart);
      xml.setProjectObjectId(m_projectObjectID);
      xml.setRemainingDuration(getDurationInHours(mpxj.getRemainingDuration()));
      xml.setRemainingEarlyFinishDate(mpxj.getRemainingEarlyFinish());
      xml.setRemainingEarlyStartDate(mpxj.getRemainingEarlyStart());
      xml.setRemainingLateFinishDate(mpxj.getRemainingLateFinish());
      xml.setRemainingLateStartDate(mpxj.getRemainingLateStart());
      xml.setRemainingLaborCost(NumberHelper.DOUBLE_ZERO);
      xml.setRemainingNonLaborCost(NumberHelper.DOUBLE_ZERO);
      xml.setRemainingLaborUnits(getDurationInHours(WorkHelper.getRemainingWorkLabor(mpxj)));
      xml.setRemainingNonLaborUnits(getDurationInHours(WorkHelper.zeroIfNull(mpxj.getRemainingWorkNonlabor())));

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
      xml.getUDF().addAll(writeUserDefinedFieldAssignments(FieldTypeClass.TASK, false, mpxj));

      writeActivityNote(mpxj);
      writePredecessors(mpxj);
      writeCodeAssignments(mpxj.getActivityCodeValues(), xml.getCode());
   }

   /**
    * Writes assignment data to a PMXML file.
    */
   private void writeAssignments()
   {
      List<ResourceAssignment> assignments = new ArrayList<>();
      m_projectFile.getTasks().forEach(t -> assignments.addAll(t.getResourceAssignments()));
      assignments.stream().filter(WriterHelper::isValidAssignment).forEach(this::writeAssignment);
   }

   /**
    * Writes a resource assignment to a PMXML file.
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
      Double actualOvertimeUnits = Optional.ofNullable(getDurationInHours(mpxj.getActualOvertimeWork())).orElse(NumberHelper.DOUBLE_ZERO);
      LocalDateTime plannedStart = Optional.ofNullable(mpxj.getPlannedStart()).orElseGet(mpxj::getStart);
      plannedStart = Optional.ofNullable(plannedStart).orElseGet(task::getStart);
      // If we can't find any finish date to use we'll fall back on using the start date which we'll assume is always populated
      LocalDateTime plannedFinish = Optional.ofNullable(mpxj.getPlannedFinish()).orElseGet(mpxj::getFinish);
      plannedFinish = Optional.ofNullable(plannedFinish).orElseGet(task::getFinish);
      plannedFinish = Optional.ofNullable(plannedFinish).orElse(plannedStart);

      Double actualWork = getDurationInHours(mpxj.getActualWork());
      Double atCompletionWork = getDurationInHours(Duration.add(mpxj.getActualWork(), mpxj.getRemainingWork(), mpxj.getEffectiveCalendar()));
      Double unitsPercentComplete = NumberHelper.getDouble(atCompletionWork) == 0.0 || NumberHelper.getDouble(actualWork) == 0.0 ? null : Double.valueOf(actualWork.doubleValue() / atCompletionWork.doubleValue());

      if (mpxj.getResource().getRole())
      {
         xml.setRoleObjectId(mpxj.getResourceUniqueID());
      }
      else
      {
         xml.setResourceObjectId(mpxj.getResourceUniqueID());
         xml.setRoleObjectId(mpxj.getRoleUniqueID());
      }

      xml.setActivityObjectId(mpxj.getTaskUniqueID());
      xml.setActualCost(getCurrency(mpxj.getActualCost()));
      xml.setActualFinishDate(mpxj.getActualFinish());
      xml.setActualOvertimeCost(getCurrency(mpxj.getActualOvertimeCost()));
      xml.setActualOvertimeUnits(actualOvertimeUnits);
      xml.setActualRegularUnits(getDurationInHours(mpxj.getActualWork()));
      xml.setActualStartDate(mpxj.getActualStart());
      xml.setActualUnits(actualWork);
      xml.setAtCompletionCost(getCurrency(NumberHelper.sumAsDouble(mpxj.getActualCost(), mpxj.getRemainingCost())));
      xml.setAtCompletionUnits(atCompletionWork);
      xml.setResourceCurveObjectId(CurveHelper.getCurveID(mpxj.getWorkContour()));
      xml.setFinishDate(mpxj.getFinish());
      xml.setGUID(DatatypeConverter.printUUID(mpxj.getGUID()));
      xml.setIsCostUnitsLinked(Boolean.valueOf(mpxj.getCalculateCostsFromUnits()));
      xml.setObjectId(mpxj.getUniqueID());
      xml.setPlannedCost(getCurrency(mpxj.getPlannedCost()));
      xml.setPlannedFinishDate(plannedFinish);
      xml.setPlannedStartDate(plannedStart);
      xml.setProjectObjectId(m_projectObjectID);
      xml.setRemainingCost(getCurrency(mpxj.getRemainingCost()));

      xml.setStartDate(mpxj.getStart());
      xml.setWBSObjectId(task.getParentTaskUniqueID());
      xml.getUDF().addAll(writeUserDefinedFieldAssignments(FieldTypeClass.ASSIGNMENT, false, mpxj));
      xml.setRateType(RateTypeHelper.getXmlFromInstance(mpxj.getRateIndex()));
      xml.setCostPerQuantity(writeRate(mpxj.getOverrideRate()));
      xml.setRateSource(RateSourceHelper.getXmlFromInstance(mpxj.getRateSource()));
      xml.setCostAccountObjectId(mpxj.getCostAccountUniqueID());
      xml.setRemainingStartDate(mpxj.getRemainingEarlyStart());
      xml.setRemainingFinishDate(mpxj.getRemainingEarlyFinish());
      xml.setResourceType(ResourceTypeHelper.getXmlFromInstance(mpxj.getResource().getType()));

      PmxmlUnitsHelper unitsHelper = new PmxmlUnitsHelper(mpxj);
      xml.setPlannedUnits(unitsHelper.getPlannedUnits());
      xml.setPlannedUnitsPerTime(unitsHelper.getPlannedUnitsPerTime());
      xml.setRemainingUnits(unitsHelper.getRemainingUnits());
      xml.setRemainingUnitsPerTime(unitsHelper.getRemainingUnitsPerTime());
      xml.setRemainingDuration(getResourceAssignmentRemainingDuration(task, mpxj));
      xml.setUnitsPercentComplete(unitsPercentComplete);

      if (m_projectFromPrimavera)
      {
         ProjectCalendar calendar = task.getEffectiveCalendar();
         xml.setPlannedCurve(TimephasedHelper.write(calendar, mpxj.getTimephasedPlannedWork()));
         xml.setActualCurve(TimephasedHelper.write(calendar, mpxj.getTimephasedActualWork()));
         xml.setRemainingCurve(TimephasedHelper.write(calendar, mpxj.getTimephasedWork()));
      }

      writeCodeAssignments(mpxj.getResourceAssignmentCodeValues(), xml.getCode());
   }

   private Double getResourceAssignmentRemainingDuration(Task task, ResourceAssignment mpxj)
   {
      if (mpxj.getActualFinish() != null)
      {
         return Double.valueOf(0);
      }

      if (mpxj.getRemainingUnits() == null || mpxj.getRemainingWork() == null || mpxj.getRemainingUnits().doubleValue() == 0)
      {
         return getDurationInHours(task.getEffectiveCalendar().getWork(mpxj.getRemainingEarlyStart(), mpxj.getRemainingEarlyFinish(), TimeUnit.HOURS));
      }

      double workPerHour = mpxj.getRemainingUnits().doubleValue();
      double remainingWork = mpxj.getRemainingWork().getDuration();
      return Double.valueOf((remainingWork * 100.0) / workPerHour);
   }

   /**
    * Writes task predecessor links to a PMXML file.
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

         xml.setLag(getDurationInHours(mpxj.getLag()));
         xml.setObjectId(mpxj.getUniqueID());
         xml.setPredecessorActivityObjectId(mpxj.getPredecessorTask().getUniqueID());
         xml.setSuccessorActivityObjectId(mpxj.getSuccessorTask().getUniqueID());
         xml.setPredecessorProjectObjectId(m_projectObjectID);
         xml.setSuccessorProjectObjectId(m_projectObjectID);
         xml.setType(RelationTypeHelper.getXmlFromInstance(mpxj.getType()));
         xml.setComments(mpxj.getNotes());
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

         expense.setAccrualType(AccrueTypeHelper.getXmlFromInstance(item.getAccrueType()));
         //expense.setActivityId(value);
         //expense.setActivityName(value);
         expense.setActivityObjectId(task.getUniqueID());
         expense.setActualCost(getCurrency(item.getActualCost()));
         expense.setActualUnits(item.getActualUnits());
         //expense.setAtCompletionCost(getCurrency(item.getAtCompletionCost()));
         //expense.setAtCompletionUnits(item.getAtCompletionUnits());
         expense.setAutoComputeActuals(Boolean.valueOf(item.getAutoComputeActuals()));
         //expense.setCBSCode(value);
         //expense.setCBSId(value);
         expense.setCostAccountObjectId(item.getAccountUniqueID());
         //expense.setCreateDate(value);
         //expense.setCreateUser(value);
         expense.setDocumentNumber(item.getDocumentNumber());
         expense.setExpenseCategoryObjectId(item.getCategoryUniqueID());
         expense.setExpenseDescription(item.getDescription());

         expense.setExpenseItem(item.getName());
         //expense.setExpensePercentComplete(value);
         //expense.setIsBaseline(value);
         //expense.setIsTemplate(value);
         //expense.setLastUpdateDate(value);
         //expense.setLastUpdateUser(value);
         expense.setObjectId(item.getUniqueID());
         expense.setPlannedCost(getCurrency(item.getPlannedCost()));
         expense.setPlannedUnits(item.getPlannedUnits());
         expense.setPricePerUnit(pricePerUnit);
         //expense.setProjectId(PROJECT_ID);
         expense.setProjectObjectId(m_projectObjectID);
         expense.setRemainingCost(getCurrency(item.getRemainingCost()));
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
      m_projectFile.getResources().stream().filter(r -> !r.getRole() && r.getUniqueID().intValue() != 0).forEach(this::writeResourceRates);
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
            Double maxUnits = availability == null || availability.getUnits() == null ? Double.valueOf(1) : Double.valueOf(availability.getUnits().doubleValue() / 100.0);

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
            rate.setShiftPeriodObjectId(entry.getShiftPeriod() == null ? null : entry.getShiftPeriod().getUniqueID());
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

      return Double.valueOf(RateHelper.convertToHours(m_projectFile.getProjectProperties(), rate));
   }

   /**
    * Write rate information for each role.
    */
   private void writeRoleRates()
   {
      m_projectFile.getResources().stream().filter(Resource::getRole).forEach(this::writeRoleRates);
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
      boolean fromDate = (LocalDateTimeHelper.compare(entry.getStartDate(), LocalDateTimeHelper.START_DATE_NA) > 0);
      boolean toDate = (LocalDateTimeHelper.compare(entry.getEndDate(), LocalDateTimeHelper.END_DATE_NA) > 0);
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
      for (NotesTopic entry : m_projectFile.getNotesTopics())
      {
         NotebookTopicType xml = m_factory.createNotebookTopicType();
         m_apibo.getNotebookTopic().add(xml);

         xml.setAvailableForEPS(Boolean.valueOf(entry.getAvailableForEPS()));
         xml.setAvailableForProject(Boolean.valueOf(entry.getAvailableForProject()));
         xml.setAvailableForActivity(Boolean.valueOf(entry.getAvailableForActivity()));
         xml.setAvailableForWBS(Boolean.valueOf(entry.getAvailableForWBS()));
         xml.setName(entry.getName());
         xml.setObjectId(entry.getUniqueID());
         xml.setSequenceNumber(entry.getSequenceNumber());
      }
   }

   /**
    * Write notes for a WBS or Project entry.
    *
    * @param wbsObjectID WBS object ID (null for project notes)
    * @param notes notes
    */
   private void writeWbsNote(Integer wbsObjectID, Notes notes)
   {
      if (notes == null || notes.toString().isEmpty())
      {
         return;
      }

      if (notesAreNativeFormat(notes))
      {
         writeNativeWbsNote(wbsObjectID, (ParentNotes) notes);
      }
      else
      {
         writeDefaultWbsNote(wbsObjectID, notes.toString());
      }
   }

   /**
    * Generate a notebook entry from plain text.
    *
    * @param wbsObjectID WBS object ID
    * @param notes notes text
    */
   private void writeDefaultWbsNote(Integer wbsObjectID, String notes)
   {
      ProjectNoteType xml = m_factory.createProjectNoteType();
      m_projectNotes.add(xml);

      xml.setNote(HtmlHelper.getHtmlFromPlainText(notes));
      xml.setNotebookTopicObjectId(m_projectFile.getNotesTopics().getDefaultTopic().getUniqueID());
      xml.setObjectId(m_sequences.getWbsNoteObjectID());
      xml.setProjectObjectId(m_projectObjectID);
      xml.setWBSObjectId(wbsObjectID);
   }

   /**
    * Generate notebook entries from structured notes.
    *
    * @param wbsObjectID WBS object ID
    * @param notes notes object
    */
   private void writeNativeWbsNote(Integer wbsObjectID, ParentNotes notes)
   {
      for (Notes note : notes.getChildNotes())
      {
         StructuredNotes structuredNotes = (StructuredNotes) note;

         ProjectNoteType xml = m_factory.createProjectNoteType();
         m_projectNotes.add(xml);

         xml.setNote(getNotes(structuredNotes.getNotes()));
         xml.setNotebookTopicObjectId(structuredNotes.getTopicID());
         xml.setObjectId(structuredNotes.getUniqueID());
         xml.setProjectObjectId(m_projectObjectID);
         xml.setWBSObjectId(wbsObjectID);
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

      xml.setNote(HtmlHelper.getHtmlFromPlainText(task.getNotes()));
      xml.setNotebookTopicObjectId(m_projectFile.getNotesTopics().getDefaultTopic().getUniqueID());
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

         xml.setNote(htmlNotes.getHtml());
         xml.setNotebookTopicObjectId(structuredNotes.getTopicID());
         xml.setObjectId(structuredNotes.getUniqueID());
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
      return notes instanceof ParentNotes && ((ParentNotes) notes).getChildNotes().stream().allMatch(n -> n instanceof StructuredNotes);
   }

   /**
    * Writes a list of UDF types.
    *
    * @author lsong
    * @param type parent entity type
    * @param summaryTaskOnly true if we're writing assignments for WBS
    * @param mpxj parent entity
    * @return list of UDFAssignmentType instances
    */
   private List<UDFAssignmentType> writeUserDefinedFieldAssignments(FieldTypeClass type, boolean summaryTaskOnly, FieldContainer mpxj)
   {
      List<UDFAssignmentType> out = new ArrayList<>();
      CustomFieldContainer customFields = m_projectFile.getCustomFields();

      for (FieldType fieldType : m_userDefinedFields)
      {
         if (type != fieldType.getFieldTypeClass())
         {
            continue;
         }

         // For the moment we're restricting writing WBS UDF assignments only to
         // UserDefinedField instances with summaryTaskOnly set to true
         // (which will typically be for values read from a P6 schedule originally)
         // TODO: consider if we can map non task user defined fields from other schedules to WBS UDF
         if (type == FieldTypeClass.TASK && summaryTaskOnly)
         {
            if (fieldType instanceof TaskField || (fieldType instanceof UserDefinedField && !((UserDefinedField) fieldType).getSummaryTaskOnly()))
            {
               continue;
            }
         }

         Object value = mpxj.getCachedValue(fieldType);
         if (value == null)
         {
            continue;
         }

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

      out.sort(Comparator.comparing(UDFAssignmentType::getTypeObjectId));

      return out;
   }

   /**
    * Write Global and EPS activity code definitions.
    */
   private void writeActivityCodeDefinitions()
   {
      List<ActivityCodeTypeType> codes = m_apibo.getActivityCodeType();
      List<ActivityCodeType> values = m_apibo.getActivityCode();
      m_projectFile.getActivityCodes().stream().filter(c -> c.getScope() != ActivityCodeScope.PROJECT).sorted(Comparator.comparing(a -> a.getSequenceNumber() == null ? Integer.valueOf(0) : a.getSequenceNumber())).forEach(c -> writeActivityCodeDefinition(codes, values, c));
   }

   /**
    * Write Project activity code definitions.
    *
    * @param codes activity codes container
    * @param values activity code values container
    */
   private void writeActivityCodeDefinitions(List<ActivityCodeTypeType> codes, List<ActivityCodeType> values)
   {
      m_projectFile.getActivityCodes().stream().filter(c -> c.getScope() == ActivityCodeScope.PROJECT).sorted(Comparator.comparing(a -> a.getSequenceNumber() == null ? Integer.valueOf(0) : a.getSequenceNumber())).forEach(c -> writeActivityCodeDefinition(codes, values, c));
   }

   /**
    * Write an activity code definition.
    *
    * @param codes activity codes container
    * @param values activity code values container
    * @param code activity code
    */
   private void writeActivityCodeDefinition(List<ActivityCodeTypeType> codes, List<ActivityCodeType> values, ActivityCode code)
   {
      ActivityCodeTypeType xml = m_factory.createActivityCodeTypeType();
      codes.add(xml);
      xml.setObjectId(code.getUniqueID());
      xml.setScope(ActivityCodeScopeHelper.getXmlFromInstance(code.getScope()));
      xml.setSequenceNumber(code.getSequenceNumber());
      xml.setName(code.getName());
      xml.setIsSecureCode(Boolean.valueOf(code.getSecure()));
      xml.setLength(WriterHelper.getCodeMaxLength(code));

      if (code.getScope() != ActivityCodeScope.GLOBAL)
      {
         xml.setProjectObjectId(m_projectObjectID);
      }

      Comparator<ActivityCodeValue> comparator = Comparator.comparing(ActivityCodeValue::getSequenceNumber).thenComparing(ActivityCodeValue::getUniqueID);
      code.getChildValues().stream().sorted(comparator).forEach(v -> writeActivityCodeValueDefinition(xml, null, values, v, comparator));
   }

   /**
    * Write project code definitions.
    */
   private void writeProjectCodeDefinitions()
   {
      for (ProjectCode code : m_projectFile.getProjectCodes())
      {
         ProjectCodeTypeType xmlCode = m_factory.createProjectCodeTypeType();
         m_apibo.getProjectCodeType().add(xmlCode);
         xmlCode.setObjectId(code.getUniqueID());
         xmlCode.setName(code.getName());
         xmlCode.setSequenceNumber(code.getSequenceNumber());
         xmlCode.setIsSecureCode(Boolean.valueOf(code.getSecure()));
         xmlCode.setLength(WriterHelper.getCodeMaxLength(code));

         for (ProjectCodeValue value : code.getValues())
         {
            ProjectCodeType xmlValue = m_factory.createProjectCodeType();
            m_apibo.getProjectCode().add(xmlValue);

            xmlValue.setObjectId(value.getUniqueID());
            xmlValue.setCodeTypeObjectId(code.getUniqueID());
            xmlValue.setCodeValue(value.getName());
            xmlValue.setDescription(value.getDescription());
            xmlValue.setParentObjectId(value.getParentValueUniqueID());
            xmlValue.setSequenceNumber(value.getSequenceNumber());
         }
      }
   }

   /**
    * Write resource code definitions.
    */
   private void writeResourceCodeDefinitions()
   {
      for (ResourceCode code : m_projectFile.getResourceCodes())
      {
         ResourceCodeTypeType xmlCode = m_factory.createResourceCodeTypeType();
         m_apibo.getResourceCodeType().add(xmlCode);
         xmlCode.setObjectId(code.getUniqueID());
         xmlCode.setName(code.getName());
         xmlCode.setSequenceNumber(code.getSequenceNumber());
         xmlCode.setIsSecureCode(Boolean.valueOf(code.getSecure()));
         xmlCode.setLength(WriterHelper.getCodeMaxLength(code));

         for (ResourceCodeValue value : code.getValues())
         {
            ResourceCodeType xmlValue = m_factory.createResourceCodeType();
            m_apibo.getResourceCode().add(xmlValue);

            xmlValue.setObjectId(value.getUniqueID());
            xmlValue.setCodeTypeObjectId(code.getUniqueID());
            xmlValue.setCodeValue(value.getName());
            xmlValue.setDescription(value.getDescription());
            xmlValue.setParentObjectId(value.getParentValueUniqueID());
            xmlValue.setSequenceNumber(value.getSequenceNumber());
         }
      }
   }

   /**
    * Write role code definitions.
    */
   private void writeRoleCodeDefinitions()
   {
      for (RoleCode code : m_projectFile.getRoleCodes())
      {
         RoleCodeTypeType xmlCode = m_factory.createRoleCodeTypeType();
         m_apibo.getRoleCodeType().add(xmlCode);
         xmlCode.setObjectId(code.getUniqueID());
         xmlCode.setName(code.getName());
         xmlCode.setSequenceNumber(code.getSequenceNumber());
         xmlCode.setIsSecureCode(Boolean.valueOf(code.getSecure()));
         xmlCode.setLength(WriterHelper.getCodeMaxLength(code));

         for (RoleCodeValue value : code.getValues())
         {
            RoleCodeType xmlValue = m_factory.createRoleCodeType();
            m_apibo.getRoleCode().add(xmlValue);

            xmlValue.setObjectId(value.getUniqueID());
            xmlValue.setCodeTypeObjectId(code.getUniqueID());
            xmlValue.setCodeValue(value.getName());
            xmlValue.setDescription(value.getDescription());
            xmlValue.setParentObjectId(value.getParentValueUniqueID());
            xmlValue.setSequenceNumber(value.getSequenceNumber());
         }
      }
   }

   /**
    * Write role code definitions.
    */
   private void writeResourceAssignmentCodeDefinitions()
   {
      for (ResourceAssignmentCode code : m_projectFile.getResourceAssignmentCodes())
      {
         ResourceAssignmentCodeTypeType xmlCode = m_factory.createResourceAssignmentCodeTypeType();
         m_apibo.getResourceAssignmentCodeType().add(xmlCode);
         xmlCode.setObjectId(code.getUniqueID());
         xmlCode.setName(code.getName());
         xmlCode.setSequenceNumber(code.getSequenceNumber());
         xmlCode.setIsSecureCode(Boolean.valueOf(code.getSecure()));
         xmlCode.setLength(WriterHelper.getCodeMaxLength(code));

         for (ResourceAssignmentCodeValue value : code.getValues())
         {
            ResourceAssignmentCodeType xmlValue = m_factory.createResourceAssignmentCodeType();
            m_apibo.getResourceAssignmentCode().add(xmlValue);

            xmlValue.setObjectId(value.getUniqueID());
            xmlValue.setCodeTypeObjectId(code.getUniqueID());
            xmlValue.setCodeValue(value.getName());
            xmlValue.setDescription(value.getDescription());
            xmlValue.setParentObjectId(value.getParentValueUniqueID());
            xmlValue.setSequenceNumber(value.getSequenceNumber());
         }
      }
   }

   /**
    * Write code assignments.
    *
    * @param map code and value mapping
    * @param assignments code assignments
    */
   private void writeCodeAssignments(Map<? extends Code, ? extends CodeValue> map, List<CodeAssignmentType> assignments)
   {
      map.values().stream().sorted(Comparator.comparing(CodeValue::getUniqueID)).forEach(v -> writeCodeAssignment(assignments, v));
   }

   /**
    * Write a code assignment.
    *
    * @param assignments code assignments
    * @param value project code value
    */
   private void writeCodeAssignment(List<CodeAssignmentType> assignments, CodeValue value)
   {
      CodeAssignmentType xml = m_factory.createCodeAssignmentType();
      assignments.add(xml);
      xml.setTypeObjectId(NumberHelper.getInt(value.getParentCodeUniqueID()));
      xml.setValueObjectId(NumberHelper.getInt(value.getUniqueID()));
   }

   /**
    * Write an activity code value.
    *
    * @param code parent activity code
    * @param parentValue parent value
    * @param values value container
    * @param value value to write
    * @param comparator sort order for values
    */
   private void writeActivityCodeValueDefinition(ActivityCodeTypeType code, ActivityCodeType parentValue, List<ActivityCodeType> values, ActivityCodeValue value, Comparator<ActivityCodeValue> comparator)
   {
      ActivityCodeType xml = m_factory.createActivityCodeType();
      values.add(xml);
      xml.setObjectId(value.getUniqueID());
      xml.setProjectObjectId(code.getProjectObjectId());
      xml.setCodeTypeObjectId(code.getObjectId());
      xml.setParentObjectId(parentValue == null ? null : parentValue.getObjectId());
      xml.setSequenceNumber(value.getSequenceNumber());
      xml.setCodeValue(value.getName());
      xml.setDescription(value.getDescription());
      xml.setColor(ColorHelper.getHtmlColor(value.getColor()));

      value.getChildValues().stream().sorted(comparator).forEach(v -> writeActivityCodeValueDefinition(code, xml, values, v, comparator));
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
            udf.setStartDateValue((LocalDateTime) value);
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
   private Double getDurationInHours(Duration duration)
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

         // Round to 2 decimal places which still allows minute accuracy
         result = Double.valueOf(Math.round(duration.getDuration() * 100.0) / 100.0);
      }
      return result;
   }

   /**
    * Formats a day name.
    *
    * @param day MPXJ Day instance
    * @return Primavera day instance
    */
   private String getDayName(DayOfWeek day)
   {
      return DAY_NAMES[DayOfWeekHelper.getValue(day) - 1];
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
    * Formats a currency value.
    *
    * @param number numeric value
    * @return Double instance
    */
   private Double getCurrency(Number number)
   {
      Double result = null;

      if (number != null)
      {
         // P6 appears to write currency with 8 decimal places, so we'll round match this
         result = Double.valueOf(Math.round(number.doubleValue() * 100000000.0) / 100000000.0);
      }

      return result;
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
   private LocalTime getEndTime(LocalTime date)
   {
      return date.minusMinutes(1);
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

   private static final String DEFAULT_PROJECT_ID = "PROJECT";
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
   private boolean m_activityTypePopulated;
   private boolean m_projectFromPrimavera;
}
