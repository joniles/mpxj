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
Field|Asta (PP)|ConceptDraw PROJECT (CDP)|Deltek OpenPlan (BK3)|FastTrack (FTS)|GanttDesigner (GNT)|GanttProject (GAN)|Merlin (SQLITE)|Microsoft (MPD)|Microsoft (MPP)|Microsoft (MPX)|Microsoft (MSPDI)|P3 (BTRIEVE)|Phoenix (PPX)|Planner (XML)|Primavera (PMXML)|Primavera (SQLITE)|Primavera (XER)|Project Commander (PC)|ProjectLibre (POD)|SDEF (SDEF)|Sage (SCHEDULE_GRID)|SureTrak (STW)|Synchro (SP)|TurboProject (PEP)
---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---
AM Text|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Activity ID Increment|✓|✓|✓|✓|✓|✓|✓| |✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Activity ID Increment Based On Selected Activity|✓|✓|✓|✓|✓|✓|✓| |✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Activity ID Prefix|✓|✓|✓|✓|✓|✓|✓| |✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Activity ID Suffix|✓|✓|✓|✓|✓|✓|✓| |✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Actual Cost| | | | | | | | | |✓| | | | | | | | | | | | | | 
Actual Duration| | | | | | | | | |✓| | | | | | | | | | | | | | 
Actual Start| | | | | | | | | |✓| | | | | | | | | | | | | | 
Actual Work| | | | | | | | | |✓| | | | | | | | | | | | | | 
Actuals In Sync| | | | | | | | | | |✓| | | | | | | | | | | | | 
Application Version|✓| | | | | | | |✓| |✓| | | | | | | |✓| | | | | 
Author|✓| | | | | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Auto Add New Resources and Tasks|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Auto Filter| | | | | | | | |✓| | | | | | | | | | | | | | | 
Auto Link|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Bar Text Date Format|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Calculate Float on Finish Date of Each Project|✓|✓|✓|✓|✓|✓|✓| |✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Calculate Multiple Float Paths| | | | | | | | | | | | | | |✓|✓|✓| | | | | | | 
Calculate Multiple Float Paths Ending With Activity Unique ID| | | | | | | | | | | | | | |✓|✓|✓| | | | | | | 
Calculate Multiple Paths Using Total Float|✓|✓|✓|✓|✓|✓|✓| |✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Category| | | | | | | |✓|✓| | | | | | | | | | | | | | | 
Comments| | | | | | | | |✓|✓|✓| | | | | | | | | | | | | 
Company| | |✓| | |✓| |✓|✓|✓|✓|✓| |✓| | | | | | | | | | 
Compute Start to Start Lag From Early Start|✓|✓|✓|✓|✓|✓|✓| |✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Consider Assignments In Other Project With Priority Equal or Higher Than|✓|✓|✓|✓|✓|✓|✓| |✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Content Status| | | | | | | | |✓| | | | | | | | | | | | | | | 
Content Type| | | | | | | | |✓| | | | | | | | | | | | | | | 
Cost| | | | | | | | | |✓| | | | | | | | | | | | | | 
Creation Date| | | | |✓| | |✓|✓| |✓| | | |✓| | | | | | | | | 
Critical Activity Type|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Critical Slack Limit| | | | | | | | |✓| | | | | | | |✓| | | | | | | 
Currency Code| | | | | | | | |✓| |✓| | | | | | | | | | | | | 
Currency Digits|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Currency Symbol|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Currency Symbol Position|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Current Date|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Custom Properties|✓|✓|✓|✓|✓|✓|✓| |✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Date Format|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Date Order|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Date Separator|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Days per Month|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Decimal Separator|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Default Calendar Unique ID|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Default Duration Is Fixed| | | | | | | | | |✓| | | | | | | | | | | | | | 
Default End Time| | |✓| | | | |✓|✓| |✓| | | | | | | |✓| | | | | 
Default Overtime Rate|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Default Standard Rate|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Default Start Time|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Default Work Units|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Document Version| | | | | | | | |✓| | | | | | | | | | | | | | | 
Duration|✓| | | | | | | | |✓| | | | | | | | | | | | | | 
Earned Value Method| | | | | | | | | | |✓| | | | | | | | | | | | | 
Editable Actual Costs| | | | | | | | |✓| |✓| | | | | | | | | | | | | 
Editing Time| | | | | | | | |✓| | | | | | | | | | | | | | | 
Export Flag| | | | | | | | | | | | | | |✓| |✓| | | | | | | 
Extended Creation Date| | | | | | | | | | |✓| | | | | | | | | | | | | 
File Application|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
File Type|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Finish Date|✓|✓|✓|✓|✓|✓| |✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Fiscal Year Start| | | | | | | | |✓| | | | | | | | | | | | | | | 
Fiscal Year Start Month|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Full Application Name| | | | | | | | |✓| | | | | | | | | | | | | | | 
GUID| | | | | | |✓| |✓| |✓| | | |✓|✓|✓| | | | | | | 
Honor Constraints| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Hyperlink Base| | | | | | | | |✓| | | | | | | | | | | | | | | 
Ignore Relationships To And From Other Projects| | | | | | | | | | | | | | | | |✓| | | | | | | 
Inserted Projects Like Summary| | | | | | | | | | |✓| | | | | | | | | | | | | 
Keywords| | | | | | | |✓|✓|✓| | | | | | | | | | | | | | 
Language| | | | | | | | |✓| | | | | | | | | | | | | | | 
Last Author| | | | | | | | |✓| | | | | | | | | | | | | | | 
Last Printed| | | | | | | | |✓| | | | | | | | | | | | | | | 
Last Saved|✓| |✓| |✓| | |✓|✓| |✓| | | | |✓| | | | | | | | 
Level All Resources|✓|✓|✓|✓|✓|✓|✓| |✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Level Resources Only Within Activity Total Float| | | | | | | | | | | | | | |✓| |✓| | | | | | | 
Leveling Priorities|✓|✓|✓|✓|✓|✓|✓| |✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Limit Number of Float Paths to Calculate|✓|✓|✓|✓|✓|✓|✓| |✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Location Unique ID| | | | | | | | | | | | | | | | |✓| | | | | | | 
MPP File Type| | | | | | | | |✓| | | | | | | | | | | | | | | 
MPX Code Page|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
MPX Delimiter|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
MPX File Version|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
MPX Program Name|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Manager| | |✓| | | | |✓|✓|✓|✓| | |✓| | | | |✓|✓| | | | 
Maximum Percentage to Overallocate Resources|✓|✓|✓|✓|✓|✓|✓| |✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Microsoft Project Server URL|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Minutes per Day|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Minutes per Month|✓|✓|✓|✓|✓|✓|✓| |✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Minutes per Week|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Minutes per Year|✓|✓|✓|✓|✓|✓|✓| |✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Multiple Critical Paths| | | | | | | |✓|✓| | | | | | | | | | | | | | | 
Must Finish By| | | | | | | | | | | | | | |✓| |✓| | | | | | | 
Name|✓| |✓| |✓|✓|✓|✓| | |✓|✓|✓|✓|✓|✓|✓| |✓| | | | | 
New Task Start Is Project Start|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
New Tasks Are Effort Driven| | | | | | | |✓| | |✓| | | | | | | | | | | | | 
New Tasks Are Manual|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓| |✓|✓|✓|✓|✓
New Tasks Estimated|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Number of Float Paths to Calculate|✓|✓|✓|✓|✓|✓|✓| |✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
PM Text|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Percentage Complete| | | | | | | | | |✓| | | | | | | | | | | | | | 
Planned Start| | | | | | | | | | | | | | |✓|✓|✓| | | | | | | 
Presentation Format| | | | | | | | |✓| | | | | | | | | | | | | | | 
Preserve Minimum Float When Leveling|✓|✓|✓|✓|✓|✓|✓| |✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Preserve Scheduled Early and Late Dates|✓|✓|✓|✓|✓|✓|✓| |✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Project Code Values|✓|✓|✓|✓|✓|✓|✓| |✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Project Externally Edited| | | | | | | | | | |✓| | | | | | | | | | | | | 
Project File Path| | | | | | | | |✓| | | | | | | | | | | | | | | 
Project ID| | | | | | | | | | | | | | |✓|✓|✓| | | | | | | 
Project Title|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Project Website URL| | | | | | | | | | | | | | |✓|✓| | | | | | | | 
Relationship Lag Calendar|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Resource Pool File| | | | | | | | |✓| | | | | | | | | | | | | | | 
Revision| | | | | | | | |✓| |✓| | | | | | | | | | | | | 
Schedule From|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Scheduled Finish| | | | | | | | | | | | | | |✓|✓|✓| | | | | | | 
Short Application Name| | | | | | | | |✓| | | | | | | | | | | | | | | 
Show Project Summary Task| | | | | | | | |✓| | | | | | | | | | | | | | | 
Split In Progress Tasks| | | | | | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Start Date|✓|✓|✓|✓|✓|✓| |✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Start Variance| | | | | | | | | |✓| | | | | | | | | | | | | | 
Status Date|✓| |✓| | | | |✓|✓| |✓|✓|✓| |✓|✓|✓| | |✓| | | | 
Subject| | | | | | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Template| | | | | | | | |✓| | | | | | | | | | | | | | | 
Thousands Separator|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Time Format|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Time Separator|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Total Slack Calculation Type|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Unique ID| | | | | | | | | | | | | | |✓|✓|✓| | | | | | | 
Updating Task Status Updates Resource Status|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Use Expected Finish Dates|✓|✓|✓|✓|✓|✓|✓| |✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
WBS Code Separator|✓|✓|✓|✓|✓|✓|✓| |✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Week Start Day|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
When Scheduling Progressed Activities Use|✓|✓|✓|✓|✓|✓|✓| |✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Work| | | | | | | | | |✓| | | | | | | | | | | | | | 
Work 2| | | | | | | | | |✓| | | | | | | | | | | | | | 

