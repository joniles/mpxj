/*
 * file:       HierarchyHelper.java
 * author:     Jon Iles
 * date:       2023-11-14
 */

/*
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or (at your
 * option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 */

package org.mpxj.common;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Helper methods for working with hierarchical data.
 */
public class HierarchyHelper
{
   /**
    * Given a list of items, sort the items such that parent items always appear
    * before child items. This allows for "one shot" creation of object instances
    * based on this data rather than having to make a second pass to create
    * the hierarchy.
    *
    * @param <K> ID type
    * @param <V> value type
    * @param list list of items
    * @param getId function to retrieve an item's ID
    * @param getParentId function to retrieve an item's parent ID
    * @return list sorted to ensure parents appear before children
    */
   public static final <K, V> List<V> sortHierarchy(List<V> list, Function<V, K> getId, Function<V, K> getParentId)
   {
      return sortHierarchy(list, getId, getParentId, null);
   }

   /**
    * Given a list of items, sort the items such that parent items always appear
    * before child items and ensure child items are sorted.
    * This allows for "one shot" creation of object instances
    * based on this data rather than having to make a second pass to create
    * the hierarchy.
    *
    * @param <K> ID type
    * @param <V> value type
    * @param list list of items
    * @param getId function to retrieve an item's ID
    * @param getParentId function to retrieve an item's parent ID
    * @param comparator sort order for items within the hierarchy
    * @return list sorted to ensure parents appear before children
    */
   public static final <K, V> List<V> sortHierarchy(List<V> list, Function<V, K> getId, Function<V, K> getParentId, Comparator<V> comparator)
   {
      // Bail out early if sorting is not required
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
         (parent == null ? root : parent).addChild(entry.getValue());
      }

      return comparator == null ? addChildNodes(new ArrayList<>(), root) : addChildNodes(new ArrayList<>(), root, (o1, o2) -> comparator.compare(o1.getItem(), o2.getItem()));
   }

   /**
    * Recursively add parent and child items to a list.
    *
    * @param <V> value type
    * @param list list to which items are added
    * @param parent parent node
    * @return list with nodes added
    */
   private static <V> List<V> addChildNodes(List<V> list, HierarchyNode<V> parent)
   {
      parent.getChildNodes().forEach(c -> {
         list.add(c.getItem());
         addChildNodes(list, c);
      });
      return list;
   }

   /**
    * Recursively add parent and child items to a list, with child items sorted.
    *
    * @param <V> value type
    * @param list list to which items are added
    * @param parent parent node
    * @param hierarchyNodeComparator sort order for items
    * @return list with nodes added
    */
   private static <V> List<V> addChildNodes(List<V> list, HierarchyNode<V> parent, Comparator<HierarchyNode<V>> hierarchyNodeComparator)
   {
      parent.getChildNodes().stream().sorted(hierarchyNodeComparator).forEach(c -> {
         list.add(c.getItem());
         addChildNodes(list, c, hierarchyNodeComparator);
      });
      return list;
   }

   /**
    * Class used to represent an item and any child items.
    *
    * @param <V> value type
    */
   private static class HierarchyNode<V>
   {
      /**
       * Root node constructor.
       */
      public HierarchyNode()
      {
         this(null);
      }

      /**
       * Constructor.
       *
       * @param item item represented by this node
       */
      public HierarchyNode(V item)
      {
         m_item = item;
      }

      /**
       * Retrieve the item represented by this node.
       *
       * @return item
       */
      public V getItem()
      {
         return m_item;
      }

      /**
       * Add a child node.
       *
       * @param child child node
       */
      public void addChild(HierarchyNode<V> child)
      {
         m_childNodes.add(child);
      }

      /**
       * Retrieve the child nodes.
       *
       * @return child nodes
       */
      public List<HierarchyNode<V>> getChildNodes()
      {
         return m_childNodes;
      }

      private final V m_item;
      private final List<HierarchyNode<V>> m_childNodes = new ArrayList<>();
   }
}
