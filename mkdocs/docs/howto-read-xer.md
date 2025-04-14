# How To: Read XER files
The XER file format has long been read and written by Primavera P6. Although an
XML file format (PMXML) is now also supported, the XER file format is still
widely used.

## Reading XER files
The simplest way to read an XER file is to use the `UniversalProjectReader`:

=== "Java"
	```java
	package org.mpxj.howto.read;
	
	import org.mpxj.ProjectFile;
	import org.mpxj.reader.UniversalProjectReader;
	
	public class XER
	{
		public void read() throws Exception
		{
			UniversalProjectReader reader = new UniversalProjectReader();
			ProjectFile project = reader.read("my-sample.xer");
		}
	}
	```

=== "C#"
	```c#
	using MPXJ.Net;
	
	namespace MPXJ.Samples.HowToRead;
	
	public class XER
	{
	 	public void Read()
	 	{
		  	var reader = new UniversalProjectReader();
		  	var project = reader.Read("my-sample.xer");
	 	}
	}
	```

## Using PrimaveraXERFileReader
You can work directly with the `PrimaveraXERFileReader` class by replacing
`UniversalProjectReader` with `PrimaveraXERFileReader`. This provides access to
additional options, as described below.

### Ignore Errors
By default P6 ignores records it can't successfully read from an XER file. MPXJ
takes the same approach, and in most cases if it doesn't receive the data it
expects for a particular record it will ignore the problematic item.

This behavior is controlled using the `setIgnoreErrors` method. The example
below illustrates how we can force the `PrimaveraXERFileReader` to report
errors encountered when reading a file:

```java
package org.mpxj.howto.read;

import org.mpxj.ProjectFile;
import org.mpxj.primavera.PrimaveraXERFileReader;

public class XERIgnoreErrors
{
   public void read() throws Exception
   {
      PrimaveraXERFileReader reader = new PrimaveraXERFileReader();
      reader.setIgnoreErrors(false);
      ProjectFile project = reader.read("my-sample.xer");
   }
}
```

Note that if errors are ignored when reading a file, the ignored errors
are available by using the `ProjectFile.getIgnoredErrors()` method.

### Charset
By default MPXJ assumes that XER files use the Windows-1252 Charset. The
`UniversalProjectReader` understands Unicode Byte Order Marks (BOM) and will
adjust the Charset appropriately if a BOM is present. If you have an XER file
with an unusual encoding, you can manually set the Charset used by the reader.

```java
package org.mpxj.howto.read;

import org.mpxj.ProjectFile;
import org.mpxj.primavera.PrimaveraXERFileReader;

import java.nio.charset.Charset;

public class XERCharset
{
   public void read() throws Exception
   {
      PrimaveraXERFileReader reader = new PrimaveraXERFileReader();
      reader.setCharset(Charset.forName("GB2312"));
      ProjectFile project = reader.read("my-sample.xer");
   }
}
```

### Multiple Projects
An XER file can contain multiple projects. By default MPXJ reads the first
project it finds in the file which has been marked as the "exported" project,
otherwise it will simply read the first project it finds. You can however use
MPXJ to list the projects contained in an XER file, as shown below:

```java
package org.mpxj.howto.read;

import org.mpxj.primavera.PrimaveraXERFileReader;

import java.io.FileInputStream;
import java.util.Map;

public class XERListProjects
{
   public void read() throws Exception
   {
      PrimaveraXERFileReader reader = new PrimaveraXERFileReader();
      FileInputStream is = new FileInputStream("my-sample.xer");
      Map<Integer, String> projects = reader.listProjects(is);
      System.out.println("ID\tName");
      for (Map.Entry<Integer, String> entry : projects.entrySet())
      {
         System.out.println(entry.getKey() + "\t" + entry.getValue());
      }
   }
}
```
The call to `listProjects` returns a `Map` whose key is the project ID,
and the values are project short names.

Once you have decided which of these projects you want to work with, you can
call `setProjectID` to tell the reader which project to open, as shown below.

```java
package org.mpxj.howto.read;

import org.mpxj.ProjectFile;
import org.mpxj.primavera.PrimaveraXERFileReader;

public class XERProjectID
{
   public void read() throws Exception
   {
      PrimaveraXERFileReader reader = new PrimaveraXERFileReader();
      reader.setProjectID(123);
      ProjectFile file = reader.read("my-sample.xer");
   }
}
```

Alternatively you can ask MPXJ to read all of the projects contained in the file:

```java
package org.mpxj.howto.read;

import org.mpxj.ProjectFile;
import org.mpxj.primavera.PrimaveraXERFileReader;

import java.util.List;

public class XERReadAll
{
   public void read() throws Exception
   {
      PrimaveraXERFileReader reader = new PrimaveraXERFileReader();
      List<ProjectFile> files = reader.readAll("my-sample.xer");
   }
}
```

The call to the `readAll` method returns a list of `ProjectFile` instances corresponding
to the projects in the XER file.

### Link Cross-Project Relations
An XER file can contain multiple projects with relations between activities
which span those projects. By default these cross-project relations are ignored.
However, if you set the `linkCrossProjectRelations` reader attribute to `true`,
MPXJ will attempt to link these relations across projects:

