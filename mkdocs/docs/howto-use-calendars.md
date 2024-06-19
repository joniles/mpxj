# How To: Use Calendars
Calendars are the foundation on which schedules are built. They determine when
work can be carried out, and when work is not possible. Given some tasks we
need to plan, and knowing how much work each task will require, a calendar can
be used to decide when work on each task could start and how much elapsed time
will be required to complete the tasks.

## Calendars in MPXJ
Let's see how calendars work in MPXJ. First let's try creating one. As it
happens, the `ProjectFile` class provides a convenience method
`addDefaultBaseCalendar` to create a default calendar. The calendar it creates
is modelled on the `Standard` calendar you'd see in Microsoft Project if you
created a new project. This default calendar defines Monday to Friday as
working days, with 8 working hours each day (8am to noon, then 1pm to 5pm).

```java
ProjectFile file = new ProjectFile();
ProjectCalendar calendar = file.addDefaultBaseCalendar();
System.out.println("The calendar name is " + calendar.getName());
```

As you can see from the code above, the calendar also has a name which we can
set to distinguish between different calendars.

## Working Days
Let's see what the calendar can tell us. First we'll use the `DayOfWeek`
enumeration to retrieve the working/non-working state for each day.

```java
for (DayOfWeek day : DayOfWeek.values()) {
   String dayType = calendar.getCalendarDayType(day).toString();
   System.out.println(day + " is a " + dayType + " day");
}
```

Running the code shown above will produce output like this:

```
MONDAY is a WORKING day
TUESDAY is a WORKING day
WEDNESDAY is a WORKING day
THURSDAY is a WORKING day
FRIDAY is a WORKING day
SATURDAY is a NON_WORKING day
SUNDAY is a NON_WORKING day
```

We can use the `setWorkingDay` method to change our pattern of working day.
Let's make Saturday a working day for our team, and make Monday a non-working
day to compensate.

```java
calendar.setWorkingDay(DayOfWeek.SATURDAY, true);
calendar.setWorkingDay(DayOfWeek.MONDAY, false);
```

Now if we use the loop we saw previously to inspect the week days, we'll see
this output:

```
MONDAY is a NON_WORKING day
TUESDAY is a WORKING day
WEDNESDAY is a WORKING day
THURSDAY is a WORKING day
FRIDAY is a WORKING day
SATURDAY is a WORKING day
SUNDAY is a NON_WORKING day
```

## Working Hours
So far, all we have done is set a flag which tells us whether a day is working
or non-working. How do we know the working times on those days? We can use the
`getCalendarHours` method to find that information.


The `getCalendarHours` method returns a `List` of `LocalTimeRange` instances.
`LocalTimeRange` is a simple immutable class which represents a span of time
between a start time and an end time as an inclusive range. Let's try printing
these `LocalTimeRange` instances to our output to see what we get:

```java
List<LocalTimeRange> hours = calendar.getCalendarHours(DayOfWeek.TUESDAY);
hours.forEach(System.out::println);
```

Here's the output:

```
[LocalTimeRange start=08:00 end=12:00]
[LocalTimeRange start=13:00 end=17:00]
```

Let's add a method to format the hours of a day a little more concisely for
display:

```java
private String formatLocalTimeRanges(List<LocalTimeRange> hours) {
   return hours.stream()
      .map(h -> h.getStart() + "-" + h.getEnd())
      .collect(Collectors.joining(", "));
}
```

So now our output looks like this:

```
08:00-12:00, 13:00-17:00
```

Let's use this method to take a look at the whole week again:

```java
for (DayOfWeek day : DayOfWeek.values()) {
   String dayType = calendar.getCalendarDayType(day).toString();
   System.out.println(day
      + " is a " + dayType + " day ("
      + formatLocalTimeRanges(calendar.getCalendarHours(day)) + ")");
}
```

Here's the output:

