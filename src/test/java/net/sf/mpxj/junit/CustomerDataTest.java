/*
 * file:       CustomerDataTest.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2008
 * date:       27/11/2008
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

package net.sf.mpxj.junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;

import net.sf.mpxj.primavera.PrimaveraXERFileWriter;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Before;

import net.sf.mpxj.ChildTaskContainer;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Task;
import net.sf.mpxj.common.FileHelper;
import net.sf.mpxj.common.JvmHelper;
import net.sf.mpxj.common.MarshallerHelper;
import net.sf.mpxj.json.JsonWriter;
import net.sf.mpxj.mpx.MPXReader;
import net.sf.mpxj.mpx.MPXWriter;
import net.sf.mpxj.mspdi.MSPDIReader;
import net.sf.mpxj.mspdi.MSPDIWriter;
import net.sf.mpxj.planner.PlannerWriter;
import net.sf.mpxj.primavera.PrimaveraDatabaseFileReader;
import net.sf.mpxj.primavera.PrimaveraPMFileWriter;
import net.sf.mpxj.primavera.PrimaveraXERFileReader;
import net.sf.mpxj.reader.UniversalProjectReader;
import net.sf.mpxj.sdef.SDEFWriter;
import net.sf.mpxj.writer.ProjectWriter;

/**
 * The tests contained in this class exercise MPXJ
 * using customer supplied data.
 */
public class CustomerDataTest
{
   /**
    * Constructor.
    */
   public CustomerDataTest()
   {
      m_privateDirectory = configureDirectory("mpxj.junit.privatedir");
      m_baselineDirectory = configureDirectory("mpxj.junit.baselinedir");
      m_primaveraFile = System.getProperty("mpxj.junit.primavera.file");
      m_primaveraBaselineDir = configureDirectory("mpxj.junit.primavera.baselinedir");

      m_universalReader = new UniversalProjectReader();
      m_mpxReader = new MPXReader();
      m_xerReader = new PrimaveraXERFileReader();
      m_xerReader.setLinkCrossProjectRelations(true);
   }

   /**
    * Test customer data.
    */
   @Test public void testCustomerData1()
   {
      testCustomerData(1, 10);
   }

   /**
    * Test customer data.
    */
   @Test public void testCustomerData2()
   {
      testCustomerData(2, 10);
   }

   /**
    * Test customer data.
    */
   @Test public void testCustomerData3()
   {
      testCustomerData(3, 10);
   }

   /**
    * Test customer data.
    */
   @Test public void testCustomerData4()
   {
      testCustomerData(4, 10);
   }

   /**
    * Test customer data.
    */
   @Test public void testCustomerData5()
   {
      testCustomerData(5, 10);
   }

   /**
    * Test customer data.
    */
   @Test public void testCustomerData6()
   {
      testCustomerData(6, 10);
   }

   /**
    * Test customer data.
    */
   @Test public void testCustomerData7()
   {
      testCustomerData(7, 10);
   }

   /**
    * Test customer data.
    */
   @Test public void testCustomerData8()
   {
      testCustomerData(8, 10);
   }

   /**
    * Test customer data.
    */
   @Test public void testCustomerData9()
   {
      testCustomerData(9, 10);
   }

   /**
    * Test customer data.
    */
   @Test public void testCustomerData10()
   {
      testCustomerData(10, 10);
   }

   /**
    * Test extracting projects from a sample SQLite P6 database.
    */
   @Test public void testPrimaveraDatabase() throws Exception
   {
      if (m_primaveraFile == null)
      {
         return;
      }

      // Accessing the database directly from (new) Google Drive is too slow.
      // Make a temporary local copy instead.
      File file = Files.createTempFile("primavera", "db").toFile();
      file.deleteOnExit();
      Files.copy(new File(m_primaveraFile).toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);

      Map<Integer, String> projects = new PrimaveraDatabaseFileReader().listProjects(file);
      long failures = projects.entrySet().stream().map(entry -> testPrimaveraProject(file, entry.getKey().intValue(), entry.getValue())).filter(x -> !x.booleanValue()).count();

      if (DIFF_BASELINE_DIR != null)
      {
         System.out.println();
         System.out.println("Baseline: " + DIFF_BASELINE_DIR.getPath());
         System.out.println("Test: " + DIFF_TEST_DIR.getPath());
      }

      assertEquals("Failed to read " + failures + " Primavera database projects", 0, failures);
   }

