/*
 * file:       MPP9CalendarFactory.java
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

import java.io.IOException;
import java.util.Date;

import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.DocumentInputStream;

import net.sf.mpxj.DateRange;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectCalendarException;
import net.sf.mpxj.ProjectFile;

/**
 * MPP9-specific calendar factory.
 */
class MPP9CalendarFactory extends AbstractCalendarFactory
{
   /**
    * Constructor.
    *
    * @param file parent ProjectFile instance
    */
   public MPP9CalendarFactory(ProjectFile file)
   {
      super(file);
   }

   /**
    * {@inheritDoc}
    */
   @Override protected int getCalendarIDOffset()
   {
      return 0;
   }

   /**
    * {@inheritDoc}
    */
   @Override protected int getBaseIDOffset()
   {
      return 4;
   }

   /**
    * {@inheritDoc}
    */
   @Override protected int getResourceIDOffset()
   {
      return 8;
   }

   /**
    * {@inheritDoc}
    */
   @Override protected int getCalendarHoursOffset()
   {
      return 4;
   }

   /**
    * {@inheritDoc}
    */
   @Override protected Integer getCalendarNameVarDataType()
   {
      return CALENDAR_NAME;
   }

   /**
    * {@inheritDoc}
    */
   @Override protected Integer getCalendarDataVarDataType()
   {
      return CALENDAR_DATA;
   }

   /**
    * {@inheritDoc}
    */
   @Override protected VarMeta getCalendarVarMeta(DirectoryEntry directory) throws IOException
   {
      return new VarMeta9(new DocumentInputStream(((DocumentEntry) directory.getEntry("VarMeta"))));
   }

   /**
    * This method extracts any exceptions associated with a calendar.
    *
    * @param data calendar data block
    * @param cal calendar instance
    */
   @Override protected void processCalendarExceptions(byte[] data, ProjectCalendar cal)
   {
      //
      // Handle any exceptions
      //
      int exceptionCount = MPPUtility.getShort(data, 0);

      if (exceptionCount != 0)
      {
         int index;
         int offset;
         ProjectCalendarException exception;
         long duration;
         int periodCount;
         Date start;

         for (index = 0; index < exceptionCount; index++)
         {
            offset = 4 + (60 * 7) + (index * 64);

            Date fromDate = MPPUtility.getDate(data, offset);
            Date toDate = MPPUtility.getDate(data, offset + 2);
            exception = cal.addCalendarException(fromDate, toDate);

            periodCount = MPPUtility.getShort(data, offset + 6);
            if (periodCount != 0)
            {
               for (int exceptionPeriodIndex = 0; exceptionPeriodIndex < periodCount; exceptionPeriodIndex++)
               {
                  start = MPPUtility.getTime(data, offset + 12 + (exceptionPeriodIndex * 2));
                  duration = MPPUtility.getDuration(data, offset + 24 + (exceptionPeriodIndex * 4));
                  exception.addRange(new DateRange(start, new Date(start.getTime() + duration)));
               }
            }
         }
      }
   }

   private static final Integer CALENDAR_NAME = Integer.valueOf(1);
   private static final Integer CALENDAR_DATA = Integer.valueOf(3);
}
