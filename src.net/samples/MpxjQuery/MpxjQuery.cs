using System;
using net.sf.mpxj;
using net.sf.mpxj.MpxjUtilities;
using net.sf.mpxj.reader;

namespace MpxjQuery
{
    class MpxjQuery
    {
        /// <summary>
        /// Main entry point.
        /// </summary>
        /// <param name="args">command line arguments</param>
        static void Main(string[] args)
        {
            try
            {
                if (args.Length != 1)
                {
                    System.Console.WriteLine("Usage: MpxQuery <input file name>");
                }
                else
                {
                    query(args[0]);
                }
            }

            catch (Exception ex)
            {
                System.Console.WriteLine(ex.StackTrace);
            }

        }

        /// <summary>
        /// This method performs a set of queries to retrieve information
        /// from the an MPP or an MPX file.
        /// </summary>
        /// <param name="filename">name of the project file</param>
        private static void query(String filename)
        {
            ProjectReader reader = ProjectReaderUtility.getProjectReader(filename);
            ProjectFile mpx = reader.read(filename);

            System.Console.WriteLine("MPP file type: " + mpx.ProjectProperties.MppFileType);

            listProjectHeader(mpx);

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

            listCustomFields(mpx);
        }

        /// <summary>
        /// Reads basic summary details from the project header.
        /// </summary>
        /// <param name="file">project file</param>
        private static void listProjectHeader(ProjectFile file)
        {
            ProjectProperties header = file.ProjectProperties;
            String formattedStartDate = header.StartDate == null ? "(none)" : header.StartDate.ToDateTime().ToString();
            String formattedFinishDate = header.FinishDate == null ? "(none)" : header.FinishDate.ToDateTime().ToString();

            System.Console.WriteLine("Project Header: StartDate=" + formattedStartDate + " FinishDate=" + formattedFinishDate);
            System.Console.WriteLine();
        }

        /// <summary>
        /// This method lists all resources defined in the file.
        /// </summary>
        /// <param name="file">project file</param>
        private static void listResources(ProjectFile file)
        {
            foreach (Resource resource in file.Resources.ToIEnumerable())
            {
                System.Console.WriteLine("Resource: " + resource.Name + " (Unique ID=" + resource.UniqueID + ") Start=" + resource.Start + " Finish=" + resource.Finish);
            }
            System.Console.WriteLine();
        }

        /// <summary>
        /// This method lists all tasks defined in the file.
        /// </summary>
        /// <param name="file">project file</param>
        private static void listTasks(ProjectFile file)
        {
            foreach (Task task in file.Tasks.ToIEnumerable())
            {
                String startDate;
                String finishDate;
                String duration;
                Duration dur;

                var date = task.Start;
                if (date != null)
                {
                    startDate = date.ToDateTime().ToString();
                }
                else
                {
                    startDate = "(no date supplied)";
                }

                date = task.Finish;
                if (date != null)
                {
                    finishDate = date.ToDateTime().ToString();
                }
                else
                {
                    finishDate = "(no date supplied)";
                }

                dur = task.Duration;
                if (dur != null)
                {
                    duration = dur.toString();
                }
                else
                {
                    duration = "(no duration supplied)";
                }

                String baselineDuration = task.BaselineDurationText;
                if (baselineDuration == null)
                {
                    dur = task.BaselineDuration;
                    if (dur != null)
                    {
                        baselineDuration = dur.toString();
                    }
                    else
                    {
                        baselineDuration = "(no duration supplied)";
                    }
                }

                System.Console.WriteLine("Task: " + task.Name + " ID=" + task.ID + " Unique ID=" + task.UniqueID + " (Start Date=" + startDate + " Finish Date=" + finishDate + " Duration=" + duration + " Baseline Duration=" + baselineDuration + " Outline Level=" + task.OutlineLevel + " Outline Number=" + task.OutlineNumber + " Recurring=" + task.Recurring + ")");
            }
            System.Console.WriteLine();
        }

        /// <summary>
        /// This method lists all tasks defined in the file in a hierarchical format, 
        /// reflecting the parent-child relationships between them.
        /// </summary>
        /// <param name="file">project file</param>
        private static void listHierarchy(ProjectFile file)
        {
            foreach (Task task in file.ChildTasks.ToIEnumerable())
            {
                System.Console.WriteLine("Task: " + task.Name);
                listHierarchy(task, " ");
            }

            System.Console.WriteLine();
        }

        /// <summary>
        /// Helper method called recursively to list child tasks.
        /// </summary>
        /// <param name="task">Task instance</param>
        /// <param name="indent">print indent</param>
        private static void listHierarchy(Task task, String indent)
        {
            foreach (Task child in task.ChildTasks.ToIEnumerable())
            {
                System.Console.WriteLine(indent + "Task: " + child.Name);
                listHierarchy(child, indent + " ");
            }
        }

        /// <summary>
        /// This method lists all resource assignments defined in the file.
        /// </summary>
        /// <param name="file">project file</param>
        private static void listAssignments(ProjectFile file)
        {
            Task task;
            Resource resource;
            String taskName;
            String resourceName;

            foreach (ResourceAssignment assignment in file.ResourceAssignments.ToIEnumerable())
            {
                task = assignment.Task;
                if (task == null)
                {
                    taskName = "(null task)";
                }
                else
                {
                    taskName = task.Name;
                }

                resource = assignment.Resource;
                if (resource == null)
                {
                    resourceName = "(null resource)";
                }
                else
                {
                    resourceName = resource.Name;
                }

                System.Console.WriteLine("Assignment: Task=" + taskName + " Resource=" + resourceName);
            }

            System.Console.WriteLine();
        }

