/*
 * file:       AbstractWbsFormat.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2018
 * date:       01/03/2018
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

package org.mpxj.primavera.common;

import java.util.ArrayList;
import java.util.List;

/**
 * Common methods to support reading the WBS format definition from P3 and SureTrak.
 */
public class AbstractWbsFormat
{
   /**
    * Parses a raw WBS value from the database and breaks it into
    * component parts ready for formatting.
    *
    * @param value raw WBS value
    */
   public void parseRawValue(String value)
   {
      int valueIndex = 0;
      int elementIndex = 0;
      m_elements.clear();
      while (valueIndex < value.length() && elementIndex < m_lengths.size())
      {
         int elementLength = m_lengths.get(elementIndex).intValue();
         if (elementIndex > 0)
         {
            m_elements.add(m_separators.get(elementIndex - 1));
         }
         int endIndex = valueIndex + elementLength;
         if (endIndex > value.length())
         {
            endIndex = value.length();
         }
         String element = value.substring(valueIndex, endIndex);
         m_elements.add(element);
         valueIndex += elementLength;
         elementIndex++;
      }
   }

   /**
    * Retrieves the formatted WBS value.
    *
    * @return formatted WBS value
    */
   public String getFormattedValue()
   {
      return joinElements(m_elements.size());
   }

   /**
    * Retrieves the level of this WBS code.
    *
    * @return level value
    */
   public Integer getLevel()
   {
      return Integer.valueOf((m_elements.size() + 1) / 2);
   }

   /**
    * Retrieves the formatted parent WBS value.
    *
    * @return formatted parent WBS value
    */
   public String getFormattedParentValue()
   {
      String result = null;
      if (m_elements.size() > 2)
      {
         result = joinElements(m_elements.size() - 2);
      }
      return result;
   }

   /**
    * Joins the individual WBS elements to make the formatted value.
    *
    * @param length number of elements to join
    * @return formatted WBS value
    */
   private String joinElements(int length)
   {
      StringBuilder sb = new StringBuilder();
      for (int index = 0; index < length; index++)
      {
         sb.append(m_elements.get(index));
      }
      return sb.toString();
   }

   private final List<String> m_elements = new ArrayList<>();
   protected final List<Integer> m_lengths = new ArrayList<>();
   protected final List<String> m_separators = new ArrayList<>();
}
