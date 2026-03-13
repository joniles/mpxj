/*
 * file:       ResourceType.java
 * author:     Jon Iles
 * date:       2004-11-25
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
 * Instances of this class represent enumerated resource type values.
 */
public enum ResourceType
{
   MATERIAL("Material", false),
   WORK( "Work", true),
   COST( "Cost", false);

   /**
    * Private constructor.
    *
    * @param name enum name
    * @param timeBased true if this is a time-based type
    */
   ResourceType(String name, boolean timeBased)
   {
      m_name = name;
      m_timeBased = timeBased;
   }

   /**
    * Returns true if this resource type is time-based.
    *
    * @return true if this resource type is time-based
    */
   public boolean isTimeBased()
   {
      return m_timeBased;
   }

   @Override public String toString()
   {
      return (m_name);
   }

   private final String m_name;
   private final boolean m_timeBased;
}
