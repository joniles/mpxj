/*
 * file:       GantBarStyleFactory14.java
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

package org.mpxj.mpp;

import org.mpxj.FieldType;
import org.mpxj.ProjectFile;
import org.mpxj.TaskField;
import org.mpxj.common.ByteArrayHelper;
import org.mpxj.common.FieldTypeHelper;

/**
 * Reads Gantt bar styles from an MPP14 file.
 */
public class GanttBarStyleFactory14 implements GanttBarStyleFactory
{
   @Override public GanttBarStyle[] processDefaultStyles(ProjectFile file, Props props)
   {
      byte[] barStyleData = props.getByteArray(DEFAULT_PROPERTIES);
      if (barStyleData == null || barStyleData.length <= 2240)
      {
         return EMPTY_STYLES;
      }

      int barStyleCount = MPPUtility.getByte(barStyleData, 2243);
      if (barStyleCount <= 0)
      {
         return EMPTY_STYLES;
      }

      GanttBarStyle[] barStyles = new GanttBarStyle[barStyleCount];
      int styleOffset = 2255;

      for (int loop = 0; loop < barStyleCount; loop++)
      {
         GanttBarStyle style = new GanttBarStyle();
         barStyles[loop] = style;

         style.setID(Integer.valueOf(ByteArrayHelper.getShort(barStyleData, styleOffset + 89)));
         style.setName(MPPUtility.getUnicodeString(barStyleData, styleOffset + 91));

         style.setLeftText(getTaskField(file, ByteArrayHelper.getInt(barStyleData, styleOffset + 67)));
         style.setRightText(getTaskField(file, ByteArrayHelper.getInt(barStyleData, styleOffset + 71)));
         style.setTopText(getTaskField(file, ByteArrayHelper.getInt(barStyleData, styleOffset + 75)));
         style.setBottomText(getTaskField(file, ByteArrayHelper.getInt(barStyleData, styleOffset + 79)));
         style.setInsideText(getTaskField(file, ByteArrayHelper.getInt(barStyleData, styleOffset + 83)));

         style.setStartShape(GanttBarStartEndShape.getInstance(barStyleData[styleOffset + 15] % 25));
         style.setStartType(GanttBarStartEndType.getInstance(barStyleData[styleOffset + 15] / 25));
         style.setStartColor(MPPUtility.getColor(barStyleData, styleOffset + 16));

         style.setMiddleShape(GanttBarMiddleShape.getInstance(barStyleData[styleOffset]));
         style.setMiddlePattern(ChartPattern.getInstance(barStyleData[styleOffset + 1]));
         style.setMiddleColor(MPPUtility.getColor(barStyleData, styleOffset + 2));

         style.setEndShape(GanttBarStartEndShape.getInstance(barStyleData[styleOffset + 28] % 25));
         style.setEndType(GanttBarStartEndType.getInstance(barStyleData[styleOffset + 28] / 25));
         style.setEndColor(MPPUtility.getColor(barStyleData, styleOffset + 29));

         style.setFromField(getTaskField(file, ByteArrayHelper.getInt(barStyleData, styleOffset + 41)));
         style.setToField(getTaskField(file, ByteArrayHelper.getInt(barStyleData, styleOffset + 45)));

         extractFlags(style, GanttBarShowForTasks.NORMAL, ByteArrayHelper.getLong(barStyleData, styleOffset + 49));
         extractFlags(style, GanttBarShowForTasks.NOT_NORMAL, ByteArrayHelper.getLong(barStyleData, styleOffset + 57));

         style.setRow((ByteArrayHelper.getShort(barStyleData, styleOffset + 65) + 1));

         styleOffset += 195;
      }

      return barStyles;
   }

   @Override public GanttBarStyleException[] processExceptionStyles(ProjectFile file, Props props)
   {
      byte[] barData = props.getByteArray(EXCEPTION_PROPERTIES);
      if (barData == null)
      {
         return EMPTY_EXCEPTIONS;
      }

      //System.out.println(ByteArrayHelper.hexdump(barData, false, 71, ""));
      GanttBarStyleException[] barStyle = new GanttBarStyleException[barData.length / 71];
      int offset = 0;
      for (int loop = 0; loop < barStyle.length; loop++)
      {
         GanttBarStyleException style = new GanttBarStyleException();
         barStyle[loop] = style;

         style.setTaskUniqueID(ByteArrayHelper.getInt(barData, offset));
         style.setGanttBarStyleID(Integer.valueOf(ByteArrayHelper.getShort(barData, offset + 4)));
         style.setBarStyleIndex(ByteArrayHelper.getShort(barData, offset + 4) - 1);

         style.setStartShape(GanttBarStartEndShape.getInstance(barData[offset + 20] % 25));
         style.setStartType(GanttBarStartEndType.getInstance(barData[offset + 20] / 25));
         style.setStartColor(MPPUtility.getColor(barData, offset + 21));

         style.setMiddleShape(GanttBarMiddleShape.getInstance(barData[offset + 6]));
         style.setMiddlePattern(ChartPattern.getInstance(barData[offset + 7]));
         style.setMiddleColor(MPPUtility.getColor(barData, offset + 8));

         style.setEndShape(GanttBarStartEndShape.getInstance(barData[offset + 33] % 25));
         style.setEndType(GanttBarStartEndType.getInstance(barData[offset + 33] / 25));
         style.setEndColor(MPPUtility.getColor(barData, offset + 34));

         style.setLeftText(getTaskField(file, ByteArrayHelper.getInt(barData, offset + 49)));
         style.setRightText(getTaskField(file, ByteArrayHelper.getInt(barData, offset + 53)));
         style.setTopText(getTaskField(file, ByteArrayHelper.getInt(barData, offset + 57)));
         style.setBottomText(getTaskField(file, ByteArrayHelper.getInt(barData, offset + 61)));
         style.setInsideText(getTaskField(file, ByteArrayHelper.getInt(barData, offset + 65)));

         //System.out.println(style);
         offset += 71;
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
    * @param file parent file
    * @param field field ID
    * @return field type
    */
   private FieldType getTaskField(ProjectFile file, int field)
   {
      FieldType result = FieldTypeHelper.getInstance(file, field);
      if (!(result instanceof TaskField))
      {
         return result;
      }

      switch ((TaskField) result)
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

      return result;
   }

   private static final Integer DEFAULT_PROPERTIES = Integer.valueOf(574619656);
   private static final Integer EXCEPTION_PROPERTIES = Integer.valueOf(574619661);

   private static final GanttBarStyle[] EMPTY_STYLES = new GanttBarStyle[0];
   private static final GanttBarStyleException[] EMPTY_EXCEPTIONS = new GanttBarStyleException[0];
}
