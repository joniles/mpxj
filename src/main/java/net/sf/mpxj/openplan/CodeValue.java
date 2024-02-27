
package net.sf.mpxj.openplan;
public class CodeValue
{
   public CodeValue(String id, String uniqueID, String description)
   {
      m_id = id;
      m_uniqueID = uniqueID;
      m_description = description;
   }

   public String getID()
   {
      return m_id;
   }

   public String getUniqueID()
   {
      return m_uniqueID;
   }

   public String getDescription()
   {
      return m_description;
   }

   private final String m_id;
   private final String m_uniqueID;
   private final String m_description;
}
