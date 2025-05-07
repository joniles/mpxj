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

package org.mpxj.explorer;

import java.util.Arrays;
import java.util.HashSet;
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
      this(object, DEFAULT_EXCLUDED_METHODS);
   }

   /**
    * Constructor.
    *
    * @param object tree node object
    * @param excludedMethods set of excluded method names
    */
   public MpxjTreeNode(Object object, Set<String> excludedMethods)
   {
      super(object);
      m_excludedMethods = excludedMethods;
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

   private final Set<String> m_excludedMethods;

   public static final Set<String> DEFAULT_EXCLUDED_METHODS = new HashSet<>(Arrays.asList("getClass", "getParentFile", "getInstance"));

}
