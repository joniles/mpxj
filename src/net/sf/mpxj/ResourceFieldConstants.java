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
      {new Integer(205520904), new Integer(Resource.TEXT1), "Text1", DataType.STRING},
      {new Integer(205520905), new Integer(Resource.TEXT2), "Text2", DataType.STRING},
      {new Integer(205520926), new Integer(Resource.TEXT3), "Text3", DataType.STRING},
      {new Integer(205520927), new Integer(Resource.TEXT4), "Text4", DataType.STRING},
      {new Integer(205520928), new Integer(Resource.TEXT5), "Text5", DataType.STRING},
      {new Integer(205520993), new Integer(Resource.TEXT6), "Text6", DataType.STRING},
      {new Integer(205520994), new Integer(Resource.TEXT7), "Text7", DataType.STRING},
      {new Integer(205520995), new Integer(Resource.TEXT8), "Text8", DataType.STRING},
      {new Integer(205520996), new Integer(Resource.TEXT9), "Text9", DataType.STRING},
      {new Integer(205520997), new Integer(Resource.TEXT10), "Text10", DataType.STRING},
      {new Integer(205521121), new Integer(Resource.TEXT11), "Text11", DataType.STRING},
      {new Integer(205521122), new Integer(Resource.TEXT12), "Text12", DataType.STRING},
      {new Integer(205521123), new Integer(Resource.TEXT13), "Text13", DataType.STRING},
      {new Integer(205521124), new Integer(Resource.TEXT14), "Text14", DataType.STRING},
      {new Integer(205521125), new Integer(Resource.TEXT15), "Text15", DataType.STRING},
      {new Integer(205521126), new Integer(Resource.TEXT16), "Text16", DataType.STRING},
      {new Integer(205521127), new Integer(Resource.TEXT17), "Text17", DataType.STRING},
      {new Integer(205521128), new Integer(Resource.TEXT18), "Text18", DataType.STRING},
      {new Integer(205521129), new Integer(Resource.TEXT19), "Text19", DataType.STRING},
      {new Integer(205521130), new Integer(Resource.TEXT20), "Text20", DataType.STRING},
      {new Integer(205521131), new Integer(Resource.TEXT21), "Text21", DataType.STRING},
      {new Integer(205521132), new Integer(Resource.TEXT22), "Text22", DataType.STRING},
      {new Integer(205521133), new Integer(Resource.TEXT23), "Text23", DataType.STRING},
      {new Integer(205521134), new Integer(Resource.TEXT24), "Text24", DataType.STRING},
      {new Integer(205521135), new Integer(Resource.TEXT25), "Text25", DataType.STRING},
      {new Integer(205521136), new Integer(Resource.TEXT26), "Text26", DataType.STRING},
      {new Integer(205521137), new Integer(Resource.TEXT27), "Text27", DataType.STRING},
      {new Integer(205521138), new Integer(Resource.TEXT28), "Text28", DataType.STRING},
      {new Integer(205521139), new Integer(Resource.TEXT29), "Text29", DataType.STRING},
      {new Integer(205521140), new Integer(Resource.TEXT30), "Text30", DataType.STRING},
      {new Integer(205520998), new Integer(Resource.START1), "Start1", DataType.DATE},
      {new Integer(205520999), new Integer(Resource.START2), "Start2", DataType.DATE},
      {new Integer(205521000), new Integer(Resource.START3), "Start3", DataType.DATE},
      {new Integer(205521001), new Integer(Resource.START4), "Start4", DataType.DATE},
      {new Integer(205521002), new Integer(Resource.START5), "Start5", DataType.DATE},
      {new Integer(205521116), new Integer(Resource.START6), "Start6", DataType.DATE},
      {new Integer(205521117), new Integer(Resource.START7), "Start7", DataType.DATE},
      {new Integer(205521118), new Integer(Resource.START8), "Start8", DataType.DATE},
      {new Integer(205521119), new Integer(Resource.START9), "Start9", DataType.DATE},
      {new Integer(205521120), new Integer(Resource.START10), "Start10", DataType.DATE},
      {new Integer(205521003), new Integer(Resource.FINISH1), "Finish1", DataType.DATE},
      {new Integer(205521004), new Integer(Resource.FINISH2), "Finish2", DataType.DATE},
      {new Integer(205521005), new Integer(Resource.FINISH3), "Finish3", DataType.DATE},
      {new Integer(205521006), new Integer(Resource.FINISH4), "Finish4", DataType.DATE},
      {new Integer(205521007), new Integer(Resource.FINISH5), "Finish5", DataType.DATE},
      {new Integer(205521086), new Integer(Resource.FINISH6), "Finish6", DataType.DATE},
      {new Integer(205521087), new Integer(Resource.FINISH7), "Finish7", DataType.DATE},
      {new Integer(205521088), new Integer(Resource.FINISH8), "Finish8", DataType.DATE},
      {new Integer(205521089), new Integer(Resource.FINISH9), "Finish9", DataType.DATE},
      {new Integer(205521090), new Integer(Resource.FINISH10), "Finish10", DataType.DATE},
      {new Integer(205521019), new Integer(Resource.COST1), "Cost1", DataType.CURRENCY},
      {new Integer(205521020), new Integer(Resource.COST2), "Cost2", DataType.CURRENCY},
      {new Integer(205521021), new Integer(Resource.COST3), "Cost3", DataType.CURRENCY},
      {new Integer(205521062), new Integer(Resource.COST4), "Cost4", DataType.CURRENCY},
      {new Integer(205521063), new Integer(Resource.COST5), "Cost5", DataType.CURRENCY},
      {new Integer(205521064), new Integer(Resource.COST6), "Cost6", DataType.CURRENCY},
      {new Integer(205521065), new Integer(Resource.COST7), "Cost7", DataType.CURRENCY},
      {new Integer(205521066), new Integer(Resource.COST8), "Cost8", DataType.CURRENCY},
      {new Integer(205521067), new Integer(Resource.COST9), "Cost9", DataType.CURRENCY},
      {new Integer(205521068), new Integer(Resource.COST10), "Cost10", DataType.CURRENCY},
      {new Integer(205521069), new Integer(Resource.DATE1), "Date1", DataType.DATE},
      {new Integer(205521070), new Integer(Resource.DATE2), "Date2", DataType.DATE},
      {new Integer(205521071), new Integer(Resource.DATE3), "Date3", DataType.DATE},
      {new Integer(205521072), new Integer(Resource.DATE4), "Date4", DataType.DATE},
      {new Integer(205521073), new Integer(Resource.DATE5), "Date5", DataType.DATE},
      {new Integer(205521074), new Integer(Resource.DATE6), "Date6", DataType.DATE},
      {new Integer(205521075), new Integer(Resource.DATE7), "Date7", DataType.DATE},
      {new Integer(205521076), new Integer(Resource.DATE8), "Date8", DataType.DATE},
      {new Integer(205521077), new Integer(Resource.DATE9), "Date9", DataType.DATE},
      {new Integer(205521078), new Integer(Resource.DATE10), "Date10", DataType.DATE},
      {new Integer(205521023), new Integer(Resource.FLAG1), "Flag1", DataType.BOOLEAN},
      {new Integer(205521024), new Integer(Resource.FLAG2), "Flag2", DataType.BOOLEAN},
      {new Integer(205521025), new Integer(Resource.FLAG3), "Flag3", DataType.BOOLEAN},
      {new Integer(205521026), new Integer(Resource.FLAG4), "Flag4", DataType.BOOLEAN},
      {new Integer(205521027), new Integer(Resource.FLAG5), "Flag5", DataType.BOOLEAN},
      {new Integer(205521028), new Integer(Resource.FLAG6), "Flag6", DataType.BOOLEAN},
      {new Integer(205521029), new Integer(Resource.FLAG7), "Flag7", DataType.BOOLEAN},
      {new Integer(205521030), new Integer(Resource.FLAG8), "Flag8", DataType.BOOLEAN},
      {new Integer(205521031), new Integer(Resource.FLAG9), "Flag9", DataType.BOOLEAN},
      {new Integer(205521022), new Integer(Resource.FLAG10), "Flag10", DataType.BOOLEAN},
      {new Integer(205521091), new Integer(Resource.FLAG11), "Flag11", DataType.BOOLEAN},
      {new Integer(205521092), new Integer(Resource.FLAG12), "Flag12", DataType.BOOLEAN},
      {new Integer(205521093), new Integer(Resource.FLAG13), "Flag13", DataType.BOOLEAN},
      {new Integer(205521094), new Integer(Resource.FLAG14), "Flag14", DataType.BOOLEAN},
      {new Integer(205521095), new Integer(Resource.FLAG15), "Flag15", DataType.BOOLEAN},
      {new Integer(205521096), new Integer(Resource.FLAG16), "Flag16", DataType.BOOLEAN},
      {new Integer(205521097), new Integer(Resource.FLAG17), "Flag17", DataType.BOOLEAN},
      {new Integer(205521098), new Integer(Resource.FLAG18), "Flag18", DataType.BOOLEAN},
      {new Integer(205521099), new Integer(Resource.FLAG19), "Flag19", DataType.BOOLEAN},
      {new Integer(205521100), new Integer(Resource.FLAG20), "Flag20", DataType.BOOLEAN},
      {new Integer(205521008), new Integer(Resource.NUMBER1), "Number1", DataType.NUMERIC},
      {new Integer(205521009), new Integer(Resource.NUMBER2), "Number2", DataType.NUMERIC},
      {new Integer(205521010), new Integer(Resource.NUMBER3), "Number3", DataType.NUMERIC},
      {new Integer(205521011), new Integer(Resource.NUMBER4), "Number4", DataType.NUMERIC},
      {new Integer(205521012), new Integer(Resource.NUMBER5), "Number5", DataType.NUMERIC},
      {new Integer(205521101), new Integer(Resource.NUMBER6), "Number6", DataType.NUMERIC},
      {new Integer(205521102), new Integer(Resource.NUMBER7), "Number7", DataType.NUMERIC},
      {new Integer(205521103), new Integer(Resource.NUMBER8), "Number8", DataType.NUMERIC},
      {new Integer(205521104), new Integer(Resource.NUMBER9), "Number9", DataType.NUMERIC},
      {new Integer(205521105), new Integer(Resource.NUMBER10), "Number10", DataType.NUMERIC},
      {new Integer(205521106), new Integer(Resource.NUMBER11), "Number11", DataType.NUMERIC},
      {new Integer(205521107), new Integer(Resource.NUMBER12), "Number12", DataType.NUMERIC},
      {new Integer(205521108), new Integer(Resource.NUMBER13), "Number13", DataType.NUMERIC},
      {new Integer(205521109), new Integer(Resource.NUMBER14), "Number14", DataType.NUMERIC},
      {new Integer(205521110), new Integer(Resource.NUMBER15), "Number15", DataType.NUMERIC},
      {new Integer(205521111), new Integer(Resource.NUMBER16), "Number16", DataType.NUMERIC},
      {new Integer(205521112), new Integer(Resource.NUMBER17), "Number17", DataType.NUMERIC},
      {new Integer(205521113), new Integer(Resource.NUMBER18), "Number18", DataType.NUMERIC},
      {new Integer(205521114), new Integer(Resource.NUMBER19), "Number19", DataType.NUMERIC},
      {new Integer(205521115), new Integer(Resource.NUMBER20), "Number20", DataType.NUMERIC},
      {new Integer(205521013), new Integer(Resource.DURATION1), "Duration1", DataType.DURATION},
      {new Integer(205521014), new Integer(Resource.DURATION2), "Duration2", DataType.DURATION},
      {new Integer(205521015), new Integer(Resource.DURATION3), "Duration3", DataType.DURATION},
      {new Integer(205521079), new Integer(Resource.DURATION4), "Duration4", DataType.DURATION},
      {new Integer(205521080), new Integer(Resource.DURATION5), "Duration5", DataType.DURATION},
      {new Integer(205521081), new Integer(Resource.DURATION6), "Duration6", DataType.DURATION},
      {new Integer(205521082), new Integer(Resource.DURATION7), "Duration7", DataType.DURATION},
      {new Integer(205521083), new Integer(Resource.DURATION8), "Duration8", DataType.DURATION},
      {new Integer(205521084), new Integer(Resource.DURATION9), "Duration9", DataType.DURATION},
      {new Integer(205521085), new Integer(Resource.DURATION10), "Duration10", DataType.DURATION},
      {new Integer(205521174), new Integer(Resource.OUTLINECODE1), "Outline Code1", DataType.STRING},
      {new Integer(205521176), new Integer(Resource.OUTLINECODE2), "Outline Code2", DataType.STRING},
      {new Integer(205521178), new Integer(Resource.OUTLINECODE3), "Outline Code3", DataType.STRING},
      {new Integer(205521180), new Integer(Resource.OUTLINECODE4), "Outline Code4", DataType.STRING},
      {new Integer(205521182), new Integer(Resource.OUTLINECODE5), "Outline Code5", DataType.STRING},
      {new Integer(205521184), new Integer(Resource.OUTLINECODE6), "Outline Code6", DataType.STRING},
      {new Integer(205521186), new Integer(Resource.OUTLINECODE7), "Outline Code7", DataType.STRING},
      {new Integer(205521188), new Integer(Resource.OUTLINECODE8), "Outline Code8", DataType.STRING},
      {new Integer(205521190), new Integer(Resource.OUTLINECODE9), "Outline Code9", DataType.STRING},
      {new Integer(205521192), new Integer(Resource.OUTLINECODE10), "Outline Code10", DataType.STRING}
   };

   public static final HashMap RESOURCE_FIELD_MPXJ_TO_NAME_MAP = new HashMap ();
   public static final HashMap RESOURCE_FIELD_PROJECT_TO_MPXJ_MAP = new HashMap();
   public static final HashMap RESOURCE_FIELD_MPXJ_TO_PROJECT_MAP = new HashMap();
   public static final HashMap RESOURCE_FIELD_MPXJ_TO_TYPE_MAP = new HashMap ();

   static
   {
      for (int loop=0; loop < RESOURCE_DATA.length; loop++)
      {
         RESOURCE_FIELD_MPXJ_TO_NAME_MAP.put(RESOURCE_DATA[loop][MPXJ_FIELD_ID], RESOURCE_DATA[loop][PROJECT_FIELD_NAME]);
         RESOURCE_FIELD_PROJECT_TO_MPXJ_MAP.put(RESOURCE_DATA[loop][PROJECT_FIELD_ID], RESOURCE_DATA[loop][MPXJ_FIELD_ID]);
         RESOURCE_FIELD_MPXJ_TO_PROJECT_MAP.put(RESOURCE_DATA[loop][MPXJ_FIELD_ID], RESOURCE_DATA[loop][PROJECT_FIELD_ID]);
         RESOURCE_FIELD_MPXJ_TO_TYPE_MAP.put(RESOURCE_DATA[loop][MPXJ_FIELD_ID], RESOURCE_DATA[loop][FIELD_DATA_TYPE]);
      }
   }

   public static final Integer NULL_RESOURCE_ID = new Integer (-65535);   
}
