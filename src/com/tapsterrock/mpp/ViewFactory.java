/*
 * file:       ViewFactory.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2005
 * date:       Apr 7, 2005
 */
 
package com.tapsterrock.mpp;

import java.io.IOException;

/**
 * This interface is implemented by classes which can create View classes
 * from the data extracted from an MS Project file.
 */
interface ViewFactory
{
   /**
    * This method is called to create a view.
    * 
    * @param fixedData view fixed data
    * @param varData view var data
    * @return View instance
    * @throws IOException
    */
   public View createView (byte[] fixedData, Var2Data varData)
      throws IOException;
}
