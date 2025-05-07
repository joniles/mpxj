/*
 * file:       Code.java
 * author:     Jon Iles
 * date:       2024-02-27
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

package org.mpxj.openplan;

import java.util.List;

/**
 * Represents a code with a fixed set of values.
 */
class Code
{
   /**
    * Constructor.
    *
    * @param id code ID
    * @param promptText prompt text
    * @param description description
    * @param values code values
    */
   public Code(String id, String promptText, String description, List<CodeValue> values)
   {
      m_id = id;
      m_promptText = promptText;
      m_description = description;
      m_values = values;
   }

   /**
    * Retrieve the code ID.
    *
    * @return code ID
    */
   public String getID()
   {
      return m_id;
   }

   /**
    * Retrieve the prompt text.
    *
    * @return prompt text
    */
   public String getPromptText()
   {
      return m_promptText;
   }

   /**
    * Retrieve the description.
    *
    * @return description
    */
   public String getDescription()
   {
      return m_description;
   }

   /**
    * Retrieve the code's values.
    *
    * @return code values
    */
   public List<CodeValue> getValues()
   {
      return m_values;
   }

   private final String m_id;
   private final String m_promptText;
   private final String m_description;
   private final List<CodeValue> m_values;
}
