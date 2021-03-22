# Field Guide
The tables below provide an indication of which fields are populated when files of different types are read using MPXJ
The tables are not hand-crafted: they have been generated from test data and are therefore may be missing some details.

## Project
### Core Fields
Field|Asta (PP)|P3 (BTRIEVE)|Phoenix (PPX)|Primavera (XER)|SDEF (SDEF)|Sage (SCHEDULE_GRID)|SureTrak (STW)|Synchro (SP)
---|---|---|---|---|---|---|---|---
AM Text|✓|✓|✓|✓|✓|✓|✓|✓
Author|✓| | | | | | | 
Auto Add New Resources and Tasks|✓|✓|✓|✓|✓|✓|✓|✓
Auto Link|✓|✓|✓|✓|✓|✓|✓|✓
Bar Text Date Format|✓|✓|✓|✓|✓|✓|✓|✓
Company| |✓| | | | | | 
Critical Activity Type|✓|✓|✓|✓|✓|✓|✓|✓
Currency Digits|✓|✓|✓|✓|✓|✓|✓|✓
Currency Symbol|✓|✓|✓|✓|✓|✓|✓|✓
Currency Symbol Position|✓|✓|✓|✓|✓|✓|✓|✓
Current Date|✓|✓|✓|✓|✓|✓|✓|✓
Custom Properties| | | |✓| | | | 
Date Format|✓|✓|✓|✓|✓|✓|✓|✓
Date Order|✓|✓|✓|✓|✓|✓|✓|✓
Date Separator|✓|✓|✓|✓|✓|✓|✓|✓
Days per Month|✓|✓|✓|✓|✓|✓|✓|✓
Decimal Separator|✓|✓|✓|✓|✓|✓|✓|✓
Default Calendar Name|✓|✓|✓|✓|✓|✓|✓|✓
Default Overtime Rate|✓|✓|✓|✓|✓|✓|✓|✓
Default Standard Rate|✓|✓|✓|✓|✓|✓|✓|✓
Default Start Time|✓|✓|✓|✓|✓|✓|✓|✓
Default Work Units|✓|✓|✓|✓|✓|✓|✓|✓
Duration|✓| | | | | | | 
Export Flag| | | |✓| | | | 
File Application|✓|✓|✓|✓|✓|✓|✓|✓
File Type|✓|✓|✓|✓|✓|✓|✓|✓
Finish Date|✓|✓| |✓|✓| | | 
Fiscal Year Start Month|✓|✓|✓|✓|✓|✓|✓|✓
GUID| | | |✓| | | | 
Last Saved|✓| | | | | | | 
MPX Code Page|✓|✓|✓|✓|✓|✓|✓|✓
MPX Delimiter|✓|✓|✓|✓|✓|✓|✓|✓
MPX File Version|✓|✓|✓|✓|✓|✓|✓|✓
MPX Program Name|✓|✓|✓|✓|✓|✓|✓|✓
Manager| | | | |✓| | | 
Microsoft Project Server URL|✓|✓|✓|✓|✓|✓|✓|✓
Minutes per Day|✓|✓|✓|✓|✓|✓|✓|✓
Minutes per Week|✓|✓|✓|✓|✓|✓|✓|✓
Name|✓|✓|✓|✓| | | | 
New Tasj Start Is Project Start|✓|✓|✓|✓|✓|✓|✓|✓
New Tasks Are Manual|✓|✓|✓|✓|✓|✓|✓|✓
New Tasks Estimated|✓|✓|✓|✓|✓|✓|✓|✓
PM Text|✓|✓|✓|✓|✓|✓|✓|✓
Project ID| | | |✓| | | | 
Project Title|✓|✓|✓|✓|✓|✓|✓|✓
Schedule From|✓|✓|✓|✓|✓|✓|✓|✓
Start Date|✓|✓| |✓|✓| | | 
Status Date|✓|✓|✓|✓|✓| | | 
Thousands Separator|✓|✓|✓|✓|✓|✓|✓|✓
Time Format|✓|✓|✓|✓|✓|✓|✓|✓
Time Separator|✓|✓|✓|✓|✓|✓|✓|✓
Unique ID| | | |✓| | | | 
Updating Task Status Updates Resource Status|✓|✓|✓|✓|✓|✓|✓|✓
Week Start Day|✓|✓|✓|✓|✓|✓|✓|✓

