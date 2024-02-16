package net.sf.mpxj.openplan;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReaderTest
{
   public static void main(String[] argv) throws Exception
   {
      ReaderTest test = new ReaderTest();
      test.read(argv[0]);

//      OpenPlanReader reader = new OpenPlanReader();
//      reader.read(argv[0]);
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
      collectUids(rows);

      rows = new TableReader(prjDir, "ACT").read();
      rows.forEach(System.out::println);
      collectUids(rows);

      rows = new TableReader(prjDir, "SCA").read();
      rows.forEach(System.out::println);
      collectUids(rows);

      // Resource assignment?
      rows = new TableReader(prjDir, "USE").read();
      rows.forEach(System.out::println);
      collectUids(rows);

      // Subprojects?
      rows = new TableReader(prjDir, "SUB").read();
      rows.forEach(System.out::println);
      collectUids(rows);

      // Relationships
      rows = new TableReader(prjDir, "REL").read();
      rows.forEach(System.out::println);
      collectUids(rows);

      // More assignment data?
      rows = new TableReader(prjDir, "ASG").read();
      rows.forEach(System.out::println);
      collectUids(rows);

      rows = new TableReader(resDir, "RDS").read();
      rows.forEach(System.out::println);
      collectUids(rows);

      rows = new TableReader(resDir, "RES").read();
      rows.forEach(System.out::println);
      collectUids(rows);

      rows = new TableReader(resDir, "RSL").read();
      rows.forEach(System.out::println);
      collectUids(rows);

      rows = new TableReader(resDir, "AVL").read();
      rows.forEach(System.out::println);
      collectUids(rows);

      m_uids.forEach(System.out::println);

      for (int loop=0; loop < 100; loop++)
      {
         UUID value = UUID.randomUUID();
         String encoded = UuidHelper.print(value);
         UUID decoded = UuidHelper.parse(encoded);
         System.out.println(value + "\t" + encoded + "\t" + decoded);
         if (!value.equals(decoded))
         {
            throw new Exception();
         }
      }

      for (String value : m_uids)
      {
         System.out.println(value +"\t" + UuidHelper.parse(value+"  "));
      }
   }


   private void collectUids(List<Row> rows)
   {
      rows.forEach(r -> ((MapRow)r).m_map.entrySet().stream().filter(e -> e.getKey().endsWith("_UID")).forEach(e -> collectUids((String)e.getValue())));
   }

   private void collectUids(String value)
   {
      m_uids.add(value);
   }



   private final List<String> m_uids = new ArrayList<>();
}
