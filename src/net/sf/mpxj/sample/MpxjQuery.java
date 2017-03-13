/*
 * file:       MpxjQuery.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2002-2003
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

package net.sf.mpxj.sample;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.mpxj.DateRange;
import net.sf.mpxj.Duration;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ProjectProperties;
import net.sf.mpxj.Relation;
import net.sf.mpxj.RelationType;
import net.sf.mpxj.Resource;
import net.sf.mpxj.ResourceAssignment;
import net.sf.mpxj.Task;
import net.sf.mpxj.mpp.TimescaleUnits;
import net.sf.mpxj.reader.UniversalProjectReader;
import net.sf.mpxj.utility.TimephasedUtility;
import net.sf.mpxj.utility.TimescaleUtility;

/**
 * This example shows an MPP, MPX or MSPDI file being read, and basic
 * task and resource data being extracted.
 */
public class MpxjQuery
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
            System.out.println("Usage: MpxQuery <input file name>");
         }
         else
         {
            query(args[0]);
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
   private static void query(String filename) throws Exception
   {
      ProjectFile mpx = new UniversalProjectReader().read(filename);

      listProjectProperties(mpx);

      listResources(mpx);

      listTasks(mpx);

      listAssignments(mpx);

      listAssignmentsByTask(mpx);

      listAssignmentsByResource(mpx);

      listHierarchy(mpx);

      listTaskNotes(mpx);

      listResourceNotes(mpx);

      listRelationships(mpx);

      listSlack(mpx);

      listCalendars(mpx);

   }

   /**
    * Reads basic summary details from the project properties.
    *
    * @param file MPX file
    */
   private static void listProjectProperties(ProjectFile file)
   {
      SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm z");
      ProjectProperties properties = file.getProjectProperties();
      Date startDate = properties.getStartDate();
      Date finishDate = properties.getFinishDate();
      String formattedStartDate = startDate == null ? "(none)" : df.format(startDate);
      String formattedFinishDate = finishDate == null ? "(none)" : df.format(finishDate);

      System.out.println("MPP file type: " + properties.getMppFileType());
      System.out.println("Project Properties: StartDate=" + formattedStartDate + " FinishDate=" + formattedFinishDate);
      System.out.println();
   }

   /**
    * This method lists all resources defined in the file.
    *
    * @param file MPX file
    */
   private static void listResources(ProjectFile file)
   {
      for (Resource resource : file.getAllResources())
      {
         System.out.println("Resource: " + resource.getName() + " (Unique ID=" + resource.getUniqueID() + ") Start=" + resource.getStart() + " Finish=" + resource.getFinish());
      }
      System.out.println();
   }

   /**
    * This method lists all tasks defined in the file.
    *
    * @param file MPX file
    */
   private static void listTasks(ProjectFile file)
   {
      SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm z");

      for (Task task : file.getAllTasks())
      {
         Date date = task.getStart();
         String text = task.getStartText();
         String startDate = text != null ? text : (date != null ? df.format(date) : "(no start date supplied)");

         date = task.getFinish();
         text = task.getFinishText();
         String finishDate = text != null ? text : (date != null ? df.format(date) : "(no finish date supplied)");

         Duration dur = task.getDuration();
         text = task.getDurationText();
         String duration = text != null ? text : (dur != null ? dur.toString() : "(no duration supplied)");

         dur = task.getActualDuration();
         String actualDuration = dur != null ? dur.toString() : "(no actual duration supplied)";

         String baselineDuration = task.getBaselineDurationText();
         if (baselineDuration == null)
         {
            dur = task.getBaselineDuration();
            if (dur != null)
            {
               baselineDuration = dur.toString();
            }
            else
            {
               baselineDuration = "(no duration supplied)";
            }
         }

         System.out.println("Task: " + task.getName() + " ID=" + task.getID() + " Unique ID=" + task.getUniqueID() + " (Start Date=" + startDate + " Finish Date=" + finishDate + " Duration=" + duration + " Actual Duration" + actualDuration + " Baseline Duration=" + baselineDuration + " Outline Level=" + task.getOutlineLevel() + " Outline Number=" + task.getOutlineNumber() + " Recurring=" + task.getRecurring() + ")");
      }
      System.out.println();
   }

   /**
    * This method lists all tasks defined in the file in a hierarchical
    * format, reflecting the parent-child relationships between them.
    *
    * @param file MPX file
    */
   private static void listHierarchy(ProjectFile file)
   {
      for (Task task : file.getChildTasks())
      {
         System.out.println("Task: " + task.getName() + "\t" + task.getStart() + "\t" + task.getFinish());
         listHierarchy(task, " ");
      }

      System.out.println();
   }

   /**
    * Helper method called recursively to list child tasks.
    *
    * @param task task whose children are to be displayed
    * @param indent whitespace used to indent hierarchy levels
    */
   private static void listHierarchy(Task task, String indent)
   {
      for (Task child : task.getChildTasks())
      {
         System.out.println(indent + "Task: " + child.getName() + "\t" + child.getStart() + "\t" + child.getFinish());
         listHierarchy(child, indent + " ");
      }
   }

   /**
    * This method lists all resource assignments defined in the file.
    *
    * @param file MPX file
    */
   private static void listAssignments(ProjectFile file)
   {
      Task task;
      Resource resource;
      String taskName;
      String resourceName;

      for (ResourceAssignment assignment : file.getAllResourceAssignments())
      {
         task = assignment.getTask();
         if (task == null)
         {
            taskName = "(null task)";
         }
         else
         {
            taskName = task.getName();
         }

         resource = assignment.getResource();
         if (resource == null)
         {
            resourceName = "(null resource)";
         }
         else
         {
            resourceName = resource.getName();
         }

         System.out.println("Assignment: Task=" + taskName + " Resource=" + resourceName);
         if (task != null)
         {
            listTimephasedWork(assignment);
         }
      }

      System.out.println();
   }

   /**
    * Dump timephased work for an assignment.
    *
    * @param assignment resource assignment
    */
   private static void listTimephasedWork(ResourceAssignment assignment)
   {
      Task task = assignment.getTask();
      int days = (int) ((task.getFinish().getTime() - task.getStart().getTime()) / (1000 * 60 * 60 * 24)) + 1;
      if (days > 1)
      {
         SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy");

         TimescaleUtility timescale = new TimescaleUtility();
         ArrayList<DateRange> dates = timescale.createTimescale(task.getStart(), TimescaleUnits.DAYS, days);
         TimephasedUtility timephased = new TimephasedUtility();

         ArrayList<Duration> durations = timephased.segmentWork(assignment.getCalendar(), assignment.getTimephasedWork(), TimescaleUnits.DAYS, dates);
         for (DateRange range : dates)
         {
            System.out.print(df.format(range.getStart()) + "\t");
         }
         System.out.println();
         for (Duration duration : durations)
         {
            System.out.print(duration.toString() + "        ".substring(0, 7) + "\t");
         }
         System.out.println();
      }
   }

   /**
    * This method displays the resource assignments for each task. This time
    * rather than just iterating through the list of all assignments in
    * the file, we extract the assignments on a task-by-task basis.
    *
    * @param file MPX file
    */
   private static void listAssignmentsByTask(ProjectFile file)
   {
      for (Task task : file.getAllTasks())
      {
         System.out.println("Assignments for task " + task.getName() + ":");

         for (ResourceAssignment assignment : task.getResourceAssignments())
         {
            Resource resource = assignment.getResource();
            String resourceName;

            if (resource == null)
            {
               resourceName = "(null resource)";
            }
            else
            {
               resourceName = resource.getName();
            }

            System.out.println("   " + resourceName);
         }
      }

      System.out.println();
   }

   /**
    * This method displays the resource assignments for each resource. This time
    * rather than just iterating through the list of all assignments in
    * the file, we extract the assignments on a resource-by-resource basis.
    *
    * @param file MPX file
    */
   private static void listAssignmentsByResource(ProjectFile file)
   {
      for (Resource resource : file.getAllResources())
      {
         System.out.println("Assignments for resource " + resource.getName() + ":");

         for (ResourceAssignment assignment : resource.getTaskAssignments())
         {
            Task task = assignment.getTask();
            System.out.println("   " + task.getName());
         }
      }

      System.out.println();
   }

   /**
    * This method lists any notes attached to tasks.
    *
    * @param file MPX file
    */
   private static void listTaskNotes(ProjectFile file)
   {
      for (Task task : file.getAllTasks())
      {
         String notes = task.getNotes();

         if (notes.length() != 0)
         {
            System.out.println("Notes for " + task.getName() + ": " + notes);
         }
      }

      System.out.println();
   }

   /**
    * This method lists any notes attached to resources.
    *
    * @param file MPX file
    */
   private static void listResourceNotes(ProjectFile file)
   {
      for (Resource resource : file.getAllResources())
      {
         String notes = resource.getNotes();

         if (notes.length() != 0)
         {
            System.out.println("Notes for " + resource.getName() + ": " + notes);
         }
      }

      System.out.println();
   }

   /**
    * This method lists task predecessor and successor relationships.
    *
    * @param file project file
    */
   private static void listRelationships(ProjectFile file)
   {
      for (Task task : file.getAllTasks())
      {
         System.out.print(task.getID());
         System.out.print('\t');
         System.out.print(task.getName());
         System.out.print('\t');

         dumpRelationList(task.getPredecessors());
         System.out.print('\t');
         dumpRelationList(task.getSuccessors());
         System.out.println();
      }
   }

   /**
    * Internal utility to dump relationship lists in a structured format
    * that can easily be compared with the tabular data in MS Project.
    *
    * @param relations relation list
    */
   private static void dumpRelationList(List<Relation> relations)
   {
      if (relations != null && relations.isEmpty() == false)
      {
         if (relations.size() > 1)
         {
            System.out.print('"');
         }
         boolean first = true;
         for (Relation relation : relations)
         {
            if (!first)
            {
               System.out.print(',');
            }
            first = false;
            System.out.print(relation.getTargetTask().getID());
            Duration lag = relation.getLag();
            if (relation.getType() != RelationType.FINISH_START || lag.getDuration() != 0)
            {
               System.out.print(relation.getType());
            }

            if (lag.getDuration() != 0)
            {
               if (lag.getDuration() > 0)
               {
                  System.out.print("+");
               }
               System.out.print(lag);
            }
         }
         if (relations.size() > 1)
         {
            System.out.print('"');
         }
      }
   }

   /**
    * List the slack values for each task.
    *
    * @param file ProjectFile instance
    */
   private static void listSlack(ProjectFile file)
   {
      for (Task task : file.getAllTasks())
      {
         System.out.println(task.getName() + " Total Slack=" + task.getTotalSlack() + " Start Slack=" + task.getStartSlack() + " Finish Slack=" + task.getFinishSlack());
      }
   }

   /**
    * List details of all calendars in the file.
    *
    * @param file ProjectFile instance
    */
   private static void listCalendars(ProjectFile file)
   {
      for (ProjectCalendar cal : file.getCalendars())
      {
         System.out.println(cal.toString());
      }
   }
}
