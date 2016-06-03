/*
 * file:       GanttarStyleFactoryCommon.java
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

import net.sf.mpxj.common.MPPTaskField;

/**
 * Reads Gantt bar styles from a MPP9 and MPP12 files.
 */
public final class GanttBarStyleFactoryCommon implements GanttBarStyleFactory
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
         barStyles = new GanttBarStyle[barStyleData[812]];
         int styleOffset = 840;
         int nameOffset = styleOffset + (barStyles.length * 58);

         for (int loop = 0; loop < barStyles.length; loop++)
         {
            String styleName = MPPUtility.getUnicodeString(barStyleData, nameOffset);
            nameOffset += (styleName.length() + 1) * 2;
            GanttBarStyle style = new GanttBarStyle();
            barStyles[loop] = style;

            style.setName(styleName);

            style.setMiddleShape(GanttBarMiddleShape.getInstance(barStyleData[styleOffset]));
            style.setMiddlePattern(ChartPattern.getInstance(barStyleData[styleOffset + 1]));
            style.setMiddleColor(ColorType.getInstance(barStyleData[styleOffset + 2]).getColor());

            style.setStartShape(GanttBarStartEndShape.getInstance(barStyleData[styleOffset + 4] % 21));
            style.setStartType(GanttBarStartEndType.getInstance(barStyleData[styleOffset + 4] / 21));
            style.setStartColor(ColorType.getInstance(barStyleData[styleOffset + 5]).getColor());

            style.setEndShape(GanttBarStartEndShape.getInstance(barStyleData[styleOffset + 6] % 21));
            style.setEndType(GanttBarStartEndType.getInstance(barStyleData[styleOffset + 6] / 21));
            style.setEndColor(ColorType.getInstance(barStyleData[styleOffset + 7]).getColor());

            style.setFromField(MPPTaskField.getInstance(MPPUtility.getShort(barStyleData, styleOffset + 8)));
            style.setToField(MPPTaskField.getInstance(MPPUtility.getShort(barStyleData, styleOffset + 12)));

            extractFlags(style, GanttBarShowForTasks.NORMAL, MPPUtility.getLong6(barStyleData, styleOffset + 16));
            extractFlags(style, GanttBarShowForTasks.NOT_NORMAL, MPPUtility.getLong6(barStyleData, styleOffset + 24));

            style.setRow(barStyleData[styleOffset + 32] + 1);

            style.setLeftText(MPPTaskField.getInstance(MPPUtility.getShort(barStyleData, styleOffset + 34)));
            style.setRightText(MPPTaskField.getInstance(MPPUtility.getShort(barStyleData, styleOffset + 38)));
            style.setTopText(MPPTaskField.getInstance(MPPUtility.getShort(barStyleData, styleOffset + 42)));
            style.setBottomText(MPPTaskField.getInstance(MPPUtility.getShort(barStyleData, styleOffset + 46)));
            style.setInsideText(MPPTaskField.getInstance(MPPUtility.getShort(barStyleData, styleOffset + 50)));

            styleOffset += 58;
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
         barStyle = new GanttBarStyleException[barData.length / 38];
         int offset = 0;
         for (int loop = 0; loop < barStyle.length; loop++)
         {
            GanttBarStyleException style = new GanttBarStyleException();
            barStyle[loop] = style;

            //System.out.println("GanttBarStyleException");
            //System.out.println(MPPUtility.hexdump(data, offset, 38, false));

            style.setTaskUniqueID(MPPUtility.getInt(barData, offset));
            style.setBarStyleIndex(MPPUtility.getShort(barData, offset + 4) - 1);

            style.setStartShape(GanttBarStartEndShape.getInstance(barData[offset + 9] % 21));
            style.setStartType(GanttBarStartEndType.getInstance(barData[offset + 9] / 21));
            style.setStartColor(ColorType.getInstance(barData[offset + 10]).getColor());

            style.setMiddleShape(GanttBarMiddleShape.getInstance(barData[offset + 6]));
            style.setMiddlePattern(ChartPattern.getInstance(barData[offset + 7]));

            style.setMiddleColor(ColorType.getInstance(barData[offset + 8]).getColor());

            style.setEndShape(GanttBarStartEndShape.getInstance(barData[offset + 11] % 21));
            style.setEndType(GanttBarStartEndType.getInstance(barData[offset + 11] / 21));
            style.setEndColor(ColorType.getInstance(barData[offset + 12]).getColor());

            style.setLeftText(MPPTaskField.getInstance(MPPUtility.getShort(barData, offset + 16)));
            style.setRightText(MPPTaskField.getInstance(MPPUtility.getShort(barData, offset + 20)));
            style.setTopText(MPPTaskField.getInstance(MPPUtility.getShort(barData, offset + 24)));
            style.setBottomText(MPPTaskField.getInstance(MPPUtility.getShort(barData, offset + 28)));
            style.setInsideText(MPPTaskField.getInstance(MPPUtility.getShort(barData, offset + 32)));

            offset += 38;
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

      while (index < 48)
      {
         if ((flagValue & flag) != 0)
         {
            GanttBarShowForTasks enumValue = GanttBarShowForTasks.getInstance(baseCriteria.getValue() + index);

            style.addShowForTasks(enumValue);
         }

         flag = flag << 1;

         index++;
      }
   }

   private static final Integer DEFAULT_PROPERTIES = Integer.valueOf(574619686);
   private static final Integer EXCEPTION_PROPERTIES = Integer.valueOf(574619661);
}
