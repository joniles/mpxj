/*
 * file:       MPXDate.java
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
 * This class represents a date within an MPX file. It is used to
 * distinguish between dates and times, which both use Date objects
 * as their underlying representation.
 */
class MPXDate extends Date
{
   /**
    * Default constructor.
    */
   MPXDate (MPXDateFormat format)
   {
      super ();
      m_format = format;
   }

   /**
    * Constructor allowing a specific date to be set.
    *
    * @param date date required expressed in milliseconds.
    */
   MPXDate (MPXDateFormat format, long date)
   {
      super (date);
      m_format = format;
   }

   /**
    * Constructor allowing a specific date to be set.
    *
    * @param date date required expressed in milliseconds.
    */
   MPXDate (MPXDateFormat format, Date date)
   {
      super (date.getTime());
      m_format = format;
   }

   public String toString ()
   {
      String result;

      if (this.equals(MPXDate.NA_DATE) == true)
      {
         result = "NA";
      }
      else
      {
         result = m_format.format (this);
      }

      return (result);
   }

   private MPXDateFormat m_format;

   /**
    * Special constant used to represent an NA date.
    */
   public static final MPXDate NA_DATE = new MPXDate (null, 0);
}
