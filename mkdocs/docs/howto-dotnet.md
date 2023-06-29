# Getting Started with .Net
For many people the easiest way to work with MPXJ is via
[NuGet](http://www.nuget.org/packages?q=mpxj). The .Net assemblies and their
dependencies can also be found in the zip file distribution from
[GitHub](https://www.github.com/joniles/mpxj/releases) or
[SourceForge](http://sourceforge.net/project/showfiles.php?group_id=70649).

You'll find a general introduction to MPXJ's functionality [here](howto-start.md).

## MPXJ assemblies
MPXJ ships with a set of .Net Framework and .Net Core assemblies, which are
managed for you by NuGet or can be found in the `src.net\lib\net45` and
`src.net\lib\netcoreapp3.1` folders of the distribution respectively.

There are actually three different .Net DLLs shipped with MPXJ - you only need
one of these:

* **mpxj.dll** - this is the default .Net version, the API is
  identical to the Java version
* **mpxj-for-csharp.dll** - in this version the API has been modified to make
  it less like Java and more like C#: there are properties rather than getter
  and setter methods and the method names follow the same uppercase initial
  letter convention used by C#.
* **mpxj-for-vb.dll** - this version also transforms getters and setters into
  properties, but the method names are unchanged. VB is case insensitive and
  can't cope with the seeing two methods whose name differs only by case

As noted above, in the "for C#" and "for VB" versions of the MPXJ DLL, getters
and setters have been replaced by properties. For example, where you would have
previously written code like this:

```C#
String text = task.getText();
task.setText(text);
```

Now when you work with the "for C#" and "for VB" versions of the MPXJ DLL,
you'll be able to write code in a more familiar style:

```C#
String text = task.Text
task.Text = text;
```

Also noted above, in the case of the "for C#" MPXJ DLL, method names have been
modified to begin with an initial capital, so the code will again have a more
familiar style. For example, using the original Java method names you'd write
something like this:

```C#
Task task = projectFile.addTask();
```

Using the "for C#" DLL your code will look like this:

```C#
Task task = projectFile.AddTask();
```

## MPXJ dependencies
Once you have selected the version of the MPXJ DLL most suitable for your
project, you will need to add its dependencies. If you are using NuGet to
manage your dependencies, this is done for you automatically. If you are
managing the dependencies manually, the files you need will all be in the
relevant sub folder with the `src.net\lib` folder of the MPXJ distribution.

## .Net samples
MPXJ ships with some sample files which can be found in the `src.net\samples`
folder of the distribution. These files illustrate how the MPXJ API can be 
used to manipulate project data. In particular the `MpxjQuery` example
shows how various elements which make up the project data can be queried.
Two versions of this utility are present in `src.net\samples`, one written in C#,
and the other written in Visual Basic (VB) to illustrate the basics of using
MPXJ in either language. Even if you are developing software in a .Net
language you may still find it useful to refer to the Java examples, and
indeed the original Java source of MPXJ, to give you an insight into how the
API can be used.

## .Net and Java types
The .Net version of MPXJ has been generated directly from the Java version using
a tool called IKVM. One of the side effects of using IKVM to perform this
conversion is that the MPXJ exposes .Net versions of the original Java data
types, so for example you will find that the API returns a type called
`LocalDateTime` rather than a .Net `DateTime`, and collections which don't
expose the familiar `IEnumerable` interface.

To simplify the translation between Java and .Net types, a set of extension
methods have been provided. These are included n the NuGet package, and the
source can be found in the `src.net\utilities` folder, in a project called
`MpxjUtilities`. This project contains extension methods which enhance both
Java and .Net classes to make it easier to pass data to and from the API. For
example the extension method `ToIEnumerable` is added to Java collection data
types which allows them to be iterated using the familiar `foreach` .Net
syntax.

To use these extension methods, simply add a reference to the `MpxjUtilities`
assembly in your own project. The methods themselves are documented in the
source, and examples of their use can be seen in the samples provided in the
`src.net\samples` folder.

## MPXJ and the GAC

For your convenience two batch files are provided in the `src.net\lib\net45`
directory: `mpxj-gac-install.bat` and `mpxj-gac-uninstall.bat`. These batch
files install the MPXJ assemblies into the GAC and uninstall the MPXJ
assemblies from the GAC using the
[`gacutil`](http://msdn.microsoft.com/en-us/library/ex0ss12c(v=vs.110)) global
assembly cache tool. Note that these batch files assume that `gacutil` is
available on the path.
