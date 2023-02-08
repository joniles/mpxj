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
Field|Asta (PP)|ConceptDraw PROJECT (CDP)|FastTrack (FTS)|GanttDesigner (GNT)|GanttProject (GAN)|Merlin (SQLITE)|Microsoft (MPD)|Microsoft (MPP)|Microsoft (MPX)|Microsoft (MSPDI)|P3 (BTRIEVE)|Phoenix (PPX)|Planner (XML)|Primavera (PMXML)|Primavera (SQLITE)|Primavera (XER)|Project Commander (PC)|ProjectLibre (POD)|SDEF (SDEF)|Sage (SCHEDULE_GRID)|SureTrak (STW)|Synchro (SP)|TurboProject (PEP)
---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---
AM Text|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Actual Cost| | | | | | | | |✓| | | | | | | | | | | | | | 
Actual Duration| | | | | | | | |✓| | | | | | | | | | | | | | 
Actual Start| | | | | | | | |✓| | | | | | | | | | | | | | 
Actual Work| | | | | | | | |✓| | | | | | | | | | | | | | 
Actuals In Sync| | | | | | | | | |✓| | | | | | | | | | | | | 
Application Version| | | | | | | |✓| |✓| | | | | | | |✓| | | | | 
Approved Budget| | | | | | | | | | | | | | |✓| | | | | | | | 
Author|✓| | | | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Auto Add New Resources and Tasks|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Auto Filter| | | | | | | |✓| | | | | | | | | | | | | | | 
Auto Link|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Bar Text Date Format|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Category| | | | | | |✓|✓| | | | | | | | | | | | | | | 
Change| | | | | | | | | | | | | |✓|✓| | | | | | | | 
Comments| | | | | | | |✓|✓|✓| | | |✓|✓|✓| | | | | | | 
Company| | | | |✓| |✓|✓|✓|✓|✓| |✓| | | | | | | | | | 
Content Status| | | | | | | |✓| | | | | | | | | | | | | | | 
Content Type| | | | | | | |✓| | | | | | | | | | | | | | | 
Cost| | | | | | | | |✓| | | | | | | | | | | | | | 
Cost Status| | | | | | | | | | | | | |✓|✓| | | | | | | | 
Cost-Budget Variance| | | | | | | | | | | | | | |✓| | | | | | | | 
Creation Date| | | |✓| | |✓|✓| |✓| | | |✓| | | | | | | | | 
Critical Activity Type|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Critical Slack Limit| | | | | | | |✓| | | | | | | | | | | | | | | 
Currency Code| | | | | | | |✓| |✓| | | | | | | | | | | | | 
Currency Digits|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Currency Symbol|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Currency Symbol Position|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Current Date|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Current Score| | | | | | | | | | | | | | |✓| | | | | | | | 
Custom Properties| | | | | | | |✓| | | | | |✓|✓|✓| | | | | | | 
Date Format|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Date Order|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Date Separator|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Days per Month|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Decimal Separator|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Default Calendar Unique ID|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Default Duration Is Fixed| | | | | | | | |✓| | | | | | | | | | | | | | 
Default End Time| | | | | | |✓|✓| |✓| | | | | | | |✓| | | | | 
Default Overtime Rate|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Default Standard Rate|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Default Start Time|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Default Work Units|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Document Version| | | | | | | |✓| | | | | | | | | | | | | | | 
Duration|✓| | | | | | | |✓| | | | | | | | | | | | | | 
Earned Value Method| | | | | | | | | |✓| | | | | | | | | | | | | 
Editable Actual Costs| | | | | | | |✓| |✓| | | | | | | | | | | | | 
Editing Time| | | | | | | |✓| | | | | | | | | | | | | | | 
Effort (FTE)| | | | | | | | | | | | | |✓|✓| | | | | | | | 
Export Flag| | | | | | | | | | | | | |✓| |✓| | | | | | | 
Extended Creation Date| | | | | | | | | |✓| | | | | | | | | | | | | 
File Application|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
File Type|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Finish Date|✓|✓|✓|✓|✓| |✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Fiscal Year Start| | | | | | | |✓| | | | | | | | | | | | | | | 
Fiscal Year Start Month|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Forecasted Revenue| | | | | | | | | | | | | |✓|✓| | | | | | | | 
Full Application Name| | | | | | | |✓| | | | | | | | | | | | | | | 
GUID| | | | | |✓| | | |✓| | | |✓|✓|✓| | | | | | | 
Honor Constraints| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Hyperlink Base| | | | | | | |✓| | | | | | | | | | | | | | | 
Inserted Projects Like Summary| | | | | | | | | |✓| | | | | | | | | | | | | 
Keywords| | | | | | |✓|✓|✓| | | | | | | | | | | | | | 
Language| | | | | | | |✓| | | | | | | | | | | | | | | 
Last Author| | | | | | | |✓| | | | | | | | | | | | | | | 
Last Printed| | | | | | | |✓| | | | | | | | | | | | | | | 
Last Saved|✓| | |✓| | |✓|✓| |✓| | | | |✓| | | | | | | | 
MPP File Type| | | | | | | |✓| | | | | | | | | | | | | | | 
MPX Code Page|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
MPX Delimiter|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
MPX File Version|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
MPX Program Name|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Manager| | | | | | |✓|✓|✓|✓| | |✓| | | | |✓|✓| | | | 
Market Risk  (20%)| | | | | | | | | | | | | |✓| | | | | | | | | 
Microsoft Project Server URL|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Minutes per Day|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Minutes per Month|✓|✓|✓|✓|✓|✓| |✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Minutes per Week|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Minutes per Year|✓|✓|✓|✓|✓|✓| |✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Multiple Critical Paths| | | | | | |✓|✓| | | | | | | | | | | | | | | 
Must Finish By| | | | | | | | | | | | | |✓| |✓| | | | | | | 
Name|✓| | |✓|✓|✓|✓| | |✓|✓|✓|✓|✓|✓|✓| |✓| | | | | 
New Tasj Start Is Project Start|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
New Tasks Are Effort Driven| | | | | | |✓| | |✓| | | | | | | | | | | | | 
New Tasks Are Manual|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓| |✓|✓|✓|✓|✓
New Tasks Estimated|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Overall Status| | | | | | | | | | | | | |✓|✓| | | | | | | | 
PM Text|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Percentage Complete| | | | | | | | |✓| | | | | | | | | | | | | | 
Planned Start| | | | | | | | | | | | | |✓|✓|✓| | | | | | | 
Presentation Format| | | | | | | |✓| | | | | | | | | | | | | | | 
Previous Score| | | | | | | | | | | | | | |✓| | | | | | | | 
Product Innovation  (25%)| | | | | | | | | | | | | |✓| | | | | | | | | 
Product ROI  (30%)| | | | | | | | | | | | | |✓| | | | | | | | | 
Production Impact  (15%)| | | | | | | | | | | | | |✓| | | | | | | | | 
Project Externally Edited| | | | | | | | | |✓| | | | | | | | | | | | | 
Project File Path| | | | | | | |✓| | | | | | | | | | | | | | | 
Project ID| | | | | | | | | | | | | |✓|✓|✓| | | | | | | 
Project Title|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Projected Final Cost| | | | | | | | | | | | | | |✓| | | | | | | | 
Quality| | | | | | | | | | | | | |✓|✓| | | | | | | | 
ROI (%)| | | | | | | | | | | | | | |✓| | | | | | | | 
Resource Capacity  (10%)| | | | | | | | | | | | | |✓| | | | | | | | | 
Revision| | | | | | | |✓| |✓| | | | | | | | | | | | | 
Risk Assessment| | | | | | | | | | | | | |✓|✓| | | | | | | | 
Safety| | | | | | | | | | | | | |✓|✓| | | | | | | | 
Schedule From|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Schedule Status| | | | | | | | | | | | | |✓|✓| | | | | | | | 
Scheduled Finish| | | | | | | | | | | | | |✓|✓|✓| | | | | | | 
Scope Status| | | | | | | | | | | | | |✓|✓| | | | | | | | 
Short Application Name| | | | | | | |✓| | | | | | | | | | | | | | | 
Show Project Summary Task| | | | | | | |✓| | | | | | | | | | | | | | | 
Six Sigma Customer Inquires Reduced| | | | | | | | | | | | | |✓|✓| | | | | | | | 
Six Sigma Defects| | | | | | | | | | | | | |✓|✓| | | | | | | | 
Six Sigma Estimated FTE Saved| | | | | | | | | | | | | |✓|✓| | | | | | | | 
Six Sigma Total Savings ($)| | | | | | | | | | | | | |✓|✓| | | | | | | | 
Split In Progress Tasks| | | | | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Start Date|✓|✓|✓|✓|✓| |✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Start Variance| | | | | | | | |✓| | | | | | | | | | | | | | 
Status Date|✓| | | | | |✓|✓| |✓|✓|✓| |✓|✓|✓| | |✓| | | | 
Subject| | | | | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Template| | | | | | | |✓| | | | | | | | | | | | | | | 
Thousands Separator|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Time Format|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Time Separator|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Total Benefits $| | | | | | | | | | | | | |✓|✓| | | | | | | | 
Unique ID| | | | | | | | | | | | | |✓|✓|✓| | | | | | | 
Updating Task Status Updates Resource Status|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Week Start Day|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Work| | | | | | | | |✓| | | | | | | | | | | | | | 
Work 2| | | | | | | | |✓| | | | | | | | | | | | | | 

### Baseline Fields
Field|Asta (PP)|ConceptDraw PROJECT (CDP)|FastTrack (FTS)|GanttDesigner (GNT)|GanttProject (GAN)|Merlin (SQLITE)|Microsoft (MPD)|Microsoft (MPP)|Microsoft (MPX)|Microsoft (MSPDI)|P3 (BTRIEVE)|Phoenix (PPX)|Planner (XML)|Primavera (PMXML)|Primavera (SQLITE)|Primavera (XER)|Project Commander (PC)|ProjectLibre (POD)|SDEF (SDEF)|Sage (SCHEDULE_GRID)|SureTrak (STW)|Synchro (SP)|TurboProject (PEP)
---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---
Baseline1 Date| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline2 Date| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline3 Date| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline4 Date| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline5 Date| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline6 Date| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline7 Date| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline8 Date| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline9 Date| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline10 Date| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline Cost| | | | | | | | |✓| | | | | | | | | | | | | | 
Baseline Date| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline Duration| | | | | | | | |✓| | | | | | | | | | | | | | 
Baseline Finish| | | | | | | | |✓| | | | | | | | | | | | | | 
Baseline Project Unique ID| | | | | | | | | | | | | |✓|✓|✓| | | | | | | 
Baseline Start| | | | | | | | |✓| | | | | | | | | | | | | | 
Baseline Work| | | | | | | | |✓| | | | | | | | | | | | | | 

