
package net.sf.mpxj;

public class CustomFieldValueMask
{
   public CustomFieldValueMask(int length, int level, String separator, CustomFieldValueDataType type)
   {
      m_length = length;
      m_level = level;
      m_separator = separator;
      m_type = type;
   }
   
   public int getLength()
   {
      return m_length;
   } 
   
   public int getLevel()
   {
      return m_level;
   }
   
   public String getSeparator()
   {
      return m_separator;
   }
   
   public CustomFieldValueDataType getType()
   {
      return m_type;
   }

   private final int m_length;
   private final int m_level;
   private final String m_separator;
   private final CustomFieldValueDataType m_type;
}
