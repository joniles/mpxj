package net.sf.mpxj.openplan;

import java.util.List;

class Code
{
   public Code(String id, String promptText, String description, List<CodeValue> values)
   {
      m_id = id;
      m_promptText = promptText;
      m_description = description;
      m_values = values;
   }

   public String getID()
   {
      return m_id;
   }

   public String getPromptText()
   {
      return m_promptText;
   }

   public String getDescription()
   {
      return m_description;
   }

   public List<CodeValue> getValues()
   {
      return m_values;
   }

   private final String m_id;
   private final String m_promptText;
   private final String m_description;
   private final List<CodeValue> m_values;
}
