# How To: Use Baselines

One tool to assist measuring how a schedule has changed over time is to create a
baseline. A baseline is a snapshot of a schedule taken at a point in time: in
the future you can compare the current state of your schedule to
this snapshot to help understand what has changed.

## Baselines in Microsoft Project

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

When you click OK, the attributes captured as a baseline by Microsoft Project
will be copied to the equivalent baseline attributes.

> Note that the baseline attributes you have captured as part of this process
> have no special properties: by default they can be edited once they have been
> captured, and no recalculation takes place if they are edited (for example, if
> you change a baseline duration of a task, the baseline finish date of the task
> won't change).

## Baselines in Primavera P6

The approach taken by Microsoft Project to managing baselines is unusual: most
other scheduling applications take the approach used by Primavera P6, which is
to take a complete copy of the schedule at the point a baseline is made, and
thus any part of the baseline schedule is available in future to be compared
with the current schedule.


which baseline is the current baselne for P6
what is automatically captured for p6