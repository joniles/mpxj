/*
 * file:       MPPAssignmentField.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2011
 * date:       14/04/2011
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

import java.util.Arrays;

import org.mpxj.AssignmentField;
import org.mpxj.DataType;
import org.mpxj.FieldType;
import org.mpxj.FieldTypeClass;
import org.mpxj.ProjectFile;
import org.mpxj.UserDefinedField;

/**
 * Utility class used to map between the integer values held in MS Project
 * to represent an assignment field, and the enumerated type used to represent
 * assignment fields in MPXJ.
 */
public final class MPPAssignmentField
{
   /**
    * Retrieve an instance of the AssignmentField class based on the data read from an
    * MS Project file.
    *
    * @param project parent project
    * @param value value from an MS Project file
    * @return AssignmentField instance
    */
   public static FieldType getInstance(ProjectFile project, int value)
   {
      return getInstance(project, value, DataType.CUSTOM);
   }

   /**
    * Retrieve an instance of the AssignmentField class based on the data read from an
    * MS Project file.
    *
    * @param project parent project
    * @param value value from an MS Project file
    * @param customFieldDataType custom field data type
    * @return AssignmentField instance
    */
   public static FieldType getInstance(ProjectFile project, int value, DataType customFieldDataType)
   {
      // The 0x4000 prefix appears to be specific to resource assignments - but don't appear to carry useful information
      if ((value & 0x8000) != 0)
      {
         return project.getUserDefinedFields().getOrCreateAssignmentField(Integer.valueOf(value), (k) -> {
            int id = (k.intValue() & 0xFFF) + 1;
            return new UserDefinedField.Builder(project)
               .uniqueID(Integer.valueOf(ASSIGNMENT_FIELD_BASE + k.intValue()))
               .internalName("ENTERPRISE_CUSTOM_FIELD" + id)
               .externalName("Enterprise Custom Field " + id)
               .fieldTypeClass(FieldTypeClass.ASSIGNMENT)
               .dataType(customFieldDataType).build();
         });
      }

      FieldType result = null;
      if (value >= 0 && value < FIELD_ARRAY.length)
      {
         result = FIELD_ARRAY[value];
      }

      return result;
   }

   /**
    * Retrieve the ID of a field, as used by MS Project.
    *
    * @param value field instance
    * @return field ID
    */
   public static int getID(FieldType value)
   {
      int result;
      if (value instanceof UserDefinedField)
      {
         result = value.getValue();
      }
      else
      {
         result = ID_ARRAY[value.getValue()];
      }
      return result;
   }

   public static final int MAX_VALUE = 715;

   private static final AssignmentField[] FIELD_ARRAY = new AssignmentField[MAX_VALUE];

