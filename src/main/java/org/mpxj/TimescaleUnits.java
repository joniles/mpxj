/*
 * file:       TimescaleUnits.java
 * author:     Jon Iles
 * date:       2005-04-07
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
 * Class representing the units which may be shown on a timescale.
 */
public enum TimescaleUnits
{
   NONE("None"),
   MINUTES("Minutes"),
   HOURS("Hours"),
   DAYS("Days"),
   WEEKS("Weeks"),
   THIRDS_OF_MONTHS("Thirds of Months"),
   MONTHS("Months"),
   QUARTERS("Quarters"),
   HALF_YEARS("Half Years"),
   YEARS("Years");

   /**
    * Private constructor.
    *
    * @param name enum name
    */
   TimescaleUnits(String name)
   {
      m_name = name;
   }

   /**
    * Retrieve the name of this time unit. Note that this is not
    * localised.
    *
    * @return name of this timescale unit
    */
   public String getName()
   {
      return m_name;
   }

   /**
    * Generate a string representation of this instance.
    *
    * @return string representation of this instance
    */
   @Override public String toString()
   {
      return m_name;
   }

   private final String m_name;
}