   /**
    * Populate field report from JUnit test data.
    */
   @Test public void testFieldCoverage()
   {
      List<File> files = new ArrayList<>();
      listFiles(files, new File(MpxjTestData.DATA_DIR));
      for (File file : files)
      {
         ProjectFile project;
         try
         {
            project = m_universalReader.read(file);
         }

         catch (Exception ex)
         {
            // we're reading our JUnit test data which we've
            // already validated... we're just improving field report coverage.
            // This will throw expected errors (password protected etc)
            // which we'll ignore.
            project = null;
         }

         if (project != null && useFieldReporter())
         {
            FIELD_REPORTER.process(project);
         }
      }
   }

   /**
    * Enable our custom character escape handler if we're running on a JVM.
    */
   @BeforeClass public static void enableCustomEscapeHandling()
   {
      if (!JvmHelper.isIkvm())
      {
         MarshallerHelper.enableCustomEscapeHandling(true);
      }
   }

   /**
    * Disable our custom character escape handler if we're running on a JVM.
    */
   @AfterClass public static void disableCustomEscapeHandling()
   {
      if (!JvmHelper.isIkvm())
      {
         MarshallerHelper.enableCustomEscapeHandling(false);
      }
   }

   /**
    * Clear the field reporter ready to begin collecting data.
    */
   @BeforeClass public static void initializeFieldReport()
   {
      if (useFieldReporter())
      {
         TEST_COUNT = 0;
         FIELD_REPORTER.clear();
      }
   }

   /**
    * Report on the data collected by the field reporter.
    */
   @AfterClass public static void generateFieldReport() throws Exception
   {
      if (useFieldReporter() && TEST_COUNT == 12)
      {
         FIELD_REPORTER.report("mkdocs/docs/field-guide.md");
         FIELD_REPORTER.reportMpp("mkdocs/docs/mpp-field-guide.md");
      }
   }

   /**
    * Increment the counter for the number of tests run.
    */
   @Before public void incrementTestCount()
   {
      ++TEST_COUNT;
   }

   private static boolean useFieldReporter()
   {
      return !JvmHelper.isIkvm();
   }

   /**
    * Test a project from a Primavera SQLite database.
    *
    * @param file database file
    * @param projectID ID of the project to extract
    * @param projectName Name of the project to extract
    * @return true if project read and validated successfully
    */
   private Boolean testPrimaveraProject(File file, int projectID, String projectName)
   {
      Boolean result = Boolean.TRUE;

      try
      {
         PrimaveraDatabaseFileReader reader = new PrimaveraDatabaseFileReader();
         reader.setProjectID(projectID);
         ProjectFile project = reader.read(file);

         Integer baselineProjectID = project.getProjectProperties().getBaselineProjectUniqueID();
         if (baselineProjectID != null)
         {
            PrimaveraDatabaseFileReader baselineReader = new PrimaveraDatabaseFileReader();
            baselineReader.setProjectID(baselineProjectID.intValue());
            project.setBaseline(baselineReader.read(file));
         }

         if (!testBaseline(projectName, project, m_primaveraBaselineDir))
         {
            System.err.println("Failed to validate Primavera database project baseline " + projectName);
            result = Boolean.FALSE;
         }

         if (useFieldReporter())
         {
            FIELD_REPORTER.process(project);
         }
      }
      catch (Exception e)
      {
         System.err.println("Failed to read Primavera database project: " + projectName);
         e.printStackTrace();
         result = Boolean.FALSE;
      }

      return result;
   }

