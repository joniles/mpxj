/*
 * file:       TaskFieldLists.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2014
 * date:       17/11/2014
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

package org.mpxj.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.mpxj.TaskField;

/**
 * Task fields grouped into logical collections.
 */
public final class TaskFieldLists
{
   public static final TaskField[] CUSTOM_COST =
   {
      TaskField.COST1,
      TaskField.COST2,
      TaskField.COST3,
      TaskField.COST4,
      TaskField.COST5,
      TaskField.COST6,
      TaskField.COST7,
      TaskField.COST8,
      TaskField.COST9,
      TaskField.COST10
   };

   public static final TaskField[] CUSTOM_DATE =
   {
      TaskField.DATE1,
      TaskField.DATE2,
      TaskField.DATE3,
      TaskField.DATE4,
      TaskField.DATE5,
      TaskField.DATE6,
      TaskField.DATE7,
      TaskField.DATE8,
      TaskField.DATE9,
      TaskField.DATE10
   };

   public static final TaskField[] CUSTOM_DURATION =
   {
      TaskField.DURATION1,
      TaskField.DURATION2,
      TaskField.DURATION3,
      TaskField.DURATION4,
      TaskField.DURATION5,
      TaskField.DURATION6,
      TaskField.DURATION7,
      TaskField.DURATION8,
      TaskField.DURATION9,
      TaskField.DURATION10
   };

   public static final TaskField[] CUSTOM_DURATION_UNITS =
   {
      TaskField.DURATION1_UNITS,
      TaskField.DURATION2_UNITS,
      TaskField.DURATION3_UNITS,
      TaskField.DURATION4_UNITS,
      TaskField.DURATION5_UNITS,
      TaskField.DURATION6_UNITS,
      TaskField.DURATION7_UNITS,
      TaskField.DURATION8_UNITS,
      TaskField.DURATION9_UNITS,
      TaskField.DURATION10_UNITS
   };

   public static final TaskField[] CUSTOM_FLAG =
   {
      TaskField.FLAG1,
      TaskField.FLAG2,
      TaskField.FLAG3,
      TaskField.FLAG4,
      TaskField.FLAG5,
      TaskField.FLAG6,
      TaskField.FLAG7,
      TaskField.FLAG8,
      TaskField.FLAG9,
      TaskField.FLAG10,
      TaskField.FLAG11,
      TaskField.FLAG12,
      TaskField.FLAG13,
      TaskField.FLAG14,
      TaskField.FLAG15,
      TaskField.FLAG16,
      TaskField.FLAG17,
      TaskField.FLAG18,
      TaskField.FLAG19,
      TaskField.FLAG20
   };

   public static final TaskField[] CUSTOM_FINISH =
   {
      TaskField.FINISH1,
      TaskField.FINISH2,
      TaskField.FINISH3,
      TaskField.FINISH4,
      TaskField.FINISH5,
      TaskField.FINISH6,
      TaskField.FINISH7,
      TaskField.FINISH8,
      TaskField.FINISH9,
      TaskField.FINISH10
   };

   public static final TaskField[] CUSTOM_NUMBER =
   {
      TaskField.NUMBER1,
      TaskField.NUMBER2,
      TaskField.NUMBER3,
      TaskField.NUMBER4,
      TaskField.NUMBER5,
      TaskField.NUMBER6,
      TaskField.NUMBER7,
      TaskField.NUMBER8,
      TaskField.NUMBER9,
      TaskField.NUMBER10,
      TaskField.NUMBER11,
      TaskField.NUMBER12,
      TaskField.NUMBER13,
      TaskField.NUMBER14,
      TaskField.NUMBER15,
      TaskField.NUMBER16,
      TaskField.NUMBER17,
      TaskField.NUMBER18,
      TaskField.NUMBER19,
      TaskField.NUMBER20
   };

   public static final TaskField[] CUSTOM_START =
   {
      TaskField.START1,
      TaskField.START2,
      TaskField.START3,
      TaskField.START4,
      TaskField.START5,
      TaskField.START6,
      TaskField.START7,
      TaskField.START8,
      TaskField.START9,
      TaskField.START10
   };

