/*
 * file:       AstaTextFileReader.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2012
 * date:       23/04/2012
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

package net.sf.mpxj.asta;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.mpxj.DayType;
import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.common.CharsetHelper;
import net.sf.mpxj.common.ReaderTokenizer;
import net.sf.mpxj.common.Tokenizer;
import net.sf.mpxj.reader.AbstractProjectStreamReader;

/**
 * This class provides a generic front end to read project data from
 * a text-based Asta PP file.
 */
public final class AstaTextFileReader extends AbstractProjectStreamReader
{
   @Override public ProjectFile read(InputStream inputStream) throws MPXJException
   {
      try
      {
         m_reader = new AstaReader();
         ProjectFile project = m_reader.getProject();
         addListenersToProject(project);

         m_tables = new HashMap<>();

         processFile(inputStream);

         processProjectProperties();
         processCalendars();
         processResources();
         processTasks();
         processPredecessors();
         processAssignments();
         // TODO: user defined field support
         project.readComplete();

         return project;
      }

      finally
      {
         m_reader = null;
      }
   }

   /**
    * Tokenizes the input file and extracts the required data.
    *
    * @param is input stream
    */
   private void processFile(InputStream is) throws MPXJException
   {
      try
      {
         InputStreamReader reader = new InputStreamReader(is, CharsetHelper.UTF8);
         Tokenizer tk = new ReaderTokenizer(reader)
         {
            @Override protected boolean startQuotedIsValid(StringBuilder buffer)
            {
               return buffer.length() == 1 && buffer.charAt(0) == '<';
            }
         };

         tk.setDelimiter(DELIMITER);
         ArrayList<String> columns = new ArrayList<>();
         String nextTokenPrefix = null;

         while (tk.getType() != Tokenizer.TT_EOF)
         {
            columns.clear();
            TableDefinition table = null;

            while (tk.nextToken() == Tokenizer.TT_WORD)
            {
               String token = tk.getToken();
               if (columns.isEmpty())
               {
                  if (token.charAt(0) == '#')
                  {
                     int index = token.lastIndexOf(':');
                     if (index != -1)
                     {
                        String headerToken;
                        if (token.endsWith("-") || token.endsWith("="))
                        {
                           headerToken = token;
                           token = null;
                        }
                        else
                        {
                           headerToken = token.substring(0, index);
                           token = token.substring(index + 1);
                        }

                        RowHeader header = new RowHeader(headerToken);
                        table = m_tableDefinitions.get(header.getType());
                        columns.add(header.getID());
                     }
                  }
                  else
                  {
                     if (token.charAt(0) == 0)
                     {
                        processFileType(token);
                     }
                  }
               }

               if (table != null && token != null)
               {
                  if (token.startsWith("<\"") && !token.endsWith("\">"))
                  {
                     nextTokenPrefix = token;
                  }
                  else
                  {
                     if (nextTokenPrefix != null)
                     {
                        token = nextTokenPrefix + DELIMITER + token;
                        nextTokenPrefix = null;
                     }

                     columns.add(token);
                  }
               }
            }

            if (table != null && columns.size() > 1)
            {
               //               System.out.println(table.getName() + " " + columns.size());
               //               ColumnDefinition[] columnDefs = table.getColumns();
               //               int unknownIndex = 1;
               //               for (int xx = 0; xx < columns.size(); xx++)
               //               {
               //                  String x = columns.get(xx);
               //                  String columnName = xx < columnDefs.length ? (columnDefs[xx] == null ? "UNKNOWN" + (unknownIndex++) : columnDefs[xx].getName()) : "?";
               //                  System.out.println(columnName + ": " + x + ", ");
               //               }
               //               System.out.println();

               TextFileRow row = new TextFileRow(table, columns, m_epochDateFormat);
               List<Row> rows = m_tables.computeIfAbsent(table.getName(), k -> new ArrayList<>());
               rows.add(row);
            }
         }
      }

      catch (Exception ex)
      {
         throw new MPXJException(MPXJException.READ_ERROR, ex);
      }
   }

