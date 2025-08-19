/*
 * file:       SDEFWriter.java
 * author:     William (Bill) Iverson
 * copyright:  (c) GeoComputer 2011
 * date:       05/14/2012
 *
 * started with MPXWriter.java as template for writing all of below
 * so it follows the logic and style of other MPXJ classes
 *
 * SDEF is the Standard Data Exchange Format, as defined by the USACE (United States
 * Army Corp of Engineers).  SDEF is a fixed column format text file, used to import
 * a project schedule up into the QCS (Quality Control System) software from USACE
 *
 * Precise specification of SDEF can be found at the USACE library:
 * https://www.publications.usace.army.mil/Portals/76/Publications/EngineerRegulations/ER_1-1-11.pdf
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

package org.mpxj.sdef;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import org.mpxj.ConstraintType;
import org.mpxj.Duration;
import org.mpxj.EventManager;
import org.mpxj.ProjectCalendar;
import org.mpxj.ProjectCalendarException;
import org.mpxj.ProjectFile;
import org.mpxj.ProjectProperties;
import org.mpxj.Relation;
import org.mpxj.Task;
import org.mpxj.TimeUnit;
import org.mpxj.common.NumberHelper;
import org.mpxj.common.ProjectCalendarHelper;
import org.mpxj.writer.AbstractProjectWriter;

/**
 * This class creates a new SDEF file from the contents of
 * a ProjectFile instance.
 */
public final class SDEFWriter extends AbstractProjectWriter
{
   /**
    * Set the character set used when writing an SDEF file.
    * According to SDEF the spec this should be ASCII,
    * which is the default.
    *
    * @param charset character set to use when writing the file
    */
   public void setCharset(Charset charset)
   {
      if (charset != null)
      {
         m_charset = charset;
      }
   }

   /**
    * Retrieve the character set used when writing an SDEF file.
    *
    * @return character set
    */
   public Charset getCharset()
   {
      return m_charset;
   }

   /**
    * Write a project file in SDEF format to an output stream.
    *
    * @param projectFile ProjectFile instance
    * @param out output stream
    */
   @Override public void write(ProjectFile projectFile, OutputStream out) throws IOException
   {
      m_projectFile = projectFile;
      m_eventManager = projectFile.getEventManager();

      m_writer = new OutputStreamWriter(out, m_charset);
      m_buffer = new StringBuilder();

      try
      {
         List<ProjectCalendar> calendars = m_projectFile.getTasks().stream().map(Task::getEffectiveCalendar).filter(Objects::nonNull).distinct().map(ProjectCalendarHelper::createTemporaryFlattenedCalendar).collect(Collectors.toList());

         // Following USACE specification from 140.194.76.129/publications/eng-regs/ER_1-1-11/ER_1-1-11.pdf
         writeFileCreationRecord(); // VOLM
         writeProjectProperties(m_projectFile.getProjectProperties()); // PROJ
         writeCalendars(calendars); // CLDR
         writeExceptions(calendars); // HOLI
         writeTasks(m_projectFile.getTasks()); // ACTV
         writePredecessors(m_projectFile.getTasks()); // PRED
         // skipped UNIT cost record for now
         writeProgress(m_projectFile.getTasks()); // PROG
         m_writer.write("END\n"); // last line, that's the end!!!
         m_writer.flush();
      }

      finally
      {
         m_writer = null;
         m_projectFile = null;
         m_buffer = null;
      }
   }

   /**
    * Write file creation record.
    */
   private void writeFileCreationRecord() throws IOException
   {
      m_writer.write("VOLM  1\n"); // first line in file
   }

