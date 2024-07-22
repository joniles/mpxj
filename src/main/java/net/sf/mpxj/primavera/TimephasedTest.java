package net.sf.mpxj.primavera;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import net.sf.mpxj.Duration;
import net.sf.mpxj.LocalDateTimeRange;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Resource;
import net.sf.mpxj.ResourceAssignment;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.TimephasedWork;
import net.sf.mpxj.mpp.TimescaleUnits;
import net.sf.mpxj.reader.UniversalProjectReader;
import net.sf.mpxj.utility.TimephasedUtility;
import net.sf.mpxj.utility.TimescaleUtility;

public class TimephasedTest
{
   public static void main(String[] argv) throws Exception
   {
      new TimephasedTest().process(argv[0], argv[1]);
   }

   public void process(String inputProjectFile, String outputHtmlFile) throws Exception
   {
      m_file = new UniversalProjectReader().read(inputProjectFile);
      TimescaleUtility timescaleUtility = new TimescaleUtility();
      m_projectStart = m_file.getProjectProperties().getStartDate();
      m_projectFinish = m_file.getProjectProperties().getFinishDate();

      if (m_projectStart.getDayOfWeek() != DayOfWeek.SUNDAY)
      {
         LocalDateTime newProjectStart = m_projectStart.with(DayOfWeek.SUNDAY);
         if (newProjectStart.isAfter(m_projectStart))
         {
            newProjectStart = newProjectStart.minusWeeks(1);
         }
         m_projectStart = newProjectStart;
      }

      long days = m_projectStart.until(m_projectFinish, ChronoUnit.DAYS);
      m_timescale = timescaleUtility.createTimescale(m_projectStart, TimescaleUnits.DAYS, (int) days);


      FileOutputStream os = new FileOutputStream(outputHtmlFile);
      PrintStream ps = new PrintStream(os);
      ps.println("<html>");

      ps.println("<head>");
      ps.println("<style>");
      ps.println("table {border: 1px solid; border-collapse: collapse;}");
      ps.println("th {border: 1px solid;}");
      ps.println("td {border: 1px solid; min-width: 1em;}");
      ps.println("td.nonworking { background-color: #DDDDDD; }");
      ps.println("</style>");
      ps.println("</head>");

      ps.println("<body>");

      ps.println("<h1>Planned</h1>");
      writeTimescale(ps, this::getTimephasedPlannedWork);

      ps.println("<h1>Actual</h1>");
      writeTimescale(ps, this::getTimephasedActualWork);

      ps.println("<h1>Remaining</h1>");
      writeTimescale(ps, this::getTimephasedWork);

      ps.println("</body>");
      ps.println("</html>");
   }

   private void writeTimescale(PrintStream ps, Function<ResourceAssignment, List<TimephasedWork>> provider)
   {
      ps.println("<table>");
      ps.println("<thead>");

      ps.println("<tr>");
      ps.println("<th>Activity ID</th><th>Activity Name</th>");
      LocalDateTime date = m_projectStart;
      while (date.isBefore(m_projectFinish))
      {
         ps.println("<th colspan='7'>" + DATE_FORMAT.format(date) + "</th>");
         date = date.plusWeeks(1);
      }
      ps.println("</tr>");

      ps.println("<tr>");
      ps.println("<th/><th/>");
      date = m_projectStart;
      while (date.isBefore(m_projectFinish))
      {
         ps.println("<th>" + DAY_FORMAT.format(date).charAt(0) + "</th>");
         date = date.plusDays(1);
      }
      ps.println("</tr>");

      for (Resource resource : m_file.getResources())
      {
         List<ResourceAssignment> assignments = resource.getTaskAssignments();
         if (assignments.isEmpty())
         {
            continue;
         }


         ps.println("<tr>");
         ps.println("<td colspan='" + m_timescale.size() + 2 + "'>" + resource.getName() + "</td>");
         ps.println("</tr>");

         for(ResourceAssignment assignment : assignments)
         {
            ps.println("<tr>");
            ps.println("<td>" + assignment.getTask().getActivityID() + "</td>");
            ps.println("<td>" + assignment.getTask().getName() + "</td>");
            ProjectCalendar calendar = assignment.getEffectiveCalendar();

            ArrayList<Duration> work = new TimephasedUtility().segmentWork(calendar, provider.apply(assignment), TimescaleUnits.DAYS, m_timescale);

            for (int index=0; index < m_timescale.size(); index++)
            {
               LocalDateTimeRange range = m_timescale.get(index);
               Duration duration = work.get(index);
               String cssClass = calendar.isWorkingDay(range.getStart().getDayOfWeek()) ? "working" : "nonworking";

               if (duration.getDuration() == 0)
               {
                  ps.println("<td class='" + cssClass + "'/>");
               }
               else
               {
                  ps.println("<td class='" + cssClass + "'>" + (int)duration.getDuration() + "</td>");
               }
            }

            ps.println("</tr>");
         }
      }

      ps.println("</thead>");
      ps.println("<tbody>");
      ps.println("</tbody>");
      ps.println("</table>");
   }

