/*
 * file:       WorkGroup.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2002-2003
 * date:       25/11/2004
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
 * Instances of this class represent enumerated work group values.
 */
public final class WorkGroup
{
   /**
    * Private constructor.
    *
    * @param value work group value
    */
   private WorkGroup (int value)
   {
      m_value = value;
   }

   /**
    * Retrieves the int representation of the work group.
    *
    * @return work group value
    */
   public int getValue ()
   {
      return (m_value);
   }

   /**
    * Retrieve a WorkGroup instance representing the supplied value.
    *
    * @param value work group value
    * @return WorkGroup instance
    */
   public static WorkGroup getInstance (int value)
   {
      WorkGroup result;

      switch (value)
      {
         case DEFAULT_VALUE:
         {
            result = DEFAULT;
            break;
         }

         default:
         case NONE_VALUE:
         {
            result = NONE;
            break;
         }
      }

      return (result);
   }

   private int m_value;

   public static final int DEFAULT_VALUE = 0;
   public static final int NONE_VALUE = 1;
   public static final int EMAIL_VALUE = 2;
   public static final int WEB_VALUE = 3;

   public static final WorkGroup DEFAULT = new WorkGroup(DEFAULT_VALUE);
   public static final WorkGroup NONE = new WorkGroup(NONE_VALUE);
   public static final WorkGroup EMAIL = new WorkGroup(EMAIL_VALUE);
   public static final WorkGroup WEB = new WorkGroup(WEB_VALUE);
}
