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
yourself: MPXJ provides a class to do this for you.

## Timephased Data in MPXJ

Timephased data in MPXJ starts life in resource assignments, and can then be
rolled up through both the task hierarchy and also the resource hierarchy. This
gives you the ability to look at the "big picture" view of work, cost, and
material consumption then drill down into the detail.

=== "Java"
	```java
	package org.mpxj.howto.use.universal;
	
	import org.mpxj.ProjectFile;
	import org.mpxj.reader.ProjectReader;
	import org.mpxj.reader.UniversalProjectReader;
	
	public class SimpleExample
	{
		public void process() throws Exception
		{
			ProjectReader reader = new UniversalProjectReader();
			ProjectFile project = reader.read("example.mpp");
		}
	}
	```
=== "C#"
	```c#
	namespace MpxjSamples.HowToUse.Universal;

	using MPXJ.Net;
		 
	public class SimpleExample
	{
		 public void Process()
		 {
			  var reader = new UniversalProjectReader();
			  var project = reader.Read("example.mpp");
		 }
	}
	```

