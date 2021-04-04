# TODO
Can't currently identify how MPP14 files are storing the "recurring" flag.
This flag is set to true on the recurring task itself, and also on the child tasks
which represent the expansion of the recurrence.

It is possible to add non-recurring tasks as children of the parent recurring task
and hence you can see a mixture of true and false values for the flag underneath the
parent recurring task.

For other MPP file types the recurring flag is easily identifiable.