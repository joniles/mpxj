/*
 * file:       PercentCompleteType.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2015
 * date:       24/02/2015
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

import java.util.HashMap;
import java.util.Map;

/**
 * Percent complete type used by Primavera.
 */
public enum PercentCompleteType
{
   DURATION("CP_Drtn"),
   PHYSICAL("CP_Phys"),
   UNITS("CP_Units");

   /**
    * Retrieve a PercentCompleteType type based on its name.
    *
    * @param value name
    * @return PercentCompleteType instance
    */
   public static PercentCompleteType getInstance(String value)
   {
      return VALUE_MAP.get(value);
   }

   /**
    * Constructor.
    *
    * @param value name
    */
   private PercentCompleteType(String value)
   {
      m_value = value;
   }

   private final String m_value;

   private static final Map<String, PercentCompleteType> VALUE_MAP = new HashMap<String, PercentCompleteType>();
   static
   {
      for (PercentCompleteType e : PercentCompleteType.values())
      {
         VALUE_MAP.put(e.m_value, e);
      }
   }
}