```
MONDAY is a NON_WORKING day ()
TUESDAY is a WORKING day (08:00-12:00, 13:00-17:00)
WEDNESDAY is a WORKING day (08:00-12:00, 13:00-17:00)
THURSDAY is a WORKING day (08:00-12:00, 13:00-17:00)
FRIDAY is a WORKING day (08:00-12:00, 13:00-17:00)
SATURDAY is a WORKING day ()
SUNDAY is a NON_WORKING day ()
```

The one thing we're missing now is that although we have set Saturday to be a
working day, it doesn't have any working hours. MPXJ has some constants which
can be used to help us add some working hours:

```java
hours = calendar.getCalendarHours(DayOfWeek.SATURDAY);
hours.add(ProjectCalendarDays.DEFAULT_WORKING_MORNING);
hours.add(ProjectCalendarDays.DEFAULT_WORKING_AFTERNOON);
```

Now when we examine our week this is what we see:

```
MONDAY is a NON_WORKING day ()
TUESDAY is a WORKING day (08:00-12:00, 13:00-17:00)
WEDNESDAY is a WORKING day (08:00-12:00, 13:00-17:00)
THURSDAY is a WORKING day (08:00-12:00, 13:00-17:00)
FRIDAY is a WORKING day (08:00-12:00, 13:00-17:00)
SATURDAY is a WORKING day (08:00-12:00, 13:00-17:00)
SUNDAY is a NON_WORKING day ()
```

> The version of MPXJ at the time of writing (12.0.0) has a limitation
> that if `setCalendarDayType` is used to make a day into a working day, we don't
> automatically add working hours for it. This behaviour is likely to
> change with the next major version of MPXJ.

What if we want to supply some working hours different from the defaults we've
used so far? To set our own working hours we just need to create as many
`LocalTimeRange` instances as we need using a pair of `LocalTime` instances for
each one to represent the start and end times.

```java
LocalTime startTime = LocalTime.of(9, 0);
LocalTime finishTime = LocalTime.of(14, 30);
hours = calendar.getCalendarHours(DayOfWeek.SATURDAY);
hours.clear();
hours.add(new LocalTimeRange(startTime, finishTime));
```

Now when we look at the working hours for Saturday, this is what we see:

```
SATURDAY is a WORKING day (09:00-14:30)
```

Now we've seen how we can create our own ranges of working time for a day, let's
tackle a slightly more challenging case: dealing with midnight. Our first step
is to take a look at the actual amount of working time we've set up on Saturday.
To do this we call the `getWork` method, as shown below.

```java
Duration duration = calendar.getWork(DayOfWeek.SATURDAY, TimeUnit.HOURS);
System.out.println(duration);
```

This `getWork` method determines the total amount of work on the given day, and
returns this in the format we specify. In this case we've asked for hours, and
we'll be receiving the result as a `Duration` object. `Duration` simply
combines the duration amount with an instance of the `TimeUnit` enumeration so
we always know the units of the duration amount.

Running the code above give us this output:

```
5.5h
```

As you can see, the `toString` method of `Duration` give us a
nicely formatted result, complete with an abbreviation for the units.

Let's try to change Saturday to be 24 hour working. First we'll configure a
midnight to midnight date range:

```java
startTime = LocalTime.MIDNIGHT;
finishTime = LocalTime.MIDNIGHT;
hours.clear();
hours.add(new LocalTimeRange(startTime, finishTime));
System.out.println(formatLocalTimeRanges(calendar.getCalendarHours(DayOfWeek.SATURDAY)));
```

This looks reasonable:

```
00:00-00:00
```

Now let's see how much work this represents:

```java
duration = calendar.getWork(DayOfWeek.SATURDAY, TimeUnit.HOURS);
System.out.println(duration);
```

```
24.0h
```

So we have our 24 hours of work on Saturday!

## Exceptions

After working a few of these 24 hour days on Saturdays, we might be in need of a
vacation! How can we add this to our calendar?

