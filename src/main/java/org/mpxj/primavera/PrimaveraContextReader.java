package org.mpxj.primavera;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mpxj.ActivityCode;
import org.mpxj.ActivityCodeContainer;
import org.mpxj.ActivityCodeValue;
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
import org.mpxj.UserDefinedFieldContainer;
import org.mpxj.common.ColorHelper;
import org.mpxj.common.HierarchyHelper;

abstract class PrimaveraContextReader
{
   protected void configure()
   {
      ProjectConfig config = m_context.getProjectConfig();
      config.setAutoTaskUniqueID(false);
      config.setAutoResourceUniqueID(false);
      config.setAutoAssignmentUniqueID(false);
      config.setAutoWBS(false);
      config.setAutoRelationUniqueID(false);
      config.setBaselineStrategy(PrimaveraBaselineStrategy.PLANNED_ATTRIBUTES);
   }

   /**
    * Process currencies.
    *
    * @param currencies currency data
    */
   protected void processCurrencies(List<Row> currencies)
   {
      CurrencyContainer container = m_context.getCurrencies();
      currencies.forEach(
         row -> container.add(
            new Currency.Builder(m_context)
               .uniqueID(row.getInteger("curr_id"))
               .numberOfDecimalPlaces(row.getInteger("decimal_digit_cnt"))
               .symbol(row.getString("curr_symbol"))
               .decimalSymbol(row.getString("decimal_symbol"))
               .digitGroupingSymbol(row.getString("digit_group_symbol"))
               .positiveCurrencyFormat(row.getString("pos_curr_fmt_type"))
               .negativeCurrencyFormat(row.getString("neg_curr_fmt_type"))
               .name(row.getString("curr_type"))
               .currencyID(row.getString("curr_short_name"))
               // group_digit_cnt
               .exchangeRate(row.getDouble("base_exch_rate"))
               .build()));
   }

   /**
    * Process locations.
    *
    * @param locations locations data
    */
   protected void processLocations(List<Row> locations)
   {
      LocationContainer container = m_context.getLocations();
      locations.forEach(
         row -> container.add(
            new Location.Builder(m_context)
               .uniqueID(row.getInteger("location_id"))
               .name(row.getString("location_name"))
               .addressLine1(row.getString("address_line1"))
               .addressLine2(row.getString("address_line2"))
               .addressLine3(row.getString("address_line3"))
               .city(row.getString("city_name"))
               .municipality(row.getString("municipality_name"))
               .state(row.getString("state_name"))
               .stateCode(row.getString("state_code"))
               .country(row.getString("country_name"))
               .countryCode(row.getString("country_code"))
               .postalCode(row.getString("postal_code"))
               .latitude(row.getDouble("latitude"))
               .longitude(row.getDouble("longitude"))
               .build()));
   }


   /**
    * Process shifts.
    *
    * @param shifts shift data
    * @param periods shift period data
    */
   protected void processShifts(List<Row> shifts, List<Row> periods)
   {
      ShiftContainer shiftContainer = m_context.getShifts();
      shifts.forEach(r -> shiftContainer.add(
         new Shift.Builder(m_context)
            .uniqueID(r.getInteger("shift_id"))
            .name(r.getString("shift_name"))
            .build()));

      ShiftPeriodContainer shiftPeriodContainer = m_context.getShiftPeriods();
      for (Row row : periods)
      {
         Shift shift = shiftContainer.getByUniqueID(row.getInteger("shift_id"));
         if (shift == null)
         {
            continue;
         }

         ShiftPeriod period = new ShiftPeriod.Builder(m_context, shift)
            .uniqueID(row.getInteger("shift_period_id"))
            .start(row.getInteger("shift_start_hr_num"))
            .build();
         shiftPeriodContainer.add(period);
      }
   }

   /**
    * Process units of measure.
    *
    * @param units units of measure
    */
   protected void processUnitsOfMeasure(List<Row> units)
   {
      UnitOfMeasureContainer container = m_context.getUnitsOfMeasure();
      units.forEach(row -> container.add(processUnitOfMeasure(row)));
   }

