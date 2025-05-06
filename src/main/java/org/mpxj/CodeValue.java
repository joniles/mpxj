/*
 * file:       CodeValue.java
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
 * Implemented by classes which represent a value forming part of a Primavera P6 code.
 */
public interface CodeValue
{
   /**
    * Retrieves the unique ID for this value.
    *
    * @return unique ID
    */
   Integer getUniqueID();

   /**
    * Retrieve the parent Code.
    *
    * @return parent Code instance
    */
   Code getParentCode();

   /**
    * Retrieve the parent code's unique ID.
    *
    * @return parent code unique ID
    */
   Integer getParentCodeUniqueID();

   /**
    * Retrieves the sequence number for this value.
    *
    * @return sequence number
    */
   Integer getSequenceNumber();

   /**
    * Retrieves the value name.
    *
    * @return value name
    */
   String getName();

   /**
    * Retrieves the value description.
    *
    * @return value description
    */
   String getDescription();

   /**
    * Retrieves the unique ID of the parent value.
    *
    * @return parent value unique ID
    */
   Integer getParentValueUniqueID();

   /**
    * Retrieves a list of child values.
    *
    * @return list of child values
    */
   List<? extends CodeValue> getChildValues();
}
