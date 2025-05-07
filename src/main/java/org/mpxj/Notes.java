/*
 * file:       Notes.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2021
 * date:       2021-01-03
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

package org.mpxj;

/**
 * Represents plain text notes.
 * Calling toString on this class, or any class
 * derived from it will return a plain text
 * representation of the notes.
 */
public class Notes
{
   /**
    * Constructor.
    *
    * @param text note text
    */
   public Notes(String text)
   {
      m_text = text;
   }

   /**
    * Determine if the note text is empty.
    *
    * @return true if the note text is empty
    */
   public boolean isEmpty()
   {
      // Subclasses may have their own representation of the text
      String text = toString();
      return text == null || text.isEmpty();
   }

   @Override public String toString()
   {
      return m_text;
   }

   private final String m_text;
}
