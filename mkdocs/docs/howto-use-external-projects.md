# How To: Use External Projects
From a schedule in Microsoft Project you can work with data from other project
files in three ways: Subprojects, External Predecessors, and Resource Pools.

## Subprojects
Microsoft Project allows you to manage larger projects by breaking them down
into Subprojects. From one MPP file, a link can be added to another MPP file
forming a parent-child relationship. The child MPP file will appear as a
summary task in the location you've selected within the parent file. When this
summary task is expanded the tasks from the child MPP file will appear
seamlessly as tasks in the parent file.

### Identifying Subproject Tasks
If you use MPXJ to read an MPP file that contains a Subproject, initially you
won't see anything different to a file which just contains ordinary tasks: the
Subproject will just appear as a normal summary task whose attributes will roll
up the details from the Subproject. If you want you can just work with the task
as-is, you only need to so something different if you want to work with the
contents of the Subproject.

```java
package org.mpxj.howto.use.externalprojects;

import org.mpxj.ProjectFile;
import org.mpxj.Task;
import org.mpxj.reader.UniversalProjectReader;

public class IdentifySubprojectTasks
{
   public void process() throws Exception
   {
      ProjectFile file = new UniversalProjectReader().read("sample.mpp");
      for (Task task : file.getTasks())
      {
         if (task.getExternalProject())
         {
            System.out.println(task.getName() + " is a subproject");
            System.out.println("The path to the file is: "
               + task.getSubprojectFile());
            System.out.println("The GUID of this project is: "
               + task.getSubprojectGUID());
            System.out.println("The offset used when displaying Unique ID values is: "
               + task.getSubprojectTasksUniqueIDOffset());
         }
      }
   }
}
```

The example above illustrates how we can identify a Subproject by using a task's
External Project attribute. Once we have identified that we have a Subproject
we can determine where the file is located, using the Subproject File 
attribute, and the GUID of this project, using the Subproject GUID attribute.

The last attribute we're looking at in this example is the Subproject Tasks
Unique ID Offset. When Microsoft Project provides a combined view of two or
more MPP files using Subprojects, one issue is that the Unique ID values in
each project will no longer be unique. To get around this problem Microsoft
Project adds an offset to the Unique ID values of the tasks it displays from
each Subproject to ensure that each one has a distinct value. This offset is
the value we're retrieving using the `getSubprojectTasksUniqueIDOffset`
method.

### Reading Subproject Data
If you wish, you can use `UniversalProjectReader` directly to load the
external project, as the example below illustrates:

```java
package org.mpxj.howto.use.externalprojects;

import org.mpxj.ProjectFile;
import org.mpxj.Task;
import org.mpxj.reader.UniversalProjectReader;

public class ReadSubprojectData
{
   public void process() throws Exception
   {
      ProjectFile file = new UniversalProjectReader().read("sample.mpp");
      Task externalProject = file.getTaskByID(Integer.valueOf(1));
      String filePath = externalProject.getSubprojectFile();
      ProjectFile externalProjectFile = new UniversalProjectReader().read(filePath);
   }
}
```

The code above assumes that the file is located on a readable filesystem at
the exact path specified by the Subproject File attribute.

> Note that these examples assume that the file is on a filesystem
> which is directly readable. For MPP files exported from Project Server,
> it is likely that the path to an external project will be in the form
> `<>\FileName` which represents a project hosted by Project Server.
> MPXJ cannot open this type of external project.

An alternative to writing your own code to do this would be to use the method
provided by MPXJ, as illustrated below:

```java
package org.mpxj.howto.use.externalprojects;

import org.mpxj.ProjectFile;
import org.mpxj.Task;
import org.mpxj.reader.UniversalProjectReader;

public class ReadSubprojectDataMpxj
{
   public void process() throws Exception
   {
      ProjectFile file = new UniversalProjectReader().read("sample.mpp");
      Task externalProjectTask = file.getTaskByID(Integer.valueOf(1));
      ProjectFile externalProjectFile = externalProjectTask.getSubprojectObject();
   }
}
```

The advantage of this approach, apart from using less code, is that MPXJ will
attempt to find the file in locations other than the full path provided
in Subproject File. By default the other place MPXJ will look is in the
working directory of the current process, however this behaviour can be
configured as the example below illustrates:

