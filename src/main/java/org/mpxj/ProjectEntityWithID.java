/*
 * file:       ProjectEntityWithID.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2002-2015
 * date:       15/04/2015
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

package org.mpxj;

/**
 * Implemented by entities which can be identified by an ID.
 */
public interface ProjectEntityWithID extends ProjectEntityWithMutableUniqueID
{
   /**
    * Retrieve the ID value of the entity.
    *
    * @return ID value
    */
   Integer getID();

   /**
    * Set the ID value of the entity.
    *
    * @param id ID value
    */
   void setID(Integer id);
}
