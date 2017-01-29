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
 * http://140.194.76.129/publications/eng-regs/ER_1-1-11/ER_1-1-11.pdf
 *
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.sf.mpxj.Duration;
import net.sf.mpxj.EventManager;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectCalendarException;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ProjectProperties;
import net.sf.mpxj.Relation;
import net.sf.mpxj.Task;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.writer.AbstractProjectWriter;

/**
 * This class creates a new SDEF file from the contents of
 * a ProjectFile instance.
 */
public final class SDEFWriter extends AbstractProjectWriter
{
   private ProjectFile m_projectFile; // from MPXJ library
   private EventManager m_eventManager;
   private PrintStream m_writer; // line out to a text file
   private StringBuilder m_buffer; // used to accumulate characters
   private Format m_formatter = new SimpleDateFormat("ddMMMyy"); // USACE required format
   private double m_minutesPerDay;
   private double m_minutesPerWeek; // needed to get everything into days
   private double m_daysPerMonth;

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
      writeTasks(m_projectFile.getAllTasks()); // ACTV
      writePredecessors(m_projectFile.getAllTasks()); // PRED
      // skipped UNIT cost record for now
      writeProgress(m_projectFile.getAllTasks()); // PROG
      m_writer.println("END"); // last line, that's the end!!!
   }

   /**
    * Write file creation record.
    *
    * @throws IOException
    */
   private void writeFileCreationRecord() throws IOException
   {
      m_writer.println("VOLM 1"); // first line in file
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

      // reset buffer to be empty, then concatenate data as required by USACE
      m_buffer.setLength(0);
      m_buffer.append("PROJ ");
      m_buffer.append(m_formatter.format(record.getStartDate()).toUpperCase() + " "); // DataDate
      m_buffer.append(SDEFmethods.lset(record.getManager(), 4) + " "); // ProjIdent
      m_buffer.append(SDEFmethods.lset(record.getProjectTitle(), 48) + " "); // ProjName
      m_buffer.append(SDEFmethods.lset(record.getSubject(), 36) + " "); // ContrName
      m_buffer.append("P "); // ArrowP
      m_buffer.append(SDEFmethods.lset(record.getKeywords(), 7)); // ContractNum
      m_buffer.append(m_formatter.format(record.getStartDate()).toUpperCase() + " "); // ProjStart
      m_buffer.append(m_formatter.format(record.getFinishDate()).toUpperCase()); // ProjEnd
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
            // Need to move HOLI up here and get 15 exceptions per line as per USACE spec.
            // for now, we'll write one line for each calendar exception, hope there aren't too many
            //
            // changing this would be a serious upgrade, too much coding to do today....
            for (ProjectCalendarException ex : record.getCalendarExceptions())
            {
               writeCalendarException(record, ex);
            }
         }
         m_eventManager.fireCalendarWrittenEvent(record); // left here from MPX template, maybe not needed???
      }
   }

   /**
    * Write a calendar exception.
    *
    * @param parentCalendar parent calendar instance
    * @param record calendar exception instance
    * @throws IOException
    */
   private void writeCalendarException(ProjectCalendar parentCalendar, ProjectCalendarException record) throws IOException
   {
      m_buffer.setLength(0);
      Calendar stepDay = Calendar.getInstance();
      stepDay.setTime(record.getFromDate()); // Start at From Date, then step through days...
      Calendar lastDay = Calendar.getInstance();
      lastDay.setTime(record.getToDate()); // last day in this exception

      m_buffer.append("HOLI ");
      m_buffer.append(SDEFmethods.lset(parentCalendar.getUniqueID().toString(), 2));

      while (stepDay.compareTo(lastDay) <= 0)
      {
         m_buffer.append(m_formatter.format(stepDay.getTime()).toUpperCase() + " ");
         stepDay.add(Calendar.DAY_OF_MONTH, 1);
      }
      m_writer.println(m_buffer.toString());
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
         m_buffer.append(SDEFmethods.rset(record.getUniqueID().toString(), 10) + " ");
         m_buffer.append(SDEFmethods.lset(record.getName(), 30) + " ");

         // Following just makes certain we have days for duration, as per USACE spec.
         Duration dd = record.getDuration();
         double duration = dd.getDuration();
         if (dd.getUnits() != TimeUnit.DAYS)
         {
            dd = Duration.convertUnits(duration, dd.getUnits(), TimeUnit.DAYS, m_minutesPerDay, m_minutesPerWeek, m_daysPerMonth);
         }
         Double days = Double.valueOf(dd.getDuration() + 0.5); // Add 0.5 so half day rounds up upon truncation
         Integer est = Integer.valueOf(days.intValue());
         m_buffer.append(SDEFmethods.rset(est.toString(), 3) + " "); // task duration in days required by USACE

         String conType = "ES "; // assume early start
         Date conDate = record.getEarlyStart();
         int test = record.getConstraintType().getValue(); // test for other types
         if (test == 1 || test == 3 || test == 6 || test == 7)
         {
            conType = "LF "; // see ConstraintType enum for definitions
            conDate = record.getLateFinish();
         }
         m_buffer.append(m_formatter.format(conDate).toUpperCase() + " "); // Constraint Date
         m_buffer.append(conType); // Constraint Type
         if (record.getCalendar() == null)
         {
            m_buffer.append("1 ");
         }
         else
         {
            m_buffer.append(SDEFmethods.lset(record.getCalendar().getUniqueID().toString(), 1) + " ");
         }
         // skipping hammock code in here
         // use of text fields for extra USACE data is suggested at my web site: www.geocomputer.com
         // not documented on how to do this here, so I need to comment out at present
         //	      m_buffer.append(SDEFmethods.Lset(record.getText1(), 3) + " ");
         //	      m_buffer.append(SDEFmethods.Lset(record.getText2(), 4) + " ");
         //	      m_buffer.append(SDEFmethods.Lset(record.getText3(), 4) + " ");
         //	      m_buffer.append(SDEFmethods.Lset(record.getText4(), 6) + " ");
         //	      m_buffer.append(SDEFmethods.Lset(record.getText5(), 6) + " ");
         //	      m_buffer.append(SDEFmethods.Lset(record.getText6(), 2) + " ");
         //	      m_buffer.append(SDEFmethods.Lset(record.getText7(), 1) + " ");
         //	      m_buffer.append(SDEFmethods.Lset(record.getText8(), 30) + " ");
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
         m_buffer.append("PRED ");
         List<Relation> predecessors = record.getPredecessors();

         for (Relation pred : predecessors)
         {
            m_buffer.append(SDEFmethods.rset(pred.getSourceTask().getUniqueID().toString(), 10) + " ");
            m_buffer.append(SDEFmethods.rset(pred.getTargetTask().getUniqueID().toString(), 10) + " ");
            String type = "C"; // default finish-to-start
            if (!pred.getType().toString().equals("FS"))
            {
               type = pred.getType().toString().substring(0, 1);
            }
            m_buffer.append(type + " ");

            Duration dd = pred.getLag();
            double duration = dd.getDuration();
            if (dd.getUnits() != TimeUnit.DAYS)
            {
               dd = Duration.convertUnits(duration, dd.getUnits(), TimeUnit.DAYS, m_minutesPerDay, m_minutesPerWeek, m_daysPerMonth);
            }
            Double days = Double.valueOf(dd.getDuration() + 0.5); // Add 0.5 so half day rounds up upon truncation
            Integer est = Integer.valueOf(days.intValue());
            m_buffer.append(SDEFmethods.rset(est.toString(), 4) + " "); // task duration in days required by USACE
         }
         m_writer.println(m_buffer.toString());
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
         m_buffer.append(SDEFmethods.rset(record.getUniqueID().toString(), 10) + " ");
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

         Duration dd = record.getRemainingDuration();
         double duration = dd.getDuration();
         if (dd.getUnits() != TimeUnit.DAYS)
         {
            dd = Duration.convertUnits(duration, dd.getUnits(), TimeUnit.DAYS, m_minutesPerDay, m_minutesPerWeek, m_daysPerMonth);
         }
         Double days = Double.valueOf(dd.getDuration() + 0.5); // Add 0.5 so half day rounds up upon truncation
         Integer est = Integer.valueOf(days.intValue());
         m_buffer.append(SDEFmethods.rset(est.toString(), 3) + " "); // task duration in days required by USACE

         DecimalFormat twoDec = new DecimalFormat("#0.00"); // USACE required currency format
         m_buffer.append(SDEFmethods.rset(twoDec.format(record.getCost().floatValue()), 12) + " ");
         m_buffer.append(SDEFmethods.rset(twoDec.format(0.00), 12) + " "); // *** assume zero progress on cost
         m_buffer.append(SDEFmethods.rset(twoDec.format(0.00), 12) + " "); // *** assume zero progress on cost
         m_buffer.append(m_formatter.format(record.getEarlyStart()).toUpperCase() + " ");
         m_buffer.append(m_formatter.format(record.getEarlyFinish()).toUpperCase() + " ");
         m_buffer.append(m_formatter.format(record.getLateStart()).toUpperCase() + " ");
         m_buffer.append(m_formatter.format(record.getLateFinish()).toUpperCase() + " ");

         dd = record.getTotalSlack();
         duration = dd.getDuration();
         if (dd.getUnits() != TimeUnit.DAYS)
         {
            dd = Duration.convertUnits(duration, dd.getUnits(), TimeUnit.DAYS, m_minutesPerDay, m_minutesPerWeek, m_daysPerMonth);
         }
         days = Double.valueOf(dd.getDuration() + 0.5); // Add 0.5 so half day rounds up upon truncation
         est = Integer.valueOf(days.intValue());
         char slack;
         if (est.intValue() >= 0)
         {
            slack = '+'; // USACE likes positive slack, so they separate the sign from the value
         }
         else
         {
            slack = '-'; // only write a negative when it's negative, i.e. can't be done in project management terms!!!
         }
         m_buffer.append(slack + " ");
         est = Integer.valueOf(Math.abs(days.intValue()));
         m_buffer.append(SDEFmethods.rset(est.toString(), 4)); // task duration in days required by USACE
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

}
