/*
 * file:       ConstraintTypeUtility.java
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

import org.mpxj.ConstraintType;

/**
 * This class contains method relating to managing ConstraintType instances
 * for MPX files.
 */
final class ConstraintTypeUtility
{
   /**
    * Constructor.
    */
   private ConstraintTypeUtility()
   {
      // private constructor to prevent instantiation
   }

   /**
    * This method takes the textual version of a constraint name
    * and returns an appropriate class instance. Note that unrecognised
    * values are treated as "As Soon As Possible" constraints.
    *
    * @param locale target locale
    * @param type text version of the constraint type
    * @return ConstraintType instance
    */
   public static ConstraintType getInstance(Locale locale, String type)
   {
      int index = 0;

      String[] constraintTypes = LocaleData.getStringArray(locale, LocaleData.CONSTRAINT_TYPES);
      for (int loop = 0; loop < constraintTypes.length; loop++)
      {
         if (constraintTypes[loop].equalsIgnoreCase(type))
         {
            index = loop;
            break;
         }
      }

      return (ConstraintType.getInstance(index));
   }
}
