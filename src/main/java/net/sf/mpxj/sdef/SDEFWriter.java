/*
 * file:       SDEFWriter.java
 * author:     William (Bill) Iverson
 * copyright:  (c) GeoComputer 2011
 * date:       05/14/2012
 *
 * started with net.sf.mpxj.mpx MPXWriter.java as template for writing all of below
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

package net.sf.mpxj.sdef;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import net.sf.mpxj.Duration;
import net.sf.mpxj.EventManager;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectCalendarException;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ProjectProperties;
import net.sf.mpxj.Relation;
import net.sf.mpxj.Task;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.common.BooleanHelper;
import net.sf.mpxj.common.DateHelper;
import net.sf.mpxj.common.NumberHelper;
import net.sf.mpxj.writer.AbstractProjectWriter;

/**
 * This class creates a new SDEF file from the contents of
 * a ProjectFile instance.
 */
public final class SDEFWriter extends AbstractProjectWriter
{
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

      m_writer = new PrintStream(out); // the print stream class is the easiest way to create a text file
      m_buffer = new StringBuilder();

      try
      {
         write(); // method call a method, this is how MPXJ is structured, so I followed the lead?
      }

      //      catch (Exception e)
      //      { // used during console debugging
      //         System.out.println("Caught Exception in SDEFWriter.java");
      //         System.out.println(" " + e.toString());
      //      }

