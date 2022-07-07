<style type='text/css' rel='stylesheet'>
table {
   display: block;
   height: 300px;
   overflow: auto;
   width: 100%;
}

th {
   position: sticky;
   top: 0;
   z-index: 1; 
}
</style>

# Field Guide
The tables below provide an indication of which fields are populated when files of different types are read using MPXJ
The tables are not hand-crafted: they have been generated from test data and are therefore may be missing some details.

## Project
### Core Fields
Field|Asta (PP)|Planner (XML)|Primavera (PMXML)|Project Commander (PC)|ProjectLibre (POD)|TurboProject (PEP)
---|---|---|---|---|---|---
AM Text|✓|✓|✓|✓|✓|✓
Application Version| | | | |✓| 
Author|✓| | | | | 
Auto Add New Resources and Tasks|✓|✓|✓|✓|✓|✓
Auto Link|✓|✓|✓|✓|✓|✓
Bar Text Date Format|✓|✓|✓|✓|✓|✓
Company| |✓| | | | 
Creation Date| | |✓| | | 
Critical Activity Type|✓|✓|✓|✓|✓|✓
Currency Digits|✓|✓|✓|✓|✓|✓
Currency Symbol|✓|✓|✓|✓|✓|✓
Currency Symbol Position|✓|✓|✓|✓|✓|✓
Current Date|✓|✓|✓|✓|✓|✓
Custom Properties| | |✓| | | 
Date Format|✓|✓|✓|✓|✓|✓
Date Order|✓|✓|✓|✓|✓|✓
Date Separator|✓|✓|✓|✓|✓|✓
Days per Month|✓|✓|✓|✓|✓|✓
Decimal Separator|✓|✓|✓|✓|✓|✓
Default Calendar Unique ID|✓|✓|✓|✓|✓|✓
Default End Time| | | | |✓| 
Default Overtime Rate|✓|✓|✓|✓|✓|✓
Default Standard Rate|✓|✓|✓|✓|✓|✓
Default Start Time|✓|✓|✓|✓|✓|✓
Default Work Units|✓|✓|✓|✓|✓|✓
Duration|✓| | | | | 
Export Flag| | |✓| | | 
File Application|✓|✓|✓|✓|✓|✓
File Type|✓|✓|✓|✓|✓|✓
Finish Date|✓| | | |✓| 
Fiscal Year Start Month|✓|✓|✓|✓|✓|✓
GUID| | |✓| | | 
Last Saved|✓| | | | | 
MPX Code Page|✓|✓|✓|✓|✓|✓
MPX Delimiter|✓|✓|✓|✓|✓|✓
MPX File Version|✓|✓|✓|✓|✓|✓
MPX Program Name|✓|✓|✓|✓|✓|✓
Manager| |✓| | |✓| 
Microsoft Project Server URL|✓|✓|✓|✓|✓|✓
Minutes per Day|✓|✓|✓|✓|✓|✓
Minutes per Month|✓|✓| |✓|✓|✓
Minutes per Week|✓|✓|✓|✓|✓|✓
Minutes per Year|✓|✓| |✓|✓|✓
Must Finish By| | |✓| | | 
Name|✓|✓|✓| |✓| 
New Tasj Start Is Project Start|✓|✓|✓|✓|✓|✓
New Tasks Are Manual|✓|✓|✓|✓| |✓
New Tasks Estimated|✓|✓|✓|✓|✓|✓
PM Text|✓|✓|✓|✓|✓|✓
Planned Start| | |✓| | | 
Project ID| | |✓| | | 
Project Title|✓|✓|✓|✓|✓|✓
Schedule From|✓|✓|✓|✓|✓|✓
Scheduled Finish| | |✓| | | 
Start Date|✓|✓|✓| |✓| 
Status Date|✓| |✓| | | 
Thousands Separator|✓|✓|✓|✓|✓|✓
Time Format|✓|✓|✓|✓|✓|✓
Time Separator|✓|✓|✓|✓|✓|✓
Unique ID| | |✓| | | 
Updating Task Status Updates Resource Status|✓|✓|✓|✓|✓|✓
Week Start Day|✓|✓|✓|✓|✓|✓

