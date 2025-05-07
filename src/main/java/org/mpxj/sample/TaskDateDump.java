/*
 * file:       TaskDateDump.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2010
 * date:       14/10/2014
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
import java.time.format.DateTimeFormatter;

import org.mpxj.ProjectFile;
import org.mpxj.Task;
import org.mpxj.reader.UniversalProjectReader;

/**
 * Simple data dump utility. Originally written to allow simple comparison
 * of data read by MPXJ, with activity data exported from Primavera to an Excel spreadsheet.
 */
public class TaskDateDump
{
   private final DateTimeFormatter m_df = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

   /**
    * Command line entry point.
    *
    * @param args command line arguments
    */
   public static void main(String[] args)
   {
      try
      {
         if (args.length != 1)
         {
            System.out.println("Usage: TaskDateDump <input file name>");
         }
         else
         {
            TaskDateDump tdd = new TaskDateDump();
            tdd.process(args[0]);
         }
      }

      catch (Exception ex)
      {
         ex.printStackTrace(System.out);
      }
   }

   /**
    * Dump data for all non-summary tasks to stdout.
    *
    * @param name file name
    */
   public void process(String name) throws Exception
   {
      ProjectFile file = new UniversalProjectReader().read(name);
      for (Task task : file.getTasks())
      {
         if (!task.getSummary())
         {
            System.out.print(task.getWBS());
            System.out.print("\t");
            System.out.print(task.getName());
            System.out.print("\t");
            System.out.print(format(task.getStart()));
            System.out.print("\t");
            System.out.print(format(task.getActualStart()));
            System.out.print("\t");
            System.out.print(format(task.getFinish()));
            System.out.print("\t");
            System.out.print(format(task.getActualFinish()));
            System.out.println();
         }
      }
   }

   /**
    * Format a date for ease of comparison.
    *
    * @param date raw date
    * @return formatted date
    */
   private String format(LocalDateTime date)
   {
      return date == null ? "" : m_df.format(date);
   }
}
