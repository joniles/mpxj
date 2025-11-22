package org.mpxj.junit;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestPlan;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.LoggingListener;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;

import static org.junit.platform.engine.discovery.ClassNameFilter.includeClassNamePatterns;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectPackage;

public class MpxjTestRunner
{
   public static void main(String[] argv)
   {
      Logger logger = Logger.getLogger(LoggingListener.class.getName());
      logger.setLevel(Level.ALL);

      ConsoleHandler handler = new ConsoleHandler();
      handler.setFormatter(new SimpleFormatter());
      handler.setLevel(Level.ALL);
      logger.addHandler(handler);
      
      TestExecutionListener listener = LoggingListener.forJavaUtilLogging();
      LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
         .selectors(selectPackage("org.mpxj.junit"))
         .build();
      Launcher launcher = LauncherFactory.create();
      TestPlan testPlan = launcher.discover(request);
      launcher.registerTestExecutionListeners(listener);
      launcher.execute(request);
   }
}
