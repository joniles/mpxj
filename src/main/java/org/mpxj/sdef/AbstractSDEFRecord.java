/*
 * file:       AbstractSDEFRecord.java
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

import java.time.LocalDateTime;

import org.mpxj.ConstraintType;
import org.mpxj.Duration;
import org.mpxj.ProjectFile;
import org.mpxj.RelationType;

/**
 * Common method implementations.
 */
abstract class AbstractSDEFRecord implements SDEFRecord
{
   @Override public void read(ProjectFile file, String line, boolean ignoreErrors)
   {
      int index = 0;
      int offset = 5;
      for (SDEFField field : getFieldDefinitions())
      {
         Object value;
         try
         {
            value = field.read(line, offset);
         }

         catch (Exception ex)
         {
            if (ignoreErrors)
            {
               file.addIgnoredError(ex);
               value = null;
            }
            else
            {
               throw ex;
            }
         }
         m_fields[index++] = value;
         offset += (field.getLength() + 1);
      }
   }

   /**
    * Retrieve the field definitions for this record type.
    *
    * @return array of field definitions
    */
   protected abstract SDEFField[] getFieldDefinitions();

   /**
    * Retrieve a string field.
    *
    * @param index field index
    * @return field value
    */
   protected String getString(int index)
   {
      return (String) m_fields[index];
   }

   /**
    * Retrieve an integer field.
    *
    * @param index field index
    * @return field value
    */
   protected Integer getInteger(int index)
   {
      return (Integer) m_fields[index];
   }

   /**
    * Retrieve a double field.
    *
    * @param index field index
    * @return field value
    */
   protected Double getDouble(int index)
   {
      return (Double) m_fields[index];
   }

   /**
    * Retrieve a date field.
    *
    * @param index field index
    * @return field value
    */
   protected LocalDateTime getDate(int index)
   {
      return (LocalDateTime) m_fields[index];
   }

   /**
    * Retrieve a duration field.
    *
    * @param index field index
    * @return field value
    */
   protected Duration getDuration(int index)
   {
      return (Duration) m_fields[index];
   }

   /**
    * Retrieve a constraint type field.
    *
    * @param index field index
    * @return field value
    */
   protected ConstraintType getConstraintType(int index)
   {
      return (ConstraintType) m_fields[index];
   }

   /**
    * Retrieve a relation type field.
    *
    * @param index field index
    * @return field value
    */
   protected RelationType getRelationType(int index)
   {
      return (RelationType) m_fields[index];
   }

   private final Object[] m_fields = new Object[getFieldDefinitions().length];
}
