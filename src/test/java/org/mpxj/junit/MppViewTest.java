/*
 * file:       MppViewTest.java
 * author:     Wade Golden
 * copyright:  (c) Packwood Software 2006
 * date:       19-September-2006
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

import java.util.HashSet;
import java.util.List;

import org.mpxj.Column;
import org.mpxj.ProjectFile;
import org.mpxj.Table;
import org.mpxj.View;
import org.mpxj.ViewType;
import org.mpxj.mpp.GanttChartView;
import org.mpxj.mpp.MPPReader;

import org.junit.Test;

/**
 * Tests to exercise MPP file read functionality for various versions of
 * MPP file.
 */
public class MppViewTest
{

   /**
    * Test view data read from an MPP9 file.
    */
   @Test public void testMpp9View() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp9resource.mpp"));
      testViews(mpp);
   }

   /**
    * Test view data read from an MPP9 file saved by Project 2007.
    */
   @Test public void testMpp9ViewFrom12()
   {
      //ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp9resource-from12.mpp"));
      //testViews(mpp);
   }

   /**
    * Test view data read from an MPP9 file saved by Project 2010.
    */
   @Test public void testMpp9ViewFrom14()
   {
      //ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp9resource-from14.mpp"));
      //testViews(mpp);
   }

   /**
    * Test view data read from an MPP12 file.
    */
   @Test public void testMpp12View() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp12resource.mpp"));
      testViews(mpp);
   }

   /**
    * Test view data read from an MPP12 file saved by Project 2010.
    */
   @Test public void testMpp12ViewFrom14() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp12resource-from14.mpp"));
      testViews(mpp);
   }

   /**
    * Test view data read from an MPP14 file.
    */
   @Test public void testMpp14View() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp14resource.mpp"));
      testViews(mpp);
   }

   /**
    * Tests MPP Views. Not an in-depth test, but covers the basics of
    * Views, like View Names, Column Names, and Column Widths
    *
    * @param mpp The ProjectFile being tested.
    */
   private void testViews(ProjectFile mpp)
   {
      List<View> views = mpp.getViews();

      // not sure what order MPP12 will store the views in,
      // so make a Set to check against when done reading in the views
      HashSet<String> setViewNames = new HashSet<>();

      for (View view : views)
      {
         // View Names
         String viewName = view.getName();
         setViewNames.add(viewName);
         Table table;

         if (view instanceof GanttChartView)
         {
            GanttChartView view9 = (GanttChartView) view;
            if (null != view9.getTable())
            {
               table = view9.getTable();

               ViewType viewType = view.getType();
               assertEquals(ViewType.GANTT_CHART, viewType);

               // verify all columns
               List<Column> cols = table.getColumns();
               HashSet<String> setColumnNames = new HashSet<>();
               for (Column col : cols)
               {
                  setColumnNames.add(col.getTitle());
                  int width = col.getWidth();
                  assertTrue(width > 0);
               }

               //assertEquals(7, setColumnNames.size());
               assertTrue(setColumnNames.contains("ID"));
               assertTrue(setColumnNames.contains("Unique ID"));
               assertTrue(setColumnNames.contains("Task Name"));
               assertTrue(setColumnNames.contains("Indicators"));
               assertTrue(setColumnNames.contains("Start"));
               assertTrue(setColumnNames.contains("Finish"));
               assertTrue(setColumnNames.contains("Resource Names"));

               assertTrue(view9.getTableWidth() > 0);
            }
         }
      }

      //assertEquals(7, setViewNames.size());
      assertTrue(setViewNames.contains("Gantt Chart"));
      assertTrue(setViewNames.contains("Test View"));
      assertTrue(setViewNames.contains("Tracking Gantt"));
      assertTrue(setViewNames.contains("Resource Sheet"));
      assertTrue(setViewNames.contains("Resource Usage"));
      assertTrue(setViewNames.contains("Task Usage"));
      //assertTrue(setViewNames.contains("")); // why blank?
   }
}
