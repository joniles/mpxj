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

package net.sf.mpxj;

import java.util.Locale;



/**
 * Instances of this type represent Resource fields.
 */
public final class ResourceField implements FieldType
{
   /**
    * Private constructor.
    *
    * @param value task field value
    * @param dataType data type
    */
   private ResourceField (int value, DataType dataType)
   {
      m_value = value;
      m_dataType = dataType;
   }

   /**
    * {@inheritDoc}
    */
   public String getName()
   {
      return (getName(Locale.ENGLISH));
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
    * {@inheritDoc}
    */
   public DataType getDataType ()
   {
      return (m_dataType);
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

   /**
    * {@inheritDoc}
    */
   public boolean equals (Object obj)
   {
      return (m_value == ((ResourceField)obj).m_value);
   }
   
   /**
    * {@inheritDoc}
    */
   public int hashCode ()
   {
      return (m_value);
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
   public static final int REMAINING_COST_VALUE = 20;
   public static final int REMAINING_WORK_VALUE = 21;
   public static final int WORK_VARIANCE_VALUE = 22;
   public static final int COST_VARIANCE_VALUE = 23;
   public static final int OVERALLOCATED_VALUE = 24;
   public static final int PEAK_VALUE = 25;
   public static final int UNIQUE_ID_VALUE = 26;
   public static final int NOTES_VALUE = 27;
   public static final int PERCENT_WORK_COMPLETE_VALUE = 28;
   public static final int TEXT3_VALUE = 29;
   public static final int TEXT4_VALUE = 30;
   public static final int TEXT5_VALUE = 31;
   public static final int OBJECTS_VALUE = 32;
   public static final int LINKED_FIELDS_VALUE = 33;
   public static final int EMAIL_ADDRESS_VALUE = 34;
   public static final int REGULAR_WORK_VALUE = 35;
   public static final int ACTUAL_OVERTIME_WORK_VALUE = 36;
   public static final int REMAINING_OVERTIME_WORK_VALUE = 37;
   public static final int OVERTIME_COST_VALUE = 38;
   public static final int ACTUAL_OVERTIME_COST_VALUE = 39;
   public static final int REMAINING_OVERTIME_COST_VALUE = 40;
   public static final int BCWS_VALUE = 41;
   public static final int BCWP_VALUE = 42;
   public static final int ACWP_VALUE = 43;
   public static final int SV_VALUE = 44;
   public static final int AVAILABLE_FROM_VALUE = 45;
   public static final int AVAILABLE_TO_VALUE = 46;
   public static final int INDICATORS_VALUE = 47;
   public static final int TEXT6_VALUE = 48;
   public static final int TEXT7_VALUE = 49;
   public static final int TEXT8_VALUE = 50;
   public static final int TEXT9_VALUE = 51;
   public static final int TEXT10_VALUE = 52;
   public static final int START1_VALUE = 53;
   public static final int START2_VALUE = 54;
   public static final int START3_VALUE = 55;
   public static final int START4_VALUE = 56;
   public static final int START5_VALUE = 57;
   public static final int FINISH1_VALUE = 58;
   public static final int FINISH2_VALUE = 59;
   public static final int FINISH3_VALUE = 60;
   public static final int FINISH4_VALUE = 61;
   public static final int FINISH5_VALUE = 62;
   public static final int NUMBER1_VALUE = 63;
   public static final int NUMBER2_VALUE = 64;
   public static final int NUMBER3_VALUE = 65;
   public static final int NUMBER4_VALUE = 66;
   public static final int NUMBER5_VALUE = 67;
   public static final int DURATION1_VALUE = 68;
   public static final int DURATION2_VALUE = 69;
   public static final int DURATION3_VALUE = 70;
   public static final int COST1_VALUE = 71;
   public static final int COST2_VALUE = 72;
   public static final int COST3_VALUE = 73;
   public static final int FLAG10_VALUE = 74;
   public static final int FLAG1_VALUE = 75;
   public static final int FLAG2_VALUE = 76;
   public static final int FLAG3_VALUE = 77;
   public static final int FLAG4_VALUE = 78;
   public static final int FLAG5_VALUE = 79;
   public static final int FLAG6_VALUE = 80;
   public static final int FLAG7_VALUE = 81;
   public static final int FLAG8_VALUE = 82;
   public static final int FLAG9_VALUE = 83;
   public static final int HYPERLINK_VALUE = 84;
   public static final int HYPERLINK_ADDRESS_VALUE = 85;
   public static final int HYPERLINK_SUBADDRESS_VALUE = 86;
   public static final int HYPERLINK_HREF_VALUE = 87;
   public static final int ASSIGNMENT_VALUE = 88;
   public static final int TASK_SUMMARY_NAME_VALUE = 89;
   public static final int CAN_LEVEL_VALUE = 90;
   public static final int WORK_CONTOUR_VALUE = 91;
   public static final int COST4_VALUE = 92;
   public static final int COST5_VALUE = 93;
   public static final int COST6_VALUE = 94;
   public static final int COST7_VALUE = 95;
   public static final int COST8_VALUE = 96;
   public static final int COST9_VALUE = 97;
   public static final int COST10_VALUE = 98;
   public static final int DATE1_VALUE = 99;
   public static final int DATE2_VALUE = 100;
   public static final int DATE3_VALUE = 101;
   public static final int DATE4_VALUE = 102;
   public static final int DATE5_VALUE = 103;
   public static final int DATE6_VALUE = 104;
   public static final int DATE7_VALUE = 105;
   public static final int DATE8_VALUE = 106;
   public static final int DATE9_VALUE = 107;
   public static final int DATE10_VALUE = 108;
   public static final int DURATION4_VALUE = 109;
   public static final int DURATION5_VALUE = 110;
   public static final int DURATION6_VALUE = 111;
   public static final int DURATION7_VALUE = 112;
   public static final int DURATION8_VALUE = 113;
   public static final int DURATION9_VALUE = 114;
   public static final int DURATION10_VALUE = 115;
   public static final int FINISH6_VALUE = 116;
   public static final int FINISH7_VALUE = 117;
   public static final int FINISH8_VALUE = 118;
   public static final int FINISH9_VALUE = 119;
   public static final int FINISH10_VALUE = 120;
   public static final int FLAG11_VALUE = 121;
   public static final int FLAG12_VALUE = 122;
   public static final int FLAG13_VALUE = 123;
   public static final int FLAG14_VALUE = 124;
   public static final int FLAG15_VALUE = 125;
   public static final int FLAG16_VALUE = 126;
   public static final int FLAG17_VALUE = 127;
   public static final int FLAG18_VALUE = 128;
   public static final int FLAG19_VALUE = 129;
   public static final int FLAG20_VALUE = 130;
   public static final int NUMBER6_VALUE = 131;
   public static final int NUMBER7_VALUE = 132;
   public static final int NUMBER8_VALUE = 133;
   public static final int NUMBER9_VALUE = 134;
   public static final int NUMBER10_VALUE = 135;
   public static final int NUMBER11_VALUE = 136;
   public static final int NUMBER12_VALUE = 137;
   public static final int NUMBER13_VALUE = 138;
   public static final int NUMBER14_VALUE = 139;
   public static final int NUMBER15_VALUE = 140;
   public static final int NUMBER16_VALUE = 141;
   public static final int NUMBER17_VALUE = 142;
   public static final int NUMBER18_VALUE = 143;
   public static final int NUMBER19_VALUE = 144;
   public static final int NUMBER20_VALUE = 145;
   public static final int START6_VALUE = 146;
   public static final int START7_VALUE = 147;
   public static final int START8_VALUE = 148;
   public static final int START9_VALUE = 149;
   public static final int START10_VALUE = 150;
   public static final int TEXT11_VALUE = 151;
   public static final int TEXT12_VALUE = 152;
   public static final int TEXT13_VALUE = 153;
   public static final int TEXT14_VALUE = 154;
   public static final int TEXT15_VALUE = 155;
   public static final int TEXT16_VALUE = 156;
   public static final int TEXT17_VALUE = 157;
   public static final int TEXT18_VALUE = 158;
   public static final int TEXT19_VALUE = 159;
   public static final int TEXT20_VALUE = 160;
   public static final int TEXT21_VALUE = 161;
   public static final int TEXT22_VALUE = 162;
   public static final int TEXT23_VALUE = 163;
   public static final int TEXT24_VALUE = 164;
   public static final int TEXT25_VALUE = 165;
   public static final int TEXT26_VALUE = 166;
   public static final int TEXT27_VALUE = 167;
   public static final int TEXT28_VALUE = 168;
   public static final int TEXT29_VALUE = 169;
   public static final int TEXT30_VALUE = 170;
   public static final int PHONETICS_VALUE = 171;
   public static final int ASSIGNMENT_DELAY_VALUE = 172;
   public static final int ASSIGNMENT_UNITS_VALUE = 173;
   public static final int BASELINE_START_VALUE = 174;
   public static final int BASELINE_FINISH_VALUE = 175;
   public static final int CONFIRMED_VALUE = 176;
   public static final int FINISH_VALUE = 177;
   public static final int LEVELING_DELAY_VALUE = 178;
   public static final int RESPONSE_PENDING_VALUE = 179;
   public static final int START_VALUE = 180;
   public static final int TEAMSTATUS_PENDING_VALUE = 181;
   public static final int CV_VALUE = 182;
   public static final int UPDATE_NEEDED_VALUE = 183;
   public static final int COST_RATE_TABLE_VALUE = 184;
   public static final int ACTUAL_START_VALUE = 185;
   public static final int ACTUAL_FINISH_VALUE = 186;
   public static final int WORKGROUP_VALUE = 187;
   public static final int PROJECT_VALUE = 188;
   public static final int OUTLINE_CODE1_VALUE = 189;
   public static final int OUTLINE_CODE2_VALUE = 190;
   public static final int OUTLINE_CODE3_VALUE = 191;
   public static final int OUTLINE_CODE4_VALUE = 192;
   public static final int OUTLINE_CODE5_VALUE = 193;
   public static final int OUTLINE_CODE6_VALUE = 194;
   public static final int OUTLINE_CODE7_VALUE = 195;
   public static final int OUTLINE_CODE8_VALUE = 196;
   public static final int OUTLINE_CODE9_VALUE = 197;
   public static final int OUTLINE_CODE10_VALUE = 198;
   public static final int MATERIAL_LABEL_VALUE = 199;
   public static final int TYPE_VALUE = 200;
   public static final int VAC_VALUE = 201;
   public static final int GROUP_BY_SUMMARY_VALUE = 202;
   public static final int WINDOWS_USER_ACCOUNT_VALUE = 203;

   public static final int MAX_VALUE = 204;
   
   public static final ResourceField ID = new ResourceField(ID_VALUE, DataType.NUMERIC);
   public static final ResourceField NAME = new ResourceField(NAME_VALUE, DataType.STRING);
   public static final ResourceField INITIALS = new ResourceField(INITIALS_VALUE, DataType.STRING);
   public static final ResourceField GROUP = new ResourceField(GROUP_VALUE, DataType.STRING);
   public static final ResourceField MAX_UNITS = new ResourceField(MAX_UNITS_VALUE, DataType.UNITS);
   public static final ResourceField BASE_CALENDAR = new ResourceField(BASE_CALENDAR_VALUE, DataType.STRING);
   public static final ResourceField STANDARD_RATE = new ResourceField(STANDARD_RATE_VALUE, DataType.RATE);
   public static final ResourceField OVERTIME_RATE = new ResourceField(OVERTIME_RATE_VALUE, DataType.RATE);
   public static final ResourceField TEXT1 = new ResourceField(TEXT1_VALUE, DataType.STRING);
   public static final ResourceField TEXT2 = new ResourceField(TEXT2_VALUE, DataType.STRING);
   public static final ResourceField CODE = new ResourceField(CODE_VALUE, DataType.STRING);
   public static final ResourceField ACTUAL_COST = new ResourceField(ACTUAL_COST_VALUE, DataType.CURRENCY);
   public static final ResourceField COST = new ResourceField(COST_VALUE, DataType.CURRENCY);
   public static final ResourceField WORK = new ResourceField(WORK_VALUE, DataType.DURATION);
   public static final ResourceField ACTUAL_WORK = new ResourceField(ACTUAL_WORK_VALUE, DataType.DURATION);
   public static final ResourceField BASELINE_WORK = new ResourceField(BASELINE_WORK_VALUE, DataType.DURATION);
   public static final ResourceField OVERTIME_WORK = new ResourceField(OVERTIME_WORK_VALUE, DataType.DURATION);
   public static final ResourceField BASELINE_COST = new ResourceField(BASELINE_COST_VALUE, DataType.CURRENCY);
   public static final ResourceField COST_PER_USE = new ResourceField(COST_PER_USE_VALUE, DataType.CURRENCY);
   public static final ResourceField ACCRUE_AT = new ResourceField(ACCRUE_AT_VALUE, DataType.ACCRUE);
   public static final ResourceField REMAINING_COST = new ResourceField(REMAINING_COST_VALUE, DataType.CURRENCY);
   public static final ResourceField REMAINING_WORK = new ResourceField(REMAINING_WORK_VALUE, DataType.DURATION);
   public static final ResourceField WORK_VARIANCE = new ResourceField(WORK_VARIANCE_VALUE, DataType.DURATION);
   public static final ResourceField COST_VARIANCE = new ResourceField(COST_VARIANCE_VALUE, DataType.CURRENCY);
   public static final ResourceField OVERALLOCATED = new ResourceField(OVERALLOCATED_VALUE, DataType.BOOLEAN);
   public static final ResourceField PEAK = new ResourceField(PEAK_VALUE, DataType.NUMERIC);
   public static final ResourceField UNIQUE_ID = new ResourceField(UNIQUE_ID_VALUE, DataType.NUMERIC);
   public static final ResourceField NOTES = new ResourceField(NOTES_VALUE, DataType.STRING);
   public static final ResourceField PERCENT_WORK_COMPLETE = new ResourceField(PERCENT_WORK_COMPLETE_VALUE, DataType.PERCENTAGE);
   public static final ResourceField TEXT3 = new ResourceField(TEXT3_VALUE, DataType.STRING);
   public static final ResourceField TEXT4 = new ResourceField(TEXT4_VALUE, DataType.STRING);
   public static final ResourceField TEXT5 = new ResourceField(TEXT5_VALUE, DataType.STRING);
   public static final ResourceField OBJECTS = new ResourceField(OBJECTS_VALUE, DataType.NUMERIC);
   public static final ResourceField LINKED_FIELDS = new ResourceField(LINKED_FIELDS_VALUE, DataType.STRING);
   public static final ResourceField EMAIL_ADDRESS = new ResourceField(EMAIL_ADDRESS_VALUE, DataType.STRING);
   public static final ResourceField REGULAR_WORK = new ResourceField(REGULAR_WORK_VALUE, DataType.DURATION);
   public static final ResourceField ACTUAL_OVERTIME_WORK = new ResourceField(ACTUAL_OVERTIME_WORK_VALUE, DataType.DURATION);
   public static final ResourceField REMAINING_OVERTIME_WORK = new ResourceField(REMAINING_OVERTIME_WORK_VALUE, DataType.DURATION);
   public static final ResourceField OVERTIME_COST = new ResourceField(OVERTIME_COST_VALUE, DataType.CURRENCY);
   public static final ResourceField ACTUAL_OVERTIME_COST = new ResourceField(ACTUAL_OVERTIME_COST_VALUE, DataType.CURRENCY);
   public static final ResourceField REMAINING_OVERTIME_COST = new ResourceField(REMAINING_OVERTIME_COST_VALUE, DataType.CURRENCY);
   public static final ResourceField BCWS = new ResourceField(BCWS_VALUE, DataType.NUMERIC);
   public static final ResourceField BCWP = new ResourceField(BCWP_VALUE, DataType.NUMERIC);
   public static final ResourceField ACWP = new ResourceField(ACWP_VALUE, DataType.NUMERIC);
   public static final ResourceField SV = new ResourceField(SV_VALUE, DataType.NUMERIC);
   public static final ResourceField AVAILABLE_FROM = new ResourceField(AVAILABLE_FROM_VALUE, DataType.DATE);
   public static final ResourceField AVAILABLE_TO = new ResourceField(AVAILABLE_TO_VALUE, DataType.DATE);
   public static final ResourceField INDICATORS = new ResourceField(INDICATORS_VALUE, DataType.STRING);
   public static final ResourceField TEXT6 = new ResourceField(TEXT6_VALUE, DataType.STRING);
   public static final ResourceField TEXT7 = new ResourceField(TEXT7_VALUE, DataType.STRING);
   public static final ResourceField TEXT8 = new ResourceField(TEXT8_VALUE, DataType.STRING);
   public static final ResourceField TEXT9 = new ResourceField(TEXT9_VALUE, DataType.STRING);
   public static final ResourceField TEXT10 = new ResourceField(TEXT10_VALUE, DataType.STRING);
   public static final ResourceField START1 = new ResourceField(START1_VALUE, DataType.DATE);
   public static final ResourceField START2 = new ResourceField(START2_VALUE, DataType.DATE);
   public static final ResourceField START3 = new ResourceField(START3_VALUE, DataType.DATE);
   public static final ResourceField START4 = new ResourceField(START4_VALUE, DataType.DATE);
   public static final ResourceField START5 = new ResourceField(START5_VALUE, DataType.DATE);
   public static final ResourceField FINISH1 = new ResourceField(FINISH1_VALUE, DataType.DATE);
   public static final ResourceField FINISH2 = new ResourceField(FINISH2_VALUE, DataType.DATE);
   public static final ResourceField FINISH3 = new ResourceField(FINISH3_VALUE, DataType.DATE);
   public static final ResourceField FINISH4 = new ResourceField(FINISH4_VALUE, DataType.DATE);
   public static final ResourceField FINISH5 = new ResourceField(FINISH5_VALUE, DataType.DATE);
   public static final ResourceField NUMBER1 = new ResourceField(NUMBER1_VALUE, DataType.NUMERIC);
   public static final ResourceField NUMBER2 = new ResourceField(NUMBER2_VALUE, DataType.NUMERIC);
   public static final ResourceField NUMBER3 = new ResourceField(NUMBER3_VALUE, DataType.NUMERIC);
   public static final ResourceField NUMBER4 = new ResourceField(NUMBER4_VALUE, DataType.NUMERIC);
   public static final ResourceField NUMBER5 = new ResourceField(NUMBER5_VALUE, DataType.NUMERIC);
   public static final ResourceField DURATION1 = new ResourceField(DURATION1_VALUE, DataType.DURATION);
   public static final ResourceField DURATION2 = new ResourceField(DURATION2_VALUE, DataType.DURATION);
   public static final ResourceField DURATION3 = new ResourceField(DURATION3_VALUE, DataType.DURATION);
   public static final ResourceField COST1 = new ResourceField(COST1_VALUE, DataType.CURRENCY);
   public static final ResourceField COST2 = new ResourceField(COST2_VALUE, DataType.CURRENCY);
   public static final ResourceField COST3 = new ResourceField(COST3_VALUE, DataType.CURRENCY);
   public static final ResourceField FLAG10 = new ResourceField(FLAG10_VALUE, DataType.BOOLEAN);
   public static final ResourceField FLAG1 = new ResourceField(FLAG1_VALUE, DataType.BOOLEAN);
   public static final ResourceField FLAG2 = new ResourceField(FLAG2_VALUE, DataType.BOOLEAN);
   public static final ResourceField FLAG3 = new ResourceField(FLAG3_VALUE, DataType.BOOLEAN);
   public static final ResourceField FLAG4 = new ResourceField(FLAG4_VALUE, DataType.BOOLEAN);
   public static final ResourceField FLAG5 = new ResourceField(FLAG5_VALUE, DataType.BOOLEAN);
   public static final ResourceField FLAG6 = new ResourceField(FLAG6_VALUE, DataType.BOOLEAN);
   public static final ResourceField FLAG7 = new ResourceField(FLAG7_VALUE, DataType.BOOLEAN);
   public static final ResourceField FLAG8 = new ResourceField(FLAG8_VALUE, DataType.BOOLEAN);
   public static final ResourceField FLAG9 = new ResourceField(FLAG9_VALUE, DataType.BOOLEAN);
   public static final ResourceField HYPERLINK = new ResourceField(HYPERLINK_VALUE, DataType.STRING);
   public static final ResourceField HYPERLINK_ADDRESS = new ResourceField(HYPERLINK_ADDRESS_VALUE, DataType.STRING);
   public static final ResourceField HYPERLINK_SUBADDRESS = new ResourceField(HYPERLINK_SUBADDRESS_VALUE, DataType.STRING);
   public static final ResourceField HYPERLINK_HREF = new ResourceField(HYPERLINK_HREF_VALUE, DataType.STRING);
   public static final ResourceField ASSIGNMENT = new ResourceField(ASSIGNMENT_VALUE, DataType.STRING);
   public static final ResourceField TASK_SUMMARY_NAME = new ResourceField(TASK_SUMMARY_NAME_VALUE, DataType.STRING);
   public static final ResourceField CAN_LEVEL = new ResourceField(CAN_LEVEL_VALUE, DataType.BOOLEAN);
   public static final ResourceField WORK_CONTOUR = new ResourceField(WORK_CONTOUR_VALUE, DataType.STRING);
   public static final ResourceField COST4 = new ResourceField(COST4_VALUE, DataType.CURRENCY);
   public static final ResourceField COST5 = new ResourceField(COST5_VALUE, DataType.CURRENCY);
   public static final ResourceField COST6 = new ResourceField(COST6_VALUE, DataType.CURRENCY);
   public static final ResourceField COST7 = new ResourceField(COST7_VALUE, DataType.CURRENCY);
   public static final ResourceField COST8 = new ResourceField(COST8_VALUE, DataType.CURRENCY);
   public static final ResourceField COST9 = new ResourceField(COST9_VALUE, DataType.CURRENCY);
   public static final ResourceField COST10 = new ResourceField(COST10_VALUE, DataType.CURRENCY);
   public static final ResourceField DATE1 = new ResourceField(DATE1_VALUE, DataType.DATE);
   public static final ResourceField DATE2 = new ResourceField(DATE2_VALUE, DataType.DATE);
   public static final ResourceField DATE3 = new ResourceField(DATE3_VALUE, DataType.DATE);
   public static final ResourceField DATE4 = new ResourceField(DATE4_VALUE, DataType.DATE);
   public static final ResourceField DATE5 = new ResourceField(DATE5_VALUE, DataType.DATE);
   public static final ResourceField DATE6 = new ResourceField(DATE6_VALUE, DataType.DATE);
   public static final ResourceField DATE7 = new ResourceField(DATE7_VALUE, DataType.DATE);
   public static final ResourceField DATE8 = new ResourceField(DATE8_VALUE, DataType.DATE);
   public static final ResourceField DATE9 = new ResourceField(DATE9_VALUE, DataType.DATE);
   public static final ResourceField DATE10 = new ResourceField(DATE10_VALUE, DataType.DATE);
   public static final ResourceField DURATION4 = new ResourceField(DURATION4_VALUE, DataType.DURATION);
   public static final ResourceField DURATION5 = new ResourceField(DURATION5_VALUE, DataType.DURATION);
   public static final ResourceField DURATION6 = new ResourceField(DURATION6_VALUE, DataType.DURATION);
   public static final ResourceField DURATION7 = new ResourceField(DURATION7_VALUE, DataType.DURATION);
   public static final ResourceField DURATION8 = new ResourceField(DURATION8_VALUE, DataType.DURATION);
   public static final ResourceField DURATION9 = new ResourceField(DURATION9_VALUE, DataType.DURATION);
   public static final ResourceField DURATION10 = new ResourceField(DURATION10_VALUE, DataType.DURATION);
   public static final ResourceField FINISH6 = new ResourceField(FINISH6_VALUE, DataType.DATE);
   public static final ResourceField FINISH7 = new ResourceField(FINISH7_VALUE, DataType.DATE);
   public static final ResourceField FINISH8 = new ResourceField(FINISH8_VALUE, DataType.DATE);
   public static final ResourceField FINISH9 = new ResourceField(FINISH9_VALUE, DataType.DATE);
   public static final ResourceField FINISH10 = new ResourceField(FINISH10_VALUE, DataType.DATE);
   public static final ResourceField FLAG11 = new ResourceField(FLAG11_VALUE, DataType.BOOLEAN);
   public static final ResourceField FLAG12 = new ResourceField(FLAG12_VALUE, DataType.BOOLEAN);
   public static final ResourceField FLAG13 = new ResourceField(FLAG13_VALUE, DataType.BOOLEAN);
   public static final ResourceField FLAG14 = new ResourceField(FLAG14_VALUE, DataType.BOOLEAN);
   public static final ResourceField FLAG15 = new ResourceField(FLAG15_VALUE, DataType.BOOLEAN);
   public static final ResourceField FLAG16 = new ResourceField(FLAG16_VALUE, DataType.BOOLEAN);
   public static final ResourceField FLAG17 = new ResourceField(FLAG17_VALUE, DataType.BOOLEAN);
   public static final ResourceField FLAG18 = new ResourceField(FLAG18_VALUE, DataType.BOOLEAN);
   public static final ResourceField FLAG19 = new ResourceField(FLAG19_VALUE, DataType.BOOLEAN);
   public static final ResourceField FLAG20 = new ResourceField(FLAG20_VALUE, DataType.BOOLEAN);
   public static final ResourceField NUMBER6 = new ResourceField(NUMBER6_VALUE, DataType.NUMERIC);
   public static final ResourceField NUMBER7 = new ResourceField(NUMBER7_VALUE, DataType.NUMERIC);
   public static final ResourceField NUMBER8 = new ResourceField(NUMBER8_VALUE, DataType.NUMERIC);
   public static final ResourceField NUMBER9 = new ResourceField(NUMBER9_VALUE, DataType.NUMERIC);
   public static final ResourceField NUMBER10 = new ResourceField(NUMBER10_VALUE, DataType.NUMERIC);
   public static final ResourceField NUMBER11 = new ResourceField(NUMBER11_VALUE, DataType.NUMERIC);
   public static final ResourceField NUMBER12 = new ResourceField(NUMBER12_VALUE, DataType.NUMERIC);
   public static final ResourceField NUMBER13 = new ResourceField(NUMBER13_VALUE, DataType.NUMERIC);
   public static final ResourceField NUMBER14 = new ResourceField(NUMBER14_VALUE, DataType.NUMERIC);
   public static final ResourceField NUMBER15 = new ResourceField(NUMBER15_VALUE, DataType.NUMERIC);
   public static final ResourceField NUMBER16 = new ResourceField(NUMBER16_VALUE, DataType.NUMERIC);
   public static final ResourceField NUMBER17 = new ResourceField(NUMBER17_VALUE, DataType.NUMERIC);
   public static final ResourceField NUMBER18 = new ResourceField(NUMBER18_VALUE, DataType.NUMERIC);
   public static final ResourceField NUMBER19 = new ResourceField(NUMBER19_VALUE, DataType.NUMERIC);
   public static final ResourceField NUMBER20 = new ResourceField(NUMBER20_VALUE, DataType.NUMERIC);
   public static final ResourceField START6 = new ResourceField(START6_VALUE, DataType.DATE);
   public static final ResourceField START7 = new ResourceField(START7_VALUE, DataType.DATE);
   public static final ResourceField START8 = new ResourceField(START8_VALUE, DataType.DATE);
   public static final ResourceField START9 = new ResourceField(START9_VALUE, DataType.DATE);
   public static final ResourceField START10 = new ResourceField(START10_VALUE, DataType.DATE);
   public static final ResourceField TEXT11 = new ResourceField(TEXT11_VALUE, DataType.STRING);
   public static final ResourceField TEXT12 = new ResourceField(TEXT12_VALUE, DataType.STRING);
   public static final ResourceField TEXT13 = new ResourceField(TEXT13_VALUE, DataType.STRING);
   public static final ResourceField TEXT14 = new ResourceField(TEXT14_VALUE, DataType.STRING);
   public static final ResourceField TEXT15 = new ResourceField(TEXT15_VALUE, DataType.STRING);
   public static final ResourceField TEXT16 = new ResourceField(TEXT16_VALUE, DataType.STRING);
   public static final ResourceField TEXT17 = new ResourceField(TEXT17_VALUE, DataType.STRING);
   public static final ResourceField TEXT18 = new ResourceField(TEXT18_VALUE, DataType.STRING);
   public static final ResourceField TEXT19 = new ResourceField(TEXT19_VALUE, DataType.STRING);
   public static final ResourceField TEXT20 = new ResourceField(TEXT20_VALUE, DataType.STRING);
   public static final ResourceField TEXT21 = new ResourceField(TEXT21_VALUE, DataType.STRING);
   public static final ResourceField TEXT22 = new ResourceField(TEXT22_VALUE, DataType.STRING);
   public static final ResourceField TEXT23 = new ResourceField(TEXT23_VALUE, DataType.STRING);
   public static final ResourceField TEXT24 = new ResourceField(TEXT24_VALUE, DataType.STRING);
   public static final ResourceField TEXT25 = new ResourceField(TEXT25_VALUE, DataType.STRING);
   public static final ResourceField TEXT26 = new ResourceField(TEXT26_VALUE, DataType.STRING);
   public static final ResourceField TEXT27 = new ResourceField(TEXT27_VALUE, DataType.STRING);
   public static final ResourceField TEXT28 = new ResourceField(TEXT28_VALUE, DataType.STRING);
   public static final ResourceField TEXT29 = new ResourceField(TEXT29_VALUE, DataType.STRING);
   public static final ResourceField TEXT30 = new ResourceField(TEXT30_VALUE, DataType.STRING);
   public static final ResourceField PHONETICS = new ResourceField(PHONETICS_VALUE, DataType.STRING);
   public static final ResourceField ASSIGNMENT_DELAY = new ResourceField(ASSIGNMENT_DELAY_VALUE, DataType.STRING);
   public static final ResourceField ASSIGNMENT_UNITS = new ResourceField(ASSIGNMENT_UNITS_VALUE, DataType.STRING);
   public static final ResourceField BASELINE_START = new ResourceField(BASELINE_START_VALUE, DataType.DATE);
   public static final ResourceField BASELINE_FINISH = new ResourceField(BASELINE_FINISH_VALUE, DataType.DATE);
   public static final ResourceField CONFIRMED = new ResourceField(CONFIRMED_VALUE, DataType.BOOLEAN);
   public static final ResourceField FINISH = new ResourceField(FINISH_VALUE, DataType.DATE);
   public static final ResourceField LEVELING_DELAY = new ResourceField(LEVELING_DELAY_VALUE, DataType.STRING);
   public static final ResourceField RESPONSE_PENDING = new ResourceField(RESPONSE_PENDING_VALUE, DataType.BOOLEAN);
   public static final ResourceField START = new ResourceField(START_VALUE, DataType.DATE);
   public static final ResourceField TEAMSTATUS_PENDING = new ResourceField(TEAMSTATUS_PENDING_VALUE, DataType.BOOLEAN);
   public static final ResourceField CV = new ResourceField(CV_VALUE, DataType.NUMERIC);
   public static final ResourceField UPDATE_NEEDED = new ResourceField(UPDATE_NEEDED_VALUE, DataType.BOOLEAN);
   public static final ResourceField COST_RATE_TABLE = new ResourceField(COST_RATE_TABLE_VALUE, DataType.STRING);
   public static final ResourceField ACTUAL_START = new ResourceField(ACTUAL_START_VALUE, DataType.DATE);
   public static final ResourceField ACTUAL_FINISH = new ResourceField(ACTUAL_FINISH_VALUE, DataType.DATE);
   public static final ResourceField WORKGROUP = new ResourceField(WORKGROUP_VALUE, DataType.STRING);
   public static final ResourceField PROJECT = new ResourceField(PROJECT_VALUE, DataType.STRING);
   public static final ResourceField OUTLINE_CODE1 = new ResourceField(OUTLINE_CODE1_VALUE, DataType.STRING);
   public static final ResourceField OUTLINE_CODE2 = new ResourceField(OUTLINE_CODE2_VALUE, DataType.STRING);
   public static final ResourceField OUTLINE_CODE3 = new ResourceField(OUTLINE_CODE3_VALUE, DataType.STRING);
   public static final ResourceField OUTLINE_CODE4 = new ResourceField(OUTLINE_CODE4_VALUE, DataType.STRING);
   public static final ResourceField OUTLINE_CODE5 = new ResourceField(OUTLINE_CODE5_VALUE, DataType.STRING);
   public static final ResourceField OUTLINE_CODE6 = new ResourceField(OUTLINE_CODE6_VALUE, DataType.STRING);
   public static final ResourceField OUTLINE_CODE7 = new ResourceField(OUTLINE_CODE7_VALUE, DataType.STRING);
   public static final ResourceField OUTLINE_CODE8 = new ResourceField(OUTLINE_CODE8_VALUE, DataType.STRING);
   public static final ResourceField OUTLINE_CODE9 = new ResourceField(OUTLINE_CODE9_VALUE, DataType.STRING);
   public static final ResourceField OUTLINE_CODE10 = new ResourceField(OUTLINE_CODE10_VALUE, DataType.STRING);
   public static final ResourceField MATERIAL_LABEL = new ResourceField(MATERIAL_LABEL_VALUE, DataType.STRING);
   public static final ResourceField TYPE = new ResourceField(TYPE_VALUE, DataType.RESOURCE_TYPE);
   public static final ResourceField VAC = new ResourceField(VAC_VALUE, DataType.CURRENCY);
   public static final ResourceField GROUP_BY_SUMMARY = new ResourceField(GROUP_BY_SUMMARY_VALUE, DataType.STRING);
   public static final ResourceField WINDOWS_USER_ACCOUNT = new ResourceField(WINDOWS_USER_ACCOUNT_VALUE, DataType.STRING);

   private int m_value;
   private DataType m_dataType;   
}