## Task
### Core Fields
Field|Asta (PP)|ConceptDraw PROJECT (CDP)|FastTrack (FTS)|GanttDesigner (GNT)|GanttProject (GAN)|Merlin (SQLITE)|Microsoft (MPD)|Microsoft (MPP)|Microsoft (MPX)|Microsoft (MSPDI)|P3 (BTRIEVE)|Phoenix (PPX)|Planner (XML)|Primavera (PMXML)|Primavera (SQLITE)|Primavera (XER)|Project Commander (PC)|ProjectLibre (POD)|SDEF (SDEF)|Sage (SCHEDULE_GRID)|SureTrak (STW)|Synchro (SP)|TurboProject (PEP)
---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---
% Complete|✓|✓|✓|✓|✓| |✓|✓|✓|✓|✓|✓|✓|✓|✓|✓| | |✓| |✓|✓| 
% Work Complete| | | | | | |✓|✓|✓|✓| | |✓|✓|✓|✓| | | | | | | 
(New)| | | | | | | | | | | | | | | |✓| | | | | | | 
(New)-1| | | | | | | | | | | | | | | |✓| | | | | | | 
(New)-2| | | | | | | | | | | | | | | |✓| | | | | | | 
(New)-3| | | | | | | | | | | | | | | |✓| | | | | | | 
(New)-4| | | | | | | | | | | | | | | |✓| | | | | | | 
(New)-5| | | | | | | | | | | | | | | |✓| | | | | | | 
(New)-6| | | | | | | | | | | | | | | |✓| | | | | | | 
(New)-7| | | | | | | | | | | | | | | |✓| | | | | | | 
(New)-8| | | | | | | | | | | | | | | |✓| | | | | | | 
(New)-9| | | | | | | | | | | | | | | |✓| | | | | | | 
(New)-10| | | | | | | | | | | | | | | |✓| | | | | | | 
(New)-11| | | | | | | | | | | | | | | |✓| | | | | | | 
(New)-12| | | | | | | | | | | | | | | |✓| | | | | | | 
(New)-13| | | | | | | | | | | | | | | |✓| | | | | | | 
(New)-14| | | | | | | | | | | | | | | |✓| | | | | | | 
(New)-15| | | | | | | | | | | | | | | |✓| | | | | | | 
(New)-16| | | | | | | | | | | | | | | |✓| | | | | | | 
(New)-17| | | | | | | | | | | | | | | |✓| | | | | | | 
(New)-18| | | | | | | | | | | | | | | |✓| | | | | | | 
(New)-19| | | | | | | | | | | | | | | |✓| | | | | | | 
(New)-20| | | | | | | | | | | | | | | |✓| | | | | | | 
(New)-21| | | | | | | | | | | | | | | |✓| | | | | | | 
(New)-22| | | | | | | | | | | | | | | |✓| | | | | | | 
(New)-23| | | | | | | | | | | | | | | |✓| | | | | | | 
(New)-24| | | | | | | | | | | | | | | |✓| | | | | | | 
(New)-25| | | | | | | | | | | | | | | |✓| | | | | | | 
(New)-26| | | | | | | | | | | | | | | |✓| | | | | | | 
(New)-27| | | | | | | | | | | | | | | |✓| | | | | | | 
(New)-28| | | | | | | | | | | | | | | |✓| | | | | | | 
(New)-29| | | | | | | | | | | | | | | |✓| | | | | | | 
(New)-30| | | | | | | | | | | | | | | |✓| | | | | | | 
-Артикул| | | | | | | | | | | | | |✓| | | | | | | | | 
-Ед. изм.| | | | | | | | | | | | | |✓| | | | | | | | | 
-Производитель| | | | | | | | | | | | | |✓| | | | | | | | | 
-ФО проект| | | | | | | | | | | | | |✓| | | | | | | | | 
ACWP| | | | | | |✓| | |✓| | | | | | | | | | | | | 
Access Restraints| | | | | | | | | | | | | |✓| |✓| | | | | | | 
Active|✓|✓|✓|✓|✓|✓| |✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Activity Codes|✓|✓|✓|✓|✓|✓| |✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Activity ID|✓| | | | | | | | | |✓|✓| |✓|✓|✓| | |✓| |✓|✓| 
Activity ID's| | | | | | | | | | | | | | | |✓| | | | | | | 
Activity Status| | | | | | | | | | | | | |✓|✓|✓| | | | | | | 
Activity Text Field| | | | | | | | | | | | | |✓| | | | | | | | | 
Activity Type| | | | | | | | | | | |✓| |✓|✓|✓| | | | | | | 
Actual Cost| | | | | | |✓|✓|✓|✓| | | |✓|✓|✓| | |✓| | | | 
Actual Duration|✓| |✓| | | |✓|✓|✓|✓| |✓|✓|✓|✓|✓| | | | |✓| | 
Actual Duration Units| | | | | | | |✓| |✓| | | | | | | | | | | | | 
Actual Finish|✓| |✓| | | |✓|✓|✓|✓|✓|✓| |✓|✓|✓| | |✓| |✓|✓| 
Actual Overtime Cost| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Actual Overtime Work| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Actual Start|✓| |✓| | | |✓|✓|✓|✓|✓|✓|✓|✓|✓|✓| | |✓| |✓|✓| 
Actual Work| | | | | | |✓|✓|✓|✓| | |✓|✓|✓|✓| | | | | | | 
Actual Work Protected| | | | | | | |✓| | | | | | | | | | | | | | | 
Agrupamento| | | | | | | | | | | | | |✓| |✓| | | | | | | 
Apontador| | | | | | | | | | | | | |✓| |✓| | | | | | | 
As Late As Possible Constraint| | | | | | | | | | | | | |✓| |✓| | | | | | | 
BOH| | | | | | | | | | | | | |✓| |✓| | | | | | | 
Bar Field|✓| | | | | | | | | | | | | | | | | | | | | | 
Bid Item| | | | | | | | | | | | | | | | | | |✓| | | | 
Board Status ID| | | | | | | |✓| | | | | | | | | | | | | | | 
Boolean Field|✓| | | | | | | | | | | | | | | | | | | | | | 
Budget Cost| | | | | | | |✓| | | | | | | | | | | | | | | 
Budget Work| | | | | | | |✓| | | | | | | | | | | | | | | 
CUT THESE DURATIONS BY ½| | | | | | | | | | | | | |✓| |✓| | | | | | | 
CV| | | | | | | | | |✓| | | | | | | | | | | | | 
CW Pre Req| | | | | | | | | | | | | | | |✓| | | | | | | 
Calendar Unique ID|✓| | | | | | |✓| |✓| |✓| |✓|✓|✓| | |✓| | |✓| 
Category of Work| | | | | | | | | | | | | | | | | | |✓| | | | 
Changes to Subcon| | | | | | | | | | | | | | | |✓| | | | | | | 
Check Flag| | | | | | | | | | | | | | | |✓| | | | | | | 
Comment| | | | | | | | | | | | | | | |✓| | | | | | | 
Comments| | | | | | | | | | | | | |✓| |✓| | | | | | | 
Complete Through|✓| |✓| | | | |✓|✓|✓|✓|✓|✓|✓|✓|✓| | |✓| |✓|✓| 
ComplyPro Data| | | | | | | | | | | | | |✓| | | | | | | | | 
Constraint Date|✓| |✓| |✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓| |✓|✓|✓| |✓| 
Constraint Type|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Contact| | | | | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Control Number| | | | | | | | | | | | | | | |✓| | | | | | | 
Cost| |✓| |✓| | |✓|✓|✓|✓| |✓| |✓|✓|✓| | |✓| | | | 
Cost Activity Field| | | | | | | | | | | | | |✓| | | | | | | | | 
Cost Code| | | | | | | | | | | | | | | |✓| | | | | | | 
Cost Performance| | | | | | | | | | | | | | |✓| | | | | | | | 
Cost Variance| |✓| | | | |✓|✓|✓|✓| | | |✓|✓|✓| | | | | | | 
Created| | | | | | |✓|✓|✓|✓| |✓| | |✓|✓| |✓| | | | | 
Critical|✓| |✓| | | | |✓|✓|✓|✓|✓| |✓|✓|✓| |✓|✓| |✓| | 
Custom Boolean| | | | |✓| | | | | | | | | | | | | | | | | | 
Custom Date| | | | |✓| | | | | | | | | | | | | | | | | | 
Custom Double| | | | |✓| | | | | | | | | | | | | | | | | | 
Custom Integer| | | | |✓| | | | | | | | | | | | | | | | | | 
Custom Text| | | | |✓| | | | | | | | | | | | | | | | | | 
Código| | | | | | | | | | | | | | | |✓| | | | | | | 
Date Field|✓| | | | | | | | | | | | | | | | | | | | | | 
Deadline|✓|✓| |✓| | |✓|✓| |✓| | | | |✓|✓| | | | | | | 
Department| | | | | | | | | | | | | | | | | | | | |✓| | 
Duration|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓| 
Duration Units| | | | | | | |✓| | | | | | | | | | | | | | | 
Duration Variance| |✓|✓| | | |✓|✓|✓|✓| |✓| |✓|✓|✓| | | | | | | 
EB or WB Cavern| | | | | | | | | | | | | |✓| |✓| | | | | | | 
Early Finish|✓| |✓| | | |✓|✓|✓|✓|✓|✓| | |✓|✓| | |✓| |✓| |✓
Early Start|✓| |✓| | | |✓|✓|✓|✓|✓|✓| | |✓|✓| | |✓| |✓| |✓
Earned Value Method| | | | | | | |✓| | | | | | | | | | | | | | | 
Effort Driven| | |✓| | | |✓|✓| |✓| | |✓| | | | |✓| | | | | 
Estimated| | | | | | |✓|✓| |✓| | | | | | | |✓| | | | | 
Expense Items|✓|✓|✓|✓|✓|✓| |✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
External Early Start| | | | | | | | | | | | | |✓| |✓| | | | | | | 
External Late Finish| | | | | | | | | | | | | |✓| | | | | | | | | 
External Task| | | | | | | |✓| | | | | | | | | | | | | | | 
Fase| | | | | | | | | | | | | |✓| |✓| | | | | | | 
Feature of Work| | | | | | | | | | | | | | | | | | |✓| | | | 
Finish|✓|✓|✓|✓|✓| |✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Finish Date Activity Field| | | | | | | | | | | | | |✓| | | | | | | | | 
Finish Day of Week| | | | | | | | | | | | | |✓| | | | | | | | | 
Finish Slack|✓| |✓| | | | |✓|✓|✓|✓|✓| |✓|✓|✓| | |✓|✓|✓| | 
Finish Variance| |✓|✓| | | | |✓|✓|✓| | | |✓|✓|✓| | | | | | | 
Fitout Sequence| | | | | | | | | | | | | | | |✓| | | | | | | 
Fixed Cost| | |✓| | | |✓|✓| | | | | | | | | | | | | | | 
Fixed Cost Accrual| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Floor_Area|✓| | | | | | | | | | | | | | | | | | | | | | 
Free Slack| | |✓| | | |✓|✓|✓|✓|✓|✓| | |✓|✓| | | | | | | 
GUID| |✓|✓| | |✓| |✓| |✓| |✓| |✓|✓|✓| | |✓| | |✓| 
Hammock Code| | | | | | | | | | | | | | | | | | |✓| | | | 
Hide Bar| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
High Risk Activities| | | | | | | | | | | | | | | |✓| | | | | | | 
Hyperlink| | | | |✓| |✓|✓| | | | | | | | | | | | | |✓| 
Hyperlink Address| | | | | | |✓|✓| | | | | | | | | | | | | | | 
Hyperlink Data| | | | | | | |✓| | | | | | | | | | | | | | | 
Hyperlink Screen Tip| | | | | | | |✓| | | | | | | | | | | | | | | 
Hyperlink SubAddress| | | | | | | |✓| | | | | | | | | | | | | | | 
ID|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Ignore Resource Calendar| | |✓| | | | |✓| |✓| | | | |✓|✓| | | | | | | 
Imported Early Finish| | | | | | | | | | | | | | | |✓| | | | | | | 
Imported Early Start| | | | | | | | | | | | | | | |✓| | | | | | | 
Imported Late Finish| | | | | | | | | | | | | | | |✓| | | | | | | 
Imported Late Start| | | | | | | | | | | | | | | |✓| | | | | | | 
Indicator Activity Field| | | | | | | | | | | | | |✓| | | | | | | | | 
Indicator Test| | | | | | | | | | | | | |✓| |✓| | | | | | | 
Integer Activity Field| | | | | | | | | | | | | |✓| | | | | | | | | 
Integer Field|✓| | | | | | | | | | | | | | | | | | | | | | 
LL Platform| | | | | | | | | | | | | |✓| |✓| | | | | | | 
LastPlannerConstraints| | | | | | | | | | | | | | | |✓| | | | | | | 
Late Finish|✓| |✓| | | |✓|✓|✓|✓|✓|✓| | |✓|✓| | |✓|✓|✓| |✓
Late Start|✓| |✓| | | |✓|✓|✓|✓|✓|✓| | |✓|✓| | |✓|✓|✓| |✓
Level Assignments| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Leveling Can Split| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Leveling Delay| | | | | | | |✓|✓|✓| | | | | | | | | | | | | 
Leveling Delay Units| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Linha do Tempo| | | | | | | | | | | | | |✓| |✓| | | | | | | 
Longest Path| | | | | | | | | | | | | | |✓|✓| | | | | | | 
MS-5| | | | | | | | | | | | | |✓| |✓| | | | | | | 
MS-6| | | | | | | | | | | | | |✓| |✓| | | | | | | 
MSP Activity ID| | | | | | | | | | | | | |✓| |✓| | | | | | | 
Mail| | | | | | | | | | | | | | | | | | | | |✓| | 
Manager| | | | | | | | | | | | | | | | | | | | |✓| | 
Manual Duration| | | | | | | |✓| |✓| | | | | | | | | | | | | 
Manual Duration Units| | | | | | | |✓| | | | | | | | | | | | | | | 
Marked| | | | | | |✓|✓| | | | | | | | | | | | | | | 
Milestone|✓|✓| | |✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓| |✓| |✓|✓| 
New UDF| | | | | | | | | | | | | |✓| | | | | | | | | 
Notes|✓|✓|✓|✓| | |✓|✓|✓|✓| |✓|✓|✓|✓|✓| | | |✓| |✓| 
Notes_ProgressUpdates| | | | | | | | | | | | | | | |✓| | | | | | | 
Number Activity Field| | | | | | | | | | | | | |✓| | | | | | | | | 
Ordinal Number| | | | | | | | | | | | | | | |✓| | | | | | | 
Original Cost| | | | | | | | | | | | | | | |✓| | | | | | | 
Outline Level|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓| |✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Outline Number|✓|✓| |✓|✓|✓|✓|✓|✓|✓|✓| | |✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Overall Percent Complete|✓| | | | | | | | | | | | | | | | | | | | | | 
Overallocated| | | | | | | | | |✓| | | | | | | | | | | | | 
Overtime Cost| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Overtime Work| | | | | | | | | |✓| | | | | | | | | | | | | 
PAC at Esc Exterior Walls| | | | | | | | | | | | | |✓| |✓| | | | | | | 
PMS_ID| | | | | | | | | | | | | | | |✓| | | | | | | 
Parent Task Unique ID| | | | | | | |✓| | | | | | | | | | | | | | | 
Percent Complete Type| | | | | | | | | | | | | |✓|✓|✓| | | | | | | 
Peso| | | | | | | | | | | | | |✓| |✓| | | | | | | 
Phase of Work| | | | | | | | | | | | | | | | | | |✓| | | | 
Physical % Complete| | | | | | | |✓| |✓| |✓| |✓|✓|✓| | | | | | | 
Planned Cost| | | | | | | | | | | | | |✓|✓|✓| | | | | | | 
Planned Duration| | | | | | | | | | | | | |✓|✓|✓| | | | | | | 
Planned Finish| | | | | | | | | | | | | |✓|✓|✓| | | | | | | 
Planned Start| | | | | | | | | | | | | |✓|✓|✓| | | | | | | 
Planned Work| | | | | | | | | | | | | |✓|✓|✓| | | | | | | 
Plot_No|✓| | | | | | | | | | | | | | | | | | | | | | 
Pour Activities| | | | | | | | | | | | | |✓| |✓| | | | | | | 
Precast Fab Tag| | | | | | | | | | | | | |✓| |✓| | | | | | | 
Predecessors|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Preleveled Finish| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Preleveled Start| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Primary Resource Unique ID| | | | | | | | | | | | | |✓|✓|✓| | | | | | | 
Primavera P6 Task Type| | | | | | | | | | | | | | | |✓| | | | | | | 
Priority| |✓| | |✓| |✓|✓|✓|✓| | |✓|✓|✓|✓| | | | | | | 
Project| | | | | | | |✓|✓|✓| | | | |✓|✓| | | | | | | 
Q Cantidad Planificada| | | | | | | | | | | | | | | |✓| | | | | | | 
Q Cantidad Realizada| | | | | | | | | | | | | | | |✓| | | | | | | 
Q Unidad de Medida| | | | | | | | | | | | | | | |✓| | | | | | | 
QA Checked|✓| | | | | | | | | | | | | | | | | | | | | | 
QA/QC| | | | | | | | | | | | | | |✓| | | | | | | | 
REV| | | | | | | | | | | | | | | |✓| | | | | | | 
Recalc Outline Codes| | | | | | | |✓| | | | | | | | | | | | | | | 
Recurring| | | | | | | |✓|✓|✓| | | | | | | | | | | | | 
Recurring Data| | | | | | | |✓| | | | | | | | | | | | | | | 
Regular Work| | | | | | |✓| | |✓| | | | | | | | | | | | | 
Related_Documents|✓| | | | | | | | | | | | | | | | | | | | | | 
Remaining Cost| | | | | | |✓|✓|✓|✓| | | |✓|✓|✓| | |✓| | | | 
Remaining Duration|✓| | | | | |✓|✓|✓|✓|✓|✓| |✓|✓|✓| |✓|✓| |✓|✓| 
Remaining Early Finish| | | | | | | | | | | | | |✓|✓|✓| | | | | | | 
Remaining Early Start| | | | | | | | | | | | | |✓|✓|✓| | | | | | | 
Remaining Late Finish| | | | | | | | | | | | | |✓|✓|✓| | | | | | | 
Remaining Late Start| | | | | | | | | | | | | |✓|✓|✓| | | | | | | 
Remaining Overtime Cost| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Remaining Overtime Work| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Remaining Work| | | | | | |✓|✓|✓|✓| | |✓|✓|✓|✓| | | | | | | 
Resource Names| | |✓| | | | | |✓| | | | | | | | | | | | | | 
Responsibility Code| | | | | | | | | | | | | | | | | | |✓| | | | 
Resume|✓| | | | | |✓|✓|✓|✓| | | | | |✓| | | | | | | 
Resume No Earlier Than| | | | | | | |✓| | | | | | | | | | | | | | | 
Rollup| | | | | | |✓|✓|✓|✓| | | | | | | | | | | | | 
SCOPE_ID| | | | | | | | | | | | | | | |✓| | | | | | | 
SIN06 Critical Path 1| | | | | | | | | | | | | | | |✓| | | | | | | 
SIN06 Critical Path 2| | | | | | | | | | | | | | | |✓| | | | | | | 
SIN06 Critical Path 3| | | | | | | | | | | | | | | |✓| | | | | | | 
SIN06 Critical Path 4| | | | | | | | | | | | | | | |✓| | | | | | | 
SIN06 Near Critical Path 1| | | | | | | | | | | | | | | |✓| | | | | | | 
SIN06 Near Critical Path 2| | | | | | | | | | | | | | | |✓| | | | | | | 
SIN06 Near Critical Path 3| | | | | | | | | | | | | | | |✓| | | | | | | 
SIN06 Near Critical Path 4| | | | | | | | | | | | | | | |✓| | | | | | | 
Safety| | | | | | | | | | | | | | |✓| | | | | | | | 
Scheduled Duration| | | | | | | |✓| | | | | | | | | | | | | | | 
Scheduled Finish| | | | | | | |✓| | | | | | | | | | | | | | | 
Scheduled Start| | | | | | | |✓| | | | | | | | | | | | | | | 
Secondary Constraint Date| | | | | | | | | | | | | | | |✓| | | | | | | 
Secondary Constraint Type| | | | | | | | | | | | | | | |✓| | | | | | | 
Section| | | | | | | | | | | | | | | | | | | | |✓| | 
Sequence Number| | | | | | | | | | | | | |✓|✓|✓| | | | | | | 
Single Shift Duration| | | | | | | | | | | | | |✓| |✓| | | | | | | 
Splits| | | | | | | |✓| |✓| | | | | | | | | | | | | 
Sprint ID| | | | | | | |✓| | | | | | | | | | | | | | | 
Start|✓|✓|✓|✓|✓| |✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Start Date Activity Field| | | | | | | | | | | | | |✓| | | | | | | | | 
Start Day of Week| | | | | | | | | | | | | |✓| | | | | | | | | 
Start Slack|✓| |✓| | | | |✓|✓|✓|✓|✓| |✓|✓|✓| | |✓|✓|✓| | 
Start Variance| |✓|✓| | | | |✓|✓|✓| | | |✓|✓|✓| | | | | | | 
Steps|✓|✓|✓|✓|✓|✓| |✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Stop| | | | | | |✓|✓|✓|✓| | | | | | | | | | | | | 
String Field|✓| | | | | | | | | | | | | | | | | | | | | | 
Subcon| | | | | | | | | | | | | | | |✓| | | | | | | 
Subcontractor| | | | | | | | | | | | | |✓| | | | | | | | | 
Subproject| | | | | | |✓|✓| | | | | | | | | | | | | | | 
Subproject File| | | | | | |✓|✓| | | | | | | | | | | | | | | 
Subproject Task ID| | | | | | | |✓| | | | | | | | | | | | | | | 
Subproject Tasks Unique ID Offset| | | | | | | |✓| | | | | | | | | | | | | | | 
Subproject Unique Task ID| | | | | | | |✓| | | | | | | | | | | | | | | 
Successors|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Summary|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓| |✓|✓|✓|✓|✓|✓| | | | |✓|✓
Summary Progress| | | | | | | |✓| | | | | | | | | | | | | | | 
Suspend Date| | | | | | | | | | | | | | | |✓| | | | | | | 
Task Calendar GUID| | | | | | | |✓| | | | | | | | | | | | | | | 
Task Field|✓| | | | | | | | | | | | | | | | | | | | | | 
Task Mode| | | | | | | |✓| |✓| | | | | | | | | | | | | 
Task Name|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Total Slack|✓| |✓| | | | |✓|✓|✓|✓|✓| |✓|✓|✓| | |✓|✓|✓| | 
Track EB or WB| | | | | | | | | | | | | |✓| |✓| | | | | | | 
Type| |✓| | | | |✓|✓|✓|✓| | |✓|✓|✓|✓| | | | | | | 
URL Field|✓| | | | | | | | | | | | | | | | | | | | | | 
USE_Text_01|✓| | | | | | | | | | | | | | | | | | | | | | 
USE_Text_02|✓| | | | | | | | | | | | | | | | | | | | | | 
USE_Text_03|✓| | | | | | | | | | | | | | | | | | | | | | 
Unique ID|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓
Unique ID Successors| | | | | | | | |✓| | | | | | | | | | | | | | 
Update Finish| | | | | | | | | | | | | | |✓| | | | | | | | 
Update Notes| | | | | | | | | | | | | | | |✓| | | | | | | 
Update Start| | | | | | | | | | | | | | |✓| | | | | | | | 
User Remaining| | | | | | | | | | | | | | | |✓| | | | | | | 
User Text 1| | | | | | | | | | | | | | | |✓| | | | | | | 
User Text Select Prime| | | | | | | | | | | | | | | |✓| | | | | | | 
User_text1| | | | | | | | | | | | | | | |✓| | | | | | | 
WBS|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓| | |✓|✓|✓|✓|✓| |✓|✓| |✓|✓
WBS Text Field| | | | | | | | | | | | | |✓| | | | | | | | | 
Work| | |✓| | |✓|✓|✓|✓|✓| | |✓|✓|✓|✓|✓| | | | | | 
Work Variance| | | | | | | |✓|✓|✓| | | |✓|✓|✓| | | | | | | 
s_om| | | | | | | | | | | | | |✓| |✓| | | | | | | 
udf_a_crew size| | | | | | | | | | | | | | | |✓| | | | | | | 
user_text1| | | | | | | | | | | | | | | |✓| | | | | | | 
user_text2| | | | | | | | | | | | | | | |✓| | | | | | | 
user_text4| | | | | | | | | | | | | | | |✓| | | | | | | 
user_text8| | | | | | | | | | | | | | | |✓| | | | | | | 
user_text01| | | | | | | | | | | | | | | |✓| | | | | | | 

