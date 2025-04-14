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

package org.mpxj.sample;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.mpxj.AssignmentField;
import org.mpxj.ChildResourceContainer;
import org.mpxj.ChildTaskContainer;
import org.mpxj.LocalDateTimeRange;
import org.mpxj.Duration;
import org.mpxj.FieldType;
import org.mpxj.ProjectCalendar;
import org.mpxj.ProjectFile;
import org.mpxj.ProjectProperties;
import org.mpxj.Relation;
import org.mpxj.RelationType;
import org.mpxj.Resource;
import org.mpxj.ResourceAssignment;
import org.mpxj.ResourceField;
import org.mpxj.Task;
import org.mpxj.TaskField;
import org.mpxj.mpp.TimescaleUnits;
import org.mpxj.reader.UniversalProjectReader;
import org.mpxj.utility.TimephasedUtility;
import org.mpxj.utility.TimescaleUtility;

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
      if (mpx == null)
      {
         throw new Exception("Unable to read file");
      }

      listProjectProperties(mpx);

      listResources(mpx);

      listTasks(mpx);

      listAssignments(mpx);

      listAssignmentsByTask(mpx);

      listAssignmentsByResource(mpx);

      listTaskHierarchy(mpx, "");

      listResourceHierarchy(mpx, "");

      listTaskNotes(mpx);

      listResourceNotes(mpx);

      listRelationships(mpx);

      listSlack(mpx);

      listCalendars(mpx);

      listPopulatedFields(mpx);

      listTasksPercentComplete(mpx);
   }

   /**
    * Reads basic summary details from the project properties.
    *
    * @param file MPX file
    */
   private static void listProjectProperties(ProjectFile file)
   {
      DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
      ProjectProperties properties = file.getProjectProperties();
      LocalDateTime startDate = properties.getStartDate();
      LocalDateTime finishDate = properties.getFinishDate();
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
      for (Resource resource : file.getResources())
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
      DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

      for (Task task : file.getTasks())
      {
         LocalDateTime date = task.getStart();
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
    * List different percent complete types for the tasks.
    *
    * @param file project file
    */
   private static void listTasksPercentComplete(ProjectFile file)
   {
      System.out.println("ID\tUniqueID\tActivity ID\tName\t%Complete Type\tDuration % Complete\tWork % Complete\tPhysical % Complete");
      for (Task task : file.getTasks())
      {
         List<Object> values = Arrays.asList(task.getID(), task.getUniqueID(), task.getActivityID(), task.getName(), task.getPercentCompleteType(), task.getPercentageComplete(), task.getPercentageWorkComplete(), task.getPhysicalPercentComplete());
         System.out.println(values.stream().map(String::valueOf).collect(Collectors.joining("\t")));
      }
      System.out.println();
   }

   /**
    * This method lists all tasks defined in the file in a hierarchical
    * format, reflecting the parent-child relationships between them.
    *
    * @param container child task container
    * @param indent current hierarchy level indent
    */
   private static void listTaskHierarchy(ChildTaskContainer container, String indent)
   {
      for (Task task : container.getChildTasks())
      {
         System.out.println(indent + "Task: " + task.getName() + "\t" + task.getStart() + "\t" + task.getFinish());
         listTaskHierarchy(task, indent + " ");
      }

      if (indent.isEmpty())
      {
         System.out.println();
      }
   }

   /**
    * This method lists all resources defined in the file in a hierarchical
    * format, reflecting the parent-child relationships between them.
    *
    * @param container child resource container
    * @param indent current hierarchy level indent
    */
   private static void listResourceHierarchy(ChildResourceContainer container, String indent)
   {
      for (Resource resource : container.getChildResources())
      {
         System.out.println(indent + "Resource: " + resource.getName());
         listResourceHierarchy(resource, indent + " ");
      }

      if (indent.isEmpty())
      {
         System.out.println();
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

      for (ResourceAssignment assignment : file.getResourceAssignments())
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

      int days = (int) ((task.getStart().until(task.getFinish(), ChronoUnit.MILLIS)) / (1000 * 60 * 60 * 24)) + 1;
      if (days > 1)
      {
         DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yy");

         TimescaleUtility timescale = new TimescaleUtility();
         ArrayList<LocalDateTimeRange> dates = timescale.createTimescale(task.getStart(), TimescaleUnits.DAYS, days);
         TimephasedUtility timephased = new TimephasedUtility();

         ArrayList<Duration> durations = timephased.segmentWork(assignment.getEffectiveCalendar(), assignment.getTimephasedWork(), TimescaleUnits.DAYS, dates);
         for (LocalDateTimeRange range : dates)
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
      for (Task task : file.getTasks())
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
      for (Resource resource : file.getResources())
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
      for (Task task : file.getTasks())
      {
         String notes = task.getNotes();

         if (!notes.isEmpty())
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
      for (Resource resource : file.getResources())
      {
         String notes = resource.getNotes();

         if (!notes.isEmpty())
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
      for (Task task : file.getTasks())
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
      if (relations != null && !relations.isEmpty())
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
            System.out.print(relation.getPredecessorTask().getID());
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
      for (Task task : file.getTasks())
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

   /**
    * List details of all fields with non-default value.
    *
    * @param file ProjectFile instance
    */
   private static void listPopulatedFields(ProjectFile file)
   {
      Set<FieldType> tasks = file.getTasks().getPopulatedFields();
      Set<FieldType> resources = file.getResources().getPopulatedFields();
      Set<FieldType> assignments = file.getResourceAssignments().getPopulatedFields();

      System.out.println("Populated task fields: " + tasks.size() + "/" + TaskField.values().length);
      System.out.println("Populated resource fields: " + resources.size() + "/" + ResourceField.values().length);
      System.out.println("Populated assignment fields: " + assignments.size() + "/" + AssignmentField.values().length);
      System.out.println();

      System.out.println("Populated task fields:");
      tasks.forEach(System.out::println);
      System.out.println();

      System.out.println("Populated resource fields:");
      resources.forEach(System.out::println);
      System.out.println();

      System.out.println("Populated assignment fields:");
      assignments.forEach(System.out::println);
      System.out.println();
   }
}
