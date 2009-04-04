/*
 * file:       GanttBarStyle.java
 * author:     Jon Iles
 *             Tom Ollar
 * copyright:  (c) Packwood Software Limited 2005-2009
 * date:       13/04/2005
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

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

import net.sf.mpxj.MPPTaskField;
import net.sf.mpxj.TaskField;

/**
 * This class represents the default style for a Gantt chart bar.
 */
public final class GanttBarStyle extends GanttBarCommonStyle
{
   /**
    * Constructor.
    *
    * @param name style name
    * @param data data from MS project
    * @param offset offset into data
    */
   public GanttBarStyle(String name, byte[] data, int offset)
   {
      m_name = name;
      m_middleShape = data[offset];
      m_middlePattern = data[offset + 1];
      m_middleShapeAndPattern = GanttBarMiddleShape.getInstance(m_middleShape, m_middlePattern);
      m_middleColor = ColorType.getInstance(data[offset + 2]);
      m_startShapeAndStyle = data[offset + 4];
      m_startShape = GanttBarStartAndEndShape.getInstance(m_startShapeAndStyle);
      m_startColor = ColorType.getInstance(data[offset + 5]);
      m_endShapeAndStyle = data[offset + 6];
      m_endShape = GanttBarStartAndEndShape.getInstance(m_endShapeAndStyle);
      m_endColor = ColorType.getInstance(data[offset + 7]);

      m_startShape.setColor(m_startColor);
      m_middleShapeAndPattern.setColor(m_middleColor);
      m_endShape.setColor(m_endColor);

      m_fromField = MPPTaskField.getInstance(MPPUtility.getShort(data, offset + 8));

      int toField = MPPUtility.getShort(data, offset + 12);

      m_toField = MPPTaskField.getInstance(toField);

      int flags1 = MPPUtility.getShort(data, offset + 16);
      int flags2 = MPPUtility.getShort(data, offset + 18);
      int flags3 = MPPUtility.getShort(data, offset + 20);
      int flags4 = MPPUtility.getShort(data, offset + 24);
      int flags5 = MPPUtility.getShort(data, offset + 26);
      int flags6 = MPPUtility.getShort(data, offset + 28);

      addToSet(GanttBarShowForCriteriaEnum.NORMAL, flags1, false);
      addToSet(GanttBarShowForCriteriaEnum.FLAG1, flags2, false);
      addToSet(GanttBarShowForCriteriaEnum.FLAG13, flags3, false);
      addToSet(GanttBarShowForCriteriaEnum.NORMAL, flags4, true);
      addToSet(GanttBarShowForCriteriaEnum.FLAG1, flags5, true);
      addToSet(GanttBarShowForCriteriaEnum.FLAG13, flags6, true);

      m_showForNormalTasks = m_showForCriteriaSet.contains(SHOW_FOR_NORMAL_TASKS);
      m_showForMilestoneTasks = m_showForCriteriaSet.contains(SHOW_FOR_MILESTONE_TASKS);
      m_showForSummaryTasks = m_showForCriteriaSet.contains(SHOW_FOR_SUMMARY_TASKS);
      m_showForCriticalTasks = m_showForCriteriaSet.contains(SHOW_FOR_CRITICAL_TASKS);
      m_showForNonCriticalTasks = m_showForCriteriaSet.contains(SHOW_FOR_NON_CRITICAL_TASKS);
      m_showForMarkedTasks = m_showForCriteriaSet.contains(SHOW_FOR_MARKED_TASKS);
      m_showForFinishedTasks = m_showForCriteriaSet.contains(SHOW_FOR_FINISHED_TASKS);
      m_showForInProgressTasks = m_showForCriteriaSet.contains(SHOW_FOR_IN_PROGRESS_TASKS);
      m_showForNotFinishedTasks = m_showForCriteriaSet.contains(SHOW_FOR_NOT_FINISHED_TASKS);
      m_showForNotStartedTasks = m_showForCriteriaSet.contains(SHOW_FOR_NOT_STARTED_TASKS);

      m_row = data[offset + 32] + 1;

      m_leftText = MPPTaskField.getInstance(MPPUtility.getShort(data, offset + 34));
      m_rightText = MPPTaskField.getInstance(MPPUtility.getShort(data, offset + 38));
      m_topText = MPPTaskField.getInstance(MPPUtility.getShort(data, offset + 42));
      m_bottomText = MPPTaskField.getInstance(MPPUtility.getShort(data, offset + 46));
      m_insideText = MPPTaskField.getInstance(MPPUtility.getShort(data, offset + 50));
   }

