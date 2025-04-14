/*
 * file:       MicrosoftProjectConstants.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2021
 * date:       15/06/2021
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
 * Container for constants specific to file types read/written by Microsoft Project.
 */
public final class MicrosoftProjectConstants
{
   /**
    * Private constructor to prevent instantiation.
    */
   private MicrosoftProjectConstants()
   {

   }

   /**
    * Maximum unique ID value MS Project will accept.
    */
   public static final int MAX_UNIQUE_ID = 0x1FFFFF;

   public static final Integer ASSIGNMENT_NULL_RESOURCE_ID = Integer.valueOf(-65535);
}
