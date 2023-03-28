/*
 * file:       StructuredNotes.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2021
 * date:       2021-01-03
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
 * Represents a note which belongs to a topic.
 */
public class StructuredNotes extends Notes
{
   /**
    * Constructor.
    *
    * @param uniqueID unique ID
    * @param topic notes topic
    * @param notes Notes instance
    */
   public StructuredNotes(Integer uniqueID, NotesTopic topic, Notes notes)
   {
      super(StructuredNotes.getStructuredText(topic.getName(), notes));
      m_uniqueID = uniqueID;
      m_topic = topic;
      m_notes = notes;
   }

   /**
    * Retrieve the unique ID of these notes.
    *
    * @return unique ID
    */
   public Integer getUniqueID()
   {
      return m_uniqueID;
   }

   /**
    * Retrieve this note's topic ID.
    *
    * @return topic ID
    */
   public Integer getTopicID()
   {
      return m_topic.getUniqueID();
   }

   /**
    * Retrieve this note's topic.
    *
    * @return topic name
    */
   public NotesTopic getTopicName()
   {
      return m_topic;
   }

   /**
    * Retrieve the notes.
    *
    * @return Notes instance
    */
   public Notes getNotes()
   {
      return m_notes;
   }

   /**
    * Create a plain text version of this note which includes the topic and the text.
    *
    * @param topicName topic name
    * @param note Notes instance
    * @return plain text note
    */
   private static String getStructuredText(String topicName, Notes note)
   {
      String result;

      String text = note == null ? null : note.toString();
      if (text == null || text.isEmpty())
      {
         result = null;
      }
      else
      {
         result = topicName + "\n" + text + "\n";
      }

      return result;
   }

   private final Integer m_uniqueID;
   private final NotesTopic m_topic;
   private final Notes m_notes;
}
