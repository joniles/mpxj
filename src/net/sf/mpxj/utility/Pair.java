/*
 * file:       Pair.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2002-2005
 * date:       14/11/2005
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

package net.sf.mpxj.utility;

/**
 * Represents a pair of values.
 * 
 * @param <L> first value type
 * @param <R> second value type
 */
public final class Pair<L, R>
{
   /**
    * Constructor.
    *
    * @param first first object
    * @param second second object
    */
   public Pair(L first, R second)
   {
      m_first = first;
      m_second = second;
   }

   /**
    * Retrieve the the first object.
    *
    * @return first object
    */
   public L getFirst()
   {
      return (m_first);
   }

   /**
    * Retrieve the second object.
    *
    * @return second object
    */
   public R getSecond()
   {
      return (m_second);
   }

   private L m_first;
   private R m_second;
}
