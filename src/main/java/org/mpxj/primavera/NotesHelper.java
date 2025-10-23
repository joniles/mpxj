package org.mpxj.primavera;

import org.mpxj.HtmlNotes;
import org.mpxj.Notes;

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
