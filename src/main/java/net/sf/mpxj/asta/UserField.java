
package net.sf.mpxj.asta;

import net.sf.mpxj.FieldType;

class UserField
{
   public UserField(FieldType field, int objectType, int dataType)
   {
      m_field = field;
      m_objectType = objectType;
      m_dataType = dataType;
   }

   public FieldType getField()
   {
      return m_field;
   }

   public int getObjectType()
   {
      return m_objectType;
   }

   public int getDataType()
   {
      return m_dataType;
   }

   private final FieldType m_field;
   private final int m_objectType;
   private final int m_dataType;
}
