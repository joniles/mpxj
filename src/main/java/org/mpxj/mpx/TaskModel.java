/*
 * file:       TaskModel.java
 * author:     Scott Melville
 *             Jon Iles
 * copyright:  (c) Packwood Software 2002-2003
 * date:       15/08/2002
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

package org.mpxj.mpx;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

import org.mpxj.FieldType;
import org.mpxj.MPXJException;
import org.mpxj.ProjectFile;
import org.mpxj.Task;
import org.mpxj.TaskField;
import org.mpxj.mpp.UserDefinedFieldMap;

/**
 * This class represents the task table definition record in an MPX file.
 * This record defines which fields are present in a task record.
 * This record has two forms, one textual and one numeric. Both
 * variants are handled by this class.
 */
final class TaskModel
{
   /**
    * Default constructor.
    *
    * @param file the parent file to which this record belongs.
    * @param locale target locale
    * @param userDefinedFieldMap user defined field map
    */
   TaskModel(ProjectFile file, Locale locale, UserDefinedFieldMap userDefinedFieldMap)
   {
      m_parentFile = file;
      m_userDefinedFieldMap = userDefinedFieldMap;
      setLocale(locale);
   }

   /**
    * This method is used to update the locale specific data used by this class.
    *
    * @param locale target locale
    */
   void setLocale(Locale locale)
   {
      m_taskNames = LocaleData.getStringArray(locale, LocaleData.TASK_NAMES);

      String name;
      m_taskNumbers.clear();

      for (int loop = 0; loop < m_taskNames.length; loop++)
      {
         name = m_taskNames[loop];

         if (name != null)
         {
            m_taskNumbers.put(name, Integer.valueOf(loop));
         }
      }
   }

   /**
    * This method is used to retrieve a linked list of field identifiers
    * indicating the fields present in a task record.
    *
    * @return array of field identifiers
    */
   public int[] getModel()
   {
      m_fields[m_count] = -1;
      return (m_fields);
   }

   /**
    * This method populates the task model from data read from an MPX file.
    *
    * @param record data read from an MPX file
    * @param isText flag indicating whether the textual or numeric data is being supplied
    */
   public void update(Record record, boolean isText) throws MPXJException
   {
      int length = record.getLength();

      for (int i = 0; i < length; i++)
      {
         if (isText)
         {
            add(getTaskCode(record.getString(i)));
         }
         else
         {
            add(record.getInteger(i).intValue());
         }
      }
   }

   /**
    * This method is called from the task class each time an attribute
    * is added, ensuring that all of the attributes present in each task
    * record are present in the resource model.
    *
    * @param field field identifier
    */
   private void add(int field)
   {
      if (field < m_flags.length)
      {
         if (!m_flags[field])
         {
            m_flags[field] = true;
            m_fields[m_count] = field;
            ++m_count;
         }
      }
   }

   /**
    * This method is called to populate the arrays which are then
    * used to generate the text version of the model.
    */
   private void populateModel()
   {
      if (m_count != 0)
      {
         m_count = 0;
         Arrays.fill(m_flags, false);
      }

      for (Task task : m_parentFile.getTasks())
      {
         for (int loop = 0; loop < MPXTaskField.MAX_FIELDS; loop++)
         {
            if (!m_flags[loop] && isFieldPopulated(task, MPXTaskField.getMpxjField(loop)))
            {
               m_flags[loop] = true;
               m_fields[m_count] = loop;
               ++m_count;
            }
         }
      }

      //
      // Ensure the model fields always appear in the same order
      //
      Arrays.sort(m_fields);
      System.arraycopy(m_fields, m_fields.length - m_count, m_fields, 0, m_count);
   }

   /**
    * Determine if a task field contains data.
    *
    * @param task task instance
    * @param field target field
    * @return true if the field contains data
    */
   private boolean isFieldPopulated(Task task, FieldType field)
   {
      boolean result = false;
      if (field != null)
      {
         field = m_userDefinedFieldMap == null ? field : m_userDefinedFieldMap.getSource(field);
         Object value = task.get(field);

         // We never write these fields to the task record.
         // We only write a predecessor entry rather than the successors entry
         // We write notes as a separate record
         if (field == TaskField.NOTES || field == TaskField.SUCCESSORS)
         {
            result = false;
         }
         else
         {
            result = ModelUtility.isFieldPopulated(field, value);
         }
      }
      return result;
   }

   /**
    * This method generates a string in MPX format representing the
    * contents of this record. Both the textual and numeric record
    * types are written by this method.
    *
    * @return string containing the data for this record in MPX format.
    */
   @Override public String toString()
   {
      populateModel();

      int number;
      char delimiter = m_parentFile.getProjectProperties().getMpxDelimiter();

      StringBuilder textual = new StringBuilder();
      StringBuilder numeric = new StringBuilder();

      textual.append(MPXConstants.TASK_MODEL_TEXT_RECORD_NUMBER);
      numeric.append(MPXConstants.TASK_MODEL_NUMERIC_RECORD_NUMBER);

      for (int loop = 0; loop < m_count; loop++)
      {
         number = m_fields[loop];

         textual.append(delimiter);
         numeric.append(delimiter);

         textual.append(getTaskField(number));
         numeric.append(number);
      }

      textual.append(MPXConstants.EOL);
      numeric.append(MPXConstants.EOL);

      textual.append(numeric);

      return (textual.toString());
   }

   /**
    * Returns Task field name of supplied code no.
    *
    * @param key - the code no of required Task field
    * @return - field name
    */
   private String getTaskField(int key)
   {
      String result = null;

      if ((key > 0) && (key < m_taskNames.length))
      {
         result = m_taskNames[key];
      }

      return (result);
   }

   /**
    * Returns code number of Task field supplied.
    *
    * @param field - name
    * @return - code no
    */
   private int getTaskCode(String field) throws MPXJException
   {
      Integer result = m_taskNumbers.get(field.trim());

      if (result == null)
      {
         throw new MPXJException(MPXJException.INVALID_TASK_FIELD_NAME + " " + field);
      }

      return (result.intValue());
   }

   private final ProjectFile m_parentFile;

   /**
    * Array of flags indicating whether each field has already been
    * added to the model.
    */
   private final boolean[] m_flags = new boolean[MPXTaskField.MAX_FIELDS];

   /**
    * Array of field numbers in order of their appearance.
    */
   private final int[] m_fields = new int[MPXTaskField.MAX_FIELDS + 1];

   /**
    * Count of the number of fields present.
    */
   private int m_count;

   /**
    * Array of task column names, indexed by ID.
    */
   private String[] m_taskNames;

   /**
    * Map used to store task field numbers.
    */
   private final HashMap<String, Integer> m_taskNumbers = new HashMap<>();
   private final UserDefinedFieldMap m_userDefinedFieldMap;
}
