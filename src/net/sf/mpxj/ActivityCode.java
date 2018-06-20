/*
 * file:       ActivityCode.java
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

import java.util.ArrayList;
import java.util.List;

/**
 * Activity code type definition, contains a list of the valid
 * values for this activity code.
 */
public class ActivityCode
{
   /**
    * Constructor.
    *
    * @param uniqueID activity code unique ID
    * @param name activity code name
    */
   public ActivityCode(Integer uniqueID, String name)
   {
      m_uniqueID = uniqueID;
      m_name = name;
   }

   /**
    * Retrieve the activity code unique ID.
    *
    * @return unique ID
    */
   public Integer getUniqueID()
   {
      return m_uniqueID;
   }

   /**
    * Retrieve the activity code name.
    *
    * @return name
    */
   public String getName()
   {
      return m_name;
   }

   /**
    * Add a value to this activity code.
    *
    * @param uniqueID value unique ID
    * @param name value name
    * @param description value description
    * @return ActivityCodeValue instance
    */
   public ActivityCodeValue addValue(Integer uniqueID, String name, String description)
   {
      ActivityCodeValue value = new ActivityCodeValue(this, uniqueID, name, description);
      m_values.add(value);
      return value;
   }

   /**
    * Retrieve a list of values for this actibity code.
    *
    * @return list of ActivityCodeValue instances
    */
   public List<ActivityCodeValue> getValues()
   {
      return m_values;
   }

   private final Integer m_uniqueID;
   private final String m_name;
   private final List<ActivityCodeValue> m_values = new ArrayList<ActivityCodeValue>();
}
