/*
 * file:       NotesHelper.java
 * author:     Jon Iles
 * date:       2025-11-12
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

package org.mpxj.primavera;

import org.mpxj.HtmlNotes;
import org.mpxj.Notes;

/**
 * Common methods for working with P6 notes.
 */
class NotesHelper
{
   /**
    * Create a Notes instance from an HTML document.
    *
    * @param text HTML document
    * @return Notes instance
    */
   public static Notes getNotes(String text)
   {
      Notes notes = getHtmlNote(text);
      return notes == null || notes.isEmpty() ? null : notes;
   }

   /**
    * Create an HtmlNote instance.
    *
    * @param text note text
    * @return HtmlNote instance
    */
   public static HtmlNotes getHtmlNote(String text)
   {
      if (text == null || text.isEmpty())
      {
         return null;
      }

      // Remove BOM and NUL characters
      String html = text.replaceAll("[\\uFEFF\\uFFFE\\x00]", "");

      // Replace newlines
      html = html.replaceAll("\\x7F\\x7F", "\n");

      HtmlNotes result = new HtmlNotes(html);

      return result.isEmpty() ? null : result;
   }
}