        /// <summary>
        /// This method displays the resource assignments for each task. 
        /// This time rather than just iterating through the list of all 
        /// assignments in the file, we extract the assignments on a task-by-task basis.
        /// </summary>
        /// <param name="file">project file</param>
        private static void listAssignmentsByTask(ProjectFile file)
        {
            foreach (Task task in file.Tasks.ToIEnumerable())
            {
                System.Console.WriteLine("Assignments for task " + task.Name + ":");

                foreach (ResourceAssignment assignment in task.ResourceAssignments.ToIEnumerable())
                {
                    Resource resource = assignment.Resource;
                    String resourceName;

                    if (resource == null)
                    {
                        resourceName = "(null resource)";
                    }
                    else
                    {
                        resourceName = resource.Name;
                    }

                    System.Console.WriteLine("   " + resourceName);
                }
            }

            System.Console.WriteLine();
        }

        /// <summary>
        /// This method displays the resource assignments for each resource. 
        /// This time rather than just iterating through the list of all 
        /// assignments in the file, we extract the assignments on a resource-by-resource basis.
        /// </summary>
        /// <param name="file">project file</param>
        private static void listAssignmentsByResource(ProjectFile file)
        {
            foreach (Resource resource in file.Resources.ToIEnumerable())
            {
                System.Console.WriteLine("Assignments for resource " + resource.Name + ":");

                foreach (ResourceAssignment assignment in resource.TaskAssignments.ToIEnumerable())
                {
                    Task task = assignment.Task;
                    System.Console.WriteLine("   " + task.Name);
                }
            }

            System.Console.WriteLine();
        }

        /// <summary>
        /// This method lists any notes attached to tasks..
        /// </summary>
        /// <param name="file">project file</param>
        private static void listTaskNotes(ProjectFile file)
        {
            foreach (Task task in file.Tasks.ToIEnumerable())
            {
                String notes = task.Notes;

                if (notes != null && notes.Length != 0)
                {
                    System.Console.WriteLine("Notes for " + task.Name + ": " + notes);
                }
            }

            System.Console.WriteLine();
        }

        /// <summary>
        /// This method lists any notes attached to resources.
        /// </summary>
        /// <param name="file">project file</param>
        private static void listResourceNotes(ProjectFile file)
        {
            foreach (Resource resource in file.Resources.ToIEnumerable())
            {
                String notes = resource.Notes;

                if (notes != null && notes.Length != 0)
                {
                    System.Console.WriteLine("Notes for " + resource.Name + ": " + notes);
                }
            }

            System.Console.WriteLine();
        }

        /// <summary>
        /// This method lists task predecessor and successor relationships.
        /// </summary>
        /// <param name="file">project file</param>
        private static void listRelationships(ProjectFile file)
        {
            foreach (Task task in file.Tasks.ToIEnumerable())
            {
                System.Console.Write(task.ID);
                System.Console.Write('\t');
                System.Console.Write(task.Name);
                System.Console.Write('\t');

                dumpRelationList(task.Predecessors);
                System.Console.Write('\t');
                dumpRelationList(task.Successors);
                System.Console.WriteLine();
            }
        }

        /// <summary>
        /// Internal utility to dump relationship lists in a structured format that can 
        /// easily be compared with the tabular data in MS Project.
        /// </summary>
        /// <param name="relations">project file</param>
        private static void dumpRelationList(java.util.List relations)
        {
            if (relations != null && relations.isEmpty() == false)
            {
                if (relations.size() > 1)
                {
                    System.Console.Write('"');
                }
                bool first = true;
                foreach (Relation relation in relations.ToIEnumerable())
                {
                    if (!first)
                    {
                        System.Console.Write(',');
                    }
                    first = false;
                    System.Console.Write(relation.TargetTask.ID);
                    Duration lag = relation.Lag;
                    if (relation.Type != RelationType.FINISH_START || lag.Duration != 0)
                    {
                        System.Console.Write(relation.Type);
                    }

                    if (lag.Duration != 0)
                    {
                        if (lag.Duration > 0)
                        {
                            System.Console.Write("+");
                        }
                        System.Console.Write(lag);
                    }
                }
                if (relations.size() > 1)
                {
                    System.Console.Write('"');
                }
            }
        }

        /// <summary>
        /// List the slack values for each task.
        /// </summary>
        /// <param name="file">project file</param>
        private static void listSlack(ProjectFile file)
        {
            foreach (Task task in file.Tasks.ToIEnumerable())
            {
                System.Console.WriteLine(task.Name + " Total Slack=" + task.TotalSlack + " Start Slack=" + task.StartSlack + " Finish Slack=" + task.FinishSlack);
            }
        }

        /// <summary>
        /// List details of all calendars in the file.
        /// </summary>
        /// <param name="file">project file</param>
        private static void listCalendars(ProjectFile file)
        {
            foreach (ProjectCalendar cal in file.Calendars.ToIEnumerable())
            {
                System.Console.WriteLine(cal.toString());
            }
        }

        /// <summary>
        /// List details of custom fields in the file.
        /// </summary>
        /// <param name="file">project file</param>
        private static void listCustomFields(ProjectFile file)
        {
            foreach (CustomField field in file.CustomFields.ToIEnumerable())
            {
                System.Console.WriteLine(field.toString());
            }
        }
    }
}
