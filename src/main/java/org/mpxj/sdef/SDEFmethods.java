/*
 * file:       SDEFmethods.java
 * author:     William (Bill) Iverson
 * copyright:  (c) GeoComputer 2011
 * date:       06/01/2012
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

package org.mpxj.sdef;

import java.time.DayOfWeek;

import org.mpxj.common.DayOfWeekHelper;
import org.mpxj.ProjectCalendar;

/**
 * This class contains some general purpose static methods used in my SDEFWriter.
 */
class SDEFmethods
{
   /**
    * Method to force an input string into a fixed width field
    * and set it on the left with the right side filled with
    * space ' ' characters.
    *
    * @param input input string
    * @param width required width
    * @return formatted string
    */
   public static String lset(String input, int width)
   {
      String result;
      StringBuilder pad = new StringBuilder();
      if (input == null)
      {
         for (int i = 0; i < width; i++)
         {
            pad.append(' '); // put blanks into buffer
         }
         result = pad.toString();
      }
      else
      {
         if (input.length() >= width)
         {
            result = input.substring(0, width); // when input is too long, truncate
         }
         else
         {
            int padLength = width - input.length(); // number of blanks to add
            for (int i = 0; i < padLength; i++)
            {
               pad.append(' '); // force put blanks into buffer
            }
            result = input + pad; // concatenate
         }
      }
      return result;
   }

   /**
    * Another method to force an input string into a fixed width field
    * and set it on the right with the left side filled with space ' ' characters.
    *
    * @param input input string
    * @param width required width
    * @return formatted string
    */
   public static String rset(String input, int width)
   {
      String result; // result to return
      StringBuilder pad = new StringBuilder();
      if (input == null)
      {
         for (int i = 0; i < width - 1; i++)
         {
            pad.append(' '); // put blanks into buffer
         }
         result = " " + pad; // one short to use + overload
      }
      else
      {
         if (input.length() >= width)
         {
            result = input.substring(0, width); // when input is too long, truncate
         }
         else
         {
            int padLength = width - input.length(); // number of blanks to add
            for (int i = 0; i < padLength; i++)
            {
               pad.append(' '); // actually put blanks into buffer
            }
            result = pad + input; // concatenate
         }
      }
      return result;
   }

   /**
    * This method takes a calendar of MPXJ library type, then returns a String of the
    * general working days USACE format.  For example, the regular 5-day work week is
    * NYYYYYN
    *
    * If you get Fridays off work, then the String becomes NYYYYNN
    *
    * @param input ProjectCalendar instance
    * @return work days string
    */
   public static String workDays(ProjectCalendar input)
   {
      StringBuilder result = new StringBuilder();
      for (DayOfWeek day : DayOfWeekHelper.ORDERED_DAYS)
      {
         result.append(input.isWorkingDay(day) ? 'Y' : 'N');
      }
      return result.toString();
   }
}
