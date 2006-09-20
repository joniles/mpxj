/*
 * file:       ProjectHeaderReader.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2006
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

import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ProjectHeader;
import net.sf.mpxj.Rate;
import net.sf.mpxj.ScheduleFrom;
import net.sf.mpxj.TimeUnit;

import org.apache.poi.poifs.filesystem.DirectoryEntry;

/**
 * This class reads project header data from MPP9 and MPP12 files.
 */
public final class ProjectHeaderReader
{
   /**
    * The main entry point for processing project header data.
    * 
    * @param file parent project file
    * @param props properties data
    * @param rootDir Root of the POI file system.
    */
   public void process (ProjectFile file, Props props, DirectoryEntry rootDir)
      throws MPXJException
   {
      ProjectHeader ph = file.getProjectHeader();
      ph.setScheduleFrom(ScheduleFrom.getInstance(1-props.getShort(Props.SCHEDULE_FROM)));
      ph.setCalendarName(props.getUnicodeString(Props.DEFAULT_CALENDAR_NAME));
      ph.setDefaultStartTime(props.getTime(Props.START_TIME));
      ph.setDefaultEndTime(props.getTime(Props.END_TIME));
      ph.setStatusDate(props.getTimestamp(Props.STATUS_DATE));
      
      //ph.setDefaultDurationIsFixed();
      ph.setDefaultDurationUnits(MPPUtility.getDurationTimeUnits(props.getShort(Props.DURATION_UNITS)));
      ph.setMinutesPerDay(new Integer(props.getInt(Props.MINUTES_PER_DAY)));
      ph.setMinutesPerWeek(new Integer(props.getInt(Props.MINUTES_PER_WEEK)));
      ph.setDefaultOvertimeRate(new Rate (props.getDouble(Props.OVERTIME_RATE), TimeUnit.HOURS));
      ph.setDefaultStandardRate(new Rate (props.getDouble(Props.STANDARD_RATE), TimeUnit.HOURS));
      ph.setDefaultWorkUnits(MPPUtility.getWorkTimeUnits(props.getShort(Props.WORK_UNITS)));
      ph.setSplitInProgressTasks(props.getBoolean(Props.SPLIT_TASKS));
      ph.setUpdatingTaskStatusUpdatesResourceStatus(props.getBoolean(Props.TASK_UPDATES_RESOURCE));

      ph.setCurrencyDigits(new Integer(props.getShort(Props.CURRENCY_DIGITS)));
      ph.setCurrencySymbol(props.getUnicodeString(Props.CURRENCY_SYMBOL));
      //ph.setDecimalSeparator();
      ph.setSymbolPosition(MPPUtility.getSymbolPosition(props.getShort(Props.CURRENCY_PLACEMENT)));
      //ph.setThousandsSeparator();


      SummaryInformation summary = new SummaryInformation (rootDir);
      ph.setProjectTitle(summary.getProjectTitle());
      ph.setSubject(summary.getSubject());
      ph.setAuthor(summary.getAuthor());
      ph.setKeywords(summary.getKeywords());
      ph.setComments(summary.getComments());
      ph.setCompany(summary.getCompany());
      ph.setManager(summary.getManager());
      ph.setCategory(summary.getCategory());
      ph.setRevision(summary.getRevision());
      ph.setDocumentSummaryInformation(summary.getDocumentSummaryInformation());

      ph.setCalculateMultipleCriticalPaths(props.getBoolean(Props.CALCULATE_MULTIPLE_CRITICAL_PATHS));      
   }
}
