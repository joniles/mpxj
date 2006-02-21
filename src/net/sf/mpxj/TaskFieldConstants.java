/*
 * file:       TaskFieldConstants.java
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

package net.sf.mpxj;

import java.util.HashMap;



/**
 * Constants relating to task fields.
 */
public final class TaskFieldConstants extends FieldConstants
{
   /**
    * Constructor.
    */
   private TaskFieldConstants ()
   {
      // Private constructor to prevent instantiation.
   }

   public static final int TASK_FIELD_PREFIX = 1887;
   
   public static final Object[][] TASK_DATA =
   {
      {new Integer(188743731), TaskField.TEXT1, "Text1", DataType.STRING},
      {new Integer(188743734), TaskField.TEXT2, "Text2", DataType.STRING},
      {new Integer(188743737), TaskField.TEXT3, "Text3", DataType.STRING},
      {new Integer(188743740), TaskField.TEXT4, "Text4", DataType.STRING},
      {new Integer(188743743), TaskField.TEXT5, "Text5", DataType.STRING},
      {new Integer(188743746), TaskField.TEXT6, "Text6", DataType.STRING},
      {new Integer(188743747), TaskField.TEXT7, "Text7", DataType.STRING},
      {new Integer(188743748), TaskField.TEXT8, "Text8", DataType.STRING},
      {new Integer(188743749), TaskField.TEXT9, "Text9", DataType.STRING},
      {new Integer(188743750), TaskField.TEXT10, "Text10", DataType.STRING},
      {new Integer(188743997), TaskField.TEXT11, "Text11", DataType.STRING},
      {new Integer(188743998), TaskField.TEXT12, "Text12", DataType.STRING},
      {new Integer(188743999), TaskField.TEXT13, "Text13", DataType.STRING},
      {new Integer(188744000), TaskField.TEXT14, "Text14", DataType.STRING},
      {new Integer(188744001), TaskField.TEXT15, "Text15", DataType.STRING},
      {new Integer(188744002), TaskField.TEXT16, "Text16", DataType.STRING},
      {new Integer(188744003), TaskField.TEXT17, "Text17", DataType.STRING},
      {new Integer(188744004), TaskField.TEXT18, "Text18", DataType.STRING},
      {new Integer(188744005), TaskField.TEXT19, "Text19", DataType.STRING},
      {new Integer(188744006), TaskField.TEXT20, "Text20", DataType.STRING},
      {new Integer(188744007), TaskField.TEXT21, "Text21", DataType.STRING},
      {new Integer(188744008), TaskField.TEXT22, "Text22", DataType.STRING},
      {new Integer(188744009), TaskField.TEXT23, "Text23", DataType.STRING},
      {new Integer(188744010), TaskField.TEXT24, "Text24", DataType.STRING},
      {new Integer(188744011), TaskField.TEXT25, "Text25", DataType.STRING},
      {new Integer(188744012), TaskField.TEXT26, "Text26", DataType.STRING},
      {new Integer(188744013), TaskField.TEXT27, "Text27", DataType.STRING},
      {new Integer(188744014), TaskField.TEXT28, "Text28", DataType.STRING},
      {new Integer(188744015), TaskField.TEXT29, "Text29", DataType.STRING},
      {new Integer(188744016), TaskField.TEXT30, "Text30", DataType.STRING},
      {new Integer(188743732), TaskField.START1, "Start1", DataType.DATE},
      {new Integer(188743735), TaskField.START2, "Start2", DataType.DATE},
      {new Integer(188743738), TaskField.START3, "Start3", DataType.DATE},
      {new Integer(188743741), TaskField.START4, "Start4", DataType.DATE},
      {new Integer(188743744), TaskField.START5, "Start5", DataType.DATE},
      {new Integer(188743962), TaskField.START6, "Start6", DataType.DATE},
      {new Integer(188743964), TaskField.START7, "Start7", DataType.DATE},
      {new Integer(188743966), TaskField.START8, "Start8", DataType.DATE},
      {new Integer(188743968), TaskField.START9, "Start9", DataType.DATE},
      {new Integer(188743970), TaskField.START10, "Start10", DataType.DATE},
      {new Integer(188743733), TaskField.FINISH1, "Finish1", DataType.DATE},
      {new Integer(188743736), TaskField.FINISH2, "Finish2", DataType.DATE},
      {new Integer(188743739), TaskField.FINISH3, "Finish3", DataType.DATE},
      {new Integer(188743742), TaskField.FINISH4, "Finish4", DataType.DATE},
      {new Integer(188743745), TaskField.FINISH5, "Finish5", DataType.DATE},
      {new Integer(188743963), TaskField.FINISH6, "Finish6", DataType.DATE},
      {new Integer(188743965), TaskField.FINISH7, "Finish7", DataType.DATE},
      {new Integer(188743967), TaskField.FINISH8, "Finish8", DataType.DATE},
      {new Integer(188743969), TaskField.FINISH9, "Finish9", DataType.DATE},
      {new Integer(188743971), TaskField.FINISH10, "Finish10", DataType.DATE},
      {new Integer(188743786), TaskField.COST1, "Cost1", DataType.CURRENCY},
      {new Integer(188743787), TaskField.COST2, "Cost2", DataType.CURRENCY},
      {new Integer(188743788), TaskField.COST3, "Cost3", DataType.CURRENCY},
      {new Integer(188743938), TaskField.COST4, "Cost4", DataType.CURRENCY},
      {new Integer(188743939), TaskField.COST5, "Cost5", DataType.CURRENCY},
      {new Integer(188743940), TaskField.COST6, "Cost6", DataType.CURRENCY},
      {new Integer(188743941), TaskField.COST7, "Cost7", DataType.CURRENCY},
      {new Integer(188743942), TaskField.COST8, "Cost8", DataType.CURRENCY},
      {new Integer(188743943), TaskField.COST9, "Cost9", DataType.CURRENCY},
      {new Integer(188743944), TaskField.COST10, "Cost10", DataType.CURRENCY},
      {new Integer(188743945), TaskField.DATE1, "Date1", DataType.DATE},
      {new Integer(188743946), TaskField.DATE2, "Date2", DataType.DATE},
      {new Integer(188743947), TaskField.DATE3, "Date3", DataType.DATE},
      {new Integer(188743948), TaskField.DATE4, "Date4", DataType.DATE},
      {new Integer(188743949), TaskField.DATE5, "Date5", DataType.DATE},
      {new Integer(188743950), TaskField.DATE6, "Date6", DataType.DATE},
      {new Integer(188743951), TaskField.DATE7, "Date7", DataType.DATE},
      {new Integer(188743952), TaskField.DATE8, "Date8", DataType.DATE},
      {new Integer(188743953), TaskField.DATE9, "Date9", DataType.DATE},
      {new Integer(188743954), TaskField.DATE10, "Date10", DataType.DATE},
      {new Integer(188743752), TaskField.FLAG1, "Flag1", DataType.BOOLEAN},
      {new Integer(188743753), TaskField.FLAG2, "Flag2", DataType.BOOLEAN},
      {new Integer(188743754), TaskField.FLAG3, "Flag3", DataType.BOOLEAN},
      {new Integer(188743755), TaskField.FLAG4, "Flag4", DataType.BOOLEAN},
      {new Integer(188743756), TaskField.FLAG5, "Flag5", DataType.BOOLEAN},
      {new Integer(188743757), TaskField.FLAG6, "Flag6", DataType.BOOLEAN},
      {new Integer(188743758), TaskField.FLAG7, "Flag7", DataType.BOOLEAN},
      {new Integer(188743759), TaskField.FLAG8, "Flag8", DataType.BOOLEAN},
      {new Integer(188743760), TaskField.FLAG9, "Flag9", DataType.BOOLEAN},
      {new Integer(188743761), TaskField.FLAG10, "Flag10", DataType.BOOLEAN},
      {new Integer(188743972), TaskField.FLAG11, "Flag11", DataType.BOOLEAN},
      {new Integer(188743973), TaskField.FLAG12, "Flag12", DataType.BOOLEAN},
      {new Integer(188743974), TaskField.FLAG13, "Flag13", DataType.BOOLEAN},
      {new Integer(188743975), TaskField.FLAG14, "Flag14", DataType.BOOLEAN},
      {new Integer(188743976), TaskField.FLAG15, "Flag15", DataType.BOOLEAN},
      {new Integer(188743977), TaskField.FLAG16, "Flag16", DataType.BOOLEAN},
      {new Integer(188743978), TaskField.FLAG17, "Flag17", DataType.BOOLEAN},
      {new Integer(188743979), TaskField.FLAG18, "Flag18", DataType.BOOLEAN},
      {new Integer(188743980), TaskField.FLAG19, "Flag19", DataType.BOOLEAN},
      {new Integer(188743981), TaskField.FLAG20, "Flag20", DataType.BOOLEAN},
      {new Integer(188743767), TaskField.NUMBER1, "Number1", DataType.NUMERIC},
      {new Integer(188743768), TaskField.NUMBER2, "Number2", DataType.NUMERIC},
      {new Integer(188743769), TaskField.NUMBER3, "Number3", DataType.NUMERIC},
      {new Integer(188743770), TaskField.NUMBER4, "Number4", DataType.NUMERIC},
      {new Integer(188743771), TaskField.NUMBER5, "Number5", DataType.NUMERIC},
      {new Integer(188743982), TaskField.NUMBER6, "Number6", DataType.NUMERIC},
      {new Integer(188743983), TaskField.NUMBER7, "Number7", DataType.NUMERIC},
      {new Integer(188743984), TaskField.NUMBER8, "Number8", DataType.NUMERIC},
      {new Integer(188743985), TaskField.NUMBER9, "Number9", DataType.NUMERIC},
      {new Integer(188743986), TaskField.NUMBER10, "Number10", DataType.NUMERIC},
      {new Integer(188743987), TaskField.NUMBER11, "Number11", DataType.NUMERIC},
      {new Integer(188743988), TaskField.NUMBER12, "Number12", DataType.NUMERIC},
      {new Integer(188743989), TaskField.NUMBER13, "Number13", DataType.NUMERIC},
      {new Integer(188743990), TaskField.NUMBER14, "Number14", DataType.NUMERIC},
      {new Integer(188743991), TaskField.NUMBER15, "Number15", DataType.NUMERIC},
      {new Integer(188743992), TaskField.NUMBER16, "Number16", DataType.NUMERIC},
      {new Integer(188743993), TaskField.NUMBER17, "Number17", DataType.NUMERIC},
      {new Integer(188743994), TaskField.NUMBER18, "Number18", DataType.NUMERIC},
      {new Integer(188743995), TaskField.NUMBER19, "Number19", DataType.NUMERIC},
      {new Integer(188743996), TaskField.NUMBER20, "Number20", DataType.NUMERIC},
      {new Integer(188743783), TaskField.DURATION1, "Duration1", DataType.DURATION},
      {new Integer(188743784), TaskField.DURATION2, "Duration2", DataType.DURATION},
      {new Integer(188743785), TaskField.DURATION3, "Duration3", DataType.DURATION},
      {new Integer(188743955), TaskField.DURATION4, "Duration4", DataType.DURATION},
      {new Integer(188743956), TaskField.DURATION5, "Duration5", DataType.DURATION},
      {new Integer(188743957), TaskField.DURATION6, "Duration6", DataType.DURATION},
      {new Integer(188743958), TaskField.DURATION7, "Duration7", DataType.DURATION},
      {new Integer(188743959), TaskField.DURATION8, "Duration8", DataType.DURATION},
      {new Integer(188743960), TaskField.DURATION9, "Duration9", DataType.DURATION},
      {new Integer(188743961), TaskField.DURATION10, "Duration10", DataType.DURATION},
      {new Integer(188744096), TaskField.OUTLINE_CODE1, "Outline Code1", DataType.STRING},
      {new Integer(188744098), TaskField.OUTLINE_CODE2, "Outline Code2", DataType.STRING},
      {new Integer(188744100), TaskField.OUTLINE_CODE3, "Outline Code3", DataType.STRING},
      {new Integer(188744102), TaskField.OUTLINE_CODE4, "Outline Code4", DataType.STRING},
      {new Integer(188744104), TaskField.OUTLINE_CODE5, "Outline Code5", DataType.STRING},
      {new Integer(188744106), TaskField.OUTLINE_CODE6, "Outline Code6", DataType.STRING},
      {new Integer(188744108), TaskField.OUTLINE_CODE7, "Outline Code7", DataType.STRING},
      {new Integer(188744110), TaskField.OUTLINE_CODE8, "Outline Code8", DataType.STRING},
      {new Integer(188744112), TaskField.OUTLINE_CODE9, "Outline Code9", DataType.STRING},
      {new Integer(188744114), TaskField.OUTLINE_CODE10, "Outline Code10", DataType.STRING}
   };

