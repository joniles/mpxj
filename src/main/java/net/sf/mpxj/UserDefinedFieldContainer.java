
package net.sf.mpxj;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import net.sf.mpxj.common.FieldTypeHelper;

public class UserDefinedFieldContainer implements Iterable<UserDefinedField>
{
   public Set<UserDefinedField> getFields()
   {
      return m_fields;
   }

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
      return m_taskFields.computeIfAbsent(id, (i) -> addField(createFunction.apply(i)));
   }

   public UserDefinedField getResourceField(Integer id, Function<Integer, UserDefinedField> createFunction)
   {
      return m_resourceFields.computeIfAbsent(id, (i) -> addField(createFunction.apply(i)));
   }

   public UserDefinedField getAssignmentField(Integer id, Function<Integer, UserDefinedField> createFunction)
   {
      return m_assignmentFields.computeIfAbsent(id, (i) -> addField(createFunction.apply(i)));
   }
   public UserDefinedField getProjectField(Integer id, Function<Integer, UserDefinedField> createFunction)
   {
      return m_projectFields.computeIfAbsent(id, (i) -> addField(createFunction.apply(i)));
   }

   public UserDefinedField addField(UserDefinedField field)
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
         m_fields.add(field);
      }

      return field;
   }

   @Override public Iterator<UserDefinedField> iterator()
   {
      return m_fields.iterator();
   }

   @Override public void forEach(Consumer<? super UserDefinedField> action)
   {
      m_fields.forEach(action);
   }

   @Override public Spliterator<UserDefinedField> spliterator()
   {
      return m_fields.spliterator();
   }

   public Stream<UserDefinedField> stream()
   {
      return StreamSupport.stream(spliterator(), false);
   }

   public boolean isEmpty()
   {
      return m_fields.isEmpty();
   }

   private final Map<Integer, UserDefinedField> m_taskFields = new HashMap<>();
   private final Map<Integer, UserDefinedField> m_resourceFields = new HashMap<>();
   private final Map<Integer, UserDefinedField> m_assignmentFields = new HashMap<>();
   private final Map<Integer, UserDefinedField> m_projectFields = new HashMap<>();
   private final Set<UserDefinedField> m_fields = new HashSet<>();
}
