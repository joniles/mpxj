/*
 * file:       PrimaveraSchedulerComparator.java
 * author:     Jon Iles
 * date:       2025-04-02
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

package org.mpxj.cpm;

import java.io.File;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.mpxj.AssignmentField;
import org.mpxj.Duration;
import org.mpxj.ProjectCalendar;
import org.mpxj.ProjectFile;
import org.mpxj.ResourceAssignment;
import org.mpxj.Task;
import org.mpxj.TaskField;
import org.mpxj.reader.UniversalProjectReader;

/**
 * Utility class allowing the contents of an existing project to be compared
 * with the output from the PrimaveraScheduler.
 */
public class PrimaveraSchedulerComparator
{
   /**
    * Main entry point when run as a command line tool.
    *
    * @param argv command line arguments
    */
   public static void main(String[] argv) throws Exception
   {
      if (argv.length != 1)
      {
         System.out.println("Usage: PrimaveraSchedulerComparator <file or folder>");
         return;
      }

      File target = new File(argv[0]);
      PrimaveraSchedulerComparator test = new PrimaveraSchedulerComparator();
      test.setDebug(true);

      if (target.isDirectory())
      {
         test.process(target, ".xer");
      }
      else
      {
         test.process(target, DEFAULT_EXCLUDED);
      }
   }

   /**
    * Enable or disable debug output.
    *
    * @param value pass true to enable debug output
    */
   public void setDebug(boolean value)
   {
      m_debug = value;
   }

   /**
    * Provide a PrintStream instance for output.
    *
    * @param printStream PrintStream instance
    */
   public void setPrintStream(PrintStream printStream)
   {
      m_printStream = printStream;
   }

   /**
    * Tell the comparator to ignore files which Microsoft Project can't read.
    *
    * @param value set of unreadable files
    */
   public void setUnreadableFiles(Set<String> value)
   {
      m_exclusions.setUnreadableFiles(value);
   }

   /**
    * Tell the comparator to ignore files which have had new copied created
    * following "Calculate Project" and "Save As".
    *
    * @param value set of scheduled files
    */
   public void setUseScheduled(Set<String> value)
   {
      m_exclusions.setUseScheduled(value);
   }

   /**
    * Tell the comparator to ignore files which MicrosoftScheduler doesn't
    * currently process to match Microsoft Project.
    *
    * @param value set of excluded files
    */
   public void setExcluded(Set<String> value)
   {
      m_exclusions.setExcluded(value);
   }

   /**
    * Tell the comparator not to test the WBS in these files.
    *
    * @param value set of excluded files
    */
   public void setNoWbsTest(Set<String> value)
   {
      m_exclusions.setNoWbsTest(value);
   }

   /**
    * Tell the comparator not to test resource assignments in these files.
    *
    * @param value set of excluded files
    */
   public void setNoResourceAssignmentTest(Set<String> value)
   {
      m_exclusions.setNoResourceAssignmentTest(value);
   }

   /**
    * Tell the comparator to ignore files which PrimaveraScheduler doesn't
    * currently process to match P6.
    *
    * @param value set of excluded files
    */
   public void setNoFloatTest(Set<String> value)
   {
      m_exclusions.setNoFloatTest(value);
   }

   /**
    * Tell the comparator to ignore files which PrimaveraScheduler doesn't
    * currently process to match P6.
    *
    * @param value set of excluded files
    */
   public void setNoLongestPathTest(Set<String> value)
   {
      m_exclusions.setNoLongestPathTest(value);
   }

   /**
    * Compare all the files in a directory with a matching suffix.
    *
    * @param directory directory
    * @param suffix file suffix
    * @return true if all files compare successfully
    */
   public boolean process(File directory, String suffix) throws Exception
   {
      File[] fileList = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(suffix));
      if (fileList == null)
      {
         throw new IllegalArgumentException();
      }

      m_directory = true;
      int failed = 0;
      int skipped = 0;
      int valid = 0;
      int success = 0;

      for (File file : fileList)
      {
         Excluded excluded = m_exclusions.getExcluded(file.getName().toLowerCase());

         if (excluded.unreadable())
         {
            continue;
         }

         if (excluded.useScheduled())
         {
            continue;
         }

         ++valid;

         if (excluded.excluded())
         {
            ++skipped;
            continue;
         }

         if (process(file, excluded))
         {
            ++success;
         }
         else
         {
            ++failed;
         }
      }

