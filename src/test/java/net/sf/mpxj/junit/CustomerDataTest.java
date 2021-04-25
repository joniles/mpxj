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
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import net.sf.mpxj.ChildTaskContainer;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Task;
import net.sf.mpxj.common.FileHelper;
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
   }

   /**
    * Test customer data.
    */
   @Test public void testCustomerData1() throws Exception
   {
      testCustomerData(1, 10);
   }

   /**
    * Test customer data.
    */
   @Test public void testCustomerData2() throws Exception
   {
      testCustomerData(2, 10);
   }

   /**
    * Test customer data.
    */
   @Test public void testCustomerData3() throws Exception
   {
      testCustomerData(3, 10);
   }

   /**
    * Test customer data.
    */
   @Test public void testCustomerData4() throws Exception
   {
      testCustomerData(4, 10);
   }

   /**
    * Test customer data.
    */
   @Test public void testCustomerData5() throws Exception
   {
      testCustomerData(5, 10);
   }

   /**
    * Test customer data.
    */
   @Test public void testCustomerData6() throws Exception
   {
      testCustomerData(6, 10);
   }

   /**
    * Test customer data.
    */
   @Test public void testCustomerData7() throws Exception
   {
      testCustomerData(7, 10);
   }

   /**
    * Test customer data.
    */
   @Test public void testCustomerData8() throws Exception
   {
      testCustomerData(8, 10);
   }

   /**
    * Test customer data.
    */
   @Test public void testCustomerData9() throws Exception
   {
      testCustomerData(9, 10);
   }

   /**
    * Test customer data.
    */
   @Test public void testCustomerData10() throws Exception
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

      File file = new File(m_primaveraFile);
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
   @Test public void testFieldCoverage() throws Exception
   {
      List<File> files = new ArrayList<>();
      listFiles(files, new File(MpxjTestData.DATA_DIR));
      for (File file : files)
      {
         ProjectFile project = null;
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

         if (project != null)
         {
            FIELD_REPORTER.process(project);
         }
      }
   }

   /**
    * Clear the field reporter ready to begin collecting data.
    */
   @BeforeClass public static void initializeFieldReport()
   {
      FIELD_REPORTER.clear();
   }

   /**
    * Report on the data collected by the field reporter.
    */
   @AfterClass public static void generateFieldReport() throws Exception
   {
      String runtime = System.getProperty("java.runtime.name");
      boolean isIKVM = runtime != null && runtime.indexOf("IKVM") != -1;
      if (!isIKVM)
      {
         FIELD_REPORTER.report("mkdocs/docs/field-guide.md");
         FIELD_REPORTER.reportMpp("mkdocs/docs/mpp-field-guide.md");
      }
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
            project.setBaseline(baselineReader.read(file), t -> t.getCanonicalActivityID());
         }

         if (!testBaseline(projectName, project, m_primaveraBaselineDir))
         {
            System.err.println("Failed to validate Primavera database project baseline " + projectName);
            result = Boolean.FALSE;
         }
         FIELD_REPORTER.process(project);
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
   private void testCustomerData(int index, int max) throws Exception
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
      String runtime = System.getProperty("java.runtime.name");
      boolean isIKVM = runtime != null && runtime.indexOf("IKVM") != -1;
      File[] fileList = parent.listFiles();
      assertNotNull(fileList);

      for (File file : fileList)
      {
         if (file.isDirectory())
         {
            listFiles(list, file);
         }
         else
         {
            String name = file.getName().toLowerCase();
            if (isIKVM && (name.endsWith(".mpd") || name.endsWith(".mdb")))
            {
               continue;
            }
            list.add(file);
         }
      }
   }

   /**
    * Validate that all of the files in the list can be read by MPXJ.
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

         ProjectFile mpxj = null;
         //System.out.println(name);

         try
         {
            mpxj = testReader(name, file);
            if (mpxj == null)
            {
               System.err.println("Failed to read " + name);
               ++failures;
               continue;
            }

            if (!testHierarchy(mpxj))
            {
               System.err.println("Failed to validate hierarchy " + name);
               ++failures;
               continue;
            }

            if (!testBaseline(file.getPath().substring(sourceDirNameLength), mpxj, m_baselineDirectory))
            {
               System.err.println("Failed to validate baseline " + name);
               ++failures;
               continue;
            }

            testWriters(mpxj);

            FIELD_REPORTER.process(mpxj);
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
    * Ensure that we can read the file.
    *
    * @param name file name
    * @param file File instance
    * @return ProjectFile instance
    */
   private ProjectFile testReader(String name, File file) throws Exception
   {
      ProjectFile mpxj = null;

      if (name.endsWith(".MPX") == true)
      {
         m_mpxReader.setLocale(Locale.ENGLISH);

         if (name.indexOf(".DE.") != -1)
         {
            m_mpxReader.setLocale(Locale.GERMAN);
         }

         if (name.indexOf(".SV.") != -1)
         {
            m_mpxReader.setLocale(new Locale("sv"));
         }

         mpxj = m_mpxReader.read(file);
      }
      else
      {
         mpxj = m_universalReader.read(file);
         if (name.endsWith(".MPP"))
         {
            validateMpp(file.getCanonicalPath(), mpxj);
         }

         // If we have an XER file, exercise the "readAll" functionality too.
         // For now, ignore files with non-standard encodings.
         if (name.endsWith(".XER") && !name.endsWith(".ENCODING.XER"))
         {
            m_xerReader.setLinkCrossProjectRelations(true);
            m_xerReader.readAll(new FileInputStream(file));
         }
      }

      return mpxj;
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

      boolean mspdi = testBaseline(name, project, baselineDirectory, "mspdi", MSPDIWriter.class);
      boolean pmxml = testBaseline(name, project, baselineDirectory, "pmxml", PrimaveraPMFileWriter.class);
      boolean json = testBaseline(name, project, baselineDirectory, "json", JsonWriter.class);
      boolean planner = testBaseline(name, project, baselineDirectory, "planner", PlannerWriter.class);
      boolean sdef = testBaseline(name, project, baselineDirectory, "sdef", SDEFWriter.class);

      return mspdi && pmxml && json && planner && sdef;
   }

   /**
    * Generate a baseline for a specific file type.
    *
    * @param name name of the project under test
    * @param project ProjectFile instance
    * @param baselineDir baseline directory location
    * @param subDir sub directory name
    * @param writerClass file writer class
    * @return true if the baseline test is successful
    */
   @SuppressWarnings("unused") private boolean testBaseline(String name, ProjectFile project, File baselineDir, String subDir, Class<? extends ProjectWriter> writerClass) throws Exception
   {
      File baselineDirectory = new File(baselineDir, subDir);

      boolean success = true;

      ProjectWriter writer = writerClass.newInstance();
      String suffix;

      // Not ideal, but...
      if (writer instanceof JsonWriter)
      {
         ((JsonWriter) writer).setPretty(true);
         suffix = ".json";
      }
      else
      {
         if (writer instanceof SDEFWriter)
         {
            suffix = ".sdef";
         }
         else
         {
            suffix = ".xml";

            if (writer instanceof PrimaveraPMFileWriter)
            {
               ((PrimaveraPMFileWriter) writer).setWriteBaselines(true);
            }
         }
      }
      File baselineFile = new File(baselineDirectory, name + suffix);

      project.getProjectProperties().setCurrentDate(BASELINE_CURRENT_DATE);

      if (baselineFile.exists())
      {
         File out = File.createTempFile("junit", suffix);
         writer.write(project, out);
         success = FileUtility.equals(baselineFile, out);

         if (success || !DEBUG_FAILURES)
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
      System.out.println();
      System.out.println("Baseline: " + baseline.getPath());
      System.out.println("Test: " + test.getPath());
      System.out.println("copy /y \"" + test.getPath() + "\" \"" + baseline.getPath() + "\"");

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
    * Ensure that we can export the file under test through our writers, without error.
    *
    * @param project ProjectFile instance
    */
   private void testWriters(ProjectFile project) throws Exception
   {
      for (Class<? extends ProjectWriter> c : WRITER_CLASSES)
      {
         File outputFile = File.createTempFile("writer_test", ".dat");
         outputFile.deleteOnExit();
         ProjectWriter p = c.newInstance();
         p.write(project, outputFile);
         FileHelper.deleteQuietly(outputFile);
      }
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
      if (xmlFile.exists() == true)
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

   private UniversalProjectReader m_universalReader;
   private MPXReader m_mpxReader;
   private PrimaveraXERFileReader m_xerReader;
   private static File DIFF_BASELINE_DIR;
   private static File DIFF_TEST_DIR;

   private static final FieldReporter FIELD_REPORTER = new FieldReporter();

   private static final List<Class<? extends ProjectWriter>> WRITER_CLASSES = new ArrayList<>();

   private static final Date BASELINE_CURRENT_DATE = new Date(1544100702438L);

   private static final boolean DEBUG_FAILURES = false;

   static
   {
      // Exercised by baseline test
      //WRITER_CLASSES.add(JsonWriter.class);

      // Exercised by baseline test
      //WRITER_CLASSES.add(MSPDIWriter.class);

      // Exercised by baseline test
      //WRITER_CLASSES.add(PlannerWriter.class);

      // Exercise by baseline test
      //WRITER_CLASSES.add(PrimaveraPMFileWriter.class);

      // Exercise by baseline test
      // WRITER_CLASSES.add(SDEFWriter.class);

      // Write MPX last as applying locale settings will change some project values
      WRITER_CLASSES.add(MPXWriter.class);
   }
}