   /**
    * Evaluate the the two-byte short stored in the file, determine if it contains one or more of 16 different
    * flags. The flag values correspond to ordinal positions in ShowGanttBarCriteriaEnum, making
    * it easy to map flag intersections to enumeration values. An index integer counter is used to avoid pow or sqr.
    *
    * @param baseCriteria the base criteria for the given flag, there are three flags so the bases are 0, 16 and 32
    * @param flagValue the flag value, originally a short read directly from the mpp file
    * @param notCondition whether this is the 'NOT' of this condition, there is 'Normal' and 'Not Normal', both
    * represented by the same 'Normal' enum value with the 'Not' property additionally assigned
    * Theoretically the resultSet could contain 86 values, the original 43, and the 'Not' of each of those, but
    * practically not all of those values could be saved successfully in a Microsoft Project document as the length
    * of the show for criteria field is limited.
    */
   private void addToSet(GanttBarShowForCriteriaEnum baseCriteria, int flagValue, boolean notCondition)
   {
      int index = 0;
      short flag = 0x0001;

      while (index <= 15)
      {
         if ((flagValue & flag) != 0)
         {
            GanttBarShowForCriteriaEnum enumValue = GanttBarShowForCriteriaEnum.getInstance(baseCriteria.getValue() + index);

            m_showForCriteriaSet.add(new GanttBarShowForCriteria(enumValue, notCondition));
         }

         flag *= 2;

         index++;
      }
   }

   /**
    * Retrieve the field used to determine the start date of this bar.
    *
    * @return from field
    */
   public TaskField getFromField()
   {
      return (m_fromField);
   }

   /**
    * Retrieve the name of this style.
    *
    * @return style name
    */
   public String getName()
   {
      return (m_name);
   }

   /**
    * Retrieve the row number of this bar.
    *
    * @return row number
    */
   public int getRow()
   {
      return (m_row);
   }

   /**
    * Retrieve a flag indicating that this bar is shown for critical tasks.
    *
    * @return boolean flag
    */
   public boolean getShowForCriticalTasks()
   {
      return (m_showForCriticalTasks);
   }

   /**
    * Retrieve a flag indicating that this bar is shown for finished tasks.
    *
    * @return boolean flag
    */
   public boolean getShowForFinishedTasks()
   {
      return (m_showForFinishedTasks);
   }

   /**
    * Retrieve a flag indicating that this bar is shown for in progress tasks.
    *
    * @return boolean flag
    */
   public boolean getShowForInProgressTasks()
   {
      return (m_showForInProgressTasks);
   }

   /**
    * Retrieve a flag indicating that this bar is shown for marked tasks.
    *
    * @return boolean flag
    */
   public boolean getShowForMarkedTasks()
   {
      return (m_showForMarkedTasks);
   }

   /**
    * Retrieve a flag indicating that this bar is shown for milestone tasks.
    *
    * @return boolean flag
    */
   public boolean getShowForMilestoneTasks()
   {
      return (m_showForMilestoneTasks);
   }

   /**
    * Retrieve a flag indicating that this bar is shown for non critical tasks.
    *
    * @return boolean flag
    */
   public boolean getShowForNonCriticalTasks()
   {
      return (m_showForNonCriticalTasks);
   }

   /**
    * Retrieve a flag indicating that this bar is shown for normal tasks.
    *
    * @return boolean flag
    */
   public boolean getShowForNormalTasks()
   {
      return (m_showForNormalTasks);
   }

   /**
    * Retrieve a flag indicating that this bar is shown for not finished tasks.
    *
    * @return boolean flag
    */
   public boolean getShowForNotFinishedTasks()
   {
      return (m_showForNotFinishedTasks);
   }

   /**
    * Retrieve a flag indicating that this bar is shown for not started tasks.
    *
    * @return boolean flag
    */
   public boolean getShowForNotStartedTasks()
   {
      return (m_showForNotStartedTasks);
   }

   /**
    * Retrieve a flag indicating that this bar is shown for summary tasks.
    *
    * @return boolean flag
    */
   public boolean getShowForSummaryTasks()
   {
      return (m_showForSummaryTasks);
   }

   /**
    * Retrieve the field used to determine the end date of this bar.
    *
    * @return to field
    */
   public TaskField getToField()
   {
      return (m_toField);
   }

   /**
    * Retrieve the start shape of this bar.
    *
    * @return start shape
    */
   public GanttBarStartAndEndShape getStartShape()
   {
      return (m_startShape);
   }

   /**
    * Retrieve the end shape of this bar.
    *
    * @return end shape
    */
   public GanttBarStartAndEndShape getEndShape()
   {
      return (m_endShape);
   }

