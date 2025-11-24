/*
 * file:       IkvmTestRunner.java
 * author:     Jon Iles
 * date:       2025-11-24
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

package org.mpxj.junit;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.junit.jupiter.engine.JupiterTestEngine;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherConfig;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.LoggingListener;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

/**
 * This class is provided to make it easier to invoke JUnit 5 tests from .Net code.
 * The problem I've encountered is that the various translated assemblies have their
 * own classloaders, and without explicit references in code, can't dynamically
 * retrieve classes available in other assemblies, therefore JUnit 5 can't
 * discover the available test classes.
 * <p/>
 * The second issue is that I haven't been able to get translated Java code to
 * enumerate the available classes.
 * <p/>
 * The result is that a .Net "driver" class is used to list the test classes from the
 * `org.mpxj.junit` assembly, then passes the list of classes to the `run` method
 * below.
 */
public class IkvmTestRunner
{
   /**
    * Given a list of fully qualified class names, run the tests they represent.
    *
    * @param classNames list of class names
    */
   public static void run(List<String> classNames)
   {
      Logger logger = Logger.getLogger(LoggingListener.class.getName());
      logger.setLevel(Level.ALL);

      Formatter formatter = new Formatter()
      {
         @Override public String format(LogRecord record)
         {
            LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochMilli(record.getMillis()), ZoneId.systemDefault());
            return TIME_FORMAT.format(date) + "\t" + record.getMessage() + "\n";
         }
      };

      ConsoleHandler handler = new ConsoleHandler();
      handler.setFormatter(formatter);
      handler.setLevel(Level.ALL);
      logger.addHandler(handler);

      LauncherConfig config = LauncherConfig.builder()
         .enableTestEngineAutoRegistration(false)
         .addTestEngines(new JupiterTestEngine())
         .build();

      LauncherDiscoveryRequestBuilder builder = LauncherDiscoveryRequestBuilder.request();
      for (String className : classNames)
      {
         try
         {
            builder.selectors(selectClass(Class.forName(className)));
         }

         catch (ClassNotFoundException ex)
         {
            System.out.println("Unable to load " + className);
         }
      }
      LauncherDiscoveryRequest request = builder.build();

      SummaryGeneratingListener listener = new SummaryGeneratingListener();

      Launcher launcher = LauncherFactory.create(config);
      launcher.registerTestExecutionListeners(LoggingListener.forJavaUtilLogging(), listener);
      launcher.execute(request);

      TestExecutionSummary summary = listener.getSummary();
      System.out.println();
      if (summary.getTotalFailureCount() == 0)
      {
         System.out.println("SUCCESS");
      }
      else
      {
         System.out.println("FAILED");
      }
   }

   private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");
}
