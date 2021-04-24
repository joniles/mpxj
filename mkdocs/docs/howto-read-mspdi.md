## How To: Read MSPDI files
The Microsoft Project Data Interchange (MSPDI) format is an XML file format
which Microsoft Project has been able to read and write since Project 2002.

### Reading MSPDI files
The simplest way to read an MSPDI file is to use the `UniversalProjectReader`:

```java
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.reader.UniversalProjectReader;

...

UniversalProjectReader reader = new UniversalProjectReader();
ProjectFile project = reader.read("my-sample.xml");
```

### Using MSPDIReader
You can work directly with the `MSPDIReader` class by replacing `UniversalProjectReader`
with `MSPDIReader`. This provides access to additional options, as described below.

#### Encoding
By default MPXJ assumes that MSPDI files are encoded as UTF-8. The `UniversalProjectReader`
understands Unicode Byte Order Marks (BOM) and will adjust the encoding appropriately if a
BOM is present. If you have an MSPDI file with an unusual encoding, you can manually
set the encoding used by the reader.

Two methods are provided to do this: `setCharset` and `setEncoding`. The `setCharset` method
takes an instance of the `Charset` class, while the `setEncoding` method takes the name of
an encoding. Examples of these methods are shown below:


```java
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.mspdi.MSPDIReader;

...

MSPDIReader reader = new MSPDIReader();

// Use a Charset instance
reader.setCharset(Charset.forName("GB2312"));
ProjectFile project = reader.read("my-sample.xml");

// Use an encoding name
reader.setEncoding("GB2312");
project = reader.read("my-sample.xml");
```

#### Microsoft Project Compatibility
Microsoft Project will read MSPDI files which are not valid XML according to the MSPDI schema.
By default MPXJ has been configured to take the same approach. If for some reason you wish to 
apply strict validation when reading an MSPDI file, you can do this using the 
`setMicrosoftProjectCompatibleInput` method, as shown below.

```java
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.mspdi.MSPDIReader;

...

MSPDIReader reader = new MSPDIReader();
reader.setMicrosoftProjectCompatibleInput(false);
ProjectFile project = reader.read("my-sample.xml");
```
