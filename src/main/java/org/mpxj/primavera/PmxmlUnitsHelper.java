/*
 * file:       PmxmlUnitsHelper.java
 * author:     Jon Iles
 * date:       2023-12-15
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

import org.mpxj.ResourceAssignment;

/**
 * Calculating planned and remaining units ready for PMXML files.
 */
class PmxmlUnitsHelper extends AbstractUnitsHelper
{
   /**
    * Constructor.
    *
    * @param assignment resource assignment
    */
   public PmxmlUnitsHelper(ResourceAssignment assignment)
   {
      super(assignment);
   }

   @Override protected double getScale()
   {
      return 100.0;
   }
}
