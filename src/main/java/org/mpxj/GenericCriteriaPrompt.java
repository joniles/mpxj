/*
 * file:       GenericCriteriaPrompt.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2010
 * date:       26/04/2010
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
 * Represents a prompt to the user as part of filter criteria.
 */
public final class GenericCriteriaPrompt
{
   /**
    * Constructor.
    *
    * @param type prompt data type
    * @param prompt text
    */
   public GenericCriteriaPrompt(DataType type, String prompt)
   {
      m_type = type;
      m_prompt = prompt;
   }

   /**
    * Retrieve the data type of the expected value.
    *
    * @return data type
    */
   public DataType getType()
   {
      return m_type;
   }

   /**
    * Retrieves the prompt text.
    *
    * @return prompt text
    */
   public String getPrompt()
   {
      return m_prompt;
   }

   @Override public String toString()
   {
      return "PROMPT(" + m_prompt + ")";
   }

   private final DataType m_type;
   private final String m_prompt;
}
