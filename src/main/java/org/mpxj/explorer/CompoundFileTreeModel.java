/*
 * file:       CompoundFileTreeModel.java
 * author:     Jon Iles
 * date:       2014-07-06
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

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import org.mpxj.mscfb.CompoundFile;

/**
 * Implements the model component of the PoiTree MVC.
 */
class CompoundFileTreeModel implements TreeModel
{
   private final EventListenerList m_listenerList = new EventListenerList();
   private CompoundFile m_file;

   /**
    * Point the model to a file.
    *
    * @param file target file
    */
   public void setFile(CompoundFile file)
   {
      m_file = file;
      fireTreeStructureChanged();
   }

   /**
    * Retrieve the model's file.
    *
    * @return target file
    */
   public CompoundFile getFile()
   {
      return m_file;
   }

   @Override public Object getRoot()
   {
      return "/";
   }

   @Override public Object getChild(Object parent, int index)
   {
      try
      {
         Object result = null;
         String path = (String) parent;
         if (m_file.isDirectory(path))
         {
            List<String> entries = m_file.list(path);
            if (entries.size() > index)
            {
               result = entries.get(index);
            }
         }
         return result;
      }

      catch (IOException ex)
      {
         throw new UncheckedIOException(ex);
      }
   }

   @Override public int getChildCount(Object parent)
   {
      try
      {
         String path = (String) parent;
         if (m_file != null && m_file.isDirectory(path))
         {
            return m_file.list(path).size();
         }

         return 0;
      }

      catch (IOException ex)
      {
         throw new UncheckedIOException(ex);
      }
   }

   @Override public boolean isLeaf(Object node)
   {
      try
      {
         return m_file != null && !m_file.isDirectory((String) node);
      }

      catch (IOException ex)
      {
         throw new UncheckedIOException(ex);
      }
   }

   @Override public void valueForPathChanged(TreePath path, Object newValue)
   {
      throw new UnsupportedOperationException();
   }

   @Override public int getIndexOfChild(Object parent, Object child)
   {
      try
      {
         int result = -1;
         String path = (String) parent;
         if (m_file != null && m_file.isDirectory(path))
         {
            List<String> entries = m_file.list(path);
            result = entries.indexOf((String) child);
         }

         return result;
      }

      catch (IOException ex)
      {
         throw new UncheckedIOException(ex);
      }
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