   public static final TaskField[] CUSTOM_TEXT =
   {
      TaskField.TEXT1,
      TaskField.TEXT2,
      TaskField.TEXT3,
      TaskField.TEXT4,
      TaskField.TEXT5,
      TaskField.TEXT6,
      TaskField.TEXT7,
      TaskField.TEXT8,
      TaskField.TEXT9,
      TaskField.TEXT10,
      TaskField.TEXT11,
      TaskField.TEXT12,
      TaskField.TEXT13,
      TaskField.TEXT14,
      TaskField.TEXT15,
      TaskField.TEXT16,
      TaskField.TEXT17,
      TaskField.TEXT18,
      TaskField.TEXT19,
      TaskField.TEXT20,
      TaskField.TEXT21,
      TaskField.TEXT22,
      TaskField.TEXT23,
      TaskField.TEXT24,
      TaskField.TEXT25,
      TaskField.TEXT26,
      TaskField.TEXT27,
      TaskField.TEXT28,
      TaskField.TEXT29,
      TaskField.TEXT30
   };

   public static final TaskField[] CUSTOM_OUTLINE_CODE =
   {
      TaskField.OUTLINE_CODE1,
      TaskField.OUTLINE_CODE2,
      TaskField.OUTLINE_CODE3,
      TaskField.OUTLINE_CODE4,
      TaskField.OUTLINE_CODE5,
      TaskField.OUTLINE_CODE6,
      TaskField.OUTLINE_CODE7,
      TaskField.OUTLINE_CODE8,
      TaskField.OUTLINE_CODE9,
      TaskField.OUTLINE_CODE10
   };

   public static final TaskField[] CUSTOM_OUTLINE_CODE_INDEX =
   {
      TaskField.OUTLINE_CODE1_INDEX,
      TaskField.OUTLINE_CODE2_INDEX,
      TaskField.OUTLINE_CODE3_INDEX,
      TaskField.OUTLINE_CODE4_INDEX,
      TaskField.OUTLINE_CODE5_INDEX,
      TaskField.OUTLINE_CODE6_INDEX,
      TaskField.OUTLINE_CODE7_INDEX,
      TaskField.OUTLINE_CODE8_INDEX,
      TaskField.OUTLINE_CODE9_INDEX,
      TaskField.OUTLINE_CODE10_INDEX
   };

   public static final TaskField[] ENTERPRISE_CUSTOM_COST =
   {
      TaskField.ENTERPRISE_COST1,
      TaskField.ENTERPRISE_COST2,
      TaskField.ENTERPRISE_COST3,
      TaskField.ENTERPRISE_COST4,
      TaskField.ENTERPRISE_COST5,
      TaskField.ENTERPRISE_COST6,
      TaskField.ENTERPRISE_COST7,
      TaskField.ENTERPRISE_COST8,
      TaskField.ENTERPRISE_COST9,
      TaskField.ENTERPRISE_COST10
   };

   public static final TaskField[] ENTERPRISE_CUSTOM_DATE =
   {
      TaskField.ENTERPRISE_DATE1,
      TaskField.ENTERPRISE_DATE2,
      TaskField.ENTERPRISE_DATE3,
      TaskField.ENTERPRISE_DATE4,
      TaskField.ENTERPRISE_DATE5,
      TaskField.ENTERPRISE_DATE6,
      TaskField.ENTERPRISE_DATE7,
      TaskField.ENTERPRISE_DATE8,
      TaskField.ENTERPRISE_DATE9,
      TaskField.ENTERPRISE_DATE10,
      TaskField.ENTERPRISE_DATE11,
      TaskField.ENTERPRISE_DATE12,
      TaskField.ENTERPRISE_DATE13,
      TaskField.ENTERPRISE_DATE14,
      TaskField.ENTERPRISE_DATE15,
      TaskField.ENTERPRISE_DATE16,
      TaskField.ENTERPRISE_DATE17,
      TaskField.ENTERPRISE_DATE18,
      TaskField.ENTERPRISE_DATE19,
      TaskField.ENTERPRISE_DATE20,
      TaskField.ENTERPRISE_DATE21,
      TaskField.ENTERPRISE_DATE22,
      TaskField.ENTERPRISE_DATE23,
      TaskField.ENTERPRISE_DATE24,
      TaskField.ENTERPRISE_DATE25,
      TaskField.ENTERPRISE_DATE26,
      TaskField.ENTERPRISE_DATE27,
      TaskField.ENTERPRISE_DATE28,
      TaskField.ENTERPRISE_DATE29,
      TaskField.ENTERPRISE_DATE30
   };

