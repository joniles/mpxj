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

      for(File file : fileList)
      {
         String name = file.getName().toLowerCase();
         if (EXCLUDED_FILES.contains(name))
         {
            continue;
         }
         process(file, scheduler);
      }
   }

   public void process(File file, Function<ProjectFile, Scheduler> scheduler) throws Exception
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
      }
      else
      {
         System.out.println("failed.");
         System.out.println(m_baselineFile.getProjectProperties().getSchedulingProgressedActivities());
         System.out.println("Forward errors: " + m_forwardErrorCount);
         System.out.println("Backward errors: " + m_backwardErrorCount);
         analyseFailures();
         System.out.println("DONE");
      }
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
      EXCLUDED_FILES.add("restricted-garden.xml");

      // Can't import into P6 to debug
      EXCLUDED_FILES.add("tender-workforce.xer");
      EXCLUDED_FILES.add("mortal-duct.xer");
      EXCLUDED_FILES.add("narrower-encouragement.xer");
      EXCLUDED_FILES.add("invalid-calendar-data.encoding.xer");
      EXCLUDED_FILES.add("duplicate-relation-uid.xer");
      EXCLUDED_FILES.add("hotter-gunmen.xml");
      EXCLUDED_FILES.add("kindly-dissolve.xml");

      // Aligns with MPXJ when scheduled
      EXCLUDED_FILES.add("teenage-contest.encoding.xer");
      EXCLUDED_FILES.add("orphic-chastisement.xer");
      EXCLUDED_FILES.add("unlined-customhouse.xer");
      EXCLUDED_FILES.add("exhaustible-concussion.xer");
      EXCLUDED_FILES.add("surface-jealousy.xer");
      EXCLUDED_FILES.add("passionate-lounge.xer");
      EXCLUDED_FILES.add("passionate-lounge.xml");
      EXCLUDED_FILES.add("synthetic-moire.xer");
      EXCLUDED_FILES.add("comments-relation-test.xer");
      EXCLUDED_FILES.add("dense-cushion.xer");
      EXCLUDED_FILES.add("frightened-heat.xer");
      EXCLUDED_FILES.add("nrg00950.xer");
      EXCLUDED_FILES.add("plain-move.xer");
      EXCLUDED_FILES.add("manic-relativity.xml");
      EXCLUDED_FILES.add("supreme-convention.xml");
      EXCLUDED_FILES.add("udf-test.xml");
      EXCLUDED_FILES.add("garish-biophysicist.xml");
      EXCLUDED_FILES.add("prime-chiropractor.xml");
      EXCLUDED_FILES.add("comments-relation-test.xml");

      // Don't understand FF relationship behaviour
      EXCLUDED_FILES.add("passionate-lounge-scheduled.xer"); // PROGRESS_OVERRIDE
      EXCLUDED_FILES.add("passionate-lounge-1.xml"); // PROGRESS_OVERRIDE

      // Don't understand SS relationship behaviour
      EXCLUDED_FILES.add("dense-cushion-scheduled.xer");
      EXCLUDED_FILES.add("teenage-contest-scheduled.xer");
      EXCLUDED_FILES.add("orange-parade.xer"); // PROGRESS_OVERRIDE
      EXCLUDED_FILES.add("orange-parade.xml"); // PROGRESS_OVERRIDE
      EXCLUDED_FILES.add("stuffy-sturgeon.xml");

      // ALAP weirdness
      EXCLUDED_FILES.add("nasty-census.xer");
      EXCLUDED_FILES.add("virtual-mast.xer");
      EXCLUDED_FILES.add("specific-academy.xer");
      EXCLUDED_FILES.add("toxic-end.xer");
      EXCLUDED_FILES.add("missing-limestone.xer");
      EXCLUDED_FILES.add("outstanding-vaudeville.xer");
      EXCLUDED_FILES.add("barbaric-pat.xer");

      // Schedule contains a loop
      EXCLUDED_FILES.add("calendar_missing_info.xer");
      EXCLUDED_FILES.add("incomprehensible-stockroom.xer");
      EXCLUDED_FILES.add("baltic-laugh.xer");

      // Time rounding issue
      EXCLUDED_FILES.add("global-sociology.xml");

      // PMXML contains remaining early/late start/finish dates not
      // early/late start/finish. There appears to be a difference...
      EXCLUDED_FILES.add("fleet-salary.xml");

      // Calculation of late finish date for activity with no successors
      EXCLUDED_FILES.add("prime-chiropractor-scheduled.xml");
      EXCLUDED_FILES.add("golden-aperture.xml");

      // Forward pass logic differs from other working files - can't see why
      EXCLUDED_FILES.add("naval-cancer.xml");

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
