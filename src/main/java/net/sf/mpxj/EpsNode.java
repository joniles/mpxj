package net.sf.mpxj;

import java.util.List;
import java.util.stream.Collectors;

public class EpsNode implements ChildEpsNodeContainer
{
   public EpsNode(EPS eps, Integer uniqueID, Integer parentUniqueID, String name, String shortName)
   {
      m_eps = eps;
      m_uniqueID = uniqueID;
      m_parentUniqueID = parentUniqueID;
      m_name = name;
      m_shortName = shortName;
   }

   public Integer getUniqueID()
   {
      return m_uniqueID;
   }

   public Integer getParentUniqueID()
   {
      return m_parentUniqueID;
   }

   public String getName()
   {
      return m_name;
   }

   public String getShortName()
   {
      return m_shortName;
   }

   public List<EpsNode> getChildEpsNodes()
   {
      return m_eps.getEpsNodes().stream().filter(n -> n.getParentUniqueID().equals(m_uniqueID)).collect(Collectors.toList());
   }

   public List<EpsProjectNode> getEpsProjectNodes()
   {
      return m_eps.getEpsProjectNodes().stream().filter(n -> n.getEpsUniqueID().equals(m_uniqueID)).collect(Collectors.toList());
   }

   private final EPS m_eps;
   private final Integer m_uniqueID;
   private final Integer m_parentUniqueID;
   private final String m_name;
   private final String m_shortName;
}
