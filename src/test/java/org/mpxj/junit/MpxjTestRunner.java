package org.mpxj.junit;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.junit.jupiter.engine.JupiterTestEngine;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.TestPlan;
import org.junit.platform.launcher.core.LauncherConfig;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.LoggingListener;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

public class MpxjTestRunner
{
//   public static void main(String[] argv)
//   {
//      List<String> classNames = Arrays.asList("org.mpxj.junit.AvailabilityTest", "org.mpxj.junit.DateUtilityTest");
//      run(classNames);
//   }

   public static void run(List<String> classNames)
   {
      Logger logger = Logger.getLogger(LoggingListener.class.getName());
      logger.setLevel(Level.ALL);

      Formatter formatter = new Formatter(){
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
      LauncherDiscoveryRequest request =  builder.build();

      SummaryGeneratingListener listener = new SummaryGeneratingListener();

      Launcher launcher = LauncherFactory.create(config);
      TestPlan testPlan = launcher.discover(request);
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
