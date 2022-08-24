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

import net.sf.mpxj.common.NumberHelper;

/**
 * Activity code type definition, contains a list of the valid
 * values for this activity code.
 */
public class ActivityCode extends ProjectEntity implements Comparable<ActivityCode>, ProjectEntityWithUniqueID
{
   public ActivityCode(ProjectFile file) {
      super(file);

      ProjectConfig config = file.getProjectConfig();

      if (config.getAutoActivityCodeUniqueID())
      {
         setUniqueID(Integer.valueOf(config.getNextActivityCodeUniqueID()));
      }
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

   public void setUniqueID(Integer uniqueID)
   {
      m_uniqueID = uniqueID;
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

   public void setScope(ActivityCodeScope scope)
   {
      m_scope = scope;
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

   public void setScopeUniqueId(Integer scopeUniqueID)
   {
      m_scopeUniqueID = scopeUniqueID;
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

   public void setSequenceNumber(Integer sequenceNumber)
   {
      m_sequenceNumber = sequenceNumber;
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

   public void setName(String name)
   {
      m_name = name;
   }

   /**
    * Add a value to this activity code.
    * @return ActivityCodeValue instance
    */
   public ActivityCodeValue addValue()
   {
      ActivityCodeValue value = new ActivityCodeValue(getParentFile(), this);
      m_values.add(value);
      return value;
   }

   /**
    * Retrieve a list of values for this activity code.
    *
    * @return list of ActivityCodeValue instances
    */
   public List<ActivityCodeValue> getValues()
   {
      return m_values;
   }

   @Override public int compareTo(ActivityCode o)
   {
      int id1 = NumberHelper.getInt(getUniqueID());
      int id2 = NumberHelper.getInt(o.getUniqueID());
      return (Integer.compare(id1, id2));
   }

   private Integer m_uniqueID;
   private ActivityCodeScope m_scope;
   private Integer m_scopeUniqueID;
   private Integer m_sequenceNumber;
   private String m_name;
   private final List<ActivityCodeValue> m_values = new ArrayList<>();
}
