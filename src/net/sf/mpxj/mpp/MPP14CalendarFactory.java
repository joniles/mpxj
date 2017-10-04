/*
 * file:       MPP14CalendarFactory.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2017
 * date:       2017-10-04
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

import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.common.NumberHelper;

/**
 * MPP12-specific calendar factory.
 */
public class MPP14CalendarFactory extends AbstractCalendarFactory
{
   /**
    * Constructor.
    *
    * @param file parent ProjectFile instance
    */
   public MPP14CalendarFactory(ProjectFile file)
   {
      super(file);

      if (NumberHelper.getInt(file.getProjectProperties().getApplicationVersion()) > ApplicationVersion.PROJECT_2010)
      {
         m_calendarIDOffset = 8;
         m_baseIDOffset = 0;
         m_resourceIDOffset = 4;
      }
      else
      {
         m_calendarIDOffset = 0;
         m_baseIDOffset = 4;
         m_resourceIDOffset = 8;
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override protected int getCalendarIDOffset()
   {
      return m_calendarIDOffset;
   }

   /**
    * {@inheritDoc}
    */
   @Override protected int getBaseIDOffset()
   {
      return m_baseIDOffset;
   }

   /**
    * {@inheritDoc}
    */
   @Override protected int getResourceIDOffset()
   {
      return m_resourceIDOffset;
   }

   private final int m_calendarIDOffset;
   private final int m_baseIDOffset;
   private final int m_resourceIDOffset;

}
