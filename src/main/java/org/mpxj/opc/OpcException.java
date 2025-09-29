/*
 * file:       OpcException.java
 * author:     Jon Iles
 * date:       2025-07-09
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

package org.mpxj.opc;

/**
 * General exception thrown when OPC API calls do not work as expected.
 */
public class OpcException extends RuntimeException
{
   /**
    * Constructor.
    */
   public OpcException()
   {
      super();
   }

   /**
    * Constructor.
    *
    * @param ex cause
    */
   public OpcException(Exception ex)
   {
      super(ex);
   }

   /**
    * Constructor.
    *
    * @param message message
    */
   public OpcException(String message)
   {
      super(message);
   }
}
