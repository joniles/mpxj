/*
 * file:       PrimaveraXERFileWriter.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2023
 * date:       07/03/2023
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

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.mpxj.ActivityCode;
import org.mpxj.ActivityCodeValue;
import org.mpxj.ActivityStatus;
import org.mpxj.ActivityType;
import org.mpxj.Availability;
import org.mpxj.CalendarType;
import org.mpxj.CostAccount;
import org.mpxj.CostRateTableEntry;
import org.mpxj.Currency;
import org.mpxj.CustomField;
import org.mpxj.Duration;
import org.mpxj.ExpenseCategory;
import org.mpxj.ExpenseItem;
import org.mpxj.FieldContainer;
import org.mpxj.FieldType;
import org.mpxj.Location;
import org.mpxj.Notes;
import org.mpxj.NotesTopic;
import org.mpxj.ParentNotes;
import org.mpxj.PercentCompleteType;
import org.mpxj.ProjectCalendar;
import org.mpxj.ProjectCode;
import org.mpxj.ProjectCodeValue;
import org.mpxj.ProjectFile;
import org.mpxj.ProjectProperties;
import org.mpxj.Relation;
import org.mpxj.Resource;
import org.mpxj.ResourceAssignment;
import org.mpxj.ResourceAssignmentCode;
import org.mpxj.ResourceAssignmentCodeValue;
import org.mpxj.ResourceCode;
import org.mpxj.ResourceCodeValue;
import org.mpxj.ResourceType;
import org.mpxj.RoleCode;
import org.mpxj.RoleCodeValue;
import org.mpxj.SchedulingProgressedActivities;
import org.mpxj.Shift;
import org.mpxj.ShiftPeriod;
import org.mpxj.SkillLevel;
import org.mpxj.Step;
import org.mpxj.StructuredNotes;
import org.mpxj.Task;
import org.mpxj.TaskContainer;
import org.mpxj.TimeUnit;
import org.mpxj.UnitOfMeasure;
import org.mpxj.UserDefinedField;
import org.mpxj.WorkContour;
import org.mpxj.common.BooleanHelper;
import org.mpxj.common.CharsetHelper;
import org.mpxj.common.FieldTypeHelper;
import org.mpxj.common.NumberHelper;
import org.mpxj.common.ObjectSequence;
import org.mpxj.common.Pair;
import org.mpxj.common.StringHelper;
import org.mpxj.writer.AbstractProjectWriter;

/**
 * XER file writer.
 */
public class PrimaveraXERFileWriter extends AbstractProjectWriter
{
   /**
    * Set the Charset used to write the file.
    *
    * @param charset Charset used when writing the file
    */
   public void setCharset(Charset charset)
   {
      m_charset = charset;
   }

   /**
    * Retrieve the Charset used to write the file.
    *
    * @return Charset instance
    */
   public Charset getCharset()
   {
      return m_charset;
   }

   @Override public void write(ProjectFile projectFile, OutputStream outputStream) throws IOException
   {
      m_file = projectFile;
      m_writer = new XerWriter(projectFile, new OutputStreamWriter(outputStream, getCharset()));
      m_rateObjectID = new ObjectSequence(1);
      m_userDefinedFields = UdfHelper.getUserDefinedFieldsSet(projectFile);
      m_projectFromPrimavera = "Primavera".equals(m_file.getProjectProperties().getFileApplication());

      // We need to do this first to ensure the default topic is created if required
      populateWbsNotes();
      populateActivityNotes();

      // Ensure the WBS hierarchy has a single root WBS
      createValidWbsHierarchy();

      try
      {
         writeHeader();
         writeResourceAssignmentCodes();
         writeExpenseCategories();
         writeCurrencies();
         writeShifts();
         writeShiftPeriods();
         writeLocations();
         writeNoteTypes();
         writeProjectCodes();
         writeResourceCodes();
         writeRoleCodes();
         writeResourceCurves();
         writeUdfDefinitions();
         writeUnitsOfMeasure();
         writeCostAccounts();
         writeResourceAssignmentCodeValues();
         writeProjectCodeValues();
         writeResourceCodeValues();
         writeRoleCodeValues();
         writeRoles();
         writeProject();
         writeRoleRates();
         writeRoleCodeAssignments();
         writeCalendars();
         writeProjectCodeAssignments();
         writeScheduleOptions();
         writeWBS();
         writeResources();
         writeActivityCodes();
         writeResourceRates();
         writeResourceCodeAssignments();
         writeRoleAssignments();
         writeActivities();
         writeWbsNotes();
         writeActivityCodeValues();
         writeActivitySteps();
         writeExpenseItems();
         writeActivityNotes();
         writePredecessors();
         writeResourceAssignments();
         writeResourceAssignmentCodeAssignments();
         writeActivityCodeAssignments();
         writeUdfValues();
         m_writer.writeTrailer();

         m_writer.flush();
      }

      finally
      {
         revertWbsHierarchyChange();
         m_writer = null;
      }
   }

   /**
    * Write the file header.
    */
   private void writeHeader()
   {
      Object[] data =
      {
         "ERMHDR",
         "20.12",
         new DateOnly(m_file.getProjectProperties().getCurrentDate()),
         "Project",
         "admin",
         "admin",
         "dbxDatabaseNoName",
         "Project Management",
         getDefaultCurrency().getCurrencyID()
      };

      m_writer.writeHeader(data);
   }

   /**
    * Write currencies.
    */
   private void writeCurrencies()
   {
      m_writer.writeTable("CURRTYPE", CURRENCY_COLUMNS);
      if (m_file.getCurrencies().isEmpty())
      {
         m_writer.writeRecord(CURRENCY_COLUMNS, DEFAULT_CURRENCY);
      }
      else
      {
         m_file.getCurrencies().forEach(c -> m_writer.writeRecord(CURRENCY_COLUMNS, c));
      }
   }

   /**
    * Write roles.
    */
   private void writeRoles()
   {
      m_writer.writeTable("ROLES", ROLE_COLUMNS);
      getSortedRoleStream().forEach(r -> m_writer.writeRecord(ROLE_COLUMNS, r));
   }

   /**
    * Write role rates.
    */
   private void writeRoleRates()
   {
      m_writer.writeTable("ROLERATE", ROLE_RATE_COLUMNS);
      getSortedRoleStream().forEach(r -> writeCostRateTableEntries(ROLE_RATE_COLUMNS, r));
   }

   /**
    * Write resource rates.
    */
   private void writeResourceRates()
   {
      m_writer.writeTable("RSRCRATE", RESOURCE_RATE_COLUMNS);
      getSortedResourceStream().forEach(r -> writeCostRateTableEntries(RESOURCE_RATE_COLUMNS, r));
   }

   /**
    * Write cost rate table entries.
    *
    * @param columns column definitions
    * @param resource parent resource
    */
   private void writeCostRateTableEntries(Map<String, ExportFunction<Map<String, Object>>> columns, Resource resource)
   {
      resource.getCostRateTable(0).stream().filter(e -> e != CostRateTableEntry.DEFAULT_ENTRY).forEach(e -> writeCostRateTableEntry(columns, resource, e));
   }

   /**
    * Write a cost rate table entry.
    *
    * @param columns column definitions
    * @param resource parent resource
    * @param entry cost rate table entry
    */
   private void writeCostRateTableEntry(Map<String, ExportFunction<Map<String, Object>>> columns, Resource resource, CostRateTableEntry entry)
   {
      Map<String, Object> map = new HashMap<>();
      map.put("object_id", m_rateObjectID.getNext());
      map.put("entity_id", resource.getUniqueID());
      map.put("cost_per_qty", entry.getRate(0));
      map.put("cost_per_qty2", entry.getRate(1));
      map.put("cost_per_qty3", entry.getRate(2));
      map.put("cost_per_qty4", entry.getRate(3));
      map.put("cost_per_qty5", entry.getRate(4));
      map.put("start_date", entry.getStartDate());
      map.put("max_qty_per_hr", getMaxQuantityPerHour(resource, entry));
      map.put("shift_period_id", entry.getShiftPeriod() == null ? null : entry.getShiftPeriod().getUniqueID());

      m_writer.writeRecord(columns, map);
   }

   /**
    * Write resources.
    */
   private void writeResources()
   {
      m_writer.writeTable("RSRC", RESOURCE_COLUMNS);
      getSortedResourceStream().forEach(r -> m_writer.writeRecord(RESOURCE_COLUMNS, r));
   }

   /**
    * Write project.
    */
   private void writeProject()
   {
      m_writer.writeTable("PROJECT", PROJECT_COLUMNS);
      m_writer.writeRecord(PROJECT_COLUMNS, m_file.getProjectProperties());
   }

   /**
    * Write calendars.
    */
   private void writeCalendars()
   {
      m_writer.writeTable("CALENDAR", CALENDAR_COLUMNS);
      m_file.getCalendars().stream().sorted(Comparator.comparing(ProjectCalendar::getUniqueID)).map(ProjectCalendarHelper::normalizeCalendar).forEach(c -> m_writer.writeRecord(CALENDAR_COLUMNS, c));
   }

   /**
    * Write schedule options.
    */
   private void writeScheduleOptions()
   {
      m_writer.writeTable("SCHEDOPTIONS", SCHEDULE_OPTIONS_COLUMNS);
      m_writer.writeRecord(SCHEDULE_OPTIONS_COLUMNS, m_file.getProjectProperties());
   }

   /**
    * Write WBS.
    */
   private void writeWBS()
   {
      m_writer.writeTable("PROJWBS", WBS_COLUMNS);
      getWbsStream().sorted(Comparator.comparing(Task::getUniqueID)).forEach(t -> m_writer.writeRecord(WBS_COLUMNS, t));
   }

   /**
    * Write activities.
    */
   private void writeActivities()
   {
      m_writer.writeTable("TASK", ACTIVITY_COLUMNS);
      getActivityStream().sorted(Comparator.comparing(Task::getUniqueID)).forEach(t -> m_writer.writeRecord(ACTIVITY_COLUMNS, t));
   }

   /**
    * Write predecessors.
    */
   private void writePredecessors()
   {
      m_writer.writeTable("TASKPRED", PREDECESSOR_COLUMNS);
      getActivityStream().map(Task::getPredecessors).flatMap(Collection::stream).sorted(Comparator.comparing(Relation::getUniqueID)).forEach(r -> m_writer.writeRecord(PREDECESSOR_COLUMNS, r));
   }

   /**
    * Write resource assignments.
    */
   private void writeResourceAssignments()
   {
      Map<String, ExportFunction<ResourceAssignment>> columns;
      if (m_projectFromPrimavera)
      {
         columns = RESOURCE_ASSIGNMENT_COLUMNS;
      }
      else
      {
         // Don't write timephased data if the schedule isn't from P6
         columns = new LinkedHashMap<>(RESOURCE_ASSIGNMENT_COLUMNS);
         columns.put("target_crv", r -> null);
         columns.put("remain_crv", r -> null);
         columns.put("actual_crv", r -> null);
      }

      m_writer.writeTable("TASKRSRC", RESOURCE_ASSIGNMENT_COLUMNS);
      getSortedResourceAssignmentStream().forEach(t -> m_writer.writeRecord(columns, t));
   }

   /**
    * Write cost accounts.
    */
   private void writeCostAccounts()
   {
      m_writer.writeTable("ACCOUNT", COST_ACCOUNT_COLUMNS);
      m_file.getCostAccounts().stream().sorted(Comparator.comparing(CostAccount::getUniqueID)).forEach(a -> m_writer.writeRecord(COST_ACCOUNT_COLUMNS, a));
   }

   /**
    * Write expense categories.
    */
   private void writeExpenseCategories()
   {
      m_writer.writeTable("COSTTYPE", EXPENSE_CATEGORY_COLUMNS);
      m_file.getExpenseCategories().stream().sorted(Comparator.comparing(ExpenseCategory::getUniqueID)).forEach(a -> m_writer.writeRecord(EXPENSE_CATEGORY_COLUMNS, a));
   }

   /**
    * Write units of measure.
    */
   private void writeUnitsOfMeasure()
   {
      m_writer.writeTable("UMEASURE", UNIT_OF_MEASURE_COLUMNS);
      m_file.getUnitsOfMeasure().stream().sorted(Comparator.comparing(UnitOfMeasure::getUniqueID)).forEach(a -> m_writer.writeRecord(UNIT_OF_MEASURE_COLUMNS, a));
   }

   /**
    * Write shifts.
    */
   private void writeShifts()
   {
      if (m_file.getShifts().isEmpty())
      {
         return;
      }

      m_writer.writeTable("SHIFT", SHIFT_COLUMNS);
      m_file.getShifts().stream().sorted(Comparator.comparing(Shift::getUniqueID)).forEach(l -> m_writer.writeRecord(SHIFT_COLUMNS, l));
   }

   /**
    * Write shift periods.
    */
   private void writeShiftPeriods()
   {
      if (m_file.getShiftPeriods().isEmpty())
      {
         return;
      }

      m_writer.writeTable("SHIFTPER", SHIFT_PERIOD_COLUMNS);
      m_file.getShiftPeriods().stream().sorted(Comparator.comparing(ShiftPeriod::getUniqueID)).forEach(l -> m_writer.writeRecord(SHIFT_PERIOD_COLUMNS, l));
   }

   /**
    * Write locations.
    */
   private void writeLocations()
   {
      if (m_file.getLocations().isEmpty())
      {
         return;
      }

      m_writer.writeTable("LOCATION", LOCATION_COLUMNS);
      m_file.getLocations().stream().sorted(Comparator.comparing(Location::getUniqueID)).forEach(l -> m_writer.writeRecord(LOCATION_COLUMNS, l));
   }

   /**
    * Write expense items.
    */
   private void writeExpenseItems()
   {
      m_writer.writeTable("PROJCOST", EXPENSE_ITEM_COLUMNS);
      getActivityStream().map(Task::getExpenseItems).flatMap(Collection::stream).sorted(Comparator.comparing(ExpenseItem::getUniqueID)).forEach(i -> m_writer.writeRecord(EXPENSE_ITEM_COLUMNS, i));
   }

   /**
    * Write resource curves.
    */
   private void writeResourceCurves()
   {
      m_writer.writeTable("RSRCCURVDATA", RESOURCE_CURVE_COLUMNS);
      m_file.getWorkContours().stream().filter(w -> !w.isContourManual() && !w.isContourFlat()).sorted(Comparator.comparing(WorkContour::getUniqueID)).forEach(r -> m_writer.writeRecord(RESOURCE_CURVE_COLUMNS, r));
   }

