
package net.sf.mpxj.synchro;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

abstract class TableReader
{
   public TableReader(InputStream stream)
   {
      m_stream = stream;
   }

   public List<MapRow> getRows()
   {
      return m_rows;
   }

   public void read() throws IOException
   {
      int tableHeader = SynchroUtility.getInt(m_stream);
      if (tableHeader != 0x39AF547A)
      {
         throw new IllegalArgumentException("Unexpected file format");
      }

      int recordCount = SynchroUtility.getInt(m_stream);
      System.out.println("recordCount: " + recordCount);

      for (int loop = 0; loop < recordCount; loop++)
      {
         readRow();
      }

      int tableTrailer = SynchroUtility.getInt(m_stream);
      if (tableTrailer != 0x6F99E416)
      {
         throw new IllegalArgumentException("Unexpected file format");
      }
   }

   protected abstract int rowMagicNumber();

   protected abstract void readRow() throws IOException;

   protected final InputStream m_stream;
   protected final List<MapRow> m_rows = new ArrayList<MapRow>();
}
