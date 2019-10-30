# Changelog

## 7.9.4 (git master)
* Ensure attribute names are valid when exporting JSON.
* Improve handling of custom field lookup values (Based on a contribution by Nick Darlington).
* Fix an issue when copying a calendar which has exceptions defined.

## 7.9.3 (10/09/2019)
* Add support for reading task early finish and late finish attributes from Asta PP files.
* Ensure XER files containing secondary constraints can be read correctly.
* Preserve calendar IDs when reading from XER files and P6 database (Based on a contribution by forenpm).
* Ensure base calendars are read correctly for P6 schedules.
* Ensure MPP files with unexpected auto filter definition data are handled gracefully.
* Preserve leveling delay format when reading tasks from MSPDI files.
* Ensure unexpected structure of timephased data is handled gracefully when reading MPP files.

## 7.9.2 (19/08/2019)
* Add support for reading and writing secondary constraints from P6 schedules (Based on a contribution by Sruthi-Ganesh)
* Improve support for Synchro SP files containing blank tasks.
* Make constraint type mapping consistent when reading and writing PMXML files.
* Improve handling of leveling delay units and actual duration units (Based in a contribution by Daniel Schmidt).
* Improve handling of certain types of malformed MPP files.
* Improve handling of certain types of malformed SDEF files.
* Map P6 Equipment resource type to cost rather than work (Contributed by forenpm)
* Improve handling of certain MPP files containing large numbers of blank tasks.
* Improve handling of certain MPX files containing trailing delimiters.

## 7.9.1 (01/07/2019)
* Set task start, finish and percent complete when reading SDEF files.

## 7.9.0 (01/07/2019)
* Add support for reading SDEF files.

## 7.8.4 (27/06/2019)
* Add support for reading data links (linked fields) configuration from MPP files.
* Updated to avoid an infinite loop when processing certain corrupt files (Contributed by ninthwaveltd).
* Update MSPDI generation to ensure MS Project correctly recognises complete tasks without resource assignments.
* Ensure that activity codes are read for P6 schedules.
* Improve support for reading custom field values derived from custom field lookup tables in MPP files.
* Improve support for MPP files written with the June 2019 update of Microsoft Project.

## 7.8.3 (24/05/2019)
* Improve handling of task baseline start, start, baseline finish, finish and slack fields read from FTS files.