### Baseline Fields
Field|Asta (PP)|P3 (BTRIEVE)|Phoenix (PPX)|Primavera (XER)|SDEF (SDEF)|Sage (SCHEDULE_GRID)|SureTrak (STW)|Synchro (SP)
---|---|---|---|---|---|---|---|---
Baseline Project Unique ID| | | |✓| | | | 

### Extended Fields
Field|Asta (PP)|P3 (BTRIEVE)|Phoenix (PPX)|Primavera (XER)|SDEF (SDEF)|Sage (SCHEDULE_GRID)|SureTrak (STW)|Synchro (SP)
---|---|---|---|---|---|---|---|---

### Enterprise Fields
Field|Asta (PP)|P3 (BTRIEVE)|Phoenix (PPX)|Primavera (XER)|SDEF (SDEF)|Sage (SCHEDULE_GRID)|SureTrak (STW)|Synchro (SP)
---|---|---|---|---|---|---|---|---

## Task
### Core Fields
Field|Asta (PP)|P3 (BTRIEVE)|Phoenix (PPX)|Primavera (XER)|SDEF (SDEF)|Sage (SCHEDULE_GRID)|SureTrak (STW)|Synchro (SP)
---|---|---|---|---|---|---|---|---
% Complete|✓|✓|✓|✓|✓| |✓|✓
% Work Complete| | | |✓| | | | 
Active|✓|✓|✓|✓|✓|✓|✓|✓
Activity ID|✓|✓|✓|✓|✓| |✓|✓
Activity Status| | | |✓| | | | 
Activity Type| | | |✓| | | | 
Actual Cost| | | |✓|✓| | | 
Actual Duration|✓| |✓|✓| | |✓| 
Actual Finish|✓|✓|✓|✓|✓| |✓|✓
Actual Start|✓|✓|✓|✓|✓| |✓|✓
Actual Work| | | |✓| | | | 
Bid Item| | | | |✓| | | 
Calendar Unique ID|✓| | |✓|✓| | |✓
Category of Work| | | | |✓| | | 
Complete Through|✓|✓|✓|✓|✓| |✓|✓
Constraint Date|✓|✓| |✓|✓|✓| |✓
Constraint Type|✓|✓|✓|✓|✓|✓|✓|✓
Cost| | |✓|✓|✓| | | 
Created| | |✓|✓| | | | 
Critical|✓|✓|✓|✓|✓|✓|✓|✓
Deadline|✓| | |✓| | | | 
Department| | | | | | |✓| 
Duration|✓|✓|✓|✓|✓|✓|✓|✓
Duration Variance| | |✓| | | | | 
Early Finish|✓|✓|✓|✓|✓| |✓| 
Early Start|✓|✓|✓|✓|✓| |✓| 
Expense Items| | | |✓| | | | 
Feature of Work| | | | |✓| | | 
Finish|✓|✓|✓|✓|✓|✓|✓|✓
Finish Slack|✓|✓|✓|✓|✓| |✓| 
Free Slack| |✓|✓|✓| | | | 
GUID| | |✓|✓|✓| | |✓
Hammock Code| | | | |✓| | | 
Hyperlink| | | | | | | |✓
ID|✓|✓|✓|✓|✓|✓|✓|✓
Ignore Resource Calendar| | | |✓| | | | 
Late Finish|✓|✓|✓|✓|✓|✓|✓| 
Late Start|✓|✓|✓|✓|✓|✓|✓| 
Mail| | | | | | |✓| 
Manager| | | | | | |✓| 
Milestone|✓|✓|✓|✓|✓| |✓|✓
Notes|✓| | |✓| |✓| |✓
Outline Level|✓|✓| |✓|✓|✓|✓|✓
Outline Number|✓|✓| |✓|✓|✓|✓|✓
Overall Percent Complete|✓| | | | | | | 
Percent Complete Type| | | |✓| | | | 
Phase of Work| | | | |✓| | | 
Physical % Complete| | | |✓| | | | 
Planned Duration| | | |✓| | | | 
Planned Finish| | | |✓| | | | 
Planned Start| | | |✓| | | | 
Planned Work| | | |✓| | | | 
Predecessors|✓|✓|✓|✓|✓|✓|✓|✓
Primary Resource Unique ID| | | |✓| | | | 
Priority| | | |✓| | | | 
Project| | | |✓| | | | 
Remaining Cost| | | |✓|✓| | | 
Remaining Duration|✓|✓|✓|✓|✓| |✓|✓
Remaining Early Finish| | | |✓| | | | 
Remaining Early Start| | | |✓| | | | 
Remaining Late Finish| | | |✓| | | | 
Remaining Late Start| | | |✓| | | | 
Remaining Work| | | |✓| | | | 
Responsibility Code| | | | |✓| | | 
Resume|✓| | |✓| | | | 
Secondary Constraint Date| | | |✓| | | | 
Secondary Constraint Type| | | |✓| | | | 
Section| | | | | | |✓| 
Start|✓|✓|✓|✓|✓|✓|✓|✓
Start Slack|✓|✓|✓|✓|✓| |✓| 
Successors|✓|✓|✓|✓|✓|✓|✓|✓
Summary|✓| |✓|✓| | | |✓
Suspend Date| | | |✓| | | | 
Task Calendar|✓| | |✓|✓| | |✓
Task Name|✓|✓|✓|✓|✓|✓|✓|✓
Total Slack|✓|✓|✓|✓|✓| |✓| 
Type| | | |✓| | | | 
Unique ID|✓|✓|✓|✓|✓|✓|✓|✓
WBS|✓| | |✓|✓|✓| |✓
Work| | | |✓| | | | 