   public static final TaskField[] ENTERPRISE_CUSTOM_DURATION =
   {
      TaskField.ENTERPRISE_DURATION1,
      TaskField.ENTERPRISE_DURATION2,
      TaskField.ENTERPRISE_DURATION3,
      TaskField.ENTERPRISE_DURATION4,
      TaskField.ENTERPRISE_DURATION5,
      TaskField.ENTERPRISE_DURATION6,
      TaskField.ENTERPRISE_DURATION7,
      TaskField.ENTERPRISE_DURATION8,
      TaskField.ENTERPRISE_DURATION9,
      TaskField.ENTERPRISE_DURATION10
   };

   public static final TaskField[] ENTERPRISE_CUSTOM_FLAG =
   {
      TaskField.ENTERPRISE_FLAG1,
      TaskField.ENTERPRISE_FLAG2,
      TaskField.ENTERPRISE_FLAG3,
      TaskField.ENTERPRISE_FLAG4,
      TaskField.ENTERPRISE_FLAG5,
      TaskField.ENTERPRISE_FLAG6,
      TaskField.ENTERPRISE_FLAG7,
      TaskField.ENTERPRISE_FLAG8,
      TaskField.ENTERPRISE_FLAG9,
      TaskField.ENTERPRISE_FLAG10,
      TaskField.ENTERPRISE_FLAG11,
      TaskField.ENTERPRISE_FLAG12,
      TaskField.ENTERPRISE_FLAG13,
      TaskField.ENTERPRISE_FLAG14,
      TaskField.ENTERPRISE_FLAG15,
      TaskField.ENTERPRISE_FLAG16,
      TaskField.ENTERPRISE_FLAG17,
      TaskField.ENTERPRISE_FLAG18,
      TaskField.ENTERPRISE_FLAG19,
      TaskField.ENTERPRISE_FLAG20
   };

   public static final TaskField[] ENTERPRISE_CUSTOM_NUMBER =
   {
      TaskField.ENTERPRISE_NUMBER1,
      TaskField.ENTERPRISE_NUMBER2,
      TaskField.ENTERPRISE_NUMBER3,
      TaskField.ENTERPRISE_NUMBER4,
      TaskField.ENTERPRISE_NUMBER5,
      TaskField.ENTERPRISE_NUMBER6,
      TaskField.ENTERPRISE_NUMBER7,
      TaskField.ENTERPRISE_NUMBER8,
      TaskField.ENTERPRISE_NUMBER9,
      TaskField.ENTERPRISE_NUMBER10,
      TaskField.ENTERPRISE_NUMBER11,
      TaskField.ENTERPRISE_NUMBER12,
      TaskField.ENTERPRISE_NUMBER13,
      TaskField.ENTERPRISE_NUMBER14,
      TaskField.ENTERPRISE_NUMBER15,
      TaskField.ENTERPRISE_NUMBER16,
      TaskField.ENTERPRISE_NUMBER17,
      TaskField.ENTERPRISE_NUMBER18,
      TaskField.ENTERPRISE_NUMBER19,
      TaskField.ENTERPRISE_NUMBER20,
      TaskField.ENTERPRISE_NUMBER21,
      TaskField.ENTERPRISE_NUMBER22,
      TaskField.ENTERPRISE_NUMBER23,
      TaskField.ENTERPRISE_NUMBER24,
      TaskField.ENTERPRISE_NUMBER25,
      TaskField.ENTERPRISE_NUMBER26,
      TaskField.ENTERPRISE_NUMBER27,
      TaskField.ENTERPRISE_NUMBER28,
      TaskField.ENTERPRISE_NUMBER29,
      TaskField.ENTERPRISE_NUMBER30,
      TaskField.ENTERPRISE_NUMBER31,
      TaskField.ENTERPRISE_NUMBER32,
      TaskField.ENTERPRISE_NUMBER33,
      TaskField.ENTERPRISE_NUMBER34,
      TaskField.ENTERPRISE_NUMBER35,
      TaskField.ENTERPRISE_NUMBER36,
      TaskField.ENTERPRISE_NUMBER37,
      TaskField.ENTERPRISE_NUMBER38,
      TaskField.ENTERPRISE_NUMBER39,
      TaskField.ENTERPRISE_NUMBER40
   };

