package net.sf.mpxj;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EPS implements ChildEpsNodeContainer
{
   public List<EpsNode> getEpsNodes()
   {
      return m_nodes;
   }

   public List<EpsNode> getChildEpsNodes()
   {
      return m_nodes.stream().filter(n -> n.getUniqueID() == null).collect(Collectors.toList());
   }

   public List<EpsProjectNode> getEpsProjectNodes()
   {
      return m_projectNodes;
   }

   private final List<EpsNode> m_nodes = new ArrayList<>();
   private final List<EpsProjectNode> m_projectNodes = new ArrayList<>();
}
