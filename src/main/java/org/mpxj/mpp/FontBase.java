/*
 * file:       FontBase.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2005
 * date:       May 24, 2005
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

package org.mpxj.mpp;

/**
 * The standard font definitions used by MS Project are split into two parts,
 * the base font (i.e. the name and the size) and the styles applied to that
 * font. This class represents the base font information.
 */
public class FontBase
{
   /**
    * Constructor.
    *
    * @param index index number for this font
    * @param name font name
    * @param size font size
    */
   public FontBase(Integer index, String name, int size)
   {
      m_index = index;
      m_name = name;
      m_size = size;
   }

   /**
    * Retrieve the font name.
    *
    * @return font name
    */
   public String getName()
   {
      return (m_name);
   }

   /**
    * Retrieve the font size.
    *
    * @return font size
    */
   public int getSize()
   {
      return (m_size);
   }

   /**
    * Retrieve the index number associated with this base font.
    *
    * @return index number
    */
   public Integer getIndex()
   {
      return (m_index);
   }

   @Override public String toString()
   {
      return ("[FontBase name=" + m_name + " size=" + m_size + "]");
   }

   private final Integer m_index;
   private final String m_name;
   private final int m_size;
}
