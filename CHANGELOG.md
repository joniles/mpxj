# Changelog

## NOTE
From version 14.0.0 onwards the `net.sf.mpxj`, `net.sf.mpxj-for-csharp` and `net.sf.mpxj-for-vb` packages are
no longer distributed. Please use the `MPXJ.Net` package instead.


## 14.6.0 (unreleased)

## 14.5.1 (2025-10-07)
* Ensure that project listeners work in MPXJ.Net when using the ProjectReaderProxy from UniversalProjectReader.
* Minor PrimaveraScheduler improvements.

## 14.5.0 (2025-10-01)
* Implemented support for reading and exporting projects using Primavera P6 Web Services (p6ws). Among other things this could be used to access projects managed by Primavera P6 EPPM.
* Add Free Slack calculation to the Task class.
* Ensure that the PrimaveraScheduler updates resource assignment dates.
* Handle blank Activity ID values when writing XER and PMXML files.
* Ensure that all Activity, Project, Resource Assignment, Resource and Role Code definitions in XER and PMXML files include a maximum length.

## 14.4.0 (2025-09-16)
* Implemented support for reading projects from Microsoft Project Server (also known as Project Online, Project Web Access or PWA).
* Improve handling of different date formats read from BK3 files.
* Adjust handling of the Mandatory Start constraint on in progress tasks in the PrimaveraScheduler.

## 14.3.5 (2025-08-26)
* When reading XER files and P6 databases, ensure that project-specific calendars are only attached to the project to which they relate.

## 14.3.4 (2025-08-20)
* Add missing reader class proxy handling in MPXJ.Net.

## 14.3.3 (2025-08-14)
* Do not supply a default value for the Task Constraint Type attribute. Previously if this value was not set, MPXJ returned `ConstraintType.AS_SOON_AS_POSSIBLE`.
* Remove workaround for locating the SQLite native library when using .Net on a Mac. This is no longer necessary following the recent SQLite version update.

## 14.3.2 (2025-08-05)
* Handle time ranges in Synchro SP file which finish at midnight.
* Improve support for reading task data from certain Synchro 6.5 SP files.

## 14.3.1 (2025-08-04)
* Improve handling of resource data read from certain Synchro 6.5 SP files.
* Ensure the parent resource is set for nested resources read from Synchro SP files.
* Populate the Resource's Resource ID attribute when reading from Synchro SP files.

## 14.3.0 (2025-08-01)
* Implemented support for reading and exporting projects from Oracle Primavera Cloud (OPC)
* Improve support for reading BK3 files.
* Avoid NPE when writing MSPDI files with manually scheduled tasks when no default calendar has been provided.
* When writing PMXML files all non-null values for User Defined Fields are included. Previously UDF values considered to be defaults were not written.

## 14.2.0 (2025-07-03)
* MS Project will reject MSPDI files where resource names contain , [ or ] characters. When writing MSPDI files, ensure resource names do not contain these characters.
* MS Project will reject MSPDI files where calendar names are longer than 51 characters. When writing MSPDI files, truncate calendar names longer than 51 characters.
* Improve handling of unusual end-of-line character combinations when reading XER files.
* Improve handling of invalid encoded UUID values read from XER files.
* Reduce memory used when reading XER files.
* Ignore invalid recurring exceptions read from BK3 files.
* Updated to use sqlite-jdbc 3.50.2.0
* Updated to use jsoup 1.21.1
* Updated to use jackson-core 2.19.1
* Updated to use jackcess 4.0.8

## 14.1.0 (2025-06-05)
* Updated to POI 5.4.1
* Improve actual duration calculation for activities with suspend and resume dates when reading XER files and Primavera P6 databases.
* Added Enable Summarization and Enable Publication flags to `ProjectProperties`.
* Added support for Enable Summarization and Enable Publication flags when reading and writing PMXML files.
* Ensure the Actual Start and Actual Finish attributes in `ProjectProperties` are populated.
* Improve retrieval of Enterprise Custom Field names when reading MSPDI files.
* Updated the JSON writer to use Jackson.

## 14.0.0 (2025-05-07)
* **NEW FEATURES**
* MPXJ can now schedule projects using CPM (Critical Path Method)
* Two new classes (`MicrosoftScheduler` and `PrimaveraScheduler`) allow MPXJ to schedule a project in a way which follows the approach of either Microsoft Project or Primavera P6.
* Added support for reading Edraw Project EDPX files
* **CHANGES**
* Improvements to accuracy of reading text UDF values from Powerproject PP files.
* Corrected conversion of elapsed durations when writing JSON files.
* Added the `Relation#lag_units` method to the ruby gem.
* **BREAKING CHANGES - .Net**
* The `net.sf.mpxj`, `net.sf.mpxj-for-csharp`, and `net.sf.mpxj-for-vb` NuGet packages are no longer being distributed. You must migrate your code to use the `MPXJ.Net` NuGet package instead.
* **BREAKING CHANGES - Java, Python**
* The name of the package containing MPXJ's Java classes has changed from `net.sf.mpxj` to `org.mpxj`. You will need to update your code by searching for `net.sf.mpxj` and replace this with `org.mpxj`. NOTE: for Java applications using Maven, the Maven Group ID **has not changed**, you will still retrieve MPXJ using the Group ID `net.sf.mpxj`.
* The constant `TaskField.PRIMARY_RESOURCE_ID` has been renamed to `TaskField.PRIMARY_RESOURCE_UNIQUE_ID`.
* The `RelationContainer#getRawSuccessors` method has been removed. Use the `RelationContainer#getSuccessors` method instead. This method now returns the same data `getRawSuccessors` returned previously.
* The deprecated `UserDefinedField` constructors have been removed, use `UserDefinedField.Builder` instead.
* The deprecated `UserDefinedField#setDataType` method has been removed, use the `UserDefinedField.Builder#dataType` method instead.
* The deprecated `StructuredNotes` constructor has been removed, use the `StructuredNotes` constructor taking a `ProjectFile` instance instead.
* The deprecated `Relation#getSourceTask` and `Relation#getTargetTask` methods have been removed, use `Relation#getPredecessorTask` and `Relation#getSuccessorTask` methods instead.
* The deprecated `Relation.Builder#sourceTask` and `Relation.Builder#targetTask` methods have been removed, use `Relation.Builder#predecessorTask` and `Relation.Builder#successorTask` methods instead.
* The deprecated `ActivityCodeValue#getType` method has been removed. Use the `ActivityCodeValue#getParentCode` method instead.
* The deprecated `ActivityCodeValue#getActivityCode` method has been removed. Use the `ActivityCodeValue#getParentCode` method instead.
* The deprecated `ActivityCodeValue#getParent` method has been removed. Use the `ActivityCodeValue#getParentValue` method instead.
* The deprecated `ActivityCodeValue#getParentUniqueID` method has been removed. Use the `ActivityCodeValue#getParentValueUniqueID` method instead.
* The deprecated `ActivityCodeValue.Builder#type` method has been removed. Use the `ActivityCodeValue.Builder#activityCode` method instead.
* The deprecated `ActivityCodeValue.Builder#parent` method has been removed. Use the `ActivityCodeValue.Builder#parentValue` method instead.
* The deprecated `Task#addActivityCode` method has been removed. Use the `Task#addActivityCodeValue` method instead.
* The deprecated `GanttBarStyleException#getBarStyleIndex` method has been removed. Use `GanttBarStyleException#getGanttBarStyleID` to retrieve the bar style ID, and `GanttChartView#getGanttBarStyleByID` to retrieve the style
* The deprecated constant `TaskField.ACTIVITY_CODE_LIST` has been removed. Use `TaskField.ACTIVITY_CODE_VALUES` instead.
* The deprecated `Task#getActivityCodes` method has been removed. Use the `Task#getActivityCodeValues` method instead.
* The deprecated `Task#setPrimaryResourceID` method has been removed. Use the `Task#setPrimaryResourceUniqueID` method instead.
* The deprecated `Task#getPrimaryResourceID` method has been removed. Use the `Task#getPrimaryResourceUniqueID` method instead.
* The deprecated `Task#isSucessor` method has been removed. Use the `Task#isSuccessor` method instead.
* The common `MPPUtility` static methods `getShort`, `getInt`and `getLong` have been moved to the `ByteArrayHelper` class.
* **BREAKING CHANGES - Ruby**
* The deprecated Ruby attribute `Relation#task_unique_id` has been removed, use `Relation#predecessor_task_unique_id` and `Relation#successor_task_unique_id` instead.

## 13.12.0 (2025-04-09)
* Added support for reading Float Path and Float Path Order from XER files and P6 databases.
* Added support for writing Float Path and Float Path Order to XER files.
* Added support for reading baselines from Phoenix schedules.
* Improve date arithmetic when using the `ProjectCalendar#getDate()` method with elapsed durations.
* Include units percent complete when writing resource assignments to PMXML files.
* Improve accuracy of resource assignment remaining units when writing XER and PMXML files.
* When writing MSPDI files, calculate resource assignment remaining work if not present.

## 13.11.0 (2025-03-10)
* Add support for reading the WBS and Activity Methodology GUID attribute from XER files and P6 databases, and for writing this to XER files.
* Improve accuracy of resource assignment start and finsh dates when reading XER files and P6 databases.
* Fixed an issue reading resource code value hierarchy from XER files.
* Improve retrieval of Gantt Bar Styles from certain MPP files.
* Added the `GanttBarStyleException` methods `getGanttBarStyleID()` and `setGanttBarStyleID()`.
* Added the `GanttChartView` method `getGanttBarStyleByID()`.
* Marked the `GanttBarStyleException#getBarStyleIndex()` method as deprecated. Use the `GanttBarStyleException#getGanttBarStyleID()` method to retrieve the Gantt Bar Style ID, then use the view's `getGanttBarStyleByID()` method to retrieve a list of matching styles.
* Added the `Duration#negate()` method to simplify negating a duration.
* Improve provision of default values for Project Planned Start date and Activity Planned Duration when writing XER files.

## 13.10.0 (2025-02-07)
* Add support for reading the P6 EPS using the `listEps()` method provided by the `PrimaveraDatabaseReader` and `PrimaveraDatabaseFileReader` classes.
* Improve handling of Activity Type attribute when reading PMXML files written by Primavera P6 6.x.
* Ensure that the External Early Start and External Late Finish attributes are written to XER files.
* Fix a NPE when calling `PrimaveraXERFileReader.listProjects()`.
* Avoid unnecessary data storage and type conversion to improve efficiency when calling `PrimaveraXERFileReader.listProjects()`.
* Provide additional `ResourceAssignment` methods to allow `List<TimephasedWork>` to be used to add timephased work, rather than requiring a `TimephasedWorkContainer`.
* Improve identification of tasks when reading certain Asta Powerproject PP files.

## 13.9.0 (2025-01-09)
* Updated to POI 5.4.0
* Updated PMXML schema to version 24.12.
* Added support for reading and writing currencies for Primavera P6 schedules.
* Improve recognition of dates displayed as NA in Microsoft Project when reading certain MPP file.
* Ignore invalid cost rate table entries when reading certain MPP files.

## 13.8.0 (2024-12-17)
* Added support for reading and writing Project Codes, Resource Codes, Role Codes and Resource Assignment Codes for Primavera P6 schedules.
* When writing PMXML files, improve handling of P6 schedules where activity code sequence numbers are missing.
* Added an *experimental* feature to `MSPDIWriter` to allow the writer to generate timephased data when none is present. Disabled by default, call the `setGenerateMissingTimephasedData` and pass `true` to enable.
* To improve consistency, the methods `Task.getPrimaryResourceID()` and `Task.setPrimaryResourceID()` have been marked as deprecated. Use the new `Task.getPrimaryResourceUniqueID()` and `Task.setPrimaryResourceUniqueID()` methods instead.
* Added the methods `Task.getPrimaryResource()` and `Task.setPrimaryResource()`.
* Improved accuracy of retrieving the resource assignment GUID attribute when reading MPP files (Contributed by Fabian Schmidt).
* Improve population of Task Start and Finish attributes when reading Primavera P6 schedules.
* Marked the `ActivityCodeValue.getParent()` method as deprecated. Use `ActivityCodeValue.getParentValue()` instead.
* Marked the `ActivityCodeValue.getParentUniqueID()` method as deprecated. Use `ActivityCodeValue.getParentValueUniqueID()` instead.
* Marked the `ActivityCodeValue.Builder.parent()` method as deprecated. Use `ActivityCodeValue.Builder.parentValue()` instead.
* Marked the `ActivityCodeValue.getActivityCode()` method as deprecated. Use `ActivityCodeValue.getParentCode()` instead.

## 13.7.0 (2024-11-25)
* Update the MPXJ ruby gem to allow access to calendar data.
* Mark the `ActivityCodeValue.getType()` method as deprecated. For clarity this method has been replaced by the new `ActivityCodeValue.getActivityCode()` method.
* Mark the `ActivityCodeValue.Builder.type()` method as deprecated. For clarity this method has been replaced by the new `ActivityCodeValue.Builder.activityCode()` method.
* Added the `Task.getActivityCodeValues()` method, which returns a `Map` of `ActivityCodeValue` instances, keyed by `ActivityCode`.
* Marked the `Task.getActivityCodes()` method as deprecated. Replaced with the `Task.getActivityCodeValues()` method which is more clearly named, and presents the activity code values in a more flexible form.
* Added the `Task.addActivityCodeValue()` method.
* Marked the `Task.addActivityCode()` method as deprecated. Replaced with the `Task.addActivityCodeValue()` method which is more clearly named.
* Further improvements to retrieval of custom field values read from MPP files.
* Ensure that missing resource assignment and task start and finish dates are handled gracefully when working with calendars for manually scheduled tasks.

## 13.6.0 (2024-11-06)
* Added the `Task.getBaselineTask()` methods. For applications where a separate baseline schedule is present or a baseline has been manually added to the `ProjectFile` instance, these methods will allow you to access the underlying baseline task instance from the current task instance.
* Added the Activity Percent Complete attribute to the `Task` class. The value of this attribute will be the Duration, Physical or Units percent complete value, based on the Percent Complete Type setting. This attribute is provided as a convenience to match the Activity Percent Complete type value shown in P6.
* Improve retrieval of custom field values for certain MPP files.
* Improve handling of PMXML files with more than 11 baselines.
* Improve handling of unexpected data types when writing JSON files.
* Added the `Relation.getPredecessorTask()` and `Relation.getSuccessorTask()` methods.
* Marked the `Relation.getSourceTask()` and `Relation.getTargetTask()` methods as deprecated, use the `Relation.getPredecessorTask()` and `Relation.getSuccessorTask()` instead.
* Ensure that with "Link Cross Project Relations" enabled when reading XER or PMXML files, the predecessor and successor lists for both tasks related acrosss projects are correctly populated.

## 13.5.1 (2024-10-28)
* Fix CVE-2024-49771: Potential Path Traversal Vulnerability (Contributed by yyjLF and sprinkle).

## 13.5.0 (2024-10-17)
* Added support for reading and writing Resource Role Assignments for Primavera schedules. The `Resource.getRoleAssignments()` method retrieves a map representing the roles a resource is assigned to, along with the skill level for each assignment. The `Resource.addRoleAssignment()` and `Resource.removeRoleAssignment()` methods allow role assignments to be added and removed.
* Added support for the Resource Primary Role attribute, which is read from and written to Primavera schedules.
* Improve handling Boolean attributes with default values when reading XER files.
* Added the `getShowStartText`, `getShowFinishText` and `getShowDurationText` methods to the `Task` class. When working with manually scheduled tasks in Microsoft Project, users can potentially supply arbitrary text for the Start, Finish and Duration attributes. Microsoft Project still stores appropriate values for these attributes, which can be accessed in MPXJ as Start, Finish and Duration, but where the user has supplied text, these attributes are available as Start Text, Finish Text, and Duration Text. The methods added by this change allow the caller to determine which version of each attribute should be shown to the user in order to replicate what they see in Microsoft Project.

## 13.4.2 (2024-10-08)
* Added the `ProjectCalendarDays.getCalendarHours()` method to allow direct access to the `ProjectCalendarHours` instances for each day of the week.

## 13.4.1 (2024-10-07)
* Added the `ProjectCalendarDays.getCalendarDayTypes()` method to allow direct access to the `DayType` instances for each day of the week.

## 13.4.0 (2024-09-18)
* Added support for reading and writing resource shifts for P6 schedules.
* Ensure the Scheduling Progressed Activities project property is populated when reading Phoenix schedules.
* When reading milestones from an Asta schedule, ensure that the Activity Type attribute is populated to allow start milestones and finish milestones to be differentiated.
* Fix an issue which occurred when writing MSPDI files with manually scheduled tasks starting on non-working days where their timephased data is split as days.

