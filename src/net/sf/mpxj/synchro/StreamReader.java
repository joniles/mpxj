
package net.sf.mpxj.synchro;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import net.sf.mpxj.Duration;
import net.sf.mpxj.common.ByteArray;

class StreamReader
{
   public StreamReader(InputStream stream)
   {
      m_stream = stream;
   }

   public Integer readByte() throws IOException
   {
      return Integer.valueOf(m_stream.read());
   }

   public List<MapRow> readTable(TableReader reader) throws IOException
   {
      reader.read();
      return reader.getRows();
   }

   public List<MapRow> readTable(Class<? extends TableReader> readerClass) throws IOException
   {
      TableReader reader;

      try
      {
         reader = readerClass.getConstructor(InputStream.class).newInstance(m_stream);
      }
      catch (Exception ex)
      {
         throw new RuntimeException(ex);
      }

      return readTable(reader);
   }

   public List<MapRow> readTableConditional(Class<? extends TableReader> readerClass) throws IOException
   {
      List<MapRow> result;
      if (SynchroUtility.getBoolean(m_stream))
      {
         result = readTable(readerClass);
      }
      else
      {
         result = Collections.emptyList();
      }
      return result;
   }

   public ByteArray readBytes(int size) throws IOException
   {
      byte[] data = new byte[size];
      m_stream.read(data);
      return new ByteArray(data);
   }

   public UUID readUUID() throws IOException
   {
      return SynchroUtility.getUUID(m_stream);
   }

   public String readString() throws IOException
   {
      return SynchroUtility.getString(m_stream);
   }

   public Date readDate() throws IOException
   {
      return SynchroUtility.getDate(m_stream);
   }

   public Duration readDuration() throws IOException
   {
      return SynchroUtility.getDuration(m_stream);
   }

   public Integer readInteger() throws IOException
   {
      return SynchroUtility.getInteger(m_stream);
   }

   public Double readDouble() throws IOException
   {
      return SynchroUtility.getDouble(m_stream);
   }

   public List<ByteArray> readBlocks(int size) throws IOException
   {
      List<ByteArray> result = new ArrayList<ByteArray>();
      int fileCount = SynchroUtility.getInt(m_stream);
      if (fileCount != 0)
      {
         for (int index = 0; index < fileCount; index++)
         {
            result.add(readBytes(size));
         }
      }
      return result;
   }

   private final InputStream m_stream;
}
