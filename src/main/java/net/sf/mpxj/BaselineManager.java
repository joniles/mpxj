/*
 * file:       BaselineManager.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2021
 * date:       23/02/2021
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

package net.sf.mpxj;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import net.sf.mpxj.common.TaskFieldLists;

/**
 * Handles setting baseline fields in one project using values read from another project.
 */
class BaselineManager
{
   /**
    * Clear the requested baseline for the supplied project.
    * 
    * @param project target project
    * @param index baseline to populate (0-10)
    */
   public void clearBaseline(ProjectFile project, int index)
   {
      TaskField[] baselineFields = getBaselineFields(index);
      project.getTasks().forEach(t -> populateBaseline(t, null, baselineFields));
   }

   /**
    * Use the supplied baseline project to set the baselineN cost, duration, finish,
    * fixed cost accrual, fixed cost, start and work attributes for the tasks
    * in the supplied project. The supplied TaskField instance is the key used
    * to match tasks in the current project with tasks in the baseline project.
    * The index argument selects which of the 10 baselines to populate. Passing
    * an index of 0 populates the main baseline.
    * 
    * @param project target project
    * @param baseline baseline project
    * @param index baseline to populate (0-10)
    * @param keyFunction generate a key used to match tasks
    */
   public void populateBaseline(ProjectFile project, ProjectFile baseline, int index, Function<Task, Object> keyFunction)
   {
      TaskField[] baselineFields = getBaselineFields(index);
      Map<Object, Task> map = baseline.getTasks().stream().filter(t -> keyFunction.apply(t) != null).collect(Collectors.toMap(t -> keyFunction.apply(t), t -> t));
      project.getTasks().forEach(t -> populateBaseline(t, map.get(keyFunction.apply(t)), baselineFields));
   }

   /**
    * Populates baseline fields in one task with values from another task.
    * 
    * @param task target task
    * @param baseline source task
    * @param baselineFields set of baseline fields to populate
    */
   private void populateBaseline(Task task, Task baseline, TaskField[] baselineFields)
   {
      IntStream.range(0, SOURCE_FIELDS.length).forEach(i -> task.set(baselineFields[i], baseline == null ? null : baseline.getCachedValue(SOURCE_FIELDS[i])));
   }

   /**
    * Determines the set of baseline fields to populate. This is either the
    * main baseline fields (when index is 0), or the baseline 1-10 fields.
    * 
    * @param index index of the baseline to populate (0-10)
    * @return array of baseline fields
    */
   private TaskField[] getBaselineFields(int index)
   {
      TaskField[] fields;
      if (index == 0)
      {
         fields = BASELINE0_FIELDS;
      }
      else
      {
         --index;
         fields = new TaskField[]
         {
            TaskFieldLists.BASELINE_COSTS[index],
            TaskFieldLists.BASELINE_DURATIONS[index],
            TaskFieldLists.BASELINE_FINISHES[index],
            TaskFieldLists.BASELINE_FIXED_COST_ACCRUALS[index],
            TaskFieldLists.BASELINE_FIXED_COSTS[index],
            TaskFieldLists.BASELINE_STARTS[index],
            TaskFieldLists.BASELINE_WORKS[index]
         };
      }
      return fields;
   }

   private static final TaskField[] SOURCE_FIELDS =
   {
      TaskField.COST,
      TaskField.DURATION,
      TaskField.FINISH,
      TaskField.FIXED_COST_ACCRUAL,
      TaskField.FIXED_COST,
      TaskField.START,
      TaskField.WORK
   };

   private static final TaskField[] BASELINE0_FIELDS =
   {
      TaskField.BASELINE_COST,
      TaskField.BASELINE_DURATION,
      TaskField.BASELINE_FINISH,
      TaskField.BASELINE_FIXED_COST_ACCRUAL,
      TaskField.BASELINE_FIXED_COST,
      TaskField.BASELINE_START,
      TaskField.BASELINE_WORK
   };
}
