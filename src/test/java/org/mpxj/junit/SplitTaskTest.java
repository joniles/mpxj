/*
 * file:       SplitTaskTest.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2005
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

package org.mpxj.junit;

import static org.junit.Assert.*;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.mpxj.LocalDateTimeRange;
import org.mpxj.ProjectFile;
import org.mpxj.Task;
import org.mpxj.mpp.MPPReader;

import org.junit.Test;

/**
 * The tests contained in this class exercise the split task functionality.
 */
public class SplitTaskTest
{
   /**
    * Exercise split task functionality.
    */
   @Test public void testSplits1() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("splits9a.mpp"));

      Task task = mpp.getTaskByUniqueID(Integer.valueOf(1));
      assertNull(task.getSplits());

      List<LocalDateTimeRange> taskSplits;
      for (int taskID = 2; taskID <= 6; taskID++)
      {
         task = mpp.getTaskByUniqueID(Integer.valueOf(taskID));
         taskSplits = task.getSplits();
         assertEquals(3, taskSplits.size());
         testSplit(taskSplits.get(0), "06/01/2006 08:00", "11/01/2006 17:00");
         //testSplit(taskSplits.get(1), "12/01/2006 08:00", "12/01/2006 17:00");
         testSplit(taskSplits.get(2), "13/01/2006 08:00", "20/01/2006 17:00");
      }

      for (int taskID = 7; taskID <= 13; taskID++)
      {
         task = mpp.getTaskByUniqueID(Integer.valueOf(taskID));
         taskSplits = task.getSplits();
         assertEquals(5, taskSplits.size());
         testSplit(taskSplits.get(0), "06/01/2006 08:00", "09/01/2006 17:00");
         //testSplit(taskSplits.get(1), "10/01/2006 08:00", "13/01/2006 17:00");
         testSplit(taskSplits.get(2), "16/01/2006 08:00", "20/01/2006 17:00");
         //testSplit(taskSplits.get(3), "23/01/2006 08:00", "24/01/2006 17:00");
         testSplit(taskSplits.get(4), "25/01/2006 08:00", "27/01/2006 17:00");
      }
   }

   /**
    * Exercise split task functionality.
    */
   @Test public void testSplits2() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("splits9b.mpp"));
      List<LocalDateTimeRange> taskSplits;
      Task task = mpp.getTaskByUniqueID(Integer.valueOf(1));
      taskSplits = task.getSplits();
      assertEquals(5, taskSplits.size());
      testSplit(taskSplits.get(0), "26/08/2005 08:00", "29/08/2005 17:00");
      testSplit(taskSplits.get(1), "30/08/2005 08:00", "01/09/2005 17:00");
      testSplit(taskSplits.get(2), "02/09/2005 08:00", "06/09/2005 17:00");
      testSplit(taskSplits.get(3), "07/09/2005 08:00", "09/09/2005 17:00");
      testSplit(taskSplits.get(4), "12/09/2005 08:00", "16/09/2005 17:00");

      task = mpp.getTaskByUniqueID(Integer.valueOf(3));
      assertNull(task.getSplits());

      task = mpp.getTaskByUniqueID(Integer.valueOf(4));
      taskSplits = task.getSplits();
      assertEquals(3, taskSplits.size());
      testSplit(taskSplits.get(0), "29/08/2005 08:00", "31/08/2005 17:00");
      testSplit(taskSplits.get(1), "01/09/2005 08:00", "02/09/2005 17:00");
      testSplit(taskSplits.get(2), "05/09/2005 08:00", "13/09/2005 17:00");

      task = mpp.getTaskByUniqueID(Integer.valueOf(5));
      taskSplits = task.getSplits();
      assertEquals(3, taskSplits.size());
      testSplit(taskSplits.get(0), "26/08/2005 08:00", "07/09/2005 17:00");
      testSplit(taskSplits.get(1), "08/09/2005 08:00", "09/09/2005 17:00");
      testSplit(taskSplits.get(2), "12/09/2005 08:00", "26/09/2005 17:00");
   }

   /**
    * Utility method to test a split task date range.
    *
    * @param range DateRange instance
    * @param start expected start date
    * @param end expected end date
    */
   private void testSplit(LocalDateTimeRange range, String start, String end)
   {
      assertEquals(start, m_df.format(range.getStart()));
      assertEquals(end, m_df.format(range.getEnd()));
   }

   private final DateTimeFormatter m_df = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
}
