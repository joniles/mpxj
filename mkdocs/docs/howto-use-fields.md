# How To: Use Fields
Once you've read a schedule using MPXJ, and you have a `ProjectFile` instance
with tasks, resources and resource assignments, how do you access the data
represented by the fields provided by each of these entities? If you're
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
How about working with a field that has a type other than a String:

```java
DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
Date startDate = df.parse("10/05/2022");
task.setStart(startDate);

System.out.println("Start date: " + df.format(task.getStart()));
```

here's the output from the sample code:

```
Start date: 10/05/2022
```

We're setting and retrieving the task's start date using a `Date` instance.
For almost all of the fields supported by tasks, resources, and resource
assignments you'll find a pair of getter and setter methods allowing you
to access and modify the field with a convenient type safe interface.

## Field Enumerations
What if we don't know ahead of time which fields we need to access? For example,
what if our application was allowing users to chose which fields to display for
each task? In this case we can use a data-driven approach to read and write
fields, as shown in the example below.

```java
task = file.addTask();
task.set(TaskField.NAME, "Task 2");

name = (String)task.get(TaskField.NAME);
System.out.println("Task name: " + name);

startDate = df.parse("11/05/2022");
task.set(TaskField.START, startDate);

System.out.println("Start date: " + df.format(task.getStart()));
```

Here's the output from this sample code:

