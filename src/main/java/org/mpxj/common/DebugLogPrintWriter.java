/*
 * file:       DebugLogPrintWriter.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2020
 * date:       2020-05-29
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

package org.mpxj.common;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Utility method for creating a PrintWriter instance for debug output.
 */
public final class DebugLogPrintWriter
{
   /**
    * If the MPXJ_DEBUG_LOG environment variable is set, create a
    * PrintWriter to write to the specified file. A new file is created each time.
    *
    * @return PrintWriter instance
    */
   public static final PrintWriter getInstance()
   {
      return getInstance(false);
   }

   /**
    * If the MPXJ_DEBUG_LOG environment variable is set, create a
    * PrintWriter to write to the specified file.
    *
    * @param append append to an existing file if true
    * @return PrintWriter instance
    */
   public static final PrintWriter getInstance(boolean append)
   {
      PrintWriter result;
      String logFile = System.getenv("MPXJ_DEBUG_LOG");
      if (logFile == null)
      {
         result = null;
      }
      else
      {
         try
         {
            result = new PrintWriter(new FileWriter(logFile, append));
            System.out.println("DebugLogger Configured");
         }
         catch (IOException e)
         {
            result = null;
         }
      }
      return result;
   }
}
