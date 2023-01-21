package net.sf.mpxj;
import java.util.Locale;

public class UserDefinedField implements FieldType
{
   public UserDefinedField(int id, String internalPrefix, String presentationPrefix, FieldTypeClass fieldypeClass)
   {
      m_id = id;
      m_internalName = internalPrefix + "_" + id;
      m_presentationName = presentationPrefix + " " + id;
      m_fieldTypeClass = fieldypeClass;
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
      return m_presentationName;
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
   private final String m_presentationName;
   private final String m_internalName;
}
