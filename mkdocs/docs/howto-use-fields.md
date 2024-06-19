# How To: Use Fields
Once you've read a schedule using MPXJ, and you have a `ProjectFile` instance
with tasks, resources and resource assignments, how do you access the data
represented as fields in each of these entities? If you're
creating or updating a schedule, how can you assign values to fields? This
section explains the different approaches you can take in each of these cases.

## Setter and Getter Methods
Let's start by creating a task we can use to demonstrate some of these
approaches:

```java
ProjectFile file = new ProjectFile();
Task task = file.addTask();
```

When you already know exactly which field you need to access, you can work with 
the data these fields contain in a type-safe way by using the setter and getter
methods provided by each class, for example:

```java
task.setName("Task 1");

String name = task.getName();
System.out.println("Task name: " + name);
```

Here's the output from the sample code:

```
Task name: Task 1
```

Here we can see that we are able to set the name of the task using a `String`,
and when we call the getter method we'll be returned the name as a `String`.
How about working with a field that has a type other than a String?

```java
LocalDateTime startDate = LocalDateTime.of(2022, 5, 10, 8, 0);
task.setStart(startDate);

System.out.println("Start date: " + task.getStart());
```

Here's the output from the sample code:

```
Start date: 2022-05-10T08:00
```

We're setting and retrieving the task's start date using a `LocalDateTime`
instance. For almost all of the fields supported by tasks, resources, and
resource assignments you'll find a pair of getter and setter methods allowing
you to access and modify the field with a convenient type safe interface.

## Field Enumerations
What if we don't know ahead of time which fields we need to access? For example,
what if our application allows users to choose which fields to display for
each task? In this case we can use a data-driven approach to read and write
fields, as shown in the example below.

```java
task = file.addTask();
task.set(TaskField.NAME, "Task 2");

name = (String)task.get(TaskField.NAME);
System.out.println("Task name: " + name);

startDate = LocalDateTime.of(2022, 5, 11, 8, 0);
task.set(TaskField.START, startDate);

System.out.println("Start date: " + task.getStart());
```

Here's the output from this sample code:

```
Task name: Task 2
Start date: 2022-05-11T08:00
```

What are the `TaskField` values in the example above? `TaskField` is an
enumeration representing all of the fields of a `Task` instance. This type of
enumeration is not unique to tasks, there are four main enumerations
available:

* `ProjectField`: fields available from `ProjectProperties`
* `ResourceField`: fields available from a `Resource` 
* `TaskField`: fields available from a `Task`
* `AssignmentField`: fields available from a `ResourceAssignment`

The `ProjectProperties`, `Resource`, `Task` and `ResourceAssignment` classes
noted above actually all implement the `FieldContainer` interface. This is the
interface that gives us the `get` and `set` methods we've seen in the examples
above. `FieldContainer` also provides us with one more interesting method:
`getCachedValue`. What is this, and why is it different to the `get` method?
Let's take a step back and look at calculated values to understand where
`getCachedValue` fits in.

## Calculated Fields
Some of the fields available from each of these classes can actually contain a
calculated value. For example: the `Task` field "Start Variance" represents the
difference between the Baseline Start date and the Start date of a task. Some
schedules may provide this value for us when we read the data they contain,
others may not. If we don't have this value when we read our schedule data, but
we do have a Baseline Start and Start date available to us, then we can perform
the calculation ourselves to produce the Start Variance value. The example
below illustrates this:

```java
// Set up the sample project
ProjectFile file = new ProjectFile();

// We need at least a default calendar to calculate variance
file.setDefaultCalendar(file.addDefaultBaseCalendar());

// Create tasks
Task task1 = file.addTask();
Task task2 = file.addTask();

// Set up example dates
LocalDateTime baselineStart = LocalDateTime.of(2022, 5, 1, 8, 0);
LocalDateTime startDate = LocalDateTime.of(2022,5, 10, 8, 0);

// Update task1 using methods
task1.setStart(startDate);
task1.setBaselineStart(baselineStart);

// Update task2 using TaskField enumeration
task2.set(TaskField.START, startDate);
task2.set(TaskField.BASELINE_START, baselineStart);

// Show the variance being retrieved by method and TaskField enumeration
System.out.println("Task 1");
System.out.println("Start Variance from method: "
   + task1.getStartVariance());
System.out.println("Start Variance from get: "
   + task1.get(TaskField.START_VARIANCE));
System.out.println();

System.out.println("Task 2");
System.out.println("Start Variance from method: "
   + task2.getStartVariance());
System.out.println("Start Variance from get: "
   + task2.get(TaskField.START_VARIANCE));
```

Here's the output from running this code:

```
Task 1
Start Variance from method: 6.0d
Start Variance from get: 6.0d

Task 2
Start Variance from method: 6.0d
Start Variance from get: 6.0d
```

