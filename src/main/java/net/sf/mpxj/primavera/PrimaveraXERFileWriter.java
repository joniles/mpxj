package net.sf.mpxj.primavera;
import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.sf.mpxj.AccrueType;
import net.sf.mpxj.ActivityCode;
import net.sf.mpxj.ActivityCodeScope;
import net.sf.mpxj.ActivityCodeValue;
import net.sf.mpxj.ActivityStatus;
import net.sf.mpxj.ActivityType;
import net.sf.mpxj.Availability;
import net.sf.mpxj.CalendarType;
import net.sf.mpxj.ConstraintType;
import net.sf.mpxj.CostAccount;
import net.sf.mpxj.CostRateTableEntry;
import net.sf.mpxj.CriticalActivityType;
import net.sf.mpxj.Duration;
import net.sf.mpxj.ExpenseCategory;
import net.sf.mpxj.ExpenseItem;
import net.sf.mpxj.HtmlNotes;
import net.sf.mpxj.Notes;
import net.sf.mpxj.PercentCompleteType;
import net.sf.mpxj.Priority;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectCalendarDays;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ProjectProperties;
import net.sf.mpxj.Rate;
import net.sf.mpxj.RateSource;
import net.sf.mpxj.Relation;
import net.sf.mpxj.RelationType;
import net.sf.mpxj.Resource;
import net.sf.mpxj.ResourceAssignment;
import net.sf.mpxj.ResourceType;
import net.sf.mpxj.Step;
import net.sf.mpxj.Task;
import net.sf.mpxj.TaskType;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.WorkContour;
import net.sf.mpxj.common.CharsetHelper;
import net.sf.mpxj.common.ColorHelper;
import net.sf.mpxj.common.HtmlHelper;
import net.sf.mpxj.common.NumberHelper;
import net.sf.mpxj.common.Pair;
import net.sf.mpxj.writer.AbstractProjectWriter;

public class PrimaveraXERFileWriter extends AbstractProjectWriter
{
   /**
    * Sets the character encoding used when writing an XER file.
    *
    * @param encoding encoding name
    */
   public void setEncoding(String encoding)
   {
      m_encoding = encoding;
   }

   /**
    * Alternative way to set the file encoding. If both an encoding name and a Charset instance
    * are supplied, the Charset instance is used.
    *
    * @param charset Charset used when writing the file
    */
   public void setCharset(Charset charset)
   {
      m_charset = charset;
   }

   @Override public void write(ProjectFile projectFile, OutputStream outputStream) throws IOException
   {
      m_file = projectFile;
      m_writer = new OutputStreamWriter(outputStream, getCharset());
      m_roleRateUniqueID = 1;
      m_resourceRateUniqueID = 1;

      try
      {
         writeHeader();
         writeExpenseCategories();
         writeCurrencies();
         writeResourceCurves();
         writeUdfDefinitions();
         writeCostAccounts();
         writeRoles();
         writeProject();
         writeRoleRates();
         writeCalendars();
         writeWBS();
         writeResources();
         writeActivityCodes();
         writeResourceRates();
         writeActivities();
         writeActivityCodeValues();
         writeActivitySteps();
         writeExpenseItems();
         writePredecessors();
         writeResourceAssignments();
         writeActivityCodeAssignments();

         m_writer.flush();
      }

      finally
      {
         m_writer = null;
      }
   }

   /**
    * Retrieve the Charset used to write the file.
    *
    * @return Charset instance
    */
   private Charset getCharset()
   {
      Charset result = m_charset;
      if (result == null)
      {
         // We default to CP1252 as this seems to be the most common encoding
         result = m_encoding == null ? CharsetHelper.CP1252 : Charset.forName(m_encoding);
      }
      return result;
   }

   private void writeHeader()
   {
      Object[] data = {
         "ERMHDR",
         "20.12",
         formatDate(new Date()),
         "Project",
         "admin",
         "admin",
         "dbxDatabaseNoName",
         "Project Management",
         CURRENCY_COLUMNS.get("curr_short_name")
      };

      try
      {
         m_writer.write(Arrays.stream(data).map(this::format).collect(Collectors.joining("\t")));
         m_writer.write("\n");
      }

      catch (IOException ex)
      {
         throw new RuntimeException(ex);
      }
   }

   private void writeCurrencies()
   {
      writeTable("CURRTYPE", CURRENCY_COLUMNS);
      writeRecord(CURRENCY_COLUMNS.values().stream());
   }

   private void writeRoles()
   {
      writeTable("ROLES", ROLE_COLUMNS);
      m_file.getResources().stream().filter(Resource::getRole).sorted(Comparator.comparing(Resource::getUniqueID)).forEach(r -> writeRecord(ROLE_COLUMNS, r));
   }

   private void writeRoleRates()
   {
      writeTable("ROLERATE", ROLE_RATE_COLUMNS);
      m_file.getResources().stream().filter(Resource::getRole).sorted(Comparator.comparing(Resource::getUniqueID)).forEach(r -> writeCostRateTableEntries(ROLE_RATE_COLUMNS, r));
   }

   private void writeResourceRates()
   {
      writeTable("RSRCRATE", RESOURCE_RATE_COLUMNS);
      m_file.getResources().stream().filter(r -> !r.getRole().booleanValue()).sorted(Comparator.comparing(Resource::getUniqueID)).forEach(r -> writeCostRateTableEntries(RESOURCE_RATE_COLUMNS, r));
   }

   private void writeCostRateTableEntries(Map<String, CostRateTableEntryFunction> columns, Resource resource)
   {
      resource.getCostRateTable(0).stream().filter(e -> e != CostRateTableEntry.DEFAULT_ENTRY).forEach(e -> writeCostRateTableEntry(columns, resource, e));
   }

   private void writeCostRateTableEntry(Map<String, CostRateTableEntryFunction> columns, Resource resource, CostRateTableEntry entry)
   {
      writeRecord(columns.values().stream().map(f -> f.apply(this, resource, entry)));
   }

   private void writeResources()
   {
      writeTable("RSRC", RESOURCE_COLUMNS);
      m_file.getResources().stream().filter(r -> !r.getRole().booleanValue()).sorted(Comparator.comparing(Resource::getUniqueID)).forEach(r -> writeRecord(RESOURCE_COLUMNS, r));
   }

   private void writeProject()
   {
      writeTable("PROJECT", PROJECT_COLUMNS);
      writeRecord(PROJECT_COLUMNS, m_file.getProjectProperties());
   }

   private void writeCalendars()
   {
      writeTable("CALENDAR", CALENDAR_COLUMNS);
      m_file.getCalendars().stream().sorted(Comparator.comparing(ProjectCalendar::getUniqueID)).map(ProjectCalendarHelper::normalizeCalendar).forEach(c -> writeRecord(CALENDAR_COLUMNS, c));
   }

   private void writeWBS()
   {
      writeTable("PROJWBS", WBS_COLUMNS);
      m_file.getTasks().stream().filter(Task::getSummary).sorted(Comparator.comparing(Task::getUniqueID)).forEach(t -> writeRecord(WBS_COLUMNS, t));
   }