      if (m_debug)
      {
         println();
         println("Files: " + fileList.length);
         println("Skipped: " + skipped);
         println("Success: " + success);
         println("Failed: " + failed);
         println("Success %: " + (success * 100.0 / valid));
      }

      return failed == 0;
   }

   /**
    * Compare a single file.
    *
    * @param file file to compare
    * @return true if compared successfully
    */
   private  boolean process(File file, Excluded excluded) throws Exception
   {
      if (m_debug)
      {
         print("Processing " + file + " ... ");
      }

      ProjectFile baselineFile = new UniversalProjectReader().read(file);
      ProjectFile workingFile = new UniversalProjectReader().read(file);

      PrimaveraScheduler scheduler = new PrimaveraScheduler();

      try
      {
         LocalDateTime start = workingFile.getProjectProperties().getPlannedStart();
         if (start == null)
         {
            start = workingFile.getProjectProperties().getStartDate();
         }

         scheduler.schedule(workingFile, start);
      }

      catch (CpmException ex)
      {
         if (m_debug)
         {
            println("failed.");
            println(ex.getMessage());
         }
         return false;
      }

      return process(baselineFile, workingFile, excluded);
   }

   /**
    * Compare two ProjectFile instances.
    *
    * @param baselineFile baseline project
    * @param workingFile working project
    * @param excluded analysis exclusions
    * @return true if compared successfully
    */
   public boolean process(ProjectFile baselineFile, ProjectFile workingFile, Excluded excluded) throws Exception
   {
      m_forwardEquivalentDateCount = 0;
      m_backwardEquivalentDateCount = 0;
      m_assignmentEquivalentDateCount = 0;
      m_forwardErrorCount = 0;
      m_backwardErrorCount = 0;
      m_assignmentErrorCount = 0;

      for (Task baselineTask : baselineFile.getTasks())
      {
         Task workingTask = workingFile.getTaskByUniqueID(baselineTask.getUniqueID());
         if (workingTask.getSummary() && excluded.noWbsTest())
         {
            continue;
         }

         compare(baselineTask, workingTask, excluded);

         if (!excluded.noResourceAssignmentTest())
         {
            for (ResourceAssignment baselineAssignment : baselineTask.getResourceAssignments())
            {
               ResourceAssignment workingAssignment = workingFile.getResourceAssignments().getByUniqueID(baselineAssignment.getUniqueID());
               compare(baselineAssignment, workingAssignment);
            }
         }
      }

      //      if (m_forwardErrorCount == 0)
      //      {
      //         m_forwardErrorCount = -1;
      //      }

//      if (m_backwardErrorCount == 0)
//      {
//         m_backwardErrorCount = -1;
//      }


      //      if (m_assignmentErrorCount == 0)
      //      {
      //         m_assignmentErrorCount = -1;
      //      }

      if (m_forwardErrorCount == 0 && m_backwardErrorCount == 0 && m_assignmentErrorCount == 0)
      {
         if (m_debug)
         {
            println("done. " + getEquivalentDateCount());
         }
         return true;
      }

      if (m_debug)
      {
         println("failed. " + getEquivalentDateCount());
         println("Project ID: " + baselineFile.getProjectProperties().getProjectID());
         println("Scheduling Progressed Activities: " + baselineFile.getProjectProperties().getSchedulingProgressedActivities());
         println("Forward errors: " + m_forwardErrorCount);
         println("Backward errors: " + m_backwardErrorCount);
         println("Assignment errors: " + m_assignmentErrorCount);
      }

      if (!m_directory && m_debug)
      {
         analyseFailures(baselineFile, workingFile, excluded);
         println("DONE");
      }

      return false;
   }

   /**
    * Report the number of equivalent dates, broken down by type.
    *
    * @return number of equivalent dates report
    */
   private String getEquivalentDateCount()
   {
      String forwardEquivalentDateCount = m_forwardEquivalentDateCount == 0 ? "" : m_forwardEquivalentDateCount + " forward equivalent dates";
      String backwardEquivalentDateCount = m_backwardEquivalentDateCount == 0 ? "" : m_backwardEquivalentDateCount + " backward equivalent dates";
      String assignmentEquivalentDateCount = m_assignmentEquivalentDateCount == 0 ? "" : m_assignmentEquivalentDateCount + " assignment equivalent dates";
      String equivalentDateCount;

      if (forwardEquivalentDateCount.isEmpty() && backwardEquivalentDateCount.isEmpty() && assignmentEquivalentDateCount.isEmpty())
      {
         return "";
      }

      String result = Stream.of(forwardEquivalentDateCount, backwardEquivalentDateCount, assignmentEquivalentDateCount).filter(s -> !s.isEmpty()).collect(Collectors.joining(" "));
      return "(" + result + ")";
   }

   /**
    * Compare two tasks.
    *
    * @param baseline baseline task
    * @param working scheduled task
    * @param excluded analysis exclusions
    */
   private void compare(Task baseline, Task working, Excluded excluded)
   {
      List<DateEquality> forwardDateComparisons = Arrays.asList(
         compareDates(baseline, working, TaskField.EARLY_START),
         compareDates(baseline, working, TaskField.EARLY_FINISH),
         compareDates(baseline, working, TaskField.START),
         compareDates(baseline, working, TaskField.FINISH),
         compareDates(baseline, working, TaskField.ACTUAL_START),
         compareDates(baseline, working, TaskField.ACTUAL_FINISH),
         compareDates(baseline, working, TaskField.REMAINING_EARLY_START),
         compareDates(baseline, working, TaskField.REMAINING_EARLY_FINISH),
         compareDates(baseline, working, TaskField.PLANNED_START),
         compareDates(baseline, working, TaskField.PLANNED_FINISH)
      );

      boolean forwardDatesFailed = forwardDateComparisons.stream().anyMatch(d -> d == DateEquality.MISMATCH);
      boolean freeFloatFailed = !excluded.noFloatTest() && !compareDurations(baseline, working, TaskField.FREE_SLACK);
      boolean totalFloatFailed = !excluded.noFloatTest() && !compareDurations(baseline, working, TaskField.TOTAL_SLACK);
      boolean longestPathFailed = !excluded.noLongestPathTest() && baseline.getLongestPath() != working.getLongestPath();
      boolean plannedDurationFailed = !baseline.getSummary() && !compareDurations(baseline, working, TaskField.PLANNED_DURATION);
      boolean actualDurationFailed = !baseline.getSummary() && !compareDurations(baseline, working, TaskField.ACTUAL_DURATION);
      boolean remainingDurationFailed = !baseline.getSummary() && !compareDurations(baseline, working, TaskField.REMAINING_DURATION);
      boolean atCompletionDurationFailed = !baseline.getSummary() && !compareDurations(baseline, working, TaskField.DURATION);
      boolean durationPercentCompleteFailed = !baseline.getSummary() && !compareNumbers(baseline, working, TaskField.PERCENT_COMPLETE);

      if (forwardDatesFailed || freeFloatFailed || totalFloatFailed || longestPathFailed || plannedDurationFailed || actualDurationFailed || remainingDurationFailed || atCompletionDurationFailed || durationPercentCompleteFailed)
      {
         ++m_forwardErrorCount;
      }

      List<DateEquality> backwardDateComparisons = Arrays.asList(
         compareDates(baseline, working, TaskField.LATE_START),
         compareDates(baseline, working, TaskField.LATE_FINISH),
         compareDates(baseline, working, TaskField.REMAINING_LATE_START),
         compareDates(baseline, working, TaskField.REMAINING_LATE_FINISH)
      );

      boolean backwardDatesFailed = backwardDateComparisons.stream().anyMatch(d -> d == DateEquality.MISMATCH);
      if (backwardDatesFailed)
      {
         ++m_backwardErrorCount;
      }

      m_forwardEquivalentDateCount += (int) forwardDateComparisons.stream()
         .filter(d -> d == DateEquality.EQUIVALENT)
         .count();

      m_backwardEquivalentDateCount += (int) backwardDateComparisons.stream()
         .filter(d -> d == DateEquality.EQUIVALENT)
         .count();

   }

   /**
    * Compare two dates from a task.
    *
    * @param baseline baseline task
    * @param working scheduled task
    * @param field field containing the dates to compare
    * @return DateEquality instance representing comparison result
    */
   private DateEquality compareDates(Task baseline, Task working, TaskField field)
   {
      LocalDateTime baselineDate = (LocalDateTime) baseline.get(field);
      if (baselineDate == null)
      {
         // We have XER files where some of the attributes we'd expect to be populated are not present. Skip these.
         return DateEquality.MATCH;
      }

      LocalDateTime workingDate = (LocalDateTime) working.get(field);
      if (workingDate == null)
      {
         return DateEquality.MISMATCH;
      }

      if (baselineDate.isEqual(workingDate))
      {
         return DateEquality.MATCH;
      }

      ProjectCalendar calendar = baseline.getEffectiveCalendar();
      //boolean result = calendar.getNextWorkStart(workingDate).isEqual(baselineDate) || calendar.getNextWorkStart(baselineDate).isEqual(workingDate);
      boolean result = calendar.getNextWorkStart(workingDate).isEqual(calendar.getNextWorkStart(baselineDate));
      if (result)
      {
         return DateEquality.EQUIVALENT;
      }

      if (!working.getSummary())
      {
         return DateEquality.MISMATCH;
      }

      // At this point we have failed to compare, but we have a WBS entry.
      // This doesn't have its own calendar so we need to look at the child calendars.
      // Yes, it's hacky. The real solution is to understand the logic P6 is
      // applying when it chooses between end of day or start of next day.
      //result = working.getChildTasks().stream().map(t -> t.getEffectiveCalendar()).anyMatch(c -> c.getNextWorkStart(workingDate).isEqual(baselineDate) || c.getNextWorkStart(baselineDate).isEqual(workingDate));
      result = allChildTasks(working).stream().map(Task::getEffectiveCalendar).anyMatch(c -> c.getNextWorkStart(workingDate).isEqual(baselineDate) || c.getNextWorkStart(baselineDate).isEqual(workingDate));
      return result ? DateEquality.EQUIVALENT : DateEquality.MISMATCH;
   }

   /**
    * Compare two duration fields.
    *
    * @param baseline baseline task
    * @param working working task
    * @param field field to compare
    * @return true if the durations match
    */
   private boolean compareDurations(Task baseline, Task working, TaskField field)
   {
      Duration baselineDuration = (Duration) baseline.get(field);
      if (baselineDuration == null)
      {
         return true;
      }

      Duration workingDuration = (Duration) working.get(field);
      if (workingDuration == null)
      {
         return true;
      }

      // Truncate to two decimal places for comparison.
      // Avoids issues with small rounding differences.
      long baselineDurationValue = (long) (baselineDuration.getDuration() * 100.0);
      long workingDurationValue = (long) (workingDuration.getDuration() * 100.0);

      return baselineDuration.getUnits() == workingDuration.getUnits() && baselineDurationValue == workingDurationValue;
   }

   /**
    * Compare numeric values.
    *
    * @param baseline baseline task
    * @param working working task
    * @param field field to compare
    * @return true if the numeric values match
    */
   private boolean compareNumbers(Task baseline, Task working, TaskField field)
   {
      Number baselineObject = (Number) baseline.get(field);
      if (baselineObject == null)
      {
         return true;
      }

      Number workingObject = (Number) working.get(field);
      if (workingObject == null)
      {
         return true;
      }

      // Truncate to two decimal places for comparison.
      // Avoids issues with small rounding differences.
      long baselineValue = (long) (baselineObject.doubleValue() * 100.0);
      long workingValue = (long) (workingObject.doubleValue() * 100.0);

      return baselineValue == workingValue;
   }

   /**
    * Write debug output to show where the two project differ.
    *
    * @param baselineFile baseline for comparison
    * @param workingFile working file for comparison
    * @param excluded analysis exclusions
    */
   private void analyseFailures(ProjectFile baselineFile, ProjectFile workingFile, Excluded excluded) throws CycleException
   {
      List<Task> activities = new DepthFirstGraphSort(workingFile, PrimaveraScheduler::isActivity).sort();
      List<Task> levelOfEffortActivities = new DepthFirstGraphSort(workingFile, PrimaveraScheduler::isLevelOfEffortActivity).sort();
      List<Task> wbsSummaryActivities = new DepthFirstGraphSort(workingFile, PrimaveraScheduler::isWbsSummary).sort();
      List<Task> wbs = workingFile.getTasks().stream().filter(Task::getSummary).collect(Collectors.toList());

      // Sort so we can see errors at the bottom first, as these are rolled up.
      Collections.reverse(wbs);

      if (m_forwardErrorCount != 0)
      {
         activities.forEach(a -> analyseForwardError(baselineFile, a));
         levelOfEffortActivities.forEach(a -> analyseForwardError(baselineFile, a));
         wbsSummaryActivities.forEach(a -> analyseForwardError(baselineFile, a));

         if (!excluded.noWbsTest())
         {
            wbs.forEach(a -> analyseForwardError(baselineFile, a));
         }
      }

      if (m_backwardErrorCount != 0)
      {
         Collections.reverse(activities);
         Collections.reverse(levelOfEffortActivities);
         activities.forEach(a -> analyseBackwardError(baselineFile, a));
         levelOfEffortActivities.forEach(a -> analyseBackwardError(baselineFile, a));
         wbsSummaryActivities.forEach(a -> analyseBackwardError(baselineFile, a));

         if (!excluded.noWbsTest())
         {
            wbs.forEach(a -> analyseBackwardError(baselineFile, a));
         }
      }

      if (m_assignmentErrorCount != 0)
      {
         workingFile.getResourceAssignments().forEach(a -> analyseAssignmentError(baselineFile, a));
      }
   }

   /**
    * Write debug information for a forward pass error.
    *
    * @param baselineFile baseline for comparison
    * @param working scheduled task
    */
   private void analyseForwardError(ProjectFile baselineFile, Task working)
   {
      Task baseline = baselineFile.getTaskByUniqueID(working.getUniqueID());
      DateEquality earlyStartFail = compareDates(baseline, working, TaskField.EARLY_START);
      DateEquality earlyFinishFail = compareDates(baseline, working, TaskField.EARLY_FINISH);
      DateEquality startFail = compareDates(baseline, working, TaskField.START);
      DateEquality finishFail = compareDates(baseline, working, TaskField.FINISH);
      DateEquality actualStartFail = compareDates(baseline, working, TaskField.ACTUAL_START);
      DateEquality actualFinishFail = compareDates(baseline, working, TaskField.ACTUAL_FINISH);
      DateEquality remainingEarlyStartFail = compareDates(baseline, working, TaskField.REMAINING_EARLY_START);
      DateEquality remainingEarlyFinishFail = compareDates(baseline, working, TaskField.REMAINING_EARLY_FINISH);
      DateEquality plannedStartFail = compareDates(baseline, working, TaskField.PLANNED_START);
      DateEquality plannedFinishFail = compareDates(baseline, working, TaskField.PLANNED_FINISH);

      boolean freeFloatFailed = !compareDurations(baseline, working, TaskField.FREE_SLACK);
      boolean totalFloatFailed = !compareDurations(baseline, working, TaskField.TOTAL_SLACK);
      boolean longestPathFailed = baseline.getLongestPath() != working.getLongestPath();
      boolean plannedDurationFailed = !compareDurations(baseline, working, TaskField.PLANNED_DURATION);
      boolean actualDurationFailed = !compareDurations(baseline, working, TaskField.ACTUAL_DURATION);
      boolean remainingDurationFailed = !compareDurations(baseline, working, TaskField.REMAINING_DURATION);
      boolean atCompletionDurationFailed = !compareDurations(baseline, working, TaskField.DURATION);
      boolean durationPercentCompleteFailed = !baseline.getSummary() && !compareNumbers(baseline, working, TaskField.PERCENT_COMPLETE);

      println((working.getActivityID() == null ? "" : working.getActivityID() + " ") + working + " " + working.getActivityType());
      println("Early Start: " + baseline.getEarlyStart() + " " + working.getEarlyStart() + earlyStartFail.getStatus());
      println("Early Finish: " + baseline.getEarlyFinish() + " " + working.getEarlyFinish() + earlyFinishFail.getStatus());
      println("Start: " + baseline.getStart() + " " + working.getStart() + startFail.getStatus());
      println("Finish: " + baseline.getFinish() + " " + working.getFinish() + finishFail.getStatus());
      println("Actual Start: " + baseline.getActualStart() + " " + working.getActualStart() + actualStartFail.getStatus());
      println("Actual Finish: " + baseline.getActualFinish() + " " + working.getActualFinish() + actualFinishFail.getStatus());
      println("Remaining Early Start: " + baseline.getRemainingEarlyStart() + " " + working.getRemainingEarlyStart() + remainingEarlyStartFail.getStatus());
      println("Remaining Early Finish: " + baseline.getRemainingEarlyFinish() + " " + working.getRemainingEarlyFinish() + remainingEarlyFinishFail.getStatus());
      println("Planned Start: " + baseline.getPlannedStart() + " " + working.getPlannedStart() + plannedStartFail.getStatus());
      println("Planned Finish: " + baseline.getPlannedFinish() + " " + working.getPlannedFinish() + plannedFinishFail.getStatus());

      println("Free Float: " + baseline.getFreeSlack() + " " + working.getFreeSlack() + (freeFloatFailed ? " FAIL" : ""));
      println("Total Float: " + baseline.getTotalSlack() + " " + working.getTotalSlack() + (totalFloatFailed ? " FAIL" : ""));
      println("Longest Path: " + baseline.getLongestPath() + " " + working.getLongestPath() + (longestPathFailed ? " FAIL" : ""));
      println("Planned Duration: " + baseline.getPlannedDuration() + " " + working.getPlannedDuration() + (plannedDurationFailed ? " FAIL" : ""));
      println("Actual Duration: " + baseline.getActualDuration() + " " + working.getActualDuration() + (actualDurationFailed ? " FAIL" : ""));
      println("Remaining Duration: " + baseline.getRemainingDuration() + " " + working.getRemainingDuration() + (remainingDurationFailed ? " FAIL" : ""));
      println("At Completion Duration: " + baseline.getDuration() + " " + working.getDuration() + (atCompletionDurationFailed ? " FAIL" : ""));
      println("Duration Percent Complete: " + baseline.getPercentageComplete() + " " + working.getPercentageComplete() + (durationPercentCompleteFailed ? " FAIL" : ""));
      println();
   }

   /**
    * Write debug information for a backward pass error.
    *
    * @param baselineFile baseline for comparison
    * @param working scheduled task
    */
   private void analyseBackwardError(ProjectFile baselineFile, Task working)
   {
      Task baseline = baselineFile.getTaskByUniqueID(working.getUniqueID());
      DateEquality lateStartFail = compareDates(baseline, working, TaskField.LATE_START);
      DateEquality lateFinishFail = compareDates(baseline, working, TaskField.LATE_FINISH);
      DateEquality remainingLateStartFail = compareDates(baseline, working, TaskField.REMAINING_LATE_START);
      DateEquality remainingLateFinishFail = compareDates(baseline, working, TaskField.REMAINING_LATE_FINISH);

      println((working.getActivityID() == null ? "" : working.getActivityID() + " ") + working);
      println("Late Start: " + baseline.getLateStart() + " " + working.getLateStart() + lateStartFail.getStatus());
      println("Late Finish: " + baseline.getLateFinish() + " " + working.getLateFinish() + lateFinishFail.getStatus());
      println("Remaining Late Start: " + baseline.getRemainingLateStart() + " " + working.getRemainingLateStart() + remainingLateStartFail.getStatus());
      println("Remaining Late Finish: " + baseline.getRemainingLateFinish() + " " + working.getRemainingLateFinish() + remainingLateFinishFail.getStatus());
      println();
   }

   /**
    * Write debug information for an assignment error.
    *
    * @param baselineFile baseline for comparison
    * @param working scheduled resource assignment
    */
   private void analyseAssignmentError(ProjectFile baselineFile, ResourceAssignment working)
   {
      ResourceAssignment baseline = baselineFile.getResourceAssignments().getByUniqueID(working.getUniqueID());
      DateEquality startFail = compareDates(baseline, working, AssignmentField.START);
      DateEquality finishFail = compareDates(baseline, working, AssignmentField.FINISH);
      DateEquality actualStartFail = compareDates(baseline, working, AssignmentField.ACTUAL_START);
      DateEquality actualFinishFail = compareDates(baseline, working, AssignmentField.ACTUAL_FINISH);
      DateEquality remainingEarlyStartFail = compareDates(baseline, working, AssignmentField.REMAINING_EARLY_START);
      DateEquality remainingEarlyFinishFail = compareDates(baseline, working, AssignmentField.REMAINING_EARLY_FINISH);
      DateEquality remainingLateStartFail = compareDates(baseline, working, AssignmentField.REMAINING_LATE_START);
      DateEquality remainingLateFinishFail = compareDates(baseline, working, AssignmentField.REMAINING_LATE_FINISH);

      println(working.getUniqueID() + " " + working);
      println("Start: " + baseline.getStart() + " " + working.getStart() + startFail.getStatus());
      println("Finish: " + baseline.getFinish() + " " + working.getFinish() + finishFail.getStatus());
      println("Actual Start: " + baseline.getActualStart() + " " + working.getActualStart() + actualStartFail.getStatus());
      println("Actual Finish: " + baseline.getActualFinish() + " " + working.getActualFinish() + actualFinishFail.getStatus());
      println("Remaining Early Start: " + baseline.getRemainingEarlyStart() + " " + working.getRemainingEarlyStart() + remainingEarlyStartFail.getStatus());
      println("Remaining Early Finish: " + baseline.getRemainingEarlyFinish() + " " + working.getRemainingEarlyFinish() + remainingEarlyFinishFail.getStatus());
      println("Remaining Late Start: " + baseline.getRemainingLateStart() + " " + working.getRemainingLateStart() + remainingLateStartFail.getStatus());
      println("Remaining Late Finish: " + baseline.getRemainingLateFinish() + " " + working.getRemainingLateFinish() + remainingLateFinishFail.getStatus());

      println();
   }

   /**
    * Return a list of all child tasks in the hierarchy beneath a WBS entry.
    *
    * @param parent WBS entry
    * @return all child tasks
    */
   private List<Task> allChildTasks(Task parent)
   {
      List<Task> result = new ArrayList<>(parent.getChildTasks());
      for (Task task : parent.getChildTasks())
      {
         result.addAll(allChildTasks(task));
      }
      return result;
   }

   /**
    * Compare two resource assignments.
    *
    * @param baseline baseline resource assignment
    * @param working working resource assignment
    */
   private void compare(ResourceAssignment baseline, ResourceAssignment working)
   {
      List<DateEquality> result = Arrays.asList(
         compareDates(baseline, working, AssignmentField.START),
         compareDates(baseline, working, AssignmentField.FINISH),
         compareDates(baseline, working, AssignmentField.ACTUAL_START),
         compareDates(baseline, working, AssignmentField.ACTUAL_FINISH),
         compareDates(baseline, working, AssignmentField.REMAINING_EARLY_START),
         compareDates(baseline, working, AssignmentField.REMAINING_EARLY_FINISH),
         compareDates(baseline, working, AssignmentField.REMAINING_LATE_START),
         compareDates(baseline, working, AssignmentField.REMAINING_LATE_FINISH)
      );

      if (result.stream().anyMatch(d -> d == DateEquality.MISMATCH))
      {
         ++m_assignmentErrorCount;
      }

      m_assignmentEquivalentDateCount += (int) result.stream().filter(d -> d == DateEquality.EQUIVALENT).count();
   }

   /**
    * Compare two dates from a resource assignment.
    *
    * @param baseline baseline resource assignment
    * @param working scheduled resource assignment
    * @param field field containing the dates to compare
    * @return DateEquality representing comparison result
    */
   private DateEquality compareDates(ResourceAssignment baseline, ResourceAssignment working, AssignmentField field)
   {
      LocalDateTime baselineDate = (LocalDateTime) baseline.get(field);
      if (baselineDate == null)
      {
         // We have XER files where some of the attributes we'd expect to be populated are not present. Skip these.
         return DateEquality.MATCH;
      }

      LocalDateTime workingDate = (LocalDateTime) working.get(field);
      if (workingDate == null)
      {
         return DateEquality.MISMATCH;
      }

      if (baselineDate.isEqual(workingDate))
      {
         return DateEquality.MATCH;
      }

      ProjectCalendar calendar = baseline.getEffectiveCalendar();
      boolean result = calendar.getNextWorkStart(workingDate).isEqual(baselineDate) || calendar.getNextWorkStart(baselineDate).isEqual(workingDate);
      return result ? DateEquality.EQUIVALENT : DateEquality.MISMATCH;
   }

   /**
    * Enumeration representing the result of a date comparison.
    */
   private enum DateEquality
   {
      MATCH(""),
      EQUIVALENT(" EQUIVALENT"),
      MISMATCH(" FAIL");

      DateEquality(String status)
      {
         m_status = status;
      }

      public String getStatus()
      {
         return m_status;
      }

      private final String m_status;
   }

   private void println()
   {
      m_printStream.println();
   }

   private void println(String value)
   {
      m_printStream.println(value);
   }

   private void print(String value)
   {
      m_printStream.print(value);
   }

   private static class Exclusions
   {
      private void configure(Set<String> value, Consumer<Excluded> consumer)
      {
         value.stream().map(k -> m_excluded.computeIfAbsent(k, v -> new Excluded())).forEach(consumer);
      }

      /**
       * Tell the comparator to ignore files which Microsoft Project can't read.
       *
       * @param value set of unreadable files
       */
      public void setUnreadableFiles(Set<String> value)
      {
         configure(value, Excluded::setUnreadable);
      }

      /**
       * Tell the comparator to ignore files which have had new copied created
       * following "Calculate Project" and "Save As".
       *
       * @param value set of scheduled files
       */
      public void setUseScheduled(Set<String> value)
      {
         configure(value, Excluded::setUseScheduled);
      }

      /**
       * Tell the comparator to ignore files which MicrosoftScheduler doesn't
       * currently process to match Microsoft Project.
       *
       * @param value set of excluded files
       */
      public void setExcluded(Set<String> value)
      {
         configure(value, Excluded::setExcluded);
      }

      /**
       * Tell the comparator not to test the WBS in these files.
       *
       * @param value set of excluded files
       */
      public void setNoWbsTest(Set<String> value)
      {
         configure(value, Excluded::setNoWbsTest);
      }

      /**
       * Tell the comparator not to test resource assignments in these files.
       *
       * @param value set of excluded files
       */
      public void setNoResourceAssignmentTest(Set<String> value)
      {
         configure(value, Excluded::setNoResourceAssignmentTest);
      }

      /**
       * Tell the comparator to ignore files which PrimaveraScheduler doesn't
       * currently process to match P6.
       *
       * @param value set of excluded files
       */
      public void setNoFloatTest(Set<String> value)
      {
         configure(value, Excluded::setNoFloatTest);
      }

      /**
       * Tell the comparator to ignore files which PrimaveraScheduler doesn't
       * currently process to match P6.
       *
       * @param value set of excluded files
       */
      public void setNoLongestPathTest(Set<String> value)
      {
         configure(value, Excluded::setNoLongestPathTest);
      }

      public Excluded getExcluded(String name)
      {
         return m_excluded.getOrDefault(name, DEFAULT_EXCLUDED);
      }

      private final Map<String, Excluded> m_excluded = new HashMap<>();
   }

   public static class Excluded
   {
      public boolean unreadable()
      {
         return m_unreadable;
      }

      public void setUnreadable()
      {
         m_unreadable = true;
      }

      public boolean useScheduled()
      {
         return m_useScheduled;
      }

      public void setUseScheduled()
      {
         m_useScheduled = true;
      }

      public boolean excluded()
      {
         return m_excluded;
      }

      public void setExcluded()
      {
         m_excluded = true;
      }

      public boolean noWbsTest()
      {
         return m_noWbsTest;
      }

      public void setNoWbsTest()
      {
         m_noWbsTest = true;
      }

      public boolean noResourceAssignmentTest()
      {
         return m_noResourceAssignmentTest;
      }

      public void setNoResourceAssignmentTest()
      {
         m_noResourceAssignmentTest = true;
      }

      public boolean noFloatTest()
      {
         return m_noFloatTest;
      }

      public void setNoFloatTest()
      {
         m_noFloatTest = true;
      }

      public boolean noLongestPathTest()
      {
         return m_noLongestPathTest;
      }

      public void setNoLongestPathTest()
      {
         m_noLongestPathTest = true;
      }

      private boolean m_unreadable;
      private boolean m_useScheduled;
      private boolean m_excluded;
      private boolean m_noWbsTest;
      private boolean m_noResourceAssignmentTest;
      private boolean m_noFloatTest;
      private boolean m_noLongestPathTest;
   }

   private boolean m_debug;
   private PrintStream m_printStream = System.out;
   private boolean m_directory;
   private int m_forwardEquivalentDateCount;
   private int m_backwardEquivalentDateCount;
   private int m_assignmentEquivalentDateCount;
   private int m_forwardErrorCount;
   private int m_backwardErrorCount;
   private int m_assignmentErrorCount;
   private final Exclusions m_exclusions = new Exclusions();

   public static final Excluded DEFAULT_EXCLUDED = new Excluded();
}
