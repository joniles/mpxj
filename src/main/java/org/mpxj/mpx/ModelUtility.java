/*
 * file:       ModelUtility.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2022
 * date:       2022-08-19
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

import java.util.List;

import org.mpxj.Duration;
import org.mpxj.FieldType;
import org.mpxj.Rate;
import org.mpxj.Relation;

/**
 * Common code used to create MPX task and resource models.
 */
final class ModelUtility
{
   /**
    * Determine if a field is populated with a non-default value.
    *
    * @param field field type
    * @param value field value
    * @return true if the field has a non-default value
    */
   @SuppressWarnings("unchecked") public static boolean isFieldPopulated(FieldType field, Object value)
   {
      if (value == null)
      {
         return false;
      }

      boolean populated;
      switch (field.getDataType())
      {
         case STRING:
         {
            populated = !(value instanceof String) || !((String) value).isEmpty();
            break;
         }

         case NUMERIC:
         case CURRENCY:
         {
            populated = ((Number) value).doubleValue() != 0.0;
            break;
         }

         case DURATION:
         case WORK:
         {
            populated = !(value instanceof Duration) || ((Duration) value).getDuration() != 0.0;
            break;
         }

         case RATE:
         {
            populated = ((Rate) value).getAmount() != 0.0;
            break;
         }

         case BOOLEAN:
         {
            populated = ((Boolean) value).booleanValue();
            break;
         }

         case RELATION_LIST:
         {
            populated = !((List<Relation>) value).isEmpty();
            break;
         }

         default:
         {
            populated = true;
            break;
         }
      }

      return populated;
   }
}
