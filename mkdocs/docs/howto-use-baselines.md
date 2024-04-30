# How To: Use Baselines

One tool to assist measuring how a schedule has changed over time is to create a
baseline. A baseline is a snapshot of a schedule taken at a point in time: in
the future you can compare the current state of your schedule to
this snapshot to help understand what has changed.

## Baselines in Scheduling Applications

### Microsoft Project

Microsoft Project captures a baseline by taking copies of a small set of
attributes for tasks, resources and resource assignments. The main attributes
captured for each of these entities are Work, Cost, Duration, Start and Finish.
These attributes can be stored as one of 11 distinct baselines: one called
simply "Baseline", and the rest labelled "Baseline 1" through to "Baseline 10".
If we pick Duration as an example attribute, the baseline value for this
attribute might appear as "Baseline Duration", "Baseline 1 Duration", "Baseline
2 Duration" and so on, depending on which set of baseline attributes you had
chosen to capture your snapshot.

Capturing a baseline in Microsoft Project is as simple as selecting the "Set
Baseline" menu option for the current project you are working with to take a
new baseline.

<p align="center"><img alt="Set Baseline Menu Option" src="/images/howto-use-baselines/set-baseline-menu-option.png" width="25%"/></p>

You'll be prompted to select which baseline you'd like to populate
("Baseline", "Baseline 1", "Baseline 2" and so on), and whether you'd like to
baseline the whole project or just selected tasks.

<p align="center"><img alt="Set Baseline Dialog" src="/images/howto-use-baselines/set-baseline-dialog.png" width="25%"/></p>

When you click OK, the attributes captured for a baseline by Microsoft Project
will be copied to the equivalent baseline attributes.

