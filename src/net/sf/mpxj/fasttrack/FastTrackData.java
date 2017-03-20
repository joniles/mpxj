/*
 * file:       FastTrackData.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software
 * date:       04/03/2017
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

package net.sf.mpxj.fasttrack;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.common.CharsetHelper;

/**
 * Read tables of data from a FastTrack file.
 */
class FastTrackData
{
   /**
    * Read a FastTrack file.
    *
    * @param file FastTrack file
    */
   public void process(File file) throws Exception
   {
      openLogFile();

      int blockIndex = 0;
      int length = (int) file.length();
      m_buffer = new byte[length];
      FileInputStream is = new FileInputStream(file);
      try
      {
         int bytesRead = is.read(m_buffer);
         if (bytesRead != length)
         {
            throw new RuntimeException("Read count different");
         }
      }
      finally
      {
         is.close();
      }

      List<Integer> blocks = new ArrayList<Integer>();
      for (int index = 64; index < m_buffer.length - 11; index++)
      {
         if (matchPattern(PARENT_BLOCK_PATTERNS, index))
         {
            blocks.add(Integer.valueOf(index));
         }
      }

      int startIndex = 0;
      for (int endIndex : blocks)
      {
         int blockLength = endIndex - startIndex;
         readBlock(blockIndex, startIndex, blockLength);
         startIndex = endIndex;
         ++blockIndex;
      }

      int blockLength = m_buffer.length - startIndex;
      readBlock(blockIndex, startIndex, blockLength);

      closeLogFile();
   }

   /**
    * Retrieve a table of data.
    *
    * @param type table type
    * @return FastTrackTable instance
    */
   public FastTrackTable getTable(FastTrackTableType type)
   {
      FastTrackTable result = m_tables.get(type);
      if (result == null)
      {
         result = EMPTY_TABLE;
      }
      return result;
   }

   /**
    * Retrieve the time units used for durations in this FastTrack file.
    *
    * @return TimeUnit instance
    */
   TimeUnit getDurationTimeUnit()
   {
      return m_durationTimeUnit == null ? TimeUnit.DAYS : m_durationTimeUnit;
   }

   /**
    * Retrieve the time units used for work in this FastTrack file.
    *
    * @return TimeUnit instance
    */
   TimeUnit getWorkTimeUnit()
   {
      return m_workTimeUnit == null ? TimeUnit.HOURS : m_workTimeUnit;
   }

   /**
    * Read a block of data from the FastTrack file and determine if
    * it contains a table definition, or columns.
    *
    * @param blockIndex index of the current block
    * @param startIndex start index of the block in the file
    * @param blockLength block length
    */
   private void readBlock(int blockIndex, int startIndex, int blockLength) throws Exception
   {
      logBlock(blockIndex, startIndex, blockLength);

      if (blockLength < 128)
      {
         readTableBlock(startIndex, blockLength);
      }
      else
      {
         readColumnBlock(startIndex, blockLength);
      }
   }

   /**
    * Read the name of a table and prepare to populate it with column data.
    *
    * @param startIndex start of the block
    * @param blockLength length of the block
    */
   private void readTableBlock(int startIndex, int blockLength)
   {
      for (int index = startIndex; index < (startIndex + blockLength - 11); index++)
      {
         if (matchPattern(TABLE_BLOCK_PATTERNS, index))
         {
            int offset = index + 7;
            int nameLength = FastTrackUtility.getInt(m_buffer, offset);
            offset += 4;
            String name = new String(m_buffer, offset, nameLength, CharsetHelper.UTF16LE).toUpperCase();
            FastTrackTableType type = REQUIRED_TABLES.get(name);
            if (type != null)
            {
               m_currentTable = new FastTrackTable(type, this);
               m_tables.put(type, m_currentTable);
            }
            else
            {
               m_currentTable = null;
            }
            m_currentFields.clear();
            break;
         }
      }
   }

   /**
    * Read multiple columns from a block.
    *
    * @param startIndex start of the block
    * @param blockLength length of the block
    */
   private void readColumnBlock(int startIndex, int blockLength) throws Exception
   {
      int endIndex = startIndex + blockLength;
      List<Integer> blocks = new ArrayList<Integer>();
      for (int index = startIndex; index < endIndex - 11; index++)
      {
         if (matchPattern(CHILD_BLOCK_PATTERNS, index))
         {
            int childBlockStart = index - 2;
            blocks.add(Integer.valueOf(childBlockStart));
         }
      }
      blocks.add(Integer.valueOf(endIndex));

      int childBlockStart = -1;
      for (int childBlockEnd : blocks)
      {
         if (childBlockStart != -1)
         {
            int childblockLength = childBlockEnd - childBlockStart;
            try
            {
               readColumn(childBlockStart, childblockLength);
            }
            catch (UnexpectedStructureException ex)
            {
               logUnexpectedStructure();
            }
         }
         childBlockStart = childBlockEnd;
      }
   }