### Baseline Fields
Field|Asta (PP)|Planner (XML)|Primavera (PMXML)|Project Commander (PC)|ProjectLibre (POD)|TurboProject (PEP)
---|---|---|---|---|---|---
Baseline Project Unique ID| | |✓| | | 

## Task
### Core Fields
Field|Asta (PP)|Planner (XML)|Primavera (PMXML)|Project Commander (PC)|ProjectLibre (POD)|TurboProject (PEP)
---|---|---|---|---|---|---
% Complete|✓|✓|✓| | | 
% Work Complete| |✓|✓| | | 
Active|✓|✓|✓|✓|✓|✓
Activity Codes|✓| |✓| | | 
Activity ID|✓| |✓| | | 
Activity Status| | |✓| | | 
Activity Type| | |✓| | | 
Actual Cost| | |✓| | | 
Actual Duration|✓|✓|✓| | | 
Actual Finish|✓| |✓| | | 
Actual Start|✓|✓|✓| | | 
Actual Work| |✓|✓| | | 
Calendar Unique ID|✓| |✓| | | 
Complete Through|✓|✓|✓| | | 
Constraint Date|✓| |✓| |✓| 
Constraint Type|✓|✓|✓|✓|✓|✓
Cost| | |✓| | | 
Cost Variance| | |✓| | | 
Created| | | | |✓| 
Critical|✓| |✓| |✓|✓
Deadline|✓| | | | | 
Duration|✓|✓|✓|✓|✓| 
Duration Variance| | |✓| | | 
Early Finish|✓| | | | |✓
Early Start|✓| | | | |✓
Effort Driven| |✓| | |✓| 
Estimated| | | | |✓| 
Expense Items| | |✓| | | 
External Early Start| | |✓| | | 
External Late Finish| | |✓| | | 
Finish|✓|✓|✓|✓|✓|✓
Finish Slack|✓| |✓| | | 
Finish Variance| | |✓| | | 
GUID| | |✓| | | 
ID|✓|✓|✓|✓|✓|✓
Late Finish|✓| | | | |✓
Late Start|✓| | | | |✓
Milestone|✓|✓|✓| | | 
Notes|✓|✓|✓| | | 
Outline Level|✓|✓|✓|✓|✓|✓
Outline Number|✓| |✓|✓|✓|✓
Overall Percent Complete|✓| | | | | 
Percent Complete Type| | |✓| | | 
Physical % Complete| | |✓| | | 
Planned Cost| | |✓| | | 
Planned Duration| | |✓| | | 
Planned Finish| | |✓| | | 
Planned Start| | |✓| | | 
Planned Work| | |✓| | | 
Predecessors|✓|✓|✓|✓|✓|✓
Primary Resource Unique ID| | |✓| | | 
Priority| |✓|✓| | | 
Remaining Cost| | |✓| | | 
Remaining Duration|✓| |✓| |✓| 
Remaining Early Finish| | |✓| | | 
Remaining Early Start| | |✓| | | 
Remaining Late Finish| | |✓| | | 
Remaining Late Start| | |✓| | | 
Remaining Work| |✓|✓| | | 
Resume|✓| | | | | 
Start|✓|✓|✓|✓|✓|✓
Start Slack|✓| |✓| | | 
Start Variance| | |✓| | | 
Successors|✓|✓|✓|✓|✓|✓
Summary|✓|✓|✓|✓| |✓
Task Name|✓|✓|✓|✓|✓|✓
Total Slack|✓| |✓| | | 
Type| |✓|✓| | | 
Unique ID|✓|✓|✓|✓|✓|✓
WBS|✓|✓|✓|✓| |✓
Work| |✓|✓|✓| | 
Work Variance| | |✓| | | 

### Baseline Fields
Field|Asta (PP)|Planner (XML)|Primavera (PMXML)|Project Commander (PC)|ProjectLibre (POD)|TurboProject (PEP)
---|---|---|---|---|---|---
Baseline Cost| | |✓| | | 
Baseline Duration|✓| |✓| | | 
Baseline Finish|✓| |✓| | | 
Baseline Start|✓| |✓| | | 
Baseline Work| | |✓| | | 

