/*
 * file:       Step.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software
 * date:       2023-01-24
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

import net.sf.mpxj.common.NumberHelper;

/**
 * Represents an activity step.
 */
public class Step
{
   /**
    * Constructor.
    *
    * @param task parent task
    */
   public Step(Task task)
   {
      m_task = task;
   }

   /**
    * Retrieve the parent task.
    *
    * @return parent task
    */
   public Task getTask()
   {
      return m_task;
   }

   /**
    * Retrieve the unique ID.
    *
    * @return unique ID
    */
   public Integer getUniqueID()
   {
      return m_uniqueID;
   }

   /**
    * Set the unique ID.
    *
    * @param uniqueID unique ID
    */
   public void setUniqueID(Integer uniqueID)
   {
      m_uniqueID = uniqueID;
   }

   /**
    * Retrieve the step name.
    *
    * @return step name
    */
   public String getName()
   {
      return m_name;
   }

   /**
    * Set the step name.
    *
    * @param name step name
    */
   public void setName(String name)
   {
      m_name = name;
   }

   /**
    * Retrieve the step percent complete.
    *
    * @return step percent complete
    */
   public Double getPercentComplete()
   {
      return m_percentComplete;
   }

   /**
    * Set the step percent complete.
    *
    * @param percentComplete percent complete
    */
   public void setPercentComplete(Double percentComplete)
   {
      m_percentComplete = percentComplete;
   }

   /**
    * Retrieve the step sequence number.
    *
    * @return step sequence number
    */
   public Integer getSequenceNumber()
   {
      return m_sequenceNumber;
   }

   /**
    * Set the step sequence number.
    *
    * @param sequenceNumber step sequence number
    */
   public void setSequenceNumber(Integer sequenceNumber)
   {
      m_sequenceNumber = sequenceNumber;
   }

   /**
    * Retrieve the step weight.
    *
    * @return step weight
    */
   public Double getWeight()
   {
      return m_weight;
   }

   /**
    * Set the step weight.
    *
    * @param weight step weight
    */
   public void setWeight(Double weight)
   {
      m_weight = weight;
   }

   /**
    * Retrieve the step description as plain text.
    *
    * @return step description
    */
   public String getDescription()
   {
      return m_description == null ? "" : m_description.toString();
   }

   /**
    * Retrieve the step description.
    *
    * @return step description
    */
   public Notes getDescriptionObject()
   {
      return m_description;
   }

   /**
    * Set the step description.
    *
    * @param notes step description
    */
   public void setDescriptionObject(Notes notes)
   {
      m_description = notes;
   }

   /**
    * Retrieve a flag indicating if the step is complete.
    *
    * @return true if the step is complete
    */
   public boolean getComplete()
   {
      return NumberHelper.getDouble(m_percentComplete) == 100.0;
   }

   private final Task m_task;
   private Integer m_uniqueID;
   private String m_name;
   private Double m_percentComplete;
   private Integer m_sequenceNumber;
   private Double m_weight;
   private Notes m_description;
}
