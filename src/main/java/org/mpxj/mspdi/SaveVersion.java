/*
 * file:       SaveVersion.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2011
 * date:       03/11/2011
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

package org.mpxj.mspdi;

/**
 * Instances of this class represent enumerated versions of Microsoft
 * Project which can read and write MSPDI files.
 */
public enum SaveVersion
{
   Project2002(9),
   Project2003(9),
   Project2007(12),
   Project2010(14),
   Project2013(14),
   Project2016(14);

   /**
    * Private constructor.
    *
    * @param type int version of the enum
    */
   SaveVersion(int type)
   {
      m_value = type;
   }

   /**
    * Accessor method used to retrieve the numeric representation of the enum.
    *
    * @return int representation of the enum
    */
   public int getValue()
   {
      return (m_value);
   }

   /**
    * Returns a string representation of the date order type
    * to be used as part of an MPX file.
    *
    * @return string representation
    */
   @Override public String toString()
   {
      return (Integer.toString(m_value));
   }

   /**
    * Internal representation of the enum int type.
    */
   private final int m_value;
}
