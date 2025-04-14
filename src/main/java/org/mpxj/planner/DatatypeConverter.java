/*
 * file:       DatatypeConverter.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2022
 * date:       01/12/2022
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

package org.mpxj.planner;

import org.mpxj.common.XmlHelper;

/**
 * This class contains methods used to perform the datatype conversions
 * required to read and write Planner files.
 */
public final class DatatypeConverter
{
   /**
    * Format string for output.
    *
    * @param value string value
    * @return formatted string
    */
   public static final String printString(String value)
   {
      // JAXB should do this... but doesn't
      return XmlHelper.replaceInvalidXmlChars(value);
   }

   /**
    * Parse string.
    *
    * @param value string value
    * @return parsed string
    */
   public static final String parseString(String value)
   {
      return value;
   }
}
