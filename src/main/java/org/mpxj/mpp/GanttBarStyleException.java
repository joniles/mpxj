/*
 * file:       GanttBarStyleException.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2005
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

package org.mpxj.mpp;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

/**
 * This class represents the default style for a Gantt chart bar.
 */
public final class GanttBarStyleException extends GanttBarCommonStyle
{
   /**
    * Retrieve the unique task ID for the task to which this style
    * exception applies.
    *
    * @return task ID
    */
   public int getTaskUniqueID()
   {
      return (m_taskUniqueID);
   }

   /**
    * Sets the task unique ID.
    *
    * @param id task unique ID
    */
   public void setTaskUniqueID(int id)
   {
      m_taskUniqueID = id;
   }

   /**
    * Set the ID of the Gantt Bar Style to which this exception is applied.
    *
    * @return bar style ID
    */
   public Integer getGanttBarStyleID()
   {
      return m_ganttBarStyleID;
   }

   /**
    * Set the ID of the Gantt Bar Style to which this exception is applied.
    *
    * @param value bar style ID
    */
   public void setGanttBarStyleID(Integer value)
   {
      m_ganttBarStyleID = value;
   }

   /**
    * Sets the bar style index.
    *
    * @param index bar style index
    */
   public void setBarStyleIndex(int index)
   {
      m_barStyleIndex = index;
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
      pw.println("   [GanttBarStyleException");
      pw.println("      TaskID=" + m_taskUniqueID);
      pw.println("      BarStyleID=" + m_ganttBarStyleID);
      pw.println("      BarStyleIndex=" + m_barStyleIndex);
      pw.println(super.toString());
      pw.println("   ]");
      pw.flush();
      return (os.toString());
   }

   private int m_taskUniqueID;
   private int m_barStyleIndex;
   private Integer m_ganttBarStyleID;
}
