# How To: Use the CPM Schedulers

MPXJ provides two Critical Path Method (CPM) scheduler implementations:
`MicrosoftScheduler` and `PrimaveraScheduler`. You can use these to schedule
a new project you have created or an existing project you have updated.
These schedulers will use the Critical Path Method algorithm to calculate
the Early Start, Early Finish, Late Start and Late Finish dates for all of the
tasks in the schedule, and from those it will set the Start and Finish dates for
each task.

The two CPM implementations aim to reproduce the results obtained when
scheduling a project in either Microsoft Project or Primavera P6. Although both
of these applications implement CPM, they both take slightly different
approaches, and so the results obtained from each scheduler will be different.

> These schedulers have been implemented by observing and attempting to
> replicate the behavior of Microsoft Project and Primavera P6. In most cases
> they will produce results which match the original applications, but this is
> not guaranteed. If you do come across differences, bug reports with sample
> data are welcome - particularly if you can explain why the original
> application is scheduling the project the way it is!
 
Both schedulers implement the `Scheduler` interface, which exposes a single
`schedule` method. This method takes a `ProjectFile` instance, which is the
project to be scheduled, and a start date, which is the date from which the
project should be scheduled.

> NOTE: neither the `MicrosoftScheduler` or the `PrimaveraScheduler`
> perform resource leveling. Also, they both assume that you have either already
> determined the duration of each task, or have added resource assignments
> to the tasks representing the required amount of work for that task.

The following sections provide some more detailed examples of using both
of these schedulers with new and existing projects.

## New Project With MicrosoftScheduler

The `MicrosoftScheduler` is intended to schedule projects in a way that is
closely aligned to how Microsoft Project would schedule the same project.

> NOTE: the `MicrosoftScheduler` only supports scheduling a project from the
> start date. Scheduling projects from the finish date is not supported.

The `MicrosoftScheduler` determines that tasks are either "task dependent",
or "resource dependent", and will schedule them accordingly.
A task dependent task will not have any labor (work) resources assigned to it,
and will already have its Duration, Actual Duration and Remaining duration
attributes set. If the task has been progressed then the Actual Start (and 
Actual Finish if applicable) will be set.

A resource dependent task will have one or more labor (work) resource
assignments, each with their Work, Actual Work and Remaining Work attributes
set. If the task has been progressed then the Actual Start (and Actual Finish
if applicable) will be set.

> NOTE: The `MicrosoftScheduler` will not correctly schedule split tasks,
> recurring tasks, or summary tasks which have constraints applied to them.

