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

The approach taken is to use the object model created to
support MPX files as the main representation of any type
of project information. On top of this a layer has been
added that allows an MPP file to be read, and the
information it contains to be mapped into the MPX data
structures. Any application that is written to use the
main MPX data structures can now use the data contained
in MPX or MPP files.


Getting started

The library is fairly simple to use. For the MPP
functionality, it depends on the POI library produced by
the Apache Jakarta project. A copy of this library can be
found in the lib directory of the distribution, and needs
to be added to your classpath in order to use the MPP
functionality. A small number of sample files have been
included in the examples directory of the distribution to
illustrate the use of the library.


Keep in touch!

We're keen to hear from you about how you are using MPXJ,
please drop us a note at mpxj@tapsterrock.com. If you use
MPXJ as a part of an application that you redistribute,
commercial or otherwise, drop us a line and we'll include
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
