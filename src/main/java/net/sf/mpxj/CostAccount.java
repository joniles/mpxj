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
    */
   public CostAccount(Integer uniqueID, String id, String name, String description, Integer sequenceNumber)
   {
      m_uniqueID = uniqueID;
      m_id = id;
      m_name = name;
      m_description = description;
      m_sequenceNumber = sequenceNumber;
   }

   /**
    * Constructor.
    *
    * @param uniqueID unique ID
    * @param id short name
    * @param name name
    * @param description description
    * @param sequenceNumber sequence number
    * @param parent parent
    */
   public CostAccount(Integer uniqueID, String id, String name, String description, Integer sequenceNumber, CostAccount parent)
   {
      m_uniqueID = uniqueID;
      m_id = id;
      m_name = name;
      m_description = description;
      m_sequenceNumber = sequenceNumber;
      m_parent = parent;
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
    */
   public String getDescription()
   {
      return m_description;
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
   private final String m_description;
   private final Integer m_sequenceNumber;
   private CostAccount m_parent;

   public static class Builder
   {
      public Builder(ProjectFile file)
      {
         m_file = file;
      }

      private final ProjectFile m_file;
   }
}