   private List<TimephasedWork> getTimephasedPlannedWork(ResourceAssignment assignment)
   {
      if (assignment.getTimephasedPlannedWork() != null)
      {
         return assignment.getTimephasedPlannedWork();
      }

      TimephasedWork item = new TimephasedWork();
      item.setStart(assignment.getPlannedStart());
      item.setFinish(assignment.getPlannedFinish());
      item.setTotalAmount(assignment.getPlannedWork());
      item.setAmountPerDay(getAmountPerDay(assignment, item));

      List<TimephasedWork> timephasedWork = new ArrayList<>();
      timephasedWork.add(item);

      return timephasedWork;
   }

   private List<TimephasedWork> getTimephasedActualWork(ResourceAssignment assignment)
   {
      if (assignment.getTimephasedActualWork() != null)
      {
         return assignment.getTimephasedActualWork();
      }

      if (assignment.getActualStart() == null)
      {
         return null;
      }

      ProjectCalendar calendar = assignment.getEffectiveCalendar();
      LocalDateTime finish = calendar.getDate(assignment.getActualStart(), assignment.getActualWork());
      // hacky - but does the job for now
      Duration amountPerDay = Duration.getInstance(calendar.getMinutesPerDay().doubleValue() / 60.0, TimeUnit.HOURS);

      TimephasedWork item = new TimephasedWork();
      item.setStart(assignment.getActualStart());
      item.setFinish(finish);
      item.setTotalAmount(assignment.getActualWork());
      item.setAmountPerDay(amountPerDay);

      List<TimephasedWork> timephasedWork = new ArrayList<>();
      timephasedWork.add(item);

      return timephasedWork;
   }

   private List<TimephasedWork> getTimephasedWork(ResourceAssignment assignment)
   {
      if (assignment.getTimephasedWork() != null)
      {
         return assignment.getTimephasedWork();
      }

      if (assignment.getActualFinish() != null)
      {
         return null;
      }

      LocalDateTime start = assignment.getActualStart() == null ? assignment.getStart() : assignment.getRemainingEarlyStart();

      TimephasedWork item = new TimephasedWork();
      item.setStart(start);
      item.setFinish(assignment.getFinish());
      item.setTotalAmount(assignment.getRemainingWork());
      item.setAmountPerDay(getAmountPerDay(assignment, item));

      List<TimephasedWork> timephasedWork = new ArrayList<>();
      timephasedWork.add(item);

      return timephasedWork;
   }

   private Duration getAmountPerDay(ResourceAssignment assignment, TimephasedWork item)
   {
      double days = assignment.getEffectiveCalendar().getDuration(item.getStart(), item.getFinish()).getDuration();
      return Duration.getInstance(item.getTotalAmount().getDuration()/days, TimeUnit.HOURS);
   }

   private ProjectFile m_file;
   private LocalDateTime m_projectStart;
   private LocalDateTime m_projectFinish;
   private List<LocalDateTimeRange> m_timescale;

   private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MMM dd");
   private static final DateTimeFormatter DAY_FORMAT = DateTimeFormatter.ofPattern("E");
}
