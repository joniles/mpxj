# How To: Use Timephased Data

## What is Timephased Data?

Timephased data is a term which refers to data distributed over time.
Take for example a simple 3 day task defined using Microsoft Project:

<p align="center"><img alt="A three day task" src="/images/howto-use-timephased/basic-task.png" width="50%"/></p>

This task covers 3 working days, each having 8 hours, so in total we have 24
hours of work. If we open the "Task Usage" view in Microsoft Project we can see
how Project has distributed the work over the three working days. For context,
the grey bar at the top are the values rolled up to the task from the task's
resource assignments shown as the rows with the white background below.

<p align="center"><img alt="Task showing timephased work by day" src="/images/howto-use-timephased/task-usage-default.png" width="50%"/></p>

We can also change the scale we are using to look at the data, so for example 
we can see an hour-by-hour view:

<p align="center"><img alt="Task showing timephased work by hour" src="/images/howto-use-timephased/task-usage-hours.png" width="90%"/></p>

In this view we are just looking at one working day, and you can see how the
work being performed during that day is scheduled for the working hours, 
with a gap for a the lunch break, as defined in the resource's calendar.

So far we have just looked at work being performed, but this view can also be used to 
examine cost and material consumption over time: In the example below, for the
same task we were looking at previously I have added a line showing how the
cost of the task (base on the hourly rate of  the resource) is distributed
over the task.

<p align="center"><img alt="Task showing timephased work and cost by day" src="/images/howto-use-timephased/task-usage-cost.png" width="60%"/></p>

So far so good, but the examples above all just show a simple flat distribution 
of work and cost over time. A timephased view of data comes into its own where
the data in question is not distributed evenly over time.

When planning a task, we may decide that most of the work, and therefore cost,
is accrued in the early stages of the task. We can reflect this either by
manually editing the hours work per day in Microsoft Project, or by asking
Microsoft Project to apply a "work contour".

In the example below I have chosen to manually edit the working hours: our task
is still being performed over 3 days, and we're still delivering 24 hours of
work, except that now half of the work is being done on the first day, and very
little is happening on the final day.

<p align="center"><img alt="Task showing manually edited timephased work and cost by day" src="/images/howto-use-timephased/task-usage-manual.png" width="60%"/></p>

This is where having access to timephased data is important: looking at the
Gantt chart alone doesn't allow us to readily understand when work is being
performed. This is also particularly important when we record actual values
for our task: actual work performed may well not be distributed evenly across
the duration of the task.

## Timescales

Before we can think about accessing timephased data using MPXJ, we need to look
at timescales. In order to retrieve timephased data, we need to specify a
timescale: an overall period of time, divided into ranges. When we generate
timephased data, our result will be a list of values, one for each of the
ranges described by our timescale.

MPXJ represents a timescale as a `List` or `LocalDateTimeRange` instances. Each
`LocalDateTimeRange` represents a "half open" range: this means that the range
starts at the provided stat time, runs up to, but not including, the end time.
An example will make this clearer. The code below creates a range
representing today's date:

=== "Java"
	```java
	LocalDateTimeRange today = new LocalDateTimeRange(
		LocalDateTime.of(2026, 2, 18, 0, 0),
		LocalDateTime.of(2026, 2, 19, 0, 0));
	```
=== "C#"
	```c#
	// TBC
	```

Today happens to be 18th February, but we can see that the range ends at
midnight on the 19th of February: the range covers the start of the day on the
18th (midnight: `00:00`) and up to _but not including_ the `00:00` on the 19th of
February.

If we want to combine several `LocalDateTimeRange` instances together to make a
timescale, our next range would start at midnight on the 19th of February, and
extend until midnight on the 20th February:

=== "Java"
	```java
	LocalDateTimeRange day1 = new LocalDateTimeRange(
		LocalDateTime.of(2026, 2, 18, 0, 0),
		LocalDateTime.of(2026, 2, 19, 0, 0));
	LocalDateTimeRange day2 = new LocalDateTimeRange(
		LocalDateTime.of(2026, 2, 19, 0, 0),
		LocalDateTime.of(2026, 2, 20, 0, 0));
	List<LocalDateTimeRange> timescale = Arrays.asList(day1, day2)
	```
