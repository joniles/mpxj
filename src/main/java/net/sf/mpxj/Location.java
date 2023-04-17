package net.sf.mpxj;

public class Location implements ProjectEntityWithUniqueID
{
   private Location(Builder builder)
   {
      m_addressLine1 = builder.m_addressLine1;
      m_addressLine2 = builder.m_addressLine2;
      m_addressLine3 = builder.m_addressLine3;
      m_city = builder.m_city;
      m_country = builder.m_country;
      m_countryCode = builder.m_countryCode;
      m_latitude = builder.m_latitude;
      m_longitude = builder.m_longitude;
      m_municipality = builder.m_municipality;
      m_name = builder.m_name;
      m_uniqueID = builder.m_uniqueID;
      m_postalCode = builder.m_postalCode;;
      m_state = builder.m_state;
      m_stateCode = builder.m_stateCode;
   }

   @Override public Integer getUniqueID()
   {
      return m_uniqueID;
   }

   @Override public void setUniqueID(Integer id)
   {
      throw new UnsupportedOperationException();
   }

   public String getAddressLine1()
   {
      return m_addressLine1;
   }

   public String getAddressLine2()
   {
      return m_addressLine2;
   }

   public String getAddressLine3()
   {
      return m_addressLine3;
   }

   public String getCity()
   {
      return m_city;
   }

   public String getCountry()
   {
      return m_country;
   }

   public String getCountryCode()
   {
      return m_countryCode;
   }

   public Double getLatitude()
   {
      return m_latitude;
   }

   public Double getLongitude()
   {
      return m_longitude;
   }

   public String getMunicipality()
   {
      return m_municipality;
   }

   public String getName()
   {
      return m_name;
   }

   public String getPostalCode()
   {
      return m_postalCode;
   }

   public String getState()
   {
      return m_state;
   }

   public String getStateCode()
   {
      return m_stateCode;
   }

   private final String m_addressLine1;
   private final String m_addressLine2;
   private final String m_addressLine3;
   private final String m_city;
   private final String m_country;
   private final String m_countryCode;
   private final Double m_latitude;
   private final Double m_longitude;
   private final String m_municipality;
   private final String m_name;
   private final Integer m_uniqueID;
   private final String m_postalCode;
   private final String m_state;
   private final String m_stateCode;

   public static class Builder
   {
      public Builder addressLine1(String value)
      {
         m_addressLine1 = value;
         return this;
      }

      public Builder addressLine2(String value)
      {
         m_addressLine2 = value;
         return this;
      }

      public Builder addressLine3(String value)
      {
         m_addressLine3 = value;
         return this;
      }

      public Builder city(String value)
      {
         m_city = value;
         return this;
      }

      public Builder country(String value)
      {
         m_country = value;
         return this;
      }

      public Builder countryCode(String value)
      {
         m_countryCode = value;
         return this;
      }

      public Builder latitude(Double value)
      {
         m_latitude = value;
         return this;
      }

      public Builder longitude(Double value)
      {
         m_longitude = value;
         return this;
      }

      public Builder municipality(String value)
      {
         m_municipality = value;
         return this;
      }

      public Builder name(String value)
      {
         m_name = value;
         return this;
      }

      public Builder uniqueID(Integer value)
      {
         m_uniqueID = value;
         return this;
      }

      public Builder postalCode(String value)
      {
         m_postalCode = value;
         return this;
      }

      public Builder state(String value)
      {
         m_state = value;
         return this;
      }

      public Builder stateCode(String value)
      {
         m_stateCode = value;
         return this;
      }

      public Location build()
      {
         return new Location(this);
      }

      private  String m_addressLine1;
      private  String m_addressLine2;
      private  String m_addressLine3;
      private  String m_city;
      private  String m_country;
      private  String m_countryCode;
      private  Double m_latitude;
      private  Double m_longitude;
      private  String m_municipality;
      private  String m_name;
      private  Integer m_uniqueID;
      private  String m_postalCode;
      private  String m_state;
      private  String m_stateCode;
   }
}
