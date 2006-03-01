/*
 * file:       ResourceConstants.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2005
 * date:       13-Feb-2006
 */
 
package net.sf.mpxj;

import java.util.HashMap;


/**
 * Constants relating to resource fields.
 */
public final class ResourceFieldConstants extends FieldConstants
{   
   /**
    * Constructor.
    */
   private ResourceFieldConstants ()
   {
      // private constructor to prevent instantiation
   }
   
   public static final int RESOURCE_FIELD_PREFIX = 2055;
   
   public static final Object[][] RESOURCE_DATA =
   {
      {new Integer(205520904), ResourceField.TEXT1, "Text1", DataType.STRING},
      {new Integer(205520905), ResourceField.TEXT2, "Text2", DataType.STRING},
      {new Integer(205520926), ResourceField.TEXT3, "Text3", DataType.STRING},
      {new Integer(205520927), ResourceField.TEXT4, "Text4", DataType.STRING},
      {new Integer(205520928), ResourceField.TEXT5, "Text5", DataType.STRING},
      {new Integer(205520993), ResourceField.TEXT6, "Text6", DataType.STRING},
      {new Integer(205520994), ResourceField.TEXT7, "Text7", DataType.STRING},
      {new Integer(205520995), ResourceField.TEXT8, "Text8", DataType.STRING},
      {new Integer(205520996), ResourceField.TEXT9, "Text9", DataType.STRING},
      {new Integer(205520997), ResourceField.TEXT10, "Text10", DataType.STRING},
      {new Integer(205521121), ResourceField.TEXT11, "Text11", DataType.STRING},
      {new Integer(205521122), ResourceField.TEXT12, "Text12", DataType.STRING},
      {new Integer(205521123), ResourceField.TEXT13, "Text13", DataType.STRING},
      {new Integer(205521124), ResourceField.TEXT14, "Text14", DataType.STRING},
      {new Integer(205521125), ResourceField.TEXT15, "Text15", DataType.STRING},
      {new Integer(205521126), ResourceField.TEXT16, "Text16", DataType.STRING},
      {new Integer(205521127), ResourceField.TEXT17, "Text17", DataType.STRING},
      {new Integer(205521128), ResourceField.TEXT18, "Text18", DataType.STRING},
      {new Integer(205521129), ResourceField.TEXT19, "Text19", DataType.STRING},
      {new Integer(205521130), ResourceField.TEXT20, "Text20", DataType.STRING},
      {new Integer(205521131), ResourceField.TEXT21, "Text21", DataType.STRING},
      {new Integer(205521132), ResourceField.TEXT22, "Text22", DataType.STRING},
      {new Integer(205521133), ResourceField.TEXT23, "Text23", DataType.STRING},
      {new Integer(205521134), ResourceField.TEXT24, "Text24", DataType.STRING},
      {new Integer(205521135), ResourceField.TEXT25, "Text25", DataType.STRING},
      {new Integer(205521136), ResourceField.TEXT26, "Text26", DataType.STRING},
      {new Integer(205521137), ResourceField.TEXT27, "Text27", DataType.STRING},
      {new Integer(205521138), ResourceField.TEXT28, "Text28", DataType.STRING},
      {new Integer(205521139), ResourceField.TEXT29, "Text29", DataType.STRING},
      {new Integer(205521140), ResourceField.TEXT30, "Text30", DataType.STRING},
      {new Integer(205520998), ResourceField.START1, "Start1", DataType.DATE},
      {new Integer(205520999), ResourceField.START2, "Start2", DataType.DATE},
      {new Integer(205521000), ResourceField.START3, "Start3", DataType.DATE},
      {new Integer(205521001), ResourceField.START4, "Start4", DataType.DATE},
      {new Integer(205521002), ResourceField.START5, "Start5", DataType.DATE},
      {new Integer(205521116), ResourceField.START6, "Start6", DataType.DATE},
      {new Integer(205521117), ResourceField.START7, "Start7", DataType.DATE},
      {new Integer(205521118), ResourceField.START8, "Start8", DataType.DATE},
      {new Integer(205521119), ResourceField.START9, "Start9", DataType.DATE},
      {new Integer(205521120), ResourceField.START10, "Start10", DataType.DATE},
      {new Integer(205521003), ResourceField.FINISH1, "Finish1", DataType.DATE},
      {new Integer(205521004), ResourceField.FINISH2, "Finish2", DataType.DATE},
      {new Integer(205521005), ResourceField.FINISH3, "Finish3", DataType.DATE},
      {new Integer(205521006), ResourceField.FINISH4, "Finish4", DataType.DATE},
      {new Integer(205521007), ResourceField.FINISH5, "Finish5", DataType.DATE},
      {new Integer(205521086), ResourceField.FINISH6, "Finish6", DataType.DATE},
      {new Integer(205521087), ResourceField.FINISH7, "Finish7", DataType.DATE},
      {new Integer(205521088), ResourceField.FINISH8, "Finish8", DataType.DATE},
      {new Integer(205521089), ResourceField.FINISH9, "Finish9", DataType.DATE},
      {new Integer(205521090), ResourceField.FINISH10, "Finish10", DataType.DATE},
      {new Integer(205521019), ResourceField.COST1, "Cost1", DataType.CURRENCY},
      {new Integer(205521020), ResourceField.COST2, "Cost2", DataType.CURRENCY},
      {new Integer(205521021), ResourceField.COST3, "Cost3", DataType.CURRENCY},
      {new Integer(205521062), ResourceField.COST4, "Cost4", DataType.CURRENCY},
      {new Integer(205521063), ResourceField.COST5, "Cost5", DataType.CURRENCY},
      {new Integer(205521064), ResourceField.COST6, "Cost6", DataType.CURRENCY},
      {new Integer(205521065), ResourceField.COST7, "Cost7", DataType.CURRENCY},
      {new Integer(205521066), ResourceField.COST8, "Cost8", DataType.CURRENCY},
      {new Integer(205521067), ResourceField.COST9, "Cost9", DataType.CURRENCY},
      {new Integer(205521068), ResourceField.COST10, "Cost10", DataType.CURRENCY},
      {new Integer(205521069), ResourceField.DATE1, "Date1", DataType.DATE},
      {new Integer(205521070), ResourceField.DATE2, "Date2", DataType.DATE},
      {new Integer(205521071), ResourceField.DATE3, "Date3", DataType.DATE},
      {new Integer(205521072), ResourceField.DATE4, "Date4", DataType.DATE},
      {new Integer(205521073), ResourceField.DATE5, "Date5", DataType.DATE},
      {new Integer(205521074), ResourceField.DATE6, "Date6", DataType.DATE},
      {new Integer(205521075), ResourceField.DATE7, "Date7", DataType.DATE},
      {new Integer(205521076), ResourceField.DATE8, "Date8", DataType.DATE},
      {new Integer(205521077), ResourceField.DATE9, "Date9", DataType.DATE},
      {new Integer(205521078), ResourceField.DATE10, "Date10", DataType.DATE},
      {new Integer(205521023), ResourceField.FLAG1, "Flag1", DataType.BOOLEAN},
      {new Integer(205521024), ResourceField.FLAG2, "Flag2", DataType.BOOLEAN},
      {new Integer(205521025), ResourceField.FLAG3, "Flag3", DataType.BOOLEAN},
      {new Integer(205521026), ResourceField.FLAG4, "Flag4", DataType.BOOLEAN},
      {new Integer(205521027), ResourceField.FLAG5, "Flag5", DataType.BOOLEAN},
      {new Integer(205521028), ResourceField.FLAG6, "Flag6", DataType.BOOLEAN},
      {new Integer(205521029), ResourceField.FLAG7, "Flag7", DataType.BOOLEAN},
      {new Integer(205521030), ResourceField.FLAG8, "Flag8", DataType.BOOLEAN},
      {new Integer(205521031), ResourceField.FLAG9, "Flag9", DataType.BOOLEAN},
      {new Integer(205521022), ResourceField.FLAG10, "Flag10", DataType.BOOLEAN},
      {new Integer(205521091), ResourceField.FLAG11, "Flag11", DataType.BOOLEAN},
      {new Integer(205521092), ResourceField.FLAG12, "Flag12", DataType.BOOLEAN},
      {new Integer(205521093), ResourceField.FLAG13, "Flag13", DataType.BOOLEAN},
      {new Integer(205521094), ResourceField.FLAG14, "Flag14", DataType.BOOLEAN},
      {new Integer(205521095), ResourceField.FLAG15, "Flag15", DataType.BOOLEAN},
      {new Integer(205521096), ResourceField.FLAG16, "Flag16", DataType.BOOLEAN},
      {new Integer(205521097), ResourceField.FLAG17, "Flag17", DataType.BOOLEAN},
      {new Integer(205521098), ResourceField.FLAG18, "Flag18", DataType.BOOLEAN},
      {new Integer(205521099), ResourceField.FLAG19, "Flag19", DataType.BOOLEAN},
      {new Integer(205521100), ResourceField.FLAG20, "Flag20", DataType.BOOLEAN},
      {new Integer(205521008), ResourceField.NUMBER1, "Number1", DataType.NUMERIC},
      {new Integer(205521009), ResourceField.NUMBER2, "Number2", DataType.NUMERIC},
      {new Integer(205521010), ResourceField.NUMBER3, "Number3", DataType.NUMERIC},
      {new Integer(205521011), ResourceField.NUMBER4, "Number4", DataType.NUMERIC},
      {new Integer(205521012), ResourceField.NUMBER5, "Number5", DataType.NUMERIC},
      {new Integer(205521101), ResourceField.NUMBER6, "Number6", DataType.NUMERIC},
      {new Integer(205521102), ResourceField.NUMBER7, "Number7", DataType.NUMERIC},
      {new Integer(205521103), ResourceField.NUMBER8, "Number8", DataType.NUMERIC},
      {new Integer(205521104), ResourceField.NUMBER9, "Number9", DataType.NUMERIC},
      {new Integer(205521105), ResourceField.NUMBER10, "Number10", DataType.NUMERIC},
      {new Integer(205521106), ResourceField.NUMBER11, "Number11", DataType.NUMERIC},
      {new Integer(205521107), ResourceField.NUMBER12, "Number12", DataType.NUMERIC},
      {new Integer(205521108), ResourceField.NUMBER13, "Number13", DataType.NUMERIC},
      {new Integer(205521109), ResourceField.NUMBER14, "Number14", DataType.NUMERIC},
      {new Integer(205521110), ResourceField.NUMBER15, "Number15", DataType.NUMERIC},
      {new Integer(205521111), ResourceField.NUMBER16, "Number16", DataType.NUMERIC},
      {new Integer(205521112), ResourceField.NUMBER17, "Number17", DataType.NUMERIC},
      {new Integer(205521113), ResourceField.NUMBER18, "Number18", DataType.NUMERIC},
      {new Integer(205521114), ResourceField.NUMBER19, "Number19", DataType.NUMERIC},
      {new Integer(205521115), ResourceField.NUMBER20, "Number20", DataType.NUMERIC},
      {new Integer(205521013), ResourceField.DURATION1, "Duration1", DataType.DURATION},
      {new Integer(205521014), ResourceField.DURATION2, "Duration2", DataType.DURATION},
      {new Integer(205521015), ResourceField.DURATION3, "Duration3", DataType.DURATION},
      {new Integer(205521079), ResourceField.DURATION4, "Duration4", DataType.DURATION},
      {new Integer(205521080), ResourceField.DURATION5, "Duration5", DataType.DURATION},
      {new Integer(205521081), ResourceField.DURATION6, "Duration6", DataType.DURATION},
      {new Integer(205521082), ResourceField.DURATION7, "Duration7", DataType.DURATION},
      {new Integer(205521083), ResourceField.DURATION8, "Duration8", DataType.DURATION},
      {new Integer(205521084), ResourceField.DURATION9, "Duration9", DataType.DURATION},
      {new Integer(205521085), ResourceField.DURATION10, "Duration10", DataType.DURATION},
      {new Integer(205521174), ResourceField.OUTLINE_CODE1, "Outline Code1", DataType.STRING},
      {new Integer(205521176), ResourceField.OUTLINE_CODE2, "Outline Code2", DataType.STRING},
      {new Integer(205521178), ResourceField.OUTLINE_CODE3, "Outline Code3", DataType.STRING},
      {new Integer(205521180), ResourceField.OUTLINE_CODE4, "Outline Code4", DataType.STRING},
      {new Integer(205521182), ResourceField.OUTLINE_CODE5, "Outline Code5", DataType.STRING},
      {new Integer(205521184), ResourceField.OUTLINE_CODE6, "Outline Code6", DataType.STRING},
      {new Integer(205521186), ResourceField.OUTLINE_CODE7, "Outline Code7", DataType.STRING},
      {new Integer(205521188), ResourceField.OUTLINE_CODE8, "Outline Code8", DataType.STRING},
      {new Integer(205521190), ResourceField.OUTLINE_CODE9, "Outline Code9", DataType.STRING},
      {new Integer(205521192), ResourceField.OUTLINE_CODE10, "Outline Code10", DataType.STRING}
   };

   public static final HashMap RESOURCE_FIELD_MPXJ_TO_NAME_MAP = new HashMap ();
   public static final HashMap RESOURCE_FIELD_PROJECT_TO_MPXJ_MAP = new HashMap();
   public static final HashMap RESOURCE_FIELD_MPXJ_TO_PROJECT_MAP = new HashMap();

   static
   {
      for (int loop=0; loop < RESOURCE_DATA.length; loop++)
      {
         RESOURCE_FIELD_MPXJ_TO_NAME_MAP.put(RESOURCE_DATA[loop][MPXJ_FIELD_ID], RESOURCE_DATA[loop][PROJECT_FIELD_NAME]);
         RESOURCE_FIELD_PROJECT_TO_MPXJ_MAP.put(RESOURCE_DATA[loop][PROJECT_FIELD_ID], RESOURCE_DATA[loop][MPXJ_FIELD_ID]);
         RESOURCE_FIELD_MPXJ_TO_PROJECT_MAP.put(RESOURCE_DATA[loop][MPXJ_FIELD_ID], RESOURCE_DATA[loop][PROJECT_FIELD_ID]);
      }
   }

   public static final Integer NULL_RESOURCE_ID = new Integer (-65535);   
}