   /**
    * Write activity steps.
    */
   private void writeActivitySteps()
   {
      m_writer.writeTable("TASKPROC", ACTIVITY_STEP_COLUMNS);
      getActivityStream().map(Task::getSteps).flatMap(Collection::stream).sorted(Comparator.comparing(Step::getUniqueID)).forEach(s -> m_writer.writeRecord(ACTIVITY_STEP_COLUMNS, s));
   }

   /**
    * Write activity codes.
    */
   private void writeActivityCodes()
   {
      m_writer.writeTable("ACTVTYPE", ACTIVITY_CODE_COLUMNS);
      m_file.getActivityCodes().stream().sorted(Comparator.comparing(ActivityCode::getUniqueID)).forEach(c -> m_writer.writeRecord(ACTIVITY_CODE_COLUMNS, c));
   }

   /**
    * Write activity code values.
    */
   private void writeActivityCodeValues()
   {
      m_writer.writeTable("ACTVCODE", ACTIVITY_CODE_VALUE_COLUMNS);
      m_file.getActivityCodes().stream().map(ActivityCode::getValues).flatMap(Collection::stream).sorted(Comparator.comparing(ActivityCodeValue::getUniqueID)).forEach(v -> m_writer.writeRecord(ACTIVITY_CODE_VALUE_COLUMNS, v));
   }

   /**
    * Write activity code assignments.
    */
   private void writeActivityCodeAssignments()
   {
      m_writer.writeTable("TASKACTV", ACTIVITY_CODE_ASSIGNMENT_COLUMNS);
      getActivityStream().collect(Collectors.toMap(t -> t, Task::getActivityCodeValues, (u, v) -> u, TreeMap::new)).forEach(this::writeActivityCodeAssignments);
   }

   /**
    * Write activity code assignments for a task.
    *
    * @param task parent task
    * @param map activity code values
    */
   private void writeActivityCodeAssignments(Task task, Map<ActivityCode, ActivityCodeValue> map)
   {
      map.values().stream().sorted(Comparator.comparing(ActivityCodeValue::getUniqueID)).forEach(v -> m_writer.writeRecord(ACTIVITY_CODE_ASSIGNMENT_COLUMNS, new Pair<>(task, v)));
   }

   /**
    * Write UDF definitions.
    */
   private void writeUdfDefinitions()
   {
      m_writer.writeTable("UDFTYPE", UDF_TYPE_COLUMNS);
      m_userDefinedFields.stream().map(f -> new Pair<>(f, m_file.getCustomFields().get(f))).sorted(Comparator.comparing(p -> p.getSecond() == null ? Integer.valueOf(FieldTypeHelper.getFieldID(p.getFirst())) : p.getSecond().getUniqueID())).forEach(p -> m_writer.writeRecord(UDF_TYPE_COLUMNS, p));
   }

   /**
    * Write UDF values.
    */
   private void writeUdfValues()
   {
      List<Map<String, Object>> records = new ArrayList<>();
      records.addAll(writeActivityUdfValues());
      records.addAll(writeWbsUdfValues());
      records.addAll(writeResourceUdfValues());
      records.addAll(writeResourceAssignmentUdfValues());
      records.addAll(writeProjectUdfValues());
      records.removeIf(Objects::isNull);

      records.sort((r1, r2) -> {
         Integer id1 = (Integer) r1.get("udf_type_id");
         Integer id2 = (Integer) r2.get("udf_type_id");
         int result = id1.compareTo(id2);
         if (result == 0)
         {
            id1 = (Integer) r1.get("fk_id");
            id2 = (Integer) r2.get("fk_id");
            result = id1.compareTo(id2);
         }
         return result;
      });

      m_writer.writeTable("UDFVALUE", UDF_ASSIGNMENT_COLUMNS);
      records.forEach(r -> m_writer.writeRecord(UDF_ASSIGNMENT_COLUMNS, r));
   }

   /**
    * Write activity UDF values.
    *
    * @return list of UDF records
    */
   private List<Map<String, Object>> writeActivityUdfValues()
   {
      Set<FieldType> fields = m_userDefinedFields.stream().filter(f -> "TASK".equals(FieldTypeClassHelper.getXerFromInstance(f))).collect(Collectors.toSet());
      Integer projectID = getProjectID(m_file.getProjectProperties().getUniqueID());
      return getActivityStream().map(t -> writeUdfAssignments(fields, projectID, t.getUniqueID(), t)).flatMap(Collection::stream).collect(Collectors.toList());
   }

   /**
    * Write WBS UDF values.
    *
    * @return list of UDF records
    */
   private List<Map<String, Object>> writeWbsUdfValues()
   {
      Set<FieldType> fields = m_userDefinedFields.stream().filter(f -> "PROJWBS".equals(FieldTypeClassHelper.getXerFromInstance(f))).collect(Collectors.toSet());
      Integer projectID = getProjectID(m_file.getProjectProperties().getUniqueID());
      return getWbsStream().map(t -> writeUdfAssignments(fields, projectID, t.getUniqueID(), t)).flatMap(Collection::stream).collect(Collectors.toList());
   }

   /**
    * Write resource UDF values.
    *
    * @return list of UDF records
    */
   private List<Map<String, Object>> writeResourceUdfValues()
   {
      Set<FieldType> fields = m_userDefinedFields.stream().filter(f -> "RSRC".equals(FieldTypeClassHelper.getXerFromInstance(f))).collect(Collectors.toSet());
      return getSortedResourceStream().map(r -> writeUdfAssignments(fields, null, r.getUniqueID(), r)).flatMap(Collection::stream).collect(Collectors.toList());
   }

   /**
    * Write resource assignment UDF values.
    *
    * @return list of UDF records
    */
   private List<Map<String, Object>> writeResourceAssignmentUdfValues()
   {
      Set<FieldType> fields = m_userDefinedFields.stream().filter(f -> "TASKRSRC".equals(FieldTypeClassHelper.getXerFromInstance(f))).collect(Collectors.toSet());
      Integer projectID = getProjectID(m_file.getProjectProperties().getUniqueID());
      return getSortedResourceAssignmentStream().map(a -> writeUdfAssignments(fields, projectID, a.getUniqueID(), a)).flatMap(Collection::stream).collect(Collectors.toList());
   }

   /**
    * Write project UDF values.
    *
    * @return list of UDF records
    */
   private List<Map<String, Object>> writeProjectUdfValues()
   {
      Set<FieldType> fields = m_userDefinedFields.stream().filter(f -> "PROJECT".equals(FieldTypeClassHelper.getXerFromInstance(f))).collect(Collectors.toSet());
      Integer projectID = getProjectID(m_file.getProjectProperties().getUniqueID());
      return writeUdfAssignments(fields, projectID, projectID, m_file.getProjectProperties());
   }

   /**
    * Write UDF assignments from a FieldContainer instance.
    *
    * @param fields UDF fields to write
    * @param projectID parent project ID
    * @param entityID container unique ID
    * @param container field container
    * @return list of UDF records
    */
   private List<Map<String, Object>> writeUdfAssignments(Set<FieldType> fields, Integer projectID, Integer entityID, FieldContainer container)
   {
      return fields.stream().map(f -> writeUdfAssignment(f, projectID, entityID, container.get(f))).collect(Collectors.toList());
   }

   /**
    * Write a UDF assignment record.
    *
    * @param type field type
    * @param projectID parent project ID
    * @param entityID parent entity ID
    * @param value field value
    * @return UDF assignment record
    */
   private Map<String, Object> writeUdfAssignment(FieldType type, Integer projectID, Integer entityID, Object value)
   {
      if (value == null)
      {
         return null;
      }

      Map<String, Object> record = new HashMap<>();

      record.put("udf_type_id", getUdfTypeID(type));
      record.put("fk_id", entityID);
      record.put("proj_id", projectID);

      switch (type.getDataType())
      {
         case DURATION:
         case STRING:
         {
            record.put("udf_text", value.toString());
            break;
         }

         case CURRENCY:
         case INTEGER:
         case SHORT:
         case NUMERIC:
         {
            record.put("udf_number", value);
            break;
         }

         case BINARY:
         {
            // Ignore binary values
            break;
         }

         case DATE:
         {
            record.put("udf_date", value);
            break;
         }

         case BOOLEAN:
         {
            record.put("udf_number", (BooleanHelper.getBoolean((Boolean) value) ? Integer.valueOf(1) : Integer.valueOf(0)));
            break;
         }

         default:
         {
            throw new RuntimeException("Unconvertible data type: " + type.getDataType());
         }
      }

      return record;
   }

   /**
    * Write note topics.
    */
   private void writeNoteTypes()
   {
      m_writer.writeTable("MEMOTYPE", NOTE_TYPE_COLUMNS);
      m_file.getNotesTopics().stream().sorted(Comparator.comparing(NotesTopic::getUniqueID)).forEach(n -> m_writer.writeRecord(NOTE_TYPE_COLUMNS, n));
   }

   /**
    * Write WBS notes.
    */
   private void writeWbsNotes()
   {
      m_writer.writeTable("WBSMEMO", WBS_NOTE_COLUMNS);
      m_wbsNotes.forEach(n -> m_writer.writeRecord(WBS_NOTE_COLUMNS, n));
   }

   /**
    * Write activity notes.
    */
   private void writeActivityNotes()
   {
      m_writer.writeTable("TASKMEMO", ACTIVITY_NOTE_COLUMNS);
      m_activityNotes.forEach(n -> m_writer.writeRecord(ACTIVITY_NOTE_COLUMNS, n));
   }

   /**
    * Write all resource role assignments.
    */
   private void writeRoleAssignments()
   {
      List<Map<String, Object>> assignments = getSortedResourceStream().flatMap(r -> getRoleAssignments(r).stream()).collect(Collectors.toList());
      if (assignments.isEmpty())
      {
         return;
      }

      m_writer.writeTable("RSRCROLE", ROLE_ASSIGNMENT_COLUMNS);
      assignments.forEach(a -> m_writer.writeRecord(ROLE_ASSIGNMENT_COLUMNS, a));
   }

   /**
    * Retrieve the role assignment data for a single resource.
    *
    * @param resource resource
    * @return list of maps containing role assignment data
    */
   private List<Map<String, Object>> getRoleAssignments(Resource resource)
   {
      if (resource.getRoleAssignments().isEmpty())
      {
         return Collections.emptyList();
      }

      List<Map<String, Object>> result = new ArrayList<>();
      Integer resourceUniqueID = resource.getUniqueID();
      String resourceShortName = WriterHelper.getResourceID(resource);
      String resourceName = StringHelper.stripControlCharacters(resource.getName());
      ResourceType resourceType = resource.getType();
      Integer resourcePrimaryRoleID = resource.getPrimaryRoleUniqueID();

      for (Map.Entry<Resource, SkillLevel> entry : resource.getRoleAssignments().entrySet().stream().sorted(Comparator.comparing(e -> e.getKey().getUniqueID())).collect(Collectors.toList()))
      {
         Map<String, Object> map = new HashMap<>();
         Resource role = entry.getKey();

         map.put("rsrc_id", resourceUniqueID);
         map.put("role_id", role.getUniqueID());
         map.put("skill_level", entry.getValue());
         map.put("role_short_name", WriterHelper.getRoleID(role));
         map.put("role_name", StringHelper.stripControlCharacters(role.getName()));
         map.put("rsrc_short_name", resourceShortName);
         map.put("rsrc_name", resourceName);
         map.put("rsrc_type", resourceType);
         map.put("rsrc_role_id", resourcePrimaryRoleID);

         result.add(map);
      }

      return result;
   }

   /**
    * Write project codes.
    */
   private void writeProjectCodes()
   {
      if (m_file.getProjectCodes().isEmpty())
      {
         return;
      }

      m_writer.writeTable("PCATTYPE", PROJECT_CODE_COLUMNS);
      m_file.getProjectCodes().stream().sorted(Comparator.comparing(ProjectCode::getUniqueID)).forEach(c -> m_writer.writeRecord(PROJECT_CODE_COLUMNS, c));
   }

   /**
    * Write project code values.
    */
   private void writeProjectCodeValues()
   {
      if (m_file.getProjectCodes().isEmpty())
      {
         return;
      }

      m_writer.writeTable("PCATVAL", PROJECT_CODE_VALUE_COLUMNS);
      m_file.getProjectCodes().stream().map(ProjectCode::getValues).flatMap(Collection::stream).sorted(Comparator.comparing(ProjectCodeValue::getUniqueID)).forEach(v -> m_writer.writeRecord(PROJECT_CODE_VALUE_COLUMNS, v));
   }

   /**
    * Write project code assignments.
    */
   private void writeProjectCodeAssignments()
   {
      Map<ProjectCode, ProjectCodeValue> assignments = m_file.getProjectProperties().getProjectCodeValues();
      if (assignments.isEmpty())
      {
         return;
      }

      m_writer.writeTable("PROJPCAT", PROJECT_CODE_ASSIGNMENT_COLUMNS);
      Integer projectID = getProjectID(m_file.getProjectProperties().getUniqueID());
      assignments.values().stream().sorted(Comparator.comparing(ProjectCodeValue::getParentCodeUniqueID)).map(v -> populateProjectCodeAssignment(projectID, v)).forEach(a -> m_writer.writeRecord(PROJECT_CODE_ASSIGNMENT_COLUMNS, a));
   }

   /**
    * Populate a map representing project code assignment record.
    *
    * @param projectID project ID
    * @param value project code value
    * @return map of fields
    */
   private Map<String, Object> populateProjectCodeAssignment(Integer projectID, ProjectCodeValue value)
   {
      Map<String, Object> map = new HashMap<>();
      map.put("proj_id", projectID);
      map.put("proj_catg_type_id", value.getParentCodeUniqueID());
      map.put("proj_catg_id", value.getUniqueID());
      return map;
   }

   /**
    * Write resource codes.
    */
   private void writeResourceCodes()
   {
      if (m_file.getResourceCodes().isEmpty())
      {
         return;
      }

      m_writer.writeTable("RCATTYPE", RESOURCE_CODE_COLUMNS);
      m_file.getResourceCodes().stream().sorted(Comparator.comparing(ResourceCode::getUniqueID)).forEach(c -> m_writer.writeRecord(RESOURCE_CODE_COLUMNS, c));
   }

