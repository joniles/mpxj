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
      ReaderTest test = new ReaderTest();
      test.read(argv[0]);
   }

   private void read(String file) throws Exception
   {
      POIFSFileSystem fs = new POIFSFileSystem(new File(file));
      DirectoryEntry root = fs.getRoot();
      Set<String> entryNames = root.getEntryNames();

//      entryNames.forEach(System.out::println);
//      System.out.println();

      String prjName = entryNames.stream().filter(s -> s.toUpperCase().endsWith("_PRJ")).findFirst().orElse(null);
      if (prjName == null)
      {
         throw new Exception();
      }

      DirectoryEntry prjDir = (DirectoryEntry) root.getEntry(prjName);
//      prjDir.getEntryNames().stream().forEach(System.out::println);

      List<Row> rows = new OpenPlanTable(prjDir, "PRJ").read();
      rows.forEach(System.out::println);

//      List<Row> rows = new OpenPlanTable(prjDir, "ACT").read();
//      rows.forEach(System.out::println);
   }
}