## 13.3.1 (2024-08-30)
* Handle duplicate custom field value unique IDs when reading MSPDI files.
* Handle missing remaining early start date when reading timephased data from a P6 schedule.

## 13.3.0 (2024-08-22)
* When reading multiple Primavera schedules from the same source, MPXJ now ensures that instances of activity code definitions, user defined field definitions, locations, units of measure, expense categories, cost accounts, work contours, and notes topics are shared across projects. This will allow you to, for example, filter tasks from multiple projects using a `Location` instance. Previously each project had its own independent instances for each of these types, which could not be used across multiple projects.
* When reading Powerproject schedules, ensure that the Activity ID attribute for WBS entries is populated using Powerproject's Unique Task ID attribute.
* Add support for reading timephased planned work from MPP files for manually scheduled tasks (Contributed by Fabian Schmidt).

## 13.2.2 (2024-08-14)
* Add missing constructors to `TimephasedCost` and `TimephasedWork` in MPXJ.Net.

## 13.2.1 (2024-08-13)
* Make the MPXJ.Net assembly strong named.

## 13.2.0 (2024-08-12)
* Implemented the `UserDefinedField.Builder` class.
* Marked the `UserDefinedField` constructor as deprecated. Use the builder class instead.
* Marked the `UserDefinedField.setDataType()` method as deprecated. Use the builder class instead.
* Updated to address an issue when writing XER files where a project does not have an explicit Unique ID value, and there are project UDF values.
* Added the convenience method `ActivityCode.addValue` to make it easier to add a value to an activity code.

