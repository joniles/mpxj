using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using net.sf.mpxj;
using net.sf.mpxj.writer;
using net.sf.mpxj.common;
using net.sf.mpxj.MpxjUtilities;

namespace MpxjSample
{
    class MpxjCreate
    {
        static void Main(string[] args)
        {
            try
            {
                if (args.Length != 1)
                {
                    Console.Out.WriteLine("Usage: MpxjCreate <output file name>");
                }
                else
                {
                    MpxjCreate create = new MpxjCreate();
                    create.process(args[0]);
                }
            }

            catch (Exception ex)
            {
                Console.Out.WriteLine(ex.ToString());
            }
        }

        public void process(string filename)
        {
            //
            // Create a ProjectFile instance
            //
            ProjectFile file = new ProjectFile();

            //
            // Uncomment these lines to test the use of alternative
            // delimiters and separators for MPX file output.
            //
            //file.setDelimiter(';');
            //file.setDecimalSeparator(',');
            //file.setThousandsSeparator('.');

            //
            // Add a default calendar called "Standard"
            //
            ProjectCalendar calendar = file.addDefaultBaseCalendar();

            //
            // Add a holiday to the calendar to demonstrate calendar exceptions
            //
            calendar.addCalendarException(ParseDate("2006-03-13"), ParseDate("2006-03-13"));

            //
            // Retrieve the project properties and set the start date. Note Microsoft
            // Project appears to reset all task dates relative to this date, so this
            // date must match the start date of the earliest task for you to see
            // the expected results. If this value is not set, it will default to
            // today's date.
            //
            ProjectProperties properties = file.ProjectProperties;
            properties.StartDate = ParseDate("2003-01-01");

            //
            // Set a couple more properties just for fun
            //
            properties.ProjectTitle = "Created by MPXJ";
            properties.Author = "Jon Iles";

            //
            // Add resources
            //
            Resource resource1 = file.addResource();
            resource1.Name = "Resource1";

            Resource resource2 = file.addResource();
            resource2.Name = "Resource2";

            //
            // This next line is not required, it is here simply to test the
            // output file format when alternative separators and delimiters
            // are used.
            //
            resource2.MaxUnits = NumberHelper.getDouble(50.0);

            //
            // Create a summary task
            //
            Task task1 = file.addTask();
            task1.Name = "Summary Task";

            //
            // Create the first sub task
            //
            Task task2 = task1.addTask();
            task2.Name = "First Sub Task";
            task2.Duration = Duration.getInstance(10.5, TimeUnit.DAYS);
            task2.Start = ParseDate("2003-01-01");

            //
            // We'll set this task up as being 50% complete. If we have no resource
            // assignments for this task, this is enough information for MS Project.
            // If we do have resource assignments, the assignment record needs to
            // contain the corresponding work and actual work fields set to the
            // correct values in order for MS project to mark the task as complete
            // or partially complete.
            //
            task2.PercentageComplete = NumberHelper.getDouble(50.0);
            task2.ActualStart = ParseDate("2003-01-01");

            //
            // Create the second sub task
            //
            Task task3 = task1.addTask();
            task3.Name = "Second Sub Task";
            task3.Start = ParseDate("2003-01-11");
            task3.Duration = Duration.getInstance(10, TimeUnit.DAYS);

            //
            // Link these two tasks
            //
            task3.addPredecessor(task2, RelationType.FINISH_START, null);

            //
            // Add a milestone
            //
            Task milestone1 = task1.addTask();
            milestone1.Name = "Milestone";
            milestone1.Start = ParseDate("2003-01-21");
            milestone1.Duration = Duration.getInstance(0, TimeUnit.DAYS);
            milestone1.addPredecessor(task3, RelationType.FINISH_START, null);

            //
            // This final task has a percent complete value, but no
            // resource assignments. This is an interesting case it it requires
            // special processing to generate the MSPDI file correctly.
            //
            Task task4 = file.addTask();
            task4.Name = "Next Task";
            task4.Duration = Duration.getInstance(8, TimeUnit.DAYS);
            task4.Start = ParseDate("2003-01-01");
            task4.PercentageComplete = NumberHelper.getDouble(70.0);
            task4.ActualStart = ParseDate("2003-01-01");

            //
            // Assign resources to tasks
            //
            ResourceAssignment assignment1 = task2.addResourceAssignment(resource1);
            ResourceAssignment assignment2 = task3.addResourceAssignment(resource2);

            //
            // As the first task is partially complete, and we are adding
            // a resource assignment, we must set the work and actual work
            // fields in the assignment to appropriate values, or MS Project
            // won't recognise the task as being complete or partially complete
            //
            assignment1.Work = Duration.getInstance(80, TimeUnit.HOURS);
            assignment1.ActualWork = Duration.getInstance(40, TimeUnit.HOURS);

            //
            // If we were just generating an MPX file, we would already have enough
            // attributes set to create the file correctly. If we want to generate
            // an MSPDI file, we must also set the assignment start dates and
            // the remaining work attribute. The assignment start dates will normally
            // be the same as the task start dates.
            //
            assignment1.RemainingWork = Duration.getInstance(40, TimeUnit.HOURS);
            assignment2.RemainingWork = Duration.getInstance(80, TimeUnit.HOURS);
            assignment1.Start = ParseDate("2003-01-01");
            assignment2.Start = ParseDate("2003-01-11");

            //
            // Write a 100% complete task
            //
            Task task5 = file.addTask();
            task5.Name = "Last Task";
            task5.Duration = Duration.getInstance(3, TimeUnit.DAYS);
            task5.Start = ParseDate("2003-01-01");
            task5.PercentageComplete = NumberHelper.getDouble(100.0);
            task5.ActualStart = ParseDate("2003-01-01");

            //
            // Write the file
            //
            ProjectWriter writer = ProjectWriterUtility.getProjectWriter(filename);
            writer.write(file, filename);

        }

        private java.util.Date ParseDate(string date)
        {
            return DateTime.Parse(date).ToJavaDate();
        }
    }
}
