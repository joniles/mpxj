
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

   public Collection<UserDefinedField> getResourceFields()
   {
      return m_resourceFields.values();
   }

   public Collection<UserDefinedField> getAssignmentFields()
   {
      return m_assignmentFields.values();
   }


   public UserDefinedField getTaskField(Integer id)
   {
      return m_taskFields.get(id);
   }

   public UserDefinedField getResourceField(Integer id)
   {
      return m_resourceFields.get(id);
   }

   public UserDefinedField getAssignmentField(Integer id)
   {
      return m_resourceFields.get(id);
   }

   public UserDefinedField getTaskField(Integer id, Function<Integer, UserDefinedField> createFunction)
   {
      return m_taskFields.computeIfAbsent(id, createFunction);
   }

   public UserDefinedField getResourceField(Integer id, Function<Integer, UserDefinedField> createFunction)
   {
      return m_resourceFields.computeIfAbsent(id, createFunction);
   }

   public UserDefinedField getAssignmentField(Integer id, Function<Integer, UserDefinedField> createFunction)
   {
      return m_assignmentFields.computeIfAbsent(id, createFunction);
   }

   public void addTaskField(UserDefinedField field)
   {
      m_taskFields.put(field.getUniqueID(), field);
   }

   public void addResourceField(UserDefinedField field)
   {
      m_resourceFields.put(field.getUniqueID(), field);
   }

   public void addAssignmentField(UserDefinedField field)
   {
      m_assignmentFields.put(field.getUniqueID(), field);
   }

   private final Map<Integer, UserDefinedField> m_taskFields = new HashMap<>();
   private final Map<Integer, UserDefinedField> m_resourceFields = new HashMap<>();
   private final Map<Integer, UserDefinedField> m_assignmentFields = new HashMap<>();

}
