/*
 * file:       ExpenseCategory.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2020
 * date:       12/10/2020
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
 * Expense category definition.
 */
public final class ExpenseCategory implements ProjectEntityWithUniqueID
{
   /**
    * Constructor.
    *
    * @param builder builder class
    */
   private ExpenseCategory(Builder builder)
   {
      m_uniqueID = builder.m_sequenceProvider.getUniqueIdObjectSequence(ExpenseCategory.class).syncOrGetNext(builder.m_uniqueID);
      m_name = builder.m_name;
      m_sequenceNumber = builder.m_sequenceNumber;
   }

   @Override public Integer getUniqueID()
   {
      return m_uniqueID;
   }

   /**
    * Retrieve the expense category name.
    *
    * @return name
    */
   public String getName()
   {
      return m_name;
   }

   /**
    * Retrieve the sequence number.
    *
    * @return sequence number
    */
   public Integer getSequenceNumber()
   {
      return m_sequenceNumber;
   }

   @Override public String toString()
   {
      return "[ExpenseCategory uniqueID=" + m_uniqueID + " name=" + m_name + "]";
   }

   private final Integer m_uniqueID;
   private final String m_name;
   private final Integer m_sequenceNumber;

   /**
    * ExpenseCategory builder.
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
       * Initialise the builder from an existing ExpenseCategory instance.
       *
       * @param value ExpenseCategory instance
       * @return builder
       */
      public Builder from(ExpenseCategory value)
      {
         m_uniqueID = value.m_uniqueID;
         m_name = value.m_name;
         m_sequenceNumber = value.m_sequenceNumber;
         return this;
      }

      /**
       * Add the unique ID.
       *
       * @param value unique ID value
       * @return builder
       */
      public Builder uniqueID(Integer value)
      {
         m_uniqueID = value;
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
       * Add the sequence number.
       *
       * @param value sequence number
       * @return builder
       */
      public Builder sequenceNumber(Integer value)
      {
         m_sequenceNumber = value;
         return this;
      }

      /**
       * Build an ExpenseCategory instance.
       *
       * @return ExpenseCategory instance
       */
      public ExpenseCategory build()
      {
         return new ExpenseCategory(this);
      }

      private final UniqueIdObjectSequenceProvider m_sequenceProvider;
      private Integer m_uniqueID;
      private String m_name;
      private Integer m_sequenceNumber;
   }
}