   /**
    * Create a unit of measure instance.
    *
    * @param row unit of measure data
    * @return UnitOfMeasure instance
    */
   private UnitOfMeasure processUnitOfMeasure(Row row)
   {
      return new UnitOfMeasure.Builder(m_context)
         .uniqueID(row.getInteger("unit_id"))
         .abbreviation(row.getString("unit_abbrev"))
         .name(row.getString("unit_name"))
         .sequenceNumber(row.getInteger("seq_num"))
         .build();
   }

   /**
    * Process expense categories.
    *
    * @param categories expense categories
    */
   protected void processExpenseCategories(List<Row> categories)
   {
      ExpenseCategoryContainer container = m_context.getExpenseCategories();
      categories.forEach(row -> container.add(new ExpenseCategory.Builder(m_context)
         .uniqueID(row.getInteger("cost_type_id"))
         .name(row.getString("cost_type"))
         .sequenceNumber(row.getInteger("seq_num"))
         .build()));
   }

   /**
    * Process cost accounts.
    *
    * @param accounts cost accounts
    */
   protected void processCostAccounts(List<Row> accounts)
   {
      CostAccountContainer container = m_context.getCostAccounts();
      HierarchyHelper.sortHierarchy(accounts, v -> v.getInteger("acct_id"), v -> v.getInteger("parent_acct_id")).forEach(row -> container.add(
         new CostAccount.Builder(m_context)
            .uniqueID(row.getInteger("acct_id"))
            .id(row.getString("acct_short_name"))
            .name(row.getString("acct_name"))
            .notes(NotesHelper.getNotes(row.getString("acct_descr")))
            .sequenceNumber(row.getInteger("acct_seq_num"))
            .parent(container.getByUniqueID(row.getInteger("parent_acct_id")))
            .build()));
   }

   /**
    * Populate notebook topics.
    *
    * @param rows notebook topic rows
    */
   protected void processNotebookTopics(List<Row> rows)
   {
      rows.forEach(this::processNotebookTopic);
   }

   /**
    * Populate an individual notebook topic.
    *
    * @param row notebook topic row
    */
   private void processNotebookTopic(Row row)
   {
      NotesTopic topic = new NotesTopic.Builder(m_context)
         .uniqueID(row.getInteger("memo_type_id"))
         .sequenceNumber(row.getInteger("seq_num"))
         .availableForEPS(row.getBoolean("eps_flag"))
         .availableForProject(row.getBoolean("proj_flag"))
         .availableForWBS(row.getBoolean("wbs_flag"))
         .availableForActivity(row.getBoolean("task_flag"))
         .name(row.getString("memo_type"))
         .build();

      m_context.getNotesTopics().add(topic);
   }

   /**
    * Process User Defined Field (UDF) definitions.
    *
    * @param fields field definitions
    */
   protected void processUdfDefinitions(List<Row> fields)
   {
      UserDefinedFieldContainer container = m_context.getUserDefinedFields();

      for (Row row : fields)
      {
         Integer fieldId = row.getInteger("udf_type_id");
         String tableName = row.getString("table_name");

         FieldTypeClass fieldTypeClass = FieldTypeClassHelper.getInstanceFromXer(tableName);
         if (fieldTypeClass == null)
         {
            continue;
         }

         UserDefinedField fieldType = new UserDefinedField.Builder(m_context)
            .uniqueID(fieldId)
            .internalName(row.getString("udf_type_name"))
            .externalName(row.getString("udf_type_label"))
            .fieldTypeClass(fieldTypeClass)
            .summaryTaskOnly(tableName.equals("PROJWBS"))
            .dataType(UdfHelper.getDataTypeFromXer(row.getString("logical_data_type")))
            .build();

         container.add(fieldType);
         m_context.getCustomFields().add(fieldType).setAlias(fieldType.getName()).setUniqueID(fieldId);
      }
   }