### Baseline Fields
Field|Asta (PP)|ConceptDraw PROJECT (CDP)|Deltek OpenPlan (BK3)|FastTrack (FTS)|GanttDesigner (GNT)|GanttProject (GAN)|Merlin (SQLITE)|Microsoft (MPD)|Microsoft (MPP)|Microsoft (MPX)|Microsoft (MSPDI)|P3 (BTRIEVE)|Phoenix (PPX)|Planner (XML)|Primavera (PMXML)|Primavera (SQLITE)|Primavera (XER)|Project Commander (PC)|ProjectLibre (POD)|SDEF (SDEF)|Sage (SCHEDULE_GRID)|SureTrak (STW)|Synchro (SP)|TurboProject (PEP)
---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---
Baseline1 Date| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline2 Date| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline3 Date| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline4 Date| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline5 Date| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline6 Date| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline7 Date| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline8 Date| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline9 Date| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline10 Date| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline Calendar Name| | | | | | | | |✓| |✓| | | | | | | | | | | | | 
Baseline Cost| | | | | | | | | |✓| | | | | | | | | | | | | | 
Baseline Date| | | | | | | | |✓| | | | | |✓| | | | | | | | | 
Baseline Duration| | | | | | | | | |✓| | | | | | | | | | | | | | 
Baseline Finish| | |✓| | | | | | |✓| | | | | | | | | | | | | | 
Baseline Project Unique ID| | | | | | | | | | | | | | |✓|✓|✓| | | | | | | 
Baseline Start| | |✓| | | | | | |✓| | | | | | | | | | | | | | 
Baseline Type Name| | | | | | | | | | | | | | |✓| | | | | | | | | 
Baseline Work| | | | | | | | | |✓| | | | | | | | | | | | | | 
Last Baseline Update Date| | | | | | | | | | | | | | |✓| | | | | | | | | 
Project Is Baseline| | | | | | | | | | | | | | |✓| | | | | | | | | 

