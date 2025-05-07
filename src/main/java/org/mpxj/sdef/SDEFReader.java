/*
 * file:       SDEFReader.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2019
 * date:       01/07/2019
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

package org.mpxj.sdef;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.mpxj.HasCharset;
import org.mpxj.MPXJException;
import org.mpxj.ProjectFile;
import org.mpxj.reader.AbstractProjectStreamReader;

/**
 * Read the contents of an SDEF file.
 */
public final class SDEFReader extends AbstractProjectStreamReader implements HasCharset
{
   /**
    * Set the character set used when reading an SDEF file.
    * According to SDEF the spec this should be ASCII,
    * which is the default.
    *
    * @param charset character set to use when reading the file
    */
   @Override public void setCharset(Charset charset)
   {
      m_charset = charset;
   }

   /**
    * Retrieve the character set used when reading an SDEF file.
    *
    * @return character set
    */
   @Override public Charset getCharset()
   {
      return m_charset == null ? StandardCharsets.US_ASCII : m_charset;
   }

   @Override public ProjectFile read(InputStream inputStream) throws MPXJException
   {
      Context context = new Context();
      ProjectFile project = context.getProject();

      project.getProjectProperties().setFileApplication("SDEF");
      project.getProjectProperties().setFileType("SDEF");

      addListenersToProject(project);

      BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, getCharset()));

      try
      {
         while (processLine(context, reader.readLine()))
         {
            // empty block
         }
      }

      catch (IOException ex)
      {
         throw new MPXJException(MPXJException.READ_ERROR, ex);
      }

      project.setDefaultCalendar(project.getCalendars().findOrCreateDefaultCalendar());
      project.readComplete();

      return project;
   }

   /**
    * Set a flag to determine if datatype parse errors can be ignored.
    * Defaults to true.
    *
    * @param ignoreErrors pass true to ignore errors
    */
   public void setIgnoreErrors(boolean ignoreErrors)
   {
      m_ignoreErrors = ignoreErrors;
   }

   /**
    * Retrieve the flag which determines if datatype parse errors can be ignored.
    * Defaults to true.
    *
    * @return true if datatype parse errors are ignored
    */
   public boolean getIgnoreErrors()
   {
      return m_ignoreErrors;
   }

   /**
    * Process a single SDEF  record.
    *
    * @param context current context
    * @param line current record
    * @return false if we have reached the end of the file
    */
   private boolean processLine(Context context, String line) throws MPXJException
   {
      if (line == null || line.startsWith("END"))
      {
         return false;
      }

      String recordID = line.substring(0, 4);
      Class<? extends SDEFRecord> klass = RECORD_MAP.get(recordID);
      if (klass == null)
      {
         throw new MPXJException("Unknown record type: " + recordID);
      }

      SDEFRecord record;
      try
      {
         record = klass.newInstance();
      }

      catch (Exception e)
      {
         throw new MPXJException(MPXJException.READ_ERROR, e);
      }

      record.read(context.getProject(), line, m_ignoreErrors);

      record.process(context);

      return true;
   }

   private Charset m_charset;
   private boolean m_ignoreErrors = true;

   private static final Map<String, Class<? extends SDEFRecord>> RECORD_MAP = new HashMap<>();
   static
   {
      RECORD_MAP.put("VOLM", VolumeRecord.class);
      RECORD_MAP.put("PROJ", ProjectRecord.class);
      RECORD_MAP.put("CLDR", CalendarRecord.class);
      RECORD_MAP.put("HOLI", HolidayRecord.class);
      RECORD_MAP.put("ACTV", ActivityRecord.class);
      RECORD_MAP.put("PRED", PrecedenceRecord.class);
      RECORD_MAP.put("UNIT", UnitCostRecord.class);
      RECORD_MAP.put("PROG", ProgressRecord.class);
   }
}