   /**
    * Read data for a single column.
    *
    * @param startIndex block start
    * @param length block length
    */
   private void readColumn(int startIndex, int length) throws Exception
   {
      if (m_currentTable != null)
      {
         int value = FastTrackUtility.getByte(m_buffer, startIndex);
         Class<?> klass = COLUMN_MAP[value];
         if (klass == null)
         {
            klass = UnknownColumn.class;
         }

         FastTrackColumn column = (FastTrackColumn) klass.newInstance();
         m_currentColumn = column;

         logColumnData(startIndex, length);

         column.read(m_currentTable.getType(), m_buffer, startIndex, length);
         FastTrackField type = column.getType();

         //
         // Don't try to add this data if:
         // 1. We don't know what type it is
         // 2. We have seen the type already
         //
         if (type != null && !m_currentFields.contains(type))
         {
            m_currentFields.add(type);
            m_currentTable.addColumn(column);
            updateDurationTimeUnit(column);
            updateWorkTimeUnit(column);

            logColumn(column);
         }
      }
   }

   /**
    * Locate a feature in the file by match a byte pattern.
    *
    * @param patterns patterns to match
    * @param bufferIndex start index
    * @return true if the bytes at the position match a pattern
    */
   private final boolean matchPattern(byte[][] patterns, int bufferIndex)
   {
      boolean match = false;
      for (byte[] pattern : patterns)
      {
         int index = 0;
         match = true;
         for (byte b : pattern)
         {
            if (b != m_buffer[bufferIndex + index])
            {
               match = false;
               break;
            }
            ++index;
         }
         if (match)
         {
            break;
         }
      }
      return match;
   }

   /**
    * Update the default time unit for durations based on data read from the file.
    *
    * @param column column data
    */
   private void updateDurationTimeUnit(FastTrackColumn column)
   {
      if (m_durationTimeUnit == null && isDurationColumn(column))
      {
         int value = ((DurationColumn) column).getTimeUnitValue();
         if (value != 1)
         {
            m_durationTimeUnit = FastTrackUtility.getTimeUnit(value);
         }
      }
   }

   /**
    * Update the default time unit for work based on data read from the file.
    *
    * @param column column data
    */
   private void updateWorkTimeUnit(FastTrackColumn column)
   {
      if (m_workTimeUnit == null && isWorkColumn(column))
      {
         int value = ((DurationColumn) column).getTimeUnitValue();
         if (value != 1)
         {
            m_workTimeUnit = FastTrackUtility.getTimeUnit(value);
         }
      }
   }

   /**
    * Determines if this is a duration column.
    *
    * @param column column to test
    * @return true if this is a duration column
    */
   private boolean isDurationColumn(FastTrackColumn column)
   {
      return column instanceof DurationColumn && column.getName().indexOf("Duration") != -1;
   }

   /**
    * Determines if this is a work column.
    *
    * @param column column to test
    * @return true if this is a work column
    */
   private boolean isWorkColumn(FastTrackColumn column)
   {
      return column instanceof DurationColumn && column.getName().indexOf("Work") != -1;
   }

   /**
    * Provide the file path for rudimentary logging to support development.
    *
    * @param logFile full path to log file
    */
   public void setLogFile(String logFile)
   {
      m_logFile = logFile;
   }

   /**
    * Open the log file for writing.
    */
   private void openLogFile() throws IOException
   {
      if (m_logFile != null)
      {
         m_log = new PrintWriter(new FileWriter(m_logFile));
      }
   }

   /**
    * Close the log file.
    */
   private void closeLogFile()
   {
      if (m_logFile != null)
      {
         m_log.flush();
         m_log.close();
      }
   }

   /**
    * Log block data.
    *
    * @param blockIndex current block index
    * @param startIndex start index
    * @param blockLength length
    */
   private void logBlock(int blockIndex, int startIndex, int blockLength)
   {
      if (m_log != null)
      {
         m_log.println("Block Index: " + blockIndex);
         m_log.println("Length: " + blockLength + " (" + Integer.toHexString(blockLength) + ")");
         m_log.println();
         m_log.println(FastTrackUtility.hexdump(m_buffer, startIndex, blockLength, true, 16, ""));
         m_log.flush();
      }
   }

