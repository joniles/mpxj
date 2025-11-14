# How To: Use Project Context

These notes provide a brief description of the `ProjectContext` class' purpose.

For many people working with MPXJ, the main concern is reading a single project
and working with the data it contains. In fact most of the file formats
supported by MPXJ can only contain a single project. In this case you will just
need to work with a `ProjectFile` instance, which provides access to all of the
components of the project, as illustrated by the simplified diagram below:


```mermaid
graph LR
    ProjectFile(ProjectFile)
    Configuration(Configuration)
    Properties(Properties)
    CodeDefinitions(Code Definitions)
    FieldDefinitions(Field Definitions)
    Calendars(Calendars)
    Resources(Resources)
    Tasks(Tasks)
    Relations(Relations)
    ResourceAssignments(Resource Assignments) 
    ProjectFile --> Configuration
    ProjectFile --> Properties
    ProjectFile --> CodeDefinitions
    ProjectFile --> FieldDefinitions
    ProjectFile --> Calendars
    ProjectFile --> Resources
    ProjectFile --> Tasks
    ProjectFile --> Relations
    ProjectFile --> ResourceAssignments
```


For applications and file formats which support multiple projects (at
the time of writing Primavera P6 databases, XER files and PMXML files) the
you can still use a `ProjectFile` instance and work with the data shown above,
however behind the scenes the data you are working with is actually stored 
differently, as the simplified diagram below illustrates:


```mermaid
graph LR
    ProjectContext(ProjectContext)
    CodeDefinitions(Code Definitions)
    FieldDefinitions(Field Definitions)
    Calendars(Calendars)
    Projects(Projects)
    Resources(Resources)
    Configuration(Configuration)
    ProjectFiles(ProjectFile)
    Properties(Properties)
    Tasks(Tasks)
    Relations(Relations)
    ResourceAssignments(Resource Assignments)
    ProjectContext --> CodeDefinitions
    ProjectContext --> FieldDefinitions
    ProjectContext --> Calendars
    ProjectContext --> Projects
    ProjectContext --> Resources
    ProjectContext --> Configuration
    Projects --> ProjectFiles
    ProjectFiles --> Properties
    ProjectFiles --> Tasks
    ProjectFiles --> Relations
    ProjectFiles --> ResourceAssignments
```

The `ProjectContext` class is used as a container for all common data shared
across projects (calendars, resources, code definitions and so on). Most of the
time you don't need to be aware of the `ProjectContext` class, but it is useful
to understand that when reading, for example, an XER file containing multiple
projects, all the resources, calendars and so on are actually stored as part of
the `ProjectContext`, even if you retrieve that data via a `ProjectFile`
instance.
