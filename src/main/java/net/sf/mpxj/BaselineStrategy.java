
package net.sf.mpxj;

import java.util.function.Function;

interface BaselineStrategy
{

   /**
    * Clear the requested baseline for the supplied project.
    *
    * @param project target project
    * @param index baseline to populate (0-10)
    */
   void clearBaseline(ProjectFile project, int index);

   /**
    * Use the supplied baseline project to set the baselineN cost, duration, finish,
    * fixed cost accrual, fixed cost, start and work attributes for the tasks
    * in the supplied project.
    *
    * The supplied keyFunction is used to generate the key
    * used to connect tasks from the current and baseline schedules. This key should
    * be unique for each task in the schedule. Instances where the key is not unique
    * will result in an incorrect baseline being applied to a task in the
    * current schedule.
    *
    * The index argument selects which of the 10 baselines to populate. Passing
    * an index of 0 populates the main baseline.
    *
    * @param project target project
    * @param baseline baseline project
    * @param index baseline to populate (0-10)
    * @param keyFunction generate a key used to match tasks
    */
   void populateBaseline(ProjectFile project, ProjectFile baseline, int index, Function<Task, Object> keyFunction);

}