   static
   {
      FIELD_ARRAY[0] = AssignmentField.UNIQUE_ID;
      FIELD_ARRAY[1] = AssignmentField.TASK_UNIQUE_ID;
      FIELD_ARRAY[2] = AssignmentField.RESOURCE_UNIQUE_ID;
      FIELD_ARRAY[3] = AssignmentField.TASK_ID;
      FIELD_ARRAY[4] = AssignmentField.RESOURCE_ID;
      FIELD_ARRAY[5] = AssignmentField.TASK_NAME;
      FIELD_ARRAY[6] = AssignmentField.RESOURCE_NAME;
      FIELD_ARRAY[7] = AssignmentField.ASSIGNMENT_UNITS;
      FIELD_ARRAY[8] = AssignmentField.WORK;
      FIELD_ARRAY[9] = AssignmentField.OVERTIME_WORK;
      FIELD_ARRAY[10] = AssignmentField.ACTUAL_WORK;
      FIELD_ARRAY[11] = AssignmentField.REGULAR_WORK;
      FIELD_ARRAY[12] = AssignmentField.REMAINING_WORK;
      FIELD_ARRAY[13] = AssignmentField.ACTUAL_OVERTIME_WORK;
      FIELD_ARRAY[14] = AssignmentField.REMAINING_OVERTIME_WORK;
      FIELD_ARRAY[16] = AssignmentField.BASELINE_WORK;
      FIELD_ARRAY[19] = AssignmentField.PEAK;
      FIELD_ARRAY[20] = AssignmentField.START;
      FIELD_ARRAY[21] = AssignmentField.FINISH;
      FIELD_ARRAY[22] = AssignmentField.ACTUAL_START;
      FIELD_ARRAY[23] = AssignmentField.ACTUAL_FINISH;
      FIELD_ARRAY[24] = AssignmentField.RESUME;
      FIELD_ARRAY[25] = AssignmentField.ASSIGNMENT_DELAY;
      FIELD_ARRAY[26] = AssignmentField.COST;
      FIELD_ARRAY[27] = AssignmentField.OVERTIME_COST;
      FIELD_ARRAY[28] = AssignmentField.ACTUAL_COST;
      FIELD_ARRAY[29] = AssignmentField.REMAINING_COST;
      FIELD_ARRAY[30] = AssignmentField.ACTUAL_OVERTIME_COST;
      FIELD_ARRAY[31] = AssignmentField.REMAINING_OVERTIME_COST;
      FIELD_ARRAY[32] = AssignmentField.BASELINE_COST;
      FIELD_ARRAY[34] = AssignmentField.BCWS;
      FIELD_ARRAY[35] = AssignmentField.BCWP;
      FIELD_ARRAY[36] = AssignmentField.ACWP;
      FIELD_ARRAY[37] = AssignmentField.SV;
      FIELD_ARRAY[38] = AssignmentField.COST_VARIANCE;
      FIELD_ARRAY[39] = AssignmentField.WORK_CONTOUR;
      FIELD_ARRAY[43] = AssignmentField.PERCENT_WORK_COMPLETE;
      FIELD_ARRAY[44] = AssignmentField.PROJECT;
      FIELD_ARRAY[49] = AssignmentField.TIMEPHASED_WORK;
      FIELD_ARRAY[50] = AssignmentField.TIMEPHASED_ACTUAL_WORK;
      FIELD_ARRAY[51] = AssignmentField.TIMEPHASED_ACTUAL_OVERTIME_WORK;
      FIELD_ARRAY[52] = AssignmentField.TIMEPHASED_BASELINE_WORK;
      FIELD_ARRAY[53] = AssignmentField.TIMEPHASED_BASELINE_COST;
      FIELD_ARRAY[55] = AssignmentField.LEVELING_DELAY_UNITS;
      FIELD_ARRAY[71] = AssignmentField.NOTES;
      FIELD_ARRAY[72] = AssignmentField.CONFIRMED;
      FIELD_ARRAY[73] = AssignmentField.RESPONSE_PENDING;
      FIELD_ARRAY[74] = AssignmentField.UPDATE_NEEDED;
      FIELD_ARRAY[75] = AssignmentField.TEAM_STATUS_PENDING;
      FIELD_ARRAY[80] = AssignmentField.COST_RATE_TABLE;
      FIELD_ARRAY[88] = AssignmentField.TEXT1;
      FIELD_ARRAY[89] = AssignmentField.TEXT2;
      FIELD_ARRAY[90] = AssignmentField.TEXT3;
      FIELD_ARRAY[91] = AssignmentField.TEXT4;
      FIELD_ARRAY[92] = AssignmentField.TEXT5;
      FIELD_ARRAY[93] = AssignmentField.TEXT6;
      FIELD_ARRAY[94] = AssignmentField.TEXT7;
      FIELD_ARRAY[95] = AssignmentField.TEXT8;
      FIELD_ARRAY[96] = AssignmentField.TEXT9;
      FIELD_ARRAY[97] = AssignmentField.TEXT10;
      FIELD_ARRAY[98] = AssignmentField.START1;
      FIELD_ARRAY[99] = AssignmentField.START2;
      FIELD_ARRAY[100] = AssignmentField.START3;
      FIELD_ARRAY[101] = AssignmentField.START4;
      FIELD_ARRAY[102] = AssignmentField.START5;
      FIELD_ARRAY[103] = AssignmentField.FINISH1;
      FIELD_ARRAY[104] = AssignmentField.FINISH2;
      FIELD_ARRAY[105] = AssignmentField.FINISH3;
      FIELD_ARRAY[106] = AssignmentField.FINISH4;
      FIELD_ARRAY[107] = AssignmentField.FINISH5;
      FIELD_ARRAY[108] = AssignmentField.NUMBER1;
      FIELD_ARRAY[109] = AssignmentField.NUMBER2;
      FIELD_ARRAY[110] = AssignmentField.NUMBER3;
      FIELD_ARRAY[111] = AssignmentField.NUMBER4;
      FIELD_ARRAY[112] = AssignmentField.NUMBER5;
      FIELD_ARRAY[113] = AssignmentField.DURATION1;
      FIELD_ARRAY[114] = AssignmentField.DURATION2;
      FIELD_ARRAY[115] = AssignmentField.DURATION3;
      FIELD_ARRAY[116] = AssignmentField.DURATION1_UNITS;
      FIELD_ARRAY[117] = AssignmentField.DURATION2_UNITS;
      FIELD_ARRAY[118] = AssignmentField.DURATION3_UNITS;
      FIELD_ARRAY[119] = AssignmentField.COST1;
      FIELD_ARRAY[120] = AssignmentField.COST2;
      FIELD_ARRAY[121] = AssignmentField.COST3;
      FIELD_ARRAY[122] = AssignmentField.FLAG10;
      FIELD_ARRAY[123] = AssignmentField.FLAG1;
      FIELD_ARRAY[124] = AssignmentField.FLAG2;
      FIELD_ARRAY[125] = AssignmentField.FLAG3;
      FIELD_ARRAY[126] = AssignmentField.FLAG4;
      FIELD_ARRAY[127] = AssignmentField.FLAG5;
      FIELD_ARRAY[128] = AssignmentField.FLAG6;
      FIELD_ARRAY[129] = AssignmentField.FLAG7;
      FIELD_ARRAY[130] = AssignmentField.FLAG8;
      FIELD_ARRAY[131] = AssignmentField.FLAG9;
      FIELD_ARRAY[132] = AssignmentField.LINKED_FIELDS;
      FIELD_ARRAY[135] = AssignmentField.OVERALLOCATED;
      FIELD_ARRAY[142] = AssignmentField.TASK_SUMMARY_NAME;
      FIELD_ARRAY[145] = AssignmentField.LEVELING_DELAY;
      FIELD_ARRAY[146] = AssignmentField.BASELINE_START;
      FIELD_ARRAY[147] = AssignmentField.BASELINE_FINISH;
      FIELD_ARRAY[150] = AssignmentField.HYPERLINK_DATA;
      FIELD_ARRAY[152] = AssignmentField.HYPERLINK;
      FIELD_ARRAY[153] = AssignmentField.HYPERLINK_ADDRESS;
      FIELD_ARRAY[154] = AssignmentField.HYPERLINK_SUBADDRESS;
      FIELD_ARRAY[155] = AssignmentField.HYPERLINK_HREF;
      FIELD_ARRAY[159] = AssignmentField.COST4;
      FIELD_ARRAY[160] = AssignmentField.COST5;
      FIELD_ARRAY[161] = AssignmentField.COST6;
      FIELD_ARRAY[162] = AssignmentField.COST7;
      FIELD_ARRAY[163] = AssignmentField.COST8;
      FIELD_ARRAY[164] = AssignmentField.COST9;
      FIELD_ARRAY[165] = AssignmentField.COST10;
      FIELD_ARRAY[166] = AssignmentField.DATE1;
      FIELD_ARRAY[167] = AssignmentField.DATE2;
      FIELD_ARRAY[168] = AssignmentField.DATE3;
      FIELD_ARRAY[169] = AssignmentField.DATE4;
      FIELD_ARRAY[170] = AssignmentField.DATE5;
      FIELD_ARRAY[171] = AssignmentField.DATE6;
      FIELD_ARRAY[172] = AssignmentField.DATE7;
      FIELD_ARRAY[173] = AssignmentField.DATE8;
      FIELD_ARRAY[174] = AssignmentField.DATE9;
      FIELD_ARRAY[175] = AssignmentField.DATE10;
      FIELD_ARRAY[176] = AssignmentField.DURATION4;
      FIELD_ARRAY[177] = AssignmentField.DURATION5;
      FIELD_ARRAY[178] = AssignmentField.DURATION6;
      FIELD_ARRAY[179] = AssignmentField.DURATION7;
      FIELD_ARRAY[180] = AssignmentField.DURATION8;
      FIELD_ARRAY[181] = AssignmentField.DURATION9;
      FIELD_ARRAY[182] = AssignmentField.DURATION10;
      FIELD_ARRAY[183] = AssignmentField.FINISH6;
      FIELD_ARRAY[184] = AssignmentField.FINISH7;
      FIELD_ARRAY[185] = AssignmentField.FINISH8;
      FIELD_ARRAY[186] = AssignmentField.FINISH9;
      FIELD_ARRAY[187] = AssignmentField.FINISH10;
      FIELD_ARRAY[188] = AssignmentField.FLAG11;
      FIELD_ARRAY[189] = AssignmentField.FLAG12;
      FIELD_ARRAY[190] = AssignmentField.FLAG13;
      FIELD_ARRAY[191] = AssignmentField.FLAG14;
      FIELD_ARRAY[192] = AssignmentField.FLAG15;
      FIELD_ARRAY[193] = AssignmentField.FLAG16;
      FIELD_ARRAY[194] = AssignmentField.FLAG17;
      FIELD_ARRAY[195] = AssignmentField.FLAG18;
      FIELD_ARRAY[196] = AssignmentField.FLAG19;
      FIELD_ARRAY[197] = AssignmentField.FLAG20;
      FIELD_ARRAY[198] = AssignmentField.NUMBER6;
      FIELD_ARRAY[199] = AssignmentField.NUMBER7;
      FIELD_ARRAY[200] = AssignmentField.NUMBER8;
      FIELD_ARRAY[201] = AssignmentField.NUMBER9;
      FIELD_ARRAY[202] = AssignmentField.NUMBER10;
      FIELD_ARRAY[203] = AssignmentField.NUMBER11;
      FIELD_ARRAY[204] = AssignmentField.NUMBER12;
      FIELD_ARRAY[205] = AssignmentField.NUMBER13;
      FIELD_ARRAY[206] = AssignmentField.NUMBER14;
      FIELD_ARRAY[207] = AssignmentField.NUMBER15;
      FIELD_ARRAY[208] = AssignmentField.NUMBER16;
      FIELD_ARRAY[209] = AssignmentField.NUMBER17;
      FIELD_ARRAY[210] = AssignmentField.NUMBER18;
      FIELD_ARRAY[211] = AssignmentField.NUMBER19;
      FIELD_ARRAY[212] = AssignmentField.NUMBER20;
      FIELD_ARRAY[213] = AssignmentField.START6;
      FIELD_ARRAY[214] = AssignmentField.START7;
      FIELD_ARRAY[215] = AssignmentField.START8;
      FIELD_ARRAY[216] = AssignmentField.START9;
      FIELD_ARRAY[217] = AssignmentField.START10;
      FIELD_ARRAY[218] = AssignmentField.TEXT11;
      FIELD_ARRAY[219] = AssignmentField.TEXT12;
      FIELD_ARRAY[220] = AssignmentField.TEXT13;
      FIELD_ARRAY[221] = AssignmentField.TEXT14;
      FIELD_ARRAY[222] = AssignmentField.TEXT15;
      FIELD_ARRAY[223] = AssignmentField.TEXT16;
      FIELD_ARRAY[224] = AssignmentField.TEXT17;
      FIELD_ARRAY[225] = AssignmentField.TEXT18;
      FIELD_ARRAY[226] = AssignmentField.TEXT19;
      FIELD_ARRAY[227] = AssignmentField.TEXT20;
      FIELD_ARRAY[228] = AssignmentField.TEXT21;
      FIELD_ARRAY[229] = AssignmentField.TEXT22;
      FIELD_ARRAY[230] = AssignmentField.TEXT23;
      FIELD_ARRAY[231] = AssignmentField.TEXT24;
      FIELD_ARRAY[232] = AssignmentField.TEXT25;
      FIELD_ARRAY[233] = AssignmentField.TEXT26;
      FIELD_ARRAY[234] = AssignmentField.TEXT27;
      FIELD_ARRAY[235] = AssignmentField.TEXT28;
      FIELD_ARRAY[236] = AssignmentField.TEXT29;
      FIELD_ARRAY[237] = AssignmentField.TEXT30;
      FIELD_ARRAY[238] = AssignmentField.DURATION4_UNITS;
      FIELD_ARRAY[239] = AssignmentField.DURATION5_UNITS;
      FIELD_ARRAY[240] = AssignmentField.DURATION6_UNITS;
      FIELD_ARRAY[241] = AssignmentField.DURATION7_UNITS;
      FIELD_ARRAY[242] = AssignmentField.DURATION8_UNITS;
      FIELD_ARRAY[243] = AssignmentField.DURATION9_UNITS;
      FIELD_ARRAY[244] = AssignmentField.DURATION10_UNITS;
      FIELD_ARRAY[246] = AssignmentField.INDEX;
      FIELD_ARRAY[247] = AssignmentField.CV;
      FIELD_ARRAY[248] = AssignmentField.WORK_VARIANCE;
      FIELD_ARRAY[262] = AssignmentField.START_VARIANCE;
      FIELD_ARRAY[263] = AssignmentField.FINISH_VARIANCE;
      FIELD_ARRAY[264] = AssignmentField.STOP;
      FIELD_ARRAY[270] = AssignmentField.VARIABLE_RATE_UNITS;
      FIELD_ARRAY[271] = AssignmentField.VAC;
      FIELD_ARRAY[275] = AssignmentField.FIXED_MATERIAL_ASSIGNMENT;
      FIELD_ARRAY[276] = AssignmentField.RESOURCE_TYPE;
      FIELD_ARRAY[279] = AssignmentField.HYPERLINK_SCREEN_TIP;
      FIELD_ARRAY[286] = AssignmentField.WBS;
      FIELD_ARRAY[289] = AssignmentField.BASELINE1_WORK;
      FIELD_ARRAY[290] = AssignmentField.BASELINE1_COST;
      FIELD_ARRAY[291] = AssignmentField.TIMEPHASED_BASELINE1_WORK;
      FIELD_ARRAY[292] = AssignmentField.TIMEPHASED_BASELINE1_COST;
      FIELD_ARRAY[295] = AssignmentField.BASELINE1_START;
      FIELD_ARRAY[296] = AssignmentField.BASELINE1_FINISH;
      FIELD_ARRAY[298] = AssignmentField.BASELINE2_WORK;
      FIELD_ARRAY[299] = AssignmentField.BASELINE2_COST;
      FIELD_ARRAY[300] = AssignmentField.TIMEPHASED_BASELINE2_WORK;
      FIELD_ARRAY[301] = AssignmentField.TIMEPHASED_BASELINE2_COST;
      FIELD_ARRAY[304] = AssignmentField.BASELINE2_START;
      FIELD_ARRAY[305] = AssignmentField.BASELINE2_FINISH;
      FIELD_ARRAY[307] = AssignmentField.BASELINE3_WORK;
      FIELD_ARRAY[308] = AssignmentField.BASELINE3_COST;
      FIELD_ARRAY[309] = AssignmentField.TIMEPHASED_BASELINE3_WORK;
      FIELD_ARRAY[310] = AssignmentField.TIMEPHASED_BASELINE3_COST;
      FIELD_ARRAY[313] = AssignmentField.BASELINE3_START;
      FIELD_ARRAY[314] = AssignmentField.BASELINE3_FINISH;
      FIELD_ARRAY[316] = AssignmentField.BASELINE4_WORK;
      FIELD_ARRAY[317] = AssignmentField.BASELINE4_COST;
      FIELD_ARRAY[318] = AssignmentField.TIMEPHASED_BASELINE4_WORK;
      FIELD_ARRAY[319] = AssignmentField.TIMEPHASED_BASELINE4_COST;
      FIELD_ARRAY[322] = AssignmentField.BASELINE4_START;
      FIELD_ARRAY[323] = AssignmentField.BASELINE4_FINISH;
      FIELD_ARRAY[325] = AssignmentField.BASELINE5_WORK;
      FIELD_ARRAY[326] = AssignmentField.BASELINE5_COST;
      FIELD_ARRAY[327] = AssignmentField.TIMEPHASED_BASELINE5_WORK;
      FIELD_ARRAY[328] = AssignmentField.TIMEPHASED_BASELINE5_COST;
      FIELD_ARRAY[331] = AssignmentField.BASELINE5_START;
      FIELD_ARRAY[332] = AssignmentField.BASELINE5_FINISH;
      FIELD_ARRAY[334] = AssignmentField.BASELINE6_WORK;
      FIELD_ARRAY[335] = AssignmentField.BASELINE6_COST;
      FIELD_ARRAY[336] = AssignmentField.TIMEPHASED_BASELINE6_WORK;
      FIELD_ARRAY[337] = AssignmentField.TIMEPHASED_BASELINE6_COST;
      FIELD_ARRAY[340] = AssignmentField.BASELINE6_START;
      FIELD_ARRAY[341] = AssignmentField.BASELINE6_FINISH;
      FIELD_ARRAY[343] = AssignmentField.BASELINE7_WORK;
      FIELD_ARRAY[344] = AssignmentField.BASELINE7_COST;
      FIELD_ARRAY[345] = AssignmentField.TIMEPHASED_BASELINE7_WORK;
      FIELD_ARRAY[346] = AssignmentField.TIMEPHASED_BASELINE7_COST;
      FIELD_ARRAY[349] = AssignmentField.BASELINE7_START;
      FIELD_ARRAY[350] = AssignmentField.BASELINE7_FINISH;
      FIELD_ARRAY[352] = AssignmentField.BASELINE8_WORK;
      FIELD_ARRAY[353] = AssignmentField.BASELINE8_COST;
      FIELD_ARRAY[354] = AssignmentField.TIMEPHASED_BASELINE8_WORK;
      FIELD_ARRAY[355] = AssignmentField.TIMEPHASED_BASELINE8_COST;
      FIELD_ARRAY[358] = AssignmentField.BASELINE8_START;
      FIELD_ARRAY[359] = AssignmentField.BASELINE8_FINISH;
      FIELD_ARRAY[361] = AssignmentField.BASELINE9_WORK;
      FIELD_ARRAY[362] = AssignmentField.BASELINE9_COST;
      FIELD_ARRAY[363] = AssignmentField.TIMEPHASED_BASELINE9_WORK;
      FIELD_ARRAY[364] = AssignmentField.TIMEPHASED_BASELINE9_COST;
      FIELD_ARRAY[367] = AssignmentField.BASELINE9_START;
      FIELD_ARRAY[368] = AssignmentField.BASELINE9_FINISH;
      FIELD_ARRAY[370] = AssignmentField.BASELINE10_WORK;
      FIELD_ARRAY[371] = AssignmentField.BASELINE10_COST;
      FIELD_ARRAY[372] = AssignmentField.TIMEPHASED_BASELINE10_WORK;
      FIELD_ARRAY[373] = AssignmentField.TIMEPHASED_BASELINE10_COST;
      FIELD_ARRAY[376] = AssignmentField.BASELINE10_START;
      FIELD_ARRAY[377] = AssignmentField.BASELINE10_FINISH;
      FIELD_ARRAY[379] = AssignmentField.TASK_OUTLINE_NUMBER;
      FIELD_ARRAY[381] = AssignmentField.ENTERPRISE_COST1;
      FIELD_ARRAY[382] = AssignmentField.ENTERPRISE_COST2;
      FIELD_ARRAY[383] = AssignmentField.ENTERPRISE_COST3;
      FIELD_ARRAY[384] = AssignmentField.ENTERPRISE_COST4;
      FIELD_ARRAY[385] = AssignmentField.ENTERPRISE_COST5;
      FIELD_ARRAY[386] = AssignmentField.ENTERPRISE_COST6;
      FIELD_ARRAY[387] = AssignmentField.ENTERPRISE_COST7;
      FIELD_ARRAY[388] = AssignmentField.ENTERPRISE_COST8;
      FIELD_ARRAY[389] = AssignmentField.ENTERPRISE_COST9;
      FIELD_ARRAY[390] = AssignmentField.ENTERPRISE_COST10;
      FIELD_ARRAY[391] = AssignmentField.ENTERPRISE_DATE1;
      FIELD_ARRAY[392] = AssignmentField.ENTERPRISE_DATE2;
      FIELD_ARRAY[393] = AssignmentField.ENTERPRISE_DATE3;
      FIELD_ARRAY[394] = AssignmentField.ENTERPRISE_DATE4;
      FIELD_ARRAY[395] = AssignmentField.ENTERPRISE_DATE5;
      FIELD_ARRAY[396] = AssignmentField.ENTERPRISE_DATE6;
      FIELD_ARRAY[397] = AssignmentField.ENTERPRISE_DATE7;
      FIELD_ARRAY[398] = AssignmentField.ENTERPRISE_DATE8;
      FIELD_ARRAY[399] = AssignmentField.ENTERPRISE_DATE9;
      FIELD_ARRAY[400] = AssignmentField.ENTERPRISE_DATE10;
      FIELD_ARRAY[401] = AssignmentField.ENTERPRISE_DATE11;
      FIELD_ARRAY[402] = AssignmentField.ENTERPRISE_DATE12;
      FIELD_ARRAY[403] = AssignmentField.ENTERPRISE_DATE13;
      FIELD_ARRAY[404] = AssignmentField.ENTERPRISE_DATE14;
      FIELD_ARRAY[405] = AssignmentField.ENTERPRISE_DATE15;
      FIELD_ARRAY[406] = AssignmentField.ENTERPRISE_DATE16;
      FIELD_ARRAY[407] = AssignmentField.ENTERPRISE_DATE17;
      FIELD_ARRAY[408] = AssignmentField.ENTERPRISE_DATE18;
      FIELD_ARRAY[409] = AssignmentField.ENTERPRISE_DATE19;
      FIELD_ARRAY[410] = AssignmentField.ENTERPRISE_DATE20;
      FIELD_ARRAY[411] = AssignmentField.ENTERPRISE_DATE21;
      FIELD_ARRAY[412] = AssignmentField.ENTERPRISE_DATE22;
      FIELD_ARRAY[413] = AssignmentField.ENTERPRISE_DATE23;
      FIELD_ARRAY[414] = AssignmentField.ENTERPRISE_DATE24;
      FIELD_ARRAY[415] = AssignmentField.ENTERPRISE_DATE25;
      FIELD_ARRAY[416] = AssignmentField.ENTERPRISE_DATE26;
      FIELD_ARRAY[417] = AssignmentField.ENTERPRISE_DATE27;
      FIELD_ARRAY[418] = AssignmentField.ENTERPRISE_DATE28;
      FIELD_ARRAY[419] = AssignmentField.ENTERPRISE_DATE29;
      FIELD_ARRAY[420] = AssignmentField.ENTERPRISE_DATE30;
      FIELD_ARRAY[421] = AssignmentField.ENTERPRISE_DURATION1;
      FIELD_ARRAY[422] = AssignmentField.ENTERPRISE_DURATION2;
      FIELD_ARRAY[423] = AssignmentField.ENTERPRISE_DURATION3;
      FIELD_ARRAY[424] = AssignmentField.ENTERPRISE_DURATION4;
      FIELD_ARRAY[425] = AssignmentField.ENTERPRISE_DURATION5;
      FIELD_ARRAY[426] = AssignmentField.ENTERPRISE_DURATION6;
      FIELD_ARRAY[427] = AssignmentField.ENTERPRISE_DURATION7;
      FIELD_ARRAY[428] = AssignmentField.ENTERPRISE_DURATION8;
      FIELD_ARRAY[429] = AssignmentField.ENTERPRISE_DURATION9;
      FIELD_ARRAY[430] = AssignmentField.ENTERPRISE_DURATION10;
      FIELD_ARRAY[441] = AssignmentField.ENTERPRISE_FLAG1;
      FIELD_ARRAY[442] = AssignmentField.ENTERPRISE_FLAG2;
      FIELD_ARRAY[443] = AssignmentField.ENTERPRISE_FLAG3;
      FIELD_ARRAY[444] = AssignmentField.ENTERPRISE_FLAG4;
      FIELD_ARRAY[445] = AssignmentField.ENTERPRISE_FLAG5;
      FIELD_ARRAY[446] = AssignmentField.ENTERPRISE_FLAG6;
      FIELD_ARRAY[447] = AssignmentField.ENTERPRISE_FLAG7;
      FIELD_ARRAY[448] = AssignmentField.ENTERPRISE_FLAG8;
      FIELD_ARRAY[449] = AssignmentField.ENTERPRISE_FLAG9;
      FIELD_ARRAY[450] = AssignmentField.ENTERPRISE_FLAG10;
      FIELD_ARRAY[451] = AssignmentField.ENTERPRISE_FLAG11;
      FIELD_ARRAY[452] = AssignmentField.ENTERPRISE_FLAG12;
      FIELD_ARRAY[453] = AssignmentField.ENTERPRISE_FLAG13;
      FIELD_ARRAY[454] = AssignmentField.ENTERPRISE_FLAG14;
      FIELD_ARRAY[455] = AssignmentField.ENTERPRISE_FLAG15;
      FIELD_ARRAY[456] = AssignmentField.ENTERPRISE_FLAG16;
      FIELD_ARRAY[457] = AssignmentField.ENTERPRISE_FLAG17;
      FIELD_ARRAY[458] = AssignmentField.ENTERPRISE_FLAG18;
      FIELD_ARRAY[459] = AssignmentField.ENTERPRISE_FLAG19;
      FIELD_ARRAY[460] = AssignmentField.ENTERPRISE_FLAG20;
      FIELD_ARRAY[461] = AssignmentField.ENTERPRISE_NUMBER1;
      FIELD_ARRAY[462] = AssignmentField.ENTERPRISE_NUMBER2;
      FIELD_ARRAY[463] = AssignmentField.ENTERPRISE_NUMBER3;
      FIELD_ARRAY[464] = AssignmentField.ENTERPRISE_NUMBER4;
      FIELD_ARRAY[465] = AssignmentField.ENTERPRISE_NUMBER5;
      FIELD_ARRAY[466] = AssignmentField.ENTERPRISE_NUMBER6;
      FIELD_ARRAY[467] = AssignmentField.ENTERPRISE_NUMBER7;
      FIELD_ARRAY[468] = AssignmentField.ENTERPRISE_NUMBER8;
      FIELD_ARRAY[469] = AssignmentField.ENTERPRISE_NUMBER9;
      FIELD_ARRAY[470] = AssignmentField.ENTERPRISE_NUMBER10;
      FIELD_ARRAY[471] = AssignmentField.ENTERPRISE_NUMBER11;
      FIELD_ARRAY[472] = AssignmentField.ENTERPRISE_NUMBER12;
      FIELD_ARRAY[473] = AssignmentField.ENTERPRISE_NUMBER13;
      FIELD_ARRAY[474] = AssignmentField.ENTERPRISE_NUMBER14;
      FIELD_ARRAY[475] = AssignmentField.ENTERPRISE_NUMBER15;
      FIELD_ARRAY[476] = AssignmentField.ENTERPRISE_NUMBER16;
      FIELD_ARRAY[477] = AssignmentField.ENTERPRISE_NUMBER17;
      FIELD_ARRAY[478] = AssignmentField.ENTERPRISE_NUMBER18;
      FIELD_ARRAY[479] = AssignmentField.ENTERPRISE_NUMBER19;
      FIELD_ARRAY[480] = AssignmentField.ENTERPRISE_NUMBER20;
      FIELD_ARRAY[481] = AssignmentField.ENTERPRISE_NUMBER21;
      FIELD_ARRAY[482] = AssignmentField.ENTERPRISE_NUMBER22;
      FIELD_ARRAY[483] = AssignmentField.ENTERPRISE_NUMBER23;
      FIELD_ARRAY[484] = AssignmentField.ENTERPRISE_NUMBER24;
      FIELD_ARRAY[485] = AssignmentField.ENTERPRISE_NUMBER25;
      FIELD_ARRAY[486] = AssignmentField.ENTERPRISE_NUMBER26;
      FIELD_ARRAY[487] = AssignmentField.ENTERPRISE_NUMBER27;
      FIELD_ARRAY[488] = AssignmentField.ENTERPRISE_NUMBER28;
      FIELD_ARRAY[489] = AssignmentField.ENTERPRISE_NUMBER29;
      FIELD_ARRAY[490] = AssignmentField.ENTERPRISE_NUMBER30;
      FIELD_ARRAY[491] = AssignmentField.ENTERPRISE_NUMBER31;
      FIELD_ARRAY[492] = AssignmentField.ENTERPRISE_NUMBER32;
      FIELD_ARRAY[493] = AssignmentField.ENTERPRISE_NUMBER33;
      FIELD_ARRAY[494] = AssignmentField.ENTERPRISE_NUMBER34;
      FIELD_ARRAY[495] = AssignmentField.ENTERPRISE_NUMBER35;
      FIELD_ARRAY[496] = AssignmentField.ENTERPRISE_NUMBER36;
      FIELD_ARRAY[497] = AssignmentField.ENTERPRISE_NUMBER37;
      FIELD_ARRAY[498] = AssignmentField.ENTERPRISE_NUMBER38;
      FIELD_ARRAY[499] = AssignmentField.ENTERPRISE_NUMBER39;
      FIELD_ARRAY[500] = AssignmentField.ENTERPRISE_NUMBER40;
      FIELD_ARRAY[501] = AssignmentField.ENTERPRISE_TEXT1;
      FIELD_ARRAY[502] = AssignmentField.ENTERPRISE_TEXT2;
      FIELD_ARRAY[503] = AssignmentField.ENTERPRISE_TEXT3;
      FIELD_ARRAY[504] = AssignmentField.ENTERPRISE_TEXT4;
      FIELD_ARRAY[505] = AssignmentField.ENTERPRISE_TEXT5;
      FIELD_ARRAY[506] = AssignmentField.ENTERPRISE_TEXT6;
      FIELD_ARRAY[507] = AssignmentField.ENTERPRISE_TEXT7;
      FIELD_ARRAY[508] = AssignmentField.ENTERPRISE_TEXT8;
      FIELD_ARRAY[509] = AssignmentField.ENTERPRISE_TEXT9;
      FIELD_ARRAY[510] = AssignmentField.ENTERPRISE_TEXT10;
      FIELD_ARRAY[511] = AssignmentField.ENTERPRISE_TEXT11;
      FIELD_ARRAY[512] = AssignmentField.ENTERPRISE_TEXT12;
      FIELD_ARRAY[513] = AssignmentField.ENTERPRISE_TEXT13;
      FIELD_ARRAY[514] = AssignmentField.ENTERPRISE_TEXT14;
      FIELD_ARRAY[515] = AssignmentField.ENTERPRISE_TEXT15;
      FIELD_ARRAY[516] = AssignmentField.ENTERPRISE_TEXT16;
      FIELD_ARRAY[517] = AssignmentField.ENTERPRISE_TEXT17;
      FIELD_ARRAY[518] = AssignmentField.ENTERPRISE_TEXT18;
      FIELD_ARRAY[519] = AssignmentField.ENTERPRISE_TEXT19;
      FIELD_ARRAY[520] = AssignmentField.ENTERPRISE_TEXT20;
      FIELD_ARRAY[521] = AssignmentField.ENTERPRISE_TEXT21;
      FIELD_ARRAY[522] = AssignmentField.ENTERPRISE_TEXT22;
      FIELD_ARRAY[523] = AssignmentField.ENTERPRISE_TEXT23;
      FIELD_ARRAY[524] = AssignmentField.ENTERPRISE_TEXT24;
      FIELD_ARRAY[525] = AssignmentField.ENTERPRISE_TEXT25;
      FIELD_ARRAY[526] = AssignmentField.ENTERPRISE_TEXT26;
      FIELD_ARRAY[527] = AssignmentField.ENTERPRISE_TEXT27;
      FIELD_ARRAY[528] = AssignmentField.ENTERPRISE_TEXT28;
      FIELD_ARRAY[529] = AssignmentField.ENTERPRISE_TEXT29;
      FIELD_ARRAY[530] = AssignmentField.ENTERPRISE_TEXT30;
      FIELD_ARRAY[531] = AssignmentField.ENTERPRISE_TEXT31;
      FIELD_ARRAY[532] = AssignmentField.ENTERPRISE_TEXT32;
      FIELD_ARRAY[533] = AssignmentField.ENTERPRISE_TEXT33;
      FIELD_ARRAY[534] = AssignmentField.ENTERPRISE_TEXT34;
      FIELD_ARRAY[535] = AssignmentField.ENTERPRISE_TEXT35;
      FIELD_ARRAY[536] = AssignmentField.ENTERPRISE_TEXT36;
      FIELD_ARRAY[537] = AssignmentField.ENTERPRISE_TEXT37;
      FIELD_ARRAY[538] = AssignmentField.ENTERPRISE_TEXT38;
      FIELD_ARRAY[539] = AssignmentField.ENTERPRISE_TEXT39;
      FIELD_ARRAY[540] = AssignmentField.ENTERPRISE_TEXT40;
      FIELD_ARRAY[545] = AssignmentField.ENTERPRISE_RESOURCE_OUTLINE_CODE1;
      FIELD_ARRAY[546] = AssignmentField.ENTERPRISE_RESOURCE_OUTLINE_CODE2;
      FIELD_ARRAY[547] = AssignmentField.ENTERPRISE_RESOURCE_OUTLINE_CODE3;
      FIELD_ARRAY[548] = AssignmentField.ENTERPRISE_RESOURCE_OUTLINE_CODE4;
      FIELD_ARRAY[549] = AssignmentField.ENTERPRISE_RESOURCE_OUTLINE_CODE5;
      FIELD_ARRAY[550] = AssignmentField.ENTERPRISE_RESOURCE_OUTLINE_CODE6;
      FIELD_ARRAY[551] = AssignmentField.ENTERPRISE_RESOURCE_OUTLINE_CODE7;
      FIELD_ARRAY[552] = AssignmentField.ENTERPRISE_RESOURCE_OUTLINE_CODE8;
      FIELD_ARRAY[553] = AssignmentField.ENTERPRISE_RESOURCE_OUTLINE_CODE9;
      FIELD_ARRAY[554] = AssignmentField.ENTERPRISE_RESOURCE_OUTLINE_CODE10;
      FIELD_ARRAY[555] = AssignmentField.ENTERPRISE_RESOURCE_OUTLINE_CODE11;
      FIELD_ARRAY[556] = AssignmentField.ENTERPRISE_RESOURCE_OUTLINE_CODE12;
      FIELD_ARRAY[557] = AssignmentField.ENTERPRISE_RESOURCE_OUTLINE_CODE13;
      FIELD_ARRAY[558] = AssignmentField.ENTERPRISE_RESOURCE_OUTLINE_CODE14;
      FIELD_ARRAY[559] = AssignmentField.ENTERPRISE_RESOURCE_OUTLINE_CODE15;
      FIELD_ARRAY[560] = AssignmentField.ENTERPRISE_RESOURCE_OUTLINE_CODE16;
      FIELD_ARRAY[561] = AssignmentField.ENTERPRISE_RESOURCE_OUTLINE_CODE17;
      FIELD_ARRAY[562] = AssignmentField.ENTERPRISE_RESOURCE_OUTLINE_CODE18;
      FIELD_ARRAY[563] = AssignmentField.ENTERPRISE_RESOURCE_OUTLINE_CODE19;
      FIELD_ARRAY[564] = AssignmentField.ENTERPRISE_RESOURCE_OUTLINE_CODE20;
      FIELD_ARRAY[565] = AssignmentField.ENTERPRISE_RESOURCE_OUTLINE_CODE21;
      FIELD_ARRAY[566] = AssignmentField.ENTERPRISE_RESOURCE_OUTLINE_CODE22;
      FIELD_ARRAY[567] = AssignmentField.ENTERPRISE_RESOURCE_OUTLINE_CODE23;
      FIELD_ARRAY[568] = AssignmentField.ENTERPRISE_RESOURCE_OUTLINE_CODE24;
      FIELD_ARRAY[569] = AssignmentField.ENTERPRISE_RESOURCE_OUTLINE_CODE25;
      FIELD_ARRAY[570] = AssignmentField.ENTERPRISE_RESOURCE_OUTLINE_CODE26;
      FIELD_ARRAY[571] = AssignmentField.ENTERPRISE_RESOURCE_OUTLINE_CODE27;
      FIELD_ARRAY[572] = AssignmentField.ENTERPRISE_RESOURCE_OUTLINE_CODE28;
      FIELD_ARRAY[573] = AssignmentField.ENTERPRISE_RESOURCE_OUTLINE_CODE29;
      FIELD_ARRAY[574] = AssignmentField.ENTERPRISE_RESOURCE_RBS;
      FIELD_ARRAY[605] = AssignmentField.RESOURCE_REQUEST_TYPE;
      FIELD_ARRAY[607] = AssignmentField.ENTERPRISE_TEAM_MEMBER;
      FIELD_ARRAY[610] = AssignmentField.ENTERPRISE_RESOURCE_MULTI_VALUE20;
      FIELD_ARRAY[612] = AssignmentField.ENTERPRISE_RESOURCE_MULTI_VALUE21;
      FIELD_ARRAY[614] = AssignmentField.ENTERPRISE_RESOURCE_MULTI_VALUE22;
      FIELD_ARRAY[616] = AssignmentField.ENTERPRISE_RESOURCE_MULTI_VALUE23;
      FIELD_ARRAY[618] = AssignmentField.ENTERPRISE_RESOURCE_MULTI_VALUE24;
      FIELD_ARRAY[620] = AssignmentField.ENTERPRISE_RESOURCE_MULTI_VALUE25;
      FIELD_ARRAY[622] = AssignmentField.ENTERPRISE_RESOURCE_MULTI_VALUE26;
      FIELD_ARRAY[624] = AssignmentField.ENTERPRISE_RESOURCE_MULTI_VALUE27;
      FIELD_ARRAY[626] = AssignmentField.ENTERPRISE_RESOURCE_MULTI_VALUE28;
      FIELD_ARRAY[628] = AssignmentField.ENTERPRISE_RESOURCE_MULTI_VALUE29;
      FIELD_ARRAY[630] = AssignmentField.ACTUAL_WORK_PROTECTED;
      FIELD_ARRAY[631] = AssignmentField.ACTUAL_OVERTIME_WORK_PROTECTED;
      FIELD_ARRAY[634] = AssignmentField.CREATED;
      FIELD_ARRAY[636] = AssignmentField.GUID;
      FIELD_ARRAY[637] = AssignmentField.ASSIGNMENT_TASK_GUID;
      FIELD_ARRAY[638] = AssignmentField.ASSIGNMENT_RESOURCE_GUID;
      FIELD_ARRAY[646] = AssignmentField.SUMMARY;
      FIELD_ARRAY[668] = AssignmentField.OWNER;
      FIELD_ARRAY[669] = AssignmentField.BUDGET_WORK;
      FIELD_ARRAY[670] = AssignmentField.BUDGET_COST;
      FIELD_ARRAY[673] = AssignmentField.BASELINE_BUDGET_WORK;
      FIELD_ARRAY[674] = AssignmentField.BASELINE_BUDGET_COST;
      FIELD_ARRAY[677] = AssignmentField.BASELINE1_BUDGET_WORK;
      FIELD_ARRAY[678] = AssignmentField.BASELINE1_BUDGET_COST;
      FIELD_ARRAY[681] = AssignmentField.BASELINE2_BUDGET_WORK;
      FIELD_ARRAY[682] = AssignmentField.BASELINE2_BUDGET_COST;
      FIELD_ARRAY[685] = AssignmentField.BASELINE3_BUDGET_WORK;
      FIELD_ARRAY[686] = AssignmentField.BASELINE3_BUDGET_COST;
      FIELD_ARRAY[689] = AssignmentField.BASELINE4_BUDGET_WORK;
      FIELD_ARRAY[690] = AssignmentField.BASELINE4_BUDGET_COST;
      FIELD_ARRAY[693] = AssignmentField.BASELINE5_BUDGET_WORK;
      FIELD_ARRAY[694] = AssignmentField.BASELINE5_BUDGET_COST;
      FIELD_ARRAY[697] = AssignmentField.BASELINE6_BUDGET_WORK;
      FIELD_ARRAY[698] = AssignmentField.BASELINE6_BUDGET_COST;
      FIELD_ARRAY[701] = AssignmentField.BASELINE7_BUDGET_WORK;
      FIELD_ARRAY[702] = AssignmentField.BASELINE7_BUDGET_COST;
      FIELD_ARRAY[705] = AssignmentField.BASELINE8_BUDGET_WORK;
      FIELD_ARRAY[706] = AssignmentField.BASELINE8_BUDGET_COST;
      FIELD_ARRAY[709] = AssignmentField.BASELINE9_BUDGET_WORK;
      FIELD_ARRAY[710] = AssignmentField.BASELINE9_BUDGET_COST;
      FIELD_ARRAY[713] = AssignmentField.BASELINE10_BUDGET_WORK;
      FIELD_ARRAY[714] = AssignmentField.BASELINE10_BUDGET_COST;
   }

   private static final int[] ID_ARRAY = new int[AssignmentField.MAX_VALUE];
   static
   {
      Arrays.fill(ID_ARRAY, -1);

      for (int loop = 0; loop < FIELD_ARRAY.length; loop++)
      {
         AssignmentField assignmentField = FIELD_ARRAY[loop];
         if (assignmentField != null)
         {
            ID_ARRAY[assignmentField.getValue()] = loop;
         }
      }
   }

   public static final int ASSIGNMENT_FIELD_BASE = 0x0F400000;
}
