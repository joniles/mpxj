/*
 * file:       ViewFactory.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2005
 * date:       Apr 7, 2005
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
