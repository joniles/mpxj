/*
 * file:       GanttBarStyleException.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2005
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

package com.tapsterrock.mpp;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

/**
 * This class represents the default style for a Gantt chart bar.
 */
public final class GanttBarStyleException extends GanttBarCommonStyle
{
   /**
    * Constructor.
    * 
    * @param data data from MS project
    * @param offset offset into data
    */
   public GanttBarStyleException (byte[] data, int offset)
   {
      m_taskID = MPPUtility.getInt(data, offset);
      m_barStyleIndex = MPPUtility.getShort(data, offset+4) - 1;
      m_middleShape = data[offset+6];
      m_middlePattern = data[offset+7];
      m_middleColor = ColorType.getInstance(data[offset+8]);
      m_startShapeAndStyle = data[offset+9];
      m_startColor = ColorType.getInstance(data[offset+10]);
      m_endShapeAndStyle = data[offset+11];
      m_endColor = ColorType.getInstance(data[offset+12]);
      
      m_leftText = TaskField.getInstance(MPPUtility.getShort(data, offset+16));
      m_rightText = TaskField.getInstance(MPPUtility.getShort(data, offset+20));
      m_topText = TaskField.getInstance(MPPUtility.getShort(data, offset+24));
      m_bottomText = TaskField.getInstance(MPPUtility.getShort(data, offset+28));
      m_insideText = TaskField.getInstance(MPPUtility.getShort(data, offset+32));
   }
   
   /**
    * Retrieve the unique task ID for the task to which this style
    * exception applies.
    * 
    * @return task ID
    */
   public int getTaskID()
   {
      return (m_taskID);
   }
      
   /**
    * Retrieves the index of the bar style to which this exception applies.
    * The standar bar styles are held in an array, retrieved using the 
    * GanttChartView.getBarStyles() method. The index returned by this method
    * is an index into the array of bar styles. The significance of this is
    * that a single bar a=on a Gantt chart could have one or more exceptions
    * associated wit it, but the exceptions will only be applied if the style
    * of the bar currently being displayed matches the style recorded here
    * in the style exception.
    * 
    * @return bar style index
    */
   public int getBarStyleIndex ()
   {
      return (m_barStyleIndex);
   }
   
   /**
    * Generate a string representation of this instance.
    * 
    * @return string representation of this instance
    */   
   public String toString ()
   {
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      PrintWriter pw = new PrintWriter (os);
      pw.println ("   [GanttBarStyleException");
      pw.println ("      TaskID=" + m_taskID);  
      pw.println ("      BarStyleIndex=" + m_barStyleIndex);  
      pw.println (super.toString());            
      pw.println ("   ]");
      pw.flush();
      return (os.toString());         
   }

   private int m_taskID;
   private int m_barStyleIndex;
}
