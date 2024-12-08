package net.sf.mpxj;

import java.util.List;

public interface Code extends ProjectEntityWithUniqueID
{
   public Integer getUniqueID();

   public Integer getSequenceNumber();

   public String getName();

   public boolean getSecure();

   public Integer getMaxLength();

   public List<ActivityCodeValue> getValues();

   public List<ActivityCodeValue> getChildValues();

   public void addValue(ActivityCodeValue value);

   public ActivityCodeValue getValueByUniqueID(Integer id);
}
