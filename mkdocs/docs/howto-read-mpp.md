# How To: Read MPP files
The native Microsoft Project file format is typically has the extension MPP
(or MPT for a template file). Although a common file extension uis used,
there are actually a number if different variants of the file format.
The list below shows the different variants, and the versions of 
Microsoft Project which produce them:

* MPP8 - Project 98
* MPP9 - Project 2000 and Project 2002
* MPP12 - Project 2003, Project 2007
* MPP14 - Project 2010 and all later versions

## Reading MPP files
The simplest way to read an MPP file is to use the `UniversalProjectReader`:

```java
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.reader.UniversalProjectReader;

// ...

UniversalProjectReader reader = new UniversalProjectReader();
ProjectFile project = reader.read("my-sample.mpp");
```

## Using MPPReader
You can work directly with the `MPPReader` class by replacing `UniversalProjectReader`
with `MPPReader`. This provides access to additional options, as described below.

### Password Protected Files
When a read password has been set for an MPP file, the contents of the file are partially
encrypted. If you attempt to read an MPP file which has been password protected an `MPXJException` 
will be raised, with the message `File is password protected`.

MPXJ only supports decryption of password protected MPP9 files. The code below illustrates
how you would supply the password:

```java
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.mpp.MPPReader;

// ...

MPPReader reader = new MPPReader();
reader.setReadPassword("my secret password");
ProjectFile project = reader.read("my-sample.mpp");
```

The encryption used by MPP9 files doesn't actually require the password in order to
read the contents of the file. If you wish you can set a flag to ignore 
the MPP9 password protection.

```java
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.mpp.MPPReader;

// ...

MPPReader reader = new MPPReader();
reader.setRespectPasswordProtection(false);
ProjectFile project = reader.read("my-sample.mpp");
```

### Presentation Data
Alongside the schedule data itself, MPXJ also extracts much of the presentation data
available in an MPP file, for example table layouts, filters, graphical indicators
and so on. If you are not interested in this type of data, you can tell MPXJ not
to read it. This will speed up reading MPP files, and slightly reduce memory consumption.
To do this you will use the `setReadPresentationData` method, as shown below:

```java
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.mpp.MPPReader;

// ...

MPPReader reader = new MPPReader();
reader.setReadPresentationData(false);
ProjectFile project = reader.read("my-sample.mpp");
```

### Properties Only
Should you wish to simply "peek" at the contents of the MPP file by just reading the
summary properties from the file, you can use the `setReadPropertiesOnly` method
as shown below:

```java
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.mpp.MPPReader;

// ...

MPPReader reader = new MPPReader();
reader.setReadPropertiesOnly(true);
ProjectFile project = reader.read("my-sample.mpp");
```

### Raw timephased data
When MPXJ reads timephased data from an MPP file it "normalises" the data,
converting it from the compact format Microsoft Project uses internally
into a representation which shows the timephased values day-by-day. This
is generally easier to understand, and can be further processed using the
methods in the `TimephasedUtility` class to show the data over the
required timescale.

If you do not want MPXJ to normalise the data, and would prefer instead to
work with the raw data directly from the MPP file, you can use the
`setUseRawTimephasedData` to do this, as shown below:

```java
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.mpp.MPPReader;

// ...

MPPReader reader = new MPPReader();
reader.setUseRawTimephasedData(true);
ProjectFile project = reader.read("my-sample.mpp");
```
