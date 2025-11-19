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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
import org.mpxj.CustomField;
import org.mpxj.CustomFieldContainer;
import org.mpxj.DataType;
import org.mpxj.Duration;
import org.mpxj.ExpenseCategory;
import org.mpxj.FieldContainer;
import org.mpxj.FieldType;
import org.mpxj.FieldTypeClass;
import org.mpxj.HtmlNotes;
import org.mpxj.LocalTimeRange;
import org.mpxj.Location;
import org.mpxj.Notes;
import org.mpxj.NotesTopic;
import org.mpxj.ProjectCalendar;
import org.mpxj.ProjectCalendarException;
import org.mpxj.ProjectCode;
import org.mpxj.ProjectCodeValue;
import org.mpxj.ProjectContext;
import org.mpxj.Rate;
import org.mpxj.Resource;
import org.mpxj.ResourceAssignmentCode;
import org.mpxj.ResourceAssignmentCodeValue;
import org.mpxj.ResourceCode;
import org.mpxj.ResourceCodeValue;
import org.mpxj.RoleCode;
import org.mpxj.RoleCodeValue;
import org.mpxj.Shift;
import org.mpxj.ShiftPeriod;
import org.mpxj.SkillLevel;
import org.mpxj.TaskField;
import org.mpxj.UnitOfMeasure;
import org.mpxj.UserDefinedField;
import org.mpxj.WorkContour;
import org.mpxj.common.BooleanHelper;
import org.mpxj.common.ColorHelper;
import org.mpxj.common.DayOfWeekHelper;
import org.mpxj.common.FieldTypeHelper;
import org.mpxj.common.HtmlHelper;
import org.mpxj.common.LocalDateTimeHelper;
import org.mpxj.common.NumberHelper;
import org.mpxj.common.RateHelper;
import org.mpxj.primavera.schema.ActivityCodeType;
import org.mpxj.primavera.schema.ActivityCodeTypeType;
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
import org.mpxj.primavera.schema.ResourceAssignmentCodeType;
import org.mpxj.primavera.schema.ResourceAssignmentCodeTypeType;
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
import org.mpxj.primavera.schema.ShiftPeriodType;
import org.mpxj.primavera.schema.ShiftType;
import org.mpxj.primavera.schema.UDFAssignmentType;
import org.mpxj.primavera.schema.UDFTypeType;
import org.mpxj.primavera.schema.UnitOfMeasureType;
import org.mpxj.primavera.schema.WorkTimeType;

final class PrimaveraPMContextWriter
{
   public PrimaveraPMContextWriter(XmlWriterState state, ProjectContext context)
   {
      m_state = state;
      m_context = context;
   }

   public void write()
   {
      writeCurrencies();
      writeLocations();
      writeShifts();
      writeUnitsOfMeasure();
      writeProjectCodeDefinitions();
      writeResourceCodeDefinitions();
      writeRoleCodeDefinitions();
      writeResourceAssignmentCodeDefinitions();
      writeActivityCodeDefinitions();
      writeUserDefinedFieldDefinitions();
      writeExpenseCategories();
      writeCostAccounts();
      writeResourceCurves();
      writeGlobalCalendars();
      writeResources();
      writeRoles();
      writeRoleAssignments();
      writeResourceRates();
      writeRoleRates();
      writeTopics();
   }

   /**
    * Write currencies.
    */
   private void writeCurrencies()
   {
      if (m_context.getCurrencies().isEmpty())
      {
         writeCurrency(m_state.getDefaultCurrency());
      }
      else
      {
         m_context.getCurrencies().forEach(this::writeCurrency);
      }
   }

