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
import net.sf.mpxj.TimeUnit;
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

         // TODO: investigate rollup logic for project summary task
         if (baselineTask.getID() == 0)
         {
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
//      boolean startFailed = !compareDates(baseline, working, TaskField.START);
//      boolean finishFailed = !compareDates(baseline, working, TaskField.FINISH);
      if (earlyStartFailed || earlyFinishFailed /*|| startFailed || finishFailed*/)
      {
         ++m_forwardErrorCount;
      }

      boolean lateStartFailed = !compareDates(baseline, working, TaskField.LATE_START);
      boolean lateFinishFailed = !compareDates(baseline, working, TaskField.LATE_FINISH);
      if (lateStartFailed || lateFinishFailed)
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

      double result = Math.abs(baseline.getEffectiveCalendar().getWork(baselineDate, workingDate, TimeUnit.MINUTES).getDuration());

      // Allowing for date arithmetic differences between MS Project and MPXJ
      return result < 0.29;
   }

   private void analyseFailures(MicrosoftScheduler scheduler) throws CycleException
   {
      //List<Task> tasks = new DepthFirstGraphSort(m_workingFile, scheduler::isTask).sort();
      List<Task> tasks = scheduler.getSortedTasks();

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
//      boolean startFail = !compareDates(baseline, working, TaskField.START);
//      boolean finishFail = !compareDates(baseline, working, TaskField.FINISH);

      System.out.println((working.getActivityID() == null ? "" : working.getActivityID()+ " ") + working);
      System.out.println("Early Start: " + baseline.getEarlyStart() + " " + working.getEarlyStart() + (earlyStartFail ? " FAIL" : ""));
      System.out.println("Early Finish: " + baseline.getEarlyFinish() + " " + working.getEarlyFinish() + (earlyFinishFail ? " FAIL" : ""));
//      System.out.println("Start: " + baseline.getStart() + " " + working.getStart() + (startFail ? " FAIL" : ""));
//      System.out.println("Finish: " + baseline.getFinish() + " " + working.getFinish() + (finishFail ? " FAIL" : ""));
      System.out.println();
   }

   private void analyseBackwardError(Task working)
   {
      Task baseline = m_baselineFile.getTaskByUniqueID(working.getUniqueID());
      boolean lateStartFail = !compareDates(baseline, working, TaskField.LATE_START);
      boolean lateFinishFail = !compareDates(baseline, working, TaskField.LATE_FINISH);

      System.out.println((working.getActivityID() == null ? "" : working.getActivityID()+ " ") + working);
      System.out.println("Late Start: " + baseline.getLateStart() + " " + working.getLateStart() + (lateStartFail ? " FAIL" : ""));
      System.out.println("Late Finish: " + baseline.getLateFinish() + " " + working.getLateFinish() + (lateFinishFail ? " FAIL" : ""));
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

   private static final Set<String> UNREADABLE_FILES = new HashSet<>();
   static
   {
      // Microsoft Project can't read
      UNREADABLE_FILES.add("easy-centrifuge.mpp");
      UNREADABLE_FILES.add("farfetched-hairshirt.mpp");
   }

   private static final Set<String> USE_SCHEDULED_COPY = new HashSet<>();
   static
   {
      // Aligns with MPXJ when scheduled
      USE_SCHEDULED_COPY.add("diverse-parade.mpp");
      USE_SCHEDULED_COPY.add("southwest-guidance.mpp");
      USE_SCHEDULED_COPY.add("statistical-chin.mpp");
      USE_SCHEDULED_COPY.add("bullish-halfback.mpp");
      USE_SCHEDULED_COPY.add("gradient-claret.mpp");
      USE_SCHEDULED_COPY.add("sandy-wizard.mpp");
      USE_SCHEDULED_COPY.add("copper-yeast.mpp");
      USE_SCHEDULED_COPY.add("capable-banana.mpp");
      USE_SCHEDULED_COPY.add("defensive-monday.mpp");
      USE_SCHEDULED_COPY.add("indelible-reward.mpp");
      USE_SCHEDULED_COPY.add("unmanageable-acquiesence.mpp");
      USE_SCHEDULED_COPY.add("idealistic-motorist.mpp");
      USE_SCHEDULED_COPY.add("literary-chouise.mpp");
      USE_SCHEDULED_COPY.add("unskilled-stint.mpp");
      USE_SCHEDULED_COPY.add("uncooperative-puzzle.mpp");
      USE_SCHEDULED_COPY.add("amusing-disharmony.mpp");
      USE_SCHEDULED_COPY.add("angry-upsurge.mpp");
      USE_SCHEDULED_COPY.add("demonstrable-adjective.mpp");
      USE_SCHEDULED_COPY.add("ample-objection.mpp");
      USE_SCHEDULED_COPY.add("hindmost-setting.mpp");
      USE_SCHEDULED_COPY.add("apparent-canyon.mpp");
      USE_SCHEDULED_COPY.add("blissful-schism.mpp");
      USE_SCHEDULED_COPY.add("olympic-layout.mpp");
      USE_SCHEDULED_COPY.add("organizational-engineering.mpp");
      USE_SCHEDULED_COPY.add("steep-leave.mpp");
      USE_SCHEDULED_COPY.add("snippy-mortgage.mpp");
      USE_SCHEDULED_COPY.add("filipino-corduroy.mpp");
      USE_SCHEDULED_COPY.add("bitterest-oscillation.mpp");
      USE_SCHEDULED_COPY.add("ill-toilet.mpp");
      USE_SCHEDULED_COPY.add("strangest-mulch.mpp");
      USE_SCHEDULED_COPY.add("aberrant-acquiesence.mpp");
      USE_SCHEDULED_COPY.add("marital-peace.mpp");
      USE_SCHEDULED_COPY.add("compositional-information.mpp");
      USE_SCHEDULED_COPY.add("stilted-vaulting.mpp");
      USE_SCHEDULED_COPY.add("unerring-cosmopolitan.mpp");
      USE_SCHEDULED_COPY.add("awry-requisition.mpp");
      USE_SCHEDULED_COPY.add("timid-tribune.mpp");
      USE_SCHEDULED_COPY.add("seismic-piston.mpp");
      USE_SCHEDULED_COPY.add("circumpolar-test.mpp");
      USE_SCHEDULED_COPY.add("symmetrical-dynamite.mpp");
      USE_SCHEDULED_COPY.add("frantic-vestibule.mpp");
      USE_SCHEDULED_COPY.add("equitable-capability.mpp");
      USE_SCHEDULED_COPY.add("feudal-walk.mpp");
      USE_SCHEDULED_COPY.add("super-smokescreen.mpp");
   }

   private static final Set<String> EXCLUDED_FILES = new HashSet<>();
   static
   {
      // Misc MPP files
      EXCLUDED_FILES.add("photographic-magic.mpp"); // External tasks used but not visible in MSP
      EXCLUDED_FILES.add("bizarre-doomsday.mpp"); // Manually scheduled task without any explicitly supplied dates
      EXCLUDED_FILES.add("serene-birthright.mpp"); // TODO: oddity handling one late finish constraint versus end of working time
      EXCLUDED_FILES.add("microsomal-finisher.mpp"); // TODO: late finish, project end determined by constrained task - use unconstrained early finish as project end?
      EXCLUDED_FILES.add("circulatory-collapse.mpp"); // TODO: needs calculation at assignment level?
      EXCLUDED_FILES.add("unerring-gravitation.mpp"); // MPP12 external project not recognised correctly
      EXCLUDED_FILES.add("bullish-halfback-scheduled.mpp"); // In progress task late finish calculation from a completed successor
      EXCLUDED_FILES.add("gallant-trolley.mpp"); // FS relation doesn't make sense
      EXCLUDED_FILES.add("bodily-hoof.mpp"); // Incomplete task and remaining duration don't make sense? Maybe resource calendar related?
      EXCLUDED_FILES.add("idealistic-motorist-scheduled.mpp"); // Manually schedules tasks, LF to LS oddity, plus resources with no resource calendars
      EXCLUDED_FILES.add("urban-dig.mpp"); // Task marked as milestone with 0d duration shown in Project, but is scheduled with a 10d duration
      EXCLUDED_FILES.add("infidel-dashboard.mpp"); // Manually scheduled task LS and LF incorrect
      EXCLUDED_FILES.add("scarlet-throughput.mpp"); // LF oddity
      EXCLUDED_FILES.add("topical-mamma.mpp"); // ES, EF != Start, Finish oddity
      EXCLUDED_FILES.add("lonesome-potentiometer.mpp"); // Summary task relation backward pass issue
      EXCLUDED_FILES.add("hazy-viscometer.mpp"); // Summary task relation backward pass issue - lag throws off calculation?
      EXCLUDED_FILES.add("bitterest-oscillation-scheduled.mpp"); // Summary task relation backward pass issue
      EXCLUDED_FILES.add("aberrant-acquiesence-scheduled.mpp"); // MS Project reports scheduling conflict
      EXCLUDED_FILES.add("lyrical-highlight.mpp"); // MS Project reports scheduling conflict
      EXCLUDED_FILES.add("suspect-catching.mpp"); // Unclear EF difference after scheduling
      EXCLUDED_FILES.add("tintable-casebook.mpp"); // ALAP vs ES, EF rollup
      EXCLUDED_FILES.add("outsized-hive.mpp"); // EF at next work start versus elapsed weeks duration - inaccurate ES
      EXCLUDED_FILES.add("shrewdest-slate.mpp"); // ES with no predecessor in summary task
      EXCLUDED_FILES.add("unmeshed-lab.mpp"); // Manually scheduled task with no dates - not clear how MS Project is selecting ES, EF
      EXCLUDED_FILES.add("extraterrestrial-apologetic.mpp"); // Summary task related EF issue?
      EXCLUDED_FILES.add("ordinary-sect.mpp"); // Duration calculation slightly out for EF
      EXCLUDED_FILES.add("uneconomic-onion.mpp"); // MS Project reports scheduling conflict
      EXCLUDED_FILES.add("doric-understructure.mpp"); // LS, LF issue summary task successor
      EXCLUDED_FILES.add("talkative-gilt.mpp"); // EF date calculation oddity
      EXCLUDED_FILES.add("shylockian-subroutine.mpp"); // Don't understand EF calculation
      EXCLUDED_FILES.add("responsive-extrapolation.mpp"); // LF calculation issue
      EXCLUDED_FILES.add("bulk-sluicehouse-scheduled.mpp"); // SF relations issue?
      EXCLUDED_FILES.add("striking-set.mpp"); // Summary task to ES calc issue
      EXCLUDED_FILES.add("bluff-shoelace.mpp"); // Summary task logic issue
      EXCLUDED_FILES.add("thicker-recital.mpp"); // LF not project end when no successors
      EXCLUDED_FILES.add("hegelian-sensing.mpp"); // LF not project end when no successors
      EXCLUDED_FILES.add("greater-furlough.mpp"); // Calendar issue: EF from constraint date but -1 hour
      EXCLUDED_FILES.add("ill-toilet-scheduled.mpp"); // EF doesn't folow from successor as expected
      EXCLUDED_FILES.add("further-slate.mpp"); // Resource overallocation causing schedule issues?
      EXCLUDED_FILES.add("symphonic-turkey.mpp"); // Resource overallocation causing schedule issues?

      // Predecessor missing when file read
      EXCLUDED_FILES.add("onrushing-stratification.mpp"); // has multiple problems even when save-as and rescheduled
      EXCLUDED_FILES.add("bulk-sluicehouse.mpp");
      EXCLUDED_FILES.add("woeful-drizzle.mpp"); // save as fixes

      // Phantom successor when file read
      EXCLUDED_FILES.add("worthy-conspiracy.mpp");

      // Manually scheduled task - assignment work doesn't calculate correctly
      EXCLUDED_FILES.add("semipublic-tweed.mpp");

      // ALAP
      EXCLUDED_FILES.add("handsome-mockery.mpp");
      EXCLUDED_FILES.add("false-suntan.mpp"); // ALAP ES to Start issue

      // Scheduled from end
      EXCLUDED_FILES.add("dietetic-phrasing.mpp");

      // Manually contoured task
      EXCLUDED_FILES.add("undeveloped-slice.mpp");
      EXCLUDED_FILES.add("nice-machinery.mpp");
      EXCLUDED_FILES.add("adjacent-documentation.mpp");
      EXCLUDED_FILES.add("surrounding-president.mpp");
      EXCLUDED_FILES.add("persistent-shareholder.mpp");
      EXCLUDED_FILES.add("ultimate-arc.mpp");
      EXCLUDED_FILES.add("moderate-facility.mpp");
      EXCLUDED_FILES.add("disruptive-boon.mpp");
      EXCLUDED_FILES.add("halfhearted-agreement.mpp");
      EXCLUDED_FILES.add("cortical-multitude.mpp");
      EXCLUDED_FILES.add("angry-prospect.mpp");
      EXCLUDED_FILES.add("rash-age.mpp");

      // Calculated correctly, but incorrect late dates read from MPP by MPXJ
      EXCLUDED_FILES.add("scatterbrained-tambourine.mpp");
      EXCLUDED_FILES.add("valid-wartime.mpp");
      EXCLUDED_FILES.add("harpy-gully.mpp");

      // MS Project 2010 save and loads different LS, LF dates than shown when calculated, MPXJ aligns with calculated dates
      EXCLUDED_FILES.add("depending-whale.mpp");
      EXCLUDED_FILES.add("ancient-boom.mpp");
      EXCLUDED_FILES.add("automotive-contour.mpp");

      // ES, EF == LS, LF, why?
      EXCLUDED_FILES.add("dread-hydrochemistry.mpp");
      EXCLUDED_FILES.add("brash-asceticism.mpp");
      EXCLUDED_FILES.add("standard-freedom.mpp");

      // Constraint on summary task
      EXCLUDED_FILES.add("false-rustler.mpp");
      EXCLUDED_FILES.add("intact-sentinel.mpp");
      EXCLUDED_FILES.add("woeful-drizzle-scheduled.mpp");

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
      EXCLUDED_FILES.add("irresolvable-booth.mpp");
      EXCLUDED_FILES.add("willful-welter.mpp");
      EXCLUDED_FILES.add("indeterminate-complication.mpp");
      EXCLUDED_FILES.add("deadliest-sonar.mpp");
      EXCLUDED_FILES.add("eternal-column.mpp");
      EXCLUDED_FILES.add("retail-quenching.mpp");
      EXCLUDED_FILES.add("coherent-intellectuality.mpp");
      EXCLUDED_FILES.add("perfect-curb.mpp");
      EXCLUDED_FILES.add("imminent-paymaster.mpp");
      EXCLUDED_FILES.add("moving-sledding.mpp");
      EXCLUDED_FILES.add("adequate-function.mpp");
      EXCLUDED_FILES.add("ceramic-rink.mpp");
      EXCLUDED_FILES.add("formative-cabinet.mpp");
      EXCLUDED_FILES.add("serious-passion.mpp");
      EXCLUDED_FILES.add("nervous-banker.mpp");
      EXCLUDED_FILES.add("wellknown-ginger.mpp");
      EXCLUDED_FILES.add("unpremeditated-payoff.mpp");
      EXCLUDED_FILES.add("amusing-disharmony-scheduled.mpp");
      EXCLUDED_FILES.add("untraditional-highball.mpp");
      EXCLUDED_FILES.add("northerly-health.mpp");
      EXCLUDED_FILES.add("supersonic-witch.mpp");
      EXCLUDED_FILES.add("madding-warfare.mpp");
      EXCLUDED_FILES.add("idle-niche.mpp");
      EXCLUDED_FILES.add("emaciated-subjectivist.mpp");
      EXCLUDED_FILES.add("microbial-inkling.mpp");
      EXCLUDED_FILES.add("undamaged-passing.mpp");
      EXCLUDED_FILES.add("tiniest-solenoid.mpp");
      EXCLUDED_FILES.add("bashful-slumber.mpp");
      EXCLUDED_FILES.add("constructive-facade.mpp");
      EXCLUDED_FILES.add("mediumistic-handclasp.mpp");
      EXCLUDED_FILES.add("bibliographical-veronica.mpp");
      EXCLUDED_FILES.add("mechanistic-brinkmanship.mpp");
      EXCLUDED_FILES.add("abysmal-grandma.mpp");
      EXCLUDED_FILES.add("ashamed-annoyance.mpp");
      EXCLUDED_FILES.add("cleaner-purveyor.mpp");
      EXCLUDED_FILES.add("odd-robin.mpp");
      EXCLUDED_FILES.add("beady-musket.mpp");
      EXCLUDED_FILES.add("maternal-ecliptic.mpp");
      EXCLUDED_FILES.add("printable-powder.mpp");
      EXCLUDED_FILES.add("sly-taste.mpp");
      EXCLUDED_FILES.add("panicky-competitor.mpp");
      EXCLUDED_FILES.add("proximal-milligram.mpp");
      EXCLUDED_FILES.add("undecorated-hick.mpp");
      EXCLUDED_FILES.add("vaguest-brigade.mpp");
      EXCLUDED_FILES.add("blind-moisture.mpp");
      EXCLUDED_FILES.add("palatable-conceptuality.mpp");
      EXCLUDED_FILES.add("unresponsive-monarch.mpp");
      EXCLUDED_FILES.add("commercial-litterbug.mpp");
      EXCLUDED_FILES.add("copper-yeast-scheduled.mpp");
      EXCLUDED_FILES.add("pulmonary-dove.mpp");
      EXCLUDED_FILES.add("photosensitive-bluebook.mpp");
   }
}