   public static final TaskField[] ENTERPRISE_CUSTOM_TEXT =
   {
      TaskField.ENTERPRISE_TEXT1,
      TaskField.ENTERPRISE_TEXT2,
      TaskField.ENTERPRISE_TEXT3,
      TaskField.ENTERPRISE_TEXT4,
      TaskField.ENTERPRISE_TEXT5,
      TaskField.ENTERPRISE_TEXT6,
      TaskField.ENTERPRISE_TEXT7,
      TaskField.ENTERPRISE_TEXT8,
      TaskField.ENTERPRISE_TEXT9,
      TaskField.ENTERPRISE_TEXT10,
      TaskField.ENTERPRISE_TEXT11,
      TaskField.ENTERPRISE_TEXT12,
      TaskField.ENTERPRISE_TEXT13,
      TaskField.ENTERPRISE_TEXT14,
      TaskField.ENTERPRISE_TEXT15,
      TaskField.ENTERPRISE_TEXT16,
      TaskField.ENTERPRISE_TEXT17,
      TaskField.ENTERPRISE_TEXT18,
      TaskField.ENTERPRISE_TEXT19,
      TaskField.ENTERPRISE_TEXT20,
      TaskField.ENTERPRISE_TEXT21,
      TaskField.ENTERPRISE_TEXT22,
      TaskField.ENTERPRISE_TEXT23,
      TaskField.ENTERPRISE_TEXT24,
      TaskField.ENTERPRISE_TEXT25,
      TaskField.ENTERPRISE_TEXT26,
      TaskField.ENTERPRISE_TEXT27,
      TaskField.ENTERPRISE_TEXT28,
      TaskField.ENTERPRISE_TEXT29,
      TaskField.ENTERPRISE_TEXT30,
      TaskField.ENTERPRISE_TEXT31,
      TaskField.ENTERPRISE_TEXT32,
      TaskField.ENTERPRISE_TEXT33,
      TaskField.ENTERPRISE_TEXT34,
      TaskField.ENTERPRISE_TEXT35,
      TaskField.ENTERPRISE_TEXT36,
      TaskField.ENTERPRISE_TEXT37,
      TaskField.ENTERPRISE_TEXT38,
      TaskField.ENTERPRISE_TEXT39,
      TaskField.ENTERPRISE_TEXT40
   };

   public static final TaskField[] BASELINE_COSTS =
   {
      TaskField.BASELINE1_COST,
      TaskField.BASELINE2_COST,
      TaskField.BASELINE3_COST,
      TaskField.BASELINE4_COST,
      TaskField.BASELINE5_COST,
      TaskField.BASELINE6_COST,
      TaskField.BASELINE7_COST,
      TaskField.BASELINE8_COST,
      TaskField.BASELINE9_COST,
      TaskField.BASELINE10_COST
   };

   public static final TaskField[] BASELINE_DURATIONS =
   {
      TaskField.BASELINE1_DURATION,
      TaskField.BASELINE2_DURATION,
      TaskField.BASELINE3_DURATION,
      TaskField.BASELINE4_DURATION,
      TaskField.BASELINE5_DURATION,
      TaskField.BASELINE6_DURATION,
      TaskField.BASELINE7_DURATION,
      TaskField.BASELINE8_DURATION,
      TaskField.BASELINE9_DURATION,
      TaskField.BASELINE10_DURATION
   };

