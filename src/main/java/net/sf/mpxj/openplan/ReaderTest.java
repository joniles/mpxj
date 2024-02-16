package net.sf.mpxj.openplan;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ReaderTest
{
   public static void main(String[] argv) throws Exception
   {
//      ReaderTest test = new ReaderTest();
//      test.read(argv[0]);

      OpenPlanReader reader = new OpenPlanReader();
      reader.read(argv[0]);
   }

   private void read(String file) throws Exception
   {
      POIFSFileSystem fs = new POIFSFileSystem(new File(file));
      DirectoryEntry root = fs.getRoot();

      String prjName = root.getEntryNames().stream().filter(s -> s.toUpperCase().endsWith("_PRJ")).findFirst().orElse(null);
      if (prjName == null)
      {
         throw new Exception();
      }

      String resourceName = root.getEntryNames().stream().filter(s -> s.toUpperCase().endsWith("_RDS")).findFirst().orElse(null);
      if (prjName == null)
      {
         throw new Exception();
      }

      DirectoryEntry prjDir = (DirectoryEntry) root.getEntry(prjName);
      DirectoryEntry resDir = (DirectoryEntry) root.getEntry(resourceName);

//      prjDir.getEntryNames().stream().forEach(System.out::println);


      List<Row> rows;

      // Project
      rows = new TableReader(prjDir, "PRJ").read();
      rows.forEach(System.out::println);

      // Activities
      rows = new TableReader(prjDir, "ACT").read();
      rows.forEach(System.out::println);

      // Some kind of code mapping?
      rows = new TableReader(prjDir, "SCA").read();
      rows.forEach(System.out::println);

      // Resource assignment?
      rows = new TableReader(prjDir, "USE").read();
      rows.forEach(System.out::println);

      // Subprojects?
      rows = new TableReader(prjDir, "SUB").read();
      rows.forEach(System.out::println);

      // Relationships
      rows = new TableReader(prjDir, "REL").read();
      rows.forEach(System.out::println);

      // More assignment data?
      rows = new TableReader(prjDir, "ASG").read();
      rows.forEach(System.out::println);

      rows = new TableReader(resDir, "RDS").read();
      rows.forEach(System.out::println);

      rows = new TableReader(resDir, "RES").read();
      rows.forEach(System.out::println);

      rows = new TableReader(resDir, "RSL").read();
      rows.forEach(System.out::println);

      rows = new TableReader(resDir, "AVL").read();
      rows.forEach(System.out::println);
   }
}
