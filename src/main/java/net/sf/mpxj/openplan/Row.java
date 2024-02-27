/*
 * file:       Row.java
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

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import net.sf.mpxj.Duration;
import net.sf.mpxj.ResourceType;

interface Row
{
   public String getString(String name);

   public LocalDateTime getDate(String name);

   public LocalTime getTime(String name);

   public Double getDouble(String name);

   public Integer getInteger(String name);

   public Boolean getBoolean(String name);

   public UUID getUuid(String name);

   public Duration getDuration(String name);

   public ResourceType getResourceType(String name);
}
