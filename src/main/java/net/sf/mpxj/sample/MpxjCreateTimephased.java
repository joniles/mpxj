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

package net.sf.mpxj.sample;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import net.sf.mpxj.Duration;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ProjectProperties;
import net.sf.mpxj.Resource;
import net.sf.mpxj.ResourceAssignment;
import net.sf.mpxj.Task;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.TimephasedWork;
import net.sf.mpxj.TimephasedWorkContainer;
import net.sf.mpxj.mspdi.MSPDIWriter;

/**
 * Provides a trivial example of adding timephased work to a resourc eassignment.
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
      // Create a simple date format to allow us to
      // easily set date values.
      //
      SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");

      //
      // Create a ProjectFile instance
      //
      ProjectFile file = new ProjectFile();

      //
      // Add a default calendar called "Standard"
      //
      file.addDefaultBaseCalendar();

      //
      // Retrieve the project properties and set the start date. Note Microsoft
      // Project appears to reset all task dates relative to this date, so this
      // date must match the start date of the earliest task for you to see
      // the expected results. If this value is not set, it will default to
      // today's date.
      //
      ProjectProperties properties = file.getProjectProperties();
      properties.setStartDate(df.parse("01/01/2003 03:00"));

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
      task1.setStart(df.parse("01/01/2003 03:00"));
      task1.setFinish(df.parse("03/01/2003 10:00"));
      ResourceAssignment assignment3 = task1.addResourceAssignment(resource1);

      //
      // Our task is 3 days long - 24h of work.
      // We split this as 20h, 2h and 2h
      //
      final TimephasedWork day1 = new TimephasedWork();
      day1.setAmountPerDay(Duration.getInstance(20, TimeUnit.HOURS));
      day1.setStart(df.parse("01/01/2003 03:00"));
      day1.setFinish(df.parse("01/01/2003 23:00"));
      day1.setModified(true);
      day1.setTotalAmount(Duration.getInstance(20, TimeUnit.HOURS));

      final TimephasedWork day2 = new TimephasedWork();
      day2.setAmountPerDay(Duration.getInstance(2, TimeUnit.HOURS));
      day2.setStart(df.parse("02/01/2003 08:00"));
      day2.setFinish(df.parse("02/01/2003 10:00"));
      day2.setModified(true);
      day2.setTotalAmount(Duration.getInstance(2, TimeUnit.HOURS));

      final TimephasedWork day3 = new TimephasedWork();
      day3.setAmountPerDay(Duration.getInstance(2, TimeUnit.HOURS));
      day3.setStart(df.parse("03/01/2003 08:00"));
      day3.setFinish(df.parse("03/01/2003 10:00"));
      day3.setModified(true);
      day3.setTotalAmount(Duration.getInstance(2, TimeUnit.HOURS));

      //
      // Add the timephased data to the assignment
      //
      assignment3.setTimephasedWork(new TimephasedWorkContainer()
      {
         @Override public boolean hasData()
         {
            return true;
         }

         @Override public List<TimephasedWork> getData()
         {
            return Arrays.asList(day1, day2, day3);
         }

         @Override public TimephasedWorkContainer applyFactor(double perDayFactor, double totalFactor)
         {
            // You'd need to implement this to handle timephased overtime work
            return null;
         }
      });

      //
      // Write the file
      //
      MSPDIWriter writer = new MSPDIWriter();

      // By default timephased data is not written so we need to enable it here
      writer.setWriteTimephasedData(true);

      //
      // If you look at the resulting project in the Resource Usage view in MS Project
      // you should see the work split as 20j, 8h and 8h over the three days of the task.
      //
      writer.write(file, filename);
   }
}
