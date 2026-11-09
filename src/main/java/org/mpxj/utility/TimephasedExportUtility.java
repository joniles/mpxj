package org.mpxj.utility;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;

import org.apache.commons.lang3.function.TriFunction;
import org.mpxj.Duration;
import org.mpxj.LocalDateTimeRange;
import org.mpxj.MPXJException;
import org.mpxj.ProjectFile;
import org.mpxj.Resource;
import org.mpxj.ResourceAssignment;
import org.mpxj.Task;
import org.mpxj.TimeUnit;
import org.mpxj.TimescaleUnits;
import org.mpxj.common.TimescaleHelper;
import org.mpxj.reader.UniversalProjectReader;

public class TimephasedExportUtility
{
   public static void main(String[] argv) throws Exception
   {
      if (argv.length != 2)
      {
         System.out.println("Usage: <input project file> <outut text file>");
         return;
      }

      ProjectFile file = new UniversalProjectReader().read(argv[0]);

      new TimephasedExportUtility().export(file, new File(argv[1]), ResourceAssignment::getTimephasedActualWork);
   }

   public void export(ProjectFile file, File output, TriFunction<ResourceAssignment, List<LocalDateTimeRange>, TimeUnit, List<Duration>> fn) throws IOException
   {
      List<ResourceAssignment> assignments = new ArrayList<>(file.getResourceAssignments());
      assignments.sort(Comparator.comparing(ResourceAssignment::getUniqueID));

      List<String> buffer = new ArrayList<>();
      TimescaleHelper helper = new TimescaleHelper();

      try(PrintWriter pw = new PrintWriter(new FileWriter(output)))
      {
         pw.println("\uFEFFAssignment Unique ID\tTask Unique ID\tTask Name\tResource Unique ID\tResource Name\tStart Date");

         for (ResourceAssignment assignment : assignments)
         {
            Resource resource = assignment.getResource();
            if (resource == null)
            {
               continue;
            }

            Task task = assignment.getTask();

            buffer.clear();
            buffer.add(String.valueOf(assignment.getUniqueID()));
            buffer.add(String.valueOf(task.getUniqueID()));
            buffer.add(formatString(task.getName()));
            buffer.add(String.valueOf(resource.getUniqueID()));
            buffer.add(formatString(resource.getName()));

            buffer.add(DATE_FORMAT.format(assignment.getStart()));

            List<LocalDateTimeRange> timescale = helper.createTimescale(assignment.getStart(), assignment.getFinish(), TimescaleUnits.DAYS);
            List<Duration> work = fn.apply(assignment, timescale, TimeUnit.MINUTES);
            if (assignment.getStart().toLocalDate().isEqual(assignment.getFinish().toLocalDate()) && assignment.getActualWork().getDuration() == 0.0)
            {
               if (assignment.getActualStart() != null)
               {
                  buffer.add("0");
               }
            }
            else
            {
               work.forEach(w -> buffer.add(formatDuration(w)));
               trimList(buffer);
            }

            pw.println(String.join("\t", buffer));
         }
      }
   }

   private static String formatString(String value)
   {
      if (value == null)
      {
         value = "";
      }
      return "\"" + value + "\"";
   }

   private String formatDuration(Duration duration)
   {
      if (duration == null)
      {
         return "";
      }

      return DECIMAL_FORMAT.format(Math.round(duration.getDuration()));
   }

   private void trimList(List<String> list)
   {
      while (list.get(list.size()-1).isEmpty())
      {
         list.remove(list.size()-1);
      }
   }

   private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
   private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.#");
}
