package net.sf.mpxj.cpm;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Task;
import net.sf.mpxj.TaskField;
import net.sf.mpxj.reader.UniversalProjectReader;

public class MicrosoftSchedulerTest
{
   public static void main(String[] argv) throws Exception
   {
      if (argv.length != 1)
      {
         System.out.println("Usage: MicrosoftSchedulerTest <file or folder>");
         return;
      }

      File target = new File(argv[0]);
      MicrosoftSchedulerTest test = new MicrosoftSchedulerTest();

      if (target.isDirectory())
      {
         test.process(target, ".mpp");
      }
      else
      {
         test.process(target);
      }
   }

   public void process(File directory, String suffix) throws Exception
   {
      m_directory = true;

      File[] fileList = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(suffix));
      int failed = 0;
      int skipped = 0;
      int valid = 0;
      int success = 0;

      for (File file : fileList)
      {
         String name = file.getName().toLowerCase();
         ++valid;

         if (EXCLUDED_FILES.contains(name))
         {
            ++skipped;
            continue;
         }

         if (process(file))
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

   public boolean process(File file) throws Exception
   {
      System.out.print("Processing " + file + " ... ");
      m_forwardErrorCount = 0;
      m_backwardErrorCount = 0;

      m_baselineFile = new UniversalProjectReader().read(file);
      m_workingFile = new UniversalProjectReader().read(file);

      MicrosoftScheduler scheduler = new MicrosoftScheduler(m_workingFile);

      try
      {
         scheduler.process(m_workingFile.getProjectProperties().getStartDate());
      }

      catch(CpmException ex)
      {
         System.out.println("failed.");
         System.out.println(ex.getMessage());
         return false;
      }

      for (Task baselineTask : m_baselineFile.getTasks())
      {
         Task workingTask = m_workingFile.getTaskByUniqueID(baselineTask.getUniqueID());

         compare(baselineTask, workingTask);
      }

      if (m_forwardErrorCount == 0 && m_backwardErrorCount == 0)
      {
         System.out.println("done.");
         return true;
      }

      System.out.println("failed.");
      System.out.println("Forward errors: " + m_forwardErrorCount);
      System.out.println("Backward errors: " + m_backwardErrorCount);

      if (!m_directory)
      {
         analyseFailures(scheduler);
      }

      System.out.println("DONE");
      return false;
   }

   private void compare(Task baseline, Task working)
   {
      boolean earlyStartFailed = !compareDates(baseline, working, TaskField.EARLY_START);
      boolean earlyFinishFailed = !compareDates(baseline, working, TaskField.EARLY_FINISH);
      boolean startFailed = !compareDates(baseline, working, TaskField.START);
      boolean finishFailed = !compareDates(baseline, working, TaskField.FINISH);
      boolean remainingEarlyStartFailed = !compareDates(baseline, working, TaskField.REMAINING_EARLY_START);
      boolean remainingEarlyFinishFailed = !compareDates(baseline, working, TaskField.REMAINING_EARLY_FINISH);
      if (earlyStartFailed || earlyFinishFailed || startFailed || finishFailed || remainingEarlyStartFailed || remainingEarlyFinishFailed)
      {
         ++m_forwardErrorCount;
      }

      boolean lateStartFailed = !compareDates(baseline, working, TaskField.LATE_START);
      boolean lateFinishFailed = !compareDates(baseline, working, TaskField.LATE_FINISH);
      boolean remainingLateStartFailed = !compareDates(baseline, working, TaskField.REMAINING_LATE_START);
      boolean remainingLateFinishFailed = !compareDates(baseline, working, TaskField.REMAINING_LATE_FINISH);
      if (lateStartFailed || lateFinishFailed || remainingLateStartFailed || remainingLateFinishFailed)
      {
         ++m_backwardErrorCount;
      }
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
      if (workingDate == null)
      {
         return false;
      }

      if (baselineDate.isEqual(workingDate))
      {
         return true;
      }

      ProjectCalendar calendar = baseline.getEffectiveCalendar();
      boolean result = calendar.getNextWorkStart(workingDate).isEqual(baselineDate) || calendar.getNextWorkStart(baselineDate).isEqual(workingDate);
      if (result || !working.getSummary())
      {
         return result;
      }

      // At this point we have failed to compare, but we have a WBS entry.
      // This doesn't have its own calendar so we need to look at the child calendars.
      // Yes, it's hacky. The real solution is to understand the logic P6 is
      // applying when it chooses between end of day or start of next day.
      //result = working.getChildTasks().stream().map(t -> t.getEffectiveCalendar()).anyMatch(c -> c.getNextWorkStart(workingDate).isEqual(baselineDate) || c.getNextWorkStart(baselineDate).isEqual(workingDate));
      result = allChildTasks(working).stream().map(t -> t.getEffectiveCalendar()).anyMatch(c -> c.getNextWorkStart(workingDate).isEqual(baselineDate) || c.getNextWorkStart(baselineDate).isEqual(workingDate));
      return result;
   }

   private void analyseFailures(MicrosoftScheduler scheduler) throws CycleException
   {
      List<Task> tasks = new DepthFirstGraphSort(m_workingFile, scheduler::isTask).sort();

      // Sort so we can see errors at the bottom first, as these are rolled up.
      List<Task> wbs = m_workingFile.getTasks().stream().filter(t -> t.getSummary()).collect(Collectors.toList());
      Collections.reverse(wbs);

      if (m_forwardErrorCount != 0)
      {
         tasks.forEach(t -> analyseForwardError(t));
         wbs.forEach(t -> analyseForwardError(t));
      }

      if (m_backwardErrorCount != 0)
      {
         Collections.reverse(tasks);
         tasks.forEach(t -> analyseBackwardError(t));
         wbs.forEach(t -> analyseBackwardError(t));
      }
   }

   private void analyseForwardError(Task working)
   {
      Task baseline = m_baselineFile.getTaskByUniqueID(working.getUniqueID());
      boolean earlyStartFail = !compareDates(baseline, working, TaskField.EARLY_START);
      boolean earlyFinishFail = !compareDates(baseline, working, TaskField.EARLY_FINISH);
      boolean startFail = !compareDates(baseline, working, TaskField.START);
      boolean finishFail = !compareDates(baseline, working, TaskField.FINISH);
      boolean remainingEarlyStartFail = !compareDates(baseline, working, TaskField.REMAINING_EARLY_START);
      boolean remainingEarlyFinishFail = !compareDates(baseline, working, TaskField.REMAINING_EARLY_FINISH);

      System.out.println((working.getActivityID() == null ? "" : working.getActivityID()+ " ") + working);
      System.out.println("Early Start: " + baseline.getEarlyStart() + " " + working.getEarlyStart() + (earlyStartFail ? " FAIL" : ""));
      System.out.println("Early Finish: " + baseline.getEarlyFinish() + " " + working.getEarlyFinish() + (earlyFinishFail ? " FAIL" : ""));
      System.out.println("Start: " + baseline.getStart() + " " + working.getStart() + (startFail ? " FAIL" : ""));
      System.out.println("Finish: " + baseline.getFinish() + " " + working.getFinish() + (finishFail ? " FAIL" : ""));
      System.out.println("Remaining Early Start: " + baseline.getRemainingEarlyStart() + " " + working.getRemainingEarlyStart() + (remainingEarlyStartFail ? " FAIL" : ""));
      System.out.println("Remaining Early Finish: " + baseline.getRemainingEarlyFinish() + " " + working.getRemainingEarlyFinish() + (remainingEarlyFinishFail ? " FAIL" : ""));
      System.out.println();
   }

   private void analyseBackwardError(Task working)
   {
      Task baseline = m_baselineFile.getTaskByUniqueID(working.getUniqueID());
      boolean lateStartFail = !compareDates(baseline, working, TaskField.LATE_START);
      boolean lateFinishFail = !compareDates(baseline, working, TaskField.LATE_FINISH);
      boolean remainingLateStartFail = !compareDates(baseline, working, TaskField.REMAINING_LATE_START);
      boolean remainingLateFinishFail = !compareDates(baseline, working, TaskField.REMAINING_LATE_FINISH);

      System.out.println((working.getActivityID() == null ? "" : working.getActivityID()+ " ") + working);
      System.out.println("Late Start: " + baseline.getLateStart() + " " + working.getLateStart() + (lateStartFail ? " FAIL" : ""));
      System.out.println("Late Finish: " + baseline.getLateFinish() + " " + working.getLateFinish() + (lateFinishFail ? " FAIL" : ""));
      System.out.println("Remaining Late Start: " + baseline.getRemainingLateStart() + " " + working.getRemainingLateStart() + (remainingLateStartFail ? " FAIL" : ""));
      System.out.println("Remaining Late Finish: " + baseline.getRemainingLateFinish() + " " + working.getRemainingLateFinish() + (remainingLateFinishFail ? " FAIL" : ""));
      System.out.println();
   }

   private List<Task> allChildTasks(Task parent)
   {
      List<Task> result = new ArrayList<>(parent.getChildTasks());
      for (Task task : parent.getChildTasks())
      {
         result.addAll(allChildTasks(task));
      }
      return result;
   }

   private boolean m_directory;
   private ProjectFile m_baselineFile;
   private ProjectFile m_workingFile;
   private int m_forwardErrorCount;
   private int m_backwardErrorCount;

   private static final Set<String> EXCLUDED_FILES = new HashSet<>();
   static
   {
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
      EXCLUDED_FILES.add("unsaid-table.mpp");
      EXCLUDED_FILES.add("olympic-layout.mpp");
      EXCLUDED_FILES.add("brownish-craving.mpp");
      EXCLUDED_FILES.add("abrupt-broom.mpp");
      EXCLUDED_FILES.add("vibrionic-show.mpp");
      EXCLUDED_FILES.add("microbial-inkling.mpp");
      EXCLUDED_FILES.add("organizational-engineering.mpp");
      EXCLUDED_FILES.add("undamaged-passing.mpp");
      EXCLUDED_FILES.add("striking-set.mpp");
      EXCLUDED_FILES.add("tiniest-solenoid.mpp");
      EXCLUDED_FILES.add("steep-leave.mpp");
      EXCLUDED_FILES.add("inappropriate-understanding.mpp");
      EXCLUDED_FILES.add("bashful-slumber.mpp");
      EXCLUDED_FILES.add("constructive-facade.mpp");
      EXCLUDED_FILES.add("lonesome-potentiometer.mpp");
      EXCLUDED_FILES.add("snippy-mortgage.mpp");
      EXCLUDED_FILES.add("hazy-viscometer.mpp");
      EXCLUDED_FILES.add("greater-furlough.mpp");
      EXCLUDED_FILES.add("filipino-corduroy.mpp");
      EXCLUDED_FILES.add("bitterest-oscillation.mpp");
      EXCLUDED_FILES.add("mediumistic-handclasp.mpp");
      EXCLUDED_FILES.add("bibliographical-veronica.mpp");
      EXCLUDED_FILES.add("ill-toilet.mpp");
      EXCLUDED_FILES.add("mechanistic-brinkmanship.mpp");
      EXCLUDED_FILES.add("sensational-haze.mpp");
      EXCLUDED_FILES.add("strangest-mulch.mpp");
      EXCLUDED_FILES.add("aberrant-acquiesence.mpp");
      EXCLUDED_FILES.add("abysmal-grandma.mpp");
      EXCLUDED_FILES.add("ashamed-annoyance.mpp");
      EXCLUDED_FILES.add("hegelian-sensing.mpp");
      EXCLUDED_FILES.add("lyrical-highlight.mpp");
      EXCLUDED_FILES.add("content-gun.mpp");
      EXCLUDED_FILES.add("suspect-catching.mpp");
      EXCLUDED_FILES.add("cleaner-purveyor.mpp");
      EXCLUDED_FILES.add("marital-peace.mpp");
      EXCLUDED_FILES.add("odd-robin.mpp");
      EXCLUDED_FILES.add("beady-musket.mpp");
      EXCLUDED_FILES.add("naval-showing.mpp");
      EXCLUDED_FILES.add("compositional-information.mpp");
      EXCLUDED_FILES.add("tintable-casebook.mpp");
      EXCLUDED_FILES.add("equitable-capability.mpp");
      EXCLUDED_FILES.add("maternal-ecliptic.mpp");
      EXCLUDED_FILES.add("selected-kit.mpp");
      EXCLUDED_FILES.add("brash-asceticism.mpp");
      EXCLUDED_FILES.add("stilted-vaulting.mpp");
      EXCLUDED_FILES.add("outsized-hive.mpp");
      EXCLUDED_FILES.add("standard-freedom.mpp");
      EXCLUDED_FILES.add("dandy-automation.mpp");
      EXCLUDED_FILES.add("printable-powder.mpp");
      EXCLUDED_FILES.add("shrewdest-slate.mpp");
      EXCLUDED_FILES.add("pituitary-springboard.mpp");
      EXCLUDED_FILES.add("unmeshed-lab.mpp");
      EXCLUDED_FILES.add("occupational-game.mpp");
      EXCLUDED_FILES.add("sly-taste.mpp");
      EXCLUDED_FILES.add("impoverished-sluice.mpp");
      EXCLUDED_FILES.add("alternative-urgency.mpp");
      EXCLUDED_FILES.add("drunken-thrift.mpp");
      EXCLUDED_FILES.add("nocturnal-package.mpp");
      EXCLUDED_FILES.add("extraterrestrial-apologetic.mpp");
      EXCLUDED_FILES.add("uneconomical-diary.mpp");

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
