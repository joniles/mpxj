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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;

/**
 * This class represents a list of relationships between tasks.
 */
public final class RelationList implements ToStringRequiresFile
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
    * @param format expected format of duration component of each relation string
    * @param locale target locale
    * @throws MPXException nroamlly thrown on parse errors
    */
   RelationList (String data, MPXNumberFormat format, Locale locale)
      throws MPXException
   {
      int length = data.length();

      if (length != 0)
      {
         int start = 0;
         int end = 0;

         while (end != length)
         {
            end = data.indexOf (',', start);
            if (end == -1)
            {
               end = length;
            }

            m_list.add (new Relation (data.substring(start, end), format, locale));

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
      Locale locale = mpx.getLocale();
      StringBuffer sb = new StringBuffer ();
      Iterator iter = m_list.iterator();

      while (iter.hasNext() == true)
      {
         if (sb.length() != 0)
         {
            sb.append (sepchar);
         }

         sb.append (((Relation)iter.next()).toString(locale));
      }

      return (sb.toString());
   }

   /**
    * This method retrieves an iterator, allowing the list
    * of relationships to be traversed.
    *
    * @return an iterator
    */
   public Iterator iterator ()
   {
      return (m_list.iterator());
   }

   /**
    * This method adds a relationship to the list.
    *
    * @param relation relationship to be added
    */
   void add (Relation relation)
   {
      m_list.add (relation);
   }

   /**
    * List of relationships.
    */
   private LinkedList m_list = new LinkedList ();
}
