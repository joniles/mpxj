/*
 * file:       MPPTaskField14.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2010
 * date:       09/03/2010
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

/**
 * Utility class used to map between the integer values held in MS Project
 * to represent a task field, and the enumerated type used to represent
 * task fields in MPXJ.
 */
public class MPPTaskField14 extends MPPTaskField
{
   /**
    * Retrieve an instance of the TaskField class based on the data read from an
    * MS Project file.
    *
    * @param value value from an MS Project file
    * @return TaskField instance
    */   
   public static TaskField getInstance(int value)
   {
      TaskField result = null;

      if (value >= 0 && value < FIELD_ARRAY.length)
      {
         result = FIELD_ARRAY[value];
      }

      return (result);
   }

   /**
    * Retrieve the ID of a field, as used by MS Project.
    * 
    * @param value field instance
    * @return field ID
    */   
   public static int getID(TaskField value)
   {
      return (ID_ARRAY[value.getValue()]);
   }

   

   private static final int BASELINE1_DELIVERABLE_FINISH_VALUE = 1;   
   private static final int ASSIGNMENT_OWNER_VALUE = 1170;   
   private static final int BASELINE1_BUDGET_WORK_VALUE = 1173;   
   private static final int BASELINE_DELIVERABLE_START_VALUE = 1174;   
   private static final int BASELINE_DELIVERABLE_FINISH_VALUE = 1175;
   private static final int BASELINE_BUDGET_WORK_VALUE = 1176;
   private static final int BASELINE_BUDGET_COST_VALUE = 1177;
   private static final int BASELINE1_DELIVERABLE_START_VALUE = 1184;   
   private static final int ACTIVE_VALUE = 1279;
   private static final int TASK_MODE_VALUE = 1280;
   private static final int START_VALUE = 1285;
   private static final int FINISH_VALUE = 1286;
   private static final int DURATION_VALUE = 1287;
   private static final int BASELINE1_COST_VALUE = 1299;   
   private static final int BASELINE_WORK_VALUE = 1300;   
   private static final int BASELINE_DURATION_VALUE = 1301;
   
   private static final int MAX_VALUE = 1302;

   private static final TaskField[] FIELD_ARRAY = new TaskField[MAX_VALUE];

   static
   {
      //
      // Initialise the array
      //
      TaskField[] base = getTaskFieldArray();
      System.arraycopy(base, 0, FIELD_ARRAY, 0, base.length);
      
      //
      // Overlay changes
      //
      FIELD_ARRAY[BASELINE1_DELIVERABLE_FINISH_VALUE] = TaskField.BASELINE1_DELIVERABLE_FINISH;      
      FIELD_ARRAY[ASSIGNMENT_OWNER_VALUE] = TaskField.ASSIGNMENT_OWNER;      
      FIELD_ARRAY[BASELINE1_BUDGET_WORK_VALUE] = TaskField.BASELINE1_BUDGET_WORK;      
      FIELD_ARRAY[BASELINE_DELIVERABLE_START_VALUE] = TaskField.BASELINE_DELIVERABLE_START;      
      FIELD_ARRAY[BASELINE_DELIVERABLE_FINISH_VALUE] = TaskField.BASELINE_DELIVERABLE_FINISH;      
      FIELD_ARRAY[BASELINE_BUDGET_WORK_VALUE] = TaskField.BASELINE_BUDGET_WORK;      
      FIELD_ARRAY[BASELINE_BUDGET_COST_VALUE] = TaskField.BASELINE_BUDGET_COST;      
      FIELD_ARRAY[BASELINE1_DELIVERABLE_START_VALUE] = TaskField.BASELINE1_DELIVERABLE_START;      
      FIELD_ARRAY[ACTIVE_VALUE] = TaskField.ACTIVE;
      FIELD_ARRAY[TASK_MODE_VALUE] = TaskField.TASK_MODE;      
      FIELD_ARRAY[START_VALUE] = TaskField.START;      
      FIELD_ARRAY[FINISH_VALUE] = TaskField.FINISH;
      FIELD_ARRAY[DURATION_VALUE] = TaskField.DURATION;      
      FIELD_ARRAY[BASELINE1_COST_VALUE] = TaskField.BASELINE1_COST;      
      FIELD_ARRAY[BASELINE_WORK_VALUE] = TaskField.BASELINE_WORK;      
      FIELD_ARRAY[BASELINE_DURATION_VALUE] = TaskField.BASELINE_DURATION;      
   }

   private static final int[] ID_ARRAY = new int[TaskField.MAX_VALUE];

   static
   {
      //
      // Initialise the array
      //
      int[] base = getIDArray();
      System.arraycopy(base, 0, ID_ARRAY, 0, base.length);

      //
      // Overlay changes
      //      
      ID_ARRAY[TaskField.BASELINE1_DELIVERABLE_FINISH_VALUE] = BASELINE1_DELIVERABLE_FINISH_VALUE;      
      ID_ARRAY[TaskField.ASSIGNMENT_OWNER_VALUE] = ASSIGNMENT_OWNER_VALUE;
      ID_ARRAY[TaskField.BASELINE1_BUDGET_WORK_VALUE] = BASELINE1_BUDGET_WORK_VALUE;      
      ID_ARRAY[TaskField.BASELINE_DELIVERABLE_START_VALUE] = BASELINE_DELIVERABLE_START_VALUE;      
      ID_ARRAY[TaskField.BASELINE_DELIVERABLE_FINISH_VALUE] = BASELINE_DELIVERABLE_FINISH_VALUE;      
      ID_ARRAY[TaskField.BASELINE_BUDGET_WORK_VALUE] = BASELINE_BUDGET_WORK_VALUE;      
      ID_ARRAY[TaskField.BASELINE_BUDGET_COST_VALUE] = BASELINE_BUDGET_COST_VALUE;
      ID_ARRAY[TaskField.BASELINE1_DELIVERABLE_START_VALUE] = BASELINE1_DELIVERABLE_START_VALUE;      
      ID_ARRAY[TaskField.ACTIVE_VALUE] = ACTIVE_VALUE;
      ID_ARRAY[TaskField.TASK_MODE_VALUE] = TASK_MODE_VALUE;
      ID_ARRAY[TaskField.START_VALUE] = START_VALUE;
      ID_ARRAY[TaskField.FINISH_VALUE] = FINISH_VALUE;
      ID_ARRAY[TaskField.DURATION_VALUE] = DURATION_VALUE;      
      ID_ARRAY[TaskField.BASELINE1_COST_VALUE] = BASELINE1_COST_VALUE;      
      ID_ARRAY[TaskField.BASELINE_WORK_VALUE] = BASELINE_WORK_VALUE;      
      ID_ARRAY[TaskField.BASELINE_DURATION_VALUE] = BASELINE_DURATION_VALUE;      
   }
}
