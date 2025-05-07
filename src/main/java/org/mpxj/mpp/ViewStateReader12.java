/*
 * file:       ViewStateReader12.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2007
 * date:       Jan 07, 2007
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

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * This class allows the saved state of a view to be read from an MPP9 file.
 */
public final class ViewStateReader12 extends ViewStateReader
{
   @Override protected Props getProps(Var2Data varData) throws IOException
   {
      Props props = null;
      byte[] propsData = varData.getByteArray(PROPS_ID, PROPS_TYPE);
      if (propsData != null)
      {
         props = new Props12(new ByteArrayInputStream(propsData));
         //System.out.println(props);
      }
      return (props);
   }

   private static final Integer PROPS_ID = Integer.valueOf(1);
   private static final Integer PROPS_TYPE = Integer.valueOf(6);
}
