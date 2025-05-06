/*
 * file:       LocaleTest.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2008
 * date:       23/09/2008
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

import static org.mpxj.junit.MpxjAssert.*;

import java.io.File;
import java.nio.file.Files;
import java.util.Locale;

import org.junit.Test;

import org.mpxj.ProjectFile;
import org.mpxj.mpx.MPXReader;
import org.mpxj.mpx.MPXWriter;

/**
 * Tests to exercise MPX locales.
 */
public class LocaleTest
{
   /**
    * Test all supported MPX locales.
    */
   @Test public void testLocales() throws Exception
   {
      assumeJvm();
      Locale[] locales = MPXReader.getSupportedLocales();
      for (Locale locale : locales)
      {
         testLocale(locale);
      }
   }

   /**
    * Test localisation.
    *
    * @param locale locale to test
    */
   private void testLocale(Locale locale) throws Exception
   {
      MPXReader reader = new MPXReader();
      MPXWriter writer = new MPXWriter();

      File in = new File(MpxjTestData.filePath("legacy/sample.mpx"));
      ProjectFile mpx = reader.read(in);
      File out = Files.createTempFile("junit-" + locale.getLanguage(), ".mpx").toFile();
      writer.setLocale(locale);
      writer.write(mpx, out);

      reader.setLocale(locale);
      reader.read(out);
      out.deleteOnExit();
   }

   /**
    * Read a file created by a German version of MS Project 98.
    */
   @Test public void testReadGerman() throws Exception
   {
      File in = new File(MpxjTestData.filePath("sample.de.mpx"));
      MPXReader reader = new MPXReader();
      reader.setLocale(Locale.GERMAN);
      reader.read(in);
   }
}
