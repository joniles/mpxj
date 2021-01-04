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
    * @param topicID topic ID
    * @param topicName topic name
    * @param note Notes instance
    */
   public StructuredNotes(Integer topicID, String topicName, Notes note)
   {
      super(StructuredNotes.getStructuredText(topicName, note));
      m_topicID = topicID;
      m_topicName = topicName;
   }

   /**
    * Retrieve this note's topic ID.
    * 
    * @return topic ID
    */
   public Integer getTopicID()
   {
      return m_topicID;
   }

   /**
    * Retrieve this note's topic name.
    * 
    * @return topic name
    */
   public String getTopicName()
   {
      return m_topicName;
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
         StringBuilder sb = new StringBuilder();
         sb.append(topicName);
         sb.append("\n");
         sb.append(text);
         sb.append("\n");
         result = sb.toString();
      }

      return result;
   }

   private final Integer m_topicID;
   private final String m_topicName;
}
