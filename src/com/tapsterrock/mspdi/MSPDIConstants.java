/*
 * file:       MSPDIConstants.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2002-2006
 * date:       01/01/2006
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

package com.tapsterrock.mspdi;

import java.math.BigInteger;
import java.util.HashMap;

import com.tapsterrock.mpx.Resource;
import com.tapsterrock.mpx.Task;

/**
 * This class contains definitions of constants used when reading and writing
 * MSPDI files.
 */
final class MSPDIConstants
{
   /**
    * Constructor.
    */
   private MSPDIConstants ()
   {
      // Private constructor to prevent instantiation.      
   }

   public static final Integer NULL_RESOURCE_ID = new Integer (-65535);
   
   public static final int TASK_FIELD_PREFIX = 1887;
   public static final int RESOURCE_FIELD_PREFIX = 2055;

   public static final int MSPDI_FIELD_ID = 0;
   public static final int MPX_FIELD_ID = 1;
   public static final int MSPDI_FIELD_NAME = 2;
   public static final int FIELD_DATA_TYPE = 3;

   static final int STRING_ATTRIBUTE = 1;
   static final int DATE_ATTRIBUTE = 2;
   static final int CURRENCY_ATTRIBUTE = 3;
   static final int BOOLEAN_ATTRIBUTE = 4;
   static final int NUMERIC_ATTRIBUTE = 5;
   static final int DURATION_ATTRIBUTE = 6;

   private static final Integer STRING_ATTRIBUTE_OBJECT = new Integer (STRING_ATTRIBUTE);
   private static final Integer DATE_ATTRIBUTE_OBJECT = new Integer (DATE_ATTRIBUTE);
   private static final Integer CURRENCY_ATTRIBUTE_OBJECT = new Integer (CURRENCY_ATTRIBUTE);
   private static final Integer BOOLEAN_ATTRIBUTE_OBJECT = new Integer (BOOLEAN_ATTRIBUTE);
   private static final Integer NUMERIC_ATTRIBUTE_OBJECT = new Integer (NUMERIC_ATTRIBUTE);
   private static final Integer DURATION_ATTRIBUTE_OBJECT = new Integer (DURATION_ATTRIBUTE);

