/*
 * file:       GraphicalIndicatorContainer.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2015
 * date:       27/04/2015
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

import java.util.HashMap;
import java.util.Map;

/**
 * Manages the collection of graphical indicators belonging to a project.
 */
public class GraphicalIndicatorContainer
{
   /**
    * Adds the definition of a graphical indicator for a field type.
    * 
    * @param field field type
    * @param indicator graphical indicator definition
    */
   public void addGraphicalIndicator(FieldType field, GraphicalIndicator indicator)
   {
      m_graphicalIndicators.put(field, indicator);
   }

   /**
    * Retrieves the definition of any graphical indicators used for the
    * given field type.
    * 
    * @param field field type
    * @return graphical indicator definition
    */
   public GraphicalIndicator getGraphicalIndicator(FieldType field)
   {
      return (m_graphicalIndicators.get(field));
   }

   /**
    * Map of graphical indicator data.
    */
   private Map<FieldType, GraphicalIndicator> m_graphicalIndicators = new HashMap<FieldType, GraphicalIndicator>();
}
