package net.sf.mpxj.openplan;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import net.sf.mpxj.Duration;
import net.sf.mpxj.ResourceType;

interface Row
{
   public String getString(String name);

   public LocalDateTime getDate(String name);

   public LocalTime getTime(String name);

   public Double getDouble(String name);

   public Integer getInteger(String name);

   public Boolean getBoolean(String name);

   public UUID getUuid(String name);
   public Duration getDuration(String name);
   public ResourceType getResourceType(String name);
}