=== "C#"
	```c#
	// TBC
	```

Fortunately, you don't need to worry about constructing a timescale for
yourself: MPXJ provides the `TimescaleUtility`. class to do this for you. To
generate a timescale to match the one shown in the samples from Microsoft
Project shown above, we can use the following code:

=== "Java"
	```java
	LocalDateTime startDate = LocalDateTime.of(2026, 2, 16, 0, 0, 0);
	List<LocalDateTimeRange> ranges = new TimescaleUtility()
		.createTimescale(startDate, 5, TimescaleUnits.DAYS);
	```
=== "C#"
	```c#
	// TBC
	```

This creates a timescale in days starting on the 16th February and running for 5
days. Alternatively you can provide start and end dates rather than counting
the number of ranges. This is useful when you won't know exactly how many
ranges will be generated to cover the period you are interested in.

=== "Java"
	```java
	LocalDateTime startDate = LocalDateTime.of(2026, 2, 16, 0, 0, 0);
	LocalDateTime endDate = LocalDateTime.of(2026, 2, 20, 0, 0, 0);
	List<LocalDateTimeRange> ranges = new TimescaleUtility()
		.createTimescale(startDate, endDate, TimescaleUnits.DAYS);
	```
=== "C#"
	```c#
	// TBC
	```

The `TimescaleUnits` enumeration provides a variety of options to allow you to
break down time periods into ranges from `MINUTES` all the way up yo `YEARS`.
The list of `LocalDateTimeRange` instances we have just created is what we'll
use in the next step to generate our timephased data.

## Timephased Data

Timephased data in MPXJ starts life in resource assignments, and can then be
rolled up through both the task hierarchy and also the resource hierarchy. This
gives you the ability to look at the "big picture" view of work, cost, and
material consumption then drill down into the detail at the individual resource
assignment level.

Three types of timephased data are available using MPXJ: Work, Cost, and Material.

### Work

The following timephased work values are available on assignments for Work
Resources and are expressed as `Duration` values. These values are not relevant
for Cost or Material resource assignments as these types of resources do not
represent work.

* **Actual Regular Work**: actual regular (non overtime) work for a resource assignment
* **Actual Overtime Work**: actual overtime work for a resource assignment
* **Actual Work**: the total of actual regular work plus actual overtime work for a resource assignment
* **Remaining Regular Work**: the remaining regular (non-overtime) work to complete a resource assignment
* **Remaining Overtime Work**: the remaining overtime work to complete a resource assignment
* **Remaining Work**: the total of remaining regular work plus remaining overtime work to complete a resource assignment
* **Work**: the total of actual work and remaining work for a resource assignment
* **Baseline Work**: work captured as a baseline for a resource assignment

We'll work through some examples here, using a sample MPP file containing a
variety of resource assignments. For ease of reference, here's a screenshot of
the sample project we'll be working with:

<p align="center"><img alt="Sample MPP file" src="/images/howto-use-timephased/timephased-sample-mpp.png" width="60%"/></p>

First we'll set up our timescale which will cover the time occupied by the
resource assignment we are working with. In this case we're asking MPXJ to 
show our timephased data split into days:

=== "Java"
	```java
	List<LocalDateTimeRange> ranges = new TimescaleUtility()
		.createTimescale(LocalDateTime.of(2026, 2, 18, 0, 0), 7, TimescaleUnits.DAYS);

	```
=== "C#"
	```c#
	// TBC
	```

