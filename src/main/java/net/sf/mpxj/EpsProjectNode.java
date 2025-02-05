package net.sf.mpxj;

public class EpsProjectNode
{
   public EpsProjectNode(Integer uniqueID, Integer epsUniqueID, String shortName, String name)
   {
      m_uniqueID = uniqueID;
      m_epsUniqueID = epsUniqueID;
      m_shortName = shortName;
      m_name = name;
   }

   public Integer getUniqueID()
   {
      return m_uniqueID;
   }

   public Integer getEpsUniqueID()
   {
      return m_epsUniqueID;
   }

   public String getShortName()
   {
      return m_shortName;
   }

   public String getName()
   {
      return m_name;
   }

   private final Integer m_uniqueID;
   private final Integer m_epsUniqueID;
   private final String m_shortName;
   private final String m_name;
}
