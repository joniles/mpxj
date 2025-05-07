/*
 * file:       CustomField.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2002-2015
 * date:       28/04/2015
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

import java.util.ArrayList;
import java.util.List;

import org.mpxj.common.FieldTypeHelper;

/**
 * Configuration detail for a field.
 */
public class CustomField implements Comparable<CustomField>
{
   /**
    * Constructor.
    *
    * @param field field
    * @param parent parent container
    */
   public CustomField(FieldType field, CustomFieldContainer parent)
   {
      m_field = field;
      m_parent = parent;
      m_table = new CustomFieldLookupTable();
      m_indicator = new GraphicalIndicator(field);
      m_masks = new ArrayList<>();
   }

   /**
    * Retrieve the field type represented by this instance.
    *
    * @return field type
    */
   public FieldType getFieldType()
   {
      return m_field;
   }

   /**
    * Retrieve the value lookup table associated with this field.
    *
    * @return value lookup table
    */
   public CustomFieldLookupTable getLookupTable()
   {
      return m_table;
   }

   /**
    * Retrieve the graphical indicator configuration for this field.
    *
    * @return graphical indicator configuration
    */
   public GraphicalIndicator getGraphicalIndicator()
   {
      return m_indicator;
   }

   /**
    * Retrieve the alias for this field.
    *
    * @return field alias
    */
   public String getAlias()
   {
      return m_alias;
   }

   /**
    * Set the alias for this field.
    *
    * @param alias field alias
    * @return this to allow method chaining
    */
   public CustomField setAlias(String alias)
   {
      m_alias = alias;
      m_parent.registerAlias(m_field, alias);
      return this;
   }

   /**
    * Retrieve the Unique ID for this field.
    * If this value is not set explicitly,
    * it defaults to the field ID used by
    * Microsoft Project.
    *
    * @return Unique ID
    */
   public Integer getUniqueID()
   {
      if (m_uniqueID == null)
      {
         m_uniqueID = Integer.valueOf(FieldTypeHelper.getFieldID(m_field));
      }
      return m_uniqueID;
   }

   /**
    * Set the Unique ID for this field.
    *
    * @param uniqueID Unique ID
    * @return this to allow method chaining
    */
   public CustomField setUniqueID(Integer uniqueID)
   {
      m_uniqueID = uniqueID;
      return this;
   }

   /**
    * Retrieve the mask definitions for this field.
    *
    * @return list of mask definitions
    */
   public List<CustomFieldValueMask> getMasks()
   {
      return m_masks;
   }

   @Override public int compareTo(CustomField f)
   {
      String name1 = getFieldType().getFieldTypeClass().name() + "." + getUniqueID() + "." + getAlias();
      String name2 = f.getFieldType().getFieldTypeClass().name() + "." + f.getUniqueID() + "." + f.getAlias();
      return name1.compareTo(name2);
   }

   @Override public String toString()
   {
      return "[CustomField field=" + m_field + " alias=" + m_alias + "]";
   }

   private final FieldType m_field;
   private final CustomFieldContainer m_parent;
   private final CustomFieldLookupTable m_table;
   private final GraphicalIndicator m_indicator;
   private final List<CustomFieldValueMask> m_masks;
   private Integer m_uniqueID;
   private String m_alias;
}
