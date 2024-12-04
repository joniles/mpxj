package net.sf.mpxj.cpm;

import java.time.LocalDateTime;

public interface Scheduler
{
   public void process(LocalDateTime projectStartDate) throws Exception;
}
