/*
 * file:       RateHelper.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2022
 * date:       2022-08-15
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

package org.mpxj.common;

import java.math.BigDecimal;

import org.mpxj.Rate;
import org.mpxj.TimeUnit;
import org.mpxj.TimeUnitDefaultsContainer;

/**
 * Utility method for working with Rates.
 */
public final class RateHelper
{
   /**
    * Convert a rate to hours.
    *
    * @param defaults defaults used for conversion
    * @param rate rate to convert
    * @return converted rate
    */
   public static double convertToHours(TimeUnitDefaultsContainer defaults, Rate rate)
   {
      double amount = rate.getAmount();
      switch (rate.getUnits())
      {
         case MINUTES:
         {
            amount = amount * 60.0;
            break;
         }

         case DAYS:
         {
            amount = (amount * 60.0) / defaults.getMinutesPerDay().doubleValue();
            break;
         }

         case WEEKS:
         {
            amount = (amount * 60.0) / defaults.getMinutesPerWeek().doubleValue();
            break;
         }

         case MONTHS:
         {
            amount = (amount * 60.0) / defaults.getMinutesPerMonth().doubleValue();
            break;
         }

         case YEARS:
         {
            amount = (amount * 60.0) / (defaults.getMinutesPerWeek().intValue() * 52);
            break;
         }

         default:
         {
            break;
         }
      }

      return amount;
   }

   /**
    * Convert a rate from amount per hour to an amount per target unit.
    *
    * @param defaults defaults used for conversion
    * @param rate rate to convert
    * @param targetUnits required units
    * @return new Rate instance
    */
   public static Rate convertFromHours(TimeUnitDefaultsContainer defaults, Rate rate, TimeUnit targetUnits)
   {
      return convertFromHours(defaults, rate.getAmount(), targetUnits);
   }

   /**
    * Convert a rate from amount per hour to an amount per target unit.
    * Handles rounding in a way which provides better compatibility with MSPDI files.
    *
    * @param defaults defaults used for conversion
    * @param value rate to convert
    * @param targetUnits required units
    * @return new Rate instance
    */
   public static Rate convertFromHours(TimeUnitDefaultsContainer defaults, BigDecimal value, TimeUnit targetUnits)
   {
      if (targetUnits == TimeUnit.YEARS)
      {
         double v = ((long) (value.doubleValue() * defaults.getMinutesPerWeek().doubleValue() * 52.0)) / 60.0;
         return new Rate(NumberHelper.round(v, 2), targetUnits);
      }
      return convertFromHours(defaults, value.doubleValue(), targetUnits);
   }

   /**
    * Convert a rate from amount per hour to an amount per target unit.
    *
    * @param defaults defaults used for conversion
    * @param value rate to convert
    * @param targetUnits required units
    * @return new Rate instance
    */
   public static Rate convertFromHours(TimeUnitDefaultsContainer defaults, double value, TimeUnit targetUnits)
   {
      switch (targetUnits)
      {
         case MINUTES:
         {
            value = value / 60.0;
            break;
         }

         case DAYS:
         {
            value = (value * defaults.getMinutesPerDay().doubleValue()) / 60.0;
            break;
         }

         case WEEKS:
         {
            value = (value * defaults.getMinutesPerWeek().doubleValue()) / 60.0;
            break;
         }

         case MONTHS:
         {
            value = (value * defaults.getMinutesPerMonth().doubleValue()) / 60.0;
            break;
         }

         case YEARS:
         {
            value = (value * defaults.getMinutesPerWeek().doubleValue() * 52.0) / 60.0;
            break;
         }

         default:
         {
            break;
         }
      }

      return new Rate(NumberHelper.round(value, 2), targetUnits);
   }
}
