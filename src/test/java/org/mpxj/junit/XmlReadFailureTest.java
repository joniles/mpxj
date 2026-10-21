/*
 * file:       XmlReadFailureTest.java
 * author:     Petr Janeček
 * date:       2026-10-28
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.mpxj.MPXJException;
import org.mpxj.conceptdraw.ConceptDrawProjectReader;
import org.mpxj.mspdi.MSPDIReader;
import org.mpxj.primavera.PrimaveraPMFileReader;
import org.mpxj.reader.ProjectReader;
import org.xml.sax.SAXParseException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests how the readers built on JAXB report a file they cannot read: a parse failure as the
 * parser's own exception, with the line and column, and a failing stream as its own exception.
 */
public class XmlReadFailureTest
{
   /**
    * An MSPDI file which is not well-formed.
    */
   @Test public void testMspdiNotWellFormed()
   {
      assertParseFailure(new MSPDIReader(), text(MSPDI + "<Name>x</Project>"));
   }

   /**
    * A well-formed MSPDI file whose root element is not a project.
    */
   @Test public void testMspdiWrongRootElement()
   {
      assertParseFailure(new MSPDIReader(), text("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<Task xmlns=\"http://schemas.microsoft.com/project\"></Task>"));
   }

   /**
    * A PMXML file which ends part way through.
    */
   @Test public void testPmxmlTruncated()
   {
      assertParseFailure(new PrimaveraPMFileReader(), text(PMXML + "<Project>"));
   }

   /**
    * A ConceptDraw file holding a byte which is not valid UTF-8.
    */
   @Test public void testConceptDrawInvalidUtf8()
   {
      InputStream invalid = new ByteArrayInputStream(new byte[] { (byte) 0xFF });
      assertParseFailure(new ConceptDrawProjectReader(), new SequenceInputStream(text("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<Document>\n<Name>"), invalid));
   }

   /**
    * An MSPDI stream which fails part way through.
    */
   @Test public void testMspdiFailingStream()
   {
      InputStream failing = new InputStream()
      {
         @Override public int read() throws IOException
         {
            throw new IOException("Stream failed");
         }
      };

      MPXJException ex = assertThrows(MPXJException.class, () -> new MSPDIReader().read(new SequenceInputStream(text(MSPDI + "<Name>x</Name>"), failing)));
      assertInstanceOf(IOException.class, ex.getCause());
      assertEquals("Stream failed", ex.getCause().getMessage());
   }

   /**
    * Check that reading fails with the parser's own exception, which gives the line and column.
    *
    * @param reader reader to use
    * @param stream file content
    */
   private static void assertParseFailure(ProjectReader reader, InputStream stream)
   {
      MPXJException ex = assertThrows(MPXJException.class, () -> reader.read(stream));
      SAXParseException cause = assertInstanceOf(SAXParseException.class, ex.getCause());
      assertTrue(cause.getLineNumber() > 0);
   }

   private static InputStream text(String text)
   {
      return new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
   }

   private static final String MSPDI = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<Project xmlns=\"http://schemas.microsoft.com/project\">\n";

   // The PMXML reader looks at the first 512 bytes before parsing, so the comment pads the file past them.
   // The reader supplies the namespace itself.
   private static final String PMXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!--" + new String(new char[600]).replace('\0', ' ') + "-->\n<APIBusinessObjects>\n";
}
