## How To: Read XER files
The XER file format has long been read and written by Primavera P6. Although an XML file format (PMXML)
is now also supported, the XER file format is still widely used.

### Reading XER files
The simplest way to read an XER file is to use the `UniversalProjectReader`:

```java
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.reader.UniversalProjectReader;

...

UniversalProjectReader reader = new UniversalProjectReader();
ProjectFile project = reader.read("my-sample.xer");
```

### Using PrimaveraXERFileReader
You can work directly with the `PrimaveraXERFileReader` class by replacing `UniversalProjectReader`
with `PrimaveraXERFileReader`. This provides access to additional options, as described below.

#### Encoding
By default MPXJ assumes that XER files are encoded using Windows-1252. The `UniversalProjectReader`
understands Unicode Byte Order Marks (BOM) and will adjust the encoding appropriately if a
BOM is present. If you have an XER file with an unusual encoding, you can manually
set the encoding used by the reader.

Two methods are provided to do this: `setCharset` and `setEncoding`. The `setCharset` method
takes an instance of the `Charset` class, while the `setEncoding` method takes the name of
an encoding. Examples of these methods are shown below:


```java
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.primavera.PrimaveraXERFileReader;

...

PrimaveraXERFileReader reader = new PrimaveraXERFileReader();

// Use a Charset instance
reader.setCharset(Charset.forName("GB2312"));
ProjectFile project = reader.read("my-sample.xer");

// Use an encoding name
reader.setEncoding("GB2312");
project = reader.read("my-sample.xer");
```

#### Multiple Projects
An XER file can contain multiple projects. By default MPXJ reads the first project
it finds in the file. You can however use MPXJ to list the projects contained in
an XER file, as shown below:

```java
import net.sf.mpxj.primavera.PrimaveraXERFileReader;

...

PrimaveraXERFileReader reader = new PrimaveraXERFileReader();
FileInputStream is = new FileInputStream("my-sample.xer");
Map<Integer, String> projects = reader.listProjects(is);
System.out.println("ID\tName");
for (Entry<Integer, String> entry : projects.entrySet())
{
   System.out.println(entry.getKey()+"\t"+entry.getValue());
}
```
The call to `listProjects` returns a `Map` whose key is the project ID,
and the values are project short names.

Once you have decided which of these projects you want to work with, you can
call `setProjectID` to tell the reader which project to open, as shown below.

```java
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.primavera.PrimaveraXERFileReader;

...

PrimaveraXERFileReader reader = new PrimaveraXERFileReader();
reader.setProjectID(123);
ProjectFile file = reader.read("my-sample.xer");
```

Alternatively you can ask MPXJ to read all of the projects contained in the file:

```java
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.primavera.PrimaveraXERFileReader;

...

PrimaveraXERFileReader reader = new PrimaveraXERFileReader();
InputStream is = new FileInputStream("my-sample.xer");
List<ProjectFile> files = reader.readAll(is);
```

The call to the `readAll` method returns a list of `ProjectFile` instances corresponding
to the projects in the XER file.

There is a variant of the `readAll` method taking a Boolean argument which determines
if cross-project predecessors and successors are linked together. For example, if we
have Project A with Task 1, and Project B with Task 2 and Task 1 is a predecessor of Task 2,
when the `linkCrossProjectRelations` argument is set to true, Task 1 will appear as a predecessor
of Task 2 despite the two tasks appearing in different projects. If `linkCrossProjectRelations` is set
to false (or any of the other methods are used to read data from an XER file),
Task 1 and Task 2 will no be linked.

The sample below shows how `readAll` can be called with the `linkCrossProjectRelations`
argument set to true:

```java
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.primavera.PrimaveraXERFileReader;

...

PrimaveraXERFileReader reader = new PrimaveraXERFileReader();
InputStream is = new FileInputStream("my-sample.xer");
List<ProjectFile> files = reader.readAll(is, true);
```

#### Activity WBS
In the original implementation of the XER file handling code, MPXJ would assign each task representing
a Primavera Activity its own distinct WBS value. This does not match Primavera's behaviour where
all of a WBS element's child activities will have the same WBS value as the parent WBS element.
MPXJ's default behaviour now matches Primavera, but should you wish to you can revert to the original
behaviour by calling the `setMatchPrimaveraWBS` as shown below.

```java
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.primavera.PrimaveraXERFileReader;

...

PrimaveraXERFileReader reader = new PrimaveraXERFileReader();
reader.setMatchPrimaveraWBS(false);
ProjectFile file = reader.read("my-sample.xer");
```
