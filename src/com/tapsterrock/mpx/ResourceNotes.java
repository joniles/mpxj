/*
 * file:       ResourceNotes.java
 * author:     Scott Melville
 *             Jon Iles
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
 * This class represents the resource notes record in an MPX file.
 */
public class ResourceNotes extends MPXRecord
{
   /**
    * Default constructor.
    *
    * @param file the parent file to which this record belongs.
    */
   ResourceNotes (MPXFile file)
      throws MPXException
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
   ResourceNotes (MPXFile file, Record record)
   {
      super (file);
      m_note = record.getString(0);
   }

   /**
    * Get any text notes attached to this Resource
    *
    * @return notes
    */
   public String getNotes ()
   {
      return (m_note);
   }

   /**
    * Set any text notes attached to this Resource
    * @param notes atached notes
    */
   public void setNotes (String notes)
   {
      m_note = notes;
   }

   /**
    * This method generates a string in MPX format representing the
    * contents of this record.
    *
    * @return string containing the data for this record in MPX format.
    */
   public String toString()
   {
      StringBuffer buffer = new StringBuffer ();

      buffer.append (RECORD_NUMBER);
      buffer.append (getParentFile().getDelimiter());
      buffer.append (m_note);
      buffer.append (MPXFile.EOL);

      return (buffer.toString());
   }

   /**
   * The text constituting this <tt>ResourceNote</tt>
   */
   private String m_note;

   /**
    * Constant containing the record number associated with this record.
    */
   public static final int RECORD_NUMBER = 51;
}
