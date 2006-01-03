/*
 * file:       MPXReader.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2005
 * date:       Jan 3, 2006
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

package com.tapsterrock.mpx;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;

/**
 * This class creates a new MPXFile instance by reading an MPX file.
 */
public final class MPXReader extends AbstractProjectReader
{
   /**
    * {@inheritDoc}
    */
   public ProjectFile read (InputStream is)
      throws MPXException
   {
      int line = 1;
   
      try
      {
         //
         // Test the header and extract the separator. If this is successful,
         // we reset the stream back as far as we can. The design of the
         // BufferedInputStream class means that we can't get back to character
         // zero, so the first record we will read will get "PX" rather than
         // "MPX" in the first field position.
         //
         BufferedInputStream bis = new BufferedInputStream(is);
         byte[] data = new byte[4];
         data[0] = (byte)bis.read();
         bis.mark(1024);
         data[1] = (byte)bis.read();
         data[2] = (byte)bis.read();
         data[3] = (byte)bis.read();
   
         if ((data[0] != 'M') || (data[1] != 'P') || (data[2] != 'X'))
         {
            throw new MPXException(MPXException.INVALID_FILE);
         }
   
         ProjectFile projectFile = new ProjectFile ();
         projectFile.setLocale(m_locale);
         projectFile.setDelimiter((char)data[3]);
   
         bis.reset();
   
         //
         // Read the file creation record. At this point we are reading
         // directly from an input stream so no character set decoding is
         // taking place. We assume that any text in this record will not
         // require decoding.
         //
         Tokenizer tk = new InputStreamTokenizer(bis);
         tk.setDelimiter(projectFile.getDelimiter());
   
         Record record;
         String number;
   
         //
         // Add the header record
         //
         projectFile.add(Integer.toString(FileCreationRecord.RECORD_NUMBER), new Record(projectFile, tk));
         ++line;
   
         //
         // Now process the remainder of the file in full. As we have read the
         // file creation record we have access to the field which specifies the
         // codepage used to encode the character set in this file. We set up
         // an input stream reader using the appropriate character set, and
         // create a new tokenizer to read from this Reader instance.
         //
         InputStreamReader reader = new InputStreamReader(bis, projectFile.getFileCreationRecord().getCodePage().getCharset());
         tk = new ReaderTokenizer(reader);
         tk.setDelimiter(projectFile.getDelimiter());
   
         //
         // Read the remainder of the records
         //
         while (tk.getType() != Tokenizer.TT_EOF)
         {
            record = new Record(projectFile, tk);
            number = record.getRecordNumber();
   
            if (number != null)
            {
               projectFile.add(number, record);
            }
            
            ++line;
         }
   
         //
         // Ensure that all tasks and resources have valid Unique IDs
         //
         projectFile.updateUniqueIdentifiers();
         
         //
         // Ensure that the structure is consistent
         //
         projectFile.updateStructure();
         
         //
         // Ensure that the unique ID counters are correct
         //
         projectFile.updateUniqueCounters();
         
         return (projectFile);
      }
   
      catch (Exception ex)
      {
         throw new MPXException(MPXException.READ_ERROR + " (failed at line " + line + ")", ex);
      }
   }

   
   /**
    * This method returns the locale used by this MPX file.
    *
    * @return current locale
    */
   public Locale getLocale ()
   {
      return (m_locale);
   }

   /**
    * This method sets the locale to be used by this MPX file.
    *
    * @param locale locale to be used
    */
   public void setLocale (Locale locale)
   {
      m_locale = locale;
   }

   /**
    * Locale used for this MPX file. Defaults to English.
    */
   private Locale m_locale = Locale.ENGLISH;   
}
