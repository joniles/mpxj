/*
 * file:       MPXJTest.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2006
 * date:       28-Feb-2006
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

import junit.framework.TestSuite;

/**
 * Test suite to collect together MPXJ tests.
 */
public class MPXJTest extends TestSuite
{
   /**
    * Construcor.
    */
   public MPXJTest ()
   {
      addTestSuite (BasicTest.class);
      addTestSuite (ProjectCalendarTest.class);
      addTestSuite (SplitTaskTest.class);      
      addTestSuite (GraphicalIndicatorTest.class);
      addTestSuite (SlackTest.class);
      addTestSuite (MppProjectHeaderTest.class);
      addTestSuite (MppTaskTest.class);
      addTestSuite (MppResourceTest.class);
      addTestSuite (MppSubprojectTest.class);
   }

   /**
    * Dummy test used to ensure the test suites added in the constructor
    * are run.
    */
   public void testAll ()
   {
      // dummy test
   }   
}
