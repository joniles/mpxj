/*
 * file:       TaskModel.java
 * author:     Scott Melville
 *             Jon Iles
 * copyright:  (c) Tapster Rock Limited 2002-2003
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

package com.tapsterrock.mpx;

import java.util.HashMap;
import java.util.Locale;

/**
 * This class represents the task table definition record in an MPX file.
 * This record defines which fields are present in a task record.
 * This record has two forms, one textual and one numeric. Both
 * variants are handled by this class.
 */
final class TaskModel extends MPXRecord
{
   /**
    * Default constructor.
    *
    * @param file the parent file to which this record belongs.
    */
   TaskModel (MPXFile file)
   {
      super(file, 0);
      setLocale(file.getLocale());
   }

   /**
    * This method is used to update the locale specific data used by this class.
    * 
    * @param locale
    */
   void setLocale (Locale locale)
   {
      m_taskNames = LocaleData.getStringArray(locale, LocaleData.TASK_NAMES);
      
      String name;
      m_taskNumbers.clear();
   
      for (int loop=0; loop < m_taskNames.length; loop++)
      {
         name = m_taskNames[loop];
         if (name != null)
         {
            m_taskNumbers.put(name, new Integer (loop));
         }            
      }        
   }
   
   /**
    * This method is used to retrieve a linked list of field identifiers
    * indicating the fields present in a task record.
    *
    * @return list of field names
    */
   public int[] getModel ()
   {
      m_fields[m_count] = -1;
      return (m_fields);
   }

   /**
    * This method populates the task model from data read from an MPX file.
    *
    * @param record data read from an MPX file
    * @param isText flag indicating whether the tetxual or numeric data is being supplied
    */
   public void update (Record record, boolean isText)
      throws MPXException
   {
      int length = record.getLength();

      for  (int i = 0 ; i < length ; i++ )
      {
         if (isText == true)
         {
            add (getTaskCode (record.getString (i)));
         }
         else
         {
            add (record.getInteger (i).intValue());
         }
      }
   }


   /**
    * Retrieves a flag indicating whether this model has been written
    * to a file.
    *
    * @return written flag
    */
   public boolean getWritten ()
   {
      return (m_written);
   }

   /**
    * This method is used to set the written flag to indicate whether this
    * model has been written to a file.
    *
    * @param written flag indicating whether the model has been written
    */
   public void setWritten (boolean written)
   {
      m_written = written;
   }


   /**
    * This method is called from the task class each time an attribute
    * is added, ensuring that all of the attributes present in each task
    * record are present in the resource model.
    *
    * @param field field identifier
    */
   public void add (int field)
   {
      if (m_flags[field] == false)
      {
         m_flags[field] = true;
         m_fields[m_count] = field;
         ++m_count;
      }
   }

   /**
    * This method generates a string in MPX format representing the
    * contents of this record. Both the textual and numeric record
    * types are written by this method.
    *
    * @return string containing the data for this record in MPX format.
    */
   public String toString()
   {
      int number;
      char delimiter = getParentFile().getDelimiter();

      StringBuffer textual = new StringBuffer();
      StringBuffer numeric = new StringBuffer();

      textual.append (RECORD_NUMBER_TEXT);
      numeric.append (RECORD_NUMBER_NUMERIC);

      for (int loop=0; loop < m_count; loop++)
      {
         number = m_fields[loop];

         textual.append (delimiter);
         numeric.append (delimiter);

         textual.append(getTaskField(number));
         numeric.append (number);
      }

      textual.append (MPXFile.EOL);
      numeric.append (MPXFile.EOL);

      textual.append (numeric.toString());

      return (textual.toString());
   }


   /**
    * Returns Task field name of supplied code no.
    *
    * @param key - the code no of required Task field
    * @return - field name
    */
   private String getTaskField (int key)
   {
      String result = null;
      
      if (key > 0 && key < m_taskNames.length)
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
   private int getTaskCode (String field)
      throws MPXException
   {
      Integer result = (Integer)m_taskNumbers.get(field);

      if (result == null)
      {
         throw new MPXException (MPXException.INVALID_TASK_FIELD_NAME + " " + field);
      }

      return (result.intValue());
   }

   /**
    * Indicating whether or not model has already been written to a file.
    */
   private boolean m_written;

   /**
    * Array of flags indicting whether each field has already been
    * added to the model.
    */
   private boolean[] m_flags = new boolean [Task.MAX_FIELDS];

   /**
    * Array of field numbers in order of their appearance.
    */
   private int[] m_fields = new int [Task.MAX_FIELDS+1];

   /**
    * Count of the number of fields present.
    */
   private int m_count;

   /**
    * Array of task column names, indexed by ID
    */
   private String[] m_taskNames;
   
   /**
    * Map used to store task field numbers.
    */
   private HashMap m_taskNumbers = new HashMap();

   /**
    * Constant value representing Text Task Model class.
    */
   static final int RECORD_NUMBER_TEXT = 60;

   /**
    * Constant value representing Numeric Task Model class.
    */
   static final int RECORD_NUMBER_NUMERIC = 61;
}

