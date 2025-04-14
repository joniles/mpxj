# MPXJ

[MPXJ](http://mpxj.org) is a Java library which allows a variety of project
file formats and databases to be read and written. This Python package uses
the [JPype](https://pypi.org/project/JPype1/) Java bridge to allow direct
access from Python to the MPXJ library. You can find details of the Java
classes and methods in the [Javadocs](http://www.mpxj.org/apidocs/index.html), and more general documentation
on the [MPXJ website](https://www.mpxj.org/).

After installing this package you can either work with some of the simple built-in
utilities provided by MPXJ for tasks like file format conversion:

```python
import jpype
import mpxj

jpype.startJVM()
from org.mpxj.sample import MpxjConvert
MpxjConvert().process('example.mpp', 'example.mpx')
jpype.shutdownJVM()
```

or you can interact directly with the object model exposed by MPXJ to extract data:

```python
import jpype
import mpxj

jpype.startJVM()
from org.mpxj.reader import UniversalProjectReader
project = UniversalProjectReader().read('example.mpp')

print("Tasks")
for task in project.getTasks():
	print(task.getID().toString() + "\t" + task.getName())

jpype.shutdownJVM()
```

or finally you can generate your own schedule:

```python
import jpype
import mpxj

jpype.startJVM()

from java.lang import Double
from java.time import LocalDate, LocalDateTime
from org.mpxj import ProjectFile, TaskField, Duration, TimeUnit, RelationType, Availability, Relation
from org.mpxj.common import LocalDateTimeHelper
from org.mpxj.writer import UniversalProjectWriter, FileFormat

# The helper class we use later to actually write the file
# uses an enumeration to determine the output format.
file_name = "test.xml"
file_format = FileFormat.MSPDI

# Create a ProjectFile instance
file = ProjectFile()

# Add a default calendar called "Standard"
calendar = file.addDefaultBaseCalendar()

# Add a holiday to the calendar to demonstrate calendar exceptions
calendar.addCalendarException(LocalDate.of(2006, 3, 13), LocalDate.of(2006, 3, 13))

# Retrieve the project properties and set the start date. Note Microsoft
# Project appears to reset all task dates relative to this date, so this
# date must match the start date of the earliest task for you to see
# the expected results. If this value is not set, it will default to
# today's date.
properties = file.getProjectProperties()
properties.setStartDate(LocalDateTime.of(2003, 1, 1, 8, 0))

# Set a couple more properties just for fun
properties.setProjectTitle("Created by MPXJ")
properties.setAuthor("Jon Iles")

# Let's create an alias for TEXT1
customFields = file.getCustomFields()
field = customFields.getOrCreate(TaskField.TEXT1)
field.setAlias("My Custom Field")

# Add resources
resource1 = file.addResource()
resource1.setName("Resource1")

resource2 = file.addResource()
resource2.setName("Resource2")
# This resource is only available 50% of the time
resource2.getAvailability().add(Availability(LocalDateTimeHelper.START_DATE_NA, LocalDateTimeHelper.END_DATE_NA, Double.valueOf(50.0)));


# Create a summary task
task1 = file.addTask()
task1.setName("Summary Task")

# Create the first sub task
task2 = task1.addTask()
task2.setName("First Sub Task")
task2.setDuration(Duration.getInstance(10.5, TimeUnit.DAYS))
task2.setStart(LocalDateTime.of(2003, 1, 1, 8, 0))
task2.setText(1, "My Custom Value 1")

# We'll set this task up as being 50% complete. If we have no resource
# assignments for this task, this is enough information for MS Project.
# If we do have resource assignments, the assignment record needs to
# contain the corresponding work and actual work fields set to the
# correct values in order for MS project to mark the task as complete
# or partially complete.
task2.setPercentageComplete(Double.valueOf(50.0))
task2.setActualStart(LocalDateTime.of(2003, 1, 1, 8, 0))

# Create the second sub task
task3 = task1.addTask()
task3.setName("Second Sub Task")
task3.setStart(LocalDateTime.of(2003, 1, 11, 8, 0))
task3.setDuration(Duration.getInstance(10, TimeUnit.DAYS))
task3.setText(1, "My Custom Value 2")


# Link these two tasks
task3.addPredecessor(Relation.Builder().targetTask(task2))

# Add a milestone
milestone1 = task1.addTask()
milestone1.setName("Milestone")
milestone1.setStart(LocalDateTime.of(2003, 1, 21, 8, 0))
milestone1.setDuration(Duration.getInstance(0, TimeUnit.DAYS))
milestone1.addPredecessor(Relation.Builder().targetTask(task3))

# This final task has a percent complete value, but no
# resource assignments. This is an interesting case it it requires
# special processing to generate the MSPDI file correctly.
task4 = file.addTask()
task4.setName("Next Task")
task4.setDuration(Duration.getInstance(8, TimeUnit.DAYS))
task4.setStart(LocalDateTime.of(2003, 1, 1, 8, 0))
task4.setPercentageComplete(Double.valueOf(70.0))
task4.setActualStart(LocalDateTime.of(2003, 1, 1, 8, 0))

# Assign resources to tasks
assignment1 = task2.addResourceAssignment(resource1)
assignment2 = task3.addResourceAssignment(resource2)

# As the first task is partially complete, and we are adding
# a resource assignment, we must set the work and actual work
# fields in the assignment to appropriate values, or MS Project
# won't recognise the task as being complete or partially complete
assignment1.setWork(Duration.getInstance(80, TimeUnit.HOURS))
assignment1.setActualWork(Duration.getInstance(40, TimeUnit.HOURS))

# If we were just generating an MPX file, we would already have enough
# attributes set to create the file correctly. If we want to generate
# an MSPDI file, we must also set the assignment start dates and
# the remaining work attribute. The assignment start dates will normally
# be the same as the task start dates.
assignment1.setRemainingWork(Duration.getInstance(40, TimeUnit.HOURS))
assignment2.setRemainingWork(Duration.getInstance(80, TimeUnit.HOURS))
assignment1.setStart(LocalDateTime.of(2003, 1, 1, 8, 0))
assignment2.setStart(LocalDateTime.of(2003, 1, 11, 8, 0))

# Write a 100% complete task
task5 = file.addTask()
task5.setName("Last Task")
task5.setDuration(Duration.getInstance(3, TimeUnit.DAYS))
task5.setStart(LocalDateTime.of(2003, 1, 1, 8, 0))
task5.setPercentageComplete(Double.valueOf(100.0))
task5.setActualStart(LocalDateTime.of(2003, 1, 1, 8, 0))

# Write a 100% complete milestone
task6 = file.addTask()
task6.setName("Last Milestone")
task6.setDuration(Duration.getInstance(0, TimeUnit.DAYS))
task6.setStart(LocalDateTime.of(2003, 1, 1, 8, 0))
task6.setPercentageComplete(Double.valueOf(100.0))
task6.setActualStart(LocalDateTime.of(2003, 1, 1, 8, 0))

# Write the file
writer = UniversalProjectWriter(file_format).write(file, file_name)

jpype.shutdownJVM()
```
