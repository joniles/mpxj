# How To: Use Baselines

One tool for measuring how a schedule has changed over time is to create a 
baseline. A baseline allows a snapshot of a schedule to be taken at a point in
time, then in future you can compare the current state of your schedule to the
baseline you've captured to see what's different.

MPXJ supports reading baseline data from schedules created by Microsoft Project,
Primavera P6, Asta Powerproject, Concept Draw PROJECT, Fasttrack, and Phoenix.
There are two distinct approaches taken by these applications to manage
baselines and these are typified by Microsoft Project and P6.

Microsoft Project supports capturing 11 distinct baselines: one called
simply "Baseline", and the rest labelled "Baseline 1" through to "Baseline 10".
In Microsoft Project you can select a menu option for the
current project you are working with to take a new baseline. This copies the
current state of your project into one of these baselines: you'll be given a
choice of which baseline you would like to use at this point.

Once you have populated a baseline you will have captured copies of attributes
like Start, Finish, Duration, Fixed Cost, Fixed Cost Accrual and so on. 

XXX explain better what's happening heer!

The
exact set of attributes captured depend on which entity you are looking at
(task, resource, resource assignment and so on). As you can see we have
captured details of key elements of the current state of the schedule, rather
than a copy of the entire schedule itself, but this is usually enough to
understand how the schedule has changed.

The baseline attributes you have captured as part of this
process have no special properties: by default they can be edited once they
have been captured, and no recalculation takes place if they are edited
(for example, if you change a baseline duration of a task, the baseline finish
date of the task won't change).

Primavera P6 takes a different approach: instead of capturing a subset of
the attributes from you current schedule and storing

which baseline is the current baselne for P6
what is automatically captured for p6