The following sections provide sample code illustrating how the
`MicrosoftScheduler` can be used to schedule a newly created project. All of
this sample code is available in the
[MPXJ Java Samples repository](https://github.com/joniles/mpxj-java-samples).

### Task Dependent Tasks
#### Planned Project

To simplify the code in the following examples, we'll be using the
method shown below to create new tasks. This avoids repeating code unnecessarily
for each task we create.

```java
private Task createTask(ChildTaskContainer parent, String name, Duration duration)
{
  Task task = parent.addTask();
  task.setName(name);
  task.setDuration(duration);
  task.setActualDuration(Duration.getInstance(0, duration.getUnits()));
  task.setRemainingDuration(duration);
  return task;
}
```

The method shown above takes as arguments the parent of the new task (either the
project file or a summary task) the name of the new task, and it's duration.
The method then manages populating the Duration, Actual Duration and Remaining
Duration attributes.


```java
ProjectFile file = new ProjectFile();

ProjectCalendar calendar = file.addDefaultBaseCalendar();
file.setDefaultCalendar(calendar);

Task summary1 = file.addTask();
summary1.setName("Summary 1");

Task task1 = createTask(summary1, "Task 1", Duration.getInstance(4, TimeUnit.DAYS));
Task task2 = createTask(summary1, "Task 2", Duration.getInstance(2, TimeUnit.DAYS));
Task task3 = createTask(summary1, "Task 3", Duration.getInstance(5, TimeUnit.DAYS));

task3.addPredecessor(new Relation.Builder().predecessorTask(task1));
task3.addPredecessor(new Relation.Builder().predecessorTask(task2));

Task summary2 = file.addTask();
summary2.setName("Summary 2");

Task task4 = createTask(summary2, "Task 4", Duration.getInstance(2, TimeUnit.DAYS));
Task task5 = createTask(summary2, "Task 5", Duration.getInstance(2, TimeUnit.DAYS));
Task task6 = createTask(summary2, "Task 6", Duration.getInstance(2, TimeUnit.DAYS));

task6.addPredecessor(new Relation.Builder().predecessorTask(task4));
task6.addPredecessor(new Relation.Builder().predecessorTask(task5)
	.lag(Duration.getInstance(1, TimeUnit.DAYS)));

Task milestone1 = createTask(file, "Milestone 1", Duration.getInstance(0, TimeUnit.DAYS));

milestone1.addPredecessor(new Relation.Builder().predecessorTask(task3));
milestone1.addPredecessor(new Relation.Builder().predecessorTask(task6));
```

We'll be working with the sample code above, which creates a new project, adds a
default calendar, creates six tasks with two summary tasks and one milestone.
The tasks are linked together by some simple predecessor-successor
relationships, one of which has some lag defined.

To schedule the file, we just need to invoke the scheduler as illustrated below:

```java
new MicrosoftScheduler().schedule(file, LocalDateTime.of(2025, 4, 11, 8, 0));
```

We're passing in the `ProjectFile` instance we've just created, along
with the date from which we want to schedule the project. At this point the
Start, Finish, Early Start, Early Finish, Late Start, and Late Finish
attributes will be populated. 

We could inspect the results of using the scheduler by calling a `printTasks`
method similar to the one implemented below:

```java
private void printTasks(ProjectFile file)
{
  System.out.println(writeTaskHeaders());
  file.getTasks().forEach(t -> System.out.println(writeTaskData(t)));
}

private String writeTaskHeaders()
{
  return TASK_COLUMNS.stream().map(TaskField::toString)
    .collect(Collectors.joining("\t"));
}

private String writeTaskData(Task task)
{
  return TASK_COLUMNS.stream().map(c -> writeValue(task.get(c)))
    .collect(Collectors.joining("\t"));
}

private String writeValue(Object value)
{
  return value instanceof LocalDateTime ? 
    DATE_TIME_FORMAT.format((LocalDateTime)value) : 
    String.valueOf(value);
}

private static final List<TaskField> TASK_COLUMNS = Arrays.asList(
  TaskField.ID,
  TaskField.UNIQUE_ID,
  TaskField.NAME,
  TaskField.DURATION,
  TaskField.START,
  TaskField.FINISH,
  TaskField.EARLY_START,
  TaskField.EARLY_FINISH,
  TaskField.LATE_START,
  TaskField.LATE_FINISH,
  TaskField.TOTAL_SLACK,
  TaskField.CRITICAL);

private static final DateTimeFormatter DATE_TIME_FORMAT = 
  DateTimeFormatter.ofPattern("dd/MM/yy HH:mm");
```

We can also then save an MSPDI file and open it in Microsoft Project to confirm
that the project has been scheduled as we expect:

```java
new UniversalProjectWriter(FileFormat.MSPDI).write(file, "scheduled.xml");
```

#### Progressed Project

Building on the sample code above, we'll now update some of the tasks
to indicate that they have been progressed, and have actual durations.
To update the tasks we'll use the method shown below to set the Actual Start,
Actual Duration and Remaining Duration attributes.

```java
private void progressTask(Task task, LocalDateTime actualStart, double percentComplete)
{
  double durationValue = task.getDuration().getDuration();
  TimeUnit durationUnits = task.getDuration().getUnits();
  task.setActualStart(actualStart);
  task.setPercentageComplete(percentComplete);
  task.setActualDuration(Duration.getInstance((percentComplete * durationValue) / 100.0, durationUnits));
  task.setRemainingDuration(Duration.getInstance(((100.0 - percentComplete) * durationValue) / 100.0, durationUnits));
}
```


We can now progress the first two tasks in our sample, and schedule the resulting
project:

```java
progressTask(task1, LocalDateTime.of(2025, 4, 11, 8, 0), 25.0);
progressTask(task2, LocalDateTime.of(2025, 4, 11, 8, 0), 50.0);

new MicrosoftScheduler().schedule(file, LocalDateTime.of(2025, 4, 11, 8, 0));
```


### Resource Dependent Tasks
#### Planned Project

To illustrate how `MicrosoftScheduler` operates with resource dependent tasks,
we'll take a look at some more sample code. Similar to our task dependent
example above we'll use a method to make our sample code less repetitive:

```java
private ResourceAssignment createResourceAssignment(Task task, Resource resource, Duration work)
{
  ResourceAssignment assignment = task.addResourceAssignment(resource);
  assignment.setWork(work);
  assignment.setActualWork(Duration.getInstance(0, work.getUnits()));
  assignment.setRemainingWork(work);
  return assignment;
}
```

This method adds a resource assignment to a task and sets the attributes
required by `MicrosoftScheduler`: Work, Actual Work and Remaining Work. Our new
sample code creates a project with the same structure as the sample we looked
at previously. The main difference is that we'll create two resources, and use
the `createResourceAssignment` method to add work using these resources to two
tasks.

```java
ProjectFile file = new ProjectFile();

ProjectCalendar calendar = file.addDefaultBaseCalendar();
file.setDefaultCalendar(calendar);

Resource resource1 = file.addResource();
resource1.setName("Resource 1");
ProjectCalendar calendar1 = file.addDefaultDerivedCalendar();
resource1.setCalendar(calendar1);
calendar1.setParent(calendar);
calendar1.setName("Resource 1");
calendar1.addCalendarException(LocalDate.of(2025, 4, 14));

Resource resource2 = file.addResource();
resource2.setName("Resource 2");
ProjectCalendar calendar2 = file.addDefaultDerivedCalendar();
resource2.setCalendar(calendar2);
calendar2.setParent(calendar);
calendar2.setName("Resource 2");

Task summary1 = file.addTask();
summary1.setName("Summary 1");

Task task1 = summary1.addTask();
task1.setName("Task 1");
createResourceAssignment(task1, resource1, Duration.getInstance(32, TimeUnit.HOURS));

Task task2 = summary1.addTask();
task2.setName("Task 2");
createResourceAssignment(task2, resource2, Duration.getInstance(16, TimeUnit.HOURS));

Task task3 = createTask(summary1, "Task 3", Duration.getInstance(5, TimeUnit.DAYS));

task3.addPredecessor(new Relation.Builder().predecessorTask(task1));
task3.addPredecessor(new Relation.Builder().predecessorTask(task2));

Task summary2 = file.addTask();
summary2.setName("Summary 2");

Task task4 = createTask(summary2, "Task 4", Duration.getInstance(2, TimeUnit.DAYS));
Task task5 = createTask(summary2, "Task 5", Duration.getInstance(2, TimeUnit.DAYS));
Task task6 = createTask(summary2, "Task 6", Duration.getInstance(2, TimeUnit.DAYS));

task6.addPredecessor(new Relation.Builder().predecessorTask(task4));
task6.addPredecessor(new Relation.Builder().predecessorTask(task5).lag(Duration.getInstance(1, TimeUnit.DAYS)));

Task milestone1 = createTask(file, "Milestone 1", Duration.getInstance(0, TimeUnit.DAYS));

milestone1.addPredecessor(new Relation.Builder().predecessorTask(task3));
milestone1.addPredecessor(new Relation.Builder().predecessorTask(task6));

new MicrosoftScheduler().schedule(file, LocalDateTime.of(2025, 4, 11, 8, 0));
```

As we did in our previous sample code, once we've created the project we
schedule it using `MicrosoftScheduler`.


#### Progressed Project

Our final example illustrates how we'd update resource assignments to record
actual work on our project. To do this we'll use another simple method to avoid
repeating the same code when we update several resource assignments:

```java
private void progressAssignment(ResourceAssignment assignment, double percentComplete)
{
  double workValue = assignment.getWork().getDuration();
  TimeUnit workUnits = assignment.getWork().getUnits();
  assignment.setPercentageWorkComplete(percentComplete);
  assignment.setActualWork(Duration.getInstance((percentComplete * workValue) / 100.0, workUnits));
  assignment.setRemainingWork(Duration.getInstance(((100.0 - percentComplete) * workValue) / 100.0, workUnits));
}
```

The main purpose of this method is to update the Actual Work and Remaining Work
attributes of a resource assignment, given a percent complete value.

Using our previous sample code, we'll add the following lines to add Actual Start dates
to the tasks we're updating, then we'll adjust the resource assignments to
have actual work:

```java
task1.setActualStart(LocalDateTime.of(2025, 4, 11, 8, 0));
progressAssignment(assignment1, 25.0);

task2.setActualStart(LocalDateTime.of(2025, 4, 11, 8, 0));
progressAssignment(assignment2, 50.0);

new MicrosoftScheduler().schedule(file, LocalDateTime.of(2025, 4, 11, 8, 0));
```

Once we've updated the resource assignments, we call the scheduler to update
the schedule to include the actual work.


## New Project With PrimaveraScheduler

The `PrimaveraScheduler` is intended to schedule projects in a way that is
closely aligned to how Primavera P6 would schedule the same project.

> NOTE: the `PrimaveraScheduler` currently only supports P6's "default"
> scheduling options (the options configured when the
> Tools->Schedule->Options->Default menu item is selected).

In the previous section we saw how `MicrosoftScheduler` differentiated between
tasks based on whether they just had duration attributes defined, or whether
they had resource assignments with work. Rather than taking this approach,
Primavera P6 uses an explicit Activity Type attribute on each task to determine
how it behaves (the value of this attribute in these cases will be either Task
Dependent or Resource Dependent). This attribute is available through the use
of getter and setter methods on instances of the `Task` class.

> Note that Task Dependent activities can have resource assignments with work,
> but these do not affect how the activity is scheduled: only the duration of
> these activities is used.

P6 requires that milestones have a specific Activity Type: either Start
Milestone or Finish Milestone.

There are also Level of Effort activities (which represent an "outline" of an
amount of effort required between two points in the schedule, determined by the
activity's predecessors and successors) and WBS Summary activities, which are
used to "roll up" details from all activities in the hierarchy beneath a parent
WBS entry. All of these activities types are supported and can be scheduled by
the `PrimaveraScheduler` class.

For Task Dependent activities, the `PrimaveraScheduler` expects that the
Duration, Actual Duration and Remaining Duration attributes are provided. For
Resource Dependent activities, the activity must have labor (work) resource
assignments with their Work, Actual Work, and Remaining Work attributes set. In
both cases, where the activity has been progressed, the activity should have the
Actual Start, and if applicable, the Actual Finish attribute populated.

The Data Date is also important to the `PrimaveraScheduler`. The Data Date is
known as the Status Date my MPXJ and can be found in the `ProjectProperties`.
The example below illustrates the Data Date being set for a project:

```java
file.getProjectProperties().setStatusDate(LocalDateTime.of(2025, 4, 11, 17, 0));
```

If your project does not have a value set for the Status Date attribute, the
`PrimaveraScheduler` will assume that the Data Date is the same as the start
date for the project your are scheduling.

The following sections provide sample code illustrating how the
`PrimaveraScheduler` can be used to schedule a newly created project. All of
this sample code is available in the
[MPXJ Java Samples repository](https://github.com/joniles/mpxj-java-samples).

### Task Dependent Activities
#### Planned Project

To simplify the code in the following examples, we'll be using the method shown
below to create new activities. This avoids repeating code unnecessarily for
each activity we create.

```java
private Task createActivity(ChildTaskContainer parent, ActivityType type,
  String name, Duration duration)
{
  Task task = parent.addTask();
  task.setActivityType(type);
  task.setName(name);
  task.setDuration(duration);
  task.setActualDuration(Duration.getInstance(0, duration.getUnits()));
  task.setRemainingDuration(duration);
  return task;
}
```

The method shown above takes as arguments the parent of the new activity
(either the project file or a WBS entry), the activity type, the name of the
new activity, and its duration. The method then manages populating the
Duration, Actual Duration and Remaining Duration attributes.

```java
ProjectFile file = new ProjectFile();

ProjectCalendar calendar = file.addDefaultBaseCalendar();
file.setDefaultCalendar(calendar);

Task summary1 = file.addTask();
summary1.setName("WBS 1");

Task task1 = createActivity(summary1, ActivityType.TASK_DEPENDENT, "Activity 1", Duration.getInstance(4, TimeUnit.DAYS));
Task task2 = createActivity(summary1, ActivityType.TASK_DEPENDENT,"Activity 2", Duration.getInstance(2, TimeUnit.DAYS));
Task task3 = createActivity(summary1, ActivityType.TASK_DEPENDENT,"Activity 3", Duration.getInstance(5, TimeUnit.DAYS));

task3.addPredecessor(new Relation.Builder().predecessorTask(task1));
task3.addPredecessor(new Relation.Builder().predecessorTask(task2));

Task summary2 = file.addTask();
summary2.setName("WBS 2");

Task task4 = createActivity(summary2, ActivityType.TASK_DEPENDENT, "Activity 4", Duration.getInstance(2, TimeUnit.DAYS));
Task task5 = createActivity(summary2, ActivityType.TASK_DEPENDENT,"Activity 5", Duration.getInstance(2, TimeUnit.DAYS));
Task task6 = createActivity(summary2, ActivityType.TASK_DEPENDENT,"Activity 6", Duration.getInstance(2, TimeUnit.DAYS));

task6.addPredecessor(new Relation.Builder().predecessorTask(task4));
task6.addPredecessor(new Relation.Builder().predecessorTask(task5).lag(Duration.getInstance(1, TimeUnit.DAYS)));

Task milestone1 = createActivity(file, ActivityType.FINISH_MILESTONE,"Milestone 1", Duration.getInstance(0, TimeUnit.DAYS));

milestone1.addPredecessor(new Relation.Builder().predecessorTask(task3));
milestone1.addPredecessor(new Relation.Builder().predecessorTask(task6));
```

We'll be working with the sample code above, which creates a new project, adds a
default calendar, creates six activities with two WBS entries and one milestone.
The activities are linked together by some simple predecessor-successor
relationships, one of which has some lag defined.

To schedule the file, we just need to invoke the scheduler as illustrated below:

```java
new PrimaveraScheduler().schedule(file, LocalDateTime.of(2025, 4, 11, 8, 0));
```

We're passing in the `ProjectFile` instance we've just created, along
with the date from which we want to schedule the project. At this point the
Start, Finish, Early Start, Early Finish, Late Start, and Late Finish
activity and WBS attributes will be populated. 

We could inspect the results of using the scheduler by calling the `printTasks`
method we saw in a previous section, and we could also save a PMXML file and
open it in Primavera P6 to confirm that the project has been scheduled as we
expect:

```java
new UniversalProjectWriter(FileFormat.PMXML).write(file, "scheduled.xml");
```

> NOTE: when you import the resulting project into P6, you will have to schedule
> it first using P6's default scheduling options in order to populate the
> Early Start, Early Finish, Late Start, and Late Finish dates.

#### Progressed Project

Building on the sample code above, we'll now update some of the activities
to indicate that they have been progressed, and have actual duration.
To update the activities we'll use the method shown below to set the Actual Start
Actual Duration and Remaining Duration attributes:

```java
private void progressActivity(Task task, LocalDateTime actualStart, double percentComplete)
{
  double durationValue = task.getDuration().getDuration();
  TimeUnit durationUnits = task.getDuration().getUnits();
  task.setActualStart(actualStart);
  task.setPercentageComplete(percentComplete);
  task.setActualDuration(Duration.getInstance((percentComplete * durationValue) / 100.0, durationUnits));
  task.setRemainingDuration(Duration.getInstance(((100.0 - percentComplete) * durationValue) / 100.0, durationUnits));
}
```

We can now progress the first two activities in our sample, and schedule the resulting
project:

```java
progressActivity(task1, LocalDateTime.of(2025, 4, 11, 8, 0), 25.0);
progressActivity(task2, LocalDateTime.of(2025, 4, 11, 8, 0), 50.0);
file.getProjectProperties().setStatusDate(LocalDateTime.of(2025, 4, 11, 17, 0));
new PrimaveraScheduler().schedule(file, LocalDateTime.of(2025, 4, 11, 8, 0));
```

Note that in the example above we've set the Data Date to be consistent with the
progress we've applied to the activities.

### Resource Dependent Activities
#### Planned Project

To illustrate how `PrimaveraScheduler` operates with resource dependent tasks,
we'll take a look at some more sample code. Similar to our task dependent
example above we'll use a method to make our sample code less repetitive:

```java
private ResourceAssignment createResourceAssignment(Task activity, Resource resource, Duration work)
{
  ResourceAssignment assignment = activity.addResourceAssignment(resource);
  assignment.setWork(work);
  assignment.setActualWork(Duration.getInstance(0, work.getUnits()));
  assignment.setRemainingWork(work);
  return assignment;
}
```

This method adds a resource assignment to an activity and sets the attributes
required by `PrimaveraScheduler`: Work, Actual Work and Remaining Work. Our new
sample code creates a project with the same structure as the sample we looked
at previously. The main difference is that we'll create two resources, and use
the `createResourceAssignment` method to add work using these resources to two
tasks.

```java
ProjectFile file = new ProjectFile();

ProjectCalendar calendar = file.addDefaultBaseCalendar();
file.setDefaultCalendar(calendar);

Resource resource1 = file.addResource();
resource1.setName("Resource 1");
ProjectCalendar calendar1 = file.addDefaultDerivedCalendar();
resource1.setCalendar(calendar1);
calendar1.setParent(calendar);
calendar1.setName("Resource 1");
calendar1.addCalendarException(LocalDate.of(2025, 4, 14));

Resource resource2 = file.addResource();
resource2.setName("Resource 2");
ProjectCalendar calendar2 = file.addDefaultDerivedCalendar();
resource2.setCalendar(calendar2);
calendar2.setParent(calendar);
calendar2.setName("Resource 2");

Task summary1 = file.addTask();
summary1.setName("WBS 1");

Task task1 = summary1.addTask();
task1.setActivityType(ActivityType.RESOURCE_DEPENDENT);
task1.setName("Activity 1");
createResourceAssignment(task1, resource1, Duration.getInstance(32, TimeUnit.HOURS));

Task task2 = summary1.addTask();
task2.setActivityType(ActivityType.RESOURCE_DEPENDENT);
task2.setName("Activity 2");
createResourceAssignment(task2, resource2, Duration.getInstance(16, TimeUnit.HOURS));

Task task3 = createActivity(summary1, ActivityType.TASK_DEPENDENT,"Activity 3", Duration.getInstance(5, TimeUnit.DAYS));

task3.addPredecessor(new Relation.Builder().predecessorTask(task1));
task3.addPredecessor(new Relation.Builder().predecessorTask(task2));

Task summary2 = file.addTask();
summary2.setName("WBS 2");

Task task4 = createActivity(summary2, ActivityType.TASK_DEPENDENT,"Activity 4", Duration.getInstance(2, TimeUnit.DAYS));
Task task5 = createActivity(summary2, ActivityType.TASK_DEPENDENT,"Activity 5", Duration.getInstance(2, TimeUnit.DAYS));
Task task6 = createActivity(summary2, ActivityType.TASK_DEPENDENT,"Activity 6", Duration.getInstance(2, TimeUnit.DAYS));

task6.addPredecessor(new Relation.Builder().predecessorTask(task4));
task6.addPredecessor(new Relation.Builder().predecessorTask(task5).lag(Duration.getInstance(1, TimeUnit.DAYS)));

Task milestone1 = createActivity(file, ActivityType.FINISH_MILESTONE,"Milestone 1", Duration.getInstance(0, TimeUnit.DAYS));

milestone1.addPredecessor(new Relation.Builder().predecessorTask(task3));
milestone1.addPredecessor(new Relation.Builder().predecessorTask(task6));

new PrimaveraScheduler().schedule(file, LocalDateTime.of(2025, 4, 11, 8, 0));
```

As we did in our previous sample code, once we've created the project we
schedule it using `PrimaveraScheduler`.

#### Progressed Project

Our final example illustrates how we'd update resource assignments to record
actual work on our project. To do this we'll use another simple method to avoid
repeating the same code when we update several resource assignments:

```java
private void progressAssignment(ResourceAssignment assignment, double percentComplete)
{
  double workValue = assignment.getWork().getDuration();
  TimeUnit workUnits = assignment.getWork().getUnits();
  assignment.setPercentageWorkComplete(percentComplete);
  assignment.setActualWork(Duration.getInstance((percentComplete * workValue) / 100.0, workUnits));
  assignment.setRemainingWork(Duration.getInstance(((100.0 - percentComplete) * workValue) / 100.0, workUnits));
}
```

The main purpose of this method is to update the Actual Work and Remaining Work
attributes of a resource assignment, given a percent complete value.

Using our previous sample code, we'll add the following lines to add Actual
Start dates to the tasks we're updating, adjust the resource
assignments to have actual work, and set the Data Date:

```java
task1.setActualStart(LocalDateTime.of(2025, 4, 11, 8, 0));
progressAssignment(assignment1, 25.0);

task2.setActualStart(LocalDateTime.of(2025, 4, 11, 8, 0));
progressAssignment(assignment2, 50.0);

file.getProjectProperties().setStatusDate(LocalDateTime.of(2025, 4, 11, 17, 0));

new PrimaveraScheduler().schedule(file, LocalDateTime.of(2025, 4, 11, 8, 0));
```

Once we've updated the resource assignments, we call the scheduler to update
the schedule to include the actual work.


## Update An Existing Project

One final illustration is how MPXJ's schedulers can be used to update an
existing project. In this example we have loaded an existing schedule from an
MPP file, and have updated the duration of the second task from 3 days to 5
days.

Note that we are passing to the scheduler the start date of the project, as read
from the project properties. This is to ensure that we replicate the date from
which the project was originally scheduled.

```java
Task task = file.getTaskByID(2);
task.setDuration(Duration.getInstance(5, TimeUnit.DAYS));

new MicrosoftScheduler().schedule(file, file.getProjectProperties().getStartDate());

new UniversalProjectWriter(FileFormat.MSPDI).write(file, "updated.xml");
```

When the project is scheduled, the scheduler will ensure that the finish date of
the updated task is adjusted accordingly, along with the start and finish dates
of any subsequent successor tasks. Although this sample is based on a Microsoft
Project file, exactly the same approach could be used with a P6 project using
the `PrimaveraScheduler`.
