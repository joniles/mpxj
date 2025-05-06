/*
 * file:       MpxjFilter.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2009
 * date:       03/05/2009
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

import org.mpxj.Filter;
import org.mpxj.ProjectFile;
import org.mpxj.Resource;
import org.mpxj.Task;
import org.mpxj.reader.UniversalProjectReader;

/**
 * This example shows tasks or resources being read from a project file,
 * a filter applied to the list, and the results displayed.
 * Executing this utility without a valid filter name will result in
 * the list of available filters being displayed.
 */
public class MpxjFilter
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
         if (args.length != 2)
         {
            System.out.println("Usage: MpxFilter <input file name> <filter name>");
         }
         else
         {
            filter(args[0], args[1]);
         }
      }

      catch (Exception ex)
      {
         ex.printStackTrace(System.out);
      }
   }

   /**
    * This method opens the named project, applies the named filter
    * and displays the filtered list of tasks or resources. If an
    * invalid filter name is supplied, a list of valid filter names
    * is shown.
    *
    * @param filename input file name
    * @param filtername input filter name
    */
   private static void filter(String filename, String filtername) throws Exception
   {
      ProjectFile project = new UniversalProjectReader().read(filename);
      Filter filter = project.getFilters().getFilterByName(filtername);

      if (filter == null)
      {
         displayAvailableFilters(project);
      }
      else
      {
         System.out.println(filter);
         System.out.println();

         if (filter.isTaskFilter())
         {
            processTaskFilter(project, filter);
         }
         else
         {
            processResourceFilter(project, filter);
         }
      }
   }

   /**
    * This utility displays a list of available task filters, and a
    * list of available resource filters.
    *
    * @param project project file
    */
   private static void displayAvailableFilters(ProjectFile project)
   {
      System.out.println("Unknown filter name supplied.");
      System.out.println("Available task filters:");
      for (Filter filter : project.getFilters().getTaskFilters())
      {
         System.out.println("   " + filter.getName());
      }

      System.out.println("Available resource filters:");
      for (Filter filter : project.getFilters().getResourceFilters())
      {
         System.out.println("   " + filter.getName());
      }

   }

   /**
    * Apply a filter to the list of all tasks, and show the results.
    *
    * @param project project file
    * @param filter filter
    */
   private static void processTaskFilter(ProjectFile project, Filter filter)
   {
      for (Task task : project.getTasks())
      {
         if (filter.evaluate(task, null))
         {
            System.out.println(task.getID() + "," + task.getUniqueID() + "," + task.getName());
         }
      }
   }

   /**
    * Apply a filter to the list of all resources, and show the results.
    *
    * @param project project file
    * @param filter filter
    */
   private static void processResourceFilter(ProjectFile project, Filter filter)
   {
      for (Resource resource : project.getResources())
      {
         if (filter.evaluate(resource, null))
         {
            System.out.println(resource.getID() + "," + resource.getUniqueID() + "," + resource.getName());
         }
      }
   }

}
