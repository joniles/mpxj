# Introduction
Welcome to MPXJ! This library enables you to read project plans (sometimes known
as schedules or programmes) from a variety of file formats and databases, and
can also write that information to a variety of file formats.

The library is loosely based around on data structures which follow the way
schedule data is represented by Microsoft Project, extended to accommodate
features and concepts from other applications.

## Supported Formats
MPXJ can read file formats including MPX, MPP, MSPDI, MPD, Planner, Primavera
P6, Primavera P3, SureTrak, Asta Powerproject, Asta Easyplan Phoenix,
Fasttrack, GanttProject, TrurboProject, ConceptDraw PROJECT, Synchro, Gantt
Designer, SDEF, Sage 100 Contractor Schedule Grid and Project Commander.

MPXJ can also write schedule data as MPX, MSPDI, PMXML, Planner and SDEF files.

More details of the supported file formats can be found
[here](supported-formats.md).


## Supported Languages
MPXJ is written and maintained in Java, however this is no barrier to using
its functionality in other languages.

Thanks to [IKVM](https://github.com/ikvm-revived/ikvm), the MPXJ distribution
also contains a native .Net DLL version of MPXJ and its library dependencies.
This allows MPXJ to be used  from any .Net programming language (for example,
C#, Visual Basic  and so on), without in most cases needing to be aware that the
original code was written  in Java. As part of the MPXJ release process the
Java and .Net libraries are both exercised using the same set of regression
tests to ensure that their behaviour is identical. Both the Java and .Net
versions of the library are used in production commercial applications - so you
can be confident that the code will work for you!

There is also now a [Ruby Gem](https://rubygems.org/gems/mpxj) which provides
native Ruby access to read from schedule files using MPXJ, and a [Python
package](https://pypi.org/project/mpxj/) which wraps the Java library to
provide full read/write access to schedule files.

You may be able to leverage MPXJ from other languages too, for example the 
[PHP/Java Bridge](http://php-java-bridge.sourceforge.net)
can be used to expose the complete MPXJ API in PHP.


### Working with Java

MPXJ is built to work with versions of Java from 1.8 onwards. For many people,
the easiest way to get started with MPXJ and its dependencies is to use Maven.
Just include the following in your POM to register MPXJ as a  dependency of your
project:

```xml
<dependency>
  <groupId>net.sf.mpxj</groupId>
  <artifactId>mpxj</artifactId>
  <version>10.4.0</version>
</dependency>
```

The traditional method of downloading the MPXJ distribution as a zip file 
can also be used. Distributions can be found at 
[SourceForge](http://sourceforge.net/project/showfiles.php?group_id=70649)
and also at [GitHub](https://www.github.com/joniles/mpxj/releases).

The zip files contain all of the source, the MPXJ JAR file in the root of the
zip file, with the libraries on which MPXJ depends being found in the `lib`
directory of the zip file. These libraries will need to be available on your
classpath in order to use all of the MPXJ functionality.

You'll find a general introduction to MPXJ's functionality [here](howto-start.md).

### Working with .Net

For many people the easiest way to work with MPXJ is to download the packages via
[NuGet](http://www.nuget.org/packages?q=mpxj). The .Net assemblies and their
dependencies can also be found in the zip file distribution from
[SourceForge](http://sourceforge.net/project/showfiles.php?group_id=70649)
or [GitHub](https://www.github.com/joniles/mpxj/releases).
You'll find a general introduction to MPXJ's functionality [here](howto-start.md)
and specific details about working with .Net [here](howto-dotnet.md).

### Working with Ruby
MPXJ is available as a RubyGem, which can be installed using
```
gem install mpxj
```
or included in you Gemfile and installed using `bundler`. Note that the
Ruby version of MPXJ is just a wrapper around the Java library, and provides
read-only access to schedule data. You can find some documentation for the
Gem [here](https://rubygems.org/gems/mpxj)

### Working with Python
MPXJ is available as a Python Package, which can be installed using
```
pip install mpxj
```
You can find some documentation for the
Package [here](https://pypi.org/project/mpxj/)

## Keep in touch
I'm keen to hear from you about how you are using MPXJ,
please [drop me a note](mailto:jon.iles@bcs.org.uk).

If you use MPXJ as a part of an application that you redistribute,
commercially or otherwise, drop me a line and I'll include
a link to your website on the [MPXJ users page](users.md).

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
This product includes functionality provided by:

* [POI](http://poi.apache.org/).
* [IKVM.NET](http://www.ikvm.net/).
* [RTF Parser Kit](https://github.com/joniles/rtfparserkit). 
* [SQLITE-JDBC](https://github.com/xerial/sqlite-jdbc).
* [ZLIB/CONTRIB](https://github.com/madler/zlib).
* [JWAT](http://jwat.org/).
* [JSOUP](http://jsoup.org/).

This product has been built with the assistance of:

* [JProfiler](https://www.ej-technologies.com/products/jprofiler/overview.html) Java Profiler
