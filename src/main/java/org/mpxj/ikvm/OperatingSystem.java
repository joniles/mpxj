/*
 * file:       OperatingSystem.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2021
 * date:       2021-11-24
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

package org.mpxj.ikvm;

import java.io.File;
import java.nio.file.Files;
import java.util.stream.Stream;

import org.mpxj.common.JvmHelper;

/**
 * Mono does not accurately identify the host operating system, which means that IKVM doesn't
 * usefully set the `os.name` property. This causes issues when loading native libraries
 * where `os.name` is used to identify the correct version to load.
 *
 * The code in this class attempts to identify the operating system when IKVM
 * is running on a non-Windows host, and provides methods to temporarily set `os.name`
 * to what we believe is actually the current operating system.
 */
public final class OperatingSystem
{
   /**
    * If required, set `os.name` to a more accurate value.
    */
   public void configure()
   {
      if (REPLACEMENT_OS_NAME != null)
      {
         m_originalOsName = System.getProperty("os.name");
         System.setProperty("os.name", REPLACEMENT_OS_NAME);
      }
   }

   /**
    * Restore `os.name` to its original value.
    */
   public void restore()
   {
      if (REPLACEMENT_OS_NAME != null)
      {
         System.setProperty("os.name", m_originalOsName);
      }
   }

   private String m_originalOsName;

   private static final String[] KNOWN_OS_NAMES =
   {
      "Windows",
      "Mac",
      "Darwin",
      "Linux"
   };

   private static final String REPLACEMENT_OS_NAME;

   static
   {
      String replacementOsName = null;
      String osname = System.getProperty("os.name");

      // Simplified code causes issues with IKVM
      // noinspection SimplifyStreamApiCallChains,Convert2MethodRef
      boolean osNotKnown = !Stream.of(KNOWN_OS_NAMES).anyMatch(name -> osname.contains(name));

      if (JvmHelper.isIkvm() && osNotKnown)
      {
         // Based on https://stackoverflow.com/questions/38790802/determine-operating-system-in-net-core
         File ostype = new File("/proc/sys/kernel/ostype");
         if (ostype.exists())
         {
            try
            {
               if (Files.readAllLines(ostype.toPath()).get(0).startsWith("Linux"))
               {
                  replacementOsName = "Linux";
               }
            }

            catch (Exception ex)
            {
               // Ignored
            }
         }
         else
         {
            File systemVersion = new File("/System/Library/CoreServices/SystemVersion.plist");
            if (systemVersion.exists())
            {
               replacementOsName = "Mac OS X";
            }
         }
      }

      REPLACEMENT_OS_NAME = replacementOsName;
   }
}