   /**
    * Write project properties.
    *
    * @param record project properties
    *
    */
   private void writeProjectProperties(ProjectProperties record) throws IOException
   {
      LocalDateTime dataDate = record.getStatusDate() == null ? m_projectFile.getProjectProperties().getCurrentDate() : record.getStatusDate();
      LocalDateTime startDate = record.getStartDate();
      LocalDateTime finishDate = record.getFinishDate();

      // reset buffer to be empty, then concatenate data as required by USACE
      m_buffer.setLength(0);
      m_buffer.append("PROJ ");
      m_buffer.append(formatDate(dataDate)).append(" "); // DataDate
      m_buffer.append(SDEFmethods.lset(record.getManager(), 4)).append(" "); // ProjIdent
      m_buffer.append(SDEFmethods.lset(record.getProjectTitle(), 48)).append(" "); // ProjName
      m_buffer.append(SDEFmethods.lset(record.getSubject(), 36)).append(" "); // ContrName
      m_buffer.append("P "); // ArrowP
      m_buffer.append(SDEFmethods.lset(record.getKeywords(), 7)); // ContractNum
      m_buffer.append(formatDate(startDate)).append(" "); // ProjStart
      m_buffer.append(formatDate(finishDate)); // ProjEnd
      m_buffer.append("\n");
      m_writer.write(m_buffer.toString());
   }

   /**
    * This will create a line in the SDEF file for each calendar
    * if there are more than 9 calendars, you'll have a big error,
    * as USACE numbers them 0-9.
    *
    * @param records list of ProjectCalendar instances
    */
   private void writeCalendars(List<ProjectCalendar> records) throws IOException
   {

      //
      // Write project calendars
      //
      for (ProjectCalendar record : records)
      {
         m_buffer.setLength(0);
         m_buffer.append("CLDR ");
         m_buffer.append(SDEFmethods.lset(record.getUniqueID().toString(), 2)); // 2 character used, USACE allows 1
         String workDays = SDEFmethods.workDays(record); // custom line, like NYYYYYN for a week
         m_buffer.append(SDEFmethods.lset(workDays, 8));
         m_buffer.append(SDEFmethods.lset(record.getName(), 30));
         m_buffer.append("\n");
         m_writer.write(m_buffer.toString());
      }
   }

   /**
    * Write calendar exceptions.
    *
    * @param records list of ProjectCalendars
    */
   private void writeExceptions(List<ProjectCalendar> records) throws IOException
   {
      for (ProjectCalendar record : records)
      {
         List<ProjectCalendarException> expandedExceptions = record.getExpandedCalendarExceptionsWithWorkWeeks();
         if (!expandedExceptions.isEmpty())
         {
            List<String> formattedExceptions = new ArrayList<>();
            String recordPrefix = "HOLI " + SDEFmethods.lset(record.getUniqueID().toString(), 2);

            for (ProjectCalendarException ex : expandedExceptions)
            {
               generateCalendarExceptions(ex, formattedExceptions);
            }

            int startIndex = 0;
            int endIndex;
            while (startIndex < formattedExceptions.size())
            {
               endIndex = Math.min(startIndex + MAX_EXCEPTIONS_PER_RECORD, formattedExceptions.size());

               m_writer.write(recordPrefix);
               m_writer.write(String.join(" ", formattedExceptions.subList(startIndex, endIndex)));
               m_writer.write("\n");
               startIndex = endIndex;
            }
         }
         m_eventManager.fireCalendarWrittenEvent(record); // left here from MPX template, maybe not needed???
      }
   }

   /**
    * Populate a list of formatted exceptions.
    *
    * @param record calendar exception instance
    * @param formattedExceptions list of formatted exceptions
    */
   private void generateCalendarExceptions(ProjectCalendarException record, List<String> formattedExceptions)
   {
      LocalDate stepDay = record.getFromDate(); // Start at From Date, then step through days...
      LocalDate lastDay = record.getToDate(); // last day in this exception

      while (!stepDay.isAfter(lastDay))
      {
         formattedExceptions.add(formatDate(stepDay));
         stepDay = stepDay.plusDays(1);
      }
   }

