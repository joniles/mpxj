/*
 * file:       Column.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2003
 * date:       02/11/2003
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

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * This class represents a column in an MS Project table. The attributes held
 * here describe the layout of the column, along with any user defined
 * title that has been associated with the column.
 */
public final class Column
{
   /**
    * Retrieves a value representing the alignment of data displayed in
    * the column.
    * 
    * @return alignment type
    */
   public int getAlignData()
   {
      return (m_alignData);
   }

   /**
    * Retrieves a value representing the alignment of the column title text.
    * 
    * @return alignment type
    */
   public int getAlignTitle()
   {
      return (m_alignTitle);
   }

   /**
    * Retrieves the type of the column. This identifier indicates what data will
    * appear in the column, and the default column title that will appear
    * if the user has not provided a user defined column title.
    * 
    * @return column type
    */
   public int getFieldType()
   {
      return m_fieldType;
   }

   /**
    * Retrieves the user defined column title. If the column has not had a
    * user defined title provided for it, then the column will be headed
    * by the default title text, and this method will return null.
    * 
    * @return user defined column title
    */
   public String getTitle()
   {
      return m_title;
   }

   /**
    * Retrieves the width of the column represented as a number of 
    * characters.
    * 
    * @return column width
    */
   public int getWidth()
   {
      return m_width;
   }

   /**
    * Sets the alignment of the data in the column.
    * 
    * @param alignment data alignment
    */
   public void setAlignData(int alignment)
   {
      m_alignData = alignment;
   }

   /**
    * Sets the alignment of the column title
    * 
    * @param alignment column title alignment
    */
   public void setAlignTitle(int alignment)
   {
      m_alignTitle = alignment;
   }

   /**
    * Sets the column type
    * 
    * @param type column type
    */
   public void setFieldType(int type)
   {
      m_fieldType = type;
   }

   /**
    * Sets the user defined column title
    * 
    * @param title user defined column title
    */
   public void setTitle(String title)
   {
      m_title = title;
   }

   /**
    * Sets the width of the column in characters
    * 
    * @param width column width
    */
   public void setWidth(int width)
   {
      m_width = width;
   }

   /**
    * This method dumps the contents of this column as a String.
    * Note that this facility is provided as a debugging aid.
    *
    * @return formatted contents of this column
    */
   public String toString ()
   {
      StringWriter sw = new StringWriter ();
      PrintWriter pw = new PrintWriter (sw);
   
      pw.print ("[Column type=");
      pw.print (m_fieldType);
      
      pw.print (" width=");
      pw.print (m_width);
      
      pw.print (" titleAlignment=");
      if (m_alignTitle == ALIGN_LEFT)
      {
         pw.print ("LEFT");
      }
      else
      {
         if (m_alignTitle == ALIGN_CENTER)
         {
            pw.print ("CENTER");         
         }
         else
         {
            pw.print ("RIGHT");            
         }         
      }
      
      pw.print (" dataAlignment=");
      if (m_alignData == ALIGN_LEFT)
      {
         pw.print ("LEFT");
      }
      else
      {
         if (m_alignData == ALIGN_CENTER)
         {
            pw.print ("CENTER");         
         }
         else
         {
            pw.print ("RIGHT");            
         }         
      }

      pw.print (" userDefinedTitle=");
      pw.print (m_title);
      pw.println ("]");
      pw.close();
      
      return (sw.toString());      
   }

   /**
    * Column alignment constants.
    */
   public static final int ALIGN_LEFT = 1;
   public static final int ALIGN_CENTER = 2;
   public static final int ALIGN_RIGHT = 3;   