```java
package org.mpxj.howto.read;

import org.mpxj.ProjectFile;
import org.mpxj.primavera.PrimaveraXERFileReader;

import java.util.List;

public class XERLinkCrossProject
{
   public void read() throws Exception
   {
      PrimaveraXERFileReader reader = new PrimaveraXERFileReader();
      reader.setLinkCrossProjectRelations(true);
      List<ProjectFile> files = reader.readAll("my-sample.xer");
   }
}
```

### Activity WBS
In the original implementation of the XER file handling code, MPXJ would assign
each task representing a Primavera Activity its own distinct WBS value. This
does not match Primavera's behaviour where all of a WBS element's child
activities will have the same WBS value as the parent WBS element. MPXJ's
default behaviour now matches Primavera, but should you wish to you can revert
to the original behaviour by calling the `setMatchPrimaveraWBS` as shown below.

```java
package org.mpxj.howto.read;

import org.mpxj.ProjectFile;
import org.mpxj.primavera.PrimaveraXERFileReader;

public class XERMatchWbs
{
   public void read() throws Exception
   {
      PrimaveraXERFileReader reader = new PrimaveraXERFileReader();
      reader.setMatchPrimaveraWBS(false);
      ProjectFile file = reader.read("my-sample.xer");
   }
}
```

### WBS is Full Path
Currently the WBS attribute of summary tasks (WBS entities in P6) will be a dot
separated hierarchy of all of the parent WBS attributes. In this example,
`root.wbs1.wbs2` is the WBS attribute for `wbs2` which has the parents `root`
and `wbs1`. To disable this behaviour, and simply record the code for the
current WBS entry (in the example above `wbs2`) call the `setWbsIsFullPath`
method, passing in `false`, as illustrated below.

```java
package org.mpxj.howto.read;

import org.mpxj.ProjectFile;
import org.mpxj.primavera.PrimaveraXERFileReader;

public class XERWbsFullPath
{
   public void read() throws Exception
   {
      PrimaveraXERFileReader reader = new PrimaveraXERFileReader();
      reader.setWbsIsFullPath(false);
      ProjectFile file = reader.read("my-sample.xer");
   }
}
```

### Reading Additional Attributes
A data-driven approach is used to extract the attributes used by MPXJ from the
XER file. You can if you wish change the way attributes are read from the file,
or add support for additional attributes. This assumes that you know the column
name of the attributes you want to work with in the XER file. To make changes
you will need to retrieve the maps which define which MPXJ attributes are used
to store which columns from the XER file:

```java
package org.mpxj.howto.read;

import org.mpxj.FieldType;
import org.mpxj.primavera.PrimaveraXERFileReader;

import java.util.Map;

public class XERAttributeMaps
{
   public void read() throws Exception
   {
      PrimaveraXERFileReader reader = new PrimaveraXERFileReader();
      Map<FieldType, String> resourceFieldMap = reader.getResourceFieldMap();
      Map<FieldType, String> wbsFieldMap = reader.getWbsFieldMap();
      Map<FieldType, String> activityFieldMap = reader.getActivityFieldMap();
      Map<FieldType, String> assignmentFieldMap = reader.getAssignmentFieldMap();
   }
}
```

These maps will contain the default mapping between columns and MPXJ attributes.
You can modify these existing mappings, or add new ones, for example:

```java
package org.mpxj.howto.read;

import org.mpxj.FieldType;
import org.mpxj.TaskField;
import org.mpxj.primavera.PrimaveraXERFileReader;

import java.util.Map;

public class XERAttributeConfig
{
   public void read() throws Exception
   {
      PrimaveraXERFileReader reader = new PrimaveraXERFileReader();
      Map<FieldType, String> activityFieldMap = reader.getActivityFieldMap();

      //
      // Store rsrc_id in NUMBER1
      //
      activityFieldMap.put(TaskField.NUMBER1, "rsrc_id");

      //
      // Read an Activity column called an_example_field and store it in TEXT10
      //
      activityFieldMap.put(TaskField.TEXT10, "an_example_field");
   }
}
```

When reading new columns from the XER file, if these columns have a type other
than String, it is important to register the type of the column to ensure that
it is converted correctly. You will also need to ensure that the MPXJ attribute
you are writing this new value to can receive the data type you are assigning to
it (for example, you must store a date in a date attribute, you can't store a
date in an integer attribute).
 
For example, if we are reading an integer column called `an_example_id` and
store it in the `NUMBER2` attribute, we will need to take the following steps:

```java
package org.mpxj.howto.read;

import org.mpxj.DataType;
import org.mpxj.FieldType;
import org.mpxj.TaskField;
import org.mpxj.primavera.PrimaveraXERFileReader;

import java.util.Map;

public class XERRegisterType
{
   public void read() throws Exception
   {
      PrimaveraXERFileReader reader = new PrimaveraXERFileReader();
      Map<String, DataType> fieldTypeMap = reader.getFieldTypeMap();
      fieldTypeMap.put("an_example_id", DataType.INTEGER);
      Map<FieldType, String> activityFieldMap = reader.getActivityFieldMap();
      activityFieldMap.put(TaskField.NUMBER2, "an_example_id");
   }
}
```
