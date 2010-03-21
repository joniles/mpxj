/*
 * file:       ResourceAssignmentFactory12.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2010
 * date:       21/03/2010
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

package net.sf.mpxj.mpp;

/**
 * Reads resource assignment data from an MPP12 file.
 */
public final class ResourceAssignmentFactory12 extends AbstractResourceAssignmentFactory
{
   /**
    * {@inheritDoc}
    */
   @Override protected Integer getCompleteWorkKey()
   {
      return COMPLETE_WORK;
   }

   /**
    * {@inheritDoc}
    */
   @Override protected Integer getPlannedWorkKey()
   {
      return PLANNED_WORK;
   }

   private static final Integer PLANNED_WORK = Integer.valueOf(49);
   private static final Integer COMPLETE_WORK = Integer.valueOf(50);
}
