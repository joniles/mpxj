
package net.sf.mpxj;

import java.util.ArrayList;
import java.util.List;

public class ActivityCode
{
   public ActivityCode(Integer uniqueID, String name)
   {
      m_uniqueID = uniqueID;
      m_name = name;
   }

   public Integer getUniqueID()
   {
      return m_uniqueID;
   }

   public String getName()
   {
      return m_name;
   }

   public ActivityCodeValue addValue(Integer uniqueID, String name, String description)
   {
      ActivityCodeValue value = new ActivityCodeValue(this, uniqueID, name, description);
      m_values.add(value);
      return value;
   }

   public List<ActivityCodeValue> getValues()
   {
      return m_values;
   }

   private final Integer m_uniqueID;
   private final String m_name;
   private final List<ActivityCodeValue> m_values = new ArrayList<ActivityCodeValue>();
}
