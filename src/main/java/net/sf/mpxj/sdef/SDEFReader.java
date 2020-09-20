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

package net.sf.mpxj.sdef;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.mpxj.CustomFieldContainer;
import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.TaskField;
import net.sf.mpxj.reader.AbstractProjectStreamReader;

/**
 * Read the contents of an SDEF file.
 */
public final class SDEFReader extends AbstractProjectStreamReader
{
   /**
    * {@inheritDoc}
    */
   @Override public ProjectFile read(InputStream inputStream) throws MPXJException
   {
      Context context = new Context();
      ProjectFile project = context.getProject();

      CustomFieldContainer fields = project.getCustomFields();
      fields.getCustomField(TaskField.TEXT1).setAlias("Activity ID");
      fields.getCustomField(TaskField.TEXT2).setAlias("Hammock Code");
      fields.getCustomField(TaskField.NUMBER1).setAlias("Workers Per Day");
      fields.getCustomField(TaskField.TEXT3).setAlias("Responsibility Code");
      fields.getCustomField(TaskField.TEXT4).setAlias("Work Area Code");
      fields.getCustomField(TaskField.TEXT5).setAlias("Mod or Claim No");
      fields.getCustomField(TaskField.TEXT6).setAlias("Bid Item");
      fields.getCustomField(TaskField.TEXT7).setAlias("Phase of Work");
      fields.getCustomField(TaskField.TEXT8).setAlias("Category of Work");
      fields.getCustomField(TaskField.TEXT9).setAlias("Feature of Work");
      fields.getCustomField(TaskField.COST1).setAlias("Stored Material");

      project.getProjectProperties().setFileApplication("SDEF");
      project.getProjectProperties().setFileType("SDEF");

      addListenersToProject(project);

      BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

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

      return project;
   }

   /**
    * {@inheritDoc}
    */
   @Override public List<ProjectFile> readAll(InputStream inputStream) throws MPXJException
   {
      return Arrays.asList(read(inputStream));
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

      record.read(line);

      record.process(context);

      return true;
   }

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
