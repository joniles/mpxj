/*
 * file:       MpxjTreeNode.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2017
 * date:       29/12/2017
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

package net.sf.mpxj.explorer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Extend DefaultMutableTreeNode so we can include the
 * names of methods we don't want to display.
 */
public class MpxjTreeNode extends DefaultMutableTreeNode
{
   /**
    * Constructor.
    */
   public MpxjTreeNode()
   {
      this(null);
   }

   /**
    * Constructor.
    *
    * @param object tree node object
    */
   public MpxjTreeNode(Object object)
   {
      this(object, null);
   }

   /**
    * Constructor.
    *
    * @param object tree node object
    * @param excludedMethods set of excluded method names
    */
   public MpxjTreeNode(Object object, List<String> excludedMethods)
   {
      super(object);
      if (excludedMethods != null)
      {
         m_excludedMethods.addAll(excludedMethods);
      }
   }

   /**
    * Retrieve the set of excluded method names.
    *
    * @return set of excluded method names
    */
   public Set<String> getExcludedMethods()
   {
      return m_excludedMethods;
   }

   private Set<String> m_excludedMethods = new HashSet<String>(DEFAULT_EXCLUDED_METHODS);

   private static final List<String> DEFAULT_EXCLUDED_METHODS = Arrays.asList("getClass", "getParentFile", "getInstance");
}
