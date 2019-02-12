## Introduction
Welcome to MPXJ! This library provides a set of facilities to
allow project information to be manipulated from a number of different programming languages. MPXJ supports
a range of data formats: Microsoft Project Exchange (MPX), Microsoft
Project (MPP, MPT), Microsoft Project Data Interchange (MSPDI XML), Microsoft 
Project Database (MPD), Planner (XML), Primavera P6 (PM XML, XER, and database), 
Primavera P3 (PRX and database), SureTrak (STX and database), 
Asta Powerproject and Easyplan (PP, MDB), Phoenix Project Manager (PPX),
FastTrack Schedule (FTS), GanttProject (GAN), TurboProject (PEP),
ConceptDraw PROJECT (CDPX, CPDZ and CPDTZ), Synchro Scheduler (SP)
and the Standard Data Exchange Format (SDEF).

The library is based around a set of data structures which
follow the way schedule data is represented by Microsoft Project. All
manipulation of project data takes place using these data
structures, which can be read from or written to the various
supported file formats. The notes in the following paragraphs explain the
facilities MPXJ offers for each file format.

* **MPX:** The MPX file format can be read by versions of Microsoft
Project up to and including Microsoft Project 2010, and written by versions up to Microsoft Project 98.
MPXJ allows MPX files to be created, read and written. See [this Microsoft support article](https://support.microsoft.com/en-gb/help/270139) for a definition of the file format.

* **MPP:** The MPP file format is Microsoft's proprietary way of storing
project data. MPXJ supports read only access to MPP files produced
by Microsoft Project 98, Microsoft Project 2000, Microsoft 
Project 2002, Microsoft Project 2003, Microsoft Project 2007, Microsoft Project 2010,
Microsoft Project 2013, Microsoft Project 2016, and Microsoft Project 2019.
MPP template files, with the suffix MPT are also supported.

* **MSPDI:** The MSPDI file format is Microsoft's XML file format for
storing project data. Versions of Microsoft Project from 2002 onwards can read and write MSPDI files. 
MPXJ allows MSPDI files to be created, read, and written. The MSDPI file format has remained
broadly unchanged since it was introduced, although several versions of Microsoft Project have tweaked
the file format slightly, and have their own updated documentation.
Documentation for the Project 2003 MSPDI file format can be downloaded as part of the
[Office 2003 XML Reference Schemas]
(https://www.microsoft.com/en-us/download/details.aspx?id=101) package.
Documentation for the Project 2007 MSPDI file format can be downloaded as part of the
[Project 2007 SDK]
(https://www.microsoft.com/en-us/download/details.aspx?id=2432).
Documentation for the Project 2010 MSPDI file format can be downloaded as part of the
[Project 2010 Reference: Software Development Kit]
(https://www.microsoft.com/en-us/download/details.aspx?id=15511).
Documentation for the Project 2013 MSPDI file format can be downloaded as part of the
[Project 2013 SDK]
(https://www.microsoft.com/en-us/download/details.aspx?id=30435).

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
form of XER or PMXML files, both of which MPXJ can read. It is also possible for MPXJ to connect
directly to the P6 database via JDBC to read project data. MPXJ can also write PMXML files 
to allow data to be exported in a form which can be consumed by P6. The PMXML schema forms
part of the P6 distribution media, which can be downloaded from the Oracle e-Delivery site.

* **PRIMAVERA P3:** Primavera P3 (Primavera Project Planner) is the forerunner of P6.
It holds projects in Btrieve database files which MPXJ can read from a directory or from a zip archive.
MPXJ can also read P3 data from PRX backup files.

* **PRIMAVERA SURETRAK:** SureTrak holds projects in Btrieve database files which MPXJ
can read from a directory or from a zip archive. MPXJ can also read SureTrak data from STX backup files.

* **POWERPROJECT:** Asta Powerproject is a planning tool used in a number of industries, 
particularly construction. Powerproject can save data to PP files or to MDB database files, 
and MPXJ can read both of these formats.

* **PHOENIX:** Phoenix Project Manager is an easy-to-use critical path method scheduling
tool aimed primarily at the construction industry. Phoenix writes PPX files which MPXJ can read. 

* **FASTTRACK:** Fasttrack Schedule is general purpose planning tool. FastTrack writes FTS files
which MPXJ can read.

* **GANTTPROJECT:** GanttProject is an open source general purpose planning tool. GanttProject writes GAN files
which MPXJ can read.

* **TURBOPROJECT:** TurboProject is general purpose planning tool. TurboProject writes PEP files
which MPXJ can read.

* **CONECPTDRAW PROJECT:** ConceptDraw PROJECT is general purpose planning tool. ConceptDraw PROJECT
writes CDPX, CPDZ and CPDTZ files which MPXJ can read.

* **SYNCHRO SCHEDULER:** Synchro Scheduler is general purpose planning tool. Synchro Scheduler writes SP files
which MPXJ can read.

* **GANTT DESIGNER:** Gantt Designer is a simple Gantt chart drawing tool. Gantt Designer writes GNT files
which MPXJ can read.

* **SDEF:** SDEF is the Standard Data Exchange Format, as defined by the USACE (United States
Army Corps of Engineers). SDEF is a fixed column format text file, used to import
a project schedule up into the QCS (Quality Control System) software from USACE.
MPXJ can write SDEF files.

## Java, .Net, Ruby, PHP...
MPXJ is written and maintained in Java, however this is no barrier to
using its functionality in other languages.

Thanks to the facilities 
provided by IKVM, the MPXJ distribution also contains a native .Net DLL version
of MPXJ and its library dependencies. This allows MPXJ to be used 
from any .Net programming language (for example, C#, Visual Basic 
and so on), without normally having to be aware that the original code was written 
in Java. As part of the MPXJ release process the Java and .Net libraries
are both exercised using the same set of regression tests to ensure that their
behaviour is identical. Both the Java and .Net versions of the library
are used in production commercial applications - so you can confident
that the code will work for you!

There is also now a [Ruby Gem](https://rubygems.org/gems/mpxj)
which provides native Ruby access to read from schedule files using MPXJ.

You may be able to leverage MPXJ from other languages too, for example the 
[PHP/Java Bridge](http://php-java-bridge.sourceforge.net)
can be used to expose the complete MPXJ API in PHP.

## Getting started with MPXJ in Java

For many people, the easiest way to get started with MPXJ and its dependencies
is to use Maven. Just include the following in your POM to register MPXJ as a 
dependency of your project:

```xml
<dependency>
  <groupId>net.sf.mpxj</groupId>
  <artifactId>mpxj</artifactId>
  <version>7.7.1</version>
</dependency>
```

The traditional method of downloading the MPXJ distribution as a zip file 
can also be used. Distributions can be found at 
[SourceForge](http://sourceforge.net/project/showfiles.php?group_id=70649)
and also at [GitHub](https://www.github.com/joniles/mpxj/releases).

The zip files contain all of the source, the MPXJ JAR file in the root of the zip
file, with the libraries on which MPXJ depends being found in the `lib` directory
of the zip file. These libraries will need to be available on your classpath
in order to use all of the MPXJ functionality.

You'll find a general introduction to MPXJ's functionality [here](howto-start.html).

## Getting started with MPXJ and .Net

For many people the easiest way to work with MPXJ is to download the packages via
[NuGet](http://www.nuget.org/packages?q=mpxj). The .Net assemblies and their
dependencies can also be found in the zip file distribution from
[SourceForge](http://sourceforge.net/project/showfiles.php?group_id=70649)
or [GitHub](https://www.github.com/joniles/mpxj/releases).
You'll find a general introduction to MPXJ's functionality [here](howto-start.html)
and specific details about working with .Net [here](howto-dotnet.html).

## Getting started with MPXJ and Ruby
MPXJ is available as a RubyGem, which can be installed using
```
gem install mpxj
```
or included in you Gemfile and installed using `bundler`. Note that the
Ruby version of MPXJ is just a wrapper around the Java library, and provides
read-only access to schedule data. You can find some documentation for the
Gem [here](https://rubygems.org/gems/mpxj)

## Keep in touch
I'm keen to hear from you about how you are using MPXJ,
please [drop me a note](mailto:jon.iles@bcs.org.uk).

If you use MPXJ as a part of an application that you redistribute,
commercially or otherwise, drop me a line and I'll include
a link to your website on the [MPXJ users page](users.html).

## You can help me
Please let me know what features you'd like to see added
to MPXJ. In addition to feature requests, if you have schedule
files that can't be read, or don't contain the data you
expect, or have a file format you'd like MPXJ to read,
please let me know. The more of this feedback you can give me, the
better I can make MPXJ!

## Licensing
MPXJ is distributed under the terms of the
[GNU LGPL](http://www.gnu.org/licenses/licenses.html#LGPL)
a copy of which can be found in the root of the
distribution. Please read this license carefully! It will cost you nothing
to use MPXJ commercially or non-commercially, but you must comply
with the terms of the license.

Please see the legal folder within the distribution for details of the
licences for the third party libraries used by MPXJ.

## Acknowledgements

This product includes functionality provided by [POI](http://poi.apache.org/).

This product includes functionality provided by [IKVM.NET](http://www.ikvm.net/).

This product includes functionality provided by [RTF Parser Kit](https://github.com/joniles/rtfparserkit). 

This product includes functionality provided by [SQLITE-JDBC](https://github.com/xerial/sqlite-jdbc).

This product includes functionality provided by [ZLIB/CONTRIB](https://github.com/madler/zlib)

This product includes functionality provided by [JWAT](http://jwat.org/).