   /**
    * Create a File instance from a path stored as a property.
    *
    * @param propertyName property name
    * @return File instance
    */
   private File configureDirectory(String propertyName)
   {
      File dir = null;
      String dirName = System.getProperty(propertyName);
      if (dirName != null && !dirName.isEmpty())
      {
         dir = new File(dirName);
         if (!dir.exists() || !dir.isDirectory())
         {
            dir = null;
         }
      }

      return dir;
   }

   /**
    * As part of the bug reports that are submitted for MPXJ I am passed a
    * number of confidential project files, which for obvious reasons cannot
    * be redistributed as test cases. These files reside in a directory on
    * my development machine, and assuming that this directory exists, this
    * test will attempt of read each of the files in turn.
    *
    * @param index current chunk
    * @param max maximum number of chunks
    */
   private void testCustomerData(int index, int max)
   {
      if (m_privateDirectory != null)
      {
         List<File> files = new ArrayList<>();
         listFiles(files, m_privateDirectory);

         int interval = files.size() / max;
         int startIndex = (index - 1) * interval;
         int endIndex;
         if (index == max)
         {
            endIndex = files.size();
         }
         else
         {
            endIndex = startIndex + interval;
         }

         executeTests(files.subList(startIndex, endIndex));
      }
   }

   /**
    * Recursively descend through the test data directory adding files to the list.
    *
    * @param list file list
    * @param parent parent directory
    */
   private void listFiles(List<File> list, File parent)
   {
      File[] fileList = parent.listFiles();
      assertNotNull(fileList);
      Arrays.sort(fileList);

      for (File file : fileList)
      {
         if (file.isDirectory())
         {
            listFiles(list, file);
         }
         else
         {
            String name = file.getName().toLowerCase();
            if (name.endsWith(".ds_store"))
            {
               continue;
            }

            list.add(file);
         }
      }
   }

   /**
    * Validate that all files in the list can be read by MPXJ.
    *
    * @param files file list
    */
   private void executeTests(List<File> files)
   {
      int failures = 0;
      int sourceDirNameLength = m_privateDirectory.getPath().length();

      for (File file : files)
      {
         String name = file.getName().toUpperCase();
         if (name.endsWith(".MPP.XML"))
         {
            continue;
         }

         try
         {
            List<ProjectFile> projects = testReader(name, file);
            if (projects.isEmpty())
            {
               System.err.println("Failed to read " + name);
               ++failures;
               continue;
            }

            String baselineName = file.getPath().substring(sourceDirNameLength);
            int baselineIndex = 0;

            for (ProjectFile project : projects)
            {
               // Apply a suffix to the second and subsequent schedules form a file
               // "ep" = embedded project, just added to the suffix to make it easier to pattern match
               String fullBaselineName = baselineIndex == 0 ? baselineName : baselineName + ".ep" + baselineIndex;
               if (!executeTests(name, fullBaselineName, project))
               {
                  ++failures;
               }
               ++baselineIndex;
            }
         }

         catch (Exception ex)
         {
            System.err.println("Failed to read " + name);
            ex.printStackTrace();
            ++failures;
         }
      }

      if (DIFF_BASELINE_DIR != null)
      {
         System.out.println();
         System.out.println("Baseline: " + DIFF_BASELINE_DIR.getPath());
         System.out.println("Test: " + DIFF_TEST_DIR.getPath());
      }

      assertEquals("Failed to read " + failures + " files", 0, failures);
   }

   /**
    * Execute tests for an schedule file.
    *
    * @param fileName parent filename
    * @param baselineName baseline name
    * @param projectFile project file
    * @return true if successful
    */
   private boolean executeTests(String fileName, String baselineName, ProjectFile projectFile) throws Exception
   {
      if (!testHierarchy(projectFile))
      {
         System.err.println("Failed to validate hierarchy " + fileName);
         return false;
      }

      if (!testBaseline(baselineName, projectFile, m_baselineDirectory))
      {
         System.err.println("Failed to validate baseline " + fileName);
         return false;
      }

      if (useFieldReporter())
      {
         FIELD_REPORTER.process(projectFile);
      }

      return true;
   }