   /**
    * Read project code definitions.
    *
    * @param types project code type data
    * @param typeValues project code value data
    */
   protected void processProjectCodeDefinitions(List<Row> types, List<Row> typeValues)
   {
      ProjectCodeContainer container = m_context.getProjectCodes();
      Map<Integer, ProjectCode> map = new HashMap<>();

      for (Row row : types)
      {
         ProjectCode code = new ProjectCode.Builder(m_context)
            .uniqueID(row.getInteger("proj_catg_type_id"))
            .sequenceNumber(row.getInteger("seq_num"))
            .name(row.getString("proj_catg_type"))
            .maxLength(row.getInteger("proj_catg_short_len"))
            .build();
         container.add(code);
         map.put(code.getUniqueID(), code);
      }

      typeValues = HierarchyHelper.sortHierarchy(typeValues, v -> v.getInteger("proj_catg_id"), v -> v.getInteger("parent_proj_catg_id"));
      for (Row row : typeValues)
      {
         ProjectCode code = map.get(row.getInteger("proj_catg_type_id"));
         if (code != null)
         {
            ProjectCodeValue value = new ProjectCodeValue.Builder(m_context)
               .projectCode(code)
               .uniqueID(row.getInteger("proj_catg_id"))
               .sequenceNumber(row.getInteger("seq_num"))
               .name(row.getString("proj_catg_short_name"))
               .description(row.getString("proj_catg_name"))
               .parentValue(code.getValueByUniqueID(row.getInteger("parent_proj_catg_id")))
               .build();
            code.addValue(value);
         }
      }
   }

   /**
    * Read resource code definitions.
    *
    * @param types resource code type data
    * @param typeValues resource code value data
    */
   protected void processResourceCodeDefinitions(List<Row> types, List<Row> typeValues)
   {
      ResourceCodeContainer container = m_context.getResourceCodes();
      Map<Integer, ResourceCode> map = new HashMap<>();

      for (Row row : types)
      {
         ResourceCode code = new ResourceCode.Builder(m_context)
            .uniqueID(row.getInteger("rsrc_catg_type_id"))
            .sequenceNumber(row.getInteger("seq_num"))
            .name(row.getString("rsrc_catg_type"))
            .maxLength(row.getInteger("rsrc_catg_short_len"))
            .build();
         container.add(code);
         map.put(code.getUniqueID(), code);
      }

      typeValues = HierarchyHelper.sortHierarchy(typeValues, v -> v.getInteger("rsrc_catg_id"), v -> v.getInteger("parent_rsrc_catg_id"));
      for (Row row : typeValues)
      {
         ResourceCode code = map.get(row.getInteger("rsrc_catg_type_id"));
         if (code != null)
         {
            ResourceCodeValue value = new ResourceCodeValue.Builder(m_context)
               .resourceCode(code)
               .uniqueID(row.getInteger("rsrc_catg_id"))
               .sequenceNumber(row.getInteger("seq_num"))
               .name(row.getString("rsrc_catg_short_name"))
               .description(row.getString("rsrc_catg_name"))
               .parentValue(code.getValueByUniqueID(row.getInteger("parent_rsrc_catg_id")))
               .build();
            code.addValue(value);
         }
      }
   }

   /**
    * Read role code definitions.
    *
    * @param types role code type data
    * @param typeValues role code value data
    */
   protected void processRoleCodeDefinitions(List<Row> types, List<Row> typeValues)
   {
      RoleCodeContainer container = m_context.getRoleCodes();
      Map<Integer, RoleCode> map = new HashMap<>();

      for (Row row : types)
      {
         RoleCode code = new RoleCode.Builder(m_context)
            .uniqueID(row.getInteger("role_catg_type_id"))
            .sequenceNumber(row.getInteger("seq_num"))
            .name(row.getString("role_catg_type"))
            .maxLength(row.getInteger("role_catg_short_len"))
            .build();
         container.add(code);
         map.put(code.getUniqueID(), code);
      }

      typeValues = HierarchyHelper.sortHierarchy(typeValues, v -> v.getInteger("role_catg_id"), v -> v.getInteger("parent_role_catg_id"));
      for (Row row : typeValues)
      {
         RoleCode code = map.get(row.getInteger("role_catg_type_id"));
         if (code != null)
         {
            RoleCodeValue value = new RoleCodeValue.Builder(m_context)
               .roleCode(code)
               .uniqueID(row.getInteger("role_catg_id"))
               .sequenceNumber(row.getInteger("seq_num"))
               .name(row.getString("role_catg_short_name"))
               .description(row.getString("role_catg_name"))
               .parentValue(code.getValueByUniqueID(row.getInteger("parent_role_catg_id")))
               .build();
            code.addValue(value);
         }
      }
   }

