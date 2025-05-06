/*
 * file:       CustomFieldValueMask.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software
 * date:       22/09/2019
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

/**
 * One element of the mask used to define the structured content of a custom field.
 */
public class CustomFieldValueMask
{
   /**
    * Constructor.
    *
    * @param length value length
    * @param level level
    * @param separator separator
    * @param type value type
    */
   public CustomFieldValueMask(int length, int level, String separator, CustomFieldValueDataType type)
   {
      m_length = length;
      m_level = level;
      m_separator = separator;
      m_type = type;
   }

   /**
    * Retrieve the value length.
    *
    * @return value length
    */
   public int getLength()
   {
      return m_length;
   }

   /**
    * Retrieve the level.
    *
    * @return level
    */
   public int getLevel()
   {
      return m_level;
   }

   /**
    * Retrieve the separator.
    *
    * @return separator
    */
   public String getSeparator()
   {
      return m_separator;
   }

   /**
    * Retrieve the value type.
    *
    * @return value type
    */
   public CustomFieldValueDataType getType()
   {
      return m_type;
   }

   @Override public String toString()
   {
      return String.format("[CustomFieldValueMask length=%d level=%d separator=%s type=%s]", Integer.valueOf(m_length), Integer.valueOf(m_level), m_separator, m_type);
   }

   private final int m_length;
   private final int m_level;
   private final String m_separator;
   private final CustomFieldValueDataType m_type;
}
