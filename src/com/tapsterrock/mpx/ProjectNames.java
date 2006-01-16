/*
 * file:       ProjectNames.java
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
 * This class represents the Project Names record as found in an MPX file.
 * This record lists all of the DDE link names stored in a Microsoft Project
 * file.
 */
public final class ProjectNames extends MPXRecord
{
   /**
    * Default constructor.
    *
    * @param file the parent file to which this record belongs.
    */
   ProjectNames (ProjectFile file)
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
   ProjectNames (ProjectFile file, Record record)
   {
      super(file);
      m_name = record.getString(0);
      m_description = record.getString(1);
   }

   /**
    * Sets the DDE link name.
    *
    * @param in DDE link name
    */
   public void setName (String in)
   {
      m_name = in;
   }

   /**
    * Gets the DDE link name.
    *
    * @return DDE link name
    */
   public String getName ()
   {
      return (m_name);
   }

   /**
    * Set description.
    *
    * @param desc descrpition
    */
   public void setDescription (String desc)
   {
      m_description = desc;
   }

   /**
    * Get description.
    *
    * @return descrpition
    */
   public String getDescription ()
   {
      return (m_description);
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
      char delimiter = getParentFile().getDelimiter();

      buffer.append (RECORD_NUMBER);
      buffer.append (delimiter);
      buffer.append (m_name);
      buffer.append (delimiter);
      buffer.append (m_description);
      buffer.append (ProjectFile.EOL);

      return (buffer.toString());
   }

   /**
    * DDE Link name.
    */
   private String m_name;

   /**
    * Description.
    */
   private String m_description;

   /**
    * Constant containing the record number associated with this record.
    */
   static final int RECORD_NUMBER = 80;
}