   public static final Object[][] TASK_DATA =
   {
      {new Integer(188743731), new Integer(Task.TEXT1), "Text1", STRING_ATTRIBUTE_OBJECT},
      {new Integer(188743734), new Integer(Task.TEXT2), "Text2", STRING_ATTRIBUTE_OBJECT},
      {new Integer(188743737), new Integer(Task.TEXT3), "Text3", STRING_ATTRIBUTE_OBJECT},
      {new Integer(188743740), new Integer(Task.TEXT4), "Text4", STRING_ATTRIBUTE_OBJECT},
      {new Integer(188743743), new Integer(Task.TEXT5), "Text5", STRING_ATTRIBUTE_OBJECT},
      {new Integer(188743746), new Integer(Task.TEXT6), "Text6", STRING_ATTRIBUTE_OBJECT},
      {new Integer(188743747), new Integer(Task.TEXT7), "Text7", STRING_ATTRIBUTE_OBJECT},
      {new Integer(188743748), new Integer(Task.TEXT8), "Text8", STRING_ATTRIBUTE_OBJECT},
      {new Integer(188743749), new Integer(Task.TEXT9), "Text9", STRING_ATTRIBUTE_OBJECT},
      {new Integer(188743750), new Integer(Task.TEXT10), "Text10", STRING_ATTRIBUTE_OBJECT},
      {new Integer(188743997), new Integer(Task.TEXT11), "Text11", STRING_ATTRIBUTE_OBJECT},
      {new Integer(188743998), new Integer(Task.TEXT12), "Text12", STRING_ATTRIBUTE_OBJECT},
      {new Integer(188743999), new Integer(Task.TEXT13), "Text13", STRING_ATTRIBUTE_OBJECT},
      {new Integer(188744000), new Integer(Task.TEXT14), "Text14", STRING_ATTRIBUTE_OBJECT},
      {new Integer(188744001), new Integer(Task.TEXT15), "Text15", STRING_ATTRIBUTE_OBJECT},
      {new Integer(188744002), new Integer(Task.TEXT16), "Text16", STRING_ATTRIBUTE_OBJECT},
      {new Integer(188744003), new Integer(Task.TEXT17), "Text17", STRING_ATTRIBUTE_OBJECT},
      {new Integer(188744004), new Integer(Task.TEXT18), "Text18", STRING_ATTRIBUTE_OBJECT},
      {new Integer(188744005), new Integer(Task.TEXT19), "Text19", STRING_ATTRIBUTE_OBJECT},
      {new Integer(188744006), new Integer(Task.TEXT20), "Text20", STRING_ATTRIBUTE_OBJECT},
      {new Integer(188744007), new Integer(Task.TEXT21), "Text21", STRING_ATTRIBUTE_OBJECT},
      {new Integer(188744008), new Integer(Task.TEXT22), "Text22", STRING_ATTRIBUTE_OBJECT},
      {new Integer(188744009), new Integer(Task.TEXT23), "Text23", STRING_ATTRIBUTE_OBJECT},
      {new Integer(188744010), new Integer(Task.TEXT24), "Text24", STRING_ATTRIBUTE_OBJECT},
      {new Integer(188744011), new Integer(Task.TEXT25), "Text25", STRING_ATTRIBUTE_OBJECT},
      {new Integer(188744012), new Integer(Task.TEXT26), "Text26", STRING_ATTRIBUTE_OBJECT},
      {new Integer(188744013), new Integer(Task.TEXT27), "Text27", STRING_ATTRIBUTE_OBJECT},
      {new Integer(188744014), new Integer(Task.TEXT28), "Text28", STRING_ATTRIBUTE_OBJECT},
      {new Integer(188744015), new Integer(Task.TEXT29), "Text29", STRING_ATTRIBUTE_OBJECT},
      {new Integer(188744016), new Integer(Task.TEXT30), "Text30", STRING_ATTRIBUTE_OBJECT},
      {new Integer(188743732), new Integer(Task.START1), "Start1", DATE_ATTRIBUTE_OBJECT},
      {new Integer(188743735), new Integer(Task.START2), "Start2", DATE_ATTRIBUTE_OBJECT},
      {new Integer(188743738), new Integer(Task.START3), "Start3", DATE_ATTRIBUTE_OBJECT},
      {new Integer(188743741), new Integer(Task.START4), "Start4", DATE_ATTRIBUTE_OBJECT},
      {new Integer(188743744), new Integer(Task.START5), "Start5", DATE_ATTRIBUTE_OBJECT},
      {new Integer(188743962), new Integer(Task.START6), "Start6", DATE_ATTRIBUTE_OBJECT},
      {new Integer(188743964), new Integer(Task.START7), "Start7", DATE_ATTRIBUTE_OBJECT},
      {new Integer(188743966), new Integer(Task.START8), "Start8", DATE_ATTRIBUTE_OBJECT},
      {new Integer(188743968), new Integer(Task.START9), "Start9", DATE_ATTRIBUTE_OBJECT},
      {new Integer(188743970), new Integer(Task.START10), "Start10", DATE_ATTRIBUTE_OBJECT},
      {new Integer(188743733), new Integer(Task.FINISH1), "Finish1", DATE_ATTRIBUTE_OBJECT},
      {new Integer(188743736), new Integer(Task.FINISH2), "Finish2", DATE_ATTRIBUTE_OBJECT},
      {new Integer(188743739), new Integer(Task.FINISH3), "Finish3", DATE_ATTRIBUTE_OBJECT},
      {new Integer(188743742), new Integer(Task.FINISH4), "Finish4", DATE_ATTRIBUTE_OBJECT},
      {new Integer(188743745), new Integer(Task.FINISH5), "Finish5", DATE_ATTRIBUTE_OBJECT},
      {new Integer(188743963), new Integer(Task.FINISH6), "Finish6", DATE_ATTRIBUTE_OBJECT},
      {new Integer(188743965), new Integer(Task.FINISH7), "Finish7", DATE_ATTRIBUTE_OBJECT},
      {new Integer(188743967), new Integer(Task.FINISH8), "Finish8", DATE_ATTRIBUTE_OBJECT},
      {new Integer(188743969), new Integer(Task.FINISH9), "Finish9", DATE_ATTRIBUTE_OBJECT},
      {new Integer(188743971), new Integer(Task.FINISH10), "Finish10", DATE_ATTRIBUTE_OBJECT},
      {new Integer(188743786), new Integer(Task.COST1), "Cost1", CURRENCY_ATTRIBUTE_OBJECT},
      {new Integer(188743787), new Integer(Task.COST2), "Cost2", CURRENCY_ATTRIBUTE_OBJECT},
      {new Integer(188743788), new Integer(Task.COST3), "Cost3", CURRENCY_ATTRIBUTE_OBJECT},
      {new Integer(188743938), new Integer(Task.COST4), "Cost4", CURRENCY_ATTRIBUTE_OBJECT},
      {new Integer(188743939), new Integer(Task.COST5), "Cost5", CURRENCY_ATTRIBUTE_OBJECT},
      {new Integer(188743940), new Integer(Task.COST6), "Cost6", CURRENCY_ATTRIBUTE_OBJECT},
      {new Integer(188743941), new Integer(Task.COST7), "Cost7", CURRENCY_ATTRIBUTE_OBJECT},
      {new Integer(188743942), new Integer(Task.COST8), "Cost8", CURRENCY_ATTRIBUTE_OBJECT},
      {new Integer(188743943), new Integer(Task.COST9), "Cost9", CURRENCY_ATTRIBUTE_OBJECT},
      {new Integer(188743944), new Integer(Task.COST10), "Cost10", CURRENCY_ATTRIBUTE_OBJECT},
      {new Integer(188743945), new Integer(Task.DATE1), "Date1", DATE_ATTRIBUTE_OBJECT},
      {new Integer(188743946), new Integer(Task.DATE2), "Date2", DATE_ATTRIBUTE_OBJECT},
      {new Integer(188743947), new Integer(Task.DATE3), "Date3", DATE_ATTRIBUTE_OBJECT},
      {new Integer(188743948), new Integer(Task.DATE4), "Date4", DATE_ATTRIBUTE_OBJECT},
      {new Integer(188743949), new Integer(Task.DATE5), "Date5", DATE_ATTRIBUTE_OBJECT},
      {new Integer(188743950), new Integer(Task.DATE6), "Date6", DATE_ATTRIBUTE_OBJECT},
      {new Integer(188743951), new Integer(Task.DATE7), "Date7", DATE_ATTRIBUTE_OBJECT},
      {new Integer(188743952), new Integer(Task.DATE8), "Date8", DATE_ATTRIBUTE_OBJECT},
      {new Integer(188743953), new Integer(Task.DATE9), "Date9", DATE_ATTRIBUTE_OBJECT},
      {new Integer(188743954), new Integer(Task.DATE10), "Date10", DATE_ATTRIBUTE_OBJECT},
      {new Integer(188743752), new Integer(Task.FLAG1), "Flag1", BOOLEAN_ATTRIBUTE_OBJECT},
      {new Integer(188743753), new Integer(Task.FLAG2), "Flag2", BOOLEAN_ATTRIBUTE_OBJECT},
      {new Integer(188743754), new Integer(Task.FLAG3), "Flag3", BOOLEAN_ATTRIBUTE_OBJECT},
      {new Integer(188743755), new Integer(Task.FLAG4), "Flag4", BOOLEAN_ATTRIBUTE_OBJECT},
      {new Integer(188743756), new Integer(Task.FLAG5), "Flag5", BOOLEAN_ATTRIBUTE_OBJECT},
      {new Integer(188743757), new Integer(Task.FLAG6), "Flag6", BOOLEAN_ATTRIBUTE_OBJECT},
      {new Integer(188743758), new Integer(Task.FLAG7), "Flag7", BOOLEAN_ATTRIBUTE_OBJECT},
      {new Integer(188743759), new Integer(Task.FLAG8), "Flag8", BOOLEAN_ATTRIBUTE_OBJECT},
      {new Integer(188743760), new Integer(Task.FLAG9), "Flag9", BOOLEAN_ATTRIBUTE_OBJECT},
      {new Integer(188743761), new Integer(Task.FLAG10), "Flag10", BOOLEAN_ATTRIBUTE_OBJECT},
      {new Integer(188743972), new Integer(Task.FLAG11), "Flag11", BOOLEAN_ATTRIBUTE_OBJECT},
      {new Integer(188743973), new Integer(Task.FLAG12), "Flag12", BOOLEAN_ATTRIBUTE_OBJECT},
      {new Integer(188743974), new Integer(Task.FLAG13), "Flag13", BOOLEAN_ATTRIBUTE_OBJECT},
      {new Integer(188743975), new Integer(Task.FLAG14), "Flag14", BOOLEAN_ATTRIBUTE_OBJECT},
      {new Integer(188743976), new Integer(Task.FLAG15), "Flag15", BOOLEAN_ATTRIBUTE_OBJECT},
      {new Integer(188743977), new Integer(Task.FLAG16), "Flag16", BOOLEAN_ATTRIBUTE_OBJECT},
      {new Integer(188743978), new Integer(Task.FLAG17), "Flag17", BOOLEAN_ATTRIBUTE_OBJECT},
      {new Integer(188743979), new Integer(Task.FLAG18), "Flag18", BOOLEAN_ATTRIBUTE_OBJECT},
      {new Integer(188743980), new Integer(Task.FLAG19), "Flag19", BOOLEAN_ATTRIBUTE_OBJECT},
      {new Integer(188743981), new Integer(Task.FLAG20), "Flag20", BOOLEAN_ATTRIBUTE_OBJECT},
      {new Integer(188743767), new Integer(Task.NUMBER1), "Number1", NUMERIC_ATTRIBUTE_OBJECT},
      {new Integer(188743768), new Integer(Task.NUMBER2), "Number2", NUMERIC_ATTRIBUTE_OBJECT},
      {new Integer(188743769), new Integer(Task.NUMBER3), "Number3", NUMERIC_ATTRIBUTE_OBJECT},
      {new Integer(188743770), new Integer(Task.NUMBER4), "Number4", NUMERIC_ATTRIBUTE_OBJECT},
      {new Integer(188743771), new Integer(Task.NUMBER5), "Number5", NUMERIC_ATTRIBUTE_OBJECT},
      {new Integer(188743982), new Integer(Task.NUMBER6), "Number6", NUMERIC_ATTRIBUTE_OBJECT},
      {new Integer(188743983), new Integer(Task.NUMBER7), "Number7", NUMERIC_ATTRIBUTE_OBJECT},
      {new Integer(188743984), new Integer(Task.NUMBER8), "Number8", NUMERIC_ATTRIBUTE_OBJECT},
      {new Integer(188743985), new Integer(Task.NUMBER9), "Number9", NUMERIC_ATTRIBUTE_OBJECT},
      {new Integer(188743986), new Integer(Task.NUMBER10), "Number10", NUMERIC_ATTRIBUTE_OBJECT},
      {new Integer(188743987), new Integer(Task.NUMBER11), "Number11", NUMERIC_ATTRIBUTE_OBJECT},
      {new Integer(188743988), new Integer(Task.NUMBER12), "Number12", NUMERIC_ATTRIBUTE_OBJECT},
      {new Integer(188743989), new Integer(Task.NUMBER13), "Number13", NUMERIC_ATTRIBUTE_OBJECT},
      {new Integer(188743990), new Integer(Task.NUMBER14), "Number14", NUMERIC_ATTRIBUTE_OBJECT},
      {new Integer(188743991), new Integer(Task.NUMBER15), "Number15", NUMERIC_ATTRIBUTE_OBJECT},
      {new Integer(188743992), new Integer(Task.NUMBER16), "Number16", NUMERIC_ATTRIBUTE_OBJECT},
      {new Integer(188743993), new Integer(Task.NUMBER17), "Number17", NUMERIC_ATTRIBUTE_OBJECT},
      {new Integer(188743994), new Integer(Task.NUMBER18), "Number18", NUMERIC_ATTRIBUTE_OBJECT},
      {new Integer(188743995), new Integer(Task.NUMBER19), "Number19", NUMERIC_ATTRIBUTE_OBJECT},
      {new Integer(188743996), new Integer(Task.NUMBER20), "Number20", NUMERIC_ATTRIBUTE_OBJECT},
      {new Integer(188743783), new Integer(Task.DURATION1), "Duration1", DURATION_ATTRIBUTE_OBJECT},
      {new Integer(188743784), new Integer(Task.DURATION2), "Duration2", DURATION_ATTRIBUTE_OBJECT},
      {new Integer(188743785), new Integer(Task.DURATION3), "Duration3", DURATION_ATTRIBUTE_OBJECT},
      {new Integer(188743955), new Integer(Task.DURATION4), "Duration4", DURATION_ATTRIBUTE_OBJECT},
      {new Integer(188743956), new Integer(Task.DURATION5), "Duration5", DURATION_ATTRIBUTE_OBJECT},
      {new Integer(188743957), new Integer(Task.DURATION6), "Duration6", DURATION_ATTRIBUTE_OBJECT},
      {new Integer(188743958), new Integer(Task.DURATION7), "Duration7", DURATION_ATTRIBUTE_OBJECT},
      {new Integer(188743959), new Integer(Task.DURATION8), "Duration8", DURATION_ATTRIBUTE_OBJECT},
      {new Integer(188743960), new Integer(Task.DURATION9), "Duration9", DURATION_ATTRIBUTE_OBJECT},
      {new Integer(188743961), new Integer(Task.DURATION10), "Duration10", DURATION_ATTRIBUTE_OBJECT},
      {new Integer(188744096), new Integer(Task.OUTLINECODE1), "Outline Code1", STRING_ATTRIBUTE_OBJECT},
      {new Integer(188744098), new Integer(Task.OUTLINECODE2), "Outline Code2", STRING_ATTRIBUTE_OBJECT},
      {new Integer(188744100), new Integer(Task.OUTLINECODE3), "Outline Code3", STRING_ATTRIBUTE_OBJECT},
      {new Integer(188744102), new Integer(Task.OUTLINECODE4), "Outline Code4", STRING_ATTRIBUTE_OBJECT},
      {new Integer(188744104), new Integer(Task.OUTLINECODE5), "Outline Code5", STRING_ATTRIBUTE_OBJECT},
      {new Integer(188744106), new Integer(Task.OUTLINECODE6), "Outline Code6", STRING_ATTRIBUTE_OBJECT},
      {new Integer(188744108), new Integer(Task.OUTLINECODE7), "Outline Code7", STRING_ATTRIBUTE_OBJECT},
      {new Integer(188744110), new Integer(Task.OUTLINECODE8), "Outline Code8", STRING_ATTRIBUTE_OBJECT},
      {new Integer(188744112), new Integer(Task.OUTLINECODE9), "Outline Code9", STRING_ATTRIBUTE_OBJECT},
      {new Integer(188744114), new Integer(Task.OUTLINECODE10), "Outline Code10", STRING_ATTRIBUTE_OBJECT}
   };

