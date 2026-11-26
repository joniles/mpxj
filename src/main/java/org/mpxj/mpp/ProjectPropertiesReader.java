/*
 * file:       ProjectPropertiesReader.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2006
 * date:       24/08/2006
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

package org.mpxj.mpp;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.function.Supplier;

import org.mpxj.Duration;
import org.mpxj.MPXJException;
import org.mpxj.ProjectFile;
import org.mpxj.ProjectProperties;
import org.mpxj.Rate;
import org.mpxj.ScheduleFrom;
import org.mpxj.TimeUnit;
import org.mpxj.cfb.CfbFile;
import org.mpxj.common.ByteArrayHelper;
import org.mpxj.common.DayOfWeekHelper;
import org.mpxj.common.NumberHelper;

/**
 * This class reads project properties data from MPP8, MPP9, and MPP12 files.
 */
final class ProjectPropertiesReader
{
   /**
    * The main entry point for processing project properties.
    *
    * @param file parent project file
    * @param props properties data
    * @param cfb parent file
    */
   public void process(ProjectFile file, Props props, CfbFile cfb) throws MPXJException
   {
      try
      {
         //MPPUtility.fileDump("props.txt", props.toString().getBytes());
         ProjectProperties ph = file.getProjectProperties();
         ph.setGUID(props.getUUID(PropsKey.GUID));
         ph.setStartDate(props.getTimestamp(PropsKey.PROJECT_START_DATE));
         ph.setFinishDate(props.getTimestamp(PropsKey.PROJECT_FINISH_DATE));
         ph.setScheduleFrom(ScheduleFrom.getInstance(1 - props.getShort(PropsKey.SCHEDULE_FROM)));
         ph.setDefaultStartTime(props.getTime(PropsKey.START_TIME));
         ph.setDefaultEndTime(props.getTime(PropsKey.END_TIME));
         ph.setStatusDate(props.getTimestamp(PropsKey.STATUS_DATE));
         ph.setHyperlinkBase(props.getUnicodeString(PropsKey.HYPERLINK_BASE));

         //ph.setDefaultDurationIsFixed();
         ph.setDefaultDurationUnits(MPPUtility.getDurationTimeUnits(props.getShort(PropsKey.DURATION_UNITS)));
         ph.setMinutesPerDay(Integer.valueOf(props.getInt(PropsKey.MINUTES_PER_DAY)));
         ph.setMinutesPerWeek(Integer.valueOf(props.getInt(PropsKey.MINUTES_PER_WEEK)));
         ph.setDefaultOvertimeRate(new Rate(props.getDouble(PropsKey.OVERTIME_RATE), TimeUnit.HOURS));
         ph.setDefaultStandardRate(new Rate(props.getDouble(PropsKey.STANDARD_RATE), TimeUnit.HOURS));
         ph.setDefaultWorkUnits(MPPUtility.getWorkTimeUnits(props.getShort(PropsKey.WORK_UNITS)));
         ph.setSplitInProgressTasks(props.getBoolean(PropsKey.SPLIT_TASKS));
         ph.setUpdatingTaskStatusUpdatesResourceStatus(props.getBoolean(PropsKey.TASK_UPDATES_RESOURCE));
         ph.setCriticalSlackLimit(Duration.getInstance(props.getInt(PropsKey.CRITICAL_SLACK_LIMIT), TimeUnit.DAYS));

         ph.setCurrencyDigits(Integer.valueOf(props.getShort(PropsKey.CURRENCY_DIGITS)));
         ph.setCurrencySymbol(props.getUnicodeString(PropsKey.CURRENCY_SYMBOL));
         ph.setCurrencyCode(props.getUnicodeString(PropsKey.CURRENCY_CODE));
         //ph.setDecimalSeparator();
         ph.setDefaultTaskType(TaskTypeHelper.getInstance(props.getShort(PropsKey.DEFAULT_TASK_TYPE)));
         ph.setSymbolPosition(MPPUtility.getSymbolPosition(props.getShort(PropsKey.CURRENCY_PLACEMENT)));
         //ph.setThousandsSeparator();
         ph.setWeekStartDay(DayOfWeekHelper.getInstance(props.getShort(PropsKey.WEEK_START_DAY) + 1));
         ph.setFiscalYearStartMonth(Integer.valueOf(props.getShort(PropsKey.FISCAL_YEAR_START_MONTH)));
         ph.setFiscalYearStart(props.getShort(PropsKey.FISCAL_YEAR_START) == 1);
         ph.setDaysPerMonth(Integer.valueOf(props.getShort(PropsKey.DAYS_PER_MONTH)));
         ph.setEditableActualCosts(props.getBoolean(PropsKey.EDITABLE_ACTUAL_COSTS));
         ph.setHonorConstraints(!props.getBoolean(PropsKey.HONOR_CONSTRAINTS));
         ph.setBaselineCalendarName(props.getUnicodeString(PropsKey.BASELINE_CALENDAR_NAME));

         Map<String, String> summaryInformation = cfb.getSummaryInformation();
         ph.setProjectTitle(getPopulatedValue(summaryInformation.get("TITLE"), () -> props.getUnicodeString(PropsKey.TITLE)));
         ph.setSubject(getPopulatedValue(summaryInformation.get("SUBJECT"), () -> props.getUnicodeString(PropsKey.SUBJECT)));
         ph.setAuthor(getPopulatedValue(summaryInformation.get("AUTHOR"), () -> props.getUnicodeString(PropsKey.AUTHOR)));
         ph.setKeywords(getPopulatedValue(summaryInformation.get("KEYWORDS"), () -> props.getUnicodeString(PropsKey.KEYWORDS)));
         ph.setComments(getPopulatedValue(summaryInformation.get("COMMENTS"), () -> props.getUnicodeString(PropsKey.COMMENTS)));
         ph.setTemplate(summaryInformation.get("TEMPLATE"));
         ph.setLastAuthor(summaryInformation.get("LAST_AUTHOR"));
         ph.setRevision(NumberHelper.parseInteger(summaryInformation.get("REVISION_NUMBER")));
         ph.setCreationDate(getLocalDateTime(summaryInformation.get("CREATED")));
         ph.setLastSaved(getLocalDateTime(summaryInformation.get("LAST_SAVED")));
         ph.setShortApplicationName(summaryInformation.get("APPLICATION_NAME"));
         ph.setEditingTime(getLong(summaryInformation.get("EDIT_TIME")));
         ph.setLastPrinted(getLocalDateTime(summaryInformation.get("LAST_PRINTED")));

         Map<String, String> documentSummaryInformation = cfb.getDocumentSummaryInformation();
         ph.setCategory(getPopulatedValue(documentSummaryInformation.get("CATEGORY"), () -> props.getUnicodeString(PropsKey.CATEGORY)));
         ph.setPresentationFormat(documentSummaryInformation.get("PRESENTATION_FORMAT"));
         ph.setManager(getPopulatedValue(documentSummaryInformation.get("MANAGER"), () -> props.getUnicodeString(PropsKey.MANAGER)));
         ph.setCompany(getPopulatedValue(documentSummaryInformation.get("COMPANY"), () -> props.getUnicodeString(PropsKey.COMPANY)));
         ph.setContentType(documentSummaryInformation.get("CONTENT_TYPE"));
         ph.setContentStatus(documentSummaryInformation.get("CONTENT_STATUS"));
         ph.setLanguage(documentSummaryInformation.get("LANGUAGE"));
         ph.setDocumentVersion(documentSummaryInformation.get("DOCUMENT_VERSION"));

         ph.setCustomProperties(cfb.getCustomProperties());

         ph.setMultipleCriticalPaths(props.getBoolean(PropsKey.MULTIPLE_CRITICAL_PATHS));

         ph.setBaselineDate(props.getTimestamp(PropsKey.BASELINE_DATE));
         ph.setBaselineDate(1, props.getTimestamp(PropsKey.BASELINE1_DATE));
         ph.setBaselineDate(2, props.getTimestamp(PropsKey.BASELINE2_DATE));
         ph.setBaselineDate(3, props.getTimestamp(PropsKey.BASELINE3_DATE));
         ph.setBaselineDate(4, props.getTimestamp(PropsKey.BASELINE4_DATE));
         ph.setBaselineDate(5, props.getTimestamp(PropsKey.BASELINE5_DATE));
         ph.setBaselineDate(6, props.getTimestamp(PropsKey.BASELINE6_DATE));
         ph.setBaselineDate(7, props.getTimestamp(PropsKey.BASELINE7_DATE));
         ph.setBaselineDate(8, props.getTimestamp(PropsKey.BASELINE8_DATE));
         ph.setBaselineDate(9, props.getTimestamp(PropsKey.BASELINE9_DATE));
         ph.setBaselineDate(10, props.getTimestamp(PropsKey.BASELINE10_DATE));

         ph.setNewTasksAreManual(props.getBoolean(PropsKey.NEW_TASKS_ARE_MANUAL));

         ph.setResourcePoolFile(getResourcePool(props.getByteArray(PropsKey.RESOURCE_POOL)));
      }

      catch (Exception ex)
      {
         throw new MPXJException(MPXJException.READ_ERROR, ex);
      }
   }

   private String getResourcePool(byte[] data)
   {
      if (data == null)
      {
         return null;
      }

      // 18 byte header
      int offset = 18;
      if (offset + 4 >= data.length)
      {
         return null;
      }

      // Length of the 8.3 filename
      int length = ByteArrayHelper.getInt(data, offset);
      offset += 4;

      // 8.3 filename
      offset += length;

      // 34 byte header
      offset += 34;
      if (offset >= data.length)
      {
         return null;
      }

      return MPPUtility.getUnicodeString(data, offset);
   }

   private LocalDateTime getLocalDateTime(String date)
   {
      return date == null ? null : LocalDateTime.parse(date, DATE_TIME_FORMAT);
   }

   private Long getLong(String value)
   {
      return value == null ? null : Long.valueOf(value);
   }

   /**
    * Use the first value if it is populated, otherwise try the supplier.
    *
    * @param value first value
    * @param supplier value supplier
    * @return string value
    */
   private String getPopulatedValue(String value, Supplier<String> supplier)
   {
      if (value == null || value.isEmpty())
      {
         value = supplier.get();
         if (value != null && value.isEmpty())
         {
            value = null;
         }
      }
      return value;
   }

   private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
}