   /**
    * Write a task.
    *
    * @param record task instance
    */
   private void writeTask(Task record) throws IOException
   {
      m_buffer.setLength(0);
      if (!record.getSummary())
      {
         m_buffer.append("ACTV ");

         m_buffer.append(getActivityID(record)).append(" ");
         m_buffer.append(SDEFmethods.lset(record.getName(), 30)).append(" ");

         // Following just makes certain we have days for duration, as per USACE spec.
         Duration dd = record.getDuration();
         if (dd == null)
         {
            dd = Duration.getInstance(0, TimeUnit.DAYS);
         }

         if (dd.getUnits() != TimeUnit.DAYS)
         {
            dd = dd.convertUnits(TimeUnit.DAYS, m_projectFile.getProjectProperties());
         }
         Double days = Double.valueOf(dd.getDuration() + 0.5); // Add 0.5 so half day rounds up upon truncation
         Integer est = Integer.valueOf(days.intValue());
         m_buffer.append(SDEFmethods.rset(est.toString(), 3)).append(" "); // task duration in days required by USACE

         String conType;
         String formattedConstraintDate;
         LocalDateTime conDate = record.getConstraintDate();
         if (conDate == null)
         {
            conType = "   ";
            formattedConstraintDate = "       ";
         }
         else
         {
            formattedConstraintDate = m_formatter.format(conDate).toUpperCase();

            switch (getConstraintType(record))
            {
               case AS_LATE_AS_POSSIBLE:
               case MUST_FINISH_ON:
               case FINISH_NO_EARLIER_THAN:
               case FINISH_NO_LATER_THAN:
               {
                  conType = "LF ";
                  break;
               }

               default:
               {
                  conType = "ES ";
               }
            }
         }

         ProjectCalendar effectiveCalendar = record.getEffectiveCalendar();

         m_buffer.append(formattedConstraintDate).append(" ");
         m_buffer.append(conType);
         m_buffer.append(SDEFmethods.lset(effectiveCalendar == null ? "" : effectiveCalendar.getUniqueID().toString(), 1)).append(" ");
         m_buffer.append(record.getHammockCode() ? "Y " : "  ");
         m_buffer.append(SDEFmethods.rset(formatNumber(record.getWorkersPerDay()), 3)).append(" ");
         m_buffer.append(SDEFmethods.lset(record.getResponsibilityCode(), 4)).append(" ");
         m_buffer.append(SDEFmethods.lset(record.getWorkAreaCode(), 4)).append(" ");
         m_buffer.append(SDEFmethods.lset(record.getModOrClaimNumber(), 6)).append(" ");
         m_buffer.append(SDEFmethods.lset(record.getBidItem(), 6)).append(" ");
         m_buffer.append(SDEFmethods.lset(record.getPhaseOfWork(), 2)).append(" ");
         m_buffer.append(SDEFmethods.lset(record.getCategoryOfWork(), 1)).append(" ");
         m_buffer.append(SDEFmethods.lset(record.getFeatureOfWork(), 30));
         m_buffer.append("\n");

         m_writer.write(m_buffer.toString());
         m_eventManager.fireTaskWrittenEvent(record);
      }
   }

   /**
    * Write an SDEF line for each task ACTV.
    *
    * @param tasks list of Task instances
    */
   private void writeTasks(List<Task> tasks) throws IOException
   {
      for (Task task : tasks)
      {
         writeTask(task); // writes one line to SDEF file
      }
   }

   /**
    * For each task, write an SDEF line for each PRED.
    *
    * @param tasks list of Task instances
    */
   private void writePredecessors(List<Task> tasks) throws IOException
   {
      for (Task task : tasks)
      {
         writeTaskPredecessors(task);
      }
   }