### Baseline Fields
Field|Asta (PP)|ConceptDraw PROJECT (CDP)|FastTrack (FTS)|GanttDesigner (GNT)|GanttProject (GAN)|Merlin (SQLITE)|Microsoft (MPD)|Microsoft (MPP)|Microsoft (MPX)|Microsoft (MSPDI)|P3 (BTRIEVE)|Phoenix (PPX)|Planner (XML)|Primavera (PMXML)|Primavera (SQLITE)|Primavera (XER)|Project Commander (PC)|ProjectLibre (POD)|SDEF (SDEF)|Sage (SCHEDULE_GRID)|SureTrak (STW)|Synchro (SP)|TurboProject (PEP)
---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---
Baseline1 Cost| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline1 Duration| | |✓| | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline1 Duration Units| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline1 Estimated Duration| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline1 Estimated Finish| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline1 Estimated Start| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline1 Finish| | |✓| | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline1 Fixed Cost| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline1 Fixed Cost Accrual| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline1 Start| | |✓| | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline1 Work| | |✓| | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline2 Cost| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline2 Duration| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline2 Duration Units| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline2 Estimated Duration| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline2 Estimated Finish| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline2 Estimated Start| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline2 Finish| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline2 Fixed Cost| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline2 Fixed Cost Accrual| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline2 Start| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline2 Work| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline3 Cost| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline3 Duration| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline3 Duration Units| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline3 Estimated Duration| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline3 Estimated Finish| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline3 Estimated Start| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline3 Finish| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline3 Fixed Cost| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline3 Fixed Cost Accrual| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline3 Start| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline3 Work| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline4 Cost| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline4 Duration| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline4 Duration Units| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline4 Estimated Duration| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline4 Estimated Finish| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline4 Estimated Start| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline4 Finish| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline4 Fixed Cost| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline4 Fixed Cost Accrual| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline4 Start| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline4 Work| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline5 Cost| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline5 Duration| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline5 Duration Units| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline5 Estimated Duration| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline5 Estimated Finish| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline5 Estimated Start| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline5 Finish| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline5 Fixed Cost| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline5 Fixed Cost Accrual| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline5 Start| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline5 Work| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline6 Cost| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline6 Duration| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline6 Duration Units| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline6 Estimated Duration| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline6 Estimated Finish| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline6 Estimated Start| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline6 Finish| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline6 Fixed Cost| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline6 Fixed Cost Accrual| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline6 Start| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline6 Work| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline7 Cost| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline7 Duration| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline7 Duration Units| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline7 Estimated Duration| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline7 Estimated Finish| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline7 Estimated Start| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline7 Finish| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline7 Fixed Cost| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline7 Fixed Cost Accrual| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline7 Start| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline7 Work| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline8 Cost| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline8 Duration| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline8 Duration Units| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline8 Estimated Duration| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline8 Estimated Finish| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline8 Estimated Start| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline8 Finish| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline8 Fixed Cost| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline8 Fixed Cost Accrual| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline8 Start| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline8 Work| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline9 Cost| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline9 Duration| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline9 Duration Units| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline9 Estimated Duration| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline9 Estimated Finish| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline9 Estimated Start| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline9 Finish| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline9 Fixed Cost| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline9 Fixed Cost Accrual| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline9 Start| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline9 Work| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline10 Cost| | |✓| | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline10 Deliverable Finish| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline10 Duration| | |✓| | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline10 Duration Units| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline10 Estimated Duration| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline10 Estimated Finish| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline10 Estimated Start| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline10 Finish| | |✓| | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline10 Fixed Cost| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline10 Fixed Cost Accrual| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline10 Start| | |✓| | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline10 Work| | |✓| | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline Budget Cost| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline Budget Work| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline Cost| |✓| | | | |✓|✓|✓|✓| | | |✓|✓|✓| | | | | | | 
Baseline Deliverable Finish| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline Deliverable Start| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline Duration|✓|✓|✓| | | |✓|✓|✓|✓| |✓| |✓|✓|✓| | | | | | | 
Baseline Duration Units| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline Estimated Duration| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline Estimated Finish| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline Estimated Start| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline Finish|✓|✓|✓| | | |✓|✓|✓|✓| | | |✓|✓|✓| | | | | | | 
Baseline Fixed Cost| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline Fixed Cost Accrual| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline Start|✓|✓|✓| | | |✓|✓|✓|✓| | | |✓|✓|✓| | | | | | | 
Baseline Work| | | | | | |✓|✓|✓|✓| | | |✓|✓|✓| | | | | | | 

