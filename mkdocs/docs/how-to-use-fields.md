# How To: Use Fields
Once you've read a schedule using MPXJ, and you have a `ProjectFile` instance
with tasks, resources and resource assignments, how do you access the data
represented by the fields provided by each of these entities? This section
explains the different approaches you can take.

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
System.out.println("Task name:" + name);
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

Here we're setting and retrieving the task's start date using a `Date` instance.
For almost all of the fields supported by tasks, resources, and resource
assignments you'll find a pair of getter and setter methods allowing you
to access and modify the field with a convenient type safe interface.

What if we don't know ahead of time which fields we need to access? In this
case we can use a data-driven approach to read and write fields, as shown in
the example below.

```java
```


field enumerations
field containers
calculated values, cached values

"indexed" values
custom fields
enterprise custom fields
