/*
 * file:       NumberUtility.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2005
 * date:       25/03/2005
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
 * This class contains utility methods for handling numeric values.
 */
public final class NumberUtility
{
   /**
    * This method retrieves an int value from a Number instance. It
    * returns zero by default if a null value is supplied.
    * 
    * @param value Number instance
    * @return int value
    */
   public static final int getInt (Number value)
   {
      return (value==null?0:value.intValue());
   }
}
