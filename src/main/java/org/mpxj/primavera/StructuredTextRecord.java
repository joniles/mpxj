/*
 * file:       StructuredTextRecord.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2022
 * date:       06/02/2022
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

package org.mpxj.primavera;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Primavera makes use of "structured text records" in a number of places to represent
 * complex data as a simple text field. The fields use bracket and pipe characters
 * as delimiters. The typical structure starts with a record number, optionally
 * followed by a record name. After this is a set of name-value pairs (which can be empty), then
 * finally a list of child records following the same format (which again can be empty).
 */
public class StructuredTextRecord
{
   /**
    * Constructor to create an empty record.
    */
   public StructuredTextRecord()
   {
      m_attributes = new LinkedHashMap<>();
      m_children = new ArrayList<>();
      m_childrenByName = Collections.emptyMap();
   }

   /**
    * Constructor.
    *
    * @param attributes attributes of this record
    * @param children child records
    */
   public StructuredTextRecord(Map<String, String> attributes, List<StructuredTextRecord> children)
   {
      m_attributes = attributes;
      m_children = children;
      m_childrenByName = m_children.stream().filter(r -> r.getRecordName() != null).collect(Collectors.toMap(StructuredTextRecord::getRecordName, r -> r, (r1, r2) -> r1));
   }

   /**
    * Retrieve the record name. The record name is optional so this may be null.
    *
    * @return record name
    */
   public String getRecordName()
   {
      return m_attributes.get(RECORD_NAME_ATTRIBUTE);
   }

   /**
    * Retrieve the record number.
    *
    * @return record number
    */
   public String getRecordNumber()
   {
      return m_attributes.get(RECORD_NUMBER_ATTRIBUTE);
   }

   /**
    * Retrieve an attribute by name.
    *
    * @param name attribute name
    * @return attribute value
    */
   public String getAttribute(String name)
   {
      return m_attributes.get(name);
   }

   /**
    * Retrieve all attributes.
    *
    * @return Map containing all attributes of this record
    */
   public Map<String, String> getAttributes()
   {
      return m_attributes;
   }

   /**
    * Retrieve all child records.
    *
    * @return List containing all child records
    */
   public List<StructuredTextRecord> getChildren()
   {
      return m_children;
   }

   /**
    * Retrieve a child record by name. Note that not all
    * Structured Text Records in Primavera have a unique
    * name for each child record. In some cases the caller is simply
    * expected to retrieve the list of child records and
    * iterate to process. Where child records have non-unique names,
    * the first record encountered in the list with a matching name
    * will be returned by this method.
    *
    * @param name child record name
    * @return child record
    */
   public StructuredTextRecord getChild(String name)
   {
      return m_childrenByName.get(name);
   }

   /**
    * Add an attribute to the record.
    *
    * @param name attribute name
    * @param value attribute value
    */
   public void addAttribute(String name, String value)
   {
      m_attributes.put(name, value);
   }

   /**
    * Add a child to the record.
    *
    * @param child StructuredTextRecord instance
    */
   public void addChild(StructuredTextRecord child)
   {
      m_children.add(child);
   }

   private final Map<String, String> m_attributes;
   private final List<StructuredTextRecord> m_children;
   private final Map<String, StructuredTextRecord> m_childrenByName;

   public static final String RECORD_NUMBER_ATTRIBUTE = "_record_number";
   public static final String RECORD_NAME_ATTRIBUTE = "_record_name";
   public static final StructuredTextRecord EMPTY = new StructuredTextRecord(Collections.emptyMap(), Collections.emptyList());
}
