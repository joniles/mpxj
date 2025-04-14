/*
 * file:       HtmlNotes.java
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

import org.mpxj.common.HtmlHelper;

/**
 * Represents notes formatted as RTF.
 */
public class HtmlNotes extends Notes
{
   /**
    * Constructor.
    *
    * @param html HTML document
    */
   public HtmlNotes(String html)
   {
      super(HtmlHelper.strip(html));
      m_html = html;
   }

   /**
    * Retrieve the HTML version of the notes.
    *
    * @return HTML document
    */
   public String getHtml()
   {
      return m_html;
   }

   private final String m_html;
}
