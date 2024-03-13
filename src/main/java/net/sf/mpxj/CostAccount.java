/*
 * file:       CostAccount.java
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

package net.sf.mpxj;

/**
 * Cost account definition.
 */
public class CostAccount implements ProjectEntityWithUniqueID
{
   /**
    * Constructor.
    *
    * @param uniqueID unique ID
    * @param id short name
    * @param name name
    * @param description description
    * @param sequenceNumber sequence number
    * @deprecated use builder
    */
   @Deprecated public CostAccount(Integer uniqueID, String id, String name, String description, Integer sequenceNumber)
   {
      m_uniqueID = uniqueID;
      m_id = id;
      m_name = name;
      m_notes = new Notes(description);
      m_sequenceNumber = sequenceNumber;
   }

   /**
    * Constructor.
    *
    * @param builder builder
    */
   private CostAccount(Builder builder)
   {
      m_uniqueID = builder.m_file.getUniqueIdObjectSequence(CostAccount.class).syncOrGetNext(builder.m_uniqueID);
      m_id = builder.m_id;
      m_name = builder.m_name;
      m_notes = builder.m_notes;
      m_sequenceNumber = builder.m_sequenceNumber;
      m_parent = builder.m_parent;
   }

   @Override public Integer getUniqueID()
   {
      return m_uniqueID;
   }

   @Override public void setUniqueID(Integer id)
   {
      throw new UnsupportedOperationException();
   }

   /**
    * Retrieve the short name.
    *
    * @return short name
    */
   public String getID()
   {
      return m_id;
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
    * Retrieve the description.
    *
    * @return description
    * @deprecated use getNotes method instead
    */
   @Deprecated public String getDescription()
   {
      return m_notes.toString();
   }

   /**
    * Retrieve the notes text.
    *
    * @return notes text
    */
   public String getNotes()
   {
      return m_notes == null ? null : m_notes.toString();
   }

   /**
    * Retrieve the notes object.
    *
    * @return notes object
    */
   public Notes getNotesObject()
   {
      return m_notes;
   }

   /**
    * Retrieve the sequence number.
    *
    * @return sequence
    */
   public Integer getSequenceNumber()
   {
      return m_sequenceNumber;
   }

   /**
    * Retrieve the parent cost account unique ID.
    *
    * @return parent cost account unique ID
    */
   public Integer getParentUniqueID()
   {
      return m_parent == null ? null : m_parent.getUniqueID();
   }

   /**
    * Retrieve the parent cost account.
    *
    * @return parent cost account
    */
   public CostAccount getParent()
   {
      return m_parent;
   }

   /**
    * Set the parent cost account.
    *
    * @param parent parent cost account
    */
   public void setParent(CostAccount parent)
   {
      m_parent = parent;
   }

   @Override public String toString()
   {
      return "[CostAccount uniqueID=" + m_uniqueID + " name=" + m_name + "]";
   }

   private final Integer m_uniqueID;
   private final String m_id;
   private final String m_name;
   private final Notes m_notes;
   private final Integer m_sequenceNumber;
   private CostAccount m_parent;

   /**
    * CostAccount builder.
    */
   public static class Builder
   {
      /**
       * Constructor.
       *
       * @param file parent file
       */
      public Builder(ProjectFile file)
      {
         m_file = file;
      }

      /**
       * Initialise the builder from an existing CostAccount instance.
       *
       * @param value CostAccount instance
       * @return builder
       */
      public Builder from(CostAccount value)
      {
         m_uniqueID = value.m_uniqueID;
         m_id = value.m_id;
         m_name = value.m_name;
         m_notes = value.m_notes;
         m_sequenceNumber = value.m_sequenceNumber;
         m_parent = value.m_parent;
         return this;
      }

      /**
       * Add unique ID.
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
       * Add ID.
       *
       * @param value id
       * @return builder
       */
      public Builder id(String value)
      {
         m_id = value;
         return this;
      }

      /**
       * Add name.
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
       * Add description.
       *
       * @param value description
       * @return builder
       * @deprecated use notes method instead
       */
      @Deprecated public Builder description(String value)
      {
         return notes(value);
      }

      /**
       * Add notes.
       *
       * @param value notes
       * @return builder
       */
      public Builder notes(String value)
      {
         m_notes = new Notes(value);
         return this;
      }

      /**
       * Add notes.
       *
       * @param value notes
       * @return builder
       */
      public Builder notes(Notes value)
      {
         m_notes = value;
         return this;
      }

      /**
       * Add sequence number.
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
       * Add parent.
       *
       * @param value parent
       * @return builder
       */
      public Builder parent(CostAccount value)
      {
         m_parent = value;
         return this;
      }

      /**
       * Build a CostAccount instance.
       *
       * @return CostAccount instance
       */
      public CostAccount build()
      {
         return new CostAccount(this);
      }

      private final ProjectFile m_file;
      private Integer m_uniqueID;
      private String m_id;
      private String m_name;
      private Notes m_notes;
      private Integer m_sequenceNumber;
      private CostAccount m_parent;
   }
}