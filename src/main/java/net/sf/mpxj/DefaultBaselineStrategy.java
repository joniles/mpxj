/*
 * file:       DefaultBaselineStrategy.java
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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import net.sf.mpxj.common.TaskFieldLists;

/**
 * Handles setting baseline fields in one project using values read from another project.
 */
public class DefaultBaselineStrategy implements BaselineStrategy
{
   @Override public void clearBaseline(ProjectFile project, int index)
   {
      TaskField[] baselineFields = getBaselineFields(index);
      project.getTasks().forEach(t -> populateBaseline(t, null, baselineFields));
   }

   @Override public void populateBaseline(ProjectFile project, ProjectFile baseline, int index)
   {
      TaskField[] baselineFields = getBaselineFields(index);
      Map<Object, Task> map = baseline.getTasks().stream().filter(t -> getKeyForTask(t) != null).collect(Collectors.toMap(this::getKeyForTask, t -> t, (u, v) -> null));
      project.getTasks().forEach(t -> populateBaseline(t, map.get(getKeyForTask(t)), baselineFields));
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
      TaskField[] sourceFields = getSourceFields();
      IntStream.range(0, sourceFields.length).forEach(i -> task.set(baselineFields[i], baseline == null ? null : baseline.getCachedValue(sourceFields[i])));
   }

   /**
    * This method is used to generate the key which connect tasks from the
    * current and baseline schedules. This key should be unique for each task
    * in the schedule. Instances where the key is not unique will result in an
    * incorrect baseline being applied to a task in the current schedule.
    *
    * If a task in the baseline schedule and a task in the main schedule both
    * generate the same key value, they are treated as the same task and values
    * from the task in the baseline schedule will be used to populate baseline
    * attributes in the main schedule.
    *
    * This default implementation assumes that the task's GUID attribute is
    * sufficient to match tasks in the baseline and main schedules. It is expected
    * that this method is overridden for specific schedule types.
    *
    * @param task task from which a key is generated
    * @return key value
    */
   protected Object getKeyForTask(Task task)
   {
      return task.getGUID();
   }

   /**
    * Determines the set of baseline fields to populate. This is either the
    * main baseline fields (when index is 0), or the baseline 1-10 fields.
    *
    * @param index index of the baseline to populate (0-10)
    * @return array of baseline fields
    */
   protected TaskField[] getBaselineFields(int index)
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

   /**
    * Fields from which values are retrieved in the baseline schedule
    * before being applied as baseline attributes to the main schedule.
    *
    * @return source fields for baseline values
    */
   protected TaskField[] getSourceFields()
   {
      return SOURCE_FIELDS;
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
