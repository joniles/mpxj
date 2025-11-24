/*
 * file:       XmlContextReader.java
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mpxj.Availability;
import org.mpxj.CostAccount;
import org.mpxj.CostAccountContainer;
import org.mpxj.CostRateTableEntry;
import org.mpxj.Currency;
import org.mpxj.CurrencyContainer;
import org.mpxj.ExpenseCategory;
import org.mpxj.ExpenseCategoryContainer;
import org.mpxj.FieldTypeClass;
import org.mpxj.Location;
import org.mpxj.LocationContainer;
import org.mpxj.NotesTopic;
import org.mpxj.ProjectCode;
import org.mpxj.ProjectCodeContainer;
import org.mpxj.ProjectCodeValue;
import org.mpxj.ProjectConfig;
import org.mpxj.Rate;
import org.mpxj.Resource;
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
import org.mpxj.TimeUnit;
import org.mpxj.UnitOfMeasure;
import org.mpxj.UnitOfMeasureContainer;
import org.mpxj.UserDefinedField;
import org.mpxj.WorkContour;
import org.mpxj.common.BooleanHelper;
import org.mpxj.common.HierarchyHelper;
import org.mpxj.common.LocalDateTimeHelper;
import org.mpxj.common.NumberHelper;
import org.mpxj.primavera.schema.CodeAssignmentType;
import org.mpxj.primavera.schema.CostAccountType;
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

/**
 * Populate a ProjectContext instance from an XML file.
 */
class XmlContextReader
{
   /**
    * Constructor.
    *
    * @param state common state data
    */
   public XmlContextReader(XmlReaderState state)
   {
      m_state = state;
   }

   /**
    * Populate the ProjectContext instance by reading data from an XML file.
    */
   public void read()
   {
      configure();
      processCurrencies();
      processLocations();
      processUnitsOfMeasure();
      processExpenseCategories();
      processCostAccounts();
      processWorkContours();
      processNotebookTopics();
      processUdfDefintions();
      processProjectCodeDefinitions();
      processResourceCodeDefinitions();
      processRoleCodeDefinitions();
      processResourceAssignmentCodeDefinitions();
      processShifts();
      XmlReaderHelper.processActivityCodeDefinitions(m_state.getContext(), m_state.getApibo().getActivityCodeType(), m_state.getApibo().getActivityCode());
      XmlReaderHelper.processCalendars(m_state.getContext(), m_state.getApibo().getCalendar());
      processResources();
      processRoles();
      processRoleAssignments();
      processResourceRates();
      processRoleRates();
   }

   /**
    * Populate the project configuration.
    */
   private void configure()
   {
      ProjectConfig config = m_state.getContext().getProjectConfig();
      config.setAutoTaskUniqueID(false);
      config.setAutoResourceUniqueID(false);
      config.setAutoCalendarUniqueID(false);
      config.setAutoAssignmentUniqueID(false);
      config.setAutoWBS(false);
      config.setAutoRelationUniqueID(false);
   }

