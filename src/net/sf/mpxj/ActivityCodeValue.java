/*
 * file:       ActivityCodeValue.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2018
 * date:       18/06/2018
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

package net.sf.mpxj;

/**
 * Represents an individual activity code value.
 */
public class ActivityCodeValue
{
   /**
    * Constructor.
    *
    * @param type parent activity code type
    * @param uniqueID unique ID
    * @param name value name
    * @param description value description
    */
   public ActivityCodeValue(ActivityCode type, Integer uniqueID, String name, String description)
   {
      m_type = type;
      m_uniqueID = uniqueID;
      m_name = name;
      m_description = description;
   }

   /**
    * Retrieves the parent activity code type.
    *
    * @return ActivityCode instance
    */
   public ActivityCode getType()
   {
      return m_type;
   }

   /**
    * Retrieves the unique ID for this value.
    *
    * @return unique ID
    */
   public Integer getUniqueID()
   {
      return m_uniqueID;
   }

   /**
    * Retrieves the value name.
    *
    * @return value name
    */
   public String getName()
   {
      return m_name;
   }

   /**
    * Retrieves the value description.
    *
    * @return value description
    */
   public String getDescription()
   {
      return m_description;
   }

   @Override public String toString()
   {
      return m_type.getName() + ": " + m_name;
   }

   private final ActivityCode m_type;
   private final Integer m_uniqueID;
   private final String m_name;
   private final String m_description;
}