   private void writeActivities()
   {
      writeTable("TASK", ACTIVITY_COLUMNS);
      m_file.getTasks().stream().filter(t -> !t.getSummary()).sorted(Comparator.comparing(Task::getUniqueID)).forEach(t -> writeRecord(ACTIVITY_COLUMNS, t));
   }

   private void writePredecessors()
   {
      writeTable("TASKPRED", PREDECESSOR_COLUMNS);
      m_file.getTasks().stream().filter(t -> !t.getSummary()).map(Task::getPredecessors).flatMap(Collection::stream).sorted(Comparator.comparing(Relation::getUniqueID)).forEach(r -> writeRecord(PREDECESSOR_COLUMNS, r));
   }

   private void writeResourceAssignments()
   {
      writeTable("TASKRSRC", RESOURCE_ASSIGNMENT_COLUMNS);
      m_file.getResourceAssignments().stream().sorted(Comparator.comparing(ResourceAssignment::getUniqueID)).forEach(t -> writeRecord(RESOURCE_ASSIGNMENT_COLUMNS, t));
   }

   private void writeCostAccounts()
   {
      writeTable("ACCOUNT", COST_ACCOUNT_COLUMNS);
      m_file.getCostAccounts().stream().sorted(Comparator.comparing(CostAccount::getUniqueID)).forEach(a -> writeRecord(COST_ACCOUNT_COLUMNS, a));
   }

   private void writeExpenseCategories()
   {
      writeTable("COSTTYPE", EXPENSE_CATEGORY_COLUMNS);
      m_file.getExpenseCategories().stream().sorted(Comparator.comparing(ExpenseCategory::getUniqueID)).forEach(a -> writeRecord(EXPENSE_CATEGORY_COLUMNS, a));
   }

   private void writeExpenseItems()
   {
      writeTable("PROJCOST", EXPENSE_ITEM_COLUMNS);
      m_file.getTasks().stream().filter(t -> !t.getSummary()).map(Task::getExpenseItems).flatMap(Collection::stream).sorted(Comparator.comparing(ExpenseItem::getUniqueID)).forEach(i -> writeRecord(EXPENSE_ITEM_COLUMNS, i));
   }

   private void writeResourceCurves()
   {
      writeTable("RSRCCURVDATA", RESOURCE_CURVE_COLUMNS);
      m_file.getWorkContours().stream().sorted(Comparator.comparing(WorkContour::getUniqueID)).forEach(r -> writeRecord(RESOURCE_CURVE_COLUMNS, r));
   }

   private void writeActivitySteps()
   {
      writeTable("TASKPROC", ACTIVITY_STEP_COLUMNS);
      m_file.getTasks().stream().filter(t -> !t.getSummary()).map(Task::getSteps).flatMap(Collection::stream).sorted(Comparator.comparing(Step::getUniqueID)).forEach(s -> writeRecord(ACTIVITY_STEP_COLUMNS, s));
   }

   private void writeActivityCodes()
   {
      writeTable("ACTVTYPE", ACTIVITY_CODE_COLUMNS);
      m_file.getActivityCodes().stream().sorted(Comparator.comparing(ActivityCode::getUniqueID)).forEach(c -> writeRecord(ACTIVITY_CODE_COLUMNS, c));
   }

   private void writeActivityCodeValues()
   {
      writeTable("ACTVCODE", ACTIVITY_CODE_VALUE_COLUMNS);
      m_file.getActivityCodes().stream().map(ActivityCode::getValues).flatMap(Collection::stream).sorted(Comparator.comparing(ActivityCodeValue::getUniqueID)).forEach(v -> writeRecord(ACTIVITY_CODE_VALUE_COLUMNS, v));
   }

   private void writeActivityCodeAssignments()
   {
      writeTable("TASKACTV", ACTIVITY_CODE_ASSIGNMENT_COLUMNS);
      m_file.getTasks().stream().filter(t -> !t.getSummary()).collect(Collectors.toMap(t -> t, Task::getActivityCodes, (u, v) -> u, TreeMap::new)).forEach(this::writeActivityCodeAssignments);
   }

   private void writeActivityCodeAssignments(Task task, List<ActivityCodeValue> values)
   {
      values.stream().sorted(Comparator.comparing(ActivityCodeValue::getUniqueID)).forEach(v -> writeRecord(ACTIVITY_CODE_ASSIGNMENT_COLUMNS, new Pair<>(task, v)));
   }

   private void writeUdfDefinitions()
   {
      writeTable("UDFTYPE", UDF_TYPE_COLUMNS);
   }

   private void writeTable(String name, Map<String, ?> map)
   {
      try
      {
         m_writer.write("%T\t" + name + "\n");
         m_writer.write("%F\t");
         m_writer.write(String.join("\t", map.keySet()));
         m_writer.write("\n");
      }

      catch (IOException ex)
      {
         throw new RuntimeException(ex);
      }
   }

   private <T> void writeRecord(Map<String, ExportFunction<T>> columns, T object)
   {
      writeRecord(columns.values().stream().map(f -> f.apply(object)));
   }

   private void writeRecord(Stream<Object> data)
   {
      try
      {
         m_writer.write("%R\t");
         m_writer.write(data.map(this::format).collect(Collectors.joining("\t")));
         m_writer.write("\n");
      }

      catch (IOException ex)
      {
         throw new RuntimeException(ex);
      }
   }

   private String format(Object object)
   {
      if (object == null)
      {
         return "";
      }

      // Handle objects which may be subclasses
      if (object instanceof Notes)
      {
         return formatNotes((Notes) object);
      }

      // Handle objects we can identify by class name
      FormatFunction f = FORMAT_MAP.get(object.getClass());

      return f == null ? object.toString() : f.apply(this, object);
   }

   private String formatDate(Date date)
   {
      return m_dateFormat.format(date);
   }

