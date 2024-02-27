/*
 * file:       CodeValue.java
 * author:     Jon Iles
 * date:       2024-02-27
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

package net.sf.mpxj.openplan;
public class CodeValue
{
   public CodeValue(String id, String uniqueID, String description)
   {
      m_id = id;
      m_uniqueID = uniqueID;
      m_description = description;
   }

   public String getID()
   {
      return m_id;
   }

   public String getUniqueID()
   {
      return m_uniqueID;
   }

   public String getDescription()
   {
      return m_description;
   }

   private final String m_id;
   private final String m_uniqueID;
   private final String m_description;
}
