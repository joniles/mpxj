# How To: Use Baselines

One tool for measuring how a schedule has changed over time is to create a
baseline. A baseline is a snapshot of a schedule taken at a point in time: at
some point in the future you can compare the current state of your schedule to
this snapshot as a way to understand what has changed.

Microsoft Project captures a baseline by taking copies of a small set of
attributes for tasks, resources and resource assignments. The main attributes
captured for each of these entities are Work, Cost, Duration, Start and Finish.
These attributes can be stored as one of 11 distinct baselines: one called
simply "Baseline", and the rest labelled "Baseline 1" through to "Baseline 10".
If we pick Duration as an example attribute, the baseline value for this
attribute might appear as "Baseline Duration", "Baseline 1 Duration", "Baseline
2 Duration" and so on, depending on which set of baseline attributes you had
chosen to capture your snapshot.

Capturing a baseline in Microsoft Project is as simple as selecting a menu
option for the current project you are working with to take a new baseline.
You'll be prompted to select which baseline you'd like to populate
("Baseline", "Baseline 1", "Baseline 2" and so on) then the relevant attributes
will be copied across to the set of baseline attributes you've selected.

The baseline attributes you have captured as part of this process have no
special properties: by default they can be edited once they have been captured,
and no recalculation takes place if they are edited (for example, if you change
a baseline duration of a task, the baseline finish date of the task won't
change).



Primavera P6 takes a different approach: instead of capturing a subset of
the attributes from you current schedule and storing

which baseline is the current baselne for P6
what is automatically captured for p6