/*
 * file:       ByteArray.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2002-2003
 * date:       03/01/2003
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

package com.tapsterrock.mpp;

/**
 * This class provides a simple object wrapper around a byte array
 * allowing it to be stored as part of a Collection.
 *
 * Note that this class has package level access only, and is not intended
 * for use outside of this context.
 */
class ByteArray
{
   /**
    * Constructor
    *
    * @param data byte array to be wrapped
    */
   public ByteArray (byte[] data)
   {
      m_data = data;
   }

	/**
	 * Constructor, creates a byte array from a subsection of 
	 * another byte array.
	 * 
	 * @param data Original byte array
	 * @param offset Offset into the original array
	 * @param size Amount of data to copy into the new array
	 */
   public ByteArray (byte[] data, int offset, int size)
   {
      m_data = new byte[size];
      for (int loop=0; loop < size; loop++)
      {
       	m_data[loop] = data[offset+loop];  
      }
   }

   /**
    * Accessor method allowing the wrapped byte array to be retrieved.
    *
    * @return the wrapped byte array
    */
   public byte[] byteArrayValue ()
   {
      return (m_data);
   }

   /**
    * Internal storage for the byte array.
    */
   private byte[] m_data;
}
