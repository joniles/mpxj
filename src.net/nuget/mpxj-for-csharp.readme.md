# MPXJ for C#

[MPXJ](http://mpxj.org) is a Java library which allows a variety of project
file formats and databases to be read and written. This NuGet package uses
[IKVM](https://github.com/ikvm-revived/ikvm) to translate the Java library into a .Net assembly.
You can find details of the classes and methods in the [Javadocs](http://www.mpxj.org/apidocs/index.html),
and more general documentation on the [MPXJ website](https://www.mpxj.org/).

This version is not a direct translation of the original Java library: instead
it provides C# properties rather than requiring the use of getter and setter
methods, and where possible adjusting method names to being with an uppercase
letter to allow more idiomatic C# to be written when using this library.

A version of this assembly is available from NuGet
([MPXJ](https://www.nuget.org/packages/net.sf.mpxj))
which is a direct translation
of the Java library and another version of the assembly 
([MPXJ for VB](https://www.nuget.org/packages/net.sf.mpxj-for-vb))
is also available which is more
suited for use with VB.

After installing this package you can either work with some of the simple
built-in utilities provided by MPXJ for tasks like file format conversion:

```c#
using net.sf.mpxj.sample;

new MpxjConvert().process("example.mpp", "example.mpx");
```

or you can interact directly with the object model exposed by MPXJ to extract data:

```c#
using net.sf.mpxj.reader;
using Task = net.sf.mpxj.Task;

var project = new UniversalProjectReader().read("example.mpp");

System.Console.WriteLine("Tasks");
foreach (Task task in project.Tasks)
{
    System.Console.WriteLine(task.ID.toString() + "\t" + task.Name);
}
```

or finally you can generate your own schedule:

```c#
using java.text;
using net.sf.mpxj;
using net.sf.mpxj.writer;

// The helper class we use later to actually write the file
// selects the file type based on the extension of the
// filename. In the example below we'll be generating an MSPDI
// file which we can import into Microsoft Project.
var filename = "/Users/joniles/Downloads/test.xml";

// Create a simple date format to allow us to easily set date values.
var df = new SimpleDateFormat("dd/MM/yyyy");

// Create a ProjectFile instance
var file = new ProjectFile();

// Add a default calendar called "Standard"
var calendar = file.addDefaultBaseCalendar();

// Add a holiday to the calendar to demonstrate calendar exceptions
calendar.addCalendarException(df.parse("13/03/2006"), df.parse("13/03/2006"));

// Retrieve the project properties and set the start date. Note Microsoft
// Project appears to reset all task dates relative to this date, so this
// date must match the start date of the earliest task for you to see
// the expected results. If this value is not set, it will default to
// today's date.
var properties = file.ProjectProperties;
properties.StartDate = df.parse("01/01/2003");

// Set a couple more properties just for fun
properties.ProjectTitle = "Created by MPXJ";
properties.Author = "Jon Iles";

// Let's create an alias for TEXT1
var customFields = file.CustomFields;
var field = customFields.getOrCreate(TaskField.TEXT1);
field.setAlias("My Custom Field");

// Add resources
var resource1 = file.addResource();
resource1.Name = "Resource1";

var resource2 = file.addResource();
resource2.Name = "Resource2";
resource2.MaxUnits = java.lang.Double.valueOf(50.0);

// Create a summary task
var task1 = file.addTask();
task1.Name = "Summary Task";

// Create the first sub task
var task2 = task1.addTask();
task2.Name = "First Sub Task";
task2.Duration = Duration.getInstance(10.5, TimeUnit.DAYS);
task2.Start = df.parse("01/01/2003");
task2.setText(1, "My Custom Value 1");

// We'll set this task up as being 50% complete. If we have no resource
// assignments for this task, this is enough information for MS Project.
// If we do have resource assignments, the assignment record needs to
// contain the corresponding work and actual work fields set to the
// correct values in order for MS project to mark the task as complete
// or partially complete.
task2.PercentageComplete = java.lang.Double.valueOf(50.0);
task2.ActualStart = df.parse("01/01/2003");

// Create the second sub task
var task3 = task1.addTask();
task3.Name = "Second Sub Task";
task3.Start = df.parse("11/01/2003");
task3.Duration = Duration.getInstance(10, TimeUnit.DAYS);
task3.setText(1, "My Custom Value 2");

// Link these two tasks
task3.addPredecessor(task2, RelationType.FINISH_START, null);

// Add a milestone
var milestone1 = task1.addTask();
milestone1.Name = "Milestone";
milestone1.Start = df.parse("21/01/2003");
milestone1.Duration = Duration.getInstance(0, TimeUnit.DAYS);
milestone1.addPredecessor(task3, RelationType.FINISH_START, null);

// This final task has a percent complete value, but no
// resource assignments. This is an interesting case it it requires
// special processing to generate the MSPDI file correctly.
var task4 = file.addTask();
task4.Name = "Next Task";
task4.Duration = Duration.getInstance(8, TimeUnit.DAYS);
task4.Start= df.parse("01/01/2003");
task4.PercentageComplete = java.lang.Double.valueOf(70.0);
task4.ActualStart = df.parse("01/01/2003");

// Assign resources to tasks
var assignment1 = task2.addResourceAssignment(resource1);
var assignment2 = task3.addResourceAssignment(resource2);

// As the first task is partially complete, and we are adding
// a resource assignment, we must set the work and actual work
// fields in the assignment to appropriate values, or MS Project
// won't recognise the task as being complete or partially complete
assignment1.Work = Duration.getInstance(80, TimeUnit.HOURS);
assignment1.ActualWork = Duration.getInstance(40, TimeUnit.HOURS);

// If we were just generating an MPX file, we would already have enough
// attributes set to create the file correctly. If we want to generate
// an MSPDI file, we must also set the assignment start dates and
// the remaining work attribute. The assignment start dates will normally
// be the same as the task start dates.
assignment1.RemainingWork = Duration.getInstance(40, TimeUnit.HOURS);
assignment2.RemainingWork = Duration.getInstance(80, TimeUnit.HOURS);
assignment1.Start = df.parse("01/01/2003");
assignment2.Start = df.parse("11/01/2003");

// Write a 100% complete task
var task5 = file.addTask();
task5.Name = "Last Task";
task5.Duration = Duration.getInstance(3, TimeUnit.DAYS);
task5.Start = df.parse("01/01/2003");
task5.PercentageComplete = java.lang.Double.valueOf(100.0);
task5.ActualStart = df.parse("01/01/2003");

// Write a 100% complete milestone
var task6 = file.addTask();
task6.Name = "Last Milestone";
task6.Duration = Duration.getInstance(0, TimeUnit.DAYS);
task6.Start = df.parse("01/01/2003");
task6.PercentageComplete = java.lang.Double.valueOf(100.0);
task6.ActualStart = df.parse("01/01/2003");

// Write the file
var writer = ProjectWriterUtility.getProjectWriter(filename);
writer.write(file, filename);
```
