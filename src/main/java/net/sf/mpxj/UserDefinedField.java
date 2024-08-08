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
    * @param uniqueID unique ID
    * @param internalName internal name for this field
    * @param externalName user-visible name for this field
    * @param fieldTypeClass type of entity on which this field can be used
    * @param summaryTaskOnly flag is true if this UDF can only be applied to summary tasks (WBS)
    * @param dataType data type of this field
    * @deprecated use the new version of this constructor
    */
   @Deprecated public UserDefinedField(Integer uniqueID, String internalName, String externalName, FieldTypeClass fieldTypeClass, boolean summaryTaskOnly, DataType dataType)
   {
      if (internalName == null || internalName.isEmpty())
      {
         internalName = "user_field_" + uniqueID;
      }

      m_uniqueID = uniqueID;
      m_internalName = internalName;
      m_externalName = externalName;
      m_fieldTypeClass = fieldTypeClass;
      m_summaryTaskOnly = summaryTaskOnly;
      m_dataType = dataType;
   }

   /**
    * Constructor.
    *
    * @param file parent file
    * @param uniqueID unique ID
    * @param internalName internal name for this field
    * @param externalName user-visible name for this field
    * @param fieldTypeClass type of entity on which this field can be used
    * @param summaryTaskOnly flag is true if this UDF can only be applied to summary tasks (WBS)
    * @param dataType data type of this field
    * @deprecated use the Builder class
    */
   @Deprecated public UserDefinedField(ProjectFile file, Integer uniqueID, String internalName, String externalName, FieldTypeClass fieldTypeClass, boolean summaryTaskOnly, DataType dataType)
   {
      if (internalName == null || internalName.isEmpty())
      {
         internalName = "user_field_" + uniqueID;
      }

      m_uniqueID = file.getUniqueIdObjectSequence(UserDefinedField.class).syncOrGetNext(uniqueID);
      m_internalName = internalName;
      m_externalName = externalName;
      m_fieldTypeClass = fieldTypeClass;
      m_summaryTaskOnly = summaryTaskOnly;
      m_dataType = dataType;
   }

   /**
    * Private constructor.
    *
    * @param builder Builder instance
    */
   private UserDefinedField(Builder builder)
   {
      m_uniqueID = builder.m_file.getUniqueIdObjectSequence(UserDefinedField.class).syncOrGetNext(builder.m_uniqueID);
      m_internalName =  builder.m_internalName == null || builder.m_internalName.isEmpty() ? builder.m_internalName = "user_field_" + m_uniqueID : builder.m_internalName;
      m_externalName = builder.m_externalName;
      m_fieldTypeClass = builder.m_fieldTypeClass;
      m_summaryTaskOnly = builder.m_summaryTaskOnly;
      m_dataType = builder.m_dataType;
   }

   @Override public int getValue()
   {
      return m_uniqueID.intValue();
   }

   /**
    * Retrieve the unique ID.
    *
    * @return unique ID
    */
   public Integer getUniqueID()
   {
      return m_uniqueID;
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
    * @deprecated use the Builder class
    */
   @Deprecated public void setDataType(DataType dataType)
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

   private final Integer m_uniqueID;
   private final FieldTypeClass m_fieldTypeClass;
   private final boolean m_summaryTaskOnly;
   private final String m_externalName;
   private final String m_internalName;
   private DataType m_dataType;

   /**
    * User defined field builder.
    */
   public static class Builder
   {
      /**
       * Constructor.
       *
       * @param file parent project file.
       */
      public Builder(ProjectFile file)
      {
         m_file = file;
      }

      /**
       * Add the unique ID.
       *
       * @param value unique ID
       * @return builder
       */
      public Builder uniqueID(Integer value)
      {
         m_uniqueID = value;
         return this;
      }

      /**
       * Add the field type class.
       *
       * @param value field type class
       * @return builder
       */
      public Builder fieldTypeClass(FieldTypeClass value)
      {
         m_fieldTypeClass = value;
         return this;
      }

      /**
       * Add the summary task only flag.
       *
       * @param value summary task only flag
       * @return builder
       */
      public Builder summaryTaskOnly(boolean value)
      {
         m_summaryTaskOnly = value;
         return this;
      }

      /**
       * Add the external name.
       *
       * @param value external name
       * @return builder
       */
      public Builder externalName(String value)
      {
         m_externalName = value;
         return this;
      }

      /**
       * Add the internal name.
       *
       * @param value internal name
       * @return builder
       */
      public Builder internalName(String value)
      {
         m_internalName = value;
         return this;
      }

      /**
       * Add the data type.
       *
       * @param value data type
       * @return builder
       */
      public Builder dataType(DataType value)
      {
         m_dataType = value;
         return this;
      }

      /**
       * Build a UserDefinedField instance.
       *
       * @return builder
       */
      public UserDefinedField build()
      {
         return new UserDefinedField(this);
      }

      private final ProjectFile m_file;
      private Integer m_uniqueID;
      private FieldTypeClass m_fieldTypeClass;
      private boolean m_summaryTaskOnly;
      private String m_externalName;
      private String m_internalName;
      private DataType m_dataType;
   }
}