   /**
    * Reads the file version and configures the expected file format.
    *
    * @param token token containing the file version
    */
   private void processFileType(String token) throws MPXJException
   {
      m_fileVersion = Integer.valueOf(token.substring(2).split(" ")[0]);
      //System.out.println(version);
      Class<? extends AbstractFileFormat> fileFormatClass = FILE_VERSION_MAP.get(m_fileVersion);
      if (fileFormatClass == null)
      {
         throw new MPXJException("Unsupported PP file format version " + m_fileVersion);
      }

      try
      {
         AbstractFileFormat format = fileFormatClass.newInstance();
         m_tableDefinitions = format.tableDefinitions();
         m_epochDateFormat = format.epochDateFormat();
      }
      catch (Exception ex)
      {
         throw new MPXJException("Failed to configure file format", ex);
      }
   }

   /**
    * Select the project properties row from the database.
    */
   private void processProjectProperties()
   {
      List<Row> rows = getTable("PROJECT_SUMMARY");
      if (!rows.isEmpty())
      {
         m_reader.processProjectProperties(m_fileVersion, rows.get(0), null, null);
      }
   }

   /**
    * Extract calendar data from the file.
    */
   private void processCalendars()
   {
      List<Row> rows = getTable("EXCEPTIONN");
      Map<Integer, DayType> exceptionMap = m_reader.createExceptionTypeMap(rows);

      rows = getTable("WORK_PATTERN");
      Map<Integer, Row> workPatternMap = m_reader.createWorkPatternMap(rows);

      rows = new ArrayList<>();// getTable("WORK_PATTERN_ASSIGNMENT"); // Need to generate an example
      Map<Integer, List<Row>> workPatternAssignmentMap = m_reader.createWorkPatternAssignmentMap(rows);

      rows = getTable("EXCEPTION_ASSIGNMENT");
      Map<Integer, List<Row>> exceptionAssignmentMap = m_reader.createExceptionAssignmentMap(rows);

      rows = getTable("TIME_ENTRY");
      Map<Integer, List<Row>> timeEntryMap = m_reader.createTimeEntryMap(rows);

      rows = getTable("CALENDAR");
      rows.sort(CALENDAR_COMPARATOR);
      for (Row row : rows)
      {
         m_reader.processCalendar(row, workPatternMap, workPatternAssignmentMap, exceptionAssignmentMap, timeEntryMap, exceptionMap);
      }
   }

   /**
    * Process resources.
    */
   private void processResources()
   {
      List<Row> permanentRows = getTable("PERMANENT_RESOURCE");
      List<Row> consumableRows = getTable("CONSUMABLE_RESOURCE");

      permanentRows.sort(PERMANENT_RESOURCE_COMPARATOR);
      consumableRows.sort(CONSUMABLE_RESOURCE_COMPARATOR);

      m_reader.processResources(permanentRows, consumableRows);
   }

   /**
    * Process tasks.
    */
   private void processTasks()
   {
      List<Row> bars = getTable("BAR");
      List<Row> expandedTasks = getTable("EXPANDED_TASK");
      List<Row> tasks = getTable("TASK");
      List<Row> milestones = getTable("MILESTONE");
      List<Row> hammocks = getTable("HAMMOCK_TASK");

      m_reader.processTasks(bars, expandedTasks, tasks, milestones, hammocks);
   }

   /**
    * Process predecessors.
    */
   private void processPredecessors()
   {
      List<Row> rows = getTable("LINK");
      List<Row> completedSections = getTable("TASK_COMPLETED_SECTION");
      rows.sort(LINK_COMPARATOR);
      m_reader.processPredecessors(rows, completedSections);
   }

   /**
    * Process resource assignments.
    */
   private void processAssignments()
   {
      List<Row> allocationRows = getTable("PERMANENT_SCHEDUL_ALLOCATION");
      List<Row> skillRows = getTable("PERM_RESOURCE_SKILL");
      List<Row> permanentAssignments = join(allocationRows, "ALLOCATIOP_OF", "PERM_RESOURCE_SKILL", skillRows, "PERM_RESOURCE_SKILLID");
      permanentAssignments.sort(ALLOCATION_COMPARATOR);
      m_reader.processAssignments(permanentAssignments);
   }

