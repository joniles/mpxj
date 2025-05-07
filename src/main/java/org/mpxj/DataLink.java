/*
 * file:       DataLink.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2019
 * date:       15/06/2019
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
 * Represents a link between two fields, either in the same project or across projects.
 * Normally each link will specify the source and target in a single record, although sometimes
 * these can be split across two records (source in one, target in the other). Also
 * when a link is removed you may end up with a record which just specifies a source but
 * no target.
 */
public final class DataLink
{
   /**
    * Constructor.
    *
    * @param id identifier for this link
    */
   public DataLink(String id)
   {
      m_id = id;
   }

   /**
    * Retrieve the link identifier.
    *
    * @return link identifier
    */
   public String getID()
   {
      return m_id;
   }

   /**
    * Retrieve the source field.
    *
    * @return source field
    */
   public FieldType getSourceField()
   {
      return m_sourceField;
   }

   /**
    * Set the source field.
    *
    * @param sourceField source field
    */
   public void setSourceField(FieldType sourceField)
   {
      m_sourceField = sourceField;
   }

   /**
    * Unique ID of the source object.
    *
    * @return unique ID
    */
   public Integer getSourceUniqueID()
   {
      return m_sourceUniqueID;
   }

   /**
    * Set the unique ID of the source object.
    *
    * @param sourceUniqueID source object unique ID
    */
   public void setSourceUniqueID(Integer sourceUniqueID)
   {
      m_sourceUniqueID = sourceUniqueID;
   }

   /**
    * Retrieve the target field.
    *
    * @return target field
    */
   public FieldType getTargetField()
   {
      return m_targetField;
   }

   /**
    * Set the target field.
    *
    * @param targetField target field
    */
   public void setTargetField(FieldType targetField)
   {
      m_targetField = targetField;
   }

   /**
    * Retrieve the unique ID of the target object.
    *
    * @return target object unique ID
    */
   public Integer getTargetUniqueID()
   {
      return m_targetUniqueID;
   }

   /**
    * Set the unique ID of the target object.
    *
    * @param targetUniqueID target object unique ID
    */
   public void setTargetUniqueID(Integer targetUniqueID)
   {
      m_targetUniqueID = targetUniqueID;
   }

   @Override public String toString()
   {
      StringBuilder sb = new StringBuilder();
      sb.append("[DataLink id=");
      sb.append(m_id);
      if (m_sourceField != null)
      {
         sb.append(" sourceField=");
         sb.append(m_sourceField.getFieldTypeClass());
         sb.append('.');
         sb.append(m_sourceField);
         sb.append(" sourceUniqueID=");
         sb.append(m_sourceUniqueID);
      }

      if (m_targetField != null)
      {
         sb.append(" targetField=");
         sb.append(m_targetField.getFieldTypeClass());
         sb.append('.');
         sb.append(m_targetField);
         sb.append(" targetUniqueID=");
         sb.append(m_targetUniqueID);
      }
      sb.append(']');

      return sb.toString();
   }

   private final String m_id;
   private FieldType m_sourceField;
   private Integer m_sourceUniqueID;
   private FieldType m_targetField;
   private Integer m_targetUniqueID;
}
