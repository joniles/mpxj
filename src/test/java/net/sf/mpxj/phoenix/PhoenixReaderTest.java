package net.sf.mpxj.phoenix;
import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Task;
import net.sf.mpxj.junit.MpxjTestData;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class PhoenixReaderTest
{
   @Test
   public void testPPXFinishDates() throws MPXJException
   {
      PhoenixReader reader = new PhoenixReader();
      ProjectFile projectFile = reader.read(MpxjTestData.filePath("23.07.01 - 00 - PHYSICAL PERCENT COMPLETE.ppx"));
      for (Task task : projectFile.getTasks()) {
         assertNotNull(task.getFinish());
      }
   }
}
