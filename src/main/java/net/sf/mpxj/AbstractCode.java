package net.sf.mpxj;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractCode<V extends CodeValue> implements Code<V>
{
   protected AbstractCode(Class<? extends Code<?>> c, Builder<?> builder)
   {
      m_uniqueID = builder.m_sequenceProvider.getUniqueIdObjectSequence(c).syncOrGetNext(builder.m_uniqueID);
      m_sequenceNumber = builder.m_sequenceNumber;
      m_name = builder.m_name;
      m_secure = builder.m_secure;
      m_maxLength = builder.m_maxLength;
   }

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
      return m_values.stream().filter(v -> v.getParentValue() == null).collect(Collectors.toList());
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

   protected final Integer m_uniqueID;
   protected final Integer m_sequenceNumber;
   protected final String m_name;
   protected final boolean m_secure;
   protected final Integer m_maxLength;
   protected final List<V> m_values = new ArrayList<>();

   public static abstract class Builder<B>
   {
      /**
       * Constructor.
       *
       * @param sequenceProvider parent file
       */
      public Builder(UniqueIdObjectSequenceProvider sequenceProvider)
      {
         m_sequenceProvider = sequenceProvider;
      }

      /**
       * Add unique ID.
       *
       * @param value unique ID
       * @return builder
       */
      public B uniqueID(Integer value)
      {
         m_uniqueID = value;
         return self();
      }

      /**
       * Add sequence number.
       *
       * @param value sequence number
       * @return builder
       */
      public B sequenceNumber(Integer value)
      {
         m_sequenceNumber = value;
         return self();
      }

      /**
       * Add name.
       *
       * @param value name
       * @return builder
       */
      public B name(String value)
      {
         m_name = value;
         return self();
      }

      /**
       * Add secure flag.
       *
       * @param value secure flag
       * @return builder
       */
      public B secure(boolean value)
      {
         m_secure = value;
         return self();
      }

      /**
       * Add max length.
       *
       * @param value max length
       * @return builder
       */
      public B maxLength(Integer value)
      {
         m_maxLength = value;
         return self();
      }

      protected abstract B self();

      private final UniqueIdObjectSequenceProvider m_sequenceProvider;
      protected Integer m_uniqueID;
      protected Integer m_sequenceNumber;
      protected String m_name;
      protected boolean m_secure;
      protected Integer m_maxLength;
   }
}