   /**
    * Ensure that we can read the file.
    *
    * @param name file name
    * @param file File instance
    * @return ProjectFile instance
    */
   private List<ProjectFile> testReader(String name, File file) throws Exception
   {
      List<ProjectFile> projects;

      if (name.endsWith(".MPX"))
      {
         m_mpxReader.setLocale(Locale.ENGLISH);

         if (name.contains(".DE."))
         {
            m_mpxReader.setLocale(Locale.GERMAN);
         }

         if (name.contains(".SV."))
         {
            m_mpxReader.setLocale(new Locale("sv"));
         }

         projects = m_mpxReader.readAll(file);
      }
      else
      {
         projects = m_universalReader.readAll(file);
         if (name.endsWith(".MPP") && projects.size() == 1)
         {
            validateMpp(file.getCanonicalPath(), projects.get(0));
         }
      }

      return projects;
   }

   /**
    * Ensure that both child and parent tasks agree on the relationship.
    *
    * @param parent schedule file to test
    * @return true if the hierarchy test is successful
    */
   private boolean testHierarchy(ChildTaskContainer parent)
   {
      for (Task task : parent.getChildTasks())
      {
         if (parent instanceof Task)
         {
            if (task.getParentTask() != parent)
            {
               return false;
            }
         }

         if (!testHierarchy(task))
         {
            return false;
         }
      }
      return true;
   }

   /**
    * Generate new files from the file under test and compare them to a baseline
    * we have previously created. This potentially allows us to capture unintended
    * changes in functionality. If we do not have a baseline for this particular
    * file, we'll generate one.
    *
    * @param name name of the project under test
    * @param project ProjectFile instance
    * @param baselineDirectory directory in which baseline files are held
    * @return true if the baseline test is successful
    */
   private boolean testBaseline(String name, ProjectFile project, File baselineDirectory) throws Exception
   {
      if (baselineDirectory == null)
      {
         return true;
      }

      Consumer<ProjectWriter> jsonConfig = (w) -> {
         ((JsonWriter) w).setPretty(true);
         ((JsonWriter) w).setIncludeLayoutData(true);
      };
      Consumer<ProjectWriter> pmxmlConfig = (w) -> ((PrimaveraPMFileWriter) w).setWriteBaselines(true);
      Consumer<ProjectWriter> mpxConfig = (w) -> ((MPXWriter) w).setUseLocaleDefaults(false);
      Consumer<ProjectWriter> mspdiConfig = (w) -> {
         ((MSPDIWriter) w).setWriteTimephasedData(true);
         ((MSPDIWriter) w).setSplitTimephasedAsDays(false);
      };

      // TODO: randomise order of execution
      boolean pmxml = testBaseline(name, project, baselineDirectory, "pmxml", ".xml", PrimaveraPMFileWriter.class, pmxmlConfig);
      boolean json = testBaseline(name, project, baselineDirectory, "json", ".json", JsonWriter.class, jsonConfig);
      boolean planner = testBaseline(name, project, baselineDirectory, "planner", ".xml", PlannerWriter.class, null);
      boolean sdef = testBaseline(name, project, baselineDirectory, "sdef", ".sdef", SDEFWriter.class, null);
      boolean xer = testBaseline(name, project, baselineDirectory, "xer", ".xer", PrimaveraXERFileWriter.class, null);
      // TODO: fix in-place renumbering for MS Project
      boolean mspdi = testBaseline(name, project, baselineDirectory, "mspdi", ".xml", MSPDIWriter.class, mspdiConfig);
      boolean mpx = testBaseline(name, project, baselineDirectory, "mpx", ".mpx", MPXWriter.class, mpxConfig);

      return mspdi && pmxml && json && planner && sdef && xer && mpx;
   }

