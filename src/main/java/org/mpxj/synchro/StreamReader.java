/*
 * file:       StreamReader.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2018
 * date:       2018-10-11
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

package org.mpxj.synchro;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;

import java.util.List;
import java.util.UUID;

import org.mpxj.Duration;
import org.mpxj.common.ByteArray;
import org.mpxj.common.InputStreamHelper;
import org.mpxj.common.SemVer;

/**
 * This class wraps an input stream, providing methods to read specific types.
 */
class StreamReader
{
   /**
    * Constructor.
    *
    * @param version version
    * @param stream input stream
    */
   public StreamReader(SemVer version, final InputStream stream)
   {
      m_version = version;
      m_stream = stream;

      //      m_stream = new InputStream()
      //      {
      //         @Override public int read() throws IOException
      //         {
      //            ++counter;
      //            return stream.read();
      //         }
      //
      //         private int counter = 24;
      //      };
   }

   /**
    * Read a single byte.
    *
    * @return Integer instance representing the byte read
    */
   public Integer readByte() throws IOException
   {
      return Integer.valueOf(m_stream.read());
   }

   /**
    * Read a Boolean.
    *
    * @return Boolean instance
    */
   public Boolean readBoolean() throws IOException
   {
      return Boolean.valueOf(DatatypeConverter.getBoolean(m_stream));
   }

   /**
    * Read a nested table. Instantiates the supplied reader class to
    * extract the data.
    *
    * @param reader table reader class
    * @return table rows
    */
   public List<MapRow> readTable(TableReader reader) throws IOException
   {
      reader.read();
      return reader.getRows();
   }

   /**
    * Read a nested table whose contents we don't understand.
    *
    * @param rowSize fixed row size
    * @param rowMagicNumber row magic number
    * @return table rows
    */
   public List<MapRow> readUnknownTable(int rowSize, int rowMagicNumber) throws IOException
   {
      TableReader reader = new UnknownTableReader(this, rowSize, rowMagicNumber);
      reader.read();
      return reader.getRows();
   }

   /**
    * Reads a nested table. Uses the supplied reader class instance.
    *
    * @param readerClass reader class instance
    * @return table rows
    */
   public List<MapRow> readTable(Class<? extends TableReader> readerClass) throws IOException
   {
      TableReader reader;

      try
      {
         reader = readerClass.getConstructor(StreamReader.class).newInstance(this);
      }
      catch (Exception ex)
      {
         throw new RuntimeException(ex);
      }

      return readTable(reader);
   }

   /**
    * Conditionally read a nested table based in the value of a boolean flag which precedes the table data.
    *
    * @param readerClass reader class
    * @return table rows or empty list if table not present
    */
   public List<MapRow> readTableConditional(Class<? extends TableReader> readerClass) throws IOException
   {
      List<MapRow> result;
      if (DatatypeConverter.getBoolean(m_stream))
      {
         result = readTable(readerClass);
      }
      else
      {
         result = Collections.emptyList();
      }
      return result;
   }

   /**
    * Conditionally read an unknown table.
    *
    * @param rowSize Unknown table row size
    * @param rowMagicNumber Unknown table row magic number
    * @return table rows or empty list if table not present
    */
   public List<MapRow> readUnknownTableConditional(int rowSize, int rowMagicNumber) throws IOException
   {
      List<MapRow> result;
      if (DatatypeConverter.getBoolean(m_stream))
      {
         result = readUnknownTable(rowSize, rowMagicNumber);
      }
      else
      {
         result = Collections.emptyList();
      }
      return result;
   }

   /**
    * Read an array of bytes of a specified size.
    *
    * @param size number of bytes to read
    * @return ByteArray instance
    */
   public ByteArray readBytes(int size) throws IOException
   {
      return new ByteArray(InputStreamHelper.read(m_stream, size));
   }

   /**
    * Read a UUID.
    *
    * @return UUID instance
    */
   public UUID readUUID() throws IOException
   {
      return DatatypeConverter.getUUID(m_stream);
   }

   /**
    * Read a string.
    *
    * @return String instance
    */
   public String readString() throws IOException
   {
      return DatatypeConverter.getString(m_stream);
   }

   /**
    * Read a date.
    *
    * @return Date instance.
    */
   public LocalDateTime readDate() throws IOException
   {
      return DatatypeConverter.getDate(m_stream);
   }

   /**
    * Read a time value.
    *
    * @return Date instance
    */
   public LocalTime readTime() throws IOException
   {
      return DatatypeConverter.getTime(m_stream);
   }

   /**
    * Read a duration.
    *
    * @return Duration instance
    */
   public Duration readDuration() throws IOException
   {
      return DatatypeConverter.getDuration(m_stream);
   }

   /**
    * Read a duration.
    *
    * @return Duration instance
    */
   public Duration readDurationFromLong() throws IOException
   {
      return DatatypeConverter.getDurationFromLong(m_stream);
   }

   /**
    * Read an int.
    *
    * @return int value
    */
   public int readInt() throws IOException
   {
      return DatatypeConverter.getInt(m_stream);
   }

   /**
    * Read an integer.
    *
    * @return Integer instance
    */
   public Integer readInteger() throws IOException
   {
      return DatatypeConverter.getInteger(m_stream);
   }

   /**
    * Read a double.
    *
    * @return Double instance.
    */
   public Double readDouble() throws IOException
   {
      return DatatypeConverter.getDouble(m_stream);
   }

   /**
    * Read a list of fixed size blocks as byte arrays.
    *
    * @param size fixed block size
    * @return list of blocks
    */
   public List<MapRow> readUnknownBlocks(int size) throws IOException
   {
      return new UnknownBlockReader(this, size).read();
   }

   /**
    * Read a list of fixed size blocks using an instance of the supplied reader class.
    *
    * @param readerClass reader class
    * @return list of blocks
    */
   public List<MapRow> readBlocks(Class<? extends BlockReader> readerClass) throws IOException
   {
      BlockReader reader;

      try
      {
         reader = readerClass.getConstructor(StreamReader.class).newInstance(this);
      }
      catch (Exception ex)
      {
         throw new RuntimeException(ex);
      }

      return reader.read();
   }

   /**
    * Retrieve the file version.
    *
    * @return file version
    */
   public SemVer getVersion()
   {
      return m_version;
   }

   public InputStream getStream()
   {
      return m_stream;
   }

   private final SemVer m_version;
   private final InputStream m_stream;
}