   /**
    * Task column type constants.
    */
   public static final int TASK_WORK = 0;      
   public static final int TASK_BASELINE_WORK = 1;
   public static final int TASK_ACTUAL_WORK = 2;
   public static final int TASK_WORK_VARIANCE = 3;   
   public static final int TASK_REMAINING_WORK = 4;
   public static final int TASK_COST = 5;   
   public static final int TASK_BASELINE_COST = 6;
   public static final int TASK_ACTUAL_COST = 7;
   public static final int TASK_FIXED_COST = 8;
   public static final int TASK_COST_VARIANCE = 9;
   public static final int TASK_REMAINING_COST = 10;   
   public static final int TASK_BCWP = 11;
   public static final int TASK_BCWS = 12;
   public static final int TASK_SV = 13;   
   public static final int TASK_NAME = 14;  
   public static final int TASK_WBS = 16;   
   public static final int TASK_CONSTRAINT_TYPE = 17;
   public static final int TASK_CONSTRAINT_DATE = 18;
   public static final int TASK_CRITICAL = 19; 
   public static final int TASK_LEVELING_DELAY = 20;   
   public static final int TASK_FREE_SLACK = 21;
   public static final int TASK_TOTAL_SLACK = 22;
   public static final int TASK_ID = 23;
   public static final int TASK_MILESTONE = 24;
   public static final int TASK_PRIORITY = 25;
   public static final int TASK_SUBPROJECT_FILE = 26;      
   public static final int TASK_BASELINE_DURATION = 27;
   public static final int TASK_ACTUAL_DURATION = 28;
   public static final int TASK_DURATION = 29;
   public static final int TASK_DURATION_VARIANCE = 30;
   public static final int TASK_REMAINING_DURATION = 31;
   public static final int TASK_PERCENT_COMPLETE = 32;
   public static final int TASK_PERCENT_WORK_COMPLETE = 33;   
   public static final int TASK_START = 35;
   public static final int TASK_FINISH = 36;   
   public static final int TASK_EARLY_START = 37;
   public static final int TASK_EARLY_FINISH = 38;
   public static final int TASK_LATE_START = 39;
   public static final int TASK_LATE_FINISH = 40;   
   public static final int TASK_ACTUAL_START = 41;
   public static final int TASK_ACTUAL_FINISH = 42;
   public static final int TASK_BASELINE_START = 43;
   public static final int TASK_BASELINE_FINISH = 44;
   public static final int TASK_START_VARIANCE = 45;   
   public static final int TASK_FINISH_VARIANCE = 46;
   public static final int TASK_PREDECESSORS = 47;
   public static final int TASK_SUCCESSORS = 48;   
   public static final int TASK_RESOURCE_NAMES = 49;
   public static final int TASK_RESOURCE_INITIALS = 50;
   public static final int TASK_TEXT1 = 51;
   public static final int TASK_START1 = 52;   
   public static final int TASK_FINISH1 = 53;
   public static final int TASK_TEXT2 = 54;
   public static final int TASK_START2 = 55;   
   public static final int TASK_FINISH2 = 56;
   public static final int TASK_TEXT3 = 57;
   public static final int TASK_START3 = 58;   
   public static final int TASK_FINISH3 = 59;
   public static final int TASK_TEXT4 = 60;
   public static final int TASK_START4 = 61;   
   public static final int TASK_FINISH4 = 62;   
   public static final int TASK_TEXT5 = 63;
   public static final int TASK_START5 = 64;      
   public static final int TASK_FINISH5 = 65;   
   public static final int TASK_TEXT6 = 66;
   public static final int TASK_TEXT7 = 67;   
   public static final int TASK_TEXT8 = 68;   
   public static final int TASK_TEXT9 = 69;   
   public static final int TASK_TEXT10 = 70; 
   public static final int TASK_MARKED = 71;
   public static final int TASK_FLAG1 = 72;
   public static final int TASK_FLAG2 = 73;
   public static final int TASK_FLAG3 = 74;      
   public static final int TASK_FLAG4 = 75;
   public static final int TASK_FLAG5 = 76;
   public static final int TASK_FLAG6 = 77;
   public static final int TASK_FLAG7 = 78;
   public static final int TASK_FLAG8 = 79;
   public static final int TASK_FLAG9 = 80;
   public static final int TASK_FLAG10 = 81;
   public static final int TASK_ROLLUP = 82;  
   public static final int TASK_CV = 83;
   public static final int TASK_PROJECT = 84;
   public static final int TASK_OUTLINE_LEVEL = 85;
   public static final int TASK_UNIQUE_ID = 86;   
   public static final int TASK_NUMBER1 = 87;
   public static final int TASK_NUMBER2 = 88;
   public static final int TASK_NUMBER3 = 89;
   public static final int TASK_NUMBER4 = 90;         
   public static final int TASK_NUMBER5 = 91;
   public static final int TASK_SUMMARY = 92;   
   public static final int TASK_CREATED = 93;
   public static final int TASK_NOTES = 94;
   public static final int TASK_UNIQUE_ID_PREDECESSORS = 95;
   public static final int TASK_UNIQUE_ID_SUCCESSORS = 96;   
   public static final int TASK_OBJECTS = 97;
   public static final int TASK_LINKED_FIELDS = 98;
   public static final int TASK_RESUME = 99;
   public static final int TASK_STOP = 100;         
   public static final int TASK_OUTLINE_NUMBER = 102;
   public static final int TASK_DURATION1 = 103;
   public static final int TASK_DURATION2 = 104;
   public static final int TASK_DURATION3 = 105;         
   public static final int TASK_COST1 = 106;
   public static final int TASK_COST2 = 107;
   public static final int TASK_COST3 = 108;   
   public static final int TASK_HIDEBAR = 109;      
   public static final int TASK_CONFIRMED = 110;
   public static final int TASK_UPDATE_NEEDED = 111;   
   public static final int TASK_CONTACT = 112;
   public static final int TASK_RESOURCE_GROUP = 113;   
   public static final int TASK_ACWP = 120;
   public static final int TASK_TYPE = 128;
   public static final int TASK_RECURRING = 129;      
   public static final int TASK_EFFORT_DRIVEN = 132;   
   public static final int TASK_OVERTIME_WORK = 163;   
   public static final int TASK_ACTUAL_OVERTIME_WORK = 164;
   public static final int TASK_REMAINING_OVERTIME_WORK = 165;
   public static final int TASK_REGULAR_WORK = 166;         
   public static final int TASK_OVERTIME_COST = 168;   
   public static final int TASK_ACTUAL_OVERTIME_COST = 169;
   public static final int TASK_REMAINING_OVERTIME_COST = 170;            
   public static final int TASK_FIXED_COST_ACCRUAL = 200;   
   public static final int TASK_INDICATORS = 205;
   public static final int TASK_HYPERLINK = 217;
   public static final int TASK_HYPERLINK_ADDRESS = 218;
   public static final int TASK_HYPERLINK_SUBADDRESS = 219;
   public static final int TASK_HYPERLINK_HREF = 220;   
   public static final int TASK_ASSIGNMENT = 224;
   public static final int TASK_OVERALLOCATED = 225;   
   public static final int TASK_EXTERNAL_TASK = 232;
   public static final int TASK_SUBPROJECT_READ_ONLY = 246;   
   public static final int TASK_RESPONSE_PENDING = 250;
   public static final int TASK_TEAMSTATUS_PENDING = 251;   
   public static final int TASK_LEVELING_CAN_SPLIT = 252;
   public static final int TASK_LEVEL_ASSIGNMENTS = 253;
   public static final int TASK_WORK_CONTOUR = 256;         
   public static final int TASK_COST4 = 258;            
   public static final int TASK_COST5 = 259;
   public static final int TASK_COST6 = 260;
   public static final int TASK_COST7 = 261;
   public static final int TASK_COST8 = 262;
   public static final int TASK_COST9 = 263;
   public static final int TASK_COST10 = 264;
   public static final int TASK_DATE1 = 265;
   public static final int TASK_DATE2 = 266;
   public static final int TASK_DATE3 = 267;
   public static final int TASK_DATE4 = 268;
   public static final int TASK_DATE5 = 269;   
   public static final int TASK_DATE6 = 260;
   public static final int TASK_DATE7 = 271;
   public static final int TASK_DATE8 = 272;
   public static final int TASK_DATE9 = 273;   
   public static final int TASK_DATE10 = 274;
   public static final int TASK_DURATION4 = 275;
   public static final int TASK_DURATION5 = 276;
   public static final int TASK_DURATION6 = 277;
   public static final int TASK_DURATION7 = 278;
   public static final int TASK_DURATION8 = 279;
   public static final int TASK_DURATION9 = 280;
   public static final int TASK_DURATION10 = 281;
   public static final int TASK_START6 = 282;         
   public static final int TASK_FINISH6 = 283;
   public static final int TASK_START7 = 284;            
   public static final int TASK_FINISH7 = 285;
   public static final int TASK_START8 = 286;               
   public static final int TASK_FINISH8 = 287;   
   public static final int TASK_START9 = 288;            
   public static final int TASK_FINISH9 = 289;
   public static final int TASK_START10 = 290;      
   public static final int TASK_FINISH10 = 291;   
   public static final int TASK_FLAG11 = 292;
   public static final int TASK_FLAG12 = 293;
   public static final int TASK_FLAG13 = 294;
   public static final int TASK_FLAG14 = 295;
   public static final int TASK_FLAG15 = 296;
   public static final int TASK_FLAG16 = 297;
   public static final int TASK_FLAG17 = 298;
   public static final int TASK_FLAG18 = 299;
   public static final int TASK_FLAG19 = 300;                  
   public static final int TASK_FLAG20 = 301;
   public static final int TASK_NUMBER6 = 302;
   public static final int TASK_NUMBER7 = 303;
   public static final int TASK_NUMBER8 = 304;
   public static final int TASK_NUMBER9 = 305;
   public static final int TASK_NUMBER10 = 306;
   public static final int TASK_NUMBER11 = 307;
   public static final int TASK_NUMBER12 = 308;
   public static final int TASK_NUMBER13 = 309;
   public static final int TASK_NUMBER14 = 310;
   public static final int TASK_NUMBER15 = 311;
   public static final int TASK_NUMBER16 = 312;
   public static final int TASK_NUMBER17 = 313;
   public static final int TASK_NUMBER18 = 314;
   public static final int TASK_NUMBER19 = 315;                           
   public static final int TASK_NUMBER20 = 316;
   public static final int TASK_TEXT11 = 317;
   public static final int TASK_TEXT12 = 318;
   public static final int TASK_TEXT13 = 319;
   public static final int TASK_TEXT14 = 320;
   public static final int TASK_TEXT15 = 321;
   public static final int TASK_TEXT16 = 322;
   public static final int TASK_TEXT17 = 323;
   public static final int TASK_TEXT18 = 324;
   public static final int TASK_TEXT19 = 325;         
   public static final int TASK_TEXT20 = 326;
   public static final int TASK_TEXT21 = 327;
   public static final int TASK_TEXT22 = 328;
   public static final int TASK_TEXT23 = 329;
   public static final int TASK_TEXT24 = 330;
   public static final int TASK_TEXT25 = 331;               
   public static final int TASK_TEXT26 = 332;
   public static final int TASK_TEXT27 = 333;
   public static final int TASK_TEXT28 = 334;
   public static final int TASK_TEXT29 = 335;            
   public static final int TASK_TEXT30 = 336;
   public static final int TASK_RESOURCE_PHONETICS = 349;   
   public static final int TASK_ASSIGNMENT_DELAY = 366;
   public static final int TASK_ASSIGNMENT_UNITS = 367;
   public static final int TASK_COST_RATE_TABLE = 368;
   public static final int TASK_PRELEVELED_START = 369;
   public static final int TASK_PRELEVELED_FINISH = 370;
   public static final int TASK_ESTIMATED = 396;
   public static final int TASK_IGNORE_RESOURCE_CALENDAR = 399;
   public static final int TASK_TASK_CALENDAR = 402;            
   public static final int TASK_OUTLINE_CODE1 = 416;      
   public static final int TASK_OUTLINE_CODE2 = 418;
   public static final int TASK_OUTLINE_CODE3 = 420;
   public static final int TASK_OUTLINE_CODE4 = 422;
   public static final int TASK_OUTLINE_CODE5 = 424;
   public static final int TASK_OUTLINE_CODE6 = 426;
   public static final int TASK_OUTLINE_CODE7 = 428;
   public static final int TASK_OUTLINE_CODE8 = 430;
   public static final int TASK_OUTLINE_CODE9 = 432;
   public static final int TASK_OUTLINE_CODE10 = 434;     
   public static final int TASK_DEADLINE = 437;
   public static final int TASK_START_SLACK = 438;
   public static final int TASK_FINISG_SLACK = 439;
   public static final int TASK_VAC = 441;   
   public static final int TASK_GROUP_BY_SUMMARY = 446;
   public static final int TASK_WBS_PREDECESSORS = 449;
   public static final int TASK_WBS_SUCCESSORS = 450;
   public static final int TASK_RESOURCE_TYPE = 451;


