/*
 * file:       TaskType.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2002-2003
 * date:       30/11/2004
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
 * Instances of this class represent enumerated task type values.
 */
public final class TaskType
{
   /**
    * Private constructor.
    *
    * @param value task type value
    */
   private TaskType (int value)
   {
      m_value = value;
   }

   /**
    * Retrieves the int representation of the task type.
    *
    * @return task type value
    */
   public int getValue ()
   {
      return (m_value);
   }

   /**
    * Retrieve a TaskType instance representing the supplied value.
    *
    * @param value task type value
    * @return TaskType instance
    */
   public static TaskType getInstance (int value)
   {
      TaskType result;

      switch (value)
      {
         case FIXED_UNITS_VALUE:
         {
            result = FIXED_UNITS;
            break;
         }

         case FIXED_DURATION_VALUE:
         {
            result = FIXED_DURATION;
            break;
         }

         default:
         case FIXED_WORK_VALUE:
         {
            result = FIXED_WORK;
            break;
         }
      }

      return (result);
   }



   private int m_value;

   /**
    * Constant representing Fixed Units
    */
   public static final int FIXED_UNITS_VALUE = 0;

   /**
    * Constant representing Fixed Duration
    */
   public static final int FIXED_DURATION_VALUE = 1;

   /**
    * Constant representing Fixed Work
    */
   public static final int FIXED_WORK_VALUE = 2;


   /**
    * Constant representing Fixed Units
    */
   public static final TaskType FIXED_UNITS = new TaskType(FIXED_UNITS_VALUE);

   /**
    * Constant representing Fixed Duration
    */
   public static final TaskType FIXED_DURATION = new TaskType(FIXED_DURATION_VALUE);

   /**
    * Constant representing Fixed Work
    */
   public static final TaskType FIXED_WORK = new TaskType(FIXED_WORK_VALUE);

}
