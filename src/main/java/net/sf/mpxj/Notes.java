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

package net.sf.mpxj;

/**
 * Interface implemented by classes representing notes.
 * Notes may be in a variety of formats, including plain text,
 * RTF, HTML, and structured notes organised into topics.
 * Classes implementing this interface will return a the plain
 * text version of their contents with the toString method is
 * called. All other details are implementation specific, but
 * are expected to include the ability to retrieve the original
 * formatted version of the notes.
 */
public interface Notes
{
   // Calling toString on a class implementing this interface
   // will return a plain text version of the notes.
}
