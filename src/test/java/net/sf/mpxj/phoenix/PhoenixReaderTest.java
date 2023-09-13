package net.sf.mpxj.phoenix;
import java.io.IOException;
import java.net.URISyntaxException;

import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ResourceLoader;
import net.sf.mpxj.Task;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class PhoenixReaderTest
{
   @Test
   public void testPPXFinishDates() throws IOException, URISyntaxException, MPXJException
   {
      PhoenixReader reader = new PhoenixReader();
      ProjectFile projectFile = reader.read(ResourceLoader.loadResource("23.07.01 - 00 - PHYSICAL PERCENT COMPLETE.ppx"));
      for (Task task : projectFile.getTasks()) {
         assertNotNull(task.getFinish());
      }
   }
}
