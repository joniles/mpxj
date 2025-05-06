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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.mpxj.ProjectCalendar;
import org.mpxj.ProjectFile;
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
         test.process(target);
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
    * Tell the comparator to ignore files which Microsoft Project can't read.
    *
    * @param value set of unreadable files
    */
   public void setUnreadableFiles(Set<String> value)
   {
      m_unreadableFiles = value;
   }

   /**
    * Tell the comparator to ignore files which have had new copied created
    * following "Calculate Project" and "Save As".
    *
    * @param value set of scheduled files
    */
   public void setUseScheduled(Set<String> value)
   {
      m_useScheduled = value;
   }

   /**
    * Tell the comparator to ignore files which MicrosoftScheduler doesn't
    * currently process to match Microsoft Project.
    *
    * @param value set of excluded files
    */
   public void setExcluded(Set<String> value)
   {
      m_excluded = value;
   }

   /**
    * Tell the comparator not to test the WBS in these file.
    *
    * @param value set of excluded files
    */
   public void setNoWbsTest(Set<String> value)
   {
      m_noWbsTest = value;
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
         String name = file.getName().toLowerCase();
         if (m_unreadableFiles.contains(name))
         {
            continue;
         }

         if (m_useScheduled.contains(name))
         {
            continue;
         }

         ++valid;

         if (m_excluded.contains(name))
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

      if (m_debug)
      {
         System.out.println();
         System.out.println("Files: " + fileList.length);
         System.out.println("Skipped: " + skipped);
         System.out.println("Success: " + success);
         System.out.println("Failed: " + failed);
         System.out.println("Success %: " + (success * 100.0 / valid));
      }

      return failed == 0;
   }

   /**
    * Compare an individual file.
    *
    * @param file file to compare
    * @return true if the files compare successfully
    */
   public boolean process(File file) throws Exception
   {
      if (m_debug)
      {
         System.out.print("Processing " + file + " ... ");
      }

      m_forwardErrorCount = 0;
      m_backwardErrorCount = 0;
      boolean analyseWbs = true;

      m_baselineFile = new UniversalProjectReader().read(file);
      m_workingFile = new UniversalProjectReader().read(file);

      PrimaveraScheduler scheduler = new PrimaveraScheduler();

      try
      {
         LocalDateTime start = m_workingFile.getProjectProperties().getPlannedStart();
         if (start == null)
         {
            start = m_workingFile.getProjectProperties().getStartDate();
         }

         scheduler.schedule(m_workingFile, start);
      }

      catch (CpmException ex)
      {
         if (m_debug)
         {
            System.out.println("failed.");
            System.out.println(ex.getMessage());
         }
         return false;
      }

      for (Task baselineTask : m_baselineFile.getTasks())
      {
         Task workingTask = m_workingFile.getTaskByUniqueID(baselineTask.getUniqueID());

         if (workingTask.getSummary() && m_noWbsTest.contains(file.getName().toLowerCase()))
         {
            analyseWbs = false;
            continue;
         }

         compare(baselineTask, workingTask);
      }

      if (m_forwardErrorCount == 0 && m_backwardErrorCount == 0)
      {
         if (m_debug)
         {
            System.out.println("done.");
         }
         return true;
      }

      if (m_debug)
      {
         System.out.println("failed.");
         System.out.println("Project ID: " + m_baselineFile.getProjectProperties().getProjectID());
         System.out.println(m_baselineFile.getProjectProperties().getSchedulingProgressedActivities());
         System.out.println("Forward errors: " + m_forwardErrorCount);
         System.out.println("Backward errors: " + m_backwardErrorCount);
      }

      if (!m_directory && m_debug)
      {
         analyseFailures(analyseWbs);
         System.out.println("DONE");
      }

      return false;
   }

   /**
    * Compare two tasks.
    *
    * @param baseline baseline task
    * @param working scheduled task
    */
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

   /**
    * Compare two dates from a task.
    *
    * @param baseline baseline task
    * @param working scheduled task
    * @param field field containing the dates to compare
    * @return true if the comparison is successful
    */
   private boolean compareDates(Task baseline, Task working, TaskField field)
   {
      LocalDateTime baselineDate = (LocalDateTime) baseline.get(field);
      if (baselineDate == null)
      {
         // We have XER files where some of the attributes we'd expect to be populated are not present. Skip these.
         return true;
      }

      LocalDateTime workingDate = (LocalDateTime) working.get(field);
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
      result = allChildTasks(working).stream().map(Task::getEffectiveCalendar).anyMatch(c -> c.getNextWorkStart(workingDate).isEqual(baselineDate) || c.getNextWorkStart(baselineDate).isEqual(workingDate));
      return result;
   }

   /**
    * Write debug output to show where the two project differ.
    *
    * @param analyseWbs true if the WBS should be compared
    */
   private void analyseFailures(boolean analyseWbs) throws CycleException
   {
      List<Task> activities = new DepthFirstGraphSort(m_workingFile, PrimaveraScheduler::isActivity).sort();
      List<Task> levelOfEffortActivities = new DepthFirstGraphSort(m_workingFile, PrimaveraScheduler::isLevelOfEffortActivity).sort();
      List<Task> wbsSummaryActivities = new DepthFirstGraphSort(m_workingFile, PrimaveraScheduler::isWbsSummary).sort();
      List<Task> wbs = m_workingFile.getTasks().stream().filter(Task::getSummary).collect(Collectors.toList());

      // Sort so we can see errors at the bottom first, as these are rolled up.
      Collections.reverse(wbs);

      if (m_forwardErrorCount != 0)
      {
         activities.forEach(this::analyseForwardError);
         levelOfEffortActivities.forEach(this::analyseForwardError);
         wbsSummaryActivities.forEach(this::analyseForwardError);

         if (analyseWbs)
         {
            wbs.forEach(this::analyseForwardError);
         }
      }

      if (m_backwardErrorCount != 0)
      {
         Collections.reverse(activities);
         Collections.reverse(levelOfEffortActivities);
         activities.forEach(this::analyseBackwardError);
         levelOfEffortActivities.forEach(this::analyseBackwardError);
         wbsSummaryActivities.forEach(this::analyseBackwardError);

         if (analyseWbs)
         {
            wbs.forEach(this::analyseBackwardError);
         }
      }
   }

   /**
    * Write debug information for a forward pass error.
    *
    * @param working scheduled task
    */
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

      System.out.println((working.getActivityID() == null ? "" : working.getActivityID() + " ") + working);
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

   /**
    * Write debug information for a backward pass error.
    *
    * @param working scheduled task
    */
   private void analyseBackwardError(Task working)
   {
      Task baseline = m_baselineFile.getTaskByUniqueID(working.getUniqueID());
      boolean lateStartFail = !compareDates(baseline, working, TaskField.LATE_START);
      boolean lateFinishFail = !compareDates(baseline, working, TaskField.LATE_FINISH);
      boolean remainingLateStartFail = !compareDates(baseline, working, TaskField.REMAINING_LATE_START);
      boolean remainingLateFinishFail = !compareDates(baseline, working, TaskField.REMAINING_LATE_FINISH);

      System.out.println((working.getActivityID() == null ? "" : working.getActivityID() + " ") + working);
      System.out.println("Late Start: " + baseline.getLateStart() + " " + working.getLateStart() + (lateStartFail ? " FAIL" : ""));
      System.out.println("Late Finish: " + baseline.getLateFinish() + " " + working.getLateFinish() + (lateFinishFail ? " FAIL" : ""));
      System.out.println("Remaining Late Start: " + baseline.getRemainingLateStart() + " " + working.getRemainingLateStart() + (remainingLateStartFail ? " FAIL" : ""));
      System.out.println("Remaining Late Finish: " + baseline.getRemainingLateFinish() + " " + working.getRemainingLateFinish() + (remainingLateFinishFail ? " FAIL" : ""));
      System.out.println();
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

   private boolean m_debug;
   private boolean m_directory;
   private ProjectFile m_baselineFile;
   private ProjectFile m_workingFile;
   private int m_forwardErrorCount;
   private int m_backwardErrorCount;
   private Set<String> m_unreadableFiles = Collections.emptySet();
   private Set<String> m_useScheduled = Collections.emptySet();
   private Set<String> m_excluded = Collections.emptySet();
   private Set<String> m_noWbsTest = Collections.emptySet();
}