### Extended Fields
Field|Asta (PP)|Planner (XML)|Primavera (PMXML)|Project Commander (PC)|ProjectLibre (POD)|TurboProject (PEP)
---|---|---|---|---|---|---
Cost1| | |✓| | | 
Finish1| | |✓| | | 
Flag1|✓| | | | | 
Number1|✓| |✓| | | 
Number2| | |✓| | | 
Start1| | |✓| | | 
Text1|✓| |✓| | | 
Text2|✓| |✓| | | 
Text3|✓| |✓| | | 
Text4| | |✓| | | 
Text5| | |✓| | | 
Text6| | |✓| | | 
Text7| | |✓| | | 
Text8| | |✓| | | 
Text9| | |✓| | | 
Text10| | |✓| | | 
Text11| | |✓| | | 

## Resource
### Core Fields
Field|Asta (PP)|Planner (XML)|Primavera (PMXML)|Project Commander (PC)|ProjectLibre (POD)|TurboProject (PEP)
---|---|---|---|---|---|---
Accrue At| | | | |✓| 
Active| | | | |✓| 
Calendar Unique ID|✓| |✓|✓| |✓
Cost Per Use|✓| | | | | 
Email Address|✓|✓| | | | 
GUID| | |✓| | | 
Generic|✓| | | | | 
Group| | | | | |✓
ID|✓|✓|✓|✓| |✓
Initials|✓|✓| | |✓| 
Material Label|✓| | | | | 
Max Units|✓| |✓| |✓| 
Name|✓|✓|✓|✓|✓|✓
Notes| | |✓| | |✓
Overallocated|✓| | | | | 
Overtime Rate Units| | | | |✓| 
Parent ID| | |✓| | |✓
Peak|✓| | | |✓| 
Per Day| | | | | |✓
Pool| | | | | |✓
Rate| | | | | |✓
Resource ID| | |✓| | | 
Role| | |✓| | | 
Standard Rate Units| | | | |✓| 
Type|✓|✓|✓|✓|✓|✓
Unique ID|✓|✓|✓|✓| |✓
Unit| | | | | |✓

### Extended Fields
Field|Asta (PP)|Planner (XML)|Primavera (PMXML)|Project Commander (PC)|ProjectLibre (POD)|TurboProject (PEP)
---|---|---|---|---|---|---
Text1| | |✓| | | 

## Resource Assignment
### Core Fields
Field|Asta (PP)|Planner (XML)|Primavera (PMXML)|Project Commander (PC)|ProjectLibre (POD)|TurboProject (PEP)
---|---|---|---|---|---|---
Actual Cost| | |✓| | | 
Actual Finish| | |✓| | | 
Actual Start| | |✓| | | 
Actual Work|✓|✓|✓| | | 
Assignment Delay|✓| | | | | 
Assignment GUID| | |✓| | | 
Assignment Units|✓|✓|✓|✓|✓|✓
Cost| | |✓| | | 
Finish|✓|✓|✓| |✓| 
Percent Work Complete|✓|✓|✓| | | 
Planned Cost| | |✓| | | 
Planned Finish| | |✓| | | 
Planned Start| | |✓| | | 
Planned Work| | |✓| | | 
Remaining Cost| | |✓| | | 
Remaining Work|✓|✓|✓|✓|✓| 
Resource Unique ID|✓|✓|✓|✓| |✓
Resume| | | | |✓| 
Start|✓|✓|✓| |✓| 
Stop| | | | |✓| 
Task Unique ID|✓|✓|✓|✓|✓|✓
Unique ID|✓|✓|✓|✓|✓|✓
Work|✓|✓|✓|✓|✓| 
Work Contour| | |✓| |✓| 

### Extended Fields
Field|Asta (PP)|Planner (XML)|Primavera (PMXML)|Project Commander (PC)|ProjectLibre (POD)|TurboProject (PEP)
---|---|---|---|---|---|---
Text1| | |✓| | | 

