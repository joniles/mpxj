MPXJ: Microsoft Project Exchange in Java



Introduction

Welcome to MPXJ! This library provides a set of
facilities to allow project information to be manipulated
in Java. The starting point for these facilities is the
MPX format, as originally defined by Microsoft. In lieu
of a better alternative this has become the de facto
standard for exchanging project-related information
between applications. MPXJ provides a comprehensive
implementation of the MPX standard, allowing MPX files to
be created, read, written, and the data they contain to
be manipulated.

In its infinite wisdom Microsoft has decide to drop
support for MPX file creation from Microsoft Project 2000
onwards. Although Microsoft Project 2000 and 2002 can
both read MPX files, neither of them can generate MPX
files without the use of third-party add-ons. To get
around this, and allow Java applications to offer more
seamless integration with data produced by Microsoft
Project, MPXJ has been enhanced to include the ability to
read the native MPP files produced by Microsoft Project
2000 and Microsoft Project 2002.

Finally, with the release of Project 2002, Microsoft have provided
support for importing and exporting project information using XML.
The name given to this format by Microsoft is the Microsoft
Project Data Interchange format (MSPDI). MPXJ can now read XML
files in the MSPDI format.

The approach taken by MPXJ to support all of these file formats is
to use the object model created to support MPX files as the main
representation of any type of project information. On top of this,
layers have been added that allow MPP or MSPDI files to be read,
and the information contained in these formats to be mapped into
the MPX data structures. Any application that is written to use
the main MPX data structures can now use the data contained in
MPX, MPP, and MSPDI files.



Getting started

The library is fairly simple to use, and to illustrate this, a
small number of sample files have been included in the examples
directory of the distribution.

If you are only reading and writing MPX files, you will not need
to add any files to your classpath apart from the MPXJ JAR itself.

The MPP functionality depends on the POI library produced by the
Apache Jakarta project. In order to use the MPP functionality the
POI JAR file must be present on your classpath. A copy of this
library can be found in the lib directory of the distribution.

The MSPDI functionality depends on the Sun JAXB 1.0 Beta reference
implementation. This is not included in the distribution, and must
be downloaded from http://java.sun.com/xml/downloads/jaxb.html.



JUnit

Should you wish to run the JUnit tests provided with MPXJ, you
need to be aware that the JUnit classloader doesn’t get on well
with JAXB, so it is recommended that you execute the JUnit test
runner with the –noloading command line option, or otherwise
disable class reloading.



Build

If you want to build MPXJ yourself using the supplied source and
ant build.xml file, there are a few points to note:

The XML schema file from which the JAXB code has been generated is
supplied by Microsoft as part of the Project 2002 distribution.
The file name is mspdi.xsd. The copyright for this file belongs to
Microsoft, and unfortunately the copyright text makes no mention
of redistribution. We therefore have to assume that at present we
are unable to redistribute this file. If you are able to locate a
copy of this file, you can rebuild the JAXB source using the xjc
target found in the ant build.xml file. Note that the xjc target
is platform specific, you will need to change the reference to the
xjc tool to be xjc.bat or xjc.sh depending on your operating
system.

You will need to have a copy of the JAXB libraries installed
locally in order to build MPXJ. You will need to edit the property
“jaxbdir” in the build.xml file to point to your local
installation.



Keep in touch!

We're keen to hear from you about how you are using MPXJ,
please drop us a note at mpxj@tapsterrock.com. If you use
MPXJ as a part of an application that you redistribute,
commercially or otherwise, drop us a line and we'll include
a link to your website on the MPXJ page



You can help!

Please let us know what features you'd like to see added
to MPXJ. In addition, the MPP functionality is not
complete as we have had to reverse engineer the data from
the MPP file format, so we need to know if you have MPP
files that can't be read, or don't contain the data you
expect. The more of this feedback you can give us, the
better we can make MPXJ!



Licensing

MPXJ is distributed under the terms of the GNU LGPL, a
copy of which can be found in the legal directory of the
distribution.

As mentioned above, the MPP functionality depends on the
use of the POI library. The license for this library can
be found in the legal directory of the distribution. To
comply with the Apache license, we must state that "this
product includes software developed by the Apache
Software Foundation (http://www.apache.org/)."

A copy of the JUnit library is provided for you
convenience to allow you to run the regression tests
provided with MPXJ. The license for JUnit can be found in
the legal directory of the distribution.