   /**
    * Write resource code values.
    */
   private void writeResourceCodeValues()
   {
      if (m_file.getResourceCodes().isEmpty())
      {
         return;
      }

      m_writer.writeTable("RCATVAL", RESOURCE_CODE_VALUE_COLUMNS);
      m_file.getResourceCodes().stream().map(ResourceCode::getValues).flatMap(Collection::stream).sorted(Comparator.comparing(ResourceCodeValue::getUniqueID)).forEach(v -> m_writer.writeRecord(RESOURCE_CODE_VALUE_COLUMNS, v));
   }

   /**
    * Write resource code assignments.
    */
   private void writeResourceCodeAssignments()
   {
      List<Map<String, Object>> assignments = getSortedResourceStream()
         .map(r -> r.getResourceCodeValues().values().stream().sorted(Comparator.comparing(ResourceCodeValue::getParentCodeUniqueID))
            .map(v -> populateResourceCodeAssignment(r.getUniqueID(), v)).collect(Collectors.toList()))
         .flatMap(Collection::stream).collect(Collectors.toList());

      if (assignments.isEmpty())
      {
         return;
      }

      m_writer.writeTable("RSRCRCAT", RESOURCE_CODE_ASSIGNMENT_COLUMNS);
      assignments.forEach(a -> m_writer.writeRecord(RESOURCE_CODE_ASSIGNMENT_COLUMNS, a));
   }

   /**
    * Write role codes.
    */
   private void writeRoleCodes()
   {
      if (m_file.getRoleCodes().isEmpty())
      {
         return;
      }

      m_writer.writeTable("ROLECATTYPE", ROLE_CODE_COLUMNS);
      m_file.getRoleCodes().stream().sorted(Comparator.comparing(RoleCode::getUniqueID)).forEach(c -> m_writer.writeRecord(ROLE_CODE_COLUMNS, c));
   }

   /**
    * Write role code values.
    */
   private void writeRoleCodeValues()
   {
      if (m_file.getRoleCodes().isEmpty())
      {
         return;
      }

      m_writer.writeTable("ROLECATVAL", ROLE_CODE_VALUE_COLUMNS);
      m_file.getRoleCodes().stream().map(RoleCode::getValues).flatMap(Collection::stream).sorted(Comparator.comparing(RoleCodeValue::getUniqueID)).forEach(v -> m_writer.writeRecord(ROLE_CODE_VALUE_COLUMNS, v));
   }

   /**
    * Write role code assignments.
    */
   private void writeRoleCodeAssignments()
   {
      List<Map<String, Object>> assignments = getSortedRoleStream()
         .map(r -> r.getRoleCodeValues().values().stream().sorted(Comparator.comparing(RoleCodeValue::getParentCodeUniqueID))
            .map(v -> populateRoleCodeAssignment(r.getUniqueID(), v)).collect(Collectors.toList()))
         .flatMap(Collection::stream).collect(Collectors.toList());

      if (assignments.isEmpty())
      {
         return;
      }

      m_writer.writeTable("ROLERCAT", ROLE_CODE_ASSIGNMENT_COLUMNS);
      assignments.forEach(a -> m_writer.writeRecord(ROLE_CODE_ASSIGNMENT_COLUMNS, a));
   }

   /**
    * Write resource assignment codes.
    */
   private void writeResourceAssignmentCodes()
   {
      if (m_file.getResourceAssignmentCodes().isEmpty())
      {
         return;
      }

      m_writer.writeTable("ASGNMNTCATTYPE", RESOURCE_ASSIGNMENT_CODE_COLUMNS);
      m_file.getResourceAssignmentCodes().stream().sorted(Comparator.comparing(ResourceAssignmentCode::getUniqueID)).forEach(c -> m_writer.writeRecord(RESOURCE_ASSIGNMENT_CODE_COLUMNS, c));
   }

   /**
    * Write resource assignment code values.
    */
   private void writeResourceAssignmentCodeValues()
   {
      if (m_file.getResourceAssignmentCodes().isEmpty())
      {
         return;
      }

      m_writer.writeTable("ASGNMNTCATVAL", RESOURCE_ASSIGNMENT_CODE_VALUE_COLUMNS);
      m_file.getResourceAssignmentCodes().stream().map(ResourceAssignmentCode::getValues).flatMap(Collection::stream).sorted(Comparator.comparing(ResourceAssignmentCodeValue::getUniqueID)).forEach(v -> m_writer.writeRecord(RESOURCE_ASSIGNMENT_CODE_VALUE_COLUMNS, v));
   }

   /**
    * Write resource assignment code assignments.
    */
   private void writeResourceAssignmentCodeAssignments()
   {
      List<Map<String, Object>> assignments = getSortedResourceAssignmentStream()
         .map(r -> r.getResourceAssignmentCodeValues().values().stream().sorted(Comparator.comparing(ResourceAssignmentCodeValue::getParentCodeUniqueID))
            .map(v -> populateResourceAssignmentCodeAssignment(r.getUniqueID(), v)).collect(Collectors.toList()))
         .flatMap(Collection::stream).collect(Collectors.toList());

      if (assignments.isEmpty())
      {
         return;
      }

      m_writer.writeTable("ASGNMNTACAT", RESOURCE_ASSIGNMENT_CODE_ASSIGNMENT_COLUMNS);
      assignments.forEach(a -> m_writer.writeRecord(RESOURCE_ASSIGNMENT_CODE_ASSIGNMENT_COLUMNS, a));
   }

   /**
    * Populate a map representing a resource code assignment record.
    *
    * @param resourceID resource ID
    * @param value resource code value
    * @return map of fields
    */
   private Map<String, Object> populateResourceCodeAssignment(Integer resourceID, ResourceCodeValue value)
   {
      Map<String, Object> map = new HashMap<>();
      map.put("rsrc_id", resourceID);
      map.put("rsrc_catg_type_id", value.getParentCodeUniqueID());
      map.put("rsrc_catg_id", value.getUniqueID());
      return map;
   }

   /**
    * Populate a map representing a role code assignment record.
    *
    * @param roleID resource ID
    * @param value role code value
    * @return map of fields
    */
   private Map<String, Object> populateRoleCodeAssignment(Integer roleID, RoleCodeValue value)
   {
      Map<String, Object> map = new HashMap<>();
      map.put("role_id", roleID);
      map.put("role_catg_type_id", value.getParentCodeUniqueID());
      map.put("role_catg_id", value.getUniqueID());
      return map;
   }

   /**
    * Populate a map representing a resource assignment code assignment record.
    *
    * @param resourceAssignmentID resource assignment ID
    * @param value resource assignment code value
    * @return map of fields
    */
   private Map<String, Object> populateResourceAssignmentCodeAssignment(Integer resourceAssignmentID, ResourceAssignmentCodeValue value)
   {
      Map<String, Object> map = new HashMap<>();
      map.put("taskrsrc_id", resourceAssignmentID);
      map.put("asgnmnt_catg_type_id", value.getParentCodeUniqueID());
      map.put("asgnmnt_catg_id", value.getUniqueID());
      map.put("proj_id", getProjectID(m_file.getProjectProperties().getUniqueID()));
      return map;
   }

   /**
    * Create notes records for all WBS.
    */
   private void populateWbsNotes()
   {
      m_wbsNotes = populateNotes(getWbsStream());
   }

   /**
    * Create notes records for all activities.
    */
   private void populateActivityNotes()
   {
      m_activityNotes = populateNotes(getActivityStream());
   }

