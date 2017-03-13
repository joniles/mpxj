/*
 * file:       UserFieldCounters.java
 * author:     Mario Fuentes
 * copyright:  (c) Packwood Software 2013
 * date:       22/03/2010
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

package net.sf.mpxj.primavera;

/**
 * Simple container holding counters used to generate field names
 * for user defined field types.
 */
class UserFieldCounters
{
   /**
    * Constructor.
    */
   public UserFieldCounters()
   {
      //
      // Set the default name for each type
      //
      for (UserFieldDataType type : UserFieldDataType.values())
      {
         m_names[type.ordinal()] = type.getDefaultFieldName();
      }
   }

   /**
    * Allow the caller to override the default field name assigned
    * to a user defined data type.
    *
    * @param type target type
    * @param fieldName field name overriding the default
    */
   public void setFieldNameForType(UserFieldDataType type, String fieldName)
   {
      m_names[type.ordinal()] = fieldName;
   }

   /**
    * Generate the next name for a user defined field.
    *
    * @param type user defined field type.
    * @return field name
    */
   public String nextName(UserFieldDataType type)
   {
      int counter = ++m_counters[type.ordinal()];
      return type.getDefaultFieldName() + counter;
   }

   /**
    * Reset the counters ready to process a new project.
    */
   public void reset()
   {
      for (int index = 0; index < m_counters.length; index++)
      {
         m_counters[index] = 0;
      }
   }

   private int[] m_counters = new int[UserFieldDataType.values().length];
   private String[] m_names = new String[UserFieldDataType.values().length];
}