### Extended Fields
Field|Asta (PP)|ConceptDraw PROJECT (CDP)|FastTrack (FTS)|GanttDesigner (GNT)|GanttProject (GAN)|Merlin (SQLITE)|Microsoft (MPD)|Microsoft (MPP)|Microsoft (MPX)|Microsoft (MSPDI)|P3 (BTRIEVE)|Phoenix (PPX)|Planner (XML)|Primavera (PMXML)|Primavera (SQLITE)|Primavera (XER)|Project Commander (PC)|ProjectLibre (POD)|SDEF (SDEF)|Sage (SCHEDULE_GRID)|SureTrak (STW)|Synchro (SP)|TurboProject (PEP)
---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---
Cost1| | |✓| | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Cost2| | |✓| | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Cost3| | | | | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Cost4| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Cost5| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Cost6| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Cost7| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Cost8| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Cost9| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Cost10| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Date1| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Date2| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Date3| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Date4| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Date5| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Date6| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Date7| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Date8| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Date9| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Date10| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Duration1| | | | | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Duration1 Units| | | | | | | |✓| | | | | | | | | | | | | | | 
Duration2| | | | | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Duration2 Units| | | | | | | |✓| | | | | | | | | | | | | | | 
Duration3| | | | | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Duration3 Units| | | | | | | |✓| | | | | | | | | | | | | | | 
Duration4| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Duration4 Units| | | | | | | |✓| | | | | | | | | | | | | | | 
Duration5| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Duration5 Units| | | | | | | |✓| | | | | | | | | | | | | | | 
Duration6| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Duration6 Units| | | | | | | |✓| | | | | | | | | | | | | | | 
Duration7| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Duration7 Units| | | | | | | |✓| | | | | | | | | | | | | | | 
Duration8| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Duration8 Units| | | | | | | |✓| | | | | | | | | | | | | | | 
Duration9| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Duration9 Units| | | | | | | |✓| | | | | | | | | | | | | | | 
Duration10| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Duration10 Units| | | | | | | |✓| | | | | | | | | | | | | | | 
Finish1| | | | | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Finish2| | | | | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Finish3| | | | | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Finish4| | | | | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Finish5| | | | | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Finish6| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Finish7| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Finish8| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Finish9| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Finish10| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Flag1| | |✓| | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Flag2| | |✓| | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Flag3| | |✓| | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Flag4| | |✓| | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Flag5| | |✓| | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Flag6| | |✓| | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Flag7| | |✓| | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Flag8| | |✓| | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Flag9| | |✓| | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Flag10| | |✓| | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Flag11| | |✓| | | |✓|✓| |✓| | | | | | | | | | | | | 
Flag12| | |✓| | | |✓|✓| |✓| | | | | | | | | | | | | 
Flag13| | |✓| | | |✓|✓| |✓| | | | | | | | | | | | | 
Flag14| | |✓| | | |✓|✓| |✓| | | | | | | | | | | | | 
Flag15| | |✓| | | |✓|✓| |✓| | | | | | | | | | | | | 
Flag16| | |✓| | | |✓|✓| |✓| | | | | | | | | | | | | 
Flag17| | |✓| | | |✓|✓| |✓| | | | | | | | | | | | | 
Flag18| | |✓| | | |✓|✓| |✓| | | | | | | | | | | | | 
Flag19| | |✓| | | |✓|✓| |✓| | | | | | | | | | | | | 
Flag20| | |✓| | | |✓|✓| |✓| | | | | | | | | | | | | 
Number1| | |✓| | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Number2| | |✓| | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Number3| | |✓| | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Number4| | |✓| | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Number5| | |✓| | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Number6| | |✓| | | |✓|✓| |✓| | | | | | | | | | | | | 
Number7| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Number8| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Number9| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Number10| | |✓| | | |✓|✓| |✓| | | | | | | | | | | | | 
Number11| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Number12| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Number13| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Number14| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Number15| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Number16| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Number17| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Number18| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Number19| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Number20| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Outline Code1| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Outline Code1 Index| | | | | | | |✓| | | | | | | | | | | | | | | 
Outline Code2| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Outline Code2 Index| | | | | | | |✓| | | | | | | | | | | | | | | 
Outline Code3| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Outline Code3 Index| | | | | | | |✓| | | | | | | | | | | | | | | 
Outline Code4| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Outline Code4 Index| | | | | | | |✓| | | | | | | | | | | | | | | 
Outline Code5| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Outline Code5 Index| | | | | | | |✓| | | | | | | | | | | | | | | 
Outline Code6| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Outline Code6 Index| | | | | | | |✓| | | | | | | | | | | | | | | 
Outline Code7| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Outline Code7 Index| | | | | | | |✓| | | | | | | | | | | | | | | 
Outline Code8| | | | | | |✓|✓| | | | | | | | | | | | | | | 
Outline Code8 Index| | | | | | | |✓| | | | | | | | | | | | | | | 
Outline Code9| | | | | | |✓|✓| | | | | | | | | | | | | | | 
Outline Code9 Index| | | | | | | |✓| | | | | | | | | | | | | | | 
Outline Code10| | | | | | |✓|✓| | | | | | | | | | | | | | | 
Outline Code10 Index| | | | | | | |✓| | | | | | | | | | | | | | | 
Start1| | | | | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Start2| | | | | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Start3| | | | | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Start4| | | | | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Start5| | | | | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Start6| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Start7| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Start8| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Start9| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Start10| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Text1| | |✓| | | |✓|✓|✓|✓| | | |✓| |✓| | | |✓| | | 
Text2| | |✓| | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Text3| | |✓| | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Text4| | |✓| | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Text5| | |✓| | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Text6| | |✓| | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Text7| | |✓| | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Text8| | |✓| | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Text9| | |✓| | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Text10| | | | | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Text11| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Text12| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Text13| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Text14| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Text15| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Text16| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Text17| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Text18| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Text19| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Text20| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Text21| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Text22| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Text23| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Text24| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Text25| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Text26| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Text27| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Text28| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Text29| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Text30| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 

