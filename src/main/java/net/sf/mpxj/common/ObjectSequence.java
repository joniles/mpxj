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

package net.sf.mpxj.common;

/**
 * Simple sequence generation class.
 */
public final class ObjectSequence
{
   public ObjectSequence(int id)
   {
      m_id = id;
   }

   public void reset(int currentMaxValue)
   {
      if (currentMaxValue >= m_id)
      {
         m_id = currentMaxValue + 1;
      }
   }

   public Integer getNext()
   {
      return Integer.valueOf(m_id++);
   }

   private int m_id;
}