## Task
### Core Fields
Field|Asta (PP)|ConceptDraw PROJECT (CDP)|Deltek OpenPlan (BK3)|FastTrack (FTS)|GanttDesigner (GNT)|GanttProject (GAN)|Merlin (SQLITE)|Microsoft (MPD)|Microsoft (MPP)|Microsoft (MPX)|Microsoft (MSPDI)|P3 (BTRIEVE)|Phoenix (PPX)|Planner (XML)|Primavera (PMXML)|Primavera (SQLITE)|Primavera (XER)|Project Commander (PC)|ProjectLibre (POD)|SDEF (SDEF)|Sage (SCHEDULE_GRID)|SureTrak (STW)|Synchro (SP)|TurboProject (PEP)
---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---
% Complete|✓|✓| |✓|✓|✓| |✓|✓|✓|✓|✓|✓|✓|✓|✓|✓| | |✓| |✓|✓| 
% Work Complete| | | | | | | |✓|✓|✓|✓| | |✓|✓|✓|✓| | | | | | | 
ACWP| | | | | | | |✓| | |✓| | | | | | | | | | | | | 
Active|✓|✓|✓|✓|✓|✓|✓| |✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Activity Code Values|✓|✓|✓|✓|✓|✓|✓| |✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Activity ID|✓| |✓| | | | | | | | |✓|✓| |✓|✓|✓| | |✓| |✓|✓| 
Activity Percent Complete|✓|✓| |✓|✓|✓| | |✓|✓|✓|✓|✓|✓|✓|✓|✓| | |✓| |✓|✓| 
Activity Status| | | | | | | | | | | | | | |✓|✓|✓| | | | | | | 
Activity Type|✓| |✓| | | | | | | | | |✓| |✓|✓|✓| | | | | | | 
Actual Cost| | | | | | | |✓|✓|✓|✓| | | |✓|✓|✓| | |✓| | | | 
Actual Duration|✓| | |✓| | | |✓|✓|✓|✓| |✓|✓|✓|✓|✓| | | | |✓| | 
Actual Duration Units| | | | | | | | |✓| |✓| | | | | | | | | | | | | 
Actual Finish|✓| | |✓| | | |✓|✓|✓|✓|✓|✓| |✓|✓|✓| | |✓| |✓|✓| 
Actual Overtime Cost| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Actual Overtime Work| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Actual Start|✓| | |✓| | | |✓|✓|✓|✓|✓|✓|✓|✓|✓|✓| | |✓| |✓|✓| 
Actual Work| | | | | | | |✓|✓|✓|✓| | |✓|✓|✓|✓| | | | | | | 
Actual Work (Labor)| | | | | | | | | | | | | | |✓|✓|✓| | | | | | | 
Actual Work (Nonlabor)| | | | | | | | | | | | | | |✓|✓|✓| | | | | | | 
Actual Work Protected| | | | | | | | |✓| | | | | | | | | | | | | | | 
Bar Name|✓| | | | | | | | | | | | | | | | | | | | | | | 
Bid Item| | | | | | | | | | | | | | | | | | | |✓| | | | 
Board Status ID| | | | | | | | |✓| | | | | | | | | | | | | | | 
Budget Cost| | | | | | | | |✓| | | | | | | | | | | | | | | 
Budget Work| | | | | | | | |✓| | | | | | | | | | | | | | | 
CV| | | | | | | | | | |✓| | | | | | | | | | | | | 
Calendar Unique ID|✓| |✓| | | | | |✓| |✓| |✓| |✓|✓|✓| | |✓| | |✓| 
Category of Work| | | | | | | | | | | | | | | | | | | |✓| | | | 
Complete Through|✓| | |✓| | | | |✓|✓|✓|✓|✓|✓|✓|✓|✓| | |✓| |✓|✓| 
Constraint Date|✓| | |✓| |✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓| |✓|✓|✓| |✓| 
Constraint Type|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Contact| | | | | | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Cost| |✓|✓| |✓| | |✓|✓|✓|✓| |✓| |✓|✓|✓| | |✓| | | | 
Cost Variance| |✓| | | | | |✓|✓|✓|✓| | | |✓|✓|✓| | | | | | | 
Created| | | | | | | |✓|✓|✓|✓| |✓| | |✓|✓| |✓| | | | | 
Critical|✓| |✓|✓| | | | |✓|✓|✓|✓|✓| |✓|✓|✓| |✓|✓| |✓| | 
Deadline|✓|✓| | |✓| | |✓|✓| |✓| | | | |✓|✓| | | | | | | 
Department| | | | | | | | | | | | | | | | | | | | | |✓| | 
Duration|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓| 
Duration Units| | | | | | | | |✓| | | | | | | | | | | | | | | 
Duration Variance|✓|✓| |✓| | | |✓|✓|✓|✓| |✓| |✓|✓|✓| | | | | | | 
Early Finish|✓| |✓|✓| | | |✓|✓|✓|✓|✓|✓| |✓|✓|✓| | |✓| |✓| |✓
Early Start|✓| |✓|✓| | | |✓|✓|✓|✓|✓|✓| |✓|✓|✓| | |✓| |✓| |✓
Earned Value Method| | | | | | | | |✓| | | | | | | | | | | | | | | 
Effort Driven| | | |✓| | | |✓|✓| |✓| | |✓| | | | |✓| | | | | 
Estimated| | | | | | | |✓|✓| |✓| | | | | | | |✓| | | | | 
Expanded|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Expected Finish| | | | | | | | | | | | | | |✓| |✓| | | | | | | 
Expense Items|✓|✓|✓|✓|✓|✓|✓| |✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
External Early Start| | | | | | | | | | | | | | |✓| |✓| | | | | | | 
External Late Finish| | | | | | | | | | | | | | |✓| |✓| | | | | | | 
External Project| | | | | | | | |✓| |✓| | | | | | | | | | | | | 
External Task| | | | | | | | |✓| |✓| | | | | | | | | | | | | 
Feature of Work| | | | | | | | | | | | | | | | | | | |✓| | | | 
Finish|✓|✓|✓|✓|✓|✓| |✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Finish Slack|✓| |✓|✓| | | | |✓|✓|✓|✓|✓| |✓|✓|✓| | |✓|✓|✓| | 
Finish Variance| |✓| |✓| | | | |✓|✓|✓| | | |✓|✓|✓| | | | | | | 
Fixed Cost| | | |✓| | | |✓|✓| |✓| | | |✓|✓|✓| | | | | | | 
Fixed Cost Accrual| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Free Slack| | |✓|✓| | | |✓|✓|✓|✓|✓|✓| | |✓|✓| | | | | | | 
GUID| |✓|✓|✓| | |✓| |✓| |✓| |✓| |✓|✓|✓| | |✓| | |✓| 
Hammock Code| | | | | | | | | | | | | | | | | | | |✓| | | | 
Hide Bar| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Hyperlink| | | | | |✓| |✓|✓| | | | | | | | | | | | | |✓| 
Hyperlink Address| | | | | | | |✓|✓| | | | | | | | | | | | | | | 
Hyperlink Data| | | | | | | | |✓| | | | | | | | | | | | | | | 
Hyperlink Screen Tip| | | | | | | | |✓| | | | | | | | | | | | | | | 
Hyperlink SubAddress| | | | | | | | |✓| | | | | | | | | | | | | | | 
ID|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Ignore Resource Calendar| | | |✓| | | | |✓| |✓| | | | |✓|✓| | | | | | | 
Late Finish|✓| |✓|✓| | | |✓|✓|✓|✓|✓|✓| |✓|✓|✓| | |✓|✓|✓| |✓
Late Start|✓| |✓|✓| | | |✓|✓|✓|✓|✓|✓| |✓|✓|✓| | |✓|✓|✓| |✓
Level Assignments| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Leveling Can Split| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Leveling Delay| | | | | | | | |✓|✓|✓| | | | | | | | | | | | | 
Leveling Delay Units| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Longest Path| | | | | | | | | | | | | | | |✓|✓| | | | | | | 
Mail| | | | | | | | | | | | | | | | | | | | | |✓| | 
Manager| | | | | | | | | | | | | | | | | | | | | |✓| | 
Manual Duration| | | | | | | | |✓| |✓| | | | | | | | | | | | | 
Manual Duration Units| | | | | | | | |✓| | | | | | | | | | | | | | | 
Marked| | | | | | | |✓|✓| | | | | | | | | | | | | | | 
Milestone|✓|✓| | | |✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓| |✓| |✓|✓| 
Notes|✓|✓| |✓|✓| | |✓|✓|✓|✓| |✓|✓|✓|✓|✓| | | |✓| |✓| 
Null| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Outline Level|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓| |✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Outline Number|✓|✓|✓| |✓|✓|✓|✓|✓|✓|✓|✓| | |✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Overall Percent Complete|✓| | | | | | | | | | | | | | | | | | | | | | | 
Overallocated| | | | | | | | | | |✓| | | | | | | | | | | | | 
Overtime Cost| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Overtime Work| | | | | | | | | | |✓| | | | | | | | | | | | | 
Parent Task Unique ID| | | | | | | | |✓| | | | | | | | | | | | | | | 
Percent Complete Type| | | | | | | | | | | | | | |✓|✓|✓| | | | | | | 
Phase of Work| | | | | | | | | | | | | | | | | | | |✓| | | | 
Physical % Complete| | | | | | | | |✓| |✓| |✓| |✓|✓|✓| | | | | | | 
Planned Cost| | | | | | | | | | | | | | |✓|✓|✓| | | | | | | 
Planned Duration| | |✓| | | | | | | | | | | |✓|✓|✓| | | | | | | 
Planned Finish| | |✓| | | | | | | | | | | |✓|✓|✓| | | | | | | 
Planned Start| | |✓| | | | | | | | | | | |✓|✓|✓| | | | | | | 
Planned Work| | | | | | | | | | | | | | |✓|✓|✓| | | | | | | 
Planned Work (Labor)| | | | | | | | | | | | | | |✓|✓|✓| | | | | | | 
Planned Work (Nonlabor)| | | | | | | | | | | | | | |✓|✓|✓| | | | | | | 
Preleveled Finish| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Preleveled Start| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Primary Resource Unique ID| | | | | | | | | | | | | | |✓|✓|✓| | | | | | | 
Priority| |✓| | | |✓| |✓|✓|✓|✓| | |✓|✓|✓|✓| | | | | | | 
Project| | | | | | | | |✓|✓|✓| | | | |✓|✓| | | | | | | 
Recalc Outline Codes| | | | | | | | |✓| | | | | | | | | | | | | | | 
Recurring| | | | | | | | |✓|✓|✓| | | | | | | | | | | | | 
Recurring Data| | | | | | | | |✓| | | | | | | | | | | | | | | 
Regular Work| | | | | | | |✓| | |✓| | | | | | | | | | | | | 
Remaining Cost| | | | | | | |✓|✓|✓|✓| | | |✓|✓|✓| | |✓| | | | 
Remaining Duration|✓| |✓| | | | |✓|✓|✓|✓|✓|✓| |✓|✓|✓| |✓|✓| |✓|✓| 
Remaining Early Finish| | | | | | | | | | | | | | |✓|✓|✓| | | | | | | 
Remaining Early Start| | | | | | | | | | | | | | |✓|✓|✓| | | | | | | 
Remaining Late Finish| | | | | | | | | | | | | | |✓|✓|✓| | | | | | | 
Remaining Late Start| | | | | | | | | | | | | | |✓|✓|✓| | | | | | | 
Remaining Overtime Cost| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Remaining Overtime Work| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Remaining Work| | |✓| | | | |✓|✓|✓|✓| | |✓|✓|✓|✓| | | | | | | 
Remaining Work (Labor)| | | | | | | | | | | | | | |✓|✓|✓| | | | | | | 
Remaining Work (Nonlabor)| | | | | | | | | | | | | | |✓|✓|✓| | | | | | | 
Resource Names| | | |✓| | | | | |✓| | | | | | | | | | | | | | 
Responsibility Code| | | | | | | | | | | | | | | | | | | |✓| | | | 
Resume|✓| | | | | | |✓|✓|✓|✓| | | |✓| |✓| | | | | | | 
Resume No Earlier Than| | | | | | | | |✓| | | | | | | | | | | | | | | 
Resume Valid| | | | | | | | | | |✓| | | | | | | | | | | | | 
Rollup| | | | | | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Scheduled Duration| | | | | | | | |✓| | | | | | | | | | | | | | | 
Scheduled Finish| | | | | | | | |✓| | | | | | | | | | | | | | | 
Scheduled Start| | | | | | | | |✓| | | | | | | | | | | | | | | 
Secondary Constraint Date| | | | | | | | | | | | | | |✓| |✓| | | | | | | 
Secondary Constraint Type| | | | | | | | | | | | | | |✓| |✓| | | | | | | 
Section| | | | | | | | | | | | | | | | | | | | | |✓| | 
Sequence Number| | | | | | | | | | | | | | |✓|✓|✓| | | | | | | 
Show Duration Text| | | | | | | | |✓| | | | | | | | | | | | | | | 
Show Finish Text| | | | | | | | |✓| | | | | | | | | | | | | | | 
Show Start Text| | | | | | | | |✓| | | | | | | | | | | | | | | 
Splits| | | | | | | | |✓| |✓| | | | | | | | | | | | | 
Sprint ID| | | | | | | | |✓| | | | | | | | | | | | | | | 
Start|✓|✓|✓|✓|✓|✓| |✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Start Slack|✓| |✓|✓| | | | |✓|✓|✓|✓|✓| |✓|✓|✓| | |✓|✓|✓| | 
Start Variance|✓|✓| |✓| | | | |✓|✓|✓| | | |✓|✓|✓| | | | | | | 
Steps|✓|✓|✓|✓|✓|✓|✓| |✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Stop| | | | | | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Subproject File| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Subproject GUID| | | | | | | | |✓| | | | | | | | | | | | | | | 
Subproject Task ID| | | | | | | | |✓| |✓| | | | | | | | | | | | | 
Subproject Task Unique ID| | | | | | | | |✓| | | | | | | | | | | | | | | 
Subproject Tasks Unique ID Offset| | | | | | | | |✓| | | | | | | | | | | | | | | 
Summary|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓| | | |✓|✓|✓
Summary Progress| | | | | | | | |✓| | | | | | | | | | | | | | | 
Suspend Date| | | | | | | | | | | | | | |✓| |✓| | | | | | | 
Task Calendar GUID| | | | | | | | |✓| | | | | | | | | | | | | | | 
Task Mode| | | | | | | | |✓| |✓| | | | | | | | | | | | | 
Task Name|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Total Slack|✓| |✓|✓| | | | |✓|✓|✓|✓|✓| |✓|✓|✓| | |✓|✓|✓| | 
Type| |✓| | | | | |✓|✓|✓|✓| | |✓|✓|✓|✓| | | | | | | 
Unique ID|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Unique ID Successors| | | | | | | | | |✓| | | | | | | | | | | | | | 
WBS|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓| |✓|✓|✓|✓|✓| |✓|✓|✓|✓|✓
Work| | |✓|✓| | |✓|✓|✓|✓|✓| | |✓|✓|✓|✓|✓| | | | | | 
Work Variance| | | | | | | | |✓|✓|✓| | | |✓|✓|✓| | | | | | | 

