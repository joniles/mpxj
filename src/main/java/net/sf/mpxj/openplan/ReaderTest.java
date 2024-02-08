package net.sf.mpxj.openplan;
import net.sf.mpxj.common.ByteArrayHelper;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;

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

      // Project
//      List<Row> rows = new OpenPlanTable(prjDir, "PRJ").read();
//      rows.forEach(System.out::println);

      //      List<Row> rows = new OpenPlanTable(prjDir, "ACT").read();
//      rows.forEach(System.out::println);

//      List<Row> rows = new OpenPlanTable(prjDir, "SCA").read();
//      rows.forEach(System.out::println);

      // Resource assignment?
//      List<Row> rows = new OpenPlanTable(prjDir, "USE").read();
//      rows.forEach(System.out::println);

      // Subprojects?
//      List<Row> rows = new OpenPlanTable(prjDir, "SUB").read();
//      rows.forEach(System.out::println);

      // Relationships
//      List<Row> rows = new OpenPlanTable(prjDir, "REL").read();
//      rows.forEach(System.out::println);

      // More assignment data?
//      List<Row> rows = new OpenPlanTable(prjDir, "ASG").read();
//      rows.forEach(System.out::println);


//            List<Row> rows = new OpenPlanTable(resDir, "RDS").read();
//            rows.forEach(System.out::println);

            List<Row> rows = new OpenPlanTable(resDir, "RES").read();
      rows.forEach(System.out::println);

//      List<Row> rows = new OpenPlanTable(resDir, "RSL").read();
//      rows.forEach(System.out::println);

//      List<Row> rows = new OpenPlanTable(resDir, "AVL").read();
//      rows.forEach(System.out::println);

   }
}