#### Assignments
Now we can locate the resource assignment we are interested in (in this case
it's Resource 2 assigned to Task 2 in the screenshot above), and use the
`getTimephasedWork` method, which will give us the the total of the actual and
remaining work for this assignment:


=== "Java"
	```java
	ResourceAssignment assignment = file.getResourceAssignments().getByUniqueID(6);
	List<Duration> work = assignment.getTimephasedWork(ranges, TimeUnit.HOURS);
	```
=== "C#"
	```c#
	// TBC
	```

You can see that we're calling the method and passing `ranges` which represents
our timescale. We also passing `TimeUnit.HOURS` to tell MPXJ what units we'd
like the work to be returned as. Finally we can add a couple of methods to help
us format the data MPXJ has returned to make it easier to read:

=== "Java"
	```java
	private void writeTableHeader(List<LocalDateTimeRange> ranges)
	{
		String labels = ranges.stream()
			.map(r -> r.getStart().getDayOfWeek().name().substring(0, 1))
			.collect(Collectors.joining("|"));
		System.out.println("||" + labels + "|");
		
		String separator = ranges.stream()
			.map(r -> "---")
			.collect(Collectors.joining("|"));
		System.out.println("|---|" + separator+ "|");
	}

	private void writeTableRow(String label, List<?> data)
	{
		String values = data.stream()
			.map(String::valueOf)
			.collect(Collectors.joining("|"));
		System.out.println("|" + label + "|" + values + "|");
	}
	```
=== "C#"
	```c#
	// TBC
	```

We'll call our new helper methods like this:

=== "Java"
	```java
	writeTableHeader(ranges);
	writeTableRow(assignment.getResource().getName(), work);
	```
=== "C#"
	```c#
	// TBC
	```

Which will return a Markdown table as shown below, with the initial letter of
the day name as the header, and a label as the first cell in each row:

```
||W|T|F|S|S|M|T|
|---|---|---|---|---|---|---|---|
|Work|8.0h|8.0h|8.0h|null|null|8.0h|8.0h|
```

For the remainder of the documentation we'll render the Markdown tables we
produce from our samples to make them easier to read:


||W|T|F|S|S|M|T|
|---|---|---|---|---|---|---|---|
|Work|8.0h|8.0h|8.0h|null|null|8.0h|8.0h|


We can see that for this resource assignment, starting on Wednesday, we have 5
working days each with 8 hours of work per day. Note that the resource
assignment spans a weekend. On the weekend days MPXJ has returned a `null`
value. This indicates that this is non-working time, so no work is expected.
Typically if MPXJ returns a zero duration for a period, this indicates that the
period is normally working time, but that no work has been performed.

The example resource assignment we're using here is in progress, so rather than
retrieving timephased Work, which combines both Actual and Remaining Work, we
can request Actual and Remaining Work separately:


=== "Java"
	```java
	List<Duration> actualWork = assignment.getTimephasedActualWork(ranges, TimeUnit.HOURS);
	List<Duration> remainingWork = assignment.getTimephasedRemainingWork(ranges, TimeUnit.HOURS);
	writeTableHeader(ranges);
	writeTableRow("Actual Work", actualWork);
	writeTableRow("Remaining Work", remainingWork);
	```
=== "C#"
	```c#
	// TBC
	```


||W|T|F|S|S|M|T|
|---|---|---|---|---|---|---|---|
|Actual Work|8.0h|4.0h|null|null|null|null|null|
|Remaining Work|null|4.0h|8.0h|null|null|8.0h|8.0h|


What we can see here is that the ranges only overlap where there is both actual
and remaining work on one day. Once the actual work has been accounted for, the
remainder of the values returned by MPXJ will be `null`. Similarly the
Remaining Work timephased data will be `null` until we reach the first period
where there is Remaining Work.

#### Resources
As we mentioned before, the resource assignment we've been working with is for
Resource 2. We can request timephased data from the resource, which will roll
up values from all of this resource's assignments. In this case there is only
the one assignment for this resource, so the values we'll retrieve should match
those we've already seen from the resource assignment. The code sample below
illustrates calling the `getTimephasedWork` method on a `Resource`:


=== "Java"
	```java
	Resource resource2 = file.getResourceByID(2);
	List<Duration> work = resource2.getTimephasedWork(ranges, TimeUnit.HOURS);
	writeTableHeader(ranges);
	writeTableRow("Resource 2 Work", work);
	```
=== "C#"
	```c#
	// TBC
	```


Here's the timephased Work from Resource 2:

||W|T|F|S|S|M|T|
|---|---|---|---|---|---|---|---|
|Resource 2 Work|8.0h|8.0h|8.0h|null|null|8.0h|8.0h|


#### Tasks
Perhaps more interesting is what we see when we look at our tasks. In the sample
file (as per the screenshot above), Task 1 and Task 2 represent work carried
out by Resources 1 and 2. These tasks have been grouped together under
the "Work Resources" summary task. Let's write some code to show how timephased
Work is retrieved from tasks, and in doing so we'll see how the work from these
two child tasks is rolled up to the parent task.

=== "Java"
	```java
	Task summaryTask = file.getTaskByID(1);
	Task task1 = file.getTaskByID(2);
	Task task2 = file.getTaskByID(3);
	List<Duration> summaryWork = summaryTask.getTimephasedWork(ranges, TimeUnit.HOURS);
	List<Duration> task1Work = task1.getTimephasedWork(ranges, TimeUnit.HOURS);
	List<Duration> task2Work = task2.getTimephasedWork(ranges, TimeUnit.HOURS);
	writeTableHeader(ranges);
	writeTableRow("Summary Work", summaryWork);
	writeTableRow("Task 1 Work", task1Work);
	writeTableRow("Task 2 Work", task2Work);
	```
=== "C#"
	```c#
	// TBC
	```

Here's the output from our code:

||W|T|F|S|S|M|T|
|---|---|---|---|---|---|---|---|
|Summary Work|16.0h|16.0h|16.0h|null|null|8.0h|8.0h|
|Task 1 Work|8.0h|8.0h|8.0h|null|null|null|null|
|Task 2 Work|8.0h|8.0h|8.0h|null|null|8.0h|8.0h|


We can see from these results how the work for each child task is rolled up to
the summary task. You can also see how `null` values are handled: on the
weekend days where both tasks have `null` values for their work, the rolled up
work is also represented as `null`. Where there are a mixture of `null` and
non-`null` values, the `null` values are just treated as zero.

### Cost

The following timephased cost values are available on assignments for all
resource types and are expressed as `Number` values in Java (`double?` in .Net).

* **Actual Regular Cost**: actual regular (non overtime) cost for a resource assignment
* **Actual Overtime Cost**: actual overtime cost for a resource assignment
* **Actual Cost**: the total of actual regular cost plus actual overtime cost for a resource assignment
* **Remaining Regular Cost**: the remaining regular (non-overtime) cost to complete a resource assignment
* **Remaining Overtime Cost**: the remaining overtime cost to complete a resource assignment
* **Remaining Cost**: the total of remaining regular cost plus remaining overtime cost to complete a resource assignment
* **Cost**: the total of actual cost and remaining cost for a resource assignment
* **Baseline Cost**: cost captured as a baseline for a resource assignment

The methods called to retrieve timephased cost information from resource
assignments, resources and task are the same as those we've just seen in the
previous section where we were retrieving timephased work. The two main
differences are that these methods return `List<Number>` rather than
`List<Duration`, and they do not take a `TimeUnit` argument as this is not
relevant for cost.

The example below covers these points. Here we are looking at the same scenario
that we illustrated at the end of the timephased work section where we are
retrieving costs from three tasks and illustrating how these roll up from child
tasks to summary tasks:


=== "Java"
	```java
	Task summaryTask = file.getTaskByID(1);
	Task task1 = file.getTaskByID(2);
	Task task2 = file.getTaskByID(3);
	List<Number> summaryWork = summaryTask.getTimephasedCost(ranges);
	List<Number> task1Work = task1.getTimephasedCost(ranges);
	List<Number> task2Work = task2.getTimephasedCost(ranges);
	writeTableHeader(ranges);
	writeTableRow("Summary Cost", summaryWork);
	writeTableRow("Task 1 Cost", task1Work);
	writeTableRow("Task 2 Cost", task2Work);
	```
=== "C#"
	```c#
	// TBC
	```


The output from this code is shown below:

||W|T|F|S|S|M|T|
|---|---|---|---|---|---|---|---|
|Summary Cost|184.0|184.0|184.0|null|null|104.0|104.0|
|Task 1 Cost|80.0|80.0|80.0|null|null|null|null|
|Task 2 Cost|104.0|104.0|104.0|null|null|104.0|104.0|


You can see that, as expected, the values we are now retrieving are numeric
rather than `Duration` instances, but the same logic is still applied for
non-working periods where `null` is returned rather than zero.