   /**
    * Very basic implementation of an inner join between two result sets.
    *
    * @param leftRows left result set
    * @param leftColumn left foreign key column
    * @param rightTable right table name
    * @param rightRows right result set
    * @param rightColumn right primary key column
    * @return joined result set
    */
   private List<Row> join(List<Row> leftRows, String leftColumn, String rightTable, List<Row> rightRows, String rightColumn)
   {
      List<Row> result = new ArrayList<>();

      RowComparator leftComparator = new RowComparator(leftColumn);
      RowComparator rightComparator = new RowComparator(rightColumn);
      leftRows.sort(leftComparator);
      rightRows.sort(rightComparator);

      ListIterator<Row> rightIterator = rightRows.listIterator();
      Row rightRow = rightIterator.hasNext() ? rightIterator.next() : null;

      for (Row leftRow : leftRows)
      {
         Integer leftValue = leftRow.getInteger(leftColumn);
         boolean match = false;

         while (rightRow != null)
         {
            Integer rightValue = rightRow.getInteger(rightColumn);
            int comparison = leftValue.compareTo(rightValue);
            if (comparison == 0)
            {
               match = true;
               break;
            }

            if (comparison < 0)
            {
               if (rightIterator.hasPrevious())
               {
                  rightRow = rightIterator.previous();
               }
               break;
            }

            rightRow = rightIterator.next();
         }

         if (match && rightRow != null)
         {
            Map<String, Object> newMap = new HashMap<>(((MapRow) leftRow).getMap());

            for (Entry<String, Object> entry : ((MapRow) rightRow).getMap().entrySet())
            {
               String key = entry.getKey();
               if (newMap.containsKey(key))
               {
                  key = rightTable + "." + key;
               }
               newMap.put(key, entry.getValue());
            }

            result.add(new MapRow(newMap));
         }
      }

      return result;
   }

   /**
    * Retrieve table data, return an empty result set if no table data is present.
    *
    * @param name table name
    * @return table data
    */
   private List<Row> getTable(String name)
   {
      return m_tables.getOrDefault(name, Collections.emptyList());
   }

   private Integer m_fileVersion;
   private AstaReader m_reader;
   private Map<String, List<Row>> m_tables;
   private Map<Integer, TableDefinition> m_tableDefinitions;
   private boolean m_epochDateFormat;

   private static final char DELIMITER = ',';

   private static final RowComparator CALENDAR_COMPARATOR = new RowComparator("CALENDARID");
   private static final RowComparator PERMANENT_RESOURCE_COMPARATOR = new RowComparator("PERMANENT_RESOURCEID");
   private static final RowComparator CONSUMABLE_RESOURCE_COMPARATOR = new RowComparator("CONSUMABLE_RESOURCEID");
   private static final RowComparator LINK_COMPARATOR = new RowComparator("LINKID");
   private static final RowComparator ALLOCATION_COMPARATOR = new RowComparator("PERMANENT_SCHEDUL_ALLOCATIONID");

   private static final Map<Integer, Class<? extends AbstractFileFormat>> FILE_VERSION_MAP = new HashMap<>();
   static
   {
      FILE_VERSION_MAP.put(Integer.valueOf(8020), FileFormat8020.class); // EasyProject 2
      FILE_VERSION_MAP.put(Integer.valueOf(9006), FileFormat9006.class); // EasyProject 3
      FILE_VERSION_MAP.put(Integer.valueOf(10008), FileFormat10008.class); // EasyProject 4
      FILE_VERSION_MAP.put(Integer.valueOf(11004), FileFormat11004.class); // EasyProject 5 and PowerProject 11
      FILE_VERSION_MAP.put(Integer.valueOf(12002), FileFormat12002.class); // PowerProject 12.0.0.2
      FILE_VERSION_MAP.put(Integer.valueOf(12005), FileFormat12005.class); // PowerProject 12
      FILE_VERSION_MAP.put(Integer.valueOf(13001), FileFormat13001.class); // PowerProject 13
      FILE_VERSION_MAP.put(Integer.valueOf(13004), FileFormat13004.class); // PowerProject 13
   }
}