   public static final HashMap TASK_FIELD_MPXJ_TO_NAME_MAP = new HashMap ();
   public static final HashMap TASK_FIELD_PROJECT_TO_MPXJ_MAP = new HashMap();
   public static final HashMap TASK_FIELD_MPXJ_TO_PROJECT_MAP = new HashMap();
   public static final HashMap TASK_FIELD_MPXJ_TO_TYPE_MAP = new HashMap ();

   static
   {
      int loop;

      for (loop=0; loop < TASK_DATA.length; loop++)
      {
         TASK_FIELD_MPXJ_TO_NAME_MAP.put(TASK_DATA[loop][MPXJ_FIELD_ID], TASK_DATA[loop][PROJECT_FIELD_NAME]);
         TASK_FIELD_PROJECT_TO_MPXJ_MAP.put(TASK_DATA[loop][PROJECT_FIELD_ID], TASK_DATA[loop][MPXJ_FIELD_ID]);
         TASK_FIELD_MPXJ_TO_PROJECT_MAP.put(TASK_DATA[loop][MPXJ_FIELD_ID], TASK_DATA[loop][PROJECT_FIELD_ID]);
         TASK_FIELD_MPXJ_TO_TYPE_MAP.put(TASK_DATA[loop][MPXJ_FIELD_ID], TASK_DATA[loop][FIELD_DATA_TYPE]);
      }
   }
}

