/*
 * file:       MPXDuration.java
 * author:     Scott Melville
 *             Jon Iles
 * copyright:  (c) Tapster Rock Limited 2002-2003
 * date:       15/08/2002
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

import java.text.DecimalFormat;

/**
 * This represents time durations as specified in an MPX file.
 */
public class MPXDuration
{
   /**
    * Constructs an instance of this class from a String representation
    * of a duration.
    *
    * @param dur String representation of a duration
    * @throws MPXException normally indicating that parsing the string has failed
    */
   public MPXDuration (String dur)
      throws MPXException
   {
      int index = dur.length() - 1;

      while (index > 0 && Character.isDigit(dur.charAt(index)) == false)
      {
         --index;
      }

      if (index == -1)
      {
         throw new MPXException (MPXException.INVALID_DURATION + " " + dur);
      }

      ++index;

      m_duration = Double.parseDouble(dur.substring(0, index));
      m_type = TimeUnit.parse(dur.substring(index));
   }

   /**
    * Constructs an instance of this class from a duration amount and
    * time unit type.
    *
    * @param duration amount of duration
    * @param type time unit of duration
    */
   public MPXDuration (double duration, int type)
   {
      m_duration = duration;
      m_type = type;
   }


   /**
    * This method generates a string in MPX format representing the
    * contents of this record.
    *
    * @return string containing the data for this record in MPX format.
    */
   public String toString ()
   {
      return (FLOAT_FORMAT.format(m_duration) + TimeUnit.format(m_type));
   }

   private double m_duration;

   private int m_type;

   private static final DecimalFormat FLOAT_FORMAT = new DecimalFormat ("#");
}