package net.sf.mpxj.cpm;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import net.sf.mpxj.ActivityType;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Task;
import net.sf.mpxj.TaskField;
import net.sf.mpxj.reader.UniversalProjectReader;

public class PrimaveraSchedulerTest
{
   public static void main(String[] argv) throws Exception
   {
      if (argv.length != 1)
      {
         System.out.println("Usage: PrimaveraSchedulerTest <file or base folder>");
         return;
      }

      File target = new File(argv[0]);
      PrimaveraSchedulerTest test = new PrimaveraSchedulerTest();

      if (target.isDirectory())
      {
         test.process(target, ".xer");
      }
      else
      {
         test.process(target);
      }
   }

   public void process(File directory, String suffix) throws Exception
   {
      File[] fileList = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(suffix));
      int failed = 0;
      int skipped = 0;
      int valid = 0;
      int success = 0;

      for (File file : fileList)
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
      boolean analyseWbs = true;

      m_baselineFile = new UniversalProjectReader().read(file);
      m_workingFile = new UniversalProjectReader().read(file);

      PrimaveraScheduler scheduler = new PrimaveraScheduler(m_workingFile);

      try
      {
         LocalDateTime start = m_workingFile.getProjectProperties().getPlannedStart();
         if (start == null)
         {
            start = m_workingFile.getProjectProperties().getStartDate();
         }

         scheduler.process(start);
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

         if (workingTask.getActivityType() == ActivityType.WBS_SUMMARY)
         {
            continue;
         }

         if (workingTask.getSummary() && NO_WBS_TEST.contains(file.getName().toLowerCase()))
         {
            analyseWbs = false;
            continue;
         }

         compare(baselineTask, workingTask);
      }

      if (m_forwardErrorCount == 0 && m_backwardErrorCount == 0)
      {
         System.out.println("done.");
         return true;
      }

