/*
 * file:       MpxCreateTimephased.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software
 * date:       29/11/2019
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

package org.mpxj.sample;

import java.time.LocalDateTime;
import java.util.Arrays;

import org.mpxj.Duration;
import org.mpxj.ProjectCalendar;
import org.mpxj.ProjectFile;
import org.mpxj.ProjectProperties;
import org.mpxj.Resource;
import org.mpxj.ResourceAssignment;
import org.mpxj.Task;
import org.mpxj.TimeUnit;
import org.mpxj.TimephasedWork;
import org.mpxj.mspdi.MSPDIWriter;

/**
 * Provides a trivial example of adding timephased work to a resource assignment.
 */
public class MpxjCreateTimephased
{
   /**
    * Main method.
    *
    * @param args array of command line arguments
    */
   public static void main(String[] args)
   {
      try
      {
         if (args.length != 1)
         {
            System.out.println("Usage: MpxjCreateTimephased <output file name>");
         }
         else
         {
            create(args[0]);
         }
      }

      catch (Exception ex)
      {
         ex.printStackTrace(System.out);
      }
   }

   /**
    * Create a schedule demonstrating how timephased work can
    * be applied to a resource assignment.
    *
    * @param filename output file
    */
   private static void create(String filename) throws Exception
   {
      //
      // Create a ProjectFile instance
      //
      ProjectFile file = new ProjectFile();

      //
      // Add a default calendar called "Standard"
      //
      ProjectCalendar calendar = file.addDefaultBaseCalendar();
      file.setDefaultCalendar(calendar);

      //
      // Retrieve the project properties and set the start date. Note Microsoft
      // Project appears to reset all task dates relative to this date, so this
      // date must match the start date of the earliest task for you to see
      // the expected results. If this value is not set, it will default to
      // today's date.
      //
      ProjectProperties properties = file.getProjectProperties();
      properties.setStartDate(LocalDateTime.of(2003, 1, 1, 3, 0));

      //
      // Add a resource
      //
      Resource resource1 = file.addResource();
      resource1.setName("Timephased Resource");

      //
      // Add a task
      //
      Task task1 = file.addTask();
      task1.setName("Timephased Task");
      task1.setDuration(Duration.getInstance(3, TimeUnit.DAYS));
      task1.setStart(LocalDateTime.of(2003, 1, 1, 3, 0));
      task1.setFinish(LocalDateTime.of(2003, 1, 3, 10, 0));
      ResourceAssignment assignment3 = task1.addResourceAssignment(resource1);

      //
      // Our task is 3 days long - 24h of work.
      // We split this as 20h, 2h and 2h
      //
      final TimephasedWork day1 = new TimephasedWork();
      day1.setAmountPerDay(Duration.getInstance(20, TimeUnit.HOURS));
      day1.setStart(LocalDateTime.of(2003, 1, 1, 3, 0));
      day1.setFinish(LocalDateTime.of(2003, 1, 1, 23, 0));
      day1.setModified(true);
      day1.setTotalAmount(Duration.getInstance(20, TimeUnit.HOURS));

      final TimephasedWork day2 = new TimephasedWork();
      day2.setAmountPerDay(Duration.getInstance(2, TimeUnit.HOURS));
      day2.setStart(LocalDateTime.of(2003, 1, 2, 8, 0));
      day2.setFinish(LocalDateTime.of(2003, 1, 1, 10, 0));
      day2.setModified(true);
      day2.setTotalAmount(Duration.getInstance(2, TimeUnit.HOURS));

      final TimephasedWork day3 = new TimephasedWork();
      day3.setAmountPerDay(Duration.getInstance(2, TimeUnit.HOURS));
      day3.setStart(LocalDateTime.of(2003, 1, 3, 8, 0));
      day3.setFinish(LocalDateTime.of(2003, 1, 1, 10, 0));
      day3.setModified(true);
      day3.setTotalAmount(Duration.getInstance(2, TimeUnit.HOURS));

      //
      // Add the timephased data to the assignment
      //
      assignment3.setTimephasedWork(Arrays.asList(day1, day2, day3));

      //
      // Write the file
      //
      MSPDIWriter writer = new MSPDIWriter();

      // By default, timephased data is not written so we need to enable it here
      writer.setWriteTimephasedData(true);

      // Also, tell the writer not to get clever with our timephased data, just write it as it is...
      writer.setSplitTimephasedAsDays(false);

      //
      // If you look at the resulting project in the Resource Usage view in MS Project
      // you should see the work split as 20j, 8h and 8h over the three days of the task.
      //
      writer.write(file, filename);
   }
}
