/*
 * file:       MpxCreate.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2002-2003
 * date:       08/02/2003
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

import com.tapsterrock.mpx.MPXFile;
import com.tapsterrock.mpx.Task;
import com.tapsterrock.mpx.MPXDuration;
import com.tapsterrock.mpx.TimeUnit;
import com.tapsterrock.mpx.Relation;
import java.text.SimpleDateFormat;


/**
 * This example illustrates creation of an MPX file from scratch.
 */
public class MpxCreate
{
   /**
    * Main method.
    *
    * @param args array of command line arguments
    */
   public static void main (String[] args)
   {
      try
      {
         if (args.length != 1)
         {
            System.out.println ("Usage: MppCreate <output file name>");
         }
         else
         {
            create (args[0]);
         }
      }

      catch (Exception ex)
      {
         ex.printStackTrace(System.out);
      }
   }

   /**
    * This method creates a summary task, two sub-tasks and a milestone,
    * all with the appropriate constraints between them.
    */
   private static void create (String filename)
      throws Exception
   {
      //
      // Create a simple date format to allow us to
      // easily set date values.
      //
      SimpleDateFormat df = new SimpleDateFormat ("dd/mm/yyyy");

      //
      // Create an empty MPX file
      //
      MPXFile file = new MPXFile ();

      //
      // Configure the file to automatically generate identifiers for tasks.
      //
      file.setAutoTaskID(true);
      file.setAutoTaskUniqueID(true);

      //
      // Configure the file to automatically generate outline levels
      //
      file.setAutoOutlineLevel(true);

      //
      // Configure the file to automatically generate WBS labels
      //
      file.setAutoWBS(true);

      //
      // Add a default calendar called "Standard"
      //
      file.addDefaultBaseCalendar();

      //
      // Create a summary task
      Task task1 = file.addTask();
      task1.setName ("Summary Task");

      //
      // Create the first sub task
      //
      Task task2 = task1.addTask();
      task2.setName ("First Sub Task");
      task2.setStart (df.parse("01/01/2003"));
      task2.setDuration (new MPXDuration (10, TimeUnit.DAYS));

      //
      // Create the second sub task
      //
      Task task3 = task1.addTask();
      task3.setName ("Second Sub Task");
      task3.setStart (df.parse("11/01/2003"));
      task3.setDuration (new MPXDuration (10, TimeUnit.DAYS));

      //
      // Link these two tasks
      //
      Relation rel1 = task3.addPredecessor (task2);
      rel1.setType(Relation.FINISH_START);

      //
      // Add a milestone
      //
      Task milestone1 = task1.addTask();
      milestone1.setName ("Milestone");
      milestone1.setStart (df.parse("21/01/2003"));
      milestone1.setDuration (new MPXDuration (0, TimeUnit.DAYS));
      Relation rel2 = milestone1.addPredecessor (task3);
      rel2.setType (Relation.FINISH_START);

      //
      // Write the file
      //
      file.write (filename);
   }

}

