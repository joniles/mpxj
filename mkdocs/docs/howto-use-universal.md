# How To: Use the Universal Project Reader

As you may have seen elsewhere in this documentation, the preferred way to read
from most sources of schedule data is to use the `UniversalProjectReader`:

```java
package org.mpxj.howto.use.universal;

import org.mpxj.ProjectFile;
import org.mpxj.reader.ProjectReader;
import org.mpxj.reader.UniversalProjectReader;

public class SimpleExample
{
   public void process() throws Exception
   {
      ProjectReader reader = new UniversalProjectReader();
      ProjectFile project = reader.read("example.mpp");
   }
}
```

This is very convenient as you don't need to know ahead of time what type of
schedule file you are working with, `UniversalProjectReader` will figure this
out for you. The drawback to this approach is that for a number of schedule
types, the reader class for that type may provide additional configuration
options to guide the way schedule data is read. In the example above, you can
see that there is no opportunity to provide any extra configuration to the
reader class selected by `UniversalProjectReader`.

To get around this issue, `UniversalProjectReader` provides access to "project
reader proxy" classes. These proxy classes implement the
`UniversalProjectReader.ProjectReaderProxy` interface and provide access to the
reader class which `UniversalProjectReader` has selected to read the project
data at the point just before schedule data has been read.

You can use these proxy classes to, for example, choose whether or not to
continue reading the type of schedule contained in the supplied file or stream,
or you can change the reader's settings before continuing to read the schedule.
The example code below illustrates both these situations.

```java
package org.mpxj.howto.use.universal;

import org.mpxj.ProjectFile;
import org.mpxj.phoenix.PhoenixReader;
import org.mpxj.reader.ProjectReader;
import org.mpxj.reader.UniversalProjectReader;
import org.mpxj.sdef.SDEFReader;

import java.io.File;

public class ProxyExample
{
   public void process(File file) throws Exception
   {
      UniversalProjectReader upr = new UniversalProjectReader();

      // Retrieve the proxy
      try (UniversalProjectReader.ProjectReaderProxy proxy
         = upr.getProjectReaderProxy(file))
      {
         // Retrieve the reader class
         ProjectReader reader = proxy.getProjectReader();

         // Determine if we want to continue processing this file type.
         // In this example we are ignoring SDEF files.
         if (reader instanceof SDEFReader)
         {
            return;
         }

         // Provide configuration for specific reader types.
         // In this example we are changing the behavior of the Phoenix reader.
         if (reader instanceof PhoenixReader)
         {
            ((PhoenixReader) reader).setUseActivityCodesForTaskHierarchy(false);
         }

         // Finally, we read the schedule
         ProjectFile project = proxy.read();

         // Now we can work with the schedule data...
      }
   }
}
```

The first thing to notice is that the proxy class is being used within a "try
with resources" statement. This is important as the `UniversalProjectReader`
may have a number of resources open (streams, temporary files, and so on) which
need to be released once you have finished with the proxy class.
`UniversalProjectReader.ProjectReaderProxy` implements the `AutoCloseable`
interface, so you can either arrange to explicitly call the `close` method
yourself at an appropriate point, or you can use try with resources to ensure
this happens automatically.

The initial line of the `try` statement calls `getProjectReaderProxy` to
retrieve the proxy. This method can be called with either a file name, a `File`
instance, or an `InputStream`. Within the `try` block, the first thing we do is
retrieve the reader class instance which the `UniversalProjectReader` has
selected to read our schedule data.

The next two stanzas of code use `instanceof` to determine the type of the
reader selected: in the first stanza we're choosing not to continue if we've
been provided with an SDEF file. In the second stanza, if we are dealing with a
Phoenix schedule, we're choosing to change the default behavior of the reader.

Finally at the end of the `try` block we're calling the `read` method of the
proxy to read the schedule. The proxy also provides a `readAll` method: if the
source data contains multiple schedules you can use this method to read them
all.

Note that we're using the `read` or `readAll` methods provided by the
proxy class, we're not attempting to use the methods provided on the reader
class itself. This is important as the `UniversalProjectReader` may have
located the schedule within a larger set of data, for example within a Zip file
or sub-directory. The proxy class already has this context, whereas you won't
necessarily have this information if you tried to use the reader class methods
directly. 