### Baseline Fields
Field|Asta (PP)|P3 (BTRIEVE)|Phoenix (PPX)|Primavera (XER)|SDEF (SDEF)|Sage (SCHEDULE_GRID)|SureTrak (STW)|Synchro (SP)
---|---|---|---|---|---|---|---|---
Baseline Duration| | |✓| | | | | 

### Extended Fields
Field|Asta (PP)|P3 (BTRIEVE)|Phoenix (PPX)|Primavera (XER)|SDEF (SDEF)|Sage (SCHEDULE_GRID)|SureTrak (STW)|Synchro (SP)
---|---|---|---|---|---|---|---|---
Cost1| | | |✓| | | | 
Duration2| | | |✓| | | | 
Finish3| | | |✓| | | | 
Finish4| | | |✓| | | | 
Number1| | | |✓| | | | 
Start3| | | |✓| | | | 
Start4| | | |✓| | | | 
Text1| | | |✓| |✓| | 
Text2| | | |✓| | | | 
Text3| | | |✓| | | | 
Text4| | | |✓| | | | 
Text5| | | |✓| | | | 
Text6| | | |✓| | | | 
Text7| | | |✓| | | | 
Text8| | | |✓| | | | 
Text9| | | |✓| | | | 
Text10| | | |✓| | | | 
Text11| | | |✓| | | | 
Text12| | | |✓| | | | 
Text13| | | |✓| | | | 
Text14| | | |✓| | | | 
Text15| | | |✓| | | | 
Text16| | | |✓| | | | 
Text17| | | |✓| | | | 
Text18| | | |✓| | | | 
Text19| | | |✓| | | | 
Text20| | | |✓| | | | 
Text21| | | |✓| | | | 
Text22| | | |✓| | | | 
Text23| | | |✓| | | | 
Text24| | | |✓| | | | 
Text25| | | |✓| | | | 
Text26| | | |✓| | | | 
Text27| | | |✓| | | | 
Text28| | | |✓| | | | 
Text29| | | |✓| | | | 
Text30| | | |✓| | | | 

### Enterprise Fields
Field|Asta (PP)|P3 (BTRIEVE)|Phoenix (PPX)|Primavera (XER)|SDEF (SDEF)|Sage (SCHEDULE_GRID)|SureTrak (STW)|Synchro (SP)
---|---|---|---|---|---|---|---|---