### Baseline Fields
Field|Asta (PP)|ConceptDraw PROJECT (CDP)|Deltek OpenPlan (BK3)|FastTrack (FTS)|GanttDesigner (GNT)|GanttProject (GAN)|Merlin (SQLITE)|Microsoft (MPD)|Microsoft (MPP)|Microsoft (MPX)|Microsoft (MSPDI)|P3 (BTRIEVE)|Phoenix (PPX)|Planner (XML)|Primavera (PMXML)|Primavera (SQLITE)|Primavera (XER)|Project Commander (PC)|ProjectLibre (POD)|SDEF (SDEF)|Sage (SCHEDULE_GRID)|SureTrak (STW)|Synchro (SP)|TurboProject (PEP)
---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---
Baseline1 Cost| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline1 Duration| | | |✓| | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline1 Duration Units| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline1 Estimated Duration| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline1 Estimated Finish| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline1 Estimated Start| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline1 Finish| | | |✓| | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline1 Fixed Cost| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline1 Fixed Cost Accrual| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline1 Start| | | |✓| | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline1 Work| | | |✓| | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline2 Cost| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline2 Duration| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline2 Duration Units| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline2 Estimated Duration| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline2 Estimated Finish| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline2 Estimated Start| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline2 Finish| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline2 Fixed Cost| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline2 Fixed Cost Accrual| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline2 Start| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline2 Work| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline3 Cost| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline3 Duration| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline3 Duration Units| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline3 Estimated Duration| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline3 Estimated Finish| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline3 Estimated Start| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline3 Finish| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline3 Fixed Cost| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline3 Fixed Cost Accrual| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline3 Start| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline3 Work| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline4 Cost| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline4 Duration| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline4 Duration Units| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline4 Estimated Duration| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline4 Estimated Finish| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline4 Estimated Start| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline4 Finish| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline4 Fixed Cost| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline4 Fixed Cost Accrual| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline4 Start| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline4 Work| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline5 Cost| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline5 Duration| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline5 Duration Units| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline5 Estimated Duration| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline5 Estimated Finish| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline5 Estimated Start| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline5 Finish| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline5 Fixed Cost| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline5 Fixed Cost Accrual| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline5 Start| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline5 Work| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline6 Cost| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline6 Duration| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline6 Duration Units| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline6 Estimated Duration| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline6 Estimated Finish| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline6 Estimated Start| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline6 Finish| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline6 Fixed Cost| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline6 Fixed Cost Accrual| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline6 Start| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline6 Work| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline7 Cost| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline7 Duration| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline7 Duration Units| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline7 Estimated Duration| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline7 Estimated Finish| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline7 Estimated Start| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline7 Finish| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline7 Fixed Cost| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline7 Fixed Cost Accrual| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline7 Start| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline7 Work| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline8 Cost| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline8 Duration| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline8 Duration Units| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline8 Estimated Duration| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline8 Estimated Finish| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline8 Estimated Start| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline8 Finish| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline8 Fixed Cost| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline8 Fixed Cost Accrual| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline8 Start| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline8 Work| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline9 Cost| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline9 Duration| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline9 Duration Units| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline9 Estimated Duration| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline9 Estimated Finish| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline9 Estimated Start| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline9 Finish| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline9 Fixed Cost| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline9 Fixed Cost Accrual| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline9 Start| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline9 Work| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline10 Cost| | | |✓| | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline10 Deliverable Finish| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline10 Duration| | | |✓| | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline10 Duration Units| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline10 Estimated Duration| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline10 Estimated Finish| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline10 Estimated Start| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline10 Finish| | | |✓| | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline10 Fixed Cost| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline10 Fixed Cost Accrual| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline10 Start| | | |✓| | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline10 Work| | | |✓| | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline Budget Cost| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline Budget Work| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline Cost| |✓|✓| | | | |✓|✓|✓|✓| | | |✓|✓|✓| | | | | | | 
Baseline Deliverable Finish| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline Deliverable Start| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline Duration|✓|✓| |✓| | | |✓|✓|✓|✓| |✓| |✓|✓|✓| | | | | | | 
Baseline Duration Units| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline Estimated Duration| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline Estimated Finish| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline Estimated Start| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline Finish|✓|✓|✓|✓| | | |✓|✓|✓|✓| | | |✓|✓|✓| | | | | | | 
Baseline Fixed Cost| | | | | | | | |✓| | | | | |✓|✓| | | | | | | | 
Baseline Fixed Cost Accrual| | | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline Start|✓|✓|✓|✓| | | |✓|✓|✓|✓| | | |✓|✓|✓| | | | | | | 
Baseline Work| | | | | | | |✓|✓|✓|✓| | | |✓|✓|✓| | | | | | | 

