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

If you use MPXJ to read an MPP file that contains a Subproject, initially you
won't see anything different to a file which just contains ordinary tasks: the
Subproject will just appear as a normal summary task whose attributes will roll
up the details from the Subproject. If you want you can just work with the task
as-is, you only need to so something different if you want to work with the
contents of the Subproject.

```java
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Task;
import net.sf.mpxj.reader.UniversalProjectReader;

...

ProjectFile file = new UniversalProjectReader().read("sample.mpp");
for (Task task : file.getTasks())
{
    if (task.getExternalProject())
    {
        System.out.println(task.getName() + " is a subproject");
        System.out.println("The path to the file is: " +
            task.getSubprojectFile());
        System.out.println("The GUID of this project is: " +
            task.getSubprojectGUID());
        System.out.println("The offset used when displaying Unique ID values is: " +
            task.getSubprojectTasksUniqueIDOffset());
    }
}

```

The example above illustrates how we can identity a Subproject using a task's
External Project attribute. Once we have identified that we have a Subproject
we can determine where the file is located, using the Subproject File 
attribute, and the GUID of this project, using the Subproject GUID attribute.

The last attribute we're looking at in this example is the Subprojec Tasks
Unique ID Offset. When Microsoft Project provides a combined view of two or
more MPP files using Subprojects, one issue is that the Unique ID values from
one project may no lnger be unique. To get around this problem Microsoft
Project adds an offset to the Unique ID values of the tasks it displays from
each Subproject to ensure that each one has a distinct value. This is the value
we're retrieving using the `getSubprojectTasksUniqueIDOffset` method.

TODO
opening Subprojects

## External Predecessors

## Resource Pools


