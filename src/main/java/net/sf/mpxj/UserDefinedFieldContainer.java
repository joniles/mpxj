
package net.sf.mpxj;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.sf.mpxj.common.NumberHelper;


public class UserDefinedFieldContainer
{
   public Collection<UserDefinedField> getTaskFields()
   {
      return m_taskFields.values();
   }

   public UserDefinedField getTaskField(Integer id)
   {
      return m_taskFields.get(id);
   }

   public void addTaskField(UserDefinedField field)
   {
      m_taskFields.put(field.getUniqueID(), field);
   }

   private final Map<Integer, UserDefinedField> m_taskFields = new HashMap<>();
}
