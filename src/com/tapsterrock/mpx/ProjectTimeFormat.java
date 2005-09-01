/*
 * file:       ProjectTimeFormat.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2002-2003
 * date:       04/01/2005
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

package com.tapsterrock.mpx;

/**
 * Instances of this class represent enumerated time format values.
 */
public final class ProjectTimeFormat
{
   /**
    * Private constructor.
    *
    * @param value time format value
    */
   private ProjectTimeFormat (int value)
   {
      m_value = value;
   }

   /**
    * Retrieves the int representation of the time format.
    *
    * @return time format value
    */
   public int getValue ()
   {
      return (m_value);
   }

   /**
    * Retrieve a TimeFormat instance representing the supplied value.
    *
    * @param value time format value
    * @return TimeFormat instance
    */
   public static ProjectTimeFormat getInstance (int value)
   {
      ProjectTimeFormat result;

      switch (value)
      {
         case TWENTY_FOUR_HOUR_VALUE:
         {
            result = TWENTY_FOUR_HOUR;
            break;
         }

         default:
         case TWELVE_HOUR_VALUE:
         {
            result = TWELVE_HOUR;
            break;
         }
      }

      return (result);
   }


   /**
    * Returns a string representation of the time format type
    * to be used as part of an MPX file.
    *
    * @return string representation
    */
   public String toString ()
   {
      return (Integer.toString(m_value));
   }

   private int m_value;

   /**
    * Constant representing 12 hour time.
    */
   public static final int TWELVE_HOUR_VALUE = 0;

   /**
    * Constant representing 24 hour time.
    */
   public static final int TWENTY_FOUR_HOUR_VALUE = 1;

   /**
    * Constant representing 12 hour time.
    */
   public static final ProjectTimeFormat TWELVE_HOUR = new ProjectTimeFormat(TWELVE_HOUR_VALUE);

   /**
    * Constant representing 24 hour time.
    */
   public static final ProjectTimeFormat TWENTY_FOUR_HOUR = new ProjectTimeFormat(TWENTY_FOUR_HOUR_VALUE);

}
