/*
 * file:       PoiTreeModel.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2014
 * date:       06/07/2014
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.Entry;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

/**
 * Implements the model component of the PoiTree MVC.
 */
public class PoiTreeModel implements TreeModel
{
   private final EventListenerList m_listenerList = new EventListenerList();
   private POIFSFileSystem m_file;

   /**
    * Point the model to a file.
    *
    * @param file POIFS file
    */
   public void setFile(POIFSFileSystem file)
   {
      m_file = file;
      fireTreeStructureChanged();
   }

   @Override public Object getRoot()
   {
      Object result = null;
      if (m_file != null)
      {
         result = m_file.getRoot();
      }
      return result;
   }

   @Override public Object getChild(Object parent, int index)
   {
      Object result = null;
      if (parent instanceof DirectoryEntry)
      {
         List<Entry> entries = getChildNodes((DirectoryEntry) parent);
         if (entries.size() > index)
         {
            result = entries.get(index);
         }
      }
      return result;
   }

   @Override public int getChildCount(Object parent)
   {
      int result;
      if (parent instanceof DirectoryEntry)
      {
         DirectoryEntry node = (DirectoryEntry) parent;
         result = node.getEntryCount();
      }
      else
      {
         result = 0;
      }
      return result;
   }

   @Override public boolean isLeaf(Object node)
   {
      return !(node instanceof DirectoryEntry);
   }

   @Override public void valueForPathChanged(TreePath path, Object newValue)
   {
      throw new UnsupportedOperationException();
   }

   @Override public int getIndexOfChild(Object parent, Object child)
   {
      int result = -1;
      if (parent instanceof DirectoryEntry)
      {
         List<Entry> entries = getChildNodes((DirectoryEntry) parent);
         //noinspection SuspiciousMethodCalls
         result = entries.indexOf(child);
      }

      return result;
   }

   @Override public void addTreeModelListener(TreeModelListener l)
   {
      m_listenerList.add(TreeModelListener.class, l);
   }

   @Override public void removeTreeModelListener(TreeModelListener l)
   {
      m_listenerList.remove(TreeModelListener.class, l);
   }

   /**
    * Retrieves child nodes from a directory entry.
    *
    * @param parent parent directory entry
    * @return list of child nodes
    */
   private List<Entry> getChildNodes(DirectoryEntry parent)
   {
      List<Entry> result = new ArrayList<>();
      Iterator<Entry> entries = parent.getEntries();
      while (entries.hasNext())
      {
         result.add(entries.next());
      }
      return result;
   }

   /**
    * Notify listeners that the tree structure has changed.
    */
   private void fireTreeStructureChanged()
   {
      // Guaranteed to return a non-null array
      Object[] listeners = m_listenerList.getListenerList();
      TreeModelEvent e = null;
      // Process the listeners last to first, notifying
      // those that are interested in this event
      for (int i = listeners.length - 2; i >= 0; i -= 2)
      {
         if (listeners[i] == TreeModelListener.class)
         {
            // Lazily create the event:
            if (e == null)
            {
               e = new TreeModelEvent(getRoot(), new Object[]
               {
                  getRoot()
               }, null, null);
            }
            ((TreeModelListener) listeners[i + 1]).treeStructureChanged(e);
         }
      }
   }

}