Regardless of how we set up the data, both the `getStartVariance` method and the
call to `get(TaskField.START_VARIANCE)` trigger the calculation and produce the
expected Start Variance value.

Rather than immediately discarding the Start Variance value we've just
calculated, this value is cached as part of the data held by the task, and will
be returned next time we use the `getStartVariance` method or we call
`get(TaskField.START_VARIANCE)`.


## Cached Values
The `getCachedValue` method allows us to retrieve a field _without attempting to
calculate a value_. It's not a method you'd normally expect to use, but it's
worth mentioning for completeness. Let's take a look at this using a new
example:

```java
// Set up the sample project with a default calendar
ProjectFile file = new ProjectFile();
file.setDefaultCalendar(file.addDefaultBaseCalendar());

// Set up example dates
LocalDateTime baselineStart = LocalDateTime.of(2022, 5, 1, 8, 0);
LocalDateTime startDate = LocalDateTime.of(2022,5, 10, 8, 0);

// Create a task
Task task = file.addTask();
task.setStart(startDate);
task.setBaselineStart(baselineStart);

System.out.println("Start Variance using getCachedValue(): " 
   + task.getCachedValue(TaskField.START_VARIANCE));
System.out.println("Start Variance using get(): " 
   + task.get(TaskField.START_VARIANCE));
System.out.println("Start Variance using getCachedValue(): " 
   + task.getCachedValue(TaskField.START_VARIANCE));
```

The output from this code is:

```
Start Variance using getCachedValue(): null
Start Variance using get(): 6.0d
Start Variance using getCachedValue(): 6.0d
```

What we can see happening here is that using the `getCachedValue` method
initially returns `null` as the Start Variance is not present, and MPXJ
doesn't attempt to calculate it. When we use the `get` method, MPXJ sees that it
doesn't have a value for this field and knows how to calculate it, and
returns the expected result. Finally if we use the `getCachedValue` method
again, as we've now calculated this value and cached it, the method returns the
Start Variance.

In summary, `getCachedValue` will never attempt to calculate values for fields
which are not already present. This can be useful if you want to read a
schedule using MPXJ, but retrieve only the fields which were in the original
schedule, not calculated or inferred by MPXJ.

## FieldType
Earlier in this section we noted that there were four main enumerations
representing the fields which particular classes can contain.

* `ProjectField`
* `ResourceField`
* `TaskField`
* `AssignmentField`

What I didn't mention then is that each of these enumerations implements the
`FieldType` interface which defines a common set of methods for each of these
enumerations. The most interesting of these methods are:

* `name()`
* `getName()`
* `getFieldTypeClass()`
* `getDataType()`

The `name()` method retrieves the name of the enumeration value exactly as it
appears in the code. The `getName()` method returns a localized version of the
name, suitable for display to end users (currently English is the default and
only supported locale).

The `getFieldTypeClass()` method returns a value from the `FieldTypeClass`
enumeration which will help you to determine which kind of object this
`FieldType` belongs to (for example task, resource, and so on). Finally the
`getDataType()` method will return a value from the `DataType` enumeration
which indicates the data type you will receive from the `get` method when
accessing this field, and the type to pass to the `set` method when updating
the field.

Here's some example code to make this a little clearer:

```java
FieldType type = TaskField.START_VARIANCE;

System.out.println("name(): " + type.name());
System.out.println("getName(): " + type.getName());
System.out.println("getFieldTypeClass(): " + type.getFieldTypeClass());
System.out.println("getDataType():" + type.getDataType());
```

In this case we're using the Task Start Variance field as an example. Here's
the output:

```
name(): START_VARIANCE
getName(): Start Variance
getFieldTypeClass(): TASK
getDataType(): DURATION
```

Returning to our earlier example of how we might allow a user to select 
fields we will display, we can use the data type of the selected field
to determine how we format the value for display.

```java
private String getValueAsText(FieldContainer container, FieldType type)
{
    Object value = container.get(type);
    if (value == null)
    {
        return "";
    }

    String result;
    switch (type.getDataType())
    {
        case CURRENCY:
        {
            result = new DecimalFormat("£0.00").format(value);
            break;
        }

        case DATE:
        {
            result = DateTimeFormatter.ofPattern("dd/MM/yyyy").format((LocalDateTime)value);
            break;
        }

        case BOOLEAN:
        {
            result = ((Boolean)value).booleanValue() ? "Yes" : "No";
            break;
        }

        default:
        {
            result = value.toString();
            break;
        }
    }

    return result;
}
```

The sample code above implements a generic method which can work with any class
implementing the `FieldContainer` interface (for example, `Task`, `Resource`
and so on). Given the particular field the user has asked us to display
(passed in via the `type` argument), we retrieve the value from the container
as an `Object`, then use the data type to decide how best to format the value
for display. (This is obviously a contrived example - I wouldn't recommend
creating new instances of `DecimalFormat` and `DateTimeFormatter` each time you
need to format a value!)

