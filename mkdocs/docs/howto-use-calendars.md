# How To: Use Calendars
Calendars are the foundation on which schedules are built. They determine when
work can be carried out, and when work is not possible. Given some tasks we
need to plan, and knowing how much work each task will require, a calendar can
be used to decide when work on each task could start and how much elapsed time
will be required to complete the tasks.

## Calendars in MPXJ
Let's see how calendars work in MPXJ. First let's try creating one:

```java
ProjectFile file = new ProjectFile();
ProjectCalendar calendar = file.addDefaultBaseCalendar();
```

As it happens, the `ProjectFile` class provides a convenience method to create a
default calendar. This is modelled on the `Standard` calendar you'd see in
Microsoft Project if you created a new project. This default calendar defines
Monday to Friday as workng days, with 8 working hours  each day (8am to noon,
then 1pm to 5pm).

## Working Days
Let's see what the calendar can tell us. First we'l use the `Day` enumeration
to retrieve the working/non-working state for each day.

```java
for (Day day : Day.values())
{
    String working = calendar.isWorkingDay(day) ? "Working" : "Non-working";
    System.out.println(day + " is a " + working + " day");
}
```

Running the code shown above will produce output like this:

```
SUNDAY is a Non-working day
MONDAY is a Working day
TUESDAY is a Working day
WEDNESDAY is a Working day
THURSDAY is a Working day
FRIDAY is a Working day
SATURDAY is a Non-working day
```

We can use the `setWorkingDay` method to change our pattern of working day.
Let's make Saturday a working day for our team, and make Monday a non-working
day to compensate.

```java
calendar.setWorkingDay(Day.SATURDAY, true);
calendar.setWorkingDay(Day.MONDAY, false);
```

Now if we use the loop we saw previously to inspect the week days, we'll see
this output:

```
SUNDAY is a Non-working day
MONDAY is a Non-working day
TUESDAY is a Working day
WEDNESDAY is a Working day
THURSDAY is a Working day
FRIDAY is a Working day
SATURDAY is a Working day
```

So far, all we have done is set a flag which tells us whether a day is working
or non-working. How do we know the working times on those days? We can use the
`getHours` method to find that information:


`getHours` returns a `List` of `DateRange` instances. `DateRange` is a
simple immutable class which represents a span of time between a start date and
an end date as an inclusive range. Let's try printing these `DateRange` instances
to our output to see what we get:

```java
ProjectCalendarHours hours = calendar.getHours(Day.TUESDAY);
hours.forEach(System.out::println);
```

Here's the output:

```
[DateRange start=Sat Jan 01 08:00:00 GMT 1 end=Sat Jan 01 12:00:00 GMT 1]
[DateRange start=Sat Jan 01 13:00:00 GMT 1 end=Sat Jan 01 17:00:00 GMT 1]
```

This isn't quite what we were expecting! What's happening here is that
`DateRange` is using `java.util.Date` values to represent the start and end of
the range. As these `java.util.Date` represents a full timestamp, we're seeing
the entire timestamp here in our output. The day, month and year components of
the timestamp have been set to a default value (in this case January 1st
0001).

> As work on MPXJ started in 2002, `java.util.Date` was the logical choice
> for representing dates, times and timestamps.


## To Do
Timezones