      finally
      { // keeps things cool after we're done
         m_writer = null;
         m_projectFile = null;
         m_buffer = null;
      }
   }

   /**
    * Writes the contents of the project file as MPX records.
    *
    * @throws IOException
    */
   private void write() throws IOException
   {
      // Following USACE specification from 140.194.76.129/publications/eng-regs/ER_1-1-11/ER_1-1-11.pdf
      writeFileCreationRecord(); // VOLM
      writeProjectProperties(m_projectFile.getProjectProperties()); // PROJ
      writeCalendars(m_projectFile.getCalendars()); // CLDR
      writeExceptions(m_projectFile.getCalendars()); // HOLI
      writeTasks(m_projectFile.getTasks()); // ACTV
      writePredecessors(m_projectFile.getTasks()); // PRED
      // skipped UNIT cost record for now
      writeProgress(m_projectFile.getTasks()); // PROG
      m_writer.println("END"); // last line, that's the end!!!
   }

   /**
    * Write file creation record.
    *
    * @throws IOException
    */
   private void writeFileCreationRecord() throws IOException
   {
      m_writer.println("VOLM  1"); // first line in file
   }

   /**
    * Write project properties.
    *
    * @param record project properties
    * @throws IOException
    *
    */
   private void writeProjectProperties(ProjectProperties record) throws IOException
   {
      // the ProjectProperties class from MPXJ has the details of how many days per week etc....
      // so I've assigned these variables in here, but actually use them in other methods
      // see the write task method, that's where they're used, but that method only has a Task object
      m_minutesPerDay = record.getMinutesPerDay().doubleValue();
      m_minutesPerWeek = record.getMinutesPerWeek().doubleValue();
      m_daysPerMonth = record.getDaysPerMonth().doubleValue();

      Date dataDate = record.getStatusDate() == null ? m_projectFile.getProjectProperties().getCurrentDate() : record.getStatusDate();
      Date startDate = record.getStartDate();
      Date finishDate = record.getFinishDate();

      // reset buffer to be empty, then concatenate data as required by USACE
      m_buffer.setLength(0);
      m_buffer.append("PROJ ");
      m_buffer.append(formatDate(dataDate) + " "); // DataDate
      m_buffer.append(SDEFmethods.lset(record.getManager(), 4) + " "); // ProjIdent
      m_buffer.append(SDEFmethods.lset(record.getProjectTitle(), 48) + " "); // ProjName
      m_buffer.append(SDEFmethods.lset(record.getSubject(), 36) + " "); // ContrName
      m_buffer.append("P "); // ArrowP
      m_buffer.append(SDEFmethods.lset(record.getKeywords(), 7)); // ContractNum
      m_buffer.append(formatDate(startDate) + " "); // ProjStart
      m_buffer.append(formatDate(finishDate)); // ProjEnd
      m_writer.println(m_buffer);
   }

   /**
    * This will create a line in the SDEF file for each calendar
    * if there are more than 9 calendars, you'll have a big error,
    * as USACE numbers them 0-9.
    *
    * @param records list of ProjectCalendar instances
    */
   private void writeCalendars(List<ProjectCalendar> records)
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
         m_writer.println(m_buffer);
      }
   }

   /**
    * Write calendar exceptions.
    *
    * @param records list of ProjectCalendars
    * @throws IOException
    */
   private void writeExceptions(List<ProjectCalendar> records) throws IOException
   {
      for (ProjectCalendar record : records)
      {
         if (!record.getCalendarExceptions().isEmpty())
         {
            List<String> formattedExceptions = new ArrayList<>();
            String recordPrefix = "HOLI " + SDEFmethods.lset(record.getUniqueID().toString(), 2);

            for (ProjectCalendarException ex : record.getCalendarExceptions())
            {
               generateCalendarExceptions(record, ex, formattedExceptions);
            }

            int startIndex = 0;
            int endIndex;
            while (startIndex < formattedExceptions.size())
            {
               if (startIndex + MAX_EXCEPTIONS_PER_RECORD <= formattedExceptions.size())
               {
                  endIndex = startIndex + MAX_EXCEPTIONS_PER_RECORD;
               }
               else
               {
                  endIndex = formattedExceptions.size();
               }

               m_writer.print(recordPrefix);
               m_writer.println(formattedExceptions.subList(startIndex, endIndex).stream().collect(Collectors.joining(" ")));
               startIndex = endIndex;
            }
         }
         m_eventManager.fireCalendarWrittenEvent(record); // left here from MPX template, maybe not needed???
      }
   }

   /**
    * Populate a list of formatted exceptions.
    *
    * @param parentCalendar parent calendar instance
    * @param record calendar exception instance
    * @param formattedExceptions list of formatted exceptions
    */
   private void generateCalendarExceptions(ProjectCalendar parentCalendar, ProjectCalendarException record, List<String> formattedExceptions)
   {
      Calendar stepDay = DateHelper.popCalendar(record.getFromDate()); // Start at From Date, then step through days...
      Calendar lastDay = DateHelper.popCalendar(record.getToDate()); // last day in this exception

      while (stepDay.compareTo(lastDay) <= 0)
      {
         formattedExceptions.add(formatDate(stepDay.getTime()));
         stepDay.add(Calendar.DAY_OF_MONTH, 1);
      }

      DateHelper.pushCalendar(stepDay);
      DateHelper.pushCalendar(lastDay);
   }

   /**
    * Write a task.
    *
    * @param record task instance
    * @throws IOException
    */
   private void writeTask(Task record) throws IOException
   {
      m_buffer.setLength(0);
      if (!record.getSummary())
      {
         m_buffer.append("ACTV ");

         m_buffer.append(getActivityID(record) + " ");
         m_buffer.append(SDEFmethods.lset(record.getName(), 30) + " ");

         // Following just makes certain we have days for duration, as per USACE spec.
         Duration dd = record.getDuration();
         if (dd == null)
         {
            dd = Duration.getInstance(0, TimeUnit.DAYS);
         }

         double duration = dd.getDuration();
         if (dd.getUnits() != TimeUnit.DAYS)
         {
            dd = Duration.convertUnits(duration, dd.getUnits(), TimeUnit.DAYS, m_minutesPerDay, m_minutesPerWeek, m_daysPerMonth);
         }
         Double days = Double.valueOf(dd.getDuration() + 0.5); // Add 0.5 so half day rounds up upon truncation
         Integer est = Integer.valueOf(days.intValue());
         m_buffer.append(SDEFmethods.rset(est.toString(), 3) + " "); // task duration in days required by USACE

         String conType;
         String formattedConstraintDate;
         Date conDate = record.getConstraintDate();
         if (conDate == null)
         {
            conType = "   ";
            formattedConstraintDate = "       ";
         }
         else
         {
            formattedConstraintDate = m_formatter.format(conDate).toUpperCase();

            switch (record.getConstraintType())
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

         m_buffer.append(formattedConstraintDate + " ");
         m_buffer.append(conType);
         if (record.getCalendar() == null)
         {
            m_buffer.append("1 ");
         }
         else
         {
            m_buffer.append(SDEFmethods.lset(record.getCalendar().getUniqueID().toString(), 1) + " ");
         }

         m_buffer.append(BooleanHelper.getBoolean(record.getHammockCode()) ? "Y " : "  ");
         m_buffer.append(SDEFmethods.rset(formatNumber(record.getWorkersPerDay()), 3) + " ");
         m_buffer.append(SDEFmethods.lset(record.getResponsibilityCode(), 4) + " ");
         m_buffer.append(SDEFmethods.lset(record.getWorkAreaCode(), 4) + " ");
         m_buffer.append(SDEFmethods.lset(record.getModOrClaimNumber(), 6) + " ");
         m_buffer.append(SDEFmethods.lset(record.getBidItem(), 6) + " ");
         m_buffer.append(SDEFmethods.lset(record.getPhaseOfWork(), 2) + " ");
         m_buffer.append(SDEFmethods.lset(record.getCategoryOfWork(), 1) + " ");
         m_buffer.append(SDEFmethods.lset(record.getFeatureOfWork(), 30));

         m_writer.println(m_buffer.toString());
         m_eventManager.fireTaskWrittenEvent(record);
      }
   }

   /**
    * Write an SDEF line for each task ACTV.
    *
    * @param tasks list of Task instances
    * @throws IOException
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
   private void writePredecessors(List<Task> tasks)
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
   private void writeTaskPredecessors(Task record)
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
            m_buffer.append(getActivityID(pred.getSourceTask()) + " ");
            m_buffer.append(getActivityID(pred.getTargetTask()) + " ");
            String type = "C"; // default finish-to-start
            if (!pred.getType().toString().equals("FS"))
            {
               type = pred.getType().toString().substring(0, 1);
            }
            m_buffer.append(type + " ");

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
               dd = Duration.convertUnits(duration, dd.getUnits(), TimeUnit.DAYS, m_minutesPerDay, m_minutesPerWeek, m_daysPerMonth);
            }
            Double days = Double.valueOf(dd.getDuration());
            Integer est = Integer.valueOf(days.intValue());
            m_buffer.append(SDEFmethods.rset(est.toString(), 4)); // task duration in days required by USACE
            m_writer.println(m_buffer.toString());
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
   private void writePROG(Task record)
   {
      m_buffer.setLength(0);
      //
      // Write the progress record
      //
      if (!record.getSummary())
      { // I don't use summary tasks for SDEF
         m_buffer.append("PROG ");
         m_buffer.append(getActivityID(record) + " ");
         Date temp = record.getActualStart();
         if (temp == null)
         {
            m_buffer.append("        "); // SDEf is column sensitive, so the number of blanks here is crucial
         }
         else
         {
            m_buffer.append(m_formatter.format(record.getActualStart()).toUpperCase() + " "); // ACTUAL START DATE
         }
         temp = record.getActualFinish();
         if (temp == null)
         {
            m_buffer.append("        ");
         }
         else
         {
            m_buffer.append(m_formatter.format(record.getActualFinish()).toUpperCase() + " "); // ACTUAL FINISH DATE
         }

         Duration dd = record.getRemainingDuration() == null ? Duration.getInstance(0, TimeUnit.DAYS) : record.getRemainingDuration();
         double duration = dd.getDuration();
         if (dd.getUnits() != TimeUnit.DAYS)
         {
            dd = Duration.convertUnits(duration, dd.getUnits(), TimeUnit.DAYS, m_minutesPerDay, m_minutesPerWeek, m_daysPerMonth);
         }
         Double days = Double.valueOf(dd.getDuration() + 0.5); // Add 0.5 so half day rounds up upon truncation
         Integer est = Integer.valueOf(days.intValue());
         m_buffer.append(SDEFmethods.rset(est.toString(), 3) + " "); // task duration in days required by USACE

         DecimalFormat twoDec = new DecimalFormat("#0.00"); // USACE required currency format
         m_buffer.append(SDEFmethods.rset(twoDec.format(NumberHelper.getDouble(record.getCost())), 12) + " ");
         m_buffer.append(SDEFmethods.rset(twoDec.format(NumberHelper.getDouble(record.getActualCost())), 12) + " ");
         m_buffer.append(SDEFmethods.rset(twoDec.format(NumberHelper.getDouble(record.getStoredMaterial())), 12) + " ");
         m_buffer.append(formatDate(record.getEarlyStart()) + " ");
         m_buffer.append(formatDate(record.getEarlyFinish()) + " ");
         m_buffer.append(formatDate(record.getLateStart()) + " ");
         m_buffer.append(formatDate(record.getLateFinish()) + " ");

         char floatSign;
         String floatValue;

         if (record.getActualFinish() == null)
         {
            dd = record.getTotalSlack();
            duration = dd.getDuration();
            if (dd.getUnits() != TimeUnit.DAYS)
            {
               dd = Duration.convertUnits(duration, dd.getUnits(), TimeUnit.DAYS, m_minutesPerDay, m_minutesPerWeek, m_daysPerMonth);
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

         m_buffer.append(floatSign + " ");
         m_buffer.append(SDEFmethods.rset(floatValue, 3)); // task duration in days required by USACE
         m_writer.println(m_buffer.toString());
         m_eventManager.fireTaskWrittenEvent(record);
      }
   }

   /**
    * Write a progress line for each task.
    *
    * @param tasks list of Task instances
    */
   private void writeProgress(List<Task> tasks)
   {
      for (Task task : tasks)
      {
         writePROG(task);
      }
   }

   private String formatDate(Date date)
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

   private ProjectFile m_projectFile; // from MPXJ library
   private EventManager m_eventManager;
   private PrintStream m_writer; // line out to a text file
   private StringBuilder m_buffer; // used to accumulate characters
   private Format m_formatter = new SimpleDateFormat("ddMMMyy"); // USACE required format
   private double m_minutesPerDay;
   private double m_minutesPerWeek; // needed to get everything into days
   private double m_daysPerMonth;

   private static final int MAX_EXCEPTIONS_PER_RECORD = 15;
}
