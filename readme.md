# Introduction
Welcome to MPXJ! This library enables you to read project plans (sometimes known
as schedules or programmes) from a variety of file formats and databases, and
can also write that information to a variety of file formats.

The library is based on data structures which follow the way
schedule data is represented by Microsoft Project, extended to accommodate
features and concepts from other applications.

## Supported Formats
MPXJ can read file formats including MPX, MPP, MSPDI, MPD, Planner, Primavera
P6, Primavera P3, SureTrak, Asta Powerproject, Asta Easyplan, Phoenix,
Fasttrack, GanttProject, TurboProject, ConceptDraw PROJECT, Synchro, Gantt
Designer, SDEF, Sage 100 Contractor Schedule Grid and Project Commander.

MPXJ can also write schedule data as MPX, MSPDI, PMXML, Planner and SDEF files.

More details of the supported file formats can be found
[here](https://www.mpxj.org/supported-formats/).


## Supported Languages
MPXJ is written and maintained in Java, however this is no barrier to using
its functionality in other languages.

Thanks to [IKVM](https://github.com/ikvm-revived/ikvm), MPXJ is
[available for .Net](https://www.nuget.org/packages?q=net.sf.mpxj), allowing
it to be used  from any .Net programming language.

There is also now a [Ruby Gem](https://rubygems.org/gems/mpxj) which provides
native Ruby access to read from schedule files using MPXJ, and a
[Python package](https://pypi.org/project/mpxj/) which wraps the Java library to
provide full read/write access to schedule files.

You may be able to leverage MPXJ from other languages too, for example the 
[PHP/Java Bridge](http://php-java-bridge.sourceforge.net)
can be used to expose the complete MPXJ API in PHP.

## Contact

I'm keen to hear from you about how you are using MPXJ, please
[drop me a note](mailto:jon.iles@bcs.org.uk).

If you use MPXJ as a part of an application that you redistribute, commercially
or otherwise, let me know and I'll include a link to your website on the
[MPXJ users page](https://www.mpxj.org/users/).

Please let me know what features you'd like to see added to MPXJ. In addition to
feature requests, if you have schedule files that can't be read, or don't
contain the data you expect, or have a file format you'd like MPXJ to read,
please let me know. The more of this feedback you can give me, the better I can
make MPXJ!

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
This library includes functionality provided by:

* [POI](http://poi.apache.org/).
* [IKVM.NET](http://www.ikvm.net/).
* [RTF Parser Kit](https://github.com/joniles/rtfparserkit). 
* [SQLITE-JDBC](https://github.com/xerial/sqlite-jdbc).
* [ZLIB/CONTRIB](https://github.com/madler/zlib).
* [JWAT](http://jwat.org/).
* [JSOUP](http://jsoup.org/).

This library has been built with the assistance of:

* [JProfiler](https://www.ej-technologies.com/products/jprofiler/overview.html) Java Profiler
