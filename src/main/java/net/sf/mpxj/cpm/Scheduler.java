package net.sf.mpxj.cpm;

import java.time.LocalDateTime;

import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Task;

public interface Scheduler
{
   public void process(ProjectFile file, LocalDateTime projectStartDate) throws Exception;
}