   public static final TaskField[] BASELINE_ESTIMATED_DURATIONS =
   {
      TaskField.BASELINE1_ESTIMATED_DURATION,
      TaskField.BASELINE2_ESTIMATED_DURATION,
      TaskField.BASELINE3_ESTIMATED_DURATION,
      TaskField.BASELINE4_ESTIMATED_DURATION,
      TaskField.BASELINE5_ESTIMATED_DURATION,
      TaskField.BASELINE6_ESTIMATED_DURATION,
      TaskField.BASELINE7_ESTIMATED_DURATION,
      TaskField.BASELINE8_ESTIMATED_DURATION,
      TaskField.BASELINE9_ESTIMATED_DURATION,
      TaskField.BASELINE10_ESTIMATED_DURATION
   };

   public static final TaskField[] BASELINE_STARTS =
   {
      TaskField.BASELINE1_START,
      TaskField.BASELINE2_START,
      TaskField.BASELINE3_START,
      TaskField.BASELINE4_START,
      TaskField.BASELINE5_START,
      TaskField.BASELINE6_START,
      TaskField.BASELINE7_START,
      TaskField.BASELINE8_START,
      TaskField.BASELINE9_START,
      TaskField.BASELINE10_START
   };

   public static final TaskField[] BASELINE_ESTIMATED_STARTS =
   {
      TaskField.BASELINE1_ESTIMATED_START,
      TaskField.BASELINE2_ESTIMATED_START,
      TaskField.BASELINE3_ESTIMATED_START,
      TaskField.BASELINE4_ESTIMATED_START,
      TaskField.BASELINE5_ESTIMATED_START,
      TaskField.BASELINE6_ESTIMATED_START,
      TaskField.BASELINE7_ESTIMATED_START,
      TaskField.BASELINE8_ESTIMATED_START,
      TaskField.BASELINE9_ESTIMATED_START,
      TaskField.BASELINE10_ESTIMATED_START
   };

   public static final TaskField[] BASELINE_FINISHES =
   {
      TaskField.BASELINE1_FINISH,
      TaskField.BASELINE2_FINISH,
      TaskField.BASELINE3_FINISH,
      TaskField.BASELINE4_FINISH,
      TaskField.BASELINE5_FINISH,
      TaskField.BASELINE6_FINISH,
      TaskField.BASELINE7_FINISH,
      TaskField.BASELINE8_FINISH,
      TaskField.BASELINE9_FINISH,
      TaskField.BASELINE10_FINISH
   };

   public static final TaskField[] BASELINE_ESTIMATED_FINISHES =
   {
      TaskField.BASELINE1_ESTIMATED_FINISH,
      TaskField.BASELINE2_ESTIMATED_FINISH,
      TaskField.BASELINE3_ESTIMATED_FINISH,
      TaskField.BASELINE4_ESTIMATED_FINISH,
      TaskField.BASELINE5_ESTIMATED_FINISH,
      TaskField.BASELINE6_ESTIMATED_FINISH,
      TaskField.BASELINE7_ESTIMATED_FINISH,
      TaskField.BASELINE8_ESTIMATED_FINISH,
      TaskField.BASELINE9_ESTIMATED_FINISH,
      TaskField.BASELINE10_ESTIMATED_FINISH
   };

   public static final TaskField[] BASELINE_WORKS =
   {
      TaskField.BASELINE1_WORK,
      TaskField.BASELINE2_WORK,
      TaskField.BASELINE3_WORK,
      TaskField.BASELINE4_WORK,
      TaskField.BASELINE5_WORK,
      TaskField.BASELINE6_WORK,
      TaskField.BASELINE7_WORK,
      TaskField.BASELINE8_WORK,
      TaskField.BASELINE9_WORK,
      TaskField.BASELINE10_WORK
   };

   public static final TaskField[] BASELINE_FIXED_COSTS =
   {
      TaskField.BASELINE1_FIXED_COST,
      TaskField.BASELINE2_FIXED_COST,
      TaskField.BASELINE3_FIXED_COST,
      TaskField.BASELINE4_FIXED_COST,
      TaskField.BASELINE5_FIXED_COST,
      TaskField.BASELINE6_FIXED_COST,
      TaskField.BASELINE7_FIXED_COST,
      TaskField.BASELINE8_FIXED_COST,
      TaskField.BASELINE9_FIXED_COST,
      TaskField.BASELINE10_FIXED_COST
   };

