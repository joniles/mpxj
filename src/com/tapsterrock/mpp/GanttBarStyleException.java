/*
 * file:       GanttBarStyleException.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2005
 * date:       Apr 13, 2005
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