   /**
    * Generate a baseline for a specific file type.
    *
    * @param name name of the project under test
    * @param project ProjectFile instance
    * @param baselineDir baseline directory location
    * @param subDir subdirectory name
    * @param suffix file suffix
    * @param writerClass file writer class
    * @param config optional writer configuration
    * @return true if the baseline test is successful
    */
   private boolean testBaseline(String name, ProjectFile project, File baselineDir, String subDir, String suffix, Class<? extends ProjectWriter> writerClass, Consumer<ProjectWriter> config) throws Exception
   {
      File baselineDirectory = new File(baselineDir, subDir);

      boolean success = true;

      ProjectWriter writer = writerClass.newInstance();

      if (config != null)
      {
         config.accept(writer);
      }

      File baselineFile = new File(baselineDirectory, name + suffix);

      project.getProjectProperties().setCurrentDate(BASELINE_CURRENT_DATE);

      if (baselineFile.exists())
      {
         File out = Files.createTempFile("junit", suffix).toFile();
         writer.write(project, out);
         success = FileUtility.equals(baselineFile, out);

         if (success)
         {
            FileHelper.deleteQuietly(out);
         }
         else
         {
            debugFailure(baselineFile, subDir, out);
         }
      }
      else
      {
         FileHelper.mkdirsQuietly(baselineFile.getParentFile());
         writer.write(project, baselineFile);
      }

      return success;
   }

   /**
    * Write a diagnostic message and populate directories to make
    * it easier to diff multiple files.
    *
    * @param baseline baseline file
    * @param writerType identifies the writer type which failed the baseline comparison
    * @param test test file
    */
   private void debugFailure(File baseline, String writerType, File test) throws IOException
   {
      String copyCommand = OS_IS_WINDOWS ? "copy /y" : "cp";

      System.out.println();
      System.out.println("Baseline: " + baseline.getPath());
      System.out.println("Test: " + test.getPath());
      System.out.println(copyCommand + " \"" + test.getPath() + "\" \"" + baseline.getPath() + "\"");

      if (DIFF_BASELINE_DIR == null)
      {
         File diffDir = FileHelper.createTempDir();
         DIFF_BASELINE_DIR = new File(diffDir, "baseline");
         DIFF_TEST_DIR = new File(diffDir, "test");
         FileHelper.mkdirs(DIFF_BASELINE_DIR);
         FileHelper.mkdirs(DIFF_TEST_DIR);
      }

      int index = baseline.getName().lastIndexOf('.');
      String name = baseline.getName().substring(0, index);
      String extension = baseline.getName().substring(index);
      String newName = name + "." + writerType + extension;

      Files.copy(baseline.toPath(), new File(DIFF_BASELINE_DIR, newName).toPath(), StandardCopyOption.REPLACE_EXISTING);
      Files.copy(test.toPath(), new File(DIFF_TEST_DIR, newName).toPath(), StandardCopyOption.REPLACE_EXISTING);
   }

   /**
    * As part of the regression test process, I save customer's MPP files
    * as MSPDI files using a version of MS Project. This method allows these
    * two versions to be compared in order to ensure that MPXJ is
    * correctly reading the data from both file formats.
    *
    * @param name file name
    * @param mpp MPP file data structure
    */
   private void validateMpp(String name, ProjectFile mpp) throws Exception
   {
      File xmlFile = new File(name + ".xml");
      if (xmlFile.exists())
      {
         ProjectFile xml = new MSPDIReader().read(xmlFile);
         MppXmlCompare compare = new MppXmlCompare();
         compare.process(xml, mpp);
      }
   }

   private final File m_privateDirectory;
   private final File m_baselineDirectory;
   private final String m_primaveraFile;
   private final File m_primaveraBaselineDir;

   private final UniversalProjectReader m_universalReader;
   private final MPXReader m_mpxReader;
   private final PrimaveraXERFileReader m_xerReader;
   private static File DIFF_BASELINE_DIR;
   private static File DIFF_TEST_DIR;

   private static int TEST_COUNT;
   private static final boolean OS_IS_WINDOWS = System.getProperty("os.name").toLowerCase().contains("windows");
   private static final FieldReporter FIELD_REPORTER = new FieldReporter();
   private static final LocalDateTime BASELINE_CURRENT_DATE = LocalDateTime.of(2018, 12, 6, 12, 51, 42);
}
