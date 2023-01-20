
package net.sf.mpxj;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

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

   public UserDefinedField getTaskField(Integer id, Function<Integer, UserDefinedField> createFunction)
   {
      return m_taskFields.computeIfAbsent(id, createFunction);
   }

   public void addTaskField(UserDefinedField field)
   {
      m_taskFields.put(field.getUniqueID(), field);
   }

   private final Map<Integer, UserDefinedField> m_taskFields = new HashMap<>();
}
