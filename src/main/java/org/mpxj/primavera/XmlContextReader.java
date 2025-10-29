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
import org.mpxj.ProjectContext;
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
import org.mpxj.primavera.schema.APIBusinessObjects;
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

class XmlContextReader
{
   public XmlContextReader(ProjectContext context, APIBusinessObjects apibo, ClashMap roleClashMap)
   {
      m_context = context;
      m_apibo = apibo;
      m_roleClashMap = roleClashMap;
   }
   
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
      XmlReaderHelper.processActivityCodeDefinitions(m_context, m_apibo.getActivityCodeType(), m_apibo.getActivityCode());
      XmlReaderHelper.processCalendars(m_context, m_apibo.getCalendar());
      processResources();
      processRoles();
      processRoleAssignments();
      processResourceRates();
      processRoleRates();
   }

   private void configure()
   {
      ProjectConfig config = m_context.getProjectConfig();
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
      ProjectCodeContainer container = m_context.getProjectCodes();
      Map<Integer, ProjectCode> map = new HashMap<>();

      for (ProjectCodeTypeType type : m_apibo.getProjectCodeType())
      {
         ProjectCode code = new ProjectCode.Builder(m_context)
            .uniqueID(type.getObjectId())
            .sequenceNumber(type.getSequenceNumber())
            .name(type.getName())
            .secure(BooleanHelper.getBoolean(type.isIsSecureCode()))
            .maxLength(type.getLength())
            .build();
         container.add(code);
         map.put(code.getUniqueID(), code);
      }

      List<ProjectCodeType> typeValues = HierarchyHelper.sortHierarchy(m_apibo.getProjectCode(), ProjectCodeType::getObjectId, ProjectCodeType::getParentObjectId);
      for (ProjectCodeType typeValue : typeValues)
      {
         ProjectCode code = map.get(typeValue.getCodeTypeObjectId());
         if (code != null)
         {
            ProjectCodeValue value = new ProjectCodeValue.Builder(m_context)
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
      ResourceCodeContainer container = m_context.getResourceCodes();
      Map<Integer, ResourceCode> map = new HashMap<>();

      for (ResourceCodeTypeType type : m_apibo.getResourceCodeType())
      {
         ResourceCode code = new ResourceCode.Builder(m_context)
            .uniqueID(type.getObjectId())
            .sequenceNumber(type.getSequenceNumber())
            .name(type.getName())
            .secure(BooleanHelper.getBoolean(type.isIsSecureCode()))
            .maxLength(type.getLength())
            .build();
         container.add(code);
         map.put(code.getUniqueID(), code);
      }

      List<ResourceCodeType> typeValues = HierarchyHelper.sortHierarchy(m_apibo.getResourceCode(), ResourceCodeType::getObjectId, ResourceCodeType::getParentObjectId);
      for (ResourceCodeType typeValue : typeValues)
      {
         ResourceCode code = map.get(typeValue.getCodeTypeObjectId());
         if (code != null)
         {
            ResourceCodeValue value = new ResourceCodeValue.Builder(m_context)
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
      RoleCodeContainer container = m_context.getRoleCodes();
      Map<Integer, RoleCode> map = new HashMap<>();

      for (RoleCodeTypeType type : m_apibo.getRoleCodeType())
      {
         RoleCode code = new RoleCode.Builder(m_context)
            .uniqueID(type.getObjectId())
            .sequenceNumber(type.getSequenceNumber())
            .name(type.getName())
            .secure(BooleanHelper.getBoolean(type.isIsSecureCode()))
            .maxLength(type.getLength())
            .build();
         container.add(code);
         map.put(code.getUniqueID(), code);
      }

      List<RoleCodeType> typeValues = HierarchyHelper.sortHierarchy(m_apibo.getRoleCode(), RoleCodeType::getObjectId, RoleCodeType::getParentObjectId);
      for (RoleCodeType typeValue : typeValues)
      {
         RoleCode code = map.get(typeValue.getCodeTypeObjectId());
         if (code != null)
         {
            RoleCodeValue value = new RoleCodeValue.Builder(m_context)
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
      ResourceAssignmentCodeContainer container = m_context.getResourceAssignmentCodes();
      Map<Integer, ResourceAssignmentCode> map = new HashMap<>();

      for (ResourceAssignmentCodeTypeType type : m_apibo.getResourceAssignmentCodeType())
      {
         ResourceAssignmentCode code = new ResourceAssignmentCode.Builder(m_context)
            .uniqueID(type.getObjectId())
            .sequenceNumber(type.getSequenceNumber())
            .name(type.getName())
            .secure(BooleanHelper.getBoolean(type.isIsSecureCode()))
            .maxLength(type.getLength())
            .build();
         container.add(code);
         map.put(code.getUniqueID(), code);
      }

      List<ResourceAssignmentCodeType> typeValues = HierarchyHelper.sortHierarchy(m_apibo.getResourceAssignmentCode(), ResourceAssignmentCodeType::getObjectId, ResourceAssignmentCodeType::getParentObjectId);
      for (ResourceAssignmentCodeType typeValue : typeValues)
      {
         ResourceAssignmentCode code = map.get(typeValue.getCodeTypeObjectId());
         if (code != null)
         {
            ResourceAssignmentCodeValue value = new ResourceAssignmentCodeValue.Builder(m_context)
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
      LocationContainer container = m_context.getLocations();
      m_apibo.getLocation().forEach(c -> container.add(
         new Location.Builder(m_context)
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
      CurrencyContainer container = m_context.getCurrencies();

      m_apibo.getCurrency().forEach(c -> container.add(
         new Currency.Builder(m_context)
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
      ShiftContainer shiftContainer = m_context.getShifts();
      ShiftPeriodContainer shiftPeriodContainer = m_context.getShiftPeriods();

      for (ShiftType xml : m_apibo.getShift())
      {
         Shift shift = new Shift.Builder(m_context)
            .name(xml.getName())
            .uniqueID(xml.getObjectId())
            .build();
         shiftContainer.add(shift);

         for (ShiftPeriodType xmlPeriod : xml.getShiftPeriod())
         {
            ShiftPeriod period = new ShiftPeriod.Builder(m_context, shift)
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
      ExpenseCategoryContainer container = m_context.getExpenseCategories();
      m_apibo.getExpenseCategory().forEach(c -> container.add(new ExpenseCategory.Builder(m_context).uniqueID(c.getObjectId()).name(c.getName()).sequenceNumber(c.getSequenceNumber()).build()));
   }

   /**
    * Process cost accounts.
    */
   private void processCostAccounts()
   {
      CostAccountContainer container = m_context.getCostAccounts();
      HierarchyHelper.sortHierarchy(m_apibo.getCostAccount(), CostAccountType::getObjectId, CostAccountType::getParentObjectId).forEach(c -> container.add(
         new CostAccount.Builder(m_context)
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
      UnitOfMeasureContainer container = m_context.getUnitsOfMeasure();
      m_apibo.getUnitOfMeasure().forEach(u -> container.add(processUnitOfMeasure(u)));
   }

   /**
    * Create a unit of measure.
    *
    * @param u unit of measure data
    * @return UnitOfMeasure instance
    */
   private UnitOfMeasure processUnitOfMeasure(UnitOfMeasureType u)
   {
      return new UnitOfMeasure.Builder(m_context)
         .uniqueID(u.getObjectId())
         .abbreviation(u.getAbbreviation())
         .name(u.getName())
         .sequenceNumber(u.getSequenceNumber())
         .build();
   }

   private void processWorkContours()
   {
      m_apibo.getResourceCurve().forEach(this::processWorkContour);
   }

   private void processWorkContour(ResourceCurveType curve)
   {
      if (m_context.getWorkContours().getByUniqueID(curve.getObjectId()) != null)
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

      m_context.getWorkContours().add(new WorkContour(curve.getObjectId(), curve.getName(), BooleanHelper.getBoolean(curve.isIsDefault()), values));
   }

   /**
    * Populate notebook topics.
    */
   private void processNotebookTopics()
   {
      m_apibo.getNotebookTopic().forEach(this::processNotebookTopic);
   }

   /**
    * Populate an individual notebook topic.
    *
    * @param xml notebook topic data
    */
   private void processNotebookTopic(NotebookTopicType xml)
   {
      NotesTopic topic = new NotesTopic.Builder(m_context)
         .uniqueID(xml.getObjectId())
         .sequenceNumber(xml.getSequenceNumber())
         .availableForEPS(BooleanHelper.getBoolean(xml.isAvailableForEPS()))
         .availableForProject(BooleanHelper.getBoolean(xml.isAvailableForProject()))
         .availableForWBS(BooleanHelper.getBoolean(xml.isAvailableForWBS()))
         .availableForActivity(BooleanHelper.getBoolean(xml.isAvailableForActivity()))
         .name(xml.getName())
         .build();

      m_context.getNotesTopics().add(topic);
   }

   /**
    * Process UDF definitions.
    */
   private void processUdfDefintions()
   {
      for (UDFTypeType udf : m_apibo.getUDFType())
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

      UserDefinedField field = new UserDefinedField.Builder(m_context)
         .uniqueID(udf.getObjectId())
         .externalName(udf.getTitle())
         .fieldTypeClass(fieldTypeClass)
         .summaryTaskOnly(udf.getSubjectArea().equals("WBS"))
         .dataType(UdfHelper.getDataTypeFromXml(udf.getDataType()))
         .build();

      m_context.getUserDefinedFields().add(field);
      m_context.getCustomFields().add(field).setAlias(udf.getTitle()).setUniqueID(udf.getObjectId());
   }

   /**
    * Process resources.
    */
   private void processResources()
   {
      List<ResourceType> resources = m_apibo.getResource();
      for (ResourceType xml : resources)
      {
         Resource resource = m_context.getResources().add();
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
         resource.setNotesObject(NotesHelper.getNotes(xml.getResourceNotes()));
         resource.setCreationDate(xml.getCreateDate());
         resource.setType(ResourceTypeHelper.getInstanceFromXml(xml.getResourceType()));
         resource.setDefaultUnits(defaultUnitsPerTime);
         resource.setParentResourceUniqueID(xml.getParentObjectId());
         resource.setResourceID(xml.getId());
         resource.setCalendar(m_context.getCalendars().getByUniqueID(xml.getCalendarObjectId()));
         resource.setCalculateCostsFromUnits(BooleanHelper.getBoolean(xml.isCalculateCostFromUnits()));
         resource.setSequenceNumber(xml.getSequenceNumber());
         resource.setActive(BooleanHelper.getBoolean(xml.isIsActive()));
         resource.setLocationUniqueID(xml.getLocationObjectId());
         resource.setUnitOfMeasureUniqueID(xml.getUnitOfMeasureObjectId());
         resource.setShiftUniqueID(xml.getShiftObjectId());
         resource.setPrimaryRoleUniqueID(xml.getPrimaryRoleObjectId());
         resource.setCurrencyUniqueID(xml.getCurrencyObjectId());

         processResourceCodeAssignments(resource, xml.getCode());

         XmlReaderHelper.populateUserDefinedFieldValues(m_context, resource, xml.getUDF());

         m_context.getEventManager().fireResourceReadEvent(resource);
      }
   }

   /**
    * Process roles.
    */
   private void processRoles()
   {
      for (RoleType role : m_apibo.getRole())
      {
         Resource resource = m_context.getResources().add();
         resource.setRole(true);
         resource.setUniqueID(m_roleClashMap.getID(role.getObjectId()));
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
      for (ResourceRoleType assignment : m_apibo.getResourceRole())
      {
         Resource resource = m_context.getResources().getByUniqueID(assignment.getResourceObjectId());
         if (resource == null)
         {
            continue;
         }

         Resource role = m_context.getResources().getByUniqueID(assignment.getRoleObjectId());
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
      List<ResourceRateType> rates = new ArrayList<>(m_apibo.getResourceRate());

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
            resource = m_context.getResources().getByUniqueID(resourceID);
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
         ShiftPeriod period = m_context.getShiftPeriods().getByUniqueID(row.getShiftPeriodObjectId());
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
      List<RoleRateType> rates = new ArrayList<>(m_apibo.getRoleRateNew().isEmpty() ? m_apibo.getRoleRate() : m_apibo.getRoleRateNew());

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
            resource = m_context.getResources().getByUniqueID(resourceID);
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
         ResourceCode code = m_context.getResourceCodes().getByUniqueID(Integer.valueOf(assignment.getTypeObjectId()));
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
         RoleCode code = m_context.getRoleCodes().getByUniqueID(Integer.valueOf(assignment.getTypeObjectId()));
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

   private final ProjectContext m_context;
   private final APIBusinessObjects m_apibo;
   private final ClashMap m_roleClashMap;
}