## 13.1.0 (2024-07-26)
* Updated to POI 5.3.0
* Add support for reading and writing timephased data for activities in P6 schedules which have a "manual" curve. (Note: MPXJ does not currently support translating timephased data between different applications, so timephased data read from an MPP file won't be written to a P6 schedule and vice versa).
* Add an attribute to the `ResourceAssignment` class to represent timephased planned work. This is read from/written to P6 as Budgeted Work.
* Update Phoenix schemas to ensure that cost types are represented as doubles.
* Updated to avoid reading apparently invalid resources from Project Commander files.
* Correct the `Finish` attribute for resource assignments when reading PMXML files.
* Improve accuracy of the `RemainingDuration` attribute for resource assignments when writing PMXML files.
* Improve recognition of non-working days when reading calendars certain PMXML files.
* Add support for the Resource Assignment field Remaining Units. (Note: this field defaults to the same value as Units if it is not explicitly populated).
* Ensure the Resource Assignment field Remaining Units is read from and written to P6 schedules.
* Improve handling of invalid calendar exception data when reading P6 schedules from XER files or a P6 database.
* Improve the implementation of the Unique ID sequence generator used by MPXJ to avoid issues when multiple classloaders are used.
* Deprecated the original `StructuredNotes` constructor. A new version of the constructor takes an additional `ProjectFile` argument.
* Deprecated the original `UserDefinedField` constructor. A new version of the constructor takes an additional `ProjectFile` argument.
* Add support for reading and writing the Project Website URL attribute for P6 schedules.
* Add support for the Notes attribute as part of the `ProjectProperties` class.
* Ensure that project notes are read from and written to PMXML files.
* Usability improvements to the Notes class hierarchy to make it easier to update notes.
* Improvements to notes handling when writing PMXML files to make it easier to construct structured notes using plain text.

## 13.0.2 (2024-07-08)
* When writing XER files, provide a default value for the Resource ID if it is not populated.

## 13.0.1 (2024-07-04)
* For XER files, ignore the "null" resource when writing resource rates.
* When reading MPP files, ensure that Enterprise Custom Field Unique IDs are unique across entities.

## 13.0.0 (2024-06-20)
* NOTE: this is a major release containing breaking changes. When updating from a 12.x release it is recommended that you first update to the most recent 12.x release and deal with any deprecation warnings before moving to this release.
* NOTE: the [original `net.sf.mpxj` NuGet packages](https://www.nuget.org/packages?q=net.sf.mpxj) are now deprecated and will be replaced by the [MPXJ.Net NuGet Package](https://www.nuget.org/packages/MPXJ.Net) in the next major MPXJ release. The `net.sf.mpxj` packages will continue to be maintained until then, at which point they will no longer be distributed. Please migrate your code to use MPXJ.Net at the earliest opportunity, and open an issue in the GitHub issue tracker if you encounter problems.
* Updated to use JAXB3. Among other things this change ensures compatibility with Spring Boot 3. Note that this may be a breaking change for you if you own application uses JAXB2.
* When reading P6 schedules, the custom properties (as retrieved using `ProjectProperties.getCustomProperties`) will no longer contain scheduling options. These are now all available as attributes of the `ProjectProperties` class.
* Removed redundant `setUniqueID` methods from immutable objects. These previously threw `UnsupportedOperationException` when called.
* The `ProjectEntityWithUniqueID` interface no longer contains the `setUniqueID` method. Entities with a mutable Unique ID attribute now implement the `ProjectEntityWithMutableUniqueID` interface, which inherits from the `ProjectEntityWithUniqueID` interface.
* The `MSPDIReader` and `PrimaveraXERFileReader` classes no longer provide getter and setter methods for `Encoding`, use the `Charset` getter and setter methods instead.
* Removed the `XerFieldType` class and replaced usages of it with the `DataType` class.
* The deprecated `ActivityCode()` constructor and `addValue` method have been removed.
* The deprecated `ActivityCodeValue()` constructor and `setParent` method have been removed.
* The deprecated `CostAccount()` constructor and `getDescription` method have been removed.
* The deprecated `CustomFieldValueItem` methods `getParent` and `setParent` have been removed.
* The deprecated `ExpenseCategory()` constructor has been removed.
* The deprecated `ExpenseItem(Task)` constructor and all setter methods have been removed.
* The deprecated `JsonWriter` methods `setEncoding` and `getEncoding` have been removed.
* The deprecated `Location.Builder()` constructor has been removed.
* The deprecated `NotesTopic()` constructor has been removed.
* The deprecated `ObjectSequence` method `reset` has been removed.
* The deprecated `PlannerWriter` methods `setEncoding` and `getEncoding` have been removed.
* The deprecated `PrimaveraXERFileWriter` method `setEncoding` has been removed.
* The deprecated `ProjectCalendar` method `getDate` has been removed.
* The deprecated `ProjectCalendarHelper` method `getExpandedExceptionsWithWorkWeeks` has been removed.
* The deprecated `ProjectEntityContainer` methods `getNextUniqueID`, `renumberUniqueIDs` and `updateUniqueIdCounter` have been removed.
* The deprecated `ProjectFile` methods `expandSubprojects` and `updateUniqueIdCounters` have been removed.
* The deprecated `ProjectReader` method `setProperties` and `setCharset` have been removed.
* The deprecated `ProjectWriterUtility` class has been removed.
* The deprecated `RateHelper` methods accepting a `ProjectFile` argument have veen removed.
* The deprecated `Relation(Task,Task,RelationType,Duration)` constructor has been removed.
* The deprecated `RelationContainer.addPredecessor(Task,Task,RelationType,Duration)` method has been removed
* The deprecated `Resource` methods `setAvailableFrom`, `setAvailableTo`, `setMaterialLabel` and `setMaxUnits` have been removed.
* The deprecated `ResourceAssignment` method `getCalendar` has been removed.
* The deprecated `Step(Task)` constructor and all setter methods have been removed.
* The deprecated `Task` method `addPredecessor(Task,RelationType,Duration)` has been removed
* The deprecated `TimephasedUtility` methods `segmentBaselineWork(ProjectFile, ...)` and `segmentBaselineCost(ProjectFile, ...)` methods have been removed.
* The deprecated `UnitOfMeasure.Builder()` constructor has been removed.

## 12.10.3 (2024-06-14)
* Add new project property `IsProjectBaseline`. When using the `readAll` method to retrieve a set of schedules, if the data source contains both schedules and baselines this property will be true for the `ProjectFile` instances which represent a baseline.

## 12.10.2 (2024-06-03)
* Added a missing unique ID mapping when writing resource assignment resource unique IDs to MSPDI files (Contributed by Alex Matatov)
* Handle null field type when reading outline code values from an MPP9 file.

## 12.10.1 (2024-05-22)
* Ignore missing `PropertySet`s when reading MPP files (Contributed by Fabian Schmidt).
* Corrected handling of the "24 Hour Calendar" Relationship Lag Calendar setting when reading and writing XER files (Based on a contribution by Alex Matatov)

## 12.10.0 (2024-05-13)
* When a baseline is added using one of the `ProjectFile.setBaseline` methods, ensure that the relevant baseline date is set in `ProjectProperties`.
* Marked the `JsonWriter` methods `setEncoding` and `getEncoding` as deprecated, use `setCharset` and `getCharset` instead.
* Marked the `PlannerWriter` methods `setEncoding` and `getEncoding` as deprecated, use `setCharset` and `getCharset` instead.
* Marked the `PrimaveraXERFileWriter` method `setEncoding` as deprecated, use `setCharset` instead.
* Marked the `ProjectCalendarHelper` method `getExpandedExceptionsWithWorkWeeks` as deprecated, use `ProjectCalendar.getExpandedCalendarExceptionsWithWorkWeeks` instead.
* Marked the `ProjectReader` method `setCharset` as deprecated. Readers which support setting the Charset now implement the `HasCharset` interface, which includes Charset getter and setter methods.
* Implemented the `UniversalProjectWriter` class. This complements the `UniversalProjectReader` class by providing a simple way for MPXJ users to write project files without having to be concerned with details of the individual `ProjectWriter` classes. This is intended to replace the `ProjectWriterUtility` class. Note that the `ProjectWriterUtility` has a somewhat brittle mechanism to determine the output file format from the supplied output file name. This is not replicated by `UniversalProjectWriter`, users are expected to provide their own code to determine the appropriate file format.
* Marked the `ProjectWriterUtility` class as deprecated.

## 12.9.3 (2024-04-24)
* Improve handling of non-standard timestamp formats in XER files.

## 12.9.2 (2024-04-19)
* Ensure calendars in Asta schedules have the correct name.
* Improve assignment of calendars to summary tasks when reading Asta schedules.
* Preserve calendar hierarchy when reading Asta schedules.

## 12.9.1 (2024-04-17)
* Fix an issue where `UniversalProjectReader` would raise an exception when handling an unknown file type.
* Ensure that resource type is included as part of the resource assignment data when writing PMXML files.

## 12.9.0 (2024-04-11)
* Updated `UniversalProjectReader` to add `getProjectReaderProxy` methods to allow access to the instance of the reader class which will be used to read a schedule, prior to the schedule being read. This will allow the reader to be configured, or schedule to be ignored without reading its content.
* Deprecated the `ProjectReader.setProperties` method. This method was originally implemented to allow settings to be passed to reader classes when using `UniversalProjectReader`. You can now use `UniversalProjectReader.getProjectReaderProxy` to achieve this.
* Add `from` method to all `Builder` classes to allow initialisation from existing objects.
* The `CostAccount.Builder` class now provides two `notes` methods to allow formatted or unformatted notes to be added to cost accounts.
* The `CostAccount` method `getDescription()` has been marked as deprecated. Use the `getNotes()` or `getNotesObject()` method instead.
* The `CustomFieldValueItem` methods `getParent` and `setParent` have been marked as deprecated. Use the `getParentUniqueID` and `setParentUniqueID` methods instead.
* JSON output from MPXJ now includes more detail for custom field definitions read from MPP files.
* When reading a PMXML file, populate the Early/Late Start/Finish date attributes from the Remaining Early/Late Start/Finish date attributes.
* Fix an issue reading WBS ID for P3 and SureTrak schedules.

## 12.8.1 (2024-03-11)
* Improve reading resource assignments from certain FastTrack FTS files.

## 12.8.0 (2024-03-04)
* Add experimental support for reading Deltek Open Plan BK3 files.
* Implemented the `Relation.Builder` class.
* Marked the `Relation(Task,Task,RelationType,Duration)` constructor as deprecated, use the `Relation.Builder` class instead.
* Marked the `RelationContainer.addPredecessor(Task,Task,RelationType,Duration)` method as deprecated, use the `RelationContainer.addPredecessor(Relation.Builder)` method instead.
* Marked the `Task.addPredecessor(Task,RelationType,Duration)` method as deprecated, use the `Task.addPredecessor(Relation.Builder)` method instead.
* Add a notes attribute to the `Relation` class and ensure that it is read from and written to P6 schedules.
* Read the Relationship Lag Calendar setting from Phoenix 5 files. (Contributed by Rohit Sinha)
* Don't write a material label to an MSPDI file for a resource which isn't a material.
* Update representation of Work Variance when writing MSPDI files to more closely match output from Microsoft Project.
* Updated to ensure that when schedules are read from XER files or P6 databases, labor and nonlabor work amounts are combined for the Actual, Remaining and Planned work attributes. This is now consistent with the existing behavior when reading PMXML files.
* Added support for new Task attributes Actual Work Labor, Actual Work Nonlabor, Remaining Work Labor, Remaining Work Nonlabor, Planned Work Labor, Planned Work Nonlabor, when reading and writing P6 schedules.
* Update default `readAll` method on reader classes to ensure that if the reader is unable to read any schedule data, an empty list is returned rather than a list containing `null`.
* Ensure that Task Start and Finish dates are both the same when reading milestones from PMXML files, and that the correct date is used depending on whether we have a Start Milestone or a Finish Milestone.

## 12.7.0 (2024-02-07)
* Added support for reading and writing the project property Baseline Calendar Name to and from MPP and MSPDI files.
* Ensure Start Variance and Finish Variance are read from and written to MSPDI files in the correct format.
* Improve accuracy of large Work Variance values read from MSPDI files.
* Add support for the Calendar GUID attribute, which is read from MPP and MSPDI files, and written to MSPDI files.
* Ensure Activity Codes are available when reading Phoenix PPX files even if they are also being used to construct the task hierarchy.
* Ensure Activity Codes Values are populated when reading Phoenix PPX files. (Contributed by Rohit Sinha)
* When writing an MSPDI file, derive the TimephasedData Unit attribute from the duration of the timephased data item.
* Fixed an issue with the `ProjectCalendar.getPreviousWorkFinish` method when called with a time which was already at the end of a period of work.
* Ensure that the `proj_node_flag` is set for the root WBS node when writing XER files.

## 12.6.0 (2024-01-22)
* Updated PMXML schema to version 23.12.
* Ensure that baselines in PMXML files written by Oracle Primavera Cloud are read.
* Fix an issue reading certain XER files and P6 databases where activities lost the relationship with their parent WBS entry.
* Added `ResourceAssignment.getEffectiveCalendar` method.
* Deprecated `ResourceAssignment.getCalendar` method, use `getEffectiveCalendar` method instead.
* Improved reading timephased baseline work from MPP files.
* Added new versions of the `TimephasedUtility.segmentBaselineWork` and `segmentBaselineCost` methods which take a `ProjectCalendar` instance as the first argument rather than a `ProjectFile` instance.
* Deprecated the `TimephasedUtility.segmentBaselineWork` and `segmentBaselineCost` methods which take a `ProjectFile` instance as the first argument.
* Added a new version of the `ProjectCalendar.getDate()` method which just takes a date and a duration as its arguments. This method handles both positive and negative durations.
* Marked the original version of the `ProjectCalendar.getDate()` method as deprecated. Use the new version instead.
* Improve recognition of task splits when reading MPP and MSPDI files.

## 12.5.0 (2023-12-18)
* Add support for the following Resource Assignment attributes: Remaining Early Start, Remaining Early Finish, Remaining Late Start, and Remaining Late Finish.
* Ensure that the Resource Assignment attributes Remaining Early Start and Remaining Early Finish are read from and written to PMXML files.
* Ensure that the Resource Assignment attributes Remaining Early Start, Remaining Early Finish, Remaining Late Start, and Remaining Late Finish are read from and written to XER files.
* Improve accuracy of reading and writing the `ProjectProperties` Relationship Lag Calendar attribute for PMXML files.
* All P6 scheduling and leveling options which were previously made available via the `ProjectProperties` custom properties map are now deprecated. These properties now have individual getter and setter methods available on the `ProjectProperties` class. Note: this may be a breaking change if you were creating schedules from scratch, populating the custom properties map, then writing PMXML or XER files. In this case you will need to update your code, for all other use cases your code will continue to work unchanged until the next major version of MPXJ.
* Added support for reading and writing the `ProjectProperties` attributes Baseline Type Name, Baseline Type Unique ID, and Last Baseline Update Date for baseline projects in PMXML files.
* When reading projects from PMXML files, if the creation date attribute is not present in the file fall back to populating the `ProjectProperties` creation date attribute with the PMXML date added attribute.
* When writing PMXML files, ensure the date added attribute for projects is populated with the creation date.
* Add the `CustomFieldContainer.remove` method to allow field configurations to be removed.
* Updated the `UserDefinedFieldContainer.remove` method to ensure that any associated field configuration is removed from the `CustomFieldContainer`.
* Ensure that Microsoft Project's "unknown" resource (with Unique ID zero) is not exported to XER files.
* Ensure that resource assignments which are not associated with an Activity or a Resource are not written to XER files.
* Durations are written to PMXML files in hours. We now round to 2 decimal places to allow minutes to be represented, and avoid unnecessary precision.
* Currency amounts written to PMXML files are now rounded to 8 decimal places to more closely match the behavior of P6, and avoid unnecessary precision.
* Decimal amounts other than currency and duration are written to PMXML files with 15 decimal places to more closely match the behavior of P6.
* Fix an issue reading ConceptDraw calendars.
* Fixed a misspelled field name in the JSON output (Contributed by Daniel Taylor).
* Improved handling of the Resource Assignment Planned and Remaining Units and Units per Time attributes read from and written to P6 schedules.
* Added support for the following project properties: Activity ID Prefix, Activity ID Suffix, Activity ID Increment and Activity ID Based On Selected Activity, and ensure these are read from and written to P6 schedules.

## 12.4.0 (2023-11-23)
* Added support for the WBS Code Separator attribute to `ProjectProperties`.
* Avoid creating duplicate `ActivityCodeValue` instances when reading Asta PP files.
* Added a new version of the `ProjectFile.expandSubprojects` method which takes a `boolean` argument indicating if external tasks should be removed. Passing `true` to this method will recreate predecessor and successor relationships using the original tasks rather than the placeholder external tasks, and will remove the external tasks.
* Marked the `ProjectFile.expandSubprojects()` method as deprecated, use the new version which takes a `boolean` argument instead.
* Ensure the `ProjectProperties` name attribute is set correctly when reading XER files and P6 databases.
* The `ProjectEntityContainer` method `renumberUniqueIDs` has been marked as deprecated.
* The `ProjectEntityContainer` method `getNextUniqueID` has been marked as deprecated. Use `ProjectFile.getUniqueIdObjectSequence(class).getNext()` instead.
* The `ProjectEntityContainer` method `updateUniqueIdCounter` has been marked as deprecated as it is no longer required.
* The `ProjectFile` method `updateUniqueIdCounters` has been marked as deprecated as it is no longer required.
* The `ObjectSequence` method `reset` has been marked as deprecated as it is no longer required.
* When creating a `Location` instance using the `Builder` class, a Unique ID will be generated if one is not supplied.
* The no-arg `Location.Builder` constructor has been marked a deprecated. Use the constructor which requires a `ProjectFile` instance instead.
* Implemented the `ExpenseItem.Builder` class.
* Marked the `ExpenseItem(task)` constructor as deprecated, use the `ExpenseItem.Builder` class instead.
* Marked all `ExpenseItem` setter methods a deprecated. The `ExpenseItem` class will be immutable in the next major release.
* Marked no-arg `UnitOfMeasure.Builder()` constructor as deprecated, use the `UnitOfMeasure.Builder(ProjectFile)` constructor instead.
* Implemented the `Step.Builder` class.
* Marked the `Step(task)` constructor as deprecated, use the `Step.Builder` class instead.
* Marked all `Step` setter methods a deprecated. The `Step` class will be immutable in the next major release.
* Marked the `NotesTopic` constructor as deprecated, use the `NotesTopic.Builder(ProjectFile)` constructor instead.
* Implemented the `ExpenseCategory.Builder` class.
* Marked the `ExpenseCategory` constructor as deprecated, use the `ExpenseCategory.Builder` class instead.
* Implemented the `CostAccount.Builder` class.
* Marked the `CostAccount` constructor as deprecated, use the `CostAccount.Builder` class instead.
* Implemented the `ActivityCodeValue.Builder` class.
* Marked the `ActivityCodeValue` constructor as deprecated, use the `ActivityCodeValue.Builder` class instead.
* Marked the `ActivityCodeValue.setParent` method as deprecated, use the `ActivityCodeValue.Builder` class instead.
* Marked the `ActivityCode.addValue` method as deprecated, use the `ActivityCodeValue.Builder` class instead to create an `ActivityCodeValue` instance and add it directly to the list held by the parent `ActivityCode`.
* Implemented the `ActivityCode.Builder` class.
* Marked the `ActivityCode` constructor as deprecated, use the `ActivityCode.Builder` class instead.
* Only predecessor `Relation` instances are now stored in `RelationContainer`, successors are generated dynamically. You will only notice a difference if you are iterating over the `RelationContainer` collection directly, in which case you will only see predecessors.

## 12.3.0 (2023-11-07)
* Retrieve role availability data when reading a schedule from a P6 database.
* Populate the project's Name and Title attributes when exporting an MSPDI file.
* Ensure the Project ID attribute is populated when writing an XER file.
* Don't include null tasks (blank tasks) when writing an XER file.
* Strip control characters from entity names written to MSPDI files and XER files.
* Ensure resource material labels written to MSPDI files meet Microsoft Project's naming requirements.
* Ensure the activity code value Name attribute is populated when read from an Asta PP file.
* Don't allow multiple values for an activity code when writing XER and PMXML files.
* The MSPDI and MPX writers now dynamically renumber Unique ID values which are too large for Microsoft Project. The original schedule is no longer modified to achieve this.

## 12.2.0 (2023-10-12)
* Add the `UnitOfMeasure` class to represent the unit of measure for a material resource. The unit of measure corresponds to the current "material label" attribute of a resource. The `Resource.getMaterialLabel()` method will now retrieve the label from the `UnitOfMeasure` instance associated with the resource. The `Resource.setMaterialLabel()` method is now deprecated, the `Resource.setUnitOfMeasure()` or `Resource.setUnitOfMeasureUniqueID()` methods should be used instead.
* Unit of measure for material resources are now read from and written to Primavera schedules.
* Improve task duration and percent completion calculation for Asta PP files.
* Improve date parsing when reading XER files written by older versions of P6.
* Added the `setIgnoreErrors` method to the Primavera database reader class, and MSPDI, Schedule Grid, and SDEF file reader classes. The current default behavior of ignoring data type parse errors is unchanged. Calling `setIgnoreErrors(false)` on one of these reader classes will ensure that an exception is raised when a data type parse error is encountered.
* Added the `ProjectFile.getIgnoredErrors()` method. The default behavior for MPXJ reader classes is to ignore data type parse errors. If any errors have been ignored when reading a schedule, details of these errors can be retrieved by calling the `ProjectFile.getIgnoredErrors()` method.
* Handle duplicate relation unique IDs when reading schedules.
* Include resource availability table in JSON output.
* Add the Resource field Default Units, and ensure this field is read and written for P6 Schedules.
* Updated the Resource attribute Max Units to ensure that this is calculated from the resource's availability table. Note that the `Resource.getMaxUnits()` method will return the resource's Max Units attribute for the current date. To retrieve the Max Units for a different date, use the `AvailabilityTable.getEntryByDate()` method.
* Marked the `Resource.setMaxUnits()` method as deprecated. The Max Units attribute is derived from the resource's availability table. Changes to Max Units should now be made by modifying the availability table.
* Updated the Resource attribute Available From to ensure that this is calculated from the resource's availability table. Note that the `Resource.getAvailableFrom()` method will return the resource's Available From attribute for the current date. To retrieve the Available From attribute for a different date, use the `AvailabilityTable.availableFrom()` method.
* Marked the `Resource.setAvailableFrom()` method as deprecated. The Available From attribute is derived from the resource's availability table. Changes to the Available From attribute  should now be made by modifying the availability table.
* Updated the Resource attribute Available To to ensure that this is calculated from the resource's availability table. Note that the `Resource.getAvailableTo()` method will return the resource's Available To attribute for the current date. To retrieve the Available To attribute for a different date, use the `AvailabilityTable.availableTo()` method.
* Marked the `Resource.setAvailableTo()` method as deprecated. The Available To attribute is derived from the resource's availability table. Changes to the Available To attribute  should now be made by modifying the availability table.

## 12.1.3 (2023-09-25)
* Added the Project Properties attribute Relationship Lag Calendar and implemented read and write support for this for P6 schedules. (Contributed by Rohit Sinha).
* Improve compatibility of PMXML files with P6 EPPM by moving the Schedule Options tag.
* Ensure Baseline Projects in PMXML files include Schedule Options and Location Object ID.

## 12.1.2 (2023-09-21)
* Updates to improve compatibility with versions of Java after Java 8.
* Ensure timestamps with fractional sections are read correctly from Phoenix PPX files (Based on a contribution by Rohit Sinha).
* Improve handling of double quotes when reading and writing XER files.
* To allow XER files written by MPXJ to be imported correctly by P6, ensure that they have a single top level WBS entry (Based on a contribution by Alex Matatov)
* Ensure that `ProjectProperties.getCustomProperties()` returns an empty Map rather than returning `null` if no custom properties have been configured.
* Ensure project calendars and project activity codes are nested within the project tag of PMXML files.

## 12.1.1 (2023-08-23)
* Fix an issue preventing native SQLite library from loading when using the .Net version of MPXJ on macOS.

## 12.1.0 (2023-08-22)
* Write schedule options to PMXML and XER files.
* Fix an arithmetic error in RateHelper when converting a rate from minutes to hours.
* Introduced new methods to RateHelper accepting a `TimeUnitDefaultsContainer` argument rather than a `ProjectFile` for greater flexibility. Marked methods taking a `ProjectFile` argument as deprecated.
* Ensure Early Finish and Late Finish are populated for Asta milestones and tasks.
* Don't attempt to calculate total slack if start slack or finish slack are missing.
* Ensure completed tasks are not marked as critical.
* Improve handling of non-standard Boolean values in MPX files.
* Improve Total Slack calculation for P6 projects.
* Handle finish milestones with `null` actual start date for actual duration calculation when reading PMXML files (Contributed by Andrew Marks).

## 12.0.2 (2023-07-25)
* Ensure that the Fixed Cost attribute is rolled up from activities to WBS entries when reading P6 schedules.

## 12.0.1 (2023-07-21)
* Improve resource hierarchy handling.
* Improve handling of external tasks read from MSPDI files.
* Improve handling of resource assignments read from Asta PP files containing multiple baselines.
* Improve filtering to ignore hammock tasks in Asta PP files and ensure that non-hammock items are not incorrectly ignored.
* Improve handling of bars without additional linked data read from Asta PP files.
* Ensure that invalid duplicate Unique ID values encountered when reading schedule data are renumbered to maintain uniqueness.
* Improve reading certain FastTrack FTS files.
* Roll up the expense item at completion values read from P6 schedules to the task Fixed Cost attribute.

## 12.0.0 (2023-06-29)
* NOTE: this is a major version release, breaking changes have been made to the MPXJ API as documented below.
* Timestamps, dates, and times are now represented by `java.time.LocalDateTime`, `java.time.LocalDate` and `java.time.LocalTime` respectively, rather than `java.util.Date` as they were originally.
* For .Net users, new `ToDateTime` and `ToNullableDateTime` extension methods have been provided to convert `java.time.LocalDateTime`, `java.time.LocalDate`, `java.time.LocalTime` to `DateTime` instances.
* For .Net users, new `ToJavaLocalDateTime`, `ToJavaLocalDate` and `ToJavaLocalTime` extension methods have been provided to convert `DateTime` instances to `java.time.LocalDateTime`, `java.time.LocalDate`, and `java.time.LocalTime`.
* The class `net.sf.mpxj.Day` has been replaced by `java.time.DayOfWeek`.
* All code previously marked as deprecated has been removed.
* Added support for reading and writing the Activity attribute "Expected Finish" for P6 schedules.

## 11.5.4 (2023-06-27)
* Improve accuracy of dates read from Synchro, Suretrak and Turboproject files.
* By default ignore errors in individual records read from XER files. This matches P6's behavior when importing XER files. Use the `PrimaveraXERFileReader.setIgnoreErrors` method to change the behavior.

## 11.5.3 (2023-06-19)
* When writing an XER file, provide the necessary default values to allow non-P6 schedules to be successfully imported into P6.
* Ensure multi-day exceptions are written to XER files correctly.
* Ensure GanttProject exception dates are read correctly.
* More closely match the Planner predecessor lag calculation.
* Ensure that `java.sql.Date` values are correctly formatted when writing XER files.
* When reading from a P6 database, check to ensure the location table is present before attemting to read locations.

## 11.5.2 (2023-06-08)
* Improve accuracy of calendar data read from certain Powerproject schedules.
* Improve handling of unusual XER files with calendar time ranges expressed in 12-hour format.
* Correctly parse midnight represented as 24:00:00 from MSPDI files written by certain non-Microsoft Project applications.
* For MSPDI files produced by applications other than Microsoft Project which have an incorrectly nested calendar hierarchy, avoid pruning derived calendars which are referenced elsewhere in the hierarchy.

## 11.5.1 (2023-05-24)
* Improve read performance when working with large schedules.
* Improve read and write performance of code handling resource calendars.
* Updated to use sqlite-jdbc 3.42.0.0

## 11.5.0 (2023-05-19)
* Added the ability to read Subproject data embedded in MSPDI files.
* Added the ability to read timephased baseline work and cost from MSPDI files.
* Added the ability to write timephased baseline work and cost to MSPDI files.
* Improve accuracy of timephased baseline work read from MPP files.
* Ensure that non-recurring calendar exceptions take precedence over recurring calendar exceptions.
* Avoid creating duplicate calendar exceptions when reading Asta PP files.
* Added the Bar Name attribute to Task, which is accessed using the `getBarName` and `setBarName` methods. This is populated with the name of the bar to which a task belongs when reading an Asta Powerproject schedule.
* When reading schedules from XER files and P6 databases, ensure durations without a value are returned as `null` rather than as a zero duration.

## 11.4.0 (2023-05-08)
* Added the "Resource Pool File" attribute to ProjectProperties, which represents the full path of the resource pool used by an MPP file. This attribute is accessible via the `getResourcePoolFile` and `setResourcePoolFile` methods.
* Added the `getResourcePoolObject` method to allow the resource pool file to be located and read
* Added support for reading the task attribute Subproject GUID from MPP files. This attribute can be accessed via the `getSubprojectGUID` and `setSubprojectGUID` methods.
* Added support for the task attribute "External Project". When this attribute is true it indicates that the task represents a subproject. The attribute is accessed via the `getExternalProject` and `setExternalProject` methods.
* When reading an MSPDI file with external task predecessors, MPXJ now attempts to recreate the placeholder external tasks which would be present if the equivalent MPP file was read.
* External task predecessors are now represented when writing an MSPDI file.
* Added the Task method `getSubprojectObject` which allows the caller to retrieve a ProjectFile instance representing the external project linked to a task.
* Added the Task method `expandSubproject`. For task which represent an external project, this method automatically loads the external project and attaches the tasks it contains as children of the current task. This is analogous to the behavior in Microsoft Project where a subproject is expanded to reveal the tasks it contains.
* Added the ProjectFile method `expandSubprojects` which identifies any tasks in the project which represent an external project and expands them, linking the tasks from the external project as children of the task in the parent project. Note that the method works recursively so multiple levels of external tasks will be expanded.
* Updated to ensure that the `internal_name` attribute of a `UserdefinedField` is generated if not present.
* Updated to avoid an exception when reading notebook topics from PMXML files.
* Marked the Task method `setSubprojectName` as deprecated. Use the `setSubProjectFile` method instead.
* Marked the Task method `getSubprojectName` as deprecated. Use `getSubprojectFile` instead.
* Marked the Task method `setExternalTaskProject` as deprecated. Use the `setSubprojectFile` method instead.
* Marked the Task method `getExternalTaskProject` as deprecated. Use the `getSubprojectFile` method instead.
* Marked the ProjectFile method `getSubProjects` as deprecated. Use the subproject attributes on individual tasks instead.
* Marked the Task methods `getSubProject` and `setSubProject` as deprecated. Use the subproject attributes instead.

## 11.3.2 (2023-04-29)
* Improve default values provided for P6 calendars with missing data.
* Implement both "planned dates" and "current dates" strategies for populating P6 baselines.
* Ensure the Project GUID is read from MPP files.

## 11.3.1 (2023-04-21)
* Improve accuracy of resource assignment Actual Start and Actual Finish dates when reading MPP files.
* Avoid generating timephased data for zero duration tasks.
* Improve preservation of custom timephased data start and end times.

## 11.3.0 (2023-04-12)
* Implemented `PrimaveraXERFileWriter` to allow MPXJ to write XER files.
* Updated the `ActivityCode` class to ensure that both the scope Project ID and EPS ID can be represented when reading a P6 schedule. (Potentially breaking change if you were using this class).
* Ensure secondary constraint date and type are written to PMXML files.
* Ensure leveling priority is written to PMXML files.
* Ensure WBS UDF values are written to PMXML files.
* Ensure integer UDF values are read correctly from XER files and P6 databases.
* Add methods to allow the project's default calendar unique ID to be set and retrieved.
* Add method to allow a calendar's parent calendar unique ID to be retrieved.
* Add method to allow a task's parent task unique ID to be retrieved.
* Add methods to allow a resource assignment's role unique ID to be set and retrieved.
* Add methods to allow a resource assignment's cost account unique ID to be set and retrieved.
* Add method to allow a cost account's parent unique ID to be retrieved.
* Add method to allow an expense item's cost account unique ID to be retrieved.
* Add method to allow an expense item's category unique ID to be retrieved.
* Added `WorkContour.isDefault()` method to allow "built in" resource curves/work contours to be distinguished from user defined curves.
* Updated to retrieve the project's start date from Phoenix PPX files (Contributed by Rohit Sinha).
* Provide access to notebook topics from P6 schedules via the `ProjectFile.getNotesTopics()` method.
* Capture unique ID of Activity and WBS notes from P6 schedules.
* Improve the calculation used to determine At Completion Duration of activities when reading XER files and P6 databases.
* Improve representation of certain duration values written to MSPDI files.
* Improve accuracy of certain work calculations where the specified time period does not start with a working day.
* Fix an issue which caused negative timephased work values to be generated when reading certain MPP files.
* Fix an issue reading XER files where the `critical_drtn_hr_cnt` field is expressed a decimal rather than an integer.
* Fix an issue populating the WBS attribute for activities read from certain XER files.

## 11.2.0 (2023-03-13)
* The project property Critical Slack Limit is now represented as a `Duration` rather than as an `Integer`. (Potentially breaking change if you were using this property directly).
* `TaskType` is now a simple enum with all Microsoft Project specific functionality moved into `TaskTypeHelper`. (Potentially breaking change if you were using the `TaskType` methods `getInstance` or `getValue` in your code)
* When reading the task type from P6 schedule the mapping to the MPXJ `TaskType` enum has been updated to more closely match P6. The main changes are that the P6 type "Fixed Units" now maps to `TaskType.FIXED_WORK` and the "Fixed Duration & Units" type now maps to a new enumeration value `TaskType.FIXED_DURATION_AND_UNITS`.
* Added support for reading project calendar exceptions from Phoenix schedules (based on a contribution by Rohit Sinha).
* The Resource attribute Active now defaults to true if the schedule being read doesn't support or contain a value for this attribute.
* Add support for reading and writing the Resource's Active flag for P6 schedules.
* Add support for reading and writing the Resource's Default Units/Time value for P6 schedules.
* Add support for reading and writing the Project's Critical Slack Limit value for P6 schedules.
* Fixed an issue reading certain types of Enterprise Custom Fields containing date values.
* Ensure activity code value parent can be set to null.
* Improved existing .Net extension methods and added support for more types.
* Added NuGet package icon
* Simplified  NuGet packaging

## 11.1.0 (2023-02-15)
* Write activity code definitions and activity code assignments to PMXML files.
* Added support for "secure" and "max length" attributes to the `ActivityCode` class.
* Added `getChildCodes` method to `ActivityCode` and `ActivityCodeValue` to make it easier to traverse activity code values hierarchically.
* Added `setDescription` method to `Step` class to make it simpler to add a plan text description.

## 11.0.0 (2023-02-08)
* User defined fields read from P6, Asta and GanttProject schedules are now represented by instances of `UserDefinedField`. They will no longer be mapped to custom field instances.
* Enterprise Custom Fields read from MPP and MSPDI files are now represented by instances of `UserDefinedField`.
* When writing MSPDI files, UserDefinedField instances which were originally read from enterprise custom fields will be written to the MSPDI file as enterprise custom fields.
* When writing MSPDI files, UserDefinedField instances which were from applications other than Microsoft Project will automatically be mapped to available custom fields.
* When writing MPX files, UserDefinedField instances will automatically be mapped to available custom fields.
* The `UserDefinedField` type implements the `FieldType` interface and so can be used with the `FieldContainer` `get` and `set` methods to work with the contents of the user defined fields.
* The `ProjectFile.getUserDefinedFields()` method has been added to provide access to all user defined fields defined in the project.
* The `CustomFieldContainer` returned by `ProjectFile.getCustomFields()` will contain entries for all `UserDefinedField` instances.
* The various `getFieldTypeByAlias` and `getFieldByAlias` methods will retrieve user defined fields by name.
* Added the convenience method `ProjectFile.getPopulatedFields()` to retrieve details of all populated fields across the project. This avoids the caller having to individually retrieve the populated fields from the tasks container, resource container and so on.
* Updated the `getPopulatedFields` methods to return a `Set` of `FieldType` rather than a `Set` of `TaskField`, `ResourceField` etc.
* The various `getPopulatedFields` methods will include instances of `UserDefinedField` in the returned collection if relevant.
* All `ENTERPRISE_CUSTOM_FIELDn` values have been removed from the `TaskField`, `ResourceField`, `AssignmentField` and `ProjectField` enumerations.
* The `getEnterpriseCustomField` and `setEnterpriseCustomField` methods have been removed from `ProjectProperties`, Task`, `Resource` and `ResourceAssignment`.
* Project UDFs are now read from P6 schedules.
* Project UDFs are now written to PMXML files.
* All code previously marked as deprecated has been removed.

## 10.16.2 (2023-01-29)
* Updated to improve reading resource attributes from certain MPP14 files.

## 10.16.1 (2023-01-26)
* Updated to make resource curve definitions (work contours) available in the `WorkContourContainer`. This container is accessed using the `ProjectFile.getWorkContours()` method.

## 10.16.0 (2023-01-24)
* Improve accuracy when normalising timephased data.
* Add support for reading activity steps from XER files, PMXML files and Primavera databases.
* Add support for writing activity steps to PMXML files.
* Updated PMXML schema to version 22.12.
* Updated methods in the `GanttBarCommonStyle` and `GanttBarStyle` classes to use a `FieldType` instance rather than a `TaskField` instance to allow more flexibility. (Note: this may be a breaking change if you are currently using these classes.)
* Optionally include some Microsoft Project layout data in JSON output.

## 10.15.0 (2023-01-11)
* Avoid writing invalid characters to PMXML, MSPDI and Planner XML files.
* Improve handling of slack values for schedules which only contain a value for total slack.
* Add support for reading constraint type and constraint date from Phoenix schedules (based on a contribution by Rohit Sinha).
* Improve timephased data calculation when assignment has zero units.
* Improve handling of very large duration values when reading and writing MSPDI files.
* Ensure the Task attributes Active, Constraint Type, Task Mode, and Type always have a value.
* Ensure the Resource attributes Type, Calculate Costs from Units, and Role always have a value.
* Ensure the Resource Assignment attributes Calculate Costs from Units, Rate Index, and Rate Source always have a value.
* Add version number constant to the Java source, accessible as `MPXJ.VERSION`.
* Ensure that UDF values are read for WBS entries in PMXML files.
* Avoid writing duplicate resource assignments to MPX files.

## 10.14.1 (2022-11-25)
* Fix CVE-2022-41954: Temporary File Information Disclosure Vulnerability (Contributed by Jonathan Leitschuh)

## 10.14.0 (2022-11-21)
* Handle missing default calendar when reading a PMXML file.
* When reading an MPP file using a file name or `File` instance, ensure a more memory-efficient approach is used.
* Improve reading certain FastTrack FTS files.
* Improve generation of timephased data where working time ends at midnight.
* Improve generation of timephased data for tasks with a calendar assigned.

## 10.13.0 (2022-11-16)
* Add support for reading a resource assignment's cost account from P6 schedules.
* Add support for writing a resource assignment's cost account to PMXML files.
* Read resource assignment custom field definitions present in MPP14 files.
* Improve identification of deleted resources when reading MPP9 files.
* Ensure tasks with task calendars in MPP files are handled correctly when generating timephased data.
* Improve generation of timephased data for material resource assignments.
* Improve accuracy of timephased data when reading certain MPP files.

## 10.12.0 (2022-11-01)
* Added the Resource Assignment attribute Calculate Costs From Units, and added read and write support for Primavera schedules.
* Added the Resource attribute Calculate Costs From Units, and added read and write support for Primavera schedules.
* Added the Resource and Role attribute Sequence Number, and added read and write support for Primavera schedules.
* Added the WBS attribute Sequence Number, and added read and write support for Primavera schedules.
* Ensure activity type is read from Phoenix schedules. (Contributed by Christopher John)
* Deprecate the `CostAccount` method `getSequence` and replace with `getSequenceNumber` to improve naming consistency.
* Deprecate the `ExpenseCategory` method `getSequence` and replace with `getSequenceNumber` to improve naming consistency.
* Avoid possible ArrayIndexOutOfBoundsException when reading GUID values from MPP files (Contributed by Rohit Sinha).

## 10.11.0 (2022-09-27)
* Deprecated the `Resource` methods `getParentID` and `setParentID`. Replaced with `getParentResourceUniqueID` and `setParentResourceUniqueID` for clarity and consistency.
* Added the `Resource` methods `setParent` and `getParent`.
* Added the `ChildResourceContainer` interface and `ResourceContainer.updateStructure` method to ensure that resources can be accessed hierarchically when reading a schedule.
* Added the `ResourceAssignment` methods `getFieldByAlias` and `setFieldByAlias` to simplify working with custom fields, and mkae the API consistent with existing methods on `Task` and `Resource`.
* Added the `TaskContainer` methods `getCustomFields` and `getFieldTypeByAlias` to simplify access to task custom fields.
* Added the `ResourceContainer` methods `getCustomFields` and `getFieldTypeByAlias` to simplify access to resource  custom fields.
* Added the `ResourceAssignmentContainer` methods `getCustomFields` and `getFieldTypeByAlias` to simplify access to resource assignment custom fields.
* Added the `getCustomFieldsByFieldTypeClass` method to `CustomFieldContainer` to allow retrieval of custom field details by parent class.
* Deprecated the `CustomFieldContainer` method `getFieldByAlias` to be replaced by `getFieldTypeByAlias` to provide a more consistent method name.
* Don't attempt to write unknown extended attributes to MSPDI files.
* Don't populate graphical indicator data if the graphical indicator is not enabled.
* Don't set custom field aliases to empty strings.
* Added the `CustomFieldContainer` method `add`.
* Deprecated the `CustomFieldContainer` method `getCustomField`, which is replaced by the `get` method (which returns `null` if the field type is not configured) and the `getOrCreate` method (which will return an existing configuration or create a new one if the requested field does not yet have a configuration).

## 10.10.0 (2022-09-13)
* Add an option to import Phoenix schedules as a flat set of tasks with separate activity codes, rather than creating a hierarchy of tasks from the activity codes. Note the default is to disable this behavior so existing functionality is unchanged. (Contributed by Christopher John)
* Add a `setProperties` method to reader classes to allow configuration to be supplied via a `Properties` instance rather than having to call setter methods. Properties passed to the `UniversalProjectReader` version of this method will be forwarded to the reader class `UniversalProjectReader` chooses to reader the supplied file. Properties for multiple reader classes can be included in the `Properties` instance, each reader class will ignore irrelevant properties.
* Added the `get` method to `Task`, `Resource`, `ResourceAssignment` and `ProjectProperties` as a replacement for the `getCurrentValue` method. The new `get` method is paired with the existing `set` method to provide read and write access to attributes of these classes. This change is intended to improve the interfaces to these classes by making them more consistent, and thus easier to understand.
* Deprecated the `getCurrentValue` method on the `Task`, `Resource`, `ResourceAssignment` and `ProjectProperties` classes. Use the new `get` method instead.
* Add getter and setter methods for the Resource attributes Cost Center, Budget Cost, Budget Work, Baseline Budget Cost, Baseline Budget Work, Baseline Budget Cost 1-10, and Baseline Budget Work 1-10.
* Add getter and setter methods for the Task attributes Response Pending, Scheduled Start,  Scheduled Finish, Scheduled Duration, Budget Cost, Budget Work, Baseline Budget Cost, Baseline Budget Work, Baseline Budget Cost 1-10, and Baseline Budget Work 1-10.
* Added support for the Resource Cost Centre attribute for MSPDI files.
* Move MPP file-specific functionality for determining baseline values from the Task class into the MPP reader class.
* Improve handling of the TaskMode attribute.
* Don't set a Task's Critical attribute unless we have valid slack values.
* Ensure `ResourceAssignment` calculated fields are returned correctly when using the `getCurrentValue` method.
* Ensure `ProjectProperties` calculated fields are returned correctly when using the `getCurrentValue` method.
* Updated to use jsoup 1.15.3

## 10.9.1 (2022-08-31)
* Ensure monthly and yearly recurrences are calculated correctly when the supplied start date is the same as the first recurrence date (Contributed by Rohit Sinha).
* Add support for reading task calendars from Phoenix files (Contributed by Rohit Sinha).
* Improve reliability of ProjectCleanUtility when using the replacement strategy.

## 10.9.0 (2022-08-23)
* Added the `ResourceAssignment.getEffectiveRate` method to allow the cost rate effective on a given date for a resource assignment to be calculated. For P6 schedules this will take account of the cost rate configuration included as part of the resource assignment.
* For P6 schedules, the `ResourceAssignment.getCostRateTable` method now takes in account any cost rate configuration details from the resource assignment when determining which table to return.
* A resource's Standard Rate, Overtime Rate and Cost per Use are now all derived from the resource's cost rate table, and not stored as attributes of the resource itself.
* The resource methods `setStandardRate`, `setOvertimeRate`, and `setCostPerUse` have been deprecated. These attributes can now only be set or updated by modifying the resource's cost rate table.
* When writing MPX files, only include attributes which have a non-empty, non-default value in at least one task or resource.
* When writing MPX files, ensure attributes which have calculated values are used.
* Add support for reading a resource assignment's rate type from P6 schedules. The rate type is accessed via the `ResourceAssignment.getRateIndex` method. The value returned by this method can be used to select the required rate using the `CostRateTableEntry,getRate` method.
* Add support for writing a resource assignment's rate type to PMXML files.
* Add support for reading a resource assignment's role from P6 schedules. The role is accessed via the `ResourceAssignment.getRole` and `ResourceAssignment.setRole` methods.
* Add support for writing a resource assignment's role to PMXML files.
* Add support for reading a resource assignment's override rate (Price / Unit) from P6 schedules. The rate is accessed via the `ResourceAssignment.getOverrideRate` and `ResourceAssignment.setOverrideRate` methods.
* Add support for writing a resource assignment's override rate (Price / Unit) to PMXML files.
* Add support for reading a resource assignment's rate source from P6 schedules. The rate source is accessed via the `ResourceAssignment.getRateSource` and `ResourceAssignment.setRateSource` methods.
* Add support for writing a resource assignment's rate source to PMXML files.

## 10.8.0 (2022-08-17)
* When reading P6 schedules, all five cost rates for a resource are now available via the `CostRateTableEntry.getRate` method.
* All five rates from each cost rate table entry can now be written to PMXML files.
* When reading files written by Microsoft Project, resource rate values now use the same units as seen by the end user rather than defaulting to hours as was the case previously. (For example, if the user sees $8/day in the source application, you will receive a Rate instance of $8/day rather than $1/hr).
* The values for a resource's standard rate, overtime rate, and cost per use attributes are now derived from the cost rate table. The values stored on the resource itself are only used if a cost rate table for the resource is not present.
* The Resource methods `getStandardRateUnits` and `getOvertimeRateUnits` are deprecated. Use the `getStandardRate` and `getOvertimeRate` methods to retrieve a `Rate` instance which will include the units for these rates.
* The Resource methods `setStandardRateUnits` and `setOvertimeRateUnits` are deprecated. Supply `Rate` instances to the `setStandardRate` and `setOvertimeRate` methods with the required units instead.
* The CostRateTableEntry methods `getStandardRateUnits` and `getOvertimeRateUnits` are deprecated. Use the `getStandardRate` and `getOvertimeRate` methods to retrieve a `Rate` instance which will include the units for these rates.
* Ensure rates are formatted "per hour" when writing MSPDI and PMXML files.
* Include cost rate tables in JSON output.

## 10.7.0 (2022-08-09)
* Use Jackcess to read Asta MDB and Microsoft Project MPD files. This allows these file to be read on platforms other than Windows.
* Improve support for reading correctly typed values for enterprise custom fields from MPP files.
* Improve array index validation when reading GUID values from MPP files.

## 10.6.2 (2022-06-29)
* Ensure `ProjectCleanUtility` can load dictionary words from distribution jar.
* Improve handling of calendars without days read from PMXML files.

## 10.6.1 (2022-06-14)
* Updated to use POI 5.2.2
* Updated to use sqlite-jdbc 3.36.0.3
* Updated to use jsoup 1.15.1

## 10.6.0 (2022-06-08)
* Added support for reading and writing the unique ID of P6 user defined fields via new `getUniqueID` and `setUniqueID` methods on `CustomField (based on a suggestion by Wes Lund).
* Added support for reading and writing scope, scope ID, and sequence number attributes for activity codes (based on a suggestion by Wes Lund).
* Added support for reading and writing sequence number and color attributes for activity code values (based on a suggestion by Wes Lund).
* Added `isWorking` method to `ProjectCalendarException` to make it clearer how to determine if the exception changes the dates it is applied to into working or non-working days.
* Improve reading task start from certain Planner files.
* Improve reading predecessor lag values from Planner files.
* Ensure calendar hierarchy is written correctly to Planner files.
* Don't write null tasks to Planner files as Planner will not read files which contain them.
* When writing Planner file, ignore constraint types which Planner can't represent.
* Don't write emply predecessor lists to Planner files.
* Improve handling of lag duration when writing Planner files.
* Improve ProjectCalendar start date calculation when we have long runs of non-working days.
* Performance enhancement for timephased data normalisation.

## 10.5.0 (2022-05-24)
* The `ProjectCalendarWeek` methods `addCalendarHours()`, `attachHoursToDay`, `removeHoursFromDay` have been removed. Use `addCalendarHours(day)`, `removeCalendarHours(day)` instead. (Note: this will be a breaking change if you were using the original methods to create or modify a schedule)
* The `ProjectCalendar` methods `attachHoursToDay` and `removeHoursFromDay` have been removed. Use the `addCalendarHours` and `removeCalendarHours` methods instead. (Note: this will be a breaking change if you were using the original methods to create or modify a schedule)
* The class hierarchy for `ProjectCalendarHours` and `ProjectCalendarException` has been simplified, but there should be no impact for uses of these classes.
* The `ProjectCalendarHours` class now implements the `List` interface. Methods in this class not part ofthe `List` interface have been deprecated in favour of the equivalent `List` methods.
* Updated `MPXWriter` to ensure: calendar names are quoted if necessary, all calendars have names, all calendar names are unique.
* Updated `MPXReader` to recognise `wk` as a valid time unit.
* Updated `MPXWriter`, `PrimaveraPMFileWriter`, `SDEFWriter` and `PlannerWriter` to ensure any working weeks defined by a calendar are represented by exceptons.
* Updated `MSPDIWriter` to ensure any working weeks defined by a calendar are represented in the "legacy" exception definition used by Microsoft Project prior to 2007.
* Updated `SDEFWriter` to ensure: only relevant calendars are written, and derived calendars are flattened.
* When reading Planner schedules MPXJ will no longer create an "artificial" resource calendar for each resource. Resources will be linked directly to the calendar used in the original schedule.
* Add support for reading the P6 calendar type and personal calendar flag from P6 schedules.
* Add support for writing the calendar type and personal calendar flag to PMXML files.
* Updated the calendar class hierarchy: `ProjectCalendar` and `ProjectCalendarWeek` both now inherit from a new class `ProjectCalendarDays`. Note that `ProjectCalendar` is no longer a subclass of `ProjectCalendarWeek`.
* The `getHours` and `isWorkingDay` methods have been moved up to `ProjectCalendar` from the `ProjectCalendarWeek` class.
* The `ProjectCalendar` method `copy` has been deprecated, without replacement.
* Added a `getWork` method to `ProjectCalendar` which calculates the amount of work given a `Day` instance.
* Added `removeWorkWeek` and `removeCalendarException` methods to `ProjectCalendar`.
* Recurring exceptions are now added to a `ProjectCalendar` using the `addCalendarException` method which takes a `recurringData` instance its argument.
* The `ProjectCalendarException` method `setRecurringData` has been removed, recurring exceptions should be added using the `addCalendarExcepton` method described above. (Note: this will be a breaking change if you were creating recurring exceptions)

## 10.4.0 (2022-05-05)
* Remove `getParent`, `setParent`, and `isDerived` from `ProjectCalendarWeek`. (Note: this will be a breaking change if you were working with `ProjectCalendarWeek` directly).
* The `ProjectProperties` methods `getDefaultCalendarName()` and `setDefaultCalendarName()` have been deprecated. Use `getDefaultCalendar()` and `setDefaultCalendar()` instead.
* Ensure that percent complete values can be read from MSPDI files even if the values are decimals.
* Improve handling of the default calendar when reading certain MSPDI files.
* Improve reading certain Phoenix PPX files.
* Improve reading certain FastTrack FTS files.
* Improve formatting of time project properties when written to JSON.
* Improve reading MPP files generated by Microsoft Project 16.0.15128.20158 and later versions.

## 10.3.0 (2022-04-29)
* General improvements to make calendar data read from different file formats more consistent.
* When reading P6 and Powerproject schedules MPXJ will no longer create an "artificial" resource calendar for each resource. Resources will be linked directly to the calendars they use in the original schedule.
* Update `MPXWriter` and `MSPDIWriter` to ensure that, when written, calendars are correctly structured in the form required by Microsoft Project.
* `JsonWriter` now includes calendar data as part of its output.
* The `ProjectCalendar` methods `setMinutesPerDay`, `setMinutesPerWeek`, `setMinutesPerMonth` and `setMinutesPerYear` have been deprecated, use `setCalendarMinutesPerDay`, `setCalendarMinutesPerWeek`, `setCalendarMinutesPerMonth` and `setCalendarMinutesPerYear` instead.
* The ProjectCalendar method `setResource` has been deprecated and will not be replaced. Use the Resource method `setCalendar` or `setCalendarUniqueID` to link a calendar with a resource.
* The ProjectCalendar method `getResource` has been deprecated. Use the `getResources` method instead to retrieve all resources linked with a calendar.
* The `Resource` methods `addResourceCalendar`, `setResourceCalendar`, `getResourceCalendar`, `setResourceCalendarUniqueID` and `getResourceCalendarUniqueID` have been deprecated and replaced by `addCalendar`, `setCalendar`, `getCalendar`, `setCalendarUniqueID` and `getCalendarUniqueID` respectively.

## 10.2.0 (2022-03-06)
* Improvements to writing currency, rate and units amounts to MSPDI files.
* When reading MPP and MSPDI files, calendar exceptions representing a single range of days, but defined as a recurring exception are converted to a range of days, removing the unnecessary recurring definition.
* Added `StructuredTextParser` to replace original code handling calendar data, project properties and curve data read from XER files and Primavera databases. Can also be used to extract data from Primavera Layout Files (PLF).
* Improve recognition of contoured resource assignments read from MPP files.
* Improve retrieval of resource assignment confirmed, response pending, linked fields, and team status pending flags from certain MPP files.

## 10.1.0 (2022-01-29)
* Improve PMXML file compatibility with P6.
* Strip any trailing invalid characters from text read from FTS files.
* Ensure all tasks read from Powerproject and Project Commander have unique IDs.
* Correct expansion of exceptions from a weekly recurring calendar exception.
* Ensure that expanded calendar exceptions are written to file formats which do not support recurring exceptions.
* Ensure that start and finish dates are set when reading milestones from GanttProject files.

## 10.0.5 (2022-01-11)
* Ensure `Task.getActivityCodes()` returns an empty list rather than `null` when no activity code values have been assigned.
* Default to using ASCII when reading and writing SDEF files, as per the SDEF specification.
* Provide methods to set and get the charset used when reading and writing SDEF files.

## 10.0.4 (2022-01-07)
* Added support for reading Code Library values (as Activity Codes) from Powerproject files.
* Updated `ProjectCleanUtility` to provide a "replace" strategy alongside the original "redact" strategy.

## 10.0.3 (2021-12-22)
* Fix issue with null tasks from certain MPP files introduced in 10.0.2.

## 10.0.2 (2021-12-16)
* Improve identification of null tasks for certain MPP files.

## 10.0.1 (2021-12-10)
* Avoid false positives when detecting password protected MPP files.

## 10.0.0 (2021-12-01)
* Added support for .NET Core 3.1
* Nuget packages now explicitly target .NET Framework 4.5 (`net45`) and .NET Core 3.1 (`netcoreapp3.1`)

## 9.8.3 (2021-11-30)
* Improve reliability when reading certain Phoenix files.
* Ensure multiple trailing nul characters are stripped from text when reading schedules from a Primavera database.

## 9.8.2 (2021-11-01)
* Improve accuracy of identifying null tasks in certain MPP files.
* Improve accuracy of identifying valid tasks in certain MPP files.
* Ensure hierarchical outline code values are read correctly from MSPDI files.
* Improve support for files produced by recent versions of FastTrack.

## 9.8.1 (2021-10-13)
* Added support for Phoenix 5 schedules.
* Improve handling of null tasks read from MPP files.

## 9.8.0 (2021-09-30)
* Introduced the BaselineStrategy interface and implementing classes. (Note: this includes a breaking change if you were using the ProjectFile.setBaseline method and supplying a lambda. You will now need to implement a BaselineStrategy and set this in ProjectConfig before setting a baseline).
* Improved accuracy of baseline attributes for Primavera schedules.

## 9.7.0 (2021-09-28)
* Add Sprint ID and Board Status ID attributes to task.
* Introduce the TimeUnitDefaultsContainer to allow constants for time unit conversions to be obtained from both project properties and calendars.
* Duration attributes are no longer returned as Duration instances by the ruby gem, they are now returned as floating point numbers. By default, durations are expressed in seconds. A new optional argument to MPXJ::Reader.read allows you to change the units used to express durations. (Note: this is a breaking change for users of the ruby gem)
* Update JsonWriter to use a relevant calendar when converting durations.
* Ensure default calendar is set correctly when reading XER and PMXML files, and P6 databases.
* Use default hours per day/week/month/year when reading P6 XER files or databases if these values are not present.
* Ensure that the minutes per day/week/month/year attributes are copied when a calendar is copied.
* When reading P6 schedules, roll up calendar for WBS entries when child activities all share the same calendar.
* Generate missing minutes per day/week/month/year for calendars read from P6 schedules.
* Inherit minutes per day/week/month/year from base calendars (Note: minor method signature changes on ProjectProperties and ProjectCalendar).
* Allow explicit values to be set for project minutes per week and minutes per year.
* Fall back on defaults for project minutes per day/week/month/year attributes.

## 9.6.0 (2021-09-13)
* Add Planned Start and Scheduled Finish to project properties.
* Add attribute_types method to Ruby classes.
* Updated to use POI 5.0.0.
* Corrected source of Must Finish By project property when reading XER files or P6 databases.
* When reading PMXML files, ensure that the activity calendar is set before calculating slack.
* Remove unused field TaskField.PARENT_TASK.
* Ensure task Unique ID and task Parent Unique ID attributes are treated as mandatory when written to JSON.
* Fix an issue with Ruby gem where a task's parent was not being retrieved correctly in some circumstances.

## 9.5.2 (2021-08-22)
* Add Must Finish By date to project properties.
* Add support for the task attributes Longest Path, External Early Start and External Early Finish, and ensure they can be read from P6 schedules.
* Rename ProjectFile.getStartDate() and ProjectFile.getFinishDate() methods for clarity. Original method names are marked as deprecated
* Ensure that all activities in a PMXML file have a CalendarID attribute to ensure compatibility with older versions of P6.
* Ensure that the user's selected progress period is used to set the project's status date attribute when reading Asta PP files.
* Ensure that a task's Complete Through attribute is not advanced to the start of the next working day (the behaviour of Microsoft Project prior to 2007 was to report Complete Through as the start of the next working day. This change ensures MPXJ matches versions of Microsoft Project from 2007 onwards. Previous behaviour can be restored using the ProjectConfig.setCompleteThroughIsNextWorkStart() method).
* Deprecate task getSplitCompleteDuration() and setSplitCompleteDuration() in favour of getCompleteThrough() and setCompleteThrough().
* Improved the implementation of the TaskContainer.synchronizeTaskIDToHierarchy method.
* Update jsoup to 1.14.2.

## 9.5.1 (2021-07-01)
* When applying a baseline using ProjectFile.setBaseline, gracefully handle duplicate task key values.
* Handle missing values populating cost rate table from an MPP file.

## 9.5.0 (2021-06-30)
* Added support for reading baseline data from embedded baselines in PP files.
* Correct resource assignment percent complete values read from PP files.
* JsonWriter no longer writes attribute type information by default. (The original behaviour can be restored by calling setWriteAttributeTypes(true) on your JsonWriter instance).
* The MPXJ Ruby Gem now generates explicit methods to access attributes rather than relying on "method_missing" to intercept and act on attribute access.
* Don't write Assignment Task GUID, Assignment Resource GUID or Resource Calendar GUID to JSON.
* Don't write a value for Assignment Work Contour to JSON if the contour is the default value (i.e. flat).
* Don't write a value for Assignment Resource Request Type to JSON if the type is the default value (i.e. none).
* Don't write a value for Task Earned Value Method to JSON if the method matches the project default.
* Don't write a value for Task Type to JSON if the type matches the project default.
* Stop writing a default value (-1) for Parent Task ID to JSON if the task does not have a parent.
* Stop writing a default value (-1) for Task Calendar ID to JSON if the task does not have a calendar.
* When reading resource assignments from an MPP file, don't record Project's internal representation of a null resource ID (-65535), record the resource ID explicitly as null.
* For MPX and Planner files, don't write resource assignments for the "null" resource.
* Handle missing status date when reading P6 schedules from XER files or database.
* When reading MPP files, treat UUIDs which are all zeros as null.
* Deprecate the 10 Resource Outline Code get and set methods and replace with get and set methods which take an index argument.
* Provide a helper method (PrimaveraHelper.baselineKey) to encapsulate key generation for setting Primavera baselines.

## 9.4.0 (2021-06-11)
* Read custom value lists for resource custom fields from MPP files (based on a suggestion by Markus Höger).
* Added support for reading custom fields from Asta Powerproject files.
* Ensure short data type values are written to JSON files as numeric values.
* Ensure delay data type values are written to JSON files as duration values.
* Don't write zero rates to JSON files.
* Introduced a separator into rate values when written to a JSON file to make it clear that the value is a rate not a duration (for example: 5.00h is now 5.00/h).
* When writing an enum value of a JSON file, ensure we write the original enum name rather than the value return by toString. This provides more meaningful output (Potentially breaking change if you use the Ruby gem or consume the JSON output directly. Affected attributes are project properties: currency symbol position, time format, date format, bar text date format, schedule from, mpx file version; resource attribute: type).
* Ensure invalid cost rate table data is handled gracefully when reading from MSPDI files.
* Handle missing data when reading MSPDI files (based on a contribution by Lord Helmchen).
* Improve population of summary task names when reading from Powerproject PP files.
* Correctly read hierarchical resource outline codes from MPP files (based on a suggestion by Markus Höger).

## 9.3.1 (2021-05-18)
* Preserve multiple assignments between an activity and a resource when reading P6 schedules.
* Renamed WorkContour.isFlat to isContourFlat and WorkContour.isContoured to isContourManual.
* Include an entry for 0% in the WorkContour curve definition.
* Fix an issue where non-working days were not being treated correctly in date calculations if they happen to still have time ranges attached.

## 9.3.0 (2021-05-06)
* Add support for reading roles from P6 databases, XER and PMXML files, and for writing roles to PMXML files. Roles are represented as resources. The new resource Boolean attribute "Role" is used to distinguish between Resource instances which represent resources and those which represent roles.
* Add support for reading resource curves from P6 databases, XER and PMXML files, and for writing resource curves to PMXML files. The WorkContour enum is now a class, and instance of this class are used to represent resource curves. The curves are available via the work contour attribute of resource assignments.
* Corrected the data type of the task physical percent complete attribute.
* Improve handling of non-standard relationship type representations encountered in XER files and P6 databases.

## 9.2.6 (2021-04-26)
* Handle invalid baseline numbers when reading MSPDI files.
* Improve custom field handling when reading GanttProject files.

## 9.2.5 (2021-04-20)
* Add launcher batch file and shell script.
* Improve handling of calculated task attributes when writing a project to a different format.
* Ensure that dates are rolled up to summary tasks when reading FastTrack files.
* Improve support for Synchro 6.3 SP files.

## 9.2.4 (2021-04-09)
* Fix an issue reading resource rate information GanttProject files.

## 9.2.3 (2021-04-08)
* Fix an issue reading Planned Duration from P6 databases and XER files.
* Ensure Duration and Actual Duration are populated for WBS entries when reading P6 schedules.

## 9.2.2 (2021-04-07)
* Fix issue with WBS ordering when writing PMXML files.

## 9.2.1 (2021-04-04)
* Improve Task critical flag calculation when reading PMXML files.
* Improve support for Synchro 6.3 SP files.

## 9.2.0 (2021-03-30)
* Improve accuracy when reading subprojects from MPP files.
* Add Remaining Late Start and Remaining Late Finish attributes to Task.
* Add Critical Activity Type attribute to Project Properties
* Read Remaining Early Start, Remaining Late Start, Remaining Early Finish and Remaining Late finish from and write to PMXML files.
* Read Remaining Late Start and Remaining Late finish from P6 database and XER files.
* Ensure that WBS entries without child activities are not marked as critical.
* Don't attempt to set the critical flag when reading XER and PMXML files where the schedule is using "longest path" to determine critical activities. (MPXJ currently doesn't have enough information to be able to determine the correct value for the critical flag in this situation).
* Ensure cost, duration, date and work attributes are rolled up to WBS entries for P6 schedules read from PMXML files, XER files and P6 databases.
* Populate baseline cost, duration, finish, start and work when reading from XER files, PMXML files and P6 databases where the "Project Baseline" has been set to "Current Project".

## 9.1.0 (2021-03-11)
* Add methods to the ProjectFile class to attach a ProjectFile instance as a baseline. The baselines attached to the ProjectFile will be used to populate the relevant baseline attributes in the current schedule.
* Added experimental support for writing baseline projects to PMXML files. 
* Added the Project GUID attribute.
* When reading PMXML files, the list of projects returned by the readAll method will include any baseline projects present in the file.
* When reading PMXML files which include the current baseline project, use this to populate the relevant baseline attributes in the main schedule.
* The Project Unique ID property is now an integer rather than a string.
* When reading Primavera schedules, populate the project properties Project ID and Baseline Project Unique ID.
* Handle Primavera resource rates which don't have a start or finish date.
* Handle MSPDI files with resource availability tables which don't have a start or finish date.
* Ensure that the Activity ID field is populated consistently for WBS entries in PMXML files compared to the same schedule read from an XER file or P6 database.
* Ensure duration of manually scheduled tasks in MPP files is represented correctly.

## 9.0.0 (2020-02-18)
* NOTE: this release introduces breaking changes!
* All fields which are non-user defined, but were previously being returned by MPXJ as custom fields are now represented as explicit field types. Custom fields now only contain values for user-defined custom fields.
* All code previously marked as deprecated has been removed.
* When reading an XER file or a P6 database, some custom project property names have been updated. LagCalendar is now CalendarForSchedulingRelationshipLag, RetainedLogic is now WhenSchedulingProgressedActivitiesUseRetainedLogic, ProgressOverride is now WhenSchedulingProgressedActivitiesUseProgressOverride, IgnoreOtherProjectRelationships is now WhenSchedulingProgressedActivitiesUseProgressOverride, and StartToStartLagCalculationType is now ComputeStartToStartLagFromEarlyStart.
* Updated PMXML schema to version 20.12.
* Fix an issue where GUID values were not being read correctly from XER files and P6 databases.
* Percent complete type is now available as a task attribute for P6 schedules from any source.
* Ensure that percent complete values are stored in the appropriate attributes when reading P6 schedules. (NOTE: Previously the "reported" percent complete value was stored as the tasks "percent complete" attribute. Now this holds the schedule percent complete value, and the percent work complete and physical percent complete attributes are also populated. To determine which value should be reported for a task, see the "percent complete type" extended field attribute.)
* Correctly handle default calendar when reading and writing PMXML files.
* Update the sort order of WBS entries and activities in PMXML files to match the order exported by P6.
* Match the way P6 exports the WBS code attribute for PMXML files.
* Update the representation of Boolean values when writing PMXML files to match the form exported by P6.
* Set the task type attribute when reading PMXML files.
* Improve duration and actual duration calculations when reading XER files and P6 databases.
* Fix an issue where resource assignment costs were not being read correctly from PMXML files.
* Read and write the suspend date and resume date attributes for PMXML files.
* General improvements to the SDEF writer.
* Updated to rtfparserkit 1.16.0.

## 8.5.1 (2021-01-07)
* Don't write unused enterprise custom field definitions to MSPDI files. This ensures that MS Project will open these files correctly.

## 8.5.0 (2021-01-06)
* Notes in their original format (HTML from P6, RTF from MS Project) can now be retrieved via the getNotesObject method on Task, Resource, and ResourceAssignment. Plain text notes can still be retrieved via the getNotes method. If you were previously using the "preserve note formatting" flag to retrieve the original formated version of a note, you will now need to use the getNotesObject method.
* Write WBS and Activity notes to PMXML files.
* PMXML compatibility improvements to ensure files can be successfully imported into P6.

## 8.4.0 (2020-12-29)
* Previously when reading PMXML files, XER files, and P6 databases, a set of baseline attributes on tasks and assignments (including Start, Finish, Duration, Cost and Work) were being populated with planned values rather than baseline values. These baseline attributes are no longer being set. The values they previously contained are now available as custom fields.
* Read activity notepad entries for XER, PMXML files and P6 databases.
* Read schedule and leveling options from PMXML files and P6 databases.
* Improve support for reading activity cost and work from PMXML files.

## 8.3.5 (2020-12-15)
* Fix CVE-2020-35460: zip slip vulnerability (with thanks to Sangeetha Rajesh S, ZOHO Corporation)

## 8.3.4 (2020-12-10)
* Updated PMXML schema to version 19.12.
* Ensure that we always set the activity planned start and planned finish dates when writing a PMXML file.
* Updated the getPopulatedFields methods to ignore fields with default values.
* Made the Resource ID attribute available as a resource's TEXT1 custom field, with the alias "Resource ID" when reading PMXML and XER files, or from a P6 database. (Note that presently for XER files and P6 databases, the Resource ID value is also read into the initials attribute. This behaviour is deprecated and will be removed in the next major MPXJ release).
* Populate the Resource ID with the value read from a P6 schedule when writing a PMXML file.
* Ensure that the hours per day, week, month and year attributes are read from and written to PMXML files.
* Fix an issue causing the hours per day calendar attribute to be read inaccurately from XER files and P6 databases.
* Read assignment actual overtime cost and work attributes from PMXML files.
* Update calculation of assignment work, cost and units attributes for PMXML files.

## 8.3.3 (2020-11-24)
* Added cost rate table support when reading from and writing to PMXML files.
* Added a getPopulatedFields method to the TaskContainer, ResourceContainer and ResourceAssignmentContainer classes. This will retrieve the set of fields which are populated with a non-null value across the whole project for Tasks, Resources, and ResourceAssignments respectively. 
* Add START_ON, FINISH_ON constraint types. § MANDATORY_START, MANDATORY_FINISH constraint types. MANDATORY_START/FINISH are now represented as MUST_START/FINISH_ON. This change allows users to distinguish between START/FINISH_ON and the MANDATORY_* constraints when reading P6 schedules.
* Improve handling of cost rate tables and availability tables when writing to an MSPDI file.
* Handle P6 databases and XER files with user defined fields of type FT_FLOAT.
* Align invalid XER record behaviour with P6.
* Handle Planner files which don't contain an allocations tag.
* Gracefully handle MPP files with missing view or table data.

## 8.3.2 (2020-10-22)
* Added support for "new tasks are manual" project property (Contributed by Rohit Sinha)
* Improved support for reading and writing outline codes and extended attributes for MSPDI files (Based on a contribution by Dave McKay)
* Improved handling of enterprise custom fields when reading MPP files
* Update Primavera database and XER readers to avoid potential type conversion errors when the caller provides their own field mappings.
* Improve handling of some MPP12 MPP file variants.
* Avoid error when reading timephased data from certain MPP files.
* Gracefully handle MPP files with missing view data.
* Update junit to 4.13.1.

## 8.3.1 (2020-10-14)
* Minor updates to PlannerReader.

## 8.3.0 (2020-10-13)
* Add the "userDefined" attribute to the CustomField class to allow caller to determine if the field has been created by a user or MPXJ.
* Add support for reading expense items, expense categories and cost accounts from XER files, PMXML files and Primavera databases.
* Add support for writing expense items, expense categories and cost accounts to PMXML files.
* Updated the XER file reader to ignore invalid records rather than reporting an error, matching the behaviour of P6
* Updated the XER file reader to ensure that activity suspend and resume dates are read correctly.
* Updated the XER file reader to ensure that if the reader returns the project selected by the caller when the caller supplies a value for project ID.
* Updated PMXML reader to avoid user defined field collisions.
* Updated PMXML reader to add setProjectID and listProjects methods.
* Update the .net extension method ToIEnumerable to work with java.lang.Iterable rather than java.util.Collection

## 8.2.0 (2020-09-09)
* All readers, including the UniversalProjectReader, now support a readAll method. If a file or database contains more than one project the readAll method can be used to retrieve them all in one operation. If the file format doesn't support multiple schedules, readAll will just return a single schedule.
* Add PrimaveraDatabaseFileReader to encapsulate access to SQLite Primavera databases.
* Ensure that the summary flag is true for WBS items in Primavera schedules, even if they have no child activities.
* Ensure that the critical flag is rolled up appropriately to WBS items when reading Primavera schedules.
* Set export flag property when reading projects from a PMXML file.
* Corrected data type of resource assignment Work Contour field.
* Corrected data type of resource fields: BCWS, BCWP, ACWP, SV, CV, and Work Contour.
* Corrected data type of task fields: CV, ACWP, VAC, CPI, EAC, SPI, TCPI, and Work Contour.

## 8.1.4 (2020-08-31)
* Fix CVE-2020-25020: XXE vulnerability (with thanks to Sangeetha Rajesh S, ZOHO Corporation)
* Import milestone constraints from Asta schedules (Contributed by Dave McKay)
* Handle elapsed durations in Asta schedules (Based on a contribution by Dave McKay)
* Correctly determine the constraint type for tasks with ALAP placement with or without predecessors when reading from Asta schedules (Contributed by Dave McKay)
* Gracefully handle a missing table name when reading an XER file.
* Gracefully handle an unexpected calendar data when reading an XER file.
* Correctly handle XER files with multibyte character encoding.
* Import all schedule and leveling options from XER files.
* Ensure project calendars are read from PMXML files.
* Added readAll methods to PrimaveraPMFileReader to allow all projects contained in a PMXML file to be read in a single pass.

## 8.1.3 (2020-06-25)
* Improve reliability when reading custom field values from certain MPP12 files.
* Improve accuracy of activity percent complete when reading from certain XER files or P6 databases.
* Improve accuracy of WBS percent complete when reading from certain XER files or P6 databases.
* Improve accuracy of task durations when reading Asta schedules.
* Fix an issue handling the end date of calendar exceptions when reading Asta schedules.
* Fix an issue with correctly identifying the calendar applied to summary tasks when reading Asta schedules.
* Populate percent complete, duration, actual start, actual finish, early start, late start, early finish and late finish attributes for summary tasks when reading Asta schedules.
* The percent complete value reported for tasks when reading Asta schedules is now Duration Percent Complete. The Overall Percent Complete value originally being returned is available in a custom field. 

## 8.1.2 (2020-06-18)
* Improve detection of unusual MSPDI file variants.
* Updated to read task notes from FastTrack FTS files.

## 8.1.1 (2020-06-17)
* Improve support for Synchro 6.2 SP files.

## 8.1.0 (2020-06-11)
* Experimental support for reading Project Commander schedules.
* Update to use JAXB 2.3.2.
* Avoid failures caused by unreadable OLE compound documents when the UniversalProjectReader is trying to determine the file type.
* Strip trailing ASCII NUL characters from text fields when reading from a Primavera database.
* Improve accuracy of task order when reading Phoenix files.
* Improve accuracy of task data when reading some MPP file variants.
* Improve reliability when reading certain SureTrak files.

## 8.0.8 (2020-04-20)
* Improve handling of numeric character references invalid for XML 1.0 in PMXML files.
* Improve handling of resource calendars read from Planner files.
* Improve handling of resource calendars read from MPX files.
* Ignore the milestone flag when reading MPX files if the task has a non-zero duration.
* Ensure JSON files can be written when Unique ID predecessor/successor attributes have been read from an MPX file.

## 8.0.7 (2020-04-17)
* Updated to rtfparserkit 1.15.0.
* Improve handling of PMXML files with empty calendar exception time ranges.

## 8.0.6 (2020-03-05)
* Updated to use POI 4.1.2.
* Improve handling of some XER file variants.

## 8.0.5 (2020-02-07)
* Allow users to determine WBS attribute content with "wbs is full path" flag for Primavera readers.
* Ensure summary task start and finish dates are populated when reading PMXML files.
* Use baseline start and finish dates as planned start and finish dates when writing PMXML files.
* Late start and late finish dates are now written to PMXML files.

## 8.0.4 (2020-02-06)
* Update sqlite-jdbc dependency to 3.30.1
* Improve handling of characters invalid for XML 1.0 in PMXML files generated by P6.

## 8.0.3 (2020-01-27)
* Improve handling of zero value durations, costs and units from certain MPP files.
* Improve percent complete calculation for certain XER file and P6 Database schedules.
* Improve percent complete calculation for certain P3 schedules.
* Improve handling of incorrectly encoded characters in PMXML files generated by P6.
* Ensure that negative durations can be written to and read from MSPDI files in the format understood by MS Project.

## 8.0.2 (2020-01-16)
* Improve handling of zero duration tasks read from Phoenix files.

## 8.0.1 (2020-01-05)
* Add missing nuget dependency

## 8.0.0 (2020-01-02)
* MPXJ now requires Java 8 or later.
* Removed deprecated methods.
* Updated to use POI 4.1.1.
* Updated to use IKVM 8.1.5717.0.

## 7.9.8 (2019-12-27)
* Added support for reading and writing outline code/custom field lookup tables for MSPDI files.
* Added sample code to demonstrate creation of timephased work.
* Populate project status date attribute when reading Asta schedules.
* Populate parent attribute when reading activity code values from Primavera schedules.
* Improve configurability of PrimaveraDatabaseReader and PrimaveraXERFileReader.
* Made JAXB JARs an explicit dependency to avoid issues with recent Java versions which do not include them.

## 7.9.7 (2019-11-25)
* Round percent complete values read from Asta files to two decimal places to avoid values like 99.9999999%.

## 7.9.6 (2019-11-22)
* Improve support for FastTrack files.

## 7.9.5 (2019-11-19)
* Added flag to manage compliance with password protection. (Contributed by ztravis)
* Improve support for Synchro 6.1 SP files.
* Fix an issue where the task hierarchy was not correctly represented when reading a PMXML file.

## 7.9.4 (2019-11-08)
* Add support for reading Sage 100 Contractor schedule grid files.
* Ensure attribute names are valid when exporting JSON.
* Improve handling of custom field lookup values (Based on a contribution by Nick Darlington).
* Fix an issue when copying a calendar which has exceptions defined.

## 7.9.3 (2019-09-10)
* Add support for reading task early finish and late finish attributes from Asta PP files.
* Ensure XER files containing secondary constraints can be read correctly.
* Preserve calendar IDs when reading from XER files and P6 database (Based on a contribution by forenpm).
* Ensure base calendars are read correctly for P6 schedules.
* Ensure MPP files with unexpected auto filter definition data are handled gracefully.
* Preserve leveling delay format when reading tasks from MSPDI files.
* Ensure unexpected structure of timephased data is handled gracefully when reading MPP files.

## 7.9.2 (2019-08-19)
* Add support for reading and writing secondary constraints from P6 schedules (Based on a contribution by Sruthi-Ganesh)
* Improve support for Synchro SP files containing blank tasks.
* Make constraint type mapping consistent when reading and writing PMXML files.
* Improve handling of leveling delay units and actual duration units (Based in a contribution by Daniel Schmidt).
* Improve handling of certain types of malformed MPP files.
* Improve handling of certain types of malformed SDEF files.
* Map P6 Equipment resource type to cost rather than work (Contributed by forenpm)
* Improve handling of certain MPP files containing large numbers of blank tasks.
* Improve handling of certain MPX files containing trailing delimiters.

## 7.9.1 (2019-07-01)
* Set task start, finish and percent complete when reading SDEF files.

## 7.9.0 (2019-07-01)
* Add support for reading SDEF files.

## 7.8.4 (2019-06-27)
* Add support for reading data links (linked fields) configuration from MPP files.
* Updated to avoid an infinite loop when processing certain corrupt files (Contributed by ninthwaveltd).
* Update MSPDI generation to ensure MS Project correctly recognises complete tasks without resource assignments.
* Ensure that activity codes are read for P6 schedules.
* Improve support for reading custom field values derived from custom field lookup tables in MPP files.
* Improve support for MPP files written with the June 2019 update of Microsoft Project.

## 7.8.3 (2019-05-24)
* Improve handling of task baseline start, start, baseline finish, finish and slack fields read from FTS files.

## 7.8.2 (2019-05-19)
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

## 7.8.1 (2019-02-13)
* Improve support for reading the Synchro Scheduler 2018 SP files.
* Add support for reading Gantt Designer GNT files.
* Improve handling of non-standard MSPDI files.
* Improve handling of non-standard GanttProject files.
* Update MSPDI generation to ensure MS Project correctly recognises complete milestones without resource assignments.
* Improve support for reading user defined fields from PMXML files.
* Ignore hammock tasks when reading PP files.

## 7.8.0 (2019-01-18)
* Added support for reading and writing GUIDs for Tasks, Resources, and Assignments in MSPDI files.
* Updated Java build to use Maven
* Updated to provide a general performance improvement (Based on a contribution by Tiago de Mello)
* Updated to fix an issue when the Microsoft JDBC driver is used to access a P6 database in SQL Server 2005
* Fixed [Issue 332](https://sourceforge.net/p/mpxj/bugs/332): Asta lag sign incorrect (Based on a contribution by Dave McKay)
* Fixed [Issue 333](https://sourceforge.net/p/mpxj/bugs/333): Asta constraints lost (Contributed by Dave McKay)
* Fixed [Issue 335](https://sourceforge.net/p/mpxj/bugs/335): MSDPI into Asta doesn't import Calendar exceptions (Contributed by Dave McKay)

## 7.7.1 (2018-10-23)
* Read additional schedule options from XER files. (Contributed by forenpm)
* Improve handling of some types of MPP file with missing resource assignment data.
* Ensure that resource assignment flag fields are read correctly for all MPP file types (Based on a contribution by Vadim Gerya).
* Ensure that timephased actual work is handled correctly for material resources (Contributed by Vadim Gerya).
* Improve accuracy when reading resource type from MPP files.
* Improve compatibility of generated MSPDI files with Asta Powerproject (Contributed by Dave McKay).

## 7.7.0 (2018-10-12)
* Add support for reading the Synchro Scheduler SP files.
* Add support for reading the activity code (ID) from Asta files.
* When reading a Phoenix file, set the project's status date to the data date from the storepoint.
* Handle MSPDI files with timephased assignments that don't specify a start and end date.

## 7.6.3 (2018-10-04)
* Add support for reading Remaining Early Start and Remaining Early Finish task attributes from P6. (Contributed by forenpm)
* Add support for reading Retained Logic and Progressive Override project attributes from P6. (Contributed by forenpm)
* Fix incorrect sign when calculating start and finish slack (Contributed by Brian Leach).
* Correctly read predecessors and successors from Phoenix files.

## 7.6.2 (2018-08-30)
* Add support for nvarchar columns when reading from a P6 database.
* Updated to correctly read percent lag durations from MSPDI files (based on a contribution by Lord Helmchen).
* Updated the data type for the ValueGUID tag in an MSPDI file (based on a contribution by Lord Helmchen).

## 7.6.1 (2018-08-29)
* Improve handling of MPP files where MPXJ is unable to read the filter definitions.
* Improve handling of SureTrak projects without a WBS.
* Improve handling of SureTrak and P3 WBS extraction.
* Handle unsupported ProjectLibre POD files more gracefully.
* Improve detection of non MS Project compound OLE documents.
* Gracefully handle XER files which contain no projects.

## 7.6.0 (2018-07-13)
* Added support for reading ConceptDraw PROJECT CDPX, CPDZ and CPDTZ files.
* Add support for reading the export_flag attribute from XER files. (Contributed by forenpm)
* Use correct licence details in Maven pom.xml (contributed by Mark Atwood).
* Improve UniversalProjectReader's handling of XER files containing multiple projects.

## 7.5.0 (2018-06-19)
* Added support for reading activity codes from P6 databases, XER files, and PMXML files.
* Added support for reading user defined values from a P6 database.
* Added support for PRX files which contain a SureTrak database.
* Added support for reading the resource "enterprise" attribute from MPP12 and MPP14 files.
* Improve performance when reading user defined values from XER files.
* Improved support for older Primavera PMXML files.
* Updated to rtfparserkit 1.11.0 for improved RTF parsing.

## 7.4.4 (2018-06-06)
* Improve handling of calendar exceptions in MPX files.
* Improve handling of MPP files with large numbers of null tasks.
* Improve robustness when reading timephased data.
* Correctly sort Primavera schedules containing WBS entries with no child activities.

## 7.4.3 (2018-05-25)
* Add support for reading the resource "generic" attribute from MPP files.
* Add a Unique ID attribute to the Relation class and populate for schedule types which support this concept.
* Store the Primavera Project ID as Unique ID in the project properties.
* Update MerlinReader to ensure support for Merlin Project Pro 5.

## 7.4.2 (2018-04-30)
* Gracefully handle malformed duration values in MSPDI files.
* Gracefully handle unexpected calendar exception data structure in certain MPP files.
* Improve handling of certain unusual MPP12 files.
* More work to gracefully handle POI issue 61677, allowing affected MPP files to be read successfully.

## 7.4.1 (2018-04-16)
* Add methods to list projects available in P3 and SureTrak database directories.
* Avoid NPE when a work pattern can't be located in an Asta Powerproject PP file.
* Avoid array bounds exception when reading certain PRX files.
* Read outline code value lists from MPP9 files.
* Handle SureTrak projects without a WBS.

## 7.4.0 (2018-03-23)
* Added support for reading Primavera SureTrak databases from directories, zip files, and STX files.
* Added support for PP files generated by Asta Powerproject from version 13.0.0.1

## 7.3.0 (2018-03-12)
* Added support for reading Primavera P3 databases from directories, zip files, and PRX files.
* Improve robustness when reading MPP files containing apparently invalid custom field data.
* Improve UniversalProjectReader byte order mark handling.
* Fixed [Issue 324](https://sourceforge.net/p/mpxj/bugs/324): Fields with lookup unreadable when a field has custom name.

## 7.2.1 (2018-01-26)
* More work to gracefully handle POI issue 61677, allowing affected MPP files to be read successfully.
* Avoid divide by zero when calculating percent complete from certain Primavera PMXML files.
* UniversalProjectReader updated to recognise MPX files with non-default separator characters.
* Update FastTrack reader to handle invalid percentage values on resource assignments.
* Update FastTrack reader to handle variations in UUID format.
* Read the full project name from XER files and the Primavera database and store it in the project title attribute.

## 7.2.0 (2018-01-18)
* Added support for reading TurboProject PEP files.
* Handle numeric values with leading spaces in XER files.
* Fix array bounds error when reading constraints from certain MPP files.

## 7.1.0 (2018-01-03)
* Added support for reading GanttProject GAN files.
* Ensure that calendar exception dates are read correctly from XER files and P6 databases regardless of the user's timezone.
* Read working day calendar exceptions from XER files and P6 database.
* Mark some ProjectFile methods as deprecated.

## 7.0.3 (2017-12-21)
* Use the Windows-1252 character set as the default when reading XER files.
* Gracefully handle POI issue 61677 to allow MPP affected MPP files to be read successfully.
* Handle recurring calendar exceptions read from MSPDI files without an occurrence count.
* Improve robustness of FastTrack schedule reader.
* Avoid reading empty calendar exceptions from MPX files.

## 7.0.2 (2017-11-20)
* Further improvements to task pruning for Asta PP files.

## 7.0.1 (2017-11-20)
* Improve robustness when reading MPP files when using certain 64-bit Java runtimes.
* Populate the project's comments property when reading an MSPDI file.
* Ensure that tasks are not discarded when reading PP files from older Asta versions.
* Fixed [Issue 319](https://sourceforge.net/p/mpxj/bugs/319): Wrong date ranges for split tasks
* Fixed [Issue 222](https://sourceforge.net/p/mpxj/bugs/222): getDefaultTaskType() not returning correct default task type

## 7.0.0 (2017-11-08)
* Added support for reading recurring exceptions from MPP and MSPDI files.
* Updated RecurringTask class interface (Note: this is a breaking API change)
* MSPDI writer now uses save version 14 by default (Note: this may affect applications which consume MSPDI files you generate)
* Correctly handle MSPDI files with Byte Order Marks.
* Handle MSPDI files with varying namespaces.
* Improve robustness Merlin file reader.
* Improve extraction of task start and finish dates from PMXML files only containing partial data.
* Prevent POI from closing the input stream when using UniversalProjectReader
* Fixed [Issue 321](https://sourceforge.net/p/mpxj/bugs/321): Cannot read mpp file using getProjectReader.

## 6.2.1 (2017-10-11)
* Gracefully handle corrupt MPP files.
* Improve reading and writing slack values for MSPDI files.
* Improve activity hierarchy extraction from Phoenix files.
* Fixed [Issue 243](https://sourceforge.net/p/mpxj/bugs/243): MSPDI Slack values not correctly set while loading.

## 6.2.0 (2017-10-06)
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

## 6.1.2 (2017-09-12)
* Gracefully handle incomplete records in XER files.

## 6.1.1 (2017-08-30)
* Ensure all classes in the gem are required

## 6.1.0 (2017-07-28)
* Provide Task.getEffectiveCalendar() method
* Populate missing finish dates in MSPDI files

## 6.0.0 (2017-07-22)
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

## 5.14.0 (2017-07-13)
* Improve handling of activity codes read from Phoenix files
* Calculate percent complete for tasks read from Phoenix files
* Populate task duration with Original Duration attribute when reading from XER files or P6 databases.
* Ensure that task finish dates are read correctly from Phoenix files.
* Improve UniversalProjectReader's handling of non-MPP OLE compound documents.
* Improve task hierarchy and ordering when reading some MPP files.

## 5.13.0 (2017-06-27)
* Further improve handling of WBS, bar, and task structure from Asta files.

## 5.12.0 (2017-06-26)
* Improve handling of WBS, bar, and task structure from Asta files.

## 5.11.0 (2017-06-20)
* Improve handling of malformed durations in MSPDI files.
* Improve performance when reading MPP files with certain kinds of timephased data.
* Raise a specific "password protected" exception type from the Ruby gem.
* Fix an issue with the storage of the "earned value method" task attribute.

## 5.10.0 (2017-05-23)
* Improve handling of deleted tasks in MPP files.
* Improve handling of invalid predecessor tasks in MPX files.
* Improve handling of invalid saved view state in MPP files.
* Fixed [Issue 313](https://sourceforge.net/p/mpxj/bugs/313): Empty baseline dates populated with garbage date instead of null.

## 5.9.0 (2017-04-27)
* Add support for reading ProjectLibre POD files (from ProjectLibre version 1.5.5 onwards).
* Correct getter method name for "file application" project property.

## 5.8.0 (2017-04-21)
* Updated to use POI 3.16 (note new dependency on Apache Commons Collections required by POI).
* Improve support for estimated durations in Merlin files.
* Read task notes from Asta files.
* Improve support for reading resource rates from Phoenix files.
* Add "file application" and "file type" to project properties to determine source of schedule data.

## 5.7.1 (2017-03-22)
* Improve support for Phoenix Project Manager XML files.

## 5.7.0 (2017-03-20)
* Add support for FastTrack Schedule files.
* Ensure that timephased data calculations correctly handle entry to and exit from DST.
* Fixed [Issue 306](https://sourceforge.net/p/mpxj/bugs/306): Microsoft Project 2016:  Issue with assignment 'Work Contour' attribute.

## 5.6.5 (2017-03-07)
* Improve handling of invalid calendar data in MSPDI files
* Improve handling of XER files containing multi-line records
* Improve handling of malformed MPX files
* Fixed [Issue 308](https://sourceforge.net/p/mpxj/bugs/308): Add support for elapsed percent to MSPDI writer
* Fixed [Issue 310](https://sourceforge.net/p/mpxj/bugs/310): MPX percent lag incorrect

## 5.6.4 (2017-02-16)
* UniversalProjectReader now recognises and handles byte order marks
* Fixed [Issue 307](https://sourceforge.net/p/mpxj/bugs/307): TimeUnit.ELAPSED_PERCENT read incorrectly from MPP files

## 5.6.3 (2017-02-08)
* Added a parameter to the Ruby gem to allow the maximum JVM memory size to be set.
* Updated to rtfparserkit 1.10.0 for improved RTF parsing.

## 5.6.2 (2017-02-06)
* Fixed [Issue 305](https://sourceforge.net/p/mpxj/bugs/305): Failed to Parse error with Primavera 15.2 or 16.1 XML files

## 5.6.1 (2017-02-03)
* Correct resource assignment handling for Phoenix Project Manager schedules.

## 5.6.0 (2017-01-29)
* Add support for Phoenix Project Manager schedules.

## 5.5.9 (2017-01-27)
* Improve robustness of date parsing for MPX files.

## 5.5.8 (2017-01-23)
* Fix NPE when reading graphical indicators with unknown field type.

## 5.5.7 (2017-01-13)
* Fix percent complete NaN value for some Primavera schedules.

## 5.5.6 (2017-01-06)
* Fix incorrectly set critical flag for primavera schedules.

## 5.5.5 (2017-01-06)
* Updated to rtfparserkit 1.9.0 for improved RTF parsing
* Improve calendar exception parsing for Primavera XER and database readers.
* Ensure the task summary flag is set correctly for Primavera schedules.
* Rollup baseline, early and late start and finish dates to WBS for Primavera schedules.
* Rollup baseline duration, remaining duration and percent complete to WBS for Primavera schedules.
* Use the project's critical slack limit value when setting the critical flag on a task.
* Experimental support for reading Merlin Project schedules.

## 5.5.4 (2016-12-01)
* Default to UTF-8 encoding when generating JSON files

## 5.5.3 (2016-11-29)
* Correctly read text from MPP files when default charset is not UTF-8.
* Improve accuracy when reading MPP9 files.

## 5.5.2 (2016-11-02)
* Add Primavera Parent Resource ID as a specific resource attribute (Based on a contribution by Dave McKay).
* PMXML writer generates currency record (Based on a contribution by Dave McKay).
* PMXML writer defaults Activity PercentCompleteType to Duration (Based on a contribution by Dave McKay).
* PMXML writer records currency and parent attributes for Resource (Based on a contribution by Dave McKay).
* PMXML writer resource assignments include RateSource and ActualOvertimeUnits attributes(Based on a contribution by Dave McKay).
* MSPDI reader: gracefully handle invalid calendar exceptions..
* PMXML writer: gracefully handle missing data.
* Planner writer: gracefully handle missing data.

## 5.5.1 (2016-10-14)
* Update universal project reader to support zip files.
* Update ruby to align error handling with universal project reader.

## 5.5.0 (2016-10-13)
* Universal project reader.
* Avoid NPE when reading PMXML files.
* Fixed [Issue 297](https://sourceforge.net/p/mpxj/bugs/297): Missing extended attributes
* Fixed [Issue 300](https://sourceforge.net/p/mpxj/bugs/300): CrossProject field omission causes issues when importing to P6

## 5.4.0 (2016-10-06)
* Updated to use POI 3.15.

## 5.3.3 (2016-08-31)
* Avoid NPE when field type is unknown.
* Improve Ruby error reporting.
* Improve support for non-standard time formats in MPX files
* Improve support for MPP14 files with very large numbers of blank tasks

## 5.3.2 (2016-08-31)
* When reading an XER file, treat FT_STATICTPYE user defined fields as text.

## 5.3.1 (2016-07-01)
* Add data date attribute to PMXML output.
* Update PMXML writer to avoid NPE.
* Update PMXML writer to allow task field used for Activity ID to be chosen.
* Updated to avoid NPE when reading an XER file where project not under EPS.
* Generate Task IDs if missing from MSPDI file

## 5.3.0 (2016-06-10)
* Add support for PP files generated by Asta Powerproject from version 13.0.0.3 onwards
* Minor improvements to SDEF support.
* Updated to rtfparserkit 1.8.0
* Improve finish time handling in PMXML files (contributed by lobmeleon)

## 5.2.2 (2016-03-11)
* Add support for resource assignment Stop and Resume attributes for MPP and MSPDI files
* Fixed [Issue 291](https://sourceforge.net/p/mpxj/bugs/291): PrimaveraPMFileWriter.write fails with java.lang.IllegalArgumentException
* Fixed [Issue 292](https://sourceforge.net/p/mpxj/bugs/292): Microsoft Project 2016 : Need to set 'Stop' and 'Resume' properties for org.mpxj.ResourceAssignment

## 5.2.1 (2016-02-11)
* Add support for PP files generated by Asta Powerproject up to version 13.0.0.3

## 5.2.0 (2016-02-08)
* Add support for PP files generated by Asta Powerproject 11, Powerproject 12, Easyplan 2, Easyplan 3, Easyplan 4, Easyplan 5 and Easyplan 6
* Fixed [Issue 285](https://sourceforge.net/p/mpxj/bugs/285): Unsupported encoding command ansicpg949
* Fixed [Issue 288](https://sourceforge.net/p/mpxj/bugs/288): AvailabilityTable getEntryByDate does not work properly

## 5.1.18 (2016-01-25)
* Fixed [Issue 285](https://sourceforge.net/p/mpxj/bugs/285): Unsupported encoding command ansicpg1254
* Fixed [Issue 286](https://sourceforge.net/p/mpxj/bugs/286): NullPointerException in CriteriaReader.getConstantValue
* Fixed [Issue 287](https://sourceforge.net/p/mpxj/bugs/287): Allow a character encoding to be specified when reading an XER file
* Write Primavera Primary Resource Unique ID to Task field Number1

## 5.1.17 (2015-12-30)
* Improve support for reading MPP files generated by Project 2016
* Handle missing time component of a time stamp field when reading an MPX file.

## 5.1.16 (2015-12-18)
* Improve support for reading MPX files generated by SureTrak

## 5.1.15 (2015-12-16)
* Fix WBS and Activity ordering for tasks from Primavera.

## 5.1.14 (2015-12-09)
* Strip unescaped control characters from JSON output.

## 5.1.13 (2015-11-26)
* For schedules imported from Primavera ensure tasks representing activities are ordered by Activity ID within the WBS to match Primavera.

## 5.1.12 (2015-11-16)
* Avoid NPE when writing MSPDI files with timephased data  (contributed by Bruno Gasnier)
* Improve resource assignment constructor (based on a contribution by Bruno Gasnier)
* Improve MPX French translations (contributed by Bruno Gasnier)
* Add calendar specific minutes per day, week, month, and year (based on a contribution by Bruno Gasnier)
* Add support for reading and writing GUID attribute for PMXML, XER files and Primavera database.

## 5.1.11 (2015-11-12)
* Avoid NPE when reading MPP14 custom properties.
* Ensure calculated task attributes are present in JSON output.
* Handle MSPDI files written by German versions of Microsoft Project (based on a contribution by Lord Helmchen)
* Fixed [Issue 277](https://sourceforge.net/p/mpxj/bugs/277): synchronizeTaskIDToHierarchy clears list of tasks
* Fixed [Issue 273](https://sourceforge.net/p/mpxj/bugs/273): PrimaveraPMFileWriter throws Exception at write(..)
* Fixed [Issue 281](https://sourceforge.net/p/mpxj/bugs/281): Parent task is always null when reading a Primavera XER file
* Ensure that Task.getSuccesors() and Task.getPredecessors() return an empty list rather than null.

## 5.1.10 (2015-09-09)
* Improve FixedMeta2 block size heuristic to improve reliability when reading MPP14 files.

## 5.1.9 (2015-08-29)
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

## 5.1.8 (2015-07-13)
* Another attempt at getting tzinfo-data dependency working

## 5.1.7 (2015-07-13)
* Updated ruby gem to make tzinfo-data dependency conditional on platform

## 5.1.6 (2015-07-13)
* Updated ruby gem to allow timezone to be provided

## 5.1.5 (2015-06-05)
* Updated to use IKVM 8.0.5449.1

## 5.1.4 (2015-06-03)
* Updated to generate Activity ID for Primavera WBS.
* Updated to correct Primavera duration percent complete calculation.

## 5.1.3 (2015-05-18)
* Updated to ensure Ruby reads Boolean attributes correctly.

## 5.1.2 (2015-05-18)
* Updated to ensure Ruby recognises short type as an integer.

## 5.1.1 (2015-05-18)
* Updated to use ruby-duration gem to avoid conflict with ActiveSupport::Duration.

## 5.1.0 (2015-05-17)
* Updated to ensure that PrimaveraDatabaseReader.setSchema accepts null or empty string
* Ensure conversion to/from .Net DateTime takes account of timezone and daylight savings (based on a contribution by Timour Koupeev)
* Updated to use POI 3.12.
* Removed ProjectFile.getTaskFieldAliases, replaced by ProjectFile.getCustomField().getFieldByAlias
* Removed ProjectFile.getResourceFieldAliases, replaced by ProjectFile.getCustomField().getFieldByAlias

## 5.0.0 (2015-05-06)
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

## 4.7.6 (2015-03-18)
* Added a Ruby wrapper for MPXJ
* Added the ability to export project data as JSON, to make it easier to work with in languages other than Java
* Added support for the Assignment attribute Resource Request Type
* Primavera database and XER readers updated to match WBS visible in Primavera for each task. Previous behaviour of generating a unique WBS for each task can be restored using a flag set on the readers.
* Avoid NPE when calculating Task Completed Through
* Read Task Earned Value Method correctly from MPP files
* Fix issue where some floating point attributes were returning NaN

## 4.7.5 (2015-02-27)
* Handle invalid Primavera calendar data gracefully

## 4.7.4 (2015-02-25)
* Fixed [Issue 257](https://sourceforge.net/p/mpxj/bugs/257): Failed to read project containing CodePage 1250 text.
* Fixed [Issue 259](https://sourceforge.net/p/mpxj/bugs/259): MS Project 2010: tasks with null baseline dates
* Incorrect task end date read from Primavera XER and database
* Incorrect percent complete read from Primavera XER, database, and PMXML files
* Failed to read fields held at the end of a fixed data block
* Added support for Task Baseline Estimated Duration, Baseline Estimated Start, Baseline Estimated Finish, Baseline Fixed Cost, and Baseline Fixed Cost Accrual
* Added the ability to customise the fields read from a Primavera database or XER file.
* Added Task Activity Type and Task Status as additional fields read from Primavera database and XER and files
* Changed Task physical percent complete methods for consistency to use Number rather than Integer

## 4.7.3 (2014-12-23)
* Updated to use POI 3.11.
* Updated to use rtfparserkit 1.1.0 for Java 6 compatibility.

## 4.7.2 (2014-12-15)
* Updated to fix Maven dependency issue.

## 4.7.1 (2014-12-08)
* Added a flag to MPPReader to indicate that only the project header should be read.

## 4.7.0 (2014-12-04)
* Implemented new RTF parser for stripping RTF to improve performance and accuracy
* Removed non-API code from the top level package
* Improved support for reading built-in and custom project properties from MPP files.
* Improved resilience of MPP file reading to unknown data structures
* Fixed issue which could cause an infinite loop when ordering tasks in a file containing multiple consecutive blank tasks
* Fixed issue where free text versions of task start, finish, and duration fields were not being read correctly from MPP14 files

## 4.6.2 (2014-11-11)
* Fixed issue with custom duration field units not read correctly from MSPDI files
* Fixed [Issue 223](https://sourceforge.net/p/mpxj/bugs/223): Problems with the lag calculated in the relation
* Outline code not read correctly from MPP file written by Project 2013
* Fixed [Issue 239](https://sourceforge.net/p/mpxj/bugs/239): Defensive changes to avoid exceptions when reading MPP files
* Fixed [Issue 250](https://sourceforge.net/p/mpxj/bugs/250): Deleted tasks being read from mpp file
* Added DotNetInputStream and DotNetOutputStream classes for ease of use under .Net.
* Updated to automatically generate and package MpxjUtilities.dll

## 4.6.1 (2014-10-17)
* Fixed NuGet metadata

## 4.6.0 (2014-10-17)
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

## 4.5.0 (2014-03-01)
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

## 4.4.0 (2013-03-14)
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

## 4.3.0 (2012-02-08)
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

## 4.2.0 (2011-06-23)
* Added support for resource assignment fields Baseline Cost 1-n, Baseline Work 1-n, Baseline Start 1-n, Baseline Finish 1-n, Start 1-n, Finish 1-n, Date 1-n, Duration 1-n, Cost 1-n, Text 1-n, Number 1-n, Flag 1-n, for MPP, MPD, and MSPDI files.
* Added support for task suspend date, task resume date, and task code read from Primavera, and represented in MS Project custom fields Date1, Date2, and Text1 respectively.
* Added support for retrieving the table associated with any view.
* Fixed [Issue 158](https://sourceforge.net/p/mpxj/bugs/158): Error converting Mpp to planner.
* Fixed [Issue 157](https://sourceforge.net/p/mpxj/bugs/157): MSPDI Linklag for TimeUnit.Percent.
* Fixed [Issue 156](https://sourceforge.net/p/mpxj/bugs/156): Error reading calendars for 2010 files.
* Fixed [Issue 159](https://sourceforge.net/p/mpxj/bugs/159): Duplication of calendar id.
* Fixed [Issue 153](https://sourceforge.net/p/mpxj/bugs/153): Wrong task start.
* Fixed [Issue 156](https://sourceforge.net/p/mpxj/bugs/156): Wrong start and finish dates for 2010 files.

## 4.1.0 (2011-05-30)
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

## 4.0.0 (2010-05-25)
* Added support for reading Microsoft Project 2010 MPP files.
* Added support for reading Primavera P6 XER files.
* Added support for reading Primavera P6 databases.
* Updated to target Java 1.6.
* Added Russian locale (Contributed by Roman Bilous).
* Relation.getDuration() is always giving result in 'HOUR' fmt.

## 3.2.0 (2010-01-20)
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

## 3.1.0 (2009-05-20)
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

## 3.0.0 (2009-01-25)
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

## 2.1.0 (2008-03-23)
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

## 2.0.0 (2007-10-07)
* Migrated to Java 5
* Introduced generics
* Introduced enums
* Updated to POI 3.0.1
* Updated to JAXB 2.1.4
* Changed company details from Tapster Rock to Packwood Software

## 1.0.0 (2007-08-30)
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

## 0.9.2 (2006-03-07)
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
* Fixed a long-standing bug where the calendar records were being written into MPX files after they were referred to in the project summary record.
* Fixed a bug where WBS and Outline Levels were not being auto generated correctly when an MPP file contained a project summary task.
* Fixed a bug where split tasks were not being reported correctly.

## 0.9.1 (2006-01-26)
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

## 0.0.25 (2005-08-11)
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
* Fixed a bug where deleted constraints in an MPP9 file were not being ignored.
* Updated to make replace the int relation type in the Relation class with instances of the RelationType class.
* Updated to derive RelationList from AbstractList.
* Added sample code to MpxjQuery to illustrate retrieval of information from Relation instances.
* Updated MpqjQuery to parse MSPDI files as well as MPP and MPX files.
* Added support for early start, early finish, late start, late finish to MPP files.
* Updated MPP9 file support to handle start as late as possible constraints. 
* Added support for subproject file information in MPP9 files.
* Fixed a bug where occasionally a task in MPP9 files were not being read.
* Fixed a NegativeArrayIndexException thrown when reading certain MPP8 files.
* Reduced the memory used by MPXJ by anything up to 60%, particularly when reading large MPP files.
* Fixed a bug when reading MPX files where the field delimiter was not comma, and task relation lists contained more than one entry.
* Updated to fix unreliable retrieval of project start and end dates from certain MPP files.
* Fixed schedule from value in MSPDI files (contributed by Frank Illenberger).
* Fixed a bug when reading durations in elapsed days from an MPP file.
* Tasks can now have arbitrary priority values. These values are mapped to/from the fixed MPP8/MPX priority values where necessary.

## 0.0.24 (2005-01-10)
* Fixed a bug (again!) where deleted resource assignments in MPP9 files were still seen by MPXJ.
* Updated to use class instances instead of primitives to represent some enumerated types.
* Updated to implement support for reading and writing all the basic Resource attributes found in MSPDI files.
* Updated to implement support for reading and writing all the basic Task attributes found in MSPDI files.
* Updated to implement support for reading and writing all the basic Project Header attributes from MPP8 and MPP9 files.
* Made MSPDI file parsing more robust to allow it by default to cope with non-schema-compliant XML in the same manner as MS Project. Implemented a new compatibility flag to allow this behaviour to be disabled in favour of strict parsing.
* Merged DateTimeSettings, CurrencySettings, and DefaultSettings into the ProjectHeader class. This change makes the project header data easier to use as it is in a single place. It also makes the entities used to describe a project consistent with the contents of the MPP and MSPDI file formats.

## 0.0.23 (2004-11-17)
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
* Updated support for password-protected files to allow write-reserved files to be read.
* Updated to use the latest version of JAXB, as shipped in Sun's Java Web Services Developer Pack (JWSDP) version  1.4.
* Updated the distribution to include the redistributable files from the JWSDP JAXB implementation. Users will no longer need to download JWSDP separately in order to make use of MPXJ's MSPDI functionality.
* Updated to prevent empty notes records being added to tasks and resources when reading an MSPDI file.
* Updated to improve accuracy when converting an MPP file to an MSPDI file.
* Added support for blank task rows in MPP8 files.
* Added support for blank resource rows in MPP8 files.
* Added support for Portuguese MPX files.
* Added support reading and writing extended attributes (apart from outline codes) for MSPDI files.
* Added support for the Resource Type attribute.

## 0.0.22 (2004-07-27)
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

## 0.0.21 (2004-05-06)
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

## 0.0.20 (2004-03-15)
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

## 0.0.19 (2003-12-02)
* Fixed a bug reading table data from certain MPP8 files
* Updated MSPDI support to use the latest version of JAXB (from JWSDP-1.3)
* Re-implemented base and resource calendars as a single MPXCalendar class
* Updated support for base calendars and resource calendars for all file formats
* Improved MPXException to print details of any nested exception when a stack trace is printed.
* Removed unnecessary use of ByteArray.java
* Added support for the following task fields: ActualOvertimeCost, ActualOvertimeWork, FixedCostAccrual, Hyperlink, HyperlinkAddress, HyperlinkSubAddress, LevelAssignments, LevelingCanSplit, LevelingDelay, PreleveledStart, PreleveledFinish, RemainingOvertimeCost, RemainingOvertimeWork.

## 0.0.18 (2003-11-13)
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

## 0.0.17 (2003-08-05)
* Fixed a bug where a decimal point was being appended to the currency format even if no decimal digits were required.
* Fixed a bug where special characters appearing in the currency symbol were not being quoted.
* Fixed a bug that caused resource assignments to be incorrectly read from some MPP8 files.
* Added a new write method to MPXFile allowing the user control over the character encoding used when writing an MPX file.

## 0.0.16 (2003-07-04)
* Fixed bug causing some extended boolean attributes to be read incorrectly.
* Fixed bug causing MPP8 file task data to be read incorrectly under certain circumstances.
* Updated calendar duration code to account for calendar exceptions.

## 0.0.15 (2003-06-17)
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

## 0.0.14 (2003-05-28)
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

## 0.0.13 (2003-05-22)
* Implemented support for the Microsoft Project 98 file format.
* Fixed a bug that prevented task and resource note text from being read.
* Updated to remove a Java 1.4 dependency introduced in 0.0.12. Will now work with Java 1.3.
* Updated to correct handling of carriage returns embedded in note fields.

## 0.0.12 (2003-05-08)
* Fixed incorrect handling of timezones and daylight saving time.
* Fixed incorrect task structure generated from outline levels.
* Updated to extract the notes fields from tasks and resources read from an MPP file.
* Added the option to remove or preserve the RTF formatting from the note fields from an MPP file.
* Updated to extract the following fields from task data in an MPP file which were previously not imported: Text11-Text30, Number6-Number20, Duration4-Duration10, Date1-Date10, Cost4-Cost10, Start6-Start10, Finish6-Finish10
* Updated to extract the following fields from resource data in an MPP file which were previously not imported: Text6-Text30, Start1-Start10, Finish1-Finish10, Number1-Number20, Duration1-Duration10, Date1-Date10, Cost1-Cost10

## 0.0.11 (2003-04-15)
* Fixed error in format string used in one of the example files.
* Fixed error where double byte characters were being read incorrectly.
* Fixed error where deleted constraints were being resurrected when read from an MPP file.
* Updated to extract the following fields from task data in an MPP file which were previously not imported: Flag11-Flag20, Rollup, HideBar, EffortDriven.

## 0.0.10 (2003-04-08)
* Corrected Actual Start and Actual End fields from MPP file.
* Fixed bug where time values were being broken by daylight saving time in the user's default locale.
* Updated to extract the following fields from task data in an MPP file which were previously not imported: Actual Work, Baseline Work, Cost Variance, Deadline, Remaining Work, Work.
* Updated to extract the following fields from resource data in an MPP file which were previously not imported: Actual Cost, Actual Overtime Cost, Actual Work, Baseline Work, Cost, Cost Variance, Max Units, Overtime Cost, Overtime Rate, Overtime Work, Peak, Regular work, Remaining Cost, Remaining Overtime Cost, Remaining Work, Standard Rate, Work, Work Variance

## 0.0.9 (2003-04-03)
* Fixed bug when handling certain types of modified MPP file where resources have been updated.
* Added sample MPP files for bugs to the JUnit tests.
* Added support for summary flag import from MPP files.
* Added automatic summary flag update when creating an MPX file programmatically.
* Added new constructor to the MSPDIFile class to allow MSPDI files to be created from scratch.

## 0.0.8 (2003-03-27)
* Added support for estimated durations.
* Fixed bug in handling certain types of modified MPP file where tasks have been updated.
* Added the facility to auto generate outline numbers.