   public static final Object[][] RESOURCE_DATA =
   {
      {new Integer(205520904), new Integer(Resource.TEXT1), "Text1", STRING_ATTRIBUTE_OBJECT},
      {new Integer(205520905), new Integer(Resource.TEXT2), "Text2", STRING_ATTRIBUTE_OBJECT},
      {new Integer(205520926), new Integer(Resource.TEXT3), "Text3", STRING_ATTRIBUTE_OBJECT},
      {new Integer(205520927), new Integer(Resource.TEXT4), "Text4", STRING_ATTRIBUTE_OBJECT},
      {new Integer(205520928), new Integer(Resource.TEXT5), "Text5", STRING_ATTRIBUTE_OBJECT},
      {new Integer(205520993), new Integer(Resource.TEXT6), "Text6", STRING_ATTRIBUTE_OBJECT},
      {new Integer(205520994), new Integer(Resource.TEXT7), "Text7", STRING_ATTRIBUTE_OBJECT},
      {new Integer(205520995), new Integer(Resource.TEXT8), "Text8", STRING_ATTRIBUTE_OBJECT},
      {new Integer(205520996), new Integer(Resource.TEXT9), "Text9", STRING_ATTRIBUTE_OBJECT},
      {new Integer(205520997), new Integer(Resource.TEXT10), "Text10", STRING_ATTRIBUTE_OBJECT},
      {new Integer(205521121), new Integer(Resource.TEXT11), "Text11", STRING_ATTRIBUTE_OBJECT},
      {new Integer(205521122), new Integer(Resource.TEXT12), "Text12", STRING_ATTRIBUTE_OBJECT},
      {new Integer(205521123), new Integer(Resource.TEXT13), "Text13", STRING_ATTRIBUTE_OBJECT},
      {new Integer(205521124), new Integer(Resource.TEXT14), "Text14", STRING_ATTRIBUTE_OBJECT},
      {new Integer(205521125), new Integer(Resource.TEXT15), "Text15", STRING_ATTRIBUTE_OBJECT},
      {new Integer(205521126), new Integer(Resource.TEXT16), "Text16", STRING_ATTRIBUTE_OBJECT},
      {new Integer(205521127), new Integer(Resource.TEXT17), "Text17", STRING_ATTRIBUTE_OBJECT},
      {new Integer(205521128), new Integer(Resource.TEXT18), "Text18", STRING_ATTRIBUTE_OBJECT},
      {new Integer(205521129), new Integer(Resource.TEXT19), "Text19", STRING_ATTRIBUTE_OBJECT},
      {new Integer(205521130), new Integer(Resource.TEXT20), "Text20", STRING_ATTRIBUTE_OBJECT},
      {new Integer(205521131), new Integer(Resource.TEXT21), "Text21", STRING_ATTRIBUTE_OBJECT},
      {new Integer(205521132), new Integer(Resource.TEXT22), "Text22", STRING_ATTRIBUTE_OBJECT},
      {new Integer(205521133), new Integer(Resource.TEXT23), "Text23", STRING_ATTRIBUTE_OBJECT},
      {new Integer(205521134), new Integer(Resource.TEXT24), "Text24", STRING_ATTRIBUTE_OBJECT},
      {new Integer(205521135), new Integer(Resource.TEXT25), "Text25", STRING_ATTRIBUTE_OBJECT},
      {new Integer(205521136), new Integer(Resource.TEXT26), "Text26", STRING_ATTRIBUTE_OBJECT},
      {new Integer(205521137), new Integer(Resource.TEXT27), "Text27", STRING_ATTRIBUTE_OBJECT},
      {new Integer(205521138), new Integer(Resource.TEXT28), "Text28", STRING_ATTRIBUTE_OBJECT},
      {new Integer(205521139), new Integer(Resource.TEXT29), "Text29", STRING_ATTRIBUTE_OBJECT},
      {new Integer(205521140), new Integer(Resource.TEXT30), "Text30", STRING_ATTRIBUTE_OBJECT},
      {new Integer(205520998), new Integer(Resource.START1), "Start1", DATE_ATTRIBUTE_OBJECT},
      {new Integer(205520999), new Integer(Resource.START2), "Start2", DATE_ATTRIBUTE_OBJECT},
      {new Integer(205521000), new Integer(Resource.START3), "Start3", DATE_ATTRIBUTE_OBJECT},
      {new Integer(205521001), new Integer(Resource.START4), "Start4", DATE_ATTRIBUTE_OBJECT},
      {new Integer(205521002), new Integer(Resource.START5), "Start5", DATE_ATTRIBUTE_OBJECT},
      {new Integer(205521116), new Integer(Resource.START6), "Start6", DATE_ATTRIBUTE_OBJECT},
      {new Integer(205521117), new Integer(Resource.START7), "Start7", DATE_ATTRIBUTE_OBJECT},
      {new Integer(205521118), new Integer(Resource.START8), "Start8", DATE_ATTRIBUTE_OBJECT},
      {new Integer(205521119), new Integer(Resource.START9), "Start9", DATE_ATTRIBUTE_OBJECT},
      {new Integer(205521120), new Integer(Resource.START10), "Start10", DATE_ATTRIBUTE_OBJECT},
      {new Integer(205521003), new Integer(Resource.FINISH1), "Finish1", DATE_ATTRIBUTE_OBJECT},
      {new Integer(205521004), new Integer(Resource.FINISH2), "Finish2", DATE_ATTRIBUTE_OBJECT},
      {new Integer(205521005), new Integer(Resource.FINISH3), "Finish3", DATE_ATTRIBUTE_OBJECT},
      {new Integer(205521006), new Integer(Resource.FINISH4), "Finish4", DATE_ATTRIBUTE_OBJECT},
      {new Integer(205521007), new Integer(Resource.FINISH5), "Finish5", DATE_ATTRIBUTE_OBJECT},
      {new Integer(205521086), new Integer(Resource.FINISH6), "Finish6", DATE_ATTRIBUTE_OBJECT},
      {new Integer(205521087), new Integer(Resource.FINISH7), "Finish7", DATE_ATTRIBUTE_OBJECT},
      {new Integer(205521088), new Integer(Resource.FINISH8), "Finish8", DATE_ATTRIBUTE_OBJECT},
      {new Integer(205521089), new Integer(Resource.FINISH9), "Finish9", DATE_ATTRIBUTE_OBJECT},
      {new Integer(205521090), new Integer(Resource.FINISH10), "Finish10", DATE_ATTRIBUTE_OBJECT},
      {new Integer(205521019), new Integer(Resource.COST1), "Cost1", CURRENCY_ATTRIBUTE_OBJECT},
      {new Integer(205521020), new Integer(Resource.COST2), "Cost2", CURRENCY_ATTRIBUTE_OBJECT},
      {new Integer(205521021), new Integer(Resource.COST3), "Cost3", CURRENCY_ATTRIBUTE_OBJECT},
      {new Integer(205521062), new Integer(Resource.COST4), "Cost4", CURRENCY_ATTRIBUTE_OBJECT},
      {new Integer(205521063), new Integer(Resource.COST5), "Cost5", CURRENCY_ATTRIBUTE_OBJECT},
      {new Integer(205521064), new Integer(Resource.COST6), "Cost6", CURRENCY_ATTRIBUTE_OBJECT},
      {new Integer(205521065), new Integer(Resource.COST7), "Cost7", CURRENCY_ATTRIBUTE_OBJECT},
      {new Integer(205521066), new Integer(Resource.COST8), "Cost8", CURRENCY_ATTRIBUTE_OBJECT},
      {new Integer(205521067), new Integer(Resource.COST9), "Cost9", CURRENCY_ATTRIBUTE_OBJECT},
      {new Integer(205521068), new Integer(Resource.COST10), "Cost10", CURRENCY_ATTRIBUTE_OBJECT},
      {new Integer(205521069), new Integer(Resource.DATE1), "Date1", DATE_ATTRIBUTE_OBJECT},
      {new Integer(205521070), new Integer(Resource.DATE2), "Date2", DATE_ATTRIBUTE_OBJECT},
      {new Integer(205521071), new Integer(Resource.DATE3), "Date3", DATE_ATTRIBUTE_OBJECT},
      {new Integer(205521072), new Integer(Resource.DATE4), "Date4", DATE_ATTRIBUTE_OBJECT},
      {new Integer(205521073), new Integer(Resource.DATE5), "Date5", DATE_ATTRIBUTE_OBJECT},
      {new Integer(205521074), new Integer(Resource.DATE6), "Date6", DATE_ATTRIBUTE_OBJECT},
      {new Integer(205521075), new Integer(Resource.DATE7), "Date7", DATE_ATTRIBUTE_OBJECT},
      {new Integer(205521076), new Integer(Resource.DATE8), "Date8", DATE_ATTRIBUTE_OBJECT},
      {new Integer(205521077), new Integer(Resource.DATE9), "Date9", DATE_ATTRIBUTE_OBJECT},
      {new Integer(205521078), new Integer(Resource.DATE10), "Date10", DATE_ATTRIBUTE_OBJECT},
      {new Integer(205521023), new Integer(Resource.FLAG1), "Flag1", BOOLEAN_ATTRIBUTE_OBJECT},
      {new Integer(205521024), new Integer(Resource.FLAG2), "Flag2", BOOLEAN_ATTRIBUTE_OBJECT},
      {new Integer(205521025), new Integer(Resource.FLAG3), "Flag3", BOOLEAN_ATTRIBUTE_OBJECT},
      {new Integer(205521026), new Integer(Resource.FLAG4), "Flag4", BOOLEAN_ATTRIBUTE_OBJECT},
      {new Integer(205521027), new Integer(Resource.FLAG5), "Flag5", BOOLEAN_ATTRIBUTE_OBJECT},
      {new Integer(205521028), new Integer(Resource.FLAG6), "Flag6", BOOLEAN_ATTRIBUTE_OBJECT},
      {new Integer(205521029), new Integer(Resource.FLAG7), "Flag7", BOOLEAN_ATTRIBUTE_OBJECT},
      {new Integer(205521030), new Integer(Resource.FLAG8), "Flag8", BOOLEAN_ATTRIBUTE_OBJECT},
      {new Integer(205521031), new Integer(Resource.FLAG9), "Flag9", BOOLEAN_ATTRIBUTE_OBJECT},
      {new Integer(205521022), new Integer(Resource.FLAG10), "Flag10", BOOLEAN_ATTRIBUTE_OBJECT},
      {new Integer(205521091), new Integer(Resource.FLAG11), "Flag11", BOOLEAN_ATTRIBUTE_OBJECT},
      {new Integer(205521092), new Integer(Resource.FLAG12), "Flag12", BOOLEAN_ATTRIBUTE_OBJECT},
      {new Integer(205521093), new Integer(Resource.FLAG13), "Flag13", BOOLEAN_ATTRIBUTE_OBJECT},
      {new Integer(205521094), new Integer(Resource.FLAG14), "Flag14", BOOLEAN_ATTRIBUTE_OBJECT},
      {new Integer(205521095), new Integer(Resource.FLAG15), "Flag15", BOOLEAN_ATTRIBUTE_OBJECT},
      {new Integer(205521096), new Integer(Resource.FLAG16), "Flag16", BOOLEAN_ATTRIBUTE_OBJECT},
      {new Integer(205521097), new Integer(Resource.FLAG17), "Flag17", BOOLEAN_ATTRIBUTE_OBJECT},
      {new Integer(205521098), new Integer(Resource.FLAG18), "Flag18", BOOLEAN_ATTRIBUTE_OBJECT},
      {new Integer(205521099), new Integer(Resource.FLAG19), "Flag19", BOOLEAN_ATTRIBUTE_OBJECT},
      {new Integer(205521100), new Integer(Resource.FLAG20), "Flag20", BOOLEAN_ATTRIBUTE_OBJECT},
      {new Integer(205521008), new Integer(Resource.NUMBER1), "Number1", NUMERIC_ATTRIBUTE_OBJECT},
      {new Integer(205521009), new Integer(Resource.NUMBER2), "Number2", NUMERIC_ATTRIBUTE_OBJECT},
      {new Integer(205521010), new Integer(Resource.NUMBER3), "Number3", NUMERIC_ATTRIBUTE_OBJECT},
      {new Integer(205521011), new Integer(Resource.NUMBER4), "Number4", NUMERIC_ATTRIBUTE_OBJECT},
      {new Integer(205521012), new Integer(Resource.NUMBER5), "Number5", NUMERIC_ATTRIBUTE_OBJECT},
      {new Integer(205521101), new Integer(Resource.NUMBER6), "Number6", NUMERIC_ATTRIBUTE_OBJECT},
      {new Integer(205521102), new Integer(Resource.NUMBER7), "Number7", NUMERIC_ATTRIBUTE_OBJECT},
      {new Integer(205521103), new Integer(Resource.NUMBER8), "Number8", NUMERIC_ATTRIBUTE_OBJECT},
      {new Integer(205521104), new Integer(Resource.NUMBER9), "Number9", NUMERIC_ATTRIBUTE_OBJECT},
      {new Integer(205521105), new Integer(Resource.NUMBER10), "Number10", NUMERIC_ATTRIBUTE_OBJECT},
      {new Integer(205521106), new Integer(Resource.NUMBER11), "Number11", NUMERIC_ATTRIBUTE_OBJECT},
      {new Integer(205521107), new Integer(Resource.NUMBER12), "Number12", NUMERIC_ATTRIBUTE_OBJECT},
      {new Integer(205521108), new Integer(Resource.NUMBER13), "Number13", NUMERIC_ATTRIBUTE_OBJECT},
      {new Integer(205521109), new Integer(Resource.NUMBER14), "Number14", NUMERIC_ATTRIBUTE_OBJECT},
      {new Integer(205521110), new Integer(Resource.NUMBER15), "Number15", NUMERIC_ATTRIBUTE_OBJECT},
      {new Integer(205521111), new Integer(Resource.NUMBER16), "Number16", NUMERIC_ATTRIBUTE_OBJECT},
      {new Integer(205521112), new Integer(Resource.NUMBER17), "Number17", NUMERIC_ATTRIBUTE_OBJECT},
      {new Integer(205521113), new Integer(Resource.NUMBER18), "Number18", NUMERIC_ATTRIBUTE_OBJECT},
      {new Integer(205521114), new Integer(Resource.NUMBER19), "Number19", NUMERIC_ATTRIBUTE_OBJECT},
      {new Integer(205521115), new Integer(Resource.NUMBER20), "Number20", NUMERIC_ATTRIBUTE_OBJECT},
      {new Integer(205521013), new Integer(Resource.DURATION1), "Duration1", DURATION_ATTRIBUTE_OBJECT},
      {new Integer(205521014), new Integer(Resource.DURATION2), "Duration2", DURATION_ATTRIBUTE_OBJECT},
      {new Integer(205521015), new Integer(Resource.DURATION3), "Duration3", DURATION_ATTRIBUTE_OBJECT},
      {new Integer(205521079), new Integer(Resource.DURATION4), "Duration4", DURATION_ATTRIBUTE_OBJECT},
      {new Integer(205521080), new Integer(Resource.DURATION5), "Duration5", DURATION_ATTRIBUTE_OBJECT},
      {new Integer(205521081), new Integer(Resource.DURATION6), "Duration6", DURATION_ATTRIBUTE_OBJECT},
      {new Integer(205521082), new Integer(Resource.DURATION7), "Duration7", DURATION_ATTRIBUTE_OBJECT},
      {new Integer(205521083), new Integer(Resource.DURATION8), "Duration8", DURATION_ATTRIBUTE_OBJECT},
      {new Integer(205521084), new Integer(Resource.DURATION9), "Duration9", DURATION_ATTRIBUTE_OBJECT},
      {new Integer(205521085), new Integer(Resource.DURATION10), "Duration10", DURATION_ATTRIBUTE_OBJECT},
      {new Integer(205521174), new Integer(Resource.OUTLINECODE1), "Outline Code1", STRING_ATTRIBUTE_OBJECT},
      {new Integer(205521176), new Integer(Resource.OUTLINECODE2), "Outline Code2", STRING_ATTRIBUTE_OBJECT},
      {new Integer(205521178), new Integer(Resource.OUTLINECODE3), "Outline Code3", STRING_ATTRIBUTE_OBJECT},
      {new Integer(205521180), new Integer(Resource.OUTLINECODE4), "Outline Code4", STRING_ATTRIBUTE_OBJECT},
      {new Integer(205521182), new Integer(Resource.OUTLINECODE5), "Outline Code5", STRING_ATTRIBUTE_OBJECT},
      {new Integer(205521184), new Integer(Resource.OUTLINECODE6), "Outline Code6", STRING_ATTRIBUTE_OBJECT},
      {new Integer(205521186), new Integer(Resource.OUTLINECODE7), "Outline Code7", STRING_ATTRIBUTE_OBJECT},
      {new Integer(205521188), new Integer(Resource.OUTLINECODE8), "Outline Code8", STRING_ATTRIBUTE_OBJECT},
      {new Integer(205521190), new Integer(Resource.OUTLINECODE9), "Outline Code9", STRING_ATTRIBUTE_OBJECT},
      {new Integer(205521192), new Integer(Resource.OUTLINECODE10), "Outline Code10", STRING_ATTRIBUTE_OBJECT}
   };