   /**
    * Resource column type constants.
    */
   public static final int RESOURCE_ID = 0;
   public static final int RESOURCE_NAME = 1;   
   public static final int RESOURCE_INITIALS = 2;   
   public static final int RESOURCE_GROUP = 3;   
   public static final int RESOURCE_MAX_UNITS = 4;      
   public static final int RESOURCE_BASE_CALENDAR = 5;
   public static final int RESOURCE_STANDARD_RATE = 6;   
   public static final int RESOURCE_OVERTIME_RATE = 7;   
   public static final int RESOURCE_TEXT1 = 8;
   public static final int RESOURCE_TEXT2 = 9;      
   public static final int RESOURCE_CODE = 10;   
   public static final int RESOURCE_ACTUAL_COST = 11;
   public static final int RESOURCE_COST = 12;      
   public static final int RESOURCE_WORK = 13;   
   public static final int RESOURCE_ACTUAL_WORK = 14;
   public static final int RESOURCE_BASELINE_WORK = 15;   
   public static final int RESOURCE_OVERTIME_WORK = 16;   
   public static final int RESOURCE_BASELINE_COST = 17;   
   public static final int RESOURCE_COST_PER_USE = 18;   
   public static final int RESOURCE_ACCRUE_AT = 19;   
   public static final int RESOURCE_REMAINING_COST = 21;   
   public static final int RESOURCE_REMAINING_WORK = 22;  
   public static final int RESOURCE_WORK_VARIANCE = 23;    
   public static final int RESOURCE_COST_VARIANCE = 24;   
   public static final int RESOURCE_OVERALLOCATED = 25;   
   public static final int RESOURCE_PEAK = 26;   
   public static final int RESOURCE_UNIQUE_ID = 27;   
   public static final int RESOURCE_NOTES = 28;   
   public static final int RESOURCE_PERCENT_WORK_COMPLETE = 29;
   public static final int RESOURCE_TEXT3 = 30;   
   public static final int RESOURCE_TEXT4 = 31;      
   public static final int RESOURCE_TEXT5 = 32;   
   public static final int RESOURCE_OBJECTS = 33;   
   public static final int RESOURCE_LINKED_FIELDS = 34;   
   public static final int RESOURCE_EMAIL_ADDRESS = 35;   
   public static final int RESOURCE_REGULAR_WORK = 38;   
   public static final int RESOURCE_ACTUAL_OVERTIME_WORK = 39;   
   public static final int RESOURCE_REMAINING_OVERTIME_WORK = 40;   
   public static final int RESOURCE_OVERTIME_COST = 47;   
   public static final int RESOURCE_ACTUAL_OVERTIME_COST = 48;
   public static final int RESOURCE_REMAINING_OVERTIME_COST = 49;   
   public static final int RESOURCE_BCWS = 51;
   public static final int RESOURCE_BCWP = 52;   
   public static final int RESOURCE_ACWP = 53;
   public static final int RESOURCE_SV = 54;   
   public static final int RESOURCE_AVAILABLE_FROM = 57;
   public static final int RESOURCE_AVAILABLE_TO = 58;  
   public static final int RESOURCE_INDICATORS = 86;   
   public static final int RESOURCE_TEXT6 = 97;   
   public static final int RESOURCE_TEXT7 = 98;      
   public static final int RESOURCE_TEXT8 = 99;         
   public static final int RESOURCE_TEXT9 = 100;         
   public static final int RESOURCE_TEXT10 = 101;   
   public static final int RESOURCE_START1 = 102;
   public static final int RESOURCE_START2 = 103;
   public static final int RESOURCE_START3 = 104;
   public static final int RESOURCE_START4 = 105;         
   public static final int RESOURCE_START5 = 106;   
   public static final int RESOURCE_FINISH1 = 107;
   public static final int RESOURCE_FINISH2 = 108;
   public static final int RESOURCE_FINISH3 = 109;
   public static final int RESOURCE_FINISH4 = 110;            
   public static final int RESOURCE_FINISH5 = 111;      
   public static final int RESOURCE_NUMBER1 = 112;
   public static final int RESOURCE_NUMBER2 = 113;
   public static final int RESOURCE_NUMBER3 = 114;
   public static final int RESOURCE_NUMBER4 = 115;      
   public static final int RESOURCE_NUMBER5 = 116;      
   public static final int RESOURCE_DURATION1 = 117;
   public static final int RESOURCE_DURATION2 = 118;   
   public static final int RESOURCE_DURATION3 = 119;      
   public static final int RESOURCE_COST1 = 123;
   public static final int RESOURCE_COST2 = 124;
   public static final int RESOURCE_COST3 = 125;         
   public static final int RESOURCE_FLAG10 = 126;
   public static final int RESOURCE_FLAG1 = 127;
   public static final int RESOURCE_FLAG2 = 128;
   public static final int RESOURCE_FLAG3 = 129;
   public static final int RESOURCE_FLAG4 = 130;   
   public static final int RESOURCE_FLAG5 = 131;
   public static final int RESOURCE_FLAG6 = 132;   
   public static final int RESOURCE_FLAG7 = 133;   
   public static final int RESOURCE_FLAG8 = 134;         
   public static final int RESOURCE_FLAG9 = 135;         
   public static final int RESOURCE_HYPERLINK = 138;    
   public static final int RESOURCE_HYPERLINK_ADDRESS = 139;   
   public static final int RESOURCE_HYPERLINK_SUBADDRESS = 140;
   public static final int RESOURCE_HYPERLINK_HREF = 141;   
   public static final int RESOURCE_ASSIGNMENT = 144;   
   public static final int RESOURCE_TASK_SUMMARY_NAME = 159;   
   public static final int RESOURCE_CAN_LEVEL = 163;   
   public static final int RESOURCE_WORK_CONTOUR = 164;   
   public static final int RESOURCE_COST4 = 166;      
   public static final int RESOURCE_COST5 = 167;
   public static final int RESOURCE_COST6 = 168;
   public static final int RESOURCE_COST7 = 169;   
   public static final int RESOURCE_COST8 = 170;
   public static final int RESOURCE_COST9 = 171;   
   public static final int RESOURCE_COST10 = 172;   
   public static final int RESOURCE_DATE1 = 173;
   public static final int RESOURCE_DATE2 = 174;
   public static final int RESOURCE_DATE3 = 175;
   public static final int RESOURCE_DATE4 = 176;         
   public static final int RESOURCE_DATE5 = 177;   
   public static final int RESOURCE_DATE6 = 178;
   public static final int RESOURCE_DATE7 = 179;
   public static final int RESOURCE_DATE8 = 180;
   public static final int RESOURCE_DATE9 = 181;   
   public static final int RESOURCE_DATE10 = 182;  
   public static final int RESOURCE_DURATION4 = 183;   
   public static final int RESOURCE_DURATION5 = 184;
   public static final int RESOURCE_DURATION6 = 185;
   public static final int RESOURCE_DURATION7 = 186;
   public static final int RESOURCE_DURATION8 = 187;
   public static final int RESOURCE_DURATION9 = 188;            
   public static final int RESOURCE_DURATION10 = 189;   
   public static final int RESOURCE_FINISH6 = 190;      
   public static final int RESOURCE_FINISH7 = 191;      
   public static final int RESOURCE_FINISH8 = 192;      
   public static final int RESOURCE_FINISH9 = 193;               
   public static final int RESOURCE_FINISH10 = 194;   
   public static final int RESOURCE_FLAG11 = 195;
   public static final int RESOURCE_FLAG12 = 196;
   public static final int RESOURCE_FLAG13 = 197;
   public static final int RESOURCE_FLAG14 = 198;
   public static final int RESOURCE_FLAG15 = 199;
   public static final int RESOURCE_FLAG16 = 200;
   public static final int RESOURCE_FLAG17 = 201;
   public static final int RESOURCE_FLAG18 = 202;
   public static final int RESOURCE_FLAG19 = 203;                        
   public static final int RESOURCE_FLAG20 = 204;   
   public static final int RESOURCE_NUMBER6 = 205;   
   public static final int RESOURCE_NUMBER7 = 206;   
   public static final int RESOURCE_NUMBER8 = 207;   
   public static final int RESOURCE_NUMBER9 = 208;            
   public static final int RESOURCE_NUMBER10 = 209;
   public static final int RESOURCE_NUMBER11 = 210;
   public static final int RESOURCE_NUMBER12 = 211;
   public static final int RESOURCE_NUMBER13 = 212;
   public static final int RESOURCE_NUMBER14 = 213;
   public static final int RESOURCE_NUMBER15 = 214;
   public static final int RESOURCE_NUMBER16 = 215;
   public static final int RESOURCE_NUMBER17 = 216;
   public static final int RESOURCE_NUMBER18 = 217;
   public static final int RESOURCE_NUMBER19 = 218;                           
   public static final int RESOURCE_NUMBER20 = 219;   
   public static final int RESOURCE_START6 = 220;   
   public static final int RESOURCE_START7 = 221;   
   public static final int RESOURCE_START8 = 222;   
   public static final int RESOURCE_START9 = 223;            
   public static final int RESOURCE_START10 = 224;   
   public static final int RESOURCE_TEXT11 = 225;
   public static final int RESOURCE_TEXT12 = 226;
   public static final int RESOURCE_TEXT13 = 227;
   public static final int RESOURCE_TEXT14 = 228;
   public static final int RESOURCE_TEXT15 = 229;
   public static final int RESOURCE_TEXT16 = 230;
   public static final int RESOURCE_TEXT17 = 231;
   public static final int RESOURCE_TEXT18 = 232;
   public static final int RESOURCE_TEXT19 = 233;                              
   public static final int RESOURCE_TEXT20 = 234;
   public static final int RESOURCE_TEXT21 = 235;
   public static final int RESOURCE_TEXT22 = 236;
   public static final int RESOURCE_TEXT23 = 237;
   public static final int RESOURCE_TEXT24 = 238;
   public static final int RESOURCE_TEXT25 = 239;
   public static final int RESOURCE_TEXT26 = 240;
   public static final int RESOURCE_TEXT27 = 241;
   public static final int RESOURCE_TEXT28 = 242;
   public static final int RESOURCE_TEXT29 = 243;                           
   public static final int RESOURCE_TEXT30 = 244;   
   public static final int RESOURCE_PHONETICS = 252;    
   public static final int RESOURCE_ASSIGNMENT_DELAY = 257;
   public static final int RESOURCE_ASSIGNMENT_UNITS = 258;   
   public static final int RESOURCE_BASELINE_START = 259;
   public static final int RESOURCE_BASELINE_FINISH = 260;   
   public static final int RESOURCE_CONFIRMED = 261;   
   public static final int RESOURCE_FINISH = 262;   
   public static final int RESOURCE_LEVELING_DELAY = 263;   
   public static final int RESOURCE_RESPONSE_PENDING = 264;   
   public static final int RESOURCE_START = 265;   
   public static final int RESOURCE_TEAM_STATUS_PENDING = 266;   
   public static final int RESOURCE_CV = 268;
   public static final int RESOURCE_UPDATE_NEEDED = 267;   
   public static final int RESOURCE_COST_RATE_TABLE = 269;   
   public static final int RESOURCE_ACTUAL_START = 270;
   public static final int RESOURCE_ACTUAL_FINISH = 271;
   public static final int RESOURCE_WORKGROUP = 272;   
   public static final int RESOURCE_PROJECT = 273;   
   public static final int RESOURCE_OUTLINE_CODE1 = 278;
   public static final int RESOURCE_OUTLINE_CODE2 = 280;
   public static final int RESOURCE_OUTLINE_CODE3 = 282;
   public static final int RESOURCE_OUTLINE_CODE4 = 284;               
   public static final int RESOURCE_OUTLINE_CODE5 = 286;      
   public static final int RESOURCE_OUTLINE_CODE6 = 288;
   public static final int RESOURCE_OUTLINE_CODE7 = 290;
   public static final int RESOURCE_OUTLINE_CODE8 = 292;
   public static final int RESOURCE_OUTLINE_CODE9 = 294;                     
   public static final int RESOURCE_OUTLINE_CODE10 = 296;   
   public static final int RESOURCE_MATERIAL_LABEL = 299;   
   public static final int RESOURCE_TYPE = 300;
   public static final int RESOURCE_VAC = 301;   
   public static final int RESOURCE_GROUP_BY_SUMMARY = 306;     
   public static final int RESOURCE_WINDOWS_USER_ACCOUNT = 311;
   
  
 
   private int m_fieldType;
   private int m_width;
   private int m_alignTitle;
   private int m_alignData;
   private String m_title;
}
