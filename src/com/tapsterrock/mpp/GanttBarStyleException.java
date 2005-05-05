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
      pw.println (super.toString());            
      pw.println ("   ]");
      pw.flush();
      return (os.toString());         
   }

   private int m_taskID;
}
