/*
 * file:       MspdiContextTest.java
 * author:     Petr Janeček
 * date:       2026-11-09
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

import java.io.ByteArrayOutputStream;
import java.lang.ref.WeakReference;
import java.util.concurrent.Callable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mpxj.ProjectFile;
import org.mpxj.mspdi.MSPDIReader;
import org.mpxj.mspdi.MSPDIWriter;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mpxj.junit.MpxjAssert.assumeJvm;

/**
 * Tests that reading or writing an MSPDI file leaves nothing on the thread which keeps the project in memory.
 */
public class MspdiContextTest
{
   /**
    * Check that we're not in IKVM, as these tests rely on how the JVM's garbage collector treats weak references.
    */
   @BeforeEach public void beforeMethod()
   {
      assumeJvm();
   }

   /**
    * Test that a project read from an MSPDI file can be garbage collected once the caller no longer uses it.
    */
   @Test public void testReadProjectIsReleased() throws Exception
   {
      assertReleased(() -> new MSPDIReader().read(MpxjTestData.filePath("mspdiavailability.xml")));
   }

   /**
    * Test that a project written as an MSPDI file can be garbage collected once the caller no longer uses it.
    */
   @Test public void testWrittenProjectIsReleased() throws Exception
   {
      assertReleased(() -> {
         ProjectFile file = new ProjectFile();
         new MSPDIWriter().write(file, new ByteArrayOutputStream());
         return file;
      });
   }

   /**
    * Check that nothing keeps the project returned by the action reachable once the action is complete.
    *
    * @param action reads or writes a project, then returns it
    */
   private static void assertReleased(Callable<ProjectFile> action) throws Exception
   {
      WeakReference<ProjectFile> reference = new WeakReference<>(action.call());
      for (int attempt = 0; attempt < 10 && reference.get() != null; attempt++)
      {
         System.gc();
      }
      assertNull(reference.get(), "The project is still reachable");
   }
}
