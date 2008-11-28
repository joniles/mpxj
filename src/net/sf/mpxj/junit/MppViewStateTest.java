/*
 * file:       MppViewStateTest.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software Limited 2007
 * date:       9-January-2007
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

package net.sf.mpxj.junit;

import java.util.List;

import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ViewState;
import net.sf.mpxj.mpp.MPPReader;

/**
 * Tests to exercise MPP file read functionality for various versions of
 * MPP file.
 */
public class MppViewStateTest extends MPXJTestCase
{

   /**
    * Test view state data read from an MPP9 file.
    * 
    * @throws Exception
    */
   public void testMpp9ViewState() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(m_basedir + "/mpp9viewstate.mpp");
      testViewState(mpp);
   }

   /**
    * Test view state data read from an MPP12 file.
    * 
    * @throws Exception
    */
   public void testMpp12ViewState() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(m_basedir + "/mpp12viewstate.mpp");
      testViewState(mpp);
   }

   /**
    * Test view state.
    * 
    * @param mpp ProjectFile instance
    */
   private void testViewState(ProjectFile mpp)
   {
      ViewState state = mpp.getViewState();
      assertNotNull(state);

      assertEquals("Gantt Chart", state.getViewName());

      List<Integer> list = state.getUniqueIdList();
      assertEquals(50, list.size());
   }
}
