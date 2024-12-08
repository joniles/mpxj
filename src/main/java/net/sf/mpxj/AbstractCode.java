package net.sf.mpxj;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractCode<V extends CodeValue> implements Code<V>
{
   @Override public Integer getUniqueID()
   {
      return m_uniqueID;
   }

   public Integer getSequenceNumber()
   {
      return m_sequenceNumber;
   }

   public String getName()
   {
      return m_name;
   }

   public boolean getSecure()
   {
      return m_secure;
   }

   public Integer getMaxLength()
   {
      return m_maxLength;
   }

   public List<V> getValues()
   {
      return m_values;
   }

   public List<V> getChildValues()
   {
      return m_values.stream().filter(v -> v.getParent() == null).collect(Collectors.toList());
   }

   /**
    * Add a value to this activity code.
    *
    * @param value activity code value
    */
   public void addValue(V value)
   {
      m_values.add(value);
   }

   /**
    * Retrieve a value belonging to this activity code using its unique ID.
    *
    * @param id activity code value unique ID
    * @return ActivityCodeValue instance or null
    */
   public V getValueByUniqueID(Integer id)
   {
      if (id == null)
      {
         return null;
      }

      // I'd prefer a map-based lookup, but this will do for now and the list of values will typically be fairly short
      return m_values.stream().filter(v -> v.getUniqueID().intValue() == id.intValue()).findFirst().orElse(null);
   }

   // TODO - builder and make final
   private  Integer m_uniqueID;
   private  Integer m_sequenceNumber;
   private  String m_name;
   private  boolean m_secure;
   private  Integer m_maxLength;
   private final List<V> m_values = new ArrayList<>();
}
