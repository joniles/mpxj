# Supported Data Sources

## File Formats

* **MPX:** The MPX file format can be read by versions of Microsoft
Project up to and including Microsoft Project 2010, and written by versions of
Microsoft Project up to Microsoft Project 98. Applications other than Microsoft
Project also commonly write MPX files as a way of sharing project data. MPXJ
can read and write MPX files.
See [this Microsoft support article](https://support.microsoft.com/en-gb/help/270139)
for a definition of the file format.

* **MPP:** Microsoft Project by default stores projects as MPP files.
MPXJ supports read only access to MPP files produced by Microsoft
Project from Microsoft Project 98 onwards (Microsoft Project 98, Microsoft
Project 2000, Microsoft  Project 2002, Microsoft Project 2003, Microsoft
Project 2007, Microsoft Project 2010, Microsoft Project 2013, Microsoft Project
2016, and Microsoft Project 2019). MPP template files, with the suffix MPT can
also be read by MPXJ.

* **MSPDI:** The MSPDI file format is Microsoft's XML file format for
sharing project data. Versions of Microsoft Project from 2002 onwards can read
and write MSPDI files.  Applications other than Microsoft Project also
commonly write MSPDI files as a way of sharing project data. 
MPXJ can read and write MSPDI files.
The MSDPI file format has remained
broadly unchanged since it was introduced, although several versions of
Microsoft Project have tweaked the file format slightly, and have their own
updated documentation.
Documentation is [available online here](https://docs.microsoft.com/en-us/office-project/xml-data-interchange/project-xml-data-interchange-schema-reference).
Documentation for the Project 2003 MSPDI file format can be downloaded as
part of the [Office 2003 XML Reference Schemas](https://www.microsoft.com/en-us/download/details.aspx?id=101) package.
Documentation for the Project 2007 MSPDI file format can be downloaded as part
of the [Project 2007 SDK](https://www.microsoft.com/en-us/download/details.aspx?id=2432). Documentation
for the Project 2010 MSPDI file format can be downloaded as part of the
[Project 2010 Reference: Software Development Kit](https://www.microsoft.com/en-us/download/details.aspx?id=15511).
Documentation for the Project 2013 MSPDI file format can be downloaded as part
of the [Project 2013 SDK](https://www.microsoft.com/en-us/download/details.aspx?id=30435).

* **MPD:** The Microsoft Project MPD file format is a Microsoft Access database
used to store one or more projects. Versions of
Microsoft Project from Microsoft Project 98 to Microsoft Project 2003 can write
MPD files. Later versions of Microsoft Project can read MPD files but can't
write them. MPXJ can read MPD files natively, without using a JDBC driver, or
via a JDBC connection. MPXJ supports reading MPD files written by versions of
Microsoft Project 2000 onwards.

* **PLANNER:** [Gnome Planner](https://wiki.gnome.org/Apps/Planner)
is a cross-platform Open Source project management tool which uses an XML file
format to store project data. MPXJ can read and write Planner files.

* **PRIMAVERA P6:** [Primavera P6](https://www.oracle.com/uk/construction-engineering/primavera-p6/)
is an industry-leading tool favoured by users with complex planning
requirements. It can export project data in the form of XER or PMXML files,
both of which MPXJ can read and write. It is also possible for MPXJ to connect
directly to a P6 database via JDBC to read project data, or if a standalone
SQLite P6 database is being used, MPXJ can read projects from this natively
without using JDBC. The PMXML schema forms part of the P6 distribution media,
which can be downloaded from the
[Oracle Software Delivery Cloud](https://edelivery.oracle.com/).

* **PRIMAVERA P3:** Primavera P3 (Primavera Project Planner) is the forerunner
of P6. It stores each project as a directory containing Btrieve database files
which MPXJ can read from the directory itself or from a zip archive of the
directory. MPXJ can also read P3 data from PRX backup files.

* **PRIMAVERA SURETRAK:** Primavera SureTrak is an early iteration of the
application which eventually became Primavera P6. SureTrak stores each project
as a directory containing Btrieve database files which MPXJ can read from the
directory itself or from a zip archive of the directory. MPXJ can also read
SureTrak data from STX backup files.

* **POWERPROJECT:** [Asta Powerproject](https://elecosoft.com/products/asta/asta-powerproject/)
is a planning tool used in a number of industries, particularly construction.
Powerproject saves data to PP files. MPXJ can read PP files produced by
Powerproject version 8 onwards (although earlier versions may also be
supported). Powerproject can also write one or more projects to MDB
(Microsoft Access) database files which MPXJ can read natively without a JDBC
driver, or via a JDBC connection.

* **PHOENIX:** [Phoenix Project Manager](https://www.phoenixcpm.com/)
is an easy-to-use critical path method scheduling tool aimed primarily at the
construction industry. Phoenix stores projects as XML files with the file
extension PPX. MPXJ can read PPX files written by Phoenix from version 4
onwards.

* **FASTTRACK:** [Fasttrack Schedule](https://www.aecsoftware.com/)
is general purpose planning tool. FastTrack stores projects as FTX files. MPXJ
can read FTX files written by Fasttrack version 10 onwards, although FTX files
written by earlier versions may be supported.

* **GANTTPROJECT:** [GanttProject](https://github.com/bardsoftware/ganttproject/releases)
is an open source general purpose planning tool. GanttProject stores projects
as GAN files, which can be read by MPXJ.

* **TURBOPROJECT:** [TurboProject](https://www.turbocad.com/turboproject/turboproject.html)
is general purpose planning tool. TurboProject store projects as PEP files,
which can be read by MPXJ.

* **CONECPTDRAW PROJECT:** [ConceptDraw PROJECT](https://www.conceptdraw.com/products/project-management-software)
is general purpose planning tool. ConceptDraw PROJECT writes CDPX, CDPZ and
CDPTZ files which MPXJ can read.

* **SYNCHRO SCHEDULER:** Synchro Scheduler is general purpose planning tool
from [Bentley Systems](https://www.bentley.com/).
Synchro Scheduler stores projects as SP files. MPXJ can read SP files written
by Synchro Scheduler version 6 and onwards, although SP files written by earlier
versions may be supported.

* **GANTT DESIGNER:** Gantt Designer is a simple Gantt chart drawing tool. Gantt
Designer stores projects as GNT files, which can be read using MPXJ.

* **SDEF:** SDEF is the [Standard Data Exchange Format](https://www.publications.usace.army.mil/Portals/76/Publications/EngineerRegulations/ER_1-1-11.pdf),
as defined by the United States Army Corps of Engineers (USACE). SDEF is a fixed
column format text file, used to export a project schedule to the QCS
(Quality Control System) software from USACE. MPXJ can read and write SDEF
files.

* **SCHEDULE_GRID:** [Sage 100 Contractor](https://www.sage.com/en-us/products/sage-100-contractor/)
is an application for small to medium sized companies in the construction
industry, providing accounting, and estimating and project management
functionality. Project plans managed in Sage 100 Contractor can be exported as
schedule grid files, which can be read by MPXJ.

* **PROJECT COMMANDER:** [Project Commander](http://projectcommander.co.uk/pmschome/homepage.html)
is a general purpose project planning application. Project Commander stores
projects as PC files. PC files written by Project Commander version 7 and
onwards can be read by MPXJ, although PC files written by earlier versions may
be supported.

* **DELTEK OPEN PLAN:** [Deltek Open Plan](https://www.deltek.com/en/project-and-portfolio-management/open-plan)
Deltek Open Plan is an enterprise project management application offering
resource management, critical path analysis, and customizable reporting.
Projects managed in Open Plan can be exported to BK3 files, which can be read
by MPXJ.

* **EDRAW PROJECT:** [Edraw Project](https://www.edrawsoft.com/edraw-project/)
is an easy to use tool which allows users to rapidly create and
maintain professional looking project plans. MPXJ reads the EDPX format
written by Edraw Project.

## Servers

* **MICROSOFT PROJECT SERVER** [Microsoft Project Server](https://www.microsoft.com/en-gb/microsoft-365/project/enterprise-project-server)
is a centralized project management platform that enables organizations to plan,
manage, and track projects, resources, and portfolios.

* **PRIMAVERA P6 WEB SERVICES** [Primavera P6 Web Services](https://www.oracle.com/construction-engineering/primavera-p6/)
is a component typically made available as part of a Primavera P6 Enterprise
Project Portfolio Management (EPPM) installation. EPPM is a centralized
planning solution based on Primavera P6.

* **ORACLE PRIMAVERA CLOUD** [Oracle Primavera Cloud](https://www.oracle.com/construction-engineering/primavera-cloud-project-management/)
(often abbreviated as OPC) is Oracle's next-generation centralized planning
solution representing an evolution of the Primavera P6 and P6 EPPM products.