### Enterprise Fields
Field|Asta (PP)|ConceptDraw PROJECT (CDP)|FastTrack (FTS)|GanttDesigner (GNT)|GanttProject (GAN)|Merlin (SQLITE)|Microsoft (MPD)|Microsoft (MPP)|Microsoft (MPX)|Microsoft (MSPDI)|P3 (BTRIEVE)|Phoenix (PPX)|Planner (XML)|Primavera (PMXML)|Primavera (SQLITE)|Primavera (XER)|Project Commander (PC)|ProjectLibre (POD)|SDEF (SDEF)|Sage (SCHEDULE_GRID)|SureTrak (STW)|Synchro (SP)|TurboProject (PEP)
---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---
Enterprise Custom Field 2| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 3| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 4| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 5| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 6| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 9| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 10| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 20| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 23| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 26| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 27| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 34| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 38| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 39| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 40| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 41| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 44| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 45| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 52| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 53| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 54| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 55| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 56| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 57| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 58| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 59| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 60| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 61| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 62| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 65| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 83| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 93| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 114| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 116| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 137| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 142| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 145| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 160| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 174| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 196| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 202| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 206| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 235| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 237| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 247| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 248| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 256| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 260| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 261| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 262| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 264| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 265| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 268| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 269| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 274| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 275| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 276| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 278| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 279| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 280| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 281| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 283| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 284| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 285| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 286| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 289| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 290| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 291| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 293| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 294| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 297| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 298| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 299| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 300| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 303| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 304| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 305| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 306| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 307| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 308| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 309| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 310| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 311| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 315| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 317| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 321| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 322| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 325| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 328| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 329| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 340| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 352| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 353| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 354| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 355| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 356| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 358| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 359| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 360| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 361| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 362| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 363| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 372| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 373| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 374| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 377| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 384| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 387| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 388| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 401| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 402| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 403| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 404| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 405| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 406| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 407| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 414| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 415| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 416| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 418| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 420| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 422| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 429| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 430| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 431| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 433| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 437| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 438| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 439| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 440| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 441| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 467| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 486| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 492| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 493| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 511| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Data| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Duration1 Units| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Duration2 Units| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Duration3 Units| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Duration4 Units| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Duration5 Units| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Duration6 Units| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Duration7 Units| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Duration8 Units| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Duration9 Units| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Duration10 Units| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Project Date1| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Project Date2| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Project Date3| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Project Date4| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Project Number2| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Project Number4| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Project Number5| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Project Number22| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Project Text1| | | | | | | |✓| |✓| | | | | | | | | | | | | 
Enterprise Project Text2| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Project Text3| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Project Text4| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Project Text5| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Project Text6| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Project Text8| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Project Text9| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Project Text10| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Project Text11| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Project Text12| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Project Text13| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Project Text14| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Project Text15| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Project Text16| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Project Text17| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Project Text18| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Project Text19| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Project Text21| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Project Text40| | | | | | | |✓| |✓| | | | | | | | | | | | | 

