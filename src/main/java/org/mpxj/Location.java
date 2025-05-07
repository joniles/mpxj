/*
 * file:       Location.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software
 * date:       2023-04-18
 */

/*
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or (at your
 * option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 */

package org.mpxj;

/**
 * Represents a location, use to tag projects, resources and activities.
 */
public final class Location implements ProjectEntityWithUniqueID
{
   /**
    * Constructor used by builder.
    *
    * @param builder location builder
    */
   private Location(Builder builder)
   {
      m_uniqueID = builder.m_sequenceProvider.getUniqueIdObjectSequence(Location.class).syncOrGetNext(builder.m_uniqueID);
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
      m_postalCode = builder.m_postalCode;
      m_state = builder.m_state;
      m_stateCode = builder.m_stateCode;
   }

   /**
    * Retrieve the unique ID.
    *
    * @return unique ID
    */
   @Override public Integer getUniqueID()
   {
      return m_uniqueID;
   }

   /**
    * Retrieve a line of the address.
    *
    * @return address line
    */
   public String getAddressLine1()
   {
      return m_addressLine1;
   }

   /**
    * Retrieve a line of the address.
    *
    * @return address line
    */
   public String getAddressLine2()
   {
      return m_addressLine2;
   }

   /**
    * Retrieve a line of the address.
    *
    * @return address line
    */
   public String getAddressLine3()
   {
      return m_addressLine3;
   }

   /**
    * Retrieve the city.
    *
    * @return city
    */
   public String getCity()
   {
      return m_city;
   }

   /**
    * Retrieve the country.
    *
    * @return country
    */
   public String getCountry()
   {
      return m_country;
   }

   /**
    * Retrieve the country code.
    *
    * @return country code
    */
   public String getCountryCode()
   {
      return m_countryCode;
   }

   /**
    * Retrieve the latitude.
    *
    * @return latitude
    */
   public Double getLatitude()
   {
      return m_latitude;
   }

   /**
    * Retrieve the longitude.
    *
    * @return longitude
    */
   public Double getLongitude()
   {
      return m_longitude;
   }

   /**
    * Retrieve the municipality.
    *
    * @return municipality
    */
   public String getMunicipality()
   {
      return m_municipality;
   }

   /**
    * Retrieve the name.
    *
    * @return name
    */
   public String getName()
   {
      return m_name;
   }

   /**
    * Retrieve the postal code.
    *
    * @return postal code
    */
   public String getPostalCode()
   {
      return m_postalCode;
   }

   /**
    * Retrieve the state.
    *
    * @return state
    */
   public String getState()
   {
      return m_state;
   }

   /**
    * Retrieve the state code.
    *
    * @return state code
    */
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

   /**
    * Location builder.
    */
   public static class Builder
   {
      /**
       * Constructor.
       *
       * @param sequenceProvider parent project file.
       */
      public Builder(UniqueIdObjectSequenceProvider sequenceProvider)
      {
         m_sequenceProvider = sequenceProvider;
      }

      /**
       * Initialise the builder from an existing Location instance.
       *
       * @param value Location instance
       * @return builder
       */
      Builder from(Location value)
      {
         m_addressLine1 = value.m_addressLine1;
         m_addressLine2 = value.m_addressLine2;
         m_addressLine3 = value.m_addressLine3;
         m_city = value.m_city;
         m_country = value.m_country;
         m_countryCode = value.m_countryCode;
         m_latitude = value.m_latitude;
         m_longitude = value.m_longitude;
         m_municipality = value.m_municipality;
         m_name = value.m_name;
         m_uniqueID = value.m_uniqueID;
         m_postalCode = value.m_postalCode;
         m_state = value.m_state;
         m_stateCode = value.m_stateCode;
         return this;
      }

      /**
       * Add an address line.
       *
       * @param value address line
       * @return builder
       */
      public Builder addressLine1(String value)
      {
         m_addressLine1 = value;
         return this;
      }

      /**
       * Add an address line.
       *
       * @param value address line
       * @return builder
       */
      public Builder addressLine2(String value)
      {
         m_addressLine2 = value;
         return this;
      }

      /**
       * Add an address line.
       *
       * @param value address line
       * @return builder
       */
      public Builder addressLine3(String value)
      {
         m_addressLine3 = value;
         return this;
      }

      /**
       * Add a city.
       *
       * @param value city
       * @return builder
       */
      public Builder city(String value)
      {
         m_city = value;
         return this;
      }

      /**
       * Add a country.
       *
       * @param value country
       * @return builder
       */
      public Builder country(String value)
      {
         m_country = value;
         return this;
      }

      /**
       * Add a country code.
       *
       * @param value country code
       * @return builder
       */
      public Builder countryCode(String value)
      {
         m_countryCode = value;
         return this;
      }

      /**
       * Add the latitude.
       *
       * @param value latitude
       * @return builder
       */
      public Builder latitude(Double value)
      {
         m_latitude = value;
         return this;
      }

      /**
       * Add the longitude.
       *
       * @param value longitude
       * @return builder
       */
      public Builder longitude(Double value)
      {
         m_longitude = value;
         return this;
      }

      /**
       * Add the municipality.
       *
       * @param value municipality
       * @return builder
       */
      public Builder municipality(String value)
      {
         m_municipality = value;
         return this;
      }

      /**
       * Add the name.
       *
       * @param value name
       * @return builder
       */
      public Builder name(String value)
      {
         m_name = value;
         return this;
      }

      /**
       * Add the unique ID.
       *
       * @param value unique ID
       * @return builder
       */
      public Builder uniqueID(Integer value)
      {
         m_uniqueID = value;
         return this;
      }

      /**
       * Add the postal code.
       *
       * @param value postal code
       * @return builder
       */
      public Builder postalCode(String value)
      {
         m_postalCode = value;
         return this;
      }

      /**
       * Add the state.
       *
       * @param value state
       * @return builder
       */
      public Builder state(String value)
      {
         m_state = value;
         return this;
      }

      /**
       * Add the sate code.
       *
       * @param value state code
       * @return builder
       */
      public Builder stateCode(String value)
      {
         m_stateCode = value;
         return this;
      }

      /**
       * Build a Location instance.
       *
       * @return Location instance
       */
      public Location build()
      {
         return new Location(this);
      }

      private final UniqueIdObjectSequenceProvider m_sequenceProvider;
      private String m_addressLine1;
      private String m_addressLine2;
      private String m_addressLine3;
      private String m_city;
      private String m_country;
      private String m_countryCode;
      private Double m_latitude;
      private Double m_longitude;
      private String m_municipality;
      private String m_name;
      private Integer m_uniqueID;
      private String m_postalCode;
      private String m_state;
      private String m_stateCode;
   }
}