> Note that the baseline attributes you have captured as part of this process
> have no special properties: they can be edited once they have been
> captured, and no recalculation takes place if they are edited (for example, if
> you change a baseline duration of a task, the baseline finish date of the task
> won't change).

### Primavera P6

The approach taken by Microsoft Project to managing baselines is unusual: most
other scheduling applications take an approach similar to that used by
Primavera P6, which is to take a complete copy of the schedule at the point a
baseline is made, and thus any part of the baseline schedule is available in
future to be compared with the current schedule. Let's look at baselines in P6
in a little more detail.

Baselines are managed in P6 in two stages. The first stage is actually to
capture a baseline, which is achieved by selecting the "Maintain Baselines..."
menu option.

<p align="center"><img alt="P6 Baseline Menu Options" src="/images/howto-use-baselines/p6-baseline-menu-options.png" width="25%"/></p>

The dialog which opens allows you to create, update, or delete a baseline. 

<p align="center"><img alt="P6 Maintain Baselines Dialog" src="/images/howto-use-baselines/p6-maintain-baselines.png" width="75%"/></p>

When a baseline is taken, although it is simply a copy of an existing project,
the baseline itself is not visible as a normal project in P6 so you can't view
or, and more importantly for us, export it. The maintain baselines dialog 
includes the option of "restoring" a baseline, which turns the baseline back
into a normal project which you can view or export.

Once you have a baseline, you then need to "assign" it in order to tell P6 what
you want to do with it by selecting the "Assign Baselines..." menu option we
saw earlier. This menu option will open a dialog similar to the one shown
below.

<p align="center"><img alt="P6 Assign Baselines Dialog" src="/images/howto-use-baselines/p6-assign-baselines-dialog.png" width="75%"/></p>

As you can see from the dialog, P6 recognizes four distinct baseline types:
the Project Baseline, and the Primary, Secondary and Tertiary User Baselines.

> Note that the User Baselines are not visible outside of P6, i.e. information
> about which baselines are assigned as user baselines does not appear in either
> PMXML or XER files exported from P6.

In a similar way to Microsoft Project, P6 allows attributes from these baselines
to be shown alongside information from the current schedule.

<p align="center"><img alt="P6 Columns Dialog" src="/images/howto-use-baselines/p6-columns-dialog.png" width="75%"/></p>

The dialog shown above is being used to select the columns to display in the
tabular part of the Gantt chart in P6. As you can see there four start and
finish attributes show here, corresponding to the Project Baseline (for example
BL Project Start) and the User Baselines (for example, BL1 Start, BL2 Start and
BL3 Start).

There is a "special case" baseline, and a slightly  obscure setting in P6 which
determines exactly which values are shown in these columns. The "special case"
baseline is where "&lt;Current Project&gt;" is selected as the baseline. This
value doesn't represent a baseline which has been taken from the current
project, but instead tells P6 that it should display the "planned" attributes
from the current schedule in any baseline columns the user adds to the Gantt
chart. So for example, if "&lt;Current Project&gt;" has been selected as the
Project Baseline, then the values in the "BL Project Start" column on the Gantt
chart will be the same as the values in the "Planned Start" column.

Where an actual baseline taken from the project has been assigned, then the
values which will be shown in the baseline columns will depend on the "Earned
Value Calculation" setting from the P6 Admin Preference dialog.

<p align="center"><img alt="P6 earned Value Setting" src="/images/howto-use-baselines/p6-earned-value-setting.png" width="75%"/></p>

If the "Earned value calculation setting" is set to "Budgeted values with
planned dates", then the Planned dates from the selected baseline schedule are
used to populate the baseline attributes in the current schedule (for example,
BL Project Start in the current schedule will be populated by the Planned Start
value from the baseline schedule). If the "Earned value calculation setting" is
NOT "Budgeted values with planned dates", then the current dates from the
selected baseline schedule are used as the baseline (for example, BL Project
Start in the current schedule will be populated by the Start value from the
baseline schedule).

> It's important to note that the "Earned Value Calculation" setting is NOT
> available in either PMXML or XER files exported from P6, so when presenting
> schedule data from these files types to an
> end user outside of P6, there is no way to automatically determine which are
> the correct baseline values to be displayed.

### Asta Powerproject

Powerproject takes the same approach as P6 to capturing a baseline - an entire
copy of the schedule is made. This copy can either be held as a separate file,
or can be embedded within the main schedule file (note the "Embed the baseline
in the file" option in the dialog shown below).

<p align="center"><img alt="Asta Baseline Dialog" src="/images/howto-use-baselines/asta-baseline-dialog.png" width="75%"/></p>

MPXJ can read baselines which have been embedded within the main schedule file,
although as you will see in a later section, with a little more work you can
attach a baseline from a separate file to the main schedule.

## Baselines in MPXJ

### Microsoft Project

Using MPXJ to work with baseline data from an MPP or MSPDI file is
straightforward as any baseline values are available directly from the task,
resource and resource assignment classes. The example below illustrates this by
retrieving the Start attribute of a task, and some of the Baseline Start
attributes.

```java
ProjectFile file = new UniversalProjectReader().read("sample.mpp");
Task task = file.getTaskByID(Integer.valueOf(1));

System.out.println("Start: " + task.getStart());
System.out.println("Baseline Start: " + task.getBaselineStart());
System.out.println("Baseline1 Start: " + task.getBaselineStart(1));
System.out.println("Baseline2 Start: " + task.getBaselineStart(2));

// ...

System.out.println("Baseline10 Start: " + task.getBaselineStart(10));
```

When a baseline is taken in Microsoft Project, the date on which the baseline
was taken is recorded. This can be used to indicate which baselines are set,
and when they were set, although this data is only available from MPP file, not
MSPDI files. The example code below illustrates how baseline dates can be
used:

```java
ProjectFile file = new UniversalProjectReader().read("sample.mpp");
ProjectProperties props = file.getProjectProperties();

for (int baselineNumber=0; baselineNumber <= 10; baselineNumber++)
{
   LocalDateTime baselineDate;
   String baselineLabel;

   if (baselineNumber == 0)
   {
      baselineDate = props.getBaselineDate();
      baselineLabel = "Baseline";
   }
   else
   {
      baselineDate = props.getBaselineDate(baselineNumber);
      baselineLabel = "Baseline " + baselineNumber;
   }

   if (baselineDate == null)
   {
      System.out.println(baselineLabel + " not set");
   }
   else
   {
      System.out.println(baselineLabel + " set on " + baselineDate);
   }
}
```

### Primavera P6

Getting baseline data for a P6 schedule is slightly more challenging than for a
Microsoft Project schedule.

The first thing to note is that XER files do not contain  baseline data. XER
files do include the ID of the Project Baseline schedule (via the
`ProjectProperties.getBaselineProjectUniqueID()` method) but nothing more. Note
that if Project Baseline ID value is zero, this indicates that the Project
Baseline selected in P6 is "&lt;Current Project&gt;". If this is the case, MPXJ
will populate the attributes for the first baseline from the planned
attributes (for example a task's Baseline Start will be set to the task's
Planned Start attribute). This matches the behavior of P6 when "&lt;Current
Project&gt;" is selected as the Project Baseline, and it the only time any form
of baseline data will be available when reading an XER file.

The easiest way to get baseline data other than "&lt;Current Project&gt;" for a
P6 schedule is to export a PMXML file. As part of the export process, the
export dialog shows a column allowing baselines to be selected for export:

<p align="center"><img alt="P6 PMXML export" src="/images/howto-use-baselines/p6-pmxml-export.png" width="75%"/></p>

This opens a dialog for you to make the selection. Note that multiple baselines
may be selected for export:

<p align="center"><img alt="P6 PMXML Select Baselines" src="/images/howto-use-baselines/p6-pmxml-select-baseline.png" width="75%"/></p>

When a PMXML file is read all of the projects exported including any baselines
will be available as `ProjectFile` instances from the `readAll` method.

```java
List<ProjectFile> projects = new UniversalProjectReader().readAll("sample-pmxml.xml");
System.out.println("The file contains " + projects.size() + " projects");
```

Alongside providing access to the current schedules and any baselines in the
file, MPXJ will also apply the same "&lt;Current Project&gt;" logic to PMXML
files as to XER files: so if the
`ProjectProperties.getBaselineProjectUniqueID()`
method returns zero, MPXJ will automatically populate the attributes of the
first baseline using values from the equivalent planned attributes (for example
a task's Baseline Start will be set to the task's Planned Start attribute).

Where PMXML files differ from XER files is that if the unique ID of one of the
exported baselines matches the ID of the Project Baseline attribute from another
project, MPXJ will link the two projects and
populate the attributes of the first baseline in the main project using the
equivalent planned attributes from the baseline project.

To link the baseline MPXJ has actually attached the baseline `ProjectFile`
instance to the main schedule using the `ProjectFile.setBaseline()` method. As
well as begin able to work the populated baseline attributes in the main
schedule, you also have direct access to the baseline `ProjectFile` instance,
as illustrated by the sample code below:

```java
ProjectFile project = new UniversalProjectReader().read("sample-pmxml.xml");
ProjectFile baseline = project.getBaseline();

System.out.println("Current project name: " + project.getProjectProperties().getName());
System.out.println("Baseline project name: " + baseline.getProjectProperties().getName());
```

MPXJ's default behavior when attaching a baseline to a project matches the
P6 configuration where the "Earned value calculation" is set
to "Budgeted values with planned dates". As noted earlier this setting is not
available in XER or PMXML files, so there is currently no way to automatically
select the correct approach. You can however change MPXJ's default behavior by
using the `ProjectFile.setBaselineStrategy` method. `BaselineStrategy` is an
interface implemented by classes which will implement the mechanism used to
match equivalent tasks from the main `ProjectFile` instance with tasks from a
baseline `ProjectFile` instance, and then populate the baseline attributes of
the tasks in the main schedule with attributes from the matching tasks in the
baseline schedule.

MPXJ provides baseline strategy implementations matching the two approaches
taken by P6, depending on the "Earned value calculation". The default strategy
is represented by the constant `PrimaveraBaselineStrategy.PLANNED_DATES`, with
the alternative approach represented by the constant
`PrimaveraBaselineStrategy.CURRENT_DATES`. The sample code below illustrates
how this constant is used to alter MPXJ's behavior to match P6 when the earned
value setting is not "Budgeted values with planned dates". 

```java
PrimaveraPMFileReader reader = new PrimaveraPMFileReader();
reader.setBaselineStrategy(PrimaveraBaselineStrategy.CURRENT_DATES);
ProjectFile file = reader.read("sample-pmxml.xml");
```

### Asta Powerproject
When reading an Asta Powerproject schedule MPXJ will read the main schedule, 
and if defined, the current baseline schedule.

> Note that at present MPXJ does not read all available baselines from a
> Powerproject schedule. Feedback welcomed if the ability to read all baselines
> is desirable.

```java
ProjectFile project = new UniversalProjectReader().read("sample.pp");
ProjectFile baseline = project.getBaseline();

System.out.println("Current project name: " + project.getProjectProperties().getName());
System.out.println("Baseline project name: " + baseline.getProjectProperties().getName());
```

## Manually Attaching Baselines

As we saw in the previous section, MPXJ can attach a baseline `ProjectFile` instance 
to the main project, and populate the relevant baseline attribute in the main project.
MPXJ will actually allow you to explicitly attach up to 11 baseline projects to a main project.
Each project attached in this way will be used to populat ethe relevnt baseline attributes.

setbaselinegetbaselines
clearbaseline



TODO: note for MPP and MSPDI we read and populate all baselines, for P6 and asta we only attach the current baseline
