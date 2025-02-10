package net.sf.mpxj.cpm;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

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
         //test.process(new File(target, "mpp"), ".mpp", MICROSOFT_PROJECT);
         test.process(new File(target, "XER"), ".xer", PRIMAVERA_P6);
         // Scheduling from XML unreliable - get all data working via XER first
         //test.process(new File(target, "PMXML"), ".xml", PRIMAVERA_P6);
      }
      else
      {
         test.process(target, target.getName().toLowerCase().endsWith(".mpp") ? MICROSOFT_PROJECT : PRIMAVERA_P6);
      }
   }

   public void process(File directory, String suffix, Function<ProjectFile, Scheduler> scheduler) throws Exception
   {
      File[] fileList = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(suffix));
      int failed = 0;
      int skipped = 0;
      int valid = 0;
      int success = 0;

      for(File file : fileList)
      {
         String name = file.getName().toLowerCase();
         if (UNREADABLE_FILES.contains(name))
         {
            continue;
         }

         if (USE_SCHEDULED_COPY.contains(name))
         {
            continue;
         }

         ++valid;

         if (EXCLUDED_FILES.contains(name))
         {
            ++skipped;
            continue;
         }

         if (process(file, scheduler))
         {
            ++success;
         }
         else
         {
            ++failed;
         }
      }


      System.out.println();
      System.out.println("Files: " + fileList.length);
      System.out.println("Skipped: " + skipped);
      System.out.println("Success: " + success);
      System.out.println("Failed: " + failed);
      System.out.println("Success %: " + (success * 100.0 / valid));
   }

   public boolean process(File file, Function<ProjectFile, Scheduler> scheduler) throws Exception
   {
      System.out.print("Processing " + file + " ... ");
      m_forwardErrorCount = 0;
      m_backwardErrorCount = 0;

      m_baselineFile = new UniversalProjectReader().read(file);
      m_workingFile = new UniversalProjectReader().read(file);

      scheduler.apply(m_workingFile).process(m_workingFile.getProjectProperties().getStartDate());

      for (Task baselineTask : m_baselineFile.getTasks())
      {
         compare(baselineTask, m_workingFile.getTaskByUniqueID(baselineTask.getUniqueID()));
      }

      if (m_forwardErrorCount == 0&& m_backwardErrorCount == 0)
      {
         System.out.println("done.");
         return true;
      }

      System.out.println("failed.");
      System.out.println("Project ID: " + m_baselineFile.getProjectProperties().getProjectID());
      System.out.println(m_baselineFile.getProjectProperties().getSchedulingProgressedActivities());
      System.out.println("Forward errors: " + m_forwardErrorCount);
      System.out.println("Backward errors: " + m_backwardErrorCount);
      analyseFailures();
      System.out.println("DONE");
      return false;
   }

   private void compare(Task baseline, Task working)
   {
      if (MicrosoftScheduler.ignoreTask(baseline))
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
            if (MicrosoftScheduler.ignoreTask(working))
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
            if (MicrosoftScheduler.ignoreTask(working))
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

   private ProjectFile m_baselineFile;
   private ProjectFile m_workingFile;
   private int m_forwardErrorCount;
   private int m_backwardErrorCount;

   private static final Function<ProjectFile, Scheduler> MICROSOFT_PROJECT = p -> new MicrosoftScheduler(p);
   private static final Function<ProjectFile, Scheduler> PRIMAVERA_P6 = p -> new PrimaveraScheduler(p);

   private static final Set<String> UNREADABLE_FILES = new HashSet<>();
   static
   {
      // Can't import into P6 to debug
      UNREADABLE_FILES.add("tender-workforce.xer");
      UNREADABLE_FILES.add("mortal-duct.xer");
      UNREADABLE_FILES.add("narrower-encouragement.xer");
      UNREADABLE_FILES.add("invalid-calendar-data.encoding.xer");
      UNREADABLE_FILES.add("duplicate-relation-uid.xer");
      UNREADABLE_FILES.add("kindly-dissolve.xml");
   }

   private static final Set<String> USE_SCHEDULED_COPY = new HashSet<>();
   static
   {
      // Aligns with MPXJ when scheduled
      USE_SCHEDULED_COPY.add("teenage-contest.encoding.xer");
      USE_SCHEDULED_COPY.add("orphic-chastisement.xer");
      USE_SCHEDULED_COPY.add("unlined-customhouse.xer");
      USE_SCHEDULED_COPY.add("exhaustible-concussion.xer");
      USE_SCHEDULED_COPY.add("surface-jealousy.xer");
      USE_SCHEDULED_COPY.add("passionate-lounge.xer");
      USE_SCHEDULED_COPY.add("passionate-lounge.xml");
      USE_SCHEDULED_COPY.add("synthetic-moire.xer");
      USE_SCHEDULED_COPY.add("comments-relation-test.xer");
      USE_SCHEDULED_COPY.add("dense-cushion.xer");
      USE_SCHEDULED_COPY.add("frightened-heat.xer");
      USE_SCHEDULED_COPY.add("nrg00950.xer");
      USE_SCHEDULED_COPY.add("plain-move.xer");
      USE_SCHEDULED_COPY.add("manic-relativity.xml");
      USE_SCHEDULED_COPY.add("supreme-convention.xml");
      USE_SCHEDULED_COPY.add("udf-test.xml");
      USE_SCHEDULED_COPY.add("garish-biophysicist.xml");
      USE_SCHEDULED_COPY.add("prime-chiropractor.xml");
      USE_SCHEDULED_COPY.add("comments-relation-test.xml");
      USE_SCHEDULED_COPY.add("smoother-melodrama.xer");
      USE_SCHEDULED_COPY.add("nasty-census.xer");
      USE_SCHEDULED_COPY.add("virtual-mast.xer");
      USE_SCHEDULED_COPY.add("specific-academy.xer");
   }

   private static final Set<String> EXCLUDED_FILES = new HashSet<>();
   static
   {
      // Resource dependent activity
      EXCLUDED_FILES.add("elected-orange.xer");
      EXCLUDED_FILES.add("role-code-test.xer");
      EXCLUDED_FILES.add("steps.xer");
      EXCLUDED_FILES.add("aloof-proton.xer");
      EXCLUDED_FILES.add("thinner-council.xer");
      EXCLUDED_FILES.add("assignment-code-test.xer");
      EXCLUDED_FILES.add("prospective-interference.xer");
      EXCLUDED_FILES.add("mythological-flourish.xer");
      EXCLUDED_FILES.add("orphic-chastisement-scheduled.xer");
      EXCLUDED_FILES.add("alive-lap.xer");
      EXCLUDED_FILES.add("prospective-interference.xml");
      EXCLUDED_FILES.add("assignment-code-test.xml");
      EXCLUDED_FILES.add("role-code-test.xml");
      EXCLUDED_FILES.add("steps.xml");
      EXCLUDED_FILES.add("baseline-issue.xml");
      EXCLUDED_FILES.add("prod00914.xml");
      EXCLUDED_FILES.add("virile-schema.xml");
      EXCLUDED_FILES.add("restricted-garden.xer");
      EXCLUDED_FILES.add("computational-infection.xer");
      EXCLUDED_FILES.add("unmistakable-client.xer");
      EXCLUDED_FILES.add("virile-schema.xer");
      EXCLUDED_FILES.add("middle-altar.xer");

      // Multiple projects
      EXCLUDED_FILES.add("preliminary-shout.xer");
      EXCLUDED_FILES.add("mutual-viewer.xer");

      // Forward pass: chain of ALAP, following by a normal SS, not clear how the SS early dates are calculated
      EXCLUDED_FILES.add("ideal-tilt.xer");

      // Backward pass: non-obvious adjustment to lag being used in FS relationship
      EXCLUDED_FILES.add("keen-knock.xer");

      // Forward pass: non-obvious adjustment to lag being used in FS relationship
      EXCLUDED_FILES.add("lovable-bridgehead.xer");

      // Backward pass: non-obvious adjustment to lag being used in FS relationship
      EXCLUDED_FILES.add("naval-cancer.xer");

      // Don't understand FF relationship behaviour
      EXCLUDED_FILES.add("passionate-lounge-scheduled.xer"); // PROGRESS_OVERRIDE
      EXCLUDED_FILES.add("passionate-lounge-1.xml"); // PROGRESS_OVERRIDE

      // Don't understand SS relationship behaviour
      EXCLUDED_FILES.add("dense-cushion-scheduled.xer"); // the remaining issue here is a 5h difference - looks like it is down to activities with different calenders
      EXCLUDED_FILES.add("teenage-contest-scheduled.xer");
      EXCLUDED_FILES.add("orange-parade.xer"); // PROGRESS_OVERRIDE

      // ALAP weirdness
      EXCLUDED_FILES.add("barbaric-pat.xer");

      // Schedule contains a loop
      EXCLUDED_FILES.add("calendar_missing_info.xer");
      EXCLUDED_FILES.add("incomprehensible-stockroom.xer");
      EXCLUDED_FILES.add("baltic-laugh.xer");

      // Rounding issue? Makes for a 1 minute difference on one activity
      EXCLUDED_FILES.add("fleet-salary.xer");
      EXCLUDED_FILES.add("global-sociology.xer");

      // Forward pass 1 day out - calendar issue?
      EXCLUDED_FILES.add("radical-reach.xer");

      // Fix in code... but conditional?
      EXCLUDED_FILES.add("single-supervision.xer");

      // Misc MPP files
      EXCLUDED_FILES.add("photographic-magic.mpp"); // External tasks used but not visible in MSP
      EXCLUDED_FILES.add("bizarre-doomsday.mpp"); // Manually scheduled task without any explicitly supplied dates
      EXCLUDED_FILES.add("optimistic-layer.mpp"); // TODO: maybe we're not working with calendars correctly - should be considering the resource calendars and merging?
      EXCLUDED_FILES.add("adequate-function.mpp"); // TODO: assignment leveling delay
      EXCLUDED_FILES.add("serene-birthright.mpp"); // TODO: oddity handling one late finish constraint versus end of working time
      EXCLUDED_FILES.add("microsomal-finisher.mpp"); // TODO: late finish, project end determined by constrained task - use unconstrained early finish as project end?
      EXCLUDED_FILES.add("circulatory-collapse.mpp"); // TODO: needs calculation at assignment level?
      EXCLUDED_FILES.add("onrushing-stratification.mpp"); // MPP reading issue: missing predecessor

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
}