   public static final TaskField[] BASELINE_FIXED_COST_ACCRUALS =
   {
      TaskField.BASELINE1_FIXED_COST_ACCRUAL,
      TaskField.BASELINE2_FIXED_COST_ACCRUAL,
      TaskField.BASELINE3_FIXED_COST_ACCRUAL,
      TaskField.BASELINE4_FIXED_COST_ACCRUAL,
      TaskField.BASELINE5_FIXED_COST_ACCRUAL,
      TaskField.BASELINE6_FIXED_COST_ACCRUAL,
      TaskField.BASELINE7_FIXED_COST_ACCRUAL,
      TaskField.BASELINE8_FIXED_COST_ACCRUAL,
      TaskField.BASELINE9_FIXED_COST_ACCRUAL,
      TaskField.BASELINE10_FIXED_COST_ACCRUAL
   };

   public static final TaskField[] BASELINE_BUDGET_COSTS =
   {
      TaskField.BASELINE1_BUDGET_COST,
      TaskField.BASELINE2_BUDGET_COST,
      TaskField.BASELINE3_BUDGET_COST,
      TaskField.BASELINE4_BUDGET_COST,
      TaskField.BASELINE5_BUDGET_COST,
      TaskField.BASELINE6_BUDGET_COST,
      TaskField.BASELINE7_BUDGET_COST,
      TaskField.BASELINE8_BUDGET_COST,
      TaskField.BASELINE9_BUDGET_COST,
      TaskField.BASELINE10_BUDGET_COST
   };

   public static final TaskField[] BASELINE_BUDGET_WORKS =
   {
      TaskField.BASELINE1_BUDGET_WORK,
      TaskField.BASELINE2_BUDGET_WORK,
      TaskField.BASELINE3_BUDGET_WORK,
      TaskField.BASELINE4_BUDGET_WORK,
      TaskField.BASELINE5_BUDGET_WORK,
      TaskField.BASELINE6_BUDGET_WORK,
      TaskField.BASELINE7_BUDGET_WORK,
      TaskField.BASELINE8_BUDGET_WORK,
      TaskField.BASELINE9_BUDGET_WORK,
      TaskField.BASELINE10_BUDGET_WORK
   };

   public static final List<TaskField> CUSTOM_FIELDS = new ArrayList<>();
   static
   {
      CUSTOM_FIELDS.addAll(Arrays.asList(TaskFieldLists.CUSTOM_TEXT));
      CUSTOM_FIELDS.addAll(Arrays.asList(TaskFieldLists.CUSTOM_DATE));
      CUSTOM_FIELDS.addAll(Arrays.asList(TaskFieldLists.CUSTOM_START));
      CUSTOM_FIELDS.addAll(Arrays.asList(TaskFieldLists.CUSTOM_FINISH));
      CUSTOM_FIELDS.addAll(Arrays.asList(TaskFieldLists.CUSTOM_COST));
      CUSTOM_FIELDS.addAll(Arrays.asList(TaskFieldLists.CUSTOM_FLAG));
      CUSTOM_FIELDS.addAll(Arrays.asList(TaskFieldLists.CUSTOM_NUMBER));
      CUSTOM_FIELDS.addAll(Arrays.asList(TaskFieldLists.CUSTOM_DURATION));
      CUSTOM_FIELDS.addAll(Arrays.asList(TaskFieldLists.CUSTOM_OUTLINE_CODE));
      CUSTOM_FIELDS.addAll(Arrays.asList(TaskFieldLists.ENTERPRISE_CUSTOM_TEXT));
      CUSTOM_FIELDS.addAll(Arrays.asList(TaskFieldLists.ENTERPRISE_CUSTOM_DATE));
      CUSTOM_FIELDS.addAll(Arrays.asList(TaskFieldLists.ENTERPRISE_CUSTOM_COST));
      CUSTOM_FIELDS.addAll(Arrays.asList(TaskFieldLists.ENTERPRISE_CUSTOM_FLAG));
      CUSTOM_FIELDS.addAll(Arrays.asList(TaskFieldLists.ENTERPRISE_CUSTOM_NUMBER));
      CUSTOM_FIELDS.addAll(Arrays.asList(TaskFieldLists.ENTERPRISE_CUSTOM_DURATION));
   }
}
