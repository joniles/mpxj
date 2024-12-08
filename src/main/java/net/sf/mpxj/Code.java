package net.sf.mpxj;

import java.util.List;

public interface Code<V extends CodeValue> extends ProjectEntityWithUniqueID
{
   public Integer getUniqueID();

   public Integer getSequenceNumber();

   public String getName();

   public boolean getSecure();

   public Integer getMaxLength();

   public List<V> getValues();

   public List<V> getChildValues();

   public void  addValue(V value);

   public V getValueByUniqueID(Integer id);
}
