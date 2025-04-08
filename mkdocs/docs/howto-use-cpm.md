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
> perform resource leveling. They both assume that you have either already
> determined the duration of each task, or have added resource assignments
> to the tasks representing the required amount of work for that task.

The following sections provide some more detailed examples of using both
of these schedulers.

## MicrosoftScheduler

The `MicrosoftScheduler` determines that tasks are either "duration driven",
or "work driven", and will schedule them accordingly.

A Duration driven task will not have any labor (work) resources assigned to it,
and will already have its Duration, Actual Duration and Remaining duration
attributes set. If the task has been progressed then the Actual Start (and if
complete, Actual Finish) date will be set.

A work driven task will have one or more labor (work) resource assignments, each
with their Work, Actual Work and Remaining Work attributes set. If the task has
been progressed then the task's Actual Start (and if complete, Actual Finish)
attribute will be set.

> NOTE: The `MicrosoftScheduler` will not correctly schedule summary tasks which
> have constraints applied to them, or split tasks.

The following sections provide sample code illustrating how the
`MicrosoftScheduler` can be used to schedule a newly created project. All of
this sample code is available in the
[MPXJ Java Samples repository](https://github.com/joniles/mpxj-java-samples).

### Planned Project, Duration Driven Tasks

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

### Progressed Project, Duration Driven Tasks

Building on the sample code above, we'll now update some of the tasks
to indicate that they have been progressed, and have actual duration.
To update the tasks we'll use the method shown below to set the relevant attributes:

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

This method simplifies our sample code, sets the Actual Start, Actual
Duration and Remaining Duration attributes.

We can now progress the first two tasks in our sample, and schedule the resulting
project:

```java
progressTask(task1, LocalDateTime.of(2025, 4, 11, 8, 0), 25.0);
progressTask(task2, LocalDateTime.of(2025, 4, 11, 8, 0), 50.0);

new MicrosoftScheduler().schedule(file, LocalDateTime.of(2025, 4, 11, 8, 0));
```


### Planned Project, Work Driven Tasks

To illustrate how `MicrosoftScheduler` operates with work-driven tasks, we'll
take a look at some more sample code. Similar to our duration-driven example
above we'll use a method to make our sample code less repetitive:

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


### Progressed Project, Work Driven Tasks

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


## PrimaveraScheduler

In the previous section we saw how `MicrosoftScheduler` differentiated between
tasks based on whether they just had duration attributes defined, or whether
they had resource assignments with work. Rather than taking this approach,
Primavera P6 uses an explicit Activity Type attribute on each task to determine
how it behaves. This attribute is available through the use of getter and
setter methods on instances of the `Task` class.

The P6 equivalent of Microsoft's "duration driven" tasks are tasks with
the "Task Dependent" activity type, and the equivalent of Microsoft's "work
driven" tasks have the "Resource Dependent" activity type. In P6, Task Dependent
activities can have resource assignments with work, but these do not affect how
the activity is scheduled: only the duration of these activities is used.

P6 also requires that milestones have a specific Activity Type: either Start
Milestone or Finish Milestone. Finally there are also Level of Effort
activities (which represent a "sketch" of an amount of effort required between
two points in the schedule, determined by their predecessors and successors)
and WBS Summary activities, which are used to "roll up" details from all
activities beneath a parent WBS entry. All of these activities types are
supported and can be scheduled by the `PrimaveraScheduler` class.

For Task Dependent activities, the `PrimaveraScheduler` expects that the Duration,
Actual Duration and Remaining Duration attributes are provided. For Resource Dependent
activities, the activity must have labor (work) resource assignments with
their Work Actual Work, and Remaining Work attributes set. In both cases, where
the activity has been progressed,the activity should have the Actual Start, and
if applicable, the Actual Finish attribute populated.

The Data Date is also important to the `PrimaveraScheduler`. The Data Date is
known as the Status Date my MPXJ and can be found in the `ProjectProperties`.
The example below illustrates the Data Date being set for a project:

```java
file.getProjectProperties().setStatusDate(LocalDateTime.of(2025, 4, 11, 17, 0));
```

If your project does not have a value set for the Status Date attribute, the
`PrimaveraScheduler` will assume that the Data Date is the same as the start
date for the project your are scheduling.


### Planned Project, Task Dependent Activities

### Progressed Project, Task Dependent Activities

### Planned Project, Resource Dependent Activities

### Progressed Project, Resource Dependent Activities

TODO: updating an existing file example for both schedulers

