/*
 * file:       TimeUnitUtility.java
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

import java.util.Locale;
import java.util.Map;

import org.mpxj.MPXJException;
import org.mpxj.TimeUnit;

/**
 * This class contains method relating to managing TimeUnit instances
 * for MPX files.
 */
final class TimeUnitUtility
{
   /**
    * Constructor.
    */
   private TimeUnitUtility()
   {
      // private constructor to prevent instantiation
   }

   /**
    * This method is used to parse a string representation of a time
    * unit, and return the appropriate constant value.
    *
    * @param units string representation of a time unit
    * @param locale target locale
    * @return numeric constant
    * @throws MPXJException normally thrown when parsing fails
    */
   @SuppressWarnings("unchecked") public static TimeUnit getInstance(String units, Locale locale) throws MPXJException
   {
      Map<String, Integer> map = LocaleData.getMap(locale, LocaleData.TIME_UNITS_MAP);
      Integer result = map.get(units.toLowerCase());
      if (result == null)
      {
         throw new MPXJException(MPXJException.INVALID_TIME_UNIT + " " + units);
      }
      return (TimeUnit.getInstance(result.intValue()));
   }
}
