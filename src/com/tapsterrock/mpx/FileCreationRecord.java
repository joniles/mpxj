/*
 * file:       FileCreationRecord.java
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

import java.util.Locale;

/**
 * This class represents the first record to appear in an MPX file. This record
 * identifies the file type, version number, originating software and the
 * separator to be used in the remainder of the file.
 */
public final class FileCreationRecord extends MPXRecord
{
   /**
    * Default constructor.
    *
    * @param file the parent file to which this record belongs.
    */
   FileCreationRecord (MPXFile file)
   {
      super (file, 0);

      setLocale (file.getLocale());
   }

   /**
    * This method is calkled when the locale of the parent file is updated.
    * It resets the locale specific currency attributes to the default values
    * for the new locale.
    *
    * @param locale new locale
    */
   void setLocale (Locale locale)
   {
      setDelimiter(LocaleData.getChar(locale, LocaleData.FILE_DELIMITER));
      setProgramName(LocaleData.getString(locale, LocaleData.PROGRAM_NAME));
      setFileVersion(LocaleData.getString(locale, LocaleData.FILE_VERSION));
      setCodePage(LocaleData.getString(locale, LocaleData.CODE_PAGE));
   }

   /**
    * This method allows the default file creation record to be updated
    * with values read from an MPX file.
    *
    * @param record record containing the data for this object.
    */
   void setValues (Record record)
   {
      m_programName = record.getString(0);
      m_fileVersion = record.getString(1);
      m_codePage = record.getString(2);
   }

   /**
    * Sets the delimiter character, "," by default
    *
    * @param delimiter delimiter character
    */
   public void setDelimiter (char delimiter)
   {
      m_delimiter = delimiter;
      getParentFile().setDelimiter (m_delimiter);
   }

   /**
    * Retrieves the delimiter character, "," by default
    *
    * @return delimiter character
    */
   public char getDelimiter()
   {
      return (m_delimiter);
   }

   /**
    * Program name file created by
    *
    * @param programName system name
    */
   public void setProgramName (String programName)
   {
      m_programName = programName;
   }

   /**
    * Program name file created by
    *
    * @return program name
    */
   public String getProgramName ()
   {
      return (m_programName);
   }

   /**
    * Version of the MPX file
    *
    * @param version MPX file version
    */
   public void setFileVersion (String version)
   {
      m_fileVersion = version;
   }

   /**
    * Version of the MPX file
    *
    * @return MPX file version
    */
   public String getFileVersion ()
   {
      return (m_fileVersion);
   }

   /**
    * Code page, for example: 850, 437, MAC, ANSI
    *
    * @param codePage code page type
    */
   public void setCodePage (String codePage)
   {
      m_codePage = codePage;
   }

   /**
    * Code page, for example: 850, 437, MAC, ANSI
    *
    * @return code page type
    */
   public String getCodePage ()
   {
      return (m_codePage);
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

      buffer.append ("MPX");
      buffer.append (delimiter);
      buffer.append (m_programName);
      buffer.append (delimiter);
      buffer.append (m_fileVersion);
      buffer.append (delimiter);
      buffer.append (m_codePage);
      buffer.append (MPXFile.EOL);

      return (buffer.toString());
   }


   /**
    * The character to be used throughout as a delimiter for MPX files.
    */
   private char m_delimiter;

   /**
    * The program and version number used to create the file
    */
   private String m_programName;

   /**
    * The version number of the MPX file format used in the file
    */
   private String m_fileVersion;

   /**
    * The code page used to create the file
    * eg (850,437,MAC,ANSI)
    */
   private String m_codePage;

   /**
    * Constant containing the record number associated with this record.
    * Note that in this case it is a dummy value, the actual value used
    * in the file is MPX. The dummy value is used to allow all record types
    * to be identified numerically.
    */
   static final int RECORD_NUMBER = 999;
}