So far we've been working with the `DayOfWeek` class to make changes to days of
the week, rather than any specific date. Now we'll need to work with a specific
date, and add an "exception" for this date. The terminology here can be
slightly confusing when coming from a programming background, but the term
exception is often used by scheduling applications in the context of making
ad-hoc adjustments to a calendar.

```java
LocalDate exceptionDate = LocalDate.of(2022, 5, 10);

boolean workingDate = calendar.isWorkingDate(exceptionDate);
System.out.println(exceptionDate + " is a "
   + (workingDate ? "working" : "non-working") + " day");
```

In the code above we're creating a `LocalDate` instance to represent the date we want
to add an exception for. The code uses the `isWorkingDate` method to determine
whether or not the  given date is a working day. Before we add the exception,
here's the output we get:

```
2022-05-10 is a working day
```

Now we can create our exception.

```java
ProjectCalendarException exception = calendar.addCalendarException(exceptionDate);
exception.setName("A day off");
```

The code above illustrates adding an exception for a single day. The code above
also shows that optionally an exception can be named, this can make it easier
to understand the purpose of each exception. Now if we re-run our code which
displays whether our chosen date is a working day, this is what we see:

```
2022-05-10 is a non-working day
```

We have successfully added an exception to turn this date into a day off!

Perhaps we were being a little too generous in giving ourselves the entire day
off, perhaps in this case we should make this a half day instead. To do that, we
just need to add a time range to the exception:

```java
startTime = LocalTime.of(8, 0);
finishTime = LocalTime.of(12, 0);
exception.add(new LocalTimeRange(startTime, finishTime));
```

Now if we look at our chosen date, this is what we see:

```
2022-05-10 is a working day
```

Let's take a closer look at what's happening on that day:

```java
System.out.println("Working time on Tuesdays is normally "
   + calendar.getWork(DayOfWeek.TUESDAY, TimeUnit.HOURS) + " but on "
   + exceptionDate + " it is "
   + calendar.getWork(exceptionDate, TimeUnit.HOURS));
```

The code above shows how we use the `getWork` method which takes a `DayOfWeek`
as an argument to look at what the default working hours are on a Tuesday, then
we use the `getWork` method which takes a `LocalDate` instance as an argument
to see what's happening on the specific Tuesday of our exception. Here's the
output we get:

```
Working time on Tuesdays is normally 8.0h but on 2022-05-10 it is 4.0h
```

We can see the effect of adding a `LocalTimeRange` to our exception: we've gone
from an exception which changes a working day into a non-working day to an
exception which just changes the number of working hours in the day. This same
approach can be used to change a date which falls on a day that's typically
non-working (for example a Sunday) into a working day, just by adding an
exception with some working hours.

We can also use a single exception to affect a number of days. First let's
write a little code to see the number of working hours over a range of days:

```java
private void dateDump(ProjectCalendar calendar, LocalDate startDate, LocalDate endDate)
{
   for (LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(1)) {
      System.out.println(date + "\t" + calendar.getWork(date, TimeUnit.HOURS));
   }
   System.out.println();
}
```

Running this code with our calendar as its stands produces this output for the
example week we're using:

```
2022-05-23  0.0h
2022-05-24  8.0h
2022-05-25  8.0h
2022-05-26  8.0h
2022-05-27  8.0h
```

Let's add an exception which covers Tuesday to Thursday that week (24th to
26th), and changes the working hours, so there are now only four hours of work
per day (9am to 12pm):

```java
LocalDate exceptionStartDate = LocalDate.of(2022, 5, 24);
LocalDate exceptionEndDate = LocalDate.of(2022, 5, 26);
exception = calendar.addCalendarException(exceptionStartDate, exceptionEndDate);
startTime = LocalTime.of(9, 0);
finishTime = LocalTime.of(13, 0);
exception.add(new LocalTimeRange(startTime, finishTime));
``` 

Here we can see that we're using a different version of the
`addCalendarException` method which takes a start and an end date, rather that
just a single date. Running our code again to print out the working hours for
each day now gives us this output:

```
2022-05-23  0.0h
2022-05-24  4.0h
2022-05-25  4.0h
2022-05-26  4.0h
2022-05-27  8.0h
```

As we can see, we've changed multiple days with this single exception.

## Working Weeks
So far we've looked at using `ProjectCalendarException`, which can make one
change (add working hours, change working hours, or make days non-working) and
apply that change to one day or a contiguous range of days. What if we want to
make more complex changes to the working pattern of a calendar?

Let's imagine that our project has a three week "crunch" period at the beginning
of October where we will need to work 16 hour days, Monday through Friday, and
8 hour days at weekends. (I hope this is a fictional example and you'd don't
have to work at such a high intensity in real life!). We *could* construct this
work pattern using exceptions: we'd need six in total, one for each of the
three sets of weekend days, and one for each of the three sets of week days.

An alternative way to do this is to set up a new working week, using the
`ProjectCalendarWeek` class. "Working Week" is perhaps a slightly misleading
name, as a `ProjectCalendarWeek` can be set up for an arbitrary range of dates,
from a few days to many weeks. What it represents is the pattern of working an
non-working time over the seven days of a week, and this pattern is applied
from the start to the end of the date range we configure.

The `ProjectCalendar` we've been working with so
far is actually already a form of working week (they share a common parent
class). The main differences between the two are that a `ProjectCalendarWeek`
allows us to specify the range of dates over which it is effective, and a
`ProjectCalendarWeek` does not have exceptions: exceptions are only added to
a `ProjectCalendar`. 

For a fresh start, we'll create a new `ProjectCalendar` instance. With this
we'll add a new working week definition and give it a name, to make it easily
identifiable. Now we'll set the dates for which this work pattern is valid
(in this case the first three weeks of October). Finally we mark every day as a
working day. Here's how our example looks in code:

```java
LocalDate weekStart = LocalDate.of(2022, 10, 1);
LocalDate weekEnd = LocalDate.of(2022, 10, 21);
calendar = file.addDefaultBaseCalendar();
ProjectCalendarWeek week = calendar.addWorkWeek();
week.setName("Crunch Time!");
week.setDateRange(new LocalDateRange(weekStart, weekEnd));
Arrays.stream(DayOfWeek.values()).forEach(d -> week.setWorkingDay(d, true));
```

Next we can set up our weekend 9am to 5pm working pattern:

```java
startTime = LocalTime.of(9, 0);
finishTime = LocalTime.of(17, 0);
LocalTimeRange weekendHours = new LocalTimeRange(startTime, finishTime);
Stream.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
   .forEach(d -> week.addCalendarHours(d).add(weekendHours));
```

Finally we can set up our weekday 5am to 9pm pattern:

```java
startTime = LocalTime.of(5, 0);
finishTime = LocalTime.of(21, 0);
LocalTimeRange weekdayHours = new LocalTimeRange(startTime, finishTime);
Stream.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
      DayOfWeek.THURSDAY, DayOfWeek.FRIDAY)
   .forEach(d -> week.addCalendarHours(d).add(weekdayHours));
```

As `ProjectCalendar` and `ProjectCalendarWeek` are both derived from the same
parent class, we can use the same code we did previously to examine how our new
`ProjectCalendarWeek` instance looks:

```
MONDAY is a WORKING day (05:00-21:00)
TUESDAY is a WORKING day (05:00-21:00)
WEDNESDAY is a WORKING day (05:00-21:00)
THURSDAY is a WORKING day (05:00-21:00)
FRIDAY is a WORKING day (05:00-21:00)
SATURDAY is a WORKING day (09:00-17:00)
SUNDAY is a WORKING day (09:00-17:00)
```

To see the effect that our new working week has had on the calendar, let's first
take a look at the week running up to the start of our crunch period. Using the
same code we worked with previously to present working hours for a range of
dates we see this output:

```
2022-09-24  0.0h
2022-09-25  0.0h
2022-09-26  8.0h
2022-09-27  8.0h
2022-09-28  8.0h
2022-09-29  8.0h
2022-09-30  8.0h
```

