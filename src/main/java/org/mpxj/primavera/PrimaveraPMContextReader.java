package org.mpxj.primavera;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mpxj.BaselineStrategy;
import org.mpxj.CostAccount;
import org.mpxj.CostAccountContainer;
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
import org.mpxj.UserDefinedField;
import org.mpxj.WorkContour;
import org.mpxj.common.BooleanHelper;
import org.mpxj.common.HierarchyHelper;
import org.mpxj.common.NumberHelper;
import org.mpxj.primavera.schema.APIBusinessObjects;
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
import org.mpxj.primavera.schema.RoleCodeType;
import org.mpxj.primavera.schema.RoleCodeTypeType;
import org.mpxj.primavera.schema.ShiftPeriodType;
import org.mpxj.primavera.schema.ShiftType;
import org.mpxj.primavera.schema.UDFTypeType;
import org.mpxj.primavera.schema.UnitOfMeasureType;

class PrimaveraPMContextReader
{
   public PrimaveraPMContextReader(APIBusinessObjects apibo, BaselineStrategy baselineStrategy)
   {
      m_apibo = apibo;
      m_baselineStrategy = baselineStrategy;
   }
   
   public ProjectContext read()
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
      return m_context;
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
      config.setBaselineStrategy(m_baselineStrategy);
   }

   /**
    * Process project code definitions.
    *
    * @param apibo top level object
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
    *
    * @param apibo top level object
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
    *
    * @param apibo top level object
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
    *
    * @param apibo top level object
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
    *
    * @param apibo top level object
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
    *
    * @param apibo top level object
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
    *
    * @param apibo top level object
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
    *
    * @param apibo top level object
    */
   private void processExpenseCategories()
   {
      ExpenseCategoryContainer container = m_context.getExpenseCategories();
      m_apibo.getExpenseCategory().forEach(c -> container.add(new ExpenseCategory.Builder(m_context).uniqueID(c.getObjectId()).name(c.getName()).sequenceNumber(c.getSequenceNumber()).build()));
   }

   /**
    * Process cost accounts.
    *
    * @param apibo top level object
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
    *
    * @param apibo top level object
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
    *
    * @param apibo top level object
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
    *
    * @param apibo top level object
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

   private final ProjectContext m_context = new ProjectContext();
   private final APIBusinessObjects m_apibo;
   private final BaselineStrategy m_baselineStrategy;
}
