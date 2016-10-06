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

package net.sf.mpxj.mpp;

import java.util.HashMap;
import java.util.Map;

import net.sf.mpxj.Day;
import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ProjectProperties;
import net.sf.mpxj.Rate;
import net.sf.mpxj.ScheduleFrom;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.common.NumberHelper;

import org.apache.poi.hpsf.CustomProperties;
import org.apache.poi.hpsf.CustomProperty;
import org.apache.poi.hpsf.DocumentSummaryInformation;
import org.apache.poi.hpsf.PropertySet;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.DocumentInputStream;

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
         //MPPUtility.fileDump("c:\\temp\\props.txt", props.toString().getBytes());
         ProjectProperties ph = file.getProjectProperties();
         ph.setStartDate(props.getTimestamp(Props.PROJECT_START_DATE));
         ph.setFinishDate(props.getTimestamp(Props.PROJECT_FINISH_DATE));
         ph.setScheduleFrom(ScheduleFrom.getInstance(1 - props.getShort(Props.SCHEDULE_FROM)));
         ph.setDefaultCalendarName(props.getUnicodeString(Props.DEFAULT_CALENDAR_NAME));
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

         ph.setCurrencyDigits(Integer.valueOf(props.getShort(Props.CURRENCY_DIGITS)));
         ph.setCurrencySymbol(props.getUnicodeString(Props.CURRENCY_SYMBOL));
         ph.setCurrencyCode(props.getUnicodeString(Props.CURRENCY_CODE));
         //ph.setDecimalSeparator();
         ph.setSymbolPosition(MPPUtility.getSymbolPosition(props.getShort(Props.CURRENCY_PLACEMENT)));
         //ph.setThousandsSeparator();
         ph.setWeekStartDay(Day.getInstance(props.getShort(Props.WEEK_START_DAY) + 1));
         ph.setFiscalYearStartMonth(Integer.valueOf(props.getShort(Props.FISCAL_YEAR_START_MONTH)));
         ph.setFiscalYearStart(props.getShort(Props.FISCAL_YEAR_START) == 1);
         ph.setDaysPerMonth(Integer.valueOf(props.getShort(Props.DAYS_PER_MONTH)));
         ph.setEditableActualCosts(props.getBoolean(Props.EDITABLE_ACTUAL_COSTS));
         ph.setHonorConstraints(!props.getBoolean(Props.HONOR_CONSTRAINTS));

         PropertySet ps = new PropertySet(new DocumentInputStream(((DocumentEntry) rootDir.getEntry(SummaryInformation.DEFAULT_STREAM_NAME))));
         SummaryInformation summaryInformation = new SummaryInformation(ps);
         ph.setProjectTitle(summaryInformation.getTitle());
         ph.setSubject(summaryInformation.getSubject());
         ph.setAuthor(summaryInformation.getAuthor());
         ph.setKeywords(summaryInformation.getKeywords());
         ph.setComments(summaryInformation.getComments());
         ph.setTemplate(summaryInformation.getTemplate());
         ph.setLastAuthor(summaryInformation.getLastAuthor());
         ph.setRevision(NumberHelper.parseInteger(summaryInformation.getRevNumber()));
         ph.setCreationDate(summaryInformation.getCreateDateTime());
         ph.setLastSaved(summaryInformation.getLastSaveDateTime());
         ph.setShortApplicationName(summaryInformation.getApplicationName());
         ph.setEditingTime(Integer.valueOf((int) summaryInformation.getEditTime()));
         ph.setLastPrinted(summaryInformation.getLastPrinted());

         ps = new PropertySet(new DocumentInputStream(((DocumentEntry) rootDir.getEntry(DocumentSummaryInformation.DEFAULT_STREAM_NAME))));
         DocumentSummaryInformation documentSummaryInformation = new DocumentSummaryInformation(ps);
         ph.setCategory(documentSummaryInformation.getCategory());
         ph.setPresentationFormat(documentSummaryInformation.getPresentationFormat());
         ph.setManager(documentSummaryInformation.getManager());
         ph.setCompany(documentSummaryInformation.getCompany());
         ph.setContentType(documentSummaryInformation.getContentType());
         ph.setContentStatus(documentSummaryInformation.getContentStatus());
         ph.setLanguage(documentSummaryInformation.getLanguage());
         ph.setDocumentVersion(documentSummaryInformation.getDocumentVersion());

         Map<String, Object> customPropertiesMap = new HashMap<String, Object>();
         CustomProperties customProperties = documentSummaryInformation.getCustomProperties();
         if (customProperties != null)
         {
            for (CustomProperty property : customProperties.values())
            {
               customPropertiesMap.put(property.getName(), property.getValue());
            }
         }
         ph.setCustomProperties(customPropertiesMap);

         ph.setCalculateMultipleCriticalPaths(props.getBoolean(Props.CALCULATE_MULTIPLE_CRITICAL_PATHS));

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
      }

      catch (Exception ex)
      {
         throw new MPXJException(MPXJException.READ_ERROR, ex);
      }
   }
}
