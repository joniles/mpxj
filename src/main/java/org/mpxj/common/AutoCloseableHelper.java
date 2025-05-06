/*
 * file:       AutoCloseableHelper
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2020
 * date:       10/067/2020
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
 * Common helper methods for working with AutoCloseable resources.
 */
public final class AutoCloseableHelper
{
   /**
    * Close a database connection without raising an exception on error.
    *
    * @param closeable connection to close
    */
   public static final void closeQuietly(AutoCloseable closeable)
   {
      if (closeable != null)
      {
         try
         {
            closeable.close();
         }

         catch (Exception ex)
         {
            // silently ignore exceptions
         }
      }
   }
}
