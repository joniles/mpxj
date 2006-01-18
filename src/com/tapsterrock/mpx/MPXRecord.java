/*
 * file:       MPXRecord.java
 * author:     Scott Melville
 *             Jon Iles
 * copyright:  (c) Tapster Rock Limited 2002-2003
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

package com.tapsterrock.mpx;


/**
 * This is the base class from which all classes representing records found
 * in an MPX file are derived. It contains common funciotnality and
 * attribute storage used by all of the derived classes.
 */
class MPXRecord
{
   /**
    * Constructor.
    *
    * @param mpx Parent MPX file
    */
   protected MPXRecord (ProjectFile mpx)
   {
      m_mpx = mpx;    
   }
            
   /**
    * Accessor method allowing retreival of MPXFile reference.
    *
    * @return reference to this MPXFile
    */
   public final ProjectFile getParentFile ()
   {
      return (m_mpx);
   }

   /**
    * Reference to parent MPXFile.
    */
   private ProjectFile m_mpx;
}
