
package net.sf.mpxj;

public class ActivityCodeValue
{
   public ActivityCodeValue(ActivityCode type, Integer uniqueID, String name, String description)
   {
      m_type = type;
      m_uniqueID = uniqueID;
      m_name = name;
      m_description = description;
   }

   public ActivityCode getType()
   {
      return m_type;
   }

   public Integer getUniqueID()
   {
      return m_uniqueID;
   }

   public String getName()
   {
      return m_name;
   }

   public String getDescription()
   {
      return m_description;
   }

   private final ActivityCode m_type;
   private final Integer m_uniqueID;
   private final String m_name;
   private final String m_description;
}
