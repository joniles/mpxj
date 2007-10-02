/*
 * file:       GanttBarStyle.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software Limited 2005
 * date:       Apr 13, 2005
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
   public GanttBarStyle (String name, byte[] data, int offset)
   {
      m_name = name;
      m_middleShape = data[offset];
      m_middlePattern = data[offset+1];
      m_middleColor = ColorType.getInstance(data[offset+2]);
      m_startShapeAndStyle = data[offset+4];
      m_startColor = ColorType.getInstance(data[offset+5]);
      m_endShapeAndStyle = data[offset+6];
      m_endColor = ColorType.getInstance(data[offset+7]);

      m_fromField = MPPTaskField.getInstance(MPPUtility.getShort(data, offset+8));
      m_toField = MPPTaskField.getInstance(MPPUtility.getShort(data, offset+12));

      int flags = MPPUtility.getShort(data, offset+16);

      m_showForNormalTasks = (flags & 0x0001) != 0;
      m_showForMilestoneTasks = (flags & 0x0002) != 0;
      m_showForSummaryTasks = (flags & 0x0004) != 0;
      m_showForCriticalTasks = (flags & 0x0008) != 0;
      m_showForNonCriticalTasks = (flags & 0x0010) != 0;
      m_showForMarkedTasks = (flags & 0x0020) != 0;
      m_showForFinishedTasks = (flags & 0x0040) != 0;
      m_showForInProgressTasks = (flags & 0x0080) != 0;
      m_showForNotFinishedTasks = (flags & 0x0100) != 0;
      m_showForNotStartedTasks = (flags & 0x0200) != 0;

      m_row = data[offset+32]+1;

      m_leftText = MPPTaskField.getInstance(MPPUtility.getShort(data, offset+34));
      m_rightText = MPPTaskField.getInstance(MPPUtility.getShort(data, offset+38));
      m_topText = MPPTaskField.getInstance(MPPUtility.getShort(data, offset+42));
      m_bottomText = MPPTaskField.getInstance(MPPUtility.getShort(data, offset+46));
      m_insideText = MPPTaskField.getInstance(MPPUtility.getShort(data, offset+50));
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
    * Generate a string representation of this instance.
    *
    * @return string representation of this instance
    */
   @Override public String toString ()
   {
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      PrintWriter pw = new PrintWriter (os);
      pw.println ("   [GanttBarStyle");
      pw.println ("      Name=" + m_name);
      pw.println ("      FromField=" + m_fromField);
      pw.println ("      ToField=" + m_toField);
      pw.println ("      ShowForNormalTasks=" + m_showForNormalTasks);
      pw.println ("      ShowForMilestoneTasks=" + m_showForMilestoneTasks);
      pw.println ("      ShowForSummaryTasks=" + m_showForSummaryTasks);
      pw.println ("      ShowForCriticalTasks=" + m_showForCriticalTasks);
      pw.println ("      ShowForNonCriticalTasks=" + m_showForNonCriticalTasks);
      pw.println ("      ShowForMarkedTasks=" + m_showForMarkedTasks);
      pw.println ("      ShowForFinishedTasks=" + m_showForFinishedTasks);
      pw.println ("      ShowForInProgressTasks=" + m_showForInProgressTasks);
      pw.println ("      ShowForNotFinishedTasks=" + m_showForNotFinishedTasks);
      pw.println ("      ShowForNotStartedTasks=" + m_showForNotStartedTasks);
      pw.println ("      Row=" + m_row);
      pw.println (super.toString());
      pw.println ("   ]");
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
}