## 7.8.2 (19/05/2019)
* Improve handling of MPP files with missing Props.
* Improve handling of custom field lookup tables for MPP12 files.
* Correctly write activity duration type to a PMXML file (Contributed by Sebastian Stock)
* Improve handling of Activity Type and Activity ID when writing PMXML files (Based on a contribution by Sebastian Stock)
* Update PMXML file reader for greater consistency with XER and P6 database readers (Activity ID, Activity Type, Status, and Primary Resource ID)
* Improve handling of certain FTS files.
* Improve handling of task notes from MPP8 files.
* More accurately read predecessors and successors from Asta PP files (Based on a contribution by Dave McKay)
* When a schedule is read from P6, P3, or SureTrak, Task.getSummary will return true only if a task is part of the WBS
* Improve support for reading the Synchro Scheduler 2018 SP files.
* Added Task.hasChildTasks() method.
* Fixed [Issue 330](https://sourceforge.net/p/mpxj/bugs/330): Splits data coming in as null for all tasks

## 7.8.1 (13/02/2019)
* Improve support for reading the Synchro Scheduler 2018 SP files.
* Add support for reading Gantt Designer GNT files.
* Improve handling of non-standard MSPDI files.
* Improve handling of non-standard GanttProject files.
* Update MSPDI generation to ensure MS Project correctly recognises complete milestones without resource assignments.
* Improve support for reading user defined fields from PMXML files.
* Ignore hammock tasks when reading PP files.

## 7.8.0 (18/01/2019)
* Added support for reading and writing GUIDs for Tasks, Resources, and Assignments in MSPDI files.
* Updated Java build to use Maven
* Updated to provide a general performance improvement (Based on a contribution by Tiago de Mello)
* Updated to fix an issue when the Microsoft JDBC driver is used to access a P6 database in SQL Server 2005
* Fixed [Issue 332](https://sourceforge.net/p/mpxj/bugs/332): Asta lag sign incorrect (Based on a contribution by Dave McKay)
* Fixed [Issue 333](https://sourceforge.net/p/mpxj/bugs/333): Asta constraints lost (Contributed by Dave McKay)
* Fixed [Issue 335](https://sourceforge.net/p/mpxj/bugs/335): MSDPI into Asta doesn't import Calendar exceptions (Contributed by Dave McKay)

## 7.7.1 (23/10/2018)
* Read additional schedule options from XER files. (Contributed by forenpm)
* Improve handling of some types of MPP file with missing resource assignment data.
* Ensure that resource assignment flag fields are read correctly for all MPP file types (Based on a contribution by Vadim Gerya).
* Ensure that timephased actual work is handled correctly for material resources (Contributed by Vadim Gerya).
* Improve accuracy when reading resource type from MPP files.
* Improve compatibility of generated MSPDI files with Asta Powerproject (Contributed by Dave McKay).

## 7.7.0 (12/10/2018)
* Add support for reading the Synchro Scheduler SP files.
* Add support for reading the activity code (ID) from Asta files.
* When reading a Phoenix file, set the project's status date to the data date from the storepoint.
* Handle MSPDI files with timephased assignments that don't specify a start and end date.

## 7.6.3 (04/10/2018)
* Add support for reading Remaining Early Start and Remaining Early Finish task attributes from P6. (Contributed by forenpm)
* Add support for reading Retained Logic and Progressive Override project attributes from P6. (Contributed by forenpm)
* Fix incorrect sign when calculating start and finish slack (Contributed by Brian Leach).
* Correctly read predecessors and successors from Phoenix files.

## 7.6.2 (30/08/2018)
* Add support for nvarchar columns when reading from a P6 database.
* Updated to correctly read percent lag durations from MSPDI files (based on a contribution by Lord Helmchen).
* Updated the data type for the ValueGUID tag in an MSPDI file (based on a contribution by Lord Helmchen).

## 7.6.1 (29/08/2018)
* Improve handling of MPP files where MPXJ is unable to read the filter definitions.
* Improve handling of SureTrak projects without a WBS.
* Improve handling of SureTrak and P3 WBS extraction.
* Handle unsupported ProjectLibre POD files more gracefully.
* Improve detection of non MS Project compound OLE documents.
* Gracefully handle XER files which contain no projects.

## 7.6.0 (13/07/2018)
* Added support for reading ConceptDraw PROJECT CDPX, CPDZ and CPDTZ files.
* Add support for reading the export_flag attribute from XER files. (Contributed by forenpm)
* Use correct licence details in Maven pom.xml (contributed by Mark Atwood).
* Improve UniversalProjectReader's handling of XER files containing multiple projects.

## 7.5.0 (19/06/2018)
* Added support for reading activity codes from P6 databases, XER files, and PMXML files.
* Added support for reading user defined values from a P6 database.
* Added support for PRX files which contain a SureTrak database.
* Added support for reading the resource "enterprise" attribute from MPP12 and MPP14 files.
* Improve performance when reading user defined values from XER files.
* Improved support for older Primavera PMXML files.
* Updated to rtfparserkit 1.11.0 for improved RTF parsing.

## 7.4.4 (06/06/2018)
* Improve handling of calendar exceptions in MPX files.
* Improve handling of MPP files with large numbers of null tasks.
* Improve robustness when reading timephased data.
* Correctly sort Primavera schedules containing WBS entries with no child activities.

## 7.4.3 (25/05/2018)
* Add support for reading the resource "generic" attribute from MPP files.
* Add a Unique ID attribute to the Relation class and populate for schedule types which support this concept.
* Store the Primavera Project ID as Unique ID in the project properties.
* Update MerlinReader to ensure support for Merlin Project Pro 5.

## 7.4.2 (30/04/2018)
* Gracefully handle malformed duration values in MSPDI files.
* Gracefully handle unexpected calendar exception data structure in certain MPP files.
* Improve handling of certain unusual MPP12 files.
* More work to gracefully handle POI issue 61677, allowing affected MPP files to be read successfully.

## 7.4.1 (16/04/2018)
* Add methods to list projects available in P3 and SureTrak database directories.
* Avoid NPE when a work pattern can't be located in an Asta Powerproject PP file.
* Avoid array bounds exception when reading certain PRX files.
* Read outline code value lists from MPP9 files.
* Handle SureTrak projects without a WBS.

## 7.4.0 (23/03/2018)
* Added support for reading Primavera SureTrak databases from directories, zip files, and STX files.
* Added support for PP files generated by Asta Powerproject from version 13.0.0.1

## 7.3.0 (12/03/2018)
* Added support for reading Primavera P3 databases from directories, zip files, and PRX files.
* Improve robustness when reading MPP files containing apparently invalid custom field data.
* Improve UniversalProjectReader byte order mark handling.
* Fixed [Issue 324](https://sourceforge.net/p/mpxj/bugs/324): Fields with lookup unreadable when a field has custom name.

## 7.2.1 (26/01/2018)
* More work to gracefully handle POI issue 61677, allowing affected MPP files to be read successfully.
* Avoid divide by zero when calculating percent complete from certain Primavera PMXML files.
* UniversalProjectReader updated to recognise MPX files with non-default separator characters.
* Update FastTrack reader to handle invalid percentage values on resource assignments.
* Update FastTrack reader to handle variations in UUID format.
* Read the full project name from XER files and the Primavera database and store it in the project title attribute.

## 7.2.0 (18/01/2018)
* Added support for reading TurboProject PEP files.
* Handle numeric values with leading spaces in XER files.
* Fix array bounds error when reading constraints from certain MPP files.

## 7.1.0 (03/01/2018)
* Added support for reading GanttProject GAN files.
* Ensure that calendar exception dates are read correctly from XER files and P6 databases regardless of the user's timezone.
* Read working day calendar exceptions from XER files and P6 database.
* Mark some ProjectFile methods as deprecated.

## 7.0.3 (21/12/2017)
* Use the Windows-1252 character set as the default when reading XER files.
* Gracefully handle POI issue 61677 to allow MPP affected MPP files to be read successfully.
* Handle recurring calendar exceptions read from MSPDI files without an occurrence count.
* Improve robustness of FastTrack schedule reader.
* Avoid reading empty calendar exceptions from MPX files.

## 7.0.2 (20/11/2017)
* Further improvements to task pruning for Asta PP files.

## 7.0.1 (20/11/2017)
* Improve robustness when reading MPP files when using certain 64 bit Java runtimes.
* Populate the project's comments property when reading an MSPDI file.
* Ensure that tasks are not discarded when reading PP files from older Asta versions.
* Fixed [Issue 319](https://sourceforge.net/p/mpxj/bugs/319): Wrong date ranges for split tasks
* Fixed [Issue 222](https://sourceforge.net/p/mpxj/bugs/222): getDefaultTaskType() not returning correct default task type

## 7.0.0 (08/11/2017)
* Added support for reading recurring exceptions from MPP and MSPDI files.
* Updated RecurringTask class interface (Note: this is a breaking API change)
* MSPDI writer now uses save version 14 by default (Note: this may affect applications which consume MSPDI files you generate)
* Correctly handle MSPDI files with Byte Order Marks.
* Handle MSPDI files with varying namespaces.
* Improve robustness Merlin file reader.
* Improve extraction of task start and finish dates from PMXML files only containing partial data.
* Prevent POI from closing the input stream when using UniversalProjectReader
* Fixed [Issue 321](https://sourceforge.net/p/mpxj/bugs/321): Cannot read mpp file using getProjectReader.

## 6.2.1 (11/10/2017)
* Gracefully handle corrupt MPP files.
* Improve reading and writing slack values for MSPDI files.
* Improve activity hierarchy extraction from Phoenix files.
* Fixed [Issue 243](https://sourceforge.net/p/mpxj/bugs/243): MSPDI Slack values not correctly set while loading.

## 6.2.0 (06/10/2017)
* Added support for reading Work Weeks from MPP files.
* Add support for calendar exception names for MPP and MSPDI files.
* Updated to use POI 3.17.
* Improve accuracy of calendar exception dates read from XER files and P6 database.
* Only write non-default user-defined field values to a PMXML file.
* Use Primavera P6 17.7 XML schema.
* Gracefully handle corrupt document summary information in MPP files.
* Don't duplicate exceptions when reading from an MSPDI file.
* Fixed [Issue 231](https://sourceforge.net/p/mpxj/bugs/231): MPP DataType: Non-unique enumeration value.
* Fixed [Issue 258](https://sourceforge.net/p/mpxj/bugs/258): Calendar Work Week missing from MPP data extraction.
* Fixed [Issue 318](https://sourceforge.net/p/mpxj/bugs/318): TimephasedWork Negative TotalAmount.
* Fixed [Issue 320](https://sourceforge.net/p/mpxj/bugs/320): Date conversion fails in PrimaveraReader.

## 6.1.2 (12/09/2017)
* Gracefully handle incomplete records in XER files.

## 6.1.1 (30/08/2017)
* Ensure all classes in the gem are required

## 6.1.0 (28/07/2017)
* Provide Task.getEffectiveCalendar() method
* Populate missing finish dates in MSPDI files

## 6.0.0 (22/07/2017)
* Gracefully handle invalid calendar data in XER files.
* Handle XER files containing blank lines.
* Add support for reading resource rates and availability tables from P6 (Contributed by Brandon Herzog).
* Include overtime in work and cost fields when reading from P6 (Contributed by Brandon Herzog).
* Read default project calendar from P6 (Contributed by Brandon Herzog).
* Read resource rate and assignment units from P6 (Contributed by Brandon Herzog).
* Set ignore resource calendar flag for tasks from P6 (Contributed by Brandon Herzog).
* Change P6 costs to be calculated from resource assignment to support XER files without the cost table (Contributed by Brandon Herzog).
* Map anticipated end date to deadline for P6 (Contributed by Brandon Herzog).
* Update task work to include actual and remaining work when reading from P6 (Contributed by Brandon Herzog).
* Calculate summary task work fields by summing up children when reading from P6 (Contributed by Brandon Herzog).
* Set task project name when reading from P6 (Contributed by Brandon Herzog).
* Fix "00:00" calendar finish times to parse as end of day when reading from P6 (Contributed by Brandon Herzog).
* Add default working hours if a calendar does not specify any hours when reading from P6 (Contributed by Brandon Herzog).
* Read fiscal year start month from P6 (Contributed by Brandon Herzog).
* Fix bug in rollup of child task dates containing null values that could set incorrect end date when reading from P6 (Contributed by Brandon Herzog).
* Fix date offset in parse of P6 calendar exceptions (Contributed by Brandon Herzog).
* Fix count of P6 UDFs that map to same data type (Contributed by Brandon Herzog).
* Add support for reading Resource and Assignment UDFs from P6 (Contributed by Brandon Herzog).
* Update P6 UDFs to fill into multiple field types to expand storage capacity, for example into TEXT and ENTERPRISE_TEXT (Contributed by Brandon Herzog).
* Use only the WBS as activity code for WBS tasks instead of also appending name for P6 tasks (Contributed by Brandon Herzog).
* Add the ability to link task Relations that cross project boundaries in XER files (Contributed by Brandon Herzog).
* Add function to clear all exceptions from ProjectCalendar instances (Contributed by Brandon Herzog).
* Reading the lag calendar scheduling option as the "LagCalendar" custom project property when reading from P6 (Contributed by Brandon Herzog).
* Updated UDF parsing to handle values as booleans if the user chooses to map them to Flag fields (Contributed by Brandon Herzog).

## 5.14.0 (13/07/2017)
* Improve handling of activity codes read from Phoenix files
* Calculate percent complete for tasks read from Phoenix files
* Populate task duration with Original Duration attribute when reading from XER files or P6 databases.
* Ensure that task finish dates are read correctly from Phoenix files.
* Improve UniversalProjectReader's handling of non-MPP OLE compound documents.
* Improve task hierarchy and ordering when reading some MPP files.

## 5.13.0 (27/06/2017)
* Further improve handling of WBS, bar, and task structure from Asta files.

## 5.12.0 (26/06/2017)
* Improve handling of WBS, bar, and task structure from Asta files.

## 5.11.0 (20/06/2017)
* Improve handling of malformed durations in MSPDI files.
* Improve performance when reading MPP files with certain kinds of timephased data.
* Raise a specific "password protected" exception type from the Ruby gem.
* Fix an issue with the storage of the "earned value method" task attribute.

## 5.10.0 (23/05/2017)
* Improve handling of deleted tasks in MPP files.
* Improve handling of invalid predecessor tasks in MPX files.
* Improve handling of invalid saved view state in MPP files.
* Fixed [Issue 313](https://sourceforge.net/p/mpxj/bugs/313): Empty baseline dates populated with garbage date instead of null.

## 5.9.0 (27/04/2017)
* Add support for reading ProjectLibre POD files (from ProjectLibre version 1.5.5 onwards).
* Correct getter method name for "file application" project property.

## 5.8.0 (21/04/2017)
* Updated to use POI 3.16 (note new dependency on Apache Commons Collections required by POI).
* Improve support for estimated durations in Merlin files.
* Read task notes from Asta files.
* Improve support for reading resource rates from Phoenix files.
* Add "file application" and "file type" to project properties to determine source of schedule data.

## 5.7.1 (22/03/2017)
* Improve support for Phoenix Project Manager XML files.

## 5.7.0 (20/03/2017)
* Add support for FastTrack Schedule files.
* Ensure that timephased data calculations correctly handle entry to and exit from DST.
* Fixed [Issue 306](https://sourceforge.net/p/mpxj/bugs/306): Microsoft Project 2016:  Issue with assignment 'Work Contour' attribute.

## 5.6.5 (07/03/2017)
* Improve handling of invalid calendar data in MSPDI files
* Improve handling of XER files containing multi-line records
* Improve handling of malformed MPX files
* Fixed [Issue 308](https://sourceforge.net/p/mpxj/bugs/308): Add support for elapsed percent to MSPDI writer
* Fixed [Issue 310](https://sourceforge.net/p/mpxj/bugs/310): MPX percent lag incorrect

## 5.6.4 (16/02/2017)
* UniversalProjectReader now recognises and handles byte order marks
* Fixed [Issue 307](https://sourceforge.net/p/mpxj/bugs/307): TimeUnit.ELAPSED_PERCENT read incorrectly from MPP files

## 5.6.3 (08/02/2017)
* Added a parameter to the Ruby gem to allow the maximum JVM memory size to be set.
* Updated to rtfparserkit 1.10.0 for improved RTF parsing.

## 5.6.2 (06/02/2017)
* Fixed [Issue 305](https://sourceforge.net/p/mpxj/bugs/305): Failed to Parse error with Primavera 15.2 or 16.1 XML files

## 5.6.1 (03/02/2017)
* Correct resource assignment handling for Phoenix Project Manager schedules.

## 5.6.0 (29/01/2017)
* Add support for Phoenix Project Manager schedules.

## 5.5.9 (27/01/2017)
* Improve robustness of date parsing for MPX files.

## 5.5.8 (23/01/2017)
* Fix NPE when reading graphical indicators with unknown field type.

## 5.5.7 (13/01/2017)
* Fix percent complete NaN value for some Primavera schedules.

## 5.5.6 (06/01/2017)
* Fix incorrectly set critical flag for primavera schedules.

## 5.5.5 (06/01/2017)
* Updated to rtfparserkit 1.9.0 for improved RTF parsing
* Improve calendar exception parsing for Primavera XER and database readers.
* Ensure the task summary flag is set correctly for Primavera schedules.
* Rollup baseline, early and late start and finish dates to WBS for Primavera schedules.
* Rollup baseline duration, remaining duration and percent complete to WBS for Primavera schedules.
* Use the project's critical slack limit value when setting the critical flag on a task.
* Experimental support for reading Merlin Project schedules.

## 5.5.4 (01/12/2016)
* Default to UTF-8 encoding when generating JSON files

## 5.5.3 (29/11/2016)
* Correctly read text from MPP files when default charset is not UTF-8.
* Improve accuracy when reading MPP9 files.

## 5.5.2 (02/11/2016)
* Add Primavera Parent Resource ID as a specific resource attribute (Based on a contribution by Dave McKay).
* PMXML writer generates currency record (Based on a contribution by Dave McKay).
* PMXML writer defaults Activity PercentCompleteType to Duration (Based on a contribution by Dave McKay).
* PMXML writer records currency and parent attributes for Resource (Based on a contribution by Dave McKay).
* PMXML writer resource assignments include RateSource and ActualOvertimeUnits attributes(Based on a contribution by Dave McKay).
* MSPDI reader: gracefully handle invalid calendar exceptions..
* PMXML writer: gracefully handle missing data.
* Planner writer: gracefully handle missing data.

## 5.5.1 (14/10/2016)
* Update universal project reader to support zip files.
* Update ruby to align error handling with universal project reader.

## 5.5.0 (13/10/2016)
* Universal project reader.
* Avoid NPE when reading PMXML files.
* Fixed [Issue 297](https://sourceforge.net/p/mpxj/bugs/297): Missing extended attributes
* Fixed [Issue 300](https://sourceforge.net/p/mpxj/bugs/300): CrossProject field omission causes issues when importing to P6

## 5.4.0 (06/10/2016)
* Updated to use POI 3.15.

## 5.3.3 (31/08/2016)
* Avoid NPE when field type is unknown.
* Improve Ruby error reporting.
* Improve support for non-standard time formats in MPX files
* Improve support for MPP14 files with very large numbers of blank tasks

## 5.3.2 (31/08/2016)
* When reading an XER file, treat FT_STATICTPYE user defined fields as text.

## 5.3.1 (01/07/2016)
* Add data date attribute to PMXML output.
* Update PMXML writer to avoid NPE.
* Update PMXML writer to allow task field used for Activity ID to be chosen.
* Updated to avoid NPE when reading an XER file where project not under EPS.
* Generate Task IDs if missing from MSPDI file

## 5.3.0 (10/06/2016)
* Add support for PP files generated by Asta Powerproject from version 13.0.0.3 onwards
* Minor improvements to SDEF support.
* Updated to rtfparserkit 1.8.0
* Improve finish time handling in PMXML files (contributed by lobmeleon)

## 5.2.2 (11/03/2016)
* Add support for resource assignment Stop and Resume attributes for MPP and MSPDI files
* Fixed [Issue 291](https://sourceforge.net/p/mpxj/bugs/291): PrimaveraPMFileWriter.write fails with java.lang.IllegalArgumentException
* Fixed [Issue 292](https://sourceforge.net/p/mpxj/bugs/292): Microsoft Project 2016 : Need to set 'Stop' and 'Resume'  properties for net.sf.mpxj.ResourceAssignment

## 5.2.1 (11/02/2016)
* Add support for PP files generated by Asta Powerproject up to version 13.0.0.3

## 5.2.0 (08/02/2016)
* Add support for PP files generated by Asta Powerproject 11, Powerproject 12, Easyplan 2, Easyplan 3, Easyplan 4, Easyplan 5 and Easyplan 6
* Fixed [Issue 285](https://sourceforge.net/p/mpxj/bugs/285): Unsupported encoding command ansicpg949
* Fixed [Issue 288](https://sourceforge.net/p/mpxj/bugs/288): AvailabilityTable getEntryByDate does not work properly

## 5.1.18 (25/01/2016)
* Fixed [Issue 285](https://sourceforge.net/p/mpxj/bugs/285): Unsupported encoding command ansicpg1254
* Fixed [Issue 286](https://sourceforge.net/p/mpxj/bugs/286): NullPointerException in CriteriaReader.getConstantValue
* Fixed [Issue 287](https://sourceforge.net/p/mpxj/bugs/287): Allow a character encoding to be specified when reading an XER file
* Write Primavera Primary Resource Unique ID to Task field Number1

## 5.1.17 (30/12/2015)
* Improve support for reading MPP files generated by Project 2016
* Handle missing time component of a time stamp field when reading an MPX file.

## 5.1.16 (18/12/2015)
* Improve support for reading MPX files generated by SureTrak

## 5.1.15 (16/12/2015)
* Fix WBS and Activity ordering for tasks from Primavera.

## 5.1.14 (09/12/2015)
* Strip unescaped control characters from JSON output.

## 5.1.13 (26/11/2015)
* For schedules imported from Primavera ensure tasks representing activities are ordered by Activity ID within the WBS to match Primavera.

## 5.1.12 (16/11/2015)
* Avoid NPE when writing MSPDI files with timephased data  (contributed by Bruno Gasnier)
* Improve resource assignment constructor (based on a contribution by Bruno Gasnier)
* Improve MPX French translations (contributed by Bruno Gasnier)
* Add calendar specific minutes per day, week, month, and year (based on a contribution by Bruno Gasnier)
* Add support for reading and writing GUID attribute for PMXML, XER files and Primavera database.

## 5.1.11 (12/11/2015)
* Avoid NPE when reading MPP14 custom properties.
* Ensure calculated task attributes are present in JSON output.
* Handle MSPDI files written by German versions of Microsoft Project (based on a contribution by Lord Helmchen)
* Fixed [Issue 277](https://sourceforge.net/p/mpxj/bugs/277): synchronizeTaskIDToHierarchy clears list of tasks
* Fixed [Issue 273](https://sourceforge.net/p/mpxj/bugs/273): PrimaveraPMFileWriter throws Exception at write(..)
* Fixed [Issue 281](https://sourceforge.net/p/mpxj/bugs/281): Parent task is always null when reading a Primavera XER file
* Ensure that Task.getSuccesors() and Task.getPredecessors() return an empty list rather than null.

## 5.1.10 (09/09/2015)
* Improve FixedMeta2 block size heuristic to improve reliability when reading MPP14 files.

## 5.1.9 (29/08/2015)
* Ensure Resource BookingType is read correctly from MPP files
* Added basic custom field attributes to JSON output
* Added Ruby methods to work with custom field aliases
* Fix to infinite loop condition when writing calendar (contributed by lobmeleon)
* Fixed [Issue 274](https://sourceforge.net/p/mpxj/bugs/274): MPXJ getNotes() API returns garbled value for multibyte characters
* Fixed [Issue 268](https://sourceforge.net/p/mpxj/bugs/268): Unsupported encoding error when reading resource notes
* Fixed [Issue 256](https://sourceforge.net/p/mpxj/bugs/256): Incorrect resource types are read (contributed by Colin Rodriguez)
* Symmetry between Primavera PM reader/writer (contributed by lobmeleon)
* Added UDF support to PMXML file reader and writer(contributed by lobmeleon)
* Updated to rtfparserkit 1.4.0

## 5.1.8 (13/07/2015)
* Another attempt at getting tzinfo-data dependency working

## 5.1.7 (13/07/2015)
* Updated ruby gem to make tzinfo-data dependency conditional on platform

## 5.1.6 (13/07/2015)
* Updated ruby gem to allow timezone to be provided

## 5.1.5 (05/06/2015)
* Updated to use IKVM 8.0.5449.1

## 5.1.4 (03/06/2015)
* Updated to generate Activity ID for Primavera WBS.
* Updated to correct Primavera duration percent complete calculation.

## 5.1.3 (18/05/2015)
* Updated to ensure Ruby reads Boolean attributes correctly.

## 5.1.2 (18/05/2015)
* Updated to ensure Ruby recognises short type as an integer.

## 5.1.1 (18/05/2015)
* Updated to use ruby-duration gem to avoid conflict with ActiveSupport::Duration.

## 5.1.0 (17/05/2015)
* Updated to ensure that PrimaveraDatabaseReader.setSchema accepts null or empty string
* Ensure conversion to/from .Net DateTime takes account of timezone and daylight savings (based on a contribution by Timour Koupeev)
* Updated to use POI 3.12.
* Removed ProjectFile.getTaskFieldAliases, replaced by ProjectFile.getCustomField().getFieldByAlias
* Removed ProjectFile.getResourceFieldAliases, replaced by ProjectFile.getCustomField().getFieldByAlias

## 5.0.0 (06/05/2015)
* Added project properties to the JSON output
* Added support for project properties to the Ruby wrapper
* Added support for reading data from a standalone Primavera P6 SQLite database
* Fixed [Issue 267](https://sourceforge.net/p/mpxj/bugs/267): XXE security vulnerability
* Fixed [Issue 266](https://sourceforge.net/p/mpxj/bugs/266): Task Number fields not saved to file if the value would floor to zero
* Fixed [Issue 255](https://sourceforge.net/p/mpxj/bugs/255): Not all project calendars are read in for Project 2013 files (based on a contribution by Colin Rodriguez)
* Renamed TaskContainer class to ChildTaskContainer
* Renamed ProjectHeader class to ProjectProperties
* Introduced ProjectConfig class
* Introduced TaskContainer class
* Introduced ResourceContainer class
* Introduced ResourceAssignmentContainer class
* Introduced ProjectCalendarContainer class
* Renamed ProjectFile.getProjectHeader to getProjectProperties
* Renamed ProjectFile.getCalendar to getDefaultCalendar
* Renamed ProjectFile.setCalendar to setDefaultCalendar
* Renamed MppReader.getReadHeaderOnly to getReadPropertiesOnly
* Renamed MppReader.setReadHeaderOnly to setReadPropertiesOnly
* Renamed ProjectFile.getCalendarUniqueID to ProjectConfig.getNextCalendarUniqueID
* Renamed ProjectFile.getResourceUniqueID to ProjectConfig.getNextResourceUniqueID
* Renamed ProjectFile.getTaskUniqueID to ProjectConfig.getNextTaskUniqueID
* Renamed ProjectFile.getAssignmentUniqueID to ProjectConfig.getNextAssignmentUniqueID
* Renamed ProjectFile.getResourceID to ProjectConfig.getNextResourceID
* Renamed ProjectFile.getTaskID to ProjectConfig.getNextTaskID
* Renamed ProjectHeader.getApplicationName to getShortApplicationName
* Renamed ProjectHeader.setApplicationName to setShortApplicationName
* Renamed ProjectHeader.setCalendarName to setDefaultCalendarName
* Renamed ProjectHeader.getCalendarName to getDefaultCalendarName
* Moved ProjectFile.getProjectFilePath to ProjectHeader.getProjectFilePath
* Moved ProjectFile.setProjectFilePath to ProjectHeader.setProjectFilePath
* Moved ProjectFile.getApplicationName to ProjectHeader.getFullApplicationName
* Moved ProjectFile.setApplicationName to ProjectHeader.setFullApplicationName
* Moved FileCreationRecord.setDelimiter to ProjectHeader.setMpxDelimiter
* Moved FileCreationRecord.getDelimiter to ProjectHeader.getMpxDelimiter
* Moved FileCreationRecord.setProgramName to ProjectHeader.setMpxProgramName
* Moved FileCreationRecord.getProgramName to ProjectHeader.getMpxProgramName
* Moved FileCreationRecord.setFileVersion to ProjectHeader.setMpxFileVersion
* Moved FileCreationRecord.getFileVersion to ProjectHeader.getMpxFileVersion
* Moved FileCreationRecord.setCodePage to ProjectHeader.setMpxCodePage
* Moved FileCreationRecord.getCodePage to ProjectHeader.getMpxCodePage
* Moved ProjectFile.getMppFileType to ProjectHeader.getMppFileType
* Moved ProjectFile.setMppFileType to ProjectHeader.setMppFileType
* Moved ProjectFile.getApplicationVersion to ProjectHeader.getApplicationVersion
* Moved ProjectFile.setApplicationVersion to ProjectHeader.setApplicationVersion
* Moved ProjectFile.setAutoFilter to ProjectHeader.setAutoFilter
* Moved ProjectFile.getAutoFilter to ProjectHeader.getAutoFilter
* Removed ProjectFile.getAliasTaskField, replaced by ProjectFile.getTaskFieldAliases().getField()
* Removed ProjectFile.getAliasResourceField, replaced by ProjectFile.getResourceFieldAliases().getField()
* Removed ProjectFile.getTaskFieldAlias, replaced by ProjectFile.getTaskFieldAliases().getAlias()
* Removed ProjectFile.setTaskFieldAlias, replaced by ProjectFile.getTaskFieldAliases().setAlias()
* Removed ProjectFile.getResourceFieldAlias, replaced by ProjectFile.getResourceFieldAliases().getAlias()
* Removed ProjectFile.setResourceFieldAlias, replaced by ProjectFile.getResourceFieldAliases().setAlias()
* Removed ProjectFile.getTaskFieldAliasMap, replaced by ProjectFile.getTaskFieldAliases
* Removed ProjectFile.getResourceFieldAliasMap, replaced by ProjectFile.getResourceFieldAliases
* Removed ProjectFile.addTable, replaced by ProjectFile.getTables().add()
* Removed ProjectFile.getTaskTableByName, replaced by ProjectFile.getTables().getTaskTableByName()
* Removed ProjectFile.getResourceTableByName, replaced by ProjectFile.getTables().getResourceTableByName()
* Removed ProjectFile.addFilter, replaced by ProjectFile.getFilters().addFilter()
* Removed ProjectFile.removeFilter, replaced by ProjectFile.getFilters().rmoveFilter()
* Removed ProjectFile.getAllResourceFilters, replaced by ProjectFile.getFilters().getResourceFilters()
* Removed ProjectFile.getAllTaskFilters, replaced by ProjectFile.getFilters().getTaskFilters()
* Removed ProjectFile.getFilterByName, replaced by ProjectFile.getFilters().getFilterByName()
* Removed ProjectFile.getFilterByID, replaced by ProjectFile.getFilters().getFilterByID()
* Removed ProjectFile.getAllGroups, replaced by ProjectFile.getGroups()
* Removed ProjectFile.getGroupByName, replaced by ProjectFile.getGroups().getByName()
* Removed ProjectFile.addGroups, replaced by ProjectFile.getGroups().add()
* Removed ProjectFile.addView, replaced by ProjectFile.getViews().add()
* Removed ProjectFile.setViewState, replaced by ProjectFile.getViews().setViewState()
* Removed ProjectFile.getViewState, replaced by ProjectFile.getViews().getViewState()
* Removed ProjectFile.getResourceSubProject, replaced by ProjectFile.getSubProjects().getResourceSubProject()
* Removed ProjectFile.setResourceSubProject, replaced by ProjectFile.getSubProjects().setResourceSubProject()
* Removed ProjectFile.addSubProject, replaced by ProjectFile.getSubProjects().add()
* Removed ProjectFile.getAllSubProjects, replaced by ProjectFile.getSubProjects
* Removed ProjectFile.fireTaskReadEvent, replaced by ProjectFile.getEventManager().fireTaskReadEvent()
* Removed ProjectFile.fireTaskWrittenEvent, replaced by ProjectFile.getEventManager().fireTaskWrittenEvent()
* Removed ProjectFile.fireResourceReadEvent, replaced by ProjectFile.getEventManager().fireResourceReadEvent()
* Removed ProjectFile.fireResourceWrittenEvent, replaced by ProjectFile.getEventManager().fireResourceWrittenEvent()
* Removed ProjectFile.fireCalendarReadEvent, replaced by ProjectFile.getEventManager().fireCalendarReadEvent()
* Removed ProjectFile.fireAssignmentReadEvent, replaced by ProjectFile.getEventManager().fireAssignmentReadEvent()
* Removed ProjectFile.fireAssignmentWrittenEvent, replaced by ProjectFile.getEventManager().fireAssignmentWrittenEvent()
* Removed ProjectFile.fireRelationReadEvent, replaced by ProjectFile.getEventManager().fireRelationReadEvent()
* Removed ProjectFile.fireRelationWrittenEvent, replaced by ProjectFile.getEventManager().fireRelationWrittenEvent()
* Removed ProjectFile.fireCalendarWrittenEvent, replaced by ProjectFile.getEventManager().fireCalendarWrittenEvent()
* Removed ProjectFile.addProjectListener, replaced by ProjectFile.getEventManager().addProjectListener()
* Removed ProjectFile.addProjectListeners, replaced by ProjectFile.getEventManager().addProjectListeners()
* Removed ProjectFile.removeProjectListener, replaced by ProjectFile.getEventManager().removeProjectListener()
* Removed ProjectFile.addGraphicalIndicator
* Removed ProjectFile.getGraphicalIndicator, replaced by ProjectFile.getCustomFields().getCustomField().getGraphicalIndicator()

## 4.7.6 (18/03/2015)
* Added a Ruby wrapper for MPXJ
* Added the ability to export project data as JSON, to make it easier to work with in languages other than Java
* Added support for the Assignment attribute Resource Request Type
* Primavera database and XER readers updated to match WBS visible in Primavera for each task. Previous behaviour of generating a unique WBS for each task can be restored using a flag set on the readers.
* Avoid NPE when calculating Task Completed Through
* Read Task Earned Value Method correctly from MPP files
* Fix issue where some floating point attributes were returning NaN

## 4.7.5 (27/02/2015)
* Handle invalid Primavera calendar data gracefully

## 4.7.4 (25/02/2015)
* Fixed [Issue 257](https://sourceforge.net/p/mpxj/bugs/257): Failed to read project containing CodePage 1250 text.
* Fixed [Issue 259](https://sourceforge.net/p/mpxj/bugs/259): MS Project 2010: tasks with null baseline dates
* Incorrect task end date read from Primavera XER and database
* Incorrect percent complete read from Primavera XER, database, and PMXML files
* Failed to read fields held at the end of a fixed data block
* Added support for Task Baseline Estimated Duration, Baseline Estimated Start, Baseline Estimated Finish, Baseline Fixed Cost, and Baseline Fixed Cost Accrual
* Added the ability to customise the fields read from a Primavera database or XER file.
* Added Task Activity Type and Task Status as additional fields read from Primavera database and XER and files
* Changed Task physical percent complete methods for consistency to use Number rather than Integer

## 4.7.3 (23/12/2014)
* Updated to use POI 3.11.
* Updated to use rtfparserkit 1.1.0 for Java 6 compatibility.

## 4.7.2 (15/12/2014)
* Updated to fix Maven dependency issue.

## 4.7.1 (08/12/2014)
* Added a flag to MPPReader to indicate that only the project header should be read.

## 4.7.0 (04/12/2014)
* Implemented new RTF parser for stripping RTF to improve performance and accuracy
* Removed non-API code from the top level package
* Improved support for reading built-in and custom project properties from MPP files.
* Improved resilience of MPP file reading to unknown data structures
* Fixed issue which could cause an infinite loop when ordering tasks in a file containing multiple consecutive blank tasks
* Fixed issue where free text versions of task start, finish, and duration fields were not being read correctly from MPP14 files

## 4.6.2 (11/11/2014)
* Fixed issue with custom duration field units not read correctly from MSPDI files
* Fixed [Issue 223](https://sourceforge.net/p/mpxj/bugs/223): Problems with the lag calculated in the relation
* Outline code not read correctly from MPP file written by Project 2013
* Fixed [Issue 239](https://sourceforge.net/p/mpxj/bugs/239): Defensive changes to avoid exceptions when reading MPP files
* Fixed [Issue 250](https://sourceforge.net/p/mpxj/bugs/250): Deleted tasks being read from mpp file
* Added DotNetInputStream and DotNetOutputStream classes for ease of use under .Net.
* Updated to automatically generate and package MpxjUtilities.dll

## 4.6.1 (17/10/2014)
* Fixed NuGet metadata

## 4.6.0 (17/10/2014)
* Added support for NuGet.
* Fixed an issue where the ID and Unique ID resource attributes were being read incorrectly from MPP14 files.
* Fixed an issue where the project's default duration format was not being used
* Fixed [Issue 248](https://sourceforge.net/p/mpxj/bugs/248): Reading .MPP file using MPXJ 4.2 reads extra unintentional ResourceAssignment with the task which is not seen in Task Sheet in Microsoft Project
* Fixed [Issue 235](https://sourceforge.net/p/mpxj/bugs/235): All resources have "Material" property
* Fixed [Issue 247](https://sourceforge.net/p/mpxj/bugs/247): Updated Primavera PM XML file reader to capture the Project ID to align with data read from XER file/database (contributed by Nathaniel Marrin)
* Fixed [Issue 247](https://sourceforge.net/p/mpxj/bugs/247): Updated Primavera PM XML file reader to ensure task percent complete supports Physical Percent, Duration Percent and Units Percent (contributed by Nathaniel Marrin)
* Fixed [Issue 247](https://sourceforge.net/p/mpxj/bugs/247): Updated Primavera PM XML file reader to ensure task baseline values match values read from XER file/database (contributed by Nathaniel Marrin)
* Fixed [Issue 247](https://sourceforge.net/p/mpxj/bugs/247): Updated Primavera PM XML file reader to ensure task actual duration to matches value read from XER file/database (contributed by Nathaniel Marrin)
* Fixed [Issue 247](https://sourceforge.net/p/mpxj/bugs/247): Updated Primavera PM XML file reader to read the task duration (contributed by Nathaniel Marrin)
* Fixed [Issue 247](https://sourceforge.net/p/mpxj/bugs/247): Updated Primavera PM XML file reader to read task LateStart, LateFinish, EarlyStart, EarlyFinish attributes correctly (contributed by Nathaniel Marrin)
* Fixed [Issue 247](https://sourceforge.net/p/mpxj/bugs/247): Updated Primavera PM XML file reader to read task Start and End correctly (contributed by Nathaniel Marrin)
* Fixed [Issue 247](https://sourceforge.net/p/mpxj/bugs/247): Updated Primavera PM XML file reader to identify milestones (contributed by Nathaniel Marrin)
* Fixed [Issue 247](https://sourceforge.net/p/mpxj/bugs/247): Updated Primavera PM XML file reader to set the task Critical attribute (contributed by Nathaniel Marrin)
* Fixed [Issue 247](https://sourceforge.net/p/mpxj/bugs/247): Updated Primavera PM XML file reader to include costs (contributed by Nathaniel Marrin)
* Fixed [Issue 247](https://sourceforge.net/p/mpxj/bugs/247): Updated Primavera XER/Database readers to read task Start and End correctly (contributed by Nathaniel Marrin)
* Migrated tests to JUnit 4

## 4.5.0 (01/03/2014)
* Added the ability to call the .Net version of MPXJ from COM.
* Added support Primavera decimal database columns.
* Added support for user defined task fields (contributed by Mario Fuentes).
* Added POM for current Maven versions (contributed by Nick Burch)
* Fixed [Issue 213](https://sourceforge.net/p/mpxj/bugs/213): Unable to load mpp from project-2013
* Fixed [Issue 226](https://sourceforge.net/p/mpxj/bugs/226): Primavera currency files without currency information
* Fixed [Issue 227](https://sourceforge.net/p/mpxj/bugs/227): PrimaveraReader cannot handle files with more than 30 user defined fields
* Fixed [Issue 224](https://sourceforge.net/p/mpxj/bugs/224): setMilestone() issue
* Fixed [Issue 210](https://sourceforge.net/p/mpxj/bugs/210): MPXJ 4.4 and 2013 files - invalid load of task data
* Updated to fix an issue with Asta Powerproject PP file tokenization
* Updated to fix an issue where valid WBS values containing .0 are corrupted
* Updated to allow Primavera hours per day to be a decimal value
* Updated to support Primavera PM XML files generated by Primavera versions up to P6v8.3 (contributed by Mario Fuentes)
* Updated to set the StatusDate attribute in the project header from a Primavera database, XER file or PM XML file.
* Updated to use (a patched version of) POI 3.10.

## 4.4.0 (14/03/2013)
* Added support for writing Primavera PM XML files.
* Added support for reading Asta Powerproject PP and MDB files.
* Added support for writing SDEF files (Contributed by William Iverson).
* Added support for reading Enterprise Custom Fields 1-50 for Task, Resources, and Resource Assignments.
* Added MpxjExtensionMethods assembly to simplify working with Java types in .Net (Contributed by Kyle Patmore)
* Provided two new .Net DLL versions in addition to the original version. These allow properties to be accessed in a ".Net style", and for languages apart from VB, provide .Net style method names.
* Updated to remove the distinction between base calendar and resource calendars in the ProjectFile class.
* Updated to improve support for custom outline codes (Contributed by Gary McKenney)
* Fixed [Issue 189](https://sourceforge.net/p/mpxj/bugs/189): getTimephasedOvertimeWork can return TimephasedWork with NaN
* Fixed [Issue 190](https://sourceforge.net/p/mpxj/bugs/190): Support for timephased cost for cost type resources
* Fixed [Issue 195](https://sourceforge.net/p/mpxj/bugs/195): Rolled Up tasks don't use default duration units
* Fixed [Issue 199](https://sourceforge.net/p/mpxj/bugs/199): Extract Primavera Task ID
* Updated to fix an issue where the resource assignment delay attribute was not being read from or written to MSPDI files correctly
* Updated to fix an issue where derived calendars were not being read correctly from MPP files
* Updated to use IKVM 7.2.

## 4.3.0 (08/02/2012)
* Added support for reading Primavera PM XML files.
* Added support for reading timephased cost, and timephased baseline cost and baseline work from MPP files.
* Added support for Work Weeks in MSPDI files (SourceForge feature request 23).
* Updated to use IKVM 7.0.
* Updated to fix SourceForge bug 3290224: Incorrect order of tasks when writing an MSPDI file (contributed by Jonathan Besanceney).
* Fixed [Issue 161](https://sourceforge.net/p/mpxj/bugs/161): ResourceAssignment.getTaskUniqueID() returns null.
* Fixed [Issue 169](https://sourceforge.net/p/mpxj/bugs/169): Wrong project name in MPX file.
* Fixed [Issue 170](https://sourceforge.net/p/mpxj/bugs/170): Wrong title in XML file when importing from XER file.
* Fixed [Issue 168](https://sourceforge.net/p/mpxj/bugs/168): Wrong record number for resource calendar in MPX file.
* Fixed [Issue 171](https://sourceforge.net/p/mpxj/bugs/171): In the XML file the element field SaveVersion is missing.
* Fixed [Issue 167](https://sourceforge.net/p/mpxj/bugs/167): Loop when import task with 0% on units of works in resources.
* Fixed [Issue 163](https://sourceforge.net/p/mpxj/bugs/163): French locale NA incorrect.
* Fixed [Issue 175](https://sourceforge.net/p/mpxj/bugs/175): Invalid dependency between child and parent.
* Fixed [Issue 174](https://sourceforge.net/p/mpxj/bugs/174): Missing tasks from MS Project 2010 mpp file.
* Fixed [Issue 179](https://sourceforge.net/p/mpxj/bugs/179): Wrong WBS code and WBS when converting a Primavera XER file.
* Fixed [Issue 177](https://sourceforge.net/p/mpxj/bugs/177): Error reading XER file with German localisation for numbers.
* Fixed [Issue 166](https://sourceforge.net/p/mpxj/bugs/166): TimephasedResourceAssignments with negative TotalWork.
* Fixed [Issue 181](https://sourceforge.net/p/mpxj/bugs/181): Wrong currency symbol in the exported file.
* Fixed [Issue 104](https://sourceforge.net/p/mpxj/bugs/104): TimephasedResourceAssignment end date not correct.
* Fixed [Issue 116](https://sourceforge.net/p/mpxj/bugs/116): Calendar hours are incorrect.
* Fixed [Issue 188](https://sourceforge.net/p/mpxj/bugs/188): NullReferenceException with getTimephasedBaselineWork.
* Fixed [Issue 191](https://sourceforge.net/p/mpxj/bugs/191): Outline number is null when opening Project 2003 MPP file.
* Fixed [Issue 192](https://sourceforge.net/p/mpxj/bugs/192): Unable to parse note (unknown locale).
* Fixed [Issue 193](https://sourceforge.net/p/mpxj/bugs/193): MPP9Reader marks all tasks after a null task as null.
* Updated to fix an issue where the Task critical attribute was incorrectly calculated for some manually scheduled tasks.
* Updated to fix an issue where the Task summary attribute was not set correctly when using certain methods to add or remove child tasks.
* Updated to fix an issue where subprojects were not read correctly (Contributed by Gary McKenney).

## 4.2.0 (23/06/2011)
* Added support for resource assignment fields Baseline Cost 1-n, Baseline Work 1-n, Baseline Start 1-n, Baseline Finish 1-n, Start 1-n, Finish 1-n, Date 1-n, Duration 1-n, Cost 1-n, Text 1-n, Number 1-n, Flag 1-n, for MPP, MPD, and MSPDI files.
* Added support for task suspend date, task resume date, and task code read from Primavera, and represented in MS Project custom fields Date1, Date2, and Text1 respectively.
* Added support for retrieving the table associated with any view.
* Fixed [Issue 158](https://sourceforge.net/p/mpxj/bugs/158): Error converting Mpp to planner.
* Fixed [Issue 157](https://sourceforge.net/p/mpxj/bugs/157): MSPDI Linklag for TimeUnit.Percent.
* Fixed [Issue 156](https://sourceforge.net/p/mpxj/bugs/156): Error reading calendars for 2010 files.
* Fixed [Issue 159](https://sourceforge.net/p/mpxj/bugs/159): Duplication of calendar id.
* Fixed [Issue 153](https://sourceforge.net/p/mpxj/bugs/153): Wrong task start.
* Fixed [Issue 156](https://sourceforge.net/p/mpxj/bugs/156): Wrong start and finish dates for 2010 files.

## 4.1.0 (30/05/2011)
* Updated ProjectFile class to change default value for "auto" flags to simplify programmatic creation of project files.
* Added support for Manual, Start Text, Finish Text, and Duration Text attributes in MSPDI files.
* Added support cost resource type for MPP12, MPP14 and MSPDI files.
* Added Task.removePredecessor method (contributed by Leslie Damon).
* Added "read presentation data" flag to MPPReader - allows clients to save time and memory when MPP presentation data not required.
* Added support for reading Primavera calendars (contributed by Bruno Gasnier).
* Added support for resource assignment leveling delay for MPP, MPD, and MSPDI files.
* Added support for "unassigned" resource assignments.
* Added support for task manual duration attribute for manually scheduled tasks in MPP14 and MSPDI files.
* Added support for resource NT account attribute for MPP9, MPP12, and MPP14 files.
* Added support for physical % complete for MPP9, MPP12, and MPP14 files.
* Fixed [Issue 120](https://sourceforge.net/p/mpxj/bugs/120): MPXJ API returns the incorrect start date of a manual task.
* Fixed [Issue 123](https://sourceforge.net/p/mpxj/bugs/123): Task id incorrect after importing from MPP14 file.
* Fixed [Issue 124](https://sourceforge.net/p/mpxj/bugs/124): MPXJ 4.0 fails to work with Project 2010 format.
* Fixed [Issue 128](https://sourceforge.net/p/mpxj/bugs/128): Index was outside the bounds of the array.
* Fixed [Issue 131](https://sourceforge.net/p/mpxj/bugs/131): header.getHonorConstraints() is not working in case of MPP.
* Fixed [Issue 139](https://sourceforge.net/p/mpxj/bugs/139): Empty notes appear for all tasks when saving in XML format.
* Fixed [Issue 122](https://sourceforge.net/p/mpxj/bugs/122): All Extended Attributes always added when using MSPDIWriter.
* Fixed [Issue 144](https://sourceforge.net/p/mpxj/bugs/144): Baseline/Actual Work in 2010 MPP missing.
* Fixed [Issue 114](https://sourceforge.net/p/mpxj/bugs/114): ResourceAssignment getCalendar not using IgnoreResourceCalendar flag
* Fixed [Issue 146](https://sourceforge.net/p/mpxj/bugs/146): ExternalTaskProject value missing.
* Fixed [Issue 137](https://sourceforge.net/p/mpxj/bugs/137): Deleted Primavera tasks handling problem.
* Fixed [Issue 143](https://sourceforge.net/p/mpxj/bugs/143): Latest CVS version gives wrong values for inactive field.
* Fixed [Issue 125](https://sourceforge.net/p/mpxj/bugs/125): Task ID order when creating a project file is not correct.
* Fixed [Issue 106](https://sourceforge.net/p/mpxj/bugs/106): Invalid tasks that should not be there.
* Updated to fix task calendars read incorrectly from MPP14 files.
* Updated to fix incorrect month duration assumption (contributed by Frank Illenberger).
* Updated to fix incorrect number format in MSPDI file in non-English locales (contributed by Frank Illenberger).
* Updated to fix incorrect resource assignment actual work attribute for MPP14 files.
* Updated to fix incorrect task leveling delay attribute for MPP9, MPP12, and MPP14 files.
* Updated to fix leveling delay and link lag when writing an MSPDI file (contributed by Frank Illenberger).
* Updated to fix incorrect assignment actual start date when writing an MSPDI file.
* Updated to improve support for material resources in MSPDI files.
* Updated to reduce overall size of MSPDI files by not writing default values.
* Updated to use IKVM 0.46.0.1.
* Updated to use POI 3.7.
* Updated to make task, resource, and assignment fields read from MPP files data-driven, rather than hard coded.

## 4.0.0 (25/05/2010)
* Added support for reading Microsoft Project 2010 MPP files.
* Added support for reading Primavera P6 XER files.
* Added support for reading Primavera P6 databases.
* Updated to target Java 1.6.
* Added Russian locale (Contributed by Roman Bilous).
* Relation.getDuration() is always giving result in 'HOUR' fmt.

## 3.2.0 (20/01/2010)
* Added support for Resource cost rate tables (Based on code by Andrei Missine).
* Added support for Resource availability (Based on code by Andrei Missine).
* Added support for successors (Based on an idea by John D. Lewis).
* Added support for task and resource GUIDs.
* Added a flag to allow raw timephased data to be retrieved from MPP files.
* Updated to fix logical operator read issue in MPP auto filters (Contributed by Andrei Missine).
* Fixed [Issue 94](https://sourceforge.net/p/mpxj/bugs/94): MPXJ Issue: Related to Project Calendar.
* Fixed [Issue 90](https://sourceforge.net/p/mpxj/bugs/90): POI License in legal folder of download wrong.
* Updated to fix Steelray bug 15468: Null Pointer Exception reading task constraints.
* Fixed [Issue 102](https://sourceforge.net/p/mpxj/bugs/102): Planner writer causes Null Pointer exception.
* Fixed [Issue 100](https://sourceforge.net/p/mpxj/bugs/100): getRecurring() task is not working
* Fixed [Issue 98](https://sourceforge.net/p/mpxj/bugs/98): getStandardRateFormat() is returning 'null'
* Fixed [Issue 97](https://sourceforge.net/p/mpxj/bugs/97): getWeekStartDay() is not working.
* Fixed [Issue 96](https://sourceforge.net/p/mpxj/bugs/96): getDaysPerMonth() is not working.
* Fixed [Issue 101](https://sourceforge.net/p/mpxj/bugs/101): Resource.getNotes() not working for MPP12 file.
* Fixed [Issue 105](https://sourceforge.net/p/mpxj/bugs/105): MPP: getEditableActualCosts() is not behaving correctly.
* Updated to use POI 3.6.
* Updated to use IKVM 0.42.0.3.
* Updated to make MPX duration parsing more lenient (Contributed by Jari Niskala).
* Updated to make MPP Var2Data extraction more robust (Contributed by Jari Niskala).
* Updated to implement MSPDI context caching to improve performance (Contributed by Jari Niskala).
* Updated to improve MPP file task structure validation. (Contributed by Jari Niskala).
* Updated to improve MPX file parsing. (Contributed by Jari Niskala).
* Updated to automatically populate missing WBS attributes. (Contributed by Jari Niskala).
* Updated to refactor the Relation class (note minor method name changes).
* Updated to add default calendar to Planner output.

## 3.1.0 (20/05/2009)
* Fixed [Issue 73](https://sourceforge.net/p/mpxj/bugs/73): Plan file fails to load.
* Fixed [Issue 72](https://sourceforge.net/p/mpxj/bugs/72): Resource Assignment Normaliser rounding problem.
* Fixed [Issue 78](https://sourceforge.net/p/mpxj/bugs/78): Column alignment values are incorrect.
* Fixed [Issue 76](https://sourceforge.net/p/mpxj/bugs/76): NullPointerException in parseExtendedAttribute() (Contributed by Paul Pogonyshev).
* Fixed [Issue 74](https://sourceforge.net/p/mpxj/bugs/74): .0 at the end of WBS code and outline number (Contributed by Paul Pogonyshev).
* Fixed [Issue 79](https://sourceforge.net/p/mpxj/bugs/79): Too strict net.sf.mpxj.mpd.ResultSetRow.
* Fixed [Issue 80](https://sourceforge.net/p/mpxj/bugs/80): Generated planner file can't be opened.
* Fixed [Issue 82](https://sourceforge.net/p/mpxj/bugs/82): Support for loading global.mpt.
* Fixed [Issue 81](https://sourceforge.net/p/mpxj/bugs/81): Lowercase table name won't work with db on linux machines.
* Fixed [Issue 71](https://sourceforge.net/p/mpxj/bugs/71): Standard Calendar localization import problem.
* Fixed [Issue 83](https://sourceforge.net/p/mpxj/bugs/83): Strange duration conversion from database
* Fixed [Issue 86](https://sourceforge.net/p/mpxj/bugs/86): FilterCriteria not being read in properly (Contributed by James Styles)
* Updated to fix Steelray bug 12335: Infinite loop when reading an MPP9 file.
* Updated to fix Steelray bug 8469: Subproject flag not set correctly.
* Updated to fix potential NPEs (Suggested by Steve Jonik).
* Updated EncryptedDocumentInputStream to wrap rather than extend the POI DocumentInputStream to allow use with POI 3.5. (Contributed by Josh Micich)
* Updated to provide strong names for .Net DLLs.

## 3.0.0 (25/01/2009)
* Updated to the Project 2007 MSPDI schema.
* Updated to POI 3.2.
* Updated to use the SAX parser with JAXB rather than DOM to reduce memory consumption.
* Updated MPX output to prevent Project 2007 complaining.
* Fixed [Issue 68](https://sourceforge.net/p/mpxj/bugs/68): Task getNumber*() methods return inaccurate large values.
* Fixed [Issue 56](https://sourceforge.net/p/mpxj/bugs/56): Duplicate task in file.getChildTasks() when opening MPX.
* Fixed [Issue 57](https://sourceforge.net/p/mpxj/bugs/57): Relation.getTask returns null.
* Fixed [Issue 58](https://sourceforge.net/p/mpxj/bugs/58): Task.getSplits() not consistent.
* Fixed [Issue 60](https://sourceforge.net/p/mpxj/bugs/60): WBS Field not imported Mpp12.
* Fixed [Issue 63](https://sourceforge.net/p/mpxj/bugs/63): There are some conflict in TaskField.
* Fixed [Issue 66](https://sourceforge.net/p/mpxj/bugs/66): MSPDIReader is not setting calendarName in projectHeader.
* Fixed [Issue 67](https://sourceforge.net/p/mpxj/bugs/67): Write resource calendar with exceptions only.
* Fixed [Issue 69](https://sourceforge.net/p/mpxj/bugs/69): File loses predecessors.
* Fixed [Issue 70](https://sourceforge.net/p/mpxj/bugs/70): Resources not bring read.
* Updated to fix incorrect duration calculations where minutes per week were not being used (Contributed by Jonas Tampier).
* Updated split task implementation to represent splits as DateRange instances rather than as hours.
* Added .Net DLLs using IKVM.
* Added support for reading timephased resource assignment data from MPP files.
* Added support CurrencyCode, CreationDate, LastSaved and HyperlinkBase project header fields.
* Added support for reading recurring task data from MPP files.
* Added methods to MPXReader and MPXWriter to allow the caller to determine the supported locales.
* Added Spanish locale (Contributed by Agustin Barto).
* Added support for durations with percentage time lag (Contributed by Jonas Tampier).
* Added support MSPDI file split tasks.

## 2.1.0 (23/03/2008)
* Updated to POI 3.0.2
* Updated to address an out of memory exception raised when processing certain MPP12 files.
* Updated to fix a problem caused by duplicate ID values in MPP12 files.
* Updated to fix a problem with the subproject unique ID calculation (Contributed by Jari Niskala).
* Fixed [Issue 48](https://sourceforge.net/p/mpxj/bugs/48): Import from Project 2007 ignores some tasks.
* Fixed [Issue 52](https://sourceforge.net/p/mpxj/bugs/52): Crash on priority not set in MSPDI-file.
* Fixed [Issue 51](https://sourceforge.net/p/mpxj/bugs/51): Resource start/finish dates with MSP 2007.
* Fixed [Issue 51](https://sourceforge.net/p/mpxj/bugs/51): MS Project 2007: Calendar exceptions dates are wrong.
* Added support for Enterprise task and resource fields.
* Added support for Baseline task and resource fields.
* Added support for extracting non-English (i.e. character set encoded) text from note fields.
* Added support for Chinese MPX files (contributed by Felix Tian).
* Added support for reading project start and end dates from all MPP file types (Bug #1827633).
* Added support for password protected MPP9 files (Contributed by Jari Niskala)
* Added support for calendar exceptions for MPP12 files (Contributed by Jari Niskala)
* Added support for value lists and descriptions for custom fields (Contributed by Jari Niskala)
* Added support for timescale formats (Contributed by Jari Niskala)
* Added support for the project file path attribute (Contributed by Jari Niskala)
* Added support for the ignore resource calendar attribute (Contributed by Jari Niskala)
* Added support for the resource actual overtime work attribute (Contributed by Jari Niskala)
* Added support for the resource material label attribute (Contributed by Jari Niskala)
* Added support for the resource NT account attribute (Contributed by Jari Niskala)
* Improved support for hyperlinks (Contributed by Jari Niskala)
* Improved support for custom fields in MPP12 files (Contributed by Jari Niskala)

## 2.0.0 (07/10/2007)
* Migrated to Java 5
* Introduced generics
* Introduced enums
* Updated to POI 3.0.1
* Updated to JAXB 2.1.4
* Changed company details from Tapster Rock to Packwood Software

## 1.0.0 (30/08/2007)
* Added support for reading MPD files via JDBC
* Added support for reading Planner files
* Added support for over allocated flag to all MPP file formats.
* Added support for calculating duration variance from MPP files.
* Added support for calculating start and finish variance from MPP files.
* Added support for attribute change listeners for Task and Resource classes.
* Added support for start slack, finish slack, free slack and total slack read from MPP files.
* Added support for external tasks.
* Added unique ID generation for calendars read from MPX files.
* Added support for the status date property of the project.
* Fixed a timezone related bug when handling dates for calendar exceptions (Contributed by Todd Brannam).
* Fixed incorrect calculation of lag times for some MPP files.
* Fixed missing predecessor tasks in certain rare MPP9 files.
* Fixed incorrect MPX file AM/PM text setting in certain locales.
* Fixed an ArrayIndexOutOfBoundsException.
* Fixed a ClassCastException.
* Fixed a zero length string error.
* Fixed a duration rounding error when reading MSPDI files.
* Fixed incorrect "as late as possible" constraint handling.
* Incorrect late start date read from an MPP9 file.
* Incorrect total slack calculation.
* Added a default for the task constraint type attribute to prevent a possible NPE when writing an MSPDI file.
* Added a default resource calendar name where the resource name is empty.
* Updated the Column.getTitle method to take account of user defined column aliases.
* Updated to add another condition to the test for deleted tasks in MPP8 files.
* Updated to significantly improve the performance of writing MSPDI files.

## 0.9.2 (07/03/2006)
* Added support for split views.
* Added support for graphical indicators.
* Added a workaround for a bug in MS Project which is seen when calendar exceptions are exported to an MSPDI file. If the exception contained seconds and milliseconds, MS Project marked every day as being affected by the exception, not the day or range of days specified.
* Updated to make date/time/number formats generic, and thus available to end users. For example, this allows users to format currencies in line with the settings in the project file.
* Standardised on minutes per day and minutes per week, rather than hours per day and hours per week.
* Provided additional time ranges for calendar exceptions.
* Refactored Task and Resource to use TaskField and ResourceField to identify fields.
* Updated to automatically generate WBS for tasks read from MPP files when no WBS information is present in the file.
* Fixed a bug when reading MPP files where task finish dates appeared before the start date where a "start no later than" constraint was in use.
* Fixed a bug which resulted in invalid MPX files being generated when a project either had no tasks, or it had no resources.
* Fixed a long standing bug where the calendar records were being written into MPX files after they were referred to in the project summary record.
* Fixed a bug where WBS and Outline Levels were not being auto generated correctly when an MPP file contained a project summary task.
* Fixed a bug where split tasks were not being reported correctly.

## 0.9.1 (26/01/2006)
* Major API rewrite.
* Added a flag called "expanded" to the Task class to represent whether a task in an MPP9 file is shown as expanded or collapsed by MS Project.
* Fixed a bug in the relation code in MpxjQuery (contributed by Shlomo Swidler).
* Modified MPXDateFormat, MPXTimeFormat and MPXCurrencyFormat to derive them from DateFormat and NumberFormat.
* Added support for MPT files.
* Fixed a bug which could case an NPE when reading certain MPP9 files.
* Added support for the "marked" attribute for MPP9 files.
* Added support for reading split task data from MPP9 files.
* Added support for reading calculate multiple critical paths flag.
* Fixed a bug which could case an array out of bounds exception in the Priority (contributed by Frank Illenberger).
* Fixed bug #1346735 "Priorities of the tasks are exported incorrectly".
* Added code to allow tasks, resources, resource assignments and calendars to be removed from the data structure.
* Implemented Italian MPX file format translation (contributed by Elio Zoggia).
* Cleaned up calendar usage.
* Added support for retrieval of custom document summary fields from the project header (contributed by Wade Golden).
* Updated to use checkstyle 4.0 and fixed warnings.
* Rationalised duration conversions into a set of methods in the MPXDuration class.
* Replaced various file format conversion utilities with the general purpose MpxjConvert utility.
* Fixed an issue where tasks with a percent complete value, but no resource assignments, would not write correctly to an MSPDI file.
* Added an accessor method for resource calendars.
* Unique ID generation was not correct for tasks, resources and calendars if these entities were added to an existing project file.
* Fixed a compatibility issue with POI3
* Added an event listener to the project file to allow notifications of resources and tasks being read and written to and from a file.
* Fixed a compiler warning when build with JDK5.
* Fixed a bug where a project start date was not being set correctly in the project header.
* Added support for reading the project header "calendar name", "schedule from" and "revision" values from MPP files.
* Fixed split task support.
* Enhanced TableFontStyle implementation.

## 0.0.25 (11/08/2005)
* Added support for reading all properties from an MPP9 file which define the visual appearance of the Gantt Chart view shown in Microsoft Project (development funding courtesy of Steelray).
* Tidied up constructors. Added no-argument constructors to the MPPFile and MSPDIFile classes.
* Fixed incorrect value in WorkGroup enumerated type.
* Implemented the resource assignment work contour property (contributed by Wade Golden).
* Implemented correct handling for MPX files using different character set encodings (suggested by Frank Illenberger).
* Fixed task duration calculation when importing an MPP file with a "non-standard" hours-per-day setting (contributed by Wade Golden).
* Updated to ensure that the MPX task fixed attribute, and the MPP/MSPDI task type attribute are correctly handled.
* Updated to implement the remaining project header attributes supported by the MSPDI file format.
* Updated to add support for reading the MPX 3.0 files generated by Primavera (courtesy of CapitalSoft).
* Fixed incorrect assumptions about conversion of durations to hours when writing MPX files (contributed by Frank Illenberger).
* Updated to calculate remaining work for resource assignments on import, to allow MSPDI export of this data to work correctly (contributed by Frank Illenberger).
* Updated to add another condition to the test for deleted tasks in MPP8 files.
* Updated to fix a problem with reading assignment data from MPP9 files.
* Rationalised the location of the JUnit tests and the sample files.
* Fixed a problem where the project start and end dates reported in the project header were incorrect.
* Fixed an array out of bounds exception when reading an MPP9 file.
* Updated to allow MPXCalendarHours to accept an arbitrary number of time periods.
* Introduced the Day class to replace the use of arbitrary integers to represent days of the week.
* Added the ability to query the task assignments for a resource using the Resource.getTaskAssignments() method.
* Fixed a problem with number formats in MSPDI files.
* Updated the MPP View class to extract the view type.
* Updated to ensure that duration values read from an MSPDI file are converted into the appropriate duration units, rather than being left as hours as the durations are represented in the MSPDI file.
* Implemented French MPX file format translation (contributed by Benoit Baranne).
* Fixed a bug reading assignment work contour attribute.
* Updated to make failure more graceful when a Microsoft Project 4.0 MPP file is encountered.
* Fixed a bug where deleted constraints in an MPP9 file were were not being ignored.
* Updated to make replace the int relation type in the Relation class with instances of the RelationType class.
* Updated to derive RelationList from AbstractList.
* Added sample code to MpxjQuery to illustrate retrieval of information from Relation instances.
* Updated MpqjQuery to parse MSPDI files as well as MPP and MPX files.
* Added support for early start, early finish, late start, late finish to MPP files.
* Updated MPP9 file support to handle start as late as possible constraints. 
* Added support for sub project file information in MPP9 files.
* Fixed a bug where occasionally a task in MPP9 files were not being read.
* Fixed a NegativeArrayIndexException thrown when reading certain MPP8 files.
* Reduced the memory used by MPXJ by anything up to 60%, particularly when reading large MPP files.
* Fixed a bug when reading MPX files where the field delimiter was not comma, and task relation lists contained more then one entry.
* Updated to fix unreliable retrieval of project start and end dates from certain MPP files.
* Fixed schedule from value in MSPDI files (contributed by Frank Illenberger).
* Fixed a bug when reading durations in elapsed days from an MPP file.
* Tasks can now have arbitrary priority values. These values are mapped to/from the fixed MPP8/MPX priority values where necessary.

## 0.0.24 (10/01/2005)
* Fixed a bug (again!) where deleted resource assignments in MPP9 files were still seen by MPXJ.
* Updated to use class instances instead of primitives to represent some enumerated types.
* Updated to implement support for reading and writing all of the basic Resource attributes found in MSPDI files.
* Updated to implement support for reading and writing all of the basic Task attributes found in MSPDI files.
* Updated to implement support for reading and writing all of the basic Project Header attributes from MPP8 and MPP9 files.
* Made MSPDI file parsing more robust to allow it by default to cope with non-schema-compliant XML in the same manner as MS Project. Implemented a new compatibility flag to allow this behaviour to be disabled in favour of strict parsing.
* Merged DateTimeSettings, CurrencySettings, and DefaultSettings into the ProjectHeader class. This change makes the project header data easier to use as it is in a single place. It also makes the entities used to describe a project consistent with the contents of the MPP and MSPDI file formats.

## 0.0.23 (17/11/2004)
* Fixed a bug where MPXJ was still using the default locale of the user's machine to create localised MPX files when a normal international MPX file was expected.
* Fixed a bug where the incorrect record delimiter was being used in by the MPX RelationList class.
* Fixed a bug where the method Task.getText21 was not retrieving the correct text value.
* Fixed a bug where the task unique ID values were being truncated unnecessarily.
* Fixed a bug where calendar exceptions were not testing the range of dates between the start and end date correctly.
* Fixed a bug where the priority of a task was being escalated when converting between an MPP9 file and an MSPDI file.
* Fixed a bug where a deadline was incorrectly being added to a task when importing data from an MPP9 file.
* Fixed a bug where deleted resource assignments in MPP9 files were still seen by MPXJ.
* Fixed a bug where MPXFile attributes were not being correctly copied by the copy constructor.
* Fixed a rounding error in MPXCalendar.getDaysInRange (contributed by Wade Golden)
* Updated to make MPXJ more robust in the face of unexpected offsets in MPP8 file format.
* Updated support for password protected files to allow write reserved files to be read.
* Updated to use the latest version of JAXB, as shipped in Sun's Java Web Services Developer Pack (JWSDP) version  1.4.
* Updated the distribution to include the redistributable files from the JWSDP JAXB implementation. Users will no longer need to download JWSDP separately in order to make use of MPXJ's MSPDI functionality.
* Updated to prevent empty notes records being added to tasks and resources when reading an MSPDI file.
* Updated to improve accuracy when converting an MPP file to an MSPDI file.
* Added support for blank task rows in MPP8 files.
* Added support for blank resource rows in MPP8 files.
* Added support for Portuguese MPX files.
* Added support reading and writing extended attributes (apart from outline codes) for MSPDI files.
* Added support for the Resource Type attribute.

## 0.0.22 (27/07/2004)
* Fixed a bug where task data was not being read correctly from very large MPP9 files.
* Fixed a bug where certain MPP8 files were not read correctly when no constraint data is present.
* Fixed a bug where certain MPP9 files were not read correctly.
* Fixed a bug where MPP9 files containing invalid resource data were not read correctly.
* Fixed a bug where MPXJ was using the default locale of the user's machine to create localised MPX files when a normal international MPX file was expected.
* Fixed a bug where MPXJ not correctly handling embedded line breaks when reading and writing MPX files.
* Removed arbitrary restrictions on the number of various entities, originally taken from the MPX specification.
* Updated MPX documentation for Task.getFixed and Task.setFixed.
* Updated MPP9 file code to improve handling invalid offset values.
* Updated to remove leading and trailing spaces from MPX task field names before processing.
* Updated to detect password protected files and raise a suitable exception.
* Implemented an enhancement to improve file loading speed by an order of magnitude for files with a large number of tasks or resources (based on a contribution by Brian Leach).
* Implemented support for Maven.
* Updated MpxCreate utility to allow it to create both MPX and MSPDI files.
* Added new JUnit test for confidential customer data.
* Added support for the resource assignment remaining work attribute for MPP8, MPP9 and MSPDI files.

## 0.0.21 (06/05/2004)
* Fixed a bug where the task start date attribute was not always correct for MPP8 files.
* Fixed a bug causing valid tasks to be incorrectly identified as being deleted in MPP8 files.
* Fixed a bug causing an exception when reading certain MPP9 files.
* Updated to allow localised MPX files to be written and read.
* Implemented support for German MPX files.
* Implemented generic mechanism for dealing with task field aliases.
* Implemented task field alias read/write for MSPDI files.
* Implemented task field alias read for MPP9 files.
* Implemented resource field alias read/write for MSPDI files.
* Implemented resource field alias read for MPP9 files.

## 0.0.20 (15/03/2004)
* Fixed a bug where alternative decimal delimiters and thousands separators were not being handled correctly when reading and writing MPX files.
* Fixed a bug causing a null pointer exception when writing an MSPDI file.
* Fixed a bug in MSPDI files where default values were being written incorrectly for some task attributes.
* Fixed a bug with MSPDI file date handling in non GMT time zones.
* Fixed a bug in processing calendar data where data block is not a multiple of 12 bytes
* Fixed a bug processing tables where column data is null
* Fixed checkstyle code warnings.
* Fixed Eclipse code warnings.
* Updated to include version 2.5 of the POI library.
* Added support for task calendars.

## 0.0.19 (02/12/2003)
* Fixed a bug reading table data from certain MPP8 files
* Updated MSPDI support to use latest version of JAXB (from JWSDP-1.3)
* Re-implemented base and resource calendars as a single MPXCalendar class
* Updated support for base calendars and resource calendars for all file formats
* Improved MPXException to print details of any nested exception when a stack trace is printed.
* Removed unnecessary use of ByteArray.java
* Added support for the following task fields: ActualOvertimeCost, ActualOvertimeWork, FixedCostAccrual, Hyperlink, HyperlinkAddress, HyperlinkSubAddress, LevelAssignments, LevelingCanSplit, LevelingDelay, PreleveledStart, PreleveledFinish, RemainingOvertimeCost, RemainingOvertimeWork.

## 0.0.18 (13/11/2003)
* Fixed a bug with writing MS Project compatible MSPDI XML files in non-GMT timezones.
* Fixed a bug with writing MSPDI XML files in non-GMT timezones.
* Fixed a bug causing an exception when zero length calendar names were present
* Fixed a bug causing MPP8 flags to be read incorrectly. Note that flag 20 is still not read correctly.
* Fixed a bug with the "Microsoft Project Compatible Output" flag for MSPDI files.
* Fixed a bug reading task text 10.
* Added new MPXFile.setIgnoreTextModel() method to allow MPXJ to ignore faulty text version of task or resource model records in MPX files.
* Improved invalid MPX data error handling and reporting.
* Added BaseCalendar.getDate method to allow end dates to be calculated based on a start date and a duration of working time.
* Made MPXDateFormat implement java.io.Serializable to allow MPXDate to serialize correctly.
* Updated the ant build file to allow MPXJ to be built without the components that depend on JAXB
* Rationalised setDefaultStartTime and setDefaultEndTime methods
* Added MppXml utility
* Added support for querying view information held in MPP files.
* Added support for querying table information held in MPP files. (NB This allows the user to retrieve column information, including user defined column names)
* Added support for outlinecode1-10 fields in MPP9 files.
* Added support for resource "available from" and "available to" fields.
* Verified that MPXJ will read MPP files written by Microsoft Project 2003 (they are still MPP9 files).

## 0.0.17 (05/08/2003)
* Fixed a bug where a decimal point was being appended to the currency format even if no decimal digits were required.
* Fixed a bug where special characters appearing in the currency symbol were not being quoted.
* Fixed a bug that caused resource assignments to be incorrectly read from some MPP8 files.
* Added a new write method to MPXFile allowing the user control over the character encoding used when writing an MPX file.

## 0.0.16 (04/07/2003)
* Fixed bug causing some extended boolean attributes to be read incorrectly.
* Fixed bug causing MPP8 file task data to be read incorrectly under certain circumstances.
* Updated calendar duration code to account for calendar exceptions.

## 0.0.15 (17/06/2003)
* Fixed a bug causing resource assignments to be duplicated in an MPX file created programmatically.
* Fixed a bug causing an incorrect duration value to be read from an MPP9 file.
* Fixed a bug causing invalid MPX files to be written in locales which don't use a period as the decimal separator.
* Fixed a bug causing embedded quote and comma characters in task and resource notes to be handled incorrectly.
* Added simple JUnit test to demonstrate iteration through relationships.
* Added an example of programmatically creating a partially complete task to the MPXCreate.java example.
* Added default values to the MPX project header.
* Added support for reading the RemainingDuration field from an MPP9 file.
* Updated predecessor and successor method documentation.
* Updated Task.get/set ResourceInitials and Task.get/set ResourceNames method documentation.
* Updated to extract the following fields from resource assignment data in MPP files which were previously not imported: ActualCost, ActualWork, Cost, Finish, Start, Units, Work.

## 0.0.14 (28/05/2003)
* Updated to extract the following fields from resource data in an MPP9 file which were previously not imported: Flag1-Flag20.
* Added the method MPPFile.getFileType to allow the type of MPP file (MPP8: 98, MPP9: 2000,2002) to be determined.
* Updated API to make classes final and constructors package access only where appropriate.
* Updated to use of 6 byte long int fields for cost and work values for MPP8.
* Fixed error in reading task fixed cost for MPP8.
* Updated to extract the following fields from task data in an MPP8 file which were previously not imported: Contact, Cost1-Cost10, Date1-Date10, Duration1-Duration10, EffortDriven, Finish1-Finish10, Flag1-Flag20, HideBar, Milestone, Number1-Number20, Rollup, Start1-Start10, Text1-Text30, Type, WBS.
* Updated to extract the following fields from resource data in an MPP8 file which were previously not imported: Code, Cost1-Cost10, Date1-Date10, Duration1-Duration10, EmailAddress, Finish1-Finish10, Number1-Number20, Start1-Start10, Text1-Text30
* Added support for task and resource note fields in MPP8 files.
* Added support for the OvertimeCost task attribute for all file formats.
* Updated to extract calendar data from MPP8 files.
* Updated resource notes to fix end of line handling problem.
* Added functionality to read default settings and currency settings data from MPP files.

## 0.0.13 (22/05/2003)
* Implemented support for the Microsoft Project 98 file format.
* Fixed a bug that prevented task and resource note text from being read.
* Updated to remove a Java 1.4 dependency introduced in 0.0.12. Will now work with Java 1.3.
* Updated to correct handling of carriage returns embedded in note fields.

## 0.0.12 (08/05/2003)
* Fixed incorrect handling of timezones and daylight saving time.
* Fixed incorrect task structure generated from outline levels.
* Updated to extract the notes fields from tasks and resources read from an MPP file.
* Added the option to remove or preserve the RTF formatting from the note fields from an MPP file.
* Updated to extract the following fields from task data in an MPP file which were previously not imported: Text11-Text30, Number6-Number20, Duration4-Duration10, Date1-Date10, Cost4-Cost10, Start6-Start10, Finish6-Finish10
* Updated to extract the following fields from resource data in an MPP file which were previously not imported: Text6-Text30, Start1-Start10, Finish1-Finish10, Number1-Number20, Duration1-Duration10, Date1-Date10, Cost1-Cost10

## 0.0.11 (15/04/2003)
* Fixed error in format string used in one of the example files.
* Fixed error where double byte characters were being read incorrectly.
* Fixed error where deleted constraints were being resurrected when read from an MPP file.
* Updated to extract the following fields from task data in an MPP file which were previously not imported: Flag11-Flag20, Rollup, HideBar, EffortDriven.

## 0.0.10 (08/04/2003)
* Corrected Actual Start and Actual End fields from MPP file.
* Fixed bug where time values were being broken by daylight saving time in the user's default locale.
* Updated to extract the following fields from task data in an MPP file which were previously not imported: Actual Work, Baseline Work, Cost Variance, Deadline, Remaining Work, Work.
* Updated to extract the following fields from resource data in an MPP file which were previously not imported: Actual Cost, Actual Overtime Cost, Actual Work, Baseline Work, Cost, Cost Variance, Max Units, Overtime Cost, Overtime Rate, Overtime Work, Peak, Regular work, Remaining Cost, Remaining Overtime Cost, Remaining Work, Standard Rate, Work, Work Variance

## 0.0.9 ()
* Fixed bug when handling certain types of modified MPP file where resources have been updated.
* Added sample MPP files for bugs to the JUnit tests.
* Added support for summary flag import from MPP files.
* Added automatic summary flag update when creating an MPX file programmatically.
* Added new constructor to the MSPDIFile class to allow MSPDI files to be created from scratch.

## 0.0.8 (27/03/2003)
* Added support for estimated durations.
* Fixed bug in handling certain types of modified MPP file where tasks have been updated.
* Added the facility to auto generate outline numbers.
