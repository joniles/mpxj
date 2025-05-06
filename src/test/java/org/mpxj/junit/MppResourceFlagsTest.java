
package org.mpxj.junit;

import static org.mpxj.junit.MpxjAssert.*;
import static org.junit.Assert.*;
import org.mpxj.ProjectFile;
import org.mpxj.Resource;
import org.mpxj.ResourceType;
import org.mpxj.mpp.MPPReader;

import org.junit.Test;

/**
 * Tests reading resource field bit flags from MPP files.
 */
public class MppResourceFlagsTest
{
   /**
    * Test MPP14 saved by Project 2010.
    */
   @Test public void testMpp14FromProject2010() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("resourceFlags-mpp14Project2010.mpp"));
      testFlags(mpp);
   }

   /**
    * Test MPP14 saved by Project 2013.
    */
   @Test public void testMpp14FromProject2013()
   {
      // TODO work in progress - fix reading from Project 2013 MPP14
      //ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("resourceFlags-mpp14Project2013.mpp");
      //testFlags(mpp);
   }

   /**
    * Common code to test flag values.
    *
    * @param mpp project file to test
    */
   private void testFlags(ProjectFile mpp)
   {
      Resource resource;

      //
      // Type
      //
      resource = mpp.getResourceByUniqueID(Integer.valueOf(1));
      assertEquals("Work 1", resource.getName());
      assertEquals(ResourceType.WORK, resource.getType());

      resource = mpp.getResourceByUniqueID(Integer.valueOf(2));
      assertEquals("Material 1", resource.getName());
      assertEquals(ResourceType.MATERIAL, resource.getType());

      resource = mpp.getResourceByUniqueID(Integer.valueOf(30));
      assertEquals("Cost 1", resource.getName());
      assertEquals(ResourceType.COST, resource.getType());

      resource = mpp.getResourceByUniqueID(Integer.valueOf(4));
      assertEquals("Material 2", resource.getName());
      assertEquals(ResourceType.MATERIAL, resource.getType());

      resource = mpp.getResourceByUniqueID(Integer.valueOf(31));
      assertEquals("Cost 2", resource.getName());
      assertEquals(ResourceType.COST, resource.getType());

      //
      // Budget
      //
      resource = mpp.getResourceByUniqueID(Integer.valueOf(5));
      assertEquals("Budget: No 1", resource.getName());
      assertFalse(resource.getBudget());

      resource = mpp.getResourceByUniqueID(Integer.valueOf(6));
      assertEquals("Budget: Yes 1", resource.getName());
      assertTrue(resource.getBudget());

      resource = mpp.getResourceByUniqueID(Integer.valueOf(7));
      assertEquals("Budget: No 2", resource.getName());
      assertFalse(resource.getBudget());

      resource = mpp.getResourceByUniqueID(Integer.valueOf(8));
      assertEquals("Budget: Yes 2", resource.getName());
      assertTrue(resource.getBudget());

      //
      // Flags
      //
      resource = mpp.getResourceByUniqueID(Integer.valueOf(9));
      assertEquals("Flag1", resource.getName());
      testFlag(resource, 1);

      resource = mpp.getResourceByUniqueID(Integer.valueOf(10));
      assertEquals("Flag2", resource.getName());
      testFlag(resource, 2);

      resource = mpp.getResourceByUniqueID(Integer.valueOf(11));
      assertEquals("Flag3", resource.getName());
      testFlag(resource, 3);

      resource = mpp.getResourceByUniqueID(Integer.valueOf(12));
      assertEquals("Flag4", resource.getName());
      testFlag(resource, 4);

      resource = mpp.getResourceByUniqueID(Integer.valueOf(13));
      assertEquals("Flag5", resource.getName());
      testFlag(resource, 5);

      resource = mpp.getResourceByUniqueID(Integer.valueOf(14));
      assertEquals("Flag6", resource.getName());
      testFlag(resource, 6);

      resource = mpp.getResourceByUniqueID(Integer.valueOf(15));
      assertEquals("Flag7", resource.getName());
      testFlag(resource, 7);

      resource = mpp.getResourceByUniqueID(Integer.valueOf(16));
      assertEquals("Flag8", resource.getName());
      testFlag(resource, 8);

      resource = mpp.getResourceByUniqueID(Integer.valueOf(17));
      assertEquals("Flag9", resource.getName());
      testFlag(resource, 9);

      resource = mpp.getResourceByUniqueID(Integer.valueOf(18));
      assertEquals("Flag10", resource.getName());
      testFlag(resource, 10);

      resource = mpp.getResourceByUniqueID(Integer.valueOf(19));
      assertEquals("Flag11", resource.getName());
      testFlag(resource, 11);

      resource = mpp.getResourceByUniqueID(Integer.valueOf(20));
      assertEquals("Flag12", resource.getName());
      testFlag(resource, 12);

      resource = mpp.getResourceByUniqueID(Integer.valueOf(21));
      assertEquals("Flag13", resource.getName());
      testFlag(resource, 13);

      resource = mpp.getResourceByUniqueID(Integer.valueOf(22));
      assertEquals("Flag14", resource.getName());
      testFlag(resource, 14);

      resource = mpp.getResourceByUniqueID(Integer.valueOf(23));
      assertEquals("Flag15", resource.getName());
      testFlag(resource, 15);

      resource = mpp.getResourceByUniqueID(Integer.valueOf(24));
      assertEquals("Flag16", resource.getName());
      testFlag(resource, 16);

      resource = mpp.getResourceByUniqueID(Integer.valueOf(25));
      assertEquals("Flag17", resource.getName());
      testFlag(resource, 17);

      resource = mpp.getResourceByUniqueID(Integer.valueOf(26));
      assertEquals("Flag18", resource.getName());
      testFlag(resource, 18);

      resource = mpp.getResourceByUniqueID(Integer.valueOf(27));
      assertEquals("Flag19", resource.getName());
      testFlag(resource, 19);

      resource = mpp.getResourceByUniqueID(Integer.valueOf(28));
      assertEquals("Flag20", resource.getName());
      testFlag(resource, 20);
   }

   /**
    * Test all 20 custom field flags.
    *
    * @param resource resource to be tested
    * @param flag flag index to test
    */
   private void testFlag(Resource resource, int flag)
   {
      for (int loop = 0; loop < 20; loop++)
      {
         assertBooleanEquals("Flag" + (loop + 1), (flag == loop + 1), resource.getFlag(loop + 1));
      }
   }
}