# Getting Started with Java

MPXJ is built to work with versions of Java from 1.8 onwards. For many people,
the easiest way to get started with MPXJ and its dependencies is to use Maven.
Just include the following in your POM to register MPXJ as a  dependency of your
project:

```xml
<dependency>
  <groupId>net.sf.mpxj</groupId>
  <artifactId>mpxj</artifactId>
  <version>14.0.0</version>
</dependency>
```

The traditional method of downloading the MPXJ distribution as a zip file 
can also be used. Distributions can be found at
[GitHub](https://www.github.com/joniles/mpxj/releases) and
[SourceForge](http://sourceforge.net/project/showfiles.php?group_id=70649).

The zip files contain all of the source, the MPXJ JAR file in the root of the
zip file, with the libraries on which MPXJ depends being found in the `lib`
directory of the zip file. These libraries will need to be available on your
classpath in order to use all of the MPXJ functionality. The `script` directory
in the zip file contains a batch file and a shell script which show how this
can be done.

You'll find a general introduction to MPXJ's functionality [here](howto-start.md),
and sample Java code using MPXJ [here](https://github.com/joniles/mpxj-java-samples).