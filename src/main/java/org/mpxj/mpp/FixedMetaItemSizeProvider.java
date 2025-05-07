/*
 * file:       FixedMetaItemSizeProvider.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2002-2013
 * date:       27/11/2013
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

package org.mpxj.mpp;

/**
 * This interface is used to deliver the item size to be used in a FixedMeta structure.
 */
interface FixedMetaItemSizeProvider
{
   /**
    * Retrieve the item size to use for a FixedMeta structure.
    *
    * @param fileSize size of the entire file containing the FixedMeta data
    * @param itemCount number of items we are expecting in the FixedMeta structure
    * @return the item size to use
    */
   int getItemSize(int fileSize, int itemCount);
}
