/*
 * file:       RowHeader.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2012
 * date:       29/04/2012
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

package org.mpxj.asta;

import java.util.ArrayList;

/**
 * Used to parse and represent the header data present at the
 * start of each line in an Asta PP file.
 */
class RowHeader
{
   /**
    * Constructor.
    *
    * @param header header text
    */
   public RowHeader(String header)
   {
      parse(header);
   }

   /**
    * Parses values out of the header text.
    *
    * @param header header text
    */
   private void parse(String header)
   {
      ArrayList<String> list = new ArrayList<>(4);
      StringBuilder sb = new StringBuilder();
      int index = 1;
      while (index < header.length())
      {
         char c = header.charAt(index++);
         if (Character.isDigit(c))
         {
            sb.append(c);
         }
         else
         {
            if (sb.length() != 0)
            {
               list.add(sb.toString());
               sb.setLength(0);
            }
         }
      }

      if (sb.length() != 0)
      {
         list.add(sb.toString());
      }

      m_id = list.get(0);
      m_sequence = Integer.parseInt(list.get(1));
      m_type = Integer.valueOf(list.get(2));
      if (list.size() > 3)
      {
         m_subtype = Integer.parseInt(list.get(3));
      }
   }

   /**
    * Retrieve the ID value of this row.
    *
    * @return ID value
    */
   public String getID()
   {
      return m_id;
   }

   /**
    * Retrieve the sequence value of this row.
    *
    * @return sequence value
    */
   public int getSequence()
   {
      return m_sequence;
   }

   /**
    * Retrieve the type of table this row belongs to.
    *
    * @return table type
    */
   public Integer getType()
   {
      return m_type;
   }

   /**
    * Retrieve the "subtype" of this row.
    *
    * @return row sub type
    */
   public int getSubtype()
   {
      return m_subtype;
   }

   @Override public String toString()
   {
      return "[RowHeader id=" + m_id + " sequence=" + m_sequence + " type=" + m_type + " subtype=" + m_subtype + "]";
   }

   private String m_id;
   private int m_sequence;
   private Integer m_type;
   private int m_subtype;
}
