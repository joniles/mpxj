/*
 * file:       SynchroLogger.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2018
 * date:       2018-10-11
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

package org.mpxj.synchro;

import java.io.PrintWriter;
import java.util.Map;

import org.mpxj.common.ByteArrayHelper;
import org.mpxj.common.DebugLogPrintWriter;

/**
 * Provides optional logging to assist with development.
 * Disabled unless a log file is specified.
 */
final class SynchroLogger
{
   /**
    * Private constructor to avoid instantiation.
    */
   private SynchroLogger()
   {

   }

   /**
    * Open the log file for writing.
    */
   public static void openLogFile()
   {
      LOG = DebugLogPrintWriter.getInstance();
   }

   /**
    * Close the log file.
    */
   public static void closeLogFile()
   {
      if (LOG != null)
      {
         LOG.flush();
         LOG.close();
      }
   }

   /**
    * Log a byte array.
    *
    * @param label label text
    * @param data byte array
    */
   public static void log(String label, byte[] data)
   {
      if (LOG != null)
      {
         LOG.write(label);
         LOG.write(": ");
         LOG.println(ByteArrayHelper.hexdump(data, true));
         LOG.flush();
      }
   }

   /**
    * Log a string.
    *
    * @param label label text
    * @param data string data
    */
   public static void log(String label, String data)
   {
      if (LOG != null)
      {
         LOG.write(label);
         LOG.write(": ");
         LOG.println(data);
         LOG.flush();
      }
   }

   /**
    * Log a table header.
    *
    * @param label label text
    * @param data table header
    */
   public static void log(String label, SynchroTable data)
   {
      if (LOG != null)
      {
         LOG.write(label);
         LOG.write(": ");
         LOG.println(data.toString());
         LOG.flush();
      }
   }

   /**
    * Log a byte array as a hex dump.
    *
    * @param data byte array
    */
   public static void log(byte[] data)
   {
      if (LOG != null)
      {
         LOG.println(ByteArrayHelper.hexdump(data, true, 16, ""));
         LOG.flush();
      }
   }

   /**
    * Log table contents.
    *
    * @param label label text
    * @param klass reader class name
    * @param map table data
    */
   public static void log(String label, Class<?> klass, Map<String, Object> map)
   {
      if (LOG != null)
      {
         LOG.write(label);
         LOG.write(": ");
         LOG.println(klass.getSimpleName());

         for (Map.Entry<String, Object> entry : map.entrySet())
         {
            LOG.println(entry.getKey() + ": " + entry.getValue());
         }
         LOG.println();
         LOG.flush();
      }
   }

   private static PrintWriter LOG;
}