## Resource
### Core Fields
Field|Asta (PP)|ConceptDraw PROJECT (CDP)|FastTrack (FTS)|GanttDesigner (GNT)|GanttProject (GAN)|Merlin (SQLITE)|Microsoft (MPD)|Microsoft (MPP)|Microsoft (MPX)|Microsoft (MSPDI)|P3 (BTRIEVE)|Phoenix (PPX)|Planner (XML)|Primavera (PMXML)|Primavera (SQLITE)|Primavera (XER)|Project Commander (PC)|ProjectLibre (POD)|SDEF (SDEF)|Sage (SCHEDULE_GRID)|SureTrak (STW)|Synchro (SP)|TurboProject (PEP)
---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---
% Work Complete| | | | | | | | | |✓| | | | | | | | | | | | | 
ACWP| | | | | | |✓| | |✓| | | | | | | | | | | | | 
Accrue At| | | | | | |✓|✓|✓|✓| | | | | | | |✓| | | | | 
Active| | | | | | | | | |✓| | | | | | | |✓| | | |✓| 
Actual Cost| | | | | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Actual Overtime Cost| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Actual Overtime Work| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Actual Work| | | | | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Actual Work Protected| | | | | | | |✓| | | | | | | | | | | | | | | 
Availability Data| | | | | | | |✓| | | | | | | | | | | | | | | 
Available From| | | | | | | |✓| |✓| | | | | | | | | | | | | 
Available To| | | | | | | |✓| |✓| | | | | | | | | | | | | 
BCWS| | | | | | | | | |✓| | | | | | | | | | | | | 
Base Calendar| | | | | | | | |✓| | | | | | | | | | | | | | 
Booking Type| | | | | | | |✓| |✓| | | | | | | | | | | | | 
Boolean Column| | | | |✓| | | | | | | | | | | | | | | | | | 
Budget| | | | | | | |✓| |✓| | | | | | | | | | | | | 
Budget Cost| | | | | | | |✓| | | | | | | | | | | | | | | 
Budget Work| | | | | | | |✓| | | | | | | | | | | | | | | 
CV| | | | | | | | | |✓| | | | | | | | | | | | | 
Calculate Costs From Units|✓|✓|✓| |✓|✓| |✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓| | |✓|✓|✓
Calendar GUID| | | | | | | |✓| | | | | | | | | | | | | | | 
Calendar Unique ID|✓|✓| | | |✓|✓|✓|✓|✓| | |✓|✓|✓|✓|✓| | | |✓| |✓
Can Level| | | | | | |✓| | |✓| | | | | | | | | | | | | 
Code| | |✓| | | |✓|✓|✓|✓|✓| | | | | | | | | |✓| | 
Cost| | | | | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Cost Center| | | | | | | |✓| |✓| | | | | | | | | | | | | 
Cost Per Use| | | | | | | |✓| | | | | | | | | | | | | | | 
Cost Rate A| | | | | | | |✓| | | | | | | | | | | | | | | 
Cost Rate B| | | | | | | |✓| | | | | | | | | | | | | | | 
Cost Rate C| | | | | | | |✓| | | | | | | | | | | | | | | 
Cost Rate D| | | | | | | |✓| | | | | | | | | | | | | | | 
Cost Rate E| | | | | | | |✓| | | | | | | | | | | | | | | 
Cost Variance| | | | | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Created| | | | | | | |✓| |✓| | | | |✓| | | | | | | | 
Date Column| | | | |✓| | | | | | | | | | | | | | | | | | 
Description| | | | | | | | | | | | | | | | | | | | | |✓| 
Double Column| | | | |✓| | | | | | | | | | | | | | | | | | 
Email Address|✓|✓|✓| |✓| |✓|✓|✓|✓| | |✓| | |✓| | | | | |✓| 
GUID| | |✓| | |✓| |✓| |✓| |✓| |✓|✓|✓| | | | | |✓| 
Generic|✓| | | | | | |✓| | | | | | | | | | | | | | | 
Group| |✓|✓| |✓| |✓|✓|✓|✓| | | | | | | | | | | | |✓
Hyperlink| | | | | | | |✓| | | | | | | | | | | | | |✓| 
Hyperlink Address| | | | | | | |✓| | | | | | | | | | | | | | | 
Hyperlink Data| | | | | | | |✓| | | | | | | | | | | | | | | 
Hyperlink Screen Tip| | | | | | | |✓| | | | | | | | | | | | | | | 
Hyperlink SubAddress| | | | | | | |✓| | | | | | | | | | | | | | | 
ID|✓|✓|✓| |✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓| | | |✓|✓|✓
Initials|✓| |✓| | | |✓|✓|✓|✓| | |✓| | | | |✓| | | | | 
Integer Column| | | | |✓| | | | | | | | | | | | | | | | | | 
Material Label|✓| | | | | |✓|✓| |✓| |✓| | | | | | | | | | | 
Max Units|✓| | | | |✓|✓|✓|✓|✓| | | |✓| | | |✓| | | | | 
Name|✓|✓|✓| |✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓| | |✓|✓|✓
Notes| |✓|✓| | | |✓|✓|✓|✓| | | |✓|✓|✓| | | | | |✓|✓
Overallocated|✓| | | | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Overtime Cost| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Overtime Rate| | | | | | | |✓| | | | | | | | | | | | | | | 
Overtime Rate Units| | | | | | | |✓| | | | | | | | | | | | | | | 
Overtime Work| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Parent ID| | | | | | | | | | | | | |✓|✓|✓| | | | | | |✓
Peak|✓| | | | | |✓|✓|✓|✓| | | | | | | |✓| | | | | 
Per Day| | | | | | | | | | | | | | | | | | | | | | |✓
Permanent Resource Field|✓| | | | | | | | | | | | | | | | | | | | | | 
Phone| | | | |✓| | | | | | | | | | | | | | | | | | 
Phonetics| | | | | | | |✓| | | | | | | | | | | | | | | 
Pool| | | | | | | | | | | | | | | | | | | | | | |✓
Rate| | | | | | | | | | | | | | | | | | | | | | |✓
Regular Work| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Remaining Cost| | | | | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Remaining Overtime Cost| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Remaining Overtime Work| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Remaining Work| | | | | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Resource ID| | | | | | | | | | | | | |✓|✓|✓| | | | | | | 
Resources Text Field| | | | | | | | | | | | | |✓| | | | | | | | | 
Role| | | | | | | | | | | | | |✓|✓|✓| | | | | | | 
SV| | | | | | | | | |✓| | | | | | | | | | | | | 
Sequence Number| | | | | | | | | | | | | |✓|✓|✓| | | | | | | 
Standard Rate| | | | | | | |✓| | | | | | | | | | | | | | | 
Standard Rate Units| | | | | | | |✓| | | | | | | | | | | | | | | 
Subproject Unique Resource ID| | | | | | | |✓| | | | | | | | | | | | | | | 
Supply Reference| | | | | | | | | | | | | | | | | | | | | |✓| 
Text Column| | | | |✓| | | | | | | | | | | | | | | | | | 
Type|✓|✓|✓| |✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓| | |✓|✓|✓
Unique ID|✓|✓|✓| |✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓| | | |✓|✓|✓
Unit| | | | | | | | | | | | | | | | | | | | | | |✓
Windows User Account| | | | | | | |✓| | | | | | | | | | | | | | | 
Work| | | | | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Work Variance| | | | | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Workgroup| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
resudf_CONT_CD| | | | | | | | | | | | | | | |✓| | | | | | | 
resudf_CRAFT_CD| | | | | | | | | | | | | | | |✓| | | | | | | 
resudf_VENDOR_NO| | | | | | | | | | | | | | | |✓| | | | | | | 

### Baseline Fields
Field|Asta (PP)|ConceptDraw PROJECT (CDP)|FastTrack (FTS)|GanttDesigner (GNT)|GanttProject (GAN)|Merlin (SQLITE)|Microsoft (MPD)|Microsoft (MPP)|Microsoft (MPX)|Microsoft (MSPDI)|P3 (BTRIEVE)|Phoenix (PPX)|Planner (XML)|Primavera (PMXML)|Primavera (SQLITE)|Primavera (XER)|Project Commander (PC)|ProjectLibre (POD)|SDEF (SDEF)|Sage (SCHEDULE_GRID)|SureTrak (STW)|Synchro (SP)|TurboProject (PEP)
---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---
Baseline1 Budget Cost| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline1 Budget Work| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline1 Cost| | | | | | | |✓| |✓| | | | | | | | | | | | | 
Baseline1 Work| | | | | | | |✓| |✓| | | | | | | | | | | | | 
Baseline2 Budget Cost| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline2 Budget Work| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline2 Cost| | | | | | | |✓| |✓| | | | | | | | | | | | | 
Baseline2 Work| | | | | | | |✓| |✓| | | | | | | | | | | | | 
Baseline3 Budget Cost| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline3 Budget Work| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline3 Cost| | | | | | | |✓| |✓| | | | | | | | | | | | | 
Baseline3 Work| | | | | | | |✓| |✓| | | | | | | | | | | | | 
Baseline4 Budget Cost| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline4 Budget Work| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline4 Cost| | | | | | | |✓| |✓| | | | | | | | | | | | | 
Baseline4 Work| | | | | | | |✓| |✓| | | | | | | | | | | | | 
Baseline5 Budget Cost| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline5 Budget Work| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline5 Cost| | | | | | | |✓| |✓| | | | | | | | | | | | | 
Baseline5 Work| | | | | | | |✓| |✓| | | | | | | | | | | | | 
Baseline6 Budget Cost| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline6 Budget Work| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline6 Cost| | | | | | | |✓| |✓| | | | | | | | | | | | | 
Baseline6 Work| | | | | | | |✓| |✓| | | | | | | | | | | | | 
Baseline7 Budget Cost| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline7 Budget Work| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline7 Cost| | | | | | | |✓| |✓| | | | | | | | | | | | | 
Baseline7 Work| | | | | | | |✓| |✓| | | | | | | | | | | | | 
Baseline8 Budget Cost| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline8 Budget Work| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline8 Cost| | | | | | | |✓| |✓| | | | | | | | | | | | | 
Baseline8 Work| | | | | | | |✓| |✓| | | | | | | | | | | | | 
Baseline9 Budget Cost| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline9 Budget Work| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline9 Cost| | | | | | | |✓| |✓| | | | | | | | | | | | | 
Baseline9 Work| | | | | | | |✓| |✓| | | | | | | | | | | | | 
Baseline10 Budget Cost| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline10 Budget Work| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline10 Cost| | | | | | | |✓| |✓| | | | | | | | | | | | | 
Baseline10 Work| | | | | | | |✓| |✓| | | | | | | | | | | | | 
Baseline Budget Cost| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline Budget Work| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline Cost| | | | | | | |✓|✓|✓| | | | | | | | | | | | | 
Baseline Work| | | | | | |✓|✓|✓|✓| | | | | | | | | | | | | 