   /**
    * Retrieve the end shape of this bar.
    *
    * @return end shape
    */
   public GanttBarMiddleShape getMiddleShapeAndPattern()
   {
      return (m_middleShapeAndPattern);
   }

   /**
    * Retrieve set of Show For criteria for this style.
    *
    * @return show for criteria
    */
   public Set<GanttBarShowForCriteria> getShowForCriteriaSet()
   {
      return (m_showForCriteriaSet);
   }

   /**
    * Generate a string representation of this instance.
    *
    * @return string representation of this instance
    */
   @Override public String toString()
   {
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      PrintWriter pw = new PrintWriter(os);
      pw.println("   [GanttBarStyle");
      pw.println("      Name=" + m_name);
      pw.println("      FromField=" + m_fromField);
      pw.println("      ToField=" + m_toField);
      pw.println("      ShowForNormalTasks=" + m_showForNormalTasks);
      pw.println("      ShowForMilestoneTasks=" + m_showForMilestoneTasks);
      pw.println("      ShowForSummaryTasks=" + m_showForSummaryTasks);
      pw.println("      ShowForCriticalTasks=" + m_showForCriticalTasks);
      pw.println("      ShowForNonCriticalTasks=" + m_showForNonCriticalTasks);
      pw.println("      ShowForMarkedTasks=" + m_showForMarkedTasks);
      pw.println("      ShowForFinishedTasks=" + m_showForFinishedTasks);
      pw.println("      ShowForInProgressTasks=" + m_showForInProgressTasks);
      pw.println("      ShowForNotFinishedTasks=" + m_showForNotFinishedTasks);
      pw.println("      ShowForNotStartedTasks=" + m_showForNotStartedTasks);
      pw.println("      Row=" + m_row);
      pw.println(super.toString());
      pw.println("   ]");
      pw.flush();
      return (os.toString());
   }

   private String m_name;
   private TaskField m_fromField;
   private TaskField m_toField;

   private boolean m_showForNormalTasks;
   private boolean m_showForMilestoneTasks;
   private boolean m_showForSummaryTasks;
   private boolean m_showForCriticalTasks;
   private boolean m_showForNonCriticalTasks;
   private boolean m_showForMarkedTasks;
   private boolean m_showForFinishedTasks;
   private boolean m_showForInProgressTasks;
   private boolean m_showForNotFinishedTasks;
   private boolean m_showForNotStartedTasks;

   private int m_row;

   private Set<GanttBarShowForCriteria> m_showForCriteriaSet = new HashSet<GanttBarShowForCriteria>();
   private GanttBarStartAndEndShape m_startShape;
   private GanttBarStartAndEndShape m_endShape;
   private GanttBarMiddleShape m_middleShapeAndPattern;

   private static final GanttBarShowForCriteria SHOW_FOR_NORMAL_TASKS = new GanttBarShowForCriteria(GanttBarShowForCriteriaEnum.NORMAL, false);
   private static final GanttBarShowForCriteria SHOW_FOR_MILESTONE_TASKS = new GanttBarShowForCriteria(GanttBarShowForCriteriaEnum.MILESTONE, false);
   private static final GanttBarShowForCriteria SHOW_FOR_SUMMARY_TASKS = new GanttBarShowForCriteria(GanttBarShowForCriteriaEnum.SUMMARY, false);
   private static final GanttBarShowForCriteria SHOW_FOR_CRITICAL_TASKS = new GanttBarShowForCriteria(GanttBarShowForCriteriaEnum.CRITICAL, false);
   private static final GanttBarShowForCriteria SHOW_FOR_NON_CRITICAL_TASKS = new GanttBarShowForCriteria(GanttBarShowForCriteriaEnum.NONCRITICAL, false);
   private static final GanttBarShowForCriteria SHOW_FOR_MARKED_TASKS = new GanttBarShowForCriteria(GanttBarShowForCriteriaEnum.MARKED, false);
   private static final GanttBarShowForCriteria SHOW_FOR_FINISHED_TASKS = new GanttBarShowForCriteria(GanttBarShowForCriteriaEnum.FINISHED, false);
   private static final GanttBarShowForCriteria SHOW_FOR_IN_PROGRESS_TASKS = new GanttBarShowForCriteria(GanttBarShowForCriteriaEnum.INPROGRESS, false);
   private static final GanttBarShowForCriteria SHOW_FOR_NOT_FINISHED_TASKS = new GanttBarShowForCriteria(GanttBarShowForCriteriaEnum.NOTFINISHED, false);
   private static final GanttBarShowForCriteria SHOW_FOR_NOT_STARTED_TASKS = new GanttBarShowForCriteria(GanttBarShowForCriteriaEnum.NOTSTARTED, false);
}
