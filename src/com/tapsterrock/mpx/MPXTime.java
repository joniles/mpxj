/*
 * file:       MPXTime.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2002-2003
 * date:       01/01/2003
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

import java.util.Date;


/**
 * This class represents a time within an MPX file. It is used to
 * distinguish between dates and times, which both use Date objects
 * as their underlying representation.
 */
class MPXTime extends Date
{
   /**
    * Default constructor.
    */
   MPXTime (MPXTimeFormat format)
   {
      super ();
      m_format = format;
   }

   /**
    * Constructor allowing a specific time to be set.
    *
    * @param time time required expressed in milliseconds.
    */
   MPXTime (MPXTimeFormat format, long time)
   {
      super (time);
      m_format = format;
   }

   /**
    * Constructor allowing a specific time to be set.
    *
    * @param time time required expressed in milliseconds.
    */
   MPXTime (MPXTimeFormat format, Date time)
   {
      super (time.getTime());
      m_format = format;
   }

   /**
    * This method builds a String representation of the time represented
    * by this instance.
    *
    * @return string representation of the time
    */
   public String toString ()
   {
      return (m_format.format(this));
   }

   private MPXTimeFormat m_format;

   /**
    * Special constant used to represent an NA date.
    */
   public static final MPXTime NA_TIME = new MPXTime (null, 0);
}
