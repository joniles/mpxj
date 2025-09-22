package org.mpxj.utility;

import java.time.LocalDateTime;

import org.mpxj.ProjectFile;
import org.mpxj.cpm.PrimaveraScheduler;
import org.mpxj.cpm.PrimaveraSchedulerComparator;
import org.mpxj.reader.UniversalProjectReader;
import org.mpxj.writer.FileFormat;
import org.mpxj.writer.UniversalProjectWriter;

public class SchedulerTest
{
   public static void main(String[] argv) throws Exception
   {
      ProjectFile baseline = new UniversalProjectReader().read("/Users/joniles/Downloads/p6-scheduled.xer");
      ProjectFile file = new UniversalProjectReader().read("/Users/joniles/Downloads/p6-original.xer");

      file.getProjectProperties().setStatusDate(LocalDateTime.of(2024, 7, 21, 8, 0));
      LocalDateTime start = file.getProjectProperties().getPlannedStart();
      if (start == null)
      {
         start = file.getProjectProperties().getStartDate();
      }

      new PrimaveraScheduler().schedule(file, start);

      PrimaveraSchedulerComparator comparator = new PrimaveraSchedulerComparator();
      comparator.setDebug(true);
      comparator.process(baseline, file, true, true);

      new UniversalProjectWriter(FileFormat.XER).write(file, "/Users/joniles/Downloads/mpxj-scheduled.xer");
   }
}
