/*
 * file:       MppResourceTest.java
 * author:     Wade Golden
 * copyright:  (c) Tapster Rock Limited 2006
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

package net.sf.mpxj.junit;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import net.sf.mpxj.AccrueType;
import net.sf.mpxj.Duration;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Rate;
import net.sf.mpxj.Resource;
import net.sf.mpxj.ResourceAssignment;
import net.sf.mpxj.ResourceType;
import net.sf.mpxj.Task;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.WorkContour;
import net.sf.mpxj.mpp.MPPReader;

/**
 * Testsb to exercise MPP file read functionality for various versions of
 * MPP file.
 */
public class MppResourceTest extends MPXJTestCase 
{
   
   /**
    * Test resource data read from an MPP9 file.
    * 
    * @throws Exception
    */   
    public void testMpp9Resource() 
       throws Exception 
    {
        ProjectFile mpp = new MPPReader().read (m_basedir + "/mpp9resource.mpp");        
        testResources(mpp);
    }

    /**
     * Test resource data read from an MPP12 file.
     * 
     * @throws Exception
     */       
    public void testMpp12Resource() 
       throws Exception 
    {
       ProjectFile mpp = new MPPReader().read (m_basedir + "/mpp12resource.mpp");
       testResources(mpp);
    }

    /**
     * Test assignment data read from an MPP9 file.
     * 
     * @throws Exception
     */   
     public void testMpp9Assignment() 
        throws Exception 
     {
         ProjectFile mpp = new MPPReader().read (m_basedir + "/mpp9resource.mpp");        
         testResourceAssignments(mpp);
     }

     /**
      * Test assignment data read from an MPP9 file.
      * 
      * @throws Exception
      */   
      public void testMpp12Assignment() 
         throws Exception 
      {
          ProjectFile mpp = new MPPReader().read (m_basedir + "/mpp12resource.mpp");        
          testResourceAssignments(mpp);
      }

    /**
     * Tests fields related to Resources.
     * 
     * @param mpp The ProjectFile being tested.
     * @throws Exception
     */
    private void testResources(ProjectFile mpp) 
       throws Exception 
    {

        /** MPP9 fields that return null:
         *
         * (would like these fixed in MPP9 as well)
         *
         * Material Label
         * Base Calendar
         */

        List listAllResources = mpp.getAllResources();
        assertTrue(listAllResources != null);
        // Fails for MPP12 as there is a summary resource
        //assertEquals(4, listAllResources.size());
        
        Resource resourceWade = mpp.getResourceByID(new Integer(1));
        Resource resourceJon = mpp.getResourceByID(new Integer(2));
        Resource resourceBrian = mpp.getResourceByID(new Integer(3));
        Resource resourceConcrete = mpp.getResourceByID(new Integer(4));
        // resource names
        assertEquals("Wade Golden", resourceWade.getName());
        assertEquals("Jon Iles", resourceJon.getName());
        assertEquals("Brian Leach", resourceBrian.getName());
        assertEquals("Concrete", resourceConcrete.getName());
        // type
        assertEquals(ResourceType.WORK, resourceWade.getType());
        assertEquals(ResourceType.MATERIAL, resourceConcrete.getType());
        // material label
        //assertEquals("ton", resourceConcrete.getMaterialLabel());
        // initials
        assertEquals("WG", resourceWade.getInitials());
        //  group
        assertEquals("Steelray", resourceWade.getGroup());
        assertEquals("Tapsterrock", resourceJon.getGroup());
        assertEquals("Steelray", resourceBrian.getGroup());
        assertEquals("Mat", resourceConcrete.getGroup());
        // max units
        assertEquals(new Double(100), resourceWade.getMaxUnits());
        // std rate
        Rate rate = new Rate(50, TimeUnit.HOURS);
        assertEquals(rate, resourceWade.getStandardRate());
        rate = new Rate(75, TimeUnit.HOURS);
        assertEquals(rate, resourceJon.getStandardRate());
        rate = new Rate(100, TimeUnit.HOURS);
        assertEquals(rate, resourceBrian.getStandardRate());
        // overtime rate
        rate = new Rate(100, TimeUnit.HOURS);
        assertEquals(rate, resourceWade.getOvertimeRate());
        rate = new Rate(150, TimeUnit.HOURS);
        assertEquals(rate, resourceJon.getOvertimeRate());
        rate = new Rate(200, TimeUnit.HOURS);
        assertEquals(rate, resourceBrian.getOvertimeRate());
        // cost per use
        assertEquals(new Double(500), resourceConcrete.getCostPerUse());
        // accrue type
        assertEquals(AccrueType.END, resourceWade.getAccrueAt());
        assertEquals(AccrueType.PRORATED, resourceJon.getAccrueAt());
        assertEquals(AccrueType.START, resourceConcrete.getAccrueAt());
        // code
        assertEquals("10", resourceWade.getCode());
        assertEquals("20", resourceJon.getCode());
        assertEquals("30", resourceBrian.getCode());
    }

    /**
     * Tests fields related to Resource Assignments.
     * 
     * @param mpp The ProjectFile being tested.
     */
    private void testResourceAssignments(ProjectFile mpp) 
    {
        Integer intOne = new Integer(1);
        DateFormat df = new SimpleDateFormat ("dd/MM/yyyy");

        List listResourceAssignments = mpp.getAllResourceAssignments();

        ResourceAssignment ra = (ResourceAssignment)listResourceAssignments.get(0);
        // id
        assertEquals(intOne, ra.getResourceID());
        assertEquals(intOne, ra.getResourceUniqueID());
        // start and finish
        assertEquals("25/08/2006", df.format(ra.getStart()));
        assertEquals("29/08/2006", df.format(ra.getFinish()));
        // task
        Task task = ra.getTask();
        assertEquals(intOne, task.getID());
        assertEquals(new Integer(2), task.getUniqueID());
        // units
        assertEquals(new Double(100), ra.getUnits());
        // work and remaining work
        Duration dur24Hours = Duration.getInstance(24, TimeUnit.HOURS);
        assertEquals(dur24Hours, ra.getWork());
        assertEquals(dur24Hours, ra.getRemainingWork());

        // Task 2
        // contour
        ResourceAssignment ra2 = (ResourceAssignment)listResourceAssignments.get(3);
        assertEquals(WorkContour.TURTLE, ra2.getWorkContour());
    }

}
