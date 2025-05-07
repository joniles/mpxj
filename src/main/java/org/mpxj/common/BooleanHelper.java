/*
 * file:       BooleanHelper.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2005
 * date:       Mar 30, 2005
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

package org.mpxj.common;

/**
 * This class contains utility methods related to Boolean objects and
 * boolean primitives.
 */
public final class BooleanHelper
{
   /**
    * Retrieve a boolean value from a Boolean object. Handles null values.
    *
    * @param value Boolean instance
    * @return boolean value
    */
   public static final boolean getBoolean(Boolean value)
   {
      return ((value != null && value.booleanValue()));
   }
}
