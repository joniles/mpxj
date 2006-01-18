/*
 * file:       Comments.java
 * author:     Scott Melville
 * copyright:  (c) Tapster Rock Limited 2002-2003
 * date:       15/08/2002
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

package com.tapsterrock.mpx;

/**
 * This class represents comments that appear in an MPX file.
 */
public final class Comments extends MPXRecord
{
   /**
    * Default constructor.
    *
    * @param file the parent file to which this record belongs.
    */
   Comments (ProjectFile file)
   {
      this (file, Record.EMPTY_RECORD);
   }

   /**
    * Constructor used to create an instance of this class from data
    * taken from an MPXFile record.
    *
    * @param file the MPXFile object to which this record belongs.
    * @param record record containing the data for  this object.
    */
   Comments (ProjectFile file, Record record)
   {
      super (file);
      m_text = record.getString(0);
   }

   /**
    * Set the text of the comment.
    *
    * @param comm - string comments
    */
   public void setComments (String comm)
   {
      m_text = comm;
   }

   /**
    * Retrieve the text of the comment.
    *
    * @return - string comments
    */
   public String getComments()
   {
      return (m_text);
   }

   /**
    * This method generates a string in MPX format representing the
    * contents of this record.
    *
    * @return string containing the data for this record in MPX format.
    */
   public String toString ()
   {
      StringBuffer buffer = new StringBuffer ();
      buffer.append (MPXConstants.COMMENTS_RECORD_NUMBER);
      buffer.append (getParentFile().getDelimiter());
      buffer.append (m_text);
      buffer.append (MPXConstants.EOL);
      return (buffer.toString());
   }


   /**
    * Text of comments.
    */
   private String m_text;

}
