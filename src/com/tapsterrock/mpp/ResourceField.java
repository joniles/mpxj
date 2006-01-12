/*
 * file:       ResourceField.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2005
 * date:       26/04/2005
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

package com.tapsterrock.mpp;

import java.util.Locale;

import com.tapsterrock.mpx.FieldType;

/**
 * Instances of this type represent Resource fields.
 */
public final class ResourceField implements FieldType
{
   /**
    * Private constructor.
    * 
    * @param value task field value
    */
   private ResourceField (int value)
   {
      m_value = value;
   }

   /**
    * {@inheritDoc}
    */
   public String getName()
   {
      return (getName(Locale.getDefault()));
   }

   /**
    * {@inheritDoc}
    */
   public String getName (Locale locale)
   {      
      String[] titles = LocaleData.getStringArray(locale, LocaleData.RESOURCE_COLUMNS);
      String result = null;
      
      if (m_value >= 0 && m_value < titles.length)
      {
         result = titles[m_value];
      }
      
      return (result);
   }
   
   /**
    * {@inheritDoc}
    */
   public int getValue ()
   {
      return (m_value);
   }
   
   /**
    * Retrieve an instance of this class based on the data read from an
    * MS Project file.
    * 
    * @param value value from an MS Project file
    * @return instance of this class
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
    * Retrieves the string representation of this instance.
    * 
    * @return string representation
    */
   public String toString ()
   {
      return (getName());
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

   public static final ResourceField ID = new ResourceField(ID_VALUE);
   public static final ResourceField NAME = new ResourceField(NAME_VALUE);
   public static final ResourceField INITIALS = new ResourceField(INITIALS_VALUE);
   public static final ResourceField GROUP = new ResourceField(GROUP_VALUE);
   public static final ResourceField MAX_UNITS = new ResourceField(MAX_UNITS_VALUE);
   public static final ResourceField BASE_CALENDAR = new ResourceField(BASE_CALENDAR_VALUE);
   public static final ResourceField STANDARD_RATE = new ResourceField(STANDARD_RATE_VALUE);
   public static final ResourceField OVERTIME_RATE = new ResourceField(OVERTIME_RATE_VALUE);
   public static final ResourceField TEXT1 = new ResourceField(TEXT1_VALUE);
   public static final ResourceField TEXT2 = new ResourceField(TEXT2_VALUE);
   public static final ResourceField CODE = new ResourceField(CODE_VALUE);
   public static final ResourceField ACTUAL_COST = new ResourceField(ACTUAL_COST_VALUE);
   public static final ResourceField COST = new ResourceField(COST_VALUE);
   public static final ResourceField WORK = new ResourceField(WORK_VALUE);
   public static final ResourceField ACTUAL_WORK = new ResourceField(ACTUAL_WORK_VALUE);
   public static final ResourceField BASELINE_WORK = new ResourceField(BASELINE_WORK_VALUE);
   public static final ResourceField OVERTIME_WORK = new ResourceField(OVERTIME_WORK_VALUE);
   public static final ResourceField BASELINE_COST = new ResourceField(BASELINE_COST_VALUE);
   public static final ResourceField COST_PER_USE = new ResourceField(COST_PER_USE_VALUE);
   public static final ResourceField ACCRUE_AT = new ResourceField(ACCRUE_AT_VALUE);
   public static final ResourceField REMAINING_COST = new ResourceField(REMAINING_COST_VALUE);
   public static final ResourceField REMAINING_WORK = new ResourceField(REMAINING_WORK_VALUE);
   public static final ResourceField WORK_VARIANCE = new ResourceField(WORK_VARIANCE_VALUE);
   public static final ResourceField COST_VARIANCE = new ResourceField(COST_VARIANCE_VALUE);
   public static final ResourceField OVERALLOCATED = new ResourceField(OVERALLOCATED_VALUE);
   public static final ResourceField PEAK = new ResourceField(PEAK_VALUE);
   public static final ResourceField UNIQUE_ID = new ResourceField(UNIQUE_ID_VALUE);
   public static final ResourceField NOTES = new ResourceField(NOTES_VALUE);
   public static final ResourceField PERCENT_WORK_COMPLETE = new ResourceField(PERCENT_WORK_COMPLETE_VALUE);
   public static final ResourceField TEXT3 = new ResourceField(TEXT3_VALUE);
   public static final ResourceField TEXT4 = new ResourceField(TEXT4_VALUE);
   public static final ResourceField TEXT5 = new ResourceField(TEXT5_VALUE);
   public static final ResourceField OBJECTS = new ResourceField(OBJECTS_VALUE);
   public static final ResourceField LINKED_FIELDS = new ResourceField(LINKED_FIELDS_VALUE);
   public static final ResourceField EMAIL_ADDRESS = new ResourceField(EMAIL_ADDRESS_VALUE);
   public static final ResourceField REGULAR_WORK = new ResourceField(REGULAR_WORK_VALUE);
   public static final ResourceField ACTUAL_OVERTIME_WORK = new ResourceField(ACTUAL_OVERTIME_WORK_VALUE);
   public static final ResourceField REMAINING_OVERTIME_WORK = new ResourceField(REMAINING_OVERTIME_WORK_VALUE);
   public static final ResourceField OVERTIME_COST = new ResourceField(OVERTIME_COST_VALUE);
   public static final ResourceField ACTUAL_OVERTIME_COST = new ResourceField(ACTUAL_OVERTIME_COST_VALUE);
   public static final ResourceField REMAINING_OVERTIME_COST = new ResourceField(REMAINING_OVERTIME_COST_VALUE);
   public static final ResourceField BCWS = new ResourceField(BCWS_VALUE);
   public static final ResourceField BCWP = new ResourceField(BCWP_VALUE);
   public static final ResourceField ACWP = new ResourceField(ACWP_VALUE);
   public static final ResourceField SV = new ResourceField(SV_VALUE);
   public static final ResourceField AVAILABLE_FROM = new ResourceField(AVAILABLE_FROM_VALUE);
   public static final ResourceField AVAILABLE_TO = new ResourceField(AVAILABLE_TO_VALUE);
   public static final ResourceField INDICATORS = new ResourceField(INDICATORS_VALUE);
   public static final ResourceField TEXT6 = new ResourceField(TEXT6_VALUE);
   public static final ResourceField TEXT7 = new ResourceField(TEXT7_VALUE);
   public static final ResourceField TEXT8 = new ResourceField(TEXT8_VALUE);
   public static final ResourceField TEXT9 = new ResourceField(TEXT9_VALUE);
   public static final ResourceField TEXT10 = new ResourceField(TEXT10_VALUE);
   public static final ResourceField START1 = new ResourceField(START1_VALUE);
   public static final ResourceField START2 = new ResourceField(START2_VALUE);
   public static final ResourceField START3 = new ResourceField(START3_VALUE);
   public static final ResourceField START4 = new ResourceField(START4_VALUE);
   public static final ResourceField START5 = new ResourceField(START5_VALUE);
   public static final ResourceField FINISH1 = new ResourceField(FINISH1_VALUE);
   public static final ResourceField FINISH2 = new ResourceField(FINISH2_VALUE);
   public static final ResourceField FINISH3 = new ResourceField(FINISH3_VALUE);
   public static final ResourceField FINISH4 = new ResourceField(FINISH4_VALUE);
   public static final ResourceField FINISH5 = new ResourceField(FINISH5_VALUE);
   public static final ResourceField NUMBER1 = new ResourceField(NUMBER1_VALUE);
   public static final ResourceField NUMBER2 = new ResourceField(NUMBER2_VALUE);
   public static final ResourceField NUMBER3 = new ResourceField(NUMBER3_VALUE);
   public static final ResourceField NUMBER4 = new ResourceField(NUMBER4_VALUE);
   public static final ResourceField NUMBER5 = new ResourceField(NUMBER5_VALUE);
   public static final ResourceField DURATION1 = new ResourceField(DURATION1_VALUE);
   public static final ResourceField DURATION2 = new ResourceField(DURATION2_VALUE);
   public static final ResourceField DURATION3 = new ResourceField(DURATION3_VALUE);
   public static final ResourceField COST1 = new ResourceField(COST1_VALUE);
   public static final ResourceField COST2 = new ResourceField(COST2_VALUE);
   public static final ResourceField COST3 = new ResourceField(COST3_VALUE);
   public static final ResourceField FLAG10 = new ResourceField(FLAG10_VALUE);
   public static final ResourceField FLAG1 = new ResourceField(FLAG1_VALUE);
   public static final ResourceField FLAG2 = new ResourceField(FLAG2_VALUE);
   public static final ResourceField FLAG3 = new ResourceField(FLAG3_VALUE);
   public static final ResourceField FLAG4 = new ResourceField(FLAG4_VALUE);
   public static final ResourceField FLAG5 = new ResourceField(FLAG5_VALUE);
   public static final ResourceField FLAG6 = new ResourceField(FLAG6_VALUE);
   public static final ResourceField FLAG7 = new ResourceField(FLAG7_VALUE);
   public static final ResourceField FLAG8 = new ResourceField(FLAG8_VALUE);
   public static final ResourceField FLAG9 = new ResourceField(FLAG9_VALUE);
   public static final ResourceField HYPERLINK = new ResourceField(HYPERLINK_VALUE);
   public static final ResourceField HYPERLINK_ADDRESS = new ResourceField(HYPERLINK_ADDRESS_VALUE);
   public static final ResourceField HYPERLINK_SUBADDRESS = new ResourceField(HYPERLINK_SUBADDRESS_VALUE);
   public static final ResourceField HYPERLINK_HREF = new ResourceField(HYPERLINK_HREF_VALUE);
   public static final ResourceField ASSIGNMENT = new ResourceField(ASSIGNMENT_VALUE);
   public static final ResourceField TASK_SUMMARY_NAME = new ResourceField(TASK_SUMMARY_NAME_VALUE);
   public static final ResourceField CAN_LEVEL = new ResourceField(CAN_LEVEL_VALUE);
   public static final ResourceField WORK_CONTOUR = new ResourceField(WORK_CONTOUR_VALUE);
   public static final ResourceField COST4 = new ResourceField(COST4_VALUE);
   public static final ResourceField COST5 = new ResourceField(COST5_VALUE);
   public static final ResourceField COST6 = new ResourceField(COST6_VALUE);
   public static final ResourceField COST7 = new ResourceField(COST7_VALUE);
   public static final ResourceField COST8 = new ResourceField(COST8_VALUE);
   public static final ResourceField COST9 = new ResourceField(COST9_VALUE);
   public static final ResourceField COST10 = new ResourceField(COST10_VALUE);
   public static final ResourceField DATE1 = new ResourceField(DATE1_VALUE);
   public static final ResourceField DATE2 = new ResourceField(DATE2_VALUE);
   public static final ResourceField DATE3 = new ResourceField(DATE3_VALUE);
   public static final ResourceField DATE4 = new ResourceField(DATE4_VALUE);
   public static final ResourceField DATE5 = new ResourceField(DATE5_VALUE);
   public static final ResourceField DATE6 = new ResourceField(DATE6_VALUE);
   public static final ResourceField DATE7 = new ResourceField(DATE7_VALUE);
   public static final ResourceField DATE8 = new ResourceField(DATE8_VALUE);
   public static final ResourceField DATE9 = new ResourceField(DATE9_VALUE);
   public static final ResourceField DATE10 = new ResourceField(DATE10_VALUE);
   public static final ResourceField DURATION4 = new ResourceField(DURATION4_VALUE);
   public static final ResourceField DURATION5 = new ResourceField(DURATION5_VALUE);
   public static final ResourceField DURATION6 = new ResourceField(DURATION6_VALUE);
   public static final ResourceField DURATION7 = new ResourceField(DURATION7_VALUE);
   public static final ResourceField DURATION8 = new ResourceField(DURATION8_VALUE);
   public static final ResourceField DURATION9 = new ResourceField(DURATION9_VALUE);
   public static final ResourceField DURATION10 = new ResourceField(DURATION10_VALUE);
   public static final ResourceField FINISH6 = new ResourceField(FINISH6_VALUE);
   public static final ResourceField FINISH7 = new ResourceField(FINISH7_VALUE);
   public static final ResourceField FINISH8 = new ResourceField(FINISH8_VALUE);
   public static final ResourceField FINISH9 = new ResourceField(FINISH9_VALUE);
   public static final ResourceField FINISH10 = new ResourceField(FINISH10_VALUE);
   public static final ResourceField FLAG11 = new ResourceField(FLAG11_VALUE);
   public static final ResourceField FLAG12 = new ResourceField(FLAG12_VALUE);
   public static final ResourceField FLAG13 = new ResourceField(FLAG13_VALUE);
   public static final ResourceField FLAG14 = new ResourceField(FLAG14_VALUE);
   public static final ResourceField FLAG15 = new ResourceField(FLAG15_VALUE);
   public static final ResourceField FLAG16 = new ResourceField(FLAG16_VALUE);
   public static final ResourceField FLAG17 = new ResourceField(FLAG17_VALUE);
   public static final ResourceField FLAG18 = new ResourceField(FLAG18_VALUE);
   public static final ResourceField FLAG19 = new ResourceField(FLAG19_VALUE);
   public static final ResourceField FLAG20 = new ResourceField(FLAG20_VALUE);
   public static final ResourceField NUMBER6 = new ResourceField(NUMBER6_VALUE);
   public static final ResourceField NUMBER7 = new ResourceField(NUMBER7_VALUE);
   public static final ResourceField NUMBER8 = new ResourceField(NUMBER8_VALUE);
   public static final ResourceField NUMBER9 = new ResourceField(NUMBER9_VALUE);
   public static final ResourceField NUMBER10 = new ResourceField(NUMBER10_VALUE);
   public static final ResourceField NUMBER11 = new ResourceField(NUMBER11_VALUE);
   public static final ResourceField NUMBER12 = new ResourceField(NUMBER12_VALUE);
   public static final ResourceField NUMBER13 = new ResourceField(NUMBER13_VALUE);
   public static final ResourceField NUMBER14 = new ResourceField(NUMBER14_VALUE);
   public static final ResourceField NUMBER15 = new ResourceField(NUMBER15_VALUE);
   public static final ResourceField NUMBER16 = new ResourceField(NUMBER16_VALUE);
   public static final ResourceField NUMBER17 = new ResourceField(NUMBER17_VALUE);
   public static final ResourceField NUMBER18 = new ResourceField(NUMBER18_VALUE);
   public static final ResourceField NUMBER19 = new ResourceField(NUMBER19_VALUE);
   public static final ResourceField NUMBER20 = new ResourceField(NUMBER20_VALUE);
   public static final ResourceField START6 = new ResourceField(START6_VALUE);
   public static final ResourceField START7 = new ResourceField(START7_VALUE);
   public static final ResourceField START8 = new ResourceField(START8_VALUE);
   public static final ResourceField START9 = new ResourceField(START9_VALUE);
   public static final ResourceField START10 = new ResourceField(START10_VALUE);
   public static final ResourceField TEXT11 = new ResourceField(TEXT11_VALUE);
   public static final ResourceField TEXT12 = new ResourceField(TEXT12_VALUE);
   public static final ResourceField TEXT13 = new ResourceField(TEXT13_VALUE);
   public static final ResourceField TEXT14 = new ResourceField(TEXT14_VALUE);
   public static final ResourceField TEXT15 = new ResourceField(TEXT15_VALUE);
   public static final ResourceField TEXT16 = new ResourceField(TEXT16_VALUE);
   public static final ResourceField TEXT17 = new ResourceField(TEXT17_VALUE);
   public static final ResourceField TEXT18 = new ResourceField(TEXT18_VALUE);
   public static final ResourceField TEXT19 = new ResourceField(TEXT19_VALUE);
   public static final ResourceField TEXT20 = new ResourceField(TEXT20_VALUE);
   public static final ResourceField TEXT21 = new ResourceField(TEXT21_VALUE);
   public static final ResourceField TEXT22 = new ResourceField(TEXT22_VALUE);
   public static final ResourceField TEXT23 = new ResourceField(TEXT23_VALUE);
   public static final ResourceField TEXT24 = new ResourceField(TEXT24_VALUE);
   public static final ResourceField TEXT25 = new ResourceField(TEXT25_VALUE);
   public static final ResourceField TEXT26 = new ResourceField(TEXT26_VALUE);
   public static final ResourceField TEXT27 = new ResourceField(TEXT27_VALUE);
   public static final ResourceField TEXT28 = new ResourceField(TEXT28_VALUE);
   public static final ResourceField TEXT29 = new ResourceField(TEXT29_VALUE);
   public static final ResourceField TEXT30 = new ResourceField(TEXT30_VALUE);
   public static final ResourceField PHONETICS = new ResourceField(PHONETICS_VALUE);
   public static final ResourceField ASSIGNMENT_DELAY = new ResourceField(ASSIGNMENT_DELAY_VALUE);
   public static final ResourceField ASSIGNMENT_UNITS = new ResourceField(ASSIGNMENT_UNITS_VALUE);
   public static final ResourceField BASELINE_START = new ResourceField(BASELINE_START_VALUE);
   public static final ResourceField BASELINE_FINISH = new ResourceField(BASELINE_FINISH_VALUE);
   public static final ResourceField CONFIRMED = new ResourceField(CONFIRMED_VALUE);
   public static final ResourceField FINISH = new ResourceField(FINISH_VALUE);
   public static final ResourceField LEVELING_DELAY = new ResourceField(LEVELING_DELAY_VALUE);
   public static final ResourceField RESPONSE_PENDING = new ResourceField(RESPONSE_PENDING_VALUE);
   public static final ResourceField START = new ResourceField(START_VALUE);
   public static final ResourceField TEAMSTATUS_PENDING = new ResourceField(TEAMSTATUS_PENDING_VALUE);
   public static final ResourceField CV = new ResourceField(CV_VALUE);
   public static final ResourceField UPDATE_NEEDED = new ResourceField(UPDATE_NEEDED_VALUE);
   public static final ResourceField COST_RATE_TABLE = new ResourceField(COST_RATE_TABLE_VALUE);
   public static final ResourceField ACTUAL_START = new ResourceField(ACTUAL_START_VALUE);
   public static final ResourceField ACTUAL_FINISH = new ResourceField(ACTUAL_FINISH_VALUE);
   public static final ResourceField WORKGROUP = new ResourceField(WORKGROUP_VALUE);
   public static final ResourceField PROJECT = new ResourceField(PROJECT_VALUE);
   public static final ResourceField OUTLINE_CODE1 = new ResourceField(OUTLINE_CODE1_VALUE);
   public static final ResourceField OUTLINE_CODE2 = new ResourceField(OUTLINE_CODE2_VALUE);
   public static final ResourceField OUTLINE_CODE3 = new ResourceField(OUTLINE_CODE3_VALUE);
   public static final ResourceField OUTLINE_CODE4 = new ResourceField(OUTLINE_CODE4_VALUE);
   public static final ResourceField OUTLINE_CODE5 = new ResourceField(OUTLINE_CODE5_VALUE);
   public static final ResourceField OUTLINE_CODE6 = new ResourceField(OUTLINE_CODE6_VALUE);
   public static final ResourceField OUTLINE_CODE7 = new ResourceField(OUTLINE_CODE7_VALUE);
   public static final ResourceField OUTLINE_CODE8 = new ResourceField(OUTLINE_CODE8_VALUE);
   public static final ResourceField OUTLINE_CODE9 = new ResourceField(OUTLINE_CODE9_VALUE);
   public static final ResourceField OUTLINE_CODE10 = new ResourceField(OUTLINE_CODE10_VALUE);
   public static final ResourceField MATERIAL_LABEL = new ResourceField(MATERIAL_LABEL_VALUE);
   public static final ResourceField TYPE = new ResourceField(TYPE_VALUE);
   public static final ResourceField VAC = new ResourceField(VAC_VALUE);
   public static final ResourceField GROUP_BY_SUMMARY = new ResourceField(GROUP_BY_SUMMARY_VALUE);
   public static final ResourceField WINDOWS_USER_ACCOUNT = new ResourceField(WINDOWS_USER_ACCOUNT_VALUE);
   
   private static final ResourceField[] FIELD_ARRAY = new ResourceField[312];
   
   static
   {
      FIELD_ARRAY[ID_VALUE] = ID;
      FIELD_ARRAY[NAME_VALUE] = NAME;
      FIELD_ARRAY[INITIALS_VALUE] = INITIALS;
      FIELD_ARRAY[GROUP_VALUE] = GROUP;
      FIELD_ARRAY[MAX_UNITS_VALUE] = MAX_UNITS;
      FIELD_ARRAY[BASE_CALENDAR_VALUE] = BASE_CALENDAR;
      FIELD_ARRAY[STANDARD_RATE_VALUE] = STANDARD_RATE;
      FIELD_ARRAY[OVERTIME_RATE_VALUE] = OVERTIME_RATE;
      FIELD_ARRAY[TEXT1_VALUE] = TEXT1;
      FIELD_ARRAY[TEXT2_VALUE] = TEXT2;
      FIELD_ARRAY[CODE_VALUE] = CODE;
      FIELD_ARRAY[ACTUAL_COST_VALUE] = ACTUAL_COST;
      FIELD_ARRAY[COST_VALUE] = COST;
      FIELD_ARRAY[WORK_VALUE] = WORK;
      FIELD_ARRAY[ACTUAL_WORK_VALUE] = ACTUAL_WORK;
      FIELD_ARRAY[BASELINE_WORK_VALUE] = BASELINE_WORK;
      FIELD_ARRAY[OVERTIME_WORK_VALUE] = OVERTIME_WORK;
      FIELD_ARRAY[BASELINE_COST_VALUE] = BASELINE_COST;
      FIELD_ARRAY[COST_PER_USE_VALUE] = COST_PER_USE;
      FIELD_ARRAY[ACCRUE_AT_VALUE] = ACCRUE_AT;
      FIELD_ARRAY[REMAINING_COST_VALUE] = REMAINING_COST;
      FIELD_ARRAY[REMAINING_WORK_VALUE] = REMAINING_WORK;
      FIELD_ARRAY[WORK_VARIANCE_VALUE] = WORK_VARIANCE;
      FIELD_ARRAY[COST_VARIANCE_VALUE] = COST_VARIANCE;
      FIELD_ARRAY[OVERALLOCATED_VALUE] = OVERALLOCATED;
      FIELD_ARRAY[PEAK_VALUE] = PEAK;
      FIELD_ARRAY[UNIQUE_ID_VALUE] = UNIQUE_ID;
      FIELD_ARRAY[NOTES_VALUE] = NOTES;
      FIELD_ARRAY[PERCENT_WORK_COMPLETE_VALUE] = PERCENT_WORK_COMPLETE;
      FIELD_ARRAY[TEXT3_VALUE] = TEXT3;
      FIELD_ARRAY[TEXT4_VALUE] = TEXT4;
      FIELD_ARRAY[TEXT5_VALUE] = TEXT5;
      FIELD_ARRAY[OBJECTS_VALUE] = OBJECTS;
      FIELD_ARRAY[LINKED_FIELDS_VALUE] = LINKED_FIELDS;
      FIELD_ARRAY[EMAIL_ADDRESS_VALUE] = EMAIL_ADDRESS;
      FIELD_ARRAY[REGULAR_WORK_VALUE] = REGULAR_WORK;
      FIELD_ARRAY[ACTUAL_OVERTIME_WORK_VALUE] = ACTUAL_OVERTIME_WORK;
      FIELD_ARRAY[REMAINING_OVERTIME_WORK_VALUE] = REMAINING_OVERTIME_WORK;
      FIELD_ARRAY[OVERTIME_COST_VALUE] = OVERTIME_COST;
      FIELD_ARRAY[ACTUAL_OVERTIME_COST_VALUE] = ACTUAL_OVERTIME_COST;
      FIELD_ARRAY[REMAINING_OVERTIME_COST_VALUE] = REMAINING_OVERTIME_COST;
      FIELD_ARRAY[BCWS_VALUE] = BCWS;
      FIELD_ARRAY[BCWP_VALUE] = BCWP;
      FIELD_ARRAY[ACWP_VALUE] = ACWP;
      FIELD_ARRAY[SV_VALUE] = SV;
      FIELD_ARRAY[AVAILABLE_FROM_VALUE] = AVAILABLE_FROM;
      FIELD_ARRAY[AVAILABLE_TO_VALUE] = AVAILABLE_TO;
      FIELD_ARRAY[INDICATORS_VALUE] = INDICATORS;
      FIELD_ARRAY[TEXT6_VALUE] = TEXT6;
      FIELD_ARRAY[TEXT7_VALUE] = TEXT7;
      FIELD_ARRAY[TEXT8_VALUE] = TEXT8;
      FIELD_ARRAY[TEXT9_VALUE] = TEXT9;
      FIELD_ARRAY[TEXT10_VALUE] = TEXT10;
      FIELD_ARRAY[START1_VALUE] = START1;
      FIELD_ARRAY[START2_VALUE] = START2;
      FIELD_ARRAY[START3_VALUE] = START3;
      FIELD_ARRAY[START4_VALUE] = START4;
      FIELD_ARRAY[START5_VALUE] = START5;
      FIELD_ARRAY[FINISH1_VALUE] = FINISH1;
      FIELD_ARRAY[FINISH2_VALUE] = FINISH2;
      FIELD_ARRAY[FINISH3_VALUE] = FINISH3;
      FIELD_ARRAY[FINISH4_VALUE] = FINISH4;
      FIELD_ARRAY[FINISH5_VALUE] = FINISH5;
      FIELD_ARRAY[NUMBER1_VALUE] = NUMBER1;
      FIELD_ARRAY[NUMBER2_VALUE] = NUMBER2;
      FIELD_ARRAY[NUMBER3_VALUE] = NUMBER3;
      FIELD_ARRAY[NUMBER4_VALUE] = NUMBER4;
      FIELD_ARRAY[NUMBER5_VALUE] = NUMBER5;
      FIELD_ARRAY[DURATION1_VALUE] = DURATION1;
      FIELD_ARRAY[DURATION2_VALUE] = DURATION2;
      FIELD_ARRAY[DURATION3_VALUE] = DURATION3;
      FIELD_ARRAY[COST1_VALUE] = COST1;
      FIELD_ARRAY[COST2_VALUE] = COST2;
      FIELD_ARRAY[COST3_VALUE] = COST3;
      FIELD_ARRAY[FLAG10_VALUE] = FLAG10;
      FIELD_ARRAY[FLAG1_VALUE] = FLAG1;
      FIELD_ARRAY[FLAG2_VALUE] = FLAG2;
      FIELD_ARRAY[FLAG3_VALUE] = FLAG3;
      FIELD_ARRAY[FLAG4_VALUE] = FLAG4;
      FIELD_ARRAY[FLAG5_VALUE] = FLAG5;
      FIELD_ARRAY[FLAG6_VALUE] = FLAG6;
      FIELD_ARRAY[FLAG7_VALUE] = FLAG7;
      FIELD_ARRAY[FLAG8_VALUE] = FLAG8;
      FIELD_ARRAY[FLAG9_VALUE] = FLAG9;
      FIELD_ARRAY[HYPERLINK_VALUE] = HYPERLINK;
      FIELD_ARRAY[HYPERLINK_ADDRESS_VALUE] = HYPERLINK_ADDRESS;
      FIELD_ARRAY[HYPERLINK_SUBADDRESS_VALUE] = HYPERLINK_SUBADDRESS;
      FIELD_ARRAY[HYPERLINK_HREF_VALUE] = HYPERLINK_HREF;
      FIELD_ARRAY[ASSIGNMENT_VALUE] = ASSIGNMENT;
      FIELD_ARRAY[TASK_SUMMARY_NAME_VALUE] = TASK_SUMMARY_NAME;
      FIELD_ARRAY[CAN_LEVEL_VALUE] = CAN_LEVEL;
      FIELD_ARRAY[WORK_CONTOUR_VALUE] = WORK_CONTOUR;
      FIELD_ARRAY[COST4_VALUE] = COST4;
      FIELD_ARRAY[COST5_VALUE] = COST5;
      FIELD_ARRAY[COST6_VALUE] = COST6;
      FIELD_ARRAY[COST7_VALUE] = COST7;
      FIELD_ARRAY[COST8_VALUE] = COST8;
      FIELD_ARRAY[COST9_VALUE] = COST9;
      FIELD_ARRAY[COST10_VALUE] = COST10;
      FIELD_ARRAY[DATE1_VALUE] = DATE1;
      FIELD_ARRAY[DATE2_VALUE] = DATE2;
      FIELD_ARRAY[DATE3_VALUE] = DATE3;
      FIELD_ARRAY[DATE4_VALUE] = DATE4;
      FIELD_ARRAY[DATE5_VALUE] = DATE5;
      FIELD_ARRAY[DATE6_VALUE] = DATE6;
      FIELD_ARRAY[DATE7_VALUE] = DATE7;
      FIELD_ARRAY[DATE8_VALUE] = DATE8;
      FIELD_ARRAY[DATE9_VALUE] = DATE9;
      FIELD_ARRAY[DATE10_VALUE] = DATE10;
      FIELD_ARRAY[DURATION4_VALUE] = DURATION4;
      FIELD_ARRAY[DURATION5_VALUE] = DURATION5;
      FIELD_ARRAY[DURATION6_VALUE] = DURATION6;
      FIELD_ARRAY[DURATION7_VALUE] = DURATION7;
      FIELD_ARRAY[DURATION8_VALUE] = DURATION8;
      FIELD_ARRAY[DURATION9_VALUE] = DURATION9;
      FIELD_ARRAY[DURATION10_VALUE] = DURATION10;
      FIELD_ARRAY[FINISH6_VALUE] = FINISH6;
      FIELD_ARRAY[FINISH7_VALUE] = FINISH7;
      FIELD_ARRAY[FINISH8_VALUE] = FINISH8;
      FIELD_ARRAY[FINISH9_VALUE] = FINISH9;
      FIELD_ARRAY[FINISH10_VALUE] = FINISH10;
      FIELD_ARRAY[FLAG11_VALUE] = FLAG11;
      FIELD_ARRAY[FLAG12_VALUE] = FLAG12;
      FIELD_ARRAY[FLAG13_VALUE] = FLAG13;
      FIELD_ARRAY[FLAG14_VALUE] = FLAG14;
      FIELD_ARRAY[FLAG15_VALUE] = FLAG15;
      FIELD_ARRAY[FLAG16_VALUE] = FLAG16;
      FIELD_ARRAY[FLAG17_VALUE] = FLAG17;
      FIELD_ARRAY[FLAG18_VALUE] = FLAG18;
      FIELD_ARRAY[FLAG19_VALUE] = FLAG19;
      FIELD_ARRAY[FLAG20_VALUE] = FLAG20;
      FIELD_ARRAY[NUMBER6_VALUE] = NUMBER6;
      FIELD_ARRAY[NUMBER7_VALUE] = NUMBER7;
      FIELD_ARRAY[NUMBER8_VALUE] = NUMBER8;
      FIELD_ARRAY[NUMBER9_VALUE] = NUMBER9;
      FIELD_ARRAY[NUMBER10_VALUE] = NUMBER10;
      FIELD_ARRAY[NUMBER11_VALUE] = NUMBER11;
      FIELD_ARRAY[NUMBER12_VALUE] = NUMBER12;
      FIELD_ARRAY[NUMBER13_VALUE] = NUMBER13;
      FIELD_ARRAY[NUMBER14_VALUE] = NUMBER14;
      FIELD_ARRAY[NUMBER15_VALUE] = NUMBER15;
      FIELD_ARRAY[NUMBER16_VALUE] = NUMBER16;
      FIELD_ARRAY[NUMBER17_VALUE] = NUMBER17;
      FIELD_ARRAY[NUMBER18_VALUE] = NUMBER18;
      FIELD_ARRAY[NUMBER19_VALUE] = NUMBER19;
      FIELD_ARRAY[NUMBER20_VALUE] = NUMBER20;
      FIELD_ARRAY[START6_VALUE] = START6;
      FIELD_ARRAY[START7_VALUE] = START7;
      FIELD_ARRAY[START8_VALUE] = START8;
      FIELD_ARRAY[START9_VALUE] = START9;
      FIELD_ARRAY[START10_VALUE] = START10;
      FIELD_ARRAY[TEXT11_VALUE] = TEXT11;
      FIELD_ARRAY[TEXT12_VALUE] = TEXT12;
      FIELD_ARRAY[TEXT13_VALUE] = TEXT13;
      FIELD_ARRAY[TEXT14_VALUE] = TEXT14;
      FIELD_ARRAY[TEXT15_VALUE] = TEXT15;
      FIELD_ARRAY[TEXT16_VALUE] = TEXT16;
      FIELD_ARRAY[TEXT17_VALUE] = TEXT17;
      FIELD_ARRAY[TEXT18_VALUE] = TEXT18;
      FIELD_ARRAY[TEXT19_VALUE] = TEXT19;
      FIELD_ARRAY[TEXT20_VALUE] = TEXT20;
      FIELD_ARRAY[TEXT21_VALUE] = TEXT21;
      FIELD_ARRAY[TEXT22_VALUE] = TEXT22;
      FIELD_ARRAY[TEXT23_VALUE] = TEXT23;
      FIELD_ARRAY[TEXT24_VALUE] = TEXT24;
      FIELD_ARRAY[TEXT25_VALUE] = TEXT25;
      FIELD_ARRAY[TEXT26_VALUE] = TEXT26;
      FIELD_ARRAY[TEXT27_VALUE] = TEXT27;
      FIELD_ARRAY[TEXT28_VALUE] = TEXT28;
      FIELD_ARRAY[TEXT29_VALUE] = TEXT29;
      FIELD_ARRAY[TEXT30_VALUE] = TEXT30;
      FIELD_ARRAY[PHONETICS_VALUE] = PHONETICS;
      FIELD_ARRAY[ASSIGNMENT_DELAY_VALUE] = ASSIGNMENT_DELAY;
      FIELD_ARRAY[ASSIGNMENT_UNITS_VALUE] = ASSIGNMENT_UNITS;
      FIELD_ARRAY[BASELINE_START_VALUE] = BASELINE_START;
      FIELD_ARRAY[BASELINE_FINISH_VALUE] = BASELINE_FINISH;
      FIELD_ARRAY[CONFIRMED_VALUE] = CONFIRMED;
      FIELD_ARRAY[FINISH_VALUE] = FINISH;
      FIELD_ARRAY[LEVELING_DELAY_VALUE] = LEVELING_DELAY;
      FIELD_ARRAY[RESPONSE_PENDING_VALUE] = RESPONSE_PENDING;
      FIELD_ARRAY[START_VALUE] = START;
      FIELD_ARRAY[TEAMSTATUS_PENDING_VALUE] = TEAMSTATUS_PENDING;
      FIELD_ARRAY[CV_VALUE] = CV;
      FIELD_ARRAY[UPDATE_NEEDED_VALUE] = UPDATE_NEEDED;
      FIELD_ARRAY[COST_RATE_TABLE_VALUE] = COST_RATE_TABLE;
      FIELD_ARRAY[ACTUAL_START_VALUE] = ACTUAL_START;
      FIELD_ARRAY[ACTUAL_FINISH_VALUE] = ACTUAL_FINISH;
      FIELD_ARRAY[WORKGROUP_VALUE] = WORKGROUP;
      FIELD_ARRAY[PROJECT_VALUE] = PROJECT;
      FIELD_ARRAY[OUTLINE_CODE1_VALUE] = OUTLINE_CODE1;
      FIELD_ARRAY[OUTLINE_CODE2_VALUE] = OUTLINE_CODE2;
      FIELD_ARRAY[OUTLINE_CODE3_VALUE] = OUTLINE_CODE3;
      FIELD_ARRAY[OUTLINE_CODE4_VALUE] = OUTLINE_CODE4;
      FIELD_ARRAY[OUTLINE_CODE5_VALUE] = OUTLINE_CODE5;
      FIELD_ARRAY[OUTLINE_CODE6_VALUE] = OUTLINE_CODE6;
      FIELD_ARRAY[OUTLINE_CODE7_VALUE] = OUTLINE_CODE7;
      FIELD_ARRAY[OUTLINE_CODE8_VALUE] = OUTLINE_CODE8;
      FIELD_ARRAY[OUTLINE_CODE9_VALUE] = OUTLINE_CODE9;
      FIELD_ARRAY[OUTLINE_CODE10_VALUE] = OUTLINE_CODE10;
      FIELD_ARRAY[MATERIAL_LABEL_VALUE] = MATERIAL_LABEL;
      FIELD_ARRAY[TYPE_VALUE] = TYPE;
      FIELD_ARRAY[VAC_VALUE] = VAC;
      FIELD_ARRAY[GROUP_BY_SUMMARY_VALUE] = GROUP_BY_SUMMARY;
      FIELD_ARRAY[WINDOWS_USER_ACCOUNT_VALUE] = WINDOWS_USER_ACCOUNT;      
   }
   
   private int m_value;
}