   /**
    * Read resource assignment code definitions.
    *
    * @param types resource assignment code type data
    * @param typeValues resource assignment code value data
    */
   protected void processResourceAssignmentCodeDefinitions(List<Row> types, List<Row> typeValues)
   {
      ResourceAssignmentCodeContainer container = m_context.getResourceAssignmentCodes();
      Map<Integer, ResourceAssignmentCode> map = new HashMap<>();

      for (Row row : types)
      {
         ResourceAssignmentCode code = new ResourceAssignmentCode.Builder(m_context)
            .uniqueID(row.getInteger("asgnmnt_catg_type_id"))
            .sequenceNumber(row.getInteger("seq_num"))
            .name(row.getString("asgnmnt_catg_type"))
            .maxLength(row.getInteger("asgnmnt_catg_short_len"))
            .build();
         container.add(code);
         map.put(code.getUniqueID(), code);
      }

      typeValues = HierarchyHelper.sortHierarchy(typeValues, v -> v.getInteger("asgnmnt_catg_id"), v -> v.getInteger("parent_asgnmnt_catg_id"));
      for (Row row : typeValues)
      {
         ResourceAssignmentCode code = map.get(row.getInteger("asgnmnt_catg_type_id"));
         if (code != null)
         {
            ResourceAssignmentCodeValue value = new ResourceAssignmentCodeValue.Builder(m_context)
               .resourceAssignmentCode(code)
               .uniqueID(row.getInteger("asgnmnt_catg_id"))
               .sequenceNumber(row.getInteger("seq_num"))
               .name(row.getString("asgnmnt_catg_short_name"))
               .description(row.getString("asgnmnt_catg_name"))
               .parentValue(code.getValueByUniqueID(row.getInteger("parent_asgnmnt_catg_id")))
               .build();
            code.addValue(value);
         }
      }
   }

   /**
    * Read activity code definitions.
    *
    * @param types activity code type data
    * @param typeValues activity code value data
    */
   protected void processActivityCodeDefinitions(List<Row> types, List<Row> typeValues)
   {
      ActivityCodeContainer container = m_context.getActivityCodes();
      Map<Integer, ActivityCode> map = new HashMap<>();

      for (Row row : types)
      {
         ActivityCode code = new ActivityCode.Builder(m_context)
            .uniqueID(row.getInteger("actv_code_type_id"))
            .scope(ActivityCodeScopeHelper.getInstanceFromXer(row.getString("actv_code_type_scope")))
            .scopeEpsUniqueID(row.getInteger("wbs_id"))
            .scopeProjectUniqueID(row.getInteger("proj_id"))
            .sequenceNumber(row.getInteger("seq_num"))
            .name(row.getString("actv_code_type"))
            .secure(row.getBoolean("super_flag"))
            .maxLength(row.getInteger("actv_short_len"))
            .build();
         container.add(code);
         map.put(code.getUniqueID(), code);
      }

      typeValues = HierarchyHelper.sortHierarchy(typeValues, v -> v.getInteger("actv_code_id"), v -> v.getInteger("parent_actv_code_id"));
      for (Row row : typeValues)
      {
         ActivityCode code = map.get(row.getInteger("actv_code_type_id"));
         if (code != null)
         {
            ActivityCodeValue value = new ActivityCodeValue.Builder(m_context)
               .activityCode(code)
               .uniqueID(row.getInteger("actv_code_id"))
               .sequenceNumber(row.getInteger("seq_num"))
               .name(row.getString("short_name"))
               .description(row.getString("actv_code_name"))
               .color(ColorHelper.parseHexColor(row.getString("color")))
               .parentValue(code.getValueByUniqueID(row.getInteger("parent_actv_code_id")))
               .build();
            code.addValue(value);
         }
      }
   }

   protected final ProjectContext m_context = new ProjectContext();
}
