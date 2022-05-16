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
default calendar defines Monday to Friday as working days, with 8 working hours
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
to represent the start and end times. The year, month and day components of each
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

Now we've seen how we can create our own ranges of working time for a day, let's
tackle a slightly more challenging case: dealing with midnight. Our first step
is to take a look at the actual amount of working time we've set up on Saturday.
To do this we call the `getWork` method, as shown below.

```java
Duration duration = calendar.getWork(Day.SATURDAY, TimeUnit.HOURS);
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
startTime = DateHelper.getTime(0, 0);
finishTime = DateHelper.getTime(0, 0);
hours.clear();
hours.add(new DateRange(startTime, finishTime));
System.out.println(formatDateRanges(calendar.getHours(Day.SATURDAY)));
```

This looks reasonable:

```
00:00-00:00
```

Now let's see how much work this represents:

```java
duration = calendar.getWork(Day.SATURDAY, TimeUnit.HOURS);
System.out.println(duration);
```

```
0.0h
```

Oh dear, that's not what we're expecting! This is one of the "gotchas" of the
way MPXJ works presently: if we want to use midnight at the end of a date
range, we actually need to explicitly create a time which is "+1 day" from our
start time. The code below shows how we can do this using `Calendar`.

```java
javaCalendar.set(Calendar.HOUR_OF_DAY, 0);
javaCalendar.set(Calendar.MINUTE, 0);
startTime = javaCalendar.getTime();

javaCalendar.add(Calendar.DAY_OF_YEAR, 1);
finishTime = javaCalendar.getTime();

hours.clear();
hours.add(new DateRange(startTime, finishTime));
System.out.println(formatDateRanges(calendar.getHours(Day.SATURDAY)));
```

We still end up with a range which looks like this:

```
00:00-00:00
```

But crucially now if we re-run the duration calculation, we get:

```
24.0h
```

So we have our 24 hours of work on Saturday!

## Exceptions

After working a few of these 24 hour days on Saturdays, we might be in need of a
vacation! How can we add this to our calendar?

So far we've been working with the `Day` class to make changes to days of the
week, rather than any specific date. Now we'll need to work with a specific
date, and add an "exception" for this date. The terminology here can be
slightly confusing when coming from a programming background, but the term
exception is often used by scheduling applications in the context of making
ad-hoc adjustments to a calendar.

```java
DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
Date exceptionDate = df.parse("10/05/2022");

boolean workingDate = calendar.isWorkingDate(exceptionDate);
System.out.println(df.format(exceptionDate) + " is a " + (workingDate ? "working" : "non-working") + " day");
```

In the code above we're creating a `Date` instance to represent the date we want
to add an exception for. The code uses the `isWorkingDate` method to determine
whether or not the  given date is a working day. Before we add the exception,
here's the output we get:

```
10/05/2022 is a working day
```

Now we can create our exception.

```java
ProjectCalendarException exception = calendar.addCalendarException(exceptionDate, exceptionDate);
exception.setName("A day off");
```

The code above illustrates adding an exception for a single day, hence we're
passing the same date twice to the `addCalendarException` method (i.e. this is
the start date and the end date of the exception). The time component of the
`Date` instance you pass in here is irrelevant, the exception is always
effective from the beginning of the day of the start date, to the end of the
day of the finish date. The code above also shows that optionally an exception
can be named, this can make it easier to understand the purpose of each
exception. Now if we re-run our code which displays whether our chosen date is
a working day, this is what we see:

```
10/05/2022 is a non-working day
```

We have successfully added an exception to turn this date into a day off!

Perhaps we were being a little too generous in giving ourselves the entire day
off, perhaps in this case we should make this a half day instead. To do that, we
just need to add a time range to the exception:

```java
startTime = DateHelper.getTime(8, 0);
finishTime = DateHelper.getTime(12, 0);
exception.add(new DateRange(startTime, finishTime));
```

Now if we look at our chosen date, this is what we see:

```
10/05/2022 is a working day
```

Let's take a closer look at what's happening on that day:

```java
System.out.println("Working time on Tuesdays is normally "
   + calendar.getWork(Day.TUESDAY, TimeUnit.HOURS) + " but on "
   + df.format(exceptionDate) + " it is "
   + calendar.getWork(exceptionDate, TimeUnit.HOURS));
```

The code above shows how we use the `getWork` method which takes a `Day` as an
argument to look at what the default working hours are on a Tuesday, then we
use the `getWork` method which takes a `Date` instance as an argument to see
what's happening on the specific Tuesday of our exception. Here's the output we
get:

```
Working time on Tuesdays is normally 8.0h but on 10/05/2022 it is 4.0h
```

We can see the effect of adding a `DateRange` to our exception: we've gone from
an exception which changes a working day into a non-working day to an exception
which just changes the number of working hours in the day. This same approach
can be used to change a date which falls on a day that's typically non-working
(for example a Sunday) into a working day, just by adding an exception with
some working hours.

We can also use a single exception to affect a number of days. First let's
write a little code to see the number of working hours over a range of days:

```java
Calendar start = Calendar.getInstance();
start.setTime(df.parse("23/05/2022"));
Calendar end = Calendar.getInstance();
end.setTime(df.parse("28/05/2022"));

for (Date date = start.getTime(); start.before(end); start.add(Calendar.DATE, 1), date = start.getTime()) {
   System.out.println(df.format(date) + "\t" + calendar.getWork(date, TimeUnit.HOURS));
}
```

Running this code with our calendar as its stands produces this output for the
example week we're using:

```
23/05/2022  0.0h
24/05/2022  8.0h
25/05/2022  8.0h
26/05/2022  8.0h
27/05/2022  8.0h
```

Let's add an exception which covers Tuesday to Thursday that week (24th to
26th), and changes the working hours, so there are now only four hours of work
per day (9am to 12pm):

```java
Date exceptionStartDate = df.parse("24/05/2022");
Date exceptionEndDate = df.parse("26/05/2022");
exception = calendar.addCalendarException(exceptionStartDate, exceptionEndDate);
startTime = DateHelper.getTime(9, 0);
finishTime = DateHelper.getTime(13, 0);
exception.add(new DateRange(startTime, finishTime));
``` 

Running our code again to print out the working hours for each day now gives us
this output:

```
23/05/2022  0.0h
24/05/2022  4.0h
25/05/2022  4.0h
26/05/2022  4.0h
27/05/2022  8.0h
```

As we can see, we've changed multiple days with this single exception.



## Working Weeks

## Recurring Exceptions

## Expanded Exceptions

## Calendar Hierarchies

## Calendars in the Wild
Reading calendars.
Changes made when writing calendars.
Note resource, project, and personal calendars from P6.

## To Do
Reader prerequisites.
Timezones.
Task and Resource relationships with the calendar.

recurring exceptions

other calendar attributes section?
calendar minutes per attributes