   /**
    * Process project code definitions.
    */
   private void processProjectCodeDefinitions()
   {
      ProjectCodeContainer container = m_state.getContext().getProjectCodes();
      Map<Integer, ProjectCode> map = new HashMap<>();

      for (ProjectCodeTypeType type : m_state.getApibo().getProjectCodeType())
      {
         ProjectCode code = new ProjectCode.Builder(m_state.getContext())
            .uniqueID(type.getObjectId())
            .sequenceNumber(type.getSequenceNumber())
            .name(type.getName())
            .secure(BooleanHelper.getBoolean(type.isIsSecureCode()))
            .maxLength(type.getLength())
            .build();
         container.add(code);
         map.put(code.getUniqueID(), code);
      }

      List<ProjectCodeType> typeValues = HierarchyHelper.sortHierarchy(m_state.getApibo().getProjectCode(), ProjectCodeType::getObjectId, ProjectCodeType::getParentObjectId);
      for (ProjectCodeType typeValue : typeValues)
      {
         ProjectCode code = map.get(typeValue.getCodeTypeObjectId());
         if (code != null)
         {
            ProjectCodeValue value = new ProjectCodeValue.Builder(m_state.getContext())
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
    */
   private void processResourceCodeDefinitions()
   {
      ResourceCodeContainer container = m_state.getContext().getResourceCodes();
      Map<Integer, ResourceCode> map = new HashMap<>();

      for (ResourceCodeTypeType type : m_state.getApibo().getResourceCodeType())
      {
         ResourceCode code = new ResourceCode.Builder(m_state.getContext())
            .uniqueID(type.getObjectId())
            .sequenceNumber(type.getSequenceNumber())
            .name(type.getName())
            .secure(BooleanHelper.getBoolean(type.isIsSecureCode()))
            .maxLength(type.getLength())
            .build();
         container.add(code);
         map.put(code.getUniqueID(), code);
      }

      List<ResourceCodeType> typeValues = HierarchyHelper.sortHierarchy(m_state.getApibo().getResourceCode(), ResourceCodeType::getObjectId, ResourceCodeType::getParentObjectId);
      for (ResourceCodeType typeValue : typeValues)
      {
         ResourceCode code = map.get(typeValue.getCodeTypeObjectId());
         if (code != null)
         {
            ResourceCodeValue value = new ResourceCodeValue.Builder(m_state.getContext())
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
    */
   private void processRoleCodeDefinitions()
   {
      RoleCodeContainer container = m_state.getContext().getRoleCodes();
      Map<Integer, RoleCode> map = new HashMap<>();

      for (RoleCodeTypeType type : m_state.getApibo().getRoleCodeType())
      {
         RoleCode code = new RoleCode.Builder(m_state.getContext())
            .uniqueID(type.getObjectId())
            .sequenceNumber(type.getSequenceNumber())
            .name(type.getName())
            .secure(BooleanHelper.getBoolean(type.isIsSecureCode()))
            .maxLength(type.getLength())
            .build();
         container.add(code);
         map.put(code.getUniqueID(), code);
      }

      List<RoleCodeType> typeValues = HierarchyHelper.sortHierarchy(m_state.getApibo().getRoleCode(), RoleCodeType::getObjectId, RoleCodeType::getParentObjectId);
      for (RoleCodeType typeValue : typeValues)
      {
         RoleCode code = map.get(typeValue.getCodeTypeObjectId());
         if (code != null)
         {
            RoleCodeValue value = new RoleCodeValue.Builder(m_state.getContext())
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
    */
   private void processResourceAssignmentCodeDefinitions()
   {
      ResourceAssignmentCodeContainer container = m_state.getContext().getResourceAssignmentCodes();
      Map<Integer, ResourceAssignmentCode> map = new HashMap<>();

      for (ResourceAssignmentCodeTypeType type : m_state.getApibo().getResourceAssignmentCodeType())
      {
         ResourceAssignmentCode code = new ResourceAssignmentCode.Builder(m_state.getContext())
            .uniqueID(type.getObjectId())
            .sequenceNumber(type.getSequenceNumber())
            .name(type.getName())
            .secure(BooleanHelper.getBoolean(type.isIsSecureCode()))
            .maxLength(type.getLength())
            .build();
         container.add(code);
         map.put(code.getUniqueID(), code);
      }

      List<ResourceAssignmentCodeType> typeValues = HierarchyHelper.sortHierarchy(m_state.getApibo().getResourceAssignmentCode(), ResourceAssignmentCodeType::getObjectId, ResourceAssignmentCodeType::getParentObjectId);
      for (ResourceAssignmentCodeType typeValue : typeValues)
      {
         ResourceAssignmentCode code = map.get(typeValue.getCodeTypeObjectId());
         if (code != null)
         {
            ResourceAssignmentCodeValue value = new ResourceAssignmentCodeValue.Builder(m_state.getContext())
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
    */
   private void processLocations()
   {
      LocationContainer container = m_state.getContext().getLocations();
      m_state.getApibo().getLocation().forEach(c -> container.add(
         new Location.Builder(m_state.getContext())
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
    */
   private void processCurrencies()
   {
      CurrencyContainer container = m_state.getContext().getCurrencies();

      m_state.getApibo().getCurrency().forEach(c -> container.add(
         new Currency.Builder(m_state.getContext())
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
    */
   private void processShifts()
   {
      ShiftContainer shiftContainer = m_state.getContext().getShifts();
      ShiftPeriodContainer shiftPeriodContainer = m_state.getContext().getShiftPeriods();

      for (ShiftType xml : m_state.getApibo().getShift())
      {
         Shift shift = new Shift.Builder(m_state.getContext())
            .name(xml.getName())
            .uniqueID(xml.getObjectId())
            .build();
         shiftContainer.add(shift);

         for (ShiftPeriodType xmlPeriod : xml.getShiftPeriod())
         {
            ShiftPeriod period = new ShiftPeriod.Builder(m_state.getContext(), shift)
               .uniqueID(xmlPeriod.getObjectId())
               .start(xmlPeriod.getStartHour())
               .build();
            shiftPeriodContainer.add(period);
         }
      }
   }

   /**
    * Process expense categories.
    */
   private void processExpenseCategories()
   {
      ExpenseCategoryContainer container = m_state.getContext().getExpenseCategories();
      m_state.getApibo().getExpenseCategory().forEach(c -> container.add(new ExpenseCategory.Builder(m_state.getContext()).uniqueID(c.getObjectId()).name(c.getName()).sequenceNumber(c.getSequenceNumber()).build()));
   }

   /**
    * Process cost accounts.
    */
   private void processCostAccounts()
   {
      CostAccountContainer container = m_state.getContext().getCostAccounts();
      HierarchyHelper.sortHierarchy(m_state.getApibo().getCostAccount(), CostAccountType::getObjectId, CostAccountType::getParentObjectId).forEach(c -> container.add(
         new CostAccount.Builder(m_state.getContext())
            .uniqueID(c.getObjectId())
            .id(c.getId())
            .name(c.getName())
            .notes(NotesHelper.getNotes(c.getDescription()))
            .sequenceNumber(c.getSequenceNumber())
            .parent(container.getByUniqueID(c.getParentObjectId()))
            .build()));
   }

   /**
    * Process units of measure.
    */
   private void processUnitsOfMeasure()
   {
      UnitOfMeasureContainer container = m_state.getContext().getUnitsOfMeasure();
      m_state.getApibo().getUnitOfMeasure().forEach(u -> container.add(processUnitOfMeasure(u)));
   }

   /**
    * Create a unit of measure.
    *
    * @param u unit of measure data
    * @return UnitOfMeasure instance
    */
   private UnitOfMeasure processUnitOfMeasure(UnitOfMeasureType u)
   {
      return new UnitOfMeasure.Builder(m_state.getContext())
         .uniqueID(u.getObjectId())
         .abbreviation(u.getAbbreviation())
         .name(u.getName())
         .sequenceNumber(u.getSequenceNumber())
         .build();
   }

   private void processWorkContours()
   {
      m_state.getApibo().getResourceCurve().forEach(this::processWorkContour);
   }

   private void processWorkContour(ResourceCurveType curve)
   {
      if (m_state.getContext().getWorkContours().getByUniqueID(curve.getObjectId()) != null)
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

      m_state.getContext().getWorkContours().add(new WorkContour(curve.getObjectId(), curve.getName(), BooleanHelper.getBoolean(curve.isIsDefault()), values));
   }

   /**
    * Populate notebook topics.
    */
   private void processNotebookTopics()
   {
      m_state.getApibo().getNotebookTopic().forEach(this::processNotebookTopic);
   }

   /**
    * Populate an individual notebook topic.
    *
    * @param xml notebook topic data
    */
   private void processNotebookTopic(NotebookTopicType xml)
   {
      NotesTopic topic = new NotesTopic.Builder(m_state.getContext())
         .uniqueID(xml.getObjectId())
         .sequenceNumber(xml.getSequenceNumber())
         .availableForEPS(BooleanHelper.getBoolean(xml.isAvailableForEPS()))
         .availableForProject(BooleanHelper.getBoolean(xml.isAvailableForProject()))
         .availableForWBS(BooleanHelper.getBoolean(xml.isAvailableForWBS()))
         .availableForActivity(BooleanHelper.getBoolean(xml.isAvailableForActivity()))
         .name(xml.getName())
         .build();

      m_state.getContext().getNotesTopics().add(topic);
   }

   /**
    * Process UDF definitions.
    */
   private void processUdfDefintions()
   {
      for (UDFTypeType udf : m_state.getApibo().getUDFType())
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

      UserDefinedField field = new UserDefinedField.Builder(m_state.getContext())
         .uniqueID(udf.getObjectId())
         .externalName(udf.getTitle())
         .fieldTypeClass(fieldTypeClass)
         .summaryTaskOnly(udf.getSubjectArea().equals("WBS"))
         .dataType(UdfHelper.getDataTypeFromXml(udf.getDataType()))
         .build();

      m_state.getContext().getUserDefinedFields().add(field);
      m_state.getContext().getCustomFields().add(field).setAlias(udf.getTitle()).setUniqueID(udf.getObjectId());
   }

   /**
    * Process resources.
    */
   private void processResources()
   {
      List<ResourceType> resources = m_state.getApibo().getResource();
      for (ResourceType xml : resources)
      {
         Resource resource = m_state.getContext().getResources().add();
         m_state.getRoleClashMap().addID(xml.getObjectId());

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
         resource.setNotesObject(NotesHelper.getNotes(xml.getResourceNotes()));
         resource.setCreationDate(xml.getCreateDate());
         resource.setType(ResourceTypeHelper.getInstanceFromXml(xml.getResourceType()));
         resource.setDefaultUnits(defaultUnitsPerTime);
         resource.setParentResourceUniqueID(xml.getParentObjectId());
         resource.setResourceID(xml.getId());
         resource.setCalendar(m_state.getContext().getCalendars().getByUniqueID(xml.getCalendarObjectId()));
         resource.setCalculateCostsFromUnits(BooleanHelper.getBoolean(xml.isCalculateCostFromUnits()));
         resource.setSequenceNumber(xml.getSequenceNumber());
         resource.setActive(BooleanHelper.getBoolean(xml.isIsActive()));
         resource.setLocationUniqueID(xml.getLocationObjectId());
         resource.setUnitOfMeasureUniqueID(xml.getUnitOfMeasureObjectId());
         resource.setShiftUniqueID(xml.getShiftObjectId());
         resource.setPrimaryRoleUniqueID(xml.getPrimaryRoleObjectId());
         resource.setCurrencyUniqueID(xml.getCurrencyObjectId());

         processResourceCodeAssignments(resource, xml.getCode());

         XmlReaderHelper.populateUserDefinedFieldValues(m_state.getContext(), resource, xml.getUDF());

         m_state.getContext().getEventManager().fireResourceReadEvent(resource);
      }
   }

   /**
    * Process roles.
    */
   private void processRoles()
   {
      for (RoleType role : m_state.getApibo().getRole())
      {
         Resource resource = m_state.getContext().getResources().add();
         resource.setRole(true);
         resource.setUniqueID(m_state.getRoleClashMap().getID(role.getObjectId()));
         resource.setName(role.getName());
         resource.setResourceID(role.getId());
         resource.setNotesObject(NotesHelper.getHtmlNote(role.getResponsibilities()));
         resource.setSequenceNumber(role.getSequenceNumber());

         processRoleCodeAssignments(resource, role.getCode());
      }
   }

   /**
    * Process role assignments.
    */
   private void processRoleAssignments()
   {
      for (ResourceRoleType assignment : m_state.getApibo().getResourceRole())
      {
         Resource resource = m_state.getContext().getResources().getByUniqueID(assignment.getResourceObjectId());
         if (resource == null)
         {
            continue;
         }

         Resource role = m_state.getContext().getResources().getByUniqueID(assignment.getRoleObjectId());
         if (role == null)
         {
            continue;
         }

         resource.addRoleAssignment(role, SkillLevelHelper.getInstanceFromXml(assignment.getProficiency()));
      }
   }

   /**
    * Process resource rates.
    */
   private void processResourceRates()
   {
      List<ResourceRateType> rates = new ArrayList<>(m_state.getApibo().getResourceRate());

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
            resource = m_state.getContext().getResources().getByUniqueID(resourceID);
            if (resource == null)
            {
               continue;
            }
            resource.getCostRateTable(0).clear();
         }

         Rate[] values = new Rate[]
         {
            Rate.valueOf(row.getPricePerUnit(), TimeUnit.HOURS),
            Rate.valueOf(row.getPricePerUnit2(), TimeUnit.HOURS),
            Rate.valueOf(row.getPricePerUnit3(), TimeUnit.HOURS),
            Rate.valueOf(row.getPricePerUnit4(), TimeUnit.HOURS),
            Rate.valueOf(row.getPricePerUnit5(), TimeUnit.HOURS),
         };

         Double costPerUse = NumberHelper.getDouble(0.0);
         Double maxUnits = NumberHelper.getDouble(NumberHelper.getDouble(row.getMaxUnitsPerTime()) * 100); // adjust to be % as in MS Project
         ShiftPeriod period = m_state.getContext().getShiftPeriods().getByUniqueID(row.getShiftPeriodObjectId());
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
    * Process role rates.
    */
   private void processRoleRates()
   {
      List<RoleRateType> rates = new ArrayList<>(m_state.getApibo().getRoleRateNew().isEmpty() ? m_state.getApibo().getRoleRate() : m_state.getApibo().getRoleRateNew());

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

         Integer resourceID = m_state.getRoleClashMap().getID(row.getRoleObjectId());
         if (resource == null || !resource.getUniqueID().equals(resourceID))
         {
            resource = m_state.getContext().getResources().getByUniqueID(resourceID);
            if (resource == null)
            {
               continue;
            }
            resource.getCostRateTable(0).clear();
         }

         Rate[] values = new Rate[]
         {
            Rate.valueOf(row.getPricePerUnit(), TimeUnit.HOURS),
            Rate.valueOf(row.getPricePerUnit2(), TimeUnit.HOURS),
            Rate.valueOf(row.getPricePerUnit3(), TimeUnit.HOURS),
            Rate.valueOf(row.getPricePerUnit4(), TimeUnit.HOURS),
            Rate.valueOf(row.getPricePerUnit5(), TimeUnit.HOURS),
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
    * Process resource code assignments.
    *
    * @param resource parent resource
    * @param codes resource code assignments
    */
   private void processResourceCodeAssignments(Resource resource, List<CodeAssignmentType> codes)
   {
      for (CodeAssignmentType assignment : codes)
      {
         ResourceCode code = m_state.getContext().getResourceCodes().getByUniqueID(Integer.valueOf(assignment.getTypeObjectId()));
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
         RoleCode code = m_state.getContext().getRoleCodes().getByUniqueID(Integer.valueOf(assignment.getTypeObjectId()));
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

   private final XmlReaderState m_state;
}
