/*
 * file:       RtfHelper.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2002-2003
 * date:       24/05/2003
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

package org.mpxj.common;

import java.io.IOException;

import com.rtfparserkit.converter.text.StringTextConverter;
import com.rtfparserkit.parser.RtfStringSource;

/**
 * This class is used to collect together utility functions for manipulating
 * RTF encoded text.
 */
public final class RtfHelper
{
   /**
    * Simple heuristic to determine if the string contains formal RTF.
    *
    * @param text source text
    * @return true if the text may contain formal RTF
    */
   private static boolean isFormalRTF(String text)
   {
      return text.startsWith("{\\rtf");
   }

   /**
    * This method removes all RTF formatting from a given piece of text.
    *
    * @param text Text from which the RTF formatting is to be removed.
    * @return Plain text
    */
   public static String strip(String text)
   {
      String result = text;
      if (text != null && !text.isEmpty())
      {
         try
         {
            boolean formalRTF = isFormalRTF(text);
            StringTextConverter stc = new StringTextConverter();
            stc.convert(new RtfStringSource(text));
            result = stripExtraLineEnd(stc.getText(), formalRTF);
         }
         catch (IOException ex)
         {
            result = "";
         }
      }

      return result;
   }

   /**
    * Remove the trailing line end from an RTF block.
    *
    * @param text source text
    * @param formalRTF true if this is a real RTF block
    * @return text with line end stripped
    */
   private static String stripExtraLineEnd(String text, boolean formalRTF)
   {
      if (formalRTF && text.endsWith("\n"))
      {
         text = text.substring(0, text.length() - 1);
      }
      return text;
   }
}
