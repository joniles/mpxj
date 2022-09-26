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

package net.sf.mpxj.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sf.mpxj.TaskField;

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

   public static final TaskField[] ENTERPRISE_COST =
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

   public static final TaskField[] ENTERPRISE_DATE =
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

   public static final TaskField[] ENTERPRISE_DURATION =
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

   public static final TaskField[] ENTERPRISE_FLAG =
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

   public static final TaskField[] ENTERPRISE_NUMBER =
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

   public static final TaskField[] ENTERPRISE_TEXT =
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

   public static final TaskField[] ENTERPRISE_CUSTOM_FIELD =
   {
      TaskField.ENTERPRISE_CUSTOM_FIELD1,
      TaskField.ENTERPRISE_CUSTOM_FIELD2,
      TaskField.ENTERPRISE_CUSTOM_FIELD3,
      TaskField.ENTERPRISE_CUSTOM_FIELD4,
      TaskField.ENTERPRISE_CUSTOM_FIELD5,
      TaskField.ENTERPRISE_CUSTOM_FIELD6,
      TaskField.ENTERPRISE_CUSTOM_FIELD7,
      TaskField.ENTERPRISE_CUSTOM_FIELD8,
      TaskField.ENTERPRISE_CUSTOM_FIELD9,
      TaskField.ENTERPRISE_CUSTOM_FIELD10,
      TaskField.ENTERPRISE_CUSTOM_FIELD11,
      TaskField.ENTERPRISE_CUSTOM_FIELD12,
      TaskField.ENTERPRISE_CUSTOM_FIELD13,
      TaskField.ENTERPRISE_CUSTOM_FIELD14,
      TaskField.ENTERPRISE_CUSTOM_FIELD15,
      TaskField.ENTERPRISE_CUSTOM_FIELD16,
      TaskField.ENTERPRISE_CUSTOM_FIELD17,
      TaskField.ENTERPRISE_CUSTOM_FIELD18,
      TaskField.ENTERPRISE_CUSTOM_FIELD19,
      TaskField.ENTERPRISE_CUSTOM_FIELD20,
      TaskField.ENTERPRISE_CUSTOM_FIELD21,
      TaskField.ENTERPRISE_CUSTOM_FIELD22,
      TaskField.ENTERPRISE_CUSTOM_FIELD23,
      TaskField.ENTERPRISE_CUSTOM_FIELD24,
      TaskField.ENTERPRISE_CUSTOM_FIELD25,
      TaskField.ENTERPRISE_CUSTOM_FIELD26,
      TaskField.ENTERPRISE_CUSTOM_FIELD27,
      TaskField.ENTERPRISE_CUSTOM_FIELD28,
      TaskField.ENTERPRISE_CUSTOM_FIELD29,
      TaskField.ENTERPRISE_CUSTOM_FIELD30,
      TaskField.ENTERPRISE_CUSTOM_FIELD31,
      TaskField.ENTERPRISE_CUSTOM_FIELD32,
      TaskField.ENTERPRISE_CUSTOM_FIELD33,
      TaskField.ENTERPRISE_CUSTOM_FIELD34,
      TaskField.ENTERPRISE_CUSTOM_FIELD35,
      TaskField.ENTERPRISE_CUSTOM_FIELD36,
      TaskField.ENTERPRISE_CUSTOM_FIELD37,
      TaskField.ENTERPRISE_CUSTOM_FIELD38,
      TaskField.ENTERPRISE_CUSTOM_FIELD39,
      TaskField.ENTERPRISE_CUSTOM_FIELD40,
      TaskField.ENTERPRISE_CUSTOM_FIELD41,
      TaskField.ENTERPRISE_CUSTOM_FIELD42,
      TaskField.ENTERPRISE_CUSTOM_FIELD43,
      TaskField.ENTERPRISE_CUSTOM_FIELD44,
      TaskField.ENTERPRISE_CUSTOM_FIELD45,
      TaskField.ENTERPRISE_CUSTOM_FIELD46,
      TaskField.ENTERPRISE_CUSTOM_FIELD47,
      TaskField.ENTERPRISE_CUSTOM_FIELD48,
      TaskField.ENTERPRISE_CUSTOM_FIELD49,
      TaskField.ENTERPRISE_CUSTOM_FIELD50,
      TaskField.ENTERPRISE_CUSTOM_FIELD51,
      TaskField.ENTERPRISE_CUSTOM_FIELD52,
      TaskField.ENTERPRISE_CUSTOM_FIELD53,
      TaskField.ENTERPRISE_CUSTOM_FIELD54,
      TaskField.ENTERPRISE_CUSTOM_FIELD55,
      TaskField.ENTERPRISE_CUSTOM_FIELD56,
      TaskField.ENTERPRISE_CUSTOM_FIELD57,
      TaskField.ENTERPRISE_CUSTOM_FIELD58,
      TaskField.ENTERPRISE_CUSTOM_FIELD59,
      TaskField.ENTERPRISE_CUSTOM_FIELD60,
      TaskField.ENTERPRISE_CUSTOM_FIELD61,
      TaskField.ENTERPRISE_CUSTOM_FIELD62,
      TaskField.ENTERPRISE_CUSTOM_FIELD63,
      TaskField.ENTERPRISE_CUSTOM_FIELD64,
      TaskField.ENTERPRISE_CUSTOM_FIELD65,
      TaskField.ENTERPRISE_CUSTOM_FIELD66,
      TaskField.ENTERPRISE_CUSTOM_FIELD67,
      TaskField.ENTERPRISE_CUSTOM_FIELD68,
      TaskField.ENTERPRISE_CUSTOM_FIELD69,
      TaskField.ENTERPRISE_CUSTOM_FIELD70,
      TaskField.ENTERPRISE_CUSTOM_FIELD71,
      TaskField.ENTERPRISE_CUSTOM_FIELD72,
      TaskField.ENTERPRISE_CUSTOM_FIELD73,
      TaskField.ENTERPRISE_CUSTOM_FIELD74,
      TaskField.ENTERPRISE_CUSTOM_FIELD75,
      TaskField.ENTERPRISE_CUSTOM_FIELD76,
      TaskField.ENTERPRISE_CUSTOM_FIELD77,
      TaskField.ENTERPRISE_CUSTOM_FIELD78,
      TaskField.ENTERPRISE_CUSTOM_FIELD79,
      TaskField.ENTERPRISE_CUSTOM_FIELD80,
      TaskField.ENTERPRISE_CUSTOM_FIELD81,
      TaskField.ENTERPRISE_CUSTOM_FIELD82,
      TaskField.ENTERPRISE_CUSTOM_FIELD83,
      TaskField.ENTERPRISE_CUSTOM_FIELD84,
      TaskField.ENTERPRISE_CUSTOM_FIELD85,
      TaskField.ENTERPRISE_CUSTOM_FIELD86,
      TaskField.ENTERPRISE_CUSTOM_FIELD87,
      TaskField.ENTERPRISE_CUSTOM_FIELD88,
      TaskField.ENTERPRISE_CUSTOM_FIELD89,
      TaskField.ENTERPRISE_CUSTOM_FIELD90,
      TaskField.ENTERPRISE_CUSTOM_FIELD91,
      TaskField.ENTERPRISE_CUSTOM_FIELD92,
      TaskField.ENTERPRISE_CUSTOM_FIELD93,
      TaskField.ENTERPRISE_CUSTOM_FIELD94,
      TaskField.ENTERPRISE_CUSTOM_FIELD95,
      TaskField.ENTERPRISE_CUSTOM_FIELD96,
      TaskField.ENTERPRISE_CUSTOM_FIELD97,
      TaskField.ENTERPRISE_CUSTOM_FIELD98,
      TaskField.ENTERPRISE_CUSTOM_FIELD99,
      TaskField.ENTERPRISE_CUSTOM_FIELD100,
      TaskField.ENTERPRISE_CUSTOM_FIELD101,
      TaskField.ENTERPRISE_CUSTOM_FIELD102,
      TaskField.ENTERPRISE_CUSTOM_FIELD103,
      TaskField.ENTERPRISE_CUSTOM_FIELD104,
      TaskField.ENTERPRISE_CUSTOM_FIELD105,
      TaskField.ENTERPRISE_CUSTOM_FIELD106,
      TaskField.ENTERPRISE_CUSTOM_FIELD107,
      TaskField.ENTERPRISE_CUSTOM_FIELD108,
      TaskField.ENTERPRISE_CUSTOM_FIELD109,
      TaskField.ENTERPRISE_CUSTOM_FIELD110,
      TaskField.ENTERPRISE_CUSTOM_FIELD111,
      TaskField.ENTERPRISE_CUSTOM_FIELD112,
      TaskField.ENTERPRISE_CUSTOM_FIELD113,
      TaskField.ENTERPRISE_CUSTOM_FIELD114,
      TaskField.ENTERPRISE_CUSTOM_FIELD115,
      TaskField.ENTERPRISE_CUSTOM_FIELD116,
      TaskField.ENTERPRISE_CUSTOM_FIELD117,
      TaskField.ENTERPRISE_CUSTOM_FIELD118,
      TaskField.ENTERPRISE_CUSTOM_FIELD119,
      TaskField.ENTERPRISE_CUSTOM_FIELD120,
      TaskField.ENTERPRISE_CUSTOM_FIELD121,
      TaskField.ENTERPRISE_CUSTOM_FIELD122,
      TaskField.ENTERPRISE_CUSTOM_FIELD123,
      TaskField.ENTERPRISE_CUSTOM_FIELD124,
      TaskField.ENTERPRISE_CUSTOM_FIELD125,
      TaskField.ENTERPRISE_CUSTOM_FIELD126,
      TaskField.ENTERPRISE_CUSTOM_FIELD127,
      TaskField.ENTERPRISE_CUSTOM_FIELD128,
      TaskField.ENTERPRISE_CUSTOM_FIELD129,
      TaskField.ENTERPRISE_CUSTOM_FIELD130,
      TaskField.ENTERPRISE_CUSTOM_FIELD131,
      TaskField.ENTERPRISE_CUSTOM_FIELD132,
      TaskField.ENTERPRISE_CUSTOM_FIELD133,
      TaskField.ENTERPRISE_CUSTOM_FIELD134,
      TaskField.ENTERPRISE_CUSTOM_FIELD135,
      TaskField.ENTERPRISE_CUSTOM_FIELD136,
      TaskField.ENTERPRISE_CUSTOM_FIELD137,
      TaskField.ENTERPRISE_CUSTOM_FIELD138,
      TaskField.ENTERPRISE_CUSTOM_FIELD139,
      TaskField.ENTERPRISE_CUSTOM_FIELD140,
      TaskField.ENTERPRISE_CUSTOM_FIELD141,
      TaskField.ENTERPRISE_CUSTOM_FIELD142,
      TaskField.ENTERPRISE_CUSTOM_FIELD143,
      TaskField.ENTERPRISE_CUSTOM_FIELD144,
      TaskField.ENTERPRISE_CUSTOM_FIELD145,
      TaskField.ENTERPRISE_CUSTOM_FIELD146,
      TaskField.ENTERPRISE_CUSTOM_FIELD147,
      TaskField.ENTERPRISE_CUSTOM_FIELD148,
      TaskField.ENTERPRISE_CUSTOM_FIELD149,
      TaskField.ENTERPRISE_CUSTOM_FIELD150,
      TaskField.ENTERPRISE_CUSTOM_FIELD151,
      TaskField.ENTERPRISE_CUSTOM_FIELD152,
      TaskField.ENTERPRISE_CUSTOM_FIELD153,
      TaskField.ENTERPRISE_CUSTOM_FIELD154,
      TaskField.ENTERPRISE_CUSTOM_FIELD155,
      TaskField.ENTERPRISE_CUSTOM_FIELD156,
      TaskField.ENTERPRISE_CUSTOM_FIELD157,
      TaskField.ENTERPRISE_CUSTOM_FIELD158,
      TaskField.ENTERPRISE_CUSTOM_FIELD159,
      TaskField.ENTERPRISE_CUSTOM_FIELD160,
      TaskField.ENTERPRISE_CUSTOM_FIELD161,
      TaskField.ENTERPRISE_CUSTOM_FIELD162,
      TaskField.ENTERPRISE_CUSTOM_FIELD163,
      TaskField.ENTERPRISE_CUSTOM_FIELD164,
      TaskField.ENTERPRISE_CUSTOM_FIELD165,
      TaskField.ENTERPRISE_CUSTOM_FIELD166,
      TaskField.ENTERPRISE_CUSTOM_FIELD167,
      TaskField.ENTERPRISE_CUSTOM_FIELD168,
      TaskField.ENTERPRISE_CUSTOM_FIELD169,
      TaskField.ENTERPRISE_CUSTOM_FIELD170,
      TaskField.ENTERPRISE_CUSTOM_FIELD171,
      TaskField.ENTERPRISE_CUSTOM_FIELD172,
      TaskField.ENTERPRISE_CUSTOM_FIELD173,
      TaskField.ENTERPRISE_CUSTOM_FIELD174,
      TaskField.ENTERPRISE_CUSTOM_FIELD175,
      TaskField.ENTERPRISE_CUSTOM_FIELD176,
      TaskField.ENTERPRISE_CUSTOM_FIELD177,
      TaskField.ENTERPRISE_CUSTOM_FIELD178,
      TaskField.ENTERPRISE_CUSTOM_FIELD179,
      TaskField.ENTERPRISE_CUSTOM_FIELD180,
      TaskField.ENTERPRISE_CUSTOM_FIELD181,
      TaskField.ENTERPRISE_CUSTOM_FIELD182,
      TaskField.ENTERPRISE_CUSTOM_FIELD183,
      TaskField.ENTERPRISE_CUSTOM_FIELD184,
      TaskField.ENTERPRISE_CUSTOM_FIELD185,
      TaskField.ENTERPRISE_CUSTOM_FIELD186,
      TaskField.ENTERPRISE_CUSTOM_FIELD187,
      TaskField.ENTERPRISE_CUSTOM_FIELD188,
      TaskField.ENTERPRISE_CUSTOM_FIELD189,
      TaskField.ENTERPRISE_CUSTOM_FIELD190,
      TaskField.ENTERPRISE_CUSTOM_FIELD191,
      TaskField.ENTERPRISE_CUSTOM_FIELD192,
      TaskField.ENTERPRISE_CUSTOM_FIELD193,
      TaskField.ENTERPRISE_CUSTOM_FIELD194,
      TaskField.ENTERPRISE_CUSTOM_FIELD195,
      TaskField.ENTERPRISE_CUSTOM_FIELD196,
      TaskField.ENTERPRISE_CUSTOM_FIELD197,
      TaskField.ENTERPRISE_CUSTOM_FIELD198,
      TaskField.ENTERPRISE_CUSTOM_FIELD199,
      TaskField.ENTERPRISE_CUSTOM_FIELD200
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

   public static final List<TaskField> EXTENDED_FIELDS = new ArrayList<>();
   static
   {
      EXTENDED_FIELDS.addAll(Arrays.asList(TaskFieldLists.CUSTOM_TEXT));
      EXTENDED_FIELDS.addAll(Arrays.asList(TaskFieldLists.CUSTOM_START));
      EXTENDED_FIELDS.addAll(Arrays.asList(TaskFieldLists.CUSTOM_FINISH));
      EXTENDED_FIELDS.addAll(Arrays.asList(TaskFieldLists.CUSTOM_COST));
      EXTENDED_FIELDS.addAll(Arrays.asList(TaskFieldLists.CUSTOM_DATE));
      EXTENDED_FIELDS.addAll(Arrays.asList(TaskFieldLists.CUSTOM_FLAG));
      EXTENDED_FIELDS.addAll(Arrays.asList(TaskFieldLists.CUSTOM_NUMBER));
      EXTENDED_FIELDS.addAll(Arrays.asList(TaskFieldLists.CUSTOM_DURATION));
      EXTENDED_FIELDS.addAll(Arrays.asList(TaskFieldLists.ENTERPRISE_COST));
      EXTENDED_FIELDS.addAll(Arrays.asList(TaskFieldLists.ENTERPRISE_DATE));
      EXTENDED_FIELDS.addAll(Arrays.asList(TaskFieldLists.ENTERPRISE_DURATION));
      EXTENDED_FIELDS.addAll(Arrays.asList(TaskFieldLists.ENTERPRISE_FLAG));
      EXTENDED_FIELDS.addAll(Arrays.asList(TaskFieldLists.ENTERPRISE_NUMBER));
      EXTENDED_FIELDS.addAll(Arrays.asList(TaskFieldLists.ENTERPRISE_TEXT));
   }
}
