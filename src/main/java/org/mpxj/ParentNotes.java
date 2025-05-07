/*
 * file:       ParentNotes.java
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

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Represents a note which is composed of one or more child notes.
 */
public class ParentNotes extends Notes
{
   /**
    * Constructor.
    *
    * @param childNotes child notes
    */
   public ParentNotes(List<Notes> childNotes)
   {
      super(null);
      m_childNotes = childNotes;
   }

   /**
    * Retrieve the list of child notes.
    *
    * @return list of child notes
    */
   public List<Notes> getChildNotes()
   {
      return m_childNotes;
   }

   @Override public String toString()
   {
      return m_childNotes.stream().filter(Objects::nonNull).map(Notes::toString).collect(Collectors.joining("\n")).trim();
   }

   private final List<Notes> m_childNotes;
}
