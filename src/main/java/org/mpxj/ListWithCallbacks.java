/*
 * file:       ListWithCallbacks.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2002-2015
 * date:       20/04/2015
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

package org.mpxj;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

/**
 * Class implementing a list interface, backed by an ArrayList instance with callbacks
 * which can be overridden by subclasses for notification of added and removed items.
 *
 * @param <T> list content type
 */
public abstract class ListWithCallbacks<T> extends AbstractList<T>
{
   /**
    * Called to notify subclasses of item addition.
    *
    * @param element added item
    */
   protected void added(T element)
   {
      // Optional implementation supplied by subclass
   }

   /**
    * Called to notify subclasses of item removal.
    *
    * @param element removed item
    */
   protected void removed(T element)
   {
      // Optional implementation supplied by subclass
   }

   /**
    * Called to notify subclasses of item replacement.
    *
    * @param oldElement old element
    * @param newElement new element
    */
   protected void replaced(T oldElement, T newElement)
   {
      // Optional implementation supplied by subclass
   }

   /**
    * Clear the list, but don't explicitly "remove" the contents.
    */
   @Override public void clear()
   {
      m_list.clear();
   }

   @Override public T get(int index)
   {
      return m_list.get(index);
   }

   @Override public int size()
   {
      return m_list.size();
   }

   @Override public T set(int index, T element)
   {
      T removed = m_list.set(index, element);
      replaced(removed, element);
      return removed;
   }

   @Override public boolean add(T e)
   {
      m_list.add(e);
      added(e);
      return true;
   }

   @Override public void add(int index, T element)
   {
      m_list.add(index, element);
      added(element);
   }

   @Override public T remove(int index)
   {
      T removed = m_list.remove(index);
      removed(removed);
      return removed;
   }

   private final List<T> m_list = new ArrayList<>();
}
