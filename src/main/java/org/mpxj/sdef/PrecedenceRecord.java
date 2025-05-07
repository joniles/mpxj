/*
 * file:       PrecedenceRecord.java
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

package org.mpxj.sdef;

import org.mpxj.Relation;
import org.mpxj.Task;

/**
 * SDEF precedence record.
 */
class PrecedenceRecord extends AbstractSDEFRecord
{
   @Override protected SDEFField[] getFieldDefinitions()
   {
      return FIELDS;
   }

   @Override public void process(Context context)
   {
      Task currentTask = context.getTask(getString(0));
      Task previousTask = context.getTask(getString(1));
      if (currentTask != null && previousTask != null)
      {
         Relation relation = currentTask.addPredecessor(new Relation.Builder()
            .predecessorTask(previousTask)
            .type(getRelationType(2))
            .lag(getDuration(3)));
         context.getEventManager().fireRelationReadEvent(relation);
      }
   }

   private static final SDEFField[] FIELDS = new SDEFField[]
   {
      new StringField("Activity ID", 10),
      new StringField("Preceding Activity", 10),
      new RelationTypeField("Predecessor Type"),
      new DurationField("Lag Duration", 4)
   };
}
