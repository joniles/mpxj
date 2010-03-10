/*
 * file:       MppViewStateTest.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2007
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
    * Test view state data read from an MPP14 file.
    * 
    * @throws Exception
    */
   public void testMpp14ViewState() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(m_basedir + "/mpp14viewstate.mpp");
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

      for (int loop = 0; loop < UNIQUE_ID_LIST.length; loop++)
      {
         assertEquals(UNIQUE_ID_LIST[loop], list.get(loop).intValue());
      }
   }

   private static final int[] UNIQUE_ID_LIST =
   {
      5,
      6,
      7,
      8,
      9,
      10,
      11,
      12,
      13,
      14,
      15,
      16,
      17,
      18,
      19,
      20,
      21,
      22,
      23,
      24,
      25,
      26,
      27,
      28,
      29,
      30,
      31,
      32,
      33,
      34,
      35,
      36,
      37,
      38,
      39,
      40,
      41,
      42,
      43,
      44,
      45,
      46,
      47,
      48,
      49,
      50,
      51,
      52,
      53,
      54
   };
}
