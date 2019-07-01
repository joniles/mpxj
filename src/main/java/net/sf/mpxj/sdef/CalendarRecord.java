/*
 * file:       CalendarRecord.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2019
 * date:       01/07/2019
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

package net.sf.mpxj.sdef;

import net.sf.mpxj.Day;
import net.sf.mpxj.ProjectCalendar;

/**
 * SDEF Calendar Record.
 */
class CalendarRecord extends AbstractSDEFRecord
{
   @Override protected SDEFField[] getFieldDefinitions()
   {
      return FIELDS;
   }
   
   @Override public void process(Context context)
   {
      ProjectCalendar calendar = context.addCalendar(getString(0));          
      calendar.setName(getString(2));
      
      String flags = getString(1);
      for (Day day : Day.values())
      {
         calendar.setWorkingDay(day, flags.charAt(day.getValue()-1) == 'Y');
      }
      context.getEventManager().fireCalendarReadEvent(calendar);
   }

   private static final SDEFField[] FIELDS = new SDEFField[]
   {
      new StringField("Calendar Code", 1),
      new StringField("Workdays", 7),
      new StringField("Calendar Description", 30)
   };
}
