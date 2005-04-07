/*
 * file:       DefaultViewFactory.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2005
 * date:       Apr 7, 2005
 */
 
package com.tapsterrock.mpp;

import java.io.IOException;

/**
 * Default implementation of a view factory for MPP9 files.
 */
class DefaultViewFactory implements ViewFactory
{
   /**
    * @see ViewFactory#createView(byte[], Var2Data)
    */
   public View createView (byte[] fixedData, Var2Data varData)
      throws IOException
   {
      View view;
      int type = MPPUtility.getShort(fixedData, 112);
      switch (type)
      {
         case View.GANTT_CHART:
         {
            view = new GanttChartView9 (fixedData, varData);
            break;
         }
         
         default:
         {
            view = new View9 (fixedData);
            break;
         }
      }
      
      return (view);
   }
}