So starting from Saturday 24th we can see that we have that standard working
pattern: weekends are non-working (zero working hours), and week days have 8
hours of working time.

Now let's look at the first week of our crunch period:

```
2022-10-01  8.0h
2022-10-02  8.0h
2022-10-03  16.0h
2022-10-04  16.0h
2022-10-05  16.0h
2022-10-06  16.0h
2022-10-07  16.0h
```

We can see that the crunch is in full effect, we're working 8 hour days at the
weekend, and 16 hour days for the rest of the week - not something I'd like to
try for any length of time!

To summarise: the `ProjectCalendar` instance itself defines the *default*
working and non-working pattern for the seven week days. Additional working
weeks can be added to the calendar which override this pattern for specific
date ranges.

## Recurring Exceptions
So far we've seen how exceptions can be used to override the default working
pattern established by a calendar for either a single day, or for a contiguous
range of days. We've also seen how an entirely new seven-day working pattern
can be applied across a range of dates by using working weeks. But what if we
want to represent a regularly occurring exception which will change our default
working pattern such as, for example, Christmas Day or Thanksgiving? To deal
with this we can use recurring exceptions.

A recurring exception can be created simply by passing an instance of
`RecurringData` to the `addCalendarException` method.

```java
RecurringData recurringData = new RecurringData();
exception = calendar.addCalendarException(recurringData);
```

Let's create a simple recurence for 1st January for five years:

```java
recurringData.setRecurrenceType(RecurrenceType.YEARLY);
recurringData.setOccurrences(5);
recurringData.setDayNumber(Integer.valueOf(1));
recurringData.setMonthNumber(Integer.valueOf(1));
recurringData.setStartDate(LocalDate.of(2023, 1, 1));
System.out.println(recurringData);
```

The `toString` method on the `RecurringData` class tries to describe the
recurrence as best it can, here's the output we'll see from the code above:

```console
[RecurringData Yearly on the 1 January From 2023-01-01 For 5 occurrences]
```

The example above shows a very simple configuration. Full details of how to use
`RecurringData` are provided elsewhere as they are beyond the scope of this
section.

Before we move on from recurring exceptions, one useful feature of the
`ProjectCalendarException` class is the `getExpandedExceptions` method. This
will convert a recurring exception into a list of individual exceptions
representing each date or range of dates the recurring exception will affect
the calendar. You may find this useful if you need to display or pass this data
on for consumption elsewhere.

## Calendar Hierarchies
Now we've seen how to set up an individual calendar, perhaps we could go ahead
and create calendars for all of the people who will be working on our project?
What we'd quickly find is that a considerable amount of the information in each
calendar will be the same: the same working week pattern, the same public
holidays and so on. We could set all of this up programmatically of course, but
wouldn't it be great if we could change this kind of detail in just one place,
and have all of our other calendars inherit it?

### Creating a Calendar Hierarchy
As it happens, we can do this as our calendars can be organised into a
hierarchy, with each "child" calendar inheriting its configuration from
a "parent" calendar and overriding that configuration as required rather like a
class hierarchy in a programing language). This will allow us to have one
shared "base" calendar for everyone, with derived calendars used for
individuals on our team where we need to add variation, for example personal
vacation time and so on.

```java
ProjectFile file = new ProjectFile();
ProjectCalendar parentCalendar = file.addDefaultBaseCalendar();
LocalDate christmasDay = LocalDate.of(2023, 12, 25);
parentCalendar.addCalendarException(christmasDay);
```

In the example above we've used the familiar `addDefaultBaseCalendar` method to
create a simple calendar, and called `addCalendarException` to add an
exception for Christmas Day 2023.

```java
ProjectCalendar childCalendar = file.addDefaultDerivedCalendar();
childCalendar.setParent(parentCalendar);
System.out.println(christmasDay + " is a working day: "
   + childCalendar.isWorkingDate(christmasDay));
```

