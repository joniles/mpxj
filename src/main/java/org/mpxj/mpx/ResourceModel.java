/*
 * file:       ResourceModel.java
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
import org.mpxj.Resource;
import org.mpxj.mpp.UserDefinedFieldMap;

/**
 * This class represents the resource table definition record in an MPX file.
 * This record defines which fields are present in a resource record.
 * This record has two forms, one textual and one numeric. Both
 * variants are handled by this class.
 */
final class ResourceModel
{
   /**
    * Default constructor.
    *
    * @param file the parent file to which this record belongs.
    * @param locale target locale
    * @param userDefinedFieldMap user defined field map
    */
   ResourceModel(ProjectFile file, Locale locale, UserDefinedFieldMap userDefinedFieldMap)
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
      m_resourceNames = LocaleData.getStringArray(locale, LocaleData.RESOURCE_NAMES);

      String name;
      m_resourceNumbers.clear();

      for (int loop = 0; loop < m_resourceNames.length; loop++)
      {
         name = m_resourceNames[loop];
         if (name != null)
         {
            m_resourceNumbers.put(name, Integer.valueOf(loop));
         }
      }
   }

   /**
    * This method populates the resource model from data read from an MPX file.
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
            add(getResourceCode(record.getString(i)));
         }
         else
         {
            add(record.getInteger(i).intValue());
         }
      }
   }

   /**
    * This method is used to retrieve an array of field identifiers
    * indicating the fields present in a resource record. Note that
    * the values in this array will be terminated by -1.
    *
    * @return array of field identifiers
    */
   public int[] getModel()
   {
      m_fields[m_count] = -1;
      return (m_fields);
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

      for (Resource resource : m_parentFile.getResources())
      {
         for (int loop = 0; loop < MPXResourceField.MAX_FIELDS; loop++)
         {
            FieldType field = MPXResourceField.getMpxjField(loop);
            field = m_userDefinedFieldMap == null ? field : m_userDefinedFieldMap.getSource(field);

            Object value = resource.get(field);
            if (ModelUtility.isFieldPopulated(field, value))
            {
               if (!m_flags[loop])
               {
                  m_flags[loop] = true;
                  m_fields[m_count] = loop;
                  ++m_count;
               }
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

      textual.append(MPXConstants.RESOURCE_MODEL_TEXT_RECORD_NUMBER);
      numeric.append(MPXConstants.RESOURCE_MODEL_NUMERIC_RECORD_NUMBER);

      for (int loop = 0; loop < m_count; loop++)
      {
         number = m_fields[loop];

         textual.append(delimiter);
         numeric.append(delimiter);

         textual.append(getResourceField(number));
         numeric.append(number);
      }

      textual.append(MPXConstants.EOL);
      numeric.append(MPXConstants.EOL);

      textual.append(numeric);

      return (textual.toString());
   }

   /**
    * This method is called from the Resource class each time an attribute
    * is added, ensuring that all of the attributes present in each resource
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
    * Given a resource field number, this method returns the resource field name.
    *
    * @param key resource field number
    * @return resource field name
    */
   private String getResourceField(int key)
   {
      String result = null;

      if (key > 0 && key < m_resourceNames.length)
      {
         result = m_resourceNames[key];
      }

      return (result);
   }

   /**
    * Given a resource field name, this method returns the resource field number.
    *
    * @param field resource field name
    * @return resource field number
    */
   private int getResourceCode(String field) throws MPXJException
   {
      Integer result = m_resourceNumbers.get(field);

      if (result == null)
      {
         throw new MPXJException(MPXJException.INVALID_RESOURCE_FIELD_NAME + " " + field);
      }

      return (result.intValue());
   }

   private final ProjectFile m_parentFile;

   /**
    * Array of flags indicating whether each field has already been
    * added to the model.
    */
   private final boolean[] m_flags = new boolean[MPXResourceField.MAX_FIELDS];

   /**
    * Array of field numbers in order of their appearance.
    */
   private final int[] m_fields = new int[MPXResourceField.MAX_FIELDS + 1];

   /**
    * Count of the number of fields present.
    */
   private int m_count;

   /**
    * Array of resource column names, indexed by ID.
    */
   private String[] m_resourceNames;

   /**
    * Map to store Resource field Numbers.
    */
   private final HashMap<String, Integer> m_resourceNumbers = new HashMap<>();
   private final UserDefinedFieldMap m_userDefinedFieldMap;
}
