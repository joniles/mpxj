/*
 * file:       DrivingRelation.java
 * author:     Jon Iles
 * date:       2026-05-21
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

package org.mpxj.cpm;

import java.time.LocalDateTime;

import org.mpxj.Relation;

/**
 * Represents a relation and the early start date derived from it.
 */
class DrivingRelation implements Comparable<DrivingRelation>
{
   /**
    * Constructor.
    *
    * @param relation source relation
    * @param earlyStart early start date derived from the relation
    */
   public DrivingRelation(Relation relation, LocalDateTime earlyStart)
   {
      m_relation = relation;
      m_earlyStart = earlyStart;
   }

   /**
    * Retrieve the source relation.
    *
    * @return source relation
    */
   public Relation getRelation()
   {
      return m_relation;
   }

   /**
    * Retrieve the early start timestamp derived from the source relation.
    *
    * @return early start
    */
   public LocalDateTime getEarlyStart()
   {
      return m_earlyStart;
   }

   @Override public int compareTo(DrivingRelation o)
   {
      return m_earlyStart.compareTo(o.getEarlyStart());
   }

   private final LocalDateTime m_earlyStart;
   private final Relation m_relation;
}