```
Task name: Task 2
Start date: 11/05/2022
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
schedules may provide this value for use when we read the data they contain,
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
DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
Date baselineStart = df.parse("01/05/2022");
Date startDate = df.parse("10/05/2022");

// Update task1 using methods
task1.setStart(startDate);
task1.setBaselineStart(baselineStart);

// Update task2 using TaskField enumeration
task2.set(TaskField.START, startDate);
task2.set(TaskField.BASELINE_START, baselineStart);

// Show the variance being retrieved by method and TaskField enumeration
System.out.println("Task 1");
System.out.println("Start Variance from method: " + task1.getStartVariance());
System.out.println("Start Variance from get: " + task1.get(TaskField.START_VARIANCE));
System.out.println();

System.out.println("Task 2");
System.out.println("Start Variance from method: " + task2.getStartVariance());
System.out.println("Start Variance from get: " + task2.get(TaskField.START_VARIANCE));
System.out.println();
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
call to `get(TaskField.START_VARIANCE` trigger the calculation and produce the
same Start Variance value.

Rather than immediately discarding the Start Variance value we've just
calculated, this value is cached as part of the task, and will be returned next
time we use the `getStartVariance` method or we call
`get(TaskField.START_VARIANCE`.


## Cached Values
The `getCachedValue` method allows us to retrieve a field _without attempting to
calculate a value_. Let's take a look at what happens with the example we
were working with in the previous section. If we use the `getCachedValue`
method to attempt to retrieve the Start Variance for each task, we should get
the same result we saw when we used the `getStartVariance` method and
the `get(TaskField.START_VARIANCE)` call:


```java
System.out.println("Task1 Start Variance: " + task1.getCachedValue(TaskField.START_VARIANCE));
System.out.println("Task2 Start Variance: " + task2.getCachedValue(TaskField.START_VARIANCE));
```

Here's the output from running this code:

```
Task1 Start Variance cached value: 6.0d
Task2 Start Variance cached value: 6.0d
```

This is what we'd expect as we have already calculated the Start Variance
value for both of these tasks, and this calculate value is cached.

What if we clear this cached value? We can
do this in two ways: we can either change one of the values the calculation
depends on, or we can explicitly set the Start Variance to `null`.

```java
/// Clear the cached start variance by updating one of its dependencies
baselineStart = df.parse("03/05/2022");
task1.setBaselineStart(baselineStart);

// Explicitly clear the start variance
task2.setStartVariance(null);
```

Now if we repeat out calls to `getCachedValue`:

```java
System.out.println("Task1 Start Variance: " + task1.getCachedValue(TaskField.START_VARIANCE));
System.out.println("Task2 Start Variance: " + task2.getCachedValue(TaskField.START_VARIANCE));
```

We see that we are returned `null` in both cases as there is no longer a 
cached value, and `getCachedValue` will never attempt to calculate a
value:

```
Task1 Start Variance: null
Task2 Start Variance: null
```

Finally, just to come full circle, we can use the `getStartVariance` method to
retrieve the Start Variance values, which forces them to be recalculated:

```java
System.out.println("Task1 Start Variance: " + task1.getCachedValue(TaskField.START_VARIANCE));
System.out.println("Task2 Start Variance: " + task2.getCachedValue(TaskField.START_VARIANCE));
```

Here's the output from running this code, note that for Task 1 we changed the
baseline start, so now the start variance is only five days rather than the 
six days it was before:

```
Task1 Start Variance from method: 5.0d
Task2 Start Variance from method: 6.0d
```

Last of all, if we used the `getCachedValue` method we'll see that as a result
of the calculation being carried out, we now have cached values available:

```java
System.out.println("Task1 Start Variance cached value: " + task1.getCachedValue(TaskField.START_VARIANCE));
System.out.println("Task2 Start Variance cached value: " + task2.getCachedValue(TaskField.START_VARIANCE));
```

Here's the output from running this code:

```
Task1 Start Variance cached value: 5.0d
Task2 Start Variance cached value: 6.0d
```

In summary, `getCachedValue` will never attempt to calculate values for fields
which are not already present. This can be useful if you want to read a
schedule using MPXJ, but retrieve only the fields which were in the original
schedule, not calculated or inferred by MPXJ.

## Indexed Fields
So far we've seen how simple fields like Name and Start can be accessed
and modified using both field-specific and generic methods. Many organisations
rely on using general purpose fields like "Text 1" and "Date 1" to store
information relevant to their schedules. If we look for methods like `setText1`
or `setDate1` we won't find them, so how can we work with these fields?

The answer is quite straightforward, for each of these "indexed" fields you'll
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

For `Task`, `Resource` and `ResourceAssignment` the following indexed fields are
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

## Populated Fields
So far we've touched on how to can read and write fields in examples where we
are targeting specific fields. If we're reading a schedule whose contents are
unknown to us, how can we tell which fields are actually populated? A typical
use-case for this might be where we need to read a schedule, then present the
user with the ability to select the columns to appear in a tabular display of
the schedule contents. If you look at the various enumerations which have
mentioned previously in this section (`TaskField`, `ResourceField` and so on)
you can see that there are a large number of possible fields a user could
choose from, so ideally we only want to show a user field which actually
contain non-default values.

To solve this problem we need to use the appropriate `getPopulatedFields` method for
each of the entities we're interested in.

```java
ProjectFile file = new UniversalProjectReader().read("example.mpp");

Set<ProjectField> populatedProjectFields = file.getProjectProperties().getPopulatedFields();
Set<TaskField> populatedTaskFields = file.getTasks().getPopulatedFields();
Set<ResourceField> populatedResourceFields = file.getResources().getPopulatedFields();
Set<AssignmentField> populatedAssignmentFields = file.getResourceAssignments().getPopulatedFields();
```

In the example above we're opening a sample file, then for each of the main
classes which implement the `FieldContainer` interface, we'll query the
container which holds those classes and call its `getPopulatedFields` method.
In each case this will return a `Set` containing the enumeration values 
representing fields which have non-default values.

## FieldType
Earlier in this section we noted that there were four main enumerations
representing the fields which particular classes can contain.

* `ProjectField`
* `ResourceField`
* `TaskField`
* `AssignmentField`

What I didn't mention earlier is that each of these enumerations inherits from a
common `FieldType` interface which defines a common set of methods each of
these enumerations must implement. The most interesting of these methods are:

* `name()`
* `getName()`
* `getFieldTypeClass()`
* `getDataType()`

The `name()` method retrieves the name of the enumeration value exactly as it
appears in the code. The `getName()` method returns a localized version of the
name, suitable for display to end users (currently English is the default and
only supported locale).

The `getFieldTypeClass()` method returns a value from the `FieldTypeClass`
enumeration which will help you to determine which class this `FieldType`
belongs to. Finally the `getDataType()` method will return a value from the
`DataType` enumeration which indicates the data type you will receive from the
`get` method when accessing this field, and the type to pass to the `set`
method when updating the field.


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
to determine how we format the the selected value for display.

```java
private String getStringValue(FieldContainer container, FieldType type)
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
            result = new DecimalFormat("£0.00").format((Number)value);
            break;
        }

        case DATE:
        {
            result = new SimpleDateFormat("dd/MM/yyyy").format((Date)value);
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
creating new instances of `DecimalFormat` and `SimpleDateFormat` each time you
need to format a value!)


custom fields
enterprise fields (enterprise_text1 etc)
enterprise custom fields