Now we've created `childCalendar`, using a method we've not seen before,
`addDefaultBaseCalendar` (we'll talk about this method in more detail in a
minute), and we've used the new calendar's `setParent` method to attach
`parentCalendar` as its parent. We can see the effect of this when we check to
see if Christmas Day 2023 is a working day. This is a Monday so by default it
will be a working day, but as `childCalendar` is inheriting from
`parentCalendar` it picks up the exception defined in `parentCalendar` and
makes Christmas Day a non-working day.

Here's the output when our code is executed:

```
2023-12-25 is a working day: false
```

We can also do the same thing with day types:

```java
parentCalendar.setCalendarDayType(DayOfWeek.TUESDAY, DayType.NON_WORKING);
System.out.println("Is " + DayOfWeek.TUESDAY + " a working day: "
   + childCalendar.isWorkingDay(DayOfWeek.TUESDAY));
```

In the example above we've set Tuesday to be a non-working day in the parent
calendar, and we can see that this is inherited by the child calendar. Here's
the output we see when we execute our code:

```
Is TUESDAY a working day: false
```

So what's special about the "derived calendar" we've just created
(`childCalendar`), why is it different to the normal calendar, and what's the
difference between the `addDefaultBaseCalendar` and `addDefaultDerivedCalendar`
methods?

The answer to this question lies in the `DayType` enumeration. Let's
take a look at the day types for `parentCalendar`.

```
SUNDAY is a NON_WORKING day
MONDAY is a WORKING day
TUESDAY is a NON_WORKING day
WEDNESDAY is a WORKING day
THURSDAY is a WORKING day
FRIDAY is a WORKING day
SATURDAY is a NON_WORKING day
```

So far so good, we have a mixture of working an non-working days, and we can see
that as part of our last example we set Tuesday to be a non-working day. Now
let's take a look at `childCalendar`:

```
SUNDAY is a DEFAULT day
MONDAY is a DEFAULT day
TUESDAY is a DEFAULT day
WEDNESDAY is a DEFAULT day
THURSDAY is a DEFAULT day
FRIDAY is a DEFAULT day
SATURDAY is a DEFAULT day
```

Ah-ha! Here we can see that the `DayType` enumeration actually has a third value
alongside `WORKING` and `NON_WORKING`: `DEFAULT`. The `DEFAULT` value simply
means that we should inherit the parent calendar's settings for this particular
day: so whether the day is working, non-working, what the working hours are,
and so on.

We can override the day type we're inheriting from the base calendar:

```java
childCalendar.setCalendarDayType(DayOfWeek.TUESDAY, DayType.WORKING);
LocalTime startTime = LocalTime.of(9, 0);
LocalTime finishTime = LocalTime.of(12, 30);
childCalendar.addCalendarHours(DayOfWeek.TUESDAY)
   .add(new LocalTimeRange(startTime, finishTime));
```

In the code above we're explicitly setting Tuesday to be a working day, rather
than inheriting the settings for Tuesday from the parent calendar, then we're
adding the working hours we want for Tuesday.

Earlier we said we come back and look at the `addDefaultDerivedCalendar` method
in a little more detail. The main difference between
`addDefaultDerivedCalendar` and `addDefaultBaseCalendar` is that the calendar
created by `addDefaultDerivedCalendar` has no working hours defined, and all
day types are set to `DEFAULT` so everything is inherited from the parent
calendar.

### Working with a Calendar Hierarchy
In general when working with a calendar hierarchy, if we use a calendar to
determine working/non-working time, working hours, and so on for a given date,
anything configured in a child calendar will always override what we find in
the parent calendar. So for example if we have exceptions or working weeks
configured in a child calendar, these will override anything found in a parent
calendar.

If we're asking the calendar a question about a particular day
(rather than a date), for example Monday, Tuesday and so on, we'll use
information from the child calendar if the day type is `WORKING` or
`NON_WORKING`, otherwise we'll work our way up the calendar hierarchy until we
find the first ancestor calendar which does not specify the day type as
`DEFAULT`, and we'll use the configuration for the day in question from that
calendar.

