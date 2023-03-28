/*
 * file:       NotesTopic.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software
 * date:       2023-03-27
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
 * Represents a topic, used by P6 to organise notes.
 */
public class NotesTopic implements ProjectEntityWithUniqueID
{
   /**
    * Constructor.
    *
    * @param uniqueID unique ID
    * @param sequenceNumber sequence number
    * @param name name
    * @param availableForEPS available for EPS flag
    * @param availableForProject available for project flag
    * @param availableForWBS available for wbs flag
    * @param availableForActivity available for activity flag
    */
   public NotesTopic(Integer uniqueID, Integer sequenceNumber, String name, boolean availableForEPS, boolean availableForProject, boolean availableForWBS, boolean availableForActivity)
   {
      m_uniqueID = uniqueID;
      m_sequenceNumber = sequenceNumber;
      m_name = name;
      m_availableForEPS = availableForEPS;
      m_availableForProject = availableForProject;
      m_availableForWBS = availableForWBS;
      m_availableForActivity = availableForActivity;
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
    * Retrieve the sequence number.
    *
    * @return sequence number
    */
   public Integer getSequenceNumber()
   {
      return m_sequenceNumber;
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
    * Retrieve the available for EPS flag.
    *
    * @return available for EPS flag
    */
   public boolean getAvailableForEPS()
   {
      return m_availableForEPS;
   }

   /**
    * Retrieve the available for project flag.
    *
    * @return available for project flag
    */
   public boolean getAvailableForProject()
   {
      return m_availableForProject;
   }

   /**
    * Retrieve the available for WBS flag.
    *
    * @return available for WBS flag
    */
   public boolean getAvailableForWBS()
   {
      return m_availableForWBS;
   }

   /**
    * Retrieve the available for activity flag.
    *
    * @return available for activity flag
    */
   public boolean getAvailableForActivity()
   {
      return m_availableForActivity;
   }

   private final Integer m_uniqueID;
   private final Integer m_sequenceNumber;
   private final String m_name;
   private final boolean m_availableForEPS;
   private final boolean m_availableForProject;
   private final boolean m_availableForWBS;
   private final boolean m_availableForActivity;

   public static final NotesTopic DEFAULT = new NotesTopic(Integer.valueOf(1), Integer.valueOf(1), "Notes", true, true, true, true);
}
