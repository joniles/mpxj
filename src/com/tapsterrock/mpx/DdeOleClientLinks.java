/*
 * file:       DdeOleClientLinks.java
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
 * This class represents the record in an MPX file that holds details of any
 * DDE links that have been made into a Microsoft Project file.
 */
public class DdeOleClientLinks extends MPXRecord
{
   /**
    * Default constructor.
    *
    * @param file the parent file to which this record belongs.
    */
   DdeOleClientLinks (MPXFile file)
   {
      super(file);
   }

   /**
    * Constructor used to create an instance of this class from data
    * taken from an MPXFile record.
    *
    * @param file the MPXFile object to which this record belongs.
    * @param record record containing the data for  this object.
    */
   DdeOleClientLinks(MPXFile file, Record record)
   {
      super (file);
      m_source = record.getString(0);
      m_linkTo = record.getString(1);
   }

   /**
    * Sets the source field
    *
    * @param src - the source
    */
   public void setSource (String src)
   {
      m_source = src;
   }

   /**
    * Gets the source value
    *
    * @return - string source
    */
   public String getSource()
   {
      return (m_source);
   }

   /**
    * Sets the Link To field
    *
    * @param link - target
    */
   public void setLinkTo (String link)
   {
      m_linkTo = link;
   }

   /**
    * Gets the link to target
    *
    * @return - target
    */
   public String getLinkTo ()
   {
      return (m_linkTo);
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
      char delimiter = getParentFile().getDelimiter();

      buffer.append (RECORD_NUMBER);
      buffer.append (delimiter);
      buffer.append (m_source);
      buffer.append (delimiter);
      buffer.append (m_linkTo);
      buffer.append (MPXFile.EOL);

      return (buffer.toString());
   }


   /**
    * Address of source.
    */
   private String m_source;

   /**
    * The reference to be linked.
    */
   private String m_linkTo;

   /**
    * Constant containing the record number associated with this record.
    */
   public static final int RECORD_NUMBER = 81;
}