   private void writeCurrency(Currency currency)
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
      m_state.getApibo().getCurrency().add(xml);
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
    * Add UDFType objects to a PMXML file.
    *
    * @author kmahan
    * @author lsong
    */
   private void writeUserDefinedFieldDefinitions()
   {
      List<UDFTypeType> fields = m_state.getApibo().getUDFType();

      for (FieldType type : m_state.getUserDefinedFields())
      {
         CustomField field = m_context.getCustomFields().get(type);
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
      List<LocationType> locations = m_state.getApibo().getLocation();
      for (Location location : m_context.getLocations())
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
      List<ShiftType> shifts = m_state.getApibo().getShift();
      for (Shift shift : m_context.getShifts())
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
      List<ExpenseCategoryType> expenseCategories = m_state.getApibo().getExpenseCategory();
      for (ExpenseCategory category : m_context.getExpenseCategories())
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
      List<CostAccountType> costAccounts = m_state.getApibo().getCostAccount();
      m_context.getCostAccounts().stream().sorted(Comparator.comparing(CostAccount::getUniqueID)).forEach(c -> writeCostAccount(costAccounts, c));
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
      List<UnitOfMeasureType> units = m_state.getApibo().getUnitOfMeasure();
      for (UnitOfMeasure uom : m_context.getUnitsOfMeasure())
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
      List<WorkContour> contours = m_context.getWorkContours().stream().filter(w -> !w.isContourManual() && !w.isContourFlat()).sorted(Comparator.comparing(WorkContour::getName)).collect(Collectors.toList());

      List<ResourceCurveType> curves = m_state.getApibo().getResourceCurve();
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
    * This method writes calendar data to a PMXML file.
    */
   private void writeGlobalCalendars()
   {
      List<CalendarType> calendars = m_state.getApibo().getCalendar();
      m_context.getCalendars().stream().filter(c -> c.getType() != org.mpxj.CalendarType.PROJECT).forEach(c -> writeCalendar(calendars, c));
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
         xml.setProjectObjectId(mpxj.getProjectUniqueID());
      }

      xml.setBaseCalendarObjectId(mpxj.getParentUniqueID());
      xml.setIsDefault(Boolean.valueOf(mpxj.getDefault()));
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
      m_context.getResources().stream().filter(r -> !r.getRole() && r.getUniqueID().intValue() != 0).forEach(this::writeResource);
   }

   /**
    * Write a single resource.
    *
    * @param mpxj Resource instance
    */
   private void writeResource(Resource mpxj)
   {
      ResourceType xml = m_factory.createResourceType();
      m_state.getApibo().getResource().add(xml);

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
      xml.setCurrencyObjectId(mpxj.getCurrencyUniqueID() == null ? CurrencyHelper.DEFAULT_CURRENCY_ID : mpxj.getCurrencyUniqueID());
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
      m_context.getResources().stream().filter(r -> r.getRole() && r.getUniqueID().intValue() != 0).forEach(this::writeRole);
   }

   /**
    * Write a single role.
    *
    * @param mpxj Resource instance
    */
   private void writeRole(Resource mpxj)
   {
      RoleType xml = m_factory.createRoleType();
      m_state.getApibo().getRole().add(xml);

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
      m_context.getResources().stream().filter(r -> !r.getRole() && r.getUniqueID().intValue() != 0).sorted(Comparator.comparing(Resource::getUniqueID)).forEach(this::writeRoleAssignments);
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
         m_state.getApibo().getResourceRole().add(assignment);

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
    * Write rate information for each resource.
    */
   private void writeResourceRates()
   {
      m_context.getResources().stream().filter(r -> !r.getRole() && r.getUniqueID().intValue() != 0).forEach(this::writeResourceRates);
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
            m_state.getApibo().getResourceRate().add(rate);

            //rate.setCreateDate(value);
            //rate.setCreateUser(value);
            rate.setEffectiveDate(entry.getStartDate());
            //rate.setLastUpdateDate(value);
            //rate.setLastUpdateUser(value);
            rate.setMaxUnitsPerTime(maxUnits);
            rate.setObjectId(m_state.getRateObjectID());
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

      return Double.valueOf(RateHelper.convertToHours(m_context.getTimeUnitDefaults(), rate));
   }

   /**
    * Write rate information for each role.
    */
   private void writeRoleRates()
   {
      m_context.getResources().stream().filter(Resource::getRole).forEach(this::writeRoleRates);
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
            m_state.getApibo().getRoleRate().add(rate);

            //rate.setCreateDate(value);
            //rate.setCreateUser(value);
            rate.setEffectiveDate(entry.getStartDate());
            //rate.setLastUpdateDate(value);
            //rate.setLastUpdateUser(value);
            rate.setMaxUnitsPerTime(maxUnits);
            rate.setObjectId(m_state.getRateObjectID());
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
      m_context.getNotesTopics().forEach(this::writeTopic);
   }

   void writeTopic(NotesTopic entry)
   {
      NotebookTopicType xml = m_factory.createNotebookTopicType();
      m_state.getApibo().getNotebookTopic().add(xml);

      xml.setAvailableForEPS(Boolean.valueOf(entry.getAvailableForEPS()));
      xml.setAvailableForProject(Boolean.valueOf(entry.getAvailableForProject()));
      xml.setAvailableForActivity(Boolean.valueOf(entry.getAvailableForActivity()));
      xml.setAvailableForWBS(Boolean.valueOf(entry.getAvailableForWBS()));
      xml.setName(entry.getName());
      xml.setObjectId(entry.getUniqueID());
      xml.setSequenceNumber(entry.getSequenceNumber());
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
      CustomFieldContainer customFields = m_context.getCustomFields();

      for (FieldType fieldType : m_state.getUserDefinedFields())
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
      List<ActivityCodeTypeType> codes = m_state.getApibo().getActivityCodeType();
      List<ActivityCodeType> values = m_state.getApibo().getActivityCode();
      m_context.getActivityCodes().stream().filter(c -> c.getScope() != ActivityCodeScope.PROJECT).sorted(Comparator.comparing(a -> a.getSequenceNumber() == null ? Integer.valueOf(0) : a.getSequenceNumber())).forEach(c -> writeActivityCodeDefinition(codes, values, c));
   }

   /**
    * Write Project activity code definitions.
    *
    * @param codes activity codes container
    * @param values activity code values container
    */
   private void writeActivityCodeDefinitions(List<ActivityCodeTypeType> codes, List<ActivityCodeType> values)
   {
      m_context.getActivityCodes().stream().filter(c -> c.getScope() == ActivityCodeScope.PROJECT).sorted(Comparator.comparing(a -> a.getSequenceNumber() == null ? Integer.valueOf(0) : a.getSequenceNumber())).forEach(c -> writeActivityCodeDefinition(codes, values, c));
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
         xml.setProjectObjectId(code.getScopeProjectUniqueID());
      }

      Comparator<ActivityCodeValue> comparator = Comparator.comparing(ActivityCodeValue::getSequenceNumber).thenComparing(ActivityCodeValue::getUniqueID);
      code.getChildValues().stream().sorted(comparator).forEach(v -> writeActivityCodeValueDefinition(xml, null, values, v, comparator));
   }

   /**
    * Write project code definitions.
    */
   private void writeProjectCodeDefinitions()
   {
      for (ProjectCode code : m_context.getProjectCodes())
      {
         ProjectCodeTypeType xmlCode = m_factory.createProjectCodeTypeType();
         m_state.getApibo().getProjectCodeType().add(xmlCode);
         xmlCode.setObjectId(code.getUniqueID());
         xmlCode.setName(code.getName());
         xmlCode.setSequenceNumber(code.getSequenceNumber());
         xmlCode.setIsSecureCode(Boolean.valueOf(code.getSecure()));
         xmlCode.setLength(WriterHelper.getCodeMaxLength(code));

         for (ProjectCodeValue value : code.getValues())
         {
            ProjectCodeType xmlValue = m_factory.createProjectCodeType();
            m_state.getApibo().getProjectCode().add(xmlValue);

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
      for (ResourceCode code : m_context.getResourceCodes())
      {
         ResourceCodeTypeType xmlCode = m_factory.createResourceCodeTypeType();
         m_state.getApibo().getResourceCodeType().add(xmlCode);
         xmlCode.setObjectId(code.getUniqueID());
         xmlCode.setName(code.getName());
         xmlCode.setSequenceNumber(code.getSequenceNumber());
         xmlCode.setIsSecureCode(Boolean.valueOf(code.getSecure()));
         xmlCode.setLength(WriterHelper.getCodeMaxLength(code));

         for (ResourceCodeValue value : code.getValues())
         {
            ResourceCodeType xmlValue = m_factory.createResourceCodeType();
            m_state.getApibo().getResourceCode().add(xmlValue);

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
      for (RoleCode code : m_context.getRoleCodes())
      {
         RoleCodeTypeType xmlCode = m_factory.createRoleCodeTypeType();
         m_state.getApibo().getRoleCodeType().add(xmlCode);
         xmlCode.setObjectId(code.getUniqueID());
         xmlCode.setName(code.getName());
         xmlCode.setSequenceNumber(code.getSequenceNumber());
         xmlCode.setIsSecureCode(Boolean.valueOf(code.getSecure()));
         xmlCode.setLength(WriterHelper.getCodeMaxLength(code));

         for (RoleCodeValue value : code.getValues())
         {
            RoleCodeType xmlValue = m_factory.createRoleCodeType();
            m_state.getApibo().getRoleCode().add(xmlValue);

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
      for (ResourceAssignmentCode code : m_context.getResourceAssignmentCodes())
      {
         ResourceAssignmentCodeTypeType xmlCode = m_factory.createResourceAssignmentCodeTypeType();
         m_state.getApibo().getResourceAssignmentCodeType().add(xmlCode);
         xmlCode.setObjectId(code.getUniqueID());
         xmlCode.setName(code.getName());
         xmlCode.setSequenceNumber(code.getSequenceNumber());
         xmlCode.setIsSecureCode(Boolean.valueOf(code.getSecure()));
         xmlCode.setLength(WriterHelper.getCodeMaxLength(code));

         for (ResourceAssignmentCodeValue value : code.getValues())
         {
            ResourceAssignmentCodeType xmlValue = m_factory.createResourceAssignmentCodeType();
            m_state.getApibo().getResourceAssignmentCode().add(xmlValue);

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

   private final XmlWriterState m_state;
   private final ProjectContext m_context;

   private final ObjectFactory m_factory = new ObjectFactory();
}
