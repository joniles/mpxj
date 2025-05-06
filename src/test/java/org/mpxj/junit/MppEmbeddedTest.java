/*
 * file:        MppEmbeddedTest.java
 * author:      Jon Iles
 * copyright:   (c) Packwood Software 2008
 * date:        15/03/2008
 */

/*
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 */

package org.mpxj.junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Test;

import org.mpxj.ProjectFile;
import org.mpxj.RtfNotes;
import org.mpxj.Task;
import org.mpxj.mpp.MPPReader;
import org.mpxj.mpp.RTFEmbeddedObject;

/**
 * Test to handle MPP file content embedded in note fields.
 */
public class MppEmbeddedTest
{
   /**
    * Test MPP9 file.
    */
   @Test public void testMpp9Embedded() throws Exception
   {
      MPPReader reader = new MPPReader();
      ProjectFile mpp = reader.read(MpxjTestData.filePath("mpp9embedded.mpp"));
      testEmbeddedObjects(mpp);
   }

   /**
    * Test MPP9 file saved by Project 2007.
    */
   @Test public void testMpp9From12Embedded() throws Exception
   {
      MPPReader reader = new MPPReader();
      ProjectFile mpp = reader.read(MpxjTestData.filePath("mpp9embedded-from12.mpp"));
      testEmbeddedObjects(mpp);
   }

   /**
    * Test MPP9 file saved by Project 2010.
    */
   @Test public void testMpp9From14Embedded() throws Exception
   {
      MPPReader reader = new MPPReader();
      ProjectFile mpp = reader.read(MpxjTestData.filePath("mpp9embedded-from14.mpp"));
      testEmbeddedObjects(mpp);
   }

   /**
    * Test MPP12 file.
    */
   @Test public void testMpp12Embedded() throws Exception
   {
      MPPReader reader = new MPPReader();
      ProjectFile mpp = reader.read(MpxjTestData.filePath("mpp12embedded.mpp"));
      testEmbeddedObjects(mpp);
   }

   /**
    * Test MPP12 file saved by Project 2010.
    */
   @Test public void testMpp12From14Embedded() throws Exception
   {
      MPPReader reader = new MPPReader();
      ProjectFile mpp = reader.read(MpxjTestData.filePath("mpp12embedded-from14.mpp"));
      testEmbeddedObjects(mpp);
   }

   /**
    * Test MPP14 file.
    */
   @Test public void testMpp14Embedded() throws Exception
   {
      MPPReader reader = new MPPReader();
      ProjectFile mpp = reader.read(MpxjTestData.filePath("mpp14embedded.mpp"));
      testEmbeddedObjects(mpp);
   }

   /**
    * Tests common to all file types.
    *
    * @param file project file
    */
   private void testEmbeddedObjects(ProjectFile file)
   {
      Task task = file.getTaskByID(Integer.valueOf(1));
      assertEquals("Task 1", task.getName());
      RtfNotes notes = (RtfNotes) task.getNotesObject();
      assertNotNull(notes);
      List<List<RTFEmbeddedObject>> list = RTFEmbeddedObject.getEmbeddedObjects(notes);
      assertNull(list);

      task = file.getTaskByID(Integer.valueOf(2));
      assertEquals("Task 2", task.getName());
      notes = (RtfNotes) task.getNotesObject();
      assertNotNull(notes);
      list = RTFEmbeddedObject.getEmbeddedObjects(notes);
      assertNotNull(list);
      assertEquals(1, list.size());
      List<RTFEmbeddedObject> objectList = list.get(0);
      assertEquals(4, objectList.size());
      assertEquals("Package", new String(objectList.get(0).getData(), 0, 7));
      assertEquals("METAFILEPICT", new String(objectList.get(2).getData(), 0, 12));

      task = file.getTaskByID(Integer.valueOf(3));
      assertEquals("Task 3", task.getName());
      notes = (RtfNotes) task.getNotesObject();
      assertNotNull(notes);
      list = RTFEmbeddedObject.getEmbeddedObjects(notes);
      assertNotNull(list);
      assertEquals(1, list.size());
      objectList = list.get(0);
      assertEquals(4, objectList.size());
      assertEquals("Package", new String(objectList.get(0).getData(), 0, 7));
      assertEquals("METAFILEPICT", new String(objectList.get(2).getData(), 0, 12));

      task = file.getTaskByID(Integer.valueOf(4));
      assertEquals("Task 4", task.getName());
      notes = (RtfNotes) task.getNotesObject();
      assertNotNull(notes);
      list = RTFEmbeddedObject.getEmbeddedObjects(notes);
      assertNotNull(list);
      assertEquals(1, list.size());
      objectList = list.get(0);
      assertEquals(4, objectList.size());
      assertEquals("Package", new String(objectList.get(0).getData(), 0, 7));
      assertEquals("METAFILEPICT", new String(objectList.get(2).getData(), 0, 12));

      task = file.getTaskByID(Integer.valueOf(5));
      assertEquals("Task 5", task.getName());
      notes = (RtfNotes) task.getNotesObject();
      assertNotNull(notes);
      list = RTFEmbeddedObject.getEmbeddedObjects(notes);
      assertNotNull(list);
      assertEquals(1, list.size());
      objectList = list.get(0);
      assertEquals(4, objectList.size());
      assertEquals("Package", new String(objectList.get(0).getData(), 0, 7));
      assertEquals("METAFILEPICT", new String(objectList.get(2).getData(), 0, 12));

      task = file.getTaskByID(Integer.valueOf(6));
      assertEquals("Task 6", task.getName());
      notes = (RtfNotes) task.getNotesObject();
      assertNotNull(notes);
      list = RTFEmbeddedObject.getEmbeddedObjects(notes);
      assertNotNull(list);
      assertEquals(2, list.size());
      objectList = list.get(0);
      assertEquals(4, objectList.size());
      assertEquals("Package", new String(objectList.get(0).getData(), 0, 7));
      assertEquals("METAFILEPICT", new String(objectList.get(2).getData(), 0, 12));
      objectList = list.get(1);
      assertEquals(4, objectList.size());
      assertEquals("Package", new String(objectList.get(0).getData(), 0, 7));
      assertEquals("METAFILEPICT", new String(objectList.get(2).getData(), 0, 12));

      task = file.getTaskByID(Integer.valueOf(7));
      assertEquals("Task 7", task.getName());
      notes = (RtfNotes) task.getNotesObject();
      assertNotNull(notes);
      list = RTFEmbeddedObject.getEmbeddedObjects(notes);
      assertNotNull(list);
      assertEquals(2, list.size());
      objectList = list.get(0);
      assertEquals(4, objectList.size());
      assertEquals("Package", new String(objectList.get(0).getData(), 0, 7));
      assertEquals("METAFILEPICT", new String(objectList.get(2).getData(), 0, 12));
      objectList = list.get(1);
      assertEquals(4, objectList.size());
      assertEquals("Package", new String(objectList.get(0).getData(), 0, 7));
      assertEquals("METAFILEPICT", new String(objectList.get(2).getData(), 0, 12));
   }
}