   public static final HashMap TASK_FIELD_MPX_TO_NAME_MAP = new HashMap ();
   public static final HashMap TASK_FIELD_XML_TO_MPX_MAP = new HashMap();
   public static final HashMap TASK_FIELD_MPX_TO_XML_MAP = new HashMap();
   public static final HashMap TASK_FIELD_MPX_TO_TYPE_MAP = new HashMap ();

   public static final HashMap RESOURCE_FIELD_MPX_TO_NAME_MAP = new HashMap ();
   public static final HashMap RESOURCE_FIELD_XML_TO_MPX_MAP = new HashMap();
   public static final HashMap RESOURCE_FIELD_MPX_TO_XML_MAP = new HashMap();
   public static final HashMap RESOURCE_FIELD_MPX_TO_TYPE_MAP = new HashMap ();

   static
   {
      int loop;

      for (loop=0; loop < TASK_DATA.length; loop++)
      {
         TASK_FIELD_MPX_TO_NAME_MAP.put(TASK_DATA[loop][MPX_FIELD_ID], TASK_DATA[loop][MSPDI_FIELD_NAME]);
         TASK_FIELD_XML_TO_MPX_MAP.put(TASK_DATA[loop][MSPDI_FIELD_ID], TASK_DATA[loop][MPX_FIELD_ID]);
         TASK_FIELD_MPX_TO_XML_MAP.put(TASK_DATA[loop][MPX_FIELD_ID], TASK_DATA[loop][MSPDI_FIELD_ID]);
         TASK_FIELD_MPX_TO_TYPE_MAP.put(TASK_DATA[loop][MPX_FIELD_ID], TASK_DATA[loop][FIELD_DATA_TYPE]);
      }

      for (loop=0; loop < RESOURCE_DATA.length; loop++)
      {
         RESOURCE_FIELD_MPX_TO_NAME_MAP.put(RESOURCE_DATA[loop][MPX_FIELD_ID], RESOURCE_DATA[loop][MSPDI_FIELD_NAME]);
         RESOURCE_FIELD_XML_TO_MPX_MAP.put(RESOURCE_DATA[loop][MSPDI_FIELD_ID], RESOURCE_DATA[loop][MPX_FIELD_ID]);
         RESOURCE_FIELD_MPX_TO_XML_MAP.put(RESOURCE_DATA[loop][MPX_FIELD_ID], RESOURCE_DATA[loop][MSPDI_FIELD_ID]);
         RESOURCE_FIELD_MPX_TO_TYPE_MAP.put(RESOURCE_DATA[loop][MPX_FIELD_ID], RESOURCE_DATA[loop][FIELD_DATA_TYPE]);
      }
   }

   private static final BigInteger BIGINTEGER_ZERO = BigInteger.valueOf(0);
}

