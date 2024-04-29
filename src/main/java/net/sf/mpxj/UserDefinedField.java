/*
 * file:       UserDefinedField.java
 * author:     Jon Iles
 * copyright:  (c) Timephased Ltd 2023
 * date:       2023-02-04
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

package net.sf.mpxj;

import java.util.Locale;

/**
 * Represents a user defined field.
 */
public class UserDefinedField implements FieldType
{
   /**
    * Constructor.
    *
    * @param id unique ID
    * @param internalName internal name for this field
    * @param externalName user-visible name for this field
    * @param fieldTypeClass type of entity on which this field can be used
    * @param summaryTaskOnly flag is true if this UDF can only be applied to summary tasks (WBS)
    * @param dataType data type of this field
    */
   public UserDefinedField(Integer id, String internalName, String externalName, FieldTypeClass fieldTypeClass, boolean summaryTaskOnly, DataType dataType)
   {
      if (internalName == null || internalName.isEmpty())
      {
         internalName = "user_field_" + id;
      }

      m_id = id;
      m_internalName = internalName;
      m_externalName = externalName;
      m_fieldTypeClass = fieldTypeClass;
      m_summaryTaskOnly = summaryTaskOnly;
      m_dataType = dataType;
   }

   @Override public int getValue()
   {
      return m_id.intValue();
   }

   /**
    * Retrieve the unique ID.
    *
    * @return unique ID
    */
   public Integer getUniqueID()
   {
      return m_id;
   }

   /**
    * For a user defined field with a FieldTypeClas of TASK,  this method
    * returns true if this is a user defined field for WBS
    * (represented as summary tasks in MPXJ).
    *
    * @return true if this is a WBS user defined field
    */
   public boolean getSummaryTaskOnly()
   {
      return m_summaryTaskOnly;
   }

   @Override public FieldTypeClass getFieldTypeClass()
   {
      return m_fieldTypeClass;
   }

   @Override public String getName()
   {
      return m_externalName;
   }

   @Override public String name()
   {
      return m_internalName;
   }

   @Override public String getName(Locale locale)
   {
      return getName();
   }

   @Override public DataType getDataType()
   {
      return m_dataType;
   }

   /**
    * Set the data type of this field.
    *
    * @param dataType data  type
    */
   public void setDataType(DataType dataType)
   {
      m_dataType = dataType;
   }

   @Override public FieldType getUnitsType()
   {
      return null;
   }

   @Override public String toString()
   {
      return getName();
   }

   private final Integer m_id;
   private final FieldTypeClass m_fieldTypeClass;
   private final boolean m_summaryTaskOnly;
   private final String m_externalName;
   private final String m_internalName;
   private DataType m_dataType;
}
