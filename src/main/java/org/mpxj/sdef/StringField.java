/*
 * file:       StringField.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2019
 * date:       01/07/2019
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

package org.mpxj.sdef;

/**
 * SDEF string field.
 */
class StringField implements SDEFField
{
   /**
    * Constructor.
    *
    * @param name field name
    * @param length field length
    */
   public StringField(String name, int length)
   {
      m_name = name;
      m_length = length;
   }

   /**
    * Retrieve the field name.
    *
    * @return field name
    */
   public String getName()
   {
      return m_name;
   }

   @Override public int getLength()
   {
      return m_length;
   }

   @Override public Object read(String line, int offset)
   {
      String result;
      if (offset + m_length > line.length())
      {
         result = null;
      }
      else
      {
         result = line.substring(offset, offset + m_length).trim();
      }
      return result;
   }

   private final String m_name;
   private final int m_length;
}