   /**
    * Log the data for a single column.
    *
    * @param startIndex offset into buffer
    * @param length length
    */
   private void logColumnData(int startIndex, int length)
   {
      if (m_log != null)
      {
         m_log.println();
         m_log.println(FastTrackUtility.hexdump(m_buffer, startIndex, length, true, 16, ""));
         m_log.println();
         m_log.flush();
      }
   }

   /**
    * Log unexpected column structure.
    */
   private void logUnexpectedStructure()
   {
      if (m_log != null)
      {
         m_log.println("ABORTED COLUMN - unexpected structure: " + m_currentColumn.getClass().getSimpleName() + " " + m_currentColumn.getName());
      }
   }

   /**
    * Log column data.
    *
    * @param column column data
    */
   private void logColumn(FastTrackColumn column)
   {
      if (m_log != null)
      {
         m_log.println("TABLE: " + m_currentTable.getType());
         m_log.println(column.toString());
         m_log.flush();
      }
   }

   private byte[] m_buffer;
   private String m_logFile;
   private PrintWriter m_log;
   private final Map<FastTrackTableType, FastTrackTable> m_tables = new EnumMap<FastTrackTableType, FastTrackTable>(FastTrackTableType.class);
   private FastTrackTable m_currentTable;
   private FastTrackColumn m_currentColumn;
   private final Set<FastTrackField> m_currentFields = new TreeSet<FastTrackField>();
   private TimeUnit m_durationTimeUnit;
   private TimeUnit m_workTimeUnit;

   private static final byte[][] PARENT_BLOCK_PATTERNS =
   {
      {
         (byte) 0xFB,
         0x01,
         0x02,
         0x00,
         0x02,
         0x00,
         (byte) 0xFF,
         (byte) 0xFF,
         0x00,
         0x00,
         0x00
      },
      {
         (byte) 0xFC,
         0x01,
         0x02,
         0x00,
         0x02,
         0x00,
         (byte) 0xFF,
         (byte) 0xFF,
         0x00,
         0x00,
         0x00
      },
      {
         (byte) 0xFD,
         0x01,
         0x02,
         0x00,
         0x02,
         0x00,
         (byte) 0xFF,
         (byte) 0xFF,
         0x00,
         0x00,
         0x00
      },
      {
         0x00,
         0x00,
         0x02,
         0x00,
         0x02,
         0x00,
         (byte) 0xFF,
         (byte) 0xFF,
         0x00,
         0x00,
         0x00
      }
   };

   private static final byte[][] CHILD_BLOCK_PATTERNS =
   {
      {
         0x05,
         0x00,
         0x00,
         0x00,
         0x01,
         0x00
      }
   };

   private static final byte[][] TABLE_BLOCK_PATTERNS =
   {
      {
         0x00,
         0x00,
         0x00,
         0x65,
         0x00,
         0x01,
         0x00
      }
   };

   private static final Class<?>[] COLUMN_MAP = new Class<?>[256];
   static
   {
      COLUMN_MAP[0x6E] = DateColumn.class;
      COLUMN_MAP[0x6F] = TimeColumn.class;
      COLUMN_MAP[0x71] = DurationColumn.class;
      COLUMN_MAP[0x46] = PercentColumn.class;
      COLUMN_MAP[0x6C] = ShortColumn.class;
      COLUMN_MAP[0x73] = ShortColumn.class;
      COLUMN_MAP[0x6D] = IdentifierColumn.class;
      COLUMN_MAP[0x70] = NumberColumn.class;
      COLUMN_MAP[0x5C] = CalendarColumn.class;
      COLUMN_MAP[0x4B] = IntegerColumn.class;
      COLUMN_MAP[0x49] = AssignmentColumn.class;
      COLUMN_MAP[0x59] = EnumColumn.class;
      COLUMN_MAP[0x53] = BooleanColumn.class;
      COLUMN_MAP[0x5b] = DoubleColumn.class;
      COLUMN_MAP[0x4A] = DoubleColumn.class;
      COLUMN_MAP[0x54] = DoubleColumn.class;
      COLUMN_MAP[0x57] = RelationColumn.class;
      COLUMN_MAP[0x58] = RelationColumn.class;
      COLUMN_MAP[0x68] = StringColumn.class;
      COLUMN_MAP[0x69] = StringColumn.class;
   }

   private static final Map<String, FastTrackTableType> REQUIRED_TABLES = new HashMap<String, FastTrackTableType>();
   static
   {
      REQUIRED_TABLES.put("ACTBARS", FastTrackTableType.ACTBARS);
      REQUIRED_TABLES.put("ACTIVITIES", FastTrackTableType.ACTIVITIES);
      REQUIRED_TABLES.put("RESOURCES", FastTrackTableType.RESOURCES);
   }

   private static final FastTrackTable EMPTY_TABLE = new FastTrackTable(null, null);
}
