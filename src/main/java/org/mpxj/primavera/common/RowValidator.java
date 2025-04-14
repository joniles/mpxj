/*
 * file:       RowValidator.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2018
 * date:       01/03/2018
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

package org.mpxj.primavera.common;

import java.util.Map;

/**
 * Implementations of this interface allow additional
 * validation checks to be supplied in order to determine
 * if a row contains valid data.
 */
public interface RowValidator
{
   /**
    * Returns true if the row is valid.
    *
    * @param row row data
    * @return true if row is valid
    */
   boolean validRow(Map<String, Object> row);
}