## Resource
### Core Fields
Field|Asta (PP)|P3 (BTRIEVE)|Phoenix (PPX)|Primavera (XER)|SDEF (SDEF)|Sage (SCHEDULE_GRID)|SureTrak (STW)|Synchro (SP)
---|---|---|---|---|---|---|---|---
Active| | | | | | | |✓
Calendar|✓| | |✓| | |✓| 
Calendar Unique ID|✓| | |✓| | |✓| 
Code| |✓| | | | |✓| 
Description| | | | | | | |✓
Email Address|✓| | |✓| | | |✓
GUID| | |✓|✓| | | |✓
Generic|✓| | | | | | | 
Hyperlink| | | | | | | |✓
ID|✓|✓|✓|✓| | |✓|✓
Initials|✓| | | | | | | 
Material Label|✓| |✓| | | | | 
Max Units|✓| | | | | | | 
Name|✓|✓|✓|✓| | |✓|✓
Notes| | | |✓| | | |✓
Overallocated|✓| | | | | | | 
Overtime Rate Units| | | |✓| | | | 
Parent ID| | | |✓| | | | 
Peak|✓| | | | | | | 
Resource ID| | | |✓| | | | 
Standard Rate| | |✓| | | | | 
Standard Rate Units| | |✓|✓| | | | 
Supply Reference| | | | | | | |✓
Type|✓|✓|✓|✓| | |✓|✓
Unique ID|✓|✓|✓|✓| | |✓|✓

### Baseline Fields
Field|Asta (PP)|P3 (BTRIEVE)|Phoenix (PPX)|Primavera (XER)|SDEF (SDEF)|Sage (SCHEDULE_GRID)|SureTrak (STW)|Synchro (SP)
---|---|---|---|---|---|---|---|---

### Extended Fields
Field|Asta (PP)|P3 (BTRIEVE)|Phoenix (PPX)|Primavera (XER)|SDEF (SDEF)|Sage (SCHEDULE_GRID)|SureTrak (STW)|Synchro (SP)
---|---|---|---|---|---|---|---|---
Text1| | | |✓| | | | 
Text2| | | |✓| | | | 
Text3| | | |✓| | | | 

### Enterprise Fields
Field|Asta (PP)|P3 (BTRIEVE)|Phoenix (PPX)|Primavera (XER)|SDEF (SDEF)|Sage (SCHEDULE_GRID)|SureTrak (STW)|Synchro (SP)
---|---|---|---|---|---|---|---|---

## Resource Assignment
### Core Fields
Field|Asta (PP)|P3 (BTRIEVE)|Phoenix (PPX)|Primavera (XER)|SDEF (SDEF)|Sage (SCHEDULE_GRID)|SureTrak (STW)|Synchro (SP)
---|---|---|---|---|---|---|---|---
Actual Cost| | | |✓| | | | 
Actual Finish| | | |✓| | | | 
Actual Start| | | |✓| | | | 
Actual Work| | | |✓| | | | 
Assignment Delay|✓| | |✓| | | | 
Assignment GUID| | | |✓| | | | 
Assignment Units|✓|✓|✓|✓| | |✓|✓
Cost| | | |✓| | | | 
Finish|✓| | |✓| | | | 
Percent Work Complete| | | |✓| | | | 
Planned Cost| | | |✓| | | | 
Planned Finish| | | |✓| | | | 
Planned Start| | | |✓| | | | 
Planned Work| | | |✓| | | | 
Remaining Cost| | | |✓| | | | 
Remaining Work|✓| | |✓| | | | 
Resource Unique ID|✓|✓|✓|✓| | |✓|✓
Start|✓| | |✓| | | | 
Task Unique ID|✓|✓|✓|✓| | |✓|✓
Unique ID|✓|✓|✓|✓| | |✓|✓
Work|✓|✓|✓|✓| | |✓|✓

### Baseline Fields
Field|Asta (PP)|P3 (BTRIEVE)|Phoenix (PPX)|Primavera (XER)|SDEF (SDEF)|Sage (SCHEDULE_GRID)|SureTrak (STW)|Synchro (SP)
---|---|---|---|---|---|---|---|---

### Extended Fields
Field|Asta (PP)|P3 (BTRIEVE)|Phoenix (PPX)|Primavera (XER)|SDEF (SDEF)|Sage (SCHEDULE_GRID)|SureTrak (STW)|Synchro (SP)
---|---|---|---|---|---|---|---|---

### Enterprise Fields
Field|Asta (PP)|P3 (BTRIEVE)|Phoenix (PPX)|Primavera (XER)|SDEF (SDEF)|Sage (SCHEDULE_GRID)|SureTrak (STW)|Synchro (SP)
---|---|---|---|---|---|---|---|---

