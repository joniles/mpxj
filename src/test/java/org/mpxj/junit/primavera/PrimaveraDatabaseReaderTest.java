/*
 * file:       PrimaveraDatabaseReaderTest.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2015
 * date:       08/05/2015
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

package org.mpxj.junit.primavera;

import static org.junit.Assert.*;
import org.mpxj.primavera.PrimaveraDatabaseReader;

import org.junit.Test;

/**
 * Tests for Primavera DatabaseReader functionality.
 */
public class PrimaveraDatabaseReaderTest
{
   /**
    * Ensure the schema attribute is working as expected.
    */
   @Test public void testSchema()
   {
      PrimaveraDatabaseReader reader = new PrimaveraDatabaseReader();
      assertEquals("", reader.getSchema());

      reader.setSchema(null);
      assertEquals("", reader.getSchema());

      reader.setSchema("");
      assertEquals("", reader.getSchema());

      reader.setSchema("test");
      assertEquals("test.", reader.getSchema());

      reader.setSchema("test.");
      assertEquals("test.", reader.getSchema());
   }
}
