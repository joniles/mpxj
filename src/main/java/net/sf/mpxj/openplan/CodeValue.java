package net.sf.mpxj.openplan;
public class CodeValue
{
   public CodeValue(String id, String description)
   {
      m_id = id;
      m_description = description;
   }

   public String getID()
   {
      return m_id;
   }

   public String getDescription()
   {
      return m_description;
   }

   private final String m_id;
   private final String m_description;
}
