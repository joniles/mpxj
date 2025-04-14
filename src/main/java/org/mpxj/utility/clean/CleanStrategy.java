/*
 * file:       CleanStrategy.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2022
 * date:       03/01/2022
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

package org.mpxj.utility.clean;

/**
 * Implemented by classes which provide a strategy for anonymizing text.
 */
public interface CleanStrategy
{
   /**
    * This method transforms the supplied text to make
    * it anonymous, using the chosen strategy.
    *
    * @param oldText original text
    * @return transformed text
    */
   String generateReplacementText(String oldText);
}
