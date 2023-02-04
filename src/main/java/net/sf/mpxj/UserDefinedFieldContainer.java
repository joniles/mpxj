
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

public class UserDefinedFieldContainer implements Collection<UserDefinedField>
{
   @Override public Iterator<UserDefinedField> iterator()
   {
      return m_fields.iterator();
   }

   @Override public Object[] toArray()
   {
      return m_fields.toArray();
   }

   @Override public <T> T[] toArray(T[] a)
   {
      return m_fields.toArray(a);
   }

   @Override public boolean add(UserDefinedField field)
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

      return true;
   }

   @Override public boolean remove(Object o)
   {
      m_taskFields.remove(o);
      m_resourceFields.remove(o);
      m_assignmentFields.remove(o);
      m_projectFields.remove(o);
      return m_fields.remove(o);
   }

   @Override public boolean containsAll(Collection<?> c)
   {
      return m_fields.containsAll(c);
   }

   @Override public boolean addAll(Collection<? extends UserDefinedField> c)
   {
      throw new UnsupportedOperationException();
   }

   @Override public boolean removeAll(Collection<?> c)
   {
      throw new UnsupportedOperationException();
   }

   @Override public boolean retainAll(Collection<?> c)
   {
      throw new UnsupportedOperationException();
   }

   @Override public void clear()
   {
      m_taskFields.clear();
      m_resourceFields.clear();
      m_assignmentFields.clear();
      m_projectFields.clear();
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

   @Override public int size()
   {
      return m_fields.size();
   }

   public boolean isEmpty()
   {
      return m_fields.isEmpty();
   }

   @Override public boolean contains(Object o)
   {
      return m_fields.contains(o);
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

   private UserDefinedField addField(UserDefinedField field)
   {
      add(field);
      return field;
   }

   private final Map<Integer, UserDefinedField> m_taskFields = new HashMap<>();
   private final Map<Integer, UserDefinedField> m_resourceFields = new HashMap<>();
   private final Map<Integer, UserDefinedField> m_assignmentFields = new HashMap<>();
   private final Map<Integer, UserDefinedField> m_projectFields = new HashMap<>();
   private final Set<UserDefinedField> m_fields = new HashSet<>();
}
