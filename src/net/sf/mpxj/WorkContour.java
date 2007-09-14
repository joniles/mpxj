/*
 * file:       WorkContour.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2002-2003
 * date:       12/02/2005
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

package net.sf.mpxj;

import net.sf.mpxj.utility.MpxjEnum;

/**
 * Instances of this class represent enumerated work contour values.
 */
public final class WorkContour implements MpxjEnum
{
   /**
    * Private constructor.
    *
    * @param value work contour value
    */
   private WorkContour (int value)
   {
      m_value = value;
   }

   /**
    * Retrieves the int representation of the work contour.
    *
    * @return work group value
    */
   public int getValue ()
   {
      return (m_value);
   }

   /**
    * Retrieve a WorkContour instance representing the supplied value.
    *
    * @param value work group value
    * @return WorkContour instance
    */
   public static WorkContour getInstance (int value)
   {
      WorkContour result;

      switch (value)
      {
         case BACK_LOADED_VALUE:
         {
            result = BACK_LOADED;
            break;
         }

         case FRONT_LOADED_VALUE:
         {
            result = FRONT_LOADED;
            break;
         }

         case DOUBLE_PEAK_VALUE:
         {
            result = DOUBLE_PEAK;
            break;
         }

         case EARLY_PEAK_VALUE:
         {
            result = EARLY_PEAK;
            break;
         }

         case LATE_PEAK_VALUE:
         {
            result = LATE_PEAK;
            break;
         }

         case BELL_VALUE:
         {
            result = BELL;
            break;
         }

         case TURTLE_VALUE:
         {
            result = TURTLE;
            break;
         }

         case CONTOURED_VALUE:
         {
            result = CONTOURED;
            break;
         }

         default:
         case FLAT_VALUE:
         {
            result = FLAT;
            break;
         }
      }

      return (result);
   }

   private int m_value;

   public static final int FLAT_VALUE = 0;
   public static final int BACK_LOADED_VALUE = 1;
   public static final int FRONT_LOADED_VALUE = 2;
   public static final int DOUBLE_PEAK_VALUE = 3;
   public static final int EARLY_PEAK_VALUE = 4;
   public static final int LATE_PEAK_VALUE = 5;
   public static final int BELL_VALUE = 6;
   public static final int TURTLE_VALUE = 7;
   public static final int CONTOURED_VALUE = 8;

   public static final WorkContour FLAT = new WorkContour(FLAT_VALUE);
   public static final WorkContour BACK_LOADED = new WorkContour(BACK_LOADED_VALUE);
   public static final WorkContour FRONT_LOADED = new WorkContour(FRONT_LOADED_VALUE);
   public static final WorkContour DOUBLE_PEAK = new WorkContour(DOUBLE_PEAK_VALUE);
   public static final WorkContour EARLY_PEAK = new WorkContour(EARLY_PEAK_VALUE);
   public static final WorkContour LATE_PEAK = new WorkContour(LATE_PEAK_VALUE);
   public static final WorkContour BELL = new WorkContour(BELL_VALUE);
   public static final WorkContour TURTLE = new WorkContour(TURTLE_VALUE);
   public static final WorkContour CONTOURED = new WorkContour(CONTOURED_VALUE);
}
