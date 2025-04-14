# How To: Read Primavera PLF files
Primavera P6 can export layout information as PLF files. These files define the
visual appearance of the P6 user interface, and can be imported and exported by
P6. Although MPXJ doesn't currently offer any facilities to interpret the
contents of these files, the data they contain can be read.

## Reading PLF files
A PLF file contains "structured text" and can be read using
`StructuredTextParser`:

```java
package org.mpxj.howto.read;

import org.mpxj.primavera.StructuredTextParser;
import org.mpxj.primavera.StructuredTextRecord;

import java.io.FileInputStream;

public class PLF
{
   public void read() throws Exception
   {
      StructuredTextParser parser = new StructuredTextParser();
      StructuredTextRecord record = parser.parse(new FileInputStream("test.plf"));
   }
}
```

### Attributes
The resulting `StructuredTextRecord` contains attributes which can be accesed
individually by name, as shown below:

```java
record.getAttribute("attribute_name");
```

The attributes can also be retrieved in the form of a `Map` containing
all attributes for this record:

```java
Map<String,String> attributes = record.getAttributes();
attributes.get("attribute_name");
```

Each record has two special attributes: a record number, and optionally a record name.
These appear as part of the identifying information for each record, not as part of
the general set of attributes for the record. These can be retrieved as shown below:

```java
String recordNumber = record.getRecordNumber();
String recordName = record.getRecordName();
```

These attributes will also be found in the attributes `Map` with the keys
`_record_number` and `_record_name`.

### Child records
Along with a set of attributes, each `StructuredTextRecord` may have child
`StructuredTextRecord` instances. These be retrieved as a list, as shown below:

```java
List<StructuredTextRecord> childRecords = record.getChildren();
```

Certain record types are named, and where this is the case a child record can
be retrieved individually by name:

```java
StructuredTextRecord child = record.getChild("child_name");
```
