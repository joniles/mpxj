/*
 * file:       LocaleDataTest.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2008
 * date:       30/08/2019
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

package org.mpxj.junit;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import org.mpxj.AssignmentField;
import org.mpxj.ProjectField;
import org.mpxj.ResourceField;
import org.mpxj.TaskField;

/**
 * LocaleData tests.
 */
public class LocaleDataTest
{
   /**
    * Ensure that all field enums have a locale entry.
    */
   @Test public void testLocaleData()
   {
      for (ProjectField field : ProjectField.values())
      {
         assertNotNull("ProjectField." + field.name() + " name is null", field.getName());
      }

      for (TaskField field : TaskField.values())
      {
         assertNotNull("TaskField." + field.name() + " name is null", field.getName());
      }

      for (ResourceField field : ResourceField.values())
      {
         assertNotNull("ResourceField." + field.name() + " name is null", field.getName());
      }

      for (AssignmentField field : AssignmentField.values())
      {
         assertNotNull("AssignmentField." + field.name() + " name is null", field.getName());
      }

   }
}
