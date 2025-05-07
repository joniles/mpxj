/*
 * file:       ActivityType.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2021
 * date:       2021-02-10
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

package org.mpxj;

/**
 * P6/PPX Activity type.
 */
public enum ActivityType
{
   TASK_DEPENDENT,
   RESOURCE_DEPENDENT,
   LEVEL_OF_EFFORT,
   START_MILESTONE,
   FINISH_MILESTONE,
   WBS_SUMMARY,
   HAMMOCK,
   START_FLAG,
   FINISH_FLAG
}
