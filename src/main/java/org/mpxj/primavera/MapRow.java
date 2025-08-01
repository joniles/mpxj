/*
 * file:       MapRow.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2010
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

package org.mpxj.primavera;

import java.util.Map;

/**
 * Implementation of the Row interface, wrapping a Map.
 */
class MapRow extends AbstractRow
{
   /**
    * Constructor.
    *
    * @param map map to be wrapped by this instance
    * @param ignoreErrors true if errors reading values are ignored
    */
   public MapRow(Map<String, Object> map, boolean ignoreErrors)
   {
      super(ignoreErrors);
      m_map = map;
   }

   /**
    * Retrieve a value from the map.
    *
    * @param name column name
    * @return column value
    */
   @Override protected Object getObject(String name)
   {
      return m_map.get(name);
   }

   protected final Map<String, Object> m_map;
}
