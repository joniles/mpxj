package net.sf.mpxj.openplan;
import net.sf.mpxj.common.ByteArrayHelper;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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

      entryNames.forEach(System.out::println);
      System.out.println();

      String prjName = entryNames.stream().filter(s -> s.toUpperCase().endsWith("_PRJ")).findFirst().orElse(null);
      if (prjName == null)
      {
         throw new Exception();
      }

      DirectoryEntry prjDir = (DirectoryEntry) root.getEntry(prjName);
      prjDir.getEntryNames().stream().forEach(System.out::println);

      readTable(new DocumentInputStream((DocumentEntry) prjDir.getEntry("ACT")));

   }

   private void readTable(InputStream is) throws IOException
   {
      int magic = getInt(is);
      int columnCount = getInt(is);
      String[] columns = new String[columnCount];
      for (int index = 0; index < columnCount; index++)
      {
         int length = is.read();
         byte[] nameBytes = new byte[length];
         is.read(nameBytes);
         String name = new String(nameBytes);
         columns[index] = name;
         System.out.println(name);
      }

      int rowCount = getInt(is);
      System.out.println(rowCount);

      for (int rowIndex = 0; rowIndex < rowCount; rowIndex++)
      {
         System.out.println("ROW " + rowIndex);
         for (int columnIndex=0; columnIndex < columnCount; columnIndex++)
         {
            int byteCount = is.read();
            byte[] bytes = new byte[byteCount];
            is.read(bytes);
            System.out.println(columns[columnIndex] + "\t"+ ByteArrayHelper.hexdump(bytes, true));
         }
         System.out.println();
      }
   }

   private int getShort(InputStream is) throws IOException
   {
      int result = 0;
      for (int shiftBy = 0; shiftBy < 16; shiftBy += 8)
      {
         result |= ((is.read() & 0xff)) << shiftBy;
      }
      return result;
   }

   private int getInt(InputStream is) throws IOException
   {
      int result = 0;
      for (int shiftBy = 0; shiftBy < 32; shiftBy += 8)
      {
         result |= ((is.read() & 0xff)) << shiftBy;
      }
      return result;
   }

}