### Extended Fields
Field|Asta (PP)|ConceptDraw PROJECT (CDP)|FastTrack (FTS)|GanttDesigner (GNT)|GanttProject (GAN)|Merlin (SQLITE)|Microsoft (MPD)|Microsoft (MPP)|Microsoft (MPX)|Microsoft (MSPDI)|P3 (BTRIEVE)|Phoenix (PPX)|Planner (XML)|Primavera (PMXML)|Primavera (SQLITE)|Primavera (XER)|Project Commander (PC)|ProjectLibre (POD)|SDEF (SDEF)|Sage (SCHEDULE_GRID)|SureTrak (STW)|Synchro (SP)|TurboProject (PEP)
---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---
Cost1| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Cost2| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Cost3| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Cost4| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Cost5| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Cost6| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Cost7| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Cost8| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Cost9| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Cost10| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Date1| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Date2| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Date3| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Date4| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Date5| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Date6| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Date7| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Date8| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Date9| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Date10| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Duration1| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Duration1 Units| | | | | | | |✓| | | | | | | | | | | | | | | 
Duration2| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Duration2 Units| | | | | | | |✓| | | | | | | | | | | | | | | 
Duration3| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Duration3 Units| | | | | | | |✓| | | | | | | | | | | | | | | 
Duration4| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Duration4 Units| | | | | | | |✓| | | | | | | | | | | | | | | 
Duration5| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Duration5 Units| | | | | | | |✓| | | | | | | | | | | | | | | 
Duration6| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Duration6 Units| | | | | | | |✓| | | | | | | | | | | | | | | 
Duration7| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Duration7 Units| | | | | | | |✓| | | | | | | | | | | | | | | 
Duration8| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Duration8 Units| | | | | | | |✓| | | | | | | | | | | | | | | 
Duration9| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Duration9 Units| | | | | | | |✓| | | | | | | | | | | | | | | 
Duration10| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Duration10 Units| | | | | | | |✓| | | | | | | | | | | | | | | 
Finish1| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Finish2| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Finish3| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Finish4| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Finish5| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Finish6| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Finish7| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Finish8| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Finish9| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Finish10| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Flag1| | |✓| | | |✓|✓| |✓| | | | | | | | | | | | | 
Flag2| | |✓| | | |✓|✓| |✓| | | | | | | | | | | | | 
Flag3| | |✓| | | |✓|✓| |✓| | | | | | | | | | | | | 
Flag4| | |✓| | | |✓|✓| |✓| | | | | | | | | | | | | 
Flag5| | |✓| | | |✓|✓| |✓| | | | | | | | | | | | | 
Flag6| | |✓| | | |✓|✓| |✓| | | | | | | | | | | | | 
Flag7| | |✓| | | |✓|✓| |✓| | | | | | | | | | | | | 
Flag8| | |✓| | | |✓|✓| |✓| | | | | | | | | | | | | 
Flag9| | |✓| | | |✓|✓| |✓| | | | | | | | | | | | | 
Flag10| | |✓| | | |✓|✓| |✓| | | | | | | | | | | | | 
Flag11| | |✓| | | |✓|✓| |✓| | | | | | | | | | | | | 
Flag12| | |✓| | | |✓|✓| |✓| | | | | | | | | | | | | 
Flag13| | |✓| | | |✓|✓| |✓| | | | | | | | | | | | | 
Flag14| | |✓| | | |✓|✓| |✓| | | | | | | | | | | | | 
Flag15| | |✓| | | |✓|✓| |✓| | | | | | | | | | | | | 
Flag16| | |✓| | | |✓|✓| |✓| | | | | | | | | | | | | 
Flag17| | |✓| | | |✓|✓| |✓| | | | | | | | | | | | | 
Flag18| | |✓| | | |✓|✓| |✓| | | | | | | | | | | | | 
Flag19| | |✓| | | |✓|✓| |✓| | | | | | | | | | | | | 
Flag20| | |✓| | | |✓|✓| |✓| | | | | | | | | | | | | 
Number1| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Number2| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Number3| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Number4| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Number5| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Number6| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Number7| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Number8| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Number9| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Number10| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Number11| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Number12| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Number13| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Number14| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Number15| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Number16| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Number17| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Number18| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Number19| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Number20| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Outline Code1| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Outline Code1 Index| | | | | | | |✓| | | | | | | | | | | | | | | 
Outline Code2| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Outline Code2 Index| | | | | | | |✓| | | | | | | | | | | | | | | 
Outline Code3| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Outline Code3 Index| | | | | | | |✓| | | | | | | | | | | | | | | 
Outline Code4| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Outline Code4 Index| | | | | | | |✓| | | | | | | | | | | | | | | 
Outline Code5| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Outline Code5 Index| | | | | | | |✓| | | | | | | | | | | | | | | 
Outline Code6| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Outline Code6 Index| | | | | | | |✓| | | | | | | | | | | | | | | 
Outline Code7| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Outline Code7 Index| | | | | | | |✓| | | | | | | | | | | | | | | 
Outline Code8| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Outline Code8 Index| | | | | | | |✓| | | | | | | | | | | | | | | 
Outline Code9| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Outline Code9 Index| | | | | | | |✓| | | | | | | | | | | | | | | 
Outline Code10| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Outline Code10 Index| | | | | | | |✓| | | | | | | | | | | | | | | 
Start1| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Start2| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Start3| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Start4| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Start5| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Start6| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Start7| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Start8| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Start9| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Start10| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Text1| | |✓| | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Text2| | |✓| | | |✓|✓| |✓| | | | | | | | | | | | | 
Text3| | |✓| | | |✓|✓| |✓| | | | | | | | | | | | | 
Text4| | |✓| | | |✓|✓| |✓| | | | | | | | | | | | | 
Text5| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Text6| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Text7| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Text8| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Text9| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Text10| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Text11| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Text12| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Text13| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Text14| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Text15| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Text16| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Text17| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Text18| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Text19| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Text20| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Text21| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Text22| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Text23| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Text24| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Text25| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Text26| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Text27| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Text28| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Text29| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Text30| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 

### Enterprise Fields
Field|Asta (PP)|ConceptDraw PROJECT (CDP)|FastTrack (FTS)|GanttDesigner (GNT)|GanttProject (GAN)|Merlin (SQLITE)|Microsoft (MPD)|Microsoft (MPP)|Microsoft (MPX)|Microsoft (MSPDI)|P3 (BTRIEVE)|Phoenix (PPX)|Planner (XML)|Primavera (PMXML)|Primavera (SQLITE)|Primavera (XER)|Project Commander (PC)|ProjectLibre (POD)|SDEF (SDEF)|Sage (SCHEDULE_GRID)|SureTrak (STW)|Synchro (SP)|TurboProject (PEP)
---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---
Enterprise| | | | | | | |✓| |✓| | | | | | | | | | | | | 
Enterprise Custom Field 3| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 4| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 5| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 6| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 7| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 8| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 9| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 10| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 11| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 12| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 13| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Data| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Duration1 Units| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Duration2 Units| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Duration3 Units| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Duration4 Units| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Duration5 Units| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Duration6 Units| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Duration7 Units| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Duration8 Units| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Duration9 Units| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Duration10 Units| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Unique ID| | | | | | | |✓| | | | | | | | | | | | | | | 

## Resource Assignment
### Core Fields
Field|Asta (PP)|ConceptDraw PROJECT (CDP)|FastTrack (FTS)|GanttDesigner (GNT)|GanttProject (GAN)|Merlin (SQLITE)|Microsoft (MPD)|Microsoft (MPP)|Microsoft (MPX)|Microsoft (MSPDI)|P3 (BTRIEVE)|Phoenix (PPX)|Planner (XML)|Primavera (PMXML)|Primavera (SQLITE)|Primavera (XER)|Project Commander (PC)|ProjectLibre (POD)|SDEF (SDEF)|Sage (SCHEDULE_GRID)|SureTrak (STW)|Synchro (SP)|TurboProject (PEP)
---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---
ACWP| | | | | | |✓| | |✓| | | | | | | | | | | | | 
Activity Resource Assignment Text Field| | | | | | | | | | | | | |✓| | | | | | | | | 
Actual Cost| | | | | | |✓|✓|✓|✓| | | |✓|✓|✓| | | | | | | 
Actual Finish| | | | | |✓|✓|✓| |✓| | | |✓|✓|✓| | | | | | | 
Actual Overtime Cost| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Actual Overtime Work| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Actual Start| | | | | |✓|✓|✓| |✓| | | |✓|✓|✓| | | | | | | 
Actual Work|✓| | | | |✓|✓|✓|✓|✓| | |✓|✓|✓|✓| | | | | | | 
Actual Work Protected| | | | | | | |✓| | | | | | | | | | | | | | | 
Assignment Delay|✓| | | | | |✓|✓|✓|✓| | | | |✓|✓| | | | | | | 
Assignment GUID| | | | | |✓| |✓| |✓| | | |✓|✓|✓| | | | | | | 
Assignment Resource GUID| | | | | | | |✓| | | | | | | | | | | | | | | 
Assignment Task GUID| | | | | | | |✓| | | | | | | | | | | | | | | 
Assignment Units|✓|✓|✓| |✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓| | |✓|✓|✓
Budget Cost| | | | | | | |✓| |✓| | | | | | | | | | | | | 
Budget Work| | | | | | | |✓| |✓| | | | | | | | | | | | | 
CV| | | | | | | | | |✓| | | | | | | | | | | | | 
Calculate Costs From Units|✓|✓|✓| |✓|✓| |✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓| | |✓|✓|✓
Confirmed| | | | | | | |✓| | | | | | | | | | | | | | | 
Cost| | | | | | |✓|✓|✓|✓| | | |✓|✓|✓| | | | | | | 
Cost Account ID| | | | | | | | | | | | | |✓|✓|✓| | | | | | | 
Cost Rate Table| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Cost Variance| | | | | | | |✓|✓|✓| | | | | | | | | | | | | 
Created| | | | | | | |✓| |✓| | | | | | | | | | | | | 
Finish|✓| | | | | |✓|✓|✓|✓| | |✓|✓|✓|✓| |✓| | | | | 
Finish Variance| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Hyperlink| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Hyperlink Address| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Hyperlink Data| | | | | | | |✓| | | | | | | | | | | | | | | 
Hyperlink Screen Tip| | | | | | | |✓| | | | | | | | | | | | | | | 
Hyperlink Subaddress| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Leveling Delay| | | | | | | |✓| | | | | | | | | | | | | | | 
Leveling Delay Units| | | | | | | |✓| | | | | | | | | | | | | | | 
Linked Fields| | | | | | | |✓| | | | | | | | | | | | | | | 
Notes| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Override Rate| | | | | | | | | | | | | |✓|✓|✓| | | | | | | 
Overtime Work| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Owner| | | | | | | |✓| | | | | | | | | | | | | | | 
Percent Work Complete|✓| | | | |✓| |✓|✓|✓| | |✓|✓|✓|✓| | | | | | | 
Planned Cost| | | | | | | | | | | | | |✓|✓|✓| | | | | | | 
Planned Finish| | | | | | | | | | | | | |✓|✓|✓| | | | | | | 
Planned Start| | | | | | | | | | | | | |✓|✓|✓| | | | | | | 
Planned Work| | | | | | | | | | | | | |✓|✓|✓| | | | | | | 
Rate Index| | | | | | | | | | | | | |✓| |✓| | | | | | | 
Rate Source|✓|✓|✓| |✓|✓| |✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓| | |✓|✓|✓
Regular Work| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Remaining Cost| | | | | | |✓|✓| |✓| | | |✓|✓|✓| | | | | | | 
Remaining Overtime Cost| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Remaining Overtime Work| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Remaining Work|✓| | | | |✓|✓|✓|✓|✓| | |✓|✓|✓|✓|✓|✓| | | | | 
Resource Request Type| | | | | | | |✓| | | | | | | | | | | | | | | 
Resource Unique ID|✓|✓|✓| |✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓| | | |✓|✓|✓
Response Pending| | | | | | |✓|✓| | | | | | | | | | | | | | | 
Resume| | | | | | | |✓| |✓| | | | | | | |✓| | | | | 
Role Unique ID| | | | | | | | | | | | | |✓|✓|✓| | | | | | | 
Start|✓| | | | | |✓|✓|✓|✓| | |✓|✓|✓|✓| |✓| | | | | 
Start Variance| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Stop| | | | | | | |✓| |✓| | | | | | | |✓| | | | | 
Task Unique ID|✓|✓|✓| |✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓| | |✓|✓|✓
Team Status Pending| | | | | | |✓|✓| | | | | | | | | | | | | | | 
Timephased Actual Overtime Work| | | | | | | |✓| | | | | | | | | | | | | | | 
Timephased Actual Work| | | | | | | |✓| | | | | | | | | | | | | | | 
Timephased Work| | | | | | | |✓| | | | | | | | | | | | | | | 
Unique ID|✓|✓|✓| |✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓| | |✓|✓|✓
Variable Rate Units| | | | | | | |✓| |✓| | | | | | | | | | | | | 
Work|✓|✓|✓| |✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓|✓| | |✓|✓| 
Work Contour| | | | | | |✓|✓| |✓| | | |✓|✓|✓| |✓| | | | | 
Work Variance| | | | | | | |✓|✓|✓| | | | | | | | | | | | | 
perm sched alloc|✓| | | | | | | | | | | | | | | | | | | | | | 

