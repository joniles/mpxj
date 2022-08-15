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

package net.sf.mpxj.common;

import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Rate;
import net.sf.mpxj.TimeUnit;

/**
 * Utility method for working with Rates.
 */
public final class RateHelper
{
   /**
    * Convert a rate to hours.
    *
    * @param file parent file
    * @param rate rate to convert
    * @return converted rate
    */
   public static double convertToHours(ProjectFile file, Rate rate)
   {
      double amount = rate.getAmount();
      switch (rate.getUnits())
      {
         case MINUTES:
         {
            amount = amount / 60.0;
            break;
         }

         case DAYS:
         {
            amount = (amount * 60.0) / file.getProjectProperties().getMinutesPerDay().doubleValue();
            break;
         }

         case WEEKS:
         {
            amount = (amount * 60.0) / file.getProjectProperties().getMinutesPerWeek().doubleValue();
            break;
         }

         case MONTHS:
         {
            amount = (amount * 60.0) / file.getProjectProperties().getMinutesPerMonth().doubleValue();
            break;
         }

         case YEARS:
         {
            amount = (amount * 60.0) / (file.getProjectProperties().getMinutesPerWeek().intValue() * 52);
            break;
         }

         default:
         {
            break;
         }
      }

      return amount;
   }

   public static Rate convertFromHours(ProjectFile file, Rate rate, TimeUnit targetUnits)
   {
      return convertFromHours(file, rate.getAmount(), targetUnits);
   }

   public static Rate convertFromHours(ProjectFile file, double value, TimeUnit targetUnits)
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
            value = (value * file.getProjectProperties().getMinutesPerDay().doubleValue()) / 60.0;
            break;
         }

         case WEEKS:
         {
            value = (value * file.getProjectProperties().getMinutesPerWeek().doubleValue()) / 60.0;
            break;
         }

         case MONTHS:
         {
            value = (value * file.getProjectProperties().getMinutesPerMonth().doubleValue()) / 60.0;
            break;
         }

         case YEARS:
         {
            value = (value * file.getProjectProperties().getMinutesPerWeek().doubleValue() * 52.0) / 60.0;
            break;
         }
      }

      return new Rate(NumberHelper.round(value, 2), targetUnits);
   }
}
