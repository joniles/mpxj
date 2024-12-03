package net.sf.mpxj.cpm;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.mpxj.ActivityType;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Task;
import net.sf.mpxj.TaskField;
import net.sf.mpxj.common.LocalDateTimeHelper;
import net.sf.mpxj.reader.UniversalProjectReader;

public class CpmTest
{
   public static void main(String[] argv) throws Exception
   {
      if (argv.length != 1)
      {
         System.out.println("Usage: CpmTest <file or base folder>");
         return;
      }

      File target = new File(argv[0]);
      CpmTest test = new CpmTest();

      if (target.isDirectory())
      {
         //test.process(new File(target, "mpp"), ".mpp", ScheduleStrategy.MICROSOFT_PROJECT);
         test.process(new File(target, "xer"), ".xer", ScheduleStrategy.PRIMAVERA_P6);
      }
      else
      {
         ScheduleStrategy strategy = target.getName().toLowerCase().endsWith(".mpp") ? ScheduleStrategy.MICROSOFT_PROJECT : ScheduleStrategy.PRIMAVERA_P6;
         test.process(target, strategy);
      }
   }

   public void process(File directory, String suffix, ScheduleStrategy strategy) throws Exception
   {
      File[] fileList = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(suffix));

      for(File file : fileList)
      {
         String name = file.getName().toLowerCase();
         if (EXCLUDED_FILES.contains(name) || (!INCLUDED_FILES.isEmpty() && !INCLUDED_FILES.contains(name)))
         {
            continue;
         }
         process(file, strategy);
      }
   }

   public void process(File file, ScheduleStrategy strategy) throws Exception
   {
      System.out.print("Processing " + file + " ... ");
      m_forwardErrorCount = 0;
      m_backwardErrorCount = 0;

      m_baselineFile = new UniversalProjectReader().read(file);
      m_workingFile = new UniversalProjectReader().read(file);

      new Schedule(strategy, m_workingFile).process(m_workingFile.getProjectProperties().getStartDate());

      for (Task baselineTask : m_baselineFile.getTasks())
      {
         compare(baselineTask, m_workingFile.getTaskByUniqueID(baselineTask.getUniqueID()));
      }

      if (m_forwardErrorCount == 0&& m_backwardErrorCount == 0)
      {
         System.out.println("done.");
      }
      else
      {
         System.out.println("failed.");
         System.out.println("Forward errors: " + m_forwardErrorCount);
         System.out.println("Backward errors: " + m_backwardErrorCount);
         analyseFailures();
         System.out.println("DONE");
      }
   }

   private void compare(Task baseline, Task working)
   {
      if (ignoreTask(baseline))
      {
         return;
      }

      boolean earlyStartFailed = !compare(baseline, working, TaskField.EARLY_START);
      boolean earlyFinishFailed = !compare(baseline, working, TaskField.EARLY_FINISH);
      if (earlyStartFailed || earlyFinishFailed)
      {
         ++m_forwardErrorCount;
      }

      boolean lateStartFailed = !compare(baseline, working, TaskField.LATE_START);
      boolean lateFinishFailed = !compare(baseline, working, TaskField.LATE_FINISH);
      if (lateStartFailed || lateFinishFailed)
      {
         ++m_backwardErrorCount;
      }
   }

   private boolean compare(Task baseline, Task working, TaskField field)
   {
      boolean result = true;
      LocalDateTime baselineDate = (LocalDateTime)baseline.get(field);
      if (baselineDate == null)
      {
         // We have XER files where some of the attributes we'd expect to be populated are not present. Skip these.
         return true;
      }

      LocalDateTime workingDate = (LocalDateTime)working.get(field);
      if (LocalDateTimeHelper.compare(baselineDate, workingDate) != 0)
      {
         ProjectCalendar calendar = baseline.getEffectiveCalendar();
         if (calendar.getNextWorkStart(workingDate).isEqual(baselineDate) || calendar.getNextWorkStart(baselineDate).isEqual(workingDate))
         {
            //System.out.print(" WARN");
         }
         else
         {
            result = false;
         }
      }

      return result;
   }

   private boolean compareDates(Task baseline, Task working, TaskField field)
   {
      LocalDateTime baselineDate = (LocalDateTime)baseline.get(field);
      if (baselineDate == null)
      {
         // We have XER files where some of the attributes we'd expect to be populated are not present. Skip these.
         return true;
      }

      LocalDateTime workingDate = (LocalDateTime)working.get(field);

      if (baselineDate.isEqual(workingDate))
      {
         return true;
      }

      ProjectCalendar calendar = baseline.getEffectiveCalendar();
      return calendar.getNextWorkStart(workingDate).isEqual(baselineDate) || calendar.getNextWorkStart(baselineDate).isEqual(workingDate);
   }

   private void analyseFailures() throws CycleException
   {
      List<Task> tasks = new DepthFirstGraphSort(m_workingFile).sort();

      if (m_forwardErrorCount != 0)
      {
         for (Task working : tasks)
         {
            if (ignoreTask(working))
            {
               continue;
            }

            Task baseline = m_baselineFile.getTaskByUniqueID(working.getUniqueID());
            boolean earlyStartFail = !compareDates(baseline, working, TaskField.EARLY_START);
            boolean earlyFinishFail = !compareDates(baseline, working, TaskField.EARLY_FINISH);

            System.out.println((working.getActivityID() == null ? "" : working.getActivityID()+ " ") + working);
            System.out.println("Early Start: " + baseline.getEarlyStart() + " " + working.getEarlyStart() + (earlyStartFail ? " FAIL" : ""));
            System.out.println("Early Finish: " + baseline.getEarlyFinish() + " " + working.getEarlyFinish() + (earlyFinishFail ? " FAIL" : ""));
            System.out.println();
         }
      }

      if (m_backwardErrorCount != 0)
      {
         Collections.reverse(tasks);

         for (Task working : tasks)
         {
            if (ignoreTask(working))
            {
               continue;
            }

            Task baseline = m_baselineFile.getTaskByUniqueID(working.getUniqueID());
            boolean lateStartFail = !compareDates(baseline, working, TaskField.LATE_START);
            boolean lateFinishFail = !compareDates(baseline, working, TaskField.LATE_FINISH);

            System.out.println((working.getActivityID() == null ? "" : working.getActivityID()+ " ") + working);
            System.out.println("Late Start: " + baseline.getLateStart() + " " + working.getLateStart() + (lateStartFail ? " FAIL" : ""));
            System.out.println("Late Finish: " + baseline.getLateFinish() + " " + working.getLateFinish() + (lateFinishFail ? " FAIL" : ""));
            System.out.println();
         }
      }
   }

   private boolean ignoreTask(Task task)
   {
      return task.getSummary() || !task.getActive() || task.getNull() || task.getActivityType() == ActivityType.LEVEL_OF_EFFORT || task.getActivityType() == ActivityType.WBS_SUMMARY;
   }

   private ProjectFile m_baselineFile;
   private ProjectFile m_workingFile;
   private int m_forwardErrorCount;
   private int m_backwardErrorCount;

   private static final Set<String> EXCLUDED_FILES = new HashSet<>();
   static
   {
      EXCLUDED_FILES.add("photographic-magic.mpp"); // External tasks used but not visible in MSP
      EXCLUDED_FILES.add("bizarre-doomsday.mpp"); // Manually scheduled task without any explicitly supplied dates
      EXCLUDED_FILES.add("optimistic-layer.mpp"); // TODO: maybe we're not working with calendars correctly - should be considering the resource calendars and merging?
      EXCLUDED_FILES.add("adequate-function.mpp"); // TODO: assignment leveling delay
      EXCLUDED_FILES.add("serene-birthright.mpp"); // TODO: oddity handling one late finish constraint versus end of working time
      EXCLUDED_FILES.add("microsomal-finisher.mpp"); // TODO: late finish, project end determined by constrained task - use unconstrained early finish as project end?
      EXCLUDED_FILES.add("circulatory-collapse.mpp"); // TODO: needs calculation at assignment level?
      EXCLUDED_FILES.add("onrushing-stratification.mpp"); // MPP reading issue: missing predecessor
      EXCLUDED_FILES.add("smoother-melodrama.xer"); // No dates - so nothing to use as a baseline
      EXCLUDED_FILES.add("tender-workforce.xer"); // Can't import into P6 to debug
      EXCLUDED_FILES.add("nasty-census.xer"); // ALAP weirdness
      EXCLUDED_FILES.add("mortal-duct.xer"); // XER 1.0 can't import to P6

      // Scheduled from end
      EXCLUDED_FILES.add("dietetic-phrasing.mpp");

      // Manually contoured task
      EXCLUDED_FILES.add("undeveloped-slice.mpp");

      // Calculated correctly, but incorrect late dates read from MPP by MPXJ
      EXCLUDED_FILES.add("scatterbrained-tambourine.mpp");
      EXCLUDED_FILES.add("valid-wartime.mpp");
      EXCLUDED_FILES.add("harpy-gully.mpp");

      // Summary task logic
      EXCLUDED_FILES.add("oppressive-pitfall.mpp");
      EXCLUDED_FILES.add("scarlet-throughput.mpp");
      EXCLUDED_FILES.add("pulmonary-dove.mpp");
      EXCLUDED_FILES.add("topical-mamma.mpp");
      EXCLUDED_FILES.add("idle-niche.mpp");
      EXCLUDED_FILES.add("madding-portrayal.mpp");
      EXCLUDED_FILES.add("apparent-canyon.mpp");
      EXCLUDED_FILES.add("photosensitive-bluebook.mpp");
      EXCLUDED_FILES.add("orange-fur.mpp");
      EXCLUDED_FILES.add("false-rustler.mpp");
      EXCLUDED_FILES.add("bluff-shoelace.mpp");
      EXCLUDED_FILES.add("emaciated-subjectivist.mpp");
      EXCLUDED_FILES.add("blissful-schism.mpp");
      EXCLUDED_FILES.add("dread-hydrochemistry.mpp");
      EXCLUDED_FILES.add("thicker-recital.mpp");

      // Split task
      EXCLUDED_FILES.add("worrisome-definition.mpp");
      EXCLUDED_FILES.add("texan-jealousy.mpp");
      EXCLUDED_FILES.add("unsupportable-reliving.mpp");
      EXCLUDED_FILES.add("auburn-dugout.mpp");
      EXCLUDED_FILES.add("oval-ambulance.mpp");
      EXCLUDED_FILES.add("passive-inhibitor.mpp");
      EXCLUDED_FILES.add("hypophyseal-comedian.mpp");
      EXCLUDED_FILES.add("long-giant.mpp");
      EXCLUDED_FILES.add("lumbar-kimono.mpp");
      EXCLUDED_FILES.add("seasonal-standing.mpp");
      EXCLUDED_FILES.add("undisputed-empire.mpp");
      EXCLUDED_FILES.add("uninvited-friend.mpp");
      EXCLUDED_FILES.add("quickest-photoluminescence.mpp");
   }

   private static final Set<String> INCLUDED_FILES = new HashSet<>();
   static
   {
      //INCLUDED_FILES.add("".toLowerCase());
   }
}
