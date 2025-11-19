/*
 * file:       XmlContextWriter.java
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

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.mpxj.ActivityCodeScope;
import org.mpxj.Availability;
import org.mpxj.AvailabilityTable;
import org.mpxj.Code;
import org.mpxj.CodeValue;
import org.mpxj.CostAccount;
import org.mpxj.CostRateTable;
import org.mpxj.CostRateTableEntry;
import org.mpxj.Currency;
import org.mpxj.CustomField;
import org.mpxj.DataType;
import org.mpxj.ExpenseCategory;
import org.mpxj.FieldType;
import org.mpxj.FieldTypeClass;
import org.mpxj.HtmlNotes;
import org.mpxj.Location;
import org.mpxj.Notes;
import org.mpxj.NotesTopic;
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
import org.mpxj.UnitOfMeasure;
import org.mpxj.WorkContour;
import org.mpxj.common.FieldTypeHelper;
import org.mpxj.common.HtmlHelper;
import org.mpxj.common.LocalDateTimeHelper;
import org.mpxj.common.NumberHelper;
import org.mpxj.common.RateHelper;
import org.mpxj.primavera.schema.ActivityCodeType;
import org.mpxj.primavera.schema.ActivityCodeTypeType;
import org.mpxj.primavera.schema.CalendarType;
import org.mpxj.primavera.schema.CodeAssignmentType;
import org.mpxj.primavera.schema.CostAccountType;
import org.mpxj.primavera.schema.CurrencyType;
import org.mpxj.primavera.schema.ExpenseCategoryType;
import org.mpxj.primavera.schema.LocationType;
import org.mpxj.primavera.schema.NotebookTopicType;
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
import org.mpxj.primavera.schema.UDFTypeType;
import org.mpxj.primavera.schema.UnitOfMeasureType;

final class XmlContextWriter extends XmlWriter
{
   public XmlContextWriter(XmlWriterState state, ProjectContext context)
   {
      super(state, context);
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
    * Write Global and EPS activity code definitions.
    */
   private void writeActivityCodeDefinitions()
   {
      List<ActivityCodeTypeType> codes = m_state.getApibo().getActivityCodeType();
      List<ActivityCodeType> values = m_state.getApibo().getActivityCode();
      m_context.getActivityCodes().stream().filter(c -> c.getScope() != ActivityCodeScope.PROJECT).sorted(Comparator.comparing(a -> a.getSequenceNumber() == null ? Integer.valueOf(0) : a.getSequenceNumber())).forEach(c -> writeActivityCodeDefinition(codes, values, c, c.getScopeProjectUniqueID()));
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

   public static final Integer DEFAULT_CURRENCY_ID = Integer.valueOf(1);
}
