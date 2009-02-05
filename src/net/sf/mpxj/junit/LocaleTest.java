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

package net.sf.mpxj.junit;

import java.io.File;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Locale;

import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.mpx.MPXReader;
import net.sf.mpxj.mpx.MPXWriter;

/**
 * Tests to exercise MPX locales.
 */
public class LocaleTest extends MPXJTestCase
{
   /**
    * Test all supported MPX locales.
    *
    * @throws Exception
    */
   public void testLocales() throws Exception
   {
      Locale[] locales = new MPXReader().getSupportedLocales();
      for (Locale locale : locales)
      {
         try
         {
            testLocale(locale);
         }

         catch (UnsupportedCharsetException ex)
         {
            //
            // If we are running under IKVM, we don't
            // have a full range of character sets available
            // so we'll ignore this.
            //
            if (!m_ikvm)
            {
               throw ex;
            }
         }
      }
   }

   /**
    * Test localisation.
    *
    * @param locale locale to test
    * @throws Exception
    */
   private void testLocale(Locale locale) throws Exception
   {
      File out = null;
      boolean success = true;

      try
      {
         MPXReader reader = new MPXReader();
         MPXWriter writer = new MPXWriter();

         File in = new File(m_basedir + "/sample.mpx");
         ProjectFile mpx = reader.read(in);
         out = File.createTempFile("junit-" + locale.getLanguage(), ".mpx");
         writer.setLocale(locale);
         writer.write(mpx, out);

         reader.setLocale(locale);
         reader.read(out);
      }

      finally
      {
         if (out != null && success == true)
         {
            out.delete();
         }
      }
   }

   /**
    * Read a file created by a German version of MS Project 98.
    *
    * @throws Exception
    */
   public void testReadGerman() throws Exception
   {
      File in = new File(m_basedir + "/sample.de.mpx");
      MPXReader reader = new MPXReader();
      reader.setLocale(Locale.GERMAN);
      reader.read(in);
   }
}
