/*
 * file:       ActivityCodeValue.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2018
 * date:       18/06/2018
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

import java.awt.Color;

/**
 * Represents an individual activity code value.
 */
public class ActivityCodeValue extends ProjectEntity implements ProjectEntityWithUniqueID
{
   public ActivityCodeValue(ProjectFile file, ActivityCode type) {
      super(file);

      ProjectConfig config = file.getProjectConfig();

      if (config.getAutoActivityCodeValueUniqueID())
      {
         setUniqueID(Integer.valueOf(config.getNextActivityCodeValueUniqueID()));
      }

      m_type = type;
   }

   /**
    * Retrieves the parent activity code type.
    *
    * @return ActivityCode instance
    */
   public ActivityCode getType()
   {
      return m_type;
   }

   /**
    * Retrieves the unique ID for this value.
    *
    * @return unique ID
    */
   public Integer getUniqueID()
   {
      return m_uniqueID;
   }

   public void setUniqueID(Integer uniqueID)
   {
      m_uniqueID = uniqueID;
   }

   /**
    * Retrieves the sequence number for this value.
    *
    * @return sequence number
    */
   public Integer getSequenceNumber()
   {
      return m_sequenceNumber;
   }

   public void setSequenceNumber(Integer sequenceNumber)
   {
      m_sequenceNumber = sequenceNumber;
   }

   /**
    * Retrieves the value name.
    *
    * @return value name
    */
   public String getName()
   {
      return m_name;
   }

   public void setName(String name)
   {
      m_name = name;
   }

   /**
    * Retrieves the value description.
    *
    * @return value description
    */
   public String getDescription()
   {
      return m_description;
   }

   public void setDescription(String description)
   {
      m_description = description;
   }

   /**
    * Retrieves the color associated with this value.
    *
    * @return Color instance
    */
   public Color getColor()
   {
      return m_color;
   }

   public void setColor(Color color)
   {
      m_color = color;
   }

   /**
    * Retrieve the parent ActivityCodeValue.
    *
    * @return parent ActivityCodeValue
    */
   public ActivityCodeValue getParent()
   {
      return m_parent;
   }

   /**
    * Set the parent ActivityCodeValue.
    *
    * @param parent parent ActivityCodeValue
    */
   public void setParent(ActivityCodeValue parent)
   {
      m_parent = parent;
   }

   @Override public String toString()
   {
      return m_type.getName() + ": " + m_name;
   }

   private final ActivityCode m_type;
   private Integer m_uniqueID;
   private Integer m_sequenceNumber;
   private String m_name;
   private String m_description;
   private Color m_color;
   private ActivityCodeValue m_parent;
}
