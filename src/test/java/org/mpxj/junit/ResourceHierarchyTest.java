/*
 * file:       ResourceHierarchyTest.java
 * author:     Jon Iles
 * date:       2023-07-03
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

import org.mpxj.ProjectFile;
import org.mpxj.Resource;
import org.junit.Assert;
import org.junit.Test;

/**
 * Resource hierarchy tests.
 */
public class ResourceHierarchyTest
{
   /**
    * Resource hierarchy tests.
    */
   @Test public void testResourceHierarchy()
   {
      ProjectFile file = new ProjectFile();

      Resource resource1 = file.addResource();
      resource1.setName("Resource 1");

      Resource resource2 = file.addResource();
      resource2.setName("Resource 2");

      Resource resource3 = resource2.addResource();
      resource3.setName("Resource 3");

      Resource resource4 = file.addResource();
      resource4.setName("Resource 4");

      Assert.assertEquals(4, file.getResources().size());
      Assert.assertEquals(3, file.getChildResources().size());
      Assert.assertEquals(0, resource1.getChildResources().size());
      Assert.assertEquals(1, resource2.getChildResources().size());
      Assert.assertEquals(0, resource3.getChildResources().size());
      Assert.assertEquals(0, resource4.getChildResources().size());

      file.removeResource(resource1);

      Assert.assertEquals(3, file.getResources().size());
      Assert.assertEquals(2, file.getChildResources().size());
      Assert.assertEquals(1, resource2.getChildResources().size());
      Assert.assertEquals(0, resource3.getChildResources().size());
      Assert.assertEquals(0, resource4.getChildResources().size());

      file.removeResource(resource3);

      Assert.assertEquals(2, file.getResources().size());
      Assert.assertEquals(2, file.getChildResources().size());
      Assert.assertEquals(0, resource2.getChildResources().size());
      Assert.assertEquals(0, resource4.getChildResources().size());
   }
}