### Baseline Fields
Field|Asta (PP)|ConceptDraw PROJECT (CDP)|FastTrack (FTS)|GanttDesigner (GNT)|GanttProject (GAN)|Merlin (SQLITE)|Microsoft (MPD)|Microsoft (MPP)|Microsoft (MPX)|Microsoft (MSPDI)|P3 (BTRIEVE)|Phoenix (PPX)|Planner (XML)|Primavera (PMXML)|Primavera (SQLITE)|Primavera (XER)|Project Commander (PC)|ProjectLibre (POD)|SDEF (SDEF)|Sage (SCHEDULE_GRID)|SureTrak (STW)|Synchro (SP)|TurboProject (PEP)
---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---
Baseline1 Budget Cost| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline1 Budget Work| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline1 Cost| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline1 Finish| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline1 Start| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline1 Work| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline2 Budget Cost| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline2 Budget Work| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline2 Cost| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline2 Finish| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline2 Start| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline2 Work| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline3 Budget Cost| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline3 Budget Work| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline3 Cost| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline3 Finish| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline3 Start| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline3 Work| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline4 Budget Cost| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline4 Budget Work| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline4 Cost| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline4 Finish| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline4 Start| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline4 Work| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline5 Budget Cost| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline5 Budget Work| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline5 Cost| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline5 Finish| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline5 Start| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline5 Work| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline6 Budget Cost| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline6 Budget Work| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline6 Cost| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline6 Finish| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline6 Start| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline6 Work| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline7 Budget Cost| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline7 Budget Work| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline7 Cost| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline7 Finish| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline7 Start| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline7 Work| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline8 Budget Cost| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline8 Budget Work| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline8 Cost| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline8 Finish| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline8 Start| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline8 Work| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline9 Budget Cost| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline9 Budget Work| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline9 Cost| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline9 Finish| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline9 Start| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline9 Work| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline10 Budget Cost| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline10 Budget Work| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline10 Cost| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline10 Finish| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline10 Start| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline10 Work| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline Budget Cost| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline Budget Work| | | | | | | |✓| | | | | | | | | | | | | | | 
Baseline Cost| | | | | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Baseline Finish| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline Start| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Baseline Work| | | | | | |✓|✓|✓|✓| | | | | | | | | | | | | 
Timephased Baseline1 Cost| | | | | | | |✓| | | | | | | | | | | | | | | 
Timephased Baseline1 Work| | | | | | | |✓| | | | | | | | | | | | | | | 
Timephased Baseline2 Cost| | | | | | | |✓| | | | | | | | | | | | | | | 
Timephased Baseline2 Work| | | | | | | |✓| | | | | | | | | | | | | | | 
Timephased Baseline3 Cost| | | | | | | |✓| | | | | | | | | | | | | | | 
Timephased Baseline3 Work| | | | | | | |✓| | | | | | | | | | | | | | | 
Timephased Baseline4 Cost| | | | | | | |✓| | | | | | | | | | | | | | | 
Timephased Baseline4 Work| | | | | | | |✓| | | | | | | | | | | | | | | 
Timephased Baseline5 Cost| | | | | | | |✓| | | | | | | | | | | | | | | 
Timephased Baseline5 Work| | | | | | | |✓| | | | | | | | | | | | | | | 
Timephased Baseline6 Cost| | | | | | | |✓| | | | | | | | | | | | | | | 
Timephased Baseline6 Work| | | | | | | |✓| | | | | | | | | | | | | | | 
Timephased Baseline7 Cost| | | | | | | |✓| | | | | | | | | | | | | | | 
Timephased Baseline7 Work| | | | | | | |✓| | | | | | | | | | | | | | | 
Timephased Baseline8 Cost| | | | | | | |✓| | | | | | | | | | | | | | | 
Timephased Baseline8 Work| | | | | | | |✓| | | | | | | | | | | | | | | 
Timephased Baseline9 Cost| | | | | | | |✓| | | | | | | | | | | | | | | 
Timephased Baseline9 Work| | | | | | | |✓| | | | | | | | | | | | | | | 
Timephased Baseline10 Cost| | | | | | | |✓| | | | | | | | | | | | | | | 
Timephased Baseline10 Work| | | | | | | |✓| | | | | | | | | | | | | | | 
Timephased Baseline Cost| | | | | | | |✓| | | | | | | | | | | | | | | 
Timephased Baseline Work| | | | | | | |✓| | | | | | | | | | | | | | | 

### Extended Fields
Field|Asta (PP)|ConceptDraw PROJECT (CDP)|FastTrack (FTS)|GanttDesigner (GNT)|GanttProject (GAN)|Merlin (SQLITE)|Microsoft (MPD)|Microsoft (MPP)|Microsoft (MPX)|Microsoft (MSPDI)|P3 (BTRIEVE)|Phoenix (PPX)|Planner (XML)|Primavera (PMXML)|Primavera (SQLITE)|Primavera (XER)|Project Commander (PC)|ProjectLibre (POD)|SDEF (SDEF)|Sage (SCHEDULE_GRID)|SureTrak (STW)|Synchro (SP)|TurboProject (PEP)
---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---
Cost1| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Cost2| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Cost3| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Cost4| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Cost5| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Cost6| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Cost7| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Cost8| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Cost9| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Cost10| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Date1| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Date2| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Date3| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Date4| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Date5| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Date6| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Date7| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Date8| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Date9| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Date10| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Duration1| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Duration1 Units| | | | | | | |✓| | | | | | | | | | | | | | | 
Duration2| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Duration2 Units| | | | | | | |✓| | | | | | | | | | | | | | | 
Duration3| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Duration3 Units| | | | | | | |✓| | | | | | | | | | | | | | | 
Duration4| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Duration4 Units| | | | | | | |✓| | | | | | | | | | | | | | | 
Duration5| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Duration5 Units| | | | | | | |✓| | | | | | | | | | | | | | | 
Duration6| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Duration6 Units| | | | | | | |✓| | | | | | | | | | | | | | | 
Duration7| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Duration7 Units| | | | | | | |✓| | | | | | | | | | | | | | | 
Duration8| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Duration8 Units| | | | | | | |✓| | | | | | | | | | | | | | | 
Duration9| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Duration9 Units| | | | | | | |✓| | | | | | | | | | | | | | | 
Duration10| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Duration10 Units| | | | | | | |✓| | | | | | | | | | | | | | | 
Finish1| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Finish2| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Finish3| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Finish4| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Finish5| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Finish6| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Finish7| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Finish8| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Finish9| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Finish10| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Flag1| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Flag2| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Flag3| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Flag4| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Flag5| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Flag6| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Flag7| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Flag8| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Flag9| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Flag10| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Flag11| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Flag12| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Flag13| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Flag14| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Flag15| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Flag16| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Flag17| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Flag18| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Flag19| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Flag20| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Number1| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Number2| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Number3| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Number4| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Number5| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Number6| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Number7| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Number8| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Number9| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Number10| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Number11| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Number12| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Number13| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Number14| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Number15| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Number16| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Number17| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Number18| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Number19| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Number20| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Start1| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Start2| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Start3| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Start4| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Start5| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Start6| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Start7| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Start8| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Start9| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Start10| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Text1| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Text2| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Text3| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Text4| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Text5| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Text6| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Text7| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Text8| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Text9| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Text10| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Text11| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Text12| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Text13| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Text14| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Text15| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Text16| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Text17| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Text18| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Text19| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Text20| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Text21| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Text22| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Text23| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Text24| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Text25| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Text26| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Text27| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Text28| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Text29| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 
Text30| | | | | | |✓|✓| |✓| | | | | | | | | | | | | 

### Enterprise Fields
Field|Asta (PP)|ConceptDraw PROJECT (CDP)|FastTrack (FTS)|GanttDesigner (GNT)|GanttProject (GAN)|Merlin (SQLITE)|Microsoft (MPD)|Microsoft (MPP)|Microsoft (MPX)|Microsoft (MSPDI)|P3 (BTRIEVE)|Phoenix (PPX)|Planner (XML)|Primavera (PMXML)|Primavera (SQLITE)|Primavera (XER)|Project Commander (PC)|ProjectLibre (POD)|SDEF (SDEF)|Sage (SCHEDULE_GRID)|SureTrak (STW)|Synchro (SP)|TurboProject (PEP)
---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---
Enterprise Custom Field 16| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 203| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 223| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 225| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 227| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 229| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 231| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 398| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 400| | | | | | | |✓| | | | | | | | | | | | | | | 
Enterprise Custom Field 410| | | | | | | |✓| | | | | | | | | | | | | | | 