## Custom Fields
So far we've seen how simple fields like Name and Start can be accessed and
modified using both field-specific and generic methods. Name and Start are
examples of standard fields which might be provided and managed by schedule
applications, and have a well understood meaning. What if we have some
additional data we want to capture in our schedule, but that data doesn't fit
into any of these standard fields?

Microsoft Project's solution to this problem is Custom Fields. By default
Microsoft Project provides a number of general purpose fields with names
like "Text 1", "Text 2", "Date 1", "Date 2" and so on, which can be used to
relevant vales as part of the schedule. If we look for methods like `setText1`
or `setDate1` we won't find them, so how can we work with these fields?

The answer is quite straightforward, for each of these custom fields you'll
find getter and setter methods which take an integer value, for example:

```java
task.setText(1, "This is Text 1");
String text1 = task.getText(1);
System.out.println("Text 1 is: " + text1);
```

If you're working with the generic `get` and `set` methods, the situation is
more straightforward as each individual field has its own enumeration, as 
shown below:

```java
task.set(TaskField.TEXT1, "This is also Text 1");
text1 = (String)task.get(TaskField.TEXT1);
System.out.println("Text 1 is: " + text1);
```

For `Task`, `Resource` and `ResourceAssignment` the following custom fields are
available for use:

* Cost 1-10
* Date 1-10
* Duration 1-10
* Flag 1-20
* Finish 1-10
* Number 1-20
* Start 1-10
* Text 1-30
* Outline Code 1-10 (`Task` and `Resource` only)

Microsoft Project allows users to configure custom fields. This facility can be
used to do something as simple as provide an alias for the field, allowing it
to be displayed with a meaningful name rather than something like "Text 1"
or "Date 1". Alternatively there are more complex configurations available, for
example constraining the values that can be entered for a field by using a
lookup table, or providing a mask to enforce a particular format.

Information about custom field configurations can be obtained from the
`CustomFieldsContainer`. The sample code below provides a simple illustration
of how we can query this data.

```java
ProjectFile file = new UniversalProjectReader().read("example.mpp");

CustomFieldContainer container = file.getCustomFields();
for (CustomField field : container)
{
    FieldType type = field.getFieldType();
    String typeClass = type.getFieldTypeClass().toString();
    String typeName = type.name();
    String alias = field.getAlias();
    System.out.println(typeClass + "." + typeName + "\t" + alias);
}
```

Depending on how the custom fields in your schedule are configured,
you'll see output like this:

```
TASK.TEXT1      Change Request Reason
TASK.NUMBER1    Number of Widgets Required
RESOURCE.DATE1  Significant Date
```

In the source above, the first thing we're retrieving from each `CustomField`
instance is the `FieldType`, which identifies the field we're configuring. The
values we retrieve here will be from one of the enumerations we've touched on
previously in this section, for example `TaskField`, `ResourceField` and so
on.

