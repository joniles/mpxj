/*
 * file:       ActivityCode.java
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Activity code type definition, contains a list of the valid
 * values for this activity code.
 */
public class ActivityCode
{
   /**
    * Constructor.
    *
    * @param parentFile parent file
    * @param uniqueID activity code unique ID
    * @param scope activity code scope
    * @param scopeUniqueID scope object unique ID
    * @param sequenceNumber sequence number
    * @param name activity code name
    * @param secure secure flag
    * @param maxLength max length
    */
   public ActivityCode(ProjectFile parentFile, Integer uniqueID, ActivityCodeScope scope, Integer scopeUniqueID, Integer sequenceNumber, String name, boolean secure, Integer maxLength)
   {
      m_parentFile = parentFile;
      m_uniqueID = uniqueID;
      m_scope = scope;
      m_scopeUniqueID = scopeUniqueID;
      m_sequenceNumber = sequenceNumber;
      m_name = name;
      m_secure = secure;
      m_maxLength = maxLength;
   }

   /**
    * Retrieve the parent file.
    *
    * @return ProjectFile instance
    */
   public ProjectFile getParentFile()
   {
      return m_parentFile;
   }

   /**
    * Retrieve the activity code unique ID.
    *
    * @return unique ID
    */
   public Integer getUniqueID()
   {
      return m_uniqueID;
   }

   /**
    * Retrieve the scope of this activity code.
    *
    * @return activity code scope
    */
   public ActivityCodeScope getScope()
   {
      return m_scope;
   }

   /**
    * Returns the ID of the scope to which this activity code
    * belongs. This will be {@code null} if the scope is
    * Global. If the scope if Project, this value will be the
    * project ID. Finally if the scope is EPS this will be
    * the ID of the EPS object.
    *
    * @return scope ID
    */
   public Integer getScopeUniqueID()
   {
      return m_scopeUniqueID;
   }

   /**
    * Retrieve the sequence number of this activity code.
    *
    * @return sequence number
    */
   public Integer getSequenceNumber()
   {
      return m_sequenceNumber;
   }

   /**
    * Retrieve the activity code name.
    *
    * @return name
    */
   public String getName()
   {
      return m_name;
   }

   /**
    * Retrieve the secure flag.
    *
    * @return secure flag
    */
   public boolean getSecure()
   {
      return m_secure;
   }

   /**
    * Retrieve the max length.
    *
    * @return max length
    */
   public Integer getMaxLength()
   {
      return m_maxLength;
   }

   /**
    * Add a value to this activity code.
    *
    * @param uniqueID value unique ID
    * @param sequenceNumber value sequence number
    * @param name value name
    * @param description value description
    * @param color value color
    * @return ActivityCodeValue instance
    */
   public ActivityCodeValue addValue(Integer uniqueID, Integer sequenceNumber, String name, String description, Color color)
   {
      ActivityCodeValue value = new ActivityCodeValue(this, uniqueID, sequenceNumber, name, description, color);
      m_values.add(value);
      return value;
   }

   /**
    * Retrieve a list of all values for this activity code,
    * including child values from the hierarchy.
    *
    * @return list of ActivityCodeValue instances
    */
   public List<ActivityCodeValue> getValues()
   {
      return m_values;
   }

   /**
    * Retrieve a list of top level values for his activity code.
    * This excludes any child values from further down the
    * hierarchy of values.
    *
    * @return list of ActivityCodeValue instances
    */
   public List<ActivityCodeValue> getChildValues()
   {
      return m_values.stream().filter(v -> v.getParent() == null).collect(Collectors.toList());
   }

   private final ProjectFile m_parentFile;
   private final Integer m_uniqueID;
   private final ActivityCodeScope m_scope;
   private final Integer m_scopeUniqueID;
   private final Integer m_sequenceNumber;
   private final String m_name;
   private final boolean m_secure;
   private final Integer m_maxLength;
   private final List<ActivityCodeValue> m_values = new ArrayList<>();
}
