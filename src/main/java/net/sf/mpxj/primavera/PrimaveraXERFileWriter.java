package net.sf.mpxj.primavera;
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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.sf.mpxj.ActivityStatus;
import net.sf.mpxj.ActivityType;
import net.sf.mpxj.AssignmentField;
import net.sf.mpxj.Availability;
import net.sf.mpxj.CalendarType;
import net.sf.mpxj.ConstraintType;
import net.sf.mpxj.CostAccount;
import net.sf.mpxj.CostRateTableEntry;
import net.sf.mpxj.CriticalActivityType;
import net.sf.mpxj.Duration;
import net.sf.mpxj.ExpenseCategory;
import net.sf.mpxj.FieldContainer;
import net.sf.mpxj.FieldType;
import net.sf.mpxj.HtmlNotes;
import net.sf.mpxj.Notes;
import net.sf.mpxj.PercentCompleteType;
import net.sf.mpxj.Priority;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectField;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ProjectProperties;
import net.sf.mpxj.Rate;
import net.sf.mpxj.Relation;
import net.sf.mpxj.RelationType;
import net.sf.mpxj.Resource;
import net.sf.mpxj.ResourceAssignment;
import net.sf.mpxj.ResourceField;
import net.sf.mpxj.ResourceType;
import net.sf.mpxj.Task;
import net.sf.mpxj.TaskField;
import net.sf.mpxj.TaskType;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.common.CharsetHelper;
import net.sf.mpxj.common.HtmlHelper;
import net.sf.mpxj.common.NumberHelper;
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
         writeCostAccounts();
         writeRoles();
         writeProject();
         writeRoleRates();
         writeCalendars();
         writeWBS();
         writeResources();
         writeResourceRates();
         writeActivities();
         writePredecessors();
         writeResourceAssignments();

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

   private void writeCostRateTableEntries(Map<String, Object> columns, Resource resource)
   {
      resource.getCostRateTable(0).stream().filter(e -> e != CostRateTableEntry.DEFAULT_ENTRY).forEach(e -> writeCostRateTableEntry(columns, resource, e));
   }

   private void writeCostRateTableEntry(Map<String, Object> columns, Resource resource, CostRateTableEntry entry)
   {
      writeRecord(columns.values().stream().map(f -> f instanceof CostRateTableEntryFunction ? ((CostRateTableEntryFunction)f).apply(this, resource, entry) : f));
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

   private void writeRecord(Map<String, ExportFunction> columns, Object object)
   {
      writeRecord(columns.values().stream().map(f -> f.apply(object)));
   }

   private void writeRecord(Map<String, Object> columns, FieldContainer container)
   {
      writeRecord(columns.values().stream().map(c -> getData(container, c)));
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

   private Object getData(FieldContainer container, Object key)
   {
      if (key instanceof FieldType)
      {
         return container.get((FieldType) key);
      }

      if (key instanceof ExportFunction)
      {
         return ((ExportFunction)key).apply(container);
      }

      return key;
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

   private interface ExportFunction
   {
      Object apply(Object source);
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

   private static final Map<String, Object> ROLE_COLUMNS = new LinkedHashMap<>();
   static
   {
      ROLE_COLUMNS.put("role_id", ResourceField.UNIQUE_ID);
      ROLE_COLUMNS.put("parent_role_id", ResourceField.PARENT_ID);
      ROLE_COLUMNS.put("seq_num", ResourceField.SEQUENCE_NUMBER);
      ROLE_COLUMNS.put("role_name", ResourceField.NAME);
      ROLE_COLUMNS.put("role_short_name", ResourceField.RESOURCE_ID);
      ROLE_COLUMNS.put("pobs_id", "");
      ROLE_COLUMNS.put("def_cost_qty_link_flag", ResourceField.CALCULATE_COSTS_FROM_UNITS);
      ROLE_COLUMNS.put("cost_qty_type", "QT_Hour");
      ROLE_COLUMNS.put("role_descr", ResourceField.NOTES);
      ROLE_COLUMNS.put("last_checksum", "");
   }

   private static final Map<String, Object> ROLE_RATE_COLUMNS = new LinkedHashMap<>();
   static
   {
      ROLE_RATE_COLUMNS.put("role_rate_id", (CostRateTableEntryFunction)(PrimaveraXERFileWriter w, Resource r, CostRateTableEntry e) -> Integer.valueOf(w.m_roleRateUniqueID++));
      ROLE_RATE_COLUMNS.put("role_id", (CostRateTableEntryFunction)(PrimaveraXERFileWriter w, Resource r, CostRateTableEntry e) -> r.getUniqueID());
      ROLE_RATE_COLUMNS.put("cost_per_qty", (CostRateTableEntryFunction)(PrimaveraXERFileWriter w, Resource r, CostRateTableEntry e) -> e.getRate(0));
      ROLE_RATE_COLUMNS.put("cost_per_qty2", (CostRateTableEntryFunction)(PrimaveraXERFileWriter w, Resource r, CostRateTableEntry e) -> e.getRate(1));
      ROLE_RATE_COLUMNS.put("cost_per_qty3", (CostRateTableEntryFunction)(PrimaveraXERFileWriter w, Resource r, CostRateTableEntry e) -> e.getRate(2));
      ROLE_RATE_COLUMNS.put("cost_per_qty4", (CostRateTableEntryFunction)(PrimaveraXERFileWriter w, Resource r, CostRateTableEntry e) -> e.getRate(3));
      ROLE_RATE_COLUMNS.put("cost_per_qty5", (CostRateTableEntryFunction)(PrimaveraXERFileWriter w, Resource r, CostRateTableEntry e) -> e.getRate(4));
      ROLE_RATE_COLUMNS.put("start_date", (CostRateTableEntryFunction)(PrimaveraXERFileWriter w, Resource r, CostRateTableEntry e) -> e.getStartDate());
      ROLE_RATE_COLUMNS.put("max_qty_per_hr", (CostRateTableEntryFunction)PrimaveraXERFileWriter::getMaxQuantityPerHour);
   }

   private static final Map<String, Object> RESOURCE_RATE_COLUMNS = new LinkedHashMap<>();
   static
   {
      RESOURCE_RATE_COLUMNS.put("rsrc_rate_id", (CostRateTableEntryFunction)(PrimaveraXERFileWriter w, Resource r, CostRateTableEntry e) -> Integer.valueOf(w.m_resourceRateUniqueID++));
      RESOURCE_RATE_COLUMNS.put("rsrc_id", (CostRateTableEntryFunction)(PrimaveraXERFileWriter w, Resource r, CostRateTableEntry e) -> r.getUniqueID());
      RESOURCE_RATE_COLUMNS.put("max_qty_per_hr", (CostRateTableEntryFunction)PrimaveraXERFileWriter::getMaxQuantityPerHour);
      RESOURCE_RATE_COLUMNS.put("cost_per_qty", (CostRateTableEntryFunction)(PrimaveraXERFileWriter w, Resource r, CostRateTableEntry e) -> e.getRate(0));
      RESOURCE_RATE_COLUMNS.put("start_date", (CostRateTableEntryFunction)(PrimaveraXERFileWriter w, Resource r, CostRateTableEntry e) -> e.getStartDate());
      RESOURCE_RATE_COLUMNS.put("shift_period_id", "");
      RESOURCE_RATE_COLUMNS.put("cost_per_qty2", (CostRateTableEntryFunction)(PrimaveraXERFileWriter w, Resource r, CostRateTableEntry e) -> e.getRate(1));
      RESOURCE_RATE_COLUMNS.put("cost_per_qty3", (CostRateTableEntryFunction)(PrimaveraXERFileWriter w, Resource r, CostRateTableEntry e) -> e.getRate(2));
      RESOURCE_RATE_COLUMNS.put("cost_per_qty4", (CostRateTableEntryFunction)(PrimaveraXERFileWriter w, Resource r, CostRateTableEntry e) -> e.getRate(3));
      RESOURCE_RATE_COLUMNS.put("cost_per_qty5", (CostRateTableEntryFunction)(PrimaveraXERFileWriter w, Resource r, CostRateTableEntry e) -> e.getRate(4));
   }

   private static final Map<String, Object> RESOURCE_COLUMNS = new LinkedHashMap<>();
   static
   {
      RESOURCE_COLUMNS.put("rsrc_id", ResourceField.UNIQUE_ID);
      RESOURCE_COLUMNS.put("parent_rsrc_id", ResourceField.PARENT_ID);
      RESOURCE_COLUMNS.put("clndr_id", ResourceField.CALENDAR_UNIQUE_ID);
      RESOURCE_COLUMNS.put("role_id", "");
      RESOURCE_COLUMNS.put("shift_id", "");
      RESOURCE_COLUMNS.put("user_id", "");
      RESOURCE_COLUMNS.put("pobs_id", "");
      RESOURCE_COLUMNS.put("guid", ResourceField.GUID);
      RESOURCE_COLUMNS.put("rsrc_seq_num", ResourceField.SEQUENCE_NUMBER);
      RESOURCE_COLUMNS.put("email_addr", ResourceField.EMAIL_ADDRESS);
      RESOURCE_COLUMNS.put("employee_code", ResourceField.CODE);
      RESOURCE_COLUMNS.put("office_phone", "");
      RESOURCE_COLUMNS.put("other_phone", "");
      RESOURCE_COLUMNS.put("rsrc_name", ResourceField.NAME);
      RESOURCE_COLUMNS.put("rsrc_short_name", ResourceField.RESOURCE_ID);
      RESOURCE_COLUMNS.put("rsrc_title_name", "");
      RESOURCE_COLUMNS.put("def_qty_per_hr", (ExportFunction)(r) -> ((Resource)r).getMaxUnits() == null ? null : ((Resource)r).getMaxUnits().doubleValue() / 100.0);
      RESOURCE_COLUMNS.put("cost_qty_type", "QT_Hour");
      RESOURCE_COLUMNS.put("ot_factor", "");
      RESOURCE_COLUMNS.put("active_flag", ResourceField.ACTIVE);
      RESOURCE_COLUMNS.put("auto_compute_act_flag", Boolean.TRUE);
      RESOURCE_COLUMNS.put("def_cost_qty_link_flag", ResourceField.CALCULATE_COSTS_FROM_UNITS);
      RESOURCE_COLUMNS.put("ot_flag", Boolean.FALSE);
      RESOURCE_COLUMNS.put("curr_id", CURRENCY_COLUMNS.get("curr_id"));
      RESOURCE_COLUMNS.put("unit_id", "");
      RESOURCE_COLUMNS.put("rsrc_type", ResourceField.TYPE);
      RESOURCE_COLUMNS.put("location_id", "");
      RESOURCE_COLUMNS.put("rsrc_notes", ResourceField.NOTES);
      RESOURCE_COLUMNS.put("load_tasks_flag", "");
      RESOURCE_COLUMNS.put("level_flag", "");
      RESOURCE_COLUMNS.put("last_checksum", "");
   }

   private static final Map<String, Object> PROJECT_COLUMNS = new LinkedHashMap<>();
   static
   {
      PROJECT_COLUMNS.put("proj_id", ProjectField.UNIQUE_ID);
      PROJECT_COLUMNS.put("fy_start_month_num", ProjectField.FISCAL_YEAR_START_MONTH);
      PROJECT_COLUMNS.put("rsrc_self_add_flag", Boolean.TRUE);
      PROJECT_COLUMNS.put("allow_complete_flag", Boolean.TRUE);
      PROJECT_COLUMNS.put("rsrc_multi_assign_flag", Boolean.TRUE);
      PROJECT_COLUMNS.put("checkout_flag", Boolean.FALSE);
      PROJECT_COLUMNS.put("project_flag", Boolean.TRUE);
      PROJECT_COLUMNS.put("step_complete_flag", Boolean.TRUE);
      PROJECT_COLUMNS.put("cost_qty_recalc_flag", Boolean.TRUE);
      PROJECT_COLUMNS.put("batch_sum_flag", Boolean.TRUE);
      PROJECT_COLUMNS.put("name_sep_char", ".");
      PROJECT_COLUMNS.put("def_complete_pct_type", PercentCompleteType.DURATION);
      PROJECT_COLUMNS.put("proj_short_name", ProjectField.PROJECT_ID);
      PROJECT_COLUMNS.put("acct_id", "");
      PROJECT_COLUMNS.put("orig_proj_id", "");
      PROJECT_COLUMNS.put("source_proj_id", "");
      PROJECT_COLUMNS.put("base_type_id", "");
      PROJECT_COLUMNS.put("clndr_id", ProjectField.DEFAULT_CALENDAR_UNIQUE_ID);
      PROJECT_COLUMNS.put("sum_base_proj_id", ProjectField.BASELINE_PROJECT_UNIQUE_ID);
      PROJECT_COLUMNS.put("task_code_base", Integer.valueOf(1000));
      PROJECT_COLUMNS.put("task_code_step", Integer.valueOf(10));
      PROJECT_COLUMNS.put("priority_num", Integer.valueOf(10));
      PROJECT_COLUMNS.put("wbs_max_sum_level", Integer.valueOf(0));
      PROJECT_COLUMNS.put("strgy_priority_num", Integer.valueOf(100));
      PROJECT_COLUMNS.put("last_checksum", "");
      PROJECT_COLUMNS.put("critical_drtn_hr_cnt", (ExportFunction)o -> ((ProjectProperties)o).getCriticalSlackLimit().convertUnits(TimeUnit.HOURS, (ProjectProperties)o).getDuration());
      PROJECT_COLUMNS.put("def_cost_per_qty", Double.valueOf(100.0));
      PROJECT_COLUMNS.put("last_recalc_date", ProjectField.STATUS_DATE);
      PROJECT_COLUMNS.put("plan_start_date", ProjectField.PLANNED_START);
      PROJECT_COLUMNS.put("plan_end_date", ProjectField.MUST_FINISH_BY);
      PROJECT_COLUMNS.put("scd_end_date", ProjectField.SCHEDULED_FINISH);
      PROJECT_COLUMNS.put("add_date", ProjectField.CREATION_DATE);
      PROJECT_COLUMNS.put("last_tasksum_date", "");
      PROJECT_COLUMNS.put("fcst_start_date", "");
      PROJECT_COLUMNS.put("def_duration_type", ProjectField.DEFAULT_TASK_TYPE);
      PROJECT_COLUMNS.put("task_code_prefix", "");
      PROJECT_COLUMNS.put("guid", ProjectField.GUID);
      PROJECT_COLUMNS.put("def_qty_type", "QT_Hour");
      PROJECT_COLUMNS.put("add_by_name", "admin");
      PROJECT_COLUMNS.put("web_local_root_path", "");
      PROJECT_COLUMNS.put("proj_url", "");
      PROJECT_COLUMNS.put("def_rate_type", "COST_PER_QTY");
      PROJECT_COLUMNS.put("add_act_remain_flag", Boolean.FALSE);
      PROJECT_COLUMNS.put("act_this_per_link_flag", Boolean.TRUE);
      PROJECT_COLUMNS.put("def_task_type", ActivityType.TASK_DEPENDENT);
      PROJECT_COLUMNS.put("act_pct_link_flag",Boolean.FALSE);
      PROJECT_COLUMNS.put("critical_path_type", ProjectField.CRITICAL_ACTIVITY_TYPE);
      PROJECT_COLUMNS.put("task_code_prefix_flag", Boolean.TRUE);
      PROJECT_COLUMNS.put("def_rollup_dates_flag", Boolean.TRUE);
      PROJECT_COLUMNS.put("use_project_baseline_flag", Boolean.TRUE);
      PROJECT_COLUMNS.put("rem_target_link_flag", Boolean.TRUE);
      PROJECT_COLUMNS.put("reset_planned_flag", Boolean.FALSE);
      PROJECT_COLUMNS.put("allow_neg_act_flag", Boolean.FALSE);
      PROJECT_COLUMNS.put("sum_assign_level", "SL_Taskrsrc");
      PROJECT_COLUMNS.put("last_fin_dates_id", "");
      PROJECT_COLUMNS.put("fintmpl_id", "");
      PROJECT_COLUMNS.put("last_baseline_update_date", "");
      PROJECT_COLUMNS.put("cr_external_key", "");
      PROJECT_COLUMNS.put("apply_actuals_date", "");
      PROJECT_COLUMNS.put("location_id", "");
      PROJECT_COLUMNS.put("loaded_scope_level", Integer.valueOf(7));
      PROJECT_COLUMNS.put("export_flag", ProjectField.EXPORT_FLAG);
      PROJECT_COLUMNS.put("new_fin_dates_id", "");
      PROJECT_COLUMNS.put("baselines_to_export", "");
      PROJECT_COLUMNS.put("baseline_names_to_export", "");
      PROJECT_COLUMNS.put("next_data_date", "");
      PROJECT_COLUMNS.put("close_period_flag", "");
      PROJECT_COLUMNS.put("sum_refresh_date", "");
      PROJECT_COLUMNS.put("trsrcsum_loaded", "");
      PROJECT_COLUMNS.put("sumtask_loaded", "");
   }

   private static final Map<String, ExportFunction> CALENDAR_COLUMNS = new LinkedHashMap<>();
   static
   {
      CALENDAR_COLUMNS.put("clndr_id", c -> ((ProjectCalendar)c).getUniqueID());
      CALENDAR_COLUMNS.put("default_flag", c -> ((ProjectCalendar)c).getParentFile().getProjectProperties().getDefaultCalendar() == c);
      CALENDAR_COLUMNS.put("clndr_name", c -> ((ProjectCalendar)c).getName());
      CALENDAR_COLUMNS.put("proj_id", c -> ((ProjectCalendar)c).getType() == CalendarType.PROJECT ? ((ProjectCalendar) c).getParentFile().getProjectProperties().getUniqueID() : null);
      CALENDAR_COLUMNS.put("base_clndr_id", c -> ((ProjectCalendar)c).getParent() == null ? null : ((ProjectCalendar)c).getParent().getUniqueID());
      CALENDAR_COLUMNS.put("last_chng_date", c -> null);
      CALENDAR_COLUMNS.put("clndr_type", c -> ((ProjectCalendar)c).getType());
      CALENDAR_COLUMNS.put("day_hr_cnt", c -> Integer.valueOf(NumberHelper.getInt(((ProjectCalendar)c).getMinutesPerDay()) / 60));
      CALENDAR_COLUMNS.put("week_hr_cnt", c -> Integer.valueOf(NumberHelper.getInt(((ProjectCalendar)c).getMinutesPerWeek()) / 60));
      CALENDAR_COLUMNS.put("month_hr_cnt", c -> Integer.valueOf(NumberHelper.getInt(((ProjectCalendar)c).getMinutesPerMonth()) / 60));
      CALENDAR_COLUMNS.put("year_hr_cnt", c -> Integer.valueOf(NumberHelper.getInt(((ProjectCalendar)c).getMinutesPerYear()) / 60));
      CALENDAR_COLUMNS.put("rsrc_private", c -> ((ProjectCalendar)c).getPersonal());
      CALENDAR_COLUMNS.put("clndr_data", c -> new ProjectCalendarStructuredTextWriter().getCalendarData((ProjectCalendar)c));
   }

   private static final Map<String, Object> WBS_COLUMNS = new LinkedHashMap<>();
   static
   {
      WBS_COLUMNS.put("wbs_id", TaskField.UNIQUE_ID);
      WBS_COLUMNS.put("proj_id", (ExportFunction)t -> ((Task)t).getParentFile().getProjectProperties().getUniqueID() );
      WBS_COLUMNS.put("obs_id", "");
      WBS_COLUMNS.put("seq_num", TaskField.SEQUENCE_NUMBER);
      WBS_COLUMNS.put("est_wt", Integer.valueOf(1));
      WBS_COLUMNS.put("proj_node_flag", Boolean.FALSE);
      WBS_COLUMNS.put("sum_data_flag", Boolean.TRUE);
      WBS_COLUMNS.put("status_code", "WS_Open");
      WBS_COLUMNS.put("wbs_short_name", (ExportFunction)t -> TaskHelper.getWbsCode((Task)t));
      WBS_COLUMNS.put("wbs_name", TaskField.NAME);
      WBS_COLUMNS.put("phase_id", "");
      WBS_COLUMNS.put("parent_wbs_id", TaskField.PARENT_TASK_UNIQUE_ID);
      WBS_COLUMNS.put("ev_user_pct", TaskField.PLANNED_COST);
      WBS_COLUMNS.put("ev_etc_user_value", "");
      WBS_COLUMNS.put("orig_cost", "");
      WBS_COLUMNS.put("indep_remain_total_cost", "");
      WBS_COLUMNS.put("ann_dscnt_rate_pct", "");
      WBS_COLUMNS.put("dscnt_period_type", "");
      WBS_COLUMNS.put("indep_remain_work_qty", "");
      WBS_COLUMNS.put("anticip_start_date", "");
      WBS_COLUMNS.put("anticip_end_date", "");
      WBS_COLUMNS.put("ev_compute_type", "EC_Cmp_pct");
      WBS_COLUMNS.put("ev_etc_compute_type", "EC_Cmp_pct");
      WBS_COLUMNS.put("guid", TaskField.GUID);
      WBS_COLUMNS.put("tmpl_guid", "");
      WBS_COLUMNS.put("plan_open_state", "");
   }

   private static final Map<String, Object> ACTIVITY_COLUMNS = new LinkedHashMap<>();
   static
   {
      ACTIVITY_COLUMNS.put("task_id", TaskField.UNIQUE_ID);
      ACTIVITY_COLUMNS.put("proj_id", (ExportFunction)t -> ((Task)t).getParentFile().getProjectProperties().getUniqueID());
      ACTIVITY_COLUMNS.put("wbs_id",TaskField.PARENT_TASK_UNIQUE_ID);
      ACTIVITY_COLUMNS.put("clndr_id", TaskField.CALENDAR_UNIQUE_ID);
      ACTIVITY_COLUMNS.put("phys_complete_pct", TaskField.PHYSICAL_PERCENT_COMPLETE);
      ACTIVITY_COLUMNS.put("rev_fdbk_flag", Boolean.FALSE);
      ACTIVITY_COLUMNS.put("est_wt", Integer.valueOf(1));
      ACTIVITY_COLUMNS.put("lock_plan_flag", Boolean.FALSE);
      ACTIVITY_COLUMNS.put("auto_compute_act_flag", Boolean.TRUE);
      ACTIVITY_COLUMNS.put("complete_pct_type", TaskField.PERCENT_COMPLETE_TYPE);
      ACTIVITY_COLUMNS.put("task_type", TaskField.ACTIVITY_TYPE);
      ACTIVITY_COLUMNS.put("duration_type", TaskField.TYPE);
      ACTIVITY_COLUMNS.put("status_code", (ExportFunction)t -> ActivityStatusHelper.getActivityStatus((Task)t));
      ACTIVITY_COLUMNS.put("task_code", TaskField.ACTIVITY_ID);
      ACTIVITY_COLUMNS.put("task_name", TaskField.NAME);
      ACTIVITY_COLUMNS.put("rsrc_id", TaskField.PRIMARY_RESOURCE_ID);

      // TODO: should be blank if complete
      ACTIVITY_COLUMNS.put("total_float_hr_cnt", TaskField.TOTAL_SLACK);
      ACTIVITY_COLUMNS.put("free_float_hr_cnt", TaskField.FREE_SLACK);

      ACTIVITY_COLUMNS.put("remain_drtn_hr_cnt", TaskField.REMAINING_DURATION);
      ACTIVITY_COLUMNS.put("act_work_qty", TaskField.ACTUAL_WORK);
      ACTIVITY_COLUMNS.put("remain_work_qty", TaskField.REMAINING_WORK);
      ACTIVITY_COLUMNS.put("target_work_qty", TaskField.PLANNED_WORK);
      ACTIVITY_COLUMNS.put("target_drtn_hr_cnt", TaskField.PLANNED_DURATION);
      ACTIVITY_COLUMNS.put("target_equip_qty", Integer.valueOf(0));
      ACTIVITY_COLUMNS.put("act_equip_qty", Integer.valueOf(0));
      ACTIVITY_COLUMNS.put("remain_equip_qty", Integer.valueOf(0));
      ACTIVITY_COLUMNS.put("cstr_date", TaskField.CONSTRAINT_DATE);
      ACTIVITY_COLUMNS.put("act_start_date", TaskField.ACTUAL_START);
      ACTIVITY_COLUMNS.put("act_end_date", TaskField.ACTUAL_FINISH);
      ACTIVITY_COLUMNS.put("late_start_date", TaskField.LATE_START);
      ACTIVITY_COLUMNS.put("late_end_date", TaskField.LATE_FINISH);
      ACTIVITY_COLUMNS.put("expect_end_date", null);
      ACTIVITY_COLUMNS.put("early_start_date", TaskField.EARLY_START);
      ACTIVITY_COLUMNS.put("early_end_date", TaskField.EARLY_FINISH);
      ACTIVITY_COLUMNS.put("restart_date", TaskField.REMAINING_EARLY_START);
      ACTIVITY_COLUMNS.put("reend_date", TaskField.REMAINING_EARLY_FINISH);
      ACTIVITY_COLUMNS.put("target_start_date", TaskField.PLANNED_START);
      ACTIVITY_COLUMNS.put("target_end_date", TaskField.PLANNED_FINISH);
      ACTIVITY_COLUMNS.put("rem_late_start_date", TaskField.REMAINING_LATE_START);
      ACTIVITY_COLUMNS.put("rem_late_end_date", TaskField.REMAINING_LATE_FINISH);
      ACTIVITY_COLUMNS.put("cstr_type", TaskField.CONSTRAINT_TYPE);
      ACTIVITY_COLUMNS.put("priority_type", TaskField.PRIORITY);
      ACTIVITY_COLUMNS.put("suspend_date", TaskField.SUSPEND_DATE);
      ACTIVITY_COLUMNS.put("resume_date", TaskField.RESUME);
      ACTIVITY_COLUMNS.put("float_path", null);
      ACTIVITY_COLUMNS.put("float_path_order", null);
      ACTIVITY_COLUMNS.put("guid", TaskField.GUID);
      ACTIVITY_COLUMNS.put("tmpl_guid", null);
      ACTIVITY_COLUMNS.put("cstr_date2", TaskField.SECONDARY_CONSTRAINT_DATE);
      ACTIVITY_COLUMNS.put("cstr_type2", TaskField.SECONDARY_CONSTRAINT_TYPE);
      ACTIVITY_COLUMNS.put("driving_path_flag", null);
      ACTIVITY_COLUMNS.put("act_this_per_work_qty", null);
      ACTIVITY_COLUMNS.put("act_this_per_equip_qty", null);
      ACTIVITY_COLUMNS.put("external_early_start_date", null);
      ACTIVITY_COLUMNS.put("external_late_end_date", null);
      ACTIVITY_COLUMNS.put("create_date", TaskField.CREATED);
      ACTIVITY_COLUMNS.put("update_date", null);
      ACTIVITY_COLUMNS.put("create_user", null);
      ACTIVITY_COLUMNS.put("update_user", null);
      ACTIVITY_COLUMNS.put("location_id", null);
   }

   private static final Map<String, ExportFunction> PREDECESSOR_COLUMNS = new LinkedHashMap<>();
   static
   {
      PREDECESSOR_COLUMNS.put("task_pred_id", r -> ((Relation)r).getUniqueID());
      PREDECESSOR_COLUMNS.put("task_id", r -> ((Relation)r).getSourceTask().getUniqueID());
      PREDECESSOR_COLUMNS.put("pred_task_id", r -> ((Relation)r).getTargetTask().getUniqueID());
      PREDECESSOR_COLUMNS.put("proj_id", r -> ((Relation)r).getSourceTask().getParentFile().getProjectProperties().getUniqueID());
      PREDECESSOR_COLUMNS.put("pred_proj_id", r -> ((Relation)r).getTargetTask().getParentFile().getProjectProperties().getUniqueID());
      PREDECESSOR_COLUMNS.put("pred_type", r -> ((Relation)r).getType());
      PREDECESSOR_COLUMNS.put("lag_hr_cnt", r -> ((Relation)r).getLag());
      PREDECESSOR_COLUMNS.put("comments", r -> null);
      PREDECESSOR_COLUMNS.put("float_path", r -> null);
      PREDECESSOR_COLUMNS.put("aref", r -> null);
      PREDECESSOR_COLUMNS.put("arls", r -> null);
   }

   private static final Map<String, Object> RESOURCE_ASSIGNMENT_COLUMNS = new LinkedHashMap<>();
   static
   {
      RESOURCE_ASSIGNMENT_COLUMNS.put("taskrsrc_id", AssignmentField.UNIQUE_ID);
      RESOURCE_ASSIGNMENT_COLUMNS.put("task_id", AssignmentField.TASK_UNIQUE_ID);
      RESOURCE_ASSIGNMENT_COLUMNS.put("proj_id", (ExportFunction)a -> ((ResourceAssignment)a).getParentFile().getProjectProperties().getUniqueID());
      RESOURCE_ASSIGNMENT_COLUMNS.put("cost_qty_link_flag", AssignmentField.CALCULATE_COSTS_FROM_UNITS);
      RESOURCE_ASSIGNMENT_COLUMNS.put("role_id", AssignmentField.ROLE_UNIQUE_ID);
      RESOURCE_ASSIGNMENT_COLUMNS.put("acct_id", AssignmentField.COST_ACCOUNT_UNIQUE_ID);
      RESOURCE_ASSIGNMENT_COLUMNS.put("rsrc_id", AssignmentField.RESOURCE_UNIQUE_ID);
      RESOURCE_ASSIGNMENT_COLUMNS.put("pobs_id", null);
      RESOURCE_ASSIGNMENT_COLUMNS.put("skill_level", null);
      RESOURCE_ASSIGNMENT_COLUMNS.put("remain_qty", AssignmentField.REMAINING_WORK);
      RESOURCE_ASSIGNMENT_COLUMNS.put("target_qty", AssignmentField.PLANNED_WORK);
      RESOURCE_ASSIGNMENT_COLUMNS.put("remain_qty_per_hr", null);
      RESOURCE_ASSIGNMENT_COLUMNS.put("target_lag_drtn_hr_cnt", AssignmentField.ASSIGNMENT_DELAY);
      RESOURCE_ASSIGNMENT_COLUMNS.put("target_qty_per_hr", null);
      RESOURCE_ASSIGNMENT_COLUMNS.put("act_ot_qty", AssignmentField.ACTUAL_OVERTIME_WORK);
      RESOURCE_ASSIGNMENT_COLUMNS.put("act_reg_qty", (ExportFunction)a -> getActualRegularWork((ResourceAssignment)a));
      RESOURCE_ASSIGNMENT_COLUMNS.put("relag_drtn_hr_cnt", null);
      RESOURCE_ASSIGNMENT_COLUMNS.put("ot_factor", null);
      RESOURCE_ASSIGNMENT_COLUMNS.put("cost_per_qty", AssignmentField.OVERRIDE_RATE);
      RESOURCE_ASSIGNMENT_COLUMNS.put("target_cost", AssignmentField.PLANNED_COST);
      RESOURCE_ASSIGNMENT_COLUMNS.put("act_reg_cost", (ExportFunction)a -> getActualRegularCost((ResourceAssignment)a));
      RESOURCE_ASSIGNMENT_COLUMNS.put("act_ot_cost", AssignmentField.ACTUAL_OVERTIME_COST);
      RESOURCE_ASSIGNMENT_COLUMNS.put("remain_cost", AssignmentField.REMAINING_COST);
      RESOURCE_ASSIGNMENT_COLUMNS.put("act_start_date", AssignmentField.ACTUAL_START);
      RESOURCE_ASSIGNMENT_COLUMNS.put("act_end_date", AssignmentField.ACTUAL_FINISH);
      RESOURCE_ASSIGNMENT_COLUMNS.put("restart_date", null);
      RESOURCE_ASSIGNMENT_COLUMNS.put("reend_date", null);
      RESOURCE_ASSIGNMENT_COLUMNS.put("target_start_date", AssignmentField.PLANNED_START);
      RESOURCE_ASSIGNMENT_COLUMNS.put("target_end_date", AssignmentField.PLANNED_FINISH);
      RESOURCE_ASSIGNMENT_COLUMNS.put("rem_late_start_date", null);
      RESOURCE_ASSIGNMENT_COLUMNS.put("rem_late_end_date", null);
      RESOURCE_ASSIGNMENT_COLUMNS.put("rollup_dates_flag", Boolean.TRUE);
      RESOURCE_ASSIGNMENT_COLUMNS.put("target_crv", null);
      RESOURCE_ASSIGNMENT_COLUMNS.put("remain_crv", null);
      RESOURCE_ASSIGNMENT_COLUMNS.put("actual_crv", null);
      RESOURCE_ASSIGNMENT_COLUMNS.put("ts_pend_act_end_flag", Boolean.FALSE);
      RESOURCE_ASSIGNMENT_COLUMNS.put("guid", AssignmentField.GUID);
      RESOURCE_ASSIGNMENT_COLUMNS.put("rate_type", (ExportFunction)a -> RateTypeHelper.getXerFromInstance(((ResourceAssignment)a).getRateIndex()));
      RESOURCE_ASSIGNMENT_COLUMNS.put("act_this_per_cost", null);
      RESOURCE_ASSIGNMENT_COLUMNS.put("act_this_per_qty", null);
      RESOURCE_ASSIGNMENT_COLUMNS.put("curv_id", (ExportFunction)a -> CurveHelper.getCurveID(((ResourceAssignment)a).getWorkContour()));
      RESOURCE_ASSIGNMENT_COLUMNS.put("rsrc_type", (ExportFunction)a -> ResourceTypeHelper.getXerFromInstance(((ResourceAssignment)a).getResource().getType()));
      RESOURCE_ASSIGNMENT_COLUMNS.put("cost_per_qty_source_type", "ST_Rsrc");
      RESOURCE_ASSIGNMENT_COLUMNS.put("create_user", null);
      RESOURCE_ASSIGNMENT_COLUMNS.put("create_date", AssignmentField.CREATED);
      RESOURCE_ASSIGNMENT_COLUMNS.put("has_rsrchours", null);
      RESOURCE_ASSIGNMENT_COLUMNS.put("taskrsrc_sum_id", null);
   }

   private static final Map<String, ExportFunction> COST_ACCOUNT_COLUMNS = new LinkedHashMap<>();
   static
   {
      COST_ACCOUNT_COLUMNS.put("acct_id", a -> ((CostAccount)a).getUniqueID());
      COST_ACCOUNT_COLUMNS.put("parent_acct_id", a -> ((CostAccount)a).getParent() == null ? null : ((CostAccount)a).getParent().getUniqueID());
      COST_ACCOUNT_COLUMNS.put("acct_seq_num", a -> ((CostAccount)a).getSequenceNumber());
      COST_ACCOUNT_COLUMNS.put("acct_name", a -> ((CostAccount)a).getID());
      COST_ACCOUNT_COLUMNS.put("acct_short_name", a -> ((CostAccount)a).getName());
      COST_ACCOUNT_COLUMNS.put("acct_descr", a -> ((CostAccount)a).getDescription());
   }

   private static final Map<String, ExportFunction> EXPENSE_CATEGORY_COLUMNS = new LinkedHashMap<>();
   static
   {
      EXPENSE_CATEGORY_COLUMNS.put("cost_type_id", c -> ((ExpenseCategory)c).getUniqueID());
      EXPENSE_CATEGORY_COLUMNS.put("seq_num", c -> ((ExpenseCategory)c).getSequenceNumber());
      EXPENSE_CATEGORY_COLUMNS.put("cost_type", c -> ((ExpenseCategory)c).getName());
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
   }
}
