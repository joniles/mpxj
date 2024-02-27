# Supported Formats

* **MPX:** The MPX file format can be read by versions of Microsoft
Project up to and including Microsoft Project 2010, and written by versions up
to Microsoft Project 98. MPXJ allows MPX files to be created, read and
written. See [this Microsoft support
article](https://support.microsoft.com/en-gb/help/270139) for a definition of
the file format.

* **MPP:** The MPP file format is Microsoft's proprietary way of storing
project data. MPXJ supports read only access to MPP files produced by
Microsoft Project 98, Microsoft Project 2000, Microsoft  Project 2002,
Microsoft Project 2003, Microsoft Project 2007, Microsoft Project 2010,
Microsoft Project 2013, Microsoft Project 2016, and Microsoft Project 2019.
MPP template files, with the suffix MPT are also supported.

* **MSPDI:** The MSPDI file format is Microsoft's XML file format for
storing project data. Versions of Microsoft Project from 2002 onwards can read
and write MSPDI files.  MPXJ allows MSPDI files to be created, read, and
written. The MSDPI file format has remained broadly unchanged since it was
introduced, although several versions of Microsoft Project have tweaked the
file format slightly, and have their own updated documentation. Documentation is
[available online here](https://docs.microsoft.com/en-us/office-project/xml-data-interchange/project-xml-data-interchange-schema-reference).
Documentation for the Project 2003 MSPDI file format can be downloaded as
part of the [Office 2003 XML Reference Schemas](https://www.microsoft.com/en-us/download/details.aspx?id=101) package.
Documentation for the Project 2007 MSPDI file format can be downloaded as part
of the [Project 2007 SDK](https://www.microsoft.com/en-us/download/details.aspx?id=2432). Documentation
for the Project 2010 MSPDI file format can be downloaded as part of the
[Project 2010 Reference: Software Development Kit](https://www.microsoft.com/en-us/download/details.aspx?id=15511).
Documentation for the Project 2013 MSPDI file format can be downloaded as part
of the [Project 2013 SDK](https://www.microsoft.com/en-us/download/details.aspx?id=30435).

* **MPD:** The MPD file format is an Access database used to
store one or more projects. The database schema used in these databases is 
also close to that used by Microsoft Project Server. MPXJ can read projects
stored in an MPD file using a JDBC connection. It is possible that MPXJ could 
also read the same data from a Microsoft Project Server database using the
same approach, but this is not something I've tested.

* **PLANNER:** Planner is an Open Source project management tool which uses
an XML file format to store project data. MPXJ can read and write the Planner
file format.

* **PRIMAVERA P6:** Primavera P6 is an industry-leading tool favoured
by users with complex planning requirements. It can export project data in the
form of XER or PMXML files, both of which MPXJ can read. It is also possible
for MPXJ to connect directly to the P6 database via JDBC to read project data.
MPXJ can also write PMXML and XER files  to allow data to be exported in a form
which can be consumed by P6. The PMXML schema forms part of the P6 distribution
media, which can be downloaded from the Oracle e-Delivery site.

* **PRIMAVERA P3:** Primavera P3 (Primavera Project Planner) is the forerunner
of P6. It holds projects in Btrieve database files which MPXJ can read from a
directory or from a zip archive. MPXJ can also read P3 data from PRX backup
files.

* **PRIMAVERA SURETRAK:** SureTrak holds projects in Btrieve database files which
MPXJ can read from a directory or from a zip archive. MPXJ can also read
SureTrak data from STX backup files.

* **POWERPROJECT:** Asta Powerproject is a planning tool used in a number of
industries,  particularly construction. Powerproject can save data to PP files
or to MDB database files,  and MPXJ can read both of these formats.

* **PHOENIX:** Phoenix Project Manager is an easy-to-use critical path method
scheduling tool aimed primarily at the construction industry. Phoenix writes
PPX files which MPXJ can read. 

* **FASTTRACK:** Fasttrack Schedule is general purpose planning tool. FastTrack
writes FTS files which MPXJ can read.

* **GANTTPROJECT:** GanttProject is an open source general purpose planning tool.
GanttProject writes GAN files which MPXJ can read.

* **TURBOPROJECT:** TurboProject is general purpose planning tool. TurboProject
writes PEP files which MPXJ can read.

* **CONECPTDRAW PROJECT:** ConceptDraw PROJECT is general purpose planning tool.
ConceptDraw PROJECT writes CDPX, CDPZ and CDPTZ files which MPXJ can read.

* **SYNCHRO SCHEDULER:** Synchro Scheduler is general purpose planning tool.
Synchro Scheduler writes SP files which MPXJ can read.

* **GANTT DESIGNER:** Gantt Designer is a simple Gantt chart drawing tool. Gantt
Designer writes GNT files which MPXJ can read.

* **SDEF:** SDEF is the Standard Data Exchange Format, as defined by the USACE
(United States Army Corps of Engineers). SDEF is a fixed column format text
file, used to import a project schedule up into the QCS (Quality Control
System) software from USACE. MPXJ can read and write SDEF files. The
specification for the file format can be found
[here](https://www.publications.usace.army.mil/Portals/76/Publications/EngineerRegulations/ER_1-1-11.pdf).

* **SCHEDULE_GRID:** Schedule grid files are produced when a schedule is exported
from Sage 100 Contractor. MPXJ can read schedule grid files.

* **PROJECT COMMANDER:** Project Commander files are the native file format used
by the Project Commander application. Project Commander writes PC files which
MPXJ can read.

* **DELTEK OPEN PLAN:** Open Plan allows export of schedules to BK3 files,
which MPXJ can read.
