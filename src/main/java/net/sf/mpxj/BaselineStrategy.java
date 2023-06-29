
package net.sf.mpxj;

public interface BaselineStrategy
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
    * The index argument selects which of the 10 baselines to populate. Passing
    * an index of 0 populates the main baseline.
    *
    * @param project target project
    * @param baseline baseline project
    * @param index baseline to populate (0-10)
    */
   void populateBaseline(ProjectFile project, ProjectFile baseline, int index);
}