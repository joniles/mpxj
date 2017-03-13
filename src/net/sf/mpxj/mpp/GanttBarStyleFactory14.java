/*
 * file:       GanttarStyleFactory14.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2010
 * date:       19/04/2010
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

package net.sf.mpxj.mpp;

import net.sf.mpxj.TaskField;
import net.sf.mpxj.common.MPPTaskField14;

/**
 * Reads Gantt bar styles from an MPP14 file.
 */
public class GanttBarStyleFactory14 implements GanttBarStyleFactory
{
   /**
    * {@inheritDoc}
    */
   @Override public GanttBarStyle[] processDefaultStyles(Props props)
   {
      GanttBarStyle[] barStyles = null;
      byte[] barStyleData = props.getByteArray(DEFAULT_PROPERTIES);
      if (barStyleData != null)
      {
         int barStyleCount = MPPUtility.getByte(barStyleData, 2243);
         if (barStyleCount > 0 && barStyleCount < 65535)
         {
            barStyles = new GanttBarStyle[barStyleCount];
            int styleOffset = 2255;

            for (int loop = 0; loop < barStyleCount; loop++)
            {
               GanttBarStyle style = new GanttBarStyle();
               barStyles[loop] = style;

               style.setName(MPPUtility.getUnicodeString(barStyleData, styleOffset + 91));

               style.setLeftText(getTaskField(MPPUtility.getShort(barStyleData, styleOffset + 67)));
               style.setRightText(getTaskField(MPPUtility.getShort(barStyleData, styleOffset + 71)));
               style.setTopText(getTaskField(MPPUtility.getShort(barStyleData, styleOffset + 75)));
               style.setBottomText(getTaskField(MPPUtility.getShort(barStyleData, styleOffset + 79)));
               style.setInsideText(getTaskField(MPPUtility.getShort(barStyleData, styleOffset + 83)));

               style.setStartShape(GanttBarStartEndShape.getInstance(barStyleData[styleOffset + 15] % 25));
               style.setStartType(GanttBarStartEndType.getInstance(barStyleData[styleOffset + 15] / 25));
               style.setStartColor(MPPUtility.getColor(barStyleData, styleOffset + 16));

               style.setMiddleShape(GanttBarMiddleShape.getInstance(barStyleData[styleOffset]));
               style.setMiddlePattern(ChartPattern.getInstance(barStyleData[styleOffset + 1]));
               style.setMiddleColor(MPPUtility.getColor(barStyleData, styleOffset + 2));

               style.setEndShape(GanttBarStartEndShape.getInstance(barStyleData[styleOffset + 28] % 25));
               style.setEndType(GanttBarStartEndType.getInstance(barStyleData[styleOffset + 28] / 25));
               style.setEndColor(MPPUtility.getColor(barStyleData, styleOffset + 29));

               style.setFromField(getTaskField(MPPUtility.getShort(barStyleData, styleOffset + 41)));
               style.setToField(getTaskField(MPPUtility.getShort(barStyleData, styleOffset + 45)));

               extractFlags(style, GanttBarShowForTasks.NORMAL, MPPUtility.getLong(barStyleData, styleOffset + 49));
               extractFlags(style, GanttBarShowForTasks.NOT_NORMAL, MPPUtility.getLong(barStyleData, styleOffset + 57));

               style.setRow((MPPUtility.getShort(barStyleData, styleOffset + 65) + 1));

               styleOffset += 195;
            }
         }
      }
      return barStyles;
   }

   /**
    * {@inheritDoc}
    */
   @Override public GanttBarStyleException[] processExceptionStyles(Props props)
   {
      GanttBarStyleException[] barStyle = null;
      byte[] barData = props.getByteArray(EXCEPTION_PROPERTIES);
      if (barData != null)
      {
         //System.out.println(MPPUtility.hexdump(barData, false, 71, ""));

         barStyle = new GanttBarStyleException[barData.length / 71];
         int offset = 0;
         for (int loop = 0; loop < barStyle.length; loop++)
         {
            GanttBarStyleException style = new GanttBarStyleException();
            barStyle[loop] = style;

            style.setTaskUniqueID(MPPUtility.getInt(barData, offset));
            style.setBarStyleIndex(MPPUtility.getShort(barData, offset + 4) - 1);

            style.setStartShape(GanttBarStartEndShape.getInstance(barData[offset + 20] % 25));
            style.setStartType(GanttBarStartEndType.getInstance(barData[offset + 20] / 25));
            style.setStartColor(MPPUtility.getColor(barData, offset + 21));

            style.setMiddleShape(GanttBarMiddleShape.getInstance(barData[offset + 6]));
            style.setMiddlePattern(ChartPattern.getInstance(barData[offset + 7]));
            style.setMiddleColor(MPPUtility.getColor(barData, offset + 8));

            style.setEndShape(GanttBarStartEndShape.getInstance(barData[offset + 33] % 25));
            style.setEndType(GanttBarStartEndType.getInstance(barData[offset + 33] / 25));
            style.setEndColor(MPPUtility.getColor(barData, offset + 34));

            style.setLeftText(getTaskField(MPPUtility.getShort(barData, offset + 49)));
            style.setRightText(getTaskField(MPPUtility.getShort(barData, offset + 53)));
            style.setTopText(getTaskField(MPPUtility.getShort(barData, offset + 57)));
            style.setBottomText(getTaskField(MPPUtility.getShort(barData, offset + 61)));
            style.setInsideText(getTaskField(MPPUtility.getShort(barData, offset + 65)));

            //System.out.println(style);
            offset += 71;
         }
      }
      return barStyle;
   }

   /**
    * Extract the flags indicating which task types this bar style
    * is relevant for. Note that this work for the "normal" task types
    * and the "negated" task types (e.g. Normal Task, Not Normal task).
    * The set of values used is determined by the baseCriteria argument.
    *
    * @param style parent bar style
    * @param baseCriteria determines if the normal or negated enums are used
    * @param flagValue flag data
    */
   private void extractFlags(GanttBarStyle style, GanttBarShowForTasks baseCriteria, long flagValue)
   {
      int index = 0;
      long flag = 0x0001;

      while (index < 64)
      {
         if ((flagValue & flag) != 0)
         {
            GanttBarShowForTasks enumValue = GanttBarShowForTasks.getInstance(baseCriteria.getValue() + index);
            if (enumValue != null)
            {
               style.addShowForTasks(enumValue);
            }
         }

         flag = flag << 1;

         index++;
      }
   }

   /**
    * Maps an integer field ID to a field type.
    *
    * @param field field ID
    * @return field type
    */
   private TaskField getTaskField(int field)
   {
      TaskField result = MPPTaskField14.getInstance(field);

      if (result != null)
      {
         switch (result)
         {
            case START_TEXT:
            {
               result = TaskField.START;
               break;
            }

            case FINISH_TEXT:
            {
               result = TaskField.FINISH;
               break;
            }

            case DURATION_TEXT:
            {
               result = TaskField.DURATION;
               break;
            }

            default:
            {
               break;
            }
         }
      }

      return result;
   }

   private static final Integer DEFAULT_PROPERTIES = Integer.valueOf(574619656);
   private static final Integer EXCEPTION_PROPERTIES = Integer.valueOf(574619661);
}