### Custom Fields
Field|Asta (PP)|ConceptDraw PROJECT (CDP)|Deltek OpenPlan (BK3)|FastTrack (FTS)|GanttDesigner (GNT)|GanttProject (GAN)|Merlin (SQLITE)|Microsoft (MPD)|Microsoft (MPP)|Microsoft (MPX)|Microsoft (MSPDI)|P3 (BTRIEVE)|Phoenix (PPX)|Planner (XML)|Primavera (PMXML)|Primavera (SQLITE)|Primavera (XER)|Project Commander (PC)|ProjectLibre (POD)|SDEF (SDEF)|Sage (SCHEDULE_GRID)|SureTrak (STW)|Synchro (SP)|TurboProject (PEP)
---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---
Cost1| | | |✓| | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Cost2| | | |✓| | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Cost3| | | | | | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Cost4| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Cost5| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Cost6| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Cost7| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Cost8| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Cost9| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Cost10| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Date1| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Date2| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Date3| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Date4| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Date5| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Date6| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Date7| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Date8| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Date9| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Date10| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Duration1| | | | | | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Duration1 Units| | | | | | | | |✓| | | | | | | | | | | | | | | 
Duration2| | | | | | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Duration2 Units| | | | | | | | |✓| | | | | | | | | | | | | | | 
Duration3| | | | | | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Duration3 Units| | | | | | | | |✓| | | | | | | | | | | | | | | 
Duration4| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Duration4 Units| | | | | | | | |✓| | | | | | | | | | | | | | | 
Duration5| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Duration5 Units| | | | | | | | |✓| | | | | | | | | | | | | | | 
Duration6| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Duration6 Units| | | | | | | | |✓| | | | | | | | | | | | | | | 
Duration7| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Duration7 Units| | | | | | | | |✓| | | | | | | | | | | | | | | 
Duration8| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Duration8 Units| | | | | | | | |✓| | | | | | | | | | | | | | | 
Duration9| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Duration9 Units| | | | | | | | |✓| | | | | | | | | | | | | | | 
Duration10| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Duration10 Units| | | | | | | | |✓| | | | | | | | | | | | | | | 
Finish1| | | | | | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Finish2| | | | | | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Finish3| | | | | | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Finish4| | | | | | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Finish5| | | | | | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Finish6| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Finish7| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Finish8| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Finish9| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Finish10| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Flag1| | | |✓| | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Flag2| | | |✓| | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Flag3| | | |✓| | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Flag4| | | |✓| | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Flag5| | | |✓| | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Flag6| | | |✓| | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Flag7| | | |✓| | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Flag8| | | |✓| | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Flag9| | | |✓| | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Flag10| | | |✓| | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Flag11| | | |✓| | | |✓|✓| |✓| | | | | | | | | | | | | 
Flag12| | | |✓| | | |✓|✓| |✓| | | | | | | | | | | | | 
Flag13| | | |✓| | | |✓|✓| |✓| | | | | | | | | | | | | 
Flag14| | | |✓| | | |✓|✓| |✓| | | | | | | | | | | | | 
Flag15| | | |✓| | | |✓|✓| |✓| | | | | | | | | | | | | 
Flag16| | | |✓| | | |✓|✓| |✓| | | | | | | | | | | | | 
Flag17| | | |✓| | | |✓|✓| |✓| | | | | | | | | | | | | 
Flag18| | | |✓| | | |✓|✓| |✓| | | | | | | | | | | | | 
Flag19| | | |✓| | | |✓|✓| |✓| | | | | | | | | | | | | 
Flag20| | | |✓| | | |✓|✓| |✓| | | | | | | | | | | | | 
Number1| | | |✓| | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Number2| | | |✓| | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Number3| | | |✓| | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Number4| | | |✓| | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Number5| | | |✓| | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Number6| | | |✓| | | |✓|✓| |✓| | | | | | | | | | | | | 
Number7| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Number8| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Number9| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Number10| | | |✓| | | |✓|✓| |✓| | | | | | | | | | | | | 
Number11| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Number12| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Number13| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Number14| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Number15| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Number16| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Number17| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Number18| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Number19| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Number20| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Outline Code1| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Outline Code1 Index| | | | | | | | |✓| | | | | | | | | | | | | | | 
Outline Code2| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Outline Code2 Index| | | | | | | | |✓| | | | | | | | | | | | | | | 
Outline Code3| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Outline Code3 Index| | | | | | | | |✓| | | | | | | | | | | | | | | 
Outline Code4| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Outline Code4 Index| | | | | | | | |✓| | | | | | | | | | | | | | | 
Outline Code5| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Outline Code5 Index| | | | | | | | |✓| | | | | | | | | | | | | | | 
Outline Code6| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Outline Code6 Index| | | | | | | | |✓| | | | | | | | | | | | | | | 
Outline Code7| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Outline Code7 Index| | | | | | | | |✓| | | | | | | | | | | | | | | 
Outline Code8| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Outline Code8 Index| | | | | | | | |✓| | | | | | | | | | | | | | | 
Outline Code9| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Outline Code9 Index| | | | | | | | |✓| | | | | | | | | | | | | | | 
Outline Code10| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Outline Code10 Index| | | | | | | | |✓| | | | | | | | | | | | | | | 
Start1| | | | | | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Start2| | | | | | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Start3| | | | | | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Start4| | | | | | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Start5| | | | | | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Start6| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Start7| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Start8| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Start9| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Start10| | | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Text1| | | |✓| | | |✓|✓|✓|✓| | | | | | | | | |✓| | | 
Text2| | | |✓| | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Text3| | | |✓| | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Text4| | | |✓| | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Text5| | | |✓| | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Text6| | | |✓| | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Text7| | | |✓| | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Text8| | | |✓| | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Text9| | | |✓| | | |