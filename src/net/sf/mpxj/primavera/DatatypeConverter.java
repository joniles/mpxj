/*
 * file:       DatatypeConverter.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2011
 * date:       08/08/2011
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

package net.sf.mpxj.primavera;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import net.sf.mpxj.ProjectFile;

/**
 * This class contains methods used to perform the datatype conversions
 * required to read and write PM files.
 */
public final class DatatypeConverter
{
   /**
    * Convert the Primavera string representation of a UUID into a Java UUID instance.
    *
    * @param value Primavera UUID
    * @return Java UUID instance
    */
   public static final UUID parseUUID(String value)
   {
      UUID result = null;
      if (value != null && !value.isEmpty())
      {
         if (value.charAt(0) == '{')
         {
            // PMXML representation: <GUID>{0AB9133E-A09A-9648-B98A-B2384894AC44}</GUID>
            result = UUID.fromString(value.substring(1, value.length() - 1));
         }
         else
         {
            // XER representation: CrkTPqCalki5irI4SJSsRA
            byte[] data = javax.xml.bind.DatatypeConverter.parseBase64Binary(value + "==");
            long msb = 0;
            long lsb = 0;

            for (int i = 0; i < 8; i++)
            {
               msb = (msb << 8) | (data[i] & 0xff);
            }

            for (int i = 8; i < 16; i++)
            {
               lsb = (lsb << 8) | (data[i] & 0xff);
            }

            result = new UUID(msb, lsb);
         }
      }
      return result;
   }

   /**
    * Retrieve a UUID in the form required by Primavera PMXML.
    *
    * @param guid UUID instance
    * @return formatted UUID
    */
   public static String printUUID(UUID guid)
   {
      return guid == null ? null : "{" + guid.toString().toUpperCase() + "}";
   }

   /**
    * Print a date time value.
    *
    * @param value date time value
    * @return string representation
    */
   public static final String printDateTime(Date value)
   {
      return (value == null ? null : getDateFormat().format(value));
   }

   /**
    * Parse a date time value.
    *
    * @param value string representation
    * @return date time value
    */
   public static final Date parseDateTime(String value)
   {
      Date result = null;

      if (value != null && value.length() != 0)
      {
         try
         {
            result = getDateFormat().parse(value);
         }

         catch (ParseException ex)
         {
            // Ignore parse exception
         }
      }

      return (result);
   }

   /**
    * Print a time value.
    *
    * @param value time value
    * @return time value
    */
   public static final String printTime(Date value)
   {
      return (value == null ? null : getTimeFormat().format(value));
   }

   /**
    * Parse a time value.
    *
    * @param value time value
    * @return time value
    */
   public static final Date parseTime(String value)
   {
      Date result = null;
      if (value != null && value.length() != 0)
      {
         try
         {
            result = getTimeFormat().parse(value);
         }

         catch (ParseException ex)
         {
            // Ignore this and return null
         }
      }
      return result;
   }

   /**
    * This method is called to set the parent file for the current
    * write operation. This allows task and resource write events
    * to be captured and passed to any file listeners.
    *
    * @param file parent file instance
    */
   public static final void setParentFile(ProjectFile file)
   {
      PARENT_FILE.set(file);
   }

   /**
    * Retrieve a date formatter.
    *
    * @return DateFormat instance
    */
   private static final DateFormat getDateFormat()
   {
      DateFormat df = DATE_FORMAT.get();
      if (df == null)
      {
         df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
         df.setLenient(false);
      }
      return (df);
   }

   /**
    * Retrieve a time formatter.
    *
    * @return DateFormat instance
    */
   private static final DateFormat getTimeFormat()
   {
      DateFormat df = TIME_FORMAT.get();
      if (df == null)
      {
         df = new SimpleDateFormat("HH:mm:ss");
         df.setLenient(false);
      }
      return (df);
   }

   private static final ThreadLocal<ProjectFile> PARENT_FILE = new ThreadLocal<ProjectFile>();
   private static final ThreadLocal<DateFormat> DATE_FORMAT = new ThreadLocal<DateFormat>();
   private static final ThreadLocal<DateFormat> TIME_FORMAT = new ThreadLocal<DateFormat>();
}
