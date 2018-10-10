
package net.sf.mpxj.synchro;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import net.sf.mpxj.common.ByteArrayHelper;

final class SynchroLogger
{
   private SynchroLogger()
   {

   }

   /**
    * Provide the file path for rudimentary logging to support development.
    *
    * @param logFile full path to log file
    */
   public static void setLogFile(String logFile)
   {
      LOG_FILE = logFile;
   }

   /**
    * Open the log file for writing.
    */
   public static void openLogFile() throws IOException
   {
      if (LOG_FILE != null)
      {
         LOG = new PrintWriter(new FileWriter(LOG_FILE));
      }
   }

   /**
    * Close the log file.
    */
   public static void closeLogFile()
   {
      if (LOG_FILE != null)
      {
         LOG.flush();
         LOG.close();
      }
   }

   public static void log(String label, byte[] data)
   {
      if (LOG != null)
      {
         LOG.write(label);
         LOG.write(": ");
         LOG.println(ByteArrayHelper.hexdump(data, true));
      }
   }

   public static void log(String label, String data)
   {
      if (LOG != null)
      {
         LOG.write(label);
         LOG.write(": ");
         LOG.println(data);
      }
   }

   public static void log(String label, SynchroTable data)
   {
      if (LOG != null)
      {
         LOG.write(label);
         LOG.write(": ");
         LOG.println(data.toString());
      }
   }

   public static void log(byte[] data)
   {
      if (LOG != null)
      {
         LOG.println(ByteArrayHelper.hexdump(data, true, 16, ""));
      }
   }

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
      }
   }

   private static String LOG_FILE;
   private static PrintWriter LOG;
}
