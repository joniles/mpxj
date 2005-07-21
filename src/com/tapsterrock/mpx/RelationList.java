/*
 * file:       RelationList.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2002-2003
 * date:       15/01/2003
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

package com.tapsterrock.mpx;

import java.util.AbstractList;
import java.util.Iterator;
import java.util.LinkedList;


/**
 * This class represents a list of relationships between tasks.
 */
public final class RelationList extends AbstractList implements ToStringRequiresFile
{
   /**
    * Default constructor.
    */
   RelationList ()
   {
   }

   /**
    * This constructor populates a list of relationships from
    * data read from an MPX file.
    *
    * @param data data read from an MPX file
    * @param file parent MPX file
    * @throws MPXException nroamlly thrown on parse errors
    */
   RelationList (String data, MPXFile file)
      throws MPXException
   {
      int length = data.length();

      if (length != 0)
      {
         int start = 0;
         int end = 0;

         while (end != length)
         {
            end = data.indexOf(',', start);

            if (end == -1)
            {
               end = length;
            }

            m_list.add(new Relation(data.substring(start, end).trim(), file));

            start = end + 1;
         }
      }
   }

   /**
    * This method generates a string in MPX format representing the
    * contents of this rlist.
    *
    * @param mpx parent mpx file
    * @return string containing the data for this list in MPX format.
    */
   public String toString (MPXFile mpx)
   {
      char sepchar = mpx.getDelimiter();
      StringBuffer sb = new StringBuffer();
      Iterator iter = m_list.iterator();

      while (iter.hasNext() == true)
      {
         if (sb.length() != 0)
         {
            sb.append(sepchar);
         }

         sb.append(((Relation)iter.next()).toString(mpx));
      }

      return (sb.toString());
   }

   /**
    * @see AbstractList#iterator()
    */
   public Iterator iterator ()
   {
      return (m_list.iterator());
   }

   /**
    * @see AbstractList#add(java.lang.Object)
    */
   public boolean add (Object relation)
   {
      return (m_list.add(relation));
   }

   /**
    * @see java.util.Collection#size()
    */
   public int size()
   {
      return (m_list.size());
   }

   /**
    * @see AbstractList#get(int)
    */
   public Object get (int index)
   {
      return (m_list.get(index));
   }
   
   /**
    * List of relationships.
    */
   private LinkedList m_list = new LinkedList();
}
