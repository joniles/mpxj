package net.sf.mpxj;

public class UnitOfMeasure implements ProjectEntityWithUniqueID
{
   private UnitOfMeasure(Builder builder)
   {
      m_uniqueID = builder.m_uniqueID;
      m_name = builder.m_name;
      m_abbreviation = builder.m_abbreviation;
      m_sequenceNumber = builder.m_sequenceNumber;
   }

   @Override public Integer getUniqueID()
   {
      return m_uniqueID;
   }

   @Override public void setUniqueID(Integer id)
   {
      throw new UnsupportedOperationException();
   }

   public String getName()
   {
      return m_name;
   }

   public String getAbbreviation()
   {
      return m_abbreviation;
   }

   public Integer getSequenceNumber()
   {
      return m_sequenceNumber;
   }

   private final Integer m_uniqueID;
   private final String m_name;
   private final String m_abbreviation;
   private final Integer m_sequenceNumber;

   public static class Builder
   {
      public Builder setUniqueID(Integer uniqueID)
      {
         m_uniqueID = uniqueID;
         return this;
      }

      public Builder setName(String name)
      {
         m_name = name;
         return this;
      }

      public Builder setAbbreviation(String abbreviation)
      {
         m_abbreviation = abbreviation;
         return this;
      }

      public Builder setSequenceNumber(Integer sequenceNumber)
      {
         m_sequenceNumber = sequenceNumber;
         return this;
      }

      public UnitOfMeasure build()
      {
         return new UnitOfMeasure(this);
      }

      private Integer m_uniqueID;
      private String m_name;
      private String m_abbreviation;
      private Integer m_sequenceNumber;
   }
}
