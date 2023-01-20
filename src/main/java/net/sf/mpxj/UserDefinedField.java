package net.sf.mpxj;
import java.util.Locale;

public class UserDefinedField implements FieldType
{
   @Override public int getValue()
   {
      return m_id;
   }

   public void setUniqueID(Integer value)
   {
      m_id = value;
   }

   public Integer getUniqueID()
   {
      return m_id;
   }

   public void setFieldTypeClass(FieldTypeClass value)
   {
      m_fieldTypeClass = value;
   }

   @Override public FieldTypeClass getFieldTypeClass()
   {
      return m_fieldTypeClass;
   }

   public void setName(String value)
   {
      m_name = value;
   }

   @Override public String getName()
   {
      return m_name;
   }

   @Override public String name()
   {
      return getName();
   }

   @Override public String getName(Locale locale)
   {
      return getName();
   }

   public void setDataType(DataType value)
   {
      m_dataType = value;
   }

   @Override public DataType getDataType()
   {
      return m_dataType;
   }

   public void setUnitsType(FieldType value)
   {
      m_unitsType = value;
   }

   @Override public FieldType getUnitsType()
   {
      return null;
   }

   private Integer m_id;
   private FieldTypeClass m_fieldTypeClass;
   private String m_name;
   private DataType m_dataType;
   private FieldType m_unitsType;
}
