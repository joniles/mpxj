package net.sf.mpxj.common;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class HierarchyHelper
{
   public static final <K, V> List<V> sortHierarchy(List<V> list, Function<V, K> getId, Function<V, K> getParentId)
   {
      if (list.size() < 2 || list.stream().allMatch(v -> getParentId.apply(v) == null))
      {
         return list;
      }

      Map<K, HierarchyNode<V>> nodes = new HashMap<>();
      list.forEach(i -> nodes.put(getId.apply(i), new HierarchyNode<>(i)));

      HierarchyNode<V> root = new HierarchyNode<>();
      for (Map.Entry<K, HierarchyNode<V>> entry : nodes.entrySet())
      {
         HierarchyNode<V> parent = nodes.get(getParentId.apply(entry.getValue().getItem()));
         if (getParentId.apply(entry.getValue().getItem()) != null && parent == null)
         {
            System.out.println("MISSING " + getParentId.apply(entry.getValue().getItem()));
         }
         (parent == null ? root : parent).addChild(entry.getValue());
      }

      List<V> result = addChildNodes(new ArrayList<>(), root);
      return result;
   }

   private static <V> List<V> addChildNodes(List<V> list, HierarchyNode<V> parent)
   {
      parent.getChildNodes().forEach(c -> { list.add(c.getItem()); addChildNodes(list, c); });
      return list;
   }

   private static class HierarchyNode<V>
   {
      public HierarchyNode()
      {
         this(null);
      }

      public HierarchyNode(V item)
      {
         m_item = item;
      }

      public V getItem()
      {
         return m_item;
      }

      public void addChild(HierarchyNode<V> child)
      {
         m_childNodes.add(child);
      }

      public List<HierarchyNode<V>> getChildNodes()
      {
         return m_childNodes;
      }

      private final V m_item;
      private final List<HierarchyNode<V>> m_childNodes = new ArrayList<>();
   }
}
