/*
 * file:       AccrueTypeUtility.java
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

import org.mpxj.AccrueType;

/**
 * This class contains method relating to managing AccrueType instances
 * for MPX files.
 */
final class AccrueTypeUtility
{
   /**
    * Constructor.
    */
   private AccrueTypeUtility()
   {
      // private constructor to prevent instantiation
   }

   /**
    * This method takes the textual version of an accrue type name
    * and populates the class instance appropriately. Note that unrecognised
    * values are treated as "Prorated".
    *
    * @param type text version of the accrue type
    * @param locale target locale
    * @return AccrueType class instance
    */
   public static AccrueType getInstance(String type, Locale locale)
   {
      AccrueType result = null;

      String[] typeNames = LocaleData.getStringArray(locale, LocaleData.ACCRUE_TYPES);

      for (int loop = 0; loop < typeNames.length; loop++)
      {
         if (typeNames[loop].equalsIgnoreCase(type))
         {
            result = AccrueType.getInstance(loop + 1);
            break;
         }
      }

      if (result == null)
      {
         result = AccrueType.PRORATED;
      }

      return (result);
   }
}
