package net.sf.mpxj;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EPS implements ChildEpsNodeContainer
{
   public void addEpsChildNode(EpsNode node)
   {
      m_nodes.put(node.getUniqueID(), node);
   }

   public List<EpsNode> getEpsNodes()
   {
      return new ArrayList<>(m_nodes.values());
   }

   public List<EpsNode> getChildEpsNodes()
   {
      return m_nodes.values().stream().filter(n -> n.getParentUniqueID() == null).collect(Collectors.toList());
   }

   public EpsNode getEpsNodeByUniqueID(Integer value)
   {
      return m_nodes.get(value);
   }

   public void addEpsProjectNode(EpsProjectNode node)
   {
      m_projectNodes.put(node.getUniqueID(), node);
   }

   public List<EpsProjectNode> getEpsProjectNodes()
   {
      return new ArrayList<>(m_projectNodes.values());
   }

   public EpsProjectNode getProjectNodeByUniqueID(Integer value)
   {
      return m_projectNodes.get(value);
   }

   private final Map<Integer, EpsNode> m_nodes = new LinkedHashMap<>();
   private final Map<Integer, EpsProjectNode> m_projectNodes = new LinkedHashMap<>();
}
