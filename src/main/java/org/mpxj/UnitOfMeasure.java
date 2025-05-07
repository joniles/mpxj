/*
 * file:       UnitOfMeasure.java
 * author:     Jon Iles
 * date:       09/10/2023
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
 * Class representing a unit of measure.
 */
public final class UnitOfMeasure implements ProjectEntityWithUniqueID
{
   /**
    * Constructor.
    *
    * @param builder builder instance
    */
   private UnitOfMeasure(Builder builder)
   {
      m_uniqueID = builder.m_sequenceProvider.getUniqueIdObjectSequence(UnitOfMeasure.class).syncOrGetNext(builder.m_uniqueID);
      m_name = builder.m_name;
      m_abbreviation = builder.m_abbreviation;
      m_sequenceNumber = builder.m_sequenceNumber;
   }

   @Override public Integer getUniqueID()
   {
      return m_uniqueID;
   }

   /**
    * Retrieve the unit of measure name.
    *
    * @return unit of measure name
    */
   public String getName()
   {
      return m_name;
   }

   /**
    * Retrieve the unit of measure abbreviation.
    *
    * @return unit of measure abbreviation
    */
   public String getAbbreviation()
   {
      return m_abbreviation;
   }

   /**
    * Retrieve the unit of measure sequence number.
    *
    * @return sequence number
    */
   public Integer getSequenceNumber()
   {
      return m_sequenceNumber;
   }

   private final Integer m_uniqueID;
   private final String m_name;
   private final String m_abbreviation;
   private final Integer m_sequenceNumber;

   /**
    * Unit of measure builder.
    */
   public static class Builder
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
       * Initialise the builder from an existing UnitOfMeasure instance.
       *
       * @param value UnitOfMeasure instance
       * @return builder
       */
      public Builder from(UnitOfMeasure value)
      {
         m_uniqueID = value.m_uniqueID;
         m_name = value.m_name;
         m_abbreviation = value.m_abbreviation;
         m_sequenceNumber = value.m_sequenceNumber;
         return this;
      }

      /**
       * Add the unique ID.
       *
       * @param uniqueID unique ID
       * @return builder
       */
      public Builder uniqueID(Integer uniqueID)
      {
         m_uniqueID = uniqueID;
         return this;
      }

      /**
       * Add the name.
       *
       * @param name name
       * @return builder
       */
      public Builder name(String name)
      {
         m_name = name;
         return this;
      }

      /**
       * Add the abbreviation.
       *
       * @param abbreviation abbreviation
       * @return builder
       */
      public Builder abbreviation(String abbreviation)
      {
         m_abbreviation = abbreviation;
         return this;
      }

      /**
       * Add the sequence number.
       *
       * @param sequenceNumber sequence number
       * @return builder
       */
      public Builder sequenceNumber(Integer sequenceNumber)
      {
         m_sequenceNumber = sequenceNumber;
         return this;
      }

      /**
       * Build a UnitOfMeasure instance.
       *
       * @return UnitOfMeasure instance
       */
      public UnitOfMeasure build()
      {
         return new UnitOfMeasure(this);
      }

      private final UniqueIdObjectSequenceProvider m_sequenceProvider;
      private Integer m_uniqueID;
      private String m_name;
      private String m_abbreviation;
      private Integer m_sequenceNumber;
   }
}
