/*
 * file:       PrimaveraPMObjectSequences.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2021
 * date:       2021-02-23
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

package org.mpxj.primavera;

import org.mpxj.common.ObjectSequence;

/**
 * Collection of sequence generator used across multiple ProjectFile exports.
 */
final class PrimaveraPMObjectSequences
{
   public Integer getProjectID()
   {
      return m_projectID.getNext();
   }

   public Integer getProjectObjectID()
   {
      return m_projectObjectID.getNext();
   }

   public Integer getRateObjectID()
   {
      return m_rateObjectID.getNext();
   }

   public Integer getWbsNoteObjectID()
   {
      return m_wbsNoteObjectID.getNext();
   }

   public Integer getActivityNoteObjectID()
   {
      return m_activityNoteObjectID.getNext();
   }

   private final ObjectSequence m_projectID = new ObjectSequence(0);
   private final ObjectSequence m_projectObjectID = new ObjectSequence(1);
   private final ObjectSequence m_rateObjectID = new ObjectSequence(1);
   private final ObjectSequence m_wbsNoteObjectID = new ObjectSequence(1);
   private final ObjectSequence m_activityNoteObjectID = new ObjectSequence(1);
}
