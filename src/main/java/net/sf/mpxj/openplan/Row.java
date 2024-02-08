package net.sf.mpxj.openplan;

import java.time.LocalDateTime;

interface Row
{
   public String getString(String name);

   public LocalDateTime getDate(String name);

   public Double getDouble(String name);

   public Integer getInteger(String name);

   public Boolean getBoolean(String name);
}
