/*
 * file:       TimephasedExportUtility
 * author:     Jon Iles
 * date:       2026-11-19
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

import org.apache.commons.lang3.function.TriFunction;
import org.mpxj.Duration;
import org.mpxj.LocalDateTimeRange;
import org.mpxj.ProjectFile;
import org.mpxj.Resource;
import org.mpxj.ResourceAssignment;
import org.mpxj.ResourceType;
import org.mpxj.Task;
import org.mpxj.TimeUnit;
import org.mpxj.TimescaleUnits;
import org.mpxj.common.TimescaleHelper;
import org.mpxj.reader.UniversalProjectReader;

/**
 * Allows export of timephased data to a file in a format which makes
 * it straightforward to compare with the same set of data exported
 * from Microsoft Project via a VBA macro.
 */
public class TimephasedExportUtility
{
   /**
    * Expects the name of the input file, and optionally
    * the name of the output file. If the output file is not
    * supplied the input file name is used with an additional ".txt"
    * suffix.
    *
    * @param argv command line arguments
    */
   public static void main(String[] argv) throws Exception
   {
      if (argv.length < 1 || argv.length > 2)
      {
         System.out.println("Usage: <input project file> [output text file]");
         return;
      }

      ProjectFile file = new UniversalProjectReader().read(argv[0]);
      String outputFile = argv.length == 2 ? argv[1] : argv[0]+ ".txt";

      new TimephasedExportUtility().export(file, new File(outputFile), ResourceAssignment::getTimephasedActualWork);
   }

   /**
    * Method used to export timephased data to a file.
    *
    * @param file input file
    * @param output output file
    * @param fn function used to generate timephased data for a resource assignment
    */
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

            if (resource.getType() != ResourceType.COST)
            {
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
                  work.forEach(w -> buffer.add(formatDuration(w, resource.getType() == ResourceType.MATERIAL)));
                  trimList(buffer);
               }
            }

            pw.println(String.join("\t", buffer));
         }
      }
   }

   /**
    * Format a string to match formatted output from MS Project VBA.
    *
    * @param value string value
    * @return formatted value
    */
   private static String formatString(String value)
   {
      if (value == null)
      {
         value = "";
      }

      if (value.indexOf('"') != -1)
      {
         value = value.replace("\"", "\"\"");
      }

      return "\"" + value + "\"";
   }

   /**
    * Format a duration value as an integer number of minutes.
    *
    * @param duration duration value
    * @param materialResource true if this is a material resource
    * @return formatted value
    */
   private String formatDuration(Duration duration, boolean materialResource)
   {
      if (duration == null)
      {
         return "";
      }

      double value = duration.getDuration();
      if (materialResource)
      {
         value /= 60;
      }

      return DECIMAL_FORMAT.format(Math.round(value));
   }

   /**
    * Remove empty trailing empty values before writing to the outut file.
    *
    * @param list trimmed list
    */
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
