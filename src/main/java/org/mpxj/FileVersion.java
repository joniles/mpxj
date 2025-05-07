/*
 * file:       FileVersion.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2002-2005
 * date:       17/03/2005
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
 * Instances of this class represent enumerated file version values.
 */
public enum FileVersion implements MpxjEnum
{
   VERSION_1_0(1),
   VERSION_3_0(3),
   VERSION_4_0(4);

   /**
    * Private constructor.
    *
    * @param value file version value
    */
   FileVersion(int value)
   {
      m_value = value;
   }

   /**
    * Retrieves the int representation of the file version.
    *
    * @return file version value
    */
   @Override public int getValue()
   {
      return (m_value);
   }

   /**
    * Retrieve a FileVersion instance representing the supplied value.
    *
    * @param value file version value
    * @return FileVersion instance
    */
   public static FileVersion getInstance(String value)
   {
      FileVersion result = VERSION_4_0;

      if (value != null)
      {
         if (!value.startsWith("4"))
         {
            if (value.startsWith("3"))
            {
               result = VERSION_3_0;
            }
            else
            {
               if (value.startsWith("1"))
               {
                  result = VERSION_1_0;
               }
            }
         }
      }

      return (result);
   }

   /**
    * Retrieve the string representation of this file type.
    *
    * @return string representation of the file type
    */
   @Override public String toString()
   {
      String result;

      switch (m_value)
      {
         case 1:
         {
            result = "1.0";
            break;
         }

         case 3:
         {
            result = "3.0";
            break;
         }

         case 4:
         default:
         {
            result = "4.0";
            break;
         }
      }

      return (result);
   }

   private final int m_value;
}
