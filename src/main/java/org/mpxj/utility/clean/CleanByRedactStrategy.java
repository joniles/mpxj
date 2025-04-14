/*
 * file:       CleanByRedactStrategy.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2022
 * date:       03/01/2022
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

package org.mpxj.utility.clean;

/**
 * Cleans text by replacing it with X's.
 */
public class CleanByRedactStrategy implements CleanStrategy
{
   @Override public String generateReplacementText(String oldText)
   {
      char c2 = 0;
      StringBuilder newText = new StringBuilder(oldText.length());
      for (int loop = 0; loop < oldText.length(); loop++)
      {
         char c = oldText.charAt(loop);
         if (Character.isUpperCase(c))
         {
            newText.append('X');
         }
         else
         {
            if (Character.isLowerCase(c))
            {
               newText.append('x');
            }
            else
            {
               if (Character.isDigit(c))
               {
                  newText.append('0');
               }
               else
               {
                  if (Character.isLetter(c))
                  {
                     // Handle other codepages etc. If possible find a way to
                     // maintain the same code page as original.
                     // E.g. replace with a character from the same alphabet.
                     // This 'should' work for most cases
                     if (c2 == 0)
                     {
                        c2 = c;
                     }
                     newText.append(c2);
                  }
                  else
                  {
                     newText.append(c);
                  }
               }
            }
         }
      }

      return newText.toString();
   }
}
