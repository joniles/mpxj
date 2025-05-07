/*
 * file:       ProjectRecord.java
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

import org.mpxj.ProjectProperties;

/**
 * SDEF project record.
 */
class ProjectRecord extends AbstractSDEFRecord
{
   @Override protected SDEFField[] getFieldDefinitions()
   {
      return FIELDS;
   }

   @Override public void process(Context context)
   {
      ProjectProperties props = context.getProject().getProjectProperties();
      props.setStatusDate(getDate(0));
      props.setManager(getString(1));
      props.setProjectTitle(getString(2));
      props.setSubject(getString(3));
      props.setKeywords(getString(5));
      props.setStartDate(getDate(6));
      props.setFinishDate(getDate(7));
   }

   private static final SDEFField[] FIELDS = new SDEFField[]
   {
      new DateField("Data Date"),
      new StringField("Project Identifier", 4),
      new StringField("Project Name", 48),
      new StringField("Contractor Name", 36),
      new StringField("Precedence", 1),
      new StringField("Contract Number", 6),
      new DateField("Project Start"),
      new DateField("Project End")
   };
}
