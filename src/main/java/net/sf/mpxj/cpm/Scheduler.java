package net.sf.mpxj.cpm;

import java.time.LocalDateTime;
import net.sf.mpxj.Task;

public interface Scheduler
{
   public void process(LocalDateTime projectStartDate) throws Exception;

   // TODO: refactor to remove this
   public boolean ignoreTask(Task task);
}