   /**
    * Write each predecessor for a task.
    *
    * @param record Task instance
    */
   private void writeTaskPredecessors(Task record) throws IOException
   {
      m_buffer.setLength(0);
      //
      // Write the task predecessor
      //
      if (!record.getSummary() && !record.getPredecessors().isEmpty())
      { // I don't use summary tasks for SDEF
         List<Relation> predecessors = record.getPredecessors();

         for (Relation pred : predecessors)
         {
            m_buffer.setLength(0);
            m_buffer.append("PRED ");
            m_buffer.append(getActivityID(pred.getSuccessorTask())).append(" ");
            m_buffer.append(getActivityID(pred.getPredecessorTask())).append(" ");
            String type = "C"; // default finish-to-start
            if (!pred.getType().toString().equals("FS"))
            {
               type = pred.getType().toString().substring(0, 1);
            }
            m_buffer.append(type).append(" ");

            Duration dd = pred.getLag();
            double duration = dd.getDuration();

            // Add 0.5 so half day rounds up upon truncation
            if (duration < 0)
            {
               duration -= 0.5;
            }
            else
            {
               duration += 0.5;
            }

            if (dd.getUnits() != TimeUnit.DAYS)
            {
               dd = Duration.convertUnits(duration, dd.getUnits(), TimeUnit.DAYS, m_projectFile.getProjectProperties());
            }
            Double days = Double.valueOf(dd.getDuration());
            Integer est = Integer.valueOf(days.intValue());
            m_buffer.append(SDEFmethods.rset(est.toString(), 4)); // task duration in days required by USACE
            m_buffer.append("\n");
            m_writer.write(m_buffer.toString());
         }
      }
   }

   /**
    * Writes a progress line to the SDEF file.
    *
    * Progress lines in SDEF are a little tricky, you need to assume a percent complete
    * this could be physical or temporal, I don't know what you're using???
    * So in this version of SDEFwriter, I just put in 0.00 for cost progress to date, see *** below
    *
    * @param record Task instance
    */
   private void writePROG(Task record) throws IOException
   {
      m_buffer.setLength(0);
      //
      // Write the progress record
      //
      if (!record.getSummary())
      { // I don't use summary tasks for SDEF
         m_buffer.append("PROG ");
         m_buffer.append(getActivityID(record)).append(" ");
         LocalDateTime temp = record.getActualStart();
         if (temp == null)
         {
            m_buffer.append("        "); // SDEF is column sensitive, so the number of blanks here is crucial
         }
         else
         {
            m_buffer.append(m_formatter.format(record.getActualStart()).toUpperCase()).append(" "); // ACTUAL START DATE
         }
         temp = record.getActualFinish();
         if (temp == null)
         {
            m_buffer.append("        ");
         }
         else
         {
            m_buffer.append(m_formatter.format(record.getActualFinish()).toUpperCase()).append(" "); // ACTUAL FINISH DATE
         }

         Duration dd = record.getRemainingDuration() == null ? Duration.getInstance(0, TimeUnit.DAYS) : record.getRemainingDuration();
         if (dd.getUnits() != TimeUnit.DAYS)
         {
            dd = dd.convertUnits(TimeUnit.DAYS, m_projectFile.getProjectProperties());
         }
         Double days = Double.valueOf(dd.getDuration() + 0.5); // Add 0.5 so half day rounds up upon truncation
         Integer est = Integer.valueOf(days.intValue());
         m_buffer.append(SDEFmethods.rset(est.toString(), 3)).append(" "); // task duration in days required by USACE

         DecimalFormat twoDec = new DecimalFormat("#0.00"); // USACE required currency format
         m_buffer.append(SDEFmethods.rset(twoDec.format(NumberHelper.getDouble(record.getCost())), 12)).append(" ");
         m_buffer.append(SDEFmethods.rset(twoDec.format(NumberHelper.getDouble(record.getActualCost())), 12)).append(" ");
         m_buffer.append(SDEFmethods.rset(twoDec.format(NumberHelper.getDouble(record.getStoredMaterial())), 12)).append(" ");
         m_buffer.append(formatDate(record.getEarlyStart())).append(" ");
         m_buffer.append(formatDate(record.getEarlyFinish())).append(" ");
         m_buffer.append(formatDate(record.getLateStart())).append(" ");
         m_buffer.append(formatDate(record.getLateFinish())).append(" ");

         char floatSign;
         String floatValue;

         if (record.getActualFinish() == null)
         {
            dd = record.getTotalSlack();
            if (dd == null)
            {
               dd = Duration.getInstance(0, TimeUnit.DAYS);
            }

            if (dd.getUnits() != TimeUnit.DAYS)
            {
               dd = dd.convertUnits(TimeUnit.DAYS, m_projectFile.getProjectProperties());
            }
            days = Double.valueOf(dd.getDuration() + 0.5); // Add 0.5 so half day rounds up upon truncation
            est = Integer.valueOf(days.intValue());
            if (est.intValue() >= 0)
            {
               floatSign = '+'; // USACE likes positive slack, so they separate the sign from the value
            }
            else
            {
               floatSign = '-'; // only write a negative when it's negative, i.e. can't be done in project management terms!!!
            }
            est = Integer.valueOf(Math.abs(days.intValue()));
            floatValue = est.toString();
         }
         else
         {
            floatSign = ' ';
            floatValue = "";
         }

         m_buffer.append(floatSign).append(" ");
         m_buffer.append(SDEFmethods.rset(floatValue, 3)); // task duration in days required by USACE
         m_buffer.append("\n");
         m_writer.write(m_buffer.toString());
         m_eventManager.fireTaskWrittenEvent(record);
      }
   }