   /**
    * Create notes records from a stream of tasks.
    *
    * @param stream tasks
    * @return notes records
    */
   private List<Map<String, Object>> populateNotes(Stream<Task> stream)
   {
      Map<Task, List<List<Notes>>> nestedList = stream.collect(Collectors.groupingBy(t -> t, LinkedHashMap::new, Collectors.mapping(t -> expandParentNotes(t.getNotesObject()), Collectors.toList())));
      Map<Task, List<StructuredNotes>> flatList = nestedList.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().stream().flatMap(Collection::stream).map(this::createStructuredNotes).collect(Collectors.toList())));
      return flatList.entrySet().stream().map(e -> e.getValue().stream().map(n -> createNotesMap(e.getKey(), n)).collect(Collectors.toList())).flatMap(Collection::stream).sorted(Comparator.comparing(n -> (Integer) n.get("entity_memo_id"))).collect(Collectors.toList());
   }

   /**
    * Expand a ParentNotes instance into a list of notes.
    *
    * @param notes Notes instance
    * @return list of notes
    */
   private List<Notes> expandParentNotes(Notes notes)
   {
      if (notes == null)
      {
         return Collections.emptyList();
      }

      if (notes instanceof ParentNotes)
      {
         return ((ParentNotes) notes).getChildNotes();
      }

      return Collections.singletonList(notes);
   }

   /**
    * Convert a "flat" Notes instance into structured notes.
    *
    * @param notes Notes instance
    * @return StructuredNotes instance
    */
   private StructuredNotes createStructuredNotes(Notes notes)
   {
      if (notes instanceof StructuredNotes)
      {
         return (StructuredNotes) notes;
      }

      return new StructuredNotes(m_file, null, m_file.getNotesTopics().getDefaultTopic(), notes);
   }

   /**
    * Create a notes record.
    *
    * @param task parent task
    * @param notes notes data
    * @return notes record
    */
   private Map<String, Object> createNotesMap(Task task, StructuredNotes notes)
   {
      Map<String, Object> map = new HashMap<>();
      map.put("entity_memo_id", notes.getUniqueID());
      map.put("proj_id", getProjectID(task.getParentFile().getProjectProperties().getUniqueID()));
      map.put("memo_type_id", notes.getTopicID());
      map.put("entity_id", task.getUniqueID());
      map.put("entity_memo", notes.getNotes());
      return map;
   }

   /**
    * Retrieve a MaxUnits instance for the max quantity per hour field.
    *
    * @param resource parent resource
    * @param entry cost rate table entry
    * @return MaxUnits instance
    */
   private MaxUnits getMaxQuantityPerHour(Resource resource, CostRateTableEntry entry)
   {
      Availability availability = resource.getAvailability().getEntryByDate(entry.getStartDate());
      return availability == null ? MaxUnits.ZERO : new MaxUnits(availability.getUnits());
   }

   /**
    * P6 expects XER files to have a single root WBS entry. If we have more
    * than one WBS entry at the top level we'll temporarily create a parent entry
    * to keep P6 happy.
    */
   private void createValidWbsHierarchy()
   {
      List<Task> wbsWithoutParent = getWbsStream().filter(t -> t.getParentTask() == null).collect(Collectors.toList());
      if (wbsWithoutParent.size() < 2)
      {
         return;
      }

      TaskContainer tasks = m_file.getTasks();
      ProjectProperties projectProperties = m_file.getProjectProperties();

      // Try to assign a unique ID before the other WBS entries if possible
      Integer uniqueID = tasks.stream().map(Task::getUniqueID).min(Comparator.naturalOrder()).orElse(null);
      if (uniqueID == null || uniqueID.intValue() <= 1)
      {
         uniqueID = m_file.getUniqueIdObjectSequence(Task.class).getNext();
      }
      else
      {
         uniqueID = Integer.valueOf(uniqueID.intValue() - 1);
      }

      String name = projectProperties.getName();
      if (name == null || name.isEmpty())
      {
         name = projectProperties.getProjectTitle();
      }

      m_originalOutlineLevel = wbsWithoutParent.get(0).getOutlineLevel();

      m_temporaryRootWbs = m_file.addTask();
      m_temporaryRootWbs.setUniqueID(uniqueID);
      m_temporaryRootWbs.setName(StringHelper.stripControlCharacters(name));
      m_temporaryRootWbs.setSequenceNumber(Integer.valueOf(0));
      m_temporaryRootWbs.setWBS(getProjectShortName(projectProperties));

      m_file.getTasks().stream().filter(t -> t != m_temporaryRootWbs && t.getParentTask() == null).forEach(t -> m_temporaryRootWbs.addChildTask(t));
   }

   /**
    * Once we're done exporting, if we've created a temporary top level WBS
    * entry, we'll remove it to ensure the data is unchanged.
    */
   private void revertWbsHierarchyChange()
   {
      if (m_temporaryRootWbs == null)
      {
         return;
      }

      List<Task> childTasks = new ArrayList<>(m_temporaryRootWbs.getChildTasks());
      for (Task task : childTasks)
      {
         m_temporaryRootWbs.removeChildTask(task);
         task.setOutlineLevel(m_originalOutlineLevel);
      }

      m_file.removeTask(m_temporaryRootWbs);
   }

   /**
    * Retrieve a stream of tasks representing activities.
    *
    * @return tasks representing activities
    */
   private Stream<Task> getActivityStream()
   {
      return m_file.getTasks().stream().filter(t -> !t.getSummary() && !t.getNull());
   }

   /**
    * Retrieve a stream of tasks representing WBS entries.
    *
    * @return tasks representing WBS entries
    */
   private Stream<Task> getWbsStream()
   {
      return m_file.getTasks().stream().filter(Task::getSummary);
   }

   /**
    * Retrieve a stream of resources.
    *
    * @return resource stream
    */
   private Stream<Resource> getSortedResourceStream()
   {
      return m_file.getResources().stream().filter(r -> !r.getRole() && r.getUniqueID().intValue() != 0).sorted(Comparator.comparing(Resource::getUniqueID));
   }

   /**
    * Retrieve a stream of roles.
    *
    * @return role stream
    */
   private Stream<Resource> getSortedRoleStream()
   {
      return m_file.getResources().stream().filter(r -> r.getRole() && r.getUniqueID().intValue() != 0).sorted(Comparator.comparing(Resource::getUniqueID));
   }

   /**
    * Retrieve a stream of resource assignments.
    *
    * @return resource assignment stream
    */
   private Stream<ResourceAssignment> getSortedResourceAssignmentStream()
   {
      return m_file.getResourceAssignments().stream().filter(WriterHelper::isValidAssignment).sorted(Comparator.comparing(ResourceAssignment::getUniqueID));
   }

   /**
    * Retrieves the "base" currency (expected to have unique ID 1), or if this is not
    * present returns the default currency constant.
    *
    * @return Currency instance
    */
   private Currency getDefaultCurrency()
   {
      Currency currency = m_file.getCurrencies().getByUniqueID(Integer.valueOf(1));
      if (currency == null)
      {
         return DEFAULT_CURRENCY;
      }
      return currency;
   }

   /**
    * Calculate actual regular work for a resource assignment.
    *
    * @param assignment resource assignment
    * @return actual regular work
    */
   private static Duration getActualRegularWork(ResourceAssignment assignment)
   {
      ProjectProperties properties = assignment.getParentFile().getProjectProperties();
      Duration actualWork = assignment.getActualWork() == null ? Duration.getInstance(0, TimeUnit.HOURS) : assignment.getActualWork().convertUnits(TimeUnit.HOURS, properties);
      Duration actualOvertimeWork = assignment.getActualOvertimeWork() == null ? Duration.getInstance(0, TimeUnit.HOURS) : assignment.getActualOvertimeWork().convertUnits(TimeUnit.HOURS, properties);
      return Duration.getInstance(actualWork.getDuration() - actualOvertimeWork.getDuration(), TimeUnit.HOURS);
   }

   /**
    * Calculate actual regular cost for a resource assignment.
    *
    * @param assignment resource assignment
    * @return actual regular cost
    */
   private static Double getActualRegularCost(ResourceAssignment assignment)
   {
      double actualCost = NumberHelper.getDouble(assignment.getActualCost());
      double actualOvertimeCost = NumberHelper.getDouble(assignment.getActualOvertimeCost());
      return Double.valueOf(actualCost - actualOvertimeCost);
   }

   /**
    * Determine the UDF Type unique ID.
    *
    * @param type UDF field type
    * @return unique ID
    */
   private static Integer getUdfTypeID(FieldType type)
   {
      return type instanceof UserDefinedField ? ((UserDefinedField) type).getUniqueID() : Integer.valueOf(FieldTypeHelper.getFieldID(type));
   }

   /**
    * Determine the UDF type name.
    *
    * @param type UDF field type
    * @return UDF type name
    */
   private static String getUdfTypeName(FieldType type)
   {
      if (type instanceof UserDefinedField)
      {
         return type.name();
      }

      return "user_field_" + getUdfTypeID(type);
   }

   /**
    * Determine the UDF type label.
    *
    * @param type UDF field type
    * @param field custom field for the field type
    * @return label text
    */
   private static String getUdfTypeLabel(FieldType type, CustomField field)
   {
      return field != null && field.getAlias() != null && !field.getAlias().isEmpty() ? field.getAlias() : type.getName();
   }

   /**
    * Determine if a location is a city.
    *
    * @param location location
    * @return true if location is a city
    */
   private static boolean locationIsCity(Location location)
   {
      return location.getCity() != null && !location.getCity().isEmpty() &&
               location.getState() != null && !location.getState().isEmpty() &&
               location.getStateCode() != null && !location.getStateCode().isEmpty() &&
               location.getCountry() != null && !location.getCountry().isEmpty() &&
               location.getCountryCode() != null && !location.getCountryCode().isEmpty();
   }

   private static Integer getProjectID(Integer id)
   {
      return id == null ? DEFAULT_PROJECT_ID : id;
   }

   private static ActivityType getActivityType(Task task)
   {
      ActivityType type = task.getActivityType();
      return type == null ? ActivityTypeHelper.EXISTING_ACTIVITY_DEFAULT_TYPE : type;
   }

   private static String getProjectShortName(ProjectProperties props)
   {
      String shortName = props.getProjectID();
      if (shortName == null || shortName.isEmpty())
      {
         shortName = "PROJECT";
      }
      return shortName;
   }

   private static Integer getSequenceNumber(Task task)
   {
      Integer sequenceNumber = task.getSequenceNumber();
      return sequenceNumber == null ? task.getID() : sequenceNumber;
   }

   private static Integer getSequenceNumber(Resource resource)
   {
      Integer sequenceNumber = resource.getSequenceNumber();
      return sequenceNumber == null ? resource.getID() : sequenceNumber;
   }

   private static PercentCompleteType getPercentCompleteType(Task task)
   {
      PercentCompleteType type = task.getPercentCompleteType();
      return type == null ? PercentCompleteType.DURATION : type;
   }

   private Charset m_charset = CharsetHelper.CP1252;
   private ProjectFile m_file;
   private XerWriter m_writer;
   private ObjectSequence m_rateObjectID;
   private List<Map<String, Object>> m_wbsNotes;
   private List<Map<String, Object>> m_activityNotes;
   private Set<FieldType> m_userDefinedFields;
   private Task m_temporaryRootWbs;
   private Integer m_originalOutlineLevel;
   private boolean m_projectFromPrimavera;

   private static final Integer DEFAULT_PROJECT_ID = Integer.valueOf(1);

   interface ExportFunction<T>
   {
      Object apply(T source);
   }

   private static final Currency DEFAULT_CURRENCY = new Currency.Builder(null)
      .uniqueID(Integer.valueOf(1))
      .numberOfDecimalPlaces(Integer.valueOf(2))
      .symbol("$")
      .decimalSymbol(".")
      .digitGroupingSymbol(",")
      .positiveCurrencyFormat("#1.1")
      .negativeCurrencyFormat("(#1.1)")
      .name("US Dollar")
      .currencyID("USD")
      .exchangeRate(Double.valueOf(1.0))
      .build();

   private static final Map<String, ExportFunction<Currency>> CURRENCY_COLUMNS = new LinkedHashMap<>();
   static
   {
      CURRENCY_COLUMNS.put("curr_id", Currency::getUniqueID);
      CURRENCY_COLUMNS.put("decimal_digit_cnt", Currency::getNumberOfDecimalPlaces);
      CURRENCY_COLUMNS.put("curr_symbol", Currency::getSymbol);
      CURRENCY_COLUMNS.put("decimal_symbol", Currency::getDecimalSymbol);
      CURRENCY_COLUMNS.put("digit_group_symbol", Currency::getDigitGroupingSymbol);
      CURRENCY_COLUMNS.put("pos_curr_fmt_type", Currency::getPositiveCurrencyFormat);
      CURRENCY_COLUMNS.put("neg_curr_fmt_type", Currency::getNegativeCurrencyFormat);
      CURRENCY_COLUMNS.put("curr_type", Currency::getName);
      CURRENCY_COLUMNS.put("curr_short_name", Currency::getCurrencyID);
      CURRENCY_COLUMNS.put("group_digit_cnt", c -> "3");
      CURRENCY_COLUMNS.put("base_exch_rate", Currency::getExchangeRate);
   }

   private static final Map<String, ExportFunction<Resource>> ROLE_COLUMNS = new LinkedHashMap<>();
   static
   {
      ROLE_COLUMNS.put("role_id", Resource::getUniqueID);
      ROLE_COLUMNS.put("parent_role_id", Resource::getParentResourceUniqueID);
      ROLE_COLUMNS.put("seq_num", Resource::getSequenceNumber);
      ROLE_COLUMNS.put("role_name", r -> StringHelper.stripControlCharacters(r.getName()));
      ROLE_COLUMNS.put("role_short_name", WriterHelper::getRoleID);
      ROLE_COLUMNS.put("pobs_id", r -> "");
      ROLE_COLUMNS.put("def_cost_qty_link_flag", r -> Boolean.valueOf(r.getCalculateCostsFromUnits()));
      ROLE_COLUMNS.put("cost_qty_type", r -> "QT_Hour");
      ROLE_COLUMNS.put("role_descr", Resource::getNotesObject);
      ROLE_COLUMNS.put("last_checksum", r -> "");
   }

   private static final Map<String, ExportFunction<Map<String, Object>>> ROLE_RATE_COLUMNS = new LinkedHashMap<>();
   static
   {
      ROLE_RATE_COLUMNS.put("role_rate_id", m -> m.get("object_id"));
      ROLE_RATE_COLUMNS.put("role_id", m -> m.get("entity_id"));
      ROLE_RATE_COLUMNS.put("cost_per_qty", m -> m.get("cost_per_qty"));
      ROLE_RATE_COLUMNS.put("cost_per_qty2", m -> m.get("cost_per_qty2"));
      ROLE_RATE_COLUMNS.put("cost_per_qty3", m -> m.get("cost_per_qty3"));
      ROLE_RATE_COLUMNS.put("cost_per_qty4", m -> m.get("cost_per_qty4"));
      ROLE_RATE_COLUMNS.put("cost_per_qty5", m -> m.get("cost_per_qty5"));
      ROLE_RATE_COLUMNS.put("start_date", m -> m.get("start_date"));
      ROLE_RATE_COLUMNS.put("max_qty_per_hr", m -> m.get("max_qty_per_hr"));
   }

   private static final Map<String, ExportFunction<Map<String, Object>>> RESOURCE_RATE_COLUMNS = new LinkedHashMap<>();
   static
   {
      RESOURCE_RATE_COLUMNS.put("rsrc_rate_id", m -> m.get("object_id"));
      RESOURCE_RATE_COLUMNS.put("rsrc_id", m -> m.get("entity_id"));
      RESOURCE_RATE_COLUMNS.put("max_qty_per_hr", m -> m.get("max_qty_per_hr"));
      RESOURCE_RATE_COLUMNS.put("cost_per_qty", m -> m.get("cost_per_qty"));
      RESOURCE_RATE_COLUMNS.put("start_date", m -> m.get("start_date"));
      RESOURCE_RATE_COLUMNS.put("shift_period_id", m -> m.get("shift_period_id"));
      RESOURCE_RATE_COLUMNS.put("cost_per_qty2", m -> m.get("cost_per_qty2"));
      RESOURCE_RATE_COLUMNS.put("cost_per_qty3", m -> m.get("cost_per_qty3"));
      RESOURCE_RATE_COLUMNS.put("cost_per_qty4", m -> m.get("cost_per_qty4"));
      RESOURCE_RATE_COLUMNS.put("cost_per_qty5", m -> m.get("cost_per_qty5"));
   }

   private static final Map<String, ExportFunction<Resource>> RESOURCE_COLUMNS = new LinkedHashMap<>();
   static
   {
      RESOURCE_COLUMNS.put("rsrc_id", Resource::getUniqueID);
      RESOURCE_COLUMNS.put("parent_rsrc_id", Resource::getParentResourceUniqueID);
      RESOURCE_COLUMNS.put("clndr_id", Resource::getCalendarUniqueID);
      RESOURCE_COLUMNS.put("role_id", Resource::getPrimaryRoleUniqueID);
      RESOURCE_COLUMNS.put("shift_id", Resource::getShiftUniqueID);
      RESOURCE_COLUMNS.put("user_id", r -> "");
      RESOURCE_COLUMNS.put("pobs_id", r -> "");
      RESOURCE_COLUMNS.put("guid", Resource::getGUID);
      RESOURCE_COLUMNS.put("rsrc_seq_num", PrimaveraXERFileWriter::getSequenceNumber);
      RESOURCE_COLUMNS.put("email_addr", Resource::getEmailAddress);
      RESOURCE_COLUMNS.put("employee_code", Resource::getCode);
      RESOURCE_COLUMNS.put("office_phone", r -> "");
      RESOURCE_COLUMNS.put("other_phone", r -> "");
      RESOURCE_COLUMNS.put("rsrc_name", r -> StringHelper.stripControlCharacters(r.getName()));
      RESOURCE_COLUMNS.put("rsrc_short_name", WriterHelper::getResourceID);
      RESOURCE_COLUMNS.put("rsrc_title_name", r -> "");
      RESOURCE_COLUMNS.put("def_qty_per_hr", r -> r.getDefaultUnits() == null || r.getDefaultUnits().doubleValue() == 0.0 ? null : Double.valueOf(r.getDefaultUnits().doubleValue() / 100.0));
      RESOURCE_COLUMNS.put("cost_qty_type", r -> "QT_Hour");
      RESOURCE_COLUMNS.put("ot_factor", r -> "");
      RESOURCE_COLUMNS.put("active_flag", r -> Boolean.valueOf(r.getActive()));
      RESOURCE_COLUMNS.put("auto_compute_act_flag", r -> Boolean.TRUE);
      RESOURCE_COLUMNS.put("def_cost_qty_link_flag", r -> Boolean.valueOf(r.getCalculateCostsFromUnits()));
      RESOURCE_COLUMNS.put("ot_flag", r -> Boolean.FALSE);
      RESOURCE_COLUMNS.put("curr_id", r -> r.getCurrencyUniqueID() == null ? DEFAULT_CURRENCY.getUniqueID() : r.getCurrencyUniqueID());
      RESOURCE_COLUMNS.put("unit_id", Resource::getUnitOfMeasureUniqueID);
      RESOURCE_COLUMNS.put("rsrc_type", Resource::getType);
      RESOURCE_COLUMNS.put("location_id", Resource::getLocationUniqueID);
      RESOURCE_COLUMNS.put("rsrc_notes", Resource::getNotesObject);
      RESOURCE_COLUMNS.put("load_tasks_flag", r -> "");
      RESOURCE_COLUMNS.put("level_flag", r -> "");
      RESOURCE_COLUMNS.put("last_checksum", r -> "");
   }

   private static final Map<String, ExportFunction<ProjectProperties>> PROJECT_COLUMNS = new LinkedHashMap<>();
   static
   {
      PROJECT_COLUMNS.put("proj_id", p -> getProjectID(p.getUniqueID()));
      PROJECT_COLUMNS.put("fy_start_month_num", ProjectProperties::getFiscalYearStartMonth);
      PROJECT_COLUMNS.put("rsrc_self_add_flag", p -> Boolean.TRUE);
      PROJECT_COLUMNS.put("allow_complete_flag", p -> Boolean.TRUE);
      PROJECT_COLUMNS.put("rsrc_multi_assign_flag", p -> Boolean.TRUE);
      PROJECT_COLUMNS.put("checkout_flag", p -> Boolean.FALSE);
      PROJECT_COLUMNS.put("project_flag", p -> Boolean.TRUE);
      PROJECT_COLUMNS.put("step_complete_flag", p -> Boolean.TRUE);
      PROJECT_COLUMNS.put("cost_qty_recalc_flag", p -> Boolean.TRUE);
      PROJECT_COLUMNS.put("batch_sum_flag", p -> Boolean.TRUE);
      PROJECT_COLUMNS.put("name_sep_char", ProjectProperties::getWbsCodeSeparator);
      PROJECT_COLUMNS.put("def_complete_pct_type", p -> PercentCompleteType.DURATION);
      PROJECT_COLUMNS.put("proj_short_name", PrimaveraXERFileWriter::getProjectShortName);
      PROJECT_COLUMNS.put("acct_id", p -> "");
      PROJECT_COLUMNS.put("orig_proj_id", p -> "");
      PROJECT_COLUMNS.put("source_proj_id", p -> "");
      PROJECT_COLUMNS.put("base_type_id", p -> "");
      PROJECT_COLUMNS.put("clndr_id", ProjectProperties::getDefaultCalendarUniqueID);
      PROJECT_COLUMNS.put("sum_base_proj_id", ProjectProperties::getBaselineProjectUniqueID);
      PROJECT_COLUMNS.put("task_code_base", ProjectProperties::getActivityIdSuffix);
      PROJECT_COLUMNS.put("task_code_step", ProjectProperties::getActivityIdIncrement);
      PROJECT_COLUMNS.put("priority_num", p -> Integer.valueOf(10));
      PROJECT_COLUMNS.put("wbs_max_sum_level", p -> Integer.valueOf(0));
      PROJECT_COLUMNS.put("strgy_priority_num", p -> Integer.valueOf(100));
      PROJECT_COLUMNS.put("last_checksum", p -> "");
      PROJECT_COLUMNS.put("critical_drtn_hr_cnt", p -> Double.valueOf(p.getCriticalSlackLimit().convertUnits(TimeUnit.HOURS, p).getDuration()));
      PROJECT_COLUMNS.put("def_cost_per_qty", p -> new CurrencyValue(Double.valueOf(100.0)));
      PROJECT_COLUMNS.put("last_recalc_date", ProjectProperties::getStatusDate);
      PROJECT_COLUMNS.put("plan_start_date", WriterHelper::getProjectPlannedStart);
      PROJECT_COLUMNS.put("plan_end_date", ProjectProperties::getMustFinishBy);
      PROJECT_COLUMNS.put("scd_end_date", ProjectProperties::getScheduledFinish);
      PROJECT_COLUMNS.put("add_date", ProjectProperties::getCreationDate);
      PROJECT_COLUMNS.put("last_tasksum_date", p -> "");
      PROJECT_COLUMNS.put("fcst_start_date", p -> "");
      PROJECT_COLUMNS.put("def_duration_type", ProjectProperties::getDefaultTaskType);
      PROJECT_COLUMNS.put("task_code_prefix", ProjectProperties::getActivityIdPrefix);
      PROJECT_COLUMNS.put("guid", ProjectProperties::getGUID);
      PROJECT_COLUMNS.put("def_qty_type", p -> "QT_Hour");
      PROJECT_COLUMNS.put("add_by_name", p -> "admin");
      PROJECT_COLUMNS.put("web_local_root_path", p -> "");
      PROJECT_COLUMNS.put("proj_url", ProjectProperties::getProjectWebsiteUrl);
      PROJECT_COLUMNS.put("def_rate_type", p -> RateTypeHelper.getXerFromInstance(Integer.valueOf(0)));
      PROJECT_COLUMNS.put("add_act_remain_flag", p -> Boolean.FALSE);
      PROJECT_COLUMNS.put("act_this_per_link_flag", p -> Boolean.TRUE);
      PROJECT_COLUMNS.put("def_task_type", p -> ActivityTypeHelper.NEW_ACTIVITY_DEFAULT_TYPE);
      PROJECT_COLUMNS.put("act_pct_link_flag", p -> Boolean.FALSE);
      PROJECT_COLUMNS.put("critical_path_type", ProjectProperties::getCriticalActivityType);
      PROJECT_COLUMNS.put("task_code_prefix_flag", p -> Boolean.valueOf(p.getActivityIdIncrementBasedOnSelectedActivity()));
      PROJECT_COLUMNS.put("def_rollup_dates_flag", p -> Boolean.TRUE);
      PROJECT_COLUMNS.put("use_project_baseline_flag", p -> Boolean.TRUE);
      PROJECT_COLUMNS.put("rem_target_link_flag", p -> Boolean.TRUE);
      PROJECT_COLUMNS.put("reset_planned_flag", p -> Boolean.FALSE);
      PROJECT_COLUMNS.put("allow_neg_act_flag", p -> Boolean.FALSE);
      PROJECT_COLUMNS.put("sum_assign_level", p -> "SL_Taskrsrc");
      PROJECT_COLUMNS.put("last_fin_dates_id", p -> "");
      PROJECT_COLUMNS.put("fintmpl_id", p -> "");
      PROJECT_COLUMNS.put("last_baseline_update_date", p -> "");
      PROJECT_COLUMNS.put("cr_external_key", p -> "");
      PROJECT_COLUMNS.put("apply_actuals_date", p -> "");
      PROJECT_COLUMNS.put("location_id", ProjectProperties::getLocationUniqueID);
      PROJECT_COLUMNS.put("loaded_scope_level", p -> Integer.valueOf(7));
      PROJECT_COLUMNS.put("export_flag", p -> Boolean.valueOf(p.getExportFlag()));
      PROJECT_COLUMNS.put("new_fin_dates_id", p -> "");
      PROJECT_COLUMNS.put("baselines_to_export", p -> "");
      PROJECT_COLUMNS.put("baseline_names_to_export", p -> "");
      PROJECT_COLUMNS.put("next_data_date", p -> "");
      PROJECT_COLUMNS.put("close_period_flag", p -> "");
      PROJECT_COLUMNS.put("sum_refresh_date", p -> "");
      PROJECT_COLUMNS.put("trsrcsum_loaded", p -> "");
      PROJECT_COLUMNS.put("sumtask_loaded", p -> "");
   }

   private static final Map<String, ExportFunction<ProjectCalendar>> CALENDAR_COLUMNS = new LinkedHashMap<>();
   static
   {
      CALENDAR_COLUMNS.put("clndr_id", ProjectCalendar::getUniqueID);
      CALENDAR_COLUMNS.put("default_flag", c -> Boolean.valueOf(c.getParentFile().getProjectProperties().getDefaultCalendar() == c));
      CALENDAR_COLUMNS.put("clndr_name", c -> StringHelper.stripControlCharacters(c.getName()));
      CALENDAR_COLUMNS.put("proj_id", c -> c.getType() == CalendarType.PROJECT ? getProjectID(c.getParentFile().getProjectProperties().getUniqueID()) : null);
      CALENDAR_COLUMNS.put("base_clndr_id", ProjectCalendar::getParentUniqueID);
      CALENDAR_COLUMNS.put("last_chng_date", c -> null);
      CALENDAR_COLUMNS.put("clndr_type", ProjectCalendar::getType);
      CALENDAR_COLUMNS.put("day_hr_cnt", c -> Integer.valueOf(NumberHelper.getInt(c.getMinutesPerDay()) / 60));
      CALENDAR_COLUMNS.put("week_hr_cnt", c -> Integer.valueOf(NumberHelper.getInt(c.getMinutesPerWeek()) / 60));
      CALENDAR_COLUMNS.put("month_hr_cnt", c -> Integer.valueOf(NumberHelper.getInt(c.getMinutesPerMonth()) / 60));
      CALENDAR_COLUMNS.put("year_hr_cnt", c -> Integer.valueOf(NumberHelper.getInt(c.getMinutesPerYear()) / 60));
      CALENDAR_COLUMNS.put("rsrc_private", c -> Boolean.valueOf(c.getPersonal()));
      CALENDAR_COLUMNS.put("clndr_data", c -> new ProjectCalendarStructuredTextWriter().getCalendarData(c));
   }

   private static final Map<String, ExportFunction<Task>> WBS_COLUMNS = new LinkedHashMap<>();
   static
   {
      WBS_COLUMNS.put("wbs_id", Task::getUniqueID);
      WBS_COLUMNS.put("proj_id", t -> getProjectID(t.getParentFile().getProjectProperties().getUniqueID()));
      WBS_COLUMNS.put("obs_id", t -> "");
      WBS_COLUMNS.put("seq_num", PrimaveraXERFileWriter::getSequenceNumber);
      WBS_COLUMNS.put("est_wt", t -> Integer.valueOf(1));
      WBS_COLUMNS.put("proj_node_flag", t -> Boolean.valueOf(t.getParentTask() == null));
      WBS_COLUMNS.put("sum_data_flag", t -> Boolean.TRUE);
      WBS_COLUMNS.put("status_code", t -> "WS_Open");
      WBS_COLUMNS.put("wbs_short_name", TaskHelper::getWbsCode);
      WBS_COLUMNS.put("wbs_name", t -> StringHelper.stripControlCharacters(t.getName()));
      WBS_COLUMNS.put("phase_id", t -> "");
      WBS_COLUMNS.put("parent_wbs_id", Task::getParentTaskUniqueID);
      WBS_COLUMNS.put("ev_user_pct", t -> Integer.valueOf(6));
      WBS_COLUMNS.put("ev_etc_user_value", t -> Double.valueOf(0.88));
      WBS_COLUMNS.put("orig_cost", t -> CurrencyValue.ZERO);
      WBS_COLUMNS.put("indep_remain_total_cost", t -> CurrencyValue.ZERO);
      WBS_COLUMNS.put("ann_dscnt_rate_pct", t -> "");
      WBS_COLUMNS.put("dscnt_period_type", t -> "");
      WBS_COLUMNS.put("indep_remain_work_qty", t -> Integer.valueOf(0));
      WBS_COLUMNS.put("anticip_start_date", t -> "");
      WBS_COLUMNS.put("anticip_end_date", t -> "");
      WBS_COLUMNS.put("ev_compute_type", t -> "EC_Cmp_pct");
      WBS_COLUMNS.put("ev_etc_compute_type", t -> "EE_PF_cpi");
      WBS_COLUMNS.put("guid", Task::getGUID);
      WBS_COLUMNS.put("tmpl_guid", Task::getMethodologyGUID);
      WBS_COLUMNS.put("plan_open_state", t -> "");
   }

   private static final Map<String, ExportFunction<Task>> ACTIVITY_COLUMNS = new LinkedHashMap<>();
   static
   {
      ACTIVITY_COLUMNS.put("task_id", Task::getUniqueID);
      ACTIVITY_COLUMNS.put("proj_id", t -> getProjectID(t.getParentFile().getProjectProperties().getUniqueID()));
      ACTIVITY_COLUMNS.put("wbs_id", Task::getParentTaskUniqueID);
      ACTIVITY_COLUMNS.put("clndr_id", Task::getCalendarUniqueID);
      ACTIVITY_COLUMNS.put("phys_complete_pct", Task::getPhysicalPercentComplete);
      ACTIVITY_COLUMNS.put("rev_fdbk_flag", t -> Boolean.FALSE);
      ACTIVITY_COLUMNS.put("est_wt", t -> Integer.valueOf(1));
      ACTIVITY_COLUMNS.put("lock_plan_flag", t -> Boolean.FALSE);
      ACTIVITY_COLUMNS.put("auto_compute_act_flag", t -> Boolean.TRUE);
      ACTIVITY_COLUMNS.put("complete_pct_type", PrimaveraXERFileWriter::getPercentCompleteType);
      ACTIVITY_COLUMNS.put("task_type", PrimaveraXERFileWriter::getActivityType);
      ACTIVITY_COLUMNS.put("duration_type", Task::getType);
      ACTIVITY_COLUMNS.put("status_code", ActivityStatusHelper::getActivityStatus);
      ACTIVITY_COLUMNS.put("task_code", WriterHelper::getActivityID);
      ACTIVITY_COLUMNS.put("task_name", t -> StringHelper.stripControlCharacters(t.getName()));
      ACTIVITY_COLUMNS.put("rsrc_id", Task::getPrimaryResourceUniqueID);
      ACTIVITY_COLUMNS.put("total_float_hr_cnt", t -> t.getActivityStatus() == ActivityStatus.COMPLETED ? null : t.getTotalSlack());
      ACTIVITY_COLUMNS.put("free_float_hr_cnt", t -> t.getActivityStatus() == ActivityStatus.COMPLETED ? null : t.getFreeSlack());
      ACTIVITY_COLUMNS.put("remain_drtn_hr_cnt", Task::getRemainingDuration);
      ACTIVITY_COLUMNS.put("act_work_qty", WorkHelper::getActualWorkLabor);
      ACTIVITY_COLUMNS.put("remain_work_qty", WorkHelper::getRemainingWorkLabor);
      ACTIVITY_COLUMNS.put("target_work_qty", WorkHelper::getPlannedWorkLabor);
      ACTIVITY_COLUMNS.put("target_drtn_hr_cnt", WriterHelper::getActivityPlannedDuration);
      ACTIVITY_COLUMNS.put("target_equip_qty", t -> WorkHelper.zeroIfNull(t.getPlannedWorkNonlabor()));
      ACTIVITY_COLUMNS.put("act_equip_qty", t -> WorkHelper.zeroIfNull(t.getActualWorkNonlabor()));
      ACTIVITY_COLUMNS.put("remain_equip_qty", t -> WorkHelper.zeroIfNull(t.getRemainingWorkNonlabor()));
      ACTIVITY_COLUMNS.put("cstr_date", Task::getConstraintDate);
      ACTIVITY_COLUMNS.put("act_start_date", Task::getActualStart);
      ACTIVITY_COLUMNS.put("act_end_date", Task::getActualFinish);
      ACTIVITY_COLUMNS.put("late_start_date", Task::getLateStart);
      ACTIVITY_COLUMNS.put("late_end_date", Task::getLateFinish);
      ACTIVITY_COLUMNS.put("expect_end_date", Task::getExpectedFinish);
      ACTIVITY_COLUMNS.put("early_start_date", Task::getEarlyStart);
      ACTIVITY_COLUMNS.put("early_end_date", Task::getEarlyFinish);
      ACTIVITY_COLUMNS.put("restart_date", Task::getRemainingEarlyStart);
      ACTIVITY_COLUMNS.put("reend_date", Task::getRemainingEarlyFinish);
      ACTIVITY_COLUMNS.put("target_start_date", Task::getPlannedStart);
      ACTIVITY_COLUMNS.put("target_end_date", Task::getPlannedFinish);
      ACTIVITY_COLUMNS.put("rem_late_start_date", Task::getRemainingLateStart);
      ACTIVITY_COLUMNS.put("rem_late_end_date", Task::getRemainingLateFinish);
      ACTIVITY_COLUMNS.put("cstr_type", Task::getConstraintType);
      ACTIVITY_COLUMNS.put("priority_type", Task::getPriority);
      ACTIVITY_COLUMNS.put("suspend_date", Task::getSuspendDate);
      ACTIVITY_COLUMNS.put("resume_date", Task::getResume);
      ACTIVITY_COLUMNS.put("float_path", Task::getFloatPath);
      ACTIVITY_COLUMNS.put("float_path_order", Task::getFloatPathOrder);
      ACTIVITY_COLUMNS.put("guid", Task::getGUID);
      ACTIVITY_COLUMNS.put("tmpl_guid", Task::getMethodologyGUID);
      ACTIVITY_COLUMNS.put("cstr_date2", Task::getSecondaryConstraintDate);
      ACTIVITY_COLUMNS.put("cstr_type2", Task::getSecondaryConstraintType);
      ACTIVITY_COLUMNS.put("driving_path_flag", t -> Boolean.FALSE);
      ACTIVITY_COLUMNS.put("act_this_per_work_qty", t -> Integer.valueOf(0));
      ACTIVITY_COLUMNS.put("act_this_per_equip_qty", t -> Integer.valueOf(0));
      ACTIVITY_COLUMNS.put("external_early_start_date", Task::getExternalEarlyStart);
      ACTIVITY_COLUMNS.put("external_late_end_date", Task::getExternalLateFinish);
      ACTIVITY_COLUMNS.put("create_date", Task::getCreateDate);
      ACTIVITY_COLUMNS.put("update_date", t -> null);
      ACTIVITY_COLUMNS.put("create_user", t -> null);
      ACTIVITY_COLUMNS.put("update_user", t -> null);
      ACTIVITY_COLUMNS.put("location_id", Task::getLocationUniqueID);
   }

   private static final Map<String, ExportFunction<Relation>> PREDECESSOR_COLUMNS = new LinkedHashMap<>();
   static
   {
      PREDECESSOR_COLUMNS.put("task_pred_id", Relation::getUniqueID);
      PREDECESSOR_COLUMNS.put("task_id", r -> r.getSuccessorTask().getUniqueID());
      PREDECESSOR_COLUMNS.put("pred_task_id", r -> r.getPredecessorTask().getUniqueID());
      PREDECESSOR_COLUMNS.put("proj_id", r -> getProjectID(r.getSuccessorTask().getParentFile().getProjectProperties().getUniqueID()));
      PREDECESSOR_COLUMNS.put("pred_proj_id", r -> getProjectID(r.getPredecessorTask().getParentFile().getProjectProperties().getUniqueID()));
      PREDECESSOR_COLUMNS.put("pred_type", Relation::getType);
      PREDECESSOR_COLUMNS.put("lag_hr_cnt", Relation::getLag);
      PREDECESSOR_COLUMNS.put("comments", Relation::getNotes);
      PREDECESSOR_COLUMNS.put("float_path", r -> null);
      PREDECESSOR_COLUMNS.put("aref", r -> null);
      PREDECESSOR_COLUMNS.put("arls", r -> null);
   }

   private static final Map<String, ExportFunction<ResourceAssignment>> RESOURCE_ASSIGNMENT_COLUMNS = new LinkedHashMap<>();
   static
   {
      RESOURCE_ASSIGNMENT_COLUMNS.put("taskrsrc_id", ResourceAssignment::getUniqueID);
      RESOURCE_ASSIGNMENT_COLUMNS.put("task_id", ResourceAssignment::getTaskUniqueID);
      RESOURCE_ASSIGNMENT_COLUMNS.put("proj_id", r -> getProjectID(r.getParentFile().getProjectProperties().getUniqueID()));
      RESOURCE_ASSIGNMENT_COLUMNS.put("cost_qty_link_flag", r -> Boolean.valueOf(r.getCalculateCostsFromUnits()));
      RESOURCE_ASSIGNMENT_COLUMNS.put("role_id", ResourceAssignment::getRoleUniqueID);
      RESOURCE_ASSIGNMENT_COLUMNS.put("acct_id", ResourceAssignment::getCostAccountUniqueID);
      RESOURCE_ASSIGNMENT_COLUMNS.put("rsrc_id", ResourceAssignment::getResourceUniqueID);
      RESOURCE_ASSIGNMENT_COLUMNS.put("pobs_id", r -> null);
      RESOURCE_ASSIGNMENT_COLUMNS.put("skill_level", r -> null);
      RESOURCE_ASSIGNMENT_COLUMNS.put("remain_qty", r -> new XerUnitsHelper(r).getRemainingUnits());
      RESOURCE_ASSIGNMENT_COLUMNS.put("target_qty", r -> new XerUnitsHelper(r).getPlannedUnits());
      RESOURCE_ASSIGNMENT_COLUMNS.put("remain_qty_per_hr", r -> new XerUnitsHelper(r).getRemainingUnitsPerTime());
      RESOURCE_ASSIGNMENT_COLUMNS.put("target_lag_drtn_hr_cnt", ResourceAssignment::getDelay);
      RESOURCE_ASSIGNMENT_COLUMNS.put("target_qty_per_hr", r -> new XerUnitsHelper(r).getPlannedUnitsPerTime());
      RESOURCE_ASSIGNMENT_COLUMNS.put("act_ot_qty", ResourceAssignment::getActualOvertimeWork);
      RESOURCE_ASSIGNMENT_COLUMNS.put("act_reg_qty", PrimaveraXERFileWriter::getActualRegularWork);
      RESOURCE_ASSIGNMENT_COLUMNS.put("relag_drtn_hr_cnt", r -> null);
      RESOURCE_ASSIGNMENT_COLUMNS.put("ot_factor", r -> null);
      RESOURCE_ASSIGNMENT_COLUMNS.put("cost_per_qty", ResourceAssignment::getOverrideRate);
      RESOURCE_ASSIGNMENT_COLUMNS.put("target_cost", r -> CurrencyValue.getInstance(r.getPlannedCost()));
      RESOURCE_ASSIGNMENT_COLUMNS.put("act_reg_cost", r -> CurrencyValue.getInstance(getActualRegularCost(r)));
      RESOURCE_ASSIGNMENT_COLUMNS.put("act_ot_cost", r -> CurrencyValue.getInstance(r.getActualOvertimeCost()));
      RESOURCE_ASSIGNMENT_COLUMNS.put("remain_cost", r -> CurrencyValue.getInstance(r.getRemainingCost()));
      RESOURCE_ASSIGNMENT_COLUMNS.put("act_start_date", ResourceAssignment::getActualStart);
      RESOURCE_ASSIGNMENT_COLUMNS.put("act_end_date", ResourceAssignment::getActualFinish);
      RESOURCE_ASSIGNMENT_COLUMNS.put("restart_date", ResourceAssignment::getRemainingEarlyStart);
      RESOURCE_ASSIGNMENT_COLUMNS.put("reend_date", ResourceAssignment::getRemainingEarlyFinish);
      RESOURCE_ASSIGNMENT_COLUMNS.put("target_start_date", ResourceAssignment::getPlannedStart);
      RESOURCE_ASSIGNMENT_COLUMNS.put("target_end_date", ResourceAssignment::getPlannedFinish);
      RESOURCE_ASSIGNMENT_COLUMNS.put("rem_late_start_date", ResourceAssignment::getRemainingLateStart);
      RESOURCE_ASSIGNMENT_COLUMNS.put("rem_late_end_date", ResourceAssignment::getRemainingLateFinish);
      RESOURCE_ASSIGNMENT_COLUMNS.put("rollup_dates_flag", r -> Boolean.TRUE);
      RESOURCE_ASSIGNMENT_COLUMNS.put("target_crv", r -> TimephasedHelper.write(r.getTask().getEffectiveCalendar(), r.getTimephasedPlannedWork()));
      RESOURCE_ASSIGNMENT_COLUMNS.put("remain_crv", r -> TimephasedHelper.write(r.getTask().getEffectiveCalendar(), r.getTimephasedWork()));
      RESOURCE_ASSIGNMENT_COLUMNS.put("actual_crv", r -> TimephasedHelper.write(r.getTask().getEffectiveCalendar(), r.getTimephasedActualWork()));
      RESOURCE_ASSIGNMENT_COLUMNS.put("ts_pend_act_end_flag", r -> Boolean.FALSE);
      RESOURCE_ASSIGNMENT_COLUMNS.put("guid", ResourceAssignment::getGUID);
      RESOURCE_ASSIGNMENT_COLUMNS.put("rate_type", r -> RateTypeHelper.getXerFromInstance(r.getRateIndex()));
      RESOURCE_ASSIGNMENT_COLUMNS.put("act_this_per_cost", r -> CurrencyValue.ZERO);
      RESOURCE_ASSIGNMENT_COLUMNS.put("act_this_per_qty", r -> Integer.valueOf(0));
      RESOURCE_ASSIGNMENT_COLUMNS.put("curv_id", r -> CurveHelper.getCurveID(r.getWorkContour()));
      RESOURCE_ASSIGNMENT_COLUMNS.put("rsrc_type", r -> r.getResource() == null ? ResourceType.WORK : r.getResource().getType());
      RESOURCE_ASSIGNMENT_COLUMNS.put("cost_per_qty_source_type", ResourceAssignment::getRateSource);
      RESOURCE_ASSIGNMENT_COLUMNS.put("create_user", r -> null);
      RESOURCE_ASSIGNMENT_COLUMNS.put("create_date", ResourceAssignment::getCreateDate);
      RESOURCE_ASSIGNMENT_COLUMNS.put("has_rsrchours", r -> null);
      RESOURCE_ASSIGNMENT_COLUMNS.put("taskrsrc_sum_id", r -> null);
   }

   private static final Map<String, ExportFunction<CostAccount>> COST_ACCOUNT_COLUMNS = new LinkedHashMap<>();
   static
   {
      COST_ACCOUNT_COLUMNS.put("acct_id", CostAccount::getUniqueID);
      COST_ACCOUNT_COLUMNS.put("parent_acct_id", CostAccount::getParentUniqueID);
      COST_ACCOUNT_COLUMNS.put("acct_seq_num", CostAccount::getSequenceNumber);
      COST_ACCOUNT_COLUMNS.put("acct_name", CostAccount::getID);
      COST_ACCOUNT_COLUMNS.put("acct_short_name", c -> StringHelper.stripControlCharacters(c.getName()));
      COST_ACCOUNT_COLUMNS.put("acct_descr", CostAccount::getNotesObject);
   }

   private static final Map<String, ExportFunction<ExpenseCategory>> EXPENSE_CATEGORY_COLUMNS = new LinkedHashMap<>();
   static
   {
      EXPENSE_CATEGORY_COLUMNS.put("cost_type_id", ExpenseCategory::getUniqueID);
      EXPENSE_CATEGORY_COLUMNS.put("seq_num", ExpenseCategory::getSequenceNumber);
      EXPENSE_CATEGORY_COLUMNS.put("cost_type", e -> StringHelper.stripControlCharacters(e.getName()));
   }

   private static final Map<String, ExportFunction<Location>> LOCATION_COLUMNS = new LinkedHashMap<>();
   static
   {
      LOCATION_COLUMNS.put("location_id", Location::getUniqueID);
      LOCATION_COLUMNS.put("location_name", l -> StringHelper.stripControlCharacters(l.getName()));
      LOCATION_COLUMNS.put("location_type", l -> locationIsCity(l) ? "City" : "LT_Point");
      LOCATION_COLUMNS.put("address_line1", Location::getAddressLine1);
      LOCATION_COLUMNS.put("address_line2", Location::getAddressLine2);
      LOCATION_COLUMNS.put("address_line3", Location::getAddressLine3);
      LOCATION_COLUMNS.put("city_name", Location::getCity);
      LOCATION_COLUMNS.put("municipality_name", Location::getMunicipality);
      LOCATION_COLUMNS.put("state_name", Location::getState);
      LOCATION_COLUMNS.put("state_code", Location::getStateCode);
      LOCATION_COLUMNS.put("country_name", Location::getCountry);
      LOCATION_COLUMNS.put("country_code", Location::getCountryCode);
      LOCATION_COLUMNS.put("postal_code", Location::getPostalCode);
      LOCATION_COLUMNS.put("longitude", Location::getLongitude);
      LOCATION_COLUMNS.put("latitude", Location::getLatitude);
   }

   private static final Map<String, ExportFunction<ExpenseItem>> EXPENSE_ITEM_COLUMNS = new LinkedHashMap<>();
   static
   {
      EXPENSE_ITEM_COLUMNS.put("cost_item_id", ExpenseItem::getUniqueID);
      EXPENSE_ITEM_COLUMNS.put("acct_id", ExpenseItem::getAccountUniqueID);
      EXPENSE_ITEM_COLUMNS.put("pobs_id", i -> null);
      EXPENSE_ITEM_COLUMNS.put("cost_type_id", ExpenseItem::getCategoryUniqueID);
      EXPENSE_ITEM_COLUMNS.put("proj_id", i -> getProjectID(i.getTask().getParentFile().getProjectProperties().getUniqueID()));
      EXPENSE_ITEM_COLUMNS.put("task_id", i -> i.getTask().getUniqueID());
      EXPENSE_ITEM_COLUMNS.put("cost_name", i -> StringHelper.stripControlCharacters(i.getName()));
      EXPENSE_ITEM_COLUMNS.put("po_number", ExpenseItem::getDocumentNumber);
      EXPENSE_ITEM_COLUMNS.put("vendor_name", ExpenseItem::getVendor);
      EXPENSE_ITEM_COLUMNS.put("act_cost", i -> CurrencyValue.getInstance(i.getActualCost()));
      EXPENSE_ITEM_COLUMNS.put("cost_per_qty", i -> CurrencyValue.getInstance(i.getPricePerUnit()));
      EXPENSE_ITEM_COLUMNS.put("remain_cost", i -> CurrencyValue.getInstance(i.getRemainingCost()));
      EXPENSE_ITEM_COLUMNS.put("target_cost", i -> CurrencyValue.getInstance(i.getPlannedCost()));
      EXPENSE_ITEM_COLUMNS.put("cost_load_type", ExpenseItem::getAccrueType);
      EXPENSE_ITEM_COLUMNS.put("auto_compute_act_flag", i -> Boolean.valueOf(i.getAutoComputeActuals()));
      EXPENSE_ITEM_COLUMNS.put("target_qty", ExpenseItem::getPlannedUnits);
      EXPENSE_ITEM_COLUMNS.put("qty_name", ExpenseItem::getUnitOfMeasure);
      EXPENSE_ITEM_COLUMNS.put("cost_descr", ExpenseItem::getDescription);
      EXPENSE_ITEM_COLUMNS.put("contract_manager_import", i -> null);
   }

   private static final Map<String, ExportFunction<WorkContour>> RESOURCE_CURVE_COLUMNS = new LinkedHashMap<>();
   static
   {
      RESOURCE_CURVE_COLUMNS.put("curv_id", WorkContour::getUniqueID);
      RESOURCE_CURVE_COLUMNS.put("curv_name", r -> StringHelper.stripControlCharacters(r.getName()));
      RESOURCE_CURVE_COLUMNS.put("default_flag", r -> Boolean.valueOf(r.isContourDefault()));
      RESOURCE_CURVE_COLUMNS.put("pct_usage_0", r -> Double.valueOf(r.getCurveValues()[0]));
      RESOURCE_CURVE_COLUMNS.put("pct_usage_1", r -> Double.valueOf(r.getCurveValues()[1]));
      RESOURCE_CURVE_COLUMNS.put("pct_usage_2", r -> Double.valueOf(r.getCurveValues()[2]));
      RESOURCE_CURVE_COLUMNS.put("pct_usage_3", r -> Double.valueOf(r.getCurveValues()[3]));
      RESOURCE_CURVE_COLUMNS.put("pct_usage_4", r -> Double.valueOf(r.getCurveValues()[4]));
      RESOURCE_CURVE_COLUMNS.put("pct_usage_5", r -> Double.valueOf(r.getCurveValues()[5]));
      RESOURCE_CURVE_COLUMNS.put("pct_usage_6", r -> Double.valueOf(r.getCurveValues()[6]));
      RESOURCE_CURVE_COLUMNS.put("pct_usage_7", r -> Double.valueOf(r.getCurveValues()[7]));
      RESOURCE_CURVE_COLUMNS.put("pct_usage_8", r -> Double.valueOf(r.getCurveValues()[8]));
      RESOURCE_CURVE_COLUMNS.put("pct_usage_9", r -> Double.valueOf(r.getCurveValues()[9]));
      RESOURCE_CURVE_COLUMNS.put("pct_usage_10", r -> Double.valueOf(r.getCurveValues()[10]));
      RESOURCE_CURVE_COLUMNS.put("pct_usage_11", r -> Double.valueOf(r.getCurveValues()[11]));
      RESOURCE_CURVE_COLUMNS.put("pct_usage_12", r -> Double.valueOf(r.getCurveValues()[12]));
      RESOURCE_CURVE_COLUMNS.put("pct_usage_13", r -> Double.valueOf(r.getCurveValues()[13]));
      RESOURCE_CURVE_COLUMNS.put("pct_usage_14", r -> Double.valueOf(r.getCurveValues()[14]));
      RESOURCE_CURVE_COLUMNS.put("pct_usage_15", r -> Double.valueOf(r.getCurveValues()[15]));
      RESOURCE_CURVE_COLUMNS.put("pct_usage_16", r -> Double.valueOf(r.getCurveValues()[16]));
      RESOURCE_CURVE_COLUMNS.put("pct_usage_17", r -> Double.valueOf(r.getCurveValues()[17]));
      RESOURCE_CURVE_COLUMNS.put("pct_usage_18", r -> Double.valueOf(r.getCurveValues()[18]));
      RESOURCE_CURVE_COLUMNS.put("pct_usage_19", r -> Double.valueOf(r.getCurveValues()[19]));
      RESOURCE_CURVE_COLUMNS.put("pct_usage_20", r -> Double.valueOf(r.getCurveValues()[20]));
   }

   private static final Map<String, ExportFunction<Step>> ACTIVITY_STEP_COLUMNS = new LinkedHashMap<>();
   static
   {
      ACTIVITY_STEP_COLUMNS.put("proc_id", Step::getUniqueID);
      ACTIVITY_STEP_COLUMNS.put("task_id", s -> s.getTask().getUniqueID());
      ACTIVITY_STEP_COLUMNS.put("proj_id", s -> getProjectID(s.getTask().getParentFile().getProjectProperties().getUniqueID()));
      ACTIVITY_STEP_COLUMNS.put("seq_num", Step::getSequenceNumber);
      ACTIVITY_STEP_COLUMNS.put("proc_name", s -> StringHelper.stripControlCharacters(s.getName()));
      ACTIVITY_STEP_COLUMNS.put("complete_flag", s -> Boolean.valueOf(s.getComplete()));
      ACTIVITY_STEP_COLUMNS.put("proc_wt", Step::getWeight);
      ACTIVITY_STEP_COLUMNS.put("complete_pct", Step::getPercentComplete);
      ACTIVITY_STEP_COLUMNS.put("proc_descr", Step::getDescriptionObject);
   }

   private static final Map<String, ExportFunction<ActivityCode>> ACTIVITY_CODE_COLUMNS = new LinkedHashMap<>();
   static
   {
      ACTIVITY_CODE_COLUMNS.put("actv_code_type_id", ActivityCode::getUniqueID);
      ACTIVITY_CODE_COLUMNS.put("actv_short_len", WriterHelper::getCodeMaxLength);
      ACTIVITY_CODE_COLUMNS.put("seq_num", ActivityCode::getSequenceNumber);
      ACTIVITY_CODE_COLUMNS.put("actv_code_type", a -> StringHelper.stripControlCharacters(a.getName()));
      ACTIVITY_CODE_COLUMNS.put("proj_id", ActivityCode::getScopeProjectUniqueID);
      ACTIVITY_CODE_COLUMNS.put("wbs_id", ActivityCode::getScopeEpsUniqueID);
      ACTIVITY_CODE_COLUMNS.put("actv_code_type_scope", ActivityCode::getScope);
   }

   private static final Map<String, ExportFunction<ActivityCodeValue>> ACTIVITY_CODE_VALUE_COLUMNS = new LinkedHashMap<>();
   static
   {
      ACTIVITY_CODE_VALUE_COLUMNS.put("actv_code_id", ActivityCodeValue::getUniqueID);
      ACTIVITY_CODE_VALUE_COLUMNS.put("parent_actv_code_id", ActivityCodeValue::getParentValueUniqueID);
      ACTIVITY_CODE_VALUE_COLUMNS.put("actv_code_type_id", ActivityCodeValue::getParentCodeUniqueID);
      ACTIVITY_CODE_VALUE_COLUMNS.put("actv_code_name", a -> StringHelper.stripControlCharacters(a.getDescription()));
      ACTIVITY_CODE_VALUE_COLUMNS.put("short_name", a -> StringHelper.stripControlCharacters(a.getName()));
      ACTIVITY_CODE_VALUE_COLUMNS.put("seq_num", ActivityCodeValue::getSequenceNumber);
      ACTIVITY_CODE_VALUE_COLUMNS.put("color", ActivityCodeValue::getColor);
      ACTIVITY_CODE_VALUE_COLUMNS.put("total_assignments", a -> null);
   }

   private static final Map<String, ExportFunction<Pair<Task, ActivityCodeValue>>> ACTIVITY_CODE_ASSIGNMENT_COLUMNS = new LinkedHashMap<>();
   static
   {
      ACTIVITY_CODE_ASSIGNMENT_COLUMNS.put("task_id", p -> p.getFirst().getUniqueID());
      ACTIVITY_CODE_ASSIGNMENT_COLUMNS.put("actv_code_type_id", p -> p.getSecond().getParentCodeUniqueID());
      ACTIVITY_CODE_ASSIGNMENT_COLUMNS.put("actv_code_id", p -> p.getSecond().getUniqueID());
      ACTIVITY_CODE_ASSIGNMENT_COLUMNS.put("proj_id", p -> getProjectID(p.getFirst().getParentFile().getProjectProperties().getUniqueID()));
   }

   private static final Map<String, ExportFunction<Pair<FieldType, CustomField>>> UDF_TYPE_COLUMNS = new LinkedHashMap<>();
   static
   {
      UDF_TYPE_COLUMNS.put("udf_type_id", p -> getUdfTypeID(p.getFirst()));
      UDF_TYPE_COLUMNS.put("table_name", p -> FieldTypeClassHelper.getXerFromInstance(p.getFirst()));
      UDF_TYPE_COLUMNS.put("udf_type_name", p -> getUdfTypeName(p.getFirst()));
      UDF_TYPE_COLUMNS.put("udf_type_label", p -> getUdfTypeLabel(p.getFirst(), p.getSecond()));
      UDF_TYPE_COLUMNS.put("logical_data_type", p -> p.getFirst().getDataType());
      UDF_TYPE_COLUMNS.put("super_flag", p -> Boolean.FALSE);
      UDF_TYPE_COLUMNS.put("indicator_expression", p -> null);
      UDF_TYPE_COLUMNS.put("summary_indicator_expression", p -> null);
   }

   private static final Map<String, ExportFunction<Map<String, Object>>> UDF_ASSIGNMENT_COLUMNS = new LinkedHashMap<>();
   static
   {
      UDF_ASSIGNMENT_COLUMNS.put("udf_type_id", u -> u.get("udf_type_id"));
      UDF_ASSIGNMENT_COLUMNS.put("fk_id", u -> u.get("fk_id"));
      UDF_ASSIGNMENT_COLUMNS.put("proj_id", u -> u.get("proj_id"));
      UDF_ASSIGNMENT_COLUMNS.put("udf_date", u -> u.get("udf_date"));
      UDF_ASSIGNMENT_COLUMNS.put("udf_number", u -> u.get("udf_number"));
      UDF_ASSIGNMENT_COLUMNS.put("udf_text", u -> u.get("udf_text"));
      UDF_ASSIGNMENT_COLUMNS.put("udf_code_id", u -> u.get("udf_code_id"));
   }

   private static final Map<String, ExportFunction<NotesTopic>> NOTE_TYPE_COLUMNS = new LinkedHashMap<>();
   static
   {
      NOTE_TYPE_COLUMNS.put("memo_type_id", NotesTopic::getUniqueID);
      NOTE_TYPE_COLUMNS.put("seq_num", NotesTopic::getSequenceNumber);
      NOTE_TYPE_COLUMNS.put("eps_flag", n -> Boolean.valueOf(n.getAvailableForEPS()));
      NOTE_TYPE_COLUMNS.put("proj_flag", n -> Boolean.valueOf(n.getAvailableForProject()));
      NOTE_TYPE_COLUMNS.put("wbs_flag", n -> Boolean.valueOf(n.getAvailableForWBS()));
      NOTE_TYPE_COLUMNS.put("task_flag", n -> Boolean.valueOf(n.getAvailableForActivity()));
      NOTE_TYPE_COLUMNS.put("memo_type", n -> StringHelper.stripControlCharacters(n.getName()));
   }

   private static final Map<String, ExportFunction<Map<String, Object>>> WBS_NOTE_COLUMNS = new LinkedHashMap<>();
   static
   {
      WBS_NOTE_COLUMNS.put("wbs_memo_id", n -> n.get("entity_memo_id"));
      WBS_NOTE_COLUMNS.put("proj_id", n -> n.get("proj_id"));
      WBS_NOTE_COLUMNS.put("memo_type_id", n -> n.get("memo_type_id"));
      WBS_NOTE_COLUMNS.put("wbs_id", n -> n.get("entity_id"));
      WBS_NOTE_COLUMNS.put("wbs_memo", n -> n.get("entity_memo"));
   }

   private static final Map<String, ExportFunction<Map<String, Object>>> ACTIVITY_NOTE_COLUMNS = new LinkedHashMap<>();
   static
   {
      ACTIVITY_NOTE_COLUMNS.put("memo_id", n -> n.get("entity_memo_id"));
      ACTIVITY_NOTE_COLUMNS.put("task_id", n -> n.get("entity_id"));
      ACTIVITY_NOTE_COLUMNS.put("memo_type_id", n -> n.get("memo_type_id"));
      ACTIVITY_NOTE_COLUMNS.put("proj_id", n -> n.get("proj_id"));
      ACTIVITY_NOTE_COLUMNS.put("task_memo", n -> n.get("entity_memo"));
   }

   private static final Map<String, ExportFunction<ProjectProperties>> SCHEDULE_OPTIONS_COLUMNS = new LinkedHashMap<>();
   static
   {
      SCHEDULE_OPTIONS_COLUMNS.put("schedoptions_id", o -> Integer.valueOf(1));
      SCHEDULE_OPTIONS_COLUMNS.put("proj_id", o -> getProjectID(o.getUniqueID()));
      SCHEDULE_OPTIONS_COLUMNS.put("sched_outer_depend_type", o -> o.getIgnoreRelationshipsToAndFromOtherProjects() ? "SD_None" : "SD_Both");
      SCHEDULE_OPTIONS_COLUMNS.put("sched_open_critical_flag", o -> Boolean.valueOf(o.getMakeOpenEndedActivitiesCritical()));
      SCHEDULE_OPTIONS_COLUMNS.put("sched_lag_early_start_flag", o -> Boolean.valueOf(o.getComputeStartToStartLagFromEarlyStart()));
      SCHEDULE_OPTIONS_COLUMNS.put("sched_retained_logic", o -> Boolean.valueOf(o.getSchedulingProgressedActivities() == SchedulingProgressedActivities.RETAINED_LOGIC));
      SCHEDULE_OPTIONS_COLUMNS.put("sched_setplantoforecast", o -> Boolean.valueOf(o.getDataDateAndPlannedStartSetToProjectForecastStart()));
      SCHEDULE_OPTIONS_COLUMNS.put("sched_float_type", o -> TotalSlackCalculationTypeHelper.getXerFromInstance(o.getTotalSlackCalculationType()));
      SCHEDULE_OPTIONS_COLUMNS.put("sched_calendar_on_relationship_lag", o -> RelationshipLagCalendarHelper.getXerFromInstance(o.getRelationshipLagCalendar()));
      SCHEDULE_OPTIONS_COLUMNS.put("sched_use_expect_end_flag", o -> Boolean.valueOf(o.getUseExpectedFinishDates()));
      SCHEDULE_OPTIONS_COLUMNS.put("sched_progress_override", o -> Boolean.valueOf(o.getSchedulingProgressedActivities() == SchedulingProgressedActivities.PROGRESS_OVERRIDE));
      SCHEDULE_OPTIONS_COLUMNS.put("level_float_thrs_cnt", ProjectProperties::getPreserveMinimumFloatWhenLeveling);
      SCHEDULE_OPTIONS_COLUMNS.put("level_outer_assign_flag", o -> Boolean.valueOf(o.getConsiderAssignmentsInOtherProjects()));
      SCHEDULE_OPTIONS_COLUMNS.put("level_outer_assign_priority", ProjectProperties::getConsiderAssignmentsInOtherProjectsWithPriorityEqualHigherThan);
      SCHEDULE_OPTIONS_COLUMNS.put("level_over_alloc_pct", ProjectProperties::getMaxPercentToOverallocateResources);
      SCHEDULE_OPTIONS_COLUMNS.put("level_within_float_flag", o -> Boolean.valueOf(o.getLevelResourcesOnlyWithinActivityTotalFloat()));
      SCHEDULE_OPTIONS_COLUMNS.put("level_keep_sched_date_flag", o -> Boolean.valueOf(o.getPreserveScheduledEarlyAndLateDates()));
      SCHEDULE_OPTIONS_COLUMNS.put("level_all_rsrc_flag", o -> Boolean.valueOf(o.getLevelAllResources()));
      SCHEDULE_OPTIONS_COLUMNS.put("sched_use_project_end_date_for_float", o -> Boolean.valueOf(o.getCalculateFloatBasedOnFinishDateOfEachProject()));
      SCHEDULE_OPTIONS_COLUMNS.put("enable_multiple_longest_path_calc", o -> Boolean.valueOf(o.getCalculateMultipleFloatPaths()));
      SCHEDULE_OPTIONS_COLUMNS.put("limit_multiple_longest_path_calc", o -> Boolean.valueOf(o.getLimitNumberOfFloatPathsToCalculate()));
      SCHEDULE_OPTIONS_COLUMNS.put("max_multiple_longest_path", ProjectProperties::getMaximumNumberOfFloatPathsToCalculate);
      SCHEDULE_OPTIONS_COLUMNS.put("use_total_float_multiple_longest_paths", o -> Boolean.valueOf(o.getCalculateMultipleFloatPathsUsingTotalFloat()));
      SCHEDULE_OPTIONS_COLUMNS.put("key_activity_for_multiple_longest_paths", ProjectProperties::getDisplayMultipleFloatPathsEndingWithActivityUniqueID);
      SCHEDULE_OPTIONS_COLUMNS.put("LevelPriorityList", o -> "priority_type,ASC_BY_FIELD/ASC"); // TODO: translation required
   }

   private static final Map<String, ExportFunction<UnitOfMeasure>> UNIT_OF_MEASURE_COLUMNS = new LinkedHashMap<>();
   static
   {
      UNIT_OF_MEASURE_COLUMNS.put("unit_id", UnitOfMeasure::getUniqueID);
      UNIT_OF_MEASURE_COLUMNS.put("seq_num", UnitOfMeasure::getSequenceNumber);
      UNIT_OF_MEASURE_COLUMNS.put("unit_abbrev", UnitOfMeasure::getAbbreviation);
      UNIT_OF_MEASURE_COLUMNS.put("unit_name", u -> StringHelper.stripControlCharacters(u.getName()));
   }

   private static final Map<String, ExportFunction<Shift>> SHIFT_COLUMNS = new LinkedHashMap<>();
   static
   {
      SHIFT_COLUMNS.put("shift_id", Shift::getUniqueID);
      SHIFT_COLUMNS.put("shift_name", s -> StringHelper.stripControlCharacters(s.getName()));
   }

   private static final Map<String, ExportFunction<ShiftPeriod>> SHIFT_PERIOD_COLUMNS = new LinkedHashMap<>();
   static
   {
      SHIFT_PERIOD_COLUMNS.put("shift_period_id", ShiftPeriod::getUniqueID);
      SHIFT_PERIOD_COLUMNS.put("shift_id", s -> s.getParentShift().getUniqueID());
      SHIFT_PERIOD_COLUMNS.put("shift_start_hr_num", s -> Integer.valueOf(s.getStart().getHour()));
   }

   private static final Map<String, ExportFunction<Map<String, Object>>> ROLE_ASSIGNMENT_COLUMNS = new LinkedHashMap<>();
   static
   {
      ROLE_ASSIGNMENT_COLUMNS.put("rsrc_id", v -> v.get("rsrc_id"));
      ROLE_ASSIGNMENT_COLUMNS.put("role_id", v -> v.get("role_id"));
      ROLE_ASSIGNMENT_COLUMNS.put("skill_level", v -> v.get("skill_level"));
      ROLE_ASSIGNMENT_COLUMNS.put("role_short_name", v -> v.get("role_short_name"));
      ROLE_ASSIGNMENT_COLUMNS.put("role_name", v -> v.get("role_name"));
      ROLE_ASSIGNMENT_COLUMNS.put("rsrc_short_name", v -> v.get("rsrc_short_name"));
      ROLE_ASSIGNMENT_COLUMNS.put("rsrc_name", v -> v.get("rsrc_name"));
      ROLE_ASSIGNMENT_COLUMNS.put("rsrc_type", v -> v.get("rsrc_type"));
      ROLE_ASSIGNMENT_COLUMNS.put("rsrc_role_id", v -> v.get("rsrc_role_id"));
   }

   private static final Map<String, ExportFunction<ProjectCode>> PROJECT_CODE_COLUMNS = new LinkedHashMap<>();
   static
   {
      PROJECT_CODE_COLUMNS.put("proj_catg_type_id", ProjectCode::getUniqueID);
      PROJECT_CODE_COLUMNS.put("seq_num", ProjectCode::getSequenceNumber);
      PROJECT_CODE_COLUMNS.put("proj_catg_short_len", WriterHelper::getCodeMaxLength);
      PROJECT_CODE_COLUMNS.put("proj_catg_type", ProjectCode::getName);
   }

   private static final Map<String, ExportFunction<ProjectCodeValue>> PROJECT_CODE_VALUE_COLUMNS = new LinkedHashMap<>();
   static
   {
      PROJECT_CODE_VALUE_COLUMNS.put("proj_catg_id", ProjectCodeValue::getUniqueID);
      PROJECT_CODE_VALUE_COLUMNS.put("proj_catg_type_id", ProjectCodeValue::getParentCodeUniqueID);
      PROJECT_CODE_VALUE_COLUMNS.put("seq_num", ProjectCodeValue::getSequenceNumber);
      PROJECT_CODE_VALUE_COLUMNS.put("proj_catg_short_name", ProjectCodeValue::getName);
      PROJECT_CODE_VALUE_COLUMNS.put("parent_proj_catg_id", ProjectCodeValue::getParentValueUniqueID);
      PROJECT_CODE_VALUE_COLUMNS.put("proj_catg_name", ProjectCodeValue::getDescription);
   }

   private static final Map<String, ExportFunction<Map<String, Object>>> PROJECT_CODE_ASSIGNMENT_COLUMNS = new LinkedHashMap<>();
   static
   {
      PROJECT_CODE_ASSIGNMENT_COLUMNS.put("proj_id", v -> v.get("proj_id"));
      PROJECT_CODE_ASSIGNMENT_COLUMNS.put("proj_catg_type_id", v -> v.get("proj_catg_type_id"));
      PROJECT_CODE_ASSIGNMENT_COLUMNS.put("proj_catg_id", v -> v.get("proj_catg_id"));
   }

   private static final Map<String, ExportFunction<ResourceCode>> RESOURCE_CODE_COLUMNS = new LinkedHashMap<>();
   static
   {
      RESOURCE_CODE_COLUMNS.put("rsrc_catg_type_id", ResourceCode::getUniqueID);
      RESOURCE_CODE_COLUMNS.put("seq_num", ResourceCode::getSequenceNumber);
      RESOURCE_CODE_COLUMNS.put("rsrc_catg_short_len", WriterHelper::getCodeMaxLength);
      RESOURCE_CODE_COLUMNS.put("rsrc_catg_type", ResourceCode::getName);
   }

   private static final Map<String, ExportFunction<ResourceCodeValue>> RESOURCE_CODE_VALUE_COLUMNS = new LinkedHashMap<>();
   static
   {
      RESOURCE_CODE_VALUE_COLUMNS.put("rsrc_catg_id", ResourceCodeValue::getUniqueID);
      RESOURCE_CODE_VALUE_COLUMNS.put("rsrc_catg_type_id", ResourceCodeValue::getParentCodeUniqueID);
      RESOURCE_CODE_VALUE_COLUMNS.put("seq_num", ResourceCodeValue::getSequenceNumber);
      RESOURCE_CODE_VALUE_COLUMNS.put("rsrc_catg_short_name", ResourceCodeValue::getName);
      RESOURCE_CODE_VALUE_COLUMNS.put("rsrc_catg_name", ResourceCodeValue::getDescription);
      RESOURCE_CODE_VALUE_COLUMNS.put("parent_rsrc_catg_id", ResourceCodeValue::getParentValueUniqueID);
   }

   private static final Map<String, ExportFunction<Map<String, Object>>> RESOURCE_CODE_ASSIGNMENT_COLUMNS = new LinkedHashMap<>();
   static
   {
      RESOURCE_CODE_ASSIGNMENT_COLUMNS.put("rsrc_id", v -> v.get("rsrc_id"));
      RESOURCE_CODE_ASSIGNMENT_COLUMNS.put("rsrc_catg_type_id", v -> v.get("rsrc_catg_type_id"));
      RESOURCE_CODE_ASSIGNMENT_COLUMNS.put("rsrc_catg_id", v -> v.get("rsrc_catg_id"));
   }

   private static final Map<String, ExportFunction<RoleCode>> ROLE_CODE_COLUMNS = new LinkedHashMap<>();
   static
   {
      ROLE_CODE_COLUMNS.put("role_catg_type_id", RoleCode::getUniqueID);
      ROLE_CODE_COLUMNS.put("seq_num", RoleCode::getSequenceNumber);
      ROLE_CODE_COLUMNS.put("role_catg_short_len", WriterHelper::getCodeMaxLength);
      ROLE_CODE_COLUMNS.put("role_catg_type", RoleCode::getName);
   }

   private static final Map<String, ExportFunction<RoleCodeValue>> ROLE_CODE_VALUE_COLUMNS = new LinkedHashMap<>();
   static
   {
      ROLE_CODE_VALUE_COLUMNS.put("role_catg_id", RoleCodeValue::getUniqueID);
      ROLE_CODE_VALUE_COLUMNS.put("role_catg_type_id", RoleCodeValue::getParentCodeUniqueID);
      ROLE_CODE_VALUE_COLUMNS.put("seq_num", RoleCodeValue::getSequenceNumber);
      ROLE_CODE_VALUE_COLUMNS.put("role_catg_short_name", RoleCodeValue::getName);
      ROLE_CODE_VALUE_COLUMNS.put("role_catg_name", RoleCodeValue::getDescription);
      ROLE_CODE_VALUE_COLUMNS.put("parent_role_catg_id", RoleCodeValue::getParentValueUniqueID);
   }

   private static final Map<String, ExportFunction<Map<String, Object>>> ROLE_CODE_ASSIGNMENT_COLUMNS = new LinkedHashMap<>();
   static
   {
      ROLE_CODE_ASSIGNMENT_COLUMNS.put("role_id", v -> v.get("role_id"));
      ROLE_CODE_ASSIGNMENT_COLUMNS.put("role_catg_type_id", v -> v.get("role_catg_type_id"));
      ROLE_CODE_ASSIGNMENT_COLUMNS.put("role_catg_id", v -> v.get("role_catg_id"));
   }

   private static final Map<String, ExportFunction<ResourceAssignmentCode>> RESOURCE_ASSIGNMENT_CODE_COLUMNS = new LinkedHashMap<>();
   static
   {
      RESOURCE_ASSIGNMENT_CODE_COLUMNS.put("asgnmnt_catg_type_id", ResourceAssignmentCode::getUniqueID);
      RESOURCE_ASSIGNMENT_CODE_COLUMNS.put("seq_num", ResourceAssignmentCode::getSequenceNumber);
      RESOURCE_ASSIGNMENT_CODE_COLUMNS.put("asgnmnt_catg_short_len", WriterHelper::getCodeMaxLength);
      RESOURCE_ASSIGNMENT_CODE_COLUMNS.put("asgnmnt_catg_type", ResourceAssignmentCode::getName);
   }

   private static final Map<String, ExportFunction<ResourceAssignmentCodeValue>> RESOURCE_ASSIGNMENT_CODE_VALUE_COLUMNS = new LinkedHashMap<>();
   static
   {
      RESOURCE_ASSIGNMENT_CODE_VALUE_COLUMNS.put("asgnmnt_catg_id", ResourceAssignmentCodeValue::getUniqueID);
      RESOURCE_ASSIGNMENT_CODE_VALUE_COLUMNS.put("asgnmnt_catg_type_id", ResourceAssignmentCodeValue::getParentCodeUniqueID);
      RESOURCE_ASSIGNMENT_CODE_VALUE_COLUMNS.put("seq_num", ResourceAssignmentCodeValue::getSequenceNumber);
      RESOURCE_ASSIGNMENT_CODE_VALUE_COLUMNS.put("asgnmnt_catg_short_name", ResourceAssignmentCodeValue::getName);
      RESOURCE_ASSIGNMENT_CODE_VALUE_COLUMNS.put("asgnmnt_catg_name", ResourceAssignmentCodeValue::getDescription);
      RESOURCE_ASSIGNMENT_CODE_VALUE_COLUMNS.put("parent_asgnmnt_catg_id", ResourceAssignmentCodeValue::getParentValueUniqueID);
   }

   private static final Map<String, ExportFunction<Map<String, Object>>> RESOURCE_ASSIGNMENT_CODE_ASSIGNMENT_COLUMNS = new LinkedHashMap<>();
   static
   {
      RESOURCE_ASSIGNMENT_CODE_ASSIGNMENT_COLUMNS.put("taskrsrc_id", v -> v.get("taskrsrc_id"));
      RESOURCE_ASSIGNMENT_CODE_ASSIGNMENT_COLUMNS.put("asgnmnt_catg_type_id", v -> v.get("asgnmnt_catg_type_id"));
      RESOURCE_ASSIGNMENT_CODE_ASSIGNMENT_COLUMNS.put("asgnmnt_catg_id", v -> v.get("asgnmnt_catg_id"));
      RESOURCE_ASSIGNMENT_CODE_ASSIGNMENT_COLUMNS.put("proj_id", v -> v.get("proj_id"));
   }
}
