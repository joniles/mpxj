/*
 * file:       SplitTaskTest.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software Limited 2005
 * date:       02-Mar-2006
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

import java.util.LinkedList;
import java.util.List;

import net.sf.mpxj.Duration;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Task;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.mpp.MPPReader;


/**
 * The tests contained in this class exercise the split task functionality.
 */
public class SplitTaskTest extends MPXJTestCase
{
   /**
    * Exercise split task functionality.
    *
    * @throws Exception
    */
   public void testSplits1 ()
      throws Exception
   {
      List<Duration> splits = new LinkedList<Duration>();
      splits.add(Duration.getInstance(32, TimeUnit.HOURS));
      splits.add(Duration.getInstance(40, TimeUnit.HOURS));
      splits.add(Duration.getInstance(88, TimeUnit.HOURS));

      ProjectFile mpp = new MPPReader().read (m_basedir + "/splits9a.mpp");
      Task task = mpp.getTaskByUniqueID(Integer.valueOf(1));
      assertNull(task.getSplits());

      task = mpp.getTaskByUniqueID(Integer.valueOf(2));
      assertEquals(splits, task.getSplits());

      task = mpp.getTaskByUniqueID(Integer.valueOf(3));
      assertEquals(splits, task.getSplits());

      task = mpp.getTaskByUniqueID(Integer.valueOf(4));
      assertEquals(splits, task.getSplits());

      task = mpp.getTaskByUniqueID(Integer.valueOf(5));
      assertEquals(splits, task.getSplits());

      task = mpp.getTaskByUniqueID(Integer.valueOf(6));
      assertEquals(splits, task.getSplits());

      splits.clear();
      splits.add(Duration.getInstance(16, TimeUnit.HOURS));
      splits.add(Duration.getInstance(48, TimeUnit.HOURS));
      splits.add(Duration.getInstance(88, TimeUnit.HOURS));
      splits.add(Duration.getInstance(104, TimeUnit.HOURS));
      splits.add(Duration.getInstance(128, TimeUnit.HOURS));

      task = mpp.getTaskByUniqueID(Integer.valueOf(7));
      assertEquals(splits, task.getSplits());

      task = mpp.getTaskByUniqueID(Integer.valueOf(8));
      assertEquals(splits, task.getSplits());

      task = mpp.getTaskByUniqueID(Integer.valueOf(9));
      assertEquals(splits, task.getSplits());

      task = mpp.getTaskByUniqueID(Integer.valueOf(10));
      assertEquals(splits, task.getSplits());

      task = mpp.getTaskByUniqueID(Integer.valueOf(11));
      assertEquals(splits, task.getSplits());

      task = mpp.getTaskByUniqueID(Integer.valueOf(12));
      assertEquals(splits, task.getSplits());

      task = mpp.getTaskByUniqueID(Integer.valueOf(13));
      assertEquals(splits, task.getSplits());
   }   
   
   /**
    * Exercise split task functionality.
    *
    * @throws Exception
    */
   public void testSplits2 ()
      throws Exception
   {
      List<Duration> splits = new LinkedList<Duration>();

      ProjectFile mpp = new MPPReader().read (m_basedir + "/splits9b.mpp");
      
      Task task = mpp.getTaskByUniqueID(Integer.valueOf(1));
      splits.clear();
      splits.add(Duration.getInstance(16, TimeUnit.HOURS));
      splits.add(Duration.getInstance(40, TimeUnit.HOURS));
      splits.add(Duration.getInstance(64, TimeUnit.HOURS));      
      splits.add(Duration.getInstance(88, TimeUnit.HOURS));      
      splits.add(Duration.getInstance(128, TimeUnit.HOURS));      
      assertEquals(splits, task.getSplits());

      task = mpp.getTaskByUniqueID(Integer.valueOf(3));
      assertNull(task.getSplits());

      task = mpp.getTaskByUniqueID(Integer.valueOf(4));
      splits.clear();
      splits.add(Duration.getInstance(24, TimeUnit.HOURS));
      splits.add(Duration.getInstance(40, TimeUnit.HOURS));
      splits.add(Duration.getInstance(96, TimeUnit.HOURS));            
      assertEquals(splits, task.getSplits());

      task = mpp.getTaskByUniqueID(Integer.valueOf(5));
      splits.clear();
      splits.add(Duration.getInstance(72, TimeUnit.HOURS));
      splits.add(Duration.getInstance(88, TimeUnit.HOURS));
      splits.add(Duration.getInstance(176, TimeUnit.HOURS));                  
      assertEquals(splits, task.getSplits());
   }   
   
}
