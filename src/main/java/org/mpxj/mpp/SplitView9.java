/*
 * file:       SplitView9.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2005
 * date:       Jan 27, 2006
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

import org.mpxj.ProjectFile;

/**
 * This class represents a user defined view in Microsoft Project
 * which is made up of two existing views, typically shown above
 * and below the division of a split screen.
 */
public class SplitView9 extends GenericView9
{
   /**
    * Constructor.
    *
    * @param parent parent file
    * @param fixedData fixed data block
    * @param varData var data block
    */
   SplitView9(ProjectFile parent, byte[] fixedData, Var2Data varData)
      throws IOException
   {
      super(parent, fixedData, varData);

      byte[] propsData = varData.getByteArray(m_id, PROPERTIES);
      if (propsData != null)
      {
         Props9 props = new Props9(new ByteArrayInputStream(propsData));

         byte[] upperViewName = props.getByteArray(UPPER_VIEW_NAME);
         if (upperViewName != null)
         {
            m_upperViewName = MPPUtility.removeAmpersands(MPPUtility.getUnicodeString(upperViewName, 0));
         }

         byte[] lowerViewName = props.getByteArray(LOWER_VIEW_NAME);
         if (lowerViewName != null)
         {
            m_lowerViewName = MPPUtility.removeAmpersands(MPPUtility.getUnicodeString(lowerViewName, 0));
         }
      }
   }

   /**
    * Retrieves the lower view name.
    *
    * @return lower view name
    */
   public String getLowerViewName()
   {
      return m_lowerViewName;
   }

   /**
    * Retrieves the upper view name.
    *
    * @return upper view name
    */
   public String getUpperViewName()
   {
      return m_upperViewName;
   }

   @Override public String toString()
   {
      return ("[SplitView9 upperViewName=" + m_upperViewName + " lowerViewName=" + m_lowerViewName + "]");
   }

   private String m_upperViewName;
   private String m_lowerViewName;

   private static final Integer PROPERTIES = Integer.valueOf(1);
   private static final Integer UPPER_VIEW_NAME = Integer.valueOf(574619658);
   private static final Integer LOWER_VIEW_NAME = Integer.valueOf(574619659);
}
