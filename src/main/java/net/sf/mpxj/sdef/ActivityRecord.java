/*
 * file:       ActivityRecord.java
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

import java.util.UUID;

import net.sf.mpxj.Task;

/**
 * SDEF Activity record.
 */
class ActivityRecord extends AbstractSDEFRecord
{
   @Override protected SDEFField[] getFieldDefinitions()
   {
      return FIELDS;
   }
   
   @Override public void process(Context context)
   {
      String activityID = getString(0);
      Task task = context.addTask(activityID);
      task.setText(1, activityID);
      task.setName(getString(1));
      task.setDuration(getDuration(2));
      task.setConstraintDate(getDate(3));
      task.setConstraintType(getConstraintType(4));
      task.setCalendar(context.getCalendar(getString(5)));
      task.setText(2, getString(6));
      task.setNumber(1, getInteger(7));
      task.setText(3, getString(8));
      task.setText(4, getString(9));
      task.setText(5, getString(10));
      task.setText(6, getString(11));
      task.setText(7, getString(12));
      task.setText(8, getString(13));
      task.setText(9, getString(14));      
      task.setGUID(UUID.nameUUIDFromBytes(activityID.getBytes()));
      context.getEventManager().fireTaskReadEvent(task);
   }
   
   private static final SDEFField[] FIELDS = new SDEFField[]
   {
      new StringField("Activity ID", 10),
      new StringField("Activity Description", 30),
      new DurationField("Activity Duration", 3),
      new DateField("Constraint Date"),
      new ConstraintTypeField("Constraint Type"),
      new StringField("Calendar Code", 1),
      new StringField("Hammock Code", 1),
      new IntegerField("Workers Per Day", 3),
      new StringField("Responsibility Code", 4),
      new StringField("Work Area Code", 4),
      new StringField("Mod of Claim No", 6),
      new StringField("Bid Item", 6),
      new StringField("Phase of Work", 2),
      new StringField("Category of Work", 1),
      new StringField("Feature of Work", 10)
   };
}
