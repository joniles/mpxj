/*
 * file:       MpxQuery.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2002-2003
 * date:       13/02/2003
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

package com.tapsterrock.utility;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.tapsterrock.mpp.MPPFile;
import com.tapsterrock.mpx.MPXDuration;
import com.tapsterrock.mpx.MPXFile;
import com.tapsterrock.mpx.ProjectHeader;
import com.tapsterrock.mpx.Resource;
import com.tapsterrock.mpx.ResourceAssignment;
import com.tapsterrock.mpx.Task;

/**
 * This example shows an MPP or an MPX file being read, and basic task and resource
 * data being extracted.
 */
public class MpxjQuery
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
            System.out.println ("Usage: MpxQuery <input file name>");
         }
         else
         {
            query (args[0]);
         }
      }

      catch (Exception ex)
      {
         ex.printStackTrace(System.out);
      }
   }

   /**
    * This method performs a set of queries to retrieve information
    * from the an MPP or an MPX file.
    *
    * @param filename name of the MPX file
    * @throws Exception on file read error
    */
   private static void query (String filename)
      throws Exception
   {
      MPXFile mpx = null;

      try
      {
         mpx = new MPXFile (filename);
      }

      catch (Exception ex)
      {
         mpx = null;
      }

      if (mpx == null)
      {
         try
         {
            mpx = new MPPFile (filename);
         }

         catch (Exception ex)
         {
            mpx = null;
         }
      }

      if (mpx == null)
      {
         throw new Exception ("Failed to read file");
      }

      listProjectHeader (mpx);
      
      listResources (mpx);

      listTasks (mpx);

      listAssignments (mpx);

      listAssignmentsByTask (mpx);

      listHierarchy (mpx);

      listTaskNotes (mpx);

      listResourceNotes (mpx);
   }

   /**
    * Reads basic summary details from the project header.
    * 
    * @param file MPX file
    */
   private static void listProjectHeader (MPXFile file)
   {
      SimpleDateFormat df = new SimpleDateFormat ("dd/MM/yyyy hh:mm z");      
      ProjectHeader header = file.getProjectHeader();
      Date startDate = header.getStartDate();
      Date finishDate = header.getFinishDate();
      String formattedStartDate = startDate==null?"(none)":df.format(startDate);
      String formattedFinishDate = finishDate==null?"(none)":df.format(finishDate);

      System.out.println ("Project Header: StartDate=" + formattedStartDate + " FinishDate=" + formattedFinishDate);
      System.out.println ();
   }
   
   /**
    * This method lists all resources defined in the file.
    *
    * @param file MPX file
    */
   private static void listResources (MPXFile file)
   {
      List allResources = file.getAllResources();
      Iterator iter = allResources.iterator();
      Resource resource;

      while (iter.hasNext() == true)
      {
         resource = (Resource)iter.next();
         System.out.println ("Resource: " + resource.getName() + " (Unique ID=" + resource.getUniqueID()+")");
      }
      System.out.println ();
   }


   /**
    * This method lists all tasks defined in the file.
    *
    * @param file MPX file
    */
   private static void listTasks (MPXFile file)
   {
      SimpleDateFormat df = new SimpleDateFormat ("dd/MM/yyyy hh:mm z");
      List allTasks = file.getAllTasks();
      Iterator iter = allTasks.iterator();
      Task task;
      String startDate;
      String finishDate;
      String duration;
      Date date;
      MPXDuration dur;

      while (iter.hasNext() == true)
      {
         task = (Task)iter.next();

         date = task.getStart();
         if (date != null)
         {
            startDate = df.format(date);
         }
         else
         {
            startDate = "(no date supplied)";
         }

         date = task.getFinish();
         if (date != null)
         {
            finishDate = df.format(date);
         }
         else
         {
            finishDate = "(no date supplied)";
         }

         dur = task.getDuration();
         if (dur != null)
         {
            duration = dur.toString();
         }
         else
         {
            duration = "(no duration supplied)";
         }

         System.out.println ("Task: " + task.getName() + " (Start Date=" + startDate + " Finish Date=" + finishDate + " Duration=" + duration + " Outline Level=" + task.getOutlineLevel() + " Outline Number=" + task.getOutlineNumber() + " Unique ID=" + task.getUniqueID() + ")");
      }
      System.out.println ();
   }


   /**
    * This method lists all tasks defined in the file in a hierarchical
    * format, reflecting the parent-child relationships between them.
    *
    * @param file MPX file
    */
   private static void listHierarchy (MPXFile file)
   {
      List tasks = file.getChildTasks();
      Iterator iter = tasks.iterator();
      Task task;

      while (iter.hasNext() == true)
      {
         task = (Task)iter.next();
         System.out.println ("Task: " + task.getName());
         listHierarchy (task, " ");
      }

      System.out.println ();
   }

   /**
    * Helper method called recursively to list child tasks.
    *
    * @param task task whose children are to be displayed
    * @param indent whitespace used to indent hierarchy levels
    */
   private static void listHierarchy (Task task, String indent)
   {
      List tasks = task.getChildTasks();
      Iterator iter = tasks.iterator();
      Task child;

      while (iter.hasNext() == true)
      {
         child = (Task)iter.next();
         System.out.println (indent + "Task: " + child.getName());
         listHierarchy (child, indent + " ");
      }
   }

   /**
    * This method lists all resource assignments defined in the file.
    *
    * @param file MPX file
    */
   private static void listAssignments (MPXFile file)
   {
      List allAssignments = file.getAllResourceAssignments();
      Iterator iter = allAssignments.iterator();
      ResourceAssignment assignment;
      Task task;
      Resource resource;

      while (iter.hasNext() == true)
      {
         assignment = (ResourceAssignment)iter.next();
         task = assignment.getTask ();
         resource = assignment.getResource ();
         System.out.println ("Assignment: Task=" + task.getName() + " Resource=" + resource.getName());
      }

      System.out.println ();
   }

   /**
    * This method displays the resource assignemnts for each task. This time
    * rather than just iterating through the list of all assignments in
    * the file, we extract the assignemnts on a task-by-task basis.
    *
    * @param file MPX file
    */
   private static void listAssignmentsByTask (MPXFile file)
   {
      List tasks = file.getAllTasks();
      Iterator taskIter = tasks.iterator();

      while (taskIter.hasNext() == true)
      {
         Task task = (Task)taskIter.next();
         System.out.println ("Assignments for " + task.getName() + ":");

         List assignments = task.getResourceAssignments();
         Iterator assignmentIter = assignments.iterator();

         while (assignmentIter.hasNext() == true)
         {
            ResourceAssignment assignment = (ResourceAssignment)assignmentIter.next();
            Resource resource = assignment.getResource();
            System.out.println ("   " + resource.getName());
         }
      }

      System.out.println ();
   }


   /**
    * This method lists any notes attached to tasks.
    *
    * @param file MPX file
    */
   private static void listTaskNotes (MPXFile file)
   {
      List tasks = file.getAllTasks();
      Iterator taskIter = tasks.iterator();

      while (taskIter.hasNext() == true)
      {
         Task task = (Task)taskIter.next();
         String notes = task.getNotes();

         if (notes != null && notes.length() != 0)
         {
            System.out.println ("Notes for " + task.getName() + ": " + notes);
         }
      }

      System.out.println ();
   }

   /**
    * This method lists any notes attached to resources.
    *
    * @param file MPX file
    */
   private static void listResourceNotes (MPXFile file)
   {
      List resources = file.getAllResources();
      Iterator resourceIter = resources.iterator();

      while (resourceIter.hasNext() == true)
      {
         Resource resource = (Resource)resourceIter.next();
         String notes = resource.getNotes();

         if (notes != null && notes.length() != 0)
         {
            System.out.println ("Notes for " + resource.getName() + ": " + notes);
         }
      }

      System.out.println ();
   }

}