      System.out.println("failed.");
      System.out.println("Project ID: " + m_baselineFile.getProjectProperties().getProjectID());
      System.out.println(m_baselineFile.getProjectProperties().getSchedulingProgressedActivities());
      System.out.println("Forward errors: " + m_forwardErrorCount);
      System.out.println("Backward errors: " + m_backwardErrorCount);
      //analyseFailures(analyseWbs);
      System.out.println("DONE");
      return false;
   }

   private void compare(Task baseline, Task working)
   {
      boolean earlyStartFailed = !compareDates(baseline, working, TaskField.EARLY_START);
      boolean earlyFinishFailed = !compareDates(baseline, working, TaskField.EARLY_FINISH);
      boolean startFailed = !compareDates(baseline, working, TaskField.START);
      boolean finishFailed = !compareDates(baseline, working, TaskField.FINISH);
      boolean actualStartFailed = !compareDates(baseline, working, TaskField.ACTUAL_START);
      boolean actualFinishFailed = !compareDates(baseline, working, TaskField.ACTUAL_FINISH);
      boolean remainingEarlyStartFailed = !compareDates(baseline, working, TaskField.REMAINING_EARLY_START);
      boolean remainingEarlyFinishFailed = !compareDates(baseline, working, TaskField.REMAINING_EARLY_FINISH);
      if (earlyStartFailed || earlyFinishFailed || startFailed || finishFailed || actualStartFailed || actualFinishFailed || remainingEarlyStartFailed || remainingEarlyFinishFailed)
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

   private void analyseFailures(boolean analyseWbs) throws CycleException
   {
      List<Task> activities = new DepthFirstGraphSort(m_workingFile, PrimaveraScheduler::isActivity).sort();
      List<Task> levelOfEffortActivities = new DepthFirstGraphSort(m_workingFile, PrimaveraScheduler::isLevelOfEffortActivity).sort();
      List<Task> wbs = m_workingFile.getTasks().stream().filter(t -> t.getSummary()).collect(Collectors.toList());

      // Sort so we can see errors at the bottom first, as these are rolled up.
      Collections.reverse(wbs);

      if (m_forwardErrorCount != 0)
      {
         activities.forEach(t -> analyseForwardError(t));
         levelOfEffortActivities.forEach(t -> analyseForwardError(t));
         if (analyseWbs)
         {
            wbs.forEach(t -> analyseForwardError(t));
         }
      }

      if (m_backwardErrorCount != 0)
      {
         Collections.reverse(activities);
         Collections.reverse(levelOfEffortActivities);
         activities.forEach(t -> analyseBackwardError(t));
         levelOfEffortActivities.forEach(t -> analyseBackwardError(t));
         if (analyseWbs)
         {
            wbs.forEach(t -> analyseBackwardError(t));
         }
      }
   }

   private void analyseForwardError(Task working)
   {
      Task baseline = m_baselineFile.getTaskByUniqueID(working.getUniqueID());
      boolean earlyStartFail = !compareDates(baseline, working, TaskField.EARLY_START);
      boolean earlyFinishFail = !compareDates(baseline, working, TaskField.EARLY_FINISH);
      boolean startFail = !compareDates(baseline, working, TaskField.START);
      boolean finishFail = !compareDates(baseline, working, TaskField.FINISH);
      boolean actualStartFail = !compareDates(baseline, working, TaskField.ACTUAL_START);
      boolean actualFinishFail = !compareDates(baseline, working, TaskField.ACTUAL_FINISH);
      boolean remainingEarlyStartFail = !compareDates(baseline, working, TaskField.REMAINING_EARLY_START);
      boolean remainingEarlyFinishFail = !compareDates(baseline, working, TaskField.REMAINING_EARLY_FINISH);

      System.out.println((working.getActivityID() == null ? "" : working.getActivityID()+ " ") + working);
      System.out.println("Early Start: " + baseline.getEarlyStart() + " " + working.getEarlyStart() + (earlyStartFail ? " FAIL" : ""));
      System.out.println("Early Finish: " + baseline.getEarlyFinish() + " " + working.getEarlyFinish() + (earlyFinishFail ? " FAIL" : ""));
      System.out.println("Start: " + baseline.getStart() + " " + working.getStart() + (startFail ? " FAIL" : ""));
      System.out.println("Finish: " + baseline.getFinish() + " " + working.getFinish() + (finishFail ? " FAIL" : ""));
      System.out.println("Actual Start: " + baseline.getActualStart() + " " + working.getActualStart() + (actualStartFail ? " FAIL" : ""));
      System.out.println("Actual Finish: " + baseline.getActualFinish() + " " + working.getActualFinish() + (actualFinishFail ? " FAIL" : ""));
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
      List<Task> result = new ArrayList<>();
      result.addAll(parent.getChildTasks());
      for (Task task : parent.getChildTasks())
      {
         result.addAll(allChildTasks(task));
      }
      return result;
   }

   private ProjectFile m_baselineFile;
   private ProjectFile m_workingFile;
   private int m_forwardErrorCount;
   private int m_backwardErrorCount;

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

      // Not actually unreadable, but contains multiple projects
      UNREADABLE_FILES.add("preliminary-shout.xer");
      UNREADABLE_FILES.add("mutual-viewer.xer");
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
      USE_SCHEDULED_COPY.add("supreme-nurse.xer");
      USE_SCHEDULED_COPY.add("multicolor-nonconformist.xer");
      USE_SCHEDULED_COPY.add("supreme-convention.xer");
      USE_SCHEDULED_COPY.add("waspish-grant.xer");
      USE_SCHEDULED_COPY.add("dramatic-male.xer");
      USE_SCHEDULED_COPY.add("sacrosanct-ozone.xer");
      USE_SCHEDULED_COPY.add("doubtful-contractor.xer");
      USE_SCHEDULED_COPY.add("aloof-proton.xer");
      USE_SCHEDULED_COPY.add("outstanding-vaudeville.xer");
      USE_SCHEDULED_COPY.add("warm-bastion.encoding.xer");
      USE_SCHEDULED_COPY.add("sadder-withdrawal.xer");
   }

   private static final Set<String> NO_WBS_TEST = new HashSet<>();
   static
   {
      // LOE activities affect WBS roll up
      NO_WBS_TEST.add("raised-walker-coverage.xer");
      NO_WBS_TEST.add("dispassionate-vertex.xer");
      NO_WBS_TEST.add("merriest-offering.xer");

      // Date disagreement with P6 affects rollup
      NO_WBS_TEST.add("aloof-proton-task-dependent.xer");
      NO_WBS_TEST.add("aloof-proton-coverage.xer");
      NO_WBS_TEST.add("aloof-proton-scheduled.xer");
      NO_WBS_TEST.add("detailed-librarian.xer");
   }

   private static final Set<String> EXCLUDED_FILES = new HashSet<>();
   static
   {
      // Resource dependent activity with resource assignments
      EXCLUDED_FILES.add("steps.xer");
      EXCLUDED_FILES.add("prospective-interference.xer");
      EXCLUDED_FILES.add("mythological-flourish.xer");
      EXCLUDED_FILES.add("computational-infection.xer");
      EXCLUDED_FILES.add("virile-schema.xer");
      EXCLUDED_FILES.add("middle-altar.xer");
      EXCLUDED_FILES.add("elected-orange.xer");
      EXCLUDED_FILES.add("role-code-test.xer");
      EXCLUDED_FILES.add("assignment-code-test.xer");
      EXCLUDED_FILES.add("orphic-chastisement-scheduled.xer");
      EXCLUDED_FILES.add("alive-lap.xer");

      // Create XER versions?
      EXCLUDED_FILES.add("baseline-issue.xml");
      EXCLUDED_FILES.add("prod00914.xml");

      // Resource dependent activity - for coverage testing only
      EXCLUDED_FILES.add("prospective-interference.xml");
      EXCLUDED_FILES.add("assignment-code-test.xml");
      EXCLUDED_FILES.add("role-code-test.xml");
      EXCLUDED_FILES.add("steps.xml");
      EXCLUDED_FILES.add("comments-relation-test-scheduled.xml");
      EXCLUDED_FILES.add("computational-infection.xml");
      EXCLUDED_FILES.add("restricted-garden.xml");
      EXCLUDED_FILES.add("unmistakable-client.xml");
      EXCLUDED_FILES.add("virile-schema.xml");

      // Milestone early finish lands on a non-working time in lag calendar
      // but working time in task calendar. Correct next work start from lag calendar?
      EXCLUDED_FILES.add("thinner-council-task-dependent-no-alap.xer");

      // Progress Override
      EXCLUDED_FILES.add("passionate-lounge-scheduled.xer");
      EXCLUDED_FILES.add("orange-parade.xer");

      // Schedule contains a loop
      EXCLUDED_FILES.add("calendar_missing_info.xer");
      EXCLUDED_FILES.add("incomprehensible-stockroom.xer");
      EXCLUDED_FILES.add("baltic-laugh.xer");

      // Rounding issue? Makes for a 1 minute difference on one activity
      EXCLUDED_FILES.add("fleet-salary.xer");
      EXCLUDED_FILES.add("global-sociology.xer");

      // LOE weirdness
      EXCLUDED_FILES.add("legislative-survey.xer");
      EXCLUDED_FILES.add("proportional-revolution.xer");
      EXCLUDED_FILES.add("toxic-end.xer");
      EXCLUDED_FILES.add("stuffy-sturgeon.xer");
      EXCLUDED_FILES.add("keen-knock.xer");

      // TODO: to investigate
      EXCLUDED_FILES.add("thinner-council.xer");
   }
}
