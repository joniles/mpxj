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

package org.mpxj.junit;

import static org.junit.Assert.*;

import java.util.List;

import org.mpxj.ProjectFile;
import org.mpxj.ViewState;
import org.mpxj.mpp.MPPReader;

import org.junit.Test;

/**
 * Tests to exercise MPP file read functionality for various versions of
 * MPP file.
 */
public class MppViewStateTest
{

   /**
    * Test view state data read from an MPP9 file.
    */
   @Test public void testMpp9ViewState() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp9viewstate.mpp"));
      testViewState(mpp);
   }

   /**
    * Test view state data read from an MPP9 file saved by Project 2007.
    */
   @Test public void testMpp9ViewStateFrom12() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp9viewstate-from12.mpp"));
      testViewState(mpp);
   }

   /**
    * Test view state data read from an MPP9 file saved by Project 2010.    */
   @Test public void testMpp9ViewStateFrom14()
   {
      //ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp9viewstate-from14.mpp"));
      //testViewState(mpp);
   }

   /**
    * Test view state data read from an MPP12 file.
    */
   @Test public void testMpp12ViewState() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp12viewstate.mpp"));
      testViewState(mpp);
   }

   /**
    * Test view state data read from an MPP12 file saved by Project 2010.
    */
   @Test public void testMpp12ViewStateFrom14()
   {
      //ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp12viewstate-from14.mpp"));
      //testViewState(mpp);
   }

   /**
    * Test view state data read from an MPP14 file.
    */
   @Test public void testMpp14ViewState() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp14viewstate.mpp"));
      testViewState(mpp);
   }

   /**
    * Test view state.
    *
    * @param mpp ProjectFile instance
    */
   private void testViewState(ProjectFile mpp)
   {
      ViewState state = mpp.getViews().getViewState();
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
