/*
 * file:       WebServicesException.java
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

package org.mpxj.primavera.webservices;

/**
 * General exception thrown when P6 Web Services API calls do not work as expected.
 */
public class WebServicesException extends RuntimeException
{
   /**
    * Constructor.
    */
   public WebServicesException()
   {
      super();
   }

   /**
    * Constructor.
    *
    * @param ex cause
    */
   public WebServicesException(Exception ex)
   {
      super(ex);
   }

   /**
    * Constructor.
    *
    * @param message message
    */
   public WebServicesException(String message)
   {
      super(message);
   }
}