This brings us on to an interesting question: how do we know if we ask the
calendar for a piece of information, whether that's come from the calendar
whose method we've just called, or if the response we've received has come from
another calendar somewhere further up the calendar hierarchy?

As it happens there are only a small number of attributes for which this is
relevant. These are summarised by the table below.

| Attribute | Set | Get | Get with Hierarchy |
|-----------|-----|-----|--------------------|
| Day Type          | `setCalendarDayType`         | `getCalendarDayType`         | `getDayType`        |
| Hours             | `addCalendarHours`           | `getCalendarHours`           | `getHours`          |
| Minutes Per Day   | `setCalendarMinutesPerDay`   | `getCalendarMinutesPerDay`   | `getMinutesPerDay`  |
| Minutes Per Week  | `setCalendarMinutesPerWeek`  | `getCalendarMinutesPerWeek`  | `getMinutesPerWeek` |
| Minutes Per Month | `setCalendarMinutesPerMonth` | `getCalendarMinutesPerMonth` | `getMinutesPerWeek` |
| Minutes Per Year  | `setCalendarMinutesPerYear`  | `getCalendarMinutesPerYear`  | `getMinutesPerYear` |

The first column give us the name of the attribute, and the second column give
the name of the method we'd call to set that attribute for the current
calendar. The third column gives us the name of the method we'd use to retrieve
the attribute *from the current calendar only* (i.e this will ignore any parent
calendars). Finally the last column gives us the name of the method we'd call
to retrieve the attribute from the current calendar, or inherit that attribute
from a parent calendar if it is not present in the current calendar.

> We haven't looked at the *Minutes Per X* attributes so far. The values
> they contain are used when calculating working time. One interesting 
> point to note is that if no calendars in a hierarchy define these values
> the default values will be retrieved from from the `ProjectFile`
> configuration, which is represented by the `ProjectConfig` class.

## How deep is your Hierarchy?
MPXJ will allow you to create an arbitrarily deep hierarchy of calendars if you
wish by establishing parent-child relationships between the calendars you
create. Most schedule application file formats will only support a limited
hierarchy of calendars, which you will see when you read files of this type
when using MPXJ. The notes below briefly outlines how calendar hierarchies
operate in some of the applications MPXJ can work with.

If you are using MPXJ to create or modify schedule data, when you write the
results to a file MPXJ will attempt to ensure that the calendars it writes to
the file format you have chosen reflect what the target application is
expecting. This means that MPXJ may end up "flattening" or otherwise
simplifying a set of calendars and their hierarchy to ensure that they are read
correctly by the target application and are "functionally equivalent" in use.

### Microsoft Project
Microsoft Project uses two tiers of calendars. The first tier of calendars are
referred to as "base calendars", one of which is marked as the default calendar
for the project. Work is scheduled based on the default calendar, unless a task
explicitly selects a different base calendar to use when being scheduled, or
resources with their own calendars have been assigned to the task. Each
resource will have its own calendar, which is always derived from a base
calendar.

> Note that, as you might expect, material resources don't have a calendar!

### Primavera P6
The situation with P6 is a little more complicated, although it's still a
two tier arrangement. P6 has the concept of Global calendars (broadly similar
to base calendars in Microsoft Project). These can be assigned to activities in
any project. Global calendars are never derived from other calendars.

You can also have Project calendars which, as their name suggests, can only be
assigned to activities in the project to which they belong. Project calendars
can be derived from a Global Calendar, or they can have no parent calendar.

Finally you can have two types of resource calendar: Shared, or Personal.
These can either be derived from a Global calendar, or can have no parent.
A Shared resource calendar can be assigned to multiple resources, but a Personal
resource calendar can only be assigned to a single resource.

