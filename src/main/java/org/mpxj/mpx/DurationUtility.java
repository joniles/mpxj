/*
 * file:       DurationUtility.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2005
 * date:       Jan 23, 2006
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

package org.mpxj.mpx;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import org.mpxj.Duration;
import org.mpxj.MPXJException;
import org.mpxj.TimeUnit;

/**
 * This class contains method relating to managing Duration instances
 * for MPX files.
 */
final class DurationUtility
{
   /**
    * Constructor.
    */
   private DurationUtility()
   {
      // private constructor to prevent instantiation
   }

   /**
    * Retrieve an Duration instance. Use shared objects to
    * represent common values for memory efficiency.
    *
    * @param dur duration formatted as a string
    * @param format number format
    * @param locale target locale
    * @return Duration instance
    */
   public static Duration getInstance(String dur, NumberFormat format, Locale locale) throws MPXJException
   {
      try
      {
         int lastIndex = dur.length() - 1;
         int index = lastIndex;
         double duration;
         TimeUnit units;

         while ((index > 0) && !Character.isDigit(dur.charAt(index)))
         {
            --index;
         }

         //
         // If we have no units suffix, assume days to allow for MPX3
         //
         if (index == lastIndex)
         {
            duration = format.parse(dur).doubleValue();
            units = TimeUnit.DAYS;
         }
         else
         {
            ++index;
            duration = format.parse(dur.substring(0, index)).doubleValue();
            while ((index < lastIndex) && (Character.isWhitespace(dur.charAt(index))))
            {
               ++index;
            }
            units = TimeUnitUtility.getInstance(dur.substring(index), locale);
         }

         return (Duration.getInstance(duration, units));
      }

      catch (ParseException ex)
      {
         throw new MPXJException("Failed to parse duration", ex);
      }
   }
}