The next thing we're doing in our sample code is to create a representation of
the parent type to which this field belongs, followed by the name of the field
itself (this is what's providing us with the value `TASK.TEXT1` for example).
Finally we're displaying the alias which has been set by the user for this
field.

> It's important to note that for schedules from Microsoft
> Project, there won't necessarily be a `CustomField` entry for
> all of the custom fields in use in a schedule. For example, if a user has
> added values to the "Text 1" field for each of the tasks in their schedule,
> but have not configured Text 1 in some way (for example by setting an
> alias or adding a lookup table) there won't be an entry for "Text 1" in the
> `CustomFieldContainer`.

As well as iterating through the collection of `CustomField` instances for the
current schedule, you can directly request the `CustomField` instance for a
specific field, as shown below:

```java
CustomField fieldConfiguration = container.get(TaskField.TEXT1);
```

One common use-case for the configuration data help in `CustomFieldContainer` is
to locate particular information you are expecting to find in the schedule. For
example, let's say that you know that the schedule you're reading should have a
field on each task which users have named "Number of Widgets Required", and you
want to read that data. You can determine which field you need by using
a method call similar to the one shown below:

```java
FieldType fieldType = container.getFieldTypeByAlias(
    FieldTypeClass.TASK,
   "Number of Widgets Required");
```

Note that the first argument we need to pass identifies which parent entity
we're expecting to find the field in. The `CustomFieldContainer` will have
entries from all field containers (tasks, resources, resource assignments and
so on) so this is used to locate the correct one - particularly useful if, for
example, a task and a resource might both have a field with the same alias!
Remember: this method will return `null` if we don't have a field with the
alias you've provided.

Once we have the `FieldType` of the field we're looking for,  we can use this to
retrieve the value using the `get` method as we've seen earlier in this
section:

```java
Task task = file.getTaskByID(Integer.valueOf(1));
Object value = task.get(fieldType);
```

Finally, there are a couple of convenience methods to make retrieving a field by
its alias easier. The first is that each "container" class for the various
entities also provides a `getFieldTypeByAlias` method. If you know ahead of
time you're looking for a field in a particular entity, this will simplify your
code somewhat. The example below illustrates this: as we're looking for a task
field we can go straight to the `TaskContainer` and ask for the field with the
alias we're looking for:

```java
fieldType = file.getTasks().getFieldTypeByAlias("Number of Widgets Required");
```

Lastly, you _can_ actually retrieve the value of a field directly from the
parent entity using its alias, as shown below:

```java
value = task.getFieldByAlias("Number of Widgets Required");
```

This is not recommended where you are iterating across multiple tasks to
retrieve values: it's more efficient to look up the `FieldType` once before you
start, then use that to retrieve the value you are interested in from each
task.

## Populated Fields
So far we've touched on how to can read and write fields in examples where we
are targeting specific fields. If we're reading a schedule whose contents are
unknown to us, how can we tell which fields are actually populated? A typical
use-case for this might be where we need to read a schedule, then present the
user with the ability to select the columns they'd like to see in a tabular
display of the schedule contents. If you look at the various enumerations we
have mentioned previously in this section (`TaskField`, `ResourceField` and so
on) you can see that there are a large number of possible fields a user could
choose from, so ideally we only want to show a user fields which actually
contain non-default values.

To solve this problem we need to use the appropriate `getPopulatedFields` method
for each of the entities we're interested in.

```java
ProjectFile file = new UniversalProjectReader().read("example.mpp");

Set<ProjectField> projectFields = file.getProjectProperties().getPopulatedFields();
Set<TaskField> taskFields = file.getTasks().getPopulatedFields();
Set<ResourceField> resourceFields = file.getResources().getPopulatedFields();
Set<AssignmentField> assignmentFields = file.getResourceAssignments().getPopulatedFields();
```

In the example above we're opening a sample file, then for each of the main
classes which implement the `FieldContainer` interface, we'll query the
container which holds those classes and call its `getPopulatedFields` method.
In each case this will return a `Set` containing the enumeration values 
representing fields which have non-default values.

If you need to you can retrieve all of this information in one go:

```java
ProjectFile file = new UniversalProjectReader().read("example.mpp");

Set<FieldType> allFields = file.getPopulatedFields();
```

The set returned by the project's `getPopulatedFields` will contain all the
populated fields from all entities which implement the `FieldContainer`
interface. You'll need to remember to look at the `FieldTypeClass` value of
each field in the resulting set to determine which entity the field belongs
to. The following section provides more detail on this.

## User Defined Fields
In an earlier section we touched briefly on how Microsoft Project uses a fixed
set of "custom fields" to allow you to store arbitrary data as part of the
schedule. A more common approach in other applications is to allow you to
create your own fields to represent the data you need to store - that way you
can have exactly the fields you need, without needing to worry if you can fit
your data into the fixed set of custom fields. In fact Microsoft Project also
supports this concept, in the form of Enterprise Custom Fields, although these
are only available if you are working with a schedule hosted in Project Server
(Project 365).

As you can imagine MPXJ can't provide dedicated getter and setter methods for
these fields as it doesn't know ahead of time what they are - they're user
defined! Instead we rely on the `get` and `set` methods to work with these
fields.

When a schedule is read  by MPXJ, each user defined field is represented
internally by an instance of the  `UserDefinedField` class. This class
implements the `FieldType` interface, and so can be used with the `get` and
`set` methods to read and write these values.

You can see which user defined fields exist in a project using
code similar to the example below:

```java
for (UserDefinedField field : project.getUserDefinedFields())
{
    System.out.println("name(): " + field.name());
    System.out.println("getName(): " + field.getName());
    System.out.println("getFieldTypeClass(): " + field.getFieldTypeClass());
    System.out.println("getDataType():" + field.getDataType());         
}
```

As well as using the `getUserDefinedFields` method on the project to see which
fields are defined, the `getPopulatedFields` methods discussed in an earlier
section will also return `UserDefinedField` instances if these fields have
values in the schedule. Information about `UserDefinedField` instances is also
available in the `CustomFieldContainer`. This means that when you read a
schedule and you are expecting certain user defined fields to be present, you
can use the `getFieldTypeByAlias` or `getFieldByAlias` methods to find the
fields you are interested in by name, as described in an earlier section. 

> If you import schedules data from an application which supports user defined
> fields and export to a Microsoft Project file format (MPX or MSPDI), MPXJ will
> automatically map any user defined fields to unused custom fields. Note that as
> there are only a finite number of custom field available, it is possible that
> not all user defined fields will be available when the resulting file is opened
> in Microsoft Project.