```java
package org.mpxj.howto.use.externalprojects;

import org.mpxj.ProjectFile;
import org.mpxj.Task;
import org.mpxj.reader.UniversalProjectReader;

import java.io.File;

public class ReadSubprojectDataDirectory
{
   public void process() throws Exception
   {
      ProjectFile file = new UniversalProjectReader().read("sample.mpp");
      file.getProjectConfig().setSubprojectWorkingDirectory(new File("/path/to/directory"));
      Task externalProjectTask = file.getTaskByID(Integer.valueOf(1));
      ProjectFile externalProjectFile = externalProjectTask.getSubprojectObject();
   }
}
```

In the code above we're calling the `setSubprojectWorkingDirectory` method
to give MPXJ details of a directory to look in when attempting to read
an external project.

Note that if MPXJ can't load the external project for any reason, the
`getSubprojectObject` method will return `null`.

### Expanding Subproject Data
In Microsoft Project, when a Subproject task is expanded it behaves just
like any other summary task by revealing the child tasks it contains. We
can reproduce this behavior using the code shown in the sample below:

```java
package org.mpxj.howto.use.externalprojects;

import org.mpxj.ProjectFile;
import org.mpxj.Task;
import org.mpxj.reader.UniversalProjectReader;

public class ExpandSubprojects
{
   public void process() throws Exception
   {
      ProjectFile file = new UniversalProjectReader().read("sample.mpp");
      Task externalProjectTask = file.getTaskByID(Integer.valueOf(1));
      System.out.println("Task has child tasks: " + externalProjectTask.hasChildTasks());
      externalProjectTask.expandSubproject();
      System.out.println("Task has child tasks: " + externalProjectTask.hasChildTasks());
   }
}
```

The `expandSubproject` method attempts to open the external project, and if
successful attaches the tasks from the external project as children of the
external project task. You are then able to access the tasks from the parent
project along with the tasks from the external project as part of the same MPXJ
ProjectFile instance.

> Note that when using the `expandSubproject` method, the
> `setSubprojectWorkingDirectory` method on `ProjectConfig` can be 
> used to tell MPXJ where to find the external projects in the same way
> we did when using the `getSubprojectObject` method.

You can also do this globally and expand all Subproject tasks in a project
by using the `expandSubprojects` method on the project file itself (we'll
cover the `false` argument we're passing into this method in the section
below on External Predecessors):

```java
package org.mpxj.howto.use.externalprojects;

import org.mpxj.ProjectFile;
import org.mpxj.reader.UniversalProjectReader;

public class ExpandSubprojectsGlobally
{
   public void process() throws Exception
   {
      ProjectFile file = new UniversalProjectReader().read("sample.mpp");
      file.expandSubprojects(false);
   }
}
```

> Remember that all the "expand subproject" functionality described in the
> notes above is doing is attaching the tasks from one `ProjectFile` instance
> as child tasks of a task in another `ProjectFile` instance. This will allow
> you to recursively descend through the tasks in a project and any subprojects.
> However, these tasks still belong to separate `ProjectFile` instances,
> so calling the `getTasks()` method on the top level `ProjectFile` instance
> will only return the tasks from that project, and will not include tasks
> from any subprojects.

## External Predecessors
The second way an external project can be referenced in a Microsoft Project
schedule is through the use of an external predecessor task. Project allows you
to enter the task ID for a predecessor in the form `myproject.mpp\123` which
selects the task with ID `123` in `myproject.mpp` as the predecessor
of the task in the schedule you are working on.

When you use an external predecessor task like this, Project includes
a "placeholder" task in your current schedule which represents the task in the
external project and has a copy of all of the relevant attributes of the task
from the external project. In many cases this placeholder task is all you need
to work with the schedule.

When you are working with MPXJ, how can you identify that you are looking
at a placeholder task representing an external predecessor? The sample
code below illustrates this:

```java
package org.mpxj.howto.use.externalprojects;

import org.mpxj.ProjectFile;
import org.mpxj.Task;
import org.mpxj.reader.UniversalProjectReader;

public class ExternalPredecessors
{
   public void process() throws Exception
   {
      ProjectFile file = new UniversalProjectReader().read("sample.mpp");
      for (Task task : file.getTasks())
      {
         if (task.getExternalTask())
         {
            System.out.println(task.getName() + " is an external predecessor");
            System.out.println("The path to the file containing this task is: "
               + task.getSubprojectFile());
            System.out.println("The ID of the task in this file is: "
               + task.getSubprojectTaskID());
            System.out.println("The Unique ID of the task in this file is: "
               + task.getSubprojectTaskUniqueID());
         }
      }
   }
}
```