When reading a P6 schedule, the `ProjectCalendar` method `getType` can be used
to retrieve the calendar type (Global, Shared, or Personal), while the
`getPersonal` method returns a Boolean flag indicating if the calendar is a
Personal resource calendar.

### Others
ConceptDraw, Planner, SureTrak and TurboProject all support some form of
calendar hierarchy, although Planner is the only one which definitely supports
an arbitrarily deep nested calendar structure.

## Calendar Container
So far we've looked at creating and configuring calendars, and lining them
together in a hierarchy. If we've just read a schedule in from a file, how can
we examine the calendars it contains? Let's set up some calendars and take a
look:

```java
ProjectFile file = new ProjectFile();
ProjectCalendar calendar1 = file.addCalendar();
calendar1.setName("Calendar 1");

ProjectCalendar calendar2 = file.addCalendar();
calendar2.setName("Calendar 2");

ProjectCalendar calendar3 = file.addCalendar();
calendar3.setName("Calendar 3");
```

Our sample code above creates three calendars, each with a distinct name. To see
what calendars our file contains we can use the `ProjectFile` method
`getCalendars`:

```java
file.getCalendars().forEach(c -> System.out.println(c.getName()));
```

Which gives us the following output, as we'd expect:

```
Calendar 1
Calendar 2
Calendar 3
```

The `getCalendars` method returns an object which implements the
`List<ProjectCalendar>` interface, but it also does more for us than just that.
The actual object being returned is a `ProjectCalendarContainer`, which is in
charge of managing the calendars in the file and making it easy to access
them.

The typical way this is done is through the use of the calendar's Unique ID
attribute. Each calendar has an `Integer` Unique ID, typically this is
read as part of the calendar information from a schedule file, or if you are
creating a schedule yourself, the default is for the Unique ID to be
automatically populated. Let's see:


```java
file.getCalendars().forEach(c -> System.out.println(c.getName()
   + " (Unique ID: " + c.getUniqueID() + ")"));
```

Here's what we get:

```
Calendar 1 (Unique ID: 1)
Calendar 2 (Unique ID: 2)
Calendar 3 (Unique ID: 3)
```

Let's use a Unique ID to retrieve a calendar:

```java
ProjectCalendar calendar = file.getCalendars().getByUniqueID(2);
System.out.println(calendar.getName());
```

Here's the result of running this code:

```
Calendar 2
```

> The `ProjectCalendarContainer` class also allows us to retrieve calendars by
> name, although that's not recommended as MPXJ doesn't enforce presence or
> uniqueness constraints on calendar names.

Most of the time accessing a calendar from some other part of MPXJ is handled
for you, for example to retrieve a resource's calendar you just need to call
the `Resource` method `getCalendar` rather than having to use
`ProjectCalendarContainer` to retrieve it by Unique ID.

## Calendar Relationships
The `ProjectCalendar` class provides a variety of methods to allow us to explore
how it relates to other calendars and the rest of the schedule.

As we've been discussing the hierarchy of calendars, the first method we can try
is `isDerived`, which will return `true` if this calendar has been derived from
a parent calendar. Alongside this we can also use the `getParent` method to 
retrieve this calendar's parent. We can traverse a hierarchy of calendars using
this method until `getParent` returns `null` at which point we know we have
reached a "base" calendar and can go no further.

Calendars can also be assigned to both Tasks and Resources. The `getTasks` and
`getResources` methods will each retrieve a list of the tasks and resources
which explicitly use this calendar.

Finally, earlier in this section we mentioned the idea of the default calendar
for a project. We can set or retrieve the default calendar using the
`ProjectFile` methods `setDefaultCalendar` and `getDefaultCalendar`, as
illustrated below.

```java
ProjectFile file = new ProjectFile();
ProjectCalendar calendar = file.addDefaultBaseCalendar();
file.setDefaultCalendar(calendar);
System.out.println("The default calendar name is "
   + file.getDefaultCalendar().getName());
```

As the name suggests, the default calendar will be used for all date, time,
duration and work calculations if no other calendar has been assigned
explicitly.
