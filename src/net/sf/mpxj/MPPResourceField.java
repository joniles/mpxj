/*
 * file:       MPPResourceField.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software Limited 2005
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

package net.sf.mpxj;



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
    * @param value value from an MS Project file
    * @return ResourceField instance
    */
   public static ResourceField getInstance (int value)
   {
      ResourceField result = null;

      if (value >=0 && value < FIELD_ARRAY.length)
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
   public static int getID (ResourceField value)
   {
      return (ID_ARRAY[value.getValue()]);
   }

   public static final int ID_VALUE = 0;
   public static final int NAME_VALUE = 1;
   public static final int INITIALS_VALUE = 2;
   public static final int GROUP_VALUE = 3;
   public static final int MAX_UNITS_VALUE = 4;
   public static final int BASE_CALENDAR_VALUE = 5;
   public static final int STANDARD_RATE_VALUE = 6;
   public static final int OVERTIME_RATE_VALUE = 7;
   public static final int TEXT1_VALUE = 8;
   public static final int TEXT2_VALUE = 9;
   public static final int CODE_VALUE = 10;
   public static final int ACTUAL_COST_VALUE = 11;
   public static final int COST_VALUE = 12;
   public static final int WORK_VALUE = 13;
   public static final int ACTUAL_WORK_VALUE = 14;
   public static final int BASELINE_WORK_VALUE = 15;
   public static final int OVERTIME_WORK_VALUE = 16;
   public static final int BASELINE_COST_VALUE = 17;
   public static final int COST_PER_USE_VALUE = 18;
   public static final int ACCRUE_AT_VALUE = 19;
   public static final int REMAINING_COST_VALUE = 21;
   public static final int REMAINING_WORK_VALUE = 22;
   public static final int WORK_VARIANCE_VALUE = 23;
   public static final int COST_VARIANCE_VALUE = 24;
   public static final int OVERALLOCATED_VALUE = 25;
   public static final int PEAK_VALUE = 26;
   public static final int UNIQUE_ID_VALUE = 27;
   public static final int NOTES_VALUE = 28;
   public static final int PERCENT_WORK_COMPLETE_VALUE = 29;
   public static final int TEXT3_VALUE = 30;
   public static final int TEXT4_VALUE = 31;
   public static final int TEXT5_VALUE = 32;
   public static final int OBJECTS_VALUE = 33;
   public static final int LINKED_FIELDS_VALUE = 34;
   public static final int EMAIL_ADDRESS_VALUE = 35;
   public static final int REGULAR_WORK_VALUE = 38;
   public static final int ACTUAL_OVERTIME_WORK_VALUE = 39;
   public static final int REMAINING_OVERTIME_WORK_VALUE = 40;
   public static final int OVERTIME_COST_VALUE = 47;
   public static final int ACTUAL_OVERTIME_COST_VALUE = 48;
   public static final int REMAINING_OVERTIME_COST_VALUE = 49;
   public static final int BCWS_VALUE = 51;
   public static final int BCWP_VALUE = 52;
   public static final int ACWP_VALUE = 53;
   public static final int SV_VALUE = 54;
   public static final int AVAILABLE_FROM_VALUE = 57;
   public static final int AVAILABLE_TO_VALUE = 58;
   public static final int INDICATORS_VALUE = 86;
   public static final int TEXT6_VALUE = 97;
   public static final int TEXT7_VALUE = 98;
   public static final int TEXT8_VALUE = 99;
   public static final int TEXT9_VALUE = 100;
   public static final int TEXT10_VALUE = 101;
   public static final int START1_VALUE = 102;
   public static final int START2_VALUE = 103;
   public static final int START3_VALUE = 104;
   public static final int START4_VALUE = 105;
   public static final int START5_VALUE = 106;
   public static final int FINISH1_VALUE = 107;
   public static final int FINISH2_VALUE = 108;
   public static final int FINISH3_VALUE = 109;
   public static final int FINISH4_VALUE = 110;
   public static final int FINISH5_VALUE = 111;
   public static final int NUMBER1_VALUE = 112;
   public static final int NUMBER2_VALUE = 113;
   public static final int NUMBER3_VALUE = 114;
   public static final int NUMBER4_VALUE = 115;
   public static final int NUMBER5_VALUE = 116;
   public static final int DURATION1_VALUE = 117;
   public static final int DURATION2_VALUE = 118;
   public static final int DURATION3_VALUE = 119;
   public static final int COST1_VALUE = 123;
   public static final int COST2_VALUE = 124;
   public static final int COST3_VALUE = 125;
   public static final int FLAG10_VALUE = 126;
   public static final int FLAG1_VALUE = 127;
   public static final int FLAG2_VALUE = 128;
   public static final int FLAG3_VALUE = 129;
   public static final int FLAG4_VALUE = 130;
   public static final int FLAG5_VALUE = 131;
   public static final int FLAG6_VALUE = 132;
   public static final int FLAG7_VALUE = 133;
   public static final int FLAG8_VALUE = 134;
   public static final int FLAG9_VALUE = 135;
   public static final int HYPERLINK_VALUE = 138;
   public static final int HYPERLINK_ADDRESS_VALUE = 139;
   public static final int HYPERLINK_SUBADDRESS_VALUE = 140;
   public static final int HYPERLINK_HREF_VALUE = 141;
   public static final int ASSIGNMENT_VALUE = 144;
   public static final int TASK_SUMMARY_NAME_VALUE = 159;
   public static final int CAN_LEVEL_VALUE = 163;
   public static final int WORK_CONTOUR_VALUE = 164;
   public static final int COST4_VALUE = 166;
   public static final int COST5_VALUE = 167;
   public static final int COST6_VALUE = 168;
   public static final int COST7_VALUE = 169;
   public static final int COST8_VALUE = 170;
   public static final int COST9_VALUE = 171;
   public static final int COST10_VALUE = 172;
   public static final int DATE1_VALUE = 173;
   public static final int DATE2_VALUE = 174;
   public static final int DATE3_VALUE = 175;
   public static final int DATE4_VALUE = 176;
   public static final int DATE5_VALUE = 177;
   public static final int DATE6_VALUE = 178;
   public static final int DATE7_VALUE = 179;
   public static final int DATE8_VALUE = 180;
   public static final int DATE9_VALUE = 181;
   public static final int DATE10_VALUE = 182;
   public static final int DURATION4_VALUE = 183;
   public static final int DURATION5_VALUE = 184;
   public static final int DURATION6_VALUE = 185;
   public static final int DURATION7_VALUE = 186;
   public static final int DURATION8_VALUE = 187;
   public static final int DURATION9_VALUE = 188;
   public static final int DURATION10_VALUE = 189;
   public static final int FINISH6_VALUE = 190;
   public static final int FINISH7_VALUE = 191;
   public static final int FINISH8_VALUE = 192;
   public static final int FINISH9_VALUE = 193;
   public static final int FINISH10_VALUE = 194;
   public static final int FLAG11_VALUE = 195;
   public static final int FLAG12_VALUE = 196;
   public static final int FLAG13_VALUE = 197;
   public static final int FLAG14_VALUE = 198;
   public static final int FLAG15_VALUE = 199;
   public static final int FLAG16_VALUE = 200;
   public static final int FLAG17_VALUE = 201;
   public static final int FLAG18_VALUE = 202;
   public static final int FLAG19_VALUE = 203;
   public static final int FLAG20_VALUE = 204;
   public static final int NUMBER6_VALUE = 205;
   public static final int NUMBER7_VALUE = 206;
   public static final int NUMBER8_VALUE = 207;
   public static final int NUMBER9_VALUE = 208;
   public static final int NUMBER10_VALUE = 209;
   public static final int NUMBER11_VALUE = 210;
   public static final int NUMBER12_VALUE = 211;
   public static final int NUMBER13_VALUE = 212;
   public static final int NUMBER14_VALUE = 213;
   public static final int NUMBER15_VALUE = 214;
   public static final int NUMBER16_VALUE = 215;
   public static final int NUMBER17_VALUE = 216;
   public static final int NUMBER18_VALUE = 217;
   public static final int NUMBER19_VALUE = 218;
   public static final int NUMBER20_VALUE = 219;
   public static final int START6_VALUE = 220;
   public static final int START7_VALUE = 221;
   public static final int START8_VALUE = 222;
   public static final int START9_VALUE = 223;
   public static final int START10_VALUE = 224;
   public static final int TEXT11_VALUE = 225;
   public static final int TEXT12_VALUE = 226;
   public static final int TEXT13_VALUE = 227;
   public static final int TEXT14_VALUE = 228;
   public static final int TEXT15_VALUE = 229;
   public static final int TEXT16_VALUE = 230;
   public static final int TEXT17_VALUE = 231;
   public static final int TEXT18_VALUE = 232;
   public static final int TEXT19_VALUE = 233;
   public static final int TEXT20_VALUE = 234;
   public static final int TEXT21_VALUE = 235;
   public static final int TEXT22_VALUE = 236;
   public static final int TEXT23_VALUE = 237;
   public static final int TEXT24_VALUE = 238;
   public static final int TEXT25_VALUE = 239;
   public static final int TEXT26_VALUE = 240;
   public static final int TEXT27_VALUE = 241;
   public static final int TEXT28_VALUE = 242;
   public static final int TEXT29_VALUE = 243;
   public static final int TEXT30_VALUE = 244;
   public static final int PHONETICS_VALUE = 252;
   public static final int ASSIGNMENT_DELAY_VALUE = 257;
   public static final int ASSIGNMENT_UNITS_VALUE = 258;
   public static final int BASELINE_START_VALUE = 259;
   public static final int BASELINE_FINISH_VALUE = 260;
   public static final int CONFIRMED_VALUE = 261;
   public static final int FINISH_VALUE = 262;
   public static final int LEVELING_DELAY_VALUE = 263;
   public static final int RESPONSE_PENDING_VALUE = 264;
   public static final int START_VALUE = 265;
   public static final int TEAMSTATUS_PENDING_VALUE = 266;
   public static final int CV_VALUE = 268;
   public static final int UPDATE_NEEDED_VALUE = 267;
   public static final int COST_RATE_TABLE_VALUE = 269;
   public static final int ACTUAL_START_VALUE = 270;
   public static final int ACTUAL_FINISH_VALUE = 271;
   public static final int WORKGROUP_VALUE = 272;
   public static final int PROJECT_VALUE = 273;
   public static final int OUTLINE_CODE1_VALUE = 278;
   public static final int OUTLINE_CODE2_VALUE = 280;
   public static final int OUTLINE_CODE3_VALUE = 282;
   public static final int OUTLINE_CODE4_VALUE = 284;
   public static final int OUTLINE_CODE5_VALUE = 286;
   public static final int OUTLINE_CODE6_VALUE = 288;
   public static final int OUTLINE_CODE7_VALUE = 290;
   public static final int OUTLINE_CODE8_VALUE = 292;
   public static final int OUTLINE_CODE9_VALUE = 294;
   public static final int OUTLINE_CODE10_VALUE = 296;
   public static final int MATERIAL_LABEL_VALUE = 299;
   public static final int TYPE_VALUE = 300;
   public static final int VAC_VALUE = 301;
   public static final int GROUP_BY_SUMMARY_VALUE = 306;
   public static final int WINDOWS_USER_ACCOUNT_VALUE = 311;

   public static final int MAX_VALUE = 312;
   
   private static final ResourceField[] FIELD_ARRAY = new ResourceField[MAX_VALUE];

   static
   {
      FIELD_ARRAY[ID_VALUE] = ResourceField.ID;
      FIELD_ARRAY[NAME_VALUE] = ResourceField.NAME;
      FIELD_ARRAY[INITIALS_VALUE] = ResourceField.INITIALS;
      FIELD_ARRAY[GROUP_VALUE] = ResourceField.GROUP;
      FIELD_ARRAY[MAX_UNITS_VALUE] = ResourceField.MAX_UNITS;
      FIELD_ARRAY[BASE_CALENDAR_VALUE] = ResourceField.BASE_CALENDAR;
      FIELD_ARRAY[STANDARD_RATE_VALUE] = ResourceField.STANDARD_RATE;
      FIELD_ARRAY[OVERTIME_RATE_VALUE] = ResourceField.OVERTIME_RATE;
      FIELD_ARRAY[TEXT1_VALUE] = ResourceField.TEXT1;
      FIELD_ARRAY[TEXT2_VALUE] = ResourceField.TEXT2;
      FIELD_ARRAY[CODE_VALUE] = ResourceField.CODE;
      FIELD_ARRAY[ACTUAL_COST_VALUE] = ResourceField.ACTUAL_COST;
      FIELD_ARRAY[COST_VALUE] = ResourceField.COST;
      FIELD_ARRAY[WORK_VALUE] = ResourceField.WORK;
      FIELD_ARRAY[ACTUAL_WORK_VALUE] = ResourceField.ACTUAL_WORK;
      FIELD_ARRAY[BASELINE_WORK_VALUE] = ResourceField.BASELINE_WORK;
      FIELD_ARRAY[OVERTIME_WORK_VALUE] = ResourceField.OVERTIME_WORK;
      FIELD_ARRAY[BASELINE_COST_VALUE] = ResourceField.BASELINE_COST;
      FIELD_ARRAY[COST_PER_USE_VALUE] = ResourceField.COST_PER_USE;
      FIELD_ARRAY[ACCRUE_AT_VALUE] = ResourceField.ACCRUE_AT;
      FIELD_ARRAY[REMAINING_COST_VALUE] = ResourceField.REMAINING_COST;
      FIELD_ARRAY[REMAINING_WORK_VALUE] = ResourceField.REMAINING_WORK;
      FIELD_ARRAY[WORK_VARIANCE_VALUE] = ResourceField.WORK_VARIANCE;
      FIELD_ARRAY[COST_VARIANCE_VALUE] = ResourceField.COST_VARIANCE;
      FIELD_ARRAY[OVERALLOCATED_VALUE] = ResourceField.OVERALLOCATED;
      FIELD_ARRAY[PEAK_VALUE] = ResourceField.PEAK;
      FIELD_ARRAY[UNIQUE_ID_VALUE] = ResourceField.UNIQUE_ID;
      FIELD_ARRAY[NOTES_VALUE] = ResourceField.NOTES;
      FIELD_ARRAY[PERCENT_WORK_COMPLETE_VALUE] = ResourceField.PERCENT_WORK_COMPLETE;
      FIELD_ARRAY[TEXT3_VALUE] = ResourceField.TEXT3;
      FIELD_ARRAY[TEXT4_VALUE] = ResourceField.TEXT4;
      FIELD_ARRAY[TEXT5_VALUE] = ResourceField.TEXT5;
      FIELD_ARRAY[OBJECTS_VALUE] = ResourceField.OBJECTS;
      FIELD_ARRAY[LINKED_FIELDS_VALUE] = ResourceField.LINKED_FIELDS;
      FIELD_ARRAY[EMAIL_ADDRESS_VALUE] = ResourceField.EMAIL_ADDRESS;
      FIELD_ARRAY[REGULAR_WORK_VALUE] = ResourceField.REGULAR_WORK;
      FIELD_ARRAY[ACTUAL_OVERTIME_WORK_VALUE] = ResourceField.ACTUAL_OVERTIME_WORK;
      FIELD_ARRAY[REMAINING_OVERTIME_WORK_VALUE] = ResourceField.REMAINING_OVERTIME_WORK;
      FIELD_ARRAY[OVERTIME_COST_VALUE] = ResourceField.OVERTIME_COST;
      FIELD_ARRAY[ACTUAL_OVERTIME_COST_VALUE] = ResourceField.ACTUAL_OVERTIME_COST;
      FIELD_ARRAY[REMAINING_OVERTIME_COST_VALUE] = ResourceField.REMAINING_OVERTIME_COST;
      FIELD_ARRAY[BCWS_VALUE] = ResourceField.BCWS;
      FIELD_ARRAY[BCWP_VALUE] = ResourceField.BCWP;
      FIELD_ARRAY[ACWP_VALUE] = ResourceField.ACWP;
      FIELD_ARRAY[SV_VALUE] = ResourceField.SV;
      FIELD_ARRAY[AVAILABLE_FROM_VALUE] = ResourceField.AVAILABLE_FROM;
      FIELD_ARRAY[AVAILABLE_TO_VALUE] = ResourceField.AVAILABLE_TO;
      FIELD_ARRAY[INDICATORS_VALUE] = ResourceField.INDICATORS;
      FIELD_ARRAY[TEXT6_VALUE] = ResourceField.TEXT6;
      FIELD_ARRAY[TEXT7_VALUE] = ResourceField.TEXT7;
      FIELD_ARRAY[TEXT8_VALUE] = ResourceField.TEXT8;
      FIELD_ARRAY[TEXT9_VALUE] = ResourceField.TEXT9;
      FIELD_ARRAY[TEXT10_VALUE] = ResourceField.TEXT10;
      FIELD_ARRAY[START1_VALUE] = ResourceField.START1;
      FIELD_ARRAY[START2_VALUE] = ResourceField.START2;
      FIELD_ARRAY[START3_VALUE] = ResourceField.START3;
      FIELD_ARRAY[START4_VALUE] = ResourceField.START4;
      FIELD_ARRAY[START5_VALUE] = ResourceField.START5;
      FIELD_ARRAY[FINISH1_VALUE] = ResourceField.FINISH1;
      FIELD_ARRAY[FINISH2_VALUE] = ResourceField.FINISH2;
      FIELD_ARRAY[FINISH3_VALUE] = ResourceField.FINISH3;
      FIELD_ARRAY[FINISH4_VALUE] = ResourceField.FINISH4;
      FIELD_ARRAY[FINISH5_VALUE] = ResourceField.FINISH5;
      FIELD_ARRAY[NUMBER1_VALUE] = ResourceField.NUMBER1;
      FIELD_ARRAY[NUMBER2_VALUE] = ResourceField.NUMBER2;
      FIELD_ARRAY[NUMBER3_VALUE] = ResourceField.NUMBER3;
      FIELD_ARRAY[NUMBER4_VALUE] = ResourceField.NUMBER4;
      FIELD_ARRAY[NUMBER5_VALUE] = ResourceField.NUMBER5;
      FIELD_ARRAY[DURATION1_VALUE] = ResourceField.DURATION1;
      FIELD_ARRAY[DURATION2_VALUE] = ResourceField.DURATION2;
      FIELD_ARRAY[DURATION3_VALUE] = ResourceField.DURATION3;
      FIELD_ARRAY[COST1_VALUE] = ResourceField.COST1;
      FIELD_ARRAY[COST2_VALUE] = ResourceField.COST2;
      FIELD_ARRAY[COST3_VALUE] = ResourceField.COST3;
      FIELD_ARRAY[FLAG10_VALUE] = ResourceField.FLAG10;
      FIELD_ARRAY[FLAG1_VALUE] = ResourceField.FLAG1;
      FIELD_ARRAY[FLAG2_VALUE] = ResourceField.FLAG2;
      FIELD_ARRAY[FLAG3_VALUE] = ResourceField.FLAG3;
      FIELD_ARRAY[FLAG4_VALUE] = ResourceField.FLAG4;
      FIELD_ARRAY[FLAG5_VALUE] = ResourceField.FLAG5;
      FIELD_ARRAY[FLAG6_VALUE] = ResourceField.FLAG6;
      FIELD_ARRAY[FLAG7_VALUE] = ResourceField.FLAG7;
      FIELD_ARRAY[FLAG8_VALUE] = ResourceField.FLAG8;
      FIELD_ARRAY[FLAG9_VALUE] = ResourceField.FLAG9;
      FIELD_ARRAY[HYPERLINK_VALUE] = ResourceField.HYPERLINK;
      FIELD_ARRAY[HYPERLINK_ADDRESS_VALUE] = ResourceField.HYPERLINK_ADDRESS;
      FIELD_ARRAY[HYPERLINK_SUBADDRESS_VALUE] = ResourceField.HYPERLINK_SUBADDRESS;
      FIELD_ARRAY[HYPERLINK_HREF_VALUE] = ResourceField.HYPERLINK_HREF;
      FIELD_ARRAY[ASSIGNMENT_VALUE] = ResourceField.ASSIGNMENT;
      FIELD_ARRAY[TASK_SUMMARY_NAME_VALUE] = ResourceField.TASK_SUMMARY_NAME;
      FIELD_ARRAY[CAN_LEVEL_VALUE] = ResourceField.CAN_LEVEL;
      FIELD_ARRAY[WORK_CONTOUR_VALUE] = ResourceField.WORK_CONTOUR;
      FIELD_ARRAY[COST4_VALUE] = ResourceField.COST4;
      FIELD_ARRAY[COST5_VALUE] = ResourceField.COST5;
      FIELD_ARRAY[COST6_VALUE] = ResourceField.COST6;
      FIELD_ARRAY[COST7_VALUE] = ResourceField.COST7;
      FIELD_ARRAY[COST8_VALUE] = ResourceField.COST8;
      FIELD_ARRAY[COST9_VALUE] = ResourceField.COST9;
      FIELD_ARRAY[COST10_VALUE] = ResourceField.COST10;
      FIELD_ARRAY[DATE1_VALUE] = ResourceField.DATE1;
      FIELD_ARRAY[DATE2_VALUE] = ResourceField.DATE2;
      FIELD_ARRAY[DATE3_VALUE] = ResourceField.DATE3;
      FIELD_ARRAY[DATE4_VALUE] = ResourceField.DATE4;
      FIELD_ARRAY[DATE5_VALUE] = ResourceField.DATE5;
      FIELD_ARRAY[DATE6_VALUE] = ResourceField.DATE6;
      FIELD_ARRAY[DATE7_VALUE] = ResourceField.DATE7;
      FIELD_ARRAY[DATE8_VALUE] = ResourceField.DATE8;
      FIELD_ARRAY[DATE9_VALUE] = ResourceField.DATE9;
      FIELD_ARRAY[DATE10_VALUE] = ResourceField.DATE10;
      FIELD_ARRAY[DURATION4_VALUE] = ResourceField.DURATION4;
      FIELD_ARRAY[DURATION5_VALUE] = ResourceField.DURATION5;
      FIELD_ARRAY[DURATION6_VALUE] = ResourceField.DURATION6;
      FIELD_ARRAY[DURATION7_VALUE] = ResourceField.DURATION7;
      FIELD_ARRAY[DURATION8_VALUE] = ResourceField.DURATION8;
      FIELD_ARRAY[DURATION9_VALUE] = ResourceField.DURATION9;
      FIELD_ARRAY[DURATION10_VALUE] = ResourceField.DURATION10;
      FIELD_ARRAY[FINISH6_VALUE] = ResourceField.FINISH6;
      FIELD_ARRAY[FINISH7_VALUE] = ResourceField.FINISH7;
      FIELD_ARRAY[FINISH8_VALUE] = ResourceField.FINISH8;
      FIELD_ARRAY[FINISH9_VALUE] = ResourceField.FINISH9;
      FIELD_ARRAY[FINISH10_VALUE] = ResourceField.FINISH10;
      FIELD_ARRAY[FLAG11_VALUE] = ResourceField.FLAG11;
      FIELD_ARRAY[FLAG12_VALUE] = ResourceField.FLAG12;
      FIELD_ARRAY[FLAG13_VALUE] = ResourceField.FLAG13;
      FIELD_ARRAY[FLAG14_VALUE] = ResourceField.FLAG14;
      FIELD_ARRAY[FLAG15_VALUE] = ResourceField.FLAG15;
      FIELD_ARRAY[FLAG16_VALUE] = ResourceField.FLAG16;
      FIELD_ARRAY[FLAG17_VALUE] = ResourceField.FLAG17;
      FIELD_ARRAY[FLAG18_VALUE] = ResourceField.FLAG18;
      FIELD_ARRAY[FLAG19_VALUE] = ResourceField.FLAG19;
      FIELD_ARRAY[FLAG20_VALUE] = ResourceField.FLAG20;
      FIELD_ARRAY[NUMBER6_VALUE] = ResourceField.NUMBER6;
      FIELD_ARRAY[NUMBER7_VALUE] = ResourceField.NUMBER7;
      FIELD_ARRAY[NUMBER8_VALUE] = ResourceField.NUMBER8;
      FIELD_ARRAY[NUMBER9_VALUE] = ResourceField.NUMBER9;
      FIELD_ARRAY[NUMBER10_VALUE] = ResourceField.NUMBER10;
      FIELD_ARRAY[NUMBER11_VALUE] = ResourceField.NUMBER11;
      FIELD_ARRAY[NUMBER12_VALUE] = ResourceField.NUMBER12;
      FIELD_ARRAY[NUMBER13_VALUE] = ResourceField.NUMBER13;
      FIELD_ARRAY[NUMBER14_VALUE] = ResourceField.NUMBER14;
      FIELD_ARRAY[NUMBER15_VALUE] = ResourceField.NUMBER15;
      FIELD_ARRAY[NUMBER16_VALUE] = ResourceField.NUMBER16;
      FIELD_ARRAY[NUMBER17_VALUE] = ResourceField.NUMBER17;
      FIELD_ARRAY[NUMBER18_VALUE] = ResourceField.NUMBER18;
      FIELD_ARRAY[NUMBER19_VALUE] = ResourceField.NUMBER19;
      FIELD_ARRAY[NUMBER20_VALUE] = ResourceField.NUMBER20;
      FIELD_ARRAY[START6_VALUE] = ResourceField.START6;
      FIELD_ARRAY[START7_VALUE] = ResourceField.START7;
      FIELD_ARRAY[START8_VALUE] = ResourceField.START8;
      FIELD_ARRAY[START9_VALUE] = ResourceField.START9;
      FIELD_ARRAY[START10_VALUE] = ResourceField.START10;
      FIELD_ARRAY[TEXT11_VALUE] = ResourceField.TEXT11;
      FIELD_ARRAY[TEXT12_VALUE] = ResourceField.TEXT12;
      FIELD_ARRAY[TEXT13_VALUE] = ResourceField.TEXT13;
      FIELD_ARRAY[TEXT14_VALUE] = ResourceField.TEXT14;
      FIELD_ARRAY[TEXT15_VALUE] = ResourceField.TEXT15;
      FIELD_ARRAY[TEXT16_VALUE] = ResourceField.TEXT16;
      FIELD_ARRAY[TEXT17_VALUE] = ResourceField.TEXT17;
      FIELD_ARRAY[TEXT18_VALUE] = ResourceField.TEXT18;
      FIELD_ARRAY[TEXT19_VALUE] = ResourceField.TEXT19;
      FIELD_ARRAY[TEXT20_VALUE] = ResourceField.TEXT20;
      FIELD_ARRAY[TEXT21_VALUE] = ResourceField.TEXT21;
      FIELD_ARRAY[TEXT22_VALUE] = ResourceField.TEXT22;
      FIELD_ARRAY[TEXT23_VALUE] = ResourceField.TEXT23;
      FIELD_ARRAY[TEXT24_VALUE] = ResourceField.TEXT24;
      FIELD_ARRAY[TEXT25_VALUE] = ResourceField.TEXT25;
      FIELD_ARRAY[TEXT26_VALUE] = ResourceField.TEXT26;
      FIELD_ARRAY[TEXT27_VALUE] = ResourceField.TEXT27;
      FIELD_ARRAY[TEXT28_VALUE] = ResourceField.TEXT28;
      FIELD_ARRAY[TEXT29_VALUE] = ResourceField.TEXT29;
      FIELD_ARRAY[TEXT30_VALUE] = ResourceField.TEXT30;
      FIELD_ARRAY[PHONETICS_VALUE] = ResourceField.PHONETICS;
      FIELD_ARRAY[ASSIGNMENT_DELAY_VALUE] = ResourceField.ASSIGNMENT_DELAY;
      FIELD_ARRAY[ASSIGNMENT_UNITS_VALUE] = ResourceField.ASSIGNMENT_UNITS;
      FIELD_ARRAY[BASELINE_START_VALUE] = ResourceField.BASELINE_START;
      FIELD_ARRAY[BASELINE_FINISH_VALUE] = ResourceField.BASELINE_FINISH;
      FIELD_ARRAY[CONFIRMED_VALUE] = ResourceField.CONFIRMED;
      FIELD_ARRAY[FINISH_VALUE] = ResourceField.FINISH;
      FIELD_ARRAY[LEVELING_DELAY_VALUE] = ResourceField.LEVELING_DELAY;
      FIELD_ARRAY[RESPONSE_PENDING_VALUE] = ResourceField.RESPONSE_PENDING;
      FIELD_ARRAY[START_VALUE] = ResourceField.START;
      FIELD_ARRAY[TEAMSTATUS_PENDING_VALUE] = ResourceField.TEAMSTATUS_PENDING;
      FIELD_ARRAY[CV_VALUE] = ResourceField.CV;
      FIELD_ARRAY[UPDATE_NEEDED_VALUE] = ResourceField.UPDATE_NEEDED;
      FIELD_ARRAY[COST_RATE_TABLE_VALUE] = ResourceField.COST_RATE_TABLE;
      FIELD_ARRAY[ACTUAL_START_VALUE] = ResourceField.ACTUAL_START;
      FIELD_ARRAY[ACTUAL_FINISH_VALUE] = ResourceField.ACTUAL_FINISH;
      FIELD_ARRAY[WORKGROUP_VALUE] = ResourceField.WORKGROUP;
      FIELD_ARRAY[PROJECT_VALUE] = ResourceField.PROJECT;
      FIELD_ARRAY[OUTLINE_CODE1_VALUE] = ResourceField.OUTLINE_CODE1;
      FIELD_ARRAY[OUTLINE_CODE2_VALUE] = ResourceField.OUTLINE_CODE2;
      FIELD_ARRAY[OUTLINE_CODE3_VALUE] = ResourceField.OUTLINE_CODE3;
      FIELD_ARRAY[OUTLINE_CODE4_VALUE] = ResourceField.OUTLINE_CODE4;
      FIELD_ARRAY[OUTLINE_CODE5_VALUE] = ResourceField.OUTLINE_CODE5;
      FIELD_ARRAY[OUTLINE_CODE6_VALUE] = ResourceField.OUTLINE_CODE6;
      FIELD_ARRAY[OUTLINE_CODE7_VALUE] = ResourceField.OUTLINE_CODE7;
      FIELD_ARRAY[OUTLINE_CODE8_VALUE] = ResourceField.OUTLINE_CODE8;
      FIELD_ARRAY[OUTLINE_CODE9_VALUE] = ResourceField.OUTLINE_CODE9;
      FIELD_ARRAY[OUTLINE_CODE10_VALUE] = ResourceField.OUTLINE_CODE10;
      FIELD_ARRAY[MATERIAL_LABEL_VALUE] = ResourceField.MATERIAL_LABEL;
      FIELD_ARRAY[TYPE_VALUE] = ResourceField.TYPE;
      FIELD_ARRAY[VAC_VALUE] = ResourceField.VAC;
      FIELD_ARRAY[GROUP_BY_SUMMARY_VALUE] = ResourceField.GROUP_BY_SUMMARY;
      FIELD_ARRAY[WINDOWS_USER_ACCOUNT_VALUE] = ResourceField.WINDOWS_USER_ACCOUNT;
   }

   
   private static final int[] ID_ARRAY = new int[MAX_VALUE];

   static
   {
      ID_ARRAY[ResourceField.ID_VALUE] = ID_VALUE;
      ID_ARRAY[ResourceField.NAME_VALUE] = NAME_VALUE;
      ID_ARRAY[ResourceField.INITIALS_VALUE] = INITIALS_VALUE;
      ID_ARRAY[ResourceField.GROUP_VALUE] = GROUP_VALUE;
      ID_ARRAY[ResourceField.MAX_UNITS_VALUE] = MAX_UNITS_VALUE;
      ID_ARRAY[ResourceField.BASE_CALENDAR_VALUE] = BASE_CALENDAR_VALUE;
      ID_ARRAY[ResourceField.STANDARD_RATE_VALUE] = STANDARD_RATE_VALUE;
      ID_ARRAY[ResourceField.OVERTIME_RATE_VALUE] = OVERTIME_RATE_VALUE;
      ID_ARRAY[ResourceField.TEXT1_VALUE] = TEXT1_VALUE;
      ID_ARRAY[ResourceField.TEXT2_VALUE] = TEXT2_VALUE;
      ID_ARRAY[ResourceField.CODE_VALUE] = CODE_VALUE;
      ID_ARRAY[ResourceField.ACTUAL_COST_VALUE] = ACTUAL_COST_VALUE;
      ID_ARRAY[ResourceField.COST_VALUE] = COST_VALUE;
      ID_ARRAY[ResourceField.WORK_VALUE] = WORK_VALUE;
      ID_ARRAY[ResourceField.ACTUAL_WORK_VALUE] = ACTUAL_WORK_VALUE;
      ID_ARRAY[ResourceField.BASELINE_WORK_VALUE] = BASELINE_WORK_VALUE;
      ID_ARRAY[ResourceField.OVERTIME_WORK_VALUE] = OVERTIME_WORK_VALUE;
      ID_ARRAY[ResourceField.BASELINE_COST_VALUE] = BASELINE_COST_VALUE;
      ID_ARRAY[ResourceField.COST_PER_USE_VALUE] = COST_PER_USE_VALUE;
      ID_ARRAY[ResourceField.ACCRUE_AT_VALUE] = ACCRUE_AT_VALUE;
      ID_ARRAY[ResourceField.REMAINING_COST_VALUE] = REMAINING_COST_VALUE;
      ID_ARRAY[ResourceField.REMAINING_WORK_VALUE] = REMAINING_WORK_VALUE;
      ID_ARRAY[ResourceField.WORK_VARIANCE_VALUE] = WORK_VARIANCE_VALUE;
      ID_ARRAY[ResourceField.COST_VARIANCE_VALUE] = COST_VARIANCE_VALUE;
      ID_ARRAY[ResourceField.OVERALLOCATED_VALUE] = OVERALLOCATED_VALUE;
      ID_ARRAY[ResourceField.PEAK_VALUE] = PEAK_VALUE;
      ID_ARRAY[ResourceField.UNIQUE_ID_VALUE] = UNIQUE_ID_VALUE;
      ID_ARRAY[ResourceField.NOTES_VALUE] = NOTES_VALUE;
      ID_ARRAY[ResourceField.PERCENT_WORK_COMPLETE_VALUE] = PERCENT_WORK_COMPLETE_VALUE;
      ID_ARRAY[ResourceField.TEXT3_VALUE] = TEXT3_VALUE;
      ID_ARRAY[ResourceField.TEXT4_VALUE] = TEXT4_VALUE;
      ID_ARRAY[ResourceField.TEXT5_VALUE] = TEXT5_VALUE;
      ID_ARRAY[ResourceField.OBJECTS_VALUE] = OBJECTS_VALUE;
      ID_ARRAY[ResourceField.LINKED_FIELDS_VALUE] = LINKED_FIELDS_VALUE;
      ID_ARRAY[ResourceField.EMAIL_ADDRESS_VALUE] = EMAIL_ADDRESS_VALUE;
      ID_ARRAY[ResourceField.REGULAR_WORK_VALUE] = REGULAR_WORK_VALUE;
      ID_ARRAY[ResourceField.ACTUAL_OVERTIME_WORK_VALUE] = ACTUAL_OVERTIME_WORK_VALUE;
      ID_ARRAY[ResourceField.REMAINING_OVERTIME_WORK_VALUE] = REMAINING_OVERTIME_WORK_VALUE;
      ID_ARRAY[ResourceField.OVERTIME_COST_VALUE] = OVERTIME_COST_VALUE;
      ID_ARRAY[ResourceField.ACTUAL_OVERTIME_COST_VALUE] = ACTUAL_OVERTIME_COST_VALUE;
      ID_ARRAY[ResourceField.REMAINING_OVERTIME_COST_VALUE] = REMAINING_OVERTIME_COST_VALUE;
      ID_ARRAY[ResourceField.BCWS_VALUE] = BCWS_VALUE;
      ID_ARRAY[ResourceField.BCWP_VALUE] = BCWP_VALUE;
      ID_ARRAY[ResourceField.ACWP_VALUE] = ACWP_VALUE;
      ID_ARRAY[ResourceField.SV_VALUE] = SV_VALUE;
      ID_ARRAY[ResourceField.AVAILABLE_FROM_VALUE] = AVAILABLE_FROM_VALUE;
      ID_ARRAY[ResourceField.AVAILABLE_TO_VALUE] = AVAILABLE_TO_VALUE;
      ID_ARRAY[ResourceField.INDICATORS_VALUE] = INDICATORS_VALUE;
      ID_ARRAY[ResourceField.TEXT6_VALUE] = TEXT6_VALUE;
      ID_ARRAY[ResourceField.TEXT7_VALUE] = TEXT7_VALUE;
      ID_ARRAY[ResourceField.TEXT8_VALUE] = TEXT8_VALUE;
      ID_ARRAY[ResourceField.TEXT9_VALUE] = TEXT9_VALUE;
      ID_ARRAY[ResourceField.TEXT10_VALUE] = TEXT10_VALUE;
      ID_ARRAY[ResourceField.START1_VALUE] = START1_VALUE;
      ID_ARRAY[ResourceField.START2_VALUE] = START2_VALUE;
      ID_ARRAY[ResourceField.START3_VALUE] = START3_VALUE;
      ID_ARRAY[ResourceField.START4_VALUE] = START4_VALUE;
      ID_ARRAY[ResourceField.START5_VALUE] = START5_VALUE;
      ID_ARRAY[ResourceField.FINISH1_VALUE] = FINISH1_VALUE;
      ID_ARRAY[ResourceField.FINISH2_VALUE] = FINISH2_VALUE;
      ID_ARRAY[ResourceField.FINISH3_VALUE] = FINISH3_VALUE;
      ID_ARRAY[ResourceField.FINISH4_VALUE] = FINISH4_VALUE;
      ID_ARRAY[ResourceField.FINISH5_VALUE] = FINISH5_VALUE;
      ID_ARRAY[ResourceField.NUMBER1_VALUE] = NUMBER1_VALUE;
      ID_ARRAY[ResourceField.NUMBER2_VALUE] = NUMBER2_VALUE;
      ID_ARRAY[ResourceField.NUMBER3_VALUE] = NUMBER3_VALUE;
      ID_ARRAY[ResourceField.NUMBER4_VALUE] = NUMBER4_VALUE;
      ID_ARRAY[ResourceField.NUMBER5_VALUE] = NUMBER5_VALUE;
      ID_ARRAY[ResourceField.DURATION1_VALUE] = DURATION1_VALUE;
      ID_ARRAY[ResourceField.DURATION2_VALUE] = DURATION2_VALUE;
      ID_ARRAY[ResourceField.DURATION3_VALUE] = DURATION3_VALUE;
      ID_ARRAY[ResourceField.COST1_VALUE] = COST1_VALUE;
      ID_ARRAY[ResourceField.COST2_VALUE] = COST2_VALUE;
      ID_ARRAY[ResourceField.COST3_VALUE] = COST3_VALUE;
      ID_ARRAY[ResourceField.FLAG10_VALUE] = FLAG10_VALUE;
      ID_ARRAY[ResourceField.FLAG1_VALUE] = FLAG1_VALUE;
      ID_ARRAY[ResourceField.FLAG2_VALUE] = FLAG2_VALUE;
      ID_ARRAY[ResourceField.FLAG3_VALUE] = FLAG3_VALUE;
      ID_ARRAY[ResourceField.FLAG4_VALUE] = FLAG4_VALUE;
      ID_ARRAY[ResourceField.FLAG5_VALUE] = FLAG5_VALUE;
      ID_ARRAY[ResourceField.FLAG6_VALUE] = FLAG6_VALUE;
      ID_ARRAY[ResourceField.FLAG7_VALUE] = FLAG7_VALUE;
      ID_ARRAY[ResourceField.FLAG8_VALUE] = FLAG8_VALUE;
      ID_ARRAY[ResourceField.FLAG9_VALUE] = FLAG9_VALUE;
      ID_ARRAY[ResourceField.HYPERLINK_VALUE] = HYPERLINK_VALUE;
      ID_ARRAY[ResourceField.HYPERLINK_ADDRESS_VALUE] = HYPERLINK_ADDRESS_VALUE;
      ID_ARRAY[ResourceField.HYPERLINK_SUBADDRESS_VALUE] = HYPERLINK_SUBADDRESS_VALUE;
      ID_ARRAY[ResourceField.HYPERLINK_HREF_VALUE] = HYPERLINK_HREF_VALUE;
      ID_ARRAY[ResourceField.ASSIGNMENT_VALUE] = ASSIGNMENT_VALUE;
      ID_ARRAY[ResourceField.TASK_SUMMARY_NAME_VALUE] = TASK_SUMMARY_NAME_VALUE;
      ID_ARRAY[ResourceField.CAN_LEVEL_VALUE] = CAN_LEVEL_VALUE;
      ID_ARRAY[ResourceField.WORK_CONTOUR_VALUE] = WORK_CONTOUR_VALUE;
      ID_ARRAY[ResourceField.COST4_VALUE] = COST4_VALUE;
      ID_ARRAY[ResourceField.COST5_VALUE] = COST5_VALUE;
      ID_ARRAY[ResourceField.COST6_VALUE] = COST6_VALUE;
      ID_ARRAY[ResourceField.COST7_VALUE] = COST7_VALUE;
      ID_ARRAY[ResourceField.COST8_VALUE] = COST8_VALUE;
      ID_ARRAY[ResourceField.COST9_VALUE] = COST9_VALUE;
      ID_ARRAY[ResourceField.COST10_VALUE] = COST10_VALUE;
      ID_ARRAY[ResourceField.DATE1_VALUE] = DATE1_VALUE;
      ID_ARRAY[ResourceField.DATE2_VALUE] = DATE2_VALUE;
      ID_ARRAY[ResourceField.DATE3_VALUE] = DATE3_VALUE;
      ID_ARRAY[ResourceField.DATE4_VALUE] = DATE4_VALUE;
      ID_ARRAY[ResourceField.DATE5_VALUE] = DATE5_VALUE;
      ID_ARRAY[ResourceField.DATE6_VALUE] = DATE6_VALUE;
      ID_ARRAY[ResourceField.DATE7_VALUE] = DATE7_VALUE;
      ID_ARRAY[ResourceField.DATE8_VALUE] = DATE8_VALUE;
      ID_ARRAY[ResourceField.DATE9_VALUE] = DATE9_VALUE;
      ID_ARRAY[ResourceField.DATE10_VALUE] = DATE10_VALUE;
      ID_ARRAY[ResourceField.DURATION4_VALUE] = DURATION4_VALUE;
      ID_ARRAY[ResourceField.DURATION5_VALUE] = DURATION5_VALUE;
      ID_ARRAY[ResourceField.DURATION6_VALUE] = DURATION6_VALUE;
      ID_ARRAY[ResourceField.DURATION7_VALUE] = DURATION7_VALUE;
      ID_ARRAY[ResourceField.DURATION8_VALUE] = DURATION8_VALUE;
      ID_ARRAY[ResourceField.DURATION9_VALUE] = DURATION9_VALUE;
      ID_ARRAY[ResourceField.DURATION10_VALUE] = DURATION10_VALUE;
      ID_ARRAY[ResourceField.FINISH6_VALUE] = FINISH6_VALUE;
      ID_ARRAY[ResourceField.FINISH7_VALUE] = FINISH7_VALUE;
      ID_ARRAY[ResourceField.FINISH8_VALUE] = FINISH8_VALUE;
      ID_ARRAY[ResourceField.FINISH9_VALUE] = FINISH9_VALUE;
      ID_ARRAY[ResourceField.FINISH10_VALUE] = FINISH10_VALUE;
      ID_ARRAY[ResourceField.FLAG11_VALUE] = FLAG11_VALUE;
      ID_ARRAY[ResourceField.FLAG12_VALUE] = FLAG12_VALUE;
      ID_ARRAY[ResourceField.FLAG13_VALUE] = FLAG13_VALUE;
      ID_ARRAY[ResourceField.FLAG14_VALUE] = FLAG14_VALUE;
      ID_ARRAY[ResourceField.FLAG15_VALUE] = FLAG15_VALUE;
      ID_ARRAY[ResourceField.FLAG16_VALUE] = FLAG16_VALUE;
      ID_ARRAY[ResourceField.FLAG17_VALUE] = FLAG17_VALUE;
      ID_ARRAY[ResourceField.FLAG18_VALUE] = FLAG18_VALUE;
      ID_ARRAY[ResourceField.FLAG19_VALUE] = FLAG19_VALUE;
      ID_ARRAY[ResourceField.FLAG20_VALUE] = FLAG20_VALUE;
      ID_ARRAY[ResourceField.NUMBER6_VALUE] = NUMBER6_VALUE;
      ID_ARRAY[ResourceField.NUMBER7_VALUE] = NUMBER7_VALUE;
      ID_ARRAY[ResourceField.NUMBER8_VALUE] = NUMBER8_VALUE;
      ID_ARRAY[ResourceField.NUMBER9_VALUE] = NUMBER9_VALUE;
      ID_ARRAY[ResourceField.NUMBER10_VALUE] = NUMBER10_VALUE;
      ID_ARRAY[ResourceField.NUMBER11_VALUE] = NUMBER11_VALUE;
      ID_ARRAY[ResourceField.NUMBER12_VALUE] = NUMBER12_VALUE;
      ID_ARRAY[ResourceField.NUMBER13_VALUE] = NUMBER13_VALUE;
      ID_ARRAY[ResourceField.NUMBER14_VALUE] = NUMBER14_VALUE;
      ID_ARRAY[ResourceField.NUMBER15_VALUE] = NUMBER15_VALUE;
      ID_ARRAY[ResourceField.NUMBER16_VALUE] = NUMBER16_VALUE;
      ID_ARRAY[ResourceField.NUMBER17_VALUE] = NUMBER17_VALUE;
      ID_ARRAY[ResourceField.NUMBER18_VALUE] = NUMBER18_VALUE;
      ID_ARRAY[ResourceField.NUMBER19_VALUE] = NUMBER19_VALUE;
      ID_ARRAY[ResourceField.NUMBER20_VALUE] = NUMBER20_VALUE;
      ID_ARRAY[ResourceField.START6_VALUE] = START6_VALUE;
      ID_ARRAY[ResourceField.START7_VALUE] = START7_VALUE;
      ID_ARRAY[ResourceField.START8_VALUE] = START8_VALUE;
      ID_ARRAY[ResourceField.START9_VALUE] = START9_VALUE;
      ID_ARRAY[ResourceField.START10_VALUE] = START10_VALUE;
      ID_ARRAY[ResourceField.TEXT11_VALUE] = TEXT11_VALUE;
      ID_ARRAY[ResourceField.TEXT12_VALUE] = TEXT12_VALUE;
      ID_ARRAY[ResourceField.TEXT13_VALUE] = TEXT13_VALUE;
      ID_ARRAY[ResourceField.TEXT14_VALUE] = TEXT14_VALUE;
      ID_ARRAY[ResourceField.TEXT15_VALUE] = TEXT15_VALUE;
      ID_ARRAY[ResourceField.TEXT16_VALUE] = TEXT16_VALUE;
      ID_ARRAY[ResourceField.TEXT17_VALUE] = TEXT17_VALUE;
      ID_ARRAY[ResourceField.TEXT18_VALUE] = TEXT18_VALUE;
      ID_ARRAY[ResourceField.TEXT19_VALUE] = TEXT19_VALUE;
      ID_ARRAY[ResourceField.TEXT20_VALUE] = TEXT20_VALUE;
      ID_ARRAY[ResourceField.TEXT21_VALUE] = TEXT21_VALUE;
      ID_ARRAY[ResourceField.TEXT22_VALUE] = TEXT22_VALUE;
      ID_ARRAY[ResourceField.TEXT23_VALUE] = TEXT23_VALUE;
      ID_ARRAY[ResourceField.TEXT24_VALUE] = TEXT24_VALUE;
      ID_ARRAY[ResourceField.TEXT25_VALUE] = TEXT25_VALUE;
      ID_ARRAY[ResourceField.TEXT26_VALUE] = TEXT26_VALUE;
      ID_ARRAY[ResourceField.TEXT27_VALUE] = TEXT27_VALUE;
      ID_ARRAY[ResourceField.TEXT28_VALUE] = TEXT28_VALUE;
      ID_ARRAY[ResourceField.TEXT29_VALUE] = TEXT29_VALUE;
      ID_ARRAY[ResourceField.TEXT30_VALUE] = TEXT30_VALUE;
      ID_ARRAY[ResourceField.PHONETICS_VALUE] = PHONETICS_VALUE;
      ID_ARRAY[ResourceField.ASSIGNMENT_DELAY_VALUE] = ASSIGNMENT_DELAY_VALUE;
      ID_ARRAY[ResourceField.ASSIGNMENT_UNITS_VALUE] = ASSIGNMENT_UNITS_VALUE;
      ID_ARRAY[ResourceField.BASELINE_START_VALUE] = BASELINE_START_VALUE;
      ID_ARRAY[ResourceField.BASELINE_FINISH_VALUE] = BASELINE_FINISH_VALUE;
      ID_ARRAY[ResourceField.CONFIRMED_VALUE] = CONFIRMED_VALUE;
      ID_ARRAY[ResourceField.FINISH_VALUE] = FINISH_VALUE;
      ID_ARRAY[ResourceField.LEVELING_DELAY_VALUE] = LEVELING_DELAY_VALUE;
      ID_ARRAY[ResourceField.RESPONSE_PENDING_VALUE] = RESPONSE_PENDING_VALUE;
      ID_ARRAY[ResourceField.START_VALUE] = START_VALUE;
      ID_ARRAY[ResourceField.TEAMSTATUS_PENDING_VALUE] = TEAMSTATUS_PENDING_VALUE;
      ID_ARRAY[ResourceField.CV_VALUE] = CV_VALUE;
      ID_ARRAY[ResourceField.UPDATE_NEEDED_VALUE] = UPDATE_NEEDED_VALUE;
      ID_ARRAY[ResourceField.COST_RATE_TABLE_VALUE] = COST_RATE_TABLE_VALUE;
      ID_ARRAY[ResourceField.ACTUAL_START_VALUE] = ACTUAL_START_VALUE;
      ID_ARRAY[ResourceField.ACTUAL_FINISH_VALUE] = ACTUAL_FINISH_VALUE;
      ID_ARRAY[ResourceField.WORKGROUP_VALUE] = WORKGROUP_VALUE;
      ID_ARRAY[ResourceField.PROJECT_VALUE] = PROJECT_VALUE;
      ID_ARRAY[ResourceField.OUTLINE_CODE1_VALUE] = OUTLINE_CODE1_VALUE;
      ID_ARRAY[ResourceField.OUTLINE_CODE2_VALUE] = OUTLINE_CODE2_VALUE;
      ID_ARRAY[ResourceField.OUTLINE_CODE3_VALUE] = OUTLINE_CODE3_VALUE;
      ID_ARRAY[ResourceField.OUTLINE_CODE4_VALUE] = OUTLINE_CODE4_VALUE;
      ID_ARRAY[ResourceField.OUTLINE_CODE5_VALUE] = OUTLINE_CODE5_VALUE;
      ID_ARRAY[ResourceField.OUTLINE_CODE6_VALUE] = OUTLINE_CODE6_VALUE;
      ID_ARRAY[ResourceField.OUTLINE_CODE7_VALUE] = OUTLINE_CODE7_VALUE;
      ID_ARRAY[ResourceField.OUTLINE_CODE8_VALUE] = OUTLINE_CODE8_VALUE;
      ID_ARRAY[ResourceField.OUTLINE_CODE9_VALUE] = OUTLINE_CODE9_VALUE;
      ID_ARRAY[ResourceField.OUTLINE_CODE10_VALUE] = OUTLINE_CODE10_VALUE;
      ID_ARRAY[ResourceField.MATERIAL_LABEL_VALUE] = MATERIAL_LABEL_VALUE;
      ID_ARRAY[ResourceField.TYPE_VALUE] = TYPE_VALUE;
      ID_ARRAY[ResourceField.VAC_VALUE] = VAC_VALUE;
      ID_ARRAY[ResourceField.GROUP_BY_SUMMARY_VALUE] = GROUP_BY_SUMMARY_VALUE;
      ID_ARRAY[ResourceField.WINDOWS_USER_ACCOUNT_VALUE] = WINDOWS_USER_ACCOUNT_VALUE;
   }
   
   public static final int RESOURCE_FIELD_BASE = 0x0C400000;      
}
