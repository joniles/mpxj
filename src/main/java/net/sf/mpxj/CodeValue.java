package net.sf.mpxj;

import java.util.List;


public interface CodeValue
{
   public Code getParentCode();

   public Integer getUniqueID();

   public Integer getSequenceNumber();

   public String getName();

   public String getDescription();

   public CodeValue getParent();

   public Integer getParentUniqueID();

   public List<? extends CodeValue> getChildValues();
}
