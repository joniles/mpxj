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
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents an individual activity code value.
 */
public class ActivityCodeValue
{
   /**
    * Constructor.
    *
    * @param type parent activity code type
    * @param uniqueID unique ID
    * @param sequenceNumber value sequence number
    * @param name value name
    * @param description value description
    * @param color value color
    * @deprecated use builder
    */
   @Deprecated public ActivityCodeValue(ActivityCode type, Integer uniqueID, Integer sequenceNumber, String name, String description, Color color)
   {
      m_type = type;
      m_uniqueID = uniqueID;
      m_sequenceNumber = sequenceNumber;
      m_name = name;
      m_description = description;
      m_color = color;
   }

   /**
    * Constructor.
    *
    * @param builder builder
    */
   private ActivityCodeValue(Builder builder)
   {
      m_uniqueID = builder.m_file.getUniqueIdObjectSequence(ActivityCodeValue.class).syncOrGetNext(builder.m_uniqueID);
      m_type = builder.m_type;
      m_sequenceNumber = builder.m_sequenceNumber;
      m_name = builder.m_name;
      m_description = builder.m_description;
      m_color = builder.m_color;
      m_parent = builder.m_parent;
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

   /**
    * Retrieves the sequence number for this value.
    *
    * @return sequence number
    */
   public Integer getSequenceNumber()
   {
      return m_sequenceNumber;
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

   /**
    * Retrieves the value description.
    *
    * @return value description
    */
   public String getDescription()
   {
      return m_description;
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
    * Retrieve the parent ActivityCodeValue unique ID.
    *
    * @return parent ActivityCodeValue unique ID
    */
   public Integer getParentUniqueID()
   {
      return m_parent == null ? null : m_parent.getUniqueID();
   }

   /**
    * Set the parent ActivityCodeValue.
    *
    * @param parent parent ActivityCodeValue
    * @deprecated use builder
    */
   @Deprecated public void setParent(ActivityCodeValue parent)
   {
      m_parent = parent;
   }

   /**
    * Retrieve any children of this value.
    *
    * @return list of ActivityCodeValue instances
    */
   public List<ActivityCodeValue> getChildValues()
   {
      return m_type.getValues().stream().filter(a -> a.m_parent == this).collect(Collectors.toList());
   }

   @Override public String toString()
   {
      return m_type.getName() + ": " + m_name;
   }

   private final ActivityCode m_type;
   private final Integer m_uniqueID;
   private final Integer m_sequenceNumber;
   private final String m_name;
   private final String m_description;
   private final Color m_color;
   private ActivityCodeValue m_parent;

   /**
    * ActivityCodeValue builder.
    */
   public static class Builder
   {
      /**
       * Constructor.
       *
       * @param file parent project file
       */
      public Builder(ProjectFile file)
      {
         m_file = file;
      }

      /**
       * Add parent activity code.
       *
       * @param value activity code
       * @return builder
       */
      public Builder type(ActivityCode value)
      {
         m_type = value;
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
       */
      public Builder description(String value)
      {
         m_description = value;
         return this;
      }

      /**
       * Add color.
       *
       * @param value color
       * @return builder
       */
      public Builder color(Color value)
      {
         m_color = value;
         return this;
      }

      /**
       * Add parent value.
       *
       * @param value parent value
       * @return builder
       */
      public Builder parent(ActivityCodeValue value)
      {
         m_parent = value;
         return this;
      }

      /**
       * Build an ActivityCodeValue instance.
       *
       * @return ActivityCodeValue instance
       */
      public ActivityCodeValue build()
      {
         return new ActivityCodeValue(this);
      }

      private final ProjectFile m_file;
      private ActivityCode m_type;
      private Integer m_uniqueID;
      private Integer m_sequenceNumber;
      private String m_name;
      private String m_description;
      private Color m_color;
      private ActivityCodeValue m_parent;
   }
}