package net.sf.mpxj;

import java.util.List;


public interface CodeValue
{
   Code<? extends CodeValue> getParentCode();

   public Integer getUniqueID();

   public Integer getSequenceNumber();

   public String getName();

   public String getDescription();

   public CodeValue getParentValue();

   public Integer getParentValueUniqueID();

   public List<? extends CodeValue> getChildValues();
}
