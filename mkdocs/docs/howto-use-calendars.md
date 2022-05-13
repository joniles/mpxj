# How To: Use Calendars
Calendars are the foundation on which schedules are built. They determine when
work can be carried out, and when work is not possible. Given some tasks we
need to plan, and knowing how much work each task will require, a calendar can
be used to decide when work on each task could start and how much elapsed time
will be required to complete the tasks.

## Calendars in MPXJ
Let's see how calendars work in MPXJ. First let's try creating one. As it
happens, the `ProjectFile` class provides a convenience method to create a
default calendar. The calendar it creates is modelled on the `Standard`
calendar you'd see in Microsoft Project if you created a new project. This
default calendar defines Monday to Friday as workng days, with 8 working hours
each day (8am to noon, then 1pm to 5pm).

```java
ProjectFile file = new ProjectFile();
ProjectCalendar calendar = file.addDefaultBaseCalendar();
System.out.println("The calendar name is " + calendar.getName());
```

As you can see from the code above, the calendar also has a name which we can
set to distinguish between different calendars.

## Working Days
Let's see what the calendar can tell us. First we'll use the `Day` enumeration
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

## Working Hours
So far, all we have done is set a flag which tells us whether a day is working
or non-working. How do we know the working times on those days? We can use the
`getHours` method to find that information:


`getHours` returns a `List` of `DateRange` instances. `DateRange` is a
simple immutable class which represents a span of time between a start date and
an end date as an inclusive range. Let's try printing these `DateRange` instances
to our output to see what we get:

```java
List<DateRange> hours = calendar.getHours(Day.TUESDAY);
hours.forEach(System.out::println);
```

Here's the output:

```
[DateRange start=Sat Jan 01 08:00:00 GMT 1 end=Sat Jan 01 12:00:00 GMT 1]
[DateRange start=Sat Jan 01 13:00:00 GMT 1 end=Sat Jan 01 17:00:00 GMT 1]
```

This isn't quite what we were expecting! What's happening here is that
`DateRange` is using `java.util.Date` values to represent the start and end of
the range, and the `toString` method of the `dateRange` class is just using
these values directly. As these `java.util.Date` represents a full timestamp,
we're seeing the entire timestamp here in our output. The day, month and year
components of the timestamp have been set to a default value (in this case
January 1st 0001).

> As work on MPXJ started in 2002, `java.util.Date` was the logical choice
> for representing dates, times and timestamps. Since Java 1.8, `LocalTime`
> would probably be a better representation for these values. This is likely
> to be a future enhancement to MPXJ.

Let's add a method to format the hours of a day tidily for display:

```java
private String formatDateRanges(List<DateRange> hours) {
  DateFormat df = new SimpleDateFormat("HH:mm");
  return hours.stream()
     .map(h -> df.format(h.getStart()) + "-" + df.format(h.getEnd()))
     .collect(Collectors.joining(", "));
}
```

In a real application we probably wouldn't want to instantiate a new
`DateFormat` each time we call the method, but this is fine as a demonstration.
So now our output looks like this:

```
08:00-12:00, 13:00-17:00
```

Let's use this method to take a look at the whole week again:

```java
for (Day day : Day.values()) {
 String working = calendar.isWorkingDay(day) ? "Working" : "Non-working";
 System.out.println(day
    + " is a " + working + " day ("
    + formatDateRanges(calendar.getHours(day)) + ")");
}
```

Here's the output:

```
SUNDAY is a Non-working day ()
MONDAY is a Non-working day ()
TUESDAY is a Working day (08:00-12:00, 13:00-17:00)
WEDNESDAY is a Working day (08:00-12:00, 13:00-17:00)
THURSDAY is a Working day (08:00-12:00, 13:00-17:00)
FRIDAY is a Working day (08:00-12:00, 13:00-17:00)
SATURDAY is a Working day ()
```

The one thing we're missing now is that although we have set Saturday to be a
working day, it doesn't have any working hours. MPXJ has some constants which
can be used to help us add some working hours:

```java
hours = calendar.getCalendarHours(Day.SATURDAY);
hours.add(ProjectCalendarDays.DEFAULT_WORKING_MORNING);
hours.add(ProjectCalendarDays.DEFAULT_WORKING_AFTERNOON);
```

Now when we examine our week this is what we see:

```
SUNDAY is a Non-working day ()
MONDAY is a Non-working day ()
TUESDAY is a Working day (08:00-12:00, 13:00-17:00)
WEDNESDAY is a Working day (08:00-12:00, 13:00-17:00)
THURSDAY is a Working day (08:00-12:00, 13:00-17:00)
FRIDAY is a Working day (08:00-12:00, 13:00-17:00)
SATURDAY is a Working day (08:00-12:00, 13:00-17:00)
```

> The version of MPXJ at the time of writing (10.5.0) has a limitation
> that if `setDayType` is used to make a day into a working day, we don't
> automatically add working hours for it. This behaviour is likely to
> change with the next major version of MPXJ.

What if we want to supply some working hours different from the defaults we've
used so far? To set our own working hours we just need to create as many
`DateRange` instances as we need using a pair of `Date` instances for each one
to represent the start and end times. The year, month and day compoents of each
`Date` are ignored. Here's an example of using Java's `Calendar` class
to set a new working time for Saturday:

```java
Calendar javaCalendar = Calendar.getInstance();
javaCalendar.set(Calendar.HOUR_OF_DAY, 9);
javaCalendar.set(Calendar.MINUTE, 0);
Date startTime = javaCalendar.getTime();

javaCalendar.set(Calendar.HOUR_OF_DAY, 14);
javaCalendar.set(Calendar.MINUTE, 30);
Date finishTime = javaCalendar.getTime();

hours = calendar.getCalendarHours(Day.SATURDAY);
hours.clear();
hours.add(new DateRange(startTime, finishTime));
```

Now when we look at the working hours for Saturday, this is what we see:

```
SATURDAY is a Working day (09:00-14:30)
```

MPXJ actually provides a helper method to simplify this process, here's the
equivalent code:

```java
startTime = DateHelper.getTime(9, 0);
finishTime = DateHelper.getTime(14, 30);
```



## To Do
24 hour working/midnigh handling
Reader prerequisites.
Timezones.
Task and Resource relationships with the calendar.


