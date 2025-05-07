/*
 * file:       MPPResourceField.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2005
 * date:       20/02/2006
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

import org.mpxj.DataType;
import org.mpxj.FieldType;
import org.mpxj.FieldTypeClass;
import org.mpxj.ProjectFile;
import org.mpxj.ResourceField;
import org.mpxj.UserDefinedField;

/**
 * Utility class used to map between the integer values held in MS Project
 * to represent a resource field, and the enumerated type used to represent
 * resource fields in MPXJ.
 */
public final class MPPResourceField
{
   /**
    * Retrieve an instance of the ResourceField class based on the data read from an
    * MS Project file.
    *
    * @param project parent project
    * @param value value from an MS Project file
    * @return ResourceField instance
    */
   public static FieldType getInstance(ProjectFile project, int value)
   {
      return getInstance(project, value, DataType.CUSTOM);
   }

   /**
    * Retrieve an instance of the ResourceField class based on the data read from an
    * MS Project file.
    *
    * @param project parent project
    * @param value value from an MS Project file
    * @param customFieldDataType custom field data type
    * @return ResourceField instance
    */
   public static FieldType getInstance(ProjectFile project, int value, DataType customFieldDataType)
   {
      if ((value & 0x8000) != 0)
      {
         return project.getUserDefinedFields().getOrCreateResourceField(Integer.valueOf(value), (k) -> {
            int id = (k.intValue() & 0xFFF) + 1;
            return new UserDefinedField.Builder(project)
               .uniqueID(Integer.valueOf(RESOURCE_FIELD_BASE + k.intValue()))
               .internalName("ENTERPRISE_CUSTOM_FIELD" + id)
               .externalName("Enterprise Custom Field " + id)
               .fieldTypeClass(FieldTypeClass.RESOURCE)
               .dataType(customFieldDataType)
               .build();
         });
      }

      ResourceField result = null;
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

   public static final int MAX_VALUE = 863;

   private static final ResourceField[] FIELD_ARRAY = new ResourceField[MAX_VALUE];

   static
   {
      FIELD_ARRAY[0] = ResourceField.ID;
      FIELD_ARRAY[1] = ResourceField.NAME;
      FIELD_ARRAY[2] = ResourceField.INITIALS;
      FIELD_ARRAY[3] = ResourceField.GROUP;
      FIELD_ARRAY[4] = ResourceField.MAX_UNITS;
      FIELD_ARRAY[5] = ResourceField.BASE_CALENDAR;
      FIELD_ARRAY[6] = ResourceField.STANDARD_RATE;
      FIELD_ARRAY[7] = ResourceField.OVERTIME_RATE;
      FIELD_ARRAY[8] = ResourceField.TEXT1;
      FIELD_ARRAY[9] = ResourceField.TEXT2;
      FIELD_ARRAY[10] = ResourceField.CODE;
      FIELD_ARRAY[11] = ResourceField.ACTUAL_COST;
      FIELD_ARRAY[12] = ResourceField.COST;
      FIELD_ARRAY[13] = ResourceField.WORK;
      FIELD_ARRAY[14] = ResourceField.ACTUAL_WORK;
      FIELD_ARRAY[15] = ResourceField.BASELINE_WORK;
      FIELD_ARRAY[16] = ResourceField.OVERTIME_WORK;
      FIELD_ARRAY[17] = ResourceField.BASELINE_COST;
      FIELD_ARRAY[18] = ResourceField.COST_PER_USE;
      FIELD_ARRAY[19] = ResourceField.ACCRUE_AT;
      FIELD_ARRAY[20] = ResourceField.NOTES;
      FIELD_ARRAY[21] = ResourceField.REMAINING_COST;
      FIELD_ARRAY[22] = ResourceField.REMAINING_WORK;
      FIELD_ARRAY[23] = ResourceField.WORK_VARIANCE;
      FIELD_ARRAY[24] = ResourceField.COST_VARIANCE;
      FIELD_ARRAY[25] = ResourceField.OVERALLOCATED;
      FIELD_ARRAY[26] = ResourceField.PEAK;
      FIELD_ARRAY[27] = ResourceField.UNIQUE_ID;
      FIELD_ARRAY[28] = ResourceField.NOTES;
      FIELD_ARRAY[29] = ResourceField.PERCENT_WORK_COMPLETE;
      FIELD_ARRAY[30] = ResourceField.TEXT3;
      FIELD_ARRAY[31] = ResourceField.TEXT4;
      FIELD_ARRAY[32] = ResourceField.TEXT5;
      FIELD_ARRAY[33] = ResourceField.OBJECTS;
      FIELD_ARRAY[34] = ResourceField.LINKED_FIELDS;
      FIELD_ARRAY[35] = ResourceField.EMAIL_ADDRESS;
      FIELD_ARRAY[38] = ResourceField.REGULAR_WORK;
      FIELD_ARRAY[39] = ResourceField.ACTUAL_OVERTIME_WORK;
      FIELD_ARRAY[40] = ResourceField.REMAINING_OVERTIME_WORK;
      FIELD_ARRAY[47] = ResourceField.OVERTIME_COST;
      FIELD_ARRAY[48] = ResourceField.ACTUAL_OVERTIME_COST;
      FIELD_ARRAY[49] = ResourceField.REMAINING_OVERTIME_COST;
      FIELD_ARRAY[51] = ResourceField.BCWS;
      FIELD_ARRAY[52] = ResourceField.BCWP;
      FIELD_ARRAY[53] = ResourceField.ACWP;
      FIELD_ARRAY[54] = ResourceField.SV;
      FIELD_ARRAY[57] = ResourceField.AVAILABLE_FROM;
      FIELD_ARRAY[58] = ResourceField.AVAILABLE_TO;
      FIELD_ARRAY[61] = ResourceField.COST_RATE_A;
      FIELD_ARRAY[62] = ResourceField.COST_RATE_B;
      FIELD_ARRAY[63] = ResourceField.COST_RATE_C;
      FIELD_ARRAY[64] = ResourceField.COST_RATE_D;
      FIELD_ARRAY[65] = ResourceField.COST_RATE_E;
      FIELD_ARRAY[70] = ResourceField.STANDARD_RATE_UNITS;
      FIELD_ARRAY[71] = ResourceField.OVERTIME_RATE_UNITS;
      FIELD_ARRAY[86] = ResourceField.INDICATORS;
      FIELD_ARRAY[97] = ResourceField.TEXT6;
      FIELD_ARRAY[98] = ResourceField.TEXT7;
      FIELD_ARRAY[99] = ResourceField.TEXT8;
      FIELD_ARRAY[100] = ResourceField.TEXT9;
      FIELD_ARRAY[101] = ResourceField.TEXT10;
      FIELD_ARRAY[102] = ResourceField.START1;
      FIELD_ARRAY[103] = ResourceField.START2;
      FIELD_ARRAY[104] = ResourceField.START3;
      FIELD_ARRAY[105] = ResourceField.START4;
      FIELD_ARRAY[106] = ResourceField.START5;
      FIELD_ARRAY[107] = ResourceField.FINISH1;
      FIELD_ARRAY[108] = ResourceField.FINISH2;
      FIELD_ARRAY[109] = ResourceField.FINISH3;
      FIELD_ARRAY[110] = ResourceField.FINISH4;
      FIELD_ARRAY[111] = ResourceField.FINISH5;
      FIELD_ARRAY[112] = ResourceField.NUMBER1;
      FIELD_ARRAY[113] = ResourceField.NUMBER2;
      FIELD_ARRAY[114] = ResourceField.NUMBER3;
      FIELD_ARRAY[115] = ResourceField.NUMBER4;
      FIELD_ARRAY[116] = ResourceField.NUMBER5;
      FIELD_ARRAY[117] = ResourceField.DURATION1;
      FIELD_ARRAY[118] = ResourceField.DURATION2;
      FIELD_ARRAY[119] = ResourceField.DURATION3;
      FIELD_ARRAY[120] = ResourceField.DURATION1_UNITS;
      FIELD_ARRAY[121] = ResourceField.DURATION2_UNITS;
      FIELD_ARRAY[122] = ResourceField.DURATION3_UNITS;
      FIELD_ARRAY[123] = ResourceField.COST1;
      FIELD_ARRAY[124] = ResourceField.COST2;
      FIELD_ARRAY[125] = ResourceField.COST3;
      FIELD_ARRAY[126] = ResourceField.FLAG10;
      FIELD_ARRAY[127] = ResourceField.FLAG1;
      FIELD_ARRAY[128] = ResourceField.FLAG2;
      FIELD_ARRAY[129] = ResourceField.FLAG3;
      FIELD_ARRAY[130] = ResourceField.FLAG4;
      FIELD_ARRAY[131] = ResourceField.FLAG5;
      FIELD_ARRAY[132] = ResourceField.FLAG6;
      FIELD_ARRAY[133] = ResourceField.FLAG7;
      FIELD_ARRAY[134] = ResourceField.FLAG8;
      FIELD_ARRAY[135] = ResourceField.FLAG9;
      FIELD_ARRAY[136] = ResourceField.HYPERLINK_DATA;
      FIELD_ARRAY[138] = ResourceField.HYPERLINK;
      FIELD_ARRAY[139] = ResourceField.HYPERLINK_ADDRESS;
      FIELD_ARRAY[140] = ResourceField.HYPERLINK_SUBADDRESS;
      FIELD_ARRAY[141] = ResourceField.HYPERLINK_HREF;
      FIELD_ARRAY[144] = ResourceField.ASSIGNMENT;
      FIELD_ARRAY[152] = ResourceField.SUBPROJECT_RESOURCE_UNIQUE_ID;
      FIELD_ARRAY[159] = ResourceField.TASK_SUMMARY_NAME;
      FIELD_ARRAY[163] = ResourceField.CAN_LEVEL;
      FIELD_ARRAY[164] = ResourceField.WORK_CONTOUR;
      FIELD_ARRAY[166] = ResourceField.COST4;
      FIELD_ARRAY[167] = ResourceField.COST5;
      FIELD_ARRAY[168] = ResourceField.COST6;
      FIELD_ARRAY[169] = ResourceField.COST7;
      FIELD_ARRAY[170] = ResourceField.COST8;
      FIELD_ARRAY[171] = ResourceField.COST9;
      FIELD_ARRAY[172] = ResourceField.COST10;
      FIELD_ARRAY[173] = ResourceField.DATE1;
      FIELD_ARRAY[174] = ResourceField.DATE2;
      FIELD_ARRAY[175] = ResourceField.DATE3;
      FIELD_ARRAY[176] = ResourceField.DATE4;
      FIELD_ARRAY[177] = ResourceField.DATE5;
      FIELD_ARRAY[178] = ResourceField.DATE6;
      FIELD_ARRAY[179] = ResourceField.DATE7;
      FIELD_ARRAY[180] = ResourceField.DATE8;
      FIELD_ARRAY[181] = ResourceField.DATE9;
      FIELD_ARRAY[182] = ResourceField.DATE10;
      FIELD_ARRAY[183] = ResourceField.DURATION4;
      FIELD_ARRAY[184] = ResourceField.DURATION5;
      FIELD_ARRAY[185] = ResourceField.DURATION6;
      FIELD_ARRAY[186] = ResourceField.DURATION7;
      FIELD_ARRAY[187] = ResourceField.DURATION8;
      FIELD_ARRAY[188] = ResourceField.DURATION9;
      FIELD_ARRAY[189] = ResourceField.DURATION10;
      FIELD_ARRAY[190] = ResourceField.FINISH6;
      FIELD_ARRAY[191] = ResourceField.FINISH7;
      FIELD_ARRAY[192] = ResourceField.FINISH8;
      FIELD_ARRAY[193] = ResourceField.FINISH9;
      FIELD_ARRAY[194] = ResourceField.FINISH10;
      FIELD_ARRAY[195] = ResourceField.FLAG11;
      FIELD_ARRAY[196] = ResourceField.FLAG12;
      FIELD_ARRAY[197] = ResourceField.FLAG13;
      FIELD_ARRAY[198] = ResourceField.FLAG14;
      FIELD_ARRAY[199] = ResourceField.FLAG15;
      FIELD_ARRAY[200] = ResourceField.FLAG16;
      FIELD_ARRAY[201] = ResourceField.FLAG17;
      FIELD_ARRAY[202] = ResourceField.FLAG18;
      FIELD_ARRAY[203] = ResourceField.FLAG19;
      FIELD_ARRAY[204] = ResourceField.FLAG20;
      FIELD_ARRAY[205] = ResourceField.NUMBER6;
      FIELD_ARRAY[206] = ResourceField.NUMBER7;
      FIELD_ARRAY[207] = ResourceField.NUMBER8;
      FIELD_ARRAY[208] = ResourceField.NUMBER9;
      FIELD_ARRAY[209] = ResourceField.NUMBER10;
      FIELD_ARRAY[210] = ResourceField.NUMBER11;
      FIELD_ARRAY[211] = ResourceField.NUMBER12;
      FIELD_ARRAY[212] = ResourceField.NUMBER13;
      FIELD_ARRAY[213] = ResourceField.NUMBER14;
      FIELD_ARRAY[214] = ResourceField.NUMBER15;
      FIELD_ARRAY[215] = ResourceField.NUMBER16;
      FIELD_ARRAY[216] = ResourceField.NUMBER17;
      FIELD_ARRAY[217] = ResourceField.NUMBER18;
      FIELD_ARRAY[218] = ResourceField.NUMBER19;
      FIELD_ARRAY[219] = ResourceField.NUMBER20;
      FIELD_ARRAY[220] = ResourceField.START6;
      FIELD_ARRAY[221] = ResourceField.START7;
      FIELD_ARRAY[222] = ResourceField.START8;
      FIELD_ARRAY[223] = ResourceField.START9;
      FIELD_ARRAY[224] = ResourceField.START10;
      FIELD_ARRAY[225] = ResourceField.TEXT11;
      FIELD_ARRAY[226] = ResourceField.TEXT12;
      FIELD_ARRAY[227] = ResourceField.TEXT13;
      FIELD_ARRAY[228] = ResourceField.TEXT14;
      FIELD_ARRAY[229] = ResourceField.TEXT15;
      FIELD_ARRAY[230] = ResourceField.TEXT16;
      FIELD_ARRAY[231] = ResourceField.TEXT17;
      FIELD_ARRAY[232] = ResourceField.TEXT18;
      FIELD_ARRAY[233] = ResourceField.TEXT19;
      FIELD_ARRAY[234] = ResourceField.TEXT20;
      FIELD_ARRAY[235] = ResourceField.TEXT21;
      FIELD_ARRAY[236] = ResourceField.TEXT22;
      FIELD_ARRAY[237] = ResourceField.TEXT23;
      FIELD_ARRAY[238] = ResourceField.TEXT24;
      FIELD_ARRAY[239] = ResourceField.TEXT25;
      FIELD_ARRAY[240] = ResourceField.TEXT26;
      FIELD_ARRAY[241] = ResourceField.TEXT27;
      FIELD_ARRAY[242] = ResourceField.TEXT28;
      FIELD_ARRAY[243] = ResourceField.TEXT29;
      FIELD_ARRAY[244] = ResourceField.TEXT30;
      FIELD_ARRAY[245] = ResourceField.DURATION4_UNITS;
      FIELD_ARRAY[246] = ResourceField.DURATION5_UNITS;
      FIELD_ARRAY[247] = ResourceField.DURATION6_UNITS;
      FIELD_ARRAY[248] = ResourceField.DURATION7_UNITS;
      FIELD_ARRAY[249] = ResourceField.DURATION8_UNITS;
      FIELD_ARRAY[250] = ResourceField.DURATION9_UNITS;
      FIELD_ARRAY[251] = ResourceField.DURATION10_UNITS;
      FIELD_ARRAY[252] = ResourceField.PHONETICS;
      FIELD_ARRAY[253] = ResourceField.INDEX;
      FIELD_ARRAY[257] = ResourceField.ASSIGNMENT_DELAY;
      FIELD_ARRAY[258] = ResourceField.ASSIGNMENT_UNITS;
      FIELD_ARRAY[259] = ResourceField.BASELINE_START;
      FIELD_ARRAY[260] = ResourceField.BASELINE_FINISH;
      FIELD_ARRAY[261] = ResourceField.CONFIRMED;
      FIELD_ARRAY[262] = ResourceField.FINISH;
      FIELD_ARRAY[263] = ResourceField.LEVELING_DELAY;
      FIELD_ARRAY[264] = ResourceField.RESPONSE_PENDING;
      FIELD_ARRAY[265] = ResourceField.START;
      FIELD_ARRAY[266] = ResourceField.TEAM_STATUS_PENDING;
      FIELD_ARRAY[267] = ResourceField.UPDATE_NEEDED;
      FIELD_ARRAY[268] = ResourceField.CV;
      FIELD_ARRAY[269] = ResourceField.COST_RATE_TABLE;
      FIELD_ARRAY[270] = ResourceField.ACTUAL_START;
      FIELD_ARRAY[271] = ResourceField.ACTUAL_FINISH;
      FIELD_ARRAY[272] = ResourceField.WORKGROUP;
      FIELD_ARRAY[273] = ResourceField.PROJECT;
      FIELD_ARRAY[276] = ResourceField.AVAILABILITY_DATA;
      FIELD_ARRAY[278] = ResourceField.OUTLINE_CODE1;
      FIELD_ARRAY[279] = ResourceField.OUTLINE_CODE1_INDEX;
      FIELD_ARRAY[280] = ResourceField.OUTLINE_CODE2;
      FIELD_ARRAY[281] = ResourceField.OUTLINE_CODE2_INDEX;
      FIELD_ARRAY[282] = ResourceField.OUTLINE_CODE3;
      FIELD_ARRAY[283] = ResourceField.OUTLINE_CODE3_INDEX;
      FIELD_ARRAY[284] = ResourceField.OUTLINE_CODE4;
      FIELD_ARRAY[285] = ResourceField.OUTLINE_CODE4_INDEX;
      FIELD_ARRAY[286] = ResourceField.OUTLINE_CODE5;
      FIELD_ARRAY[287] = ResourceField.OUTLINE_CODE5_INDEX;
      FIELD_ARRAY[288] = ResourceField.OUTLINE_CODE6;
      FIELD_ARRAY[289] = ResourceField.OUTLINE_CODE6_INDEX;
      FIELD_ARRAY[290] = ResourceField.OUTLINE_CODE7;
      FIELD_ARRAY[291] = ResourceField.OUTLINE_CODE7_INDEX;
      FIELD_ARRAY[292] = ResourceField.OUTLINE_CODE8;
      FIELD_ARRAY[293] = ResourceField.OUTLINE_CODE8_INDEX;
      FIELD_ARRAY[294] = ResourceField.OUTLINE_CODE9;
      FIELD_ARRAY[295] = ResourceField.OUTLINE_CODE9_INDEX;
      FIELD_ARRAY[296] = ResourceField.OUTLINE_CODE10;
      FIELD_ARRAY[297] = ResourceField.OUTLINE_CODE10_INDEX;
      FIELD_ARRAY[299] = ResourceField.MATERIAL_LABEL;
      FIELD_ARRAY[300] = ResourceField.TYPE;
      FIELD_ARRAY[301] = ResourceField.VAC;
      FIELD_ARRAY[306] = ResourceField.GROUP_BY_SUMMARY;
      FIELD_ARRAY[311] = ResourceField.WINDOWS_USER_ACCOUNT;
      FIELD_ARRAY[312] = ResourceField.HYPERLINK_SCREEN_TIP;
      FIELD_ARRAY[340] = ResourceField.WBS;
      FIELD_ARRAY[341] = ResourceField.ENTERPRISE_DATA;
      FIELD_ARRAY[342] = ResourceField.BASELINE1_WORK;
      FIELD_ARRAY[343] = ResourceField.BASELINE1_COST;
      FIELD_ARRAY[348] = ResourceField.BASELINE1_START;
      FIELD_ARRAY[349] = ResourceField.BASELINE1_FINISH;
      FIELD_ARRAY[352] = ResourceField.BASELINE2_WORK;
      FIELD_ARRAY[353] = ResourceField.BASELINE2_COST;
      FIELD_ARRAY[358] = ResourceField.BASELINE2_START;
      FIELD_ARRAY[359] = ResourceField.BASELINE2_FINISH;
      FIELD_ARRAY[362] = ResourceField.BASELINE3_WORK;
      FIELD_ARRAY[363] = ResourceField.BASELINE3_COST;
      FIELD_ARRAY[368] = ResourceField.BASELINE3_START;
      FIELD_ARRAY[369] = ResourceField.BASELINE3_FINISH;
      FIELD_ARRAY[372] = ResourceField.BASELINE4_WORK;
      FIELD_ARRAY[373] = ResourceField.BASELINE4_COST;
      FIELD_ARRAY[378] = ResourceField.BASELINE4_START;
      FIELD_ARRAY[379] = ResourceField.BASELINE4_FINISH;
      FIELD_ARRAY[382] = ResourceField.BASELINE5_WORK;
      FIELD_ARRAY[383] = ResourceField.BASELINE5_COST;
      FIELD_ARRAY[388] = ResourceField.BASELINE5_START;
      FIELD_ARRAY[389] = ResourceField.BASELINE5_FINISH;
      FIELD_ARRAY[392] = ResourceField.BASELINE6_WORK;
      FIELD_ARRAY[393] = ResourceField.BASELINE6_COST;
      FIELD_ARRAY[398] = ResourceField.BASELINE6_START;
      FIELD_ARRAY[399] = ResourceField.BASELINE6_FINISH;
      FIELD_ARRAY[402] = ResourceField.BASELINE7_WORK;
      FIELD_ARRAY[403] = ResourceField.BASELINE7_COST;
      FIELD_ARRAY[408] = ResourceField.BASELINE7_START;
      FIELD_ARRAY[409] = ResourceField.BASELINE7_FINISH;
      FIELD_ARRAY[412] = ResourceField.BASELINE8_WORK;
      FIELD_ARRAY[413] = ResourceField.BASELINE8_COST;
      FIELD_ARRAY[418] = ResourceField.BASELINE8_START;
      FIELD_ARRAY[419] = ResourceField.BASELINE8_FINISH;
      FIELD_ARRAY[422] = ResourceField.BASELINE9_WORK;
      FIELD_ARRAY[423] = ResourceField.BASELINE9_COST;
      FIELD_ARRAY[428] = ResourceField.BASELINE9_START;
      FIELD_ARRAY[429] = ResourceField.BASELINE9_FINISH;
      FIELD_ARRAY[432] = ResourceField.BASELINE10_WORK;
      FIELD_ARRAY[433] = ResourceField.BASELINE10_COST;
      FIELD_ARRAY[438] = ResourceField.BASELINE10_START;
      FIELD_ARRAY[439] = ResourceField.BASELINE10_FINISH;
      FIELD_ARRAY[442] = ResourceField.TASK_OUTLINE_NUMBER;
      FIELD_ARRAY[443] = ResourceField.ENTERPRISE_UNIQUE_ID;
      FIELD_ARRAY[446] = ResourceField.ENTERPRISE_COST1;
      FIELD_ARRAY[447] = ResourceField.ENTERPRISE_COST2;
      FIELD_ARRAY[448] = ResourceField.ENTERPRISE_COST3;
      FIELD_ARRAY[449] = ResourceField.ENTERPRISE_COST4;
      FIELD_ARRAY[450] = ResourceField.ENTERPRISE_COST5;
      FIELD_ARRAY[451] = ResourceField.ENTERPRISE_COST6;
      FIELD_ARRAY[452] = ResourceField.ENTERPRISE_COST7;
      FIELD_ARRAY[453] = ResourceField.ENTERPRISE_COST8;
      FIELD_ARRAY[454] = ResourceField.ENTERPRISE_COST9;
      FIELD_ARRAY[455] = ResourceField.ENTERPRISE_COST10;
      FIELD_ARRAY[456] = ResourceField.ENTERPRISE_DATE1;
      FIELD_ARRAY[457] = ResourceField.ENTERPRISE_DATE2;
      FIELD_ARRAY[458] = ResourceField.ENTERPRISE_DATE3;
      FIELD_ARRAY[459] = ResourceField.ENTERPRISE_DATE4;
      FIELD_ARRAY[460] = ResourceField.ENTERPRISE_DATE5;
      FIELD_ARRAY[461] = ResourceField.ENTERPRISE_DATE6;
      FIELD_ARRAY[462] = ResourceField.ENTERPRISE_DATE7;
      FIELD_ARRAY[463] = ResourceField.ENTERPRISE_DATE8;
      FIELD_ARRAY[464] = ResourceField.ENTERPRISE_DATE9;
      FIELD_ARRAY[465] = ResourceField.ENTERPRISE_DATE10;
      FIELD_ARRAY[466] = ResourceField.ENTERPRISE_DATE11;
      FIELD_ARRAY[467] = ResourceField.ENTERPRISE_DATE12;
      FIELD_ARRAY[468] = ResourceField.ENTERPRISE_DATE13;
      FIELD_ARRAY[469] = ResourceField.ENTERPRISE_DATE14;
      FIELD_ARRAY[470] = ResourceField.ENTERPRISE_DATE15;
      FIELD_ARRAY[471] = ResourceField.ENTERPRISE_DATE16;
      FIELD_ARRAY[472] = ResourceField.ENTERPRISE_DATE17;
      FIELD_ARRAY[473] = ResourceField.ENTERPRISE_DATE18;
      FIELD_ARRAY[474] = ResourceField.ENTERPRISE_DATE19;
      FIELD_ARRAY[475] = ResourceField.ENTERPRISE_DATE20;
      FIELD_ARRAY[476] = ResourceField.ENTERPRISE_DATE21;
      FIELD_ARRAY[477] = ResourceField.ENTERPRISE_DATE22;
      FIELD_ARRAY[478] = ResourceField.ENTERPRISE_DATE23;
      FIELD_ARRAY[479] = ResourceField.ENTERPRISE_DATE24;
      FIELD_ARRAY[480] = ResourceField.ENTERPRISE_DATE25;
      FIELD_ARRAY[481] = ResourceField.ENTERPRISE_DATE26;
      FIELD_ARRAY[482] = ResourceField.ENTERPRISE_DATE27;
      FIELD_ARRAY[483] = ResourceField.ENTERPRISE_DATE28;
      FIELD_ARRAY[484] = ResourceField.ENTERPRISE_DATE29;
      FIELD_ARRAY[485] = ResourceField.ENTERPRISE_DATE30;
      FIELD_ARRAY[486] = ResourceField.ENTERPRISE_DURATION1;
      FIELD_ARRAY[487] = ResourceField.ENTERPRISE_DURATION2;
      FIELD_ARRAY[488] = ResourceField.ENTERPRISE_DURATION3;
      FIELD_ARRAY[489] = ResourceField.ENTERPRISE_DURATION4;
      FIELD_ARRAY[490] = ResourceField.ENTERPRISE_DURATION5;
      FIELD_ARRAY[491] = ResourceField.ENTERPRISE_DURATION6;
      FIELD_ARRAY[492] = ResourceField.ENTERPRISE_DURATION7;
      FIELD_ARRAY[493] = ResourceField.ENTERPRISE_DURATION8;
      FIELD_ARRAY[494] = ResourceField.ENTERPRISE_DURATION9;
      FIELD_ARRAY[495] = ResourceField.ENTERPRISE_DURATION10;
      FIELD_ARRAY[496] = ResourceField.ENTERPRISE_DURATION1_UNITS;
      FIELD_ARRAY[497] = ResourceField.ENTERPRISE_DURATION2_UNITS;
      FIELD_ARRAY[498] = ResourceField.ENTERPRISE_DURATION3_UNITS;
      FIELD_ARRAY[499] = ResourceField.ENTERPRISE_DURATION4_UNITS;
      FIELD_ARRAY[500] = ResourceField.ENTERPRISE_DURATION5_UNITS;
      FIELD_ARRAY[501] = ResourceField.ENTERPRISE_DURATION6_UNITS;
      FIELD_ARRAY[502] = ResourceField.ENTERPRISE_DURATION7_UNITS;
      FIELD_ARRAY[503] = ResourceField.ENTERPRISE_DURATION8_UNITS;
      FIELD_ARRAY[504] = ResourceField.ENTERPRISE_DURATION9_UNITS;
      FIELD_ARRAY[505] = ResourceField.ENTERPRISE_DURATION10_UNITS;
      FIELD_ARRAY[506] = ResourceField.ENTERPRISE_FLAG1;
      FIELD_ARRAY[507] = ResourceField.ENTERPRISE_FLAG2;
      FIELD_ARRAY[508] = ResourceField.ENTERPRISE_FLAG3;
      FIELD_ARRAY[509] = ResourceField.ENTERPRISE_FLAG4;
      FIELD_ARRAY[510] = ResourceField.ENTERPRISE_FLAG5;
      FIELD_ARRAY[511] = ResourceField.ENTERPRISE_FLAG6;
      FIELD_ARRAY[512] = ResourceField.ENTERPRISE_FLAG7;
      FIELD_ARRAY[513] = ResourceField.ENTERPRISE_FLAG8;
      FIELD_ARRAY[514] = ResourceField.ENTERPRISE_FLAG9;
      FIELD_ARRAY[515] = ResourceField.ENTERPRISE_FLAG10;
      FIELD_ARRAY[516] = ResourceField.ENTERPRISE_FLAG11;
      FIELD_ARRAY[517] = ResourceField.ENTERPRISE_FLAG12;
      FIELD_ARRAY[518] = ResourceField.ENTERPRISE_FLAG13;
      FIELD_ARRAY[519] = ResourceField.ENTERPRISE_FLAG14;
      FIELD_ARRAY[520] = ResourceField.ENTERPRISE_FLAG15;
      FIELD_ARRAY[521] = ResourceField.ENTERPRISE_FLAG16;
      FIELD_ARRAY[522] = ResourceField.ENTERPRISE_FLAG17;
      FIELD_ARRAY[523] = ResourceField.ENTERPRISE_FLAG18;
      FIELD_ARRAY[524] = ResourceField.ENTERPRISE_FLAG19;
      FIELD_ARRAY[525] = ResourceField.ENTERPRISE_FLAG20;
      FIELD_ARRAY[546] = ResourceField.ENTERPRISE_NUMBER1;
      FIELD_ARRAY[547] = ResourceField.ENTERPRISE_NUMBER2;
      FIELD_ARRAY[548] = ResourceField.ENTERPRISE_NUMBER3;
      FIELD_ARRAY[549] = ResourceField.ENTERPRISE_NUMBER4;
      FIELD_ARRAY[550] = ResourceField.ENTERPRISE_NUMBER5;
      FIELD_ARRAY[551] = ResourceField.ENTERPRISE_NUMBER6;
      FIELD_ARRAY[552] = ResourceField.ENTERPRISE_NUMBER7;
      FIELD_ARRAY[553] = ResourceField.ENTERPRISE_NUMBER8;
      FIELD_ARRAY[554] = ResourceField.ENTERPRISE_NUMBER9;
      FIELD_ARRAY[555] = ResourceField.ENTERPRISE_NUMBER10;
      FIELD_ARRAY[556] = ResourceField.ENTERPRISE_NUMBER11;
      FIELD_ARRAY[557] = ResourceField.ENTERPRISE_NUMBER12;
      FIELD_ARRAY[558] = ResourceField.ENTERPRISE_NUMBER13;
      FIELD_ARRAY[559] = ResourceField.ENTERPRISE_NUMBER14;
      FIELD_ARRAY[560] = ResourceField.ENTERPRISE_NUMBER15;
      FIELD_ARRAY[561] = ResourceField.ENTERPRISE_NUMBER16;
      FIELD_ARRAY[562] = ResourceField.ENTERPRISE_NUMBER17;
      FIELD_ARRAY[563] = ResourceField.ENTERPRISE_NUMBER18;
      FIELD_ARRAY[564] = ResourceField.ENTERPRISE_NUMBER19;
      FIELD_ARRAY[565] = ResourceField.ENTERPRISE_NUMBER20;
      FIELD_ARRAY[566] = ResourceField.ENTERPRISE_NUMBER21;
      FIELD_ARRAY[567] = ResourceField.ENTERPRISE_NUMBER22;
      FIELD_ARRAY[568] = ResourceField.ENTERPRISE_NUMBER23;
      FIELD_ARRAY[569] = ResourceField.ENTERPRISE_NUMBER24;
      FIELD_ARRAY[570] = ResourceField.ENTERPRISE_NUMBER25;
      FIELD_ARRAY[571] = ResourceField.ENTERPRISE_NUMBER26;
      FIELD_ARRAY[572] = ResourceField.ENTERPRISE_NUMBER27;
      FIELD_ARRAY[573] = ResourceField.ENTERPRISE_NUMBER28;
      FIELD_ARRAY[574] = ResourceField.ENTERPRISE_NUMBER29;
      FIELD_ARRAY[575] = ResourceField.ENTERPRISE_NUMBER30;
      FIELD_ARRAY[576] = ResourceField.ENTERPRISE_NUMBER31;
      FIELD_ARRAY[577] = ResourceField.ENTERPRISE_NUMBER32;
      FIELD_ARRAY[578] = ResourceField.ENTERPRISE_NUMBER33;
      FIELD_ARRAY[579] = ResourceField.ENTERPRISE_NUMBER34;
      FIELD_ARRAY[580] = ResourceField.ENTERPRISE_NUMBER35;
      FIELD_ARRAY[581] = ResourceField.ENTERPRISE_NUMBER36;
      FIELD_ARRAY[582] = ResourceField.ENTERPRISE_NUMBER37;
      FIELD_ARRAY[583] = ResourceField.ENTERPRISE_NUMBER38;
      FIELD_ARRAY[584] = ResourceField.ENTERPRISE_NUMBER39;
      FIELD_ARRAY[585] = ResourceField.ENTERPRISE_NUMBER40;
      FIELD_ARRAY[586] = ResourceField.ENTERPRISE_OUTLINE_CODE1;
      FIELD_ARRAY[588] = ResourceField.ENTERPRISE_OUTLINE_CODE2;
      FIELD_ARRAY[590] = ResourceField.ENTERPRISE_OUTLINE_CODE3;
      FIELD_ARRAY[592] = ResourceField.ENTERPRISE_OUTLINE_CODE4;
      FIELD_ARRAY[594] = ResourceField.ENTERPRISE_OUTLINE_CODE5;
      FIELD_ARRAY[596] = ResourceField.ENTERPRISE_OUTLINE_CODE6;
      FIELD_ARRAY[598] = ResourceField.ENTERPRISE_OUTLINE_CODE7;
      FIELD_ARRAY[600] = ResourceField.ENTERPRISE_OUTLINE_CODE8;
      FIELD_ARRAY[602] = ResourceField.ENTERPRISE_OUTLINE_CODE9;
      FIELD_ARRAY[604] = ResourceField.ENTERPRISE_OUTLINE_CODE10;
      FIELD_ARRAY[606] = ResourceField.ENTERPRISE_OUTLINE_CODE11;
      FIELD_ARRAY[608] = ResourceField.ENTERPRISE_OUTLINE_CODE12;
      FIELD_ARRAY[610] = ResourceField.ENTERPRISE_OUTLINE_CODE13;
      FIELD_ARRAY[612] = ResourceField.ENTERPRISE_OUTLINE_CODE14;
      FIELD_ARRAY[614] = ResourceField.ENTERPRISE_OUTLINE_CODE15;
      FIELD_ARRAY[616] = ResourceField.ENTERPRISE_OUTLINE_CODE16;
      FIELD_ARRAY[618] = ResourceField.ENTERPRISE_OUTLINE_CODE17;
      FIELD_ARRAY[620] = ResourceField.ENTERPRISE_OUTLINE_CODE18;
      FIELD_ARRAY[622] = ResourceField.ENTERPRISE_OUTLINE_CODE19;
      FIELD_ARRAY[624] = ResourceField.ENTERPRISE_OUTLINE_CODE20;
      FIELD_ARRAY[626] = ResourceField.ENTERPRISE_OUTLINE_CODE21;
      FIELD_ARRAY[628] = ResourceField.ENTERPRISE_OUTLINE_CODE22;
      FIELD_ARRAY[630] = ResourceField.ENTERPRISE_OUTLINE_CODE23;
      FIELD_ARRAY[632] = ResourceField.ENTERPRISE_OUTLINE_CODE24;
      FIELD_ARRAY[634] = ResourceField.ENTERPRISE_OUTLINE_CODE25;
      FIELD_ARRAY[636] = ResourceField.ENTERPRISE_OUTLINE_CODE26;
      FIELD_ARRAY[638] = ResourceField.ENTERPRISE_OUTLINE_CODE27;
      FIELD_ARRAY[640] = ResourceField.ENTERPRISE_OUTLINE_CODE28;
      FIELD_ARRAY[642] = ResourceField.ENTERPRISE_OUTLINE_CODE29;
      FIELD_ARRAY[644] = ResourceField.ENTERPRISE_RBS;
      FIELD_ARRAY[646] = ResourceField.ENTERPRISE_TEXT1;
      FIELD_ARRAY[647] = ResourceField.ENTERPRISE_TEXT2;
      FIELD_ARRAY[648] = ResourceField.ENTERPRISE_TEXT3;
      FIELD_ARRAY[649] = ResourceField.ENTERPRISE_TEXT4;
      FIELD_ARRAY[650] = ResourceField.ENTERPRISE_TEXT5;
      FIELD_ARRAY[651] = ResourceField.ENTERPRISE_TEXT6;
      FIELD_ARRAY[652] = ResourceField.ENTERPRISE_TEXT7;
      FIELD_ARRAY[653] = ResourceField.ENTERPRISE_TEXT8;
      FIELD_ARRAY[654] = ResourceField.ENTERPRISE_TEXT9;
      FIELD_ARRAY[655] = ResourceField.ENTERPRISE_TEXT10;
      FIELD_ARRAY[656] = ResourceField.ENTERPRISE_TEXT11;
      FIELD_ARRAY[657] = ResourceField.ENTERPRISE_TEXT12;
      FIELD_ARRAY[658] = ResourceField.ENTERPRISE_TEXT13;
      FIELD_ARRAY[659] = ResourceField.ENTERPRISE_TEXT14;
      FIELD_ARRAY[660] = ResourceField.ENTERPRISE_TEXT15;
      FIELD_ARRAY[661] = ResourceField.ENTERPRISE_TEXT16;
      FIELD_ARRAY[662] = ResourceField.ENTERPRISE_TEXT17;
      FIELD_ARRAY[663] = ResourceField.ENTERPRISE_TEXT18;
      FIELD_ARRAY[664] = ResourceField.ENTERPRISE_TEXT19;
      FIELD_ARRAY[665] = ResourceField.ENTERPRISE_TEXT20;
      FIELD_ARRAY[666] = ResourceField.ENTERPRISE_TEXT21;
      FIELD_ARRAY[667] = ResourceField.ENTERPRISE_TEXT22;
      FIELD_ARRAY[668] = ResourceField.ENTERPRISE_TEXT23;
      FIELD_ARRAY[669] = ResourceField.ENTERPRISE_TEXT24;
      FIELD_ARRAY[670] = ResourceField.ENTERPRISE_TEXT25;
      FIELD_ARRAY[671] = ResourceField.ENTERPRISE_TEXT26;
      FIELD_ARRAY[672] = ResourceField.ENTERPRISE_TEXT27;
      FIELD_ARRAY[673] = ResourceField.ENTERPRISE_TEXT28;
      FIELD_ARRAY[674] = ResourceField.ENTERPRISE_TEXT29;
      FIELD_ARRAY[675] = ResourceField.ENTERPRISE_TEXT30;
      FIELD_ARRAY[676] = ResourceField.ENTERPRISE_TEXT31;
      FIELD_ARRAY[677] = ResourceField.ENTERPRISE_TEXT32;
      FIELD_ARRAY[678] = ResourceField.ENTERPRISE_TEXT33;
      FIELD_ARRAY[679] = ResourceField.ENTERPRISE_TEXT34;
      FIELD_ARRAY[680] = ResourceField.ENTERPRISE_TEXT35;
      FIELD_ARRAY[681] = ResourceField.ENTERPRISE_TEXT36;
      FIELD_ARRAY[682] = ResourceField.ENTERPRISE_TEXT37;
      FIELD_ARRAY[683] = ResourceField.ENTERPRISE_TEXT38;
      FIELD_ARRAY[684] = ResourceField.ENTERPRISE_TEXT39;
      FIELD_ARRAY[685] = ResourceField.ENTERPRISE_TEXT40;
      FIELD_ARRAY[686] = ResourceField.GENERIC;
      FIELD_ARRAY[687] = ResourceField.ENTERPRISE_BASE_CALENDAR;
      FIELD_ARRAY[688] = ResourceField.ENTERPRISE_REQUIRED_VALUES;
      FIELD_ARRAY[689] = ResourceField.ENTERPRISE_NAME_USED;
      FIELD_ARRAY[690] = ResourceField.REQUEST_DEMAND;
      FIELD_ARRAY[691] = ResourceField.ENTERPRISE;
      FIELD_ARRAY[692] = ResourceField.ENTERPRISE_IS_CHECKED_OUT;
      FIELD_ARRAY[693] = ResourceField.ENTERPRISE_CHECKED_OUT_BY;
      FIELD_ARRAY[694] = ResourceField.ENTERPRISE_LAST_MODIFIED_DATE;
      FIELD_ARRAY[695] = ResourceField.ENTERPRISE_TEAM_MEMBER;
      FIELD_ARRAY[696] = ResourceField.INACTIVE;
      FIELD_ARRAY[699] = ResourceField.BOOKING_TYPE;
      FIELD_ARRAY[700] = ResourceField.ENTERPRISE_MULTI_VALUE20;
      FIELD_ARRAY[702] = ResourceField.ENTERPRISE_MULTI_VALUE21;
      FIELD_ARRAY[704] = ResourceField.ENTERPRISE_MULTI_VALUE22;
      FIELD_ARRAY[706] = ResourceField.ENTERPRISE_MULTI_VALUE23;
      FIELD_ARRAY[708] = ResourceField.ENTERPRISE_MULTI_VALUE24;
      FIELD_ARRAY[710] = ResourceField.ENTERPRISE_MULTI_VALUE25;
      FIELD_ARRAY[712] = ResourceField.ENTERPRISE_MULTI_VALUE26;
      FIELD_ARRAY[714] = ResourceField.ENTERPRISE_MULTI_VALUE27;
      FIELD_ARRAY[716] = ResourceField.ENTERPRISE_MULTI_VALUE28;
      FIELD_ARRAY[718] = ResourceField.ENTERPRISE_MULTI_VALUE29;
      FIELD_ARRAY[720] = ResourceField.ACTUAL_WORK_PROTECTED;
      FIELD_ARRAY[721] = ResourceField.ACTUAL_OVERTIME_WORK_PROTECTED;
      FIELD_ARRAY[726] = ResourceField.CREATED;
      FIELD_ARRAY[728] = ResourceField.GUID;
      FIELD_ARRAY[729] = ResourceField.CALENDAR_GUID;
      FIELD_ARRAY[737] = ResourceField.SUMMARY;
      FIELD_ARRAY[738] = ResourceField.ERROR_MESSAGE;
      FIELD_ARRAY[740] = ResourceField.DEFAULT_ASSIGNMENT_OWNER;
      FIELD_ARRAY[752] = ResourceField.BUDGET;
      FIELD_ARRAY[753] = ResourceField.BUDGET_WORK;
      FIELD_ARRAY[754] = ResourceField.BUDGET_COST;
      FIELD_ARRAY[755] = ResourceField.IMPORT;
      FIELD_ARRAY[756] = ResourceField.BASELINE_BUDGET_WORK;
      FIELD_ARRAY[757] = ResourceField.BASELINE_BUDGET_COST;
      FIELD_ARRAY[760] = ResourceField.BASELINE1_BUDGET_WORK;
      FIELD_ARRAY[761] = ResourceField.BASELINE1_BUDGET_COST;
      FIELD_ARRAY[764] = ResourceField.BASELINE2_BUDGET_WORK;
      FIELD_ARRAY[765] = ResourceField.BASELINE2_BUDGET_COST;
      FIELD_ARRAY[768] = ResourceField.BASELINE3_BUDGET_WORK;
      FIELD_ARRAY[769] = ResourceField.BASELINE3_BUDGET_COST;
      FIELD_ARRAY[772] = ResourceField.BASELINE4_BUDGET_WORK;
      FIELD_ARRAY[773] = ResourceField.BASELINE4_BUDGET_COST;
      FIELD_ARRAY[776] = ResourceField.BASELINE5_BUDGET_WORK;
      FIELD_ARRAY[777] = ResourceField.BASELINE5_BUDGET_COST;
      FIELD_ARRAY[780] = ResourceField.BASELINE6_BUDGET_WORK;
      FIELD_ARRAY[781] = ResourceField.BASELINE6_BUDGET_COST;
      FIELD_ARRAY[784] = ResourceField.BASELINE7_BUDGET_WORK;
      FIELD_ARRAY[785] = ResourceField.BASELINE7_BUDGET_COST;
      FIELD_ARRAY[788] = ResourceField.BASELINE8_BUDGET_WORK;
      FIELD_ARRAY[789] = ResourceField.BASELINE8_BUDGET_COST;
      FIELD_ARRAY[792] = ResourceField.BASELINE9_BUDGET_WORK;
      FIELD_ARRAY[793] = ResourceField.BASELINE9_BUDGET_COST;
      FIELD_ARRAY[796] = ResourceField.BASELINE10_BUDGET_WORK;
      FIELD_ARRAY[797] = ResourceField.BASELINE10_BUDGET_COST;
      FIELD_ARRAY[800] = ResourceField.TEAM_ASSIGNMENT_POOL;
      FIELD_ARRAY[801] = ResourceField.COST_CENTER;
      FIELD_ARRAY[803] = ResourceField.ASSIGNMENT_OWNER;
      FIELD_ARRAY[808] = ResourceField.ACTIVE;
      FIELD_ARRAY[853] = ResourceField.PROPOSED_START;
      FIELD_ARRAY[856] = ResourceField.PROPOSED_FINISH;
      FIELD_ARRAY[859] = ResourceField.PROPOSED_MAX_UNITS;
      FIELD_ARRAY[862] = ResourceField.ENGAGEMENT_STATUS;
   }

   private static final int[] ID_ARRAY = new int[ResourceField.MAX_VALUE];
   static
   {
      Arrays.fill(ID_ARRAY, -1);

      for (int loop = 0; loop < FIELD_ARRAY.length; loop++)
      {
         ResourceField resourceField = FIELD_ARRAY[loop];
         if (resourceField != null)
         {
            ID_ARRAY[resourceField.getValue()] = loop;
         }
      }
   }

   public static final int RESOURCE_FIELD_BASE = 0x0C400000;
}
