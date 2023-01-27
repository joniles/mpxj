
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

   public Collection<UserDefinedField> getProjectFields()
   {
      return m_projectFields.values();
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
      return m_assignmentFields.get(id);
   }

   public UserDefinedField getProjectField(Integer id)
   {
      return m_projectFields.get(id);
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
   public UserDefinedField getProjectField(Integer id, Function<Integer, UserDefinedField> createFunction)
   {
      return m_projectFields.computeIfAbsent(id, createFunction);
   }

   public void addField(UserDefinedField field)
   {
      Map<Integer, UserDefinedField> map;

      switch(field.getFieldTypeClass())
      {
         case TASK:
         {
            map = m_taskFields;
            break;
         }

         case RESOURCE:
         {
            map = m_resourceFields;
            break;
         }

         case ASSIGNMENT:
         {
            map = m_assignmentFields;
            break;
         }

         case PROJECT:
         {
            map = m_projectFields;
            break;
         }

         default:
         {
            map = null;
            break;
         }
      }

      if (map != null)
      {
         map.put(field.getUniqueID(), field);
      }
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
   public void addProjectField(UserDefinedField field)
   {
      m_projectFields.put(field.getUniqueID(), field);
   }

   private final Map<Integer, UserDefinedField> m_taskFields = new HashMap<>();
   private final Map<Integer, UserDefinedField> m_resourceFields = new HashMap<>();
   private final Map<Integer, UserDefinedField> m_assignmentFields = new HashMap<>();
   private final Map<Integer, UserDefinedField> m_projectFields = new HashMap<>();
}
