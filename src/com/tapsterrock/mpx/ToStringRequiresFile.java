/*
 * file:       ToStringRequiresFile.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2002-2003
 * date:       19/04/2004
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

package com.tapsterrock.mpx;

/**
 * This interface is used to identify classes which require the parent MPX
 * file to be passed as an argument to the toString method in order to
 * correctly generate the text to appear in an MPX file.
 */
interface ToStringRequiresFile
{
   /**
    * Generate a string representation of a set of data, suitable for 
    * inclusion in an MPX file.
    * 
    * @param mpx parent mpx file
    * @return string representation of data
    */
   public String toString (MPXFile mpx);
}