   /**
    * Write a progress line for each task.
    *
    * @param tasks list of Task instances
    */
   private void writeProgress(List<Task> tasks) throws IOException
   {
      for (Task task : tasks)
      {
         writePROG(task);
      }
   }

   private String formatDate(LocalDateTime date)
   {
      String result;
      if (date == null)
      {
         result = "       ";
      }
      else
      {
         result = m_formatter.format(date).toUpperCase();
      }
      return result;
   }

   private String formatDate(LocalDate date)
   {
      String result;
      if (date == null)
      {
         result = "       ";
      }
      else
      {
         result = m_localDateFormatter.format(date).toUpperCase();
      }
      return result;
   }

   private String formatNumber(Number value)
   {
      String result;
      if (value == null)
      {
         result = "";
      }
      else
      {
         result = value.toString();
      }
      return result;
   }

   private String getActivityID(Task task)
   {
      // Example SDEF files I've seen include an alphanumeric Activity ID
      // field, left justified, rather than the number right justified field
      // as defined by the spec. We'll use the Activity ID if it is present
      // and left justify it, otherwise we'll follow the spec with a numeric identifier.
      String activityID = task.getActivityID();
      if (activityID == null)
      {
         activityID = SDEFmethods.rset(String.valueOf(NumberHelper.getInt(task.getUniqueID())), 10);
      }
      else
      {
         activityID = SDEFmethods.lset(activityID, 10);
      }

      return activityID;
   }

   /**
    * Retrieve the constraint type and default to As Soon As Possible
    * if no constraint type is present.
    *
    * @param task target task
    * @return constraint type
    */
   private ConstraintType getConstraintType(Task task)
   {
      return task.getConstraintType() == null ? ConstraintType.AS_SOON_AS_POSSIBLE : task.getConstraintType();
   }

   private ProjectFile m_projectFile;
   private EventManager m_eventManager;
   private OutputStreamWriter m_writer;
   private StringBuilder m_buffer;
   private Charset m_charset = StandardCharsets.US_ASCII;
   private final DateTimeFormatter m_formatter = DateTimeFormatter.ofPattern("ddMMMyy", Locale.ENGLISH);
   private final DateTimeFormatter m_localDateFormatter = DateTimeFormatter.ofPattern("ddMMMyy", Locale.ENGLISH);
   private static final int MAX_EXCEPTIONS_PER_RECORD = 15;
}
