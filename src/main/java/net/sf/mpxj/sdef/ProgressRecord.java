/*
 * file:       ProgressRecord.java
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

import net.sf.mpxj.Duration;
import net.sf.mpxj.Task;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.common.NumberHelper;

/**
 * SDEF progress record.
 */
class ProgressRecord extends AbstractSDEFRecord
{
   @Override protected SDEFField[] getFieldDefinitions()
   {
      return FIELDS;
   }
   
   @Override public void process(Context context)
   {
      Double totalCost = getDouble(4);
      Double costToDate = getDouble(5);
      Double remainingCost = Double.valueOf(NumberHelper.getDouble(totalCost) - NumberHelper.getDouble(costToDate));
      Duration totalFloat = getDuration(12);
      if ("-".equals(getString(11)))
      {
         totalFloat = Duration.getInstance(-totalFloat.getDuration(), TimeUnit.DAYS);
      }
      
      Task task = context.getTask(getString(0));
      task.setActualStart(getDate(1));
      task.setActualFinish(getDate(2));
      task.setRemainingDuration(getDuration(3));
      task.setCost(getDouble(4));
      task.setRemainingCost(remainingCost);
      task.setCost(1, getDouble(6));
      task.setEarlyStart(getDate(7));
      task.setEarlyFinish(getDate(8));
      task.setLateStart(getDate(9));
      task.setLateFinish(getDate(10));
      task.setTotalSlack(totalFloat);
   }
   
   private static final SDEFField[] FIELDS = new SDEFField[]
   {
      new StringField("Activity ID", 10),
      new DateField("Actual Start Date"),
      new DateField("Actual Finish Date"),
      new DurationField("Remaining Duration", 3),
      new DoubleField("Activity Cost", 12),
      new DoubleField("Cost to Date", 12),
      new DoubleField("Stored Material", 12),
      new DateField("Early Start Date"),
      new DateField("Early Finish Date"),
      new DateField("Late Start Date"),
      new DateField("Late Finish Date"),
      new StringField("Float Sign", 1),
      new DurationField("Total Float", 3)
   };
}
