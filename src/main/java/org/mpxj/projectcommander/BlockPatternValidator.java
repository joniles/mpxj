/*
 * file:       BlockPatternValidator.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2020
 * date:       24/05/2020
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

package org.mpxj.projectcommander;

import java.util.Set;

/**
 * Represents a validator which can be used to determine if a potential
 * match for the start of a block is valid in the location it appears
 * at in the file.
 */
interface BlockPatternValidator
{
   /**
    * Returns true if the start of the block is likely to be valid given
    * the location it appears in the file.
    *
    * @param matchedPatternNames set of block names read so far
    * @return true if the block start is valid
    */
   boolean valid(Set<String> matchedPatternNames);
}
