/*
 * file:       ScheduleFrom.java
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
 * Instances of this class represent enumerated schedule from values.
 */
public final class ScheduleFrom
{
   /**
    * Private constructor.
    * 
    * @param value schedule from value
    */
   private ScheduleFrom (int value)
   {
      m_value = value;
   }

   /**
    * Retrieves the int representation of the schedule from value.
    * 
    * @return schedule from value
    */
   public int getValue ()
   {
      return (m_value);
   }
   
   /**
    * Retrieve a ScheduleFrom instance representing the supplied value.
    * 
    * @param value schedule from value
    * @return ScheduleFrom instance
    */
   public static ScheduleFrom getInstance (int value)
   {
      ScheduleFrom result;
      
      switch (value)
      {
         case FINISH_VALUE:
         {
            result = FINISH;
            break;
         }
         
         default:         
         case START_VALUE:
         {
            result = START;
            break;
         }            
      }
      
      return (result);
   }
   
   /**
    * Returns a string representation of the schedule from type
    * to be used as part of an MPX file.
    * 
    * @return string representation
    */
   public String toString ()
   {
      return (Integer.toString(m_value));
   }
   
   private int m_value;
   
   /**
    * Constant representing Schedule From Start
    */
   public static final int START_VALUE = 0;

   /**
    * Constant representing Schedule From Finish
    */
   public static final int FINISH_VALUE = 1;
   

   /**
    * Constant representing Schedule From Start
    */
   public static final ScheduleFrom START = new ScheduleFrom(START_VALUE);

   /**
    * Constant representing Schedule From Finish
    */
   public static final ScheduleFrom FINISH = new ScheduleFrom(FINISH_VALUE);      
}
