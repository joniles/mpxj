/*
 * file:       RtfNotes.java
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

import org.mpxj.common.RtfHelper;

/**
 * Represents notes formatted as RTF.
 */
public class RtfNotes extends Notes
{
   /**
    * Constructor.
    *
    * @param rtf RTF document
    */
   public RtfNotes(String rtf)
   {
      super(RtfHelper.strip(rtf));
      m_rtf = rtf;
   }

   /**
    * Retrieve the RTF version of the notes.
    *
    * @return RTF document
    */
   public String getRtf()
   {
      return m_rtf;
   }

   private final String m_rtf;
}
