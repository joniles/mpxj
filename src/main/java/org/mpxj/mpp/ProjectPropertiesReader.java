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

import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

import org.mpxj.common.ByteArrayHelper;
import org.mpxj.common.DayOfWeekHelper;
import org.mpxj.Duration;
import org.apache.poi.hpsf.CustomProperties;
import org.apache.poi.hpsf.CustomProperty;
import org.apache.poi.hpsf.DocumentSummaryInformation;
import org.apache.poi.hpsf.PropertySet;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.DocumentInputStream;

import org.mpxj.MPXJException;
import org.mpxj.ProjectFile;
import org.mpxj.ProjectProperties;
import org.mpxj.Rate;
import org.mpxj.ScheduleFrom;
import org.mpxj.TimeUnit;
import org.mpxj.common.NumberHelper;

/**
 * This class reads project properties data from MPP8, MPP9, and MPP12 files.
 */
public final class ProjectPropertiesReader
{
   /**
    * The main entry point for processing project properties.
    *
    * @param file parent project file
    * @param props properties data
    * @param rootDir Root of the POI file system.
    */
   public void process(ProjectFile file, Props props, DirectoryEntry rootDir) throws MPXJException
   {
      try
      {
         //MPPUtility.fileDump("props.txt", props.toString().getBytes());
         ProjectProperties ph = file.getProjectProperties();
         ph.setGUID(props.getUUID(Props.GUID));
         ph.setStartDate(props.getTimestamp(Props.PROJECT_START_DATE));
         ph.setFinishDate(props.getTimestamp(Props.PROJECT_FINISH_DATE));
         ph.setScheduleFrom(ScheduleFrom.getInstance(1 - props.getShort(Props.SCHEDULE_FROM)));
         ph.setDefaultStartTime(props.getTime(Props.START_TIME));
         ph.setDefaultEndTime(props.getTime(Props.END_TIME));
         ph.setStatusDate(props.getTimestamp(Props.STATUS_DATE));
         ph.setHyperlinkBase(props.getUnicodeString(Props.HYPERLINK_BASE));

         //ph.setDefaultDurationIsFixed();
         ph.setDefaultDurationUnits(MPPUtility.getDurationTimeUnits(props.getShort(Props.DURATION_UNITS)));
         ph.setMinutesPerDay(Integer.valueOf(props.getInt(Props.MINUTES_PER_DAY)));
         ph.setMinutesPerWeek(Integer.valueOf(props.getInt(Props.MINUTES_PER_WEEK)));
         ph.setDefaultOvertimeRate(new Rate(props.getDouble(Props.OVERTIME_RATE), TimeUnit.HOURS));
         ph.setDefaultStandardRate(new Rate(props.getDouble(Props.STANDARD_RATE), TimeUnit.HOURS));
         ph.setDefaultWorkUnits(MPPUtility.getWorkTimeUnits(props.getShort(Props.WORK_UNITS)));
         ph.setSplitInProgressTasks(props.getBoolean(Props.SPLIT_TASKS));
         ph.setUpdatingTaskStatusUpdatesResourceStatus(props.getBoolean(Props.TASK_UPDATES_RESOURCE));
         ph.setCriticalSlackLimit(Duration.getInstance(props.getInt(Props.CRITICAL_SLACK_LIMIT), TimeUnit.DAYS));

         ph.setCurrencyDigits(Integer.valueOf(props.getShort(Props.CURRENCY_DIGITS)));
         ph.setCurrencySymbol(props.getUnicodeString(Props.CURRENCY_SYMBOL));
         ph.setCurrencyCode(props.getUnicodeString(Props.CURRENCY_CODE));
         //ph.setDecimalSeparator();
         ph.setDefaultTaskType(TaskTypeHelper.getInstance(props.getShort(Props.DEFAULT_TASK_TYPE)));
         ph.setSymbolPosition(MPPUtility.getSymbolPosition(props.getShort(Props.CURRENCY_PLACEMENT)));
         //ph.setThousandsSeparator();
         ph.setWeekStartDay(DayOfWeekHelper.getInstance(props.getShort(Props.WEEK_START_DAY) + 1));
         ph.setFiscalYearStartMonth(Integer.valueOf(props.getShort(Props.FISCAL_YEAR_START_MONTH)));
         ph.setFiscalYearStart(props.getShort(Props.FISCAL_YEAR_START) == 1);
         ph.setDaysPerMonth(Integer.valueOf(props.getShort(Props.DAYS_PER_MONTH)));
         ph.setEditableActualCosts(props.getBoolean(Props.EDITABLE_ACTUAL_COSTS));
         ph.setHonorConstraints(!props.getBoolean(Props.HONOR_CONSTRAINTS));
         ph.setBaselineCalendarName(props.getUnicodeString(Props.BASELINE_CALENDAR_NAME));

         PropertySet ps = null;

         try
         {
            ps = new PropertySet(new DocumentInputStream(((DocumentEntry) rootDir.getEntry(SummaryInformation.DEFAULT_STREAM_NAME))));
         }

         catch (FileNotFoundException ex)
         {
            // Microsoft Project opens a file successfully with missing summary property set.
            // We'll do the same here.
            file.addIgnoredError(ex);
         }

         SummaryInformation summaryInformation = ps == null ? new SummaryInformation() : new SummaryInformation(ps);
         ph.setProjectTitle(summaryInformation.getTitle());
         ph.setSubject(summaryInformation.getSubject());
         ph.setAuthor(summaryInformation.getAuthor());
         ph.setKeywords(summaryInformation.getKeywords());
         ph.setComments(summaryInformation.getComments());
         ph.setTemplate(summaryInformation.getTemplate());
         ph.setLastAuthor(summaryInformation.getLastAuthor());
         ph.setRevision(NumberHelper.parseInteger(summaryInformation.getRevNumber()));
         ph.setCreationDate(getLocalDateTime(summaryInformation.getCreateDateTime()));
         ph.setLastSaved(getLocalDateTime(summaryInformation.getLastSaveDateTime()));
         ph.setShortApplicationName(summaryInformation.getApplicationName());
         ph.setEditingTime(Integer.valueOf((int) summaryInformation.getEditTime()));
         ph.setLastPrinted(getLocalDateTime(summaryInformation.getLastPrinted()));

         try
         {
            ps = new PropertySet(new DocumentInputStream(((DocumentEntry) rootDir.getEntry(DocumentSummaryInformation.DEFAULT_STREAM_NAME))));
         }

         catch (RuntimeException | FileNotFoundException ex)
         {
            // RuntimeException:
            // I have one example MPP file which has a corrupt document summary property set.
            // Microsoft Project opens the file successfully, apparently by just ignoring
            // the corrupt data. We'll do the same here. I have raised a bug with POI
            // to see if they want to make the library more robust in the face of bad data.
            // https://bz.apache.org/bugzilla/show_bug.cgi?id=61550

            // FileNotFoundException:
            // Microsoft Project opens a file successfully with missing document summary property set.
            // We'll do the same here.

            file.addIgnoredError(ex);
            ps = null;
         }

         DocumentSummaryInformation documentSummaryInformation = ps == null ? new DocumentSummaryInformation() : new DocumentSummaryInformation(ps);
         ph.setCategory(documentSummaryInformation.getCategory());
         ph.setPresentationFormat(documentSummaryInformation.getPresentationFormat());
         ph.setManager(documentSummaryInformation.getManager());
         ph.setCompany(documentSummaryInformation.getCompany());
         ph.setContentType(documentSummaryInformation.getContentType());
         ph.setContentStatus(documentSummaryInformation.getContentStatus());
         ph.setLanguage(documentSummaryInformation.getLanguage());
         ph.setDocumentVersion(documentSummaryInformation.getDocumentVersion());

         Map<String, Object> customPropertiesMap = new TreeMap<>();
         CustomProperties customProperties = documentSummaryInformation.getCustomProperties();
         if (customProperties != null)
         {
            for (CustomProperty property : customProperties.properties())
            {
               Object value = property.getValue();
               if (value instanceof Date)
               {
                  value = getLocalDateTime((Date) value);
               }
               customPropertiesMap.put(property.getName(), value);
            }
         }
         ph.setCustomProperties(customPropertiesMap);

         ph.setMultipleCriticalPaths(props.getBoolean(Props.MULTIPLE_CRITICAL_PATHS));

         ph.setBaselineDate(props.getTimestamp(Props.BASELINE_DATE));
         ph.setBaselineDate(1, props.getTimestamp(Props.BASELINE1_DATE));
         ph.setBaselineDate(2, props.getTimestamp(Props.BASELINE2_DATE));
         ph.setBaselineDate(3, props.getTimestamp(Props.BASELINE3_DATE));
         ph.setBaselineDate(4, props.getTimestamp(Props.BASELINE4_DATE));
         ph.setBaselineDate(5, props.getTimestamp(Props.BASELINE5_DATE));
         ph.setBaselineDate(6, props.getTimestamp(Props.BASELINE6_DATE));
         ph.setBaselineDate(7, props.getTimestamp(Props.BASELINE7_DATE));
         ph.setBaselineDate(8, props.getTimestamp(Props.BASELINE8_DATE));
         ph.setBaselineDate(9, props.getTimestamp(Props.BASELINE9_DATE));
         ph.setBaselineDate(10, props.getTimestamp(Props.BASELINE10_DATE));

         ph.setNewTasksAreManual(props.getBoolean(Props.NEW_TASKS_ARE_MANUAL));

         ph.setResourcePoolFile(getResourcePool(props.getByteArray(Props.RESOURCE_POOL)));
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

   private LocalDateTime getLocalDateTime(Date date)
   {
      if (date == null)
      {
         return null;
      }

      return LocalDateTime.ofInstant(date.toInstant(), TimeZone.getDefault().toZoneId());
   }
}
