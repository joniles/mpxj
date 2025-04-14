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

package org.mpxj.fasttrack;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mpxj.TimeUnit;
import org.mpxj.common.CharsetHelper;
import org.mpxj.common.DebugLogPrintWriter;
import org.mpxj.common.InputStreamHelper;

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
      try (FileInputStream is = new FileInputStream(file))
      {
         m_buffer = InputStreamHelper.read(is, length);
      }

      configureVersion();

      if (getSupported())
      {
         List<Integer> blocks = new ArrayList<>();
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
      }

      closeLogFile();
   }

   /**
    * Extract the version number and set version dependent options.
    */
   private void configureVersion()
   {
      int version = FastTrackUtility.getInt(m_buffer, 4);
      switch (version)
      {
         //         case 138:
         //         {
         //            m_supported = true;
         //            m_charset = CharsetHelper.UTF8;
         //            break;
         //         }

         case 139:
         {
            m_supported = true;
            m_charset = CharsetHelper.UTF16LE;
            m_columnMap = COLUMN_MAP1;
            break;
         }

         case 144: // 10.2
         case 145: // 11.0 / 2020?
         {
            m_supported = true;
            m_charset = CharsetHelper.UTF8;
            m_columnMap = COLUMN_MAP1;
            break;
         }

         case 146: // 12.0 / 2022?
         {
            m_supported = true;
            m_charset = CharsetHelper.UTF8;
            m_columnMap = COLUMN_MAP2;
            break;
         }

         default:
         {
            m_supported = false;
            break;
         }
      }
   }

   /**
    * Returns true if this file version is supported.
    *
    * @return true if file version is supported
    */
   public boolean getSupported()
   {
      return m_supported;
   }

   /**
    * Retrieve the charset to use when reading text from this file version.
    *
    * @return Charset instance
    */
   public Charset getCharset()
   {
      return m_charset;
   }

   /**
    * Retrieve a table of data.
    *
    * @param type table type
    * @return FastTrackTable instance
    */
   public FastTrackTable getTable(FastTrackTableType type)
   {
      return m_tables.getOrDefault(type, EMPTY_TABLE);
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
            String name = FastTrackUtility.getString(m_buffer, offset, nameLength).toUpperCase();
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
      List<Integer> blocks = new ArrayList<>();
      for (int index = startIndex; index < endIndex - 11; index++)
      {
         if (matchChildBlock(index))
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
         Class<?> klass = m_columnMap[value];
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
   private boolean matchPattern(byte[][] patterns, int bufferIndex)
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
    * Locate a child block by byte pattern and validate by
    * checking the length of the string we are expecting
    * to follow the pattern.
    *
    * @param bufferIndex start index
    * @return true if a child block starts at this point
    */
   private boolean matchChildBlock(int bufferIndex)
   {
      if (!matchPattern(CHILD_BLOCK_PATTERNS, bufferIndex))
      {
         return false;
      }
      // TODO: use pattern length
      int index = 6;

      //
      // The first step will produce false positives. To handle this, we should find
      // the name of the block next, and check to ensure that the length
      // of the name makes sense.
      //
      int nameLength = FastTrackUtility.getInt(m_buffer, bufferIndex + index);

      //      System.out.println("Name length: " + nameLength);
      //
      //      if (nameLength > 0 && nameLength < 100)
      //      {
      //         String name = new String(m_buffer, bufferIndex+index+4, nameLength, CharsetHelper.UTF16LE);
      //         System.out.println("Name: " + name);
      //      }

      return nameLength > 0 && nameLength < 100;
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
      return column instanceof DurationColumn && column.getName().contains("Duration");
   }

   /**
    * Determines if this is a work column.
    *
    * @param column column to test
    * @return true if this is a work column
    */
   private boolean isWorkColumn(FastTrackColumn column)
   {
      return column instanceof DurationColumn && column.getName().contains("Work");
   }

   /**
    * Open the log file for writing.
    */
   private void openLogFile()
   {
      m_log = DebugLogPrintWriter.getInstance();
   }

   /**
    * Close the log file.
    */
   private void closeLogFile()
   {
      if (m_log != null)
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
         m_log.println(FastTrackUtility.hexdump(m_buffer, startIndex, true, blockLength, true, 16, ""));
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
         m_log.println(FastTrackUtility.hexdump(m_buffer, startIndex, true, length, true, 16, ""));
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

   /**
    * Retrieve the current FastTrackData instance.
    *
    * @return FastTrackData instance
    */
   public static FastTrackData getInstance()
   {
      return INSTANCE.get();
   }

   /**
    * Clear the current FastTrackData instance.
    */
   public static void clearInstance()
   {
      INSTANCE.remove();
   }

   private byte[] m_buffer;
   private PrintWriter m_log;
   private final Map<FastTrackTableType, FastTrackTable> m_tables = new EnumMap<>(FastTrackTableType.class);
   private FastTrackTable m_currentTable;
   private FastTrackColumn m_currentColumn;
   private final Set<FastTrackField> m_currentFields = new HashSet<>();
   private TimeUnit m_durationTimeUnit;
   private TimeUnit m_workTimeUnit;
   private boolean m_supported;
   private Charset m_charset;
   private Class<?>[] m_columnMap;

   private static final ThreadLocal<FastTrackData> INSTANCE = ThreadLocal.withInitial(FastTrackData::new);

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
      },
      {
         0x05,
         0x00,
         0x00,
         0x00,
         0x02,
         0x00
      },
      {
         0x06,
         0x00,
         0x00,
         0x00,
         0x02,
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
      },
      {
         0x00,
         0x00,
         0x00,
         0x65,
         0x00,
         0x02,
         0x00
      }
   };

   private static final Class<?>[] COLUMN_MAP1 = new Class<?>[256];
   static
   {
      COLUMN_MAP1[0x6E] = DateColumn.class;
      COLUMN_MAP1[0x6F] = TimeColumn.class;
      COLUMN_MAP1[0x71] = DurationColumn1.class;
      COLUMN_MAP1[0x46] = PercentColumn.class;
      COLUMN_MAP1[0x6C] = ShortColumn1.class;
      COLUMN_MAP1[0x73] = ShortColumn1.class;
      COLUMN_MAP1[0x6D] = IdentifierColumn1.class;
      COLUMN_MAP1[0x70] = NumberColumn1.class;
      COLUMN_MAP1[0x5C] = CalendarColumn1.class;
      COLUMN_MAP1[0x4B] = IntegerColumn.class;
      COLUMN_MAP1[0x49] = AssignmentColumn1.class;
      COLUMN_MAP1[0x59] = EnumColumn.class;
      COLUMN_MAP1[0x53] = BooleanColumn.class;
      COLUMN_MAP1[0x5b] = DoubleColumn1.class;
      COLUMN_MAP1[0x4A] = DoubleColumn1.class;
      COLUMN_MAP1[0x54] = DoubleColumn1.class;
      COLUMN_MAP1[0x57] = RelationColumn1.class;
      COLUMN_MAP1[0x58] = RelationColumn1.class;
      COLUMN_MAP1[0x68] = StringColumn1.class;
      COLUMN_MAP1[0x69] = StringColumn1.class;
   }

   private static final Class<?>[] COLUMN_MAP2 = new Class<?>[256];
   static
   {
      COLUMN_MAP2[0x6E] = DateColumn.class;
      COLUMN_MAP2[0x6F] = TimeColumn.class;
      COLUMN_MAP2[0x71] = DurationColumn2.class;
      COLUMN_MAP2[0x46] = PercentColumn.class;
      COLUMN_MAP2[0x6C] = ShortColumn2.class;
      COLUMN_MAP2[0x73] = ShortColumn2.class;
      COLUMN_MAP2[0x6D] = IdentifierColumn2.class;
      COLUMN_MAP2[0x70] = NumberColumn2.class;
      COLUMN_MAP2[0x5C] = CalendarColumn2.class;
      COLUMN_MAP2[0x4B] = IntegerColumn.class;
      COLUMN_MAP2[0x49] = AssignmentColumn2.class;
      COLUMN_MAP2[0x59] = EnumColumn.class;
      COLUMN_MAP2[0x53] = BooleanColumn.class;
      COLUMN_MAP2[0x5b] = DoubleColumn2.class;
      COLUMN_MAP2[0x4A] = DoubleColumn2.class;
      COLUMN_MAP2[0x54] = DoubleColumn2.class;
      COLUMN_MAP2[0x57] = RelationColumn2.class;
      COLUMN_MAP2[0x58] = RelationColumn2.class;
      COLUMN_MAP2[0x68] = StringColumn2.class;
      COLUMN_MAP2[0x69] = StringColumn2.class;
   }

   private static final Map<String, FastTrackTableType> REQUIRED_TABLES = new HashMap<>();
   static
   {
      REQUIRED_TABLES.put("ACTBARS", FastTrackTableType.ACTBARS);
      REQUIRED_TABLES.put("ACTIVITIES", FastTrackTableType.ACTIVITIES);
      REQUIRED_TABLES.put("RESOURCES", FastTrackTableType.RESOURCES);
   }

   private static final FastTrackTable EMPTY_TABLE = new FastTrackTable(null, null);
}
