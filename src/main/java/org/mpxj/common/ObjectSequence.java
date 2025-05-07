/*
 * file:       ObjectSequence.java
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

package org.mpxj.common;

/**
 * Simple sequence generation class.
 */
public final class ObjectSequence
{
   /**
    * Constructor.
    *
    * @param id initial value
    */
   public ObjectSequence(int id)
   {
      m_id = id;
   }

   /**
    * Sync the sequence with a value known to be in use.
    * If necessary, update the sequence to follow on from this value.
    *
    * @param id value in use
    */
   public void sync(Integer id)
   {
      if (id != null && id.intValue() >= m_id)
      {
         m_id = id.intValue() + 1;
      }
   }

   /**
    * Retrieve the next value from the sequence.
    *
    * @return next value
    */
   public Integer getNext()
   {
      return Integer.valueOf(m_id++);
   }

   /**
    * If the id is not null, sync the sequence with it.
    * If the id is null, generate a new id.
    *
    * @param id id value
    * @return id value
    */
   public Integer syncOrGetNext(Integer id)
   {
      if (id == null)
      {
         return getNext();
      }
      sync(id);
      return id;
   }

   private int m_id;
}
