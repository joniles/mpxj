/*
 * file:       ProjectEntity.java
 * author:     Scott Melville
 *             Jon Iles
 * copyright:  (c) Packwood Software 2002-2003
 * date:       15/08/2002
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
 * This is the base class from which all classes representing records found
 * in an MPX file are derived. It contains common functionality and
 * attribute storage used by all of the derived classes.
 */
class ProjectEntity
{
   /**
    * Constructor.
    *
    * @param mpx Parent MPX file
    */
   protected ProjectEntity(ProjectFile mpx)
   {
      m_mpx = mpx;
   }

   /**
    * Accessor method allowing retrieval of ProjectFile reference.
    *
    * @return reference to this the parent ProjectFile instance
    */
   public final ProjectFile getParentFile()
   {
      return (m_mpx);
   }

   /**
    * Reference to parent ProjectFile.
    */
   private final ProjectFile m_mpx;
}
