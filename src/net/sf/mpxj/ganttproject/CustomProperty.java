
package net.sf.mpxj.ganttproject;

import net.sf.mpxj.FieldType;

final class CustomProperty
{
   public CustomProperty(FieldType[] fields)
   {
      this(fields, 0);
   }

   public CustomProperty(FieldType[] fields, int index)
   {
      m_fields = fields;
      m_index = index;
   }

   public FieldType getField()
   {
      FieldType result = null;
      if (m_index < m_fields.length)
      {
         result = m_fields[m_index++];
      }

      return result;
   }

   private final FieldType[] m_fields;
   private int m_index;
}
