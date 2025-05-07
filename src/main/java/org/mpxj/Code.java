/*
 * file:       Code.java
 * author:     Jon Iles
 * date:       2024-12-10
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

import java.util.List;

/**
 * Interface implemented by classes representing Primavera P6 codes.
 */
public interface Code extends ProjectEntityWithUniqueID
{
   /**
    * Retrieve the project code unique ID.
    *
    * @return unique ID
    */
   @Override Integer getUniqueID();

   /**
    * Retrieve the sequence number of this project code.
    *
    * @return sequence number
    */
   Integer getSequenceNumber();

   /**
    * Retrieve the project code name.
    *
    * @return name
    */
   String getName();

   /**
    * Retrieve the secure flag.
    *
    * @return secure flag
    */
   boolean getSecure();

   /**
    * Retrieve the max length.
    *
    * @return max length
    */
   Integer getMaxLength();

   /**
    * Retrieve all values for this code.
    *
    * @return list of all values
    */
   List<? extends CodeValue> getValues();

   /**
    * Retrieve the immediate child values for this code.
    *
    * @return list of child values
    */
   List<? extends CodeValue> getChildValues();
}
