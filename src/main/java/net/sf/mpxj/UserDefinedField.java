package net.sf.mpxj;
import java.util.Locale;

public class UserDefinedField implements FieldType
{
   public UserDefinedField(int id, String internalName, String externalName, FieldTypeClass fieldTypeClass)
   {
      m_id = id;
      m_internalName = internalName;
      m_externalName = externalName;
      m_fieldTypeClass = fieldTypeClass;
   }

   @Override public int getValue()
   {
      return m_id.intValue();
   }

   public Integer getUniqueID()
   {
      return m_id;
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
      return DataType.CUSTOM;
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
   private final String m_externalName;
   private final String m_internalName;
}
