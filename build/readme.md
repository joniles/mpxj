# Building MPXJ

Building all of the artifacts for an MPXJ release has become quite a complex
operation. I had come to realise that the Windows machine on which releases
were being created had accumulated lots of different installed packages,
libraries and configuration settings, to the point it wasn't clear what the
bare minimum to create a release actually was.

To address this, I've created the batch file you'll find in this directory. It
is intended to be run as an administrator on a clean Windows 10 install.
Assuming all goes well, the script will download and install all of the
prerequisites for building an MPXJ release. When the script finishes you will
be prompted to add entries to the path, to make `ant`, `mvn` and `nuget`
available from the command line.

At this point the script has downloaded a snapshot of the current MPXJ source
to allow you to test the build. From a command prompt you can issue the
following commands:

```
cd c:\java\mpxj
ant clean
ant distribute
```

The script also installs various other tools I find useful for developing MPXJ
(Sublime Text, Eclipse, Intellij IDEA). These are not strictly necessary for the
build but are useful to have available.
