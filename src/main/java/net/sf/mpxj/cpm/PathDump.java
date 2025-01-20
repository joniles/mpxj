package net.sf.mpxj.cpm;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Task;
import net.sf.mpxj.reader.UniversalProjectReader;

public class PathDump
{
   public static final void main(String[] argv) throws Exception
   {
      if (argv.length != 1)
      {
         System.out.println("Usage: PathDump <file>");
         return;
      }

      ProjectFile file = new UniversalProjectReader().read(argv[0]);

      List<List<Task>> paths = new PathHelper(file).getAllPaths();
      System.out.println(writeHeader());
      paths.forEach(PathDump::writePath);
   }

   private static void writePath(List<Task> path)
   {
      path.forEach(t -> System.out.println(writeTask(t)));
      System.out.println();
   }

   private static String writeHeader()
   {
      return String.join("\t",
         "Unique ID",
         "Activity ID",
         "Name",
         "Early Start",
         "Early Finish",
         "Late Start",
         "Late Finish",
         "Remaining Duration",
         "Total Slack"
      );
   }

   private static String writeTask(Task task)
   {
      return String.join("\t",
         task.getUniqueID().toString(),
         task.getActivityID() == null ? "" : task.getActivityID(),
         task.getName(),
         task.getEarlyStart().format(DATE_FORMAT),
         task.getEarlyFinish().format(DATE_FORMAT),
         task.getLateStart().format(DATE_FORMAT),
         task.getLateFinish().format(DATE_FORMAT),
         task.getRemainingDuration().toString(),
         task.getTotalSlack().toString()
      );
   }

   private static final DateTimeFormatter DATE_FORMAT = new DateTimeFormatterBuilder().parseLenient().appendPattern("yyyy-MM-dd HH:mm:ss").toFormatter();
}
