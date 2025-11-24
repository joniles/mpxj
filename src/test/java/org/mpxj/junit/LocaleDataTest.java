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





import org.junit.jupiter.api.Test;
import org.mpxj.AssignmentField;
import org.mpxj.ProjectField;
import org.mpxj.ResourceField;
import org.mpxj.TaskField;

import static org.junit.jupiter.api.Assertions.assertNotNull;

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
         assertNotNull(field.getName(), "ProjectField." + field.name() + " name is null");
      }

      for (TaskField field : TaskField.values())
      {
         assertNotNull(field.getName(), "TaskField." + field.name() + " name is null");
      }

      for (ResourceField field : ResourceField.values())
      {
         assertNotNull(field.getName(), "ResourceField." + field.name() + " name is null");
      }

      for (AssignmentField field : AssignmentField.values())
      {
         assertNotNull(field.getName(), "AssignmentField." + field.name() + " name is null");
      }

   }
}