   private String formatNotes(Notes notes)
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
         result = result.replace("\n", "\u007F\u007F");
      }

      return result;
   }

   private String formatRate(Rate rate)
   {
      return m_rateFormat.format(rate.getAmount());
   }

   private String formatUUID(UUID value)
   {
      byte[] data = new byte[16];
      long lsb = value.getLeastSignificantBits();
      long msb = value.getMostSignificantBits();

      data[15] = (byte)(lsb & 0xff);
      data[14] = (byte)(lsb >> 8 & 0xff);
      data[13] = (byte)(lsb >> 16 & 0xff);
      data[12] = (byte)(lsb >> 24 & 0xff);
      data[11] = (byte)(lsb >> 32 & 0xff);
      data[10] = (byte)(lsb >> 40 & 0xff);
      data[9] = (byte)(lsb >> 48 & 0xff);
      data[8] = (byte)(lsb >> 56 & 0xff);

      data[6] = (byte)(msb & 0xff);
      data[7] = (byte)(msb >> 8 & 0xff);
      data[4] = (byte)(msb >> 16 & 0xff);
      data[5] = (byte)(msb >> 24 & 0xff);
      data[0] = (byte)(msb >> 32 & 0xff);
      data[1] = (byte)(msb >> 40 & 0xff);
      data[2] = (byte)(msb >> 48 & 0xff);
      data[3] = (byte)(msb >> 56 & 0xff);

      String result = javax.xml.bind.DatatypeConverter.printBase64Binary(data);

      return result.substring(0, result.length()-2);
   }

   private String formatDuration(Duration duration)
   {
      if (duration == null)
      {
         return "";
      }

      return m_doubleFormat.format(duration.convertUnits(TimeUnit.HOURS, m_file.getProjectProperties()).getDuration());
   }

   private String getMaxQuantityPerHour(Resource resource, CostRateTableEntry entry)
   {
      Availability availability = resource.getAvailability().getEntryByDate(entry.getStartDate());
      if (availability == null)
      {
         return "0";
      }

      return m_maxUnitsFormat.format(NumberHelper.getDouble(availability.getUnits()) / 100.0);
   }

   private static Duration getActualRegularWork(ResourceAssignment assignment)
   {
      ProjectProperties properties = assignment.getParentFile().getProjectProperties();
      Duration actualWork = assignment.getActualWork().convertUnits(TimeUnit.HOURS, properties);
      Duration actualOvertimeWork = assignment.getActualOvertimeWork().convertUnits(TimeUnit.HOURS, properties);
      return Duration.getInstance(actualWork.getDuration() - actualOvertimeWork.getDuration(), TimeUnit.HOURS);
   }

   private static Double getActualRegularCost(ResourceAssignment assignment)
   {
      ProjectProperties properties = assignment.getParentFile().getProjectProperties();
      Number actualCost = assignment.getActualCost();
      Number actualOvertimeCost = assignment.getActualOvertimeCost();
      return Double.valueOf(actualCost.doubleValue() - actualOvertimeCost.doubleValue());
   }

   private String m_encoding;
   private Charset m_charset;
   private ProjectFile m_file;
   private OutputStreamWriter m_writer;

   private int m_roleRateUniqueID;

   private int m_resourceRateUniqueID;

   private final Format m_dateFormat = new SimpleDateFormat("yyyy-MM-dd");

   private final Format m_timestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

   private final DecimalFormat m_rateFormat = new DecimalFormat("0.0000");

   private final DecimalFormat m_maxUnitsFormat = new DecimalFormat("0.####");

   private final DecimalFormat m_doubleFormat = new DecimalFormat("0.####");

   private interface CostRateTableEntryFunction
   {
      Object apply(PrimaveraXERFileWriter writer, Resource resource, CostRateTableEntry entry);
   }

   private interface ExportFunction<T>
   {
      Object apply(T source);
   }

   private interface FormatFunction
   {
      String apply(PrimaveraXERFileWriter writer, Object source);
   }

   private static final Map<String, Object> CURRENCY_COLUMNS = new LinkedHashMap<>();
   static
   {
      CURRENCY_COLUMNS.put("curr_id", "1");
      CURRENCY_COLUMNS.put("decimal_digit_cnt", "2");
      CURRENCY_COLUMNS.put("curr_symbol", "$");
      CURRENCY_COLUMNS.put("decimal_symbol", ".");
      CURRENCY_COLUMNS.put("digit_group_symbol", ",");
      CURRENCY_COLUMNS.put("pos_curr_fmt_type", "#1.1");
      CURRENCY_COLUMNS.put("neg_curr_fmt_type", "(#1.1)");
      CURRENCY_COLUMNS.put("curr_type", "US Dollar");
      CURRENCY_COLUMNS.put("curr_short_name", "USD");
      CURRENCY_COLUMNS.put("group_digit_cnt", "3");
      CURRENCY_COLUMNS.put("base_exch_rate", "1");
   }

   private static final Map<String, ExportFunction<Resource>> ROLE_COLUMNS = new LinkedHashMap<>();
   static
   {
      ROLE_COLUMNS.put("role_id", Resource::getUniqueID);
      ROLE_COLUMNS.put("parent_role_id", Resource::getParentResourceUniqueID);
      ROLE_COLUMNS.put("seq_num", Resource::getSequenceNumber);
      ROLE_COLUMNS.put("role_name", Resource::getName);
      ROLE_COLUMNS.put("role_short_name", Resource::getResourceID);
      ROLE_COLUMNS.put("pobs_id", r -> "");
      ROLE_COLUMNS.put("def_cost_qty_link_flag", Resource::getCalculateCostsFromUnits);
      ROLE_COLUMNS.put("cost_qty_type", r -> "QT_Hour");
      ROLE_COLUMNS.put("role_descr", Resource::getNotesObject);
      ROLE_COLUMNS.put("last_checksum", r -> "");
   }

   private static final Map<String, CostRateTableEntryFunction> ROLE_RATE_COLUMNS = new LinkedHashMap<>();
   static
   {
      ROLE_RATE_COLUMNS.put("role_rate_id", (w, r, e) -> Integer.valueOf(w.m_roleRateUniqueID++));
      ROLE_RATE_COLUMNS.put("role_id", (w, r, e) -> r.getUniqueID());
      ROLE_RATE_COLUMNS.put("cost_per_qty", (w, r, e) -> e.getRate(0));
      ROLE_RATE_COLUMNS.put("cost_per_qty2", (w, r, e) -> e.getRate(1));
      ROLE_RATE_COLUMNS.put("cost_per_qty3", (w, r, e) -> e.getRate(2));
      ROLE_RATE_COLUMNS.put("cost_per_qty4", (w, r, e) -> e.getRate(3));
      ROLE_RATE_COLUMNS.put("cost_per_qty5", (w, r, e) -> e.getRate(4));
      ROLE_RATE_COLUMNS.put("start_date", (w, r, e) -> e.getStartDate());
      ROLE_RATE_COLUMNS.put("max_qty_per_hr", PrimaveraXERFileWriter::getMaxQuantityPerHour);
   }

   private static final Map<String, CostRateTableEntryFunction> RESOURCE_RATE_COLUMNS = new LinkedHashMap<>();
   static
   {
      RESOURCE_RATE_COLUMNS.put("rsrc_rate_id", (w, r, e) -> Integer.valueOf(w.m_resourceRateUniqueID++));
      RESOURCE_RATE_COLUMNS.put("rsrc_id", (w, r, e) -> r.getUniqueID());
      RESOURCE_RATE_COLUMNS.put("max_qty_per_hr", PrimaveraXERFileWriter::getMaxQuantityPerHour);
      RESOURCE_RATE_COLUMNS.put("cost_per_qty", (w, r, e) -> e.getRate(0));
      RESOURCE_RATE_COLUMNS.put("start_date", (w, r, e) -> e.getStartDate());
      RESOURCE_RATE_COLUMNS.put("shift_period_id", (w, r, e) -> "");
      RESOURCE_RATE_COLUMNS.put("cost_per_qty2", (w, r, e) -> e.getRate(1));
      RESOURCE_RATE_COLUMNS.put("cost_per_qty3", (w, r, e) -> e.getRate(2));
      RESOURCE_RATE_COLUMNS.put("cost_per_qty4", (w, r, e) -> e.getRate(3));
      RESOURCE_RATE_COLUMNS.put("cost_per_qty5", (w, r, e) -> e.getRate(4));
   }

   private static final Map<String, ExportFunction<Resource>> RESOURCE_COLUMNS = new LinkedHashMap<>();
   static
   {
      RESOURCE_COLUMNS.put("rsrc_id", Resource::getUniqueID);
      RESOURCE_COLUMNS.put("parent_rsrc_id", Resource::getParentResourceUniqueID);
      RESOURCE_COLUMNS.put("clndr_id", Resource::getCalendarUniqueID);
      RESOURCE_COLUMNS.put("role_id", r -> "");
      RESOURCE_COLUMNS.put("shift_id", r -> "");
      RESOURCE_COLUMNS.put("user_id", r -> "");
      RESOURCE_COLUMNS.put("pobs_id", r -> "");
      RESOURCE_COLUMNS.put("guid", Resource::getGUID);
      RESOURCE_COLUMNS.put("rsrc_seq_num", Resource::getSequenceNumber);
      RESOURCE_COLUMNS.put("email_addr", Resource::getEmailAddress);
      RESOURCE_COLUMNS.put("employee_code", Resource::getCode);
      RESOURCE_COLUMNS.put("office_phone", r -> "");
      RESOURCE_COLUMNS.put("other_phone", r -> "");
      RESOURCE_COLUMNS.put("rsrc_name", Resource::getName);
      RESOURCE_COLUMNS.put("rsrc_short_name", Resource::getResourceID);
      RESOURCE_COLUMNS.put("rsrc_title_name", r -> "");
      RESOURCE_COLUMNS.put("def_qty_per_hr", r -> r.getMaxUnits() == null ? null : r.getMaxUnits().doubleValue() / 100.0);
      RESOURCE_COLUMNS.put("cost_qty_type", r -> "QT_Hour");
      RESOURCE_COLUMNS.put("ot_factor", r -> "");
      RESOURCE_COLUMNS.put("active_flag", Resource::getActive);
      RESOURCE_COLUMNS.put("auto_compute_act_flag", r -> Boolean.TRUE);
      RESOURCE_COLUMNS.put("def_cost_qty_link_flag", Resource::getCalculateCostsFromUnits);
      RESOURCE_COLUMNS.put("ot_flag", r -> Boolean.FALSE);
      RESOURCE_COLUMNS.put("curr_id", r -> CURRENCY_COLUMNS.get("curr_id"));
      RESOURCE_COLUMNS.put("unit_id", r -> "");
      RESOURCE_COLUMNS.put("rsrc_type", Resource::getType);
      RESOURCE_COLUMNS.put("location_id", r -> "");
      RESOURCE_COLUMNS.put("rsrc_notes", Resource::getNotesObject);
      RESOURCE_COLUMNS.put("load_tasks_flag", r -> "");
      RESOURCE_COLUMNS.put("level_flag", r -> "");
      RESOURCE_COLUMNS.put("last_checksum", r -> "");
   }

   private static final Map<String, ExportFunction<ProjectProperties>> PROJECT_COLUMNS = new LinkedHashMap<>();
   static
   {
      PROJECT_COLUMNS.put("proj_id", ProjectProperties::getUniqueID);
      PROJECT_COLUMNS.put("fy_start_month_num", ProjectProperties::getFiscalYearStartMonth);
      PROJECT_COLUMNS.put("rsrc_self_add_flag", p -> Boolean.TRUE);
      PROJECT_COLUMNS.put("allow_complete_flag", p -> Boolean.TRUE);
      PROJECT_COLUMNS.put("rsrc_multi_assign_flag", p -> Boolean.TRUE);
      PROJECT_COLUMNS.put("checkout_flag", p -> Boolean.FALSE);
      PROJECT_COLUMNS.put("project_flag", p -> Boolean.TRUE);
      PROJECT_COLUMNS.put("step_complete_flag", p -> Boolean.TRUE);
      PROJECT_COLUMNS.put("cost_qty_recalc_flag", p -> Boolean.TRUE);
      PROJECT_COLUMNS.put("batch_sum_flag", p -> Boolean.TRUE);
      PROJECT_COLUMNS.put("name_sep_char", p -> ".");
      PROJECT_COLUMNS.put("def_complete_pct_type", p -> PercentCompleteType.DURATION);
      PROJECT_COLUMNS.put("proj_short_name", ProjectProperties::getProjectID);
      PROJECT_COLUMNS.put("acct_id", p -> "");
      PROJECT_COLUMNS.put("orig_proj_id", p -> "");
      PROJECT_COLUMNS.put("source_proj_id", p -> "");
      PROJECT_COLUMNS.put("base_type_id", p -> "");
      PROJECT_COLUMNS.put("clndr_id", ProjectProperties::getDefaultCalendarUniqueID);
      PROJECT_COLUMNS.put("sum_base_proj_id", ProjectProperties::getBaselineProjectUniqueID);
      PROJECT_COLUMNS.put("task_code_base", p -> Integer.valueOf(1000));
      PROJECT_COLUMNS.put("task_code_step", p -> Integer.valueOf(10));
      PROJECT_COLUMNS.put("priority_num", p -> Integer.valueOf(10));
      PROJECT_COLUMNS.put("wbs_max_sum_level", p -> Integer.valueOf(0));
      PROJECT_COLUMNS.put("strgy_priority_num", p -> Integer.valueOf(100));
      PROJECT_COLUMNS.put("last_checksum", p -> "");
      PROJECT_COLUMNS.put("critical_drtn_hr_cnt", p -> p.getCriticalSlackLimit().convertUnits(TimeUnit.HOURS, p).getDuration());
      PROJECT_COLUMNS.put("def_cost_per_qty", p -> Double.valueOf(100.0));
      PROJECT_COLUMNS.put("last_recalc_date", ProjectProperties::getStatusDate);
      PROJECT_COLUMNS.put("plan_start_date", ProjectProperties::getPlannedStart);
      PROJECT_COLUMNS.put("plan_end_date", ProjectProperties::getMustFinishBy);
      PROJECT_COLUMNS.put("scd_end_date", ProjectProperties::getScheduledFinish);
      PROJECT_COLUMNS.put("add_date", ProjectProperties::getCreationDate);
      PROJECT_COLUMNS.put("last_tasksum_date", p -> "");
      PROJECT_COLUMNS.put("fcst_start_date", p -> "");
      PROJECT_COLUMNS.put("def_duration_type", ProjectProperties::getDefaultTaskType);
      PROJECT_COLUMNS.put("task_code_prefix", p -> "");
      PROJECT_COLUMNS.put("guid", ProjectProperties::getGUID);
      PROJECT_COLUMNS.put("def_qty_type", p -> "QT_Hour");
      PROJECT_COLUMNS.put("add_by_name", p -> "admin");
      PROJECT_COLUMNS.put("web_local_root_path", p -> "");
      PROJECT_COLUMNS.put("proj_url", p -> "");
      PROJECT_COLUMNS.put("def_rate_type", p -> RateTypeHelper.getXerFromInstance(Integer.valueOf(0)));
      PROJECT_COLUMNS.put("add_act_remain_flag", p -> Boolean.FALSE);
      PROJECT_COLUMNS.put("act_this_per_link_flag", p -> Boolean.TRUE);
      PROJECT_COLUMNS.put("def_task_type", p -> ActivityType.TASK_DEPENDENT);
      PROJECT_COLUMNS.put("act_pct_link_flag", p -> Boolean.FALSE);
      PROJECT_COLUMNS.put("critical_path_type", ProjectProperties::getCriticalActivityType);
      PROJECT_COLUMNS.put("task_code_prefix_flag", p -> Boolean.TRUE);
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
      PROJECT_COLUMNS.put("location_id", p -> "");
      PROJECT_COLUMNS.put("loaded_scope_level", p -> Integer.valueOf(7));
      PROJECT_COLUMNS.put("export_flag", ProjectProperties::getExportFlag);
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
      CALENDAR_COLUMNS.put("default_flag", c -> c.getParentFile().getProjectProperties().getDefaultCalendar() == c);
      CALENDAR_COLUMNS.put("clndr_name", ProjectCalendarDays::getName);
      CALENDAR_COLUMNS.put("proj_id", c -> c.getType() == CalendarType.PROJECT ? c.getParentFile().getProjectProperties().getUniqueID() : null);
      CALENDAR_COLUMNS.put("base_clndr_id", ProjectCalendar::getParentUniqueID);
      CALENDAR_COLUMNS.put("last_chng_date", c -> null);
      CALENDAR_COLUMNS.put("clndr_type", ProjectCalendar::getType);
      CALENDAR_COLUMNS.put("day_hr_cnt", c -> Integer.valueOf(NumberHelper.getInt(c.getMinutesPerDay()) / 60));
      CALENDAR_COLUMNS.put("week_hr_cnt", c -> Integer.valueOf(NumberHelper.getInt(c.getMinutesPerWeek()) / 60));
      CALENDAR_COLUMNS.put("month_hr_cnt", c -> Integer.valueOf(NumberHelper.getInt(c.getMinutesPerMonth()) / 60));
      CALENDAR_COLUMNS.put("year_hr_cnt", c -> Integer.valueOf(NumberHelper.getInt(c.getMinutesPerYear()) / 60));
      CALENDAR_COLUMNS.put("rsrc_private", ProjectCalendar::getPersonal);
      CALENDAR_COLUMNS.put("clndr_data", c -> new ProjectCalendarStructuredTextWriter().getCalendarData(c));
   }

   private static final Map<String, ExportFunction<Task>> WBS_COLUMNS = new LinkedHashMap<>();
   static
   {
      WBS_COLUMNS.put("wbs_id", Task::getUniqueID);
      WBS_COLUMNS.put("proj_id", t -> t.getParentFile().getProjectProperties().getUniqueID() );
      WBS_COLUMNS.put("obs_id", t -> "");
      WBS_COLUMNS.put("seq_num", Task::getSequenceNumber);
      WBS_COLUMNS.put("est_wt", t -> Integer.valueOf(1));
      WBS_COLUMNS.put("proj_node_flag", t -> Boolean.FALSE);
      WBS_COLUMNS.put("sum_data_flag", t -> Boolean.TRUE);
      WBS_COLUMNS.put("status_code", t -> "WS_Open");
      WBS_COLUMNS.put("wbs_short_name", TaskHelper::getWbsCode);
      WBS_COLUMNS.put("wbs_name", Task::getName);
      WBS_COLUMNS.put("phase_id", t -> "");
      WBS_COLUMNS.put("parent_wbs_id", Task::getParentTaskUniqueID);
      WBS_COLUMNS.put("ev_user_pct", Task::getPlannedCost);
      WBS_COLUMNS.put("ev_etc_user_value", t -> "");
      WBS_COLUMNS.put("orig_cost", t -> "");
      WBS_COLUMNS.put("indep_remain_total_cost", t -> "");
      WBS_COLUMNS.put("ann_dscnt_rate_pct", t -> "");
      WBS_COLUMNS.put("dscnt_period_type", t -> "");
      WBS_COLUMNS.put("indep_remain_work_qty", t -> "");
      WBS_COLUMNS.put("anticip_start_date", t -> "");
      WBS_COLUMNS.put("anticip_end_date", t -> "");
      WBS_COLUMNS.put("ev_compute_type", t -> "EC_Cmp_pct");
      WBS_COLUMNS.put("ev_etc_compute_type", t -> "EC_Cmp_pct");
      WBS_COLUMNS.put("guid", Task::getGUID);
      WBS_COLUMNS.put("tmpl_guid", t -> "");
      WBS_COLUMNS.put("plan_open_state", t -> "");
   }

   private static final Map<String, ExportFunction<Task>> ACTIVITY_COLUMNS = new LinkedHashMap<>();
   static
   {
      ACTIVITY_COLUMNS.put("task_id", Task::getUniqueID);
      ACTIVITY_COLUMNS.put("proj_id", t -> t.getParentFile().getProjectProperties().getUniqueID());
      ACTIVITY_COLUMNS.put("wbs_id", t -> t.getParentTask().getUniqueID());
      ACTIVITY_COLUMNS.put("clndr_id", Task::getCalendarUniqueID);
      ACTIVITY_COLUMNS.put("phys_complete_pct", Task::getPhysicalPercentComplete);
      ACTIVITY_COLUMNS.put("rev_fdbk_flag", t -> Boolean.FALSE);
      ACTIVITY_COLUMNS.put("est_wt", t -> Integer.valueOf(1));
      ACTIVITY_COLUMNS.put("lock_plan_flag", t -> Boolean.FALSE);
      ACTIVITY_COLUMNS.put("auto_compute_act_flag", t -> Boolean.TRUE);
      ACTIVITY_COLUMNS.put("complete_pct_type", Task::getPercentCompleteType);
      ACTIVITY_COLUMNS.put("task_type", Task::getActivityType);
      ACTIVITY_COLUMNS.put("duration_type", Task::getType);
      ACTIVITY_COLUMNS.put("status_code", ActivityStatusHelper::getActivityStatus);
      ACTIVITY_COLUMNS.put("task_code", Task::getActivityID);
      ACTIVITY_COLUMNS.put("task_name", Task::getName);
      ACTIVITY_COLUMNS.put("rsrc_id", Task::getPrimaryResourceID);

      // TODO: should be blank if complete
      ACTIVITY_COLUMNS.put("total_float_hr_cnt", Task::getTotalSlack);
      ACTIVITY_COLUMNS.put("free_float_hr_cnt", Task::getFreeSlack);

      ACTIVITY_COLUMNS.put("remain_drtn_hr_cnt", Task::getRemainingDuration);
      ACTIVITY_COLUMNS.put("act_work_qty", Task::getActualWork);
      ACTIVITY_COLUMNS.put("remain_work_qty", Task::getRemainingWork);
      ACTIVITY_COLUMNS.put("target_work_qty", Task::getPlannedWork);
      ACTIVITY_COLUMNS.put("target_drtn_hr_cnt", Task::getPlannedDuration);
      ACTIVITY_COLUMNS.put("target_equip_qty", t -> Integer.valueOf(0));
      ACTIVITY_COLUMNS.put("act_equip_qty", t -> Integer.valueOf(0));
      ACTIVITY_COLUMNS.put("remain_equip_qty", t -> Integer.valueOf(0));
      ACTIVITY_COLUMNS.put("cstr_date", Task::getConstraintDate);
      ACTIVITY_COLUMNS.put("act_start_date", Task::getActualStart);
      ACTIVITY_COLUMNS.put("act_end_date", Task::getActualFinish);
      ACTIVITY_COLUMNS.put("late_start_date", Task::getLateStart);
      ACTIVITY_COLUMNS.put("late_end_date", Task::getLateFinish);
      ACTIVITY_COLUMNS.put("expect_end_date", t -> null);
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
      ACTIVITY_COLUMNS.put("float_path", t -> null);
      ACTIVITY_COLUMNS.put("float_path_order", t -> null);
      ACTIVITY_COLUMNS.put("guid", Task::getGUID);
      ACTIVITY_COLUMNS.put("tmpl_guid", t -> null);
      ACTIVITY_COLUMNS.put("cstr_date2", Task::getSecondaryConstraintDate);
      ACTIVITY_COLUMNS.put("cstr_type2", Task::getSecondaryConstraintType);
      ACTIVITY_COLUMNS.put("driving_path_flag", t -> null);
      ACTIVITY_COLUMNS.put("act_this_per_work_qty", t -> null);
      ACTIVITY_COLUMNS.put("act_this_per_equip_qty", t -> null);
      ACTIVITY_COLUMNS.put("external_early_start_date", t -> null);
      ACTIVITY_COLUMNS.put("external_late_end_date", t -> null);
      ACTIVITY_COLUMNS.put("create_date", Task::getCreateDate);
      ACTIVITY_COLUMNS.put("update_date", t -> null);
      ACTIVITY_COLUMNS.put("create_user", t -> null);
      ACTIVITY_COLUMNS.put("update_user", t -> null);
      ACTIVITY_COLUMNS.put("location_id", t -> null);
   }

   private static final Map<String, ExportFunction<Relation>> PREDECESSOR_COLUMNS = new LinkedHashMap<>();
   static
   {
      PREDECESSOR_COLUMNS.put("task_pred_id", Relation::getUniqueID);
      PREDECESSOR_COLUMNS.put("task_id", r -> r.getSourceTask().getUniqueID());
      PREDECESSOR_COLUMNS.put("pred_task_id", r -> r.getTargetTask().getUniqueID());
      PREDECESSOR_COLUMNS.put("proj_id", r -> r.getSourceTask().getParentFile().getProjectProperties().getUniqueID());
      PREDECESSOR_COLUMNS.put("pred_proj_id", r -> r.getTargetTask().getParentFile().getProjectProperties().getUniqueID());
      PREDECESSOR_COLUMNS.put("pred_type", Relation::getType);
      PREDECESSOR_COLUMNS.put("lag_hr_cnt", Relation::getLag);
      PREDECESSOR_COLUMNS.put("comments", r -> null);
      PREDECESSOR_COLUMNS.put("float_path", r -> null);
      PREDECESSOR_COLUMNS.put("aref", r -> null);
      PREDECESSOR_COLUMNS.put("arls", r -> null);
   }

   private static final Map<String, ExportFunction<ResourceAssignment>> RESOURCE_ASSIGNMENT_COLUMNS = new LinkedHashMap<>();
   static
   {
      RESOURCE_ASSIGNMENT_COLUMNS.put("taskrsrc_id", ResourceAssignment::getUniqueID);
      RESOURCE_ASSIGNMENT_COLUMNS.put("task_id", ResourceAssignment::getTaskUniqueID);
      RESOURCE_ASSIGNMENT_COLUMNS.put("proj_id", r -> r.getParentFile().getProjectProperties().getUniqueID());
      RESOURCE_ASSIGNMENT_COLUMNS.put("cost_qty_link_flag", ResourceAssignment::getCalculateCostsFromUnits);
      RESOURCE_ASSIGNMENT_COLUMNS.put("role_id", ResourceAssignment::getRoleUniqueID);
      RESOURCE_ASSIGNMENT_COLUMNS.put("acct_id", ResourceAssignment::getCostAccountUniqueID);
      RESOURCE_ASSIGNMENT_COLUMNS.put("rsrc_id", ResourceAssignment::getResourceUniqueID);
      RESOURCE_ASSIGNMENT_COLUMNS.put("pobs_id", r -> null);
      RESOURCE_ASSIGNMENT_COLUMNS.put("skill_level", r -> null);
      RESOURCE_ASSIGNMENT_COLUMNS.put("remain_qty", ResourceAssignment::getRemainingWork);
      RESOURCE_ASSIGNMENT_COLUMNS.put("target_qty", ResourceAssignment::getPlannedWork);
      RESOURCE_ASSIGNMENT_COLUMNS.put("remain_qty_per_hr", r -> null);
      RESOURCE_ASSIGNMENT_COLUMNS.put("target_lag_drtn_hr_cnt", ResourceAssignment::getDelay);
      RESOURCE_ASSIGNMENT_COLUMNS.put("target_qty_per_hr", r -> null);
      RESOURCE_ASSIGNMENT_COLUMNS.put("act_ot_qty", ResourceAssignment::getActualOvertimeWork);
      RESOURCE_ASSIGNMENT_COLUMNS.put("act_reg_qty", PrimaveraXERFileWriter::getActualRegularWork);
      RESOURCE_ASSIGNMENT_COLUMNS.put("relag_drtn_hr_cnt", r -> null);
      RESOURCE_ASSIGNMENT_COLUMNS.put("ot_factor", r -> null);
      RESOURCE_ASSIGNMENT_COLUMNS.put("cost_per_qty", ResourceAssignment::getOverrideRate);
      RESOURCE_ASSIGNMENT_COLUMNS.put("target_cost", ResourceAssignment::getPlannedCost);
      RESOURCE_ASSIGNMENT_COLUMNS.put("act_reg_cost", PrimaveraXERFileWriter::getActualRegularCost);
      RESOURCE_ASSIGNMENT_COLUMNS.put("act_ot_cost", ResourceAssignment::getActualOvertimeCost);
      RESOURCE_ASSIGNMENT_COLUMNS.put("remain_cost", ResourceAssignment::getRemainingCost);
      RESOURCE_ASSIGNMENT_COLUMNS.put("act_start_date", ResourceAssignment::getActualStart);
      RESOURCE_ASSIGNMENT_COLUMNS.put("act_end_date", ResourceAssignment::getActualFinish);
      RESOURCE_ASSIGNMENT_COLUMNS.put("restart_date", r -> null);
      RESOURCE_ASSIGNMENT_COLUMNS.put("reend_date", r -> null);
      RESOURCE_ASSIGNMENT_COLUMNS.put("target_start_date", ResourceAssignment::getPlannedStart);
      RESOURCE_ASSIGNMENT_COLUMNS.put("target_end_date", ResourceAssignment::getPlannedFinish);
      RESOURCE_ASSIGNMENT_COLUMNS.put("rem_late_start_date", r -> null);
      RESOURCE_ASSIGNMENT_COLUMNS.put("rem_late_end_date", r -> null);
      RESOURCE_ASSIGNMENT_COLUMNS.put("rollup_dates_flag", r -> Boolean.TRUE);
      RESOURCE_ASSIGNMENT_COLUMNS.put("target_crv", r -> null);
      RESOURCE_ASSIGNMENT_COLUMNS.put("remain_crv", r -> null);
      RESOURCE_ASSIGNMENT_COLUMNS.put("actual_crv", r -> null);
      RESOURCE_ASSIGNMENT_COLUMNS.put("ts_pend_act_end_flag", r -> Boolean.FALSE);
      RESOURCE_ASSIGNMENT_COLUMNS.put("guid", ResourceAssignment::getGUID);
      RESOURCE_ASSIGNMENT_COLUMNS.put("rate_type", r -> RateTypeHelper.getXerFromInstance(r.getRateIndex()));
      RESOURCE_ASSIGNMENT_COLUMNS.put("act_this_per_cost", r -> null);
      RESOURCE_ASSIGNMENT_COLUMNS.put("act_this_per_qty", r -> null);
      RESOURCE_ASSIGNMENT_COLUMNS.put("curv_id", r -> CurveHelper.getCurveID(r.getWorkContour()));
      RESOURCE_ASSIGNMENT_COLUMNS.put("rsrc_type", r -> r.getResource().getType());
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
      COST_ACCOUNT_COLUMNS.put("acct_short_name", CostAccount::getName);
      COST_ACCOUNT_COLUMNS.put("acct_descr", CostAccount::getDescription);
   }

   private static final Map<String, ExportFunction<ExpenseCategory>> EXPENSE_CATEGORY_COLUMNS = new LinkedHashMap<>();
   static
   {
      EXPENSE_CATEGORY_COLUMNS.put("cost_type_id", ExpenseCategory::getUniqueID);
      EXPENSE_CATEGORY_COLUMNS.put("seq_num", ExpenseCategory::getSequenceNumber);
      EXPENSE_CATEGORY_COLUMNS.put("cost_type", ExpenseCategory::getName);
   }

   private static final Map<String, ExportFunction<ExpenseItem>> EXPENSE_ITEM_COLUMNS = new LinkedHashMap<>();
   static
   {
      EXPENSE_ITEM_COLUMNS.put("cost_item_id", ExpenseItem::getUniqueID);
      EXPENSE_ITEM_COLUMNS.put("acct_id", ExpenseItem::getAccountUniqueID);
      EXPENSE_ITEM_COLUMNS.put("pobs_id", i -> null);
      EXPENSE_ITEM_COLUMNS.put("cost_type_id", i -> i.getCategory().getUniqueID());
      EXPENSE_ITEM_COLUMNS.put("proj_id", i -> i.getTask().getParentFile().getProjectProperties().getUniqueID());
      EXPENSE_ITEM_COLUMNS.put("task_id", i -> i.getTask().getUniqueID());
      EXPENSE_ITEM_COLUMNS.put("cost_name", ExpenseItem::getName);
      EXPENSE_ITEM_COLUMNS.put("po_number", ExpenseItem::getDocumentNumber);
      EXPENSE_ITEM_COLUMNS.put("vendor_name", ExpenseItem::getVendor);
      EXPENSE_ITEM_COLUMNS.put("act_cost", ExpenseItem::getActualCost);
      EXPENSE_ITEM_COLUMNS.put("cost_per_qty", ExpenseItem::getPricePerUnit);
      EXPENSE_ITEM_COLUMNS.put("remain_cost", ExpenseItem::getRemainingCost);
      EXPENSE_ITEM_COLUMNS.put("target_cost", ExpenseItem::getPlannedCost);
      EXPENSE_ITEM_COLUMNS.put("cost_load_type", ExpenseItem::getAccrueType);
      EXPENSE_ITEM_COLUMNS.put("auto_compute_act_flag", ExpenseItem::getAutoComputeActuals);
      EXPENSE_ITEM_COLUMNS.put("target_qty", ExpenseItem::getPlannedUnits);
      EXPENSE_ITEM_COLUMNS.put("qty_name", ExpenseItem::getUnitOfMeasure);
      EXPENSE_ITEM_COLUMNS.put("cost_descr", ExpenseItem::getDescription);
      EXPENSE_ITEM_COLUMNS.put("contract_manager_import", i -> null);
   }

   private static final Map<String, ExportFunction<WorkContour>> RESOURCE_CURVE_COLUMNS = new LinkedHashMap<>();
   static
   {
      RESOURCE_CURVE_COLUMNS.put("curv_id", WorkContour::getUniqueID);
      RESOURCE_CURVE_COLUMNS.put("curv_name", WorkContour::getName);
      RESOURCE_CURVE_COLUMNS.put("default_flag", WorkContour::isContourDefault);
      RESOURCE_CURVE_COLUMNS.put("pct_usage_0", r -> r.getCurveValues()[0]);
      RESOURCE_CURVE_COLUMNS.put("pct_usage_1", r -> r.getCurveValues()[1]);
      RESOURCE_CURVE_COLUMNS.put("pct_usage_2", r -> r.getCurveValues()[2]);
      RESOURCE_CURVE_COLUMNS.put("pct_usage_3", r -> r.getCurveValues()[3]);
      RESOURCE_CURVE_COLUMNS.put("pct_usage_4", r -> r.getCurveValues()[4]);
      RESOURCE_CURVE_COLUMNS.put("pct_usage_5", r -> r.getCurveValues()[5]);
      RESOURCE_CURVE_COLUMNS.put("pct_usage_6", r -> r.getCurveValues()[6]);
      RESOURCE_CURVE_COLUMNS.put("pct_usage_7", r -> r.getCurveValues()[7]);
      RESOURCE_CURVE_COLUMNS.put("pct_usage_8", r -> r.getCurveValues()[8]);
      RESOURCE_CURVE_COLUMNS.put("pct_usage_9", r -> r.getCurveValues()[9]);
      RESOURCE_CURVE_COLUMNS.put("pct_usage_10", r -> r.getCurveValues()[10]);
      RESOURCE_CURVE_COLUMNS.put("pct_usage_11", r -> r.getCurveValues()[11]);
      RESOURCE_CURVE_COLUMNS.put("pct_usage_12", r -> r.getCurveValues()[12]);
      RESOURCE_CURVE_COLUMNS.put("pct_usage_13", r -> r.getCurveValues()[13]);
      RESOURCE_CURVE_COLUMNS.put("pct_usage_14", r -> r.getCurveValues()[14]);
      RESOURCE_CURVE_COLUMNS.put("pct_usage_15", r -> r.getCurveValues()[15]);
      RESOURCE_CURVE_COLUMNS.put("pct_usage_16", r -> r.getCurveValues()[16]);
      RESOURCE_CURVE_COLUMNS.put("pct_usage_17", r -> r.getCurveValues()[17]);
      RESOURCE_CURVE_COLUMNS.put("pct_usage_18", r -> r.getCurveValues()[18]);
      RESOURCE_CURVE_COLUMNS.put("pct_usage_19", r -> r.getCurveValues()[19]);
      RESOURCE_CURVE_COLUMNS.put("pct_usage_20", r -> r.getCurveValues()[20]);
   }

   private static final Map<String, ExportFunction<Step>> ACTIVITY_STEP_COLUMNS = new LinkedHashMap<>();
   static
   {
      ACTIVITY_STEP_COLUMNS.put("proc_id", Step::getUniqueID);
      ACTIVITY_STEP_COLUMNS.put("task_id", s -> s.getTask().getUniqueID());
      ACTIVITY_STEP_COLUMNS.put("proj_id", s -> s.getTask().getParentFile().getProjectProperties().getUniqueID());
      ACTIVITY_STEP_COLUMNS.put("seq_num", Step::getSequenceNumber);
      ACTIVITY_STEP_COLUMNS.put("proc_name", Step::getName);
      ACTIVITY_STEP_COLUMNS.put("complete_flag", Step::getComplete);
      ACTIVITY_STEP_COLUMNS.put("proc_wt", Step::getWeight);
      ACTIVITY_STEP_COLUMNS.put("complete_pct", Step::getPercentComplete);
      ACTIVITY_STEP_COLUMNS.put("proc_descr", Step::getDescriptionObject);
   }

   private static final Map<String, ExportFunction<ActivityCode>> ACTIVITY_CODE_COLUMNS = new LinkedHashMap<>();
   static
   {
      ACTIVITY_CODE_COLUMNS.put("actv_code_type_id", ActivityCode::getUniqueID);
      ACTIVITY_CODE_COLUMNS.put("actv_short_len", ActivityCode::getMaxLength);
      ACTIVITY_CODE_COLUMNS.put("seq_num", ActivityCode::getSequenceNumber);
      ACTIVITY_CODE_COLUMNS.put("actv_code_type", ActivityCode::getName);
      ACTIVITY_CODE_COLUMNS.put("proj_id", ActivityCode::getScopeProjectUniqueID);
      ACTIVITY_CODE_COLUMNS.put("wbs_id", ActivityCode::getScopeEpsUniqueID);
      ACTIVITY_CODE_COLUMNS.put("actv_code_type_scope", ActivityCode::getScope);
   }

   private static final Map<String, ExportFunction<ActivityCodeValue>> ACTIVITY_CODE_VALUE_COLUMNS = new LinkedHashMap<>();
   static
   {
      ACTIVITY_CODE_VALUE_COLUMNS.put("actv_code_id", ActivityCodeValue::getUniqueID);
      ACTIVITY_CODE_VALUE_COLUMNS.put("parent_actv_code_id", ActivityCodeValue::getParentUniqueID);
      ACTIVITY_CODE_VALUE_COLUMNS.put("actv_code_type_id", a -> a.getType().getUniqueID());
      ACTIVITY_CODE_VALUE_COLUMNS.put("actv_code_name", ActivityCodeValue::getDescription);
      ACTIVITY_CODE_VALUE_COLUMNS.put("short_name", ActivityCodeValue::getName);
      ACTIVITY_CODE_VALUE_COLUMNS.put("seq_num", ActivityCodeValue::getSequenceNumber);
      ACTIVITY_CODE_VALUE_COLUMNS.put("color", ActivityCodeValue::getColor);
      ACTIVITY_CODE_VALUE_COLUMNS.put("total_assignments", a -> null);
   }

   private static final Map<String, ExportFunction<Pair<Task, ActivityCodeValue>>> ACTIVITY_CODE_ASSIGNMENT_COLUMNS = new LinkedHashMap<>();
   static
   {
      ACTIVITY_CODE_ASSIGNMENT_COLUMNS.put("task_id", p -> p.getFirst().getUniqueID());
      ACTIVITY_CODE_ASSIGNMENT_COLUMNS.put("actv_code_type_id", p -> p.getSecond().getType().getUniqueID());
      ACTIVITY_CODE_ASSIGNMENT_COLUMNS.put("actv_code_id", p -> p.getSecond().getUniqueID());
      ACTIVITY_CODE_ASSIGNMENT_COLUMNS.put("proj_id", p -> p.getFirst().getParentFile().getProjectProperties().getUniqueID());
   }

   private static final Map<String, ExportFunction<ActivityCodeValue>> UDF_TYPE_COLUMNS = new LinkedHashMap<>();
   static
   {
      UDF_TYPE_COLUMNS.put("udf_type_id", u -> null);
      UDF_TYPE_COLUMNS.put("table_name", u -> null);
      UDF_TYPE_COLUMNS.put("udf_type_name", u -> null);
      UDF_TYPE_COLUMNS.put("udf_type_label", u -> null);
      UDF_TYPE_COLUMNS.put("logical_data_type", u -> null);
      UDF_TYPE_COLUMNS.put("super_flag", u -> null);
      UDF_TYPE_COLUMNS.put("indicator_expression", u -> null);
      UDF_TYPE_COLUMNS.put("summary_indicator_expression", u -> null);
   }

   private static final Map<Class<?>, FormatFunction> FORMAT_MAP = new HashMap<>();
   static
   {
      FORMAT_MAP.put(Date.class, (w, o) -> w.m_timestampFormat.format(o));
      FORMAT_MAP.put(Double.class, (w, o) -> w.m_doubleFormat.format(o));
      FORMAT_MAP.put(Boolean.class, (w, o) -> ((Boolean) o).booleanValue() ? "Y" : "N");
      FORMAT_MAP.put(Rate.class, (w, o) -> w.formatRate((Rate) o));
      FORMAT_MAP.put(UUID.class, (w, o) -> w.formatUUID((UUID)o));
      FORMAT_MAP.put(ResourceType.class, (w, o) -> ResourceTypeHelper.getXerFromInstance((ResourceType)o));
      FORMAT_MAP.put(CriticalActivityType.class, (w, o) -> CriticalActivityTypeHelper.getXerFromInstance((CriticalActivityType)o));
      FORMAT_MAP.put(TaskType.class, (w, o) -> TaskTypeHelper.getXerFromInstance((TaskType) o));
      FORMAT_MAP.put(CalendarType.class, (w, o) -> CalendarTypeHelper.getXerFromInstance((CalendarType)o));
      FORMAT_MAP.put(ActivityType.class, (w, o) -> ActivityTypeHelper.getXerFromInstance((ActivityType)o));
      FORMAT_MAP.put(PercentCompleteType.class, (w, o) -> PercentCompleteTypeHelper.getXerFromInstance((PercentCompleteType)o));
      FORMAT_MAP.put(ActivityStatus.class, (w, o) -> ActivityStatusHelper.getXerFromInstance((ActivityStatus)o));
      FORMAT_MAP.put(Duration.class, (w, o) -> w.formatDuration((Duration)o));
      FORMAT_MAP.put(ConstraintType.class, (w, o) -> ConstraintTypeHelper.getXerFromInstance((ConstraintType)o));
      FORMAT_MAP.put(Priority.class, (w, o) -> PriorityHelper.getXerFromInstance((Priority)o));
      FORMAT_MAP.put(RelationType.class, (w, o) -> RelationTypeHelper.getXerFromInstance((RelationType)o));
      FORMAT_MAP.put(AccrueType.class, (w, o) -> AccrueTypeHelper.getXerFromInstance((AccrueType)o));
      FORMAT_MAP.put(ActivityCodeScope.class, (w, o) -> ActivityCodeScopeHelper.getXerFromInstance((ActivityCodeScope)o));
      FORMAT_MAP.put(Color.class, (w, o) -> ColorHelper.getHexColor((Color)o));
      FORMAT_MAP.put(RateSource.class, (w, o) -> RateSourceHelper.getXerFromInstance((RateSource)o));
   }
}