As the code above illustrates, if the `getExternalTask` method return true, the
task is an external predecessor. As illustrated by the code there are three
relevant attributes: Subproject File (the location of the external project this
predecessor belongs to), and the Subproject Task ID and Subproject Task Unique
ID which are the ID and Unique ID of the task in the schedule it comes from.

As with a task representing an external project, you can retrieve the project
for an external predecessor task using the `getSubprojectObject` method. Note
however that the `expandSubproject` method will have no effect as the external
predecessor task does not represent an entire project!

## Predecessors and Successors from Subprojects
As we saw in a previous section, when working with Microsoft Project you can
configure a project with a number of subprojects. When this is the case you can
also create predecessor or successor relationships  between tasks in any
of these projects. When you open your MPP file in Microsoft Project, and all of
the subprojects can also be opened, then Microsoft Project will present you
with a unified view of the tasks and their relationships, even though the
relationships cross different files. However, if you open your project but do
not have the subproject files available, you will see placeholder external
tasks representing the predecessor or successor tasks from the missing
subproject files.

When reading the file using MPXJ, you will encounter the same situation: opening
your MPP file without any of the subprojects being available you will see
placeholder external tasks for predecessor and successor tasks from the
subproject files. As we have already seen, the `expandSubprojects` method can
be used to expand all subprojects, if the files they represent are available,
allowing you to traverse the hierarchy of tasks. The `expandSubprojects` method
also offers some additional functionality: when you pass `true` to this method,
MPXJ will attempt to replace any predecessor or successor relationships which
include placeholder external tasks with relationships which refer to the
original task from a subproject, and those placeholder external tasks will be
removed from the project entirely. This functionality is intended to replicate
what you would see if you opened your file in Microsoft Project and all
subprojects were successfully loaded.

> As noted previously, the `expandSubprojects` method is only stitching together
> a set of individual `ProjectFile` instances so the tasks they contain can
> be traversed seamlessly, and in this case the predecessor and successor
> relationships between those tasks no longer use placeholder external tasks.
> This is still not a single unified `ProjectFile` instance so care should be
> taken when working with this data to bear in mind that it comes from a number
> of separate files.


## Resource Pools
The final way an external project can be used from a Microsoft Project schedule
is as a resource pool. A resource pool schedule allows you to capture details
of all of your organisation's resources in one place, then refer to them from
multiple schedules. Setting up a resource pool like this should ensure that
your resource utilisation across different projects is accurately captured as
the utilisation detail in the resource pool is updated by the projects using
those resources.

The full path for a project's resource pool can be retrieved using the
`getResourcePoolFile` method as illustrated below:

```java
package org.mpxj.howto.use.externalprojects;

import org.mpxj.ProjectFile;
import org.mpxj.reader.UniversalProjectReader;

public class ResourcePool
{
   public void process() throws Exception
   {
      ProjectFile file = new UniversalProjectReader().read("sample.mpp");
      String path = file.getProjectProperties().getResourcePoolFile();
   }
}
```

In a similar manner to the other external project examples given in previous
sections, MPXJ can also open and read the resource pool file for you:

```java
package org.mpxj.howto.use.externalprojects;

import org.mpxj.ProjectFile;
import org.mpxj.reader.UniversalProjectReader;

public class ResourcePoolObject
{
   public void process() throws Exception
   {
      ProjectFile file = new UniversalProjectReader().read("sample.mpp");
      ProjectFile resourcePool = file.getProjectProperties().getResourcePoolObject();
   }
}
```

## MSPDI Files
Much of the detail noted above is also applicable to MSPDI files, but with the
following exceptions:

* Where the MSPDI file contains a Subproject, only the Subproject File attribute
  will be populated, the Subproject GUID add Subproject Tasks Unique ID Offset
  will not be available.
* If an MSPDI file has been saved in Microsoft Project from an MPP file which
  contains Subprojects, and those Subprojects were expanded at the point the
  file was exported, the MSPDI file will actually contain the data for the
  Subproject as well as the main project. MPXJ will automatically read this
  data, which you can access using the `getSubprojectObject`
  method on the task, or you can call the `expandSubproject` or
  `expandSubprojects` methods documented in the previous sections to show the
  tasks contained in the Subproject as part of the main project.
* Where the original MPP file contained an external task predecessor, the
  equivalent MSPDI file will not contain a placeholder task for the predecessor.
  MPXJ will generate one for you, but this will contain none of the attributes
  you would find if you read the MPP file using MPXJ.
* MSPDI files do not contain any references to resource pools.

> Note that although Microsoft Project will write external predecessor
> information to an MSPDI file, it will fail to load these correctly when
> the MSPDI file is reopened.
