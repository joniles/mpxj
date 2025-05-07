/*
 * file:       StructuredTextParseException.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2022
 * date:       06/02/2022
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

/**
 * This exception is raised if an unexpected structure is encountered
 * when parsing Primavera structured text.
 */
public class StructuredTextParseException extends RuntimeException
{
   /**
    * Constructor.
    *
    * @param message detail message
    */
   public StructuredTextParseException(String message)
   {
      super(message);
   }
}
